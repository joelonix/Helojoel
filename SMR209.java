package com.ingenico.testsuite.cardpayment;
/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/testsuite/cardpayment/SMR209.java $
$Id: SMR209.java 18228 2016-04-25 13:12:28Z haripraks $
 */
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ingenico.common.CommonConstants;

/**
 *  SMR-209:VISA Direct sale
 * @author Hariprasad
 */
public class SMR209 extends SuiteCardPayment{

	/**
	 * VISA Direct sale
	 */
	@Test(groups={"SMR209"})
	public void smr209()  {
		try{

			final String numComm=testDataOR.get("num_comm"),posNum=testDataOR.get("pos_number"),amount=testDataOR.get("amount"), curDate=getCurrentdate(),
					appendedNumComm=appendValToStr(numComm, "3"),axisDatabase=testDataOR.get("databaseAxis"),oneyearDatabase=testDataOR.get("databaseOneYear"),vmTools=testDataOR.get("VMTools"),
					axisHost=testDataOR.get("axisHost"),axisPort=testDataOR.get("axisPort"),autoUser=testDataOR.get("autouser"),
					noe2eFile=CommonConstants.C3PO+"indic_emv_noe2e.xml",e2eFile=CommonConstants.C3PO+"ref_emv_e2e.xml";

			//get client id from database
			//			sqlQuery="select id_client from client where client_name='"+customer+"';";
			//			resSet=getDataBaseVal(eportalDatabase,sqlQuery,CommonConstants.ONEMIN);

			//get date and timein minSec format from current date
			String date=curDate.substring(0, 8),minNsec=curDate.substring(8, curDate.length()),path,dateInFormate,amountInFormate,amtInappFormat,command,amtInFullLength;
			boolean databaseStatus=false;

			//Framing data to modify xml file
			amtInFullLength=modifyAmountLenth(amount);
			final String[] xmlAttrVals={"Comm","Term","Montant","DateTrnsCAM","HeureTrnsCAM"}, newAttrVals={appendedNumComm,posNum,amtInFullLength,date,minNsec};

			logger.info("SMR209 execution started");
			//Prepare a simulator to perform a chip sale with VISA card
			logger.info("Step 1:");
			logger.info("Prepare a simulator to perform a chip sale transaction");
			logger.info("C3 Simulator should be ready in host machine "+vmTools);

			//Modifying xml File
			logger.info("Step 2:");
			logger.info("Execute the chip sale transaction and validate in Axis database");
			readXmlNEdit(xmlAttrVals,newAttrVals,noe2eFile);
			logger.info("Prepared XML files for the chip sale transaction");

			//SSH command executer
			command="c3_simulator -l "+axisHost+" -p "+axisPort+" -s indic_emv_noe2e.xml -r ref_emv_e2e.xml -v DEBUG";
			fileCopyNmove(vmTools, autoUser,noe2eFile,e2eFile);
			sshCommandExecuter(vmTools, autoUser, command);
			logger.info("Executed the c3_simulator command inside the directory of two xml files ");
			dateInFormate=getdateIndiffForm(curDate);
			amountInFormate=getAmountInFormat(amtInFullLength);

			if(dbCheck){
				// verify data in the emv.mvtemv table in axis Database
				logger.info("Formated amount in montant format is "+amountInFormate);
				sqlQuery="SELECT * FROM emv.mvtemv WHERE montant='"+amountInFormate+"' AND numcomm='"+numComm+"' AND numtpv='"+posNum+"' AND datetimec3='"+dateInFormate+"';";
				resSet = dbMethods.getDataBaseVal(axisDatabase,sqlQuery,CommonConstants.ONEMIN);
				databaseStatus=resSet.getObject("montant").toString().equals(amountInFormate)&&resSet.getString("numcomm").equals(numComm)&&resSet.getString("numtpv").equals(posNum)&&resSet.getString("datetimec3").equals(dateInFormate);
				Assert.assertTrue(databaseStatus,"Chip sale transaction data with axis Database is not replicated properly");
				logger.info("Validated Chip sale transaction data with axis Database");

				// verify data in the emv.full_mvtemv table in one year Database
				//Wait for maximum 2 minutes for Axis replication
				logger.info("Step 3:");
				logger.info("Validate Chip sale transaction in one year database");
				logger.info("Wait for maximum 2 minutes for Axis replication");
				databaseStatus=false;
				sqlQuery="SELECT * FROM emv.full_mvtemv WHERE montant='"+amountInFormate+"' AND numcomm='"+numComm+"' AND numtpv='"+posNum+"' AND datetimec3='"+dateInFormate+"';";
				resSet = dbMethods.getDataBaseVal(oneyearDatabase,sqlQuery,CommonConstants.TWOMIN);
				databaseStatus=resSet.getObject("montant").toString().equals(amountInFormate)&&resSet.getString("numcomm").equals(numComm)&&resSet.getString("numtpv").equals(posNum)&&resSet.getString("datetimec3").equals(dateInFormate);
				Assert.assertTrue(databaseStatus,"Chip sale transaction data with one year Database is not replicated properly");
				logger.info("Validated Chip sale transaction data with one year Database");
			}

			//Go to instore payment transaction journal sub page
			logger.info("Step 4 and 5:");
			logger.info("Access e portal with super user");

			eportalCust=testDataOR.get("customer");
			final String firstName=testDataOR.get("superuser_first_name"),lastName=testDataOR.get("superuser_last_name");
			login("URLEportal",testDataOR.get("superuser"),firstName,lastName);

			navigateToSubPage(JORNAL,selUtils.getCommonObject("instorepay_tab_xpath"),selUtils.getCommonObject("inStorepaymenjor_xpath"));
			//selUtils.slctChkBoxOrRadio(selUtils.getCommonObject("servertime_id"));

			//validate transaction data from application
			amtInappFormat=getAmtInAppFormat(amountInFormate);
			logger.info("Formatted Amount "+amtInappFormat);
			path=getPath("transjournaltab_xpath").replace("DATE", dateInFormate).replace("AMOUNT", amtInappFormat).replace("NUMCOMM", numComm).replace("POS", posNum);

			selUtils.clickOnWebElement(selUtils.getObject("servertime_xpath"));
			logger.info("selected Server Time radio button");
			waitNSec(2);
			selUtils.clickOnWebElement(selUtils.getCommonObject("search_id"));
			logger.info("clicked on Search button");
			waitMethods.waitForelementNotdisplayed(selUtils.getCommonObject("search_id"));
			vRowDataInTable(path, "transaction data");

			logger.info("SMR209 is successfully executed");
		}
		catch (Throwable t) {
			t.printStackTrace();
			handleException(t);
		}

	}

	/**
	 * Method for reading and editing the values of XML file
	 * @param attrVals
	 * @param attrNewVals
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */

	private void readXmlNEdit(String[] attrVals,String[] attrNewVals,String noe2eFile) throws IOException, ParserConfigurationException, SAXException{
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(noe2eFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("field");
			for (int i= 0; i< nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					for(int j=0;j<attrVals.length;j++){
						if(eElement.getAttribute("name").equals(attrVals[j]) && eElement.hasAttribute("value")){
							eElement.setAttribute("value", attrNewVals[j]);
						}
						else if(eElement.getAttribute("name").equals(attrVals[j]) && eElement.hasAttribute("valuehex")){
							eElement.setAttribute("valuehex", attrNewVals[j]);
						}
					}
				}
			}
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(noe2eFile));
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Failed while editing the values of XML file ");
		}
	}

	/**
	 * Modified amount value in 12 digit format
	 * @param amount
	 * @return
	 */
	private String modifyAmountLenth(String amount)
	{
		String temp=amount;
		if (amount.length() < 12) {
			for (int i = 0; i < 12-amount.length(); i++) {
				temp = "0" + temp;
			}
		}
		return temp;
	}

}

