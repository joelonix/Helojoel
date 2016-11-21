package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1787.java $
$Id: EPL_1787.java 7955 2014-06-11 12:37:02Z cariram $
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;
import com.ingenico.util.PDF_Reader;

public class EPL_1787 extends SuiteCardPayment {

	private boolean isStatus=true;
	private String str =" ",appDataString,
			csvFilepath,xpath1,startDate,pdf;
	private ArrayList<String> list_extra_CSV_info = new ArrayList<String>(), csvData= new ArrayList<String>(),refinedCSV= new ArrayList<String>(),tableData= new ArrayList<String>();
	private List<WebElement> row,col,headerData,rows,headers=new ArrayList<WebElement>();
	private List<String> headerDataAsString;
	private boolean isShopPresent;
	private WebElement table;
	private int index,j=0;
	public static  Locale locale = Locale.ENGLISH;	

	/**
	 * getheaderData returns header list
	 * @return List<WebElement> 
	 */
	private	List <WebElement> getheaderColData(String dateTxt){
		headers=getheaderData(dateTxt);
		for(int i=0;i<headers.size();i++){
			if(headers.get(i).getText().equals("")){
				continue;
			}
		} 
		return headers;
	}


	/**
	 * getColumnData returns all columns data
	 * @return List<WebElement> 
	 */
	private	List <WebElement> getColumnData(String text){
		String xpath=getPath("nusdate_xpath").replace("TABLEID", text);
		WebElement table =getObjectDirect(By.xpath(xpath));
		List<WebElement> cols = table.findElements(By.tagName("td"));
		return cols;
	}

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
	private String getTableRow(int indexVal,String text){
		/*String setDate = getDateStringForAvaliableData();
		String xpath=getPath("table_store_reconciliation_xpath").replace("YESTDATE", setDate);*/
		String xpath=getPath("nusdate_xpath").replace("TABLEID", text);
		String xpath1=xpath.concat("/tbody");
		WebElement table =getObjectDirect(By.xpath(xpath1));
		row = table.findElements(By.tagName("tr"));
		col= row.get(indexVal).findElements(By.tagName("td"));
		return getTableRowCol(col);
	}

	/**
	 * getExtraCSVItems returns List of unintended csv items
	 * @return List<String>
	 */
	List<String> getExtraCSVItems(){
		list_extra_CSV_info.add("");
		list_extra_CSV_info.add("Shop");
		list_extra_CSV_info.add("NUS");
		list_extra_CSV_info.add("Reconciliation");
		list_extra_CSV_info.add("Source");
		list_extra_CSV_info.add("Bank Settlement Date");
		list_extra_CSV_info.add("Currency");
		list_extra_CSV_info.add("Store");
		list_extra_CSV_info.add(shop1SydneyBC);
		list_extra_CSV_info.add(startDate);
		list_extra_CSV_info.add("AUD");
		return list_extra_CSV_info;
	}
	/**
	 * EPL-1787 To Verify the CSV and PDF with application data
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1787() {
		try {
			logger.info(" EPL-1787 executing started");
			login(CONFIG.getProperty("superuser1"),	CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			// The breadcrumb must contain this text “Transaction Reconciliation”
			waitForElementPresent("cardpayment_link");
			cardPaymentSubPageNavigator(subModTrnRecon);
			verifyBreadCrumb(cpReconBC);
			logger.info("Transaction Reconciliation submodule is visible");

			//Set From and To date
			logger.info("Step 2:");
			setYearMonthDay();
			startDate = getObject("strt_date_id").getAttribute("value");

			//VERIFYING CSV Data
			// uncheck bank reconciliation to get results of only store  reconciliation
			logger.info("Step 3:");
			clickReconSearch("reconciliation_bank_id");	
			text=getNusDataTableId();
			rows = verifyNoReconcile("nusdate_xpath",text);
			if(rows.size()==1 && rows.get(0).getText().equals("Not yet reconciled.")){
				logger.info("Not yet reconciled.Test data not available for test to proceed.");
				Assert.fail();
			}
			else{
				// Download and read csv
				clickOnDownloadCSVPDF("export_csv_xpath", csvType);	
				waitNSec(5);
				csvFilepath = getCSVPDFPath(cpReconPdfFileName, csvType);				
				csvData = readDownloadedCSV(csvFilepath);
				//Filtering to get Data from CSV that matches the table data
				for(int data =0; data <csvData.size();data++){
					if(getExtraCSVItems().contains(csvData.get(data))){
						continue;
					}
					refinedCSV.add(csvData.get(data));
				}			
				//Adding header and column data of the table
				headers = getheaderColData(text);
				headers.addAll(getColumnData(text));
				for(int headName=0;headName<headers.size();headName++){
					if(headers.get(headName).getText().trim().equals("")){
						continue;
					}
					//headers.get(i).getText().trim();
					tableData.add(headers.get(headName).getText().trim());
				} 
				//Comparing CSV data with table data
				for(int i=0;i<tableData.size();i++){
					if(refinedCSV.get(j).matches("\\d{4}-\\d{2}-\\d{2}")){
						j=j+1;
						i=i-1;
						continue;	
					}
					if(!tableData.get(i).trim().contains(refinedCSV.get(j).trim())){
						isStatus = false;
						break;
					}
					j++;
				}

				Assert.assertTrue(isStatus, "CSV data is not as expected in the application");
				logger.info("CSV data is validated successfully");
				isShopPresent = getObject("bread_crumb_id").getText().contains(shop1SydneyBC);
				Assert.assertEquals(isShopPresent, csvData.contains(shop1SydneyBC));

				//VERIFYING PDF Data
				logger.info("Step 4:");
				clickOnDownloadCSVPDF("export_pdf_xpath", pdfType);	
				waitNSec(5);
				Process proc1 = Runtime.getRuntime().exec(CommonConstants.autoItPath+"FF.exe");
				path=getCSVPDFPath(cpReconPdfFileName, pdfType);
				pdf = PDF_Reader.readPdf(path);
				logger.info("pdf read completed");
				proc1.destroy();

				//Verifying Header	
				headerData = getheaderColData(text);
				headerDataAsString = new ArrayList<String>();

				for(int x=0;x<headerData.size();x++){
					headerDataAsString.add(headerData.get(x).getText());
				}
				StringBuffer sb5 = new StringBuffer();
				for(int j=0;j<headerDataAsString.size();j++){
					sb5 = sb5.append(headerDataAsString.get(j));
					sb5 = sb5.append(" ");
				}
				appDataString = sb5.toString().trim();
				Assert.assertEquals(pdf.contains(appDataString), true);
				logger.info("Table header validated in PDF");

				//Verifying rows
				String xpath=getPath("nusdate_xpath").replace("TABLEID", text);
				xpath1=xpath.concat("/tbody");
				table =getObjectDirect(By.xpath(xpath1));
				rows = table.findElements(By.tagName("tr"));

				//Verifying each row data with PDF
				index =0;
				while(index < rows.size()){
					Assert.assertEquals(pdf.contains(getTableRow(index,text).trim()), true);
					index++;
				}
				logger.info("Table Data validated in PDF");

				isShopPresent = getObject("bread_crumb_id").getText().contains(shop1SydneyBC);
				Assert.assertEquals(isShopPresent, pdf.contains(shop1SydneyBC));
			}
			logger.info(" EPL-1787 execution successful");
		}catch (Throwable t)
		{
			proc1.destroy();
			handleException(t);
		}
	}


}
