package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1717.java $
$Id: EPL_1717.java 7955 2014-06-11 12:37:02Z cariram $
*/
import java.io.IOException;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1717 extends SuiteCardPayment {	
	
	private String selectedTabClassName="tabs-selected",expected;
	
	
	/**
	 * verifyContentFilteredDataWithBreadCrumb verifies if breadcrumb displays the same value as the selected option in the dropdown 
	 * @param selectLocator
	 */
	public void verifyContentFilteredDataWithBreadCrumb(String selectLocator){
		String text = getSelectedItem(getObject(selectLocator));
		verifyBreadCrumb(text);
	}
	/**
	 * EPL-1717 Check presence of cashier view / a user which customer has cashier display provisioned in Everest and have other applications as MIGS
	 * 
	 * @throws IOException
	 */
	@Test()
	public void epl_1717()
	{		
		try
		{
			logger.info(" EPL-1717 executing started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);	
			verifyBreadCrumb(cpTransOverBC);
			
			//Test Data preparation by selecting period as Last month
			logger.info("Step 2:");
			selectItem(getObject("period_id"), lastMonth);
			getObject("search_link").click();
			
			//Go to lowest level by choosing a country, a city and a shop
			goToMidLevel("settlement_id");			
			
			//Verify MID, POS, Cashier, Card Type tabs are visible
			logger.info("Step 3:");
			verifyElementDisp("mid_xpath");
			verifyElementDisp("pos_xpath");
			verifyElementDisp("cashier_xpath");			
			if(isElementPresent("card_type_xpath"))
			{
				verifyElementDisp("card_type_xpath");
			}
			else
			{
				logger.info("Card Type tab is not present to execute the test");
				Assert.fail("Card Type tab is not present to execute the test");
			}
			logger.info("Validated all tabs : MID, POS, Cashier ");
			
			//Navigate to POS tab
			logger.info("Step 4, 5, 6:");
			getObject("pos_xpath").click();
			
			//Verifying all POS name zones are clickable
			linksClickableInTable("table2_xpath");	
			getObject("settlement_id").click();
			xpath = getPath("cashier_xpath").replace("/a", "");
			expected = getObjectDirect(By.xpath(xpath)).getAttribute("className");
			Assert.assertEquals(selectedTabClassName, expected);
			logger.info("Cashier tab is selected");
			
			//Check coherency of DATA, breadcrumb and the content of filters
			verifyContentFilteredDataWithBreadCrumb("country_id");
			verifyContentFilteredDataWithBreadCrumb("city_id");
			verifyContentFilteredDataWithBreadCrumb("city_id");
			logger.info("Bread crumb data and content filters are in sync");
			
			logger.info(" EPL-1717 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	
}
