package client.follow_unfollow_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import utility.TypeError;

public class UnfollowRequest {

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
			username = null;
			System.err.println("You can't unfollow user " + username + " because he doesn't exists in Winsome");
		}
			
		if(response.equals(TypeError.UNFOLLOWERERROR)) {
			username = null;
			System.err.println("You can't unfollow user " + username + " because you already unfollowed him");
		}
			
		if(response.equals(TypeError.UNFOLLOWHIMSELFERROR))
			System.err.println("You can't unfollow yourself");
			
		if(response.equals(TypeError.SUCCESS)) {
			username = null;
			System.out.println("Now you are unfollowing user " + username);
		}
		
		return;
	}
	
}
