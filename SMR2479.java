package com.ingenico.testsuite.tmsmanagement;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/assettracking/SMR2479.java $
$Id: SMR2479.java 17894 2016-04-06 10:00:04Z haripraks $
 */
import org.testng.annotations.Test;

/**
 * SMR-2479:Asset Tracking: Terminal Creation
 * @author Hariprasad.KS
 *
 */
public class SMR2479 extends SuiteTmsManagement{

	/**
	 * Asset Tracking: Terminal Creation
	 */
	@Test(groups="SMR2479")
	public void smr2479() {
		try{

			eportalCust=testDataOR.get("customer");
			final String multiuser=testDataOR.get("multi_user_login"),firstName=testDataOR.get("multi_first_name"),lastName=testDataOR.get("multi_last_name"),
					serialno=testDataOR.get("at_serial_number"),partno=testDataOR.get("at_part_number");
			logger.info("SMR2479 execution started");
			//Access ePortal with multi_user_login and select customer
			logger.info("Step 1:");
			login("URLEportal",multiuser,firstName,lastName);

			//Go to "TMS" tab,and click on Asset tracking Terminals page
			logger.info("Step 2,3:");
			navigateToSubPage(ASSETTKING, selUtils.getCommonObject("tms_tab_xpath"), selUtils.getCommonObject("tms_assttrack_xpath"));
			selUtils.switchToFrame();
			waitMethods.waitForWebElementPresent(selUtils.getCommonObject("tampagexhtml_xpath"));
			selUtils.clickOnWebElement(selUtils.getCommonObject("terminals_link"));
			logger.info("Clicked on terminals link");
			
			//Click on the "Add terminals" button and enter serial and part number
			logger.info("Step 4:");
			waitMethods.waitForWebElementPresent(selUtils.getCommonObject("tableloadimg_xpath"));
			waitMethods.waitForElementNotPresent("tableloadimg_xpath");
			selUtils.clickOnWebElement(selUtils.getObject("addterminal_xpath"));
			waitMethods.waitForWebElementPresent(selUtils.getObject("atterminalfield_id"));
			waitMethods.waitForWebElementPresent(selUtils.getObject("atsrno_id"));
			//selUtils.getObject("atsrno_id").click();
			selUtils.getObject("atsrno_id").sendKeys(serialno);
			selUtils.populateInputBox("atpartno_id", partno);
			
			cnfmPopupActMsg("terminalconfirm_id","successmsg_id", ATTRMSUCMSG);
			selUtils.clickOnWebElement(selUtils.getObject("atclose_id"));
			
			//verify Add terminals is created successfully in terminals page
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			selUtils.verifyBreadCrumb(ASSETTKING);
			selUtils.verifyBreadCrumb(TERMINALS);
			vMultiTableValue("atbody_xpath",serialno, partno);
			
			//Click on logout of multiuser
			logger.info("Step 5:");
			logger.info("multiuser logout is taken care at the suite level as part of the SSO");
			
			logger.info("SMR2479 Executed Successfully");	
		}
		catch (Throwable t) {
			handleException(t);
		}
	}
}

