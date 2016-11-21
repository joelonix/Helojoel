package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1772.java $
$Id: EPL_1772.java 7916 2014-06-10 12:30:12Z cariram $
 */
import java.io.IOException;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1772 extends SuiteCardPayment
{
	private String path,shopName,appType;	

	/**
	 * EPL-1772 Check trans journal - All applications / Check Detailed View
	 * 
	 * @throws IOException
	 */
	@Test()
	public void epl_1772()
	{		
		try
		{
			logger.info(" EPL-1772 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			waitForElementPresent("journalsearch_xpath");
			getObject("journalsearch_xpath").click();
			logger.info("Search clicked ");

			//Fetching table header and putting it into String list
			logger.info("Step 2:");
			shopName = getFirstColVal("card_payment_journal_table_header_xpath", colShop, "cp_j_header_col_data_xpath");
			appType = getFirstColVal("card_payment_journal_table_header_xpath", colAppType, "cp_j_header_col_data_xpath");
			logger.info("Fetched application type for");
			getObject("plus_icon_xpath").click();			
			logger.info("Clicked plus icon");

			// switch to the card payment details popup window			
			switchToWindow();
			//Verify if details window is displayed
			Assert.assertEquals(getObject("cp_detailedview_table_xpath").isDisplayed(), true);

			//In Details Window verify if shopname and apptype tally with table in application
			Assert.assertTrue(getObject("cpjournal_detailshop_xpath").getText().contains(shopName), "The selected Transaction contains different shop name ");
			Assert.assertTrue(getObject("cpjournal_detailapp_xpath").getText().contains(appType), "The selected Transaction contains different App name ");
			driver.close();

			//Verify if multiple detail windows can be opened
			driver.switchTo().window(ParentWindow);
			getObject("plus_icon_xpath").click();			
			waitForWindow();
			path = getPath("plus_icon_xpath").replace("tr[1]", "tr[2]");
			getObjectDirect(By.xpath(path)).click();			
			waitForWindow();
			closeChildWindows();
			logger.info("VErified the multiple windows are opened and closed succesfully");			
			logger.info(" EPL-1772 execution successful");
		} catch (Throwable t)
		{
			handlePopUpException(t);
		}
	}
}
