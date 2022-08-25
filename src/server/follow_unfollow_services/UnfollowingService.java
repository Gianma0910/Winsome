package server.follow_unfollow_services;

import java.io.IOException;
import java.net.Socket;

import RMI.RMICallback;

public interface UnfollowingService {

	void removeFollowing(String usernameToUnfollow, RMICallback stubCallbackRegistration, Socket socket) throws IOException;
	
}
