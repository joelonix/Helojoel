package com.ingenico.testsuite.tmsmanagement;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/tmsmanagement/SMR1008.java $
$Id: SMR1008.java 16708 2016-01-20 09:53:29Z rjadhav $
 */

import org.testng.annotations.Test;
/**
 * SMR-1008:Enable/disable report
 * @author Hariprasad.KS
 *
 */
public class SMR1008 extends SuiteTmsManagement{

	/**
	 * Add a report to a group .
	 * @param browser
	 */
	@Test(groups="SMR1008")
	public void smr1008() {

		try{
			eportalCust = testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name"),rptShrtName=testDataOR.get("report_short_name"), customer=testDataOR.get("customer"), reportGrupName=testDataOR.get("report_group");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1008 execution started");

			//Access Everest with a superuser
			//Navigate to TMS reports and edit report group name and short name
			logger.info("Step 1,2,3,4:");
			navigateNeditRPT(reportGrupName);
			clkOnEditshrtName("editshrtname_xpath", rptShrtName);
			
			//Uncheck the "Active" option, validate, save and validate message
			logger.info("Step 5:");
			selUtils.unSlctChkBoxOrRadio(selUtils.getObject("rpts_activchkbox_id"));
			selUtils.verifyElementNotSelected(selUtils.getObject("rpts_activchkbox_id"));
			selUtils.clickOnWebElement(selUtils.getObject("addrpts_bbtn_id"));
			selUtils.clickOnWebElement(selUtils.getObject("rept_savebbtn_xpath"));
			vErrorMsg(selUtils.getCommonObject("errorreport_id"));
			selUtils.verifyTextContains(selUtils.getCommonObject("msg_xpath"), "successfully updated");
			
			//Access ePortal with customer and go to TMS->Available reports
			logger.info("Step 6,7:");
			loginNclkAvailRpt(customer);
			//vRptNotDisp("rept_grpname_xpath",customer,rptShrtName);
			vRptNotDisp("childrept_grpname_xpath",reportGrupName,rptShrtName);
			
			//Login everest and go to TMS reports and edit reports
			logger.info("Step 8,9,10:");
			logout();
			logger.info("Logout successfully");
			login("URLEverest", "username_name", "password_name", testDataOR.get("superuser"),"superuser_password", "submit_button_xpath",firstName,lastName);
			navigateNeditRPT(reportGrupName);
			clkOnEditshrtName("editshrtname_xpath", rptShrtName);
			
			//Check the "Active" option, validate and save
			logger.info("Step 11:");
			selUtils.slctChkBoxOrRadio(selUtils.getObject("rpts_activchkbox_id"));
			selUtils.verifyElementSelected(selUtils.getObject("rpts_activchkbox_id"));
			selUtils.clickOnWebElement(selUtils.getObject("addrpts_bbtn_id"));
			selUtils.clickOnWebElement(selUtils.getObject("rept_savebbtn_xpath"));
			vErrorMsg(selUtils.getCommonObject("errorreport_id"));
			selUtils.verifyTextContains(selUtils.getCommonObject("msg_xpath"), "successfully updated");
			
			//Access ePortal with customer and go to TMS->Available reports
			logger.info("Step 12:");
			loginNclkAvailRpt(customer);
			vReportIsDisplayed("rept_grpname_xpath",customer,rptShrtName);
			//driver.switchTo().defaultContent();
			logger.info("SMR1008 executed successfully");	
		}
		catch (Throwable t) {

			handleException(t);
		}
	}

	
}

