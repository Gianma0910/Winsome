package server.wallet_service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import server.database.Database;
import utility.TypeError;

public class GetWalletServicesImpl implements GetWalletServices {

	private Database db;
	private BufferedWriter writerOutput;
	
	public GetWalletServicesImpl(Database db, BufferedWriter writerOutput) {
		this.db = db;
		this.writerOutput = writerOutput;
	}
	
	@Override
	public void getWallet(Socket socket) throws IOException {
		if(db.getUserLoggedIn().containsKey(socket) == false) {
			sendError(TypeError.CLIENTNOTLOGGED, writerOutput);
			return;
		}
		
		String username = db.getUsernameBySocket(socket);
		
		String result = db.getWalletUserJson(username);
		
		writerOutput.write(result);
		writerOutput.newLine();
		writerOutput.flush();
		
		return;
	}

	@Override
	public void getWalletInBitcoin(Socket socket) throws IOException {
		if(db.getUserLoggedIn().containsKey(socket) == false) {
			sendError(TypeError.CLIENTNOTLOGGED, writerOutput);
			return;
		}
		
		String username = db.getUsernameBySocket(socket);
		
		String result = db.getWalletUserInBitcoin(username);
		
		writerOutput.write(result);
		writerOutput.newLine();
		writerOutput.flush();
		
		return;
	}

	private void sendError(String error, BufferedWriter writerOutput) throws IOException {
		writerOutput.write(error);
		writerOutput.newLine();
		writerOutput.flush();
	}
	
}
