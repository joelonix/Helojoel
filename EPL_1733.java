package com.ingenico.eportal.testsuite.cardPayment;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EPL_1733 extends SuiteCardPayment
{	
	
	private String country, city, shop, actVal;
	private String[] transHeadersArr={tabCashier, colTransaction, colNetVal, colSale, colSaleAmt, colRefunds, colRefundAmt, colCanSales, csCanSaleAmtRow};
	private List<WebElement>transList, salesList, refundsList, cancelList, cancelRefList, netvalList, saList, raList, csaList, craList;	
	private int count, actTrans;
	private double netValue, saValue, raValue, csaValue, craValue; 
	
	

	/**
	 * EPL-1733 Check content of main view / POS view / all applications / a user which customer has cashier display provisioned in Everest
	 * 
	 * @throws IOException
	 */
	@Test
	public void epl_1733()
	{		
		try{
			logger.info("  EPL-1733 started execution");
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
			verifyElementDisp("mid_xpath");	
			
			logger.info("Step 4:");
			getObject("cashier_xpath").click();
			
			verifyOverviewCashierBC(country, city, shop, afterLevelCardBC, allPosBC);		
			
			//Verify header columns	
			verifyPresenceOfCols("cash_table_xpath", transHeadersArr);		
			logger.info("In Cashier tab : the coherency of breadcrumb is as expected and 9 columns are displayed as expected");
			
			//Verify for For each lines, Transactions = Sales + Refunds + Cancellations
			actVal = getPageNum();					
			
			//Getting all the column values
			verifyTransNetValue();					
			logger.info(" EPL-1733 execution successful");			
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
		transList=get_list_of_elements("transcol_cashrows_xpath");			
		salesList=get_list_of_elements("salescol_cashrows_xpath");			
		refundsList=get_list_of_elements("refundscol_cashrows_xpath");			
		cancelList=get_list_of_elements("cancelcol_cashrows_xpath");
		cancelRefList=get_list_of_elements("cancelrefcol_cashrows_xpath");
		
		netvalList=get_list_of_elements("netvalcol_cashrows_xpath");			
		saList=get_list_of_elements("sacol_cashrows_xpath");			
		raList=get_list_of_elements("racol_cashrows_xpath");			
		csaList=get_list_of_elements("csacol_cashrows_xpath");	
		craList=get_list_of_elements("cracol_cashrows_xpath");	
		
		//Verifying each record
		for(count=0;count<(Integer.valueOf(actVal).intValue());count++)
		{	
			//Verify for For each lines, Transactions = Sales + Refunds + Cancellations			
			actTrans=getIntegerVal(salesList.get(count))
					+getIntegerVal(refundsList.get(count))
					+getIntegerVal(cancelList.get(count))
					+getIntegerVal(cancelRefList.get(count));
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
			
			logger.info("Compared Values are " +netValue +" and "+ round((saValue-(raValue+csaValue)+craValue), 2));
			Assert.assertEquals(netValue,round((saValue-(raValue+csaValue)+craValue), 2));				
		}
		logger.info("For each line : Transactions = Sales + Refunds + Cancellations");
		logger.info("For each line : Net value = Sales amount – (Refunds amount + Cancellations amount)");	
	}
}
