package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR139.java $
$Id: SMR139.java 17185 2016-02-19 09:17:56Z jsamuel $
 */

import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;
/**
 * SMR-139:Provision Numcomm only(manually)
 * @author Nagaveni.Guttula
 */
public class SMR139  extends SuiteCardPayment{

	/**
	 * Provision Numcomm manually without a POS
	 */
	@Test(groups={"SMR139"})
	public void smr139() {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR139 execution started");
			final String numComm=testDataOR.get("num_comm"),projectName=testDataOR.get("project_name"),cust=testDataOR.get("customer"),
					dbEportal=testDataOR.get("databaseEportal"),sql,merchnId;
			final String[] numcomVal={testDataOR.get("num_comm"),testDataOR.get("code_bank"),testDataOR.get("adr_x25")};

			//Access Everest with the Everest superuser
			//Navigate to Card Payment-Customer Provisioning and 
			//select the customer
			addProfToCust(cust);

			//Step 5:Select 'Manual localization',NumComm and click Next	
			logger.info("Step 6:");
			selUtils.selectItem(selUtils.getObject("operationorder_id"), MANUALLOCALZTION);
			logger.info("Selected "+MANUALLOCALZTION);
			selUtils.clickOnWebElement(selUtils.getObject("numcomm_xpath"));
			selUtils.clickOnWebElement(selUtils.getObject("orderopenext_id"));

			//Step 6: Select a lowest level zone and click Next
			logger.info("Step 7:");
			selUtils.selectItem(selUtils.getObject("localization_id"), testDataOR.get("lowest_level_zone_a"));
			selUtils.clickOnWebElement(selUtils.getObject("localizationnext_xpath"));
			logger.info("Selected a lowest level zone and next button");


			//Step 7:select 'Manual' and add numcomm data then click green button 
			logger.info("Step 8:");
			//waitNSec(2);
			selUtils.clickOnWebElement(selUtils.getObject("numcommmanual_xpath"));
			selUtils.clickOnWebElement(selUtils.getObject("addnumcomm_xpath"));
			waitNSec(2);
			addNumcommval(NUMCOMLOC, numcomVal);
			selUtils.selectItem(selUtils.getObject("numert_id"), testDataOR.get("num_ert"));
			logger.info("Selected num ert"+testDataOR.get("num_ert"));
			//selUtils.clickOnWebElement(selUtils.getObject("addnumcommgbutt_xpath"));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObject("addnumcommgbutt_xpath"));
			vErrorMessage("numcommerr_id");
			selUtils.clickOnWebElement(selUtils.getObject("enternumcommnext_xpath"));

			reportErrMessage("pos_num_err_id");
			logger.info("Selected manual option and added numcomm data then next button");

			//Step 8: Click Next		
			logger.info("Step 9:");
			selUtils.clickOnWebElement(selUtils.getObject("viewconfignext_id"));
			logger.info("Clicked on next button");

			//Step 9:Enter a 'Project Name' and click on the 'Save' button			
			logger.info("Step 10:");
			selUtils.populateInputBox("projname_id", projectName);
			selUtils.clickOnWebElement(selUtils.getObject("configsumsavebttn_xpath"));
			logger.info("Entered the projectName as "+projectName+" and clicked on save button");

			//Step 10: Go to "Pending Provisioning" sub-tab		
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), testDataOR.get("customer"));
			logger.info("Step 11:");
			clkOnPendingProv(projectName);

			//Step 11:Click the edit button of projectName	
			logger.info("Step 12:");
			clkOnDirectObj("editpospending_xpath","NAME",projectName);
			logger.info("Clicked on the edit button");

			//Step 12: Deploy the request,click on prov tab			
			logger.info("Step 13:");
			clickOnDeploy();
			Assert.assertTrue(selUtils.getCommonObject("succ_deploymsg_xpath").getText().contains(SUCSSFULLYDPLYD),SUCSSFULLYDPLYD+"success message does not appear");
			logger.info("Verified the success message");

			//check that the numComm  has been correctly created
			clckOnProvOrPosTab("provtab_xpath",PROVTAB);
			colIndex=selUtils.getIndexForColHeader("colheaders_css", NUMCOMMCOL);
			verifyLvlColLvlValPresence("provlst_css",colIndex,numComm);
			logger.info("Verified the Numcomm value  "+numComm+" is displayed in the  Provisioning list");

			if(dbCheck){
				/*
				 * A row with the column 'merchantid'= numcommm 
				 * has been created in the 'merchantid' table of the eportal db
				 */
				sql= "SELECT merchantid FROM merchantid WHERE merchantid='"+numComm+"';";
				resSet = dbMethods.getDataBaseVal(dbEportal,sql,CommonConstants.ONEMIN);
				merchnId=resSet.getString("merchantid");
				Assert.assertTrue(merchnId.equals(numComm), numComm+" value is not stored in database");
				logger.info("Verified merchantid column is having "+ numComm +" in the database");
			}
			
			logger.info("SMR139 executed successfully");
		}
		catch (Throwable t) {
			handleException(t);
		}


	}

}
