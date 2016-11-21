package com.ingenico.base;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/base/TestBase.java $
$Id: TestBase.java 8036 2014-06-16 09:19:40Z nguttula $
 */

import java.awt.AWTException;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import au.com.bytecode.opencsv.CSVReader;

import com.ingenico.base.logger.Logger;
import com.ingenico.base.logger.LoggerFactory;
import com.ingenico.common.CommonConstants;
import com.ingenico.util.ErrorUtil;
import com.ingenico.util.PDF_Reader;
import com.ingenico.util.Xls_Reader;

public class TestBase {
	public static Properties CONFIG = null, OR = null, commonOR = null;		
	public static Xls_Reader cardPaymentXls = null, ePaymentXls = null;	
	public static Logger logger = LoggerFactory.getLogger();
	public boolean isBrowserOpened = false,exists, x = false,cmpIMG;
	public JavascriptExecutor js;
	public Set<String> windowIds;
	public String[] winArray;
	public SimpleDateFormat df ;
	public static String  currentDate, comment="comments1234",actualimg = CommonConstants.filedownloadPath + "actual_ingenico_logo",


			//Levels declarations
			countryAus="AUSTRALIE", countryFran="FRANCE", countrySpa="SPAIN", countryUK="UK", cityParis = "PARIS", shop1Paris = "PARIS_SHOP1", shop2Paris = "PARIS_SHOP2",
			cityBrisbane="BRISBANE",brisbaneSop1="BRISBANE_SHOP1", brisbaneSop2="BRISBANE_SHOP2", citylyon="LYON", shop1Lyon = "LYON_SHOP2", shop2Lyon = "LYON_SHOP1", 
			citySydney="SYDNEY", shop1Sydney="SYDNEY_SHOP1", shop2Sydney = "SYDNEY_SHOP2", cityBarcelone="BARCELONE", barceloneShop1="BARCELONE_SHOP1", barceloneShop2="BARCELONE_SHOP2", 
			cityBerm = "BIRMINGHAM", birminghmaShop1="BIRMINGHAM_SHOP1", birminghmaShop2="BIRMINGHAM_SHOP2", cityLondon = "LONDON", londonShop1="LONDON_SHOP1",londonShop2="LONDON_SHOP2",
			cityMadrid = "MADRID", madridShop1 = "MADRID_SHOP1", madridShop2 = "MADRID_SHOP2", 
			defaultCountryVal="Choose one", 

			//Currency declarations
			currencyAus = "AUD", currencyFran = "EUR", currencySpa = "EUR", currencyUK = "GBP", currencyLabel = "Currency",

			//Periods declarations
			lastMonth = "Last month", thisMonth = "This month", periodToday  = "Today", periodYest = "Yesterday", periodThisWeek="This week", periodLast3M = "Last 3 months",
			periodLastWk = "Last week", period20Days = "Last 20 days", period30Days = "Last 30 days", periodLast6M = "Last 6 months", periodDefault = "---",
			periodthisbilling="This billing period",lastbillingprd="Last billing period",last6monthsprd="Last 6 billing periods",

			//Bread crumbs declarations
			snapshotBC = "Snapshot", cpTransOverBC = "Transaction Overview", cpTransJourBC = "Transaction Journal", cpEPJourBC = "ePayment journal",
			cpTransSettleBC = "Settlement Overview", cpReconBC = "Transaction Reconciliation", gprsSimUsageBC = "SIM Usage", gprsSimFleetBC= "SIM Fleet",
			allPosBC = "All POS", allCashierBC = "All Cashiers", allCardTypeBC = "All Card Types", allMidBC = "All MID", allApplicationsTypesBC="All Application Types",
			umUsersBc="Users", umAddUsersBc="Users » Add User", cpAvoirBC = "Avoir Conforama", bcLogs="Logs", bcPoolView="Pool View",
			epJournal="Journal",epSubModMoto="MOTO",umSubModUsers="Users",umSubModAct="Activity", schRptBrdCrm="Scheduled Reports",

			//Applications and Card declarations
			emvUKApp = "EMV UK", emvFRApp = "EMV FR", emvESApp = "EMV ES", amexApp = "Amex", chqFRApp = "CHEQUE FR", emvAUSApp = "EMV AUS", emvPLBSApp = "EMV PLBS FR",
			migsApp = "MIGS", dccApp = "DCC FCC FR", dinersFRApp = "Diners FR", dinersIntApp = "Diners Int.", frEMEApp = "FR EME", amexPisteFr="Amex Piste FR",emvSansApp="EMV Sans Contact",
			finarefFrApp="Finaref FR",deltaApp="Delta",dinersApp="DINERS", alphraApp = "Alphyra FR", finarefApp = "FINAREF FR",
			emvFRAppVal = "2", alphraAppVal = "12", amexAppVal = "3", emvAusAppVal = "5", cheqFrAppVal = "4", dinersAppVal = "37", emvEsAppVal = "6", migsAppVal = "19", 
			frEMEAppVal = "26", diffAppVal = "10", shop1ParisVal="10014", shop1Val="10002", shop2Val="10016", shop3Val="10019", contFranVal = "10001", cityParisVal="10011",
			cityLyonVal="10010", shop1LyonVal="10012", emvUKAppVal = "1", countryAusVal="10000",
			visaCardType="Visa", cetCardType = "Card Entry Type",  cardType="AMEX", acceptMethodNone="None", cetNotDefined = "Not defined",			 
			cetKeyedEntry = "Keyed Entry", cetSwiped = "Swiped", cetChipCard = "Chip card", cetSwipeRead = "Swipe with attempt to read chip",			
			viewRcpt="View Receipt", dinersSwipedApp="DINERS SWIPED",electronApp="Electron",jcbApp="JCB", jcbSwipedApp="JCB SWIPED",laserApp="Laser",maestroApp="Maestro", maestroContactlessApp="MAESTRO CONTACTLESS",
			maestroUkApp="Maestro UK", masterCardApp="MASTER CARD",masterCardContactlessApp="MASTERCARD CONTACTLESS",masterCardDebitApp="Mastercard debit", postBankApp="PostBank",
			roiVisaDebitApp="ROI visa debit", soloApp="Solo", unknownApp="Unknown", isaApp="VISA", visaContactlessApp="VISA CONTACTLESS", visaOneApp="Visa one4all", vPayApp="VPAY",

			csTotalTransRow = "Total Transactions",csTotalSalesRow = "Total Sales",csTotalRefundsRow = "Total Refunds",csTotalCanSalesRow="Total Cancel.(Sales)",totCanRefunds="Total Cancel.(Refunds)",
			totCancelCashBack="Total Cancel.(Cashback)",trnscAmnt="Transactions Amount",colSaleAmt = "Sales Amount",refAnt = "Refunds amount",cancelSalesAmnt="Cancel.(Sales) Amount",
			csCanSaleAmtRow="Cancel. (Sales) Amount",cancelRefundsAmnt="Cancel.(Refunds) Amount",cancelCashBackAmnt="Cancel.(Cashback) Amount",avgSaleVal="Average Sale Value",lowestSaleVal="Lowest Sale Value",highestSaleVal="Highest Sale Value",
			cancelSalesVal = "Cancel. (Sales)",cancelRefundsVal = "Cancel. (Refunds)", cancelCashBackVal = "Cancel. (Cashback)",colRefundAmt = "Refunds Amount",

			//Errors, Messages declarations,TAM options
			tabSelectionAttrib = "tabs-selected", valAll = "All", startEndMsg="Start dates cannot be older than 15 months.",
			alert2_msg="The period must be less than 6 months.", frenchLang = "Francais", frenchLoginErrMsg = "Bienvenue sur l'e-Portal d'Ingenico !", engLang = "English", forgotPassMsg = "Forgot Password?",
			passResetMsg = "Please enter your login. An email will be sent with your new temporary password.",loginErrMess="Invalid Authentication Information",clientSelctionMessage="Please select client:",
			welcomeMessage="Welcome to Ingenico e-Portal!", gprsSimFleet="SIMFleet",gprsSimUsage="SIMUsage",subModTrnOvr="TransactionOverview",subModTrnJrn="TransactionJournal",subModSetlOvr="SettlementOverview",
			subModTrnRecon="TransactionReconciliation",softwareOverview="Software Overview",hardwareOverview="Hardware Overview",assetTracking="Asset tracking",
			noTransMsg = "No transcactions to your search", questionVal = "???", invalidSimMsg = "Please enter a valid SIM Id.", invalidPercMsg = "Please enter valid usage percentage.",
			altMsg1="One or more values are not correct.", altMsg2="Please correct highlighted fields.", validMidMsg = "Please enter a valid MID.", validPosMsg = "Please enter a valid POS.", setMidErrMsg = "This field can only contain alphanumeric characters",
			setNumErrMsg = "Settlement Number is not correct.", statusNew="New",statusAvail="Available",displayText="Displaying",alet_msg3="The period must be less than 15 months.",
			tmsSofOvrwMod="SoftwareOverview", tmsHardOvrwMod="HardwareOverview",currAlertMsg="The levels selected must have the same currencies",tmsModule="TMS",orderCreatedMsg = "The order has been successfully created: Transaction approved.",
			voucherMsg = "If the voucher does not appear automatically, click here.",refundSuccssMsg="The status of the transaction has been successfully changed: Transaction approved.",
			noResultTxt="No result to your search", warningUsertype="Please select usage type",warningStatustype="Please select at least one status",warGraphType="Please select a graph type",

			//GPRS Offer	
			rpa_sim="Rate Plan Alerts - Sim",rpa_pool="Rate Plan Alerts - Pool",statusover="Status Overview",sessionOver="Session Overview",rateplanbreak="Rate plan breakdown",statusBreak="Status Breakdown",
			ratePlanalert="Rate Plan Alerts (SIM)",rpapool="Rate Plan Alerts (Pool)",usageHist="Usage History",simfleetHist="SIM Fleet History",simActivity="SIM Activity",valLevel="Level",country="Country",city="City",shop="Shop",	
			valLevel1="Level 1",valLevel2="Level 2",valLevel3="Level 3",allStatus="All Statuses",

			//User Management
			users="Users",addUser="Add user",deactUser="Deactivate user",actUser="Activate user",

			//Xpath Attribute
			titleAtt="Title", indexAtt="INDEX", boxtitleAtt="BoxTitle", boxNameAtt="BOXNAME", boxnoAtt="BOXNO",	style="style",			

			//Customer, Page, Box Declarations
			customer="Customer:", maxPageLimit = "10", minPageLimit = "1", pageLimit30 = "30", pageLimit10 = "10", pageLimit20 = "20", pageLimit40 = "40", pageLimit45 = "45", pageLimit100 = "100",
			pageLimit50 = "50", boxTransAmnts = "Transactions & Amounts", boxTransHist = "Transaction History",boxOnOffTrans = "Online/Offline Transactions",
			pageLimit55 = "55", pageLimit15 = "15", pageLimit320 = "320", pageLimit200 = "200",clientOne="QA_EPORTAL_C1",client13="QA_EPORTAL_C13",
			ten="10",twenty="20",thirty="30",fourty="40",fifty="50", pageview2="2", noOfPage="1",perc_max="100",perc_min="50",
			perc_one="1",perc_zero="0",

			//csv, pdf file names
			cpOverCSVFileName = "cardpayment_overview", cpJourCSVFileName = "CardPayment_journal_", cpJourPDFFileName = "cardpayment_journal", transCSVFileName = "transactions_", gprsHistFileName = "gprs_history_",
			pdfFileInitial = "CardPayment_", cpReconFileName = "cardpayment_reconciliation", csvType = "csv", pdfType = "pdf", autiItExe = "FF.exe", gprsFleetFileName = "gprs_simfleet", gprsLabel = "GPRS", gprsUsageFileName = "gprs_sim_usage_pool_view",
			gprssimUsage="gprs_simusage",cpJournFile  = "CardPayment_journal", gprsLogsFileName = "gprs_logs", cpReconcileFileName = "CardPayment_reconciliation",
			cpReconDiffFileName = "cardpayment_reconciliationdifference", dbModule = "Dashboard", cpModule = "Card Payment", epModule ="ePayment", srModule = "Scheduled Report",
			umModule = "User Management", cpReconExpFileName = "cardpayment_reconciliationexceptiondetail",simChangeHist="gprs_simchangehistory_",
			cpOverfilename = "card_payment_overview",cpOvermid = "card_payment_overview_mid",cpOverpos = "card_payment_overview_pos",cpOvercashier = "card_payment_overview_cashier",cpOvercardtype = "card_payment_overview_card_type",
			umLogsFileName = "user_management_logs", umUsersFileName="user_management_users", transactionOverView="Transaction Overview",
			cpReconPdfFileName="card_payment_reconciliation",cpCSVJrn="card_payment_journal_",cpPDFJrn="card_payment_journal", 
			packageFile="package_alert_",statusOverFile="status_overview_",sessionOverFile="sessions_overview_",poolalert="pool_alert_",
			gprs_simdetails_file="gprs_simdetails",gprs_simusagepoolview_file="gprs_sim_usage_detailed_pool_view",gprs_simUsageFileName="gprs_simusagevolumes",simusagelogs="gprs_simusagelogs",
			rateplan_break_file="rate_plan_breakdown_",status_break_file="status_breakdown_",usage_evolution_file="usage_evolution_",simActivitySolu_file="sim_activity_evolution_",
			sim_fleet_evolution_file="sim_fleet_evolution_", cpReconDiffCSVFileName = "card_payment_reconciliation_difference",

