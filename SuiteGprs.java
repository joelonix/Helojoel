package com.ingenico.testsuite.gprs;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/gprs/SuiteGprs.java $
$Id: SuiteGprs.java 18136 2016-04-19 10:33:07Z jsamuel $
 */

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;

import com.ingenico.base.TestBase;
import com.ingenico.common.CommonConstants;

public class SuiteGprs extends TestBase {

	public String[] ipRange,simcards,simcardsus;
	public final static String REGEXP="^0+(?!$)",USEDIPHEDR="Used IP",CUSTNAMEHEDR="Customer Name",PROVIDHEDR="Provider Name",RATEPLANCOL="Rate Plan",
			RATESPLNWINTITLE="Rate Plans Allowed",SIMCRDDELIVERY="SIM Card Delivery",NOBILNGCYCLE="NO BILLING CYCLE",COLICCID="ICCID",SIMCARDS="SIM Cards",CREATEBATCH="Create Batch",
			SIMBTCHDTXT="SIM Batched increased to ",SIMNTBTCHDTXT="SIM Not Batched decreased to ",COUNTCOL="Count",BATCHNAME="Batch name",STATUS="Status",SIMSTATUS="SIM Status",IPMNGT="IP Management",
			OPERSTATUS="Operational Status",EXPALRTIPTEXT="The ip ranges are successfully allocated to the customer",SPACE="",ACTIVATE="Activate",ACTIVATED="Activated",UNDERACTIVATION="Under Activation",RADIUSREQEST="Radius request",UNDERSUSPENSION="Under Suspension",
			SUSPEND="Suspend",SUSPENDED="Suspended",UNSUSPEND="Unsuspend",UNDERREACTIVE="Under Reactivation",PREACTORASSOC="Pre-Activated/Associated",SIMFLEET="SIM Fleet",ASSOCIATE="Associate",ALLOCATE="Allocate",COLSIMID="SIM Id.",COLLVL3="Level 3",
			ACVTREADY="Activation ready",ACTVATE="Activate",SUSPEN="Suspen",ACTCONFIRM="Activation Confirmation",ADDTOPOOL="Add to pool",PROVREQ="Provider Request",NONE="None",SIMCARDSCOL="SIMCARDS",POOLNAME="Pool Name",PREACTNOTASS="Pre-Activated/Not Associated",
			ACTRQST="Activation Request",NORESULTTXT="No result to your search",REACTIVATE="Re-activate";

	String actalrrTxt;
	public int totNoSimCrdsYN,totNoSimCrdsN,totNoSimCrds;
	HashMap<Integer, String> simCardsAS,simCardsYN,simCardsN;


	//TODO**********************Framework Related Functions****************************************************

	/** 
	 * Initializes suite execution
	 */
	@BeforeSuite(alwaysRun=true)
	void initSetUp()  {
		initialize();
	}

	/**
	 * Add or edit rate plans
	 * @param modWinLoc
	 * @param bttnloc
	 * @param rates
	 */
	public void addOrEditRatePlan(String modWinLoc,String rateselloc,String bttnloc,String rates){
		if(getModWinDisp(selUtils.getObject(modWinLoc),RATESPLNWINTITLE))
		{
			//selUtils.selectItem(selUtils.getObject(rateselloc), rates);
			selUtils.selectValueInDropDown(selUtils.getObjects(rateselloc), rates);
			logger.info("Selected the rate plan "+ rates);
			selUtils.clickOnWebElement(selUtils.getObject(bttnloc));
			logger.info("Clicked on add/edit button");
		}
		else{
			Assert.fail(RATESPLNWINTITLE+"is not displayed");
			logger.info(RATESPLNWINTITLE+"is not displayed");
		}
	}

	/**
	 * Add or Edit rates
	 * @param addoreditLoc
	 * @param ratepln
	 */
	public void vAddEditRates(String addeditmodwinloc,String rateselloc,String addoreditLoc,String ratepln){

		addOrEditRatePlan(addeditmodwinloc,rateselloc,addoreditLoc,ratepln);
		selUtils.clickOnWebElement(selUtils.getObject("apply_bttn_id"));
		colIndex=selUtils.getIndexForColHeader("rateplancolheder_css", RATEPLANCOL);
		vRatePlans("rateplnslists_xpath", colIndex, ratepln.trim());
	}

