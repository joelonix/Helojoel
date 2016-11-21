package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1680.java $
$Id: EPL_1680.java 7858 2014-06-09 08:59:33Z cariram $
*/
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1680 extends SuiteCardPayment
{
		
	private String transNumCon;	
	
	

	/**
	 * EPL-1680 Check presence of level 1 selector
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1680()
	{		
		try
		{
			logger.info(" EPL-1680 execution started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//Verifying currency and transaction numbers of all countries 
			verifyCurrency(currencyAus, countryAus, transNumCon, countryFran);
			verifyCurrency(currencyFran, countryFran, transNumCon, countrySpa);
			verifyCurrency(currencySpa, countrySpa, transNumCon, countryUK);
			verifyCurrency(currencyUK, countryUK, transNumCon, countryUK);				
			
			logger.info(" EPL-1680 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	/**
	 * Verifies currency and transaction numbers of all countries 
	 * @param currency
	 * @param country
	 * @param transNum
	 * @param selecCont
	 * @throws InterruptedException
	 */
	private void verifyCurrency(String currency, String country, String transNum, String selecCont) throws InterruptedException 
	{
		//Verifying currency of the country 
		List<WebElement> vals=get_list_of_elements("allsale_values_xpath");										
		Assert.assertEquals(((vals.get(0).getText().substring(0,3)).trim()), currency);			
		logger.info("Currency is "+currency+ " as per country "+ country);
		
		//Verify transaction numbers for the country
		transNum=getObject("total_trans_xpath").getText();
		logger.info("Transaction Numbers for " +country+ " is : "+ transNum);
		if(!country.equals(countryUK))
		{
			selectDropItem("country_level_id", selecCont);			
			//**********************3.1 change***************
			getObject("view_link").click();
			waitNSec(2);
		}		
	}
}
