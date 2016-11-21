package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1762.java $
$Id: EPL_1762.java 7916 2014-06-10 12:30:12Z cariram $
*/

import java.io.IOException;

import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1762 extends SuiteCardPayment
{
	
	private String cpSubModArr[]=new String[] {snapshotBC, cpTransOverBC, cpTransJourBC, cpTransSettleBC, cpReconBC,cpAvoirBC}, 
			epSubModArr[]=new String[] {snapshotBC, epJournal, epSubModMoto}, 
			gprsSubModArr[]=new String[] {snapshotBC, gprsSimFleetBC, gprsSimUsageBC},
			tmsSubModArr[]=new String[] {snapshotBC, softwareOverview, hardwareOverview}, 
			umSubModArr[]=new String[] {snapshotBC, umSubModUsers, umSubModAct};
	
	
	/**
	 * EPL-1762 All drop down menus display each sub-modules immediately
	 * 
	 * @throws IOException
	 */

	@Test()
	public void epl_1762()
	{		
		try
		{
			logger.info(" EPL-1762 executing started");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1, 2:");
			
			modSubPagePresence(cpSubModArr,"cardpayment_link","CardPayment_xpath");
			modSubPagePresence(epSubModArr,"ePayment_link","ePayment_xpath");
			modSubPagePresence(gprsSubModArr,"GPRS_link","GPRS_xpath");
			modSubPagePresence(tmsSubModArr,"TMS_link","tms_submoduleslist_xpath");
			modSubPagePresence(umSubModArr,"UserManagement_link","UserMgt_xpath");
			logger.info(" EPL-1762 execution successful");
		} 
		catch (Throwable t)
		{
			handleException(t);
		}
	}

	
	
	/**
	 * Card Payment Sub Module method
	 * @param cpSubModArr[]
	 * @throws InterruptedException
	 */
	private void modSubPagePresence(String modarr[],String locator1,String locator2) throws InterruptedException			
	{
		Actions action = new Actions(driver);
		webelement = getCommonObject(locator1);
		action.moveToElement(webelement).perform();
		elements = getCommonObjects(locator2);		
		for (int j = 0; j < elements.size(); j++)
		{			
			String elementText = elements.get(j).getText();			
			if(elementText.equals(modarr[0]) || elementText.equals(modarr[1]) || elementText.equals(modarr[2])||
					elementText.equals(modarr[3]) || elementText.equals(modarr[4])|| elementText.equals(modarr[5]))			
				logger.info(elementText + " sub module is present under Card Payment module");
			else
			{
				logger.info(elementText + " sub module is not present under Card Payment module");
				Assert.fail(" sub module is not present under Card Payment module");
			}
		}

	}
}
