package com.ingenico.testsuite.eportal;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/eportal/SMR1137.java $
$Id: SMR1137.java 16708 2016-01-20 09:53:29Z rjadhav $
 */
import org.testng.annotations.Test;
/**
 * SMR-1137:Enable/disable Card Payment module
 * @author Hariprasad.KS
 */
public class SMR1137 extends SuiteEportal{

	/**
	 * Enable/disable Card Payment module
	 * @param browser
	 */
	@Test(groups="SMR1137")
	public void smr1137(){
		try {
			//final String firstName=testDataOR.get("mono_first_name"),lastName=testDataOR.get("mono_last_name"); 
			//login("URLEportal",testDataOR.get("mono_user_login"),firstName,lastName);
			eportalCust=testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEportal",testDataOR.get("superuser"),firstName,lastName);
			//login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			/*logger.info("SMR1137 execution started");
			final String customerName=testDataOR.get("customer");

			//Access ePortal and 'In Store Payment' menu should be available
			logger.info("Step 1 :");
			logger.info("Access eportal with superuser");
			selUtils.verifyElementDisp(selUtils.getCommonObject("instorepay_tab_xpath"), INSTOREPAY);

			//logout from eportal and login everest, select customer in eportal
			logger.info("Step 2,3,4:");
			logoutNEvselCust(customerName);
			
			//Disable ‘ePayment’ module and validate
			logger.info("Step 5:");
			disableModNVal("ep_ckbx_id", EPAYMENT);
						
			//Disable 'Card Payment' module and validate
			logger.info("Step 6:");
			disableModNVal("cp_ckbx_id", CARDPAYMENT);
						
			//Access ePortal with a superuser and select customer
			//In Store Payment menu should not be available
			logger.info("Step 7:");
			logoutEpSelCust(customerName);
			Assert.assertFalse(selUtils.isElementPresentCommon("instorepay_tab_xpath"),INSTOREPAY+" menu is available");
			logger.info("In store payment menu is not available");

			//logout from eportal and login everest, select customer in eportal
			logger.info("Step 8,9:");
			logoutNEvselCust(customerName);
			
			//Enable 'Card Payment' module and validate
			logger.info("Step 10");
			enableModNVal("cp_ckbx_id", CARDPAYMENT);
		
			
			//Access ePortal and 'In Store Payment' menu should be available
			logger.info("Step 11");
			logoutEpSelCust(customerName);
			selUtils.verifyElementDisp(selUtils.getCommonObject("instorepay_tab_xpath"), INSTOREPAY);*/
			logger.info("SMR1137 execution started");
		}catch (Throwable t) {
			handleException(t);
		}
	}
	
}
