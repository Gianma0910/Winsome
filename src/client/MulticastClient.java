package client;

import java.net.DatagramSocket;

public class MulticastClient extends Thread {

	private DatagramSocket socketUDP;
	
	public MulticastClient(DatagramSocket socketUDP) {
		this.socketUDP = socketUDP;
	}
	
	public void run() {
		
	}
	
}
