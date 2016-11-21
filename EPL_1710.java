package com.ingenico.eportal.testsuite.cardPayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1710.java $
$Id: EPL_1710.java 7888 2014-06-09 13:17:58Z cariram $
*/
import java.io.IOException;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1710 extends SuiteCardPayment
{
	
	private String id, idCity, idShop; 
	private int count=0;	
	private String[] countryExpArr={valAll, countryAus,countryFran, countrySpa, countryUK},
			cityAusExpArr={cityBrisbane, citySydney}, cityFraExpArr={citylyon, cityParis}, cityUKExpArr={cityBerm, cityLondon}, citySpaExpArr={cityBarcelone, cityMadrid},	 
			cityExpArr={valAll, cityBarcelone, cityBerm, cityBrisbane, cityLondon, citylyon, cityMadrid, cityParis, citySydney},
			shopExpArr={valAll, barceloneShop1, barceloneShop2, birminghmaShop1, birminghmaShop2, brisbaneSop1, brisbaneSop2,
			londonShop1, londonShop2, shop2Lyon, shop1Lyon, madridShop1, madridShop2, shop1Paris, shop2Paris, shop1Sydney, shop2Sydney}, 

			shopBrisExpArr={brisbaneSop1, brisbaneSop2}, shopSydExpArr={shop1Sydney, shop2Sydney}, shopLyExpArr={shop2Lyon, shop1Lyon}, shopParExpArr={shop1Paris, shop2Paris},
			shopBirExpArr={birminghmaShop1, birminghmaShop2}, shopLonExpArr={londonShop1, londonShop2}, shopBarExpArr={barceloneShop1, barceloneShop2}, 
			shopMadExpArr={madridShop1, madridShop2}, countryActArr, cityActArr, shopActArr;
	
	

	/**
	 * EPL-1710 Transaction Overview sub-module / Check filters / Level filter
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1710()
	{		
		try{
			logger.info("  EPL-1710 started execution");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);	
			
			//Verify Level 1
			logger.info("Step 2, 3:");
			countryActArr=getListItems(getObject("country_id"));
			logger.info("The following countries are available in the drop down : ");
			for(count=0;count<countryActArr.length;count++)
			{			
				logger.info(countryActArr[count]);				
				Assert.assertEquals(countryActArr[count], countryExpArr[count]);				
			}
			
			//Verify Level 2					
			cityActArr=getListItems(getObject("city_id"));
			logger.info("The following cities are available in the drop down : ");
			for(count=0;count<cityActArr.length;count++)
			{			
				logger.info(cityActArr[count]);				
				Assert.assertEquals(cityActArr[count], cityExpArr[count]);
			}			
			
			//Verify Level 3			
			shopActArr=getListItems(getObject("shop_id"));
			logger.info("The following shops are available in the drop down : ");
			for(count=0;count<shopActArr.length;count++)
			{			
				logger.info(shopActArr[count]);				
				Assert.assertEquals(shopActArr[count], shopExpArr[count]);
			}	
			
			//Verify bread crumbs at all levels
			logger.info("Step 4, 5, 6, 7, 8:");
			for(count=1;count<countryActArr.length;count++)
			{			
				id=getPath("level1_id").replace("INDEX", Integer.toString(count));				
				getObjectDirect(By.id(id)).click();
				verifyBreadCrumb(countryActArr[count]);
				
				//Verify country Australia
				if(countryActArr[count].equals(countryAus))
				{
					//Verify bread crumb after selecting each city for Australia
					for(int i=0;i<cityAusExpArr.length;i++)
					{
						idCity=getPath("level1_id").replace("INDEX", Integer.toString((i+1)));						
						getObjectDirect(By.id(idCity)).click();						
						verifyBreadCrumb(cityAusExpArr[i]);
						if(cityAusExpArr[i].equals(cityBrisbane))
						{
							for(int j=0;j<shopBrisExpArr.length;j++)
							{
								//Verify bread crumb after selecting each shop for BRISBANE
								idShop=getPath("level1_id").replace("INDEX", Integer.toString((j+1)));						
								getObjectDirect(By.id(idShop)).click();						
								verifyBreadCrumb(shopBrisExpArr[j]);
								getObject("breadcrumb_level2_xpath").click();
							}
						}
						if(cityAusExpArr[i].equals(citySydney))
						{
							for(int j=0;j<shopSydExpArr.length;j++)
							{
								//Verify bread crumb after selecting each shop for SYDNEY
								idShop=getPath("level1_id").replace("INDEX", Integer.toString((j+1)));						
								getObjectDirect(By.id(idShop)).click();						
								verifyBreadCrumb(shopSydExpArr[j]);
								getObject("breadcrumb_level2_xpath").click();
							}
						}						
						getObject("breadcrumb_level1_xpath").click();
					}
					logger.info("Breadcrumb is correctly displayed after selecting each shop for country "+countryActArr[count]);
					logger.info("Breadcrumb is correctly displayed after selecting each city for country "+countryActArr[count]);
				}
				//Verify country France
				if(countryActArr[count].equals(countryFran))
				{
					for(int i=0;i<cityFraExpArr.length;i++)
					{
						//Verify bread crumb after selecting each city for France
						idCity=getPath("level1_id").replace("INDEX", Integer.toString((i+1)));						
						getObjectDirect(By.id(idCity)).click();						
						verifyBreadCrumb(cityFraExpArr[i]);
						if(cityFraExpArr[i].equals(citylyon))
						{
							for(int j=0;j<shopLyExpArr.length;j++)
							{
								//Verify bread crumb after selecting each shop for LYON
								idShop=getPath("level1_id").replace("INDEX", Integer.toString((j+1)));						
								getObjectDirect(By.id(idShop)).click();						
								verifyBreadCrumb(shopLyExpArr[j]);
								getObject("breadcrumb_level2_xpath").click();
							}
						}
						if(cityFraExpArr[i].equals(cityParis))
						{
							for(int j=0;j<shopParExpArr.length;j++)
							{
								//Verify bread crumb after selecting each shop for PARIS
								idShop=getPath("level1_id").replace("INDEX", Integer.toString((j+1)));						
								getObjectDirect(By.id(idShop)).click();						
								verifyBreadCrumb(shopParExpArr[j]);
								getObject("breadcrumb_level2_xpath").click();
							}
						}						
						getObject("breadcrumb_level1_xpath").click();
					}
					logger.info("Breadcrumb is correctly displayed after selecting each shop for country "+countryActArr[count]);
					logger.info("Breadcrumb is correctly displayed after selecting each city for country "+countryActArr[count]);
				}
				//Verify country Spain
				if(countryActArr[count].equals(countrySpa))
				{
					//Verify bread crumb after selecting each city for Spain
					for(int i=0;i<citySpaExpArr.length;i++)
					{
						idCity=getPath("level1_id").replace("INDEX", Integer.toString((i+1)));						
						getObjectDirect(By.id(idCity)).click();						
						verifyBreadCrumb(citySpaExpArr[i]);
						if(citySpaExpArr[i].equals(cityBarcelone))
						{
							for(int j=0;j<shopBarExpArr.length;j++)
							{
								//Verify bread crumb after selecting each shop for BARCELONE
								idShop=getPath("level1_id").replace("INDEX", Integer.toString((j+1)));						
								getObjectDirect(By.id(idShop)).click();						
								verifyBreadCrumb(shopBarExpArr[j]);
								getObject("breadcrumb_level2_xpath").click();
							}
						}
						if(citySpaExpArr[i].equals(cityMadrid))
						{
							for(int j=0;j<shopMadExpArr.length;j++)
							{
								//Verify bread crumb after selecting each shop for MADRID
								idShop=getPath("level1_id").replace("INDEX", Integer.toString((j+1)));						
								getObjectDirect(By.id(idShop)).click();						
								verifyBreadCrumb(shopMadExpArr[j]);
								getObject("breadcrumb_level2_xpath").click();
							}
						}						
						getObject("breadcrumb_level1_xpath").click();
						
					}
					logger.info("Breadcrumb is correctly displayed after selecting each shop for country "+countryActArr[count]);
					logger.info("Breadcrumb is correctly displayed after selecting each city for country "+countryActArr[count]);
				}
				//Verify country UK
				if(countryActArr[count].equals(countryUK))
				{
					//Verify bread crumb after selecting each city for UK
					for(int i=0;i<cityUKExpArr.length;i++)
					{
						idCity=getPath("level1_id").replace("INDEX", Integer.toString((i+1)));						
						getObjectDirect(By.id(idCity)).click();						
						verifyBreadCrumb(cityUKExpArr[i]);
						if(cityUKExpArr[i].equals(cityBerm))
						{
							for(int j=0;j<shopBirExpArr.length;j++)
							{
								//Verify bread crumb after selecting each shop for BIRMINGHAM
								idShop=getPath("level1_id").replace("INDEX", Integer.toString((j+1)));						
								getObjectDirect(By.id(idShop)).click();						
								verifyBreadCrumb(shopBirExpArr[j]);
								getObject("breadcrumb_level2_xpath").click();
							}
						}
						if(cityUKExpArr[i].equals(cityLondon))
						{
							for(int j=0;j<shopLonExpArr.length;j++)
							{
								//Verify bread crumb after selecting each shop for LONDON
								idShop=getPath("level1_id").replace("INDEX", Integer.toString((j+1)));						
								getObjectDirect(By.id(idShop)).click();						
								verifyBreadCrumb(shopLonExpArr[j]);
								getObject("breadcrumb_level2_xpath").click();
							}
						}						
						getObject("breadcrumb_level1_xpath").click();
					}
					logger.info("Breadcrumb is correctly displayed after selecting each shop for country "+countryActArr[count]);
					logger.info("Breadcrumb is correctly displayed after selecting each city for country "+countryActArr[count]);
				}
				getObject("cardpayment_link").click();	
				getObject("transaction_overview_link").click();	
			}			
			logger.info("Breadcrumb is correctly displayed after selecting each country");
			logger.info(" EPL-1710 execution successful");
			} 
		catch (Throwable t)
		{
			handleException(t);	
		}	
	}
	
}
