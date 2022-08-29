package server;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import RMI.RMICallback;
import RMI.RMICallbackImpl;
import RMI.RMIRegistration;
import RMI.RMIRegistrationImpl;
import configuration.ServerConfiguration;
import exceptions.IllegalFileException;
import exceptions.InvalidConfigurationException;
import server.database.Database;

/**
 * Class that represents the Winsome server. It will receive all the client request and execute them.
 * @author Gianmarco Petrocchi.
 *
 */
public class ServerMain {
	
	public static void main(String[] args) throws InvalidConfigurationException, AlreadyBoundException, IOException, InterruptedException {
		if(args.length != 1) {
			System.err.println("Usage: java -cp \".:./bin/:./libs/gson-2.8.9.jar\" ServerMain <path file configuration>\n");
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
		
		ArrayList<Socket> userLogged = new ArrayList<>();
		
		RMIRegistrationImpl registrationImpl = new RMIRegistrationImpl(db);
		RMIRegistration stubRegistration = (RMIRegistration) UnicastRemoteObject.exportObject(registrationImpl, 0);
		LocateRegistry.createRegistry(serverConf.RMIREGISTRYPORT);
		Registry registry = LocateRegistry.getRegistry(serverConf.RMIREGISTRYPORT);
		registry.bind(serverConf.REGISTRATIONSERVICENAME, stubRegistration);
		
		RMICallbackImpl callbackImpl = new RMICallbackImpl();
		RMICallback stubCallbackRegistration = (RMICallback) UnicastRemoteObject.exportObject(callbackImpl, 0);
		registry = LocateRegistry.getRegistry(serverConf.RMIREGISTRYPORT);
		registry.bind(serverConf.CALLBACKSERVICENAME, stubCallbackRegistration);
		
		System.out.println("File properties read successfully\n");
		
		try {
			db.loadUsersFromJsonFile(new File(serverConf.USERSFILENAMEPATH), new File(serverConf.FOLLOWINGFILENAMEPATH), new File(serverConf.TRANSACTIONSFILENAMEPATH));
		} catch (IllegalFileException | FileNotFoundException e) {
			System.err.println("Warning: users could not be recovered from backup.");
			e.printStackTrace();
		}

		System.out.println("Upload of users to database completed succesfully");
		
		try {
			db.loadPostsFromJsonFile(new File(serverConf.POSTSFILENAMEPATH), new File(serverConf.VOTESFILENAMEPATH), new File(serverConf.COMMENTSFILENAMEPATH), new File(serverConf.MUTABLEDATAPOSTSFILENAMEPATH));
		} catch (IllegalFileException | FileNotFoundException e) {
			System.err.println("Warning: posts could not be recovered from backup.");
			e.printStackTrace();
		}

		System.out.println("Upload of posts to database completed successfully");
		System.out.println("Server is now running...\n");
		
		Thread rewards = new Thread(new TaskReward(db, serverConf));
		rewards.start();
		
		Thread backup = new Thread(new TaskBackup(db, serverConf));
		backup.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			public void run() {
				System.out.println("\nServer has now entered in shutdown mode\n");
				
				rewards.interrupt();
				
				for(Socket s : userLogged) {
					try {
						s.close();
					} catch (IOException e) {
						System.err.println("I/O error occured during shutdown");
						e.printStackTrace();
					}
				}
				
				threadPool.shutdown();
				try {
					if(!threadPool.awaitTermination(serverConf.DELAYSHUTDOWNTHREADPOOL, TimeUnit.MILLISECONDS)) {
						threadPool.shutdown();
					}
				} catch (InterruptedException e) {
					threadPool.shutdownNow();
				}
				
				try {backup.join(500);}
				catch(InterruptedException e) { }
				
				try {rewards.join(500);}
				catch(InterruptedException e) { }
				
				try {
					serverSocketTCP.close();
				} catch (IOException e) {
					System.err.println("I/O error occured during shutdown");
					e.printStackTrace();
				}
				serverSocketUDP.close();
			}
		});
		
		while(threadPool.isShutdown() == false) {
			Socket socket = serverSocketTCP.accept();
			userLogged.add(socket);
			threadPool.execute(new TaskHandler(socket, db, serverConf, stubCallbackRegistration));
		}
	}

}
