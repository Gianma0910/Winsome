package server.load_action_services;

import java.io.IOException;
import java.rmi.RemoteException;

import RMI.RMICallback;

/**
 * Interface that contains methods to handle load following service provided by server.
 * @author Gianmarco Petrocchi.
 *
 */
public interface LoadFollowingService {

	/**
	 * Method used to handle load following service provided by server.
	 * @param stubCallbackRegistration ClientStorage stub used to set his following list. Cannot be null.
	 * @param username Username that the client used to logged in Winsome. Cannot be null.
	 * @throws RemoteException Only when occurs remote error.
	 * @throws IOException Only when occurs I/O error.
	 */
	void loadFollowing(RMICallback stubCallbackRegistration, String username) throws RemoteException, IOException;
	
}
