package client.wallet_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import utility.Transaction;

public class GetWalletRequest {

	public static void performGetWalletAction(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
		StringBuilder request = new StringBuilder();
		
		request.append(requestSplitted[0]);
		
		writerOutput.write(request.toString());
		writerOutput.newLine();
		writerOutput.flush();

		Gson gson = new GsonBuilder().create();
		
		String serializedTransactions = readerInput.readLine();
		String [] outputSplitted = serializedTransactions.split("@");
		
		Type listOfTransactions = new TypeToken<ArrayList<Transaction>>() {}.getType();
	
		ArrayList<Transaction> userTransactions = gson.fromJson(outputSplitted[0], listOfTransactions);
		
		System.out.println("---------------------------------------------");
		System.out.println("              Your transactions              ");
		System.out.println("---------------------------------------------");
		System.out.printf("%5s %20s", "Amount", "Timestamp");
		System.out.println();
		System.out.println("---------------------------------------------");
		
		for(Transaction t : userTransactions) {
			System.out.printf("%f %30s", t.getAmount(), t.getTimeStamp());
			System.out.println();
		}
		
		System.out.println("---------------------------------------------");
		System.out.println("Your total amount is: " + outputSplitted[1]);
		
		return;
	}
	
	
}
