package com.ingenico.base;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/base/SelUtils.java $
$Id: SelUtils.java 18288 2016-04-28 05:35:43Z rkahreddyga $
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

/**
 * SetUtils class contains common methods related to object, common object, element and web element.
 *
 */
public class SelUtils {
	public boolean exists;
	public WaitMethods waitMethods = new WaitMethods();
	public WebDriver driver;
	private Logger logger = TestBase.logger;
	private Properties objR = TestBase.objR, commonOR = TestBase.commonOR;
	private WebDriverWait wait = TestBase.wait;
	public JavascriptExecutor javScrpt;
	public String prop;
	private String xpath = "",value;
	private int iter, count;
	public List<WebElement> options;

	/**
	 *  Class Constructor for getting the Driver object from TestBase Class
	 */
	public SelUtils() {
		this.driver = TestBase.getDriver();
	}

	/**
	 * It's for common method to locate common object
	 * @param commobject
	 * @return elt
	 */
	public WebElement getCommonObject(String commobject) {
		WebElement elt;	
		elt=getWebElement(commonOR,commobject);
		return elt;
	}

	/**
	 * It's for common method for object used in module
	 * @param object
	 * @return elt
	 */
	public WebElement getObject(String object) {
		WebElement elt;
		elt=getWebElement(objR,object);
		return elt;
	}

	/**
	 * It's for common method to get list of objects
	 * @param object
	 * @return elts
	 */
	public List<WebElement> getObjects(String object) {
		List<WebElement> elts;
		elts=getWebElements(objR,object);
		return elts;
	}

	/**
	 * It's for common method to get list of common objects
	 * @param commonObject
	 * @return elts
	 */
	public List<WebElement> getCommonObjects(String commonObject) {
		List<WebElement> elts;
		elts=getWebElements(commonOR,commonObject);
		return elts;
	}

	/**
	 * Returns the web element
	 * @param locator
	 * @return elt
	 */
	public WebElement getObjectDirect(final By locator) {
		int counter =0;
		WebElement elt=null;
		try
		{
			for(counter=0;counter<4;counter++)
			{
				try{
					elt = driver.findElement(locator);
					break;
				}
				catch (Exception e1){
					TestBase.waitNSec(1);
				}
			}
			if(counter==4){
				//Assert.assertFalse("".equals(elt));gives pmd warining,returns element as null,dosent go to catch block
				Assert.assertFalse(elt.equals(""));
			}
		}
		
		catch (Exception e)
		{					
			Assert.fail("Cannot find object with key -- "+ locator);
		}	
		
		return elt;

	}

	/**
	 * Returns more than one web elements
	 * @param locator
	 * @return elts
	 */
	public List<WebElement> getObjectsDirect(final By locator) {
		int counter =0;
		List<WebElement> elts=null;
		try
		{
			for(counter=0;counter<4;counter++)
			{
				try{
					elts = driver.findElements(locator);
					break;
				}
				catch (Exception e1)
				{				
					TestBase.waitNSec(1);
				}
			}
			if(counter==4)
			{
				Assert.assertFalse("".equals(elts));
			}

		} catch (Exception e)
		{					
			Assert.fail("Cannot find objects with key -- " + locator);
		}//end of for							
		return elts;
	}

	/**
	 * It's common method to check page source objects which are not displayed
	 * @param commobject
	 * @return
	 */
	public void verifyElementNotPresent(String str){
		try{
			Assert.assertFalse(driver.getPageSource().contains(str), "Expected '"+str+"' is displayed");
		}catch (Exception e)
		{					
			Assert.fail("Expected '"+str+"' is displayed");
		}
	}

	/**
	 * Method for clicking on any webElement
	 * @param webelement
	 */
	public void clickOnWebElement(WebElement webelement)
	{
		waitMethods.waitForWebElementPresent(webelement);
		//wait.until(ExpectedConditions.visibilityOf(webelement));
		//webelement.click();
		//((JavascriptExecutor) driver).executeScript("arguments[0].click();", webelement);
		Actions action = new Actions(driver);
		action.moveToElement(webelement).click().build().perform();
	}

	/**
	 * Clicking on left pane element
	 * @param webelement
	 * @param attrName
	 * @param attrVal
	 * @author Hariprasad.KS
	 */
	public void clickOnNavPaneItems(WebElement webelement,String attrName,String attrVal)
	{
		String attrValue=webelement.getAttribute(attrName);
		if(attrValue.endsWith(attrVal))
		{
			clickOnWebElement(webelement);
		}
		else{
			logger.info("Already leftPane items are opened");
		}
	}

