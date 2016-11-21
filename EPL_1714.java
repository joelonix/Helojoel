package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1714.java $
$Id: EPL_1714.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;


public class EPL_1714 extends SuiteCardPayment{
	
	private String midVal="123456", posVal = "23456",startDate,endDate;	
	
	
	

	/**
	 * verifyDropdownIsReset verifies if dropdown is reset to default value
	 * @param selectId
	 * @param resetVal
	 */
	public void verifyDropdownIsReset(String selectId, String resetVal)
	{
		String actual = getSelectedItem(getObject(selectId));
		Assert.assertEquals(actual, resetVal);
	}
	/**
	 * EPL-1714 To verify the reset filter button resets all options in Transaction Overview
	 * 
	 * @throws IOException
	 */
	@Test()
	public void epl_1714()
	{		
		try
		{
			logger.info(" EPL-1714 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			SetTimeZone();
			cardPaymentSubPageNavigator(subModTrnOvr);	
			
			//Selecting level 1 organisation
			getObject("settlement_id").click();
			
			//Setting values period,mid and pos
			logger.info("Step 2:");
			setAllFilters(emvAUSApp);			

			logger.info("Step 3:");
			getObject("reset_link").click();			
			logger.info("Clicked Reset button");
			
			//Verify period and date are reset
			logger.info("Step 4, 5:");
			verifyFiltersReset();			
			logger.info(" EPL-1714 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	
	

	/**
	 * set all the values in the filters
	 */
	private void setAllFilters(String arrItem)
	{
		//Setting values period,mid and pos
		selectItem(getObject("period_id"),periodYest);
		
		getObject("mid_id").sendKeys(midVal);
		getObject("pos_id").sendKeys(posVal);		
		
		//Setting card application type and card type
		selectItem(getObject("select_appli_id"), emvAUSApp);
		selectItem(getObject("card_type_id"), visaCardType);		
		logger.info("Set all values");
	}
	
	/**
	 * verifies after clicking on reset button filters are reset
	 */
	private void verifyFiltersReset()
	{
		verifyDropdownIsReset("period_id", periodToday);
		
		startDate = getObject("strt_date_id").getAttribute("value");
		endDate = getObject("end_date_id").getAttribute("value");
		
		Assert.assertEquals(startDate, getTodaysDate());
		Assert.assertEquals(endDate, getTodaysDate());
		
		//verifying other dropdowns
		verifyDropdownIsReset("select_appli_id", valAll);
		Assert.assertFalse(isElementPresent("card_type_id"));
		
		//Verify if mid and pos are reset
		verifyMidPosReset("mid_id");
		verifyMidPosReset("pos_id");		
		logger.info("Values are Reset: application dropdown,period, mid and pos");
	}
}




