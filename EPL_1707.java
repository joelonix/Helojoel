package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1707.java $
$Id: EPL_1707.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1707 extends SuiteCardPayment
{	
	
	private String colorSearch, colorReset, colorRed, colorGray;	
	private String[] listitems={snapshotBC, cpTransJourBC, cpTransSettleBC, cpReconBC, cpAvoirBC};
	private int count=0;
	private List<WebElement> seeOptions;
		
	

	/**
	 * EPL-1707 Transaction Overview sub-module / Check general organization
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1707()
	{		
		try{
			logger.info("  EPL-1707 started execution");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);				
			
			//verify bread crumb with default view
			verifyOverviewBC(defaultOverviewBC);			
			
			//Verify the presence of transaction overview table
			logger.info("Step 2:");
			verifyElementDisp("table_id");
			logger.info(" Transaction overview table is displayed");
			
			//Verify the bottom message			
			Assert.assertEquals(getObject("bottom_msg_xpath").getText().contains(bottomMsg), true);
			logger.info("At the bottom of Transaction overview table, message is displayed as expected "+getObject("bottom_message_xpath").getText());
			
			//Verify receipt id and period
			Assert.assertEquals(getObject("receipt_id").getAttribute("checked"), "true");
			Assert.assertEquals(new Select(getObject("period_id")).getFirstSelectedOption().getText(), periodToday);
			logger.info(" By default, Receipt Time button is selected and by default, Today as pre-defined period is selected");
			
			//Verify Levels' default value, Mid and Pos filters
			verifyDefaultSettings();			
			
			//Verify See also links	
			verifySeeAlsoLinks();		
			
			//verify option menu is present
			verifyOptionMenu();	
			
			//Verify colours of Search and Reset buttons
			verifySearchResetColor();			
			Assert.assertEquals(getSelectedItem(getObject("select_appli_id")), valAll);
			verifyElementNotPresent(cetCardType);
			selectItem(getObject("select_appli_id"), chqFRApp);
			verifyElementNotPresent(cetCardType);
			logger.info(" EPL-1707 execution successful");
			} 
		catch (Throwable t)
		{
			handleException(t);	
		}	
	}
	
	
	/**
	 * verifies Default Settings
	 */
	private void verifyDefaultSettings()
	{
		Assert.assertEquals(getSelectedItem(getObject("country_id")), valAll);
		Assert.assertEquals(getSelectedItem(getObject("city_id")), valAll);
		Assert.assertEquals(getSelectedItem(getObject("shop_id")), valAll);
		Assert.assertTrue(getObject("mid_id").isDisplayed());
		Assert.assertEquals(getObject("mid_id").getText(), "");
		Assert.assertTrue(getObject("pos_id").isDisplayed());
		Assert.assertEquals(getObject("pos_id").getText(), "");
		logger.info(" Default values for all level filters are 'All'");
		logger.info("In the left hand side column, MID and POS filters are present and by default, empty");
	}
	
	
	/**
	 * verifies See Also links navigate to correct pages 
	 */
	private void verifySeeAlsoLinks()
	{		
		verifyElementDisp("single_seealso_id");
		seeOptions=get_list_of_elements("seealso_links_xpath");
		for(WebElement option:seeOptions){
			Assert.assertEquals(listitems[count],(option.getText()));
			count++;
		}
	}
	
	/**
	 * verifies Options Menu in Overview Page 
	 */
	private void verifyOptionMenu()
	{
		verifyElementDisp("export_csv_xpath");
		verifyElementDisp("export_pdf_xpath");
		verifyElementDisp("add_to_saved_searches_id");		
		logger.info("The see also menu with Snapshot / Transaction Journal / Settlement Overview / Transaction renconciliation is displayed");
		logger.info("At the top of Main view, 3 buttons : export CSV (with an icon) / Export PDF (with an icon) / Add to saved searches (with an icon) are displayed ");
	}
	
	/**
	 * Verifies colour of Search and Reset buttons
	 */
	private void verifySearchResetColor()
	{
		colorSearch = getObject("search_link").getCssValue("background-color");		
		colorReset = getObject("reset_link").getCssValue("background-color");	
		colorRed=colorSearch.substring(0, ((colorSearch.length()-4)));
		colorGray=colorReset.substring(0, ((colorReset.length()-4)));			
		Assert.assertEquals(colorGray, "rgba(204, 204, 204");		
		Assert.assertEquals(colorRed, "rgba(227, 28, 24");
		logger.info("Two buttons : Search button in red color + Reset filters in gray color are displayed");		
	}
}
