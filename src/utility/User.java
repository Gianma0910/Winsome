package utility;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Class that represents a single user in Winsome
 * @author Gianmarco Petrocchi
 */
public class User{

	/** User's username */
	private String username;
	/** User's password*/
	private String password;
	/** User's tag list*/
	private ArrayList<String> tagList;
	
	private ArrayList<Transaction> transactions;

	private ArrayList<String> following;
	
	/**
	 * Basic constructor for a User
	 * @param username Cannot be null
	 * @param password Cannot be null
	 * @param tags Cannot be null
	 */
	public User(String username, String password, ArrayList<String> tags) {	
		this.username = username;
		this.password = password;
		this.tagList = tags;
		
		this.transactions = new ArrayList<>();
		this.following = new ArrayList<>();
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	/** Return user's username*/
	public String getUsername() {
		return username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	/** Return user's password*/
	public String getPassword() {
		return password;
	}
	
	public void setTagList(ArrayList<String> tagList) {
		this.tagList = tagList;
	}
	
	/** Return user's tag list*/
	public ArrayList<String> getTagList(){
		return tagList;
	}
	
	public void addTransaction(Transaction t) {
		Objects.requireNonNull(t, "Transaction to add is null");
		
		transactions.add(t);
		return;
	}
	
	public ArrayList<Transaction> getTransactions(){
		return transactions;
	}
	
	public void addFollowing(String username) {
		following.add(username);
	}
	
	public void removeFollowing(String username) {
		following.remove(username);
	}
	
	public ArrayList<String> getFollowing(){
		return following;
	}
}

