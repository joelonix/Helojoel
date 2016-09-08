package com.ingenico.testsuite.gprs;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR1039.java $
$Id: SMR1039.java 18136 2016-04-19 10:33:07Z jsamuel $
 */
import java.sql.SQLException;
import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;

/**
 * SMR-1039:Import SIM cards
 * @author Joel
 *
 */
public class SMR1039 extends SuiteGprs{

	/**
	 * Import SIM cards
	 */
	@Test(groups="SMR1039")
	public void smr1039(){
		try {

			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1039 execution started");

			csv=getDataFromCSV(CommonConstants.IMPSIMCSV);
			//remove all empty blank items
			csv.removeAll(Arrays.asList(null,""));

			//Coverting array list to array
			String[] arrFromCSV=new String[csv.size()];

			arrFromCSV=csv.toArray(arrFromCSV);

			final String dbEportal=testDataOR.get("databaseEportal");
			final int totBeforSimImpt,totAfterSimImp,noOfEntries=csv.size()-1;

			//Access Everest with a superuser
			//Go to "GPRS SIM Management"
			logger.info("Step 1, 2 :");
			navToSubPage("gprs_tab_xpath","simmngmtsubpage_xpath",SIMMNGMNT);
			logger.info("Navigated to GPRS SIM Management");

			//Calculate no of SIM not Batched before import 
			totBeforSimImpt=simBfrAfrImp("simnotbatched_xpath");

			//In the sim cards from the import sim menu
			logger.info("Step 3 :");
			uploadImportSim();
			checkAlert();

			if(dbCheck){
				//Connect to eportal database and validate
				logger.info("Step 4 :");
				verifyDBVals(dbEportal,arrFromCSV);
				logger.info("Verified iccid column is having all the simcards from csv in the database");
			}

			//No of batched sim after import
			totAfterSimImp=simBfrAfrImp("simnotbatched_xpath");

			int simCardDiff=totAfterSimImp-noOfEntries;

			if(simCardDiff==totBeforSimImpt)
			{

				logger.info("Total no of simcards after csv import is  increased to "+ totAfterSimImp);

			}else
			{

				Assert.fail("Total no of Sim cards not batched are not increased after import csv");

			}

			//Click on SIM not batched
			logger.info("Step 5 :");
			selUtils.clickOnWebElement(selUtils.getObject("simnotbatched_xpath"));
			colIndex=selUtils.getIndexForColHeader("gprscolheader_css", COLICCID);
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			for (iter =1 ;iter< arrFromCSV.length;iter++) 
			{
				verifyLvlColLvlValPresence("entitytablelst_css",colIndex,arrFromCSV[iter].trim());
			}

			logger.info("SMR1039 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}

	}

	/**
	 * Method to import sim csv file
	 */
	private void uploadImportSim(){
		final String importSIM = "import_sim.csv";
		selUtils.clickOnWebElement(selUtils.getObject("impsim_id"));
		//selUtils.populateInputBox("imp_sim_browsebttn_xpath", CommonConstants.IMPSIMCSV);
		selUtils.getObject("imp_sim_browsebttn_xpath").sendKeys(CommonConstants.IMPSIMCSV);
		Assert.assertTrue(selUtils.getObject("imp_sim_browsebttn_xpath").getAttribute("value").contains(importSIM), importSIM + " is not loaded");
		selUtils.selectItem(selUtils.getObject("type_simimp_id"),SIMCARDS);
		logger.info(importSIM + " is loaded");
		selUtils.clickOnWebElement(selUtils.getObject("imp_sim_loadbttn_xpath"));
		if(getModWinDisp(selUtils.getObject("imp_sim_windialog_xpath"),SIMCRDDELIVERY))
		{
			/*selUtils.selectItem(selUtils.getObject("selbilngcycle_xpath"), NOBILNGCYCLE);
			logger.info("Selected the billing cycle "+NOBILNGCYCLE);*/
			selUtils.clickOnWebElement(selUtils.getObject("imp_okbttn_xpath"));
			logger.info("Clicked on ok button");
		}
		else{
			logger.info("SIM Card Delivery Modal window is not displayed");
		}
	}


	/**
	 * Method to verify sim cards in the database
	 * @param dburl
	 * @param listcsv
	 * @throws SQLException
	 */
	private void verifyDBVals(String dburl,String[] arrfromcsv) throws SQLException{

		String simcardVal=null;

		for (int i = 1; i < arrfromcsv.length; i++) {
			sqlQuery= "SELECT iccid FROM sim_card Where iccid='"+arrfromcsv[i]+"';";
			resSet = dbMethods.getDataBaseVal(dburl,sqlQuery,CommonConstants.ONEMIN);
			simcardVal=resSet.getString("iccid").trim();
			Assert.assertTrue(simcardVal.equals(arrfromcsv[i]), "Actual is "+arrfromcsv[i]+"And expected is "+simcardVal+" data does not exist ");
			logger.info("Verified "+simcardVal+" exists is the database");
		}

	}

}

