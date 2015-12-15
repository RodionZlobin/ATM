package se.rodion.exceptions;

public final class ATMSecurityException extends Exception
{
	private static final long serialVersionUID = 7347259427020439413L;
	
	public ATMSecurityException(String message)
	{
		super(message);
	}
	
	public ATMSecurityException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
