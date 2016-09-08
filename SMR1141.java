package com.ingenico.testsuite.eportal;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/eportal/SMR1141.java $
$Id: SMR1141.java 16708 2016-01-20 09:53:29Z rjadhav $
 */
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * SMR-1141:Enable/disable GPRS module
 * @author Hariprasad.KS
 */
public class SMR1141 extends SuiteEportal{

	/**
	 * Enable/disable GPRS module
	 */
	@Test(groups="SMR1141")
	public void smr1141(){
		try {

			eportalCust=testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEportal",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1141 execution started");
			final String customerName=testDataOR.get("customer");
			
			//Access ePortal and select customer,'GPRS' menu should be available
			logger.info("Step 1 :");
			logger.info("Access eportal with superuser");
			selUtils.verifyElementDisp(selUtils.getCommonObject("gprs_tab_xpath"), GPRS);

			// Log out from ePortal, access everest with customer
			logger.info("Step 2,3,4 :");
			logoutNEvselCust(customerName);

			//Disable 'GPRS' module and validate
			logger.info("Step 5 :");
			disableModNVal("gprs_ckbx_id", GPRS);
		
			//Access ePortal with a superuser and select the customer
			//The 'GPRS' menu should not be available
			logger.info("Step 6 :");
			logoutEpSelCust(customerName);
			Assert.assertFalse(selUtils.isElementPresentCommon("gprs_tab_xpath"),GPRS+" menu is available");
			logger.info("GPRS menu is not available");

			// Log out from ePortal, access everest with customer
			logger.info("Step 7,8 :");
			logoutNEvselCust(customerName);

			// Enable 'GPRS' module and validate
			logger.info("Step 9 :");
			enableModNVal("gprs_ckbx_id", GPRS);
			
			//Access ePortal with a superuser and select the customer
			//The 'GPRS' menu should be available
			logger.info("Step 10 :");
			logoutEpSelCust(customerName);
			selUtils.verifyElementDisp(selUtils.getCommonObject("gprs_tab_xpath"), GPRS);

			logger.info("SMR1141 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}

	}
	
	
}
