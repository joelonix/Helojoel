package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1779.java $
$Id: EPL_1779.java 7916 2014-06-10 12:30:12Z cariram $
 */
import java.io.IOException;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1779 extends SuiteCardPayment{
	private String inCorrectMidVal = "123456";	
	
	/**
	 * EPL-1779 Create a search to have no transactions as result
	 * In the main view, there is this message “No result to your search. Please try another search.
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1779()
	{		
		try
		{
			logger.info(" EPL-1779 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			//Navigate to transaction journal page
			cardPaymentSubPageNavigator(subModTrnJrn);
			waitForElementPresent("journalsearch_xpath");

			//Enter an incorrect MID id and execute search
			logger.info("Step 2:");
			getObject("mid_id").sendKeys(inCorrectMidVal);
			logger.info("Mid value set with incorrect value");
			getObject("journalsearch_xpath").click();
			logger.info("Search clicked ");

			//Verify no search results are retained
			element = getObject("table_card_payment_journal_xpath").findElement(By.tagName("tr"));
			Assert.assertEquals(element.getText(), noResultMsg);
			logger.info("No search results msg validated");
			logger.info(" EPL-1779 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
}