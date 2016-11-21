package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1742.java $
$Id: EPL_1742.java 7888 2014-06-09 13:17:58Z cariram $
*/

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1742 extends SuiteCardPayment
{
	
	private String linksExpArr[]={snapshotBC, cpTransOverBC, cpTransSettleBC, cpReconBC,cpAvoirBC, cpEPJourBC}; 
	private int count=0;
	
	/**
	 * EPL-1742 Check organization of Transaction Journal sub-module
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1742()
	{		
		try{
			logger.info("  EPL-1742 started execution");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);	
			
			//Assert.assertTrue(getObject("search_link").isDisplayed());
			verifyElementDisp("search_link");
			logger.info("Search tool is present");
			
			getObject("search_link").click();			
			waitForElementPresent("cpjournal_table_id");
			verifyElementDisp("cpjournal_table_id");
			logger.info("Search results table to show the transaction data for the current search is displayed");
			
			logger.info("Step 2:");
			verifyElementDisp("export_csv_xpath");
			verifyElementDisp("export_pdf_xpath");
			verifyElementDisp("add_to_saved_searches_id");
			logger.info("Options menu (with Add to favorites, export CSV, export PDF options) is present");
			logger.info("Links present in See Also menu are : ");
			verifyLeftLinks("seealso_links_xpath", linksExpArr)	;
			logger.info("See also menu (Snapshot, Transaction overview, Settlement Overview, Transaction reconciliation and ePayment journal options) is displayed");
			logger.info(" EPL-1742 execution successful");
			} 
		catch (Throwable t)
		{
			handleException(t);
		}	
	}
	
	
	/**
	 * Verifies list items are same as an array items
	 * @param locator
	 * @param arr
	 */
	private void verifyLeftLinks(String locator, String [] arr)
	{
		List<WebElement> seeLinksOptions=get_list_of_elements(locator);
		for(count=0;count<seeLinksOptions.size();count++)
		{			
			Assert.assertEquals(arr[count], seeLinksOptions.get(count).getText());			
		}	
	}
}
