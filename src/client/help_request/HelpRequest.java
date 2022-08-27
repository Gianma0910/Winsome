package client.help_request;

/**
 * Class used to perform help action.
 * @author Gianmarco Petrocchi.
 *
 */
public class HelpRequest {

	/**
	 * Static method used to perform help action. It doesn't sends a request to server, but it simply print in System.in the list of possible commands to use 
	 * in Winsome.
	 */
	public static void performHelpAction() {
		System.out.println("List of commands:");
		System.out.println();
		System.out.println("register <username> <password> <tags> -> register a new user in Winsome, with that username, password and tags");
		System.out.println("login <username> <password> -> login with a user in Winsome");
		System.out.println("logout");
		System.out.println("list users -> see a list of users with at least one tag in common");
		System.out.println("list followers -> see your list of followers");
		System.out.println("list following -> see your list of following, user that you follow");
		System.out.println("follow <username> -> follow a certain user");
		System.out.println("unfollow <username> -> unfollow a certain user");
		System.out.println("blog -> see the list of your posts");
		System.out.println("show feed -> see the list of post of users you are following");
		System.out.println("post <title> <content> -> create a post with the specified title and content");
		System.out.println("show post <idPost> -> see a certain post");
		System.out.println("delete <idPost> -> delete a certain post from Winsome");
		System.out.println("rewin <idPost> -> share/rewin a certain post, it must be in your feed");
		System.out.println("rate <IdPost> <vote> -> add vote to a certain post, it must be 1 or -1 (1 = like, -1 = dislike)");
		System.out.println("comment <idPost> <comment> -> add comment to a certain post");
		System.out.println("wallet -> see your transactions and your total amount");
		System.out.println("wallet btc -> see your portfolio converted in bitcoin");
	}
	
}
