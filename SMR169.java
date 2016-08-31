package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR169.java $
$Id: SMR169.java 16708 2016-01-20 09:53:29Z rjadhav $
 */

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;
/**
 * SMR-169:Move all the POSs  associated to a given numcomm  to a new numcomm 
 *  @author Nagaveni.Guttula
 */
public class SMR169  extends SuiteCardPayment{

	@Test(groups={"SMR169"})
	/**
	 * Change numcomm-pos link(1)
	 */
	public void smr169() {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR169 execution started");
			final String changeNumComm=testDataOR.get("num_comm_to_change"),
					newNumcom=testDataOR.get("new_num_comm"),dbAxis=testDataOR.get("databaseAxis");
			projectName=testDataOR.get("project_name");
			final List<String> posNums = new ArrayList<String>(),assPoses = new ArrayList<String>(), allColData;

			//Access Everest,Go to Card Payment-Customer Provisioning and,
		    //select the customer ,Go to "Provisioning 
			logger.info("Step 1,2,3:");
			vProjNameinProv(projectName);

			//Click the "change numcomm-pos link" button of a numcomm num_comm_to_change			 
			logger.info("Step 4:");
			clkOnDirectObj("editnumcomposlnk_xpath", "NUMCOMM", changeNumComm);
			logger.info("Cliked on the change numcommposlink");
			checkAlert();

			// Select new_num_comm and click on Change button
			logger.info("Step 5:");
			if(dbCheck){
				sqlQuery="SELECT numtpv from emv.tpvemv WHERE numcomm ='"+changeNumComm+"';";
				resSet = dbMethods.getDataBaseVal(testDataOR.get("databaseAxis"),sqlQuery,CommonConstants.ONEMIN);
				resSet.previous();
				while(resSet.next()){
					posNum=	resSet.getString("numtpv");
					posNums.add(posNum);
				}
			}
			Assert.assertTrue(getModWinDisp(selUtils.getObject("selnumcompupwin_xpath"), SELECTNUMCOM),"Modal window is not displayed");
			selUtils.selectItem(selUtils.getObject("selnumcomm_id"), newNumcom.trim());
			logger.info("Selected the numcomm as "+ newNumcom);
			selUtils.clickOnWebElement(selUtils.getObject("numcomchangebttn_css"));

			//Click 'Next' button			
			logger.info("Step 6:");
			selUtils.clickOnWebElement(selUtils.getObject("viewconfignext_id"));
			logger.info("Clicked next button of view config window");

			//Enter a 'Project Name' project_name   click on the 'Save' button			
			logger.info("Step 7:");
			selUtils.populateInputBox("projname_id", projectName);
			selUtils.clickOnWebElement(selUtils.getObject("configsumsavebttn_xpath"));
			logger.info("Entered the projectName as "+projectName+ "and clicked on the Save button");

			//Go to "Pending Provisioning" sub-tab			
			logger.info("Step 8:");
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), testDataOR.get("customer"));
			/*
			 * Above step is just a work around step,since we faced issue when 
			 * on pending provisioning tabclicking
			 */
			clkOnPendingProv(projectName);

			//Click the edit button of the pending operation project_name		
			logger.info("Step 9:");
			clkOnDirectObj("editpospending_xpath","NAME",projectName);
			logger.info("Clicked on the edit button in Pending Provisioning Tab for the project");

			//Click on the 'Deploy'and'OK' button to confirm the deployment request			
			logger.info("Step 10:");
			clickOnDeploy();
			clckOnProvOrPosTab("provtab_xpath",PROVTAB);
			clkOnDirectObj("numcommdetls_xpath", "NUMCOMM", newNumcom);
			allColData=getColData("colheaders_css",POSNUMBERCOL,"posallcol_xpath");
			Assert.assertTrue(allColData.containsAll(posNums),"All PosNumbers are not associated with "+newNumcom);
			logger.info("Verified that all the Poses are associated with "+newNumcom);
			
			if(dbCheck){
				//Verifying the data in the axisdb
				sqlQuery="SELECT numtpv from emv.tpvemv WHERE numcomm ='"+newNumcom+"';";
				resSet = dbMethods.getDataBaseVal(dbAxis,sqlQuery,CommonConstants.ONEMIN);
				resSet.previous();
				while(resSet.next()){
					posNum=	resSet.getString("numtpv");
					assPoses.add(posNum);
				}
				Assert.assertTrue(assPoses.containsAll(posNums),"All PosNumbers are not associated with "+newNumcom);
				logger.info("Verified the data in axisdb all the Poses are associated with "+newNumcom);
			}
			logger.info("SMR169 executed successfully");
		}
		catch (Throwable t) {
			handleException(t);
		}
	}
}