	/**
	 * It's common method to enter data in input boxes
	 * @return
	 */
	public void populateInputBox(String object, String value){
		try{
			if(!getObject(object).getAttribute("value").isEmpty()){
				getObject(object).clear();
			}
			getObject(object).sendKeys(value);
		}
		catch(Exception e)
		{
			Assert.fail("Expected value is not set to the input box");
		}
	}	

	/**
	 * Select any of the option from the list
	 * @param element
	 * @param value	
	 */
	public void selectItem(WebElement element,String value){
		waitMethods.waitForWebElementPresent(element);
		Select listbox=new Select(element);
		listbox.selectByVisibleText(value);		
		TestBase.waitNSec(2);
	}

	/**
	 * de selects any of the option from the list
	 * @param element
	 * @param value	
	 */
	public void deselectItem(WebElement element,String value){
		Select listbox=new Select(element);
		listbox.deselectByVisibleText(value);
		TestBase.waitNSec(2);
	}

	/**
	 * Method for selecting the check box or radio items
	 * @param element
	 */
	public void slctChkBoxOrRadio(WebElement element)
	{
		try{
			if(element.isSelected())
			{
				logger.info("Checkbox/Radio is already selected by default");			
			}
			else
			{
				//Please discuss before using jacascript click here with team.
				//((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
				waitMethods.waitForWebElementPresent(element);
				element.click();
				logger.info("Clicked on checkbox/radio");
			}
		}catch(Exception e){
			Assert.fail("Failed while selecting a checkbox");
		}
	}

	/**
	 * Method for not selecting the check box or radio items
	 * @param element
	 */
	public void unSlctChkBoxOrRadio(WebElement element)
	{
		if(element.isSelected()){
			element.click();
		}
		else{
			logger.info("Checkbox/Radio is not selected by default");
		}
	}

	/**
	 * validate checkbox or radio and then select
	 * @param element
	 */
	public void vNclkChkOrRadio(WebElement element)
	{
		if(element.isSelected())
		{
			Assert.fail("Checkbox/Radio is already selected");			
		}
		else
		{
			element.click();
		}
	}

	/**
	 * It returns the currently selected item 
	 * @param element
	 * @return
	 */
	public static String getSelectedItem(WebElement element){
		Select listbox=new Select(element);
		return listbox.getFirstSelectedOption().getText();
	}

	/**
	 * Select drop down option after de selecting all the options
	 * @param element
	 * @param value
	 */
	public void selectDropItem(WebElement element,String value)
	{
		try{
			Select listbox=new Select(element);
			listbox.deselectAll();
			listbox.selectByVisibleText(value);
			TestBase.waitNSec(2);
		}
		catch(Throwable t) {
			Assert.fail("Option could not be selected in drop down");
		}
	}

	/**
	 * Method for selecting value  from drop down
	 * @param dropDownList
	 * @param value
	 */
	public void selectValueInDropDown(List<WebElement> dropDownList, String value) {
		try{
			for(WebElement element : dropDownList){
				if(element.getText().contains(value)){
					element.click();					
					break;
				}
			}
		} catch (Exception e){
			e.printStackTrace();
			Assert.fail("Cannot find element list "+ dropDownList +" with Value -- " + value);
		}
	}

	/**
	 * Verify multiple selection.
	 * @param comboBoxElement
	 * @param selectedItemsList
	 */
	public void selectMultiple(WebElement comboBoxElement, String[] selectedItemsList){
		int itemCount, iter;
		Actions action=new Actions(driver);
		Select select = new Select(comboBoxElement);
		select.deselectAll();
		options = select.getOptions();
		for(itemCount = 0; itemCount <selectedItemsList.length; itemCount++){
			for(iter = 0; iter < options.size(); iter++){
				value = options.get(iter).getText();
				if(value.equalsIgnoreCase(selectedItemsList[itemCount])){
					action.click(options.get(iter)).keyUp(Keys.CONTROL).perform();
					break;
				}
			}
		}
	}

	/**
	 * Select using java script.
	 * @param locID
	 * @param value
	 */
	public void multiSelect(String locID ,String value){
		JavascriptExecutor javScrpt = (JavascriptExecutor) driver;	
		javScrpt.executeScript("document.getElementById('"+locID+"').value='"+value+"'");
		TestBase.waitNSec(1);
	}

