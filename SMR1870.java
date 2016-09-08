package com.ingenico.testsuite.usermanagement;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/usermanagement/SMR1870.java $
$Id: SMR1870.java 18197 2016-04-22 10:02:44Z rkahreddyga $
 */
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
/**
 *SMR-1870:Deactivate/Reactivate a UM user
 *@author Raghunath.K
 *
 */
public class SMR1870 extends SuiteUserManagement{

	/**
	 * SMR-1870:Deactivate/Reactivate a UM user
	 */
	@Test(groups="SMR1870")
	public void smr1870() {

		try{
			eportalCust=testDataOR.get("customer");
			String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			logger.info("SMR1870 execution started");
			logger.info("Step 1:");
			login("URLEportal",testDataOR.get("superuser"),firstName,lastName);			
			final String [] tabloc={"dashboard_tab_xpath","gprs_tab_xpath"};
			final String monoUser=testDataOR.get("mono_user_login"),terminalRole=testDataOR.get("terminal_role");
			
			
			logger.info("Step 2:");
			//Access ePortal with the um superuser and select customer
			//click the "User Management" button then activated users tab
			selUtils.clickOnWebElement(selUtils.getCommonObject("usermangement_link"));
			logger.info("Clicked on User management tab");
			selUtils.switchToFrame();
			selUtils.jScriptClick(selUtils.getObject("activateduser_id"));
			logger.info("Clicked on ActivatedUsers tab");

			// Click on the arrow-column of the mono_user_login line.
			//Click on the "Deactivate user" button of the dropdown list.
			logger.info("Step 3:");
			dactNactUser(monoUser,"deactbutton_id",DACTVATE);
			
			//Validate user shall not be listed under Activated Users tab
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			waitNSec(2);
			//waitMethods.waitForelementNotdisplayed(selUtils.getObject("progressZone_id"));
			xpath=getPath("monoactdeact_xpath").replace(MONOUSER, monoUser);
			Assert.assertFalse(selUtils.isElementPresentxpath(xpath),"Failed due to "+monoUser+" is present under Activated users");
			logger.info(monoUser+" has not displayed under "+ACTIVATE+" user tab");	
			
			//Validate user shall  be listed under deactivated Users tab
			selUtils.jScriptClick(selUtils.getObject("deactivateduser_id"));
			logger.info("Clicked on deactivate subtab");
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			Assert.assertTrue(selUtils.isElementPresentxpath(xpath),"Failed due to "+monoUser+" is not present under deactivated users");
			logger.info(monoUser+" has displayed under "+DACTVATE+" user tab");	
			
			//Logout from ePortal
			logger.info("Step 4 and Step 5:");
			eportalLogoutAndLogin(monoUser);
			Assert.assertTrue(selUtils.getObject("monologinerr_id").getText().equalsIgnoreCase(INVALIDAUTH),"Failed due to "+monoUser+" login sucessfully");
			logger.info(INVALIDAUTH+" is verified sucessfully");
			
			//Access ePortal with a superuser and select <customer>
			logger.info("Step 6:");
			homePageLogin("eportalusr_name","eportalpswd_name", testDataOR.get("superuser"),"superuser_password","newversnloginbttn_xpath");
			logger.info("Superuser login is successfull");
			//Access ePortal with a superuser and select <customer>
			logger.info("Step 7:");
			selUtils.clickOnWebElement(selUtils.getCommonObject("usermangement_link"));
			logger.info("Clicked on User management tab");
			selUtils.switchToFrame();
			selUtils.jScriptClick(selUtils.getObject("deactivateduser_id"));
			logger.info("Clicked on Deactivated Users tab");
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			
			// Click on the arrow-column of the mono_user_login line .
			// Click on the "Activate user" button 
			logger.info("Step 8:");
			dactNactUser(monoUser,"actuserbutton_id",ACTIVATE);
			
			//Validate user shall not be listed under Activated Users tab
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			waitNSec(2);
			//waitMethods.waitForelementNotdisplayed(selUtils.getObject("progressZone_id"));
			xpath=getPath("monoactdeact_xpath").replace(MONOUSER, monoUser);
			Assert.assertFalse(selUtils.isElementPresentxpath(xpath),"Failed due to "+monoUser+" is present under deactivated user users");
			logger.info(monoUser+" has not displayed under "+DACTVATE+" user tab");		
			
			//Validate user shall  be listed under deactivated Users tab
			selUtils.jScriptClick(selUtils.getObject("activateduser_id"));
			logger.info("Clicked on activate subtab");
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			Assert.assertTrue(selUtils.isElementPresentxpath(xpath),"Failed due to "+monoUser+" is not present under activated users");
			logger.info(monoUser+" has displayed under "+ACTIVATE+" user tab");		
			
			
			//Logout from ePortal
			logger.info("Step 9:");
			logout();	
			//added refresh to handle the cache issue
			driver.navigate().refresh();
    		//Access ePortal with the user mono_user_login
			logger.info("Step 10:");
			firstName=testDataOR.get("mono_first_name");lastName=testDataOR.get("mono_last_name");
			waitMethods.waitForWebElementPresent(selUtils.getCommonObject("newversnloginbttn_xpath"));
			monoUsrLoginTbs(monoUser,firstName,lastName,tabloc,terminalRole);
			
			//Logout mono user from ePortal
			//Logout is handeled in SSO
			logger.info("Step 11:");
			logger.info("Mono user logout is taken care at the suite level as part of the SSO");
			logger.info("SMR1870 executed successfully");	
		}
		catch (Throwable t) {
			handleException(t);
		}
	}
	
	/**
	 * Deactivate and activate monouser
	*/
	
	private void dactNactUser(String monouser,String locator,String userAction)
	{
		selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
		//waitMethods.waitForelementNotdisplayed(selUtils.getObject("progressZone_id"));
		waitNSec(4);
		xpath=getPath("monoarrowdown_xpath").replace(MONOUSER,monouser);
		selUtils.isElementPresentxpath(xpath);
		selUtils.clickOnWebElement(selUtils.getObjectDirect(By.xpath(xpath)));
		logger.info("Clicked on arrow-column button");
		selUtils.jScriptClick(selUtils.getObject(locator));
		//selUtils.clickOnWebElement(selUtils.getObject(locator));
		logger.info("Clicked on "+userAction+" user");
		selUtils.clickOnWebElement(selUtils.getObject("deactvteuserok_xpath"));	
		logger.info("Clicked on "+userAction+" selected user ok button");
	}
	
	
	
}