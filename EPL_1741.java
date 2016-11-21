package com.ingenico.eportal.testsuite.cardPayment;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/ePortal/branches/20140616_eportal_3.3.1/src/com/ingenico/eportal/testsuite/cardPayment/EPL_1741.java $
$Id: EPL_1741.java 7888 2014-06-09 13:17:58Z cariram $
*/

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1741 extends SuiteCardPayment
{
	
	private String country,city, shop, actVal, netVal, saVal, raVal, csaVal, craVal;	
	private String[] transHeadersArr={tabCardType, colTransaction, colNetVal, colSale, colSaleAmt, colRefunds, colRefundAmt, colCanSales, csCanSaleAmtRow, colCanRef, cancelRefundsAmnt},
			transHeadersUKArr={tabCardType, colTransaction, colNetVal, colSale, colSaleAmt, colRefunds, colRefundAmt, colCanSales, csCanSaleAmtRow, colCanRef, cancelRefundsAmnt, colCanCashB, cancelCashBackAmnt};
	private List<WebElement> transList, salesList, refundsList, cancelList, cancelRefundList, cancelCashBackList, craList, ccbList, netvalList, saList, raList, csaList;
	private int count, actTrans;
	private double netValue, saValue, raValue, csaValue, craValue, ccbValue; 
	
	

	/**
	 * EPL-1741 Check content of main view / Card type view / all
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1741()
	{		
		try{
			logger.info("  EPL-1741 started execution");
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);					
					
			//Navigating to the last level
			logger.info("Step 2:");
			country = navigateToLevel("settlement_id");
			city = navigateToLevel("settlement_id");
			selectItem(getObject("period_id"), periodLastWk);
			getObject("search_link").click();
			shop = navigateToLevel("settlement_id");
			
			waitForElementPresent("mid_id");
			verifyElementDisp("mid_id");
			logger.info("MID tab is displayed on clicking on lowest level after selecting "+country+" country");
			
			//Select EMV AUS application
			logger.info("Step 3:");
			executeAppSearch(emvAUSApp);
			getObject("settlement_id").click();	
			verifyTabPresence("card_type_xpath");		
			
			//verify bread crumb with all application search			
			emvAUSApp = getSelectedItem(getObject("select_appli_id"));
			if(emvAUSApp.equals(valAll))
			{					
				verifyOverviewBC(beforeLevelBC+country+" » "+city+" » "+shop+afterLevelBC);
			}
			else
			{				
				verifyOverviewBC(beforeLevelBC+country+" » "+city+" » "+shop+" » "+emvAUSApp+afterLevelCardBC);
			}
			
			//Verify header columns				
			logger.info("Following columns are displayed in the Card Type table after selecting "+emvAUSApp+" for country "+country);
			waitForElementPresent("cardtype_table_xpath");
			verifyPresenceOfCols("cardtype_table_xpath", transHeadersArr);			
			logger.info("In Card Type tab : the coherency of breadcrumb is as expected and 12 columns are displayed as expected");
						
			//Verify for For each lines, Transactions = Sales + Refunds + Cancel. (Sales) + Cancel.(Refunds)
			logger.info("Step 4:");
			if((isElementPresent("journal_row_count_xpath")))			{
				
				actVal = getPageNum();			

				//Getting all the column values
				transList=get_list_of_elements("transcol_cardrows_xpath");			
				salesList=get_list_of_elements("salescol_cardrows_xpath");			
				refundsList=get_list_of_elements("refundscol_cardrows_xpath");			
				cancelList=get_list_of_elements("cancelcol_cardrows_xpath");
				cancelRefundList=get_list_of_elements("cancelrefundcol_cardrows_xpath");
				cancelCashBackList=get_list_of_elements("cancelcashbackcol_cardrows_xpath");
				netvalList=get_list_of_elements("netvalcol_cardrows_xpath");			
				saList=get_list_of_elements("sacol_cardrows_xpath");			
				raList=get_list_of_elements("racol_cardrows_xpath");			
				csaList=get_list_of_elements("csacol_cardrows_xpath");	
				craList=get_list_of_elements("cracol_cardrows_xpath");	
				ccbList=get_list_of_elements("ccbcol_cardrows_xpath");	
				
				//Verifying each record
				for(count=0;count<(Integer.valueOf(actVal).intValue());count++)
				{					
					//Verify for For each lines, Transactions = Sales + Refunds + Cancel. (Sales) + Cancel.(Refunds)
					actTrans=(Integer.valueOf(salesList.get(count).getText()).intValue())
							+(Integer.valueOf(refundsList.get(count).getText()).intValue())
							+(Integer.valueOf(cancelList.get(count).getText()).intValue())
							+(Integer.valueOf(cancelRefundList.get(count).getText()).intValue());
					Assert.assertEquals(actTrans, (Integer.valueOf(transList.get(count).getText()).intValue()));
					
					//Verify For each lines, Net value = Sales amount – (Refunds amount + Cancel. (Sales) amount - Cancel. (Refunds) amount) 
					netVal = ((netvalList.get(count).getText()).substring(4, (netvalList.get(count).getText().length()))).trim();
					netVal=netVal.replace(",", "");				
					netValue=Double.valueOf(netVal).doubleValue();
					netValue= round((netValue), 2);	
					
					saVal = ((saList.get(count).getText()).substring(4, (saList.get(count).getText().length()))).trim();
					saVal=saVal.replace(",", "");				
					saValue=Double.valueOf(saVal).doubleValue();
					saValue= round((saValue), 2);
					
					raVal = ((raList.get(count).getText()).substring(4, (raList.get(count).getText().length()))).trim();
					raVal=raVal.replace(",", "");				
					raValue=Double.valueOf(raVal).doubleValue();
					raValue= round((raValue), 2);
					
					csaVal = ((csaList.get(count).getText()).substring(4, (csaList.get(count).getText().length()))).trim();
					csaVal=csaVal.replace(",", "");				
					csaValue=Double.valueOf(csaVal).doubleValue();
					csaValue= round((csaValue), 2);
					
					craVal = ((craList.get(count).getText()).substring(4, (craList.get(count).getText().length()))).trim();
					craVal=craVal.replace(",", "");				
					craValue=Double.valueOf(craVal).doubleValue();
					craValue= round((craValue), 2);
					
					logger.info("Compared Values are " +netValue +" and "+ round((saValue-(raValue+csaValue-craValue)), 2));
					Assert.assertEquals(netValue,round(saValue-(raValue+csaValue-craValue),2));				
				}
				logger.info("For each line : Transactions = Sales + Refunds + Cancel. (Sales) + Cancel.(Refunds)");
				logger.info("For each line : Net value = Sales amount – (Refunds amount + Cancel. (Sales) amount - Cancel. (Refunds) amount) ");
			}
			else 
			{
				logger.info("There are no data to display for Application Type "+emvAUSApp+" in Card Type tab");
				Assert.fail("There are no data to display for Application Type "+emvAUSApp+" in Card Type tab");
			}	
			//Navigate to Transaction Overview
			cardPaymentSubPageNavigator(subModTrnOvr);					
			
			//Navigating to the last level for UK country
			logger.info("Step 5:");
			country = navigateToLevel("uk_country_xpath");
			city = navigateToLevel("settlement_id");
			selectItem(getObject("period_id"), periodLastWk);
			getObject("search_link").click();
			shop = navigateToLevel("settlement_id");
			
			waitForElementPresent("mid_id");
			verifyElementDisp("mid_id");			
			logger.info("Country "+country+ " is selected" );
			logger.info("MID tab is displayed on clicking on lowest level after selecting "+country+ " country");
			
			//Select EMV UK application	
			logger.info("Step 6:");
			executeAppSearch(emvUKApp);			
			getObject("settlement_id").click();
			logger.info("Step 7:");
			verifyTabPresence("card_type_xpath");		
			
			//verify bread crumb with all application search			
			emvAUSApp = getSelectedItem(getObject("select_appli_id"));
			if(emvAUSApp.equals(valAll))
			{						
				verifyOverviewBC(beforeLevelBC+country+" » "+city+" » "+shop+afterLevelBC);
			}
			else
			{
				verifyOverviewBC(beforeLevelBC+country+" » "+city+" » "+shop+" » "+emvUKApp+afterLevelCardBC);
			}
			
			//Verify header columns			
			logger.info("Following columns are displayed in the Card Type table after selecting "+emvUKApp+" for country "+country);
			verifyPresenceOfCols("cardtype_table_xpath", transHeadersUKArr);			
			logger.info("In Card Type tab : the coherency of breadcrumb is as expected and 14 columns are displayed as expected");
				
			if((isElementPresent("journal_row_count_xpath")))
			{
				//Verify for For each lines, Transactions = Sales + Refunds + Cancel. (Sales) + Cancel.(Refunds) + Cancel. (Cashback) 
				
				actVal = getPageNum();			

				//Getting all the column values
				transList=get_list_of_elements("transcol_cardrows_xpath");			
				salesList=get_list_of_elements("salescol_cardrows_xpath");			
				refundsList=get_list_of_elements("refundscol_cardrows_xpath");			
				cancelList=get_list_of_elements("cancelcol_cardrows_xpath");
				cancelRefundList=get_list_of_elements("cancelrefundcol_cardrows_xpath");
				cancelCashBackList=get_list_of_elements("cancelcashbackcol_cardrows_xpath");
				netvalList=get_list_of_elements("netvalcol_cardrows_xpath");			
				saList=get_list_of_elements("sacol_cardrows_xpath");			
				raList=get_list_of_elements("racol_cardrows_xpath");			
				csaList=get_list_of_elements("csacol_cardrows_xpath");	
				craList=get_list_of_elements("cracol_cardrows_xpath");	
				ccbList=get_list_of_elements("ccbcol_cardrows_xpath");
				
				//Verifying each record				
				for(count=0;count<(Integer.valueOf(actVal).intValue());count++)
				{	
					//Verify for For each lines, Transactions = Sales + Refunds + Cancel. (Sales) + Cancel.(Refunds) + Cancel. (Cashback)					
					actTrans=getIntegerVal(salesList.get(count))
							+getIntegerVal(refundsList.get(count))
							+getIntegerVal(cancelList.get(count))
							+getIntegerVal(cancelRefundList.get(count))
							+getIntegerVal(cancelCashBackList.get(count));
					Assert.assertEquals(actTrans, getIntegerVal(transList.get(count)));

					//Verify For each lines, Net value = Sales amount – (Refunds amount + Cancel. (Sales) amount - Cancel. (Refunds) amount +Cancel. (Cashback) amount  ) 					
					netValue = getDirectDoubleValWithoutCurr(netvalList.get(count));
					netValue= round((netValue), 2);	
					
					saValue = getDirectDoubleValWithoutCurr(saList.get(count));
					saValue= round((saValue), 2);
					
					raValue = getDirectDoubleValWithoutCurr(raList.get(count));
					raValue= round((raValue), 2);
					
					csaValue = getDirectDoubleValWithoutCurr(csaList.get(count));
					csaValue= round((csaValue), 2);
					
					craValue = getDirectDoubleValWithoutCurr(craList.get(count));
					craValue= round((craValue), 2);
					
					ccbValue = getDirectDoubleValWithoutCurr(ccbList.get(count));
					ccbValue= round((ccbValue), 2);

					logger.info("Compared Values are " +netValue +" and "+ round((saValue-(raValue+csaValue-craValue+ccbValue)), 2));					
					Assert.assertEquals(netValue,round(saValue-(raValue+csaValue-craValue+ccbValue),2));	
				}
				logger.info("For each line : Transactions = Sales + Refunds + Cancel. (Sales) + Cancel.(Refunds) + Cancel. (Cashback)");
				logger.info("For each line : Net value = Sales amount – (Refunds amount + Cancel. (Sales) amount - Cancel. (Refunds) amount +Cancel. (Cashback) amount  ) ");
			}
			else 
			{
				logger.info("There are no data to display for Application Type "+emvUKApp+" in Card Type tab");
				Assert.fail("There are no data to display for Application Type "+emvUKApp+" in Card Type tab");
			}			
			logger.info(" EPL-1741 execution successful");			
		}
		catch (Throwable t)
		{
			handleException(t);		
		}	
	}
	
}
