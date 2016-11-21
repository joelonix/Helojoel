package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1689.java $
$Id: EPL_1689.java 7858 2014-06-09 08:59:33Z cariram $
*/
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1689 extends SuiteCardPayment
{	
	private int tsExp=0, ts=0, tco=0, ttExp=0, tr=0, tcs=0, tcr=0, tcc=0, tsw=0;	
	private String[] payTransLabelsArr={csTotalTransRow, csTotalSalesRow, csTotalSalesRow, csTotalSalesWCBRow, csTotalCashRow, csTotalRefundsRow, csTotalCanSalesRow, totCanRefunds, totCancelCashBack, csTotalFailedRow},
			indexArr = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
	private List<WebElement> payTransLabels, payTransVals;
	private List<String> payTransLabelsList;	
	
	

	/**
	 * EPL-1689 Check tables of Yesterday’s Main Information section / Check Payment Amounts Table
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1689()
	{		
		try
		{
			logger.info(" EPL-1689 execution started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//Select UK country
			logger.info("Step 2:");
			selectDropItem("country_level_id", countryUK);
			getObject("view_link").click();
			
			//getting all row/column values from Payment Transaction Amounts, table 
			logger.info("Step 3:");
			payTransLabels	= get_list_of_elements("pay_trans_col1_xpath");
			payTransLabelsList	= getListItemsAsString("pay_trans_col1_xpath");
			payTransVals	= get_list_of_elements("pay_trans_col2_xpath"); 
			
			verifyRowsLabels(payTransLabelsArr);			
			
			//Total Sales = Total Sales + Total Cash out
			verifyTotalTrans();			
			
			//Total Transactions = Total Sales +Total Refunds + Total Cancel. (Sales) + Total Cancel. (Refunds)+ Total Cancel. (Cashback)
			verifyTotalSales();	
			
			//Verifying clicking on value links directs to Trans Overview page
			for(int i = 0; i<indexArr.length; i++)
			{
				verifyLinks(indexArr[i], indexArr[indexArr.length-1], countryUK);
			}			
			logger.info("All currency values in Payment Transaction Amounts table are clickable and direct to Transaction Overview page");
			logger.info(" EPL-1689 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
	
	public void switchToSnapshot() throws InterruptedException
	{
		try{	
			cardPaymentSubPageNavigator("Snapshot");
			selectDropItem("country_level_id", countryUK);
			getObject("view_link").click();
			}
		catch(Throwable e)
		{
			logger.error("Could not switch to Snapshot page");
			Assert.fail("Could not switch to Snapshot page");
		}
	}
	
	/**
	 * verifies total transactions
	 */
	private void verifyTotalTrans()
	{		
		tsExp = getIntegerVal(payTransVals.get(1));
		if(isElementPresent("total_sales_xpath"))
		{				
			ts = getIntegerVal(payTransVals.get(2));
		}		
		tsw = getTransVal("total_wcb_xpath", "total_wcbval_xpath");
		tco = getTransVal("total_cashout_xpath", "total_cashoutval_xpath");
		
		Assert.assertEquals((ts+tsw+tco), tsExp );
		logger.info("Total Sales ("+tsExp+") = (Total Sales + Total Sales WCB + Total Cash out)("+(ts+tsw+tco)+")");		
	}
	
	/**
	 * verifies total Sales
	 */
	private void verifyTotalSales()
	{		
		ttExp = getIntegerVal(payTransVals.get(0));
		if(isElementPresent("total_sales_xpath"))
		{			
			ts = getIntegerVal(payTransVals.get(1));
		}
		tr = getTransVal("total_refunds_xpath", "total_refundsval_xpath");
		tcs = getTransVal("total_can_sales_xpath", "total_can_salesval_xpath");
		tcr = getTransVal("total_can_refunds_xpath", "total_can_refundsval_xpath");
		tcc = getTransVal("total_can_cash_xpath", "total_can_cashval_xpath");		
		Assert.assertEquals((ts+tr+tcs+tcr+tcc), ttExp );
		logger.info("Total Transactions ("+ttExp+")= (Total Sales +Total Refunds + Total Cancel. (Sales) + Total Cancel. (Refunds)+ Total Cancel. (Cashback)) ("+(ts+tr+tcs+tcr+tcc)+")");
	}
}
