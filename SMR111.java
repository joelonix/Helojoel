package com.ingenico.testsuite.tmsmanagement;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/tmsmanagement/SMR111.java $
$Id: SMR111.java 17374 2016-03-03 09:00:40Z amsingh $
 */

import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;
/**
 * SMR-111:Create a report group
 * @author Joel.Samuel
 *
 */
public class SMR111 extends SuiteTmsManagement{

	/**
	 * Create a report group from Everest
	 * @param browser
	 */
	@Test(groups="SMR111")
	public void smr111() {

		try{
			eportalCust=testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name"),strErrorMsg;
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR111 execution started");
			
			final String rptShrtName=testDataOR.get("report_short_name");
			final String rptGroup=testDataOR.get("report_group");

			//Access Everest with a superuser
			//Go to "TMS >> Reports" submenu
			logger.info("Step 1, 2:");
			navigateToSubPage(TMSREPORTS,selUtils.getCommonObject("tms_tab_xpath"),selUtils.getCommonObject("tms_reports_xpath"));

			//Click the "+" button
			logger.info("Step 3:");
			//selUtils.clickOnWebElement(selUtils.getCommonObject("plusbtn_xpath"));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();",selUtils.getCommonObject("plusbtn_xpath"));
			
			//Add report_group and report_short_name
			logger.info("Step 4,5:");
			generateReport();
			logger.info("Report short name " +rptShrtName+ " added");
			colIndex=selUtils.getIndexForColHeader("tmrptsheader_xpath",SHRTNAMECOL);
			verifyLvlColLvlValPresence("rptstablst_css",colIndex,rptShrtName);
			
			//Click the "Save" button ,The report is successfully added
			logger.info("Step 6:");
			selUtils.clickOnWebElement(selUtils.getObject("rept_savebbtn_xpath"));
			// verify error messages
			if (selUtils.getCommonObject("errorreport_id").isDisplayed()) {
				strErrorMsg = selUtils.getCommonObject("errorreport_id").getText().trim();
				Assert.fail(strErrorMsg + " due to fail");
			}	
			
			logger.info("Report name "+rptGroup+" successfully added");
			selUtils.verifyTextContains(selUtils.getCommonObject("msg_xpath"), "Report name "+rptGroup+" successfully added");

			//Access ePortal with the um superuser and select the <customer>
			//Go to "TMS >> Snapshot >> Reports >> Available Reports" sub-tab
			logger.info("Step 7,8:");
			vCreatedRptGroup(testDataOR.get("customer"),rptGroup,rptShrtName);

			logger.info("SMR111 executed successfully");	
		}
		catch (Throwable t) {
			
			handleException(t);
		}
	}
	
}

