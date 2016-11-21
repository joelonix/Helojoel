package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1785.java $
$Id: EPL_1785.java 7916 2014-06-10 12:30:12Z cariram $
 */
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1785 extends SuiteCardPayment {
	private String yest =  null,  startDate;	

	
	/**
	 * EPL-1785 To Verify the reset filter button resets all options in Transaction Reconciliation
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1785()
	{		
		try
		{
			logger.info(" EPL-1785 executing started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnRecon);
			verifyBreadCrumb(cpReconBC);
			logger.info("Transaction Reconciliation submodule is visible");

			//Set start date , select country and uncheck the store and bank reconciliation	
			logger.info("Step 2:");
			selectItem(getObject("country_id"), countryAus);
			getObject("reconciliation_store_id").click();
			getObject("reconciliation_bank_id").click();
			logger.info("Set country, "+countryAus+" and unchecked store and bank");

			//Click Reset Filters
			logger.info("Step 3:");
			getObject("reset_link").click();
			waitForTxtPresent("country_id",defaultCountryVal);
			logger.info("Clicked Reset link");

			//Get Systems yesterday date
			waitForCommonElementPresent("breadcrumb_id");
			yest = getYestDate();

			//Verify date is set to yesterday's date
			startDate = getObject("strt_date_id").getAttribute("value");
			Assert.assertEquals(startDate, yest);
			logger.info("Verified date is reset to yesterday");

			//Verify Country dropdown is reset
			Assert.assertTrue(getSelectedItem(getObject("country_id")).contains(defaultDropdownVal),defaultDropdownVal+ " is not displayed by default");

			//Verify the checkboxes are reset(they are checked)
			verifyElementSelected("reconciliation_store_id");
			verifyElementSelected("reconciliation_bank_id");
			logger.info("Country dropdown and Store and bank checkboxes are reset");
			logger.info(" EPL-1785 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

}

