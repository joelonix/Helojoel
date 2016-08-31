package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR151.java $
$Id: SMR151.java 16708 2016-01-20 09:53:29Z rjadhav $
 */

import org.testng.annotations.Test;

/**
 * SMR-151:Update the data of a POS through the modification of the 
 * 'axconfig.dat' file
 * @author Joel 
 *
 */

public class SMR151  extends SuiteCardPayment{

	@Test(groups={"SMR151"})

	/**
	 * Update the data of a POS through the modification of the 'axconfig.dat' 
	 * file
	 */
	public void smr151() {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR151 execution started");	
			posNum=testDataOR.get("pos_number");
			projectName=testDataOR.get("project_name");

			//Access Everest with a superuser
			//Go to "Card Payment >> Customer Provisioning" sub menu
			logger.info("Step 1, 2, 3:");
			vProjNameinProv(projectName);

			//Click the "Details" button of a numcomm <num_comm> with 
			//associated POSs
			logger.info("Step 4:");
			clkOnDirectObj("numcom_details_xpath","NUMCOMVAL",testDataOR.get("num_comm"));
			logger.info("Clicked on the details button of numcomm");

			//Click the "Edit" button of a POS <pos_number>
			logger.info("Step 5:");
			clkOnDirectObj("pos_edit_xpath","POSNUM",posNum);
			logger.info("Clicked on edit button of posnumber "+posNum);

			//Click on the 'Next' button
			logger.info("Step 6,7,8:");
			enterPNameAndVal(projectName);

			//Click the edit button of the pending operation <project_name>
			logger.info("Step 9:");
			clkOnDirectObj("editpospending_xpath","NAME",projectName);
			logger.info("Clicked on the edit button");

			//click on the 'Deploy' and Ok buttons
			logger.info("Step 10:");
			vProjNotExistInPendingProv(projectName);

			//<pos_number> is still present in the "Details" part of <num_comm>
			selUtils.clickOnWebElement(selUtils.getObject("provtab_xpath"));
			logger.info("Clicked on Provisioning tab");

			clkOnDirectObj("numcom_details_xpath","NUMCOMVAL",testDataOR.get("num_comm"));
			logger.info("Clicked on details link of the numcom");

			colIndex=selUtils.getIndexForColHeader("colheaders_css", POSNUMBERCOL);
			verifyLvlColLvlValPresence("provPoslst_css",colIndex,posNum);
			logger.info("Verified that posnumber  "+posNum+" is still present in the details part of numcomm");

			logger.info("SMR151 executed successfully");	
		}
		catch (Throwable t) {
			handleException(t);
		}
	}

}
