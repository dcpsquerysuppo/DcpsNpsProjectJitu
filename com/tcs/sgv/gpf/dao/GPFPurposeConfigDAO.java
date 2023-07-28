package com.tcs.sgv.gpf.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface GPFPurposeConfigDAO extends GenericDao
{
	List getAllRAPurpose()throws Exception;
	
	List getAllNRAPurpose()throws Exception;
	
	void insertCmnLookupMst(String lStrLookupName, Long lLngParentLookUpId, Long lLngCrtdUsrId, Long lLngCrtdPostId)throws Exception;
	
	void updatePurposeCategory(String[] lArrLookUpName, String[] lArrLookupId)throws Exception;
}
