package utility;

public class Vote {

	private int idPost;
	private String authorVote;
	private int vote;

	public Vote(int idPost, String authorVote, int vote) {
		this.idPost = idPost;
		this.authorVote = authorVote;
		this.vote = vote;
	}
	
	public int getIdPost() {
		return idPost;
	}
	
	public String getAuthorVote() {
		return authorVote;
	}
	
	public int getVote() {
		return vote;
	}
}
