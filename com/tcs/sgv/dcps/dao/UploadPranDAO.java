package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface UploadPranDAO extends GenericDao {

	
	public Long getFileId() throws Exception;
	public boolean checkIfPranUploaded(String pranNo) throws Exception;
	public List getUploadedDtls() throws Exception;
	public List getUploadedReportDtls(String FileId,String Status) throws Exception;
	//jitu
	public Integer activePranNOUpdate(String pranNo) throws Exception;
	public Integer activePranNOUpdate1(ArrayList emplList);
	}
