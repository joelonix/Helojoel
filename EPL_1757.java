package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1757.java $
$Id: EPL_1757.java 7916 2014-06-10 12:30:12Z cariram $
*/

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1757 extends SuiteCardPayment{
	
	private String id,transTypesArr[] = {sale, cancelSalesVal, othersLabel, refLabel, colFailed},transTypesArrInOrder[] = {sale, refLabel, cancelSalesVal, colFailed, othersLabel};
	private WebElement recieptTable;
	private List<String> transTypesList;	
	private List<WebElement> recieptRows, recieptCols;
	private int count, indexForTrans;
	
	/**
	 * EPL-1757  All applications / Check Filters / Check Transaction type filter works correctly
	 * 
	 * @throws IOException
	 */
	@Test()
	public void epl_1757()
	{		
		try
		{
			logger.info(" EPL-1757 execution started");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);			
			
			//Check list of transaction types
			logger.info("Following Transaction Types are present ");
			compareListAndArray("trans_types_xpath", transTypesArr);
			
			//Verify all the transaction types are selected by default
			logger.info("Step 2:");
			verifyAllCheckBoxesSelected("trans_type_id");
			logger.info("All transaction types are selected by default");
			
			//select Last week as period filter
			selectItem(getObject("period_id"), periodLastWk);
			getObject("search_link").click();
			waitForTxtPresent("journal_row_count_xpath", displayText);
			
			//Get the index of Transaction Type column
			//Verify if Transaction Type column has records when all checkboxes are selected		
			//Verify search by selecting transaction type one by one
			getObject("advanced_search_link").click();
			
			//un check all the check boxes
			logger.info("Step 3:");
			getObject("cp_check_all_id").click();
			
			//Check one checkbox at a time and verify the search
			logger.info("Step 4:");
			for(int cnt =0; cnt<transTypesArr.length-1; cnt ++)
			{							
					id=getPath("trans_type_id").replace("INDEX", Integer.toString((cnt+1)));		
					WebElement transCheckBox =getObjectDirect(By.id(id));
					
					//select the check box, search and verify search results
					transCheckBox.click();
					getObject("search_link").click();	
					waitForTxtPresent("journal_row_count_xpath", displayText);
					verifyTransRecords(transTypesArrInOrder[cnt]);
					//Get back to the Journal page
					getObject("advanced_search_link").click();
					
					//Deselecting the checkbox
					id=getPath("trans_type_id").replace("INDEX", Integer.toString((cnt+1)));		
					transCheckBox =getObjectDirect(By.id(id));
					transCheckBox.click();				
			}
			logger.info(" EPL-1757 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
	
	
	
	/*
	 * compares list of strings with Array of String
	 */
	private void compareListAndArray(String locator, String [] arr)
	{
		transTypesList	= getListItemsAsString(locator);
		for(count=0; count<arr.length; count++)
		{		
			Assert.assertTrue(transTypesList.contains(arr[count]));						
			logger.info(arr[count]);				
		}
	}
	
	/*
	 * Verifies all checkboxes are selected
	 */
	private void verifyAllCheckBoxesSelected(String locator)
	{
		for(count =1; count<=transTypesArr.length; count ++)
		{
			if(count > 4)
			{
				id=getPath("trans_type_id").replace("INDEX", "others");	
				WebElement otherCheckBox =getObjectDirect(By.id(id));
				Assert.assertTrue(otherCheckBox.isSelected());
			}
			else
			{
				id=getPath("trans_type_id").replace("INDEX", Integer.toString(count));		
				WebElement transCheckBox =getObjectDirect(By.id(id));
				Assert.assertTrue(transCheckBox.isSelected());
			}
		}
	}
	
	/*
	 * verifies transaction records
	 */
	private void verifyTransRecords(String transType)
	{		
		indexForTrans = getIndexForColHeader("card_payment_journal_table_header_xpath", colTransType);			
		recieptTable = getObject("table_card_payment_journal_xpath");
		recieptRows = recieptTable.findElements(By.tagName("tr"));
		if(indexForTrans!= -1){
			for(int rowIndex=0;rowIndex<(recieptRows.size());rowIndex++) {
				recieptCols = recieptRows.get(rowIndex).findElements(By.tagName("td"));
				for(int colIndex=0;colIndex<(recieptCols.size());colIndex++)
				{
					if(colIndex==indexForTrans)
					{					
						Assert.assertTrue(recieptCols.get(colIndex).getText().contains(transType));
						break;						
					}
				}
			}
			logger.info("Validated the Transaction Types '"+transType+"' field in search results");
		}
		else{
			logger.info("Index of the column is incorrect");
			Assert.fail("Index of the column is incorrect");
		}
	}

}
