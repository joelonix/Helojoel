package com.ingenico.testsuite.gprs;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR1055.java $
$Id: SMR1055.java 17097 2016-02-16 11:48:45Z haripraks $
 */
import org.testng.annotations.Test;

/**
 * SMR-1055:Activate a SIM
 * @author Hariprasad.KS
 *
 */
public class SMR1055 extends SuiteGprs{

	/**
	 * Activate a SIM
	 */
//	HashMap<Integer, String> simCardsYN,simCardsN;
	@Test(groups="SMR1055")
	public void smr1055(){
		try {
			eportalCust=testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1055 execution started");

			final String simcardsYn=testDataOR.get("sim_cards_yn"),custName=testDataOR.get("customer");
			String[] simCards;
			
			

			//Access Everest with a superuser
			//Go to "GPRS - GPRS Management" and click sim cards tab
			logger.info("Step 1,2,3 :");
			navGPRSMgmtClkSIM(custName);

			// select activate in action sim menu then select sim cards and submit
			logger.info("Step 4 :");			
			sActnSimNSimCds(ACTIVATE);
			simCards=simcardsYn.split(",");
			
			//sim cards stored into simCardsYN and simCardsN		
			simCardsYNnN(simCards,PREACTNOTASS);	
			logger.info("Simcards of " + PREACTNOTASS+ " stored into simCardsYN AND/OR "+NONE+" stored into simCardsN");
			selChkBoxofSIMs(simCards);
			selUtils.clickOnWebElement(selUtils.getObject("activateok_xpath"));
			logger.info("Customer Activation Request csv file downloaded");
			checkAlert();
			
			//ePortal-Radius Replication and Sim Status verification
			if(!(simCardsYN.isEmpty()))
			valSIMStaus(simCardsYN,ACTIVATED,SIMSTATUS);
			else
				logger.info(ACTIVATED+" simcards are not there for verify status");
			
			if(!(simCardsN.isEmpty())){
			valSIMStaus(simCardsN,UNDERACTIVATION,SIMSTATUS);
			valSIMStaus(simCardsN,PROVREQ,OPERSTATUS);
			}
			else{
				logger.info(NONE+" simcards are not there for verify status");
			}
						
			//Converting Hashmap values into array     
		    String[] simcardYN= simCardsYN.values().toArray(new String[simCardsYN.values().size()]);
		    String[] simcardN= simCardsN.values().toArray(new String[simCardsN.values().size()]);
			// Data base verification
			if (dbCheck && !(simCardsYN.isEmpty())) {
				// validate with database
				vRadiusDatabase(simcardYN, "radcheck");
				logger.info("Sim cards data is validated in radcheck table");
				vRadiusDatabase(simcardYN, "radreply");
				logger.info("Sim cards data is validated in radreply table");
			}
			else
				logger.info(ACTIVATED+" Simcards are not there for database validation");

			logger.info("Step 5 :");
			logoutEpSelCust(custName);
			logger.info("Access eportal with superuser");
			if(simcardYN.length!=0)
			navGPRSPageNval(simcardYN, "activated_xpath", ACTIVATED);

			logger.info("Step 6 :");
			if(simcardN.length!=0)
			navGPRSPageNval(simcardN, "underactivation_xpath", UNDERACTIVATION);
			logger.info("SMR1055 is successfully executed");
		} catch (Throwable t) {
			handleException(t);
		}
	}
	
	/**
	 *Group of simCards are stored into variables
	 * @param simCardsYN
	 * @param simCardsN
	 */	
//	private void simCardsYNnN(String[] simCards)
//	{
//		int ksy = 0, ksn = 0;
//		simCardsYN = new HashMap<Integer, String>();
//		simCardsN = new HashMap<Integer, String>();
//		for (iter = 0; iter < simCards.length; iter++) {
//			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
//			colIndex = selUtils.getIndexForColHeader("gprscolheader_css",SIMSTATUS);
//			xpath = TestBase.getPath("simcardstat_xpath");
//			webElement = selUtils.getObjectDirect(By.xpath(xpath.replace("ICCID", simCards[iter]).replace("INDEX",Integer.toString(colIndex + 1))));
//			
//			if (webElement.getText().trim().contains(PREACTNOTASS)) {
//				simCardsYN.put(ksy, simCards[iter]);
//				ksy++;
//			} else if (webElement.getText().trim().contains(NONE)) {
//				simCardsN.put(ksn, simCards[iter]);
//				ksn++;
//			} else {
//				Assert.fail("Simcards are invalid status");
//			}
//		}
//		
//		if(simCardsYN.keySet().size()==0 || simCardsN.keySet().size()==0)
//			Assert.fail("Simcards of sim_cards_yn should be 2 groupss");
//		else
//			logger.info("Simcards of " + PREACTNOTASS+ " stored into simCardsYN "+NONE+" stored into simCardsN");
//		
//	}
	
}





