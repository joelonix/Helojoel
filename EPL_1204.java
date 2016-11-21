package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1204.java $
$Id: EPL_1204.java 7858 2014-06-09 08:59:33Z cariram $
*/
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1204 extends SuiteCardPayment
{	
	

	/**
	 * EPL-1204 Check that an user with only TMS module, has not acess to Dashboard module
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1204()
	{		
		try
		{
			logger.info(" EPL-1204 execution started");			
			login(CONFIG.getProperty("superuser12"),CONFIG.getProperty("superuserPassword"));
			
			//Verify that Dashboard module is not present.
			logger.info("Step 1:");
			Assert.assertFalse(isElementPresent("dashoard_link"), "Dashboard module is displayed for the user, "+CONFIG.getProperty("superuser12"));
			logger.info(" Verified that Dashboard module is not present.");

			logger.info(" EPL-1204 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
}