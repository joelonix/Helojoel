package com.ingenico.testsuite.tmsmanagement;

/*
 $HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/tmsmanagement/SMR873.java $
 $Id: SMR873.java 18101 2016-04-18 09:41:42Z haripraks $
 */

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;

/**
 * SMR-873 Create an estate
 * @author Raghunath K
 *
 */
public class SMR873 extends SuiteTmsManagement {

	/**
	 * Create an estate.
	 * 
	 */
	@Test(groups = "SMR873")
	public void smr873() {

		try {
			eportalCust = testDataOR.get("customer");
			final String firstName = testDataOR.get("superuser_first_name"), lastName = testDataOR.get("superuser_last_name"),
			dbIngEstate=testDataOR.get("databaseIngEstate"),
			rootEstate = testDataOR.get("root_estate"),estateSignature = testDataOR.get("estate_signature"),est2signature = testDataOR.get("estate2_signature"),
			estateName=testDataOR.get("estate_name"),estate2name=testDataOR.get("estate2_name"),subEstateSign=testDataOR.get("subestate_signature"),
			subEstateName=testDataOR.get("subestate_name");
			
			logger.info("SMR873 execution started");

			// Access Eportal with a superuser
			// Go to "TMS"
			logger.info("Step 1 and 2");
			login("URLEportal", testDataOR.get("superuser"), firstName,lastName);
			navigateSubMenu(SNAPSHOT, "tms_snapshot_xpath", "estates_link");
			logger.info("Clicked on estates link");
			clkAddEstate();

			// Select Root estate and then enter estate details
			//Click on confirm
			logger.info("Step 3");
			addEstate(rootEstate,estateSignature,estateName);
			vMultiTableValue("estatetbldata_xpath",estateSignature, estateName);
			
			//Access the Ingestate database and look for a row
			logger.info("Step 4");
			if(dbCheck){
				//Verify created estate name correctly updated in database
				sqlQuery="SELECT * FROM terminal a INNER JOIN terminal b ON a.parent_id = b.terminal_id "
				+ "WHERE a.signature ='"+estateSignature+"' AND a.name = '"+estateName+"' AND b.name LIKE '%"+rootEstate+"' ;";
				resSet = dbMethods.getDataBaseVal(dbIngEstate,sqlQuery,CommonConstants.ONEMIN);	
				logger.info("Verified the values of estate name correctly inserted into the 'databaseIngEstate' database");
				
				//Retrieve row size
				if(resSet.getRow()==1)
					logger.info("Retrieved only one row sucessfully ");
					else
					Assert.fail(" Failed due to Row size is : "+resSet.getRow());
			}
						
			//Go to Estates and then click  on "Add an estate"
			logger.info("Step 5");
			selUtils.clickOnWebElement(selUtils.getObject("estates_link"));
			logger.info("Clicked on estates link");
			clkAddEstate();		
			
			//Select Root estate and then enter estate2 details
			logger.info("Step 6");
			addEstate(rootEstate,est2signature,estate2name);
			vMultiTableValue("estatetbldata_xpath",est2signature, estate2name);
			
			//Go to Estates and then click  on "Add an estate"
			logger.info("Step 7");
			selUtils.clickOnWebElement(selUtils.getObject("estates_link"));
			logger.info("Clicked on estates link");
			clkAddEstate();
			
			//Select Root estate and then enter estate2 details
			logger.info("Step 8");
			addEstate(estateSignature,subEstateSign,subEstateName);	
			vMultiTableValue("estatetbldata_xpath", subEstateSign, subEstateName);
			logger.info("SMR873 executed successfully");
			
		} catch (Throwable t) {
			handleException(t);
		}
	}
	
	/**
	 * Enter root details and click on confirm
	 * @param rootestate
	 * @param estSignature
	 * @param estName
	 */
	private void addEstate(String rootestate,String estSignature,String estName)
	{
		
		if(getModWinDisp(selUtils.getObject("mdialogboxaddanestate_xpath"),ADDNESTATE))
		{
			options=selUtils.getObjects("rootestatesel_xpath");
			selUtils.selectValueInDropDown(options,rootestate);
			waitMethods.waitForWebElementPresent(selUtils.getObject("editestesignature_id"));
			//Due to text enter issue used to click on textbox
			selUtils.clickOnWebElement(selUtils.getObject("editestesignature_id"));
			selUtils.populateInputBox("editestesignature_id", estSignature);
			logger.info("Entered estate signature");
			selUtils.clickOnWebElement(selUtils.getObject("editestatename_id"));
			selUtils.populateInputBox("editestatename_id", estName);		
			logger.info("Entered estate name");
			selUtils.clickOnWebElement(selUtils.getObject("addnestateconfirm_id"));
			monitorErrMsgDisp("dialogboxaddesterror_id");
			vExpValPresent("addanestatesucessmsg_xpath", ADDESTATESUCCESS);
			selUtils.clickOnWebElement(selUtils.getObject("addanestateclose_xpath"));
		}
		else{
			Assert.fail(ADDNESTATE+" Model DialogBox has not present due to fail");
		}
	}
	
	/**
	 * Go to Estates and click Add an estates
	 * 
	 */
	private void clkAddEstate(){
		waitMethods.waitForWebElementPresent(selUtils.getObject("estatebtnxhtml_xpath"));
		waitNSec(3);
		selUtils.clickOnWebElement(selUtils.getObject("actioncrteestate_xpath"));
		logger.info("Clicked on add an estate ");
	}

}
