package com.ingenico.testsuite.gprs;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR1075.java $
$Id: SMR1075.java 18096 2016-04-18 08:46:42Z haripraks $
 */
import java.util.ArrayList;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.ingenico.common.CommonConstants;

/**
 * SMR-1075:Create pool from SIM cards
 * @author Hariprasad.KS
 *
 */
public class SMR1075 extends SuiteGprs{
	ArrayList<String> activatedSIMs = new ArrayList<String>(),noneSIMs = new ArrayList<String>(),tempSIMS= new ArrayList<String>();
	
	/**
	 * Create pool from SIM cards
	 */
	@Test(groups="SMR1075")
	public void smr1075(){
		try {
			eportalCust=testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name"),databaseEportal=testDataOR.get("databaseEportal"),
					simcardsyn=testDataOR.get("sim_cards_yn"),simcardsn=testDataOR.get("sim_cards_n"),poolnameValue=testDataOR.get("pool_name");
			// This step is included based on communication with Guillaume
			if(!dbCheck){
				Assert.fail("You cannot run this test case with DataBaseVerifications set to false");
			}
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1075 execution started");

			final String custName=testDataOR.get("customer");
			String[] activatedSIMCards,noneSIMCards,simCardsyn=simcardsyn.split(","),simCardsn=simcardsn.split(",");
			int simStatusIndex,poolNameIndex,iccidIndex;
			
			//Access Everest with a superuser 
			//Go to "GPRS - GPRS Management" and click sim cards tab
			logger.info("Step 1,2 :");
			navGPRSMgmtClkSIM(custName);
			
			//Select "Add to pool" in the "Action SIM" menu then select sim cards and click submit.
			//Then check 'New SIM Pool' button, enter pool_name in the new sim pool field then validate.
			logger.info("Step 3 :");
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			selUtils.clickOnNavPaneItems(selUtils.getObject("action_sim_xpath"),ATTRCLAS,VALCLOSE);
			selUtils.selectItem(selUtils.getObject("selaction_xpath"),ADDTOPOOL);
			simStatusIndex=selUtils.getIndexForColHeader("gprscolheader_css", SIMSTATUS);
			activatedSIMs=checkSIMCards(simCardsyn, tempSIMS, ACTIVATED, simStatusIndex);
			activatedSIMCards=new String[activatedSIMs.size()];
			activatedSIMCards=activatedSIMs.toArray(activatedSIMCards);
			noneSIMs=checkSIMCards(simCardsn, tempSIMS, NONE, simStatusIndex);
			noneSIMCards=new String[noneSIMs.size()];
			noneSIMCards=noneSIMs.toArray(noneSIMCards);
			
			selUtils.clickOnWebElement(selUtils.getObject("navpanesubmit_xpath"));
			selUtils.clickOnWebElement(selUtils.getObject("newsimpool_xpath"));
			selUtils.getObject("poolname_id").sendKeys(poolnameValue);
			selUtils.clickOnWebElement(selUtils.getObject("addpoolok_xpath"));
			checkAlert();
			
			//validation pool name column- sim_cards_n(=None) set to pool_name and sim_cards_yn(=Activated) set to blank
			poolNameIndex=selUtils.getIndexForColHeader("gprscolheader_css", POOLNAME);
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			
			
			vSimYNcolVal(activatedSIMCards, poolNameIndex,"","undercreagif_xpath");
			vSimNcolVal(noneSIMCards, poolNameIndex,poolnameValue);
			
			//Access Everest with a superuser 
			//Go to "GPRS - GPRS Management" and click SIM Pools tab and validate
			logger.info("Step 4 :");
			//navGPRSMgmtClkSIM(custName);
			navigateToSubPage(GPRSMNGMNT,selUtils.getCommonObject("gprs_tab_xpath"),selUtils.getCommonObject("gprsmngmtsubpage_xpath"));
			logger.info("Navigated to "+GPRSMNGMNT+" tab");
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), custName);
			logger.info("Selected customer "+custName);

			//Go to pool cards tab
			selUtils.clickOnWebElement(selUtils.getObject("simpoolstab_xpath"));
			logger.info("Clicked on SIM Pools tab");
			poolNameIndex=selUtils.getIndexForColHeader("gprscolheader_css", POOLNAME);
			linkText=getPath("poollinktext_link").replace(NAME, poolnameValue);
			selUtils.verifyTextEqualsWith(selUtils.getObjectDirect(By.linkText(linkText)), poolnameValue);
			selUtils.vDirectEleDisplayed("undercreagif_xpath", COLICCID, poolnameValue);
			
			//click on pool_name and validate only 'sim_cards_n' are listed
			logger.info("Step 5 :");
			clkOnDirectObj("poollinktext_link", NAME, poolnameValue);
			iccidIndex=selUtils.getIndexForColHeader("gprscolheader_css", COLICCID);
			vSIMcardsListed(noneSIMCards, iccidIndex);
			for(String SIMCards:activatedSIMCards)
			{
				xpath=getPath("batchdetailstab_xpath").replace(COLICCID, SIMCards).replace("INDEX", Integer.toString(iccidIndex+1));
				assertFalse(selUtils.isElementPresentxpath(xpath));
			}
			logger.info("Only sim_cards_n sim cards are listed in pool name "+poolnameValue);
			
