package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1701.java $
$Id: EPL_1701.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1701 extends SuiteCardPayment
{
	/**
	 * EPL-1701 To Verify graphs displayed on Snapshot page
	 * 
	 * @throws IOException
	 */
	
	@Test()
	public void epl_1701()
	{		
		try
		{
			logger.info(" EPL-1701 execution started");	
			login(CONFIG.getProperty("superuser7"),CONFIG.getProperty("superuserPassword"));	
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			logger.info("Step 2:");
			if(getObject("snapshot_graph1_xpath").getText().equals(csABGraph))
			{
				logger.info(csABGraph+ " graph is displayed");
			}			
			
			//Verifying if application breakdown and Level 2 breakdown graph is displayed
			if(isElementPresent("snapshot_graph_xpath"))	
			{
				if(getObject("snapshot_graph_xpath").getText().equals(csLevel2BGraph))
				{
					logger.info(csLevel2BGraph+" graph is displayed");
				}
				else
				{
					logger.info(csCTBGraph+" graph is displayed");
				}
			}
			
			logger.info(" EPL-1701 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	
}

