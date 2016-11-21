package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1767.java $
$Id: EPL_1767.java 7916 2014-06-10 12:30:12Z cariram $
 */

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1767 extends SuiteCardPayment {
	
	private String CustomData1_EN = "610450",UserData2_EN = "user_data2_10005_1",
			CustomData3_EN = "user_data5_10005_1", CustomData4_EN = "820072",CustomData2_EN="2012-10-29",noResultMessage="No result to your search. Please try another search.";
	private JavascriptExecutor js;
	private List<WebElement>  filterData;	
	
	/**
	 * Method to select app type from Combobox 
	 * @param id
	 * @param value
	 */
	private void javaScriptSelect(String id, String value) {
		js = (JavascriptExecutor) driver;
		js.executeScript("document.getElementById('" + id + "').value='"+ value + "'");
	}



	/**
	 * EPL-1768 All applications / Check filters / Check reset filters button
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1767() {
		try {
			logger.info(" EPL-1767 executing started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			logger.info("clicked on TransactionJournal");

			/*Choose ALL as application and check the list of filters of
			customized fields section*/
			logger.info("Step 2:");
			getObject("advanced_search_link").click();			
			verifyInputTextElt("customisedfiled_name", "type", "text");
			logger.info("filter1 Nus text box is verified as text box for application type 'All'");

			// The filter 2 of customized fields section must be a drop-down list
			Assert.assertFalse(new Select(getObject("userdata2_xpath")).isMultiple());
			logger.info("filter2 drop down list is verified for application type 'All'");
			
			/*Choose EMV FR as application and check the list of filters of
			customized fields section*/
			logger.info("Step 3:");
			advancedSearch(emvFRAppVal);			

			// customised filters verification
			verifyCustomisedFiltersEMVFR();

			/*Choose CHEQUE FR as application and check the list of filters of
			customized fields section*/
			logger.info("Step 4:");
			cardPaymentPageNavigator();
			cardPaymentSubPageNavigator(subModTrnJrn);
			advancedSearch(cheqFrAppVal);

			// customised filters verification
			verifyCustomisedFiltersChequeFR();

			/*Check that filter 1 (a text box) works correctly:
			Choose EMV FR as application and don't set any value in filter 1
			and execute search:*/
			logger.info("Step 5:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			multiSelect("select_appli", emvFRAppVal);
			getObject("search_link").click();	
			waitForTxtPresent("journal_row_count_xpath", displayText);	
			restoreTables();
			String nusVal = getFirstColVal("card_payment_journal_table_header_xpath", colNUS, "cp_j_header_col_data_xpath");			
			verifyRecordsPresence("customised_nustextbox_xpath", "nuscolumn_xpath");	

			/*Choose EMV FR as application and set an correct value
			(alphanumeric characters) in filter 1 NUS and execute search:*/
			verifyRecordsWithVal("customised_nustextbox_xpath", colNUS, nusVal);	

			// Check that filter 2 (a drop-down list) works correctly
			logger.info("Step 6:");
			clickAdvSearchAndClear("customised_nustextbox_xpath");
			getObject("customised_setdate_xpath").click();
			getObject("clear_link").click();
			getObject("search_link").click();
			Assert.assertTrue(getObjects("user_data2_EN_column_xpath").size() > 1);

			// Check that filter 2 (a drop-down list) works correctly
			clickAdvSearchAndClear("customised_nustextbox_xpath");
			selectItem(getObject("customised_filter2_xpath"), UserData2_EN);
			getObject("customised_setdate_xpath").click();
			getObject("clear_link").click();
			getObject("search_link").click();
			verifyFilter("user_data2_EN_column_xpath",UserData2_EN);

			// check that filter 3 a text box works correctly
			logger.info("Step 7:");
			clickOnAdvanSearch("customised_filter2_xpath");
			getObject("search_link").click();
			getObject("advanced_search_link").click();
			getObject("customised_setdate_xpath").click();
			getObject("clear_link").click();
			getObject("customised_nustextbox_xpath").clear();
			getObject("search_link").click();
			Assert.assertTrue(getObjects("custom_data1_EN_column_xpath").size() > 1);

			/*Choose EMV FR as application and set an correct value
			(alphanumeric characters) in filter 3 and execute search:*/
			getObject("advanced_search_link").click();					
			getObject("search_link").click();	
			waitForTxtPresent("journal_row_count_xpath", displayText);
			String custData1Val = getFirstColVal("card_payment_journal_table_header_xpath", colCustData1, "cp_j_header_col_data_xpath");			
			verifyRecordsWithVal("customised_filter3_xpath", colCustData1, custData1Val);

			/*Check that filter 4 (a date in calendar) works correctly
			get current date*/
			logger.info("Step 8:");
			getObject("advanced_search_link").click();
			setFromEndDate("customised_setdate_xpath",2012,9,29);
			getObject("search_link").click();
			verifyFilter("user_data2_EN_column_xpath",CustomData2_EN);

			getObject("advanced_search_link").click();
			waitForElementPresent("strt_date_id");
			cal=getCurrentDate("strt_date_id");

			// StartDate > EndDate = SartDate +4
			addEndDate(1,"customised_setdate_xpath",cal);
			getObject("search_link").click();
			Assert.assertEquals(getObject("noresult_search_xpath").getText(), noResultMessage);

			/*Check that filter 5 (a dropdown) works correctly
			Check that filter 2 (a drop-down list) works correctly*/
			logger.info("Step 9:");
			clickAdvSearchAndClear("customised_filter3_xpath");
			getObject("customised_setdate_xpath").click();
			getObject("clear_link").click();
			getObject("search_link").click();
			Assert.assertTrue(getObjects("custom_data3_EN_column_xpath").size() > 1);

			// Check that filter 5 (a drop-down list) works correctly
			clickAdvSearchAndClear("customised_filter3_xpath");
			selectItem(getObject("customised_filter5_xpath"), CustomData3_EN);
			getObject("customised_setdate_xpath").click();
			getObject("clear_link").click();
			getObject("search_link").click();
			verifyFilter("custom_data4_EN_column_xpath",CustomData3_EN);

			// check that filter 6 a text box works correctly
			logger.info("Step 10:");
			clickOnAdvanSearch("customised_filter5_xpath");
			getObject("search_link").click();
			getObject("advanced_search_link").click();
			getObject("customised_setdate_xpath").click();
			getObject("clear_link").click();
			getObject("customised_filter3_xpath").clear();
			getObject("search_link").click();
			Assert.assertTrue(getObjects("custom_data4_EN_column_xpath").size() >= 1);

			/*Choose EMV FR as application and set an correct value
			(alphanumeric characters) in filter 3 and execute search:*/
			clickOnAdvanSearch("customised_filter5_xpath");
			getObject("search_link").click();
			getObject("advanced_search_link").click();
			getObject("customised_filter3_xpath").clear();
			getObject("customised_filter6_xpath").sendKeys(CustomData1_EN);
			getObject("customised_setdate_xpath").click();
			getObject("clear_link").click();
			getObject("search_link").click();
			verifyFilter("custom_data4_EN_column_xpath",CustomData4_EN);

			logger.info(" EPL-1767 execution successful");
		}catch (Throwable t)
		{
			handleException(t);
		}
	}

	
	/**clicks on advanced serach
	 * 
	 * @param app
	 */
	private void advancedSearch(String app)
	{
		multiSelect("select_appli", app);
		getObject("search_link").click();
		getObject("advanced_search_link").click();
	}
	
	/**
	 * verifies records presence
	 * @param textLocator
	 * @param colLocator
	 */
	private void verifyRecordsPresence(String textLocator, String colLocator)
	{
		getObject("advanced_search_link").click();			
		getObject(textLocator).clear();
		getObject("search_link").click();
		Assert.assertTrue(getObjects(colLocator).size() > 1);
	}
	
	/**
	 * verifies records with val
	 * @param textLocator
	 * @param col
	 * @param colVal
	 */
	private void verifyRecordsWithVal(String textLocator, String col, String colVal)
	{
		getObject("advanced_search_link").click();			
		getObject(textLocator).clear();
		getObject(textLocator).sendKeys(colVal);			
		getObject("search_link").click();			
		verifyfilter(col, colVal);
	}
	
	/**
	 * Method to verify NusColumn contents
	 */
	private void verifyfilter(String colName, String colValExp) {

		if(isElementPresent("journal_message_xpath"))
		{
			logger.info("No result to search with the expected data.");
		}
		else
		{			
			verifyAllColValues("card_payment_journal_table_header_xpath", colName, "cp_j_header_col_data_xpath", colValExp);
		}		
	}
	
	private void clickOnAdvanSearch(String locator){
		getObject("advanced_search_link").click();
		javaScriptSelect("select_appli", emvFRAppVal);
		selectItem(getObject(locator), valAll);
	}
	
	private void clickAdvSearchAndClear(String locator){
		getObject("advanced_search_link").click();
		javaScriptSelect("select_appli", emvFRAppVal);
		getObject(locator).clear();
	}
	
	/**
	 * Customised method for verification of customised filters when EMV FR chosen as app type 
	 * @throws InterruptedException
	 */
	private void verifyCustomisedFiltersEMVFR() throws InterruptedException {
		// The filter 1 of customized fields section must be a text box
		verifyInputTextElt("customised_nustextbox_xpath", "type", "text");	
		logger.info("filter1 Nus text box is verified");

		// The filter 2 of customized fields section must be a drop-down
		// list
		Assert.assertFalse(new Select(getObject("customised_filter2_xpath")).isMultiple());
		logger.info("filter2 drop down list is verified");

		// The filter 3 of customized fields section must be a text box
		verifyInputTextElt("customised_filter3_xpath", "type", "text");
		logger.info("filter3 Text box is verified");

		//The filter 4 of customised fields section must be date in calender
		verifyInputTextElt("calender_img_xpath", "title", custmizedElt3Title);
		logger.info("filter4 date in calender is verified");

		// The filter 5 of customized fields section must be a drop-down
		// list
		Assert.assertFalse(new Select(getObject("customised_filter5_xpath")).isMultiple());
		logger.info("filter5 drop down list is verified");

		// The filter 6 of customized fields section must be a text box
		verifyInputTextElt("customised_filter6_xpath", "type", "text");
		logger.info("filter6 Text box is verified");

	}

	/**
	 * Customised method for verification of customised filters when Cheque FR chosen as app type 
	 * @throws InterruptedException
	 */
	private void verifyCustomisedFiltersChequeFR() throws InterruptedException {
		// The filter 1 of customized fields section must be a text box
		verifyInputTextElt("customised_nustextbox2_xpath", "type", "text");
		logger.info("filter1 Nus text box is verified");

		//The filter 2 of customized fields section must be a drop-down list
		Assert.assertFalse(new Select(getObject("customised_filter2_2_xpath")).isMultiple());
		logger.info("filter2 drop down list is verified");

		// The filter 3 of customized fields section must be a text box
		verifyInputTextElt("customised_filter3_2_xpath", "type", "text");
		logger.info("filter3 Text box is verified");

		//The filter 4 of customised fields section must be date in calender
		verifyInputTextElt("calender_img_xpath", "title", custmizedElt3Title);
		logger.info("filter4 date in calender is verified");

		// The filter 5 of customized fields section must be a drop-down
		// list
		Assert.assertFalse(new Select(getObject("customised_filter5_2_xpath")).isMultiple());
		logger.info("filter5 drop down list is verified");

		// The filter 6 of customized fields section must be a text box
		verifyInputTextElt("customised_filter6_2_xpath", "type", "text");
		logger.info("filter6 Text box is verified");

	}
	
	/**
	 * Method to verify CustomData4_EN column contents
	 */
	private void verifyFilter(String locator,String val) {
		filterData = getObjects(locator);
		for (int i = 0; i < filterData.size(); i++) {
			Assert.assertTrue(filterData.get(i).getText().contains(val));
		}
	}

}
