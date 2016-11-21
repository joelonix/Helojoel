package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1764.java $
$Id: EPL_1764.java 8030 2014-06-16 06:30:03Z cariram $
 */


import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1764 extends SuiteCardPayment{
	
	private String authID,incorrectAuthID="@#$%^RD";
	private List<WebElement> cols,detailRows;
	private int authRowIndex;

	/**
	 * EPL-1764 All applications / Check Filters / Authorization Number filter works correctly
	 * 
	 * @throws IOException
	 */
	@Test()
	public void epl_1764()
	{		
		try
		{
			logger.info(" EPL-1764 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);	

			//Preparing test data
			logger.info("Preparing test data...");
			getObject("advanced_search_link").click();
			getObject("withoutAuth_checbox_id").click();
			getObject("search_link").click();
			waitForTxtPresent("journal_row_count_xpath", displayText);
			getObject("plus_icon_xpath").click();
			
			logger.info("Clicked plus icon");

			// switch to the card payment details popup window
			switchToWindow();
			logger.info("Switched to detail window");

			authRowIndex=getDetailedRowIndex();
			cols = detailRows.get(authRowIndex).findElements(By.tagName("td"));
			authID = cols.get(1).getText();
			logger.info("Prepared test data....");
			driver.close();
			driver.switchTo().window(ParentWindow);

			//End of test data prep
			/*Set a correct value (alphanumeric character) in Authorization Number filter and execute search
			Set Auth num in advance Search*/
			logger.info("Step 2, 3, 4:");
			cardPaymentSubPageNavigator(subModTrnJrn);	
			getObject("advanced_search_link").click();
			if(!(authID.equals("-")))
			{
				getObject("auth_num_id").sendKeys(authID);
				getObject("search_link").click();

				getObject("plus_icon_xpath").click();
				logger.info("Clicked plus icon");

				// switch to the card payment details popup window
				switchToWindow();
				logger.info("Switched to detail window");
				
				authRowIndex=getDetailedRowIndex();
				Assert.assertTrue(detailRows.get(authRowIndex).getText().contains(authID));
				logger.info("Validated the cashier row data in detail view");
				logger.info("Validated the search results setting auth num field");
				driver.close();
				driver.switchTo().window(ParentWindow);

				//Don’t set any value in auth num filter and execute search.In the main view, there are some transactions.
				cardPaymentSubPageNavigator(subModTrnJrn);
				getObject("search_link").click();
				logger.info("Clicked search without setting auth num field");
				Assert.assertTrue(isElementPresent("journal_row_count_xpath"));
				logger.info("Validated the search results without setting auth num field");

				//Set an incorrect value (special characters) in auth filter and execute search.The clear and friendly error message is displayed
				cardPaymentSubPageNavigator(subModTrnJrn);
				getObject("advanced_search_link").click();
				getObject("auth_num_id").sendKeys(incorrectAuthID);
				getObject("search_link").click();
			}
			else
			{
				logger.info("Test Data with Authorization Number, "+authID+ " is not proper to execute test");
				Assert.fail("Test Data with Authorization Number, "+authID+ " is not proper to execute test");
			}

			verifyTextContains("alert_token_text_xpath", altMsg1);
			verifyTextContains("alert_token_text_xpath", altMsg2);
			logger.info("Validated alert message with incorrect auth num field");

			logger.info(" EPL-1764 execution successful");
		} 
		catch (Throwable t)
		{
			handlePopUpException(t);
		}
	}

	

	/**
	 * get detailed row index no
	 * @return
	 */
	private int getDetailedRowIndex(){
		waitForElementPresent("cp_detailedview_table_xpath");
		detailRows = getObject("cp_detailedview_table_xpath").findElements(By.tagName("tr"));
		for(int rIndex=0; rIndex < detailRows.size() ; rIndex++){
			if(detailRows.get(rIndex).getText().contains(authNumLabel)){
				authRowIndex = rIndex;
			}
		}
		return authRowIndex;
	}

}
