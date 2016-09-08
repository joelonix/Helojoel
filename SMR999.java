package com.ingenico.testsuite.gprs;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR999.java $
$Id: SMR999.java 17094 2016-02-16 11:36:45Z haripraks $
 */
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *  SMR-999:Create a new GPRS provider
 * @author Raghunath.K
 *
 */
public class SMR999 extends SuiteGprs {
	
	/**
	 *  Create a new GPRS provider
	 *  
	 */
	@Test(groups="SMR999")
	public void smr999()  {		
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			final String providerName=testDataOR.get("provider"),
			ggsn1=testDataOR.get("ggsn_1"),ggsn2=testDataOR.get("ggsn_2"),
			mcc=testDataOR.get("mcc"),mnc=testDataOR.get("mnc");
			
			logger.info("SMR999 execution started");
			//Access Everest with the Everest Superuser
			//Go to "GPRS" and then go to" Provider Management"
			logger.info("Step 1, 2:");
			navigateToSubPage(PVDRMGMT,selUtils.getCommonObject("gprs_tab_xpath"),selUtils.getCommonObject("prvmgmt_tab_xpath"));
			
			//Click on the 'Add Provider' menu
			logger.info("Step 3:");
			selUtils.clickOnWebElement(selUtils.getObject("addprovider_link"));
			logger.info("Clicked on add provider link");
			
			//Add new Provider 
			logger.info("Step 4:");
			addProvider(providerName,ggsn1,ggsn2,mcc,mnc);
						
			//Click on the Add button and New provider shall be displayed in the Providers List
			logger.info("Step 5:");
			selUtils.clickOnWebElement(selUtils.getObject("addpvdr_id"));
			logger.info("Clicked on the add button");
			
			//New provider is dispalyed in the provider list		
			colIndex=selUtils.getIndexForColHeader("prvmgmt_colheaders_xpath",PVDRNAMECOL);
			verifyLvlColLvlValPresence("pvdrtbl_id",colIndex,providerName);
			logger.info("Verified the providername value  "+providerName+" is displayed in the  Provider list");
			
			logger.info("SMR999 execution successful");
		}
		catch (Throwable t) {
			handleException(t);
		}

	}
	
	/**
	 * Add Provider
	 * @param providername
	 * @param ggsn1
	 * @param ggsn2
	 * @param mcc
	 * @param mnc
	 */
	private void addProvider(String providerName,String ggsn1,String ggsn2,String mcc,String mnc)
	{
		String strErrorMsg;
		selUtils.populateInputBox("pvdrname_id", providerName);
		selUtils.populateInputBox("pvdrggsn1_id", ggsn1);
		selUtils.populateInputBox("pvdrggsn2_id", ggsn2);
		selUtils.populateInputBox("pvdrmcc1_id", mcc);
		selUtils.populateInputBox("pvdrmnc1_id", mnc);
		selUtils.clickOnWebElement(selUtils.getObject("addpvdr_vlidline_xpath"));
		logger.info("Clicked on the Validate line button");
		
		//verify error messages
		if(selUtils.getCommonObject("addpvdr_errmsg_id").isDisplayed())
		{
			strErrorMsg=selUtils.getCommonObject("addpvdr_errmsg_id").getText().trim();
			Assert.fail(strErrorMsg+" due to error");	
		}
		
	}
	
}
