package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1679.java $
$Id: EPL_1679.java 7858 2014-06-09 08:59:33Z cariram $
*/
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class EPL_1679 extends SuiteCardPayment
{
	
	private String xpath;
	private WebElement elt;
	
	

	/**
	 * EPL-1679 Check right hand side column of snapshot sub-module
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1679()
	{		
		try
		{
			logger.info(" EPL-1679 execution started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//Verifying swapping between receipt time and server time
			logger.info("Step 2:");
			verifyElementDisp("receipt_id");
			verifyElementDisp("server_id");			
			getObject("server_id").click();
			verifyElementSelected("server_id");			
			getObject("receipt_id").click();
			verifyElementSelected("receipt_id");			
			logger.info("Choose Displayed Time menu is displayed and swaping between the receipt time and the server time works correctly");
			
			//Verifying all the links direct to corresponding page correctly			
			verifyNavigationLinks();
			logger.info("All links work correctly and directing to the correct page");			
			logger.info(" EPL-1679 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	/**
	 * Verifies all the links direct to corresponding page correctly
	 * @throws InterruptedException
	 */
	private void verifyNavigationLinks() throws InterruptedException
	{
		List<WebElement> seeList=getObjects("see_alsodynlist_xpath");		
		
		//Verifying all the links direct to corresponding page correctly
		for(int i =0; i<seeList.size();i++)
		{
			xpath=getPath("see_alsolist_xpath").replace("INDEX", Integer.toString(i+1));		
			elt = getObjectDirect(By.xpath(xpath));
			elt.click();			
			getObject("cardpayment_link").click();			
			//cardPaymentSubPageNavigator(snapshotBC);
		}		
	}
}
