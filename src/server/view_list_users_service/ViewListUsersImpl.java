package server.view_list_users_service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import server.database.Database;

public class ViewListUsersImpl implements ViewListUsers {
	private Database db;
	private BufferedWriter writerOutput;
	
	public ViewListUsersImpl(Database db, BufferedWriter writerOutput) {
		this.db = db;
		this.writerOutput = writerOutput; 
	}
	
	@Override
	public void viewListUsers(Socket socket) throws IOException {
		String usersRegisteredJson = db.getRegisteredUsersJson(db.getUsernameBySocket(socket));
		
		writerOutput.write(usersRegisteredJson);
		writerOutput.newLine();
		writerOutput.flush();
	}

}
