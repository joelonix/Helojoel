package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1771.java $
$Id: EPL_1771.java 7916 2014-06-10 12:30:12Z cariram $
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
public class EPL_1771 extends SuiteCardPayment{
	private String str;
	private List<WebElement> rows,row,col,headerData,columnCheckboxList;
	private List<String>headerDataAsString;
	private int index;
	private StringBuffer sb = new StringBuffer();
	
	/**
	 * getTableRowCol returns string with row columns data with space in between
	 * @return String
	 */
	private String getTableRowCol(List<WebElement> col){
		str = "";
		for(int j = 0;j <col.size();j++){
			str = str+" "+col.get(j).getText();
		}
		return str;
	}

	/**
	 * getTableRow returns string for entire row with specified index
	 * @param indexVal
	 * @return String
	 */
	public String getTableRow(int indexVal){
		row = getObject("card_payment_journal_id").findElements(By.tagName("tr"));
		col= row.get(indexVal).findElements(By.tagName("td"));
		return getTableRowCol(col);
	}
	/**
	 * EPL-1771 All applications / Check export PDF of Main View
	 * 
	 * @throws IOException
	 */
	@Test()
	public void epl_1771()
	{		
		try
		{
			logger.info(" EPL-1771 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnJrn);
			multiSelect("select_appli", emvAusAppVal);
			getObject("search_link").click();
			logger.info("Step 2:");
			restoreTables();

			//PDF
			//Preparing test data
			getObject("column_icon_xpath").click();
			columnCheckboxList = get_list_of_elements("cp_checkbox_tab_xpath");
			for(int i=6;i<columnCheckboxList.size();i++){
				columnCheckboxList.get(i).click();
			}
			saveWebTable();

			//Clicked pdf download
			logger.info("Step 3:");
			clickOnDownloadCSVPDF("export_pdf_xpath", pdfType);
			if(driver.getPageSource().contains("Some data has already been output, can't send PDF file")){
				logger.info("PDF download has application error 'FPDF error'.Failing the test");
				Assert.fail("PDF download has application error 'FPDF error'.Failing the test");
			}
			waitNSec(5);
			Process proc1 = Runtime.getRuntime().exec(CommonConstants.autoItPath+"FF.exe");
			Thread.sleep(CommonConstants.fiveSec);
			String pdf = PDF_Reader.readPdf(CommonConstants.filedownloadPath+ cpPDFJrn + ".pdf");
			logger.info("pdf read completed");
			proc1.destroy();
			rows = getObject("card_payment_journal_id").findElements(By.tagName("tr"));

			//Verifying Header data with pdf
			headerData = rows.get(0).findElements(By.tagName("th"));
			headerDataAsString = new ArrayList<String>();
			for(int x=0;x<headerData.size();x++){
				headerDataAsString.add(headerData.get(x).getText());
			}
			for(int j=0;j<headerDataAsString.size();j++){
				sb = sb.append(headerDataAsString.get(j));
				sb = sb.append(" ");
			}
			Assert.assertTrue(pdf.contains(sb.toString().trim())) ;

			//Verifying table data with pdf
			logger.info("Step 4:");
			index =1;
			while(index < rows.size()){
				Assert.assertEquals(pdf.contains(getTableRow(index).trim()), true);
				index++;
			}
			getObject("restore_config_xpath").click();
			getObject("transaction_journal_ok_xpath").click();
			logger.info("Validated pdf with table data");
			logger.info(" EPL-1771 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}
}
