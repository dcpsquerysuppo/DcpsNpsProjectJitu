package com.tcs.sgv.common.dao;

import java.text.SimpleDateFormat;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class ChangeDCPSorGPFDAOImpl extends GenericDaoHibernateImpl
{
	Session ghibSession = null;
	public ChangeDCPSorGPFDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public String getDojFromSevaarth(String lStrSevaarthId) throws Exception
	{
		String lStrDoj = "";
		List lLstData = null;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT doj FROM MstEmp WHERE sevarthId =:sevarthId ");
			lSBQuery.append("AND regStatus IN (1,2) ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevarthId", lStrSevaarthId);
			lLstData = lQuery.list();
			
			if(lLstData.size() > 0){
				lStrDoj = lLstData.get(0).toString();
			}
		}catch(Exception e){
			logger.error("Error is: "+ e,e);
			throw e;
		}
		return lStrDoj;
	}
	
	public void updateDPvalue(String lStrSevaarthId, String lStrDpValue, String lStrDOJ) throws Exception
	{
		try{
			SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE MstEmp ");
			if(lStrDpValue.equals("GPF")){
				lSBQuery.append("SET regStatus = 2, dcpsOrGpf = 'N' ");
			}else if(lStrDpValue.equals("DCPS")){
				lSBQuery.append("SET regStatus = 1, dcpsOrGpf = 'Y' ");
			}
			lSBQuery.append(",doj=:DOJ WHERE sevarthId =:sevarthId ");			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevarthId", lStrSevaarthId);
			lQuery.setDate("DOJ", lObjSimpleDateFormat.parse(lStrDOJ));
			lQuery.executeUpdate();
		}catch(Exception e){
			logger.error("Error is: "+ e,e);
			throw e;
		}
	}
}
