package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1709.java $
$Id: EPL_1709.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1709 extends SuiteCardPayment
{
	
	private String startDate, endDate, startHour, endHour; 
	private int count=0;
	private String[] periodArr={periodDefault, periodToday, periodYest, periodThisWeek, periodLastWk, period20Days, period30Days, thisMonth, lastMonth, periodLast6M};
	private List<WebElement> periodList;
	
	

	/**
	 * EPL-1709 Transaction Overview sub-module / Check filters / Date filter
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1709()
	{		
		try{
			logger.info("  EPL-1709 started execution");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));	
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);	
			
			//Verify default values for Receipt Time and Period
			logger.info("Step 2:");
			Assert.assertEquals(getObject("receipt_id").getAttribute("checked"), "true");
			Assert.assertEquals(new Select(getObject("period_id")).getFirstSelectedOption().getText(), periodToday);
			logger.info(" By default, Receipt Time button is selected and by default, Today as pre-defined period is selected");
			
			//Verify Receipt Time Search
			logger.info("Step 3:");
			startDate=getObject("strt_date_id").getText();
			endDate=getObject("end_date_id").getText();
			startHour=new Select(getObject("start_hour_id")).getFirstSelectedOption().getText();
			endHour=new Select(getObject("end_hour_id")).getFirstSelectedOption().getText();
			
			verifyReceiptSearch(startDate, endDate, startHour, endHour);			
			logger.info("Breadcrumb is correctly displayed after selecting Receipt Time.");
			
			//Verify Server Time Search
			logger.info("Step 4:");
			getObject("server_id").click();
			verifyReceiptSearch(startDate, endDate, startHour, endHour);				
			logger.info("Breadcrumb is correctly displayed after selecting Server Time.");
			
			//Verify Period options
			logger.info("Step 5:");
			periodList=new Select(getObject("period_id")).getOptions();
			for(count=0;count<periodList.size();count++)
			{				
				Assert.assertEquals(periodList.get(count).getText(), periodArr[count]);
			}
			logger.info("Period options are displayed correctly as expected");
			
			//Verify search after selecting "---"
			logger.info("Step 6:");
			selectItem(getObject("period_id"), periodDefault);
			verifyReceiptSearch(startDate, endDate, startHour, endHour);			
			logger.info("Breadcrumb is correctly displayed after selecting '---' Period");
			
			//Verify bread crumb after selecting all period values
			logger.info("Step 7:");
			for(count=0;count<periodList.size();count++)
			{	
				new Select(getObject("period_id")).selectByVisibleText(periodArr[count]);
				startDate=getObject("strt_date_id").getText();
				endDate=getObject("end_date_id").getText();
				startHour=new Select(getObject("start_hour_id")).getFirstSelectedOption().getText();
				endHour=new Select(getObject("end_hour_id")).getFirstSelectedOption().getText();
				verifyReceiptSearch(startDate, endDate, startHour, endHour);				
				logger.info("Breadcrumb is correctly displayed after selecting '"+periodArr[count]+"' Period");
			}
			logger.info(" EPL-1709 execution successful");
			} 
		catch (Throwable t)
		{
			handleException(t);	
		}	
	}
	
	
	/**
	 * verifies search based on period and bread crumbs for start date, Time and End date, time
	 * @param sDate
	 * @param eDate
	 * @param sHour
	 * @param eHour
	 */
	private void verifyReceiptSearch(String sDate, String eDate, String sHour, String eHour)
	{
		getObject("search_link").click();
		verifyBreadCrumb(startDate);
		verifyBreadCrumb(endDate);
		verifyBreadCrumb(startHour);
		verifyBreadCrumb(endHour);
	}
}
