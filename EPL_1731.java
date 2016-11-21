package com.ingenico.eportal.testsuite.cardPayment;

import java.io.File;
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

public class EPL_1731 extends SuiteCardPayment{
	private boolean isStatus;
	private String str,headerRow, value,expectedText,path,pdf;	
	private ArrayList<String> headerList1,tableCols,tableDataFull,labelList;
	private StringBuffer sb = new StringBuffer();
	private List<WebElement> row,col,rows,tableRows;
	private int index=0,tableIndex,csvIndex, labelIndex, headerIndex;
	private List<String> csvData,refinedCSV = new ArrayList<String>();
	private WebElement tableRowColData;
	private CSVReader reader;
	private File file;
	private Process proc1;
		
		
	
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
	 * getTableRowCol returns string with row columns data with space in between
	 * @return String
	 */
	private String getTableThirdRowCol(List<WebElement> col){
		str = "";
		for(int j = 0;j <col.size();j++){
			if(j==1 || j==3 || j==5){
				str = str+"  "+col.get(j).getText();
			}
			else{
				str = str+" "+col.get(j).getText();
			}
		}
   		return str;
	}
	
	/**
	 * getTableRow returns string for entire row with specified index
	 * @param indexVal
	 * @return String
	 */
	private String getTableRow(int index)
	{		
		 	WebElement table = getObject("table1_xpath");
			row = table.findElements(By.tagName("tr"));
			col= row.get(index).findElements(By.tagName("td"));
			
			if(index!=2){
			return getTableRowCol(col);
			}
			else{
				return getTableThirdRowCol(col);
			}
		  }
	
	/**
	 * getLabelsList returns list of expected labels
	 * @return ArrayList<String> 
	 */
	private ArrayList<String> getLabelsList(){
		ArrayList<String> labels = new ArrayList<String>();		
		labels.add(preDefPeriod);
		labels.add(fromLabel);
		labels.add(toLable);
		labels.add(colCountry);
		labels.add(colCity);
		labels.add(colShop);		
		labels.add(tabMid);
		return labels;
	}
	/**
	 * EPL-1731 MID Tab / case of user with several applications / check content of export files
	 * 
	 * @throws IOException
	 */
	
