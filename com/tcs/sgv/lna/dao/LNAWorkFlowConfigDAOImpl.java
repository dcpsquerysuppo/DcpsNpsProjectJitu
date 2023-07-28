package com.tcs.sgv.lna.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.dao.AclMstRoleDao;
import com.tcs.sgv.acl.dao.AclMstRoleDaoImpl;
import com.tcs.sgv.acl.valueobject.AclPostroleRlt;
import com.tcs.sgv.acl.valueobject.AclRoleMst;
import com.tcs.sgv.common.dao.CmnDatabaseMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLookupMstDAOImpl;
import com.tcs.sgv.common.dao.UserConfigDAOImpl;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.valueobject.CmnDatabaseMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.ess.dao.OrgPostMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgUserMstDaoImpl;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.ess.valueobject.OrgUserpostRlt;
import com.tcs.sgv.fms.dao.CmnProjectMstDao;
import com.tcs.sgv.fms.dao.CmnProjectMstDaoImpl;
import com.tcs.sgv.fms.dao.WfDocMstDaoImpl;
import com.tcs.sgv.fms.dao.WfOrgLocMpgMstDaoImpl;
import com.tcs.sgv.fms.dao.WfOrgPostMpgMstDaoImpl;
import com.tcs.sgv.fms.dao.WfOrgUsrMpgMstDao;
import com.tcs.sgv.fms.dao.WfOrgUsrMpgMstDaoImpl;
import com.tcs.sgv.fms.valueobject.CmnProjectMst;
import com.tcs.sgv.fms.valueobject.WfHierachyPostMpg;
import com.tcs.sgv.fms.valueobject.WfHierarchyReferenceMst;
import com.tcs.sgv.fms.valueobject.WfOrgLocMpgMst;
import com.tcs.sgv.fms.valueobject.WfOrgPostMpgMst;
import com.tcs.sgv.fms.valueobject.WfOrgUsrMpgMst;
import com.tcs.sgv.ess.valueobject.OrgPostDetailsRlt;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Jul 12, 2012
 */

