package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1761.java $
$Id: EPL_1761.java 7916 2014-06-10 12:30:12Z cariram $
*/

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1761 extends SuiteCardPayment{
	private boolean areRowsPresent = false;
	private String settlementID, specialCharsSettleID = "$#@%!@@";	
	private List<WebElement> rows;
	
	
	/**
	 * EPL-1761 Check reconciliation filter
	 * 
	 * @throws IOException
	 */
	
	@Test()
	public void epl_1761()
	{		
		try
		{
			logger.info(" EPL-1761 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			
			//Preparing test data
			getObject("advanced_search_link").click();
			getObject("no_id").click(); //unchecking checkbox
			getObject("search_link").click();
			
			//Fetching list of headers
			settlementID = getFirstColVal("card_payment_journal_table_header_xpath", colSettNum, "cp_j_header_col_data_xpath");			
			logger.info("Test Data preparation done");
			
			//Execute search with a settlement number
			logger.info("Step 4:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			getObject("advanced_search_link").click();
			getObject("settlement_num_id").sendKeys(settlementID);
			getObject("search_link").click();
			logger.info("Search clicked with settlement num set");
			
			//Verify In the main view, there are some transactions.
			rows = getObject("table_card_payment_journal_xpath").findElements(By.tagName("tr"));
			if(rows.size()>0){
				areRowsPresent = true;
			}
			Assert.assertEquals(areRowsPresent, true);
			logger.info("Search returned rows");
			
			//Don’t set any value in Settlement Number filter and execute search
			logger.info("Step 2:");
			cardPaymentSubPageNavigator("TransactionJournal");
			getObject("search_link").click();
			logger.info("Search clicked without settlement num being set");
			
			rows = getObject("table_card_payment_journal_xpath").findElements(By.tagName("tr"));
			if(rows.size()>0){
				areRowsPresent = true;
			}
			Assert.assertEquals(areRowsPresent, true);
			logger.info("Search returned rows");
			
			//Set an incorrect value (special characters) in Settlement Number filter and execute search, The clear and friendly error message is displayed
			logger.info("Step 3:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			getObject("advanced_search_link").click();
			getObject("settlement_num_id").sendKeys(specialCharsSettleID);
			getObject("search_link").click();
			logger.info("Search clicked with settlement num set");
			
			verifyTextContains("alert_token_text_xpath", altMsg1);
			verifyTextContains("alert_token_text_xpath", altMsg2);
			logger.info("Validated alert successfully.");
			
			logger.info(" EPL-1761 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	

}
