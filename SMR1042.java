package com.ingenico.testsuite.gprs;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR1042.java $
$Id: SMR1042.java 18096 2016-04-18 08:46:42Z haripraks $
 */

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * SMR-1042:Allocate a batch
 * @author Hariprasad.KS
 *
 */
public class SMR1042 extends SuiteGprs{

	/**
	 * Allocate a batch
	 */
	@Test(groups="SMR1042")
	public void smr1042(){
		try {
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1042 execution started");

			final String simcardyn=testDataOR.get("sim_cards_yn"),simcardn=testDataOR.get("sim_cards_n"),batchName=testDataOR.get("batch_name"),custName=testDataOR.get("customer"),allocated="Allocated";
			String[] simCardsYN,simCardsN;
			
			//Access Everest with a superuser
			//Go to "GPRS SIM Management"
			logger.info("Step 1, 2 :");
			navToSubPage("gprs_tab_xpath", "simmngmtsubpage_xpath", SIMMNGMNT);

			//Select Batch name and allocate
			logger.info("Step 3 :");
			/*colIndex=selUtils.getIndexForColHeader("gprscolheader_css", BATCHNAME);
			verifyLvlColLvlValPresence("entitytablelst_css",colIndex,batchName);*/
			vColValPresence(BATCHNAME, batchName);
			
			clkOnDirectObj("selectcheck_xpath", "NAME", batchName);
			selUtils.clickOnNavPaneItems(selUtils.getObject("actionbatch_xpath"),ATTRCLAS,VALCLOSE);
			selUtils.clickOnWebElement(selUtils.getObject("allocate_link"));
			if(getModWinDisp(selUtils.getObject("alloc_modwin_xpath"),ALLOCATE))
			{
			selUtils.selectItem(selUtils.getObject("customername_xpath"),custName);
			selUtils.clickOnWebElement(selUtils.getObject("btch_okbtn_xpath"));
			logger.info("Clicked on ok button");
			}
			else{
	            Assert.fail(ALLOCATE+" model window is not displayed");
			}
			//Negative condition handles
			checkAlert();
			
			//verify customer name and status field
			/*colIndex=selUtils.getIndexForColHeader("gprscolheader_css", BATCHNAME);
			verifyLvlColLvlValPresence("entitytablelst_css",colIndex,batchName);*/
			vColValPresence(BATCHNAME, batchName);

			colIndex=selUtils.getIndexForColHeader("gprscolheader_css", STATUS);
			xpath=getPath("tabledata_xpath").replace("BATCHNAME",batchName).replace("INDEX",Integer.toString(colIndex+1));
			selUtils.verifyTextEqualsWith(selUtils.getObjectDirect(By.xpath(xpath)), allocated);

			xpath=getPath("tablecustdata_xpath").replace("BATCHNAME",batchName).replace(NAME,custName);
			selUtils.verifyElementDisp(selUtils.getObjectDirect(By.xpath(xpath)), custName);

			//Go to GPRS -GPRS Management and then select customer
			logger.info("Step 4 :");
			navGPRSMgmtClkSIM(custName);
			simCardsYN=simcardyn.split(",");
			simCardsN=simcardn.split(",");
			colIndex=selUtils.getIndexForColHeader("gprscolheader_css", COLICCID);
			/*for (iter =0 ;iter< simCardsYN.length;iter++) 
			{
				verifyLvlColLvlValPresence("entitytablelst_css",colIndex,simCardsYN[iter].trim());
			}
			for (iter =0 ;iter< simCardsN.length;iter++) 
			{
				verifyLvlColLvlValPresence("entitytablelst_css",colIndex,simCardsN[iter].trim());
			}*/
			vSimCards(simCardsYN);
			vSimCards(simCardsN);

			logger.info("SMR1042 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}
	}
}

