package client.view_list_request;

import java.util.ArrayList;
import java.util.Iterator;

import client.FollowerDatabaseImpl;

public class ViewListFollowersRequest {

	public static void performViewListFollowers(FollowerDatabaseImpl stubClientDatabase) {
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
