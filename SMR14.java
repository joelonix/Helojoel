package com.ingenico.testsuite.entitymanagement;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/entitymanagement/SMR14.java $
$Id: SMR14.java 16708 2016-01-20 09:53:29Z rjadhav $
 */

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;

/**
 *  SMR-14:Create Entity
 * @author Joel.Samuel
 *
 */
public class SMR14 extends SuiteEntityManagement{

	/**
	 * Create an entity from Everest and checking the creation in the other 
	 * components.
	 */
	@Test(groups="SMR14")
	public void smr14()  {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);

			logger.info("SMR14 execution started");
			final String entity;
			//Access Everest with a superuser login,Go to "Customer" module
			logger.info("Step 1,2:");
			navigateToCustomerPage();

			//Go to 'Entity' and 'View Entities' sub-tab
			logger.info("Step 3:");
			selUtils.clickOnWebElement(selUtils.getObject("entity_plusbtn_xpath"));
			selUtils.clickOnWebElement(selUtils.getObject("viv_entities_link"));
			logger.info("Navigated to 'View Entities' Page");

			//Create Entity by entering entity name  and SAP number 
			logger.info("Step 4:");
			selUtils.clickOnWebElement(selUtils.getCommonObject("plusbtn_xpath"));
			entityName=addEntity();
			colIndex=selUtils.getIndexForColHeader("colheaders_css", ENTITYCOLNAME);
			verifyLvlColLvlValPresence("entitytablelst_css",colIndex,entityName);
			selUtils.verifyObjDirectDisp("tambttn_xpath","ENTITYNAME", entityName);

			if(dbCheck){
				sqlQuery = "SELECT group_name FROM  party_group WHERE group_name="+"\'"+entityName+"\'";
				resSet = dbMethods.getDataBaseVal(testDataOR.get("databaseTAM"),sqlQuery,CommonConstants.ONEMIN);
				entity=resSet.getString("group_name");
				Assert.assertTrue(entity.equalsIgnoreCase(entityName), entityName+ " does not exist in the database");
				logger.info("'"+entityName+ "' exists in the database");
			}
			logger.info("SMR14 execution successful");
		}
		catch (Throwable t) {
			handleException(t);
		}

	}

}



