package RMI;

import java.rmi.RemoteException;
import java.util.ArrayList;

import exceptions.UserAlreadyExistsException;
import server.database.Database;
import utility.TypeError;
import utility.User;

public class RMIRegistrationImpl implements RMIRegistration {

	private Database db;
	
	public RMIRegistrationImpl(Database db) {
		super();
		this.db = db;
	}
	
	public String register(String username, String password, ArrayList<String> tags) throws RemoteException {
		//check if the parameter username is empty
		if(username.isEmpty() || username == null)
			return TypeError.USERNAMENULL;
		
		//check if the parameter username already exists in Winsome
		if(db.isUserRegistered(username))
			return TypeError.USRALREADYEXIST;
		
		//check if the parameter password is empty
		if(password.isEmpty() || password == null)
			return TypeError.PWDNULL;
	
		//create a new User
		User user = new User(username, password, tags);
	
		System.out.println(user.getTagList());
		
		//add the new User into Database
		db.addUserToDatabase(username, user);
		return TypeError.SUCCESS;
	}
}
