package com.ingenico.eportal.testsuite.cardPayment;

import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1738 extends SuiteCardPayment
{
	/**
	 * EPL-1738 Cashier tab / Check presence of lowest level column / case of you choose an organization structure with the lowest level filter is equal to “All”
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1738()
	{		
		try{
			logger.info("  EPL-1738 started execution");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);			
			
			//Navigating to Cashier tab	
			logger.info("Step 2:");
			goToMidLevel("settlement_id", "period_id", lastMonth);	
			verifyElementDisp("mid_xpath");	
			
			logger.info("Step 3:");
			getObject("cashier_xpath").click();
						
			//Verify search with All option
			logger.info("Step 4:");
			selectItem(getObject("shop_id"), valAll);			
			getObject("search_link").click();		
			
			//Verify header columns if Shop column is present
			logger.info("Step 5:");
			verifyPresenceOfCol("cash_table_xpath", colShop);				
			logger.info("In Cashier tab : After selecting 'All' option in shop, table shows lowest level column");
			
			//Verify Shop coulmn is present in icon below the table
			getObject("column_icon_xpath").click();	
			verifyNoPresenceOfCol("columns_xpath", colShop, 1);			
			logger.info("In Cashier tab : After selecting 'All' option in shop, icon below table does not show lowest level column");	
			logger.info(" EPL-1738 execution successful");			
		}
		catch (Throwable t)
		{
			handleException(t);		
		}	
	}
	
}
