package com.ingenico.testsuite.tmsmanagement;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/tmsmanagement/SuiteTmsManagement.java $
$Id: SuiteTmsManagement.java 18141 2016-04-19 12:46:52Z haripraks $
 */

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;

import com.ingenico.base.TestBase;

/**
 * Common methods and variables of TMS suite
 */
public class SuiteTmsManagement extends TestBase {

	//Declaration of common variables
	public final static String RMIPROTOCOL="RMI",BATCH="Batch",BATCHNREQ="Batch and on request",SIGNATURE="Signature",ROOTSIGNTR="ROOTSIGNTR",ROOTNAME="ROOTNAME",ADDTERMINALSSUCCESS="Terminals have been added successfully.",
			UPDATSUCSSMSG="successfully updated",SHRTNAMECOL="Short name",ADDNESTATE="Add an estate",ADDTERMINALS="Add terminals",ADDESTATESUCCESS="The estate has been created with success",OWNER="Owner",LOCATION="Location",
			U32="U32",WINCE="WINCE",TELIUM="Telium",TETRA="TETRA",SIGNATUR="SIGNATUR",COLOWNER="Owner",COLLOCATION="Location",MODIFY="MODIFY",FIND="FIND",MOVE="MOVE",ADDNOWNER="Add an owner",ADDLOCATION="Add a location",
			ADDEVENTS="Add events",ADDINSPEC="Add inspection",ADDINVENT="Add inventory",MODFY="Modify",FOOTERDISP="Number of elements displayed";
	
	String sigTemp,tempSeril,terPartNum,serilPart;

	//Framework Related Functions

	/** 
	 * Initializes suite execution	  
	 */
	@BeforeSuite(alwaysRun=true)
	void initSetUp()   {
		initialize();
	}	

	/**
	 * To Generate Report
	 */
	public void generateReport(){
		selUtils.slctChkBoxOrRadio(selUtils.getObject("active_chkbox_id"));
		selUtils.populateInputBox("shortname_id", testDataOR.get("report_group"));
		selUtils.selectItem(selUtils.getObject("sel_notassownrs_id"), testDataOR.get("customer"));
		populateReportForm();


	}
	
	/**
	 * Verifies if Customer is current tab, if not navigate to dash board
	 *
	 */
	public void nvigateToModule(String locator,String moduleName) {
		try{
			((JavascriptExecutor)driver).executeScript(JSCLICK, selUtils.getCommonObject(locator));
			logger.info("Navigated to "+moduleName+" page");	
		} catch (Throwable e) {
			Assert.fail("Failed while clicking on "+moduleName);
		}

	}

	/**
	 * Setting start or end date from calendar icon	 
	 */
	public void setDate()
	{
		selUtils.clickOnWebElement(selUtils.getCommonObject("nextexeDate_id"));
		waitMethods.waitForWebElementPresent(selUtils.getCommonObject("selDate_id"));
		((JavascriptExecutor)driver).executeScript(JSCLICK, selUtils.getCommonObject("selcurrent_css"));	
	}

	/**
	 * Verify the success message
	 * @param locator
	 * @param firstreplcae
	 * @param secondreplace
	 */
	public void  vReportIsDisplayed(String locator,String firstreplcae,String secondreplace){
		xpath=getPath(locator).replace("REPORTNAME", firstreplcae).replace("REPORTGROUP", secondreplace);
		Assert.assertTrue(selUtils.getObjectDirect(By.xpath(xpath)).isDisplayed(), " element is not displayed");
		logger.info("element is displayed");
	}

	/**
	 * Fill the Report Form
	 */
	public void populateReportForm(){
		String genMode=testDataOR.get("generation_mode"),strErrorMsg;
		selUtils.getObject("ownr_assbttn_id").click();
		selUtils.selectItem(selUtils.getObject("sel_genmode_id"), genMode);
		if(BATCH.equals(genMode)||BATCHNREQ.equals(genMode)){
			setDate();
			selUtils.selectItem(selUtils.getObject("sel_frquency_id"), testDataOR.get("frequency"));
		}
		selUtils.populateInputBox("description_id",testDataOR.get("free_text"));
		selUtils.selectItem(selUtils.getObject("sel_timezone_id"), testDataOR.get("time_zone"));
		selUtils.getObject("addrpt_plusbttn_xpath").click();
		selUtils.slctChkBoxOrRadio(selUtils.getObject("rpts_activchkbox_id"));
		selUtils.populateInputBox("rpts_shortname_id", testDataOR.get("report_short_name"));
		selUtils.populateInputBox("rpts_description_id",testDataOR.get("free_text"));
		selUtils.selectItem(selUtils.getObject("rpts_opformat_id"), testDataOR.get("output_Format"));
		selUtils.populateInputBox("rpts_filename_id",testDataOR.get("report_file_name"));
		if(BATCH.equals(genMode)||BATCHNREQ.equals(genMode)){
			//selUtils.populateInputBox("rpts_templatename_id",testDataOR.get("filename_template"));
			selUtils.populateInputBox("rpts_templatename_id",testDataOR.get("file_name_template"));
			selUtils.populateInputBox("rpts_referencehr_id",testDataOR.get("reference_hour"));
		}
		selUtils.getObject("addrpts_bbtn_id").click();
		// verify error messages
		if (selUtils.getCommonObject("erroraddeditreports_id").isDisplayed()) {
			strErrorMsg = selUtils.getCommonObject("erroraddeditreports_id").getText().trim();
			Assert.fail(strErrorMsg + " due to fail");
		}

	}

