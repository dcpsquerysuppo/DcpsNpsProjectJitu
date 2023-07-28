package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Oct 30, 2012
 */

public interface AddNewAdminFieldDeptDAO extends GenericDao
{
	List getAllAdminData() throws Exception;
	
	Long getMaxLocIdAdmin() throws Exception;
	
	Long getMaxAdminCode() throws Exception;
	
	List getAdminData(Long lLngLocId) throws Exception;
	
	Long getAdminDeptCode(Long lLngLocId) throws Exception;
	
	List getAllAdminDepartment() throws Exception;
	
	List getAllFieldData(Long lLngAdminCode) throws Exception;
	
	List getFieldData(Long lLngFieldId) throws Exception;
	
	Long getMaxLocIdField() throws Exception;
}
