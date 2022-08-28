package server.login_logout_services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import configuration.ServerConfiguration;
import server.database.Database;
import utility.TypeError;

/**
 * Class that implements LoginService. This class is used to login a user in Winsome.
 * @author Gianmarco Petrocchi.
 *
 */
public class LoginServiceImpl implements LoginService{

	private Database db;
	private BufferedWriter writerOutput;
	
	public LoginServiceImpl(Database db, BufferedWriter writerOutput) {
		this.db = db;
		this.writerOutput = writerOutput;
	}
	
	public void login(String username, String password, Socket socketClient, ServerConfiguration serverConf) throws IOException {
		String error;
		
		ConcurrentHashMap<Socket, String> userLogged = db.getUserLoggedIn();
		
		//check if the username is registered in Winsome
		if(!db.isUserRegistered(username)) {
			error = TypeError.USERNAMEWRONG;
			sendError(error, writerOutput);
			return;
		}
		
		//check if the parameter password is equals to user's password identified by the parameter username
		if(!password.equals(db.getUserByUsername(username).getPassword())) {
			error = TypeError.PWDWRONG;
			sendError(error, writerOutput);
			return;
		}
		
		//check if the username is already logged
		if(userLogged.containsValue(username)) {
			error = TypeError.USRALREADYLOGGED;
			sendError(error, writerOutput);
			return;
		}
		
		if(userLogged.containsKey(socketClient)) {
			error = TypeError.CLIENTALREADYLOGGED;
			sendError(error, writerOutput);
			return;
		}
			
		error = TypeError.SUCCESS;
		sendError(error, writerOutput);
		
		writerOutput.write(serverConf.getMulticastInfo());
		writerOutput.newLine();
		writerOutput.flush();
			
		db.setFollowingListForUser(username);
		db.setFollowerListUser(username);
		db.setPostListForUser(username);
		
		//add the socketClient that identified the client into the Database.
		//That operation indicates that the client is logged with a certain username
		db.addUserLoggedIn(socketClient, username);
	
		return;
	}
	
	private void sendError(String error, BufferedWriter writerOutput) throws IOException {
		writerOutput.write(error);
		writerOutput.newLine();
		writerOutput.flush();
	}
	
}
