package com.ingenico.testsuite.eportal;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/eportal/SMR1145.java $
$Id: SMR1145.java 16708 2016-01-20 09:53:29Z rjadhav $
 */

import java.util.concurrent.TimeUnit;

import org.testng.annotations.Test;

/**
 * SMR-1141:Enable/disable Asset tracking module
 * @author Joel F
 */
public class SMR1145 extends SuiteEportal{

	/**
	 * Enable/disable Asset tracking module
	 */
	@Test(groups="SMR1145")
	public void smr1145(){
		try {

			eportalCust=testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEportal",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1145 execution started");
			final String customerName=testDataOR.get("customer");
			
			//Access ePortal with superuser and select the <customer>
			//then go to 'TMS' menu
			logger.info("Step 1 :");
			logger.info("Access eportal with superuser");
			driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
			vPresenceOfSubMod(ASSETTKING,selUtils.getCommonObject("tms_tab_xpath"),"tms_submods_xpath");
			
			// Log out from ePortal, access everest with customer
			logger.info("Step 3,4,5 :");
			logoutNEvselCust(customerName);
			
			//Disable 'Asset tracking' module and validate
			logger.info("Step 6 :");
			selUtils.unSlctChkBoxOrRadio(selUtils.getCommonObject("asstrking_ckbx_id"));
			selUtils.getCommonObject("savebttn_xpath").click();
			selUtils.verifyElementNotSelected(selUtils.getCommonObject("asstrking_ckbx_id"));
			logger.info("Disabled asset tracking module and validated");
			
			logger.info("Step 7 :");
			logoutEpSelCust(customerName);
			logger.info("Access eportal with superuser");
			vSubModNotPresent(ASSETTKING,selUtils.getCommonObject("tms_tab_xpath"),"tms_assttrack_xpath");
			
			logger.info("Step 8,9 :");
			logoutNEvselCust(customerName);
			
			//Enable 'Asset tracking' module and validate
			logger.info("Step 10 :");
			selUtils.slctChkBoxOrRadio(selUtils.getCommonObject("asstrking_ckbx_id"));
			selUtils.getCommonObject("savebttn_xpath").click();
			selUtils.verifyElementSelected(selUtils.getCommonObject("asstrking_ckbx_id"));
			logger.info("Enabled Asset tracking module and validated");
			
			logger.info("Step 11 :");
			logoutEpSelCust(customerName);
			logger.info("Access eportal with superuser");
			vPresenceOfSubMod(ASSETTKING,selUtils.getCommonObject("tms_tab_xpath"),"tms_submods_xpath");

			logger.info("SMR1145 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}

	}
	
}
