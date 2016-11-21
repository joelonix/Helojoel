package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1770.java $
$Id: EPL_1770.java 7916 2014-06-10 12:30:12Z cariram $
 */
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import au.com.bytecode.opencsv.CSVReader;

public class EPL_1770 extends SuiteCardPayment{
	private boolean isStatus;
	private String fileName;
	private List<WebElement> journalHeader,rows,cols;
	private List<String> jheaderList,colsList = new ArrayList<String>();
	private int dataIndex;
	private List<Integer> csvHeaderIndexList,tIndex = new ArrayList<Integer>();
	private List<String[]> csvData= new ArrayList<String[]>();
	private ArrayList<String>refinedCSVList = new ArrayList<String>(),tableList;
	private CSVReader reader;

	
	/**
	 * readCSVHeaderIndex returns all csv header indices
	 * @return ArrayList<Integer> 
	 */
	private ArrayList<Integer> readCSVHeaderIndex(String path) throws IOException{
		reader = new CSVReader(new FileReader(path));
		String[] array = reader.readNext();
		ArrayList<Integer> headerIndexList = new ArrayList<Integer>();
		System.out.println("Array Length = "+array.length);
		for(int i=0;i<array.length;i++){
			System.out.println("Array Item = "+array[i]);
			if(array[i].contains(curr) || array[i].contains(colCountry) || array[i].contains(colCity) || array[i].contains(tabCashier) 
					|| array[i].contains(pinPadLabel) || array[i].contains(authNumLabel)) {
				headerIndexList.add(i);
			}
		}
		reader.close();
		//new File(path).delete();
		logger.info("Header indices read successfully");
		return headerIndexList;
	}
	/**
	 * readDownloadedCSVData returns all csv data
	 * @return List<String[]> 
	 */
	private	List<String[]> readDownloadedCSVData(String path) throws IOException{
		CSVReader reader = new CSVReader(new FileReader(path));
		List<String[]> listfromCSV = reader.readAll();
		reader.close();
		new File(path).delete();
		logger.info("CSV Data read is successful");
		return listfromCSV;
	}

	/**
	 * EPL-1770 All applications / Check export CSV of Main View
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1770()
	{		
		try
		{
			logger.info(" EPL-1770 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			
			//Selecting appType EMV AUS
			cardPaymentSubPageNavigator(subModTrnJrn);
			multiSelect("select_appli", emvAusAppVal);			
			logger.info("Select Application Type" + emvAUSApp);
			getObject("search_link").click();
			logger.info("Step 2:");
			logger.info("Search clicked based on application type EMV AUS ");

			// Download and read csv
			logger.info("Step 3:");
			clickOnDownloadCSVPDF("export_csv_xpath", csvType);
			fileName = cpCSVJrn+date_Formatter("_");
			String path = getCSVPDFPath(fileName, csvType);

			//Getting the header Index Data from CSV
			csvHeaderIndexList = readCSVHeaderIndex(path);
			csvData = readDownloadedCSVData(path);
			//Eliminating the extra data from CSV to match it with table data
			for(String[] array: csvData)
			{
				for(int csvIndex=0;csvIndex<array.length;csvIndex++){
					if(csvHeaderIndexList.contains(csvIndex)){
						continue;
					}
					refinedCSVList.add(array[csvIndex]);
				}
			}
			logger.info("CSV Data is ready");

			//Fetching table data
			getTableData();

			//Comparing CSV data with table data
			logger.info("Step 4:");
			dataIndex =0;
			isStatus = true;
			for(String strVal :refinedCSVList){
				if(!tableList.get(dataIndex).contains(strVal)){
					isStatus = false;
					break;
				}
				dataIndex = dataIndex+1;
			}
			if(isStatus) {
				logger.info("CSV data is validated"); 
			}
			logger.info(" EPL-1770 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	/**
	 * prepare table Data
	 */
	private void getTableData()
	{
		journalHeader = get_list_of_elements("card_payment_journal_table_header_xpath");
		jheaderList =  new ArrayList<String>();
		for(int jheadIndex=0;jheadIndex<journalHeader.size();jheadIndex++)  {
			jheaderList.add(journalHeader.get(jheadIndex).getText());
		}
		for(int i=0;i<journalHeader.size();i++){
			if(journalHeader.get(i).getText().contains("Settlement") || journalHeader.get(i).getText().contains("Auth.")){
				tIndex.add(i);
			}
		}
		//Eliminating extra table data that is not present in CSV
		rows = getObject("table_card_payment_journal_xpath").findElements(By.tagName("tr"));
		for(int rIndex = 0; rIndex < rows.size();rIndex++){
			cols = rows.get(rIndex).findElements(By.tagName("td"));
			for(int colsIndex=1;colsIndex<cols.size();colsIndex++){
				if(tIndex.contains(colsIndex)){
					continue;
				}
				else{
					if(colsIndex== jheaderList.indexOf("Mode")){
						colsList.add(cols.get(colsIndex).findElement(By.tagName("img")).getAttribute("title"));
					}
					colsList.add(cols.get(colsIndex).getText());
				}
			}
		}
		//Concatinating table header and body data
		jheaderList.addAll(colsList);
		jheaderList.remove(0);
		tableList = new ArrayList<String>();
		for(String s:jheaderList){
			if(s.equals("Settlement") || s.equals("Settlement Date") || s.equals("Auth."))  continue;
			else
				tableList.add(s);
		}
		for(int tableIndex = 0; tableIndex <tableList.size() ;tableIndex++){
			if(tableList.get(tableIndex).equals("Online") || tableList.get(tableIndex).equals("Offline")){
				tableList.remove(tableIndex+1);
			}
		}
		logger.info("Table Data is ready");
	}

}
