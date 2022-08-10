package client.post_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import client.PostWrapperShow;
import utility.TypeError;

public class ShowPostRequest {

	public static void performShowPostAction(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
		if(requestSplitted.length != 3)
			throw new IllegalArgumentException("Number of arguments insert for show post operation is invalid, you must type only: show post <idPost>");
	
		try {
			int idPost = Integer.parseInt(requestSplitted[2]);

			StringBuilder request = new StringBuilder();

			request.append(requestSplitted[0]).append(":").append(requestSplitted[1]).append(":").append(idPost);

			writerOutput.write(request.toString());
			writerOutput.newLine();
			writerOutput.flush();

			String error = readerInput.readLine();

			if(error.equals(TypeError.IDPOSTNOTEXISTS)) {
				System.err.println("The specified idPost doesn't exists");
				return;
			}else if(error.equals(TypeError.SUCCESS)){
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
				System.out.println("Number comments: " + pws.getNumberComments());
				System.out.println("-------------------------------------");

				String error2 = readerInput.readLine();

				if(error2.equals(TypeError.POSTINYOURBLOG)) {
					System.out.println("Do you want to delete this post? If yes, use the delete command");
					return;
				}else if(error.equals(TypeError.POSTINYOURFEED)) {
					System.out.println("This post is in your feed, you can add vote or comment. Use the right command");
					return;
				}
			}
		}catch(NumberFormatException e) {
			System.err.println("The parameter " + requestSplitted[2] + " isn't a parsable integer");
		}
	}
	
}
