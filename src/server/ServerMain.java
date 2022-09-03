package server;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
	
	public static final String pathConfigurationFile = "src/files/server_configuration.txt";
	
	public static void main(String[] args) throws AlreadyBoundException, IOException, InterruptedException, InvalidConfigurationException {
		File configurationFile = new File(pathConfigurationFile);
		
		ServerConfiguration serverConf = new ServerConfiguration(configurationFile);
		
		ServerSocket serverSocketTCP = new ServerSocket(serverConf.TCPPORT);
		
		ExecutorService threadPool = Executors.newCachedThreadPool();
		
		Database db = new Database();
		
		//list used to save client socket that connects to server. 
		//When the server run the shutdownHook, it closes all the client socket.
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
				
			}
		});
		
		while(threadPool.isShutdown() == false) {
			Socket socket = serverSocketTCP.accept();
			userLogged.add(socket);
			threadPool.execute(new TaskHandler(socket, db, serverConf, stubCallbackRegistration));
		}
	}

}
