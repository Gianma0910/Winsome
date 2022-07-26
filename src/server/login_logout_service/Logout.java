package server.login_logout_service;

/**
 * Service that allow a user to logout from Winsome
 * @author Gianmarco Petrocchi
 *
 */
public interface Logout {

	/**
	 * This method allows a user to logout from Winsome by using the specified username 
	 * @param username User's username that want logout from Winsome
	 * @return A String that represents the error that occurs during the execution of method
	 */
	String logout(String username);
	
}
