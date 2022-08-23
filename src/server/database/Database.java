package server.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
 * Class that contains all the data of Winsome (Users, Posts, Comments, Votes, Transactions Wallet, follower and following)
 * @author Gianmarco Petrocchi
 *
 */
public class Database extends Storage{

	private ConcurrentHashMap<String, User> userRegistered;

	/**Concurrent collection that contains user to be backuped in user's file
	 * <K, V>: K is a String that represents the username; V is an User object with the username specified in K */
	private ConcurrentHashMap<String, User> userToBeBackedup;
	
	private ConcurrentHashMap<String, User> userBackedUp;
	
	/** Concurrent colleciton that contains user logged in Winsome
	 * <K, V>: K is the client socket; V is a String that represents the logged user's username */
	private ConcurrentHashMap<Socket, String> userLoggedIn;
	
	private ConcurrentHashMap<String, ArrayList<String>> userFollowing;
	
	private ConcurrentHashMap<String, ArrayList<String>> userFollower;
	
	private ConcurrentHashMap<String, ArrayList<Post>> blogUser;
	
	private ConcurrentHashMap<Integer, Post> allPosts;
	
	private ConcurrentHashMap<Integer, Post> postToBeBackedup;
	
	private ConcurrentHashMap<Integer, Post> postBackedup;
	
	private ConcurrentHashMap<String, ArrayList<Post>> userFeed;
	
	private ConcurrentHashMap<String, ConcurrentLinkedQueue<Post>> postRewinnedByUser;
	
	private AtomicInteger idPost;
	
	private boolean postRecoveredFromBackup = false;
	
	private boolean firstBackupForUsers = false;
	
	private boolean postDeletedSinceLastBackup = false;
	
	private ReentrantReadWriteLock backupLock = new ReentrantReadWriteLock(true);
	
	/**
	 * Basic constructor for Database class
	 */
	public Database() {
		this.userRegistered = new ConcurrentHashMap<String, User>();
		this.userToBeBackedup = new ConcurrentHashMap<String, User>();
		this.userBackedUp = new ConcurrentHashMap<String, User>();
		this.userLoggedIn = new ConcurrentHashMap<Socket, String>();
		this.userFollowing = new ConcurrentHashMap<String, ArrayList<String>>();
		this.userFollower = new ConcurrentHashMap<String, ArrayList<String>>();
		this.blogUser = new ConcurrentHashMap<String, ArrayList<Post>>();
		this.allPosts = new ConcurrentHashMap<Integer, Post>();
		this.postToBeBackedup = new ConcurrentHashMap<Integer, Post>();
		this.postBackedup = new ConcurrentHashMap<Integer, Post>();
		this.userFeed = new ConcurrentHashMap<String, ArrayList<Post>>();
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
		
		if(Objects.requireNonNull(userRegistered) != null) {
			userRegistered.putIfAbsent(username, u);
			userToBeBackedup.putIfAbsent(username, u);
		}
	}
	
