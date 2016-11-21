package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1695.java $
$Id: EPL_1695.java 7858 2014-06-09 08:59:33Z cariram $
*/
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1695 extends SuiteCardPayment
{	
	private double avg;
	private List<WebElement> vals;
	
	

	/**
	 * EPL-1695 Check Sale value Table
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1695()
	{		
		try
		{
			logger.info(" EPL-1695 execution started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//Select France country and view
			logger.info("Step 2:");
			selectDropItem("country_level_id", countryFran);
			getObject("view_link").click();
			
			logger.info("Step 3:");
			Assert.assertTrue(getObject("sale_value_xpath").isDisplayed());	
			
			//Verifying Sale Value table is present
			logger.info(" Sale Value Table is present");
			
			//Get all the values from table and verify currency
			vals=get_list_of_elements("allsale_values_xpath");
			verifyCurrencyInTable(currencyFran);			
			
			//Getting Lower, Average and Highest values
			avg = verifyLowestSaleVal();
			
			//Verifying Average Sales value = Sales Amount / (Total Sales + Total PWCB + Total Cash)
			verifyAvgSaleVal(avg);
				logger.info(" EPL-1695 execution successful");
		}
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
}
