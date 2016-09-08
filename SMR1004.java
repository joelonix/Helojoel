package com.ingenico.testsuite.tmsmanagement;

/*
 $HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/tmsmanagement/SMR1004.java $
 $Id: SMR1004.java 17538 2016-03-11 11:31:27Z rkahreddyga $
 */

import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.base.SelUtils;

/**
 * SMR-1004:Add a report to a group
 * 
 * @author Nagaveni.Guttula
 *
 */
public class SMR1004 extends SuiteTmsManagement {

	/**
	 * Add a report to a group .
	 * 
	 * @param browser
	 */
	@Test(groups = "SMR1004")
	public void smr1004() {

		try {
			eportalCust = testDataOR.get("customer");
			final String firstName = testDataOR.get("superuser_first_name"), lastName = testDataOR.get("superuser_last_name"),strErrorMsg;
			login("URLEverest", testDataOR.get("superuser"), firstName,lastName);
			String reportGrupName = testDataOR.get("report_group"), genModeVal, customer = testDataOR.get("customer"),rptShrtName2=testDataOR.get("report_short_name_2");
			logger.info("SMR1004 execution started");

			// Access Everest with a superuser
			// Go to "TMS >> Reports" submenu
			logger.info("Step 1, 2:");
			navigateToSubPage(TMSREPORTS,selUtils.getCommonObject("tms_tab_xpath"),selUtils.getCommonObject("tms_reports_xpath"));

			// Click the edit button of report_group in the "Tms Reports" list
			logger.info("Step 3:");
			clkOnEditReport(reportGrupName);

			// Add report_group and report_short_name
			logger.info("Step 4");
			genModeVal = SelUtils.getSelectedItem(selUtils.getObject("sel_genmode_id"));
			selUtils.clickOnWebElement(selUtils.getObject("addrpt_plusbttn_xpath"));
			editReportVals(genModeVal);
			logger.info("Report short name2 "+ rptShrtName2 + " added");
			colIndex = selUtils.getIndexForColHeader("tmrptsheader_xpath",SHRTNAMECOL);
			verifyLvlColLvlValPresence("rptstablst_css", colIndex,rptShrtName2);

			// Click the "Save" button ,The report is successfully added
			logger.info("Step 5:");
			//selUtils.clickOnWebElement(selUtils.getObject("rept_savebbtn_xpath"));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObject("rept_savebbtn_xpath"));
			
			// customer=getObject("assownrs_id").getText();
			// if(selUtils.getCommonObject("errorreport_id").getAttribute("class").endsWith("errorMessage"))
			// {
			// Assert.fail("Report was not created due to error");
			// logger.info("Report was not created due to error");
			// }
			
			// verify error messages
			if (selUtils.getCommonObject("errorreport_id").isDisplayed()) {
				strErrorMsg = selUtils.getCommonObject("errorreport_id").getText().trim();
				Assert.fail(strErrorMsg + " due to fail");
			}
			logger.info("Report name " + reportGrupName+ " is successfully updated");

			// Access ePortal with the um superuser and select the <customer>
			// Go to "TMS >> Snapshot >> Reports >> Available Reports" sub-tab
			logger.info("Step 6,7:");
			vCreatedRptGroup(customer,reportGrupName,rptShrtName2);

			logger.info("SMR1004 executed successfully");
		} catch (Throwable t) {
			handleException(t);
		}
	}

}
