package com.tcs.sgv.dcps.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.ess.valueobject.OrgDesignationMst;

public class TerminationDtlsRqstsDAOImpl extends GenericDaoHibernateImpl implements
TerminationRqstsDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;
	
	
	public TerminationDtlsRqstsDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
		// TODO Auto-generated constructor stub
	}

	public List getEmployees(String treasuryCode,String user) throws Exception
	{
		 List resultList=null;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	
	        sb.append(" SELECT nvl(tr.TRANSACTION_ID,'-'),emp.EMP_NAME,emp.DCPS_ID,emp.DDO_CODE,off.OFF_NAME,tr.SEVARTH_ID FROM TERN_TERMINATION_DTLS tr ");
	        sb.append(" inner join MST_DCPS_EMP emp on emp.SEVARTH_ID=tr.SEVARTH_ID inner join MST_DCPS_DDO_OFFICE off on off.DCPS_DDO_OFFICE_MST_ID=CURR_OFF   ");
	        
	        if(user!=null && "TO".equalsIgnoreCase(user))
	        {
	        	sb.append(" where substr(tr.DDO_CODE,1,2)=substr("+treasuryCode+",1,2) ");
	        	 sb.append(" and tr.STATUS in (60003) ");
	        }
	        else
	        {
	        	sb.append(" where tr.STATUS in (60006) ");
	        }
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	        resultList = selectQuery.list();
	        
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return resultList;
		  
	}
	
	public List getEmployeeMissingCredits(String sevarthId) throws Exception
	{
		 List resultList=null;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	
	        sb.append(" SELECT case when c.MONTH_ID < 4 then cast(f.FIN_YEAR_CODE+1 as integer) else cast(f.FIN_YEAR_CODE as integer) end as CODE,m.MONTH_NAME,l.LOOKUP_NAME,c.CONTRIBUTION,v.VOUCHER_NO,to_char(v.VOUCHER_DATE,'dd-MM-yyyy') FROM TRN_DCPS_CONTRIBUTION c ");
	        sb.append(" inner join mst_dcps_emp emp on emp.DCPS_EMP_ID=c.DCPS_EMP_ID inner join MST_DCPS_CONTRI_VOUCHER_DTLS v on v.MST_DCPS_CONTRI_VOUCHER_DTLS=c.RLT_CONTRI_VOUCHER_ID  ");
	        sb.append(" inner join SGVA_MONTH_MST m on m.MONTH_ID=c.MONTH_ID and m.LANG_ID='en_US' inner join SGVC_FIN_YEAR_MST f on f.FIN_YEAR_ID=c.FIN_YEAR_ID inner join CMN_LOOKUP_MST l on l.LOOKUP_ID=c.TYPE_OF_PAYMENT ");
	        sb.append(" where c.STATUS in ('F','G') and emp.SEVARTH_ID='"+sevarthId+"' ");
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	        resultList = selectQuery.list();
	        
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return resultList;
		  
	}
	
	public List getEmployeeClosingBalance(String sevarthId) throws Exception
	{
		 List resultList=null;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	
	        sb.append(" SELECT CLOSE_NET FROM MST_DCPS_CONTRIBUTION_YEARLY y inner join MST_DCPS_EMP emp on emp.DCPS_ID=y.DCPS_ID ");
	        sb.append(" where  emp.SEVARTH_ID='"+sevarthId+"' order by y.YEAR_ID desc   ");
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	        resultList = selectQuery.list();
	        
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return resultList;
		  
	}
	public List getEmployeeLegacyOldAmount(String sevarthId) throws Exception
	{
		 List resultList=null;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	
	        sb.append("  SELECT sum(cast(EMP_CONTRI as double))+sum(cast(EMPLR_CONTRI as double)) FROM DCPS_LEGACY_DATA l inner join MST_DCPS_EMP emp on emp.DCPS_ID=l.DCPS_ID ");
	        sb.append(" where  emp.SEVARTH_ID='"+sevarthId+"'  ");
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	        resultList = selectQuery.list();
	        
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return resultList;
		  
	}

	@Override
	public Long getPk(String tranId) throws Exception 
	{
	
		List resultList=null;
		Long l=null;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	
	        sb.append("  SELECT TERMINATION_DTLS FROM TERN_TERMINATION_DTLS ");
	        sb.append(" where  TRANSACTION_ID='"+tranId+"'  ");
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	        resultList = selectQuery.list();
	        if(resultList!=null && resultList.size()>0 && resultList.get(0)!=null)
	        {
	        	l=Long.parseLong(resultList.get(0).toString());
	        }
	        
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return l;
	}
	
	
	}
