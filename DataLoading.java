package com.ingenico.base;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/base/DataLoading.java $
$Id: DataLoading.java 17310 2016-02-29 09:53:34Z haripraks $
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.ho.yaml.Yaml;
import org.rythmengine.Rythm;

import com.ingenico.common.CommonConstants;

/**
 * DataLoading : Is used to create the testdata_rythm.yaml file and
 * create the Profile Description.xml files,by rendering the data
 */
public abstract class DataLoading {
    /**
     * Declaration of class variables 
     */
	private static Map<String, Object> params;
	
 /**
  * Method for creating the the testdata_rythm.yaml 
  * file and Profile Description.xml files
  */
	public static void main(String[] args) throws IOException
	{
		String jenkinsfolderID = args[0];
		//CSV data files preparation for execution
		String[] inputFiles={"activationRequest.csv","activationConfirmation.csv","import_sim.csv","numcomm.csv","pos.csv","posnumcomm.csv","postonumcomm.csv"};
		File file;
		FileWriter fwr;
		String result;
		
		//Reading the input file testdata.yaml file
		params=(Map<String, Object>) Yaml.load(new File(CommonConstants.INPUTYAMLFILE));
		{
			file=new File(CommonConstants.TESTDATAFILEPATH+ jenkinsfolderID + ".yml");
			file.createNewFile();
			fwr=new FileWriter(file);
			result=Rythm.render(CommonConstants.INPUTYAMLFILE,params);
			fwr.write(result);
			fwr.close();
		}
		
		for(String filename:inputFiles)
		{
			if(new File(CommonConstants.INPUTFILEPATH+filename).exists())
			{
				file=new File(CommonConstants.fileUploadpath+filename);
				file.createNewFile();
				fwr=new FileWriter(file);
				result=Rythm.render(CommonConstants.INPUTFILEPATH+filename,params);
				fwr.write(result);
				fwr.close();
			}
			else{
				System.out.println("NO FILE "+CommonConstants.INPUTFILEPATH+filename);
			}
		}
	}

}
