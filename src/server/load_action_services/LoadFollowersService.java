package server.load_action_services;

import java.io.IOException;
import java.rmi.RemoteException;

import RMI.RMICallback;

/**
 * Interface that contains methods to handle load followers service provided by server.
 * @author Gianmarco Petrocchi.
 */
public interface LoadFollowersService {

	/**
	 * Method used to contains method to handle load followers service provided by server.
	 * @param stubCallbackRegistration ClientStorage stub used to set his followers list. Cannot be null.
	 * @param username Username taht the client used to logged in Winsome. Cannot be null.
	 * @throws RemoteException Only when occurs remote error.
	 * @throws IOException Only when occurs I/O error.
	 */
	void loadFollowers(RMICallback stubCallbackRegistration, String username) throws RemoteException, IOException;
	
}
