package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/prov/testsuite/entitymanagement/E2E14.java $
$Id: E2E14.java 11738 2014-12-23 09:46:07Z jsamuel $
 */


import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;

/**
 *   SMR-187:Add Terminal
 * @author Joel.Samuel
 *
 */
public class SMR187 extends SuiteCardPayment{
	
	/**
	 * Add Terminal in White List
	 */
	@Test(groups={"SMR187"})
	public void smr187()  {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR187 execution started");
			final String serNo=testDataOR.get("serial_number"),dbAxis=testDataOR.get("databaseAxis"),noserie;
			
			//Access Everest with a superuser,Card Payment>>Customer 
			//Provisioning and select the customer
			logger.info("Step 1,2:");
			navigateToSubPage(CUSTPROV,selUtils.getCommonObject("cardpaymt_tab_xpath"),selUtils.getCommonObject("custprov_xpath"));
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), testDataOR.get("customer"));

			//Goto White List select an Axis Location  and click on "+" button
			logger.info("Step 3:");
			selUtils.clickOnWebElement(selUtils.getObject("whitelisttab_xpath"));
			selUtils.selectItem(selUtils.getObject("axisloc_sel_id"), testDataOR.get("axis_location"));
			selUtils.clickOnWebElement(selUtils.getCommonObject("plusbtn_xpath"));
			logger.info("Selected axis location " +testDataOR.get("axis_location")+"  and clicked on Plus button");

			//Enter serial number <serial_number>,click on the 'OK' button 
			logger.info("Step 4:");
			selUtils.clickOnWebElement(selUtils.getCommonObject("plusbtn_xpath"));
			selUtils.populateInputBox("witlis_serno_id", serNo);
			selUtils.clickOnWebElement(selUtils.getCommonObject("okbttn_xpath"));
			logger.info("Terminal " +serNo+" created successfully");
			colIndex=selUtils.getIndexForColHeader("colheaders_css",SERNOCOL);
			verifyLvlColLvlValPresence("entitytablelst_css",colIndex,serNo);

			if(dbCheck){
				sqlQuery = "SELECT noserie FROM  adm.serial_number WHERE noserie="+"\'"+serNo+"\'";
				resSet = dbMethods.getDataBaseVal(dbAxis,sqlQuery,CommonConstants.ONEMIN);
				noserie=resSet.getString("noserie");
				Assert.assertTrue(noserie.equalsIgnoreCase(serNo), serNo+ "does not exist in the database");
				logger.info(serNo+ " exists in the database");
			}

			logger.info("SMR187 execution successful");
		}
		catch (Throwable t) {
			handleException(t);
		}

	}

}



