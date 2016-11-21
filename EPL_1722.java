package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1722.java $
$Id: EPL_1722.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1722 extends SuiteCardPayment
{
	
	private String city, shop, appType;
	
	

	/**
	 * EPL-1722 Check the hierarchy of pages of Transaction overview / user with an organization structure level 2 - multi level 1
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1722()
	{		
		try{
			logger.info("  EPL-1722 started execution");
			login(CONFIG.getProperty("superuser5"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			getObject("cardpayment_link").click();			
			getObject("transaction_overview_link").click();						
			getObject("search_link").click();				
			
			//verify bread crumb with all application search
			logger.info("Step 2:");
			verifyOverviewBC(defaultOverviewBC);			
			Assert.assertEquals(getObject("column_width_xpath").getText(),overCityLabel);
			logger.info("In Main view for level 1: the coherency of breadcrumb is as expected and only cities are displayed");
			
			//verify bread crumb with level 2 search
			logger.info("Step 3:");
			city = getObject("settlement_id").getText();
			getObject("settlement_id").click();
			appType = getSelectedItem(getObject("select_appli_id"));
			verifyOverviewBC(beforeLevelBC+city+" » "+appType+afterLevelNoCardBC);			
			
			Assert.assertEquals(getObject("column_width_xpath").getText(),overShopLabel);
			logger.info("In Main view for level 2: the coherency of breadcrumb is as expected and only shops are displayed");
			
			//verify bread crumb with level 3 search
			logger.info("Step 4:");
			shop=getObject("all_level_id").getText();	
			getObject("all_level_id").click();
			verifyOverviewBC(beforeLevelBC+city+" » "+shop+" » "+allMidBC+" » "+appType+afterLevelNoCardBC);						
			logger.info("In Main view for lowest level : the coherency of breadcrumb is as expected ");			
			if(appType.equals(emvESApp))
			{
				verifyElementDisp("mid_xpath");
				verifyElementDisp("pos_xpath");				
				logger.info("MID, POS tabs are displayed ");
			}
			else
			{
				verifyElementDisp("mid_xpath");
				verifyElementDisp("pos_xpath");
				verifyElementDisp("cashier_xpath");
				verifyElementDisp("card_type_xpath");				
				logger.info("MID, POS, Cashier and Card Type tabs are displayed");
			} 			
			logger.info(" EPL-1722 execution successful");
			
		}
		catch (Throwable t)
		{
			handleException(t);	
		}	
	}
	
}
