package com.tcs.sgv.common.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.dao.AclRoleDetailsDaoImpl;
import com.tcs.sgv.acl.valueobject.AclPostroleRlt;
import com.tcs.sgv.acl.valueobject.AclRoleMst;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.valueobject.CmnDatabaseMst;
import com.tcs.sgv.common.valueobject.CmnLanguageMst;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.common.valueobject.RltDdoOrg;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.ess.dao.OrgDesignationMstDao;
import com.tcs.sgv.ess.dao.OrgDesignationMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgGradeMstDao;
import com.tcs.sgv.ess.dao.OrgGradeMstDaoImpl;
import com.tcs.sgv.ess.dao.OrgPostMstDao;
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
import com.tcs.sgv.fms.dao.CmnProjectMstDao;
import com.tcs.sgv.fms.dao.CmnProjectMstDaoImpl;
import com.tcs.sgv.fms.valueobject.CmnProjectMst;
import com.tcs.sgv.fms.valueobject.WfOrgLocMpgMst;
import com.tcs.sgv.fms.valueobject.WfOrgPostMpgMst;
import com.tcs.sgv.fms.valueobject.WfOrgUsrMpgMst;
import com.tcs.sgv.reports.common.dao.CmnLocationMstDAO;

public class DdoOutsideSevaarthDAOImpl extends GenericDaoHibernateImpl
{
	Session ghibSession = null;	
	public DdoOutsideSevaarthDAOImpl(Class type ,SessionFactory sessionFactory) 
	{
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public void updateUserName(String lStrNewUname,String lStrOldUname) throws Exception
	{
		StringBuilder lSBQuery = new StringBuilder();
		List lLstResData = null;
		List lLstUsr = null;
		
		try{
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
				}				
			}
			
			lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE ORG_USER_MST SET USER_NAME =:newUname, ACTIVATE_FLAG = 1 \n");
			lSBQuery.append("WHERE USER_NAME = :oldUname \n");
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("newUname", lStrNewUname);
			lQuery.setString("oldUname", lStrOldUname);
			lQuery.executeUpdate();
		}catch(Exception e){
			logger.error("Error is: " + e);
			throw e;
		}
	}
	
	public void updateDdoDetails(Long lLngUserId,String lStrEmpName,String lStrDob,String lStrDoj,String lStrServcExpr) throws Exception
	{
		try{
			SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE OrgEmpMst SET empFname =:empName, empMname =' ', empLname =' ', activateFlag =1, \n");
			lSBQuery.append("empDob =:DOB, empDoj =:DOJ, empSrvcExp=:EXP \n");
			lSBQuery.append("WHERE orgUserMst.userId =:userId \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("empName", lStrEmpName);
			lQuery.setParameter("DOB", lObjSimpleDateFormat.parse(lStrDob));
			lQuery.setParameter("DOJ", lObjSimpleDateFormat.parse(lStrDoj));
			lQuery.setParameter("EXP", lObjSimpleDateFormat.parse(lStrServcExpr));
			lQuery.setParameter("userId", lLngUserId);
			lQuery.executeUpdate();
		}catch(Exception e){
			logger.equals("Error is: " + e);
			throw e;
		}
	}
}
