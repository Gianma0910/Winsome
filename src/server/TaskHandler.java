package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import RMI.RMICallback;
import configuration.ServerConfiguration;
import server.database.Database;
import server.follow_unfollow_services.FollowingServiceImpl;
import server.follow_unfollow_services.UnfollowingServiceImpl;
import server.load_action_services.LoadFollowersServiceImpl;
import server.load_action_services.LoadFollowingServiceImpl;
import server.login_logout_services.LoginServiceImpl;
import server.login_logout_services.LogoutServiceImpl;
import server.post_action_services.PostServicesImpl;
import server.view_list_users_services.ViewListUsersServiceImpl;
import server.wallet_services.GetWalletServicesImpl;
import utility.TypeError;

/**
 * Server thread that connect to client and receive the client request.
 * @author Gianmarco Petrocchi.
 *
 */
public class TaskHandler implements Runnable {

	/** Socket TCP of server.*/
	private Socket socket;
	/** Database.*/
	private Database db;
	/** BufferedReader used to write/send response to client.*/
	private BufferedWriter writerOutput;
	/** BufferedReader used to read/receive request by clien.*/
	private BufferedReader readerInput;
	/** Server configuration.*/
	private ServerConfiguration serverConf;
	/** Stub of callback service.*/
	private RMICallback stubCallbackRegistration;
	
