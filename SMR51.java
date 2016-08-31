package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR51.java $
$Id: SMR51.java 16708 2016-01-20 09:53:29Z rjadhav $
 */

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;
/**
 * SMR-51:Create POS
 * @author Nagaveni.Guttula
 *
 */
public class SMR51  extends SuiteCardPayment{

	@Test(groups={"SMR51"})
	/**
	 * Declare a POS manually from Everest
	 */
	public void smr51() {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR51 execution started");
			final String dbAxis=testDataOR.get("databaseAxis"),dbTam=testDataOR.get("databaseTAM"),dbIngestate=testDataOR.get("databaseIngEstate"),
					numptv,signature,name;
			posPosNumHeader=testDataOR.get("pos_number");
			projectName=testDataOR.get("project_name");

			//Access Everest,Go to Card Payment-Customer Provisioning 
			//and select the customer 
			logger.info("Step 1,2:");
			navigateToSubPage(CUSTPROV,selUtils.getCommonObject("cardpaymt_tab_xpath"),selUtils.getCommonObject("custprov_xpath"));
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), testDataOR.get("customer"));

			//verify and delete existing project name for pos creation
			selUtils.clickOnWebElement(selUtils.getCommonObject("pendingprovtab_xpath"));
			verifyExistingData("deleteprojectname_xpath","colheaders_css","colallrows_xpath",projectName, PROJECTNAME);

			//Go to "POS Declaration" sub-tab and click the "+" button			
			logger.info("Step 3:");
			selUtils.clickOnWebElement(selUtils.getObject("posdecltab_xpath"));
			logger.info("Clicked on the POS Declaration TAB");
			selUtils.clickOnWebElement(selUtils.getObject("addprov_xpath"));
			logger.info("Clicked on the plus button");

			//If  there are more than one Axis location select axis_location
			//And click next button
			logger.info("Step 4:");
			selAxisLoc(testDataOR.get("axis_location"));

			//Select an existing ADM profile adm_profile and click 'Next' button			 
			logger.info("Step 5:");
			Assert.assertTrue(getModWinDisp(selUtils.getObject("selctprofilewin_xpath"), SELECTPROFILE),"Modal window is not displayed");
			selUtils.selectItem(selUtils.getObject("profile_id"), testDataOR.get("admfr_profile").replaceAll(".zip", "").trim());
			logger.info("Selected the profile as "+ (testDataOR.get("admfr_profile").replaceAll(".zip", "")).trim());
			selUtils.clickOnWebElement(selUtils.getObject("profilenext_id"));

			//Select 'Manual localization' in the "Order" drop down list			
			logger.info("Step 6:");
			selUtils.selectItem(selUtils.getObject("operationorder_id"), MANUALLOCALZTION);
			logger.info("Selected "+MANUALLOCALZTION);
			selUtils.clickOnWebElement(selUtils.getObject("orderopenext_id"));

			//Select a lowest level zone <lowest_level_zone_a> from the drop down list			
			logger.info("Step 7:");
			selUtils.selectItem(selUtils.getObject("localization_id"), testDataOR.get("lowest_level_zone_a"));
			logger.info("Selected the level as "+ testDataOR.get("lowest_level_zone_a"));
			selUtils.clickOnWebElement(selUtils.getObject("localizationnext_xpath"));


			//Enter POS details and click Next			
			logger.info("Step 8:");
			selUtils.clickOnWebElement(selUtils.getObject("enterposanual_xpath"));
			selUtils.clickOnWebElement(selUtils.getObject("addpos_xpath"));
			selUtils.populateInputBox("posnumber_id", posPosNumHeader);
			logger.info("Entered the value as "+ posPosNumHeader);
			selUtils.selectValueInDropDown(selUtils.getObjects("postypeitems_xpath"), EMBEDDED);

			wait.until(ExpectedConditions.visibilityOf(selUtils.getObject("addposgbutt_xpath")));
			selUtils.scrollToView(selUtils.getObject("addposgbutt_xpath"));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObject("addposgbutt_xpath"));
			vErrorMessage("poserr_id");

			//selUtils.clickOnWebElement(selUtils.getObject("addposgbutt_xpath"));
			selUtils.clickOnWebElement(selUtils.getObject("enterposnext_xpath"));
			reportErrMessage("pos_num_err_id");
			
			//Click 'Next' button			
			logger.info("Step 9:");
			selUtils.clickOnWebElement(selUtils.getObject("viewconfignext_id"));
			logger.info("Clicked on next button");

			//Enter a 'Project Name' project_name   click on the 'Save' button			
			logger.info("Step 10:");
			selUtils.populateInputBox("projname_id", projectName);
			logger.info("Entered the projectName as "+projectName);
			selUtils.clickOnWebElement(selUtils.getObject("configsumsavebttn_xpath"));
			waitNSec(3);

			//Go to "Pending Provisioning" sub-tab			
			logger.info("Step 11, 12:");
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), testDataOR.get("customer"));
			/*
			 * Above step is just a work around step,since we faced issue when 
			 * on pending provisioning tabclicking
			 */

			//Click the edit button of the pending operation			
			clkOnPendingProv(projectName);
			clkOnDirectObj("editpospending_xpath","NAME",projectName);
			logger.info("Clicked on the edit button in Pending Provisioning Tab for the project");

			//Click on the 'Deploy'and'OK' button to confirm the deployment request			
			logger.info("Step 13:");
			clickOnDeploy();
			Assert.assertTrue(selUtils.getCommonObject("succ_deploymsg_xpath").getText().contains(SUCSSFULLYDPLYD),SUCSSFULLYDPLYD+"sucess message does not appear");
			logger.info("Verified the success message");
			clckOnProvOrPosTab("posdecltab_xpath", POSDECTAB);

			//searchVal(posPosNumHeader, "posnumfilter_id", VALEQ, "posnumtxt_id");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#tabListPos>tbody")));
			colIndex=selUtils.getIndexForColHeader("colheaders_css", POSNUMBERCOL);
			verifyLvlColLvlValPresence("provPoslst_css",colIndex,posPosNumHeader);
			logger.info("Verified the created POS, "+posPosNumHeader+" is displayed under POS Declarartion Tab");

			if(dbCheck){
				//Verifying the Created posNumber header in axis_database
				String sql= "SELECT numtpv FROM adm.tpvadm WHERE numtpv='"+posPosNumHeader+"';";
				resSet = dbMethods.getDataBaseVal(dbAxis,sql,CommonConstants.ONEMIN);
				numptv=resSet.getString("numtpv");
				Assert.assertTrue(numptv.equals(posPosNumHeader), posPosNumHeader+" value is not stored in database");
				logger.info("Verified numtpv column is having  "+ posPosNumHeader );

				//Verifying the Created posNumber header in both TAM 
				sql= "SELECT signature FROM ingenico_terminal WHERE signature LIKE '%"+posPosNumHeader+"';";

				resSet = dbMethods.getDataBaseVal(dbTam,sql,CommonConstants.ONEMIN);
				signature=resSet.getString("signature");
				Assert.assertTrue(signature.endsWith(posPosNumHeader), posPosNumHeader+" value is not stored in database");
				logger.info("Verified signature column is finishing by "+ posPosNumHeader );

				//Verifying the Created posNumber header in IngEstate Databases
				sql="SELECT name FROM terminal WHERE name LIKE '%"+posPosNumHeader+"';";
				resSet = dbMethods.getDataBaseVal(dbIngestate,sql,CommonConstants.ONEMIN);
				name=resSet.getString("name");
				Assert.assertTrue(name.endsWith(posPosNumHeader), posPosNumHeader+" value is not stored in database");
				logger.info("Verified name column is finishing by "+ posPosNumHeader );
			}
			
			logger.info("SMR51 executed successfully");
		}
		catch (Throwable t) {
			handleException(t);
		}
	}
}











