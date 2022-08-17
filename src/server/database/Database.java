package server.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
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
	//private ConcurrentHashMap<String, User> userBackuped;
	/** Concurrent colleciton that contains user logged in Winsome
	 * <K, V>: K is the client socket; V is a String that represents the logged user's username */
	private ConcurrentHashMap<Socket, String> userLoggedIn;
	
	private ConcurrentHashMap<String, ArrayList<String>> userFollowing;
	
	private ConcurrentHashMap<String, ArrayList<String>> userFollower;
	
	private ConcurrentHashMap<String, ArrayList<Post>> blogUser;
	
	private ConcurrentHashMap<Integer, Post> allPosts;
	
	private ConcurrentHashMap<Integer, Post> postToBeBackedup;
	
	//private ConcurrentHashMap<Integer, Post> postBackedup;
	
	private ConcurrentHashMap<String, ArrayList<Post>> userFeed;
	
	private ConcurrentHashMap<String, ConcurrentLinkedQueue<Post>> postRewinnedByUser;
	
	private AtomicInteger idPost;
	
	private boolean postRecoveredFromBackup = false;
	
	private boolean firstBackupForUsers = false;
	
	/**
	 * Basic constructor for Database class
	 */
	public Database() {
		this.userRegistered = new ConcurrentHashMap<String, User>();
		this.userToBeBackedup = new ConcurrentHashMap<String, User>();
		//this.userBackuped = new ConcurrentHashMap<String, User>();
		this.userLoggedIn = new ConcurrentHashMap<Socket, String>();
		this.userFollowing = new ConcurrentHashMap<String, ArrayList<String>>();
		this.userFollower = new ConcurrentHashMap<String, ArrayList<String>>();
		this.blogUser = new ConcurrentHashMap<String, ArrayList<Post>>();
		this.allPosts = new ConcurrentHashMap<Integer, Post>();
		this.postToBeBackedup = new ConcurrentHashMap<Integer, Post>();
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
		
		userRegistered.putIfAbsent(username, u);
		userToBeBackedup.putIfAbsent(username, u);
	}
	
	/** 
	 * Check if the user is registered
	 * @param username User's username. Cannot be null
	 * @return true if the user is registerd, false otherwise
	 */
	public boolean isUserRegistered(String username) {		
		return userRegistered.containsKey(username);
	}
	
	/**
	 * Return an User object with the specified username
	 * @param username User's username
	 * @return An User object with the specified username, null otherwise
	 */
	public User getUserByUsername(String username) {
		Objects.requireNonNull(username, "Username is null");
		
		User u = userRegistered.get(username);
		
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
		postToBeBackedup.putIfAbsent(idPost, newPost);
		
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
				return (f.getDeclaringClass() == Post.class && f.getName().equals("content") && f.getName().equals("rewin") && f.getName().equals("votes") && f.getName().equals("comments")
						&& f.getName().equals("iterations") && f.getName().equals("newCommentsBy") && f.getName().equals("newVotes") && f.getName().equals("curators"));
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
	
	public ConcurrentHashMap<String, GainAndCurators> calculateGains(){
		ConcurrentHashMap<String, GainAndCurators> map = new ConcurrentHashMap<String, GainAndCurators>();
		
		for(Post p : allPosts.values()) 
			map.putIfAbsent(p.getAuthor(), p.getGainAndCurators());
		
		return map;
	}
	
	public void updateRewards(ConcurrentHashMap<String, GainAndCurators> gains, double authorPercentage) throws IllegalArgumentException, ClientNotRegisteredException, InvalidAmountException{
		Objects.requireNonNull(gains, "Map of gains is null");
		
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
	
	public String getWalletUserJson(String username) {
		Objects.requireNonNull(username, "Username to get the user is null");
		
		Gson gson = new GsonBuilder().create();
		
		User u = getUserByUsername(username);
		ArrayList<Transaction> transactions = u.getTransactions();
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
	
	public void loadUsersFromJsonFile(File usersFile, File followingFile, File followerFile, File transactionsFile) throws IOException {
		Objects.requireNonNull(usersFile, "Users file is null");
		Objects.requireNonNull(followingFile, "Following file is null");
		Objects.requireNonNull(followerFile, "Followers file is null");
		Objects.requireNonNull(transactionsFile, "Transactions file is null");
		
		Gson gson = new Gson();
		this.firstBackupForUsers = true;
		
		try(InputStream is = new FileInputStream(usersFile); JsonReader reader = new JsonReader(new InputStreamReader(is))){
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
							tags = new ArrayList<String>();
							
							while(reader.hasNext()) {
								reader.beginObject();
								String temp = reader.nextName();
								if(!temp.equals("nameAttribute")) break;
								tags.add(reader.nextString());
								reader.endObject();
							}
							reader.endArray();
							break;
						}
						default: {
							reader.skipValue();
							break;
						}
					}
				}
				reader.endObject();
				User u = new User(username, password, tags);
				
				this.userRegistered.putIfAbsent(username, u);
				this.userFollower.putIfAbsent(username, new ArrayList<String>());
				this.userFollowing.putIfAbsent(username, new ArrayList<String>());
				this.userFeed.putIfAbsent(username, new ArrayList<Post>());
				this.blogUser.putIfAbsent(username, new ArrayList<Post>());
				this.postRewinnedByUser.putIfAbsent(username, new ConcurrentLinkedQueue<>());
			}
			reader.endArray();
		}
		try(InputStream is = new FileInputStream(followingFile); JsonReader reader = new JsonReader(new InputStreamReader(is))){
			reader.setLenient(true);
			reader.beginArray();
			
			while(reader.hasNext()) {
				reader.beginObject();
				
				String nameAttribute = null;
				String username = null;
				ArrayList<String> following = new ArrayList<String>();
				
				while(reader.hasNext()) {
					nameAttribute = reader.nextName();
					
					switch(nameAttribute) {
						case "username": {
							username = reader.nextString();
							break;
						}
						case "following": {
							reader.beginArray();
							
							while(reader.hasNext())
								following.add(reader.nextString());
							reader.endArray();
							
							break;
						}
					}
					
					for(String s : following) {
						this.addFollowing(username, s);
					}
				}
				reader.endObject();
			}
			reader.endArray();
		}
		try(InputStream is = new FileInputStream(followerFile); JsonReader reader = new JsonReader(new InputStreamReader(is))){
			reader.setLenient(true);
			reader.beginArray();
			
			while(reader.hasNext()) {
				reader.beginObject();
				
				String nameAttribute = null;
				String username = null;
				ArrayList<String> followers = new ArrayList<String>();
				
				while(reader.hasNext()) {
					nameAttribute = reader.nextName();
					
					switch(nameAttribute){
						case "username" : {
							username = reader.nextString();
							break;
						}
						case "followers" : {
							reader.beginArray();
							
							while(reader.hasNext())
								followers.add(reader.nextString());
							reader.endArray();
							
							break;
						}
					}
					
					for(String s : followers)
						this.addFollower(username, s);
				}
				reader.endObject();
			}
			reader.endArray();
		}
		try(InputStream is = new FileInputStream(transactionsFile); JsonReader reader = new JsonReader(new InputStreamReader(is))){
			reader.setLenient(true);
			reader.beginArray(); 
			
			while(reader.hasNext()) {
				reader.beginObject();
				
				String nameAttribute = null;
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
								if(flag) { this.getUserByUsername(username).addTransaction(gson.fromJson(obj, Transaction.class)); }
							}
							reader.endArray();
						}
					}
				}
				reader.endObject();
			}
			reader.endArray();
		}
	}
	
	public void loadPostsFromJsonFile(File postsFile, File votesFile, File commentsFile, File mutableDataPostsFile) throws IOException {
		Objects.requireNonNull(postsFile, "Posts file is null");
		Objects.requireNonNull(votesFile, "Votes file is null");
		Objects.requireNonNull(commentsFile, "Comments file is null");
		Objects.requireNonNull(mutableDataPostsFile, "Mutable data posts file is null");
		
		Gson gson = new Gson();
		
		Map<Integer, JsonObject> parsedPosts = new HashMap<>();
		
		try(InputStream is = new FileInputStream(postsFile); JsonReader reader = new JsonReader(new InputStreamReader(is))){
			reader.setLenient(true);
			reader.beginArray();
			
			while(reader.hasNext()) {
				reader.beginObject();
				
				String nameAttribute = null;
				int id = -1;
				
				while(reader.hasNext()) {
					nameAttribute = reader.nextName();
					
					switch(nameAttribute) {
						case "id": {
							id = reader.nextInt();
							parsedPosts.putIfAbsent(id, new JsonObject());
							parsedPosts.get(id).addProperty(nameAttribute, id);
							
							break;
						}
						case "author": {
							parsedPosts.get(id).addProperty(nameAttribute, reader.nextString());
							break;
						}
						case "content": {
							parsedPosts.get(id).addProperty(nameAttribute, reader.nextString());
							break;
						}
						case "title": {
							parsedPosts.get(id).addProperty(nameAttribute, reader.nextString());
							break;
						}
					}
				}
				reader.endObject();
			}
			reader.endArray();
		}
		
		try(InputStream is = new FileInputStream(mutableDataPostsFile); JsonReader reader = new JsonReader(new InputStreamReader(is))){
			reader.setLenient(true);
			reader.beginArray();
			
			while(reader.hasNext()) {
				reader.beginObject();
				
				String nameAttribute = null;
				int id = -1;
				int newVotes = -1;
				int iterations = -1;
				JsonArray rewinners = new JsonArray();
				JsonObject newCommentsBy = new JsonObject();
				JsonArray curators = new JsonArray();
				JsonArray comments = new JsonArray();
				JsonArray votes = new JsonArray();
				
				while(reader.hasNext()) {
					nameAttribute = reader.nextName();
					
					switch(nameAttribute) {
						case "id": {
							id = reader.nextInt();
							break;
						}
						case "rewin":{
							reader.beginArray();
							
							while(reader.hasNext())
								rewinners.add(reader.nextString());
							
							reader.endArray();
							break;
						}
						case "newVotes": {
							newVotes = reader.nextInt();
							break;
						}
						case "iterations": {
							iterations = reader.nextInt();
							break;
						}
						case "curators": {
							reader.beginArray();
							
							while(reader.hasNext())
								curators.add(reader.nextString());
						
							reader.endArray();
							
							break;
						}
						case "newCommentsBy": {
							reader.beginObject();
							
							while(reader.hasNext())
								newCommentsBy.addProperty(reader.nextName(), reader.nextInt());
							
							reader.endObject();
							
							break;
						}
						case "comments": {
							comments = loadCommentsFromJsonFile(commentsFile);
							
							break;
						}
						case "votes": {
							votes = loadVotesFromJsonFile(votesFile);
							
							break;
						}
					}
				}
				reader.endObject();
				parsedPosts.get(id).add("rewin", rewinners);
				parsedPosts.get(id).add("newCommentsBy", newCommentsBy);
				parsedPosts.get(id).add("curators", curators);
				parsedPosts.get(id).add("comments", comments);
				parsedPosts.get(id).add("votes", votes);
				parsedPosts.get(id).addProperty("newVotes", newVotes);
				parsedPosts.get(id).addProperty("iterations", iterations);
			}
			reader.endArray();
			
			this.postRecoveredFromBackup = true;
			
			for(Entry<Integer, JsonObject> entry : parsedPosts.entrySet()) {
				Post p = gson.fromJson(entry.getValue(), Post.class);
				this.allPosts.putIfAbsent(entry.getKey(), p);
				this.blogUser.get(p.getAuthor()).add(p);
				
				LinkedHashSet<String> rewinners = p.getRewin();
				
				for(String s : rewinners) 
					this.postRewinnedByUser.get(s).add(p);
				
				ArrayList<String> followers = this.userFollower.get(p.getAuthor());
				
				for(String s : followers)
					this.userFeed.get(s).add(p);
			}
		}
	}
	
	private JsonArray loadCommentsFromJsonFile(File commentsFile) throws IOException {
		JsonArray comments = new JsonArray();
		
		try(InputStream is = new FileInputStream(commentsFile); JsonReader reader = new JsonReader(new InputStreamReader(is))){
			reader.setLenient(true);
			reader.beginArray();
			
			while(reader.hasNext()) {
				reader.beginObject();
				
				String nameAttribute = null;
				JsonObject obj = new JsonObject();
				
				while(reader.hasNext()) {
					nameAttribute = reader.nextName();
					
					if(nameAttribute.equals("id")) obj.addProperty(nameAttribute, reader.nextString());
					else if(nameAttribute.equals("author")) obj.addProperty(nameAttribute, reader.nextString());
					else if(nameAttribute.equals("content")) obj.addProperty(nameAttribute, reader.nextString());
				}
				reader.endObject();
				comments.add(obj);
			}
			reader.endArray();
		}
		
		return comments;
	}
	
	private JsonArray loadVotesFromJsonFile(File votesFile) throws IOException {
		JsonArray votes = new JsonArray();
		
		try(InputStream is = new FileInputStream(votesFile); JsonReader reader = new JsonReader(new InputStreamReader(is))){
			reader.setLenient(true);
			reader.beginArray();
			
			while(reader.hasNext()) {
				reader.beginObject();
				
				String nameAttribute = null;
				JsonObject obj = new JsonObject();
				
				while(reader.hasNext()) {
					nameAttribute = reader.nextName();
					
					if(nameAttribute.equals("id")) obj.addProperty(nameAttribute, reader.nextInt());
					else if(nameAttribute.equals("authorVote")) obj.addProperty(nameAttribute, reader.nextString());
					else if(nameAttribute.equals("vote")) obj.addProperty(nameAttribute, reader.nextInt());
				}
				reader.endObject();
				votes.add(obj);
			}
			reader.endArray();
		}
		
		return votes;
	}
}
