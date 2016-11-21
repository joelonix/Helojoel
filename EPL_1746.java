package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1746.java $
$Id: EPL_1746.java 7888 2014-06-09 13:17:58Z cariram $
*/

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1746 extends SuiteCardPayment
{
	/**
	 * EPL-1746 Check no presence of cashier display / a user which customer hasn’t cashier display provisioned in Everest
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1746()
	{		
		try{
			logger.info("  EPL-1746 started execution");
			login(CONFIG.getProperty("superuser2"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);	
			Assert.assertFalse(getObject("cashier_lable_xpath").isDisplayed());
			
			logger.info("Step 2:");
			getObject("search_link").click(); 
			logger.info("Cashier field is not present in main view");
			
			//Reset the table configuration.
			logger.info("Reset the table configuration.");
			restoreTables();
			
			//opening the detailed transaction window
			logger.info("Step 3:");
			getObject("plus_icon_xpath").click();			
			switchToWindow();
			verifyElementNotPresent(tabCashier);
			logger.info("Cashier field is not present in detailed view");
			
			driver.close();
			driver.switchTo().window(ParentWindow);	
			logger.info(" EPL-1746 execution successful");
			
		}
		catch (Throwable t)
		{
			handlePopUpException(t);	
		}	
	}
	
}