	/**
	 * verify the object is multiselect or not
	 * @param element
	 * @return
	 */
	public boolean vObjectIsMultiple(WebElement element)
	{
		Select multiSel = null;
		multiSel=new Select(element);
		return multiSel.isMultiple();
	}

	/**
	 * Verify Element is not selected	 
	 * @param element
	 */
	public void verifyElementNotSelected(WebElement element) 
	{
		Assert.assertFalse(element.isSelected(), "Expected element is selected");
		logger.info("Verified element is not selected.");
	}

	/**
	 * Verify Element is selected
	 * @param element
	 */	
	public void verifyElementSelected(WebElement element)
	{
		Assert.assertTrue(element.isSelected(), "Expected element is not selected");
		logger.info("Verified element is selected.");
	}

	/**
	 * Verify Element is enabled
	 * @param element
	 */	
	public void verifyEltEnabled(WebElement element)
	{		
		Assert.assertTrue(element.isEnabled(), "Expected element is not enabled");
		logger.info("Verified element is enabled.");
	}

	/**
	 * Verify Element is disabled
	 * @param element
	 */	
	public void verifyEltDisabled(WebElement element)
	{
		Assert.assertFalse(element.isEnabled(), "Expected element is enabled");
		logger.info("Verified element is not enabled.");
	}

	/**
	 * Verify Element is displayed
	 * @param webelement
	 * @param eleName
	 */
	public void verifyElementDisp(WebElement webelement, String eleName)
	{
		Assert.assertTrue(webelement.isDisplayed(), "Expected element '"+eleName+"'is not displayed");
		logger.info("Verified, '"+eleName+"' element is displayed.");
	}

	/**
	 * Verify Common Element is not displayed with single parameter
	 * @param element
	 */	
	public void verifyElementNotDisp(WebElement element)
	{
		Assert.assertFalse(element.isDisplayed(), "Expected element is displayed");
		logger.info("Verified expected element is not displayed");

	}

	/**
	 * Method for the web element was displayed 	
	 * @param locator
	 * @param valToreplace
	 * @param replacableVal 
	 * @author Nagaveni.Guttula
	 */
	public void verifyObjDirectDisp(String locator,String valToreplace,String replacableVal){
		xpath="";
		if(!(replacableVal.isEmpty())){
			xpath = TestBase.getPath(locator).replace(valToreplace,replacableVal);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			Assert.assertTrue(getObjectDirect(By.xpath(xpath)).isDisplayed(), " element is not displayed");
		}
	}

	/**
	 * Method to verify n number of elements displayed
	 * @param expectedArr
	 */
	public void verifyElementsDisplayed(String expectedArr[]){
		for (int iter=0;iter<expectedArr.length;iter++){
			Assert.assertTrue(getObject(expectedArr[iter]).isDisplayed(),expectedArr[iter]+" is not displayed");
			logger.info("Expected element is displayed");
		}
	}

	/**
	 * Method to verify n number of elements displayed
	 * @param expectedArr
	 */
	public void verifyCommonElementsDisplayed(String expectedArr[]){
		for (int iter=0;iter<expectedArr.length;iter++){
			Assert.assertTrue(getCommonObject(expectedArr[iter]).isDisplayed(),expectedArr[iter]+" is not displayed");
			logger.info("Expected element is displayed");
		}
	}

	/**
	 * Method for verifying the list of elements are dispalyed
	 * @param locator
	 * @param valToreplace
	 * @param replacableVal
	 * @author Nagaveni.Guttula
	 */
	public void verifyObjsDirectDisp(String locator,String[] arrItems,String replacableVal){
		for(iter=0;iter<arrItems.length;iter++){
			xpath="";
			xpath = TestBase.getCommonPath(locator).replace(replacableVal,arrItems[iter]);
			Assert.assertTrue(getObjectDirect(By.xpath(xpath)).isDisplayed(), " element is not displayed");
			logger.info("Verified the "+ arrItems[iter]+" is displayed");
		}
	}

	/**
	 * Verify Element Text is not present
	 * @param element
	 * @param text
	 */	
	public void verifyTextNotPresent(WebElement element, String text)
	{
		try
		{
			Assert.assertFalse(element.getText().equalsIgnoreCase(text),"Actual text '"+element.getText()+"'and Expected text '"+text+"' are present");
			logger.info("Verified the '"+text+"' is not present");
		}catch (Exception e)
		{					
			Assert.fail("Expected text '"+text+"' is present");
		}
	}

