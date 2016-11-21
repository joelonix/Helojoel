package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1754.java $
$Id: EPL_1754.java 7916 2014-06-10 12:30:12Z cariram $
*/

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1754 extends SuiteCardPayment {
	private boolean isTrue;
	private String incorrectPOS="@#$%^RD";
	private WebElement recieptTable;
	private List<WebElement> recieptRows;
		
	/**
	 * EPL-1754 All applications / Check Filters / POS filter (location section)
	 * 
	 * @throws IOException
	 */
	@Test()
	public void epl_1754()
	{		
		try
		{
			logger.info(" EPL-1754 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);	
			getObject("search_link").click();
			logger.info("Clicked search link");
			
			//Navigate to level 1
			goToMidLevel("settlement_id");
			
			//Fetching posID of the first row as Test Data
			getObject("pos_xpath").click();
			String posID = getFirstColVal("pos_tableheader_xpath", tabPos, "cp_trn_pos_header_col_data_xpath");	
			
			//Set a correct value (alphanumeric character) in POS filter and execute search.The search has to be successful
			//Navigate to transaction journal  enter a POS and search
			logger.info("Step 3:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			getObject("pos_id").sendKeys(posID);
			getObject("search_link").click();
			
			verifyAllColValues("card_payment_journal_table_header_xpath", colPOS, "cp_j_header_col_data_xpath", posID);	
			
			//Don’t set any value in POS filter and execute search.In the main view, there are some transactions.
			logger.info("Step 2:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			getObject("search_link").click();
			logger.info("Clicked search without setting POS field");
			
			recieptTable = getObject("table_card_payment_journal_xpath");
			recieptRows = recieptTable.findElements(By.tagName("tr"));
			if(recieptRows.size()==1 && recieptRows.get(0).getText().contains(noResultMsg)){
				logger.info(noResultMsg);
				Assert.fail(noResultMsg);
			}
			if(recieptRows.size()>=1){
				isTrue = true;
				Assert.assertTrue(isTrue);
			}
			logger.info("Validated search results successfully without POSid");
			
			//Set an incorrect value (special characters) in POS filter and execute search.The clear and friendly error message is displayed
			logger.info("Step 4:");
			cardPaymentPageNavigator();
			cardPaymentSubPageNavigator(subModTrnJrn);
			getObject("pos_id").sendKeys(incorrectPOS);
			getObject("search_link").click();		
			
			verifyTextContains("alert_token_text_xpath", altMsg1);
			verifyTextContains("alert_token_text_xpath", altMsg2);
			logger.info("Validated alert message with incorrect POS ID");
			
			logger.info(" EPL-1754 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	

}
