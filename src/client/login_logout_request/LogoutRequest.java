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
			
			return true;
		}
		
		return false;
	}
	
}
