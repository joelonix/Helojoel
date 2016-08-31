package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR143.java $
$Id: SMR143.java 18135 2016-04-19 10:32:34Z jsamuel $
 */
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;

/**
 * SMR-143: Import POS into Numcomm
 * @author Hariprasad.KS 
 *
 */

public class SMR143  extends SuiteCardPayment{

	@Test(groups={"SMR143"})
	/**
	 * Import POS into Numcomm
	 */
	public void smr143() {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR143 execution started");	
			final String posNumCSV=getDataFromCSV( CommonConstants.POSTONUMCOMMCSV,POSPOSNUM).trim(),projectName=testDataOR.get("project_name"),
					lowestZone=testDataOR.get("lowest_level_zone_a"),customer=testDataOR.get("customer"),
					numcommFromCSV=getDataFromCSV(CommonConstants.NUMCOMMCSV,NUMCOMM).trim(),dbEportal=testDataOR.get("databaseEportal"),
					dbAxis=testDataOR.get("databaseAxis"),pos,numptv,numcom;

			//Access Everest with a superuser
			//Go to "Card Payment >> Customer Provisioning" sub menu
			logger.info("Step 1, 2:");
			// Common steps for smr143 and smr50
			selProfToCust();
		
			selUtils.clickOnWebElement(selUtils.getObject("pos_radiobttn_xpath"));
			selUtils.clickOnWebElement(selUtils.getObject("orderopenext_id"));
			logger.info("Selected POS option and then clicked next button");

			//Select a lowest level zone  and then click next button
			logger.info("Step 7:");
			selUtils.selectItem(selUtils.getObject("localization_id"), lowestZone);
			selUtils.clickOnWebElement(selUtils.getObject("localizationnext_xpath"));
			logger.info("Selected a lowest level zone and next button");

			//select  'Manual' and add num comm data then click green button 
			logger.info("Step 8:");
			selUtils.selectItem(selUtils.getObject("numcomm_selection_id"),numcommFromCSV);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObject("numcomm_next_xpath"));
			//selUtils.clickOnWebElement(selUtils.getObject("numcomm_next_xpath"));
			logger.info("Selected "+numcommFromCSV+" from csv and then clicked next button");

			//Select 'Load from CSV file', select the POS CSV file N click next
			logger.info("Step 9:");
			selUtils.clickOnWebElement(selUtils.getObject("loadposcsv_xpath"));
			selUtils.getObject("poscsvload_id").sendKeys(CommonConstants.POSTONUMCOMMCSV);
			//selUtils.populateInputBox("poscsvload_id", CommonConstants.POSTONUMCOMMCSV );
			selUtils.clickOnWebElement(selUtils.getObject("enterposnext_xpath"));
			reportErrMessage("pos_num_err_id");
			logger.info("Loaded CSV file by selecting Load from CSV file option");

			//Click 'Next' button
			logger.info("Step 10:");
			selUtils.clickOnWebElement(selUtils.getObject("viewconfignext_id"));
			logger.info("Clicked on next button");

			//Enter a 'Project Name' and then click 'Save' button
			logger.info("Step 11:");
			selUtils.getObject("projname_id").sendKeys(projectName);
			selUtils.clickOnWebElement(selUtils.getObject("configsumsavebttn_xpath"));
			waitNSec(3);
			logger.info("Enetered project name and clicked on save button");

			//Go to "Pending Provisioning"  and verify 
			logger.info("Step 12:");
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), customer);
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
			Assert.assertTrue(selUtils.getCommonObject("succ_deploymsg_xpath").getText().contains(SUCSSFULLYDPLYD));
			logger.info("Verified the success message");
			
			if(dbCheck){
				//validating data with database
				// validate data in Eportal database
				sqlQuery = "SELECT * FROM pos WHERE pos='"+posNumCSV+"'";
				resSet=dbMethods.getDataBaseVal(dbEportal,sqlQuery,CommonConstants.ONEMIN);
				pos=resSet.getString("pos");
				Assert.assertTrue(pos.equals(posNumCSV), "Table data does not exist ");
				logger.info("pos number "+posNumCSV+ " exists in table");

				// validate data in databaseAxis database
				sqlQuery="SELECT numtpv,numcomm FROM emv.tpvemv WHERE numtpv='"+posNumCSV+"' AND numcomm='"+numcommFromCSV+"';";
				resSet=dbMethods.getDataBaseVal(dbAxis,sqlQuery,CommonConstants.ONEMIN);
				numptv=resSet.getString("numtpv");
				numcom=resSet.getString("numcomm");
				Assert.assertTrue(numptv.equals(posNumCSV)&&numcom.equals(numcommFromCSV), "Table data does not exist ");
				logger.info("numtpv="+posNumCSV+" And numcomm="+numcommFromCSV+ " exists in table");
			}

			logger.info("SMR143 executed successfully");	
		}
		catch (Throwable t) {
			handleException(t);
		}
	}

}