	/**
	 * Method to verify diff in sim cards,order of passing beforbatch and after
	 * -batch args are interchaged and passed based on whether to verify decrea
	 * -se or increase in the no of simcards,to avoid negative diff
	 * @param beforebatch
	 * @param afterbatch
	 * @param noofcards
	 * @param incordectext
	 */
	public void vSimCardDiff(int beforebatch,int afterbatch,int noofcards,String incordectext){
		int simCardDiff=beforebatch-afterbatch;


		if(simCardDiff==noofcards)
		{

			logger.info(incordectext +noofcards);

		}else
		{

			Assert.fail("Miss match in SIM Cards");

		}
	}

	/**
	 * Method for checking the Alert presence
	 */
	public void vTextIpAlertMsg() {
		try {
			wait.until(ExpectedConditions.alertIsPresent());
			Alert alert = driver.switchTo().alert();
			actalrrTxt=alert.getText();
			alert.accept();
			Assert.assertEquals(actalrrTxt,EXPALRTIPTEXT);
			logger.info(EXPALRTIPTEXT);

		} catch (Exception e) {
			Assert.fail("Failed due to "+ actalrrTxt);
		}
	}

	/**
	 * validate records related to sim inserted in table
	 * @param simCards
	 * @param tableName
	 * @author Hariprasad.KS
	 */
	public void vRadiusDatabase(String[] simCards,String tableName)
	{
		try{
			for (int loop =0 ;loop< simCards.length;loop++) 
			{
				sqlQuery = "SELECT * FROM "+tableName+" where username LIKE'%"+simCards[loop]+"%'";
				resSet = dbMethods.getDataBaseVal(testDataOR.get("databaseRadius"),sqlQuery,CommonConstants.ONEMIN);
				Assert.assertTrue(resSet.getString("username").equals("GGSN022_"+simCards[loop]),"Result set data is not matching with GGSN022_");
				resSet.next();
				Assert.assertTrue(resSet.getString("username").equals("GGSN021_"+simCards[loop]),"Result set data is not matching with GGSN021_");
				logger.info("Records related to sim card"+"GGSN022_"+simCards[loop]+" "+"GGSN021_"+simCards[loop]+" are inserted");
			}
		}catch(Exception e){
			Assert.fail("Failed while validating radius database table values");
		}
	}
	/**
	 * Open SSH Connection and execute command to change the status of the SIM 
	 * @param host
	 * @param user
	 * @param password
	 * @param command
	 * @author Hariprasad.KS
	 */
	/*public void sshCommandExecuter(String host,String user,String password,String command)
	{
		try{

			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			Session session=jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig(config);
			session.connect();
			logger.info("SSH Connection opened");
			Channel channel=session.openChannel("exec");
			((ChannelExec)channel).setCommand(command);
			channel.setInputStream(null);
			((ChannelExec)channel).setOutputStream(System.err);
			InputStream in=channel.getInputStream();
			channel.connect();
			byte[] tmp=new byte[1024];
			while(true){
				while(in.available()>0){
					int i=in.read(tmp, 0, 1024);
					if(i<0)break;
					logger.info(new String(tmp, 0, i));
				}
				if(channel.isEOF()){
					logger.info("exit-status: "+channel.getExitStatus());
					break;
				}
				try{Thread.sleep(1000);}catch(Exception ee){}
			}
			channel.disconnect();
			session.disconnect();
			logger.info("SSH Connection closed");
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/

	/**
	 * wait for maximum of 2 minutes to replicate the sim status change
	 * @param xpath
	 * @param simcard
	 * @param colIndex
	 * @param status
	 * @author Hariprasad.KS
	 */
	public void waitForRadiusrepli(boolean testData,String xpath,String simcard,int colIndex,String status)  {  

		try {
			logger.info("Radious Replicaiton has Started");
			webElement=selUtils.getObjectDirect(By.xpath(xpath.replace("ICCID", simcard).replace("INDEX", Integer.toString(colIndex+1))));
			for(iter=0;iter<120;iter++)
			{ 
				try{
					if(testData){

						/* selUtils.clickOnWebElement(selUtils.getObject("pictoburgerclk_xpath"));
						selUtils.scrollToView(selUtils.getObject("gprssubmit_id"));
						selUtils.clickOnWebElement(selUtils.getObject("gprssubmit_id"));*/	

						((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObject("simdetailstatus_id"));
						webElement=selUtils.getObjectDirect(By.xpath(xpath.replace("ICCID", simcard).replace("INDEX", Integer.toString(colIndex))));
					}else{
						selUtils.clickOnWebElement(selUtils.getObject("simcardstab_xpath"));
						selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
						webElement=selUtils.getObjectDirect(By.xpath(xpath.replace("ICCID", simcard).replace("INDEX", Integer.toString(colIndex+1))));	
					}
					if(webElement.getText().trim().equalsIgnoreCase(status))   {
						logger.info("Radious Replicaiton has "+status+" Updated");
						break;
					} else {
						waitNSec(1);
					}
				} catch(Exception e){
					waitNSec(1);
				}
			}
			if(iter>=120)
			{
				Assert.fail("maximum of 2 minutes waited to replicate the sim status to change to "+status);				
			}
		} catch (Exception e)
		{		
			Assert.fail("Problem while replicating the sim status to change"+e.getMessage());
		}
	}

	/**
	 * Navigate GPRS Management page and click sim card tab
	 * @param custName
	 */
	public void navGPRSMgmtClkSIM(String custName)
	{
		navigateToSubPage(GPRSMNGMNT,selUtils.getCommonObject("gprs_tab_xpath"),selUtils.getCommonObject("gprsmngmtsubpage_xpath"));
		logger.info("Navigated to "+GPRSMNGMNT+" tab");
		selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), custName);
		logger.info("Selected customer "+custName);

