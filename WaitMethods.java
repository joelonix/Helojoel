package com.ingenico.base;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/base/WaitMethods.java $
$Id: WaitMethods.java 16351 2015-12-23 05:31:04Z jsamuel $
 */

import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

/**
 * WaitMethods class contains all methods related to wait
 */
public class WaitMethods {
	
	//Declare WebDriver variable
	public WebDriver driver;
	//Create instance for Properties
	private Properties objR = TestBase.objR;
	//Create instance for WebDriverWait
	private WebDriverWait wait = TestBase.wait;
	
	//Declare loop variable
	private int iter;
	//Declare boolean variable
	public boolean exists, status;
	//Declare properties variable
	public static String prop;
	//Decalre Set String variable
	public Set<String> windowIds;
	
	/**
	 *  Class Constructor for getting the Driver object from TestBase Class
	 */
	public WaitMethods() {
		this.driver = TestBase.getDriver();
	}
	
	/** The waitForTitlePresent function will wait for the element for a
	 * default duration of customized seconds To increase or decrease this time
	 * change the value of the integer 'timeoutSec' in "Common.java"  * 
	 * @param title	 
	 */
	public  boolean waitForTitlePresent(String title) { 
		exists = false;
		for(iter=0;iter<30;iter++)  {   
			try{
				if(driver.getTitle().contains(title))   {                                       
					exists = true;
					break;
				} else {
					TestBase.waitNSec(1);
				}
			} catch(Exception e){
				TestBase.waitNSec(1);
			}
		}
		return exists;
	}	

	/** The waitForTxttPresent function will wait for the element text for a
	 * default duration of customized seconds To increase or decrease this time
	 * change the value of the integer 'timeoutSec' in "Common.java"  * 
	 * @param webelement
	 * @param text
	 */
	public  boolean waitForTxtPresent(WebElement webelement, String text) { 
		exists = false;
		waitForWebElementPresent(webelement);
		for(iter=0; iter<15; iter++)  { 
			try {
				if(webelement.getText().contains(text)) {                                       
					exists = true;
					break;
				}else {
					TestBase.waitNSec(1);
				}
			}catch(Exception e){
				TestBase.waitNSec(1);
			}
		}
		return exists;
	}
	
	/**
	 * Method to wait for any window to be present
	 * @return
	 */
	public  boolean waitForWindow() 
	{                
		exists = false;
		try{
			for(int iter=0;iter<30;iter++) 
			{
				windowIds = driver.getWindowHandles();
				if(windowIds.size()>1)
				{   
					exists = true;
					break;
				}else
				{
					TestBase.waitNSec(1);
				}
			}			
		}
		catch(Exception e)
		{
			TestBase.waitNSec(1);
		}
		return exists;
	}

	/** The waitForElementPresent function will wait for the element for a
	 * default duration of customized seconds To increase or decrease this time
	 * change the value of the integer 'timeoutSec' in "Common.java"  * 
	 * @param webEle	  
	 */
	public  boolean waitForWebElementPresent(WebElement webEle) {  
		exists = false;
		for(iter=0;iter<25;iter++)  {   
			try{
				if(webEle.isDisplayed())   {                                       
					exists = true;
					break;
				} else {
					TestBase.waitNSec(1);
				}
			} catch(Exception e){
				TestBase.waitNSec(1);
			}
		}
		return exists;
	}
	
	/**
	 * isElementPresent checks if the element is present in the application
	 * Returns the 
	 * @param object
	 * @return xval
	 */
	public boolean isElementPresent(String  object) {
		boolean xval=false;
		String locators;
		//driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		//Locators  locators = Locators.valueOf(object.substring(object.lastIndexOf('_')+1));
		locators=object.substring(object.lastIndexOf('_')+1);
		prop = objR.getProperty(object);
		try{
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
			//driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			return xval;
		}
		catch (NoSuchElementException e) {
			return false;
		}
	}

	/** The waitForElementNotPresent function will wait for the element for a
	 * default duration of customized seconds To increase or decrease this time
	 * change the value of the integer 'timeoutSec' in "Common.java"  * 
	 * @param object	
	 */
	public  void waitForElementNotPresent(String object) {
		for(int counter=0;counter<30;counter++)  { 
			if(!(isElementPresent(object))){
				break;
			}
		}
	}
	
	/**
	 * wait for element not displayed
	 * @param xpath
	 * @return
	 */
	public  boolean waitForelementNotdisplayed(WebElement xpathElement) {            
		exists = false;
		for(iter=0;iter<15;iter++)  { 
			try{
				if(xpathElement.isDisplayed())   {                                       
//					exists = true;
//					break;
					TestBase.waitNSec(1);
				}else{
//					TestBase.waitNSec(1);
					exists = true;
					break;
				}
			}
			catch(Exception e){
				TestBase.waitNSec(1);
			}
		}
		return exists;
	}

	/**
	 * This method checks the WebDriverWait 
	 * visibility of all elements and if not available
	 * will return false
	 * @param webElts  	 
	 */
	public boolean waitUntilVisibilityOfAllElements(List<WebElement> webElts){
		status = false;
		try{
			TestBase.waitNSec(2);
			wait.until(ExpectedConditions.visibilityOfAllElements(webElts));
			status =true;
		}catch (Exception e){
			e.printStackTrace();
			status = false;
		}
		return status;

	}

	/**
	 * This method checks the WebDriverWait Visibility
	 *  and if not available will return false
	 * @param locatorDirect - sample- "axis_customertreepanel_id"
	 * @param getPathORCommonPath 
	 * @author Nagaveni.Guttula	 
	 */
	public boolean waitUntilVisibilityOfElements(String locatorDirect, String getPathORCPath){
		status = false;
		String locators;
		try {
			//Locators locators = Locators.valueOf(locatorDirect.substring(locatorDirect.lastIndexOf('_') + 1));
			locators=locatorDirect.substring(locatorDirect.lastIndexOf('_') + 1);
			switch (locators) {
			case "link":
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.linkText(getPathORCPath)));
				status = true;
				break;
			case "xpath":
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(getPathORCPath)));
				status = true;
				break;
			case "css":
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(getPathORCPath)));
				status = true;
				break;
			case "id":
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(getPathORCPath)));
				status = true;
				break;
			case "name":
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.name(getPathORCPath)));
				status = true;
				break;
			case "classname":
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className(getPathORCPath)));
				status = true;
				break;
			default:
				Assert.fail("Invalid locator type");	
				break;
			}
		} catch (Exception e){
			e.printStackTrace();
			status = false;
		}
		return status;
	}
	
	/**
	 * This method waits for page to be in ready state
	 * @return
	 */
	public  boolean waitForJSPageLoad() {  
		exists = false;
		for(iter=0;iter<25;iter++)  
		{   
			try
			{
				JavascriptExecutor js = (JavascriptExecutor)driver; 
				//Initially bellow given if condition will check ready state of page. 
				if (js.executeScript("return document.readyState").toString().equals("complete"))
				{ 
					System.out.println("Page Is loaded."); 
					exists=true;
					break;
				} 
				else 
				{
					TestBase.waitNSec(1);
				}
			} catch(Exception e)
			{
				TestBase.waitNSec(1);
			}
		}
		return exists;
	}

}
