package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

import client.FollowerDatabase;
import exceptions.ClientNotRegisteredException;

public interface RMICallback extends Remote{
	
	/**
	 * Method used to register a Winsome user into the follower/following service.
	 * Follower/following of a user are updated with the RMI callback.
	 * @param stub Remote object of the class FollowerDatabase that contains methods to update the follower/following of a Winsome user
	 * @param username Username associated to the stub
	 * @throws RemoteExcpetion Whan occurs remote error
	 */
	void registerForCallback(FollowerDatabase stub, String username) throws RemoteException;
	
	/**
	 * Method used to unregister a Winsome user from the follower/following service.
	 * @param stub Remote object of the class FollowerDatabase that contains methods to update the follower/following of a Winsome user
	 * @param username Username associated to the stub 	
	 * @throws ClientNotRegisteredException Occurs when a user try to unregister from the service without having previously registered
	 * @throws RemoteException When occurs remote error
	 */
	void unregisterForCallback(String username) throws RemoteException, ClientNotRegisteredException;
}
