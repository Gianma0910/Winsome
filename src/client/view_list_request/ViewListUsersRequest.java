package client.view_list_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import client.UserWrapper;
import utility.TypeError;

/**
 * Class used to send and receive view list users request and response.
 * @author Gianmarco Petrocchi.
 *
 */
public class ViewListUsersRequest {

	/**
	 * Static method used to send and receive view list users request and response, only when the client is already logged. It sends a request with this syntax: list:users, if it is different from this syntax the client
	 * will receive INVALIDREQUESTERRROR. The client will receive by server a lists of users that has at least one tag in common with him. The client will see only the username and tags of this list of users. 
	 * @param requestSplitted Client request.
	 * @param writerOutput BufferedWriter used to write/send request to server.
	 * @param readerInput BufferedReader used to read/receive response by server.
	 * @throws IOException Only when occurs I/O error.
	 */
	public static void performViewListUsers(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
		StringBuilder requestClient = new StringBuilder();
		
		for(int i = 0; i < requestSplitted.length; i++) {
			requestClient.append(requestSplitted[i]);
			
			if(i < requestSplitted.length - 1)
				requestClient.append(":");
		}
		
		writerOutput.write(requestClient.toString());
		writerOutput.newLine();
		writerOutput.flush();
		
		Gson gson = new GsonBuilder().create();
		
		String serializationRegisteredUser = readerInput.readLine();
		
		if(serializationRegisteredUser.equals(TypeError.INVALIDREQUESTERROR)) {
			System.err.println("Number of arguments insert for view list users operation is not valid, you must type only: list users");
			return;
		}else if(serializationRegisteredUser.equals(TypeError.CLIENTNOTLOGGED)) {
			System.err.println("You can't do this operation because you are not logged in Winsome");
			return;
		}else {
			Type listOfUsers = new TypeToken<ArrayList<UserWrapper>>() {}.getType();
			
			ArrayList<UserWrapper> outputUserList = gson.fromJson(serializationRegisteredUser, listOfUsers);
		
			System.out.println("---------------------------------------------------------------------------");
			System.out.println("          List of registerd users with at least one tag in common          ");
			System.out.println("---------------------------------------------------------------------------");
			System.out.printf("%5s %20s", "Username", "Tag list");
			System.out.println();
			System.out.println("---------------------------------------------------------------------------");
			
			for(UserWrapper uw : outputUserList) {
				System.out.format("%7s %20s", uw.getUsername(), uw.getTagList());
				System.out.println();
			}
			
			System.out.println("---------------------------------------------------------------------------");
		}
	}
}
