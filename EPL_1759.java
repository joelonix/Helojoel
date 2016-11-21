package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1759.java $
$Id: EPL_1759.java 8030 2014-06-16 06:30:03Z cariram $
*/

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1759 extends SuiteCardPayment{
	private boolean areRowsPresent=false;
	private List<WebElement> rows;
	public static  Locale locale = Locale.ENGLISH;
	
	/**
	 * EPL-1759 All applications / Check Filters / Check Settlement filters work correctly / Settlement Date filter
	 * 
	 * @throws IOException
	 */
	
	@Test()
	public void epl_1759()
	{		
		try
		{
			logger.info(" EPL-1759 executing started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			SetTimeZone();
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			
			getObject("advanced_search_link").click();			
			

			//Setting settlement date	
			logger.info("Step 2:");
			String currDate  = currentDateFormatter();
			logger.info("Current date is : "+currDate);
			String[] dateArr = currDate.split("-");	
			
			Calendar cal = Calendar.getInstance(locale);
			String month1=getMonthForInt(cal.get(Calendar.MONTH)+1);
			
			logger.info("Current Month is : "+month1);
			waitForElementPresent("setdate_image_xpath");
			setSettlementDate(Integer.parseInt(dateArr[0]), month1, Integer.parseInt(dateArr[2]));
			waitNSec(2);

			getObject("search_link").click();
			waitForTxtPresent("journal_row_count_xpath", displayText);
			logger.info("Setting settlement date and clicked search");
			
			//Verify In the main view, there are some transactions.
			rows = getObject("table_card_payment_journal_xpath").findElements(By.tagName("tr"));
			if(rows.size()>0) areRowsPresent = true;
			Assert.assertEquals(areRowsPresent, true);
			logger.info("Search returned rows");
			
			logger.info(" EPL-1759 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	
	
	/**
	 * Setting settlement date
	 * @param year
	 * @param month
	 * @param date
	 */
	private void setSettlementDate(int year,String month,int date)
	{
		try{
			
			getObject("setdate_image_xpath").click();
			waitForCommonElementPresent("Year_xpath");
			getCommonObject("Year_xpath").click();
			Select dropdownYear=new Select(getCommonObject("Year_xpath"));
			dropdownYear.selectByVisibleText(Integer.toString(year));
			logger.info("Year selected in calender : "+ year);			
			getCommonObject("Month_xpath").click();
			waitForCommonElementPresent("Month_xpath");
			Select dropdownMonth=new Select(getCommonObject("Month_xpath"));
			dropdownMonth.selectByVisibleText(month);
			logger.info("Month selected in calender : "+ month);
			path=getCommonPath("Date_link").replace("Date", Integer.toString(date));
			getObjectDirect(By.linkText(path)).click();	
			logger.info("Date selected in calender : "+ date);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
			logger.error("Exception in setSettlementDate .. "+e.getMessage()+ "   Stacke trace  "  + e.getStackTrace().toString());
			Assert.fail("Error with reading date localied message .. "+e.getLocalizedMessage()  + " message "+e.getMessage());
		}
	}

}
