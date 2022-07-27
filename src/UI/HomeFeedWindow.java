package UI;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JFrame;

public class HomeFeedWindow extends JFrame implements ActionListener{

	private Socket clientSocket;
	private BufferedWriter writerOutput;
	private BufferedReader readerInput;
	
	public HomeFeedWindow(Socket clientSocket) {
		try {
			this.clientSocket = clientSocket;
			this.writerOutput = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			this.readerInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			this.setBounds(0, 0, (int) dim.getWidth(), (int) dim.getHeight()) ;
			this.setResizable(false);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setTitle("Winsome");
			this.setLocationRelativeTo(null);
			
			
			
			
		}catch(IOException e) {
			System.err.println(e.getMessage());
		}
		
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
