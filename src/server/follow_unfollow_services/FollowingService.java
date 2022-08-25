package server.follow_unfollow_services;

import java.io.IOException;
import java.net.Socket;

import RMI.RMICallback;

public interface FollowingService {

	void addFollower(String usernameToFollow, RMICallback stubCallbackRegistration, Socket socket) throws IOException;
	
}
