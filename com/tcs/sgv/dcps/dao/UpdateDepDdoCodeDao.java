package com.tcs.sgv.dcps.dao;


import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface UpdateDepDdoCodeDao extends GenericDao{

	List getAllEmp(String lStrSevaarthId);

	List checkFormUpdate(String lStrSevaarthId);

	List checkSevaarthIdExist(String lStrSevaarthId);

	String updateDdoCode(String sevarthId, String depDdoCode);

	List checkDepDdoCode(String depDdoCode);
}
