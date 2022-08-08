package client.view_list_request;

import java.util.ArrayList;
import java.util.Iterator;

import client.FollowerDatabaseImpl;

public class ViewListFollowingRequest {

	public static void performViewListFollowing(FollowerDatabaseImpl stubClientDatabase) {
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