public class LNAWorkFlowConfigDAOImpl extends GenericDaoHibernateImpl implements LNAWorkFlowConfigDAO
{
	Session ghibSession = null;
	ResourceBundle gObjRsrcBndle = null; 
	public LNAWorkFlowConfigDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/LNAConstants");
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public String getDdoCode(Long lLngPostId)throws Exception
	{
		List lLstResData = null;
		String lStrDdoCode = "";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ddoCode FROM OrgDdoMst WHERE postId =:postId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("postId", lLngPostId);
			lLstResData = lQuery.list();
			
			if(lLstResData != null && lLstResData.size() > 0){
				lStrDdoCode = lLstResData.get(0).toString();
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lStrDdoCode;
	}
	
	
	public List getAllUsersForDDO(String lStrDdoCode)throws Exception
	{
		List lLstAllUsers = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ME.name, OUPR.orgPostMstByPostId.postId, OUM.userName, dg.dsgnName ");
			lSBQuery.append("FROM MstEmp ME, OrgEmpMst OEM, OrgUserpostRlt OUPR, OrgUserMst OUM , OrgDesignationMst dg,OrgPostDetailsRlt rlt ");
			lSBQuery.append(" WHERE ME.orgEmpMstId = OEM.empId AND OEM.orgUserMst.userId = OUPR.orgUserMst.userId AND ME.ddoCode = :ddoCode ");
			lSBQuery.append(" AND OUPR.activateFlag = 1 AND OEM.orgUserMst.userId = OUM.userId and ME.group in ('A','B') ");
			lSBQuery.append(" and rlt.orgDesignationMst.dsgnId = dg.dsgnId and rlt.orgPostMst.postId=OUPR.orgPostMstByPostId.postId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			
			lLstAllUsers = lQuery.list();
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lLstAllUsers;
	}
	
	
	public List getAllRoles()throws Exception
	{
		List lLStAllRoles = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT roleId, roleDesc FROM RltLevelRole WHERE  ");
			lSBQuery.append(" roleId IN(800002)");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lLStAllRoles = lQuery.list();
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lLStAllRoles;
	}
	
	public String getAllRolesForUser(Long lLngPostId)throws Exception
	{
		List lLstRolesForUsr = null;
		String lStrRolesForUsr = "";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT aclRoleMst.roleId FROM AclPostroleRlt WHERE orgPostMst.postId =:postId ");
			lSBQuery.append("AND cmnLookupMstByActivate.lookupId = 1");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("postId", lLngPostId);
			lLstRolesForUsr = lQuery.list();
			
			if(lLstRolesForUsr != null && lLstRolesForUsr.size() > 0){
				for (Integer lIntCnt=0; lIntCnt<lLstRolesForUsr.size(); lIntCnt++) 
				{				  	
					lStrRolesForUsr = lStrRolesForUsr + lLstRolesForUsr.get(lIntCnt)+",";
				}
				lStrRolesForUsr = lStrRolesForUsr.substring(0,lStrRolesForUsr.length()-1);
			}			
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lStrRolesForUsr;
	}
	
	
	public String assignRolesToUser(Long lLngUsrPostId,List lLstSelctdRoles,Long lLngCtrdUsr, 
			Long lLngCrtdPost, Map inputMap)throws Exception
	{
		String lStrResData = "";
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT aclRoleMst.roleId, cmnLookupMstByActivate.lookupId FROM AclPostroleRlt ");
			lSBQuery.append("WHERE orgPostMst.postId = :postId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("postId", lLngUsrPostId);
			
			List<Object[]> resList = lQuery.list();
			
			ArrayList allActLevels = new ArrayList();
			ArrayList updateRoleFlag = new ArrayList();
			ArrayList allRolesGPF = new ArrayList();
			
			//allRolesGPF.add(Long.parseLong("800001"));
			//allRolesGPF.add(Long.parseLong("800002"));
			//allRolesGPF.add(Long.parseLong("800018"));  //lna ho
			allRolesGPF.add(Long.parseLong("800002"));
			
			
			if (!resList.isEmpty()) 
			{
				for(Object[] tuple:resList)
				{ 
					Long resRoleId = Long.parseLong(tuple[0].toString());
					Long resActFlag = Long.parseLong(tuple[1].toString());
					if(resActFlag.equals(Long.valueOf("1")))
					{
						if(allRolesGPF.contains(resRoleId)){
							allActLevels.add(resRoleId);        //List of all active flag with Pid
						}
					}
				}
				
				if(lLstSelctdRoles != null && lLstSelctdRoles.size() > 0){
					allActLevels.removeAll(lLstSelctdRoles);
				}
					
				for(Object[] tuple:resList)
				{
					Long resRoleId = Long.parseLong(tuple[0].toString());
					Long resActFlag = Long.parseLong(tuple[1].toString());
					
					if(resActFlag.equals(Long.valueOf("2")))
					{
						if (lLstSelctdRoles.contains(resRoleId)) 
						{
							updateRoleFlag.add(resRoleId);
						}
					}
					else
					{
						if(lLstSelctdRoles.contains(resRoleId))
						{
							lLstSelctdRoles.remove(resRoleId);
						}
					}
				}
				
				if(lLstSelctdRoles != null && lLstSelctdRoles.size() > 0)
				{
					if(updateRoleFlag != null && updateRoleFlag.size() > 0){
						lLstSelctdRoles.removeAll(updateRoleFlag);
					}
				}
			}
			
			if(updateRoleFlag != null && updateRoleFlag.size() > 0){
				updateAcvtFlgForPostRoleRlt(lLngUsrPostId,updateRoleFlag,lLngCtrdUsr,lLngCrtdPost);
				lStrResData = lStrResData + "I";
			}
			
			if(allActLevels != null && allActLevels.size() > 0){
				removeRoleFromPostRoleRlt(lLngUsrPostId,allActLevels,lLngCtrdUsr,lLngCrtdPost);
				lStrResData = lStrResData + "R";
			}
			
			if(lLstSelctdRoles != null && lLstSelctdRoles.size() > 0)
			{
				addNewEntryInPostRole(lLngUsrPostId,lLstSelctdRoles,lLngCtrdUsr,lLngCrtdPost,inputMap);
				lStrResData = lStrResData + "I";
			}
			
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			logger.error("PostId is===============>>>"+lLngUsrPostId);
			throw e;
		}
		
		return lStrResData;
	}
	
	
	public void updateAcvtFlgForPostRoleRlt(Long lLngUsrPostId, List lLstRoles, Long lLngUpdtUsrId, Long lLngUpdtPostId)throws Exception
	{
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE AclPostroleRlt SET cmnLookupMstByActivate.lookupId = 1, updatedDate=:updtDate, ");
			lSBQuery.append("orgUserMstByUpdatedBy.userId =:updtUsrId, orgPostMstByUpdatedByPost.postId =:updtPostId ");
			lSBQuery.append("WHERE orgPostMst.postId =:postId AND aclRoleMst.roleId IN (:roleList)");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("postId", lLngUsrPostId);
			lQuery.setLong("updtUsrId", lLngUpdtUsrId);
			lQuery.setLong("updtPostId", lLngUpdtPostId);
			lQuery.setParameterList("roleList", lLstRoles);
			lQuery.setDate("updtDate", DBUtility.getCurrentDateFromDB());
			lQuery.executeUpdate();
			
			lSBQuery = null;
			lQuery = null;
			NewRegDdoDAO newRegDdoDAO = new NewRegDdoDAOImpl(OrgPostDetailsRlt.class,
					 this.getSessionFactory());
			OrgPostDetailsRlt orgPostDetailsRlt = new OrgPostDetailsRlt();
			
			 lSBQuery = new StringBuilder();
			lSBQuery.append("from OrgPostDetailsRlt pd where pd.orgPostMst.postId = :postId ");
			 lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("postId", lLngUsrPostId.longValue());			
			
			if(lQuery != null && lQuery.list().size() > 0){
				orgPostDetailsRlt = (OrgPostDetailsRlt)lQuery.list().get(0);
			}
			
			orgPostDetailsRlt.setPostName(orgPostDetailsRlt.getPostName()+"(Head of Office)");
			newRegDdoDAO.update(orgPostDetailsRlt);
			
			lSBQuery = null;
			lQuery = null;
			newRegDdoDAO = null;
			orgPostDetailsRlt = null;
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}
	
	
	public void removeRoleFromPostRoleRlt(Long lLngUsrPostId, List lLstRoles, Long lLngUpdtUsrId, Long lLngUpdtPostId)throws Exception
	{		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE AclPostroleRlt SET cmnLookupMstByActivate.lookupId = 2, updatedDate=:updtDate, ");
			lSBQuery.append("orgUserMstByUpdatedBy.userId =:updtUsrId, orgPostMstByUpdatedByPost.postId =:updtPostId ");
			lSBQuery.append("WHERE orgPostMst.postId =:postId AND aclRoleMst.roleId IN (:roleList)");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("postId", lLngUsrPostId.longValue());
			lQuery.setLong("updtUsrId", lLngUpdtUsrId.longValue());
			lQuery.setLong("updtPostId", lLngUpdtPostId.longValue());
			lQuery.setParameterList("roleList", lLstRoles);
			lQuery.setDate("updtDate", DBUtility.getCurrentDateFromDB());
			lQuery.executeUpdate();
			
			lSBQuery = null;
			lQuery = null;
			NewRegDdoDAO newRegDdoDAO = new NewRegDdoDAOImpl(OrgPostDetailsRlt.class,
					 this.getSessionFactory());
			OrgPostDetailsRlt orgPostDetailsRlt = new OrgPostDetailsRlt();
			
			 lSBQuery = new StringBuilder();
			lSBQuery.append("from OrgPostDetailsRlt pd where pd.orgPostMst.postId = :postId ");
			 lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("postId", lLngUsrPostId.longValue());			
			
			if(lQuery != null && lQuery.list().size() > 0){
				orgPostDetailsRlt = (OrgPostDetailsRlt)lQuery.list().get(0);
			}
			
			if(orgPostDetailsRlt.getPostName().contains("(Head of Office)"))
			orgPostDetailsRlt.setPostName(orgPostDetailsRlt.getPostName().substring(0, 
					orgPostDetailsRlt.getPostName().indexOf("(Head of Office)")));
			newRegDdoDAO.update(orgPostDetailsRlt);
			
			lSBQuery = null;
			lQuery = null;
			newRegDdoDAO = null;
			orgPostDetailsRlt = null;
			
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}
	
	
	public void addNewEntryInPostRole(Long lLngUsrPostId, List lLstRoles, Long lLngCrtdUsrId, Long lLngCrtdPostId, Map inputMap)throws Exception
	{
		UserConfigDAOImpl aclPostRoleVO = new UserConfigDAOImpl(AclPostroleRlt.class,this.getSessionFactory());
		
		try{
			if(lLstRoles != null && lLstRoles.size()>0)
			{
				OrgPostMstDaoImpl orgPostId = new OrgPostMstDaoImpl(OrgPostMst.class,this.getSessionFactory());
				OrgPostMst postId = orgPostId.read(lLngUsrPostId);
				OrgPostMst crtPostId =  orgPostId.read(lLngCrtdPostId);
				
				AclMstRoleDao lAclMstRoleDao = new AclMstRoleDaoImpl(AclRoleMst.class,this.getSessionFactory());
				
				CmnLookupMstDAOImpl actFlag = new CmnLookupMstDAOImpl(CmnLookupMst.class,this.getSessionFactory()); 
				CmnLookupMst activeFlag = actFlag.read(Long.parseLong("1"));
				
				OrgUserMstDaoImpl userMst = new OrgUserMstDaoImpl(OrgUserMst.class,this.getSessionFactory());
				OrgUserMst crtUserId = userMst.read(lLngCrtdUsrId);			
				
				Long roleLevelId= new Long("0");
									
				for (int i = 0; i < lLstRoles.size(); i++) 
				{								
					roleLevelId = IFMSCommonServiceImpl.getNextSeqNum("acl_postrole_rlt", inputMap);
					AclRoleMst roleId = lAclMstRoleDao.read(Long.parseLong(lLstRoles.get(i).toString()));
					AclPostroleRlt levelRoleVO = new AclPostroleRlt();
					//AclPostroleRlt levelRoleVO = new AclPostroleRlt();
					levelRoleVO.setPostRoleId(roleLevelId++);
					levelRoleVO.setOrgPostMst(postId);
					levelRoleVO.setAclRoleMst(roleId);
					levelRoleVO.setStartDate(DBUtility.getCurrentDateFromDB());
					levelRoleVO.setEndDate(null);
					levelRoleVO.setCmnLookupMstByActivate(activeFlag);
					levelRoleVO.setOrgUserMstByCreatedBy(crtUserId);
					levelRoleVO.setCreatedDate(DBUtility.getCurrentDateFromDB());
					levelRoleVO.setOrgPostMstByCreatedByPost(crtPostId);
					levelRoleVO.setOrgUserMstByUpdatedBy(null);
					levelRoleVO.setUpdatedDate(null);
					levelRoleVO.setOrgPostMstByUpdatedByPost(null);
					aclPostRoleVO.create(levelRoleVO);
				}
				
				 NewRegDdoDAO newRegDdoDAO = new NewRegDdoDAOImpl(OrgPostDetailsRlt.class,
						 this.getSessionFactory());
				OrgPostDetailsRlt orgPostDetailsRlt = new OrgPostDetailsRlt();
				
				StringBuilder lSBQuery = new StringBuilder();
				lSBQuery.append("from OrgPostDetailsRlt pd where pd.orgPostMst.postId = :postId ");
				Query lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setLong("postId", lLngUsrPostId.longValue());			
				
				if(lQuery != null && lQuery.list().size() > 0){
					orgPostDetailsRlt = (OrgPostDetailsRlt)lQuery.list().get(0);
				}
				
				orgPostDetailsRlt.setPostName(orgPostDetailsRlt.getPostName()+"(Head of Office)");
				
				logger.info("index "+orgPostDetailsRlt.getPostName().indexOf("(Head of Office)"));
				newRegDdoDAO.update(orgPostDetailsRlt);
				lSBQuery = null;
				lQuery = null;
				newRegDdoDAO = null;
				orgPostDetailsRlt = null;
			}
		}catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}
	
	
	public String insertDataForWorkflow(String locCode, Long lLngPostId, Long langId, List arrLevelid,Map objectArgs
			  ,Long updUserId,Long uptPostId) throws Exception
	{			
		String strUserLevelOuter ="";
		String strRes = "";
		ArrayList delLevelId = new ArrayList();
		ArrayList descList = new ArrayList();
		ArrayList arrRoleId = new ArrayList();
		ArrayList astLevelId = new ArrayList();
		List astPostId = null;
		Long lLngUserId = null;
					
		try {
			StringBuffer sbLevel = new StringBuffer();
			
			sbLevel.append(
				"select distinct wp.levelId,wp.activateFlag, wr.wfDocMst.docId " + 
				"  from WfHierachyPostMpg wp , WfHierarchyReferenceMst wr " + 
				" where  wr.wfDocMst.docId IN (800007,800008,800009) AND " + 
				" wr.hierachyRefId = wp.hierachyRefId " +
				" and  wp.wfOrgPostMpgMst.postId = :Pid " + 
				" and wp.wfOrgLocMpgMst.locId = :locCode " );
			
				
				Query sqlLevelQuery = ghibSession.createQuery(sbLevel.toString());
				
				sqlLevelQuery.setString("locCode", locCode);
				sqlLevelQuery.setLong("Pid", lLngPostId);				
				ArrayList updateLevelFlag = new ArrayList();
				ArrayList updateRoleFlag = new ArrayList();
				ArrayList allActLevels = new ArrayList();
				
				List<Object[]> resList = sqlLevelQuery.list();

				if (!resList.isEmpty()) 
				{
					for(Object[] tuple:resList)
					{ /*add all active levels to an array*/
						Integer resLevelId = Integer.parseInt(tuple[0].toString());
						Long resActFlag = Long.parseLong(tuple[1].toString());
						if(resActFlag.equals(Long.valueOf("1"))){
							allActLevels.add(resLevelId);
						}
					}
					/*check whether the role is already assigned to the level and now is unchecked so needs to be deactivated */						
					if(arrLevelid !=null){
						allActLevels.removeAll(arrLevelid);
					}
					
					for(Object[] tuple:resList)
					{
						Integer resLevelId = Integer.parseInt(tuple[0].toString());
						Long resActFlag = Long.parseLong(tuple[1].toString());
						if(arrLevelid != null)
						{
							if(resActFlag.equals(Long.valueOf("2")))
							{
								if (arrLevelid.contains(resLevelId)) {
									updateLevelFlag.add(resLevelId);
								}
							}
							else 
							{
							/*check whether the role is already assigned to the level */		
								if(arrLevelid.contains(resLevelId)){
									arrLevelid.remove(resLevelId);
								}
							}
						}
					
					}
					if(arrLevelid !=null)
					{	
						if(updateLevelFlag != null && updateLevelFlag.size() >0){
							arrLevelid.removeAll(updateLevelFlag);
						}
					}
					
					delLevelId = (ArrayList)allActLevels ;
				}
				else{
					generateRefId(locCode,objectArgs,langId,updUserId);
				}
				
				/*get  ref_id for insertion in WF_HIERACHY_POST_MPG*/
				ArrayList<Long> refId = new ArrayList<Long>();
				refId = getRefId(locCode);
					
				
				/*when the level is not present but is active make it deactive: DELETE*/
				
				if(!delLevelId.isEmpty()&& delLevelId!= null)
				{
					delLevelEntriesWF(lLngPostId,uptPostId,updUserId,locCode,refId,delLevelId);
					strRes = "D";				
				}
				
				/*when the level is present but is deactivated to activate it set flag=1*/
				if(!updateLevelFlag.isEmpty() && updateLevelFlag!= null)
				{
					updateActFlagForPostWF(lLngPostId,uptPostId,updUserId,updateLevelFlag,locCode,refId);
					strRes += "I";
				}			
				
				/*Insert for common pool ends*/
				
				if(arrLevelid != null && arrLevelid.size() >0)
				{					
					strUserLevelOuter = addPostInHierarchy(locCode,lLngPostId,updUserId,objectArgs,(ArrayList) arrLevelid,refId);
					strRes += "I";
				}
				
		} catch (Exception e) {			
			logger.error(" Error is : " + e, e);
			logger.error("PostId is===============>>>"+lLngPostId);
			throw e;
		}		
			
		return strRes;
	}
	
	
	public void generateRefId(String locCode,Map objectArgs,Long lLngId,Long updUserId)throws Exception
	{
		List lLstDocId = new ArrayList();
		Map lLstDocDscrptn = new HashMap();
		String lStrDocDesc = "";
		Long lLngMaxRefId = null;
		Long lLngHrSeqNo = null;
		Session hibSession = getSession();
		
		try {
			CmnDatabaseMstDaoImpl dbDAO = new CmnDatabaseMstDaoImpl(CmnDatabaseMst.class,this.getSessionFactory());
			CmnDatabaseMst  dbId = dbDAO.read(Long.parseLong("99"));
			
			WfOrgUsrMpgMstDao lwfOrgUsrMpgMstDao = new WfOrgUsrMpgMstDaoImpl(WfOrgUsrMpgMst.class,this.getSessionFactory());			 
			WfOrgUsrMpgMst lwfOrgUsrMpgMst = lwfOrgUsrMpgMstDao.read("1");
			
			com.tcs.sgv.fms.dao.WfDocMstDao lObjWfDocMstDao = new WfDocMstDaoImpl(com.tcs.sgv.fms.valueobject.WfDocMst.class,this.getSessionFactory());
			com.tcs.sgv.fms.valueobject.WfDocMst lObjWfDocMst = null;
			
			
			lLstDocId.add(Long.parseLong("800007"));
			lLstDocId.add(Long.parseLong("800008"));
			lLstDocId.add(Long.parseLong("800009"));
			
			lLstDocDscrptn.put("800007",gObjRsrcBndle.getString("LNA.CompAdvanceOffline"));
			lLstDocDscrptn.put("800008",gObjRsrcBndle.getString("LNA.MotorAdvanceOffline"));
			lLstDocDscrptn.put("800009",gObjRsrcBndle.getString("LNA.HouseAdvanceOffline"));
			
			lLngMaxRefId = getMaxRefId();
			
			
			if(chkEntryWfHrfMst(locCode,lLstDocId) == "N")
			{
				for(int i=0; i<lLstDocId.size(); i++)
				{
					lLngHrSeqNo = lLngMaxRefId;
					lObjWfDocMst = new com.tcs.sgv.fms.valueobject.WfDocMst();
					lObjWfDocMst = (com.tcs.sgv.fms.valueobject.WfDocMst)lObjWfDocMstDao.read(Long.parseLong(lLstDocId.get(i).toString()));	
					lStrDocDesc = lLstDocDscrptn.get( lLstDocId.get(i).toString()).toString();				
					
					WfHierarchyReferenceMst lObjWfHiRefMst = new WfHierarchyReferenceMst();
					lObjWfHiRefMst.setHierachySeqId(lLngHrSeqNo);
					lObjWfHiRefMst.setHierachyRefId(lLngMaxRefId);
					lObjWfHiRefMst.setWfDocMst(lObjWfDocMst);
					lObjWfHiRefMst.setReferenceName(lStrDocDesc);
					lObjWfHiRefMst.setDescription(lStrDocDesc);
					lObjWfHiRefMst.setCrtDt(DBUtility.getCurrentDateFromDB());
					lObjWfHiRefMst.setStartDate(DBUtility.getCurrentDateFromDB());
					lObjWfHiRefMst.setActivateFlag(1);
					lObjWfHiRefMst.setCmnDatabaseMst(dbId);
					lObjWfHiRefMst.setLocationCode(locCode);
					lObjWfHiRefMst.setLangId(lLngId.toString());
					lObjWfHiRefMst.setWfOrgUsrMpgMstByCrtUsr(lwfOrgUsrMpgMst);
					hibSession.save(lObjWfHiRefMst);
					hibSession.flush();
					lLngMaxRefId++;
				}
			}
		} catch (Exception e) {			
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}
	
	public Long getMaxRefId()throws Exception
	{
		Long lLngMaxId = null;
		Session hibSession = getSession();
		
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT MAX(hierachySeqId) FROM WfHierarchyReferenceMst ");
			lSBQuery.append("WHERE hierachyRefId LIKE '8%'");
			Query lQuery = hibSession.createQuery(lSBQuery.toString());			
			lLngMaxId = (Long) lQuery.list().get(0);
			if(lLngMaxId != null){
				lLngMaxId++;
			}
		} catch (Exception e) {			
			logger.error(" Error is : " + e, e);
			throw e;
		}
		
		return lLngMaxId;
	}
	
	public String chkEntryWfHrfMst(String lStrLocCode,List docId) throws Exception
	{
		List lLstResData = null;
		String lStrRes = "";
		Session ghibSession = getSession();
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("FROM WfHierarchyReferenceMst WHERE locationCode = :locCode AND wfDocMst.docId IN (:docId)");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("locCode", lStrLocCode);
			lQuery.setParameterList("docId", docId);
			
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lStrRes = "Y";
			}else{
				lStrRes = "N";
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		
		return lStrRes;
	}
	
	public String getDocDesc(Long DocId)throws Exception
	{
		String lStrDocDesc = "";
		Session hibSession = getSession();
		
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT docName FROM WfDocMst WHERE docId =:docId");
			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("docId", DocId);
			lStrDocDesc = lQuery.list().get(0).toString();
		} catch (Exception e) {			
			logger.error(" Error is : " + e, e);
			throw e;
		}
		
		return lStrDocDesc;
	}
	
	public ArrayList<Long> getRefId(String locCode) throws Exception
	  {
		  ArrayList<Long> resList = new ArrayList<Long>();	
		  ArrayList lLstDoc = new ArrayList<Long>();
		  Session hibSession = getSession();
		  
		  
		  lLstDoc.add(Long.parseLong("800007"));
		  lLstDoc.add(Long.parseLong("800008"));
		  lLstDoc.add(Long.parseLong("800009"));
		  
		  try{
			  StringBuffer sbRefId = new StringBuffer();
		  	  sbRefId.append("select distinct wm.hierachyRefId " +
						"  from WfHierarchyReferenceMst wm" +
						" where wm.locationCode = :locCode" +
						" and wm.wfDocMst.docId in (:DocList) and activateFlag = 1");
						
				Query sqlRefId = hibSession.createQuery(sbRefId.toString());
	
				sqlRefId.setString("locCode", locCode);
				sqlRefId.setParameterList("DocList", lLstDoc);
	
				List resRefId = sqlRefId.list();
				if (resRefId != null) {
					for(int i =0;i<resRefId.size();i++)
					{
						resList.add(Long.parseLong(resRefId.get(i).toString()));
					}
				}
		  } catch (Exception e) {			
				logger.error(" Error is : " + e, e);
				throw e;
			}
		return resList;
	  }
	
	public void delLevelEntriesWF(Long lLngPostId,Long uptPostId,Long uptUserId,String locCode,
			 ArrayList<Long> refId,ArrayList levelId) throws Exception
	 {
	 	Session hibSession = getSession();
		
	 	try{
			if(!refId.isEmpty()){
				StringBuffer sbDelRoleId = new StringBuffer();
				 sbDelRoleId.append("UPDATE WF_HIERACHY_POST_MPG WPG SET WPG.ACTIVATE_FLAG = 2 ," +
				 					" WPG.END_DATE =:dt, " +
				 					" WPG.LST_UPD_USR = :userId, WPG.LST_UPD_DT =:dt " +
									" WHERE WPG.POST_ID = :pid" + 
									" AND WPG.HIERACHY_REF_ID in(:refId) " + 
									" AND WPG.LOC_ID = :locCode" + 
									" AND WPG.LEVEL_ID in (:levelId) ");
			
				 Query strDelQuery =hibSession.createSQLQuery(sbDelRoleId.toString());
				 strDelQuery.setLong("userId", Long.parseLong("1"));
				 strDelQuery.setDate("dt", DBUtility.getCurrentDateFromDB());
				 strDelQuery.setLong("pid",lLngPostId);
				 strDelQuery.setString("locCode", locCode);
				 strDelQuery.setParameterList("levelId", levelId);
				 strDelQuery.setParameterList("refId", refId);
				 strDelQuery.executeUpdate();
				 hibSession.flush();
			}
	 	}catch (Exception e) {			
			logger.error(" Error is : " + e, e);
			throw e;
		}
	 }
	
	public void updateActFlagForPostWF(Long lLngPostId,Long uptPostId,Long uptUserId,ArrayList levelId,String lStrLocCode,ArrayList refId) throws Exception
	 {
	 	Session hibSession = getSession();
	 	
	 	ArrayList ilevelId =new ArrayList();
	 	
	 	try{
		 	if(!levelId.isEmpty()){
				 StringBuffer sbDelRoleId = new StringBuffer();
				 sbDelRoleId.append("UPDATE WF_HIERACHY_POST_MPG WPG SET WPG.ACTIVATE_FLAG = 1, " +
						 			" WPG.END_DATE = null , " +
									" WPG.LST_UPD_USR = :userId, WPG.LST_UPD_DT =:dt" +
									" WHERE WPG.POST_ID = :pid" + 
									" AND WPG.HIERACHY_REF_ID in(:refId) " + 
									" AND WPG.LOC_ID = :locCode" + 
									" AND WPG.LEVEL_ID in (:levelId) ");
			
				 Query strDelQuery =hibSession.createSQLQuery(sbDelRoleId.toString());
				 
				 strDelQuery.setLong("pid", lLngPostId);
				 strDelQuery.setString("locCode", lStrLocCode);
				 strDelQuery.setParameterList("levelId", levelId);
				 strDelQuery.setParameterList("refId", refId);
				 strDelQuery.setLong("userId", Long.parseLong("1"));
				 strDelQuery.setDate("dt", DBUtility.getCurrentDateFromDB());
				 strDelQuery.executeUpdate();
				 hibSession.flush();
		 	} 
	 	}catch (Exception e) {			
			logger.error(" Error is : " + e, e);
			throw e;
		}
	 }
	
	public String addPostInHierarchy(String locCode,Long lLngPostId,Long updUserId,Map objectArgs,ArrayList levelId,
			ArrayList<Long> refId) throws Exception
	{
		String resString="";		
		UserConfigDAOImpl WfHierachyVO = new UserConfigDAOImpl(WfHierachyPostMpg.class,this.getSessionFactory());
		Long lLngUserId = null;
		
		try{
			if(chkEntryWfOrgPost(lLngPostId.toString()) == "N"){
				addEntryWfOrgPost(lLngPostId.toString());
			}
			if(chkEntryWfOrgLoc(locCode) == "N"){
				addEntryWfOrgLoc(locCode);
			}
			
			lLngUserId = getUserIdFromPostId(lLngPostId);
			
			if(chkEntryWfOrgUser(lLngUserId) == "N"){
				addEntryWfOrgUser(lLngUserId);
			}
			if(chkEntryWfOrgUser(updUserId) == "N"){
				addEntryWfOrgUser(updUserId);
			}
			
			if(levelId != null && levelId.size()>0)
			{
			  Session hibSession = getSession();
			
			  WfOrgPostMpgMstDaoImpl lwfOrgPostMpgMstDaoImpl=new WfOrgPostMpgMstDaoImpl(WfOrgPostMpgMst.class, this.getSessionFactory());
			  WfOrgPostMpgMst lwfOrgPostMpgMst = lwfOrgPostMpgMstDaoImpl.read(lLngPostId.toString());
			
			  WfOrgUsrMpgMstDao lwfOrgUsrMpgMstDao = new WfOrgUsrMpgMstDaoImpl(WfOrgUsrMpgMst.class,this.getSessionFactory());
			  WfOrgUsrMpgMst lwfOrgUsrMpgMst = lwfOrgUsrMpgMstDao.read(updUserId.toString());		  
			
			  WfOrgLocMpgMstDaoImpl wfOrgLocMpgMstDaoImpl=new WfOrgLocMpgMstDaoImpl(WfOrgLocMpgMst.class,this.getSessionFactory());
			  WfOrgLocMpgMst wfOrgLocMpgMst=wfOrgLocMpgMstDaoImpl.read(locCode);
				
			  Long startSeqNo= new Long("0");
			  // Long startSeqNo = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("wf_hierachy_post_mpg", objectArgs,levelId.size());
			  
			  for(int i=0;i<levelId.size();i++)
			  {
				  WfHierachyPostMpg wfHiePostMpg = null;
				  for(int j=0; j < refId.size(); j++){ 
				  if(chkEntryWfHrPost(lLngPostId, locCode, Integer.parseInt(levelId.get(i).toString()),refId.get(j)) == "N")
				  {
					  
					 
				  	startSeqNo  = IFMSCommonServiceImpl.getNextSeqNum("wf_hierachy_post_mpg", objectArgs);
						  				 
				  	wfHiePostMpg =  new WfHierachyPostMpg();
					wfHiePostMpg.setHierachySeqId(startSeqNo);
					wfHiePostMpg.setParentHierachy(null);
					wfHiePostMpg.setWfOrgPostMpgMst(lwfOrgPostMpgMst);
					wfHiePostMpg.setLevelId(Integer.parseInt(levelId.get(i).toString()));
					wfHiePostMpg.setHierachyRefId(refId.get(j));
					wfHiePostMpg.setWfOrgUsrMpgMstByCrtUsr(lwfOrgUsrMpgMst);
					wfHiePostMpg.setCrtDt(DBUtility.getCurrentDateFromDB());
					wfHiePostMpg.setWfOrgUsrMpgMstByLstUpdUsr(null);
					wfHiePostMpg.setLstUpdDt(null);
					wfHiePostMpg.setStartDate(DBUtility.getCurrentDateFromDB());
					wfHiePostMpg.setEndDate(null);
					wfHiePostMpg.setActivateFlag(1);
					wfHiePostMpg.setWfOrgLocMpgMst(wfOrgLocMpgMst);
					wfHiePostMpg.setLangId("1");
					wfHiePostMpg.setDueDays(null);
					//WfHierachyVO.create(wfHiePostMpg);
					hibSession.save(wfHiePostMpg);
					hibSession.flush();	
					wfHiePostMpg = null;
					 }
				  }
			  }
			  resString="I";
			  
			}
		}catch(Exception e){
			logger.error(" Error is : " + e);
			e.printStackTrace();
			throw e;
		}
		
		return resString;
	}
	
	public String chkEntryWfOrgPost(String Pid)throws Exception
	{
		Session hibSession = getSession();
		String res = "";
		List lLstRes = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT postId FROM WfOrgPostMpgMst ");
			lSBQuery.append("WHERE postId = :postId");
			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setString("postId", Pid);
			lLstRes = lQuery.list();
			
			if(lLstRes.size() > 0 && !lLstRes.isEmpty()){
				res = "Y";
			}else{
				res = "N";
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return res;
	}
	
	public void addEntryWfOrgPost(String Pid)throws Exception
	{
		Session hibSession = getSession();
		Integer lIntProjId = 101;
		try{
			CmnProjectMstDao lObjCmnProjectMstDao = new CmnProjectMstDaoImpl(CmnProjectMst.class,this.getSessionFactory());
			CmnProjectMst lObjCmnProjectMst = lObjCmnProjectMstDao.read(lIntProjId);
			
			CmnDatabaseMstDaoImpl dbDAO = new CmnDatabaseMstDaoImpl(CmnDatabaseMst.class,this.getSessionFactory());
			CmnDatabaseMst  dbId = dbDAO.read(Long.parseLong("99"));
			
			WfOrgPostMpgMst lObjWfOrgPostMpgMst = new WfOrgPostMpgMst();
			
			lObjWfOrgPostMpgMst.setPostId(Pid);
			lObjWfOrgPostMpgMst.setCmnProjectMst(lObjCmnProjectMst);
			lObjWfOrgPostMpgMst.setCmnDatabaseMst(dbId);
			hibSession.save(lObjWfOrgPostMpgMst);
			hibSession.flush();
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}
	
	public String chkEntryWfOrgLoc(String LocCode)throws Exception
	{
		Session hibSession = getSession();
		String res = "";
		List lLstRes = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("FROM WfOrgLocMpgMst ");
			lSBQuery.append("WHERE locId = :locId");
			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setString("locId", LocCode);
			lLstRes = lQuery.list();
			
			if(lLstRes.size() > 0 && !lLstRes.isEmpty()){
				res = "Y";
			}else{
				res = "N";
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return res;
	}
	
	public void addEntryWfOrgLoc(String LocCode)throws Exception
	{
		Session hibSession = getSession();
		Integer lIntProjId = 101;
		try{
			CmnProjectMstDao lObjCmnProjectMstDao = new CmnProjectMstDaoImpl(CmnProjectMst.class,this.getSessionFactory());
			CmnProjectMst lObjCmnProjectMst = lObjCmnProjectMstDao.read(lIntProjId);
			
			CmnDatabaseMstDaoImpl dbDAO = new CmnDatabaseMstDaoImpl(CmnDatabaseMst.class,this.getSessionFactory());
			CmnDatabaseMst dbId = dbDAO.read(Long.parseLong("99"));
			
			WfOrgLocMpgMst lObjWfOrgLocMpgMst = new WfOrgLocMpgMst();
			
			lObjWfOrgLocMpgMst.setLocId(LocCode);
			lObjWfOrgLocMpgMst.setCmnProjectMst(lObjCmnProjectMst);			
			hibSession.save(lObjWfOrgLocMpgMst);
			hibSession.flush();
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}
	
	public String chkEntryWfOrgUser(Long lLngUserId)
	{
		Session hibSession = getSession();
		String res = "";
		List lLstRes = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("FROM WfOrgUsrMpgMst WHERE userId = :userId \n");
			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("userId", lLngUserId.toString());
			lLstRes = lQuery.list();
			
			if(lLstRes.size() > 0){
				res = "Y";
			}else{
				res = "N";
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
		}
		
		return res;
	}
	
	public void addEntryWfOrgUser(Long lLngUserId)
	{
		Session hibSession = getSession();
		Integer lIntProjId = 101;
		
		try{
			CmnProjectMstDao lObjCmnProjectMstDao = new CmnProjectMstDaoImpl(CmnProjectMst.class,this.getSessionFactory());
			CmnProjectMst lObjCmnProjectMst = lObjCmnProjectMstDao.read(lIntProjId);
			
			CmnDatabaseMstDaoImpl dbDAO = new CmnDatabaseMstDaoImpl(CmnDatabaseMst.class,this.getSessionFactory());
			CmnDatabaseMst dbId = dbDAO.read(Long.parseLong("99"));
			
			WfOrgUsrMpgMst lObjWfUsrMpgMst = new WfOrgUsrMpgMst();
			lObjWfUsrMpgMst.setUserId(lLngUserId.toString());
			lObjWfUsrMpgMst.setCmnProjectMst(lObjCmnProjectMst);
			lObjWfUsrMpgMst.setCmnDatabaseMst(dbId);
			
			hibSession.save(lObjWfUsrMpgMst);
			hibSession.flush();
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
		}
	}
	
	public String chkEntryWfHrPost(Long Pid,String lStrLocCode,Integer lIntLevelId,Long refId) throws Exception
	{
		List lLstResData = null;
		String lStrRes = "";
		Session ghibSession = getSession();
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT * FROM WF_HIERACHY_POST_MPG WHERE POST_ID = :pid AND LOC_ID = :locId \n");
			lSBQuery.append("AND LEVEL_ID = :levelId AND ACTIVATE_FLAG = 1 and HIERACHY_REF_ID = :refId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("pid", Pid.toString());
			lQuery.setParameter("locId",lStrLocCode);
			lQuery.setParameter("levelId",lIntLevelId);
			lQuery.setParameter("refId",refId);
			
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lStrRes = "Y";
			}else{
				lStrRes = "N";
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		
		return lStrRes;
	}
	
	public List getLevelIdForRoles(List lLstRoles) throws Exception
	 {
		 List lLstLevelId = null;
		 Session ghibSession = getSession();
		 
		 try{
			 StringBuilder lSBQuery = new StringBuilder();
			 lSBQuery.append("SELECT LEVEL_ID FROM RLT_LEVEL_ROLE WHERE ROLE_ID IN (:roleId)");
			 Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			 lQuery.setParameterList("roleId", lLstRoles);
			 lLstLevelId = lQuery.list();
		 }catch(Exception e){
			 logger.error(" Error is : " + e, e);
			  throw e; 
		 }
		 return lLstLevelId;
	 }
	
	public Long getUserIdFromPostId(Long lLngPostId)throws Exception
	{
		List lLstResdata = null;
		Long lLngUserId = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT orgUserMst.userId FROM OrgUserpostRlt WHERE orgPostMstByPostId.postId = :postId ");
			lSBQuery.append("AND activateFlag = 1");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("postId", lLngPostId);
			
			lLstResdata = lQuery.list();
			
			if(lLstResdata != null && lLstResdata.size() > 0){
				lLngUserId = (Long) lLstResdata.get(0);
			}
		}catch(Exception e){
			 logger.error(" Error is : " + e, e);
			  throw e; 
		}
		
		return lLngUserId;
	}
	
	public String chkEntryForVerifier(String lStrLocationCode)throws Exception
	{
		List lLstResData = null;
		String lStrVerifierFlag = "";
		
		ArrayList lLstDoc = new ArrayList<Long>();		    
		  
		lLstDoc.add(Long.parseLong("800007"));
		lLstDoc.add(Long.parseLong("800008"));
		lLstDoc.add(Long.parseLong("800009"));
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT WPM.hierachyRefId FROM WfHierachyPostMpg WPM, WfHierarchyReferenceMst WRM ");
			lSBQuery.append("WHERE WPM.levelId = 5 AND WPM.wfOrgLocMpgMst.locId = :locCode ");
			lSBQuery.append("AND WPM.hierachyRefId = WRM.hierachyRefId AND WRM.wfDocMst.docId IN (:docId) ");
			lSBQuery.append("AND WPM.activateFlag = 1 AND WRM.activateFlag = 1");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("locCode", lStrLocationCode);
			lQuery.setParameterList("docId", lLstDoc);
			lLstResData = lQuery.list();
			
			if(lLstResData != null && lLstResData.size() > 0){
				lStrVerifierFlag = "Y";
			}else{
				lStrVerifierFlag = "N";
			}
		}catch(Exception e){
			 logger.error(" Error is : " + e, e);
			  throw e; 
		}
		
		return lStrVerifierFlag;
	}
	
	public String chkEntryForHO(String lStrLocationCode)throws Exception
	{
		List lLstResData = null;
		String lStrHOFlag = "";
		
		ArrayList lLstDoc = new ArrayList<Long>();		    
		  
		lLstDoc.add(Long.parseLong("800007"));
		lLstDoc.add(Long.parseLong("800008"));
		lLstDoc.add(Long.parseLong("800009"));
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT WPM.hierachyRefId FROM WfHierachyPostMpg WPM, WfHierarchyReferenceMst WRM, AclPostroleRlt APR ");
			lSBQuery.append("WHERE WPM.levelId = 20 AND WPM.wfOrgLocMpgMst.locId = :locCode ");
			lSBQuery.append("AND WPM.hierachyRefId = WRM.hierachyRefId AND WRM.wfDocMst.docId IN (:docId) ");
			lSBQuery.append("AND WPM.wfOrgPostMpgMst.postId = APR.orgPostMst.postId AND APR.aclRoleMst.roleId = 800018 ");
			lSBQuery.append("AND WPM.activateFlag = 1 AND WRM.activateFlag = 1 AND APR.cmnLookupMstByActivate.lookupId = 1");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("locCode", lStrLocationCode);
			lQuery.setParameterList("docId", lLstDoc);
			lLstResData = lQuery.list();
			
			if(lLstResData != null && lLstResData.size() > 0){
				lStrHOFlag = "Y";
			}else{
				lStrHOFlag = "N";
			}
		}catch(Exception e){
			 logger.error(" Error is : " + e, e);
			  throw e; 
		}
		
		return lStrHOFlag;
	}
	
	public String getActiveHOLst(String locCode){
		 String lStrHODtls = null;
		 Session ghibSession = getSession();
		 try{
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("SELECT dcps.SEVARTH_ID, dcps.EMP_NAME FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post ");
		lSBQuery.append(" on post.POST_ID = role.POST_ID  ");
		lSBQuery.append(" inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID and usr.ACTIVATE_FLAG = 1");
		lSBQuery.append(" inner join org_emp_mst emp on emp.user_id = usr.user_id ");
		lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID ");
		lSBQuery.append(" where post.LOC_ID = :locCode and role.ROLE_ID = 800002 and role.ACTIVATE_FLAG =1 ");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("locCode", locCode);
			
		if(lQuery != null && lQuery.list().size()> 0)
		{
			Object obj[] = (Object[])lQuery.list().get(0);
			lStrHODtls = obj[1]+"("+obj[0]+")\n\r";
		}
		 }catch(Exception e){
			 logger.error(" Error is : " + e, e);			 
		 }
		 return lStrHODtls;
	}
	
	public String chkRoleHoEntry( String locCode){
		List lLstResData = null;
		String lStrHoRoleFlag = "";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT * FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post ");
			lSBQuery.append(" on role.POST_ID = post.POST_ID and  role.ACTIVATE_FLAG =1 and role.role_id = 800002 ");
			lSBQuery.append(" where post.LOC_ID = "+locCode);
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLstResData = lQuery.list();
			
			if(lLstResData != null && lLstResData.size() > 0){
				lStrHoRoleFlag = "Y";
			}else{
				lStrHoRoleFlag = "N";
			}
		}catch(Exception e){
			 logger.error(" Error is : " + e, e);			 
		}
		
		return lStrHoRoleFlag;
	}
	
	public String getRefIdByDescriptionAndLocId(String locId, String docDescription){
		List lLstResData = null;
		String lStrHoRoleFlag = "";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT HIERACHY_REF_ID FROM WF_HIERARCHY_REFERENCE_MST where LOC_CODE =:locCode "
					+" and DESCRIPTION = :descrption ");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("locCode", locId);
			lQuery.setParameter("descrption", docDescription);
			lLstResData = lQuery.list();
			
			if(lLstResData != null && lLstResData.size() > 0){
				lStrHoRoleFlag = lLstResData.get(0).toString();
			}
		}catch(Exception e){
			 logger.error(" Error is : " + e, e);			 
		}
		
		return lStrHoRoleFlag;
	}
	
