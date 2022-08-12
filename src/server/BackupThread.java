package server;

import java.io.File;

import configuration.ServerConfiguration;
import server.database.Database;

public class BackupThread extends Thread{

	private Database db;
	private ServerConfiguration serverConf;
	
	private File userStorageFile;
	private File userFollowingStorageFile;
	private File userFollowersStorageFile;
	private File postStorageFile;
	private File votesStorageFile;
	private File commentsStorageFile;
	
	public BackupThread(Database db, ServerConfiguration serverConf) {
		this.db = db;
		this.serverConf = serverConf;
		
		this.userStorageFile = new File(serverConf.USERSFILENAMEPATH);
		this.userFollowingStorageFile = new File(serverConf.FOLLOWINGFILENAMEPATH);
		this.userFollowersStorageFile = new File(serverConf.FOLLOWERFILENAMEPATH);
		this.postStorageFile = new File(serverConf.POSTSFILENAMEPATH);
		this.votesStorageFile = new File(serverConf.VOTESFILENAMEPATH);
		this.commentsStorageFile = new File(serverConf.COMMENTSFILENAMEPATH);
	}
	
	public void run() {
		
	}
	
}
