package com.ingenico.testsuite.gprs;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR1056.java $
$Id: SMR1056.java 17575 2016-03-15 04:53:54Z rkahreddyga $
 */

import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;

/**
 * SMR-1056:Import activation request
 * @author Hariprasad.KS
 *
 */
public class SMR1056 extends SuiteGprs{

	/**
	 * Import activation request
	 */
	@Test(groups="SMR1056")
	public void smr1056(){
		try {
			eportalCust=testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1056 execution started");

			final String simcardyn=testDataOR.get("sim_cards_yn"),custName=testDataOR.get("customer");
			String[] simCards,simcardYN,simcardN;

			//Access Everest with a superuser
			//Go to "GPRS - GPRS Management" and click sim cards tab
			logger.info("Step 1,2,3 :");
			navGPRSMgmtClkSIM(custName);
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			simCards=simcardyn.split(",");
			simCardsYNnN(simCards,PREACTNOTASS);
			logger.info("Simcards of " + PREACTNOTASS+ " stored into simCardsYN AND/OR "+NONE+" stored into simCardsN");

			logger.info("Step 4 :");
			uploadCSVFile(CommonConstants.ACTREQCSV, ACTRQST,"activationRequest.csv");
			checkAlert();

			//Wait 2 min for radious replication then refresh the Customer/SIM Cards Everest page
			logger.info("Step 5 :");
			valSIMStaus(simCardsYN, ACTIVATED,SIMSTATUS);
			valSIMStaus(simCardsN,  UNDERACTIVATION,SIMSTATUS);
			valSIMStaus(simCardsN,  PROVREQ,OPERSTATUS);

			//Converting Hashmap values into array     
			simcardYN= simCardsYN.values().toArray(new String[simCardsYN.values().size()]);
			simcardN= simCardsN.values().toArray(new String[simCardsN.values().size()]);

			// Data base verification
			if (dbCheck && simcardYN!=null) {
				// validate with database
				vRadiusDatabase(simcardYN, "radcheck");
				logger.info("Sim cards data is validated in radcheck table");
				vRadiusDatabase(simcardYN, "radreply");
				logger.info("Sim cards data is validated in radreply table");
			}

			logger.info("Step 6 :");
			logoutEpSelCust(custName);
			logger.info("Access eportal with superuser");
			if(simcardYN!=null)
				navGPRSPageNval(simcardYN, "activated_xpath", ACTIVATED);
			else
				logger.info("There is no sim status  "+ACTIVATED+" to validate");
			
			logger.info("Step 7 :");
			if(simcardN!=null)
				navGPRSPageNval(simcardN, "underactivation_xpath", UNDERACTIVATION);
			else
				logger.info("There is no sim status  "+UNDERACTIVATION+" to validate");
			
			logger.info("SMR1056 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}
	}

}

