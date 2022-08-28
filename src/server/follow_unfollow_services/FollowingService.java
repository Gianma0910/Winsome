package server.follow_unfollow_services;

import java.io.IOException;
import java.net.Socket;

import RMI.RMICallback;

/**
 * Interface that contains methods to handle add follower service provided by server.
 * @author Gianmarco Petrocchi.
 */
public interface FollowingService {

	/**
	 * Method used to handle add follower service provided by server.
	 * Before handle user request it checks if it is logged in Winsome, otherwise the user will receive CLIENTNOTLOGGED error.
	 * Before handle user request it checks if the new following exists in Winsome, otherwise the user will receive FOLLOWERNOTEXISTS error. 
	 * @param usernameToFollow Username of user that represents the new following. Cannot be null.
	 * @param stubCallbackRegistration ClientStorage stub used to update user's followers list. Cannot be null.
	 * @param socket Socket of the client that send the request to add follower. Cannot be null.
	 * @throws IOException Only when occurs I/O error.
	 */
	void addFollower(String usernameToFollow, RMICallback stubCallbackRegistration, Socket socket) throws IOException;
	
}
