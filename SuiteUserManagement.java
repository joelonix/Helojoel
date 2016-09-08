package com.ingenico.testsuite.usermanagement;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/usermanagement/SuiteUserManagement.java $
$Id: SuiteUserManagement.java 18071 2016-04-14 09:28:56Z rkahreddyga $
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;

import com.ingenico.base.SelUtils;
import com.ingenico.base.TestBase;
import com.ingenico.common.CommonConstants;

/**
 * Common methods and variables of usermanagement suite
 *
 */
public class SuiteUserManagement extends TestBase {
	//Declaration of common variables
	public String[] usrmngRightsChbx={"intusrmngt_chkbx_id","extusrmngt_chkbx_id"},
			profChkBxs={"rcptn_ckbx_id","stoissue_ckbx_id","depundep_ckbx_id",
			"mainrepair_ckbx_id","inventory_ckbx_id","inspevnt_ckbx_id","decommterm_ckbx_id"};
	//Declaration of common variables
	public String ckbxArrTwo[]={"assttrkingrole_id","eventss_id","terminalrole_id"};
	public List<String>  appList;
	public final static String SUCCCREATED="successfully created",MONOUSER="MONOUSER",INVALIDAUTH="Invalid Authentication Information",
			DACTVATE="Deactivate",ACTIVATE="activate";


	//Framework Related Functions

	/** 
	 * Initializes suite execution
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws Exception 
	 */
	@BeforeSuite(alwaysRun=true)
	void initSetUp()   {
		initialize();
	}

	/**
	 * Method for Profile Creation
	 */
	public void createProfile(String yamlval[]){
		waitMethods.waitForWebElementPresent(selUtils.getObject("profname_id"));
		selUtils.populateInputBox("profname_id", testDataOR.get("multi_profile_name"));
		selUtils.slctChkBoxOrRadio(selUtils.getObject("intnlusr_chbx_id"));
		selUtils.slctChkBoxOrRadio(selUtils.getObject("readrite_rdbttn_id"));
		selChkOrRadiobttns(yamlval,usrmngRightsChbx);
		selUtils.selectDropItem(selUtils.getObject("ep_entities_id"), testDataOR.get("entity"));
		selUtils.selectDropItem(selUtils.getObject("ep_cust_id"), testDataOR.get("customer"));
		//new step
		selUtils.slctChkBoxOrRadio(selUtils.getObject("ftp_rdwrt_radio_id"));
		selcRoles("assttrkingrole_id","astrking_roles_radiobttns_xpath",testDataOR.get("multi_prof_asset_tracking_role"));
		selcRoles("terminalrole_id","terminalroles_radiobttns_xpath",testDataOR.get("multi_prof_terminal_role"));
		selcRoles("eventss_id","events_ckboxes_xpath",testDataOR.get("events"));
		selUtils.selectItem(selUtils.getObject("ingestaterole_id"), testDataOR.get("multi_prof_ingestate_role"));
		selUtils.clickOnWebElement(selUtils.getCommonObject("save_link"));
	}

