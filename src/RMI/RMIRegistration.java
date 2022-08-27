package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Interface that contains a method signature that must be implemented in RMIRegistrationImpl.
 * @author Gianmarco Petrocchi.
 *
 */
public interface RMIRegistration extends Remote{

	/**
	 * Method used to register a new Winsome user.
	 * @param username New user's username. Cannot be null.
	 * @param password New user's password. Cannot be null.
	 * @param tgs New user's list of tags. Cannot be null.
	 * @throws RemoteException When occurs remote error.
	 * @return A String that represents the type of error that occurs during the execution of method. Return TypeError.SUCCESS if the operation is successfully completed.
	 */
	String register(String username, String password, ArrayList<String> tgs) throws RemoteException;
	
}
