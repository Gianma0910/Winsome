package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastClient extends Thread {
	
	private MulticastSocket multicastSocket;
	private InetAddress multicastGroup;
	private int multicastPort;
	private boolean opened;
	
	public MulticastClient() {
		this.multicastSocket = null;
		this.multicastGroup = null;
		this.multicastPort = 0;
	}
	
	public void run() {
//		while(this.opened == false || currentThread().isInterrupted()) {
//			System.out.println("I'm running...");
//			try {
//				Thread.sleep(3000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	public void setMulticastSocket() {
		try {
			this.multicastSocket = new MulticastSocket(multicastPort);
			this.multicastSocket.joinGroup(multicastGroup);
		}catch(IOException e) {
			System.err.println("Error in creating the multicast socket and joining the relative group");
			System.err.println(e.getMessage());
		}
	
	}
	
	public void setMulticastPort(int multicastPort) {
		this.multicastPort = multicastPort;
	}
	
	public void setMulticastGroup(InetAddress multicastGroup) {
		this.multicastGroup = multicastGroup;
	}
	
	public void interrupt() {
		try {
			this.opened = false;
			multicastSocket.leaveGroup(multicastGroup);
			multicastSocket.close();
		} catch (IOException e) {
			System.err.println("Error in leaving the multicast group");
			System.err.println(e.getMessage());
		}
		
	}
	
	
}
