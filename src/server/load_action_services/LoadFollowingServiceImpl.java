package server.load_action_services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import RMI.ClientStorage;
import RMI.RMICallback;
import server.database.Database;
import utility.TypeError;

public class LoadFollowingServiceImpl implements LoadFollowingService {

	private Database db;
	private BufferedWriter writerOutput;
	
	public LoadFollowingServiceImpl(Database db, BufferedWriter writerOutput) {
		this.db = db;
		this.writerOutput = writerOutput;
	}
	
	@Override
	public void loadFollowing(RMICallback stubCallbackRegistration, String username) throws RemoteException, IOException {
		ClientStorage databaseClient = stubCallbackRegistration.getCallback(username);
		
		ArrayList<String> followingUser = db.getFollowingListByUsername(username);
		
		databaseClient.setFollowing(followingUser);
		
		writerOutput.write(TypeError.SUCCESS);
		writerOutput.newLine();
		writerOutput.flush();
	}

}
