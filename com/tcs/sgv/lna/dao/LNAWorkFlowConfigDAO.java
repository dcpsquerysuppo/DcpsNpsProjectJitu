package com.tcs.sgv.lna.dao;

import java.util.ArrayList;
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

public interface LNAWorkFlowConfigDAO extends GenericDao
{
	String getDdoCode(Long lLngPostId)throws Exception;
	
	List getAllUsersForDDO(String lStrDdoCode)throws Exception;
	
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
	
	String getActiveHOLst(String locCode);
	String chkRoleHoEntry(String locID);
	public void generateRefId(String locCode,Map objectArgs,Long lLngId,Long updUserId)throws Exception;
	public ArrayList<Long> getRefId(String locCode) throws Exception;
	
	public String chkEntryWfOrgPost(String Pid)throws Exception;
	public void addEntryWfOrgPost(String Pid)throws Exception;
	public String chkEntryWfOrgLoc(String LocCode)throws Exception;
	public void addEntryWfOrgLoc(String LocCode)throws Exception;
	public String chkEntryWfOrgUser(Long lLngUserId);
	public void addEntryWfOrgUser(Long lLngUserId);
	public String addPostInHierarchy(String locCode,Long lLngPostId,Long updUserId,Map objectArgs,ArrayList levelId,
			ArrayList<Long> refId) throws Exception;
	public String getRefIdByDescriptionAndLocId(String locId, String docDescription);
	public String getPostIdForLocId(String postId);
	public boolean isDDOOrNot(String postId);
	
	public boolean lnaActiveDdoOrNot(String lStrDdoCode);
	
}
