package com.tcs.sgv.pensionpay.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.ResourceBundle;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;


public class MonthlyLogger
{
	private File printLogFile = null;
	private Map inputMap = null;
	private ResourceBundle gObjRBndle = ResourceBundle.getBundle("log4j");
	
	public MonthlyLogger(Map inputMap) throws IOException
	{
		
		this.inputMap = inputMap;
		
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		
		String printLogPath = gObjRBndle.getString("logpath");
		
		String strFileName = "Monthlylog" + sdf.format(DBUtility.getCurrentDateFromDB());
		
		
		try
		{
			printLogFile = new File(printLogPath + strFileName+".txt");
			
			if(!printLogFile.exists())
				printLogFile.createNewFile();
		}
		catch (Exception e) 
		{
			
		}
		
		
	}
	
	public void writeMonthlyLog(String strLog) throws IOException{
		
		FileWriter fWriter = new FileWriter(printLogFile, true);
		
		fWriter.append(System.getProperty("line.separator"));
		fWriter.append("Monthly Bug  ================== Date :" + DBUtility.getCurrentDateFromDB() + "==============");
		fWriter.append(System.getProperty("line.separator"));
		fWriter.append("POST ID : " + SessionHelper.getPostId(inputMap));
		fWriter.append(System.getProperty("line.separator"));
		fWriter.append("USER ID : " + SessionHelper.getUserName(inputMap));
		fWriter.append(System.getProperty("line.separator"));
		fWriter.append(strLog);
		fWriter.append(System.getProperty("line.separator"));
		fWriter.append("Monthly Bug END ==================");
		
		fWriter.flush();
		fWriter.close();
		
		
	}
}
