package server.wallet_service;

import java.io.IOException;
import java.net.Socket;

public interface GetWallet {

	void getWallet(Socket socket) throws IOException;

	void getWalletInBitcoin(Socket socket) throws IOException;
}
