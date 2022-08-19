package client.post_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import utility.TypeError;

public class DeletePostRequest {

	public static void performDeletePostAction(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
		if(requestSplitted.length != 2)
		
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
				System.err.println("Number of arguments insert for delete post operation is not valid, you must type only: delete <idPost>");
				return;
			}else if(error.equals(TypeError.CLIENTNOTLOGGED)) {
				System.err.println("You can't do this operation because you are not logged in Winsome");
				return;
			}else if(error.equals(TypeError.IDPOSTNOTEXISTS)) {
				System.err.println("You can't delete this post because it doesn't exists");
				return;
			}else if(error.equals(TypeError.DELETEPOSTFEEDERROR)) {
				System.err.println("You can't delete this post because it is in your feed");
				return;
			}else if(error.equals(TypeError.POSTNOTINYOURBLOG)) {
				System.err.println("You can't delete this post because it isn't in your blog");
				return;
			}else if(error.equals(TypeError.SUCCESS)) {
				System.out.println("Post identified by id " + idPost + " is removed from Winsome");
				return;
			}
		}catch(NumberFormatException e) {
			System.err.println("The parameter " + requestSplitted[1] + " isn't a parsable integer");
		}
	}
	
}
