package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1684.java $
$Id: EPL_1684.java 7955 2014-06-11 12:37:02Z cariram $
*/
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1684 extends SuiteCardPayment
{
	/**
	 * EPL-1684 Check presence of level 1 selector
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1684()
	{		
		try
		{
			logger.info(" EPL-1684 execution started");	
			login(CONFIG.getProperty("superuser9"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//Verifying there is top level selector (list of shops)
			logger.info("Step 2:");
			Assert.assertEquals(getObject("left_level_id").getText(), shopLabel);
			logger.info(" There is a top level selector (list of shops)  in a drop-down box");
			logger.info(" EPL-1684 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
}
