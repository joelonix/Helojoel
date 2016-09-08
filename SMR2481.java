package com.ingenico.testsuite.tmsmanagement;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/tmsmanagement/SMR2481.java $
$Id: SMR2481.java 18241 2016-04-26 06:50:01Z haripraks $
 */

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.ingenico.base.TestBase;

/**
 * SMR-2481:Asset Tracking: Event Creation
 * @author Hariprasad.KS
 *
 */
public class SMR2481 extends SuiteTmsManagement{

	/**
	 * Asset Tracking: Event Creation
	 */
	@Test(groups="SMR2481")
	public void smr2481() {
		try{

			eportalCust=testDataOR.get("customer");
			final String multiuser=testDataOR.get("multi_user_login"),firstName=testDataOR.get("multi_first_name"),lastName=testDataOR.get("multi_last_name"),
					serialno=testDataOR.get("at_serial_number"),partno=testDataOR.get("at_part_number"),eventtype1=testDataOR.get("event_type1"),attermStatus=testDataOR.get("at_terminal_status"),
					owner1=testDataOR.get("owner1"),location1=testDataOR.get("location1"),newserialno=testDataOR.get("new_at_serial_number"),newpartno=testDataOR.get("new_at_part_number"),
					eventtype2=testDataOR.get("event_type2"),newattermStatus=testDataOR.get("new_at_terminal_status"),connectionStatus=testDataOR.get("connection_status"),
					generalStatus=testDataOR.get("general_status"),sealsintegrity=testDataOR.get("seals_integrity");

			logger.info("SMR2481 execution started");
			//Access ePortal with multi_user_login and select customer
			logger.info("Step 1:");
			login("URLEportal",multiuser,firstName,lastName);

			//Go to "TMS" tab,and click on Asset tracking events management link
			logger.info("Step 2,3:");
			navToSubPage("tms_tab_xpath", "tms_assttrack_xpath", ASSETTKING);
			selUtils.switchToFrame();
			waitMethods.waitForWebElementPresent(selUtils.getCommonObject("tampagexhtml_xpath"));
			selUtils.clickOnWebElement(selUtils.getCommonObject("eventsmgmt_link"));
			logger.info("Clicked on Events Management link");

			//Add event and verify 
			logger.info("Step 4:");
			logger.info("Add an event");
			addEvent(serialno, partno, eventtype1, attermStatus, owner1, location1);
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			vMultiValue("multitable_xpath", serialno, attermStatus,eventtype1);
			logger.info("verified an event"+eventtype1+"added successfully");
			
			logger.info("Step 5:");
			logger.info("Add new event");
			addEvent(newserialno, newpartno, eventtype2, newattermStatus, owner1, location1);
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			vMultiValue("multitable_xpath", newserialno, newattermStatus,eventtype2);
			logger.info("verified an event"+eventtype2+"added successfully");
			
			logger.info("Step 6:");
			selUtils.clickOnWebElement(selUtils.getCommonObject("terminals_link"));
			logger.info("Clicked on terminals link");
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(TestBase.getCommonPath("roundpoitloader_xpath"))));
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));	
			vMultiTableValue("terminaltable_xpath", newserialno, newpartno);
			logger.info("verified terminal with serial number"+newserialno+" and part number"+newpartno+"added successfully");

			logger.info("Step 7:");
			logger.info("Add an Inspection");
			selUtils.clickOnWebElement(selUtils.getCommonObject("eventsmgmt_link"));
			logger.info("Clicked on Events Management link");
			addInspection(serialno, partno, attermStatus, connectionStatus, generalStatus, sealsintegrity);
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			vMultiTableValue("terminaltable_xpath", serialno, attermStatus);
			logger.info("verified inspection with serial number"+serialno+" and terminal status"+attermStatus+"added successfully");
			
			logger.info("Step 8:");
			logger.info("Add an Inventory");
			addInventory(serialno, partno, attermStatus, owner1, location1, connectionStatus, generalStatus, sealsintegrity);
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			vMultiTableValue("terminaltable_xpath", serialno, attermStatus);
			logger.info("verified inventory with serial number"+serialno+" and terminal status"+attermStatus+"added successfully");
			
			logger.info("Step 9:");
			logger.info("multiuser logout is taken care at the suite level as part of the SSO");

			logger.info("SMR2481 Executed Successfully");	
		}
		catch (Throwable t) {
			handleException(t);
		}
	}

	/**
	 * Create an event
	 * @param serialno
	 * @param partno
	 * @param eventtype
	 * @param attermStatus
	 * @param owner
	 * @param location
	 */
	private void addEvent(String serialno,String partno,String eventtype,String attermStatus,String owner,String location)
	{
		selUtils.clickOnWebElement(selUtils.getObject("addevents_xpath"));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(TestBase.getCommonPath("roundpoitloader_xpath"))));
		selUtils.waitForTxtPresent("ateventfoot_xpath", FOOTERDISP);
		if(getModWinDisp(selUtils.getObject("moddialogaddevent_xpath"),ADDEVENTS))
		{
			
			//waitMethods.waitForWebElementPresent(selUtils.getObject("eventfield_id"));
			//waitMethods.waitForWebElementPresent(selUtils.getCommonObject("tableloadimg_xpath"));
			
		//	waitMethods.waitForElementNotPresent("tableloadimg_xpath");
			waitMethods.waitForWebElementPresent(selUtils.getObject("atsrno_id"));
			selUtils.populateInputBox("atsrno_id", serialno);
			selUtils.populateInputBox("atpartno_id", partno);

			selUtils.selectItem(selUtils.getObject("eventtype_xpath"), eventtype);
			selUtils.selectItem(selUtils.getObject("termstatus_xpath"), attermStatus);
			selUtils.populateInputBox("eventowner_xpath", owner);
			waitNSec(2);
			clkOnDirectObj("autodropoption_xpath", NAME, owner);
			selUtils.populateInputBox("eventlocation_xpath", location);
			waitNSec(2);
			clkOnDirectObj("autodropoption_xpath", NAME, location);
				
			selUtils.clickOnWebElement(selUtils.getObject("eventconfirm_id"));
			monitorErrMsgDisp("createeventerror_id");
			selUtils.clickOnWebElement(selUtils.getObject("eventclose_id"));
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(TestBase.getCommonPath("roundpoitloader_xpath"))));

		}
		else{
			Assert.fail(ADDEVENTS+" Model DialogBox has not present due to fail");
		}
	}
	/**
	 * Add inspection
	 * @param serialno
	 * @param partno
	 * @param attermStatus
	 * @param connectionStatus
	 * @param generalStatus
	 * @param sealsintegrity
	 */
	private void addInspection(String serialno,String partno,String attermStatus,String connectionStatus,String generalStatus,String sealsintegrity)
	{
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(TestBase.getCommonPath("roundpoitloader_xpath"))));
		selUtils.scrollLeft();
		selUtils.clickOnWebElement(selUtils.getObject("addinspection_xpath"));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(TestBase.getCommonPath("roundpoitloader_xpath"))));
		selUtils.waitForTxtPresent("ateventfoot_xpath", FOOTERDISP);
		if(getModWinDisp(selUtils.getObject("moddialogaddinspec_xpath"),ADDINSPEC))
		{
			selUtils.populateInputBox("atsrno_id", serialno);
			selUtils.populateInputBox("atpartno_id", partno);
			selUtils.selectItem(selUtils.getObject("termstatus_xpath"), attermStatus);
			if(connectionStatus.equals("None")||generalStatus.equals("None")||sealsintegrity.equals("None"))
			{
				Assert.fail("Please provide the status different from 'None'");
			}
			else{
				selUtils.selectItem(selUtils.getObject("connectstatusinspe_id"), connectionStatus);
				selUtils.selectItem(selUtils.getObject("generalstatusinspe_id"), generalStatus);
				selUtils.selectItem(selUtils.getObject("sealsstatusinspe_id"), sealsintegrity);
			}
			selUtils.clickOnWebElement(selUtils.getObject("eventconfirm_id"));
			monitorErrMsgDisp("createeventerror_id");
			selUtils.clickOnWebElement(selUtils.getObject("eventclose_id"));
		}
		else{
			Assert.fail(ADDINSPEC+" Model DialogBox has not present due to fail");
		}
	}

	/**
	 * Add inventory
	 * @param serialno
	 * @param partno
	 * @param attermStatus
	 * @param owner1
	 * @param location1
	 * @param connectionStatus
	 * @param generalStatus
	 * @param sealsintegrity
	 */
	private void addInventory(String serialno,String partno,String attermStatus,String owner1,String location1,String connectionStatus,String generalStatus,String sealsintegrity)
	{
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(TestBase.getCommonPath("roundpoitloader_xpath"))));
		selUtils.clickOnWebElement(selUtils.getObject("addinventory_xpath"));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(TestBase.getCommonPath("roundpoitloader_xpath"))));
		selUtils.waitForTxtPresent("ateventfoot_xpath", FOOTERDISP);
		if(getModWinDisp(selUtils.getObject("moddialogaddinvent_xpath"),ADDINVENT))
		{
			selUtils.populateInputBox("atsrno_id", serialno);
			selUtils.populateInputBox("atpartno_id", partno);
			selUtils.selectItem(selUtils.getObject("termstatus_xpath"), attermStatus);
			
			selUtils.populateInputBox("eventowner_xpath", owner1);
			waitNSec(2);
			clkOnDirectObj("autodropoption_xpath", NAME, owner1);
			selUtils.populateInputBox("eventlocation_xpath", location1);
			waitNSec(2);
			clkOnDirectObj("autodropoption_xpath", NAME, location1);
			selUtils.clickOnWebElement(selUtils.getObject("eventdetails_id"));
			if(connectionStatus.equals("None")||generalStatus.equals("None")||sealsintegrity.equals("None"))
			{
				Assert.fail("Please provide the status different from 'None'");
			}
			else{
				selUtils.selectItem(selUtils.getObject("connectionstatus_id"), connectionStatus);
				selUtils.selectItem(selUtils.getObject("generalstatus_id"), generalStatus);
				selUtils.selectItem(selUtils.getObject("sealsstatus_id"), sealsintegrity);
			}
			selUtils.clickOnWebElement(selUtils.getObject("eventconfirm_id"));
			monitorErrMsgDisp("createeventerror_id");
			selUtils.clickOnWebElement(selUtils.getObject("eventclose_id"));
		}
		else{
			Assert.fail(ADDINVENT+" Model DialogBox has not present due to fail");
		}
	}
}

