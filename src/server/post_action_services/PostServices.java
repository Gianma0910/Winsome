package server.post_action_services;

import java.io.IOException;
import java.net.Socket;

/**
 * Interface that contains methods to handle posts' services provided by server.
 * @author Gianmarco Petrocchi.
 */
public interface PostServices {

	/**
	 * Method used to handle create post service provided by server.
	 * Before handle user request it checks if it is logged in Winsome, otherwise the user will receive CLIENTNOTLOGGED error.
	 * Before create the post it checks if the title length is greater than 20 characters, otherwise the user will receive TITLELENGTHERROR error.
	 * Before create the post it checks if the content length is greater than 500 characters, otherwise the user will receive CONTENTLENGTHERROR error.
	 * @param title Title of post. Cannot be null.
	 * @param content Content of post. Cannot be null.
	 * @param socket Socket of client that send the create post request. Cannot be null.
	 * @throws IOException Only when occurs I/O error.
	 */
	void createPost(String title, String content, Socket socket) throws IOException;
	
	/**
	 * Method used to handle show user's posts service provided by server.
	 * Before handle user request it checks if it is logged in Winsome, otherwise the user will receive CLIENTNOTLOGGED error.
	 * @param socket Socket of client that send the blog request. Cannot be null.
	 * @throws IOException Only when occurs I/O error.
	 */
	void viewUserPost(Socket socket) throws IOException;
	
	/**
	 * Method used to handle show user's feed service provided by server.
	 * Before handle user request it checks if it is logged in Winsome, otherwise the user will receive CLIENTNOTLOGGED error.
	 * @param socket Socket of client that send the show feed request. Cannot be null.
	 * @throws IOException Only when occurs I/O error.
	 */
	void viewUserFeed(Socket socket) throws IOException;
	
	/**
	 * Method used to handle show post service provided by server.
	 * Before handle user request it checks if it is logged in Winsome, otherwise the user will receive CLIENTNOTLOGGED error.
	 * Before handle user request it checks if the post specified by idPostToParse exists in Winsome, otherwise the user will receive IDPOSTNOTEXISTS error.
	 * @param idPostToParse Id post to be parsed. Cannot be null.
	 * @param socket Socket of client that send the show post request. Cannot be null.
	 * @throws IOException Only when occurs I/O error.
	 */
	void viewPost(String idPostToParse, Socket socket) throws IOException;
	
	/**
	 * Method used to handle delete post service provided by server.
	 * Before handle user request it checks if it is logged in Winsome, otherwise the user will receive CLIENTNOTLOGGED error.
	 * Before handle user request it checks if the post specified by idPostToParse exists in Winsome, otherwise the user will receive IDPOSTNOTEXISTS error.
	 * Before handle user request it checks if the post to delete isn't in user's feed, otherwise the user will receive DELETEPOSTFEEDERROR error.
	 * Before handle user request it checks if the post to delete isn't in user's blog, otherwise the user will receive POSTNOTINYOURBLOG error.
	 * @param idPostToParse Id post to be parsed. Cannot be null.
	 * @param socket Socket of client that send the delete post request. Cannot be null.
	 * @throws IOException Only when occurs I/O error.
	 */
	void deletePost(String idPostToParse, Socket socket) throws IOException;
	
	/**
	 * Method used to handle rate post service provided by server.
	 * Before handle user request it checks if it is logged in Winsome, otherwise the user will receive CLIENTNOTLOGGED error.
	 * Before handle user request it checks if the post specified by idPostToParse exists in Winsome, otherwise the user will receive VOTEPOSTNOTEXISTS error.
	 * Before handle user request it checks if the user isn't the author of post, otherwise the user will receive VOTEAUTHORPOST error.
	 * Before handle user request it checks if the user hasn't already rated the post, otherwise the user will receive VOTEAUTHORPOST error.
	 * Before handle user request it checks the post specified by idPostToParse is in user's feed, otherwise the user will receive VOTEPOSTNOTINFEED error.
	 * Before handle user request it checks if the vote specified by voteToParse is equals to 1 or -1, otherwise the user will receive VOTENUMBERNOTVALID error.
	 * @param idPostToParse Id post to be parsed. Cannot be null.
	 * @param voteToParse Vote to be parsed. Cannot be null.
	 * @param socket Socket of client that send the rate post request. Cannot be null.
	 * @throws IOException Only when occurs I/O error.
	 */
	void ratePost(String idPostToParse, String voteToParse, Socket socket) throws IOException;
	
	/**
	 * Method used to handle add comment service provided by server.
	 * Before handle user request it checks if it is logged in Winsome, otherwise the user will receive CLIENTNOTLOGGED error.
	 * Before handle user request it checks if the post specified by idPostToParse exists in Winsome, otherwise the user will receive IDPOSTNOTEXISTS error.
	 * Before handle user request it checks if the post specified by idPostToParse isn't in user's blog, otherwise the user will receive POSTINYOURBLOG error.
	 * Before handle user request it checks if the post specified by idPostToParse is in user's feed, otherwise the user will receive POSTNOTINYOURFEED error.
	 * @param idPostToParse Id post to be parsed. Cannot be null.
	 * @param contentComment Content of comment to add. Cannot be null.
	 * @param socket Socket of client that send the add comment request. Cannot be null.
	 * @throws IOException Only when occurs I/O error. 
	 */
	void commentPost(String idPostToParse, String contentComment, Socket socket) throws IOException;
	
	/**
	 * Method used to handle rewin post service provided by server.
	 * Before handle user request it checks if it is logged in Winsome, otherwise the user will receive CLIENTNOTLOGGED error.
	 * Before handle user request it checks if the post specified by idPostToParse exists in Winsome, otherwise the user will receive IDPOSTNOTEXISTS error.
	 * Before handle user request it checks if the post specified by idPostToParse isn't in user's blog, otherwise the user will receive POSTINYOURBLOG error.
	 * Before handle user request it checks if the post specified by idPostToParse is in user's feed, otherwise the user will receive POSTNOTINYOURFEED error.
	 * @param idPostToParse Id post to be parsed. Cannot be null.
	 * @param socket Socket of client that send the rewin post request. Cannot be null.
	 * @throws IOException Only when occurs I/O error.
	 */
	void rewinPost(String idPostToParse, Socket socket) throws IOException;
}
