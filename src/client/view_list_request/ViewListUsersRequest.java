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
import utility.TypeError;

public class ViewListUsersRequest {

	public static void performViewListUsers(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
		StringBuilder requestClient = new StringBuilder();
		
		for(int i = 0; i < requestSplitted.length; i++) {
			requestClient.append(requestSplitted[i]);
			
			if(i < requestSplitted.length - 1)
				requestClient.append(":");
		}
		
		writerOutput.write(requestClient.toString());
		writerOutput.newLine();
		writerOutput.flush();
		
		Gson gson = new GsonBuilder().create();
		
		String serializationRegisteredUser = readerInput.readLine();
		
		if(serializationRegisteredUser.equals(TypeError.INVALIDREQUESTERROR)) {
			System.err.println("Number of arguments insert for view list users operation is not valid, you must type only: list users");
			return;
		}else if(serializationRegisteredUser.equals(TypeError.CLIENTNOTLOGGED)) {
			System.err.println("You can't do this operation because you are not logged in Winsome");
			return;
		}else {
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
}
