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
	/** This error occurs only when a user try to become a new follower of a user that he already follow */
	public static final String FOLLOWERERROR = "FOLLOWERERROR";
	/** This error occurs only when a user try to create a post with a title length greater than 20 characters */
	public static final String TITLELENGTHERROR = "TITLELENGTHERROR";
	/** This error occurs only when a user try to create a post with a content length greater than 500 characters */
	public static final String CONTENTLENGTHERROR = "CONTENTLENGTHERROR";
	/** This error occurs only when a user try to vote a post that not exists */
	public static final String VOTEPOSTNOTEXISTS = "VOTEPOSTNOTEXISTS";
	/** This error occurs only when a user try to vote a post that he is already voted */
	public static final String VOTEALREADYEXISTS = "VOTEALREADYEXISTS";
	/** This error occurs only when a user try to vote a post that it isn't in his feed */
	public static final String VOTEPOSTNOTINFEED = "VOTEPOSTNOTINFEED";
	/** This error occurs only when a user try to vote his post */
	public static final String VOTEAUTHORPOST = "VOTEAUTHORPOST";
	/** This error occurs only when a user try to login twice*/
	public static final String CLIENTALREADYLOGGED = "CLIENTALREADYLOGGED";
}
