package com.ingenico.base;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/base/TestBase.java $
$Id: TestBase.java 18247 2016-04-26 07:19:30Z rkahreddyga $
 */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ho.yaml.Yaml;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
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
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import au.com.bytecode.opencsv.CSVReader;

import com.ingenico.common.CommonConstants;

/**
 *  TestBase is having all common members and member functions
 *
 */
public class TestBase {
	public static SelUtils selUtils;
	public static WaitMethods waitMethods;
	public static Properties config, objR, commonOR;
	public static Logger logger = LogManager.getLogger();
	public String  noOfPage,sProject,xpath,linkText,anotherxpath,fileName,imgPath,error,value,custbrdCrum="Customer "+BCDELIMTR+" Customers List",entityName,entitySapCode,ingEstName,epayLocation,
			posPosNumHeader,projectName, sqlQuery,name;
	public int iter,colIndex,colIndxAnother,maxNoPage,count,maxNoPageCount=20;
	public boolean isBrowserOpened,exists, xval,status;
	public List<WebElement> webelements,options;
	public WebElement webElement,anotherWebEle,page,tabContainer;
	public static WebDriverWait wait;
	public static WebDriver driver;
	public final static String JSCLICK="arguments[0].click();",BCDELIMTR = "»",EPAYMNTLOCATION="e-Payment Location",IPMGMT="IP Management",GPRSMGMT="GPRS Management",
			TMSINGESTLOC="IngEstate Location",INGESTATE="IngEstate",LANGFRAN="Francais (FR)",ATTRCLAS="class",BOUYTELE="Bouygues Telecom",NAMECOL="Name",
			VALEQ="Equals",VALCLOSE="close",AXISLOC="Axis location",USEDIP="0",CUSTPROV="Customer Provisioning",POSNUMHEADER="POS Number Header",STRUCTURE="Structure",
			CURRENT="current",FILEUPLOADEXE="FU.exe",TMSREPORTS="Reports",YES="yes",PROJECTNAME="Project name",NAME="NAME",EMV="EMV",PVDRNAMECOL="Provider Name",
			ENGLISH="English",ENLANG="en",NONE="None",MULTIVIEW="Multiview",LOGINCOL="Login",PFDESCTEMP="ProfileDescription_template.xml",PFDESC="ProfileDescription.xml",
			REPORTNAME="Report Name",ASSETTKING="Asset tracking",SUCCSMSG="success",SIGNTURE="Signature",ALL="All",EPAYMENT="e-Payment",CARDPAYMENT="Card Payment",
			GPRS="GPRS",INSTOREPAY="In Store Payment",GPRSMNGMNT="GPRS Management",PVDRMGMT="Provider Management",SIMMNGMNT="SIM Management",TRANSJOURNAL="Transaction Journal",
			CUSTNAME="Customer Name",SNAPSHOT="Snapshot",PROF="Profile",EPORTAL="Eportal",FILEUPLOADPATH="data/file_uploads/",TEMPPROFL="TEMPPROF",TEMPFLDR="TEMP",
			PROFLENAME="Profile Name",CPPROFILE="Card Payment Profile",PROFILEZIP="profiles.zip",SIMUSAGE="SIM Usage",COLLVL1="Level 1",JORNAL="Journal",MULTICHANNEL="Multi-Channel",
			KEYINDECATOR="Key Indicators",SUCSSFULLYDPLYD="successfully deployed",SUCCFULLYUPDT="successfully updated",SRLNUM="Serial number",PARTNUM="Part number",TERMINALS="Terminals",
			ATTRMSUCMSG = "Terminal(s) has been created",VALUE="VALUE",STATUS="STATUS",CUSTOMER="CUSTOMER",SUCSSFULLYEDITED="successfully edited";
	public  static String posHeader,profileTxt,uRL,browser,eportalCust;
	public String[] maxNoOfPages,arrFromCsv;
	public static String[] profile = new String[2];
	public static Actions action;
	public static Map<String,String> testDataOR ;
	public static String jenkinsfolderID;
	public List<String[]> listfromCSV;
	public List<String> csvData= new ArrayList<String>(), csv,listOfColVals= new ArrayList<String>();
	public static String tempFldrNme,outPutZipFile=null,sourceFolder=null;
	public static FileOutputStream fos;
	public static ZipOutputStream zos;
	public static FileInputStream fin;
	public static boolean dbCheck;
	public static ResultSet resSet;

	protected DataBaseMethods dbMethods = new DataBaseMethods();

	/**TODO--Methods which Initialize XML and Current Project--*/
	static
	{
		objR=new Properties();				
		config=new Properties();
		commonOR=new Properties();
	}
	/**
	 *  Initializing the Tests
	 *  Loading xml files	 
	 * @throws FileNotFoundException 
	 */
	public void initialize() {
		try {
			config.loadFromXML(new FileInputStream(CommonConstants.CONFIGFILE));
			commonOR.loadFromXML(new FileInputStream(CommonConstants.COMMONCONFIGFILE));
			String pageName = getUIMapPage();
			objR.loadFromXML(new FileInputStream(pageName));
			testDataOR = (Map<String, String>) Yaml.load(new File(CommonConstants.TESTDATAFILEPATH + jenkinsfolderID + ".yml"));
			//			loadYMLFile();
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("XML config files not loaded as expected", e);
		}
		/*
		 * Object object = Yaml.load(new File("data/testdata.yaml"));
		 * Map<String, String> object1 = (Map<String, String>) Yaml.load(new
		 * File("data/testdata.yaml"));
		 */
	}


	/**
	 * Closing browser
	 * 
	 */
	@AfterSuite(alwaysRun=true)
	public void tearDown(){
		logout();
		driver.quit();
	}

	/**
	 * Get UIMap Page
	 * @return uiMap
	 */
	public String getUIMapPage() {
		String[] projectName = this.getClass().toString().split(" ")[1].trim().split("\\.");	
		String uiMap = ""; 
		sProject=projectName[projectName.length-2];
		if(sProject!="")
		{
			uiMap = CommonConstants.OBJECTLOC + sProject + "_UIMap.xml";
		}
		else{
			Assert.fail("Cannot identify currently running project!!!");
		}
		return uiMap;
	}

	/**
	 * Get running project
	 * @return sProject
	 */
	/*public String getCurrentProject(){		
		String sProject = null,projects;		
		String[] projectName = this.getClass().toString().split(" ")[1].trim().split("\\.");		
		projects=projectName[projectName.length-2];
		switch (projects) {
		case "entitymanagement":
			sProject = "entitymanagement";
			break;
		case "location":
			sProject = "location";
			break;
		case "customermanagement":
			sProject = "customermanagement";
			break;
		case "tmsmanagement":
			sProject="tmsmanagement";
			break;
		case "gprs":
			sProject="gprs";
			break;
		case "cardpayment":
			sProject="cardpayment";
			break;
		case "usermanagement":
			sProject="usermanagement";
			break;
		case "eportal":
			sProject="eportal";
			break;	
		case "assettracking":
			sProject="assettracking";
			break;
		case "ogone":
			sProject="ogone";
			break;
		case "tokenserver":
			sProject="tokenserver";
			break;
		default:		   
			Assert.fail("Cannot identify currently running project!!!");
			break;
		}
		return sProject;
	}*/

	/**
	 * Common method to handle script exception
	 * logger.error should be in 3rd place(to get message even if captchascreenshot method fails) 
	 */
	public void handleException(Throwable exception)
	{
		fileName=this.getClass().getSimpleName();
		error=exception.getMessage();
		logger.error(error);
		captureScreenShotOnFailure(fileName);
		imgPath=getImgPath(fileName);
		logger.error(fileName+" execution failed: <a href='"+imgPath+"' target=blank><img src='"+imgPath+"' height="+20+" width="+40+" /></a>");
		Assert.fail(error, exception);
	}	

	/**
	 * For getting the Jenkins build number
	 * 
	 * @param folderID
	 */
	@Parameters({"FOLDER_ID", "BROWSER", "DB_CHECK"})
	@BeforeSuite(alwaysRun=true)
	public static void getJenkinsParameters(String folderID, String browserVal, boolean db_Check){
		browser = browserVal;
		jenkinsfolderID = folderID;
		dbCheck = db_Check;
	}

