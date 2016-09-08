package com.ingenico.testsuite.usermanagement;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/usermanagement/SMR12.java $
$Id: SMR12.java 17296 2016-02-29 04:50:05Z haripraks $
 */

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.ingenico.base.SelUtils;
/**
 * SMR-12: Create mono-view Users Profile
 * @author Nagaveni.Guttula
 *
 */
public class SMR12 extends SuiteUserManagement{

	/**
	 * Create mono-view Users Profile
	 */
	@Test(groups="SMR12")
	public void smr12() {

		try{
			
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			
			//Testing
//			eportalCust=MULTIVIEW;
//			final String prof=testDataOR.get("multi_profile_name");
//			login("URLEportal",testDataOR.get("superuser"),firstName,lastName);
//			vUseExstsProLists(prof,"adduser_xpath",true);
			
			//Testing
			
			
			
			eportalCust=testDataOR.get("customer");
			login("URLEportal",testDataOR.get("superuser"),firstName,lastName);

			logger.info("SMR12 execution started");
			final String profileName=testDataOR.get("mono_profile_name"),customerName=testDataOR.get("customer");

			
			//Access ePortal with the um superuser and select customer		
			logger.info("Step 1:");
			assertEquals(SelUtils.getSelectedItem(selUtils.getCommonObject("client_id")),customerName);
			logger.info("Verified the user has access to the mono customer view ");
			
			//Click the "User Management" button			
			logger.info("Step 2:");
			selUtils.clickOnWebElement(selUtils.getCommonObject("usermangement_link"));
			logger.info("Clicked on the User Management button" );
			
			//Click the "Profiles" button in the "User Management" page		
			logger.info("Step 3:");
			selUtils.switchToFrame();
			selUtils.clickOnWebElement(selUtils.getObject("profiletab_id"));
			logger.info("Clicked on the Profiles button");
			
			//Click the "Add Profile" button and create a profile
			logger.info("Step 4:");
			selUtils.clickOnWebElement(selUtils.getObject("addprofbttn_xpath"));
			logger.info("Clicked on the Add Profile button");
			waitMethods.waitForWebElementPresent(selUtils.getObject("global_plsbttn_xpath"));
			
			clkOnModRtsPlsBttn("expandglobal_xpath","global_plsbttn_xpath");
			waitMethods.waitForWebElementPresent(selUtils.getObject("profname_id"));
			selUtils.populateInputBox("profname_id", profileName);
			//selectDefLan();
			selUtils.slctChkBoxOrRadio(selUtils.getObject("chkallrits_chkbox_xpath"));
			waitNSec(2);
			clkOnModRtsPlsBttn("userlvl_xpath","userlvl_plusbttn_xpath");
			selectLevel(testDataOR.get("level"));
			clkOnModRtsPlsBttn("expandtms_xpath","tms_plusbttn_xpath");
			selUtils.slctChkBoxOrRadio(selUtils.getObject("terminalrole_id"));
			xpath=getPath("terminalroles_radiobttns_xpath").replace("NAME", testDataOR.get("mono_prof_terminal_role"));
			selUtils.slctChkBoxOrRadio(selUtils.getObjectDirect(By.xpath(xpath)));
			selUtils.clickOnWebElement(selUtils.getCommonObject("prof_savebttn_xpath"));
			waitMethods.waitForWebElementPresent(selUtils.getObject("activateduser_id"));
			
			//verify the added profilename in profile list
			//waitMethods.waitForWebElementPresent
			colIndex=selUtils.getIndexForColHeader("ep_profls_colheaderlst_css",NAMECOL);
			verifyLvlColLvlValPresence("ep_profls_vals_css",colIndex,profileName);
			logger.info("Verified the profile name as "+profileName+" is displayed in the profile list");
			driver.switchTo().defaultContent();	
			
			if(dbCheck){
				//Database validation			
				sqlQuery = "SELECT is_user_profile,userid FROM  um.user_properties WHERE is_user_profile='t' AND userid LIKE "+"\'%"+profileName+"%\'";
				vProfInDataBase(profileName,sqlQuery);
			}
			
			//click the "User Management" button,Add user" button,select 
			//"Use existing profile" dropdown list from the "Add user" form
			logger.info("Step 5,6:");
			vUseExstsProLists(profileName,"adduserbttn_xpath");
			logger.info("SMR12 executed successfully");	
		}
		catch (Throwable t) {
			handleException(t);
		}
	}
	

}



