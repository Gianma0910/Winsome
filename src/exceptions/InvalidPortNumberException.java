package exceptions;

/**
 * This exception occurs when the port number is not valid [1024, 65535]
 * @author Gianmarco Petrocchi
 *
 */
public class InvalidPortNumberException extends Exception {

	public InvalidPortNumberException() {
		super();
	}
	
	public InvalidPortNumberException(String msg) {
		System.out.println(msg);
	}
	
}
