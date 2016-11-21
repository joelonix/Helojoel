package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1702.java $
$Id: EPL_1702.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1702 extends SuiteCardPayment
{
	/**
	 * EPL-1702 Check graph of Yesterday’s Main Information section
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1702()
	{		
		try
		{
			logger.info(" EPL-1702 execution started");
			login(CONFIG.getProperty("superuser9"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//Verify displayed graph
			logger.info("Step 2:");
			verifyGraphDisplayed(csCTBGraph, csABGraph);				
			getContains(csHourlyBGraph);
			logger.info(csHourlyBGraph+" and "+csABGraph+" graph are displayed for user, "+CONFIG.getProperty("superuser9"));
			logger.info(" EPL-1702 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
}
