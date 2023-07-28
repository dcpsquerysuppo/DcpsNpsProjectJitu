package com.tcs.sgv.dcps.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ajaxtags.helpers.ValueItem;


import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;


public interface FormS2DAO extends GenericDao{

	List getEmpNameForS1AutoComplete(String upperCase, String strDDOCode);

	String checkSevaarthIdExist(String txtSevaarthId, String strDDOCode);
	
	List getSectionADetailsNew(String sevaarthId) throws Exception;

	List getSectionBDetails(String sevaarthId, String isDeputation) throws Exception;
	
	List getSectionCDetailsNew(String sevaarthId)throws Exception;
	
	List getDTORegNo(String sevaarthId) throws Exception;

	List checkNmnCount(String sevaarthId) throws Exception;

	String checkUpdationDone(String txtSevaarthId);

	List getEmpListForFrmS1Edit(String strDDOCode, String flag, String txtSearch, String isDeputation);

	List getEmpDesigList(String strDDOCode);

	String checkDDORegPresent(String strDDOCode);

	boolean checkBranchAddress(String txtSevaarthId);

	void deleteMultipleRecords(String sevaarthId);

	//for new form S1
	String getDDOCode(String strLocationCode);

	String chkFrmUpdatedByLgnDdo(String txtSearch,
			String strDDOCode);
}