	/** 
	 * Check if the user is registered
	 * @param username User's username. Cannot be null
	 * @return true if the user is registerd, false otherwise
	 */
	public boolean isUserRegistered(String username) {
		Objects.requireNonNull(username, "Username used to check if the user is registered is null");
		
		if(Objects.requireNonNull(userRegistered) != null && Objects.requireNonNull(userRegistered.keySet()) != null) {
			return userRegistered.containsKey(username);
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
		
		if(Objects.requireNonNull(userRegistered) != null && Objects.requireNonNull(userRegistered.keySet()) != null) {
			User u = userRegistered.get(username);
			
			return u;
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
	
	public String getUsernameBySocket(Socket socket) {
		Objects.requireNonNull(socket, "Parameter socket use to get the logged user is null");
		
		if(Objects.requireNonNull(userLoggedIn) != null && Objects.requireNonNull(userLoggedIn.keySet()) != null) {
			return userLoggedIn.get(socket);
		}
		
		return null;
	}
	
	/**
	 * @return The ConcurrentHashMap of user that are already logged in
	 */
	public ConcurrentHashMap<Socket, String> getUserLoggedIn(){
		if(Objects.requireNonNull(userLoggedIn) != null && Objects.requireNonNull(userLoggedIn.keySet()) != null) {
			return userLoggedIn;
		}
		
		return null;
	}
	
	public void setFollowerListUser(String username) {
		Objects.requireNonNull(username, "Username used to set his follower list is null");
		
		if(Objects.requireNonNull(userFollower) != null) {
			userFollower.putIfAbsent(username, new ArrayList<>());
		}
	}
	
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
			
			addPostToUserFeedByFollower(usernameNewFollower, usernameUpdateFollower);
			
			return;
		}
	}

	public void removeFollower(String usernameUpdateFollower, String usernameToRemove) {
		Objects.requireNonNull(usernameUpdateFollower, "Username used to update his followers is null");
		Objects.requireNonNull(usernameToRemove, "Username to remove from followers is null");
		
		if(Objects.requireNonNull(userFollower) != null && Objects.requireNonNull(userFollower.keySet()) != null) {
			ArrayList<String> followers = userFollower.get(usernameUpdateFollower);
			
			followers.remove(usernameToRemove);
			
			removePostFromUserFeedByFollower(usernameToRemove, usernameUpdateFollower);
		}
	}

	public ArrayList<String> getFollowerListByUsername(String username) {
		Objects.requireNonNull(username, "Username used to get his follower list is null");
		
		if(Objects.requireNonNull(userFollower) != null && Objects.requireNonNull(userFollower.keySet()) != null) {
			return userFollower.get(username);
		}
		
		return null;
	}
	
	public void setFollowingListForUser(String username) {
		Objects.requireNonNull(username, "Username used to set his following list is null");
		
		if(Objects.requireNonNull(userFollowing) != null) {
			userFollowing.putIfAbsent(username, new ArrayList<>());	
		}
		
		return;
	}
	
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
	
	public ArrayList<String> getFollowingListByUsername(String username){
		Objects.requireNonNull(username, "Username used to get his following list is null");
		
		if(Objects.requireNonNull(userFollowing) != null && Objects.requireNonNull(userFollowing.keySet()) != null) {
			return userFollowing.get(username);
		}
		
		return null;
	}
	
	public String getRegisteredUsersJson(String username) {
		Objects.requireNonNull(username, "Username used to get the specified user is null");
		
		if(Objects.requireNonNull(userRegistered) != null && Objects.requireNonNull(userRegistered.keySet()) != null) {
			User user = userRegistered.get(username);
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
			
			for(String s : userRegistered.keySet()) {
				if(userRegistered.get(s).getUsername().equals(username)) continue;
				else registeredUsers.add(userRegistered.get(s));
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
				
			return serializationUsers.toString();
		}
		
		return "[]";
	}
	
	public synchronized int getAndIncrementIdPost() {
		return idPost.getAndIncrement();
	}
	
	public void setPostListForUser(String username) {
		Objects.requireNonNull(username, "Username used to set his posts list is null");
		
		if(Objects.requireNonNull(blogUser) != null) {
			blogUser.putIfAbsent(username, new ArrayList<Post>());
			
			return;	
		}
	}
	
	public String addPostInWinsome(int idPost, String authorPost, String titlePost, String contentPost) {
		Objects.requireNonNull(idPost,  "Id post is null");
		Objects.requireNonNull(authorPost, "Author post is null");
		Objects.requireNonNull(titlePost, "Title post is null");
		Objects.requireNonNull(contentPost, "Content post is null");
		
		Post newPost = new Post(idPost, titlePost, contentPost, authorPost);
	
		if(Objects.requireNonNull(blogUser) != null && Objects.requireNonNull(blogUser.keySet()) != null
				&& Objects.requireNonNull(allPosts) != null && Objects.requireNonNull(postToBeBackedup) != null) {

			blogUser.get(authorPost).add(newPost);
			allPosts.putIfAbsent(idPost, newPost);
			postToBeBackedup.putIfAbsent(idPost, newPost);

			ArrayList<String> authorFollower = userFollower.get(authorPost);

			for(String s : authorFollower) {
				addPostToUserFeedByAddingPost(s, newPost);
			}

			return "New post created with id: " + idPost;

		}
		
		return "Impossible to create post with id: " + idPost;
	}

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
	
	public void setUserFeed(String username) {
		Objects.requireNonNull(username, "Username used to set his feed is null");
		
		if(Objects.requireNonNull(userFeed) != null) {
			userFeed.putIfAbsent(username, new ArrayList<Post>());
			
			return;
		}
	}
	
	public String getUserFeedJson(String username) {
		Objects.requireNonNull(username, "Username used to get his feed is null");
		
		if(Objects.requireNonNull(userFeed) != null && Objects.requireNonNull(userFeed.keySet()) != null) {
			ArrayList<Post> feedPost = userFeed.get(username);
			
			Gson gson = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
				
				@Override
				public boolean shouldSkipField(FieldAttributes f) {
					return (f.getDeclaringClass() == Post.class && f.getName().equals("content") && f.getName().equals("rewin") && f.getName().equals("votes") && f.getName().equals("comments")
							&& f.getName().equals("iterations") && f.getName().equals("newCommentsBy") && f.getName().equals("newVotes") && f.getName().equals("curators"));
				}
				
				@Override
				public boolean shouldSkipClass(Class<?> arg0) {
					return false;
				}
			}).create();
			
			StringBuilder serializedPost = new StringBuilder();
			Iterator<Post> it = feedPost.iterator();
			
			serializedPost.append("[");
			while(it.hasNext()) {
				serializedPost.append(gson.toJson(it.next()));
				if(it.hasNext())
					serializedPost.append(", ");
			}
			serializedPost.append("]");
			
			return serializedPost.toString();
		}
		
		return "[]";
	}
	
