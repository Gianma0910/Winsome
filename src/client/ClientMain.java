package client;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

import RMI.RMIRegistration;
import configuration.ClientConfiguration;
import exceptions.InvalidConfigurationException;
import utility.TypeError;

public class ClientMain {
	
	public static void main(String[] args) throws InvalidConfigurationException, IOException {
		if(args.length != 1) {
			System.err.println("Usage: java ClientMain <path configuration file>\n");
			System.err.println("Check the documentation\n");
			System.exit(0);
		}
		
		String pathConfigurationFile = args[0];
		File configurationFile = new File(pathConfigurationFile);
		
		ClientConfiguration clientConf = new ClientConfiguration(configurationFile);
		
		System.out.println("File properties read successfully\n");
		
		Socket socketTCP = new Socket(clientConf.SERVERADDRESS, clientConf.TCPPORT);
		BufferedWriter writerOutput = new BufferedWriter(new OutputStreamWriter(socketTCP.getOutputStream()));
		BufferedReader readerInput = new BufferedReader(new InputStreamReader(socketTCP.getInputStream()));
				
		String request;
		Scanner scan = new Scanner(System.in);
		boolean shutdown = false;
		
		while(!shutdown) {
			request = scan.nextLine();
			String [] requestSplitted = request.split(" ");
			String command = requestSplitted[0];
			
			switch(command) {
			case "register" : {
				performRegisterAction(requestSplitted, clientConf);
				readerInput = new BufferedReader(new InputStreamReader(socketTCP.getInputStream()));
				writerOutput = new BufferedWriter(new OutputStreamWriter(socketTCP.getOutputStream()));					
				break;
			}
			case "login" : {
				performLoginAction(requestSplitted, clientConf, writerOutput, readerInput);
				break;
			}
			case "logout" : {
				boolean logout = performLogoutAction(command, writerOutput, readerInput);
				if(logout) {
					socketTCP.close();
					scan.close();
					shutdown = true;
				}
				break;
			}
			}
			
		}
		
	}

	private static void performRegisterAction(String [] requestSplitted, ClientConfiguration clientConf) {
		if(requestSplitted.length != 8)
			throw new IllegalArgumentException("Number of arguments insert for the registration operation is invalid, you must type: register <username> <password> <tags> (tags is a list of 5 string)");
		
		String username  = requestSplitted[1];
		String password  = requestSplitted[2];
		String tag1 = requestSplitted[3];
		String tag2 = requestSplitted[4];
		String tag3 = requestSplitted[5];
		String tag4 = requestSplitted[6];
		String tag5 = requestSplitted[7];
		
		ArrayList<String> tagList = new ArrayList<String>();
		tagList.add(tag1);
		tagList.add(tag2);
		tagList.add(tag3);
		tagList.add(tag4);
		tagList.add(tag5);
		
		try {
			Registry reg = LocateRegistry.getRegistry(clientConf.RMIREGISTRYHOST, clientConf.RMIREGISTRYPORT);
			RMIRegistration regService = (RMIRegistration) reg.lookup(clientConf.REGISTRATIONSERVICENAME);
			
			String error = regService.register(username, password, tagList);
			
			if(error.equals(TypeError.USRALREADYEXIST)) {
				System.err.println("User with username " + username + " is already exists");
				return;
			}else if(error.equals(TypeError.SUCCESS)) {
				System.out.println("User with username " + username + " is registered in Winsome");
				return;
			}
		}catch(RemoteException e) {
			System.err.println(e.getMessage());
		} catch (NotBoundException e) {
			System.err.println(e.getMessage());
		}
	}
	

	private static void performLoginAction(String[] requestSplitted, ClientConfiguration clientConf, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
		if(requestSplitted.length != 3)
			throw new IllegalArgumentException("Number of arguments insert for the login operation is invalid, you must type: login <username> <password>");
		
		String username = requestSplitted[1];
		String password = requestSplitted[2];
		
		StringBuilder requestClient = new StringBuilder();
		requestClient.append("login").append(":").append(username).append(":").append(password);
		
		writerOutput.write(requestClient.toString());
		writerOutput.newLine();
		writerOutput.flush();
		
		String response = readerInput.readLine();
		
		if(response.equals(TypeError.PWDWRONG)) {
			System.err.println("Password insert to login is wrong, insert the correct password");
		}else if(response.equals(TypeError.USERNAMEWRONG)) {
			System.err.println("Username insert to login is wrong, insert the correct username");
		}else if(response.equals(TypeError.USRALREADYLOGGED)) {
			System.err.println("A user with the username " + username + " is already logged in Winsome");
		}else if(response.equals(TypeError.SUCCESS)) {
			System.out.println(username + " is logged in Winsome");
			
			System.out.println("Receiving multicast address and multicast port...");
			
			String multicastInfo = readerInput.readLine();
			String [] multicastInfoSplitted = multicastInfo.split(":");
					
			String address = multicastInfoSplitted[0].substring(1, multicastInfoSplitted[0].length());
			InetAddress multicastAddress = InetAddress.getByName(address);
			int multicastPort = Integer.parseInt(multicastInfoSplitted[1]);
			MulticastSocket socketMulticast = new MulticastSocket(multicastPort);
			socketMulticast.joinGroup(multicastAddress);
			
			MulticastClient multicastClient = new MulticastClient(socketMulticast);
		
			System.out.println("User signed for multicast service");
		}
	
		return;
	}
	
	private static boolean performLogoutAction(String command, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
		writerOutput.write(command);
		writerOutput.newLine();
		writerOutput.flush();
		
		String response = readerInput.readLine();
		
		if(response.equals(TypeError.LOGOUTERROR)) {
			System.err.println("Error occurs during logout operation, probably the user is not logged in");
		}else if(response.equals(TypeError.SUCCESS)) {
			System.out.println("The server perform the logout operation successfully");
			
			return true;
		}
		
		return false;
	}
	
}
