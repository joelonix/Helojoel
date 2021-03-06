package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1693.java $
$Id: EPL_1693.java 7858 2014-06-09 08:59:33Z cariram $
*/
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1693 extends SuiteCardPayment
{
	private int count=0;
	private double tnvExp=0.0, tnvAct=0.0, sa=0.0, ra=0.0, ca=0.0, cra=0.0, ccb=0.0;
	private String[] payTransLabelsArr={csTransNetValRow, colSaleAmt, colSaleAmt, csSalesWCBAmtRow, csCashOutAmtRow, colRefundAmt, csCanSaleAmtRow, cancelRefundsAmnt, cancelCashBackAmnt},
			indexArr = {"1", "2", "3", "4", "5", "6"};
	private List<WebElement> payTransLabels, payTransVals;
	
	

	/**
	 * EPL-1693 Check tables of Yesterday�s Main Information section / Check Payment Amounts Table
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1693()
	{		
		try
		{
			logger.info(" EPL-1693 execution started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//Select UK country
			logger.info("Step 2:");
			selectDropItem("country_level_id", countryUK);
			getObject("view_link").click();
			
			//getting all row/column values from Payment Transaction Amounts, table 
			logger.info("Step 3:");
			payTransLabels	= get_list_of_elements("pay_trans_lable_xpath"); 
			payTransVals	= get_list_of_elements("pay_trans_val_xpath"); 
			logger.info("Following are the rows present in Transaction Amount Table :");
			for(count=0; count<payTransLabels.size(); count++)
			{				
				Assert.assertEquals((payTransLabels.get(count).getText().trim()), payTransLabelsArr[count]);
				Assert.assertEquals(((payTransVals.get(count).getText().substring(0,3)).trim()), currencyUK);
				logger.info(payTransLabels.get(count).getText().trim()+" : "+payTransVals.get(count).getText().trim());				
			}
			logger.info("Currency is verified as "+currencyUK);
			
			//Get all the currency values from the table and convert to double				
			tnvExp = getDirectDoubleValWithoutCurr(payTransVals.get(0));			
			tnvExp = round(tnvExp,2);
			
			sa = getDirectDoubleValWithoutCurr(payTransVals.get(1));
			ra = getDirectDoubleValWithoutCurr(payTransVals.get(5));
			ca = getDirectDoubleValWithoutCurr(payTransVals.get(6));
			cra = getDirectDoubleValWithoutCurr(payTransVals.get(7));
			ccb = getDirectDoubleValWithoutCurr(payTransVals.get(8));	
			
			//verifying Transaction Net Value = Sale Amount � Refunds amount � cancel. (Sales) Amount + Cancel.(Refunds) Amount - Cancel. (Cashback) Amount		
			tnvAct = Double.valueOf(round(sa-ra-ca+cra-ccb, 2)).doubleValue();
			Assert.assertEquals(tnvAct, tnvExp );
			logger.info(" Transaction Net Value ( "+tnvExp+") = Sale Amount � Refunds amount � cancellation amount ("+tnvAct+")");
			
			//Verifying clicking on value links directs to Trans Overview page
			for(int i = 0; i<indexArr.length; i++)
			{
				verifySanpAmtLinks(indexArr[i], indexArr[indexArr.length-1], countryUK);
			}			
			logger.info("All currency values in Payment Transaction Amounts are clickable and directs to Transaction Overview page");
			logger.info(" EPL-1693 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
}