	private void addPostToUserFeedByFollower(String usernameUpdateFeed, String usernameTakePost) {
		Objects.requireNonNull(usernameUpdateFeed, "Username used to update his feed is null");
		Objects.requireNonNull(usernameTakePost, "Username used to take post to add to a certain user's feed is null");
		
		if(Objects.requireNonNull(userFeed) != null && Objects.requireNonNull(userFeed.keySet()) != null
				&& Objects.requireNonNull(blogUser) != null && Objects.requireNonNull(blogUser.keySet()) != null) {
			ArrayList<Post> feed = userFeed.get(usernameUpdateFeed);
			
			if(feed == null) 
				feed = new ArrayList<Post>();
			
			ArrayList<Post> posts = blogUser.get(usernameTakePost);
			
			feed.addAll(posts);
			
			return;
		}
	}
	
	private void removePostFromUserFeedByFollower(String usernameUpdateFeed, String usernameTakePost) {
		Objects.requireNonNull(usernameUpdateFeed, "Username used to update his feed is null");
		Objects.requireNonNull(usernameTakePost, "Username used to remove post from user's feed is null");
		
		if(Objects.requireNonNull(userFeed) != null && Objects.requireNonNull(userFeed.keySet()) != null) {
			ArrayList<Post> feed = userFeed.get(usernameUpdateFeed);
			Iterator<Post> it = feed.iterator();
			
			while(it.hasNext()) {
				Post p = it.next();
				if(usernameTakePost.equals(p.getAuthor()))
					it.remove();
				else continue;
			}
			
			return;
		}
	}
	
	private void addPostToUserFeedByAddingPost(String usernameUpdateFeed, Post newPost) {
		Objects.requireNonNull(usernameUpdateFeed, "Username used to update his feed is null");
		Objects.requireNonNull(newPost, "The post that must be added to user's feed is null");
		
		if(Objects.requireNonNull(userFeed) != null && Objects.requireNonNull(userFeed.keySet()) != null) {
			ArrayList<Post> feed = userFeed.get(usernameUpdateFeed);
			
			feed.add(newPost);
			
			return;
		}
	}
	
