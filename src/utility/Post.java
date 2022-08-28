package utility;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;

/**
 * Class that represents a post in Winsome. This class is only used by server.
 * @author Gianmarco Petrocchi.
 */
public class Post {

	/** Id post.*/
	private int idPost;
	/** Title of post.*/
	private String title;
	/** Content of post.*/
	private String content;
	/** Author of post.*/
	private String author;
	/** Set of users' username that rewinned this post.*/
	private LinkedHashSet<String> rewin;
	/** Set of votes.*/
	private LinkedHashSet<Vote> votes;
	/** Set of comments.*/
	private LinkedHashSet<Comment> comments;
	/** Iterations for rewards calculation.*/
	private int iterations;
	/** Map that count, for all the users that commented this post, 
	 * the number of new comments. Used for rewards calculation.*/
	private Map<String, Integer> newCommentsBy;
	/** Count the number of new votes. Used for rewards calculation.*/
	private int newVotes;
	/** Set of curators.*/
	private LinkedHashSet<String> curators;
	
	/**
	 * Basic constructor.
	 * @param idPost Id post.
	 * @param title Title of post.
	 * @param content Content of post.
	 * @param username Author of post.
	 */
	public Post(int idPost, String title, String content, String username) {
		this.idPost = idPost;
		this.title = title;
		this.content = content;
		this.author = username;
		this.rewin = new LinkedHashSet<String>();
		this.votes = new LinkedHashSet<Vote>();
		this.comments = new LinkedHashSet<Comment>();
		
		this.iterations = 0;
		this.newCommentsBy = new HashMap<String, Integer>();
		this.newVotes = 0;
		this.curators = new LinkedHashSet<String>();
	}
	
	/**
	 * @return Id post.
	 */
	public int getIdPost() {
		return idPost;
	}
	
	/**
	 * @return Title of post.
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @return Content of post.
	 */
	public String getContent() {
		return content;
	}
	
	/**
	 * @return Author of post.
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * @return Set of users' username in rewin set.
	 */
	public LinkedHashSet<String> getRewin() {
		return rewin;
	}
	
	/**
	 * Add a new rewin.
	 * @param rewinUser New rewinner add. Cannot be null.
	 */
	public synchronized void addRewin(String rewinUser) {
		Objects.requireNonNull(rewinUser, "New rewinner is null");
		
		this.rewin.add(rewinUser);
	}
	
	/**
	 * Remove rewin.
	 * @param rewinUser Username to remove from set. Cannot be null.
	 */
	public synchronized void removeRewin(String rewinUser) {
		Objects.requireNonNull(rewinUser, "Rewinner to remove is null");		
		this.rewin.remove(rewinUser);
	}
	
	/**
	 * Remove all users' username that rewinned this post.
	 */
	public synchronized void removeAllRewin() {
		this.rewin.removeAll(rewin);
	}
	
	/**
	 * Check if this post is rewinned.
	 * @return true if the size of set id different from 0; false otherwise.
	 */
	public boolean isRewinned() {
		return rewin.size() != 0 ? true : false;
	}

	/**
	 * Set posts' votes.
	 * @param votes Set of posts' votes. Cannot be null.
	 */
	public void setVotes(LinkedHashSet<Vote> votes) {
		Objects.requireNonNull(votes, "Set of votes is null");
		
		this.votes = votes;
	}
	
	/**
	 * @return Posts' votes.
	 */
	public LinkedHashSet<Vote> getVotes(){
		return votes;
	}
	
	/**
	 * Add a new vote to this post. 
	 * @param v New vote to add. Cannot be null.
	 */
	public synchronized void addVote(Vote v) {
		Objects.requireNonNull(v, "Vote to add is null");
		
		votes.add(v);
	}
	
	/**
	 * Remove all votes from this post.
	 */
	public synchronized void removeAllVotes() {
		votes.removeAll(votes);
	}
	
	/**
	 * @return Posts' comments.
	 */
	public LinkedHashSet<Comment> getComments(){
		return comments;
	}
	
	/**
	 * Add a new comment to this post.
	 * @param c New comment to add. Cannot be null.
	 */
	public synchronized void addComment(Comment c) {
		Objects.requireNonNull(c, "New comment to add is null");
		
		comments.add(c);
	}
	
	/**
	 * Remove all comments from this post.
	 */
	public synchronized void removeAllComment() {
		comments.removeAll(comments);
	}
	
	/**
	 * @return Number of iterations.
	 */
	public int getiterations() {
		return iterations;
	}
	
	/**
	 * @return Set of curators.
	 */
	public LinkedHashSet<String> getCurators(){
		return curators;
	}
	
	/**
	 * Add a new curator to this post.
	 * @param nameCurator New curator to add. Cannot be null.
	 */
	public void addCurators(String nameCurator) {
		Objects.requireNonNull(nameCurator, "New curator to add is null");
		
		curators.add(nameCurator);
	}
	
	/**
	 * Set new votes to this post.
	 * @param votes Number of votes.
	 */
	public void setNewVotes(int votes) {
		this.newVotes = votes;
	}
	
	/**
	 * @return Number of new votes.
	 */
	public int getNewVotes() {
		return newVotes;
	}
	
	/**
	 * @param username User's username. Cannot be null.
	 * @return Number of new comments of a user
	 */
	public int getNumUserComments(String username) {
		Objects.requireNonNull(username, "Username is null");
		
		return newCommentsBy.get(username);
	}
	
	/**
	 * Increment the number of new comments of a user.
	 * @param username User's username. Cannot be null.
	 */
	public void incrementNumUserComments(String username) {
		Objects.requireNonNull(username, "Username is null");
		
		newCommentsBy.compute(username, (k ,v) -> v == null ? 1 : v + 1);
	}
	
	/**
	 * @return Map of new comments of users.
	 */
	public Map<String, Integer> getNewCommentsBy(){
		return newCommentsBy;
	}
	
	/**
	 * Set the map of new comments by.
	 * @param newCommentsBy Cannot be null.
	 */
	public void setNewCommentsBy(Map<String, Integer> newCommentsBy) {
		Objects.requireNonNull(newCommentsBy, "Map is null");
		
		this.newCommentsBy = newCommentsBy;
	}
	
	/**
	 * @return Gains of all the curators of this post.
	 */
	public GainAndCurators getGainAndCurators() {
		
		iterations++;
		
		double temp = 0;
		for(Integer cp : newCommentsBy.values()) {
			temp += (2 / (1 + Math.pow(Math.E, -(cp - 1))));
		}
		LinkedHashSet<String> c = curators;
		
		double result = (Math.log(Math.max(newVotes, 0) + 1) + Math.log(temp + 1)) / iterations;
		
		newCommentsBy = new HashMap<>();
		curators = new LinkedHashSet<>();
		newVotes = 0;
		
		return new GainAndCurators(result, c);
	}
	
}
