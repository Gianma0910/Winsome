package utility;

import java.util.Calendar;
import java.util.Objects;

import exceptions.InvalidAmountException;

/**
 * Class that represents a transaction in Winsome. This class is used only by server.
 * @author Gianmarco Petrocchi.
 *
 */
public class Transaction {
	
	/** Amount of transaction.*/
	private double amount;
	/** When the transaction was done.*/
	private String timestamp;
	
	/**
	 * Basic constructor.
	 * @param amount Amount of transaction. It must be greater than 0.
	 * @throws InvalidAmountException Only if the amount is less or equals than 0.
	 */
	public Transaction(double amount) throws InvalidAmountException {
		Objects.requireNonNull(amount, "Amount parameter is null");
		
		if(amount <= 0)
			throw new InvalidAmountException("Negative transactions are not supported");
		
		this.amount = amount;

		Calendar c = Calendar.getInstance();
		this.timestamp = c.getTime().toString();
	}
	
	/**
	 * @return Amount of transaction.
	 */
	public double getAmount() {
		return amount;
	}
	
	/**
	 * @return When the transaction was done.
	 */
	public String getTimeStamp() {
		return timestamp;
	}
}
