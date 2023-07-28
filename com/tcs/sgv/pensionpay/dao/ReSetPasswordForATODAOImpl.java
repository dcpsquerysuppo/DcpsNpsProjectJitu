package com.tcs.sgv.pensionpay.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class ReSetPasswordForATODAOImpl extends GenericDaoHibernateImpl
{
	Session ghibSession = null;
	public ReSetPasswordForATODAOImpl(Class type ,SessionFactory sessionFactory) 
	{
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public String chkUserName(String lStrUserName, String lStrLocCode) throws Exception
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
					lSBQuery.append("SELECT locationCode FROM OrgPostMst WHERE postId IN (:postId)\n");
					lQuery = ghibSession.createQuery(lSBQuery.toString());
					lQuery.setParameterList("postId", lLstResData);
					lLstFinalData = lQuery.list();
					if(lLstFinalData.contains(lStrLocCode)){
						lSBQuery = new StringBuilder();
						lSBQuery.append("SELECT empFname || empMname || empLname FROM OrgEmpMst WHERE orgUserMst.userId = :userId \n");
						lQuery = ghibSession.createQuery(lSBQuery.toString());
						lQuery.setParameter("userId", lLngUserId);
						lStrEmpName = (String) lQuery.list().get(0);
						lStrFinalData = lStrEmpName + "#" + lLngUserId.toString();
					}else{
						lStrFinalData = "location";
					}
				}
			}else{
				lStrFinalData = "Invalid";
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		
		return lStrFinalData;
	}
	
	public String getBirthDate(Long lLngUserId) throws Exception
	{
		Date lObjDt = null;
		String lStrBdate = "";
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("ddMMyyyy");
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT empDob FROM OrgEmpMst WHERE orgUserMst.userId =:userId \n");			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("userId", lLngUserId);
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
}
