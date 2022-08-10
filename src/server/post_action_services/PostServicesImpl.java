package server.post_action_services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import server.database.Database;
import utility.Post;
import utility.TypeError;

public class PostServicesImpl implements PostServices {
	private Database db;
	private BufferedWriter writerOutput;
	
	public PostServicesImpl(Database db, BufferedWriter writerOutput) {
		this.db = db;
		this.writerOutput = writerOutput;
	}
	
	@Override
	public void createPost(String [] requestSplitted, Socket socket) throws IOException {
		String titlePost = requestSplitted[1];
		String contentPost = requestSplitted[2];
		String authorPost = db.getUsernameBySocket(socket);
		
		String error;
		
		if(titlePost.length() > 20) {
			error = TypeError.TITLELENGTHERROR;
			sendError(error, writerOutput);
			return;
		}else if(contentPost.length() > 500) {
			error = TypeError.CONTENTLENGTHERROR;
			sendError(error, writerOutput);
			return;
		}
		
		int idPost = db.getAndIncrementIdPost();
		error = db.addPostInWinsome(idPost, authorPost, titlePost, contentPost);
		 
		sendError(error, writerOutput);	
	}
	
	@Override
	public void viewUserPost(Socket socket) throws IOException {
		String username = db.getUsernameBySocket(socket);
		
		String serializationUserPost = db.getUserPostJson(username);
		
		writerOutput.write(serializationUserPost);
		writerOutput.newLine();
		writerOutput.flush();
		
		return;
	}
	
	@Override
	public void viewUserFeed(Socket socket) throws IOException {
		String username = db.getUsernameBySocket(socket);
		
		String serializationUserFeed = db.getUserFeedJson(username);
		
		writerOutput.write(serializationUserFeed);
		writerOutput.newLine();
		writerOutput.flush();
		
		return;
	}
	
	@Override
	public void viewPost(String idPostToParse, Socket socket) throws IOException {
		int idPost = Integer.parseInt(idPostToParse);
		
		String username = db.getUsernameBySocket(socket);
		
		if(db.isPostNull(idPost) == false) {
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
		}else if(db.isPostInFeed(idPost, username)) {
			sendError(TypeError.POSTINYOURFEED, writerOutput);
		}
		
		return;
	}
	
	@Override
	public void deletePost(String idPostToParse, Socket socket) throws IOException {
		int idPost = Integer.parseInt(idPostToParse);
		
		String username = db.getUsernameBySocket(socket);
		
		if(db.isPostNull(idPost) == false) {
			sendError(TypeError.IDPOSTNOTEXISTS, writerOutput);
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
	}
	
	private void sendError(String error, BufferedWriter writerOutput) throws IOException {
		writerOutput.write(error);
		writerOutput.newLine();
		writerOutput.flush();
	}
	
}
