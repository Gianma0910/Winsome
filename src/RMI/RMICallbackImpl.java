package RMI;

import java.rmi.RemoteException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import exceptions.ClientNotRegisteredException;

/**
 * Class that represents a list of ClientStorage stub. This class is used to register a client stub into followers/following update service.
 * @author Gianmarco Petrocchi.
 *
 */
public class RMICallbackImpl implements RMICallback {
	
	/**
	 * Map of users that are registered in followers/following service. 
	 * <K, V>: K is a String that represents the username used by client to logged in Winsome, V is a ClientStorgeObject that represents the stub.
	 */
	private ConcurrentHashMap<String, ClientStorage> clientsStubs;
	
	public RMICallbackImpl() {
		super();
		this.clientsStubs = new ConcurrentHashMap<String, ClientStorage>();
	}

	@Override
	public void registerForCallback(ClientStorage stub, String username) throws RemoteException{
		Objects.requireNonNull(stub, "Stub is null");
		Objects.requireNonNull(username, "Username associate to the stub is null");
	
		clientsStubs.putIfAbsent(username, stub);
	}

	@Override
	public void unregisterForCallback(ClientStorage stub) throws RemoteException, ClientNotRegisteredException {
		Objects.requireNonNull(stub, "The specified stub is null");
		
		if(!clientsStubs.containsValue(stub))
			throw new ClientNotRegisteredException("Doesn't exists this client registered to the follower/following service");
	
		for(String username : clientsStubs.keySet()) {
			if((clientsStubs.get(username)).equals(stub)) {
				clientsStubs.remove(username, stub);
				return;
			}else continue;
		}
	}
	
	@Override
	public ClientStorage getCallback(String username) {
		return clientsStubs.get(username);
	}
	
}
