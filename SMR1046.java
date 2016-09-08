package com.ingenico.testsuite.gprs;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR1046.java $
$Id: SMR1046.java 18096 2016-04-18 08:46:42Z haripraks $
 */
import java.util.ArrayList;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;

/**
 * SMR-1046:Import activation confirmation
 * @author Hariprasad.KS
 *
 */
public class SMR1046 extends SuiteGprs{
	ArrayList<String> underActiSIMs = new ArrayList<String>();
	int simStatusIndex,opStatusIndex;
	
	/**
	 * Import activation confirmation
	 */
	@Test(groups="SMR1046")
	public void smr1046(){
		try {
			eportalCust=testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name"),
					simcards=testDataOR.get("sim_cards_yn"),batchName=testDataOR.get("batch_name"),custName=testDataOR.get("customer");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1046 execution started");
			String[] activatedSIMCards,simCards=simcards.split(",");

			//Access Everest with a superuser
			//Go to "GPRS - SIM Management" 
			logger.info("Step 1,2:");
			navToSubPage("gprs_tab_xpath", "simmngmtsubpage_xpath", SIMMNGMNT);

			//Click on the batch name and validate some of the SIM status 
			//set to 'Under Activation' and Operational Status set to 'Provider Request'
			logger.info("Step 3 :");
			vSIMNOperStatus(batchName, simCards);

			//go to gprs sim management and select "Activation confirmation" in the "Import SIM" menu
			//Then click browse and select CSV file, validate and click load button
			logger.info("Step 4 :");
			activatedSIMCards=new String[underActiSIMs.size()];
			activatedSIMCards=underActiSIMs.toArray(activatedSIMCards);
			navToSubPage("gprs_tab_xpath", "simmngmtsubpage_xpath", SIMMNGMNT);
			uploadCSVFile(CommonConstants.ACTCONFIRMCSV, ACTCONFIRM,"activationConfirmation.csv");
			checkAlert();
			
			//Wait 2 min for radious replication then click on batch name Then validate
			logger.info("Step 5 :");
			xpath=getPath("batchdetailstab_xpath").replace(COLICCID, activatedSIMCards[0]).replace("INDEX", Integer.toString(simStatusIndex+1));
			waitForRadiusrepli(xpath,batchName,ACTIVATED);
			for(String activatedSIM:activatedSIMCards)
			{
				xpath=getPath("batchdetailstab_xpath").replace(COLICCID, activatedSIM).replace("INDEX", Integer.toString(simStatusIndex+1));
				selUtils.verifyTextEqualsWith(selUtils.getObjectDirect(By.xpath(xpath)), ACTIVATED);
			}
			//Database check
			if(dbCheck){
				vRadiusDatabase(activatedSIMCards, "radcheck");
				logger.info("Sim cards data is validated in radcheck table");
				vRadiusDatabase(activatedSIMCards, "radreply");
				logger.info("Sim cards data is validated in radreply table");
			}
			//Go to ePortal with a superuser, select a customer then go to GPRS/snapshot.
			//Click on activated  link  and validate
			logger.info("Step 6 :");
			logoutEpSelCust(custName);
			logger.info("Access eportal with superuser");
			navGPRSPageNval(activatedSIMCards, "activated_xpath", ACTIVATED);

			logger.info("SMR1046 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}
	}
	
	/**
	 * Select batch name and validate sim, operational status
	 * @param batchName
	 * @param simCards
	 * @return
	 */
	private void vSIMNOperStatus(String batchName,String[] simcards){
		colIndex=selUtils.getIndexForColHeader("gprscolheader_css", BATCHNAME);
		verifyLvlColLvlValPresence("entitytablelst_css",colIndex,batchName);
		clkOnDirectObj("batchname_xpath", "BATCH", batchName);
		logger.info("Clicked on batch name "+batchName);
		//get column numbers
		simStatusIndex=selUtils.getIndexForColHeader("gprscolheader_css", SIMSTATUS);
		opStatusIndex=selUtils.getIndexForColHeader("gprscolheader_css", OPERSTATUS);
		for(String SIMCards:simcards)
		{
			xpath=getPath("batchdetailstab_xpath").replace(COLICCID, SIMCards).replace("INDEX", Integer.toString(simStatusIndex+1));
			anotherxpath=getPath("batchdetailstab_xpath").replace(COLICCID, SIMCards).replace("INDEX", Integer.toString(opStatusIndex+1));
			logger.info(selUtils.getObjectDirect(By.xpath(xpath)).getText());
			logger.info(selUtils.getObjectDirect(By.xpath(anotherxpath)).getText());
			if(selUtils.getObjectDirect(By.xpath(xpath)).getText().equals(UNDERACTIVATION) &&selUtils.getObjectDirect(By.xpath(anotherxpath)).getText().equals(PROVREQ) )
			{
				underActiSIMs.add(SIMCards);
				logger.info(SIMCards+"Sim status are set to Under Activation and Operational Status set to Provider Request");
			}
		}
		Assert.assertFalse(underActiSIMs.isEmpty(),"No sim status are set to Under Activation and Operational Status set to Provider Request ");
	}
	

}

