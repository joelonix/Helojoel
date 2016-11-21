package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1728.java $
$Id: EPL_1728.java 7955 2014-06-11 12:37:02Z cariram $
*/
import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1728 extends SuiteCardPayment
{
	/**
	 * EPL-1728 MID Tab / case of user with only one application / no presence of “application type” column
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1728()
	{		
		try{
			logger.info("  EPL-1728 started execution");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);				
			
			//Navigate to the last level and verify if MID tab is displayed	
			logger.info("Step 2:");
			getObject("settlement_id").click();
			getObject("settlement_id").click();
			selectItem(getObject("period_id"), lastMonth);
			getObject("search_link").click();
			getObject("settlement_id").click();
			verifyElementDisp("mid_id");
			
			//Verify Search after selecting 'EMV AUS' Application type
			logger.info("Step 3:");
			selectItem(getObject("select_appli_id"), emvAUSApp);
			getObject("search_link").click();
			getObject("settlement_id").click();			
			
			//Verify header columns that Application Type column is not there
			logger.info("Step 4:");
			verifyNoPresenceOfCol("mid_headers_xpath", colAppType);				
			
			//Verify icon columns that Application Type column is not there
			getObject("column_icon_xpath").click();	
			verifyNoPresenceOfCol("columns_xpath", colAppType);			
			logger.info(" In icon columns Application Type column is not there");
			logger.info(" EPL-1728 execution successful");
			} 
		catch (Throwable t)
		{
			handleException(t);	
		}	
	}
	
}
