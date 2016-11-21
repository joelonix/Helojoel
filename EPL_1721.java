package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1721.java $
$Id: EPL_1721.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1721 extends SuiteCardPayment
{
	
	private String city, shop,country;	
	

	/**
	 * EPL-1721 Check the hierarchy of pages of Transaction overview / user with an organization structure level 3 - multi level 1
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1721()
	{		
		try{
			logger.info("  EPL-1721 started execution");
			login(CONFIG.getProperty("superuser4"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);					
			getObject("search_link").click(); 
			
			//verify bread crumb with all application search
			logger.info("Step 2:");
			verifyOverviewBC(defaultOverviewBC);			
			Assert.assertEquals(getObject("column_width_xpath").getText(),colCountry); 
			logger.info("In Main view for Country: the coherency of breadcrumb is as expected and only Country is displayed");
			
			//verify bread crumb with level 2 search
			logger.info("Step 3:");
			country = getObject("settlement_id").getText();
			getObject("settlement_id").click();
			verifyOverviewCityBC(country, afterLevelNoCardBC);		
			Assert.assertEquals(getObject("column_width_xpath").getText(),colCity); 
			logger.info("In Main view for City: the coherency of breadcrumb is as expected and only City is displayed");
			
			//verify bread crumb with level 2 search
			logger.info("Step 4:");
			city=getObject("settlement_id").getText();			
			getObject("settlement_id").click();
			verifyOverviewShopBC(country, city, afterLevelNoCardBC);			
			Assert.assertEquals(getObject("column_width_xpath").getText(),colShop); 
			logger.info("In Main view for Shop: the coherency of breadcrumb is as expected and only Shop is displayed");
			
			//verify bread crumb with level 3 search
			logger.info("Step 5:");
			shop=getObject("settlement_id").getText();	
			getObject("settlement_id").click();
			verifyOverviewMIDBC(country, city, shop, afterLevelNoCardBC);		
			
			verifyElementDisp("mid_xpath");
			verifyElementDisp("pos_xpath");			
			logger.info("MID, POS tabs are displayed ");
			if(isElementPresent("cashier_xpath"))
			{				
				verifyElementDisp("cashier_xpath");				
				logger.info("Cashier  tab is displayed");
			} 
			if(isElementPresent("card_type_xpath"))
			{				
				verifyElementDisp("card_type_xpath");					
				logger.info("Card Type tab is displayed");
			} 
			
			logger.info(" EPL-1721 execution successful");
			
		}
		catch (Throwable t)
		{
			handleException(t);		
		}	
	}
	
}
