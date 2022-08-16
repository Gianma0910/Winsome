package server.wallet_service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import server.database.Database;

public class GetWalletImpl implements GetWallet {

	private Database db;
	private BufferedWriter writerOutput;
	
	public GetWalletImpl(Database db, BufferedWriter writerOutput) {
		this.db = db;
		this.writerOutput = writerOutput;
	}
	
	@Override
	public void getWallet(Socket socket) throws IOException {
		String username = db.getUsernameBySocket(socket);
		
		String result = db.getWalletUserJson(username);
		
		writerOutput.write(result);
		writerOutput.newLine();
		writerOutput.flush();
		
		return;
	}

	@Override
	public void getWalletInBitcoin(Socket socket) throws IOException {
		String username = db.getUsernameBySocket(socket);
		
		String result = db.getWalletUserInBitcoin(username);
		
		writerOutput.write(result);
		writerOutput.newLine();
		writerOutput.flush();
		
		return;
	}

}