	/**
	 * Verify Element Text is present
	 * @param webelement
	 * @param text 
	 */	
	public void verifyTextContains(WebElement webelement, String text)
	{
		try
		{	
			Assert.assertTrue(webelement.getText().trim().contains(text.trim()),"Expected text "+webelement.getText().trim()+"and Actual text'"+text+"' are mismatching");
			logger.info("Verified the '"+text+"' is present");
		}catch (Exception e)
		{					
			Assert.fail("Verified the '"+text+"' is not present");
		}
	}

	/**
	 * Verify Element Text is present
	 * @param webelement
	 * @param text 
	 */	
	public void verifyTextStartsWith(WebElement webelement, String text)
	{
		try
		{	
			Assert.assertTrue(webelement.getText().trim().startsWith(text.trim()),"Expected text starts with "+webelement.getText().trim()+"and Actual text starts with'"+text+"' are mismatching");
			logger.info("Verified the text of the element starts with '"+text+"' is present");
		}catch (Exception e)
		{					
			Assert.fail("Verified the text of the element starts with '"+text+"' is not present");
		}
	}

	/**
	 * Verify Element Text is present
	 * @param webelement
	 * @param text
	 */	
	public void verifyTextEqualsWith(WebElement webelement, String text)
	{
		try
		{				
			Assert.assertTrue(webelement.getText().trim().equalsIgnoreCase(text.trim()),"Expected text '"+webelement.getText().trim()+"' and Actual text '"+text+"' are not matching");
			logger.info("Verified the text equals with '"+text+"' is present");
		}catch (Exception e)
		{					
			Assert.fail("Verified the text equals with'"+text+"' is not present");
		}
	}

	/**
	 * Verify Element Text is not present
	 * @param webelement
	 * @param text
	 */	
	public void verifyTextNotContains(WebElement webelement, String text)
	{
		try
		{
			Assert.assertFalse(webelement.getText().contains(text),"Expected text '"+webelement.getText()+"'and Actual text '"+text+"' are matching");
			logger.info("Verified the '"+text+"' is not present");
		}catch (Exception e)
		{					
			Assert.fail("Expected text '"+text+"' is present");
		}

	}	

	/**
	 * verify button value using attribute parameter
	 * @param element
	 * @param attName
	 * @param text
	 */
	public void verifyValueWithAttribute(WebElement webelement,String attName,String text)
	{
		String webelementvalue=webelement.getAttribute(attName);
		Assert.assertTrue(webelementvalue.contains(text),"Actual value '"+webelementvalue+"'and Expected value '"+text+"' are not matching");
		logger.info("Verified the '"+text+"' element is present");
	}

	/**
	 * Verify the sting present in the bread crumb with delay (to be removed later)
	 * @param text
	 */
	public void verifyBreadCrumb(String text) 
	{
		//waitMethods.waitUntilVisibilityOfElements("breadcrum_css", TestBase.getCommonPath("bread_crumb_id"));
		waitMethods.waitForWebElementPresent(getCommonObject("bread_crum_id"));
		Assert.assertTrue(getCommonObject("bread_crum_id").getText().contains(text),"Actual text '"+getCommonObject("bread_crum_id").getText()+"'Contains Expected text is '"+text+"' are not matching");
	}

	/**
	 * Method for verifying the listItemsPresence
	 * @param actItems
	 * @param expItems
	 * @author Nagaveni.Guttula
	 */
	public void vListItemspresence(String[] actItems,String[] expItems){
		List<String> listItems = new ArrayList<String>(Arrays.asList(actItems));
		for(String eachItem:expItems)
		{
			//Assert.assertTrue(listItems.contains(eachItem),"  listItem '"+eachItem+"' is not present");
			Assert.assertTrue(listItems.contains(eachItem),"Expected item '"+eachItem+"' Actual item '"+actItems+"' are not present");
		}
		logger.info("Verified the presence of all listItems");
	}
	/**
	 * validate customized locator displays or not
	 * @param locator
	 * @param oldChar
	 * @param newChar
	 * @author Hariprasad.KS
	 */
	public void vDirectEleDisplayed(String locator,String oldChar,String newChar)
	{
		String locPath=TestBase.getPath(locator).replace(oldChar, newChar);
		Assert.assertTrue(waitMethods.waitForWebElementPresent(getObjectDirect(By.xpath(locPath))),newChar+" is not displayed");
	}
	
