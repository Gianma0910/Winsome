package utility;

/**
 * Utility class that contains all the type of error that can occurs during the execution of the client request.
 * It doesn't nedd to be istantiated
 * @author Gianmarco Petrocchi
 */
public class TypeError {
	/** This "error" occurs only when the operation is successfully completed*/
	public static final String SUCCESS = "SUCCESS";
	/** This error occurs only when the new user's username is null */
	public static final String USERNAMENULL = "USERNAMENULL";
	/** This error occurs only when the new user's password is null */
	public static final String PWDNULL = "PWDNULL";
	/** This error occurs only when the new user's username already exists in Winsome*/
	public static final String USRALREADYEXIST = "USRALREADYEXIST";
	/** This error occurs only when a user try to login with a wrong username*/
	public static final String USERNAMEWRONG = "USERNAMEWRONG";
	/** This error occurs only when a user try to login with a wrong password*/
	public static final String PWDWRONG = "PWDWRONG";
	/** This error occurs only when a client try to login with a username of a user that is already logged in*/
	public static final String USRALREADYLOGGED = "USRALREADYLOGGED";
	/** This error occurs only when the logout method is executed */
	public static final String LOGOUTERROR = "LOGOUTERROR";
}
