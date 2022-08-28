package server.login_logout_services;

import java.io.IOException;
import java.net.Socket;

import configuration.ServerConfiguration;

/**
 * Interface that contains methods to handle login service provided by server.
 * @author Gianmarco Petrocchi
 *
 */
public interface LoginService {
	
	/**
	 * Method used to handle login service provided by server. Allows a user already registered in Winsome to login.
	 * It checks if the username specified to login is registered, otherwise the client will receive USERNAMEWRONG error.
	 * It checks if the password specified to login is associated with the right username, otherwise the client will receive PASSWORDWRONG error.
	 * It checks if the username specified to login is not already logged in Winsome, otherwise the client will receive USERALREADYLOGGED error.
	 * It checks if the client that send login request isn't already logged with another user, otherwise the client will receive CLIENTALREADYLOGGED error.
	 * @param username User's username. Cannot be null
	 * @param password User's password. Cannot be null
	 * @param socketClient Socket of client that send the login request. Cannot be null
	 */
	void login(String username, String password, Socket socketClient, ServerConfiguration serverConf) throws IOException;
	
}
