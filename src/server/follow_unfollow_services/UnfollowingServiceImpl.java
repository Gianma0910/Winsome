package server.follow_unfollow_services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import RMI.ClientStorage;
import RMI.RMICallback;
import server.database.Database;
import utility.TypeError;

/**
 * Class that implements UnfollowingService. This class is used to remove follower in ClientStorage's followers list
 * and to remove follower in Database's collection.
 * @author Gianmarco Petrocchi.
 *
 */
public class UnfollowingServiceImpl implements UnfollowingService {
	private Database db;
	private BufferedWriter writerOutput;
	
	public UnfollowingServiceImpl(Database db, BufferedWriter writerOutput) {
		this.db = db;
		this.writerOutput = writerOutput;
	}
	
	@Override
	public void removeFollowing(String usernameToUnfollow, RMICallback stubCallbackRegistration, Socket socket) throws IOException {
		if(db.getUserLoggedIn().containsKey(socket) == false) {
			sendError(TypeError.CLIENTNOTLOGGED, writerOutput);
			return;
		}
		
		if(!db.isUserRegistered(usernameToUnfollow)) {
			sendError(TypeError.UNFOLLOWINGNOTEXISTS, writerOutput);
			return;
		}
		
		String usernameRemoveFollow = db.getUsernameBySocket(socket);
		
		//check if the username that represents the following to remove is registered but not logged
		//if this two conditions are satisfied will not update ClientStorage's followers list of usernameToUnFollow.
		if(db.isUserRegistered(usernameToUnfollow) == true && db.isUserLogged(usernameToUnfollow) == false) {
			db.removeFollowing(usernameRemoveFollow, usernameToUnfollow);
			db.removeFollower(usernameToUnfollow, usernameRemoveFollow);
			
			ArrayList<String> followingListUser = db.getFollowingListByUsername(usernameRemoveFollow);
			ClientStorage stub = stubCallbackRegistration.getCallback(usernameRemoveFollow);
			stub.setFollowing(followingListUser);
			
			sendError(TypeError.SUCCESS, writerOutput);
		}else {
			ClientStorage stubUsernameAssociated = stubCallbackRegistration.getCallback(usernameToUnfollow);
			String error = stubUsernameAssociated.removeFollower(usernameRemoveFollow);
			
			if(error.equals(TypeError.FOLLOWERERROR) || error.equals(TypeError.UNFOLLOWHIMSELFERROR)) {
				sendError(error, writerOutput);
			}else if(error.equals(TypeError.SUCCESS)) {
				db.removeFollowing(usernameRemoveFollow, usernameToUnfollow);
				db.removeFollower(usernameToUnfollow, usernameRemoveFollow);
				
				ArrayList<String> followingListUser = db.getFollowingListByUsername(usernameRemoveFollow);
				ClientStorage stub = stubCallbackRegistration.getCallback(usernameRemoveFollow);
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
