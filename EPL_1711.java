package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1711.java $
$Id: EPL_1711.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1711 extends SuiteCardPayment
{
	private boolean flagMsg;
	private String midExpNum, midActNum, msg;	
	
	/*
	 * Enter mid filter and verify search
	 */
	public void verifySearch()
	{
		try{
			getObject("mid_id").sendKeys(midExpNum);
			getObject("search_link").click();
			if(isElementPresent("trans_no_mid_xpath"))
			{
				logger.info("There is no MID data to display to execute test");
				Assert.fail("There is no MID data to display to execute test");
			}
			midActNum=getObject("mid_val_xpath").getText();
			Assert.assertEquals(midActNum, midExpNum);
			getObject("cardpayment_link").click();
			getObject("transaction_overview_link").click();
		}
		catch(Exception e)
		{
			logger.error("Search not verified with correct value");
			Assert.fail("Search not verified with correct value");
		}
		
	}
	
	/*
	 * Verify mid filter at all levels
	 */
	public void verifyTranOver() throws InterruptedException
	{	
		try{
			verifyMIDPOSBlank("mid_id");
			getObject("settlement_id").click();			
			verifyMIDPOSBlank("mid_id");
			getObject("all_level_id").click();			
			verifyMIDPOSBlank("mid_id");
			getObject("all_level_id").click();			
			verifyElementDisp("mid_id");			
			if(getObject("mid_val_xpath").isDisplayed())
			{
				midExpNum = getObject("mid_val_xpath").getText();
			}
			else
			{
				logger.info("There are no MID data displayed");
			}
			getObject("card_type_xpath").click();			
			verifyElementDisp("mid_id");		
			getObject("pos_xpath").click();			
			verifyElementNotPresent(midFullLabel);				
			getObject("cashier_xpath").click();			
			verifyElementNotPresent("MID (full):");
		}
		catch(Exception e)
		{
			logger.error("Search not verified with correct value in Trans - Overview page");
			Assert.fail("Search not verified with correct value in Trans - Overview page");
		}
		
	}
	
	/*
	 * Verify search with correct value of mid filter at all levels
	 */
	public void verifyValidSearchwithMID() throws InterruptedException
	{	
		try{
			verifySearch();
			getObject("settlement_id").click();			
			verifySearch();
			getObject("settlement_id").click();	
			getObject("all_level_id").click();			
			verifySearch();
			getObject("settlement_id").click();	
			getObject("all_level_id").click();
			getObject("all_level_id").click();			
			selectItem(getObject("mid_id"),midExpNum);
			getObject("search_link").click();
			midActNum=getObject("mid_val_xpath").getText();
			Assert.assertEquals(midActNum, midExpNum);
			getObject("card_type_xpath").click();			
			selectItem(getObject("mid_id"),midExpNum);
			getObject("search_link").click();
			midActNum=getObject("mid_val_xpath").getText();
			Assert.assertEquals(midActNum, midExpNum);
		}
		catch(Exception e)
		{
			logger.error("Search not verified with correct value");
			Assert.fail("Search not verified with correct value");
		}
				
	}	
	
	/*
	 * Verify search with incorrect value of mid filter at all levels
	 */
	public void verifyInvalidSearchwithMID() throws InterruptedException
	{	
		try{
			verifyInvalidSearch("mid_id", incorrectMidPosVal, validMidMsg);
			getObject("settlement_id").click();			
			verifyInvalidSearch("mid_id", incorrectMidPosVal, validMidMsg);
			getObject("settlement_id").click();	
			getObject("all_level_id").click();			
			verifyInvalidSearch("mid_id", incorrectMidPosVal, validMidMsg);		
		}
		catch(Exception e)
		{
			logger.error("Search not verified with incorrect value");
			Assert.fail("Search not verified with incorrect value");
		}
	}
	
	

	/**
	 * EPL-1711 Transaction Overview sub-module / Check filters / MID filter
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1711()
	{		
		try{
			logger.info("  EPL-1711 started execution");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);	
				
			logger.info("Step 2, 3, 5:");
			verifyTranOver();			
			logger.info("In the left hand side column, MID filter is present and by default, MID filter is empty");
			cardPaymentSubPageNavigator(subModTrnOvr);	
			
			//Verify search with invalid value of mid
			logger.info("Step 6:");
			verifyInvalidSearchwithMID();
			logger.info("The clear and friendly error message is displayed as expected : '"+flagMsg+"' "+msg);
			
			//Verify search with valid value of mid
			logger.info("Step 4:");
			verifyValidSearchwithMID();
			logger.info("The search is successful and the result is as expected");
							 
			logger.info(" EPL-1711 execution successful");
			} 
		catch (Throwable t)
		{
			handleException(t);	
		}	
	}
	
}