		//Go to sim cards tab
		selUtils.clickOnWebElement(selUtils.getObject("simcardstab_xpath"));
		logger.info("Clicked on SIM Cards tab");
	}

	/**
	 * Navigate to eportal gprs-snapshot and validate sim status
	 * @param custName
	 * @param simCards
	 * @param locator
	 * @param simStatus
	 */
	public void navGPRSPageNval(String[] simCards,String locator,String simStatus)
	{
		//navigateToSubPage(SNAPSHOT,selUtils.getCommonObject("gprs_tab_xpath"),selUtils.getCommonObject("gprssnapshot_xpath"));
		navToSubPage("gprs_tab_xpath","gprssnapshot_xpath",SNAPSHOT);
		logger.info("Navigated to Snapshot tab");
		selUtils.clickOnWebElement(selUtils.getObject(locator));
		logger.info("Clicked on "+simStatus+" link");
		waitNSec(1);
		selUtils.scrollToView(selUtils.getCommonObject("simfleetdetailtable_xpath"));
		colIndex=selUtils.getIndexForColHeader("gprseportalhedr_css", STATUS);
		for (iter =0 ;iter< simCards.length;iter++) 
		{
			xpath = TestBase.getPath("simfleettab_xpath").replace("ICCID",simCards[iter]);
			webElement=selUtils.getObjectDirect(By.xpath(xpath.replace("INDEX", Integer.toString(colIndex+1))));
			selUtils.verifyTextEqualsWith(webElement, simStatus);
		}
	}

	/**
	 * validate SIM status
	 * @param simCards
	 * @param provider
	 * @param simStatus
	 */
	public void valSIMStaus(String[] simCards,String provider,String simStatus)
	{
		selUtils.clickOnWebElement(selUtils.getObject("simcardstab_xpath"));
		logger.info("Clicked on SIM Cards tab to refresh");
		colIndex=selUtils.getIndexForColHeader("gprscolheader_css", SIMSTATUS);
		xpath = TestBase.getPath("simcardsdata_xpath").replace(NAME,provider);
		waitForRadiusrepli(false,xpath, simCards[0], colIndex, simStatus);
		selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
		for (iter =0 ;iter< simCards.length;iter++) 
		{
			webElement=selUtils.getObjectDirect(By.xpath(xpath.replace("ICCID", simCards[iter]).replace("INDEX", Integer.toString(colIndex+1))));
			selUtils.verifyTextEqualsWith(webElement, simStatus);
		}
	}

	/**
	 * validate SIM status
	 * @param simCards
	 * @param provider
	 * @param simStatus
	 */
	public void valSIMStaus(HashMap<Integer, String> simCards,String simStatus,String colName)
	{
		int lop;
		selUtils.clickOnWebElement(selUtils.getObject("simcardstab_xpath"));
		logger.info("Clicked on SIM Cards tab to refresh");
		colIndex=selUtils.getIndexForColHeader("gprscolheader_css", colName);
		xpath = TestBase.getPath("simcardsdata_xpath");
		for (lop =0 ;lop< simCards.size();lop++) 
		{
			waitForRadiusrepli(false,xpath, simCards.get(lop), colIndex, simStatus);
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			webElement=selUtils.getObjectDirect(By.xpath(xpath.replace("ICCID", simCards.get(lop)).replace("INDEX", Integer.toString(colIndex+1))));
			selUtils.verifyTextEqualsWith(webElement, simStatus);
			logger.info("Simcard "+simCards.get(lop)+" status has changed to "+simStatus);
		}
	}



	/**
	 * validate simcard status and operational status
	 * @param simCards
	 * @param provider
	 * @param simStatus
	 */
	public void vSIMNOperStatus(String[] simCards,String provider,String simStatus)
	{
		selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
		colIndex=selUtils.getIndexForColHeader("gprscolheader_css", SIMSTATUS);
		colIndxAnother=selUtils.getIndexForColHeader("gprscolheader_css", OPERSTATUS);
		xpath = TestBase.getPath("simcardsdata_xpath").replace(NAME,provider);
		for (iter =0 ;iter< simCards.length;iter++) 
		{
			webElement=selUtils.getObjectDirect(By.xpath(xpath.replace("ICCID", simCards[iter]).replace("INDEX", Integer.toString(colIndex+1))));
			selUtils.verifyTextEqualsWith(webElement, simStatus);
			webElement=selUtils.getObjectDirect(By.xpath(xpath.replace("ICCID", simCards[iter]).replace("INDEX", Integer.toString(colIndxAnother+1))));
			selUtils.verifyTextEqualsWith(webElement, RADIUSREQEST);
		}
	}

	/**
	 * select simcard check box for changing sim status
	 * @param simCards
	 * @param provider
	 */
	public void selChkBoxofSIMs(String[] simCards)
	{
		xpath = TestBase.getPath("simcardschk_xpath");
		for (iter =0 ;iter< simCards.length;iter++) 
		{
			webElement=selUtils.getObjectDirect(By.xpath(xpath.replace("ICCID", simCards[iter])));
			waitMethods.waitForWebElementPresent(webElement);
			selUtils.slctChkBoxOrRadio(webElement);
		}
		selUtils.clickOnWebElement(selUtils.getObject("navpanesubmit_xpath"));
	}



	/**
	 * Method to verify rate plans
	 * @param locator2
	 * @param index
	 * @param rateplan
	 */
	public void vRatePlans(String locator2, int index, String rateplan)

	{
		String xpath = getPath(locator2).replace("INDEX", Integer.toString(index+1));
		List<WebElement> colValList = selUtils.getObjectsDirect(By.xpath(xpath));

		for (iter = 1; iter < colValList.size(); iter++) {
			if(colValList.get(iter).getText().trim().contains(rateplan)){
				Assert.assertTrue(colValList.get(iter).getText().trim().contains(rateplan)," '"+rateplan+"' is not present in the expected list");
				logger.info(" '"+rateplan+"' present in the expected list");
			}

		}

	}

	/**
	 * Method to get no of sim cards batched or not batched
	 * @param objloc
	 * @return
	 */
	public int simBfrAfrImp(String objloc)
	{
		String bchtxtbfrorafr=selUtils.getObject(objloc).getText().trim();
		bchtxtbfrorafr=bchtxtbfrorafr.substring(bchtxtbfrorafr.indexOf(":")+1, bchtxtbfrorafr.indexOf("Cards")).trim();

		int totbfrafr=Integer.parseInt(bchtxtbfrorafr);

		return totbfrafr;

	}

	/**
	 *Javascript webelement click
	 * @param webelement
	 *
	 */
	public void jsClick(WebElement webelement)
	{
		waitMethods.waitForWebElementPresent(webelement);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();", webelement);
		/*Actions action = new Actions(driver);
		action.moveToElement(webelement).click().build().perform();*/
	}
	/**
	 * 
	 * @param simcr
	 */
	public void vSimCards(String simcr[])
	{
		for (iter =0 ;iter< simcr.length;iter++) 
		{
			verifyLvlColLvlValPresence("entitytablelst_css",colIndex,simcr[iter].trim());
		}
	}


	/**
	 * Upload CSV file
	 */
	public void uploadCSVFile(String filePath,String selectType,String fileName){
		//final String importSIM = "import_sim.csv";
		//selUtils.clickOnWebElement(selUtils.getObject("impsim_id"));
		selUtils.clickOnNavPaneItems(selUtils.getObject("importsim_xpath"),ATTRCLAS,VALCLOSE);
		//selUtils.populateInputBox("imp_sim_browsebttn_xpath", filePath);
		selUtils.getObject("imp_sim_browsebttn_xpath").sendKeys(filePath);
		Assert.assertTrue(selUtils.getObject("imp_sim_browsebttn_xpath").getAttribute("value").contains(fileName), fileName + " is not loaded");
		selUtils.selectItem(selUtils.getObject("type_simimp_id"),selectType);
		logger.info(fileName + " is loaded");
		selUtils.clickOnWebElement(selUtils.getObject("imp_sim_loadbttn_xpath"));
	}

	public void waitForRadiusrepli(String locator,String batchName,String status)
	{
		try {
			for(iter=0;iter<120;iter++)
			{ 
				try{
					//selUtils.clickOnWebElement(selUtils.getObject("allbatches_link"));
					selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
					colIndex=selUtils.getIndexForColHeader("gprscolheader_css", BATCHNAME);
					verifyLvlColLvlValPresence("entitytablelst_css",colIndex,batchName);
					clkOnDirectObj("batchname_xpath", "BATCH", batchName);
					webElement=selUtils.getObjectDirect(By.xpath(locator));		

					if(webElement.getText().trim().equalsIgnoreCase(status))   { 
						break;
					} else {
						selUtils.clickOnWebElement(selUtils.getCommonObject("allbatches_link"));
						waitNSec(1);
					}
				} catch(Exception e){
					waitNSec(1);
				}
			}
			if(iter>=120)
			{
				Assert.fail("maximum of 2 minutes waited to replicate the sim status to change to "+status);				
			}
		} catch (Exception e)
		{		
			Assert.fail("Problem while replicating the sim status to change"+e.getMessage());
		}	
	}

	/**
	 *Select Action Sim and Select simcards
	 * @param webelement
	 *
	 */
	public void sActnSimNSimCds(String simStatus)
	{
		selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
		selUtils.clickOnNavPaneItems(selUtils.getObject("action_sim_xpath"),ATTRCLAS,VALCLOSE);
		selUtils.selectItem(selUtils.getObject("selaction_xpath"),simStatus);
	}

	/**
	 *Select Action Sim and Select simcards
	 * @param webelement
	 *
	 */
	public void vColValPresence(String colName,String vData)
	{
		colIndex=selUtils.getIndexForColHeader("gprscolheader_css", colName);
		selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
		verifyLvlColLvlValPresence("entitytablelst_css",colIndex,vData);
	}

	/**
	 *SimCards are stored into variables
	 * @param simCards
	 */	
	public HashMap<Integer, String> simcardsActNSus(String[] simCards,String colName,String simStatus)
	{
		int key=0;
		simCardsAS = new HashMap<Integer, String>();
		for(iter =0 ;iter< simCards.length;iter++)
		{
			selUtils.selectMaxSizeinTable(selUtils.getCommonObject("showresult_id"));
			colIndex=selUtils.getIndexForColHeader("gprscolheader_css", colName);
			xpath = TestBase.getPath("simcardstat_xpath");
			webElement=selUtils.getObjectDirect(By.xpath(xpath.replace("ICCID", simCards[iter]).replace("INDEX", Integer.toString(colIndex+1))));
			waitMethods.waitForWebElementPresent(webElement);
			if(webElement.getText().trim().contains(simStatus))
			{ 
				simCardsAS.put(key,simCards[iter]);
				key++;
			}			
		}
		if(simCardsAS.isEmpty())
			Assert.fail("Simcards of "+simStatus+" should not be zero");
		else
			logger.info("Simcards of " + simStatus+ " stored into simCardsAS ");
		return simCardsAS;
	}

	/**
	 *Group of simCards are stored into variables
	 * @param simCardsYN
	 * @param simCardsN
	 */	
	public void simCardsYNnN(String[] simCards,String simStatus)
	{
		int ksy = 0, ksn = 0;
		simCardsYN = new HashMap<Integer, String>();
		simCardsN = new HashMap<Integer, String>();
		for (iter = 0; iter < simCards.length; iter++) {
			colIndex = selUtils.getIndexForColHeader("gprscolheader_css",SIMSTATUS);
			xpath = TestBase.getPath("simcardstat_xpath");
			webElement = selUtils.getObjectDirect(By.xpath(xpath.replace("ICCID", simCards[iter]).replace("INDEX",Integer.toString(colIndex + 1))));

			if (webElement.getText().trim().contains(simStatus)) {
				simCardsYN.put(ksy, simCards[iter]);
				ksy++;
			} else if (webElement.getText().trim().contains(NONE)) {
				simCardsN.put(ksn, simCards[iter]);
				ksn++;
			} else {
				Assert.fail("Simcards are invalid status");
			}
		}

		//		if(!(simCardsYN.isEmpty()) | !(simCardsN.isEmpty()))
		//			logger.info("Simcards of " + simStatus+ " stored into simCardsYN AND/OR "+NONE+" stored into simCardsN");
		//		else
		//		Assert.fail("Simcards of sim_cards_yn should not be empty");

		if(simCardsYN.isEmpty() & simCardsN.isEmpty())
			Assert.fail("Simcards of sim_cards_yn should not be empty");
	}

	/**
	 * validate sim cards are listed in the table
	 * @param allSIMCards
	 * @param colIndex
	 */
	public void vSIMcardsListed(String[] allSIMCards,int colIndex)
	{
		for(String SIMCards:allSIMCards)
		{
			xpath=getPath("batchdetailstab_xpath").replace(COLICCID, SIMCards).replace("INDEX", Integer.toString(colIndex+1));
			selUtils.verifyTextEqualsWith(selUtils.getObjectDirect(By.xpath(xpath)), SIMCards);
		}
	}

	/**
	 * click on the hyperlinked number under the activated/Suspended column
	 * @param level
	 * @param col1
	 * @param col2
	 */
	public void clickOnHyperLink(String level,String col1,String col2){
		int indexOfLvlCol =selUtils.getIndexForColHeader("gprssimfleetdetail_css", col1);
		int indexOfActCol =selUtils.getIndexForColHeader("gprssimfleetdetail_css", col2);
		String colLv1xpath=getPath("gprslevel1col_xpath").replace("INDEX", Integer.toString(indexOfLvlCol+1));

		int rowIndex=selUtils.getIndexForRow(By.xpath(colLv1xpath), level);
		xpath=getPath("numberedlnk_xpath").replace("ROWINDEX", Integer.toString(rowIndex+1)).replace("COLINDEX", Integer.toString(indexOfActCol+1));
		if(selUtils.getObjectDirect(By.xpath(xpath)).findElement(By.tagName("a")) != null){
			selUtils.clickOnWebElement(selUtils.getObjectDirect(By.xpath(xpath)).findElement(By.tagName("a")));
		}
		else{
			Assert.fail("There is no presence of hyperlink");
		}
	}

	/**
	 * Method will get the rowno of the simcard
	 * @param locator
	 * @param index
	 * @param simcards
	 * @return
	 */
	public int getSimRowIndex(String locator, int index,String firstSim)

	{
		if(selUtils.isElementPresentCommon("page_id")){
			int pageNum=0,pageIter=0;
			String[] pageItems=getListItems(selUtils.getCommonObject("page_id"));
			if(pageItems.length>=maxNoPageCount){
				pageIter=maxNoPageCount;
			}
			else{
				pageIter=pageItems.length;
			}
			outerloop:for(count=0;count<pageIter;count++){
				page=selUtils.getCommonObject("page_id");
				selUtils.selectItem(selUtils.getCommonObject("page_id"),pageItems[count]);
				waitMethods.waitForWebElementPresent(selUtils.getCommonObject("page_id"));
				String xpath = getPath(locator).replace("INDEX", Integer.toString(index+1));
				List<WebElement> colValList = selUtils.getObjectsDirect(By.xpath(xpath));
				for (iter = 0; iter < colValList.size(); iter++) {
					if(colValList.get(iter).getText().equals(firstSim))
					{
						xpath=getPath("statusbttn_xpath").replace("SIMCARD",colValList.get(iter).getText());
						selUtils.clickOnWebElement(selUtils.getObjectDirect(By.xpath(xpath)));
						break outerloop;
					}

				}
				pageNum++;
			}
			if(pageNum==pageIter){
				Assert.fail("The expected data  was not present with in the numberof pages "+pageIter);
			}
		}

		return iter;
	}

	/**
	 * Method to verify sim not present
	 * @param locator2
	 * @param index
	 * @param simcards
	 */
	private boolean vSusOrActSIM(String locator, int index, String firstsim,String simstatus)

	{
		boolean val=false;
		String xpath = getPath(locator).replace("INDEX", Integer.toString(index+1));
		List<WebElement> colValList = selUtils.getObjectsDirect(By.xpath(xpath));

		for (iter = 0; iter < colValList.size(); iter++) 
		{
			if(colValList.get(iter).getText().equals(firstsim))
			{
				selUtils.scrollToView(selUtils.getCommonObject("simfleetdetailtable_xpath"));
				colIndex=selUtils.getIndexForColHeader("gprssimfleetdetail_css",STATUS);
				xpath=getPath("simsus_xpath").replace("SIMCARD",colValList.get(iter).getText()).replace("INDEX", Integer.toString(colIndex-2));
				webElement =selUtils.getObjectDirect(By.xpath(xpath));
				if(webElement.getText().trim().equals(simstatus)){
					logger.info("Radius Replicaiton has "+simstatus+" Updated");
					val=true;
					break;
				}
			}

		}
		if(iter>=colValList.size())
		{
			navigateToSubPage(SIMFLEET,selUtils.getCommonObject("gprs_tab_xpath"),selUtils.getCommonObject("simfleet_subpage_xpath"));
		}

		return val;

	}

	/**
	 * Method to suspend/Reactivate a simcard
	 * @param simCards
	 */
	public void susOrActivateSim(String firstSim,String simstatus)
	{

		colIndex=selUtils.getIndexForColHeader("gprseportalhedr_css", COLSIMID);
		int index=getSimRowIndex("simidcols_xpath",colIndex,firstSim);
		xpath=getPath("chstat_xpath").replace("INDEX", Integer.toString(index-1));
		selUtils.clickOnWebElement(selUtils.getObjectDirect(By.xpath(xpath)));
		selUtils.selectItem(selUtils.getObject("status_id"), simstatus);
		selUtils.clickOnWebElement(selUtils.getObject("statussub_bttn_xpath"));
		logger.info("Clicked on Submit button");
		selUtils.clickOnWebElement(selUtils.getObject("confirmsubmit_id"));
		selUtils.clickOnWebElement(selUtils.getObject("alertboxok_xpath"));
		logger.info(" Clicked on alert box ok button");
	}

	/**
	 * Method to verify sim status to be updated in status col as suspended/
	 * Activated
	 * @param lvzone
	 * @param simCrads
	 */
	public void vRadiusRepli(String firstsim,String simstatus)
	{
		for(int counter=0;counter<120;counter++)
		{
			boolean val=false;
			clickOnHyperLink(simstatus);
			String noRestext=selUtils.getObject("noresult_xpath").getText();
			if(noRestext.contains(NORESULTTXT))
			{
				navigateToSubPage(SIMFLEET,selUtils.getCommonObject("gprs_tab_xpath"),selUtils.getCommonObject("simfleet_subpage_xpath"));
			}
			else
			{
				colIndex=selUtils.getIndexForColHeader("gprssimfleetdetail_css",COLSIMID);
				val=vSusOrActSIM("simidcols_xpath",colIndex,firstsim,simstatus);
				if(val)
				{
					break;  
				}


			}
			if(counter>=120)
			{
				Assert.fail("maximum of 2 minutes waited to replicate the sim status to change to "+status);				
			}

		}
	}

	/**
	 * click on the hyperlinked number under the activated/Suspended column
	 * @param actcol
	 */
	public void clickOnHyperLink(String actcol){
		List<WebElement> links;
		int indexOfActCol =selUtils.getIndexForColHeader("gprssimfleetdetail_css", actcol);
		String colLv1xpath=getPath("gprslevel1col_xpath").replace("INDEX", Integer.toString(indexOfActCol+1));
		if(actcol.equals(SUSPENDED)){

			outerloop:for(count=0;count<120;count++)
			{
				links=selUtils.getObjectsDirect(By.xpath(colLv1xpath));
				for(iter=0;iter<links.size();iter++)
				{
					links=selUtils.getObjectsDirect(By.xpath(colLv1xpath));
					if(!links.get(iter).getText().equals("0"))
					{
						links.get(iter).click();
						break outerloop;
					}
					else
					{
						navigateToSubPage(SIMFLEET,selUtils.getCommonObject("gprs_tab_xpath"),selUtils.getCommonObject("simfleet_subpage_xpath"));
					}
				}
			}
		if(count>=120)
		{
			Assert.fail("maximum of 2 minutes waited to replicate the sim status to change to "+status);				
		}


		}
		else{
			links=selUtils.getObjectsDirect(By.xpath(colLv1xpath));
			for(iter=0;iter<links.size();iter++)
			{
				if(!links.get(iter).getText().equals("0"))
				{
					links.get(iter).click();
					break;
				}
				else
				{
					Assert.fail("There is no presence of hyperlink");
				}
			}
		}

	}

	/**
	 * validate records related to sim deleted in table
	 * @param simCards
	 * @param tableName
	 */
	public void vTabledatadinDB(HashMap<Integer, String> simCardsS,String tableName)
	{
		resSet=null;
		try{
			for (int loop =0 ;loop< simCardsS.size();loop++) 
			{
				sqlQuery = "SELECT * FROM "+tableName+" where username LIKE'%"+simCardsS.get(loop)+"%'";
				dbMethods.connection = dbMethods.dbConnection(testDataOR.get("databaseRadius"), testDataOR.get("dbuser"),testDataOR.get("dbpassword"));
				try{	
					resSet = dbMethods.statement.executeQuery(sqlQuery);
				} catch (NullPointerException e) {
					logger.info("Records related to sim card"+"GGSN022_"+simCardsS.get(loop)+" "+"GGSN021_"+simCardsS.get(loop)+" are not present");
				}
				if(resSet!=null)
				{
					Assert.fail(tableName+" is having "+simCardsS.get(loop)+" value");
				}
			}
		} catch (SQLException e) {
			Assert.fail("Problem while validating table data");
			e.printStackTrace();
		}
	}
}