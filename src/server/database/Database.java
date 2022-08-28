package server.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import exceptions.ClientNotRegisteredException;
import exceptions.IllegalFileException;
import exceptions.InvalidAmountException;
import utility.Comment;
import utility.GainAndCurators;
import utility.Post;
import utility.Transaction;
import utility.User;
import utility.Vote;

/**
 * Class that contains all the data of Winsome (Users, Posts, Comments, Votes, Transactions Wallet, follower and following).
 * @author Gianmarco Petrocchi.
 *
 */
public class Database extends Storage{
	
	/**Concurrent collection that contains users to be backed up in user's file.
	 * <K, V>: K is a String that represents the username; V is an User object with the username specified in K. */
	private ConcurrentHashMap<String, User> userToBeBackedup;
	
	/**Concurrent collection that contains users already backed up in user's file.
	 * <K, V>: K is a String that represents the username; V is an User object with the username specified in K. */
	private ConcurrentHashMap<String, User> userBackedUp;
	
	/** Concurrent collection that contains user logged in Winsome.
	 * <K, V>: K is the client socket; V is a String that represents the logged user's username.*/
	private ConcurrentHashMap<Socket, String> userLoggedIn;
	
	/**
	 * Concurrent collection that contains, for all the registered users, his following list.
	 * <K, V>: K is a String that represents the username; V is an ArrayList that represents his following list.*/
	private ConcurrentHashMap<String, ArrayList<String>> userFollowing;
	
	/**
	 * Concurrent collections that contains, for all the registered users, his followers list.
	 * <K, V>: K is a String that represents the username; V is an ArrayList that represents his followers list.*/
	private ConcurrentHashMap<String, ArrayList<String>> userFollower;
	
	/**
	 * Concurrent collection that contains, for all the registered used, his posts.
	 * <K, V>: K is a String that represents the username; V is an ArrayList that represents his posts.*/
	private ConcurrentHashMap<String, ArrayList<Post>> blogUser;
	
	/**
	 * Concurrent collection that contains posts to be backed up.
	 * <K, V>: K is an Integer that represents idPost; V is a Post object with the idPost specified in K*/
	private ConcurrentHashMap<Integer, Post> postToBeBackedup;
	
	/**
	 * Concurrent collection that contains posts already backed up.
	 * <K, V>: K is an Integer that represents idPost; V is a Post object with the idPost specified in K*/
	private ConcurrentHashMap<Integer, Post> postBackedup;
	
	/**
	 * Concurrent collection that contains posts rewinned by user.
	 * <K, V>: K is a String that represents username of user; V is a ConcurrentLinkedQueue of rewinned posts.*/
	private ConcurrentHashMap<String, ConcurrentLinkedQueue<Post>> postRewinnedByUser;
	
	/** Variable used when a new post is created. It will be increment by one and it must be unique for all posts.*/
	private AtomicInteger idPost;
	
	/** Toggled on if this is the first backup and the storage has been recovered from a JSON file. */
	private boolean postRecoveredFromBackup = false;
	
	/** Toggled on if this is the first backup and the storage has been recovered from a JSON file.*/
	private boolean firstBackupForUsers = false;
	
	/** Toggled on if a post has been deleted since the last backup. */
	private boolean postDeletedSinceLastBackup = false;
	
	/**
	 * Basic constructor for Database.
	 */
	public Database() {
		this.userToBeBackedup = new ConcurrentHashMap<String, User>();
		this.userBackedUp = new ConcurrentHashMap<String, User>();
		this.userLoggedIn = new ConcurrentHashMap<Socket, String>();
		this.userFollowing = new ConcurrentHashMap<String, ArrayList<String>>();
		this.userFollower = new ConcurrentHashMap<String, ArrayList<String>>();
		this.blogUser = new ConcurrentHashMap<String, ArrayList<Post>>();
		this.postToBeBackedup = new ConcurrentHashMap<Integer, Post>();
		this.postBackedup = new ConcurrentHashMap<Integer, Post>();
		this.postRewinnedByUser = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Post>>();
		
		this.idPost = new AtomicInteger(0);
		this.firstBackupForUsers = false;
		this.postRecoveredFromBackup = false;
	}
	
	/**
	 * Add a new User in database after the registration
	 * @param username User's username. Cannot be null
	 * @param u User to be registered. Cannot be null
	 */
	public void addUserToDatabase(String username, User u) {
		Objects.requireNonNull(username, "Username is null");
		Objects.requireNonNull(u, "User is null");
		
		if(Objects.requireNonNull(userToBeBackedup) != null) {
			userToBeBackedup.putIfAbsent(username, u);
		}
	}
	
	/** 
	 * Check if the user is registered
	 * @param username User's username. Cannot be null
	 * @return true if the user is registered, false otherwise
	 */
	public boolean isUserRegistered(String username) {
		Objects.requireNonNull(username, "Username used to check if the user is registered is null");
		
		if(Objects.requireNonNull(userToBeBackedup) != null && Objects.requireNonNull(userToBeBackedup.keySet()) != null
				&& Objects.requireNonNull(userBackedUp) != null && Objects.requireNonNull(userBackedUp.keySet()) != null) {
			
			return (userToBeBackedup.containsKey(username) || userBackedUp.containsKey(username));
		}
		
		return false;
	}
	
	/**
	 * Return an User object with the specified username
	 * @param username User's username
	 * @return An User object with the specified username, null otherwise
	 */
	public User getUserByUsername(String username) {
		Objects.requireNonNull(username, "Username used to get the specified user is null");
		
		if(Objects.requireNonNull(userToBeBackedup) != null && Objects.requireNonNull(userToBeBackedup.keySet()) != null
				&& Objects.requireNonNull(userBackedUp) != null && Objects.requireNonNull(userBackedUp.keySet()) != null) {
			
			if(userToBeBackedup.containsKey(username)) {
				return userToBeBackedup.get(username);
			}else if(userBackedUp.containsKey(username)) {
				return userBackedUp.get(username);
			}
			
			return null;
		}
		
		return null;
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
		
		if(Objects.requireNonNull(userLoggedIn) != null) {
			userLoggedIn.putIfAbsent(socketClient, username);
		}
	}
	
	/**
	 * Method used for a logout request. Remove the socket and the mapped value, so the client is no longer logged in
	 * @param socket Client socket to logout. Cannot be null
	 * @return true if the element is removed, false otherwise
	 */
	public boolean removeUserLoggedIn(Socket socket) {
		Objects.requireNonNull(socket, "Parameter socket is null");
		
		if(Objects.requireNonNull(userLoggedIn) != null && Objects.requireNonNull(userLoggedIn.keySet()) != null) {
			boolean result = userLoggedIn.remove(socket, userLoggedIn.get(socket));		
			return result;
		}
		
		return false;
	}
	
