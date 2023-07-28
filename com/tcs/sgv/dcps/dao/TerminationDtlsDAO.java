package com.tcs.sgv.dcps.dao;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface TerminationDtlsDAO extends GenericDao {

	public List checkIfEmployeeBelongsToDDO(String sevarthID ,String lStrDDOCode) throws Exception;
	public List checkIfTerminationDone(String sevarthID) throws Exception;
	public List getEmpDetails(String sevarthID,String tranId) throws Exception;
	public List getEmpPayDetails(String sevarthID) throws Exception;
	public List getDesignationList(long langId, long locId);
	public List getEmpSavedDetails(String sevarthID) throws Exception;
	public List getEmployeesForFormC(String ddoCode) throws Exception;
	public String getEmployeesPPAN(String sevarthID) throws Exception;
	public List getEmpDetailsForFormA(String sevarthID) throws Exception;
	public List getAlreadySavedData(String sevarthID) throws Exception;
	public List getEmployeesInRejected(String ddoCode) throws Exception;
	public int getLatestOrderNo(Long termId) throws Exception;
	public String getTranId(String tranId) throws Exception;
	public List getReportData(String sevarthID) throws Exception;
	}
