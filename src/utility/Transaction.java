package utility;

import java.util.Calendar;
import java.util.Objects;

import exceptions.InvalidAmountException;

public class Transaction {
	
	private double amount;
	private String timestamp;
	
	public Transaction(double amount) throws InvalidAmountException {
		Objects.requireNonNull(amount, "Amount parameter is null");
		
		if(amount <= 0)
			throw new InvalidAmountException("Negative transactions are not supported");
		
		this.amount = amount;

		Calendar c = Calendar.getInstance();
		this.timestamp = c.getTime().toString();
	}
	
	public double getAmount() {
		return amount;
	}
	
	public String getTimeStamp() {
		return timestamp;
	}
}
