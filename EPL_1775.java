package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1775.java $
$Id: EPL_1775.java 7916 2014-06-10 12:30:12Z cariram $
 */
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class EPL_1775 extends SuiteCardPayment
{
	/**
	 * EPL-1775 Check content of view receipt
	 * @throws IOException
	 */
	@Test()
	public void epl_1775()
	{		
		try
		{
			logger.info(" EPL-1775 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			
			//You must be in Transaction Journal sub-module of Card Payment module
			cardPaymentSubPageNavigator(subModTrnJrn);
			//verifyBreadCrumbHeader(cpTransJourBC);
			verifyBreadCrumb("bread_crumb_id", cpTransJourBC);

			//Choose ALL as application
			//Create a search to have some transactions in the Main view
			logger.info("Step 2:");
			selectItem(getObject("select_appli_id"), valAll);
			getObject("journalsearch_xpath").click();
			logger.info("Selected application All");

			List<WebElement>  radiobutton =getObjects("card_payment_journal_input_xpath");
			logger.info("Step 3:");
			radiobutton.get(0).click();
			getObject("view_receipt_link").click();			
			waitForWindow();
			windowIds = driver.getWindowHandles();
			it = windowIds.iterator();					
			ParentWindow = it.next();
			popupWindow = it.next();
			driver.switchTo().window(popupWindow);
			text =getObject("receipt_xpath").getText();

			//verify date of transaction			
			verifyRegExp(text);			
			driver.switchTo().window(ParentWindow);

			//In the same time, you can be able to open several pop-up of receipts
			logger.info("Step 4:");
			verifyMultiplePopUp(radiobutton);
			logger.info(" EPL-1775 execution successful");
		} 
		catch (Throwable t)
		{
			handlePopUpException(t);
		}
	}
}