package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1744.java $
$Id: EPL_1744.java 7888 2014-06-09 13:17:58Z cariram $
*/

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1744 extends SuiteCardPayment {
	private boolean isRadioPresent=true;
	private String app1 = alphraApp, app2 = emvAUSApp;
	private ArrayList<String> colsText = new ArrayList<String>(),appTypes = new ArrayList<String>();
	private WebElement table;
	private List<WebElement> columns,listOfRadioButtons,listOfHeaders;
	private List<String> headers = new ArrayList<String>();		
	
	
	
	/**
	 * EPL-1744 Check no presence of card number filter in advanced criteria and detailed view
	 * 
	 * @throws IOException
	 */
	
	@Test()
	public void epl_1744()
	{		
		try
		{
			logger.info(" EPL-1744 executing started");	
			login(CONFIG.getProperty("superuser2"),CONFIG.getProperty("superuserPassword"));	
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			
			//Get list of apptypes
			appTypes = listOfAppTypes();		
			
			//For each app type verify card num field absence, radio buttons in search results and absence of card num field in detailed view
			logger.info("Step 2, 3, 4, 5, 6:");
			for(int index=0;index<appTypes.size();index++)
			{				
				String app = appTypes.get(index);
				
				if(app.equals(app1)){
					selectAppli(alphraAppVal);
					logger.info(" Selected application type, Alphyra FR");	
				}
				
				else if(app.equals(app2)){
					selectAppli(emvAusAppVal);
					logger.info(" Selected application type, CHEQUE FR");	
				}
				
				Assert.assertFalse(isElementPresent("card_num_id"));
				logger.info(" Card Number field is not displayed for the selected application type in Advance Criteria");
				
				//Verify card num in main view					
				getObject("search_link").click();
				
				listOfRadioButtons = get_list_of_elements("journal_reciept_radio_xpath");
				if(listOfRadioButtons.size()==0){
					isRadioPresent = false;
				}
				//Verify Radio buttons and view receipt link
				Assert.assertFalse(isRadioPresent);
				Assert.assertFalse(isElementPresent("view_receipt_link"));
				
				logger.info("At the top of Main view, there isn't the view receipt button");
				logger.info("At the left of Main view, for each transaction there is no radio button to open the view receipt");

				//Verify card num in detailed view			
				listOfHeaders = getObject("card_payment_journal_id").findElements(By.tagName("th"));
				for(WebElement header: listOfHeaders){
						headers.add(header.getText());
				}
				Assert.assertFalse(headers.contains(cardNum));				
				logger.info("Verified the Card Number is not present in Main view for apptype "+appTypes.get(index));
				
				verifyCardNumNotPresentInDetailedView(cardNum);
				cardPaymentSubPageNavigator("TransactionJournal");
			}
			logger.info(" EPL-1744 execution successful");
			
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
		appTypeList.add(app1);		
		appTypeList.add(app2);	
		return appTypeList;
	}
	
	/**
	 * verifyCardNumPresentInDetailedView verifies if card Number field is present for a app type in detailed view
	 * @return ArrayList<String>
	 */
	private void verifyCardNumNotPresentInDetailedView(String cardNum) throws InterruptedException
	{		
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
				Assert.assertEquals(colsText.contains(cardNum), false);
				logger.info("Card Number is not displayed in detail window");
				logger.info("Closing the detail window");
				driver.close();
				driver.switchTo().window(ParentWindow);
			}
			else{
				logger.info("No results to your search.Failing the test");
				Assert.fail("No results to your search.Failing the test");
			}
		}
		catch (NullPointerException e) {
			captureScreenShotOnFailure(fileName);
			driver.close();
			driver.switchTo().window(ParentWindow);
		}
	}


}
