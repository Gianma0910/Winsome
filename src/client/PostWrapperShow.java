package client;

import java.util.LinkedHashSet;

import utility.Comment;
import utility.Vote;

/**
 * Class used to serialized the list of posts that the client receive after a "show post" request.
 * @author Gianmarco Petrocchi.
 *
 */
public class PostWrapperShow {

	/** Title of post*/
	private String title;
	/** Content of post*/
	private String content;
	/** Votes of post*/
	private LinkedHashSet<Vote> votes;
	/** Comments of post*/
	private LinkedHashSet<Comment> comments;
	
	/**
	 * Basic constructor.
	 * @param title Title of post.
	 * @param content Content of post.
	 * @param votes Votes of post.
	 * @param comments Comments of post.
	 */
	public PostWrapperShow(String title, String content, LinkedHashSet<Vote> votes, LinkedHashSet<Comment> comments) {
		this.title = title;
		this.content = content;
		this.votes = votes;
		this.comments = comments;
	}
	
	/**
	 * @return Title of post.
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @return Content of post.
	 */
	public String getContent() {
		return content;
	}
	
	/**
	 * Count the number of positive votes that the post has.
	 * @return Number of positive votes.
	 */
	public int getNumberPositiveVotes() {
		int numberPositiveVotes = 0;
		
		for(Vote v : votes) {
			if(v.getVote() == 1)
				numberPositiveVotes++;
			else continue;
		}
		
		return numberPositiveVotes;
	}
	
	/**
	 * Count the number of negative votes that the post has.
	 * @return Number of negative votes.
	 */
	public int getNumberNegativeVotes() {
		int numberNegativeVotes = 0;
		
		for(Vote v : votes) {
			if(v.getVote() == -1)
				numberNegativeVotes++;
			else continue;
		}
		
		return numberNegativeVotes;
	}
	
	/**
	 * @return Comments of post.
	 */
	public LinkedHashSet<Comment> getComments() {
		return comments;
	}
	
}
