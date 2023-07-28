package com.tcs.sgv.common.dao;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.ess.dao.OrgPostMstDao;
import com.tcs.sgv.ess.dao.OrgPostMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgUserMstDao;
import com.tcs.sgv.ess.dao.OrgUserMstDaoImpl;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.ess.valueobject.OrgUserpostRlt;

public class ChangeUserNameDAOImpl extends GenericDaoHibernateImpl
{	
	Session ghibSession = null;
	public ChangeUserNameDAOImpl(Class type ,SessionFactory sessionFactory) 
	{
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public List getDataFromDdoCode(String lStrDdoCode) throws Exception
	{
		List lLstResData = null;
		
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ddoName, ddoPersonalName, postId \n");
			lSBQuery.append("FROM OrgDdoMst \n");
			lSBQuery.append("WHERE ddoCode = :ddoCode ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			
			lLstResData = lQuery.list();
		} catch (Exception e) {					
			logger.error(" Error is : " + e, e);
			throw e;
		}
		
		return lLstResData;
	}
	
	public String getUnameFromDdoCode(String lStrDdoCode) throws Exception
	{		
		String lStrUserName = "";
		
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT OUM.userName \n");
			lSBQuery.append("FROM OrgDdoMst ODM, OrgUserMst OUM, OrgUserpostRlt OUPR \n");
			lSBQuery.append("WHERE ODM.ddoCode = :ddoCode AND ODM.postId = OUPR.orgPostMstByPostId.postId \n");
			lSBQuery.append("AND OUPR.orgUserMst.userId = OUM.userId AND OUPR.activateFlag = 1");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			
			lStrUserName = lQuery.list().get(0).toString();
		} catch (Exception e) {						
			logger.error(" Error is : " + e, e);
			throw e;
		}
		
		return lStrUserName;
	}
	
