package client;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.Socket;

import java.rmi.NotBoundException;

import java.util.Scanner;

import client.follow_unfollow_request.FollowRequest;
import client.follow_unfollow_request.UnfollowRequest;
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
import exceptions.ClientNotLoggedException;
import exceptions.ClientNotRegisteredException;
import exceptions.InvalidConfigurationException;

public class ClientMain {
	
	public static void main(String[] args) throws InvalidConfigurationException, IOException, NotBoundException, ClientNotRegisteredException, ClientNotLoggedException {
		if(args.length != 1) {
			System.err.println("Usage: java ClientMain <path configuration file>\n");
			System.err.println("Check the documentation\n");
			System.exit(0);
		}
		
		String pathConfigurationFile = args[0];
		File configurationFile = new File(pathConfigurationFile);
		
		ClientConfiguration clientConf = new ClientConfiguration(configurationFile);
		
		System.out.println("File properties read successfully\n");
		
		Socket socketTCP = new Socket(clientConf.SERVERADDRESS, clientConf.TCPPORT);
		BufferedWriter writerOutput = new BufferedWriter(new OutputStreamWriter(socketTCP.getOutputStream()));
		BufferedReader readerInput = new BufferedReader(new InputStreamReader(socketTCP.getInputStream()));
			
		MulticastClient multicastClient = new MulticastClient();
		
		FollowerDatabaseImpl stubClientDatabase = new FollowerDatabaseImpl();
		
		String request;
		Scanner scan = new Scanner(System.in);
		boolean shutdown = false;
		boolean isUserLogged = false;
		
		while(!shutdown) {
			request = scan.nextLine();
			String [] requestSplitted = request.split(" ");
			String command = requestSplitted[0];
			
			switch(command) {
			case "register" : {
				RegisterRequest.performRegisterAction(requestSplitted, clientConf);				
				break;
			}
			case "login" : {
				LoginRequest.performLoginAction(requestSplitted, clientConf, writerOutput, readerInput, multicastClient, stubClientDatabase);
				isUserLogged = true;
				break;
			}
			case "logout" : {
				shutdown = LogoutRequest.performLogoutAction(requestSplitted, writerOutput, readerInput, multicastClient, clientConf, socketTCP, stubClientDatabase);
				break;
			}
			case "list": {
				if(isUserLogged == false)
					throw new ClientNotLoggedException("You can't do this operation because you are not logged in. Please use register operation or login operation");
					
				if(requestSplitted.length != 2)
					throw new IllegalArgumentException("Number of arguments insert for view list operation is not valid, you must type: list users, list followers, list following");
				
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
			}
			case "follow" : {
				if(isUserLogged == false)
					throw new ClientNotLoggedException("You can't do this operation because you are not logged in. Please use register operation or login operation");
				
				FollowRequest.performAddFollowerAction(requestSplitted, readerInput, writerOutput);
				break;
			}
			case "unfollow": {
				if(isUserLogged == false)
					throw new ClientNotLoggedException("You can't do this operation because you are not logged in. Please use register operation or login operation");
				
				UnfollowRequest.performRemoveFollowerAction(requestSplitted, readerInput, writerOutput);
				break;
			}
			case "post": {
				if(isUserLogged == false)
					throw new ClientNotLoggedException("You can't do this operation because you are not logged in. Please use register operation or login operation");
				
				String [] r = request.split(" \"");
				PostRequest.performCreatePost(r, readerInput, writerOutput);
				break;
			}
			case "blog": {
				if(isUserLogged == false)
					throw new ClientNotLoggedException("You can't do this operation because you are not logged in. Please use register operation or login operation");
				
				ViewBlogRequest.performViewBlogAction(requestSplitted, writerOutput, readerInput);
				break;
			}
			case "show": {
				if(isUserLogged == false)
					throw new ClientNotLoggedException("You can't do this operation because you are not logged in. Please use register operation or login operation");
				
				if(requestSplitted[1].equals("feed")) {
					ShowFeedRequest.performShowFeedRequest(requestSplitted, writerOutput, readerInput);
					break;
				}else if(requestSplitted[1].equals("post")) {
					ShowPostRequest.performShowPostAction(requestSplitted, writerOutput, readerInput);
					break;
				}
			}
			case "delete": {
				if(isUserLogged == false)
					throw new ClientNotLoggedException("You can't do this operation because you are not logged in. Please use register operation or login operation");
				
				DeletePostRequest.performDeletePostAction(requestSplitted, writerOutput, readerInput);
				break;
			}
			case "rate": {
				if(isUserLogged == false)
					throw new ClientNotLoggedException("You can't do this operation because you are not logged in. Please use register operation or login operation");
				
				RatePostRequest.performRatePostAction(requestSplitted, writerOutput, readerInput);
				break;
			}
			case "comment": {
				if(isUserLogged == false)
					throw new ClientNotLoggedException("You can't do this operation because you are not logged in. Please use register operation or login operation");
				
				String [] takeComment = request.split("\"");
				CommentPostRequest.performCommentPostAction(requestSplitted, takeComment, writerOutput, readerInput);
				break;
			}
			case "rewin": {
				if(isUserLogged == false)
					throw new ClientNotLoggedException("You can't do this operation because you are not logged in. Please use register operation or login operation");
				
				RewinPostRequest.performRewinPostAction(requestSplitted, writerOutput, readerInput);
				break;
			}
			case "wallet": {
				if(isUserLogged == false)
					throw new ClientNotLoggedException("You can't do this operation because yuo ar enot logged in. Please use register operation or login operation");
				
				if(requestSplitted.length == 1)
					GetWalletRequest.performGetWalletAction(requestSplitted, writerOutput, readerInput);
				else if(requestSplitted.length == 2)
					GetWalletInBitcoinRequest.performGetWalletInBitcoinAction(requestSplitted, writerOutput, readerInput);
				else throw new IllegalArgumentException("Number of arguments insert for get wallet operation is not valid, you must type only: wallet or wallet btc");
				
				break;
			}
			default: {
				System.err.println("This command doesn't exists, please check the documentation");
				break;
			}
			}
			
		}
		
		scan.close();
		System.exit(1);
		
	}
}
