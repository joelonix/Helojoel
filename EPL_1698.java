package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1698.java $
$Id: EPL_1698.java 8005 2014-06-13 09:21:06Z cariram $
*/
import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1698 extends SuiteCardPayment
{
	/**
	 * EPL-1698 Check graphs of Yesterday’s Main Information section / Check Payment Amounts Table
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1698()
	{		
		try
		{
			logger.info(" EPL-1698 execution started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));	
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//Verify Australia country graph
			logger.info("Step 2, 3:");
			verifyGraphDisplayed(csCTBGraph, csABGraph);		
			getContains(csCityBGraph);
			logger.info(csCityBGraph+" graph are displayed for "+countryAus);
			
			//Verify France country graph
			logger.info("Step 4, 5:");
			selectDropItem("country_level_id", countryFran);
			getObject("view_link").click();
			verifyGraphDisplayed(csCTBGraph, csABGraph);
			getContains(csCityBGraph);
			logger.info(csCityBGraph+" graph are displayed for "+countryFran);			
			
			//Verify Spain country graph
			logger.info("Step 6, 7:");
			selectDropItem("country_level_id", countrySpa);
			getObject("view_link").click();
			verifyGraphDisplayed(csHourlyBGraph, csCityBGraph);			
			getContains(csCityBGraph);
			logger.info(csHourlyBGraph+" and " +csCityBGraph+" graph are displayed for "+countrySpa);
			
			//Verify UK country graph
			logger.info("Step 8, 9:");
			selectDropItem("country_level_id", countryUK);
			getObject("view_link").click();			
			verifyGraphDisplayed(csCTBGraph, csABGraph);		
			getContains(csCityBGraph);		
			logger.info(csCityBGraph+" graph are displayed for "+countryUK);
			logger.info(" EPL-1698 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
}
