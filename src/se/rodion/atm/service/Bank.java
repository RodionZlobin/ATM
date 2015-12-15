package se.rodion.atm.service;

import se.rodion.atm.model.BankReceipt;

public interface Bank
{
	String getBankId();
	long getBalance(String accountHolderId);
	long withdrawAmount(int amount);
	BankReceipt requestReceipt(long transactionId);	
}
