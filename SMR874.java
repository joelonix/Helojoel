package com.ingenico.testsuite.tmsmanagement;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/tmsmanagement/SMR874.java $
$Id: SMR874.java 17658 2016-03-22 09:47:44Z rkahreddyga $
*/

import java.sql.SQLException;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.base.TestBase;
import com.ingenico.common.CommonConstants;

/**
 *  SMR-874:Modify an estate
 * @author Raghunath K
 *
 */
public class SMR874 extends SuiteTmsManagement {

	/**
	 * Modify an estate
	 */
	@Test(groups = "SMR874")
	public void smr874() {

		try {
			eportalCust = testDataOR.get("customer");
			final String firstName = testDataOR.get("superuser_first_name"), lastName = testDataOR.get("superuser_last_name"),
			dbIngEstate=testDataOR.get("databaseIngEstate"),estsignature = testDataOR.get("estate_signature"),
			newEstateSign=testDataOR.get("new_estate_signature"),newEstateName=testDataOR.get("new_estate_name"),
			subEstName = testDataOR.get("subestate_name"),subEstSignr = testDataOR.get("subestate_signature"),
			firstSerialNum = testDataOR.get("first_serial_number"),lastSerialNum = testDataOR.get("last_serial_number"),
			trmnalTech = testDataOR.get("technology"),trmnalPartNum= testDataOR.get("terminal_part_number");
			/*
			long sLen,sNumInc,fSNum=Long.valueOf(firstSerialNum),lSNum=Long.valueOf(lastSerialNum);
			String sigTemp,tempSeril,terPartNum,serilPart;*/
			
			
			logger.info("SMR874 execution started");

			// Access Eportal with a superuser
			// Go to "TMS" and then click on terminals
			logger.info("Step 1");
			login("URLEportal", testDataOR.get("superuser"), firstName,lastName);
			navigateSubMenu(SNAPSHOT, "eptmstab_xpath", "estates_link");
			logger.info("Clicked on estates link");

			// click on the edit button of estatesignature
			logger.info("Step 2");
			waitMethods.waitForWebElementPresent(selUtils.getCommonObject("waitmenuboxlistestates_id"));
			xpath = TestBase.getPath("editestatesign_xpath").replace(NAME,estsignature);
			webElement=selUtils.getObjectDirect(By.xpath(xpath));
			selUtils.clickOnWebElement(webElement);
			logger.info("Clicked on edit estate signature");


			// Click on Modify
			logger.info("Step 3");
			waitMethods.waitForWebElementPresent(selUtils.getCommonObject("estateButtonsAreaId_id"));
			selUtils.clickOnWebElement(selUtils.getObject("modifyclk_xpath"));
			logger.info("Clicked on Modify button");
			
			
			//set new estatesignature and name
			logger.info("Step 4");
			mEsteSigNname(newEstateSign,newEstateName);
			
			
			//Go to Estates
			logger.info("Step 5");
			selUtils.clickOnWebElement(selUtils.getObject("estates_link"));
			logger.info("Clicked on estates link");	
			vMultiTableValue("vestatemodifysigname_xpath",newEstateSign,newEstateName);
			logger.info("Verified the text equals with '"+newEstateSign+"' is present");
			
			//Subestate signature is still subestate of new estate signature
			vMultiTableValue("vsubestatemodifysigname_xpath",newEstateSign, subEstSignr);
			
			//Go to terminals,click on find and search new estate Signature
			//Access the Ingestate database and look for a row
			logger.info("Step 6");
			selUtils.clickOnWebElement(selUtils.getObject("terminals_link"));
			logger.info("Clicked on terminals link");	
			findNSrchSign(newEstateSign);
			
			//Verify terminals first and last serial numbers concatanation of Automated terminal part number
			//first terminal part number 8 digits and last Serial Number 8 digits for TETRA and TELIUM terminals
						
			trminlSignCrte(firstSerialNum,lastSerialNum,trmnalPartNum,trmnalTech);
			
			//Access the Ingestate database
			//Look for a row in the terminal table
			logger.info("Step 7");
			vDbTerminalSign(dbIngEstate,subEstSignr,subEstName,newEstateName);
			
			logger.info("SMR874 executed successfully");
		} catch (Throwable t) {
			handleException(t);
		}
	}
	
	
	
	/**
	 * Verify database for every terminal signature 
	 * should have one row in terminal table
	 * @param dbIngEstate
	 * @param sigTemp
	 * @param rootEstate
	 */
	
	private void vDbTerminalSign(String dbIngEstate,String subEstSignr,String subEstName,String newEstateName) throws SQLException {
		if(dbCheck){
			//Verify created estate name correctly updated in database
			sqlQuery="SELECT * FROM terminal a INNER JOIN terminal b ON a.parent_id = b.terminal_id " 
			+ "WHERE a.signature ='"+subEstSignr+"' AND a.name LIKE '%"+subEstName+"' AND b.name LIKE '%"+newEstateName+"' ;";
			resSet = dbMethods.getDataBaseVal(dbIngEstate,sqlQuery,CommonConstants.ONEMIN);					
			
			
			//Retrieve row size
			if(resSet.getRow()==1)
			{
				logger.info("Verified the values of estate name correctly inserted into the 'databaseIngEstate' database");
				logger.info("Retrieved Only one row Sucessfully ");
			}
			else
				Assert.fail("Failed due to row size is :"+resSet.getRow());
		  }
		}

	/**
	 * Modify estate signatue,name and click on confirm
	 * @param newEstSign
	 * @param newEstName
	 */
	private void mEsteSigNname(String newEstSign,String newEstName)
	{
		if(getModWinDisp(selUtils.getObject("modialogmodify_xpath"),MODFY))
		{
		waitMethods.waitForWebElementPresent(selUtils.getCommonObject("modifyestatedata_id"));
		selUtils.populateInputBox("editestatesign_id", newEstSign);
		logger.info("Entered newEstSign");
		selUtils.populateInputBox("editestatename_id", newEstName);
		logger.info("Entered newEstName");
		selUtils.clickOnWebElement(selUtils.getObject("editestateconfirm_id"));
		reportErrMessage("modifysignerrmsg_xpath");
		selUtils.clickOnWebElement(selUtils.getObject("editestateclosebtn_id"));
		}
		else
			Assert.fail(MODFY+" Model DialogBox has not present due to fail");
	}
}
