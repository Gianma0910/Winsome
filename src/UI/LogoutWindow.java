package UI;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;

import utility.TypeError;

public class LogoutWindow extends JFrame implements ActionListener{
	private JButton logoutButton = new JButton("Logout");

	private BufferedWriter writerOutput;
	private BufferedReader readerInput;
	
	public LogoutWindow(Socket clientSocket) {
		try {
			this.writerOutput = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			this.readerInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			this.setSize(new Dimension(300, 300));
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setResizable(false);
			this.setLocationRelativeTo(null);
			
			logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			logoutButton.setAlignmentY(Component.CENTER_ALIGNMENT);
			logoutButton.addActionListener(this);
			logoutButton.setOpaque(true);
			
			this.add(logoutButton);
		}catch(IOException e) {
			System.err.println(e.getMessage());
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if(e.getSource().equals(logoutButton)) {
				String requestLogout = "logout";
				
				writerOutput.write(requestLogout);
				writerOutput.newLine();
				writerOutput.flush();
				
				String response = readerInput.readLine();
				
				if(response.equals(TypeError.SUCCESS)) {
					System.out.println("Login successfully completed");
				}else if(response.equals(TypeError.LOGOUTERROR)) {
					System.out.println("Error occurs during logout");
				}
			}
		}catch(IOException ex) {
			System.err.println(ex.getMessage());
		}
		
		
	}

}
