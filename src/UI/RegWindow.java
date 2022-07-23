package UI;

import java.awt.Color;
import java.awt.Component;
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
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import RMI.RMIRegistration;
import configuration.ClientConfiguration;
import utility.TypeError;

public class RegWindow extends JFrame implements ActionListener{
	/** Frame's title */
	private JLabel title = new JLabel("Welcome to Winsome! This is the register window");
	private JPanel panel = new JPanel();
	private JPanel panelError = new JPanel();
	/** Label where will set some error text during the registration */
	private JLabel labelError = new JLabel();
	/** Textfield to insert the username */
	private JTextField username = new JTextField();
	/** PasswordFiels to insert the password */
	private JPasswordField password = new JPasswordField();
	/** Textifield to insert the first tag */
	private JTextField tag1 = new JTextField();
	/** Textifield to insert the second tag */
	private JTextField tag2 = new JTextField();
	/** Textifield to insert the third tag */
	private JTextField tag3 = new JTextField();
	/** Textifield to insert the fourth tag */
	private JTextField tag4 = new JTextField();
	/** Textifield to insert the fifth tag */
	private JTextField tag5 = new JTextField();
	/** JButton when pressed allow a user to register*/
	private JButton regButton = new JButton("Register");
	/** JButton when pressed open the LoginWindow. It must be used only when you are already registred */
	private JButton alreadyRegistered = new JButton("Already registered? Click here");
	
	private JLabel text1 = new JLabel("Insert username:");
	private JLabel text2 = new JLabel("Insert password:");
	private JLabel text3 = new JLabel("Insert tag 1:");
	private JLabel text4 = new JLabel("Insert tag 2:");
	private JLabel text5 = new JLabel("Insert tag 3:");
	private JLabel text6 = new JLabel("Insert tag 4:");
	private JLabel text7 = new JLabel("Insert tag 5:");
	
	private ClientConfiguration clientConf;
	
