package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1782.java $
$Id: EPL_1782.java 8030 2014-06-16 06:30:03Z cariram $
 */
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1782 extends SuiteCardPayment{
	private boolean  isFromCalDisplayed = false, isToCalDisplayed = false;
	private String startDate,endDate,alert_msg , month1,periodMsg="The period must be less than 31 days.";;	
	private int year1,day1;
	public static  Locale locale = Locale.ENGLISH;
	
	/**
	 * EPL-1782 To Verify the alert popup seen when date range is chosen as 2 or more months and reconciliation main window is seen when date range is within a month Transaction Reconciliation
	 * 
	 * @throws IOException
	 */
	@Test()
	public void epl_1782()
	{		
		try
		{
			logger.info(" EPL-1782 executing started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));	
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnRecon);
			verifyBreadCrumb(cpReconBC);
			logger.info("Transaction Reconciliation submodule is visible");
			
			//Verification 1 - The title must be the name of user data 1’s customer is pending on clarification
			Assert.assertEquals(getObject("cprecon_userdata_xpath").getText(), colNUS);
			
			//Verify filter contains From and To Calenders
			logger.info("Step 2:");
			isFromCalDisplayed = getObject("date_id").isDisplayed();
			Assert.assertEquals(isFromCalDisplayed, true);
			
			isToCalDisplayed = getObject("to_date_id").isDisplayed();
			Assert.assertEquals(isToCalDisplayed, true);
			logger.info("From and To date filters are visible");
			
			//Verify, By default, each calendar must contain day date for start date
			startDate = getObject("strt_date_id").getAttribute("value");
			Assert.assertEquals(dateHasCorrectFormat(startDate), true);			
			
			//Verify, By default, each calendar must contain day date for end date 
			endDate = getObject("end_date_id").getAttribute("value");
			Assert.assertEquals(dateHasCorrectFormat(endDate), true);
			logger.info("Date format validated");
			waitNSec(2);
			
			// A popup is automatically opened with this error message “The period must be less than 31 days.”
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", locale);	
			Calendar cal = Calendar.getInstance(locale);			
			cal.add(Calendar.DATE, (-65));			
			Date newDate = cal.getTime();			
			String date = formatter.format(newDate);			
			year1=cal.get(Calendar.YEAR);				
			month1=getMonthForInt(cal.get(Calendar.MONTH)+1);			
			day1=cal.get(Calendar.DATE);						
			setStartDate(year1, month1, day1);
			logger.info("Setting start date with more than 2 months duration difference");
			
			//Verify alert message when date range is more than 2 months
			logger.info("Step 3:");
			selectItem(getObject("shop_id"), shop2Lyon);
			getCommonObject("search_link").click();
			waitForTxtPresent("alert_error_text_css", periodMsg);
			alert_msg = periodMsg;				
			Assert.assertEquals(getObject("alert_text_xpath").getText(), alert_msg);
			getObject("alert_Ok_xpath").click();
			logger.info("Validated alert message, '"+alert_msg+"'");
		
			//Choosing date range within a month You must see reconciliation in main view
			logger.info("Step 4:");
			getObject("cpjourreset_xpath").click();selectItem(getObject("shop_id"), shop2Lyon);
			getCommonObject("search_link").click();
			waitForTxtPresent("bread_crumb_id", shop2Lyon);
			verifyBreadCrumb("Transaction Reconciliation");
			logger.info("Validated search when start date is set within 2 months");
			logger.info(" EPL-1782 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	
}
