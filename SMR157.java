package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR157.java $
$Id: SMR157.java 16708 2016-01-20 09:53:29Z rjadhav $
 */


import org.testng.Assert;
import org.testng.annotations.Test;

import com.ingenico.common.CommonConstants;

/**
 * SMR-157:Change numcomm-pos link(2)
 * @author Joel.Samuel
 *
 */
public class SMR157 extends SuiteCardPayment{

	@Test(groups={"SMR157"})
	/**
	 * Change numcomm-pos link(2)
	 */
	public void smr157()  {
		try{
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEverest",testDataOR.get("superuser"),firstName,lastName);
			logger.info("SMR157 execution started");
			final String projectName=testDataOR.get("project_name"),posNumToChange=testDataOR.get("pos_number_to_change"),newNumCom=testDataOR.get("new_num_comm"),
					dbAxis=testDataOR.get("databaseAxis"),numptv,numcom;
			
			
			//Access Everest with a superuser,Go to cardpayment,customer 
			//provisioning and select the customer
			logger.info("Step 1,2,3:");
			vProjNameinProv(projectName);
			
			//click on the details button of a numcomm to change with associat
			//-ed POSs 
			logger.info("Step 4:");
			clkOnDirectObj("numcom_details_xpath","NUMCOMVAL",testDataOR.get("num_comm_to_change"));
			logger.info("Clicked on the details button of numcomm");
			
			//click on the change numcom-pos link,choose the proper pos number
			//if there are multiple choices
			logger.info("Step 5:");
			clkOnDirectObj("numcom_pos_tochange_xpath","POSNUM",posNumToChange);
			logger.info("Clicked on the change numcom pos link");
			checkAlert();
			
			//Select the new numcomm and click on the change buton,choose the 
			//proper numcomm if there are multiple choices
			logger.info("Step 6:");
			selUtils.selectItem(selUtils.getObject("selnumcomm_id"), newNumCom);
			logger.info("Selected '"+ newNumCom);
			selUtils.clickOnWebElement(selUtils.getObject("numcomchangebttn_css"));
			logger.info("clicked on num comm change button");
			
			//Click 'Next' button
			logger.info("Step 7:");
			selUtils.clickOnWebElement(selUtils.getObject("viewconfignext_id"));
			logger.info("clicked on next button");
			
			//Enter a 'Project Name' and click on the 'Save' button			
			logger.info("Step 8:");
			selUtils.populateInputBox("projname_id", projectName);
			selUtils.clickOnWebElement(selUtils.getObject("configsumsavebttn_xpath"));
			logger.info("Entered the projectName as "+projectName+"and clicked on save button");

			//Go to "Pending Provisioning" sub-tab		
			logger.info("Step 9:");
			selUtils.selectItem(selUtils.getCommonObject("selectcustomer_id"), testDataOR.get("customer"));
			
			//Above step is just a work around step,since we faced issue when 
			//on pending provisioning tabclicking
			clkOnPendingProv(projectName);
			
			//Click the edit button of the pending operation <project_name>			
			logger.info("Step 10:");
			clkOnDirectObj("editpospending_xpath","NAME",projectName);
			logger.info("Clicked on the edit button");

			//Deploy the request,click on prov tab			
			logger.info("Step 11:");
			clickOnDeploy();
			clckOnProvOrPosTab("provtab_xpath",PROVTAB);
			
			//click on detalis link of new numcomm 
			clkOnDirectObj("numcom_details_xpath","NUMCOMVAL",newNumCom);
			logger.info("Clicked on the details button of numcomm");
			
			//verifying the changed posnumber in the table
			colIndex=selUtils.getIndexForColHeader("colheaders_css", POSNUMBERCOL);
			verifyLvlColLvlValPresence("provPoslst_css",colIndex,posNumToChange);
			logger.info("Verified the PosNumber value  "+posNumToChange+" is displayed in the Provisioning list");
			
			if(dbCheck){
				//Validating the results in Axis database
				sqlQuery="SELECT numtpv,numcomm FROM emv.tpvemv WHERE numtpv='"+posNumToChange+"' AND numcomm='"+newNumCom+"';";
				resSet = dbMethods.getDataBaseVal(dbAxis,sqlQuery,CommonConstants.ONEMIN);
				numptv=resSet.getString("numtpv");
				numcom=resSet.getString("numcomm");
				Assert.assertTrue(numptv.equals(posNumToChange)&&numcom.equals(newNumCom), "Table data does not exist ");
			}
			
			logger.info("SMR157 execution successful");
		}
		catch (Throwable t) {
			handleException(t);
		}

	}
	
}
