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

public class TerminationDtlsDAOImpl extends GenericDaoHibernateImpl implements
TerminationDtlsDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;
	
	
	public TerminationDtlsDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
		// TODO Auto-generated constructor stub
	}

	public List checkIfEmployeeBelongsToDDO(String sevarthID ,String lStrDDOCode) throws Exception
	{
		 List resultList=null;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	        Date lDtCurrDate = SessionHelper.getCurDate();
	        sb.append(" SELECT reg_status,form_status FROM mst_dcps_emp where  DDO_CODE=:lStrDDOCode ");
	        if (!"".equals(sevarthID) && sevarthID != null) {
	            sb.append(" AND UPPER(SEVARTH_ID) = :lStrSevaarthId");
	        }
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	        if (!"".equals(sevarthID) && sevarthID != null) {
	            selectQuery.setParameter("lStrSevaarthId", (Object)sevarthID.trim());
	        }
	        selectQuery.setParameter("lStrDDOCode", (Object)lStrDDOCode.trim());
	         resultList = selectQuery.list();
	        
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return resultList;
		  
	}
	
	public List checkIfTerminationDone(String sevarthID) throws Exception
	{
		List resultList=null;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	        Date lDtCurrDate = SessionHelper.getCurDate();
	        sb.append(" SELECT emp.EMP_ID,to_char(emp.END_DATE,'yyyy-MM-dd'),dcps.EMP_NAME,dcps.SEVARTH_ID FROM HR_EIS_EMP_END_DATE emp inner join HR_EIS_EMP_MST mst on mst.EMP_ID=emp.EMP_ID inner join MST_DCPS_EMP dcps on dcps.ORG_EMP_MST_ID=mst.EMP_MPG_ID where ");
	        if (!"".equals(sevarthID) && sevarthID != null) {
	            sb.append(" UPPER(dcps.SEVARTH_ID) = :lStrSevaarthId");
	        }
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	        if (!"".equals(sevarthID) && sevarthID != null) {
	            selectQuery.setParameter("lStrSevaarthId", (Object)sevarthID.trim());
	        }
	        
	         resultList = selectQuery.list();
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return resultList;
	}
	
	public List getEmpDetails(String sevarthID,String tranId) throws Exception
	{
		List resultList=null;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	        Date lDtCurrDate = SessionHelper.getCurDate();
	        sb.append(" SELECT dcps.EMP_NAME,dcps.SEVARTH_ID,dcps.DCPS_ID,dcps.PRAN_NO,to_char(dcps.DOJ,'dd-MM-yyyy'),d.DSGN_NAME,to_char(emp.END_DATE,'dd-MM-yyyy'),l.LOOKUP_NAME, ");
	        sb.append(" dcps.ADDRESS_BUILDING ||','|| dcps.ADDRESS_STREET ||','|| dcps.LANDMARK || ',' || dcps.DISTRICT || ',' || dcps.PINCODE as emp_add,s1.FORM_S1_ID, ");
	        sb.append(" s1.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||','|| s1.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||','|| s1.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA || ',' || s1.PRESENT_ADDRESS_DISTRICT_TOWN_CITY || ',' || s1.PRESENT_ADDRESS_STATE_UNION_TERRITORY || ',' || s1.PRESENT_ADDRESS_PIN_CODE as s1_add,tern.TERMINATION_DTLS,l.LOOKUP_ID from ");
	        sb.append(" MST_DCPS_EMP dcps  inner join ORG_DESIGNATION_MST d on d.DSGN_ID=dcps.DESIGNATION ");
	        sb.append(" inner join HR_EIS_EMP_MST mst on mst.emp_mpg_id=dcps.ORG_EMP_MST_ID");
	        sb.append(" inner join HR_EIS_EMP_END_DATE emp on emp.EMP_ID=mst.EMP_ID ");
	        sb.append(" inner join CMN_LOOKUP_MST l on l.LOOKUP_ID=emp.REASON ");
	        sb.append(" left outer join FRM_FORM_S1_DTLS s1 on s1.SEVARTH_ID=dcps.SEVARTH_ID ");
	        sb.append(" left outer join TERN_TERMINATION_DTLS tern on tern.SEVARTH_ID=dcps.SEVARTH_ID and tern.STATUS not in (60009) ");
	        if (!"".equals(sevarthID) && sevarthID != null) {
	            sb.append(" where UPPER(dcps.SEVARTH_ID) = '"+sevarthID+"' ");
	        }
	        else  if (!"".equals(tranId) && tranId != null) 
	        {
	        	 sb.append(" where tern.TRANSACTION_ID = '"+tranId+"' ");
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
	public List getEmpDetailsForFormA(String sevarthID) throws Exception
	{
		List resultList=null;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	        Date lDtCurrDate = SessionHelper.getCurDate();
	        sb.append(" SELECT dcps.SEVARTH_ID,to_char(emp.END_DATE,'dd-MM-yyyy'),l.LOOKUP_ID, ");
	        sb.append(" dcps.ADDRESS_BUILDING ||','|| dcps.ADDRESS_STREET ||','|| dcps.LANDMARK || ',' || dcps.DISTRICT || ',' || dcps.PINCODE as emp_add,s1.FORM_S1_ID, ");
	        sb.append(" s1.PRESENT_ADD_FLAT_UNIT_NO_BLOCK_NO ||','|| s1.PRESENT_ADDRESS_NAME_OF_PREMISE_BUILDING_VILLAGE ||','|| s1.PRESENT_ADDRESS_AREA_LOCALITY_TALUKA || ',' || s1.PRESENT_ADDRESS_DISTRICT_TOWN_CITY || ',' || s1.PRESENT_ADDRESS_STATE_UNION_TERRITORY || ',' || s1.PRESENT_ADDRESS_PIN_CODE as s1_add ,d.DSGN_ID from ");
	        sb.append(" MST_DCPS_EMP dcps  inner join ORG_DESIGNATION_MST d on d.DSGN_ID=dcps.DESIGNATION ");
	        sb.append(" inner join HR_EIS_EMP_MST mst on mst.emp_mpg_id=dcps.ORG_EMP_MST_ID");
	        sb.append(" inner join HR_EIS_EMP_END_DATE emp on emp.EMP_ID=mst.EMP_ID ");
	        sb.append(" inner join CMN_LOOKUP_MST l on l.LOOKUP_ID=emp.REASON ");
	        sb.append(" left outer join FRM_FORM_S1_DTLS s1 on s1.SEVARTH_ID=dcps.SEVARTH_ID ");
	       
	        if (!"".equals(sevarthID) && sevarthID != null) {
	            sb.append(" where UPPER(dcps.SEVARTH_ID) = '"+sevarthID+"' ");
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
	
	public List getEmpPayDetails(String sevarthID) throws Exception
	{
		List resultList=null;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	        Date lDtCurrDate = SessionHelper.getCurDate();
	        sb.append(" SELECT paybill.PAYBILL_MONTH,mon.MONTH_NAME,paybill.PAYBILL_YEAR ,mpg.VOUCHER_NO,to_char(mpg.VOUCHER_DATE,'dd-MM-yyyy') FROM HR_PAY_PAYBILL paybill ");
	        sb.append(" inner join PAYBILL_HEAD_MPG mpg on mpg.paybill_id =paybill.paybill_grp_id  and mpg.approve_flag in (1,5) inner join SGVA_MONTH_MST mon on mon.MONTH_ID=paybill.PAYBILL_MONTH and mon.LANG_ID='en_US' inner join  ");
	        sb.append("  (select temp.emp_id as emp1,temp.paybill_year as year1,max(paybill1.paybill_month) as month1  from HR_PAY_PAYBILL paybill1 inner join ");
	        sb.append(" (select paybill.EMP_ID as EMP_ID,max(paybill.paybill_year) as PAYBILL_YEAR from paybill_head_mpg mpg ");
	        sb.append(" inner join HR_PAY_PAYBILL paybill on mpg.paybill_id =paybill.paybill_grp_id  inner join hr_eis_emp_mst eis on eis.EMP_ID=paybill.EMP_ID ");
	        sb.append(" inner join mst_dcps_emp emp on emp.ORG_EMP_MST_ID =eis.EMP_MPG_ID where mpg.approve_flag in (1,5) and emp.SEVARTH_ID='"+sevarthID+"' ");
	        sb.append(" group by paybill.EMP_ID) temp on temp.emp_id=paybill1.EMP_ID and temp.paybill_year=paybill1.paybill_year  ");
	        sb.append(" group by temp.emp_id,temp.paybill_year) temp1 on temp1.emp1=paybill.EMP_ID and temp1.year1=paybill.PAYBILL_YEAR and temp1.month1=paybill.PAYBILL_MONTH   ");
	       
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	       
	        
	         resultList = selectQuery.list();
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return resultList;
	}
	
	
	public List getEmpSavedDetails(String sevarthID) throws Exception
	{
		List resultList=null;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	        Date lDtCurrDate = SessionHelper.getCurDate();
	        sb.append(" SELECT to_char(d.TERMINATION_DATE,'dd-MM-yyyy'),l.LOOKUP_NAME,d.CUR_ADDRESS,mst.DSGN_NAME,d.REASON_OF_TERMINATION,mst.DSGN_ID,d.TERMINATION_DTLS,d.STATUS,l.LOOKUP_ID FROM TERN_TERMINATION_DTLS d  ");
	        sb.append(" inner join CMN_LOOKUP_MST l on l.LOOKUP_ID=d.REASON_OF_TERMINATION inner join ORG_DESIGNATION_MST mst on mst.DSGN_ID=d.DESIGNATION where   ");
	        sb.append("  d.SEVARTH_ID='"+sevarthID+"' and d.status not in (60009) ");
	       
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	       
	        
	         resultList = selectQuery.list();
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return resultList;
	}
	public String getEmployeesPPAN(String sevarthID) throws Exception
	{
		List resultList=null;
		String pPan="";
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	        Date lDtCurrDate = SessionHelper.getCurDate();
	        sb.append(" SELECT PPAN FROM ");
	        sb.append(" MST_DCPS_EMP  where   ");
	        sb.append(" SEVARTH_ID='"+sevarthID+"' ");
	       
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	       
	        
	         resultList = selectQuery.list();
	         if(resultList!=null && resultList.size()>0)
	         {
	        	 pPan=resultList.get(0).toString();
	         }
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return pPan;
	}
	
	public List getDesignationList(long langId, long locId) {
		Session hibSession = getSession();
		StringBuffer query1 = new StringBuffer(
		"from OrgDesignationMst as orgDegnMst where orgDegnMst.cmnLanguageMst.langId=");
		query1.append(langId);
		query1
		.append(" and dsgnId in (select pd.orgDesignationMst.dsgnId from OrgPostDetailsRlt as pd where pd.cmnLocationMst.locId=");
		query1.append(locId);
		query1.append(")");
		logger.info("Query for Designation in Bulk Allowances "
				+ query1.toString());
		Query sqlQuery1 = hibSession.createQuery(query1.toString());
		List<OrgDesignationMst> dsgnList = sqlQuery1.list();
		return dsgnList;
	}
	
	
	public List getEmployeesForFormC(String ddoCode) throws Exception
	{
		 List resultList=null;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	
	        sb.append(" SELECT nvl(tr.TRANSACTION_ID,'-'),emp.EMP_NAME,emp.DCPS_ID,emp.DDO_CODE,off.OFF_NAME,tr.SEVARTH_ID FROM TERN_TERMINATION_DTLS tr ");
	        sb.append(" inner join MST_DCPS_EMP emp on emp.SEVARTH_ID=tr.SEVARTH_ID inner join MST_DCPS_DDO_OFFICE off on off.DCPS_DDO_OFFICE_MST_ID=CURR_OFF where  ");
	        sb.append(" tr.DDO_CODE="+ddoCode+" and tr.STATUS in (60004) ");
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	        resultList = selectQuery.list();
	        
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return resultList;
		  
	}
	
	public List getAlreadySavedData(String sevarthId) throws Exception
	{
		 List resultList=null;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	
	        sb.append(" SELECT tr.STATUS,tr.SEVARTH_ID FROM TERN_TERMINATION_DTLS tr ");
	        sb.append(" where tr.SEVARTH_ID='"+sevarthId+"' and  tr.status not in (60009) ");
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	        resultList = selectQuery.list();
	        
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return resultList;
		  
	}
	
	public List getEmployeesInRejected(String ddoCode) throws Exception
	{
		 List resultList=null;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	
	        sb.append(" SELECT nvl(tr.TRANSACTION_ID,'-'),emp.EMP_NAME,emp.DCPS_ID,emp.DDO_CODE,off.OFF_NAME,tr.SEVARTH_ID,tr.REASON_OF_TO_REJECT,case when tr.STATUS=60007 then 'SRKA' else 'Treasury' end ,tr.TERMINATION_DTLS FROM TERN_TERMINATION_DTLS tr ");
	        sb.append(" inner join MST_DCPS_EMP emp on emp.SEVARTH_ID=tr.SEVARTH_ID inner join MST_DCPS_DDO_OFFICE off on off.DCPS_DDO_OFFICE_MST_ID=CURR_OFF   ");
	        sb.append(" where tr.DDO_CODE="+ddoCode+" ");
	        sb.append(" and tr.STATUS in (60007,60005) ");
	       
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	        resultList = selectQuery.list();
	        
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return resultList;
		  
	}
	public int getLatestOrderNo(Long termId) throws Exception
	{
		 List resultList=null;
		 int orderNo=0;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	
	        sb.append("  SELECT max(ORDER_NO)+1 FROM TERN_TERMINATION_DTLS_HST where TERMINATION_DTLS="+termId );
	       
	       
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	        resultList = selectQuery.list();
	        if(resultList!=null && resultList.size()>0 && resultList.get(0)!=null )
	        {
	        	orderNo=Integer.parseInt(resultList.get(0).toString());
	        }
	        
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return orderNo;
		  
	}
	public String getTranId(String tranId) throws Exception
	{
		 List resultList=null;
		 String transacId=tranId+"000001";
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	
	        sb.append("  SELECT max(cast(TRANSACTION_ID as bigint))+1 FROM TERN_TERMINATION_DTLS where TRANSACTION_ID like '" + tranId + "%' " );
	       
	       
	        selectQuery = this.ghibSession.createSQLQuery(sb.toString());
	        resultList = selectQuery.list();
	        if(resultList!=null && resultList.size()>0 && resultList.get(0)!=null )
	        {
	        	transacId=resultList.get(0).toString();
	        }
	        
		}
		catch (Exception e) {
            this.gLogger.error((Object)("Error is :" + e), (Throwable)e);
            throw e;
        }
		return transacId;
		  
	}

	public List getReportData(String sevarthID) throws Exception
	{
		List resultList=null;
		try
		{
			StringBuilder sb = new StringBuilder();
	        SQLQuery selectQuery = null;
	        Date lDtCurrDate = SessionHelper.getCurDate();
	        sb.append(" SELECT d.TRANSACTION_ID,emp.EMP_NAME,emp.DCPS_ID,emp.DDO_CODE,off.OFF_NAME,dsgn.DSGN_NAME,to_char(d.TERMINATION_DATE,'dd-MM-yyyy'),l.lookup_name,d.sevarth_id,d.status FROM  ");
	        sb.append(" TERN_TERMINATION_DTLS d inner join MST_DCPS_EMP emp on emp.SEVARTH_ID=d.SEVARTH_ID");
	        sb.append(" inner join MST_DCPS_DDO_OFFICE off on off.DCPS_DDO_OFFICE_MST_ID=emp.CURR_OFF ");
	        sb.append(" inner join ORG_DESIGNATION_MST dsgn on dsgn.DSGN_ID=d.DESIGNATION ");
	        sb.append(" inner join CMN_LOOKUP_MST l on l.lookup_id=d.REASON_OF_TERMINATION where d.STATUS not in (60009) ");
	           if (!"".equals(sevarthID) && sevarthID != null) {
	            sb.append(" and UPPER(d.SEVARTH_ID) = '"+sevarthID+"' ");
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

	
	

	}
