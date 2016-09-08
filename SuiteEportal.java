package com.ingenico.testsuite.eportal;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/eportal/SuiteEportal.java $
$Id: SuiteEportal.java 14606 2015-08-24 13:01:43Z jsamuel $
 */
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;

import com.ingenico.base.TestBase;
/**
 * Common Methods and Variables for the eportal package
 *
 */
public class SuiteEportal extends TestBase {

	//TODO***Framework Related Functions***
	
	/** 
	 * Initializes suite execution
	 */
	@BeforeSuite(alwaysRun=true)
	void initSetUp()  {
		initialize();
	}	
	
	//TODO****Common functions to eportal module****

	
	/**
	 * Method to verify presence of submodule
	 * @param subModuleName,@param tab, @param subModule
	 */
	public void vPresenceOfSubMod(String subModuleName,WebElement tab,String subModule){ 		
		exists=false;
		action = new Actions(driver);
		action.moveToElement(tab).build().perform();
		waitMethods.waitForWebElementPresent(selUtils.getCommonObject(subModule));
		xpath=getCommonPath(subModule);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
		tabContainer = selUtils.getCommonObject(subModule);
		options = tabContainer.findElements(By.tagName("li"));
		for(WebElement subPage : options){
			value=	subPage.getText();
			if(value.trim().equalsIgnoreCase(subModuleName.trim())){
				logger.info(subModuleName + ", sub module is present ");
				exists=true;
				break;
			}
			
		}
		if(!exists){
			logger.info(subModuleName + ", sub module is not present under Main Tab");
			Assert.fail(" Sub module is not present under Main Tab ");
		}
	}

}
