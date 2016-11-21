package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1747.java $
$Id: EPL_1747.java 7888 2014-06-09 13:17:58Z cariram $
*/

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1747 extends SuiteCardPayment
{
	private WebElement table;
	private List<WebElement> columns;
	private List<String> colsText = new ArrayList<String>();
	
	
	
	
	/**
	 * EPL-1747 Check presence of cashier display / a user which customer has cashier display provisioned in Everest
	 * 
	 * @throws IOException
	 */
	
	@Test()
	public void epl_1747()
	{		
		try
		{
			logger.info(" EPL-1747 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			verifyBreadCrumb(cpTransJourBC);
			
			//Verify Cashier ID field is present
			logger.info("Step 2:");
			Assert.assertEquals(getObject("cashier_id").isDisplayed(), true);
			getObject("search_link").click();
			logger.info("Clicked search link");
			
			//Opening detailed View of first transaction
			getObject("plus_icon_xpath").click();
			logger.info("Clicked plus icon");
			
			// switch to the card payment details popup window
			switchToWindow();
			logger.info("Switched to detail window");
			
			//Verify if details window is displayed
			table = getObject("cp_detailedview_table_xpath");
			columns = table.findElements(By.tagName("td"));
			for(int colIndex = 0;colIndex<columns.size();colIndex++){
				colsText.add(columns.get(colIndex).getText());
			}
			//Verify Cashier column is present in detailed View
			logger.info("Step 3:");
			Assert.assertEquals(colsText.contains(tabCashier), true);
			logger.info("Verified the Cashier column presence");
			driver.close();
			driver.switchTo().window(ParentWindow);
			logger.info(" EPL-1747 execution successful");
		} 
		catch (Throwable t)
		{
			handlePopUpException(t);
		}
	}
	
	
}
