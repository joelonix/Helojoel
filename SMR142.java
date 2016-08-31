package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR142.java $
$Id: SMR142.java 16708 2016-01-20 09:53:29Z rjadhav $
 */

import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;
/**
 *  SMR-142:Add POS to Numcomm (manually)
 * @author Joel
 */
public class SMR142  extends SuiteCardPayment{

	/**
	 * Add POS to Numcomm (manually)
	 */
	@Test
	public void smr142() {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR142 execution started");
			final String numComm=testDataOR.get("num_comm"),cust=testDataOR.get("customer")
					,posNum=testDataOR.get("pos_number"),dbEportal=testDataOR.get("databaseEportal"),dbAxis=testDataOR.get("databaseAxis")
					,pos,numptv,numcom;
			final String[] posLoc={"posrefund_xpath","posforcing_xpath","posvoid_xpath","posnumcomm_xpath"},
					posVal={testDataOR.get("refund_option"),testDataOR.get("forcing_option"),testDataOR.get("void_option"),numComm};
			
			//Access Everest with the Everest superuser
			//Navigate to Card Payment-Customer Provisioning and select the 
			//customer
			addProfToCust(cust);

			//Select 'Manual localization',pos and click Next	
			logger.info("Step 6:");
			selUtils.selectItem(selUtils.getObject("operationorder_id"), MANUALLOCALZTION);
			logger.info("Selected "+MANUALLOCALZTION);
			selUtils.clickOnWebElement(selUtils.getObject("pos_radiobttn_xpath"));
			selUtils.clickOnWebElement(selUtils.getObject("orderopenext_id"));

			//Select a lowest level zone and click Next
			logger.info("Step 7:");
			selUtils.selectItem(selUtils.getObject("localization_id"), testDataOR.get("lowest_level_zone_a"));
			selUtils.clickOnWebElement(selUtils.getObject("localizationnext_xpath"));
			logger.info("Selected a lowest level zone and next button");
			
			//Select the <num_comm> from the dropdown list,click 'Next' button
			logger.info("Step 8:");
			selUtils.selectItem(selUtils.getObject("numcomm_selection_id"), numComm);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObject("locnext_id"));
			logger.info("Selected numcomm zone and next button");

			
			//select manual pos,and add pos details
			logger.info("Step 9:");
			selUtils.slctChkBoxOrRadio(selUtils.getObject("enterposanual_xpath"));
			selUtils.clickOnWebElement(selUtils.getObject("addpos_xpath"));
			selUtils.getObject("posnumber_id").sendKeys(posNum);
			addPOSval(posLoc, posVal);
			
			//Edit projectname and deploy
			editPNameNDeply();
			
			if(dbCheck){
				//validating data with database,validate data in Eportal database
				sqlQuery = "SELECT * FROM pos WHERE pos='"+posNum+"'";
				resSet = dbMethods.getDataBaseVal(dbEportal,sqlQuery,CommonConstants.ONEMIN);
				pos=resSet.getString("pos");
				Assert.assertTrue(pos.equals(posNum), "Table data does not exist ");
				logger.info("pos number "+posNum+ " exists in table");

				//Validating the results in Axis database
				sqlQuery="SELECT numtpv,numcomm FROM emv.tpvemv WHERE numtpv='"+posNum+"' AND numcomm='"+numComm+"';";
				resSet = dbMethods.getDataBaseVal(dbAxis,sqlQuery,CommonConstants.ONEMIN);
				numptv=resSet.getString("numtpv");
				numcom=resSet.getString("numcomm");
				Assert.assertTrue(numptv.equals(posNum)&&numcom.equals(numComm), "Table data does not exist ");
			}
			
			logger.info("SMR142 executed successfully");
		}
		catch (Throwable t) {
			handleException(t);
		}
	}
	
}



