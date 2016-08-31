package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR174.java $
$Id: SMR174.java 16708 2016-01-20 09:53:29Z rjadhav $
 */

import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.base.SelUtils;
import com.ingenico.common.CommonConstants;

/**
 *  SMR-174:Override POS
 * @author Joel
 */
public class SMR174  extends SuiteCardPayment{

	/**
	 * Override POS
	 */
	@Test(groups="SMR174")
	public void smr174() {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR174 execution started");
			final String numComm=getDataFromCSV(CommonConstants.NUMCOMMCSV ,NUMCOMM).trim(),cust=testDataOR.get("customer"),posNum=testDataOR.get("pos_number"),refOption=testDataOR.get("refund_option")
					,forcOption=testDataOR.get("forcing_option"),voidOption=testDataOR.get("void_option")
					,axisDataBase=testDataOR.get("databaseAxis"),numptv,credit,forcage,annultype;
			final String[] posLoc={"posrefund_xpath","posforcing_xpath","posvoid_xpath","posnumcomm_xpath"},
					posVal={refOption,forcOption,voidOption,numComm};

			//Access Everest with the Everest superuser,Navigate to Card Payment
			//-Customer Provisioning and select the customer
			addProfToCust(cust);

			//Select 'Manual localization',pos and click Next	
			logger.info("Step 6:");
			selUtils.selectItem(selUtils.getObject("operationorder_id"), MANUALLOCALZTION);
			logger.info("Selected "+MANUALLOCALZTION);
			selUtils.clickOnWebElement(selUtils.getObject("pos_radiobttn_xpath"));
			selUtils.clickOnWebElement(selUtils.getObject("ovrrideckbx_id"));
			selUtils.clickOnWebElement(selUtils.getObject("orderopenext_id"));

			//Select a lowest level zone and click Next
			logger.info("Step 7:");
			selUtils.selectItem(selUtils.getObject("localization_id"), testDataOR.get("lowest_level_zone_a"));
			selUtils.clickOnWebElement(selUtils.getObject("localizationnext_xpath"));
			logger.info("Selected a lowest level zone and next button");

			//Select the <num_comm> from the dropdown list,click 'Next' button
			logger.info("Step 8:");
			selUtils.selectItem(selUtils.getObject("numcomm_selection_id"), numComm);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObject("numcomm_next_xpath"));
			//selUtils.clickOnWebElement(selUtils.getObject("numcomm_next_xpath"));
			logger.info("Selected numcomm zone and next button");

			//select manual pos,and add pos details
			logger.info("Step 9:");
			selUtils.slctChkBoxOrRadio(selUtils.getObject("enterposanual_xpath"));
			selUtils.clickOnWebElement(selUtils.getObject("addpos_xpath"));
			selUtils.getObject("posnumber_id").sendKeys(posNum);
			addPOSval(posLoc, posVal);

			//Fetching all refund,forcing and void options from the application 
			refVal=SelUtils.getSelectedItem(selUtils.getObject("posrefund_id"));
			forVal=SelUtils.getSelectedItem(selUtils.getObject("posforcing_id"));
			voidVal=SelUtils.getSelectedItem(selUtils.getObject("posvoid_id"));

			refVal=refVal.substring(refVal.indexOf("(")+1, refVal.indexOf(")"));


			forVal=forVal.substring(forVal.indexOf("(")+1, forVal.indexOf(")"));


			voidVal=voidVal.substring(voidVal.indexOf("(")+1, voidVal.indexOf(")"));

			//Edit projectname and deploy
			editPNameNDeply();

			if(dbCheck){
				sqlQuery="SELECT numtpv,credit,forcage,annultype FROM emv.tpvemv WHERE numtpv='"+posNum+"' AND credit='"+refVal+"' AND forcage='"+forVal+"' AND annultype='"+voidVal+"';";
				resSet = dbMethods.getDataBaseVal(axisDataBase,sqlQuery,CommonConstants.ONEMIN);
				numptv=resSet.getString("numtpv");
				credit=resSet.getString("credit");
				forcage=resSet.getString("forcage");
				annultype=resSet.getString("annultype");
				boolean vDBValues=numptv.equals(posNum)&&credit.equals(refVal.trim())&&forcage.equals(forVal.trim())&&annultype.equals(voidVal.trim());
				Assert.assertTrue(vDBValues, "Table data does not exist");
				logger.info("Verified all the column values in the Table");
			}

			logger.info("SMR174 executed successfully");
		}
		catch (Throwable t) {
			handleException(t);
		}
	}

}
