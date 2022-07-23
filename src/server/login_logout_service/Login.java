package server.login_logout_service;

import java.net.Socket;

public interface Login {
	
	/**
	 * This method allow a user, already registered in Winsome, to login with his username and password
	 * @param username User's username. Cannot be null
	 * @param password User's password. Cannot be null
	 * @param socketClient Client's socket that want to login. Cannot be null
	 * @return A String that represents the type of error that occurs during the execution of method. Return TypeError.SUCCESS if the operation is successfully completed
	 */
	String login(String username, String password, Socket socketClient);
	
}
