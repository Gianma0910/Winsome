package client.post_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import utility.TypeError;

/**
 * Class that perform rewin post request.
 * @author Gianmarco Petrocchi.
 *
 */
public class RewinPostRequest {

	/**
	 * Static class used to rewin post action, only when the client is already logged. It sends a request with this syntax: rewin:idPost, if the request is different from this syntax the client will receive
	 * INVALIDREQUESTERROR. The idPost must be of a existed post in Winsome, otherwise the client will receive IDPOSTNOTEXISTS. A user can rewin only post in his feed, not post in his blog.
	 * @param requestSplitted Client request.
	 * @param writerOutput BufferedWriter used to write/send request to server.
	 * @param readerInput BufferedReader used to read/receive response by server.
	 * @throws IOException Only when occurs I/O error.
	 */
	public static void performRewinPostAction(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
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
				int idPost = Integer.parseInt(requestSplitted[1]);
				System.out.println("You rewin post " + idPost);
				return;
			}
		}catch(NumberFormatException e) {
			System.err.println("Parameter id post is not a parsable integer");
		}
	}
	
}
