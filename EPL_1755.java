package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1755.java $
$Id: EPL_1755.java 7916 2014-06-10 12:30:12Z cariram $
*/

import java.io.IOException;

import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1755 extends SuiteCardPayment{
	
	private String cashierID,incorrectCashierID="@#$%^RD";

	/**
	 * EPL-1755 All applications / Check Filters / Cashier filter (location section)
	 * 
	 * @throws IOException
	 */
	@Test()
	public void epl_1755()
	{		
		try
		{
			logger.info(" EPL-1755 executing started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);	

			//Test Data Prep
			//Selecting BRISBANE_SHOP2
			logger.info("Step 2:");
			Select appDd = new Select(getObject("select_appli_id"));
			Assert.assertTrue(appDd.getFirstSelectedOption().getText().equals(valAll));
			getObject("search_link").click();
			if(getObject("page_no_xpath").isDisplayed())
			{
				verifyElementDisp("page_no_xpath");
				logger.info("The search is successful and in the main view, some transactions are displayed");
			}
			else
			{
				logger.info("The are no data found to be displayed to click on the plus icon");
				Assert.fail("The are no data found to be displayed to click on the plus icon");
			}			

			//Fetching cashierID of the first row as Test Data
			//Set a correct value (alphanumeric character) in cashier filter and execute search.The search has to be successful
			//Navigate to transaction journal
			logger.info("Step 3, 4:");
			getObject("plus_icon_xpath").click();			
			logger.info("Clicked on plus icon");

			// switch to the card payment details popup window		
			switchToWindow();
			logger.info("Switched to detail window");
			
			//Verifying Cashier field in detail view			
			if(isElementPresent("cashier_data_xpath"))
			{
				cashierID = getObject("cashier_data_xpath").getText();					
			}	
			driver.close();
			driver.switchTo().window(ParentWindow);
			logger.info("Noted the Cashier data, "+cashierID+" from detailed window");

			//Set an correct value in Cashier filter and execute search.
			logger.info("Step 5:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			getObject("cashier_id").sendKeys(cashierID);
			getObject("search_link").click();			
			if(getObject("page_no_xpath").isDisplayed())
			{				
				verifyElementDisp("page_no_xpath");
				logger.info("The search is successful and in the main view, only transactions for cashier selected are displayed");
			}
			else
			{
				logger.info("The are no data found to be displayed ");
				Assert.fail("The are no data found to be displayed ");
			}

			//Set an incorrect value (special characters) in Cashier filter and execute search.The clear and friendly error message is displayed
			logger.info("Step 6:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			getObject("cashier_id").sendKeys(incorrectCashierID);
			getObject("search_link").click();

			verifyTextContains("alert_token_text_xpath", altMsg1);
			verifyTextContains("alert_token_text_xpath", altMsg2);
			logger.info("Validated alert message with incorrect Cashier field");
			logger.info(" EPL-1755 execution successful");
		} 
		catch (Throwable t)
		{
			handlePopUpException(t);
		}
	}

	

}
