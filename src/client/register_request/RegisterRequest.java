package client.register_request;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import RMI.RMIRegistration;
import configuration.ClientConfiguration;
import utility.TypeError;

public class RegisterRequest {

	public static void performRegisterAction(String [] requestSplitted, ClientConfiguration clientConf) {
		if(requestSplitted.length != 8)
			System.err.println("Number of arguments insert for the registration operation is invalid, you must type: register <username> <password> <tags> (tags is a list of 5 string)");
		
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
	
}
