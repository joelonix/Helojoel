package com.ingenico.testsuite.gprs;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR1016.java $
$Id: SMR1016.java 16708 2016-01-20 09:53:29Z rjadhav $
 */
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * SMR-1016:Add offer types
 * Summary:
 * Add offer types to a customer
 * @author Hariprasad.KS
 *
 */

public class SMR1016 extends SuiteGprs{

	/**
	 * Offer type localization
	 */
	@Test(groups="SMR1016")
	public void smr1016(){
		try {
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			final String customerName=testDataOR.get("customer"),providerName=testDataOR.get("provider"),offerName=testDataOR.get("offer_name"),locpath;
			logger.info("SMR1016 execution started");

			//Access Everest with a superuser
			//Go to "GPRS - GPRS Management"
			logger.info("Step 1, 2 :");
			navigateToSubPage(GPRSMGMT,selUtils.getCommonObject("gprs_tab_xpath"),selUtils.getCommonObject("gprsmngmtsubpage_xpath"));
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"),customerName);
			logger.info("Selected the customer "+customerName);

			//In Offer Type, click the "+" button
			logger.info("Step 3 :");
			selUtils.clickOnWebElement(selUtils.getObject("addoffername_xpath"));
			logger.info("Clicked on + button of Offer Type");
			
			//Add offer type and validate
			logger.info("Step 4 :");
			selUtils.selectItem(selUtils.getObject("inputprovadd_id"), providerName);
			selUtils.populateInputBox("inputofferadd_id", offerName);
			selUtils.clickOnWebElement(selUtils.getObject("inputoffernames_id"));
			selUtils.clickOnWebElement(selUtils.getObject("applybutton_xpath"));
			logger.info("Offer type is added");
			locpath=getPath("offertypetab_xpath").replace("PROV", providerName).replace(NAME, offerName);
			Assert.assertTrue(selUtils.getObjectDirect(By.xpath(locpath)).isDisplayed(), "Offer type is not displayed");
			logger.info("Offer name "+offerName+" for "+providerName+" is displayed in Offer type list");
			
			logger.info("SMR1016 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}

	}
}


