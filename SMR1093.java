package com.ingenico.testsuite.gprs;


/*$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR1093.java $
$Id: SMR1093.java 18096 2016-04-18 08:46:42Z haripraks $*/
 

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * SMR-1093:Create batch from individual SIM cards
 * @author Joel
 *
 */
public class SMR1093 extends SuiteGprs{


	/**
	 * Create batch from individual SIM cards
	 */
	@Test(groups="SMR1093")
	public void smr1093(){
		try {
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR1093 execution started");

			final String [] simcardYN,simcardN;
			final String batchName=testDataOR.get("batch_name"),ICIDRANGETXT="ICCID range",
					simcardyn=testDataOR.get("sim_cards_yn"),simcardn=testDataOR.get("sim_cards_n");

			final int noOfCrdsBfrSimBch,noOfCrdsAfrSimBch,totBeforSimImpt,totAfterSimImp;

			//Access Everest with a superuser
			//Go to "GPRS SIM Management"
			logger.info("Step 1, 2 :");
			navToSubPage("gprs_tab_xpath","simmngmtsubpage_xpath",SIMMNGMNT);
			logger.info("Navigated to GPRS SIM Management");
			
			//Calculate no of SIM  Batched before sim card batching 
			noOfCrdsBfrSimBch=simBfrAfrImp("simbatched_xpath");

			//Calculate no of SIM not Batched before sim batching 
			totBeforSimImpt=simBfrAfrImp("simnotbatched_xpath");
			
			simcardYN=simcardyn.split(",");
			simcardN=simcardn.split(",");

			//Click on SIM not batched
			logger.info("Step 3 :");
			selUtils.clickOnWebElement(selUtils.getObject("simnotbatched_xpath"));
			logger.info("Clicked on SIM not batched link");
			
			//Select sim cards,Create Batch
			logger.info("Step 4 :");
			vColValPresence(COLICCID,simcardYN[0]);
			clkChBxOfSimYnorN(simcardYN);
			clkChBxOfSimYnorN(simcardN);
			selUtils.clickOnNavPaneItems(selUtils.getObject("action_sim_xpath"),ATTRCLAS,VALCLOSE);
			selUtils.clickOnWebElement(selUtils.getObject("crtbtch_xpath"));
			createBatch("createBtch_modwin_xpath",batchName);

			//Go to "GPRS>>SIM Management
			logger.info("Step 5,6 :");
			navToSubPage("gprs_tab_xpath","simmngmtsubpage_xpath",SIMMNGMNT);
			logger.info("Navigated to GPRS SIM Management");
			vColValPresence(BATCHNAME,batchName);
			
			//verify ICCID range
			totNoSimCrdsYN=simcardYN.length;
			totNoSimCrdsN=simcardN.length;
			totNoSimCrds=(totNoSimCrdsYN+totNoSimCrdsN);
			String simCount=Integer.toString(totNoSimCrds);

			xpath=getPath("iccid_range_xpath").replace("BATCHNAME",batchName).replace("SIMCARDSTART", simcardYN[0].trim()).replace("SIMCARDEND", simcardN[totNoSimCrdsN-1].trim());
			selUtils.verifyElementDisp(selUtils.getObjectDirect(By.xpath(xpath)),ICIDRANGETXT);

			//to Verify Count,get col index of Batch name and Count col
			int colIndexCountcol=selUtils.getIndexForColHeader("gprscolheader_css", COUNTCOL);
			xpath=getPath("sim_countcol_xpath").replace("BATCHNAME",batchName).replace("INDEXCOUNTCOL", colIndexCountcol+"").replace("INDEXBATCHCOL", colIndex+"");
			String countVal=selUtils.getObjectDirect(By.xpath(xpath)).getText();
			assertEquals(countVal, simCount);
			
			//No of sim cards in the sim batched list must have been increased
			//by the number selected simcards
			noOfCrdsAfrSimBch=simBfrAfrImp("simbatched_xpath");
			vSimCardDiff(noOfCrdsAfrSimBch,noOfCrdsBfrSimBch,totNoSimCrds,SIMBTCHDTXT);
			
			//No of sim not batched after sim batching
			totAfterSimImp=simBfrAfrImp("simnotbatched_xpath");
			vSimCardDiff(totBeforSimImpt,totAfterSimImp,totNoSimCrds,SIMNTBTCHDTXT);
			
			//Click on sim not batched,verify selected simcards must have been
			//removed from the sim not batched list
			selUtils.clickOnWebElement(selUtils.getObject("simnotbatched_xpath"));
			colIndex = selUtils.getIndexForColHeader("gprscolheader_css", COLICCID);
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			vSCrdsNtInSBchLst("iccid_colvals_xpath",colIndex,simcardYN);
			vSCrdsNtInSBchLst("iccid_colvals_xpath",colIndex,simcardN);
			
			//click on batch name in the simbatched list ,verify simbatched 
			//simcards
			navToSubPage("gprs_tab_xpath","simmngmtsubpage_xpath",SIMMNGMNT);
			vColValPresence(BATCHNAME,batchName);
			clkOnDirectObj("batchname_xpath", "BATCH", batchName);
			colIndex=selUtils.getIndexForColHeader("gprscolheader_css", COLICCID);
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			vSimCards(simcardYN);
			vSimCards(simcardN);

			logger.info("SMR1093 is successfully executed");
			
		}catch (Throwable t) {
			handleException(t);
		}


	}

