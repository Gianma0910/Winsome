package server.login_logout_service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import server.database.Database;
import utility.TypeError;

public class LogoutImpl implements Logout{
	
	private Database db;
	private BufferedWriter writerOutput;
	
	public LogoutImpl(Database db, BufferedWriter writerOutput) {
		this.db = db;
		this.writerOutput = writerOutput;
	}
	
	public void logout(Socket socketClient) throws IOException {
		String error;
		
		if(db.removeUserLoggedIn(socketClient) == true)
			error = TypeError.SUCCESS;
		else 
			error = TypeError.LOGOUTERROR;
		
		writerOutput.write(error);
		writerOutput.newLine();
		writerOutput.flush();
		
		return;
	}
}
