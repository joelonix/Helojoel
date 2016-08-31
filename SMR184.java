package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR184.java $
$Id: SMR184.java 17658 2016-03-22 09:47:44Z rkahreddyga$
 */

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * SMR-184:Remove Profile
 * @author Raghunath.K
 *
 */

public class SMR184 extends SuiteCardPayment {

	/**
	 * Remove Profile
	 * @param browser
	 * 
	 */
	@Test(groups={"SMR184"})
	public void smr184() {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name"),
			custName=testDataOR.get("customer"),profileAdmREmv=testDataOR.get("profile").replaceAll(".zip", "").trim();
			
			logger.info("SMR184 execution started");
			//Access Everest with the Everest superuser
			logger.info("Step 1");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
		    logger.info("Accessed with everest superuser");
			//Go to Card Payment then Profile submenu
		    //Click on edit button of profile assignment
		   //Click on delete button of profile
			logger.info("Step 2, Step 3 and Step 4");
			
			if(profileAdmREmv.contains("AQAADM") || profileAdmREmv.contains("AQAEMV"))
			{
				editProAsigment(profileAdmREmv, custName);
				delProAsigment(profileAdmREmv);
			}
			else
			Assert.fail("Invalid profile has passed");
			
			//Verify profile should be removed from the list
			waitMethods.waitForWebElementPresent(selUtils.getCommonObject("cardpaymt_tab_xpath"));
			navToSubPage("cardpaymt_tab_xpath", "cardpayprofile_xpath", PROF);
			colIndex=selUtils.getIndexForColHeader("profcollst_css",PROFLENAME);
			vrfyColValNotPresence("entitytablelst_css", colIndex, profileAdmREmv);
			
			logger.info("SMR184 executed successfully");

		}catch (Throwable t) {
			handleException(t);
		}


	}

}

