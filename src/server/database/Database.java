package server.database;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import utility.Post;
import utility.User;

/**
 * Class that contains all the data of Winsome (Users, Posts, Comments, Votes, Transactions Wallet, follower and following)
 * @author Gianmarco Petrocchi
 *
 */
public class Database {

	/**Concurrent collection that contains user to be backuped in user's file
	 * <K, V>: K is a String that represents the username; V is an User object with the username specified in K */
	private ConcurrentHashMap<String, User> userToBeBackuped;
	//private ConcurrentHashMap<String, User> userBackuped;
	/** Concurrent colleciton that contains user logged in Winsome
	 * <K, V>: K is the client socket; V is a String that reppresents the logged user's username */
	private ConcurrentHashMap<Socket, String> userLoggedIn;
	
	private ConcurrentHashMap<String, ArrayList<String>> userFollowing;
	
	private ConcurrentHashMap<String, ArrayList<String>> userFollower;
	
	private ConcurrentHashMap<String, Post> userPosts;
	
	private AtomicInteger idPost;
	
	/**
	 * Basic constructor for Database class
	 */
	public Database() {
		this.userToBeBackuped = new ConcurrentHashMap<String, User>();
		//this.userBackuped = new ConcurrentHashMap<String, User>();
		this.userLoggedIn = new ConcurrentHashMap<Socket, String>();
		this.userFollowing = new ConcurrentHashMap<String, ArrayList<String>>();
		this.userFollower = new ConcurrentHashMap<String, ArrayList<String>>();
		this.userPosts = new ConcurrentHashMap<String, Post>();
		
		this.idPost = new AtomicInteger(0);

	}
	
	/**
	 * Add a new User in database after the registration
	 * @param username User's username. Cannot be null
	 * @param u User to be registered. Cannot be null
	 */
	public void addUserToDatabase(String username, User u) {
		Objects.requireNonNull(username, "Username is null");
		Objects.requireNonNull(u, "User is null");
		
		userToBeBackuped.putIfAbsent(username, u);
	}
	
	/** 
	 * Check if the user is registered
	 * @param username User's username. Cannot be null
	 * @return true if the user is registerd, false otherwise
	 */
	public boolean isUserRegistered(String username) {		
		return userToBeBackuped.containsKey(username);
	}
	
	/**
	 * Return an User object with the specified username
	 * @param username User's username
	 * @return An User object with the specified username, null otherwise
	 */
	public User getUserByUsername(String username) {
		Objects.requireNonNull(username, "Username is null");
		
		User u = userToBeBackuped.get(username);
		
		return u;
	}
	
	/**
	 * Method used after a successfully login. Add the specified client socket and username to a collection
	 * called userLoggedIn. It's used to check if a client try to login with a username that is already logged in Winsome
	 * @param socketClient Client socket that communicate with the server for login. Cannot be null
	 * @param username User's username used to login. Cannot be null
	 */
	public void addUserLoggedIn(Socket socketClient, String username) {
		Objects.requireNonNull(socketClient, "Parameter socket is null");
		Objects.requireNonNull(username, "Username is null");
		
		userLoggedIn.putIfAbsent(socketClient, username);
	}
	
	/**
	 * Method used for a logout request. Remove the socket and the mapped value, so the client is no longer logged in
	 * @param socket Client socket to logout. Cannot be null
	 * @return true if the element is removed, false otherwise
	 */
	public boolean removeUserLoggedIn(Socket socket) {
		Objects.requireNonNull(socket, "Parameter socket is null");
		
		boolean result = userLoggedIn.remove(socket, userLoggedIn.get(socket));		
		return result;
	}
	
	public String getUsernameBySocket(Socket socket) {
		Objects.requireNonNull(socket, "Parameter socket is null");
		
		return userLoggedIn.get(socket);
	}
	
	/**
	 * @return The ConcurrentHashMap of user that are already logged in
	 */
	public ConcurrentHashMap<Socket, String> getUserLoggedIn(){
		return userLoggedIn;
	}
	
	public void setFollowerListUser(String username) {
		Objects.requireNonNull(username, "Username is null");
		
		userFollower.putIfAbsent(username, new ArrayList<>());
		
		return;
	}
	
