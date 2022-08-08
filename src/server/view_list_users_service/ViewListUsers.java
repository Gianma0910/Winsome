package server.view_list_users_service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public interface ViewListUsers {

	void viewListUsers(Socket socket) throws IOException;
	
}
