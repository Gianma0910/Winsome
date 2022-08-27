package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * Thread client side that communicates with server using UDP protocol. 
 * This thread will join a multicast group where will receive notification about gain calculation.
 * @author Gianmarco Petrocchi
 *
 */
public class MulticastClient extends Thread {
	
	/** Multicast UDP socket.*/
	private MulticastSocket multicastSocket;
	/** Multicast group to receive notification.*/
	private InetAddress multicastGroup;
	/** Multicast port.*/
	private int multicastPort;
	/** Flag to see if this thread is still running*/
	private boolean opened;
	
	/**
	 * Basic constructor. Initially the parameters are null or 0 because they will be set after a successfully login.
	 */
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
			String s = new String(packet.getData(), StandardCharsets.US_ASCII); //used only to receive the message of server.
		}
	}
	
	/**
	 *Set the multicast socket and join the multicast group.
	 */
	public void setMulticastSocket() {
		try {
			this.multicastSocket = new MulticastSocket(multicastPort);
			this.multicastSocket.joinGroup(multicastGroup);
		}catch(IOException e) {
			System.err.println("Error in creating the multicast socket and joining the relative group");
			System.err.println(e.getMessage());
		}
	
	}
	
	/**
	 * Set the multicast port.
	 * @param multicastPort Multicast port.
	 */
	public void setMulticastPort(int multicastPort) {
		this.multicastPort = multicastPort;
	}
	
	/**
	 * Set the multicast group.
	 * @param multicastGroup Multicast group.
	 */
	public void setMulticastGroup(InetAddress multicastGroup) {
		this.multicastGroup = multicastGroup;
	}
	
	/**
	 * Interrupt the current thread. It will leave the multicast group and close the multicast socket.
	 */
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