			//CardPayment Offer	
			amounts="Amounts",saleVal="Sale Value",onOrOffTrns="On/Off Transactions",offHist="Offline History",cetBrkDwn="CET Breakdown",cetHist="CET History",saleValueHist="Sale Value History",
			cardpreNotPreHist="Card present/not present History", cpPieChart="cardPayementPieChart",emidBrkDwnChrt="EMID_BREAKDOWN_CHART",

			//Regular Expression
			regExp1="\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\s\\w{2}",regExp2="\\d{2}:\\d{2}:\\d{2}",
			regExp3="^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$",
			regExp4="Exported from Ingenico e-Portal -\\s\\d{4}-\\d{2}-\\d{2}",regExp5="[A-Z,]",
			regExp6="Exported from Ingenico e-Portal -\\s\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\s\\w{2}",
			regExp7="\\d{2}-\\d{2}-\\d{4}\\s\\d{2}:\\d{2}:\\d{2}",regExp8="[,.%MB]",			

			//E payment createOrder Data 
			addressTxt="Address",state="State",zip="ZIP",amount="25",cardHolderName = "CARD_HOLDER_NAME", cardNumber = "5301250070000175", expirytMonth = "12",
			expiryYear = "2020", cvc = "123", amtInt = "100", amtDec = "00",

			//Application Names
			axis="Axis",host="Host",pos="POS",axisFall="Axis fallback",axisnew="axis",hostnew="host",posnew="pos",axisFallnew="fallback",

			//common variables
			close="Close",back="Back",next="Next", popUp = "POPUP", defaultStartTime = "12:00 AM", defaultEndTime = "11:59 PM",

			//Edit box text
			edit="Edit",duplicate="Duplicate",exportCSV="Export CSV",exportPDF="Export PDF";

	public static long endTime,systime;	
	public WebDriver driver=null;
	public WebElement webelement,savedItem,savedsearchItemElement,savedSearchItem,searchItemElement, tab_container,boxelement;
	private File fXmlFile;
	public ArrayList<String> content = new ArrayList<String>();
	private int count,check;
	public Date testDateFormat=null, date;
	public Process proc;
	public String  value, year, month, day, ParentWindow=null, formatDateStr, elementText,path,text,warMess,
			sdate=null, imgPath, error=null,fileName, parentWindow,waitTime, eleName, level;
	public List<WebElement> options, elements,cList,noPages,tr_collection;
	public Calendar cal, now;
	public int oneDayMore	= 1, oneDayLess = -1, monthDiff = -16, i,j,k,itemcount;
	public static  Locale locale = Locale.ENGLISH;
	public Actions action;
	public Select select;
	public PDDocument document;
	public List pages;
	public Iterator iter,imageIter;
	public PDPage page;
	public Map pageImages;
	public PDResources resources;
	public PDXObjectImage image;

	static
	{
		OR=new Properties();				
		CONFIG=new Properties();
		commonOR=new Properties();	
	}
	/**
	 *  initializing the Tests
	 * @throws Exception
	 */
	public void initialize() throws Exception {
		try {

			CONFIG.loadFromXML(new FileInputStream(CommonConstants.configFile));
			commonOR.loadFromXML(new FileInputStream(CommonConstants.commonConfigFile));
			String pageName=getUIMapPage();
			OR.loadFromXML(new FileInputStream(pageName));
		} catch (InvalidPropertiesFormatException e) {

			e.printStackTrace();
		}	 
		logger.debug("Loaded XML Files successfully");
	}

