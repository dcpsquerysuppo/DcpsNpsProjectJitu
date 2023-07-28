/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 28, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.dcps.valueobject.MstDcpsBillGroup;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.ess.valueobject.OrgPostDetailsRlt;
import com.tcs.sgv.ess.valueobject.OrgPostMst;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 28, 2011
 */
public interface DdoBillGroupDAO extends GenericDao {

	public List getSchemesforDDOComboVO(String lStrDDOCode);

	public List getSavedBillGroups(String lStrDdoCode, String billGroupFlag);

	public List<MstEmp> getEmpListForBGIdNotNull(String lStrDDOCode)
			throws Exception;

	public MstDcpsBillGroup getBillGroupDtlsForBillGroupId(Long lLongBillGroupId);

	public List<MstEmp> getShowGroupList(String lStrDDOCode) throws Exception;

	public List getBillGroups(String lStrDDOCode) throws Exception;
	
	public List getBillGroupsWithSchemeCode(String lStrDDOCode) throws Exception;

	public List getDDOOffices(String lStrDDOCode) throws Exception;

	public Integer getEmpListCount(String lStrDDOCode, Map displayTag)
			throws Exception;
	
	public Integer getPostListCount(String locationId, Map displayTag)
	throws Exception;

	public List<MstEmp> getEmpList(String lStrDDOCode, Map displayTag)
			throws Exception;
	
	public List<OrgPostDetailsRlt> getPostList(String locationId, Map displayTag)
	throws Exception;

	public String getBillGroupDetailsForBillGroupId(Long lLongBillGroupId);
	public List getStatusForBillGroupId(Long lLongBillGroupId);
	public List<MstEmp> getEmpListForGivenBillGroup(Long lLongBillGroupId,
			String lStrDDOCode) throws Exception;
	
	public List<OrgPostDetailsRlt> getPostListForGivenBillGroup(Long lLongBillGroupId,
			String locationId) throws Exception;

	public Boolean checkGroupExistsOrNotForBG(Long lLongBillGroupId)
			throws Exception;

	public void deleteClassGroupsForGivenBGId(Long lBillGroupId);

	public List getClassGroupsForGivenBGId(Long lBillGroupId) throws Exception;
	
	public Integer getTotalSubBGsForScheme(String lStrDDOCode, String lStrSchemeCode) throws Exception;
	
	public Integer getTotalEmployeesForBillGroup(Long lLongBillGroupId) throws Exception;
	
	public void updateBillNoInPayroll (Long lLongEmpId,Long lLongbillGroupId,String lStrAttachOrDetach);
	
	public void updateBillNoOfPostInPayroll (Long lLongPostId,Long lLongbillGroupId,String lStrAttachOrDetach);
	
	public List<OrgPostMst> getPostTypeListFromPostId(long postId) throws Exception;
	public List getPostTypeListForGivenBillGroup(Long lLongBillGroupId, String locationId) throws Exception ;
	public long getPostTypeId(long postId) throws Exception;
	public List getPostListForLocation(String locationId, Map displayTag) throws Exception;
	public String getdsgnNameFromEmployeeId(long employeeId);
	
	public Boolean checkContributionsExistInTheBillGroup(Long lLongBillGroupId,String lStrDdoCode) throws Exception ;
	
	public List<MstEmp> getEmpListForBGIdNull(String lStrDDOCode, Map displayTag) throws Exception ;
	
	public Integer getTotalPostsForBillGroup(Long lLongBillGroupId) throws Exception;
	public Integer getBillsGeneratedForTheBillGroup(Long lLongBillGroupId) throws Exception;
	
	//adde by shailesh
	public String getSubsPairFromdcpsEmpId(String dcpsEmpID);
}
