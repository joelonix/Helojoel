package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1783.java $
$Id: EPL_1783.java 7916 2014-06-10 12:30:12Z cariram $
 */
import java.io.IOException;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1783 extends SuiteCardPayment
{
	/**
	 * EPL-1782 To Verify the alert popup seen when date range is chosen as 2 or more months and reconciliation main window is seen when date range is within a month Transaction Reconciliation
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1783()
	{		
		try
		{
			logger.info(" EPL-1783 executing started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnRecon);
			verifyBreadCrumb(cpReconBC);
			logger.info("Transaction Reconciliation submodule is visible");

			//Verification of organization level of user and their default value as Choose one
			logger.info("Step 2:");
			verifyDefaultLevelVal("country_id");
			verifyDefaultLevelVal("city_id");
			verifyDefaultLevelVal("shop_id");			

			//Alert msg is seen when Choose an organization level without choose a lowest level and execute search
			logger.info("Step 3:");
			selectItem(getObject("country_id"), countryAus);
			getObject("search_link").click();
			logger.info("Search clicked with country chosen");
			Assert.assertEquals(getObject("alert_text_xpath").getText(), alertmsgRecon);
			getObject("alert_Ok_xpath").click();
			logger.info("Alert info validated");

			//Choose lowest level organization path to see main window
			logger.info("Step 4:");
			selectItem(getObject("shop_id"), shop1Sydney);	
			getObject("search_link").click();
			verifyBreadCrumb("Transaction Reconciliation");
			logger.info("validated search when level3 is chosen");
			logger.info(" EPL-1783 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	/**
	 * verifies default level value
	 * @param locatorLevel
	 */
	private void verifyDefaultLevelVal(String locatorLevel)
	{
		WebElement level = getObject(locatorLevel);
		Assert.assertEquals(level.isDisplayed(), true);
		Assert.assertTrue(getSelectedItem(level).contains(defaultDropdownVal), defaultDropdownVal +" is not disaplayed as deafult value");
		logger.info("Validated "+level+" dropdown and default value");
	}
}
