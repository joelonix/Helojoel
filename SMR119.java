package com.ingenico.testsuite.tmsmanagement;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/tmsmanagement/SMR119.java $
$Id: SMR119.java 16708 2016-01-20 09:53:29Z rjadhav $
 */

import org.testng.annotations.Test;
/**
 * SMR-119:Enable/disable report group
 * @author Hariprasad.KS
 *
 */
public class SMR119 extends SuiteTmsManagement{

	/**
	 * Enable/disable report group
	 * @param browser
	 */
	@Test(groups="SMR119")
	public void smr119() {

		try{
			eportalCust = testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name"),customer=testDataOR.get("customer"), reportGrupName=testDataOR.get("report_group");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR119 execution started");

			//Access Everest with a superuser
			//Navigate to TMS reports and edit report group name
			logger.info("Step 1,2,3:");
			navigateNeditRPT(reportGrupName);

			//Uncheck the "Active" option, save and validate message
			logger.info("Step 4:");
			selUtils.unSlctChkBoxOrRadio(selUtils.getObject("active_chkbox_id"));
			selUtils.clickOnWebElement(selUtils.getObject("rept_savebbtn_xpath"));
			vErrorMsg(selUtils.getCommonObject("errorreport_id"));
			selUtils.verifyTextContains(selUtils.getCommonObject("msg_xpath"), "successfully updated");

			//Access ePortal with customer and go to TMS->Available reports
			//And validate report_group is not available
			logger.info("Step 5,6:");
			loginNclkAvailRpt(customer);
			vRptNotDisp("rept_grpname_xpath",customer,reportGrupName);

			//Login everest and go to TMS reports and edit reports
			logger.info("Step 8:");
			logout();
			logger.info("Logout successfully");
			login("URLEverest", "username_name", "password_name", testDataOR.get("superuser"),"superuser_password", "submit_button_xpath",firstName,lastName);
			navigateNeditRPT(reportGrupName);

			//Check the "Active" option, save and validate message
			logger.info("Step 9:");
			selUtils.slctChkBoxOrRadio(selUtils.getObject("active_chkbox_id"));
			selUtils.clickOnWebElement(selUtils.getObject("rept_savebbtn_xpath"));
			// verify error messages
			vErrorMsg(selUtils.getCommonObject("errorreport_id"));
			selUtils.verifyTextContains(selUtils.getCommonObject("msg_xpath"), "successfully updated");

			//Access ePortal with customer and go to TMS->Available reports
			//And validate report_group is available
			logger.info("Step 10:");
			loginNclkAvailRpt(customer);
			vReportIsDisplayed("rept_grpname_xpath",customer,reportGrupName);

			logger.info("SMR119 executed successfully");	
		}
		catch (Throwable t) {

			handleException(t);
		}
	}
}

