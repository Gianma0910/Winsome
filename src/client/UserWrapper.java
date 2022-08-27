package client;

import java.util.ArrayList;

/**
 * Class used to serialized list of users that the client receive after "list users" request.
 * @author Gianmarco Petrocchi.
 *
 */
public class UserWrapper{

	/** Username of user.*/
	private String username;
	/** Tags list of user*/
	private ArrayList<String> tagList;
	
	/**
	 * Basic constructor.
	 * @param username Username of user.
	 * @param tagList Tags list of user.
	 */
	public UserWrapper(String username, ArrayList<String> tagList) {
		this.username = username;
		this.tagList = tagList;
	}
	
	/**
	 * @return Username of user.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * @return Tags list of user.
	 */
	public ArrayList<String> getTagList(){
		return tagList;
	}
}