	/**
	 * @param socket Socket client. Cannot be null.
	 * @return Username of logged client identified by socket parameter.
	 */
	public String getUsernameBySocket(Socket socket) {
		Objects.requireNonNull(socket, "Parameter socket use to get the logged user is null");
		
		if(Objects.requireNonNull(userLoggedIn) != null && Objects.requireNonNull(userLoggedIn.keySet()) != null) {
			return userLoggedIn.get(socket);
		}
		
		return null;
	}
	
	/**
	 * @return The ConcurrentHashMap of user that are already logged in.
	 */
	public ConcurrentHashMap<Socket, String> getUserLoggedIn(){
		if(Objects.requireNonNull(userLoggedIn) != null && Objects.requireNonNull(userLoggedIn.keySet()) != null) {
			return userLoggedIn;
		}
		
		return null;
	}
	
	/**
	 * Check if the user with the specified username is logged in Winsome.
	 * @param username Username of user to get. Cannot be null.
	 * @return true if the user is logged, false otherwise.
	 */
	public boolean isUserLogged(String username) {
		Objects.requireNonNull(username, "Username used to verify if the specified use is logged is null");
		
		if(Objects.requireNonNull(userLoggedIn) != null && Objects.requireNonNull(userLoggedIn.keySet()) != null) {
			for(Socket socket : userLoggedIn.keySet()) {
				if(userLoggedIn.get(socket).equals(username))
					return true;
				else continue;
			}
			
			return false;
		}
		
		return false;
	}
	
	/**
	 * Set followers list of a user identified by the username.
	 * @param username Username of user to set his followers list. Cannot be null.
	 */
	public void setFollowerListUser(String username) {
		Objects.requireNonNull(username, "Username used to set his follower list is null");
		
		if(Objects.requireNonNull(userFollower) != null) {
			userFollower.putIfAbsent(username, new ArrayList<>());
		}
	}
	
	/**
	 * Add follower to user's followers collection.
	 * @param usernameUpdateFollower Username of user that receive a new follow. Cannot be null.
	 * @param usernameNewFollower Username of a user that send follow request.
	 */
	public void addFollower(String usernameUpdateFollower, String usernameNewFollower) {
		Objects.requireNonNull(usernameUpdateFollower, "Username used to update his followers is null");
		Objects.requireNonNull(usernameNewFollower, "Username new follower is null");
		
		if(Objects.requireNonNull(userFollower) != null && Objects.requireNonNull(userFollower.keySet()) != null) {
			ArrayList<String> followers = userFollower.get(usernameUpdateFollower);
			
			if(followers == null) {
				followers = new ArrayList<>();
				followers.add(usernameNewFollower);
			}else {
				followers.add(usernameNewFollower);
			}
			
			return;
		}
	}

	/**
	 * Remove follower from user's followers collection.
	 * @param usernameUpdateFollower Username of user that unfollow another user. Cannot be null.
	 * @param usernameToRemove Username of user to remove from userFollower.
	 */
	public void removeFollower(String usernameUpdateFollower, String usernameToRemove) {
		Objects.requireNonNull(usernameUpdateFollower, "Username used to update his followers is null");
		Objects.requireNonNull(usernameToRemove, "Username to remove from followers is null");
		
		if(Objects.requireNonNull(userFollower) != null && Objects.requireNonNull(userFollower.keySet()) != null) {
			ArrayList<String> followers = userFollower.get(usernameUpdateFollower);
			
			followers.remove(usernameToRemove);
		}
	}

	/**
	 * @param username Username to get his followers list.
	 * @return Followers list of a user identified by the username.
	 */
	public ArrayList<String> getFollowerListByUsername(String username) {
		Objects.requireNonNull(username, "Username used to get his follower list is null");
		
		if(Objects.requireNonNull(userFollower) != null && Objects.requireNonNull(userFollower.keySet()) != null) {
			return userFollower.get(username);
		}
		
		return null;
	}
	
	/**
	 * Set following list of user identified by username.
	 * @param username Username of user. Cannot be null.
	 */
	public void setFollowingListForUser(String username) {
		Objects.requireNonNull(username, "Username used to set his following list is null");
		
		if(Objects.requireNonNull(userFollowing) != null) {
			userFollowing.putIfAbsent(username, new ArrayList<>());	
		}
		
		return;
	}
	
	/**
	 * Add following in user's following collection.
	 * @param usernameUpdateFollowing Username of user that follow a new user. Cannot be null.
	 * @param usernameNewFollowing Username of user that receive a new follow. Cannot be null.
	 */
	public void addFollowing(String usernameUpdateFollowing, String usernameNewFollowing) {
		Objects.requireNonNull(usernameUpdateFollowing, "Username used to update his following is null");
		Objects.requireNonNull(usernameNewFollowing, "Username new following is null");
		
		if(Objects.requireNonNull(userFollowing) != null && Objects.requireNonNull(userFollowing.keySet()) != null) {
			ArrayList<String> following = userFollowing.get(usernameUpdateFollowing);
			
			if(following == null) {
				following = new ArrayList<>();
				following.add(usernameNewFollowing);
			}else {
				following.add(usernameNewFollowing);
			}
			
			User u = getUserByUsername(usernameUpdateFollowing);
			
			u.addFollowing(usernameNewFollowing);
			
			return;
		}
	}
	
	/**
	 * Remove following in userFollowing collection.
	 * @param usernameUpdateFollowing Username of user that unfollow another user. Cannot be null.
	 * @param usernameToRemove Username of user to remove from userFollowing.
	 */
	public void removeFollowing(String usernameUpdateFollowing, String usernameToRemove) {
		Objects.requireNonNull(usernameUpdateFollowing, "Username user to update his following is null");
		Objects.requireNonNull(usernameToRemove, "Username to be removed from following is null");
		
		if(Objects.requireNonNull(userFollowing) != null && Objects.requireNonNull(userFollowing.keySet()) != null) {
			ArrayList<String> following = userFollowing.get(usernameUpdateFollowing);
			
			following.remove(usernameToRemove);
			
			User u = getUserByUsername(usernameUpdateFollowing);
			
			u.removeFollowing(usernameToRemove);
			
			return;	
		}
	}
	
