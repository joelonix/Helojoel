package com.ingenico.testsuite.usermanagement;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/usermanagement/SMR1881.java $
$Id: SMR1881.java 18071 2016-04-14 09:28:56Z rkahreddyga $
 */

import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;
/**
 * SMR-1881: Add a mono user
 * Preconditions
 * A customer customer exists and should 
 * have subscribed to 'Card Payment', 
 * 'e-Payment', 'GPRS' and 'IngeEstate' modules.
 * @author Hariprasad.KS
 *
 */
public class SMR1881 extends SuiteUserManagement{

	/**
	 * Add a mono user
	 */
	@Test(groups="SMR1881")
	public void smr1881() {

		try{
			eportalCust=testDataOR.get("customer");
			String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEportal",testDataOR.get("superuser"),firstName,lastName);

			logger.info("SMR1881 execution started");
			final String [] reportingChkBxs={"transjournal_id","dashbordmodule_id","advancedrights_id"},
			tabloc={"dashboard_tab_xpath","gprs_tab_xpath"},evntsYamlVal={testDataOR.get("reception"),
			testDataOR.get("entry_and_issue"),testDataOR.get("deployment"),testDataOR.get("maintenance"),
			testDataOR.get("inventory"),testDataOR.get("inspection"),testDataOR.get("decommissioning")};
			final String monoUser=testDataOR.get("mono_user_login"),terminalRole=testDataOR.get("terminal_role");;

			//Access ePortal with the um superuser and select customer
			//click the "User Management" button
			logger.info("Step 1, 2:");
			selUtils.clickOnWebElement(selUtils.getCommonObject("usermangement_link"));
			logger.info("Clicked on User management tab");

			//Click the "Add user" button and then fill fields and save
			logger.info("Step 3:");
			selUtils.switchToFrame();
			selUtils.clickOnWebElement(selUtils.getObject("addprofbttn_xpath"));
			addMonoUser(reportingChkBxs,evntsYamlVal,monoUser,terminalRole);
			Assert.assertTrue(selUtils.getObject("usr_mgmt_succmsg_id").getText().contains(SUCCCREATED),"Failed while validating "+SUCCCREATED+"success message");
			logger.info("Verified the success message");
			if(waitMethods.isElementPresent("ermsg_xpath")){
				Assert.fail("User creation is unsuccessful, please enter correct data");
			}
			//driver.switchTo().defaultContent();
			
			if(dbCheck){
				//Data base check
				sqlQuery = "SELECT userid FROM  um.user_properties WHERE userid="+"\'"+monoUser+"\'";
				resSet = dbMethods.getDataBaseVal(testDataOR.get("databaseUM"),sqlQuery,CommonConstants.ONEMIN);
				Assert.assertTrue(resSet.getString("userid").equalsIgnoreCase(monoUser), monoUser+ "does not exist in the database");
				logger.info(monoUser+ "exist in the database");
			}

			//Change to 'ingenico' through LDAP psw of mono user
			logger.info("Step 4:");
			ldap(monoUser);
			logger.info("mono_user_login password is changed to ingenico through LDAP");
			logout();
			logger.info("Logout successfully");
			waitMethods.waitForWebElementPresent(selUtils.getCommonObject("eportalusr_name"));

			//Access ePortal with the user mono_user_login
			logger.info("Step 5:");
			firstName=testDataOR.get("mono_first_name");lastName=testDataOR.get("mono_last_name");
			monoUsrLoginTbs(monoUser,firstName,lastName,tabloc,terminalRole);
			logger.info("SMR1881 executed successfully");	
		}
		catch (Throwable t) {
			handleException(t);
		}
	}

	/**
	 * Add mono User
	 * Updated with scroll down function to avoid failure
	 * @author Hariprasad.KS
	 */
	private void addMonoUser(String[] reportingChkBxs,String[] evntsYamlVal,String monouser,String terminalRole){
		clkOnModRtsPlsBttn("expandglobal_xpath","global_plsbttn_xpath");
		waitMethods.waitForWebElementPresent(selUtils.getObject("profname_id"));
		selUtils.populateInputBox("profname_id", monouser);
		selUtils.populateInputBox("usrfrstname_id", testDataOR.get("mono_first_name"));
		selUtils.populateInputBox("usrlstname_id", testDataOR.get("mono_last_name"));
		selectDefLan();
		selUtils.selectItem(selUtils.getObject("existingprof_id"), testDataOR.get("mono_user_profile"));
		if(testDataOR.get("mono_user_profile").equals(NONE)){
			clkOnModRtsPlsBttn("userlvl_xpath","userlvl_plusbttn_xpath");
			selectLevel(testDataOR.get("level"));
			clkOnModRtsPlsBttn("usr_mgmt_rits_xpath","usr_mgmt_plsbttn_xpath");
			selUtils.slctChkBoxOrRadio(selUtils.getObject("accessusermgmt_id"));
			clkOnModRtsPlsBttn("expand_pmtrights_xpath","pmtrights_plsbttn_xpath");
			selUtils.slctChkBoxOrRadio(selUtils.getObject("dashbrdckbx_id"));
			clkOnModRtsPlsBttn("expand_ftprghts_xpath","ftprghts_pls_bttn_xpath");
			selUtils.slctChkBoxOrRadio(selUtils.getObject("ftp_rdwrt_radio_id"));
			clkOnModRtsPlsBttn("expandtms_xpath","tms_plusbttn_xpath");
		//	selUtils.slctChkBoxOrRadio(selUtils.getObject("ftp_rdwrt_radio_id"));
			//if(!"None".equalsIgnoreCase(testDataOR.get("terminal_role"))){
				selcRoles("terminalrole_id","terminalroles_radiobttns_xpath",terminalRole);
				if("None".equalsIgnoreCase(terminalRole)||"Terminal Viewer".equalsIgnoreCase(terminalRole)){
					logger.info("Terminal role is set to "+terminalRole);
				}
				else{
					selChkOrRadiobttn(testDataOR.get("access_to_sensitive_parameters"),"sensitivedata_id");
				}
		//	}
			selTmsAsstNEvntsRole(evntsYamlVal,profChkBxs);
			clkOnModRtsPlsBttn("expand_gprsrts_xpath","gprsrts_plsbttn_xpath");
			selUtils.slctChkBoxOrRadio(selUtils.getObject("advancedrights_id"));
			selUtils.slctChkBoxOrRadio(selUtils.getObject("dashbrd_gprsckbx_id"));
		}
		//selUtils.clickOnWebElement(selUtils.getCommonObject("save_link"));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getCommonObject("save_link"));
	}
	
}