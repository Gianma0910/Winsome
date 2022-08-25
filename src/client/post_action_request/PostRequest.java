package client.post_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.Scanner;

import utility.TypeError;

public class PostRequest {

	public static void performCreatePost(String command, Scanner scan, BufferedReader readerInput, BufferedWriter writerOutput) throws IOException {
		writerOutput.write(command);
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
