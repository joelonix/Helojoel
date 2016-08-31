package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR136.java $
$Id: SMR136.java 18135 2016-04-19 10:32:34Z jsamuel $
 */

import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;
/**
 * SMR-136:Import Numcomm & POS(Automatic localization)
 * @author Nagaveni.Guttula
 *
 */
public class SMR136  extends SuiteCardPayment{


	/**
	 * Import Numcomm with an existing POS (defined in CSV file)
	 */
	@Test(groups={"SMR136"})
	public void smr136() {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR136 execution started");

			final String numComm=getDataFromCSV( CommonConstants.POSNUMCOMMCSV,NUMCOMM),projectName=testDataOR.get("project_name"),cust=testDataOR.get("customer"),
					dbEportal=testDataOR.get("databaseEportal"),dbAxis=testDataOR.get("databaseAxis"),pos,merchnId,numptv,numcom;
			posPosNumHeader=getDataFromCSV( CommonConstants.POSCSV,POSPOSNUM);

			//Access Everest with the Everest superuser
			//Navigate to Card Payment-Customer Provisioning and 
			//select the customer
			addProfToCust(cust);

			//Select 'Automatic localization' in the "Order" drop down list			
			logger.info("Step 6:");
			selUtils.selectItem(selUtils.getObject("operationorder_id"), AUTOLOCALZN);
			logger.info("Selected "+AUTOLOCALZN);
			selUtils.clickOnWebElement(selUtils.getObject("orderopenext_id"));

			//Step 6: Select the CSV file <csv_file> and click Next
			logger.info("Step 7:");
			//selUtils.populateInputBox("loadcsv_input_id", CommonConstants.POSNUMCOMMCSV );
			selUtils.getObject("loadcsv_input_id").sendKeys(CommonConstants.POSNUMCOMMCSV);
			selUtils.clickOnWebElement(selUtils.getObject("loadposnumnxt_bttn_id"));
			reportErrMessage("pos_num_err_id");
			logger.info("Selected the CSV file and clicked next button");

			//Click Next		
			logger.info("Step 8:");
			selUtils.clickOnWebElement(selUtils.getObject("viewconfignext_id"));
			logger.info("Clicked next button of view config window");

			//Enter a 'Project Name' and click on the 'Save' button			
			logger.info("Step 9:");
			selUtils.populateInputBox("projname_id", projectName);
			logger.info("Entered the projectName as "+projectName);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();",selUtils.getObject("configsumsavebttn_xpath"));
			//selUtils.clickOnWebElement(selUtils.getObject("configsumsavebttn_xpath"));
			waitMethods.waitForWebElementPresent(selUtils.getObject("pendingprov_displaytxt_xpath"));
			logger.info("Entered the project name and clicked save button");
			
			//Go to "Pending Provisioning" sub-tab		
			logger.info("Step 10,11:");
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), testDataOR.get("customer"));
			
			/* * Above step is just a work around step,since we faced issue when 
			 * on pending provisioning tabclicking
			 */

			//Click the edit button of the pending operation <project_name>			
			clkOnPendingProv(projectName);
			clkOnDirectObj("editpospending_xpath","NAME",projectName);
			logger.info("Clicked on the edit button");

			//Deploy the request,click on prov tab			
			logger.info("Step 12:");
			clickOnDeploy();
			Assert.assertTrue(selUtils.getCommonObject("succ_deploymsg_xpath").getText().contains(SUCSSFULLYDPLYD),SUCSSFULLYDPLYD+"success message does not appear");
			logger.info("Verified the success message");
			clckOnProvOrPosTab("provtab_xpath",PROVTAB);

			/*searchVal(numComm, "numcommfilter_id", VALEQ, "numcomminput_id");*/
			colIndex=selUtils.getIndexForColHeader("colheaders_css", NUMCOMMCOL);
			verifyLvlColLvlValPresence("provlst_css",colIndex,numComm);
			logger.info("Verified the Numcomm value  "+numComm+" is displayed in the  Provisioning list");
			
			if(dbCheck){
				//pos_numbers of the csv file are created in the "pos" table of the ePortal db	
				String sql= "SELECT pos FROM pos WHERE pos='"+posPosNumHeader+"';";
				resSet = dbMethods.getDataBaseVal(dbEportal,sql,CommonConstants.ONEMIN);
				pos=resSet.getString("pos");
				Assert.assertTrue(pos.equals(posPosNumHeader), posPosNumHeader+" value is not stored in database");
				logger.info("Verified pos column is having  "+ posPosNumHeader +" in the database");

				//num_comms(csv)  are created in the merchantid table of the eportaldb
				sql= "SELECT merchantid FROM merchantid WHERE merchantid='"+numComm+"';";
				resSet = dbMethods.getDataBaseVal(dbEportal,sql,CommonConstants.ONEMIN);
				merchnId=resSet.getString("merchantid");
				Assert.assertTrue(merchnId.equals(numComm), numComm+" value is not stored in database");
				logger.info("Verified merchantid column is having "+ numComm +" in the database");

				//pos_numbers,numcomm (CSV) are created in the 'emv.tpvemv' table of the axisdb
				sql="SELECT numtpv,numcomm FROM emv.tpvemv WHERE numtpv='"+posPosNumHeader+"' AND numcomm='"+numComm+"';";
				resSet = dbMethods.getDataBaseVal(dbAxis,sql,CommonConstants.ONEMIN);
				numptv=resSet.getString("numtpv");
				numcom=resSet.getString("numcomm");
				Assert.assertTrue(numptv.equals(posPosNumHeader)&&numcom.equals(numComm), posPosNumHeader+"and"+numComm+" value is not stored in database");
				logger.info("Verified numtpv column and numcomm column is having "+ posPosNumHeader+"and "+ numComm);
			}
			
			logger.info("SMR136 executed successfully");
		}
		catch (Throwable t) {
			handleException(t);
		}
	}

}










