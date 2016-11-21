package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1706.java $
$Id: EPL_1706.java 7955 2014-06-11 12:37:02Z cariram $
*/
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1706 extends SuiteCardPayment
{	
	
	private String xpath;
	private WebElement table;
	private List<WebElement> rows, cols;	
	private HashMap< String, String> obMap = new HashMap< String, String>();

	/**
	 * tableReaderUpdated read table data and put it into hashmap
	 * @param xpath
	 * @return
	 * @throws IOException
	 */
	private HashMap< String, String> tableReaderUpdated(String xpath) throws IOException{

		table = getObject(xpath);
		rows = table.findElements(By.tagName("tr"));
		for(int i=0;i<(rows.size());i++) {
			cols = rows.get(i).findElements(By.tagName("td"));		
			obMap.put((cols.get(0).getText()).trim(), (cols.get(1).getText().replaceAll("[A-Z,]","")).trim());
		}
		logger.info("Table data read sucessfully");
		return obMap;
	}
	
	/**
	 * getIndexForHeader fetches index of specified header
	 * @param header
	 * @return int
	 */
	private int getIndexForHeader(String header){
		String xpath = getPath("card_payment_overview_table_header_xpath");
		WebElement tableHeader = getObjectDirect(By.xpath(xpath));
		List<WebElement> headerCols = tableHeader.findElements(By.tagName("th"));
		for(int i=0;i<headerCols.size();i++){
			if(headerCols.get(i).getText().equals(header))
			return i;
		}
		return -1;
	}
	
	/**
	 * getTableData get header index
	 * @param headerName
	 * @return
	 */
	private String getTableData(String headerName){
		xpath=getPath("card_payment_overview_table_row1_xpath").replace("INDEX",(getIndexForHeader(headerName)+1)+"");
		String tabledata = getObjectDirect(By.xpath(xpath)).getText();
		return tabledata;
	}	
	
	

	/**
	 * EPL-1706  Check coherency of data between Snapshot and Transaction overview.
	 * @throws IOException
	 */

	@Test()
	public void epl_1706()
	{		
		try
		{
			logger.info(" EPL-1706 execution started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			logger.info("Step 1:");
			
            //You must be in Snapshot sub-module of  Card Payment module
			cardPaymentSubPageNavigator(snapshotBC);
			verifyBreadCrumb(snapshotBC);
			logger.info("verifed navigated to "+snapshotBC);
			
			//You must be in Transaction Journal sub-module of Card Payment module
			tableReaderUpdated("card_payment_transactions_table_xpath");
			
			//For each country level, create a search for the same displayed time as snapshot and yesterday period, for all applications
			logger.info("Step 2:");
			cardPaymentSubPageNavigator(subModTrnOvr);
			verifyBreadCrumb(cpTransOverBC);
			logger.info("verifed navigated to "+cpTransOverBC);				
			
			selectItem(getObject("period_id"), periodYest);			
			getObject("search_link").click();
			
			//verify is sanpshot and transaction overview data is coherent
			logger.info("Step 3:");
			verifyCoherency();		
			
			logger.info(" EPL-1706 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
	
	/**
	 * verifies coherency between Snapshot and Overview Page
	 */
	private void verifyCoherency()
	{
		Assert.assertEquals(obMap.get(csTotalTransRow).trim(),getTableData(colTransaction));
		if(obMap.containsKey(csTotalGlobSales)){
		Assert.assertEquals(obMap.get(csTotalGlobSales).trim(),getTableData(colGlobalSale));
		}
		Assert.assertEquals(obMap.get(csTotalRefundsRow).trim(),getTableData(colRefunds));
		Assert.assertEquals(obMap.get(csTotalCanSalesRow).trim(),getTableData(colCanSales));
		Assert.assertEquals(obMap.get(totCanRefunds).trim(),getTableData(colCanRef));
		logger.info("verifed is sanpshot and transaction overview data is coherent");
	}
}