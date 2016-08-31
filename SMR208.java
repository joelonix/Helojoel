package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR208.java $
$Id: SMR208.java 18135 2016-04-19 10:32:34Z jsamuel $
 */


import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;

/**
 * SMR-208:Import POS with automatic localization
 * @author Joel.Samuel
 *
 */
public class SMR208 extends SuiteCardPayment{

	@Test(groups={"SMR208"})
	/**
	 * Import POS with automatic localization
	 */
	public void smr208()  {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR208 execution started");
			final String posNum=getDataFromCSV( CommonConstants.POSCSV,POSPOSNUM),projectName=testDataOR.get("project_name")
					,dbAxis=testDataOR.get("databaseAxis"),dbTam=testDataOR.get("databaseTAM"),dbIngestate=testDataOR.get("databaseIngEstate"),
					numptv,signature,name;

			//Access Everest with a superuser,Card Payment,CustomerProvisioning  
			//and select the customer
			logger.info("Step 1,2:");
			navigateToSubPage(CUSTPROV,selUtils.getCommonObject("cardpaymt_tab_xpath"),selUtils.getCommonObject("custprov_xpath"));
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), testDataOR.get("customer"));

			//verify and delete existing project name for pos creation
			selUtils.clickOnWebElement(selUtils.getCommonObject("pendingprovtab_xpath"));
			verifyExistingData("deleteprojectname_xpath","colheaders_css","colallrows_xpath",projectName, PROJECTNAME);

			//Go to POS Declaration and click the "+" button
			logger.info("Step 3:");
			selUtils.clickOnWebElement(selUtils.getObject("posdecltab_xpath"));
			selUtils.clickOnWebElement(selUtils.getObject("addprov_xpath"));
			logger.info("Clicked on pos declaration tab,and plus button");

			//If  there are more than one Axis location select axis_location
			//And click next button
			logger.info("Step 4:");
			selAxisLoc(testDataOR.get("axis_location"));
			
			//Select an existing ADM profile and clickNext 
			logger.info("Step 5:");
			selUtils.selectItem(selUtils.getObject("profile_id"), testDataOR.get("admfr_profile").replaceAll(".zip", "").trim());
			logger.info("Selected the profile as "+ (testDataOR.get("admfr_profile").replaceAll(".zip", "")).trim());
			selUtils.clickOnWebElement(selUtils.getObject("profilenext_id"));
			logger.info("Clicked on next button");

			//Select 'Automatic localization' and click 'Next'
			logger.info("Step 6:");
			selUtils.selectItem(selUtils.getObject("operationorder_id"), AUTOLOCALZN);
			logger.info("Selected '"+AUTOLOCALZN);
			selUtils.clickOnWebElement(selUtils.getObject("orderopenext_id"));
			logger.info("Clicked on next button");

			//Select the CSV file <csv_file> and click Next
			logger.info("Step 7:");
			//selUtils.populateInputBox("loadcsv_input_id", CommonConstants.POSCSV );
			selUtils.getObject("loadcsv_input_id").sendKeys(CommonConstants.POSCSV);
			logger.info("Uploaded pos csv file");
			selUtils.clickOnWebElement(selUtils.getObject("loadposnumnxt_bttn_id"));
			logger.info("Clicked on load button");
			reportErrMessage("pos_num_err_id");

			//Click Next
			logger.info("Step 8:");
			selUtils.clickOnWebElement(selUtils.getObject("viewconfignext_id"));
			logger.info("Clicked on next button");

			//Enter a 'Project Name'and save
			logger.info("Step 9:");
			selUtils.populateInputBox("projname_id", projectName);
			logger.info("Entered the projectName as "+projectName);
			selUtils.clickOnWebElement(selUtils.getObject("configsumsavebttn_xpath"));
			logger.info("Clicked on save button");

			//Go to "Pending Provisioning" 
			logger.info("Step 10,11:");
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), testDataOR.get("customer"));
			
			/* * Above step is just a work around step,since we faced issue when 
			 * on pending provisioning tabclicking*/
			 

			//Click the edit button of the pending operation 
			clkOnPendingProv(projectName);
			clkOnDirectObj("editpospending_xpath","NAME",projectName);
			logger.info("Clicked on the edit button");

			//click on the 'Deploy' and click on POS tab
			logger.info("Step 12:");
			clickOnDeploy();
			Assert.assertTrue(selUtils.getCommonObject("succ_deploymsg_xpath").getText().contains(SUCSSFULLYDPLYD),SUCSSFULLYDPLYD+"sucess message does not appear");
			logger.info("Verified the success message");
			clckOnProvOrPosTab("posdecltab_xpath", POSDECTAB);

			//get value from CSV
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#tabListPos>tbody")));
			colIndex=selUtils.getIndexForColHeader("colheaders_css", POSNUMBERCOL);
			verifyLvlColLvlValPresence("provPoslst_css",colIndex,posNum);
			logger.info("Verified the PosNumber value  "+posNum+" is displayed in the Provisioning list");

			if(dbCheck){
				//POS imported from csv file are created in the axis_db
				String sql= "SELECT numtpv FROM adm.tpvadm WHERE numtpv='"+posNum+"';";
				resSet = dbMethods.getDataBaseVal(dbAxis,sql,CommonConstants.ONEMIN);
				numptv=resSet.getString("numtpv");
				Assert.assertTrue(numptv.equals(posNum), posNum+" value is not stored in database");
				logger.info("Verified pos column is having  "+ posNum +" in the database");
				
				//Verifying the Created posNumber TAM database
				sql= "SELECT signature FROM ingenico_terminal WHERE signature LIKE '%"+posNum+"';";
				resSet = dbMethods.getDataBaseVal(dbTam,sql,CommonConstants.ONEMIN);
				signature=resSet.getString("signature");
				Assert.assertTrue(signature.endsWith(posNum), posNum+" value is not stored in database");
				logger.info("Verified signature column is finishing by "+ posNum +" in the database");

				//Verifying the Created posNumber in IngEstate Databases
				sql="SELECT name FROM terminal WHERE name LIKE '%"+posNum+"';";
				resSet = dbMethods.getDataBaseVal(dbIngestate,sql,CommonConstants.ONEMIN);
				name=resSet.getString("name");
				Assert.assertTrue(name.endsWith(posNum), posNum+" value is not stored in database");
				logger.info("Verified name column is finishing by "+ posNum +" in the database");
			}

			logger.info("SMR208 execution is successful");
		}
		catch (Throwable t) {
			handleException(t);
		}

	}
}



