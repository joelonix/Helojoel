package com.ingenico.testsuite.customermanagement;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/customermanagement/SMR1872.java $
$Id: SMR1872.java 16708 2016-01-20 09:53:29Z rjadhav $
 */

import java.sql.SQLException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;

/**
 *  SMR-1872:undeployed customer - Administrative and ePortal settings
 * @author Joel.Samuel
 *
 */
public class SMR1872 extends SuiteCustomerManagement{

	/**
	 * class level variable declaration
	 */
	private String customerID;

	/**
	 * Create generic customer,@param dburl
	 * @param browser
	 */
	@Test(groups="SMR1872")
	public void smr1872()  {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1872 execution started");
			final String epOtnsYamlVal[]={testDataOR.get("card_payment"),testDataOR.get("gprs"),testDataOR.get("tms"),testDataOR.get("asset_tracking")};
			final String custName=testDataOR.get("customer"),dbUM=testDataOR.get("databaseUM");

			//Access Everest with a superuser,Go to "Customer" module, and 
			//click "Add New Customer"
			logger.info("Step 1,2:");
			navigateToCustomerPage();
			waitMethods.waitForWebElementPresent(selUtils.getCommonObject("displaytxt_xpath"));

			//Delete existing customer
			//serchNDelExstingCust(custName,"del_evecust_xpath",custName);
			//verifyExistingData("del_evecust_xpath",custName,NAMECOL);

			//Create customer and verify Customer id and Everest Customer id
			logger.info("Step 3:");
			createCustomer(custName,testDataOR.get("customer_sap"));
			customerID=selUtils.getObject("custid_minus_xpath").getText();
			logger.info("Customer with name, '"+custName+" ' is created successfully in the customer list");
			Assert.assertTrue(vCustEvID(),"Customer id and Everest Customer id are not set in the form");

			//Verify created customer is displayed in the list
			selUtils.getCommonObject("cust_tab_link").click();
			colIndex=selUtils.getIndexForColHeader("colheaders_css",NAMECOL);
			verifyLvlColLvlValPresence("entitytablelst_css",colIndex,custName);
			
			if(dbCheck){
				//Verify created customer is updated in database
				vCreatedCustInDB(customerID,dbUM);
			}

			//Navigate to "e-Portal" module, and verify the customer 			
			logger.info("Step 4:");
			vEportalPage(custName,LANGFRAN,epOtnsYamlVal);

			logger.info("SMR1872 execution successful");
		}
		catch (Throwable t) {
			handleException(t);
		}

	}

	/**
	 * Method to verify CustID and EverestCustID
	 * @return
	 */
	private boolean vCustEvID(){
        String everCustID;
		exists=false;
		customerID=selUtils.getObject("custid_minus_xpath").getText().trim();
		everCustID=selUtils.getObject("ever_custid_minus_xpath").getText().trim();
		if(customerID.isEmpty()&&everCustID.isEmpty()){
			Assert.fail("customer id '"+customerID+"' and Everest customer id '"+everCustID+"' are not set in the form");
		}else{
			exists=true;
			logger.info("Verified customer id '"+customerID+"' and Everest customer id '"+everCustID+"' are set in the form");
		}

		return exists;
	}

	/**
	 * Method to verify created customer exists in DB,@param dburl@param custid
	 * @throws SQLException
	 */
	private void vCreatedCustInDB(String custid,String umDB) throws SQLException{
			String vCustIdCol;
		    sqlQuery = "SELECT customer_id FROM um.customer_properties WHERE customer_id="+"\'"+custid+"\'";
		    resSet = dbMethods.getDataBaseVal(umDB,sqlQuery,CommonConstants.ONEMIN);
		    vCustIdCol=resSet.getString("customer_id");
			Assert.assertTrue(vCustIdCol.equalsIgnoreCase(custid), custid+ " does not exist in customer_id col in the database");
			logger.info("Customer ID, '"+custid+ "' exists in the customer_id col in the database");
	}
	

}



