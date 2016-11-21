package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1719.java $
$Id: EPL_1719.java 7759 2014-06-04 12:10:20Z cariram $
*/
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1719 extends SuiteCardPayment{
	
	
	private String midVal="1234567890";	
		
	

	/**
	 * EPL-1719 To verify the card type tab is not visible in Transaction Overview
	 * 
	 * @throws IOException
	 */
	
	@Test()
	public void epl_1719()
	{		
		try
		{
			logger.info(" EPL-1719 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));	
			cardPaymentSubPageNavigator(subModTrnOvr);	
			verifyBreadCrumb(cpTransOverBC);
			
			//Setting MID 
			getObject("mid_id").sendKeys(midVal);

			//Selecting app type "Amex" and verify Card type dropdown is not visible 
			selectItem(getObject("select_appli_id"), amexApp);
			Assert.assertEquals(isElementPresent("card_type_id"), false);
			logger.info("Card Type dropdown not displayed");

			logger.info("Set all values");

			getObject("search_link").click();
			logger.info("Clicked Search");

			//Verify card Type tab is not visible after search
			Assert.assertEquals(isElementPresent("card_type_xpath"), false);
			logger.info("Card Type tab is not visible");
			logger.info(" EPL-1719 execution successful");

		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	


}
