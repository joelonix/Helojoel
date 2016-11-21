package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1753.java $
$Id: EPL_1753.java 7916 2014-06-10 12:30:12Z cariram $
*/

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1753 extends SuiteCardPayment{
	private boolean isMIDCorrect = false;
	private String cashierVal="!@#$%^";
	private WebElement recieptTable;
	private List<WebElement> recieptRows,rows;
	private int index=0,pageNum,totalPages;
	
	
	
	
	
	/**
	 * EPL-1753 To Verify the application type is same in transaction overview and journal based on MID
	 * 
	 * @throws IOException
	 */
	@Test()
	public void epl_1753()
	{		
		try
		{
			logger.info(" EPL-1753 executing started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);	
			
			getObject("search_link").click();
			
			goToMidLevel("settlement_id");	
			
			//Fetching midID and AppID of the first row as Test Data
			String midID = getFirstColVal("cp_trn_header_xpath", tabMid, "cp_trn_header_col_data_xpath");			
			logger.info("Test data is ready, "+midID);
			
			//Navigate to transcation journal.Set a correct value (alphanumeric character) in MID filter and execute search
			logger.info("Step 3:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			getObject("mid_id").sendKeys(midID);
			getObject("search_link").click();	
			logger.info("Clicked Search after entering midID");
			
			//The search has to be successful
			verifySearchSuccessful();
			isCorrectMIDvalDisplayed(midID);
			
			//Don’t set any value in MID filter and execute search.In the main view, there are some transactions.
			logger.info("Step 2:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			getObject("search_link").click();
			logger.info("Clicked search without entering MID id");
			verifySearchSuccessful();
			
			// Set an incorrect value (special characters) in Cashier filter and execute search.The clear and friendly error message is displayed
			logger.info("Step 4:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			getObject("cashier_id").sendKeys(cashierVal);
			getObject("search_link").click();
			logger.info("Clicked search entering wrong cashier id");
			
			verifyTextContains("alert_token_text_xpath", altMsg1);
			verifyTextContains("alert_token_text_xpath", altMsg2);
			logger.info("Validated alert message");
			logger.info(" EPL-1753 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	
	
	/**
	 * verifySearchSuccessful counts the number of rows to verify if size is greater than 1 implying search is successful
	 * @return void
	 */
	private void verifySearchSuccessful(){
		recieptTable = getObject("table_card_payment_journal_xpath");
		recieptRows = recieptTable.findElements(By.tagName("tr"));
		if(recieptRows.size()>=1 && !recieptRows.get(0).getText().contains("No result to your search")){
			logger.info("Search successful");
		}
		else{
			logger.info("Search failed");
			Assert.fail();
		}
	}
	
	/**
	 * areShopsDisplayed asserts if expected Shops are displayed in search results
	 */
	private void isCorrectMIDvalDisplayed(String midID){
		pageNum=0;
		while(pageNum < totalPages){
			int count=0;
			rows = getObject("table_card_payment_journal_xpath").findElements(By.tagName("tr"));
			index=getIndexForColHeader("card_payment_journal_table_header_xpath", colMID);
			for(WebElement row:rows){
				List<WebElement> col= row.findElements(By.tagName("td"));
				if(col.get(index).getText().contains(midID)){
					isMIDCorrect=true;
					Assert.assertTrue(isMIDCorrect);
				}
				else{
					Assert.fail();
				}
				count++;
			}
			if(count==rows.size()){
				getObject("tj_nextpage_image_xpath").click();
				pageNum++;
			}
			logger.info("Validated shops for the particular country at page " + pageNum);
		}
	}
}
