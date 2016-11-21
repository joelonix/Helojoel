package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1678.java $
$Id: EPL_1678.java 7858 2014-06-09 08:59:33Z cariram $
*/
import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1678 extends SuiteCardPayment
{
	

	/**
	 * EPL-1678 Check organization of snapshot sub-module
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1678()
	{		
		try
		{
			logger.info(" EPL-1678 execution started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);			
			
			//Verifying default tables and graphs
			logger.info("Step 2:");
			verifyElementDisp("yest_table_xpath");
			verifyElementDisp("pay_trans_xpath");
			verifyElementDisp("pay_trans_amnt_xpath");
			verifyElementDisp("sale_value_xpath");
			verifyElementDisp("hist_tab1_xpath");
			verifyElementDisp("hist_tab2_xpath");	
			logger.info("Yesterday’s Main Information:graphs and tables, Evolution graph section: is displayed as expected");
			
			verifyElementDisp("receipt_id");
			verifyElementDisp("server_id");	
			getContains(snapShotLevel1);			
			verifyElementDisp("view_link");
			verifyElementDisp("see_also_id");
			logger.info("Choose Displayed Time filter (Receipt and server time), country filter with a view button and See also menu is displayed as expected");
			logger.info(" EPL-1678 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
}