	public String getPostIdForLocId(String postId){
		List lLstResData = null;
		String lStrHoRoleFlag = "";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT LOC_ID FROM ORG_POST_DETAILS_RLT where POST_ID = "+postId);
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLstResData = lQuery.list();
			
			if(lLstResData != null && lLstResData.size() > 0){
				lStrHoRoleFlag = lLstResData.get(0).toString();
			}
		}catch(Exception e){
			 logger.error(" Error is : " + e, e);			 
		}
		
		return lStrHoRoleFlag;
	}
	
	public boolean isDDOOrNot(String postId){		
		boolean  roleOfDDO = false;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT * FROM ACL_POSTROLE_RLT where ACTIVATE_FLAG = 1 and ROLE_ID = 700002 and POST_ID = "+postId);
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			
			if(lQuery != null && lQuery.list() != null && lQuery.list().size() > 0){
				roleOfDDO = true;
			}
		}catch(Exception e){
			 logger.error(" Error is : " + e, e);			 
		}
		
		return roleOfDDO;
	}
	
	public boolean lnaActiveDdoOrNot(String lStrDdoCode){		
		boolean  roleOfDDO = false;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ddo_code FROM hr_lna_active_ddo where DDO_CODE = :ddoCode and activate_flag = 1 ");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			
			if(lQuery != null && lQuery.list() != null && lQuery.list().size() > 0){
				String ddo_code = lQuery.uniqueResult().toString();
				roleOfDDO = true;
			}
		}catch(Exception e){
			 logger.error(" Error is : " + e, e);			 
		}
		
		return roleOfDDO;
	}
	
}