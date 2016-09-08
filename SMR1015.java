package com.ingenico.testsuite.gprs;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR1015.java $
$Id: SMR1015.java 16708 2016-01-20 09:53:29Z rjadhav $
 */
import org.testng.annotations.Test;

/**
 * SMR-1015:Offer type localization
 * Summary:
 * Configure Offer Type localization
 * @author Hariprasad.KS
 *
 */


public class SMR1015 extends SuiteGprs{

	/**
	 * Offer type localization
	 */
	@Test(groups="SMR1015")
	public void smr1015(){
		try {
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			final String customerName=testDataOR.get("customer"), offerTypelbl1=testDataOR.get("offer_type_label1"),offerTypelbl2=testDataOR.get("offer_type_label2"),succmsg;
			logger.info("SMR1015 execution started");

			//Access Everest with a superuser and go to eportal and select customer
			logger.info("Step 1,2 :");
			waitMethods.waitForWebElementPresent(selUtils.getCommonObject("eportal_tab_link"));
			selUtils.clickOnWebElement(selUtils.getCommonObject("eportal_tab_link"));
			selUtils.selectItem(selUtils.getCommonObject("cust_Sel_id"), customerName);
			logger.info("Navigated to eportal module and selected customer "+customerName);
			
			//In the "Offer Type Label" set labels and validate
			logger.info("Step 3 :");
			selUtils.populateInputBox("offertypelbl1_id", offerTypelbl1);
			selUtils.populateInputBox("offertypelbl2_id", offerTypelbl2);
			selUtils.clickOnWebElement(selUtils.getObject("savebutton_xpath"));
			succmsg="Customer "+customerName+" successfully updated";
			selUtils.verifyTextContains(selUtils.getCommonObject("succ_deploymsg_xpath"), succmsg);
			selUtils.verifyValueWithAttribute(selUtils.getObject("offertypelbl1_id"), "value", offerTypelbl1);
			selUtils.verifyValueWithAttribute(selUtils.getObject("offertypelbl2_id"), "value", offerTypelbl2);
			logger.info("SMR1015 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}

	}
}


