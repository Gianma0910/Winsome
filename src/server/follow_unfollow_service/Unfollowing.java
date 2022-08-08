package server.follow_unfollow_service;

import java.io.IOException;
import java.net.Socket;

import RMI.RMICallback;

public interface Unfollowing {

	void removeFollowing(String usernameToUnfollow, RMICallback stubCallbackRegistration, Socket socket) throws IOException;
	
}
