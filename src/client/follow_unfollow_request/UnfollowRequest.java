package client.follow_unfollow_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import utility.TypeError;

public class UnfollowRequest {

	public static void performRemoveFollowerAction(String[] requestSplitted, BufferedReader readerInput, BufferedWriter writerOutput) throws IOException {
		if(requestSplitted.length != 2)
			throw new IllegalArgumentException("Number of arguments insert for unfollow operation is not valid, you must type only: unfollow <username>");
		
		String username = requestSplitted[1];
		StringBuilder requestClient = new StringBuilder();
		
		requestClient.append("unfollow").append(":").append(username);
		
		writerOutput.write(requestClient.toString());
		writerOutput.newLine();
		writerOutput.flush();
		
		String response = readerInput.readLine();
		
		if(response.equals(TypeError.UNFOLLOWINGNOTEXISTS))
			System.err.println("You can't unfollow user " + username + " because he doesn't exists in Winsome");
		
		if(response.equals(TypeError.UNFOLLOWERERROR))
			System.err.println("You can't unfollow user " + username + " because you already unfollowed him");
		
		if(response.equals(TypeError.UNFOLLOWHIMSELFERROR))
			System.err.println("You can't unfollow yourself");
			
		if(response.equals(TypeError.SUCCESS))
			System.out.println("Now you are unfollowing user " + username);
		
		return;
	}
	
}
