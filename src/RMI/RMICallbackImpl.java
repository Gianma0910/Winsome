package RMI;

import java.rmi.RemoteException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import client.FollowerDatabase;
import exceptions.ClientNotRegisteredException;

public class RMICallbackImpl implements RMICallback {
	
	private ConcurrentHashMap<String, FollowerDatabase> clientsStubs;
	
	public RMICallbackImpl() {
		super();
		this.clientsStubs = new ConcurrentHashMap<String, FollowerDatabase>();
	}

	@Override
	public void registerForCallback(FollowerDatabase stub, String username) throws RemoteException{
		Objects.requireNonNull(stub, "Stub is null");
		Objects.requireNonNull(username, "Username associate to the stub is null");
	
		clientsStubs.putIfAbsent(username, stub);
	}

	@Override
	public void unregisterForCallback(String username) throws RemoteException, ClientNotRegisteredException {
		Objects.requireNonNull(username, "Username associated to the stub is null");
		
		if(!clientsStubs.containsKey(username))
			throw new ClientNotRegisteredException("Doesn't exists a client registered to the follower/following service with the specified username");
	
		clientsStubs.remove(username, clientsStubs.get(username));
	}
	
}
