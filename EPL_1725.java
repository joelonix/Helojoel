package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1725.java $
$Id: EPL_1725.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1725 extends SuiteCardPayment
{
	
	private String actVal, appType, levelVal;	
	
	

	/**
	 * EPL-1725 Check the hierarchy of pages of Transaction overview / user with an organization structure level 1 - mono level 1
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1725()
	{		
		try{
			logger.info("  EPL-1725 started execution");
			login(CONFIG.getProperty("superuser10"),CONFIG.getProperty("superuserPassword"));	
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);					
			getObject("search_link").click(); 
			
			//breadCrumbText = getObject("bread_crumb_id").getText();
			levelVal = getObject("settlement_id").getText();
			Assert.assertEquals(getObject("column_width_xpath").getText(),overLevelLabel);
			
			//verify bread crumb with all application search and verify only one country is displayed
			logger.info("Step 2:");
			verifyOverviewBC(beforeLevelBC+levelVal+afterLevelBC);
			Assert.assertEquals(getObject("column_width_xpath").getText(), overLevelLabel);
			actVal = getPageNum();
			Assert.assertEquals(actVal, "1");						
			logger.info("Level 1 page of transaction overview sub-module is displayed and there is only one level 1 :"+levelVal);
			
			//verify bread crumb with level 2 search
			logger.info("Step 3:");
			getObject("settlement_id").click();
			verifyOverviewBC(beforeLevelBC+levelVal+afterLevelMidAppBC);			
			appType = getSelectedItem(getObject("select_appli_id"));	
			logger.info("In Main view for lowest level : the coherency of breadcrumb is as expected ");
			
			if(appType.equals(valAll))
			{
				verifyElementDisp("mid_xpath");
				verifyElementDisp("pos_xpath");				
				if(isElementPresent("cashier_xpath"))
				{
					logger.info("Cashier tab is displayed ");
				}
				if(isElementPresent("card_type_xpath"))
				{
					logger.info("Card Type tab is displayed ");
				}
				Assert.assertTrue(isElementPresent("cashier_xpath") | isElementPresent("card_type_xpath"));
				
				logger.info("MID, POS tabs are displayed ");
			}
			else
			{
				verifyElementDisp("mid_xpath");
				verifyElementDisp("pos_xpath");				
				logger.info("MID, POS tabs are displayed");
			} 
			
			logger.info(" EPL-1725 execution successful");
			
		}
		catch (Throwable t)
		{
			handleException(t);		
		}	
	}
	
}
