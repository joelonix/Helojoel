package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1768.java $
$Id: EPL_1768.java 7916 2014-06-10 12:30:12Z cariram $
 */

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1768 extends SuiteCardPayment{
	
	private String  midVal = "12345678", posVal = "234567", cashierVal = "100230", authNum = "123456778", 
				   todayDate,fromDate,toDate, emptyString = "";
	
	

	/**
	 * EPL-1768 All applications / Check filters / Check reset filters button
	 * 
	 * @throws IOException
	 */
	
	@Test()
	public void epl_1768()
	{		
		try
		{
			logger.info(" EPL-1768 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);	
			
			//In Main criteria
			//Setting date and period filters
			logger.info("Step 2:");
			setAllFilters();
			
			//Resetting all values by clicking reset button
			getObject("reset_link").click();
			logger.info("Set all advanced search values");
			
			//Verify if values are reset
			VerifyResetValues();
			
			logger.info(" EPL-1768 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	
	
	/**
	 * setting all values in filters
	 */
	private void setAllFilters()
	{
		selectItem(getObject("period_id"), lastMonth);
		
		//Setting Country-France, City-Lyon, Shop - LyonShop_2
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("document.getElementById('level_1').value='"+contFranVal+"'");
		js.executeScript("document.getElementById('level_2').value='"+cityLyonVal+"'");
		js.executeScript("document.getElementById('level_3').value='"+shop1LyonVal+"'");
		
		//Set MID, POS, Cashier values
		getObject("mid_id").sendKeys(midVal);
		getObject("pos_id").sendKeys(posVal);
		getObject("cashier_id").sendKeys(cashierVal);
		
		//Set min and max amount
		getObject("min_amount_id").sendKeys(pageLimit20);
		getObject("max_amount_id").sendKeys(pageLimit50);
		
		//Set application type and unchecked transtype "Fail" checkbox
		//Select EMV FR as app type
		multiSelect("select_appli", emvFRAppVal);		
		getObject("search_link").click();
		getObject("advanced_search_link").click();
		getObject("fail_checkbox_xpath").click();
		logger.info("Set all values");
		
		//Set settlement ID and mode
		getObject("no_id").click();		
		
		//Set customized fields
		getObject("auth_num_id").sendKeys(authNum);		
		new Select(getObject("journal_userData2_en_xpath")).selectByIndex(1);
	}
	
	/**
	 * verifying all values are reset
	 */
	private void VerifyResetValues()
	{
		fromDate = getObject("strt_date_id").getAttribute("value");
		toDate = getObject("end_date_id").getAttribute("value");
		Assert.assertTrue(fromDate.equals(getTodayDate()));
		Assert.assertTrue(toDate.equals(getTodayDate()));
		logger.info("Validated the dates are reset to today's date");
		
		Assert.assertEquals(getSelectedItem(getObject("period_id")), periodToday);
		logger.info("Validated Period is reset to Today");
		
		Assert.assertEquals(getSelectedItem(getObject("country_id")), valAll);
		Assert.assertEquals(getSelectedItem(getObject("city_id")), valAll);
		Assert.assertEquals(getSelectedItem(getObject("shop_id")), valAll);
		logger.info(" Validated Country, City and Shop dropdowns are reset");
		
		Assert.assertEquals(getObject("mid_id").getText(), emptyString);
		Assert.assertEquals(getObject("pos_id").getText(), emptyString);
		Assert.assertEquals(getObject("cashier_id").getText(), emptyString);
		logger.info("Validated MID, POS and Cashier fields are reset to blanks");
	
		Assert.assertEquals(getObject("min_amount_id").getText(), emptyString);
		Assert.assertEquals(getObject("max_amount_id").getText(), emptyString);
		logger.info("Validated Min and max amount fields are reset to blanks");
		
		Assert.assertEquals(getSelectedItem(getObject("select_appli_id")), valAll);
		Assert.assertEquals(getObject("cp_check_all_id").isSelected(), true);
		logger.info("Validated AppType is reset to All and all transaction type checkboxes are checked");
		
		Assert.assertEquals(getObject("no_id").isSelected(), true);
		Assert.assertEquals(getObject("yes_id").isSelected(), true);
		logger.info("Validated the both settlement filters are checked");
		
		Assert.assertEquals(getObject("online_id").isSelected(), true);
		Assert.assertEquals(getObject("offline_id").isSelected(), true);
		logger.info("Validated Mode checkboxes are checked");
		
		getObject("advanced_search_link").click();
		Assert.assertEquals(getObject("auth_num_id").getText(), emptyString);		
		Assert.assertEquals(getSelectedItem(getObject("journal_userData2_en_xpath")), valAll);
		logger.info("Customized fields, auth num is reset to blank and userData2_EN dropdown is reset to All");
	}
	
	/**
	 * Get today's date in yyyy-MM-dd format
	 * @return yestDate
	 */
	private String getTodayDate(){
		Calendar today = Calendar.getInstance();  
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		todayDate = dateFormat.format(today.getTime());
		return todayDate;
	}

}
