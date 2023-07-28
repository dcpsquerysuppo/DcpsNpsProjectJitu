package com.tcs.sgv.common.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import utils.IEncryption;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class ReSetPasswordDAOImpl extends GenericDaoHibernateImpl 
{
	Session ghibSession = null;
	public ReSetPasswordDAOImpl(Class type ,SessionFactory sessionFactory) 
	{
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public String getBirthDate(String lStrDdoCode) throws Exception
	{
		Date lObjDt = null;
		String lStrBdate = "";
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("ddMMyyyy");
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT OEM.empDob FROM OrgEmpMst OEM, OrgDdoMst ODM, OrgUserpostRlt OUPR \n");
			lSBQuery.append("WHERE ODM.ddoCode = :ddoCode AND ODM.postId = OUPR.orgPostMstByPostId.postId \n");
			lSBQuery.append("AND OUPR.orgUserMst.userId = OEM.orgUserMst.userId AND OUPR.activateFlag = 1 \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lObjDt = (Date) lQuery.list().get(0);
			if(lObjDt != null){
				lStrBdate = lObjSimpleDateFormat.format(lObjDt);
			}
		}catch (Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		
		return lStrBdate;
	}
	
	public Long getUserIdForDdo(String lStrDdoCode) throws Exception
	{
		Long lLngUserId = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT OUPR.orgUserMst.userId FROM OrgDdoMst ODM, OrgUserpostRlt OUPR \n");
			lSBQuery.append("WHERE ODM.ddoCode = :ddoCode AND ODM.postId = OUPR.orgPostMstByPostId.postId AND OUPR.activateFlag = 1 \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lLngUserId = (Long) lQuery.list().get(0);
		}catch (Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		
		return lLngUserId;
	}
	
	public void setPassword(Long lLngUserId, String lStrPasswd) throws Exception
	{
		IEncryption iencryption = null;
		String lStrEncrypt = "";
		
		try{
			Class class1 = Class.forName("md5.MD5Encryption");
			if (class1 != null)
				iencryption = (IEncryption) class1.newInstance();
			else
				iencryption = null;

			if (iencryption != null && lStrPasswd != null)
				lStrEncrypt = iencryption.crypt(lStrPasswd);
				
			if(!lStrEncrypt.equals("")){
				StringBuilder lSBQuery = new StringBuilder();
				lSBQuery.append("UPDATE OrgUserMst SET password = :passwd WHERE userId =:usrId \n");
				Query lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("passwd", lStrEncrypt);
				lQuery.setLong("usrId", lLngUserId);
				lQuery.executeUpdate();
			}
			
		}catch (Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
	}
	
	public String chkUserName(String lStrUserName) throws Exception
	{
		List lLstResData = null;
		List lLstFinalData = null;
		String lStrEmpName = "";
		String lStrFinalData = "N";
		Long lLngUserId = null;
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
				lSBQuery.append("SELECT orgPostMstByPostId.postId FROM OrgUserpostRlt WHERE orgUserMst.userId =:userId \n");
				lSBQuery.append("AND activateFlag = 1 \n");
				lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("userId", lLngUserId);
				lLstResData = lQuery.list();
				
				if(lLstResData.size() > 0){
					lSBQuery = new StringBuilder();
					lSBQuery.append("FROM RltDdoAsst WHERE asstPostId IN (:postId) OR ddoPostId IN (:postId) \n");
					lQuery = ghibSession.createQuery(lSBQuery.toString());
					lQuery.setParameterList("postId", lLstResData);
					lLstFinalData = lQuery.list();
					if(lLstFinalData.size() > 0){
						lSBQuery = new StringBuilder();
						lSBQuery.append("SELECT empFname || empMname || empLname FROM OrgEmpMst WHERE orgUserMst.userId = :userId \n");
						lQuery = ghibSession.createQuery(lSBQuery.toString());
						lQuery.setParameter("userId", lLngUserId);
						lStrEmpName = (String) lQuery.list().get(0);
						lStrFinalData = lStrEmpName + "#" + lLngUserId.toString();
					}else{
						lStrFinalData = "DCPS";
					}
				}
			}else{
				lStrFinalData = "Invalid";
			}
		}catch (Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lStrFinalData;
	}
}
