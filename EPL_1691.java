package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1691.java $
$Id: EPL_1691.java 7858 2014-06-09 08:59:33Z cariram $
*/
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1691 extends SuiteCardPayment
{
	private int count=0;
	private double tnvExp=0.0, tnvAct=0.0, sa=0.0, ra=0.0, csa=0.0, cra=0.0;
	private String[] payTransLabelsArr={csTransNetValRow, colSaleAmt, colRefundAmt, csCanSaleAmtRow};
	private List<WebElement> payTransVals;
	
	

	/**
	 * EPL-1691 Check tables of Yesterday’s Main Information section / Check Payment Amounts Table
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1691()
	{		
		try
		{
			logger.info(" EPL-1691 execution started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//Select France country
			logger.info("Step 2:");
			selectDropItem("country_level_id", countryFran);
			getObject("view_link").click();
			
			//getting all row/column values from Payment Transaction Amounts, table	
			logger.info("Step 3:");
			payTransVals	= get_list_of_elements("pay_trans_val_xpath"); 
			verifyAmtRowLabels(payTransLabelsArr);				
			
			//Get all the currency values from table and convert to double
			verifyTransNetAmount();				
			
			//Verifying clicking on value links directs to Trans Overview page
			verifyAmountsLinks("1");
			verifyAmountsLinks("2");
			switchToSnapshot("ramnt_xpath");				
			switchToSnapshot("can_salamnt_xpath");
			getObject("can_reamnt_xpath").click();
			verifyBreadCrumb(cpTransOverBC);			
			logger.info("All currency values in Payment Transaction Amounts table are clickable and directs to Transaction Overview page");
			logger.info(" EPL-1691 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
	
	
	/**
	 * Verifies label rows presence in table and currency
	 * @param rowArr
	 */
	private void verifyAmtRowLabels(String [] rowArr)
	{
		List<WebElement> payTransLabels	= get_list_of_elements("pay_trans_lable_xpath"); 
		List<String> payTransLabelsList	= getListItemsAsString("pay_trans_lable_xpath");
		List<WebElement> payTransVals	= get_list_of_elements("pay_trans_val_xpath"); 
		
		logger.info("Following are the rows present in Transaction Amount Table :");
		for(count=0; count<payTransLabels.size(); count++)
		{				
			Assert.assertEquals(((payTransVals.get(count).getText().substring(0,3)).trim()), currencyFran);
			logger.info(payTransLabels.get(count).getText().trim()+" : "+payTransVals.get(count).getText().trim());				
		}
		logger.info("Following rows are displayed in Payment Transactions Amounts Table as expected");	
		for(count=0; count<rowArr.length; count++)
		{		
			Assert.assertTrue(payTransLabelsList.contains(rowArr[count]));						
			logger.info(rowArr[count]);				
		} 
		logger.info("Currency is verified as "+currencyFran);
	}
	
	/**
	 * Verifies transactions net value
	 */
	private void verifyTransNetAmount()
	{
		tnvExp = getDirectDoubleValWithoutCurr(payTransVals.get(0));		
		tnvExp = round(tnvExp,2);
		
		sa = getDirectDoubleValWithoutCurr(payTransVals.get(1));		
		ra = getDoubleValWithoutCurr("ramnt_xpath");
		csa = getDoubleValWithoutCurr("can_salamnt_xpath");
		cra = getDoubleValWithoutCurr("can_reamnt_xpath");		
		
		//verifying Transaction Net Value = Sale Amount – Refunds amount – cancellation amount		
		tnvAct = Double.valueOf(round(sa-ra-csa+cra,2)).doubleValue();
		Assert.assertEquals(tnvAct, tnvExp );
		logger.info(" Transaction Net Value ( "+tnvExp+") = Sale Amount – Refunds amount  - cancel (Sales) amount + Cancel (Refunds) Amount ("+tnvAct+")");
	}
	
	/**
	 * Verifies clicking on amounts links directs to overview page 
	 * @param locator
	 * @throws InterruptedException
	 */
	private void verifyAmountsLinks(String index) throws InterruptedException
	{
		xpath=getPath("pay_ind_val_xpath").replace("INDEX",index);
		getObjectDirect(By.xpath(xpath)).click();					
		verifyBreadCrumb(cpTransOverBC);
		cardPaymentSubPageNavigator(snapshotBC);
		selectDropItem("country_level_id", countryFran);		
		getObject("view_link").click();		
	}	
	
	/**
	 * verifies bread crumb and switching to snapshot
	 * @param locator
	 * @throws InterruptedException
	 */
	private void switchToSnapshot(String locator) throws InterruptedException
	{
		try{
			getObject(locator).click();
			verifyBreadCrumb(cpTransOverBC);
			cardPaymentSubPageNavigator(snapshotBC);
			selectDropItem("country_level_id", countryFran);			
			getObject("view_link").click();				
		}
		catch(Exception e)
		{
			logger.error("Could not switch to Snapshot page");
			Assert.fail("Could not switch to Snapshot page");
		}
	}
}
