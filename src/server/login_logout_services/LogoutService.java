package server.login_logout_services;

import java.io.IOException;
import java.net.Socket;

/**
 * Interface that contains methods to handle logout service provided by server.
 * @author Gianmarco Petrocchi.
 *
 */
public interface LogoutService {

	/**
	 * Method used to handle logout service provided by server. Only user register and logged can logout from Winsome.
	 * Before handle user request it checks if it is logged in Winsome, otherwise the user will receive CLIENTNOTLOGGED error.
	 * If the logout operation terminate erroneously, the client will receive LOGOUTERROR error.
	 * @param socket Socket of client that send logout request. Cannot be null.
	 */
	void logout(Socket socket) throws IOException;
	
}
