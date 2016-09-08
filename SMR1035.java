package com.ingenico.testsuite.gprs;

/*
 $HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SMR1035.java $
 $Id: SMR1035.java 17094 2016-02-16 11:36:45Z haripraks $
 */

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * SMR-1035: Allocate GGSN IP ranges for a 
 * customer and set the “Backup option”
 * field in the “Data Per Provider” section
 * 
 * @author Raghunath.K
 *
 */
public class SMR1035 extends SuiteGprs {

	/**
	 * Allocate GGSN IP ranges for a customer
	 * @param browser
	 * 
	 */
	HashMap<Integer, String> ggsn1,ggsn2;
	
	@Test(groups = "SMR1035")
	public void smr1035() {
		try {
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			final String customerName = testDataOR.get("customer"), numberOfSim = testDataOR.get("number_of_sim"),
					providerName = testDataOR.get("provider");
			int rowCount,noofRows=0;
			boolean ipStatT = true;
			boolean ipStatF = false;
			String freeIpTotal=null,custPathData,custPathlink;
			List<WebElement> rowele;
			//HashMap<Integer, String> GGSN1=null,GGSN2=null;

			logger.info("SMR1035 execution started");
			// Access Everest with the Everest Superuser
			// Go to 'GPRS' tab and then 'GPRS Management' sub-tab
			logger.info("Step 1, 2 :");
			navigateToSubPage(GPRSMGMT,selUtils.getCommonObject("gprs_tab_xpath"),selUtils.getCommonObject("gprsmngmtsubpage_xpath"));
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"),customerName);
			logger.info("Selected the customer " + customerName);

			// Click on the 'Affectation' button in the 'GGSN1 IP range'
			logger.info("Step 3 :");	

			rowCount=selUtils.getObjects("addiprangestbl__xpath").size(); 
			if(rowCount==2){
				logger.info("IP ranges for "+customerName+" is Not allocated");
			}	
			else
			{
				Assert.fail("IP ranges for "+customerName+" is allocated due to error");
			}
			selUtils.clickOnWebElement(selUtils.getObject("affectation_btn_xpath"));
			logger.info(" Clicked on affectation button ");

			// In IP Range Affectation Set 'Number of SIM' and selcet provider
			logger.info("Step 4 :");

			addIPRangeAfftion(numberOfSim,providerName);		 
			vTextIpAlertMsg();

			// IP Range Affectatation Free Ip Total
			freeIpTotal=selUtils.getObject("freeiptotal_xpath").getText();

			//verify Free IP column is greater or equal to numberofsim
			if(Integer.parseInt(freeIpTotal) >= Integer.parseInt(numberOfSim)){
				rowele=selUtils.getObjects("ggsnipranges_xpath");
				noofRows=rowele.size();

				// Read GGSN1,GGSN2 
				ipRanges(providerName);
			}
			else
			{
				Assert.fail("Free IP total is not greater then Number of Sim");
			}

			// Go to 'GPRS' tab and then 'IP Management' sub-tab
			logger.info("Step 5 :");
			navigateToSubPage(IPMGMT,selUtils.getCommonObject("gprs_tab_xpath"),selUtils.getCommonObject("ipmanagement_xpath"));
			selUtils.clickOnWebElement(selUtils.getObject("iprangelist_xpath"));
			logger.info("Clicked on IP Range button");
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));

			// Verify Customer Name column must be set to customer
			// IP ranges data are neither editable nor removable
			for(int row=0;row<noofRows;row++)
			{
				custPathData=getPath("ipranges_cust_xpath").replace("IP1", ggsn1.get(row).toString()).replace("IP2", ggsn2.get(row).toString()).replace("CUSTOMER",customerName);
				vIPRowDataInTable(custPathData,ipStatT,customerName);

				custPathlink=getPath("ipranges_edtrmv_xpath").replace("IP1", ggsn1.get(row).toString());
				vIPRowDataInTable(custPathlink,ipStatF,customerName);
			}

			// Go to 'GPRS' tab and then 'GPRS Management' sub-tab
			logger.info("Step 6 :");
			navigateToSubPage(GPRSMGMT,selUtils.getCommonObject("gprs_tab_xpath"),selUtils.getCommonObject("gprsmngmtsubpage_xpath"));
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"),customerName);
			logger.info("Selected the customer " + customerName);

