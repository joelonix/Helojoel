package com.ingenico.testsuite.eportal;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/eportal/SMR1143.java $
$Id: SMR1143.java 16708 2016-01-20 09:53:29Z rjadhav $
 */

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * SMR-1143:Enable/disable TMS module
 * @author Joel F
 */
public class SMR1143 extends SuiteEportal{

	/**
	 * Enable/disable TMS module
	 */
	@Test(groups="SMR1143")
	public void smr1143(){
		try {
			
			eportalCust=testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEportal",testDataOR.get("superuser"),firstName,lastName);

			logger.info("SMR1143 execution started");
			final String customerName=testDataOR.get("customer");
			
			//Access ePortal with superuser and select the <customer>
			//then go to 'TMS' menu
			logger.info("Step 1 :");
			logger.info("Access eportal with superuser");
			selUtils.verifyElementDisp(selUtils.getCommonObject("tms_tab_xpath"), "TMS module is available");
			
			// Log out from ePortal, access everest with customer
			logger.info("Step 2,3,4 :");
			logoutNEvselCust(customerName);
			
			//Disable 'TMS' module and validate
			logger.info("Step 5 :");
			selUtils.unSlctChkBoxOrRadio(selUtils.getCommonObject("tms_ckbx_id"));
			selUtils.unSlctChkBoxOrRadio(selUtils.getCommonObject("asstrking_ckbx_id"));
			selUtils.getCommonObject("savebttn_xpath").click();
			selUtils.verifyElementNotSelected(selUtils.getCommonObject("tms_ckbx_id"));
			selUtils.verifyElementNotSelected(selUtils.getCommonObject("asstrking_ckbx_id"));
			logger.info("Disabled tms and Asset tracking module and validated");
			
			// Access ePortal with a superuser and select the <customer>
		    //The 'TMS' menu should not be available
			logoutEpSelCust(customerName);
			Assert.assertFalse(selUtils.isElementPresentCommon("tms_tab_xpath"),"TMS menu is available");
			logger.info("TMS menu is not available");
			
			// Log out from ePortal, access everest with customer
			logger.info("Step 7,8 :");
			logoutNEvselCust(customerName);
			
			//Enable 'TMS' module and validate
			logger.info("Step 9 :");
			selUtils.slctChkBoxOrRadio(selUtils.getCommonObject("tms_ckbx_id"));
			selUtils.slctChkBoxOrRadio(selUtils.getCommonObject("asstrking_ckbx_id"));
			selUtils.getCommonObject("savebttn_xpath").click();
			selUtils.verifyElementSelected(selUtils.getCommonObject("tms_ckbx_id"));
			selUtils.verifyElementSelected(selUtils.getCommonObject("asstrking_ckbx_id"));
			logger.info("Enabled Asset tracking and TMS module and validated");
			
			//Access ePortal with superuser and select the <customer>
		    //The 'TMS' menu should be available
			logoutEpSelCust(customerName);
			logger.info("Access eportal with superuser");
			selUtils.verifyElementDisp(selUtils.getCommonObject("tms_tab_xpath"), "TMS module is available");

			logger.info("SMR1143 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}

	}
	
}
