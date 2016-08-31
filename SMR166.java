package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR166.java $
$Id: SMR166.java 16708 2016-01-20 09:53:29Z rjadhav $
 */


import org.testng.annotations.Test;

/**
 * SMR-166:Edit Numcomm
 * @author Hariprasad.KS 
 *
 */
public class SMR166  extends SuiteCardPayment{


	/**
	 * Edit Numcomm by updating the 'axconfig.dat' file content
	 */
	@Test(groups={"SMR166"})
	public void smr166() {
		try{
			
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR166 execution started");	
			final String numcommval=testDataOR.get("num_comm"),projectName=testDataOR.get("project_name");

			//Access Everest with a superuser,Navigate to Provisioning sub tab
			logger.info("Step 1,2,3:");
			vProjNameinProv(projectName);

			//Click on Edit numcomm button  of a merchant numcomm
			logger.info("Step 4:");
			clkOnDirectObj("editnumcomm_xpath","NAME",numcommval);
			logger.info("Clicked on numcomm "+numcommval+" edit button");

			//Click next button and enter project name then go to pending prov
			// and validate project name in the list
			logger.info("Step 5,6,7:");
			enterPNameAndVal(projectName);

			//Edit project in the pending prov list and deploy
			logger.info("Step 8,9:");
			clkOnDirectObj("editpospending_xpath","NAME",projectName);
			logger.info("Clicked on edit of project in the Pending Provisioning list");
			vProjNotExistInPendingProv(projectName);

			//num_comm is still present in the "Provisioning" page.
			selUtils.clickOnWebElement(selUtils.getObject("provtab_xpath"));
			logger.info("Clicked on Provisioning tab");
			colIndex=selUtils.getIndexForColHeader("colheaders_css", NUMCOMMCOL);
			verifyLvlColLvlValPresence("provlst_css",colIndex,numcommval);

			logger.info("SMR166 executed successfully");	
		}
		catch (Throwable t) {
			handleException(t);
		}
	}

}

