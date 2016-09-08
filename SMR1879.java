package com.ingenico.testsuite.usermanagement;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/usermanagement/SMR1879.java $
$Id: SMR1879.java 17921 2016-04-07 09:42:20Z rkahreddyga $
 */


import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.base.SelUtils;
import com.ingenico.common.CommonConstants;

/**
 * SMR-1879:Add a multi user
 * @author Joel.Samuel
 *
 */
public class SMR1879 extends SuiteUserManagement{

	/**
	 * Add a multi user
	 * @param browser
	 */
	@Test(groups="SMR1879")
	public void smr1879() {

		try{
			eportalCust=MULTIVIEW;

			String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEportal",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1879 execution started");
			final String intExtYamlVal[]={testDataOR.get("internal_user_management"),
					testDataOR.get("external_user_management"),
					testDataOR.get("internal_provisioning_tasks")},
					evntsYamlVal[]={testDataOR.get("reception"),testDataOR.get("entry_and_issue"),
					testDataOR.get("deployment"),testDataOR.get("maintenance"),testDataOR.get("inventory"),
					testDataOR.get("inspection"),testDataOR.get("decommissioning")};
			final String multiuser=testDataOR.get("multi_user_login"),terminalRole=testDataOR.get("terminal_role");

			//Access ePortal with the um superuser and select Multiview customer
			//click the "User Management" button
			logger.info("Step 1, 2:");
			selUtils.clickOnWebElement(selUtils.getCommonObject("usermangement_link"));
			logger.info("Clicked on User management tab");

			//Click the "Add user" button
			logger.info("Step 3:");
			selUtils.switchToFrame();
			selUtils.clickOnWebElement(selUtils.getObject("adduser_link"));
			addMultiUser(intExtYamlVal,evntsYamlVal,terminalRole);
			if(waitMethods.isElementPresent("ermsg_xpath")){
				Assert.fail("User creation is unsuccessful, please enter correct data");
			}
		//	driver.switchTo().defaultContent();
			
			if(dbCheck){
				sqlQuery = "SELECT userid FROM  um.user_properties WHERE userid="+"\'"+multiuser+"\'";
				resSet = dbMethods.getDataBaseVal(testDataOR.get("databaseUM"),sqlQuery,CommonConstants.ONEMIN);
				Assert.assertTrue(resSet.getString("userid").equalsIgnoreCase(multiuser), multiuser+ " does not exist in the database");
				logger.info(multiuser+ " exists in the database");
			}

			//Change to 'ingenico' through LDAP 
			//the password of user multi_user_login
			logger.info("Step 4:");
			ldap(testDataOR.get("multi_user_login"));
			logger.info("multi_user_login password is changed to ingenico through LDAP");

			//Access ePortal with the multi user
			//And try to select the Multiview page
			logger.info("Step 5:");
			logout();
			logger.info("Logout successfully");
			waitMethods.waitForWebElementPresent(selUtils.getCommonObject("eportalusr_name"));
			firstName=testDataOR.get("multi_first_name");lastName=testDataOR.get("multi_last_name");
			login("URLEportal","eportalusr_name","eportalpswd_name",testDataOR.get("multi_user_login"),"user_password","newversnloginbttn_xpath",firstName,lastName);
			logger.info("Accessed eportal with multi_user_login");
			//waitMethods.waitForWebElementPresent
			Assert.assertTrue(SelUtils.getSelectedItem(selUtils.getCommonObject("client_id")).equals(MULTIVIEW), "Drop down field is not set to multiview.");
			logger.info("Drop down field is set to Multiview.");

			//Access ePortal with the multi user
			//And select a Customer from customers
			logger.info("Step 6:");
			String custNames=testDataOR.get("customers");
			String[] cust=custNames.split(",");
			selUtils.selectItem(selUtils.getCommonObject("client_id"), cust[0]);

			selUtils.clickOnWebElement(selUtils.getCommonObject("confirm_id"));
			assertEquals(SelUtils.getSelectedItem(selUtils.getCommonObject("client_id")),cust[0]);
			logger.info("SMR1879 Executed Successfully");	
		}
		catch (Throwable t) {
			handleException(t);
		}
	}

}

