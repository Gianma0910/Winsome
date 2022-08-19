package client.wallet_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import utility.TypeError;

public class GetWalletInBitcoinRequest {

	public static void performGetWalletInBitcoinAction(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
		if(!requestSplitted[1].equals("btc"))
			throw new IllegalArgumentException("If you want to get your wallet in Bitcoin, you must type the keyword btc");
		
		StringBuilder request = new StringBuilder();
		
		for(int i = 0; i < requestSplitted.length; i++) {
			request.append(requestSplitted[i]);
			
			if(i < requestSplitted.length - 1)
				request.append(":");
		}
		
		writerOutput.write(request.toString());
		writerOutput.newLine();
		writerOutput.flush();
	
		String walletConverted = readerInput.readLine();
			
		if(walletConverted.equals(TypeError.INVALIDREQUESTERROR)) {
			System.err.println("Number of arguments insert for get wallet in bitcoin is not valid, you must type only: wallet btc");
			return;
		}else if(walletConverted.equals(TypeError.CLIENTNOTLOGGED)) {
			System.err.println("You can't do this operation because you are not logged in Winsome");
			return;
		}else {
			System.out.println("Your wallet in Bitcoin: " + walletConverted);
			return;
		}
	}
	
}