	private void removePostFromUserFeedByDeletingPost(int idPost, String authorPost) {
		Objects.requireNonNull(idPost, "Id used to get the specified post is null");
		Objects.requireNonNull(authorPost, "Author post is null");
		
		if(Objects.requireNonNull(userFollower) != null && Objects.requireNonNull(userFollower.keySet()) != null) {
			ArrayList<String> followerAuthorPost = userFollower.get(authorPost);
			
			for(String s : followerAuthorPost) {
				ArrayList<Post> feedAuthorPostFollower = userFeed.get(s);
				Iterator<Post> it = feedAuthorPostFollower.iterator();
				while(it.hasNext()) {
					Post p = it.next();
					if(p.getIdPost() == idPost) {
						it.remove();
						break;
					}else continue;
				}
			}
			
			return;
		}
	}
	
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
	
	public Post getPostById(int idPost) {
		Objects.requireNonNull(idPost, "Id used to get the specified post is null");
		
		if(Objects.requireNonNull(allPosts) != null && Objects.requireNonNull(allPosts.keySet()) != null) {
			return allPosts.get(idPost);
		}
		
		return null;
	}
	
	public boolean isPostAuthor(int idPost, String username) {
		Objects.requireNonNull(idPost, "Id used to get the specified post is null");
		Objects.requireNonNull(username, "Username used to get check if the user is the author is null");
		
		Post p = getPostById(idPost);
		
		return p.getAuthor().equals(username);
		
	}
	
	public boolean isPostInFeed(int idPost, String username) {
		Objects.requireNonNull(idPost, "Id used to get the specified post is null");
		Objects.requireNonNull(username, "Username used to get user's feed is null");
		
		if(Objects.requireNonNull(userFeed) != null && Objects.requireNonNull(userFeed.keySet()) != null) {
			ArrayList<Post> feed = userFeed.get(username);
			Post p = getPostById(idPost);
			
			return feed.contains(p);
		}
	
		return false;
	}
	
	public boolean isPostNotNull(int idPost) {
		Objects.requireNonNull(idPost, "Id used to get the specified post is null");
		
		return getPostById(idPost) != null ? true : false;
	}
	
	public void removePostFromWinsome(int idPost) {
		Objects.requireNonNull(idPost, "Id used to get the specifies post is null");
		
		if(Objects.requireNonNull(allPosts) != null && Objects.requireNonNull(allPosts.keySet()) != null
				&& Objects.requireNonNull(blogUser) != null && Objects.requireNonNull(blogUser.keySet()) != null) {
			
			Post p = getPostById(idPost);
			
			String authorPost = p.getAuthor();
			
			p.removeAllComment();
			p.removeAllVotes();
			
			allPosts.remove(idPost, p);
			blogUser.remove(authorPost, p);
			
			removePostFromUserFeedByDeletingPost(idPost, authorPost);
		}
	}
	
