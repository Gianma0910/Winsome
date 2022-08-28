package utility;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Class that represents a single user in Winsome. This class is used only by server.
 * @author Gianmarco Petrocchi.
 */
public class User{

	/** User's username.*/
	private String username;
	/** User's password.*/
	private String password;
	/** User's tag list.*/
	private ArrayList<String> tagList;
	/** User's transactions.*/
	private ArrayList<Transaction> transactions;
	/** User's following*/
	private ArrayList<String> following;
	
	/**
	 * Basic constructor for a User.
	 * @param username User's username.
	 * @param password User's password.
	 * @param tags User's tags list.
	 */
	public User(String username, String password, ArrayList<String> tags) {	
		this.username = username;
		this.password = password;
		this.tagList = tags;
		
		this.transactions = new ArrayList<>();
		this.following = new ArrayList<>();
	}

	/**
	 * Set user's username.
	 * @param username User's username. Cannot be null.
	 */
	public void setUsername(String username) {
		Objects.requireNonNull(username, "Username is null");
		
		this.username = username;
	}
	
	/**
	 * @return User's username.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Set user's password.
	 * @param password User's password. Cannot be null.
	 */
	public void setPassword(String password) {
		Objects.requireNonNull(password, "Password is null");
		
		this.password = password;
	}
	
	/**
	 * @return User's password.
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Set user's tags list.
	 * @param tagList User's tags list. Cannot be null.
	 */
	public void setTagList(ArrayList<String> tagList) {
		Objects.requireNonNull(tagList, "Tags list is null");
		
		this.tagList = tagList;
	}
	
	/**
	 * @return User's tags list.
	 */
	public ArrayList<String> getTagList(){
		return tagList;
	}
	
	/**
	 * Add a new transaction for this user.
	 * @param t New transaction to add. Cannot be null.
	 */
	public synchronized void addTransaction(Transaction t) {
		Objects.requireNonNull(t, "Transaction to add is null");
		
		transactions.add(t);
		return;
	}
	
	/**
	 * @return User's transactions
	 */
	public ArrayList<Transaction> getTransactions(){
		return transactions;
	}
	
	/**
	 * Add a new user to this user's following list.
	 * @param username Username to add to following list. Cannot be null.
	 */
	public synchronized void addFollowing(String username) {
		Objects.requireNonNull(username, "Username to add to following list is null");
		
		following.add(username);
	}
	
	/**
	 * Remove username from this user's following list.
	 * @param username Username to remove from following list. Cannot be null.
	 */
	public synchronized void removeFollowing(String username) {
		Objects.requireNonNull(username, "Username to remove from following list is null");
		
		following.remove(username);
	}
	
	/**
	 * @return User's following list.
	 */
	public ArrayList<String> getFollowing(){
		return following;
	}

}

