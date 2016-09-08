package com.ingenico.base;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/base/SelUtils.java $
$Id: SelUtils.java 16128 2015-12-09 10:38:34Z haripraks $
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.testng.Assert;

/*
 * This Class Contains Methods Related to DataBase
 */
public class DataBaseMethods {
	
	public Connection connection;
	public Statement statement;
	private ResultSet resSet;
	
	public Map<String,String> testDBdata=null;
	
	public int iter;
	
	/**
	 * Data base connection
	 * @param dbURL
	 * @param dbUName
	 * @param dbPswd
	 * @return
	 */
	public Connection dbConnection(String dbURL,String dbUName,String dbPswd)
	{
		try {
			return connection = DriverManager.getConnection(dbURL, dbUName,dbPswd);
		} catch (SQLException e) {
			Assert.fail("Problem with db connection "+e.getMessage());
			e.printStackTrace();
		}
		return connection;
	}

	/**
	 * Executes SQL query on Data base and returns Resultset
	 * 
	 * @param urldb
	 * @param sql
	 * @param maxWaitTime
	 * @return Resultset
	 */
	public  ResultSet getDataBaseVal(String urldb,String sql,int maxWaitTime)  {  
		testDBdata= TestBase.testDataOR;
		resSet=null;
		connection=dbConnection(urldb, testDBdata.get("dbuser"),testDBdata.get("dbpassword"));
		try {
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			for(iter=0;iter<maxWaitTime;iter++)
			{ 
				try{
					resSet=statement.executeQuery(sql);
					if(resSet.next())   { 
						break;
					} else {
						TestBase.waitNSec(1);
					}
				} catch(SQLException e){
					TestBase.waitNSec(1);
				}
			}
			if(resSet==null)
			{
				Assert.fail("Expected data does not exist in the database");				

			}
			//statement.close();
			connection.close();
		} catch (SQLException e)
		{		
			Assert.fail("Problem while creating the statement"+e.getMessage());
			e.printStackTrace();
		}

		return resSet;
	}


}
