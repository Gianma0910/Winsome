package server.login_logout_service;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import server.database.Database;
import utility.TypeError;

public class LoginImpl implements Login{

	private Database db;
	
	public LoginImpl(Database db) {
		this.db = db;
	}
	
	public String login(String username, String password, Socket socketClient) {
		//check if the username is registered in Winsome
		if(!db.isUserRegistered(username)) 
			return TypeError.USERNAMEWRONG;
		
		//check if the parameter password is equals to user's password identified by the parameter username
		if(!password.equals(db.getUserByUsername(username).getPassword())) 
			return TypeError.PWDWRONG;
		
		ConcurrentHashMap<Socket, String> usrLogged = db.getUserLoggedIn();
			
		//check if the username is already logged
		if(usrLogged.containsValue(username)) 
			return TypeError.USRALREADYLOGGED;
		
		//add the socketClient that identified the client into the Database.
		//That operation indicates that the client is logged with a certain username
		db.addUserLoggedIn(socketClient, username);
		
		return TypeError.SUCCESS;
	}
	
}
