package client.post_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.Scanner;

import utility.TypeError;

/**
 * Class that perform create post request.
 * @author Gianmarco Petrocchi.
 */
public class PostRequest {

	/**
	 * Static method used to create post, only when the client is already logged. It sends a request with this syntax: post, if the request is different from this syntax the client will receive a 
	 * INVALIDREQUESTERROR. After a successfully response received from server, the client write in System.in the title and content of post, this two strings are encoded in Base64 and sends to server.
	 * Title and content of post has a maximum length, 20 characters for title and 500 characters for content.
	 * @param requestSplitted Client request.
	 * @param scan Scanner used to write in System.in for title and content of post.
	 * @param readerInput BufferedReader used to read/receive response by server.
	 * @param writerOutput BufferedWriter used to write/send response to server.
	 * @throws IOException Only when occurs I/O error. 
	 */
	public static void performCreatePost(String [] requestSplitted, Scanner scan, BufferedReader readerInput, BufferedWriter writerOutput) throws IOException {
		StringBuilder request = new  StringBuilder();
		
		for(int i = 0; i < requestSplitted.length; i++) {
			request.append(requestSplitted[i]);
			
			if(i < requestSplitted.length - 1) {
				request.append(":");
			}
		}
		
		writerOutput.write(request.toString());
		writerOutput.newLine();
		writerOutput.flush();
		
		String response = readerInput.readLine();
		
		if(response.equals(TypeError.CLIENTNOTLOGGED)) {
			System.err.println("You can't do this operation because you are not logged in Winsome");
			return;
		}else if(response.equals(TypeError.INVALIDREQUESTERROR)) {
			System.err.println("Number of arguments insert for create post action isn't valid, you must only type: post");
			return;
		}else if(response.equals(TypeError.SUCCESS)) {
			System.out.print("Insert title of post:\t");
			String title = scan.nextLine();
			System.out.print("Insert content of post:\t");
			String content = scan.nextLine();
			
			byte[] encodedTitle = Base64.getEncoder().encode(title.getBytes());
			byte[] encodedContent = Base64.getEncoder().encode(content.getBytes());
			
			String t = new String(encodedTitle, 0, encodedTitle.length);
			String c = new String(encodedContent, 0, encodedContent.length);

			StringBuilder requestClient = new StringBuilder();
			requestClient.append(t).append(" ").append(c);

			writerOutput.write(requestClient.toString());
			writerOutput.newLine();
			writerOutput.flush();

			String response2 = readerInput.readLine();
			
			if(response2.equals(TypeError.TITLELENGTHERROR)) {
				System.err.println("Title legth is greater than 20 characters, modify the title if you want publish this post");
				return;
			}else if(response2.equals(TypeError.CONTENTLENGTHERROR)) {
				System.err.println("Content length is greater than 500 characters, modify the content if you want publish this post");
				return;
			}else
				System.out.println(response2);

		}
	
		return;
	}
	
}
