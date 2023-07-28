package com.tcs.sgv.common.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.valueobject.AclPostroleRlt;
import com.tcs.sgv.acl.valueobject.AclRoleMst;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.idgenerator.delegate.IDGenerateDelegate;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.valueobject.CmnLanguageMst;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.ess.dao.OrgDesignationMstDao;
import com.tcs.sgv.ess.dao.OrgDesignationMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgGradeMstDao;
import com.tcs.sgv.ess.dao.OrgGradeMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgPostMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgUserMstDao;
import com.tcs.sgv.ess.dao.OrgUserMstDaoImpl;
import com.tcs.sgv.ess.valueobject.OrgDesignationMst;
import com.tcs.sgv.ess.valueobject.OrgEmpMst;
import com.tcs.sgv.ess.valueobject.OrgGradeMst;
import com.tcs.sgv.ess.valueobject.OrgPostDetailsRlt;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.ess.valueobject.OrgUserpostRlt;

public class AddNewUserDAOImpl extends GenericDaoHibernateImpl 
{
	Session ghibSession = null;
	public AddNewUserDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public List<ComboValuesVO> getAllTreasury() throws Exception {

		List<ComboValuesVO> lLstTreasury = new ArrayList<ComboValuesVO>();
		StringBuilder lStrQuery = new StringBuilder();
		List lLstResultList;
		Session ghibSession = getSession();
		try {
			lStrQuery.append(" Select locId,locName ");
			lStrQuery.append(" FROM CmnLocationMst ");
			lStrQuery.append(" WHERE departmentId = :departmentId");
			lStrQuery.append(" AND cmnLanguageMst.langId =:langId");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			hqlQuery.setLong("departmentId", 100003);
			hqlQuery.setLong("langId", 1);
			
			lLstResultList = hqlQuery.list();
			if (lLstResultList != null && lLstResultList.size() > 0) {
				Iterator itr = lLstResultList.iterator();

				ComboValuesVO cmbVO = null;
				Object[] obj = null;
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString().replaceAll("&", "And"));
					lLstTreasury.add(cmbVO);
				}
			}
			
		} catch (Exception e) {
			logger.error("Error in getAllTreasury :" + e, e);
			throw e;
		}
		return lLstTreasury;
	}
	
	public List<ComboValuesVO> getAllRoles() throws Exception {

		List<ComboValuesVO> lLstRoles = new ArrayList<ComboValuesVO>();
		StringBuilder lStrQuery = new StringBuilder();
		List lLstResultList;
		Session ghibSession = getSession();
		try {
			lStrQuery.append(" Select aclRoleMst.roleId,roleName ");
			lStrQuery.append(" FROM AclRoleDetailsRlt ");
			lStrQuery.append(" WHERE aclRoleMst.roleId IN (365450,365451,365452,365453,365454,365460,365461,365462,365463,365466) ");
			lStrQuery.append(" AND cmnLanguageMst.langId = :langId");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());			
			hqlQuery.setLong("langId", 1);
			
			lLstResultList = hqlQuery.list();
			if (lLstResultList != null && lLstResultList.size() > 0) {
				Iterator itr = lLstResultList.iterator();

				ComboValuesVO cmbVO = null;
				Object[] obj = null;
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					lLstRoles.add(cmbVO);
				}
			}			
		} catch (Exception e) {
			logger.error("Error in getAllRoles :" + e, e);
			throw e;
		}
		return lLstRoles;
	}
	
	public List<ComboValuesVO> getAllDesignation() throws Exception {

		List<ComboValuesVO> lLstDsgn = new ArrayList<ComboValuesVO>();
		StringBuilder lStrQuery = new StringBuilder();
		List lLstResultList;
		Session ghibSession = getSession();
		try {
			lStrQuery.append("SELECT distinct odm.dsgnCode, odm.dsgnName ");
			lStrQuery.append("FROM OrgPostMst opm, CmnLocationMst clm, OrgDesignationMst odm ");
			lStrQuery.append("WHERE opm.locationCode = clm.locationCode AND clm.departmentId =:deptId AND clm.cmnLanguageMst.langId =:langId ");
			lStrQuery.append("AND odm.dsgnCode = opm.dsgnCode AND odm.cmnLanguageMst.langId =:langId ");			
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			hqlQuery.setLong("langId", 1);
			hqlQuery.setLong("deptId", 100003);
			
			lLstResultList = hqlQuery.list();
			if (lLstResultList != null && lLstResultList.size() > 0) {
				Iterator itr = lLstResultList.iterator();

				ComboValuesVO cmbVO = null;
				Object[] obj = null;
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					lLstDsgn.add(cmbVO);
				}
			}			
		} catch (Exception e) {
			logger.error("Error in getAllDesignation :" + e, e);
			throw e;
		}
		return lLstDsgn;
	}	
	
	public String chkSevaarthId(String lStrSevaarth) throws Exception
	{
		String lStrResData = "";
		List lLstResData = null;
		StringBuilder lSBQuery = null;
		Long lLngUserId = null;
		
		try{
			lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT userId FROM OrgUserMst WHERE userName =:usrName ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("usrName", lStrSevaarth);			
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lLngUserId = (Long) lLstResData.get(0);
				lSBQuery = new StringBuilder();
				lSBQuery.append("SELECT APR.aclRoleMst.roleId FROM OrgUserpostRlt OUPR, AclPostroleRlt APR ");
				lSBQuery.append("WHERE OUPR.orgUserMst.userId = :userId AND OUPR.orgPostMstByPostId.postId = APR.orgPostMst.postId ");
				lSBQuery.append("AND OUPR.activateFlag = 1 AND APR.cmnLookupMstByActivate.lookupId = 1 ");
				Query lQueryRole = ghibSession.createQuery(lSBQuery.toString());
				lQueryRole.setLong("userId", lLngUserId);
				lLstResData = lQueryRole.list();
				
				if(lLstResData.size() == 0){
					lSBQuery = new StringBuilder();
					lSBQuery.append("SELECT ODM.ddoName FROM OrgUserMst OUM, OrgDdoMst ODM, OrgUserpostRlt OUPR ");
					lSBQuery.append("WHERE OUM.userName = :sevaarthId AND OUM.userId = OUPR.orgUserMst.userId ");
					lSBQuery.append("AND OUPR.orgPostMstByPostId.postId = ODM.postId ");
					Query lQueryDdo = ghibSession.createQuery(lSBQuery.toString());
					lQueryDdo.setParameter("sevaarthId", lStrSevaarth);
					lLstResData = lQueryDdo.list();
					
					if(lLstResData.size() == 0){
						lStrResData = "E";
					}else{
						lStrResData = "Y";
					}
				}else{
					lStrResData = "Y";
				}
			}else{
				lStrResData = "N";
			}
		}catch (Exception e) {
			logger.error("Error in chkSevaarthId :" + e, e);
			throw e;
		}
		return lStrResData;
	}
	
	
	
	
	
	public Long insertUserMst(String lStrSevaarthId, Long lLngUserIdCrtd, Long lLngPostIdCrtd, Map inputMap) throws Exception
	{
		Long lLngUserId = null;
		Date lObjPwdChng = new Date();
		lObjPwdChng.setMonth(lObjPwdChng.getMonth()+2);
		
		try{
			CmnLookupMstDAO lObjCmnLookUpMstDao = new CmnLookupMstDAOImpl(CmnLookupMst.class,this.getSessionFactory());
			CmnLookupMst lObjCmnLookupMst = lObjCmnLookUpMstDao.read(1l);
			
			OrgUserMstDao lObjOrgUserMstDao = new OrgUserMstDaoImpl(OrgUserMst.class,this.getSessionFactory());
			OrgUserMst lObjOrgUserMstCrtdUsr = lObjOrgUserMstDao.read(lLngUserIdCrtd);
			
			OrgPostMstDaoImpl orgPostId = new OrgPostMstDaoImpl(OrgPostMst.class,this.getSessionFactory());
			OrgPostMst postId = orgPostId.read(lLngPostIdCrtd);
			
			OrgUserMst lObjUserMst = new OrgUserMst();
			//lLngUserId = Long.parseLong(IDGenerateDelegate.getNextIdWODbidLocationId("org_user_mst", inputMap));
			lLngUserId = Long.parseLong(IDGenerateDelegate.getNextIdWODbidLocationId("org_post_mst", inputMap));
			lObjUserMst.setUserId(lLngUserId);
			lObjUserMst.setUserName(lStrSevaarthId);
			lObjUserMst.setPassword("0b76f0f411f6944f9d192da0fcbfb292");
			lObjUserMst.setPwdchangedDate(lObjPwdChng);
			lObjUserMst.setCmnLookupMst(lObjCmnLookupMst);
			lObjUserMst.setStartDate(DBUtility.getCurrentDateFromDB());
			lObjUserMst.setActivateFlag(1l);
			lObjUserMst.setCreatedDate(DBUtility.getCurrentDateFromDB());
			lObjUserMst.setOrgUserMstByCreatedBy(lObjOrgUserMstCrtdUsr);
			lObjUserMst.setOrgPostMstByCreatedByPost(postId);
			lObjUserMst.setSecretQueCode("Secret_Other");
			lObjUserMst.setSecretAnswer("ifms");
			ghibSession.save(lObjUserMst);
			ghibSession.flush();
		}catch(Exception e){
			  logger.error(" Error in insertUserMst : " + e, e);
			  logger.error("**********************User Id : "+lLngUserId + "**********************");
			  throw e;
		}
		
		return lLngUserId;
	}
	
	public void insertEmpMst(Long lLngUserId, String lStrFname, Long lLngUserIdCrtd, Long lLngPostIdCrtd, String lStrSalutation, String lStrDOB, Map inputMap) throws Exception
	{
		Long lLngEmpId = null;
		try{
			SimpleDateFormat lObjDateFormate = new SimpleDateFormat("dd/MM/yyyy");			
			Date lObjEmpDoj = new Date("01/01/9999");
			
			OrgGradeMstDao lObjOrgGradeMstDao = new OrgGradeMstDaoImpl(OrgGradeMst.class,this.getSessionFactory());
			OrgGradeMst lObjOrgGradeMst = lObjOrgGradeMstDao.read(100066l);
			
			CmnLanguageMstDao lObjCmnLanguageDao = new CmnLanguageMstDaoImpl(CmnLanguageMst.class,this.getSessionFactory());
			CmnLanguageMst lObjCmnLanguageMst = lObjCmnLanguageDao.read(1l);
			
			OrgUserMstDao lObjOrgUserMstDao = new OrgUserMstDaoImpl(OrgUserMst.class,this.getSessionFactory());
			OrgUserMst lObjOrgUserMst = lObjOrgUserMstDao.read(lLngUserId);			
			OrgUserMst lObjOrgUserMstCrtd = lObjOrgUserMstDao.read(lLngUserIdCrtd);
			
			OrgPostMstDaoImpl orgPostId = new OrgPostMstDaoImpl(OrgPostMst.class,this.getSessionFactory());
			OrgPostMst postId = orgPostId.read(lLngPostIdCrtd);
			
			OrgEmpMst lObjEmpMst = new OrgEmpMst();
			lLngEmpId = Long.parseLong(IDGenerateDelegate.getNextIdWODbidLocationId("org_emp_mst", inputMap));
			lObjEmpMst.setEmpId(lLngEmpId);
			lObjEmpMst.setEmpFname(lStrFname);
			lObjEmpMst.setEmpLname(" ");
			lObjEmpMst.setEmpDob(lObjDateFormate.parse(lStrDOB));
			lObjEmpMst.setEmpDoj(lObjEmpDoj);
			lObjEmpMst.setEmpSrvcFlag(1l);
			lObjEmpMst.setOrgGradeMst(lObjOrgGradeMst);
			lObjEmpMst.setCmnLanguageMst(lObjCmnLanguageMst);
			lObjEmpMst.setOrgUserMst(lObjOrgUserMst);
			lObjEmpMst.setStartDate(DBUtility.getCurrentDateFromDB());
			lObjEmpMst.setActivateFlag(1l);
			lObjEmpMst.setOrgUserMstByCreatedBy(lObjOrgUserMstCrtd);
			lObjEmpMst.setOrgPostMstByCreatedByPost(postId);
			lObjEmpMst.setCreatedDate(DBUtility.getCurrentDateFromDB());
			lObjEmpMst.setEmpPrefix(lStrSalutation);
			
			ghibSession.save(lObjEmpMst);
			ghibSession.flush();
		}catch(Exception e){
			  logger.error(" Error in insertEmpMst : " + e, e);
			  throw e;
		}
	}
	
	public void insertOrgPostMst(Long lLngPostId, String lStrLocationCode, Long lLngUserIdCrtd, Long lLngPostIdCrtd, String lStrDsgnCode, Map inputMap)throws Exception
	{			
		try{
			CmnLookupMstDAO lObjCmnLookUpMstDao = new CmnLookupMstDAOImpl(CmnLookupMst.class,this.getSessionFactory());
			CmnLookupMst lObjCmnLookupMst = lObjCmnLookUpMstDao.read(13l);
			
			OrgUserMstDao lObjOrgUserMstDao = new OrgUserMstDaoImpl(OrgUserMst.class,this.getSessionFactory());
			OrgUserMst lObjOrgUserMstCrtdUsr = lObjOrgUserMstDao.read(lLngUserIdCrtd);
			
			OrgPostMstDaoImpl orgPostId = new OrgPostMstDaoImpl(OrgPostMst.class,this.getSessionFactory());
			OrgPostMst postId = orgPostId.read(lLngPostIdCrtd);
			
			OrgPostMst lObjOrgPostMst = new OrgPostMst();
			//lLngPostId = IFMSCommonServiceImpl.getNextSeqNum("org_post_mst", inputMap);
			lObjOrgPostMst.setPostId(lLngPostId);
			lObjOrgPostMst.setParentPostId(-1l);
			lObjOrgPostMst.setPostLevelId(1l);			
			lObjOrgPostMst.setCmnLookupMst(lObjCmnLookupMst);
			lObjOrgPostMst.setActivateFlag(1l);
			lObjOrgPostMst.setOrgUserMstByCreatedBy(lObjOrgUserMstCrtdUsr);
			lObjOrgPostMst.setOrgPostMstByCreatedByPost(postId);
			lObjOrgPostMst.setCreatedDate(DBUtility.getCurrentDateFromDB());
			lObjOrgPostMst.setLocationCode(lStrLocationCode);
			lObjOrgPostMst.setDsgnCode(lStrDsgnCode);
			ghibSession.save(lObjOrgPostMst);
			ghibSession.flush();
		}catch(Exception e){
			  logger.error(" Error in insertOrgPostMst : " + e, e);
			  logger.error("Post Id : "+lLngPostId);
			  throw e;
		}		
	}
	
	public void insertPostDtlsRlt(String lStrLocCode, Long lLngPostId, String lStrPostName, Long lLngDsgnId, Long lLngUserIdCrtd, Long lLngPostIdCrtd, Map inputMap)throws Exception
	{
		Long lLngPostDtlsId = null;
		try{
			OrgPostMstDaoImpl orgPostId = new OrgPostMstDaoImpl(OrgPostMst.class,this.getSessionFactory());
			OrgPostMst postId = orgPostId.read(lLngPostId);
			OrgPostMst postIdCrtd = orgPostId.read(lLngPostIdCrtd);
			
			CmnLocationMstDaoImpl lObjCmnLocationMstDao = new CmnLocationMstDaoImpl(CmnLocationMst.class,this.getSessionFactory());
			CmnLocationMst lObjCmnLocationMst = lObjCmnLocationMstDao.read(Long.parseLong(lStrLocCode));
			
			OrgDesignationMstDao lObjDesgnMstDao = new OrgDesignationMstDaoImpl(OrgDesignationMst.class,this.getSessionFactory());
			OrgDesignationMst lObjOrgDesigmMst = lObjDesgnMstDao.read(lLngDsgnId);
			
			CmnLanguageMstDao lObjCmnLanguageDao = new CmnLanguageMstDaoImpl(CmnLanguageMst.class,this.getSessionFactory());
			CmnLanguageMst lObjCmnLanguageMst = lObjCmnLanguageDao.read(1l);
			
			OrgUserMstDao lObjOrgUserMstDao = new OrgUserMstDaoImpl(OrgUserMst.class,this.getSessionFactory());
			OrgUserMst lObjOrgUserMstCrtd = lObjOrgUserMstDao.read(lLngUserIdCrtd);			
			
			OrgPostDetailsRlt lObjPostDtldRlt = new OrgPostDetailsRlt();
			lLngPostDtlsId = Long.parseLong(IDGenerateDelegate.getNextIdWODbidLocationId("org_post_details_rlt", inputMap));
			lObjPostDtldRlt.setPostDetailId(lLngPostDtlsId);
			lObjPostDtldRlt.setOrgPostMst(postId);
			lObjPostDtldRlt.setPostName(lStrPostName);
			if(lStrPostName.length() > 60){
				lObjPostDtldRlt.setPostShortName(lStrPostName.substring(0, 60));
			}else{
				lObjPostDtldRlt.setPostShortName(lStrPostName);
			}
			lObjPostDtldRlt.setCmnLocationMst(lObjCmnLocationMst);
			lObjPostDtldRlt.setOrgDesignationMst(lObjOrgDesigmMst);
			lObjPostDtldRlt.setCmnLanguageMst(lObjCmnLanguageMst);
			lObjPostDtldRlt.setOrgUserMstByCreatedBy(lObjOrgUserMstCrtd);
			lObjPostDtldRlt.setOrgPostMstByCreatedByPost(postIdCrtd);
			lObjPostDtldRlt.setCreatedDate(DBUtility.getCurrentDateFromDB());
			ghibSession.save(lObjPostDtldRlt);
			ghibSession.flush();
		}catch(Exception e){
			  logger.error(" Error in insertPostDtlsRlt : " + e, e);
			  throw e;
		}
	}
	
	public void insertPostRoleRlt(Long lLngPostId, Long lLngUserIdCrtd, Long lLngPostIdCrtd, Long lLngRoleId, Map inputMap)throws Exception
	{
		Long lLngPostRoleId = null;
		
		try{
			OrgPostMstDaoImpl orgPostId = new OrgPostMstDaoImpl(OrgPostMst.class,this.getSessionFactory());
			OrgPostMst postId = orgPostId.read(lLngPostId);
			OrgPostMst postIdCrtd = orgPostId.read(lLngPostIdCrtd);
			
			DdoOutsideSevaarthDAOImpl lObjDdoOutSideSevaarth = new DdoOutsideSevaarthDAOImpl(AclRoleMst.class,this.getSessionFactory());
			AclRoleMst lObjAclRoleMst = (AclRoleMst) lObjDdoOutSideSevaarth.read(lLngRoleId);
			
			CmnLookupMstDAOImpl actFlag = new CmnLookupMstDAOImpl(CmnLookupMst.class,this.getSessionFactory()); 
			CmnLookupMst activeFlag = actFlag.read(Long.parseLong("1"));
			
			OrgUserMstDao lObjOrgUserMstDao = new OrgUserMstDaoImpl(OrgUserMst.class,this.getSessionFactory());
			OrgUserMst lObjOrgUserMstCrtd = lObjOrgUserMstDao.read(lLngUserIdCrtd);
			
			AclPostroleRlt lObjAclPostRoleRlt = new AclPostroleRlt();
			lLngPostRoleId = Long.parseLong(IDGenerateDelegate.getNextIdWODbidLocationId("acl_postrole_rlt", inputMap));
			lObjAclPostRoleRlt.setPostRoleId(lLngPostRoleId);
			lObjAclPostRoleRlt.setOrgPostMst(postId);
			lObjAclPostRoleRlt.setAclRoleMst(lObjAclRoleMst);
			lObjAclPostRoleRlt.setStartDate(DBUtility.getCurrentDateFromDB());
			lObjAclPostRoleRlt.setCmnLookupMstByActivate(activeFlag);
			lObjAclPostRoleRlt.setOrgPostMstByCreatedByPost(postIdCrtd);
			lObjAclPostRoleRlt.setOrgUserMstByCreatedBy(lObjOrgUserMstCrtd);
			lObjAclPostRoleRlt.setCreatedDate(DBUtility.getCurrentDateFromDB());
			ghibSession.save(lObjAclPostRoleRlt);
			ghibSession.flush();
		}catch(Exception e){
			  logger.error(" Error in insertPostRoleRlt : " + e, e);
			  throw e;
		}
	}
	
	public void insertUserPostRlt(Long lLngPostId, Long lLngUserId, Long lLngUserIdCrtd, Long lLngPostIdCrtd, Map inputMap)throws Exception
	{
		Long lLngEmpPostId = null;
		
		try{
			OrgPostMstDaoImpl orgPostId = new OrgPostMstDaoImpl(OrgPostMst.class,this.getSessionFactory());
			OrgPostMst postId = orgPostId.read(lLngPostId);
			OrgPostMst postIdCrtd = orgPostId.read(lLngPostIdCrtd);
			
			OrgUserMstDao lObjOrgUserMstDao = new OrgUserMstDaoImpl(OrgUserMst.class,this.getSessionFactory());
			OrgUserMst lObjOrgUserMstCrtd = lObjOrgUserMstDao.read(lLngUserIdCrtd);
			OrgUserMst lObjOrgUserMst = lObjOrgUserMstDao.read(lLngUserId);
			
			CmnLookupMstDAO lObjCmnLookUpMstDao = new CmnLookupMstDAOImpl(CmnLookupMst.class,this.getSessionFactory());
			CmnLookupMst lObjCmnLookupMst = lObjCmnLookUpMstDao.read(13l);
			
			OrgUserpostRlt lObjOrgUserpostRlt = new OrgUserpostRlt();
			lLngEmpPostId = Long.parseLong(IDGenerateDelegate.getNextIdWODbidLocationId("org_userpost_rlt", inputMap));
			lObjOrgUserpostRlt.setEmpPostId(lLngEmpPostId);
			lObjOrgUserpostRlt.setOrgUserMst(lObjOrgUserMst);
			lObjOrgUserpostRlt.setOrgPostMstByPostId(postId);
			lObjOrgUserpostRlt.setStartDate(DBUtility.getCurrentDateFromDB());
			lObjOrgUserpostRlt.setActivateFlag(1l);
			lObjOrgUserpostRlt.setOrgPostMstByCreatedByPost(postIdCrtd);
			lObjOrgUserpostRlt.setOrgUserMstByCreatedBy(lObjOrgUserMstCrtd);
			lObjOrgUserpostRlt.setCreatedDate(DBUtility.getCurrentDateFromDB());
			lObjOrgUserpostRlt.setCmnLookupUserPostType(lObjCmnLookupMst);
			ghibSession.save(lObjOrgUserpostRlt);
			ghibSession.flush();
		}catch(Exception e){
			  logger.error(" Error in insertUserPostRlt : " + e, e);
			  throw e;
		}
	}
	
	public void updateEmpUserName(String lStrUserName) throws Exception
	{
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE OrgUserMst OUM SET OUM.userName = :uName || '_EMP' \n");
			lSBQuery.append("WHERE OUM.userName = :uName \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("uName", lStrUserName);
			lQuery.executeUpdate();
		}catch (Exception e) {
			logger.error(" Error in updateEmpUserName : " + e, e);
			throw e;
		}
	}
}
