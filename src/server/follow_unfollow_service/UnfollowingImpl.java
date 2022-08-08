package server.follow_unfollow_service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import RMI.FollowerDatabase;
import RMI.RMICallback;
import server.database.Database;
import utility.TypeError;

public class UnfollowingImpl implements Unfollowing {
	private Database db;
	private BufferedWriter writerOutput;
	
	public UnfollowingImpl(Database db, BufferedWriter writerOutput) {
		this.db = db;
		this.writerOutput = writerOutput;
	}
	
	@Override
	public void removeFollowing(String usernameToUnfollow, RMICallback stubCallbackRegistration, Socket socket) throws IOException {
		if(!db.isUserRegistered(usernameToUnfollow)) {
			sendError(TypeError.UNFOLLOWINGNOTEXISTS, writerOutput);
			return;
		}
		
		FollowerDatabase stubUsernameAssociated = stubCallbackRegistration.getCallback(usernameToUnfollow);
		
		String usernameRemoveFollow = db.getUsernameBySocket(socket);
		String error = stubUsernameAssociated.removeFollower(usernameRemoveFollow);
		
		if(error.equals(TypeError.FOLLOWERERROR) || error.equals(TypeError.UNFOLLOWHIMSELFERROR)) {
			sendError(error, writerOutput);
		}else if(error.equals(TypeError.SUCCESS)) {
			db.removeFollowing(usernameRemoveFollow, usernameToUnfollow);
			db.removeFollower(usernameToUnfollow, usernameRemoveFollow);
			
			ArrayList<String> followingListUser = db.getFollowingListByUsername(usernameRemoveFollow);
			FollowerDatabase stub = stubCallbackRegistration.getCallback(usernameRemoveFollow);
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
