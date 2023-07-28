package com.tcs.sgv.gpf.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.dao.AclMstRoleDao;
import com.tcs.sgv.acl.dao.AclMstRoleDaoImpl;
import com.tcs.sgv.acl.valueobject.AclPostroleRlt;
import com.tcs.sgv.acl.valueobject.AclRoleMst;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.dao.CmnDatabaseMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLookupMstDAOImpl;
import com.tcs.sgv.common.dao.UserConfigDAOImpl;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.valueobject.CmnDatabaseMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.ess.dao.OrgPostMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgUserMstDaoImpl;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
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

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Jul 12, 2012
 */

public class GPFWorkFlowConfigDAOImpl extends GenericDaoHibernateImpl implements GPFWorkFlowConfigDAO
{
	Session ghibSession = null;
	public GPFWorkFlowConfigDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public String getDdoCode(Long lLngPostId)throws Exception
	{
		List lLstResData = null;
		String lStrDdoCode = "";
		
		
		logger.error(" lLngPostId: " +lLngPostId);
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ddoCode FROM OrgDdoMst WHERE postId =:postId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("postId", lLngPostId);
			lLstResData = lQuery.list();
			
			if(lLstResData != null && lLstResData.size() > 0){
				lStrDdoCode = lLstResData.get(0).toString();
				logger.error(" lStrDdoCode: " +lStrDdoCode);
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lStrDdoCode;
	}
	
	
	public List getAllUsersForDDO(String lStrDdoCode,String gStrLocationCode)throws Exception
	{
		List lLstAllUsers = null;
		logger.error(" lStrDdoCode: " +lStrDdoCode);
		logger.error(" gStrLocationCode: " +gStrLocationCode);
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			
			/*lSBQuery.append(" SELECT ME.name, OUPR.orgPostMstByPostId.postId, OUM.userName ");
			lSBQuery.append(" FROM MstEmp ME, OrgEmpMst OEM, OrgUserpostRlt OUPR, OrgUserMst OUM ");
			lSBQuery.append(" WHERE ME.orgEmpMstId = OEM.empId AND OEM.orgUserMst.userId = OUPR.orgUserMst.userId AND ME.ddoCode = :ddoCode ");
			lSBQuery.append(" AND OUPR.activateFlag = 1 AND OEM.orgUserMst.userId = OUM.userId ");
			*/
			

			lSBQuery.append(" SELECT ME.EMP_NAME, OUPR.post_id, OUM.USER_NAME,grd.GRADE_NAME,dg.DSGN_NAME ");
			lSBQuery.append(" FROM MST_DCPS_EMP ME, ORG_EMP_MSt OEM, ORG_USERPOST_RLT OUPR, ORG_USER_MST OUM, ORG_GRADE_MST grd,ORG_DESIGNATION_MST dg,ORG_POST_DETAILS_RLT detail ");
			lSBQuery.append(" WHERE ME.ORG_EMP_MST_ID = OEM.EMP_ID AND OEM.USER_ID= OUPR.USER_ID AND ME.DDO_CODE = :ddoCode and oem.GRADE_ID=grd.GRADE_ID ");
			//lSBQuery.append(" AND OUPR.ACTIVATE_FLAG = 1 AND OEM.USER_ID = OUM.USER_ID and detail.POST_ID=OUPR.POST_ID and detail.DSGN_ID = dg.DSGN_ID and detail.LOC_ID=:gStrLocationCode and OEM.GRADE_ID <>100067 order by ME.EMP_NAME asc ");
			lSBQuery.append(" AND OUPR.ACTIVATE_FLAG = 1 AND OEM.USER_ID = OUM.USER_ID and detail.POST_ID=OUPR.POST_ID and detail.DSGN_ID = dg.DSGN_ID and detail.LOC_ID=:gStrLocationCode  order by ME.EMP_NAME asc ");
		 
							 
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lQuery.setParameter("gStrLocationCode", Long.parseLong(gStrLocationCode));
			
			lLstAllUsers = lQuery.list();
			
			logger.error(" lSBQuerygetAllUsersForDDO: " +lSBQuery);
			
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lLstAllUsers;
	}
	
	/*//swt 21/12/2020
	public List getEmpForDDO(String lStrSevaarthId,String lStrEmployeeName,String lStrDdoCode,String lStrRoleId)throws Exception
	{
		List lLstAllUsers = null;
		logger.error(" lStrDdoCode: " +lStrDdoCode);
		logger.error(" lStrSevaarthId: " +lStrSevaarthId);
		logger.error(" lStrEmployeeName: " +lStrEmployeeName);
		//logger.error(" lStroleid: " +lStroleid);
		//logger.error(" gStrLocationCode: " +gStrLocationCode);
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
		if(lStrRoleId.equalsIgnoreCase("800001")||lStrRoleId.equalsIgnoreCase("8000016")||lStrRoleId.equalsIgnoreCase("800005")||lStrRoleId.equalsIgnoreCase("800002")){
					lSBQuery.append(" SELECT dcps.SEVARTH_ID,dcps.EMP_NAME,TO_CHAR(emp.EMP_DOJ,'YYYY-MM-DD'),TO_CHAR(emp.EMP_SRVC_EXP,'YYYY-MM-DD'),dg.DSGN_NAME,grd.GRADE_NAME,(CASE WHEN RP.ROLE_ID='800002' then 'Y' else 'N' end) as HO, ");
					lSBQuery.append(" (CASE WHEN RP.ROLE_ID='800001' then 'Y' else 'N' end) as Verifier,(CASE WHEN RP.ROLE_ID='800005' then 'Y' else 'N' end) as DEO,(CASE WHEN RP.ROLE_ID='8000016' then 'Y' else 'N' end) as RHO ");
					lSBQuery.append(" FROM ACL_POSTROLE_RLT role  inner join ORG_POST_DETAILS_RLT post on post.POST_ID = role.POST_ID  inner join ACL_ROLE_DETAILS_RLT rp on rp.ROLE_ID=role.role_id   inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID and usr.ACTIVATE_FLAG = 1 ");
					lSBQuery.append(" inner join org_user_mst user on user.user_id=usr.user_id inner join org_emp_mst emp on emp.user_id = usr.user_id   inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID  inner join ORG_GRADE_MST grd on emp.GRADE_ID=grd.GRADE_ID ");
					lSBQuery.append(" inner join ORG_DESIGNATION_MST dg on dg.DSGN_ID = dcps.DESIGNATION where post.LOC_ID = '"+lStrDdoCode+"' and role.ROLE_ID in(800001,8000016,800005,800002) and role.ACTIVATE_FLAG =1 ");
			}else{
			
		lSBQuery.append(" SELECT dcps.SEVARTH_ID,dcps.EMP_NAME,TO_CHAR(OEM.EMP_DOJ,'YYYY-MM-DD'),TO_CHAR(OEM.EMP_SRVC_EXP,'YYYY-MM-DD'),dg.DSGN_NAME,GRD.GRADE_NAME ");
			lSBQuery.append(" FROM MST_DCPS_EMP dcps, ORG_EMP_MSt OEM, ORG_USERPOST_RLT OUPR, ORG_USER_MST OUM, ORG_GRADE_MST grd,ORG_DESIGNATION_MST dg,ORG_POST_DETAILS_RLT detail ");
			lSBQuery.append(" WHERE dcps.ORG_EMP_MST_ID = OEM.EMP_ID AND OEM.USER_ID= OUPR.USER_ID AND dcps.DDO_CODE = '"+lStrDdoCode+"' and oem.GRADE_ID=grd.GRADE_ID ");
			//lSBQuery.append(" WHERE ME.ORG_EMP_MST_ID = OEM.EMP_ID AND OEM.USER_ID= OUPR.USER_ID and oem.GRADE_ID=grd.GRADE_ID ");
			lSBQuery.append(" AND OUPR.ACTIVATE_FLAG = 1 AND OEM.USER_ID = OUM.USER_ID and detail.POST_ID=OUPR.POST_ID and detail.DSGN_ID = dg.DSGN_ID ");
			}
			if(!lStrSevaarthId.isEmpty() ) {

				//lSBQuery.append(" and (dcps.SEVARTH_ID='"+lStrSevaarthId+"' OR ME.SEVARTH_ID='"+lStrSevaarthId+"') ");
				lSBQuery.append(" and dcps.SEVARTH_ID='"+lStrSevaarthId+"' ");
				
				Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				//lQuery.setParameter("sevaarthId", lStrSevaarthId);


			} if (!lStrEmployeeName.isEmpty() ) {

				//lSBQuery.append(" and (dcps.EMP_NAME='"+lStrEmployeeName+"' OR ME.EMP_NAME='"+lStrEmployeeName+"') ");
				lSBQuery.append(" and dcps.EMP_NAME='"+lStrEmployeeName+"' ");

					Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				//lQuery.setParameter("employeeName", lStrEmployeeName);
			} 
			
						 
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			lLstAllUsers = lQuery.list();
			
			logger.error(" lSBQuerygetAllUsersForDDO: " +lSBQuery);
			
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lLstAllUsers;
	}
	*/
	
	public List getAllRoles()throws Exception
	{
		List lLStAllRoles = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			/*lSBQuery.append("SELECT roleId, roleDesc FROM RltLevelRole WHERE moduleName = 'GPF' ");
			lSBQuery.append("AND roleId IN(800005,800001,800002) AND ");*/
			
			lSBQuery.append(" SELECT * FROM RLT_LEVEL_ROLE WHERE MODULE_NAME = 'GPF' "); 
			lSBQuery.append(" AND ROLE_ID IN(800005,800001,800002,8000016)order by ROLE_ID ");
		
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLStAllRoles = lQuery.list();
			
			logger.error(" lSBQuerygetAllRoles: " +lSBQuery);
			
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
		logger.error(" lSBQuerygetAllRolesForUser: " +lLngPostId);
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
			
			logger.error(" lSBQuerygetAllRolesForUser: " +lSBQuery);
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lStrRolesForUsr;
	}
	
	
	public String assignRolesToUser(Long lLngUsrPostId,List lLstSelctdRoles,Long lLngCtrdUsr, 
			Long lLngCrtdPost, Map inputMap)throws Exception
	{
		
		logger.error("assignRolesToUser");
		logger.error("lLngUsrPostId"+lLngUsrPostId);
		logger.error("lLngCtrdUsr"+lLngCtrdUsr);
		logger.error("lLngCrtdPost"+lLngCrtdPost);
		logger.error("lLstSelctdRoles"+lLstSelctdRoles.toString());
		
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
			
			allRolesGPF.add(Long.parseLong("800001"));
			allRolesGPF.add(Long.parseLong("800002"));
			allRolesGPF.add(Long.parseLong("800005"));
			
			logger.error("allRolesGPF Vivek "+allRolesGPF);
			
			if (!resList.isEmpty()) 
			{
				for(Object[] tuple:resList)
				{ 
					logger.error("resRoleId 1 Vivek ");
					Long resRoleId = Long.parseLong(tuple[0].toString());
					Long resActFlag = Long.parseLong(tuple[1].toString());
					logger.error("resRoleId 1 Vivek "+resRoleId);
					logger.error("resActFlag 1 Vivek "+resActFlag);
					if(resActFlag.equals(Long.valueOf("1")))
					{
						logger.error("resRoleId Vivek "+resRoleId);
						if(allRolesGPF.contains(resRoleId)){
							logger.error("allRolesGPF Vivek ");
							allActLevels.add(resRoleId);        //List of all active flag with Pid
						}
					}
				}
				
				logger.error("lLstSelctdRoles Vivek "+lLstSelctdRoles.size());
				logger.error("lLstSelctdRoles Vivek "+lLstSelctdRoles);
				if(lLstSelctdRoles != null && lLstSelctdRoles.size() > 0){
					logger.error("lLstSelctdRoles Vivek ");
					allActLevels.removeAll(lLstSelctdRoles);
				}
					
				for(Object[] tuple:resList)
				{
					logger.error("resRoleId 2 Vivek ");
					Long resRoleId = Long.parseLong(tuple[0].toString());
					Long resActFlag = Long.parseLong(tuple[1].toString());
					logger.error("resRoleId 2 Vivek "+resRoleId);
					logger.error("resActFlag 2 Vivek "+resActFlag);
					if(resActFlag.equals(Long.valueOf("2")))
					{
						logger.error("resRoleId 2 Vivek244 "+resRoleId);
						if (lLstSelctdRoles.contains(resRoleId)) 
						{
							logger.error("resRoleId 2 Vivek "+resRoleId);
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
			
			logger.error("updateRoleFlag 1 Vivek "+updateRoleFlag);
			logger.error("updateRoleFlag 1 Vivek "+updateRoleFlag.size());
			logger.error("allActLevels 1 Vivek "+allActLevels);
			logger.error("allActLevels 1 Vivek "+allActLevels.size());
			logger.error("lLstSelctdRoles 1 Vivek "+lLstSelctdRoles);
			logger.error("lLstSelctdRoles 1 Vivek "+lLstSelctdRoles.size());
			if(updateRoleFlag != null && updateRoleFlag.size() > 0){
				logger.error("updateAcvtFlgForPostRoleRlt 2 Vivek "+updateRoleFlag);
				updateAcvtFlgForPostRoleRlt(lLngUsrPostId,updateRoleFlag,lLngCtrdUsr,lLngCrtdPost);
				lStrResData = lStrResData + "I";
			}
			
			//Commented By Vivek Sharma 17 June 2017
			/*if(allActLevels != null && allActLevels.size() > 0){
				logger.error("removeRoleFromPostRoleRlt 2 Vivek "+allActLevels);
				removeRoleFromPostRoleRlt(lLngUsrPostId,allActLevels,lLngCtrdUsr,lLngCrtdPost);
				lStrResData = lStrResData + "R";
			}*/
			
			if(lLstSelctdRoles != null && lLstSelctdRoles.size() > 0)
			{
				logger.error("addNewEntryInPostRole 2 Vivek "+lLstSelctdRoles);
				addNewEntryInPostRole(lLngUsrPostId,lLstSelctdRoles,lLngCtrdUsr,lLngCrtdPost,inputMap);
				lStrResData = lStrResData + "I";
			}
			
			logger.error(" lSBQueryassignRolesToUser: " +lSBQuery);
			
		}
		
		catch(Exception e){
			logger.error(" Error is : " + e, e);
			logger.error("PostId is===============>>>"+lLngUsrPostId);
			throw e;
		}
		
		return lStrResData;
	}
	
	
	public void updateAcvtFlgForPostRoleRlt(Long lLngUsrPostId, List lLstRoles, Long lLngUpdtUsrId, Long lLngUpdtPostId)throws Exception
	{
		try{
			logger.error(" ##############updateAcvtFlgForPostRoleRlt############ ");
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
			
			logger.error(" lSBQueryupdateAcvtFlgForPostRoleRlt: " +lSBQuery);
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}
	
	
	public void removeRoleFromPostRoleRlt(Long lLngUsrPostId, List lLstRoles, Long lLngUpdtUsrId, Long lLngUpdtPostId)throws Exception
	{		
		try{
			logger.error(" ##############removeRoleFromPostRoleRlt############ ");
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
			
			logger.error(" lSBQueryremoveRoleFromPostRoleRlt: " +lSBQuery);
			
			
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}
	
	
	public void addNewEntryInPostRole(Long lLngUsrPostId, List lLstRoles, Long lLngCrtdUsrId, Long lLngCrtdPostId, Map inputMap)throws Exception
	{
		
		logger.error(" ##############addNewEntryInPostRole############ ");
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
			}
			
		
		}catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}
	
	
	public String insertDataForWorkflow(String locCode, Long lLngPostId, Long langId, List arrLevelid,Map objectArgs,Long updUserId,Long uptPostId) throws Exception
	{			
		
		logger.error(" insertDataForWorkflow:::::::::::::: ");
		String strUserLevelOuter ="";
		String strRes = "";
		ArrayList delLevelId = new ArrayList();
		ArrayList descList = new ArrayList();
		ArrayList arrRoleId = new ArrayList();
		ArrayList astLevelId = new ArrayList();
		List astPostId = null;
		Long lLngUserId = null;
		
		logger.error(" locCode "+locCode);
		logger.error(" lLngPostId  pid "+lLngPostId);
		logger.error(" langId "+langId);
		logger.error(" updUserId "+updUserId);
		logger.error(" uptPostId "+uptPostId);
		logger.error(" arrLevelid "+arrLevelid);	
		
		try {
			StringBuffer sbLevel = new StringBuffer();
			
			sbLevel.append(
				"select distinct wp.levelId,wp.activateFlag " + 
				"  from WfHierachyPostMpg wp , WfHierarchyReferenceMst wr " + 
				" where  wr.wfDocMst.docId IN (800001,800002,800003) AND " + 
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
					logger.error(" insertDataForWorkflow 429 ");
					for(Object[] tuple:resList)
					{ /*add all active levels to an array*/
						logger.error(" insertDataForWorkflow 432 ");
						Integer resLevelId = Integer.parseInt(tuple[0].toString());
						Long resActFlag = Long.parseLong(tuple[1].toString());
						if(resActFlag.equals(Long.valueOf("1"))){
							allActLevels.add(resLevelId);
						}
					}
					/*check whether the role is already assigned to the level and now is unchecked so needs to be deactivated */						
					if(arrLevelid !=null){
						logger.error(" insertDataForWorkflow 441 ");
						allActLevels.removeAll(arrLevelid);
					}
					
					for(Object[] tuple:resList)
					{
						logger.error(" insertDataForWorkflow 447 ");
						Integer resLevelId = Integer.parseInt(tuple[0].toString());
						Long resActFlag = Long.parseLong(tuple[1].toString());
						logger.error(" insertDataForWorkflow 450 "+arrLevelid);
						if(arrLevelid != null)
						{
							logger.error(" insertDataForWorkflow 453 ");
							if(resActFlag.equals(Long.valueOf("2")))
							{
								logger.error(" insertDataForWorkflow 456 ");
								if (arrLevelid.contains(resLevelId)) {
									logger.error(" resLevelId&&&&&&&&&&&&&&&&&&&& "+resLevelId);
									updateLevelFlag.add(resLevelId);
									
									
								}
							}
							/*else 
							{
							check whether the role is already assigned to the level 		
								if(arrLevelid.contains(resLevelId)){
									arrLevelid.remove(resLevelId);
								}
							}*/
						}
					
					}
					if(arrLevelid !=null)
					{	
						logger.error(" insertDataForWorkflow 476 ");
						logger.error(" insertDataForWorkflow 477 "+updateLevelFlag);
						if(updateLevelFlag != null && updateLevelFlag.size() >0){
							logger.error(" insertDataForWorkflow 479 ");
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
					//strUserLevelOuter = addPostInHierarchyMDC(locCode,lLngPostId,updUserId,objectArgs,(ArrayList) arrLevelid,refId);
					strRes += "I";
				}
				
				logger.error(" sbLevel: " +sbLevel);
				
		} catch (Exception e) {			
			logger.error(" Error is : " + e, e);
			logger.error("PostId is===============>>>"+lLngPostId);
			throw e;
		}		
			
		return strRes;
	}
	
	public String insertDataForWorkflowMDC(String locCode, Long lLngPostId, Long langId, List arrLevelid,Map objectArgs,Long updUserId,Long uptPostId) throws Exception
	{			
		
		logger.error(" insertDataForWorkflow:::::::::::::: ");
		String strUserLevelOuter ="";
		String strRes = "";
		ArrayList delLevelId = new ArrayList();
		ArrayList descList = new ArrayList();
		ArrayList arrRoleId = new ArrayList();
		ArrayList astLevelId = new ArrayList();
		List astPostId = null;
		Long lLngUserId = null;
		
		logger.error(" locCode "+locCode);
		logger.error(" lLngPostId  pid "+lLngPostId);
		logger.error(" langId "+langId);
		logger.error(" updUserId "+updUserId);
		logger.error(" uptPostId "+uptPostId);
		logger.error(" arrLevelid "+arrLevelid);	
		
		try {
			StringBuffer sbLevel = new StringBuffer();
			
			sbLevel.append(
				"select distinct wp.levelId,wp.activateFlag " + 
				"  from WfHierachyPostMpg wp , WfHierarchyReferenceMst wr " + 
				" where  wr.wfDocMst.docId IN (800001,800002,800003) AND " + 
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
					logger.error(" insertDataForWorkflow 429 ");
					for(Object[] tuple:resList)
					{ /*add all active levels to an array*/
						logger.error(" insertDataForWorkflow 432 ");
						Integer resLevelId = Integer.parseInt(tuple[0].toString());
						Long resActFlag = Long.parseLong(tuple[1].toString());
						if(resActFlag.equals(Long.valueOf("1"))){
							allActLevels.add(resLevelId);
						}
					}
					/*check whether the role is already assigned to the level and now is unchecked so needs to be deactivated */						
					if(arrLevelid !=null){
						logger.error(" insertDataForWorkflow 441 ");
						allActLevels.removeAll(arrLevelid);
					}
					
					for(Object[] tuple:resList)
					{
						logger.error(" insertDataForWorkflow 447 ");
						Integer resLevelId = Integer.parseInt(tuple[0].toString());
						Long resActFlag = Long.parseLong(tuple[1].toString());
						logger.error(" insertDataForWorkflow 450 "+arrLevelid);
						if(arrLevelid != null)
						{
							logger.error(" insertDataForWorkflow 453 ");
							if(resActFlag.equals(Long.valueOf("2")))
							{
								logger.error(" insertDataForWorkflow 456 ");
								if (arrLevelid.contains(resLevelId)) {
									logger.error(" resLevelId&&&&&&&&&&&&&&&&&&&& "+resLevelId);
									updateLevelFlag.add(resLevelId);
									
									
								}
							}
							/*else 
							{
							check whether the role is already assigned to the level 		
								if(arrLevelid.contains(resLevelId)){
									arrLevelid.remove(resLevelId);
								}
							}*/
						}
					
					}
					if(arrLevelid !=null)
					{	
						logger.error(" insertDataForWorkflow 476 ");
						logger.error(" insertDataForWorkflow 477 "+updateLevelFlag);
						if(updateLevelFlag != null && updateLevelFlag.size() >0){
							logger.error(" insertDataForWorkflow 479 ");
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
					//strUserLevelOuter = addPostInHierarchy(locCode,lLngPostId,updUserId,objectArgs,(ArrayList) arrLevelid,refId);
					strUserLevelOuter = addPostInHierarchyMDC(locCode,lLngPostId,updUserId,objectArgs,(ArrayList) arrLevelid,refId);
					strRes += "I";
				}
				
				logger.error(" sbLevel: " +sbLevel);
				
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
			
			
			lLstDocId.add(Long.parseLong("800001"));
			lLstDocId.add(Long.parseLong("800002"));
			lLstDocId.add(Long.parseLong("800003"));
			
			
			lLngMaxRefId = getMaxRefId();
			
			lLngHrSeqNo = lLngMaxRefId;
			if(chkEntryWfHrfMst(locCode,lLstDocId) == "N")
			{
				for(int i=0; i<lLstDocId.size(); i++)
				{
					lObjWfDocMst = new com.tcs.sgv.fms.valueobject.WfDocMst();
					lObjWfDocMst = (com.tcs.sgv.fms.valueobject.WfDocMst)lObjWfDocMstDao.read(Long.parseLong(lLstDocId.get(i).toString()));	
					lStrDocDesc = getDocDesc((Long) lLstDocId.get(i));				
					
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
					lLngHrSeqNo++;
				}
			}
		} catch (Exception e) {			
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}
	
	public Long getMaxRefId()throws Exception
	{
		
		logger.info("getMaxRefId");
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
		
		logger.info("chkEntryWfHrfMst");
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
		
		logger.info("getDocDesc");
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
		  
		  
		  lLstDoc.add(Long.parseLong("800001"));
		  lLstDoc.add(Long.parseLong("800002"));
		  lLstDoc.add(Long.parseLong("800003"));
		  
		  try{
			  StringBuffer sbRefId = new StringBuffer();
		  	  sbRefId.append("select distinct wm.hierachyRefId " +
						"  from WfHierarchyReferenceMst wm" +
						" where wm.locationCode = :locCode" +
						" and wm.wfDocMst.docId in (:DocList)");
						
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

		logger.error("delLevelEntriesWF");
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
		logger.error("updateActFlagForPostWF");
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
		logger.error("in addPostInHierarchy************************"+locCode);
		logger.error("lLngPostId::::::::"+lLngPostId);
		logger.error("updUserId::::::::"+updUserId);
		logger.error("levelId::::::::"+levelId);
		logger.error("refId::::::::"+refId);
		String resString="";		
		UserConfigDAOImpl WfHierachyVO = new UserConfigDAOImpl(WfHierachyPostMpg.class,this.getSessionFactory());
		Long lLngUserId = null;
		
		try{
			lLngUserId = getUserIdFromPostId(lLngPostId);
			
			if(chkEntryWfOrgPost(lLngPostId.toString()) == "N"){
				addEntryWfOrgPost(lLngPostId.toString());
			}
			if(chkEntryWfOrgPost(lLngUserId.toString()) == "N"){
				addEntryWfOrgPost(lLngUserId.toString());
			}
			if(chkEntryWfOrgPost(updUserId.toString()) == "N"){
				addEntryWfOrgPost(updUserId.toString());
			}
			if(chkEntryWfOrgLoc(locCode) == "N"){
				addEntryWfOrgLoc(locCode);
			}
			
			
			
			if(chkEntryWfOrgUser(lLngUserId) == "N"){
				addEntryWfOrgUser(lLngUserId);
			}
			if(chkEntryWfOrgUser(updUserId) == "N"){
				addEntryWfOrgUser(updUserId);
			}
			if(chkEntryWfOrgUser(lLngPostId) == "N"){
				addEntryWfOrgUser(lLngPostId);
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
				  if(chkEntryWfHrPost(lLngPostId, locCode, Integer.parseInt(levelId.get(i).toString())) == "N")
				  {
					  logger.info("chkEntryWfHrPost::::::::::::::::::::"+chkEntryWfHrPost(lLngPostId, locCode, Integer.parseInt(levelId.get(i).toString())));
				  	startSeqNo  = IFMSCommonServiceImpl.getNextSeqNum("wf_hierachy_post_mpg", objectArgs);
					logger.info("startSeqNo:::::::::::::"+startSeqNo);  				 
			  		WfHierachyPostMpg wfHiePostMpg = new WfHierachyPostMpg();
					wfHiePostMpg.setHierachySeqId(startSeqNo);
					wfHiePostMpg.setParentHierachy(null);
					wfHiePostMpg.setWfOrgPostMpgMst(lwfOrgPostMpgMst);
					wfHiePostMpg.setLevelId(Integer.parseInt(levelId.get(i).toString()));
					wfHiePostMpg.setHierachyRefId(refId.get(0));
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
					//System.out.println("wfHiePostMpg-->"+wfHiePostMpg);
					hibSession.save(wfHiePostMpg);
  				    hibSession.flush();	
					logger.info("end::::::::::::::::::::");
				  }
			  }
			  resString="I";
			  
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		
		return resString;
	}
	
	public String addPostInHierarchyMDC(String locCode,Long lLngPostId,Long updUserId,Map objectArgs,ArrayList levelId,
			ArrayList<Long> refId) throws Exception
	{
		logger.error("in addPostInHierarchy************************"+locCode);
		logger.error("lLngPostId::::::::"+lLngPostId);
		logger.error("updUserId::::::::"+updUserId);
		logger.error("levelId::::::::"+levelId);
		logger.error("refId::::::::"+refId);
		String resString="";		
		UserConfigDAOImpl WfHierachyVO = new UserConfigDAOImpl(WfHierachyPostMpg.class,this.getSessionFactory());
		Long lLngUserId = null;
		
		try{
			
			lLngUserId = getUserIdFromPostId(lLngPostId);
			
			if(chkEntryWfOrgPost(lLngPostId.toString()) == "N"){
				addEntryWfOrgPost(lLngPostId.toString());
			}
			if(chkEntryWfOrgPost(lLngUserId.toString()) == "N"){
				addEntryWfOrgPost(lLngUserId.toString());
			}
			if(chkEntryWfOrgPost(updUserId.toString()) == "N"){
				addEntryWfOrgPost(updUserId.toString());
			}
			
			if(chkEntryWfOrgLoc(locCode) == "N"){
				addEntryWfOrgLoc(locCode);
			}
			
			
			
			if(chkEntryWfOrgUser(lLngUserId) == "N"){
				addEntryWfOrgUser(lLngUserId);
			}
			if(chkEntryWfOrgUser(updUserId) == "N"){
				addEntryWfOrgUser(updUserId);
			}
			if(chkEntryWfOrgUser(lLngPostId) == "N"){
				addEntryWfOrgUser(lLngPostId);
			}
			
			if(levelId != null && levelId.size()>0)
			{
			  Session ghibSession = getSession();
			
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
				  if(chkEntryWfHrPostMDC(lLngPostId, locCode, Integer.parseInt(levelId.get(i).toString())) == "N")
				  {
					  //logger.info("chkEntryWfHrPost::::::::::::::::::::"+chkEntryWfHrPost(lLngPostId, locCode, Integer.parseInt(levelId.get(i).toString())));
				  	startSeqNo  = IFMSCommonServiceImpl.getNextSeqNum("wf_hierachy_post_mpg", objectArgs);
					logger.info("startSeqNo:::::::::::::"+startSeqNo);  				 
			  		WfHierachyPostMpg wfHiePostMpg = new WfHierachyPostMpg();
					wfHiePostMpg.setHierachySeqId(startSeqNo);
					wfHiePostMpg.setParentHierachy(null);
					wfHiePostMpg.setWfOrgPostMpgMst(lwfOrgPostMpgMst);
					wfHiePostMpg.setLevelId(Integer.parseInt(levelId.get(i).toString()));
					wfHiePostMpg.setHierachyRefId(refId.get(0));
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
					System.out.println("wfHiePostMpg-->"+wfHiePostMpg);
					ghibSession.save(wfHiePostMpg);
  				    ghibSession.flush();	
					logger.info("end::::::::::::::::::::");
				  }
			  }
			  resString="I";
			  
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		
		return resString;
	}
	
	
	
	public String chkEntryWfOrgPost(String Pid)throws Exception
	{
		logger.error("chkEntryWfOrgPost");
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
		logger.error("addEntryWfOrgPost************************"+Pid);
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
		
		logger.error("chkEntryWfOrgLoc");
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
		
		logger.error("addEntryWfOrgLoc");
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
		logger.error("chkEntryWfOrgUser::::::::::::::::::::::"+lLngUserId);
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
		logger.error("addEntryWfOrgUser");
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
	
	public String chkEntryWfHrPost(Long Pid,String lStrLocCode,Integer lIntLevelId) throws Exception
	{
		logger.error("chkEntryWfHrPost");
		List lLstResData = null;
		String lStrRes = "";
		Session ghibSession = getSession();
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			
			lSBQuery.append(" SELECT post.HIERACHY_SEQ_ID FROM WF_HIERACHY_POST_MPG post inner join WF_HIERARCHY_REFERENCE_MST ref ");
			lSBQuery.append(" on post.HIERACHY_REF_ID = ref.HIERACHY_REF_ID ") ;
			lSBQuery.append(" WHERE post.POST_ID = :pid AND post.LOC_ID = :locId  ");
			lSBQuery.append(" AND post.LEVEL_ID = :levelId AND post.ACTIVATE_FLAG = 1 and ref.doc_Id IN (800001,800002,800003) and ref.ACTIVATE_FLAG =1 ");
			
					
			/*lSBQuery.append("SELECT * FROM WF_HIERACHY_POST_MPG WHERE POST_ID = :pid AND LOC_ID = :locId \n");
			lSBQuery.append("AND LEVEL_ID = :levelId AND ACTIVATE_FLAG = 1");*/
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("pid", Pid.toString());
			lQuery.setParameter("locId",lStrLocCode);
			lQuery.setParameter("levelId",lIntLevelId);
			
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
	
	public String chkEntryWfHrPostMDC(Long Pid,String lStrLocCode,Integer lIntLevelId) throws Exception
	{
		logger.error("chkEntryWfHrPost");
		List lLstResData = null;
		String lStrRes = "";
		Session ghibSession = getSession();
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			
			lSBQuery.append(" SELECT DISTINCT post.HIERACHY_SEQ_ID FROM WF_HIERACHY_POST_MPG post inner join WF_HIERARCHY_REFERENCE_MST ref ");
			lSBQuery.append(" on post.HIERACHY_REF_ID = ref.HIERACHY_REF_ID ") ;
			lSBQuery.append(" WHERE post.POST_ID = :pid AND post.LOC_ID = :locId  ");
			lSBQuery.append(" AND post.LEVEL_ID = :levelId and ref.doc_Id IN (800001,800002,800003) and ref.ACTIVATE_FLAG =1 ");
			
					
			/*lSBQuery.append("SELECT * FROM WF_HIERACHY_POST_MPG WHERE POST_ID = :pid AND LOC_ID = :locId \n");
			lSBQuery.append("AND LEVEL_ID = :levelId AND ACTIVATE_FLAG = 1");*/
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("pid", Pid.toString());
			lQuery.setParameter("locId",lStrLocCode);
			lQuery.setParameter("levelId",lIntLevelId);
			
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
	
	//swt 08/01/2021
			public String getLevelIdForRolesMDC(String lLstRoles) throws Exception {

				StringBuilder lSBQuery = new StringBuilder();
				List lLstLevelList = null;
				String lLstLevelId = "";
				try {
					lSBQuery.append(" SELECT LEVEL_ID FROM RLT_LEVEL_ROLE WHERE ROLE_ID ='"+lLstRoles+"' ");
					//Query lQuery = ghibSession.createQuery(lSBQuery.toString());
					Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

					lLstLevelList = lQuery.list();
					if(lLstLevelList != null && lLstLevelList.size() > 0){
					
						lLstLevelId = lLstLevelList.get(0).toString();
					}
					
					//lStrDdoCode = lLstCodeList.get(0).toString();
				} catch (Exception e) {
					logger.error("Exception in getDdoCode of GPFRequestProcessDAOImpl  : ", e);			
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
	
	public String getUsername(Long lLngUserId)throws Exception
	{
		List lLstResdata = null;
		String lStrUsername = "";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT user.USER_NAME FROM ORG_USER_MST user where user.USER_ID=:lLngUserId ");
						
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("lLngUserId", lLngUserId);
			
			lLstResdata = lQuery.list();
			
			if(lLstResdata != null && lLstResdata.size() > 0){
				lStrUsername = lLstResdata.get(0).toString();
			}
		}catch(Exception e){
			 logger.error(" Error is : " + e, e);
			  throw e; 
		}
		
		return lStrUsername;
	}
	
	public String chkEntryForVerifier(String lStrLocationCode)throws Exception
	{
		List lLstResData = null;
		String lStrVerifierFlag = "";
		
		ArrayList lLstDoc = new ArrayList<Long>();		    
		  
		lLstDoc.add(Long.parseLong("800001"));
		lLstDoc.add(Long.parseLong("800002"));
		lLstDoc.add(Long.parseLong("800003"));
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT WPM.hierachyRefId FROM WfHierachyPostMpg WPM, WfHierarchyReferenceMst WRM ");
			lSBQuery.append("WHERE WPM.levelId = 20 AND WPM.wfOrgLocMpgMst.locId = :locCode ");
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
		logger.error("chkEntryForHO");
		List lLstResData = null;
		String lStrHOFlag = "";
		
		ArrayList lLstDoc = new ArrayList<Long>();		    
		  
		lLstDoc.add(Long.parseLong("800001"));
		lLstDoc.add(Long.parseLong("800002"));
		lLstDoc.add(Long.parseLong("800003"));
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT WPM.hierachyRefId FROM WfHierachyPostMpg WPM, WfHierarchyReferenceMst WRM, AclPostroleRlt APR ");
			lSBQuery.append("WHERE WPM.levelId = 30 AND WPM.wfOrgLocMpgMst.locId = :locCode ");
			lSBQuery.append("AND WPM.hierachyRefId = WRM.hierachyRefId AND WRM.wfDocMst.docId IN (:docId) ");
			lSBQuery.append("AND WPM.wfOrgPostMpgMst.postId = APR.orgPostMst.postId AND APR.aclRoleMst.roleId = 800002 ");
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
	
	public String chkRoleHoEntry(long postId, String locCode) throws Exception{

		logger.error("chkRoleHoEntry");
		
		List lLstResData = null;
		String lStrHoRoleFlag = "";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT post.POST_ID FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post ");
			lSBQuery.append(" on role.POST_ID = post.POST_ID and  role.ACTIVATE_FLAG =1 and role.role_id = 800002 ");
			lSBQuery.append(" where post.LOC_ID = "+locCode);
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLstResData = lQuery.list();
			
			if(lLstResData != null && lLstResData.size() > 0){
				lStrHoRoleFlag = lQuery.list().get(0).toString();
			}else{
				lStrHoRoleFlag = "N";
			}
		}catch(Exception e){
			 logger.error(" Error is : " + e, e);
			  throw e; 
		}
		
		return lStrHoRoleFlag;
	}
	
	public String chkHOOrNot(String postId) throws Exception{
		
		logger.error("chkHOOrNot");
		List lLstResData = null;
		String lStrHoRoleFlag = "";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT * FROM ACL_POSTROLE_RLT role ");
			lSBQuery.append(" where role.ACTIVATE_FLAG =1 and role.role_id = 800002 ");
			lSBQuery.append("  and role.POST_ID =  "+postId);
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLstResData = lQuery.list();
			
			if(lLstResData != null && lLstResData.size() > 0){
				lStrHoRoleFlag = "Y";
			}else{
				lStrHoRoleFlag = "N";
			}
		}catch(Exception e){
			 logger.error(" Error is : " + e, e);
			  throw e; 
		}
		
		return lStrHoRoleFlag;
	}
	
	/*public String getActiveHOLst(String locCode){
		

		logger.info("getActiveHOLst");
		 String lStrHODtls = null;
		 Session ghibSession = getSession();
		 try{
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("SELECT dcps.SEVARTH_ID, dcps.EMP_NAME FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post ");
		lSBQuery.append(" on post.POST_ID = role.POST_ID  ");
		lSBQuery.append(" inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID and usr.ACTIVATE_FLAG = 1 ");
		lSBQuery.append(" inner join org_emp_mst emp on emp.user_id = usr.user_id ");
		lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID ");
		lSBQuery.append(" where post.LOC_ID = :locCode and role.ROLE_ID = 800002 and role.ACTIVATE_FLAG =1 ");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("locCode", locCode);
			
		if(lQuery != null && lQuery.list().size()> 0)
		{
			Object obj[] = (Object[])lQuery.list().get(0);
			lStrHODtls = obj[1]+"("+obj[0]+")";
		}
		 }catch(Exception e){
			 logger.error(" Error is : " + e, e);			 
		 }
		 return lStrHODtls;
	}*/
	
	public List getCurrentVerifiers(String gStrLocationCode){
	{
		List lLstCurrentVerifiers = null;
		logger.error(" 	gStrLocationCode "+gStrLocationCode);
		try{
			StringBuilder lSBQuery = new StringBuilder();
						
			lSBQuery.append("SELECT user.user_name,dcps.EMP_NAME FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post ");
			lSBQuery.append(" on post.POST_ID = role.POST_ID  ");
			lSBQuery.append(" inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID and usr.ACTIVATE_FLAG = 1 ");
			lSBQuery.append(" inner join org_user_mst user on user.user_id=usr.user_id " );
			lSBQuery.append(" inner join org_emp_mst emp on emp.user_id = usr.user_id ");
			lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID ");
			lSBQuery.append(" where post.LOC_ID = :gStrLocationCode and role.ROLE_ID = 800001 and role.ACTIVATE_FLAG =1 ");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("gStrLocationCode", gStrLocationCode);
			
			if(lQuery != null && lQuery.list().size()> 0)
			{
				lLstCurrentVerifiers = lQuery.list();
			}
		}
			catch(Exception e){
			logger.error(" Error in getCurrentVerifiers : " + e, e);
			
		}
		return lLstCurrentVerifiers;
	}
	
	
	
}
	
	public List getCurrentDEO(String gStrLocationCode){
		{
			List lLstCurrentDEO = null;
			
			try{
				StringBuilder lSBQuery = new StringBuilder();
							
				lSBQuery.append("SELECT dcps.SEVARTH_ID, dcps.EMP_NAME FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post ");
				lSBQuery.append(" on post.POST_ID = role.POST_ID  ");
				lSBQuery.append(" inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID and usr.ACTIVATE_FLAG = 1 ");
				lSBQuery.append(" inner join org_emp_mst emp on emp.user_id = usr.user_id ");
				lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID ");
				lSBQuery.append(" where post.LOC_ID = :gStrLocationCode and role.ROLE_ID = 800005 and role.ACTIVATE_FLAG =1 ");
				
				Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lQuery.setParameter("gStrLocationCode", gStrLocationCode);
				
				if(lQuery != null && lQuery.list().size()> 0)
				{
					lLstCurrentDEO = lQuery.list();
				}
			}
				catch(Exception e){
				logger.error(" Error in lLstCurrentDEO : " + e, e);
				
			}
			return lLstCurrentDEO;
		}
		
		
		
	}
	
	
public List getActiveHOLst(String locCode){
		

		logger.error("getActiveHOLst");
		logger.error("locCode"+locCode);
		
		
		 List lLstHODtls = null;
		 Session ghibSession = getSession();
		 try{
		StringBuilder lSBQuery = new StringBuilder();
		
		
		lSBQuery.append(" SELECT dcps.SEVARTH_ID, dcps.EMP_NAME,usr.POST_ID FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post ");
		lSBQuery.append(" on post.POST_ID = role.POST_ID  ");
		lSBQuery.append(" inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID and usr.ACTIVATE_FLAG = 1 ");
		lSBQuery.append(" inner join org_emp_mst emp on emp.user_id = usr.user_id ");
		lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID ");
		lSBQuery.append(" where post.LOC_ID = :locCode and role.ROLE_ID = 800002 and role.ACTIVATE_FLAG =1 ");
		
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("locCode", locCode);
			
		if(lQuery != null && lQuery.list().size()> 0)
		{
			lLstHODtls = lQuery.list();
		}
		
		
			
		 }catch(Exception e){
			 logger.error(" Error is : " + e, e);			 
		 }
		 return lLstHODtls;
	}

public String getPendingFlag(String lStrLstActPostId)
{
	
	logger.error("getPendingFlag dao called"+lStrLstActPostId);
	List lLstPending = null;
	String lStrPendingFlag = "N";
	
	try{
		StringBuilder lSBQuery = new StringBuilder();
		
		lSBQuery.append(" SELECT gpf.gpfAccNo,job.lstActPostId FROM MstGpfAdvance gpf, WfJobMst job ");
		lSBQuery.append(" where gpf.mstGpfAdvanceId=job.jobRefId ");
		lSBQuery.append(" and job.lstActPostId=:lStrLstActPostId ");
		lSBQuery.append(" and gpf.statusFlag like 'F%' ");
		
		
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("lStrLstActPostId", lStrLstActPostId);
		
		
		lLstPending = lQuery.list();
		
		logger.error("size::"+lLstPending.size());
		
		if(lLstPending != null && lLstPending.size() > 0){
			lStrPendingFlag = "Y";
			logger.error("lStrPendingFlag::"+lStrPendingFlag);
		}
		
		logger.error("lStrPendingFlag"+lStrPendingFlag);
	}
	
	catch(Exception e){
		logger.error(" Error is : " + e, e);
		
	}
	return lStrPendingFlag;
}

public String getPostId(String lStrUserName){
	
	logger.error("getPostId"+lStrUserName);

	
	List lLstPostId = null;
	String lStrPostId = "";
	
	try{
		StringBuilder lSBQuery = new StringBuilder();
		
		lSBQuery.append(" SELECT post.post_id FROM org_user_mst user, org_userpost_rlt post ");
		lSBQuery.append(" where user.user_id=post.user_id ");
		lSBQuery.append(" and user.user_name=:lStrUserName ");
	
		
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("lStrUserName", lStrUserName);
		
		lLstPostId = lQuery.list();
		
		if(lLstPostId != null && lLstPostId.size() > 0){
			lStrPostId = lLstPostId.get(0).toString();
		}
		
		logger.error("lStrPostId"+lStrPostId);
	}
	
	catch(Exception e){
		logger.error(" Error in getPostId : " + e, e);
		
	}
	return lStrPostId;
	
	
}


public String DeAssignRole(Long lLngUsrPostId)throws Exception
{
	String lStrDeassignFlag = "";
	logger.error("DeAssignRole service called"+lLngUsrPostId);
	try{
		StringBuilder lSBQuery = new StringBuilder();
		
		lSBQuery.append("update ACL_POSTROLE_RLT post set post.ACTIVATE_FLAG=2 ");
		lSBQuery.append("where post.POST_ID =:postId and post.ROLE_ID=800001 ");
		
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setLong("postId", lLngUsrPostId);
	
		
		
		lQuery.executeUpdate();
		lStrDeassignFlag = String.valueOf(lQuery.executeUpdate());
		
	}catch(Exception e){
		logger.error(" Error is : " + e, e);
		throw e;
	}
	
	return lStrDeassignFlag;
}

public String getPendingFlagHO(String lStrLstActPostId)
{
	
	logger.error("getPendingFlagHO dao called"+lStrLstActPostId);
	List lLstPending = null;
	String lStrPendingFlag = "N";
	
	try{
		StringBuilder lSBQuery = new StringBuilder();
		
		lSBQuery.append(" SELECT gpf.gpfAccNo,job.lstActPostId FROM MstGpfAdvance gpf, WfJobMst job ");
		lSBQuery.append(" where gpf.mstGpfAdvanceId=job.jobRefId ");
		lSBQuery.append(" and job.lstActPostId=:lStrLstActPostId ");
		lSBQuery.append(" and gpf.statusFlag='F2' ");
		
		
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("lStrLstActPostId", lStrLstActPostId);
		
		
		lLstPending = lQuery.list();
		
		logger.error("size::"+lLstPending.size());
		
		if(lLstPending != null && lLstPending.size() > 0){
			lStrPendingFlag = "Y";
		}
		
		logger.error("lStrPendingFlag"+lStrPendingFlag);
	}
	
	catch(Exception e){
		logger.error(" Error is : " + e, e);
		
	}
	return lStrPendingFlag;
}

public String DeAssignRoleHO(Long lLngUsrPostId)throws Exception
{
	String lStrDeassignFlag = "";
	logger.error("DeAssignRoleHO service called"+lLngUsrPostId);
	try{
		StringBuilder lSBQuery = new StringBuilder();
		
		lSBQuery.append("update ACL_POSTROLE_RLT post set post.ACTIVATE_FLAG=2 ");
		lSBQuery.append("where post.POST_ID =:postId and post.ROLE_ID=800002 ");
		
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setLong("postId", lLngUsrPostId);
	
		
		
		lQuery.executeUpdate();
		lStrDeassignFlag = String.valueOf(lQuery.executeUpdate());
		
	}catch(Exception e){
		logger.error(" Error is : " + e, e);
		throw e;
	}
	
	return lStrDeassignFlag;
}

public String DeAssignRoleDEO(Long lLngUsrPostId)throws Exception
{
	String lStrDeassignFlag = "";
	logger.error("dao DeAssignRoleDEO called"+lLngUsrPostId);
	try{
		StringBuilder lSBQuery = new StringBuilder();
		
		
		
		
		
		lSBQuery.append("update ACL_POSTROLE_RLT post set post.ACTIVATE_FLAG=2 ");
		lSBQuery.append("where post.POST_ROLE_ID='"+lLngUsrPostId+"' ");
		
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		//lQuery.setLong("postId", lLngUsrPostId);
	
		
		
		lQuery.executeUpdate();
		lStrDeassignFlag = String.valueOf(lQuery.executeUpdate());
		logger.error("lSBQuery***DeAssignRoleDEO*******"+lSBQuery.toString());
		logger.error("lSBQuery***DeAssignRoleDEO*******"+lStrDeassignFlag);
		
		
	}catch(Exception e){
		logger.error(" Error is : " + e, e);
		throw e;
	}
	
	return lStrDeassignFlag;
}


public String getValidateUsernameFlag(String lStrUsername)
{
	
	logger.error("getValidateUsernameFlag dao called"+lStrUsername);
	List username = null;
	String lStrValidateUsernameFlag = "N";
	
	try{
		StringBuilder lSBQuery = new StringBuilder();
		

	      /*lSBQuery.append("SELECT ME.name,ME.sevarthId ");
	      lSBQuery.append("FROM MstEmp ME, OrgEmpMst OEM, OrgUserpostRlt OUPR, OrgUserMst OUM ");
	      lSBQuery.append("WHERE ME.orgEmpMstId = OEM.empId AND OUM.userId = OUPR.userId ");
	      lSBQuery.append("AND OUPR.activateFlag = 1 and OUM.userName=:lStrUsername ");*/
	      
			lSBQuery.append("SELECT ME.name, OUPR.orgPostMstByPostId.postId, OUM.userName ");
			lSBQuery.append("FROM MstEmp ME, OrgEmpMst OEM, OrgUserpostRlt OUPR, OrgUserMst OUM ");
			lSBQuery.append("WHERE ME.orgEmpMstId = OEM.empId AND OEM.orgUserMst.userId = OUPR.orgUserMst.userId ");
			lSBQuery.append("AND OUPR.activateFlag = 1 AND OEM.orgUserMst.userId = OUM.userId AND OUM.userName=:lStrUsername");
				
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("lStrUsername", lStrUsername);
		
		
		
		username = lQuery.list();
		
		
		
		if(username != null && username.size() > 0){
			lStrValidateUsernameFlag = "Y";
		}
		
		logger.error("lStrValidateUsernameFlag"+lStrValidateUsernameFlag);
	}
	
	catch(Exception e){
		logger.error(" Error is : " + e, e);
		
	}
	return lStrValidateUsernameFlag;
}

public String getLocationCode(String lStrddocode) {

	StringBuilder lSBQuery = new StringBuilder();
	List lLstCodeList = null;
	String lStrDdoCode = "";
	try {
		lSBQuery.append(" SELECT locationCode ");
		lSBQuery.append(" FROM OrgDdoMst ");
		lSBQuery.append(" WHERE ddoCode = :lStrddocode  ");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("lStrddocode", lStrddocode);

		lLstCodeList = lQuery.list();

		lStrDdoCode = lLstCodeList.get(0).toString();
	} catch (Exception e) {
		logger.error("Exception in getDdoCode of GPFRequestProcessDAOImpl  : ", e);			
	}
	return lStrDdoCode;
}


public String chkEntryForHOMDC(String lStrLocationCode)throws Exception
{
	logger.error("chkEntryForHO");
	List lLstResData = null;
	String lStrHOFlag = "";
	
	ArrayList lLstDoc = new ArrayList<Long>();		    
	  

	
	try{
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("SELECT dcps.SEVARTH_ID, dcps.EMP_NAME FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post ");
		lSBQuery.append(" on post.POST_ID = role.POST_ID  ");
		lSBQuery.append(" inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID and usr.ACTIVATE_FLAG = 1 ");
		lSBQuery.append(" inner join org_emp_mst emp on emp.user_id = usr.user_id ");
		lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID ");
		lSBQuery.append(" where post.LOC_ID = :locCode and role.ROLE_ID = 800002 and role.ACTIVATE_FLAG =1 ");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("locCode", lStrLocationCode);
	
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

//gokarna newww


public void InsertPostForRhoAsst1(Long lStrPostid)
{
	 Session hibSession = getSession();
     StringBuffer strBfr = new StringBuffer();
     strBfr.append("INSERT INTO ACL_POSTROLE_RLT(POST_ROLE_ID,POST_ID,ROLE_ID,START_DATE,END_DATE,ACTIVATE_FLAG,CREATED_BY,CREATED_DATE,CREATED_BY_POST,UPDATED_BY,UPDATED_DATE,UPDATED_BY_POST) VALUES ((SELECT max(POST_ROLE_ID) FROM ACL_POSTROLE_RLT)+1,"+lStrPostid+",8000016,sysdate,null,1,1,sysdate,1,null,null,null) ");    
     Query updateQuery = hibSession.createSQLQuery(strBfr.toString());
     logger.error("insertMappingRolePost" + updateQuery);
     updateQuery.executeUpdate();
}

//Added by vivek sharma 17 Jun 17
public String reAssignRoleRHOAST(Long lLngUsrPostId)throws Exception
{
	String lStrDeassignFlag = "";
	logger.error("DeAssignRoleHO service called"+lLngUsrPostId);
	try{
		StringBuilder lSBQuery = new StringBuilder();
		
		lSBQuery.append("update ACL_POSTROLE_RLT post set post.ACTIVATE_FLAG=1 ");
		lSBQuery.append("where post.POST_ID =:postId and post.ROLE_ID=8000016 ");
		
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setLong("postId", lLngUsrPostId);
	
		
		
		lQuery.executeUpdate();
		lStrDeassignFlag = String.valueOf(lQuery.executeUpdate());
		
	}catch(Exception e){
		logger.error(" Error is : " + e, e);
		throw e;
	}
	
	return lStrDeassignFlag;
}




public List getActiveRhoAsstLst(String locCode){
	

	logger.error("getActiveHOLst");
	logger.error("locCode"+locCode);
	
	
	 List lLstHODtls = null;
	 Session ghibSession = getSession();
	 try{
	StringBuilder lSBQuery = new StringBuilder();
	lSBQuery.append(" SELECT dcps.SEVARTH_ID, dcps.EMP_NAME FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post ");
	lSBQuery.append(" on post.POST_ID = role.POST_ID  ");
	lSBQuery.append(" inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID and usr.ACTIVATE_FLAG = 1 ");
	lSBQuery.append(" inner join org_emp_mst emp on emp.user_id = usr.user_id ");
	lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID ");
	lSBQuery.append(" where post.LOC_ID = '"+locCode+"' and role.ROLE_ID = 8000016 and role.ACTIVATE_FLAG =1 ");
	Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	
	
	//lQuery.setParameter("locCode", locCode);
		
	logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
	
	if(lQuery != null && lQuery.list().size()> 0)
	{
		lLstHODtls = lQuery.list();
	}
	
	 }catch(Exception e){
		 logger.error(" Error is : " + e, e);			 
	 }
	 return lLstHODtls;
}

public String getPostRoleID(String Locid){
	
	logger.error("getLocid"+Locid);

	
	List lLstPostIdList = null;
	String lStrPostId ="";
	
	try{
		StringBuilder lSBQuery = new StringBuilder();
		
		/*lSBQuery.append(" SELECT post.post_id FROM org_user_mst user, org_userpost_rlt post ");
		lSBQuery.append(" where user.user_id=post.user_id ");
		lSBQuery.append(" and user.user_name=:lStrUserName ");
		
		*/
		lSBQuery.append(" SELECT role.post_role_id FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post "); 
		lSBQuery.append(" on post.POST_ID = role.POST_ID   inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID ");
		lSBQuery.append(" and usr.ACTIVATE_FLAG = 1  inner join org_emp_mst emp on emp.user_id = usr.user_id "); 
		lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID"); 
		lSBQuery.append(" where post.LOC_ID = '"+Locid+"' and role.ROLE_ID = 800005 and role.ACTIVATE_FLAG =1 "); 

		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		//lQuery.setParameter("lStrUserName", lStrUserName);
		
		lLstPostIdList = lQuery.list();
		
		logger.error("lSBQuery***getPostRoleID*******"+lSBQuery.toString());
		
		
		if(lLstPostIdList != null && lLstPostIdList.size() > 0){
			lStrPostId = lLstPostIdList.get(0).toString();
		}
		
		logger.error("lStrPostId"+lStrPostId);
	}
	
	catch(Exception e){
		logger.error(" Error in getPostId : " + e, e);
		
	}
	return lStrPostId;
	
	
}

public String getPostRoleIDMDC(String Locid,String lLngPostId){
	
	logger.error("getLocid"+Locid);

	
	List lLstPostIdList = null;
	String lStrPostId ="";
	
	try{
		StringBuilder lSBQuery = new StringBuilder();
		
		/*lSBQuery.append(" SELECT post.post_id FROM org_user_mst user, org_userpost_rlt post ");
		lSBQuery.append(" where user.user_id=post.user_id ");
		lSBQuery.append(" and user.user_name=:lStrUserName ");
		
		*/
		lSBQuery.append(" SELECT role.post_role_id FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post "); 
		lSBQuery.append(" on post.POST_ID = role.POST_ID   inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID ");
		lSBQuery.append(" and usr.ACTIVATE_FLAG = 1  inner join org_emp_mst emp on emp.user_id = usr.user_id "); 
		lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID"); 
		lSBQuery.append(" where post.LOC_ID = '"+Locid+"' and usr.POST_ID ='"+lLngPostId+"' and role.ROLE_ID = 800005 and role.ACTIVATE_FLAG =1 "); 

		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		//lQuery.setParameter("lStrUserName", lStrUserName);
		
		lLstPostIdList = lQuery.list();
		
		logger.error("lSBQuery***getPostRoleID*******"+lSBQuery.toString());
		
		
		if(lLstPostIdList != null && lLstPostIdList.size() > 0){
			lStrPostId = lLstPostIdList.get(0).toString();
		}
		
		logger.error("lStrPostId"+lStrPostId);
	}
	
	catch(Exception e){
		logger.error(" Error in getPostId : " + e, e);
		
	}
	return lStrPostId;
	
	
}


public String getPostRoleIVER(String Locid){
	
	logger.error("getLocid"+Locid);

	
	List lLstPostIdList = null;
	String lStrPostId ="";
	
	try{
		StringBuilder lSBQuery = new StringBuilder();
		
		/*lSBQuery.append(" SELECT post.post_id FROM org_user_mst user, org_userpost_rlt post ");
		lSBQuery.append(" where user.user_id=post.user_id ");
		lSBQuery.append(" and user.user_name=:lStrUserName ");
		
		*/
		lSBQuery.append(" SELECT role.post_role_id FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post "); 
		lSBQuery.append(" on post.POST_ID = role.POST_ID   inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID ");
		lSBQuery.append(" and usr.ACTIVATE_FLAG = 1  inner join org_emp_mst emp on emp.user_id = usr.user_id "); 
		lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID"); 
		lSBQuery.append(" where post.LOC_ID = '"+Locid+"' and role.ROLE_ID = 800001 and role.ACTIVATE_FLAG =1 "); 

		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		//lQuery.setParameter("lStrUserName", lStrUserName);
		
		lLstPostIdList = lQuery.list();
		
		logger.error("lSBQuery***getPostRoleID*******"+lSBQuery.toString());
		
		
		if(lLstPostIdList != null && lLstPostIdList.size() > 0){
			lStrPostId = lLstPostIdList.get(0).toString();
		}
		
		logger.error("lStrPostId"+lStrPostId);
	}
	
	catch(Exception e){
		logger.error(" Error in getPostId : " + e, e);
		
	}
	return lStrPostId;
	
	
}

public String getPostRoleIVERMDC(String Locid,String lLngPostId){
	
	logger.error("getLocid"+Locid);

	
	List lLstPostIdList = null;
	String lStrPostId ="";
	
	try{
		StringBuilder lSBQuery = new StringBuilder();
		
		/*lSBQuery.append(" SELECT post.post_id FROM org_user_mst user, org_userpost_rlt post ");
		lSBQuery.append(" where user.user_id=post.user_id ");
		lSBQuery.append(" and user.user_name=:lStrUserName ");
		
		*/
		lSBQuery.append(" SELECT role.post_role_id FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post "); 
		lSBQuery.append(" on post.POST_ID = role.POST_ID   inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID ");
		lSBQuery.append(" and usr.ACTIVATE_FLAG = 1  inner join org_emp_mst emp on emp.user_id = usr.user_id "); 
		lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID"); 
		lSBQuery.append(" where post.LOC_ID = '"+Locid+"' and usr.POST_ID ='"+lLngPostId+"' and role.ROLE_ID = 800001 and role.ACTIVATE_FLAG =1 "); 

		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		//lQuery.setParameter("lStrUserName", lStrUserName);
		
		lLstPostIdList = lQuery.list();
		
		logger.error("lSBQuery***getPostRoleID*******"+lSBQuery.toString());
		
		
		if(lLstPostIdList != null && lLstPostIdList.size() > 0){
			lStrPostId = lLstPostIdList.get(0).toString();
		}
		
		logger.error("lStrPostId"+lStrPostId);
	}
	
	catch(Exception e){
		logger.error(" Error in getPostId : " + e, e);
		
	}
	return lStrPostId;
	
	
}

public String getPostRoleRHOASTT(String Locid){
	
	logger.error("getLocid"+Locid);

	
	List lLstPostIdList = null;
	String lStrPostId ="";
	
	try{
		StringBuilder lSBQuery = new StringBuilder();
		
		/*lSBQuery.append(" SELECT post.post_id FROM org_user_mst user, org_userpost_rlt post ");
		lSBQuery.append(" where user.user_id=post.user_id ");
		lSBQuery.append(" and user.user_name=:lStrUserName ");
		
		*/
		lSBQuery.append(" SELECT role.post_role_id FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post "); 
		lSBQuery.append(" on post.POST_ID = role.POST_ID   inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID ");
		lSBQuery.append(" and usr.ACTIVATE_FLAG = 1  inner join org_emp_mst emp on emp.user_id = usr.user_id "); 
		lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID"); 
		lSBQuery.append(" where post.LOC_ID = '"+Locid+"' and role.ROLE_ID = 8000016 and role.ACTIVATE_FLAG =1 "); 

		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		//lQuery.setParameter("lStrUserName", lStrUserName);
		
		lLstPostIdList = lQuery.list();
		
		logger.error("lSBQuery***getPostRoleID*******"+lSBQuery.toString());
		
		
		if(lLstPostIdList != null && lLstPostIdList.size() > 0){
			lStrPostId = lLstPostIdList.get(0).toString();
		}
		
		logger.error("lStrPostId"+lStrPostId);
	}
	
	catch(Exception e){
		logger.error(" Error in getPostId : " + e, e);
		
	}
	return lStrPostId;
	
	
}
/*public String DeAssignRoleRHOASST(Long lLngUsrPostId)throws Exception
{
	String lStrDeassignFlag = "";
	logger.error("dao DeAssignRoleDEO called"+lLngUsrPostId);
	try{
		StringBuilder lSBQuery = new StringBuilder();
		
		
		
		
		
		lSBQuery.append("update ACL_POSTROLE_RLT post set post.ACTIVATE_FLAG=2 ");
		lSBQuery.append("where post.POST_ROLE_ID='"+lLngUsrPostId+"' ");
		
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		//lQuery.setLong("postId", lLngUsrPostId);
	
		
		
		lQuery.executeUpdate();
		lStrDeassignFlag = String.valueOf(lQuery.executeUpdate());
		
	}catch(Exception e){
		logger.error(" Error is : " + e, e);
		throw e;
	}
	
	return lStrDeassignFlag;
}

*/

public String getPostRoleHO(String Locid){
	
	logger.error("getLocid"+Locid);

	
	List lLstPostIdList = null;
	String lStrPostId ="";
	
	try{
		StringBuilder lSBQuery = new StringBuilder();
		
		/*lSBQuery.append(" SELECT post.post_id FROM org_user_mst user, org_userpost_rlt post ");
		lSBQuery.append(" where user.user_id=post.user_id ");
		lSBQuery.append(" and user.user_name=:lStrUserName ");
		
		*/
		lSBQuery.append(" SELECT role.post_role_id FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post "); 
		lSBQuery.append(" on post.POST_ID = role.POST_ID   inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID ");
		lSBQuery.append(" and usr.ACTIVATE_FLAG = 1  inner join org_emp_mst emp on emp.user_id = usr.user_id "); 
		lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID"); 
		lSBQuery.append(" where post.LOC_ID = '"+Locid+"' and role.ROLE_ID = 800002 and role.ACTIVATE_FLAG =1 "); 

		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		//lQuery.setParameter("lStrUserName", lStrUserName);
		
		lLstPostIdList = lQuery.list();
		
		logger.error("lSBQuery***getPostRoleID*******"+lSBQuery.toString());
		
		
		if(lLstPostIdList != null && lLstPostIdList.size() > 0){
			lStrPostId = lLstPostIdList.get(0).toString();
		}
		
		logger.error("lStrPostId"+lStrPostId);
	}
	
	catch(Exception e){
		logger.error(" Error in getPostId : " + e, e);
		
	}
	return lStrPostId;
	
	
}

//swt
public String getPostRoleHOMDC(String Locid,String lLngPostId){
	
	logger.error("getLocid"+Locid);

	
	List lLstPostIdList = null;
	String lStrPostId ="";
	
	try{
		StringBuilder lSBQuery = new StringBuilder();
		
		/*lSBQuery.append(" SELECT post.post_id FROM org_user_mst user, org_userpost_rlt post ");
		lSBQuery.append(" where user.user_id=post.user_id ");
		lSBQuery.append(" and user.user_name=:lStrUserName ");
		
		*/
		lSBQuery.append(" SELECT role.post_role_id FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post "); 
		lSBQuery.append(" on post.POST_ID = role.POST_ID   inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID ");
		lSBQuery.append(" and usr.ACTIVATE_FLAG = 1  inner join org_emp_mst emp on emp.user_id = usr.user_id "); 
		lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID"); 
		lSBQuery.append(" where post.LOC_ID = '"+Locid+"' and usr.POST_ID ='"+lLngPostId+"' and role.ROLE_ID = 800002 and role.ACTIVATE_FLAG =1 "); 

		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		//lQuery.setParameter("lStrUserName", lStrUserName);
		
		lLstPostIdList = lQuery.list();
		
		logger.error("lSBQuery***getPostRoleID*******"+lSBQuery.toString());
		
		
		if(lLstPostIdList != null && lLstPostIdList.size() > 0){
			lStrPostId = lLstPostIdList.get(0).toString();
		}
		
		logger.error("lStrPostId"+lStrPostId);
	}
	
	catch(Exception e){
		logger.error(" Error in getPostId : " + e, e);
		
	}
	return lStrPostId;
	
	
}

//Added by vivek sharma 06 feb 17
public List getActiveFlagRole(Long lLngPostId) {
	

	logger.error("getActiveHOLst");
	logger.error("lLngPostId"+lLngPostId);
	
	
	 List lLstActRole = null;
	 Session ghibSession = getSession();
	 try{
	StringBuilder lSBQuery = new StringBuilder();
	//lSBQuery.append("SELECT apr.ACTIVATE_FLAG, ardr.ROLE_NAME ");
	lSBQuery.append("SELECT apr.ACTIVATE_FLAG, ardr.ROLE_NAME,ardr.ROLE_ID ");
	lSBQuery.append(" FROM ACL_POSTROLE_RLT apr inner join ACL_ROLE_DETAILS_RLT ardr  ");
	lSBQuery.append(" on apr.ROLE_ID = ardr.ROLE_ID ");
	lSBQuery.append("  where apr.ROLE_ID in (800005,800002,800001,8000016) ");
	//lSBQuery.append("  and apr.ACTIVATE_FLAG = 1 and apr.POST_ID = "+lLngPostId+" ");
	lSBQuery.append(" and apr.POST_ID = "+lLngPostId+" ");
	
	Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	//lQuery.setParameter("lLngPostId", lLngPostId);
	lLstActRole = lQuery.list();
	//lQuery.setParameter("locCode", locCode);
		
	logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
	logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstActRole.size());
	
	if(lLstActRole != null && lLstActRole.size()> 0)
	{
		lLstActRole = lQuery.list();
	}
	
	 }catch(Exception e){
		 logger.error(" Error is : " + e, e);			 
	 }
	 return lLstActRole;
}
//Added by Sooraj
//The following 2 methods check if there any request is pending under HO/verifier
public int checkPendingRequestHO(String username)
{
	logger.error("checkPendingRequest");
	logger.error("UserName"+username);
	int res1=0;
	int res2=0;
	int res3=0;
	int res4=0;
	int res5=0;
	int res6=0;
	int res7=0;
	List checkList1 = null;
	List checkList2 = null;
	List checkList3 = null;
	Session ghibSession = getSession();
	username=username.trim();
	try
	{
		//The following 3 queries check if there is any loan request is pending
		logger.info("Before 1st query");
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select count(1) ");
		lSBQuery.append(" FROM MST_GPF_EMP_SUBSCRIPTION CS,wf_job_mst WJ,mst_emp_gpf_acc MEG,Mst_dcps_Emp ME ");
		lSBQuery.append(" WHERE CS.gpf_Acc_No = MEG.gpf_Acc_No AND CS.status_Flag LIKE 'F%' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
		lSBQuery.append(" AND WJ.job_Ref_Id =CS.gpf_Emp_Subscription_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
		lSBQuery.append(" AND WJ.doc_Id =800001  AND ME.emp_group ='D'");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		/*checkList1=lQuery.list();
		res1=checkList1.size();*/
		res1=Integer.parseInt(lQuery.uniqueResult().toString());
		logger.info("res1="+res1);
		logger.info("Before 2nd query");
		StringBuilder lSBQuery2 = new StringBuilder();
		lSBQuery2.append("select count(1) ");
		lSBQuery2.append(" FROM IFMS.MST_GPF_ADVANCE MGA,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
		lSBQuery2.append(" WHERE MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.SEVAARTH_ID = MEG.SEVAARTH_ID AND MGA.status_Flag LIKE 'F%' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
		lSBQuery2.append(" AND WJ.job_Ref_Id = MGA.mst_Gpf_Advance_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
		lSBQuery2.append(" AND WJ.doc_Id = 800002 AND ME.EMP_group ='D' AND MGA.data_Entry is null");
		Query lQuery2 = ghibSession.createSQLQuery(lSBQuery2.toString());
		res2=Integer.parseInt(lQuery2.uniqueResult().toString());
		/*checkList2=lQuery2.list();
		res2=checkList2.size();*/
		
		logger.info("res2="+res2);
		logger.info("Before 3rd query");
		StringBuilder lSBQuery3 = new StringBuilder();
		lSBQuery3.append("select count(1) ");
		lSBQuery3.append(" FROM Trn_Gpf_Final_Withdrawal TGF,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
		lSBQuery3.append(" WHERE TGF.gpf_Acc_No = MEG.gpf_Acc_No AND TGF.SEVAARTH_ID = MEG.SEVAARTH_ID AND TGF.req_Status LIKE 'F%' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
		lSBQuery3.append(" AND WJ.job_Ref_Id = TGF.trn_Gpf_Final_Withdrawal_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
		lSBQuery3.append(" AND WJ.doc_Id =800003 AND ME.EMP_group ='D'");
		Query lQuery3 = ghibSession.createSQLQuery(lSBQuery3.toString());
		res3=Integer.parseInt(lQuery3.uniqueResult().toString());
		/*checkList3=lQuery3.list();
		res3=checkList3.size();*/
		
		// The following 2 queries Check if there is any order generation is pending
		StringBuilder lSBQuery4 = new StringBuilder();
		lSBQuery4.append("select count(1) ");
		lSBQuery4.append(" FROM IFMS.MST_GPF_ADVANCE MGA ");
		lSBQuery4.append(" inner join Mst_Emp_Gpf_Acc MG on MGA.GPF_ACC_NO = MG.GPF_ACC_NO and MGA.sevaarth_id = MG.sevaarth_id");
		lSBQuery4.append(" inner join  Mst_DCPS_Emp MGE on MG.MST_GPF_EMP_ID = MGE.DCPS_EMP_ID and MGE.DCPS_OR_GPF ='N' and  MGE.EMP_group ='D'");
		lSBQuery4.append(" inner join Org_Ddo_Mst ODM on MG.ddo_Code=ODM.ddo_Code ");
		lSBQuery4.append(" left outer join MST_GPF_BILL_DTLS blt on blt.TRANSACTION_ID=MGA.TRANSACTION_ID ");
		lSBQuery4.append(" WHERE MGA.status_Flag IN ('A','AC') ");
		lSBQuery4.append(" AND (MGA.FRWDRD_TO_RHO_POST_ID = (SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"') or  ODM.LOCATION_CODE =(SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"')) ");
		lSBQuery4.append(" AND MGA.data_Entry is null  ");
		Query lQuery4 = ghibSession.createSQLQuery(lSBQuery4.toString());
		res4=Integer.parseInt(lQuery4.uniqueResult().toString());
		
		
		StringBuilder lSBQuery5 = new StringBuilder();
		lSBQuery5.append("select count(1) ");
		lSBQuery5.append("  FROM TRN_GPF_FINAL_WITHDRAWAL TGFW ");
		lSBQuery5.append("  inner join Mst_Emp_Gpf_Acc MG on TGFW.GPF_ACC_NO = MG.GPF_ACC_NO and TGFW.sevaarth_id = MG.sevaarth_id ");
		lSBQuery5.append(" inner join  Mst_DCPS_Emp MGE on MG.MST_GPF_EMP_ID = MGE.DCPS_EMP_ID and MGE.DCPS_OR_GPF ='N' and  MGE.EMP_group ='D'  ");
		lSBQuery5.append(" inner join Org_Ddo_Mst ODM on MG.ddo_Code=ODM.ddo_Code ");
		lSBQuery5.append(" left outer join MST_GPF_BILL_DTLS blt on blt.TRANSACTION_ID=TGFW.TRANSACTION_ID  ");
		lSBQuery5.append(" WHERE TGFW.REQ_STATUS = 'A' ");
		lSBQuery5.append(" AND (TGFW.FRWDRD_TO_RHO_POST_ID = (SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"') or  ODM.LOCATION_CODE =(SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"')) ");
		Query lQuery5 = ghibSession.createSQLQuery(lSBQuery5.toString());
		res5=Integer.parseInt(lQuery5.uniqueResult().toString());
		
		
		//checking if there any request is pending for initial data entry
		StringBuilder lSBQuery6 = new StringBuilder();
		lSBQuery6.append("select count(1) ");
		lSBQuery6.append(" FROM trn_emp_gpf_acc TEG, Mst_Emp_Gpf_Acc MEG, Org_Ddo_Mst ODM, Mst_Gpf_Yearly GPF  WHERE TEG.status_Flag = 'F' ");
		lSBQuery6.append(" AND TEG.sevaarth_Id = MEG.sevaarth_Id AND MEG.ddo_Code = ODM.ddo_Code  AND GPF.gpf_Acc_No=MEG.gpf_Acc_No AND GPF.sevaarth_id=MEG.sevaarth_id AND ODM.location_Code = (SELECT loc_id ");
		lSBQuery6.append(" FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"') ");
		Query lQuery6 = ghibSession.createSQLQuery(lSBQuery6.toString());
		res6=Integer.parseInt(lQuery6.uniqueResult().toString());
		
		
		
		
		
		res7=res1+res2+res3+res4+res5+res6;
		logger.info("check1234567");
		logger.info("res1="+res1);
		logger.info("res2="+res2);
		logger.info("res3="+res3);
		logger.info("res4="+res4);
		logger.info("res5="+res5);
		logger.info("res6="+res6);
		logger.info("res7="+res7);
	}
	catch (Exception e) {
		logger.error("CheckPendingRequestException");
	}
	return res7;
	
}
public int checkPendingRequestVerifier(String username)
{
	logger.error("checkPendingRequest");
	logger.error("UserName"+username);
	int res1=0;
	int res2=0;
	int res3=0;
	int res4=0;
	List checkList1 = null;
	List checkList2 = null;
	List checkList3 = null;
	Session ghibSession = getSession();
	username=username.trim();
	try
	{
		//The following 3 queries check if there is any loan request is pending
		logger.info("Before 1st query");
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select count(1) ");
		lSBQuery.append(" FROM MST_GPF_EMP_SUBSCRIPTION CS,wf_job_mst WJ,mst_emp_gpf_acc MEG,Mst_dcps_Emp ME ");
		lSBQuery.append(" WHERE CS.gpf_Acc_No = MEG.gpf_Acc_No AND CS.status_Flag LIKE 'F%' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
		lSBQuery.append(" AND WJ.job_Ref_Id =CS.gpf_Emp_Subscription_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
		lSBQuery.append(" AND WJ.doc_Id =800001  AND ME.emp_group ='D'");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		res1=Integer.parseInt(lQuery.uniqueResult().toString());
		logger.info("res1="+res1);
		
		logger.info("Before 2nd query");
		StringBuilder lSBQuery2 = new StringBuilder();
		lSBQuery2.append("select count(1) ");
		lSBQuery2.append(" FROM IFMS.MST_GPF_ADVANCE MGA,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
		lSBQuery2.append(" WHERE MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.sevaarth_id = MEG.sevaarth_id AND MGA.status_Flag LIKE 'F%' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
		lSBQuery2.append(" AND WJ.job_Ref_Id = MGA.mst_Gpf_Advance_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
		lSBQuery2.append(" AND WJ.doc_Id = 800002 AND ME.EMP_group ='D' AND MGA.data_Entry is null");
		Query lQuery2 = ghibSession.createSQLQuery(lSBQuery2.toString());
		res2=Integer.parseInt(lQuery2.uniqueResult().toString());
		logger.info("res2="+res2);
		
		logger.info("Before 3rd query");
		StringBuilder lSBQuery3 = new StringBuilder();
		lSBQuery3.append("select count(1) ");
		lSBQuery3.append(" FROM Trn_Gpf_Final_Withdrawal TGF,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
		lSBQuery3.append(" WHERE TGF.gpf_Acc_No = MEG.gpf_Acc_No and TGF.sevaarth_id = MEG.sevaarth_id and TGF.req_Status LIKE 'F%' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
		lSBQuery3.append(" AND WJ.job_Ref_Id = TGF.trn_Gpf_Final_Withdrawal_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
		lSBQuery3.append(" AND WJ.doc_Id =800003 AND ME.EMP_group ='D'");
		Query lQuery3 = ghibSession.createSQLQuery(lSBQuery3.toString());
		res3=Integer.parseInt(lQuery3.uniqueResult().toString());
		
		res4=res1+res2+res3;
		logger.info("No of requests="+res4);
	}
	catch (Exception e) {
		logger.error("CheckPendingRequestException");
	}
	return res4;
	
}
public int checkPendingRequestDEO(String username)
{

	logger.error("checkPendingRequest");
	logger.error("UserName"+username);
	int res1=0;
	int res2=0;
	int res3=0;
	int res4=0;
	Session ghibSession = getSession();
	username=username.trim();
	try
	{
		logger.info("Inside DEO try mtd");
		logger.info("Before 1st query");
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" select count(1) ");
		lSBQuery.append(" FROM Mst_Gpf_Emp_Subscription CS, Mst_Emp_Gpf_Acc MEG, Mst_dcps_Emp ME ");
		lSBQuery.append(" WHERE CS.gpf_Acc_No = MEG.gpf_Acc_No AND CS.status_Flag IN ('D','R') AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
		lSBQuery.append(" AND ME.emp_group ='D' AND ME.ddo_Code=(select ddo_code from org_ddo_mst odm inner join ORG_POST_DETAILS_RLT opdr on odm.LOCATION_CODE=opdr.LOC_ID ");
		lSBQuery.append(" inner join ORG_USERPOST_RLT our on our.POST_ID=opdr.POST_ID ");
		lSBQuery.append(" inner join ORG_USER_MST oum on oum.USER_ID=our.USER_ID ");
		lSBQuery.append(" where oum.USER_NAME='"+username+"' and our.ACTIVATE_FLAG=1)");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		res1=Integer.parseInt(lQuery.uniqueResult().toString());
		logger.info("res1="+res1);
		
		StringBuilder lBSQuery1=new StringBuilder();
		lBSQuery1.append(" select count(1) ");
		lBSQuery1.append(" FROM Mst_Gpf_Advance MGA, Mst_Emp_Gpf_Acc MEG, Mst_dcps_Emp ME ");
		lBSQuery1.append(" WHERE MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.sevaarth_id = MEG.sevaarth_id AND MGA.status_Flag IN ('D','R','DR') AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
		lBSQuery1.append(" AND ME.EMP_GROUP='D' AND ME.ddo_Code=(select ddo_code from org_ddo_mst odm inner join ORG_POST_DETAILS_RLT opdr on odm.LOCATION_CODE=opdr.LOC_ID ");
		lBSQuery1.append(" inner join ORG_USERPOST_RLT our on our.POST_ID=opdr.POST_ID ");
		lBSQuery1.append(" inner join ORG_USER_MST oum on oum.USER_ID=our.USER_ID ");
		lBSQuery1.append(" where oum.USER_NAME='"+username+"' and our.ACTIVATE_FLAG=1) ");
		Query lQuery1=ghibSession.createSQLQuery(lBSQuery1.toString());
		res2=Integer.parseInt(lQuery1.uniqueResult().toString());
		
		
		StringBuilder lBSQuery2=new StringBuilder();
		lBSQuery2.append(" SELECT count(1) ");
		lBSQuery2.append(" FROM TRN_GPF_FINAL_WITHDRAWAL tgfw, MST_EMP_GPF_ACC mega, MST_DCPS_EMP mde ");
		lBSQuery2.append(" where tgfw.GPF_ACC_NO=mega.GPF_ACC_NO and tgfw.sevaarth_id=mega.sevaarth_id and mega.MST_GPF_EMP_ID=mde.DCPS_EMP_ID ");
		lBSQuery2.append(" and tgfw.REQ_STATUS in ('D','DR','R') and mde.DCPS_OR_GPF='N' and mde.EMP_GROUP='D' and mde.DDO_CODE= ");
		lBSQuery2.append(" (select ddo_code from org_ddo_mst odm inner join ORG_POST_DETAILS_RLT opdr on odm.LOCATION_CODE=opdr.LOC_ID ");
		lBSQuery2.append(" inner join ORG_USERPOST_RLT our on our.POST_ID=opdr.POST_ID ");
		lBSQuery2.append(" inner join ORG_USER_MST oum on oum.USER_ID=our.USER_ID ");
		lBSQuery2.append(" where oum.USER_NAME='"+username+"' and our.ACTIVATE_FLAG=1) ");
		Query lQuery2=ghibSession.createSQLQuery(lBSQuery2.toString());
		res3=Integer.parseInt(lQuery2.uniqueResult().toString());
	
		res4=res1+res2+res3;
		logger.info("no of request pending under DEO is"+res4);
	}
	catch(Exception e)
	{
		logger.error("CheckPendingRequestDEOException");
	}
	return res4;
	
}

@Override
public String chkEmpGroupHoEntry(Long lLngPostId, String gStrLocationCode) {
	logger.error("chkRoleHoEntry");
	
	List lLstResData = null;
	String chkEmpGroupHoEntryflag = "";
		try{
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" SELECT MST.SEVARTH_ID FROM MST_DCPS_EMP MST ");
		lSBQuery.append(" inner join ORG_EMP_MST ORG ON ORG.EMP_ID = MST.ORG_EMP_MST_ID INNER JOIN ORG_USER_MST user on user.USER_ID=org.USER_ID INNER JOIN ORG_USERPOST_RLT post ON post.USER_ID=user.USER_ID ");
		//.append(" where MST.DDO_CODE='"+gStrLocationCode+"' AND post.POST_ID='"+lLngPostId+"' AND ORG.GRADE_ID IN (100001,100064,100065) ");
        lSBQuery.append(" where MST.DDO_CODE='"+gStrLocationCode+"' AND post.POST_ID='"+lLngPostId+"' ");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lLstResData = lQuery.list();
		
		if(lLstResData != null && lLstResData.size() > 0){
			chkEmpGroupHoEntryflag = "Y";
		}else{
			chkEmpGroupHoEntryflag = "N";
		}
	}catch(Exception e){
		 logger.error(" Error is : " + e, e);
		  throw e; 
	}
	
	return chkEmpGroupHoEntryflag;
}

@Override
public String getPostIDuser(String lStrUserName) {
	List lLstResData = null;
	String lStrDdoCode = "";
	
	
	logger.error("lStrUserName: "+lStrUserName);
	
	try{
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" SELECT UP.POST_ID FROM ORG_USER_MST ORG INNER JOIN ORG_USERPOST_RLT UP ON UP.USER_ID=ORG.USER_ID ");
		lSBQuery.append(" WHERE ORG.USER_NAME='"+lStrUserName+"' ");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		//lQuery.setLong("postId", lStrUserName);
		lLstResData = lQuery.list();
		
		if(lLstResData != null && lLstResData.size() > 0){
			lStrDdoCode = lLstResData.get(0).toString();
			logger.error(" lStrDdoCode: " +lStrDdoCode);
		}
	}catch(Exception e){
		logger.error(" Error is : " + e, e);
		throw e;
	}
	return lStrDdoCode;
}

/*@Override
public String chkEmpGroupHoEntry(Long lLngPostId, String gStrLocationCode) {
	// TODO Auto-generated method stub
	return null;
}*/

public String getValidateDDOCodeFlag(String lStrDDOCode)
{
	
	logger.error("getValidateDDOCodeFlag dao called"+lStrDDOCode);
	List ddo = null;
	String lStrValidateDDOCodeFlag = "N";
	
	try{
		StringBuilder lSBQuery = new StringBuilder();
		
		lSBQuery.append(" SELECT DDO.ddoName FROM OrgDdoMst DDO ");
		lSBQuery.append(" where DDO.ddoCode=:lStrDDOCode ");
				
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("lStrDDOCode", lStrDDOCode);
		
		
		ddo = lQuery.list();
		
		logger.error("size::"+ddo.size());
		
		if(ddo != null && ddo.size() > 0){
			lStrValidateDDOCodeFlag = "Y";
		}
		
		logger.error("lStrValidateDDOCodeFlag"+lStrValidateDDOCodeFlag);
	}
	
	catch(Exception e){
		logger.error(" Error is : " + e, e);
		
	}
	return lStrValidateDDOCodeFlag;
}
public List getAllRole(String locCode){
	

	logger.error("getActiveHOLst");
	logger.error("locCode"+locCode);
	
	
	 List lLstHODtls = null;
	 Session ghibSession = getSession();
	 try{
	StringBuilder lSBQuery = new StringBuilder();
	lSBQuery.append(" SELECT rp.ROLE_NAME,dcps.EMP_NAME,user.user_name,dg.DSGN_NAME,dcps.emp_group FROM ACL_POSTROLE_RLT role  inner join ORG_POST_DETAILS_RLT post on post.POST_ID = role.POST_ID ");
	lSBQuery.append(" inner join ACL_ROLE_DETAILS_RLT rp on rp.ROLE_ID=role.role_id   inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID and usr.ACTIVATE_FLAG = 1 ");
	lSBQuery.append(" inner join org_user_mst user on user.user_id=usr.user_id  inner join org_emp_mst emp on emp.user_id = usr.user_id  ");
	lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID  inner join ORG_DESIGNATION_MST dg on dg.DSGN_ID = dcps.DESIGNATION");
	//lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID ");
	lSBQuery.append(" where post.LOC_ID = :locCode and role.ROLE_ID in(800001,8000016,800005,800002) and role.ACTIVATE_FLAG =1 order by rp.ROLE_NAME ASC ");
	
	
	Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	lQuery.setParameter("locCode", locCode);
		
	if(lQuery != null && lQuery.list().size()> 0)
	{
		lLstHODtls = lQuery.list();
	}
	
	
		
	 }catch(Exception e){
		 logger.error(" Error is : " + e, e);			 
	 }
	 return lLstHODtls;
}


//swt 18/12/2020
	public List getAllRolesForMDC(String lStrDdoCode)throws Exception
	{
		List lLStAllRoles = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			/*lSBQuery.append("SELECT roleId, roleDesc FROM RltLevelRole WHERE moduleName = 'GPF' ");
			lSBQuery.append("AND roleId IN(800005,800001,800002) AND ");*/

			lSBQuery.append("  SELECT  rp.ROLE_NAME,dcps.EMP_NAME,user.user_name,dg.DSGN_NAME,grd.GRADE_NAME FROM ACL_POSTROLE_RLT role  inner join ORG_POST_DETAILS_RLT post on post.POST_ID = role.POST_ID ");
			lSBQuery.append(" inner join ACL_ROLE_DETAILS_RLT rp on rp.ROLE_ID=role.role_id   inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID and usr.ACTIVATE_FLAG = 1 ");
			lSBQuery.append(" inner join org_user_mst user on user.user_id=usr.user_id  inner join org_emp_mst emp on emp.user_id = usr.user_id  ");
			lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID inner join ORG_GRADE_MST grd on emp.GRADE_ID=grd.GRADE_ID  inner join ORG_DESIGNATION_MST dg on dg.DSGN_ID = dcps.DESIGNATION");
			//lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID ");
			lSBQuery.append(" where post.LOC_ID = :lStrDdoCode and role.ROLE_ID in(800001,8000016,800005,800002) and role.ACTIVATE_FLAG =1 order by rp.ROLE_NAME ASC ");
		
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("lStrDdoCode", lStrDdoCode);
			lLStAllRoles = lQuery.list();
			
			logger.error(" lSBQuerygetAllRoles: " +lSBQuery);
			
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lLStAllRoles;
	}
	
	//swt 29/12/2020
	public String getRoleOfEmp(String lStrDdoCode,String lStrSevaarthId)throws Exception
	{
		String lLStEmpRole = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			/*lSBQuery.append("SELECT roleId, roleDesc FROM RltLevelRole WHERE moduleName = 'GPF' ");
			lSBQuery.append("AND roleId IN(800005,800001,800002) AND ");*/

			lSBQuery.append(" SELECT RP.ROLE_ID FROM ACL_POSTROLE_RLT role  inner join ORG_POST_DETAILS_RLT post on post.POST_ID = role.POST_ID ");
			lSBQuery.append(" inner join ACL_ROLE_DETAILS_RLT rp on rp.ROLE_ID=role.role_id   inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID and usr.ACTIVATE_FLAG = 1 ");
			lSBQuery.append(" inner join org_user_mst user on user.user_id=usr.user_id inner join org_emp_mst emp on emp.user_id = usr.user_id  ");
			lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID  inner join ORG_GRADE_MST grd on emp.GRADE_ID=grd.GRADE_ID ");
			//lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID ");
			lSBQuery.append(" inner join ORG_DESIGNATION_MST dg on dg.DSGN_ID = dcps.DESIGNATION where post.LOC_ID = '"+lStrDdoCode+"' and role.ACTIVATE_FLAG =1 and DCPS.SEVARTH_ID='"+lStrSevaarthId+"' ");
		
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			//lQuery.setParameter("lStrDdoCode", lStrDdoCode);
			lLStEmpRole = lQuery.toString();
			
			logger.error(" lSBQuerygetAllRoles: " +lSBQuery);
			
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lLStEmpRole;
	}
	
	//swt 28/12/2020
		public List getMappedRoleForEmp(String lStrDdoCode,String lStrSevaarthId)throws Exception
		{
			List Emprole = null;
			
			try{
				StringBuilder lSBQuery = new StringBuilder();
				/*lSBQuery.append("SELECT roleId, roleDesc FROM RltLevelRole WHERE moduleName = 'GPF' ");
				lSBQuery.append("AND roleId IN(800005,800001,800002) AND ");*/

				lSBQuery.append(" SELECT rp.ROLE_id,(case when rp.ROLE_id='800001' then 'V'  when rp.ROLE_id='800002' then 'H'  when rp.ROLE_id='800005' then 'D'  when rp.ROLE_id='8000016' then 'R' END )AS role FROM ACL_POSTROLE_RLT role  inner join ORG_POST_DETAILS_RLT post on post.POST_ID = role.POST_ID ");
				lSBQuery.append(" inner join ACL_ROLE_DETAILS_RLT rp on rp.ROLE_ID=role.role_id   inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID and usr.ACTIVATE_FLAG = 1 ");
				lSBQuery.append(" inner join org_user_mst user on user.user_id=usr.user_id  inner join org_emp_mst emp on emp.user_id = usr.user_id  ");
				lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID  inner join ORG_DESIGNATION_MST dg on dg.DSGN_ID = dcps.DESIGNATION ");
				lSBQuery.append(" where post.LOC_ID = '"+lStrDdoCode+"' and role.ROLE_ID in(800001,8000016,800005,800002) and role.ACTIVATE_FLAG =1 and dcps.SEVARTH_ID= '"+lStrSevaarthId+"' ");
				
			
				Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				//lQuery.setParameter("lStrDdoCode", lStrDdoCode);
				//lQuery.setParameter("lStrSevaarthId", lStrSevaarthId);
				Emprole = lQuery.list();
				
				logger.error(" lSBQuerygetAllRoles: " +lSBQuery);
				
			}catch(Exception e){
				logger.error(" Error is : " + e, e);
				throw e;
			}
			return Emprole;
		}
	



		//swt 21/12/2020
		public List getEmpForDDO(String lStrSevaarthId,String lStrEmployeeName,String locid,String lStrDdoCode,String lStrDdoName)throws Exception
		{
			List lLstAllUsers = null;
			logger.error(" lStrDdoCode: " +lStrDdoCode);
			logger.error(" lStrSevaarthId: " +lStrSevaarthId);
			logger.error(" lStrEmployeeName: " +lStrEmployeeName);
			//logger.error(" lStroleid: " +lStroleid);
			//logger.error(" gStrLocationCode: " +gStrLocationCode);
			
			try{
				StringBuilder lSBQuery = new StringBuilder();
			/*if(lStroleid.equalsIgnoreCase("800001")||lStroleid.equalsIgnoreCase("8000016")||lStroleid.equalsIgnoreCase("800005")||lStroleid.equalsIgnoreCase("800002")){*/
						lSBQuery.append(" SELECT dcps.SEVARTH_ID,dcps.EMP_NAME,TO_CHAR(emp.EMP_DOJ,'YYYY-MM-DD'),TO_CHAR(emp.EMP_SRVC_EXP,'YYYY-MM-DD'),dg.DSGN_NAME,grd.GRADE_NAME,sum(CASE WHEN RP.ROLE_ID='800002' then 1 else 0 end) as HO, ");
						lSBQuery.append(" sum(CASE WHEN RP.ROLE_ID='800001' then 1 else 0 end) as Verifier,sum(CASE WHEN RP.ROLE_ID='800005' then 1 else 0 end) as DEO,sum(CASE WHEN RP.ROLE_ID='8000016' then 1 else 0 end) as RHO,usr.POST_ID ");
						lSBQuery.append(" FROM ACL_POSTROLE_RLT role  inner join ORG_POST_DETAILS_RLT post on post.POST_ID = role.POST_ID  inner join ACL_ROLE_DETAILS_RLT rp on rp.ROLE_ID=role.role_id   inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID and usr.ACTIVATE_FLAG = 1 ");
						lSBQuery.append(" inner join org_user_mst user on user.user_id=usr.user_id inner join org_emp_mst emp on emp.user_id = usr.user_id   inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID  inner join ORG_GRADE_MST grd on emp.GRADE_ID=grd.GRADE_ID ");
						lSBQuery.append(" inner join ORG_DESIGNATION_MST dg on dg.DSGN_ID = dcps.DESIGNATION where post.LOC_ID = '"+locid+"' and role.ROLE_ID in(800001,8000016,800005,800002) and role.ACTIVATE_FLAG =1 ");
						
						if(!lStrSevaarthId.isEmpty() ) {

							lSBQuery.append(" and  dcps.SEVARTH_ID='"+lStrSevaarthId+"' group by dcps.SEVARTH_ID,dcps.EMP_NAME,TO_CHAR(emp.EMP_DOJ,'YYYY-MM-DD'),TO_CHAR(emp.EMP_SRVC_EXP,'YYYY-MM-DD'),dg.DSGN_NAME,grd.GRADE_NAME,usr.POST_ID ");
							
							Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
							//lQuery.setParameter("sevaarthId", lStrSevaarthId);


						} if (!lStrEmployeeName.isEmpty() ) {

							lSBQuery.append(" and dcps.EMP_NAME='"+lStrEmployeeName+"' group by dcps.SEVARTH_ID,dcps.EMP_NAME,TO_CHAR(emp.EMP_DOJ,'YYYY-MM-DD'),TO_CHAR(emp.EMP_SRVC_EXP,'YYYY-MM-DD'),dg.DSGN_NAME,grd.GRADE_NAME,usr.POST_ID ");

								Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
							//lQuery.setParameter("employeeName", lStrEmployeeName);
						} 
						
						
						Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
						
				
						lLstAllUsers = lQuery.list();
						logger.error(" lSBQuerygetAllUsersForDDO: " +lSBQuery);
						
						
						if(lLstAllUsers.isEmpty() ){
							StringBuilder lSBQuery2 = new StringBuilder();
						lSBQuery2.append(" SELECT ME.SEVARTH_ID,ME.EMP_NAME,TO_CHAR(OEM.EMP_DOJ,'YYYY-MM-DD'),TO_CHAR(OEM.EMP_SRVC_EXP,'YYYY-MM-DD'),dg.DSGN_NAME,GRD.GRADE_NAME,0,0,0,0,OUPR.POST_ID ");
						lSBQuery2.append(" FROM MST_DCPS_EMP ME, ORG_EMP_MSt OEM, ORG_USERPOST_RLT OUPR, ORG_USER_MST OUM, ORG_GRADE_MST grd,ORG_DESIGNATION_MST dg,ORG_POST_DETAILS_RLT detail ");
						lSBQuery2.append(" WHERE ME.ORG_EMP_MST_ID = OEM.EMP_ID AND OEM.USER_ID= OUPR.USER_ID AND ME.DDO_CODE = '"+lStrDdoCode+"' and oem.GRADE_ID=grd.GRADE_ID ");
						//lSBQuery.append(" WHERE ME.ORG_EMP_MST_ID = OEM.EMP_ID AND OEM.USER_ID= OUPR.USER_ID and oem.GRADE_ID=grd.GRADE_ID ");
						lSBQuery2.append(" AND OUPR.ACTIVATE_FLAG = 1 AND OEM.USER_ID = OUM.USER_ID and detail.POST_ID=OUPR.POST_ID and detail.DSGN_ID = dg.DSGN_ID ");
						
						
						if(!lStrSevaarthId.isEmpty() ) {

							lSBQuery2.append(" and  ME.SEVARTH_ID='"+lStrSevaarthId+"' ");
							
							Query lQuery1 = ghibSession.createSQLQuery(lSBQuery2.toString());
							//lQuery.setParameter("sevaarthId", lStrSevaarthId);


						} if (!lStrEmployeeName.isEmpty() ) {

							lSBQuery2.append(" and ME.EMP_NAME='"+lStrEmployeeName+"' ");

								Query lQuery1 = ghibSession.createSQLQuery(lSBQuery2.toString());
							//lQuery.setParameter("employeeName", lStrEmployeeName);
						} 
						Query lQuery1 = ghibSession.createSQLQuery(lSBQuery2.toString());
						
			            lLstAllUsers = lQuery1.list();
			            
						logger.error(" lSBQuerygetAllUsersForDDO: " +lSBQuery2);
						
						}
					
				
				
			}catch(Exception e){
				logger.error(" Error is : " + e, e);
				throw e;
			}
			return lLstAllUsers;
		}

		//swt 23/12/2020

		public String getDdoCodeOfEmp(String lStrSevaarthId) {

			StringBuilder lSBQuery = new StringBuilder();
			List lLstCodeList = null;
			//lStrSevaarthId = "";
			try {
				lSBQuery.append(" SELECT sevarthId ");
				lSBQuery.append(" FROM MstEmp ");
				lSBQuery.append(" WHERE sevarthId = :sevaarthId ");
				Query lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("sevaarthId", lStrSevaarthId);

				lLstCodeList = lQuery.list();

				lStrSevaarthId = lLstCodeList.get(0).toString();
			} catch (Exception e) {
				logger.error("Exception in getDdoCodeOfEmp of GPFRequestProcessDAOImpl  : ", e);			
			}
			return lStrSevaarthId;
		}

		//swt 24/12/2020
		public String getLocationCodeNew(String lStrddocode,String ddocode,String ddoName) {

			StringBuilder lSBQuery = new StringBuilder();
			List lLstCodeList = null;
			String lStrDdoCode = "";
			try {
				lSBQuery.append(" SELECT locationCode ");
				lSBQuery.append(" FROM OrgDdoMst ");
				lSBQuery.append(" WHERE (ddoCode = :lStrddocode OR ddoCode= :lStddocode OR ddoName= :lStrDdoName)  ");
				Query lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("lStrddocode", lStrddocode);
				lQuery.setParameter("lStddocode", ddocode);
				lQuery.setParameter("lStrDdoName", ddoName);

				lLstCodeList = lQuery.list();
				if(lLstCodeList != null && lLstCodeList.size() > 0){
				
					lStrDdoCode = lLstCodeList.get(0).toString();
				}
				
				//lStrDdoCode = lLstCodeList.get(0).toString();
			} catch (Exception e) {
				logger.error("Exception in getDdoCode of GPFRequestProcessDAOImpl  : ", e);			
			}
			return lStrDdoCode;
		}
		
		public String getLocationForPost(String lStrddocode) {

			StringBuilder lSBQuery = new StringBuilder();
			List lLstCodeList = null;
			String lStrDdoCode = "";
			try {
				lSBQuery.append(" SELECT locationCode ");
				lSBQuery.append(" FROM OrgDdoMst ");
				lSBQuery.append(" WHERE ddoCode = :lStrddocode ");
				Query lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("lStrddocode", lStrddocode);
				//lQuery.setParameter("lStddocode", ddocode);

				lLstCodeList = lQuery.list();
				if(lLstCodeList != null && lLstCodeList.size() > 0){
				
					lStrDdoCode = lLstCodeList.get(0).toString();
				}
				
				//lStrDdoCode = lLstCodeList.get(0).toString();
			} catch (Exception e) {
				logger.error("Exception in getDdoCode of GPFRequestProcessDAOImpl  : ", e);			
			}
			return lStrDdoCode;
		}

		//swt 11/01/2021
		public String chkEmpGroupVerifierEntry(Long lLngPostId, String gStrLocationCode) {
			logger.error("chkRoleVerifierEntry");
			
			List lLstResData = null;
			String chkEmpGroupVerifierEntry = "";
				try{
				StringBuilder lSBQuery = new StringBuilder();
				lSBQuery.append(" SELECT MST.SEVARTH_ID FROM MST_DCPS_EMP MST ");
				lSBQuery.append(" inner join ORG_EMP_MST ORG ON ORG.EMP_ID = MST.ORG_EMP_MST_ID INNER JOIN ORG_USER_MST user on user.USER_ID=org.USER_ID INNER JOIN ORG_USERPOST_RLT post ON post.USER_ID=user.USER_ID ");
				//lSBQuery.append(" where MST.DDO_CODE='"+gStrLocationCode+"' AND post.POST_ID='"+lLngPostId+"' AND ORG.GRADE_ID IN (100001,100064,100065) ");
		        lSBQuery.append(" where MST.DDO_CODE='"+gStrLocationCode+"' AND post.POST_ID='"+lLngPostId+"' ");
				Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstResData = lQuery.list();
				
				if(lLstResData != null && lLstResData.size() > 0){
					chkEmpGroupVerifierEntry = "Y";
				}else{
					chkEmpGroupVerifierEntry = "N";
				}
			}catch(Exception e){
				 logger.error(" Error is : " + e, e);
				  throw e; 
			}
			
			return chkEmpGroupVerifierEntry;
		}	
		
		//swt 11/01/2021
		public String chkRoleVerifierEntry(long postId, String locCode) throws Exception{

			logger.error("chkRoleHoEntry");
			
			List lLstResData = null;
			String lStrHoRoleFlag = "";
			
			try{
				StringBuilder lSBQuery = new StringBuilder();
				lSBQuery.append(" SELECT post.POST_ID FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post ");
				lSBQuery.append(" on role.POST_ID = post.POST_ID and  role.ACTIVATE_FLAG =1 and role.role_id = 800001 ");
				lSBQuery.append(" where post.LOC_ID = "+locCode);
				Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstResData = lQuery.list();
				
				if(lLstResData != null && lLstResData.size() > 0){
					lStrHoRoleFlag = lQuery.list().get(0).toString();
				}else{
					lStrHoRoleFlag = "N";
				}
			}catch(Exception e){
				 logger.error(" Error is : " + e, e);
				  throw e; 
			}
			
			return lStrHoRoleFlag;
		}
		
		public String chkRoleRhoEntry(long postId, String locCode) throws Exception{

			logger.error("chkRoleHoEntry");
			
			List lLstResData = null;
			String lStrHoRoleFlag = "";
			
			try{
				StringBuilder lSBQuery = new StringBuilder();
				lSBQuery.append(" SELECT post.POST_ID FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post ");
				lSBQuery.append(" on role.POST_ID = post.POST_ID and  role.ACTIVATE_FLAG =1 and role.role_id = 8000016 ");
				lSBQuery.append(" where post.LOC_ID = "+locCode);
				Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstResData = lQuery.list();
				
				if(lLstResData != null && lLstResData.size() > 0){
					lStrHoRoleFlag =lQuery.list().get(0).toString();
				}else{
					lStrHoRoleFlag = "N";
				}
			}catch(Exception e){
				 logger.error(" Error is : " + e, e);
				  throw e; 
			}
			
			return lStrHoRoleFlag;
		}
		
		//swt 11/01/2021
				public String chkEmpGroupRhoEntry(Long lLngPostId, String gStrLocationCode) {
					logger.error("chkRoleVerifierEntry");
					
					List lLstResData = null;
					String chkEmpGroupVerifierEntry = "";
						try{
						StringBuilder lSBQuery = new StringBuilder();
						lSBQuery.append(" SELECT MST.SEVARTH_ID FROM MST_DCPS_EMP MST ");
						lSBQuery.append(" inner join ORG_EMP_MST ORG ON ORG.EMP_ID = MST.ORG_EMP_MST_ID INNER JOIN ORG_USER_MST user on user.USER_ID=org.USER_ID INNER JOIN ORG_USERPOST_RLT post ON post.USER_ID=user.USER_ID ");
						//lSBQuery.append(" where MST.DDO_CODE='"+gStrLocationCode+"' AND post.POST_ID='"+lLngPostId+"' AND ORG.GRADE_ID IN (100001,100064,100065) ");
				        lSBQuery.append(" where MST.DDO_CODE='"+gStrLocationCode+"' AND post.POST_ID='"+lLngPostId+"' ");
						Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
						lLstResData = lQuery.list();
						
						if(lLstResData != null && lLstResData.size() > 0){
							chkEmpGroupVerifierEntry = "Y";
						}else{
							chkEmpGroupVerifierEntry = "N";
						}
					}catch(Exception e){
						 logger.error(" Error is : " + e, e);
						  throw e; 
					}
					
					return chkEmpGroupVerifierEntry;
				}	
			
				
				public String chkEntryForDeo(String lStrLocationCode)throws Exception
				{
					List lLstResData = null;
					String lStrVerifierFlag = "";
					
					ArrayList lLstDoc = new ArrayList<Long>();		    
					  
					lLstDoc.add(Long.parseLong("800001"));
					lLstDoc.add(Long.parseLong("800002"));
					lLstDoc.add(Long.parseLong("800003"));
					
					try{
						StringBuilder lSBQuery = new StringBuilder();
						lSBQuery.append("SELECT WPM.hierachyRefId FROM WfHierachyPostMpg WPM, WfHierarchyReferenceMst WRM ");
						lSBQuery.append("WHERE WPM.levelId = 10 AND WPM.wfOrgLocMpgMst.locId = :locCode ");
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
				
				public String chkRoleDeoEntry(long postId, String locCode) throws Exception{

					logger.error("chkRoleHoEntry");
					
					List lLstResData = null;
					String lStrHoRoleFlag = "";
					
					try{
						StringBuilder lSBQuery = new StringBuilder();
						lSBQuery.append(" SELECT * FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post ");
						lSBQuery.append(" on role.POST_ID = post.POST_ID and  role.ACTIVATE_FLAG =1 and role.role_id = 800005 ");
						lSBQuery.append(" where role.post_id='"+postId+"' and LOC_ID = '"+locCode+"' ");
						Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
						lLstResData = lQuery.list();
						
						if(lLstResData != null && lLstResData.size() > 0){
							lStrHoRoleFlag = "Y";
						}else{
							lStrHoRoleFlag = "N";
						}
					}catch(Exception e){
						 logger.error(" Error is : " + e, e);
						  throw e; 
					}
					
					return lStrHoRoleFlag;
				}
				
				public String getPostRoleRHOASTTMdc(String Locid,String lLngPostId) throws Exception{
					
					logger.error("getLocid"+Locid);

					
					List lLstPostIdList = null;
					String lStrPostId ="";
					
					
					try{
						StringBuilder lSBQuery = new StringBuilder();
						
						/*lSBQuery.append(" SELECT post.post_id FROM org_user_mst user, org_userpost_rlt post ");
						lSBQuery.append(" where user.user_id=post.user_id ");
						lSBQuery.append(" and user.user_name=:lStrUserName ");
						
						*/
						lSBQuery.append(" SELECT role.post_role_id FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post "); 
						lSBQuery.append(" on post.POST_ID = role.POST_ID   inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID ");
						lSBQuery.append(" and usr.ACTIVATE_FLAG = 1  inner join org_emp_mst emp on emp.user_id = usr.user_id "); 
						lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID"); 
						lSBQuery.append(" where post.LOC_ID = '"+Locid+"' and usr.POST_ID ='"+lLngPostId+"' and role.ROLE_ID = 8000016 and role.ACTIVATE_FLAG =1 "); 

						
						Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
						//lQuery.setParameter("lStrUserName", lStrUserName);
						
						lLstPostIdList = lQuery.list();
						
						logger.error("lSBQuery***getPostRoleID*******"+lSBQuery.toString());
						
						/*if(lLstPostIdList != null && lLstPostIdList.size() > 0){
							lStrPostId = "Y";
						}else{
							lStrPostId = "N";
						}*/
						
						if(lLstPostIdList != null && lLstPostIdList.size() > 0){
							lStrPostId = lLstPostIdList.get(0).toString();
						}
						
						logger.error("lStrPostId"+lStrPostId);
					}
					
					catch(Exception e){
						logger.error(" Error in getPostId : " + e, e);
						
					}
					return lStrPostId;
					
					
				}		
				
				public List<Long> getAllHierarchyRefIdsForLocation(Long LocationCode) {

					List<Long> listHierarchyRefIds = null;

					StringBuilder lSBQuery = new StringBuilder();

					Query lQuery = null;

					lSBQuery.append(" SELECT DISTINCT WFP.HIERACHY_REF_ID FROM WF_HIERACHY_POST_MPG WFP");
					lSBQuery.append(" JOIN WF_HIERARCHY_REFERENCE_MST WFR ON WFP.HIERACHY_REF_ID = WFR.HIERACHY_REF_ID ");
					lSBQuery.append(" JOIN WF_DOC_MST WFD ON WFR.DOC_ID = WFD.DOC_ID");
					lSBQuery.append(" WHERE WFD.DOC_ID in (800001,800002,800003) and ");
					lSBQuery.append(" WFP.LOC_ID = " + LocationCode );

					lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

					listHierarchyRefIds = lQuery.list();

					return listHierarchyRefIds;

				}
				
				public Boolean checkEntryInWfHierachyPostMpg(Long lLongHierarchyRefId,Long lLongPostId,String levelId) {

					StringBuilder lSBQuery = new StringBuilder();
					List<Long> tempList = new ArrayList();
					Boolean flag = true;

					lSBQuery.append(" SELECT * FROM WF_HIERACHY_POST_MPG WFP where WFP.HIERACHY_REF_ID = " + lLongHierarchyRefId + " and WFP.POST_ID = '"+ lLongPostId  +"' and WFP.LEVEL_ID ='"+ levelId +"' ");
					Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

					tempList = lQuery.list();
					if (tempList.size() == 0) {
						flag = false;
					}
					return flag;

				}
				public List getValidateDdoCode(String txtDdoCodeEmp,String lStrDdoName)throws Exception
				{
					List lLstResData = null;
					//String lStrDdoCode = "";
					
					
					logger.error(" lLngPostId: " +txtDdoCodeEmp);
					
					try{
						StringBuilder lSBQuery = new StringBuilder();
						lSBQuery.append("SELECT ddoCode,ddoName FROM OrgDdoMst WHERE ddoCode ='"+ txtDdoCodeEmp  +"' or ddoName='"+ lStrDdoName  +"' ");
						Query lQuery = ghibSession.createQuery(lSBQuery.toString());
						//lQuery.setLong("postId", txtDdoCodeEmp);
						lLstResData = lQuery.list();
						
					}catch(Exception e){
						logger.error(" Error is : " + e, e);
						throw e;
					}
					return lLstResData;
				}	
				
				public List getValidateSevarthId(String txtDdoCodeEmp,String lStrSevaarthId,String lStrEmployeeName)throws Exception
				{
					List lLstResData = null;
					
					logger.error(" lLngPostId: " +txtDdoCodeEmp);
					
					try{
						StringBuilder lSBQuery = new StringBuilder();
						//lSBQuery.append("SELECT SEVARTH_ID,EMP_NAME,DDO_CODE FROM mst_dcps_emp where ddo_Code ='"+ txtDdoCodeEmp  +"' and sevarth_Id ='"+ lStrSevaarthId  +"' ");
						lSBQuery.append("SELECT SEVARTH_ID,EMP_NAME,DDO_CODE FROM mst_dcps_emp where ddo_Code ='"+ txtDdoCodeEmp  +"' and (sevarth_Id ='"+ lStrSevaarthId  +"' or emp_name='"+ lStrEmployeeName  +"') ");
						Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
						lLstResData = lQuery.list();
					
					}catch(Exception e){
						logger.error(" Error is : " + e, e);
						throw e;
					}
					return lLstResData;
				}
				

				public String getServiceEnd(String lStrSevaarthId,String lStrEmployeeName,String lStrDdoCode) {
					
					Date empDOSupAnn;
					String statusFlag = "";
					String statusFlagc = "";
					String flag = "y";
					try {
						
						StringBuilder lSBQuery = new StringBuilder();
						lSBQuery.append(" SELECT org.EMP_SRVC_EXP FROM MST_DCPS_EMP dcps, ORG_EMP_MST org where org.EMP_ID=dcps.ORG_EMP_MST_ID and dcps.ddo_code ='"+ lStrDdoCode  +"' and (dcps.SEVARTH_ID=:SevaarthId or dcps.emp_name='"+ lStrEmployeeName  +"') ");

						Query lQuery1 = ghibSession.createSQLQuery(lSBQuery.toString());
						lQuery1.setParameter("SevaarthId", lStrSevaarthId);
						
						

						empDOSupAnn = (Date) lQuery1.uniqueResult();
						logger.info("empDOSupAnn"+empDOSupAnn);


						logger.info("query"+lSBQuery.toString());
						//if(empDOSupAnn != null && ((List) empDOSupAnn).size() > 0){
						Date futureDate = empDOSupAnn;
						int supyear = futureDate.getYear()+1900;
						int supmonth = futureDate.getMonth()+1;
						int supday = futureDate.getDate();

						logger.error("supyear"+supyear);
						logger.error("supmonth"+supmonth);
						logger.error("supday"+supday);

						Date currDate = new Date();
						int curryear = 1900 + currDate.getYear();
						int currmonth = currDate.getMonth() + 1;
						int currday = currDate.getDay();

						logger.error("curryear"+curryear);
						logger.error("currmonth"+currmonth);
						logger.error("currday"+currday);

						/*if(curryear == supyear){
							flag = "n";
						}*/

						if(supyear - curryear < 0){
							flag = "n";
						}

						if(supyear - curryear == 0 && currmonth > supmonth ){
							flag = "n";
			}
							} catch (Exception e) {
						logger.error(" Error is : " + e, e);
					}
					return flag;

				}
			
				
				public String getDdoNameFromDdoCode(String lStrDdoCode) {
					List lLstResData = null;
					String lStrDdoCodeEmp ="";
					logger.error(" lLngPostId: " +lStrDdoCode);
					
					try{
						StringBuilder lSBQuery = new StringBuilder();
						lSBQuery.append("SELECT ddoName FROM OrgDdoMst WHERE ddoCode ='"+ lStrDdoCode  +"' ");
						Query lQuery = ghibSession.createQuery(lSBQuery.toString());
						//lQuery.setLong("postId", txtDdoCodeEmp);
						lLstResData = lQuery.list();
						
						if(lLstResData != null && lLstResData.size() > 0){
							lStrDdoCodeEmp = lLstResData.get(0).toString();
							logger.error(" lStrDdoCode: " +lStrDdoCodeEmp);
						}
						
					}catch(Exception e){
						logger.error(" Error is : " + e, e);
						throw e;
					}
					return lStrDdoCodeEmp;
				}	
				
public void unlockAccountForuserId(Long lLnguserId) {

			StringBuilder lSBQuery = new StringBuilder();
			//lSBQuery.append(" update org_user_mst set ACTIVATE_FLAG = 1,PWDCHANGED_DATE = null where USER_ID = (select USER_ID from org_emp_mst where EMP_ID = " + lLongOrgEmpMstId + ")");
			lSBQuery.append(" update org_user_mst set PASSWORD = '0b76f0f411f6944f9d192da0fcbfb292' where USER_ID = '"+ lLnguserId  +"' ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.executeUpdate();
		}
		
		public String getEmpNameFromSevaarthId(String lStrSevaarthId) {
			List<String> lLstEmpName = new ArrayList<String>();
			String lStrEmpName = "";
			try {
				StringBuilder lSBQuery = new StringBuilder();
				lSBQuery.append("SELECT name");
				lSBQuery.append(" FROM MstEmp WHERE sevarthId = :SevaarthId ");			
				Query lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("SevaarthId", lStrSevaarthId);
				lLstEmpName = lQuery.list();
				if(!lLstEmpName.isEmpty())
					lStrEmpName = lLstEmpName.get(0);

			} catch (Exception e) {
				logger.error(" Error is : " + e, e);
			}
			return lStrEmpName;
		}
		
		
		public String getSevaId(String lStrSevaarthId)throws Exception
		{
			List lLstResData = null;
			String lStrEmpCode = "";
			
			
			logger.error(" lStrSevaarthId: " +lStrSevaarthId);
			
			try{
				StringBuilder lSBQuery = new StringBuilder();
				lSBQuery.append("SELECT sevarthId FROM MstEmp WHERE sevarthId = :SevaarthId");
				Query lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("SevaarthId", lStrSevaarthId);
				lLstResData = lQuery.list();
				
				if(lLstResData.size() > 0){
					lStrEmpCode = "Y";
				}else{
					lStrEmpCode = "N";
				}
			}catch(Exception e){
				logger.error(" Error is : " + e, e);
				throw e;
			}
			return lStrEmpCode;
		}
		
		public String getEmpName(String lStrEmployeeName,String lStrDdoCode)throws Exception
		{
			List lLstResData = null;
			String lStrEmpCode = "";
			
			
			logger.error(" lStrEmployeeName: " +lStrEmployeeName);
			
			try{
				StringBuilder lSBQuery = new StringBuilder();
				lSBQuery.append("SELECT emp_name FROM Mst_dcps_Emp WHERE emp_name = '"+ lStrEmployeeName  +"' and ddo_Code = '"+ lStrDdoCode  +"' ");
				Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				//lQuery.setParameter("EmployeeName", lStrEmployeeName);
				//lQuery.setParameter("DdoCode", lStrDdoCode);
				lLstResData = lQuery.list();
				
				if(lLstResData.size() > 0){
					lStrEmpCode = "Y";
				}else{
					lStrEmpCode = "N";
				}
			}catch(Exception e){
				logger.error(" Error is : " + e, e);
				throw e;
			}
			return lStrEmpCode;
		}
		
		public List getEmpNameForAutoComplete(String searchKey, String lStrDdoCode,String lStrUser) {
			ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
			ComboValuesVO cmbVO;
			Object[] obj;

			StringBuilder lSBQuery = new StringBuilder();
			Query lQuery = null;
			lSBQuery.append("select ME.orgEmpMstId,ME.name from MstEmp ME,OrgDdoMst ODM where UPPER(ME.name) LIKE :searchKey");
			lSBQuery.append("  AND ME.ddoCode = ODM.ddoCode ");
			//lSBQuery.append("   AND ODM.hodLocCode = :HodLocCode");
			if (lStrDdoCode != null && !"".equals(lStrDdoCode)) {
				lSBQuery.append(" and ODM.ddoCode = :ddoCode");
			}
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("searchKey", '%' + searchKey + '%');
			//lQuery.setParameter("HodLocCode", lStrHodLocCode);
			if (lStrDdoCode != null && !"".equals(lStrDdoCode)) {
				lQuery.setParameter("ddoCode", lStrDdoCode);
			}
			List resultList = lQuery.list();

			if (resultList != null && !resultList.isEmpty()) {
				Iterator it = resultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					cmbVO.setId(obj[1].toString());
					cmbVO.setDesc(obj[1].toString());
					finalList.add(cmbVO);
				}
			}

			return finalList;
		}	
		
		public List getDdoNameForAutoComplete(String searchKey) {
			ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
			ComboValuesVO cmbVO;
			Object[] obj;

			StringBuilder lSBQuery = new StringBuilder();
			Query lQuery = null;
			//lSBQuery.append("select ODM.ddoId,ODM.ddoName from OrgDdoMst ODM where UPPER(ODM.ddoName) LIKE :searchKey");
			lSBQuery.append("select ODM.locationCode,ODM.ddoName from OrgDdoMst ODM where UPPER(ODM.ddoName) LIKE :searchKey");

			
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("searchKey", '%' + searchKey + '%');
        	List resultList = lQuery.list();

			if (resultList != null && !resultList.isEmpty()) {
				Iterator it = resultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					cmbVO.setId(obj[1].toString());
					cmbVO.setDesc(obj[1].toString());
					finalList.add(cmbVO);
				}
			}

			return finalList;
		}		
		//swt 08/02/2020
				public String getEmpDdoCode(String ddoName) {

					StringBuilder lSBQuery = new StringBuilder();
					List lLstCodeList = null;
					String lStrDdoCode = "";
					try {
						lSBQuery.append(" SELECT ddoCode ");
						lSBQuery.append(" FROM OrgDdoMst ");
						lSBQuery.append(" WHERE ddoName= :lStrDdoName  ");
						Query lQuery = ghibSession.createQuery(lSBQuery.toString());
						lQuery.setParameter("lStrDdoName", ddoName);

						lLstCodeList = lQuery.list();
						if(lLstCodeList != null && lLstCodeList.size() > 0){
						
							lStrDdoCode = lLstCodeList.get(0).toString();
						}
						
						//lStrDdoCode = lLstCodeList.get(0).toString();
					} catch (Exception e) {
						logger.error("Exception in getDdoCode of GPFRequestProcessDAOImpl  : ", e);			
					}
					return lStrDdoCode;
				}	
				
				//swt 11/02/2021
				/*public List getEmployeeRoleDtls(Long lLngUserId)throws Exception
				{
					List lLstResdata = null;
					String lStrUsername = "";
					
					try{
						StringBuilder lSBQuery = new StringBuilder();
						//lSBQuery.append(" SELECT user.USER_NAME FROM ORG_USER_MST user where user.USER_ID=:lLngUserId ");
						lSBQuery.append(" SELECT mst.sevarth_id,mst.emp_name FROM mst_dcps_emp mst inner join org_emp_mst org on org.emp_id=mst.ORG_EMP_MST_ID where org.USER_ID=:lLngUserId ");
									
						Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
						lQuery.setParameter("lLngUserId", lLngUserId);
						
						lLstResdata = lQuery.list();
						
						if(lLstResdata != null && lLstResdata.size() > 0){
							lStrUsername = lLstResdata.get(0).toString();
						}
					}catch(Exception e){
						 logger.error(" Error is : " + e, e);
						  throw e; 
					}
					
					return lLstResdata;
				}	*/
				
				public String chkEmpGroupHoEntryMDC(Long lLngPostId, String gStrLocationCode) {
					logger.error("chkRoleHoEntry");
					
					List lLstResData = null;
					String chkEmpGroupHoEntryflag = "";
						try{
						StringBuilder lSBQuery = new StringBuilder();
						lSBQuery.append(" SELECT MST.SEVARTH_ID FROM MST_DCPS_EMP MST ");
						lSBQuery.append(" inner join ORG_EMP_MST ORG ON ORG.EMP_ID = MST.ORG_EMP_MST_ID INNER JOIN ORG_USER_MST user on user.USER_ID=org.USER_ID INNER JOIN ORG_USERPOST_RLT post ON post.USER_ID=user.USER_ID ");
						lSBQuery.append(" where MST.DDO_CODE='"+gStrLocationCode+"' AND post.POST_ID='"+lLngPostId+"' ");
				        //lSBQuery.append(" where MST.DDO_CODE='"+gStrLocationCode+"' AND post.POST_ID='"+lLngPostId+"' ");
						Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
						lLstResData = lQuery.list();
						
						if(lLstResData != null && lLstResData.size() > 0){
							chkEmpGroupHoEntryflag = "Y";
						}else{
							chkEmpGroupHoEntryflag = "N";
						}
					}catch(Exception e){
						 logger.error(" Error is : " + e, e);
						  throw e; 
					}
					
					return chkEmpGroupHoEntryflag;
				}
				
	
				public int checkPendingRequestDEOMdc(String username)
				{

					logger.error("checkPendingRequest");
					logger.error("UserName"+username);
					//int res1=0;
					int res2=0;
					int res3=0;
					int res4=0;
					Session ghibSession = getSession();
					username=username.trim();
					try
					{
						
						StringBuilder lBSQuery1=new StringBuilder();
						lBSQuery1.append(" select count(1) ");
						lBSQuery1.append(" FROM Mst_Gpf_Advance MGA, Mst_Emp_Gpf_Acc MEG, Mst_dcps_Emp ME ");
						lBSQuery1.append(" WHERE MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.sevaarth_id = MEG.sevaarth_id AND MGA.status_Flag IN ('D','R','DR') AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
						lBSQuery1.append(" AND ME.EMP_GROUP='D' AND ME.ddo_Code=(select ddo_code from org_ddo_mst odm inner join ORG_POST_DETAILS_RLT opdr on odm.LOCATION_CODE=opdr.LOC_ID ");
						lBSQuery1.append(" inner join ORG_USERPOST_RLT our on our.POST_ID=opdr.POST_ID ");
						lBSQuery1.append(" inner join ORG_USER_MST oum on oum.USER_ID=our.USER_ID ");
						lBSQuery1.append(" where oum.USER_NAME='"+username+"' and our.ACTIVATE_FLAG=1) ");
						Query lQuery1=ghibSession.createSQLQuery(lBSQuery1.toString());
						res2=Integer.parseInt(lQuery1.uniqueResult().toString());
						
						
						StringBuilder lBSQuery2=new StringBuilder();
						lBSQuery2.append(" SELECT count(1) ");
						lBSQuery2.append(" FROM TRN_GPF_FINAL_WITHDRAWAL tgfw, MST_EMP_GPF_ACC mega, MST_DCPS_EMP mde ");
						lBSQuery2.append(" where tgfw.GPF_ACC_NO=mega.GPF_ACC_NO and tgfw.sevaarth_id=mega.sevaarth_id and mega.MST_GPF_EMP_ID=mde.DCPS_EMP_ID ");
						lBSQuery2.append(" and tgfw.REQ_STATUS in ('D','DR','R') and mde.DCPS_OR_GPF='N' and mde.EMP_GROUP='D' and mde.DDO_CODE= ");
						lBSQuery2.append(" (select ddo_code from org_ddo_mst odm inner join ORG_POST_DETAILS_RLT opdr on odm.LOCATION_CODE=opdr.LOC_ID ");
						lBSQuery2.append(" inner join ORG_USERPOST_RLT our on our.POST_ID=opdr.POST_ID ");
						lBSQuery2.append(" inner join ORG_USER_MST oum on oum.USER_ID=our.USER_ID ");
						lBSQuery2.append(" where oum.USER_NAME='"+username+"' and our.ACTIVATE_FLAG=1) ");
						Query lQuery2=ghibSession.createSQLQuery(lBSQuery2.toString());
						res3=Integer.parseInt(lQuery2.uniqueResult().toString());
					
						res4=res2+res3;
						logger.info("no of request pending under DEO is"+res4);
					}
					catch(Exception e)
					{
						logger.error("CheckPendingRequestDEOException");
					}
					return res4;
					
				}	
				
				public List chkPendingReqDeoAdvance(String username) {
					

					logger.error("getActiveHOLst");
					logger.error("lLngPostId"+username);
					
					
					 List lLstpendingReq = null;
					 Session ghibSession = getSession();
					 try{
					StringBuilder lSBQuery = new StringBuilder();
					
					 lSBQuery.append(" select MGA.MST_GPF_ADVANCE_id,MGA.advance_type ");
					 lSBQuery.append(" FROM Mst_Gpf_Advance MGA, Mst_Emp_Gpf_Acc MEG, Mst_dcps_Emp ME ");
					 lSBQuery.append(" WHERE MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.sevaarth_id = MEG.sevaarth_id AND MGA.status_Flag IN ('D','R','DR') AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
					 lSBQuery.append(" AND ME.EMP_GROUP='D' AND ME.ddo_Code=(select ddo_code from org_ddo_mst odm inner join ORG_POST_DETAILS_RLT opdr on odm.LOCATION_CODE=opdr.LOC_ID ");
					 lSBQuery.append(" inner join ORG_USERPOST_RLT our on our.POST_ID=opdr.POST_ID ");
					 lSBQuery.append(" inner join ORG_USER_MST oum on oum.USER_ID=our.USER_ID ");
					 lSBQuery.append(" where oum.USER_NAME='"+username+"' and our.ACTIVATE_FLAG=1) ");
					
					Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
					//lQuery.setParameter("lLngPostId", lLngPostId);
					lLstpendingReq = lQuery.list();
					//lQuery.setParameter("locCode", locCode);
						
					logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
					logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstpendingReq.size());
					
					if(lLstpendingReq != null && lLstpendingReq.size()> 0)
					{
						lLstpendingReq = lQuery.list();
					}
					
					 }catch(Exception e){
						 logger.error(" Error is : " + e, e);			 
					 }
					 return lLstpendingReq;
				}
				
				
         public List chkPendingReqDeoFinal(String username) {
					
					 List lLstpendingReq = null;
					 Session ghibSession = getSession();
					 try{
					StringBuilder lSBQuery = new StringBuilder();
					
					 lSBQuery.append(" SELECT tgfw.TRN_GPF_FINAL_WITHDRAWAL_ID,'FW' as Advance_type ");
					 lSBQuery.append(" FROM TRN_GPF_FINAL_WITHDRAWAL tgfw, MST_EMP_GPF_ACC mega, MST_DCPS_EMP mde ");
					 lSBQuery.append(" where tgfw.GPF_ACC_NO=mega.GPF_ACC_NO and tgfw.sevaarth_id=mega.sevaarth_id and mega.MST_GPF_EMP_ID=mde.DCPS_EMP_ID ");
					 lSBQuery.append(" and tgfw.REQ_STATUS in ('D','DR','R') and mde.DCPS_OR_GPF='N' and mde.EMP_GROUP='D' and mde.DDO_CODE=  ");
					 lSBQuery.append(" (select ddo_code from org_ddo_mst odm inner join ORG_POST_DETAILS_RLT opdr on odm.LOCATION_CODE=opdr.LOC_ID ");
					 lSBQuery.append(" inner join ORG_USERPOST_RLT our on our.POST_ID=opdr.POST_ID ");
					 lSBQuery.append(" inner join ORG_USER_MST oum on oum.USER_ID=our.USER_ID ");
					 lSBQuery.append(" where oum.USER_NAME='"+username+"' and our.ACTIVATE_FLAG=1) ");
					
					
					Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
					//lQuery.setParameter("lLngPostId", lLngPostId);
					lLstpendingReq = lQuery.list();
					//lQuery.setParameter("locCode", locCode);
						
					logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
					logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstpendingReq.size());
					
					if(lLstpendingReq != null && lLstpendingReq.size()> 0)
					{
						lLstpendingReq = lQuery.list();
					}
					
					 }catch(Exception e){
						 logger.error(" Error is : " + e, e);			 
					 }
					 return lLstpendingReq;
				}
         
         public int checkPendingRequestVerifierMdc(String username)
         {
         	logger.error("checkPendingRequest");
         	logger.error("UserName"+username);
         	//int res1=0;
         	int res2=0;
         	int res3=0;
         	int res4=0;
         	List checkList1 = null;
         	List checkList2 = null;
         	List checkList3 = null;
         	Session ghibSession = getSession();
         	username=username.trim();
         	try
         	{
         		//The following 3 queries check if there is any loan request is pending
         		
         		
         		logger.info("Before 2nd query");
         		StringBuilder lSBQuery2 = new StringBuilder();
         		lSBQuery2.append("select count(1) ");
         		lSBQuery2.append(" FROM IFMS.MST_GPF_ADVANCE MGA,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
         		lSBQuery2.append(" WHERE MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.sevaarth_id = MEG.sevaarth_id AND MGA.status_Flag='F1' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
         		lSBQuery2.append(" AND WJ.job_Ref_Id = MGA.mst_Gpf_Advance_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
         		lSBQuery2.append(" AND WJ.doc_Id = 800002 AND ME.EMP_group ='D' AND MGA.data_Entry is null");
         		Query lQuery2 = ghibSession.createSQLQuery(lSBQuery2.toString());
         		res2=Integer.parseInt(lQuery2.uniqueResult().toString());
         		logger.info("res2="+res2);
         		
         		logger.info("Before 3rd query");
         		StringBuilder lSBQuery3 = new StringBuilder();
         		lSBQuery3.append("select count(1) ");
         		lSBQuery3.append(" FROM Trn_Gpf_Final_Withdrawal TGF,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
         		lSBQuery3.append(" WHERE TGF.gpf_Acc_No = MEG.gpf_Acc_No and TGF.sevaarth_id = MEG.sevaarth_id and TGF.req_Status='F1' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
         		lSBQuery3.append(" AND WJ.job_Ref_Id = TGF.trn_Gpf_Final_Withdrawal_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
         		lSBQuery3.append(" AND WJ.doc_Id =800003 AND ME.EMP_group ='D'");
         		Query lQuery3 = ghibSession.createSQLQuery(lSBQuery3.toString());
         		res3=Integer.parseInt(lQuery3.uniqueResult().toString());
         		
         		res4=res2+res3;
         		logger.info("No of requests="+res4);
         	}
         	catch (Exception e) {
         		logger.error("CheckPendingRequestException");
         	}
         	return res4;
         	
         }
         
         public List chkPendingReqVeriAdvance(String username) {
				

				logger.error("getActiveHOLst");
				logger.error("lLngPostId"+username);
				
				
				 List lLstpendingReq = null;
				 Session ghibSession = getSession();
				 try{
				StringBuilder lSBQuery = new StringBuilder();
				
				lSBQuery.append(" select MGA.MST_GPF_ADVANCE_ID,MGA.ADVANCE_TYPE ");
				 lSBQuery.append(" FROM IFMS.MST_GPF_ADVANCE MGA,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
				 lSBQuery.append(" WHERE MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.sevaarth_id = MEG.sevaarth_id AND MGA.status_Flag='F1' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
				 lSBQuery.append(" AND WJ.job_Ref_Id = MGA.mst_Gpf_Advance_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
				 lSBQuery.append(" AND WJ.doc_Id = 800002 AND ME.EMP_group ='D' AND MGA.data_Entry is null ");
			
				
				Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				//lQuery.setParameter("lLngPostId", lLngPostId);
				lLstpendingReq = lQuery.list();
				//lQuery.setParameter("locCode", locCode);
					
				logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
				logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstpendingReq.size());
				
				if(lLstpendingReq != null && lLstpendingReq.size()> 0)
				{
					lLstpendingReq = lQuery.list();
				}
				
				 }catch(Exception e){
					 logger.error(" Error is : " + e, e);			 
				 }
				 return lLstpendingReq;
			}
			
			
  public List chkPendingReqVeriFinal(String username) {
				
				 List lLstpendingReq = null;
				 Session ghibSession = getSession();
				 try{
				StringBuilder lSBQuery = new StringBuilder();
				
				 
					lSBQuery.append(" select TGF.TRN_GPF_FINAL_WITHDRAWAL_ID,'FW' as Advance_type ");
					lSBQuery.append("  FROM Trn_Gpf_Final_Withdrawal TGF,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
					lSBQuery.append("  WHERE TGF.gpf_Acc_No = MEG.gpf_Acc_No and TGF.sevaarth_id = MEG.sevaarth_id and TGF.req_Status='F1' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
					lSBQuery.append("  AND WJ.job_Ref_Id = TGF.trn_Gpf_Final_Withdrawal_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
					lSBQuery.append("  AND WJ.doc_Id =800003 AND ME.EMP_group ='D' ");
				
				
				Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				//lQuery.setParameter("lLngPostId", lLngPostId);
				lLstpendingReq = lQuery.list();
				//lQuery.setParameter("locCode", locCode);
					
				logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
				logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstpendingReq.size());
				
				if(lLstpendingReq != null && lLstpendingReq.size()> 0)
				{
					lLstpendingReq = lQuery.list();
				}
				
				 }catch(Exception e){
					 logger.error(" Error is : " + e, e);			 
				 }
				 return lLstpendingReq;
			}
				
			

						
				public String updatePendingReqDeoAdvance(String MstGpfAdvId)throws Exception
				{
					String lStrPendingId = "";
					logger.error("updatePendingReqDeoAdvance called"+MstGpfAdvId);
					try{
						StringBuilder lSBQuery = new StringBuilder();
						
						lSBQuery.append(" UPDATE Mst_Gpf_Advance SET STATUS_FLAG='DX' WHERE MST_GPF_ADVANCE_ID='"+MstGpfAdvId+"' ");
                        Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
						lQuery.executeUpdate();
						lStrPendingId = String.valueOf(lQuery.executeUpdate());
						
					}catch(Exception e){
						logger.error(" Error is : " + e, e);
						throw e;
					}
					
					return lStrPendingId;
				}
				
				public String updatePendingReqDeoFinal(String MstGpfAdvId)throws Exception
				{
					String lStrPendingId = "";
					logger.error("updatePendingReqDeoAdvance called"+MstGpfAdvId);
					try{
						StringBuilder lSBQuery = new StringBuilder();
						
						lSBQuery.append(" UPDATE TRN_GPF_FINAL_WITHDRAWAL SET REQ_STATUS='DX' WHERE TRN_GPF_FINAL_WITHDRAWAL_ID='"+MstGpfAdvId+"' ");
                        Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
						lQuery.executeUpdate();
						lStrPendingId = String.valueOf(lQuery.executeUpdate());
						
					}catch(Exception e){
						logger.error(" Error is : " + e, e);
						throw e;
					}
					
					return lStrPendingId;
				}
				
				public String updatePendingReqVeriAdvance(String MstGpfAdvId)throws Exception
				{
					String lStrPendingId = "";
					logger.error("updatePendingReqDeoAdvance called"+MstGpfAdvId);
					try{
						StringBuilder lSBQuery = new StringBuilder();
						
						lSBQuery.append(" UPDATE Mst_Gpf_Advance SET STATUS_FLAG='R' WHERE MST_GPF_ADVANCE_ID='"+MstGpfAdvId+"' ");
                        Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
						lQuery.executeUpdate();
						lStrPendingId = String.valueOf(lQuery.executeUpdate());
						
					}catch(Exception e){
						logger.error(" Error is : " + e, e);
						throw e;
					}
					
					return lStrPendingId;
				}
				
				public String updatePendingReqVeriFinal(String MstGpfAdvId)throws Exception
				{
					String lStrPendingId = "";
					logger.error("updatePendingReqDeoAdvance called"+MstGpfAdvId);
					try{
						StringBuilder lSBQuery = new StringBuilder();
						
						lSBQuery.append(" UPDATE TRN_GPF_FINAL_WITHDRAWAL SET REQ_STATUS='R' WHERE TRN_GPF_FINAL_WITHDRAWAL_ID='"+MstGpfAdvId+"' ");
                        Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
						lQuery.executeUpdate();
						lStrPendingId = String.valueOf(lQuery.executeUpdate());
						
					}catch(Exception e){
						logger.error(" Error is : " + e, e);
						throw e;
					}
					
					return lStrPendingId;
				}
				
				public int checkPendingRequestHOmdc(String username)
				{
					logger.error("checkPendingRequest");
					logger.error("UserName"+username);
					int res1=0;
					int res2=0;
					int res3=0;
					int res4=0;
					int res5=0;
					int res6=0;
					int res7=0;
					int res8=0;
					int res9=0;
					List checkList1 = null;
					List checkList2 = null;
					List checkList3 = null;
					Session ghibSession = getSession();
					username=username.trim();
					try
					{
						
						logger.info("Before 2nd query");
						StringBuilder lSBQuery2 = new StringBuilder();
						lSBQuery2.append("select count(1) ");
						lSBQuery2.append(" FROM IFMS.MST_GPF_ADVANCE MGA,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
						lSBQuery2.append(" WHERE MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.SEVAARTH_ID = MEG.SEVAARTH_ID AND MGA.status_Flag in ('F2','F4') AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
						lSBQuery2.append(" AND WJ.job_Ref_Id = MGA.mst_Gpf_Advance_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
						lSBQuery2.append(" AND WJ.doc_Id = 800002 AND ME.EMP_group ='D' AND MGA.data_Entry is null");
						Query lQuery2 = ghibSession.createSQLQuery(lSBQuery2.toString());
						res2=Integer.parseInt(lQuery2.uniqueResult().toString());
						/*checkList2=lQuery2.list();
						res2=checkList2.size();*/
						
						logger.info("res2="+res2);
						logger.info("Before 3rd query");
						StringBuilder lSBQuery3 = new StringBuilder();
						lSBQuery3.append("select count(1) ");
						lSBQuery3.append(" FROM Trn_Gpf_Final_Withdrawal TGF,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
						lSBQuery3.append(" WHERE TGF.gpf_Acc_No = MEG.gpf_Acc_No AND TGF.SEVAARTH_ID = MEG.SEVAARTH_ID AND TGF.req_Status in ('F2','F4') AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
						lSBQuery3.append(" AND WJ.job_Ref_Id = TGF.trn_Gpf_Final_Withdrawal_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
						lSBQuery3.append(" AND WJ.doc_Id =800003 AND ME.EMP_group ='D'");
						Query lQuery3 = ghibSession.createSQLQuery(lSBQuery3.toString());
						res3=Integer.parseInt(lQuery3.uniqueResult().toString());
					
						StringBuilder lSBQuery6 = new StringBuilder();
						lSBQuery6.append("select count(1) ");
						lSBQuery6.append(" FROM trn_emp_gpf_acc TEG, Mst_Emp_Gpf_Acc MEG, Org_Ddo_Mst ODM, Mst_Gpf_Yearly GPF  WHERE TEG.status_Flag = 'F' ");
						lSBQuery6.append(" AND TEG.sevaarth_Id = MEG.sevaarth_Id AND MEG.ddo_Code = ODM.ddo_Code  AND GPF.gpf_Acc_No=MEG.gpf_Acc_No AND GPF.sevaarth_id=MEG.sevaarth_id AND ODM.location_Code = (SELECT loc_id ");
						lSBQuery6.append(" FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"') ");
						Query lQuery6 = ghibSession.createSQLQuery(lSBQuery6.toString());
						res6=Integer.parseInt(lQuery6.uniqueResult().toString());
						
						StringBuilder lSBQuery1 = new StringBuilder();

					     lSBQuery1.append(" select count(1) from (  ");
					    lSBQuery1.append(" (SELECT MGA.TRANSACTION_ID ");
					    lSBQuery1.append(" FROM IFMS.MST_GPF_ADVANCE MGA,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
					    lSBQuery1.append(" WHERE  MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.SEVAARTH_ID = MEG.SEVAARTH_ID AND MGA.status_Flag='A' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
					    lSBQuery1.append(" AND WJ.job_Ref_Id = MGA.mst_Gpf_Advance_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
					    lSBQuery1.append(" AND WJ.doc_Id = 800002 AND ME.EMP_group ='D' AND MGA.data_Entry is null and MGA.advance_type<>'CS' ");
					    lSBQuery1.append(" and MGA.TRANSACTION_ID not in ");
					    lSBQuery1.append(" (SELECT MGA.TRANSACTION_ID ");
					    lSBQuery1.append(" FROM IFMS.MST_GPF_ADVANCE MGA,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME,mst_gpf_bill_dtls bill ");
					    lSBQuery1.append(" WHERE MGA.transaction_id=bill.transaction_id and MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.SEVAARTH_ID = MEG.SEVAARTH_ID AND MGA.status_Flag='A' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
					    lSBQuery1.append(" AND WJ.job_Ref_Id = MGA.mst_Gpf_Advance_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
					    lSBQuery1.append(" AND WJ.doc_Id = 800002 AND ME.EMP_group ='D' AND MGA.data_Entry is null)) ");
					    lSBQuery1.append(" union ");
					    lSBQuery1.append(" (SELECT TGF.TRANSACTION_ID FROM Trn_Gpf_Final_Withdrawal TGF,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME  ");
					   lSBQuery1.append("  WHERE TGF.gpf_Acc_No = MEG.gpf_Acc_No AND TGF.SEVAARTH_ID = MEG.SEVAARTH_ID AND TGF.req_Status='A' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N'  ");
					   lSBQuery1.append(" AND WJ.job_Ref_Id = TGF.trn_Gpf_Final_Withdrawal_Id AND WJ.lst_Act_Post_Id ='"+username+"'  ");
					   lSBQuery1.append(" AND WJ.doc_Id =800003 AND ME.EMP_group ='D' ");
					   lSBQuery1.append(" and TGF.TRANSACTION_ID not in ");
					   lSBQuery1.append(" (SELECT TGF.TRANSACTION_ID FROM Trn_Gpf_Final_Withdrawal TGF,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ,mst_gpf_bill_dtls bill  ");
					   lSBQuery1.append(" WHERE TGF.transaction_id=bill.transaction_id  and TGF.gpf_Acc_No = MEG.gpf_Acc_No AND TGF.SEVAARTH_ID = MEG.SEVAARTH_ID AND TGF.req_Status='A' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N'  ");
					  lSBQuery1.append("  AND WJ.job_Ref_Id = TGF.trn_Gpf_Final_Withdrawal_Id AND WJ.lst_Act_Post_Id ='"+username+"'  ");
					 lSBQuery1.append(" AND WJ.doc_Id =800003 AND ME.EMP_group ='D'))) ");
						   Query lQuery1 = ghibSession.createSQLQuery(lSBQuery1.toString());
							res1=Integer.parseInt(lQuery1.uniqueResult().toString());
							
							
							StringBuilder lSBQuery8 = new StringBuilder();
							lSBQuery8.append(" select count(1) from( ");
							lSBQuery8.append(" SELECT TRANSACTION_ID FROM MST_LNA_HOUSE_ADVANCE where STATUS_FLAG='F' and TO_POST_ID='"+username+"' ");
							lSBQuery8.append(" union ");
							lSBQuery8.append(" SELECT TRANSACTION_ID FROM MST_LNA_COMP_ADVANCE where STATUS_FLAG='F' and TO_POST_ID='"+username+"' ");
							lSBQuery8.append(" union ");
							lSBQuery8.append(" SELECT TRANSACTION_ID FROM MST_LNA_MOTOR_ADVANCE where STATUS_FLAG='F' and TO_POST_ID='"+username+"') ");

							Query lQuery8 = ghibSession.createSQLQuery(lSBQuery8.toString());
							res8=Integer.parseInt(lQuery8.uniqueResult().toString());
						
							
							StringBuilder lSBQuery9 = new StringBuilder();


							lSBQuery9.append("  select count(1) from( ");
							lSBQuery9.append(" SELECT HOUSE_ADVANCE_ID FROM MST_LNA_HOUSE_ADVANCE where STATUS_FLAG like 'A%' and TO_POST_ID='"+username+"' and order_no is null  ");
							//lSBQuery9.append(" and order_no not in (SELECT order_no FROM MST_LNA_BILL_DTLS) ");
							lSBQuery9.append(" union ");
							lSBQuery9.append(" SELECT COMPUTER_ADVANCE_ID FROM MST_LNA_COMP_ADVANCE where STATUS_FLAG='A' and TO_POST_ID='"+username+"' and order_no is null  ");
							//lSBQuery9.append(" and order_no not in (SELECT order_no FROM MST_LNA_BILL_DTLS) ");
							lSBQuery9.append(" union ");
							lSBQuery9.append(" SELECT MOTOR_ADVANCE_ID FROM MST_LNA_MOTOR_ADVANCE where STATUS_FLAG='A' and TO_POST_ID='"+username+"' and order_no is null)  ");
							//lSBQuery9.append(" and order_no not in (SELECT order_no FROM MST_LNA_BILL_DTLS)) ");
							   Query lQuery9 = ghibSession.createSQLQuery(lSBQuery9.toString());
								res9=Integer.parseInt(lQuery9.uniqueResult().toString());
						
						
						res7=res1+res2+res3+res4+res5+res6+res8+res9;
						logger.info("check1234567");
						logger.info("res1="+res1);
						logger.info("res2="+res2);
						logger.info("res3="+res3);
						logger.info("res4="+res4);
						logger.info("res5="+res5);
						logger.info("res6="+res6);
						logger.info("res7="+res7);
						logger.info("res8="+res8);
						logger.info("res9="+res9);
					}
					catch (Exception e) {
						logger.error("CheckPendingRequestException");
					}
					return res7;
					
				}
				
				public List chkPendingReqHoAdvance(String username) {
					

					logger.error("getActiveHOLst");
					logger.error("lLngPostId"+username);
					
					
					 List lLstpendingReq = null;
					 Session ghibSession = getSession();
					 try{
					StringBuilder lSBQuery = new StringBuilder();
					
					    lSBQuery.append(" select MGA.MST_GPF_ADVANCE_ID,MGA.ADVANCE_TYPE ");
						lSBQuery.append("  FROM IFMS.MST_GPF_ADVANCE MGA,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
						lSBQuery.append("  WHERE MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.SEVAARTH_ID = MEG.SEVAARTH_ID AND MGA.status_Flag in ('F2','F4') AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
						lSBQuery.append("  AND WJ.job_Ref_Id = MGA.mst_Gpf_Advance_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
						lSBQuery.append("  AND WJ.doc_Id = 800002 AND ME.EMP_group ='D' AND MGA.data_Entry is null ");
						/*lSBQuery.append(" union all ");
						lSBQuery.append("  select MGA.MST_GPF_ADVANCE_ID,MGA.ADVANCE_TYPE ");
						 lSBQuery.append(" FROM IFMS.MST_GPF_ADVANCE MGA ");
						 lSBQuery.append(" inner join Mst_Emp_Gpf_Acc MG on MGA.GPF_ACC_NO = MG.GPF_ACC_NO and MGA.sevaarth_id = MG.sevaarth_id ");
						 lSBQuery.append(" inner join  Mst_DCPS_Emp MGE on MG.MST_GPF_EMP_ID = MGE.DCPS_EMP_ID and MGE.DCPS_OR_GPF ='N' and  MGE.EMP_group ='D' ");
						 lSBQuery.append(" inner join Org_Ddo_Mst ODM on MG.ddo_Code=ODM.ddo_Code ");
						 lSBQuery.append(" left outer join MST_GPF_BILL_DTLS blt on blt.TRANSACTION_ID=MGA.TRANSACTION_ID ");
						 lSBQuery.append(" WHERE MGA.status_Flag IN ('A','AC') ");
						 lSBQuery.append("  AND (MGA.FRWDRD_TO_RHO_POST_ID = (SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"') or  ODM.LOCATION_CODE =(SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"')) ");
						 lSBQuery.append(" AND MGA.data_Entry is null  ");
					*/
					
					
					Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
					//lQuery.setParameter("lLngPostId", lLngPostId);
					lLstpendingReq = lQuery.list();
					//lQuery.setParameter("locCode", locCode);
						
					logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
					logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstpendingReq.size());
					
					if(lLstpendingReq != null && lLstpendingReq.size()> 0)
					{
						lLstpendingReq = lQuery.list();
					}
					
					 }catch(Exception e){
						 logger.error(" Error is : " + e, e);			 
					 }
					 return lLstpendingReq;
				}
				
				
	  public List chkPendingReqHoFinal(String username) {
					
					 List lLstpendingReq = null;
					 Session ghibSession = getSession();
					 try{
					StringBuilder lSBQuery = new StringBuilder();
					
					 lSBQuery.append(" select TGF.TRN_GPF_FINAL_WITHDRAWAL_ID,'FW' as Advance_type ");
					 lSBQuery.append(" FROM Trn_Gpf_Final_Withdrawal TGF,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME  "); 
					 lSBQuery.append("  WHERE TGF.gpf_Acc_No = MEG.gpf_Acc_No AND TGF.SEVAARTH_ID = MEG.SEVAARTH_ID AND TGF.req_Status in ('F2','F4') AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N'  ");
					 lSBQuery.append(" AND WJ.job_Ref_Id = TGF.trn_Gpf_Final_Withdrawal_Id AND WJ.lst_Act_Post_Id ='"+username+"'  ");
					 lSBQuery.append(" AND WJ.doc_Id =800003 AND ME.EMP_group ='D'  ");
					/* lSBQuery.append(" UNION all  ");
					 lSBQuery.append(" select TGFW.TRN_GPF_FINAL_WITHDRAWAL_ID,'FW' as Advance_type  ");
					  lSBQuery.append(" FROM TRN_GPF_FINAL_WITHDRAWAL TGFW  ");
					  lSBQuery.append(" inner join Mst_Emp_Gpf_Acc MG on TGFW.GPF_ACC_NO = MG.GPF_ACC_NO and TGFW.sevaarth_id = MG.sevaarth_id  ");
					  lSBQuery.append(" inner join  Mst_DCPS_Emp MGE on MG.MST_GPF_EMP_ID = MGE.DCPS_EMP_ID and MGE.DCPS_OR_GPF ='N' and  MGE.EMP_group ='D'   ");
					  lSBQuery.append(" inner join Org_Ddo_Mst ODM on MG.ddo_Code=ODM.ddo_Code  ");
					  lSBQuery.append("left outer join MST_GPF_BILL_DTLS blt on blt.TRANSACTION_ID=TGFW.TRANSACTION_ID  "); 
					  lSBQuery.append(" WHERE TGFW.REQ_STATUS = 'A'  ");
					  lSBQuery.append(" AND (TGFW.FRWDRD_TO_RHO_POST_ID = (SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"') or  ODM.LOCATION_CODE =(SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"'))  ");
					*/
				
					
					Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
					//lQuery.setParameter("lLngPostId", lLngPostId);
					lLstpendingReq = lQuery.list();
					//lQuery.setParameter("locCode", locCode);
						
					logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
					logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstpendingReq.size());
					
					if(lLstpendingReq != null && lLstpendingReq.size()> 0)
					{
						lLstpendingReq = lQuery.list();
					}
					
					 }catch(Exception e){
						 logger.error(" Error is : " + e, e);			 
					 }
					 return lLstpendingReq;
				}
	  
	  public int checkPendingRequestHOmdcOrderGen(String username)
		{
			logger.error("checkPendingRequest");
			logger.error("UserName"+username);
			
			int res4=0;
			int res5=0;
			int res7=0;
			int res6=0;
			List checkList1 = null;
			List checkList2 = null;
			List checkList3 = null;
			Session ghibSession = getSession();
			username=username.trim();
			try
			{
				
			
				
				// The following 2 queries Check if there is any order generation is pending
				StringBuilder lSBQuery4 = new StringBuilder();
				lSBQuery4.append("select count(1) ");
				lSBQuery4.append(" FROM IFMS.MST_GPF_ADVANCE MGA ");
				lSBQuery4.append(" inner join Mst_Emp_Gpf_Acc MG on MGA.GPF_ACC_NO = MG.GPF_ACC_NO and MGA.sevaarth_id = MG.sevaarth_id");
				lSBQuery4.append(" inner join  Mst_DCPS_Emp MGE on MG.MST_GPF_EMP_ID = MGE.DCPS_EMP_ID and MGE.DCPS_OR_GPF ='N' and  MGE.EMP_group ='D'");
				lSBQuery4.append(" inner join Org_Ddo_Mst ODM on MG.ddo_Code=ODM.ddo_Code ");
				lSBQuery4.append(" left outer join MST_GPF_BILL_DTLS blt on blt.TRANSACTION_ID=MGA.TRANSACTION_ID ");
				lSBQuery4.append(" WHERE MGA.status_Flag IN ('A','AC')  and blt.STATUS_FLAG in (0,2) ");
				lSBQuery4.append(" AND (MGA.FRWDRD_TO_RHO_POST_ID = (SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"') or  ODM.LOCATION_CODE =(SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"')) ");
				lSBQuery4.append(" AND MGA.data_Entry is null  ");
				Query lQuery4 = ghibSession.createSQLQuery(lSBQuery4.toString());
				res4=Integer.parseInt(lQuery4.uniqueResult().toString());
				
				
				StringBuilder lSBQuery5 = new StringBuilder();
				lSBQuery5.append("select count(1) ");
				lSBQuery5.append("  FROM TRN_GPF_FINAL_WITHDRAWAL TGFW ");
				lSBQuery5.append("  inner join Mst_Emp_Gpf_Acc MG on TGFW.GPF_ACC_NO = MG.GPF_ACC_NO and TGFW.sevaarth_id = MG.sevaarth_id ");
				lSBQuery5.append(" inner join  Mst_DCPS_Emp MGE on MG.MST_GPF_EMP_ID = MGE.DCPS_EMP_ID and MGE.DCPS_OR_GPF ='N' and  MGE.EMP_group ='D'  ");
				lSBQuery5.append(" inner join Org_Ddo_Mst ODM on MG.ddo_Code=ODM.ddo_Code ");
				lSBQuery5.append(" left outer join MST_GPF_BILL_DTLS blt on blt.TRANSACTION_ID=TGFW.TRANSACTION_ID  ");
				lSBQuery5.append(" WHERE TGFW.REQ_STATUS = 'A'  and blt.STATUS_FLAG in (0,2) ");
				lSBQuery5.append(" AND (TGFW.FRWDRD_TO_RHO_POST_ID = (SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"') or  ODM.LOCATION_CODE =(SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"')) ");
				Query lQuery5 = ghibSession.createSQLQuery(lSBQuery5.toString());
				res5=Integer.parseInt(lQuery5.uniqueResult().toString());
				
				StringBuilder lSBQuery6 = new StringBuilder();
				lSBQuery6.append(" select count(1) from(  ");
				 lSBQuery6.append(" SELECT HOUSE_ADVANCE_ID FROM MST_LNA_HOUSE_ADVANCE where STATUS_FLAG like 'A%' and TO_POST_ID='"+username+"'  ");
				lSBQuery6.append(" and order_no  in (SELECT order_no FROM MST_LNA_BILL_DTLS where STATUS_FLAG in (0,1))  ");
				lSBQuery6.append(" union  ");
				lSBQuery6.append(" SELECT COMPUTER_ADVANCE_ID FROM MST_LNA_COMP_ADVANCE where STATUS_FLAG='A' and TO_POST_ID='"+username+"'  ");
				lSBQuery6.append(" and order_no in (SELECT order_no FROM MST_LNA_BILL_DTLS where STATUS_FLAG in (0,1))  ");
				lSBQuery6.append(" union  ");
				lSBQuery6.append(" SELECT MOTOR_ADVANCE_ID FROM MST_LNA_MOTOR_ADVANCE where STATUS_FLAG='A' and TO_POST_ID='"+username+"'  ");
				lSBQuery6.append(" and order_no  in (SELECT order_no FROM MST_LNA_BILL_DTLS where STATUS_FLAG in (0,1)))  ");
				Query lQuery6 = ghibSession.createSQLQuery(lSBQuery6.toString());
				res6=Integer.parseInt(lQuery6.uniqueResult().toString());
				
	            res7=res4+res5+res6;
				logger.info("check1234567");
				
				
				logger.info("res4="+res4);
				logger.info("res5="+res5);
				logger.info("res6="+res6);
				logger.info("res7="+res7);
			}
			catch (Exception e) {
				logger.error("CheckPendingRequestException");
			}
			return res7;
			
		}
	  
	  public List chkPendingReqHoOrderGen(String username) {
			

			logger.error("getActiveHOLst");
			logger.error("lLngPostId"+username);
			
			
			 List lLstpendingReq = null;
			 Session ghibSession = getSession();
			 try{
			StringBuilder lSBQuery = new StringBuilder();
			
			   
				lSBQuery.append("  select MGA.TRANSACTION_ID ");
				 lSBQuery.append(" FROM IFMS.MST_GPF_ADVANCE MGA ");
				 lSBQuery.append(" inner join Mst_Emp_Gpf_Acc MG on MGA.GPF_ACC_NO = MG.GPF_ACC_NO and MGA.sevaarth_id = MG.sevaarth_id ");
				 lSBQuery.append(" inner join  Mst_DCPS_Emp MGE on MG.MST_GPF_EMP_ID = MGE.DCPS_EMP_ID and MGE.DCPS_OR_GPF ='N' and  MGE.EMP_group ='D' ");
				 lSBQuery.append(" inner join Org_Ddo_Mst ODM on MG.ddo_Code=ODM.ddo_Code ");
				 lSBQuery.append(" left outer join MST_GPF_BILL_DTLS blt on blt.TRANSACTION_ID=MGA.TRANSACTION_ID ");
				 lSBQuery.append(" WHERE MGA.status_Flag IN ('A','AC') and blt.STATUS_FLAG in (0,2) ");
				 lSBQuery.append("  AND (MGA.FRWDRD_TO_RHO_POST_ID = (SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"') or  ODM.LOCATION_CODE =(SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"')) ");
				 lSBQuery.append(" AND MGA.data_Entry is null  ");
				 lSBQuery.append(" union  ");
				 lSBQuery.append(" select TGFW.TRANSACTION_ID  ");
				  lSBQuery.append(" FROM TRN_GPF_FINAL_WITHDRAWAL TGFW  ");
				  lSBQuery.append(" inner join Mst_Emp_Gpf_Acc MG on TGFW.GPF_ACC_NO = MG.GPF_ACC_NO and TGFW.sevaarth_id = MG.sevaarth_id  ");
				  lSBQuery.append(" inner join  Mst_DCPS_Emp MGE on MG.MST_GPF_EMP_ID = MGE.DCPS_EMP_ID and MGE.DCPS_OR_GPF ='N' and  MGE.EMP_group ='D'   ");
				  lSBQuery.append(" inner join Org_Ddo_Mst ODM on MG.ddo_Code=ODM.ddo_Code  ");
				  lSBQuery.append("left outer join MST_GPF_BILL_DTLS blt on blt.TRANSACTION_ID=TGFW.TRANSACTION_ID  "); 
				  lSBQuery.append(" WHERE TGFW.REQ_STATUS = 'A'  and blt.STATUS_FLAG in (0,2) ");
				  lSBQuery.append(" AND (TGFW.FRWDRD_TO_RHO_POST_ID = (SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"') or  ODM.LOCATION_CODE =(SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"'))  ");
				  lSBQuery.append(" union  ");
				  lSBQuery.append(" (select TRANSACTION_ID from(  ");
					 lSBQuery.append(" SELECT TRANSACTION_ID FROM MST_LNA_HOUSE_ADVANCE where STATUS_FLAG like 'A%' and TO_POST_ID='"+username+"'  ");
					 lSBQuery.append(" and order_no  in (SELECT order_no FROM MST_LNA_BILL_DTLS where STATUS_FLAG in (0,1))  ");
					 lSBQuery.append(" union  ");
					 lSBQuery.append(" SELECT TRANSACTION_ID FROM MST_LNA_COMP_ADVANCE where STATUS_FLAG='A' and TO_POST_ID='"+username+"'  ");
					 lSBQuery.append(" and order_no in (SELECT order_no FROM MST_LNA_BILL_DTLS where STATUS_FLAG in (0,1))  ");
					 lSBQuery.append(" union  ");
					 lSBQuery.append(" SELECT TRANSACTION_ID FROM MST_LNA_MOTOR_ADVANCE where STATUS_FLAG='A' and TO_POST_ID='"+username+"'  ");
					 lSBQuery.append(" and order_no  in (SELECT order_no FROM MST_LNA_BILL_DTLS where STATUS_FLAG in (0,1))))  ");
			
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			//lQuery.setParameter("lLngPostId", lLngPostId);
			lLstpendingReq = lQuery.list();
			//lQuery.setParameter("locCode", locCode);
				
			logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
			logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstpendingReq.size());
			
			if(lLstpendingReq != null && lLstpendingReq.size()> 0)
			{
				lLstpendingReq = lQuery.list();
			}
			
			 }catch(Exception e){
				 logger.error(" Error is : " + e, e);			 
			 }
			 return lLstpendingReq;
		}
	  
/*	  
	  public List chkPendingReqHoAdvanceOrderGen(String username) {
			

			logger.error("getActiveHOLst");
			logger.error("lLngPostId"+username);
			
			
			 List lLstpendingReq = null;
			 Session ghibSession = getSession();
			 try{
			StringBuilder lSBQuery = new StringBuilder();
			
			   
				lSBQuery.append("  select MGA.MST_GPF_ADVANCE_ID,MGA.ADVANCE_TYPE ");
				 lSBQuery.append(" FROM IFMS.MST_GPF_ADVANCE MGA ");
				 lSBQuery.append(" inner join Mst_Emp_Gpf_Acc MG on MGA.GPF_ACC_NO = MG.GPF_ACC_NO and MGA.sevaarth_id = MG.sevaarth_id ");
				 lSBQuery.append(" inner join  Mst_DCPS_Emp MGE on MG.MST_GPF_EMP_ID = MGE.DCPS_EMP_ID and MGE.DCPS_OR_GPF ='N' and  MGE.EMP_group ='D' ");
				 lSBQuery.append(" inner join Org_Ddo_Mst ODM on MG.ddo_Code=ODM.ddo_Code ");
				 lSBQuery.append(" left outer join MST_GPF_BILL_DTLS blt on blt.TRANSACTION_ID=MGA.TRANSACTION_ID ");
				 lSBQuery.append(" WHERE MGA.status_Flag IN ('A','AC') and blt.STATUS_FLAG in (0,2) ");
				 lSBQuery.append("  AND (MGA.FRWDRD_TO_RHO_POST_ID = (SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"') or  ODM.LOCATION_CODE =(SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"')) ");
				 lSBQuery.append(" AND MGA.data_Entry is null  ");
			
			
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			//lQuery.setParameter("lLngPostId", lLngPostId);
			lLstpendingReq = lQuery.list();
			//lQuery.setParameter("locCode", locCode);
				
			logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
			logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstpendingReq.size());
			
			if(lLstpendingReq != null && lLstpendingReq.size()> 0)
			{
				lLstpendingReq = lQuery.list();
			}
			
			 }catch(Exception e){
				 logger.error(" Error is : " + e, e);			 
			 }
			 return lLstpendingReq;
		}*/
		
		
/*public List chkPendingReqHoFinalOrderGen(String username) {
			
			 List lLstpendingReq = null;
			 Session ghibSession = getSession();
			 try{
			StringBuilder lSBQuery = new StringBuilder();
			
			
			 lSBQuery.append(" select TGFW.TRN_GPF_FINAL_WITHDRAWAL_ID,'FW' as Advance_type  ");
			  lSBQuery.append(" FROM TRN_GPF_FINAL_WITHDRAWAL TGFW  ");
			  lSBQuery.append(" inner join Mst_Emp_Gpf_Acc MG on TGFW.GPF_ACC_NO = MG.GPF_ACC_NO and TGFW.sevaarth_id = MG.sevaarth_id  ");
			  lSBQuery.append(" inner join  Mst_DCPS_Emp MGE on MG.MST_GPF_EMP_ID = MGE.DCPS_EMP_ID and MGE.DCPS_OR_GPF ='N' and  MGE.EMP_group ='D'   ");
			  lSBQuery.append(" inner join Org_Ddo_Mst ODM on MG.ddo_Code=ODM.ddo_Code  ");
			  lSBQuery.append("left outer join MST_GPF_BILL_DTLS blt on blt.TRANSACTION_ID=TGFW.TRANSACTION_ID  "); 
			  lSBQuery.append(" WHERE TGFW.REQ_STATUS = 'A'  and blt.STATUS_FLAG in (0,2) ");
			  lSBQuery.append(" AND (TGFW.FRWDRD_TO_RHO_POST_ID = (SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"') or  ODM.LOCATION_CODE =(SELECT loc_id FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"'))  ");
			
		
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			//lQuery.setParameter("lLngPostId", lLngPostId);
			lLstpendingReq = lQuery.list();
			//lQuery.setParameter("locCode", locCode);
				
			logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
			logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstpendingReq.size());
			
			if(lLstpendingReq != null && lLstpendingReq.size()> 0)
			{
				lLstpendingReq = lQuery.list();
			}
			
			 }catch(Exception e){
				 logger.error(" Error is : " + e, e);			 
			 }
			 return lLstpendingReq;
		}*/
	  
	  public List chkPendingReqHoInitialDataEntry(String username) {
			
			 List lLstpendingReq = null;
			 Session ghibSession = getSession();
			 try{
			StringBuilder lSBQuery = new StringBuilder();
			
			 lSBQuery.append(" select TEG.TRN_EMP_GPF_ACC_ID,TEG.SEVAaRTH_ID,teg.STATUS_FLAG  "); 
				lSBQuery.append("  FROM trn_emp_gpf_acc TEG, Mst_Emp_Gpf_Acc MEG, Org_Ddo_Mst ODM, Mst_Gpf_Yearly GPF  WHERE TEG.status_Flag = 'F'  "); 
				lSBQuery.append("  AND TEG.sevaarth_Id = MEG.sevaarth_Id AND MEG.ddo_Code = ODM.ddo_Code  AND GPF.gpf_Acc_No=MEG.gpf_Acc_No AND GPF.sevaarth_id=MEG.sevaarth_id AND ODM.location_Code = (SELECT loc_id  "); 
				 lSBQuery.append(" FROM ORG_POST_DETAILS_RLT where POST_ID='"+username+"')  "); 
			
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			//lQuery.setParameter("lLngPostId", lLngPostId);
			lLstpendingReq = lQuery.list();
			//lQuery.setParameter("locCode", locCode);
				
			logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
			logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstpendingReq.size());
			
			if(lLstpendingReq != null && lLstpendingReq.size()> 0)
			{
				lLstpendingReq = lQuery.list();
			}
			
			 }catch(Exception e){
				 logger.error(" Error is : " + e, e);			 
			 }
			 return lLstpendingReq;
		}
	  
	  public List chkPendingReqHoApprovedAdv(String username) {
			
			 List lLstpendingReq = null;
			 Session ghibSession = getSession();
			 try{
			StringBuilder lSBQuery = new StringBuilder();
			
		    lSBQuery.append(" select MGA.MST_GPF_ADVANCE_ID,MGA.ADVANCE_TYPE ");
		    lSBQuery.append(" FROM IFMS.MST_GPF_ADVANCE MGA,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
		    lSBQuery.append(" WHERE  MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.SEVAARTH_ID = MEG.SEVAARTH_ID AND MGA.status_Flag='A' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
		    lSBQuery.append(" AND WJ.job_Ref_Id = MGA.mst_Gpf_Advance_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
		    lSBQuery.append(" AND WJ.doc_Id = 800002 AND ME.EMP_group ='D' AND MGA.data_Entry is null and MGA.advance_type<>'CS' ");
		    lSBQuery.append(" and MGA.TRANSACTION_ID not in ");
		    lSBQuery.append(" (SELECT MGA.TRANSACTION_ID ");
		    lSBQuery.append(" FROM IFMS.MST_GPF_ADVANCE MGA,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME,mst_gpf_bill_dtls bill ");
		    lSBQuery.append(" WHERE MGA.transaction_id=bill.transaction_id and MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.SEVAARTH_ID = MEG.SEVAARTH_ID AND MGA.status_Flag='A' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
		    lSBQuery.append(" AND WJ.job_Ref_Id = MGA.mst_Gpf_Advance_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
		    lSBQuery.append(" AND WJ.doc_Id = 800002 AND ME.EMP_group ='D' AND MGA.data_Entry is null) ");
		   
			
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			//lQuery.setParameter("lLngPostId", lLngPostId);
			lLstpendingReq = lQuery.list();
			//lQuery.setParameter("locCode", locCode);
				
			logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
			logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstpendingReq.size());
			
			if(lLstpendingReq != null && lLstpendingReq.size()> 0)
			{
				lLstpendingReq = lQuery.list();
			}
			
			 }catch(Exception e){
				 logger.error(" Error is : " + e, e);			 
			 }
			 return lLstpendingReq;
		}
	  
	  public List chkPendingReqHoApprovedFw(String username) {
			
			 List lLstpendingReq = null;
			 Session ghibSession = getSession();
			 try{
			StringBuilder lSBQuery = new StringBuilder();
			
		  
		    lSBQuery.append(" select TGF.TRN_GPF_FINAL_WITHDRAWAL_ID,'FW' as Advance_type FROM Trn_Gpf_Final_Withdrawal TGF,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME  ");
		   lSBQuery.append("  WHERE TGF.gpf_Acc_No = MEG.gpf_Acc_No AND TGF.SEVAARTH_ID = MEG.SEVAARTH_ID AND TGF.req_Status='A' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N'  ");
		   lSBQuery.append(" AND WJ.job_Ref_Id = TGF.trn_Gpf_Final_Withdrawal_Id AND WJ.lst_Act_Post_Id ='"+username+"'  ");
		   lSBQuery.append(" AND WJ.doc_Id =800003 AND ME.EMP_group ='D' ");
		   lSBQuery.append(" and TGF.TRANSACTION_ID not in ");
		   lSBQuery.append(" (SELECT TGF.TRANSACTION_ID FROM Trn_Gpf_Final_Withdrawal TGF,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ,mst_gpf_bill_dtls bill  ");
		   lSBQuery.append(" WHERE TGF.transaction_id=bill.transaction_id  and TGF.gpf_Acc_No = MEG.gpf_Acc_No AND TGF.SEVAARTH_ID = MEG.SEVAARTH_ID AND TGF.req_Status='A' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N'  ");
		  lSBQuery.append("  AND WJ.job_Ref_Id = TGF.trn_Gpf_Final_Withdrawal_Id AND WJ.lst_Act_Post_Id ='"+username+"'  ");
		 lSBQuery.append(" AND WJ.doc_Id =800003 AND ME.EMP_group ='D') ");
			
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			//lQuery.setParameter("lLngPostId", lLngPostId);
			lLstpendingReq = lQuery.list();
			//lQuery.setParameter("locCode", locCode);
				
			logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
			logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstpendingReq.size());
			
			if(lLstpendingReq != null && lLstpendingReq.size()> 0)
			{
				lLstpendingReq = lQuery.list();
			}
			
			 }catch(Exception e){
				 logger.error(" Error is : " + e, e);			 
			 }
			 return lLstpendingReq;
		}
					
	  public String updatePendingReqHoInitial(String MstGpfAdvId)throws Exception
		{
			String lStrPendingId = "";
			logger.error("updatePendingReqDeoAdvance called"+MstGpfAdvId);
			try{
				StringBuilder lSBQuery = new StringBuilder();
				
				lSBQuery.append(" UPDATE TRN_EMP_GPF_ACC SET STATUS_FLAG='D' WHERE TRN_EMP_GPF_ACC_ID='"+MstGpfAdvId+"' ");
              Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	
				lQuery.executeUpdate();
				lStrPendingId = String.valueOf(lQuery.executeUpdate());
				
			}catch(Exception e){
				logger.error(" Error is : " + e, e);
				throw e;
			}
			
			return lStrPendingId;
		}	
				
				public String checkDeoRoleMapped(String Locid){
					
					logger.error("getLocid"+Locid);

					
					List lLstPostIdList = null;
					String lStrPostId ="";
					
					try{
						StringBuilder lSBQuery = new StringBuilder();
						
						/*lSBQuery.append(" SELECT post.post_id FROM org_user_mst user, org_userpost_rlt post ");
						lSBQuery.append(" where user.user_id=post.user_id ");
						lSBQuery.append(" and user.user_name=:lStrUserName ");
						
						*/
						lSBQuery.append(" SELECT role.post_role_id FROM ACL_POSTROLE_RLT role inner join ORG_POST_DETAILS_RLT post "); 
						lSBQuery.append(" on post.POST_ID = role.POST_ID   inner join ORG_USERPOST_RLT usr on usr.post_id = post.POST_ID ");
						lSBQuery.append(" and usr.ACTIVATE_FLAG = 1  inner join org_emp_mst emp on emp.user_id = usr.user_id "); 
						lSBQuery.append(" inner join mst_dcps_emp dcps on dcps.ORG_EMP_MST_ID = emp.EMP_ID"); 
						lSBQuery.append(" where post.LOC_ID = '"+Locid+"' and role.ROLE_ID = 800005 and role.ACTIVATE_FLAG =1 "); 

						
						Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
						//lQuery.setParameter("lStrUserName", lStrUserName);
						
						lLstPostIdList = lQuery.list();
						
						logger.error("lSBQuery***getPostRoleID*******"+lSBQuery.toString());
						
						
						if(lLstPostIdList != null && lLstPostIdList.size() > 0){
							lStrPostId = lLstPostIdList.get(0).toString();
						}
						
						logger.error("lStrPostId"+lStrPostId);
					}
					
					catch(Exception e){
						logger.error(" Error in getPostId : " + e, e);
						
					}
					return lStrPostId;
					
					
				}
				
				public void updateWfHierachyPostMpgFlag(Long lLngPostId) {

					StringBuilder lSBQuery = new StringBuilder();
					//lSBQuery.append(" update org_user_mst set ACTIVATE_FLAG = 1,PWDCHANGED_DATE = null where USER_ID = (select USER_ID from org_emp_mst where EMP_ID = " + lLongOrgEmpMstId + ")");
					//lSBQuery.append(" update org_user_mst set PASSWORD = '0b76f0f411f6944f9d192da0fcbfb292' where USER_ID = '"+ lLnguserId  +"' ");
					lSBQuery.append(" update WF_HIERACHY_POST_MPG set ACTIVATE_FLAG=1 where post_id='"+ lLngPostId  +"' and LEVEL_ID in (10,20,30) ");
					Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
					lQuery.executeUpdate();
				}
				

     
    	 public List chkPendingReqInRHO(String username) {
			
		 List lLstpendingReq = null;
		 Session ghibSession = getSession();
		 try{
		StringBuilder lSBQuery = new StringBuilder();
		

           lSBQuery.append(" select mga.SEVAARTH_ID ");
           lSBQuery.append(" FROM IFMS.MST_GPF_ADVANCE MGA,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
           lSBQuery.append(" WHERE MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.SEVAARTH_ID = MEG.SEVAARTH_ID AND MGA.status_Flag='F4' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
           lSBQuery.append(" AND WJ.job_Ref_Id = MGA.mst_Gpf_Advance_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
           lSBQuery.append(" AND WJ.doc_Id = 800002 AND ME.EMP_group ='D' AND MGA.data_Entry is null ");
           lSBQuery.append(" union ");
           lSBQuery.append(" select TGF.SEVAARTH_ID ");
           lSBQuery.append(" FROM Trn_Gpf_Final_Withdrawal TGF,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
           lSBQuery.append(" WHERE TGF.gpf_Acc_No = MEG.gpf_Acc_No AND TGF.SEVAARTH_ID = MEG.SEVAARTH_ID AND TGF.req_Status='F4' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
           lSBQuery.append(" AND WJ.job_Ref_Id = TGF.trn_Gpf_Final_Withdrawal_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
           lSBQuery.append(" AND WJ.doc_Id =800003 AND ME.EMP_group ='D' ");
          
		
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		//lQuery.setParameter("lLngPostId", lLngPostId);
		lLstpendingReq = lQuery.list();
		//lQuery.setParameter("locCode", locCode);
			
		logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
		logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstpendingReq.size());
		
		if(lLstpendingReq != null && lLstpendingReq.size()> 0)
		{
			lLstpendingReq = lQuery.list();
		}
		
		 }catch(Exception e){
			 logger.error(" Error is : " + e, e);			 
		 }
		 return lLstpendingReq;
	}
    	 
    	 public List chkPendingReqInRHOApproved(String username) {
 			
    		 List lLstpendingReq = null;
    		 Session ghibSession = getSession();
    		 try{
    		StringBuilder lSBQuery = new StringBuilder();
    		

    		   lSBQuery.append("  select distinct sevaarth_id from (   ");
    		   lSBQuery.append("  (SELECT MGA.SEVAARTH_ID  ");
    		   lSBQuery.append("   FROM IFMS.MST_GPF_ADVANCE MGA,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
    		   lSBQuery.append("   WHERE  MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.SEVAARTH_ID = MEG.SEVAARTH_ID AND MGA.status_Flag='A' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N'  ");
    		  lSBQuery.append("   AND WJ.job_Ref_Id = MGA.mst_Gpf_Advance_Id AND WJ.lst_Act_Post_Id ='"+username+"'  ");
    		  lSBQuery.append("   AND WJ.doc_Id = 800002 AND ME.EMP_group ='D' AND MGA.data_Entry is null and MGA.advance_type<>'CS' and MGA.FRWDRD_TO_RHO_POST_ID is not null ");
    		  lSBQuery.append("   and MGA.TRANSACTION_ID not in  ");
    		  lSBQuery.append("  (SELECT MGA.TRANSACTION_ID  ");
    		  lSBQuery.append("   FROM IFMS.MST_GPF_ADVANCE MGA,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME,mst_gpf_bill_dtls bill  ");
    		  lSBQuery.append("   WHERE MGA.transaction_id=bill.transaction_id and MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.SEVAARTH_ID = MEG.SEVAARTH_ID AND MGA.status_Flag='A' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N'  ");
    		  lSBQuery.append("  AND WJ.job_Ref_Id = MGA.mst_Gpf_Advance_Id AND WJ.lst_Act_Post_Id ='"+username+"'  ");
    		  lSBQuery.append("  AND WJ.doc_Id = 800002 AND ME.EMP_group ='D' AND MGA.data_Entry is null))  ");
    		  lSBQuery.append("   union  ");
    		  lSBQuery.append(" (SELECT TGF.sevaarth_id FROM Trn_Gpf_Final_Withdrawal TGF,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME  ");
    		  lSBQuery.append("  WHERE TGF.gpf_Acc_No = MEG.gpf_Acc_No AND TGF.SEVAARTH_ID = MEG.SEVAARTH_ID AND TGF.req_Status='A' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N'  ");
    		  lSBQuery.append("  AND WJ.job_Ref_Id = TGF.trn_Gpf_Final_Withdrawal_Id AND WJ.lst_Act_Post_Id ='"+username+"'  ");
    		  lSBQuery.append("  AND WJ.doc_Id =800003 AND ME.EMP_group ='D' and TGF.FRWDRD_TO_RHO_POST_ID is not null ");
    		  lSBQuery.append("  and TGF.TRANSACTION_ID not in  ");
    		  lSBQuery.append("  (SELECT TGF.TRANSACTION_ID FROM Trn_Gpf_Final_Withdrawal TGF,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ,mst_gpf_bill_dtls bill  "); 
    		  lSBQuery.append("  WHERE TGF.transaction_id=bill.transaction_id  and TGF.gpf_Acc_No = MEG.gpf_Acc_No AND TGF.SEVAARTH_ID = MEG.SEVAARTH_ID AND TGF.req_Status='A' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N'  "); 
    		  lSBQuery.append("  AND WJ.job_Ref_Id = TGF.trn_Gpf_Final_Withdrawal_Id AND WJ.lst_Act_Post_Id ='"+username+"'  ");
    		  lSBQuery.append("  AND WJ.doc_Id =800003 AND ME.EMP_group ='D')))  ");
    		
    		
    		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
    		//lQuery.setParameter("lLngPostId", lLngPostId);
    		lLstpendingReq = lQuery.list();
    		//lQuery.setParameter("locCode", locCode);
    			
    		logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
    		logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstpendingReq.size());
    		
    		if(lLstpendingReq != null && lLstpendingReq.size()> 0)
    		{
    			lLstpendingReq = lQuery.list();
    		}
    		
    		 }catch(Exception e){
    			 logger.error(" Error is : " + e, e);			 
    		 }
    		 return lLstpendingReq;
    	}
		
    	 public int checkPendingRequestRHOAsstMdc(String username)
         {
         	logger.error("checkPendingRequest");
         	logger.error("UserName"+username);
         	//int res1=0;
         	int res2=0;
         	int res3=0;
         	int res4=0;
         	int res8=0;
         	int res9=0;
         	List checkList1 = null;
         	List checkList2 = null;
         	List checkList3 = null;
         	Session ghibSession = getSession();
         	username=username.trim();
         	try
         	{
         		//The following 3 queries check if there is any loan request is pending
         		
         		
         		logger.info("Before 2nd query");
         		StringBuilder lSBQuery2 = new StringBuilder();
         		lSBQuery2.append("select count(1) ");
         		lSBQuery2.append(" FROM IFMS.MST_GPF_ADVANCE MGA,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
         		lSBQuery2.append(" WHERE MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.sevaarth_id = MEG.sevaarth_id AND MGA.status_Flag='F3' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
         		lSBQuery2.append(" AND WJ.job_Ref_Id = MGA.mst_Gpf_Advance_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
         		lSBQuery2.append(" AND WJ.doc_Id = 800002 AND ME.EMP_group ='D' AND MGA.data_Entry is null");
         		Query lQuery2 = ghibSession.createSQLQuery(lSBQuery2.toString());
         		res2=Integer.parseInt(lQuery2.uniqueResult().toString());
         		logger.info("res2="+res2);
         		
         		logger.info("Before 3rd query");
         		StringBuilder lSBQuery3 = new StringBuilder();
         		lSBQuery3.append("select count(1) ");
         		lSBQuery3.append(" FROM Trn_Gpf_Final_Withdrawal TGF,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
         		lSBQuery3.append(" WHERE TGF.gpf_Acc_No = MEG.gpf_Acc_No and TGF.sevaarth_id = MEG.sevaarth_id and TGF.req_Status='F3' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
         		lSBQuery3.append(" AND WJ.job_Ref_Id = TGF.trn_Gpf_Final_Withdrawal_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
         		lSBQuery3.append(" AND WJ.doc_Id =800003 AND ME.EMP_group ='D'");
         		Query lQuery3 = ghibSession.createSQLQuery(lSBQuery3.toString());
         		res3=Integer.parseInt(lQuery3.uniqueResult().toString());
         		
         		StringBuilder lSBQuery8 = new StringBuilder();
				lSBQuery8.append(" select count(1) from( ");
				lSBQuery8.append(" SELECT TRANSACTION_ID FROM MST_LNA_HOUSE_ADVANCE where STATUS_FLAG='F' and TO_POST_ID='"+username+"' ");
				lSBQuery8.append(" union ");
				lSBQuery8.append(" SELECT TRANSACTION_ID FROM MST_LNA_COMP_ADVANCE where STATUS_FLAG='F' and TO_POST_ID='"+username+"' ");
				lSBQuery8.append(" union ");
				lSBQuery8.append(" SELECT TRANSACTION_ID FROM MST_LNA_MOTOR_ADVANCE where STATUS_FLAG='F' and TO_POST_ID='"+username+"') ");

				Query lQuery8 = ghibSession.createSQLQuery(lSBQuery8.toString());
				res8=Integer.parseInt(lQuery8.uniqueResult().toString());
			
				
				StringBuilder lSBQuery9 = new StringBuilder();


				lSBQuery9.append("  select count(1) from( ");
				lSBQuery9.append(" SELECT HOUSE_ADVANCE_ID FROM MST_LNA_HOUSE_ADVANCE where STATUS_FLAG like 'A%' and TO_POST_ID='"+username+"' ");
				lSBQuery9.append(" and order_no not in (SELECT order_no FROM MST_LNA_BILL_DTLS) ");
				lSBQuery9.append(" union ");
				lSBQuery9.append(" SELECT COMPUTER_ADVANCE_ID FROM MST_LNA_COMP_ADVANCE where STATUS_FLAG='A' and TO_POST_ID='"+username+"' ");
				lSBQuery9.append(" and order_no not in (SELECT order_no FROM MST_LNA_BILL_DTLS) ");
				lSBQuery9.append(" union ");
				lSBQuery9.append(" SELECT MOTOR_ADVANCE_ID FROM MST_LNA_MOTOR_ADVANCE where STATUS_FLAG='A' and TO_POST_ID='"+username+"' ");
				lSBQuery9.append(" and order_no not in (SELECT order_no FROM MST_LNA_BILL_DTLS)) ");
				   Query lQuery9 = ghibSession.createSQLQuery(lSBQuery9.toString());
					res9=Integer.parseInt(lQuery9.uniqueResult().toString());
					
         		res4=res2+res3+res8+res9;
         		logger.info("No of requests="+res4);
         	}
         	catch (Exception e) {
         		logger.error("CheckPendingRequestException");
         	}
         	return res4;
         	
         }
    	 
    	 public List chkPendingReqRHOAsstAdvance(String username) {
				

				logger.error("getActiveHOLst");
				logger.error("lLngPostId"+username);
				
				
				 List lLstpendingReq = null;
				 Session ghibSession = getSession();
				 try{
				StringBuilder lSBQuery = new StringBuilder();
				
				lSBQuery.append(" select MGA.MST_GPF_ADVANCE_ID,MGA.ADVANCE_TYPE ");
				 lSBQuery.append(" FROM IFMS.MST_GPF_ADVANCE MGA,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
				 lSBQuery.append(" WHERE MGA.gpf_Acc_No = MEG.gpf_Acc_No and MGA.sevaarth_id = MEG.sevaarth_id AND MGA.status_Flag='F3' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
				 lSBQuery.append(" AND WJ.job_Ref_Id = MGA.mst_Gpf_Advance_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
				 lSBQuery.append(" AND WJ.doc_Id = 800002 AND ME.EMP_group ='D' AND MGA.data_Entry is null ");
			
				
				Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				//lQuery.setParameter("lLngPostId", lLngPostId);
				lLstpendingReq = lQuery.list();
				//lQuery.setParameter("locCode", locCode);
					
				logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
				logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstpendingReq.size());
				
				if(lLstpendingReq != null && lLstpendingReq.size()> 0)
				{
					lLstpendingReq = lQuery.list();
				}
				
				 }catch(Exception e){
					 logger.error(" Error is : " + e, e);			 
				 }
				 return lLstpendingReq;
			}
    	 
    	 public List chkPendingReqRHOAsstFinal(String username) {
				
			 List lLstpendingReq = null;
			 Session ghibSession = getSession();
			 try{
			StringBuilder lSBQuery = new StringBuilder();
			
			 
				lSBQuery.append(" select TGF.TRN_GPF_FINAL_WITHDRAWAL_ID,'FW' as Advance_type ");
				lSBQuery.append("  FROM Trn_Gpf_Final_Withdrawal TGF,Wf_Job_Mst WJ, Mst_Emp_Gpf_Acc MEG, Mst_DCPS_Emp ME ");
				lSBQuery.append("  WHERE TGF.gpf_Acc_No = MEG.gpf_Acc_No and TGF.sevaarth_id = MEG.sevaarth_id and TGF.req_Status='F3' AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_Or_Gpf='N' ");
				lSBQuery.append("  AND WJ.job_Ref_Id = TGF.trn_Gpf_Final_Withdrawal_Id AND WJ.lst_Act_Post_Id ='"+username+"' ");
				lSBQuery.append("  AND WJ.doc_Id =800003 AND ME.EMP_group ='D' ");
			
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			//lQuery.setParameter("lLngPostId", lLngPostId);
			lLstpendingReq = lQuery.list();
			//lQuery.setParameter("locCode", locCode);
				
			logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
			logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstpendingReq.size());
			
			if(lLstpendingReq != null && lLstpendingReq.size()> 0)
			{
				lLstpendingReq = lQuery.list();
			}
			
			 }catch(Exception e){
				 logger.error(" Error is : " + e, e);			 
			 }
			 return lLstpendingReq;
		}
    	 
    	 /*public int checkPendingLNARequestHOmdc(String username)
			{
				logger.error("checkPendingRequest");
				logger.error("UserName"+username);
				int res1=0;
				int res2=0;
				int res7=0;
				List checkList1 = null;
				List checkList2 = null;
				List checkList3 = null;
				Session ghibSession = getSession();
				username=username.trim();
				try
				{
					
					logger.info("Before 2nd query");
					StringBuilder lSBQuery2 = new StringBuilder();
					lSBQuery2.append(" select count(1) from( ");
					lSBQuery2.append(" SELECT TRANSACTION_ID FROM MST_LNA_HOUSE_ADVANCE where STATUS_FLAG='F' and TO_POST_ID='"+username+"' ");
					lSBQuery2.append(" union ");
					lSBQuery2.append(" SELECT TRANSACTION_ID FROM MST_LNA_COMP_ADVANCE where STATUS_FLAG='F' and TO_POST_ID='"+username+"' ");
					lSBQuery2.append(" union ");
					lSBQuery2.append(" SELECT TRANSACTION_ID FROM MST_LNA_MOTOR_ADVANCE where STATUS_FLAG='F' and TO_POST_ID='"+username+"') ");

					Query lQuery2 = ghibSession.createSQLQuery(lSBQuery2.toString());
					res2=Integer.parseInt(lQuery2.uniqueResult().toString());
				
					
					StringBuilder lSBQuery1 = new StringBuilder();


					  lSBQuery1.append("  select count(1) from( ");
					 lSBQuery1.append(" SELECT HOUSE_ADVANCE_ID FROM MST_LNA_HOUSE_ADVANCE where STATUS_FLAG like 'A%' and TO_POST_ID='"+username+"' ");
					 lSBQuery1.append(" and order_no not in (SELECT order_no FROM MST_LNA_BILL_DTLS) ");
					 lSBQuery1.append(" union ");
					 lSBQuery1.append(" SELECT COMPUTER_ADVANCE_ID FROM MST_LNA_COMP_ADVANCE where STATUS_FLAG='A' and TO_POST_ID='"+username+"' ");
					 lSBQuery1.append(" and order_no not in (SELECT order_no FROM MST_LNA_BILL_DTLS) ");
					 lSBQuery1.append(" union ");
					 lSBQuery1.append(" SELECT MOTOR_ADVANCE_ID FROM MST_LNA_MOTOR_ADVANCE where STATUS_FLAG='A' and TO_POST_ID='"+username+"' ");
					 lSBQuery1.append(" and order_no not in (SELECT order_no FROM MST_LNA_BILL_DTLS)) ");
					   Query lQuery1 = ghibSession.createSQLQuery(lSBQuery1.toString());
						res1=Integer.parseInt(lQuery1.uniqueResult().toString());
					
					
					res7=res1+res2;
				
					logger.info("res1="+res1);
					logger.info("res2="+res2);
					
					logger.info("res7="+res7);
				}
				catch (Exception e) {
					logger.error("CheckPendingRequestException");
				}
				return res7;
				
			} */
    	 
    	 public List chkPendingReqLNAHo(String username) {
				

				logger.error("getActiveHOLst");
				logger.error("lLngPostId"+username);
				
				
				 List lLstpendingReq = null;
				 Session ghibSession = getSession();
				 try{
				StringBuilder lSBQuery = new StringBuilder();
				
		
					
					  lSBQuery.append("  SELECT HOUSE_ADVANCE_ID,'HBA' as ADVANCE_TYPE FROM MST_LNA_HOUSE_ADVANCE where STATUS_FLAG='F' and TO_POST_ID='"+username+"'  ");
					  lSBQuery.append("  union  ");
					  lSBQuery.append(" SELECT HOUSE_ADVANCE_ID,'HBA' as ADVANCE_TYPE FROM MST_LNA_HOUSE_ADVANCE where STATUS_FLAG like 'A%' and TO_POST_ID='"+username+"'  and order_no is null   ");
					 // lSBQuery.append(" and order_no not in (SELECT order_no FROM MST_LNA_BILL_DTLS))  ");
					  lSBQuery.append(" union  ");
					  lSBQuery.append(" SELECT COMPUTER_ADVANCE_ID,'CA' as ADVANCE_TYPE FROM MST_LNA_COMP_ADVANCE where STATUS_FLAG='F' and TO_POST_ID='"+username+"'  ");
					  lSBQuery.append(" union  ");
					  lSBQuery.append(" SELECT COMPUTER_ADVANCE_ID,'CA' as ADVANCE_TYPE FROM MST_LNA_COMP_ADVANCE where STATUS_FLAG='A' and TO_POST_ID='"+username+"'  and order_no is null   "); 
					  //lSBQuery.append(" and order_no not in (SELECT order_no FROM MST_LNA_BILL_DTLS))  ");
					  lSBQuery.append(" union  ");
					  lSBQuery.append(" SELECT MOTOR_ADVANCE_ID,'MA' as ADVANCE_TYPE FROM MST_LNA_MOTOR_ADVANCE where STATUS_FLAG='F' and TO_POST_ID='"+username+"'  ");
					  lSBQuery.append("  union  ");
					  lSBQuery.append(" SELECT MOTOR_ADVANCE_ID,'MA' as ADVANCE_TYPE  FROM MST_LNA_MOTOR_ADVANCE where STATUS_FLAG='A' and TO_POST_ID='"+username+"'  and order_no is null   ");
					  //lSBQuery.append(" and order_no not in (SELECT order_no FROM MST_LNA_BILL_DTLS))  ");
					
				Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				//lQuery.setParameter("lLngPostId", lLngPostId);
				lLstpendingReq = lQuery.list();
				//lQuery.setParameter("locCode", locCode);
					
				logger.error("lSBQuery*****getActiveRhoAsstLst*****"+lSBQuery.toString());
				logger.error("lLstActRolesize*****getActiveRhoAsstLst*****"+lLstpendingReq.size());
				
				if(lLstpendingReq != null && lLstpendingReq.size()> 0)
				{
					lLstpendingReq = lQuery.list();
				}
				
				 }catch(Exception e){
					 logger.error(" Error is : " + e, e);			 
				 }
				 return lLstpendingReq;
			} 
    	 public String updatePendingReqLNA(String advanceId,String advancetype)throws Exception
			{
				String lStrPendingId = "";
				logger.error("updatePendingReqDeoAdvance called"+advanceId);
				logger.error("updatePendingReqDeoAdvance called"+advancetype);
				try{
					StringBuilder lSBQuery = new StringBuilder();
					
					if(advancetype.equals("HBA"))
					{
					lSBQuery.append(" UPDATE MST_LNA_HOUSE_ADVANCE SET STATUS_FLAG='R',TO_POST_ID=cast(CREATED_USER_ID as bigint) WHERE HOUSE_ADVANCE_ID='"+advanceId+"' ");
					}
					if(advancetype.equals("CA"))
					{
					lSBQuery.append(" UPDATE MST_LNA_COMP_ADVANCE SET STATUS_FLAG='R',TO_POST_ID=cast(CREATED_USER_ID as bigint) WHERE COMPUTER_ADVANCE_ID='"+advanceId+"' ");
					}
					if(advancetype.equals("MA"))
					{
					lSBQuery.append(" UPDATE MST_LNA_MOTOR_ADVANCE SET STATUS_FLAG='R',TO_POST_ID=cast(CREATED_USER_ID as bigint) WHERE MOTOR_ADVANCE_ID='"+advanceId+"' ");
					}
					
					
					
                 Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		
					lQuery.executeUpdate();
					lStrPendingId = String.valueOf(lQuery.executeUpdate());
					
				}catch(Exception e){
					logger.error(" Error is : " + e, e);
					throw e;
				}
				
				return lStrPendingId;
			}
    	 
    	 
}