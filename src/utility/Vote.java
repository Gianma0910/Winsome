package utility;

import java.util.Objects;

/**
 * Class that represents a single vote in Winsome. This class is used only by server.
 * @author Gianmarco Petrocchi.
 */
public class Vote {

	/** Id post.*/
	private int idPost;
	/** Author of vote.*/
	private String authorVote;
	/** Vote.*/
	private int vote;

	/**
	 * Basic constructor.
	 * @param idPost Id post. Cannot be null.
	 * @param authorVote Author of vote. Cannot be null.
	 * @param vote Vote. Cannot be null.
	 */
	public Vote(int idPost, String authorVote, int vote) {
		Objects.requireNonNull(idPost, "Id post is null");
		Objects.requireNonNull(authorVote, "Author of vote is null");
		Objects.requireNonNull(vote, "Vote is null");
		
		this.idPost = idPost;
		this.authorVote = authorVote;
		this.vote = vote;
	}
	
	/**
	 * @return Id post.
	 */
	public int getIdPost() {
		return idPost;
	}
	
	/**
	 * @return Author of vote.
	 */
	public String getAuthorVote() {
		return authorVote;
	}
	
	/**
	 * @return Vote.
	 */
	public int getVote() {
		return vote;
	}
}
