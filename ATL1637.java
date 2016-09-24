package com.ingenico.atlas.testsuite.customer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.ingenico.common.CommonConstants;

public class ATL1637 extends TestSuiteBase
{
	private boolean isTestPass = true,True=true;
	private String error = null, fileName, imgPath,ascendingOrder;
	String[] entityColNames = {"name_entitycol_id", "sapcode_entitycol_id", "comment_entitycol_id","entity_tablefooter_xpath","entity_savebutton_xpath","entity_chkbox_xpath","managecolbutton_id","exportpdf_xpath","exportcsv_xpath"};
	private List<WebElement>  colVals;


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
	 *  ATL-1685:Create Entity - Validations 
	 * 
	 * @throws IOException
	 */
	@Test()
	public void atl1637()
	{		
		try
		{
			logger.info(" atl1637 execution started");	
			/*Step:1Login as an Internal user and select Multi View >> Customer
			Check for the new Entity Sub module under Customer page
			Customer multi-view page should have 2 tabs
			1. Entity list tab that contains entity list table
			2. Customer list tab that will contain actual customer page      (customer list table)*/
			custNavigateProvMode(multiCust, moduleCustomer, "customerlistab_id");
			waitForElementNotPresent("loading_xpath");
			verifyEltIsDisplayed("customerlistab_id", "CustomerListTab");
			verifyEltIsDisplayed("entityListTab_id", "EntityListTab");
			clickOnEntitytab();
			waitForElementNotPresent("loading_xpath");
			
			
			/*Step:3Check for the Entity List sub module
			The Main zone is composed of a table which will benefit from all table features (footer, column choice, export .CSV, export .PDF, Save this search, etc.)
			Column name
			C1 NA checkbox to select the entity
			C2 Name The entityname
			C3 SAP code The entitySAP code
			C4 comment*/
			for(int i=0; i<entityColNames.length; i++)
			{
				Assert.assertTrue(getObject(entityColNames[i]).isDisplayed(),"The filter "+entityColNames[i] +" is not displayed");
				logger.info("The filter "+entityColNames[i] +" is displayed");
			}
			
			/*Step:4Check for the order of the Entity list
			 By default, the entity list is ordered by name in ascending order.*/
			//verifyColSorting();
			verifyColSortOrd("entiryname_colheader_xpath","ASC",ascendingOrder);
			logger.info("Verified that by default Entity list Name column was in Ascending Order");
			
			/*Step:5Click on Create Entity button and verify the pop up
			The pop up should contain details which is given below in the screenshot*/
			getObject("add_entity_id").click();
			wait2Sec();
			logger.info("clicked on addentity button");
			
			verifyEltIsDisplayed("create_entity_name_id", "Entity name field");
			verifyEltIsDisplayed("create_entity_sapcode_id", "SapCode field");
			verifyEltIsDisplayed("entity_comment_id", "Entity Comment field");
			verifyEltIsDisplayed("quit_id", "quit button");
			
			/*Step:2Check for the mandatory symbol (*) for mandatory fields
			All the mandatory fields should have the symbol (*)*/
			verifyEltAttributeVal("name_mandatory_xpath", "class", "error-text");
			logger.info("Verified name label field as Mandatory field");
			
			verifyEltAttributeVal("sapcode_mandatory_xpath", "class", "error-text");
			logger.info("Verified sap code label field as Mandatory field");
			
			verifyEltAttributeVal("requesttype_mandatory_xpath", "class", "error-text");
			logger.info("request type label field Contains error-text");
			getObject("quit_id").click();
			logger.info("Clicked on quit button");
			logger.info("ATL1637 executed successfully");		
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

	//-------------------Methoddefinitions----------------------------//
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

	public void verifyColSorting() throws InterruptedException{
	ArrayList<String> locationName = new ArrayList<String>();
	while(True){
		int size=getObjects("entity_namecol_allrows_xpath").size();
		for(int i=0;i<size;i++)	{
			locationName.add(getObjects("entity_namecol_allrows_xpath").get(i).getText().toLowerCase().replace("-","").replace("_",""));
		}
		String pageNo = getObject("no_of_pages_xpath").getText().replaceAll("[\\D]","").trim();
		System.out.println("pageNo"+pageNo+getObject("no_of_pages_input_xpath").getAttribute("value"));
		if(getObject("no_of_pages_input_xpath").getAttribute("value").equalsIgnoreCase(pageNo))
		{
			logger.info("finish reading all axis table data");
			break ;
		}
		getObject("nextpagebutton_xpath").click();
		Thread.sleep(CommonConstants.fourSec);
	}
	ArrayList<String> locationNamesorted = new ArrayList<String>();
	locationNamesorted.addAll(locationName);
	Collections.sort(locationNamesorted);
    Assert.assertTrue(locationNamesorted.equals(locationName));
    logger.info("verified axis location column is available in ascending order");
	}	

}
