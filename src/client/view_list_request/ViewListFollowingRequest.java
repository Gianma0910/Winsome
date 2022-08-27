package client.view_list_request;

import java.util.ArrayList;
import java.util.Iterator;

import client.ClientStorageImpl;

/**
 * Class used to perform view following list action.
 * @author Gianmarco Petrocchi.
 *
 */
public class ViewListFollowingRequest {

	/**
	 * Static method used to perform view list following action, only when the client is already logged. It doesn't sends a request to server, but it works in local by using his storage where 
	 * there are updated followers and following lists. In this case the client will see only the usernames of users that he follow.
	 * @param stubClientDatabase Client storage used to get his following list.
	 */
	public static void performViewListFollowing(ClientStorageImpl stubClientDatabase) {
		ArrayList<String> following = stubClientDatabase.getFollowing();
		
		System.out.print("Following: ");
		
		Iterator<String> it = following.iterator();
		
		while(it.hasNext()) {
			System.out.print(it.next());
			if(it.hasNext())
				System.out.print(", ");
		}
		
		System.out.println();
		
		return;
	}
	
}