			//Go to the 'Data Per Provider' section and click on apply button
			logger.info("Step 7 :");
			backupApplyClick(providerName);
			logger.info("SMR1035 is successfully executed");	

		} catch (Throwable t) {
			handleException(t);
		}

	}





	/**
	 * Add  IP Range Affection
	 * @param numOfSim
	 * @param provider
	 */	
	private void addIPRangeAfftion(String numOfSim,String provider )
	{
		selUtils.populateInputBox("number_of_sim_id", numOfSim);
		logger.info("Number of sims: " + numOfSim);
		selUtils.selectItem(selUtils.getObject("sel_provider_ggsn_id"),	provider);
		selUtils.clickOnWebElement(selUtils.getObject("add_ggsn_iprange_id"));
		logger.info("Clicked on add ggsn ip range button");
	}

	/**
	 * IP ranges allocate for GSSN1,GSSN2
	 * IP ranges for provider should be allocated
	 * @param GGSN1
	 * @param GGSN2
	 * @param provider
	 */

	private void ipRanges(String provider)
	{
		ggsn1 = new HashMap<Integer, String>();
		ggsn2 = new HashMap<Integer, String>();

		int key=0;
		List<WebElement> rowele=selUtils.getObjects("ggsnipranges_xpath");
		for(int row=0;row<rowele.size();row++)
		{	
			if(key<=rowele.size())
			{					
				String ipRangeRow = null;
				int col = 0;
				while (col <= 1) {
					ipRangeRow = rowele.get(row).getText().trim();
					String iprow[] = ipRangeRow.split("  ");
					if (col == 0) {
						ggsn1.put(key, iprow[col].trim());
					} else {
						ggsn2.put(key, iprow[col].trim());
					}
					col++;
				}
			}
			colIndex=selUtils.getIndexForColHeader("accessipcolheder_css",PVDRNAMECOL);
			verifyExpValinCol("tbl_access_iprange_id",colIndex,provider);
			key++;

		}
	}

	/**
	 * Select provider,Check the 'Backup Option' and then apply
	 * @param provider
	 */

	private void backupApplyClick(String provider)
	{
		String strErrorMsg;
		selUtils.selectItem(selUtils.getCommonObject("sel_provider_id"),provider);
		logger.info("Selected provider is:"+provider);
		selUtils.getObject("backupoption_id").click();
		logger.info("Backup Option has checked");
		selUtils.clickOnWebElement(selUtils.getObject("apply_btn_xpath"));
		if(selUtils.getCommonObject("diverroraccess_id").isDisplayed())
		{
			strErrorMsg=selUtils.getCommonObject("diverroraccess_id").getText().trim();
			Assert.fail(strErrorMsg+" due to fail");	
		}
		logger.info("Sucessfully clicked on apply button");
	}

	/**
	 * Verify Customer Name column must be set to customer
	 * IP ranges data are neither editable nor removable
	 * @param locPath
	 * @param colIndex
	 * @param actualCellData
	 */
	private void vIPRowDataInTable(String locPath,boolean value,String actualCellData)
	{
		int pageIter,pageNum = 0;
		String[] pageItems=getListItems(selUtils.getCommonObject("page_id"));
		if(pageItems.length>=maxNoPageCount){
			pageIter=maxNoPageCount;
		}
		else{
			pageIter=pageItems.length;
		}
		outerloop: for(int count=0;count<pageIter;count++){
			page=selUtils.getCommonObject("page_id");
			selUtils.selectItem(selUtils.getCommonObject("page_id"),pageItems[count]);
			waitMethods.waitForWebElementPresent(selUtils.getCommonObject("page_id"));
			if(value){
				if(selUtils.isElementPresentxpath(locPath))
				{
					assertEquals(actualCellData, selUtils.getObjectDirect(By.xpath(locPath)).getText());
					break outerloop;
				}
				else if(!value){
					if(selUtils.isElementPresentxpath(locPath)){
						Assert.fail("IP Range can be editable or removable");
					}
					else
					{
						logger.info("IP Range can't be editable or removable");
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
}