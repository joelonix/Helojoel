package com.ingenico.testsuite.gprs;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR1021.java $
$Id: SMR1021.java 18096 2016-04-18 08:46:42Z haripraks $
 */
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;

/**
 * SMR-1021:Create IP range
 * Summary
 *  Create an IP range for a provider
 * Preconditions
 * a <provider> must exist: as per testlink description changes
 * @author Hariprasad.KS
 *
 */


public class SMR1021 extends SuiteGprs{

	/**
	 * Create IP range
	 */
	@Test(groups="SMR1021")
	public void smr1021(){
		try {
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			String ggsn1,ggsn2,providerName;
			ggsn1=testDataOR.get("ggsn_1_ip_range");ggsn2=testDataOR.get("ggsn_2_ip_range");providerName=testDataOR.get("provider");
			logger.info("SMR1021 execution started");

			//Access Everest with a superuser
			//Go to "GPRS - IP Management"
			logger.info("Step 1, 2 :");
			navToSubPage("gprs_tab_xpath", "ipmanagement_xpath", IPMNGT);
			/*action.moveToElement(selUtils.getCommonObject("gprs_tab_xpath")).build().perform();
			((JavascriptExecutor) driver).executeScript(JSCLICK,selUtils.getCommonObject("ipmanagement_xpath"));*/
			logger.info("Navigated to GPRS IP Management");
			
			//Add IP ranges and validate added data with database and application
			logger.info("Step 3 :");
			addIPRange(ggsn1, ggsn2,providerName);
			Assert.assertTrue(driver.findElement(By.xpath(objR.getProperty("iptable_xpath"))).isDisplayed(),"IP Ranges are not created properly");
			logger.info("Added IP Ranges are "+ggsn1+ "And "+ggsn2);

			//Data validation with Application
			ipRange=ggsn1.split("\\.");
			ggsn1=ipRange[0].replaceFirst(REGEXP, "")+"."+ipRange[1].replaceFirst(REGEXP, "")+"."+ipRange[2].replaceFirst(REGEXP, "")+"."+ipRange[3].split("/")[0].replaceFirst(REGEXP, "")+"/"+ipRange[3].split("/")[1];
			ipRange=ggsn2.split("\\.");
			ggsn2=ipRange[0].replaceFirst(REGEXP, "")+"."+ipRange[1].replaceFirst(REGEXP, "")+"."+ipRange[2].replaceFirst(REGEXP, "")+"."+ipRange[3].split("/")[0].replaceFirst(REGEXP, "")+"/"+ipRange[3].split("/")[1];
			vAppIPRanges(ggsn1, ggsn2,providerName);
			logger.info("validated IP Ranges in application are "+ggsn1+ "And "+ggsn2);

			if(dbCheck){
				//Data validation in DB
				sqlQuery = "SELECT * FROM ip_adresses_range WHERE ip_adr_range_ggsn1='"+ggsn1+"' AND ip_adr_range_ggsn2='"+ggsn2+"'";
				resSet = dbMethods.getDataBaseVal(testDataOR.get("databaseEportal"),sqlQuery,CommonConstants.ONEMIN);
				Assert.assertTrue(resSet.getString("ip_adr_range_ggsn1").equals(ggsn1)&&resSet.getString("ip_adr_range_ggsn2").equals(ggsn2),"IP Ranges Value is not present in the Table");
				logger.info("Respected Value present in the Table");
			}

			logger.info("SMR1021 is successfully executed");		
		}catch (Throwable t) {
			handleException(t);
		}

	}

	/**
	 * Add IP range
	 * @param ggsn1
	 * @param ggsn2
	 */
	private void addIPRange(String ggsn1,String ggsn2,String providerName)
	{
		String[] ggsn=null;
		ggsn=ggsn1.split("\\.");
		selUtils.clickOnNavPaneItems(selUtils.getObject("ipranges_xpath"),ATTRCLAS,VALCLOSE);
		selUtils.clickOnWebElement(selUtils.getObject("addiprange_link"));
		selUtils.selectItem(selUtils.getObject("provider_id"), providerName);
		selUtils.getObject("ggsn1ip1_id").sendKeys(ggsn[0]);
		selUtils.getObject("ggsn1ip2_id").sendKeys(ggsn[1]);
		selUtils.getObject("ggsn1ip3_id").sendKeys(ggsn[2]);
		selUtils.getObject("ggsn1ip4_id").sendKeys(ggsn[3].split("/")[0]);
		ggsn=ggsn2.split("\\.");
		selUtils.getObject("ggsn2ip1_id").sendKeys(ggsn[0]);
		selUtils.getObject("ggsn2ip2_id").sendKeys(ggsn[1]);
		selUtils.getObject("ggsn2ip3_id").sendKeys(ggsn[2]);
		selUtils.getObject("ggsn2ip4_id").sendKeys(ggsn[3].split("/")[0]);
		selUtils.getObject("ggsn_id").sendKeys(ggsn[3].split("/")[1]);
		selUtils.clickOnWebElement(selUtils.getObject("addiprange_id"));
		checkAlert();
		//Assert.assertFalse(isElementPresent("erroriprange_xpath"),"error while adding IP Ranges");
	}
	/**
	 * Validate IP address ranges with provider name in application
	 * @param ggsn1
	 * @param ggsn2
	 */
	private void vAppIPRanges(String ggsn1,String ggsn2,String providerName)
	{
		String locPath;
		int pageIter,pageNum = 0,loop=0;
		String[] headrName={USEDIPHEDR,CUSTNAMEHEDR,PROVIDHEDR},colValue={USEDIP,"",providerName},pageItems=getListItems(selUtils.getCommonObject("page_id"));
		locPath=getPath("iprangetable_xpath").replace("IP1", ggsn1).replace("IP2", ggsn2).replace("NAME", providerName);
		if(pageItems.length>=maxNoPageCount){
			pageIter=maxNoPageCount;
		}
		else{
			pageIter=pageItems.length;
		}
		outerloop: for(int count=0;count<pageIter;count++)
		{
			page=selUtils.getCommonObject("page_id");
			selUtils.selectItem(selUtils.getCommonObject("page_id"),pageItems[count]);
			waitMethods.waitForWebElementPresent(selUtils.getCommonObject("page_id"));
			if(selUtils.isElementPresentxpath(locPath))
			{
				for(String hName:headrName)
				{
					colIndex=selUtils.getIndexForColHeader("gprscolheader_css", hName);
					locPath=getPath("iprangetab_xpath").replace("IP1", ggsn1).replace("IP2", ggsn2).replace("COlNO", Integer.toString(++colIndex));
					assertEquals(colValue[loop++], selUtils.getObjectDirect(By.xpath(locPath)).getText());
				}
				break outerloop;
			}
			pageNum++;
		}
		if(pageNum==pageIter)
		{
			Assert.fail("Test is failed because of maximum of "+maxNoPageCount+" pages reached ");
		}
	}
}


