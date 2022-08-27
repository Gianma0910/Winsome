package client;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Objects;

import RMI.ClientStorage;
import utility.TypeError;

@SuppressWarnings("serial")
/**
 * Class that represents the storage where there are updated followers and following lists.
 * This two lists are updated with callback service and asynchronous response by server.
 * @author Gianmarco Petrocchi.
 */
public class ClientStorageImpl extends UnicastRemoteObject implements ClientStorage, Serializable {

	/** Followers list.*/
	private ArrayList<String> followers;
	/** Following list.*/
	private ArrayList<String> following;
	/** Username that the client used to login in Winsome.*/
	private String username;
	
	/**
	 * Basic constructor. The username initially is null because it will be set after a successfully login.
	 * @throws RemoteException Only when occurs remote error.
	 */
	public ClientStorageImpl() throws RemoteException {
		super();
		this.followers = new ArrayList<>();
		this.following = new ArrayList<>();
		this.username = null;
	}

	@Override
	public String addFollower(String username) {
		Objects.requireNonNull(username, "Username is null");
		
		if(followers.contains(username))
			return TypeError.FOLLOWERERROR;
		
		if(username.equals(this.username))
			return TypeError.FOLLOWHIMSELFERROR;
		
		followers.add(username);
		System.out.println("User " + username + " added to your follower");
		
		return TypeError.SUCCESS;
	}

	@Override
	public String removeFollower(String username) {
		Objects.requireNonNull(username, "User is null");
		
		if(!followers.contains(username))
			return TypeError.FOLLOWERERROR;
			
		if(username.equals(this.username))
			return TypeError.UNFOLLOWHIMSELFERROR;
		
		followers.remove(username);
		System.out.println("User " + username + " removed from your follower");
		
		return TypeError.SUCCESS;
	}

	@Override
	public void setFollowers(ArrayList<String> followers) {
		Objects.requireNonNull(followers, "Follower list is null");
		
		this.followers.addAll(followers);
		System.out.println("Followers has just been setted");
	}
	
	@Override
	public void setFollowing(ArrayList<String> following) {
		Objects.requireNonNull(following, "Following list is null");
		
		this.following = following;
	}
	
	@Override
	public ArrayList<String> getFollowing(){
		return following;
	}
	
	/**
	 * @return Followers list.
	 */
	public ArrayList<String> getFollowers(){
		return followers;
	}
	
	/**
	 * Set the username of client. Method used after a successfully login.
	 * @param username Username used by client to login.
	 */
	public void setUsername(String username) {
		Objects.requireNonNull(username, "Username to associate is null");
		
		this.username = username;
	}
	
	/**
	 * @return The username that the client used to login.
	 */
	public String getUsername() {
		return username;
	}
}
