package server.login_logout_service;

import java.net.Socket;

import server.database.Database;
import utility.TypeError;

public class LogoutImpl {
	
	private Database db;
	
	public LogoutImpl(Database db) {
		this.db = db;
	}
	
	public String logout(Socket socketClient) {
		if(db.removeUserLoggedIn(socketClient) == true)
			return TypeError.SUCCESS;
		else 
			return TypeError.LOGOUTERROR;
	}
	
	
}
