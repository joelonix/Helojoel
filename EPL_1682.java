package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1682.java $
$Id: EPL_1682.java 7955 2014-06-11 12:37:02Z cariram $
*/
import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1682 extends SuiteCardPayment
{
	/**
	 * EPL-1682 Check presence of level 1 selector
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1682()
	{		
		try
		{
			logger.info(" EPL-1682 executing started");	
			login(CONFIG.getProperty("superuser5"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//Verifying there is top level selector	
			logger.info("Step 2:");
			verifyLeftLevel(snapCityLabel, snapShotLevel2);			
			logger.info(" There is top level selector in a drop-down box");
			logger.info(" EPL-1682 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
}
