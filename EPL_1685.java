package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1685.java $
$Id: EPL_1685.java 7858 2014-06-09 08:59:33Z cariram $
*/
import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1685 extends SuiteCardPayment
{
	/**
	 * EPL-1685 Check presence of level 1 selector
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1685()
	{		
		try
		{
			logger.info(" EPL-1685 executing started");
			login(CONFIG.getProperty("superuser10"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			logger.info("Step 2:");
			verifyElementNotPresent(snapShotLevel1);
			logger.info(" There is a top level selector (list of shops)  in a drop-down box");
			logger.info(" EPL-1685 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
}
