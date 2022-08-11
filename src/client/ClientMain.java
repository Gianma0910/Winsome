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
import client.post_action_request.ShowFeedRequest;
import client.post_action_request.ShowPostRequest;
import client.post_action_request.ViewBlogRequest;
import client.register_request.RegisterRequest;
import client.view_list_request.ViewListFollowersRequest;
import client.view_list_request.ViewListFollowingRequest;
import client.view_list_request.ViewListUsersRequest;
import configuration.ClientConfiguration;
import exceptions.ClientNotRegisteredException;
import exceptions.InvalidConfigurationException;
import utility.TypeError;

public class ClientMain {
	
	public static void main(String[] args) throws InvalidConfigurationException, IOException, NotBoundException, ClientNotRegisteredException {
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
				break;
			}
			case "logout" : {
				shutdown = LogoutRequest.performLogoutAction(requestSplitted, writerOutput, readerInput, multicastClient, clientConf, socketTCP, stubClientDatabase);
				break;
			}
			case "list": {
				if(requestSplitted.length != 2)
					throw new IllegalArgumentException("Number of arguments insert for view list operation is not valid, you must type: list users, list followers, list following");
				
				if(requestSplitted[1].equals("users")) {
					ViewListUsersRequest.performViewListUsers(requestSplitted, writerOutput, readerInput);
				}else if(requestSplitted[1].equals("followers")) {
					ViewListFollowersRequest.performViewListFollowers(stubClientDatabase);
				}else if(requestSplitted[1].equals("following")){
					ViewListFollowingRequest.performViewListFollowing(stubClientDatabase);
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
				String [] r = request.split(" \"");
				PostRequest.performCreatePost(r, readerInput, writerOutput);
				break;
			}
			case "blog": {
				ViewBlogRequest.performViewBlogAction(requestSplitted, writerOutput, readerInput);
				break;
			}
			case "show": {
				if(requestSplitted[1].equals("feed")) {
					ShowFeedRequest.performShowFeedRequest(requestSplitted, writerOutput, readerInput);
				}else if(requestSplitted[1].equals("post")) {
					ShowPostRequest.performShowPostAction(requestSplitted, writerOutput, readerInput);
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
				String [] takeComment = request.split("\"");
				CommentPostRequest.performCommentPostAction(requestSplitted, takeComment, writerOutput, readerInput);
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
