package com.ingenico.testsuite.customermanagement;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/customermanagement/SMR1379.java $
$Id: SMR1379.java 16708 2016-01-20 09:53:29Z rjadhav $
 */
import org.testng.annotations.Test;

/**
 * SMR-1379:Activate Asset Tracking after deployment
 * The access to the ePortal modules have been initialized (as described in test
 * SMR-1872) with  disabled Asset Tracking flag (asset_tracking not set)
 * @author Hariprasad.KS
 */
public class SMR1379 extends SuiteCustomerManagement{

	/**
	 * Activate Asset Tracking after deployment
	 */
	@Test(groups="SMR1379")
	public void smr1379(){
		try {
			eportalCust=testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1379 execution started");
			//final String customerName=testDataOR.get("customer");

			//Access Everest with a superuser and select customer in cust module 
			logger.info("Step 1,2 :");
			clkCustNameList(eportalCust);

			//Go to the "ePortal" module.
			logger.info("Step 3");
			selUtils.clickOnWebElement(selUtils.getCommonObject("eportal_tab_link"));
			logger.info("Navigated to eportal module");

			//Check the "Asset Tracking" box and validate message then logoff 
			logger.info("Step 4");
			selUtils.vNclkChkOrRadio(selUtils.getCommonObject("asstrking_ckbx_id"));
			selUtils.getCommonObject("savebttn_xpath").click();
			logger.info("Clicked on save button");
			selUtils.verifyTextContains(selUtils.getCommonObject("posheder_errmsg_id"), SUCCSMSG);

			//Access ePortal with a superuser and select the customer
			logger.info("Step 5,6");
			logoutEpSelCust(eportalCust);

			//Go to Asset tracking tab,should be able to access 
			logger.info("Step 7");
			navigateToSubPage(ASSETTKING, selUtils.getCommonObject("tms_tab_xpath"), selUtils.getCommonObject("tms_assttrack_xpath"));
			selUtils.switchToFrame();
			waitMethods.waitForWebElementPresent(selUtils.getCommonObject("bread_crum_id"));
			selUtils.verifyTextContains(selUtils.getCommonObject("bread_crum_id"), ASSETTKING);
			//driver.switchTo().defaultContent();

			logger.info("SMR1379 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}

	}
}
