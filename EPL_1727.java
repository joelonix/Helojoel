package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1727.java $
$Id: EPL_1727.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;

import org.testng.annotations.Test;

public class EPL_1727 extends SuiteCardPayment
{
	
	private String country, city, shop;
	private String[] transHeadersArr={tabMid, colTransaction, colNetVal, colSale, colSaleAmt, colRefunds, colRefundAmt,colCanSales,
			csCanSaleAmtRow,colCanRef,cancelRefundsAmnt};
	
	

	/**
	 * EPL-1727 Check content of main view / Levels views / all applications
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1727()
	{		
		try{
			logger.info("  EPL-1727 started execution");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			
			//Navigate to Transaction Overview, search and get bread crumb
			cardPaymentSubPageNavigator(subModTrnOvr);					
			getObject("search_link").click();
			
			//Navigate to last level
			logger.info("Step 2:");
			country = navigateToLevel("settlement_id");
			city = navigateToLevel("settlement_id");
			shop = navigateToLevel("settlement_id");			
			verifyElementDisp("mid_xpath");
			
			//Verify coherency in Mid tab
			logger.info("Step 3:");
			verifyOverviewMIDBC(country, city, shop, afterLevelCardBC);
			
			//Verify header columns
			verifyPresenceOfCols("mid_headers_xpath", transHeadersArr);	
			
			//Verify list of columns present in the header of the table
			logger.info("Step 4:");
			verifyHeaderColsWithIcon("mid_headers_xpath", 1);					
			logger.info("There are exactly same columns as Main view except the MID column");			
			logger.info(" EPL-1727 execution successful");
			
		}
		catch (Throwable t)
		{
			handleException(t);	
		}	
	}
	
}
