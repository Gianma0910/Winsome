package client;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import RMI.RMICallback;
import RMI.RMIRegistration;
import configuration.ClientConfiguration;
import exceptions.ClientNotRegisteredException;
import exceptions.InvalidConfigurationException;
import utility.TypeError;

public class ClientMain {
	
	public static void main(String[] args) throws InvalidConfigurationException, IOException, NotBoundException, ClientNotRegisteredException {
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
			
		MulticastClient multicastClient = new MulticastClient();
		
		FollowerDatabaseImpl stubClientDatabase = new FollowerDatabaseImpl();
		
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
				break;
			}
			case "login" : {
				performLoginAction(requestSplitted, clientConf, writerOutput, readerInput, multicastClient, stubClientDatabase);
				break;
			}
			case "logout" : {
				if(requestSplitted.length != 1)
					throw new IllegalArgumentException("Number of arguments insert for logout operation is not valid, you must type only: logout");
				
				writerOutput.write(requestSplitted[0]);
				writerOutput.newLine();
				writerOutput.flush();
				
				String response = readerInput.readLine();
				
				if(response.equals(TypeError.LOGOUTERROR)) {
					System.err.println("Error during logout operation");
				}else if(response.equals(TypeError.SUCCESS)) {
					System.out.println("Logout operation complete succesfully");
					multicastClient.interrupt();
					
					Registry reg = LocateRegistry.getRegistry(clientConf.RMIREGISTRYHOST, clientConf.RMIREGISTRYPORT);
					RMICallback callbackService = (RMICallback) reg.lookup(clientConf.CALLBACKSERVICENAME);
					
					callbackService.unregisterForCallback(stubClientDatabase);
					
					socketTCP.close();
					
					shutdown = true;
				}
				
				break;
			}
			case "list": {
				if(requestSplitted.length != 2)
					throw new IllegalArgumentException("Number of arguments insert for view list operation is not valid, you must type: list users, list followers, list following");
				
				if(requestSplitted[1].equals("users")) {
					performViewUsers(requestSplitted, readerInput, writerOutput);
				}else if(requestSplitted[1].equals("followers")) {
					performViewFollowers(stubClientDatabase);
				}else if(requestSplitted[1].equals("following")){
					performViewFollowing(requestSplitted, readerInput, writerOutput);
				}
				
				break;
			}
			case "follow" : {
				performAddFollowerAction(requestSplitted, readerInput, writerOutput);
				break;
			}
			case "unfollow": {
				performRemoveFollowerAction(requestSplitted, readerInput, writerOutput);
				break;
			}
			default: {
				System.err.println("This command doesn't exists, please check the documentation");
				break;
			}
			}
			
		}
		
		scan.close();
		System.exit(1);
		
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
	

	private static void performLoginAction(String [] requestSplitted, ClientConfiguration clientConf, BufferedWriter writerOutput, BufferedReader readerInput, MulticastClient multicastClient, FollowerDatabaseImpl stubClientDatabase) throws IOException {
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
			return;
		}else if(response.equals(TypeError.USERNAMEWRONG)) {
			System.err.println("Username insert to login is wrong, insert the correct username");
			return;
		}else if(response.equals(TypeError.USRALREADYLOGGED)) {
			System.err.println("A user with the username " + username + " is already logged in Winsome");
			return;
		}else if(response.equals(TypeError.SUCCESS)) {
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

			return;
		}
		
	}
	

	private static void performViewUsers(String[] requestSplitted, BufferedReader readerInput, BufferedWriter writerOutput) throws IOException {
		StringBuilder requestClient = new StringBuilder();
		
		requestClient.append(requestSplitted[0]).append(":").append(requestSplitted[1]);
		
		writerOutput.write(requestClient.toString());
		writerOutput.newLine();
		writerOutput.flush();
		
		Gson gson = new GsonBuilder().create();
		
		String serializationRegisteredUser = readerInput.readLine();
		
		Type listOfUsers = new TypeToken<ArrayList<UserWrapper>>() {}.getType();
		
		ArrayList<UserWrapper> outputUserList = gson.fromJson(serializationRegisteredUser, listOfUsers);
	
		System.out.println("--------------------------------------------");
		System.out.printf("%5s %10s", "Username", "Tag list");
		System.out.println();
		System.out.println("--------------------------------------------");
		
		for(UserWrapper uw : outputUserList) {
			System.out.format("%7s %15s", uw.getUsername(), uw.getTagList());
			System.out.println();
		}
		
		System.out.println("--------------------------------------------");

	}
	
	private static void performViewFollowers(FollowerDatabaseImpl stubClientDatabase) {
		ArrayList<String> followers = stubClientDatabase.getFollowers();
		
		System.out.print("Followers: ");
		
		Iterator<String> it = followers.iterator();
		
		while(it.hasNext()) {
			System.out.print(it.next());
			if(it.hasNext())
				System.out.print(", ");
		}
		
		System.out.println();
		
		return;
		
	}
	
	private static void performViewFollowing(String[] requestSplitted, BufferedReader readerInput, BufferedWriter writerOutput) throws IOException {
		StringBuilder requestClient = new StringBuilder();
		
		requestClient.append(requestSplitted[0]).append(":").append(requestSplitted[1]);
		
		writerOutput.write(requestClient.toString());
		writerOutput.newLine();
		writerOutput.flush();
		
		String response = readerInput.readLine();
		
		response = response.substring(1, response.length()-1);
		
		System.out.println("Following: " + response);
	}
	
	private static void performAddFollowerAction(String[] requestSplitted, BufferedReader readerInput, BufferedWriter writerOutput) throws IOException {
		if(requestSplitted.length != 2)
			throw new IllegalArgumentException("Number of arguments insert for following operation is not valid, you must type only: follow <username>");
		
		String username = requestSplitted[1];
		StringBuilder requestClient = new StringBuilder();
		
		requestClient.append("follow").append(":").append(username);
		
		writerOutput.write(requestClient.toString());
		writerOutput.newLine();
		writerOutput.flush();
		
		String response = readerInput.readLine();
		
		if(response.equals(TypeError.FOLLOWERERROR))
			System.err.println("You can't follow user " + username + " because you already followed him");
		
		if(response.equals(TypeError.SUCCESS))
			System.out.println("Now you are following user " + username);
		
		return;
	}
	
	private static void performRemoveFollowerAction(String[] requestSplitted, BufferedReader readerInput, BufferedWriter writerOutput) throws IOException {
		if(requestSplitted.length != 2)
			throw new IllegalArgumentException("Number of arguments insert for unfollow operation is not valid, you must type only: unfollow <username>");
		
		String username = requestSplitted[1];
		StringBuilder requestClient = new StringBuilder();
		
		requestClient.append("unfollow").append(":").append(username);
		
		writerOutput.write(requestClient.toString());
		writerOutput.newLine();
		writerOutput.flush();
		
		String response = readerInput.readLine();
		
		if(response.equals(TypeError.FOLLOWERERROR))
			System.err.println("You can't unfollow user " + username + " because you already unfollowed him");
		
		if(response.equals(TypeError.SUCCESS))
			System.out.println("Now you are unfollowing user " + username);
		
		return;
	}
	

}