	public RegWindow(ClientConfiguration clientConf) {
		this.clientConf = clientConf;
		
		//setting some properties for frame
		this.setSize(600, 600);
		this.setTitle("Winsome");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		
		panel.setBounds(50, 50, 530, 530);
		panel.setLayout(new GridBagLayout());
		panel.setOpaque(true);
		
		//set the dimensione for labelError
		labelError.setPreferredSize(new Dimension(200, 20));
		//set the color of the writing
		labelError.setForeground(Color.red);
		labelError.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelError.setAlignmentY(Component.CENTER_ALIGNMENT);
		labelError.setOpaque(true);
		
		//add the labelError to panelError
		panelError.add(labelError);

		//set the dimension for TextField "username"
		username.setPreferredSize(new Dimension(200, 30));
		
		//set the dimension for TextField "password"
		password.setPreferredSize(new Dimension(200, 30));
		
		//set some properties for JButton "alreadyRegistered"
		alreadyRegistered.setBorderPainted(false);
		alreadyRegistered.setFocusable(false);
		alreadyRegistered.setContentAreaFilled(false);
		alreadyRegistered.setFont(new Font("MV Boli", Font.PLAIN, 17));
		
		//add an ActionListener to JButton "alreadyRegistered"
		//The ActionListener work when the specified JButton is clicked
		alreadyRegistered.addActionListener(this);
		
		//add an ActionListener to JButton "regButton".
		//The ActionLister work when the specified JButton is clicked
		regButton.addActionListener(this);
		
		//set the dimension for all the TextField tags
		tag1.setPreferredSize(new Dimension(200, 30));
		tag2.setPreferredSize(new Dimension(200, 30));
		tag3.setPreferredSize(new Dimension(200, 30));
		tag4.setPreferredSize(new Dimension(200, 30));
		tag5.setPreferredSize(new Dimension(200, 30));
		
		//Constraints for the GridBagLayout.
		//It is used to arrange the components inside the GridBagLayout.
		GridBagConstraints constraints = new GridBagConstraints();
		
		//In this case, with this kind of constraints, we arrange the components vertically
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(0, 0, 10, 0);
		panel.add(alreadyRegistered, constraints);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(text1, constraints);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.insets = new Insets(0, 0, 10, 0);
		panel.add(username, constraints);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(text2, constraints);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.insets = new Insets(0, 0, 10, 0);
		panel.add(password, constraints);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(text3, constraints);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.insets = new Insets(0, 0, 10, 0);
		panel.add(tag1, constraints);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 7;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(text4, constraints);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 8;
		constraints.insets = new Insets(0, 0, 10, 0);
		panel.add(tag2, constraints);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 9;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(text5, constraints);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 10;
		constraints.insets = new Insets(0, 0, 10, 0);
		panel.add(tag3, constraints);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 11;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(text6, constraints);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 12;
		constraints.insets = new Insets(0, 0, 10, 0);
		panel.add(tag4, constraints);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 13;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(text7, constraints);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 14;
		constraints.insets = new Insets(0, 0, 10, 0);
		panel.add(tag5, constraints);
		
		constraints.fill = GridBagConstraints.NORTHWEST;
		constraints.gridx = 0;
		constraints.gridy = 15;
		constraints.insets = new Insets(0, 0, 0, 10);
		panel.add(regButton, constraints);
		
		constraints.gridx = 5;
		constraints.gridy = 7;
		constraints.insets = new Insets(0, 35, 0, 0);
		panel.add(panelError, constraints);
		
		//set some properties for JLabel "title" of frame
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setVerticalAlignment(JLabel.NORTH);
		title.setFont(new Font("MV Boli", Font.PLAIN, 25));
		
		//add panel with GridBagLayout to frame
		this.add(panel);
		//add title to frame
		this.add(title);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			String usr, pwd, tg1, tg2, tg3, tg4, tg5;
			ArrayList<String> tgs = new ArrayList<String>(5);

			//check if the "regButton" was clicked
			if(e.getSource().equals(regButton)) {
				//get all the text was written in the various TextField
				usr = username.getText();
				pwd = String.valueOf(password.getPassword());
				tg1 = tag1.getText();
				tg2 = tag2.getText();
				tg3 = tag3.getText();
				tg4 = tag4.getText();
				tg5 = tag5.getText();

				tgs.add(tg1);
				tgs.add(tg2);
				tgs.add(tg3);
				tgs.add(tg4);
				tgs.add(tg5);

				try {
					//get RMIRegistry by using RMIREGISTRYHOST and RMIREGISTRYPORT properties of clientConfig
					Registry reg = LocateRegistry.getRegistry(clientConf.RMIREGISTRYHOST, clientConf.RMIREGISTRYPORT);
					//get RMIRegistration service from the RMIRegistry by usin the REGISTRATIONSERVICENAME property of clientConfig
					RMIRegistration service = (RMIRegistration) reg.lookup(clientConf.REGISTRATIONSERVICENAME);

					//call method register of the RMIRegistration service and get the return value of method
					String error = service.register(usr, pwd, tgs);

					//check the value of String error
					if(error.equals(TypeError.USERNAMENULL)) {
						labelError.setText("Username insert is empty");
					}else if(error.equals(TypeError.PWDNULL)) {
						labelError.setText("Password insert is empty");
					}else if(error.equals(TypeError.USRALREADYEXIST)) {
						labelError.setText("User already exists");
					}else if(error.equals(TypeError.SUCCESS)) { //if the register completed successfully
						//Create TCP socket by using SERVERADRRESS and TCPPORT properties of clientConfig
						Socket clientSocket = new Socket(clientConf.SERVERADDRESS, clientConf.TCPPORT);
						//create PrintWriter by using socket's output stream
						BufferedWriter writerOuput = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
						//create BufferedReader by using socket's input stream
						BufferedReader readerInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
						//istantiated LoginWindow with input and output stream and than show the LoginWindow GUI
						LoginWindow loginWindow = new LoginWindow(writerOuput, readerInput);
						loginWindow.setVisible(true);
						this.setVisible(false);
					}
				} catch (RemoteException e1) {
					e1.printStackTrace();
				} catch (NotBoundException e1) {
					e1.printStackTrace();
				}
				
			}else if(e.getSource().equals(alreadyRegistered)) { //check if the "alreadyRegistered" button was clicked
				Socket clientSocket = new Socket(clientConf.SERVERADDRESS, clientConf.TCPPORT);
				BufferedWriter writerOuput = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
				BufferedReader readerInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				LoginWindow loginWindow = new LoginWindow(writerOuput, readerInput);
				loginWindow.setVisible(true);
				this.setVisible(false);
			}
		}catch(IOException ex) {
			System.out.println(ex.getMessage());
		}
	}
	
}
