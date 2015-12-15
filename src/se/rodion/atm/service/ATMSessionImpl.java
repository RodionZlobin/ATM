package se.rodion.atm.service;

import se.rodion.atm.model.ATMCard;
import se.rodion.atm.model.ATMReceipt;
import se.rodion.exceptions.ATMException;

public class ATMSessionImpl extends AbstractATMSession implements ATMSession
{

	private long transactionId;
	
	public ATMSessionImpl(ATMCard atmCard, Bank bank)
	{
		super(atmCard, bank);
//		transactionId = 0;
	}

	@Override
	public long withdrawAmount(int amount) throws ATMException
	{
		if (transactionId == 0)
		{
			if (amount < bank.getBalance(atmCard.getAccountHolderId()))
			{
				if (amount < 100 || amount > 10000 || amount % 100 != 0)
				{
					throw new ATMException("Amount could be between min 100, max 10000 and devided to 100");
				}
				else
				{
					transactionId = 1L;
					return bank.withdrawAmount(amount);
				}
			}
			else
			{
				throw new ATMException("You don´t have enought funds on account");
			}
		}
		else
		{
			throw new ATMException("Please make new authorization");
		}

	}

	@Override
	public ATMReceipt requestReceipt(long transactionId)
	{
		return new ATMReceipt(transactionId, bank.requestReceipt(transactionId).getAmount());
	}

	@Override
	public long checkBalance() throws ATMException
	{
		if (transactionId == 0)
		{
			transactionId = 1L;
			return bank.getBalance(atmCard.getAccountHolderId());
		}
		else
		{
			throw new ATMException("Please make new authorization");
		}
	}
	
	@Override
	public long getTransactionId()
	{
		return transactionId;
	}

}
