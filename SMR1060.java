package com.ingenico.testsuite.gprs;

import java.util.HashMap;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR1060.java $
$Id: SMR1060.java 17000 2016-02-09 13:04:05Z rkahreddyga $
 */
import org.testng.annotations.Test;

/**
 * SMR-1060:Unsuspend a SIM
 * @author Hariprasad.KS
 *
 */
public class SMR1060 extends SuiteGprs{

	/**
	 * Unsuspend a SIM
	 */
	HashMap<Integer, String> simCardsUS;
	@Test(groups="SMR1060")
	public void smr1060(){
		try {
			eportalCust=testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1060 execution started");

			final String simcardsYn=testDataOR.get("sim_cards_yn"),custName=testDataOR.get("customer");
			String[] simCards;

			//Access Everest with a superuser
			//Go to "GPRS - GPRS Management" and click sim cards tab
			logger.info("Step 1,2,3 :");
			navGPRSMgmtClkSIM(custName);
			
			// select Unsuspend in action sim menu then select sim cards and submit
			logger.info("Step 4 :");
			sActnSimNSimCds(UNSUSPEND);
			simCards=simcardsYn.split(",");
			
			simcardsActNSus(simCards,SIMSTATUS,SUSPENDED);
			simCardsUS=new HashMap<Integer, String>();
			simCardsUS.putAll(simCardsAS);
				
			//Converting Hashmap values into array 
			simcardsus= simCardsUS.values().toArray(new String[simCardsUS.values().size()]);
			selChkBoxofSIMs(simcardsus);
			selUtils.clickOnWebElement(selUtils.getObject("unsuspendok_xpath"));
			
			//ePortal-Radius Replication and Sim Status verification 
			logger.info("Step 5 and 6 :");
			valSIMStaus(simCardsUS, ACTIVATED,SIMSTATUS);
			if(dbCheck){
				//validate with database
				vRadiusDatabase(simcardsus, "radcheck");
				logger.info("Sim cards data is validated in radcheck table");
				vRadiusDatabase(simcardsus, "radreply");
				logger.info("Sim cards data is validated in radreply table");
			}
					
			
			 
			//Go to eportal then select customer	
			logger.info("Step 7 :");
			logoutEpSelCust(custName);
			logger.info("Access eportal with superuser");
			//Navigate to gprs snapshot and click on Activated sim's
			navGPRSPageNval(simcardsus, "activated_xpath", ACTIVATED);
			
			logger.info("SMR1060 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}
	}
	

}

