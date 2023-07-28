/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 2, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.List;
import java.util.Map;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;

/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 Feb 2, 2011
 */
public interface ArrearsDAO extends GenericDao {

	List<ComboValuesVO> getAllDepartment(Long lLngDepartmentId, Long langId)
			throws Exception;

	public List getEmpListForSixPCArrears(String lStrDDOCode) throws Exception;

	public List getEmpListForSixPCArrearsYearly(String lStrDDOCode,
			Long finYearId) throws Exception;

	List<ComboValuesVO> getAllDesignation(Long langId) throws Exception;

	public List getYearsForSixPCYearly();

	public List getEmpListForContribution(String lStrDDOCode,
			Long lLongbillGroupId, Long monthId, Long yearId);

	public List getBillGroups() throws Exception;

	public List getMonths();

	public List getYears();

	public String getDdoCode(Long lLngAsstPostId);

	public Object[] getSchemeNameFromBillGroup(Long billGroupId);

	public List getEmpDtlsFromDDODesig(String lStrDesigId, Map inputMap)
			throws Exception;

	public String getEmpNameFromId(Long lLngdcpsEmpId) throws Exception;

	public List getAllSavedRequestsForTier(Integer lIntCriteria,
			String lStrPostId) throws Exception;

	public List getTierDtlFromDesig(String lStrDesignation) throws Exception;

	public TrnDcpsContribution getContributionDetailsForEmpId(Long empId)
			throws Exception;

	public Long getSixPCIDforEmpId(Long empId) throws Exception;

	public List getEmpListForContributionCorrection(String lStrDDOCode,
			Long lLongbillGroupId, Long monthId, Long yearId) throws Exception;

	public List getEmpListForSixPCArrearAmountSchedule(String lStrDDOCode,
			Long yearId) throws Exception;

	public List getEmpListForContribution(String lStrDDOCode,
			Long lLongbillGroupId, Long monthId, Long yearId, String lStrUser,
			String lStrUse, String lStrPostId);

	public Long getEmpIdforContributionId(Long contributionId);

	public Long getMstDcpsTierICntrnbtnPk(Long lLngEmpId) throws Exception;

	public Long getRltSixPCYearlyIDforEmpId(Long empId);
}
