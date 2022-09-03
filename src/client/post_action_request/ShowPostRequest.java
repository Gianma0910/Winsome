package client.post_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedHashSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import client.PostWrapperShow;
import utility.Comment;
import utility.TypeError;

/**
 * Class used to send and receive show post request and response.
 * @author Gianmarco Petrocchi.
 */
public class ShowPostRequest {

	/**
	 * Static method used to send and receive show post request and response, only when the client is already logged. It sends a request with this syntax: show:post:idPost, if the request is different from this syntax the client will receive
	 * INVALIDREQUESTERROR. The idPost specified in request must be of a existed post in Winsome, otherwise the client will receive IDPOSTNOTEXISTS error. The client will receive a string that represents the specified post 
	 * and the client serialized its with Gson. The client will see title, content, number of positives and negatives votes, comments of post.
	 * @param requestSplitted Client request.
	 * @param writerOutput BufferedWriter used to write/send request to server.
	 * @param readerInput BufferedReader used to read/receive response by server.
	 * @throws IOException Only when occurs I/O error.
	 */
	public static void performShowPostAction(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {

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
				System.err.println("Number of arguments insert for show post operation is not valid, you must type only: show post <id post>");
				return;
			}else if(error.equals(TypeError.CLIENTNOTLOGGED)) {
				System.err.println("You can't do this operation because you are not logged in Winsome");
				return;
			}else if(error.equals(TypeError.NUMBERFORMATERRROR)) {
				System.err.println("Expected integer argument in request");
				return;
			}else if(error.equals(TypeError.IDPOSTNOTEXISTS)) {
				System.err.println("The specified idPost doesn't exists");
				return;
			}else if(error.equals(TypeError.SUCCESS)){
				int idPost = Integer.parseInt(requestSplitted[2]);
				String serializedPost = readerInput.readLine();

				Gson gson = new GsonBuilder().create();

				PostWrapperShow pws = gson.fromJson(serializedPost, PostWrapperShow.class);

				System.out.println("-------------------------------------");
				System.out.println("            Post " + idPost + "      ");
				System.out.println("-------------------------------------");
				System.out.println("Title: " + pws.getTitle());
				System.out.println("Content: " + pws.getContent());
				System.out.println("Number positive votes: " + pws.getNumberPositiveVotes());
				System.out.println("Number negative votes: " + pws.getNumberNegativeVotes());
				System.out.print("Comments: ");
				
				LinkedHashSet<Comment> comments = pws.getComments();
				for(Comment c : comments)
					System.out.println(          c.getAuthor() + ": " + c.getContent());
				System.out.println();
				System.out.println("-------------------------------------");

				String error2 = readerInput.readLine();

				if(error2.equals(TypeError.POSTINYOURBLOG)) {
					System.out.println("Do you want to delete this post? If yes, use the delete command");
					return;
				}else if(error2.equals(TypeError.POSTINYOURFEED)) {
					System.out.println("This post is in your feed, you can add vote or comment. Use the right command");
					return;
				}
			}
		}catch(NumberFormatException e) {
			System.err.println("The parameter " + requestSplitted[2] + " isn't a parsable integer");
		}
	}
	
}