	/**
	 * Method to Sim Cards Batch 
	 * @param modWinLoc
	 * @param batchname
	 */
	private void createBatch(String modWinLoc,String batchname){
		if(getModWinDisp(selUtils.getObject(modWinLoc),CREATEBATCH))
		{
			selUtils.populateInputBox("crt_btchname_id",batchname);
			selUtils.clickOnWebElement(selUtils.getObject("btch_okbtn_id"));
			if(selUtils.getCommonObject("batcherrmsg_id").isDisplayed())
			{
				String errText=selUtils.getCommonObject("batcherrmsg_id").getText().trim();	
				Assert.fail("failed due to "+errText);				
			}
			logger.info("Clicked on ok button");
		}
		else{
			Assert.fail(CREATEBATCH+"is not displayed");
		}
	}


	/**
	 * Verifies sim cards not in iccid cols
	 * @param locator2
	 * @param index
	 * @param simcards
	 */
	private void vSCrdsNtInSBchLst(String locator, int index,String simcards[])

	{

		if(selUtils.isElementPresentCommon("page_id")){
			int pageNum=0,pageIter=0;
			String[] pageItems=getListItems(selUtils.getCommonObject("page_id"));
			if(pageItems.length>=maxNoPageCount){
				pageIter=maxNoPageCount;
			}
			else{
				pageIter=pageItems.length;
			}

			outerloop:for(count=0;count<pageIter;count++){
				page=selUtils.getCommonObject("page_id");
				selUtils.selectItem(selUtils.getCommonObject("page_id"),pageItems[count]);
				waitMethods.waitForWebElementPresent(selUtils.getCommonObject("page_id"));

				String xpath = getPath(locator).replace("INDEX", Integer.toString(index+1));
				List<WebElement> colValList = selUtils.getObjectsDirect(By.xpath(xpath));
				
				List<String> cardsList = Arrays.asList(simcards);
				for (iter = 1; iter < colValList.size(); iter++) {
					if(cardsList.contains(colValList.get(iter).getText())){
						Assert.fail("Sim cards from the sim not batched list are not disaapeared after Batching");
						break outerloop;
					}
					
				}
				pageNum++;
			}
			if(pageNum==pageIter){
				logger.info("The sim cards after batching are not in the sim not batched list");
			}
		}
		else{
			vSimNotPresent(locator,index,simcards);
		}
	}



	/**
	 * Method to verify sim not present
	 * @param locator2
	 * @param index
	 * @param simcards
	 */
	private void vSimNotPresent(String locator, int index, String simcards[])

	{
		String xpath = getPath(locator).replace("INDEX", Integer.toString(index+1));
		List<WebElement> colValList = selUtils.getObjectsDirect(By.xpath(xpath));
		
		List<String> cardsList = Arrays.asList(simcards);
		for (iter = 1; iter < colValList.size(); iter++) {
			if(cardsList.contains(colValList.get(iter).getText())){
				Assert.fail("Sim cards from the sim not batched list are not disappeared after Batching");
			}
			
		}
		logger.info("The sim cards after batching are not in the sim not batched list");
	}
	
	/**
	 * check the checkbox of simcards
	 * @param simcr
	 */
	private void clkChBxOfSimYnorN(String simcr[])
	{
		for (String arr:simcr) 
		{
			clkOnDirectObj("simcrd_ckbx_xpath", "SIMCARDS", arr.trim());
		}
		
	}
}


