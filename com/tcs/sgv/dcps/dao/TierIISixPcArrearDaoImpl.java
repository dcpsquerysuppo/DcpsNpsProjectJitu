package com.tcs.sgv.dcps.dao;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.sql.Connection;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.pensionproc.dao.PensionProcComparators;

public class TierIISixPcArrearDaoImpl extends GenericDaoHibernateImpl implements
		TierIISixPcArrearDao {

	private final Log gLogger;
	org.hibernate.Session ghibSession;
	
	public TierIISixPcArrearDaoImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		this.gLogger = LogFactory.getLog(this.getClass());
		this.ghibSession = null;
		this.ghibSession = sessionFactory.getCurrentSession();
		this.setSessionFactory(sessionFactory);
	}

	public List getEmpListForFiveInstApprove(String strDDOCode,
			String BillNo,String strRoleId,String interestApproved,String searchSeva,String searchEmp) {

		List lLstEmpFiveInst = null;
		// List lLstEmp = null;
		StringBuilder lSBQuery = null;
		// StringBuilder lSBQuery1 = null;
		// String postId;
		// postId = gStrLocationCode;
		logger.info("strRoleId**********"+ strRoleId);
		System.out.println("role Id--->"+strRoleId);
		if (strRoleId.equals("700008")) {
			try {
				System.out.println("inside method");
				lSBQuery = new StringBuilder();

				lSBQuery.append(" SELECT diSTINCT t.DCPS_EMP_ID,t8.fin_year_id,cast(t8.yearly_amount as varchar) as TimeA,t2.fin_year_id,cast(t2.yearly_amount as varchar) as TimeB,t3.fin_year_id,cast(t3.yearly_amount as varchar) as TimeC,"
						+ "t4.fin_year_id,cast(t4.yearly_amount as varchar) as TimeD,t5.fin_year_id,cast(t5.yearly_amount as varchar) as TimeE ,empmst.EMP_NAME,empmst.SEVARTH_ID,empmst.DCPS_ID,case when empmst.PRAN_NO is null then 'N/A' else empmst.PRAN_NO end ,t8.interest, case when t8.bill_no is null then 'N' else 'Y' end ");
				lSBQuery.append(" FROM RLT_DCPS_SIXPC_YEARLY t ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t8 ON t.DCPS_EMP_ID=t8.DCPS_EMP_ID AND t8.fin_year_id=22 and  t8.status_flag='A' and t8.state_flag='2' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t2 ON t.DCPS_EMP_ID=t2.DCPS_EMP_ID AND t2.fin_year_id=23 and  t2.status_flag='A' and t2.state_flag='2' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t3 ON t.DCPS_EMP_ID=t3.DCPS_EMP_ID AND t3.fin_year_id=24 and  t3.status_flag='A' and t3.state_flag='2' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t4 ON t.DCPS_EMP_ID=t4.DCPS_EMP_ID AND t4.fin_year_id=25 and  t4.status_flag='A' and t4.state_flag='2' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t5 ON t.DCPS_EMP_ID=t5.DCPS_EMP_ID AND t5.fin_year_id=26 and  t5.status_flag='A' and t5.state_flag='2' ");
				lSBQuery.append(" inner join MST_DCPS_EMP empmst ");
				lSBQuery.append(" on t.dcps_emp_id=empmst.dcps_emp_id ");
				//lSBQuery.append(" where t.status_flag='A' and t.ddo_code='" + strDDOCode + "' and t.state_flag='2' ");
				lSBQuery.append(" where t.status_flag='A' and t.DCPS_EMP_ID in(select dcps_emp_id from MST_DCPS_EMP where DDO_CODE ='" + strDDOCode + "' and dcps_or_gpf='Y' and reg_status=1 and (DOJ >='2005-11-01' and DOJ <='2009-03-31') and SUPER_ANN_DATE >'2021-01-31' ) and t.state_flag='2' ");
				lSBQuery.append(" ORDER BY t.DCPS_EMP_ID ASC ");
				logger.info("Query to DDO lLstEmpFiveInst in  heaqder**********"+ lSBQuery.toString());
				System.out.println("role Id--->"+lSBQuery.toString());

				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				logger.info("Query to DDO lLstEmpFiveInst in  heaqder**********"+ lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		}
		else if (strRoleId.equals("700002")) {
			try {
				System.out.println("inside method");
				lSBQuery = new StringBuilder();

				lSBQuery.append(" SELECT diSTINCT t.DCPS_EMP_ID,t8.fin_year_id,cast(t8.yearly_amount as varchar) as TimeA,t2.fin_year_id,cast(t2.yearly_amount as varchar) as TimeB,t3.fin_year_id,cast(t3.yearly_amount as varchar) as TimeC,"
						+ "t4.fin_year_id,cast(t4.yearly_amount as varchar) as TimeD,t5.fin_year_id,cast(t5.yearly_amount as varchar) as TimeE ,empmst.EMP_NAME,empmst.SEVARTH_ID,empmst.DCPS_ID,case when empmst.PRAN_NO is null then 'N/A' else empmst.PRAN_NO end ,t8.interest,case when t8.reason is null then '.' else  t8.reason end, case when t8.bill_no is null then 'N' else 'Y' end ");
				lSBQuery.append(" FROM RLT_DCPS_SIXPC_YEARLY t ");
				if(interestApproved.equals("Y"))
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t8 ON t.DCPS_EMP_ID=t8.DCPS_EMP_ID AND t8.fin_year_id=22 and  t8.status_flag='A' ");//and t8.BILL_NO is null
				else
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t8 ON t.DCPS_EMP_ID=t8.DCPS_EMP_ID AND t8.fin_year_id=22 and  t8.status_flag='A' and t8.BILL_NO is null ");//and t8.BILL_NO is null
				
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t2 ON t.DCPS_EMP_ID=t2.DCPS_EMP_ID AND t2.fin_year_id=23 and  t2.status_flag='A' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t3 ON t.DCPS_EMP_ID=t3.DCPS_EMP_ID AND t3.fin_year_id=24 and  t3.status_flag='A' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t4 ON t.DCPS_EMP_ID=t4.DCPS_EMP_ID AND t4.fin_year_id=25 and  t4.status_flag='A' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t5 ON t.DCPS_EMP_ID=t5.DCPS_EMP_ID AND t5.fin_year_id=26 and  t5.status_flag='A' ");
				lSBQuery.append(" inner join MST_DCPS_EMP empmst ");
				lSBQuery.append(" on t.dcps_emp_id=empmst.dcps_emp_id ");
				
				if(!searchSeva.equals("")){
					lSBQuery.append(" where t.status_flag='A' and t.DCPS_EMP_ID in(select dcps_emp_id from MST_DCPS_EMP where DDO_CODE ='" + strDDOCode + "' and SEVARTH_ID='" + searchSeva + "' and dcps_or_gpf='Y' and reg_status=1 and (DOJ >='2005-11-01' and DOJ <='2009-03-31') and SUPER_ANN_DATE >'2021-01-31' ) and ((t.state_flag in ('0','1','2')) or (t.state_flag is null))  ORDER BY t.DCPS_EMP_ID ASC ");
				}else if(!searchEmp.equals("")){
					lSBQuery.append(" where t.status_flag='A' and t.DCPS_EMP_ID in(select dcps_emp_id from MST_DCPS_EMP where DDO_CODE ='" + strDDOCode + "' and emp_name like '%" + searchEmp + "%' and dcps_or_gpf='Y' and (DOJ >='2005-11-01' and DOJ <='2009-03-31') and reg_status=1 and SUPER_ANN_DATE >'2021-01-31' ) and ((t.state_flag in ('0','1','2')) or (t.state_flag is null)) ORDER BY t.DCPS_EMP_ID ASC  ");
				}else{
					lSBQuery.append(" where t.status_flag='A' and t.DCPS_EMP_ID in(select dcps_emp_id from MST_DCPS_EMP where DDO_CODE ='" + strDDOCode + "' and (DOJ >='2005-11-01' and DOJ <='2009-03-31') and dcps_or_gpf='Y' and reg_status=1 and SUPER_ANN_DATE >'2021-01-31' ) and ((t.state_flag in ('0','1','2')) or (t.state_flag is null)) ORDER BY t8.interest asc ");	
				}
				
				//lSBQuery.append(" ORDER BY t.DCPS_EMP_ID ASC ");
				logger.info("Query to DDO lLstEmpFiveInst in  heaqder**********"+ lSBQuery.toString());
				System.out.println("role Id--->"+lSBQuery.toString());

				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				logger.info("Query to DDO lLstEmpFiveInst in  heaqder**********"+ lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		} else if(strRoleId.equals("700003")) {
			try {

				System.out.println("inside method");
				lSBQuery = new StringBuilder();

				lSBQuery.append(" SELECT diSTINCT t.DCPS_EMP_ID,t8.fin_year_id,cast(t8.yearly_amount as varchar) as TimeA,t2.fin_year_id,cast(t2.yearly_amount as varchar) as TimeB,t3.fin_year_id,cast(t3.yearly_amount as varchar) as TimeC,"
						+ "t4.fin_year_id,cast(t4.yearly_amount as varchar) as TimeD,t5.fin_year_id,cast(t5.yearly_amount as varchar) as TimeE ,empmst.EMP_NAME,empmst.SEVARTH_ID,empmst.DCPS_ID,case when empmst.PRAN_NO is null then 'N/A' else empmst.PRAN_NO end ,t8.interest, case when t8.bill_no is null then 'N' else 'Y' end ");
				lSBQuery.append(" FROM RLT_DCPS_SIXPC_YEARLY t ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t8 ON t.DCPS_EMP_ID=t8.DCPS_EMP_ID AND t8.fin_year_id=22 and  t8.status_flag='A' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t2 ON t.DCPS_EMP_ID=t2.DCPS_EMP_ID AND t2.fin_year_id=23 and  t2.status_flag='A' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t3 ON t.DCPS_EMP_ID=t3.DCPS_EMP_ID AND t3.fin_year_id=24 and  t3.status_flag='A' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t4 ON t.DCPS_EMP_ID=t4.DCPS_EMP_ID AND t4.fin_year_id=25 and  t4.status_flag='A' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t5 ON t.DCPS_EMP_ID=t5.DCPS_EMP_ID AND t5.fin_year_id=26 and  t5.status_flag='A' ");
				lSBQuery.append(" inner join MST_DCPS_EMP empmst ");
				lSBQuery.append(" on t.dcps_emp_id=empmst.dcps_emp_id ");
				lSBQuery.append(" where t.status_flag='A' and t.DCPS_EMP_ID in(select dcps_emp_id from MST_DCPS_EMP where DDO_CODE ='" + strDDOCode + "' and dcps_or_gpf='Y' and reg_status=1 and (DOJ >='2005-11-01' and DOJ <='2009-03-31') and SUPER_ANN_DATE >'2021-01-31') and t.state_flag='1' and t.BILL_NO='"+BillNo+"'");
				
				lSBQuery.append(" ORDER BY t.DCPS_EMP_ID ASC ");
				logger.info("Query to DDO lLstEmpFiveInst in  heaqder**********"+ lSBQuery.toString());
				System.out.println("role Id--->"+lSBQuery.toString());

				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				logger.info("Query to DDO lLstEmpFiveInst in  heaqder**********"+ lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			
				
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}

		}else if(strRoleId.equals("700004")) {
			try {
				lSBQuery = new StringBuilder();

				lSBQuery.append(" SELECT diSTINCT t.DCPS_EMP_ID,t8.fin_year_id,t8.yearly_amount AS TimeA,t2.fin_year_id,t2.yearly_amount AS TimeB,t3.fin_year_id,t3.yearly_amount AS TimeC,"
						+ "t4.fin_year_id,t4.yearly_amount AS TimeD,t5.fin_year_id,t5.yearly_amount AS TimeE ,empmst.EMP_NAME,empmst.SEVARTH_ID,empmst.DCPS_ID ,case when t8.bill_no is null then 'N' else 'Y' end ");
				lSBQuery.append(" FROM RLT_DCPS_SIXPC_YEARLY t ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t8 ON t.DCPS_EMP_ID=t8.DCPS_EMP_ID AND t8.fin_year_id=22 ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t2 ON t.DCPS_EMP_ID=t2.DCPS_EMP_ID AND t2.fin_year_id=23 ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t3 ON t.DCPS_EMP_ID=t3.DCPS_EMP_ID AND t3.fin_year_id=24 ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t4 ON t.DCPS_EMP_ID=t4.DCPS_EMP_ID AND t4.fin_year_id=25 ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t5 ON t.DCPS_EMP_ID=t5.DCPS_EMP_ID AND t5.fin_year_id=26 ");
				lSBQuery.append(" inner join MST_DCPS_EMP empmst ");
				lSBQuery.append(" on t.dcps_emp_id=empmst.dcps_emp_id ");
				lSBQuery.append(" where t.state_flag='2' and (empmst.DOJ >='2005-11-01' and empmst.DOJ <='2009-03-31') and empmst.SUPER_ANN_DATE >'2021-01-31' ");
				lSBQuery.append(" ORDER BY t.DCPS_EMP_ID ASC ");

				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				logger.info("Query to srka lLstEmpFiveInst in  heaqder**********"
						+ lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}			
		}
		return lLstEmpFiveInst;
	}

	
	public List getDdoSearchList(String gStrLocationCode,String searchDDO) {
		List lLstEmpFiveInst = null;
		StringBuilder sb = null;
			try {
				sb = new StringBuilder();
				sb.append(" SELECT ddo.ddo_code FROM org_ddo_mst ddo join mst_dcps_ddo_office office on office.ddo_code = ddo.ddo_code and office.ddo_office='Yes' ");
				sb.append(" where ddo.DDO_NAME= '"+searchDDO+"' and substr(ddo.ddo_code,1,4)='"+gStrLocationCode+"' ");
				Query stQuery = ghibSession.createSQLQuery(sb.toString());
				lLstEmpFiveInst = stQuery.list();
				
				if(lLstEmpFiveInst.size()>0)
				searchDDO=(String) lLstEmpFiveInst.get(0);

				if(!searchDDO.equals("")){
				sb = new StringBuilder();
				sb.append("select f.DDO_CODE,count(*),sum(f.EMPLOYEECOUNT),sum(f.TIERIIAMOUNT),sum(f.INTEREST),sum(f.INTEREST+f.TIERIIAMOUNT),ddo1.DDO_NAME from TIERTWO_NAMUMNA_F f left join org_ddo_mst ddo1 on f.DDO_CODE=ddo1.DDO_CODE where f.stage=1 and f.DDO_CODE in "); 
				sb.append("( "); 
				sb.append("   SELECT "); 
				sb.append("   ddo.DDO_CODE "); 
				sb.append("   FROM ORG_DDO_MST ddo "); 
				sb.append("   RIGHT JOIN MST_DCPS_DDO_OFFICE office on ddo.DDO_CODE=office.DDO_CODE "); 
				sb.append("   and office.DDO_OFFICE='Yes' "); 
				sb.append("   RIGHT JOIN ORG_USERPOST_RLT user on ddo.POST_ID=user.POST_ID "); 
				sb.append("   and user.ACTIVATE_FLAG=1 "); 
				sb.append("   where ddo.DDO_CODE ='"+searchDDO+"' "); 
				sb.append(") group by f.DDO_CODE,ddo1.DDO_NAME ");
				}else{
					sb = new StringBuilder();
					sb.append("select f.DDO_CODE,count(*),sum(f.EMPLOYEECOUNT),sum(f.TIERIIAMOUNT),sum(f.INTEREST),sum(f.INTEREST+f.TIERIIAMOUNT),ddo1.DDO_NAME from TIERTWO_NAMUMNA_F f left join org_ddo_mst ddo1 on f.DDO_CODE=ddo1.DDO_CODE where f.stage=1 and f.DDO_CODE in "); 
					sb.append("( "); 
					sb.append("   SELECT "); 
					sb.append("   ddo.DDO_CODE "); 
					sb.append("   FROM ORG_DDO_MST ddo "); 
					sb.append("   RIGHT JOIN MST_DCPS_DDO_OFFICE office on ddo.DDO_CODE=office.DDO_CODE "); 
					sb.append("   and office.DDO_OFFICE='Yes' "); 
					sb.append("   RIGHT JOIN ORG_USERPOST_RLT user on ddo.POST_ID=user.POST_ID "); 
					sb.append("   and user.ACTIVATE_FLAG=1 "); 
					sb.append("   where ddo.DDO_CODE like '"+gStrLocationCode+"%' "); 
					sb.append(") group by f.DDO_CODE,ddo1.DDO_NAME ");	
				}
				stQuery = ghibSession.createSQLQuery(sb.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		
		return lLstEmpFiveInst;
	}

	
	public void empListOfFiveInstUpdateTO(String dcpsEmpId,String Interest,String BillNo, String isDeputation) {
		org.hibernate.Session session = this.getSession();
		StringBuilder lSBQuery = new StringBuilder();
		
		if(isDeputation.equals("Y"))
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set STATE_FLAG='11' where dcps_emp_id ='"+ dcpsEmpId + "' and FIN_YEAR_ID IN(22,23,24,25,26) ");
		else
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set STATE_FLAG='1' where dcps_emp_id ='"+ dcpsEmpId + "' and FIN_YEAR_ID IN(22,23,24,25,26) ");
		
		SQLQuery lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		
		lSBQuery = new StringBuilder();
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set BILL_NO='"+BillNo+"',interest='"+Interest+"' where dcps_emp_id ='"
				+ dcpsEmpId + "' and FIN_YEAR_ID IN(22)  ");
		
		SQLQuery lQuery1 = session.createSQLQuery(lSBQuery.toString());
		int status = lQuery1.executeUpdate();
		
	}
	
	
	public String generateOrderF(String strDDOCode, String interest,
			String totalAMount, String grandTotalAmount, int length,String DcpsId,String ArrayDcpsID,String ArrayTotalAmount,String ArrayInterest) {
		String Status="Y";
		try{
		int year = Calendar.getInstance().get(Calendar.YEAR);
		 int month = Calendar.getInstance().get(Calendar.MONTH)+1;
        //code for generating sequence
		  String Count = "";
		    StringBuilder Strbld = new StringBuilder();
		    
		    Strbld.append(" SELECT count(1)+1 FROM TIERTWO_NAMUMNA_F where DDO_CODE ='"+strDDOCode+"' and BILL_YEAR='"+year+"' and BILL_MONTH = '"+month+"' ");
		    
		    SQLQuery lSEQQuery = this.ghibSession.createSQLQuery(Strbld.toString());
		    
		    Count = lSEQQuery.list().get(0).toString();
		 
		 String Seq=strDDOCode+year+month+"00"+Count;
		 Status=Seq;
		
		StringBuilder lSBQuery = new StringBuilder();
		Double Total =Double.parseDouble(totalAMount)+Double.parseDouble(interest);
		
		
		lSBQuery.append(" insert into TIERTWO_NAMUMNA_F values( '"+Seq+"', '"+year+"', '"+month+"','"+totalAMount+"','"+interest+"','"+length+"',sysdate,'"+strDDOCode+"','"+Total+"',1) ");
		SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		
		
		//for insert bill details
		String DcpsID_split[] = ArrayDcpsID.split("~");
		String TotalAmount_split[] = ArrayTotalAmount.split("~");
		String Interest_split[] = ArrayInterest.split("~");

		
		//To get sequence
		 StringBuilder StrSeqCount = new StringBuilder();
		    
		 StrSeqCount.append(" select count(*) from TIERII_NAMUMNA_F_EMP_DETAILS where  DDO_CODE ='"+strDDOCode+"'  ");
		    
		    SQLQuery SQLCount = this.ghibSession.createSQLQuery(StrSeqCount.toString());
		    
		   int SeqCount = Integer.parseInt(SQLCount.list().get(0).toString());
		

		if (ArrayDcpsID != null && DcpsID_split.length > 0) {

			for (Integer lInt = 0; lInt < Interest_split.length; lInt++) {
				String SeqCount1=strDDOCode+SeqCount;
				String dcpsId=DcpsID_split[lInt];
				String instAmount=TotalAmount_split[lInt];
				String intAmount=Interest_split[lInt];
				StringBuilder lSBQueryBillDetails = new StringBuilder();
				lSBQueryBillDetails.append("INSERT INTO TIERII_NAMUMNA_F_EMP_DETAILS  "); 
				lSBQueryBillDetails.append("VALUES ( '"+SeqCount1+"','"+dcpsId+"','"+instAmount+"',"+intAmount+",'"+Seq+"','"+strDDOCode+"','"+year+"', '"+month+"',null,sysdate,null,null,null,null,0,null) ");
				SQLQuery lQueryBillDetails = ghibSession.createSQLQuery(lSBQueryBillDetails.toString());
				lQueryBillDetails.executeUpdate();
				SeqCount++;
			}
//				TierIISixPcArrearDao.empListOfFiveInstUpdateTO(dcpsEmp_Id[lInt],Interest_split[lInt]);
			}
		
		
		
		

		}
		catch(Exception e){
			System.out.println("Exception e"+e);
//			Status="N";
		}
		
		return Status;
	}


	public String generateTierTwoBill(String strDDOCode, String interest,
			String totalAMount, String grandTotalAmount, int length,String DcpsId,String ArrayDcpsID,String ArrayTotalAmount,String ArrayInterest,String isDeputation) {
		String Status="Y";
		if(!isDeputation.equals("Y")){
		try{
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH)+1;
        //code for generating sequence
		String Count = "";
		StringBuilder Strbld = new StringBuilder();
		Strbld.append(" SELECT count(1)+1 FROM TIERTWO_BILL_DTLS where DDO_CODE ='"+strDDOCode+"' and BILL_YEAR='"+year+"' and BILL_MONTH = '"+month+"' ");
		SQLQuery lSEQQuery = this.ghibSession.createSQLQuery(Strbld.toString());
		Count = lSEQQuery.list().get(0).toString();
		String Seq=strDDOCode+year+month+"00"+Count;
		
		StringBuilder lSBQuery = new StringBuilder();
		Double Total =Double.parseDouble(totalAMount)+Double.parseDouble(interest);
		lSBQuery.append(" insert into TIERTWO_BILL_DTLS values( '"+Seq+"', '"+year+"', '"+month+"','"+totalAMount+"','"+interest+"','"+length+"',null,null,0,sysdate,'"+strDDOCode+"',null,'"+Total+"') ");
		SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		
		//for insert bill details
		String DcpsID_split[] = ArrayDcpsID.split("~");
		String TotalAmount_split[] = ArrayTotalAmount.split("~");
		String Interest_split[] = ArrayInterest.split("~");
		
		//To get sequence
		 StringBuilder StrSeqCount = new StringBuilder();
		 StrSeqCount.append(" select count(*) from TIERII_EMP_DETAILS where  DDO_CODE ='"+strDDOCode+"'  ");
		 SQLQuery SQLCount = this.ghibSession.createSQLQuery(StrSeqCount.toString());
		 int SeqCount = Integer.parseInt(SQLCount.list().get(0).toString());
		 if (ArrayDcpsID != null && DcpsID_split.length > 0) {
			for (Integer lInt = 0; lInt < Interest_split.length; lInt++) {
				String SeqCount1=strDDOCode+SeqCount;
				String dcpsId=DcpsID_split[lInt];
				String instAmount=TotalAmount_split[lInt];
				String intAmount=Interest_split[lInt];
				StringBuilder lSBQueryBillDetails = new StringBuilder();
				lSBQueryBillDetails.append("INSERT INTO TIERII_EMP_DETAILS  "); 
				lSBQueryBillDetails.append("VALUES ( '"+SeqCount1+"','"+dcpsId+"','"+instAmount+"',"+intAmount+",'"+Seq+"','"+strDDOCode+"','"+year+"', '"+month+"',null,sysdate,null,null,null,null,0) ");
				SQLQuery lQueryBillDetails = ghibSession.createSQLQuery(lSBQueryBillDetails.toString());
				lQueryBillDetails.executeUpdate();
				SeqCount++;
			}	
		 }
		lSBQuery = new StringBuilder();
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set reason=null ,STATE_FLAG='3',BILL_NO='"+Seq+"' where dcps_emp_id  in ("+ DcpsId +")");
		
		SQLQuery lQuery1 = ghibSession.createSQLQuery(lSBQuery.toString());
		int status = lQuery1.executeUpdate();
		}catch(Exception e){
			System.out.println("Exception e"+e);
		}
	 }else{
			try{
				int year = Calendar.getInstance().get(Calendar.YEAR);
				int month = Calendar.getInstance().get(Calendar.MONTH)+1;
		        //code for generating sequence
				String Count = "";
				/*StringBuilder Strbld = new StringBuilder();
				Strbld.append(" select DDO_CODE from MST_TREASURY_DDOCODE_MPG where LOC_ID ="+ strDDOCode + " and ACTIVE_FLAG = 1 ");
				SQLQuery lSEQQuery = this.ghibSession.createSQLQuery(Strbld.toString());
				strDDOCode=lSEQQuery.list().get(0).toString();*/
				
				StringBuilder Strbld = new StringBuilder();
				Strbld.append(" SELECT count(1)+1 FROM TIERTWO_BILL_DTLS where DDO_CODE ='"+strDDOCode+"' and BILL_YEAR='"+year+"' and BILL_MONTH = '"+month+"' ");
				SQLQuery lSEQQuery = this.ghibSession.createSQLQuery(Strbld.toString());
				Count = lSEQQuery.list().get(0).toString();
				String Seq=strDDOCode+year+month+"00"+Count;
				
				StringBuilder lSBQuery = new StringBuilder();
				Double Total =Double.parseDouble(totalAMount)+Double.parseDouble(interest);
				lSBQuery.append(" insert into TIERTWO_BILL_DTLS values( '"+Seq+"', '"+year+"', '"+month+"','"+totalAMount+"','"+interest+"','"+length+"',null,null,10,sysdate,'"+strDDOCode+"',null,'"+Total+"') ");
				SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lQuery.executeUpdate();
				
				//for insert bill details
				String DcpsID_split[] = ArrayDcpsID.split("~");
				String TotalAmount_split[] = ArrayTotalAmount.split("~");
				String Interest_split[] = ArrayInterest.split("~");
				
				//To get sequence
				 StringBuilder StrSeqCount = new StringBuilder();
				 StrSeqCount.append(" select count(*) from TIERII_EMP_DETAILS where  DDO_CODE ='"+strDDOCode+"'  ");
				 SQLQuery SQLCount = this.ghibSession.createSQLQuery(StrSeqCount.toString());
				 int SeqCount = Integer.parseInt(SQLCount.list().get(0).toString());
				 if (ArrayDcpsID != null && DcpsID_split.length > 0) {
					for (Integer lInt = 0; lInt < Interest_split.length; lInt++) {
						String SeqCount1=strDDOCode+SeqCount;
						String dcpsId=DcpsID_split[lInt];
						String instAmount=TotalAmount_split[lInt];
						String intAmount=Interest_split[lInt];
						StringBuilder lSBQueryBillDetails = new StringBuilder();
						lSBQueryBillDetails.append("INSERT INTO TIERII_EMP_DETAILS  "); 
						lSBQueryBillDetails.append("VALUES ( '"+SeqCount1+"','"+dcpsId+"','"+instAmount+"',"+intAmount+",'"+Seq+"','"+strDDOCode+"','"+year+"', '"+month+"',null,sysdate,null,null,null,null,10) ");
						SQLQuery lQueryBillDetails = ghibSession.createSQLQuery(lSBQueryBillDetails.toString());
						lQueryBillDetails.executeUpdate();
						SeqCount++;
					}	
				 }
				lSBQuery = new StringBuilder();
				lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set reason=null ,STATE_FLAG='13',BILL_NO='"+Seq+"' where dcps_emp_id  in ("+ DcpsId +")");
				
				SQLQuery lQuery1 = ghibSession.createSQLQuery(lSBQuery.toString());
				int status = lQuery1.executeUpdate();
				}catch(Exception e){
					System.out.println("Exception e"+e);
				} 
	 }
		return Status;
	}


	public void updateInstAmtTO(String dcpsEmpId, String dueDrawnAmt,String orderID,String deputation) {
		org.hibernate.Session session = this.getSession();
		StringBuilder lSBQuery = new StringBuilder();
		
		if(deputation.equals("Y"))
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set STATE_FLAG='12' where dcps_emp_id ='"+ dcpsEmpId+ "' and FIN_YEAR_ID IN(22,23,24,25,26) and STATE_FLAG='11' ");
		else
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set STATE_FLAG='2' where dcps_emp_id ='"+ dcpsEmpId+ "' and FIN_YEAR_ID IN(22,23,24,25,26) and STATE_FLAG='1' ");
		
		SQLQuery lQuery = session.createSQLQuery(lSBQuery.toString());
		int status = lQuery.executeUpdate();
		
	    lSBQuery = new StringBuilder();
		if(deputation.equals("Y"))
		lSBQuery.append("update TIERII_NAMUMNA_F_EMP_DETAILS set STATUS =12 where BILLL_ID='"+orderID+"'  and DCPS_ID ='"+dcpsEmpId+"'");
		else
		lSBQuery.append("update TIERII_NAMUMNA_F_EMP_DETAILS set STATUS =2 where BILLL_ID='"+orderID+"'  and DCPS_ID ='"+dcpsEmpId+"'");
			
		SQLQuery lQuery11 = session.createSQLQuery(lSBQuery.toString());
		int status11 = lQuery11.executeUpdate();
		
	}

	public String getRoleId(Long lLngPostId) {

		String lStrRoleId = null;
		List lLstRoleDtls = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" select ROLE_ID  from ACL_POSTROLE_RLT where POST_ID ='"+ lLngPostId + "'  and ROLE_ID in (700003,700004,700002,451467900,100018) ");////$t19Apr2022
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lLstRoleDtls = lQuery.list();

			if (lLstRoleDtls != null) {
				if (lLstRoleDtls.size() != 0) {
					if (lLstRoleDtls.get(0) != null) {
						lStrRoleId = lLstRoleDtls.get(0).toString();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lStrRoleId;
	}
	
	
	public void emprejectByTo(String dcpsEmpId,String Reason,String orderID,String deputation) {
		org.hibernate.Session session = this.getSession();
		StringBuilder lSBQuery = new StringBuilder();
		
		if(deputation.equals("Y"))
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set STATE_FLAG=10 where dcps_emp_id ='"+ dcpsEmpId + "' and FIN_YEAR_ID IN(22,23,24,25,26) and STATE_FLAG='11' ");
		else
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set STATE_FLAG=0 where dcps_emp_id ='"+ dcpsEmpId + "' and FIN_YEAR_ID IN(22,23,24,25,26) and STATE_FLAG='1' ");	
		
		SQLQuery lQuery = session.createSQLQuery(lSBQuery.toString());
		int status = lQuery.executeUpdate();
		
		lSBQuery = new StringBuilder();
		if(deputation.equals("Y"))
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set REASON='"+Reason+"',BILL_NO=null,INTEREST =null where dcps_emp_id ='"+ dcpsEmpId + "' and FIN_YEAR_ID IN(22) and STATE_FLAG='10' ");
		else
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set REASON='"+Reason+"',BILL_NO=null,INTEREST =null where dcps_emp_id ='"+ dcpsEmpId + "' and FIN_YEAR_ID IN(22) and STATE_FLAG='0' ");	
		
		SQLQuery lQuery1 = session.createSQLQuery(lSBQuery.toString());
		int status1 = lQuery1.executeUpdate();
		
		lSBQuery = new StringBuilder();
		if(deputation.equals("Y"))
		lSBQuery.append("update TIERII_NAMUMNA_F_EMP_DETAILS set STATUS =-11 ,REASON='"+Reason+"' where BILLL_ID='"+orderID+"'  and DCPS_ID ='"+dcpsEmpId+"'");
		else
		lSBQuery.append("update TIERII_NAMUMNA_F_EMP_DETAILS set STATUS =-1 ,REASON='"+Reason+"' where BILLL_ID='"+orderID+"'  and DCPS_ID ='"+dcpsEmpId+"'");
		
		SQLQuery lQuery11 = session.createSQLQuery(lSBQuery.toString());
		int status11 = lQuery11.executeUpdate();
		}

	@Override
	public List getTierIIBillList(String ddoCode,String month,String year, String searchSeva,String searchEmp) {
		List lLstEmpFiveInst = null;
		StringBuilder lSBQuery = null;
			try {
				lSBQuery = new StringBuilder();
				
				if(!searchSeva.equals("")){
				lSBQuery.append(" select substr(bill.BILL_ID,0,4)||'/'||bill.BILL_YEAR||'/TIER-II/'||bill.BILL_ID ,bill.DDO_CODE ,bill.EMPLOYEECOUNT ,bill.INTEREST,bill.TIERIIAMOUNT ,bill.INTEREST+bill.TIERIIAMOUNT,bill.AUTH_NUMBER ,DECODE(BILL_STATUS,'0','Order Generated','-2','Order Deleted','-1','Bill Deleted','2','Bill Generated','3','Bill is fowarded to BEAMS','4','Bill is forwarded To SRKA','5','Grant is Approved From SRKA','6','BDS Generated','7','TIER-II Bill Approved'),bill.BILL_YEAR,bill.BILL_MONTH,to_char(bill.BILL_GENERATION_DATE, 'dd/mm/yyyy'),bill.BILL_ID,bill.VOUCHER_NO,to_char(bill.VOUCHER_DATE,'dd/mm/yyyy') ");
				lSBQuery.append(" from TIERTWO_BILL_DTLS bill join TIERII_EMP_DETAILS emp on bill.BILL_ID=emp.BILLL_ID join mst_dcps_emp mst on emp.dcps_ID=mst.DCPS_EMP_ID where bill.DDO_CODE = '"+ddoCode+"' and mst.SEVARTH_ID='"+searchSeva+"' order by bill.BILL_GENERATION_DATE desc ");
				}else if(!searchEmp.equals("")){
				lSBQuery.append(" select substr(bill.BILL_ID,0,4)||'/'||bill.BILL_YEAR||'/TIER-II/'||bill.BILL_ID ,bill.DDO_CODE ,bill.EMPLOYEECOUNT ,bill.INTEREST,bill.TIERIIAMOUNT ,bill.INTEREST+bill.TIERIIAMOUNT,bill.AUTH_NUMBER ,DECODE(BILL_STATUS,'0','Order Generated','-2','Order Deleted','-1','Bill Deleted','2','Bill Generated','3','Bill is fowarded to BEAMS','4','Bill is forwarded To SRKA','5','Grant is Approved From SRKA','6','BDS Generated','7','TIER-II Bill Approved'),bill.BILL_YEAR,bill.BILL_MONTH,to_char(bill.BILL_GENERATION_DATE, 'dd/mm/yyyy'),bill.BILL_ID,bill.VOUCHER_NO,to_char(bill.VOUCHER_DATE,'dd/mm/yyyy') ");
				lSBQuery.append(" from TIERTWO_BILL_DTLS bill join TIERII_EMP_DETAILS emp on bill.BILL_ID=emp.BILLL_ID join mst_dcps_emp mst on emp.dcps_ID=mst.DCPS_EMP_ID where bill.DDO_CODE = '"+ddoCode+"' and mst.emp_name like '%"+searchEmp+"%' order by bill.BILL_GENERATION_DATE desc ");
				}else{
				lSBQuery.append(" select substr(BILL_ID,0,4)||'/'||BILL_YEAR||'/TIER-II/'||BILL_ID ,DDO_CODE ,EMPLOYEECOUNT ,INTEREST,TIERIIAMOUNT ,INTEREST+TIERIIAMOUNT,AUTH_NUMBER ,DECODE(BILL_STATUS,'0','Order Generated','-2','Order Deleted','-1','Bill Deleted','2','Bill Generated','3','Bill is fowarded to BEAMS','4','Bill is forwarded To SRKA','5','Grant is Approved From SRKA','6','BDS Generated','7','TIER-II Bill Approved'),BILL_YEAR,BILL_MONTH,to_char(BILL_GENERATION_DATE, 'dd/mm/yyyy'),BILL_ID,VOUCHER_NO,to_char(VOUCHER_DATE,'dd/mm/yyyy')  from TIERTWO_BILL_DTLS where DDO_CODE like '"+ddoCode+"%'  ");	
				if(!month.equals(""))
				lSBQuery.append(" and BILL_STATUS in(0,-2,-1,2,3,4,5,6,7) and BILL_YEAR='"+year+"' and  BILL_MONTH='"+month+"' order by BILL_GENERATION_DATE desc ");
				else
				lSBQuery.append(" and BILL_STATUS in(0,-2,-1,2,3,4,5,6,7) order by BILL_GENERATION_DATE desc ");
				}
			
				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		
		return lLstEmpFiveInst;
	}
	
	
	public List getTierIIBillList(String locID,String searchSeva, String searchEmp,String cmbDDOCode,String IsDeputation,String blank) {
		List lLstEmpFiveInst = null;
		StringBuilder lSBQuery = null;
			try {
				lSBQuery = new StringBuilder();
				if(!IsDeputation.equals("Y")){
				lSBQuery.append(" select substr(t.BILL_ID,0,4)||'/'||t.BILL_YEAR||'/TIER-II/'||t.BILL_ID ,t.DDO_CODE ,t.EMPLOYEECOUNT ,t.INTEREST,t.TIERIIAMOUNT ,t.INTEREST+t.TIERIIAMOUNT,t.AUTH_NUMBER ,DECODE(BILL_STATUS,'0','Order Generated','-2','Order Deleted','-1','Bill Deleted','2','Bill Generated','3','Bill is fowarded to BEAMS','4','Bill is forwarded To SRKA','5','Grant is Approved From SRKA','6','BDS Generated','7','TIER-II Bill Approved'),t.BILL_YEAR,t.BILL_MONTH,to_char(t.BILL_GENERATION_DATE, 'dd/mm/yyyy'),t.BILL_ID,o.ddo_name ");
				if(!searchSeva.equals("")){
				lSBQuery.append(" from TIERTWO_BILL_DTLS t join TIERII_EMP_DETAILS emp on t.BILL_ID=emp.BILLL_ID join mst_Dcps_emp mst on emp.DCPS_ID=mst.DCPS_EMP_ID left join org_ddo_mst o on t.DDO_CODE=o.ddo_code ");
				lSBQuery.append(" where t.BILL_STATUS =4  and mst.SEVARTH_ID='"+searchSeva+"' ");
				}else if(!searchEmp.equals("")){
					lSBQuery.append(" from TIERTWO_BILL_DTLS t join TIERII_EMP_DETAILS emp on t.BILL_ID=emp.BILLL_ID join mst_Dcps_emp mst on emp.DCPS_ID=mst.DCPS_EMP_ID  left join org_ddo_mst o on t.DDO_CODE=o.ddo_code "); 
				lSBQuery.append(" where t.BILL_STATUS =4 and mst.emp_name like '"+searchEmp+"%' ");
				}else if(!cmbDDOCode.equals("")){
				lSBQuery.append(" from TIERTWO_BILL_DTLS t left join org_ddo_mst o on t.DDO_CODE=o.ddo_code "); 
				lSBQuery.append(" where t.DDO_CODE = '"+cmbDDOCode+"'  and t.BILL_STATUS =4 ");
				}else{
				lSBQuery.append(" from TIERTWO_BILL_DTLS t left join org_ddo_mst o on t.DDO_CODE=o.ddo_code "); 
				lSBQuery.append(" where substr(t.DDO_CODE,0,4) = '"+locID+"'  and t.BILL_STATUS =4 ");
				}
				}else{
					lSBQuery.append(" select substr(t.BILL_ID,0,4)||'/'||t.BILL_YEAR||'/TIER-II/'||t.BILL_ID ,t.DDO_CODE ,t.EMPLOYEECOUNT ,t.INTEREST,t.TIERIIAMOUNT ,t.INTEREST+t.TIERIIAMOUNT,t.AUTH_NUMBER ,DECODE(BILL_STATUS,'10','Order Generated','-12','Order Deleted','-11','Bill Deleted','12','Bill Generated','13','Bill is fowarded to BEAMS','14','Bill is forwarded To SRKA','15','Grant is Approved From SRKA','16','BDS Generated','17','TIER-II Bill Approved'),t.BILL_YEAR,t.BILL_MONTH,to_char(t.BILL_GENERATION_DATE, 'dd/mm/yyyy'),t.BILL_ID,o.ddo_name ");
					if(!searchSeva.equals("")){
					lSBQuery.append(" from TIERTWO_BILL_DTLS t join TIERII_EMP_DETAILS emp on t.BILL_ID=emp.BILLL_ID join mst_Dcps_emp mst on emp.DCPS_ID=mst.DCPS_EMP_ID left join org_ddo_mst o on t.DDO_CODE=o.ddo_code ");
					lSBQuery.append(" where t.BILL_STATUS =14  and mst.SEVARTH_ID='"+searchSeva+"' ");
					}else if(!searchEmp.equals("")){
						lSBQuery.append(" from TIERTWO_BILL_DTLS t join TIERII_EMP_DETAILS emp on t.BILL_ID=emp.BILLL_ID join mst_Dcps_emp mst on emp.DCPS_ID=mst.DCPS_EMP_ID  left join org_ddo_mst o on t.DDO_CODE=o.ddo_code "); 
					lSBQuery.append(" where t.BILL_STATUS =14 and mst.emp_name like '"+searchEmp+"%' ");
					}else if(!cmbDDOCode.equals("")){
					lSBQuery.append(" from TIERTWO_BILL_DTLS t left join org_ddo_mst o on t.DDO_CODE=o.ddo_code "); 
					lSBQuery.append(" where t.DDO_CODE = '"+cmbDDOCode+"'  and t.BILL_STATUS =14 ");
					}else{
					lSBQuery.append(" from TIERTWO_BILL_DTLS t left join org_ddo_mst o on t.DDO_CODE=o.ddo_code "); 
					lSBQuery.append(" where substr(t.DDO_CODE,0,4) = '"+locID+"'  and t.BILL_STATUS =14 ");
					}	
				}
				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		
		return lLstEmpFiveInst;
	}


	
	
	public List getTierIIOrderF(String ddoCode,String month,String year,String login,String searchSeva,String searchEmp,String locId) {
		List lLstEmpFiveInst = null;
		List lLstEmpFiveInstNew = null;
		String value=null;
		StringBuilder lSBQuery = null;
			try {
				lSBQuery = new StringBuilder();
				if(login.equals("Treasury") || ((searchSeva.length()>0 && locId.length()==4)||(searchEmp.length()>0 && locId.length()==4)) ){/////$t showP
					
				  if(!searchSeva.equals("")){	
				  lSBQuery.append(" select dtls.BILL_ID,dtls.BILL_ID,dtls.BILL_year,dtls.bill_month,dtls.ddo_code,dtls.TIERIIAMOUNT+dtls.INTEREST,dtls.EMPLOYEECOUNT,temp.a from ");
				  lSBQuery.append(" (select sum(dt.INST_AMOUNT+dt.int_amount)as a,dt.BILLL_ID from TIERII_NAMUMNA_F_EMP_DETAILS dt join mst_Dcps_emp emp on dt.DCPS_ID=emp.DCPS_EMP_ID WHERE dt.STATUS in(0,2) and emp.SEVARTH_ID='"+searchSeva+"' GROUP by dt.BILLL_ID)temp ");//BILLL_ID='111122222220219001' and 
				  //lSBQuery.append(" join TIERTWO_NAMUMNA_F dtls on dtls.BILL_ID=temp.billl_id and dtls.DDO_CODE like '"+locId+"%' and dtls.BILL_YEAR='"+year+"'and dtls.BILL_MONTH='"+month+"' order by dtls.BILL_GENERATION_DATE desc ");  
				  lSBQuery.append(" join TIERTWO_NAMUMNA_F dtls on dtls.BILL_ID=temp.billl_id and dtls.DDO_CODE like '"+locId+"%' and dtls.BILL_YEAR='"+year+"' order by dtls.BILL_GENERATION_DATE desc ");  
				  }else if(!searchEmp.equals("")){
				  lSBQuery.append(" select dtls.BILL_ID,dtls.BILL_ID,dtls.BILL_year,dtls.bill_month,dtls.ddo_code,dtls.TIERIIAMOUNT+dtls.INTEREST,dtls.EMPLOYEECOUNT,temp.a from ");
				  lSBQuery.append(" (select sum(dt.INST_AMOUNT+dt.int_amount)as a,dt.BILLL_ID from TIERII_NAMUMNA_F_EMP_DETAILS dt join mst_Dcps_emp emp on dt.DCPS_ID=emp.DCPS_EMP_ID WHERE dt.STATUS in(0,2) and emp.emp_name like '"+searchEmp+"%' GROUP by dt.BILLL_ID)temp ");//BILLL_ID='111122222220219001' and 
				  //lSBQuery.append(" join TIERTWO_NAMUMNA_F dtls on dtls.BILL_ID=temp.billl_id and dtls.DDO_CODE like '"+locId+"%' and dtls.BILL_YEAR='"+year+"'and dtls.BILL_MONTH='"+month+"' order by dtls.BILL_GENERATION_DATE desc ");
				  lSBQuery.append(" join TIERTWO_NAMUMNA_F dtls on dtls.BILL_ID=temp.billl_id and dtls.DDO_CODE like '"+locId+"%' and dtls.BILL_YEAR='"+year+"' order by dtls.BILL_GENERATION_DATE desc ");
				  }else{
				  lSBQuery.append(" select dtls.BILL_ID,dtls.BILL_ID,dtls.BILL_year,dtls.bill_month,dtls.ddo_code,dtls.TIERIIAMOUNT+dtls.INTEREST,dtls.EMPLOYEECOUNT,temp.a from ");
				  lSBQuery.append(" (select sum(INST_AMOUNT+int_amount)as a,BILLL_ID from TIERII_NAMUMNA_F_EMP_DETAILS WHERE STATUS in(0,2)  GROUP by BILLL_ID)temp ");//BILLL_ID='111122222220219001' and 
				  //lSBQuery.append(" join TIERTWO_NAMUMNA_F dtls on dtls.BILL_ID=temp.billl_id and dtls.DDO_CODE = '"+ddoCode+"' and dtls.BILL_YEAR='"+year+"'and dtls.BILL_MONTH='"+month+"' order by dtls.BILL_GENERATION_DATE desc ");
				  lSBQuery.append(" join TIERTWO_NAMUMNA_F dtls on dtls.BILL_ID=temp.billl_id and dtls.DDO_CODE = '"+ddoCode+"' and dtls.BILL_YEAR='"+year+"' order by dtls.BILL_GENERATION_DATE desc ");
				  }
				}else if(login.equals("DDO") || (ddoCode !=null)||!ddoCode.equals("")  ){/////$t showP
					 if(!searchSeva.equals("")){
						 lSBQuery.append(" select f.BILL_ID,f.BILL_ID,f.BILL_year,f.bill_month,f.ddo_code,f.tierIIAMOUNT+f.INTEREST,f.EMPLOYEECOUNT ");
						 lSBQuery.append(" from TIERTWO_NAMUMNA_F f join TIERII_NAMUMNA_F_EMP_DETAILS dtls on f.BILL_ID=dtls.BILLL_ID join mst_Dcps_Emp emp on dtls.dcps_ID=emp.dcps_emp_id ");
						 //lSBQuery.append(" where f.DDO_CODE = '"+ddoCode+"' and f.BILL_YEAR='"+year+"' and f.BILL_MONTH='"+month+"' and emp.SEVARTH_ID='"+searchSeva+"' order by f.BILL_GENERATION_DATE desc ");
						 lSBQuery.append(" where f.DDO_CODE = '"+ddoCode+"' and emp.SEVARTH_ID='"+searchSeva+"' and stage=1 order by f.BILL_GENERATION_DATE desc ");
					 }else if(!searchEmp.equals("")){
						 lSBQuery.append(" select f.BILL_ID,f.BILL_ID,f.BILL_year,f.bill_month,f.ddo_code,f.tierIIAMOUNT+f.INTEREST,f.EMPLOYEECOUNT ");
						 lSBQuery.append(" from TIERTWO_NAMUMNA_F f join TIERII_NAMUMNA_F_EMP_DETAILS dtls on f.BILL_ID=dtls.BILLL_ID join mst_Dcps_Emp emp on dtls.dcps_ID=emp.dcps_emp_id ");
						 //lSBQuery.append(" where f.DDO_CODE = '"+ddoCode+"' and f.BILL_YEAR='"+year+"' and f.BILL_MONTH='"+month+"' and emp.emp_name like '"+searchEmp+"%' order by f.BILL_GENERATION_DATE desc ");
						 lSBQuery.append(" where f.DDO_CODE = '"+ddoCode+"' and emp.emp_name like '"+searchEmp+"%' and stage=1 order by f.BILL_GENERATION_DATE desc ");
					 }else{
				     //lSBQuery.append(" select BILL_ID,BILL_ID,BILL_year,bill_month,ddo_code,tierIIAMOUNT+INTEREST,EMPLOYEECOUNT from TIERTWO_NAMUMNA_F where DDO_CODE = '"+ddoCode+"' and BILL_YEAR='"+year+"' and  BILL_MONTH='"+month+"' order by BILL_GENERATION_DATE desc ");
					 
				     lSBQuery.append(" select BILL_ID,BILL_ID,BILL_year,bill_month,ddo_code,tierIIAMOUNT+INTEREST,EMPLOYEECOUNT from TIERTWO_NAMUMNA_F where DDO_CODE = '"+ddoCode+"' and stage=1 ");
				     if(!month.equals(""))
					 lSBQuery.append(" and BILL_YEAR='"+year+"' and  BILL_MONTH='"+month+"' order by BILL_GENERATION_DATE desc ");
					 else
					 lSBQuery.append(" order by BILL_GENERATION_DATE desc ");
					 }
				}else{
				lSBQuery.append(" select dtls.BILL_ID,dtls.BILL_ID,dtls.BILL_year,dtls.bill_month,dtls.ddo_code,dtls.TIERIIAMOUNT+dtls.INTEREST,dtls.EMPLOYEECOUNT,temp.a from ");
				lSBQuery.append(" (select sum(INST_AMOUNT+int_amount)as a,BILLL_ID from TIERII_NAMUMNA_F_EMP_DETAILS WHERE STATUS=2  GROUP by BILLL_ID)temp ");//BILLL_ID='111122222220219001' and
				lSBQuery.append(" join TIERTWO_NAMUMNA_F dtls on dtls.BILL_ID=temp.billl_id and dtls.DDO_CODE = '"+ddoCode+"' and dtls.BILL_YEAR='"+year+"'and dtls.BILL_MONTH='"+month+"' order by BILL_GENERATION_DATE desc ");
				}

				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				
				Object[] tuple = (Object[]) lLstEmpFiveInst.get(0);
					value=tuple[0].toString();
					/*logger.info("lLstEmp count-->" + value.toString());
					value=tuple[1].toString();
					logger.info("lLstEmp count-->" + value.toString());
					value=tuple[2].toString();
					logger.info("lLstEmp count-->" + value.toString());
					value=tuple[3].toString();
					logger.info("lLstEmp count-->" + value.toString());
				    value=tuple[4].toString();
				    logger.info("lLstEmp count-->" + value.toString());
				    value=tuple[5].toString();
				    logger.info("lLstEmp count-->" + value.toString());
				    value=tuple[6].toString();
				    logger.info("lLstEmp count-->" + value.toString());
				    value=tuple[7].toString();
				    logger.info("lLstEmp count-->" + value.toString());*/
				    logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		
		return lLstEmpFiveInst;
	}

	
	
	public void ForwardToSrkaTierIIBill(String dDOCODE, String billNo, String IsDeputation) {
		// TODO Auto-generated method stub
		int status;
			
		    StringBuilder lSBQuery = new StringBuilder();
		    if (IsDeputation.equals("Y"))
			lSBQuery.append(" update TIERTWO_BILL_DTLS set BILL_STATUS=14 where ddo_code='"+dDOCODE+"' and bill_id ='"+billNo+"'");
		    else
		    lSBQuery.append(" update TIERTWO_BILL_DTLS set BILL_STATUS=4 where ddo_code='"+dDOCODE+"' and bill_id ='"+billNo+"'");
			
			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lSBQuery = new StringBuilder();
			status=lQuery.executeUpdate();
		}
	
	
	public void ApproveGrantSrkaTierIIBill(String dDOCODE, String billNo,String IsDeputation) {
		// TODO Auto-generated method stub
		int status;
			StringBuilder lSBQuery = new StringBuilder();
			if(!IsDeputation.equals("Y"))
			lSBQuery.append(" update TIERTWO_BILL_DTLS set BILL_STATUS=5 where ddo_code='"+dDOCODE+"' and bill_id ='"+billNo+"'");
			else
			lSBQuery.append(" update TIERTWO_BILL_DTLS set BILL_STATUS=15 where ddo_code='"+dDOCODE+"' and bill_id ='"+billNo+"'");
			
			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lSBQuery = new StringBuilder();
			status=lQuery.executeUpdate();
		}
	
	@Override
	public void deleteTierIIBill(String dDOCODE, String billNo,String flag,String deputation) {
		// TODO Auto-generated method stub
		int status;
		
		if(deputation.equalsIgnoreCase("Y")){
		
		if(flag.equalsIgnoreCase("BILL"))
		{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" update TIERTWO_BILL_DTLS set BILL_STATUS=12 where ddo_code='"+dDOCODE+"' and bill_id ='"+billNo+"'");
			
			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lSBQuery = new StringBuilder();
			status=lQuery.executeUpdate();
		}
		else
		{
			StringBuilder lSBQuery = new StringBuilder();
			if(flag.equalsIgnoreCase("B"))
			lSBQuery.append(" update TIERTWO_BILL_DTLS set BILL_STATUS=-11 where ddo_code='"+dDOCODE+"' and bill_id ='"+billNo+"'");
			else
			lSBQuery.append(" update TIERTWO_BILL_DTLS set BILL_STATUS=-12 where ddo_code='"+dDOCODE+"' and bill_id ='"+billNo+"'");
	
				
			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lSBQuery = new StringBuilder();
			lQuery.executeUpdate();
			
			//lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set STATE_FLAG =2,BILL_NO=null where BILL_NO='"+billNo+"' and STATE_FLAG='3' ");
			
			lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set STATE_FLAG =12 where BILL_NO='"+billNo+"' and STATE_FLAG='13' ");
			
			SQLQuery lQuery1 = ghibSession.createSQLQuery(lSBQuery.toString());
			 status = lQuery1.executeUpdate();
		}
	  }else{
		  if(flag.equalsIgnoreCase("BILL"))
			{
				StringBuilder lSBQuery = new StringBuilder();
				lSBQuery.append(" update TIERTWO_BILL_DTLS set BILL_STATUS=2 where ddo_code='"+dDOCODE+"' and bill_id ='"+billNo+"'");
				
				SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lSBQuery = new StringBuilder();
				status=lQuery.executeUpdate();
				
			}
			else
			{
				StringBuilder lSBQuery = new StringBuilder();
				if(flag.equalsIgnoreCase("B"))
				lSBQuery.append(" update TIERTWO_BILL_DTLS set BILL_STATUS=-1 where ddo_code='"+dDOCODE+"' and bill_id ='"+billNo+"'");
				else
				lSBQuery.append(" update TIERTWO_BILL_DTLS set BILL_STATUS=-2 where ddo_code='"+dDOCODE+"' and bill_id ='"+billNo+"'");
		
					
				SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lSBQuery = new StringBuilder();
				lQuery.executeUpdate();
				
				//lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set STATE_FLAG =2,BILL_NO=null where BILL_NO='"+billNo+"' and STATE_FLAG='3' ");
				
				lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set STATE_FLAG =2 where BILL_NO='"+billNo+"' and STATE_FLAG='3' ");
				
				SQLQuery lQuery1 = ghibSession.createSQLQuery(lSBQuery.toString());
				 status = lQuery1.executeUpdate();
			} 
	  }
	}

	
	public List gteBillDetailsForBEAMS(String BillNO,String DDOCode,String IsDeputation) {
		List lLstEmpFiveInst = null;
		StringBuilder lSBQuery = null;
			try {
				lSBQuery = new StringBuilder();
				if(!IsDeputation.equals("Y"))
				lSBQuery.append(" select BILL_MONTH,BILL_YEAR, BILL_ID ,BILL_GENERATION_DATE,TOTAL_AMOUNT from TIERTWO_BILL_DTLS where DDO_CODE = '"+DDOCode+"' and BILL_ID='"+BillNO+"' and BILL_STATUS='5' ");
				else
				lSBQuery.append(" select BILL_MONTH,BILL_YEAR, BILL_ID ,BILL_GENERATION_DATE,TOTAL_AMOUNT from TIERTWO_BILL_DTLS where DDO_CODE = '"+DDOCode+"' and BILL_ID='"+BillNO+"' and BILL_STATUS='15'  ");

				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		
		return lLstEmpFiveInst;
	}
	public List getOrderDetails(String BillNO,String Month,String Year) {
		List lLstEmpFiveInst = null;
		StringBuilder lSBQuery = null;
			try {
				lSBQuery = new StringBuilder();
				lSBQuery.append("select em.emp_name,em.sevarth_id,em.dcps_id,em.PRAN_NO,six.AMOUNT_PAID,y.INTEREST,(six.AMOUNT_PAID+y.INTEREST) from RLT_DCPS_SIXPC_YEARLY y join MST_DCPS_EMP em on em.DCPS_EMP_ID=y.DCPS_EMP_ID join MST_DCPS_sixpc six on em.DCPS_EMP_ID=six.DCPS_EMP_ID where y.BILL_NO='"+BillNO+"' and y.FIN_YEAR_ID =22  and y.STATE_flag=3 ");
				lSBQuery.append(" union select '','','','TOTAL',cast(sum(six.AMOUNT_PAID) as bigint),cast(sum(y.INTEREST) as bigint) ,cast(sum(six.AMOUNT_PAID+y.INTEREST) as bigint) from RLT_DCPS_SIXPC_YEARLY y join MST_DCPS_EMP em on em.DCPS_EMP_ID=y.DCPS_EMP_ID join MST_DCPS_sixpc six on em.DCPS_EMP_ID=six.DCPS_EMP_ID where y.BILL_NO='"+BillNO+"' and y.FIN_YEAR_ID =22 and y.STATE_flag=3 ");
				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		
		return lLstEmpFiveInst;
	}
	
	public List getOrderDetailsF(String BillNO,String Month,String Year) {
		List lLstEmpFiveInst = null;
		StringBuilder lSBQuery = null;
			try {
				lSBQuery = new StringBuilder();
				lSBQuery.append("select em.emp_name,em.sevarth_id,em.dcps_id,f.INST_AMOUNT+int_amount  from TIERII_NAMUMNA_F_EMP_DETAILS f "); 
				lSBQuery.append("	join  MST_DCPS_EMP em on em.DCPS_EMP_ID=f.DCPS_ID and f.BILLL_ID='"+BillNO+"' ");
				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		
		return lLstEmpFiveInst;
	}
	
	public List getOrderDetailsG(String BillNO,String Month,String Year) {
		List lLstEmpFiveInst = null;
		StringBuilder lSBQuery = null;
			try {
				lSBQuery = new StringBuilder();
				lSBQuery.append("	select em.emp_name,em.sevarth_id,em.dcps_id,em.pran_no,f.INST_AMOUNT,int_amount,f.INST_AMOUNT+int_amount  from TIERII_NAMUMNA_F_EMP_DETAILS f "); 
				lSBQuery.append("	join  MST_DCPS_EMP em on em.DCPS_EMP_ID=f.DCPS_ID and f.BILLL_ID='"+BillNO+"' ");
				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		
		return lLstEmpFiveInst;
	}
	
	
	
	public String getLocationName(String lLngPostId) {

		String lStrRoleId = null;
		List lLstRoleDtls = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" select LOC_NAME   from CMN_LOCATION_MST where LOC_ID  like '"+lLngPostId+"%' and  DEPARTMENT_ID=100003   ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lLstRoleDtls = lQuery.list();

			if (lLstRoleDtls != null) {
				if (lLstRoleDtls.size() != 0) {
					if (lLstRoleDtls.get(0) != null) {
						lStrRoleId = lLstRoleDtls.get(0).toString();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lStrRoleId;
	}
	public String getDDOName(String lLngPostId) {

		String lStrRoleId = null;
		List lLstRoleDtls = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" select ddo_name||'('||ddo_personal_name||')' from ORG_DDO_MST where DDO_CODE ='"+lLngPostId+"'  ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lLstRoleDtls = lQuery.list();

			if (lLstRoleDtls != null) {
				if (lLstRoleDtls.size() != 0) {
					if (lLstRoleDtls.get(0) != null) {
						lStrRoleId = lLstRoleDtls.get(0).toString();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lStrRoleId;
	}
	public List getTierIIInterest(String SevaarthID) throws Exception{

		
		StringBuilder lSBQuery = new StringBuilder();
		List SevarthIdList=null;
		
		try
		{
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT diSTINCT t.DCPS_EMP_ID,cast(t8.yearly_amount as varchar) as TimeA,cast(t2.yearly_amount as varchar) as TimeB,cast(t3.yearly_amount as varchar) as TimeC,"
					+ "cast(t4.yearly_amount as varchar) as TimeD,cast(t4.yearly_amount as varchar) as TimeE,cast(t5.yearly_amount as varchar) as TimeF ,empmst.EMP_NAME,empmst.SEVARTH_ID,empmst.DCPS_ID,case when empmst.PRAN_NO is null then 'N/A' else empmst.PRAN_NO end  ");
			lSBQuery.append(" FROM RLT_DCPS_SIXPC_YEARLY t ");
			lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t8 ON t.DCPS_EMP_ID=t8.DCPS_EMP_ID AND t8.fin_year_id=22 ");
			lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t2 ON t.DCPS_EMP_ID=t2.DCPS_EMP_ID AND t2.fin_year_id=23 ");
			lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t3 ON t.DCPS_EMP_ID=t3.DCPS_EMP_ID AND t3.fin_year_id=24 ");
			lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t4 ON t.DCPS_EMP_ID=t4.DCPS_EMP_ID AND t4.fin_year_id=25 ");
			lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t5 ON t.DCPS_EMP_ID=t5.DCPS_EMP_ID AND t5.fin_year_id=26 ");
			lSBQuery.append(" inner join MST_DCPS_EMP empmst ");
			lSBQuery.append(" on t.dcps_emp_id=empmst.dcps_emp_id ");
			lSBQuery.append(" where t.status_flag='A' and empmst.SEVARTH_ID ='"+SevaarthID+"' ");  //t.DCPS_EMP_ID = 9910000269 and (t.state_flag='0' or t.state_flag is null)
			lSBQuery.append(" ORDER BY t.DCPS_EMP_ID ASC ");

			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			SevarthIdList= lQuery.list();
		}
		catch(Exception e){
			gLogger.error(" Error in getEmpDtls " + e, e);
			e.printStackTrace();
			throw e;
		}
			
			
			return SevarthIdList;

		}

	public List getInterestCalculation(String finYearId){

		
		StringBuilder sb = new StringBuilder();
		List SevarthIdList=null;
		
		try
		{
			sb = new StringBuilder();
			
			if(finYearId.equalsIgnoreCase("32"))
				System.out.println("admin");

			sb.append("select r.INTEREST/100, DAYS (DATE(r.APPLICABLE_TO)) -DAYS (DATE(r.EFFECTIVE_FROM))+1    from MST_DCPS_INTEREST_RATE r "); 
			sb.append("join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID =r.FIN_YEAR_ID "); 
			sb.append("where r.FIN_YEAR_ID >21 and r.FIN_YEAR_ID="+finYearId+" order by r.EFFECTIVE_FROM ");

			
			Query lQuery = ghibSession.createSQLQuery(sb.toString());
			SevarthIdList= lQuery.list();
		}
		catch(Exception e){
			gLogger.error(" Error in getEmpDtls " + e, e);
			e.printStackTrace();
			throw e;
		}
			
			
			return SevarthIdList;

		}


	public List getLoopingFOrIntereseCalculations() {

		
		StringBuilder sb = new StringBuilder();
		List SevarthIdList=null;
		
		try
		{
			sb = new StringBuilder();

			sb.append("select distinct(r.FIN_YEAR_ID) ,fin.FIN_YEAR_DESC    from MST_DCPS_INTEREST_RATE r "); 
			sb.append("join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID =r.FIN_YEAR_ID "); 
			sb.append("where r.FIN_YEAR_ID >21 order by r.FIN_YEAR_ID ");

			
			Query lQuery = ghibSession.createSQLQuery(sb.toString());
			SevarthIdList= lQuery.list();
		}
		catch(Exception e){
			gLogger.error(" Error in getEmpDtls " + e, e);
			e.printStackTrace();
			throw e;
		}
			
			
			return SevarthIdList;

		}


//get Pending EMployee
	public String getTierIIPendingOrderCount(String ddoCode,String month,String year,String orderId,String type) {
		String lLstEmpFiveInst = "0";
		StringBuilder lSBQuery = null;
			try {
				lSBQuery = new StringBuilder();
				if(type.equals("regular"))
				lSBQuery.append(" select count(*) from  TIERII_NAMUMNA_F_EMP_DETAILS     where BILLL_ID='"+orderId+"'  and status=-1 and  DDO_CODE = '"+ddoCode+"' ");
				else
				lSBQuery.append(" select count(*) from  TIERII_NAMUMNA_F_EMP_DETAILS     where BILLL_ID='"+orderId+"'  and status=-11 and  DDO_CODE = '"+ddoCode+"' "); 

				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list().get(0).toString();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		
		return lLstEmpFiveInst;
	}
	
	//get Approved EMployee
		public String getTierIIApprovedOrderCount(String ddoCode,String month,String year,String orderId,String type) {
			String lLstEmpFiveInst = "0";
			StringBuilder lSBQuery = null;
				try {
					lSBQuery = new StringBuilder();
					if(type.equals("regular"))
					lSBQuery.append(" select count(*) from  TIERII_NAMUMNA_F_EMP_DETAILS     where BILLL_ID='"+orderId+"'  and status=2 and  DDO_CODE = '"+ddoCode+"' ");
					else
					lSBQuery.append(" select count(*) from  TIERII_NAMUMNA_F_EMP_DETAILS   where BILLL_ID='"+orderId+"'  and status=12 and  DDO_CODE = '"+ddoCode+"' "); 

					Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
					lLstEmpFiveInst = stQuery.list().get(0).toString();
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Error is :" + e, e);
				}
			
			return lLstEmpFiveInst;
		}

		//get Remaining EMployee
				public String getTierIIRemaingOrderCount(String ddoCode,String month,String year,String orderId,String type) {
					String lLstEmpFiveInst = "0";
					StringBuilder lSBQuery = null;
						try {
							lSBQuery = new StringBuilder();
							if(type.equals("regular"))
							lSBQuery.append(" select count(*) from  TIERII_NAMUMNA_F_EMP_DETAILS  where BILLL_ID='"+orderId+"'  and status=0 and   DDO_CODE = '"+ddoCode+"' ");
							else
							lSBQuery.append(" select count(*) from  TIERII_NAMUMNA_F_EMP_DETAILS  where BILLL_ID='"+orderId+"'  and status=10 and DDO_CODE = '"+ddoCode+"'  "); 

							Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
							lLstEmpFiveInst = stQuery.list().get(0).toString();
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("Error is :" + e, e);
						}
					
					return lLstEmpFiveInst;
				}


	public String space(int noOfSpace) {	
	    String blank = "";	
	    for(int i = 0; i < noOfSpace; ++i) {	
	       blank = blank + " ";	
	    }	
	    return blank;
	}
	public void empListNewTierIIInterestCalculation(String dcpsEmpId, String interest_split, String GrandTotalAmount, String IsDeputation){
		org.hibernate.Session session = this.getSession();
		StringBuilder lSBQuery = new StringBuilder();
		SQLQuery lQuery;
		
		int v=(int) (Double.parseDouble(interest_split));
		
		lSBQuery = new StringBuilder();
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set INTEREST='"+v+"' where dcps_emp_id ='"+ dcpsEmpId + "' and FIN_YEAR_ID=22 ");
		lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		
		lSBQuery = new StringBuilder();
		if(IsDeputation.equals("Y"))////$t19Apr2022 
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set STATE_FLAG='11' where dcps_emp_id ='"+ dcpsEmpId + "' and FIN_YEAR_ID IN(22,23,24,25,26) and STATE_FLAG='10' ");
		else
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set STATE_FLAG='1' where dcps_emp_id ='"+ dcpsEmpId + "' and FIN_YEAR_ID IN(22,23,24,25,26)  ");
		
		lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
	}

	public void empListNewTierIIInterestCalculationPart(String dcpsEmpId){
		org.hibernate.Session session = this.getSession();
		StringBuilder lSBQuery = new StringBuilder();
		String val= "0";
		List TableData1;
		
		lSBQuery.append("select BILL_NO from RLT_DCPS_SIXPC_YEARLY where dcps_emp_id ='"+ dcpsEmpId + "' and FIN_YEAR_ID IN(22) ");
		SQLQuery lQuery = session.createSQLQuery(lSBQuery.toString());
		val= lQuery.list().get(0).toString();
		
		lSBQuery = new StringBuilder();
		lSBQuery.append("select SUM(INST_AMOUNT)||'#'||sum(INT_AMOUNT) from TIERII_NAMUMNA_F_EMP_DETAILS WHERE BILLL_ID='"+val+"' ");
		lQuery = session.createSQLQuery(lSBQuery.toString());
		TableData1 = lQuery.list();
		String[] totalAmtBH = TableData1.get(0).toString().split("#");
		int totalAmt=Integer.parseInt(totalAmtBH[0])+Integer.parseInt(totalAmtBH[1]);
		
		lSBQuery = new StringBuilder();
		lSBQuery.append("update TIERTWO_NAMUMNA_F set INTEREST='"+totalAmtBH[1]+"',TOTAL_AMOUNT='"+totalAmt+"' where BILL_ID ='"+ val + "' ");
		lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
	}
	public List getTreasuryDdoCode(long longLoggedInLocation) {
		List temp = null;
		String ddoCode = "";
		StringBuilder Strbld = new StringBuilder();

		Strbld.append(" select DDO_CODE,PYAMENT_MODE from MST_TREASURY_DDOCODE_MPG where LOC_ID ="
				+ longLoggedInLocation + " and ACTIVE_FLAG = 1 ");

		SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());

		temp = lQuery.list();
		// this.//ghibSession.disconnect();
		return temp;
	}
	public void updateTierIIBillDetails(String authNo, long paybillId, String flag) {
		try {
			Session hibSession = getSession();
			StringBuffer str = new StringBuffer();
			if (flag.equals("Y")) {
				str.append("update TIERTWO_BILL_DTLS set AUTH_NUMBER = '" + authNo
						+ "',BILL_STATUS = 6 where BILL_ID = " + paybillId
						+ " ");
			} else {
				str.append("update TIERTWO_BILL_DTLS set BILL_STATUS = 5 where BILL_ID = "
						+ paybillId + " ");
			}
			this.gLogger.error("str" + str);
			Query query1 = hibSession.createSQLQuery(str.toString());
			query1.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public String getDDODetails(final String DDOCode) {
        List lLstEmpFiveInst = null;
        StringBuilder sb = null;
        try {
            final Session ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
            sb = new StringBuilder();
            sb.append("select design.DSGN_NAME ");
            sb.append("from MST_DCPS_DDO_OFFICE off ");
            sb.append("join cmn_location_mst loc on substr(loc.location_code,0,2)=substr(off.ddo_code,0,2) and loc.department_id=100003 ");
            sb.append("join ORG_DDO_MST ddo on ddo.ddo_code=off.ddo_code ");
            sb.append("join ORG_POST_DETAILS_RLT post on post.POST_ID=ddo.post_id ");
            sb.append("join ORG_DESIGNATION_MST design on post.DSGN_ID=design.DSGN_ID ");
            sb.append("where off.DDO_CODE = '" + DDOCode + "' and off.ddo_office='Yes' ");
            final Query stQuery = (Query)ghibSession.createSQLQuery(sb.toString());
            lLstEmpFiveInst = stQuery.list();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return (String) lLstEmpFiveInst.get(0);
    }
	public void updateVoucherEntry(String BillNo, String authNO, String voucherNo, String vouchedate, String type)
			throws HibernateException, SQLException, ParseException {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    Date parsedDate = dateFormat.parse(vouchedate);
	    Timestamp timestampvouchedate = new java.sql.Timestamp(parsedDate.getTime());
		StringBuilder sb = new StringBuilder();
		if(type.equals("regular"))
		sb.append("  update ifms.TIERTWO_BILL_DTLS  set BILL_STATUS=7,VOUCHER_NO="+ voucherNo + ",VOUCHER_DATE='" + timestampvouchedate+ "' where BILL_ID='" + BillNo + "' and AUTH_NUMBER='" + authNO + "' and BILL_STATUS=6 ");
		else
		sb.append("  update ifms.TIERTWO_BILL_DTLS  set BILL_STATUS=17,VOUCHER_NO="+ voucherNo + ",VOUCHER_DATE='" + timestampvouchedate+ "' where BILL_ID='" + BillNo + "' and AUTH_NUMBER='" + authNO + "' and BILL_STATUS=16 ");
			
		Query updateQuery = this.ghibSession.createSQLQuery(sb.toString());

		this.logger.info("Query to updateVoucherEntry**********"+ sb.toString());

		updateQuery.executeUpdate();
		// this.ghibSession.connection().commit();
		// this.//ghibSession.disconnect();
	}
	public List getEmpNameForAutoComplete(String searchKey,
			String lStrSearchType, String lStrDDOCode,String lStrSearchBy,String deputation) {

		ArrayList<ComboValuesVO> finalList = null;
		try {
			finalList = new ArrayList<ComboValuesVO>();
			ComboValuesVO cmbVO;
			Object[] obj;

			StringBuilder sb = new StringBuilder();
			Query selectQuery = null;
			Date lDtCurrDate = SessionHelper.getCurDate();
			
			sb.append("select name,name from MstEmp where UPPER(name) LIKE :searchKey and regStatus in (1) and dcpsOrGpf='Y' and dcpsId is not null ");
		/*if(!deputation.equals("Y")){
			if (lStrDDOCode != null) {
				if (!"".equals(lStrDDOCode) && !lStrSearchBy.equals("searchFromDDOSelection")) {
					sb.append(" and ddoCode = :ddoCode and ddoCode is not null ");
				}
			}
		  }else{
			  sb.append(" and ddoCode is null ");
		  }*/
		  sb.append(" and ( servEndDate is null or servEndDate >= :currentDate ) ");			
		  
		  selectQuery = ghibSession.createQuery(sb.toString());
		  
			selectQuery.setParameter("searchKey", '%' + searchKey + '%');
			selectQuery.setDate("currentDate", lDtCurrDate);
			/*if(!deputation.equals("Y")){
			if (lStrDDOCode != null) {
				if (!"".equals(lStrDDOCode)) {
					selectQuery.setParameter("ddoCode", lStrDDOCode.trim());
				}
			  }
		  }*/
		  List resultList = selectQuery.list();

			cmbVO = new ComboValuesVO();

			if (resultList != null && resultList.size() > 0) {
				Iterator it = resultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					cmbVO.setId(obj[1].toString());
					cmbVO.setDesc(obj[1].toString());
					finalList.add(cmbVO);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return finalList;

	}
	public List getEmpListForFiveInstApproveUpdateInst(String gStrLocationCode,String searchSeva,String searchEmp) {
		List lLstEmpFiveInst = null;
		StringBuilder lSBQuery = null;
			try {
				lSBQuery = new StringBuilder();
				
				lSBQuery.append(" SELECT diSTINCT t.DCPS_EMP_ID,empmst.EMP_NAME,empmst.SEVARTH_ID,empmst.dcps_id,t8.fin_year_id,cast(t8.yearly_amount as varchar) as TimeA,cast(nvl(t8.VOUCHER_NO,0) as varchar) as TAV,cast(nvl(trunc(t8.VOUCHER_DATE), sysdate) as TIMESTAMP) as TAD,t2.fin_year_id,cast(t2.yearly_amount as varchar) as TimeB,cast(nvl(t2.VOUCHER_NO,0) as varchar) as TBV,cast(nvl(trunc(t2.VOUCHER_DATE), sysdate) as TIMESTAMP) as TBD,t3.fin_year_id,cast(t3.yearly_amount as varchar) as TimeC,cast(nvl(t3.VOUCHER_NO,0) as varchar) as TCV,cast(nvl(trunc(t3.VOUCHER_DATE), sysdate) as TIMESTAMP) as TCD,  "
						+ " t4.fin_year_id,cast(t4.yearly_amount as varchar) as TimeD,cast(nvl(t4.VOUCHER_NO,0) as varchar) as TDV,cast(nvl(trunc(t4.VOUCHER_DATE), sysdate) as TIMESTAMP) as TDD,t5.fin_year_id,cast(t5.yearly_amount as varchar) as TimeE,cast(nvl(t5.VOUCHER_NO,0) as varchar) as TEV,cast(nvl(trunc(t5.VOUCHER_DATE), sysdate) as TIMESTAMP) as TED,nvl(t2.REASON,'NA') ");
				lSBQuery.append(" FROM RLT_DCPS_SIXPC_YEARLY t ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t8 ON t.DCPS_EMP_ID=t8.DCPS_EMP_ID AND t8.fin_year_id=22 and  t8.status_flag='A' and t8.bill_no is null ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t2 ON t.DCPS_EMP_ID=t2.DCPS_EMP_ID AND t2.fin_year_id=23 and  t2.status_flag='A' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t3 ON t.DCPS_EMP_ID=t3.DCPS_EMP_ID AND t3.fin_year_id=24 and  t3.status_flag='A' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t4 ON t.DCPS_EMP_ID=t4.DCPS_EMP_ID AND t4.fin_year_id=25 and  t4.status_flag='A' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t5 ON t.DCPS_EMP_ID=t5.DCPS_EMP_ID AND t5.fin_year_id=26 and  t5.status_flag='A' ");
				lSBQuery.append(" inner join MST_DCPS_EMP empmst ");
				lSBQuery.append(" on t.dcps_emp_id=empmst.dcps_emp_id ");
				
				if(!searchSeva.equals(""))
				lSBQuery.append(" where t.status_flag='A' and t.DCPS_EMP_ID in(select dcps_emp_id from MST_DCPS_EMP where DDO_CODE like '" + gStrLocationCode + "%' and dcps_or_gpf='Y' and reg_status=1 and (DOJ >='2005-11-01' and DOJ <='2009-03-31') and SUPER_ANN_DATE >'2021-01-31' and sevarth_id='"+searchSeva+"') ");
				else if(!searchEmp.equals(""))
				lSBQuery.append(" where t.status_flag='A' and t.DCPS_EMP_ID in(select dcps_emp_id from MST_DCPS_EMP where DDO_CODE like '" + gStrLocationCode + "%' and dcps_or_gpf='Y' and reg_status=1 and (DOJ >='2005-11-01' and DOJ <='2009-03-31') and SUPER_ANN_DATE >'2021-01-31' and emp_name like '%"+searchEmp+"%') ");
				else
				lSBQuery.append(" where t.status_flag='A' and t.DCPS_EMP_ID in(select dcps_emp_id from MST_DCPS_EMP where DDO_CODE like '" + gStrLocationCode + "%' and dcps_or_gpf='W' and reg_status=10 and (DOJ >='2005-11-01' and DOJ <='2009-03-31') and SUPER_ANN_DATE >'2021-01-31' and sevarth_id='"+searchSeva+"') ");
				
				lSBQuery.append(" ORDER BY t.DCPS_EMP_ID ASC ");

				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
			   /* Object[] obj=(Object[]) lLstEmpFiveInst.get(0);
			    for (int i = 0; i < obj.length; i++) {
			            logger.info("lLstEmp val-->"+i+"-->" + obj[i].toString());
			        }*/
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		
		return lLstEmpFiveInst;
	}
	
	
	
	
	public void updateFiveInstAmtTO(String SevarthID,String Reason,String Total,String InstI,String InstINo,String txtStartDateI,String InstII,String InstIINo,String txtStartDateII,String InstIII
			,String InstIIINo,String txtStartDateIII,String InstIV,String InstIVNo,String txtStartDateIV,String InstV,String InstVNo,String txtStartDateV,String Deputation
			, String cmbInstDtlsI, String cmbInstDtlsII, String cmbInstDtlsIII, String cmbInstDtlsIV, String cmbInstDtlsV, String cmbVCDtlsI, String cmbVCDtlsII, String cmbVCDtlsIII, String cmbVCDtlsIV, String cmbVCDtlsV
			, String cmbTreasuryI, String cmbTreasuryII, String cmbTreasuryIII, String cmbTreasuryIV, String cmbTreasuryV
		    , String chanInstI, String chanInstII, String chanInstIII, String chanInstIV, String chanInstV) throws ParseException{
		org.hibernate.Session session = this.getSession();
		StringBuilder lSBQuery = new StringBuilder();
		
		
		if(cmbVCDtlsI.trim().equals("-1"))
	    	   cmbVCDtlsI="NA ";
	          if(cmbVCDtlsII.trim().equals("-1"))
	        	  cmbVCDtlsII="NA ";
	          if(cmbVCDtlsIII.trim().equals("-1"))
	        	  cmbVCDtlsIII="NA ";
	          if(cmbVCDtlsIV.trim().equals("-1"))
	        	  cmbVCDtlsIV="NA ";
	          if(cmbVCDtlsV.trim().equals("-1"))
	        	  cmbVCDtlsV="NA ";
		
	          if(cmbTreasuryI.trim().equals("-1"))
	        	  cmbTreasuryI="NA ";
	             if(cmbTreasuryII.trim().equals("-1"))
	            	 cmbTreasuryII="NA ";
	             if(cmbTreasuryIII.trim().equals("-1"))
	            	 cmbTreasuryIII="NA ";
	             if(cmbTreasuryIV.trim().equals("-1"))
	            	 cmbTreasuryIV="NA ";
	             if(cmbTreasuryV.trim().equals("-1"))
	            	 cmbTreasuryV="NA ";
	          
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    Date parsedDate = dateFormat.parse(txtStartDateI);
	    Timestamp timestampvouchedate = new java.sql.Timestamp(parsedDate.getTime());
	    
	    int total=Integer.valueOf(InstI)+Integer.valueOf(InstII)+Integer.valueOf(InstIII)+Integer.valueOf(InstIV)+Integer.valueOf(InstV);
	    
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set YEARLY_AMOUNT='"+InstI+"',VOUCHER_NO='"+InstINo+"',VOUCHER_DATE='"+timestampvouchedate+"' ");
				if(Deputation.equals("Y"))
					lSBQuery.append(" ,STATUS_FLAG='A',STATE_FLAG='10',Inst_dtls='"+cmbInstDtlsI+"',Inst_VC_dtls='"+cmbVCDtlsI+"',INST_TREASURY='"+cmbTreasuryI+"',CHANGE_INST='"+chanInstI+"' WHERE dcps_emp_id in(select dcps_emp_id from mst_Dcps_emp where SEVARTH_ID='"+SevarthID+"') and FIN_YEAR_ID='22' ");
		       else 
		            lSBQuery.append(" WHERE dcps_emp_id in(select dcps_emp_id from mst_Dcps_emp where SEVARTH_ID='"+SevarthID+"') and FIN_YEAR_ID='22' and STATUS_FLAG='A' ");
				
		SQLQuery lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		
		parsedDate = dateFormat.parse(txtStartDateII);
	    timestampvouchedate = new java.sql.Timestamp(parsedDate.getTime());
		lSBQuery = new StringBuilder();
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set YEARLY_AMOUNT='"+InstII+"',VOUCHER_NO='"+InstIINo+"',VOUCHER_DATE='"+timestampvouchedate+"' ");
				if(Deputation.equals("Y"))
					lSBQuery.append(" ,STATUS_FLAG='A',Reason='"+"DEPT "+Reason+"',STATE_FLAG='10',Inst_dtls='"+cmbInstDtlsII+"',Inst_VC_dtls='"+cmbVCDtlsII+"',INST_TREASURY='"+cmbTreasuryII+"',CHANGE_INST='"+chanInstII+"' WHERE dcps_emp_id in(select dcps_emp_id from mst_Dcps_emp where SEVARTH_ID='"+SevarthID+"') and FIN_YEAR_ID='23' ");
		       else 
		            lSBQuery.append(" ,Reason='"+Reason+"' WHERE dcps_emp_id in(select dcps_emp_id from mst_Dcps_emp where SEVARTH_ID='"+SevarthID+"') and FIN_YEAR_ID='23' and STATUS_FLAG='A' "); 
		lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		
		parsedDate = dateFormat.parse(txtStartDateIII);
	    timestampvouchedate = new java.sql.Timestamp(parsedDate.getTime());
		lSBQuery = new StringBuilder();
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set YEARLY_AMOUNT='"+InstIII+"',VOUCHER_NO='"+InstIIINo+"',VOUCHER_DATE='"+timestampvouchedate+"' ");
				if(Deputation.equals("Y"))
					lSBQuery.append(" ,STATUS_FLAG='A',STATE_FLAG='10',Inst_dtls='"+cmbInstDtlsIII+"',Inst_VC_dtls='"+cmbVCDtlsIII+"',INST_TREASURY='"+cmbTreasuryIII+"',CHANGE_INST='"+chanInstIII+"' WHERE dcps_emp_id in(select dcps_emp_id from mst_Dcps_emp where SEVARTH_ID='"+SevarthID+"') and FIN_YEAR_ID='24' ");
		       else 
		            lSBQuery.append(" WHERE dcps_emp_id in(select dcps_emp_id from mst_Dcps_emp where SEVARTH_ID='"+SevarthID+"') and FIN_YEAR_ID='24' and STATUS_FLAG='A' "); 
		lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		
		parsedDate = dateFormat.parse(txtStartDateIV);
	    timestampvouchedate = new java.sql.Timestamp(parsedDate.getTime());
		lSBQuery = new StringBuilder();
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set YEARLY_AMOUNT='"+InstIV+"',VOUCHER_NO='"+InstIVNo+"',VOUCHER_DATE='"+timestampvouchedate+"' ");
				if(Deputation.equals("Y"))
					lSBQuery.append(" ,STATUS_FLAG='A',STATE_FLAG='10',Inst_dtls='"+cmbInstDtlsIV+"',Inst_VC_dtls='"+cmbVCDtlsIV+"',INST_TREASURY='"+cmbTreasuryIV+"',CHANGE_INST='"+chanInstIV+"' WHERE dcps_emp_id in(select dcps_emp_id from mst_Dcps_emp where SEVARTH_ID='"+SevarthID+"') and FIN_YEAR_ID='25' ");
		       else 
		            lSBQuery.append(" WHERE dcps_emp_id in(select dcps_emp_id from mst_Dcps_emp where SEVARTH_ID='"+SevarthID+"') and FIN_YEAR_ID='25' and STATUS_FLAG='A' "); 
		lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		
		parsedDate = dateFormat.parse(txtStartDateV);
	    timestampvouchedate = new java.sql.Timestamp(parsedDate.getTime());
		lSBQuery = new StringBuilder();
		lSBQuery.append("update RLT_DCPS_SIXPC_YEARLY set YEARLY_AMOUNT='"+InstV+"',VOUCHER_NO='"+InstVNo+"',VOUCHER_DATE='"+timestampvouchedate+"' ");
				if(Deputation.equals("Y"))
					lSBQuery.append(" ,STATUS_FLAG='A',STATE_FLAG='10',Inst_dtls='"+cmbInstDtlsV+"',Inst_VC_dtls='"+cmbVCDtlsV+"',INST_TREASURY='"+cmbTreasuryV+"',CHANGE_INST='"+chanInstV+"' WHERE dcps_emp_id in(select dcps_emp_id from mst_Dcps_emp where SEVARTH_ID='"+SevarthID+"') and FIN_YEAR_ID='26' ");
		       else 
		            lSBQuery.append(" WHERE dcps_emp_id in(select dcps_emp_id from mst_Dcps_emp where SEVARTH_ID='"+SevarthID+"') and FIN_YEAR_ID='26' and STATUS_FLAG='A' "); 
		lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		
		lSBQuery = new StringBuilder();
		lSBQuery.append("update MST_DCPS_SIXPC SET TOTAL_AMOUNT='"+total+"',AMOUNT_PAID='"+total+"' WHERE dcps_emp_id in(select dcps_emp_id from mst_Dcps_emp where SEVARTH_ID='"+SevarthID+"') ");
		lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		
	}
	public List getEmpListForUpdateInstDeputation(String searchSeva,String searchEmp, String searchEmp2) {
		List lLstEmpFiveInst = null;
		StringBuilder lSBQuery = null;
			try {
				lSBQuery = new StringBuilder();
				
				lSBQuery.append(" SELECT diSTINCT t.DCPS_EMP_ID,empmst.EMP_NAME,empmst.SEVARTH_ID,empmst.dcps_id,t8.fin_year_id,cast(t8.yearly_amount as varchar) as TimeA,cast(nvl(t8.VOUCHER_NO,0) as varchar) as TAV,cast(nvl(trunc(t8.VOUCHER_DATE), sysdate) as TIMESTAMP) as TAD,t2.fin_year_id,cast(t2.yearly_amount as varchar) as TimeB,cast(nvl(t2.VOUCHER_NO,0) as varchar) as TBV,cast(nvl(trunc(t2.VOUCHER_DATE), sysdate) as TIMESTAMP) as TBD,t3.fin_year_id,cast(t3.yearly_amount as varchar) as TimeC,cast(nvl(t3.VOUCHER_NO,0) as varchar) as TCV,cast(nvl(trunc(t3.VOUCHER_DATE), sysdate) as TIMESTAMP) as TCD,  "
						+ " t4.fin_year_id,cast(t4.yearly_amount as varchar) as TimeD,cast(nvl(t4.VOUCHER_NO,0) as varchar) as TDV,cast(nvl(trunc(t4.VOUCHER_DATE), sysdate) as TIMESTAMP) as TDD,t5.fin_year_id,cast(t5.yearly_amount as varchar) as TimeE,cast(nvl(t5.VOUCHER_NO,0) as varchar) as TEV,cast(nvl(trunc(t5.VOUCHER_DATE), sysdate) as TIMESTAMP) as TED,nvl(t2.REASON,'NA'),nvl(t8.INST_DTLS,'NA'),nvl(t2.INST_DTLS,'NA'),nvl(t3.INST_DTLS,'NA'),nvl(t4.INST_DTLS,'NA'),nvl(t5.INST_DTLS,'NA'),nvl(t8.Inst_VC_dtls,'NA'),nvl(t2.Inst_VC_dtls,'NA'),nvl(t3.Inst_VC_dtls,'NA'),nvl(t4.Inst_VC_dtls,'NA'),nvl(t5.Inst_VC_dtls,'NA'),nvl(t8.INST_TREASURY,'NA'),nvl(t2.INST_TREASURY,'NA'),nvl(t3.INST_TREASURY,'NA'),nvl(t4.INST_TREASURY,'NA'),nvl(t5.INST_TREASURY,'NA'),nvl(t3.REASON,'NA') ");
				lSBQuery.append(" FROM RLT_DCPS_SIXPC_YEARLY t ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t8 ON t.DCPS_EMP_ID=t8.DCPS_EMP_ID AND t8.fin_year_id=22 and t8.bill_no is null and t8.state_flag=10 ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t2 ON t.DCPS_EMP_ID=t2.DCPS_EMP_ID AND t2.fin_year_id=23 and t2.state_flag=10 ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t3 ON t.DCPS_EMP_ID=t3.DCPS_EMP_ID AND t3.fin_year_id=24 and t3.state_flag=10 ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t4 ON t.DCPS_EMP_ID=t4.DCPS_EMP_ID AND t4.fin_year_id=25 and t4.state_flag=10 ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t5 ON t.DCPS_EMP_ID=t5.DCPS_EMP_ID AND t5.fin_year_id=26 and t5.state_flag=10 ");
				lSBQuery.append(" inner join MST_DCPS_EMP empmst ");
				lSBQuery.append(" on t.dcps_emp_id=empmst.dcps_emp_id join TIERII_ATTACH_DETACH_DTLS attach on empmst.DCPS_EMP_ID=attach.DCPS_EMP_ID ");
				
				if(!searchSeva.equals(""))
				lSBQuery.append(" where attach.loc_id in(SELECT ddo_code FROM org_ddo_mst where POST_ID='"+searchEmp2+"' and ACTIVATE_FLAG=1 fetch first rows only) and t.DCPS_EMP_ID in(select dcps_emp_id from MST_DCPS_EMP where dcps_or_gpf='Y' and reg_status=1 and (DOJ >='2005-11-01' and DOJ <='2009-03-31') and SUPER_ANN_DATE >'2021-01-31' and sevarth_id='"+searchSeva+"') ");
				else if(!searchEmp.equals(""))
				lSBQuery.append(" where attach.loc_id in(SELECT ddo_code FROM org_ddo_mst where POST_ID='"+searchEmp2+"' and ACTIVATE_FLAG=1 fetch first rows only) and t.DCPS_EMP_ID in(select dcps_emp_id from MST_DCPS_EMP where dcps_or_gpf='Y' and reg_status=1 and (DOJ >='2005-11-01' and DOJ <='2009-03-31') and SUPER_ANN_DATE >'2021-01-31' and emp_name like '%"+searchEmp+"%') ");
				
				lSBQuery.append(" ORDER BY t.DCPS_EMP_ID ASC ");

				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
			   /* Object[] obj=(Object[]) lLstEmpFiveInst.get(0);
			    for (int i = 0; i < obj.length; i++) {
			            logger.info("lLstEmp val-->"+i+"-->" + obj[i].toString());
			        }*/
				
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		
		return lLstEmpFiveInst;
	}

	public List getEmpListForUpdateInstDeputationNoRlt(String searchSeva,String searchEmp, String val, String gStrPostId) {
		List lLstEmpFiveInst = null;
		StringBuilder lSBQuery = null;
			try {
				lSBQuery = new StringBuilder();
				String flag="N";
				
				if(val.equals("noRlt")){
					lSBQuery.append(" SELECT bill_no FROM RLT_DCPS_SIXPC_YEARLY where DCPS_EMP_ID in(SELECT empmst.dcps_emp_id FROM MST_DCPS_EMP empmst where empmst.DDO_CODE is null and empmst.dcps_or_gpf='Y' and empmst.reg_status=1 and empmst.SUPER_ANN_DATE >'2021-01-31'  ");
					if(!searchSeva.equals(""))
					lSBQuery.append(" and (empmst.DOJ >='2005-11-01' and empmst.DOJ <='2009-03-31') and empmst.sevarth_id='"+searchSeva+"') and fin_year_id=22  ");
					else if(!searchEmp.equals(""))
					lSBQuery.append(" and (empmst.DOJ >='2005-11-01' and empmst.DOJ <='2009-03-31') and empmst.emp_name like '%"+searchEmp+"%') and fin_year_id=22 ");
					Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
					lLstEmpFiveInst = stQuery.list();
					logger.info("lLstEmp count-1->" + lLstEmpFiveInst.size());
					
				if(lLstEmpFiveInst==null || lLstEmpFiveInst.size()<1 ){
				lSBQuery = new StringBuilder();
				lSBQuery.append(" SELECT empmst.EMP_NAME,empmst.SEVARTH_ID,empmst.dcps_id,case when empmst.dept_ddo_Code is null then 'N/A' else empmst.dept_ddo_Code end,empmst.dcps_emp_id FROM MST_DCPS_EMP empmst join TIERII_ATTACH_DETACH_DTLS attach on empmst.DCPS_EMP_ID=attach.DCPS_EMP_ID and attach.loc_id in(SELECT ddo_code FROM org_ddo_mst where POST_ID='"+gStrPostId+"' and ACTIVATE_FLAG=1 fetch first rows only) ");
				flag="Y";
				}
				}else{
				lSBQuery.append(" SELECT empmst.EMP_NAME,empmst.SEVARTH_ID,empmst.dcps_id,case when empmst.dept_ddo_Code is null then 'N/A' else empmst.dept_ddo_Code end,empmst.dcps_emp_id FROM MST_DCPS_EMP empmst ");
				flag="Y";
				}
				if(!searchSeva.equals("") && flag.equals("Y")){
				lSBQuery.append(" where empmst.DDO_CODE is null and empmst.dcps_or_gpf='Y' and empmst.reg_status=1 and empmst.SUPER_ANN_DATE >'2021-01-31' and (empmst.DOJ >='2005-11-01' and empmst.DOJ <='2009-03-31') and empmst.sevarth_id='"+searchSeva+"' ");
				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-2->" + lLstEmpFiveInst.size());
				}else if(!searchEmp.equals("") && flag.equals("Y")){
				lSBQuery.append(" where empmst.DDO_CODE is null and empmst.dcps_or_gpf='Y' and empmst.reg_status=1 and empmst.SUPER_ANN_DATE >'2021-01-31' and (empmst.DOJ >='2005-11-01' and empmst.DOJ <='2009-03-31') and empmst.emp_name like '%"+searchEmp+"%' ");
				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				}else{
				lLstEmpFiveInst=null;
				}
				logger.info("lLstEmp count-3->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		return lLstEmpFiveInst;
	}
	
	public void insertFiveInstAmtTO(String SevarthID,String Reason,String Total,String InstI,String InstINo,String txtStartDateI,String InstII,String InstIINo,String txtStartDateII,String InstIII
			,String InstIIINo,String txtStartDateIII,String InstIV,String InstIVNo,String txtStartDateIV,String InstV,String InstVNo,String txtStartDateV,String Deputation
			,String gStrLocationCode,String gStrPostId,String gStrUserId,String Treasury, String cmbInstDtlsI, String cmbInstDtlsII, String cmbInstDtlsIII, String cmbInstDtlsIV, String cmbInstDtlsV
			, String cmbVCDtlsI, String cmbVCDtlsII, String cmbVCDtlsIII, String cmbVCDtlsIV, String cmbVCDtlsV
			, String cmbTreasuryI, String cmbTreasuryII, String cmbTreasuryIII, String cmbTreasuryIV, String cmbTreasuryV) throws ParseException{
		org.hibernate.Session session = this.getSession();
		StringBuilder lSBQuery = new StringBuilder();
		List lLstEmpFiveInst = null;
		Object[] empDetails = null;
		lSBQuery.append(" SELECT dcps_emp_id,doj FROM MST_DCPS_EMP where sevarth_id='"+SevarthID+"' ");
		SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lLstEmpFiveInst = lQuery.list();
		if(lLstEmpFiveInst!=null && lLstEmpFiveInst.size()>0){
			empDetails=(Object[]) lLstEmpFiveInst.get(0);
		}
		
       if(InstI.trim().equals("0"))
		InstINo="0000";
       if(InstII.trim().equals("0"))
		InstIINo="0000";
       if(InstIII.trim().equals("0"))
		InstIIINo="0000";
       if(InstIV.trim().equals("0"))
		InstIVNo="0000";
       if(InstV.trim().equals("0"))
		InstVNo="0000";
       
       if(cmbVCDtlsI.trim().equals("-1")||cmbVCDtlsI.trim().equals(""))
    	   cmbVCDtlsI="NA ";
       if(cmbVCDtlsII.trim().equals("-1")||cmbVCDtlsII.trim().equals(""))
        	  cmbVCDtlsII="NA ";
       if(cmbVCDtlsIII.trim().equals("-1")||cmbVCDtlsIII.trim().equals(""))
        	  cmbVCDtlsIII="NA ";
       if(cmbVCDtlsIV.trim().equals("-1")||cmbVCDtlsIV.trim().equals(""))
        	  cmbVCDtlsIV="NA ";
       if(cmbVCDtlsV.trim().equals("-1")||cmbVCDtlsV.trim().equals(""))
        	  cmbVCDtlsV="NA ";
	
          if(cmbTreasuryI.trim().equals("-1")||cmbTreasuryI.trim().equals(""))
        	  cmbTreasuryI="NA ";
             if(cmbTreasuryII.trim().equals("-1")||cmbTreasuryII.trim().equals(""))
            	 cmbTreasuryII="NA ";
             if(cmbTreasuryIII.trim().equals("-1")||cmbTreasuryIII.trim().equals(""))
            	 cmbTreasuryIII="NA ";
             if(cmbTreasuryIV.trim().equals("-1")||cmbTreasuryIV.trim().equals(""))
            	 cmbTreasuryIV="NA ";
             if(cmbTreasuryV.trim().equals("-1")||cmbTreasuryV.trim().equals(""))
            	 cmbTreasuryV="NA ";
		
		List ddoDetails;
		Object[] ddoDtls = null;
		lSBQuery = new StringBuilder();
		/*lSBQuery.append(" SELECT attach.LOC_ID,mpg.DDO_CODE FROM TIERII_ATTACH_DETACH_DTLS attach join MST_TREASURY_DDOCODE_MPG mpg on attach.LOC_ID=mpg.LOC_ID ");
		lSBQuery.append(" join mst_Dcps_emp emp on attach.DCPS_EMP_ID=emp.DCPS_EMP_ID where emp.dcps_Emp_id='"+empDetails[0]+"' and mpg.ACTIVE_FLAG = 1 ");*/
		lSBQuery.append(" SELECT substr(attach.LOC_ID,1,4),attach.LOC_ID FROM TIERII_ATTACH_DETACH_DTLS attach ");
		lSBQuery.append(" join mst_Dcps_emp emp on attach.DCPS_EMP_ID=emp.DCPS_EMP_ID where emp.dcps_Emp_id='"+empDetails[0]+"' ");
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		ddoDetails = lQuery.list();
		if(ddoDetails!=null && ddoDetails.size()>0)
			ddoDtls=(Object[]) ddoDetails.get(0);
				
	    int total=Integer.valueOf(InstI)+Integer.valueOf(InstII)+Integer.valueOf(InstIII)+Integer.valueOf(InstIV)+Integer.valueOf(InstV);
	    
	    lSBQuery = new StringBuilder();
	    lSBQuery.append(" INSERT INTO MST_DCPS_SIXPC (DCPS_SIXPC_ID,DCPS_EMP_ID,TOTAL_AMOUNT,AMOUNT_PAID,FROM_DATE,TO_DATE,LANG_ID,LOC_ID,DB_ID,CREATED_POST_ID,CREATED_USER_ID,CREATED_DATE,UPDATED_POST_ID,UPDATED_USER_ID,UPDATED_DATE,STATUS_FLAG,REMARKS,INSTALLMENT_LEFT,ORDER_NO,ORDER_DATE,STATUS_OLD_SEVAARTH) ");
        lSBQuery.append(" VALUES ((SELECT max(DCPS_SIXPC_ID)+1 FROM MST_DCPS_SIXPC),'"+empDetails[0]+"','"+total+"','"+total+"','"+empDetails[1]+"','2009-03-31',1,'"+ddoDtls[0]+"',99,1,1,sysdate,'"+gStrPostId+"','"+gStrUserId+"',null,'A',null,0,null,null,null) ");
        lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date parsedDate = dateFormat.parse(txtStartDateI.toString());
		Timestamp timestampvouchedate = new java.sql.Timestamp(parsedDate.getTime());
		lSBQuery = new StringBuilder();
	    lSBQuery.append(" INSERT INTO RLT_DCPS_SIXPC_YEARLY (DCPS_SIXPC_YEARLY_ID,DCPS_EMP_ID,YEARLY_AMOUNT,FIN_YEAR_ID,LANG_ID,LOC_ID,DB_ID,CREATED_POST_ID,CREATED_USER_ID,CREATED_DATE,UPDATED_POST_ID,UPDATED_USER_ID,UPDATED_DATE,STATUS_FLAG,REMARKS,ACTIVE_FLAG,SCHEDULE_ID,DDO_CODE,STATUS_OLD_SEVAARTH,VOUCHER_NO,VOUCHER_DATE,TREASURY,BILL_NO,INTEREST,STATE_FLAG,REASON,INST_DTLS,Inst_VC_dtls,INST_TREASURY,CHANGE_INST) ");
        lSBQuery.append(" VALUES ((SELECT max(DCPS_SIXPC_YEARLY_ID)+1 FROM RLT_DCPS_SIXPC_YEARLY),'"+empDetails[0]+"','"+InstI+"',22,1,'"+ddoDtls[0]+"',99,1,1,sysdate,'"+gStrPostId+"','"+gStrUserId+"',sysdate,'A',null,1,1,'"+ddoDtls[1]+"',null,'"+InstINo+"','"+timestampvouchedate+"','"+ddoDtls[0]+"',null,null,'10',null,'"+cmbInstDtlsI+"','"+cmbVCDtlsI+"','"+cmbTreasuryI+"','Y') ");
        lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		
		parsedDate = dateFormat.parse(txtStartDateII);
	    timestampvouchedate = new java.sql.Timestamp(parsedDate.getTime());
		lSBQuery = new StringBuilder();
	    lSBQuery.append(" INSERT INTO RLT_DCPS_SIXPC_YEARLY (DCPS_SIXPC_YEARLY_ID,DCPS_EMP_ID,YEARLY_AMOUNT,FIN_YEAR_ID,LANG_ID,LOC_ID,DB_ID,CREATED_POST_ID,CREATED_USER_ID,CREATED_DATE,UPDATED_POST_ID,UPDATED_USER_ID,UPDATED_DATE,STATUS_FLAG,REMARKS,ACTIVE_FLAG,SCHEDULE_ID,DDO_CODE,STATUS_OLD_SEVAARTH,VOUCHER_NO,VOUCHER_DATE,TREASURY,BILL_NO,INTEREST,STATE_FLAG,REASON,INST_DTLS,Inst_VC_dtls,INST_TREASURY,CHANGE_INST) ");
        lSBQuery.append(" VALUES ((SELECT max(DCPS_SIXPC_YEARLY_ID)+1 FROM RLT_DCPS_SIXPC_YEARLY),'"+empDetails[0]+"','"+InstII+"',23,1,'"+ddoDtls[0]+"',99,1,1,sysdate,'"+gStrPostId+"','"+gStrUserId+"',sysdate,'A',null,1,1,'"+ddoDtls[1]+"',null,'"+InstIINo+"','"+timestampvouchedate+"','"+ddoDtls[0]+"',null,null,'10',null,'"+cmbInstDtlsII+"','"+cmbVCDtlsII+"','"+cmbTreasuryII+"','Y') ");
        lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		
		parsedDate = dateFormat.parse(txtStartDateIII);
	    timestampvouchedate = new java.sql.Timestamp(parsedDate.getTime());
		lSBQuery = new StringBuilder();
	    lSBQuery.append(" INSERT INTO RLT_DCPS_SIXPC_YEARLY (DCPS_SIXPC_YEARLY_ID,DCPS_EMP_ID,YEARLY_AMOUNT,FIN_YEAR_ID,LANG_ID,LOC_ID,DB_ID,CREATED_POST_ID,CREATED_USER_ID,CREATED_DATE,UPDATED_POST_ID,UPDATED_USER_ID,UPDATED_DATE,STATUS_FLAG,REMARKS,ACTIVE_FLAG,SCHEDULE_ID,DDO_CODE,STATUS_OLD_SEVAARTH,VOUCHER_NO,VOUCHER_DATE,TREASURY,BILL_NO,INTEREST,STATE_FLAG,REASON,INST_DTLS,Inst_VC_dtls,INST_TREASURY,CHANGE_INST) ");
        lSBQuery.append(" VALUES ((SELECT max(DCPS_SIXPC_YEARLY_ID)+1 FROM RLT_DCPS_SIXPC_YEARLY),'"+empDetails[0]+"','"+InstIII+"',24,1,'"+ddoDtls[0]+"',99,1,1,sysdate,'"+gStrPostId+"','"+gStrUserId+"',sysdate,'A',null,1,1,'"+ddoDtls[1]+"',null,'"+InstIIINo+"','"+timestampvouchedate+"','"+ddoDtls[0]+"',null,null,'10','DeptNewRlt','"+cmbInstDtlsIII+"','"+cmbVCDtlsIII+"','"+cmbTreasuryIII+"','Y') ");
        lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		
		parsedDate = dateFormat.parse(txtStartDateIV);
	    timestampvouchedate = new java.sql.Timestamp(parsedDate.getTime());
		lSBQuery = new StringBuilder();
	    lSBQuery.append(" INSERT INTO RLT_DCPS_SIXPC_YEARLY (DCPS_SIXPC_YEARLY_ID,DCPS_EMP_ID,YEARLY_AMOUNT,FIN_YEAR_ID,LANG_ID,LOC_ID,DB_ID,CREATED_POST_ID,CREATED_USER_ID,CREATED_DATE,UPDATED_POST_ID,UPDATED_USER_ID,UPDATED_DATE,STATUS_FLAG,REMARKS,ACTIVE_FLAG,SCHEDULE_ID,DDO_CODE,STATUS_OLD_SEVAARTH,VOUCHER_NO,VOUCHER_DATE,TREASURY,BILL_NO,INTEREST,STATE_FLAG,REASON,INST_DTLS,Inst_VC_dtls,INST_TREASURY,CHANGE_INST) ");
        lSBQuery.append(" VALUES ((SELECT max(DCPS_SIXPC_YEARLY_ID)+1 FROM RLT_DCPS_SIXPC_YEARLY),'"+empDetails[0]+"','"+InstIV+"',25,1,'"+ddoDtls[0]+"',99,1,1,sysdate,'"+gStrPostId+"','"+gStrUserId+"',sysdate,'A',null,1,1,'"+ddoDtls[1]+"',null,'"+InstIVNo+"','"+timestampvouchedate+"','"+ddoDtls[0]+"',null,null,'10',null,'"+cmbInstDtlsIV+"','"+cmbVCDtlsIV+"','"+cmbTreasuryIV+"','Y') ");
        lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		
		parsedDate = dateFormat.parse(txtStartDateV);
	    timestampvouchedate = new java.sql.Timestamp(parsedDate.getTime());
		lSBQuery = new StringBuilder();
	    lSBQuery.append(" INSERT INTO RLT_DCPS_SIXPC_YEARLY (DCPS_SIXPC_YEARLY_ID,DCPS_EMP_ID,YEARLY_AMOUNT,FIN_YEAR_ID,LANG_ID,LOC_ID,DB_ID,CREATED_POST_ID,CREATED_USER_ID,CREATED_DATE,UPDATED_POST_ID,UPDATED_USER_ID,UPDATED_DATE,STATUS_FLAG,REMARKS,ACTIVE_FLAG,SCHEDULE_ID,DDO_CODE,STATUS_OLD_SEVAARTH,VOUCHER_NO,VOUCHER_DATE,TREASURY,BILL_NO,INTEREST,STATE_FLAG,REASON,INST_DTLS,Inst_VC_dtls,INST_TREASURY,CHANGE_INST) ");
        lSBQuery.append(" VALUES ((SELECT max(DCPS_SIXPC_YEARLY_ID)+1 FROM RLT_DCPS_SIXPC_YEARLY),'"+empDetails[0]+"','"+InstV+"',26,1,'"+ddoDtls[0]+"',99,1,1,sysdate,'"+gStrPostId+"','"+gStrUserId+"',sysdate,'A',null,1,1,'"+ddoDtls[1]+"',null,'"+InstVNo+"','"+timestampvouchedate+"','"+ddoDtls[0]+"',null,null,'10',null,'"+cmbInstDtlsV+"','"+cmbVCDtlsV+"','"+cmbTreasuryV+"','Y') ");
        lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
	}

	public List getTreasuryList() {
		String query = "select CM.locId , CM.locName from CmnLocationMst CM where departmentId = 100003 and CM.locId not in(1111,9991,2028915,99991) order by CM.locId";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());

		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	public void insertAttachTierIIEmp(String txtAttachDate,String cmbOfficeCode, String dcpsEmpId, String gStrPostId,String gStrUserId, String cmbOfficeName) throws ParseException {
		StringBuilder lSBQuery = new StringBuilder();
		
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date date = formatter.parse(txtAttachDate);
		Timestamp timeStampDate = new Timestamp(date.getTime());
		
		lSBQuery = new StringBuilder();
	    lSBQuery.append(" INSERT INTO TIERII_ATTACH_DETACH_DTLS(DCPS_EMP_ID,LOC_ID,ATTACH_DATE,CREATED_USER_ID,CREATED_POST_ID,CREATED_DATE,OFFICE_NAME) ");
        lSBQuery.append(" VALUES('"+dcpsEmpId+"','"+cmbOfficeCode+"','"+timeStampDate+"','"+gStrUserId+"','"+gStrPostId+"',sysdate,'"+cmbOfficeName+"') ");
        SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		
		lSBQuery = new StringBuilder();
		lSBQuery.append(" update RLT_DCPS_SIXPC_YEARLY set STATE_FLAG='10' WHERE dcps_emp_id ='"+dcpsEmpId+"' and FIN_YEAR_ID in('22','23','24','25','26') ");
        lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
        lQuery.executeUpdate();
		
	}

	public String checkEmpExistInAttach(String searchSeva, String searchEmp) {
		List lLstEmpFiveInst = null;
		StringBuilder lSBQuery = null;
		String status="no";
			try {
				lSBQuery = new StringBuilder();
				if(!searchSeva.equals(""))
				lSBQuery.append(" SELECT CREATED_POST_ID FROM TIERII_ATTACH_DETACH_DTLS where REASON_DETACH is null and dcps_emp_id in(select dcps_emp_id from mst_Dcps_emp where sevarth_id='"+searchSeva+"' and dcps_or_gpf='Y' and reg_status=1 and (DOJ >='2005-11-01' and DOJ <='2009-03-31') and SUPER_ANN_DATE >'2021-01-31') ");
				else if(!searchEmp.equals(""))
				lSBQuery.append(" SELECT CREATED_POST_ID FROM TIERII_ATTACH_DETACH_DTLS where REASON_DETACH is null and dcps_emp_id in(select dcps_emp_id from mst_Dcps_emp where emp_name like '%"+searchEmp+"%' and dcps_or_gpf='Y' and reg_status=1 and (DOJ >='2005-11-01' and DOJ <='2009-03-31') and SUPER_ANN_DATE >'2021-01-31') ");
				
				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				if(lLstEmpFiveInst.size()>0){
				lSBQuery = new StringBuilder();
				lSBQuery.append(" SELECT cmn.loc_name FROM ORG_POST_DETAILS_RLT post join CMN_LOCATION_MST cmn on post.LOC_ID=cmn.LOC_ID WHERE post.POST_ID='"+lLstEmpFiveInst.get(0)+"' ");
				stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				status=lLstEmpFiveInst.get(0).toString();
				}else{
				lSBQuery = new StringBuilder();
				if(!searchSeva.equals(""))
				lSBQuery.append(" SELECT case when BILL_no is null then 'A' else 'B' end FROM RLT_DCPS_SIXPC_YEARLY where fin_year_id=22 and dcps_emp_id in(select dcps_emp_id from mst_Dcps_emp where sevarth_id='"+searchSeva+"') ");
				else if(!searchEmp.equals(""))
				lSBQuery.append(" SELECT case when BILL_no is null then 'A' else 'B' end FROM RLT_DCPS_SIXPC_YEARLY where fin_year_id=22 and dcps_emp_id in(select dcps_emp_id from mst_Dcps_emp where emp_name like '%"+searchEmp+"%') ");
				stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				if(lLstEmpFiveInst.size()>0){
				status=lLstEmpFiveInst.get(0).toString();
				if(status.equals("A"))
				status="no";
				}
			  }
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		return status;
	}
	
	public List getEmpListForFiveInstApproveDeputation(String strDDOCode,
			String BillNo,String strRoleId,String interestApproved,String searchSeva,String searchEmp, String gStrPostId, String noData) {

		List lLstEmpFiveInst = null;
		// List lLstEmp = null;
		StringBuilder lSBQuery = null;
		// StringBuilder lSBQuery1 = null;
		// String postId;
		// postId = gStrLocationCode;
		logger.info("strRoleId**********"+ strRoleId);
		System.out.println("role Id--->"+strRoleId);
		if (strRoleId.equals("700008")) {
			try {
				System.out.println("inside method");
				lSBQuery = new StringBuilder();

				lSBQuery.append(" SELECT diSTINCT t.DCPS_EMP_ID,t8.fin_year_id,cast(t8.yearly_amount as varchar) as TimeA,t2.fin_year_id,cast(t2.yearly_amount as varchar) as TimeB,t3.fin_year_id,cast(t3.yearly_amount as varchar) as TimeC,"
						+ "t4.fin_year_id,cast(t4.yearly_amount as varchar) as TimeD,t5.fin_year_id,cast(t5.yearly_amount as varchar) as TimeE ,empmst.EMP_NAME,empmst.SEVARTH_ID,empmst.DCPS_ID,case when empmst.PRAN_NO is null then 'N/A' else empmst.PRAN_NO end ,t8.interest, case when t8.bill_no is null then 'N' else 'Y' end ");
				lSBQuery.append(" FROM RLT_DCPS_SIXPC_YEARLY t ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t8 ON t.DCPS_EMP_ID=t8.DCPS_EMP_ID AND t8.fin_year_id=22 and  t8.status_flag='A' and t8.state_flag='12' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t2 ON t.DCPS_EMP_ID=t2.DCPS_EMP_ID AND t2.fin_year_id=23 and  t2.status_flag='A' and t2.state_flag='12' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t3 ON t.DCPS_EMP_ID=t3.DCPS_EMP_ID AND t3.fin_year_id=24 and  t3.status_flag='A' and t3.state_flag='12' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t4 ON t.DCPS_EMP_ID=t4.DCPS_EMP_ID AND t4.fin_year_id=25 and  t4.status_flag='A' and t4.state_flag='12' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t5 ON t.DCPS_EMP_ID=t5.DCPS_EMP_ID AND t5.fin_year_id=26 and  t5.status_flag='A' and t5.state_flag='12' ");
				lSBQuery.append(" inner join MST_DCPS_EMP empmst ");
				lSBQuery.append(" on t.dcps_emp_id=empmst.dcps_emp_id ");
				lSBQuery.append(" where t.status_flag='A' and t.DCPS_EMP_ID in(select emp.dcps_emp_id from MST_DCPS_EMP emp join TIERII_ATTACH_DETACH_DTLS dtls on emp.DCPS_EMP_ID=dtls.DCPS_EMP_ID where emp.dcps_or_gpf='Y' and emp.reg_status=1 and (emp.DOJ >='2005-11-01' and emp.DOJ <='2009-03-31') and emp.SUPER_ANN_DATE >'2021-01-31' and dtls.DETACH_DATE is null  ");
				lSBQuery.append(" and dtls.REASON_DETACH is null and dtls.CREATED_POST_ID='"+gStrPostId+"' and dtls.loc_id='"+strDDOCode+"' ) and t.state_flag='12' ");
				lSBQuery.append(" ORDER BY t.DCPS_EMP_ID ASC ");
				logger.info("Query to DDO lLstEmpFiveInst in  heaqder**********"+ lSBQuery.toString());
				System.out.println("role Id--->"+lSBQuery.toString());

				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				logger.info("Query to DDO lLstEmpFiveInst in  heaqder**********"+ lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		}
		else if (strRoleId.equals("100018")) {
			try {
				System.out.println("inside method");
				lSBQuery = new StringBuilder();

				lSBQuery.append(" SELECT diSTINCT t.DCPS_EMP_ID,t8.fin_year_id,cast(t8.yearly_amount as varchar) as TimeA,t2.fin_year_id,cast(t2.yearly_amount as varchar) as TimeB,t3.fin_year_id,cast(t3.yearly_amount as varchar) as TimeC,"
						+ "t4.fin_year_id,cast(t4.yearly_amount as varchar) as TimeD,t5.fin_year_id,cast(t5.yearly_amount as varchar) as TimeE ,empmst.EMP_NAME,empmst.SEVARTH_ID,empmst.DCPS_ID,case when empmst.PRAN_NO is null then 'N/A' else empmst.PRAN_NO end ,t8.interest,case when t8.reason is null then '.' else  t8.reason end, case when t8.bill_no is null then 'N' else 'Y' end,decode(t8.CHANGE_INST,'Y','Y','N','N',null,'N'),decode(t2.CHANGE_INST,'Y','Y','N','N',null,'N'),decode(t3.CHANGE_INST,'Y','Y','N','N',null,'N'),decode(t4.CHANGE_INST,'Y','Y','N','N',null,'N'),decode(t5.CHANGE_INST,'Y','Y','N','N',null,'N') ");
				lSBQuery.append(" FROM RLT_DCPS_SIXPC_YEARLY t ");
				if(interestApproved.equals("Y"))
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t8 ON t.DCPS_EMP_ID=t8.DCPS_EMP_ID AND t8.fin_year_id=22 and  t8.status_flag='A' ");//and t8.BILL_NO is null
				else
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t8 ON t.DCPS_EMP_ID=t8.DCPS_EMP_ID AND t8.fin_year_id=22 and  t8.status_flag='A' and t8.BILL_NO is null ");//and t8.BILL_NO is null
				
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t2 ON t.DCPS_EMP_ID=t2.DCPS_EMP_ID AND t2.fin_year_id=23 and  t2.status_flag='A' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t3 ON t.DCPS_EMP_ID=t3.DCPS_EMP_ID AND t3.fin_year_id=24 and  t3.status_flag='A' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t4 ON t.DCPS_EMP_ID=t4.DCPS_EMP_ID AND t4.fin_year_id=25 and  t4.status_flag='A' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t5 ON t.DCPS_EMP_ID=t5.DCPS_EMP_ID AND t5.fin_year_id=26 and  t5.status_flag='A' ");
				lSBQuery.append(" inner join MST_DCPS_EMP empmst ");
				lSBQuery.append(" on t.dcps_emp_id=empmst.dcps_emp_id ");
				
				if(!searchSeva.equals("")){
					lSBQuery.append(" where t.status_flag='A' and t.DCPS_EMP_ID in(select emp.dcps_emp_id from MST_DCPS_EMP emp join TIERII_ATTACH_DETACH_DTLS dtls on emp.DCPS_EMP_ID=dtls.DCPS_EMP_ID where dtls.LOC_ID ='" + strDDOCode + "' and emp.dcps_or_gpf='Y' and emp.sevarth_id='"+searchSeva+"' and emp.reg_status=1 and (emp.DOJ >='2005-11-01' and emp.DOJ <='2009-03-31') and emp.SUPER_ANN_DATE >'2021-01-31' and dtls.DETACH_DATE is null and dtls.REASON_DETACH is null and dtls.CREATED_POST_ID='"+gStrPostId+"') and ((t.state_flag in ('10','11','12')) or (t.state_flag is null)) ORDER BY t8.interest asc ");
				}else if(!searchEmp.equals("")){
					lSBQuery.append(" where t.status_flag='A' and t.DCPS_EMP_ID in(select emp.dcps_emp_id from MST_DCPS_EMP emp join TIERII_ATTACH_DETACH_DTLS dtls on emp.DCPS_EMP_ID=dtls.DCPS_EMP_ID where dtls.LOC_ID ='" + strDDOCode + "' and emp.dcps_or_gpf='Y' and emp.emp_name like '"+searchEmp+"%' and emp.reg_status=1 and (emp.DOJ >='2005-11-01' and emp.DOJ <='2009-03-31') and emp.SUPER_ANN_DATE >'2021-01-31' and dtls.DETACH_DATE is null and dtls.REASON_DETACH is null and dtls.CREATED_POST_ID='"+gStrPostId+"') and ((t.state_flag in ('10','11','12')) or (t.state_flag is null)) ORDER BY t8.interest asc ");
				}else{
				lSBQuery.append(" where t.status_flag='A' and t.DCPS_EMP_ID in(select emp.dcps_emp_id from MST_DCPS_EMP emp join TIERII_ATTACH_DETACH_DTLS dtls on emp.DCPS_EMP_ID=dtls.DCPS_EMP_ID where dtls.LOC_ID ='" + strDDOCode + "' and emp.dcps_or_gpf='Y' and emp.reg_status=1 and (emp.DOJ >='2005-11-01' and emp.DOJ <='2009-03-31') and emp.SUPER_ANN_DATE >'2021-01-31' and dtls.DETACH_DATE is null and dtls.REASON_DETACH is null and dtls.CREATED_POST_ID='"+gStrPostId+"') and ((t.state_flag in ('10','11','12')) or (t.state_flag is null)) ORDER BY t8.interest asc ");
			 }
				logger.info("Query to DDO lLstEmpFiveInst in  heaqder**********"+ lSBQuery.toString());
				System.out.println("role Id--->"+lSBQuery.toString());

				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				logger.info("Query to DDO lLstEmpFiveInst in  heaqder**********"+ lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		} else if(strRoleId.equals("700003")) {
			try {

				System.out.println("inside method");
				lSBQuery = new StringBuilder();

				lSBQuery.append(" SELECT diSTINCT t.DCPS_EMP_ID,t8.fin_year_id,cast(t8.yearly_amount as varchar) as TimeA,t2.fin_year_id,cast(t2.yearly_amount as varchar) as TimeB,t3.fin_year_id,cast(t3.yearly_amount as varchar) as TimeC,"
						+ "t4.fin_year_id,cast(t4.yearly_amount as varchar) as TimeD,t5.fin_year_id,cast(t5.yearly_amount as varchar) as TimeE ,empmst.EMP_NAME,empmst.SEVARTH_ID,empmst.DCPS_ID,case when empmst.PRAN_NO is null then 'N/A' else empmst.PRAN_NO end ,t8.interest,case when t8.reason is null then '.' else t8.reason end, case when t8.bill_no is null then 'N' else 'Y' end,decode(t8.CHANGE_INST,'Y','Y','N','N',null,'N'),decode(t2.CHANGE_INST,'Y','Y','N','N',null,'N'),decode(t3.CHANGE_INST,'Y','Y','N','N',null,'N'),decode(t4.CHANGE_INST,'Y','Y','N','N',null,'N'),decode(t5.CHANGE_INST,'Y','Y','N','N',null,'N') ");
				lSBQuery.append(" FROM RLT_DCPS_SIXPC_YEARLY t ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t8 ON t.DCPS_EMP_ID=t8.DCPS_EMP_ID AND t8.fin_year_id=22 and  t8.status_flag='A' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t2 ON t.DCPS_EMP_ID=t2.DCPS_EMP_ID AND t2.fin_year_id=23 and  t2.status_flag='A' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t3 ON t.DCPS_EMP_ID=t3.DCPS_EMP_ID AND t3.fin_year_id=24 and  t3.status_flag='A' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t4 ON t.DCPS_EMP_ID=t4.DCPS_EMP_ID AND t4.fin_year_id=25 and  t4.status_flag='A' ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t5 ON t.DCPS_EMP_ID=t5.DCPS_EMP_ID AND t5.fin_year_id=26 and  t5.status_flag='A' ");
				lSBQuery.append(" inner join MST_DCPS_EMP empmst ");
				lSBQuery.append(" on t.dcps_emp_id=empmst.dcps_emp_id ");
				if(!searchSeva.equals("")){
					lSBQuery.append(" where t.status_flag='A' and t.DCPS_EMP_ID in(select emp.dcps_emp_id from MST_DCPS_EMP emp join TIERII_ATTACH_DETACH_DTLS dtls on emp.DCPS_EMP_ID=dtls.DCPS_EMP_ID where dtls.LOC_ID ='" + strDDOCode + "' and emp.dcps_or_gpf='Y' and emp.reg_status=1 and (emp.DOJ >='2005-11-01' and emp.DOJ <='2009-03-31') and emp.SUPER_ANN_DATE >'2021-01-31' and dtls.DETACH_DATE is null and dtls.REASON_DETACH is null and SEVARTH_ID='" + searchSeva + "' and dtls.CREATED_POST_ID='"+gStrPostId+"') and ((t.state_flag in ('10','11','12')) or (t.state_flag is null)) ");
					}else if(!searchEmp.equals("")){
					lSBQuery.append(" where t.status_flag='A' and t.DCPS_EMP_ID in(select emp.dcps_emp_id from MST_DCPS_EMP emp join TIERII_ATTACH_DETACH_DTLS dtls on emp.DCPS_EMP_ID=dtls.DCPS_EMP_ID where dtls.LOC_ID ='" + strDDOCode + "' and emp.dcps_or_gpf='Y' and emp.reg_status=1 and (emp.DOJ >='2005-11-01' and emp.DOJ <='2009-03-31') and emp.SUPER_ANN_DATE >'2021-01-31' and dtls.DETACH_DATE is null and dtls.REASON_DETACH is null and emp_name like '%" + searchEmp + "%' and dtls.CREATED_POST_ID='"+gStrPostId+"' ) and ((t.state_flag in ('10','11','12')) or (t.state_flag is null)) ");
					}else{
					lSBQuery.append(" where t.status_flag='A' and t.DCPS_EMP_ID in(select emp.dcps_emp_id from MST_DCPS_EMP emp join TIERII_ATTACH_DETACH_DTLS dtls on emp.DCPS_EMP_ID=dtls.DCPS_EMP_ID where dtls.LOC_ID ='" + strDDOCode + "' and emp.dcps_or_gpf='Y' and emp.reg_status=1 and (emp.DOJ >='2005-11-01' and emp.DOJ <='2009-03-31') and emp.SUPER_ANN_DATE >'2021-01-31' and dtls.DETACH_DATE is null and dtls.REASON_DETACH is null ) and t.state_flag in ('11') and t.BILL_NO='"+BillNo+"' ");
					}
				
				lSBQuery.append(" ORDER BY t.DCPS_EMP_ID ASC ");
				logger.info("Query to DDO lLstEmpFiveInst in  heaqder**********"+ lSBQuery.toString());
				System.out.println("role Id--->"+lSBQuery.toString());

				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				logger.info("Query to DDO lLstEmpFiveInst in  heaqder**********"+ lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			
				
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}

		}else if(strRoleId.equals("700004")) {
			try {
				lSBQuery = new StringBuilder();

				lSBQuery.append(" SELECT diSTINCT t.DCPS_EMP_ID,t8.fin_year_id,t8.yearly_amount AS TimeA,t2.fin_year_id,t2.yearly_amount AS TimeB,t3.fin_year_id,t3.yearly_amount AS TimeC,"
						+ "t4.fin_year_id,t4.yearly_amount AS TimeD,t5.fin_year_id,t5.yearly_amount AS TimeE ,empmst.EMP_NAME,empmst.SEVARTH_ID,empmst.DCPS_ID ,case when t8.bill_no is null then 'N' else 'Y' end ");
				lSBQuery.append(" FROM RLT_DCPS_SIXPC_YEARLY t ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t8 ON t.DCPS_EMP_ID=t8.DCPS_EMP_ID AND t8.fin_year_id=22 ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t2 ON t.DCPS_EMP_ID=t2.DCPS_EMP_ID AND t2.fin_year_id=23 ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t3 ON t.DCPS_EMP_ID=t3.DCPS_EMP_ID AND t3.fin_year_id=24 ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t4 ON t.DCPS_EMP_ID=t4.DCPS_EMP_ID AND t4.fin_year_id=25 ");
				lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t5 ON t.DCPS_EMP_ID=t5.DCPS_EMP_ID AND t5.fin_year_id=26 ");
				lSBQuery.append(" inner join MST_DCPS_EMP empmst ");
				lSBQuery.append(" on t.dcps_emp_id=empmst.dcps_emp_id ");
				lSBQuery.append(" where t.state_flag='2' and (empmst.DOJ >='2005-11-01' and empmst.DOJ <='2009-03-31') and empmst.SUPER_ANN_DATE >'2021-01-31' ");
				lSBQuery.append(" ORDER BY t.DCPS_EMP_ID ASC ");

				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				logger.info("Query to srka lLstEmpFiveInst in  heaqder**********"+ lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}			
		}
		return lLstEmpFiveInst;
	}
	
	public String getDdoCodeForDDO(String dcps_emp_id) {
		String lStrDdoCode = null;
		List lLstDdoDtls = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			/*lSBQuery.append(" select DDO_CODE from MST_TREASURY_DDOCODE_MPG WHERE ACTIVE_FLAG=1 ");
			lSBQuery.append(" and LOC_ID in(SELECT LOC_ID FROM TIERII_ATTACH_DETACH_DTLS where DCPS_EMP_ID='"+dcps_emp_id+"' and DETACH_DATE is null and REASON_DETACH is null) ");*/
			
			lSBQuery.append(" (SELECT LOC_ID FROM TIERII_ATTACH_DETACH_DTLS where DCPS_EMP_ID='"+dcps_emp_id+"' and DETACH_DATE is null and REASON_DETACH is null) ");
			
			Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			logger.info("Query to srka lLstEmpFiveInst in  heaqder**********"+ lSBQuery.toString());
			lLstDdoDtls = stQuery.list();
		    lStrDdoCode =lLstDdoDtls.get(0).toString();
            } catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}
		return lStrDdoCode;
	}
	
	public String generateOrderFDept(String strDDOCode, String interest,
			String totalAMount, String grandTotalAmount, int length,String DcpsId,String ArrayDcpsID,String ArrayTotalAmount,String ArrayInterest) {
		String Status="Y";
		try{
		int year = Calendar.getInstance().get(Calendar.YEAR);
		 int month = Calendar.getInstance().get(Calendar.MONTH)+1;
		  String Count = "";
		    StringBuilder Strbld = new StringBuilder();
		    Strbld.append(" SELECT count(1)+1 FROM TIERTWO_NAMUMNA_F where DDO_CODE ='"+strDDOCode+"' and BILL_YEAR='"+year+"' and BILL_MONTH = '"+month+"' ");
		    SQLQuery lSEQQuery = this.ghibSession.createSQLQuery(Strbld.toString());
		    Count = lSEQQuery.list().get(0).toString();
		    String Seq=strDDOCode+year+month+"00"+Count;
		    Status=Seq;
		    
		StringBuilder lSBQuery = new StringBuilder();
		Double Total =Double.parseDouble(totalAMount)+Double.parseDouble(interest);
		
		lSBQuery.append(" insert into TIERTWO_NAMUMNA_F values( '"+Seq+"', '"+year+"', '"+month+"','"+totalAMount+"','"+interest+"','"+length+"',sysdate,'"+strDDOCode+"','"+Total+"',11) ");
		SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.executeUpdate();
		
		//for insert bill details
		String DcpsID_split[] = ArrayDcpsID.split("~");
		String TotalAmount_split[] = ArrayTotalAmount.split("~");
		String Interest_split[] = ArrayInterest.split("~");
		
		//To get sequence
		 StringBuilder StrSeqCount = new StringBuilder();
		 StrSeqCount.append(" select count(*) from TIERII_NAMUMNA_F_EMP_DETAILS where DDO_CODE ='"+strDDOCode+"' ");
		 SQLQuery SQLCount = this.ghibSession.createSQLQuery(StrSeqCount.toString());
		 int SeqCount = Integer.parseInt(SQLCount.list().get(0).toString());

		if (ArrayDcpsID != null && DcpsID_split.length > 0) {
			for (Integer lInt = 0; lInt < Interest_split.length; lInt++) {
				String SeqCount1=strDDOCode+SeqCount;
				String dcpsId=DcpsID_split[lInt];
				String instAmount=TotalAmount_split[lInt];
				String intAmount=Interest_split[lInt];
				StringBuilder lSBQueryBillDetails = new StringBuilder();
				lSBQueryBillDetails.append("INSERT INTO TIERII_NAMUMNA_F_EMP_DETAILS  "); 
				lSBQueryBillDetails.append("VALUES ( '"+SeqCount1+"','"+dcpsId+"','"+instAmount+"',"+intAmount+",'"+Seq+"','"+strDDOCode+"','"+year+"', '"+month+"',null,sysdate,null,null,null,null,10,null) ");
				SQLQuery lQueryBillDetails = ghibSession.createSQLQuery(lSBQueryBillDetails.toString());
				lQueryBillDetails.executeUpdate();
				SeqCount++;
			}
		 }
		}
		catch(Exception e){
			System.out.println("Exception e"+e);
		}
		return Status;
	}
	

	public List getTierIIOrderFDept(String ddoCode,String post_id,String month,String year,String login,String searchSeva,String searchEmp,String locId) {
		List lLstEmpFiveInst = null;
		List lLstEmpFiveInstNew = null;
		String value=null;
		StringBuilder lSBQuery = null;
			try {
				lSBQuery = new StringBuilder();
				if(login.equals("Treasury") || ((searchSeva.length()>0 && locId.length()==4)||(searchEmp.length()>0 && locId.length()==4)) ){/////$tDept
					
				  if(!searchSeva.equals("")){	
				  lSBQuery.append(" select dtls.BILL_ID,dtls.BILL_ID,dtls.BILL_year,dtls.bill_month,dtls.ddo_code,dtls.TIERIIAMOUNT+dtls.INTEREST,dtls.EMPLOYEECOUNT,temp.a from ");
				  lSBQuery.append(" (select sum(dt.INST_AMOUNT+dt.int_amount)as a,dt.BILLL_ID from TIERII_NAMUMNA_F_EMP_DETAILS dt join mst_Dcps_emp emp on dt.DCPS_ID=emp.DCPS_EMP_ID WHERE dt.STATUS in(10,12) and emp.SEVARTH_ID='"+searchSeva+"' GROUP by dt.BILLL_ID)temp ");//BILLL_ID='111122222220219001' and
				  lSBQuery.append(" join TIERTWO_NAMUMNA_F dtls on dtls.BILL_ID=temp.billl_id and dtls.DDO_CODE like '"+locId+"%' and dtls.BILL_YEAR='"+year+"' order by dtls.BILL_GENERATION_DATE desc ");  
				  }else if(!searchEmp.equals("")){
				  lSBQuery.append(" select dtls.BILL_ID,dtls.BILL_ID,dtls.BILL_year,dtls.bill_month,dtls.ddo_code,dtls.TIERIIAMOUNT+dtls.INTEREST,dtls.EMPLOYEECOUNT,temp.a from ");
				  lSBQuery.append(" (select sum(dt.INST_AMOUNT+dt.int_amount)as a,dt.BILLL_ID from TIERII_NAMUMNA_F_EMP_DETAILS dt join mst_Dcps_emp emp on dt.DCPS_ID=emp.DCPS_EMP_ID WHERE dt.STATUS in(10,12) and emp.emp_name like '"+searchEmp+"%' GROUP by dt.BILLL_ID)temp ");//BILLL_ID='111122222220219001' and
				  lSBQuery.append(" join TIERTWO_NAMUMNA_F dtls on dtls.BILL_ID=temp.billl_id and dtls.DDO_CODE like '"+locId+"%' and dtls.BILL_YEAR='"+year+"' order by dtls.BILL_GENERATION_DATE desc ");
				  }else{
				  lSBQuery.append(" select dtls.BILL_ID,dtls.BILL_ID,dtls.BILL_year,dtls.bill_month,dtls.ddo_code,dtls.TIERIIAMOUNT+dtls.INTEREST,dtls.EMPLOYEECOUNT,temp.a from ");
				  lSBQuery.append(" (select sum(INST_AMOUNT+int_amount)as a,BILLL_ID from TIERII_NAMUMNA_F_EMP_DETAILS WHERE STATUS in(10,12)  GROUP by BILLL_ID)temp ");
				  lSBQuery.append(" join TIERTWO_NAMUMNA_F dtls on dtls.BILL_ID=temp.billl_id and dtls.DDO_CODE = '"+ddoCode+"' and dtls.BILL_YEAR='"+year+"' order by dtls.BILL_GENERATION_DATE desc ");
				  }
				}else if(login.equals("HOD")){/////$t showP
					 if(!searchSeva.equals("")){
						 lSBQuery.append(" select f.BILL_ID,f.BILL_ID,f.BILL_year,f.bill_month,f.ddo_code,f.tierIIAMOUNT+f.INTEREST,f.EMPLOYEECOUNT ");
						 lSBQuery.append(" from TIERTWO_NAMUMNA_F f join TIERII_NAMUMNA_F_EMP_DETAILS dtls on f.BILL_ID=dtls.BILLL_ID join mst_Dcps_Emp emp on dtls.dcps_ID=emp.dcps_emp_id ");
						 lSBQuery.append(" where emp.SEVARTH_ID='"+searchSeva+"' order by f.BILL_GENERATION_DATE desc ");
					 }else if(!searchEmp.equals("")){
						 lSBQuery.append(" select f.BILL_ID,f.BILL_ID,f.BILL_year,f.bill_month,f.ddo_code,f.tierIIAMOUNT+f.INTEREST,f.EMPLOYEECOUNT ");
						 lSBQuery.append(" from TIERTWO_NAMUMNA_F f join TIERII_NAMUMNA_F_EMP_DETAILS dtls on f.BILL_ID=dtls.BILLL_ID join mst_Dcps_Emp emp on dtls.dcps_ID=emp.dcps_emp_id ");
						 lSBQuery.append(" where emp.emp_name like '"+searchEmp+"%' order by f.BILL_GENERATION_DATE desc ");
					 }else{
				     lSBQuery.append(" SELECT f.BILL_ID,f.BILL_ID,f.BILL_year,f.bill_month,f.ddo_code,f.tierIIAMOUNT+f.INTEREST,f.EMPLOYEECOUNT FROM TIERTWO_NAMUMNA_F F where f.bill_id ");
				     lSBQuery.append(" in(SELECT distinct(femp.billl_id)FROM TIERII_NAMUMNA_F_EMP_DETAILS FEMP join TIERII_ATTACH_DETACH_DTLS dtls on dtls.DCPS_EMP_ID=FEMP.DCPS_ID ");
				     lSBQuery.append(" and dtls.CREATED_POST_ID='"+post_id+"') ");
				     /*if(!month.equals(""))
					 lSBQuery.append(" and BILL_YEAR='"+year+"' and  BILL_MONTH='"+month+"' order by BILL_GENERATION_DATE desc ");
					 else*/
					 lSBQuery.append(" order by f.BILL_GENERATION_DATE desc ");
					 }
				}else{
				lSBQuery.append(" select dtls.BILL_ID,dtls.BILL_ID,dtls.BILL_year,dtls.bill_month,dtls.ddo_code,dtls.TIERIIAMOUNT+dtls.INTEREST,dtls.EMPLOYEECOUNT,temp.a from ");
				lSBQuery.append(" (select sum(INST_AMOUNT+int_amount)as a,BILLL_ID from TIERII_NAMUMNA_F_EMP_DETAILS WHERE STATUS=2  GROUP by BILLL_ID)temp ");//BILLL_ID='111122222220219001' and
				//lSBQuery.append(" join TIERTWO_NAMUMNA_F dtls on dtls.BILL_ID=temp.billl_id and dtls.DDO_CODE = '"+ddoCode+"' and dtls.BILL_YEAR='"+year+"'and dtls.BILL_MONTH='"+month+"' order by BILL_GENERATION_DATE desc ");
				lSBQuery.append(" join TIERTWO_NAMUMNA_F dtls on dtls.BILL_ID=temp.billl_id and dtls.BILL_YEAR='"+year+"'and dtls.BILL_MONTH='"+month+"' order by BILL_GENERATION_DATE desc ");
				}

				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				
				Object[] tuple = (Object[]) lLstEmpFiveInst.get(0);
					value=tuple[0].toString();
					/*logger.info("lLstEmp count-->" + value.toString());
					value=tuple[1].toString();
					logger.info("lLstEmp count-->" + value.toString());
					value=tuple[2].toString();
					logger.info("lLstEmp count-->" + value.toString());
					value=tuple[3].toString();
					logger.info("lLstEmp count-->" + value.toString());
				    value=tuple[4].toString();
				    logger.info("lLstEmp count-->" + value.toString());
				    value=tuple[5].toString();
				    logger.info("lLstEmp count-->" + value.toString());
				    value=tuple[6].toString();
				    logger.info("lLstEmp count-->" + value.toString());
				    value=tuple[7].toString();
				    logger.info("lLstEmp count-->" + value.toString());*/
				    logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		
		return lLstEmpFiveInst;
	}

	public List getDdoSearchListDept(String gStrLocationCode,String searchDDO) {
		List lLstEmpFiveInst = null;
		StringBuilder sb = null;
			try {
				sb = new StringBuilder();
				sb.append(" SELECT ddo.ddo_code FROM org_ddo_mst ddo join mst_dcps_ddo_office office on office.ddo_code = ddo.ddo_code and office.ddo_office='Yes' ");
				sb.append(" where ddo.DDO_NAME= '"+searchDDO+"' and substr(ddo.ddo_code,1,4)='"+gStrLocationCode+"' ");
				Query stQuery = ghibSession.createSQLQuery(sb.toString());
				lLstEmpFiveInst = stQuery.list();
				
				if(lLstEmpFiveInst.size()>0)
				searchDDO=(String) lLstEmpFiveInst.get(0);

				if(!searchDDO.equals("")){
				sb = new StringBuilder();
				sb.append("select f.DDO_CODE,count(*),sum(f.EMPLOYEECOUNT),sum(f.TIERIIAMOUNT),sum(f.INTEREST),sum(f.INTEREST+f.TIERIIAMOUNT),ddo1.DDO_NAME from TIERTWO_NAMUMNA_F f left join org_ddo_mst ddo1 on f.DDO_CODE=ddo1.DDO_CODE where f.stage=11 and f.DDO_CODE in "); 
				sb.append("( "); 
				sb.append("   SELECT "); 
				sb.append("   ddo.DDO_CODE "); 
				sb.append("   FROM ORG_DDO_MST ddo "); 
				sb.append("   RIGHT JOIN MST_DCPS_DDO_OFFICE office on ddo.DDO_CODE=office.DDO_CODE "); 
				sb.append("   and office.DDO_OFFICE='Yes' "); 
				sb.append("   RIGHT JOIN ORG_USERPOST_RLT user on ddo.POST_ID=user.POST_ID "); 
				sb.append("   and user.ACTIVATE_FLAG=1 "); 
				sb.append("   where ddo.DDO_CODE ='"+searchDDO+"' "); 
				sb.append(") group by f.DDO_CODE,ddo1.DDO_NAME ");
				}else{
					sb = new StringBuilder();
					sb.append("select f.DDO_CODE,count(*),sum(f.EMPLOYEECOUNT),sum(f.TIERIIAMOUNT),sum(f.INTEREST),sum(f.INTEREST+f.TIERIIAMOUNT),ddo1.DDO_NAME from TIERTWO_NAMUMNA_F f left join org_ddo_mst ddo1 on f.DDO_CODE=ddo1.DDO_CODE where f.stage=11 and f.DDO_CODE in "); 
					sb.append("( "); 
					sb.append("   SELECT "); 
					sb.append("   ddo.DDO_CODE "); 
					sb.append("   FROM ORG_DDO_MST ddo "); 
					sb.append("   RIGHT JOIN MST_DCPS_DDO_OFFICE office on ddo.DDO_CODE=office.DDO_CODE "); 
					sb.append("   and office.DDO_OFFICE='Yes' "); 
					sb.append("   RIGHT JOIN ORG_USERPOST_RLT user on ddo.POST_ID=user.POST_ID "); 
					sb.append("   and user.ACTIVATE_FLAG=1 "); 
					sb.append("   where ddo.DDO_CODE like '"+gStrLocationCode+"%' "); 
					sb.append(") group by f.DDO_CODE,ddo1.DDO_NAME ");	
				}
				stQuery = ghibSession.createSQLQuery(sb.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		
		return lLstEmpFiveInst;
	}
	
	public List getTierIIBillListDept(String ddoCode,String month,String year, String searchSeva, String searchEmp) {
		List lLstEmpFiveInst = null;
		StringBuilder lSBQuery = null;
			try {
				lSBQuery = new StringBuilder();
				
				if(!searchSeva.equals("")){
				lSBQuery.append(" select DISTINCT(bill.BILL_ID),substr(bill.BILL_ID,0,4)||'/'||bill.BILL_YEAR||'/TIER-II/'||bill.BILL_ID ,bill.DDO_CODE ,bill.EMPLOYEECOUNT ,bill.INTEREST,bill.TIERIIAMOUNT ,bill.INTEREST+bill.TIERIIAMOUNT,bill.AUTH_NUMBER,DECODE(BILL_STATUS,'10','Order Generated','-12','Order Deleted','-11','Bill Deleted','12','Bill Generated','13','Bill is fowarded to BEAMS','14','Bill is forwarded To SRKA','15','Grant is Approved From SRKA','16','BDS Generated','17','TIER-II Bill Approved'),bill.BILL_YEAR,bill.BILL_MONTH,to_char(bill.BILL_GENERATION_DATE,'dd/mm/yyyy'),bill.BILL_ID,bill.VOUCHER_NO,to_char(bill.VOUCHER_DATE,'dd/mm/yyyy'),bill.BILL_GENERATION_DATE  ");
				lSBQuery.append(" from TIERTWO_BILL_DTLS bill join TIERII_EMP_DETAILS emp on bill.BILL_ID=emp.BILLL_ID join mst_dcps_emp mst on emp.dcps_ID=mst.DCPS_EMP_ID join TIERII_ATTACH_DETACH_DTLS att on mst.DCPS_emp_ID=att.DCPS_EMP_ID where att.CREATED_POST_ID='"+year+"' and att.LOC_ID='"+ddoCode+"' and mst.SEVARTH_ID='"+searchSeva+"' and bill.BILL_STATUS in(10,-12,-11,12,13,14,15,16,17) ");
				}else if(!searchEmp.equals("")){
				lSBQuery.append(" select DISTINCT(bill.BILL_ID),substr(bill.BILL_ID,0,4)||'/'||bill.BILL_YEAR||'/TIER-II/'||bill.BILL_ID ,bill.DDO_CODE ,bill.EMPLOYEECOUNT ,bill.INTEREST,bill.TIERIIAMOUNT ,bill.INTEREST+bill.TIERIIAMOUNT,bill.AUTH_NUMBER,DECODE(BILL_STATUS,'10','Order Generated','-12','Order Deleted','-11','Bill Deleted','12','Bill Generated','13','Bill is fowarded to BEAMS','14','Bill is forwarded To SRKA','15','Grant is Approved From SRKA','16','BDS Generated','17','TIER-II Bill Approved'),bill.BILL_YEAR,bill.BILL_MONTH,to_char(bill.BILL_GENERATION_DATE,'dd/mm/yyyy'),bill.BILL_ID,bill.VOUCHER_NO,to_char(bill.VOUCHER_DATE,'dd/mm/yyyy'),bill.BILL_GENERATION_DATE  ");
				lSBQuery.append(" from TIERTWO_BILL_DTLS bill join TIERII_EMP_DETAILS emp on bill.BILL_ID=emp.BILLL_ID join mst_dcps_emp mst on emp.dcps_ID=mst.DCPS_EMP_ID join TIERII_ATTACH_DETACH_DTLS att on mst.DCPS_emp_ID=att.DCPS_EMP_ID where att.CREATED_POST_ID='"+year+"' and att.LOC_ID='"+ddoCode+"' and mst.emp_name like '%"+searchEmp+"%' and bill.BILL_STATUS in(10,-12,-11,12,13,14,15,16,17) ");
				}else{
				lSBQuery.append(" select DISTINCT(bill.BILL_ID),substr(bill.BILL_ID,0,4)||'/'||bill.BILL_YEAR||'/TIER-II/'||bill.BILL_ID,bill.DDO_CODE,bill.EMPLOYEECOUNT,bill.INTEREST,bill.TIERIIAMOUNT,bill.INTEREST+bill.TIERIIAMOUNT,bill.AUTH_NUMBER,DECODE(BILL_STATUS,'10','Order Generated','-12','Order Deleted','-11','Bill Deleted','12','Bill Generated','13','Bill is fowarded to BEAMS','14','Bill is forwarded To SRKA','15','Grant is Approved From SRKA','16','BDS Generated','17','TIER-II Bill Approved'),bill.BILL_YEAR,bill.BILL_MONTH,to_char(bill.BILL_GENERATION_DATE,'dd/mm/yyyy'),bill.BILL_ID,bill.VOUCHER_NO,to_char(bill.VOUCHER_DATE,'dd/mm/yyyy'),bill.BILL_GENERATION_DATE  ");	
				lSBQuery.append(" from TIERTWO_BILL_DTLS bill join TIERII_EMP_DETAILS dtls on bill.BILL_ID=dtls.BILLL_ID join TIERII_ATTACH_DETACH_DTLS att on dtls.DCPS_ID=att.DCPS_EMP_ID  ");	
				lSBQuery.append(" where att.CREATED_POST_ID='"+year+"' and att.LOC_ID='"+ddoCode+"' and bill.BILL_STATUS in(10,-12,-11,12,13,14,15,16,17)  ");	
				if(!month.equals(""))
				lSBQuery.append(" and bill.BILL_YEAR='"+year+"' and  bill.BILL_MONTH='"+month+"' order by bill.BILL_GENERATION_DATE desc ");
				else
				lSBQuery.append(" order by bill.BILL_GENERATION_DATE desc ");
				}

				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lLstEmpFiveInst = stQuery.list();
				logger.info("lLstEmp count-->" + lLstEmpFiveInst.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error is :" + e, e);
			}
		
		return lLstEmpFiveInst;
	}
	
	public String  getHODDetails(Long lLngPostId) {
    	String lLstEmpFiveInst = null;
        StringBuilder sb = null;
        try {
            final Session ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
            sb = new StringBuilder();
            sb.append(" SELECT lna.HOD_DSGN FROM ORG_USERPOST_RLT user join ORG_POST_DETAILS_RLT post on user.USER_ID=post.POST_ID ");
            sb.append(" join CMN_LOCATION_MST cmn on post.LOC_ID=cmn.LOC_ID join ORG_USER_MST mst on user.user_id=mst.USER_ID join TIERII_HOD_PROFILE lna on user.POST_ID=lna.CREATED_POST_ID ");
            sb.append(" WHERE user.POST_ID='"+lLngPostId+"' and user.ACTIVATE_FLAG = 1 ");
            
            final Query stQuery = (Query)ghibSession.createSQLQuery(sb.toString());
            lLstEmpFiveInst = (String) stQuery.list().get(0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return lLstEmpFiveInst;
	}

	public void saveHODDetails(String hodName, String hodDsgn,String hodOffice, String hodDDO, String gStrUserId,String gStrPostId, String gStrLocationCode) {
       try {
		    StringBuilder lSBQuery = new StringBuilder();
		    lSBQuery.append(" INSERT INTO TIERII_HOD_PROFILE(LOCATION_CODE,HOD_NAME,HOD_DSGN,HOD_OFFICE,HOD_DDO_CODE,CREATED_POST_ID,CREATED_USER_ID,CREATED_DATE) ");
		    lSBQuery.append(" VALUES('"+gStrLocationCode+"','"+hodName+"','"+hodDsgn+"','"+hodOffice+"','"+hodDDO+"','"+gStrPostId+"','"+gStrUserId+"',sysdate) ");
		    SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.executeUpdate();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	 }
  }
	
	public List getDummyOffices(String lStrPostId) throws Exception {
		
		String lLstEmpFiveInst = null;
        StringBuilder sb = null;
        String val="";
        try {
            final Session ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
            sb = new StringBuilder();
            sb.append(" SELECT DDO_CODE FROM ORG_DDO_MST where POST_ID ='"+lStrPostId+"' and ACTIVATE_FLAG =1 ");
            final Query stQuery = (Query)ghibSession.createSQLQuery(sb.toString());
            lLstEmpFiveInst = (String) stQuery.list().get(0);
            val=lLstEmpFiveInst.substring(0, 2);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

		List<ComboValuesVO> lLstOffice = new ArrayList<ComboValuesVO>();
		List lLstResultList = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		ComboValuesVO cmbVO;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT dummyOfficeId,dummyOfficeName FROM MstDummyOffice WHERE dummyOfficeId IS NOT NULL AND treasury like :treasury  order by dummyOfficeName  ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("treasury", val.trim()+"%");
			
			// , searchKeyword+"%"
			
			logger.info("Query to DUMMY OFFICE heaqder**********"
					+ lSBQuery.toString());
			lLstResultList = hqlQuery.list();
			Collections.sort(lLstResultList,
					new PensionProcComparators.ObjectArrayComparator(false, 1,
							0, 2, 0, true));
			cmbVO = new ComboValuesVO();
			if (lLstResultList != null && lLstResultList.size() > 0) {
				Iterator it = lLstResultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					Object[] obj = (Object[]) it.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					lLstOffice.add(cmbVO);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.info("Error is  " + e);
		}
		return lLstOffice;

	}
	
}// end class
