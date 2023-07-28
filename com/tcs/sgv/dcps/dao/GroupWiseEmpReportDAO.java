package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface GroupWiseEmpReportDAO extends GenericDao
{
	List getEmpCountForYear(String lStrFinYear)throws Exception;
}
