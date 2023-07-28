package com.tcs.sgv.common.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class UpdateUserNameDAOImpl extends GenericDaoHibernateImpl
{
	Session ghibSession = null;
	public UpdateUserNameDAOImpl(Class type,SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public String getDataFromUserName(String lStrUserName) throws Exception
	{
		List lLstResData = null;
		List lLstFinalData = null;
		String lStrEmpName = "";
		String lStrFinalData = "N";
		Long lLngUserId = null;
		String lStrLocationName = "";
		StringBuilder lSBQuery = null;
		Query lQuery = null;
		
		try{
			lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT userId FROM OrgUserMst WHERE userName = :userName \n");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("userName", lStrUserName);
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lLngUserId = (Long) lLstResData.get(0);
				
				lSBQuery = new StringBuilder();
				lSBQuery.append("SELECT OEM.empFname || OEM.empMname || OEM.empLname, CLM.locName \n");
				lSBQuery.append("FROM OrgEmpMst OEM, OrgUserpostRlt OUPR, OrgPostMst OPM, CmnLocationMst CLM \n");
				lSBQuery.append("WHERE OEM.orgUserMst.userId = :userId AND OEM.orgUserMst.userId = OUPR.orgUserMst.userId \n");
				lSBQuery.append("AND OUPR.orgPostMstByPostId.postId = OPM.postId AND CLM.locationCode = OPM.locationCode \n");
				lSBQuery.append("AND OUPR.activateFlag = 1 AND OUPR.cmnLookupUserPostType.lookupId = 13 \n");
				lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("userId", lLngUserId);
				lLstFinalData = lQuery.list();
				
				Object[] lObj = (Object[]) lLstFinalData.get(0);
				lStrEmpName =  lObj[0].toString();
				lStrLocationName = lObj[1].toString();
					
				lStrFinalData = lStrEmpName + "#" + lStrLocationName;
			}else{
				lStrFinalData = "Invalid";
			}
		}catch (Exception e){
			logger.error(" Error in getDataFromUserName : " + e, e);
			throw e;
		}
		return lStrFinalData;
	}
	
	public String chkNewUserName(String lStrUserName) throws Exception
	{
		List lLstResData = null;
		String lStrResData = "";
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT userId FROM OrgUserMst WHERE userName = :userName \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("userName", lStrUserName);
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lStrResData = "Y";
			}else{
				lStrResData = "N";
			}
		}catch(Exception e){
			logger.error(" Error in chkNewUserName : " + e, e);
			throw e;
		}
		return lStrResData;
	}
	
	public void updateUserName(String lStrOldUserName, String lStrNewUserName) throws Exception
	{
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE ORG_USER_MST SET USER_NAME =:newUname, ACTIVATE_FLAG = 1 \n");
			lSBQuery.append("WHERE USER_NAME = :oldUname \n");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("newUname", lStrNewUserName);
			lQuery.setString("oldUname", lStrOldUserName);
			lQuery.executeUpdate();
		}catch(Exception e){
			logger.error(" Error in updateUserName : " + e, e);
			throw e;
		}
	}
}
