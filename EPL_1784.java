package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1784.java $
$Id: EPL_1784.java 7916 2014-06-10 12:30:12Z cariram $
 */
import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
public class EPL_1784 extends SuiteCardPayment
{
	/**
	 * EPL-1784 Transaction Reconciliation / EMV AUS Application / Westpac – Check reconciliation filter
	 * 
	 * @throws IOException
	 */
	
	@Test()
	public void epl_1784()
	{		
		try
		{
			logger.info(" EPL-1784 executing started");			
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));	
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnRecon);
			verifyBreadCrumb(cpReconBC);
			logger.info("Transaction Reconciliation submodule is visible");
			
			//This filter must contain 3 checks boxes:By default, the store box and bank store must be checked
			logger.info("Step 2:");
			verifyElementDisp("reconciliation_store_id");
			verifyElementDisp("reconciliation_bank_id");
			verifyElementDisp("show_exception_report_id");
			
			verifyElementSelected("reconciliation_store_id");
			verifyElementSelected("reconciliation_bank_id");
		    verifyElementNotSelected("show_exception_report_id");
			logger.info("Verfied the checkboxes presence and their states");
			
			//Deselected all type of reconciliation and click search button.Automatically, the store check box and bank check box are selected and Search
			//is executed successfully
			selectItem(getObject("shop_id"), shop2Sydney);
			
			//Uncheck bank and store
			logger.info("Step 3:");
			getObject("reconciliation_store_id").click();
			getObject("reconciliation_bank_id").click();
			verifyElementNotSelected("reconciliation_store_id");
			verifyElementNotSelected("reconciliation_bank_id");
			logger.info("Checkboxes Store and Bank are unchecked");
			getObject("search_link").click();
			logger.info("Clicked search");
			
			//Bank and Store checkboxes are checked
			logger.info("Step 4:");
			waitForCommonElementPresent("breadcrumb_id");
			verifyElementSelected("reconciliation_store_id");
			verifyElementSelected("reconciliation_bank_id");
			logger.info("After click of search checkboxes are checked again");
			
			//Search is executed successfully with both store and bank tables displayed
			verifyTableDisplayed("table_store_reconciliation_xpath");
			verifyTableDisplayed("table_bank_reconciliation_xpath");
			logger.info("Search is successful");
			
			//Selected all type of reconciliation and click search button and verify Search is executed correctly
			
			//Click Reset Filters
			getObject("reset_link").click();
			selectItem(getObject("shop_id"), shop2Sydney);
			getObject("show_exception_report_id").click();
			getObject("search_link").click();
			logger.info("Clicked search");
			waitForCommonElementPresent("breadcrumb_id");
			verifyTableDisplayed("table_store_reconciliation_xpath");
			verifyTableDisplayed("table_bank_reconciliation_xpath");
			verifyTableDisplayed("table_reconciliation_exception_xpath");
			logger.info("Search is successful");
			logger.info(" EPL-1784 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	/**
	 * verifyTableDisplayed verifies table presence
	 *@param xpath
	 */
	
	public void verifyTableDisplayed(String xpath){
		String yestDate = getYestDate();
	    String xpathNew=getPath(xpath).replace("YESTDATE", yestDate);		
		WebElement table =getObjectDirect(By.xpath(xpathNew));
		Assert.assertEquals(table.isDisplayed(), true);
	}
}
