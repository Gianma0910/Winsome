package server.login_logout_services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import server.database.Database;
import utility.TypeError;

public class LogoutServiceImpl implements LogoutService{
	
	private Database db;
	private BufferedWriter writerOutput;
	
	public LogoutServiceImpl(Database db, BufferedWriter writerOutput) {
		this.db = db;
		this.writerOutput = writerOutput;
	}
	
	public void logout(Socket socketClient) throws IOException {
		String error;
		
		ConcurrentHashMap<Socket, String> userLogged = db.getUserLoggedIn();
		
		if(!userLogged.containsKey(socketClient))
			error = TypeError.CLIENTNOTLOGGED;
		else if(db.removeUserLoggedIn(socketClient) == true)
			error = TypeError.SUCCESS;
		else 
			error = TypeError.LOGOUTERROR;
		
		sendError(error, writerOutput);
		
		return;
	}
	
	private void sendError(String error, BufferedWriter writerOutput) throws IOException {
		writerOutput.write(error);
		writerOutput.newLine();
		writerOutput.flush();
	}
}
