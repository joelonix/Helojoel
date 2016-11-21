package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1751.java $
$Id: EPL_1751.java 7888 2014-06-09 13:17:58Z cariram $
*/

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1751 extends SuiteCardPayment
{
	
	private String fromVal, endVal, xpath,
			listitemsAct[], listitemsExp[]={periodDefault, periodToday, periodYest, periodThisWeek, periodLastWk, period20Days, period30Days, thisMonth, lastMonth};
	private ArrayList<String> al=new ArrayList<String>();
	
	
	
	/**
	 * EPL-1751 All applications / Check Filters / Period filter
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1751()
	{		
		try{
			logger.info("  EPL-1751 started execution");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);	
			
			//Verify search after selecting receipt id	
			logger.info("Step 2:");
			verifyElementSelected("receipt_id");
			getObject("search_link").click();
			waitForTxtPresent("journal_row_count_xpath", displayText);
			logger.info("The search is successfull and the result is correct after selecting Receipt Id");
			
			//Verify search after selecting server id
			logger.info("Step 3:");
			getObject("advanced_search_link").click();
			getObject("server_id").click();
			getObject("search_link").click();
			waitForTxtPresent("journal_row_count_xpath", displayText);
			logger.info("The search is successfull and the result is correct after selecting Server Id");
			
			//Verify the pre defined filter
			logger.info("Step 4:");
			getObject("advanced_search_link").click();
			listitemsAct = getListItems(getObject("period_id"));
			logger.info("The following predefined filter values are there");
			for (int index=0;index<listitemsAct.length;index++)
			{
				Assert.assertEquals(listitemsAct[index], listitemsExp[index]);
				logger.info(listitemsAct[index]);
			}
			logger.info("The predefined filter has the same values as expected");
			
			//Verify search for each period value
			logger.info("Step 5, 6:");
			List<WebElement> firstList = get_list_of_elements("period_serach_xpath");
			cardPaymentSubPageNavigator(subModTrnJrn);
			al.add(periodLastWk);
			al.add(period20Days);
			al.add(period30Days);
			al.add(thisMonth);
			al.add(lastMonth);
			verifyPeriodSearch(firstList);	
			
			logger.info(" EPL-1751 execution successful");
			} 
		catch (Throwable t)
		{
			handleException(t);
		}	
	}
	
	/**
	 * verifies other period search
	 */
	private void verifyPeriodSearch(List<WebElement> fList) throws InterruptedException
	{
		List<WebElement> firstList = get_list_of_elements("period_serach_xpath");
		for(int index =1; index<listitemsExp.length;index++)
		{				
			selectItem(getObject("period_id"), listitemsExp[index]);				
			fromVal = getObject("strt_date_id").getText();
			endVal = getObject("end_date_id").getText();
			getObject("search_link").click();
			waitForTxtPresent("journal_row_count_xpath", displayText);
			
			//Verify for first 3 period filters , 
			if(listitemsExp[index].equals(periodToday) || listitemsExp[index].equals(periodYest) ||  listitemsExp[index].equals(periodThisWeek))
			{
				firstList = get_list_of_elements("period_serach_xpath");
				for(int i=0; i<firstList.size(); i++)
				{						
					Assert.assertTrue(firstList.get(i).getText().contains(fromVal));
				}					
				logger.info("The result of search is in coherency with From and To filters after selecting '"+listitemsExp[index]+"' period filter");
			}
			
			//verify for rest of the filter first and last page
			else if(al.contains(listitemsExp[index]))
			{
				firstList = get_list_of_elements("period_serach_xpath");
				for(int j=0; j<firstList.size(); j++)
				{						
					Assert.assertTrue(firstList.get(j).getText().contains(fromVal));
				}				
				xpath = getPath("tj_nextpage_image_xpath").replace("span[1]", "span[2]");
				List<WebElement> lastList = driver.findElements(By.xpath(xpath));
				for(int j=0; j<lastList.size(); j++)
				{						
					Assert.assertTrue(lastList.get(j).getText().contains(endVal));
				}				
				logger.info("The result of search is in coherency with From and To filters after selecting '"+listitemsExp[index]+"' period filter");
			} 			
			cardPaymentSubPageNavigator(subModTrnJrn);				
		}
	}
}
