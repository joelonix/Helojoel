package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1743.java $
$Id: EPL_1743.java 7888 2014-06-09 13:17:58Z cariram $
*/

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.com.bytecode.opencsv.CSVReader;

import com.ingenico.common.CommonConstants;
import com.ingenico.util.PDF_Reader;

public class EPL_1743 extends SuiteCardPayment {
	private boolean isStatus;
	private String str,savedSearchedName="1743 CP >> Transaction Journal",pdf,linksArr[] = {snapshotBC, cpTransOverBC, cpTransSettleBC, cpReconBC};
	private List<WebElement> seeAlsoElements,journalHeader,rows,cols,row,col,headerData,columnCheckboxList;
	private List<String>  jheaderList,colsList = new ArrayList<String>(),headerDataAsString;
	private int dataIndex,index,csvIndex,jheadIndex,jIndex,rIndex,cIndex,tableIndex,checkBoxIndex,
	seeAlsoElIndex,hIndex,hDataIndex;
	private List<Integer> csvHeaderIndexList,tIndex = new ArrayList<Integer>();
	private List<String[]> csvData= new ArrayList<String[]>();
	private ArrayList<String>refinedCSVList = new ArrayList<String>(),tableList;
	private StringBuffer sb = new StringBuffer();
	private WebElement savedSearchItem,searchItemElement;
	

	

	/**
	 * EPL-1743 Right hand side column of Transaction journal sub-module / Check organization
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1743()
	{		
		try
		{
			logger.info(" EPL-1743 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			verifyBreadCrumb(cpTransJourBC);

			//Select EMV AUS in application type	
			logger.info("Step 2:");
			multiSelect("select_appli", emvAusAppVal);
			getObject("search_link").click();
			
			getObject("restore_config_xpath").click();
			getObject("transaction_journal_ok_xpath").click();

			//Verify Advanced Search and Basic search
			logger.info("Step 3:");
			verifyElementDisp("advanced_search_link");
			verifyElementDisp("basic_search_link");

			//Verify options menu
			verifyOptionMenuPresent("export_csv_xpath");
			verifyOptionMenuPresent("export_pdf_xpath");
			verifyOptionMenuPresent("add_to_saved_searches_id");

			logger.info("Verified options menu");

			//CSV Verification
			verifyCSVData();


			//PDF Verification
			//Preparing test data
			getObject("column_icon_xpath").click();
			columnCheckboxList = get_list_of_elements("cp_checkbox_tab_xpath");
			for(checkBoxIndex=6;checkBoxIndex<columnCheckboxList.size();checkBoxIndex++){
				columnCheckboxList.get(checkBoxIndex).click();
			}			
			saveWebTable();

			//Clicked pdf download	
			verifyPDFData();			
			restoreTables();
			logger.info("Validated pdf with table data");

			//Add to saved searches
			verifyAddToSavedSearchesLink();
			logger.info("Validated the Add To Saved Searches link");

			//In see also menu, there are three shortcuts and verify they work correctly
			seeAlsoElements=get_list_of_elements("seealso_links_xpath");		
			verifyPresenceOfCols("seealso_links_xpath", linksArr);

			//seeAlsoElements.size()-1 is done to eliminate ePayment journal which not the part of test case verification		
			verifySeeAlsoNavigation();
			getObject("search_link").click();

			Assert.assertTrue(isElementPresent("view_receipt_link"));
			logger.info("Verified view receipt link");

			//Verifying few filters and Search link on main view
			cardPaymentSubPageNavigator(subModTrnJrn);
			Assert.assertTrue(isElementPresent("country_id"));
			Assert.assertTrue(isElementPresent("city_id"));
			Assert.assertTrue(isElementPresent("shop_id"));
			Assert.assertTrue(isElementPresent("search_link"));
			logger.info("Validated the presence of filters and Search link");

			logger.info(" EPL-1743 execution successful");

		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
	
	/**
	 * verifies CSV data
	 */
	private void verifyCSVData() throws IOException
	{
		clickOnDownloadCSVPDF("export_csv_xpath", csvType);		
		fileName = cpCSVJrn+date_Formatter("_");
		String path = getCSVPDFPath(fileName, csvType);		
		//***********************end*************************		
		//Getting the header Index Data from CSV
		csvHeaderIndexList = readCSVHeaderIndex(path);
		csvData = readDownloadedCSVData(path);
		//Preparing CSV data as its relevance to the table data by eliminating extra data from CSV
		for(String[] array: csvData)
		{
			for(csvIndex=0;csvIndex<array.length;csvIndex++){
				if(csvHeaderIndexList.contains(csvIndex)){
					continue;
				}
				refinedCSVList.add(array[csvIndex]);
			}
		}
		logger.info("CSV Data is ready");

		//Fetching table data
		journalHeader = get_list_of_elements("card_payment_journal_table_header_xpath");
		jheaderList =  new ArrayList<String>();
		for(jheadIndex=0;jheadIndex<journalHeader.size();jheadIndex++)  {
			jheaderList.add(journalHeader.get(jheadIndex).getText());
		}
		for(jIndex=0;jIndex<journalHeader.size();jIndex++){
			if(journalHeader.get(jIndex).getText().contains(colSettlement) || journalHeader.get(jIndex).getText().contains(colAuth)){
				tIndex.add(jIndex);
			}
		}
		//Emininating un needed info from table
		rows = getObject("table_card_payment_journal_xpath").findElements(By.tagName("tr"));
		for(rIndex = 0; rIndex < rows.size();rIndex++){
			cols = rows.get(rIndex).findElements(By.tagName("td"));
			for(cIndex=1;cIndex<cols.size();cIndex++){
				if(tIndex.contains(cIndex)){
					continue;
				}
				else{
					if(cIndex== jheaderList.indexOf("Mode")){
						colsList.add(cols.get(cIndex).findElement(By.tagName("img")).getAttribute("title"));
					}
					colsList.add(cols.get(cIndex).getText());
				}
			}
		}
		jheaderList.addAll(colsList);
		jheaderList.remove(0);
		tableList = new ArrayList<String>();
		for(String s:jheaderList){
			if(s.equals("Settlement") || s.equals(colSettDate) || s.equals(colAuth))  continue;
			else
				tableList.add(s);
		}
		for(tableIndex = 0; tableIndex <tableList.size() ;tableIndex++){
			if(tableList.get(tableIndex).equals(onlineLabel) || tableList.get(tableIndex).equals(offlineLabel)){
				tableList.remove(tableIndex+1);
			}
		}
		logger.info("Table Data is ready");

		//Comparing CSV data with table data
		dataIndex =0;
		isStatus = true;
		for(String strVal :refinedCSVList){
			if(!tableList.get(dataIndex).contains(strVal)){
				isStatus = false;
				break;
			}
			dataIndex = dataIndex+1;
		}
		if(isStatus){
			logger.info("CSV data is validated"); 
		}

	}
	
