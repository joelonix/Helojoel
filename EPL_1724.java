package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1724.java $
$Id: EPL_1724.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1724 extends SuiteCardPayment
{
	
	private String shop, appType;
		
	

	/**
	 * EPL-1724 Check the hierarchy of pages of Transaction overview / user with an organization structure level 3 - multi level 1
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1724()
	{		
		try{
			logger.info("  EPL-1724 started execution");
			login(CONFIG.getProperty("superuser9"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			getObject("cardpayment_link").click();			
			getObject("transaction_overview_link").click();						
			getObject("search_link").click(); 			
			
			//verify bread crumb with all application search
			logger.info("Step 2:");
			verifyOverviewBC(defaultTransBC);			
			Assert.assertEquals(getObject("column_width_xpath").getText(), overShopLabel);
			logger.info("In Main view for level 1: the coherency of breadcrumb is as expected and only Shops are displayed");
			
			//verify bread crumb with level 2 search
			logger.info("Step 3:");
			shop = getObject("settlement_id").getText();
			getObject("settlement_id").click();			
			appType = getSelectedItem(getObject("select_appli_id"));
			verifyOverviewBC(beforeLevelBC+shop+" » "+allMidBC+" » "+appType+" » "+allCardTypeBC);				
			logger.info("In Main view for level 2: the coherency of breadcrumb is as expected ");
			
			if(appType.equals(emvAUSApp))
			{
				verifyElementDisp("mid_xpath");
				verifyElementDisp("pos_xpath");
				verifyElementDisp("cashier_xpath");
				verifyTabPresence("card_type_xpath");
				verifyElementDisp("card_type_xpath");				
				logger.info("MID, POS tabs, Cashier and Card Type are displayed ");
			}
			else
			{
				Assert.assertTrue(getObject("pos_xpath").isDisplayed());				
				Assert.assertTrue(getObject("mid_xpath").isDisplayed());
				logger.info("MID, POS tabs are displayed");
			} 
			
			logger.info(" EPL-1724 execution successful");
			
		}
		catch (Throwable t)
		{
			handleException(t);	
		}	
	}
	
}
