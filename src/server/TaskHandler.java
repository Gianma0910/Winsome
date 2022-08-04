package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Objects;

import RMI.FollowerDatabase;
import RMI.RMICallback;
import configuration.ServerConfiguration;
import server.database.Database;
import server.login_logout_service.LoginImpl;
import server.login_logout_service.LogoutImpl;
import utility.TypeError;

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

					LoginImpl loginService = new LoginImpl(db);
					
					String error = loginService.login(username, password, socket);
					
					//send the result of login 
					writerOutput.write(error);
					writerOutput.newLine();
					writerOutput.flush();
					
					if(error.equals(TypeError.SUCCESS)) {
						writerOutput.write(serverConf.getMulticastInfo());
						writerOutput.newLine();
						writerOutput.flush();
						
						db.setFollowingListForUser(username);
						db.setFollowerListUser(username);
					}
					
					break;
				}
				case "logout":{					
					LogoutImpl logoutService = new LogoutImpl(db);
					
					String error = logoutService.logout(socket);
					
					writerOutput.write(error);
					writerOutput.newLine();
					writerOutput.flush();
					
					socket.close();
					
					break;
				}
				case "follow":{
					
					String usernameToFollow = requestSplitted[1];
					
					FollowerDatabase stubUsernameAssociated = stubCallbackRegistration.getCallback(usernameToFollow);
					
					String usernameNewFollow = db.getUsernameBySocket(socket);
					String error = stubUsernameAssociated.addFollower(usernameNewFollow);
					
					if(error.equals(TypeError.FOLLOWERERROR)) {
						writerOutput.write(error);
						writerOutput.newLine();
						writerOutput.flush();
					}else if(error.equals(TypeError.SUCCESS)){
						db.addFollowing(usernameNewFollow, usernameToFollow);
						db.addFollower(usernameToFollow, usernameNewFollow);
						
						writerOutput.write(error);
						writerOutput.newLine();
						writerOutput.flush();
					}
					
					break;
				}
				case "unfollow": { 
					
					String usernameToUnfollow = requestSplitted[1];
					
					FollowerDatabase stubUsernameAssociated = stubCallbackRegistration.getCallback(usernameToUnfollow);
					
					String usernameRemoveFollow = db.getUsernameBySocket(socket);
					String error = stubUsernameAssociated.removeFollower(usernameRemoveFollow);
					
					if(error.equals(TypeError.FOLLOWERERROR)) {
						writerOutput.write(error);
						writerOutput.newLine();
						writerOutput.flush();
					}else if(error.equals(TypeError.SUCCESS)) {
						db.removeFollowing(usernameRemoveFollow, usernameToUnfollow);
						db.removeFollower(usernameToUnfollow, usernameRemoveFollow);
						
						writerOutput.write(error);
						writerOutput.newLine();
						writerOutput.flush();
					}
					
					break;
				}
				case "list" : {
					if(requestSplitted[1].equals("users")) {
						String usersRegisteredJson = db.getRegisteredUsersJson(db.getUsernameBySocket(socket));
						
						writerOutput.write(usersRegisteredJson);
						writerOutput.newLine();
						writerOutput.flush();
						
						break;
					}else if(requestSplitted[1].equals("following")) {
						String usersFollowing = db.toStringFollowingListByUsername(db.getUsernameBySocket(socket));
						
						writerOutput.write(usersFollowing);
						writerOutput.newLine();
						writerOutput.flush();
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
