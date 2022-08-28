package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import configuration.ServerConfiguration;
import exceptions.ClientNotRegisteredException;
import exceptions.InvalidAmountException;
import server.database.Database;

/**
 * Server thread that calculate the rewards for all the users registerd, at certain interval of time.
 * @author Gianmarco Petrocchi.
 */
public class TaskReward implements Runnable {

	/** Database.*/
	private Database db;
	/** Multicast socket UDP*/
	private MulticastSocket multicastSocket;
	/** Interval of time for doing calculation of rewards.*/
	private int interval;
	/** Multicast address to send notification.*/
	private InetAddress multicastAddress;
	/** Multicast port*/
	private int port;
	/** Author percentage used for calculation of rewards*/
	private double authorPercentage;
	
	/**
	 * Basic constructor.
	 * @param db Database. Cannot be null.
	 * @param serverConf Server configuration used to set some constructor's variables. Cannot be null.
	 * @throws IOException Only when occurs I/O error.
	 */
	public TaskReward(Database db, ServerConfiguration serverConf) throws IOException {
		Objects.requireNonNull(db, "Database is null");
		Objects.requireNonNull(serverConf, "Configuration server is null");
		
		this.db = db;
		this.interval = serverConf.DELAYEARNINGSCALCULATION;
		this.multicastAddress = serverConf.MULTICASTADDRESS;
		this.port = serverConf.MULTICASTPORT;
		this.authorPercentage = serverConf.AUTHORPERCENTAGEEARN;
		
		this.multicastSocket = new MulticastSocket(port);
	}

	@Override
	public void run() {
		System.out.println("Reward task is now running!");
		byte [] bytes = "Rewards have been now calculated!".getBytes(StandardCharsets.US_ASCII);
		
		while(true) {
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				multicastSocket.close();
				return;
			}
			
			try {
				db.updateRewards(db.calculateGains(), authorPercentage);
			} catch (IllegalArgumentException | ClientNotRegisteredException | InvalidAmountException e1) {
				System.err.println(e1.getMessage());
			}
			
			DatagramPacket message = new DatagramPacket(bytes, bytes.length, multicastAddress, port);
			try{
				multicastSocket.send(message);
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
