package com.ingenico.testsuite.usermanagement;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/usermanagement/SMR11.java $
$Id: SMR11.java 16710 2016-01-20 10:02:08Z rjadhav $
 */

import org.testng.annotations.Test;

/**
 * SMR-11:Create multi-view Users Profile
 * @author Joel.Samuel
 */
public class SMR11 extends SuiteUserManagement{
	
	/**
	 * Create multi-view Users Profile
	 * @param browser	 
	*/
	 
	@Test(groups="SMR11")
	public void smr11() {

		try{
			
			eportalCust=MULTIVIEW;
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEportal",testDataOR.get("superuser"),firstName,lastName);
			
			logger.info("SMR11 execution started");
			final String intExtYamlVal[]={testDataOR.get("internal_user_management"),testDataOR.get("external_user_management"),testDataOR.get("internal_provisioning_tasks")};
			final String prof=testDataOR.get("multi_profile_name");
			
			
			//Access ePortal with the um superuser and select Multiview customer
			//click the "Profiles" button in the "User Management" page
			logger.info("Step 1, 2:");
			selUtils.clickOnWebElement(selUtils.getCommonObject("usermangement_link"));
			logger.info("Clicked on the User Management button" );
			selUtils.switchToFrame();
			selUtils.clickOnWebElement(selUtils.getObject("pofilestab_link"));

			//Click the "Add Profile" button
			logger.info("Step 3:");
			selUtils.clickOnWebElement(selUtils.getObject("addprofbttn_id"));
			createProfile(intExtYamlVal);
			logger.info("Profile created successfully.");

			colIndex=selUtils.getIndexForColHeader("ep_profls_colheaderlst_css",NAMECOL);
			verifyLvlColLvlValPresence("ep_profls_vals_css",colIndex,prof);
			driver.switchTo().defaultContent();	
			
			if(dbCheck){
				sqlQuery = "SELECT is_user_profile,userid FROM  um.user_properties WHERE is_user_profile='t' AND userid="+"\'"+prof+"\'";
				vProfInDataBase(prof,sqlQuery);
			}

			//click the "User Management" button,Add user" button,select 
			//"Use existing profile" dropdown list from the "Add user" form
			logger.info("Step 4,5:");
			vUseExstsProLists(prof,"adduser_xpath");
			
			logger.info("SMR11 executed successfully");	
		}
		catch (Throwable t) {
			handleException(t);
		}
	}
}

