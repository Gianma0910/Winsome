package utility;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class Post {

	private int idPost;
	private String title;
	private String content;
	private String author;
	private LinkedHashSet<String> rewin;
	private LinkedHashSet<Vote> votes;
	private LinkedHashSet<Comment> comments;
	private int iterations;
	private Map<String, Integer> newCommentsBy;
	private int newVotes;
	private LinkedHashSet<String> curators;
	
	public Post(int idPost, String title, String content, String username) {
		this.idPost = idPost;
		this.title = title;
		this.content = content;
		this.author = username;
		this.rewin = new LinkedHashSet<String>();
		this.votes = new LinkedHashSet<Vote>();
		this.comments = new LinkedHashSet<Comment>();
		
		this.iterations = 0;
		this.newCommentsBy = new HashMap<String, Integer>();
		this.newVotes = 0;
		this.curators = new LinkedHashSet<String>();
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
	
	public LinkedHashSet<String> getRewin() {
		return rewin;
	}
	
	public synchronized void addRewin(String rewinUser) {
		this.rewin.add(rewinUser);
	}
	
	public synchronized void removeRewin(String rewinUser) {
		this.rewin.remove(rewinUser);
	}
	
	public synchronized void removeAllRewin() {
		this.rewin.removeAll(rewin);
	}
	
	public boolean isRewinned() {
		return rewin.size() != 0 ? true : false;
	}

	public void setVotes(LinkedHashSet<Vote> votes) {
		this.votes = votes;
	}
	
	public LinkedHashSet<Vote> getVotes(){
		return votes;
	}
	
	public synchronized void addVote(Vote v) {
		votes.add(v);
	}
	
	public synchronized void removeAllVotes() {
		votes.removeAll(votes);
	}
	
	public LinkedHashSet<Comment> getComments(){
		return comments;
	}
	
	public synchronized void addComment(Comment c) {
		comments.add(c);
	}
	
	public synchronized void removeAllComment() {
		comments.removeAll(comments);
	}
	
	public int getiterations() {
		return iterations;
	}
	
	public LinkedHashSet<String> getCurators(){
		return curators;
	}
	
	public void addCurators(String nameCurator) {
		curators.add(nameCurator);
	}
	
	public void setNewVotes(int votes) {
		this.newVotes = votes;
	}
	
	public int getNewVotes() {
		return newVotes;
	}
	
	public int getNumUserComments(String username) {
		return newCommentsBy.get(username);
	}
	
	public void incrementNumUserComments(String username) {
		newCommentsBy.compute(username, (k ,v) -> v == null ? 1 : v + 1);
	}
	
	public Map<String, Integer> getNewCommentsBy(){
		return newCommentsBy;
	}
	
	public void setNewCommentsBy(Map<String, Integer> newCommentsBy) {
		this.newCommentsBy = newCommentsBy;
	}
	
	public GainAndCurators getGainAndCurators() {
		
		iterations++;
		
		double temp = 0;
		for(Integer cp : newCommentsBy.values()) {
			temp += (2 / (1 + Math.pow(Math.E, -(cp - 1))));
		}
		LinkedHashSet<String> c = curators;
		
		double result = (Math.log(Math.max(newVotes, 0) + 1) + Math.log(temp + 1)) / iterations;
		
		newCommentsBy = new HashMap<>();
		curators = new LinkedHashSet<>();
		newVotes = 0;
		
		return new GainAndCurators(result, c);
	}
	
}
