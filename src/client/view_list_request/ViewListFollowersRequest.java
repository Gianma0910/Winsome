package client.view_list_request;

import java.util.ArrayList;
import java.util.Iterator;

import client.ClientStorageImpl;

/**
 * Class used to perform view list followers action.
 * @author Gianmarco Petrocchi
 *
 */
public class ViewListFollowersRequest {

	/**
	 * Static method used to perform view list followers action, only when the client is already logged. It doesn't sends a request to server, but it works in local by using his storage where 
	 * there are updated followers and following lists. In this case the client will see only the usernames of users that follow him.
	 * @param stubClientDatabase Client storage used to get his followers list.
	 */
	public static void performViewListFollowers(ClientStorageImpl stubClientDatabase) {
		ArrayList<String> followers = stubClientDatabase.getFollowers();
		
		System.out.print("Followers: ");
		
		Iterator<String> it = followers.iterator();
		
		while(it.hasNext()) {
			System.out.print(it.next());
			if(it.hasNext())
				System.out.print(", ");
		}
		
		System.out.println();
		
		return;
	}
	
	
}
