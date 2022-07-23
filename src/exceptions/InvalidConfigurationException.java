package exceptions;

/**
 * This exception occurs when there are some error in client and server configuration
 * @author Gianmarco Petrocchi
 *
 */
public class InvalidConfigurationException extends Exception{

	public InvalidConfigurationException() {
		super();
	}
	
	public InvalidConfigurationException(String msg) {
		super(msg);
	}
}