	/**
	 * Browser set up
	 * @param browser
	 * @throws MalformedURLException
	 * @throws InterruptedException
	 */
	@Parameters({"browser"})
	@BeforeClass
	public void setup(String browser) throws MalformedURLException, InterruptedException{
		DesiredCapabilities capability=null;
		try{
			if(browser.equalsIgnoreCase("firefox")){			
				FirefoxProfile profile = new FirefoxProfile();
				profile.setEnableNativeEvents(true);
				profile.setPreference("browser.download.folderList", 2);
				profile.setPreference("browser.download.manager.showWhenStarting",false);
				profile.setPreference("browser.download.dir",CommonConstants.filedownloadPath);
				profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf,application/msword,text/csv");
				capability= DesiredCapabilities.firefox();
				//capability.setBrowserName("firefox");
				capability.setCapability(FirefoxDriver.PROFILE, profile);
				//capability.setPlatform(org.openqa.selenium.Platform.ANY);
				driver=new FirefoxDriver(capability);
			}

			if(browser.equalsIgnoreCase("internet explorer")){
				System.setProperty("webdriver.ie.driver", CommonConstants.IEDriverPath+"\\IEDriverServer.exe");
				capability= DesiredCapabilities.internetExplorer();
				capability.setBrowserName("internet explorer");
				capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);  
				capability.setPlatform(org.openqa.selenium.Platform.WINDOWS);
				driver=new InternetExplorerDriver(capability);
			}

			driver.get(CONFIG.getProperty("BrowserURL"));
			//To assert IE certificate error message
			if(browser.equalsIgnoreCase("internet explorer")){
				driver.navigate().to("javascript:document.getElementById('overridelink').click()");
				driver.navigate().to("javascript:document.getElementById('overridelink').click()");
			}
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			driver.manage().window().maximize();

		}
		catch(Throwable t)
		{
			ErrorUtil.addVerificationFailure(t);
			logger.error("Exception thrown opening browser");
		}
	}
	/**
	 *  initializing the Tests
	 * @throws Exception
	 */
	@BeforeSuite
	public void initSetUp() throws Exception {
		initialize();
	}

	/**
	 * Get UIMap Page
	 * @return uiMap
	 */
	public String getUIMapPage() {
		String sProject=getCurrentProject();		
		String uiMap = ""; 
		uiMap = CONFIG.getProperty(sProject+"UIMapFile");
		return uiMap;
	}

	/**
	 * Get running project
	 * @return sProject
	 */
	public String getCurrentProject(){
		String sProject = null;
		if (this.getClass().toString().contains(".ePayment")) 
			sProject = "ePayment";
		else if (this.getClass().toString().contains(".cardPayment"))
			sProject = "cardPayment";
		else if (this.getClass().toString().contains(".userManagement"))
			sProject = "userManagement";
		else if (this.getClass().toString().contains(".dashboard"))
			sProject = "dashboard";
		else if (this.getClass().toString().contains(".tms"))
			sProject = "tms";
		else if (this.getClass().toString().contains(".gprs"))
			sProject = "gprs";
		else if (this.getClass().toString().contains(".scheduledReports"))

			sProject = "scheduledReports";

		else if (this.getClass().toString().contains(".transverse")) 

			sProject = "transverse";
		else    
			Assert.fail("Cannot identify currently running project!!!");

		return sProject;
	}	


	/**
	 * Method for returning  module object Locator
	 * @param objLocator
	 * @return
	 */
	public String getPath(String objLocator) {
		String x="";
		try {
			x = OR.getProperty(objLocator);
			if(x.equals(""))
				Assert.fail("Path to locate element is Null");

		} catch (Exception t) {
			logger.error("Cannot find object with key -- " + objLocator);

		}
		return x;

	}

	/**
	 * Method for returning common object Locator
	 * @param objLocator
	 * @return
	 */
	public String getCommonPath(String commObjLocator) {
		String x="";
		try {
			x = commonOR.getProperty(commObjLocator);
			if(x.equals(""))
				Assert.fail("Path to locate element is Null");
		} catch (Exception t) {
			logger.error("Cannot find object with key -- " + commObjLocator);

		}
		return x;
	}



	/**
	 * It's for common method for common objects
	 * @param commobject
	 * @return
	 */
	public WebElement getCommonObject(String commobject) {
		int counter =0;
		WebElement x=null;		
		try
		{
			for(counter=0;counter<4;counter++)
			{
				try {
					if(commobject.contains("link"))
					{
						x = driver.findElement(By.linkText(commonOR.getProperty(commobject)));
					}
					else if(commobject.contains("xpath"))
					{
						x = driver.findElement(By.xpath(commonOR.getProperty(commobject)));
					}
					else if(commobject.contains("css"))
					{
						x = driver.findElement(By.cssSelector(commonOR.getProperty(commobject)));
					}
					else if(commobject.contains("id"))
					{
						x = driver.findElement(By.id(commonOR.getProperty(commobject)));
					}
					else if(commobject.contains("name"))
					{
						x = driver.findElement(By.name(commonOR.getProperty(commobject)));	
					}
					else if(commobject.contains("classname"))
					{
						x = driver.findElement(By.className(commonOR.getProperty(commobject)));	
					}
					else
					{
						logger.error("Object locator format is not proper");
						Assert.fail("Object locator format is not proper");
					}
					break;
				}
				catch (Exception e1)
				{				
					waitNSec(1);
				}
			}
			if(counter==4)
			{
				Assert.assertFalse(x.equals(""));
			}
		} catch (Exception e)
		{					
			logger.error("Cannot find object with key -- " + commobject);			
			Assert.fail("Cannot find object with key -- "+ commobject);
		}//end of for							
		return x;
	}

	/**
	 * It's for common method for objects used in module
	 * @param object
	 * @return x
	 */
	public WebElement getObject(String object) {
		int counter =0;
		WebElement x=null;		
		try {
			for(counter=0;counter<4;counter++)
			{
				try{
					if(object.contains("link"))
					{
						x = driver.findElement(By.linkText(OR.getProperty(object)));
					}
					else if(object.contains("xpath"))
					{					
						x = driver.findElement(By.xpath(OR.getProperty(object)));
					}
					else if(object.contains("css"))
					{
						x = driver.findElement(By.cssSelector(OR.getProperty(object)));
					}
					else if(object.contains("id"))
					{					
						x = driver.findElement(By.id(OR.getProperty(object)));
					}
					else if(object.contains("name"))
					{
						x = driver.findElement(By.name(OR.getProperty(object)));	
					}
					else if(object.contains("classname"))
					{
						x = driver.findElement(By.className(OR.getProperty(object)));	
					}
					else
					{
						logger.error("Object locator format is not proper");
						Assert.fail("Object locator format is not proper");
					}
					break;
				}
				catch (Exception e1)
				{				
					waitNSec(1);
				}
			}
			if(counter==4)
			{
				Assert.assertFalse(x.equals(""));
			}
		} catch (Exception e)
		{					
			logger.error("Cannot find object with key -- " + object);			
			Assert.fail("Cannot find object with key -- "+ object);
		}//end of for							
		return x;
	}

	/**
	 * It's for common method to get module List of objects
	 * @param object
	 * @return x
	 */
	public List<WebElement> getObjects(String object) {
		int counter =0;
		List<WebElement> x=null;		
		try {
			for(counter=0;counter<4;counter++)
			{
				try{
					if(object.contains("link"))
					{
						x = driver.findElements(By.linkText(OR.getProperty(object)));
					}
					else if(object.contains("xpath"))
					{					
						x = driver.findElements(By.xpath(OR.getProperty(object)));
					}
					else if(object.contains("css"))
					{
						x = driver.findElements(By.cssSelector(OR.getProperty(object)));
					}
					else if(object.contains("id"))
					{					
						x = driver.findElements(By.id(OR.getProperty(object)));
					}
					else if(object.contains("name"))
					{
						x = driver.findElements(By.name(OR.getProperty(object)));	
					}
					else if(object.contains("classname"))
					{
						x = driver.findElements(By.className(OR.getProperty(object)));	
					}
					else
					{
						logger.error("Object locator format is not proper");
						Assert.fail("Object locator format is not proper");
					}
					break;
				}
				catch (Exception e1)
				{				
					waitNSec(1);
				}
			}
			if(counter==4)
			{
				Assert.assertFalse(x.equals(""));
			}
		} catch (Exception e)
		{					
			logger.error("Cannot find objects with key -- " + object);			
			Assert.fail("Cannot find objects with key -- "+ object);
		}//end of for							
		return x;
	}

	/**
	 * Returns the web element
	 * @param locator
	 * @return x
	 */
	public WebElement getObjectDirect(final By locator) {
		int counter =0;
		WebElement x=null;
		try
		{
			for(counter=0;counter<4;counter++)
			{
				try{
					x = driver.findElement(locator);
					break;
				}
				catch (Exception e1)
				{				
					waitNSec(1);
				}
			}
			if(counter==4)
			{
				Assert.assertFalse(x.equals(""));
			}

		} catch (Exception e)
		{					
			logger.error("Cannot find object with key -- " + locator);			
			Assert.fail("Cannot find object with key -- "+ locator);
		}//end of for							
		return x;
	}

	/**
	 * Returns the list of web elements
	 * @param locator
	 * @return x
	 */
	public List<WebElement> getObjectsDirect(final By locator) {
		int counter =0;
		List<WebElement> x=null;
		try
		{
			for(counter=0;counter<4;counter++)
			{
				try{
					x = driver.findElements(locator);
					break;
				}
				catch (Exception e1)
				{				
					waitNSec(1);
				}
			}
			if(counter==4)
			{
				Assert.assertFalse(x.equals(""));
			}

		} catch (Exception e)
		{					
			logger.error("Cannot find objects with key -- " + locator);			
			Assert.fail("Cannot find objects with key -- "+ locator);
		}//end of for							
		return x;
	}

	/**It's common method for list of  common objects	 
	 * @param commobjects
	 * @return
	 */
	public List<WebElement> getCommonObjects(String commobjects) 
	{
		elements = null;
		try 
		{
			if (commobjects.contains("xpath")) 
			{
				elements = driver.findElements(By.xpath(commonOR
						.getProperty(commobjects)));
			}
		} 
		catch (Exception t) 
		{
			logger.error("Cannot find objects with key -- " + commobjects);
			Assert.fail("Cannot find objects with key -- "+ commobjects);
		}
		return elements;
	}


	/**It's for method for List of elements	 
	 * @param commobjects
	 * @return
	 */
	public List<WebElement> get_list_of_elements(String commobjects) 
	{
		elements = null;
		try 
		{
			if (commobjects.contains("xpath")) 
			{
				elements = driver.findElements(By.xpath(OR
						.getProperty(commobjects)));
			}
			else if (commobjects.contains("css")) 
			{
				elements = driver.findElements(By.cssSelector(OR
						.getProperty(commobjects)));
			}
		} 
		catch (Exception t) 
		{
			logger.error("Cannot find objects with key -- " + commobjects);
			Assert.fail("Cannot find objects with key -- "+ commobjects);

		}
		return elements;
	}

	/**It's for common method for List of elements	 by direct xpath 
	 * @param commobjects
	 * @return
	 */
	public List<WebElement> get_list_of_elements(String commobjects,int i) 
	{
		elements = null;
		try 
		{

			elements = driver.findElements(By.xpath(commobjects));

		} 
		catch (Exception t) 
		{
			logger.error("Cannot find objects with key -- " + commobjects);
			Assert.fail("Cannot find objects with key -- "+ commobjects);

		}
		return elements;
	}


	/**
	 * Login
	 * @param username
	 * @param password
	 * @return boolean
	 */
	public void login(String username, String password) {
		try{
			getCommonObject("UserName_xpath").sendKeys(username);
			getCommonObject("Password_xpath").sendKeys(password);
			getCommonObject("Submit_button_xpath").click();
			Assert.assertTrue(getCommonObject("login_name_id").getText().contains(username));
			logger.info("Logged in successfully with '"+username+"' user.");
		}
		catch(Exception e)
		{
			logger.error("Problem with login with '"+username+"' user.");
		}

	}

	/**
	 * Logout
	 */
	public void logout() {
		try{
			if(driver.findElement(By.linkText("Sign out")).isDisplayed())
				getCommonObject("logout_link").click();
		}catch(Exception e){
			logger.info("Log out link is not present");
		}
	}

	/**
	 * captureScreenShotOnFailure
	 * @param filename
	 * @throws IOException
	 */
	public void captureScreenShotOnFailure(String filename) {
		try {
			//WebDriver augmentedDriver = new Augmenter().augment(driver);
			File source = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(source, new File(CommonConstants.screenshotPath+this.getCurrentProject().toString()+"\\"+ filename + ".jpg")); 
		}
		catch(IOException e) {
			logger.error("Failed to capture screenshot: " + e.getMessage());
		}
	}

	/**
	 * It's for common method for check page source objects
	 * @param commobject
	 * @return
	 */
	public void getContains(String str){
		try{

			Assert.assertTrue(driver.getPageSource().contains(str),"Expected value is missing "+str);
		}

		catch (Exception e)
		{					
			logger.error("Cannot find the expected value "+str);

		}
	}

	/**
	 * It's for common method to enter data in input boxes
	 * @return
	 */
	public void populateInputBox(String Object, String value){
		try{
			if(!getObject(Object).getAttribute("value").isEmpty()){
				getObject(Object).clear();
			}		
			getObject(Object).sendKeys(value);
		}
		catch(Exception e)
		{
			logger.error("Expected value is not set to the input box");
			Assert.fail("Expected value is not set to the input box");
		}
	}

	/**
	 * isElementPresent checks if the element is present in the application
	 * Returns the 
	 * @param object
	 * @return x
	 */

	public boolean isElementPresent(String  object) {
		boolean x=false;
		try{
			if(object.contains("link")){
				driver.findElement(By.linkText(OR.getProperty(object)));
				x=true;
			}
			else if(object.contains("xpath")){	
				driver.findElement(By.xpath(OR.getProperty(object)));
				x=true;
			}
			else if(object.contains("css")){	
				driver.findElement(By.cssSelector(OR.getProperty(object)));
				x=true;
			}
			else if(object.contains("id")){	
				driver.findElement(By.id(OR.getProperty(object)));
				x=true;
			}
			else if(object.contains("name")){	
				driver.findElement(By.name(OR.getProperty(object)));
				x=true;
			}
			else if(object.contains("classname")){	
				driver.findElement(By.className(OR.getProperty(object)));	
				x=true;
			}
			return x;
		}
		catch (NoSuchElementException e) {
			return false;
		}
	}




	/**
	 * Navigate to dashboad Page
	 * @throws InterruptedException 
	 */
	public void dashboardPageNavigator() 
	{
		waitForCommonElementPresent("dashboard_link");
		getCommonObject("dashboard_link").click();
	}

	/**
	 * Navigate to cardPayment Page
	 * @throws InterruptedException 
	 */
	public void cardPaymentPageNavigator() 
	{
		waitForCommonElementPresent("cardpayment_link");
		getCommonObject("cardpayment_link").click();
	}

	/**
	 * Navigate to ePayment Page
	 * @throws InterruptedException 
	 */
	public void ePaymentPageNavigator() 
	{
		waitForCommonElementPresent("ePayment_link");
		getCommonObject("ePayment_link").click();
	}

	/**
	 * Navigate to GPRS Page
	 * @throws InterruptedException 
	 */
	public void GPRSPageNavigator() 
	{
		waitForCommonElementPresent("GPRS_link");
		getCommonObject("GPRS_link").click();
	}

	/**
	 * Navigate to TMSPayment Page
	 * @throws InterruptedException 
	 */
	public void TMSPaymentPageNavigator() 
	{
		waitForCommonElementPresent("TMS_link");
		getCommonObject("TMS_link").click();
	}

	/**
	 * Navigate to scheduledReports Page
	 * @throws InterruptedException
	 */
	public void scheduledReportsPageNavigator() 
	{
		waitForCommonElementPresent("ScheduledReport_link");
		getCommonObject("ScheduledReport_link").click();
	}

	/**
	 * Navigate to userManagement Page
	 * @throws InterruptedException 
	 */
	public void userManagementPageNavigator() 
	{
		waitForCommonElementPresent("UserManagement_link");
		getCommonObject("UserManagement_link").click();
	}

	/**
	 * verifies if Dash board is current tab if not navigates to dash board
	 */
	public void verifyDashboardPage(){
		if(!(getCommonObject("dashboard_link").getAttribute("class").equals("current")))
		{			
			((JavascriptExecutor)driver).executeScript("arguments[0].click();", getCommonObject("dashboard_link"));
			logger.info("Clicked on dashboard page");
		}
	}
	/**
	 * cardPayment SubPage's Navigator method
	 * @param subPageName
	 * @throws InterruptedException
	 */
	public void cardPaymentSubPageNavigator(String subPageName) 
	{
		Actions action=new Actions(driver);
		webelement=getCommonObject("cardpayment_link");
		action.moveToElement(webelement).perform();
		if(subPageName.equalsIgnoreCase(snapshotBC))
			getCommonObject("CPSnapshot_xpath").click();
		else if(subPageName.equalsIgnoreCase(subModTrnOvr))
			getCommonObject("TransactionOverview_xpath").click();
		else if(subPageName.equalsIgnoreCase(subModTrnJrn))
			getCommonObject("TransactionJournal_xpath").click();
		else if(subPageName.equalsIgnoreCase(subModSetlOvr))
			getCommonObject("SettlementOverview_xpath").click();
		else if(subPageName.equalsIgnoreCase(subModTrnRecon))
			getCommonObject("TransactionReconciliation_xpath").click();
		else
		{
			logger.error("No Subpages found in Card Payment Page");
			Assert.fail("No Subpages found in Card Payment Page");
		}

	}

	/**
	 * ePayment SubPage's Navigator method
	 * @param subPageName
	 * @throws InterruptedException
	 */
	public void ePaymentSubPageNavigator(String subPageName) 
	{
		Actions action=new Actions(driver);
		webelement=getCommonObject("ePayment_link");
		action.moveToElement(webelement).perform();
		if(subPageName.equalsIgnoreCase(snapshotBC))
			getCommonObject("EPSnapshot_xpath").click();
		else if(subPageName.equalsIgnoreCase(epJournal))
			getCommonObject("Journal_xpath").click();
		else if(subPageName.equalsIgnoreCase(epSubModMoto))
			getCommonObject("MOTO_xpath").click();
		else
		{
			logger.error("No Subpages found in ePayment Page");
			Assert.fail("No Subpages found in ePayment Page");
		}
	}

	/**
	 * GPRS SubPage's Navigator method
	 * @param subPageName
	 * @throws InterruptedException
	 */
	public void GPRSSubPageNavigator(String subPageName) 
	{
		action=new Actions(driver);
		webelement=getCommonObject("GPRS_link");
		action.moveToElement(webelement).perform();
		if(subPageName.equalsIgnoreCase(snapshotBC))
		{
			getCommonObject("GPRSSnapshot_xpath").click();
		}
		else if(subPageName.equalsIgnoreCase(gprsSimFleet))
		{
			getCommonObject("SIMFleet_xpath").click();
		}
		else if(subPageName.equalsIgnoreCase(gprsSimUsage))
		{
			getCommonObject("SIMUsage_xpath").click();
		}
		else
		{
			logger.error("No Subpages found in GPRS Page");
			Assert.fail("No Subpages found in GPRS Page");
		}
	}

	/**
	 * TMS SubPage's Navigator method
	 * @param subPageName
	 * @throws InterruptedException
	 */
	public void TMSSubPageNavigator(String subPageName) 
	{
		action=new Actions(driver);
		webelement=getCommonObject("TMS_link");
		action.moveToElement(webelement).perform();
		if(subPageName.equalsIgnoreCase(snapshotBC))
		{
			getCommonObject("TMSSnapshot_xpath").click();
		}
		else if(subPageName.equalsIgnoreCase(tmsSofOvrwMod))
		{
			getCommonObject("SoftwareOverview_xpath").click();
		}
		else if(subPageName.equalsIgnoreCase(tmsHardOvrwMod))
		{
			getCommonObject("HardwareOverview_xpath").click();
		}
		else
		{
			logger.error("No Subpages found in TMS Page");
			Assert.fail("No Subpages found in TMS Page");
		}
	}

	/**
	 * User Management SubPage's Navigator method
	 * @param subPageName
	 * @throws InterruptedException
	 */
	public void UMSubPageNavigator(String subPageName) 
	{
		waitForCommonElementPresent("UserManagement_link");
		action=new Actions(driver);
		webelement=getCommonObject("UserManagement_link");
		action.moveToElement(webelement).perform();
		if(subPageName.equalsIgnoreCase(snapshotBC))
			getCommonObject("UMSnapshot_xpath").click();
		else if(subPageName.equalsIgnoreCase(umSubModUsers))
			getCommonObject("Users_xpath").click();
		else if(subPageName.equalsIgnoreCase(umSubModAct))
			getCommonObject("Activity_xpath").click();
		else
		{
			logger.error("No Subpages found in User Management Page");
			Assert.fail("No Subpages found in User Management Page");
		}
	}

	/**
	 * Navigate to any sub page of GPRS module and validating Bread crumb
	 * @param Subpage
	 * @param breadCrum
	 * @throws InterruptedException
	 */
	public void navigateToGPRSSubPage(String Subpage,String breadCrum) 
	{
		waitForAjaxLoadImg("imgloader_xpath");
		GPRSPageNavigator();
		GPRSSubPageNavigator(Subpage);		
		verifyBreadCrumb("bread_crumb_id", breadCrum);
		logger.info("Navigated to GPRS Subpage "+Subpage);
	}


	/**
	 * GPRS Sub Module presence method
	 * @param gprsSubModArr[]
	 * @throws InterruptedException
	 */
	public void GPRSSubPagePresence(String gprsSubModArr[]) 
	{
		waitForCommonElementPresent("GPRS_link");
		Actions action = new Actions(driver);
		webelement = getCommonObject("GPRS_link");
		action.moveToElement(webelement).perform();
		elements = getCommonObjects("GPRS_xpath");
		for (int j = 0; j < elements.size(); j++)
		{			
			String elementText = elements.get(j).getText();			
			if(elementText.equals(gprsSubModArr[0]) || elementText.equals(gprsSubModArr[1]) || elementText.equals(gprsSubModArr[2]))				
				logger.info(elementText + " sub module is present under GPRS module");
			else
			{
				logger.error(elementText + " sub module is not present under GPRS module");
				Assert.fail(elementText + " sub module is not present under GPRS module");
			}
		}

	}

	/**
	 * Card Payment Sub Module presence method
	 * @param cpSubModArr[]
	 * @throws InterruptedException
	 */
	public void cardPaymentSubPagePresence(String cpSubModArr[]) 			
	{
		Actions action = new Actions(driver);
		webelement = getCommonObject("cardpayment_link");
		action.moveToElement(webelement).perform();
		elements = getCommonObjects("CardPayment_xpath");		
		for (int j = 0; j < elements.size(); j++)
		{			
			String elementText = elements.get(j).getText();			
			if(elementText.equals(cpSubModArr[0]) || elementText.equals(cpSubModArr[1]) || elementText.equals(cpSubModArr[2])||
					elementText.equals(cpSubModArr[3]) || elementText.equals(cpSubModArr[4])|| elementText.equals(cpSubModArr[5]))			
				logger.info(elementText + " sub module is present under Card Payment module");
			else
			{
				logger.info(elementText + " sub module is not present under Card Payment module");
				Assert.fail(elementText + " sub module is not present under Card Payment module");
			}
		}

	}

	/**
	 * ePayment Sub Module presence method
	 * @param epSubModArr[]
	 * @throws InterruptedException
	 */
	public void ePaymentSubPagePresence(String epSubModArr[]) throws InterruptedException
	{
		Actions action = new Actions(driver);
		webelement = getCommonObject("ePayment_link");
		action.moveToElement(webelement).perform();
		elements = getCommonObjects("ePayment_xpath");
		for (int j = 0; j < elements.size(); j++)
		{			
			String elementText = elements.get(j).getText();			
			if(elementText.equals(epSubModArr[0]) || elementText.equals(epSubModArr[1]) || elementText.equals(epSubModArr[2]))
				logger.info(elementText + " sub module is present under ePayment module");
			else
			{
				logger.info(elementText + " sub module is not present under ePayment module");
				Assert.fail(elementText + " sub module is not present under ePayment module");
			}
		}

	}

	/**
	 * TMS Sub Module presence method
	 * @param tmsSubModArr[]
	 * @throws InterruptedException
	 */
	public void tmsSubPagePresence(String tmsSubModArr[]) throws InterruptedException
	{
		Actions action = new Actions(driver);
		webelement = getCommonObject("TMS_link");
		action.moveToElement(webelement).perform();
		elements = getCommonObjects("tms_submoduleslist_xpath");
		for (int j = 0; j < elements.size(); j++)
		{			
			String elementText = elements.get(j).getText();		
			if(elementText.equals(tmsSubModArr[0]) || elementText.equals(tmsSubModArr[1]) || elementText.equals(tmsSubModArr[2]))
				logger.info(elementText + " sub module is present under TMS module");
			else
			{
				logger.info(elementText + " sub module is not present under TMS module");
				Assert.fail(elementText +" sub module is not present under TMS module");
			}
		}

	}	

	/**
	 * User Management Sub Module presence method
	 * @param umSubModArr[]
	 * @throws InterruptedException
	 */
	public void userSubPagePresence(String umSubModArr[]) throws InterruptedException
	{
		Actions action = new Actions(driver);
		webelement = getCommonObject("UserManagement_link");
		action.moveToElement(webelement).perform();
		elements = getCommonObjects("UserMgt_xpath");
		for (int j = 0; j < elements.size(); j++)
		{			
			String elementText = elements.get(j).getText();			
			if(elementText.equals(umSubModArr[0]) || elementText.equals(umSubModArr[1]) || elementText.equals(umSubModArr[2]))
				logger.info(elementText
						+ " sub module is present under User Management module");
			else
			{
				logger.info(elementText + " sub module is not present under User module");
				Assert.fail(elementText +" sub module is not present under User module");
			}
		}

	}



	/**
	 * TODO --------------------------- Date related functions----------------------------------------------------------------------
	 * 	
	 */
	/**Returns current date without any separator
	 * Date formatter 
	 * return current Date
	 */
	public static String dateFormatter(){
		Calendar cal=Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int date = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH);
		int newMonth = month + 1;
		if(newMonth <= 9){
			if(date<=9){
				currentDate = (year+"0"+newMonth+"0"+date);
			}else{
				currentDate = (year+"0"+newMonth+""+date); 
			}
		}
		else{

			if(date<=9){
				currentDate = (year+""+newMonth+"0"+date);
			}else{
				currentDate = (year+""+newMonth+""+date); 
			}
		}
		return currentDate;
	}

	/**Returns current date with -
	 * Date formatter 
	 * return current Date with _
	 */
	public static String date_Formatter(String seperator){
		Calendar cal=Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int date = cal.get(Calendar.DATE);
		int Month = cal.get(Calendar.MONTH);
		int newMonth = Month + 1;
		if(newMonth <= 9){
			if(date<=9){
				currentDate = (year+seperator+"0"+newMonth+seperator+"0"+date);
			}else{
				currentDate = (year+seperator+"0"+newMonth+seperator+date); 
			}
		}
		else{
			if(date<=9){
				currentDate = (year+seperator+newMonth+seperator+"0"+date);
			}else{
				currentDate = (year+seperator+newMonth+seperator+date); 
			}
		}
		return currentDate;
	}

	/**
	 * Setting start or end date from calendar icon
	 * @param year
	 * @param month
	 * @param date
	 */
	public void setFromEndDate(String locator,int year,int month,int date)
	{
		try{
			getCommonObject(locator).click();
			waitForCommonElementPresent("Year_xpath");		
			getCommonObject("Year_xpath").click();
			Select dropdownYear=new Select(getCommonObject("Year_xpath"));
			//wait1Sec();			
			dropdownYear.selectByVisibleText(Integer.toString(year));
			getCommonObject("Month_xpath").click();
			waitForCommonElementPresent("Month_xpath");
			Select dropdownMonth=new Select(getCommonObject("Month_xpath"));			
			dropdownMonth.selectByIndex(month);
			path=getCommonPath("Date_link").replace("Date", Integer.toString(date));
			getObjectDirect(By.linkText(path)).click();			
		}
		catch(Exception e)
		{
			Assert.fail("Error with reading date ");
		}
	}



	//Verify, Date formate as 'yyyy-MM-dd HH:mm:ss a'
	public boolean verifyDateTimeFormat(String date) throws ParseException{
		formatDateStr = "yyyy-MM-dd hh:mm:ss a";
		SimpleDateFormat dateFormat= new SimpleDateFormat(formatDateStr);
		dateFormat.setLenient(false);
		testDateFormat = dateFormat.parse(date);
		return true;	
	}

	/**
	 * Date formatter 
	 * return current Date with -
	 */
	public static String currentDateFormatter(){
		Calendar cal = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",locale );
		currentDate=dateFormat.format(cal.getTime());
		return currentDate;

	}	

	/**
	 * Verify the from and to date while selecting the period.
	 * throws ParseException
	 */
	public void date_diff(int day, String xpath) throws ParseException{

		final	String sdate = getObject("strt_date_id").getAttribute("value");
		final	SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
		final	Date dt = df.parse( sdate ); // conversion from String
		final java.util.Calendar cal = GregorianCalendar.getInstance();
		cal.setTime( dt );
		int expdate=day;
		cal.add(GregorianCalendar.DATE,expdate); // date manipulation
		//Verify the period selections
		logger.info(" Verify the period selections: "+day+" days");		

		//Verify the details
		elements = get_list_of_elements(xpath);
		for(j=0; j<2; j++){
			if(j == 1){
				cal.add(GregorianCalendar.DATE, 1);
				for(i=0; i<elements.size(); i++){

					Assert.assertFalse(elements.get(i).getText().contains(df.format( cal.getTime())));

				}
			}
			if(j == 2){
				cal.add(GregorianCalendar.DATE, -(day+2));
				for(int i=0;i<elements.size();i++){

					Assert.assertFalse(elements.get(i).getText().contains(df.format( cal.getTime())));

				}
			}

		}
		logger.info(" Verified the Search result.");
	}


	/**
	 * get current date
	 * @param loc
	 * @return 
	 * @throws ParseException
	 */
	public Calendar getCurrentDate(String loc) throws ParseException{
		sdate = getObject(loc).getAttribute("value");						
		df = new SimpleDateFormat( "yyyy-MM-dd",locale );
		date = df.parse( sdate ); // conversion from String
		cal = GregorianCalendar.getInstance();
		cal.setTime( date );
		return cal;

	}

	/**
	 * adds end date
	 * @param n
	 * @param loc
	 * @return 
	 */
	public Calendar addEndDate(int n,String loc,Calendar cal){
		cal.add(GregorianCalendar.DATE, n);
		setFromEndDate(loc,cal.get(GregorianCalendar.YEAR),cal.get(GregorianCalendar.MONTH),cal.get(GregorianCalendar.DATE));
		return cal;
	}


	/**
	 * Setting start date
	 * @param year
	 * @param month
	 * @param date
	 */
	public void setFromDate(String locator,int year,String month,int date)
	{
		try{
			getCommonObject(locator).click();
			waitForCommonElementPresent("Year_xpath");
			getCommonObject("Year_xpath").click();
			Select dropdownYear=new Select(getCommonObject("Year_xpath"));			
			dropdownYear.selectByVisibleText(Integer.toString(year));
			getCommonObject("Month_xpath").click();
			Select dropdownMonth=new Select(getCommonObject("Month_xpath"));			
			dropdownMonth.selectByVisibleText(month);
			path=getCommonPath("Date_link").replace("Date", Integer.toString(date));
			getObjectDirect(By.linkText(path)).click();			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.error("Exception in setStartDate .. "+e.getMessage()+ "   Stacke trace  "  + e.getStackTrace().toString());
			Assert.fail("Error with reading date localied message .. "+e.getLocalizedMessage()  + " message "+e.getMessage());			
		}
	}

	/**
	 * NavigateBack
	 */
	public void navigateBack() {
		try{
			driver.navigate().back();
		}
		catch(Exception e)
		{
			Assert.fail("Error with Navigating back to page ");
		}
	}	

	/**
	 * Refresh the browser body 
	 * @throws InterruptedException 
	 * @throws AWTException 
	 */
	public void refresh() {
		driver.navigate().refresh();
		//driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		waitNSec(7);			
	}

	/*
	 * rounds off the double  value
	 */
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}	

	/** This methods gets the list of any drop down items as String
	 * Select list of options 
	 * @param object
	 * @return drop down options
	 */
	public String[] getListItems(WebElement object){
		Select dropDown = new Select(object);           
		i=0;
		List<WebElement> Options = dropDown.getOptions();
		String[] listitems=new String[Options.size()];
		for(WebElement option:Options){
			listitems[i]=option.getText();
			i++;
		}
		return listitems;

	}

	/**
	 * Select any of the option from the list
	 * @param object
	 * @param value
	 */
	public void selectItem(WebElement object,String value){
		Select listbox=new Select(object);
		listbox.selectByVisibleText(value);
		waitNSec(2);
	}

	/**
	 * It returns the currently selected item 
	 * @param object
	 * @return
	 */
	public static String getSelectedItem(WebElement object){
		Select listbox=new Select(object);
		return listbox.getFirstSelectedOption().getText();
	}

	/**
	 * Select drop down option
	 * 
	 */	
	public void selectDropItem(String object,String value)
	{
		Select listbox=new Select(getObject(object));
		listbox.deselectAll();
		listbox.selectByVisibleText(value);
	}

	/**
	 * This method is useful for selecting the multiple values from combo box using actions
	 * 
	 */
	public void selectMultiple(String comboBoxElement, String[] selectedItemsList){
		action=new Actions(driver);
		select = new Select(getObject(comboBoxElement));
		select.deselectAll();
		options = select.getOptions();;
		for(itemcount = 0; itemcount <selectedItemsList.length; itemcount++){
			for(i = 0; i < options.size(); i++){
				value = options.get(i).getText();
				if(value.equalsIgnoreCase(selectedItemsList[itemcount])){
					action.click(options.get(i)).keyUp(Keys.CONTROL).perform();
					break;
				}
			}
		}
	}
	/**
	 * This method is useful for selecting the multiple values from combo box without using actions
	 * @param comboBoxElement
	 * @param selectedItemsList
	 */
	public void selectComboboxMultiple(String comboBoxElement, String[] selectedItemsList){
		select = new Select(getObject(comboBoxElement));
		options = select.getOptions();
		for(itemcount = 0; itemcount <selectedItemsList.length; itemcount++){
			for(i = 0; i < options.size(); i++){
				value = options.get(i).getText();
				if(value.equalsIgnoreCase(selectedItemsList[itemcount])){
					select.selectByVisibleText(value);
					getObject(comboBoxElement).sendKeys(Keys.CONTROL);
				}
			}
		}
	}

	/**
	 * multiSelect using java script.
	 * @param id
	 * @param value
	 */
	public void multiSelect(String id ,String value){
		JavascriptExecutor js = (JavascriptExecutor) driver;	
		js.executeScript("document.getElementById('"+id+"').value='"+value+"'");
		waitNSec(1);
	}	

	/**
	 * de selects any of the option from the list
	 * @param object
	 * @param value
	 * @throws InterruptedException 
	 */
	public void deselectItem(WebElement object,String value){
		Select listbox=new Select(object);
		listbox.deselectByVisibleText(value);
		waitNSec(2);
	}

	/**
	 * Returns table row size 
	 * @param object(x path of table)
	 * @return integer(row count)
	 */
	public int webtableGetRowCount(String tableobject){
		tr_collection=driver.findElements(By.xpath(OR.getProperty(tableobject)+"/tbody/tr"));
		return tr_collection.size();     
	}

	/**
	 * Returns column count in a table
	 * @param object(x path of table)
	 * @return integer(column count)
	 */
	public int webtableGetColCount(String tableobject){
		int colcnt=0;
		try{
			colcnt=driver.findElements(By.xpath(OR.getProperty(tableobject)+"/tbody/tr[1]/descendant::td")).size();
		}
		catch(Exception e)
		{
			Assert.fail("Column not found");
		}
		return colcnt;
	}

	/**
	 * Returns cell value of a table with given row and column
	 * @param object(xpath of table)
	 * @param rownum
	 * @param colnum
	 * @return String
	 */
	public String webtableGetCellData(String tableobject,int rownum,int colnum){
		String getCellData=null;
		try{
			getCellData=driver.findElement(By.xpath(OR.getProperty(tableobject)+"/tbody/tr["+rownum+"]/td["+colnum+"]")).getText();
		}
		catch(Exception e)
		{
			Assert.fail("Error with returning cell data");
		}
		return getCellData;
	}

	/**
	 * webtableGetRowWithCellData
	 * @param object(xpath of table)
	 * @param celldata
	 * @param colnum
	 * @return integer(row number)
	 */
	public int webtableGetRowWithCellData(String tableobject,String celldata,int colnum){
		int rownum=0;String tbldata;List<WebElement> linksub;
		try{
			for (int i=1;i<=webtableGetRowCount(tableobject);i++){
				tbldata=webtableGetCellData(tableobject,i,colnum);
				if(celldata.equalsIgnoreCase(tbldata)){
					rownum=i;
					break;
				}
				//driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
				linksub=driver.findElement(By.xpath(OR.getProperty(tableobject)+"/tbody/tr["+i+"]/td[1]")).findElements(By.xpath(".//img[contains(@id,'linkSub')]"));
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				if(linksub.size()>0)
					i=i+1;

			}
		}
		catch(Exception e)
		{
			Assert.fail("Error with Get Cell data ");
		}
		return rownum;
	}

	/**
	 * webtableGetColNumOfHeader
	 * @param object(xpath of table)
	 * @param header
	 * @return integer(column number)
	 */
	public int webtableGetColNumOfHeader(String object,String header){
		int colcnt=0;String colhdr;int colnum=0;
		try{
			colcnt=driver.findElements(By.xpath(OR.getProperty(object)+"/thead/tr/descendant::th")).size();
			for(int i=3;i<=colcnt;i++){
				colhdr=driver.findElement(By.xpath(OR.getProperty(object)+"/thead/tr/th["+i+"]")).getText();
				if(header.equalsIgnoreCase(colhdr)){
					colnum=i;
					break;
				}
			}
		}
		catch(Exception e)
		{
			Assert.fail("Error with Get Cell number of header data ");
		}
		return colnum;
	}

	/**
	 * webtableSelectCheckbox
	 * @param tableobject
	 * @param celldata
	 * @param colname
	 * @param checkboxcolnum
	 * @return 
	 */
	public void webtableSelectCheckbox(String tableobject,String celldata,String colname,int checkboxcolnum){
		int rownum=0,colnum=0;
		try{
			colnum=webtableGetColNumOfHeader(tableobject,colname);
			rownum=webtableGetRowWithCellData(tableobject,celldata,colnum);
			driver.findElement(By.xpath(OR.getProperty(tableobject)+"/tbody/tr["+rownum+"]/td["+checkboxcolnum+"]/descendant::input[contains(@type,'checkbox')]")).click();
		}catch(Exception e)
		{
			Assert.fail("Error with Get Cell check box ");
		}
	}



	/**
	 * Sets clipboard data
	 * @param string
	 */
	public static void setClipboardData(String string) {
		StringSelection stringSelection = new StringSelection(string);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
	}

	public static boolean isNumeric(String string){
		//	return string.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(string, pos);
		return string.length() == pos.getIndex();
	}

	/**
	 * File download using AutoIT 
	 * Note: Now we are not using , so u can refer "xml_file_download(boolean read, String browser, String xml_path)"
	 * @param read
	 * @throws Exception
	 */
	public void xml_file_download(boolean read, String browser, String xml_path) throws Exception{
		try{
			waitForCommonElementPresent("settings_xpath");
			getCommonObject("settings_xpath").click();
			waitForCommonElementPresent("restore_default_xpath");
			getCommonObject("export_dashboard_xpath").click();			
			waitNSec(3);			
			Process proc = Runtime.getRuntime().exec(CommonConstants.autoItPath+autiItExe);
			waitNSec(5);			
			proc.destroy();
			logger.info("XML File download completed.");
			waitNSec(3);	
			fXmlFile = new File(xml_path);
			if (fXmlFile.exists()){
				logger.info("Verify that a xml file is downloaded");
				if(!read)
					fXmlFile.delete();
			} else
				logger.info("Verify that a xml file is not downloaded");

		}
		catch(Exception e)
		{
			Assert.fail("Error with Get Cell data ");
		}
	}

	/**
	 * Read XML file 
	 * @throws InterruptedException 
	 * @throws AWTException 
	 */
	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	}



	/**
	 * Read the xml file
	 * @param file
	 * @param str
	 * @param contnt
	 * @throws ParserConfigurationException
	 * @throws Exception
	 * @throws IOException
	 */
	public void read_xml_file(File file,String str, String contnt) throws ParserConfigurationException, Exception, IOException{
		count=0;check=0;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName(str);
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				content.add(getTagValue("content_name", eElement));	      

				if(contnt.equals(content.get(count))){
					check =count;
					break;
				}
			}

			count++;
		}
		Assert.assertEquals(contnt, content.get(check));
	}

	/**
	 * Switch to the next window
	 * @throws InterruptedException
	 */
	public void switchToWindow() 
	{
		try{
			waitForWindow();
			windowIds = driver.getWindowHandles();			
			Iterator<String> wndNavigator = windowIds.iterator();
			ParentWindow = wndNavigator.next();
			String popupWindow = wndNavigator.next();			
			driver.switchTo().window(popupWindow);
		}
		catch(Exception e)
		{
			Assert.fail("Error with Switch to Window ",e);
		}
	}
	/**
	 * Verify the sting present in the bread crumb
	 * @param text
	 */
	public void verifyBreadCrumb(String text) 
	{
		Assert.assertTrue(getCommonObject("breadcrumb_id").getText().contains(text));
	}

	/**
	 * Verify the sting present in the bread crumb without delay
	 * @param text
	 */
	public void verifyBreadCrumb(String locator,String text) 
	{
		try
		{
			waitForTxtPresent(locator, text);
			Assert.assertTrue(getObject(locator).getText().contains(text));
		}catch (Throwable t) {
			Assert.fail("Failed during verifyBreadCrumb validation");
		}
	}

	/*  To Verify the expected Tab name is not present  */
	public void isTabNotPresent(String tabName){
		tab_container = getObject("tab_names_css");
		options = tab_container.findElements(By.tagName("li"));
		for(WebElement tabname : options){
			value = tabname.getText();
			if (!value.equalsIgnoreCase(tabName)){
				Assert.assertFalse(tabName.equals(value));				
			}	
		}
	}

	/*  To Verify the expected Tab name is present  */
	public void isTabPresent(String tabName){
		tab_container = getObject("tab_names_css");
		options = tab_container.findElements(By.tagName("li"));

		for(WebElement tabname : options){
			value = tabname.getText();
			if (value.equalsIgnoreCase(tabName)){
				Assert.assertEquals(value, tabName);
			}	
		}
	}


	/**
	 *This API will compare two image file 
	 *return true if both image files are equal else return false*/

	public static boolean compareImage(File fileA, File fileB) {        
		try {
			// take buffer data from both image files //
			BufferedImage biA = ImageIO.read(fileA);
			DataBuffer dbA = biA.getData().getDataBuffer();
			int sizeA = dbA.getSize();                      
			BufferedImage biB = ImageIO.read(fileB);
			DataBuffer dbB = biB.getData().getDataBuffer();
			int sizeB = dbB.getSize();
			// compare data-buffer objects //
			if(sizeA == sizeB) {
				for(int i=0; i<sizeA; i++) { 
					if(dbA.getElem(i) != dbB.getElem(i)) {
						return false;
					}
				}
				return true;
			}
			else {
				return false;
			}
		} 
		catch (Exception e) { 
			logger.info("Failed to compare image files ...");
			return  false;
		}
	}


	/**
	 * Do Restore Default setting on Dashboard page.
	 * 
	 */
	public void restoreDefault() {
		try{
			//Restore Default setting.
			logger.info(" Restore Default setting.");
			waitForCommonElementPresent("settings_xpath");
			getCommonObject("settings_xpath").click();
			waitForCommonElementPresent("restore_default_xpath");
			getCommonObject("restore_default_xpath").click();
			waitForCommonElementPresent("restore_ok_xpath");
			getCommonObject("restore_ok_xpath").click();
		} catch(Throwable e) {
			logger.info(" Exception "+e);
			Assert.fail("Error while restoring the Dashboard page.");
		}
	}

	/**
	 * Method for Scrolling Up/Down
	 * @throws InterruptedException 
	 * 
	 */
	public void scrollDown(String xCoord,String yCoord) {
		driver.switchTo().defaultContent();
		js = (JavascriptExecutor) driver;
		js.executeScript("scroll('"+xCoord+"','"+yCoord+"')");
		waitNSec(2);
	}
	/**
	 * Method to verify n number of elements displayed
	 * @param expectedArr
	 */
	public void verifyElementsDisplayed(String expectedArr[]){
		for (int i=0;i<expectedArr.length;i++){
			Assert.assertTrue(getObject(expectedArr[i]).isDisplayed(),expectedArr[i]+" is not displayed");
			logger.info(expectedArr[i]+" is displayed");
		}
	}


	/**
	 * TODO-------------------------Wait Related Methods--------------------------------------------------------
	 */

	/**
	 * waits for Dash board page to load
	 * @param locator
	 * @return
	 */
	public  boolean waitForAjaxLoadImg(String locator) 
	{                
		boolean exists = false;
		try{
			for(int i=0;i<25;i++) 
			{
				if(!isElementPresent(locator))
				{   
					exists = true;										
					break;
				}
				else
				{	
					waitNSec(1);
				}
			}
		}
		catch(Exception e)
		{
			waitNSec(1);
		}
		return exists;
	}

	/**
	 * wait for 1 second
	 */
	public void waitNSec(int waitTime) {
		try {
			switch (waitTime) {
			case 1:
				Thread.sleep(CommonConstants.oneSec);
				break;
			case 2:
				Thread.sleep(CommonConstants.twoSec);
				break;
			case 3:
				Thread.sleep(CommonConstants.threeSec);
				break;
			case 4:
				Thread.sleep(CommonConstants.fourSec);
				break;
			case 5:
				Thread.sleep(CommonConstants.fiveSec);
				break;
			case 6:
				Thread.sleep(CommonConstants.sixSec);
				break;
			case 7:
				Thread.sleep(CommonConstants.sevenSec);
				break;			
			default:
				Assert.fail("Please specify wait time within 10. Currently given is: " + waitTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
	}

	/** The waitForElementPresent function will wait for the element for a
	 * default duration of customized seconds To increase or decrease this time
	 * change the value of the integer 'timeoutSec' in "Common.java"  * 
	 * @param Locator
	 * @param driver
	 */
	public  boolean waitForCommonElementPresent(String locator) {
		driver.manage().timeouts().pageLoadTimeout(180, TimeUnit.SECONDS);
		for( i=0;i<30;i++)  {   
			try{
				if(getCommonObject(locator).isDisplayed())   {  
					exists = true;
					break;
				} else {
					waitNSec(1);
				}
			} catch(Exception e){
				waitNSec(1);
			}
		}
		return exists;
	}

	/** The waitForElementPresent function will wait for the element for a
	 * default duration of customized seconds To increase or decrease this time
	 * change the value of the integer 'timeoutSec' in "Common.java"  * 
	 * @param Locator
	 * @param driver
	 */
	public  boolean waitForElementPresent(String locator) {  
		for(i=0;i<30;i++)  {   
			try{
				if(getObject(locator).isDisplayed())   {                                       
					exists = true;
					break;
				} else {
					waitNSec(1);
				}
			} catch(Exception e){
				waitNSec(1);
			}
		}
		return exists;
	}
	/**
	 * wait for element with xpath displayed
	 * @param xpath
	 * @return
	 */
	public  boolean waitForelementdisplayed(String xpath) {            
		exists = false;
		for(i=0;i<30;i++)  { 
			try{
				if(getObjectDirect(By.xpath(xpath)).isDisplayed())   {                                       
					exists = true;
					break;
				}else{
					waitNSec(1);
				}
			}
			catch(Exception e){
				waitNSec(1);
			}
		}
		return exists;
	}

	/** The waitForTxttPresent function will wait for the element text for a
	 * default duration of customized seconds To increase or decrease this time
	 * change the value of the integer 'timeoutSec' in "Common.java"  * 
	 * @param Locator
	 */
	public  boolean waitForTxtPresent(String locator, String text) {  
		for(i=0; i<30; i++)  { 
			try {
				if(getObject(locator).getText().contains(text)) {                                       
					exists = true;
					break;
				}else {
					waitNSec(1);
				}
			}catch(Exception e){
				waitNSec(1);
			}
		}
		return exists;
	}

	/** The waitForElementNotPresent function will wait for the element not present for a
	 * default duration of customized seconds To increase or decrease this time
	 * change the value of the integer 'timeoutSec' in "Common.java"  * 
	 * @param Locator
	 * @param driver
	 */
	public  void waitForElementNotPresent(String object) {
		for(int counter=0;counter<40;counter++)  { 
			if(!(isElementPresent(object))){
				break;
			}
		}
	}
	/*public void waitForElementNotPresent(String locator) {
		for(int counter=0;counter<30;counter++)  {  
			try {
				if(!getObject(locator).isDisplayed())   {  
					break;
				}else{
					waitNSec(1);
				}
			}
			catch(Exception e){
				waitNSec(1);
			}
		} 
	}*/
	/** The waitForElementPresent function will wait for the element for a
	 * default duration of customized seconds To increase or decrease this time
	 * change the value of the integer 'timeoutSec' in "Common.java"  * 
	 * @param Locator
	 * @param driver
	 */
	public  boolean waitForWebElementPresent(WebElement webEle) {  
		for(i=0;i<30;i++)  {   
			try{
				if(webEle.isDisplayed())   {                                       
					exists = true;
					break;
				} else {
					waitNSec(1);
				}
			} catch(Exception e){
				waitNSec(1);
			}
		}
		return exists;
	}
	/**
	 * WaitForElementNtSelected 
	 * Returns the 
	 * @param object
	 * @return x
	 */
	public boolean WaitForElementNtSelected(String object){
		for(int i=0; i<30; i++)  {  
			try {
				if(object.contains("link"))
					x =	!(driver.findElement(By.linkText(OR.getProperty(object))).isSelected());
				else if(object.contains("xpath"))
					x	= !(driver.findElement(By.xpath(OR.getProperty(object))).isSelected());
				else if(object.contains("css"))
					x= !(driver.findElement(By.cssSelector(OR.getProperty(object))).isSelected());
				else if(object.contains("id"))
					x= !(driver.findElement(By.id(OR.getProperty(object))).isSelected());
				else if(object.contains("name"))
					x=!(driver.findElement(By.name(OR.getProperty(object))).isSelected());	
				else
					Assert.fail(" Locator Format is not Valid.");
				if (x) 
					return x;
			} catch(Exception e){
				waitNSec(1);
			}
		}
		return x;
	}
	/**
	 * WaitForElementSelected 
	 * Returns the 
	 * @param object
	 * @return x
	 */
	public boolean WaitForElementSelected(String object){
		for(int i=0; i<30; i++)  {  
			try {
				if(object.contains("link"))
					x =	driver.findElement(By.linkText(OR.getProperty(object))).isSelected();
				else if(object.contains("xpath"))
					x	= driver.findElement(By.xpath(OR.getProperty(object))).isSelected();
				else if(object.contains("css"))
					x= driver.findElement(By.cssSelector(OR.getProperty(object))).isSelected();
				else if(object.contains("id"))
					x= driver.findElement(By.id(OR.getProperty(object))).isSelected();
				else if(object.contains("name"))
					x=driver.findElement(By.name(OR.getProperty(object))).isSelected();	
				else
					Assert.fail(" Locator Format is not Valid.");
				if (x) 
					return x;
			} catch(Exception e){
				waitNSec(1);
			}
		}
		return x;
	}
	/** The waitForTxtNotPresent function will wait for the element text for a
	 * default duration of customized seconds To increase or decrease this time
	 * change the value of the integer 'timeoutSec' in "Common.java"	 * 
	 * @param Locator
	 */
	public  boolean waitForTxtNotPresent(String locator, String text) {            
		boolean exists = false;
		for(i=0;i<30;i++)  {      
			try {
				if(!(getObject(locator).getText().contains(text)))   {                                       
					exists = true;
					break;
				}else{
					waitNSec(1);
				}
			}catch(Exception e){
				waitNSec(1);
			}
		}
		return exists;
	}

	/** The waitForvaluePresent function will wait for the element val for a
	 * default duration of customized seconds To increase or decrease this time
	 * change the value of the integer 'timeoutSec' in "Common.java"  * 
	 * @param Locator
	 */
	public  boolean waitForValPresent(String locator, String text) {  
		for(i=0; i<30; i++)  { 
			try {
				if(getObject(locator).getAttribute("value").contains(text)) {  
					exists = true;
					break;
				}else {
					waitNSec(1);
				}
			}catch(Exception e){
				waitNSec(1);
			}
		}
		return exists;
	}

	/**
	 * WaitForElementDisplayed till the element is displayed
	 * Returns the 
	 * @param object
	 * @return x
	 */
	public void WaitForElementDisplayed(final String object){
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>()
				{
			public Boolean apply(WebDriver driver) {
				boolean x = false;
				try{
					if(object.contains("link"))
						x=	 driver.findElement(By.linkText(OR.getProperty(object))).isDisplayed();
					else if(object.contains("xpath"))
						x	= driver.findElement(By.xpath(OR.getProperty(object))).isDisplayed();
					else if(object.contains("css"))
						x= driver.findElement(By.cssSelector(OR.getProperty(object))).isDisplayed();
					else if(object.contains("id"))
						x= driver.findElement(By.id(OR.getProperty(object))).isDisplayed();
					else if(object.contains("name"))
						x=driver.findElement(By.name(OR.getProperty(object))).isDisplayed();	
					else if(object.contains("classname"))
						x= driver.findElement(By.className(OR.getProperty(object))).isDisplayed();	
				}
				catch (NoSuchElementException e) {
					return x= false;
				}
				return x;
			}});
	}

	/**
	 * WaitForElementClickable till the element is clickable
	 * Returns the 
	 * @param object
	 * @return
	 */
	public void WaitForElementClickable(String object){
		WebDriverWait wait = new WebDriverWait(driver, 10);
		try{
			if(object.contains("link"))
				wait.until(ExpectedConditions.elementToBeClickable(By.linkText(OR.getProperty(object))));
			else if(object.contains("xpath"))
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath(OR.getProperty(object))));
			else if(object.contains("css"))
				wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(OR.getProperty(object))));
			else if(object.contains("id"))
				wait.until(ExpectedConditions.elementToBeClickable(By.id(OR.getProperty(object))));
			else if(object.contains("name"))
				wait.until(ExpectedConditions.elementToBeClickable(By.name(OR.getProperty(object))));
			else if(object.contains("classname"))
				wait.until(ExpectedConditions.elementToBeClickable(By.className(OR.getProperty(object))));
		}
		catch (NoSuchElementException e) {
			logger.error("Cannot find object with key -- " + object);			
			Assert.fail("Cannot find object with key -- "+ object);
		}
	}

	/** The waitForTitlePresent function will wait for the element for a
	 * default duration of customized seconds To increase or decrease this time
	 * change the value of the integer 'timeoutSec' in "Common.java"  * 
	 * @param Locator
	 * @param driver
	 */
	public  boolean waitForTitlePresent(String title) {  
		for(i=0;i<30;i++)  {   
			try{
				if(driver.getTitle().contains(title))   {                                       
					exists = true;
					break;
				} else {
					waitNSec(1);
				}
			} catch(Exception e){
				waitNSec(1);
			}
		}
		return exists;
	}
	/** The waitForTitlePresent function will wait for the element for a
	 * default duration of customized seconds To increase or decrease this time
	 * change the value of the integer 'timeoutSec' in "Common.java"  * 
	 * @param Locator
	 * @param driver
	 */
	public  boolean waitForTitleNotPresent(String title) {  
		boolean nonexists = false;
		for(i=0;i<30;i++)  {   
			try{
				if(!driver.getTitle().contains(title))   {                                       
					nonexists = true;
					break;
				} else {
					waitNSec(1);
				}
			} catch(Exception e){
				waitNSec(1);
			}
		}
		return nonexists;
	}

	/**
	 * Method to wait for multiple windows is present
	 * @return
	 */
	public  boolean waitForWindow() 
	{                
		boolean exists = false;
		try{
			for(int i=0;i<30;i++) 
			{
				windowIds = driver.getWindowHandles();
				if(windowIds.size()>1)
				{   
					exists = true;
					break;
				}else
				{
					waitNSec(1);
				}
			}
			System.out.println(i);
		}
		catch(Exception e)
		{
			waitNSec(1);
		}
		return exists;
	}


	/**
	 * TODO---------------------Verification Methods--------------------------------------------------------
	 * 
	 */

	/**
	 * Verify Element is not selected
	 *  
	 * 
	 */
	public void verifyElementNotSelected(String element) 
	{
		Assert.assertFalse(getObject(element).isSelected());
	}

	/**
	 * Verify Element is selected
	 * 
	 */	
	public void verifyElementSelected(String element)
	{
		Assert.assertTrue(getObject(element).isSelected());
	}	

	/**
	 * Verify Element is enabled
	 * 
	 */	
	public void verifyEltEnabled(String object)
	{
		Assert.assertTrue(getObject(object).isEnabled());

	}

	/**
	 * Verify Element is disabled
	 * 
	 */	
	public void verifyEltDisabled(String object)
	{
		Assert.assertFalse(getObject(object).isEnabled());
	}

	/**
	 * Verify Element is not present in the page source
	 * 
	 */	
	public void verifyElementNotPresent(String element)
	{
		Assert.assertFalse(driver.getPageSource().contains(element),"Verifying "+element +" is present in the page");
	}

	/**
	 * It's for common method for check page source objects that is not displayed
	 * @param commobject
	 * @return
	 */
	public void getContainsNot(String str){
		try{
			Assert.assertFalse(driver.getPageSource().contains(str), "Expected '"+str+"' is not displayed");
		}catch (Exception e)
		{					
			logger.error("Failed during getContainsNot validation");
			Assert.fail("Failed during getContainsNot validation");
		}
	}	

	/**
	 * Verify Element is displayed with single parameter
	 */	
	public void verifyElementDisp(String element)
	{
		Assert.assertTrue(getObject(element).isDisplayed(), "Expected element is not displayed");
		logger.info("Expected element is displayed.");

	}

	/**
	 * Verify Common Element is displayed with single parameter
	 */	
	public void verifyElementDispCommon(String element)
	{
		Assert.assertTrue(getCommonObject(element).isDisplayed(), "Expected element is not displayed");
		logger.info("Expected element is  displayed");
	}

	/**
	 * Verify Common Element is displayed with single parameter
	 */	
	public void verifyElementNotDisp(String element)
	{
		Assert.assertFalse(getObject(element).isDisplayed(), "Expected element is not displayed"+element);
		logger.info("Expected element is  displayed");

	}


	/**
	 * Verify Element Text is present
	 * 
	 */	
	public void verifyTextPresent(String object, String text)
	{
		Assert.assertTrue(getObject(object).getText().equalsIgnoreCase(text));
	}

	/**
	 * Verify Element Text is present Common
	 * 
	 */	
	public void verifyTextPresentCommon(String object, String text)
	{
		Assert.assertTrue(getCommonObject(object).getText().equalsIgnoreCase(text),text+"is not present");
		logger.info("Verified the "+text+"is present");
	}

	/**
	 * Verify Element Text is present
	 * 
	 */	
	public void verifyTextNotPresent(String object, String text)
	{
		try
		{
			Assert.assertFalse(getObject(object).getText().equalsIgnoreCase(text),text+"is present");
			logger.info("Verified the "+text+"is not present");
		}catch (Exception e)
		{					
			logger.error("Failed during verifyTextNotPresent validation");
			Assert.fail("Failed during verifyTextNotPresent validation");
		}
	}

	/**
	 * Verify Element Text is present
	 * 
	 */	
	public void verifyTextContains(String object, String text)
	{
		try
		{
			Assert.assertTrue(getObject(object).getText().trim().contains(text),text+" is not present");
			logger.info("Verified the "+text+" is present");
		}catch (Exception e)
		{					
			logger.error("Failed during verifyTextContains validation");
			Assert.fail("Failed during verifyTextContains validation");
		}
	}

	/**
	 * Verify Element Text is present
	 * 
	 */	
	public void verifyTextNotContains(String object, String text)
	{
		try
		{
			Assert.assertFalse(getObject(object).getText().contains(text),text+" is not present");
			logger.info("Verified the "+text+" is present");
		}catch (Exception e)
		{					
			logger.error("Failed during verifyTextNotContains validation");
			Assert.fail("Failed during verifyTextNotContains validation");
		}

	}

	/**
	 * Verify Element Text is present
	 * 
	 */	
	public void verifyTextContainsCommon(String object, String text)
	{
		Assert.assertTrue(getCommonObject(object).getText().contains(text),text+" is not present");
		logger.info("Verified the "+text+" is present");

	}

	/**
	 * Verify Element Text is present
	 * 
	 */	
	public void verifyTextEquals(String object, String exptext)
	{
		Assert.assertEquals(getObject(object).getText().trim(),exptext,exptext+" is not present");
		logger.info("Verified the "+exptext+" is present");

	}

	/**
	 * Verify Attribute Type
	 * @param locator
	 * @param type
	 */
	public void vAttributeType(String locator,String type){
		Assert.assertTrue(getObject(locator).getAttribute("type").contains(type));
	}

	/**
	 * TODO---------------------CardPayment Related  Methods--------------------------------------------------------
	 * 
	 */
	/**
	 * restores table configuration
	 * 
	 */
	public void restoreTables() 
	{
		getCommonObject("restore_config_xpath").click();
		getCommonObject("resttable_ok_xpath").click();		
		//wait2Sec();
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		logger.info("Reset the table configuration.");
	}

	/**
	 * Verifies if any specific option is present in select element
	 * @param selectLocator
	 * @param option
	 * @return
	 */
	public boolean verifySelectOption(String selectLocator, String option)
	{
		boolean flag = false;
		Select appTypedd = new Select(getObject(selectLocator));
		List<WebElement> Options = appTypedd.getOptions();
		try
		{
			for(int index=0; index<Options.size(); index++)
			{
				if(Options.get(index).getText().equals(option))
				{
					flag = true;					
				}
			}
		}catch (Throwable t) {
			Assert.fail("Failed during verifySelectOption validation");
		}
		return flag;
	}

	/**
	 * getIndexForHeader fetches index of specified header
	 * @param header
	 * @return int
	 */
	public int getIndexForColHeader(String locator, String header){
		String xpath = getPath(locator).replace("/th/div", "");		
		WebElement tableHeader = getObjectDirect(By.xpath(xpath));
		List<WebElement> headerCols = tableHeader.findElements(By.tagName("th"));
		for(int i=0;i<headerCols.size();i++){
			if(headerCols.get(i).getText().equals(header))
				return i;
		}
		return -1;
	}

	/**
	 * This Method is used for Saving the WebTable 
	 */
	public void saveWebTable(){
		waitForCommonElementPresent("save_column_id");
		getCommonObject("save_column_id").click();
		waitForCommonElementPresent("alert_ok_xpath");
		getCommonObject("alert_ok_xpath").click();
		waitNSec(3);
		//driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		logger.info("Saved the changes of WebTable");
	}

	/**
	 * go to lowest level
	 * @param locator
	 */
	public void goToMidLevel(String locator)
	{
		getObject(locator).click();
		getObject(locator).click();
		getObject(locator).click();
	}	


	/**
	 * verifies tab presence 
	 */
	public void verifyTabPresence(String tabLocator)
	{
		if(isElementPresent(tabLocator))
		{
			getObject(tabLocator).click();	
			logger.info("Card Type tab is displayed");
		}
		else
		{
			logger.info("Card Type tab is not displayed to execute test");
			Assert.fail("Card Type tab is not displayed to execute test");
		}
	}

	/**
	 * navigates to a level and get the level clicked
	 * @param locator
	 * @return
	 */
	public String navigateToLevel(String locator)
	{
		level = getObject(locator).getText();
		getObject(locator).click();
		return level;
	}


	/**
	 * TODO---------------------DashBoard Related  Methods--------------------------------------------------------
	 * 
	 */

	/**
	 * Edit box
	 */
	public void editBox(String index){
		try{
			String xpath;
			//mouse over the box menu 
			xpath=getPath("box_options_xpath").replace("INDEX", index);	
			waitForelementdisplayed(xpath);
			WebElement myElement =getObjectDirect(By.xpath(xpath));
			Actions builder = new Actions(driver);
			builder.moveToElement(myElement).build().perform();

			//click on the edit box link
			xpath=getPath("box_edit_xpath").replace("INDEX", index);  
			waitForelementdisplayed(xpath);
			getObjectDirect(By.xpath(xpath)).click();	
			logger.info("clicked on edit box");
		}
		catch(Exception e)
		{
			logger.error("Error during editing the box");	
			Assert.fail("Error during editing the box");
		}
	}

	/**
	 * Method to wait for the Created Graph
	 * @param locator
	 * @param box
	 */
	public void waitForBoxName(String locator,String box){
		String boxno=getBoxNumber(box);		
		String xpath=getPath(locator).replace("BOXNO", boxno);		
		waitForelementdisplayed(xpath);
	}

	/**
	 * Delete Dash board Box
	 * @param title
	 * @throws InterruptedException
	 */
	public void deleteGraph(String title) throws InterruptedException 
	{
		try{
			WebElement boxelement;
			String xpath,index;
			xpath = (getPath("any_title_xpath").replace("Title", title));
			index = getObjectDirect(By.xpath(xpath)).getAttribute("id");
			index = index.replaceAll("[A-z]", "");
			xpath = getPath("delete_dashboard_xpath").replace("INDEX", index);
			boxelement = getObjectDirect(By.xpath(xpath));
			boxelement.click();
			Alert alt = driver.switchTo().alert();
			elementText = alt.getText();
			waitNSec(2);
			Assert.assertEquals(elementText,"Do you really want to delete this content?");			
			alt.accept();
			waitNSec(2);
			refresh();
			logger.info("Deleted the created box "+title);

		}catch (Throwable t) 
		{
			Assert.fail("Failed during deleteGraph validation");
		}

	}

	/**
	 * enters graph title
	 * @param locator1
	 * @param title
	 * @param locator2
	 */
	public void enterGraphTitle(String locator1,String title,String locator2){
		getObject(locator1).clear();
		getObject(locator1).sendKeys(title);
		getObject(locator2).click();
	}	

	/**
	 * select CP/GPRS radio button to create box
	 * @param modLocator
	 */
	public void chooseYourMod(String modLocator){
		getObject("add_content_id").click();
		waitForElementPresent(modLocator);
		getObject(modLocator).click();		
		waitForElementPresent("next_xpath");
		getObject("next_xpath").click();
	}


	/**
	 * verifies all options in drop down equals to predefined values
	 * @param selectLocator
	 * @param arr
	 */
	public void verifySelectOptions(String selectLocator, String [] arr)
	{
		List<WebElement> options = new Select(getObject(selectLocator)).getOptions();
		logger.info("Verify all the options");
		try
		{
			for(int i=0;i<arr.length;i++)
			{
				logger.info(arr[i]+"   "+options.get(i).getText().trim());
				Assert.assertEquals(arr[i], options.get(i).getText().trim());
			}
		}catch (Throwable t) {
			Assert.fail("Failed during verifySelectOptions validation");
		}
	}


	/**
	 * clicks on graph offer
	 * @param locator1
	 * @param locator2
	 * @param locator3
	 */
	public void graphOffer(String locator1,String locator2,String locator3){
		waitForElementPresent(locator1);
		getObject(locator1).click();
		waitForElementPresent(locator2);
		getObject(locator2).click();
		waitForElementPresent(locator3);
		getObject(locator3).click();
	}

	/**
	 * Verifying Data is present or not
	 * @param locator
	 * @throws InterruptedException
	 */
	public void verifyIfnoDataCommon(String locator,String mod)
	{
		String xpath =getCommonPath(locator).replace("MODULE", mod);

		if(getObjectDirect(By.xpath(xpath)).getText().contains(noResultTxt))
		{
			Assert.fail(noResultTxt);
		}	
	}

	/**
	 * Change title of the edited box
	 * @param index
	 * @param newTitle
	 */
	public void changeTitleAndClose(String index,String newTitle)
	{
		String xpath;
		xpath=getPath("edited_dashboard_title_xpath").replace("INDEX", index); 
		waitForelementdisplayed(xpath);
		getObjectDirect(By.xpath(xpath)).clear();
		getObjectDirect(By.xpath(xpath)).sendKeys(newTitle);
		logger.info("close edited window");
		xpath=getPath("edited_ok_dashboard_xpath").replace("INDEX", index);
		waitForelementdisplayed(xpath);
		getObjectDirect(By.xpath(xpath)).click();
		waitForTitlePresent(newTitle);
	}


	/**
	 * Get Index of Box
	 * @param BoxName
	 * @return
	 */
	public String getBoxIndex(String BoxName)
	{
		String xpath,index = null;
		try
		{
			xpath=getPath("box_index_xpath").replace("BOXNAME",BoxName );
			waitForelementdisplayed(xpath);
			index =getObjectDirect(By.xpath(xpath)).getAttribute("id");
			index =index.replaceAll("[A-z]","");
			return index;
		}catch (Throwable t) {
			Assert.fail("Failed during getBoxIndex validation");
		}
		return index;

	}

	/**
	 * Get box number
	 * @param BoxName
	 * @return
	 */
	public String getBoxNumber(String BoxName)
	{
		String xpath,boxno = null;
		try
		{
			xpath=getPath("box_boxno_xpath").replace("BOXNAME",BoxName );
			waitForelementdisplayed(xpath);
			boxno =getObjectDirect(By.xpath(xpath)).getAttribute("id");
			boxno =boxno.replaceAll("[A-z]","");
			return boxno;
		}catch (Throwable t) {
			Assert.fail("Failed during getBoxIndex validation");
		}
		return boxno;
	}

	/**
	 * Create duplicate box
	 * @param index
	 */
	public void duplicateBox(String index){
		try{
			String xpath;
			//mouse over the box menu 
			xpath=getPath("box_options_xpath").replace("INDEX", index);	
			waitForelementdisplayed(xpath);
			WebElement myElement =getObjectDirect(By.xpath(xpath));
			Actions builder = new Actions(driver);
			builder.moveToElement(myElement).build().perform();

			//click on the duplicate link
			xpath=getPath("box_duplicate_xpath").replace("INDEX", index);   
			waitForelementdisplayed(xpath);
			getObjectDirect(By.xpath(xpath)).click();
			logger.info("Duplicate box created");
		}
		catch(Exception e)
		{
			logger.error("Problem during duplicating box");	
			Assert.fail("Problem during duplicating box");
		}
	}

	/**
	 * Verify Image presence with created box
	 * @param inde
	 */
	public void verifyImagePresence(String inde)
	{
		boolean result;
		path=getPath("box_icon_xpath").replace("INDEX", inde);
		result=driver.findElement(By.xpath(path)).isDisplayed();
		Assert.assertTrue(result);
	}

	/**
	 * Box creation validation
	 * @param locator
	 * @param titleName
	 */
	public void boxCreationValidation(String locator,String titleName)
	{
		waitForBoxName(locator,titleName);
		getContains(titleName);			
		logger.info("Graph is displayed in Dashboard after creation "+titleName);
	}

	/**
	 * Create DB box
	 * @param locator1
	 * @param locator2
	 * @param titleName
	 * @param perid
	 * @throws InterruptedException
	 */
	public void createDBBox(String modLoc,String locator1,String locator2,String nextLoc) throws InterruptedException
	{
		dashboardPageNavigator();
		chooseYourMod(modLoc);
		selectItem(getObject("country_id"), countryFran);
		getObject("sellev_next_id").click();
		getObject(locator1).click();
		waitForElementPresent(nextLoc);
		getObject(locator2).click();
		getObject(nextLoc).click();
	}

	/**
	 * Verifies that default name of graph
	 * @param oldTitle
	 * @param titleName
	 * @throws InterruptedException
	 */
	public void vDefaultNameAndTitle(String oldTitle,String titleName) throws InterruptedException
	{
		//Verify that default name of graph 
		waitForElementPresent("contenttitle_xpath");
		Assert.assertEquals(oldTitle,getObject("contenttitle_xpath").getAttribute("value"));
		logger.info("Verified that default name of graph is "+oldTitle);

		enterGraphTitle("boxtitle_id", titleName, "OK_link");
		logger.info("Created the Dashboard graph "+titleName);
		refresh();
	}

	/**
	 * Verifies the warning message
	 * @param msg
	 */
	public void vWarningMessage(String msg)
	{
		warMess=getObject("usage_warning_xpath").getText();
		Assert.assertTrue(warMess.contains(msg));
		getObject("usage_warning_ok_xpath").click();
	}
	/**
	 * TODO---------------------Scheduled Reports  Related  Methods--------------------------------------------------------
	 * 
	 */

	/**
	 * Common Method for Canceling any Subscription
	 * @param report
	 * @throws InterruptedException
	 */
	public void cancelSubscription(String report) {
		String xpath;
		xpath=getPath("report_chkbox_xpath").replace("PERIOD", report);
		//wait1Sec();
		waitForelementdisplayed(xpath);
		getObjectDirect(By.xpath(xpath)).click();
		//wait1Sec();
		waitForelementdisplayed(xpath);
		getObject("cancelsubscription_button_id").click();
		getObject("alert_ok_link").click();
		logger.info("Deleted the report "+report);
	}

	/**
	 * Method to select radio button or checkbox if not selected
	 */
	public void vChkBoxRdBtnNotSelected(String locator){
		if(!getObject(locator).isSelected()){
			getObject(locator).click();
		}
	}

	/**
	 * Method to select radio button or checkbox if not selected
	 */
	public void vChkBoxRdBtnisSelected(String locator){
		if(getObject(locator).isSelected()){			
			getObject(locator).click();
		}
	}

	/**
	 * Closing browser
	 * 
	 */
	@AfterClass
	public void tearDown(){
		logout();
		driver.quit();
	}

	/**
	 * Click on download csv or PDF link
	 * @param index
	 * @throws InterruptedException
	 */
	public void csvORpdfDownloadBox(String locator,String index) {
		try{
			String xpath;
			Actions builder;
			WebElement myElement;
			//mouse over the box menu
			xpath=getPath("box_options_xpath").replace("INDEX", index);		
			myElement =getObjectDirect(By.xpath(xpath));
			builder = new Actions(driver);
			builder.moveToElement(myElement).build().perform();

			//click on the csv download link
			xpath=getPath(locator).replace("INDEX", index); 
			waitForelementdisplayed(xpath);
			getObjectDirect(By.xpath(xpath)).click();
			waitNSec(7);
			logger.info("CSV downloaded sucessfully");
		}
		catch(Exception e)
		{

			logger.error("Problem with downloading CSV or PDF file");	
			Assert.fail("Problem with downloading CSV or PDF file");
		}
	}
	/**Download pdf and check number of lines	 
	 * @param table_xpath,fileName,download_xpath
	 * @return
	 */
	public void downloadPdfAndCheck(String tableXpath,String fileName,String downloadXpath ) throws IOException{

		try{
			WebElement country=	getObjectDirect(By.xpath(tableXpath));
			cList = country.findElements(By.tagName("tr"));
			waitNSec(3);

			//clicking download link
			getObjectDirect(By.xpath(getPath(downloadXpath))).click();
			logger.debug("Clicked on download PDF link");
			waitNSec(3);
			Process proc = Runtime.getRuntime().exec(CommonConstants.autoItPath+autiItExe);
			waitNSec(5);
			logger.info("Downloaded file,  "+fileName);
			proc.destroy();

			//read pdf file
			String pdf= PDF_Reader.readPdf(CommonConstants.filedownloadPath+fileName+".pdf");
			logger.info("PDF read completed");

			//verifying line no is present in pdf file
			logger.info("PDF Lines "+"* Total number of lines: "+cList.size());
			Assert.assertTrue(pdf.contains("* Total number of lines: "+cList.size()));

		}
		catch(Exception e)
		{
			Assert.fail("Problem with downloading PDF "+e);
		}
	}

	/**Verifies Table size and downloads pdf and checks number of lines	 
	 * @param table_xpath,fileName
	 * @return
	 */
	public void downloadPdfAndCheck(String tableXpath,String fileName) throws IOException{
		cList=null;
		int scList=0;
		try{
			noPages=new Select(getObject("no_of_page_xpath")).getOptions();

			//select all the data type
			for(int i=0;i<noPages.size();i++)
			{  
				noPages=new Select(getObject("no_of_page_xpath")).getOptions();
				new Select(getObject("no_of_page_xpath")).selectByVisibleText(noPages.get(i).getText());
				WebElement country=	getObjectDirect(By.xpath(tableXpath));
				if(noPages.size()==1){
					cList = country.findElements(By.tagName("tr"));
				}else{
					cList = country.findElements(By.tagName("tr"));			
					scList	= scList+cList.size();	
				}
			}
			waitNSec(3);

			//clicking download link
			clickOnDownloadCSVPDF("pdf_download_xpath", pdfType);			
			Process proc = Runtime.getRuntime().exec(CommonConstants.autoItPath+autiItExe);
			waitNSec(5);	

			//read pdf file
			String pdf= PDF_Reader.readPdf(CommonConstants.filedownloadPath+fileName+".pdf");
			logger.debug("PDF read completed");

			//verifying line no is present in pdf file
			if(noPages.size()==1){
				Assert.assertTrue(pdf.contains("* Total number of lines: "+cList.size()));
			}else{
				Assert.assertTrue(pdf.contains("* Total number of lines: "+scList));	
			}

			proc.destroy();			
		}
		catch(Exception e)
		{
			Assert.fail("Problem with downloading PDF "+e);
		}
	}

	/**
	 * delete csv file
	 * @param file
	 * @throws IOException
	 */
	public void deleteCSVFile(BufferedReader br,File fileObj) throws IOException
	{
		br.close();
		if(fileObj.exists())
			fileObj.delete();
	}

	/**
	 * clicks on csv download link
	 */
	public void clickOnDownloadCSVPDF(String element, String fileType)
	{
		logger.info(" Download the "+fileType+" file.");
		getObject(element).click();
		waitNSec(5);
	}

	/**
	 * get path of pdf file
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public String getCSVPDFPath(String file,String fileType)
	{
		String filePath = null;
		try
		{
			logger.info(" Read data from "+fileType+" file ");
			filePath = CommonConstants.filedownloadPath+file+"."+fileType;

		}
		catch(Exception e)
		{
			Assert.fail("Failed during getCSVPDFPath validation");
		}
		return filePath;
	}
	/**
	 * closes csv file
	 * @param reader
	 * @param file
	 * @throws IOException
	 */
	public void closeCSV(CSVReader read, File fileObj) throws IOException
	{
		read.close();
		if(fileObj.exists())
			fileObj.delete();
	}


	/**
	 * Read complete PDF File
	 * @param file
	 * @return data
	 * @throws IOException throws IO exception
	 * @throws InterruptedException 
	 */
	public static String readPdf(String file) throws IOException, InterruptedException {
		while (!new File(file).exists()) {
			Thread.sleep(CommonConstants.twoSec);
		}
		PDDocument pddDocument = PDDocument.load(new File(file));
		PDFTextStripper textStripper = new PDFTextStripper();
		String data = textStripper.getText(pddDocument);
		pddDocument.close();
		return data;
	}
	/**
	 * Read image from PDF File
	 * @param pdfpath,imgPath.
	 * @throws IOException throws IO exception
	 */
	public void read_pdfImg(String pdfpath,String imgPath ) throws IOException {
		document = null; 
		try {
			document = PDDocument.load(pdfpath);
		} catch (IOException ex) {
			Assert.fail("pdf did not load",ex);
		}
		pages = document.getDocumentCatalog().getAllPages();
		iter = pages.iterator(); 
		i =1;
		while (iter.hasNext()) {
			page = (PDPage) iter.next();
			resources = page.getResources();
			pageImages = resources.getImages();
			if (pageImages != null) { 
				imageIter = pageImages.keySet().iterator();
				while (imageIter.hasNext()) {
					text = (String) imageIter.next();
					image = (PDXObjectImage) pageImages.get(text);
					image.write2file(imgPath);
					i ++;
				}
			}
		}
		document.close();
	}

	/**
	 * Closes pdf file
	 * @param path
	 */
	public void closePDF(String path)
	{
		if(new File(path).exists()){
			new File(path).delete();
		}
	}


	/**
	 * Closing all the child windows
	 * 
	 */
	public void closeChildWindows()
	{
		try{
			windowIds = driver.getWindowHandles();		
			winArray = windowIds.toArray(new String[0]);		
			if(winArray.length >= 2)
			{			
				parentWindow =winArray[0];
				for(int i=1;i<winArray.length;i++){
					driver.switchTo().window(winArray[i]);
					driver.close();
				}
				driver.switchTo().window(parentWindow);
				logger.info("Closing multiple windows");
			}
		}
		catch(Exception e)
		{
			Assert.fail();
		}
	}

	/**
	 * set time zone in eporal application
	 */
	public void SetTimeZone()	
	{
		try{
			getObject("profile_css").click();
			if(CONFIG.getProperty("country").contentEquals("india"))
			{
				new Select(getObject("timezone_css")).selectByVisibleText("Indian/Maldives");
			}else if(CONFIG.getProperty("country").contentEquals("france")){
				new Select(getObject("timezone_css")).selectByVisibleText("Europe/Paris");	
			}
			getObject("profile_ok_css").click();
			waitNSec(2);
			logger.info("Time Zone set Successfully");
		}
		catch(Throwable e)
		{
			Assert.fail("Exception thrown while setting timezone in Application");
		}

	}


	/**
	 * finds all values of a column matches with a specified value
	 * @param locator1
	 * @param col
	 * @param locator2
	 * @param colVal
	 */
	public void verifyAllColValues(String locator1, String col, String locator2, String colVal)
	{
		int index = getIndexForColHeader(locator1, col);
		String xpath = getPath(locator2).replace("INDEX", Integer.toString(index+1));
		//waitForelementdisplayed(xpath);
		List<WebElement> colValList = getObjectsDirect(By.xpath(xpath));
		for(int i=0; i<colValList.size();i++)
		{
			Assert.assertEquals(colValList.get(i).getText(), colVal, "Column value is not as expected");
		}
		logger.info("All values in column, "+col+ " are same as "+colVal);
	}
	/**
	 * Method for verifying the element is not displayed
	 * locator type as id/x path/link/css..
	 * locator value as id value /xpath value
	 * objectName as expected element 
	 * @author Nagaveni.Guttula
	 *
	 */
	public enum objectKey {
		link, xpath, css, id,name,classname;
	}
	public void verifyObjectNotPresent(String locatorType,String locatorValue, String ObjectName){
		boolean x=false;
		objectKey locator = objectKey.valueOf(locatorType);
		try{
			switch(locator){
			case link:
				driver.findElement(By.linkText(locatorValue));
				x=true;
				Assert.assertFalse(x, ObjectName+"is displayed");
			case xpath:		
				driver.findElement(By.xpath(locatorValue));
				x=true;
				Assert.assertFalse(x, ObjectName+"is displayed");
			case css:
				driver.findElement(By.cssSelector(locatorValue));
				x=true;
				Assert.assertFalse(x, ObjectName+"is displayed");
			case id:
				driver.findElement(By.id(locatorValue));
				x=true;
				Assert.assertFalse(x, ObjectName+"is displayed");
			case name:
				driver.findElement(By.name(locatorValue));
				x=true;
				Assert.assertFalse(x, ObjectName+"is displayed");
			case classname:
				driver.findElement(By.className(locatorValue));
				x=true;
				Assert.assertFalse(x, ObjectName+"is displayed");
			}
		}
		catch (NoSuchElementException e) {
			Assert.assertFalse(x, ObjectName+"is displayed");
		}
	}



	/**
	 * Method to compare two image of extension .jpg and .png
	 * 
	 * @param extention
	 */
	public void compareImg(String extention,String expImg1,String expImg2) {

		if (extention.contains("jpg")) {
			actualimg = actualimg + ".jpg";
			if (cmpIMG = compareImage(new File(actualimg), new File(expImg1))) {

				Assert.assertTrue(cmpIMG);
				new File(actualimg).delete();

			}
		} else if (extention.contains("png")) {

			actualimg = actualimg + ".png";

			if ((cmpIMG = compareImage(new File(actualimg), new File(expImg2)))) {

				Assert.assertTrue(cmpIMG);
				new File(actualimg).delete();

			}
		}
	}	

	/**
	 * Verifying Data is present or not
	 * @param locator
	 * @throws InterruptedException
	 */
	public void verifyIfnoData(String locator)
	{
		if(getObject(locator).getText().contains(noResultTxt))
		{
			logger.info("No Result to your Search in the Application");
			Assert.fail("No Result to your Search in the Application");
		}	
	}


	/**
	 * common method to handle script catch
	 * 
	 */
	public void handleException(Throwable ex)
	{
		fileName=this.getClass().getSimpleName();
		imgPath=CommonConstants.screenshotPath+this.getCurrentProject().toString()+"\\"+ fileName + ".jpg";
		captureScreenShotOnFailure(fileName);
		ErrorUtil.addVerificationFailure(ex);			
		error =ex.getMessage();
		logger.error(fileName+"execution failed:<a href='"+imgPath+"'><img src='"+imgPath+"' height="+20+" width="+40+" /></a>");
		Assert.fail(error,ex);
		return;
	}	

	/**
	 * common method to handle script catch with pdf and pop up option
	 * 
	 */
	public void handlePopUpException(Throwable ex)
	{			
		fileName=this.getClass().getSimpleName();
		imgPath=CommonConstants.screenshotPath+this.getCurrentProject().toString()+"\\"+ fileName + ".jpg";
		captureScreenShotOnFailure(fileName);
		closeChildWindows();
		ErrorUtil.addVerificationFailure(ex);			
		error =ex.getMessage();
		logger.error(fileName+"execution failed:<a href='"+imgPath+"'><img src='"+imgPath+"' height="+20+" width="+40+" /></a>");
		Assert.fail(error,ex);
		return;			
	}	

	/**
	 * Verify 'Saved Searched list Item'
	 * @param savedSearchName
	 * @throws InterruptedException 
	 */
	public void deleteSavedSearchItem(String savedSearchName) throws InterruptedException{
		try{
			getCommonObject("savedsearches_link").click();
			waitNSec(2);
			//waitForCommonElementPresent("saved_searchlistbox_xpath");
			options=getCommonObjects("saved_searchlistbox_xpath");
			for(int itemCount = 0; itemCount < options.size() ; itemCount++){
				savedsearchItemElement = options.get(itemCount).findElement(By.cssSelector("a"));
				waitForWebElementPresent(savedsearchItemElement);
				String value = savedsearchItemElement.getText();
				if(value.equalsIgnoreCase(savedSearchName)){	
					waitNSec(1);
					options.get(itemCount).findElement(By.xpath("//td/img[@src='img/deleteLink.gif']")).click();
					waitForElementPresent("savedok_link");
					getObject("savedok_link").click();
					waitForElementNotPresent("savedok_link");
					logger.info("Saved item is deleted successfully "+savedSearchName);
					break;
				}
			}
		}
		catch(Throwable e)
		{
			Assert.fail("Exception thrown while deleting saved search");
		}
	}

	/**
	 * Clicks on any web element
	 * @param locator
	 */
	public void clickOnElement(String locator){
		getObject(locator).click();

	}

	/**
	 * Generates Random Number
	 * @return
	 */
	public int getRandomNum()
	{
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(1000);		   
		return randomInt;		  
	}
}

