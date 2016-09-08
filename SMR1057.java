package com.ingenico.testsuite.gprs;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR1057.java $
$Id: SMR1057.java 17577 2016-03-15 06:12:54Z rkahreddyga $
 */

import java.util.HashMap;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.base.TestBase;
import com.ingenico.common.CommonConstants;

/**
 * SMR-1057:Import activation confirmation
 * @author Hariprasad.KS
 *
 */
public class SMR1057 extends SuiteGprs{

	/**
	 * Import activation confirmation
	 */
	@Test(groups="SMR1057")
	public void smr1057(){
		try {
			eportalCust=testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			final String simcardyn=testDataOR.get("sim_cards_yn"),custName=testDataOR.get("customer");
			String[] simcardYN,simCards;

			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1057 execution started");

			//Access Everest with a superuser
			//Go to "GPRS - GPRS Management" and click sim cards tab
			logger.info("Step 1,2 :");
			navGPRSMgmtClkSIM(custName);
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			simCards=simcardyn.split(",");
			simundractNpReq(simCards,SIMSTATUS,UNDERACTIVATION,OPERSTATUS,PROVREQ);
			logger.info("Simcards of " + UNDERACTIVATION+ " and "+PROVREQ+" stored into simCardsYN");

			//import Activation confirmation csv file
			logger.info("Step 3 :");
			uploadCSVFile(CommonConstants.ACTCONFIRMCSV, ACTCONFIRM,"activationConfirmation.csv");
			checkAlert();

			//Wait 2 min for radious replication then refresh the Customer/SIM Cards Everest page
			logger.info("Step 4 :");
			valSIMStaus(simCardsYN, ACTIVATED,SIMSTATUS);

			simcardYN= simCardsYN.values().toArray(new String[simCardsYN.values().size()]);
			// Data base verification
			if (dbCheck && simcardYN!=null) {
				// validate with database
				vRadiusDatabase(simcardYN, "radcheck");
				logger.info("Sim cards data is validated in radcheck table");
				vRadiusDatabase(simcardYN, "radreply");
				logger.info("Sim cards data is validated in radreply table");
			}

			//Go to ePortal with a superuser, select a customer then go to GPRS/snapshot.
			//Click on activated  link  and validate
			logger.info("Step 5 :");
			logoutEpSelCust(custName);
			logger.info("Access eportal with superuser");
			if(simcardYN!=null)
				navGPRSPageNval(simcardYN, "activated_xpath", ACTIVATED);
			else
				logger.info("There is no sim status  "+ACTIVATED+" to validate");

			logger.info("SMR1057 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}
	}
	/**
	 *SimCards are stored into variables
	 * @param simCards
	 */	
	public HashMap<Integer, String> simundractNpReq(String[] simCards,String colName,String simStatus,String oprcolName,String oprStatus)
	{
		int key=0;
		simCardsYN = new HashMap<Integer, String>();
		for(iter =0 ;iter< simCards.length;iter++)
		{
			colIndex=selUtils.getIndexForColHeader("gprscolheader_css", colName);
			colIndxAnother=selUtils.getIndexForColHeader("gprscolheader_css", oprcolName);
			xpath = TestBase.getPath("simcardstat_xpath"); 
			webElement=selUtils.getObjectDirect(By.xpath(xpath.replace("ICCID", simCards[iter]).replace("INDEX", Integer.toString(colIndex+1))));
			xpath = TestBase.getPath("simcardstat_xpath"); 
			anotherWebEle=selUtils.getObjectDirect(By.xpath(xpath.replace("ICCID", simCards[iter]).replace("INDEX", Integer.toString(colIndxAnother+1))));
			waitMethods.waitForWebElementPresent(webElement);
			if(webElement.getText().trim().contains(simStatus) && anotherWebEle.getText().trim().contains(oprStatus))
			{ 
				simCardsYN.put(key,simCards[iter]);
				key++;
			}			
		}
		if(simCardsYN.isEmpty())
			Assert.fail("Simcards of "+simStatus+" should not be zero");
		
		return simCardsYN;
	}
}

