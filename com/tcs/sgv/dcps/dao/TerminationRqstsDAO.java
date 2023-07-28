package com.tcs.sgv.dcps.dao;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface TerminationRqstsDAO extends GenericDao {

	public List getEmployees(String treasuryCode,String user) throws Exception;
	public List getEmployeeMissingCredits(String sevarthId) throws Exception;
	public List getEmployeeClosingBalance(String sevarthId) throws Exception;
	public List getEmployeeLegacyOldAmount(String sevarthId) throws Exception;
	public Long getPk(String tranId) throws Exception;
	
	
	}
