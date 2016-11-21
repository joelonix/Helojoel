package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1781.java $
$Id: EPL_1781.java 7916 2014-06-10 12:30:12Z cariram $
 */
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1781 extends SuiteCardPayment{
	private String yestDate,startDate,endDate,expected;
	private List<WebElement> seeAlsoElements;	
	private String[] linksArr = {snapshotBC, cpTransOverBC, cpTransJourBC, cpTransSettleBC,cpAvoirBC};
	private WebElement table,col;

	
	/** 
	 * Verifies the option menu items like CSV, PDF download links 
	 * @param Locator
	 */
	private void verifyOptionMenuPresent(String locator){
		boolean isPresent = getObject(locator).isDisplayed();
		Assert.assertEquals(isPresent, true);
	}

	/**
	 * verifyDefaultSelection takes select object and default val and verifies with actual default val
	 * @param object
	 * @param defaultVal
	 * @return void
	 */
	private void verifyDefaultSelection(String object,String defaultVal){
		String actual = getSelectedItem(getObject(object));
		Assert.assertTrue(actual.contains(defaultVal), defaultVal +" is not disaplayed as deafult value");
	}

	/**
	 * verifyCheckboxChecked takes checkbox object and verifies if its checked
	 * @param object
	 * @return void
	 */
	private void verifyCheckboxChecked(String object){
		Assert.assertEquals(getObject(object).isDisplayed(), true);
		String isChecked = getObject(object).getAttribute("checked");
		Assert.assertEquals(isChecked, "true");
	}

	/**
	 * EPL-1781 To Verify Transaction Reconciliation / EMV AUS Application / Westpac – check organization of Transaction reconciliation feature
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1781()
	{		
		try
		{
			logger.info(" EPL-1781 executing started");			
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			
			// The breadcrumb must contain this text “Transaction Reconciliation”
			cardPaymentSubPageNavigator(subModTrnRecon);
			waitForElementPresent("export_csv_xpath");
			//verifyBreadCrumbHeader(cpReconBC);
			verifyBreadCrumb("bread_crumb_id", cpReconBC);
			logger.info("Transaction Reconciliation submodule is visible");

			//In the Main View, you must see this message “Please select filter(s) in the right hand side column of the screen”
			logger.info("Step 2:");
			yestDate = getYestDate();
			xpath=getPath("table_store_reconciliation_xpath").replace("YESTDATE", yestDate);		
			table =getObjectDirect(By.xpath(xpath));
			col = table.findElement(By.tagName("td"));			
			Assert.assertEquals(col.getText(), reconFilterMsg);

			//Verify options menu
			verifyOptionMenuPresent("export_csv_xpath");
			verifyOptionMenuPresent("export_pdf_xpath");
			verifyOptionMenuPresent("add_to_saved_searches_id");
			logger.info("Verified options menu");

			//In see also menu, there are three shortcuts and verify they work correctly
			verifyPresenceOfCols("seealso_links_xpath", linksArr);
			seeAlsoElements=get_list_of_elements("seealso_links_xpath");
			for(int seeAlsoIndex=0;seeAlsoIndex<seeAlsoElements.size();seeAlsoIndex++){
				expected = seeAlsoElements.get(seeAlsoIndex).getText();
				seeAlsoElements.get(seeAlsoIndex).click();	
				waitForElementPresent("bread_crumb_id");
				verifyBreadCrumb("bread_crumb_id", expected);
				cardPaymentSubPageNavigator(subModTrnRecon);
				seeAlsoElements=get_list_of_elements("seealso_links_xpath");
			}
			logger.info("Verified see also menu items");

			//Verifying username
			verifyTextPresent("username_xpath", userName);

			//Verify From /To Calenders have day date range
			//Verify, By default, each calendar must contain day date for start date
			startDate = getObject("strt_date_id").getAttribute("value");
			Assert.assertEquals(dateHasCorrectFormat(startDate), true);

			//Verify, By default, each calendar must contain day date for end date 
			endDate = getObject("end_date_id").getAttribute("value");
			Assert.assertEquals(dateHasCorrectFormat(endDate), true);
			logger.info("Validated the dates");

			//Verify organization structure. By default, all levels contains “choose one” 
			verifyDefaultSelection("country_id", defaultDropdownVal);
			verifyDefaultSelection("city_id", defaultDropdownVal);
			verifyDefaultSelection("shop_id", defaultDropdownVal);
			logger.info("Verified the organization structure and default values");

			//Verify Reconciliation filter with two check boxes: store and bank. By default , the two boxes must be checked 
			verifyCheckboxChecked("reconciliation_store_id");
			verifyCheckboxChecked("reconciliation_bank_id");
			logger.info("Validated reconciliation filters");

			//Verifying Search and Reset Filters			
			verifyElementDispCommon("search_link");
			verifyElementDisp("cpjourreset_xpath");
			logger.info("Validated search and reset filters");
			logger.info(" EPL-1781 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
}
