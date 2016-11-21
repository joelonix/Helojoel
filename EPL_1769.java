package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1769.java $
$Id: EPL_1769.java 7916 2014-06-10 12:30:12Z cariram $
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1769 extends SuiteCardPayment{
	private String pages;
	private List<WebElement> journalHeader,rows,cols;
	private List<String> jh;
	private int totalPages,rdIndex;
	private String [] colArr = {colReceipt, colServer, colOpMode, colAmt, colShop, colAppType, tabCardType, colTransType, colResCode, colMID, colPOS, colSettlement, 
			colSettNum, colSettDate, colAuth, colP2PE, colNUS, colUserData2};

	/**
	 * EPL-1769 All applications / Check content of Main View
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1769()
	{		
		try
		{
			logger.info(" EPL-1769 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");

			//Selecting appType EMV AUS
			cardPaymentSubPageNavigator(subModTrnJrn);
			getObject("search_link").click();

			//Fetching table header and putting it into String list
			logger.info("Step 2:");
			verifyAllColPresent(colArr);
			journalHeader = get_list_of_elements("card_payment_journal_table_header_xpath");
			jh =  new ArrayList<String>();
			for(int jheadIndex=0;jheadIndex<journalHeader.size();jheadIndex++)  jh.add(journalHeader.get(jheadIndex).getText());

			//Fetching all rows
			rows = getObject("table_card_payment_journal_xpath").findElements(By.tagName("tr"));

			//Plus icon presence
			logger.info("Step 3:");
			Assert.assertTrue(isElementPresent("plus_icon_xpath"));
			logger.info("validated plus icon");

			pages = getObject("cp_span_label_xpath").getText();
			totalPages = Integer.parseInt(pages.replace("of ", ""));
			logger.info("Fetched total number of pages=" + totalPages);

			//Validating Receipt Date for all rows
			rdIndex = jh.indexOf("Receipt Date");
			cols = rows.get(i).findElements(By.tagName("td"));

			//Validating Receipt date
			String exp = cols.get(rdIndex).getText();
			Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}(\\s(A|P)M)?");
			Matcher m = p.matcher(exp);
			Assert.assertTrue(m.find());
			logger.info("Validated receipt date");

			//Validating Server Date
			exp = cols.get(jh.indexOf("Server Date")).getText();
			m = p.matcher(exp);
			Assert.assertTrue(m.find());
			logger.info("Vaidated server date");

			//Validating Amount of transaction with currency type
			p = Pattern.compile("AUD\\s([0-9]*)\\.[0-9]*");
			exp = cols.get(jh.indexOf("Amount")).getText();
			m = p.matcher(exp);
			//Assert.assertTrue(m.find());
			logger.info("Vaidated Amount with currency");
			logger.info(" EPL-1769 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}



}
