package client.post_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

import utility.TypeError;

/**
 * Class used to send and receive add comment request and response.
 * @author Gianmarco Petrocchi.
 */
public class CommentPostRequest {

	/**
	 * Static method used to send and receive add comment request and response, only when the client is already logged. It sends a request with this syntax: comment:idPost, if the request is different from this syntax the client
	 * will receive a INVALIDREQUESTERROR. After a successfully response receive from server, the client write in System.in the content of comment, this string is encoded in Base64 with StandardCharset.UTF_8 and then sends to server.
	 * With this char set the client could use all possible characters.The idPost specified must be of a existed post in Winsome, otherwise the client will receive IDPOSTNOTEXISTS error. A user can comment only post in his feed, not post in his blog.
	 * @param requestSplitted Client request.
	 * @param writerOutput BufferedWriter used to write/send request to server.
	 * @param readerInput BufferedReader used to read/receive response by server.
 	 * @param scan Scanner used to write in System.in for content comment.
	 * @throws IOException Only when occurs I/O error.
	 */
	public static void performCommentPostAction(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput, Scanner scan) throws IOException {
		
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
			
			if(error.equals(TypeError.CLIENTNOTLOGGED)) {
				System.err.println("You can't do this operation because you are not logged in Winsome");
				return;
			}else if(error.equals(TypeError.INVALIDREQUESTERROR)) {
				System.err.println("Number of arguments insert for add comment operation is not valid, you must only type: comment <id post>");
				return;
			}else if(error.equals(TypeError.SUCCESS)){
				int idPost = Integer.parseInt(requestSplitted[1]);
				
				System.out.print("Insert the content of comment:\t");
				String contentComment = scan.nextLine();
				
				String encodedContentComment = Base64.getEncoder().encodeToString(contentComment.getBytes(StandardCharsets.UTF_8));
				
				writerOutput.write(encodedContentComment);
				writerOutput.newLine();
				writerOutput.flush();
				
				String error2 = readerInput.readLine();
				
				if(error2.equals(TypeError.IDPOSTNOTEXISTS)) {
					System.err.println("You can't comment this post because it doesn't exists");
					return;
				}else if(error2.equals(TypeError.POSTINYOURBLOG)) {
					System.err.println("You can't comment your post, you can comment only posts present in your feed");
					return;
				}else if(error2.equals(TypeError.POSTNOTINYOURFEED)) {
					System.err.println("You can't comment this post because it isn't in your feed");
					return;
				}else if(error2.equals(TypeError.SUCCESS)) {
					System.out.println("Add comment to post " + idPost);
					return;
				}
			}
		}catch(NumberFormatException e) {
			System.err.println("Parameter id post isn't a parsable integer");
		}
	}
	
}
