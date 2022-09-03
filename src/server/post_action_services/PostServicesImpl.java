package server.post_action_services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import server.database.Database;
import utility.TypeError;

/**
 * Class that implements PostService. This class is used to execute all the possible interaction with posts existed in Winsome.
 * @author Gianmarco Petrocchi.
 *
 */
public class PostServicesImpl implements PostServices {
	private Database db;
	private BufferedWriter writerOutput;
	
	public PostServicesImpl(Database db, BufferedWriter writerOutput) {
		this.db = db;
		this.writerOutput = writerOutput;
	}
	
	@Override
	public void createPost(String title, String content, Socket socket) throws IOException {
		String error;
		
		if(db.getUserLoggedIn().containsKey(socket) == false) {
			error = TypeError.CLIENTNOTLOGGED;
			sendError(error, writerOutput);
			return;
		}
	
		String authorPost = db.getUsernameBySocket(socket);
		
		if(title.length() > 20) {
			error = TypeError.TITLELENGTHERROR;
			sendError(error, writerOutput);
			return;
		}else if(content.length() > 500) {
			error = TypeError.CONTENTLENGTHERROR;
			sendError(error, writerOutput);
			return;
		}
		
		int idPost = db.getAndIncrementIdPost();
		error = db.addPostInWinsome(idPost, authorPost, title, content);
		 
		sendError(error, writerOutput);	
	}
	
	@Override
	public void viewUserPost(Socket socket) throws IOException {
		if(db.getUserLoggedIn().containsKey(socket) == false) {
			sendError(TypeError.CLIENTNOTLOGGED, writerOutput);
			return;
		}
		
		String username = db.getUsernameBySocket(socket);
		
		String serializationUserPost = db.getUserPostJson(username);
		
		writerOutput.write(serializationUserPost);
		writerOutput.newLine();
		writerOutput.flush();
		
		return;
	}
	
	@Override
	public void viewUserFeed(Socket socket) throws IOException {
		if(db.getUserLoggedIn().containsKey(socket) == false) {
			sendError(TypeError.CLIENTNOTLOGGED, writerOutput);
			return;
		}
		
		String username = db.getUsernameBySocket(socket);
		
		String serializationUserFeed = db.getUserFeedJson(username);
		
		writerOutput.write(serializationUserFeed);
		writerOutput.newLine();
		writerOutput.flush();
		
		return;
	}
	
	@Override
	public void viewPost(String idPostToParse, Socket socket) throws IOException {
		if(db.getUserLoggedIn().containsKey(socket) == false) {
			sendError(TypeError.CLIENTNOTLOGGED, writerOutput);
			return;
		}
		
		try {
			int idPost = Integer.parseInt(idPostToParse);
			
			String username = db.getUsernameBySocket(socket);
			
			if(db.isPostNotNull(idPost) == false) {
				sendError(TypeError.IDPOSTNOTEXISTS, writerOutput);
				return;
			}
			
			sendError(TypeError.SUCCESS, writerOutput);
			
			String serializedPost = db.getPostByIdJson(idPost);
			
			writerOutput.write(serializedPost);
			writerOutput.newLine();
			writerOutput.flush();
			
			if(db.isPostAuthor(idPost, username)) {
				sendError(TypeError.POSTINYOURBLOG, writerOutput);
			}
			
			if(db.isPostInFeed(idPost, username)) {
				sendError(TypeError.POSTINYOURFEED, writerOutput);
			}
			
			return;
		}catch(NumberFormatException e) {
			sendError(TypeError.NUMBERFORMATERRROR, writerOutput);
			return;
		}
	}
	
	@Override
	public void deletePost(String idPostToParse, Socket socket) throws IOException {
		if(db.getUserLoggedIn().containsKey(socket) == false) {
			sendError(TypeError.CLIENTNOTLOGGED, writerOutput);
			return;
		}
		
		try {
			int idPost = Integer.parseInt(idPostToParse);
			
			String username = db.getUsernameBySocket(socket);
			
			if(db.isPostNotNull(idPost) == false) {
				sendError(TypeError.IDPOSTNOTEXISTS, writerOutput);
				return;
			}
			
			if(db.isPostInFeed(idPost, username) && db.isPostAuthor(idPost, username)) {
				db.removePostFromWinsome(idPost);
				
				sendError(TypeError.SUCCESS, writerOutput);
				return;
			}
			
			if(db.isPostInFeed(idPost, username)) {
				sendError(TypeError.DELETEPOSTFEEDERROR, writerOutput);
				return;
			}
			
			if(db.isPostAuthor(idPost, username) == false) {
				sendError(TypeError.POSTNOTINYOURBLOG, writerOutput);
				return;
			}
			
			db.removePostFromWinsome(idPost);
			
			sendError(TypeError.SUCCESS, writerOutput);
			return;
		}catch(NumberFormatException e) {
			sendError(TypeError.NUMBERFORMATERRROR, writerOutput);
			return;
		}
		
	}
	
