package UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import utility.TypeError;

public class LoginWindow extends JFrame implements ActionListener{
	/** TextField to insert username*/
	private JTextField username = new JTextField();
	/** PasswordField to insert password*/
	private JPasswordField password = new JPasswordField();
	
	private JLabel text1 = new JLabel("Insert username:");
	private JLabel text2 = new JLabel("Insert password:");
	/** Title of frame*/
	private JLabel title = new JLabel("This is the login window");
	/** Label where will set some error text during the login*/
	private JLabel error = new JLabel();
	
	/** JButton when pressed allow a user to login*/
	private JButton loginButton = new JButton("Login");
	
	private JPanel panel = new JPanel();
	private JPanel panelError = new JPanel();
		
	private BufferedWriter writerOutput;
	private BufferedReader readerInput;
	private Socket clientSocket;
	
	public LoginWindow(Socket clientSocket) {
		try {
			this.clientSocket = clientSocket;
			this.writerOutput = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			this.readerInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			//set some properties of frame
			this.setSize(new Dimension(400, 400));
			this.setLocationRelativeTo(null);
			this.setTitle("Winsome");
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setResizable(false);

			//set some properties of title frame
			title.setHorizontalAlignment(JLabel.CENTER);
			title.setVerticalAlignment(JLabel.NORTH);
			title.setFont(new Font("MV Boli", Font.PLAIN, 25));

			//set the color of writing of JLabel "error"
			error.setForeground(Color.red);
			//set the dimension of JLabel "error"
			error.setPreferredSize(new Dimension(400, 50));
			error.setFont(new Font(null, Font.PLAIN, 22));
			error.setHorizontalAlignment(JLabel.CENTER);
			error.setVerticalAlignment(JLabel.CENTER);
			error.setOpaque(true);

			//set coordinates and dimension of JPanel
			panel.setBounds(0, 50, 400, 200);
			panel.setLayout(new GridBagLayout());
			panel.setOpaque(true);

			panelError.setBounds(0, 255, 400, 100);
			panelError.setOpaque(true);

			//add JLabel "error" to panelError
			panelError.add(error);

			//constraints for the GridBagLayout
			//It is used to arrenge the components inside the GridBagLayout
			GridBagConstraints constraints = new GridBagConstraints();

			//set the dimensione of the two JTextField
			username.setPreferredSize(new Dimension(200, 30));
			password.setPreferredSize(new Dimension(200, 30));

			//In this case, with this kind of constraints, we arrange the components vertically

			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.insets = new Insets(0, 0, 0, 0);
			panel.add(text1, constraints);

			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.gridx = 0;
			constraints.gridy = 1;
			constraints.insets = new Insets(0, 0, 10, 0);
			panel.add(username, constraints);

			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.gridx = 0;
			constraints.gridy = 2;
			constraints.insets = new Insets(0, 0, 0, 0);
			panel.add(text2, constraints);

			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.gridx = 0;
			constraints.gridy = 3;
			constraints.insets = new Insets(0, 0, 10, 0);
			panel.add(password, constraints);

			constraints.fill = GridBagConstraints.NORTHWEST;
			constraints.gridx = 0;
			constraints.gridy = 4;
			constraints.insets = new Insets(0, 0, 0, 0);
			panel.add(loginButton, constraints);

			//add an ActionLister to JButton "loginButton"
			//The ActionListener work when the specified JButton is clicked
			loginButton.addActionListener(this);

			//add components to the frame
			this.add(panelError);
			this.add(panel);
			this.add(title);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
}

	@Override
	public void actionPerformed(ActionEvent event) {
		try {
			String usr, pwd;
			String requestLogin;
			String response;

			//check if the "loginButton" was clicked
			if(event.getSource().equals(loginButton)) {
				if(username.getText().trim().isEmpty() || String.valueOf(password.getPassword()).trim().isEmpty()) {
					error.setText("Empty parameter");
				}else {
					//get all the text was written int the two JTextField 
					usr = username.getText();
					pwd = String.valueOf(password.getPassword());
					//build the login request
					requestLogin = "login:" + usr + ":" + pwd;

					//write the login request to the server (send)
					writerOutput.write(requestLogin);
					writerOutput.newLine();
					writerOutput.flush();

					System.out.println(requestLogin);

					//read the server response (receive)
					response = readerInput.readLine();

					System.out.println(response);

					//check the value of String response
					if(response.equals(TypeError.PWDWRONG)) {
						error.setText("Password insert is wrong");
					}else if(response.equals(TypeError.USERNAMEWRONG)) {
						error.setText("Username insert is wrong");
					}else if(response.equals(TypeError.USRALREADYLOGGED)) {
						error.setText("This user is already logged in Winsome");
					}else if(response.equals(TypeError.SUCCESS)) {
						error.setForeground(Color.green);
						error.setText("Login completed successfully");
						LogoutWindow logoutWindow = new LogoutWindow(clientSocket);
						logoutWindow.setVisible(true);
						this.setVisible(false);
					}
				}
			}	
		}catch(IOException e) {
			System.err.println(e.getMessage());
		}	
	}
}
