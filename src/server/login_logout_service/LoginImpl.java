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
		//check if the parameter password is equals to user's password identified by the parameter username
		if(!password.equals(db.getUserByUsername(username).getPassword()))
			return TypeError.PWDWRONG;
			
		ConcurrentHashMap<Socket, String> usrLogged = new ConcurrentHashMap<Socket, String>();
		usrLogged = db.getUserLoggedIn();
		
		//check if the client identified by socketClient is trying to login with a username that is already logged in
		for(Socket s : usrLogged.keySet()) {
			String usr1 = usrLogged.get(s);
			if(usr1 == username && socketClient.equals(s) == false)
				return TypeError.USRALREADYLOGGED;
			else continue;
		}
		
		//add the socketClient that identified the client into the Database.
		//That operation indicates that the client is logged with a certain username
		db.addUserLoggedIn(socketClient, username);
		
		return TypeError.SUCCESS;
	}
	
}
