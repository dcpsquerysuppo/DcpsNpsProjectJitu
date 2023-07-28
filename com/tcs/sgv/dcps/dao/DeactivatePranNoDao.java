package com.tcs.sgv.dcps.dao;


import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface DeactivatePranNoDao extends GenericDao{
	
	List getAllEmp(String lStrSevaarthId, String lStrPranNo);
	
	List getAllFile(String lStrPranNo);
	
	List checkSevaarthIdExist(String lStrSevaarthId, String strPranNo);
	
	String deactivatePranNo(String fileName, String pranNo, String remark);
	
	String deactivatePran(String pranNo, String remark);

}
