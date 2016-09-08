package com.ingenico.testsuite.tmsmanagement;

/*
 $HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/tmsmanagement/SMR879.java $
 $Id: SMR879.java 18101 2016-04-18 09:41:42Z haripraks $
 */

import java.sql.SQLException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;

/**
 * SMR-879:Create multiple terminals
 * @author Raghunath K
 *
 */
public class SMR879 extends SuiteTmsManagement {

	/**
	 * Create multiple terminals
	 */
	@Test(groups = "SMR879")
	public void smr879() {

		try {
			eportalCust = testDataOR.get("customer");
			final String firstName = testDataOR.get("superuser_first_name"), lastName = testDataOR.get("superuser_last_name"),
			dbIngEstate=testDataOR.get("databaseIngEstate"),subestsignature = testDataOR.get("subestate_signature"),
			estateName = testDataOR.get("subestate_name"),
			trmnalModel = testDataOR.get("terminal_model");
			
			final String firstSerialNum = testDataOR.get("first_serial_number"),trmnalTech = testDataOR.get("technology"),
			lastSerialNum = testDataOR.get("last_serial_number"),trmnalPartNum= testDataOR.get("terminal_part_number");
			
			long sLen,sNumInc,fSNum=Long.valueOf(firstSerialNum),lSNum=Long.valueOf(lastSerialNum);
			String sigTemp,tempSeril,terPartNum,serilPart;
			
			
			logger.info("SMR879 execution started");

			// Access Eportal with a superuser
			// Go to "TMS" and then click on terminals
			logger.info("Step 1");
			login("URLEportal", testDataOR.get("superuser"), firstName,lastName);
			navigateSubMenu(SNAPSHOT, "tms_snapshot_xpath", "terminals_link");
			logger.info("Clicked on terminals link");

			// click on add terminals
			logger.info("Step 2");
			waitMethods.waitForWebElementPresent(selUtils.getObject("estatebtnxhtml_xpath"));
			waitNSec(3);
			selUtils.clickOnWebElement(selUtils.getObject("addterminalsbtnId_id"));
			logger.info("Clicked on add terminals ");


			// Select Root estate and then   estate details
			//Click on confirm
			logger.info("Step 3");
			addTerminals(subestsignature,trmnalTech,trmnalModel,firstSerialNum,lastSerialNum,trmnalPartNum);
			
			//Search estate Signature
			//Access the Ingestate database and look for a row
			logger.info("Step 4 and 5");			
			findNSrchSign(subestsignature);
			
			//Verify terminals first and last serial numbers concatanation of Automated terminal part number
			//first terminal part number 8 digits and last Serial Number 8 digits for TETRA and TELIUM terminals
						
			sLen=lSNum-fSNum;
			if(trmnalTech.equalsIgnoreCase(TELIUM) | trmnalTech.equalsIgnoreCase(TETRA)){
				terPartNum=trmnalPartNum.substring(0,8);	
				for(int sLop=0;sLop<=sLen;sLop++){	
					sNumInc=fSNum+sLop;
					tempSeril=Long.toString(sNumInc);
					serilPart=tempSeril.substring(tempSeril.length()-8, tempSeril.length());
					sigTemp=terPartNum.concat(serilPart);
					vSingleTblValue("terminalSignature_xpath",SIGNATUR,sigTemp);
					vDbTerminalSign(dbIngEstate,sigTemp,estateName);
				}
			}
			else
				Assert.fail("Invalid technology has selected");
			
			logger.info("SMR879 executed successfully");
		} catch (Throwable t) {
			handleException(t);
		}
	}
	
	
	
	/**
	 * Verify database for every terminal signature 
	 * should have one row in terminal table
	 * @param dbIngEstate
	 * @param termnalSignr
	 * @param rootEstate
	 */
	
	private void vDbTerminalSign(String dbIngEstate,String termnalSignr, String rootEstate) throws SQLException {
		if(dbCheck){
			//Verify created estate name correctly updated in database
			sqlQuery="SELECT * FROM terminal a INNER JOIN terminal b ON a.parent_id = b.terminal_id " 
			+ "WHERE a.signature ='"+termnalSignr+"' AND b.name LIKE '%"+rootEstate+"' ;";
			resSet = dbMethods.getDataBaseVal(dbIngEstate,sqlQuery,CommonConstants.ONEMIN);					
			
			//Retrieve row size
			if(resSet.getRow()==1)
			{
				logger.info("Verified the values of estate name correctly inserted into the 'databaseIngEstate' database");
				logger.info("Retrieved Only one row Sucessfully ");
			}
			else
				Assert.fail("Failed due to row size is :"+resSet.getRow());
		  }
		}

	/**
	 * Enter terminal details and click on confirm
	 * @param estSignature
	 * @param terminalTech
	 * @param terminalsModel
	 * @param fSerialNum
	 * @param lSerialNum
	 * @param terminalPartNum
	 */
	private void addTerminals(String estSignature,String terminalTech,String terminalsModel,String fSerialNum,String lSerialNum,String terminalPartNum)
	{	
		waitMethods.waitForWebElementPresent(selUtils.getObject("addterminalwindowwait_xpath"));
		if(getModWinDisp(selUtils.getObject("mdialogboxaddterminals_xpath"),ADDTERMINALS))
		{
			options=selUtils.getObjects("terminalestate_xpath");
			selUtils.selectValueInDropDown(options, estSignature);
			logger.info("Selected estate signature");
			selUtils.selectItem(selUtils.getObject("terminalstechnology_id"),terminalTech);
			logger.info("Selected technology");
			selUtils.selectItem(selUtils.getObject("terminalsmodel_id"),terminalsModel);
			logger.info("Selected model");
			selUtils.clickOnWebElement(selUtils.getObject("terminalserialNum_id"));
			selUtils.populateInputBox("terminalserialNum_id", fSerialNum);
			logger.info("Entered serial number");
			selUtils.clickOnWebElement(selUtils.getObject("terminalserialNumto_id"));
			selUtils.populateInputBox("terminalserialNumto_id", lSerialNum);		
			logger.info("Entered through number");
			selUtils.clickOnWebElement(selUtils.getObject("terminalpartnumber_id"));
			selUtils.populateInputBox("terminalpartnumber_id", terminalPartNum);		
			logger.info("Entered partnumber number");
			selUtils.clickOnWebElement(selUtils.getObject("terminalconfirm_id"));
			logger.info("Clicked on confirm button");
			monitorErrMsgDisp("terminalpopuperror_id");
			vExpValPresent("terminalpopupresult_id", ADDTERMINALSSUCCESS);			
			selUtils.clickOnWebElement(selUtils.getObject("addanterminalclose_xpath"));
		}
		else{
			Assert.fail(ADDTERMINALS+" Model DialogBox has not present due to fail");
		}
	}
	
	
}
