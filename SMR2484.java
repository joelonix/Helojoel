package com.ingenico.testsuite.tmsmanagement;

/*
 $HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/tmsmanagement/SMR2483.java $
 $Id: SMR2483.java 17883 2016-04-06 07:50:53Z jsamuel $
 */


import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * SMR-2483:Asset Tracking: Terminal Management Functions
 *
 */
public class SMR2484 extends SuiteTmsManagement {

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
			navigateToSubPage(SNAPSHOT,selUtils.getCommonObject("eptmstab_xpath"),selUtils.getCommonObject("tms_assttrack_xpath"));
			selUtils.switchToFrame();
			//selUtils.clickOnWebElement(selUtils.getObject("terminals_link"));
			//logger.info("Clicked on terminal link");
			waitNSec(2);
			
			//read boxno of the ftdacp03			
			String boxno=getBoxNumber("Terminals status history");
			System.out.println("The box no is "+boxno);
			
			xpath=getPath("clickontooltip_xpath").replace("BOXNO", boxno);
			//System.out.println(xpath);
			selUtils.getObjectDirect(By.xpath(xpath)).click();
			action.moveToElement(selUtils.getObjectDirect(By.xpath(xpath))).click().build().perform();
			//selUtils.clickOnWebElement(selUtils.getObjectDirect(By.xpath(xpath)));
			String text=selUtils.getObject("canvastip_xpath").getText();
			System.out.println("the text is "+text);
			
			

			

			logger.info("SMR2483 executed successfully");

		} catch (Throwable t) {
			handleException(t);
		}
	}
	
	/**
	 * Get Index of Box
	 * @param BoxName
	 * @return
	 */
	public String getBoxIndex(String BoxName)
	{
		String xpath,index = null;
		try
		{
			xpath=getPath("box_index_xpath").replace("BOXNAME",BoxName );
			//waitForelementdisplayed(xpath);
			index =selUtils.getObjectDirect(By.xpath(xpath)).getAttribute("id");
			index =index.replaceAll("[A-z]","");
			return index;
		}catch (Throwable t) {
			Assert.fail("Failed during getBoxIndex validation");
		}
		return index;

	}

	/**
	 * Get box number
	 * @param BoxName
	 * @return
	 */
	public String getBoxNumber(String BoxName)
	{
		String xpath,boxno = null;
		try
		{
			xpath=getPath("box_boxno_xpath").replace("BOXNAME",BoxName );
			//waitForelementdisplayed(xpath);
			waitNSec(4);
			boxno =selUtils.getObjectDirect(By.xpath(xpath)).getAttribute("id");
			boxno =boxno.replaceAll("[A-z]","");
			return boxno;
		}catch (Throwable t) {
			Assert.fail("Failed during getBoxIndex validation");
		}
		return boxno;
	}

	
}


