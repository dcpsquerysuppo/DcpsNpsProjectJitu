package com.tcs.sgv.dcps.dao;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class NSDLTransactionDAOImpl extends GenericDaoHibernateImpl {
	private Session ghibSession = null;
	private final Logger gLogger = Logger.getLogger(getClass());

	public NSDLTransactionDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
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
			Strbld.append(" where trn.STATUS = 'G' and mst.VOUCHER_STATUS = 1 and  emp.AC_DCPS_MAINTAINED_BY =:acMain  and trn.MONTH_ID =:month  and trn.FIN_YEAR_ID =:year and  trn.BATCH_ID is null AND emp.LOC_ID <> 380001 ");
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



	public String selectDataForNSDLRepo(String acMain, String monthid,
			String yearid) {
		// TODO Auto-generated method stub
		String TranId=null;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" Select max(TRANSACTION_ID) from NSDL_REPORT where AIS_TYPE='"+acMain+"' and MONTH_ID='"+monthid+"' and YEAR_ID ="+yearid+" ");
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		List transactionId=lQuery.list();
		if(transactionId.size()>0)

			TranId= transactionId.get(0).toString();

		return TranId;
	}

	
	public List selectDataForNSDLGen(String acMain, String monthid,String yearid) {
		// TODO Auto-generated method stub
		List batchId=null;
		Query lQuery=null;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" SELECT   trn.BATCH_ID,trn.NSDL_IAS_REP_STATUS || trn.NSDL_IPS_REP_STATUS || trn.NSDL_IFS_REP_STATUS,trn.DCPS_EMP_ID  FROM  "); 
		Strbld.append(" TRN_DCPS_CONTRIBUTION trn inner join MST_DCPS_EMP emp on  emp.DCPS_EMP_ID=trn.DCPS_EMP_ID  ");
		Strbld.append(" inner join CMN_LOOKUP_MST look on trn.TYPE_OF_PAYMENT=look.LOOKUP_ID ");
		Strbld.append(" where  trn.BATCH_ID is  null  and trn.EMPLOYER_CONTRI_FLAG='Y' and trn.STATUS='G' and emp.AC_DCPS_MAINTAINED_BY='"+acMain+"' and trn.MONTH_ID='"+monthid+"' and trn.FIN_YEAR_ID='"+yearid+"'  and emp.ddo_code not like '1111%' ");
	
		 lQuery = ghibSession.createSQLQuery(Strbld.toString());
		 batchId=lQuery.list();
		
         return batchId;
	}
	public List selectBatchIdList(String acMain) {
		// TODO Auto-generated method stub
		List batchId=null;
		Query lQuery=null;
		StringBuilder  Strbld = new StringBuilder();
		try{
		Strbld.append(" SELECT distinct  nsdl.BILL_NO,post.BILL_AMOUNT,nsdl.BATCH_ID,nsdl.TRANSACTION_ID,fin.FIN_YEAR_DESC,look.LOOKUP_NAME,decode(nsdl.NSDL_STATUS,'G','File Generated','U','Transaction Id Updated','Bill Generated') as status,'N' as IS_DEP FROM NSDL_REPORT nsdl "); 
		Strbld.append(" inner join TRN_DCPS_CONTRIBUTION trn on nsdl.BATCH_ID=trn.BATCH_ID and trn.EMPLR_BILL_NO=nsdl.BILL_NO and nsdl.YEAR_ID=trn.FIN_YEAR_ID   ");
		Strbld.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID =nsdl.YEAR_ID  ");
		Strbld.append(" inner join SGVA_MONTH_MST mon on mon.MONTH_ID=month(trn.STARTDATE)    ");
		Strbld.append(" INNER JOIN MST_DCPS_POST_EMPLOYER_CONTRI post on post.BILL_NO = trn.EMPLR_BILL_NO and post.FIN_YEAR  = nsdl.YEAR_ID and nsdl.AC_MAINTAINED_BY=post.AC_DCPS_MAINTAINED_BY ");
		Strbld.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID=post.AC_DCPS_MAINTAINED_BY   ");
		Strbld.append(" where   post.AC_DCPS_MAINTAINED_BY='"+acMain+"'    and nsdl.NSDL_STATUS in ('G','U','A')  order by nsdl.BILL_NO  ");
		/*Strbld.append(" where trn.EMPLOYER_CONTRI_FLAG='Y' and trn.STATUS='G' and emp.AC_DCPS_MAINTAINED_BY='"+acMain+"'   and nsdl.NSDL_STATUS in ('G','U','A') ");
		Strbld.append(" order by nsdl.BILL_NO ");*/
		
		 lQuery = ghibSession.createSQLQuery(Strbld.toString());
		 batchId=lQuery.list();
		

		}
		catch(Exception e){
			e.printStackTrace();
		}
		 
		
         return batchId;
	}
	public List selectBatchIdListChallan(String acMain) {
		// TODO Auto-generated method stub
		List batchId=null;
		Query lQuery=null;
		StringBuilder  Strbld = new StringBuilder();
		try{
		Strbld.append("SELECT  nsdl.DEP_FILE_ID, nsdl.FILE_AMOUNT, nsdl.BATCH_ID, nsdl.TRANSACTION_ID, fin.FIN_YEAR_DESC, look.LOOKUP_NAME, case when nsdl.NSDL_STATUS='G' then 'File Generated' when nsdl.NSDL_STATUS='U' then 'Transaction Id Updated' else  'Bill Generated' end as status,'Y' as IS_DEP FROM NSDL_REPORT nsdl "); 
	
		Strbld.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID =nsdl.YEAR_ID  ");
		Strbld.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID=nsdl.AC_MAINTAINED_BY where nsdl.AC_MAINTAINED_BY='"+acMain+"' and nsdl.NSDL_STATUS in ('G','U','A') and nsdl.IS_DEPUTATION='Y' ");
		
		
		 lQuery = ghibSession.createSQLQuery(Strbld.toString());
		 batchId=lQuery.list();
		

		}
		catch(Exception e){
			e.printStackTrace();
		}
		 
		
         return batchId;
	}
	public Boolean checktranNO(String tranno)
	{

		StringBuffer stbuff = new StringBuffer();
		List<Long>  lstpn = new ArrayList();
		Boolean flag  = false;
		stbuff.append("SELECT BATCH_ID FROM NSDL_REPORT where TRANSACTION_ID=:tranno");

		Query lstQuery = ghibSession.createSQLQuery(stbuff.toString());
		lstQuery.setParameter("tranno",tranno);

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
	
	public void updateTransactionIdNsdl (Long batchId,String transactionId,String acMain,String yearid)throws Exception {

		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();

			lSBQuery.append(" update NSDL_REPORT set TRANSACTION_ID=:transactionId,NSDL_STATUS='U' where  BATCH_ID=:batchId and  YEAR_ID=:yearid    and AC_MAINTAINED_BY=:acMain ");
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("transactionId", transactionId);
			lQuery.setParameter("batchId", batchId);
			lQuery.setParameter("yearid", Long.parseLong(yearid));
			lQuery.setParameter("acMain", acMain);
		
			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw(e);
		}
	}
	public void updateTransactionIdTrn (Long batchId,String transactionId,String yearid, String billno)throws Exception {

		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();

			lSBQuery.append(" update TRN_DCPS_CONTRIBUTION set TRANSACTION_ID=:transactionId where EMPLR_BILL_NO=:billno  and FIN_YEAR_ID=:yearid and BATCH_ID=:batchId ");
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("transactionId", transactionId);
			lQuery.setParameter("batchId", batchId);
			lQuery.setParameter("yearid", yearid);
			lQuery.setParameter("billno", billno);

			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw(e);
		}
	}
	public void updateTransactionIdTrnChallan (Long batchId,String transactionId,String yearid, String billno)throws Exception {

		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();

			lSBQuery.append(" update TRN_DCPS_CONTRIBUTION set TRANSACTION_ID=:transactionId where  FIN_YEAR_ID=:yearid and BATCH_ID=:batchId and IS_DEPUTATION='Y' ");
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("transactionId", transactionId);
			lQuery.setParameter("batchId", batchId);
			lQuery.setParameter("yearid", yearid);
			

			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw(e);
		}
	}
	public void updateBillGen (Long batchId,String transactionId, String billno)throws Exception {

		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();

			lSBQuery.append(" update NSDL_REPORT set BILL_GEN_DATE=:lStrCurrDate,BILL_GEN='Y',NSDL_STATUS='A' where BILL_NO=:billno and BATCH_ID=:batchId and TRANSACTION_ID=:transactionId  ");
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("transactionId", transactionId);
			lQuery.setParameter("lStrCurrDate", new Date());
			lQuery.setParameter("batchId", batchId);
			lQuery.setParameter("billno", billno);

			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw(e);
		}
	}
	public void updateBillGenChallan (Long batchId,String transactionId, String billno)throws Exception {

		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();

			lSBQuery.append(" update NSDL_REPORT set BILL_GEN_DATE=:lStrCurrDate,BILL_GEN='Y',NSDL_STATUS='A' where DEP_FILE_ID=:billno and BATCH_ID=:batchId and TRANSACTION_ID=:transactionId  ");
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("transactionId", transactionId);
			lQuery.setParameter("lStrCurrDate", new Date());
			lQuery.setParameter("batchId", batchId);
			lQuery.setParameter("billno", billno);

			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw(e);
		}
	}
	
	public Boolean  checkbillgen(Long batchId,String transactionId, String billno)
	{

		StringBuffer stbuff = new StringBuffer();
		List<Long>  lstpn = new ArrayList();
		Boolean flag  = false;
		stbuff.append("SELECT BATCH_ID FROM NSDL_REPORT where TRANSACTION_ID=:transactionId and BILL_NO=:billno and BATCH_ID=:batchId and BILL_GEN='Y'  ");

		Query lstQuery = ghibSession.createSQLQuery(stbuff.toString());
		lstQuery.setParameter("transactionId",transactionId);
		lstQuery.setParameter("billno",billno);
		lstQuery.setParameter("batchId",batchId);
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
	public Boolean  checkbillgenChallan(Long batchId,String transactionId, String billno)
	{

		StringBuffer stbuff = new StringBuffer();
		List<Long>  lstpn = new ArrayList();
		Boolean flag  = false;
		stbuff.append("SELECT BATCH_ID FROM NSDL_REPORT where TRANSACTION_ID=:transactionId and BATCH_ID=:batchId and BILL_GEN='Y' and IS_DEPUTATION='Y' and DEP_FILE_ID=:billno ");

		Query lstQuery = ghibSession.createSQLQuery(stbuff.toString());
		lstQuery.setParameter("transactionId",transactionId);
		lstQuery.setParameter("billno",billno);
		lstQuery.setParameter("batchId",batchId);
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
	public double selectTotalContrperbillno(Long batchid, String billno) {
		// TODO Auto-generated method stub
		double tContri=0.0;
		List<Double> listAmount = null;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" SELECT sum(CONTRIBUTION) FROM TRN_DCPS_CONTRIBUTION where BATCH_ID=:batchid and EMPLR_BILL_NO=:billno "); 
		
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		lQuery.setParameter("billno",billno);
		lQuery.setParameter("batchid",batchid);
		listAmount=lQuery.list();
		if (listAmount.size() != 0 && listAmount != null) {
			if (listAmount.get(0) != null) {
				tContri = listAmount.get(0);
			}
		}
		
         return tContri;
	}
	
	public double selectTotalContrperbillnoChallan(Long batchid, String billno) {
		// TODO Auto-generated method stub
		double tContri=0.0;
		List<Double> listAmount = null;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" SELECT sum(CONTRIBUTION) FROM TRN_DCPS_CONTRIBUTION where BATCH_ID=:batchid and  IS_DEPUTATION='Y' "); 
		
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

		lQuery.setParameter("batchid",batchid);
		listAmount=lQuery.list();
		if (listAmount.size() != 0 && listAmount != null) {
			if (listAmount.get(0) != null) {
				tContri = listAmount.get(0);
			}
		}
		
         return tContri;
	}
	public List selectDatemonyrforbillno(Long batchid, String billno) {
		// TODO Auto-generated method stub
		double tContri=0.0;
		List listAmount = null;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append("SELECT to_char(BILL_GEN_DATE,'dd/MM/yyyy') as bill_date,month(BILL_GEN_DATE) as month,year(BILL_GEN_DATE) as year FROM NSDL_REPORT where BILL_NO=:billno and BATCH_ID=:batchid "); 
		
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		lQuery.setParameter("billno",billno);
		lQuery.setParameter("batchid",batchid);
		listAmount=lQuery.list();
		
		
         return listAmount;
	}
	public List selectDatemonyrforbillnoChallan(Long batchid, String billno) {
		// TODO Auto-generated method stub
		double tContri=0.0;
		List listAmount = null;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append("SELECT to_char(BILL_GEN_DATE,'dd/MM/yyyy') as bill_date,month(BILL_GEN_DATE) as month,year(BILL_GEN_DATE) as year FROM NSDL_REPORT where DEP_FILE_ID=:billno and BATCH_ID=:batchid "); 
		
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		lQuery.setParameter("billno",billno);
		lQuery.setParameter("batchid",batchid);
		listAmount=lQuery.list();
		
		
         return listAmount;
	}
	
	public Long selectBillAmtforbillno(String acMainBy , String billno,Long finYear) {
		// TODO Auto-generated method stub
		Long tContri=0L;
		List listAmount = null;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" SELECT BILL_AMOUNT FROM MST_DCPS_POST_EMPLOYER_CONTRI post inner join NSDL_REPORT nsdl "); 
		Strbld.append(" on nsdl.BILL_NO=post.BILL_NO and post.FIN_YEAR=nsdl.YEAR_ID and post.AC_DCPS_MAINTAINED_BY=nsdl.AC_MAINTAINED_BY");
	    Strbld.append(" where post.BILL_NO=:billno and post.AC_DCPS_MAINTAINED_BY=:acMainBy and post.FIN_YEAR=:finYear and nsdl.BILL_GEN is  null ");
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		lQuery.setParameter("billno",billno);
		lQuery.setParameter("acMainBy",acMainBy);
		lQuery.setParameter("finYear",finYear);
		
		listAmount=lQuery.list();
		if (listAmount.size() != 0 && listAmount != null) 
		{
			tContri=Long.parseLong(listAmount.get(0).toString());
		}
		
		
         return tContri;
	}
	public Long selectBillAmtforbillnoChallan(String acMainBy , String billno,Long finYear) {
		// TODO Auto-generated method stub
		Long tContri=0L;
		List listAmount = null;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" SELECT cast(sum(nsdl.FILE_AMOUNT) as bigint)  FROM  NSDL_REPORT nsdl where   "); 
		Strbld.append(" nsdl.YEAR_ID=:finYear and nsdl.AC_MAINTAINED_BY =:acMainBy and nsdl.IS_DEPUTATION='Y' and nsdl.BILL_GEN is null and nsdl.DEP_FILE_ID=:billno "); 
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		lQuery.setParameter("billno",billno);
		lQuery.setParameter("acMainBy",acMainBy);
		lQuery.setParameter("finYear",finYear);
		
		listAmount=lQuery.list();
		if (listAmount.size() != 0 && listAmount != null && listAmount.get(0)!=null) 
		{
			tContri=Long.parseLong(listAmount.get(0).toString());
		}
		
		
         return tContri;
	}
	
	public Long CheckBillAmtforbillno(String acMainBy ,Long finYear)throws Exception  {
		// TODO Auto-generated method stub
		Long tContri=0L;
		List  listAmount = new ArrayList();
		try
		{		
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" SELECT cast(sum(post.BILL_AMOUNT) as varchar(20))  FROM MST_DCPS_POST_EMPLOYER_CONTRI post inner join NSDL_REPORT nsdl on "); 
		Strbld.append(" nsdl.YEAR_ID=post.FIN_YEAR and post.AC_DCPS_MAINTAINED_BY=nsdl.AC_MAINTAINED_BY and post.BILL_NO=nsdl.BILL_NO and BILL_GEN='Y' "); 
		Strbld.append(" where post.AC_DCPS_MAINTAINED_BY=:acMainBy and post.FIN_YEAR=:finYear and BILL_GEN_DATE is not null"); 
		
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		
		lQuery.setParameter("acMainBy",acMainBy);
		lQuery.setParameter("finYear",finYear);
		
		listAmount=lQuery.list();
		gLogger.error("listAmount is  ** :"+listAmount );
		gLogger.error("listAmount size is **:"+listAmount.size());
		if (listAmount.size() != 0 && listAmount != null && listAmount.get(0)!=null) 
		{
			logger.info("listAmount.get(0)*******"+listAmount.get(0));
			tContri=Long.parseLong(listAmount.get(0).toString());
		}
	
        
	}
		 catch (Exception e) {
				e.printStackTrace();
				gLogger.error("Error is :" + e, e);
				throw(e);
			}
		 return tContri;
}
	
	public Long CheckBillAmtforbillnoChallan(String acMainBy ,Long finYear)throws Exception  {
		// TODO Auto-generated method stub
		Long tContri=0L;
		List  listAmount = new ArrayList();
		try
		{		
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" SELECT cast(sum(nsdl.FILE_AMOUNT) as bigint)  FROM  NSDL_REPORT nsdl where   "); 
		Strbld.append(" nsdl.YEAR_ID=:finYear and nsdl.AC_MAINTAINED_BY =:acMainBy and nsdl.IS_DEPUTATION='Y' and nsdl.BILL_GEN='Y' "); 
		
		
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		
		lQuery.setParameter("acMainBy",acMainBy);
		lQuery.setParameter("finYear",finYear);
		
		listAmount=lQuery.list();
		gLogger.error("listAmount is  ** :"+listAmount );
		gLogger.error("listAmount size is **:"+listAmount.size());
		if (listAmount.size() != 0 && listAmount != null && listAmount.get(0)!=null) 
		{
			logger.info("listAmount.get(0)*******"+listAmount.get(0));
			tContri=Long.parseLong(listAmount.get(0).toString());
		}
	
        
	}
		 catch (Exception e) {
				e.printStackTrace();
				gLogger.error("Error is :" + e, e);
				throw(e);
			}
		 return tContri;
}
	

	
}
