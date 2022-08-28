package server.view_list_users_services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import server.database.Database;
import utility.TypeError;

/**
 * Class that implements ViewListUsersService. This class is used to send the list of users that
 * has at least one tag in common. 
 * @author Gianmarco Petrocchi.
 *
 */
public class ViewListUsersServiceImpl implements ViewListUsersService {
	private Database db;
	private BufferedWriter writerOutput;
	
	public ViewListUsersServiceImpl(Database db, BufferedWriter writerOutput) {
		this.db = db;
		this.writerOutput = writerOutput; 
	}
	
	@Override
	public void viewListUsers(Socket socket) throws IOException {
		if(db.getUserLoggedIn().containsKey(socket) == false) {
			writerOutput.write(TypeError.CLIENTNOTLOGGED);
			writerOutput.newLine();
			writerOutput.flush();
			
			return;
		}
		
		String usersRegisteredJson = db.getRegisteredUsersJson(db.getUsernameBySocket(socket));
		
		writerOutput.write(usersRegisteredJson);
		writerOutput.newLine();
		writerOutput.flush();
		
		return;
	}

}
