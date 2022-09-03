package client.wallet_action_request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import utility.TypeError;

/**
 * Class used to send and receive get wallet in bitcoin request and response.
 * @author Gianmarco Petrocchi.
 *
 */
public class GetWalletInBitcoinRequest {

	/**
	 * Static method used to send and receive get wallet in bitcoin request and response, only when the client is already logged. It sends a request with the following syntax: wallet:btc, if it is different from this syntax 
	 * the client will receive INVALIDREQUESTERRROR. The client will receive a string that represents his total amount in his wallet converted in bitcoin.
	 * @param requestSplitted Client request.
	 * @param writerOutput BufferedWriter used to write/send request to server.
	 * @param readerInput BufferedReader used to read/receive response by server.
	 * @throws IOException Only when occurs I/O error.
	 */
	public static void performGetWalletInBitcoinAction(String [] requestSplitted, BufferedWriter writerOutput, BufferedReader readerInput) throws IOException{
		
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
			System.err.println("Arguments insert for get wallet in bitcoin is not valid, you must type only: wallet btc");
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
