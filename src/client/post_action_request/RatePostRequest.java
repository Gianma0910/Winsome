package client.post_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import utility.TypeError;

public class RatePostRequest {

	public static void performRatePostAction(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
		if(requestSplitted.length != 3)
			throw new IllegalArgumentException("Number of arguments insert for rate post operation is not valid, you must type only: rate <id post> <vote> (vote must be 1 or -1)");
		
		try {
			int idPost = Integer.parseInt(requestSplitted[1]);
			int vote = Integer.parseInt(requestSplitted[2]);
			
			StringBuilder request = new StringBuilder();
			request.append("rate").append(":").append(idPost).append(":").append(vote);
			
			writerOutput.write(request.toString());
			writerOutput.newLine();
			writerOutput.flush();
			
			String error = readerInput.readLine();
			
			if(error.equals(TypeError.VOTEPOSTNOTEXISTS)) {
				System.err.println("You can't vote this post because it doesn't exists");
				return;
			}else if(error.equals(TypeError.VOTENUMBERNOTVALID)) {
				System.err.println("Arguments " + vote + " is not valid, it must be 1 or -1");
				return;
			}else if(error.equals(TypeError.VOTEALREADYEXISTS)) {
				System.err.println("You can't vote this post because you alredy give a vote to this");
				return;
			}else if(error.equals(TypeError.VOTEAUTHORPOST)) {
				System.err.println("You can't vote your post, you can vote post in your feed");
				return;
			}else if(error.equals(TypeError.VOTEPOSTNOTINFEED)) {
				System.err.println("You can't vote this post because it isn't in your feed");
				return;
			}else if(error.equals(TypeError.SUCCESS)) {
				System.out.println("Add vote " + vote + " to post " + idPost);
				return;
			}
		}catch(NumberFormatException e) {
			System.err.println("Arguments insert for rate post operation aren't parsable integer");
		}
	}
	
}
