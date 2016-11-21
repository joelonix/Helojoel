package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1730.java $
$Id: EPL_1730.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1730 extends SuiteCardPayment {
	
	
	private String midID,appType;
		
		
	
	/**
	 * EPL-1730 To Verify the application type is same in transaction overview and journal based on MID
	 * 
	 * @throws IOException
	 */
	@Test()
	public void epl_1730()
	{		
		try
		{
			logger.info(" EPL-1730 executing started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);	
			verifyBreadCrumb(cpTransOverBC); 
			
			//Selecting Last month as part of test data
			selectItem(getObject("period_id"), lastMonth);
			
			//Navigating to Level 1
			logger.info("Step 2:");
			getObject("search_link").click();
			goToMidLevel("settlement_id");			
			
			//Fetching midID and AppID of the first row as Test Data
			logger.info("Step 3:");
			midID = getFirstColVal("cp_trn_header_xpath", tabMid, "cp_trn_header_col_data_xpath");
			appType = getFirstColVal("cp_trn_header_xpath", colAppType, "cp_trn_header_col_data_xpath");			
			logger.info("Test data is ready");
			
			//Navigate to transcation journal
			logger.info("Step 4:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			selectItem(getObject("period_id"), lastMonth);
			
			// Search based on MID
			getObject("mid_id").sendKeys(midID);
			getObject("search_link").click();
			
			logger.info("Clicked Search after entering midID");
			verifyAllColValues("card_payment_journal_table_header_xpath", colAppType, "cp_j_header_col_data_xpath", appType);			
			logger.info(" EPL-1730 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	

}
