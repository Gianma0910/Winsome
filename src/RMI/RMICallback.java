package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

import exceptions.ClientNotRegisteredException;

/**
 * Interface that contains signatures of method that must be implemented in RMICallbackImpl class.
 * This interface contains method to register and unregister client to callback service.
 * @author Gianmarco Petrocchi.
 *
 */
public interface RMICallback extends Remote{
	
	/**
	 * Method used to register a Winsome user into the follower/following service.
	 * Follower/following of a user are updated with the RMI callback.
	 * @param stub Remote object of the class FollowerDatabase that contains methods to update the follower/following of a Winsome user.
	 * @param username Username associated to the stub.
	 * @throws RemoteExcpetion Whan occurs remote error.
	 */
	void registerForCallback(ClientStorage stub, String username) throws RemoteException;
	
	/**
	 * Method used to unregister a Winsome user from the follower/following service.
	 * @param stub Remote object of the class FollowerDatabase that contains methods to update the follower/following of a Winsome user.
	 * @throws ClientNotRegisteredException Occurs when a user try to unregister from the service without having previously registered.
	 * @throws RemoteException When occurs remote error.
	 */
	void unregisterForCallback(ClientStorage stub) throws RemoteException, ClientNotRegisteredException;
	
	/**
	 * Method used to get the stub of FollowerDatabase class associated to the specified username.
	 * @param username Username used to get his stub. Cannot be null.
	 * @return An object of class FollowerDatabase associated to the specified username.
	 * @throws RemoteException When occurs remote error.
	 */
	ClientStorage getCallback(String username) throws RemoteException;
}
