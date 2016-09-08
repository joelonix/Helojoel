package com.ingenico.testsuite.entitymanagement;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/entitymanagement/SuiteEntityManagement.java $
$Id: SuiteEntityManagement.java 14814 2015-09-07 09:23:32Z rkahreddyga $
 */

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;

import com.ingenico.base.TestBase;
/**
 *  SuiteEntityManagement:Create Entity   
 */
public class SuiteEntityManagement extends TestBase {
	
    /**
     * Declaration of Common variables
     */
	public final static String ENTITYCOLNAME="Entity Name";


	//TODO****Framework Related Functions*****

	/** 
	 * Initializes suite execution 
	 */
	@BeforeSuite(alwaysRun=true)
	void initSetUp()  {
		initialize();
	}	

	//TODO***Common functions to all EntityManagement sub modules****


	/**
	 * Method to create entity
	 * @return entityName	 
	 */
	
	public String  addEntity(){
		entityName=testDataOR.get("entity");
		entitySapCode=testDataOR.get("entity_sap");
		if(selUtils.getObject("add_entymodalwin_css").isDisplayed()){
			selUtils.populateInputBox("ad_entname_id", entityName);
			selUtils.populateInputBox("ad_sapcode_id", entitySapCode);
			selUtils.getObject("okbtn_xpath").click();
			if(selUtils.getCommonObject("posheder_errmsg_id").getAttribute("class").endsWith("errorMessage"))
			{
				Assert.fail("Problem with entity creation");	
				logger.info("Problem with entity creation");
			}				
			logger.info("Entity with name '"+entityName+"' successfully added");
		}
		return entityName;
	}

}