	public void addVoteToPost(int idPost, int vote, String authorVote) {
		Objects.requireNonNull(idPost, "Id post is null");
		Objects.requireNonNull(vote, "New vote for post is null");
		Objects.requireNonNull(authorVote, "Author vote is null");
		
		if(Objects.requireNonNull(allPosts) != null && Objects.requireNonNull(allPosts.keySet()) != null
				&& Objects.requireNonNull(blogUser) != null && Objects.requireNonNull(blogUser.keySet()) != null) {
			
			Vote v = new Vote(idPost, authorVote, vote);
			
			Post p = allPosts.get(idPost);
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
	
	public boolean isPostAlreadyVotedByUser(int idPost, String authorVote) {
		Objects.requireNonNull(idPost, "Id post is null");
		Objects.requireNonNull(authorVote, "Author vote is null");
		
		if(Objects.requireNonNull(allPosts) != null && Objects.requireNonNull(allPosts.keySet()) != null) {
			Post p = allPosts.get(idPost);
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
	
	public void addCommentToPost(int idPost, String comment, String authorComment) {
		Objects.requireNonNull(idPost, "Id post is null");
		Objects.requireNonNull(comment, "New comment for post is null");
		Objects.requireNonNull(authorComment, "Author comment is null");
		
		Comment c = new Comment(idPost, authorComment, comment);
		
		if(Objects.requireNonNull(allPosts) != null && Objects.requireNonNull(allPosts.keySet()) != null
				&& Objects.requireNonNull(blogUser) != null && Objects.requireNonNull(blogUser.keySet()) != null) {
			
			Post p = allPosts.get(idPost);
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
			
			ArrayList<String> followerAuthorRewin = userFollower.get(authorRewin);
		
			for(String s : followerAuthorRewin) {
				addPostToFeedByRewin(s, p);
			}
		}
	}
	
	private void addPostToFeedByRewin(String usernameUpdateFeed, Post p) {
		Objects.requireNonNull(usernameUpdateFeed, "Author rewin is null");
		Objects.requireNonNull(p, "Post to add into feed is null");
		
		if(Objects.requireNonNull(userFeed) != null && Objects.requireNonNull(userFeed.keySet()) != null) {
			ArrayList<Post> feed = userFeed.get(usernameUpdateFeed);
			feed.add(p);
			
			return;
		}
	}
	
	public ConcurrentHashMap<String, GainAndCurators> calculateGains(){
		ConcurrentHashMap<String, GainAndCurators> map = new ConcurrentHashMap<String, GainAndCurators>();
		
		if(Objects.requireNonNull(allPosts) != null && Objects.requireNonNull(allPosts.keySet()) != null) {
			for(Post p : allPosts.values()) 
				map.putIfAbsent(p.getAuthor(), p.getGainAndCurators());
			
			return map;
		}
		
		return map;
	}
	
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
	
	public void loadUsersFromJsonFile(File usersFile, File followingFile, File transactionsFile) throws IOException, IllegalFileException, FileNotFoundException {
		Objects.requireNonNull(usersFile, "Users file is null");
		Objects.requireNonNull(followingFile, "Following file is null");
		Objects.requireNonNull(transactionsFile, "Transactions file is null");
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Type userType = new TypeToken<ConcurrentHashMap<String, User>>(){} .getType();
		
		Map<String, User> parsedUsers = new HashMap<String, User>();
		
		this.firstBackupForUsers = true;
	
		if(Files.exists(usersFile.toPath(), LinkOption.NOFOLLOW_LINKS) && Files.exists(followingFile.toPath(), LinkOption.NOFOLLOW_LINKS) && Files.exists(transactionsFile.toPath(), LinkOption.NOFOLLOW_LINKS)) {
			try(FileInputStream fis = new FileInputStream(usersFile); JsonReader reader = new JsonReader(new InputStreamReader(fis))){
				Map<String, User> dataInUsersFile = gson.fromJson(reader, userType);
				
				for(String s : dataInUsersFile.keySet()) {
					User u = dataInUsersFile.get(s);
					parsedUsers.putIfAbsent(u.getUsername(), new User(null, null, new ArrayList<String>()));
					parsedUsers.get(u.getUsername()).setUsername(u.getUsername());
					parsedUsers.get(u.getUsername()).setPassword(u.getPassword());
					parsedUsers.get(u.getUsername()).setTagList(u.getTagList());
				}
			}
			
			try(FileInputStream fis = new FileInputStream(followingFile); JsonReader reader = new JsonReader(new InputStreamReader(fis))){
				Map<String, User> dataInFollowingFile = gson.fromJson(reader, userType);

				for(String s : dataInFollowingFile.keySet()) {
					User u = dataInFollowingFile.get(s);
					ArrayList<String> followingUser = u.getFollowing();
					for(String follow : followingUser) {
						parsedUsers.get(u.getUsername()).addFollowing(follow);
					}
				}
			}
			
			try(FileInputStream fis = new FileInputStream(transactionsFile); JsonReader reader = new JsonReader(new InputStreamReader(fis))){
				Map<String, User> dataInTransactionsFile = gson.fromJson(reader, userType);

				for(String s : dataInTransactionsFile.keySet()) {
					User u = dataInTransactionsFile.get(s);
					ArrayList<Transaction> transactionsUser = u.getTransactions();
					for(Transaction t : transactionsUser) {
						parsedUsers.get(u.getUsername()).addTransaction(t);
					}
				}
			}
			
			for(Entry<String, User> entry : parsedUsers.entrySet()) {
				User u = entry.getValue();
				this.userRegistered.putIfAbsent(entry.getKey(), u);
				this.userFollower.putIfAbsent(entry.getKey(), new ArrayList<String>());
				this.userFollowing.putIfAbsent(entry.getKey(), new ArrayList<String>());
				this.userFeed.putIfAbsent(entry.getKey(), new ArrayList<Post>());
				this.blogUser.putIfAbsent(entry.getKey(), new ArrayList<Post>());
				this.postRewinnedByUser.putIfAbsent(entry.getKey(), new ConcurrentLinkedQueue<Post>());
			}
			
			for(User u : userRegistered.values()) {
				ArrayList<String> followingUser = u.getFollowing();
				for(String username : followingUser) {
					this.userFollowing.get(u.getUsername()).add(username);
					this.userFollower.get(username).add(u.getUsername());
				}
			}
			
			System.out.println(this.userRegistered);
		}else {usersFile.createNewFile(); followingFile.createNewFile(); transactionsFile.createNewFile();}
	}
	
	public void  backupUsers(File usersFile, File followingFile, File transactionsFile) throws IOException {
		Objects.requireNonNull(usersFile, "Users file is null");
		Objects.requireNonNull(followingFile, "Following file is null");
		Objects.requireNonNull(transactionsFile, "Transactions file is null");
		
		try {
			backupLock.writeLock().lock();
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
			userBackedUp = new ConcurrentHashMap<String, User>();
			
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
		}finally {backupLock.writeLock().unlock();}
		
	} 
	
	public void loadPostsFromJsonFile(File postsFile, File votesFile, File commentsFile, File mutableDataPostsFile) throws IOException, IllegalFileException, FileNotFoundException {
		Objects.requireNonNull(postsFile, "Posts file is null");
		Objects.requireNonNull(votesFile, "Votes file is null");
		Objects.requireNonNull(commentsFile, "Comments file is null");
		Objects.requireNonNull(mutableDataPostsFile, "Mutable data posts file is null");
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Type postType = new TypeToken<Map<Integer, Post>>(){} .getType();
		
		Map<Integer, Post> parsedPosts = new HashMap<>();
	
		if(Files.exists(postsFile.toPath(), LinkOption.NOFOLLOW_LINKS) && Files.exists(votesFile.toPath(), LinkOption.NOFOLLOW_LINKS)
		   && Files.exists(commentsFile.toPath(), LinkOption.NOFOLLOW_LINKS) && Files.exists(mutableDataPostsFile.toPath(), LinkOption.NOFOLLOW_LINKS)) {
			
			try(FileInputStream fis = new FileInputStream(postsFile); JsonReader reader = new JsonReader(new InputStreamReader(fis))){
				Map<Integer, Post> dataInPostsFile = gson.fromJson(reader, postType);
				
				for(Integer i : dataInPostsFile.keySet()) {
					Post p = dataInPostsFile.get(i);
					parsedPosts.putIfAbsent(p.getIdPost(), new Post(0, null, null, null));
					parsedPosts.get(p.getIdPost()).setIdPost(p.getIdPost());
					parsedPosts.get(p.getIdPost()).setAuthor(p.getAuthor());
					parsedPosts.get(p.getIdPost()).setTitle(p.getTitle());
					parsedPosts.get(p.getIdPost()).setContent(p.getContent());
				}
			}
			
			try(FileInputStream fis = new FileInputStream(votesFile); JsonReader reader = new JsonReader(new InputStreamReader(fis))){
				Map<Integer, Post> dataInVotesFile = gson.fromJson(reader, postType);

				for(Integer i : dataInVotesFile.keySet()) {
					Post p = dataInVotesFile.get(i);
					parsedPosts.get(p.getIdPost()).setVotes(p.getVotes());
				}
			}
			
			try(FileInputStream fis = new FileInputStream(commentsFile); JsonReader reader = new JsonReader(new InputStreamReader(fis))){
				Map<Integer, Post> dataInCommentsFile = gson.fromJson(reader, postType);
			
				for(Integer i : dataInCommentsFile.keySet()) {
					Post p = dataInCommentsFile.get(i);
					parsedPosts.get(p.getIdPost()).setComments(p.getComments());
				}
			}
			
			try(FileInputStream fis = new FileInputStream(mutableDataPostsFile); JsonReader reader = new JsonReader(new InputStreamReader(fis))){
				Map<Integer, Post> dataInMutablePostFile = gson.fromJson(reader, postType);

				for(Integer i : dataInMutablePostFile.keySet()) {
					Post p = dataInMutablePostFile.get(i);
					parsedPosts.get(p.getIdPost()).setInteractions(p.getiterations());
					parsedPosts.get(p.getIdPost()).setRewin(p.getRewin());
					parsedPosts.get(p.getIdPost()).setNewVotes(p.getNewVotes());
					parsedPosts.get(p.getIdPost()).setCurators(p.getCurators());
					parsedPosts.get(p.getIdPost()).setNewCommentsBy(p.getNewCommentsBy());
				} 
			}
			
			for(Entry<Integer, Post> entry : parsedPosts.entrySet()) {
				Post p = entry.getValue();
				this.allPosts.putIfAbsent(entry.getKey(), p);
				this.blogUser.get(p.getAuthor()).add(p);
				
				for(String s : p.getRewin()) {
					this.postRewinnedByUser.get(s).add(p);
				}
			}
			
			loadUsersFeed();
		}else {postsFile.createNewFile(); votesFile.createNewFile(); commentsFile.createNewFile(); mutableDataPostsFile.createNewFile();}
	
		return;
	}
	
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
		
		try {
			backupLock.writeLock().lock();
			
			if(postDeletedSinceLastBackup) {
				postDeletedSinceLastBackup = false;
				backupNonCached(strategy, postsFile, postBackedup);
			}
			
			backupCached(strategy, postsFile, postBackedup, postToBeBackedup, postRecoveredFromBackup);
			postToBeBackedup = new ConcurrentHashMap<Integer, Post>();
			postRecoveredFromBackup = false;
			
			backupNonCached(new ExclusionStrategy() {
				
				@Override
				public boolean shouldSkipField(FieldAttributes f) {
					return (f.getDeclaringClass() == Post.class && !f.getName().equals("idPost") && !f.getName().equals("votes") && !f.getName().equals("authorVote"));
				}
				
				@Override
				public boolean shouldSkipClass(Class<?> arg0) {
					return false;
				}
			}, votesFile, postBackedup);
			
			backupNonCached(new ExclusionStrategy() {
				
				@Override
				public boolean shouldSkipField(FieldAttributes f) {
					return (f.getDeclaringClass() == Post.class && !f.getName().equals("idPost") && !f.getName().equals("comments") && !f.getName().equals("author"));
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
		}finally {backupLock.writeLock().unlock();}
		
	}
	
	private void loadUsersFeed() {
		Iterator<User> userIt = this.userRegistered.values().iterator();
		Collection<Post> posts = this.allPosts.values();
		
		while(userIt.hasNext()) {
			ArrayList<Post> feed = this.userFeed.get(userIt.next().getUsername());
			try {
				ArrayList<String> following = this.userFollowing.get(userIt.next().getUsername());
				
				for(String s : following) {
					Stream<Post> stream = posts.stream().filter(p -> p.getAuthor().equals(s));
					feed.addAll(stream.collect(Collectors.toList()));
				}
			}catch(NoSuchElementException e) {
				feed = new ArrayList<>();
				continue;
			}
		}
	}
}
