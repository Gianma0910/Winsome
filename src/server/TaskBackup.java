package server;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import configuration.ServerConfiguration;
import server.database.Database;

/**
 * Server thread that do backup at certain interval of time.
 * @author Gianmarco Petrocchi.
 *
 */
public class TaskBackup implements Runnable{

	/** Database.*/
	private Database db;
	/** Interval of time for doing backup.*/
	private int interval;
	/** File of user's data for registration.*/
	private File usersFile;
	/** File of user's following.*/
	private File followingFile;
	/** File of user's transactions.*/
	private File transactionsFile;
	/** File of posts' immutable data. */
	private File postsFile;
	/** File of posts' votes.*/
	private File votesFile;
	/** File of posts' comments.*/
	private File commentsFile;
	/** File of posts' mutable data.*/
	private File mutableDataPostFile;
	
	/**
	 * Basic contructor.
	 * @param db Database. Cannot be null.
	 * @param serverConf Server configuration to set the various constructor's variables. Cannot be null.
	 */
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
