package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1699.java $
$Id: EPL_1699.java 7858 2014-06-09 08:59:33Z cariram $
*/
import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class EPL_1699 extends SuiteCardPayment
{
	
	private String xpath;
	private WebElement myElement;
	
	

	/**
	 * EPL-1699 Check graphs of Yesterday’s Main Information section / Check Payment Amounts Table
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1699()
	{		
		try
		{
			logger.info(" EPL-1699 execution started");	
			login(CONFIG.getProperty("superuser3"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(snapshotBC);
			
			//Verify Australia country graph
			logger.info("Step 2:");
			xpath = getPath("snapshot_graph_xpath").replace("div[2]", "div[1]");
			myElement =getObjectDirect(By.xpath(xpath));
			if(myElement.getText().equals(csHourlyBGraph))
			{
				logger.info(csHourlyBGraph+" graph is displayed");
			}
			else if (myElement.getText().equals(csCTBGraph))
			{
				logger.info(csCTBGraph+" graph is displayed");
			}
			else
			{
				logger.info(csABGraph+" graph is displayed");
			}
			
			getContains(csLevel2BGraph);
			logger.info(csLevel2BGraph+" graph is displayed");			
			logger.info(" EPL-1699 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
}
