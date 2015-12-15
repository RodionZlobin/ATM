import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import se.rodion.atm.model.ATMCard;
import se.rodion.atm.model.BankReceipt;
import se.rodion.atm.service.ATM;
import se.rodion.atm.service.ATMSession;
import se.rodion.atm.service.ATMSessionImpl;
import se.rodion.atm.service.Bank;
import se.rodion.exceptions.ATMException;
import se.rodion.exceptions.ATMSecurityException;



@RunWith(MockitoJUnitRunner.class)
public class ATMTest
{
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private Bank bankMock;
	
	@Mock
	private ATMSession atmSessionMock;
	
	private ATM atm;
	private int validPin = 1234;
	private int invalidPin = 4321;
	private List<Bank> banks;
	private ATMCard card1;
	private ATMCard card2;
	private String bankId1 = "Swedbank";
	private String bankId2 = "Nordea";
	private String accountHolderId = "Rodion";
	
	@Before
	public void setup()
	{
		bankMock = mock(Bank.class);
		atmSessionMock = mock(ATMSessionImpl.class);
		
		card1 = new ATMCard(accountHolderId, bankId1, validPin);
		card2 = new ATMCard(accountHolderId, bankId2, validPin);
		
		when(bankMock.getBankId()).thenReturn(bankId1);
		when(bankMock.getBalance(accountHolderId)).thenReturn(12000L);
		
		banks = new ArrayList<>();
		banks.add(bankMock);
		atm = new ATM (banks);
		
	}

	
	
	@Test
	public void testWithdrawAmountRules() throws ATMException, ATMSecurityException
	{
		thrown.expect(ATMException.class);
		thrown.expectMessage("Amount could be between min 100, max 10000 and devided to 100");		

		ATMSession atmSession = atm.verifyPin(validPin, card1);
		atmSession.withdrawAmount(11000);
	}
	
	@Test
	public void testWrongCodeIsNotAcceptable() throws ATMSecurityException, ATMException
	{
		thrown.expect(ATMSecurityException.class);
		thrown.expectMessage("Pin is not correct");
		
		ATMSession atmSession = atm.verifyPin(invalidPin, card1);
		atmSession.withdrawAmount(140);		
	}
	
	@Test
	public void testIfBankNotExistsAtThisBankomat() throws ATMSecurityException, ATMException
	{
		thrown.expect(ATMException.class);
		thrown.expectMessage("Cards of your bank should not be sprovided by this ATM");
		
		atm.verifyPin(validPin, card2);	
	}
	
	@Test //check balance
	public void testOnlyOneOperationPerInloggningCheckBalance() throws ATMSecurityException, ATMException
	{
		thrown.expect(ATMException.class);
		thrown.expectMessage("Please make new authorization");
		
		ATMSession atmSession = atm.verifyPin(validPin, card1);
		atmSession.checkBalance();
		atmSession.checkBalance();		
	}
	
	@Test //withdrawAmount
	public void testOnlyOneOperationPerInloggningWithdrawAmount() throws ATMSecurityException, ATMException
	{
		thrown.expect(ATMException.class);
		thrown.expectMessage("Please make new authorization");
		
		ATMSession atmSession = atm.verifyPin(validPin, card1);
		atmSession.withdrawAmount(5000);
		atmSession.withdrawAmount(1000);		
	}
	
	@Test
	public void testWithdrawAmount() throws ATMSecurityException, ATMException
	{
		ATMSession atmSession = atm.verifyPin(validPin, card1);
		
		when(bankMock.withdrawAmount(5000)).thenReturn(5000L);
		
		long realWithdrawAmount = atmSession.withdrawAmount(5000);
		assertThat(realWithdrawAmount, is(equalTo(5000L)));
	}
	
	@Test
	public void testRequestReceipt() throws ATMSecurityException, ATMException
	{
		
		ATMSession atmSession = atm.verifyPin(validPin, card1);
		int amount = (int) atmSession.withdrawAmount(2500);
		long transactionId = atmSession.getTransactionId();
		
		when(bankMock.requestReceipt(transactionId)).thenReturn(new BankReceipt(bankId1, transactionId, amount, new Date()));
		
		atmSession.requestReceipt(transactionId);
		
		verify(bankMock).requestReceipt(transactionId);
	}
	
	@Test
	public void testCheckBalance() throws ATMSecurityException, ATMException
	{
		ATMSession atmSession = atm.verifyPin(validPin, card1);
		
		long balance = atmSession.checkBalance();
		assertThat(balance, is(equalTo(12000L)));
		verify(bankMock).getBalance(accountHolderId);
	}
	
	@Test
	public void testItWillComeIllegalArgExceptionIfBanksIsEmpty()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("List of banks is empty");
		
		atm = new ATM(new ArrayList<Bank>());
	}
	
	@Test
	public void testYouCouldNotGetBiggerAmountThenYourAccountBalance() throws ATMSecurityException, ATMException
	{
		thrown.expect(ATMException.class);
		thrown.expectMessage("You don´t have enought funds on account");
		
		when(bankMock.getBalance(accountHolderId)).thenReturn(2000L);
		
		ATMSession atmSession = atm.verifyPin(validPin, card1);
		atmSession.withdrawAmount(5000);
	}
}
