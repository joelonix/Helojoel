package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR52.java $
$Id: SMR52.java 16708 2016-01-20 09:53:29Z rjadhav $
 */

import org.testng.annotations.Test;
/**
 * SMR-52:Upload Profile
 * @author Nagaveni.Guttula
 *
 */
public class SMR52  extends SuiteCardPayment{

	@Test(groups={"SMR52"})
	/**
	 * Upload an application profile
	 */
	public void smr52() {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR52 execution started");	
			final String admFldrNam="AQA_ADM",emvFldrNam="AQA_EMV",appProfileName=testDataOR.get("admfr_profile"),
				emvFRProf=testDataOR.get("emvfr_profile"),custName=testDataOR.get("customer");
			
			profileCrt(admFldrNam);
			profilezip(appProfileName,tempFldrNme);
			profileCrt(emvFldrNam);
			profilezip(emvFRProf,tempFldrNme);

			//Creating the Profiles.zip file with the contents of ADM,AMV and EMV
			zipProfiles();

			//Access Everest with the Everest superuser,GOTO to Card Payment
			//-Profile"and assign profile to the customer
			logger.info("Step 1,2,3:");
			vExistsNAddProfile(appProfileName,custName);			

			//GOTO to CP-CustomerProvisioning and check profile exists to customer
			logger.info("Step 4:");
			vProfileExistsToCust(appProfileName);			

			//Repeat steps 2 to 4 for each profiles 			
			logger.info("Step 5:");
			vExistsNAddProfile(emvFRProf,custName);
			vProfileExistsToCust(emvFRProf);
			logger.info("SMR52 executed successfully");
		}
		catch (Throwable t) {
			handleException(t);
		}
	}
	
}