	public String checkNewUserName(String lStrUsrName, Long lLngPostId) throws Exception
	{
		List lLstResData = null;
		String lStrRes = "";
		StringBuilder lSBQuery = null;
		Query lQuery = null;
		Long lLngUserId = null;
		String lStrPostLookUp = null;
		String lStrDdoCode = "";
		String lStrDdoName = "";
		List lLstDdoDtls = null;
		List lLstEmpDtls = null;
		String lStrFname = "";
		String lStrMname = "";
		String lStrLname = "";
		Object []lObj = null;
		
		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT OUM.userId, OUM.userName, OUPR.cmnLookupUserPostType.lookupId \n");
			lSBQuery.append("FROM OrgUserMst OUM, OrgUserpostRlt OUPR, OrgDdoMst ODM \n");
			lSBQuery.append("WHERE OUM.userName =:uName AND OUM.userId = OUPR.orgUserMst.userId AND OUPR.orgPostMstByPostId.postId = ODM.postId \n");
			lSBQuery.append("AND OUPR.activateFlag = 1 \n");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("uName", lStrUsrName);
			
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lStrRes = "E";
				for(int i=0;i<lLstResData.size();i++){
					lObj = (Object[]) lLstResData.get(i);
					if(lObj[2].toString().equals("13")){
						lStrPostLookUp = "13";
						break;
					}else{
						lStrPostLookUp = "100116";
					}
				}
				
				if(lStrPostLookUp.equals("13")){
					lLngUserId = Long.parseLong(lObj[0].toString());
					lSBQuery = new StringBuilder();
					lSBQuery.append("SELECT ddoCode, ddoName FROM OrgDdoMst WHERE postId in (SELECT orgPostMstByPostId.postId from OrgUserpostRlt WHERE orgUserMst.userId =:userId AND activateFlag = 1) \n");
					lQuery = ghibSession.createQuery(lSBQuery.toString());
					lQuery.setParameter("userId", lLngUserId);
					lLstDdoDtls = lQuery.list();
					if(lLstDdoDtls.size() > 0){
						Object []lObjDdo = (Object[]) lLstDdoDtls.get(0);
						lStrDdoCode = lObjDdo[0].toString();
						lStrDdoName = lObjDdo[1].toString();
						lStrRes = "Y"+"#"+lStrDdoCode+"$"+lStrDdoName;
					}
				}else if(lStrPostLookUp.equals("100116")){
					lStrRes = "Y#Child";
				}
			}else{
				
				lSBQuery = new StringBuilder();
				lSBQuery.append("FROM AclPostroleRlt WHERE orgPostMst.postId = :postId \n");
				lSBQuery.append("AND aclRoleMst.roleId IN (365450,365451,365452,365453,365454,365460,365461,365462,365463,365466) \n");
				lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setLong("postId", lLngPostId);
				
				List lLstData = lQuery.list();
				if(lLstData.size() > 0){
					lSBQuery = new StringBuilder();
					lSBQuery.append("SELECT empFname,empMname,empLname FROM OrgEmpMst WHERE orgUserMst.userId IN (SELECT orgUserMst.userId FROM OrgUserpostRlt WHERE orgPostMstByPostId.postId =:postId AND activateFlag = 1) \n");
					lQuery = ghibSession.createQuery(lSBQuery.toString());
					lQuery.setLong("postId", lLngPostId);
					lLstEmpDtls = lQuery.list();
					if(lLstEmpDtls.size() > 0){
						Object []lObjEmp = (Object[]) lLstEmpDtls.get(0);
						if(lObjEmp[0] != null){
							lStrFname = lObjEmp[0].toString();
						}
						if(lObjEmp[1] != null){
							lStrMname = lObjEmp[1].toString();
						}
						if(lObjEmp[2] != null){
							lStrLname = lObjEmp[2].toString();
						}
						lStrRes = "Exist"+"#"+lStrFname+" "+lStrMname+" "+lStrLname;
					}
				}else{
					lStrRes = "N";
				}
			}			
		} catch (Exception e) {				
			logger.error(" Error is : " + e, e);
			throw e;
		}
		
		return lStrRes;
	}
	
	public String updateUname(String lStrOldUname, String lStrNewUname, Long lLongSecondaryPostId, Long lLongSecondaryUserId, String lStrDdoCode) throws Exception
	{
		StringBuilder lSBQuery = null;
		List lLstResData = null;
		List lLstUsr = null;
		List lLstChkPensionUsr = null;
		Long lLngPrimaryLookUpId = 13l;		
		String lStrDdoPrimaryOrSecondary = "";
		String lStrResData = "Update";
		
		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append("FROM OrgUserMst \n");
			lSBQuery.append("WHERE userName =:uName \n");			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("uName", lStrNewUname);
			
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				StringBuilder lSBQueryUsr = new StringBuilder();
				lSBQueryUsr.append("SELECT * FROM ORG_USER_MST WHERE USER_NAME =:uName AND USER_ID NOT IN (SELECT USER_ID FROM ORG_USERPOST_RLT WHERE POST_ID IN \n");
				lSBQueryUsr.append("(SELECT POST_ID FROM ACL_POSTROLE_RLT WHERE ROLE_ID IN(365450,365451,365452,365453,365454,365460,365461,365462,365463,365466))) \n");
				lSBQueryUsr.append("AND USER_ID NOT IN (SELECT POST_ID FROM ORG_USERPOST_RLT WHERE POST_ID IN(SELECT POST_ID FROM ORG_DDO_MST)) ");
				Query lQueryUsr = ghibSession.createSQLQuery(lSBQueryUsr.toString());
				lQueryUsr.setParameter("uName", lStrNewUname);
				lLstUsr = lQueryUsr.list();
				if(lLstUsr.size() > 0){
					StringBuilder lSBQueryChkEmp = new StringBuilder();
					lSBQueryChkEmp.append("SELECT OUM.userName FROM OrgUserMst OUM WHERE OUM.userName = :usrName || '_EMP' \n");
					Query lQueryChkEmp = ghibSession.createQuery(lSBQueryChkEmp.toString());
					lQueryChkEmp.setParameter("usrName", lStrNewUname);
					lLstResData = lQueryChkEmp.list();
					
					if(lLstResData.size() == 0){
						StringBuilder lSBQueryUpdtUsr = new StringBuilder();
						lSBQueryUpdtUsr.append("UPDATE OrgUserMst OUM SET OUM.userName = :uName || '_EMP' \n");
						lSBQueryUpdtUsr.append("WHERE OUM.userName = :uName \n");					
						Query lQueryUpdtUsr = ghibSession.createQuery(lSBQueryUpdtUsr.toString());
						lQueryUpdtUsr.setParameter("uName", lStrNewUname);
						//lQueryUpdtUsr.setParameter("ddoCode",lStrDdoCode);					
						lQueryUpdtUsr.executeUpdate();
					}
				}else{
					StringBuilder lSBQueryChkPensionUser = new StringBuilder();
					lSBQueryChkPensionUser.append("SELECT * FROM ORG_USER_MST WHERE USER_NAME =:uName AND USER_ID IN (SELECT USER_ID FROM ORG_USERPOST_RLT WHERE POST_ID IN \n");
					lSBQueryChkPensionUser.append("(SELECT POST_ID FROM ACL_POSTROLE_RLT WHERE ROLE_ID IN(365450,365451,365452,365453,365454,365460,365461,365462,365463,365466))) ");					
					Query lQueryChkPensionUsr = ghibSession.createSQLQuery(lSBQueryChkPensionUser.toString());
					lQueryChkPensionUsr.setParameter("uName", lStrNewUname);
					lLstChkPensionUsr = lQueryChkPensionUsr.list();
					
					if(lLstChkPensionUsr != null && lLstChkPensionUsr.size() > 0){
						lSBQuery = new StringBuilder();
						lSBQuery.append("UPDATE ORG_USER_MST SET USER_NAME =:newUname, ACTIVATE_FLAG = 1, UPDATED_DATE = :currentDate \n");
						lSBQuery.append("WHERE USER_ID = :userId \n");
						lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
						lQuery.setString("newUname", lStrNewUname+"_DDO");
						lQuery.setLong("userId", lLongSecondaryPostId);
						lQuery.setDate("currentDate", DBUtility.getCurrentDateFromDB());
						lQuery.executeUpdate();
						
						lSBQuery = new StringBuilder();
						lSBQuery.append("UPDATE ORG_USER_MST SET ACTIVATE_FLAG = 1, UPDATED_DATE = :currentDate \n");
						lSBQuery.append("WHERE USER_NAME = :uname \n");
						lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
						lQuery.setString("uname", lStrDdoCode+"_AST");
						lQuery.setDate("currentDate", DBUtility.getCurrentDateFromDB());
						lQuery.executeUpdate();
						
						lSBQuery = new StringBuilder();
						lSBQuery.append(" UPDATE ORG_DDO_MST set ACTIVATE_FLAG = 1, UPDATED_DATE = :currentDate \n");
						lSBQuery.append(" where DDO_CODE = :ddoCode \n");
						lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
						lQuery.setString("ddoCode", lStrOldUname);
						lQuery.setDate("currentDate", DBUtility.getCurrentDateFromDB());
						lQuery.executeUpdate();
						
						return lStrResData+"#"+lStrNewUname+"_DDO";
					}else{
						Long lLngUserIdNew = getUserIdFromUserName(lStrNewUname);
						if(lLngUserIdNew == null){
							lStrResData = "inactive";
						}else{
							lSBQuery = new StringBuilder();
							lSBQuery.append("SELECT cmnLookupUserPostType.lookupId FROM OrgUserpostRlt WHERE orgUserMst.userId = :userId  \n");
							lSBQuery.append("AND activateFlag = 1 AND cmnLookupUserPostType.lookupId = 13 \n");
							lQuery = ghibSession.createQuery(lSBQuery.toString());
							lQuery.setLong("userId", lLngUserIdNew);
							List lLstData = lQuery.list();
							
							StringBuilder lSBQueryUpdt = new StringBuilder();
							lSBQueryUpdt.append("UPDATE OrgUserpostRlt SET activateFlag = 0, endDate =:endDate \n");
							lSBQueryUpdt.append("WHERE orgPostMstByPostId.postId =:PostId AND activateFlag = 1 ");							
							Query lQuerySec = ghibSession.createQuery(lSBQueryUpdt.toString());		
							lQuerySec.setParameter("PostId", lLongSecondaryPostId);
							lQuerySec.setParameter("endDate", DBUtility.getCurrentDateFromDB());
							lQuerySec.executeUpdate();
							
							if(lLstData != null && lLstData.size() > 0){
								addEntryInUserPost(lLngUserIdNew, lLongSecondaryPostId, 100116l);
							}else{
								addEntryInUserPost(lLngUserIdNew, lLongSecondaryPostId, 13l);
							}
						}
						
						//lStrResData = "Exist";
						return lStrResData+"#"+lStrNewUname;
					}
				}
				lStrDdoPrimaryOrSecondary = "Y";
			}
			else
			{					
				lSBQuery = new StringBuilder();
				lSBQuery.append("FROM OrgUserpostRlt WHERE orgPostMstByPostId.postId = :secondaryPostId and cmnLookupUserPostType.lookupId = :lLngPriLookup \n");
				lSBQuery.append("AND activateFlag = 1 \n");
				Query lQueryCheckPrimaryUserId = ghibSession.createQuery(lSBQuery.toString());
				lQueryCheckPrimaryUserId.setLong("secondaryPostId", lLongSecondaryPostId);
				lQueryCheckPrimaryUserId.setLong("lLngPriLookup", lLngPrimaryLookUpId);
				lLstResData = lQueryCheckPrimaryUserId.list();
				
				if(lLstResData.size() > 0){
					lStrDdoPrimaryOrSecondary = "Y";
				}else{
					lStrDdoPrimaryOrSecondary = "N";
					
					/*lSBQuery = new StringBuilder();
					lSBQuery.append("UPDATE OrgUserpostRlt SET activateFlag = 0, endDate = :endDate \n");
					lSBQuery.append("WHERE orgPostMstByPostId.postId = :postId AND activateFlag = 1 \n");
					Query lQueryLkUp = ghibSession.createQuery(lSBQuery.toString());
					lQueryLkUp.setParameter("postId", lLongSecondaryPostId);
					lQueryLkUp.setParameter("endDate", DBUtility.getCurrentDateFromDB());
					lQueryLkUp.executeUpdate();					
					addEntryInUserPost(lLongSecondaryPostId, lLongSecondaryPostId, lLngPrimaryLookUpId);*/
				}
			}
			
			if(lStrDdoPrimaryOrSecondary.equals("Y")){
				lSBQuery = new StringBuilder();
				lSBQuery.append("UPDATE ORG_USER_MST SET USER_NAME =:newUname, ACTIVATE_FLAG = 1, UPDATED_DATE = :currentDate \n");
				lSBQuery.append("WHERE USER_NAME = :oldUname \n");
				lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lQuery.setString("newUname", lStrNewUname);
				lQuery.setString("oldUname", lStrOldUname);
				lQuery.setDate("currentDate", DBUtility.getCurrentDateFromDB());
				lQuery.executeUpdate();
				
				lSBQuery = new StringBuilder();
				lSBQuery.append("UPDATE ORG_USER_MST SET ACTIVATE_FLAG = 1, UPDATED_DATE = :currentDate \n");
				lSBQuery.append("WHERE USER_NAME = :uname \n");
				lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lQuery.setString("uname", lStrDdoCode+"_AST");
				lQuery.setDate("currentDate", DBUtility.getCurrentDateFromDB());
				lQuery.executeUpdate();
				
				lSBQuery = new StringBuilder();
				lSBQuery.append(" UPDATE ORG_DDO_MST set ACTIVATE_FLAG = 1, UPDATED_DATE = :currentDate \n");
				lSBQuery.append(" where DDO_CODE = :ddoCode\n");
				lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lQuery.setString("ddoCode", lStrOldUname);
				lQuery.setDate("currentDate", DBUtility.getCurrentDateFromDB());
				lQuery.executeUpdate();
				
			}else if(lStrDdoPrimaryOrSecondary.equals("N")){
				lSBQuery = new StringBuilder();
				lSBQuery.append("UPDATE ORG_USER_MST SET USER_NAME =:newUname, ACTIVATE_FLAG = 1, UPDATED_DATE = :currentDate \n");
				lSBQuery.append("WHERE USER_ID = :userId \n");
				lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lQuery.setString("newUname", lStrNewUname);
				lQuery.setLong("userId", lLongSecondaryPostId);
				lQuery.setDate("currentDate", DBUtility.getCurrentDateFromDB());
				lQuery.executeUpdate();
				
				lSBQuery = new StringBuilder();
				lSBQuery.append("UPDATE ORG_USER_MST SET ACTIVATE_FLAG = 1, UPDATED_DATE = :currentDate \n");
				lSBQuery.append("WHERE USER_NAME = :uname \n");
				lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lQuery.setString("uname", lStrDdoCode+"_AST");
				lQuery.setDate("currentDate", DBUtility.getCurrentDateFromDB());
				lQuery.executeUpdate();
				
				lSBQuery = new StringBuilder();
				lSBQuery.append(" UPDATE ORG_DDO_MST set ACTIVATE_FLAG = 1, UPDATED_DATE = :currentDate \n");
				lSBQuery.append(" where DDO_CODE = :ddoCode \n");
				lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lQuery.setString("ddoCode", lStrOldUname);
				lQuery.setDate("currentDate", DBUtility.getCurrentDateFromDB());
				lQuery.executeUpdate();
			}
			
		} catch (Exception e) {			
			logger.error(" Error is : " + e, e);
			throw e;
		}		
		return lStrResData+"#"+lStrNewUname;
	}
	
	public String getUserIdPostIDFromDdoCode(String lStrDdoCode) throws Exception
	{
		String lStrRes = "";
		List lLstData = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ODM.postId, OUPR.orgUserMst.userId FROM OrgDdoMst ODM, OrgUserpostRlt OUPR \n");
			lSBQuery.append("WHERE ODM.ddoCode = :ddoCode AND ODM.postId = OUPR.orgPostMstByPostId.postId AND OUPR.activateFlag = 1 \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lLstData = lQuery.list();
			
			if(lLstData.size() > 0){
				Object[] lObj = (Object[]) lLstData.get(0);
				lStrRes = lObj[0].toString()+"#"+lObj[1].toString();
			}
		}catch(Exception e) {			
			logger.error(" Error is : " + e, e);
			throw e;
		}
		
		return lStrRes;
	}
	
	public void setPrimaryPost(Long LLongSecondaryUserId, Long lLongSecondaryPostId, Long lLongPrimaryUserId, Long lLongPrimaryPost) throws Exception
	{
		Long lLngSecndLookup = 100116l;
		Long lLngPriLookup = 13l;
		StringBuilder lSBQuery = null;
		List lLstResData = null;
		Long lLngChildPostId = null;
		
		try{
			
			// Checks child for given user id and updates one of those post as Primary if the post is switched to other post so that its childs dont remain parentless.
			
			// Checks if Secondary User Id is a Primary one for other DDOs (Childs)
			
			lSBQuery = new StringBuilder();
			lSBQuery.append("FROM OrgUserpostRlt WHERE orgPostMstByPostId.postId = :secondaryPostId and cmnLookupUserPostType.lookupId = :lLngPriLookup \n");
			lSBQuery.append("AND activateFlag = 1 \n");
			Query lQueryCheckPrimaryUserId = ghibSession.createQuery(lSBQuery.toString());
			lQueryCheckPrimaryUserId.setLong("secondaryPostId", lLongSecondaryPostId);
			lQueryCheckPrimaryUserId.setLong("lLngPriLookup", lLngPriLookup);
			lLstResData = lQueryCheckPrimaryUserId.list();
			
			if(lLstResData.size() > 0)
			{
				lSBQuery = new StringBuilder();
				lSBQuery.append("SELECT orgPostMstByPostId.postId FROM OrgUserpostRlt WHERE orgUserMst.userId = :secondaryUserId and cmnLookupUserPostType.lookupId = :lLngSecndLookup \n");
				lSBQuery.append("AND activateFlag = 1 \n");
				Query lQueryCheckChildForUserId = ghibSession.createQuery(lSBQuery.toString());
				lQueryCheckChildForUserId.setParameter("secondaryUserId", LLongSecondaryUserId);
				lQueryCheckChildForUserId.setParameter("lLngSecndLookup", lLngSecndLookup);
				lLstResData = lQueryCheckChildForUserId.list();
				
				if(lLstResData.size() > 0){
					lLngChildPostId = Long.valueOf(lLstResData.get(0).toString());
					lSBQuery = new StringBuilder();
					lSBQuery.append("UPDATE OrgUserpostRlt SET cmnLookupUserPostType.lookupId = 13 ");                       
					lSBQuery.append("WHERE orgPostMstByPostId.postId = :postId ");
					Query lQueryChild = ghibSession.createQuery(lSBQuery.toString());
					lQueryChild.setParameter("postId", lLngChildPostId);
					lQueryChild.executeUpdate();
				}
			}
			
			lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE OrgUserpostRlt SET activateFlag = 0, endDate =:endDate \n");
			lSBQuery.append("WHERE orgPostMstByPostId.postId =:secondaryPostId \n");
			lSBQuery.append("AND orgUserMst.userId = :secondaryUserId AND activateFlag = 1 \n");
			Query lQuerySec = ghibSession.createQuery(lSBQuery.toString());
			lQuerySec.setParameter("secondaryUserId", LLongSecondaryUserId);			
			lQuerySec.setParameter("secondaryPostId", lLongSecondaryPostId);
			lQuerySec.setParameter("endDate", DBUtility.getCurrentDateFromDB());
			lQuerySec.executeUpdate();
			
			addEntryInUserPost(lLongPrimaryUserId,lLongSecondaryPostId,lLngSecndLookup);			
		}catch(Exception e) {			
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}
	
	public Long getPostByUserName(String lStrUserName)
	{
		Long lLngPostId = null;
		
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT orgPostMstByPostId.postId FROM OrgUserpostRlt WHERE orgPostMstByPostId.postId IN \n");
			lSBQuery.append("(SELECT userId FROM OrgUserMst WHERE userName = :uName) AND activateFlag = 1 \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("uName", lStrUserName);
			lLngPostId = (Long) lQuery.list().get(0);
		} catch (Exception e) {		
			logger.error(" Error is : " + e, e);			
		}
		return lLngPostId;
	}
	
	public void addEntryInUserPost(Long lLongPrimaryUserId, Long lLongSecondaryPostId, Long lLngLookupId)
	{
		Long lLngEmpPostId = null;		
		OrgUserpostRlt lObjUserpostRlt = new OrgUserpostRlt();
		
		try{
			CmnLookupMstDAO lObjCmnLookupMstDAO = new CmnLookupMstDAOImpl(CmnLookupMst.class,this.getSessionFactory());
			CmnLookupMst lObjCmnLookupMst = lObjCmnLookupMstDAO.read(lLngLookupId);
			
			OrgUserMstDao  lObjOrgUserMstDao = new OrgUserMstDaoImpl(OrgUserMst.class,this.getSessionFactory());
			OrgUserMst lobjOrgUserMst = lObjOrgUserMstDao.read(lLongPrimaryUserId);			
			OrgUserMst lobjOrgUserMstCrtd = lObjOrgUserMstDao.read(1l);
			
			OrgPostMstDao lObjOrgPostMstDao = new OrgPostMstDaoImpl(OrgPostMst.class,this.getSessionFactory());
			OrgPostMst lObjOrgPostMst = lObjOrgPostMstDao.read(lLongSecondaryPostId);
			OrgPostMst lObjOrgPostMstCrtd = lObjOrgPostMstDao.read(1l);
			
			lLngEmpPostId = getMaxEmpPostId();
			lObjUserpostRlt.setEmpPostId(lLngEmpPostId);
			lObjUserpostRlt.setOrgUserMst(lobjOrgUserMst);
			lObjUserpostRlt.setOrgPostMstByPostId(lObjOrgPostMst);
			lObjUserpostRlt.setOrgUserMstByCreatedBy(lobjOrgUserMstCrtd);
			lObjUserpostRlt.setOrgPostMstByCreatedByPost(lObjOrgPostMstCrtd);
			lObjUserpostRlt.setActivateFlag(1l);
			lObjUserpostRlt.setCmnLookupUserPostType(lObjCmnLookupMst);			
			lObjUserpostRlt.setStartDate(DBUtility.getCurrentDateFromDB());
			lObjUserpostRlt.setCreatedDate(DBUtility.getCurrentDateFromDB());
			ghibSession.save(lObjUserpostRlt);
			ghibSession.flush();
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
		}
	}
	
	public Long getMaxEmpPostId()
	{
		Long lLngEmpPostId = null;
		
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("SELECT MAX(empPostId) FROM OrgUserpostRlt \n");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lLngEmpPostId = (Long) lQuery.list().get(0);
		
		return (lLngEmpPostId+1);
	}
	
	public Long getUserIdFromUserName(String lStrUserName)
	{
		Long lLngUserId = null;
		
		try {
			StringBuilder lSBQuery = new StringBuilder();		
			lSBQuery.append("SELECT userId FROM OrgUserMst WHERE userName = :uName AND activateFlag = 1 \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("uName", lStrUserName);
			lLngUserId = (Long) lQuery.list().get(0);
		} catch (Exception e) {		
			logger.error(" Error is : " + e, e);			
		}
		return lLngUserId;
	}
	
}