	/**
	 * Method to click on Available Reports
	 */
	public void clickOnAvalibleRpts(){
		if(selUtils.getObject("menurpts_id").getAttribute("class").equals("close")){
			//selUtils.getObject("menurpts_id").click();
			selUtils.clickOnWebElement(selUtils.getObject("menurpts_id"));
			logger.info("Clicked on menu reports");
			//selUtils.getObject("avail_report_link").click();
			selUtils.clickOnWebElement(selUtils.getObject("avail_report_link"));
			logger.info("Clicked on avail reports reports");
		}else{
			//selUtils.getObject("avail_report_link").click();
			selUtils.clickOnWebElement(selUtils.getObject("avail_report_link"));
			//((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObject("avail_report_link"));
			logger.info("clicked on avail report link");
			/*driver.navigate().refresh();
			logger.info("page is refreshed");
			waitNSec(3);
			selUtils.switchToFrame();
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObject("avail_report_link"));
			logger.info("clicked on avail report link after page refresh");
			waitNSec(2);*/
			//logger.info("clicked on available reports link");
		}

	}
	/**
	 * Method for clicking  the report  for editing
	 * @param reportName
	 * @author Nagaveni.Guttula
	 */
	public void clkOnEditReport(String reportName){
		colIndex=selUtils.getIndexForColHeader("colheaders_css", REPORTNAME);
		verifyLvlColLvlValPresence("entitytablelst_css", colIndex,reportName );
		clkOnDirectObj("editreprt_img_xpath", "REPORTNAME", reportName);
		logger.info("Clicked on the edit image of the "+reportName);
	}
	/**
	 * Method for editing the report values
	 * @param genModeVal
	 * @author Nagaveni.Guttula
	 */
	public  void editReportVals(String genModeVal){
		String strErrorMsg;
		if(getModWinDisp(selUtils.getObject("reprtpuptit_xpath"), TMSREPORTS)){
			selUtils.slctChkBoxOrRadio(selUtils.getObject("rpts_activchkbox_id"));
			selUtils.populateInputBox("rpts_shortname_id", testDataOR.get("report_short_name_2"));
			selUtils.populateInputBox("rpts_description_id",testDataOR.get("free_text_2"));
			selUtils.selectItem(selUtils.getObject("rpts_opformat_id"), testDataOR.get("output_format_2"));
			selUtils.populateInputBox("rpts_filename_id",testDataOR.get("report_file_name_2"));
			if(genModeVal.equals(BATCH) || genModeVal.equals(BATCHNREQ)){
				selUtils.populateInputBox("rpts_templatename_id",testDataOR.get("file_name_template_2"));
				selUtils.populateInputBox("rpts_referencehr_id",testDataOR.get("reference_hour_2"));
			}
			selUtils.getObject("addrpts_bbtn_id").click();
			// verify error messages
			if (selUtils.getCommonObject("erroraddeditreports_id").isDisplayed()) {
				strErrorMsg = selUtils.getCommonObject("erroraddeditreports_id").getText().trim();
				Assert.fail(strErrorMsg + " due to fail");
			}
			logger.info("Clicked on add button ");
		}
	}

	/**
	 * Method to verify created reports
	 * @param Cust,rptgrp,rptshrtname
	 * @throws InterruptedException 
	 */
	public void vCreatedRptGroup(String cust,String rptgrp,String rptshrtname) throws InterruptedException{
		logger.info("Start of  vCreatedRptGroup method");
		loginNclkAvailRpt(cust);
		vReportIsDisplayed("rept_grpname_xpath",cust,rptgrp);
		vReportIsDisplayed("rept_grpname_xpath",cust,rptshrtname);
		logger.info("END of  vCreatedRptGroup method");
//		//selUtils.switchToFrame();
//		driver.close();
//		driver.switchTo().window(parentwindow);
//		driver.switchTo().defaultContent();
	}

