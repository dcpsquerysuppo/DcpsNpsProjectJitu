package com.tcs.sgv.common.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import com.tcs.sgv.common.utils.DBUtility;


public class DuplicateRequestLogger {

	private File printLogFile = null;
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("ApplicationDB");

	public DuplicateRequestLogger() throws IOException {

		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

		String printLogPath = gObjRsrcBndle.getString("CMN.LOGPATH");

		String strFileName = "DuplicateRequest" + sdf.format(DBUtility.getCurrentDateFromDB());

		printLogFile = new File(printLogPath + strFileName + ".txt");

		if (!printLogFile.exists())
			printLogFile.createNewFile();
	}

	public void writeDdoLog(String txnSpecificDtls) throws IOException {

		FileWriter fWriter = new FileWriter(printLogFile, true);

		fWriter.append(System.getProperty("line.separator"));
		fWriter.append("~~~~~~~~~~~~~~~~~ EVENT START " + DBUtility.getCurrentDateFromDB() + " ~~~~~~~~~~~~~~~");
		fWriter.append(System.getProperty("line.separator"));
		fWriter.append(txnSpecificDtls);
		fWriter.append(System.getProperty("line.separator"));
		fWriter.append("~~~~~~~~~~~~~~~~~~~~~~ EVENT END ~~~~~~~~~~~~~~~~~~~~~~");

		fWriter.flush();
		fWriter.close();
	}
}