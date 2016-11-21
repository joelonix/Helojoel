package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1703.java $
$Id: EPL_1703.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1703 extends SuiteCardPayment
{
	/**
	 * EPL-1703 Check graph of Yesterday’s Main Information section
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1703()
	{		
		try
		{
			logger.info(" EPL-1703 execution started");
			login(CONFIG.getProperty("superuser11"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//Verify displayed graph
			logger.info("Step 2:");
			if(getObject("snapshot_graph1_xpath").getText().equals(csABGraph))
			{
				logger.info(csABGraph+" graph is displayed");
			}
			//getContains("Hourly Breakdown");
			if(getObject("snapshot_graph_xpath").getText().equals(csHourlyBGraph))
			{
				logger.info(csHourlyBGraph+" graph is displayed");
			}
				
			logger.info(" EPL-1703 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
}
