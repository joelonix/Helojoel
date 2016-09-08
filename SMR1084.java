package com.ingenico.testsuite.gprs;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR1084.java $
$Id: SMR1084.java 17300 2016-02-29 07:21:06Z jsamuel $
 */

import org.testng.annotations.Test;

/**
 * SMR-1084:Reactivate a SIM
 * @author Joel F
 *
 */
public class SMR1084 extends SuiteGprs{

	/**
	 * Reactivate a SIM
	 */
	@Test(groups="SMR1084")
	public void smr1084(){
		try {
			eportalCust=testDataOR.get("customer");
			final String simcards=testDataOR.get("sim_cards_yn");
			String[] simCards=simcards.split(",");
			String [] firstSIM={simCards[0]};
			String firstSim=simCards[0].toString();
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");

			//Access Eportal with a superuser
			//Go to "GPRS - SIM Fleet" subtab
			logger.info("Step 1,2 :");
			login("URLEportal",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1082 execution started");
			navigateToSubPage(SIMFLEET,selUtils.getCommonObject("gprs_tab_xpath"),selUtils.getCommonObject("simfleet_subpage_xpath"));

			//Click on the hyperlinked number under suspended column
			logger.info("Step 3 :");
			clickOnHyperLink(SUSPENDED);

			//Re-activate a sim
			logger.info("Step 4 :");
			susOrActivateSim(firstSim,REACTIVATE);
			navigateToSubPage(SIMFLEET,selUtils.getCommonObject("gprs_tab_xpath"),selUtils.getCommonObject("simfleet_subpage_xpath"));
			
			//wait for radius replication and verify in the database
			logger.info("Step 5 :");
			vRadiusRepli(firstSim,ACTIVATED);
			if(dbCheck){
				vRadiusDatabase(firstSIM, "radcheck");
				logger.info("Sim cards data is validated in radcheck table");
				vRadiusDatabase(firstSIM, "radreply");
				logger.info("Sim cards data is validated in radreply table");
			}

			logger.info("SMR1084 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}
	}
}
