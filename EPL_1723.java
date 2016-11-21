package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1723.java $
$Id: EPL_1723.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1723 extends SuiteCardPayment
{
	
	private String shop;
		
	

	/**
	 * EPL-1723 Check the hierarchy of pages of Transaction overview / user with an organization structure level 2 - multi level 1
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1723()
	{		
		try{
			logger.info("  EPL-1723 started execution");
			login(CONFIG.getProperty("superuser7"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			getObject("cardpayment_link").click();			
			getObject("transaction_overview_link").click();						
			getObject("search_link").click();				
			
			//verify bread crumb with all application search
			logger.info("Step 2, 3:");
			verifyOverviewBC(defaultTransBC+afterLevelNoCardBC);			
			logger.info("In Main view for level 1: the coherency of breadcrumb is as expected and only one city is displayed");
			
			//verify bread crumb with level 1 search
			logger.info("Step 4:");
			getObject("settlement_id").click();
			shop = getObject("settlement_id").getText();
			getObject("settlement_id").click();
			verifyOverviewBC(beforeLevelBC+shop+afterLevelMidAppBC);					
			logger.info("In Main view for level 3: the coherency of breadcrumb is as expected ");		
			verifyElementDisp("mid_xpath");
			verifyElementDisp("pos_xpath");
			if(isElementPresent("cashier_xpath"))
			{							
				verifyElementDisp("cashier_xpath");
				logger.info("Cashier  tab is displayed");
			} 
			else
			{
				logger.info("Cashier tab is not present");				
			}
			if(isElementPresent("cardtype_xpath"))
			{					
				verifyElementDisp("cardtype_xpath");
				logger.info("Card Type tab is displayed");
			}
			else
			{
				logger.info("Card Type tab is not present ");				
			}			
			logger.info("MID, POS tabs are displayed");
			logger.info(" EPL-1723 execution successful");
			} 
		catch (Throwable t)
		{
			handleException(t);	
		}	
	}
	
}
