package com.ingenico.atlas.testsuite.tms;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.os.WindowsUtils;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import au.com.bytecode.opencsv.CSVReader;
import com.ingenico.common.CommonConstants;
import com.ingenico.util.PDF_Reader;

public class ATL1104 extends TestSuiteBase {
	private boolean isTestPass = true;
	private String error = null, fileName, imgPath;
		
	@BeforeClass
	public void loginSetUp()
	{
		try{			
			login(CONFIG.getProperty("internalautouser2"),CONFIG.getProperty("internaluserPassword"));			
			Assert.assertTrue(getCommonObject("login_name_id").getText().contains(CONFIG.getProperty("internalautouser2")),"login failed");
			logger.info("Login Successfully");
		}
		catch(Throwable e)
		{			
			isTestPass = false;
			reportTestResult();
			driver.quit();			
			Assert.fail("Exception thrown while logging Application");
		}	
	}	

	@BeforeTest
	public void beforeEachTest()
	{
		checkTestSkip(this.getClass().getSimpleName());
	}

	/**
	 * 	ATL-1104:TMS Host Pagination ,verifying Export CSV/PDF
	 */

	@Test()
	public void atl1104()
	{		
		try
		{
			logger.info(" atl1104 execution started");			
			//select the customer 
			multiCustNavToTMS(multiCust,tmsMod);
			
			//Verifying the Basic features of TMS Table
			verifybasicTablefeatures();
			
			//Verify default order for TMS host.
			verifyColSortOrd("tmsname_colheader_xpath","ASC","ascendingOrder");
			logger.info("Verified that by default Entity list Name column was in Ascending Order");
			
			//Verify for Pagination - 
			verifyPagination();
			
			//Export PDF
			verifyPdfFiles();
			
			//verifying Export CSV
			verifyexportCsvFiles();
			
			//Need to automate sortingorder once we get clarification from QA team if italic values verifying the Column choice
			verifyColDisAppearsAfterUnChk("Name","tms_namecolname_xpath", "tmscolchoice_colchkbox_xpath");
			logger.info("Verified the Column Choice");
			logger.info("ATL1104 Executed successfully");
		}catch (Throwable t)
		{
			fileName=this.getClass().getSimpleName();
			imgPath=CommonConstants.screenshotPath+ fileName + ".jpg";
			captureScreenShotOnFailure(fileName);
			isTestPass = false;
			error=t.getMessage();
			logger.error(fileName+" execution failed:<a href='"+imgPath+"'><img src='"+imgPath+"' height="+20+" width="+40+" /></a>");
			Assert.fail(error,t);
			return;
		}
	}

