package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1750.java $
$Id: EPL_1750.java 7888 2014-06-09 13:17:58Z cariram $
*/


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1750 extends SuiteCardPayment
{	
	 
	private String id,checkArr[] = {"1", "2", "3", "4", "others"}; 
	private List<WebElement> options;
	private Pattern p;
	private Matcher m;
	private List<String> optionsText;
	
	/**
	 * EPL-1750
	 * All applications / Check list of Filters
	 * @throws IOException
	 */
	@Test
	public void epl_1750() throws InterruptedException
	{			
		try {
			logger.info("EPO_1750 execution started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);	
			
			//Check presence of these filters:
			
			logger.info("Step 2, 3:");
			verifyElementDisp("receipt_id");
			verifyElementDisp("server_id");
			verifyElementSelected("receipt_id");
			logger.info("Validated reciept and server radio buttons");
			
			//The Pre-defined period must contain these periods :
			Select select = new Select(getObject("period_id"));
			options = select.getOptions();
			for(WebElement option : options){
				Assert.assertTrue(getExpectedPeriodList().contains(option.getText()));
			}
			logger.info("Validated Pre-defined period options");
			
			//Calendars with “from” period and “To” period
			//Verifying start date
			verifyDatePattern("strt_date_id", "\\d{4}-\\d{2}-\\d{2}");		
			
			//verifying start time
			verifyTimePattern("start_hour_id", "\\d{2}:\\d{2}(\\s(A|P)M)?");
			
			
			//Verifying end date
			verifyDatePattern("end_date_id", "\\d{4}-\\d{2}-\\d{2}");
			
			//verifying end time
			verifyTimePattern("end_hour_id", "\\d{2}:\\d{2}(\\s(A|P)M)?");
			
			//Verify Levels filter (Countries / Cities / Shops)
			getContains(colCountry);
			getContains(colCity);
			getContains(colShop);
			getContains(tabMid);
			getContains(tabPos);
			logger.info("Validated Levels filter (Countries / Cities / Shops)");
			
			//Verify amount filters
			getContains(minLabel);
			getContains(maxLabel);
			Assert.assertTrue(isElementPresent("min_amount_id"));
			Assert.assertTrue(isElementPresent("max_amount_id"));
			logger.info("Validated amount filters");
			
			//Verify Application type filter:
			select = new Select(getObject("select_appli_id"));
			options = select.getOptions();
			
			optionsText = new ArrayList<String>();
			for(WebElement option : options){
				optionsText.add(option.getText());
			}
			
			for(String appType:getExpectedAppTypeList()){
				Assert.assertTrue(optionsText.contains(appType));
			}
			
			Assert.assertEquals(getSelectedItem(getObject("select_appli_id")), valAll);
			logger.info("Validated Application type filter");
			
			//Verify Transaction type filter:
			id = getPath("trans_type_id");
			
			//Verify Transaction type filters are displayed
			for(int i=0;i<checkArr.length;i++)
			{
				verifyTransCheckBoxes(checkArr[i]);
			}			
			logger.info("Validated transaction type filters are displayed");
			
			//Verify When you select the "Check / uncheck all", then all checkboxes are selected
			verifyElementSelected("cp_check_all_id");
			logger.info("Validated When you select the 'Check / uncheck all', then all checkboxes are selected");
			
			//Verify When you deselect the "Check / uncheck all", then all checkboxes are deselected
			id = getPath("trans_type_id");
			getObject("cp_check_all_id").click();
			Assert.assertFalse(getObject("cp_check_all_id").isSelected());
			for(int i=0;i<checkArr.length;i++)
			{
				verifyNoTransCheckBoxes(checkArr[i]);
			}			
			logger.info("Validated When you deselect the 'Check / uncheck all', then all checkboxes are deselected");
			
			logger.info("EPO_1750 execution successful");

		} catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	/**
	 * verifies date pattern
	 * @param locator
	 * @param pattern
	 */
	private void verifyDatePattern(String locator, String pattern)
	{
		String date = getObject(locator).getAttribute("value");
		p = Pattern.compile(pattern);
		m = p.matcher(date);
		Assert.assertTrue(m.find());
		logger.info("Validated start date format : YYYY-MM-DD");
	}
	
	/**
	 * verifies time pattern
	 * @param locator
	 * @param pattern
	 */
	private void verifyTimePattern(String locator, String pattern)
	{
		String time = getSelectedItem(getObject(locator));
		p = Pattern.compile(pattern);
		m = p.matcher(time);
		Assert.assertTrue(m.find());
		logger.info("Validated start time format : HH:MM AM/PM");
	}
	
	/**
	 * verifies transaction type check boxes selection
	 * @param index
	 */
	private void verifyTransCheckBoxes(String index)
	{
		id = getPath("trans_type_id");
		String checkbox = id.replace("INDEX", index);
		Assert.assertTrue(getObjectDirect(By.id(checkbox)).isDisplayed());
		Assert.assertTrue(getObjectDirect(By.id(checkbox)).isSelected());
	}
	
	/**
	 * verifies transaction type check boxes no selection
	 * @param index
	 */
	private void verifyNoTransCheckBoxes(String index)
	{
		id = getPath("trans_type_id");
		String checkbox = id.replace("INDEX", index);
		Assert.assertFalse(getObjectDirect(By.id(checkbox)).isSelected());
	}
	
	/**
	 * Expected period list
	 * @return
	 */
	private List<String> getExpectedPeriodList(){
		List<String> periodList = new ArrayList<String>();
		periodList.add(periodDefault);
		periodList.add(periodToday);
		periodList.add(periodYest);
		periodList.add(periodThisWeek);
		periodList.add(periodLastWk);
		periodList.add(period20Days);
		periodList.add(period30Days);
		periodList.add(thisMonth);
		periodList.add(lastMonth);
		return periodList;
	}
	
	/**
	 * Expected app type list
	 * @return
	 */
	private List<String> getExpectedAppTypeList(){
		List<String> appList = new ArrayList<String>();
		appList.add(valAll);
		appList.add(alphraApp);
		appList.add(chqFRApp);		
		appList.add(emvAUSApp);
		appList.add(emvESApp);
		appList.add(emvFRApp);
		appList.add(emvUKApp);	
		return appList;
	}
}
