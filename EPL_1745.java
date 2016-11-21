package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1745.java $
$Id: EPL_1745.java 7888 2014-06-09 13:17:58Z cariram $
*/

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1745 extends SuiteCardPayment
{	
	private ArrayList<String> colsText = new ArrayList<String>(),appTypes = new ArrayList<String>();
	private WebElement table;
	private List<WebElement> columns,listOfRadioButtons;
	
	/**
	 * EPL-1745  Check presence of PAN display / a user which customer has PAN display provisioned in Everest
	 * 
	 * @throws IOException
	 */
	
	@Test()
	public void epl_1745()
	{		
		try
		{
			logger.info(" EPL-1745 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			
			//Get list of apptypes
			appTypes = listOfAppTypes();
			
			//For each app type verify card num field presence, radio buttons in search results and presence of card num field in detailed view
			logger.info("Step 2, 3, 4, 5, 6:");
			for(int index=0;index<appTypes.size();index++)
			{				
				String app = appTypes.get(index);
				if(app.equals(amexApp)){
					selectAppli(amexAppVal);
					logger.info(" Selected 'Amex' Application Type");	
				}
				
				else if(app.equals(emvESApp)){
					selectAppli(emvEsAppVal);
					logger.info(" Selected 'EMV ES' Application Type");	
				}
				else if(app.equals(emvFRApp)){
					selectAppli(emvFRAppVal);
					logger.info(" Selected 'EMV FR' Application Type");
				}
				else{
					selectAppli(emvUKAppVal);
					logger.info(" Selected 'EMV UK' Application Type");
				}
				
				waitForElementPresent("card_num_id");
				Assert.assertTrue(isElementPresent("card_num_id"));
				logger.info(" Card Number is displayed for the Application selected in Advanced Criteria");
				
				//Hari: Adding Search 
				getObject("search_link").click();
				
				//validate table data is present or not
				//noResultToSearch();
				verifyIfnoData("noresult_search_xpath");
				
				//Verify card num in detailed view				
				int indexForCardNum  = getIndexForColHeader("card_payment_journal_table_header_xpath", cardLabel);	
				String xpath = getPath("card_num_table_header_xpath").replace("INDEX", Integer.toString(indexForCardNum+1));
				
				Assert.assertEquals(getObjectDirect(By.xpath(xpath)).getText().equals(cardLabel), true);
				logger.info(" Card Number is displayed for the Application selected in Main View");
				
				listOfRadioButtons = get_list_of_elements("journal_reciept_radio_xpath");
				
				//Verify Radio buttons and view receipt link
				for(WebElement radio: listOfRadioButtons){
					Assert.assertTrue(radio.isDisplayed());					
					Assert.assertTrue(getObject("view_receipt_link").isEnabled());					
				}
				logger.info(" At the left of Main view, for each sale transaction, there is  radio button to open the view receipt ");
				verifyCardNumPresentInDetailedView(cardLabel);
				
				cardPaymentSubPageNavigator(subModTrnJrn);
			}		
			logger.info(" EPL-1745 execution successful");
			
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	
	
	/**
	 * listOfAppTypes returns list of expected Application types
	 * @return ArrayList<String>
	 */
	private ArrayList<String> listOfAppTypes(){
		ArrayList<String> appTypeList = new ArrayList<String>();
		appTypeList.add(amexApp);		
		appTypeList.add(emvESApp);
		appTypeList.add(emvFRApp);		
		appTypeList.add(emvUKApp);				
		return appTypeList;
	}
	
	/**
	 * verifyCardNumPresentInDetailedView verifies if card Number field is present for a app type in detailed view
	 * @return ArrayList<String>
	 */
	private void verifyCardNumPresentInDetailedView(String cardNum) throws InterruptedException{
		//migrate from 3.0 to 3.0.2(commented below part)
		try{
			if(isElementPresent("plus_icon_xpath")==true){
				getObject("plus_icon_xpath").click();				
				logger.info("Clicked plus icon to open detailed view");
				
				// switch to the card payment details popup window
				switchToWindow();
				logger.info("Switched to detail window");
				
				//Verify if details window is displayed
				table = getObject("cp_detailedview_table_xpath");
				columns = table.findElements(By.tagName("td"));
				for(int colIndex = 0;colIndex<columns.size();colIndex++){
					colsText.add(columns.get(colIndex).getText());
				}
				logger.info("Card number validating is "+cardNum);
				Assert.assertEquals(colsText.contains(cardNum), true);
				logger.info("Card Number field is displayed in detail window");
				driver.close();
				driver.switchTo().window(ParentWindow);
			}
			else{
				logger.info("No results to your search.Failing the test");
				Assert.fail("No results to your search.Failing the test");
			}
		}
		catch(NullPointerException e){
			captureScreenShotOnFailure(fileName);
			driver.close();
			driver.switchTo().window(ParentWindow);
		}
	}
	
}
