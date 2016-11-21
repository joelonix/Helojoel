package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1686.java $
$Id: EPL_1686.java 7858 2014-06-09 08:59:33Z cariram $
*/
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1686 extends SuiteCardPayment
{	
	private int tsExp=0, ts, tsw, tco, ttExp, tr, tcs, tcr;	
	private String[] payTransLabelsArr={csTotalTransRow, csTotalSalesRow, csTotalSalesRow, csTotalSalesWCBRow, csTotalCashRow, csTotalRefundsRow, csTotalCanSalesRow, totCanRefunds, csTotalFailedRow},
			indexArr = {"1", "2", "3", "4", "5", "6", "7", "8"};
	
	private List<WebElement>  payTransVals, payTransLabels;
	private List<String> payTransLabelsList;
	
	

	/**
	 * EPL-1686 Check tables of Yesterday’s Main Information section / Check Payment Transactions Table
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1686()
	{		
		try
		{
			logger.info(" EPL-1686 execution started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//getting all row/column values from Payment Transaction Amounts, table 
			logger.info("Step 2, 3:");
			payTransLabels	= get_list_of_elements("pay_trans_col1_xpath"); 
			payTransLabelsList	= getListItemsAsString("pay_trans_col1_xpath");
			payTransVals	= get_list_of_elements("pay_trans_col2_xpath"); 
			
			verifyRowsLabels(payTransLabelsArr);			
			
			//Total Sales = Total Sales + Total Sales WCB + Total Cash out
			verifyTotalSales();		
			
			//Total Transactions = Total Sales +Total Refunds + Total Cancel. (Sales) + Total Cancel. (Refunds) 
			verifyTotalTrans();			
			
			//Verifying clicking on value links directs to Trans Overview page
			for(int i = 0; i<indexArr.length; i++)
			{
				verifyLinks(indexArr[i], indexArr[indexArr.length-1]);
			}	
			cardPaymentSubPageNavigator(snapshotBC);
			logger.info("All currency values in Payment Transaction Amounts table are clickable and direct to Transaction Overview page");
			logger.info(" EPL-1686 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
	
	/**
	 * Verifies total sales
	 */
	private void verifyTotalSales()
	{		
		tsExp = getIntegerVal(payTransVals.get(1));
		ts = getIntegerVal(payTransVals.get(2));
		tsw = getIntegerVal(payTransVals.get(3));
		tco = getIntegerVal(payTransVals.get(4));
		Assert.assertEquals((ts+tsw+tco), tsExp );
		logger.info("Total Sales = Total Sales + Total Sales WCB + Total Cash out");
	}
	
	
	/**
	 * verifies total transactions
	 */
	private void verifyTotalTrans()
	{		
		ttExp = getIntegerVal(payTransVals.get(0));
		ts = getIntegerVal(payTransVals.get(1));
		tr = getIntegerVal(payTransVals.get(5));
		tcs = getIntegerVal(payTransVals.get(6));
		tcr = getIntegerVal(payTransVals.get(7));
		Assert.assertEquals((ts+tr+tcs+tcr), ttExp );
		logger.info("Total Transactions = Total Sales +Total Refunds + Total Cancel. (Sales) + Total Cancel. (Refunds)");
	}	
}
