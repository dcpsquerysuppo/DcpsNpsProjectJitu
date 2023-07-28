package com.tcs.sgv.gpf.dao;

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
 * Jul 12, 2012
 */

public interface GPFWorkFlowConfigDAO extends GenericDao
{
	String getDdoCode(Long lLngPostId)throws Exception;
	
	List getAllUsersForDDO(String lStrDdoCode,String gStrLocationCode)throws Exception;
	
	List getAllRoles()throws Exception;
	
	String getAllRolesForUser(Long lLngPostId)throws Exception;
	
	String assignRolesToUser(Long lLngUsrPostId,List lLstSelctdRoles,Long lLngCtrdUsr, Long lLngCrtdPost, Map inputMap)throws Exception;
	
	void updateAcvtFlgForPostRoleRlt(Long lLngUsrPostId, List lLstRoles, Long lLngUpdtUsrId, Long lLngUpdtPostId)throws Exception;
	
	void removeRoleFromPostRoleRlt(Long lLngUsrPostId, List lLstRoles, Long lLngUpdtUsrId, Long lLngUpdtPostId)throws Exception;
	
	void addNewEntryInPostRole(Long lLngUsrPostId, List lLstRoles, Long lLngCrtdUsrId, Long lLngCrtdPostId, Map inputMap)throws Exception;
	
	String insertDataForWorkflow(String locCode, Long lLngPostId, Long langId, List arrLevelid,Map objectArgs,Long updUserId,Long uptPostId) throws Exception;
	
	List getLevelIdForRoles(List lLstRoles) throws Exception;
	
	String chkEntryForVerifier(String lStrLocationCode)throws Exception;
	
	String chkEntryForHO(String lStrLocationCode)throws Exception;
	String chkEntryForHOMDC(String lStrLocationCode)throws Exception;
	
	String chkRoleHoEntry(long postId,String locCode) throws Exception;
	String chkHOOrNot(String postId) throws Exception;
	//String getActiveHOLst(String locCode);
	
	List getCurrentVerifiers(String gStrLocationCode);
	
	List getCurrentDEO(String gStrLocationCode);
	
	List getActiveHOLst(String locCode);
	
	String getPendingFlag(String lStrLstActPostId);
	String getPendingFlagHO(String lStrLstActPostId);
	
	String getPostId(String lStrSevaarthId);
	
	String DeAssignRole(Long lLngUsrPostId)throws Exception;
	String DeAssignRoleHO(Long lLngUsrPostId)throws Exception;
	String DeAssignRoleDEO(Long lLngUsrPostId)throws Exception;
	
	String getValidateDDOCodeFlag(String lStrDDOCode);
	String getValidateUsernameFlag(String lStrUsername);
	
	String getLocationCode(String lStrddocode);
	String getPostRoleID (String locid) throws Exception;
	String getUsername(Long lStrddocode) throws Exception;
	Long getUserIdFromPostId(Long lLngPostId) throws Exception;
	//gokarna newww
	void InsertPostForRhoAsst1(Long lStrddocode)throws Exception;
	List getActiveRhoAsstLst(String locCode);
	
	//
	
	
	// 
	String getPostRoleRHOASTT (String locid) throws Exception;

	String getPostRoleIVER (String locid) throws Exception;
	String getPostRoleHO (String locid) throws Exception;

	List getActiveFlagRole(Long lngPostId);            //Added by vivek sharma 06 feb 17

	String reAssignRoleRHOAST(Long postid)throws Exception; //Added by vivek sharma 17 June 17
	int checkPendingRequestHO(String username)throws Exception; //Added by Sooraj on 20th October 17
	int checkPendingRequestVerifier(String username)throws Exception;//Added by Sooraj on 20th October 17
	int checkPendingRequestDEO(String username)throws Exception; //Added by Sooraj on 26th October 2017
	 //String getPostIdforPending(String username)throws Exception;

	String chkEmpGroupHoEntry(Long lLngPostId, String gStrLocationCode);//Added by kavita on 19th  2020

	String getPostIDuser(String lStrUserName);//ADD BY KAVITA 
	
	List getAllRole(String lStrDdoCode);//ADD BY KAVITA
	
	List getAllRolesForMDC(String lStrDdoCode)throws Exception; //swt 18/12/2020
	
	List getEmpForDDO(String lStrSevaarthId,String lStrEmployeeName,String locid,String lStrDdoCode,String lStrDdoName)throws Exception; //swt 21/12/2020
	 
	String getDdoCodeOfEmp(String lStrSevaarthId); //swt 24/12/2020
	
	String getLocationCodeNew(String lStrddocode,String ddocode,String lStrDdoName); //swt 24/12/2020
	
	List getMappedRoleForEmp(String loc_id,String lStrSevaarthId)throws Exception; //swt 28/12/2020
	
