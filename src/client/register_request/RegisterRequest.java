package client.register_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import RMI.RMIRegistration;
import client.FollowerDatabaseImpl;
import client.MulticastClient;
import client.login_logout_request.LoginRequest;
import configuration.ClientConfiguration;
import utility.TypeError;

public class RegisterRequest {

	public static void performRegisterAction(String [] requestSplitted, ClientConfiguration clientConf, BufferedWriter writerOutput, BufferedReader readerInput, MulticastClient multicastClient, FollowerDatabaseImpl stubClientDatabase) throws IOException{
		if(requestSplitted.length < 4 || requestSplitted.length > 8) {
			System.err.println("Number of arguments insert for the registration operation is invalid, you must type: register <username> <password> <tags> (tags is a list of 5 string)");
			return;
		}
		
		String username  = requestSplitted[1];
		String password  = requestSplitted[2];
		
		int lengthRequest = requestSplitted.length;
		ArrayList<String> tagList = new ArrayList<String>();
		
		switch (lengthRequest) {
			case 4: {
				String tag1 = requestSplitted[3];
				tagList.add(tag1);
				break;
			}
			case 5: {
				String tag1 = requestSplitted[3];
				String tag2 = requestSplitted[4];
				tagList.add(tag1);
				tagList.add(tag2);
				break;
			}
			case 6: {
				String tag1 = requestSplitted[3];
				String tag2 = requestSplitted[4];
				String tag3 = requestSplitted[5];
				tagList.add(tag1);
				tagList.add(tag2);
				tagList.add(tag3);
				break;
			}
			case 7: {
				String tag1 = requestSplitted[3];
				String tag2 = requestSplitted[4];
				String tag3 = requestSplitted[5];
				String tag4 = requestSplitted[6];
				tagList.add(tag1);
				tagList.add(tag2);
				tagList.add(tag3);
				tagList.add(tag4);
				break;
			}
			case 8 : {
				String tag1 = requestSplitted[3];
				String tag2 = requestSplitted[4];
				String tag3 = requestSplitted[5];
				String tag4 = requestSplitted[6];
				String tag5 = requestSplitted[7];
				tagList.add(tag1);
				tagList.add(tag2);
				tagList.add(tag3);
				tagList.add(tag4);
				tagList.add(tag5);
				break;
			}
		}
		
		try {
			Registry reg = LocateRegistry.getRegistry(clientConf.RMIREGISTRYHOST, clientConf.RMIREGISTRYPORT);
			RMIRegistration regService = (RMIRegistration) reg.lookup(clientConf.REGISTRATIONSERVICENAME);
			
			String error = regService.register(username, password, tagList);
			
			if(error.equals(TypeError.USRALREADYEXIST)) {
				System.err.println("User with username " + username + " is already exists");
				return;
			}else if(error.equals(TypeError.SUCCESS)) {
				System.out.println("User with username " + username + " is registered in Winsome");
				
				String [] requestLoginSplitted = {"login", username, password};
				
				LoginRequest.performLoginAction(requestLoginSplitted, clientConf, writerOutput, readerInput, multicastClient, stubClientDatabase);
				
				return;
			}
		}catch(RemoteException e) {
			System.err.println(e.getMessage());
		} catch (NotBoundException e) {
			System.err.println(e.getMessage());
		}
	}
	
}