package se.rodion.atm.service;

import se.rodion.atm.model.ATMCard;
import se.rodion.atm.model.ATMReceipt;
import se.rodion.exceptions.ATMException;

public abstract class AbstractATMSession implements ATMSession
{
	protected final ATMCard atmCard;
	protected final Bank bank;
	
	public AbstractATMSession(ATMCard atmCard, Bank bank)
	{
		this.atmCard = atmCard;
		this.bank = bank;
	}
	
	public abstract long withdrawAmount(int amount) throws ATMException;
	
	public abstract ATMReceipt requestReceipt(long transactionId);
	
	public abstract long checkBalance() throws ATMException;

	public abstract long getTransactionId();
	
}
