package client;

public class PostWrapper {

	private int idPost;
	private String title;
	private String author;

	public PostWrapper(int idPost, String title, String author) {
		this.idPost = idPost;
		this.title = title;
		this.author = author;
	}
	
	public int getIdPost() {
		return idPost;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getAuthor() {
		return author;
	}
}
