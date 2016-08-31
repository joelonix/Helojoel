package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR140.java $
$Id: SMR140.java 18135 2016-04-19 10:32:34Z jsamuel $
 */

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.base.TestBase;
import com.ingenico.common.CommonConstants;

/**
 *  SMR-140:Load Numcomm CSV file without a POS
 * @author Raghunath.K
 *
 */
public class SMR140 extends SuiteCardPayment {

	/**
	 *  Load Numcomm CSV File
	 */
	@Test(groups="SMR140")
	public void smr140()  {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			final String projectName=testDataOR.get("project_name"),dbEportal=testDataOR.get("databaseEportal"),merchnId,
			numComm=getDataFromCSV( CommonConstants.NUMCOMMCSV,NUMCOMM).trim();
			
			logger.info("SMR140 execution started");
			//Access Everest with the Everest superuser
			//Go to "Card Payment" sub menu "Customer Provisioning"  
			//and select Customer
			logger.info("Step 1, 2:");

			navigateToSubPage(CUSTPROV,selUtils.getCommonObject("cardpaymt_tab_xpath"),selUtils.getCommonObject("custprov_xpath"));
			selUtils.selectItem(selUtils.getCommonObject("cust_Sel_id"), testDataOR.get("customer"));
			
			//verify and delete existing project name for pos creation
			selUtils.clickOnWebElement(selUtils.getCommonObject("pendingprovtab_xpath"));
			verifyExistingData("deleteprojectname_xpath","colheaders_css","colallrows_xpath", projectName, PROJECTNAME);
			
			//Go to "Provisioning" tab and click on plus button		
			logger.info("Step 3:");
			selUtils.clickOnWebElement(selUtils.getObject("provtab_xpath"));
			logger.info("Clicked on the Provisioning Tab");
			selUtils.clickOnWebElement(selUtils.getObject("provplus_xpath"));
			logger.info("Clicked on the plus button");
			
			// If there are more than one Axis location for customer then select Axis location 
			//And click next button
			logger.info("Step 4:");
			selAxisLoc(testDataOR.get("axis_location"));
						
			//Select an existing EMV profile and then click on Next button
			logger.info("Step 5:");
			logger.info("profile window is displayed");
			selUtils.selectItem(selUtils.getObject("profile_id"), testDataOR.get("emvfr_profile").replaceAll(".zip","").trim());
			logger.info("Selected the profile as "+ (testDataOR.get("emvfr_profile").replaceAll(".zip", "")).trim());
			selUtils.clickOnWebElement(selUtils.getObject("profilenext_id"));
			logger.info("Clicked on next button");
			
			//Select Manual localization in the "Order" dropdown list
			// select the NumComm option button and then click Next
			logger.info("Step 6:");
			selUtils.selectItem(selUtils.getObject("operationorder_id"), MANUALLOCALZTION);
			logger.info("Selected "+MANUALLOCALZTION);
			selUtils.clickOnWebElement(selUtils.getObject("numcomm_xpath"));
			selUtils.clickOnWebElement(selUtils.getObject("orderopenext_id"));
			logger.info("Selected NumComm option and then clicked next button");
			
			//Select a lowest level zone and then click Next
			logger.info("Step 7:");
			selUtils.selectItem(selUtils.getObject("localization_id"), testDataOR.get("lowest_level_zone_a"));
			selUtils.clickOnWebElement(selUtils.getObject("localizationnext_xpath"));
			logger.info("Selected Lowest level zone next button");
			
			//Select Load from CSV file , select the Numcomm CSV file and then click Next
			logger.info("Step 8:");
			selUtils.clickOnWebElement(selUtils.getObject("sel_csvfile_xpath"));
			logger.info("Selected Load from CSV file");
			//selUtils.populateInputBox("sel_Loadcsvfile_id", CommonConstants.NUMCOMMCSV );
			selUtils.getObject("sel_Loadcsvfile_id").sendKeys(CommonConstants.NUMCOMMCSV);
			selUtils.clickOnWebElement(selUtils.getObject("numcomm_next_xpath"));
			reportErrMessage("pos_num_err_id");
			logger.info("Selected Numcomm CSV file and then clicked next button ");
			
			//Click Next button
			logger.info("Step 9:");
			selUtils.clickOnWebElement(selUtils.getObject("viewconfignext_id"));
			logger.info("clicked on viewconfignext button");			
			
			//Enter a Project Name and then click on the Save button
			logger.info("Step 10:");
			selUtils.populateInputBox("projname_id", projectName);
			logger.info("Entered the projectName as "+projectName);
			selUtils.clickOnWebElement(selUtils.getObject("configsumsavebttn_xpath"));
			
			//Go to 'Pending Provisioning' sub-tab
			logger.info("Step 11:");
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), testDataOR.get("customer"));
			clkOnPendingProv(projectName);
			
			
			//Click the edit button of the pending operation projectname	
			logger.info("Step 12:");
			clkOnDirectObj("editpospending_xpath","NAME",projectName);
			logger.info("Clicked on the edit button");
			
			//click on 'Deploy' button and then click on 'OK' button to confirm the deployment request
			logger.info("Step 13:");
			clickOnDeploy();
			Assert.assertTrue(selUtils.getCommonObject("succ_deploymsg_xpath").getText().contains(SUCSSFULLYDPLYD));
			logger.info("Verified the success message");
			clckOnProvOrPosTab("provtab_xpath",PROVTAB);
			
			/*searchVal(numComm, "numcommfilter_id", VALEQ, "numcomminput_id");*/
			waitMethods.waitUntilVisibilityOfElements("provlst_css",TestBase.getCommonPath("provlst_css"));
			colIndex=selUtils.getIndexForColHeader("colheaders_css", NUMCOMMCOL);
			verifyLvlColLvlValPresence("provlst_css",colIndex,numComm);
			logger.info("Verified the Numcomm value  "+numComm+" is displayed in the  Provisioning list");
			
			if(dbCheck){
				//num_comms(csv)  are created in the merchantid table of the eportaldb
				sqlQuery= "SELECT merchantid FROM merchantid WHERE merchantid='"+numComm+"';";
				resSet = dbMethods.getDataBaseVal(dbEportal,sqlQuery,CommonConstants.ONEMIN);
				merchnId=resSet.getString("merchantid");
				Assert.assertTrue(merchnId.equals(numComm), numComm+" value is not stored in database");
				logger.info("Verified merchantid column is having "+ numComm +" in the database");
			}
			
			logger.info("SMR140 execution successful");
		}
		catch (Throwable t) {
			handleException(t);
		}

	}


}
