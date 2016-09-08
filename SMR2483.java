package com.ingenico.testsuite.tmsmanagement;

/*
 $HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/tmsmanagement/SMR2483.java $
 $Id: SMR2483.java 18069 2016-04-14 09:19:04Z jsamuel $
 */


import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * SMR-2483:Asset Tracking: Terminal Management Functions
 * @author Joel F
 */
public class SMR2483 extends SuiteTmsManagement {

	/**
	 * Terminal Management Functions
	 * 
	 */
	@Test(groups = "SMR2483")
	public void smr2483() {

		try 
		{
			// Access Eportal with a multiuser login
			// Go to "TMS"
			eportalCust=testDataOR.get("customer");
			final String multiuser=testDataOR.get("multi_user_login"),firstName=testDataOR.get("multi_first_name"),lastName=testDataOR.get("multi_last_name"),
			newATSerialNo=testDataOR.get("new_at_serial_number"),modAtSerialNo=testDataOR.get("modified_at_serial_number"),modAtPartNo=testDataOR.get("modified_at_part_number");
			logger.info("SMR2483 execution started");
			logger.info("Step 1:");
			login("URLEportal",multiuser,firstName,lastName);

			//Go to tms,click on asset tracking Events management sub menu
			logger.info("Step 2, 3:");
			navigateToSubPage(ASSETTKING,selUtils.getCommonObject("eptmstab_xpath"),selUtils.getCommonObject("tms_assttrack_xpath"));
			selUtils.switchToFrame();
			selUtils.clickOnWebElement(selUtils.getObject("terminals_link"));
			logger.info("Clicked on terminal link");

			//click on edit button of new_at_serial_number
			logger.info("Step 4:");
			editTermNVerify(newATSerialNo,modAtSerialNo,modAtPartNo);

			//Delete the events
			logger.info("Step 5:");
			clkOnDirectObj("serorpartdel_xpath", "SERIALORPART", modAtSerialNo);
			logger.info("Clicked on delete terminal");
			selUtils.clickOnWebElement(selUtils.getObject("delterminalconfirm_id"));
			selUtils.clickOnWebElement(selUtils.getObject("delterminalclose_id"));
			xpath=getPath("termserialorpartno_xpath").replace("SERIALORPART", modAtSerialNo);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
			Assert.assertFalse(selUtils.isElementPresentxpath(xpath),"terminal "+ modAtSerialNo +" is present after deletion");
			logger.info("Verifed the terminal "+ modAtSerialNo +" is not present after deletion");
			
			//Logout multi user from ePortal
			//Logout is handeled in SSO
			logger.info("Step 6:");
			logger.info("Multi user logout is taken care at the suite level as part of the SSO");

			logger.info("SMR2483 executed successfully");

		} catch (Throwable t) {
			handleException(t);
		}
	}
	
	/**
	 * Method to edit the terminal,and verify the edited terminal in the page
	 * @param newAtSerNo
	 * @param modAtSerNo
	 * @param modAtPrtNo
	 */
	private void editTermNVerify(String newAtSerNo,String modAtSerNo,String modAtPrtNo)
	{
		clkOnDirectObj("serorpartedit_xpath", "SERIALORPART", newAtSerNo);
		logger.info("Clicked on edit terminal");
		selUtils.getObject("atserialno_xpath").clear();
		selUtils.getObject("atpartno_xpath").clear();
		selUtils.populateInputBox("atserialno_xpath", modAtSerNo);
		selUtils.populateInputBox("atpartno_xpath", modAtPrtNo);
		selUtils.clickOnWebElement(selUtils.getObject("modifytermconfirm_id"));
		selUtils.clickOnWebElement(selUtils.getObject("modifytermclose_id"));
		xpath=getPath("termserialorpartno_xpath").replace("SERIALORPART", modAtSerNo);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		webElement=selUtils.getObjectDirect(By.xpath(xpath));
		selUtils.verifyTextEqualsWith(webElement, modAtSerNo);
		xpath=getPath("termserialorpartno_xpath").replace("SERIALORPART", modAtPrtNo);
		webElement=selUtils.getObjectDirect(By.xpath(xpath));
		selUtils.verifyTextEqualsWith(webElement, modAtPrtNo);
		
	}
	
}