	/**
	 * Get project Path to display screen shot through jenkins
	 * @return
	 */
	public String getImgPath(String fileName){
		String[] projectPath = null;

		String regEx = Pattern.quote(System.getProperty("file.separator"));
		projectPath = System.getProperty("user.dir").trim().split(regEx);
		String projectname=projectPath[projectPath.length-1];
		return (CommonConstants.SCREENSHOTLINK + jenkinsfolderID +File.separator+ fileName + ".jpg").replaceAll("projectPath", projectname);
	}

	/**
	 * Captures screenshot on failure
	 * @param filename	 
	 */
	public void captureScreenShotOnFailure(String filename) {
		try {
			//WebDriver augmentedDriver = new Augmenter().augment(driver);
			File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);			
			FileUtils.copyFile(source, new File(CommonConstants.SCREENSHOTPATH 
					+ jenkinsfolderID +File.separator+ filename + ".jpg"));
		}
		catch( IOException e) {
			logger.error("Failed to capture screenshot: " + e.getMessage());
		}
	}


	/*TODO-Method for setting up browser, Login, Logout and closing the browser-*/
	/**
	 * Setting the browser profiles
	 * @param browser
	 * @throws MalformedURLException	 
	 */
	//	@Parameters({"browser"})
	//	@BeforeSuite(alwaysRun=true)
	public void setup() {
		//		browser=browserVal;
		DesiredCapabilities capability=null;
		try{
			if("firefox".equalsIgnoreCase(browser)){			
				FirefoxProfile profile = new FirefoxProfile();
				profile.setEnableNativeEvents(true);
				profile.setPreference("browser.download.folderList", 2);
				profile.setPreference("browser.download.manager.showWhenStarting",false);
				profile.setPreference("browser.download.dir",CommonConstants.fileDownloadPath);
				profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf,application/force-download,application/x-download,text/csv,image/jpeg");			    
				profile.setPreference("pdfjs.disabled", true);
				capability= DesiredCapabilities.firefox();			
				capability.setCapability("unexpectedAlertBehaviour", "ignore");
				capability.setCapability(FirefoxDriver.PROFILE, profile);				
				driver = new FirefoxDriver(capability);
			}

			if("internet explorer".equalsIgnoreCase(browser)){		
				System.setProperty("webdriver.ie.driver", CommonConstants.IEDRIVERPATH+"\\IEDriverServer.exe");
				capability= DesiredCapabilities.internetExplorer();				
				capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);  
				capability.setPlatform(org.openqa.selenium.Platform.WINDOWS);
				driver=new InternetExplorerDriver(capability);
			}
			//driver.get(testDataOR.get("URLEverest"));
			//To assert IE certificate error message
			if("internet explorer".equalsIgnoreCase(browser)){
				driver.navigate().to("javascript:document.getElementById('overridelink').click()");
				driver.navigate().to("javascript:document.getElementById('overridelink').click()");
			}
			driver.manage().timeouts().implicitlyWait(Integer.parseInt(config.getProperty("default_implicitWait")), TimeUnit.SECONDS);
			driver.manage().window().maximize();
			wait = new WebDriverWait(driver, 10);
			setDriver(driver);
			selUtils = PageFactory.initElements(driver, SelUtils.class);
			waitMethods = PageFactory.initElements(driver, WaitMethods.class);
			action = new Actions(driver);
		}
		catch(Throwable t){
			Assert.fail("Exception thrown while opening browser");
		}
	}

	//	@BeforeSuite(alwaysRun=true)
	/*	public void setupInit()
	{
		initialize();
		String superuser=testDataOR.get("superuser"),firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
		login("URLEverest", "username_name", "password_name", superuser,"superuser_password", "submit_button_xpath",firstName,lastName);
	}
	 */
	public void login(String browserURLKey,String user,String firstName,String lastName) 
	{
		boolean title,header;
		try{
			uRL=browserURLKey;
			driver.switchTo().defaultContent();
			((JavascriptExecutor) driver).executeScript("scroll('1000','0')");

			if(driver!=null)
			{
				//				uRL=browserURLKey;
				clickOnPageTab();
				if("URLEverest".equalsIgnoreCase(uRL))
				{
					try
					{
						title=driver.getTitle().contains("Everest");
						if(!title || !(driver.findElement(By.id("headerTable")).getText().contains(user)))
						{

							logout();
							homePageLogin("username_name","password_name",user,"superuser_password","submit_button_xpath");
							Assert.assertTrue(selUtils.getCommonObject("login_name_id").getText().contains(user),"Login procedure Falied while validating login name "+user);
						}
					}
					catch(Exception e)
					{
						driver.quit();
						setup();
						homePageLogin("username_name","password_name",testDataOR.get("superuser"),"superuser_password","submit_button_xpath");
						Assert.assertTrue(selUtils.getCommonObject("login_name_id").getText().contains(user),"Login procedure Falied while validating login name "+user);
					}
				}
				//else if(uRL.contains(EPORTAL))
				else if(uRL.contains(EPORTAL)&&!user.equals(testDataOR.get("mono_user_login")))
				{
					try
					{
						title=driver.getTitle().contains("e-Portal");
						header=driver.findElement(By.id("header")).getText().contains(firstName+" "+lastName);
						if(!title||!header||!SelUtils.getSelectedItem(selUtils.getCommonObject("client_id")).contains(eportalCust))
						{
							eportalLogoutAndLogin(user);
							Assert.assertTrue(selUtils.getCommonObject("header_id").getText().contains(firstName+" "+lastName),"Login procedure Falied while validating first name and last name "+firstName+" "+lastName);
						}
					}
					catch(Exception e)
					{
						driver.quit();
						setup();
						homePageLogin("eportalusr_name","eportalpswd_name", user,"superuser_password","newversnloginbttn_xpath");
						Assert.assertTrue(selUtils.getCommonObject("header_id").getText().contains(firstName+" "+lastName),"Login procedure Falied while validating first name and last name "+firstName+" "+lastName);

					}

				}
				else
				{
					Assert.assertTrue(selUtils.getCommonObject("header_id").getText().contains(firstName+" "+lastName),"Login procedure Falied while validating first name and last name "+firstName+" "+lastName);
				}


			} 

		}
		catch(Exception t)
		{
			if("URLEverest".equalsIgnoreCase(uRL))
			{
				setup();
				homePageLogin("username_name","password_name",user,"superuser_password","submit_button_xpath");
				Assert.assertTrue(selUtils.getCommonObject("login_name_id").getText().contains(user),"Login procedure Falied while validating login name "+user);
			}
			else if(uRL.contains(EPORTAL))
			{
				setup();
				homePageLogin("eportalusr_name","eportalpswd_name", user,"superuser_password","newversnloginbttn_xpath");
				Assert.assertTrue(selUtils.getCommonObject("header_id").getText().contains(firstName+" "+lastName),"Login procedure Falied while validating first name and last name "+firstName+" "+lastName);
			}
		}
	}

	public void clickOnPageTab()
	{
		try{
			if(driver.getTitle().contains("Everest"))
			{
				((JavascriptExecutor)driver).executeScript(JSCLICK, selUtils.getCommonObject("cust_tab_link"));
			}
			else if(driver.getTitle().contains("e-Portal") ||driver.getTitle().contains("Login") )
			{
				((JavascriptExecutor) driver).executeScript(JSCLICK,selUtils.getCommonObject("eportalfirsttab_xpath"));
			}
		}
		catch(AssertionError | Exception e)
		{
			logger.error("Failed in click On first page of the applicaiton"+e.getMessage());
			driver.quit();
		}
	}

	public void homePageLogin(String userNameLoc,String passWrdLoc,String userNameValKey,String passWrdValKey,String submitBttnLoc)
	{
		//		try
		//		{
		driver.get(testDataOR.get(uRL));
		waitMethods.waitForWebElementPresent(selUtils.getCommonObject(userNameLoc));
		selUtils.getCommonObject(userNameLoc).sendKeys(userNameValKey);

		//Start For SMR9 and SMR232
		/*String className=this.getClass().getSimpleName();
			if(className.equalsIgnoreCase("SMR9")||className.equalsIgnoreCase("SMR232")||(className.equalsIgnoreCase("SMR1269")&&uRL.contains(EPORTAL)))
			{
				selUtils.getCommonObject(passWrdLoc).sendKeys(testDataOR.get("user_password"));

			}
			else{
				selUtils.getCommonObject(passWrdLoc).sendKeys(testDataOR.get(passWrdValKey));
			}*/
		if(userNameValKey.equals(testDataOR.get("multi_user_login"))||userNameValKey.equals(testDataOR.get("mono_user_login")))
		{
			selUtils.getCommonObject(passWrdLoc).sendKeys(testDataOR.get("user_password"));
		}
		else
		{
			selUtils.getCommonObject(passWrdLoc).sendKeys(testDataOR.get(passWrdValKey));
		}
		//End For  SMR9 and SMR232
		selUtils.getCommonObject(submitBttnLoc).click();

		if(uRL.contains(EPORTAL)&&!userNameValKey.equals(testDataOR.get("mono_user_login")))
		{
			waitMethods.waitForWebElementPresent(selUtils.getCommonObject("clientokbttn_xpath"));
			selUtils.selectItem(selUtils.getCommonObject("client_id"), eportalCust);
			logger.info("selected "+eportalCust);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();",selUtils.getCommonObject("clientokbttn_xpath"));
			//selUtils.clickOnWebElement(selUtils.getCommonObject("clientokbttn_xpath"));
			logger.info("Clicked on ok button");
		}
		//		}
		//		catch(Exception e)
		//		{
		//			logger.error("Exception occured in home page login "+e.getMessage());
		//			//driver.quit();
		//
		//		}
	}

	public void eportalLogoutAndLogin(String user)
	{
		//		try{
		logout();
		homePageLogin("eportalusr_name","eportalpswd_name", user,"superuser_password","newversnloginbttn_xpath");
		//		}
		//		catch(Exception e){
		//			Assert.fail("Problem with eportal client selection"+e.getMessage());
		//		}
	}


	/**
	 * Get Driver
	 * @return
	 */
	public static WebDriver getDriver() {
		return driver;
	}

	/**
	 * Set Driver
	 * @param driver
	 */
	public static void setDriver(WebDriver driver) {
		TestBase.driver = driver;
	}

	/**
	 * Login
	 * @param browserURLKey
	 * @param userNameLoc
	 * @param passWrdLoc
	 * @param userNameValKey
	 * @param passWrdValKey
	 * @param submitBttnLoc
	 */
	public void login(String browserURLKey,String userNameLoc,String passWrdLoc,String userNameValKey,String passWrdValKey,String submitBttnLoc,String firstName,String lastName){
		try{
			uRL=browserURLKey;
			homePageLogin(userNameLoc, passWrdLoc, userNameValKey, passWrdValKey, submitBttnLoc);
			if(uRL.contains(EPORTAL)){
				Assert.assertTrue(selUtils.getCommonObject("header_id").getText().contains(firstName+" "+lastName),"Login procedure Falied while validating first name and last name "+firstName+" "+lastName);
			}
			else{
				Assert.assertTrue(selUtils.getCommonObject("login_name_id").getText().contains(userNameValKey),"Login procedure Falied while validating login name "+userNameValKey);
			}
		}
		catch(Throwable e)
		{
			Assert.fail("Problem with login ");
		}
	}

	/**
	 * Logout
	 */
	public void logout() {
		try{
			if(driver.findElement(By.linkText("Sign out")).isDisplayed()){
				//selUtils.getCommonObject("logout_xpath").click();
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getCommonObject("logout_xpath"));
				waitMethods.waitForWebElementPresent(selUtils.getCommonObject("username_name"));
				logger.info("clicked on logout link of Everest");
			}
		}catch(Exception e){

			try{
				driver.switchTo().defaultContent();
				waitMethods.waitForWebElementPresent(selUtils.getCommonObject("homepage_toolcaption_css"));
				//if(selUtils.getCommonObject("homepage_toolcaption_css").isDisplayed())
				if(selUtils.getCommonObject("ep_signout_id").isDisplayed())

				{
					//selUtils.getCommonObject("homepage_toolcaption_css").click();
					waitMethods.waitForWebElementPresent(selUtils.getCommonObject("ep_signout_id"));
					//selUtils.getCommonObject("ep_signout_id").click();
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getCommonObject("ep_signout_id"));
					selUtils.getCommonObject("confirm_id").click();
					waitMethods.waitForWebElementPresent(selUtils.getCommonObject("eportalusr_name"));
					logger.info("Clicked on logout link of Eportal");
				}
			}catch(Throwable t){
				Assert.fail("Problem with logout"+t.getMessage());
			}
		}
	}

	/*TODO-Methods to get locator from object locator xml file-*/

	/**
	 * Method for returning object locator
	 * @param objLocator
	 * @return locPath
	 */
	public static String getPath(String objLocator) {
		String locPath="";
		try {
			locPath = objR.getProperty(objLocator);
			if("".equals(locPath))
			{
				Assert.fail("Path to locate element is null");
			}

		} catch (Throwable t) {
			Assert.fail("Cannot find locator path with key -- " + objLocator);
		}
		return locPath;

	}

	/**
	 * Method for returning common object locator
	 * @param objLocator
	 * @return locPath
	 */
	public static String getCommonPath(String commObjLocator) {
		String locPath="";
		try {
			locPath = commonOR.getProperty(commObjLocator);
			if("".equals(locPath))
			{
				Assert.fail("Path to locate element is Null");
			}
		} catch (Throwable t) {
			Assert.fail("Cannot find locator path with key -- " + commObjLocator);
		}
		return locPath;
	}

	/*TODO-Methods related to finding the web elements- */

	/**
	 * Gets list of options as String items
	 * @param element
	 * @return drop down options
	 */
	public String[] getListItems(WebElement element)
	{
		Select dropDown = new Select(element);           
		int iter=0;
		List<WebElement> options = dropDown.getOptions();
		String[] listitems=new String[options.size()];
		try
		{
			for(WebElement option:options){
				listitems[iter]=option.getText();
				iter++;
			}
		}catch (Throwable t) {
			Assert.fail("Failed during getting list Items from drop down");
		}
		return listitems;
	}

	/**
	 * Convert web elements list to string list 
	 * @param ele
	 * @return list string options
	 */
	public List<String> getListItemsAsString(List<WebElement> ele){		          
		int iter=0;
		List<WebElement> options = ele;		
		List<String> listitems=new ArrayList<String>();
		for(iter=0;iter<options.size();iter++)
		{			
			listitems.add(options.get(iter).getText());

		}		
		return listitems;
	}

	/**
	 * It's common method for check page source objects
	 * @param str	 
	 */
	public void getContains(String str){
		try{
			Assert.assertTrue(driver.getPageSource().contains(str), "Expected '"+str+"' is not displayed");
		}

		catch (Exception e)
		{					
			Assert.fail("Expected '"+str+"' is not displayed");

		}
	}

	/*TODO--Wait Related Functions---*/

	/**
	 * Wait for N second
	 * @param waitTime
	 */
	public static void waitNSec(int waitTime) {
		try {
			switch (waitTime) {
			case 1:
				Thread.sleep(CommonConstants.ONESEC);
				break;
			case 2:
				Thread.sleep(CommonConstants.TWOSEC);
				break;
			case 3:
				Thread.sleep(CommonConstants.THREESEC);
				break;
			case 4:
				Thread.sleep(CommonConstants.FOURSEC);
				break;
			case 5:
				Thread.sleep(CommonConstants.FIVESEC);
				break;
			case 6:
				Thread.sleep(CommonConstants.SIXSEC);
				break;
			case 7:
				Thread.sleep(CommonConstants.SEVENSEC);
				break;			
			default:
				Assert.fail("Please specify wait time within 10. Currently given is: " + waitTime);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
	}


	/*TODO--Functional Methods related to modules------
	 * 
	 */
	/**

	 * Validate if the section is multi select or not
	 * @param element
	 * @param eleSection
	 */
	public void vIsMultiple(WebElement element,String eleSection)
	{
		Select multiSel=new Select(element);
		Assert.assertTrue(multiSel.isMultiple(),eleSection+" section is not multiple select list");
		logger.info(eleSection+" section is multiple select list");
	}

	/**
	 * validate multi list values
	 * @param listelement
	 * @param expItems
	 */
	public void vMultiListOptions(WebElement listelement,String[] expItems )
	{
		String[] appItems=   getListItems(listelement);
		List<String>  appList = Arrays.asList(appItems);  
		for(int iter=0;iter<expItems.length;iter++){
			Assert.assertTrue(appList.contains(expItems[iter]),"Expected listItems are not Present");
		}
	}

	/**
	 * SubPage Navigate method
	 * @param subModuleName
	 * @param tab
	 * @param subModule
	 */
	public void navigateToSubPage(String subModuleName,WebElement tab,WebElement subModule){
		/*		action.moveToElement(tab).build().perform();
		((JavascriptExecutor) driver).executeScript(JSCLICK,subModule);

		 */		
		exists=false;
		action = new Actions(driver);
		action.moveToElement(tab).build().perform();
		options = tab.findElements(By.tagName("li"));
		for(WebElement subPage : options){
			value=	subPage.getText();
			if(value.trim().equalsIgnoreCase(subModuleName.trim()))
			{
				subModule.click();
				logger.info("Clicked on the "+subModuleName );
				exists=true;
				break;	
			}
			else
			{
				continue;
			}
		}
		if(!exists){
			Assert.fail(subModuleName + ", sub module is not present under Main Tab ");
		}

	}
	/**
	 * page Navigation javascript click
	 * @param mainPageloc
	 * @param subPageloc
	 */
	public void navToSubPage(String mainPageloc,String subPageloc,String pageName)
	{
		action.moveToElement(selUtils.getCommonObject(mainPageloc)).build().perform();
		((JavascriptExecutor) driver).executeScript(JSCLICK,selUtils.getCommonObject(subPageloc));
		logger.info("Navigated to "+pageName+" tab");
	}

	/**
	 * Verifies if Customer is current tab, if not navigate to dash board
	 *
	 */
	public void navigateToCustomerPage() {
		try{
			((JavascriptExecutor)driver).executeScript(JSCLICK, selUtils.getCommonObject("cust_tab_link"));
			logger.info("Navigated to Customer page");	
		} catch (Throwable e) {
			Assert.fail("Failed while clicking on Customer module");
		}

	}

	/**
	 * This method for generating random number
	 * @param charLength
	 * @return
	 */
	public String generateRandomNumber(int charLength) {
		return String.valueOf(charLength < 1 ? 0 : new Random()
		.nextInt(9 * (int) Math.pow(10, charLength - 1) - 1)
		+ (int) Math.pow(10, charLength - 1));
	}

	/**
	 * Verify, the expected modal window is displayed.
	 * @param webElement
	 * @param title
	 */
	public boolean getModWinDisp(WebElement webElement,String title){
		exists=false;
		waitMethods.waitForWebElementPresent(webElement);
		if(webElement.getText().equals(title)){
			exists=true;
		}
		return exists;
	}

	/**
	 * Method for entering the field values in pop up
	 * @param fieldsListLoc
	 * @param values
	 */
	public void setValsForPopUpWin(WebElement webElement,String popupTxt,String[] fieldsListLoc,String[] values){
		if(getModWinDisp(webElement,popupTxt)){
			for(iter=0;iter<fieldsListLoc.length;iter++){
				selUtils.populateInputBox(fieldsListLoc[iter], values[iter]);
			}
		}
	}

	/**
	 * Method for deleting the created local data by clicking the delete img
	 * @param locator
	 * @param dataToDelet
	 */
	public void clkOnDelImg(String locator,String dataToDelet){
		try{
			deleteExistsData(locator,dataToDelet);
		}catch(Throwable t){
			Assert.fail("There is no option to delete the data "+dataToDelet);
		}

	}

	/**
	 * Method for deleting the existing data if the delete image is displayed
	 * @param locator
	 * @param dataToDelet
	 */
	public void deleteExistsData(String locator,String dataToDelet){
		try{
			xpath="";
			xpath=getPath(locator).replace("NAME", dataToDelet);
			if(selUtils.getObjectDirect(By.xpath(xpath)).isDisplayed() && !(dataToDelet.isEmpty())){
				List<WebElement> elts=selUtils.getObjectsDirect(By.xpath(xpath));
				for(int cnti=0;cnti<elts.size();cnti++){
					selUtils.getObjectDirect(By.xpath(xpath)).click();
					selUtils.acceptAlert();
				}
				logger.info("Deleted the "+dataToDelet+" data");
			}
			else
			{
				logger.info("There is no option to delete");
			}

		}catch(Exception e){
			Assert.fail("There is no option  to delete the data "+dataToDelet);
		}
	}

	/**
	 * Method to verify exp value in column in the table
	 * @param tableObject
	 * @param searchColnum
	 * @param leveltxt
	 */
	public void verifyLvlColLvlValPresence(String tableObject,int searchColnum,String leveltxt)
	{ 
		if(selUtils.isElementPresentCommon("page_id")){
			int pageNum=0,iter,pageIter=0;
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
				List<WebElement> rows=selUtils.getCommonObject(tableObject).findElements(By.tagName("tr"));
				if(uRL.contains(EPORTAL))
				{
					iter=0;	
				}					
				else
				{
					iter=1;
				}					
				for(int rowcount=iter;rowcount<rows.size();rowcount++){
					List<WebElement> cols=rows.get(rowcount).findElements(By.tagName("td"));
					for(int colcount=0;colcount<cols.size();colcount++){
						if(colcount==searchColnum && cols.get(colcount).getText().equalsIgnoreCase(leveltxt))
							//if(colcount==searchColnum && cols.get(colcount).getText().contains(leveltxt))
						{
							Assert.assertTrue(cols.get(colcount).getText().equalsIgnoreCase(leveltxt),"Expected data  is"+cols.get(colcount).getText()+" Actual data  is'"+leveltxt+"' both the data is mismatching");
							//Assert.assertTrue(cols.get(colcount).getText().contains(leveltxt),leveltxt+"The expected data '"+leveltxt+"' is not present in the expected list");
							logger.info("The expected data '"+leveltxt+"' is present in the expected list");
							break outerloop;							
						}
					}
				}
				pageNum++;
			}
			if(pageNum==pageIter){
				Assert.fail("The expected data "+leveltxt+" was not present with in the numberof pages "+pageIter);
			}
		}
		else{
			verifyExpValinCol(tableObject,searchColnum,leveltxt);
		}
	}

	
	
	/**
	 * Method to verify exp value in column in the table
	 * @param tableObject
	 * @param searchColnum
	 * @param leveltxt
	 */
	
	
	public void vrfyColValNotPresence(String tableObject,int searchColnum,String leveltxt)
	{ 
		if(selUtils.isElementPresentCommon("page_id")){
			int pageNum=0,iter,pageIter=0;
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
				List<WebElement> rows=selUtils.getCommonObject(tableObject).findElements(By.tagName("tr"));
				if(uRL.contains(EPORTAL))
				{
					iter=0;	
				}					
				else
				{
					iter=1;
				}					
				for(int rowcount=iter;rowcount<rows.size();rowcount++){
					List<WebElement> cols=rows.get(rowcount).findElements(By.tagName("td"));
					for(int colcount=0;colcount<cols.size();colcount++){
						if(colcount==searchColnum && cols.get(colcount).getText().equalsIgnoreCase(leveltxt))
							//if(colcount==searchColnum && cols.get(colcount).getText().contains(leveltxt))
						{
							Assert.assertTrue(cols.get(colcount).getText().equalsIgnoreCase(leveltxt),"Expected data  is"+cols.get(colcount).getText()+" Actual data  is'"+leveltxt+"' both the data is mismatching");
							//Assert.assertTrue(cols.get(colcount).getText().contains(leveltxt),leveltxt+"The expected data '"+leveltxt+"' is not present in the expected list");
							Assert.fail("The expected data '"+leveltxt+"' is present in the expected list");
							break outerloop;							
						}
					}
				}
				pageNum++;
			}
			if(pageNum==pageIter){
				logger.info("The expected data "+leveltxt+" was not present with in the numberof pages "+pageIter);
			}
		}
		else{
			verifyExpValinCol(tableObject,searchColnum,leveltxt);
		}
	}

	
	
	
	
	/**
	 * Method to verify exp value in a particular column
	 * @param tableObject
	 * @param searchColnum
	 * @param leveltxt
	 */
	public void verifyExpValinCol(String tableObject,int searchColnum,String leveltxt){
		int rowNum=1,iter;
		List<WebElement> rows=selUtils.getCommonObject(tableObject).findElements(By.tagName("tr"));
		if(uRL.contains(EPORTAL))
		{
			iter=0;
		}			
		else
		{
			iter=1;
		}			
		outerloop:for(int rowcount=iter;rowcount<rows.size();rowcount++){
			List<WebElement> cols=rows.get(rowcount).findElements(By.tagName("td"));
			for(int colcount=0;colcount<cols.size();colcount++){
				if(colcount==searchColnum && cols.get(colcount).getText().trim().equalsIgnoreCase(leveltxt))
					//if(colcount==searchColnum && cols.get(colcount).getText().trim().contains(leveltxt))
				{					
					//Assert.assertTrue(cols.get(colcount).getText().trim().equalsIgnoreCase(leveltxt), "'"+leveltxt + "' does not exist in the list");
					Assert.assertTrue(cols.get(colcount).getText().trim().contains(leveltxt), "'"+leveltxt + "' does not exist in the list");
					logger.info("'"+leveltxt+ "' is present in the expected list");
					break outerloop;					
				}
			}
			rowNum++;
		}
		if(rowNum==rows.size()){
			Assert.fail("The expected data '"+leveltxt+"' is not present in the list");
		}

	}

	/**
	 * Method for getting number of pages
	 * @return
	 */
	public int getNumberOfPages()
	{
		noOfPage=selUtils.getCommonObject("noofpage_xpath").getText();
		maxNoOfPages =noOfPage.substring(noOfPage.lastIndexOf("of"), noOfPage.length()).trim().split(" ");
		maxNoPage=Integer.parseInt(maxNoOfPages[1]);
		return maxNoPage;
	}

	/**
	 * Method for verifying the existing data in all the pages and deleting the data
	 * @param locator
	 * @param expectedData
	 * @param colName
	 */
	public void verifyExistingData(String locator,String colhederloc,String allrwsloc,String expectedData,String colName){
		int pageIter=0,pageNum=0;
		if(selUtils.isElementPresentCommon("page_id")){
			WebElement page=selUtils.getCommonObject("page_id");
			String[] pageItems=getListItems(page);
			if(pageItems.length>=maxNoPageCount){
				pageIter=maxNoPageCount;
			}
			else{
				pageIter=pageItems.length;
			}
			pageNum:for(int count=0;count<pageIter;count++){
				page=selUtils.getCommonObject("page_id");
				selUtils.selectItem(page,pageItems[count]);
				waitMethods.waitForWebElementPresent(selUtils.getCommonObject("page_id"));
				exists=verifyNDeleteData(locator,colhederloc,allrwsloc,expectedData,colName);
				if(exists){
					break pageNum;
				}
				pageNum++;
			}
			if(pageNum==maxNoPageCount){
				Assert.fail("Verified the uniqueness of the data  with in the twenty pages");
			}
		}
		/*else{
			if(isElementPresentCommon("noresulttxt_xpath")){
				logger.info("There is no data to verify");
			}*/
		else{
			verifyNDeleteData(locator,colhederloc,allrwsloc,expectedData,colName);
		}
	}

	/**
	 * Method for deleting data if delete image is displayed
	 * @param locator
	 * @param expectedData
	 * @return
	 */
	public boolean verifyNDeleteData(String locator,String colhederloc,String allrwsloc,String expectedData,String columnName){
		try{
			exists=false;
			xpath="";
			colIndex=0;
			colIndex=selUtils.getIndexForColHeader(colhederloc, columnName);
			xpath=getCommonPath(allrwsloc).replace("COLINDEX", Integer.toString(colIndex+1));
			List<WebElement> elts = selUtils.getObjectsDirect(By.xpath(xpath));
			for(WebElement elt:elts){
				if(elt.getText().trim().equals(expectedData)){
					deleteExistsData(locator, expectedData);
					exists=true;
					break;
				}
			}
		}catch(Throwable t){
			Assert.fail("There is no option  to delete the data "+expectedData);
		}
		return exists;
	}

	/**
	 * Method for filtering  the single value 
	 * @param expVal
	 * @param filterLocator
	 * @param preDefFilterVal
	 * @param filterIpLocator
	 * @return
	 */
	public String searchVal(String expVal,String filterLocator,String preDefFilterVal,String filterIpLocator){
		selUtils.selectItem(selUtils.getObject(filterLocator), preDefFilterVal);
		if(expVal.trim().isEmpty())
		{
			logger.info("There is no profile to upload");
			Assert.fail("There is no profile to upload");
		}
		else
		{
			selUtils.populateInputBox(filterIpLocator, expVal.trim());
			selUtils.clickOnWebElement(selUtils.getCommonObject("searchbttn_xpath"));
		}			
		return expVal;
	}

	/**
	 * Selects check box or radio button
	 * @param yamlval,@param chbx
	 */
	public void selChkOrRadiobttns(String yamlval[],String chbx[]){
		for(int count=0;count<chbx.length;count++){
			if(yamlval[count].equalsIgnoreCase("true"))
			{
				selUtils.slctChkBoxOrRadio(selUtils.getObject(chbx[count]));
			}				
		}
	}

	/**
	 * Selects check box or radio button,for common among all modules
	 * @param yamlval,@param chbx
	 */
	public void selChkOrRadiobttnsCommon(String yamlval[],String chbx[]){
		for(int count=0;count<chbx.length;count++){
			if(yamlval[count].equalsIgnoreCase("true"))
			{
				selUtils.slctChkBoxOrRadio(selUtils.getCommonObject(chbx[count]));
			}				
		}
	}

	/**
	 * Method to login to eportal application and select client
	 * @param view
	 */
	public void epLoginNSelClient(String customer,String userlogin){
		String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
		login("URLEportal","eportalusr_name","eportalpswd_name", userlogin,"superuser_password","newversnloginbttn_xpath",firstName,lastName);
	}

	/**
	 * Login to everest application select customer in eportal 
	 * @param customerName
	 */
	public void selCustInEvrest(String customerName)
	{
		String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
		waitMethods.waitForWebElementPresent(selUtils.getCommonObject("eportalusr_name"));
		name=getCommonPath("eportalusr_name");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(name)));
		login("URLEverest", "username_name", "password_name", testDataOR.get("superuser"),"superuser_password", "submit_button_xpath",firstName,lastName);
		waitMethods.waitForWebElementPresent(selUtils.getCommonObject("eportal_tab_link"));
		selUtils.clickOnWebElement(selUtils.getCommonObject("eportal_tab_link"));
		selUtils.selectItem(selUtils.getCommonObject("cust_Sel_id"), customerName);
	}

	/**
	 * Logout application and login everest with customer name
	 * @param customerName
	 */
	public void logoutNEvselCust(String customerName)
	{
		logout();
		selCustInEvrest(customerName);
		logger.info("Logout and login everest with customer name "+customerName);
	}

	/**
	 * logout and login eportal application with customer name
	 * @param customerName
	 */
	public void logoutEpSelCust(String customerName)
	{
		logout();
		epLoginNSelClient(customerName,testDataOR.get("superuser"));
		logger.info("Logout and login eportal with customer name "+customerName);
	}

	/**
	 * Enable module and validate
	 * @param locator
	 * @param modName
	 */
	public void disableModNVal(String locator,String modName)
	{
		selUtils.unSlctChkBoxOrRadio(selUtils.getCommonObject(locator));
		selUtils.getCommonObject("savebttn_xpath").click();
		selUtils.verifyElementNotSelected(selUtils.getCommonObject(locator));
		logger.info("Disabled "+modName+" module and validated");
	}

	/**
	 * Disable module and validate
	 * @param locator
	 * @param modName
	 */
	public void enableModNVal(String locator,String modName)
	{
		selUtils.slctChkBoxOrRadio(selUtils.getCommonObject(locator));
		selUtils.getCommonObject("savebttn_xpath").click();
		selUtils.verifyElementSelected(selUtils.getCommonObject(locator));
		logger.info("Enabled "+modName+" module and validated");
	}

	/**
	 * Method for clicking on the object direct
	 * @param objKey
	 * @param valToReplace
	 * @param replacableVal
	 */
	public void clkOnDirectObj(String objKey,String valToReplace,String replacableVal){
		String locVal="",locators;
		try {
			//Locators locators = Locators.valueOf(objKey.substring(objKey.lastIndexOf('_') + 1));
			locators=objKey.substring(objKey.lastIndexOf('_') + 1);
			locVal=getPath(objKey).replace(valToReplace,replacableVal);
			switch (locators) {
			case "link":
				selUtils.getObjectDirect(By.linkText(locVal)).click();
				break;
			case "xpath":
				selUtils.getObjectDirect(By.xpath(locVal)).click();
				break;
			case "css":
				selUtils.getObjectDirect(By.cssSelector(locVal)).click();
				break;
			case "id":
				selUtils.getObjectDirect(By.id(locVal)).click();
				break;
			case "name":
				selUtils.getObjectDirect(By.name(locVal)).click();
				break;
			case "classname":
				selUtils.getObjectDirect(By.className(locVal)).click();
				break;
			default:
				Assert.fail("Invalid locator type");
				break;
			}
		} catch (Exception e){
			e.printStackTrace();
			Assert.fail("Not able to clickon "+replacableVal+" webelement");
		}
	}

	/**
	 * get download path of the file
	 * @param file, fileType
	 * @return	 
	 */
	public String getDownFilePath(String file,String fileType)
	{
		String filePath = null;
		try
		{
			logger.info(" Get the path of "+fileType+" file ");
			filePath = CommonConstants.fileDownloadPath+file+"."+fileType;
		}
		catch(Exception e)
		{
			Assert.fail("Failed during getting path of the file",e);
		}
		return filePath;
	}

	/**
	 * get upload path of the file
	 * @param file, fileType
	 * @return	 
	 */
	public String getUpldFilePath(String file,String fileType)
	{
		String filePath = null;
		try
		{
			logger.info(" Get the path of "+fileType+" file ");
			filePath = CommonConstants.fileUploadpath+file+"."+fileType;
		}
		catch(Exception e)
		{
			Assert.fail("Failed during getting path of the file",e);
		}
		return filePath;
	}

	/**
	 * Method to verify not presence of sub mod
	 * @param subModuleName,@param tab,@param subModule
	 * 
	 */
	public void vSubModNotPresent(String subModuleName,WebElement tab,String subModule){ 		
		exists=false;
		action = new Actions(driver);
		action.moveToElement(tab).build().perform();
		Assert.assertFalse(selUtils.isElementPresentCommon(subModule),subModuleName+ "is present under main tab");
		logger.info("Verified "+subModuleName+ "is not present under main tab");

	}

	/**
	 * Method to check expected and actual text equality
	 * @param locator
	 * @param actual
	 */
	public void assertEquals(String actualText,String expectedText){		
		Assert.assertEquals(actualText,expectedText,"Actual text '"+actualText+"' is not equal to the expected '"+expectedText+"'");
		logger.info("Actual text '"+actualText+"' is equal to the expected '"+expectedText+"'");
	}

	/**
	 * compare two strings
	 * @param actual
	 * @param expected
	 */
	public void assertContains(String actual,String expected){
		Assert.assertTrue(actual.contains(expected),"Expected text is not present '"+expected+"' ");
		logger.info("Expected text is present '"+expected+"'");
	}
	/**
	 * compare two strings
	 * @param actual
	 * @param expected
	 */
	public void assertFalse(boolean booleanVal){
		Assert.assertFalse(booleanVal,"Expected false but boolean value is true ");
	}
	/**
	 * Method used for selecting the multiple vals from combo box
	 * @param comboBoxElement
	 * @param selectedItemsList
	 */
	public void selectMultipleVals(String comboBoxElement, String selectedItemsList){
		String mulSel[];
		mulSel=selectedItemsList.split(",");
		fileName=this.getClass().getSimpleName();
		if(!"SMR1872".equalsIgnoreCase(fileName)){
			selUtils.deselectItem(selUtils.getObject("ep_cust_id"), ALL);
		}
		Select select = new Select(selUtils.getObject(comboBoxElement));
		for(int itemcount = 0; itemcount <mulSel.length; itemcount++){
			select.selectByVisibleText(mulSel[itemcount]);
			selUtils.getObject(comboBoxElement).sendKeys(Keys.CONTROL);
		}

	}

	/**
	 * Method for reading the CSV Data and closing the file 
	 * @param path
	 * @return csv
	 * @throws IOException
	 */
	public List<String> readCSVNClose(final String path) throws IOException{
		final CSVReader reader = new CSVReader(new FileReader(path));
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
		logger.info("CSV Data read is successful");
		return csv;
	}
	/**
	 * Method for getting the csvData for the specified column
	 * @return
	 * @throws IOException
	 */
	public String getDataFromCSV(final String path,final String colName) throws IOException{
		int count=0;
		csvData = readCSVNClose(path);
		final String[] colNval=csvData.toString().split(",");
		final String[] cols=colNval[0].split(";");
		final String[] colVals=colNval[1].split(";");
		for(int iter=0;iter<cols.length;iter++){
			if(cols[iter].equalsIgnoreCase(colName)){
				count=iter;
				break;
			}
		}
		return colVals[count];
	}

	/**
	 * Method for getting the csvData as list
	 * @return
	 * @throws IOException
	 */
	public List<String> getDataFromCSV(final String path) throws IOException{
		String colVals[]=null;
		//List<String> listOfColVals=new ArrayList<String>();
		csvData = readCSVNClose(path);
		for(String line:csvData){
			colVals=line.split(";");
			listOfColVals.add(colVals[0]);
		}
		return listOfColVals;
	}

	/**
	 * Method for checking the Alert presence
	 */
	public void checkAlert() {
		try {
			wait.until(ExpectedConditions.alertIsPresent());
			Alert alert = driver.switchTo().alert();
			String alrrTxt=alert.getText();
			alert.accept();
			if(alrrTxt.contains("Process done"))
			{
				logger.info("Alert message "+alrrTxt);
			}
			else{
				Assert.fail("Failed due to Alert message as "+alrrTxt);
			}

		} catch (Exception e) {
			logger.info("Alert is not present");
		}
	}

	/**
	 * 
	 * @param fileName
	 * @param fileType
	 * @throws IOException
	 */
	public String getFileData(String fileName,String fileType) throws IOException{
		String val="";
		String datFile=getUpldFilePath(fileName,fileType);
		FileReader file = new FileReader(new File(datFile));
		BufferedReader bufferReader = new BufferedReader(file);
		String temp = bufferReader.readLine();
		while (temp != null) {
			val=val.trim()+"\n"+temp.trim();
			temp=bufferReader.readLine();
		}
		bufferReader.close();
		return val;

	}

	/**
	 * Get Amount in montant format
	 * @param amount
	 * @return
	 */
	public String getAmountInFormat(String amount)
	{
		String temp;
		double testvalue;
		temp=amount.replaceAll("^0*", "");
		if(amount.length()==3&&amount.endsWith("00")&&!amount.endsWith("000"))
		{
			testvalue=Double.parseDouble(amount)/100.0;
			temp=Double.toString(testvalue);
		}
		else if(!"".equals(temp))
		{
			testvalue=Double.parseDouble(temp)/100.0;
			temp=Double.toString(testvalue);
		}

		else{
			temp="0.0";
		}
		return temp;
	}

	/**
	 * Get date in application format
	 * @param amount
	 * @return
	 */
	public String getAmtInAppFormat(String amount)
	{
		String temp;
		Locale locale1 = Locale.UK;
		temp = NumberFormat.getNumberInstance(locale1).format(Double.parseDouble(amount));
		return temp.trim();
	}

	/**
	 * validate data table
	 * @param locPath
	 * @param elemName
	 */
	public void vRowDataInTable(String locPath,String elemName)
	{
		innerloop: for(int i=0;i<=4;i++)
		{
			if(selUtils.isElementPresentxpath(locPath))
			{
				break innerloop;
			}
			else{
				selUtils.clickOnWebElement(selUtils.getCommonObject("search_xpath"));
				selUtils.clickOnWebElement(selUtils.getObject("servertime_xpath"));
				logger.info("selected Server Time radio button");
				selUtils.clickOnWebElement(selUtils.getCommonObject("search_id"));
				logger.info("clicked on Search button");
				waitMethods.waitForelementNotdisplayed(selUtils.getCommonObject("search_id"));
			}
		}
	
		int pageIter,pageNum = 0;
		String[] pageItems=getListItems(selUtils.getCommonObject("page_id"));
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
				selUtils.verifyElementDisp(selUtils.getObjectDirect(By.xpath(locPath)), elemName);
				logger.info("Expected data is present in the application table");
				break outerloop;
			}
			pageNum++;
		}
		if(pageNum==pageIter)
		{
			//Assert.fail("Test is failed because of maximum of "+maxNoPageCount+" pages reached ");
			Assert.fail("The expected "+elemName+" is not present in the page");
		}
	}

	/**
	 * Method for clicking on the Edit Terminal with signature
	 * @param signature
	 */
	public void clkOnElement(String pageid,String eltloc,String elename){
		if(selUtils.isElementPresentCommon(pageid)){
			int pageIter=0,pageNum=0;
			xpath="";
			String[] pageItems=getListItems(selUtils.getCommonObject(pageid));
			if(pageItems.length>=maxNoPageCount){
				pageIter=maxNoPageCount;
			}
			else{
				pageIter=pageItems.length;
			}
			A:for(int count=0;count<pageIter;count++){
				page=selUtils.getCommonObject(pageid);
				selUtils.selectItem(selUtils.getCommonObject(pageid),pageItems[count]);
				waitMethods.waitForWebElementPresent(selUtils.getCommonObject(pageid));
				try{
					xpath=getPath(eltloc).replace("NAME", elename);
					selUtils.getObjectDirect(By.xpath(xpath)).click();
					logger.info("Clicked on the edit terminal of the signature "+elename);
					break A;
				}catch(Exception e){
					pageNum++;
				}
			}
			if(pageNum==pageIter){
				Assert.fail("The expected data  was not present with in the pages "+pageIter);
			}
		}
	}

	/**
	 * Method is to create the Profile by taking the profile name from the input
	 * file "testdata_rythm.yaml" file,@param profileName
	 * @throws IOException 
	 * @throws Exception 
	 */
	public static void profileCrt(String profileName) throws IOException  
	{
		tempFldrNme=FILEUPLOADPATH+TEMPFLDR;
		final String prFileNam=FILEUPLOADPATH+profileName;

		final File srcdir = new File(prFileNam);
		final File destdir= new File(tempFldrNme);
		copySrcToDestDir(srcdir, destdir);
	}

	/**
	 * Copy files from source directory to destination directory
	 * @throws IOException 
	 */
	public static void copySrcToDestDir(File srcDir,File destDir) throws IOException 
	{
		final File[] files = srcDir.listFiles();
		for(File file:files)
		{
			if(!file.toString().endsWith(".svn"))
			{
				FileUtils.copyFileToDirectory(file, destDir);
			}
		}

	}

	/**
	 * Method is to used to call the zip method and copying the zip files into 
	 * TEMP folder,@param val,tempFldrNme
	 * @throws IOException 
	 * @throws Exception 
	 */
	public static void profilezip(String val,String tempFldrNme) throws IOException  
	{
		sourceFolder=tempFldrNme;
		outPutZipFile=FILEUPLOADPATH+val;

		final File dir = new File(sourceFolder);
		zipit(outPutZipFile,dir);

		final File outputzip=new File(outPutZipFile);
		final File tempdir=new File(FILEUPLOADPATH+TEMPPROFL);

		FileUtils.copyFileToDirectory(outputzip, tempdir);

		FileUtils.deleteDirectory(dir);
		outputzip.delete();
	}
	/**
	 * Method is to used to zip the files and folders with the contents
	 * @param output,dir
	 * @throws IOException 
	 */

	public static void zipit(String output,File dir) throws IOException 
	{
		fos= new FileOutputStream(output);
		zos=new ZipOutputStream(fos);
		int bytesRead;

		final byte[] buffer = new byte[1024];

		if(dir.isDirectory())
		{
			final File[] files = dir.listFiles();

			for(int i=0; i < files.length ; i++)
			{

				fin = new FileInputStream(files[i]);
				zos.putNextEntry(new ZipEntry(files[i].getName()));
				int length;

				while((length = fin.read(buffer)) > 0)
				{
					zos.write(buffer, 0, length);
				}
				//close the InputStream
				fin.close();
			}
		}
		else
		{
			final ZipEntry zipEntry = new ZipEntry(dir.getName());
			zos.putNextEntry(zipEntry);

			fin= new FileInputStream(dir);
			while ((bytesRead = fin.read(buffer)) > 0) {
				zos.write(buffer, 0, bytesRead);
			}
			fin.close();			
		}
		//close the ZipOutputStream
		zos.closeEntry();
		zos.close();
		fos.close();
	}

	/**
	 * Add and verify profile
	 * @param appProfileName
	 * @param custName
	 * @throws IOException
	 */
	public void vExistsNAddProfile(String appProfileName,String custName) throws IOException  {

		action.moveToElement(selUtils.getCommonObject("cardpaymt_tab_xpath")).build().perform();
		((JavascriptExecutor) driver).executeScript(JSCLICK,selUtils.getCommonObject("cardpayprofile_xpath"));
		//navigateToSubPage(PROF,selUtils.getCommonObject("cardpaymt_tab_xpath"),selUtils.getCommonObject("cardpayprofile_xpath"));
		//Deleting existing profile if delete icon is displayed
		//vexistProfileNDelete(appProfileName);
		selUtils.clickOnWebElement(selUtils.getCommonObject("plusbtn_xpath"));
		//addProfileToCustomer(PROFILEZIP,appProfileName);
		addProfToCust(PROFILEZIP,appProfileName,custName);	
		//Existing profile
		existingProf(appProfileName);

		/*try{
			if(getModWinDisp(selUtils.getCommonObject("updatprof_wintitl_xpath"), UPDATEPROFILE)){
				logger.info(appProfileName+ " profile already exists");
				Assert.fail(appProfileName+ " profile already exists");
			}
		}catch(Exception e){
			logger.info("Uploaded Profile to the customer");
		}*/

		vAddedProfile(appProfileName,"proftable_css","profcollst_css");
		logger.info("Verified the Profile "+appProfileName+" is added in the profile list");

	}

	/**
	 * Method for uploading and adding profile to the deployed customer
	 * @param profileNameprofcollst_css
	 * @param profilezipName
	 * @throws IOException 
	 */

	public void addProfToCust(final String profilezipName,final String profileName,String customer) throws IOException   {
		if(getModWinDisp(selUtils.getCommonObject("custprofwindow_xpath"),CPPROFILE) && !(profileName.isEmpty())){
			unZipNAssignProfileToCust(CommonConstants.fileUploadpath +profilezipName,profileName,customer);
		}
		else{
			logger.info("There is no profile to upload");
			selUtils.clickOnWebElement(selUtils.getCommonObject("close_link"));
		}
	}
	/**
	 * Method for unzipping the file
	 * @param filePath	 
	 * @throws IOException 
	 */
	public void unZipNAssignProfileToCust(final String filePath,final String expProfile,String customer) throws IOException{
		FileInputStream fis = null;
		ZipInputStream zipIs = null;
		ZipEntry zEntry = null;
		int count=0;
		exists=false;
		final File  frFile = new File(filePath);
		while(!frFile.exists()&&count!=3){
			waitNSec(2);
			count++;
		}
		if(count==3)
		{
			Assert.fail("File read is unsuccessful");
		}

		//try {
		fis = new FileInputStream(filePath);
		final byte[] tmp = new byte[4*1024];
		FileOutputStream fos = null;
		zipIs = new ZipInputStream(new BufferedInputStream(fis));
		while((zEntry = zipIs.getNextEntry()) != null){
			//	try{
			if(zEntry.getName().equals(expProfile)){
				fos = new FileOutputStream(CommonConstants.fileUploadpath+zEntry.getName());
				exists=true;
				int size = 0;
				while((size = zipIs.read(tmp)) != -1){
					fos.write(tmp, 0 , size);
				}
				assignProfileToCust(CommonConstants.fileUploadpath +expProfile,customer);
				waitNSec(3);
				fos.flush();
				fos.close();
				final File ftdel = new File(CommonConstants.fileUploadpath+zEntry.getName());
				ftdel.delete();
				break;
			}
			//}
			/* catch(Exception ex){
					ex.printStackTrace();
				}*/
		}
		if(!exists){
			zipIs.close();
			Assert.fail("There is no profile to upload");
		}
		zipIs.close();
		//}
		/*catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException io) {
			io.printStackTrace();
		}*/
	}
	/**
	 * Method for assigning the profile to the customer
	 * @param filePath
	 */
	public void assignProfileToCust(final String filePath,String customer){
		selUtils.getCommonObject("browsebttn_id").sendKeys(filePath);
		selUtils.selectItem(selUtils.getCommonObject("notassign_custdrpdwn_id"),customer);	
		logger.info(customer +" Customer has selected");
		selUtils.clickOnWebElement(selUtils.getCommonObject("assigncustom_bttn_id"));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();",selUtils.getCommonObject("addprof_okbttn_xpath"));
		//		selUtils.clickOnWebElement(selUtils.getCommonObject("addprof_okbttn_xpath"));
		logger.info("Card payment profile window clicked on OK button");
	}
	/**
	 * Method for verifying whether the profile is added successfully or not 
	 * in the profile list
	 * @param profileName
	 * @param profListLoctr
	 */
	public void vAddedProfile(final String profileName,final String profListLoctr,String profcolheadersloc){
		profileTxt=profileName.replaceAll(".zip", "").trim();
		colIndex=selUtils.getIndexForColHeader(profcolheadersloc,PROFLENAME);
		verifyLvlColLvlValPresence(profListLoctr,colIndex,profileTxt);
	}
	/**
	 * verify existing profile
	 * @param profileName
	 */
	public void existingProf(String profileName)
	{	
		String path;
		try{
			path=getCommonPath("updatprof_wintitl_xpath");
			if( selUtils.isElementPresentxpath(path)){
				Assert.fail(profileName+" is existing profile due to fail ");
			}
		}catch(Exception e){
			logger.info(profileName+" profile added successfull");
		}
	}
	/**
	 * Zip the profiles
	 */
	public void zipProfiles()
	{
		try{
			sourceFolder=FILEUPLOADPATH+TEMPPROFL;
			outPutZipFile=FILEUPLOADPATH+PROFILEZIP;
			final File profdir = new File(sourceFolder);
			zipit(outPutZipFile,profdir);
			FileUtils.deleteDirectory(profdir);
		}catch(Exception e){
			Assert.fail("Failed while zip the profiles");
		}
	}

	/**
	 * Method to report error,when duplicate data is given
	 * @param locator
	 */
	public void  reportErrMessage(String locator)
	{
		exists=selUtils.isElementPresentCommon(locator);
		if(exists)
		{
			String errText=selUtils.getCommonObject(locator).getText().trim();	
			Assert.fail("failed due to "+errText);				
		}
	}


	/**
	 * Method to report error,when duplicate data is given
	 * @param locator
	 */
	public void monitorErrMsgDisp(String locator)
	{
		if(selUtils.getCommonObject(locator).isDisplayed())
		{
			String errText=selUtils.getCommonObject(locator).getText().trim();	
			Assert.fail("failed due to "+errText);				
		}
		else
		{
			logger.info("Clicked on button Successfully");
		}
	}

	/**
	 * verify column value presence and click on customer name
	 * @param custName
	 */
	public void clkCustNameList(String custName)
	{
		colIndex=selUtils.getIndexForColHeader("colheaders_css", NAMECOL);
		verifyLvlColLvlValPresence("entitytablelst_css", colIndex, custName);
		clkOnDirectObj("customer_link","CustName",custName);
		logger.info("Clicked on "+custName+ " customer");
	}

	/**
	 * Method to verify not presence of sub mod
	 * @param subModuleName,@param tab,@param subModule
	 * 
	 */
	public void vSubModPresent(String subModuleName,WebElement tab,String subModule){ 		
		action = new Actions(driver);
		action.moveToElement(tab).build().perform();
		Assert.assertTrue(selUtils.isElementPresentCommon(subModule),subModuleName+ "is not present under main tab");
		logger.info("Verified "+subModuleName+ "is present under main tab");
	}
	/**
	 * Method to check collapse of plus button and click
	 * @param objloc1
	 * @param objloc2
	 */
	public void clkOnModRtsPlsBttn(String objloc1,String objloc2){
		if(selUtils.getObject(objloc1).getAttribute("class").contains("closeoff"))
		{
			selUtils.clickOnWebElement(selUtils.getObject(objloc2));
		}
	}

	/**
	 * Method to verify created proj in pending prov tab
	 * @param projectName
	 */
	public void clkOnPendingProv(String projname){

		((JavascriptExecutor) driver).executeScript("arguments[0].click();",selUtils.getCommonObject("pendingprovtab_xpath"));
		//selUtils.clickOnWebElement(selUtils.getCommonObject("pendingprovtab_xpath"));
		logger.info("Clicked on the Pending Provisioning TAB");
		waitMethods.waitForWebElementPresent(selUtils.getObject("pendingprov_displaytxt_xpath"));
		/*colIndex=getIndexForColHeader("colheaders_css", PROJECTNAME);
		verifyLvlColLvlValPresence("entitytablelst_css",colIndex,projname);*/
	}

	/**
	 * Method to click on deploy button-Deploying the request
	 */
	public void clickOnDeploy(){
		selUtils.clickOnWebElement(selUtils.getObject("pendingdeploybttn_id"));
		selUtils.acceptAlert();
		logger.info("Clicked on deploy button");
	}
	/**
	 * Click on the expected button of the modal window and verify the message. 
	 **/
	public void cnfmPopupActMsg(String loc, String msgLoc, String msg) {
		selUtils.clickOnWebElement(selUtils.getObject(loc));
		vExpValPresent(msgLoc, msg);
	}	


	/**
	 * Verify Element Text is present
	 */	
	public void vExpValPresent(String object, String text){
		waitMethods.waitForTxtPresent(selUtils.getCommonObject(object), text);
		Assert.assertTrue((selUtils.getCommonObject(object).getText().trim().equalsIgnoreCase(text)), "Expected message is :'"+text+"' But Actual message is"+selUtils.getCommonObject(object).getText().trim());
		logger.info("Verified, expected value :'"+text+"' is displayed.");
	}
	/**
	 * verify two values in a table	
	 * * @param serialno
	 * @param partno
	 */
	public void vMultiTableValue(String locator,String serialno,String partno)
	{
		xpath = TestBase.getPath(locator).replace(NAME,serialno).replace(VALUE, partno);
		webElement=selUtils.getObjectDirect(By.xpath(xpath));
		selUtils.verifyTextEqualsWith(webElement, partno);
	}

	/**
	 * verify two values in a table	
	 * * @param serialno
	 * @param partno
	 */
	public void vMultiValue(String locator,String name,String aname,String value)
	{
		xpath = TestBase.getPath(locator).replace(NAME,name).replace(STATUS,aname).replace(VALUE, value);
		webElement=selUtils.getObjectDirect(By.xpath(xpath));
		selUtils.verifyTextEqualsWith(webElement, value);
	}
	/**
	 * verify one value in a table
	 * @param signature
	 */

	public void vSingleTblValue(String locator,String value,String data)
	{
		xpath = TestBase.getPath(locator).replace(value,data);
		webElement=selUtils.getObjectDirect(By.xpath(xpath));
		selUtils.verifyTextEqualsWith(webElement, data);
	}

	/**
	 * Accept alert and display logger message
	 */
	
	public void alertAccept()
	{
		wait.until(ExpectedConditions.alertIsPresent());
		Alert alert = driver.switchTo().alert();
		String alrrTxt=alert.getText();
		alert.accept();
	}
	
}


