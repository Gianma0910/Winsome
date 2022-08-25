package server.login_logout_services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import configuration.ServerConfiguration;

/**
 * Service that allow a user to login in Winsome
 * @author Gianmarco Petrocchi
 *
 */
public interface LoginService {
	
	/**
	 * This method allow a user, already registered in Winsome, to login with his username and password. At the end the server send a certain kind of error to the client.
	 * @param username User's username. Cannot be null
	 * @param password User's password. Cannot be null
	 * @param socketClient Client's socket that want to login. Cannot be null
	 */
	void login(String username, String password, Socket socketClient, ServerConfiguration serverConf) throws IOException;
	
}
