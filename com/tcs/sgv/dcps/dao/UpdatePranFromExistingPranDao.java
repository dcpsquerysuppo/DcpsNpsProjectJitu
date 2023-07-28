package com.tcs.sgv.dcps.dao;


import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface UpdatePranFromExistingPranDao extends GenericDao{
	
	List getAllEmp(String lStrSevaarthId, String lStrPranNo);
	
	List getAllFile(String lStrPranNo);
	
	List checkSevaarthIdExist(String lStrSevaarthId, String strPranNo);
	
	String deactivatePranNo(String fileName, String pranNo, String remark,String newPranNo);
	
	String deactivatePran(String pranNo, String remark,String newPranNo);
	
	List testPranNO(String pranno);
	
	List testPranNoForOld(String pranno);

	void insertPranDetails(Long lLngPkIdForPran,String empName,String dcpsId,String sevarthId,String OldpranNo,
			String newPranNo,String remarks,String ddoCode,String treasuryName);
}
