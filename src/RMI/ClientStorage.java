package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Interface that contains signatures of method that must be implemented in ClientStorareImpl class.
 * This interface contains method to update followers and following lists.
 * @author Gianmarco Petrocchi.
 */
public interface ClientStorage extends Remote{

	/**
	 * Add a follower to the followers list. If the followers list already contained username parameter, the client will receive FOLLOWERROR.
	 * If the username parameter is equals to the username that the client used to logged in Winsome, the client will receive FOLLOWHIMSELFERROR.
	 * @param u Username of follower. Cannot be null.
	 * @return A string of successfully add follower.
	 * @throws RemoteException Only when occurs remote error.
	 */
	String addFollower(String u) throws RemoteException;
	
	/**
	 * Remove a follower from the followers list. If the followers list doesn't contain username parameter, the client will receive FOLLOWERROR.
	 * If the username parameter is equals to the username that the client used to logged in Winsome, the client will receive UNFOLLOWHIMSELFERROR.
	 * @param u Username of follower. Cannot be null.
	 * @return A string of successfully remove follower.
	 * @throws RemoteException Only when occurs remote error.
	 */
	String removeFollower(String u) throws RemoteException;

	/**
	 * Set the followers list.
	 * @param followers Followers list. Cannot be null.
	 * @throws RemoteException Only when occurs remote error.
	 */
	void setFollowers(ArrayList<String> followers) throws RemoteException;

	/**
	 * Set the following list.
	 * @param following Following list. Cannot be null.
	 * @throws RemoteException Only when occurs remote error.
	 */
	void setFollowing(ArrayList<String> following) throws RemoteException;
	
	/**
	 * @return Following list.
	 * @throws RemoteException Only when occurs remote error.
	 */
	ArrayList<String> getFollowing() throws RemoteException;
}
