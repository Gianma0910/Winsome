package client.post_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import utility.TypeError;

public class RewinPostRequest {

	public static void performRewinPostAction(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
		if(requestSplitted.length != 2)
			throw new IllegalArgumentException("Number of arguments insert for rewin post operation is not valid, you must type only: rewin <id post>");
		
		try {
			int idPost = Integer.parseInt(requestSplitted[1]);
			
			StringBuilder request = new StringBuilder();
		
			for(int i = 0; i < requestSplitted.length; i++) {
				request.append(requestSplitted[i]);
				
				if(i < requestSplitted.length - 1)
					request.append(":");
			}
			
			writerOutput.write(request.toString());
			writerOutput.newLine();
			writerOutput.flush();
			
			String error = readerInput.readLine();
			
			if(error.equals(TypeError.INVALIDREQUESTERROR)) {
				System.err.println("Number of arguments insert for rewin post operation is not valid, you must type only: rewin <id post>");
				return;
			}else if(error.equals(TypeError.CLIENTNOTLOGGED)) {
				System.err.println("You can't do this operation because you are not logged in Winsome");
				return;
			}if(error.equals(TypeError.IDPOSTNOTEXISTS)) {
				System.err.println("You can't rewin this post because it doesn't exists");
				return;
			}else if(error.equals(TypeError.POSTINYOURBLOG)) {
				System.err.println("You can't rewin your post, you must rewin only posts present in your feed");
				return;
			}else if(error.equals(TypeError.POSTNOTINYOURFEED)) {
				System.err.println("You can't rewin this post because it isn't in your feed");
				return;
			}else if(error.equals(TypeError.SUCCESS)) {
				System.out.println("You rewin post " + idPost);
				return;
			}
		}catch(NumberFormatException e) {
			System.err.println("Parameter id post is not a parsable integer");
		}
	}
	
}
