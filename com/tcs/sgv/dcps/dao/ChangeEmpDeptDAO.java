/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	June 13, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 June 13, 2011
 */
public interface ChangeEmpDeptDAO extends GenericDao {
	
	List getEmpDetails(String strDcpsId);
	
	public void updateParentDeptOfDDO (String lStrDDOCode , String lStrDeptLocCode, String lStrHODLocCode) throws Exception;
	public void updateParentDeptOfAllEmpsUnderTheDDO (String lStrDDOCode ,String lStrHODLocCode) throws Exception ;
	public List getAllEmpsUnderDDO(String lStrDdoCode) throws Exception ;
	public List getFieldHODComboForEmp(Long lLongEmpId)	throws Exception;
	public List getParentDeptComboForEmp(Long lLongEmpId) throws Exception;
	public void updateParentDeptOfEmp(Long lLongEmpId,String lStrHODLocCode) throws Exception;
	public void updateDDOCodeOfEmp(String lStrDDOCode,Long lLongDcpsEmpId) throws Exception ;
}
