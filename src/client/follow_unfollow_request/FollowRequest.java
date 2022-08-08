package client.follow_unfollow_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import utility.TypeError;

public class FollowRequest {

	public static void performAddFollowerAction(String[] requestSplitted, BufferedReader readerInput, BufferedWriter writerOutput) throws IOException {
		if(requestSplitted.length != 2)
			throw new IllegalArgumentException("Number of arguments insert for following operation is not valid, you must type only: follow <username>");
		
		String username = requestSplitted[1];
		StringBuilder requestClient = new StringBuilder();
		
		requestClient.append("follow").append(":").append(username);
		
		writerOutput.write(requestClient.toString());
		writerOutput.newLine();
		writerOutput.flush();
		
		String response = readerInput.readLine();
		
		if(response.equals(TypeError.FOLLOWERNOTEXISTS))
			System.err.println("You can't follow user " + username + " because he doesn't exists in Winsome");
			
		if(response.equals(TypeError.FOLLOWERERROR))
			System.err.println("You can't follow user " + username + " because you already followed him");
		
		if(response.equals(TypeError.FOLLOWHIMSELFERROR))
			System.err.println("You can't follow yourself");
		
		if(response.equals(TypeError.SUCCESS))
			System.out.println("Now you are following user " + username);
		
		return;
	}
	
}