	/**
	 * Basic constructor of TaskHandler class
	 * @param socket Socket created by ServerSocket.accept(), it communicate with the client. Cannot be null.
	 * @param serverConf Server configuration used to set some constructor's variables. Cannot be null.
	 * @param db Database. Cannot be null.
	 * @param stubCallbackRegistration Stub callback service. Cannot be null.
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
				
				//split the request client using the caharacter ":"
				String [] requestSplitted = requestClient.split(":");
				//take the first string parsed
				String command = requestSplitted[0];

				//check the string command and then execute the right method
				switch(command) {
				case "login" :{
					if(requestSplitted.length != 3) {
						sendError(TypeError.INVALIDREQUESTERROR, writerOutput);
					}else {
						String username = requestSplitted[1];
	
						String password = requestSplitted[2];

						LoginServiceImpl loginService = new LoginServiceImpl(db, writerOutput);
						
						loginService.login(username, password, socket, serverConf);
					}
					
					break;
				}
				case "logout":{	
					if(requestSplitted.length != 1) {
						sendError(TypeError.INVALIDREQUESTERROR, writerOutput);
					}else {
						LogoutServiceImpl logoutService = new LogoutServiceImpl(db, writerOutput);
						
						logoutService.logout(socket);
					}
					
					break;
				}
				case "follow":{
					if(requestSplitted.length != 2) {
						sendError(TypeError.INVALIDREQUESTERROR, writerOutput);
					}else {
						String usernameToFollow = requestSplitted[1];

						FollowingServiceImpl followingService = new FollowingServiceImpl(db, writerOutput);

						followingService.addFollower(usernameToFollow, stubCallbackRegistration, socket);
					}
					
					break;
				}
				case "unfollow": { 
					if(requestSplitted.length != 2) {
						sendError(TypeError.INVALIDREQUESTERROR, writerOutput);
					}else {
						String usernameToUnfollow = requestSplitted[1];
						
						UnfollowingServiceImpl unfollowingService = new UnfollowingServiceImpl(db, writerOutput);
						
						unfollowingService.removeFollowing(usernameToUnfollow, stubCallbackRegistration, socket);
					}
					
					break;
				}
				case "list" : {
					if(requestSplitted.length != 2) {
						sendError(TypeError.INVALIDREQUESTERROR, writerOutput);
					}else {
						ViewListUsersServiceImpl listUsersService = new ViewListUsersServiceImpl(db, writerOutput);
						
						listUsersService.viewListUsers(socket);
					}
					
					break;
				}
				case "post": {
					if(requestSplitted.length != 1) {
						sendError(TypeError.INVALIDREQUESTERROR, writerOutput);
					}else{
						sendError(TypeError.SUCCESS, writerOutput);
						
						String createPostRequest = readerInput.readLine();
						String [] createPostRequestSplitted = createPostRequest.split(" ");
						
						byte[] titleBytes = Base64.getDecoder().decode(createPostRequestSplitted[0]);
						byte[] contentBytes = Base64.getDecoder().decode(createPostRequestSplitted[1]);
						
						String title = new String(titleBytes, StandardCharsets.UTF_8);
						String content = new String(contentBytes, StandardCharsets.UTF_8);
						
						PostServicesImpl postServices = new PostServicesImpl(db, writerOutput);

						postServices.createPost(title, content, socket);
					}

					break;
				}
				case "blog": {
					if(requestSplitted.length != 1) {
						sendError(TypeError.INVALIDREQUESTERROR, writerOutput);
					}else {
						PostServicesImpl postServices = new PostServicesImpl(db, writerOutput);
						
						postServices.viewUserPost(socket);
					}
					
					break;
				}
				case "show": {
					if(requestSplitted[1].equals("feed")) {
						if(requestSplitted.length != 2) {
							sendError(TypeError.INVALIDREQUESTERROR, writerOutput);
						}else {
							PostServicesImpl postServices = new PostServicesImpl(db, writerOutput);
							
							postServices.viewUserFeed(socket);
						}
					}else if(requestSplitted[1].equals("post")) {
						if(requestSplitted.length != 3) {
							sendError(TypeError.INVALIDREQUESTERROR, writerOutput);
						}else {
							PostServicesImpl postServices = new PostServicesImpl(db, writerOutput);
							
							postServices.viewPost(requestSplitted[2], socket);
						}
					}
						
					break;
				}
				case "delete": {
					if(requestSplitted.length != 2) {
						sendError(TypeError.INVALIDREQUESTERROR, writerOutput);
					}else {
						PostServicesImpl postServices = new PostServicesImpl(db, writerOutput);
						
						postServices.deletePost(requestSplitted[1], socket);
					}
					
					break;
				}
				case "rate": {
					if(requestSplitted.length != 3) {
						sendError(TypeError.INVALIDREQUESTERROR, writerOutput);
					}else {
						PostServicesImpl postServices = new PostServicesImpl(db, writerOutput);
						
						postServices.ratePost(requestSplitted[1], requestSplitted[2], socket);
					}
					
					break;
				}
				case "comment": {
					if(requestSplitted.length != 2) {
						sendError(TypeError.INVALIDREQUESTERROR, writerOutput);
					}else {
						sendError(TypeError.SUCCESS, writerOutput);
					
						String idPostToParse = requestSplitted[1];
						
						String requestAddComment = readerInput.readLine();
						
						byte[] requestBytes = Base64.getDecoder().decode(requestAddComment.getBytes());
						
						String contentComment = new String(requestBytes, StandardCharsets.UTF_8);
						
						PostServicesImpl postServices = new PostServicesImpl(db, writerOutput);
						
						postServices.commentPost(idPostToParse, contentComment, socket);
					}
				
					break;
				}
				case "rewin": {
					if(requestSplitted.length != 2) {
						sendError(TypeError.INVALIDREQUESTERROR, writerOutput);
					}else {
						PostServicesImpl postServicesImpl = new PostServicesImpl(db, writerOutput);
						
						postServicesImpl.rewinPost(requestSplitted[1], socket);
					}
					
					break;
				}
				case "wallet": {
					GetWalletServicesImpl walletServicesImpl = new GetWalletServicesImpl(db, writerOutput);
					
					if(requestSplitted.length == 1)
						walletServicesImpl.getWallet(socket);
					else if(requestSplitted.length == 2)
						walletServicesImpl.getWalletInBitcoin(socket);
					else 
						sendError(TypeError.INVALIDREQUESTERROR, writerOutput);
					
					break;
				}
				case "load": {
					if(requestSplitted[1].equals("followers")) {
						LoadFollowersServiceImpl loadServicesImpl = new LoadFollowersServiceImpl(db, writerOutput);
						
						loadServicesImpl.loadFollowers(stubCallbackRegistration, requestSplitted[2]);
						
					}else if(requestSplitted[1].equals("following")) {
						LoadFollowingServiceImpl loadServicesImpl = new LoadFollowingServiceImpl(db, writerOutput);
						
						loadServicesImpl.loadFollowing(stubCallbackRegistration, requestSplitted[2]);
						
					}
					
					break;
				}
				}
			}
		}catch(SocketTimeoutException ex) {
			System.err.println(ex.getMessage());
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}
	}

	private void sendError(String error, BufferedWriter writerOutput) throws IOException {
		writerOutput.write(error);
		writerOutput.newLine();
		writerOutput.flush();
	}
	
}
