package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1697.java $
$Id: EPL_1697.java 7858 2014-06-09 08:59:33Z cariram $
*/
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class EPL_1697 extends SuiteCardPayment
{
	private	double avg;
	private List<WebElement> vals;
	
	

	/**
	 * EPL-1697 Check Sale value Table
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1697()
	{		
		try
		{
			logger.info(" EPL-1697 execution started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//Select France country and view
			logger.info("Step 2:");
			selectDropItem("country_level_id", countryUK);
			getObject("view_link").click();	
			
			logger.info("Step 3:");
			verifyElementDisp("sale_value_xpath");
			
			//Verifying Sale Value table is present
			logger.info(" Sale Value Table is present");
			
			//Get all the values from table and verify currency
			vals=get_list_of_elements("allsale_values_xpath");
			verifyCurrencyInTable(currencyUK);			
			
			
			//Verifying Lowest Sale Value < Average Sale Value < Highest Sale Value
			avg = verifyLowestSaleVal();					
			
			//Verifying Sales Amount / Total Sales
			verifyAvgSaleVal(avg);			
			logger.info(" EPL-1697 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
}
