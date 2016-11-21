package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1716.java $
$Id: EPL_1716.java 7955 2014-06-11 12:37:02Z cariram $
*/
import java.io.IOException;

import org.testng.annotations.Test;


public class EPL_1716 extends SuiteCardPayment{
	
	private String appTypeYes=emvFRApp, appTypeNo=migsApp;
	String [] appArr;
	
	

	/**
	 * EPL-1716 Check no presence of cashier view / a user which customer hasn’t cashier display provisioned in Everest
	 * 
	 * @throws IOException
	 */
		
	@Test()
	public void epl_1716()
	{		
		try
		{
			logger.info(" EPL-1716 executing started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1, 2:");
			cardPaymentSubPageNavigator(subModTrnOvr);
			verifyTabs(appTypeNo);				
			logger.info(" EPL-1715 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	
	
	/**
	 * verifies cashier tab
	 * @param appType
	 */
	private void verifyTabs(String appType)
	{
		selectItem(getObject("select_appli_id"), appType);		
		getObject("search_link").click();
		
		//Navigating to the lowest level with EMV FR application type
		goToMidLevel("settlement_id");		
		verifyElementDisp("mid_xpath");
		verifyElementDisp("pos_xpath");
		if(appType.equals(appTypeYes))
		{
			verifyElementDisp("cashier_xpath");
			logger.info(" MID, POS and Cashier tabs are present with "+appType);
		}
		else
		{
			if(!(isElementPresent("cashier_xpath")))
			{
				logger.info("Cashier tab is not displayed with "+appType);
			}
			logger.info(" MID, POS tabs are present with "+appType);
		}
	}
}




