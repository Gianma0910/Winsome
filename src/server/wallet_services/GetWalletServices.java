package server.wallet_services;

import java.io.IOException;
import java.net.Socket;

public interface GetWalletServices {

	void getWallet(Socket socket) throws IOException;

	void getWalletInBitcoin(Socket socket) throws IOException;
}