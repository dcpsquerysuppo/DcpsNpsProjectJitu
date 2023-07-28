/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 25, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 25, 2011
 */
public interface SearchEmployeeDAO extends GenericDao {

	public List searchEmployees(String lStrDcpsId, String lStrEmpName,
			Date lDateEmpDOB, String lStrDdoCode);

	public List searchEmps(String lStrSevarthId, String lStrName,
			String lStrDdoCode);

	public List getPayDetails(Long lLngEmpId);

	public List searchEmployeesForSRKA(String lStrDcpsId, String lStrEmpName,
			Date lDateEmpDOB);

	public List searchEmpsForSRKA(String lStrSevarthId, String lStrName);

	public List getEmpNameForAutoComplete(String searchKey,
			String lStrSearchType, String lStrDDOCode,String lStrSearchBy);

	public void UpdateNewPost(Long lLngDcpsEmpId, Long lLngNewPost);

	public String getDesigFromPost(Long lLongPostId);

	public Object[] getBGForPost(Long lLongPostId);

	public Object[] getGRForPost(Long lLongPostId);

	//Adde by pooja - 28-03-2019
	public String getEmpGroup(Long lStrDcpsEmpId,String lStrCadre);
	
	//Added by pooja  01122020 Change pay post details
	
	/*public List getAllDcpsEmployees(String lStrSevarthId, String lStrName,
			String lStrDdoCode);*/
}
