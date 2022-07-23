package server.threads;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Objects;

import server.database.Database;
import server.login_logout_service.LoginImpl;

/**
 * Thread that connect to client and receive the client request
 * @author Gianmarco Petrocchi
 *
 */
public class TaskHandler implements Runnable {

	private Socket socket;
	private Database db;
	private BufferedWriter writerOutput;
	private BufferedReader readerInput;
	
	/**
	 * Basic constructor of TaskHandler class
	 * @param socket Socket created by ServerSocket.accept(), it communicate with the client. Cannot be null
	 * @param db Database. Cannot be null
	 */
	public TaskHandler(Socket socket, Database db) {
		Objects.requireNonNull(socket, "Socket is null");
		Objects.requireNonNull(db, "Database is null");
		
		this.socket = socket;
		this.db = db;
	
		try {
			this.writerOutput = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.readerInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			System.err.println("Impossible to create input and output stream by client socket");
		}	
	}
	
	@Override
	public void run() {
		System.out.println("Connected " + socket);
		
		try {
			String requestClient;
		
			//read the request client (receive)
			requestClient = readerInput.readLine();
			
			//socket.setSoTimeout(10000);
	
			System.out.println(requestClient);
	
			//split the request client using the caharacter ":"
			String [] requestSplitted = requestClient.split(":");
			//take the first string parsed
			String command = requestSplitted[0];
			
			//check the string command and then execute the right method
			switch(command) {
				case "login" :{
					//take the username written in the request client
					String username = requestSplitted[1];
					//take the password written in the request client
					String password = requestSplitted[2];
					
					//istantiated the LoginImpl
					LoginImpl loginService = new LoginImpl(db);
					
					//call the method login and get the return value of method
					String error = loginService.login(username, password, socket);
					
					//write the String error to client (send)
					writerOutput.write(error);
					writerOutput.newLine();
					writerOutput.flush();
					break;
				}
			}
		}catch(SocketTimeoutException ex) {
			System.err.println(ex.getMessage());
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}

		
		
	}

}
