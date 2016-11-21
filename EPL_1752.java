package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1752.java $
$Id: EPL_1752.java 7888 2014-06-09 13:17:58Z cariram $
*/

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1752 extends SuiteCardPayment {
	private boolean isShopPresent=false;
	private String pages,countryList[];
	private List<WebElement> rows;
	private int pageNum =0,totalPages = 0,index=0;
	private List<String> listOfShops = new ArrayList<String>();
	private String [] shopsLevelArr = {"10018", "10019", "10024", "10025", "10006", "10007", "10026", "10027", "10012", "10013", "10020", "10014", "10015", "10008", "10009"}, 
			
	maxShopsArr = {pageLimit50, pageLimit50, pageLimit50, pageLimit50, pageLimit50, pageLimit50, pageLimit320, pageLimit200, pageLimit200, pageLimit20, pageLimit50, pageLimit45, pageLimit50, pageLimit100, pageLimit100},
	shopsArr ={barceloneShop1, barceloneShop2, birminghmaShop1, birminghmaShop2, brisbaneSop1, brisbaneSop2, londonShop1, londonShop2, shop2Lyon, shop1Lyon, madridShop1, shop1Paris, shop2Paris, shop1Sydney, shop2Sydney};
	
	
	
	/**
	 * EPL-1752 All applications / Check Filters / Levels filters (location section)
	 * 
	 * @throws IOException
	 */
	
	@Test()
	public void epl_1752()
	{		
		try
		{
			logger.info(" EPL-1752 executing started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			countryList = getListItems(getObject("country_id"));
			for(int cindex=1;cindex<countryList.length;cindex++){
				Assert.assertTrue(getCountries().contains(countryList[cindex]));
			}
			logger.info("Step 2, 3, 4:");
			Assert.assertEquals(getSelectedItem(getObject("country_id")), valAll);
			logger.info("Validated all countries and default selection");
			
			//Verifying Australia search results			
			verifySearch("period_id", periodLastWk, "level_1", "10000", pageLimit10, pageLimit50, getAustraliaShops());	
			logger.info("Validated level 1- Australia search results");
			
			//Verifying France search results
			cardPaymentSubPageNavigator(subModTrnJrn);
			verifySearch("period_id", periodLastWk, "level_1", "10001", pageLimit50, pageLimit55, getFranceShops());
			logger.info("Validated level 1- France search results");
			
			//Verifying Spain search results
			cardPaymentSubPageNavigator(subModTrnJrn);
			verifySearch("period_id", periodLastWk, "level_1", "10002", pageLimit10, pageLimit20, getSpainShops());	
			logger.info("Validated level 1- Spain search results");
			
			//Verifying UK search results
			cardPaymentSubPageNavigator(subModTrnJrn);
			verifySearch("period_id", periodLastWk, "level_1", "10003", pageLimit10, pageLimit20, getUKShops());	
			logger.info("Validated level 1- UK search results");
			
			//Verifying BRISBANE search results
			logger.info("Step 5, 6:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			verifyCityShopSearch("period_id", periodLastWk, "level_2", "10004", pageLimit10, pageLimit50, getAustraliaShops(), 0,2);
			logger.info("Validated level 2- BRISBANE search results");
			
			
			//Verifying SYDNEY search results
			cardPaymentSubPageNavigator(subModTrnJrn);
			verifyCityShopSearch("period_id", periodLastWk, "level_2", "10005", pageLimit10, pageLimit50, getAustraliaShops(), 2,4);			
			logger.info("Validated level 2- SYDNEY search results");
			
			
			//Verifying LYON search results
			cardPaymentSubPageNavigator(subModTrnJrn);
			verifyCityShopSearch("period_id", periodLastWk, "level_2", "10010", pageLimit10, pageLimit15, getFranceShops(), 0,2);
			logger.info("Validated level 2- LYON search results");
			
			//Verifying PARIS search results
			cardPaymentSubPageNavigator(subModTrnJrn);
			verifyCityShopSearch("period_id", periodLastWk, "level_2", "10011", pageLimit10, pageLimit15, getFranceShops(), 2,4);	
			logger.info("Validated level 2- PARIS search results");
			
			//Verifying BARCELONE search results
			cardPaymentSubPageNavigator(subModTrnJrn);
			verifyCityShopSearch("period_id", periodLastWk, "level_2", "10016", pageLimit10, pageLimit50, getSpainShops(), 0,2);	
			logger.info("Validated level 2- BARCELONE search results");
			
			//Verifying MADRID search results
			cardPaymentSubPageNavigator(subModTrnJrn);
			verifyCityShopSearch("period_id", periodLastWk, "level_2", "10017", pageLimit10, pageLimit50, getSpainShops(), 2,4);
			logger.info("Validated level 2- MADRID search results");
			
			//Verifying BIRMINGHAM search results
			cardPaymentSubPageNavigator(subModTrnJrn);
			verifyCityShopSearch("period_id", periodLastWk, "level_2", "10022", pageLimit10, pageLimit50, getUKShops(), 0,2);
			logger.info("Validated level 2- BIRMINGHAM search results");
			
			//Verifying LONDON search results
			cardPaymentSubPageNavigator(subModTrnJrn);
			verifyCityShopSearch("period_id", periodLastWk, "level_2", "10023", pageLimit10, pageLimit50, getUKShops(), 2,4);	
			logger.info("Validated level 2- LONDON search results");
			
			//Verifying BARCELONE_SHOP1 search results
			logger.info("Step 7, 8:");
			for(int i=0; i<shopsLevelArr.length; i++)
			{
				cardPaymentSubPageNavigator(subModTrnJrn);
				verifyShopSearch("period_id", periodLastWk, "level_3", shopsLevelArr[i], pageLimit10, maxShopsArr[i], shopsArr[i]);
				logger.info("Validated level 3- "+shopsArr[i]+" search results");
			}
			
			//Verifying MADRID_SHOP2 search results
			cardPaymentSubPageNavigator(subModTrnJrn);			
			selectItem(getObject("period_id"), periodLastWk);			
			multiSelect("level_3", "10021");			
			getObject("search_link").click();			
			index=getIndexForColHeader("card_payment_journal_table_header_xpath", shop);
			totalPages = fetchTotalPages();
			isShopDisplayed(madridShop2);
			logger.info("Validated level 3- MADRID_SHOP2 search results");			
					
			logger.info(" EPL-1752 execution successful");
			
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	
	
	/**
	 * verifies search for shops
	 * @param locator
	 * @param period
	 * @param levelId
	 * @param levelVal
	 * @param min
	 * @param max
	 * @param shopList
	 */
	private void verifySearch(String locator, String period, String levelId, String levelVal, String min, String max, List<String> shopList)
	{
		selectItem(getObject(locator), period);		
		multiSelect(levelId, levelVal);
		getObject("min_amount_id").sendKeys(min);
		getObject("max_amount_id").sendKeys(max);
		getObject("search_link").click();
		index=getIndexForColHeader("card_payment_journal_table_header_xpath", shop);
		totalPages = fetchTotalPages();
		areShopsDisplayed(shopList);	
	}
	
	/**
	 * verifies serach for city and shops
	 * @param locator
	 * @param period
	 * @param levelId
	 * @param levelVal
	 * @param min
	 * @param max
	 * @param shopList
	 * @param start
	 * @param end
	 */
	private void verifyCityShopSearch(String locator, String period, String levelId, String levelVal, String min, String max, List<String> shopList, int start, int end)
	{
		selectItem(getObject(locator), period);
		multiSelect(levelId, levelVal);
		getObject("min_amount_id").sendKeys(min);
		getObject("max_amount_id").sendKeys(max);
		getObject("search_link").click();
		index=getIndexForColHeader("card_payment_journal_table_header_xpath", shop);
		totalPages = fetchTotalPages();
		listOfShops = shopList.subList(start, end);
		areShopsDisplayed(listOfShops);
	}
	
	/**
	 * verifies search for shop only
	 * @param locator
	 * @param period
	 * @param levelId
	 * @param levelVal
	 * @param min
	 * @param max
	 * @param shop
	 */
	private void verifyShopSearch(String locator, String period, String levelId, String levelVal, String min, String max, String shopVal)
	{
		selectItem(getObject(locator), period);
		multiSelect(levelId, levelVal);
		getObject("min_amount_id").sendKeys(min);
		getObject("max_amount_id").sendKeys(max);
		getObject("search_link").click();
		index=getIndexForColHeader("card_payment_journal_table_header_xpath", shop);
		totalPages = fetchTotalPages();
		isShopDisplayed(shopVal);
	}
	
	/**
	 * getCountries returns a predefined list of expected countries configured in the eportal
	 * @return ArrayList<String>
	 */
	private ArrayList<String> getCountries(){
		ArrayList<String> countries = new ArrayList<String>();
		countries.add(countryAus);
		countries.add(countryFran);
		countries.add(countrySpa);
		countries.add(countryUK);
		return countries;
	}
	/**
	 * getAustraliaShops returns a predefined list of expected cities for Australia
	 * @return List<String>
	 */
	private List<String> getAustraliaShops(){
		List<String> ausShops = new ArrayList<String>();
		ausShops.add(brisbaneSop1);
		ausShops.add(brisbaneSop2);
		ausShops.add(shop1Sydney);
		ausShops.add(shop2Sydney);
		return ausShops;
	}
	/**
	 * getFranceShops returns a predefined list of expected cities for France
	 * @return List<String>
	 */
	private List<String> getFranceShops(){
		List<String> franceShops = new ArrayList<String>();
		franceShops.add(shop2Lyon);
		franceShops.add(shop1Lyon);
		franceShops.add(shop1Paris);
		franceShops.add(shop2Paris);
		return franceShops;
	}
	/**
	 * getSpainShops returns a predefined list of expected cities for Spain
	 * @return List<String>
	 */
	private List<String> getSpainShops(){
		List<String> spainShops = new ArrayList<String>();
		spainShops.add(barceloneShop1);
		spainShops.add(barceloneShop2);
		spainShops.add(madridShop1);
		spainShops.add(madridShop2);
		return spainShops;
	}
	/**
	 * getUKShops returns a predefined list of expected cities for UK
	 * @return List<String>
	 */
	private List<String> getUKShops(){
		List<String> ukShops = new ArrayList<String>();
		ukShops.add(birminghmaShop1);
		ukShops.add(birminghmaShop2);
		ukShops.add(londonShop1);
		ukShops.add(londonShop2);
		return ukShops;
	}
	/**
	 * fetchTotalPages returns number of pages in the search result and fails if no search results are returned
	 * @return int
	 */
	private int fetchTotalPages(){
		if(isElementPresent("cp_span_label_xpath")== false){
			logger.info("No search results.Failing the test");
			Assert.fail("No search results.Failing the test");
		}
		pages = getObject("cp_span_label_xpath").getText();
		totalPages = Integer.parseInt(pages.replace("of ", ""));
		logger.info("Fetched total number of pages=" + totalPages);
		return totalPages;
		
	}
	/**
	 * areShopsDisplayed asserts if expected Shops are displayed in search results
	 */
	private void areShopsDisplayed(List<String> listOfShops){
		pageNum=0;
		while(pageNum < totalPages){
			int count=0;
			rows = getObject("table_card_payment_journal_xpath").findElements(By.tagName("tr"));
		
			for(WebElement row:rows){
				List<WebElement> col= row.findElements(By.tagName("td"));
				if(listOfShops.contains(col.get(index).getText())){
					isShopPresent=true;
					Assert.assertTrue(isShopPresent);
				}
				else{
					Assert.fail();
				}
				count++;
			}
			if(count==rows.size()){
				getObject("tj_nextpage_image_xpath").click();
				pageNum++;
			}
			logger.info("Validated shops for the particular country at page " + pageNum);
		}
	}
	/**
	 * isShopDisplayed asserts if expected Shop is displayed in search results
	 */
	private void isShopDisplayed(String shopName){
		pageNum=0;
		while(pageNum < totalPages){
			int count=0;
			rows = getObject("table_card_payment_journal_xpath").findElements(By.tagName("tr"));
				for(WebElement row:rows){
					List<WebElement> col= row.findElements(By.tagName("td"));
					if(shopName.contains(col.get(index).getText())){
						isShopPresent=true;
						Assert.assertTrue(isShopPresent);
					}
					else{
						Assert.fail();
					}
					count++;
				}
				if(count==rows.size()){
					getObject("tj_nextpage_image_xpath").click();
					pageNum++;
				}
				logger.info("Validated shop " + shopName);
		}
	}
}
