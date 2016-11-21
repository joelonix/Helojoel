package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1715.java $
$Id: EPL_1715.java 7955 2014-06-11 12:37:02Z cariram $
*/
import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1715 extends SuiteCardPayment
{
	/**
	 * EPL-1715 Check no presence of cashier view / a user which customer hasn’t cashier display provisioned in Everest
	 * 
	 * @throws IOException
	 */
		
	@Test()
	public void epl_1715()
	{		
		try
		{
			logger.info(" EPL-1715 execution started");
			login(CONFIG.getProperty("superuser2"),CONFIG.getProperty("superuserPassword"));
			
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);	
			
			//Selecting level 1 organization
			logger.info("Step 2, 3:");
			goToMidLevel("settlement_id");			
			verifyElementDisp("mid_xpath");
			if(isElementPresent("pos_xpath"))
			{
				logger.info(" POS tabs is present");
			}
			if(!(isElementPresent("cashier_xpath")))
			{
				logger.info("Cashier tab is not displayed");
			}			
			logger.info(" MID, POS tabs are present and Cashier tab is not displayed");
			logger.info(" EPL-1715 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	
}




