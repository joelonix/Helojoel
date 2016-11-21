package com.ingenico.eportal.testsuite.cardPayment;

import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1739 extends SuiteCardPayment
{
	/**
	 * EPL-1739 Cashier tab / Check no presence of lowest level column / if the search gives only one result in the organization 
	 * structure list AND when the search was done on a lowest level organization structure
	 * 
	 * @throws IOException
	 */
	
	@Test()
	public void epl_1739()
	{		
		try
		{
			logger.info(" EPL-1739 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);	
			
			//Navigate to last level
			logger.info("Step 2:");
			goToMidLevel("settlement_id");			
			verifyElementDisp("mid_xpath");		
			logger.info("MID Tab is displayed after navigating to the lowest level");		
			
			//Select shop and do search	
			logger.info("Step 3:");
			selectItem(getObject("shop_id"), brisbaneSop1);			
			getObject("search_link").click();
			getObject("settlement_id").click();	
			
			//Verify if Cashier tab is displayed
			logger.info("Step 4:");
			getObject("cashier_xpath").click();
			verifyElementDisp("cashier_xpath");			
			logger.info("Cashier Tab is displayed ");
			
			//Verify list of columns present in the header of the table
			logger.info("Step 5:");
			verifyNoPresenceOfCol("cash_table_xpath", colShop);
			logger.info("Lowest level column is displayed in the table");
			
			//Verify Shop coulmn is present in icon below the table
			getObject("column_icon_xpath").click();		
			verifyNoPresenceOfCol("columns_xpath", colShop);			
			logger.info("Lowest level column is not displayed in icon columns below the table");	
			logger.info(" EPL-1739 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	

}
