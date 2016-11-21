package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1763.java $
$Id: EPL_1763.java 7916 2014-06-10 12:30:12Z cariram $
*/

import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1763 extends SuiteCardPayment
{
	/**
	 * EPL-1763 Check trans journal mode in search results
	 * 
	 * @throws IOException
	 */
	
	@Test()
	public void epl_1763()
	{		
		try
		{
			logger.info(" EPL-1763 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			
			//In mode filter, checked online and checked offline and execute search
			logger.info("Step 2, 6:");
			getObject("search_link").click();
			logger.info("Search clicked with mode set to online and offline");
			
			//Fetching number of pages 
			totalPages=fetchingNoOfPages();
			
			//Verifying if mode column has offline or online images
			vColImgs(totalPages, "mode_col_xpath", "mode_image_xpath", onlineLabel, offlineLabel);
			
			logger.info("Started validation for mode online");
			//checked only online box and execute search
			logger.info("Step 3:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			getObject("advanced_search_link").click();
			getObject("offline_id").click();
			getObject("search_link").click();
			
			//Fetching number of pages 
			totalPages=fetchingNoOfPages();
			
			//Verifying if mode column has online image
			vColImgs(totalPages, "mode_col_xpath", "mode_image_xpath", onlineLabel, offlineLabel);
			
			//checked only offline box and execute search
			logger.info("Step 4, 5:");
			logger.info("Starting execution for mode offline");
			cardPaymentSubPageNavigator(subModTrnJrn);
			getObject("advanced_search_link").click();
			getObject("online_id").click();
			getObject("search_link").click();
			
			//Fetching number of pages 
			totalPages=fetchingNoOfPages();
			
			//Verifying if mode column has online image
			vColImgs(totalPages, "mode_col_xpath", "mode_image_xpath", onlineLabel, offlineLabel);
			logger.info(" EPL-1763 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	

}
