package com.tcs.sgv.dcps.dao;

import java.text.SimpleDateFormat;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class GroupWiseEmpReportDAOImpl extends GenericDaoHibernateImpl implements GroupWiseEmpReportDAO
{
	Session ghibSession = null;
	public GroupWiseEmpReportDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List getEmpCountForYear(String lStrFinYear)throws Exception
	{
		List lLstEmpCount = null;		
		String []lArrFromToDate = null;
		String lStrFromToDate = "";
		String lStrFromdate = "";
		
		SimpleDateFormat lObjSimpleDate = new SimpleDateFormat("dd/MM/yyyy");
		
		try{
			lStrFromToDate = getFromToDate(Long.parseLong(lStrFinYear));
			lArrFromToDate = lStrFromToDate.split(",");
			lStrFromdate = lArrFromToDate[0];
			
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT group,COUNT(*) FROM MstEmp WHERE doj >= :doj ");
			lSBQuery.append("AND group IN ('A','B','BnGz','C','D') AND group is not null group by group ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());			
			lQuery.setParameter("doj", lObjSimpleDate.parse(lStrFromdate));
			lLstEmpCount = lQuery.list();
		}catch(Exception e){
			logger.error("Error is :" + e, e);
		}
		
		return lLstEmpCount;
	}
	
	public String getFromToDate(Long lLngFinYearId)
	{
		List lLstResData = null;
		SimpleDateFormat lObjSimpleDate = new SimpleDateFormat("dd/MM/yyyy");
		String lStrFromDate = "";
		String lStrToDate = "";
		String lStrResData = "";
		Object lObj[] = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT fromDate, toDate FROM SgvcFinYearMst ");
			lSBQuery.append("WHERE finYearId = :finYearId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("finYearId", lLngFinYearId);
			lLstResData = lQuery.list();
			
			if(lLstResData != null && lLstResData.size() > 0){
				lObj = (Object[]) lLstResData.get(0);
				lStrFromDate = lObjSimpleDate.format(lObj[0]);
				lStrToDate = lObjSimpleDate.format(lObj[1]);
			}
			
			lStrResData = lStrFromDate+","+lStrToDate;
		}catch(Exception e){
			logger.error("Exception in getFromToDate:" + e, e);
		}
		
		return lStrResData;
	}
}
