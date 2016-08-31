package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR214.java $
$Id: SMR214.java 16708 2016-01-20 09:53:29Z rjadhav $
 */

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
/**
 * SMR-214:Move POS
 * @author Nagaveni.Guttula
 */
public class SMR214  extends SuiteCardPayment{

	/**
	 * Moving POS to other Localization 
	 */
	@Test(groups={"SMR214"})
	public void smr214() {
		try{
			eportalCust=testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR214 execution started");
			posNum=testDataOR.get("pos_number");
			final String cust=testDataOR.get("customer"),signature=testDataOR.get("pos_number");
			final String level3b=testDataOR.get("lowest_level_zone_b");

			//Access Everest with the Everest superuser,GOTO  Card Payment-
			//Customer Provisioning and select the customer
			logger.info("Step 1,2:");
			navigateToSubPage(CUSTPROV,selUtils.getCommonObject("cardpaymt_tab_xpath"),selUtils.getCommonObject("custprov_xpath"));
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), cust);

			//Go to POS Declaration sub-tab
			logger.info("Step 3:");
			selUtils.clickOnWebElement(selUtils.getObject("posdecltab_xpath"));
			logger.info("Clicked on the POS Declaration TAB");

			// Select the POS,then click "CardPayment >> Move POS" from the left menu.
			logger.info("Step 4:");
			clkOnDirectObj("selposnum_xpath", "POSNUM", posNum);
			selUtils.clickOnNavPaneItems(selUtils.getObject("leftmenucp_xpath"), ATTRCLAS,VALCLOSE);
			selUtils.clickOnWebElement(selUtils.getObject("movepos_link"));
			logger.info("Clicked on the Move Pos link");

			//Select a lowest level destination zone lowest_level_zone_b and validate
			logger.info("Step 5:");
			selNVerifyPosLocaliztion(level3b);
			logout();
			logger.info("Logout successfully");
			//Access ePortal,select the customer go to "TMS >> Terminals"
			logger.info("Step 6,7:");
			epLoginNSelClient(cust,testDataOR.get("superuser"));
			selUtils.getCommonObject("tms_link").click();
			selUtils.switchToFrame();
			selUtils.clickOnNavPaneItems(selUtils.getCommonObject("tmsactions_xpath"),ATTRCLAS,VALCLOSE );
			selUtils.clickOnWebElement(selUtils.getCommonObject("terminals_link"));
			
			// click on Find and Search button
			selUtils.clickOnWebElement(selUtils.getCommonObject("find_link"));
			selUtils.clickOnWebElement(selUtils.getCommonObject("srch_link"));
			logger.info("Clicked on the find and search buttons of terminals");

			//click on the "Edit" button of the terminal with signature
			logger.info("Step 8:");
			//clkOnEditTerminal("pageindex_id","editterminal_xpath",signature);
			clkOnElement("pageindex_id","editterminal_xpath",signature);
			selUtils.verifyObjDirectDisp("estwithsigntur_xpath","SIGNTUR" , signature);
			logger.info("Verified the estate with signature as "+signature);
			selUtils.verifyObjDirectDisp("estlvl_xpath", "LEVEL3",level3b);
			logger.info("Verified the estate with "+ level3b);
			//driver.switchTo().defaultContent();
			
			logger.info("SMR214 Executed Successfully");
		}
		catch (Throwable t) {
			handleException(t);
		}
	}
	
	/**
	 * This method used for selecting the new localization 
	 */
	private void selNVerifyPosLocaliztion(String zoneLvl){
		if(getModWinDisp(selUtils.getObject("selnewzonpup_xpath"), SELECTNEWZONE)){
			selUtils.selectItem(selUtils.getObject("newlocalztion_id"), zoneLvl);
			logger.info("Selected  Localization as "+ zoneLvl);
			selUtils.clickOnWebElement(selUtils.getObject("movebttn_id"));
			selUtils.clickOnWebElement(selUtils.getObject("movepos_nxt_id"));
			selUtils.clickOnWebElement(selUtils.getObject("posdecltab_xpath"));
			colIndex=selUtils.getIndexForColHeader("colheaders_css", POSNUMBERCOL);
			verifyLvlColLvlValPresence("provPoslst_css", colIndex,posNum );
			xpath=getPath("posass_withzone_xpath").replace("POSNUM", posNum);
			xpath=xpath.replace("LEVEL3", zoneLvl);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			Assert.assertTrue(selUtils.getObjectDirect(By.xpath(xpath)).isDisplayed(), " element is not displayed");
			logger.info(" Verified the Pos "+ posNum+" moved to new localiztion as"+ zoneLvl);
			
		}
	}
}

				
	
	
