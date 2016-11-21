package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1729.java $
$Id: EPL_1729.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1729 extends SuiteCardPayment
{
	/**
	 * EPL-1729 MID Tab / Search for  several applications/ presence of “application type” column
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1729()
	{		
		try{
			logger.info("  EPL-1729 started execution");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);				
			
			//Navigate to the last level and verify if MID tab is displayed		
			logger.info("Step 2:");
			getObject("all_level_id").click();
			getObject("settlement_id").click();
			selectItem(getObject("period_id"), lastMonth);
			getObject("search_link").click();
			getObject("settlement_id").click();
			verifyElementDisp("mid_id");					
			
			//Verify header columns that Application Type column is present
			logger.info("Step 3:");
			verifyPresenceOfCol("mid_headers_xpath", colAppType);
									
			//Verify icon columns that Application Type column is present
			getObject("column_icon_xpath").click();	
			verifyPresenceOfCol("columns_xpath", colAppType);			
			logger.info(" In icon columns Application Type column is present");
			logger.info(" EPL-1729 execution successful");
			} 
		catch (Throwable t)
		{
			handleException(t);	
		}	
	}
	
}
