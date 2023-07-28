package com.tcs.sgv.dcps.dao;

import java.util.List;

public interface MissingCreditDAO {

	List getAllDdoCode(String LocCode) throws Exception;
	
	List getAllTreasury() throws Exception;
}