	/** 
	 * Verifies the option menu items like CSV, PDF download links 
	 * @param Locator
	 */
	private void verifyOptionMenuPresent(String locator){
		boolean isPresent = getObject(locator).isDisplayed();
		Assert.assertEquals(isPresent, true);
	}
	/**
	 * readCSVHeaderIndex returns all csv header indices
	 * @return ArrayList<Integer> 
	 */
	private ArrayList<Integer> readCSVHeaderIndex(String path) throws IOException{
		CSVReader reader = new CSVReader(new FileReader(path));
		String[] array = reader.readNext();
		ArrayList<Integer> headerIndexList = new ArrayList<Integer>();
		for(int i=0;i<array.length;i++){
			if(array[i].contains(curr) || array[i].contains(colCountry) || array[i].contains(colCity) || array[i].contains(tabCashier) 
					|| array[i].contains(pinPadLabel) || array[i].contains(authNumLabel)) {
				headerIndexList.add(i);
			}
		}
		reader.close();
		logger.info("Header indices read successfully");
		return headerIndexList;
	}

	/**
	 * readDownloadedCSVData returns all csv data
	 * @return List<String[]> 
	 */
	private List<String[]> readDownloadedCSVData(String path) throws IOException{
		CSVReader reader = new CSVReader(new FileReader(path));
		List<String[]> listfromCSV = reader.readAll();
		reader.close();
		new File(path).delete();
		logger.info("CSV Data read is successful");
		return listfromCSV;
	}

	/**
	 * getTableRowCol returns string with row columns data with space in between
	 * @return String
	 */
	private String getTableRowCol(List<WebElement> col){
		str = "";
		for(int j = 0;j <col.size();j++){
			str = str+" "+col.get(j).getText();
		}
		return str;
	}

