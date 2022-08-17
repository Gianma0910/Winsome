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

public class TaskReward implements Runnable {

	private Database db;
	private MulticastSocket multicastSocket;
	private int interval;
	private InetAddress multicastAddress;
	private int port;
	private double authorPercentage;
	private boolean opened;
	
	public TaskReward(Database db, ServerConfiguration serverConf) throws IOException {
		Objects.requireNonNull(db, "Database is null");
		Objects.requireNonNull(serverConf, "Configuration server is null");
		
		this.db = db;
		this.interval = serverConf.DELAYEARNINGSCALCULATION;
		this.multicastAddress = serverConf.MULTICASTADDRESS;
		this.port = serverConf.MULTICASTPORT;
		this.authorPercentage = serverConf.AUTHORPERCENTAGEEARN;
		
		this.multicastSocket = new MulticastSocket(port);
		this.opened = true;
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