			//Execute stored procedure "run_sim_card_change(null)" in eportal database	
			logger.info("Step 6 :");
			//if(dbCheck){
				sqlQuery="SELECT * FROM run_sim_card_change(null)";
				dbMethods.getDataBaseVal(databaseEportal,sqlQuery,CommonConstants.ONEMIN);
				logger.info("Executed stored procedure 'run_sim_card_change(null)'");
			//}
			
			//Go to SIM Cards on the Everest interface
			logger.info("Step 7");
			selUtils.clickOnWebElement(selUtils.getObject("simcardstab_xpath"));
			logger.info("Clicked on SIM Cards tab");
			poolNameIndex=selUtils.getIndexForColHeader("gprscolheader_css", POOLNAME);
			vSimYNcolVal(activatedSIMCards, poolNameIndex, poolnameValue,"activatedgif_xpath");
			
			//Go to SIM Pools and clik on pool_name
			logger.info("Step 8");
			selUtils.clickOnWebElement(selUtils.getObject("simpoolstab_xpath"));
			clkOnDirectObj("poollinktext_link", NAME, poolnameValue);
			logger.info("Clicked on SIM Pools tab and pool name"+poolnameValue);
			iccidIndex=selUtils.getIndexForColHeader("gprscolheader_css", COLICCID);
			vSIMcardsListed(noneSIMCards, iccidIndex);
			vSIMcardsListed(activatedSIMCards, iccidIndex);
			
			//Go to ePortal with a superuser and select customer
			//Go to GPRS/SUM USage/PoolView then click pool_name, validate 'sim_cards_yn' sim lists
			logger.info("Step 9");
			logoutEpSelCust(custName);
			logger.info("Access eportal with superuser");
			navToSubPage("gprs_tab_xpath","ep_simusage_xpath",SIMUSAGE);
			selUtils.clickOnWebElement(selUtils.getObject("poolview_id"));
			logger.info("Navgated to GPRS - SIM Usage - Pool view page");
			clkOnDirectObj("poollinktext_link", NAME, poolnameValue);
			logger.info("Clicked on pool name "+poolnameValue);
			
			iccidIndex=selUtils.getIndexForColHeader("gprssimfleetdetail_css", COLSIMID);
			vSIMcardsListed(activatedSIMCards, iccidIndex);
			
			logger.info("SMR1075 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}
	}

	/**
	 * select check box of the sim cards
	 * @param simCardsyn
	 * @param tempSIMS
	 * @param SIMstatus
	 * @param colindex
	 * @return
	 */
	private ArrayList<String> checkSIMCards(String[] simCardsyn,ArrayList<String> tempSIMS,String SIMstatus,int colindex)
	{
		tempSIMS.clear();
		for(String SIMCards:simCardsyn)
		{
			xpath=getPath("batchdetailstab_xpath").replace(COLICCID, SIMCards).replace("INDEX", Integer.toString(colindex+1));
			if(selUtils.getObjectDirect(By.xpath(xpath)).getText().equals(SIMstatus) )
			{
				tempSIMS.add(SIMCards);
				xpath=getPath("simcardchkbox_xpath").replace(COLICCID, SIMCards);
				selUtils.getObjectDirect(By.xpath(xpath)).click();
			}
		}
		return tempSIMS;
	}

	/**
	 * validate sim cards YN with pool name displayed or not with status of sim
	 * @param simCardsyn
	 * @param colindex
	 * @param poolnameValue
	 * @param locator
	 */
	private void vSimYNcolVal(String[] simCardsyn,int colindex,String poolnameValue,String locator)
	{
		for(String SIMCards:simCardsyn)
		{
			xpath=getPath("batchdetailstab_xpath").replace(COLICCID, SIMCards).replace("INDEX", Integer.toString(colindex+1));
			selUtils.verifyTextEqualsWith(selUtils.getObjectDirect(By.xpath(xpath)), poolnameValue);
			selUtils.vDirectEleDisplayed(locator, COLICCID, SIMCards);
		}
	}
	
	/**
	 * validate sim cards N with pool name displayed or not
	 * @param simCardsn
	 * @param colindex
	 * @param poolnameValue
	 */
	private void vSimNcolVal(String[] simCardsn,int colindex,String poolnameValue)
	{
		for(String SIMCards:simCardsn)
		{
			xpath=getPath("batchdetailstab_xpath").replace(COLICCID, SIMCards).replace("INDEX", Integer.toString(colindex+1));
			selUtils.verifyTextEqualsWith(selUtils.getObjectDirect(By.xpath(xpath)), poolnameValue);
		}
	}
	
}

