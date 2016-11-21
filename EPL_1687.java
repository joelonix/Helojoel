package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1687.java $
$Id: EPL_1687.java 7858 2014-06-09 08:59:33Z cariram $
*/
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1687 extends SuiteCardPayment
{	
	private int tsExp=0, ts=0, tr=0, tcs=0, tcr=0;	
	private String[] payTransLabelsArr={csTotalTransRow, csTotalSalesRow, csTotalRefundsRow, csTotalCanSalesRow, csTotalFailedRow},
			indexArr = {"1", "2", "3", "4", "5"};
	private List<WebElement>  payTransVals, payTransLabels;
	private List<String> payTransLabelsList;
	
	
	

	/**
	 * EPL-1687 Check tables of Yesterday’s Main Information section / Check Payment Amounts Table
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1687()
	{		
		try
		{
			logger.info(" EPL-1687 execution started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//Select France country
			logger.info("Step 2:");
			selectDropItem("country_level_id", countryFran);
			getObject("view_link").click();	
			
			//get labels and values from the table
			logger.info("Step 3:");
			payTransLabels	= get_list_of_elements("pay_trans_col1_xpath"); 
			payTransLabelsList	= getListItemsAsString("pay_trans_col1_xpath");			
			payTransVals	= get_list_of_elements("pay_trans_col2_xpath");	
			
			verifyRowsLabels(payTransLabelsArr);			
			
			//Total Transactions = Total Sales +Total Refunds + Total Cancel. (Sales)
			verifyTotalTrans();			
			
			//Verifying clicking on value links directs to Trans Overview page
			for(int i = 0; i<indexArr.length; i++)
			{
				verifyLinks(indexArr[i], indexArr[indexArr.length-1], countryFran);
			}			
			logger.info("All currency values in Payment Transaction Amounts table are clickable and direct to Transaction Overview page");
			logger.info(" EPL-1687 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
	
	/**
	 * verifies total transactions
	 */
	private void verifyTotalTrans()
	{		
		tsExp = getIntegerVal(payTransVals.get(0));
		if(isElementPresent("total_sales_xpath"))
		{			
			ts = getIntegerVal(payTransVals.get(1));
		}
		
		tr = getTransVal("total_refunds_xpath", "total_refundsval_xpath");
		tcs = getTransVal("total_can_sales_xpath", "total_can_salesval_xpath");
		tcr = getTransVal("total_can_refunds_xpath", "total_can_refundsval_xpath");
		
		Assert.assertEquals((ts+tr+tcs+tcr), tsExp );
		logger.info("Total Transactions ("+tsExp+")= (Total Sales +Total Refunds + Total Cancel. (Sales) + Total Cancel. (Refunds)) ("+(ts+tr+tcs+tcr)+")");			
	}	
}
