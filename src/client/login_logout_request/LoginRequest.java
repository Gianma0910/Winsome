package client.login_logout_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import RMI.RMICallback;
import client.FollowerDatabaseImpl;
import client.MulticastClient;
import configuration.ClientConfiguration;
import utility.TypeError;

public class LoginRequest{

	public static void performLoginAction(String [] requestSplitted, ClientConfiguration clientConf, BufferedWriter writerOutput, BufferedReader readerInput, MulticastClient multicastClient, FollowerDatabaseImpl stubClientDatabase) throws IOException {

		String username = null;
		
		StringBuilder requestClient = new StringBuilder();
		
		for(int i = 0; i < requestSplitted.length; i++) {
			requestClient.append(requestSplitted[i]);
			
			if(i < requestSplitted.length - 1)
				requestClient.append(":");
		}
			
		writerOutput.write(requestClient.toString());
		writerOutput.newLine();
		writerOutput.flush();
		
		String response = readerInput.readLine();
		
		if(response.equals(TypeError.INVALIDREQUESTERROR)) {
			System.err.println("Number of arguments insert for the login operation is invalid, you must type: login <username> <password>");
			return;
		}if(response.equals(TypeError.PWDWRONG)) {
			System.err.println("Password insert to login is wrong, insert the correct password");
			return;
		}else if(response.equals(TypeError.USERNAMEWRONG)) {
			System.err.println("Username insert to login is wrong, insert the correct username");
			return;
		}else if(response.equals(TypeError.USRALREADYLOGGED)) {
			username = requestSplitted[1];
			System.err.println("A user with the username " + username + " is already logged in Winsome");
			return;
		}else if(response.equals(TypeError.CLIENTALREADYLOGGED)) {
			System.err.println("This client is already logged in Winsome");
			return;
		}else if(response.equals(TypeError.SUCCESS)) {
			username = requestSplitted[1];
			System.out.println(username + " is logged in Winsome");
			
			System.out.println("Receiving multicast address and multicast port...");
			
			String multicastInfo = readerInput.readLine();
			String [] multicastInfoSplitted = multicastInfo.split(":");		
			
			int multicastPort = Integer.parseInt(multicastInfoSplitted[1]);
			String address = multicastInfoSplitted[0].substring(1, multicastInfoSplitted[0].length());
			InetAddress multicastAddress = InetAddress.getByName(address);
			
			multicastClient.setMulticastPort(multicastPort);
			multicastClient.setMulticastGroup(multicastAddress);
			multicastClient.setMulticastSocket();
			multicastClient.start();
			
			stubClientDatabase.setUsername(username);
			
			System.out.println("User " + username + " signed for multicast service");
			
			try {
				Registry reg = LocateRegistry.getRegistry(clientConf.RMIREGISTRYHOST, clientConf.RMIREGISTRYPORT);
				RMICallback callbackService = (RMICallback) reg.lookup(clientConf.CALLBACKSERVICENAME);
				
				callbackService.registerForCallback(stubClientDatabase, username);
				
				System.out.println("User " + username + " has just been registered to callback follower/following update service");
			} catch (NotBoundException e) {
				e.printStackTrace();
			}

			StringBuilder requestLoadFollower = new StringBuilder();
			requestLoadFollower.append("load").append(":").append("followers").append(":").append(username);
			
			writerOutput.write(requestLoadFollower.toString());
			writerOutput.newLine();
			writerOutput.flush();
			
			String response2 = readerInput.readLine();
			
			if(response2.equals(TypeError.SUCCESS)) {
				System.out.println("Loading followers list for " + username + " completed successfully");
			}
			
			StringBuilder requestLoadFollowing = new StringBuilder();
			requestLoadFollowing.append("load").append(":").append("following").append(":").append(username);
			
			writerOutput.write(requestLoadFollowing.toString());
			writerOutput.newLine();
			writerOutput.flush();
			
			String response3 = readerInput.readLine();
			
			if(response3.equals(TypeError.SUCCESS)) {
				System.out.println("Loading following list for " + username + " completed successfully");
			}
			
			return;
		}
	}
	
}
