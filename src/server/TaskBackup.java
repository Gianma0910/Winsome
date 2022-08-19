package server;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import configuration.ServerConfiguration;
import server.database.Database;

public class TaskBackup implements Runnable{

	private Database db;
	private int interval;
	private File usersFile;
	private File followingFile;
	private File transactionsFile;
	private File postsFile;
	private File votesFile;
	private File commentsFile;
	private File mutableDataPostFile;
	
	public TaskBackup(Database db, ServerConfiguration serverConf) {
		Objects.requireNonNull(db, "Parameter database is null");
		Objects.requireNonNull(serverConf, "Server configuration is null");
		
		this.db = db;
		this.interval = serverConf.DELAYBACKUP;
		this.usersFile = new File(serverConf.USERSFILENAMEPATH);
		this.followingFile = new File(serverConf.FOLLOWINGFILENAMEPATH);
		this.transactionsFile = new File(serverConf.TRANSACTIONSFILENAMEPATH);
		this.postsFile = new File(serverConf.POSTSFILENAMEPATH);
		this.votesFile = new File(serverConf.VOTESFILENAMEPATH);
		this.commentsFile = new File(serverConf.COMMENTSFILENAMEPATH);
		this.mutableDataPostFile = new File(serverConf.MUTABLEDATAPOSTSFILENAMEPATH);
	}
	
	@Override
	public void run() {
			System.out.println("Backup task is now running!");
			
			while(!Thread.currentThread().isInterrupted()) {
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					return;
				}
				try {
					db.backupUsers(usersFile, followingFile, transactionsFile);
					db.backupPosts(postsFile, votesFile, commentsFile, mutableDataPostFile);
				}catch(IOException e) {
					System.err.println("Fatal error occured in TaskBackup: now aborting...");
					e.printStackTrace();
					System.exit(1);
				}
			}
	}
	
}
