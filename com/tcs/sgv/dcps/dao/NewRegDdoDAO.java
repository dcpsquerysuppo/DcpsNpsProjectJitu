/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 8, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.acl.valueobject.AclRoleMst;
import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.dcps.valueobject.HrPaySubstituteEmpMpg;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmpDetails;
import com.tcs.sgv.dcps.valueobject.RltDcpsPayrollEmp;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 8, 2011
 */
public interface NewRegDdoDAO extends GenericDao {

	public List getDesignations(String lStrCurrOffice) throws Exception;

	public List getCurrentOffices();

	public List getGroupName(Long cadreId);

	public List getNominees(String empId);

	public void deleteNomineesForGivenEmployee(Long lLongEmpId);

	public List getOfficeDetails(Long lLngOfficeId);

	public List getAllDcpsEmployees(String lStrUser, String lStrPostId,
			String lStrDdoCode);

	public List getAllDcpsEmployeesForCaseStatus(String lStrUser,
			String lStrPostId, String lStrDdoCode, String lStrSearchValue);

	public List getAllDcpsEmployeesForDesig(String lStrUser, String lStrPostId,
			String lStrDdoCode, String lStrSearchValue);

	public void updatePhyStatus(Long lLongEmpId);

	public DdoOffice getDdoOfficeVO(Long ddoOfficeId);

	public Long getDcpsEmpPayrollIdForEmpId(Long dcpsEmpId);

	public Boolean checkDcpsEmpPayrollIdForEmpIdExists(Long dcpsEmpId);

	public Long getTotalEmployees();

	public RltDcpsPayrollEmp getPayrollVOForEmpId(Long dcpsEmpId);

	public MstEmp getEmpVOForEmpId(Long dcpsEmpId);
	
	public MstEmpDetails getEmpVOArchivedForEmpId(Long dcpsEmpId) ;

	public List getApprovalByDDODatesforAll(String lStrDDODode,
			String lStrPostId);

	public List getAllPayScales();

	public List getFormListForDDO(String lStrDDOCode);

	public List getDesigsForAutoComplete(String search);

	public String getDistrictForDDO(String lStrDdoCode);

	public List getApprovedFormsForDDO(String lStrDDOCode);
	
	public List getAllApprovedEmpsUnderDDO(String lStrDDOCode,String lStrSevaarthId,String lStrName) ;
	
	public MstEmp getEmpVOForDCPSId(String dcpsId,String ddoCode) ;
	
	public void deleteRltPayrollEmpForGivenEmployee(Long lLongEmpId) ;
	
	public void lockAccountForOrgEmpId (Long lLongOrgEmpMstId);
	
	public Long getPostForEmpId(Long lLongOrgEmpId) ;
	
	public Long getUserIdForEmpId(Long lLongOrgEmpId);
	
	public Long getUserIdForPostId(Long PostId) ;
	
	public Boolean checkEntryInRltDDOAsstTable(Long lLongAsstPostId,Long lLongDDOPostId);
	
	public Boolean checkEntryInAclPostRoleTable(Long lLongAsstPostId);
	
	public void unlockAccountForOrgEmpId (Long lLongOrgEmpMstId);
	
	public Object[] getUserNameAndPwdForEmpId(Long lLongOrgEmpId); 
	
	public AclRoleMst getRoleVOForRoleId(Long roleId);
	
	public OrgPostMst getPostVOForPostId(Long postId);
	
	public OrgUserMst getUserVOForUserId(Long userId) ;
	
	public void insertAclPostRoleRlt(Long lLongAclPostRoleId,Long lLongRoleIdOfDDOAsst,Long lLongPostId,Long lLongDDOPostId,Date gDtCurDate) throws Exception;
	
	public Boolean checkEntryInWFOrgUserMpgMst(Long lLongUserId);
	
	public Boolean checkEntryInWFOrgPostMpgMst(Long lLongPostId);
	
	public void insertWFOrgPostMpg(Long lLongPostId) throws Exception ;
	
	public void insertWFOrgUsrMpg(Long lLongUserId) throws Exception ;

	public void insertWfHierachyPostMpg(Long lLongHierarchySeqId ,Long lLongHierarchyRefId ,Long lLongPostId,Long lLongCreatedByUserId,Date gDtCurDate,Long LocId ) throws Exception;
	
	public List<Long> getAllHierarchyRefIdsForLocation(Long LocationCode);
	
	public Boolean checkEntryInWfHierachyPostMpg(Long lLongHierarchyRefId,Long lLongPostId);
	
	public void updateDDOAsstStatusInMstEmp (Long lLongDcpsEmpId,String lStrRequest);
	
	public Boolean checkIfNameExists(String lStrName) ;
	
	public Boolean checkIfNameAndDOBExists(String lStrName, Date lDateDOB) ;
	
	public void deleteRltDdoAsstEntryWhileDeAssign(Long lLongAsstPostId,Long lLongDDOPostId) ;
	
	public Boolean checkIfDuplicateEmpForGivenCriteria(String lStrName, Date lDateDOB, Character lCharGender,Character lCharFatherOrHusb) ;
	
	public List getAllDupEmpsForMDC() ;
	
	//added by shailesh
	public List getEmployeesForDesg(long desgnCode, long locId);
	public List getSubstituteList();
	public List getPostDetails(long postId);
	public List getPostPsrDetails(long postId);
	public void insertNewPostDetails(HrPaySubstituteEmpMpg objHrPaySubEmpMpg);
	public long getNextSeqNum() ;
	public boolean checkSubstitute(String empId);
	public void updateHrPaySubsEmpMpg(long postId);
	public void updateParentPostId(long postId);
	public String getPostName(long postId);
	public long getPostIdFrmEmpID(long EmpId);
	public long getPostIdFrmRltDcpsPayEmp(long empID);
	public boolean checkSubstituteForRjctn(long postId);
	public long getVacantPostLstSize(long dsgnCode, long locId);
	public List getReligionData();
	// add by amit bhattad
	public List getListOfDupEmp( String lStrName, String lStrDOB, String lStrGender);
	public int checkIfUID(String uid);
	public int checkIfEID(String eid_No);
	
	//added by samadhan for pf account no. check
	public String checkPfDetails(String pfSeries, String pfAccNo);
	//added by sunitha for post display:start
	public String getPostType(Long postId);
	//added by sunitha for post display:end

	//added by arpan
	public List getHrPaySubstituteVO(Long postId);
	public String getSubstituteEmpName(long subPostId);
	public Long getPostId(long lLngDcpsPayrollId);

	public List getmaxSRno(String lStrDdoCode);

	public int getCountTempEmpR3Data(String dcpsId);

	public void updateTempEmpR3Data(String dcpsId);
}
