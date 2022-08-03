package RMI;

import java.rmi.RemoteException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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
	public void unregisterForCallback(FollowerDatabase stub) throws RemoteException, ClientNotRegisteredException {
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
	public FollowerDatabase getCallback(String username) {
		return clientsStubs.get(username);
	}
	
}
