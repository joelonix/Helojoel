package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR50.java $
$Id: SMR50.java 17311 2016-02-29 09:54:49Z amsingh $
 */
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;

/**
 * SMR-50:Provision Numcomm & POS(manually)
 * @author Hariprasad.KS 
 *
 */

public class SMR50  extends SuiteCardPayment{

	@Test(groups={"SMR50"})
	/**
	 * Provision Numcomm with an existing POS (manually)
	 */
	public void smr50() {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR50 execution started");	
			final String projectName=testDataOR.get("project_name"),
					dbEportal=testDataOR.get("databaseEportal"),dbAxis=testDataOR.get("databaseAxis"),pos,numptv,numcom,merchnId;
			posNum=testDataOR.get("pos_number");
			final String[] posLoc={"posrefund_xpath","posforcing_xpath","posvoid_xpath","posnumcomm_xpath"},
					posVal={testDataOR.get("refund_option"),testDataOR.get("forcing_option"),testDataOR.get("void_option"),testDataOR.get("num_comm")},
					numcomVal={testDataOR.get("num_comm"),testDataOR.get("code_bank"),testDataOR.get("adr_x25")};

			//Access Everest with a superuser,Go to "Card Payment,
			//Customer Provisioning" sub menu
			logger.info("Step 1,2:");
			// Common steps for smr143 and smr50
			selProfToCust();
			selUtils.clickOnWebElement(selUtils.getObject("numcommpos_xpath"));
			selUtils.clickOnWebElement(selUtils.getObject("orderopenext_id"));
			logger.info("Selected NumComm and POS option and then clicked next button");

			//Select a lowest level zone  and then click next button
			logger.info("Step 7:");
			selUtils.selectItem(selUtils.getObject("localization_id"), testDataOR.get("lowest_level_zone_a"));
			selUtils.clickOnWebElement(selUtils.getObject("localizationnext_xpath"));
			logger.info("Selected a lowest level zone and next button");

			//select  'Manual' and add num comm data then click green button 
			logger.info("Step 8:");
			selUtils.clickOnWebElement(selUtils.getObject("numcommmanual_xpath"));
			selUtils.clickOnWebElement(selUtils.getObject("addnumcomm_xpath"));
			addNumcommval(NUMCOMLOC, numcomVal);
			selUtils.selectItem(selUtils.getObject("numert_id"), testDataOR.get("num_ert"));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObject("addnumcommgbutt_xpath"));
			vErrorMessage("numcommerr_id");
			selUtils.clickOnWebElement(selUtils.getObject("enternumcommnext_xpath"));
			logger.info("Added numcomm data "+ posVal[3]);
			logger.info("Selected manual option and added numcomm data then next button");

			//select  'Manual' and add pos details then click green button 
			logger.info("Step 9:");
			selUtils.clickOnWebElement(selUtils.getObject("enterposanual_xpath"));
			
			selUtils.clickOnWebElement(selUtils.getObject("addpos_xpath"));
			selUtils.getObject("posnumber_id").sendKeys(posNum);
			addPOSval(posLoc, posVal);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObject("addposgbutt_xpath"));
//			selUtils.clickOnWebElement(selUtils.getObject("addposgbutt_xpath"));
			selUtils.clickOnWebElement(selUtils.getObject("enterposnext_xpath"));
			reportErrMessage("pos_num_err_id");
			logger.info("Select manual and added pos details then next button");

			//Click 'Next' button
			logger.info("Step 10:");
			selUtils.scrollToView(selUtils.getObject("viewconfignext_id"));
			wait.until(ExpectedConditions.visibilityOf(selUtils.getObject("viewconfignext_id")));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObject("viewconfignext_id"));
			logger.info("Clicked on next button");

			//Enter a 'Project Name' and then click 'Save' button
			logger.info("Step 11:");
			waitMethods.waitForWebElementPresent(selUtils.getObject("projname_id"));
			selUtils.getObject("projname_id").sendKeys(projectName);
			selUtils.clickOnWebElement(selUtils.getObject("configsumsavebttn_xpath"));
			waitNSec(3);
			xpath=getPath("editpospending_xpath").replace("NAME", projectName);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			logger.info("Enetered project name and clicked on save button");

			//Go to "Pending Provisioning"  and verify 
			logger.info("Step 12:");
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), testDataOR.get("customer"));
			/*
			 * Above step is just a work around step,since we faced issue when 
			 * on pending provisioning tabclicking
			 */
			clkOnPendingProv(projectName);

			//edit the project in the "Pending Provisioning" and then deploy 
			logger.info("Step 13,14:");
			clkOnDirectObj("editpospending_xpath","NAME",projectName);
			logger.info("Clicked on the edit button in Pending Provisioning Tab for the project");
			clickOnDeploy();
			Assert.assertTrue(selUtils.getCommonObject("succ_deploymsg_xpath").getText().contains(SUCSSFULLYDPLYD),SUCSSFULLYDPLYD+"sucess message does not appear");
			logger.info("Verified the success message");
			//Click on the "Provisioning" tab and 
			//check that the Num comm  has been correctly created
			clckOnProvOrPosTab("provtab_xpath", PROVTAB);

			colIndex=selUtils.getIndexForColHeader("colheaders_css", NUMCOMMCOL);
			verifyLvlColLvlValPresence("provlst_css",colIndex,posVal[3]);
			logger.info("Verified the created numcomm data "+posVal[3]+" is displayed under Provisioning tab");

			if(dbCheck){
				//validating data with database
				// validate data in Eportal database
				sqlQuery = "SELECT * FROM pos WHERE pos='"+posNum+"'";
				resSet = dbMethods.getDataBaseVal(dbEportal,sqlQuery,CommonConstants.ONEMIN);
				pos=resSet.getString("pos");
				Assert.assertTrue(pos.equals(posNum), "Table data does not exist ");
				logger.info("pos number "+posNum+ " exists in table");

				// validate data in Eportal database
				sqlQuery = "SELECT * FROM merchantid WHERE merchantid='"+posVal[3]+"'";
				resSet = dbMethods.getDataBaseVal(dbEportal,sqlQuery,CommonConstants.ONEMIN);
				merchnId=resSet.getString("merchantid");
				Assert.assertTrue(merchnId.equals(posVal[3]), "Table data does not exist ");
				logger.info("merchantid="+posVal[3]+ " exists in table");

				// validate data in databaseAxis database
				sqlQuery="SELECT numtpv,numcomm FROM emv.tpvemv WHERE numtpv='"+posNum+"' AND numcomm='"+posVal[3]+"';";
				resSet = dbMethods.getDataBaseVal(dbAxis,sqlQuery,CommonConstants.ONEMIN);
				numptv=resSet.getString("numtpv");
				numcom=resSet.getString("numcomm");
				Assert.assertTrue(numptv.equals(posNum)&&numcom.equals(posVal[3]), "Table data does not exist ");
				logger.info("numtpv="+posNum+" And numcomm="+posVal[3]+ " exists in table");
			}

			logger.info("SMR50 executed successfully");	
		}
		catch (Throwable t) {
			handleException(t);
		}
	}

}



