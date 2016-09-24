package com.ingenico.atlas.testsuite.customer;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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

public class ATL1682 extends TestSuiteBase
{
	private boolean isTestPass = true;
	private String error = null, fileName, imgPath,selectitem,ascendingOrder,small="testdata",pdfData;
	private List<WebElement>  colVals;
	private int pagenum,nxtpagenum,prvpagenum;
	private ArrayList<String> csv;
	private List<String[]> listfromCSV;
	private ArrayList<String> csvData= new ArrayList<String>();


	@BeforeClass
	public void loginSetUp()
	{
		try{			
			login(CONFIG.getProperty("internalautouser2"),CONFIG.getProperty("internaluserPassword"));			
			Assert.assertTrue(getCommonObject("login_name_id").getText().contains(CONFIG.getProperty("internalautouser2")));
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
	 * ATL1684:Create Entity - Breadcrumbs
	 */

	@Test()
	public void atl1682()
	{		
		try
		{
		
			logger.info(" atl1682 execution started");			
			//select the customer 
			selectCustomer(multiCust);
			logger.info("Selected automatic Customer");

			//Navigate to Self-Provisioning-> Customer module.
			WaitForElementDisplayed("provisioning_link");
			wait2Sec();
			provisioningSubPageNavigator(moduleCustomer);
			logger.info("Navigated to Customer module");

			//navigating to the customer page
			waitforPagetoLoad(By.id(getPath("customerlistab_id")));
			wait2Sec();

			//clickonEntity tab
			clickOnEntitytab();

			//sorting the Name Column of entitylist table
			verifyColSortOrd("entiryname_colheader_xpath","ASC",ascendingOrder);
			logger.info("Verified that by default Entity list Name column was in Ascending Order");
			sortColData("entity_namecol_allrows_xpath");
			getObject("entityListTab_id").click();
			logger.info("Clicked on Entity list tab");
			waitExplicitDisplayed(By.id(getPath("entitytable_header_id")));
			Thread.sleep(CommonConstants.oneSec);
			vColValsOrders("entiryname_colheader_xpath","entity_namecol_allrows_xpath", "Name", "ASC","Ascending Order");
			vColValsOrders("entiryname_colheader_xpath","entity_namecol_allrows_xpath", "Name", "DESC","Descending Order");

			//Check for the filter options on the left hand side of the screen under Entity Tab
			Assert.assertTrue(getObject("filtertxt_xpath").isDisplayed(),"Filters are not Present on the Entity List page");
			Assert.assertTrue(getObject("seealso_txt_xpath").isDisplayed(),"See Also Field is not present in the Entity list Page");
			logger.info("Verified the presence of '"+"Filter' and also '"+"See Also' Fields in the Entity List Page");
			verifyFilteritems();

			// Check whether search fields are case sensitive or not
			verifyNotcaseSensitive(small);

			//Select some Filter criteria and Search Download the list as CSV file
			/*selectCriteria();
			logger.info("Selected criteria for exporting CSV ");
			verifyexportCsvFiles();

			//verifying the PDF Data
			verifyPdfFiles();
			 */
			//Verifying Page Navigation
			getObject("entityListTab_id").click();
			logger.info("Clicked on Entity list tab");
			waitExplicitDisplayed(By.id(getPath("entitytable_header_id")));
			Thread.sleep(CommonConstants.oneSec);
			scrollDown();
			isfrstandlstpagenavigated();
			isnextpageNavigated();
			isprevpagenavigated();
			logger.info("Verified the Pageination of Entity");
			logger.info("ATL1682 Executed Successfully");

		}
		catch (Throwable t)
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

	private void selectCriteria() throws InterruptedException{
		String entityname=getObject("entityname_frstval_xpath").getText();
		getObject("namefilter_id").clear();
		if(entityname.length()>=3)
			getObject("namefilter_id").sendKeys(entityname.substring(0, 2));
		else
			getObject("namefilter_id").sendKeys(entityname);
		getCommonObject("request_searchbuttn_xpath").click();
		waitForElementNotPresent("ongoingreq_loading_xpath");
		Thread.sleep(CommonConstants.twoSec);
	}
	/**
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private ArrayList<String> readDownloadedCSV(String path) throws IOException{
		CSVReader reader = new CSVReader(new FileReader(path));
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
		String addpostxt="";
		if( getCommonObject("exportcsv_xpath").isEnabled()){
			getCommonObject("exportcsv_xpath").click();
			logger.info("clicked on exported csv download link");
			Thread.sleep(CommonConstants.threeSec);
			Runtime.getRuntime().exec(System.getProperty("user.dir") + "\\auto-it\\FF.exe");
			Thread.sleep(CommonConstants.sevenSec);
			path = System.getProperty("user.dir") + "\\file-downloads\\"+ "Entity" + ".csv";      
			csvData = readDownloadedCSV(path);
			WindowsUtils.tryToKillByName("FF.exe");

			//getting the entire posdata
			List<WebElement> rows=getObjects("ongoingtableallrows_xpath");
			for(int rownum=0;rownum<rows.size();rownum++){
				String postext=getObjects("ongoingtableallrows_xpath").get(rownum).getText();
				addpostxt=addpostxt+postext;
			}

			//Comparing CSV data with table data
			int i =0;
			Boolean isStatus = true;
			for(String strVal : csvData){
				if(!addpostxt.trim().contains(strVal)){
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
	/**
	 * Validating PDF
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void verifyPdfFiles() throws InterruptedException, IOException{
		if( getCommonObject("exportpdf_xpath").isEnabled()){
			getCommonObject("exportpdf_xpath").click();
			logger.info("clicked on exported pdf download link");
			Thread.sleep(CommonConstants.threeSec);
			Runtime.getRuntime().exec(System.getProperty("user.dir") + "\\auto-it\\FF.exe");
			Thread.sleep(CommonConstants.sevenSec);
			path = System.getProperty("user.dir") + "\\file-downloads\\"+ "Entity" + ".pdf";
			pdfData=PDF_Reader.readPdf(path);
			logger.info("pdf file read completed");
			WindowsUtils.tryToKillByName("FF.exe");

			List<WebElement> rows=getObjects("ongoingtableallrows_xpath");
			//validating PDF data
			for(int rownum=0;rownum<rows.size();rownum++){
				String postext=getObjects("ongoingtableallrows_xpath").get(rownum).getText();
				Assert.assertTrue(pdfData.contains(postext.trim()), "The PDF DATA is not matching with the Application data");
			}
		} 
		logger.info("Table Data validated in PDF");
	}

	/**
	 * Verifying whether Name filed is case sensitive or not
	 * @param small
	 */
	private void verifyNotcaseSensitive(String small){
		getObject("namefilter_id").clear();
		getObject("namefilter_id").sendKeys(small);
		String actTxt=getObject("namefilter_id").getAttribute("value");
		Assert.assertEquals(actTxt, small,"The Name Field is not accepting the Small letters");
		getObject("namefilter_id").clear();
		getObject("namefilter_id").sendKeys(small.toUpperCase());
		actTxt=getObject("namefilter_id").getAttribute("value");
		Assert.assertEquals(actTxt, small.toUpperCase(),"The Name Field is not accepting the Capital letters");
		logger.info("Verified the Name Filter is not Case Sensitive");
	}
	/**
	 * Verifying the Fields in Filters Option
	 */
	private void verifyFilteritems(){
		System.out.println(getObject("filterimg_xpath").isSelected());
		if((getObject("filterimg_xpath").isSelected())==true){
			getObject("filterimg_xpath").click();
		}
		Assert.assertTrue(getObject("filterarea_xpath").isDisplayed());
		Assert.assertTrue(getObject("namefilter_id").isDisplayed(),"Name Field is not Present in the Filters field");
		Assert.assertTrue(getObject("sapcode_txtfiled_id").isDisplayed(),"SAP CODE Field is not Present in the Filters field" );
		logger.info("Verified the presence of Name and SAP code Text Fields");
	}
	/**
	 * 
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
		System.out.println(colAtribute);
		Assert.assertTrue(colAtribute.endsWith(sortingOrder),colName + " column is not having "+sortingOrderStr+" order.");
	}

	void vColValsOrders(String colHdrLocator,String colValsLocator, String colName, String sortingOrder, String sortingOrderStr) throws InterruptedException{ 
		vals.clear();
		if(sortingOrder.equals("ASC")){ 
			selectShowResMaxval("5");
			scrollUp();
			colVals = getObjects(colValsLocator);
			for (itemCount = 0; itemCount< colVals.size(); itemCount++) {
				Assert.assertEquals(colVals.get(itemCount).getText(), ascVals.get(itemCount),  colName + " column expected page values are not sorted in '"+sortingOrderStr+"' order.");
			}
		} 
		else {
			selectShowResMaxval("5");
			scrollUp();
			getObject(colHdrLocator).click();
			waitForElementNotPresent("ongoingreq_loading_xpath");
			String colAtribute = getObject(colHdrLocator).getAttribute("class");
			if(colAtribute.endsWith("ASC"))
				getObject(colHdrLocator).click();
			else
				logger.info("The Column is already in Descending Order no need to click"); 
			colVals = getObjects(colValsLocator);
			for (itemCount = 0; itemCount< colVals.size(); itemCount++) {
				System.out.println(colVals.get(itemCount).getText());
				System.out.println(descVals.get(itemCount));
				Assert.assertEquals(colVals.get(itemCount).getText(), descVals.get(itemCount+1),colName + " column expected page values are not sorted in '"+sortingOrderStr+"' order.");
			}
		}

		// count = count + PgSz;
		logger.info("Verified, '"+ colName + "' column expected page values are sorted in '"+sortingOrderStr+"' order.");
	}

	/**
	 * 			  
	 * @param colLoc
	 * @throws InterruptedException
	 */
	void sortColData(String colLoc) throws InterruptedException{
		ascVals.clear();
		descVals.clear();
		vals.clear();
		colVals = getObjects(colLoc);
		selectShowResMaxval(showresMaxval);
		String pageNumTxt=getObject("pageof-pagenumtxt_xpath").getText();
		String[] pagenumcnt=pageNumTxt.split(" ");
		String pagenum= String.valueOf(pagenumcnt[1]);
		int totpage=Integer.parseInt(pagenum);
		System.out.println(totpage);

		for(int pgcnt=1;pgcnt<=totpage;pgcnt++){
			if(pgcnt>1){
				getObject("entitytable_nxtpagebuttn_xpath").click();
				waitForElementNotPresent("ongoingreq_loading_xpath");
				Thread.sleep(CommonConstants.twoSec);
			}
			colVals = getObjects(colLoc);
			for (itemCount = 0; itemCount < colVals.size(); itemCount++) {
				vals.add(colVals.get(itemCount).getText());
			}

		}
		Collections.sort(vals);
		ascVals.addAll(vals);
		System.out.println(ascVals);
		Comparator<String> mycomparator = Collections.reverseOrder();
		Collections.sort(vals,mycomparator);
		descVals.addAll(vals);
		System.out.println(descVals);

	}
}
	

	
