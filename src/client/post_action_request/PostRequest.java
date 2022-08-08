package client.post_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import utility.TypeError;

public class PostRequest {

	public static void performCreatePost(String[] requestSplitted, BufferedReader readerInput, BufferedWriter writerOutput) throws IOException {
		String titlePost = requestSplitted[1];
		String contentPost = requestSplitted[2];
		
		StringBuilder requestClient = new StringBuilder();
		requestClient.append("post").append(":").append(titlePost).append(":").append(contentPost);
		
		writerOutput.write(requestClient.toString());
		writerOutput.newLine();
		writerOutput.flush();
		
		String response = readerInput.readLine();
		
		if(response.equals(TypeError.TITLELENGTHERROR))
			System.err.println("Title length greater than 20 characters, modify the title if you want publish the post");
		else if(response.equals(TypeError.CONTENTLENGTHERROR))
			System.err.println("Content length greater than 500 characters, modify the content if you want to publish the post");
		else 
			System.out.println(response);
		
		return;
	}
	
}
