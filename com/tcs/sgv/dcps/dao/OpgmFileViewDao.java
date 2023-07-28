package com.tcs.sgv.dcps.dao;

import java.util.List;

public interface OpgmFileViewDao {

	/* method transfer to formS1dao
	 String generateOpgmFile(String gStrLocationCode, int i);*/

//	List getOpgmFileList(String gStrLocationCode, String month, String year);
	String getOpgmFileMonth(String Treasury, String month, String year, String SevaarthID, String EmpName);
	List getOpgmFileList(String Treasury, String month, String year, String SevaarthID, String EmpName);
	void deleteOpgmFile(String treasury, String fileNo);

	void downloadOpgmFile(String fileNo);

	List getFinyear();

	List getMonths();
	
}
