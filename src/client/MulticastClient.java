package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class MulticastClient extends Thread {
	
	private MulticastSocket multicastSocket;
	private InetAddress multicastGroup;
	private int multicastPort;
	private boolean opened;
	
	public MulticastClient() {
		this.multicastSocket = null;
		this.multicastGroup = null;
		this.multicastPort = 0;
		this.opened = true;
	}
	
	public void run() {
		System.out.println("Multicast client is now running!");
		
		while(opened == true && !Thread.currentThread().isInterrupted()) {
			byte [] bytes = new byte[2048];
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
			try {
				multicastSocket.receive(packet);
			} catch(SocketTimeoutException e) {
				continue;
			}catch (IOException e) {
				System.err.println("Fatal I/O error: now aborting...");
				e.printStackTrace();
				System.exit(1);
			}
			@SuppressWarnings("unused")
			String s = new String(packet.getData(), StandardCharsets.US_ASCII);
		}
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
