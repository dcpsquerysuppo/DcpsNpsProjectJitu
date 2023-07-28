/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 11, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.valueobject.MstEmp;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 11, 2011
 */
public interface NewRegTreasuryDAO extends GenericDao {

	public List getAllDDOListForPhyFormRcvd(String lStrPostId,
			String lStrUserType) throws Exception;

	public List getAllFormsForDDO(String lStrDDODode, String lStrPostId);

	public List getAllSavedRequests(Integer lIntCriteria, String lStrPostId);

	public Long getCountOfEmpOfSameNameAndDesigAndSameDept(String lStrEmpName,
			String lStrDesig, String lStrDept);

	public String getDDOAsstPostIdForEmpId(String dcpsEmpId);

	public List getAllFormsStatusList(String lStrATOPostId, String lStrTOPostId);

	public List getApprovalByDDODatesforAll(String lStrPostId);

	public Long getCountOfEmpOfSameName(String dcpsId);
	
	public void ArchiveNewRegForm(MstEmp lObjDcpsEmpMst,ServiceLocator serv) throws Exception;
	
//NPS
	public Long getCountOfEmpOfSameNameForNps(String PPAN);
	


}