	String getRoleOfEmp(String lStrDdoCode,String lStrSevaarthId)throws Exception; //swt 29/12/2020
	
	String getLocationForPost(String lStrDdoCode); //swt 05/01/2021	
	
	String getLevelIdForRolesMDC(String lLstRoles) throws Exception; //swt 09/01/2021
	
	String chkEmpGroupVerifierEntry(Long lLngPostId, String gStrLocationCode);//swt 11/01/2021
	
	String chkRoleVerifierEntry(long postId, String locCode) throws Exception; //swt 11/01/2021
	
	String chkRoleRhoEntry(long postId, String locCode) throws Exception; //swt 12/01/2021
	
	String chkEmpGroupRhoEntry(Long lLngPostId, String gStrLocationCode);//swt 12/01/2021
	
	String chkRoleDeoEntry(long postId, String locCode) throws Exception;//swt 12/01/2021
	
	String chkEntryForDeo(String lStrLocationCode)throws Exception;//swt 12/01/2021
	
	//String getPostRoleRHOASTTMdc(String Locid);//swt 12/01/2021
	
	String insertDataForWorkflowMDC(String locCode, Long lLngPostId, Long langId, List arrLevelid,Map objectArgs,Long updUserId,Long uptPostId) throws Exception;
	
	String getPostRoleHOMDC (String locid,String lLngPostId) throws Exception;
	
	String getPostRoleIVERMDC (String locid,String lLngPostId) throws Exception;
	
	String getPostRoleIDMDC (String locid,String lLngPostId) throws Exception;
	
	List getValidateDdoCode(String txtDdoCodeEmp,String lStrDdoName)throws Exception;
	
	List getValidateSevarthId(String txtDdoCodeEmp,String lStrSevaarthId,String lStrEmployeeName)throws Exception;
	
	String getPostRoleRHOASTTMdc(String Locid,String lLngPostId) throws Exception;
	
	String getServiceEnd(String lStrSevaarthId,String lStrEmployeeName,String lStrDdoCode);
	
	String getDdoNameFromDdoCode(String lStrDdoCode);
	
	List getDdoNameForAutoComplete(String searchKey);
	
	void unlockAccountForuserId(Long lLnguserId)throws Exception;
	
	String getEmpNameFromSevaarthId(String lStrSevaarthId);
	
	String getSevaId(String lStrSevaarthId)throws Exception;
	
	String getEmpName(String lStrEmployeeName,String lStrDdoCode)throws Exception;
	
	List getEmpNameForAutoComplete(String searchKey, String lStrDdoCode,String lStrUser);
	
	String getEmpDdoCode(String ddoName);

	String chkEmpGroupHoEntryMDC(Long lLngPostId, String txtDdoCodeEmp);
	
	List chkPendingReqDeoAdvance(String username);
	
	List chkPendingReqDeoFinal(String username);
	
    List chkPendingReqVeriAdvance(String username);
	
	List chkPendingReqVeriFinal(String username);
	
	String updatePendingReqDeoAdvance(String MstGpfAdvId)throws Exception;
	
	String updatePendingReqDeoFinal(String MstGpfAdvId)throws Exception;
	
	String updatePendingReqVeriAdvance(String MstGpfAdvId)throws Exception;
	
	String updatePendingReqVeriFinal(String MstGpfAdvId)throws Exception;
	
	String checkDeoRoleMapped(String Locid);
	
	int checkPendingRequestDEOMdc(String username);
	
	int checkPendingRequestVerifierMdc(String username);
	
	int checkPendingRequestHOmdc(String username);
	
	List chkPendingReqHoAdvance(String username);
	
	List chkPendingReqHoFinal(String username);
	
	List chkPendingReqHoInitialDataEntry(String username);
	
	String updatePendingReqHoInitial(String MstGpfAdvId)throws Exception;
	
	int checkPendingRequestHOmdcOrderGen(String username);
	
	 //List chkPendingReqHoAdvanceOrderGen(String username);
	 
	 //List chkPendingReqHoFinalOrderGen(String username);
	
	List chkPendingReqHoOrderGen(String username);

	void updateWfHierachyPostMpgFlag(Long lLnguserId)throws Exception;
	//List getEmployeeRoleDtls(Long lLngUserId)throws Exception;
	List chkPendingReqHoApprovedAdv(String username);
	List chkPendingReqHoApprovedFw(String username);
	List chkPendingReqInRHOApproved(String username);
	List chkPendingReqInRHO(String username);
	int checkPendingRequestRHOAsstMdc(String username);
	List chkPendingReqRHOAsstAdvance(String username);
	List chkPendingReqRHOAsstFinal(String username);
	//int checkPendingLNARequestHOmdc(String username);
	List chkPendingReqLNAHo(String username);
	String updatePendingReqLNA(String advanceId,String advancetype)throws Exception;
	
	
}
