package com.tcs.sgv.dcps.dao;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsContributionYearly;

public class NsdlSrkaFileGeneDAOImpl extends GenericDaoHibernateImpl {
	private Session ghibSession = null;
	private final Logger gLogger = Logger.getLogger(getClass());

	public NsdlSrkaFileGeneDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}

	
/*
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
*/


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
			sb.append("   SELECT LOOKUP_ID,LOOKUP_DESC FROM CMN_LOOKUP_MST where LOOKUP_ID in (700240,700241,700242,700174)  ");
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
	/*	public List getEmployeeList(String acMain,String billno,String finType, String fromDate, String toDate)
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
			Strbld.append(" and post.BILL_NO=trn.EMPLR_BILL_NO  ");
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


	}*/

	//SRKA ashish
	public List getEmployeeListNsdl(String finType,String treasury)
	{
		List empLst = null;
		List finalList=null;


		StringBuilder  Strbld = new StringBuilder();

		try{

			
			Strbld.append(" select xyz.EMP_NAME,xyz.DCPS_ID,xyz.PRAN_NO,xyz.CONTRIB_EMP_DIFF,xyz.CONTRIB_EMPLR_DIFF,xyz.INT_EMP_DIFF,xyz.INT_EMPLR_DIFF,xyz.loc_name,xyz.dto_reg_no,xyz.ddo_reg_no,xyz.OPEN_INT_EMP,xyz.OPEN_INT_EMPLR from ( ");
			Strbld.append("   SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast((cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) - cast(nvl(ldata.EMP_CONTRI,0) as varchar) )as varchar)as CONTRIB_EMP_DIFF,cast((cast(sum(r3.CUR_EMPLR_CONTRIB)as varchar) - cast(nvl(ldata.EMPLR_CONTRI,0) as varchar) )as varchar)as CONTRIB_EMPLR_DIFF,DEC(cast(temp.INT_EMPL_CONTRIB as double) -cast(nvl(ldata.EMP_INT,0) as double ),25,2) as INT_EMP_DIFF, ");     
			Strbld.append("  DEC(cast(temp.INT_EMPLR_CONTRIB as double) -cast(nvl(ldata.EMPLR_INT,0) as double ),25,2) as INT_EMPLR_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
			Strbld.append("	DEC(cast(nvl(temp.OPEN_INT_EMP,0) as double) -cast(nvl(ldata.OPEN_EMP,0) as double ),25,2) as OPEN_INT_EMP,DEC(cast(nvl(temp.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) as OPEN_INT_EMPLR ");
			//Strbld.append("   ");
			//Strbld.append("    ");
			//Strbld.append("   "); 
			//Strbld.append("    ");
			Strbld.append("  FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR    ");
			Strbld.append("  inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'    ");
			Strbld.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO      ");
			Strbld.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");  
			Strbld.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");  
			Strbld.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE ");  
			
			
			Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM   DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");  
			Strbld.append("  where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and (temp.INT_EMPL_CONTRIB <> 0 or temp.OPEN_INT_EMP <> 0 ) and temp.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null) "); 
			Strbld.append("  group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,ldata.EMP_CONTRI,ldata.EMPLR_CONTRI,ldata.EMP_INT,ldata.EMPLR_INT,ldata.OPEN_EMP,ldata.OPEN_EMPLR,temp.INT_EMPL_CONTRIB,temp.INT_EMPL_CONTRIB,temp.OPEN_INT_EMP,temp.OPEN_INT_EMPLR "); 
			
			Strbld.append("  union all ");
			
			Strbld.append("   SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(0 as varchar) as CONTRIB_EMP_DIFF,cast(0 as varchar) as CONTRIB_EMPLR_DIFF, ");     
			Strbld.append("   DEC(0,25,2) as INT_EMP_DIFF,DEC(0,25,2) as INT_EMPLR_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
			Strbld.append("	DEC(cast(nvl(temp.OPEN_INT_EMP,0) as double) -cast(nvl(ldata.OPEN_EMP,0) as double ),25,2) as OPEN_INT_EMP,DEC(cast(nvl(temp.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) as OPEN_INT_EMPLR ");
			//sb1.append("   ");
			//sb1.append("    ");
			//sb1.append("   "); 
			//sb1.append("    ");
			Strbld.append("  FROM TEMPEMPR3 temp ");
			Strbld.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO      ");  
			Strbld.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");  
			Strbld.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");  
			Strbld.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE ");  
			Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM   DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");
			Strbld.append("  where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and  temp.OPEN_INT_EMP > 0 and temp.batch_id is not null and temp.OPEN_BATCH_ID is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)) xyz "); 
			Strbld.append("  order by xyz.ddo_reg_no ");
			
			 Query selectQuery1 = ghibSession.createSQLQuery(Strbld.toString());
			 empLst= selectQuery1.list();
			 
		
			
/* Previous code before update by akshay
		  	Strbld.append("  SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(sum(r3.CUR_EMPL_CONTRIB) as varchar),cast(sum(r3.CUR_EMPLR_CONTRIB)as varchar),cast(temp.INT_EMPL_CONTRIB as double),   ");   
			Strbld.append("	 cast(temp.INT_EMPLR_CONTRIB as double),loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no    ");
			Strbld.append("  FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR   "); 
			Strbld.append("  inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'   ");
			Strbld.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO     ");
			Strbld.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2)  ");
			Strbld.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
			Strbld.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE    ");
			Strbld.append("   where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and temp.INT_EMPL_CONTRIB<>0 ");
			Strbld.append("  group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB   order by  reg.ddo_reg_no ");
previous code end here		*/	
			
			
			
			/*Strbld.append(" where trn.REG_STATUS =1 and  post.AC_DCPS_MAINTAINED_BY = :acMain and post.BILL_NO= :billno  and post.FIN_YEAR= :finType   ");
			Strbld.append(" and emp.REG_STATUS=1 and mst.VOUCHER_STATUS=1 "); 
			Strbld.append(" and post.BILL_NO=trn.EMPLR_BILL_NO  ");
			Strbld.append(" and emp.PRAN_NO is NOT NULL and trn.CONTRIBUTION is not null  and trn.EMPLOYER_CONTRI_FLAG ='Y'  ");
			Strbld.append(" and ((trn.VOUCHER_DATE between '" + fromDate + "' AND '"+ toDate + "') or (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "')) AND emp.LOC_ID <> 380001 ");

			Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,mo.MONTH_NAME,trn.FIN_YEAR_ID,fin.FIN_YEAR_DESC ");   
			Strbld.append(" ,month(trn.STARTDATE),year(trn.STARTDATE),fin.FIN_YEAR_CODE,trn.EMPLR_BILL_NO,mo.month_id,trn.TYPE_OF_PAYMENT  ");
			Strbld.append(" order by EMP_NAME ");*/

			//ended By Ashish
	
			/*lQuery.setString("acMain",acMain);
			lQuery.setString("billno",billno);
			lQuery.setString("finType",finType);*/

			


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



	public void updateRepStatus(String dcpsId, String BatchId,String finyr,String treasury) {
		StringBuffer sb= new StringBuffer();
		//ghibSession = getSession();
		try{
			sb.append("update TEMPEMPR3 set batch_id="+BatchId+" ");

			sb.append(" where  emp_id_no='"+dcpsId+"' and  fin_year='"+finyr+"' and batch_id is null ");
			int i = ghibSession.createSQLQuery(sb.toString()).executeUpdate();

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void updateRepOpenStatus(String dcpsId, String BatchId,String finyr,String treasury) {
		StringBuffer sb= new StringBuffer();
		//ghibSession = getSession();
		try{
			sb.append("update TEMPEMPR3 set OPEN_BATCH_ID="+BatchId+" ");

			sb.append(" where  emp_id_no='"+dcpsId+"' and  fin_year='"+finyr+"' and OPEN_BATCH_ID is null ");
			int i = ghibSession.createSQLQuery(sb.toString()).executeUpdate();

		}catch(Exception e){
			e.printStackTrace();
		}
	}

/*	public String getStatus(String acMain, String monthid, String yearid) {
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

	}*/

	public void insertDataForNSDLRepo(String BatchId,String finYr,String treasury) {

		StringBuilder  Strbld = new StringBuilder();
		try{

			Strbld.append(" insert into MST_NSDL_SRKA_GEN (FIN_YEAR,loc_id,BATCH_ID,CREATED_DATE,IS_GENE) values ('"+finYr+"','"+treasury+"','"+BatchId+"',sysdate,'N') ");

			int i = ghibSession.createSQLQuery(Strbld.toString()).executeUpdate();
			//ghibSession.connection().commit();
			logger.info("Query is *************"+Strbld.toString());
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	
	public void updateDataForNSDLRepo(String BatchId,String finYr,String treasury) {

		StringBuilder  Strbld = new StringBuilder();
		try{

			//Strbld.append(" insert into MST_NSDL_SRKA_GEN (FIN_YEAR,loc_id,BATCH_ID,CREATED_DATE,IS_GENE) values ('"+finYr+"','"+treasury+"','"+BatchId+"',sysdate,'N') ");
			Strbld.append("update MST_NSDL_SRKA_GEN set batch_id='"+BatchId+"' where FIN_YEAR='"+finYr+"' and LOC_ID='"+treasury+"' ");

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
		Strbld.append(" Select max(BATCH_ID) from NSDL_REPORT ");
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		List transactionId=lQuery.list();
		if(transactionId.size()>0)

			BATCH_ID= transactionId.get(0).toString();

		return BATCH_ID;
	}

	public List selectTrnPk(String finYr,String treasury)throws Exception {


		List contrList = null;
		Query lQuery = null;
		StringBuilder lSBQuery = null;
		try{
			lSBQuery = new StringBuilder();
			
			

			lSBQuery.append(" select DISTINCT DCPS_ID  from ( select  abc.EMP_NAME,abc.DCPS_ID,abc.PRAN_NO,abc.CONTRIB_EMP_DIFF, ");
			lSBQuery.append(" abc.CONTRIB_EMPLR_DIFF, ");
			lSBQuery.append(" abc.INT_EMP_DIFF,      ");
			lSBQuery.append(" abc.INT_EMPLR_DIFF,abc.loc_name,abc.dto_reg_no,abc.ddo_reg_no,abc.OPEN_INT_EMP,abc.OPEN_INT_EMPLR from ( ");
			lSBQuery.append(" SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast((cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) - cast(nvl(ldata.EMP_CONTRI,0) as varchar)) as varchar)as CONTRIB_EMP_DIFF, ");
			lSBQuery.append(" cast((cast(sum(r3.CUR_EMPLR_CONTRIB)as varchar) - cast(nvl(ldata.EMPLR_CONTRI,0) as varchar))as varchar)as CONTRIB_EMPLR_DIFF, ");
			lSBQuery.append(" DEC(cast(temp.INT_EMPL_CONTRIB as double) -cast(nvl(ldata.EMP_INT,0) as double ) ,25,2) as INT_EMP_DIFF,      ");
			lSBQuery.append(" DEC(cast(temp.INT_EMPLR_CONTRIB as double) -cast(nvl(ldata.EMPLR_INT,0) as double ) ,25,2) as INT_EMPLR_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,  ");  
			lSBQuery.append(" DEC(cast(nvl(temp.OPEN_INT_EMP,0) as double) -cast(nvl(ldata.OPEN_EMP,0) as double ),25,2) as OPEN_INT_EMP,DEC(cast(nvl(temp.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) as OPEN_INT_EMPLR ");
			lSBQuery.append(" FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR    ");
			lSBQuery.append(" inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'   ");
			lSBQuery.append(" inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO     ");
			lSBQuery.append(" inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");
			lSBQuery.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
			lSBQuery.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE   ");
			lSBQuery.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");
			lSBQuery.append(" where temp.FIN_YEAR='"+finYr+"' and loc.loc_id='"+treasury+"' and emp.AC_DCPS_MAINTAINED_BY=700174  ");
			lSBQuery.append("  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and temp.INT_EMPL_CONTRIB<>0 and temp.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
			lSBQuery.append(" group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
			lSBQuery.append(" temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,ldata.EMP_CONTRI,ldata.EMPLR_CONTRI,ldata.EMP_INT, ");
			lSBQuery.append(" ldata.EMPLR_INT,ldata.OPEN_EMP,ldata.OPEN_EMPLR,temp.INT_EMPL_CONTRIB,temp.INT_EMPL_CONTRIB,temp.OPEN_INT_EMP,temp.OPEN_INT_EMPLR) abc  where (abc.CONTRIB_EMP_DIFF <> 0 or abc.INT_EMP_DIFF <> 0 or abc.OPEN_INT_EMPLR <> 0 ) ) ");
			
			
			
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

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
	public List selectTrnPkForOpen(String finYr,String treasury)throws Exception {


		List contrList = null;
		Query lQuery = null;
		StringBuilder lSBQuery = null;
		try{
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT distinct emp.DCPS_ID   FROM TEMPEMPR3 temp     "); 
			lSBQuery.append(" inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO      ");
			lSBQuery.append(" inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2)  ");
			lSBQuery.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
			lSBQuery.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE    ");
			lSBQuery.append(" left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");
			lSBQuery.append(" where temp.FIN_YEAR='"+finYr+"' and loc.loc_id ='"+treasury+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and DEC(cast(nvl(temp.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) > 0  and temp.OPEN_BATCH_ID is null and temp.batch_id is not null and (emp.EMP_SERVEND_DT  >= '2015-04-01' or emp.EMP_SERVEND_DT is null)   ");
			
			
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

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
	public List selectDataForNSDLGen(String finType,String treasury) {
		// TODO Auto-generated method stub

		List temp=null;
		String batchId=null;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" SELECT * FROM MST_NSDL_SRKA_GEN where  FIN_YEAR = '"+finType+"' and loc_id='"+treasury+"' "); 

		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());


		temp=lQuery.list();
		logger.info("temp size"+temp.size());

		return temp;
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
	/*public List selectBillNo(String aisType,String finTypeSelected) {
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
	}*/
	/*public String selectfinYearforBill(String acMain,String billNo,String finType) {

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
	}*/

/*	public void updatefinYear(String aisType,String finYear,String billNo,String BatchId) {
		StringBuffer sb= new StringBuffer();
		//ghibSession = getSession();
		try{
			sb.append(" update NSDL_REPORT set YEAR_ID='"+finYear+"',NSDL_STATUS='G' where BILL_NO='"+billNo+"' and AC_MAINTAINED_BY='"+aisType+"' and BATCH_ID="+BatchId+" ")	;

			int i = ghibSession.createSQLQuery(sb.toString()).executeUpdate();

			logger.info("Query is *************"+sb.toString());



			logger.info("Query is *************"+i );

			logger.info("Number of row updated is *************"+i);

		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
	/*public String accMain(String accMain) {
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
*/
	/*public void updateAgainNsdl(Long batchId,String acMain, String billNo,String finType) {

		logger.info("batchId is ****"+batchId);
		logger.info("acMain is ****"+acMain);

		logger.info("billNo is ****"+billNo);
		try{
			StringBuffer sb= new StringBuffer();
			Query lQuery = null;

			sb.append(" update NSDL_REPORT set BATCH_ID="+batchId+"  where AC_MAINTAINED_BY='"+acMain+"' and BILL_NO='"+billNo+"' and YEAR_ID='"+finType+"'  ");
			lQuery = ghibSession.createSQLQuery(sb.toString());
			int i = lQuery.executeUpdate();
			logger.info("Query is *************"+sb.toString());
			logger.info("Number of row updated is *************"+i);

		}catch(Exception e){
			e.printStackTrace();
		}

	}*/

	public List getFinyeardesc() {

		String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2007' and '2011' order by finYearCode ASC";
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

	public String getEmployeeIntList(String finType,String treasury) {

		List empLst = null;
		String Intamount=null;


		StringBuilder  Strbld = new StringBuilder();


/*added by akshay added (INT_EMP_DIFF) and removed cast(sum(abc.b) as varchar),*/		
		Strbld.append(" 	 select cast(sum(abc.INT_EMP_DIFF) as double) from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) as a,cast(temp.INT_EMPL_CONTRIB as double) as b,DEC(cast(temp.INT_EMPL_CONTRIB as double) - cast(nvl(ldata.EMP_INT,0) as double ),25,2) as INT_EMP_DIFF,  loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg  ");   
		Strbld.append(" FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO  "); 
		Strbld.append(" and temp.FIN_YEAR=r3.FIN_YEAR  ");
		Strbld.append(" inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'    ");
		Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO ");
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");
		Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");
		Strbld.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE   "); 
/*added by akshay*/		Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI, sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");  

		Strbld.append(" where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"' and emp.AC_DCPS_MAINTAINED_BY=700174 and emp.pran_no is not null  and emp.PRAN_ACTIVE=1 and temp.batch_id is  null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)   ");
		Strbld.append(" and temp.INT_EMPL_CONTRIB<>0 ");
		Strbld.append("  group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,ldata.EMP_INT  order by  reg.ddo_reg_no ) abc  ");
	

		//Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,temp.INT_EMPL_CONTRIB,temp.INT_EMPLR_CONTRIB ");
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		logger.info("script for all employee ---------"+lQuery.toString() );

		/*empLst = lQuery.list();
		if(empLst.size()>0 || empLst!=null ){
			 Intamount=empLst.get(0).toString();
		}*/
		
		
		
		empLst = lQuery.list();
		if(empLst!=null && empLst.size()>0){
		if (empLst.get(0)!=null)
		{
		Intamount=empLst.get(0).toString();
		}

		}
		
		
		
		
		return Intamount;
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
	public Long getNextSeqNum(String generatePkForBatchId){


		Long genId=0l;
		List lGenIdForMonthly=null;

		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsContributionYearly lObjMstDcpsContributionYearly = null;
		Query lQuery = null;

		lSBQuery.append(" SELECT GENERATED_ID FROM INT_TABLE_SEQ_MST where UPPER(TABLE_NAME)= :generatePkForBatchId ");
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("generatePkForBatchId", generatePkForBatchId);

		lGenIdForMonthly = lQuery.list();
		if(lGenIdForMonthly.size()>0 && lGenIdForMonthly.get(0)!=null){
			genId=Long.parseLong(lGenIdForMonthly.get(0).toString());
		}

		return genId;


	}
	public void updateGeneratedId(Long dcpsContributiongeneratePkForBatchId,String tableName){
		Session session = getSession();
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" UPDATE INT_TABLE_SEQ_MST SET GENERATED_ID=:generatedId WHERE UPPER(TABLE_NAME) = :tableName");

		Query lQuery = session.createSQLQuery(lSBQuery.toString());
		lQuery.setLong("generatedId", dcpsContributiongeneratePkForBatchId);
		lQuery.setParameter("tableName", tableName);

		int status = lQuery.executeUpdate();

	}
	public String getbatchIdForNsdl(String finType,String treasury) {
		// TODO Auto-generated method stub

		List temp=null;
		String batchId=null;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" SELECT BATCH_ID FROM MST_NSDL_SRKA_GEN where  FIN_YEAR = '"+finType+"' and loc_id='"+treasury+"' and is_gene='N' "); 

		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());


		temp=lQuery.list();
		logger.info("temp size"+temp.size());
		if(temp!=null && temp.size()>0){
			batchId=temp.get(0).toString();
		}

		return batchId;
	}
	
	public String getTranbatchIdForNsdl(String finType,String treasury) {
		// TODO Auto-generated method stub

		List temp=null;
		String batchId=null;
		StringBuilder  Strbld = new StringBuilder();
		Strbld.append(" SELECT BATCH_ID FROM MST_NSDL_SRKA_GEN where  FIN_YEAR = '"+finType+"' and loc_id='"+treasury+"' and is_gene='Y' "); 

		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());


		temp=lQuery.list();
		logger.info("temp size"+temp.size());
		if(temp!=null && temp.size()>0){
			batchId=temp.get(0).toString();
		}

		return batchId;
	}

	public String getEmployeeContriTotalList(String finType,String treasury) {

		List empLst = null;


		String amountTotal=null;
		StringBuilder  Strbld = new StringBuilder();


/*added by akshay added cast(CONTRIB_EMP_DIFF as double)+cast(INT_EMP_DIFF as double) and removed cast(sum(abc.a) as double)+cast(sum(abc.b) as double),*/		
		Strbld.append("    select cast(sum(abc.CONTRIB_EMP_DIFF) as double)   from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) ,cast(temp.INT_EMPL_CONTRIB as double) , cast((cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) - cast(nvl(ldata.EMP_CONTRI,0) as varchar)) as varchar)as CONTRIB_EMP_DIFF,DEC(cast(temp.INT_EMPL_CONTRIB as double) -cast(nvl(ldata.EMP_INT,0) as double ),25,2) as INT_EMP_DIFF,  loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ");   
		Strbld.append(" FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO  "); 
		Strbld.append(" and temp.FIN_YEAR=r3.FIN_YEAR  ");
		Strbld.append(" inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'    ");
		Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO ");
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2)  ");
		Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");
		Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE   ");
/*added by akshay*/		Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI, sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");   
		Strbld.append(" where temp.FIN_YEAR='"+finType+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='"+treasury+"' and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and  temp.INT_EMPL_CONTRIB <> 0 and temp.batch_id is null and (emp.EMP_SERVEND_DT  >= '2015-04-01' or emp.EMP_SERVEND_DT is null)   ");
		Strbld.append("    group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,ldata.EMP_CONTRI,ldata.EMP_INT  ) abc ");

		//Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,temp.INT_EMPL_CONTRIB,temp.INT_EMPLR_CONTRIB ");
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		logger.info("script for all employee ---------"+lQuery.toString() );

		empLst = lQuery.list();
		if(empLst.size()>0 || empLst!=null ){
			if(empLst.get(0)!=null)
			{
				amountTotal=empLst.get(0).toString();
			}
			
		}
		
		return amountTotal;
	}
	
	public String getEmployeeContriOpenTotalList(String finType,String treasury) {

		List empLst = null;


		String amountTotal=null;
		StringBuilder  Strbld = new StringBuilder();


/*added by akshay added cast(CONTRIB_EMP_DIFF as double)+cast(INT_EMP_DIFF as double) and removed cast(sum(abc.a) as double)+cast(sum(abc.b) as double),*/		
		Strbld.append("    select cast(sum(abc.OPEN_INT_EMP) as double)   from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) as a,cast(temp.INT_EMPL_CONTRIB as double) as b, cast((cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) - cast(nvl(ldata.EMP_CONTRI,0) as varchar)) as varchar)as CONTRIB_EMP_DIFF,DEC(cast(temp.INT_EMPL_CONTRIB as double) -cast(nvl(ldata.EMP_INT,0) as double ),25,2) as INT_EMP_DIFF,  loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg ,cast(nvl(temp.OPEN_INT_EMP,0) as double ) as OPEN_INT_EMP,cast(nvl(temp.OPEN_INT_EMPLR,0) as double ) as OPEN_INT_EMPLR       ");   
		Strbld.append(" FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO  "); 
		Strbld.append(" and temp.FIN_YEAR=r3.FIN_YEAR  ");
		Strbld.append(" inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'    ");
		Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO ");
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2)  ");
		Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");
		Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE   ");
/*added by akshay*/		Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI, sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");   
		Strbld.append(" where temp.FIN_YEAR='"+finType+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='"+treasury+"' and emp.pran_no is not null  and emp.PRAN_ACTIVE=1 and  temp.INT_EMPL_CONTRIB <> 0 and temp.batch_id is null and (emp.EMP_SERVEND_DT  >= '2015-04-01' or emp.EMP_SERVEND_DT is null)   ");
		Strbld.append("    group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,ldata.EMP_CONTRI,ldata.EMP_INT,temp.OPEN_INT_EMP,temp.OPEN_INT_EMPLR  order by  reg.ddo_reg_no ) abc ");

		//Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,temp.INT_EMPL_CONTRIB,temp.INT_EMPLR_CONTRIB ");
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		logger.info("script for all employee ---------"+lQuery.toString() );

		empLst = lQuery.list();
		if(empLst.size()>0 || empLst!=null ){
			if(empLst.get(0)!=null)
			{
				amountTotal=empLst.get(0).toString();
			}
			
		}
		
		StringBuilder sb1 = new StringBuilder();
		
		sb1.append("   select sum(abc.OPEN_INT_EMP) from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(0 as varchar) as CONTRIB_EMP_DIFF,cast(0 as varchar) as CONTRIB_EMPLR_DIFF,cast(0 as varchar) as INT_EMP_DIFF, ");     
		sb1.append("  cast(0 as varchar) as INT_EMPLR_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
		sb1.append("	cast(nvl(temp.OPEN_INT_EMP,0) as double ) as OPEN_INT_EMP,cast(nvl(temp.OPEN_INT_EMPLR,0) as double ) as OPEN_INT_EMPLR ");
		//sb1.append("   ");
		//sb1.append("    ");
		//sb1.append("   "); 
		//sb1.append("    ");
		sb1.append("  FROM TEMPEMPR3 temp ");
		sb1.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO      ");
		sb1.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");  
		sb1.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");  
		sb1.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE ");  
		sb1.append("  where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and  temp.OPEN_INT_EMP > 0 and temp.batch_id is not null and temp.OPEN_BATCH_ID is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null) "); 
		sb1.append("  ) abc ");
		
		 Query selectQuery1 = ghibSession.createSQLQuery(sb1.toString());
		 List empLstOpening= selectQuery1.list();
		 
		 if(empLstOpening!=null  && empLstOpening.size()>0){
				if(empLstOpening.get(0)!=null)
				{
					Double d=0d;
					if(amountTotal!=null)
					{
						d= Double.parseDouble(amountTotal)+Double.parseDouble(empLstOpening.get(0).toString());
					}
					else
					{
						d=Double.parseDouble(empLstOpening.get(0).toString());
					}
					
					amountTotal=d.toString();
				}
				
			}
		
		return amountTotal;
	}
	
	public String getEmployerContriOpenTotalList(String finType,String treasury) {

		List empLst = null;


		String amountTotal=null;
		StringBuilder  Strbld = new StringBuilder();


/*added by akshay added cast(CONTRIB_EMP_DIFF as double)+cast(INT_EMP_DIFF as double) and removed cast(sum(abc.a) as double)+cast(sum(abc.b) as double),*/		
		Strbld.append("    select cast(sum(abc.OPEN_INT_EMPLR) as double)   from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) ,cast(temp.INT_EMPL_CONTRIB as double) , cast((cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) - cast(nvl(ldata.EMP_CONTRI,0) as varchar)) as varchar)as CONTRIB_EMP_DIFF,DEC(cast(temp.INT_EMPL_CONTRIB as double) -cast(nvl(ldata.EMP_INT,0) as double ),25,2) as INT_EMP_DIFF,  loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,DEC(cast(nvl(temp.OPEN_INT_EMP,0) as double) -cast(nvl(ldata.OPEN_EMP,0) as double ),25,2) as OPEN_INT_EMP,DEC(cast(nvl(temp.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) as OPEN_INT_EMPLR     ");   
		Strbld.append(" FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO  "); 
		Strbld.append(" and temp.FIN_YEAR=r3.FIN_YEAR  ");
		Strbld.append(" inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'    ");
		Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO ");
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2)  ");
		Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");
		Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE   ");
		Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");   
		Strbld.append(" where temp.FIN_YEAR='"+finType+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='"+treasury+"' and emp.pran_no is not null and emp.PRAN_ACTIVE=1  and  (temp.INT_EMPL_CONTRIB <> 0 or temp.OPEN_INT_EMP <> 0) and temp.batch_id is null and (emp.EMP_SERVEND_DT  >= '2015-04-01' or emp.EMP_SERVEND_DT is null)   ");
		Strbld.append("    group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,ldata.EMP_CONTRI,ldata.EMP_INT,ldata.OPEN_EMP,ldata.OPEN_EMPLR,temp.OPEN_INT_EMP,temp.OPEN_INT_EMPLR  ) abc ");

		//Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,temp.INT_EMPL_CONTRIB,temp.INT_EMPLR_CONTRIB ");
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		logger.info("script for all employee ---------"+lQuery.toString() );

		empLst = lQuery.list();
		if(empLst.size()>0 || empLst!=null ){
			if(empLst.get(0)!=null)
			{
				amountTotal=empLst.get(0).toString();
			}
			
		}
	
		
		StringBuilder sb1 = new StringBuilder();
		
		sb1.append("   select cast(sum(abc.OPEN_INT_EMPLR) as double) from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(0 as varchar) as CONTRIB_EMP_DIFF,cast(0 as varchar) as CONTRIB_EMPLR_DIFF,cast(0 as varchar) as INT_EMP_DIFF, ");     
		sb1.append("  cast(0 as varchar) as INT_EMPLR_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
		sb1.append("	DEC(cast(nvl(temp.OPEN_INT_EMP,0) as double) -cast(nvl(ldata.OPEN_EMP,0) as double ),25,2) as OPEN_INT_EMP,DEC(cast(nvl(temp.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) as OPEN_INT_EMPLR ");
		//sb1.append("   ");
		//sb1.append("    ");
		//sb1.append("   "); 
		//sb1.append("    ");
		sb1.append("  FROM TEMPEMPR3 temp ");
		sb1.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO ");
		sb1.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");  
		sb1.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");  
		sb1.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE "); 
		sb1.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");
		sb1.append("  where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and  temp.OPEN_INT_EMP > 0 and temp.batch_id is not null and temp.OPEN_BATCH_ID is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null) "); 
		sb1.append("  ) abc ");
		
		 Query selectQuery1 = ghibSession.createSQLQuery(sb1.toString());
		 List empLstOpening= selectQuery1.list();
		 
		 if(empLstOpening!=null  && empLstOpening.size()>0){
				if(empLstOpening.get(0)!=null)
				{
					Double d=0d;
					if(amountTotal!=null)
					{
						d= Double.parseDouble(amountTotal)+Double.parseDouble(empLstOpening.get(0).toString());
					}
					else
					{
						d=Double.parseDouble(empLstOpening.get(0).toString());
					}
					
					amountTotal=d.toString();
				}
				
			}
		
		return amountTotal;
	}
	
	
	public String getEmployeeContriTotalListInterest(String finType,String treasury) {

		List empLst = null;


		String amountTotal=null;
		StringBuilder  Strbld = new StringBuilder();


/*added by akshay added cast(CONTRIB_EMP_DIFF as double)+cast(INT_EMP_DIFF as double) and removed cast(sum(abc.a) as double)+cast(sum(abc.b) as double),*/		
		Strbld.append("    select cast(sum(final.INT_EMP_DIFF) as double) from (select * from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) ,cast(temp.INT_EMPL_CONTRIB as double) , cast((cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) - cast(nvl(ldata.EMP_CONTRI,0) as varchar)) as varchar)as CONTRIB_EMP_DIFF,DEC(cast(temp.INT_EMPL_CONTRIB as double) -cast(nvl(ldata.EMP_INT,0) as double ),25,2) as INT_EMP_DIFF, loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ");   
		Strbld.append(" FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO  "); 
		Strbld.append(" and temp.FIN_YEAR=r3.FIN_YEAR  ");
		Strbld.append(" inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'    ");
		Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO ");
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2)  ");
		Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");
		Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE   ");
/*added by akshay*/		Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI, sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");   
		Strbld.append(" where temp.FIN_YEAR='"+finType+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='"+treasury+"' and emp.pran_no is not null  and emp.PRAN_ACTIVE=1 and  temp.INT_EMPL_CONTRIB <> 0 and temp.batch_id is null and (emp.EMP_SERVEND_DT  >= '2015-04-01' or emp.EMP_SERVEND_DT is null)   ");
		Strbld.append("    group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,ldata.EMP_CONTRI,ldata.EMP_INT  ) abc where abc.INT_EMP_DIFF > 0) final");

		//Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,temp.INT_EMPL_CONTRIB,temp.INT_EMPLR_CONTRIB ");
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
		logger.info("script for all employee ---------"+lQuery.toString() );

		empLst = lQuery.list();
		if(empLst.size()>0 || empLst!=null ){
			if(empLst.get(0)!=null)
			{
				amountTotal=empLst.get(0).toString();
			}
			
		}
		
		return amountTotal;
	}
	
	
	public Long getDDoRegCount(String finType,String treasury ) {
		// TODO Auto-generated method stub

		List temp=null;
		Long regCount=null;
		StringBuilder  Strbld = new StringBuilder();

		Strbld.append("  SELECT count(distinct(reg.ddo_reg_no)) ");   
		Strbld.append(" FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO  "); 
		Strbld.append(" and temp.FIN_YEAR=r3.FIN_YEAR  ");
		Strbld.append(" inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'    ");
		Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO ");
		Strbld.append(" inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2)  ");
		Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");
		Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
/*added by akshay*/		Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI, sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");   

		Strbld.append("  where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null  and emp.PRAN_ACTIVE=1  and temp.batch_id is null and (emp.EMP_SERVEND_DT  >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  and temp.INT_EMPL_CONTRIB <> 0  ");

		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());


		temp=lQuery.list();
		logger.info("temp size"+temp.size());
		if(temp!=null && temp.size()>0){
			regCount=Long.parseLong(temp.get(0).toString());
		}

		return regCount;
	}
	
	public List getEmployeeList(String finType,String treasury)
	{
		List empLst = null;

		List finalList=null;

		StringBuilder  Strbld = new StringBuilder();

		try{

		/*	Strbld.append("  SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,r3.CUR_EMPL_CONTRIB,r3.CUR_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,      ");   
			Strbld.append("	 	temp.INT_EMPLR_CONTRIB,mo.MONTH_NAME,r3.PAY_YEAR,loc.loc_name,dto.dto_reg_no,mo.month_id   ");
			Strbld.append("  FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR   "); 
			Strbld.append("  inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'   ");
			Strbld.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO     ");
			Strbld.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(temp.TRY_CD,1,2) ");
			Strbld.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(temp.TRY_CD,1,2) and loc.department_id=100003 ");
			Strbld.append("  inner join mst_ddo_reg reg on reg.ddo_code=(temp.TRY_CD||temp.ddo_cd)  ");
			Strbld.append("   where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and temp.INT_EMPL_CONTRIB<>0 ");
			
			*/
			
			
			
			Strbld.append(" select xyz.EMP_NAME,xyz.DCPS_ID,xyz.PRAN_NO,xyz.CONTRIB_EMP_DIFF,xyz.CONTRIB_EMPLR_DIFF,xyz.INT_EMP_DIFF,xyz.INT_EMPLR_DIFF,xyz.loc_name,xyz.dto_reg_no,xyz.ddo_reg_no,xyz.OPEN_INT_EMP,xyz.OPEN_INT_EMPLR from ( ");
			
			Strbld.append(" select  abc.EMP_NAME,abc.DCPS_ID,abc.PRAN_NO,abc.CONTRIB_EMP_DIFF, ");
			Strbld.append(" abc.CONTRIB_EMPLR_DIFF, ");
			Strbld.append(" abc.INT_EMP_DIFF,      ");
			Strbld.append(" abc.INT_EMPLR_DIFF,abc.loc_name,abc.dto_reg_no,abc.ddo_reg_no,abc.OPEN_INT_EMP,abc.OPEN_INT_EMPLR from ( ");
			Strbld.append(" SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast((cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) - cast(nvl(ldata.EMP_CONTRI,0) as varchar)) as varchar)as CONTRIB_EMP_DIFF, ");
			Strbld.append(" cast((cast(sum(r3.CUR_EMPLR_CONTRIB)as varchar) - cast(nvl(ldata.EMPLR_CONTRI,0) as varchar))as varchar)as CONTRIB_EMPLR_DIFF, ");
			Strbld.append(" DEC(cast(temp.INT_EMPL_CONTRIB as double) -cast(nvl(ldata.EMP_INT,0) as double ) ,25,2) as INT_EMP_DIFF,      ");
			Strbld.append(" DEC(cast(temp.INT_EMPLR_CONTRIB as double) -cast(nvl(ldata.EMPLR_INT,0) as double ) ,25,2) as INT_EMPLR_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,  ");  
			Strbld.append("	DEC(cast(nvl(temp.OPEN_INT_EMP,0) as double) -cast(nvl(ldata.OPEN_EMP,0) as double ),25,2) as OPEN_INT_EMP,DEC(cast(nvl(temp.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) as OPEN_INT_EMPLR ");
			Strbld.append(" FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR    ");
			Strbld.append(" inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'   ");
			Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO     ");
			Strbld.append(" inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");
			Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
			Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE   ");
			Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");
			Strbld.append(" where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"' and emp.AC_DCPS_MAINTAINED_BY=700174  ");
			Strbld.append("  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and temp.INT_EMPL_CONTRIB<>0 and temp.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
			Strbld.append(" group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
			Strbld.append(" temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,ldata.EMP_CONTRI,ldata.EMPLR_CONTRI,ldata.EMP_INT, ");
			Strbld.append(" ldata.EMPLR_INT, ldata.OPEN_EMP,ldata.OPEN_EMPLR,temp.INT_EMPL_CONTRIB,temp.INT_EMPL_CONTRIB,temp.OPEN_INT_EMP,temp.OPEN_INT_EMPLR) abc  where (abc.CONTRIB_EMP_DIFF <> 0 or abc.INT_EMP_DIFF <> 0 or abc.OPEN_INT_EMPLR <> 0 ) ");
			
			
			Strbld.append(" union all ");

			
			Strbld.append("  select * from (  SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(0 as varchar) as CONTRIB_EMP_DIFF,cast(0 as varchar) as CONTRIB_EMPLR_DIFF, ");     
			Strbld.append("   DEC(0,25,2) as INT_EMP_DIFF,DEC(0,25,2) as INT_EMPLR_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
			Strbld.append("	DEC(cast(nvl(temp.OPEN_INT_EMP,0) as double) -cast(nvl(ldata.OPEN_EMP,0) as double ),25,2) as OPEN_INT_EMP,DEC(cast(nvl(temp.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) as OPEN_INT_EMPLR ");
			//sb1.append("   ");
			//sb1.append("    ");
			//sb1.append("   "); 
			//sb1.append("    ");
			Strbld.append("  FROM TEMPEMPR3 temp     ");
			Strbld.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO      ");
			Strbld.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");  
			Strbld.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");  
			Strbld.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE ");  
			Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");
			Strbld.append("  where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and  temp.OPEN_INT_EMP > 0 and temp.batch_id is not null and  temp.OPEN_BATCH_ID is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)) pqr where ( pqr.OPEN_INT_EMPLR <> 0 ) ) xyz "); 
			Strbld.append("  order by  xyz.ddo_reg_no ");
			
			 Query selectQuery1 = ghibSession.createSQLQuery(Strbld.toString());
			  empLst= selectQuery1.list();
			 
			


		}
		catch(Exception e)
		{
			logger.info("Error occer in  getEmployeeList ---------"+ e);
		}
		return empLst;


	}

	public String getEmployeeListDdoregNsdl(String finType,String treasury,String ddoRegNo)
	{
		List empLst = null;

		String empDdoLst = null;

		StringBuilder  Strbld = new StringBuilder();

		try{

/*added by akshay added cast(CONTRIB_EMP_DIFF as double)+cast(INT_EMP_DIFF as double) removed select cast(sum(abc.a) as double)+sum(abc.b),*/			
			Strbld.append("select cast(sum(CONTRIB_EMP_DIFF) as double) from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) as a,cast(temp.INT_EMPL_CONTRIB as double) as b, cast((cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) - cast(nvl(ldata.EMP_CONTRI,0) as varchar))as varchar)as CONTRIB_EMP_DIFF,DEC(cast(temp.INT_EMPL_CONTRIB as double) -cast(nvl(ldata.EMP_INT,0) as double ) ,25,2) as INT_EMP_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg  ");   
			//Strbld.append("	 cast(sum(temp.INT_EMPLR_CONTRIB) as varchar),loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no    ");
			Strbld.append("  FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR   "); 
			Strbld.append("  inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'   ");
			Strbld.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO     ");
			Strbld.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");
			Strbld.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
			Strbld.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE    ");
/*added by akshay*/		Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI, sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");   

			Strbld.append("   where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"'  and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null  and emp.PRAN_ACTIVE=1  and temp.INT_EMPL_CONTRIB <> 0  and temp.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
			Strbld.append("   group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,ldata.EMP_CONTRI,ldata.EMP_INT  order by  reg.ddo_reg_no ) abc ");//group by abc.ddoreg 
/*added by akshay*/	Strbld.append("  group by abc.ddoreg having abc.ddoreg='"+ddoRegNo+"' ");

			/*Strbld.append(" where trn.REG_STATUS =1 and  post.AC_DCPS_MAINTAINED_BY = :acMain and post.BILL_NO= :billno  and post.FIN_YEAR= :finType   ");
			Strbld.append(" and emp.REG_STATUS=1 and mst.VOUCHER_STATUS=1 "); 
			Strbld.append(" and post.BILL_NO=trn.EMPLR_BILL_NO  ");
			Strbld.append(" and emp.PRAN_NO is NOT NULL and trn.CONTRIBUTION is not null  and trn.EMPLOYER_CONTRI_FLAG ='Y'  ");
			Strbld.append(" and ((trn.VOUCHER_DATE between '" + fromDate + "' AND '"+ toDate + "') or (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "')) AND emp.LOC_ID <> 380001 ");

			Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,mo.MONTH_NAME,trn.FIN_YEAR_ID,fin.FIN_YEAR_DESC ");   
			Strbld.append(" ,month(trn.STARTDATE),year(trn.STARTDATE),fin.FIN_YEAR_CODE,trn.EMPLR_BILL_NO,mo.month_id,trn.TYPE_OF_PAYMENT  ");
			Strbld.append(" order by EMP_NAME ");*/

			//ended By Ashish
			logger.info("   ---------"+Strbld.toString() );
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
			logger.info("script for all employee ---------"+lQuery.toString() );
			/*lQuery.setString("acMain",acMain);
			lQuery.setString("billno",billno);
			lQuery.setString("finType",finType);*/

			empLst = lQuery.list();
			if(empLst!=null && empLst.size()>0 && empLst.get(0)!=null)
			{
				empDdoLst=empLst.get(0).toString();
			}
		}
		catch(Exception e)
		{
			logger.info("Error occer in  getEmployeeList ---------"+ e);
		}
		return empDdoLst;


	}
	
	
	public String getEmployeeOpenIntListDdoregNsdl(String finType,String treasury,String ddoRegNo)
	{
		List empLst = null;

		String empDdoLst = null;

		StringBuilder  Strbld = new StringBuilder();

		try{

/*added by akshay added cast(CONTRIB_EMP_DIFF as double)+cast(INT_EMP_DIFF as double) removed select cast(sum(abc.a) as double)+sum(abc.b),*/			
			Strbld.append("select cast(sum(OPEN_INT_EMP) as double) from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) as a,cast(temp.INT_EMPL_CONTRIB as double) as b, cast((cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) - cast(nvl(ldata.EMP_CONTRI,0) as varchar))as varchar)as CONTRIB_EMP_DIFF,DEC(cast(temp.INT_EMPL_CONTRIB as double) -cast(nvl(ldata.EMP_INT,0) as double ) ,25,2) as INT_EMP_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg,cast(nvl(temp.OPEN_INT_EMP,0) as double ) as OPEN_INT_EMP,cast(nvl(temp.OPEN_INT_EMPLR,0) as double ) as OPEN_INT_EMPLR  ");   
			//Strbld.append("	 cast(sum(temp.INT_EMPLR_CONTRIB) as varchar),loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no    ");
			Strbld.append("  FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR   "); 
			Strbld.append("  inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'   ");
			Strbld.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO     ");
			Strbld.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");
			Strbld.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
			Strbld.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE    ");
/*added by akshay*/		Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI, sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");   

			Strbld.append("   where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"'  and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null   and emp.PRAN_ACTIVE=1  and temp.INT_EMPL_CONTRIB <> 0 and temp.OPEN_INT_EMP <> 0  and temp.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
			Strbld.append("   group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,ldata.EMP_CONTRI,ldata.EMP_INT ,temp.OPEN_INT_EMP,temp.OPEN_INT_EMPLR order by  reg.ddo_reg_no ) abc ");//group by abc.ddoreg 
/*added by akshay*/	Strbld.append("  group by abc.ddoreg having abc.ddoreg='"+ddoRegNo+"' ");

			/*Strbld.append(" where trn.REG_STATUS =1 and  post.AC_DCPS_MAINTAINED_BY = :acMain and post.BILL_NO= :billno  and post.FIN_YEAR= :finType   ");
			Strbld.append(" and emp.REG_STATUS=1 and mst.VOUCHER_STATUS=1 "); 
			Strbld.append(" and post.BILL_NO=trn.EMPLR_BILL_NO  ");
			Strbld.append(" and emp.PRAN_NO is NOT NULL and trn.CONTRIBUTION is not null  and trn.EMPLOYER_CONTRI_FLAG ='Y'  ");
			Strbld.append(" and ((trn.VOUCHER_DATE between '" + fromDate + "' AND '"+ toDate + "') or (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "')) AND emp.LOC_ID <> 380001 ");

			Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,mo.MONTH_NAME,trn.FIN_YEAR_ID,fin.FIN_YEAR_DESC ");   
			Strbld.append(" ,month(trn.STARTDATE),year(trn.STARTDATE),fin.FIN_YEAR_CODE,trn.EMPLR_BILL_NO,mo.month_id,trn.TYPE_OF_PAYMENT  ");
			Strbld.append(" order by EMP_NAME ");*/

			//ended By Ashish
			logger.info("   ---------"+Strbld.toString() );
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
			logger.info("script for all employee ---------"+lQuery.toString() );
			/*lQuery.setString("acMain",acMain);
			lQuery.setString("billno",billno);
			lQuery.setString("finType",finType);*/

			empLst = lQuery.list();
			if(empLst!=null && empLst.size()>0 && empLst.get(0)!=null)
			{
				empDdoLst=empLst.get(0).toString();
			}
			StringBuilder sb1 = new StringBuilder();
			sb1.append("   select sum(abc.OPEN_INT_EMP) from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(0 as varchar) as CONTRIB_EMP_DIFF,cast(0 as varchar) as CONTRIB_EMPLR_DIFF,cast(0 as varchar) as INT_EMP_DIFF, ");     
			sb1.append("  cast(0 as varchar) as INT_EMPLR_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
			sb1.append("	cast(nvl(temp.OPEN_INT_EMP,0) as double ) as OPEN_INT_EMP,cast(nvl(temp.OPEN_INT_EMPLR,0) as double ) as OPEN_INT_EMPLR ");
			//sb1.append("   ");
			//sb1.append("    ");
			//sb1.append("   "); 
			//sb1.append("    ");
			sb1.append("  FROM TEMPEMPR3 temp ");
			sb1.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO      ");
			sb1.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");  
			sb1.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");  
			sb1.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE ");  
			sb1.append("  where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and  temp.OPEN_INT_EMP > 0 and temp.batch_id is not null and temp.OPEN_BATCH_ID is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null) "); 
			sb1.append("  ) abc group by abc.ddo_reg_no having abc.ddo_reg_no='"+ddoRegNo+"' ");
			
			 Query selectQuery1 = ghibSession.createSQLQuery(sb1.toString());
			 List empLstOpening= selectQuery1.list();
			 
			 if(empLstOpening!=null  && empLstOpening.size()>0){
					if(empLstOpening.get(0)!=null)
					{
						Double d=0d;
						if(empDdoLst!=null)
						{
							d= Double.parseDouble(empDdoLst)+Double.parseDouble(empLstOpening.get(0).toString());
						}
						else
						{
							d=Double.parseDouble(empLstOpening.get(0).toString());
						}
						
						empDdoLst=d.toString();
					}
					
				}
		}
		catch(Exception e)
		{
			logger.info("Error occer in  getEmployeeList ---------"+ e);
		}
		return empDdoLst;


	}
	
	public String getEmployerOpenIntListDdoregNsdl(String finType,String treasury,String ddoRegNo)
	{
		List empLst = null;

		String empDdoLst = null;

		StringBuilder  Strbld = new StringBuilder();

		try{

/*added by akshay added cast(CONTRIB_EMP_DIFF as double)+cast(INT_EMP_DIFF as double) removed select cast(sum(abc.a) as double)+sum(abc.b),*/			
			Strbld.append("select cast(sum(OPEN_INT_EMPLR) as double) from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) as a,cast(temp.INT_EMPL_CONTRIB as double) as b, cast((cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) - cast(nvl(ldata.EMP_CONTRI,0) as varchar))as varchar)as CONTRIB_EMP_DIFF,DEC(cast(temp.INT_EMPL_CONTRIB as double) -cast(nvl(ldata.EMP_INT,0) as double ) ,25,2) as INT_EMP_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg,cast(nvl(temp.OPEN_INT_EMP,0) as double ) as OPEN_INT_EMP,cast(nvl(temp.OPEN_INT_EMPLR,0) as double ) as OPEN_INT_EMPLR  ");   
			//Strbld.append("	 cast(sum(temp.INT_EMPLR_CONTRIB) as varchar),loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no    ");
			Strbld.append("  FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR   "); 
			Strbld.append("  inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'   ");
			Strbld.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO     ");
			Strbld.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");
			Strbld.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
			Strbld.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE    ");
/*added by akshay*/		Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI, sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");   

			Strbld.append("   where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"'  and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null  and emp.PRAN_ACTIVE=1  and temp.INT_EMPL_CONTRIB <> 0 and temp.OPEN_INT_EMP <> 0  and temp.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
			Strbld.append("   group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,ldata.EMP_CONTRI,ldata.EMP_INT,temp.OPEN_INT_EMP,temp.OPEN_INT_EMPLR  order by  reg.ddo_reg_no ) abc ");//group by abc.ddoreg 
/*added by akshay*/	Strbld.append("  group by abc.ddoreg having abc.ddoreg='"+ddoRegNo+"' ");

			/*Strbld.append(" where trn.REG_STATUS =1 and  post.AC_DCPS_MAINTAINED_BY = :acMain and post.BILL_NO= :billno  and post.FIN_YEAR= :finType   ");
			Strbld.append(" and emp.REG_STATUS=1 and mst.VOUCHER_STATUS=1 "); 
			Strbld.append(" and post.BILL_NO=trn.EMPLR_BILL_NO  ");
			Strbld.append(" and emp.PRAN_NO is NOT NULL and trn.CONTRIBUTION is not null  and trn.EMPLOYER_CONTRI_FLAG ='Y'  ");
			Strbld.append(" and ((trn.VOUCHER_DATE between '" + fromDate + "' AND '"+ toDate + "') or (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "')) AND emp.LOC_ID <> 380001 ");

			Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,mo.MONTH_NAME,trn.FIN_YEAR_ID,fin.FIN_YEAR_DESC ");   
			Strbld.append(" ,month(trn.STARTDATE),year(trn.STARTDATE),fin.FIN_YEAR_CODE,trn.EMPLR_BILL_NO,mo.month_id,trn.TYPE_OF_PAYMENT  ");
			Strbld.append(" order by EMP_NAME ");*/

			//ended By Ashish
			logger.info("   ---------"+Strbld.toString() );
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
			logger.info("script for all employee ---------"+lQuery.toString() );
			/*lQuery.setString("acMain",acMain);
			lQuery.setString("billno",billno);
			lQuery.setString("finType",finType);*/

			empLst = lQuery.list();
			if(empLst!=null && empLst.size()>0 && empLst.get(0)!=null)
			{
				empDdoLst=empLst.get(0).toString();
			}
			StringBuilder sb1 = new StringBuilder();
			sb1.append("   select sum(abc.OPEN_INT_EMPLR) from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(0 as varchar) as CONTRIB_EMP_DIFF,cast(0 as varchar) as CONTRIB_EMPLR_DIFF,cast(0 as varchar) as INT_EMP_DIFF, ");     
			sb1.append("  cast(0 as varchar) as INT_EMPLR_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
			sb1.append("	cast(nvl(temp.OPEN_INT_EMP,0) as double ) as OPEN_INT_EMP,cast(nvl(temp.OPEN_INT_EMPLR,0) as double ) as OPEN_INT_EMPLR ");
			//sb1.append("   ");
			//sb1.append("    ");
			//sb1.append("   "); 
			//sb1.append("    ");
			sb1.append("  FROM TEMPEMPR3 temp ");
			sb1.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO      ");
			sb1.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");  
			sb1.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");  
			sb1.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE ");  
			sb1.append("  where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and  temp.OPEN_INT_EMP > 0 and temp.batch_id is not null and temp.OPEN_BATCH_ID is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null) "); 
			sb1.append("  ) abc group by abc.ddo_reg_no having abc.ddo_reg_no='"+ddoRegNo+"' ");
			
			 Query selectQuery1 = ghibSession.createSQLQuery(sb1.toString());
			 List empLstOpening= selectQuery1.list();
			 
			 if(empLstOpening!=null  && empLstOpening.size()>0){
					if(empLstOpening.get(0)!=null)
					{
						Double d=0d;
						if(empDdoLst!=null)
						{
							d= Double.parseDouble(empDdoLst)+Double.parseDouble(empLstOpening.get(0).toString());
						}
						else
						{
							d=Double.parseDouble(empLstOpening.get(0).toString());
						}
						
						empDdoLst=d.toString();
					}
					
				}
		}
		catch(Exception e)
		{
			logger.info("Error occer in  getEmployeeList ---------"+ e);
		}
		return empDdoLst;


	}
	
	
	public String getEmployeeListDdoregNsdlInt(String finType,String treasury,String ddoRegNo)
	{
		List empLst = null;

		String empDdoLst = null;

		StringBuilder  Strbld = new StringBuilder();

		try{

/*added by akshay added cast(CONTRIB_EMP_DIFF as double)+cast(INT_EMP_DIFF as double) removed select cast(sum(abc.a) as double)+sum(abc.b),*/			
			Strbld.append("select cast(sum(final.INT_EMP_DIFF) as double) from (select * from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) as a,cast(temp.INT_EMPL_CONTRIB as double) as b, cast((cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) - cast(nvl(ldata.EMP_CONTRI,0) as varchar))as varchar)as CONTRIB_EMP_DIFF,DEC(cast(temp.INT_EMPL_CONTRIB as double) -cast(nvl(ldata.EMP_INT,0) as double ) ,25,2) as INT_EMP_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg  ");   
			//Strbld.append("	 cast(sum(temp.INT_EMPLR_CONTRIB) as varchar),loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no    ");
			Strbld.append("  FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR   "); 
			Strbld.append("  inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'   ");
			Strbld.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO     ");
			Strbld.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");
			Strbld.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
			Strbld.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE    ");
/*added by akshay*/		Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI, sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");   

			Strbld.append("   where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"'  and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1   and temp.INT_EMPL_CONTRIB <> 0  and temp.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
			Strbld.append("   group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,ldata.EMP_CONTRI,ldata.EMP_INT  order by  reg.ddo_reg_no ) abc ) final where final.INT_EMP_DIFF > 0  ");//group by abc.ddoreg 
/*added by akshay*/	Strbld.append("  group by final.ddoreg having final.ddoreg='"+ddoRegNo+"' ");

			/*Strbld.append(" where trn.REG_STATUS =1 and  post.AC_DCPS_MAINTAINED_BY = :acMain and post.BILL_NO= :billno  and post.FIN_YEAR= :finType   ");
			Strbld.append(" and emp.REG_STATUS=1 and mst.VOUCHER_STATUS=1 "); 
			Strbld.append(" and post.BILL_NO=trn.EMPLR_BILL_NO  ");
			Strbld.append(" and emp.PRAN_NO is NOT NULL and trn.CONTRIBUTION is not null  and trn.EMPLOYER_CONTRI_FLAG ='Y'  ");
			Strbld.append(" and ((trn.VOUCHER_DATE between '" + fromDate + "' AND '"+ toDate + "') or (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "')) AND emp.LOC_ID <> 380001 ");

			Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,mo.MONTH_NAME,trn.FIN_YEAR_ID,fin.FIN_YEAR_DESC ");   
			Strbld.append(" ,month(trn.STARTDATE),year(trn.STARTDATE),fin.FIN_YEAR_CODE,trn.EMPLR_BILL_NO,mo.month_id,trn.TYPE_OF_PAYMENT  ");
			Strbld.append(" order by EMP_NAME ");*/

			//ended By Ashish
			logger.info("   ---------"+Strbld.toString() );
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
			logger.info("script for all employee ---------"+lQuery.toString() );
			/*lQuery.setString("acMain",acMain);
			lQuery.setString("billno",billno);
			lQuery.setString("finType",finType);*/

			empLst = lQuery.list();
			if(empLst!=null && empLst.size()>0 && empLst.get(0)!=null)
			{
				empDdoLst=empLst.get(0).toString();
			}
		}
		catch(Exception e)
		{
			logger.info("Error occer in  getEmployeeList ---------"+ e);
		}
		return empDdoLst;


	}
	
	
	
	
	
	public String[] getEmployeeCountDdoregNsdl(String finType,String treasury)
	{
		List empLst = null;

		String [] empCountLst = new String[10000];

		StringBuilder  Strbld = new StringBuilder();

		try{

			Strbld.append(" SELECT sum(tmp.count1)FROM (SELECT count(DISTINCT emp.pran_no) as count1,reg.ddo_reg_no  as regno  ");   
			//Strbld.append("	 cast(sum(temp.INT_EMPLR_CONTRIB) as varchar),loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no    ");
			Strbld.append("  FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR   "); 
			Strbld.append("  inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'   ");
			Strbld.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO     ");
			Strbld.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2)  ");
			Strbld.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");
			Strbld.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE    ");
/*added by akshay*/		Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI, sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");   

			Strbld.append("   where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"' and  emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null  and emp.PRAN_ACTIVE=1 and (temp.INT_EMPL_CONTRIB<>0 or temp.OPEN_INT_EMP <> 0)  ");
			Strbld.append(" group by temp.emp_id_no,reg.ddo_reg_no  order by reg.ddo_reg_no )tmp group by tmp.regno ");
			/*Strbld.append(" where trn.REG_STATUS =1 and  post.AC_DCPS_MAINTAINED_BY = :acMain and post.BILL_NO= :billno  and post.FIN_YEAR= :finType   ");
			Strbld.append(" and emp.REG_STATUS=1 and mst.VOUCHER_STATUS=1 "); 
			Strbld.append(" and post.BILL_NO=trn.EMPLR_BILL_NO  ");
			Strbld.append(" and emp.PRAN_NO is NOT NULL and trn.CONTRIBUTION is not null  and trn.EMPLOYER_CONTRI_FLAG ='Y'  ");
			Strbld.append(" and ((trn.VOUCHER_DATE between '" + fromDate + "' AND '"+ toDate + "') or (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "')) AND emp.LOC_ID <> 380001 ");

			Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,mo.MONTH_NAME,trn.FIN_YEAR_ID,fin.FIN_YEAR_DESC ");   
			Strbld.append(" ,month(trn.STARTDATE),year(trn.STARTDATE),fin.FIN_YEAR_CODE,trn.EMPLR_BILL_NO,mo.month_id,trn.TYPE_OF_PAYMENT  ");
			Strbld.append(" order by EMP_NAME ");*/

			//ended By Ashish
			logger.info("   ---------"+Strbld.toString() );
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
			logger.info("script for all employee ---------"+lQuery.toString() );
			/*lQuery.setString("acMain",acMain);
			lQuery.setString("billno",billno);
			lQuery.setString("finType",finType);*/

			empLst = lQuery.list();
			for(int i=0;i<empLst.size();i++)
			empCountLst[i]=empLst.get(i).toString();
		}
		catch(Exception e)
		{
			logger.info("Error occer in  getEmployeeList ---------"+ e);
		}
		return empCountLst;


	}

	public Map<String, Long> getEmpCountZeroDdoregNsdl(String finType)
	{

		List<Object[]> empLst = null;
		String [] empCountLst = new String[10000];
		Map<String, Long> lMapEmpCountDtls = null;
		StringBuilder  Strbld = new StringBuilder();
		Long count=0l;
		String ddoRegNo=null;
		try{

			Strbld.append(" SELECT sum(tmp.count1), tmp.regno FROM (SELECT count(DISTINCT emp.pran_no) as count1,reg.ddo_reg_no  as regno  ");   
			//Strbld.append("	 cast(sum(temp.INT_EMPLR_CONTRIB) as varchar),loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no    ");
			Strbld.append("  FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR   "); 
			Strbld.append("  inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'   ");
			Strbld.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO     ");
			Strbld.append("  inner join MST_DTO_REG dto on  substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2)  ");
			Strbld.append("  inner join CMN_LOCATION_MST loc  on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
			Strbld.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE    ");
/*added by akshay*/		Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI, sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");   

			Strbld.append("   where temp.FIN_YEAR='"+finType+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1  ");
			Strbld.append(" group by temp.emp_id_no,reg.ddo_reg_no  order by reg.ddo_reg_no )tmp group by tmp.regno ");
			/*Strbld.append(" where trn.REG_STATUS =1 and  post.AC_DCPS_MAINTAINED_BY = :acMain and post.BILL_NO= :billno  and post.FIN_YEAR= :finType   ");
			Strbld.append(" and emp.REG_STATUS=1 and mst.VOUCHER_STATUS=1 "); 
			Strbld.append(" and post.BILL_NO=trn.EMPLR_BILL_NO  ");
			Strbld.append(" and emp.PRAN_NO is NOT NULL and trn.CONTRIBUTION is not null  and trn.EMPLOYER_CONTRI_FLAG ='Y'  ");
			Strbld.append(" and ((trn.VOUCHER_DATE between '" + fromDate + "' AND '"+ toDate + "') or (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "')) AND emp.LOC_ID <> 380001 ");

			Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,mo.MONTH_NAME,trn.FIN_YEAR_ID,fin.FIN_YEAR_DESC ");   
			Strbld.append(" ,month(trn.STARTDATE),year(trn.STARTDATE),fin.FIN_YEAR_CODE,trn.EMPLR_BILL_NO,mo.month_id,trn.TYPE_OF_PAYMENT  ");
			Strbld.append(" order by EMP_NAME ");*/

			//ended By Ashish
			logger.info("   ---------"+Strbld.toString() );
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
			logger.info("script for all employee ---------"+lQuery.toString() );
			/*lQuery.setString("acMain",acMain);
			lQuery.setString("billno",billno);
			lQuery.setString("finType",finType);*/

			empLst = lQuery.list();
			lMapEmpCountDtls = new HashMap<String, Long>();
			
			for (Object[] lArrObj : empLst) {
				count =  Long.parseLong(lArrObj[0].toString());
				
				logger.info("count : "+count);
				
			     ddoRegNo =   lArrObj[1].toString();
				
				if (ddoRegNo != null) {
					lMapEmpCountDtls.put(ddoRegNo, count);
				}
				
				
			}
			
			
		}
		catch(Exception e)
		{
			logger.info("Error occer in  getEmployeeList ---------"+ e);
		}
		return lMapEmpCountDtls;


	}
	public List getAllTreasuries() {

		ArrayList<ComboValuesVO> arrTreasury = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		String treasury_id = null;
		String treasury_name = null;

		try {			
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
			"select CM.loc_Id , CM.loc_Name from Cmn_Location_Mst CM where department_Id in (100003)  and CM.LANG_ID = 1 and CM.LOC_ID not in(9991,1111)  order by CM.loc_id  ");

			lCon = DBConnection.getConnection();
			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				treasury_id = lRs.getString("loc_Id");
				treasury_name = lRs.getString("loc_Name");
				vo.setId(treasury_id);
				vo.setDesc(treasury_id+"-"+treasury_name);
				arrTreasury.add(vo);
			}

		} catch (Exception e) {
			gLogger.info("Sql Exception:" + e, e);
		} finally {
			try {
				if (lStmt != null) {
					lStmt.close();
				}
				if (lRs != null) {
					lRs.close();
				}
				if (lCon != null) {
					lCon.close();
				}

				lStmt = null;
				lRs = null;
				lCon = null;
			} catch (Exception e) {				
				gLogger.info("Sql Exception:" + e, e);
			}
		}
		return arrTreasury;

	}
	public String getEmployeeRecordCountDdoregNsdl(String finType,String treasury,String ddoRegNo)
	{
		List empLst = null;

		String  empDdoLst=null;
		
		StringBuilder  Strbld = new StringBuilder();

		try{

			
			
			Strbld.append(" select sum(final.count1)+sum(final.count2)+sum(final.count3) as a from (select abc.EMP_NAME,abc.DCPS_ID,abc.PRAN_NO,abc.a,abc.b, ");
					
			Strbld.append(" case when abc.CONTRIB_EMP_DIFF = 0 then 0 else 1 end as count1, case when abc.INT_EMP_DIFF <= 0 then 0 else 1 end as count2, ");
			Strbld.append(" case when abc.OPEN_INT_EMP = 0 then 0 else 1 end as count3 , ");
			Strbld.append(" abc.loc_name,abc.dto_reg_no,abc.ddoreg      ");
					
			Strbld.append(" from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) as a,cast(temp.INT_EMPL_CONTRIB as double) as b, ");
			Strbld.append(" cast((cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) - cast(nvl(ldata.EMP_CONTRI,0) as varchar))as varchar)as CONTRIB_EMP_DIFF, ");
			Strbld.append(" DEC(cast(temp.INT_EMPL_CONTRIB as double) -cast(nvl(ldata.EMP_INT,0) as double ) ,25,2) as INT_EMP_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg ,     ");
			Strbld.append("	cast(nvl(temp.OPEN_INT_EMP,0) as double ) as OPEN_INT_EMP,cast(nvl(temp.OPEN_INT_EMPLR,0) as double ) as OPEN_INT_EMPLR ");		
			Strbld.append(" FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR     ");
			Strbld.append(" inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'    ");
			Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO      ");
			Strbld.append(" inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2)  ");
			Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003   ");
			Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE     ");
			Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI, sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");  

			Strbld.append(" where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"'  and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1  and temp.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)    and temp.INT_EMPL_CONTRIB <> 0   ");
			Strbld.append(" group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
			Strbld.append(" temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,ldata.EMP_CONTRI,ldata.EMP_INT, temp.OPEN_INT_EMP,temp.OPEN_INT_EMPLR order by  reg.ddo_reg_no ) abc )final  ");
			Strbld.append(" group by final.ddoreg having final.ddoreg='"+ddoRegNo+"' ");
			  
			  
		/*	Strbld.append(" select sum(final.count1)+sum(final.count2) as a from ( select  abc.EMP_NAME,abc.DCPS_ID,abc.PRAN_NO,abc.a,abc.CONTRIB_EMPLOYER,abc.b, ");   
			Strbld.append(" abc.c, abc.loc_name,abc.dto_reg_no,abc.ddoreg, abc.CONTRIB_EMP_DIFF,abc.CONTRIB_EMPLR_DIFF,abc.INT_EMP_DIFF,abc.INT_EMPLR_DIFF, "); 
			Strbld.append(" case when abc.CONTRIB_EMP_DIFF = 0 then 0 else 1 end as count1, case when abc.INT_EMP_DIFF = 0 then 0 else 1 end as count2 from ");
			Strbld.append(" ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE as a,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double) as b, ");
			Strbld.append(" cast(yr.INT_CONTRB_EMPLOYER as double) as c, loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg, ");
			Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");

			Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
			Strbld.append(" (cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )) as INT_EMP_DIFF, ");
			Strbld.append(" (cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )) as INT_EMPLR_DIFF ");
			Strbld.append(" FROM MST_DCPS_CONTRIBUTION_YEARLY yr ");
			Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID    ");
			Strbld.append(" inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");
			Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
			Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
			Strbld.append(" left outer join dcps_legacy_data ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code ");
			Strbld.append(" where yr.YEAR_ID='"+finType+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='"+treasury+"' and emp.pran_no is not null  ");
			Strbld.append(" order by  reg.ddo_reg_no ) abc ) final ");
			Strbld.append(" group by final.ddoreg ");
			Strbld.append(" having final.ddoreg='"+ddoRegNo+"' ");
			Strbld.append("");*/
			
			logger.info("   ---------"+Strbld.toString() );
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
			logger.info("script for all employee ---------"+lQuery.toString() );
			

			empLst = lQuery.list();
			if(empLst!=null && empLst.size()>0){
				empDdoLst=empLst.get(0).toString();
			}
			
			
			StringBuilder sb1 = new StringBuilder();
			
			sb1.append("   select sum(abc.count3) from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(0 as varchar) as CONTRIB_EMP_DIFF,cast(0 as varchar) as CONTRIB_EMPLR_DIFF,cast(0 as varchar) as INT_EMP_DIFF, ");     
			sb1.append("  cast(0 as varchar) as INT_EMPLR_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
			sb1.append("	cast(nvl(temp.OPEN_INT_EMP,0) as double ) as OPEN_INT_EMP,cast(nvl(temp.OPEN_INT_EMPLR,0) as double ) as OPEN_INT_EMPLR,case when temp.OPEN_INT_EMP > 0 then 1 else 0 end as count3 ");
			//sb1.append("   ");
			//sb1.append("    ");
			//sb1.append("   "); 
			//sb1.append("    ");
			sb1.append("  FROM TEMPEMPR3 temp ");
			sb1.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO      ");
			sb1.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");  
			sb1.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");  
			sb1.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE ");  
			sb1.append("  where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and  temp.OPEN_INT_EMP > 0 and temp.batch_id is not null and temp.OPEN_BATCH_ID is null  and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null) "); 
			sb1.append("  ) abc group by abc.ddo_reg_no having abc.ddo_reg_no='"+ddoRegNo+"' ");
			
			 Query selectQuery1 = ghibSession.createSQLQuery(sb1.toString());
			 List empLstOpening= selectQuery1.list();
			 
			 if(empLstOpening!=null  && empLstOpening.size()>0){
					if(empLstOpening.get(0)!=null)
					{
						Long d=0L;
						if(empDdoLst!=null)
						{
							d= Long.parseLong(empDdoLst)+Long.parseLong(empLstOpening.get(0).toString());
						}
						else
						{
							d=Long.parseLong(empLstOpening.get(0).toString());
						}
						
						empDdoLst=d.toString();
					}
					
				}
		
		}
		catch(Exception e)
		{
			logger.info("Error occer in  getEmployeeList ---------"+ e);
		}
		return empDdoLst;


	}
	
	
	
	
	
	public HashMap getEmployeeRecordCountDdoregNsdlMap(String finType,String treasury)
	{
		List empLst = null;

		Object []  empDdoLst=null;
		
		StringBuilder  Strbld = new StringBuilder();
		
		HashMap m=new HashMap();

		try{

			
			
			Strbld.append(" select sum(final.count1)+sum(final.count2)+sum(final.count3) as a,final.ddo_reg_no from (select abc.EMP_NAME,abc.DCPS_ID,abc.PRAN_NO,abc.a,abc.b, ");
					
			Strbld.append(" case when abc.CONTRIB_EMP_DIFF = 0 then 0 else 1 end as count1, case when abc.INT_EMP_DIFF <= 0 then 0 else 1 end as count2, ");
			Strbld.append(" case when abc.OPEN_INT_EMPLR > 0 then 1 else 0 end as count3 , ");
			Strbld.append(" abc.loc_name,abc.dto_reg_no,abc.ddo_reg_no  ");
					
			Strbld.append(" from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) as a,cast(temp.INT_EMPL_CONTRIB as double) as b, ");
			Strbld.append(" cast((cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) - cast(nvl(ldata.EMP_CONTRI,0) as varchar))as varchar)as CONTRIB_EMP_DIFF, ");
			Strbld.append(" DEC(cast(temp.INT_EMPL_CONTRIB as double) -cast(nvl(ldata.EMP_INT,0) as double ) ,25,2) as INT_EMP_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ,     ");
			Strbld.append("	DEC(cast(nvl(temp.OPEN_INT_EMP,0) as double) -cast(nvl(ldata.OPEN_EMP,0) as double ),25,2) as OPEN_INT_EMP,DEC(cast(nvl(temp.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) as OPEN_INT_EMPLR ");		
			Strbld.append(" FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR     ");
			Strbld.append(" inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'    ");
			Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO      ");
			Strbld.append(" inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2)  ");
			Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003   ");
			Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE     ");
			Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");  

			Strbld.append(" where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"'  and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1   and temp.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null) and   ( temp.INT_EMPL_CONTRIB <> 0 or temp.OPEN_INT_EMP <> 0)   ");
			Strbld.append(" group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
			Strbld.append(" temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,ldata.EMP_CONTRI,ldata.EMP_INT,ldata.OPEN_EMP,ldata.OPEN_EMPLR, temp.OPEN_INT_EMP,temp.OPEN_INT_EMPLR  ) abc )final  ");
			Strbld.append(" group by final.ddo_reg_no ");
			  
			  
		/*	Strbld.append(" select sum(final.count1)+sum(final.count2) as a from ( select  abc.EMP_NAME,abc.DCPS_ID,abc.PRAN_NO,abc.a,abc.CONTRIB_EMPLOYER,abc.b, ");   
			Strbld.append(" abc.c, abc.loc_name,abc.dto_reg_no,abc.ddoreg, abc.CONTRIB_EMP_DIFF,abc.CONTRIB_EMPLR_DIFF,abc.INT_EMP_DIFF,abc.INT_EMPLR_DIFF, "); 
			Strbld.append(" case when abc.CONTRIB_EMP_DIFF = 0 then 0 else 1 end as count1, case when abc.INT_EMP_DIFF = 0 then 0 else 1 end as count2 from ");
			Strbld.append(" ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE as a,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double) as b, ");
			Strbld.append(" cast(yr.INT_CONTRB_EMPLOYER as double) as c, loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg, ");
			Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");

			Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
			Strbld.append(" (cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )) as INT_EMP_DIFF, ");
			Strbld.append(" (cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )) as INT_EMPLR_DIFF ");
			Strbld.append(" FROM MST_DCPS_CONTRIBUTION_YEARLY yr ");
			Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID    ");
			Strbld.append(" inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");
			Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
			Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
			Strbld.append(" left outer join dcps_legacy_data ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code ");
			Strbld.append(" where yr.YEAR_ID='"+finType+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='"+treasury+"' and emp.pran_no is not null  ");
			Strbld.append(" order by  reg.ddo_reg_no ) abc ) final ");
			Strbld.append(" group by final.ddoreg ");
			Strbld.append(" having final.ddoreg='"+ddoRegNo+"' ");
			Strbld.append("");*/
			
			logger.info("   ---------"+Strbld.toString() );
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
			logger.info("script for all employee ---------"+lQuery.toString() );
			

			empLst = lQuery.list();
			if(empLst!=null && empLst.size()>0 )
			{
				for(int i=0;i< empLst.size();i++)
				{
					empDdoLst=(Object[]) empLst.get(i);
					if(empDdoLst!=null && empDdoLst.length >0)
					{
						m.put(empDdoLst[1], empDdoLst[0]);
					}
				}
				
			}
			
			
			StringBuilder sb1 = new StringBuilder();
			
			sb1.append("   select sum(abc.count3) ,abc.ddo_reg_no from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(0 as varchar) as CONTRIB_EMP_DIFF,cast(0 as varchar) as CONTRIB_EMPLR_DIFF,cast(0 as varchar) as INT_EMP_DIFF, ");     
			sb1.append("  cast(0 as varchar) as INT_EMPLR_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
			sb1.append("	cast(nvl(temp.OPEN_INT_EMP,0) as double ) as OPEN_INT_EMP,cast(nvl(temp.OPEN_INT_EMPLR,0) as double ) as OPEN_INT_EMPLR,case when DEC(cast(nvl(temp.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2)  > 0 then 1 else 0 end as count3 ");
			//sb1.append("   ");
			//sb1.append("    ");
			//sb1.append("   "); 
			//sb1.append("    ");
			sb1.append("  FROM TEMPEMPR3 temp ");
			sb1.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO      ");
			sb1.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");  
			sb1.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");  
			sb1.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE ");
			sb1.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");
			sb1.append("  where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and  temp.OPEN_INT_EMP > 0 and temp.batch_id is not null and temp.OPEN_BATCH_ID is null  and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null) "); 
			sb1.append("  ) abc group by abc.ddo_reg_no ");
			
			 Query selectQuery1 = ghibSession.createSQLQuery(sb1.toString());
			 List empLstOpening= selectQuery1.list();
			 
			 if(empLstOpening!=null  && empLstOpening.size()>0)
			 {
				 for(int i=0;i< empLstOpening.size();i++)
					{
						empDdoLst=(Object[]) empLstOpening.get(i);
						if(empDdoLst!=null && empDdoLst.length >0)
						{
							if(m!=null && m.containsKey(empDdoLst[1]))
							{
								Long l= Long.parseLong(m.get(empDdoLst[1]).toString())+Long.parseLong(empDdoLst[0].toString());
								m.remove(empDdoLst[1]);
								m.put(empDdoLst[1], l);
							}
							else
							{
								m.put(empDdoLst[1], empDdoLst[0]);
							}
							
						}
					}
			 }
		
		}
		catch(Exception e)
		{
			logger.info("Error occer in  getEmployeeList ---------"+ e);
		}
		return m;


	}

	
	public HashMap getEmployeeListDdoregNsdlMap(String finType,String treasury)
	{
		List empLst = null;

		Object[] empDdoLst = null;

		StringBuilder  Strbld = new StringBuilder();
		
		HashMap m= new HashMap();

		try{

/*added by akshay added cast(CONTRIB_EMP_DIFF as double)+cast(INT_EMP_DIFF as double) removed select cast(sum(abc.a) as double)+sum(abc.b),*/			
			Strbld.append("select cast(sum(CONTRIB_EMP_DIFF) as double),abc.ddo_reg_no from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) ,cast(temp.INT_EMPL_CONTRIB as double) , cast((cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) - cast(nvl(ldata.EMP_CONTRI,0) as varchar))as varchar)as CONTRIB_EMP_DIFF,DEC(cast(temp.INT_EMPL_CONTRIB as double) -cast(nvl(ldata.EMP_INT,0) as double ) ,25,2) as INT_EMP_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no  ");   
			//Strbld.append("	 cast(sum(temp.INT_EMPLR_CONTRIB) as varchar),loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no    ");
			Strbld.append("  FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR   "); 
			Strbld.append("  inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'   ");
			Strbld.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO     ");
			Strbld.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");
			Strbld.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
			Strbld.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE    ");
/*added by akshay*/		Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI, sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");   

			Strbld.append("   where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"'  and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null  and emp.PRAN_ACTIVE=1  and temp.INT_EMPL_CONTRIB <> 0  and temp.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
			Strbld.append("   group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,ldata.EMP_CONTRI,ldata.EMP_INT  ) abc ");//group by abc.ddoreg 
/*added by akshay*/	Strbld.append("  group by abc.ddo_reg_no ");

			/*Strbld.append(" where trn.REG_STATUS =1 and  post.AC_DCPS_MAINTAINED_BY = :acMain and post.BILL_NO= :billno  and post.FIN_YEAR= :finType   ");
			Strbld.append(" and emp.REG_STATUS=1 and mst.VOUCHER_STATUS=1 "); 
			Strbld.append(" and post.BILL_NO=trn.EMPLR_BILL_NO  ");
			Strbld.append(" and emp.PRAN_NO is NOT NULL and trn.CONTRIBUTION is not null  and trn.EMPLOYER_CONTRI_FLAG ='Y'  ");
			Strbld.append(" and ((trn.VOUCHER_DATE between '" + fromDate + "' AND '"+ toDate + "') or (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "')) AND emp.LOC_ID <> 380001 ");

			Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,mo.MONTH_NAME,trn.FIN_YEAR_ID,fin.FIN_YEAR_DESC ");   
			Strbld.append(" ,month(trn.STARTDATE),year(trn.STARTDATE),fin.FIN_YEAR_CODE,trn.EMPLR_BILL_NO,mo.month_id,trn.TYPE_OF_PAYMENT  ");
			Strbld.append(" order by EMP_NAME ");*/

			//ended By Ashish
			logger.info("   ---------"+Strbld.toString() );
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
			logger.info("script for all employee ---------"+lQuery.toString() );
			/*lQuery.setString("acMain",acMain);
			lQuery.setString("billno",billno);
			lQuery.setString("finType",finType);*/

			empLst = lQuery.list();
			if(empLst!=null && empLst.size()>0 )
			{
				for(int i=0;i< empLst.size();i++)
				{
					empDdoLst=(Object[]) empLst.get(i);
					if(empDdoLst!=null && empDdoLst.length >0)
					{
						m.put(empDdoLst[1], empDdoLst[0]);
					}
				}
				
			}
		}
		catch(Exception e)
		{
			logger.info("Error occer in  getEmployeeList ---------"+ e);
		}
		return m;


	}

	
	public HashMap getEmployeeListDdoregNsdlIntMap(String finType,String treasury)
	{
		List empLst = null;

		Object[] empDdoLst = null;

		StringBuilder  Strbld = new StringBuilder();
		
		HashMap m= new HashMap();

		try{

/*added by akshay added cast(CONTRIB_EMP_DIFF as double)+cast(INT_EMP_DIFF as double) removed select cast(sum(abc.a) as double)+sum(abc.b),*/			
			Strbld.append("select cast(sum(final.INT_EMP_DIFF) as double),final.ddo_reg_no from (select * from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) ,cast(temp.INT_EMPL_CONTRIB as double) , cast((cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) - cast(nvl(ldata.EMP_CONTRI,0) as varchar))as varchar)as CONTRIB_EMP_DIFF,DEC(cast(temp.INT_EMPL_CONTRIB as double) -cast(nvl(ldata.EMP_INT,0) as double ) ,25,2) as INT_EMP_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no   ");   
			//Strbld.append("	 cast(sum(temp.INT_EMPLR_CONTRIB) as varchar),loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no    ");
			Strbld.append("  FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR   "); 
			Strbld.append("  inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'   ");
			Strbld.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO     ");
			Strbld.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");
			Strbld.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
			Strbld.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE    ");
/*added by akshay*/		Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI, sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");   

			Strbld.append("   where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"'  and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1   and (temp.INT_EMPL_CONTRIB <> 0 or temp.OPEN_INT_EMP <> 0)  and temp.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
			Strbld.append("   group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,ldata.EMP_CONTRI,ldata.EMP_INT  ) abc ) final where final.INT_EMP_DIFF > 0  ");//group by abc.ddoreg 
/*added by akshay*/	Strbld.append("  group by final.ddo_reg_no  ");

			/*Strbld.append(" where trn.REG_STATUS =1 and  post.AC_DCPS_MAINTAINED_BY = :acMain and post.BILL_NO= :billno  and post.FIN_YEAR= :finType   ");
			Strbld.append(" and emp.REG_STATUS=1 and mst.VOUCHER_STATUS=1 "); 
			Strbld.append(" and post.BILL_NO=trn.EMPLR_BILL_NO  ");
			Strbld.append(" and emp.PRAN_NO is NOT NULL and trn.CONTRIBUTION is not null  and trn.EMPLOYER_CONTRI_FLAG ='Y'  ");
			Strbld.append(" and ((trn.VOUCHER_DATE between '" + fromDate + "' AND '"+ toDate + "') or (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "')) AND emp.LOC_ID <> 380001 ");

			Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,mo.MONTH_NAME,trn.FIN_YEAR_ID,fin.FIN_YEAR_DESC ");   
			Strbld.append(" ,month(trn.STARTDATE),year(trn.STARTDATE),fin.FIN_YEAR_CODE,trn.EMPLR_BILL_NO,mo.month_id,trn.TYPE_OF_PAYMENT  ");
			Strbld.append(" order by EMP_NAME ");*/

			//ended By Ashish
			logger.info("   ---------"+Strbld.toString() );
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
			logger.info("script for all employee ---------"+lQuery.toString() );
			/*lQuery.setString("acMain",acMain);
			lQuery.setString("billno",billno);
			lQuery.setString("finType",finType);*/

			empLst = lQuery.list();
			if(empLst!=null && empLst.size()>0 )
			{
				for(int i=0;i< empLst.size();i++)
				{
					empDdoLst=(Object[]) empLst.get(i);
					if(empDdoLst!=null && empDdoLst.length >0)
					{
						m.put(empDdoLst[1], empDdoLst[0]);
					}
				}
				
			}
		}
		catch(Exception e)
		{
			logger.info("Error occer in  getEmployeeList ---------"+ e);
		}
		return m;


	}
	
	public HashMap getEmployerOpenIntListDdoregNsdlMap(String finType,String treasury)
	{
		List empLst = null;

		Object []  empDdoLst=null;
		
		StringBuilder  Strbld = new StringBuilder();
		
		HashMap m=new HashMap();

		try{

/*added by akshay added cast(CONTRIB_EMP_DIFF as double)+cast(INT_EMP_DIFF as double) removed select cast(sum(abc.a) as double)+sum(abc.b),*/			
			Strbld.append("select cast(sum(OPEN_INT_EMPLR) as double) ,abc.ddo_reg_no from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) ,cast(temp.INT_EMPL_CONTRIB as double) , cast((cast(sum(r3.CUR_EMPL_CONTRIB) as varchar) - cast(nvl(ldata.EMP_CONTRI,0) as varchar))as varchar)as CONTRIB_EMP_DIFF,DEC(cast(temp.INT_EMPL_CONTRIB as double) -cast(nvl(ldata.EMP_INT,0) as double ) ,25,2) as INT_EMP_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ,DEC(cast(nvl(temp.OPEN_INT_EMP,0) as double) -cast(nvl(ldata.OPEN_EMP,0) as double ),25,2) as OPEN_INT_EMP,DEC(cast(nvl(temp.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) as OPEN_INT_EMPLR  ");   
			//Strbld.append("	 cast(sum(temp.INT_EMPLR_CONTRIB) as varchar),loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no    ");
			Strbld.append("  FROM TEMPEMPR3 temp inner join tempr3 r3 on r3.EMP_ID_NO=temp.EMP_ID_NO and temp.FIN_YEAR=r3.FIN_YEAR   "); 
			Strbld.append("  inner join SGVA_MONTH_MST mo on mo.MONTH_ID = r3.PAY_MONTH  and mo.LANG_ID = 'en_US'   ");
			Strbld.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO     ");
			Strbld.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");
			Strbld.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
			Strbld.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE    ");
			Strbld.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");  

			Strbld.append("   where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"'  and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null  and emp.PRAN_ACTIVE=1  and (temp.INT_EMPL_CONTRIB <> 0 or temp.OPEN_INT_EMP <> 0) and temp.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
			Strbld.append("   group by emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,temp.INT_EMPLR_CONTRIB,temp.INT_EMPL_CONTRIB,ldata.EMP_CONTRI,ldata.EMP_INT,ldata.OPEN_EMP,ldata.OPEN_EMPLR,temp.OPEN_INT_EMP,temp.OPEN_INT_EMPLR   ) abc ");//group by abc.ddoreg 
/*added by akshay*/	Strbld.append("  group by abc.ddo_reg_no ");

			/*Strbld.append(" where trn.REG_STATUS =1 and  post.AC_DCPS_MAINTAINED_BY = :acMain and post.BILL_NO= :billno  and post.FIN_YEAR= :finType   ");
			Strbld.append(" and emp.REG_STATUS=1 and mst.VOUCHER_STATUS=1 "); 
			Strbld.append(" and post.BILL_NO=trn.EMPLR_BILL_NO  ");
			Strbld.append(" and emp.PRAN_NO is NOT NULL and trn.CONTRIBUTION is not null  and trn.EMPLOYER_CONTRI_FLAG ='Y'  ");
			Strbld.append(" and ((trn.VOUCHER_DATE between '" + fromDate + "' AND '"+ toDate + "') or (trn.MISSING_CREDIT_APPROVAL_DATE between '" + fromDate + "' AND '"+ toDate + "')) AND emp.LOC_ID <> 380001 ");

			Strbld.append(" group by  emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,look.LOOKUP_NAME,mo.MONTH_NAME,trn.FIN_YEAR_ID,fin.FIN_YEAR_DESC ");   
			Strbld.append(" ,month(trn.STARTDATE),year(trn.STARTDATE),fin.FIN_YEAR_CODE,trn.EMPLR_BILL_NO,mo.month_id,trn.TYPE_OF_PAYMENT  ");
			Strbld.append(" order by EMP_NAME ");*/

			//ended By Ashish
			logger.info("   ---------"+Strbld.toString() );
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
			logger.info("script for all employee ---------"+lQuery.toString() );
			/*lQuery.setString("acMain",acMain);
			lQuery.setString("billno",billno);
			lQuery.setString("finType",finType);*/

			empLst = lQuery.list();
			if(empLst!=null && empLst.size()>0 )
			{
				for(int i=0;i< empLst.size();i++)
				{
					empDdoLst=(Object[]) empLst.get(i);
					if(empDdoLst!=null && empDdoLst.length >0)
					{
						m.put(empDdoLst[1], empDdoLst[0]);
					}
				}
				
			}
			StringBuilder sb1 = new StringBuilder();
			sb1.append("   select cast(sum(OPEN_INT_EMPLR) as double),abc.ddo_reg_no from ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,cast(0 as varchar) as CONTRIB_EMP_DIFF,cast(0 as varchar) as CONTRIB_EMPLR_DIFF,cast(0 as varchar) as INT_EMP_DIFF, ");     
			sb1.append("  cast(0 as varchar) as INT_EMPLR_DIFF,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
			sb1.append("	DEC(cast(nvl(temp.OPEN_INT_EMP,0) as double) -cast(nvl(ldata.OPEN_EMP,0) as double ),25,2) as OPEN_INT_EMP,DEC(cast(nvl(temp.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) as OPEN_INT_EMPLR ");
			//sb1.append("   ");
			//sb1.append("    ");
			//sb1.append("   "); 
			//sb1.append("    ");
			sb1.append("  FROM TEMPEMPR3 temp ");
			sb1.append("  inner join mst_dcps_emp emp on emp.DCPS_ID=temp.EMP_ID_NO      ");
			sb1.append("  inner join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(emp.ddo_code,1,2) ");  
			sb1.append("  inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");  
			sb1.append("  inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE ");
			sb1.append("  left outer join (SELECT l.FIN_YEAR,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM  DCPS_LEGACY_DATA l where dcps_id<>'#N/A'  group by l.FIN_YEAR,l.DCPS_ID) ldata on temp.emp_ID_no=ldata.dcps_id and temp.FIN_YEAR=ldata.FIN_YEAR ");
			sb1.append("  where temp.FIN_YEAR='"+finType+"' and loc.loc_id='"+treasury+"' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and  temp.OPEN_INT_EMP > 0 and temp.batch_id is not null and temp.OPEN_BATCH_ID is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null) "); 
			sb1.append("  ) abc group by abc.ddo_reg_no ");
			
			 Query selectQuery1 = ghibSession.createSQLQuery(sb1.toString());
			 List empLstOpening= selectQuery1.list();
			 
			 if(empLstOpening!=null  && empLstOpening.size()>0)
			 {

				 for(int i=0;i< empLstOpening.size();i++)
					{
						empDdoLst=(Object[]) empLstOpening.get(i);
						if(empDdoLst!=null && empDdoLst.length >0)
						{
							if(m!=null && m.containsKey(empDdoLst[1]))
							{
								Double l= Double.parseDouble(m.get(empDdoLst[1]).toString())+Double.parseDouble(empDdoLst[0].toString());
								m.remove(empDdoLst[1]);
								m.put(empDdoLst[1], l);
							}
							else
							{
								m.put(empDdoLst[1], empDdoLst[0]);
							}
							
						}
					}
			 
			 }
		}
		catch(Exception e)
		{
			logger.info("Error occer in  getEmployeeList ---------"+ e);
		}
		return m;


	}




	

}
  