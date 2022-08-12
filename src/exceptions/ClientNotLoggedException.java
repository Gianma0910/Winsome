package exceptions;

public class ClientNotLoggedException extends Exception {

	public ClientNotLoggedException() {
		super();
	}
	
	public ClientNotLoggedException(String msg) {
		super(msg);
	}
}
