package client;

import java.util.LinkedHashSet;

import utility.Comment;
import utility.Vote;

public class PostWrapperShow {

	private String title;
	private String content;
	private LinkedHashSet<Vote> votes;
	private LinkedHashSet<Comment> comments;
	
	public PostWrapperShow(String title, String content, LinkedHashSet<Vote> votes, LinkedHashSet<Comment> comments) {
		this.title = title;
		this.content = content;
		this.votes = votes;
		this.comments = comments;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getContent() {
		return content;
	}
	
	public int getNumberPositiveVotes() {
		int numberPositiveVotes = 0;
		
		for(Vote v : votes) {
			if(v.getVote() == 1)
				numberPositiveVotes++;
			else continue;
		}
		
		return numberPositiveVotes;
	}
	
	public int getNumberNegativeVotes() {
		int numberNegativeVotes = 0;
		
		for(Vote v : votes) {
			if(v.getVote() == -1)
				numberNegativeVotes++;
			else continue;
		}
		
		return numberNegativeVotes;
	}
	
	public LinkedHashSet<Comment> getComments() {
		return comments;
	}
	
}
