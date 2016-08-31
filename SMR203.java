package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR203.java $
$Id: SMR203.java 16708 2016-01-20 09:53:29Z rjadhav $
 */
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Test;

/**
 * SMR-203:Move Numcomm
 * @author Hariprasad.KS 
 *
 */

public class SMR203  extends SuiteCardPayment{

	@Test(groups={"SMR203"})
	/**
	 * Move Numcomm
	 */
	public void smr203() {
		try{
			eportalCust=testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR203 execution started");	
			final String customer=testDataOR.get("customer"),zoneb=testDataOR.get("lowest_level_zone_b"),numcomm=testDataOR.get("num_comm");
			final List<String> allColData;
			String numberOfPOSs[];

			//Access Everest with a superuser,Go to "Card Payment, 
			//Customer Provisioning" sub menu
			logger.info("Step 1,2:");
			navigateToSubPage(CUSTPROV,selUtils.getCommonObject("cardpaymt_tab_xpath"),selUtils.getCommonObject("custprov_xpath"));
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), customer);

			//Go to "Provisioning" sub-tab 
			logger.info("Step 3:");
			selUtils.clickOnWebElement(selUtils.getObject("provtab_xpath"));
			logger.info("Clicked on Provisioning tab");

			//Retrive the POSs numbers by clicking details link for the numcomm
			clkOnDirectObj("detailsnumcom_xpath", "NUMCOMM", numcomm);
			logger.info("Clicked on details link  of "+numcomm+" to retrive POSs");
			allColData=getColData("colheaders_css",POSNUMBERCOL,"posallcol_xpath");
			numberOfPOSs = allColData.toArray(new String[allColData.size()]);
			for(int posNum=0;posNum<numberOfPOSs.length;posNum++)
			{
				logger.info("POS number"+numberOfPOSs[posNum]);
			}
			selUtils.clickOnWebElement(selUtils.getObject("provtab_xpath"));
			logger.info("Clicked on Provisioning tab");

			//Click the "Move numcomm" button of a num_comm with associated POSs
			//in lowest_level_a
			logger.info("Step 4:");
			clkOnDirectObj("movenumcom_xpath", "NUMCOMM", numcomm);
			logger.info("Clicked on Move numcomm button of numcomm "+numcomm);

			//Select the new zone lowest_level_b, move and validate
			logger.info("Step 5:");
			selUtils.selectItem(selUtils.getObject("newlocalztion_id"), zoneb);
			logger.info("Selected  Localization as "+ zoneb);
			selUtils.clickOnWebElement(selUtils.getObject("movebttn_id"));
			selUtils.clickOnWebElement(selUtils.getObject("movepos_nxt_id"));
			selUtils.clickOnWebElement(selUtils.getObject("provtab_xpath"));

			xpath=getPath("lewestzone_xpath").replace("NUMCOMM",numcomm).replace(NAME, zoneb);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			selUtils.verifyElementDisp(selUtils.getObjectDirect(By.xpath(xpath)), zoneb);
			logger.info(" Verified the numcomm "+ numcomm+" moved to new localiztion as"+ zoneb);

			for(int posNum=0;posNum<numberOfPOSs.length;posNum++)
			{
				selUtils.clickOnWebElement(selUtils.getObject("posdecltab_xpath"));
				xpath=getPath("posass_withzone_xpath").replace("POSNUM", numberOfPOSs[posNum]).replace("LEVEL3", zoneb);
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
				selUtils.verifyElementDisp(selUtils.getObjectDirect(By.xpath(xpath)), zoneb);
				logger.info(" Verified the Pos "+ numberOfPOSs[posNum]+" moved to new localiztion as"+ zoneb);
			}

			logger.info("Step 6:");
			logoutEpSelCust(customer);

			logger.info("Step 7:");
			selUtils.getCommonObject("tms_link").click();
			selUtils.switchToFrame();
			selUtils.clickOnNavPaneItems(selUtils.getCommonObject("tmsactions_xpath"),ATTRCLAS,VALCLOSE );
			selUtils.clickOnWebElement(selUtils.getCommonObject("terminals_link"));
			// click on Find and Search button
			selUtils.clickOnWebElement(selUtils.getCommonObject("find_link"));
			selUtils.clickOnWebElement(selUtils.getCommonObject("srch_link"));
			logger.info("Clicked on the find and search buttons of terminals");

			logger.info("Step 8:");
			for(int posNum=0;posNum<numberOfPOSs.length;posNum++)
			{
				clkOnElement("pageindex_id","editterminal_xpath",numberOfPOSs[posNum]);
				selUtils.verifyObjDirectDisp("estlvl_xpath", "LEVEL3",zoneb);
				logger.info("Verified the estate with "+ zoneb);
				driver.navigate().back();
			}
			//driver.switchTo().defaultContent();
			logger.info("SMR203 executed successfully");	
		}
		catch (Throwable t) {
			handleException(t);
		}
	}
}
