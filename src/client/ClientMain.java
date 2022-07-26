package client;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.Socket;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.util.Scanner;

import client.follow_unfollow_request.FollowRequest;
import client.follow_unfollow_request.UnfollowRequest;
import client.help_request.HelpRequest;
import client.login_logout_request.LoginRequest;
import client.login_logout_request.LogoutRequest;
import client.post_action_request.CommentPostRequest;
import client.post_action_request.DeletePostRequest;
import client.post_action_request.PostRequest;
import client.post_action_request.RatePostRequest;
import client.post_action_request.RewinPostRequest;
import client.post_action_request.ShowFeedRequest;
import client.post_action_request.ShowPostRequest;
import client.post_action_request.ViewBlogRequest;
import client.register_request.RegisterRequest;
import client.view_list_request.ViewListFollowersRequest;
import client.view_list_request.ViewListFollowingRequest;
import client.view_list_request.ViewListUsersRequest;
import client.wallet_action_request.GetWalletInBitcoinRequest;
import client.wallet_action_request.GetWalletRequest;
import configuration.ClientConfiguration;
import exceptions.ClientNotRegisteredException;
import exceptions.InvalidConfigurationException;

/**
 * Class that represents a single client/user that will login in Winsome.
 * @author Gianmarco Petrocchi.
 *
 */
public class ClientMain {
	
	public static final String pathConfigurationFile = "src/files/client_configuration.txt";
	
	public static void main(String[] args) throws InvalidConfigurationException, IOException, NotBoundException, ClientNotRegisteredException{
		File configurationFile = new File(pathConfigurationFile);
		
		ClientConfiguration clientConf = new ClientConfiguration(configurationFile);
		
		System.out.println("File properties read successfully\n");

		//set TCP socket and his buffer for read and write
		Socket socketTCP = new Socket(clientConf.SERVERADDRESS, clientConf.TCPPORT);
		BufferedWriter writerOutput = new BufferedWriter(new OutputStreamWriter(socketTCP.getOutputStream()));
		BufferedReader readerInput = new BufferedReader(new InputStreamReader(socketTCP.getInputStream()));
			
		MulticastClient multicastClient = new MulticastClient(); //its parameters will be set in login request.
		
		ClientStorageImpl stubClientDatabase = new ClientStorageImpl(); //its parameters will be set in login request.
		
		String request = null; //client request
		Scanner scan = new Scanner(System.in); //used to read the request
		boolean shutdown = false; //it will be true only when the client request a logout
		
		System.out.println("Welcome to Winsome! Please register or login if you are already registered");
		
		try {
			while(!shutdown) {
				request = scan.nextLine();
				String [] requestSplitted = request.split(" "); //request parsed by using space character
				String command = requestSplitted[0]; //it represents the first string of the request (login, logout...)
				
				//check the kind of command and then execute the right method
				switch(command) {
				case "register" : {
					RegisterRequest.performRegisterAction(requestSplitted, clientConf, writerOutput, readerInput, multicastClient, stubClientDatabase);				
					break;
				}
				case "login" : {
					LoginRequest.performLoginAction(requestSplitted, clientConf, writerOutput, readerInput, multicastClient, stubClientDatabase);
					break;
				}
				case "logout" : {
					shutdown = LogoutRequest.performLogoutAction(requestSplitted, writerOutput, readerInput, multicastClient, clientConf, socketTCP, stubClientDatabase);
					break;
				}
				case "list": {
					if(requestSplitted[1].equals("users")) {
						ViewListUsersRequest.performViewListUsers(requestSplitted, writerOutput, readerInput);
						break;
					}else if(requestSplitted[1].equals("followers")) {
						ViewListFollowersRequest.performViewListFollowers(stubClientDatabase);
						break;
					}else if(requestSplitted[1].equals("following")){
						ViewListFollowingRequest.performViewListFollowing(stubClientDatabase);
						break;
					}
					
					break;
				}
				case "follow" : {
					FollowRequest.performAddFollowerAction(requestSplitted, readerInput, writerOutput);
					break;
				}
				case "unfollow": {
					UnfollowRequest.performRemoveFollowerAction(requestSplitted, readerInput, writerOutput);
					break;
				}
				case "post": {
					PostRequest.performCreatePost(requestSplitted, scan, readerInput, writerOutput);
					break;
				}
				case "blog": {
					ViewBlogRequest.performViewBlogAction(requestSplitted, writerOutput, readerInput);
					break;
				}
				case "show": {
					if(requestSplitted[1].equals("feed")) {
						ShowFeedRequest.performShowFeedRequest(requestSplitted, writerOutput, readerInput);
						break;
					}else if(requestSplitted[1].equals("post")) {
						ShowPostRequest.performShowPostAction(requestSplitted, writerOutput, readerInput);
						break;
					}
					
					break;
				}
				case "delete": {
					DeletePostRequest.performDeletePostAction(requestSplitted, writerOutput, readerInput);
					break;
				}
				case "rate": {
					RatePostRequest.performRatePostAction(requestSplitted, writerOutput, readerInput);
					break;
				}
				case "comment": {
					CommentPostRequest.performCommentPostAction(requestSplitted, writerOutput, readerInput, scan);
					break;
				}
				case "rewin": {
					RewinPostRequest.performRewinPostAction(requestSplitted, writerOutput, readerInput);
					break;
				}
				case "wallet": {
					if(requestSplitted.length == 1)
						GetWalletRequest.performGetWalletAction(requestSplitted, writerOutput, readerInput);
					else if(requestSplitted.length == 2)
						GetWalletInBitcoinRequest.performGetWalletInBitcoinAction(requestSplitted, writerOutput, readerInput);
					else throw new IllegalArgumentException("Number of arguments insert for get wallet operation is not valid, you must type only: wallet or wallet btc");
					
					break;
				}
				case "help":{
					HelpRequest.performHelpAction();
					break;
				}
				default: {
					System.err.println("This command doesn't exists, please check the documentation");
					break;
				}
				}
			}
		}catch(SocketException e) {
			System.err.println("Server isn't running: abort...");
			scan.close();
			System.exit(0);
		}
	
		
		scan.close();
		System.exit(1);
		
	}
}
