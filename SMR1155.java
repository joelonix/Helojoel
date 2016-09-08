package com.ingenico.testsuite.customermanagement;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/customermanagement/SMR1155.java $
$Id: SMR1155.java 16708 2016-01-20 09:53:29Z rjadhav $
 */

import java.sql.SQLException;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;

/**
 * SMR-1155:Deploy Customer
 * Deploy a customer from Everest and checking the deployment in the other components
 * Preconditions:
 * Preconditions
 * An undeployed customer <customer> exists
 * The Customer structure and ePortal options in "ePortal >> ePortal Provisioning" must be set for the customer (as described in tests E2E-238 and E2E-49)
 * @author Hariprasad.KS
 *
 */

public class SMR1155 extends SuiteCustomerManagement{

	/**
	 * Deploy Customer
	 */
	@Test(groups="SMR1155")
	public void smr1155(){
		try {
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			final String custName,clientID,dbEportal=testDataOR.get("databaseEportal"),dbTAM=testDataOR.get("databaseTAM"),
					     dbIngestate=testDataOR.get("databaseIngEstate");
			final int zoneLevel;
			final String [] zoneNames;
			boolean vDBClos;

			logger.info("SMR1155 execution started");
			custName=testDataOR.get("customer");
			zoneNames=new String[]{testDataOR.get("zone_nameone_a"),testDataOR.get("zone_nametwo_a"),testDataOR.get("zone_namethree_a")};
			zoneLevel=Integer.parseInt(testDataOR.get("depth"));

			// Access Everest with a superuser,Go to "Customer" module
			logger.info("Step 1,2:");
			navigateToCustomerPage();

			//Click the "e-Portal" button of the <customer> to be deployed
			logger.info("Step 3 :");
			logger.info("Deploying customer "+custName);
			colIndex=selUtils.getIndexForColHeader("colheaders_css", NAMECOL);
			verifyLvlColLvlValPresence("entitytablelst_css", colIndex, custName);
			//get client ID
			locPath=getPath("custdeployrow_xpath").replace(CUSTNAME, custName);
			waitMethods.waitForWebElementPresent(selUtils.getObjectDirect(By.xpath(locPath)));
			clientID=selUtils.getObjectDirect(By.xpath(locPath)).getText().trim();
			locPath=getPath("custandprodlist_xpath").replace(CUSTNAME, custName);
			selUtils.clickOnWebElement(selUtils.getObjectDirect(By.xpath(locPath)));

			//validate deployed customer with application
			logger.info("Step 4 :");
			selUtils.acceptAlert();
			Assert.assertFalse(selUtils.getCommonObject("posheder_errmsg_id").getAttribute("class").endsWith("errorMessage"),"error on deploy eportal customer");
			selUtils.vDirectEleDisplayed("custeportaldeploy_xpath", CUSTNAME, custName);
			selUtils.vDirectEleDisplayed("custtamdeploy_xpath", CUSTNAME, custName);
			logger.info("Validated deployed customer with the application");

			if(dbCheck){
				// Verify eportal database
				for(int i=0;i<zoneLevel;i++)
				{
					sqlQuery = "SELECT id_client,org_name FROM organization_structure WHERE id_client='"+clientID+"' AND org_name='"+zoneNames[i]+"'";
					resSet = dbMethods.getDataBaseVal(dbEportal,sqlQuery,CommonConstants.ONEMIN);
					vDBClos=resSet.getString("id_client").equals(clientID)&&resSet.getString("org_name").equals(zoneNames[i]);
					Assert.assertTrue(vDBClos, "Table data does not Exist ");
					
				}
				logger.info("ePortal database has new record with customer, "+custName+ "' and all the zones of the customer ");
				
				// Verify TAM database
				sqlQuery = "SELECT name FROM ingenico_estate WHERE name='"+custName+"'";
				vNewRecordInDB(dbTAM,sqlQuery, "name",custName);

				//Verify ingestate database
				sqlQuery = "SELECT sponsor_name FROM sponsor WHERE sponsor_name='"+custName+"'";
				vNewRecordInDB(dbIngestate,sqlQuery,"sponsor_name", custName);
			}

			logger.info("SMR1155 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}

	}

	/**
	 * validate new records present in the database
	 * @param databaseurl
	 * @param query
	 * @param custName
	 * @author Hariprasad.KS
	 */
	private void vNewRecordInDB(String databaseurl,String query,String name,String custName)
	{
		String vCustNameCol;
		try {
			resSet = dbMethods.getDataBaseVal(databaseurl,query,CommonConstants.ONEMIN);
			vCustNameCol=resSet.getString(name);
			Assert.assertTrue(vCustNameCol.equals(custName), custName+" data does not Exist in the database");
			logger.info("Database has new record with customer, '"+custName+"'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
