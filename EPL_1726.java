package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1726.java $
$Id: EPL_1726.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1726 extends SuiteCardPayment
{	
	
	private String country, city, countryFra;
	private String[] transHeadersArr={colTransaction, colNetVal, colGlobalSale, colGlobalSaleAmt, colRefunds, colRefundAmt};
	private String[] transHeadersFraArr={colTransaction, colNetVal, colSale, colSaleAmt, colRefunds, colRefundAmt};
		
	

	/**
	 * EPL-1726 Check content of main view / Levels views / all applications
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1726()
	{		
		try{
			logger.info("  EPL-1726 started execution");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			
			//Navigate to Transaction Overview, search and get bread crumb
			cardPaymentSubPageNavigator(subModTrnOvr);					
			getObject("search_link").click();			
			
			//verify bread crumb with all application search
			logger.info("Step 2, 3:");
			verifyOverviewBC(defaultOverviewBC);			
			Assert.assertEquals(getObject("column_width_xpath").getText(),colCountry); 
			
			//Verify header columns
			verifyPresenceOfCols("transover_header_xpath", transHeadersArr);			
			logger.info("In Main view for level 1: the coherency of breadcrumb is as expected and only Level 1 are displayed");
			
			//Verify list of columns present in icon below the table
			logger.info("Step 4:");
			verifyHeaderColsWithIcon("get_table_xpath", 1);				
			logger.info("There are exactly same columns as Main view except the Level 1 (country) column");
			
			//Verify bread crumb after selecting France
			logger.info("Step 5:");
			countryFra=getObject("all_level_id").getText();
			getObject("all_level_id").click();
			logger.info("Country "+countryFra+" is selected");					
			
			//verify bread crumb with France country
			verifyOverviewBC(beforeLevelBC+countryFra+afterLevelBC);			
			Assert.assertEquals(getObject("column_width_xpath").getText(),colCity); 			
			
			//Verify header columns	
			verifyPresenceOfCols("transover_header_xpath", transHeadersFraArr);				
			logger.info("In Main view for "+countryFra+" : the coherency of breadcrumb is as expected and only cities are displayed");
			
			//Verify list of columns present in icon below the table
			logger.info("Step 6:");
			verifyHeaderColsWithIcon("get_table_xpath", 1);					
			logger.info("There are exactly same columns as Main view for country "+countryFra);
			
			//verify bread crumb with level 2 search
			logger.info("Step 7:");
			cardPaymentSubPageNavigator(subModTrnOvr);		
			country = getObject("settlement_id").getText();					
			getObject("settlement_id").click();
			verifyOverviewCityBC(country, afterLevelCardBC);						
			Assert.assertEquals(getObject("column_width_xpath").getText(), colCity);			
			
			//Verify header columns	
			verifyPresenceOfCols("transover_header_xpath", transHeadersArr);			
			logger.info("In Main view for level 2: the coherency of breadcrumb is as expected and only Level 2 are displayed");
			
			//Verify list of columns present in icon below the table
			logger.info("Step 8:");
			verifyHeaderColsWithIcon("get_table_xpath", 1);				
			logger.info("There are exactly same columns as Main view except the Level 2 (city) column");
			
			//verify bread crumb with level 3 search
			logger.info("Step 9:");
			city = getObject("settlement_id").getText();
			getObject("settlement_id").click();
			verifyOverviewShopBC(country, city, afterLevelCardBC);				
			Assert.assertEquals(getObject("column_width_xpath").getText(), colShop);			
			
			//Verify header columns	
			verifyPresenceOfCols("transover_header_xpath", transHeadersArr);			
			logger.info("In Main view for level 3: the coherency of breadcrumb is as expected and only Level 3 are displayed");
			
			//Verify list of columns present in icon below the table
			verifyHeaderColsWithIcon("get_table_xpath", 1);				
			logger.info("There are exactly same columns as Main view except the Level 3 (shop) column");
			getObject("settlement_id").click();
			verifyElementDisp("mid_xpath");			
			logger.info(" EPL-1726 execution successful");
			
		}
		catch (Throwable t)
		{
			handleException(t);	
		}	
	}
	
}
