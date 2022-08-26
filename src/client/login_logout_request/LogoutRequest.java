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

/**
 * Class that perform logout request.
 * @author Gianmarco Petrocchi.
 *
 */
public class LogoutRequest {

	/**
	 * Static method used to perform logout action, only when the client is already logged otherwise it will receive a CLIENTNOTLOGGED error. It sends a request with this syntax: logout, if the request is different from this syntax
	 * the client will receive a INVALIDREQUESTERROR error. After logout the client interrupt MulticastClient thread, unregister his stub to the callback service and close the socket.
	 * @param requestSplitted Client request.
 	 * @param writerOutput BufferedWriter used to write/send a request to server.
	 * @param readerInput BufferedReader used to read/receive the response by server.
 	 * @param multicastClient MulticastClient thread that must to be interrupted after logout.
	 * @param clientConf Client configuration.
	 * @param socketTCP Socket TCP that must be close after logout.
	 * @param stubClientDatabase Local storage of client that contains followers and following of a user. This two lists must be updated with a callback. In this case the method uses the stub to unregister from callback service.
	 * @return true if logout operation is successfully completed, false otherwise.
	 * @throws NotBoundException Only when the lookup in the registry failed.
	 * @throws ClientNotRegisteredException Only when a user isn't registered (doesn't exists is stub).
	 * @throws IOException Only when occurs I/O error.
	 */
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
