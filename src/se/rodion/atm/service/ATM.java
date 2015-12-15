package se.rodion.atm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.rodion.atm.model.ATMCard;
import se.rodion.exceptions.ATMException;
import se.rodion.exceptions.ATMSecurityException;

public class ATM
{
	private final Map<String, Bank> banks;
	
	public ATM(List<Bank> banks) //banks!!!
	{
		this.banks = new HashMap<>();
		
		if (banks == null || banks.isEmpty())
		{
			throw new IllegalArgumentException("List of banks is empty");
		}
		
		for (Bank bank : banks)
			this.banks.put(bank.getBankId(), bank);

	} 
	
	public ATMSessionImpl verifyPin(int pin, ATMCard card) throws ATMSecurityException, ATMException
	{
		if (card.verifyPin(pin))
		{
			return new ATMSessionImpl(card, getBank(card));
		}
		else
		{
			throw new ATMSecurityException("Pin is not correct");
		}		
	}
	
	private Bank getBank(ATMCard card) throws ATMException
	{
		if (banks.containsKey(card.getBankId()))
		{
			return banks.get(card.getBankId());
		}
		else
		{
			throw new ATMException("Cards of your bank should not be sprovided by this ATM");
		}
	}	
}
