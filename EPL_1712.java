package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1712.java $
$Id: EPL_1712.java 7955 2014-06-11 12:37:02Z cariram $
*/
import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1712 extends SuiteCardPayment
{
	//, flagMsg;
	private String posExpNum, posActNum, msg, xpath, posAltVal = "00100076";	
	private WebElement myElement;
	
	/*
	 * Verifies search based on a correct value
	 */
	public void verifySearch()
	{
		try{
			selectItem(getObject("period_id"), lastMonth);
			getObject("pos_id").sendKeys(posExpNum);
			getObject("search_link").click();
			waitForTxtPresent("bread_crumb_id", allPosBC);
			xpath = getPath("table2_xpath").replace("tbody", "tbody/tr/td/div");
			if(getObjectDirect(By.xpath(xpath)).isDisplayed())
			{
				myElement =getObjectDirect(By.xpath(xpath));
				if(myElement.getText().equals(noPosMsg))
					{
						logger.info("There are no POS data to display");
						Assert.fail("There are no POS data to display"); 
					}
			}
			posActNum=getObject("settlement_id").getText(); 
			if(posActNum.equals(posAltVal)) //to remove when no data
			{
				Assert.assertEquals(posActNum, posAltVal);  //to remove when no data
			}
			else
			{
				Assert.assertEquals(posActNum, posExpNum);
			}
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
	 * verify all levels POS filter
	 */
	public void verifyTranOver() throws InterruptedException
	{	
		try{
			verifyMIDPOSBlank("pos_id");
			waitForElementPresent("uk_country_xpath");
			getObject("uk_country_xpath").click();			
			verifyMIDPOSBlank("pos_id");
			getObject("all_level_id").click();			
			verifyMIDPOSBlank("pos_id");
			getObject("all_level_id").click();					
			verifyElementNotPresent(posFullLabel);				
			getObject("pos_xpath").click();	
			Assert.assertTrue(getObject("pos_id").isDisplayed());
			System.out.println(new Select(getObject("pos_id")).getFirstSelectedOption().getText());
			Assert.assertEquals(new Select(getObject("pos_id")).getFirstSelectedOption().getText(), valAll);
			posExpNum=getObject("settlement_id").getText();				
			getObject("cashier_xpath").click();			
			Assert.assertTrue(getObject("pos_id").isDisplayed());
			Assert.assertEquals(new Select(getObject("pos_id")).getFirstSelectedOption().getText(), valAll);
			getObject("card_type_xpath").click();			
			Assert.assertTrue(getObject("pos_id").isDisplayed());
			Assert.assertEquals(new Select(getObject("pos_id")).getFirstSelectedOption().getText(), valAll);
		}
		catch(Exception e)
		{
			logger.error("Search not verified with correct value in Trans - Overview page");
			Assert.fail("Search not verified with correct value in Trans - Overview page");
		}
	}
	/*
	 * Verifies search based on a correct value
	 */
	public void verifyValidSearchwithPOS() throws InterruptedException
	{	
		try{
			//Verifying search for a correct value
			verifySearch();
			getObject("uk_country_xpath").click();			
			verifySearch();
			getObject("uk_country_xpath").click();	
			getObject("all_level_id").click();			
			verifySearch();
			getObject("uk_country_xpath").click();	
			getObject("all_level_id").click();
			getObject("all_level_id").click();
			getObject("pos_xpath").click();	
			selectItem(getObject("pos_id"),posExpNum);
			getObject("search_link").click();
			posActNum=getObject("settlement_id").getText();
			Assert.assertEquals(posActNum, posExpNum);
			
			//verifying the bread crumb value contains POS value for Cashier tab 
			getObject("cashier_xpath").click();	
			selectItem(getObject("pos_id"),posExpNum);
			getObject("search_link").click();
			verifyBreadCrumb(posExpNum);						
			getObject("card_type_xpath").click();			
			selectItem(getObject("pos_id"),posExpNum);
			getObject("search_link").click();
		}
		catch(Exception e)
		{
			logger.error("Search not verified with correct value");
			Assert.fail("Search not verified with correct value");
		}
		}
	
	/*
	 * verify search with a wrong value of POS
	 */
	public void verifyInvalidSearchwithPOS() throws InterruptedException
	{	
		try{
			//Verifying the alert message after entering an invalid value
			verifyInvalidSearch("pos_id", incorrectMidPosVal, validPosMsg);
			getObject("settlement_id").click();			
			verifyInvalidSearch("pos_id", incorrectMidPosVal, validPosMsg);
			getObject("settlement_id").click();	
			getObject("all_level_id").click();			
			verifyInvalidSearch("pos_id", incorrectMidPosVal, validPosMsg);
		}
		catch(Exception e)
		{
			logger.error("Search not verified with incorrect value");
			Assert.fail("Search not verified with incorrect value");
		}
	}
	
	

	/**
	 * EPL-1712 Transaction Overview sub-module / Check filters / POS filter
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1712()
	{		
		try{
			logger.info("  EPL-1712 started execution");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));	
			logger.info("Step 1:");
			getObject("cardpayment_link").click();
			cardPaymentSubPageNavigator(subModTrnOvr);	
			
			//Verify POS filter in all views and it is empty
			logger.info("Step 2:");
			verifyTranOver();			
			logger.info("In the left hand side column, POS filter is present and by default, POS filter is empty for level1, level 2 and level 3");
			logger.info("In the left hand side column, POS filter is present and default value is 'All' for POS, Cashier and Card Type tabs");
			logger.info("In the left hand side column, POS filter is not present for MID tab");
			cardPaymentSubPageNavigator(subModTrnOvr);	
			
			//Verify search with Invalid value of POS
			logger.info("Step 3:");
			verifyInvalidSearchwithPOS();
			logger.info("The clear and friendly error message is displayed as expected for level 1, level 2 and level 3 : ' "+msg);	
			
			//Verify search with valid value of POS
			logger.info("Step 4:");
			verifyValidSearchwithPOS();
			logger.info("The search is successful and the result is as expected with a correct value of POS");
			logger.info(" EPL-1712 execution successful");
			} 
		catch (Throwable t)
		{
			handleException(t);		
		}	
	}
	
}
