package server.follow_unfollow_service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import RMI.FollowerDatabase;
import RMI.RMICallback;
import server.database.Database;
import utility.TypeError;

public class FollowingImpl implements Following {
	private Database db;
	private BufferedWriter writerOutput;
	
	public FollowingImpl(Database db, BufferedWriter writerOutput) {
		this.db = db;
		this.writerOutput = writerOutput;
	}
	
	@Override
	public void addFollower(String usernameToFollow, RMICallback stubCallbackRegistration, Socket socket) throws IOException {
		if(!db.isUserRegistered(usernameToFollow)) {
			sendError(TypeError.FOLLOWERNOTEXISTS, writerOutput);
			return;
		}
		
		FollowerDatabase stubUsernameAssociated = stubCallbackRegistration.getCallback(usernameToFollow);
		
		String usernameNewFollow = db.getUsernameBySocket(socket);
		String error = stubUsernameAssociated.addFollower(usernameNewFollow);
		
		if(error.equals(TypeError.FOLLOWERERROR) || error.equals(TypeError.FOLLOWHIMSELFERROR)) {
			sendError(error, writerOutput);
		}else if(error.equals(TypeError.SUCCESS)){
			db.addFollowing(usernameNewFollow, usernameToFollow);
			db.addFollower(usernameToFollow, usernameNewFollow);
			
			ArrayList<String> followingListUser = db.getFollowingListByUsername(usernameNewFollow);
			FollowerDatabase stub = stubCallbackRegistration.getCallback(usernameNewFollow);
			stub.setFollowing(followingListUser);
			
			sendError(error, writerOutput);
		}
	}

	private void sendError(String error, BufferedWriter writerOutput) throws IOException {
		writerOutput.write(error);
		writerOutput.newLine();
		writerOutput.flush();
	}
	
}
