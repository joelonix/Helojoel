package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR53.java $
$Id: SMR53.java 16708 2016-01-20 09:53:29Z rjadhav $
 */
import org.testng.Assert;
import org.testng.annotations.Test;
/**
 * SMR-53:Create Pos Header
 * @author Nagaveni.Guttula
 *
 */
public class SMR53  extends SuiteCardPayment{

	@Test(groups={"SMR53"})
	/**
	 * Create Pos Header with a Header size from 1 to 8 digits
	 */
	public void smr53() {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR53 execution started");	

			//Access Everest with a superuser,Navigate to Card Payment-Customer 
			//Provisioning and select the customer
			logger.info("Step 1,2:");
			navigateToSubPage(CUSTPROV,selUtils.getCommonObject("cardpaymt_tab_xpath"),selUtils.getCommonObject("custprov_xpath"));
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), testDataOR.get("customer"));
			logger.info("Selected the customer as "+testDataOR.get("customer"));
			
			//Create POS header and verify in the list 			
			logger.info("Step 3:");
			selUtils.clickOnWebElement(selUtils.getObject("posnum_headertab_xpath"));
			logger.info("Clicked on the "+POSNUMHEADER+" tab");
			posHeader=createPosHeader();
			Assert.assertTrue(selUtils.getCommonObject("posheder_errmsg_id").getText().endsWith(SUCSSFULLYADDED),
					"Failed due to Message ' " + selUtils.getCommonObject("posheder_errmsg_id").getText()+" ' ");
			logger.info("Verified  the Posheader "+posHeader+" successfully created message");
			logger.info("SMR53 executed successfully");	
		}
		catch (Throwable t) {
			handleException(t);
		}
	}
}


