package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1720.java $
$Id: EPL_1720.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1720 extends SuiteCardPayment
{
	
	private String country, city, shop;
	
	

	/**
	 * EPL-1720 Check the hierarchy of pages of Transaction overview / user with an organization structure level 3 - multi level 1
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1720()
	{		
		try{
			logger.info("  EPL-1720 started execution");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);	
			
			//verify bread crumb with default view
			logger.info("Step 2:");
			verifyOverviewBC(defaultOverviewBC);			
			
			//verify bread crumb with level 1 search
			logger.info("Step 3:");
			country = getObject("settlement_id").getText();
			getObject("settlement_id").click();
			verifyOverviewCityBC(country, afterLevelCardBC);		
			
			//verify bread crumb with level 2 search
			logger.info("Step 4:");
			city=getObject("all_level_id").getText();	
			getObject("all_level_id").click();
			verifyOverviewShopBC(country, city, afterLevelCardBC);	
			
			//verify bread crumb with level 3 search
			logger.info("Step 5:");
			shop=getObject("all_level_id").getText();	
			getObject("all_level_id").click();
			verifyOverviewMIDBC(country, city, shop, afterLevelCardBC);					
			verifyElementDisp("mid_xpath");
			verifyElementDisp("pos_xpath");
			verifyElementDisp("cashier_xpath");
			verifyElementDisp("card_type_xpath");			
			logger.info("MID, POS, Cashier and Card Type tabs are displayed");
			logger.info(" EPL-1720 execution successful");
			} 
		catch (Throwable t)
		{
			handleException(t);	
		}	
	}
	
}
