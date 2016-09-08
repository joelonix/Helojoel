package com.ingenico.testsuite.gprs;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR1059.java $
$Id: SMR1059.java 17299 2016-02-29 07:04:52Z haripraks $
 */
import java.util.HashMap;
import org.testng.annotations.Test;

/**
 * SMR-1059:Suspend a SIM
 * @author Hariprasad.KS
 *
 */
public class SMR1059 extends SuiteGprs{

	/**
	 * Suspend a SIM
	 */
	HashMap<Integer, String> simCardsS;
	@Test(groups="SMR1059")
	public void smr1059(){

		try {
			eportalCust=testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1059 execution started");

			final String simcardsYn=testDataOR.get("sim_cards_yn"),custName=testDataOR.get("customer");
			String[] simCards;

			//Access Everest with a superuser
			//Go to "GPRS - GPRS Management" and click sim cards tab
			logger.info("Step 1,2,3 :");
			navGPRSMgmtClkSIM(custName);

			// select Suspend in action sim menu then select sim cards and submit
			logger.info("Step 4 :");
			sActnSimNSimCds(SUSPEND);
			simCards=simcardsYn.split(",");
			
			simcardsActNSus(simCards,SIMSTATUS,ACTIVATED);
			simCardsS=new HashMap<Integer, String>();
			simCardsS.putAll(simCardsAS);

			//Select Activated simcards	     
		    simcards= simCardsS.values().toArray(new String[simCardsS.values().size()]);
			selChkBoxofSIMs(simcards);
			
			selUtils.clickOnWebElement(selUtils.getObject("suspendok_xpath"));
			
			//Wait maximum 2 min for ePortal Radius replication
			logger.info("Step 5 and Step 6 :");
			valSIMStaus(simCardsS,SUSPENDED,SIMSTATUS);			
			if(dbCheck){
				//validate with database
				vTabledatadinDB(simCardsS, "radcheck");
				logger.info("Sim cards data is validated in radcheck table");
				vTabledatadinDB(simCardsS, "radreply");
				logger.info("Sim cards data is validated in radreply table");
			}
			
			//Go to eportal then select customer 
			logger.info("Step 7 :");
			logoutEpSelCust(custName);
			logger.info("Access eportal with superuser");
			
			//Navigate to gprs-snapshot and click on Suspended sim's
			//Selected simcards are appear with status suspended
			navGPRSPageNval(simcards, "suspended_xpath", SUSPENDED);
			logger.info("SMR1059 is successfully executed");
			
		}catch (Throwable t) {
			handleException(t);
		}
	}
	
	/**
	 * validate records related to sim deleted in table
	 * @param simCards
	 * @param tableName
	 * @author Hariprasad.KS
	 *//*
	private void vTabledatadelted(HashMap<Integer, String> simCardsS,String tableName)
	{
		resSet=null;
		try{
			for (int loop =0 ;loop< simCardsS.size();loop++) 
			{
				sqlQuery = "SELECT * FROM "+tableName+" where username LIKE'%"+simCardsS.get(loop)+"%'";
				dbMethods.connection = dbMethods.dbConnection(testDataOR.get("databaseRadius"), testDataOR.get("dbuser"),testDataOR.get("dbpassword"));
				try{	
					resSet = dbMethods.statement.executeQuery(sqlQuery);
				} catch (NullPointerException e) {
					logger.info("Records related to sim card"+"GGSN022_"+simCardsS.get(loop)+" "+"GGSN021_"+simCardsS.get(loop)+" are not present");
				}
				if(resSet!=null)
				{
					Assert.fail(tableName+" is having "+simCardsS.get(loop)+" value");
				}
			}
		} catch (SQLException e) {
			Assert.fail("Problem while validating table data");
			e.printStackTrace();
		}
	}*/
	

}