	/**
	 * Refresh the browser body 	  
	 */
	public void refresh() {
		driver.navigate().refresh();

	}

	/**
	 * Navigate Back
	 */
	public void navigateBack() {
		try{
			driver.navigate().back();
		}
		catch(Exception e)
		{
			Assert.fail("Error with navigating back to the page ");
		}
	}	

	/**
	 * Switch to frame.
	 */
	public void switchToFrame(){
		driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
	}

	/**
	 * Method for handling the alert boxes
	 * @author Nagaveni.Guttula
	 */
	public void acceptAlert() {
		try {
			wait.until(ExpectedConditions.alertIsPresent());
			Alert alert = driver.switchTo().alert();
			alert.accept();

		} catch (Exception e) {
			logger.info("Alert is not present");
		}
	}

	/**
	 * Method for Scrolling Down	 
	 */
	public void scrollDown() {
		driver.switchTo().defaultContent();
		javScrpt = (JavascriptExecutor) driver;
		javScrpt.executeScript("scroll('0','1000')");
		driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
	}

	/**
	 * Method for Scrolling To WebElement	 
	 */
	public void scrollToView(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}
	/**
	 * scroll to left side
	 */
	public void scrollLeft() {
		javScrpt = (JavascriptExecutor) driver;
		javScrpt.executeScript("window.scrollBy(-95,0)", "");
	}


	/**
	 * Select maximum size of the table
	 * @param element
	 * @author Hariprasad.KS
	 */
	public void selectMaxSizeinTable(WebElement element ){
		String[] shwMaxResArr;
		wait.until(ExpectedConditions.visibilityOf(element));
		String shwMaxRes = element.getText();
		shwMaxResArr = shwMaxRes.split("\n");
		if(shwMaxResArr.length > 1){
			selectItem(element, shwMaxResArr[shwMaxResArr.length-1]);
		}

	}

	/**
	 * getIndexForHeader fetches index of specified header
	 * @param locator
	 * @param header
	 * @return count
	 */
	public int getIndexForColHeader(String locator, String header){
		count=-1;
		List<WebElement> headerCols = getCommonObjects(locator);
		for(int iter=0;iter<headerCols.size();iter++){
			if(headerCols.get(iter).getText().trim().equalsIgnoreCase(header.trim())){
				count=iter;
				break;
			}
		}
		if(count==-1){
			Assert.fail("Expected column name is not there");
		}
		return count;

	}
	
	/**
	 * getIndexForHeader fetches index of specified header
	 * @param locator
	 * @param header
	 * @return count
	 */
	public int getIndexForRow(By loc, String header){
		count=-1;
		List<WebElement> rowheaders=getObjectsDirect(loc);
		for(int iter=0;iter<rowheaders.size();iter++){
			if(rowheaders.get(iter).getText().trim().equalsIgnoreCase(header.trim())){
				count=iter;
				break;
			}
		}
		if(count==-1){
			Assert.fail("Expected row name is not there");
		}
		return count;

	}

	/**
	 * Method for checking common element presence
	 * @param object
	 * @return
	 */
	public boolean isElementPresentCommon(String  commonObject) {
		boolean xval=false;
		String locators;
		//Locators  locators = Locators.valueOf(commonObject.substring(commonObject.lastIndexOf('_')+1));
		locators=commonObject.substring(commonObject.lastIndexOf('_')+1);
		try{
			prop=commonOR.getProperty(commonObject);
			switch (locators) {

			case "link":			
				driver.findElement(By.linkText(prop));
				xval=true;
				break;
			case "xpath":					
				driver.findElement(By.xpath(prop));
				xval=true;
				break;
			case "css":
				driver.findElement(By.cssSelector(prop));
				xval=true;
				break;
			case "id":					
				driver.findElement(By.id(prop));
				xval=true;
				break;
			case "name":
				driver.findElement(By.name(prop));
				xval=true;
				break;
			case "classname":	
				driver.findElement(By.className(prop));	
				xval=true;
				break;
			default:
				Assert.fail("Object locator format is not proper");
				break;
			}	
			return xval;
		}
		catch (NoSuchElementException e) {
			return false;
		}
	}

