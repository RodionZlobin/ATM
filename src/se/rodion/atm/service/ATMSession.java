package se.rodion.atm.service;

import se.rodion.atm.model.ATMReceipt;
import se.rodion.exceptions.ATMException;

public interface ATMSession
{
	long withdrawAmount(int amount) throws ATMException;
	ATMReceipt requestReceipt(long transactionId);
	long checkBalance() throws ATMException;
	long getTransactionId();
}
