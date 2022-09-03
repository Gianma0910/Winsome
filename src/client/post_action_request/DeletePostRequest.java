package client.post_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import utility.TypeError;

/**
 * Class used to send and receive delete post request and response.
 * @author Gianmarco Petrocchi.
 */
public class DeletePostRequest {

	/**
	 * Static method used to send and receive delete post request and response, only when the client is already logged. It sends a request with this syntax: delete:idPost, if the request is different from this syntax the client will receive
	 * INVALIDREQUESTERROR. The idPost specified in request must be of a existed post in Winsome, otherwise the client will receive IDPOSTNOTEXISTS error. A user can delete only post in his blog, not post in his feed.
	 * @param requestSplitted Client request.
	 * @param writerOutput BufferedWriter used to write/send request to server.
	 * @param readerInput BufferedReader used to read/receive response by server.
	 * @throws IOException Only when occurs I/O error.
	 */
	public static void performDeletePostAction(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
		
		try {
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
			}else if(error.equals(TypeError.NUMBERFORMATERRROR)) {
				System.err.println("Expected integer argument in request");
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
				int idPost = Integer.parseInt(requestSplitted[1]);
				System.out.println("Post identified by id " + idPost + " is removed from Winsome");
				return;
			}
		}catch(NumberFormatException e) {
			System.err.println("The parameter " + requestSplitted[1] + " isn't a parsable integer");
		}
	}
	
}
