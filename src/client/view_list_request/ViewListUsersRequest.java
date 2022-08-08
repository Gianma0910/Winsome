package client.view_list_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import client.UserWrapper;

public class ViewListUsersRequest {

	public static void performViewListUsers(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
		StringBuilder requestClient = new StringBuilder();
		
		requestClient.append(requestSplitted[0]).append(":").append(requestSplitted[1]);
		
		writerOutput.write(requestClient.toString());
		writerOutput.newLine();
		writerOutput.flush();
		
		Gson gson = new GsonBuilder().create();
		
		String serializationRegisteredUser = readerInput.readLine();
		
		Type listOfUsers = new TypeToken<ArrayList<UserWrapper>>() {}.getType();
		
		ArrayList<UserWrapper> outputUserList = gson.fromJson(serializationRegisteredUser, listOfUsers);
	
		System.out.println("---------------------------------------------------------------------------");
		System.out.println("          List of registerd users with at least one tag in common          ");
		System.out.println("---------------------------------------------------------------------------");
		System.out.printf("%5s %20s", "Username", "Tag list");
		System.out.println();
		System.out.println("---------------------------------------------------------------------------");
		
		for(UserWrapper uw : outputUserList) {
			System.out.format("%7s %20s", uw.getUsername(), uw.getTagList());
			System.out.println();
		}
		
		System.out.println("---------------------------------------------------------------------------");
	}
	
}
