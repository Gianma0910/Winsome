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
import utility.TypeError;

public class GetWalletRequest {

	public static void performGetWalletAction(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
		StringBuilder request = new StringBuilder();
		
		for(int i = 0; i < requestSplitted.length; i++) {
			request.append(requestSplitted[i]);
			
			if(i < requestSplitted.length)
				request.append(":");
		}
			
		writerOutput.write(request.toString());
		writerOutput.newLine();
		writerOutput.flush();

		Gson gson = new GsonBuilder().create();
		
		String serializedTransactions = readerInput.readLine();
		
		if(serializedTransactions.equals(TypeError.INVALIDREQUESTERROR)) {
			System.err.println("Number of argument insert for get wallet operation is not valid, you must type only: wallet");
			return;
		}else if(serializedTransactions.equals(TypeError.CLIENTNOTLOGGED)) {
			System.err.println("You can't do this operation because you are not logged in Winsome");
			return;
		}else {
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
		}
		
		return;
	}
	
	
}
