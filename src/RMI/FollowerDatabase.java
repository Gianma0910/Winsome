package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import utility.User;

public interface FollowerDatabase extends Remote{

	String addFollower(String u) throws RemoteException;
	
	String removeFollower(String u) throws RemoteException;

	void setFollowers(ArrayList<String> followers) throws RemoteException;

}
