package com.ingenico.testsuite.customermanagement;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/customermanagement/SuiteCustomerManagement.java $
$Id: SuiteCustomerManagement.java 17788 2016-03-31 09:10:32Z jsamuel $
 */

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;

import com.ingenico.base.TestBase;

public class SuiteCustomerManagement extends TestBase {
	
	/**
	 * Declaration of common variables
	 */
	public static String locPath;

	public final static String IPSEC="IP Sec",AXISSTANDARD="Standard",CUSTNAME="CUSTNAME",ZONENAME="ZONENAME";

	public String [] epChkBoxOptns={"cp_ckbx_id","gprs_ckbx_id","tms_ckbx_id","asstrking_ckbx_id"};


	//TODO**Framework Related Functions**

	/** 
	 * Initializes suite execution	 
	 */
	@BeforeSuite(alwaysRun=true)
	void initSetUp()  {
		initialize();
	}	


	/**
	 * Method to create Customer
	 * @param custname
	 * @param sapname
	 */
	public void createCustomer(String custname,String sapname){
		selUtils.getObject("addnewcus_link").click();
		selUtils.selectItem(selUtils.getObject("entity_id"), testDataOR.get("entity"));
		selUtils.populateInputBox("custname_id", custname);
		selUtils.populateInputBox("sapcode_id", sapname);
		selUtils.selectItem(selUtils.getObject("ipconecty_id"), IPSEC);
		selUtils.clickOnWebElement(selUtils.getObject("axis_chkbox_id"));
		selUtils.selectItem(selUtils.getObject("axis_Selc_id"), AXISSTANDARD);
		selectMultipleVals("notassaxislocs_id", testDataOR.get("axis_locations"));
		selUtils.getObject("assaxislocs_id").click();
		//chkboxsubscription("epymnt_chkbox_id", "epaymnt_Selc_id","epayment_location");
		//selUtils.getObject("gprs_chkbox_id").click();
		logger.info("Before clicking gprs check box");
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObject("gprs_chkbox_id"));
		logger.info("After clicking gprs check box");
		chkboxsubscription("ingestate_chkbox_id", "ingestate_Selc_id", "ingestate_location");
		selUtils.getCommonObject("savebttn_xpath").click();
		if(selUtils.getCommonObject("posheder_errmsg_id").getAttribute("class").endsWith("errorMessage"))
		{
			Assert.fail("Problem with customer creation");	
			logger.info("Problem with customer creation");
		}				
		logger.info("Customer with name '"+custname+"' successfully added");

	}

	/**
	 * Method to check the check boxes and select from drop down
	 * @param chkboxLoc
	 * @param selcLoc	
	 * @param Val
	 */
	public void chkboxsubscription(String chkboxLoc,String selcLoc,String val){
		//selUtils.getObject(chkboxLoc).click();
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObject(chkboxLoc));
		logger.info("Clicked on IngEstate checkbox");
		selUtils.selectItem(selUtils.getObject(selcLoc), testDataOR.get(val));
	}

	/**
	 * Method to verify Eportal Page of the customer
	 * @param custname
	 * @param lang
	 * @param yamlval1
	 */
	public void vEportalPage(String custname,String lang,String [] yamlval1){
		selUtils.getCommonObject("eportal_tab_link").click();
		selUtils.selectItem(selUtils.getCommonObject("cust_Sel_id"), custname);
		selUtils.selectItem(selUtils.getObject("avail_lang_id"), lang);
		selUtils.getObject("selctionerbttn_xpath").click();
		selUtils.slctChkBoxOrRadio(selUtils.getObject("user_emailckbx_id"));
		selChkOrRadiobttnsCommon(yamlval1,epChkBoxOptns);
		selUtils.getCommonObject("savebttn_xpath").click();
		Assert.assertTrue(selUtils.getCommonObject("msg_xpath").getText().equals("Customer "+custname+" successfully updated"),custname+" not updated successfully");
		logger.info("Verified message about the "+custname+" successfully updated");
	}

	/**
	 * Creating zone levels based on Chosen Structure Depth
	 * @param zoneNames
	 * @param currency
	 * @param zoneLevel
	 * @author Hariprasad.KS
	 */
	public void addZoneLevels(String[] zoneNames,String currency,int zoneLevel)
	{
		selUtils.selectItem(selUtils.getObject("structuredepth_id"), Integer.toString(zoneLevel));
		selUtils.clickOnWebElement(selUtils.getObject("addzone_xpath"));
		waitMethods.waitForWebElementPresent(selUtils.getObject("orgstructform_id"));
		selUtils.getObject("orgstructlevelname_id").sendKeys(zoneNames[0]);
		selUtils.selectItem(selUtils.getObject("orgstructcurrency_id"), currency);
		selUtils.clickOnWebElement(selUtils.getObject("orgstructok_id"));
		logger.info("Zone added is "+zoneNames[0]);
		if(zoneLevel==2||zoneLevel==3)
		{
			addlevel2N3Zone(zoneNames[0], zoneNames[1]);
		}
		if(zoneLevel==3)
		{
			locPath=getPath("addzoneexpander_xpath").replace(ZONENAME, zoneNames[0]);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObjectDirect(By.xpath(locPath)));
			//selUtils.clickOnWebElement(selUtils.getObjectDirect(By.xpath(locPath)));
			addlevel2N3Zone(zoneNames[1], zoneNames[2]);
		}
	}

	/**
	 * Creating a level2 and level3 zones
	 * @param zone1
	 * @param zone2
	 * @author Hariprasad.KS
	 */
	public void addlevel2N3Zone(String zone1,String zone2)
	{
		locPath=getPath("addzonetable_xpath").replace(ZONENAME, zone1);
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObjectDirect(By.xpath(locPath)));
		//selUtils.clickOnWebElement(selUtils.getObjectDirect(By.xpath(locPath)));
		selUtils.getObject("orgstructlevelname_id").sendKeys(zone2);
		selUtils.clickOnWebElement(selUtils.getObject("orgstructok_id"));
		logger.info("Zone added is "+zone2);
	}

	/**
	 * validate the zones added
	 * @param zoneLevel
	 * @param zoneNames
	 * @author Hariprasad.KS
	 */
	public void vAddedZones(int zoneLevel,String[] zoneNames)
	{
		for(int i=0;i<zoneLevel;i++)
		{
			locPath=getPath("zoneintable_xpath").replace(ZONENAME, zoneNames[i]);
			waitMethods.waitForWebElementPresent(selUtils.getObjectDirect(By.xpath(locPath)));
			Assert.assertTrue(selUtils.isElementPresentxpath(locPath));
			logger.info("Validated zone name "+zoneNames[i]);
			if(i!=zoneLevel-1)
			{
				locPath=getPath("addzoneexpander_xpath").replace(ZONENAME, zoneNames[i]);
				waitNSec(1); 
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObjectDirect(By.xpath(locPath)));
				//selUtils.clickOnWebElement(selUtils.getObjectDirect(By.xpath(locPath)));
			}
		}
	}
//	/**
//	 * verify column value presence and click on customer name
//	 * @param custName
//	 */
//	public void clkCustNameList(String custName)
//	{
//		colIndex=selUtils.getIndexForColHeader("colheaders_css", NAMECOL);
//		verifyLvlColLvlValPresence("entitytablelst_css", colIndex, custName);
//		clkOnDirectObj("customer_link","CustName",custName);
//		logger.info("Clicked on "+custName+ " customer");
//	}


}