package server.load_action_services;

import java.io.IOException;
import java.rmi.RemoteException;

import RMI.RMICallback;

public interface LoadFollowingService {

	void loadFollowing(RMICallback stubCallbackRegistration, String username) throws RemoteException, IOException;
	
}
