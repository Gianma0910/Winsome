package client.login_logout_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import RMI.RMICallback;
import client.FollowerDatabaseImpl;
import client.MulticastClient;
import configuration.ClientConfiguration;
import exceptions.ClientNotRegisteredException;
import utility.TypeError;

public class LogoutRequest {

	public static boolean performLogoutAction(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput, MulticastClient multicastClient, ClientConfiguration clientConf, Socket socketTCP, FollowerDatabaseImpl stubClientDatabase) throws NotBoundException, ClientNotRegisteredException, IOException {
		
		StringBuilder requestClient = new StringBuilder();
		
		for(int i = 0; i < requestSplitted.length; i++) {
			requestClient.append(requestSplitted[i]);
			
			if(i < requestSplitted.length - 1)
				requestClient.append(":");
		}
		
		writerOutput.write(requestSplitted[0]);
		writerOutput.newLine();
		writerOutput.flush();
		
		String response = readerInput.readLine();
		
		if(response.equals(TypeError.INVALIDREQUESTERROR)) {
			System.err.println("Number of arguments insert for logout operation is not valid, you must type only: logout");
		}else if(response.equals(TypeError.CLIENTNOTLOGGED)) {
			System.err.println("You can't do this operation because you are not logged in Winsome");
		}else if(response.equals(TypeError.LOGOUTERROR)) {
			System.err.println("Error during logout operation");
		}else if(response.equals(TypeError.SUCCESS)) {
			System.out.println("Logout operation complete succesfully");
			multicastClient.interrupt();
			
			Registry reg = LocateRegistry.getRegistry(clientConf.RMIREGISTRYHOST, clientConf.RMIREGISTRYPORT);
			RMICallback callbackService = (RMICallback) reg.lookup(clientConf.CALLBACKSERVICENAME);
			
			callbackService.unregisterForCallback(stubClientDatabase);
			
			socketTCP.close();
			
			return true;
		}
		
		return false;
	}
	
}
