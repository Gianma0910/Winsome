package utility;

import java.util.Objects;

/**
 * Class that represents a comment for post in Winsome. This class is used only by server.
 * @author Gianmarco Gianmarco.
 */
public class Comment {
	
	/** Id post.*/
	private int idPost;
	/** Author of comment*/
	private String author;
	/** Content of comment. */
	private String content;
	
	/**
	 * Basic constructor.
	 * @param idPost Id post. Cannot be null.
	 * @param author Author of comment. Cannot be null.
	 * @param content Content of comment. Cannot be null.
	 */
	public Comment(int idPost, String author, String content) {
		Objects.requireNonNull(idPost, "Id post is null");
		Objects.requireNonNull(author, "Author of comment is null");
		Objects.requireNonNull(content, "Content of comment is null");
		
		this.idPost = idPost;
		this.author = author;
		this.content = content;
	}
	
	/**
	 * @return Id post.
	 */
	public int getIdPost() {
		return idPost;
	}
	
	/**
	 * @return Author of comment.
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * @return Content of comment.
	 */
	public String getContent() {
		return content;
	}
}
