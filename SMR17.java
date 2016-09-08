package com.ingenico.testsuite.tmsmanagement;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/tmsmanagement/SMR17.java $
$Id: SMR17.java 16708 2016-01-20 09:53:29Z rjadhav $
 */

import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;
/**
 * SMR-17:Create IngEstate Location
 * @author Nagaveni.Guttula
 *
 */
public class SMR17 extends SuiteTmsManagement{
    //IngEstate Fields
	final private String[] ingLocPopUpFields={"inglocationname_id","locationipadrss_id",
	"inglocipport_id","ingloclogin_id","inglocpswd_id","inglocconfrmpswd_id","inglocweburl_id"},
	 dbColNames={"ip","port","ingestate_name","login","webstart_link"};

	/**
	 * Create an Ing Loc from Everest and other components.
	 * @param browser
	 */
	@Test(groups="SMR17")
	public void smr17() {

		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR17 execution started");
			ingEstName=testDataOR.get("ingestate_location");
			final String[] ingLocPopUpVals={ingEstName,testDataOR.get("ingestate_ip"),testDataOR.get("ingestate_port"),
					testDataOR.get("ingestate_login"),testDataOR.get("ingestate_password"),testDataOR.get("ingestate_password"),
					testDataOR.get("ingestate_webstart_url")},dbColVals={testDataOR.get("ingestate_ip"),testDataOR.get("ingestate_port"),ingEstName,
					testDataOR.get("ingestate_login"),testDataOR.get("ingestate_webstart_url")};
			final String dbTam=testDataOR.get("databaseTAM");
			String dbcolname;
			
			
			//Access Everest with a superuser
			//Navigate to 'TMS' and 'IngEstate Location' sub menu			 
			logger.info("Step 1, 2:");
			navigateToSubPage(TMSINGESTLOC,selUtils.getCommonObject("tms_tab_xpath"),selUtils.getCommonObject("tmsingestlocation_xpath"));

			//Create IngEstate location and verify it is added successfully 		
			logger.info("Step 3:");
			verifyExistingData("ingest_deleteimg_xpath","colheaders_css","colallrows_xpath",ingEstName, NAMECOL);
			//clickOnWebElement(getObject("tmsingest_plsicon_xpath"));
			logger.info(" Before Clicked on Plus button");
			selUtils.clickOnWebElement(selUtils.getCommonObject("plusbtn_xpath"));
			logger.info(" After Clicked on Plus button");
			setValsForPopUpWin(selUtils.getCommonObject("popup_wintitle_id"),INGESTATE,ingLocPopUpFields,ingLocPopUpVals);
			selUtils.selectItem(selUtils.getObject("inglocprotocol_id"), RMIPROTOCOL);
			logger.info(" Before Clicked on ok button");
			//selUtils.clickOnWebElement(selUtils.getCommonObject("okbttn_xpath"));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getCommonObject("okbttn_xpath"));
			logger.info(" After Clicked on ok button");
			logger.info("IngEstate location with name '"+ingEstName+"' successfully added");
			if(selUtils.getCommonObject("posheder_errmsg_id").getAttribute("class").endsWith("errorMessage"))
			{
				Assert.fail("IngEstate location was not created due to error");	
				logger.info("IngEstate location was not created due to error");
			}	
			colIndex=selUtils.getIndexForColHeader("colheaders_css", NAMECOL);
			verifyLvlColLvlValPresence("entitytablelst_css",colIndex,ingEstName);				

			if(dbCheck){
				//Verify created location correctly updated in database
				sqlQuery="SELECT ip,port,ingestate_name,login,webstart_link FROM ingenico_ingestate_configuration WHERE ingestate_name='"+ingEstName+"';";
				resSet = dbMethods.getDataBaseVal(dbTam,sqlQuery,CommonConstants.ONEMIN);			
				for(int cnti=0;cnti<dbColNames.length;cnti++){
					dbcolname=resSet.getString(dbColNames[cnti]);
					Assert.assertTrue(dbcolname.equals(dbColVals[cnti]), dbColVals[cnti]+" value is not matched in the database");
				}			

				logger.info("Verified all the values are correctly inserted into the 'ingenico_ingestate_configuration' database");
			}

			logger.info("SMR17 executed successfully");	
		}
		catch (Throwable t) {
			handleException(t);
		}
	}

}


