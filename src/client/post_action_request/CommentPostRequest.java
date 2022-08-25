package client.post_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import utility.TypeError;

public class CommentPostRequest {

	public static void performCommentPostAction(String [] requestSplitted, String [] takeComment, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
		
		try {
			if(takeComment.length != 2) {
				System.err.println("Number of arguments insert for add comment operation is not valid, you must type only: comment <id post> <comment>");		
				return;
			}
			
			int idPost = Integer.parseInt(requestSplitted[1]);
			String contentComment = takeComment[1];
			
			StringBuilder request = new StringBuilder();
			
			request.append("comment").append(":").append(idPost).append(":").append(contentComment);
			
			writerOutput.write(request.toString());
			writerOutput.newLine();
			writerOutput.flush();
			
			String error = readerInput.readLine();
			
			if(error.equals(TypeError.CLIENTNOTLOGGED)) {
				System.err.println("You can't do this operation because you are not logged in Winsome");
				return;
			}else if(error.equals(TypeError.IDPOSTNOTEXISTS)) {
				System.err.println("You can't comment this post because it doesn't exists");
				return;
			}else if(error.equals(TypeError.POSTINYOURBLOG)) {
				System.err.println("You can't comment your post, you can comment only posts present in your feed");
				return;
			}else if(error.equals(TypeError.POSTNOTINYOURFEED)) {
				System.err.println("You can't comment this post because it isn't in your feed");
				return;
			}else if(error.equals(TypeError.SUCCESS)) {
				System.out.println("Add comment to post " + idPost);
				return;
			}
		}catch(NumberFormatException e) {
			System.err.println("Parameter id post isn't a parsable integer");
		}
	}
	
}
