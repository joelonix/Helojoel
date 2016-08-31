package com.ingenico.testsuite.cardpayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SuiteCardPayment.java $
$Id: SuiteCardPayment.java 18233 2016-04-26 06:11:23Z rkahreddyga $
 */

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.server.handler.AcceptAlert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ingenico.base.SelUtils;
import com.ingenico.base.TestBase;
import com.ingenico.common.CommonConstants;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
/**
 * Common methods and variables for the cardpayment suitebase
 *
 */
public class SuiteCardPayment extends TestBase {
	/**
	 * Declaration of common variables
	 */
	public static String posNum,numcomm,outPutZipFile=null,sourceFolder=null,refVal,forVal,voidVal;
	public final static String PROFILESLIST="Profiles List",EDITPROFILE="Edit profile assignment",CPPROFILE="Card Payment Profile",
			SELECTPROFILE="Select profile",MANUALLOCALZTION="Manual localization",EMBEDDED="Embedded",AUTOLOCALZN="Automatic localization",
			NUMCOMM="NUM_NumComm",NUMCOMMCOL="Numcomm",POSNUMBERCOL="POS Number",POSPOSNUM="POS_POSNumber",SERNOCOL="Serial Number",UPDATEPROFILE="Update Profile",
			PROFLENAME="Profile Name",SELECTNEWZONE="Select New Zone",LOCALZTION="Localization",SELECTNUMCOM="Select NumComm",PROVTAB="Provisioning",POSDECTAB="POS Declaration",
			FILEUPLOADPATH="data/file_uploads/",TEMPPROFL="TEMPPROF",TEMPFLDR="TEMP",PROFILEZIP="profiles.zip",SELAXISLOC="Select Axis Location",SUCSSFULLYADDED="successfully added";
	public List<String[]> listfromCSV;
	public List<String> csvData= new ArrayList<String>(), csv;	
	public final static String[] NUMCOMLOC={"numcomm_id","numcodebank_id","numaddr_id"};
	public final static String[] NUMCOMLOCjs={"NUM/NumComm","NUM/CodeBank","NUM/AdrsX25"};
	public String [] opVals;
	public static FileOutputStream fos;
	public static ZipOutputStream zos;
	public static FileInputStream fin;
	 public static  Locale locale = Locale.ENGLISH;
	// final String axislocation=testDataOR.get("axis_location");




	//TODO*****Framework Related Functions******
	/** 
	 * Initializes suite execution	
	 */
	@BeforeSuite(alwaysRun=true)
	void initSetUp()  {
		initialize();
	}	

	//TODO*****Common functions to all CP sub modules******

	/**
	 * Method for creating the PosHeader
	 * 
	 */
	public  String createPosHeader(){
		selUtils.clickOnWebElement(selUtils.getCommonObject("plusbtn_xpath"));
		if(getModWinDisp(selUtils.getCommonObject("popup_wintitle_id"),POSNUMHEADER)){
			posHeader=setPosHeaderVals(testDataOR.get("axis_location"));
		}
		return posHeader;
	}

	/**
	 * Method for entering the values while creating the posHeader	
	 * @param teamUnit
	 * @return posHeader
	 */


	public String setPosHeaderVals(final String axisLoc){
		if(selUtils.getObject("posnum_axisloc_id").isEnabled()){
			selUtils.selectItem(selUtils.getObject("posnum_axisloc_id"), axisLoc);
		}
		if(SelUtils.getSelectedItem(selUtils.getObject("posnum_axisloc_id")).equals(axisLoc)){
			posNum=testDataOR.get("POS_number_header");
			selUtils.populateInputBox("posheadertxt_id",posNum );
			posHeader=selUtils.getObject("posheadertxt_id").getAttribute("value");
			selUtils.clickOnWebElement(selUtils.getCommonObject("okbttn_xpath"));
		}
		return posHeader;
	}
	/**
	 * Method for verifying the error message for the duplicate posheader numbers
	 * @return posHeader
	 */
	public String verifyDuplicatePosHeaderMsg(){
		while(!(selUtils.getCommonObject("posheder_errmsg_id").getAttribute("style").contains("none"))){
			posHeader=createPosHeader();
		}
		return posHeader;
	}
	/**
	 * Method for fileuploads
	 * @param name
	 * @throws IOException	 
	 */
	/*public void fileUpload(String name) throws IOException 
	{
		profile[0]=CommonConstants.AUTOITPATH +FILEUPLOADEXE;
		profile[1]=CommonConstants.fileUploadpath +name;
		Runtime.getRuntime().exec(profile);
		logger.info("File Uploaded successfully,name of the file "+name);
	}*/

