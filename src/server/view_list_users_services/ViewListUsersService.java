package server.view_list_users_services;

import java.io.IOException;
import java.net.Socket;

/**
 * Interface that contains methods to handle list users service provided by server. 
 * @author Gianmarco Petrocchi.
 */
public interface ViewListUsersService {

	/**
	 * Method used to handle list users service provided by server. 
	 * Before handle user request it checks if it is logged in Winsome, otherwise the user will receive CLIENTNOTLOGGED error.
	 * @param socket Socket of client that send the list users request. Cannot be null.
	 * @throws IOException Only when occurs I/O error.
	 */
	void viewListUsers(Socket socket) throws IOException;
	
}
