package com.ingenico.eportal.testsuite.cardPayment;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1732 extends SuiteCardPayment
{	
	
	private String country, posVal, city, shop, appType, actVal;
	private String[] transHeadersArr={tabPos, colTransaction, colNetVal, colSale, colSaleAmt, colRefunds, colRefundAmt, colCanSales, csCanSaleAmtRow};
	private List<WebElement> transList, salesList, refundsList, cancelList, crList, netvalList, saList, raList, csaList, craList;
	
	private int count, actTrans;
	private double netValue, saValue, raValue, csaValue, craValue;
		
	

	/**
	 * EPL-1732 Check content of main view / POS view / all applications / a user which customer has cashier display provisioned in Everest
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1732()
	{		
		try{
			logger.info("  EPL-1732 started execution");	
			login(CONFIG.getProperty("superuser1"),CONFIG.getProperty("superuserPassword"));
			logger.info("Step 1:");
			cardPaymentSubPageNavigator(subModTrnOvr);
			
			logger.info("Step 2:");
			getObject("search_link").click();
			
			//Navigating to POS tab
			logger.info("Step 3:");
			country = navigateToLevel("settlement_id");
			city = navigateToLevel("settlement_id");
			shop = navigateToLevel("settlement_id");			
		
			logger.info("Step 4:");
			getObject("pos_xpath").click();
			verifyOverviewPOSBC(country, city, shop, afterLevelCardBC);
			
			//Verify header columns	
			verifyPresenceOfCols("pos_table_xpath", transHeadersArr);			
			logger.info("In POS tab : the coherency of breadcrumb is as expected and 9 columns are displayed as expected");
			
			//Verify list of columns present in icon below the table
			getObject("column_icon_xpath").click();	
			verifyPresenceOfCols("columns_xpath", transHeadersArr, 1);	
			
			//Verify for For each lines, Transactions = Sales + Refunds + Cancellations
			actVal = getPageNum();
			
			//Getting all the column values
			verifyTransNetValue();		
			
			logger.info("Step 5:");
				country = getSelectedItem(getObject("country_id"));
				city = getSelectedItem(getObject("city_id"));
				shop = getSelectedItem(getObject("shop_id"));
				posVal = getObject("settlement_id").getText();
				getObject("settlement_id").click();
				verifyElementDisp("cashier_xpath");
				verifyOverviewCashierBC(country, city, shop, afterLevelCardBC, posVal);					
				
				//verify bread crumb in Cashier page
				appType = getSelectedItem(getObject("select_appli_id"));			
				Assert.assertEquals(getSelectedItem(getObject("country_id")), country);
				Assert.assertEquals(getSelectedItem(getObject("city_id")), city);
				Assert.assertEquals(getSelectedItem(getObject("shop_id")), shop);			
				getObject("breadcrumb_level2_xpath").click();
				getObject("settlement_id").click();
				getObject("pos_xpath").click();	
				Assert.assertEquals(getSelectedItem(getObject("select_appli_id")), appType);	
				logger.info("POS value is selectable and after clicking on it, directs to Cashier page and coherency of breadcrumb is as expected ");
				logger.info("Coherency of Filters is as expected");
				logger.info(" EPL-1732 execution successful");			
		}
		catch (Throwable t)
		{
			handleException(t);	
		}	
	}
	
	
	/**
	 * verifies Transaction and amount values
	 */
	private void verifyTransNetValue()
	{			
		transList=get_list_of_elements("transcol_rows_xpath");			
		salesList=get_list_of_elements("salescol_rows_xpath");			
		refundsList=get_list_of_elements("refundscol_rows_xpath");			
		cancelList=get_list_of_elements("cancelcol_rows_xpath");
		crList=get_list_of_elements("cancelrefcol_rows_xpath");
		
		netvalList=get_list_of_elements("netvalcol_rows_xpath");			
		saList=get_list_of_elements("sacol_rows_xpath");			
		raList=get_list_of_elements("racol_rows_xpath");			
		csaList=get_list_of_elements("csacol_rows_xpath");	
		craList=get_list_of_elements("cracol_rows_xpath");	
		
		//Verifying each record		
		for(count=0;count<(Integer.valueOf(actVal).intValue());count++)
		{	
			//Verify for For each lines, Transactions = Sales + Refunds + Cancel. (Sales) + Cancel (Refunds)
			actTrans=getIntegerVal(salesList.get(count))
					+getIntegerVal(refundsList.get(count))
					+getIntegerVal(cancelList.get(count))
					+getIntegerVal(crList.get(count));			
			Assert.assertEquals(actTrans, getIntegerVal(transList.get(count)));
			
			//Verify For each lines, Net value = Sales amount – (Refunds amount + Cancellations amount)			
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
			
			logger.info("Compared Values are " +netValue +" and "+ round((saValue-(raValue+csaValue-craValue)), 2));
			Assert.assertEquals(netValue,round((saValue-(raValue+csaValue-craValue)), 2));				
		}
		logger.info("For each line : Transactions = Sales + Refunds + Cancellations");
		logger.info("For each line : Net value = Sales amount – (Refunds amount + Cancellations amount)");
	}
}
