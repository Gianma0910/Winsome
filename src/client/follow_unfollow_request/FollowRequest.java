package client.follow_unfollow_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import utility.TypeError;

/**
 * Class used to send and receive follow request and response.
 * @author Gianmarco Petrocchi.
 *
 */
public class FollowRequest {

	/**
	 * Static method used to send and receive follow request and response, only when the client is already logged. It sends a request with the this syntax: follow:username, if the request is different from this syntax the client will receive a 
	 * INVALIDREQUESTERROR. The username specified in request must be a different user registered in Winsome. A user can't follow himself, otherwise the client receive
	 * a FOLLOWHIMSELFERROR. If the username doesn't exists in Winsome the client will receive a FOLLOWERNOTEXISTS.
	 * @param requestSplitted Client request.
	 * @param readerInput BufferedReader used to read/receive the response by server.
	 * @param writerOutput BufferedWriter used to write/send the request to server. 
	 * @throws IOException Only when occurs I/O error.
	 */
	public static void performAddFollowerAction(String[] requestSplitted, BufferedReader readerInput, BufferedWriter writerOutput) throws IOException {
		
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
			System.err.println("Number of arguments insert for following operation is not valid, you must type only: follow <username>");
		
		if(response.equals(TypeError.CLIENTNOTLOGGED))
			System.err.println("You can't do this operation because you are not logged in Winsome");
		
		if(response.equals(TypeError.FOLLOWERNOTEXISTS)) {
			username = requestSplitted[1];
			System.err.println("You can't follow user " + username + " because he doesn't exists in Winsome");
		}
		
		if(response.equals(TypeError.FOLLOWERERROR)) {
			username = requestSplitted[1];
			System.err.println("You can't follow user " + username + " because you already followed him");
		}
		
		if(response.equals(TypeError.FOLLOWHIMSELFERROR))
			System.err.println("You can't follow yourself");
		
		if(response.equals(TypeError.SUCCESS)) {
			username = requestSplitted[1];
			System.out.println("Now you are following user " + username);
		}
		
		return;
	}
	
}
