package se.rodion.exceptions;

public final class ATMException extends Exception
{
	private static final long serialVersionUID = -975394977402455124L;
	
	public ATMException(String message)
	{
		super(message);
	}
	
	public ATMException(String message, Throwable cause)
	{
		super(message, cause);
	}	
}
