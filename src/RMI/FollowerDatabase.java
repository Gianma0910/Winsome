package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface FollowerDatabase extends Remote{

	String addFollower(String u) throws RemoteException;
	
	String removeFollower(String u) throws RemoteException;

	void setFollowers(ArrayList<String> followers) throws RemoteException;

	void setFollowing(ArrayList<String> following) throws RemoteException;
	
	ArrayList<String> getFollowing() throws RemoteException;
}
