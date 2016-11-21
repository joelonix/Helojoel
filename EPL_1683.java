package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1683.java $
$Id: EPL_1683.java 7955 2014-06-11 12:37:02Z cariram $
*/
import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1683 extends SuiteCardPayment
{
	/**
	 * EPL-1683 Check Sale value Table
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1683()
	{		
		try
		{
			logger.info(" EPL-1683 execution started");	
			login(CONFIG.getProperty("superuser7"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//Verifying there is no top level selector
			logger.info("Step 2:");
			verifyElementNotPresent(snapShotLevel1);
			logger.info(" There is no top level selector in a drop-down box");
			logger.info(" EPL-1683 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
}
