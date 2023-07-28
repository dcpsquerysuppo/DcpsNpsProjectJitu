/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 24, 2011		Kapil Devani								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.dcps.valueobject.DDOInformationDetail;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.dcps.valueobject.HstEmp;
import com.tcs.sgv.dcps.valueobject.MstDcpsBillGroup;
import com.tcs.sgv.dcps.valueobject.MstEmp;

public interface DdoProfileDAO extends GenericDao {

	public List getYears() throws Exception;

	public List getBranchNames(Long lLngBankName) throws Exception;

	public List getBankNames() throws Exception;

	public List getStateNames(Long langId) throws Exception;

	public String getDdoCode(Long lLngAsstPostId);

	public String getDdoCodeForDDO(Long lLngPostId);

	public List getDistricts(Long lStrCurrState) throws Exception;

	public List getTaluka(Long lStrCurrDst) throws Exception;

	public List getAllOffices(String lStrDdoCode);

	public List getSchemeListForDDO(String lStrDDOCode);

	public List getSchemeNamesFromCode(String schemeCode);

	public String getSchemeNameFromCode(String schemeCode);

	public List getShowGroupList(String lStrDDOCode) throws Exception;

	public List<MstEmp> getEmpListForBGIdNotNull(String lStrDDOCode) throws Exception;

	public List<MstDcpsBillGroup> getBillGroupList(String lStrDDOCode) throws Exception;

	public DdoOffice getDdoOfficeDtls(Long ddoOfficeId);

	public List<DdoOffice> getDDOOfficeList() throws Exception;

	public List getAllDepartment(Long lLngDepartmentId, Long langId) throws Exception;

	public List getAllDesignation(Long langId) throws Exception;

	public DDOInformationDetail getDdoInfo(String lStrDdoCode);

	public List getSavedBillGroups(String lStrDdoCode);

	public List getEmpListForDeselection(String lStrDdoCode, Long lLngDesigId, String lStrEmpName, String lStrSevaarthId)
			throws Exception;

	public Boolean checkMultipleEntryInHstEmpForEmpIdOrNot(Long dcpsEmpId) throws Exception;

	public Long getDcpsEmpIdFromSevaarthIdOrName(String lStrEmpName, String lStrSevarthId);

	public List getEmpListForSelection(String lStrEmpName, String lStrSevarthId,
			Boolean lBlMultipleEntriesInHstEmpForEmpId, String lStrDesignation) throws Exception;

	public void deselectEmp(String lStrDcpsId, Map inputMap) throws Exception;

	public void selectEmp(String lStrDcpsId, String lStrDdoCode, Map inputMap) throws Exception;

	public Long getHstEmpPkVal(Long lLngDcpsEmpId) throws Exception;

	public List getYearsForSixPCYearly() throws Exception;

	public List getEmpListForSixPCArrearAmountSchedule(String lStrDDOCode, Long yearId) throws Exception;

	public List getSchemesforDDOComboVO(String lStrDDOCode);

	public String getIFSCCodeForBranch(Long branchName) throws Exception;

	public String getIFSCCodeForBranchBsrCode(String branchName) throws Exception;

	public List getClassGroupforBillGroupId(Long billGroupId) throws Exception;

	public MstDcpsBillGroup getBillGroupDtlsForBillGroupId(Long lLongBillGroupId);

	public Integer getEmpListCount(String lStrDDOCode, Map displayTag) throws Exception;

	public List getBillGroups(String lStrDDOCode) throws Exception;

	public List getDDOOffices(String lStrDDOCode) throws Exception;

	public List<MstEmp> getEmpListForGivenBillGroup(Long lLongBillGroupId, String lStrDDOCode) throws Exception;

	public String getBillGroupDetailsForBillGroupId(Long lLongBillGroupId);

	public Boolean checkArrearsApprovedBeforeDeselection(Long dcpsEmpId, Long yearId);

	public Boolean checkArrearsExistForEmp(Long dcpsEmpId);
	
	public Boolean checkIfPrevArrsApproved(Long dcpsEmpId, Long yearId);

	//changed by shailesh
	
	//public List getVacantPostsInOffice(Long lLongOffice, String lStrDDOCode) throws Exception;
	public List getVacantPostsInOffice(Long lLongOffice, String lStrDDOCode, long lLongDesig) throws Exception;
	//public List getVacantPostsInOffice(Long lLongOffice, long locId, String lStrDDOCode) throws Exception;
	public List getVacantPostsInOffice(Long lLongOffice, long locId, String lStrDDOCode, long lLongDesig) throws Exception;

	public HstEmp getHstEmpVOLatest(Long dcpsEmpId);

	public Object[] getPostBGAndGROrderForEmp(Long dcpsEmpId);
	//added by shailesh : start
	public List getSubstituteList();
	public List getPostDetails(long dcpsEmpId);
	public String billGenerated(long empID,int year,int month,String locId);
	public int getLPCUniqueID(String payslipId,String empId);
	/*
	public void update4thInstToDraftFromRjt(Long lLngDCPSEmpId, Long lLngCurPostID,
			Long lLngCurUserId, Date lDtCurDate) throws Exception ;
		*/

}