	/**
	 * Method for selecting the level from level list
	 * @author Nagaveni.Guttula
	 */
	public void selectLevel(String level){
		selUtils.selectItem(selUtils.getObject("selectlevel_id"),level);
		xpath=getPath("level1_id");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(xpath)));
		if("Level 1".equalsIgnoreCase(level)){
			selUtils.selectItem(selUtils.getObject("level1_id"), testDataOR.get("zone_nameone_a"));
		}
		else if("Level 2".equalsIgnoreCase(level)){
			waitMethods.waitForWebElementPresent(selUtils.getObject("level1_id"));
			selUtils.selectItem(selUtils.getObject("level1_id"), testDataOR.get("zone_nameone_a"));
			selUtils.selectItem(selUtils.getObject("level2_id"), testDataOR.get("zone_nametwo_a"));
			logger.info("Selected Level 2");
		}
		else if("Level 3".equalsIgnoreCase(level)){
			selUtils.selectItem(selUtils.getObject("level1_id"), testDataOR.get("zone_nameone_a"));
			selUtils.selectItem(selUtils.getObject("level2_id"), testDataOR.get("zone_nametwo_a"));
			selUtils.selectItem(selUtils.getObject("level3_id"), testDataOR.get("zone_namethree_a"));
			logger.info("Selected Level 3");
		}
	}
	/**
	 * Method for clicking on the up and down arrows of the module rights
	 * @param elt (down arrow locator)
	 * @param elt1(up arrow locator)
	 * @author Nagaveni.Guttula
	 */
	public void expandmoduleRights(WebElement elt,WebElement elt1){
		if(elt.isDisplayed()){
			selUtils.clickOnWebElement(elt);
		}
		else if(elt1.isDisplayed()){
			logger.info("Already module rights are opened");
		}

	}
	/**
	 * This method for selecting  default language as English or en
	 * @author Nagaveni.Guttula
	 */
	public void selectDefLan(){
		String[] actItems=getListItems(selUtils.getObject("language_id"));
		List<String> listItems = new ArrayList<String>(Arrays.asList(actItems));
		if(listItems.contains(ENGLISH)){
			selUtils.selectItem(selUtils.getObject("language_id"), ENGLISH);
		}
		else if(listItems.contains(ENLANG)){
			selUtils.selectItem(selUtils.getObject("language_id"), ENLANG);
		}
	}

	/**
	 * Method to Add a Multi User
	 */
	public void addMultiUser(String yamlval[],String yamlval1[],String termrole){
		final String [] userArr={testDataOR.get("multi_user_login"),
				testDataOR.get("multi_first_name"),testDataOR.get("multi_last_name")},
				locatorArr={"profname_id","usrfrstname_id","usrlstname_id"};
		waitMethods.waitForWebElementPresent(selUtils.getObject("profname_id"));
		for(int count=0;count<userArr.length;count++ ){
			selUtils.populateInputBox(locatorArr[count], userArr[count]);
		}
		selectDefLan();
		selUtils.selectItem(selUtils.getObject("existingprof_id"), testDataOR.get("multi_user_profile"));
		if(SelUtils.getSelectedItem(selUtils.getObject("existingprof_id")).equals(NONE)){
			selChkOrRadiobttn(testDataOR.get("internal_user"),"intnlusr_chbx_id");
			//selChkOrRadiobttn(testDataOR.get("web_service_account"),"webservice_id");
			selUtils.slctChkBoxOrRadio(selUtils.getObject("readrite_rdbttn_id"));
			selChkOrRadiobttns(yamlval,usrmngRightsChbx);
			if(selUtils.getObject("intprovtsk_chkbx_id").isEnabled()){
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", 
						selUtils.getObject("intprovtsk_chkbx_id"));
			}
			selUtils.selectDropItem(selUtils.getObject("ep_entities_id"), testDataOR.get("entity"));
			if(!SelUtils.getSelectedItem(selUtils.getObject("ep_entities_id")).equals(ALL))
			{
				selectMultipleVals("ep_cust_id",testDataOR.get("customers"));
			}
			selChkOrRadiobttn(testDataOR.get("transaction_management"),"transmgmtmod_id");
			selUtils.slctChkBoxOrRadio(selUtils.getObject("ftp_rdwrt_radio_id"));
			//if(!"None".equalsIgnoreCase(testDataOR.get("terminal_role"))){
			selcRoles("terminalrole_id","terminalroles_radiobttns_xpath",termrole);
			//}
			/*if(!selUtils.getObject("terminalview_id").isSelected())
			{
				for(WebElement ele:selUtils.getObjects("tamsubmod_xpath"))
				{
					if(ele.isSelected())
					{
						selChkOrRadiobttn(testDataOR.get("access_to_sensitive_parameters"),"sensitivedata_id");
						break;
					}
				}
			}*/
			if("Terminal Viewer".equalsIgnoreCase(termrole)){
				logger.info("Terminal role is set to "+termrole);
			}
			else{
				selChkOrRadiobttn(testDataOR.get("access_to_sensitive_parameters"),"sensitivedata_id");
			}	
			selTmsAsstNEvntsRole(yamlval1,profChkBxs);
			selUtils.selectItem(selUtils.getObject("ingestaterole_id"), testDataOR.get("ingestate_role"));
		}
		selUtils.clickOnWebElement(selUtils.getCommonObject("save_link"));
	}

	/**
	 * Method to select tms rights,asset tracking role and events chkbxs
	 * @param yamlval1,@param chk
	 */
	public void selTmsAsstNEvntsRole(String yamlval1[],String chk[]){

		if(!"None".equalsIgnoreCase(testDataOR.get("asset_tracking_role"))){
			selcRoles("assttrkingrole_id","astrking_roles_radiobttns_xpath",testDataOR.get("asset_tracking_role"));
			selUtils.slctChkBoxOrRadio(selUtils.getObject("eventss_id"));
			selChkOrRadiobttns(yamlval1,chk);
		}
	}


	//Predefined methods for LDAP UM Provisioning Testcases 	

	/**
	 * Method for ldap login
	 * @param userLogin
	 */
	public void ldap(String userLogin) {

		try{
			changePassword(userLogin);
		}
		catch (Throwable t) {
			handleException(t);
		}
	}

	/**
	 * Method for getting the ldap credentials
	 * @return
	 * @throws NamingException
	 */
	public static DirContext getCtx() throws NamingException{
		Hashtable<String,String> env = new Hashtable<String,String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, testDataOR.get("ldap"));
		env.put(Context.SECURITY_AUTHENTICATION,"simple");
		env.put(Context.SECURITY_PRINCIPAL,"cn=manager,dc=managedservices,dc=ingenico,dc=com"); 
		env.put(Context.SECURITY_CREDENTIALS,testDataOR.get("ldap_password")); 		
		return new InitialDirContext(env);
	}

	/**
	 * Change pswd method through ldap
	 * @param userName
	 * @throws NamingException
	 */
	public static void changePassword(String userName) throws NamingException{
		DirContext ctx = getCtx();

		ModificationItem modNewPassword = new ModificationItem(
				DirContext.REPLACE_ATTRIBUTE,
				new BasicAttribute("userPassword", testDataOR.get("user_password")));

		// Now change the user password with the password attribute
		ModificationItem[] modItemList = new ModificationItem[] {modNewPassword };
		ctx.modifyAttributes(new CompositeName().add("uid=" + userName + ",ou=users,dc=managedservices,dc=ingenico,dc=com"),
				modItemList);
		ctx.close();


	}

	/**
	 * Selecting checkbox
	 * @param yamlval
	 * @param locator
	 */
	public void selChkOrRadiobttn(String yamlval,String locator){
		if("true".equalsIgnoreCase(yamlval)){
			selUtils.slctChkBoxOrRadio(selUtils.getObject(locator));
		}

	}

	/**
	 * select chkboxval
	 * @param chbxloc
	 * @param bttnsloc
	 * @param yamlval
	 */
	public void selcRoles(String chbxloc,String bttnsloc,String yamlval){
		try{
			selUtils.slctChkBoxOrRadio(selUtils.getObject(chbxloc));
			xpath=getPath(bttnsloc).replace("NAME", yamlval);
			selUtils.slctChkBoxOrRadio(selUtils.getObjectDirect(By.xpath(xpath)));
		}
		catch(NullPointerException e)
		{
			Assert.fail("Element is not available to select roles");
		}
	}

	/**
	 * Method to verify profile in use exist drop down for smr11 and smr12
	 * @param profile
	 */
	public void vUseExstsProLists(String profile,String objloc){
		//String profName;
		waitMethods.waitForWebElementPresent(selUtils.getCommonObject("usermangement_link"));
		selUtils.clickOnWebElement(selUtils.getCommonObject("usermangement_link"));
		
		waitNSec(3);
		
		selUtils.switchToFrame();
		driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
		//selUtils.clickOnWebElement(selUtils.getObject("profiletab_id"));
		//selUtils.clickOnWebElement(selUtils.getObject("activateduser_id"));
		//waitMethods.waitForWebElementPresent(selUtils.getObject(objloc));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObject(objloc));
		//selUtils.clickOnWebElement(selUtils.getObject(objloc));
		driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
		logger.info("Clicked on the add user button");
		//delay added to handle headless mode of execution
		waitNSec(5);
		//selUtils.clickOnWebElement(selUtils.getObject("profname_id"));
		//selectDefLan();
		//waitMethods.waitForWebElementPresent(selUtils.getObject("existingprof_id"));
		appList= getListItemsAsString(selUtils.getObjects("useexists_profilelist_xpath"));
		Assert.assertTrue(appList.contains(profile), profile+" is not displayed in the userexisting profile dropdown list");
		logger.info("Verified the "+profile+" is displayed in the User Existing Profile drop down");
		
		
		/*waitMethods.waitForWebElementPresent(selUtils.getObject("personaldata_id"));
		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
		((JavascriptExecutor)driver).executeScript(JSCLICK, selUtils.getObject("existingprof_id"));
		jsExecutor.executeScript("execute(0),selUtils.getObject("existingprof_id")");*/
		//		selUtils.scrollToView(selUtils.getObject("existingprof_id"));

		//waitMethods.waitForWebElementPresent(selUtils.getObject("existingprof_id"));
		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("existingProfileId")));
		//		waitMethods.waitForWebElementPresent(selUtils.getObject("existingprof_id"));
		//wait.until(ExpectedConditions.visibilityOf(selUtils.getObject("existingprof_id")));

		//		appList= getListItemsAsString(selUtils.getObjects("useexists_profilelist_xpath"));
		//		Assert.assertTrue(appList.contains(profile), profile+" is not displayed in the userexisting profile dropdown list");

		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(TestBase.getPath("existingprof_id"))));
		//selectDefLan();
		/*selUtils.clickOnWebElement(selUtils.getObject("profname_id"));
		waitMethods.waitForWebElementPresent(selUtils.getObject("existingprof_id"));
		waitNSec(3);
		selUtils.selectItem(selUtils.getObject("existingprof_id"),profile);
		profName=SelUtils.getSelectedItem(selUtils.getObject("existingprof_id"));

		if(okConfirm)
		{
			// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(TestBase.getPath("oklocator_xpath"))));
			selUtils.clickOnWebElement(selUtils.getObject("oklocator_xpath"));
		}
		else{
			// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(TestBase.getPath("confirmlocator_xpath"))));
			selUtils.clickOnWebElement(selUtils.getObject("confirmlocator_xpath"));
		}
		Assert.assertEquals(profile, profName.trim(),"Profile name is not matching");

		logger.info("Verified the "+profile+" is displayed in the User Existing Profile drop down");*/
		//driver.switchTo().defaultContent();	
	}

	/**
	 * Database validation ,Common method to smr11 and smr12
	 * @param profile,@param sqlstatement, @throws SQLException
	 */
	public void vProfInDataBase(String profile,String sqlstatement) throws SQLException{
		resSet = dbMethods.getDataBaseVal(testDataOR.get("databaseUM"),sqlstatement,CommonConstants.ONEMIN);
		Assert.assertTrue(resSet.getString("is_user_profile").equalsIgnoreCase("t") && resSet.getString("userid").contains(profile), profile+ "does not exist in the database");
		logger.info(profile+ " exists in the database");
	}

	/**
	 * Method to check collapse of plus button and click
	 * @param objloc1
	 * @param objloc2
	 */
	public void clkOnModRtsPlsBttn(String objloc1,String objloc2){
		if(selUtils.getObject(objloc1).getAttribute("class").contains("closeoff"))
		{
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", selUtils.getObject(objloc2));
			//selUtils.clickOnWebElement(selUtils.getObject(objloc2));
		}
	}
	
	
	/*
	* Access eportal with monouser
	* Check dashboard,gprs tabs should be display
	* Check usermanagement should not be dispaly
	*/
	public void monoUsrLoginTbs(String monouser,String fname,String lname,String[] tabsloc,String terminalrole){
		
		login("URLEportal","eportalusr_name","eportalpswd_name",monouser,"user_password","newversnloginbttn_xpath",fname,lname);
		logger.info("Access eportal with mono_user_login");

		//'Dashboard', 'GPRS' tabs should be displayed
		selUtils.verifyCommonElementsDisplayed(tabsloc);
		logger.info("Verified Dashboard, GPRS tabs are displayed");

		//TMS tab should not be displayed if terminal_role and 
		//asst_trking_role are not set to 'None' at the same time
		//TMS tab should be displayed if terminal role and assert tracking role are not set to None
		if(!"None".equalsIgnoreCase(terminalrole) || !"None".equalsIgnoreCase(testDataOR.get("asset_tracking_role")))
		{
			Assert.assertTrue(selUtils.getCommonObject("tms_tab_xpath").isDisplayed(),"TMS tab is not displayed");
			logger.info("TMS tab is displayed as terminal role or assert tracking role are not set to None");
		}
		
		//'User management' tab should not be displayed
		Assert.assertFalse(selUtils.isElementPresentCommon("usermgmt_tab_xpath"),"Failed due to User management tab is present under main tab");
		logger.info("Verified User management tab is not displayed");
	}
	
}