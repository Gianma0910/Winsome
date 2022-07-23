package exceptions;

/**
 * This exception occurs when a new user try to register himself in Winsome with an already existed username
 * @author Gianmarco Petrocchi
 */
public class UserAlreadyExistsException extends Exception {

	public UserAlreadyExistsException() {
		super();
	}
	
	public UserAlreadyExistsException(String msg) {
		super(msg);
	}
}
