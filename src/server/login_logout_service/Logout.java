package server.login_logout_service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

/**
 * Service that allow a user to logout from Winsome
 * @author Gianmarco Petrocchi
 *
 */
public interface Logout {

	/**
	 * This method allows a user to logout from Winsome by using the specified username. At the end the server send a certain kind of error to the client
	 * @param username User's username that want logout from Winsome
	 */
	void logout(Socket socket) throws IOException;
	
}
