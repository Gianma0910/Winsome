package server;
import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import RMI.RMICallback;
import RMI.RMICallbackImpl;
import RMI.RMIRegistration;
import RMI.RMIRegistrationImpl;
import configuration.ServerConfiguration;
import exceptions.InvalidConfigurationException;
import server.database.Database;

public class ServerMain {

	public static void main(String[] args) throws InvalidConfigurationException, AlreadyBoundException, IOException {
		if(args.length != 1) {
			System.err.println("Usage: java ServerMain <path configuration file>");
			System.err.println("Check the documentation\n");
			System.exit(0);
		}
		
		String pathConfigurationFile = args[0];
		File configurationFile = new File(pathConfigurationFile);
		
		ServerConfiguration serverConf = new ServerConfiguration(configurationFile);
		
		ServerSocket serverSocketTCP = new ServerSocket(serverConf.TCPPORT);
		DatagramSocket serverSocketUDP = new DatagramSocket();
		
		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(serverConf.THREADBLOCKINGQUEUE);
		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(serverConf.COREPOOLSIZE, serverConf.MAXIMUMCOREPOOLSIZE, serverConf.KEEPALIVETIME, TimeUnit.MILLISECONDS, queue);
	
		threadPool.allowCoreThreadTimeOut(false);
		
		Database db = new Database();
		
		RMIRegistrationImpl registrationImpl = new RMIRegistrationImpl(db);
		RMIRegistration stubRegistration = (RMIRegistration) UnicastRemoteObject.exportObject(registrationImpl, 0);
		LocateRegistry.createRegistry(serverConf.RMIREGISTRYPORT);
		Registry registry = LocateRegistry.getRegistry(serverConf.RMIREGISTRYPORT);
		registry.bind(serverConf.REGISTRATIONSERVICENAME, stubRegistration);
		
		RMICallbackImpl callbackImpl = new RMICallbackImpl();
		RMICallback stubCallbackRegistration = (RMICallback) UnicastRemoteObject.exportObject(callbackImpl, 0);
		registry = LocateRegistry.getRegistry(serverConf.RMIREGISTRYPORT);
		registry.bind(serverConf.CALLBACKSERVICENAME, stubCallbackRegistration);
		
		System.out.println("File properties read successfully, server is running...\n");
		
		while(true) {
			Socket socket = serverSocketTCP.accept();
			threadPool.execute(new TaskHandler(socket, db, serverConf, stubCallbackRegistration));
		}
	}

}
