package client.post_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import utility.TypeError;

/**
 * Class used to send and receive rate post request and response.
 * @author Gianmarco Petrocchi.
 */
public class RatePostRequest {

	/**
	 * Static method used to send and receive rate post request and response, only when the client is already logged. It sends a request with this syntax: rate:idPost:vote, if this request is different from this syntax the client will receive
	 * INVALIDREQUESTERROR. The idPost specified in request must be of a existed post in Winsome, otherwise the client will receive VOTEPOSTNOTEXISTS. A user can vote only post in his feed, not post in his blog. 
	 * The vote specified in request must be an Integer, 1 or -1 not other number otherwise the client will receive VOTENUMBERNOTVALID error.
	 * @param requestSplitted Client request.
	 * @param writerOutput BufferedWriter used to write/send request to server.
	 * @param readerInput BufferedReader used to read/receive response by server.
	 * @throws IOException Only when occurs I/O error.
	 */
	public static void performRatePostAction(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
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
				System.err.println("Number of arguments insert for rate post operation is not valid, you must type only: rate <id post> <vote> (vote must be 1 or -1)");
				return;
			}else if(error.equals(TypeError.CLIENTNOTLOGGED)) {
				System.err.println("You can't do this operation because you are not logged in Winsome");
				return;
			}else if(error.equals(TypeError.VOTEPOSTNOTEXISTS)) {
				System.err.println("You can't vote this post because it doesn't exists");
				return;
			}else if(error.equals(TypeError.VOTENUMBERNOTVALID)) {
				int vote = Integer.parseInt(requestSplitted[2]);
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
				int idPost = Integer.parseInt(requestSplitted[1]);
				int vote = Integer.parseInt(requestSplitted[2]);
				System.out.println("Add vote " + vote + " to post " + idPost);
				return;
			}
		}catch(NumberFormatException e) {
			System.err.println("Arguments insert for rate post operation aren't parsable integer");
		}
	}
	
}