	/**
	 * Method used to send to user specified by parameter username his following list.
	 * @param username Username used to get his following list. Cannot be null.
	 * @return A string that represents his list of following.
	 */
	public String toStringFollowingListByUsername(String username) {
		Objects.requireNonNull(username, "Username used to get his following list is null");
		StringBuilder response = new StringBuilder();
		
		if(Objects.requireNonNull(userFollowing) != null && Objects.requireNonNull(userFollowing.keySet()) != null) {
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
		
		return "[]";
	}
	
	/**
	 * Method used to get the follwing list of user specified by parameter username.
	 * @param username Username used to get his following list. Cannot be null.
	 * @return An ArrayList<String> that represents the following list of user.
	 */
	public ArrayList<String> getFollowingListByUsername(String username){
		Objects.requireNonNull(username, "Username used to get his following list is null");
		
		if(Objects.requireNonNull(userFollowing) != null && Objects.requireNonNull(userFollowing.keySet()) != null) {
			return userFollowing.get(username);
		}
		
		return null;
	}
	
	/**
	 * Method used to send to user a list of registered users with at least one tag in common. The user will see
	 * for all the users only username and tags list.
	 * @param username Username used to get the specified user with his tag list. Cannot be null.
	 * @return A String that represents the list of register users with at least one tag in common.
	 */
	public String getRegisteredUsersJson(String username) {
		Objects.requireNonNull(username, "Username used to get the specified user is null");
		
		if(Objects.requireNonNull(userToBeBackedup) != null && Objects.requireNonNull(userToBeBackedup.keySet()) != null
				&& Objects.requireNonNull(userBackedUp) != null && Objects.requireNonNull(userBackedUp.keySet()) != null) {
			
			User user = getUserByUsername(username);
			ArrayList<String> tagList = user.getTagList();
			
			Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {

				@Override
				public boolean shouldSkipClass(Class<?> arg0) {
					return false;
				}

				@Override
				public boolean shouldSkipField(FieldAttributes f) {
					return (f.getDeclaringClass() == User.class && !f.getName().equals("username") && !f.getName().equals("tagList"));
				}
				
			}).create();
			
			StringBuilder serializationUsers = new StringBuilder();
			ArrayList<User> registeredUsers = new ArrayList<>();
			
			for(User u : userBackedUp.values()) {
				if(u.getUsername().equals(username)) continue;
				else {
					for(String s : tagList) {
						if(u.getTagList().contains(s)) {
							registeredUsers.add(u);
							break;
						}else continue;
					}
				}
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
		
		return "[]";
	}
	
	/**
	 * Synchronized method used to get and increment the AtomicInteger variable. This variable is common
	 * for all the server threads that work with Database class.
	 * @return An Integer that represents idPost.
	 */
	public synchronized int getAndIncrementIdPost() {
		return idPost.getAndIncrement();
	}
	
	/**
	 * Set the list of posts of user specified by username.
	 * @param username Username used to set his list of posts. Cannot be null.
	 */
	public void setPostListForUser(String username) {
		Objects.requireNonNull(username, "Username used to set his posts list is null");
		
		if(Objects.requireNonNull(blogUser) != null) {
			blogUser.putIfAbsent(username, new ArrayList<Post>());
			
			return;	
		}
	}
	
	/**
	 * Add a post in Winsome with the specified idPost, author, title and content.
	 * @param idPost Id post., incremented by server threads. Cannot be null.
	 * @param authorPost Author of post. Cannot be null.
	 * @param titlePost Title of post. Cannot be null.
	 * @param contentPost Content of post. Cannot be null.
	 * @return A String that indicates the successfully add post in Winsome, or a String that indicates the erroneous add post.
	 */
	public String addPostInWinsome(int idPost, String authorPost, String titlePost, String contentPost) {
		Objects.requireNonNull(idPost,  "Id post is null");
		Objects.requireNonNull(authorPost, "Author post is null");
		Objects.requireNonNull(titlePost, "Title post is null");
		Objects.requireNonNull(contentPost, "Content post is null");
		
		Post newPost = new Post(idPost, titlePost, contentPost, authorPost);
	
		if(Objects.requireNonNull(blogUser) != null && Objects.requireNonNull(blogUser.keySet()) != null
				&& Objects.requireNonNull(postToBeBackedup) != null && Objects.requireNonNull(postToBeBackedup.keySet()) != null) {

			blogUser.get(authorPost).add(newPost);
			postToBeBackedup.putIfAbsent(idPost, newPost);

			return "New post created with id: " + idPost;

		}
		
		return "Impossible to create post with id: " + idPost;
	}

	/**
	 * Method used to send to user his list of posts. The user will see for all the posts only idPost, author and title.
	 * @param username Username used to get the list of posts of the specified user. Cannot be null.
	 * @return A String that represents his list of posts. 
	 */
	public String getUserPostJson(String username) {
		Objects.requireNonNull(username, "Username used to get his posts list is null");
		
		Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {

			@Override
			public boolean shouldSkipClass(Class<?> arg0) {
				return false;
			}

			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				return (f.getDeclaringClass() == Post.class && f.getName().equals("content") && f.getName().equals("rewin") && f.getName().equals("votes") && f.getName().equals("comments")
						&& f.getName().equals("iterations") && f.getName().equals("newCommentsBy") && f.getName().equals("newVotes") && f.getName().equals("curators"));
			}
			
		}).create();
		
		if(Objects.requireNonNull(blogUser) != null && Objects.requireNonNull(blogUser.keySet()) != null) {
			ArrayList<Post> posts = blogUser.get(username);
			
			StringBuilder serializationPosts = new StringBuilder();
			Iterator<Post> it = posts.iterator();
			
			serializationPosts.append("[");
			while(it.hasNext()) {
				serializationPosts.append(gson.toJson(it.next()));
				if(it.hasNext())
					serializationPosts.append(", ");
			}
			serializationPosts.append("]");
			
			return serializationPosts.toString();
		}
		
		return "[]";
	}
	
	/**
	 * Method used to send a user his feed, that is the list of posts of the users he follows.
	 * This method take the list of following of the user specified by username and then build his feed.
	 * The user for all the posts in his feed will see idPost, author and title.
	 * @param username Username used to get his following list, used to build his feed. Cannot be null.
	 * @return A String that represents his feed.
	 */
	public String getUserFeedJson(String username) {
		Objects.requireNonNull(username, "Username used to get his feed is null");
		
		if(Objects.requireNonNull(blogUser) != null && Objects.requireNonNull(blogUser.keySet()) != null
				&& Objects.requireNonNull(userFollowing) != null && Objects.requireNonNull(userFollowing.keySet()) != null) {
			ArrayList<String> following = userFollowing.get(username);
			
			ArrayList<Post> feed = new ArrayList<>();
			
			for(String s : following) {
				ArrayList<Post> postUser = blogUser.get(s);
				feed.addAll(postUser);
			}
			
			Gson gson = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
				
				@Override
				public boolean shouldSkipField(FieldAttributes f) {
					return (f.getDeclaringClass() == Post.class && !f.getName().equals("idPost") && !f.getName().equals("author") && !f.getName().equals("title"));
				}
				
				@Override
				public boolean shouldSkipClass(Class<?> arg0) {
					return false;
				}
			}).create();
			
			Iterator<Post> it = feed.iterator();
			StringBuilder serializationPosts = new StringBuilder();
			
			serializationPosts.append("[");
			while(it.hasNext()) {
				serializationPosts.append(gson.toJson(it.next()));
				
				if(it.hasNext()) {
					serializationPosts.append(", ");
				}
			}
			serializationPosts.append("]");
			