	/**
	 * Access ePortal with customer and go to TMS->Available reports
	 * @param customer
	 * @throws InterruptedException 
	 */
	public void loginNclkAvailRpt(String customer) throws InterruptedException
	{
		logoutEpSelCust(customer);
		//driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
		waitNSec(3);
		logger.info("Before TMS click");
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getCommonObject("tms_link"));
		selUtils.clickOnWebElement(selUtils.getCommonObject("tms_link"));
		//((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getCommonObject("dashboard_tab_xpath"));
		//selUtils.clickOnWebElement(selUtils.getCommonObject("tmstab_xpath"));
		//selUtils.clickOnWebElement(selUtils.getCommonObject("usermangement_link"));
		//selUtils.clickOnWebElement(selUtils.getCommonObject("tms_link"));
		//selUtils.clickOnWebElement(selUtils.getCommonObject("tms_link"));
		//((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getCommonObject("tms_link"));
		logger.info("After TMS click");
		//waitMethods.waitForWebElementPresent(selUtils.getCommonObject("content_id"));
		logger.info("Waiting for frame load");
		//wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(selUtils.getCommonObject("tmsiframe_xpath")));
		logger.info("loading frame");
		selUtils.switchToFrame();
		logger.info("Waiting for available report link");
		waitMethods.waitForWebElementPresent(selUtils.getCommonObject("bread_crum_id"));
		wait.until(ExpectedConditions.elementToBeClickable(selUtils.getObject("avail_report_link")));
		waitNSec(4);
		waitMethods.waitForWebElementPresent(selUtils.getObject("menurpts_id"));
		clickOnAvalibleRpts();
		logger.info("End of loginNclkAvailRpt method");
		//return parentwindow;
	}
	/**
	 * Validate report not displayed
	 * @param locator
	 * @param firstreplcae
	 * @param secondreplace
	 */
	public void vRptNotDisp(String locator,String firstreplcae,String secondreplace){
		xpath=getPath(locator).replace("REPORTNAME", firstreplcae).replace("REPORTGROUP", secondreplace);
		Assert.assertFalse(selUtils.isElementPresentxpath(xpath),"Failed due to element not present");
		logger.info("Element is not displayed");
	}
	/**
	 * Click on edit short report
	 * @param locator
	 * @param reportName
	 */
	public void clkOnEditshrtName(String locator,String reportName){
		clkOnDirectObj(locator, "REPORTNAME", reportName);
		logger.info("Clicked on the edit image of the "+reportName);
	}
	/**
	 * Navigate to TMS reports and edit report group name
	 * @param reportGrupName
	 */
	public void navigateNeditRPT(String reportGrupName)
	{
		navigateToSubPage(TMSREPORTS,selUtils.getCommonObject("tms_tab_xpath"),selUtils.getCommonObject("tms_reports_xpath"));
		clkOnEditReport(reportGrupName);
	}
	/**
	 * handle error message for negative scenario
	 * @param element
	 *  @author Hariprasad.KS
	 */
	public void vErrorMsg(WebElement element)
	{
		String strErrorMsg;
		if (element.isDisplayed()) {
			strErrorMsg = element.getText().trim();
			Assert.fail(strErrorMsg + " due to fail");
		}
	}
	
	/**
	*  TMS> Asset tracking and then click on submenu 
	*/
	public void navigateSubMenu(String subModName,String subModLoc,String subMenuLocator)
	{
		navigateToSubPage(subModName,selUtils.getCommonObject("eptmstab_xpath"),selUtils.getCommonObject(subModLoc));
		selUtils.switchToFrame();
		selUtils.clickOnWebElement(selUtils.getObject(subMenuLocator));
	}	
	
	/*
	*  Click on find and search signature in estate dropdownlist
	*
	*/
	public void findNSrchSign(String signature){
		selUtils.clickOnWebElement(selUtils.getObject("terminalsfind_id"));
		logger.info("Clicked on find link");
		options=selUtils.getObjects("findterminalestate_xpath");
		selUtils.selectValueInDropDown(options, signature);
		logger.info("Selected estatesignature option");
		waitMethods.waitForWebElementPresent(selUtils.getObject("waitforsearchdiv_xpath"));
		selUtils.clickOnWebElement(selUtils.getObject("findterminalssearch_xpath"));
		logger.info("Clicked on search");
		selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
	}
	
	/*
	* Verify terminals first and last serial numbers concatanation of Automated terminal part number
	*/
	public void trminlSignCrte(String fSeriln,String lSeriln,String termPartN,String termTech)
	{
		long sLen,sNumInc,fSNum=Long.valueOf(fSeriln),lSNum=Long.valueOf(lSeriln);
		
		sLen=lSNum-fSNum;
		if(termTech.equalsIgnoreCase(TELIUM) | termTech.equalsIgnoreCase(TETRA)){
			terPartNum=termPartN.substring(0,8);	
			for(int sLop=0;sLop<=sLen;sLop++){	
				sNumInc=fSNum+sLop;
				tempSeril=Long.toString(sNumInc);
				serilPart=tempSeril.substring(tempSeril.length()-8, tempSeril.length());
				sigTemp=terPartNum.concat(serilPart);
				vSingleTblValue("terminalSignature_xpath",SIGNATUR,sigTemp);
			}
		}
		else
			Assert.fail("Invalid technology has selected");
	}
	
}
