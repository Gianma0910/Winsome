package client.wallet_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class GetWalletInBitcoinRequest {

	public static void performGetWalletInBitcoinAction(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException {
		if(!requestSplitted[1].equals("btc"))
			throw new IllegalArgumentException("If you want to get your wallet in Bitcoin, you must type the keyword btc");
		
		StringBuilder request = new StringBuilder();
		request.append(requestSplitted[0]).append(":").append(requestSplitted[1]);
		
		writerOutput.write(request.toString());
		writerOutput.newLine();
		writerOutput.flush();
	
		String walletConverted = readerInput.readLine();
			
		System.out.println("Your wallet in Bitcoin: " + walletConverted);
		
		return;
	}
	
}
