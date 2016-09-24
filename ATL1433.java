package com.ingenico.atlas.testsuite.tms;

import java.io.IOException;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.ingenico.common.CommonConstants;

public class ATL1433 extends TestSuiteBase {
	private boolean isTestPass = true;
	private String error = null, fileName, imgPath;

	@BeforeClass
	public void loginSetUp()
	{
		try{			
			login(CONFIG.getProperty("internalautouser1"),CONFIG.getProperty("internaluserPassword1"));			
			Assert.assertTrue(getCommonObject("login_name_id").getText().contains(CONFIG.getProperty("internalautouser1")),"login failed");
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
	 *ATL-1433:FRTMi05-M	UC#TMi-1 - Create TMS host - Positive Flow - Standard 
	 * 
	 * @throws IOException
	 */
	@Test
	public void atl1433()
	{		
		try
		{
			logger.info(" atl1433 execution started");			

			/*Step:1Navigate to Self Provisioning  -> TMS 	Verify for filters and contents on the left side.
			The following filter criteria should be displayed.The Filters will be:*/
			multiCustNavToTMS(multiCust, tmsMod);
			wait5Sec();
			wait2Sec();
			verifyEltIsDisplayed("name_editbox_id", "Name filter");
			verifyEltIsDisplayed("address_editbox_id", "address filter");
			
			/*Step:2Verify  width, height and spacing  of the filters.
			 * Should be  displayed as per eportal ergonomics.Note - to be updated later*/
			Assert.assertTrue(getObject("name_editbox_id").getCssValue("height").contains("18px"));
			Assert.assertTrue(getObject("address_editbox_id").getCssValue("height").contains("18px"));
			Assert.assertTrue(getObject("name_editbox_id").getCssValue("width").contains("143px"));
			Assert.assertTrue(getObject("address_editbox_id").getCssValue("width").contains("143px"));
			logger.info("verified width ,height and spacing of the filter");

			/*Step:3Verify the color of  'Search' and 'Reset filters' buttons
			Should be same as per eportal erogonomic.*/

			/*Step:4Check sorting on column headers.
			Arrow sign should be displayed for - Sort Ascending and Sort Descending.*/
			//getObject("name_columnselec_xpath").click();
			//getObject("managecolbutton_id").click();
			/*Thread.sleep(CommonConstants.sevenSec);
			webelement=getObject("name_columnselec_xpath");
			act=new Actions(driver);
			act.moveToElement(webelement).build().perform();
			Thread.sleep(CommonConstants.sevenSec);
			verifyEltIsDisplayed("ascending_down_arrow_xpath", "ascending arrow");*/

			/*Step:5Click on '+' filter button and then verify
			All contents for search should display.*/

			/*Step:6Click on '-' filter button and then verify
			Al contents should be collapsed.*/
			verifyEltIsDisplayedCommon("plus_min_xpath", "Plus icon");
			verifyEltIsDisplayedCommon("see_also_xpath", "See also");
			getCommonObject("plus_min_xpath").click();
			verifyEltIsDisplayedCommon("card_payment_xpath", "Card payment ");
			verifyEltIsDisplayedCommon("tms_lik_xpath", "Tms link ");
			verifyEltIsDisplayedCommon("customer_xpath", "Customer link");
			logger.info("checked for collapse and expand of Filters and See Also panel by clicking on '-' and '+' beside it");

			/*Step:7Verify for the buttons in the filters section
			Two button should be displayed -
			1) Search
			2) Reset Filters*/
			verifyEltIsDisplayed("search_bttn_id", "Search Button");
			verifyEltIsDisplayed("resetfilter_bttn_id", "Search Button");
			logger.info("verified Search and Reset Filter bttns is displayed");
			logger.info("Atl1433 execution successful");
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

}
