package com.ingenico.testsuite.tmsmanagement;

/*
 $HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/tmsmanagement/SMR2482.java $
 $Id: SMR2482.java 18084 2016-04-15 09:40:39Z jsamuel $
 */


import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * SMR-2482:Asset Tracking: Event Management Functions
 *@author joel.samuel
 */
public class SMR2482 extends SuiteTmsManagement {

	/**
	 * Asset Tracking: Event Management Functions
	 * 
	 */
	@Test(groups = "SMR2482")
	public void smr2482() {

		try 
		{
			// Access Eportal with a multiuser login
			// Go to "TMS"
			eportalCust=testDataOR.get("customer");
			final String multiuser=testDataOR.get("multi_user_login"),firstName=testDataOR.get("multi_first_name"),lastName=testDataOR.get("multi_last_name"),
					newATSerialNo=testDataOR.get("new_at_serial_number"),owner1=testDataOR.get("owner1"),owner2=testDataOR.get("owner2"),location1=testDataOR.get("location1"),location2=testDataOR.get("location2");
			logger.info("SMR2482 execution started");
			logger.info("Step 1:");
			login("URLEportal",multiuser,firstName,lastName);

			//Go to tms,click on asset tracking Events management sub menu
			logger.info("Step 2, 3:");
			navigateToSubPage(ASSETTKING,selUtils.getCommonObject("eptmstab_xpath"),selUtils.getCommonObject("tms_assttrack_xpath"));
			selUtils.switchToFrame();
			selUtils.clickOnWebElement(selUtils.getObject("evnts_mngmnt_link"));
			logger.info("Clicked on events management link");

			//click on edit button of the event for serial number,new at serial
			//no
			logger.info("Step 4:");
			editEventTerm(newATSerialNo,owner1,owner2,location1,location2);
			vEditedOwnOrLoc(COLOWNER,owner2,newATSerialNo);
			vEditedOwnOrLoc(COLLOCATION,location2,newATSerialNo);

			//Delete the events
			logger.info("Step 5:");
			clkOnDirectObj("eventsdel_xpath", "EVENTS", newATSerialNo);
			selUtils.clickOnWebElement(selUtils.getObject("deleventconfirmbttn_id"));
			selUtils.clickOnWebElement(selUtils.getObject("deleventclosebttn_id"));
			xpath=getPath("newatevnts_xpath").replace("EVENTS", newATSerialNo);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
			Assert.assertFalse(selUtils.isElementPresentxpath(xpath),"Failed due to"+newATSerialNo+" is present after deletion");
			logger.info("Verified "+newATSerialNo+" is not present after deletion");
			
			//Logout multi user from ePortal
			//Logout is handeled in SSO
			logger.info("Step 6:");
			logger.info("Multi user logout is taken care at the suite level as part of the SSO");
			

			logger.info("SMR2482 executed successfully");

		} catch (Throwable t) {
			handleException(t);
		}
	}

	/**
	 * Method to edit the event
	 * @param newAT
	 * @param own1
	 * @param own2
	 * @param loc1
	 * @param loc2
	 */
	private void editEventTerm(String newAT,String own1,String own2,String loc1,String loc2){
		xpath=getPath("eventsedit_xpath").replace("EVENTS", newAT);
		selUtils.clickOnWebElement(selUtils.getObjectDirect(By.xpath(xpath)));
		logger.info("Clicked on edit event");
		selUtils.clickOnWebElement(selUtils.getObject("evntsmodifybttn_id"));
		logger.info("Clicked on  modified button");
		selUtils.populateInputBox("owner_xpath", own2);
		clkOnDirectObj("autodropoption_xpath", "NAME", own2);
		selUtils.populateInputBox("location_xpath", loc2);
		clkOnDirectObj("autodropoption_xpath", "NAME", loc2);
		selUtils.clickOnWebElement(selUtils.getObject("evntsconfirmbttn_id"));
		selUtils.clickOnWebElement(selUtils.getObject("evntsclosebttn_id"));
		selUtils.clickOnWebElement(selUtils.getObject("backtoevntlist_id"));
	}

    /**
     * Method to verify edited owner or location in the events page	
     * @param colName
     * @param ownorloc
     * @param atserialno
     */
	private void vEditedOwnOrLoc(String colName,String ownorloc,String atserialno){
		int ownerOrLoccolIndex=selUtils.getIndexForColHeader("evntscolheder_css", colName);
		xpath=getPath("ownorlocname_xpath").replace("EVENTS", atserialno);
		xpath=xpath.replace("COLINDEX", (ownerOrLoccolIndex-2)+"");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		webElement=selUtils.getObjectDirect(By.xpath(xpath));
		selUtils.verifyTextEqualsWith(webElement, ownorloc);
	}
}