	public void addFollower(String usernameUpdateFollower, String usernameNewFollower) {
		Objects.requireNonNull(usernameUpdateFollower, "Username used to update his followers is null");
		Objects.requireNonNull(usernameNewFollower, "Username new follower is null");
		
		ArrayList<String> followers = userFollower.get(usernameUpdateFollower);
		
		if(followers == null) {
			followers = new ArrayList<>();
			followers.add(usernameNewFollower);
		}else {
			followers.add(usernameNewFollower);
		}
		
		return;
	}
	
	public void removeFollower(String usernameUpdateFollower, String usernameToRemove) {
		Objects.requireNonNull(usernameUpdateFollower, "Username used to update his followers is null");
		Objects.requireNonNull(usernameToRemove, "Username to remove from followers is null");
		
		ArrayList<String> followers = userFollower.get(usernameUpdateFollower);
		
		followers.remove(usernameToRemove);
	}
	
	public ArrayList<String> getFollowerListByUsername(String username) {
		Objects.requireNonNull(username, "Username is null");
		
		return userFollower.get(username);
	}
	
	public void setFollowingListForUser(String username) {
		Objects.requireNonNull(username, "Username is null");
		
		userFollowing.putIfAbsent(username, new ArrayList<>());
		
		return;
	}
	
	public void addFollowing(String usernameUpdateFollowing, String usernameNewFollowing) {
		Objects.requireNonNull(usernameUpdateFollowing, "Username used to update his following is null");
		Objects.requireNonNull(usernameNewFollowing, "Username new following is null");
		
		ArrayList<String> following = userFollowing.get(usernameUpdateFollowing);
		
		if(following == null) {
			following = new ArrayList<>();
			following.add(usernameNewFollowing);
		}else {
			following.add(usernameNewFollowing);
		}
		return;
	}
	
	public void removeFollowing(String usernameUpdateFollowing, String usernameToRemove) {
		Objects.requireNonNull(usernameUpdateFollowing, "Username user to update his following is null");
		Objects.requireNonNull(usernameToRemove, "Username to be removed from following is null");
		
		ArrayList<String> following = userFollowing.get(usernameUpdateFollowing);
		
		following.remove(usernameToRemove);
		
		return;
	}
	
	public String toStringFollowingListByUsername(String username) {
		Objects.requireNonNull(username, "Username is null");
		StringBuilder response = new StringBuilder();
		
		ArrayList<String> following = userFollowing.get(username);
		Iterator<String> it = following.iterator();
		
		response.append("[");
		while(it.hasNext()) {
			response.append(it.next());
			if(it.hasNext())
				response.append(", ");
		}
		response.append("]");
		
		return response.toString();
	}
	
	public ArrayList<String> getFollowingListByUsername(String username){
		Objects.requireNonNull(username, "Username is null");
		
		return userFollowing.get(username);
	}
	
	public String getRegisteredUsersJson(String username) {
		User user = userToBeBackuped.get(username);
		ArrayList<String> tagList = user.getTagList();
		
		Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {

			@Override
			public boolean shouldSkipClass(Class<?> arg0) {
				return false;
			}

			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				return (f.getDeclaringClass() == User.class && f.getName().equals("password"));
			}
			
		}).create();
		
		StringBuilder serializationUsers = new StringBuilder();
		ArrayList<User> registeredUsers = new ArrayList<>();
		
		for(String s : userToBeBackuped.keySet()) {
			if(userToBeBackuped.get(s).getUsername().equals(username)) continue;
			else registeredUsers.add(userToBeBackuped.get(s));
		}
		
		Iterator<User> it = registeredUsers.iterator();
		serializationUsers.append("[");
		
		while(it.hasNext()) {
			User u = it.next();
			for(String s : tagList) {
				if(u.getTagList().contains(s)) {
					serializationUsers.append(gson.toJson(u));
					if(it.hasNext())
						serializationUsers.append(",");
					
					break;
				}
				else continue;
			}
		}
		serializationUsers.append("]");
		
		System.out.println(serializationUsers.toString());
		
		return serializationUsers.toString();
	}
	
	public synchronized int getAndIncrementIdPost() {
		return idPost.getAndIncrement();
	}

	public String addPostInWinsome(int idPost, String authorPost, String titlePost, String contentPost) {
		Post newPost = new Post(idPost, titlePost, contentPost, authorPost);
	
		userPosts.putIfAbsent(authorPost, newPost);
		
		return "New post created with id: " + idPost;
	}

}
