package server.wallet_services;

import java.io.IOException;
import java.net.Socket;

/**
 * Interface that contains method to handle get wallet service provided by server. 
 * @author Gianmarco Petrocchi.
 *
 */
public interface GetWalletServices {

	/**
	 * Method used to handle get wallet service provided by server. The user will receive a String that contains
	 * all his transactions and his total amount.
	 * Before handle user request it checks if it is logged in Winsome, otherwise the user will receive CLIENTNOTLOGGED error.
	 * @param socket Socket client that send the get wallet request. Cannot be null.
	 * @throws IOException Only when occurs I/O error.
	 */
	void getWallet(Socket socket) throws IOException;

	/**
	 * Method used to handle get wallet in bitcoin provided by server. The user will receive a String that contains
	 * is total amount but converted in Bitcoin.
	 * Before handle user request it checks if it is logged in Winsome, otherwise the user will receive CLIENTNOTLOGGED error.
	 * @param socket Socket client that send the get wallet btc request. Cannot be null.
	 * @throws IOException Only when occurs I/O error.
	 */
	void getWalletInBitcoin(Socket socket) throws IOException;
}
