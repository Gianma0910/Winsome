package client.follow_unfollow_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import utility.TypeError;

/**
 * Class used to send and receive unfollow request and response.
 * @author Gianmarco Petrocchi.
 *
 */
public class UnfollowRequest {

	/**
	 * Static used to send and receive unfollow request and response, only when the client is already logged. It sends a request with the this syntax: unfollow:username, if the request is different from this syntax the client will receive a 
	 * INVALIDREQUESTERROR. The username specified in request must be a different user registered in Winsome. A user can't unfollow himself, otherwise the client receive
	 * a UNFOLLOWHIMSELFERROR. If the username doesn't exists in Winsome the client will receive a UNFOLLOWERNOTEXISTS.
	 * @param requestSplitted Client request.
	 * @param readerInput BufferedReader used to read/receive the response by server.
	 * @param writerOutput BufferedWriter used to write/send the request to server.
	 * @throws IOException Only when occurs I/O error.
	 */
	public static void performRemoveFollowerAction(String[] requestSplitted, BufferedReader readerInput, BufferedWriter writerOutput) throws IOException {
		
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
		
		if(response.equals(TypeError.INVALIDREQUESTERROR))
			System.err.println("Number of arguments insert for unfollow operation is not valid, you must type only: unfollow <username>");
		
		if(response.equals(TypeError.CLIENTNOTLOGGED))
			System.err.println("You can't do this operation because you are not logged in Winsome");
		
		if(response.equals(TypeError.UNFOLLOWINGNOTEXISTS)) {
			username = requestSplitted[1];
			System.err.println("You can't unfollow user " + username + " because he doesn't exists in Winsome");
		}
			
		if(response.equals(TypeError.UNFOLLOWERERROR)) {
			username = requestSplitted[1];
			System.err.println("You can't unfollow user " + username + " because you already unfollowed him");
		}
			
		if(response.equals(TypeError.UNFOLLOWHIMSELFERROR))
			System.err.println("You can't unfollow yourself");
			
		if(response.equals(TypeError.SUCCESS)) {
			username = requestSplitted[1];
			System.out.println("Now you are unfollowing user " + username);
		}
		
		return;
	}
	
}
