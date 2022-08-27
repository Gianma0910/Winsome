package server.follow_unfollow_services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import RMI.ClientStorage;
import RMI.RMICallback;
import server.database.Database;
import utility.TypeError;

public class FollowingServiceImpl implements FollowingService {
	private Database db;
	private BufferedWriter writerOutput;
	
	public FollowingServiceImpl(Database db, BufferedWriter writerOutput) {
		this.db = db;
		this.writerOutput = writerOutput;
	}
	
	@Override
	public void addFollower(String usernameToFollow, RMICallback stubCallbackRegistration, Socket socket) throws IOException {
		if(db.getUserLoggedIn().containsKey(socket) == false) {
			sendError(TypeError.CLIENTNOTLOGGED, writerOutput);
			return;
		}
		
		if(!db.isUserRegistered(usernameToFollow)) {
			sendError(TypeError.FOLLOWERNOTEXISTS, writerOutput);
			return;
		}
	
		String usernameNewFollow = db.getUsernameBySocket(socket);
		
		if(db.isUserRegistered(usernameToFollow) == true && db.isUserLogged(usernameToFollow) == false) {
			db.addFollowing(usernameNewFollow, usernameToFollow);
			db.addFollower(usernameToFollow, usernameNewFollow);
			
			ArrayList<String> followingListUser = db.getFollowingListByUsername(usernameNewFollow);
			ClientStorage stub = stubCallbackRegistration.getCallback(usernameNewFollow);
			stub.setFollowing(followingListUser);
			
			sendError(TypeError.SUCCESS, writerOutput);
		}else {
			ClientStorage stubUsernameAssociated = stubCallbackRegistration.getCallback(usernameToFollow);
			String error = stubUsernameAssociated.addFollower(usernameNewFollow);
			
			if(error.equals(TypeError.FOLLOWERERROR) || error.equals(TypeError.FOLLOWHIMSELFERROR)) {
				sendError(error, writerOutput);
			}else if(error.equals(TypeError.SUCCESS)){
				db.addFollowing(usernameNewFollow, usernameToFollow);
				db.addFollower(usernameToFollow, usernameNewFollow);
				
				ArrayList<String> followingListUser = db.getFollowingListByUsername(usernameNewFollow);
				ClientStorage stub = stubCallbackRegistration.getCallback(usernameNewFollow);
				stub.setFollowing(followingListUser);
				
				sendError(error, writerOutput);
			}
		}

	}

	private void sendError(String error, BufferedWriter writerOutput) throws IOException {
		writerOutput.write(error);
		writerOutput.newLine();
		writerOutput.flush();
	}
	
}
