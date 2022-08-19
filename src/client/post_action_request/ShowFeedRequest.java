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

public class ShowFeedRequest {

	public static void performShowFeedRequest(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {

		StringBuilder request = new StringBuilder();
		
		for(int i = 0; i < requestSplitted.length; i++) {
			request.append(requestSplitted[i]);
			
			if(i < requestSplitted.length - 1)
				request.append(":");
		}
		
		writerOutput.write(request.toString());
		writerOutput.newLine();
		writerOutput.flush();
		
		Gson gson = new GsonBuilder().create();
		
		String serialiazedPosts = readerInput.readLine();
		
		if(serialiazedPosts.equals(TypeError.INVALIDREQUESTERROR)) {
			System.err.println("Number of arguments insert for show feed operation is not valid, you must type only: show feed");
			return;
		}else if(serialiazedPosts.equals(TypeError.CLIENTNOTLOGGED)) {
			System.err.println("You can't do this operation because you are not logged in Winsome");
			return;
		}else {
			Type listOfPosts = new TypeToken<ArrayList<PostWrapper>>() {}.getType();
			
			ArrayList<PostWrapper> feedUser = gson.fromJson(serialiazedPosts, listOfPosts);
		
			System.out.println("----------------------------------------------");
			System.out.println("                   Your feed                 ");
			System.out.println("----------------------------------------------");
		
			for(PostWrapper pw : feedUser) {
				System.out.println("Id post: " + pw.getIdPost());
				System.out.println("Author: " + pw.getAuthor());
				System.out.println("Title: " + pw.getTitle());
				System.out.println("----------------------------------------------");
			}
		}
	
		return;
	}
	
}
