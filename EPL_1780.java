package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1780.java $
$Id: EPL_1780.java 7916 2014-06-10 12:30:12Z cariram $
 */
import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1780 extends SuiteCardPayment
{
	/**
	 * EPL-1780 To Verify the presence of submodules under Card Payment Module
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1780()
	{		
		try
		{
			logger.info(" EPL-1780 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));	
			logger.info("Step 1:");
			//Navigate to Snapshot sub module of card payment module and verify if snapshot module is visible
			cardPaymentSubPageNavigator(snapshotBC);
			verifyBreadCrumb(snapshotBC);
			logger.info("Step 2:");
			logger.info("Snapshot submodule is visible");

			//Navigate to Transaction overview of card payment module and verify if its visible
			cardPaymentSubPageNavigator(subModTrnOvr);
			verifyBreadCrumb(cpTransOverBC);
			logger.info("Transaction Overview submodule is visible");

			//Navigate to Transaction Journal of card payment module and verify if its visible
			cardPaymentSubPageNavigator(subModTrnJrn);
			verifyBreadCrumb(cpTransJourBC);
			logger.info("Transaction Journal submodule is visible"); 

			//Navigate to Transaction Reconciliation module and verify if its visible
			cardPaymentSubPageNavigator(subModTrnRecon);
			verifyBreadCrumb(cpReconBC);
			logger.info("Transaction Reconciliation submodule is visible");
			logger.info(" EPL-1780 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
}


