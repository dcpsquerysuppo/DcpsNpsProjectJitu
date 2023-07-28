package com.tcs.sgv.gpf.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * @author 397138
 * 
 */
public interface GPFArrearsManualEntryDAO extends GenericDao {
	public List getMonths();

	public List getYears();

	public List getEmpArrearsDtls(String[] lStrArrEmpGroup, String lStrDdoCode);

	public List getEmpDtls(String lStrSevaarthID, String lStrDdoCode);

	public Long getPkValue(String lStrGpfAccNo, Long lLngYearId);

	Double getPreviousCloseBal(String lStrGpfAccNo, Integer lIntInstNo);

	List getNextInstallmentNo(String lStrGpfAccNo);
}
