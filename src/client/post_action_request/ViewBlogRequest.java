package client.post_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import client.PostWrapper;
import utility.TypeError;

/**
 * Class used to send and receive view blog request and response.
 * @author Gianmarco Petrocchi.
 *
 */
public class ViewBlogRequest {

	/**
	 * Static method used to send and receive view blog request and response, only when the client is already logged. It sends a request with this syntax: blog, if it is different from this syntax the client will receive
	 * INVALIDREQUESTERROR. The client will receive by server a string that represents the list of his posts and then it serialized its with Gson. The client will see idPost, author and title of posts.
	 * @param request ClientRequest.
	 * @param writerOutput BufferedWriter used to write/send request to server.
	 * @param readerInput BufferedReader used to read/receive response by server.
	 * @throws IOException Only when occurs I/O error.
	 */
	public static void performViewBlogAction(String [] request, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
		
		StringBuilder requestClient = new StringBuilder();
		
		for(int i = 0; i < request.length; i++) {
			requestClient.append(request[i]);
			
			if(i < request.length - 1)
				requestClient.append(":");
		}
		
		writerOutput.write(requestClient.toString());
		writerOutput.newLine();
		writerOutput.flush();
		
		Gson gson = new GsonBuilder().create();
		
		String serializationUserPost = readerInput.readLine();
		
		if(serializationUserPost.equals(TypeError.INVALIDREQUESTERROR)) {
			System.err.println("Number of arguments insert for view blog operation is not valid, you must type only: blog");
			return;
		}else if(serializationUserPost.equals(TypeError.CLIENTNOTLOGGED)) {
			System.err.println("You can't do this operation because you are not logged in Winsome");
			return;
		}else {
			Type listOfPost = new TypeToken<ArrayList<PostWrapper>>() {}.getType();
			
			ArrayList<PostWrapper> userPost = gson.fromJson(serializationUserPost, listOfPost);
			
			System.out.println("----------------------------------------------");
			System.out.println("                   Your post                   ");
			System.out.println("----------------------------------------------");
			
			for(PostWrapper pw : userPost) {
				System.out.println("Id Post: " + pw.getIdPost());
				System.out.println("Author: " + pw.getAuthor());
				System.out.println("Title: " + pw.getTitle());
				System.out.println("----------------------------------------------");
			}
		}
	}
	
}
