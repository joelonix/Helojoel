package com.ingenico.testsuite.gprs;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR1031.java $
$Id: SMR1031.java 16708 2016-01-20 09:53:29Z rjadhav $
 */

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * SMR-1031:Rate plan
 * @author joel.samuel
 *
 */
public class SMR1031 extends SuiteGprs{

	/**
	 * Rate plan
	 */
	@Test(groups="SMR1031")
	public void smr1031(){
		try {
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1031 execution started");
			final String ratePlan1=testDataOR.get("rate_plan_1"),ratePlan2=testDataOR.get("rate_plan_2"),custName=testDataOR.get("customer");
			
			//Access Everest with a superuser
			//Go to "GPRS Management"
			logger.info("Step 1, 2 :");
			navigateToSubPage(GPRSMNGMNT,selUtils.getCommonObject("gprs_tab_xpath"),selUtils.getCommonObject("gprsmngmtsubpage_xpath"));
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), custName);

			//Add <rate_plan_1> from the "Value" drop list and verify
			logger.info("Step 3 :");
			selUtils.clickOnWebElement(selUtils.getObject("addrateplanplusbttn_xpath"));
			vAddEditRates("rateplansmodwin_xpath","ratepln_selc_xpath","add_ratepln_bttn_id",ratePlan1);
			
			//Edit rate plan
			logger.info("Step 4 :");
			clkOnDirectObj("edit_rate_planbttn_xpath","NAME",ratePlan1);
			logger.info("Clicked on the edit rate plan button");
			vAddEditRates("editrat_pln_modwin_xpath","ratepln_edit_xpath","edit_ratpln_bttn_id",ratePlan2);
			
			//Add rate plan1
			logger.info("Step 5 :");
			selUtils.clickOnWebElement(selUtils.getObject("addrateplanplusbttn_xpath"));
			logger.info("Clicked on the add rate plan button");
			vAddEditRates("rateplansmodwin_xpath","ratepln_selc_xpath","add_ratepln_bttn_id",ratePlan1);
			
			//Delete Rate plan 2
			logger.info("Step 6 :");
			deleteExistsData("delete_ratepln_xpath", ratePlan2);
			xpath=getPath("ratepln_txt_xpath").replace("NAME",ratePlan2);
            Assert.assertFalse(selUtils.isElementPresentxpath(xpath),"Web element is still exist not deleted properly");
            selUtils.clickOnWebElement(selUtils.getObject("apply_bttn_id"));
            
			logger.info("SMR1031 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}

	}
	
}