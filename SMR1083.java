package com.ingenico.testsuite.gprs;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR1083.java $
$Id: SMR1083.java 17300 2016-02-29 07:21:06Z jsamuel $
 */
import java.util.HashMap;
import org.testng.annotations.Test;

/**
 * SMR-1083:Suspend a SIM
 * @author Joel F
 *
 */
public class SMR1083 extends SuiteGprs{

	/**
	 * Suspend a SIM
	 */
	@Test(groups="SMR1083")
	public void smr1083(){
		try {
			eportalCust=testDataOR.get("customer");
			final String simcards=testDataOR.get("sim_cards_yn");
			String[] simCards=simcards.split(",");
			String firstSim=simCards[0].toString();
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			
			HashMap<Integer, String> simCardsS=new HashMap<Integer, String>();
			simCardsS.put(0, firstSim);

			//Access Eportal with a superuser
			//Go to "GPRS - SIM Fleet" subtab
			logger.info("Step 1,2 :");
			login("URLEportal",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1082 execution started");
			navigateToSubPage(SIMFLEET,selUtils.getCommonObject("gprs_tab_xpath"),selUtils.getCommonObject("simfleet_subpage_xpath"));

			//Click on the hyperlinked number under activated column
			logger.info("Step 3 :");
			clickOnHyperLink(ACTIVATED);

			//Suspend a sim
			logger.info("Step 4 :");
			susOrActivateSim(firstSim,SUSPEND);
			
			//wait for radius replication and verify in the database
			logger.info("Step 5 :");
			navigateToSubPage(SIMFLEET,selUtils.getCommonObject("gprs_tab_xpath"),selUtils.getCommonObject("simfleet_subpage_xpath"));
			vRadiusRepli(firstSim,SUSPENDED);
			if(dbCheck){
				vTabledatadinDB(simCardsS, "radcheck");
				logger.info("Sim cards data is validated in radcheck table");
				vTabledatadinDB(simCardsS, "radreply");
				logger.info("Sim cards data is validated in radreply table");
			}

			logger.info("SMR1083 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}
	}
}

