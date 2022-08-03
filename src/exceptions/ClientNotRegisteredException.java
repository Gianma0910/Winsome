package exceptions;

public class ClientNotRegisteredException extends Exception {

	public ClientNotRegisteredException() {
		super();
	}
	
	public ClientNotRegisteredException(String msg) {
		super(msg);
	}
}
