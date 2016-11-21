package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1688.java $
$Id: EPL_1688.java 7858 2014-06-09 08:59:33Z cariram $
*/
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1688 extends SuiteCardPayment
{	
	private int count=0, tsExp=0, ts, tr, tcs;	
	private String[] payTransLabelsArr={csTotalTransRow, csTotalSalesRow, csTotalRefundsRow, csTotalCanSalesRow, csTotalFailedRow},
			indexArr = {"1", "2", "3", "4"};
	private List<WebElement> payTransLabels, payTransVals;
	
	

	/**
	 * EPL-1688 Check tables of Yesterday’s Main Information section / Check Payment Amounts Table
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1688()
	{		
		try
		{
			logger.info(" EPL-1688 execution started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//Select Spain country
			logger.info("Step 2:");
			selectDropItem("country_level_id", countrySpa);
			getObject("view_link").click();
			
			//getting all row/column values from Payment Transaction Amounts, table 
			logger.info("Step 3:");
			payTransLabels	= get_list_of_elements("pay_trans_col1_xpath"); 
			payTransVals	= get_list_of_elements("pay_trans_col2_xpath"); 
			logger.info("Following are the rows present in Payment Transactions Table :");			
			for(count=0; count<payTransLabels.size(); count++)
			{					
				Assert.assertEquals((payTransLabels.get(count).getText().trim()), payTransLabelsArr[count]);				
				logger.info(payTransLabels.get(count).getText().trim()+" : "+payTransVals.get(count).getText().trim());				
			}
			
			//Total Transactions = Total Sales +Total Refunds + Total Cancel. (Sales)			
			tsExp = getIntegerVal(payTransVals.get(0));
			ts = getIntegerVal(payTransVals.get(1));
			tr = getIntegerVal(payTransVals.get(2));
			tcs = getIntegerVal(payTransVals.get(3));
			Assert.assertEquals((ts+tr+tcs), tsExp );
			logger.info("Total Transactions = Total Sales +Total Refunds + Total Cancel. (Sales)");			
			
			//Verifying clicking on value links directs to Trans Overview page
			for(int i = 0; i<indexArr.length; i++)
			{
				verifyLinks(indexArr[i], indexArr[indexArr.length-1], countrySpa);
			}			
			logger.info("All currency values in Payment Transaction Amounts are clickable and direct to Transaction Overview page");
			logger.info(" EPL-1688 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
}
