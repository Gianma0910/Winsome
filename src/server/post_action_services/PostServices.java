package server.post_action_services;

import java.io.IOException;
import java.net.Socket;

public interface PostServices {

	void createPost(String [] requestSplitted, Socket socket) throws IOException;
	
	void viewUserPost(Socket socket) throws IOException;
	
	void viewUserFeed(Socket socket) throws IOException;
}
