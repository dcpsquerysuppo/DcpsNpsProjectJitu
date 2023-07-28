package com.tcs.sgv.dcps.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class AllIndiaServicesDAOImpl extends GenericDaoHibernateImpl {
	private Session ghibSession = null;
	private final Logger gLogger = Logger.getLogger(getClass());
//	SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
//	Date date = new Date();

	public AllIndiaServicesDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}

	public List getAllIndiaServicesEmpList(String aisType){
		List empLst = null;
		ghibSession = getSession();
		StringBuffer bf = new StringBuffer();
		bf.append(" SELECT mst.DCPS_EMP_ID,mst.EMP_NAME,mst.SEVARTH_ID,mst.doj,mst.dcps_id,mst.pran_no,rlt.GIS_APPLICABLE FROM MST_DCPS_EMP mst inner join RLT_DCPS_PAYROLL_EMP rlt ");
		bf.append(" on mst.DCPS_EMP_ID = rlt.DCPS_EMP_ID  ");
		bf.append(" where rlt.GIS_APPLICABLE in ");
		if(aisType != null && !aisType.equals("-1"))
			bf.append(" ("+aisType+") ");
		else bf.append(" (700214,700215,700216) ");
		bf.append(" and mst.DOJ >= '2004-01-01' and mst.REG_STATUS in (1,2) and mst.FORM_STATUS = 1   AND mst.LOC_ID <> 380001  ");
		bf.append(" order by mst.EMP_NAME,rlt.GIS_APPLICABLE ");

		gLogger.info(bf);

		Query qry = ghibSession.createSQLQuery(bf.toString());
		if(qry != null){
			empLst = qry.list();
		}
		return empLst; 
	}


	public Boolean checkPranNO(String pranno)
	{

		StringBuffer stbuff = new StringBuffer();
		List<Long>  lstpn = new ArrayList();
		Boolean flag  = false;
		stbuff.append("select dcpsEmpId from Mst_DCPS_EMP where PRAN_NO =:pranNO");

		Query lstQuery = ghibSession.createSQLQuery(stbuff.toString());
		lstQuery.setParameter("pranNO",pranno);

		lstpn = lstQuery.list();

		if(lstpn != null)
		{
			if(lstpn.size()!=0)
			{
				flag = true;
			}
		}
		return flag;

	}

	public List getMonths() {

		String query = "select monthId,monthName from SgvaMonthMst where monthId < 13";
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



	public List getFinyears() {

		String query = "select finYearId,finYearCode from SgvcFinYearMst where finYearCode between '2008' and '2015' order by finYearCode ASC";
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

	public List getAISlist()
	{
		List lstAis = null;

		try{		
			StringBuilder sb = new StringBuilder();
			sb.append("   SELECT LOOKUP_ID,LOOKUP_DESC FROM CMN_LOOKUP_MST where LOOKUP_ID in (700240,700241,700242)  ");
			Query LsQuery = ghibSession.createSQLQuery(sb.toString());

			logger.info("Script is ----------------"+LsQuery.toString());		
			List lLstResult = LsQuery.list();		
			ComboValuesVO lObjComboValuesVO = null;		
			if(lLstResult!= null && lLstResult.size()!=0)
			{
				lstAis = new ArrayList<Object>();
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++)
				{				
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lstAis.add(lObjComboValuesVO);
				}
			} else {
				lstAis = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--ALL--");
				lstAis.add(lObjComboValuesVO);
			}
		}
		catch(Exception e)
		{
			logger.info("Error found in getAISlist ----------"+e);
		}
		return lstAis;

	}
	public List getEmployeeList(String acMain,String billno,String finType, String fromDate, String toDate)
	{
		List empLst = null;

		logger.info("User data"+acMain+"---------------"+billno);

		StringBuilder  Strbld = new StringBuilder();

		try{

			Strbld.append(" SELECT  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,trn.FIN_YEAR_ID,mo.MONTH_NAME,fin.FIN_YEAR_DESC,mo.month_id  ");   
			Strbld.append("	,sum(trn.CONTRIBUTION),sum(decode(trn.EMPLOYER_CONTRI_FLAG,'Y',trn.CONTRIBUTION,0) )as EmployerContri,month(trn.STARTDATE),year(trn.STARTDATE),trn.TYPE_OF_PAYMENT ");
            Strbld.append("  FROM TRN_DCPS_CONTRIBUTION trn inner join mst_DCPS_EMP emp on emp.DCPS_EMP_ID = trn.DCPS_EMP_ID   "); 
			Strbld.append(" inner join MST_DCPS_CONTRI_VOUCHER_DTLS mst on mst.MST_DCPS_CONTRI_VOUCHER_DTLS = trn.RLT_CONTRI_VOUCHER_ID    ");
			Strbld.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID = trn.TYPE_OF_PAYMENT    ");
			Strbld.append(" inner join SGVC_FIN_YEAR_MST fin on  trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE  ");
			Strbld.append(" inner join SGVA_MONTH_MST mo on mo.MONTH_ID = month(trn.STARTDATE)  and mo.LANG_ID = 'en_US'  ");
			Strbld.append(" inner join MST_DCPS_POST_EMPLOYER_CONTRI post on trn.EMPLR_BILL_NO=post.BILL_NO ");
			Strbld.append(" where trn.REG_STATUS =1 and  post.AC_DCPS_MAINTAINED_BY = :acMain and post.BILL_NO= :billno  and post.FIN_YEAR= :finType   ");
			Strbld.append(" and emp.REG_STATUS=1 and mst.VOUCHER_STATUS=1 "); 
			Strbld.append(" and post.BILL_NO=trn.EMPLR_BILL_NO and trn.EMPLR_BILL_NO=:billno and trn.EMPLR_YEAR_ID=:finType  ");
			Strbld.append(" and emp.PRAN_NO is NOT NULL and trn.CONTRIBUTION is not null  and trn.EMPLOYER_CONTRI_FLAG ='Y'  ");
			Strbld.append(" and ((trn.VOUCHER_DATE between '" + fromDate + "' AND '"+ toDate + "') or (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "')) AND emp.LOC_ID <> 380001 ");

			Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,mo.MONTH_NAME,trn.FIN_YEAR_ID,fin.FIN_YEAR_DESC ");   
			Strbld.append(" ,month(trn.STARTDATE),year(trn.STARTDATE),fin.FIN_YEAR_CODE,trn.EMPLR_BILL_NO,mo.month_id,trn.TYPE_OF_PAYMENT  ");
			Strbld.append(" order by EMP_NAME ");

			//ended By Ashish
			logger.info("   ---------"+Strbld.toString() );
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
			logger.info("script for all employee ---------"+lQuery.toString() );
			lQuery.setString("acMain",acMain);
			lQuery.setString("billno",billno);
			lQuery.setString("finType",finType);
			
			empLst = lQuery.list();


		}
		catch(Exception e)
		{
			logger.info("Error occer in  getEmployeeList ---------"+ e);
		}
		return empLst;


	}
	public List getEmployeeListForCahallan(String acMain,String finType, String fromDate, String toDate)
	{
		List empLst = null;

		

		StringBuilder  Strbld = new StringBuilder();

		try{

			Strbld.append(" SELECT  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,trn.FIN_YEAR_ID,mo.MONTH_NAME,fin.FIN_YEAR_DESC,mo.month_id  ");   
			Strbld.append("	,sum(trn.CONTRIBUTION),sum(trn.CONTRIBUTION) as EmployerContri,month(trn.STARTDATE),year(trn.STARTDATE),trn.TYPE_OF_PAYMENT ");
            Strbld.append("  FROM TRN_DCPS_CONTRIBUTION trn inner join mst_DCPS_EMP emp on emp.DCPS_EMP_ID = trn.DCPS_EMP_ID   "); 
			Strbld.append(" inner join MST_DCPS_CONTRI_VOUCHER_DTLS mst on mst.MST_DCPS_CONTRI_VOUCHER_DTLS = trn.RLT_CONTRI_VOUCHER_ID    ");
			Strbld.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID = trn.TYPE_OF_PAYMENT    ");
			Strbld.append(" inner join SGVC_FIN_YEAR_MST fin on  trn.STARTDATE>=fin.from_date and fin.to_date>=trn.STARTDATE  ");
			Strbld.append(" inner join SGVA_MONTH_MST mo on mo.MONTH_ID = month(trn.STARTDATE)  and mo.LANG_ID = 'en_US'  ");
			Strbld.append(" where trn.REG_STATUS =1 and  emp.AC_DCPS_MAINTAINED_BY = :acMain    ");
			Strbld.append(" and emp.REG_STATUS=1 and mst.VOUCHER_STATUS=1 and trn.IS_DEPUTATION='Y' and trn.FIN_YEAR_ID < 28 and trn.BATCH_ID is null "); 
			Strbld.append(" and emp.PRAN_NO is NOT NULL and trn.CONTRIBUTION is not null  and trn.EMPLOYER_CONTRI_FLAG ='Y'  ");
			Strbld.append(" and  (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "') AND emp.LOC_ID <> 380001 ");

			Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,mo.MONTH_NAME,trn.FIN_YEAR_ID,fin.FIN_YEAR_DESC ");   
			Strbld.append(" ,month(trn.STARTDATE),year(trn.STARTDATE),fin.FIN_YEAR_CODE,trn.EMPLR_BILL_NO,mo.month_id,trn.TYPE_OF_PAYMENT  ");
			Strbld.append(" order by EMP_NAME ");

			//ended By Ashish
			logger.info("   ---------"+Strbld.toString() );
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
			logger.info("script for all employee ---------"+lQuery.toString() );
			lQuery.setString("acMain",acMain);
			
			
			
			empLst = lQuery.list();


		}
		catch(Exception e)
		{
			logger.info("Error occer in  getEmployeeList ---------"+ e);
		}
		return empLst;


	}

	
	
	
	public String getyear(String yearid)
	{

		String year = null;
		String query = "SELECT FIN_YEAR_CODE FROM SGVC_FIN_YEAR_MST where FIN_YEAR_ID =:yearid";		
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		selectQuery.setString("yearid",yearid);
		year = (String) selectQuery.uniqueResult();

		return year;

	}

	public String getmonth(String monthid)
	{

		String month = null;
		String query = "SELECT MONTH_NAME FROM SGVA_MONTH_MST where MONTH_ID=:monthid";		
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		selectQuery.setString("monthid",monthid);
		month = (String) selectQuery.uniqueResult();

		return month;

	}



	public void updateRepStatus(String aisType, Long dcpsEmpID, String BatchId,String billno, String fromDate, String toDate) {
		StringBuffer sb= new StringBuffer();
		//ghibSession = getSession();
		try{
			sb.append("update TRN_DCPS_CONTRIBUTION SET ")	;
			if(aisType!=null && !aisType.equals("") && Long.parseLong(aisType)==700240)  {

				sb.append("  NSDL_IAS_REP_STATUS=1 , BATCH_ID="+BatchId+"  ");
			}

			if(aisType!=null && !aisType.equals("") && Long.parseLong(aisType)==700241)  {

				sb.append("  NSDL_IPS_REP_STATUS=1 , BATCH_ID="+BatchId+"");
			}

			if(aisType!=null && !aisType.equals("") && Long.parseLong(aisType)==700242)  {

				sb.append("  NSDL_IFS_REP_STATUS=1, BATCH_ID="+BatchId+"");
			}
			sb.append(" where  DCPS_CONTRIBUTION_ID="+dcpsEmpID+" and EMPLR_BILL_NO='"+billno+"'  and ((VOUCHER_DATE between '" + fromDate + "' AND '"+ toDate + "') or (MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "')) ");
			int i = ghibSession.createSQLQuery(sb.toString()).executeUpdate();
			//ghibSession.connection().commit();
			logger.info("Query is *************"+sb.toString());
			//Query selectQuery = ghibSession.createSQLQuery(sb.toString());

			//selectQuery.setParameter("dcpsEmpID", dcpsEmpID);
			logger.info("Query is *************"+i );

			logger.info("Number of row updated is *************"+i);

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void updateRepStatusForChallan(String aisType, Long dcpsEmpID, String BatchId, String fromDate, String toDate) {
		StringBuffer sb= new StringBuffer();
		//ghibSession = getSession();
		try{
			sb.append("update TRN_DCPS_CONTRIBUTION SET ")	;
			if(aisType!=null && !aisType.equals("") && Long.parseLong(aisType)==700240)  {

				sb.append("  NSDL_IAS_REP_STATUS=1 , BATCH_ID="+BatchId+"  ");
			}

			if(aisType!=null && !aisType.equals("") && Long.parseLong(aisType)==700241)  {

				sb.append("  NSDL_IPS_REP_STATUS=1 , BATCH_ID="+BatchId+"");
			}

			if(aisType!=null && !aisType.equals("") && Long.parseLong(aisType)==700242)  {

				sb.append("  NSDL_IFS_REP_STATUS=1, BATCH_ID="+BatchId+"");
			}
			sb.append(" where  DCPS_CONTRIBUTION_ID="+dcpsEmpID+"   and  (MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "') and IS_DEPUTATION='Y'");
			int i = ghibSession.createSQLQuery(sb.toString()).executeUpdate();
			//ghibSession.connection().commit();
			logger.info("Query is *************"+sb.toString());
			//Query selectQuery = ghibSession.createSQLQuery(sb.toString());

			//selectQuery.setParameter("dcpsEmpID", dcpsEmpID);
			logger.info("Query is *************"+i );

			logger.info("Number of row updated is *************"+i);

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getStatus(String acMain, String monthid, String yearid) {
		String status = null;

		logger.info("User data"+acMain+"---------------"+monthid+"------------------"+yearid);

		StringBuilder  Strbld = new StringBuilder();

		try{

			Strbld.append(" Select NSDL_IAS_REP_STATUS || NSDL_IFS_REP_STATUS || NSDL_IPS_REP_STATUS ");

			//ended By Ashish
			Strbld.append("  FROM TRN_DCPS_CONTRIBUTION trn inner join mst_DCPS_EMP emp on emp.DCPS_EMP_ID = trn.DCPS_EMP_ID  ");
			Strbld.append(" inner join MST_DCPS_CONTRI_VOUCHER_DTLS mst on mst.MST_DCPS_CONTRI_VOUCHER_DTLS = trn.RLT_CONTRI_VOUCHER_ID  ");
			//Strbld.append(" inner join RLT_DCPS_PAYROLL_EMP rlt on rlt.DCPS_EMP_ID = emp.DCPS_EMP_ID  ");
			Strbld.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID = trn.TYPE_OF_PAYMENT  ");
			//Strbld.append(" full join ifms.MST_DCPS_CONTRIBUTION_MONTHLY mn on mn.DCPS_ID = emp.DCPS_ID   ");
			Strbld.append("   inner join SGVC_FIN_YEAR_MST fin on  fin.FIN_YEAR_ID = trn.FIN_YEAR_ID   ");
			Strbld.append("  inner join SGVA_MONTH_MST mo on mo.MONTH_ID = trn.MONTH_ID  ");
			Strbld.append(" where trn.STATUS = 'G' and mst.VOUCHER_STATUS = 1 and  emp.AC_DCPS_MAINTAINED_BY =:acMain  and trn.MONTH_ID =:month  and trn.FIN_YEAR_ID =:year and  trn.BATCH_ID is null ");
			Strbld.append(" order by emp.EMP_NAME");
			//ended By Ashish
			logger.info("   ---------"+Strbld.toString() );
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

			logger.info("script for all employee ---------"+lQuery.toString() );
			lQuery.setString("acMain",acMain);
			lQuery.setString("month",monthid);
			lQuery.setString("year",yearid); 

			status=(String) lQuery.list().get(0);

		}
		catch(Exception e)
		{
			logger.info("Error occer in  getEmployeeList ---------"+ e);
		}
		return status;

	}
	
	public void insertDataForNSDLRepo(Long BatchId,String acMain, String billNo) {

		StringBuilder  Strbld = new StringBuilder();
		try{

			Strbld.append(" insert into NSDL_REPORT (BATCH_ID,AC_MAINTAINED_BY,BILL_NO)values("+BatchId+",'"+acMain+"','"+billNo+"') ");

			int i = ghibSession.createSQLQuery(Strbld.toString()).executeUpdate();
			//ghibSession.connection().commit();
			logger.info("Query is *************"+Strbld.toString());
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	public void insertDataForNSDLRepoChallan(Long BatchId,String acMain, String fileId) {

		StringBuilder  Strbld = new StringBuilder();
		try{

			Strbld.append(" insert into NSDL_REPORT (BATCH_ID,AC_MAINTAINED_BY,BILL_NO,IS_DEPUTATION,DEP_FILE_ID)values("+BatchId+",'"+acMain+"','00','Y','"+fileId+"') ");

			int i = ghibSession.createSQLQuery(Strbld.toString()).executeUpdate();
			//ghibSession.connection().commit();
			logger.info("Query is *************"+Strbld.toString());
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public String selectDataForNSDLRepo(String acMain, String billno,String finType) {
		// TODO Auto-generated method stub
		String BATCH_ID=null;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" Select nvl(max(BATCH_ID),10000000000000) from NSDL_REPORT ");
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		List transactionId=lQuery.list();
		if(transactionId.size()>0)

			BATCH_ID= transactionId.get(0).toString();

		return BATCH_ID;
	}
	public String selectDataForNSDLRepoChallan(String acMain,String finType) {
		// TODO Auto-generated method stub
		String BATCH_ID=null;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" Select nvl(max(BATCH_ID),10000000000000) from NSDL_REPORT ");
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		List transactionId=lQuery.list();
		if(transactionId.size()>0)

			BATCH_ID= transactionId.get(0).toString();

		return BATCH_ID;
	}

	public List selectTrnPk(String dcpsid,String billno,String fromDate,String toDate)throws Exception {


		List contrList = null;
		Query lQuery = null;
		StringBuilder lSBQuery = null;
		try{
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT trn.DCPS_CONTRIBUTION_ID FROM  TRN_DCPS_CONTRIBUTION trn inner join mst_dcps_emp emp on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID "); 
			lSBQuery.append(" where DCPS_ID = :dcpsid and  ((trn.VOUCHER_DATE between '" + fromDate + "' AND '"+ toDate + "') or (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "'))   and trn.EMPLR_BILL_NO= :billno ");
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("dcpsid",dcpsid);
			lQuery.setString("billno",billno);
			contrList = lQuery.list();





		}
		catch(Exception e)
		{
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw(e);
		}
		return contrList;
	}
	public List selectTrnPkForChallan(String dcpsid,String fromDate,String toDate)throws Exception {


		List contrList = null;
		Query lQuery = null;
		StringBuilder lSBQuery = null;
		try{
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT trn.DCPS_CONTRIBUTION_ID FROM  TRN_DCPS_CONTRIBUTION trn inner join mst_dcps_emp emp on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID "); 
			lSBQuery.append(" where DCPS_ID = :dcpsid and   (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "')   and trn.IS_DEPUTATION='Y' and  trn.FIN_YEAR_ID < 28 and trn.BATCH_ID is null ");
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("dcpsid",dcpsid);
			
			contrList = lQuery.list();





		}
		catch(Exception e)
		{
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw(e);
		}
		return contrList;
	}
	public boolean selectDataForNSDLGen(String acMain,String billNo,String finType) {
		// TODO Auto-generated method stub
		boolean batchId=false;
		List temp=null;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" SELECT BATCH_ID FROM nsdl_report where BILL_NO='"+billNo+"' and AC_MAINTAINED_BY='"+acMain+"' and YEAR_ID = "+finType+" "); 

		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

		
		temp=lQuery.list();
		logger.info("temp size"+temp.size());
		if(temp.size()>0)
		{
			batchId=true;
		}
		return batchId;
	}
	public boolean selectDataForNSDLGenChallan(String acMain,String finType) {
		// TODO Auto-generated method stub
		boolean batchId=false;
		List temp=null;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" SELECT BATCH_ID FROM nsdl_report where  AC_MAINTAINED_BY='"+acMain+"' and YEAR_ID = "+finType+"  and IS_DEPUTATION='Y' "); 

		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

		
		temp=lQuery.list();
		logger.info("temp size"+temp.size());
		if(temp.size()>0)
		{
			batchId=true;
		}
		return batchId;
	}
	public int selectFileIDForChallan(String fileName) {
		// TODO Auto-generated method stub
		int batchId=1;
		List temp=null;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" select substr(DEP_FILE_ID,12,2) from nsdl_report where DEP_FILE_ID like '"+fileName+"%'  "); 

		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

		
		temp=lQuery.list();
		logger.info("temp size"+temp.size());
		if(temp!=null && temp.size()>0)
		{
			batchId=Integer.parseInt(temp.get(0).toString());
			batchId=batchId+1;
		}
		
		return batchId;
	}
	//Added by Ashish to increment Batch Id each time
	/*public Long selectBatchIdForNSDL() {
		// TODO Auto-generated method stub
		List lbatchIdgen=null;

		Long batchId=0L;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" SELECT max(GENERATED_ID)+1 FROM CMN_TABLE_SEQ_MST_DCPS where TABLE_NAME=batch_id_generation "); 

		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		lbatchIdgen=(lQuery.list());

		if(lbatchIdgen.size()>0 || lbatchIdgen!=null)
		{
			batchId=Long.parseLong(lbatchIdgen.get(0).toString());
		}


         return batchId;
	}*/
	public List selectBillNo(String aisType,String finTypeSelected) {
		// TODO Auto-generated method stub
		List Billno=null;

		try {


			StringBuilder  Strbld = new StringBuilder();
			Strbld.append(" SELECT BILL_NO,bill_no FROM MST_DCPS_POST_EMPLOYER_CONTRI where AC_DCPS_MAINTAINED_BY=:aisType and FIN_YEAR= :finTypeSelected order by  bill_no"); 

			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
			lQuery.setString("aisType",aisType);
			lQuery.setString("finTypeSelected",finTypeSelected);
			Billno=lQuery.list();


		}
		catch (Exception e) {			
			gLogger.info("Sql Exception:" + e, e);

		} 
		return Billno;
	}
	public String selectfinYearforBill(String acMain,String billNo,String finType) {
		
		logger.info("acMain is"+acMain);
		logger.info("billNo is"+billNo);
		// TODO Auto-generated method stub
		String FIN_YEAR=null;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" SELECT fin_year FROM MST_DCPS_POST_EMPLOYER_CONTRI where AC_DCPS_MAINTAINED_BY=:acMain and BILL_NO=:billNo  and FIN_YEAR= :finType");
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		lQuery.setParameter("billNo",billNo);
		lQuery.setParameter("acMain",acMain);
		lQuery.setParameter("finType",finType);
		List billId=lQuery.list();
		logger.info("batchId size  is"+billId.size());
		if(billId.size()>0)

			FIN_YEAR= billId.get(0).toString();

		return FIN_YEAR;
	}

	public void updatefinYear(String aisType,String finYear,String billNo,String BatchId) {
		StringBuffer sb= new StringBuffer();
		//ghibSession = getSession();
		try{
			sb.append(" update NSDL_REPORT set YEAR_ID="+finYear+",NSDL_STATUS='G' where BILL_NO='"+billNo+"' and AC_MAINTAINED_BY='"+aisType+"' and BATCH_ID="+BatchId+" ")	;

			int i = ghibSession.createSQLQuery(sb.toString()).executeUpdate();

			logger.info("Query is *************"+sb.toString());



			logger.info("Query is *************"+i );

			logger.info("Number of row updated is *************"+i);

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void updatefinYearChallan(String aisType,String finYear,String BatchId) {
		StringBuffer sb= new StringBuffer();
		//ghibSession = getSession();
		try{
			sb.append(" update NSDL_REPORT set YEAR_ID="+finYear+",NSDL_STATUS='G' where  AC_MAINTAINED_BY='"+aisType+"' and BATCH_ID="+BatchId+" and IS_DEPUTATION='Y' ")	;

			int i = ghibSession.createSQLQuery(sb.toString()).executeUpdate();

			logger.info("Query is *************"+sb.toString());



			logger.info("Query is *************"+i );

			logger.info("Number of row updated is *************"+i);

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public String accMain(String accMain) {
		List listaccMain = null;

		String  accMainby = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("SELECT distinct decode(look.LOOKUP_NAME,'A/c Maintained BY IAS','IAS','A/c Maintained BY IPS','IPS','A/c Maintained BY IFS','IFS','SRKA') ac_maintainedby   FROM CMN_LOOKUP_MST look inner join mst_dcps_emp emp on emp.AC_DCPS_MAINTAINED_BY=look.LOOKUP_ID where  emp.AC_DCPS_MAINTAINED_BY=:accMain ");

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setString("accMain",accMain);
		listaccMain=lQuery.list();
		if (listaccMain.size() != 0 && listaccMain != null) {
			if (listaccMain.get(0) != null) {
				accMainby = listaccMain.get(0).toString();
			}
		}
		return accMainby;
	}

	public void updateAgainNsdl(Long batchId,String acMain, String billNo,String finType) {

		logger.info("batchId is ****"+batchId);
		logger.info("acMain is ****"+acMain);

		logger.info("billNo is ****"+billNo);
		try{
		StringBuffer sb= new StringBuffer();
		Query lQuery = null;

		sb.append(" update NSDL_REPORT set BATCH_ID="+batchId+"  where AC_MAINTAINED_BY='"+acMain+"' and BILL_NO='"+billNo+"' and YEAR_ID="+finType+"  ");
		lQuery = ghibSession.createSQLQuery(sb.toString());
		int i = lQuery.executeUpdate();
		logger.info("Query is *************"+sb.toString());
		logger.info("Number of row updated is *************"+i);
	
	}catch(Exception e){
		e.printStackTrace();
	}

	}
	
	public void updateAgainNsdlChallan(Long batchId,String acMain,String finType, String fileId) {

		logger.info("batchId is ****"+batchId);
		logger.info("acMain is ****"+acMain);

		
		try{
		StringBuffer sb= new StringBuffer();
		Query lQuery = null;

		sb.append(" update NSDL_REPORT set BATCH_ID="+batchId+",DEP_FILE_ID='"+fileId+"' where AC_MAINTAINED_BY='"+acMain+"'   and YEAR_ID="+finType+" and IS_DEPUTATION='Y' ");
		lQuery = ghibSession.createSQLQuery(sb.toString());
		int i = lQuery.executeUpdate();
		logger.info("Query is *************"+sb.toString());
		logger.info("Number of row updated is *************"+i);
	
	}catch(Exception e){
		e.printStackTrace();
	}

	}
	public void updateFileAmount(String fileId,double amount) {

		
		try{
		StringBuffer sb= new StringBuffer();
		Query lQuery = null;

		sb.append(" update NSDL_REPORT set FILE_AMOUNT="+amount+" where DEP_FILE_ID='"+fileId+"' ");
		lQuery = ghibSession.createSQLQuery(sb.toString());
		int i = lQuery.executeUpdate();
		logger.info("Query is *************"+sb.toString());
		logger.info("Number of row updated is *************"+i);
	
	}catch(Exception e){
		e.printStackTrace();
	}

	}
	
	public List getFinyeardesc() {
		Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        if(4>month+1){
            year = year-1;
        }
		String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2007' and '"+year+"' order by finYearCode ASC";
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
	
	public List selectFromToDate(String finYearId)throws Exception {


		List contrList = null;
		Query lQuery = null;
		StringBuilder lSBQuery = null;
		try{
			lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT to_char(FROM_DATE,'yyyy-MM-dd'),to_char(TO_DATE,'yyyy-MM-dd') FROM  SGVC_FIN_YEAR_MST where FIN_YEAR_ID= :finYearId "); 
			
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			lQuery.setString("finYearId",finYearId);
			contrList = lQuery.list();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.error("Error is :" + e, e);
			throw(e);
		}
		return contrList;
	}
	

	public List getEmployeeIntList(String aisType, String treasuryNo, String finType, String fromDate, String toDate) {

		List empLst = null;

		

		StringBuilder  Strbld = new StringBuilder();

		
		Strbld.append(" SELECT  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,temp.INT_EMPL_CONTRIB,temp.INT_EMPLR_CONTRIB ");   
		Strbld.append(" FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO  "); 
		Strbld.append(" and temp.FIN_YEAR=r3.FIN_YEAR  ");
		Strbld.append(" inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'    ");
		Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO ");
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(temp.TRY_CD,1,2) ");
		Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(temp.TRY_CD,1,2) and loc.department_id=100003");
		Strbld.append(" where temp.FIN_YEAR='2007-2008' and emp.AC_DCPS_MAINTAINED_BY=700174 and loc.loc_id= 2501 and emp.pran_no is not null  ");
		Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,temp.INT_EMPL_CONTRIB,temp.INT_EMPLR_CONTRIB ");
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		logger.info("script for all employee ---------"+lQuery.toString() );
	
		empLst = lQuery.list();
		return empLst;
	}	
	public String getFinyrdesc(long yrId) {
		
		List lyrDesc=null;
		String  yrDesc = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" select fin_Year_Desc from Sgvc_Fin_Year_Mst where fin_Year_id="+yrId );

		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	
		lyrDesc=lQuery.list();
		if (lyrDesc.size() != 0 && lyrDesc != null) {
			if (lyrDesc.get(0) != null) {
				yrDesc = lyrDesc.get(0).toString();
			}
		}
		return yrDesc;
	}

	
}
