package com.ingenico.eportal.testsuite.cardPayment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;
import com.ingenico.util.PDF_Reader;

public class EPL_1734 extends SuiteCardPayment{
	
	private String str,headerRow,path,expectedText;	
	private ArrayList<String> headerList1, tableCols, tableDataFull, labelList;
	private StringBuffer sb = new StringBuffer();
	private List<WebElement> row,col;
	private List<String> csvData,refinedCSV = new ArrayList<String>();		
	private Process proc1;
	
	

	
	/**
	 * getTableRowCol returns string with row columns data with space in between
	 * @return String
	 */
	public String getTableRowCol(List<WebElement> col){
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
	public String getTableRow(int index)
	{
		try
		{
			WebElement table = getObject("table3_xpath");
			row = table.findElements(By.tagName("tr"));
			col= row.get(index).findElements(By.tagName("td"));
		}
		catch(Exception e)
		{
			logger.error("Not able to find rows and columns");
			Assert.fail("Not able to find rows and columns");
		}
		return getTableRowCol(col);

	}
	
	/**
	 * getLabelsList returns list of expected labels
	 * @return List<String> 
	 */
	private ArrayList<String> getLabelsList(){
		ArrayList<String> labels = new ArrayList<String>();
		labels.add(preDefPeriod);
		labels.add(fromLabel);
		labels.add(toLable);
		labels.add(colCountry);
		labels.add(colCity);
		labels.add(colShop);
		labels.add(tabPos);
		return labels;
	}

	/**
	 * EPL-1734 Check cashier tab / content of export files
	 * 
	 * @throws IOException
	 */
	@Test()
	public void epl_1734()
	{		
		try
		{
			logger.info(" EPL-1734 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);				
			getObject("search_link").click();

			// navigating to Cashier view
			logger.info("Step 2:");
			goToMidLevel("settlement_id");	
			logger.info("Step 3:");
			getObject("cashier_xpath").click();
			restoreTables();
			
			//Reading table headers and columns
			logger.info("Step 4:");
			tableCols = getColumnDataList("table3_xpath");			
			headerList1 = getHeaderList("table3_xpath");				

			//Concatenating header and column data
			headerList1.addAll(tableCols);
			tableDataFull = headerList1;

			// Download and read csv
			clickOnDownloadCSVPDF("export_csv_xpath", csvType);	

			logger.info("Read data from CSV file");	
			path = getCSVPDFPath(cpOvercashier, csvType);			
			csvData = readDownloadedCSV(path);			
			for(int i =0;i<csvData.size();i++){
				if(csvData.get(i).contains(currencyLabel)||csvData.get(i).contains(currencyAus))
				{
					continue;
				}
				refinedCSV.add(csvData.get(i));
			}

			//Comparing CSV data with table data
			int i =0;
			Boolean isStatus = true;
			for(String strVal :refinedCSV){
				if(!tableDataFull.get(i).contains(strVal))
				{
					isStatus = false;
					break;
				}
				i = i+1;
			}
			if(isStatus)
			{
				logger.info("CSV data is validated and same as displayed in application");		
			}

			//VERIFYING PDF Data
			logger.info("Step 4:");
			clickOnDownloadCSVPDF("export_pdf_xpath", pdfType);			
			proc1 = Runtime.getRuntime().exec(CommonConstants.autoItPath+autiItExe);
			path = getCSVPDFPath(cpOvercashier, pdfType);
			String pdf = PDF_Reader.readPdf(path);			
			waitNSec(5);			
			logger.info("pdf read completed");
			proc1.destroy();
			labelList = getLabelsList();			
			for(int labelIndex=0;labelIndex<labelList.size();labelIndex++){
				
				if(labelList.get(labelIndex).equals(preDefPeriod) ){
					verifyLabelInPdf(preDefPeriod, "period_id", labelList, labelIndex, pdf);					
				}
				else if(labelList.get(labelIndex).equals(fromLabel)){
					verifyDataInPdf("strt_date_id", fromLabel, labelList, labelIndex, pdf);					
				}
				else if(labelList.get(labelIndex).equals(toLable) ){
					verifyDataInPdf("end_date_id", toLable, labelList, labelIndex, pdf);					
				}
				else if(labelList.get(labelIndex).equals(colCountry) ){
					verifyLabelInPdf(colCountry, "country_id", labelList, labelIndex, pdf);					
				}
				else if(labelList.get(labelIndex).equals(colCity) ){
					verifyLabelInPdf(colCity, "city_id", labelList, labelIndex, pdf);					
				}
				else if(labelList.get(labelIndex).equals(colShop) ){
					verifyLabelInPdf(colShop, "shop_id", labelList, labelIndex, pdf);					
				}
				else if(labelList.get(labelIndex).equals(colPOS))
				{
					value = getSelectedItem(getObject("pos_id"));
					expectedText = "POS "+value;
					Assert.assertEquals(pdf.contains(expectedText), true);
					logger.info("Label 'POS' verified with PDF Data");
				}
			}

			logger.info("Labels and dropdowns verified with PDF Data");

			//Verifying tableheaders in PDF
			for(int headerIndex=0;headerIndex<getHeaderList("table3_xpath").size();headerIndex++){
				sb = sb.append(getHeaderList("table3_xpath").get(headerIndex));
				sb = sb.append(" ");
			}
			headerRow = sb.toString().trim();			   
			Assert.assertEquals(pdf.contains(headerRow), true);			  
			logger.info("Table Header verfied in PDF and as expected");


			//Verifying tableData in PDF			  
			WebElement tableRowColData = getObject("table3_xpath");
			List<WebElement> rows = tableRowColData.findElements(By.tagName("tr"));

			int index = 0;
			while(index <rows.size()-1){
				Assert.assertEquals(pdf.contains(getTableRow(index).trim()), true);	
				index++;
			}
			logger.info("Table data verfied in PDF and is same as displayed in application");
			restoreTables();
			logger.info(" EPL-1734 execution successful");
		} 
		catch (Throwable t)
		{			
			handleException(t);
		}
	}

	

}
