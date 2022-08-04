package client;

import java.util.ArrayList;

public class UserWrapper {

	private String username;
	private ArrayList<String> tagList;
	
	public UserWrapper(String username, ArrayList<String> tagList) {
		this.username = username;
		this.tagList = tagList;
	}
	
	public String getUsername() {
		return username;
	}
	
	public ArrayList<String> getTagList(){
		return tagList;
	}
}
