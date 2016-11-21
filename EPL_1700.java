package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1700.java $
$Id: EPL_1700.java 7858 2014-06-09 08:59:33Z cariram $
*/
import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1700 extends SuiteCardPayment
{
	/**
	 * EPL-1700 Check graphs of Yesterday’s Main Information section / Check Payment Amounts Table
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1700()
	{		
		try
		{
			logger.info(" EPL-1700 execution started");	
			login(CONFIG.getProperty("superuser5"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//Verify Australia country graph
			logger.info("Step 2:");
			verifyGraph("snapshot_graph1_xpath", csHourlyBGraph);			
			if(isElementPresent("snapshot_graph_xpath"))	
			{
				verifyGraph("snapshot_graph_xpath", csShopBGraph);				
			}	
					
			logger.info(" EPL-1700 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
}
