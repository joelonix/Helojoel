package com.ingenico.atlas.testsuite.customer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.os.WindowsUtils;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import au.com.bytecode.opencsv.CSVReader;
import com.ingenico.common.CommonConstants;

public class ATL310 extends TestSuiteBase
{
	private boolean isTestPass = true;
	private String error = null, fileName, imgPath;
	

	@BeforeClass
	public void loginSetUp()
	{
		try{			
			login(CONFIG.getProperty("internalautouser1"),CONFIG.getProperty("internaluserPassword"));			
			Assert.assertTrue(getCommonObject("login_name_id").getText().contains(CONFIG.getProperty("internalautouser1")));
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
	 *ATLAS Phase1.1/1.2 / Customer_PerCustomer / Contacts / ATL-310:FRCUe06: Contact list tab - Default view
	 * 
	 * @throws IOException
	 */

	@Test()
	public void atl310()
	{		
		try
		{
			logger.info(" atl310 execution started");			

			/*Step:1Navigate to Self-Provisioning-> Customer module.
			Click on Contact list tab. Verify the contents
			Should contain the following data in a table
			    1.Name
			    2.Phone
			    3.Email
			    4.Address
			    5.Comment,The data should be displayed in ascending order of the Name
                All columns should be aligned properly*/
			custNavigateSelfMode(autoCust1, moduleCustomer, "contactlist_tab_id");
			verifyEltEnabled("contactlist_tab_id");
			
			//Verify all the expected columns are present
			vCustCols();
			
			//Verify default order for contact list host.
			verifyColSortOrd("entiryname_colheader_xpath","ASC","ascendingOrder");
			logger.info("Verified that by default ContactList Name column was in Ascending Order");
			
			/*Step:2Check the page layout
			Should include pagination features, column selector by clicking 
			on a column header,sorting of columns ,export .CSV, export .PDF, Save this search, etc.*/
			for(i=0; i<pageFeatures.length; i++)
			{
				Assert.assertTrue(getObject(pageFeatures[i]).isDisplayed(), "The PageFeatures "+ pageFeatures[i] +" is not displayed");
			}
			
			logger.info("The page features are displayed");

			
			/*Step:3Check/uncheck columns in column selector
			column display should be according to user selection and the UI should be displayed proper*/
			getObject("managecolbutton_id").click();
			wait2Sec();
			wait2Sec();
			webelement=getObject("columns_xpath");
			act=new Actions(driver);
			act.moveToElement(webelement).build().perform();
			wait2Sec();
			getObject("columns_name_xpath").click();
			getObject("columns_phone_xpath").click();
			
			scrollUp();
			
			for(int i=0; i<removedcolIDArr.length; i++)
			{
				Assert.assertFalse(getObject(removedcolIDArr[i]).isDisplayed(),"Removed column is displayed");
				logger.info("Removed column is not displayed");
			}
			
			getObject("columns_name_xpath").click();
			getObject("columns_phone_xpath").click();
			getObject("managecolbutton_id").click();
			
			/*Step:4Click on Export PDF or Export CSV
			Data should be exported correctly in PDF or CSV respectively.*/
			verifyexportCsvFiles();

			logger.info(" atl310 execution successful");
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

	//------------------------Method Definitions--------------------------------------//
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
		if( getCommonObject("csv_button_xpath").isEnabled()){
			Thread.sleep(CommonConstants.sevenSec);
			getCommonObject("csv_button_xpath").click();
			logger.info("clicked on exported csv download link");
			Thread.sleep(CommonConstants.tenSeconds);
			Runtime.getRuntime().exec(System.getProperty("user.dir") + "\\auto-it\\FF.exe");
			Thread.sleep(CommonConstants.sevenSec);
			path = System.getProperty("user.dir") + "\\file-downloads\\"+ "ContactList" + ".csv";      
			csvData = readDownloadedCSV(path);
			WindowsUtils.tryToKillByName("FF.exe");
			//getting the entire applicationData
			List<WebElement> rows=getObjects("ongoingtableallrows_xpath");
			System.out.println(rows.size());
			for(int rownum=1;rownum<rows.size();rownum++){
				String postext=getObjects("ongoingtableallrows_xpath").get(rownum).getText();
				System.out.println(postext);
				addpostxt=addpostxt+" "+postext.trim();
			}
		    cols=colnames();
		  appposdata=cols.concat(addpostxt);
			//Assert.assertTrue(csvData.contains(addpostxt.trim()), "The CSV DATA is not matching with the Application data");
			//Comparing CSV data with table data
			int i =0;
			Boolean isStatus = true;
			for(String strVal : csvData){
				if(!appposdata.trim().contains(strVal.trim())){
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

	private String colnames(){
		String addedcols="";
		for(int i=0;i<colArrEng.length;i++){
			addedcols=addedcols+" "+colArrEng[i];
		}
		return addedcols;
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
		colName = getObject(colHdrLocator).getText();
		 colAtribute = getObject(colHdrLocator).getAttribute("class");
		Assert.assertTrue(colAtribute.endsWith(sortingOrder),colName + " column is not having "+sortingOrderStr+" order.");
	}
}