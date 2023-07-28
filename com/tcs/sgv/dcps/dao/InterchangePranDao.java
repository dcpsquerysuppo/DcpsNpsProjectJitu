package com.tcs.sgv.dcps.dao;


import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface InterchangePranDao extends GenericDao{
	
	List getAllEmp(String lStrSevaarthId1, String lStrSevaarthId2);
	
	List getAllFile(String lStrPranNo);
	
	List checkSevaarthIdExist(String lStrSevaarthId);
	
	String swapPranNo(String fileName, String PranNew1,String PranNew2, String remark);
	
	String swapPran(String PranNew1,String PranNew2, String remark);

}
