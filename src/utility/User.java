package utility;

import java.io.Serializable;
import java.util.ArrayList;

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
	private ArrayList<String> tags;
	
	/**
	 * Basic constructor for a User
	 * @param username Cannot be null
	 * @param password Cannot be null
	 * @param tags Cannot be null
	 */
	public User(String username, String password, ArrayList<String> tags) {	
		this.username = username;
		this.password = password;
		this.tags = tags;
	}

	/** Return user's username*/
	public String getUsername() {
		return username;
	}
	
	/** Return user's password*/
	public String getPassword() {
		return password;
	}
	
	/** Return user's tag list*/
	public ArrayList<String> getTagList(){
		return tags;
	}
	
}

