package utility;

public class Post {

	private int idPost;
	private String title;
	private String content;
	private String author;
	private String rewin;
	
	public Post(int idPost, String title, String content, String username) {
		this.idPost = idPost;
		this.title = title;
		this.content = content;
		this.author = username;
		this.rewin = null;
	}
	
	public int idPost() {
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
}
