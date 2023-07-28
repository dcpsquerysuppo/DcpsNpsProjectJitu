package com.tcs.sgv.common.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class GetLoginPageDetailsDAOImpl extends GenericDaoHibernateImpl
{
	Session ghibSession = null;
	public GetLoginPageDetailsDAOImpl(Class type ,SessionFactory sessionFactory) 
	{
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public Long getTotalDdo() throws Exception
	{
		Long lLngDdoCount = null;
		List lLstResData = null;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT COUNT(distinct DDO_CODE) FROM ORG_DDO_MST WHERE ACTIVATE_FLAG = 1");			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lLngDdoCount = Long.parseLong(lLstResData.get(0).toString());
			}
		}catch(Exception e){
			logger.error("Error is: "+ e);
			throw e;
		}
		return lLngDdoCount;
	}
	
	public Long getTotalEmployee() throws Exception
	{
		Long lLngEmpCount = null;
		List lLstResData = null;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT COUNT(DISTINCT DCPS_EMP_ID) FROM MST_DCPS_EMP ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lLngEmpCount = Long.parseLong(lLstResData.get(0).toString());
			}
		}catch(Exception e){
			logger.error("Error is: "+ e);
			throw e;
		}
		return lLngEmpCount;
	}
	
	public Long getTotalBillCount() throws Exception
	{
		List lLstResData = null;
		Long lLngBillCnt = null;
		Calendar lObjCalendar = Calendar.getInstance();		
		Integer lIntYear = lObjCalendar.get(lObjCalendar.YEAR);
		Integer lIntCurMonth = lObjCalendar.get(lObjCalendar.MONTH) + 1;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("select sum(inn.Bill_Count) from(SELECT dist.DISTRICT_NAME,ddo.DDO_NAME,count(bill.DESCRIPTION) as Bill_Count \n");
			lSBQuery.append("FROM PAYBILL_HEAD_MPG mpg ,MST_DCPS_BILL_GROUP bill ,ORG_DDO_MST ddo ,CMN_DISTRICT_MST dist,CMN_LOCATION_MST loc \n");
			lSBQuery.append("where bill.BILL_GROUP_ID = mpg.BILL_NO   and  bill.DDO_CODE = ddo.DDO_CODE and ddo.LOCATION_CODE = loc.LOC_ID \n");
			lSBQuery.append("and loc.LOC_DISTRICT_ID = dist.DISTRICT_ID and mpg.PAYBILL_YEAR ="+ lIntYear +" and  mpg.PAYBILL_MONTH ="+ lIntCurMonth+" \n");
			lSBQuery.append("and  mpg.APPROVE_FLAG in (0,1) group by dist.DISTRICT_NAME,ddo.DDO_NAME order by dist.DISTRICT_NAME, ddo.DDO_NAME) inn \n");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lLngBillCnt = Long.parseLong(lLstResData.get(0).toString());
			}
		}catch(Exception e){
			logger.error("Error is: "+ e);
			throw e;
		}
		return lLngBillCnt;
	}
	
	public Long getTotalEmpInBill()throws Exception
	{
		List lLstResData = null;
		Long lLngEmpInBillCnt = null;
		Calendar lObjCalendar = Calendar.getInstance();		
		Integer lIntYear = lObjCalendar.get(lObjCalendar.YEAR);
		Integer lIntCurMonth = lObjCalendar.get(lObjCalendar.MONTH) + 1;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT count(distinct emp_id) FROM hr_pay_paybill where PAYBILL_GRP_ID in ( \n");
			lSBQuery.append("SELECT distinct head.PAYBILL_ID FROM MST_DCPS_BILL_GROUP mst , PAYBILL_HEAD_MPG head where mst.BILL_GROUP_ID = head.BILL_NO \n");
			lSBQuery.append("and head.APPROVE_FLAG in (0,1) and PAYBILL_MONTH = "+ lIntCurMonth +" and PAYBILL_YEAR = "+ lIntYear +") ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lLngEmpInBillCnt = Long.parseLong(lLstResData.get(0).toString());
			}
		}catch(Exception e){
			logger.error("Error is: "+ e);
			throw e;
		}
		return lLngEmpInBillCnt;
	}
	
	public String getMonthDesc(Integer lIntMonthId) throws Exception
	{
		String lStrMonthDesc = "";
		List lLstResData = null;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT MONTH_NAME FROM SGVA_MONTH_MST WHERE MONTH_NO ="+ lIntMonthId +" \n");
			lSBQuery.append("AND LANG_ID = 'en_US' \n");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());			
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lStrMonthDesc = (String) lLstResData.get(0);
			}
		}catch(Exception e){
			logger.error("Error is: "+ e);
			throw e;
		}
		return lStrMonthDesc;
	}
	
	public Long getLoggedInEmp() throws Exception
	{
		List lLstResData = null;
		Long lLngLoggedInEmp = null;
		Date lobjDate = new Date();
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT COUNT(*) FROM FRM_LOGIN_AUDIT where LOGIN_DATE_TIME > :curDate and LOGOUT_TYPE is null and LOGIN_STATUS = 142 ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setDate("curDate", lobjDate);
			
			lLstResData = lQuery.list();			
			
			if(lLstResData.size() > 0){
				lLngLoggedInEmp = Long.parseLong(lLstResData.get(0).toString());
			}
		}catch(Exception e){
			logger.error("Error is: "+ e);
			throw e;
		}
		return lLngLoggedInEmp;
	}
}