	@AfterTest
	public void reportTestResult()
	{
		reportTestResultInExcel(this.getClass().getSimpleName(), isTestPass);
	}
	/**
	 * Tick off the check box of the specified field. Verify, the check box is ticked off and 
	 * the specified column inside the table disappears.
	 * @throws InterruptedException 
	 */
	void verifyColDisAppearsAfterUnChk(String expCol,String expColLocator, String colChkBLocator) throws InterruptedException{
		scrollDown();
		getObject("managecolbutton_id").click();
		waitForElementPresent("columns_xpath");
		wait2Sec();
		actionClickonElt("columns_xpath");
		wait5Sec();
		clickOnObjDirect(colChkBLocator, "COLNAME", expCol);
		verifyElementNotSelected(xpath);
		logger.info("Not tikked off the '"+expCol+"' column.");
		logger.info("Verified, tikked off the '"+expCol+"' column.");
		verifyEltIsDisplayed(expColLocator, expCol);
		logger.info("Verified, '"+expCol+"' column disappers, inside the table.");
	}
	/*
	verifying basic table features
	 */
	private void verifybasicTablefeatures() throws InterruptedException{
		
		verifyEltIsDisplayed("tmstable_id", " Tms Table structure");
		//verifying Column names
		clickonrestore();
		verifyEltIsDisplayed("tms_namecolname_xpath", "Name Column");
		verifyEltIsDisplayed("tms_desccolname_xpath", "desccolname");
		verifyEltIsDisplayed("tms_adresscolname_xpath", "adresscolname");
		verifyEltIsDisplayed("exportcsv_xpath", "Export CSV Button");
		verifyEltIsDisplayed("exportpdf_xpath", "Export pdf Button");
		verifyEltIsDisplayed("managecols_bttn_id", "Managing Columns BUtton");
		verifyEltIsDisplayed("requestpage_restoreimg_css", "Restore image Button");
		verifyEltIsDisplayed("tmstable_footer_xpath", "Footer");
	}
	/**
	 * verifying columns in sorted order
	 * @param colHdrLocator
	 * @param sortingOrder
	 * @param sortingOrderStr
	 * @throws InterruptedException
	 */
	private void verifyColSortOrd(String colHdrLocator,  String sortingOrder,  String sortingOrderStr) throws InterruptedException{
		WebElement cols=getObject(colHdrLocator);
		cols.click();
		waitForElementNotPresent("ongoingreq_loading_xpath");
		String colName = getObject(colHdrLocator).getText();
		String colAtribute = getObject(colHdrLocator).getAttribute("class");
		Assert.assertTrue(colAtribute.endsWith(sortingOrder),colName + " column is not having "+sortingOrderStr+" order.");
	}
	/**
	 * verifying the footer buttons
	 * @throws InterruptedException 
	 * 
	 */
	private void verifyPagination() throws InterruptedException{
		
		//verifying the presence of Displayed text
		if(getCommonObject("frst_page_xpath").isDisplayed()){
			getCommonObject("frst_page_xpath").click();
		}
		wait2Sec();
		verifyEltIsDisplayed("display_records_xpath", "displaying records");
		if(getObject("display_records_xpath").getText().contains("No results to display")){
			logger.info("No results to display");
		}
		else{
			Assert.assertTrue(getObject("display_records_xpath").getText().startsWith("Displaying 1 to "));
			logger.info("Verified the Presence of Displaying Records Text");
			
			//verifying the presence of page 1 of pagenum txt
			verifyEltIsDisplayed("currentpageno_name", "currentpageno");
			verifyEltIsDisplayed("page_txt_xpath", "page txt");
			Assert.assertTrue((getCommonObject("page_txt_xpath").getText().contentEquals("Page")));
			verifyEltIsDisplayed("pageof-pagenumtxt_xpath", "pageof pagenumtxt");
			Assert.assertTrue(getCommonObject("pageof-pagenumtxt_xpath").getText().startsWith("of"));
			logger.info("Verified the presence of Page of pagenum text ");
			
			//verifying the Pagination: presence of Navigation to the next page and Previous page,first and last page
			rows=getObjects("ongoingtableallrows_xpath");
			rowsize=rows.size()-1;
			logger.info("The Number of records displayed are:"+ rowsize);
			verifyrecordsperpage("10");
			verifyrecordsperpage("15");
			verifyrecordsperpage("20");
			verifyrecordsperpage("5");
			isfrstandlstpagenavigated();
			isnextpageNavigated();
			isprevpagenavigated();
			logger.info("Verified the navigation of first and last page");
		}
	}
	/**
	 * Validating PDF
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void verifyPdfFiles() throws InterruptedException, IOException{
		if( getCommonObject("exportpdf_xpath").isEnabled()){
			getCommonObject("exportpdf_xpath").click();
			logger.info("clicked on exported pdf download link");
			wait2Sec();
			Runtime.getRuntime().exec(System.getProperty("user.dir") + "\\auto-it\\FF.exe");
			wait5Sec();
			wait5Sec();
			path = System.getProperty("user.dir") + "\\file-downloads\\"+ "TMSHostList" + ".pdf";
			pdfData=PDF_Reader.readPdf(path);
			logger.info("pdf file read completed");
			WindowsUtils.tryToKillByName("FF.exe");
			rows=getObjects("ongoingtableallrows_xpath");
			
			//validating PDF data
			for(rownum=1;rownum<rows.size();rownum++){
				 cols=getObjects("ongoingtableallrows_xpath").get(rownum).findElements(By.tagName("td"));
				for(colnum=1;colnum<cols.size();colnum++){
					txt=cols.get(colnum).getText();
					tabletxt=tabletxt+" "+txt;
				}
			}
			Assert.assertTrue(pdfData.contains(tabledata.trim()), "The PDF DATA is not matching with the Application data");
		} 
		logger.info("Table Data validated in PDF");
	}
	/**
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private ArrayList<String> readDownloadedCSV(String path) throws IOException{
		reader = new CSVReader(new FileReader(path));
		listfromCSV = reader.readAll();
		csv= new ArrayList<String>();
		for(String[] array: listfromCSV)
		{
			for(String str: array)
			{
				csv.add(str); 
			}
		}
		reader.close();
		new File(path).delete();
		logger.info("CSV Data read is successful");
		return csv;
	}
	private void verifyexportCsvFiles() throws InterruptedException, IOException{
		if( getCommonObject("exportcsv_xpath").isEnabled()){
			getCommonObject("exportcsv_xpath").click();
			logger.info("clicked on exported csv download link");
			wait2Sec();
			Runtime.getRuntime().exec(System.getProperty("user.dir") + "\\auto-it\\FF.exe");
			wait5Sec();
			wait2Sec();
			path = System.getProperty("user.dir") + "\\file-downloads\\"+ "TMSHost" + ".csv";      
			csvData = readDownloadedCSV(path);
			WindowsUtils.tryToKillByName("FF.exe");
			
			//Adding columns
			appData.clear();
			for(i=0;i<tmscolNames.length;i++){
				appData.add(tmscolNames[i]);
			}
			
			//validating PDF data
			 rows=getObjects("ongoingtableallrows_xpath");
			for(rownum=1;rownum<rows.size();rownum++){
				cols=getObjects("ongoingtableallrows_xpath").get(rownum).findElements(By.tagName("td"));
				for(colnum=1;colnum<cols.size();colnum++){
					String txt=cols.get(colnum).getText();
					appData.add(txt);
				}
			}
			
			//Comparing CSV data with table data
			i =0;
			Boolean isStatus = true;
			for(String strVal : appData){
				
				if(!csvData.contains(strVal)){
					isStatus = false;
					break;
				}
				i = i+1;
			}
			if(isStatus) {
				logger.info("CSV data is validated");
			}
			else{
				logger.error("CSV data is not validated with appdata");
				Assert.fail("CSV data is not matching with appdata");
			}
		}
	}
	
}
