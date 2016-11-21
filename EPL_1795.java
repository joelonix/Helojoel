package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1795.java $
$Id: EPL_1795.java 7916 2014-06-10 12:30:12Z cariram $
 */
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1795 extends SuiteCardPayment
{
	/**
	 * EPL-1795 Name of favourite link must be called “Saved searches” in Card
	 * Payment - Transaction Journal sub-module
	 * 
	 * @throws IOException
	 */
	@Test()
	public void epl_1795()
	{
		try{
			logger.info("  EPL-1795 started execution");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1, 2:");
			Assert.assertTrue(getObject("ce_saved_searches_xpath").getText().contains(savedSearches));
			logger.info(savedSearches + " is present");
			logger.info(" EPL-1795 execution successful");
			} 
		catch (Throwable t)
		{
			handleException(t);	
		}		
	}	
}