	@Test()
	public void epl_1731() throws IOException
	{		
		try
		{
			logger.info(" EPL-1731 executing started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);	
			verifyBreadCrumb(cpTransOverBC);		
			
			//Navigate to Level1
			logger.info("Step 2:");
			goToMidLevel("settlement_id");
			tableRows = getObject("table1_xpath").findElements(By.tagName("tr"));
			if(tableRows.size()==1 && tableRows.get(0).getText().contains(noTransMsg)){
				logger.info("No transactions to your search. Failing the test");
				Assert.fail("No transactions to your search. Failing the test");
			}
			
			//Reading table headers and columns
			logger.info("Step 3:");
			tableCols = getColumnDataList("table1_xpath");
			headerList1 = getHeaderList("table1_xpath"); 
			
			//Concatenating header and column data
			headerList1.addAll(tableCols);
			tableDataFull = headerList1;
			
			// Download and read csv
			clickOnDownloadCSVPDF("export_csv_xpath", csvType);			
			path = getCSVPDFPath(cpOvermid, csvType);			
			csvData = readDownloadedCSV(path);
			
			//Eliminating extra data
			for(csvIndex =0;csvIndex<csvData.size();csvIndex++){
				if(csvData.get(csvIndex).contains(currencyLabel)||csvData.get(csvIndex).contains(currencyAus)){
					continue;
				}
				refinedCSV.add(csvData.get(csvIndex));
			}
			
			//Comparing CSV data with table data
			tableIndex =0;
			isStatus = true;
			for(String strVal :refinedCSV){
				if(!tableDataFull.get(tableIndex).contains(strVal)){
					isStatus = false;
					break;
				}
				tableIndex = tableIndex+1;
			}
			if(isStatus) {
				logger.info("CSV data is validated");
			}
			//Preparing test data
			restoreTables();			
			getObject("column_icon_xpath").click();
			List<WebElement> checkboxes = get_list_of_elements("cp_checkbox_tab_xpath");
			for(int i=9 ; i < checkboxes.size();i++){
				checkboxes.get(i).click();
			}
			saveWebTable();			
			tableRowColData = getObject("table1_xpath");
			rows = tableRowColData.findElements(By.tagName("tr"));
			 
			List<WebElement> cols =  rows.get(0).findElements(By.tagName("td"));
			selectItem(getObject("select_appli_xpath"), cols.get(1).getText());
			getObject("search_link").click();
			getObject("settlement_id").click();
			 
			 //VERIFYING PDF Data
			logger.info("Step 4:");
			clickOnDownloadCSVPDF("export_pdf_xpath", pdfType);				
			proc1 = Runtime.getRuntime().exec(CommonConstants.autoItPath+autiItExe);
			waitNSec(5);			
			path = getCSVPDFPath(cpOvermid, pdfType);
			pdf = PDF_Reader.readPdf(path);
			logger.info("PDF read completed");
			proc1.destroy();		
			
			labelList = getLabelsList();
			
			//Verifying the labels and values that appear in pdf
			for(labelIndex=0;labelIndex<labelList.size();labelIndex++)
			{
				if(labelList.get(labelIndex).equals(preDefPeriod) )
				{
					verifyLabelInPdf(preDefPeriod, "period_id", labelList, labelIndex, pdf);					
				}
				else if(labelList.get(labelIndex).equals(fromLabel))
				{
					verifyDataInPdf("strt_date_id", fromLabel, labelList, labelIndex, pdf);					
				}
				else if(labelList.get(labelIndex).equals(toLable) )
				{
					verifyDataInPdf("end_date_id", toLable, labelList, labelIndex, pdf);					
				}
				else if(labelList.get(labelIndex).equals(colCountry) )
				{
					verifyLabelInPdf(colCountry, "country_id", labelList, labelIndex, pdf);					
				}
				else if(labelList.get(labelIndex).equals(colCity) )
				{
					verifyLabelInPdf(colCity, "city_id", labelList, labelIndex, pdf);					
				}
				else if(labelList.get(labelIndex).equals(tabMid))
				{
					value = getSelectedItem(getObject("mid_id"));
					expectedText = "MID "+value;
					Assert.assertEquals(pdf.contains(expectedText), true);
					logger.info("Label 'MID' verified with PDF Data");
				}
				
				
				else if(labelList.get(labelIndex).equals(appTypeLabel))
					{
						verifyLabelInPdf(appTypeLabel, "select_appli_id", labelList, labelIndex, pdf);						
					}	
			}				
			logger.info("Labels and dropdowns verified with PDF Data");
			
			//Verifying tableheaders in PDF
			   for(headerIndex=0;headerIndex<getHeaderList("table1_xpath").size();headerIndex++){
				   sb = sb.append(getHeaderList("table1_xpath").get(headerIndex));
				   sb = sb.append(" ");
			   }
			  headerRow = sb.toString().trim();
			  Assert.assertEquals(pdf.contains(headerRow), true);
			  
			  logger.info("Table Header verfied in PDF");
			  
			//Verifying tableData in PDF
			  
			  tableRowColData = getObject("table1_xpath");
			  rows = tableRowColData.findElements(By.tagName("tr"));
			  
			  while(index < rows.size()){
				Assert.assertEquals(pdf.contains(getTableRow(index).trim()), true);
				  index++;
			  }
			  logger.info("Table data verfied in PDF");
			
			restoreTables();
			logger.info(" EPL-1731 execution successful");
		} 
		catch (Throwable t)
		{
			closeCSV(reader, file);
			handleException(t);			
		}
	}
	
	

}
