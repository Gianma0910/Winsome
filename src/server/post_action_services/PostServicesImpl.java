package server.post_action_services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import server.database.Database;
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

	private void sendError(String error, BufferedWriter writerOutput) throws IOException {
		writerOutput.write(error);
		writerOutput.newLine();
		writerOutput.flush();
	}
	
}
