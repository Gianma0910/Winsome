package client;

/**
 * Class used to serialized the list of post that the client receive after a "show feed" request.
 * @author Gianmarco Petrocchi.
 *
 */
public class PostWrapper {

	/**Id post.*/
	private int idPost;
	/**Title of post.*/
	private String title;
	/** Author of post.*/
	private String author;

	/**
	 * Basic constructor. 
	 * @param idPost Id post.
	 * @param title Title of post.
	 * @param author Author of post.
	 */
	public PostWrapper(int idPost, String title, String author) {
		this.idPost = idPost;
		this.title = title;
		this.author = author;
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
	 * @return Author of post.
	 */
	public String getAuthor() {
		return author;
	}
}
