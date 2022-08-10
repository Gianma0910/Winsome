package utility;

import java.util.LinkedHashSet;

public class Post {

	private int idPost;
	private String title;
	private String content;
	private String author;
	private String rewin;
	private LinkedHashSet<Vote> votes;
	private LinkedHashSet<Comment> comments;
	
	public Post(int idPost, String title, String content, String username) {
		this.idPost = idPost;
		this.title = title;
		this.content = content;
		this.author = username;
		this.rewin = null;
		this.votes = new LinkedHashSet<Vote>();
		this.comments = new LinkedHashSet<Comment>();
	}
	
	public int getIdPost() {
		return idPost;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getContent() {
		return content;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getRewin() {
		return rewin;
	}
	
	public void setRewin(String rewinUser) {
		this.rewin = rewinUser;
	}
	
	public boolean isRewinned() {
		return rewin != null ? true : false;
	}
	
	public void setVotes(LinkedHashSet<Vote> votes) {
		this.votes = votes;
	}
	
	public LinkedHashSet<Vote> getVotes(){
		return votes;
	}
	
	public void addVote(Vote v) {
		votes.add(v);
	}
	
	public void removeVote(Vote v) {
		votes.remove(v);
	}
	
	public void setComments(LinkedHashSet<Comment> comments) {
		this.comments = comments;
	}
	
	public LinkedHashSet<Comment> getComments(){
		return comments;
	}
	
	public void addComment(Comment c) {
		comments.add(c);
	}
	
	public void removeComment(Comment c) {
		comments.remove(c);
	}
	
	public void removeAllComment() {
		comments.removeAll(comments);
	}
	
	public void removeAllVotes() {
		votes.removeAll(votes);
	}
}
