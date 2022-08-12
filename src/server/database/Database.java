package server.database;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import utility.Comment;
import utility.Post;
import utility.User;
import utility.Vote;

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
	
	private ConcurrentHashMap<String, ArrayList<Post>> blogUser;
	
	private ConcurrentHashMap<Integer, Post> allPosts;
	
	private ConcurrentHashMap<String, ArrayList<Post>> userFeed;
	
	private ConcurrentHashMap<String, ConcurrentLinkedQueue<Post>> postRewinnedByUser;
	
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
		this.blogUser = new ConcurrentHashMap<String, ArrayList<Post>>();
		this.allPosts = new ConcurrentHashMap<Integer, Post>();
		this.userFeed = new ConcurrentHashMap<String, ArrayList<Post>>();
		this.postRewinnedByUser = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Post>>();
		
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
		
		addPostToUserFeedByFollower(usernameNewFollower, usernameUpdateFollower);
		
		return;
	}

	public void removeFollower(String usernameUpdateFollower, String usernameToRemove) {
		Objects.requireNonNull(usernameUpdateFollower, "Username used to update his followers is null");
		Objects.requireNonNull(usernameToRemove, "Username to remove from followers is null");
		
		ArrayList<String> followers = userFollower.get(usernameUpdateFollower);
		
		followers.remove(usernameToRemove);
		
		removePostFromUserFeedByFollower(usernameToRemove, usernameUpdateFollower);
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
			
		return serializationUsers.toString();
	}
	
	public synchronized int getAndIncrementIdPost() {
		return idPost.getAndIncrement();
	}
	
	public void setPostListForUser(String username) {
		Objects.requireNonNull(username, "Username is null");
		
		blogUser.putIfAbsent(username, new ArrayList<Post>());
		
		return;
	}
	
	public String addPostInWinsome(int idPost, String authorPost, String titlePost, String contentPost) {
		Objects.requireNonNull(idPost,  "Id post is null");
		Objects.requireNonNull(authorPost, "Author post is null");
		Objects.requireNonNull(titlePost, "Title post is null");
		Objects.requireNonNull(contentPost, "Content post is null");
		
		Post newPost = new Post(idPost, titlePost, contentPost, authorPost);
	
		blogUser.get(authorPost).add(newPost);
		allPosts.putIfAbsent(idPost, newPost);
		
		ArrayList<String> authorFollower = userFollower.get(authorPost);
		
		for(String s : authorFollower) {
			addPostToUserFeedByAddingPost(s, newPost);
		}
		
		return "New post created with id: " + idPost;
	}

	public String getUserPostJson(String username) {
		Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {

			@Override
			public boolean shouldSkipClass(Class<?> arg0) {
				return false;
			}

			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				return (f.getDeclaringClass() == Post.class && f.getName().equals("content") && f.getName().equals("rewin") && f.getName().equals("votes") && f.getName().equals("comments"));
			}
			
		}).create();
		
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
	
	public void setUserFeed(String username) {
		Objects.requireNonNull(username, "Username is null");
		
		userFeed.putIfAbsent(username, new ArrayList<Post>());
		
		return;
	}
	
	public String getUserFeedJson(String username) {
		Objects.requireNonNull(username, "Username is null");
		
		ArrayList<Post> feedPost = userFeed.get(username);
		
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
			
			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				return (f.getDeclaringClass() == Post.class && f.getName().equals("content") && f.getName().equals("rewin") && f.getName().equals("votes") && f.getName().equals("comments"));
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
	
	private void addPostToUserFeedByFollower(String usernameUpdateFeed, String usernameTakePost) {
		Objects.requireNonNull(usernameUpdateFeed, "Username used to update his feed is null");
		Objects.requireNonNull(usernameTakePost, "Username used to take post to add to a certain user's feed is null");
		
		ArrayList<Post> feed = userFeed.get(usernameUpdateFeed);
		
		if(feed == null) 
			feed = new ArrayList<Post>();
		
		ArrayList<Post> posts = blogUser.get(usernameTakePost);
		
		feed.addAll(posts);
		
		return;
	}
	
	private void removePostFromUserFeedByFollower(String usernameUpdateFeed, String usernameTakePost) {
		Objects.requireNonNull(usernameUpdateFeed, "Username used to update his feed is null");
		Objects.requireNonNull(usernameTakePost, "Username used to remove post from user's feed is null");
		
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
	
	private void addPostToUserFeedByAddingPost(String usernameUpdateFeed, Post newPost) {
		Objects.requireNonNull(usernameUpdateFeed, "Username used to update his feed is null");
		Objects.requireNonNull(newPost, "The post that must be added to user's feed is null");
		
		ArrayList<Post> feed = userFeed.get(usernameUpdateFeed);
		
		feed.add(newPost);
		
		return;
	}
	
	private void removePostFromUserFeedByDeletingPost(int idPost, String authorPost) {
		Objects.requireNonNull(idPost, "Id used to get the specified post is null");
		Objects.requireNonNull(authorPost, "Author post is null");
		
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
	
	public String getPostByIdJson(int idPost) {
		Objects.requireNonNull(idPost, "Id used to get the specified post is null");
		
		Gson gson = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
			
			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				return (f.getDeclaringClass() == Post.class && f.getName().equals("idPost") && f.getName().equals("rewin") && f.getName().equals("author"));
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
		
		return allPosts.get(idPost);
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
		
		ArrayList<Post> feed = userFeed.get(username);
		Post p = getPostById(idPost);
		
		return feed.contains(p);
		
	}
	
	public boolean isPostNotNull(int idPost) {
		Objects.requireNonNull(idPost, "Id used to get the specified post is null");
		
		return getPostById(idPost) != null ? true : false;
	}
	
	public void removePostFromWinsome(int idPost) {
		Objects.requireNonNull(idPost, "Id used to get the specifies post is null");
		
		Post p = getPostById(idPost);
		
		String authorPost = p.getAuthor();
		
		p.removeAllComment();
		p.removeAllVotes();
		
		allPosts.remove(idPost, p);
		blogUser.remove(authorPost, p);
		
		removePostFromUserFeedByDeletingPost(idPost, authorPost);
	}
	
	public void addVoteToPost(int idPost, int vote, String authorVote) {
		Objects.requireNonNull(idPost, "Id post is null");
		Objects.requireNonNull(vote, "New vote for post is null");
		Objects.requireNonNull(authorVote, "Author vote is null");
		
		Vote v = new Vote(idPost, authorVote, vote);
		
		Post p = allPosts.get(idPost);
		p.addVote(v);
		
		ArrayList<Post> posts = blogUser.get(p.getAuthor());
		
		for(Post post : posts) {
			if(post.getIdPost() == idPost) {
				post.addVote(v);
				continue;
			}else continue;
		}
		
		return;
	}
	
	public boolean isPostAlreadyVotedByUser(int idPost, String authorVote) {
		Objects.requireNonNull(idPost, "Id post is null");
		Objects.requireNonNull(authorVote, "Author vote is null");
		
		Post p = allPosts.get(idPost);
		LinkedHashSet<Vote> votes = p.getVotes();
		
		for(Vote v : votes) {
			if(v.getAuthorVote().equals(authorVote))
				return true;
			else continue;
		}
		
		return false;
	}
	
	public void addCommentToPost(int idPost, String comment, String authorComment) {
		Objects.requireNonNull(idPost, "Id post is null");
		Objects.requireNonNull(comment, "New comment for post is null");
		Objects.requireNonNull(authorComment, "Author comment is null");
		
		Comment c = new Comment(idPost, authorComment, comment);
		
		Post p = allPosts.get(idPost);
		p.addComment(c);
		
		ArrayList<Post> posts = blogUser.get(p.getAuthor());
		
		for(Post post : posts) {
			if(post.getIdPost() == idPost) {
				post.addComment(c);
				break;
			}else continue;
		}
		
		return;
	}
	
	public void addRewinToPost(int idPost, String authorRewin) {
		Objects.requireNonNull(idPost, "Id to get the specified post is null");
		Objects.requireNonNull(authorRewin, "Author rewin is null");
		
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
	
	private void addPostToFeedByRewin(String usernameUpdateFeed, Post p) {
		Objects.requireNonNull(usernameUpdateFeed, "Author rewin is null");
		Objects.requireNonNull(p, "Post to add into feed is null");
		
		ArrayList<Post> feed = userFeed.get(usernameUpdateFeed);
		feed.add(p);
		
		return;
	}
	
}
