package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1713.java $
$Id: EPL_1713.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1713 extends SuiteCardPayment
{
	private boolean flag=false;
	private String appDefaultVal;		
	private int count;
	private String[] appArr;
	
	

	/**
	 * EPL-1713 Transaction Overview sub-module / Check filters / Application and card type filters
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1713()
	{		
		try{
			logger.info("  EPL-1713 started execution");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);	
			
			//Verify presence of Application Type filter
			logger.info("Step 2, 3, 4:");
			if(getObject("select_appli_id").isDisplayed())
			{
				appDefaultVal=getSelectedItem(getObject("select_appli_id"));
				Assert.assertEquals(appDefaultVal, valAll);
				logger.info("Following Application Types are displayed in drop down :");
				appArr = getListItems(getObject("select_appli_id"));
				for(count =0; count<appArr.length; count++)
				{
					logger.info(appArr[count]);
				}
				//Verify presence of Card Type filter
				Assert.assertFalse(isElementPresent("cet_id"));				
			}			
			logger.info("The default value for application type filter is 'All' and at this moment, the card type filter doesn't appear");
			
			//Verify for appNoCardType Card Type is not present
			logger.info("Step 5, 6:");
			verifyCETPresence(chqFRApp);			
			logger.info("The card type filter doesn't appear for application "+chqFRApp+ " and search is correct and coherency of bread crumb is as expected");
			
			flag = verifySelectOption("select_appli_id", amexApp);
			if(flag)
			{
				//Verify for appCardType Card Type is present				
				verifyCETPresence(amexApp);
				logger.info("The card type filter appears for application "+amexApp+ " and search is correct and coherency of bread crumb is as expected");
				logger.info(" EPL-1713 execution successful");
			}
			else{
				logger.info(amexApp +" is not available in the appliaction to select");
				Assert.fail(amexApp +" is not available in the appliaction to select");
			
			}				
		} 
		catch (Throwable t)
		{
			handleException(t);		
		}	
	}
	
	
	/**
	 * verifies cet filter presence after selecting application
	 * @param appType
	 */
	private void verifyCETPresence(String appType)
	{
		selectItem(getObject("select_appli_id"),appType);
		if(appType.equals(chqFRApp))
		{
			Assert.assertFalse(isElementPresent("cet_id"));
		}
		if(appType.equals(amexApp))
		{
			Assert.assertTrue(isElementPresent("cet_id"));
		}		
		getObject("search_link").click();
		verifyBreadCrumb(appType);
	}	
}
