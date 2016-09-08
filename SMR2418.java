package com.ingenico.testsuite.tmsmanagement;

/*
 $HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/tmsmanagement/SMR2418.java $
 $Id: SMR2418.java 18034 2016-04-13 09:51:26Z rkahreddyga $
 */

import org.testng.annotations.Test;


/**
 * SMR-2418:Asset Tracking: Create Owners and Locations
 * @author Raghunath K
 */
public class SMR2418 extends SuiteTmsManagement {

	/**
	 * Asset Tracking: Create Owners and Locations
	 * 
	 */
	@Test(groups = "SMR2418")
	public void smr2418() {

		try 
		{
			// Access Eportal with superuser
			// Go to "TMS" and then assert tracking
			eportalCust = testDataOR.get("customer");
			final String firstName = testDataOR.get("superuser_first_name"), lastName = testDataOR.get("superuser_last_name"),
			owner1=testDataOR.get("owner1"),owner2=testDataOR.get("owner2"),
			location1=testDataOR.get("location1"),location2=testDataOR.get("location2");
					
			logger.info("SMR2418 execution started");
			logger.info("Step 1");
			login("URLEportal", testDataOR.get("superuser"), firstName,lastName);
			
			logger.info("Step 2 and step 3");
			navigateSubMenu(ASSETTKING, "tms_assttrack_xpath", "tmsowners_link");
			logger.info("Clicked on owners link");
			
			// Add an owner1 and verify owner1 in the Owners page
			logger.info("Step 4");
			addOwner(owner1);
			
			// Add an owner2 and verify owner2 in the Owners page
			logger.info("Step 5");
			addOwner(owner2);
			
			//Click on the locations sub menu
			logger.info("Step 6");
			selUtils.clickOnWebElement(selUtils.getObject("tmslocations_link"));
			logger.info("Clicked on locations link");
			
			// Add and verify location1 should be correctly added on the locations page
			logger.info("Step 7");
			addLocation(location1);
			
			//Add and verify location2 should be correctly added on the locations page
			logger.info("Step 8");
			addLocation(location2);
			
			logger.info("SMR2418 executed successfully");

		} catch (Throwable t) {
			handleException(t);
		}
	}

	
	/**
	*  Asset Tracking: Add an Owners
	*/
	private void addOwner(String owner)
	{
		selUtils.clickOnWebElement(selUtils.getObject("addnownerbtnid_id"));
		selUtils.populateInputBox("addownerfld_id", owner);
		logger.info("Entered "+owner);
		selUtils.clickOnWebElement(selUtils.getObject("confmpopupcrtowner0_id"));
		reportErrMessage("popupownermsgresult_xpath");
		logger.info("Clicked on confirm button");
		selUtils.clickOnWebElement(selUtils.getObject("closepopupcrtowner0_id"));
		logger.info("Clicked on close button");
		
		//verify onwner should be correctly added on the owners page
		colIndex=selUtils.getIndexForColHeader("ownerloccolmn_css", OWNER);
		selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
		verifyLvlColLvlValPresence("addownerlocations_css", colIndex, owner);
	}
	
/**
*  Asset Tracking: Add an Locations
*/
	private void addLocation(String location)
	{
		selUtils.clickOnWebElement(selUtils.getObject("addnownerbtnid_id"));
		selUtils.populateInputBox("addlocationfld_id", location);
		logger.info("Entered "+location);
		selUtils.clickOnWebElement(selUtils.getObject("confmpopupcrtlocation0_id"));
		reportErrMessage("popupownermsgresult_xpath");
		logger.info("Clicked on confirm button");
		selUtils.clickOnWebElement(selUtils.getObject("closepopupcrtlocation0_id"));
		logger.info("Clicked on close button");
		
		//verify location should be correctly added on the location page
		colIndex=selUtils.getIndexForColHeader("ownerloccolmn_css", LOCATION);
		selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
		verifyLvlColLvlValPresence("addownerlocations_css", colIndex, location);
		
	}
	
	
}


