package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1690.java $
$Id: EPL_1690.java 8005 2014-06-13 09:21:06Z cariram $
*/
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1690 extends SuiteCardPayment
{	
	private int count=0;
	private double tnvExp, tnvAct, gsa, ra, csa, cra, sa, swa, coa, saAct;
	private String[] payTransLabelsArr={csTransNetValRow, colSaleAmt, csSalesWCBAmtRow,
			csCashOutAmtRow, colRefundAmt, csCanSaleAmtRow, cancelRefundsAmnt},
				payTransLabelsAltArr={csTransNetValRow, colSaleAmt, csSalesWCBAmtRow,
			csCashOutAmtRow, colRefundAmt, csCanSaleAmtRow, cancelRefundsAmnt};
	
	

	/**
	 * EPL-1690 Check tables of Yesterday’s Main Information section / Check Payment Amounts Table
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1690()
	{		
		try
		{
			logger.info(" EPL-1690 execution started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//getting all row/column values from Payment Transaction Amounts, table
			logger.info("Step 2, 3:");
			verifyAmtRowLabels(payTransLabelsArr);			
			
			//getting all the currency values from table convert to double
			verifyTransNetAmount();				
			
			//Verifying clicking on value links directs to Trans Overview page
			verifyAmountsLinks("tnv_val_xpath");
			verifyAmountsLinks("gsa_val_xpath");
			verifyAmountsLinks("sa_val_xpath");
			verifyAmountsLinks("swa_val_xpath");
			verifyAmountsLinks("coa_val_xpath");
			verifyAmountsLinks("ra_val_xpath");	
			waitForTxtPresent("bread_crumb_id", snapshotBC);
			if(driver.getPageSource().contains("Cancel. (Sales) Amount"))
			{
				getObject("csa_val_xpath").click();
			}
			else
			{
				getObject("csa_altval_xpath").click();
			}
			verifyBreadCrumb(cpTransOverBC);
			cardPaymentPageNavigator();	
			waitForTxtPresent("bread_crumb_id", snapshotBC);
			getObject("cra_val_xpath").click();
			verifyBreadCrumb(cpTransOverBC);
			logger.info("All currency values in Payment Transaction Amounts table are clickable and directs to Transaction Overview page");
			logger.info(" EPL-1690 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
	public void switchToSnapshot() throws InterruptedException
	{
		cardPaymentSubPageNavigator("Snapshot");		
		
	}
	
	/**
	 * verifies Amount rows labels in snapshot page
	 * @param rowArr
	 */
	private void verifyAmtRowLabels(String [] rowArr)
	{
		List<WebElement> payTransLabels	= get_list_of_elements("pay_trans_lable_xpath");
		List<String> payTransLabelsList	= getListItemsAsString("pay_trans_lable_xpath");	
		List<WebElement> payTransVals	= get_list_of_elements("pay_trans_val_xpath");	
		logger.info("Following are the rows present in Transaction Amounts Table :");					
		for(count=0; count<payTransLabels.size(); count++)
		{					
			Assert.assertEquals(((payTransVals.get(count).getText().substring(0,3)).trim()), currencyAus);
			logger.info(payTransLabels.get(count).getText().trim()+" : "+payTransVals.get(count).getText().trim());				
		}
		logger.info("Following are the rows present in Payment Transaction Amounts Table as expected :");	
		for(count=0; count<rowArr.length; count++)
		{		
			Assert.assertTrue(payTransLabelsList.contains(rowArr[count]) || payTransLabelsList.contains(payTransLabelsAltArr[count]));						
			logger.info(rowArr[count]);				
		} 
		logger.info("Currency is verified as "+currencyAus);
	}
	
	/**
	 * verifies Transaction Net Amount and Sales Amount
	 */
	private void verifyTransNetAmount()
	{
		tnvExp = getDoubleValWithoutCurr("tnv_val_xpath");
		gsa = getDoubleValWithoutCurr("gsa_val_xpath");
		ra = getDoubleValWithoutCurr("ra_val_xpath");		
		if(driver.getPageSource().contains(csCanSaleAmtRow))
		{			
			csa = getDoubleValWithoutCurr("csa_val_xpath");
		}
		else
		{			
			csa = getDoubleValWithoutCurr("csa_altval_xpath");
		}
		if(driver.getPageSource().contains(cancelRefundsAmnt))
		{				
			cra = getDoubleValWithoutCurr("cra_val_xpath");
		}
		else
		{			
			cra = getDoubleValWithoutCurr("cra_altval_xpath");
		}
		cra = getDoubleValWithoutCurr("cra_val_xpath");
		sa = getDoubleValWithoutCurr("sa_val_xpath");
		swa = getDoubleValWithoutCurr("swa_val_xpath");
		coa = getDoubleValWithoutCurr("coa_val_xpath");	
		
		//verifying Transaction Net Value = Sale Amount – Refunds amount – cancel. (Sales Amount) + Cancel. (Refunds) amount		
		tnvAct = Double.valueOf(round(gsa-ra-csa+cra,2)).doubleValue();
		Assert.assertEquals(tnvAct, tnvExp );
		logger.info(" Transaction Net Value ("+tnvExp+")= (Sale Amount – Refunds amount – cancel. (Sales Amount) + Cancel. (Refunds) amount) ("+tnvAct+")");
					
		//Verifying Sales Amount includes Sales Amount + Sale WCB Amount + Cash Out Amount		
		saAct = Double.valueOf(round(sa+swa+coa,2)).doubleValue();
		Assert.assertEquals(saAct, gsa );
		logger.info(" Sales Amount includes Sales Amount + Sale WCB Amount + Cash Out Amount");
	}
	
	/**
	 * Verifies clicking on amounts links directs to overview page 
	 * @param locator
	 * @throws InterruptedException
	 */
	private void verifyAmountsLinks(String locator) throws InterruptedException
	{
		getObject(locator).click();
		verifyBreadCrumb(cpTransOverBC);				
		cardPaymentPageNavigator();		
	}	
	
}