			return serializationPosts.toString();
		}
		
		return "[]";
	}
	
	/**
	 * Method used to send to client a certain post specified by parameter idPost. The user will see for this post only 
	 * title, content, comments, and number of positive and negative votes.
	 * @param idPost Id of post used to get his data. Cannot be null.
	 * @return A String that represents the single post with his data specified in the description of method.
	 */
	public String getPostByIdJson(int idPost) {
		Objects.requireNonNull(idPost, "Id used to get the specified post is null");
		
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
			
			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				return (f.getDeclaringClass() == Post.class && f.getName().equals("idPost") && f.getName().equals("rewin") && f.getName().equals("author") && f.getName().equals("curators")
						&& f.getName().equals("newCommentsBy") && f.getName().equals("newVotes") && f.getName().equals("iterations"));
			}
			
			@Override
			public boolean shouldSkipClass(Class<?> arg0) {
				return false;
			}
		}).create();
		
		Post p = getPostById(idPost);
			
		return gson.toJson(p);
	}
	
	/**
	 * @param idPost Id of post to get from database. Cannot be null.
	 * @return The post specified by parameter idPost.
	 */
	public Post getPostById(int idPost) {
		Objects.requireNonNull(idPost, "Id used to get the specified post is null");
		
		if(Objects.requireNonNull(postToBeBackedup) != null && Objects.requireNonNull(postToBeBackedup.keySet()) != null
				&& Objects.requireNonNull(postBackedup) != null && Objects.requireNonNull(postBackedup.keySet()) != null) {
			
			if(postToBeBackedup.containsKey(idPost)) {
				return postToBeBackedup.get(idPost);
			}else if(postBackedup.containsKey(idPost)) {
				return postBackedup.get(idPost);
			}
			
			return null;
		}
		
		return null;
	}
	
	/**
	 * Method used to check if the user specified by username is the author of the post specified by idPost.
	 * @param idPost Id of post. Cannot be null.
	 * @param username Username of user. Cannot be null.
	 * @return true if the user is the author of post, false otherwise.
	 */
	public boolean isPostAuthor(int idPost, String username) {
		Objects.requireNonNull(idPost, "Id used to get the specified post is null");
		Objects.requireNonNull(username, "Username used to get check if the user is the author is null");
		
		Post p = getPostById(idPost);
		
		return p.getAuthor().equals(username);
		
	}
	
	/**
	 * Method used to check if the post specified by idPost is in user's feed specified by username.
	 * @param idPost Id of post. Cannot be null.
	 * @param username Username of user. Cannot be null.
	 * @return true if the post is in user's feed, false otherwise.
	 */
	public boolean isPostInFeed(int idPost, String username) {
		Objects.requireNonNull(idPost, "Id used to get the specified post is null");
		Objects.requireNonNull(username, "Username used to get user's feed is null");
		
		if(Objects.requireNonNull(blogUser) != null && Objects.requireNonNull(blogUser.keySet()) != null
				&& Objects.requireNonNull(userFollowing) != null && Objects.requireNonNull(userFollowing.keySet()) != null) {
			
			ArrayList<String> following = userFollowing.get(username);
			ArrayList<Post> feed = new ArrayList<>();
			
			for(String s : following) {
				ArrayList<Post> postUser = blogUser.get(s);
				feed.addAll(postUser);
			}
			
			Post p = getPostById(idPost);
			
			return feed.contains(p);
		}
	
		return false;
	}
	
	/**
	 * Method used to check if the post specified by idPost existed in Winsome.
	 * @param idPost Id of post. Cannot be null.
	 * @return true if the post existed, false otherwise.
	 */
	public boolean isPostNotNull(int idPost) {
		Objects.requireNonNull(idPost, "Id used to get the specified post is null");
		
		return getPostById(idPost) != null ? true : false;
	}
	
	/**
	 * Remove post specified by idPost from Winsome. When the post is removed all its votes and comments are deleted.
	 * This method used a flag to see if the post is deleted since the last backup.
	 * @param idPost Id of post. Cannot be null.
	 */
	public void removePostFromWinsome(int idPost) {
		Objects.requireNonNull(idPost, "Id used to get the specifies post is null");
		
		if(Objects.requireNonNull(postToBeBackedup) != null && Objects.requireNonNull(postToBeBackedup.keySet()) != null
				&& Objects.requireNonNull(blogUser) != null && Objects.requireNonNull(blogUser.keySet()) != null
				&& Objects.requireNonNull(postBackedup) != null && Objects.requireNonNull(postBackedup.keySet()) != null) {
			
			Post p = getPostById(idPost);
			
			String authorPost = p.getAuthor();
			
			p.removeAllComment();
			p.removeAllVotes();
			p.removeAllRewin();
			
			blogUser.get(authorPost).remove(p);
			
			if(postToBeBackedup.containsKey(idPost)) {
				postToBeBackedup.remove(idPost, p);
			}else if(postBackedup.containsKey(idPost)) {
				postBackedup.remove(idPost, p);
			}
			
			for(String s : postRewinnedByUser.keySet()) {
				ConcurrentLinkedQueue<Post> postRewinned = postRewinnedByUser.get(s);
				if(postRewinned.contains(p)) {
					postRewinned.remove(p);
					blogUser.get(s).remove(p);
				}
				else continue;
			}
			
			postDeletedSinceLastBackup = true;
		}
	}

	/**
	 * Add a vote to a specified post in Winsome. If it is a positive votes it will be used to calculate the gains of the user,
	 * othrewise it will not be considered.
	 * @param idPost Id of post to add vote. Cannot be null.
	 * @param vote Positive or negative vote, can be only 1 or -1. Cannot be null.
	 * @param authorVote Author of vote. Cannot be null.
	 */
	public void addVoteToPost(int idPost, int vote, String authorVote) {
		Objects.requireNonNull(idPost, "Id post is null");
		Objects.requireNonNull(vote, "New vote for post is null");
		Objects.requireNonNull(authorVote, "Author vote is null");
		
		if(Objects.requireNonNull(postToBeBackedup) != null && Objects.requireNonNull(postToBeBackedup.keySet()) != null
				&& Objects.requireNonNull(blogUser) != null && Objects.requireNonNull(blogUser.keySet()) != null
				&& Objects.requireNonNull(postBackedup) != null && Objects.requireNonNull(postBackedup.keySet()) != null) {
			 
			Vote v = new Vote(idPost, authorVote, vote);
			
			Post p = getPostById(idPost);
			p.addVote(v);
			
			if(v.getVote() == 1) {
				p.getCurators().add(authorVote);
				int numVotes = p.getNewVotes();
				numVotes++;
				p.setNewVotes(numVotes);
			}else {
				int numVotes = p.getNewVotes();
				numVotes--;
				p.setNewVotes(numVotes);
			}
			
			ArrayList<Post> posts = blogUser.get(p.getAuthor());
			
			for(Post post : posts) {
				if(post.getIdPost() == idPost) {
					post.addVote(v);
					
					if(v.getVote() == 1) {
						post.getCurators().add(authorVote);
						int numVotes = post.getNewVotes();
						numVotes++;
						post.setNewVotes(numVotes);
					}else {
						int numVotes = post.getNewVotes();
						numVotes--;
						post.setNewVotes(numVotes);
					}
					break;
				}else continue;
			}
			
			return;
		}
	}
	
	/**
	 * Method used to check if the post specified by idPost is already voted by user specified by authorVote.
	 * @param idPost Id of post. Cannot be null.
	 * @param authorVote Author of vote. Cannot be null.
	 * @return true if the post is already voted by user, false otherwise.
	 */
	public boolean isPostAlreadyVotedByUser(int idPost, String authorVote) {
		Objects.requireNonNull(idPost, "Id post is null");
		Objects.requireNonNull(authorVote, "Author vote is null");
		
		if(Objects.requireNonNull(postToBeBackedup) != null && Objects.requireNonNull(postToBeBackedup.keySet()) != null
				&& Objects.requireNonNull(postBackedup) != null && Objects.requireNonNull(postBackedup.keySet()) != null) {
			Post p = getPostById(idPost);
			LinkedHashSet<Vote> votes = p.getVotes();
			
			for(Vote v : votes) {
				if(v.getAuthorVote().equals(authorVote))
					return true;
				else continue;
			}
			
			return false;
		}
		
		return false;
	}
	
	/**
	 * Add a comment to a certain post specified by idPost in Winsome. 
	 * The new comments added to a post will be used to calculate gains of the user.
	 * @param idPost Id of post. Cannot be null.
	 * @param comment Content of comment. Cannot be null.
	 * @param authorComment Author of comment. Cannot be null.
	 */
	public void addCommentToPost(int idPost, String comment, String authorComment) {
		Objects.requireNonNull(idPost, "Id post is null");
		Objects.requireNonNull(comment, "New comment for post is null");
		Objects.requireNonNull(authorComment, "Author comment is null");
		
		Comment c = new Comment(idPost, authorComment, comment);
		
		if(Objects.requireNonNull(postToBeBackedup) != null && Objects.requireNonNull(postToBeBackedup.keySet()) != null
				&& Objects.requireNonNull(blogUser) != null && Objects.requireNonNull(blogUser.keySet()) != null
				&& Objects.requireNonNull(postBackedup) != null && Objects.requireNonNull(postBackedup.keySet()) != null) {
			
			Post p = getPostById(idPost);
			p.addComment(c);
			p.getCurators().add(authorComment);
			p.incrementNumUserComments(authorComment);
			
			ArrayList<Post> posts = blogUser.get(p.getAuthor());
			
			for(Post post : posts) {
				if(post.getIdPost() == idPost) {
					post.addComment(c);
					post.getCurators().add(authorComment);
					post.incrementNumUserComments(authorComment);
					break;
				}else continue;
			}
			
			return;
		}
	}
	
	/**
	 * Rewin a certain post in Winsome specified by idPost. The post must be in user's feed and this post
	 * is visible to all the following user of the author of rewin in their feed. The post rewinned is visible also in author rewin blog. 
	 * @param idPost Id of post. Cannot be null.
	 * @param authorRewin User that do the rewin. Cannot be null.
	 */
	public void addRewinToPost(int idPost, String authorRewin) {
		Objects.requireNonNull(idPost, "Id to get the specified post is null");
		Objects.requireNonNull(authorRewin, "Author rewin is null");
		
		if(Objects.requireNonNull(blogUser) != null && Objects.requireNonNull(blogUser.keySet()) != null
				&& Objects.requireNonNull(postRewinnedByUser) != null && Objects.requireNonNull(postRewinnedByUser.keySet()) != null
				&& Objects.requireNonNull(userFollower) != null && Objects.requireNonNull(userFollower.keySet()) != null) {
			
			Post p = getPostById(idPost);
			
			p.getRewin().add(authorRewin);
			
			ArrayList<Post> posts = blogUser.get(p.getAuthor());
			
			for(Post post : posts) {
				if(post.getIdPost() == idPost) {
					post.addRewin(authorRewin);
					break;
				}else continue;
			}
			
			if(postRewinnedByUser.get(authorRewin) == null) {
				postRewinnedByUser.putIfAbsent(authorRewin, new ConcurrentLinkedQueue<Post>());
				ConcurrentLinkedQueue<Post> postRewinned = postRewinnedByUser.get(authorRewin);
				postRewinned.add(p);
			}else {
				ConcurrentLinkedQueue<Post> postRewinned = postRewinnedByUser.get(authorRewin);
				postRewinned.add(p);
			}
			
			blogUser.get(authorRewin).add(p);
		}
	}
	
	/**
	* Handles the computation of the rewards to be handed out.	 
	* @return Map of GainAndCurators where the key is the username of user
	* and the value is an object of GainAndCurators class.
	*/
	public ConcurrentHashMap<String, GainAndCurators> calculateGains(){
		ConcurrentHashMap<String, GainAndCurators> map = new ConcurrentHashMap<String, GainAndCurators>();
		
		if(Objects.requireNonNull(postBackedup) != null && Objects.requireNonNull(postBackedup.keySet()) != null) {
			for(Post p : postBackedup.values()) 
				map.putIfAbsent(p.getAuthor(), p.getGainAndCurators());
			
			return map;
		}
		
		return map;
	}
	
	/**
	 * Update the rewards of users by using a map of gains, where the key is the username of user and the value is an object of GainAndCurators class.
	 * For the calculation of the rewards will be used two percentage, one for author and one for curators.
	 * @param gains Map of gains for all the users registered in Winsome. Cannot be null.
	 * @param authorPercentage Author percentage used to calculate the gains.
	 * @throws IllegalArgumentException Only when the author percentage is not belongs 0 and 100.
	 * @throws ClientNotRegisteredException Only when the client is not registered in Winsome.
	 * @throws InvalidAmountException Only when the databse try to create a Transaction object with a negative amount. 
	 */
	public void updateRewards(ConcurrentHashMap<String, GainAndCurators> gains, double authorPercentage) throws IllegalArgumentException, ClientNotRegisteredException, InvalidAmountException{
		if(Objects.requireNonNull(gains, "Map of gains is null") != null) {
			if(authorPercentage <= 0 || authorPercentage >= 100) throw new IllegalArgumentException("Author percentage is not valid");
			
			User u = null;
			Transaction t = null;
			
			for(Entry<String, GainAndCurators> entry : gains.entrySet()) {
				String username = entry.getKey();
				double gain = entry.getValue().gain;
				Set<String> curators = entry.getValue().getCurators();
				
				if(gain == 0) continue;
				if((u = getUserByUsername(username)) == null) throw new ClientNotRegisteredException("Client with username " + username + " doesn't exists");
				
				t = new Transaction((gain * authorPercentage) / 100);
				u.addTransaction(t);
				
				for(String s : curators) {
					if((u = getUserByUsername(s)) == null) throw new ClientNotRegisteredException("Client with username " + username + " doesn't exists"); 
					t = new Transaction((gain * (100 - authorPercentage)) / (100 * curators.size())); 
					u.addTransaction(t);
				}
			}
		}
	}
	
	/**
	 * Method used to send to user his history of transactions and the total amount. 
	 * @param username Username of user. Cannot be null.
	 * @return A String that contains all the transactions and the total amount.
	 */
	public String getWalletUserJson(String username) {
		Objects.requireNonNull(username, "Username to get the specified user is null");
		
		Gson gson = new GsonBuilder().create();
		
		User u = getUserByUsername(username);
		ArrayList<Transaction> transactions = u.getTransactions();
		
		if(Objects.requireNonNull(transactions) != null) {
			Iterator<Transaction> it = transactions.iterator();
			
			StringBuilder serializedTransactions = new StringBuilder();
			double totalAmount = 0;
			
			serializedTransactions.append("[");
			while(it.hasNext()) {
				Transaction t = it.next();
				serializedTransactions.append(gson.toJson(t));
				totalAmount += t.getAmount();
				if(it.hasNext())
					serializedTransactions.append(", ");
			}
			serializedTransactions.append("]");
			
			serializedTransactions.append("@").append(totalAmount);
			
			return serializedTransactions.toString();
		}
		
		return "[]@0";
	}
	
	/**
	 * Method used to sent to user his total amount in his wallet but converted in Bitcoin.
	 * The server communicates with the site RANDOM.org to take random exchange rate.
	 * @param username Username of user.
	 * @return A String that represents his total amount in his wallet but converted in Bitcoin.
	 * @throws MalformedURLException Only when the URL used to communicates with RANDOM.org site is not correct.
	 * @throws IOException Only when occurs I/O error.
	 */
	public String getWalletUserInBitcoin(String username) throws MalformedURLException, IOException {
		 Objects.requireNonNull(username, "Username to get the user is null");
	
		 String randomGenUrl = "https://www.random.org/decimal-fractions/?num=1&dec=10&col=1&format=plain&rnd=new";
		 double exchangeRate = -1;
		 User u = getUserByUsername(username); 
		 
		 try(InputStream inputStream = new URL(randomGenUrl).openStream();
		     InputStreamReader isr = new InputStreamReader(inputStream);
			 BufferedReader readerInput = new BufferedReader(isr)){
		
			 exchangeRate = Double.parseDouble(readerInput.readLine());
		 }
		 return Double.toString(u.getTransactions().stream().mapToDouble(t -> t.getAmount()).sum() * exchangeRate);
	}	
	
	/**
	 * Take all the files used to stores users' data and load them into the various database's collection. 
	 * The data relate: user's data for registration, user's following and user's transactions.
	 * @param usersFile File of user's data for registration. Cannot be null.
	 * @param followingFile File of user's following data. Cannot be null.
	 * @param transactionsFile File of user's transactions data. Cannot be null.
	 * @throws IOException Only when occurs I/O error.
	 * @throws IllegalFileException Only when, at restart of server, some files are empty.
	 * @throws FileNotFoundException Only when a path of file doesn't exists.
	 */
	public void loadUsersFromJsonFile(File usersFile, File followingFile, File transactionsFile) throws IOException, IllegalFileException, FileNotFoundException {
		Objects.requireNonNull(usersFile, "Users file is null");
		Objects.requireNonNull(followingFile, "Following file is null");
		Objects.requireNonNull(transactionsFile, "Transactions file is null");
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		Map<String, User> parsedUsers = new HashMap<String, User>();
		
		this.firstBackupForUsers = true;
	
		if(Files.exists(usersFile.toPath(), LinkOption.NOFOLLOW_LINKS) && Files.exists(followingFile.toPath(), LinkOption.NOFOLLOW_LINKS) && Files.exists(transactionsFile.toPath(), LinkOption.NOFOLLOW_LINKS)) {
			
			if(usersFile.length() == 0 || followingFile.length() == 0 || transactionsFile.length() == 0) {
				throw new IllegalFileException("Files for upload users data exists, but some of them are empty. Impossible to do upload of data");
			}else {
				try(FileInputStream fis = new FileInputStream(usersFile); JsonReader reader = new JsonReader(new InputStreamReader(fis))){
					reader.setLenient(true);
					reader.beginArray();
				
					while(reader.hasNext()) {
						reader.beginObject();
						String nameAttribute = null;
						String username = null;
						String password = null;
						ArrayList<String> tags = null;
						
						while(reader.hasNext()) {
							nameAttribute = reader.nextName();
							switch(nameAttribute) {
								case "username": {
									username = reader.nextString();
									break;
								}
								case "password": {
									password = reader.nextString();
									break;
								}
								case "tagList": {
									reader.beginArray();
									tags = new ArrayList<>();
									while(reader.hasNext()) {
										tags.add(reader.nextString());
									}
									reader.endArray();
									break;
								}
							}
						}
						reader.endObject();
						parsedUsers.putIfAbsent(username, new User(null, null, new ArrayList<>()));
						parsedUsers.get(username).setUsername(username);
						parsedUsers.get(username).setPassword(password);
						parsedUsers.get(username).setTagList(tags);
					}
					reader.endArray();
				}
				
				try(FileInputStream fis = new FileInputStream(followingFile); JsonReader reader = new JsonReader(new InputStreamReader(fis))){
					reader.setLenient(true);
					reader.beginArray();
					
					while(reader.hasNext()) {
						reader.beginObject();
						String nameAttribute = null;
						String username = null;
						ArrayList<String> following = new ArrayList<>();
						
						while(reader.hasNext()) {
							nameAttribute = reader.nextName();
							switch (nameAttribute) {
								case "username": {
									username = reader.nextString();
									break;
								}
								case "following": {
									reader.beginArray();
									while(reader.hasNext()) {
										following.add(reader.nextString());
									}
									reader.endArray();
								}
							}
							for(String s : following) {
								parsedUsers.get(username).addFollowing(s);
							}
						}
						reader.endObject();
					}
					reader.endArray();
				}
				
				try(FileInputStream fis = new FileInputStream(transactionsFile); JsonReader reader = new JsonReader(new InputStreamReader(fis))){
					reader.setLenient(true);
					reader.beginArray();
					
					while(reader.hasNext()) {
						reader.beginObject();
						String nameAttribute;
						String username = null;
						JsonObject obj = new JsonObject();
						while(reader.hasNext()) {
							nameAttribute = reader.nextName();
							boolean flag = false;
							switch(nameAttribute) {
								case "username": {
									username = reader.nextString();
									break;
								}
								case "transactions": {
									reader.beginArray();
									while(reader.hasNext()) {
										flag = true;
										reader.beginObject();
										while(reader.hasNext()) {
											nameAttribute = reader.nextName();
											if(nameAttribute.equals("amount")) obj.addProperty(nameAttribute, reader.nextDouble());
											else if(nameAttribute.equals("timestamp")) obj.addProperty(nameAttribute, reader.nextString());
										}
										reader.endObject();
										if(flag) {
											parsedUsers.get(username).addTransaction(gson.fromJson(obj, Transaction.class));
										}
									}
									reader.endArray();
								}
							}
						}
						reader.endObject();
					}
					reader.endArray();
				}
				
				for(Entry<String, User> entry : parsedUsers.entrySet()) {
					User u = entry.getValue();
					this.userBackedUp.putIfAbsent(entry.getKey(), u);
					this.userFollower.putIfAbsent(entry.getKey(), new ArrayList<String>());
					this.userFollowing.putIfAbsent(entry.getKey(), new ArrayList<String>());
					this.blogUser.putIfAbsent(entry.getKey(), new ArrayList<Post>());
					this.postRewinnedByUser.putIfAbsent(entry.getKey(), new ConcurrentLinkedQueue<Post>());
				}
				
				for(User u : userBackedUp.values()) {
					ArrayList<String> followingUser = u.getFollowing();
					for(String username : followingUser) {
						this.userFollowing.get(u.getUsername()).add(username);
						this.userFollower.get(username).add(u.getUsername());
					}
				}
			}
		}else {usersFile.createNewFile(); followingFile.createNewFile(); transactionsFile.createNewFile();}
	}
	
	/**
	 * Backup data about users in three files: file of user's data for registration, 
	 * file of user's following, file of user's transactions. 
	 * @param usersFile File of user's data for registration. Cannot be null.
	 * @param followingFile File of user's following data. Cannot be null.
	 * @param transactionsFile File of user's transactions data. Cannot be null.
	 * @throws IOException Only when occurs I/O error.
	 */
	public void  backupUsers(File usersFile, File followingFile, File transactionsFile) throws IOException {
		Objects.requireNonNull(usersFile, "Users file is null");
		Objects.requireNonNull(followingFile, "Following file is null");
		Objects.requireNonNull(transactionsFile, "Transactions file is null");
		
		backupCached(new ExclusionStrategy() {

			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				return (f.getDeclaringClass() == User.class && !f.getName().equals("username") && !f.getName().equals("password") && !f.getName().equals("tagList"));
			}

			@Override
			public boolean shouldSkipClass(Class<?> arg0) {

				return false;
			}
		}, usersFile, userBackedUp, userToBeBackedup, firstBackupForUsers);
		firstBackupForUsers = false;
		userBackedUp.putAll(userToBeBackedup);
		userToBeBackedup = new ConcurrentHashMap<String, User>();

		backupNonCached(new ExclusionStrategy() {

			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				return (f.getDeclaringClass() == User.class && !f.getName().equals("username") && !f.getName().equals("following"));
			}

			@Override
			public boolean shouldSkipClass(Class<?> arg0) {
				return false;
			}
		}, followingFile, userBackedUp);

		backupNonCached(new ExclusionStrategy() {

			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				return (f.getDeclaringClass() == User.class && !f.getName().equals("username") && !f.getName().equals("transactions"));
			}

			@Override
			public boolean shouldSkipClass(Class<?> arg0) {
				return false;
			}
		}, transactionsFile, userBackedUp);

		
	} 
	
	/**
	 * Take all the files used to stores posts' data and load them into the various database's collection. 
	 * The data relate: posts' immutable data (idPost, author, content, title), posts' votes, posts' comments and posts' mutable data used to calculate gains for users.
	 * @param postsFile File of posts' immutable data. Cannot be null.
	 * @param votesFile File of posts' votes. Cannot be null.
	 * @param commentsFile File of posts' comments. Cannot be null.
	 * @param mutableDataPostsFile File of posts' mutable data. Cannot be null.
	 * @throws IOException Only when occurs I/O error.
	 * @throws IllegalFileException Only when, at restart of server, some files are empty.
	 * @throws FileNotFoundException Only when a file path doesn't exists.
	 */
	public void loadPostsFromJsonFile(File postsFile, File votesFile, File commentsFile, File mutableDataPostsFile) throws IOException, IllegalFileException, FileNotFoundException {
		Objects.requireNonNull(postsFile, "Posts file is null");
		Objects.requireNonNull(votesFile, "Votes file is null");
		Objects.requireNonNull(commentsFile, "Comments file is null");
		Objects.requireNonNull(mutableDataPostsFile, "Mutable data posts file is null");
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		Map<Integer, JsonObject> parsedPosts = new HashMap<>();
	
		if(Files.exists(postsFile.toPath(), LinkOption.NOFOLLOW_LINKS) && Files.exists(votesFile.toPath(), LinkOption.NOFOLLOW_LINKS)
		   && Files.exists(commentsFile.toPath(), LinkOption.NOFOLLOW_LINKS) && Files.exists(mutableDataPostsFile.toPath(), LinkOption.NOFOLLOW_LINKS)) {
			
			if(postsFile.length() == 0 || votesFile.length() == 0 || commentsFile.length() == 0 || mutableDataPostsFile.length() == 0) {
				throw new IllegalFileException("Files for upload posts data exists, but some of them are empty. Impossible to do upload of data");
			}
			
			try(FileInputStream fis = new FileInputStream(postsFile); JsonReader reader = new JsonReader(new InputStreamReader(fis))){
				reader.setLenient(true);
				reader.beginArray();
				
				while(reader.hasNext()) {
					reader.beginObject();
					String nameAttribute = null;
					int id = -1;
					String author = null;
					String title = null;
					String content = null;
					while(reader.hasNext()) {
						nameAttribute = reader.nextName();
						
						switch(nameAttribute) {
							case "idPost": {
								id = reader.nextInt();
								break;
							}
							case "author":{
								author = reader.nextString();
								break;
							}
							case "title": {
								title = reader.nextString();
								break;
							}
							case "content": {
								content = reader.nextString();
								break;
							}
						}
					}
					reader.endObject();
					parsedPosts.putIfAbsent(id, new JsonObject());
					parsedPosts.get(id).addProperty("idPost", id);
					parsedPosts.get(id).addProperty("title", title);
					parsedPosts.get(id).addProperty("content", content);
					parsedPosts.get(id).addProperty("author", author);
				}
				reader.endArray();
			}
			
			try(FileInputStream fis = new FileInputStream(votesFile); JsonReader reader = new JsonReader(new InputStreamReader(fis))){
				reader.setLenient(true);
				reader.beginArray();
				
				while(reader.hasNext()) {
					reader.beginObject();
					String nameAttribute = null;
					int id = -1;
					JsonArray votes = new JsonArray();
					while(reader.hasNext()) {
						nameAttribute = reader.nextName();
						switch(nameAttribute) {
							case "idPost": {
								id = reader.nextInt();
								break;
							}
							case "votes": {
								reader.beginArray();
								while(reader.hasNext()) {
									reader.beginObject();
									JsonObject obj = new JsonObject();
									while(reader.hasNext()) {
										nameAttribute = reader.nextName();
										if(nameAttribute.equals("idPost")) obj.addProperty(nameAttribute, reader.nextInt());
										else if(nameAttribute.equals("authorVote")) obj.addProperty(nameAttribute, reader.nextString());
										else if(nameAttribute.equals("vote")) obj.addProperty(nameAttribute, reader.nextInt());
									}
									reader.endObject();
									votes.add(obj);
								}
								reader.endArray();
								break;
							}
						}
					}
					reader.endObject();
					parsedPosts.get(id).add("votes", votes);
				}
				reader.endArray();
			}
			
			try(FileInputStream fis = new FileInputStream(commentsFile); JsonReader reader = new JsonReader(new InputStreamReader(fis))){
				reader.setLenient(true);
				reader.beginArray();
				
				while(reader.hasNext()) {
					reader.beginObject();
					String nameAttribute = null;
					int id = -1;
					JsonArray comments = new JsonArray();
					while(reader.hasNext()) {
						nameAttribute = reader.nextName();
						switch(nameAttribute) {
							case "idPost": {
								id = reader.nextInt();
								break;
							}
							case "comments": {
								reader.beginArray();
								while(reader.hasNext()) {
									reader.beginObject();
									JsonObject obj = new JsonObject();
									while(reader.hasNext()) {
										nameAttribute = reader.nextName();
										if(nameAttribute.equals("idPost")) obj.addProperty(nameAttribute, reader.nextInt());
										else if(nameAttribute.equals("author")) obj.addProperty(nameAttribute, reader.nextString());
										else if(nameAttribute.equals("content")) obj.addProperty(nameAttribute, reader.nextString());
									}
									reader.endObject();
									comments.add(obj);
								}
								reader.endArray();
							}
						}
					}
					reader.endObject();
					parsedPosts.get(id).add("comments", comments);
				}
				reader.endArray();
			}
			
			try(FileInputStream fis = new FileInputStream(mutableDataPostsFile); JsonReader reader = new JsonReader(new InputStreamReader(fis))){
				reader.setLenient(true);
				reader.beginArray();
				
				while(reader.hasNext()) {
					reader.beginObject();
					String nameAttribute = null;
					int id = -1;
					int newVotes = -1;
					int iterations = -1;
					JsonObject newCommentsBy = new JsonObject();
					JsonArray curators = new JsonArray();
					JsonArray rewinners = new JsonArray();
					while(reader.hasNext()) {
						nameAttribute = reader.nextName();
						switch(nameAttribute) {
							case "idPost": {
								id = reader.nextInt();
								break;
							}
							case "rewin": {
								reader.beginArray();
								while(reader.hasNext()) {
									rewinners.add(reader.nextString());
								}
								reader.endArray();
								break;
							}
							case "iterations": {
								iterations = reader.nextInt();
								break;
							}
							case "newVotes": {
								newVotes = reader.nextInt();
								break;
							}
							case "newCommentsBy": {
								reader.beginObject();
								while(reader.hasNext()) {
									newCommentsBy.addProperty(reader.nextName(), reader.nextInt());
								}
								reader.endObject();
								break;
							}
							case "curators": {
								reader.beginArray();
								while(reader.hasNext()) {
									curators.add(reader.nextString());
								}
								reader.endArray();
								break;
							}
						}
					}
					reader.endObject();
					parsedPosts.get(id).add("rewin", rewinners);
					parsedPosts.get(id).add("newCommentsBy", newCommentsBy);
					parsedPosts.get(id).add("curators", curators);
					parsedPosts.get(id).addProperty("iterations", iterations);
					parsedPosts.get(id).addProperty("newVotes", newVotes);
				}
				reader.endArray();
			}
			
			for(Entry<Integer, JsonObject> entry : parsedPosts.entrySet()) {
				Post p = gson.fromJson(entry.getValue(), Post.class);
				this.postBackedup.putIfAbsent(entry.getKey(), p);
				this.blogUser.get(p.getAuthor()).add(p);
				this.idPost.set(entry.getKey());
				
				for(String s : p.getRewin()) {
					this.postRewinnedByUser.get(s).add(p);
				}
			}
			
			this.idPost.getAndIncrement();
		}else {postsFile.createNewFile(); votesFile.createNewFile(); commentsFile.createNewFile(); mutableDataPostsFile.createNewFile();}
	
		return;
	}
	
	/**
	 * Backup data about posts in four files: file of posts' immutable data, file of posts' votes, file of posts' comments,
	 * file of posts' mutable data.
	 * @param postsFile File of posts' immutable data. Cannot be null.
	 * @param votesFile File of posts' votes. Cannot be null.
	 * @param commentsFile File of posts' comments. Cannot be null.
	 * @param mutableDataPostsFile File of posts' mutable data. Cannot be null.
	 * @throws FileNotFoundException Only when a path of file doesn't exists.
	 * @throws NullPointerException Only when some objects or variables is null.
	 * @throws IOException Only when occurs I/O error.
	 */
	public void backupPosts(File postsFile, File votesFile, File commentsFile, File mutableDataPostFile) throws FileNotFoundException, NullPointerException, IOException {
		Objects.requireNonNull(postsFile, "Posts file is null");
		Objects.requireNonNull(votesFile, "Votes file is null");
		Objects.requireNonNull(commentsFile, "Comments file is null");
		Objects.requireNonNull(mutableDataPostFile, "Mutable data posts file is null");
	
		ExclusionStrategy strategy = new ExclusionStrategy() {
			
			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				return (f.getDeclaringClass() == Post.class && !f.getName().equals("idPost") && !f.getName().equals("author")
						&& !f.getName().equals("title") && !f.getName().equals("content"));
			}
			
			@Override
			public boolean shouldSkipClass(Class<?> arg0) {
				return false;
			}
		};
		

		if(postDeletedSinceLastBackup) {
			postDeletedSinceLastBackup = false;
			backupNonCached(strategy, postsFile, postBackedup);
		}

		backupCached(strategy, postsFile, postBackedup, postToBeBackedup, postRecoveredFromBackup);
		postBackedup.putAll(postToBeBackedup);
		postToBeBackedup = new ConcurrentHashMap<Integer, Post>();
		postRecoveredFromBackup = false;

		backupNonCached(new ExclusionStrategy() {

			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				return (f.getDeclaringClass() == Post.class && !f.getName().equals("idPost") && !f.getName().equals("votes"));
			}

			@Override
			public boolean shouldSkipClass(Class<?> arg0) {
				return false;
			}
		}, votesFile, postBackedup);

		backupNonCached(new ExclusionStrategy() {

			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				return (f.getDeclaringClass() == Post.class && !f.getName().equals("idPost") && !f.getName().equals("comments"));
			}

			@Override
			public boolean shouldSkipClass(Class<?> arg0) {
				return false;
			}
		}, commentsFile, postBackedup);

		backupNonCached(new ExclusionStrategy() {

			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				return (f.getDeclaringClass() == Post.class && !f.getName().equals("idPost") && !f.getName().equals("iterations") 
						&& !f.getName().equals("rewin") && !f.getName().equals("newCommentsBy") 
						&& !f.getName().equals("curators") && !f.getName().equals("newVotes"));
			}

			@Override
			public boolean shouldSkipClass(Class<?> arg0) {
				return false;
			}
		}, mutableDataPostFile, postBackedup);


	}
}
