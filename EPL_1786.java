package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1786.java $
$Id: EPL_1786.java 7916 2014-06-10 12:30:12Z cariram $
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1786 extends SuiteCardPayment {
	private List<WebElement> cols,header=new ArrayList<WebElement>();
	private List<String> firstColVals = new ArrayList<String>(),tableHeaderData  = new ArrayList<String>();
	private String[][] eData;
	private String []qa_eportalc1Row, ingenicoRow, DifferenceRow;
	private WebElement table;
	public static  Locale locale = Locale.ENGLISH;
	private List<String> listOfFirstColVals(){
		firstColVals.add(custOne);
		firstColVals.add(ingenicoCheck);
		firstColVals.add(diffCheck);
		return firstColVals;
	}
		
	/**
	 * expectedHeaderList returns a List of expected header data
	 * @return List<WebElement> 
	 */
	private List<String> expectedHeaderList(){
		List<String> headers = new ArrayList<String>();
		headers.add(" ");
		headers.add(colSale);
		headers.add(colSaleAmt);
		headers.add(colRefunds);
		headers.add(colRefundAmt);
		headers.add(colTotal);
		headers.add(colNetAmt);
		return headers;
	}
	/**
	 * verifyCalculasFor takes headerName and calculates the difference betweeb qa_eportal val and ingenico val 
	 * and matches it with the difference row value in the table
	 * @param headerName 
	 */

	private void verifyCalculasFor(String header){
		qa_eportalc1Row = eData[0];
		ingenicoRow = eData[1];
		DifferenceRow = eData[2];
		int headerIndex = tableHeaderData.indexOf(header);
		if(qa_eportalc1Row[headerIndex].contains("AUD ")){
			StringBuffer sb1 = new StringBuffer(qa_eportalc1Row[headerIndex]);
			sb1.replace(0, 4, "");
			String s1 = sb1.toString();
			if(s1.contains(",")){
				s1 = s1.replace(",", "");
			}
			float f1 = Float.valueOf(s1.trim()).floatValue();

			StringBuffer sb2 = new StringBuffer(ingenicoRow[headerIndex]);
			sb2.replace(0, 4, "");
			String s2 = sb2.toString();
			if(s2.contains(",")){
				s2 = s2.replace(",", "");
			}
			float f2 = Float.valueOf(s2.trim()).floatValue();

			StringBuffer sb3 = new StringBuffer(DifferenceRow[headerIndex]);
			sb3.replace(0, 4, "");
			String s3 = sb3.toString();
			if(s3.contains(",")){
				s3 = s3.replace(",", "");
			}
			float f3 = Float.valueOf(s3.trim()).floatValue();
			Assert.assertEquals(f1-f2, f3);
		}
		else{
			String qaRowVal = qa_eportalc1Row[headerIndex];
			String ingenicoRowVal = ingenicoRow[headerIndex];
			String diffRowVal = DifferenceRow[headerIndex];
			if(qaRowVal.contains(",")){
				qaRowVal = qaRowVal.replace(",", "");
			}
			if(ingenicoRowVal.contains(",")){
				ingenicoRowVal = ingenicoRowVal.replace(",", "");
			}
			if(diffRowVal.contains(",")) {
				diffRowVal = diffRowVal.replace(",", "");
			}
			int v1 = Integer.parseInt(qa_eportalc1Row[headerIndex]);  
			int v2 = Integer.parseInt(ingenicoRow[headerIndex]);	
			int diff = Integer.parseInt(DifferenceRow[headerIndex]);
			Assert.assertEquals(v1-v2, diff);
		}
	}

	/**
	 * EPL-1786 To Verify the transaction reconciliation module display tables verification
	 * 
	 * @throws IOException
	 */
	@Test()
	public void epl_1786()
	{		
		try
		{
			logger.info(" EPL-1786 executing started");			
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			
			// The breadcrumb must contain this text “Transaction Reconciliation”
			waitForElementPresent("cardpayment_link");
			cardPaymentSubPageNavigator(subModTrnRecon);
			verifyBreadCrumb(cpReconBC);
			logger.info("Transaction Reconciliation submodule is visible");

			//Set From and To date
			logger.info("Step 2:");
			setYearMonthDay();		

			//Executing Search
			clickReconSearch("reconciliation_bank_id");

			//Verify breadcrumb
			verifyShopBC();

			//Trading date tab 
			verifyTradeDate();	
			text=getNusDataTableId();
			rows = verifyNoReconcile("nusdate_xpath",text);

			//Verifying if data is there		  
			if(rows.size()==1 && rows.get(0).getText().equals("Not yet reconciled.")){
				logger.info("Not yet reconciled. Test data not available for test to proceed.");
				Assert.fail("Not yet reconciled. Test data not available for test to proceed.");
			}
			else{	

				//In the tab, there are 3 main lines:Customer line (store), Ingenico line,	Difference line
				rows = getTableRows(text);
				firstColVals = listOfFirstColVals();
				for(int rowIndex=1;rowIndex<(rows.size());rowIndex++) {
					cols = rows.get(rowIndex).findElements(By.tagName("td"));
					for(int colIndex=0;colIndex<(cols.size());colIndex++){	
						if(colIndex==0){
							Assert.assertEquals(firstColVals.contains(cols.get(colIndex).getText()), true);
						}
					}
				}
				logger.info("Validated 3 main lines in the table");

				//Verify Headers
				header = getheaderData(text);
				for(WebElement headerText: header){
					tableHeaderData.add(headerText.getText());
				}
				Assert.assertEquals(tableHeaderData, expectedHeaderList());
				logger.info("Header data validated");


				//Getting table rows into a 2D array
				eData = new String[rows.size()+1][7];

				for(int row=1;row<(rows.size());row++) {
					cols = rows.get(row).findElements(By.tagName("td"));
					for(int col=0;col<(cols.size());col++){	
						eData[row-1][col] = cols.get(col).getText();
					}
				}
				//Verify the Calculas for various columns
				logger.info("Step 3:");
				verifyCalculasFor(colSale);
				verifyCalculasFor(colSaleAmt);
				verifyCalculasFor(colRefunds);
				verifyCalculasFor(colRefundAmt);
				verifyCalculasFor(colTotal);
				verifyCalculasFor(colNetAmt);
				logger.info("Calculas for various columns validated successfully");

				// clicking on the date that is having data and Verify it is possible to hide a tab of reconciliation transactions of a day by clicking on the name of trading _date
				logger.info("Step 4:");
				String[] splitTxt=text.split("_");
				int len=splitTxt.length;
				xpath=getPath("nusdatelink_xpath").replace("NUSDATE","NUS: "+splitTxt[len-1]);
				getObjectDirect(By.xpath(xpath)).click();
				waitNSec(2);
				xpath=getPath("nusdate_xpath").replace("TABLEID", text);
				table =getObjectDirect(By.xpath(xpath));
				Assert.assertEquals(table.isDisplayed(), false);
				logger.info("Table is hidden by clicking on name of trading _date");

				//Check it is possible to show a tab of reconciliation transactions of a day by clicking on the name of trading _date
				logger.info("Step 5:");
				xpath=getPath("nusdatelink_xpath").replace("NUSDATE","NUS: "+splitTxt[len-1]);
				getObjectDirect(By.xpath(xpath)).click();
				waitNSec(2);
				Assert.assertEquals(table.isDisplayed(), true);
				logger.info("Table is displayed again by clicking on name of trading _date");
			}	
			logger.info(" EPL-1786 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
}