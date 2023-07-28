package com.tcs.sgv.dcps.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ajaxtags.helpers.ValueItem;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;





import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ajaxtags.helpers.ValueItem;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;


public interface FormS1DAO extends GenericDao{

	List getEmpNameForS1AutoComplete(String upperCase, String strDDOCode);

	String checkSevaarthIdExist(String txtSevaarthId, String strDDOCode);

	List getSectionADetails(String sevaarthId) throws Exception;

	List getSectionBDetails(String sevaarthId, String isDeputation) throws Exception;

	List getSectionCDetails(String sevaarthId)throws Exception;

	List getDTORegNo(String sevaarthId) throws Exception;

	List checkNmnCount(String sevaarthId) throws Exception;

	String checkUpdationDone(String txtSevaarthId);
	
	List getEmpListForFrmS1Edit(String strDDOCode, String flag, String txtSearch, String isDeputation,String LocationCode);
	
	List getFDDtls(String sevarthId1);
	
	List getNDDtls1(String sevarthId1);
	
	List getNDDtls2(String sevarthId1);
	
	List getNDDtls3(String sevarthId1);
	
	List getDDdtls(String sevarthId1);
	
	List getAssoDtoRegNo(String sevarthId1);
	
//	List csrfFormforwardFormTO(String sevaarthid,String Dcpsid);
	String createTextFilesForCSRF(String sevaarthId);
	
	void csrfFormforwardFormTO(String sevaarthid)throws Exception;

//	String checkEmpListForFrmS1Dep(String strDDOCode, String flag, String txtSearch, String isDeputation);
	
	List getEmpDesigList(String strDDOCode);

	String checkDDORegPresent(String strDDOCode);

	boolean checkBranchAddress(String txtSevaarthId);

	void deleteMultipleRecords(String sevaarthId);

	//for new form S1
	String getDDOCode(String strLocationCode);

	String chkFrmUpdatedByLgnDdo(String txtSearch);
	
//	String getDdo(String lStrSevaarthId);
	
	 String checkForPpan(String lStrSevaarthId);
	 //$t 2020-2-2 
	 void removeEmpFromTo(String sevarthId1)throws Exception;
}

////$t 20-1-2O20 Before OPGM
//package com.tcs.sgv.dcps.dao;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//import org.ajaxtags.helpers.ValueItem;
//
//
//import com.tcs.sgv.core.dao.GenericDao;
//import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
//import com.tcs.sgv.dcps.valueobject.MstEmp;
//
//
//public interface FormS1DAO extends GenericDao{
//
//	List getEmpNameForS1AutoComplete(String upperCase, String strDDOCode);
//
//	String checkSevaarthIdExist(String txtSevaarthId, String strDDOCode);
//
//	List getSectionADetails(String sevaarthId) throws Exception;
//
//	List getSectionBDetails(String sevaarthId, String isDeputation) throws Exception;
//
//	List getSectionCDetails(String sevaarthId)throws Exception;
//
//	List getDTORegNo(String sevaarthId) throws Exception;
//
//	List checkNmnCount(String sevaarthId) throws Exception;
//
//	String checkUpdationDone(String txtSevaarthId);
//
//	List getEmpListForFrmS1Edit(String strDDOCode, String flag, String txtSearch, String isDeputation);
//
////	String checkEmpListForFrmS1Dep(String strDDOCode, String flag, String txtSearch, String isDeputation);
//	
//	List getEmpDesigList(String strDDOCode);
//
//	String checkDDORegPresent(String strDDOCode);
//
//	boolean checkBranchAddress(String txtSevaarthId);
//
//	void deleteMultipleRecords(String sevaarthId);
//
//	//for new form S1
//	String getDDOCode(String strLocationCode);
//
//	String chkFrmUpdatedByLgnDdo(String txtSearch);
//	
////	String getDdo(String lStrSevaarthId);
//	
//	 String checkForPpan(String lStrSevaarthId);
//}