	/**
	 * Method for assigning the profile to the customer
	 * @param filePath
	 */
	public void assignProfileToCust(final String filePath){
		selUtils.getCommonObject("browsebttn_id").sendKeys(filePath);
		selUtils.selectItem(selUtils.getObject("notassign_custdrpdwn_id"),testDataOR.get("customer"));	
		selUtils.clickOnWebElement(selUtils.getObject("assigncustom_bttn_id"));
		selUtils.clickOnWebElement(selUtils.getObject("addprof_okbttn_xpath"));
	}

	/**
	 * Method to verify whether the profile exists to customer or not
	 * @param appProfileName
	 */
	public void vProfileExistsToCust(String appProfileName){
		if(!(appProfileName.isEmpty()))
		{ 
			navigateToSubPage(CUSTPROV,selUtils.getCommonObject("cardpaymt_tab_xpath"),selUtils.getCommonObject("custprov_xpath"));
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), testDataOR.get("customer"));
			selUtils.clickOnWebElement(selUtils.getCommonObject("configtab_xpath"));
			logger.info("Clicked on the Configuration TAB");
			colIndex=selUtils.getIndexForColHeader("gprscolheader_css",PROFLENAME);
			verifyLvlColLvlValPresence("entitytablelst_css",colIndex,appProfileName.replaceAll(".zip", "").trim());
			logger.info("Verified the Profile "+appProfileName+" is added to the customer");				
		}
	}
	/**
	 * Navigate to cardpayment Provisioning sub tab
	 * @param projectName
	 
	 */
	public void vProjNameinProv(String projectName)
	{
		navigateToSubPage(CUSTPROV,selUtils.getCommonObject("cardpaymt_tab_xpath"),selUtils.getCommonObject("custprov_xpath"));
		selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), testDataOR.get("customer"));
		logger.info("Selected the customer as "+testDataOR.get("customer"));

		//verify and delete existing project name for pos creation
		selUtils.clickOnWebElement(selUtils.getCommonObject("pendingprovtab_xpath"));
		verifyExistingData("deleteprojectname_xpath","colheaders_css","colallrows_xpath",projectName, PROJECTNAME);

		//Go to "Provisioning" sub-tab 
		selUtils.clickOnWebElement(selUtils.getObject("provtab_xpath"));
		logger.info("Clicked on Provisioning tab");
	}
	/**
	 * Enter project name and save then validate in pending Provisioning list
	 * @param projectName
	 
	 */
	public void enterPNameAndVal(String projectName)
	{
		selUtils.clickOnWebElement(selUtils.getObject("viewconfignext_id"));
		logger.info("Clicked on next button");

		//Enter a 'Project Name' and then click 'Save' button
		selUtils.getObject("projname_id").sendKeys(projectName);
		selUtils.clickOnWebElement(selUtils.getObject("configsumsavebttn_xpath"));
		waitNSec(3);
		logger.info("Eneter a project name "+projectName+" and click on save button");
		selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), testDataOR.get("customer"));
		/*
		 * Above step is just a work around step,since we faced issue when 
		 * on pending provisioning tabclicking
		 */
		clkOnPendingProv(projectName);
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
	 * Method to click on Prov or Pos Tab 
	 * @param tabloc,@param tablabel
	 */
	public void clckOnProvOrPosTab(String tabloc,String tablabel){
		waitMethods.waitForelementNotdisplayed(selUtils.getObjectDirect(By.xpath(getPath("pendingaxisxonfig_xpath"))));
		selUtils.clickOnWebElement(selUtils.getObject(tabloc));
		logger.info("Clicked on "+tablabel);
	}

	/**
	 * Method to verify deployed proj not in pending prov list
	 * @param projname
	 */
	public void vProjNotExistInPendingProv(String projname){
		clickOnDeploy();
		Assert.assertTrue(selUtils.getCommonObject("succ_deploymsg_xpath").getText().contains(SUCSSFULLYDPLYD));
		xpath=getPath("project_xpath").replace(NAME, projname);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
		Assert.assertFalse(selUtils.isElementPresentxpath(xpath),projname+"is present in the 'Pending Provisioning' list");
		logger.info(projname+ " is not in the 'Pending Provisioning' list any longer");
	}
	/**
	 * Method for clicking on the Edit Terminal with signature
	 * @param signature
	
	 */
	public void clkOnEditTerminal(String signature){
		if(selUtils.isElementPresentCommon("pageindex_id")){
			int pageIter=0,pageNum=0;
			xpath="";
			String[] pageItems=getListItems(selUtils.getCommonObject("pageindex_id"));
			if(pageItems.length>=maxNoPageCount){
				pageIter=maxNoPageCount;
			}
			else{
				pageIter=pageItems.length;
			}
			A:for(int count=0;count<pageIter;count++){
				page=selUtils.getCommonObject("pageindex_id");
				selUtils.selectItem(selUtils.getCommonObject("pageindex_id"),pageItems[count]);
				waitMethods.waitForWebElementPresent(selUtils.getCommonObject("pageindex_id"));
				try{
					xpath=getPath("editterminal_xpath").replace("SIGNTUR", signature);
					selUtils.getObjectDirect(By.xpath(xpath)).click();
					logger.info("Clicked on the edit terminal of the signature "+signature);
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
	 * Method for appending value to a string
	 * @return
	 */
	public String appendValToStr(String number, String appendedVal){
		String val="";
		for(int i=0;i<number.length();i++){
			val=val+appendedVal+number.charAt(i);
		}
		if(val.length()!=20){
			int count=20-(val.length());
			for(int j=0;j<count;j++){
				val= val.trim()+0;
			}
		}
		return val.trim();
	}
	/**
	 * Method for getting the current date
	 * @return
	 */
	public static String getCurrentdate(){
		DateFormat dateformat = new SimpleDateFormat("ddMMyyyyhhmmss",locale);
		Calendar actual = Calendar.getInstance();
		return dateformat.format(actual.getTime());
	}
	/**
	 * 
	 * @param date
	 * @return
	 */
	public String getdateIndiffForm(String dateinFormate)
	{
		Date date = null;
		DateFormat datefrmt = null;
		try {
			datefrmt=new SimpleDateFormat("ddMMyyyyhhmmss",locale);
			date = datefrmt.parse(dateinFormate);
			datefrmt=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",locale);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return datefrmt.format(date);
	}

	/**
	 * Add num comm
	 * @param numcomLoc
	 * @param numcomVal
	 */
	public void addNumcommval(String[] numcomLoc,String[] numcomVal )
	{
		int indx=0;
		for(String nloc:numcomLoc)
		{
			   //waitNSec(2);
			   selUtils.populateInputBox(nloc, numcomVal[indx++]);
			   //wait.until(ExpectedConditions.visibilityOf((selUtils.getObject(nloc))));
			   //waitMethods.waitForWebElementPresent(selUtils.getObject(nloc));
			  // selUtils.getObject(nloc).sendKeys(numcomVal[indx++]);
			   /*JavascriptExecutor jse = (JavascriptExecutor) driver;
			   //jse.executeScript("document.getElementById('NUM/NumComm').value = '606509';");
			   jse.executeScript("document.getElementById('"+nloc+"').value = '"+numcomVal[indx++]+"';");
			   logger.info("entered numcom val");*/
			  /* if(selUtils.getObject(nloc).getAttribute("value").isEmpty())
			   {
				waitMethods.waitForWebElementPresent(selUtils.getObject(nloc));   
			    selUtils.getObject(nloc).sendKeys(numcomVal[indx++]);
			    logger.info("entered "+numcomVal[indx++]);
			   }*/
		}
	}

	/**
	 * Add pos
	 * @param posLoc
	 * @param posVal
	 */
	public void addPOSval(String[] posLoc,String[] posVal )
	{
		int cnt=0;
		for(String ploc:posLoc)
		{
			selUtils.selectValueInDropDown(selUtils.getObjects(ploc), posVal[cnt++]);
		}
	}

	/**
	 * Method for reading and editing
	 * the tagValues of XML file	
	 * @param filepath
	 * @param tagNames
	 * @param tagvals
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */

	public void readXmlNEdit(String filepath,String[] tagNames, String [] tagvals) throws IOException, ParserConfigurationException, SAXException{
		try {
			String profile=getprofileFilePath(filepath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(profile);
			doc.getDocumentElement().normalize();
			Node unit = doc.getElementsByTagName("unit").item(0);
			NodeList list = unit.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				for(int j=0;j<tagNames.length;j++){
					if (node.getNodeName().equals(tagNames[j])){
						node.setTextContent(tagvals[j]);
					}
				}
			}
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(profile));
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Method for getting the entire data of particular column
	 * @param tableHeaderLoc
	 * @param colLabel
	 * @param colDataLoc
	 * @return
	
	 */
	public List<String> getColData(String tableHeaderLoc,String colLabel,String colDataLoc){
		final List<String> colData = new ArrayList<String>();
		final List<WebElement> elts;
		String colTxt;
		colIndex=selUtils.getIndexForColHeader(tableHeaderLoc, colLabel);
		xpath=getPath(colDataLoc).replace("COLINDEX", Integer.toString(colIndex+1) );
		elts=selUtils.getObjectsDirect(By.xpath(xpath));
		for(iter=1;iter<elts.size();iter++){
			colTxt=elts.get(iter).getText();
			colData.add(colTxt.trim());
		}
		return colData;

	}
	/**
	 * Method for getting the Profile path
	 * @param file
	 * @return
	 */
	public String getprofileFilePath(String file)
	{
		String filePath = null;
		try
		{
			logger.info(" Get the path of "+file+" file ");
			filePath = file;
		}
		catch(Exception e)
		{
			Assert.fail("Failed during getting path of the file",e);
		}
		return filePath;
	}

	/**
	 * Select Axis location
	 * @param axislocation
	 */
	public void selAxisLoc(String axislocation)
	{
		if(getModWinDisp(selUtils.getObject("sel_axisloc_xpath"),SELAXISLOC))
		{
			selUtils.selectItem(selUtils.getObject("posnum_axisloc_id"), axislocation);
			logger.info("Selected the axis location "+ axislocation);
			selUtils.clickOnWebElement(selUtils.getObject("axislocnext_id"));
			logger.info("Clicked on next button");
		}
		else{
			logger.info("No more than one axislocation so "+SELAXISLOC+" not present");
		}
	}

	/**
	 * Set text vlues in to the field
	 * @param element
	 * @param value
	 */
	public void setText(WebElement element, String value)
	{
		copyToClipbord(value);
		element.click();
		element.sendKeys(Keys.chord(Keys.CONTROL, "v"), "");
	}

	/**
	 * copy the data 
	 * @param copyTo
	 */
	public void copyToClipbord(String copyTo)
	{
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection str = new StringSelection(copyTo);
		clipboard.setContents(str, null );
	}
	
	/**
	 * Method to verify,error message when inputting,wrong format of,
	 * numcomm or posnumber
	 * @param locator
	 */
	public void vErrorMessage(String locator)
	{
		if(selUtils.getCommonObject(locator).isDisplayed())
		{
			String errText=selUtils.getCommonObject(locator).getText();
			if(errText.contains("Incorrect fields :"))
			{
				errText=errText.substring(errText.indexOf(':')+1).trim();
			}
			Assert.fail(errText+" failed due to wrong num_comm/pos_number format");	
		}
	}
	
	/**
	 * Execute simulator command in host machine
	 * @param host
	 * @param user
	 * @param password
	 * @param command
	 */
	public void sshCommandExecuter(String host,String user,String command)
	{
		try{
			int commandStatus;
			String tmpstr;
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			config.put("PreferredAuthentications","publickey,keyboard-interactive,password");
			JSch jsch = new JSch();
			jsch.addIdentity(CommonConstants.TRUSTSTOREPATH);
			Session session=jsch.getSession(user, host, 22);
			//session.setPassword(password);
			session.setConfig(config);
			session.connect();
			logger.info("SSH Connection opened");
			Channel channel=session.openChannel("exec");
			((ChannelExec)channel).setCommand(command);
			channel.setInputStream(null);
			((ChannelExec)channel).setOutputStream(System.err);
			InputStream inputStream=channel.getExtInputStream();
		//	Thread.sleep(2000);	
			channel.connect();
			byte[] tmp=new byte[1024];
			while(true){
				while(inputStream.available()>0){
					iter=inputStream.read(tmp, 0, 1024);
					if(iter<0)
					{
						break;
					}
					new String(tmp, 0, iter);
				}
				tmpstr = new String(tmp, "UTF-8");
				if(channel.isEOF()){
					commandStatus=channel.getExitStatus();
					logger.info("exit-status: "+commandStatus);
					if(commandStatus!=0)
					{
						Assert.fail("Simulator command is not executed properly so the exit status is "+commandStatus);
					}
					else if(tmpstr.contains("NACK RECEIVED"))
					{
						Assert.fail("Simulator command execution in host machine"+host+" is not success with acknowledge NACK RECEIVED");
					}
					break;
				}
				try{Thread.sleep(1000);}catch(Exception ee){}
			}
			channel.disconnect();
			session.disconnect();
			logger.info("SSH Connection closed");
		}catch(Exception e){
			Assert.fail("Simulation command execution is failed in host machine"+host);
		}
	}

	/**
	 * Copy file from windows machine to VMTools(host) machine
	 * @param host
	 * @param user
	 * @param password
	 */
	public void fileCopyNmove(String host,String user,String noe2eFile,String e2eFile)
	{
		java.util.Properties config = new java.util.Properties(); 
		config.put("StrictHostKeyChecking", "no");
		config.put("PreferredAuthentications","publickey,keyboard-interactive,password");
		//String copyTo = "/home/uauto"; 
		String copyTo = "/usr/share/autotest";
		try {
			JSch jsch = new JSch();
			Session session = null;
			jsch.addIdentity(CommonConstants.TRUSTSTOREPATH);
			session = jsch.getSession(user, host, 22);
			session.setConfig(config);
			//session.setPassword(password);
			session.connect(); 
			logger.info("Session connection successful");
			Channel channel = session.openChannel("sftp");
			channel.connect();
			logger.info("Channel connection successful");
			waitNSec(1);
			ChannelSftp sftpChannel = (ChannelSftp) channel; 
			sftpChannel.put(noe2eFile, copyTo);
			sftpChannel.put(e2eFile, copyTo);
			logger.info("File copied successfully");
			sftpChannel.exit();
			session.disconnect();
		} catch (JSchException e) {
			Assert.fail("Unable to open session to copy file in to host machine "+e.getMessage(),e);
		} catch (SftpException e) {
			Assert.fail("Unable to open sftp channel to copy file in to host machine "+e.getMessage(),e);
		}
	}
	
	/**
	 * Common method to tests smr136,smr142,smr139 and smr174
	 * @param customer
	 */
	public void addProfToCust(String customer)
	{
		logger.info("Step 1, 2:");
		navigateToSubPage(CUSTPROV,selUtils.getCommonObject("cardpaymt_tab_xpath"),selUtils.getCommonObject("custprov_xpath"));
		selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"),customer);
		logger.info("Selected the customer "+customer);
		
		//verify and delete existing project name for pos creation
		selUtils.clickOnWebElement(selUtils.getCommonObject("pendingprovtab_xpath"));
		verifyExistingData("deleteprojectname_xpath","colheaders_css","colallrows_xpath", projectName, PROJECTNAME);

	    //Go to "Provisioning" sub-tab and click the "+" button		
		logger.info("Step 3:");
		selUtils.clickOnWebElement(selUtils.getObject("provtab_xpath"));
		logger.info("Clicked on the Provisioning TAB");
		selUtils.clickOnWebElement(selUtils.getObject("addprov_xpath"));
		logger.info("Clicked on the plus button");
		
		//If  there are more than one Axis location select axis_location
		//And click next button
		logger.info("Step 4:");
		selAxisLoc(testDataOR.get("axis_location"));

		//Select an existing EMV profile and click 'Next' button			
		logger.info("Step 5:");
		Assert.assertTrue(getModWinDisp(selUtils.getObject("selctprofilewin_xpath"), SELECTPROFILE),"Modal window is not displayed");
		selUtils.selectItem(selUtils.getObject("profile_id"), testDataOR.get("emvfr_profile").replaceAll(".zip", "").trim());
		logger.info("Selected the profile as "+ (testDataOR.get("emvfr_profile").replaceAll(".zip", "")).trim());
		selUtils.clickOnWebElement(selUtils.getObject("profilenext_id"));
	}
	
	/**
	 * select profile to customer
	 * Common Steps for SMR143 and SMR50
	 *
	 */
	
	public void selProfToCust()
	{
	final String axislocation=testDataOR.get("axis_location");
	
	navigateToSubPage(CUSTPROV,selUtils.getCommonObject("cardpaymt_tab_xpath"),selUtils.getCommonObject("custprov_xpath"));
	selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), testDataOR.get("customer"));

	//verify and delete existing project name for pos creation
	selUtils.clickOnWebElement(selUtils.getCommonObject("pendingprovtab_xpath"));
	verifyExistingData("deleteprojectname_xpath","colheaders_css","colallrows_xpath",projectName, PROJECTNAME);

	//Go to "Provisioning" sub-tab and click the "+" button
	logger.info("Step 3:");
	selUtils.clickOnWebElement(selUtils.getObject("provtab_xpath"));
	logger.info("Clicked on Provisioning tab");
	selUtils.clickOnWebElement(selUtils.getObject("addprov_xpath"));
	logger.info("Clicked on plus icon");

	//If  there are more than one Axis location select axis_location
	//And click next button
	logger.info("Step 4:");
	selAxisLoc(axislocation);
	
	//Select an existing application profile  and next button
	logger.info("Step 5:");
	Assert.assertTrue(getModWinDisp(selUtils.getObject("selctprofilewin_xpath"), SELECTPROFILE),"Modal window is not displayed");
	selUtils.selectItem(selUtils.getObject("profile_id"), testDataOR.get("emvfr_profile").replaceAll(".zip", "").trim());
	logger.info("Selected the profile as "+ (testDataOR.get("emvfr_profile").replaceAll(".zip", "")).trim());
	selUtils.clickOnWebElement(selUtils.getObject("profilenext_id"));
	logger.info("Clicked on next button");

	//Select 'Manual localization' in the "Order" dropdown list
	//select the 'NumComm and POS' option and then click next button
	logger.info("Step 6:");
	selUtils.selectItem(selUtils.getObject("operationorder_id"), MANUALLOCALZTION);
	logger.info("Selected "+MANUALLOCALZTION);
	
}
	/**
	 * Click on pending provisioning , edit projectname
	 * Deploy request
	 * Common Steps for SMR142 and SMR174
	 *
	 */
	public void editPNameNDeply()
	{
		final String cust=testDataOR.get("customer"),projectName=testDataOR.get("project_name");
		
		selUtils.clickOnWebElement(selUtils.getObject("addposgbutt_xpath"));
		selUtils.clickOnWebElement(selUtils.getObject("enterposnext_xpath"));
		reportErrMessage("pos_num_err_id");
		logger.info("Select manual and added pos details then next button");

		//Click 'Next' button
		logger.info("Step 10:");
		selUtils.clickOnWebElement(selUtils.getObject("viewconfignext_id"));
		logger.info("Clicked on next button");

		//Enter a 'Project Name' and click on the 'Save' button			
		logger.info("Step 11:");
		selUtils.populateInputBox("projname_id", projectName);
		logger.info("Entered the projectName as "+projectName);
		selUtils.clickOnWebElement(selUtils.getObject("configsumsavebttn_xpath"));

		//Go to "Pending Provisioning" sub-tab		
		selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), cust);
		logger.info("Step 12:");
		clkOnPendingProv(projectName);

		//Click the edit button of projectName	
		logger.info("Step 13:");
		clkOnDirectObj("editpospending_xpath","NAME",projectName);
		logger.info("Clicked on the edit button");

		//Deploy the request,click on prov tab			
		logger.info("Step 14:");
		clickOnDeploy();
		Assert.assertTrue(selUtils.getCommonObject("succ_deploymsg_xpath").getText().contains(SUCSSFULLYDPLYD),SUCSSFULLYDPLYD+"does not appear");
		logger.info("Verified the success message");

	}
	
	
	
	/**
	 * Goto card payment and then submodule profile
	 * Click on edit button of profile and  customer assignment
	 * @param profileName
	 * @param customer
	 */
	public void  editProAsigment(String profileName,String customer)
	{
		navToSubPage("cardpaymt_tab_xpath", "cardpayprofile_xpath", PROF);
		//Click on edit profile
		//Select customer from assigned customer to not not assigned customer
		colIndex=selUtils.getIndexForColHeader("profcollst_css",PROFLENAME);
		verifyLvlColLvlValPresence("entitytablelst_css", colIndex, profileName);
		clkOnDirectObj("editprofile_xpath",NAME,profileName);
		logger.info("Clicked on Edit profile "+profileName);
				
		//Edit profile assignment
		if(getModWinDisp(selUtils.getObject("editprofileasscustr__xpath"),EDITPROFILE)){
			clkOnDirectObj("editprocustomer_xpath",CUSTOMER,customer);
			selUtils.clickOnWebElement(selUtils.getObject("caneditassigncust_id"));
			logger.info("Click on assigned to not assigned customer list");
			selUtils.clickOnWebElement(selUtils.getObject("editproassigcustwinok_xpath"));
			logger.info(EDITPROFILE+" window clicked on OK button");
		}
		Assert.assertTrue(selUtils.getCommonObject("posheder_errmsg_id").getText().contains(SUCSSFULLYEDITED),SUCSSFULLYEDITED+" sucess message does not appear");
		logger.info("Profile with name "+profileName+" successfully edited");
	}
	
	/**
	 * Click on delete button of profile and then click ok
	 * @param profileName
	 */
	public void  delProAsigment(String profileName)
	{
		navToSubPage("cardpaymt_tab_xpath", "cardpayprofile_xpath", PROF);
		//Click on edit profile
		//Select customer from assigned customer to not not assigned customer
		colIndex=selUtils.getIndexForColHeader("profcollst_css",PROFLENAME);
		verifyLvlColLvlValPresence("entitytablelst_css", colIndex, profileName);
		clkOnDirectObj("deleteprofile_xpath",NAME,profileName);
		alertAccept();
		logger.info("Profile with name "+profileName+" successfully deleted");		
	}
	
	
	
	
	
	/**
	 * Click on edit button of the pending operation
	 * Deploy request
	 * Common Steps for SMR136 and SMR208
	 *
	 */
	/*public void editPendPosNDep()
	{
		selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), testDataOR.get("customer"));
		
		 * Above step is just a work around step,since we faced issue when 
		 * on pending provisioning tabclicking
		 

		//Click the edit button of the pending operation 
		clkOnPendingProv(projectName);
		clkOnDirectObj("editpospending_xpath","NAME",projectName);
		logger.info("Clicked on the edit button");

		//click on the 'Deploy' and click on POS tab
		logger.info("Step 12:");
		clickOnDeploy();
		Assert.assertTrue(selUtils.getCommonObject("succ_deploymsg_xpath").getText().contains(SUCSSFULLYDPLYD),SUCSSFULLYDPLYD+"sucess message does not appear");
		logger.info("Verified the success message");
	}*/

}








