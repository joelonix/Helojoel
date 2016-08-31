package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/prov/testsuite/cardpayment/E2E52.java $
$Id: E2E52.java 12373 2015-02-12 10:55:43Z nguttula $
 */
import java.io.IOException;

import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * E2E-1052:Upgrade profile
 * @author Nagaveni.Guttula
 *
 */
public class SMR1052  extends SuiteCardPayment{

	/**
	 * Upgrade profile
	 */
	@Test()
	public void e2e1052() {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("E2E1052 Execution Started");	

			//Access Everest with the Everest superuser
			String upgradedProfile=testDataOR.get("profile_upgrade"),custName=testDataOR.get("customer");
			String version=testDataOR.get("profile_version");
			xpath="";
			logger.info("Step 1:");

			
			//Go to "Card Payment >> Profile" submenu
			logger.info("Step 2");
			navigateToSubPage(PROF,selUtils.getCommonObject("cardpaymt_tab_xpath"),selUtils.getCommonObject("cardpayprofile_xpath"));

			
			//Click on the "+" button and assign profile to the customer
			//click on the "OK" button
			vExistProfileVersion(upgradedProfile,version);
			addUpgradedProf(upgradedProfile,custName);


			//Navigate to the "Card Payment >> Customer Provisioning"
			//select the customer <customer>
			logger.info("Step 4:");
			vProfileExistsToCust(upgradedProfile);
			xpath=getPath("profvers_incustconfig_xpath").replace("PROFILENAME",upgradedProfile);
			selUtils.verifyObjDirectDisp(xpath, "VERSION", version);
			logger.info("E2E1052 Executed successfully"); 	

		}
		catch (Throwable t) {
			handleException(t);
		}
	}

	/**
	 * This method is used for adding the profile to the customer and validating
	 * whether profile was assigned to the customer or not
	 * @throws IOException 
	 * @author Nagaveni.Guttula
	 * @throws InterruptedException 
	 * 
	 */
	private void addUpgradedProf(String appProfileName,String custName) throws IOException{
		selUtils.clickOnWebElement(selUtils.getCommonObject("plusbtn_xpath"));
		addProfToCust(PROFILEZIP, appProfileName, custName);
		if(getModWinDisp(selUtils.getCommonObject("updatprof_wintitl_xpath"), UPDATEPROFILE)){
		((JavascriptExecutor) driver).executeScript("document.getElementById('saveProfilesForm').click();", selUtils.getObject("updateprofilyes_id"));
			logger.info("Clicked on the YES button");
			waitNSec(2);
		}
		logger.info("Added "+appProfileName+"profile to the Customer");
		
		
	}
	/**
	 * Method for verifying the uniqueness of the profile version 
	 * @param profileName
	 * @param version
	 */
	private void vExistProfileVersion(String profileName,String versionNum){
		xpath="";
		profileTxt=profileName.replaceAll(".zip", "");
		
		if(!selUtils.isElementPresentCommon("noresulttxt_xpath")){
		xpath=getPath("profver_inproflist_xpath").replace("PROFILENAME",profileTxt);
		xpath=xpath.replace("VERSION",versionNum);
		if(selUtils.isElementPresentxpath(xpath)){
			Assert.fail("Existing profile version is displayed");
		}
		logger.info("Verified the uniqueness of the profile version number");
		}
	}
}












