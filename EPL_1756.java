package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1756.java $
$Id: EPL_1756.java 7916 2014-06-10 12:30:12Z cariram $
*/

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1756 extends SuiteCardPayment{
	
	private String incorrectMinVal="@#$%^RD", incorrectMaxVal="@#$%^RD", actualAlertMsg;
	
	
	
	/**
	 * EPL-1756 All applications / Check Filters / Amount filter
	 * 
	 * @throws IOException
	 */
	@Test()
	public void epl_1756()
	{		
		try
		{
			logger.info(" EPL-1756 execution started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));	
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);				
			
			//Entering correct value in Minimum amount field and verify search
			logger.info("Step 2:");
			getObject("min_amount_id").sendKeys(fifty);
			getObject("search_link").click();
			verifyRecordsPresent(minTag, withLabel);
			
			//without entering any value in Minimum amount field and verify search
			logger.info("Step 3:");
			getObject("advanced_search_link").click();
			getObject("min_amount_id").clear();
			getObject("search_link").click();
			verifyRecordsPresent(minTag, withoutLabel);			
			
			//Set an incorrect value (special characters) in Minimum amount filter, execute search and verify friendly error message is displayed
			getObject("advanced_search_link").click();			
			getObject("min_amount_id").sendKeys(incorrectMinVal);
			getObject("search_link").click();	
			verifyAlertMsg(altMsg1, altMsg2, minTag);		
			
			//Entering correct value in Maximum amount field and verify search
			getObject("reset_link").click();
			getObject("max_amount_id").sendKeys(perc_max);
			getObject("search_link").click();
			verifyRecordsPresent(maxTag, withLabel);
			
			//without entering any value in Maximum amount field and verify search
			getObject("advanced_search_link").click();
			getObject("max_amount_id").clear();
			getObject("search_link").click();
			verifyRecordsPresent(maxTag, withoutLabel);	
			
			//Set an incorrect value (special characters) in Maximum amount filter, execute search and verify friendly error message is displayed
			getObject("advanced_search_link").click();			
			getObject("max_amount_id").sendKeys(incorrectMaxVal);
			getObject("search_link").click();	
			verifyAlertMsg(altMsg1, altMsg2, maxTag);			
			logger.info(" EPL-1756 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	
	
	/*
	 * Verifies search results and the message at the bottom
	 */
	private void verifyRecordsPresent(String field1, String field2)
	{
		if(isElementPresent("journal_row_count_xpath"))
		{
			logger.info("The search is successful and In the main view, there are some transactions "+field2+" entering a "+field1+" value");
		}
		else
		{
			Assert.fail("There are no transactions for the selected value of "+field1+" amount");
			logger.info("There are no transactions for the selected value of "+field1+" amount");
		}
		
	}
	
	/*
	 * Verifies the alert message after entering incorrect value
	 */
	private void verifyAlertMsg(String alertMsg1, String alertMsg2, String field1)
	{
		actualAlertMsg= getObject("alert_token_text_xpath").getText();		
		Assert.assertTrue(actualAlertMsg.contains(alertMsg1));
		Assert.assertTrue(actualAlertMsg.contains(alertMsg2));
		getObject("alert_token_Ok_xpath").click();
		logger.info("The clear and friendly error message is displayed as expected when entering an incorrect "+field1+ " amount value");
	}

}
