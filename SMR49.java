package com.ingenico.testsuite.customermanagement;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/customermanagement/SMR49.java $
$Id: SMR49.java 18300 2016-04-28 08:23:55Z rkahreddyga $
 */
import org.testng.annotations.Test;

/**
 * SMR-49:Add Zones
 * Adding zones from Everest and checking the creation in the other components.
 * Preconditions:
 * A superuser exists
 * An undeployed customer exists (as described in test case SMR-1872)
 * @author Hariprasad.KS
 *
 */

public class SMR49 extends SuiteCustomerManagement{

	/**
	 * Add Zones
	 */
	@Test(groups="SMR49")
	public void smr49(){
		try {
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			final String custName,currencya,currencyb;
			final int zoneLevel;
			final String [] zoneNames_a,zoneNames_b;
			zoneNames_a=new String []{testDataOR.get("zone_nameone_a"),testDataOR.get("zone_nametwo_a"),testDataOR.get("zone_namethree_a")};
			zoneNames_b=new String []{testDataOR.get("zone_nameone_b"),testDataOR.get("zone_nametwo_b"),testDataOR.get("zone_namethree_b")};
			zoneLevel=Integer.parseInt(testDataOR.get("depth"));
			custName=testDataOR.get("customer");currencya=testDataOR.get("level_1_a_currency");currencyb=testDataOR.get("level_1_b_currency");
			logger.info("SMR49 execution started");
			
			// Access Everest with a superuser,Go to "Customer" module
			logger.info("Step 1,2:");
			navigateToCustomerPage();

			//Click on the name of the <customer>
			logger.info("Step 3 :");
			clkCustNameList(custName);
						
			//Go to "Structure" tab
			logger.info("Step 4 :");
			//selUtils.clickOnWebElement(selUtils.getObject("structure_tab_xpath"));
			selUtils.jScriptClick(selUtils.getObject("structure_tab_xpath"));
			logger.info("Clicked on the '"+STRUCTURE+ "' Tab" +" of the customer");

			//choose structure depth,currency and enter zone names
			// then validate zones appearing with application
			logger.info("Step 5,6 :");
			addZoneLevels(zoneNames_a, currencya, zoneLevel);
			vAddedZones(zoneLevel,zoneNames_a);

			addZoneLevels(zoneNames_b, currencyb, zoneLevel);
			vAddedZones(zoneLevel,zoneNames_b);

			logger.info("SMR49 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}

	}

}