	/**
	 * isElementPresent checks if the element is present in the application
	 * Returns the 
	 * @param object
	 * @return xval
	 */
	public boolean isElementPresentxpath(String  object) {
		boolean xval=false;
		try{
			driver.findElement(By.xpath(object));
			xval=true;
		}
		catch (NoSuchElementException e) {
			return false;
		}
		return xval;
	}

	/**
	 * get Object property
	 * @param objPop
	 * @param object
	 * @return
	 */
	public WebElement getWebElement(Properties objPop,String object)
	{
		WebElement elt = null;
		String propValue,locators;
		int counter =0;
		try
		{
			//Locators  locators = Locators.valueOf(object.substring(object.lastIndexOf('_')+1));
			locators=object.substring(object.lastIndexOf('_')+1);
			forloop:	for(counter=0;counter<4;counter++)
			{
				try {
					propValue = objPop.getProperty(object);
					switch (locators) {
					case "link":
						elt = driver.findElement(By.linkText(propValue));
						break forloop;					
					case "xpath":
						elt = driver.findElement(By.xpath(propValue));
						break forloop;
					case "css":
						elt = driver.findElement(By.cssSelector(propValue));
						break forloop;					
					case "id":
						elt = driver.findElement(By.id(propValue));
						break forloop;
					case "name":
						elt = driver.findElement(By.name(propValue));
						break forloop;
					case "classname":
						elt = driver.findElement(By.className(propValue));	
						break forloop;
					default:
						Assert.fail("Object locator format is not proper");	
						break;
					}
				}
				catch (Exception e1)
				{				
					TestBase.waitNSec(1);
				}
			}
			if(counter==4)
			{
				Assert.assertFalse(elt.equals(""));
			}
		} catch (Exception e)
		{					
			Assert.fail("Cannot find object with key -- "+ object);

		}//end of for							
		return elt;
	}
	
	
	/**
	 * The waitForTxttPresent function will wait for the element text for a 
	 * default duration of customized seconds To increase or decrease this time
	 * change the value of the integer 'timeoutSec' in "Common.java"	 
	 * @param Locator
	 **/
	public boolean waitForTxtPresent(String locator, String text) {  
		exists = false;
		int cnti=0;
		for(cnti=0; cnti<20; cnti++)  
		{ 
			try {
				if(getObject(locator).getText().contains(text)) { 
					exists = true;
					break;
				}else {
					TestBase.waitNSec(1);
				}
			}catch(Exception e){
				TestBase.waitNSec(1);
			}
		}			
		/*if (cnti==20)
				{
				    logger.info("Associated text is not found with the web element");
					Assert.assertTrue(getObject(locator).getText().contains(text));
				}	*/
		//}
		return exists;
	}

	/**
	 * get Objects property
	 * @param objPop
	 * @param object
	 * @return
	 */
	public List<WebElement> getWebElements(Properties objPop,String object)
	{
		List<WebElement> elts=null;
		String propValue,locators;
		int counter =0;
		try {
			//Locators  locators = Locators.valueOf(object.substring(object.lastIndexOf('_')+1));
			locators=object.substring(object.lastIndexOf('_')+1);
			forloop:	for(counter=0;counter<4;counter++)
			{

				try{
					propValue = objPop.getProperty(object);
					switch (locators) {
					case "link":					
						elts = driver.findElements(By.linkText(propValue));
						break forloop;
					case "xpath":				
						elts = driver.findElements(By.xpath(propValue));
						break forloop;
					case "css":	
						elts = driver.findElements(By.cssSelector(propValue));
						break forloop;
					case "id":						
						elts = driver.findElements(By.id(propValue));
						break forloop;
					case "name":	
						elts = driver.findElements(By.name(propValue));	
						break forloop;
					case "classname":	
						elts = driver.findElements(By.className(propValue));	
						break forloop;
					default:
						Assert.fail("Object locator format is not proper");
						break;
					}
				}				
				catch (Exception e1)
				{				
					TestBase.waitNSec(1);
				}
			}
			if(counter==4)
			{
				Assert.assertFalse(elts.equals(""));
			}
		} catch (Exception e)
		{					
			Assert.fail("Cannot find objects with key -- "+ object);
		}//end of for							
		return elts;
	}
	
	/**
	 * Method for clicking on javascript
	 * @param webelement
	 */
	public void jScriptClick(WebElement webelement)
	{
		waitMethods.waitForWebElementPresent(webelement);
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", webelement);
	}
		
}
