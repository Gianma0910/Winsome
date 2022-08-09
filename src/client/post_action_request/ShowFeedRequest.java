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

public class ShowFeedRequest {

	public static void performShowFeedRequest(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
		if(requestSplitted.length != 2)
			throw new IllegalArgumentException("Number of arguments insert for show feed operation is invalid, you must type only: show feed");
		
		StringBuilder request = new StringBuilder();
		
		request.append(requestSplitted[0]).append(":").append(requestSplitted[1]);
		
		writerOutput.write(request.toString());
		writerOutput.newLine();
		writerOutput.flush();
		
		Gson gson = new GsonBuilder().create();
		
		String serialiazedPosts = readerInput.readLine();
		
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
		
		return;
	}
	
}
