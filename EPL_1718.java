package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1718.java $
$Id: EPL_1718.java 7955 2014-06-11 12:37:02Z cariram $
*/
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1718 extends SuiteCardPayment{
	
	private String midVal="1234567890";	
	
	

	/**
	 * EPL-1718 To verify the card type tab in Transaction Overview
	 * 
	 * @throws IOException
	 */
	
	@Test()
	public void epl_1718()
	{		
		try
		{
			logger.info(" EPL-1718 executing started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);	
			verifyBreadCrumb(cpTransOverBC);
			
			//Setting MID value
			logger.info("Step 2:");
			getObject("mid_id").sendKeys(midVal);
			
			//Selecting application type
			selectItem(getObject("select_appli_id"), emvAUSApp);
			
			//Verify Card Type dropdown is visible
			verifyElementDisp("card_type_id");			
			
			//Selecting card type
			logger.info("Step 3, 4:");
			selectItem(getObject("card_type_id"), cardType);
			logger.info("Set all values: MID, Application Type, Card Type");

			getObject("search_link").click();
			logger.info("Clicked Search");
			
			//Verify Card Type tab is visible after search
			if(isElementPresent("card_type_xpath"))
			{	
				verifyElementDisp("card_type_xpath");
				logger.info("Card Type tab is visible");
			}
			else
			{
				logger.info("Card Type tab is not present to execute the test");
				Assert.fail("Card Type tab is not present to execute the test");
			}			
			logger.info(" EPL-1718 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	

}
