package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1748.java $
$Id: EPL_1748.java 7888 2014-06-09 13:17:58Z cariram $
*/

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;
import com.ingenico.util.PDF_Reader;

public class EPL_1748 extends SuiteCardPayment {
	
	private String pdf;
	private ArrayList<String> csvHeaderList=new ArrayList<String>(),colsText = new ArrayList<String>();
	private WebElement table;
	private List<WebElement> columns;
	
	
	
	/**
	 * EPL-1748 User which client hasn’t User Data 1, 2, 3, 4, 5 provisioned in Everest / Search tools - customized fields section / check no presence of User Data filters
	 * 
	 * @throws IOException
	 */
	
	@Test()
	public void epl_1748()
	{		
		try
		{
			logger.info(" EPL-1748 executing started");
			login(CONFIG.getProperty("superuser2"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			
			//Customized fields are not present in Trans Journal for Superuser2
			logger.info("Step 2:");
			getObject("advanced_search_link").click();
			Assert.assertFalse(isElementPresent("journal_nus_xpath")); 
			Assert.assertFalse(isElementPresent("journal_nuslabel_xpath"));
			Assert.assertFalse(isElementPresent("journal_userData2_en_xpath"));
			Assert.assertFalse(isElementPresent("journal_userData2_label_xpath"));
			
			logger.info("Validated Customized fields are not present in Trans Journal for Superuser2 in CSV");
			
			logger.info("Step 3:");
			getObject("search_link").click();
			
			//Customized fields (with particular name) are no present in the CSV file
			logger.info("Step 4:");
			verifyCSVData();
			
			//Customized fields (with particular name) are no present in the PDF file
			logger.info("Step 5:");
			verifyPDFData();
			
			//Customized fields (with particular name) are no present in the detailed view
			getObject("plus_icon_xpath").click();
			logger.info("Clicked plus icon");
			
			// switch to the card payment details popup window
			switchToWindow();
			
			//Verify if details window is displayed
			logger.info("Step 6:");
			table = getObject("cp_detailedview_table_xpath");
			columns = table.findElements(By.tagName("td"));
			for(int colIndex = 0;colIndex<columns.size();colIndex++){
				colsText.add(columns.get(colIndex).getText());
			}
			//Verify customized fields are not present in detailed view
			Assert.assertEquals(colsText.contains(colNUS), false);
			Assert.assertEquals(colsText.contains(colUserData2), false);
			logger.info("Verified the Customized fields NUS and UserData2_EN are not present in details window");
			driver.close();
			driver.switchTo().window(ParentWindow);
			logger.info(" EPL-1748 execution successful");
		} 
		catch (Throwable t)
		{
			handlePopUpException(t);
		}
	}
	
	/**
	 * verifies CSV data
	 * @throws IOException
	 */
	private void verifyCSVData() throws IOException
	{
		clickOnDownloadCSVPDF("export_csv_xpath", csvType);		
		fileName = cpCSVJrn+date_Formatter("_");
		String path = getCSVPDFPath(fileName, csvType);		
		//Getting the header Data from CSV
		csvHeaderList = readCSVHeader(path);
		
		Assert.assertEquals(csvHeaderList.contains(colNUS), false);
		Assert.assertEquals(csvHeaderList.contains(colUserData2), false);
		logger.info("Validated Customized fields NUS and UserData2_EN are not present in CSV");
	}
	
	/**
	 * verifies PDF data
	 * @throws IOException
	 */
	private void verifyPDFData()
	{
		try
		{
			clickOnDownloadCSVPDF("export_pdf_xpath", pdfType);			
			proc1 = Runtime.getRuntime().exec(CommonConstants.autoItPath+autiItExe);
			
			String path = getCSVPDFPath(cpPDFJrn, pdfType);
			pdf = PDF_Reader.readPdf(path);			
			logger.info("pdf read completed");
			proc1.destroy();
			Assert.assertEquals(pdf.contains(colNUS), false);
			Assert.assertEquals(pdf.contains(colUserData2), false);
			logger.info("Validated Customized fields NUS and UserData2_EN are not present for Superuser2 in PDF");
						
		}
		catch(Throwable t)
		{
			proc1.destroy();	
			Assert.fail("PDF verification failed");
		}
	}
	
}
