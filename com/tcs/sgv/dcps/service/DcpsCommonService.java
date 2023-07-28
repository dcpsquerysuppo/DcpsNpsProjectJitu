/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 7, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 7, 2011
 */
public interface DcpsCommonService extends GenericDao {

	public List getMonths();

	public List getYears();

	public String getDdoCode(Long lLngAsstPostId);

	public String getDdoCodeForDDO(Long lLngPostId);

	List<ComboValuesVO> getAllDesignation(Long langId) throws Exception;

	List<ComboValuesVO> getAllDepartment(Long lLngDepartmentId, Long langId)
			throws Exception;

	public List getBillGroups() throws Exception;

	public Date getLastDate(Integer month, Integer year);

	public Date getFirstDate(Integer month, Integer year);

	public Object[] getSchemeNameFromBillGroup(Long billGroupId);

	public String getYearCodeForYearId(Long yearId);

	public List getCadres();

	public List getBankNames() throws Exception;

	public List getBranchNames(Long lLngBankCode) throws Exception;

	public Long getIFSCCodeForBranch(Long branchName) throws Exception;

	public List getStateNames(Long langId) throws Exception;

	public List getDistricts(Long lStrCurrState) throws Exception;

	public List getTaluka(Long lStrCurrDst) throws Exception;

	public List getDesignations(String lStrCurrOffice) throws Exception;

	public List getCurrentOffices();

}
