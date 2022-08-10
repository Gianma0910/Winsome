package utility;

public class Comment {

	private int idPost;
	private String author;
	private String content;
	
	public Comment(int idPost, String author, String content) {
		this.idPost = idPost;
		this.author = author;
		this.content = content;
	}
	
	public int getIdPost() {
		return idPost;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getContent() {
		return content;
	}
}
