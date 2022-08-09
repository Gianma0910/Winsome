package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Objects;

import RMI.RMICallback;
import configuration.ServerConfiguration;
import server.database.Database;
import server.follow_unfollow_service.FollowingImpl;
import server.follow_unfollow_service.UnfollowingImpl;
import server.login_logout_service.LoginImpl;
import server.login_logout_service.LogoutImpl;
import server.post_action_services.PostServicesImpl;
import server.view_list_users_service.ViewListUsersImpl;

/**
 * Thread that connect to client and receive the client request
 * @author Gianmarco Petrocchi
 *
 */
public class TaskHandler implements Runnable {

	private Socket socket;
	private Database db;
	private BufferedWriter writerOutput;
	private BufferedReader readerInput;
	private ServerConfiguration serverConf;
	private RMICallback stubCallbackRegistration;
	
	/**
	 * Basic constructor of TaskHandler class
	 * @param socket Socket created by ServerSocket.accept(), it communicate with the client. Cannot be null
	 * @param db Database. Cannot be null
	 */
	public TaskHandler(Socket socket, Database db, ServerConfiguration serverConf, RMICallback stubCallbackRegistration) {
		Objects.requireNonNull(socket, "Socket is null");
		Objects.requireNonNull(db, "Database is null");
		
		this.socket = socket;
		this.db = db;
		this.serverConf = serverConf;
		this.stubCallbackRegistration = stubCallbackRegistration;
		
		try {
			this.writerOutput = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.readerInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			System.err.println("Impossible to create input and output stream by client socket");
		}	
	}
	
	@Override
	public void run() {
		System.out.println("Connected " + socket);
		
		try {
			
			while(socket.isConnected()) {
			
				String requestClient;

				//read the request client (receive)
				requestClient = readerInput.readLine();

				//socket.setSoTimeout(10000);

				System.out.println(requestClient);
				
				//split the request client using the caharacter ":"
				String [] requestSplitted = requestClient.split(":");
				//take the first string parsed
				String command = requestSplitted[0];

				//check the string command and then execute the right method
				switch(command) {
				case "login" :{
					
					//take the username written in the request client
					String username = requestSplitted[1];
					//take the password written in the request client
					String password = requestSplitted[2];

					LoginImpl loginService = new LoginImpl(db, writerOutput);
					
					loginService.login(username, password, socket, serverConf);
					
					break;
				}
				case "logout":{					
					LogoutImpl logoutService = new LogoutImpl(db, writerOutput);
					
					logoutService.logout(socket);
					
					break;
				}
				case "follow":{
					
					String usernameToFollow = requestSplitted[1];
					
					FollowingImpl followingService = new FollowingImpl(db, writerOutput);
					
					followingService.addFollower(usernameToFollow, stubCallbackRegistration, socket);
					
					break;
				}
				case "unfollow": { 
					
					String usernameToUnfollow = requestSplitted[1];
					
					UnfollowingImpl unfollowingService = new UnfollowingImpl(db, writerOutput);
					
					unfollowingService.removeFollowing(usernameToUnfollow, stubCallbackRegistration, socket);
					
					break;
				}
				case "list" : {
					ViewListUsersImpl listUsersService = new ViewListUsersImpl(db, writerOutput);
					
					listUsersService.viewListUsers(socket);
					
					break;
				}
				case "post": {
					PostServicesImpl postServices = new PostServicesImpl(db, writerOutput);
					
					postServices.createPost(requestSplitted, socket);
					
					break;
				}
				case "blog": {
					PostServicesImpl postServices = new PostServicesImpl(db, writerOutput);
					
					postServices.viewUserPost(socket);
				}
				case "show": {
					if(requestSplitted[1].equals("feed")) {
						PostServicesImpl postServices = new PostServicesImpl(db, writerOutput);
						
						postServices.viewUserFeed(socket);
					}
						
				}
				}
			}
		}catch(SocketTimeoutException ex) {
			System.err.println(ex.getMessage());
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}

		
		
	}

}
