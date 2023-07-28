package com.tcs.sgv.dcps.dao;

import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Sep 10, 2012
 */

public interface ChangeAdminFieldDeptForDdoDAO extends GenericDao
{
	List getAllAdminDepartment() throws Exception;
	
	String getAdminAndFieldDeptOfDdo(String lStrDdoCode)throws Exception;
	
	List getAllDesignation(Long lLngFieldDept)throws Exception;
	
	List getAllCadreNames(Long lLngFieldDept)throws Exception;
	
	List getAllCadreIDName(Long lLngFieldDept)throws Exception;
	
	void updateData(String lStrDdoCode, String lStrAdminDept, String lStrFieldDept, Map<Integer, String> lMapCadre)throws Exception;
	
	public List getAllDesignationUsed(Long lLngFieldDept,String lStrDDOCode)throws Exception;
	
	public List getAllCadreNamesUsed(Long lLngFieldDept,String lStrDDOCode)throws Exception;
}
