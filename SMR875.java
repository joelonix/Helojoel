package com.ingenico.testsuite.tmsmanagement;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/tmsmanagement/SMR875.java $
$Id: SMR875.java 17658 2016-03-22 09:47:44Z rkahreddyga $
*/

import java.sql.SQLException;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.base.TestBase;
import com.ingenico.common.CommonConstants;

/**
 *  SMR-875:Move an estate
 * @author Raghunath K
 *
 */
public class SMR875 extends SuiteTmsManagement {

	/**
	 * Move an estate
	 */
	@Test(groups = "SMR875")
	public void smr875() {

		try {
			eportalCust = testDataOR.get("customer");
			final String firstName = testDataOR.get("superuser_first_name"), lastName = testDataOR.get("superuser_last_name"),
			dbIngEstate=testDataOR.get("databaseIngEstate"),est2Sign = testDataOR.get("estate2_signature"),
			subEstName=testDataOR.get("subestate_name"),est2Name=testDataOR.get("estate2_name"),
			subEstSign = testDataOR.get("subestate_signature"), firstSerialNum = testDataOR.get("first_serial_number"),
			lastSerialNum = testDataOR.get("last_serial_number"),trmnalTech = testDataOR.get("technology"),
			trmnalPartNum= testDataOR.get("terminal_part_number");		
			
			logger.info("SMR875 execution started");
			// Access Eportal with a superuser
			// Go to "TMS" and then click on terminals
			logger.info("Step 1");
			login("URLEportal", testDataOR.get("superuser"), firstName,lastName);
			navigateSubMenu(SNAPSHOT, "eptmstab_xpath", "estates_link");
			logger.info("Clicked on estates link");

			// click on the edit button of estatesignature
			logger.info("Step 2");
			
			waitMethods.waitForWebElementPresent(selUtils.getCommonObject("waitmenuboxlistestates_id"));
			xpath = TestBase.getPath("editestatesign_xpath").replace(NAME,subEstSign);
			webElement=selUtils.getObjectDirect(By.xpath(xpath));
			selUtils.clickOnWebElement(webElement);
			logger.info("Clicked on edit subestate signature");


			// Click on Modify
			logger.info("Step 3");
			selUtils.waitForTxtPresent("estactionmovebtnid_id",MOVE);
			selUtils.clickOnWebElement(selUtils.getObject("estactionmovebtnid_id"));
			logger.info("Clicked on Move button");
			
			
			//set new estatesignature and name
			logger.info("Step 4");
			options=selUtils.getObjects("selmovetonewest_xpath");
			selUtils.selectValueInDropDown(options, est2Sign);
			logger.info("Selected "+est2Sign);
			selUtils.clickOnWebElement(selUtils.getObject("diaboxmovtoestconfirm_id"));
			logger.info("Clicked on confirm button");
			selUtils.clickOnWebElement(selUtils.getObject("diaboxmovtoestclose_id"));
			logger.info("Clicked on close button");
			
			//Go to Estates
			//subestatesignatue is now a subestate of estate2signature
			logger.info("Step 5");
			selUtils.clickOnWebElement(selUtils.getObject("estates_link"));
			logger.info("Clicked on estates link");	
			vMultiTableValue("vsubestatemodifysigname_xpath",est2Sign,subEstSign);
			logger.info("Verified the text equals with '"+subEstSign+"' is now a subestate of "+est2Sign);
			
			
			//Go to terminals,click on find and search new estate Signature
			//Access the Ingestate database and look for a row
			logger.info("Step 6");
			selUtils.clickOnWebElement(selUtils.getObject("terminals_link"));
			logger.info("Clicked on terminals link");
			findNSrchSign(est2Sign);
			
			//Verify terminals first and last serial numbers concatanation of Automated terminal part number
			//first terminal part number 8 digits and last Serial Number 8 digits for TETRA and TELIUM terminals
						
			trminlSignCrte(firstSerialNum,lastSerialNum,trmnalPartNum,trmnalTech);	
			
			//Access the Ingestate database and look for a row in the 'terminal' table
			logger.info("Step 7");
			vDbTerminalSign(dbIngEstate,subEstName,est2Name);
			
			logger.info("SMR875 executed successfully");
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
	
	private void vDbTerminalSign(String dbIngEstate,String subEstSignr, String newEstateName) throws SQLException {
		if(dbCheck){
			//Verify created estate name correctly updated in database
			sqlQuery="SELECT * FROM terminal a INNER JOIN terminal b ON a.parent_id = b.terminal_id " 
			+ "WHERE a.name ='"+subEstSignr+"' AND b.name LIKE '%"+newEstateName+"' ;";
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
}