	@Override
	public void ratePost(String idPostToParse, String voteToParse, Socket socket) throws IOException {
		if(db.getUserLoggedIn().containsKey(socket) == false) {
			sendError(TypeError.CLIENTNOTLOGGED, writerOutput);
			return;
		}
		
		try {
			int idPost = Integer.parseInt(idPostToParse);
			int vote = Integer.parseInt(voteToParse);
			
			String authorVote = db.getUsernameBySocket(socket);
			
			if(db.isPostNotNull(idPost) == false) {
				sendError(TypeError.VOTEPOSTNOTEXISTS, writerOutput);
				return;
			}
			
			if(db.isPostAuthor(idPost, authorVote)) {
				sendError(TypeError.VOTEAUTHORPOST, writerOutput);
				return;
			}
			
			if(db.isPostInFeed(idPost, authorVote) == false) {
				sendError(TypeError.VOTEPOSTNOTINFEED, writerOutput);
				return;
			}
			
			if(db.isPostAlreadyVotedByUser(idPost, authorVote)) {
				sendError(TypeError.VOTEALREADYEXISTS, writerOutput);
				return;
			}
			
			if(vote != 1 && vote != -1) {
				sendError(TypeError.VOTENUMBERNOTVALID, writerOutput);
				return;
			}
			
			db.addVoteToPost(idPost, vote, authorVote);
			sendError(TypeError.SUCCESS, writerOutput);
		
			return;
		}catch(NumberFormatException e) {
			sendError(TypeError.NUMBERFORMATERRROR, writerOutput);
			return;
		}
		
	}
	
	@Override
	public void commentPost(String idPostToParse, String contentComment, Socket socket) throws IOException {
		if(db.getUserLoggedIn().containsKey(socket) == false) {
			sendError(TypeError.CLIENTNOTLOGGED, writerOutput);
			return;
		}
		
		int idPost = Integer.parseInt(idPostToParse);

		String authorComment = db.getUsernameBySocket(socket);

		if(db.isPostNotNull(idPost) == false) {
			sendError(TypeError.IDPOSTNOTEXISTS, writerOutput);
			return;
		}

		if(db.isPostAuthor(idPost, authorComment)) {
			sendError(TypeError.POSTINYOURBLOG, writerOutput);
			return;
		}

		if(db.isPostInFeed(idPost, authorComment) == false) {
			sendError(TypeError.POSTNOTINYOURFEED, writerOutput);
			return;
		}

		db.addCommentToPost(idPost, contentComment, authorComment);
		sendError(TypeError.SUCCESS, writerOutput);

		return;

	}
	
	@Override
	public void rewinPost(String idPostToParse, Socket socket) throws IOException {
		if(db.getUserLoggedIn().containsKey(socket) == false) {
			sendError(TypeError.CLIENTNOTLOGGED, writerOutput);
			return;
		}
		
		try {
			int idPost = Integer.parseInt(idPostToParse);
			String authorRewin = db.getUsernameBySocket(socket);
			
			if(db.isPostNotNull(idPost) == false) {
				sendError(TypeError.IDPOSTNOTEXISTS, writerOutput);
				return;
			}
			
			if(db.isPostAuthor(idPost, authorRewin)) {
				sendError(TypeError.POSTINYOURBLOG, writerOutput);
				return;
			}
		
			if(db.isPostInFeed(idPost, authorRewin) == false) {
				sendError(TypeError.POSTNOTINYOURFEED, writerOutput);
				return;
			}
			
			db.addRewinToPost(idPost, authorRewin);
			sendError(TypeError.SUCCESS, writerOutput);
		
			return;
		}catch(NumberFormatException e) {
			sendError(TypeError.NUMBERFORMATERRROR, writerOutput);
			return;
		}
		
	}

	private void sendError(String error, BufferedWriter writerOutput) throws IOException {
		writerOutput.write(error);
		writerOutput.newLine();
		writerOutput.flush();
	}
}
