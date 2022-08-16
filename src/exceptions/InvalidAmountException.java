package exceptions;

/**
 * This exception occurs only when we try to istantiate a new Transaction object with a negative amount or equals to zero
 * @author Gianmarco Petrocchi
 */
public class InvalidAmountException extends Exception{

	public InvalidAmountException() {
		super();
	}
	
	public InvalidAmountException(String msg) {
		super(msg);
	}
}
