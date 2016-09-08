package com.ingenico.testsuite.customermanagement;


/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/customermanagement/SMR134.java $
$Id: SMR134.java 18404 2016-05-04 06:40:16Z jsamuel $
 */
import org.testng.annotations.Test;
import org.openqa.selenium.By;

/**
 * SMR-134:Edit Zone
 * @author joel.samuel
 *
 */


public class SMR134 extends SuiteCustomerManagement{

	/**
	 * Edit Zone
	 */
	@Test(groups="SMR134")
	public void smr134(){
		try {
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR134 execution started");
			//final String custName,zoneOnea,newZoneOnea;
			
			final String custName=testDataOR.get("customer"),zoneOnea=testDataOR.get("zone_nameone_a"),newZoneOnea=testDataOR.get("new_zone_nameone_a"),
		    newSapCode=testDataOR.get("new_sap_code");
			logger.info("SMR134 execution started");
			
			// Access Everest with a superuser,Go to "Customer" module
			logger.info("Step 1,2:");
			navigateToCustomerPage();

			//Click on the name of the <customer>
			logger.info("Step 3 :");
			clkCustNameList(custName);
						
			//Go to "Structure" tab
			logger.info("Step 4 :");
			selUtils.clickOnWebElement(selUtils.getObject("structure_tab_xpath"));
			logger.info("Clicked on the '"+STRUCTURE+ "' Tab" +" of the customer");

			//Click on the "Edit" button of a zone <zone_nameone_a>
			logger.info("Step 5 :");
			editZone(zoneOnea,newZoneOnea,newSapCode);
			vZone(newZoneOnea);
			vSapCode(newZoneOnea,newSapCode);
			
			//Click on the "Edit" button of a zone <new_zone_nameone_a>
			logger.info("Step 6 :");
			editZone(newZoneOnea,zoneOnea,"");
			vZone(zoneOnea);
			vSapCode(zoneOnea,"");
			
			logger.info("SMR134 is successfully executed");		
			
		}catch (Throwable t) {
			handleException(t);
		}

	}
	
	/**
	 * Method to edit zone
	 * @param zonename
	 * @param editedZoneName
	 * @param sapcode
	 */
	private void editZone(String zonename,String editedZoneName,String sapcode){
		clkOnDirectObj("editzonelvlonea_xpath","LEVEL",zonename);
		selUtils.populateInputBox("orgstructlevelname_id", editedZoneName);
		selUtils.populateInputBox("sap_structure_id", sapcode);
		selUtils.clickOnWebElement(selUtils.getObject("orgstructok_id"));
	}
	
	/**
	 * Method to verify zones
	 * @param zoneName
	 */
	private void vZone(String zoneName){
		locPath=getPath("edit_newzonea_xpath").replace("NEWZONE", zoneName);
		waitMethods.waitForWebElementPresent(selUtils.getObjectDirect(By.xpath(locPath)));
		webElement=selUtils.getObjectDirect(By.xpath(locPath));
		selUtils.verifyTextEqualsWith(webElement, zoneName);
		logger.info("Validated edited zone name "+zoneName);
	}
	
	/**
	 * Method to verify sapcode
	 * @param zoneName
	 * @param sapcode
	 */
	private void vSapCode(String zoneName,String sapcode){
		
		colIndex=selUtils.getIndexForColHeader("orgstructcolheader_xpath","SAP");
		//vMultiTableValue("sapcode_xpath", zoneName, colIndex+"");
		xpath=getPath("sapcode_xpath").replace("NEWZONE", zoneName).replace("INDEX", colIndex+"");
		webElement=selUtils.getObjectDirect(By.xpath(xpath));
		selUtils.verifyTextEqualsWith(webElement, sapcode);
		logger.info("Validated edited sapcode "+sapcode);
	}

}
