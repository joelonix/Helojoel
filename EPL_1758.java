package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1758.java $
$Id: EPL_1758.java 7916 2014-06-10 12:30:12Z cariram $
*/

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1758 extends SuiteCardPayment
{
	private int totalPages;
	private List<WebElement> rows;

	

	/**
	 * EPL-1778 All applications / Check Filters / Check Settlement filters work correctly / Settlement (YES / NO) filter
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1758()
	{		
		try
		{
			logger.info(" EPL-1758 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);

			getObject("advanced_search_link").click();

			//In settlement filter, no checked YES and NO and execute search
			logger.info("Step 2, 3:");
			getObject("yes_id").click();
			getObject("no_id").click();
			getObject("search_link").click();
			waitForTxtPresent("journal_row_count_xpath", displayText);
			
			//Fetching number of pages 
			totalPages=fetchingNoOfPages();


			//Verifying if settlement yes and no are unchecked
			logger.info("Step 4:");
			logger.info("Validating Settlement with both 'Yes' and 'No' unchecked ");
			vColImgs(totalPages, "settlement_col_xpath", "settlement_image_xpath", yesLabel, noLabel);

			//In settlement filter,checked YES and NO and execute search
			logger.info("Step 5:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			getObject("advanced_search_link").click();
			verifyElementSelected("yes_id");
			verifyElementSelected("no_id");
			getObject("search_link").click();
			waitForTxtPresent("journal_row_count_xpath", displayText);
			
			//Fetching number of pages 
			totalPages=fetchingNoOfPages();

			//Verifying if settlement yes and no are checked
			logger.info("Step 6:");
			logger.info("Validating Settlement with both 'Yes' and 'No' checked ");
			vColImgs(totalPages, "settlement_col_xpath", "settlement_image_xpath", yesLabel, noLabel);

			//In settlement filter, checked YES and execute search
			cardPaymentSubPageNavigator(subModTrnJrn);
			getObject("advanced_search_link").click();
			verifyElementSelected("yes_id");
			getObject("no_id").click();
			getObject("search_link").click();
			waitForTxtPresent("journal_row_count_xpath", displayText);
			
			//Fetching number of pages 
			totalPages=fetchingNoOfPages();

			//Verifying if settlement yes is checked
			logger.info("Validating Settlement with 'Yes' checked and 'No' unchecked ");
			vColImgs(totalPages, "settlement_col_xpath", "settlement_image_xpath", yesLabel, noLabel);

			//In settlement filter, checked NO and execute search
			logger.info("Step 7:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			getObject("advanced_search_link").click();
			getObject("yes_id").click();
			verifyElementSelected("no_id");
			getObject("search_link").click();
			waitForTxtPresent("journal_row_count_xpath", displayText);
			
			rows = getObject("table_card_payment_journal_xpath").findElements(By.tagName("tr"));
			if(rows.size()==1 && rows.get(0).getText().contains("No result to your search")){
				logger.info("No test data for Settlement filter NO");
				Assert.fail("No test data for Settlement filter NO");
			}
			//Fetching number of pages 
			totalPages=fetchingNoOfPages();

			//Verifying if settlement no is checked
			logger.info("Validating Settlement with 'Yes' unchecked and 'No' checked ");
			vColImgs(totalPages, "settlement_col_xpath", "settlement_image_xpath", yesLabel, noLabel);

			logger.info(" EPL-1758 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

}
