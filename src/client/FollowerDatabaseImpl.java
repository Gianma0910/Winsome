package client;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Objects;

import RMI.FollowerDatabase;
import utility.TypeError;
import utility.User;

public class FollowerDatabaseImpl extends UnicastRemoteObject implements FollowerDatabase, Serializable {

	private ArrayList<String> followers;
	private ArrayList<String> following;
	private String username;
	
	public FollowerDatabaseImpl() throws RemoteException {
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
		
		followers.add(username);
		System.out.println("User " + username + " added to your follower");
		
		return TypeError.SUCCESS;
	}

	@Override
	public String removeFollower(String username) {
		Objects.requireNonNull(username, "User is null");
		
		if(!followers.contains(username))
			return TypeError.FOLLOWERERROR;
			
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
	
	public ArrayList<String> getFollowers(){
		return followers;
	}
	
	public void setUsername(String username) {
		Objects.requireNonNull(username, "Username to associate is null");
		
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setFollowing(ArrayList<String> following) {
		Objects.requireNonNull(following, "Following list is null");
		
		this.following = following;
	}
	
	public ArrayList<String> getFollowing(){
		return following;
	}
	
}
