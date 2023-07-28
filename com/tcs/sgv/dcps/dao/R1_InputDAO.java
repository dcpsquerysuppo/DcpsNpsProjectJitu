package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface R1_InputDAO extends GenericDao
{
	List getAllDdoCode(String LocCode) throws Exception;
	
	List getAllTreasury() throws Exception;
}