	/**
	 * getTableRow returns string for entire row with specified index
	 * @param indexVal
	 * @return String
	 */
	private String getTableRow(int indexVal)
	{		 
		row = getObject("card_payment_journal_id").findElements(By.tagName("tr"));
		col= row.get(indexVal).findElements(By.tagName("td"));
		return getTableRowCol(col);
	}

	/**
	 * Verify 'Add to saved searches' button.............................
	 * @throws InterruptedException
	 */
	private void verifyAddToSavedSearchesLink() throws InterruptedException {
		Assert.assertTrue(getObject("save_search_link").isDisplayed());
		getObject("save_search_link").click();
		waitForElementPresent("user_favorite_name_xpath");
		populateInputBox("user_favorite_name_xpath", savedSearchedName );
		getObject("ok_saved_searches_xpath").click();
		getObject("ok_link").click();

		//click on saved search link at top right corner
		getObject("saved_Searches_link").click();
		waitNSec(3);		
		verifySavedSearchItem(savedSearchedName);
		deleteSavedSearchItem(savedSearchedName);
	}

	/**
	 * Verify 'Saved Searched list Item'
	 * @param savedSearchName
	 */
	private void verifySavedSearchItem(String savedSearchName)
	{
		try
		{				
			savedSearchItem =getObject("SavedSearches_css");
			options = savedSearchItem.findElements(By.tagName("li"));
			for(int itemCount = 0; itemCount <= options.size() ; itemCount++)
			{
				savedSearchItem =getObject("SavedSearches_css");
				options = savedSearchItem.findElements(By.tagName("li"));
				searchItemElement = options.get(itemCount).findElement(By.cssSelector("table>tbody>tr>td>div>a>div"));
				value = searchItemElement.getText();
				if(value.equalsIgnoreCase(savedSearchName)){
					Assert.assertTrue(true);
					searchItemElement.click();
					verifyBreadCrumb(cpTransJourBC);
					break;
				}
			}
		}
		catch(Exception e)
		{			
			logger.error("Not able to find Saved Search option");
			Assert.fail("Not able to find Saved Search option");			
		}
	}
	
	/**
	 * verifies PDF data
	 */
	private void verifyPDFData()
	{
		try{
			clickOnDownloadCSVPDF("export_pdf_xpath", pdfType);			
			proc1 = Runtime.getRuntime().exec(CommonConstants.autoItPath+autiItExe);			
			String path = getCSVPDFPath(cpPDFJrn, pdfType);
			waitNSec(5);
			proc1.destroy();
			pdf = PDF_Reader.readPdf(path);
			waitNSec(3);
			logger.info("PDF read completed");		

			rows = getObject("card_payment_journal_id").findElements(By.tagName("tr"));
			//Verifying Header data
			headerData = rows.get(0).findElements(By.tagName("th"));
			headerDataAsString = new ArrayList<String>();
			for(hIndex=0;hIndex<headerData.size();hIndex++){
				headerDataAsString.add(headerData.get(hIndex).getText());
			}
			for(hDataIndex=0;hDataIndex<headerDataAsString.size();hDataIndex++){
				sb = sb.append(headerDataAsString.get(hDataIndex));
				sb = sb.append(" ");
			}			
			Assert.assertTrue(pdf.contains(sb.toString().trim())) ;

			//Verifying table data
			index =1;
			while(index < rows.size()){
				Assert.assertEquals(pdf.contains(getTableRow(index).trim()), true);
				index++;
			}
		}
		catch(Throwable t)
		{
			Assert.fail("PDF verification failed");
		}
	}
	
	/**
	 * verifies See also navigation
	 * @throws InterruptedException
	 */
	private void verifySeeAlsoNavigation() throws InterruptedException
	{
		seeAlsoElements=get_list_of_elements("seealso_links_xpath");
		for(seeAlsoElIndex=0;seeAlsoElIndex<linksArr.length;seeAlsoElIndex++)
		{			
			seeAlsoElements.get(seeAlsoElIndex).click();			
			waitForTxtPresent("bread_crumb_id", linksArr[seeAlsoElIndex]);
			verifyBreadCrumb(linksArr[seeAlsoElIndex]);
			cardPaymentSubPageNavigator(subModTrnJrn);
			seeAlsoElements=get_list_of_elements("seealso_links_xpath");
		}
		logger.info("Verified see also menu items");
	}

}
