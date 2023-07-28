package com.tcs.sgv.dcps.dao;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class InterchangePranDaoImpl extends GenericDaoHibernateImpl implements InterchangePranDao{
	private Session ghibSession = null;
	private final Logger gLogger = Logger.getLogger(getClass());
	public InterchangePranDaoImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
		// TODO Auto-generated constructor stub
	}

	public List getAllEmp(String lStrSevaarthId1, String lStrSevaarthId2){

		List lLstEmpDeselect = null;

		StringBuilder  Strbld = new StringBuilder();
		try {
			Strbld.append(" SELECT emp.EMP_NAME,emp.DCPS_ID,emp.SEVARTH_ID, to_char(emp.DOB,'dd-MM-yyyy'), to_char(emp.DOJ,'dd-MM-yyyy'),emp.PPAN,emp.PRAN_NO,emp.DDO_CODE,loc.LOC_NAME ,case when emp.PRAN_ACTIVE =1 then 'Active' else 'Not Active' end ");
			Strbld.append(" FROM mst_dcps_emp emp left outer join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)= substr(emp.DDO_CODE,1,2) and loc.DEPARTMENT_ID=100003  ");
			Strbld.append(" left outer join MST_DTO_REG dto on substr(dto.DTO_CODE,1,2) = substr(loc.LOC_ID,1,2) where 1=1  and emp.PRAN_ACTIVE = 1 and SEVARTH_ID in ('"+lStrSevaarthId1+"','"+lStrSevaarthId2+"')");

			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
			logger.info("query lstgetAllEmp ---------"+ Strbld.toString());
			lLstEmpDeselect = lQuery.list();
		}
		catch(Exception e)
		{
			logger.info("Error occured in lstgetAllEmp ---------"+ e);
			e.printStackTrace();
		}
		return lLstEmpDeselect;
	}


	public List getAllFile(String lStrPranNo){

		List lLstEmpDeselect1 = null;

		StringBuilder  Strbld = new StringBuilder();
		try {
			Strbld.append(" SELECT bh.FILE_NAME,bh.BH_TOTAL_AMT,  mon.MONTH_NAME,bh.YEAR,case when bh.FILE_STATUS =0 then 'File Generated'  when bh.FILE_STATUS =1 then 'File Validated' end  FROM NSDL_SD_DTLS sd  ");
			Strbld.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME = sd.FILE_NAME  ");
			Strbld.append(" inner join MST_DCPS_EMP emp on sd.SD_PRAN_NO = emp.PRAN_NO ");
			Strbld.append(" inner join SGVA_MONTH_MST mon on mon.MONTH_ID = bh.MONTH ");
			Strbld.append(" left outer join CMN_LOCATION_MST loc on substr(loc.loc_id,1,4) = substr(sd.FILE_NAME,1,4)  and loc.DEPARTMENT_ID in (100003,100006) ");
			Strbld.append(" where bh.TRANSACTION_ID is null and bh.FILE_STATUS in(0,1) and bh.STATUS <> -1  ");
			Strbld.append(" and sd.SD_PRAN_NO in ("+lStrPranNo+")");
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

			logger.info("query lstgetAllEmp ---------"+ Strbld.toString());
			lLstEmpDeselect1 = lQuery.list();
		}
		catch(Exception e)
		{
			logger.info("Error occured in lstgetAllEmp ---------"+ e);
			e.printStackTrace();

		}
		return lLstEmpDeselect1;
	}

	public List checkSevaarthIdExist(String lStrSevaarthId) {

		StringBuilder sb = new StringBuilder();
		Query selectQuery = null;

		sb.append(" SELECT  SEVARTH_ID , PRAN_ACTIVE , nvl(Pran_no,'1') FROM mst_dcps_emp where  1=1 ");

		if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
			sb.append(" and UPPER(SEVARTH_ID) = :lStrSevaarthId");
		}

		logger.info("Inside checkSevaarthIdExist query is *********** "+sb.toString());

		selectQuery = ghibSession.createSQLQuery(sb.toString());

		if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
			selectQuery.setParameter("lStrSevaarthId", lStrSevaarthId.trim().toUpperCase());	
		}
		List exist = selectQuery.list();

		return exist;

	}

	public String swapPranNo(String fileName, String PranNew1,String PranNew2, String remark)
	{
		logger.info("Inside swapPranNo Query************");

		StringBuilder strBuld = new StringBuilder();  
		StringBuilder strBuld1 = new StringBuilder();  
		StringBuilder strBuld2 = new StringBuilder();  
		StringBuilder strBuld3 = new StringBuilder();  
		StringBuilder strBuld4 = new StringBuilder();  

		Query updateQuery = null;
		Query updateQuery1 = null;
		Query updateQuery2 = null;
		Query updateQuery3 = null;
		Query updateQuery4 = null;

		String flag = "NA";
		try
		{

			strBuld.append(" update mst_dcps_emp set PRAN_NO = '"+PranNew1+"' , UPDATED_DATE =  sysdate , PRAN_REMARK = :strRemark where PRAN_NO = '"+PranNew2+"'" );

			strBuld2.append(" update mst_dcps_emp set PRAN_NO = '"+PranNew2+"' , UPDATED_DATE =  sysdate , PRAN_REMARK = :strRemark where PRAN_NO = '"+PranNew1+"'" );
			
			strBuld1.append(" update NSDL_BH_DTLS set status = '-1' , FILE_STATUS = '-1' where FILE_NAME in ("+fileName+") " );

			strBuld3.append(" update NSDL_SD_DTLS set SD_PRAN_NO = '"+PranNew1 +"' where SD_PRAN_NO = '"+ PranNew2+"'" );

			strBuld4.append(" update NSDL_SD_DTLS set SD_PRAN_NO = '"+PranNew2 +"' where SD_PRAN_NO = '"+PranNew1 +"'" );
			
			logger.info("Inside updatePranNo Query************" +strBuld.toString());
			updateQuery = ghibSession.createSQLQuery(strBuld.toString());

			logger.info("Inside updatePranNo Query************" +strBuld1.toString());
			updateQuery1 = ghibSession.createSQLQuery(strBuld1.toString());

			logger.info("Inside updatePranNo Query************" +strBuld2.toString());
			updateQuery2 = ghibSession.createSQLQuery(strBuld2.toString());
			
			logger.info("Inside updatePranNo Query************" +strBuld3.toString());
			updateQuery3 = ghibSession.createSQLQuery(strBuld3.toString());
			
			logger.info("Inside updatePranNo Query************" +strBuld4.toString());
			updateQuery4 = ghibSession.createSQLQuery(strBuld4.toString());
			
			updateQuery.setParameter("strRemark", remark);
			updateQuery2.setParameter("strRemark", remark);
			
			int result=updateQuery.executeUpdate();
			int result1=updateQuery1.executeUpdate();
			int result2=updateQuery2.executeUpdate();
			int result3=updateQuery3.executeUpdate();
			int result4=updateQuery4.executeUpdate();

			logger.info("result is "+result+"  result1"+result1+"  result2"+result2);

			if ((result!= 0 && result > 0)&&(result1!= 0 && result1 > 0)&&(result2!= 0 && result2 > 0)) 
			{
				flag="Updated";

			}
			else {

				flag= "NotUpdated";
			}}
		catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}


	public String swapPran(String PranNew1,String PranNew2, String remark)
	{
		logger.info("Inside swapPran Query************");

		StringBuilder strBuld = new StringBuilder();  
		StringBuilder strBuld2 = new StringBuilder();  
		StringBuilder strBuld3 = new StringBuilder();  
		StringBuilder strBuld4 = new StringBuilder();  

		Query updateQuery = null;
		Query updateQuery2 = null;
		Query updateQuery3 = null;
		Query updateQuery4 = null;

		String flag = "NA";
		try
		{

			strBuld.append(" update mst_dcps_emp set PRAN_NO = '"+PranNew1+"' , UPDATED_DATE =  sysdate , PRAN_REMARK = :strRemark where PRAN_NO = '"+PranNew2+"'" );

			strBuld2.append(" update mst_dcps_emp set PRAN_NO = '"+PranNew2+"' , UPDATED_DATE =  sysdate , PRAN_REMARK = :strRemark where PRAN_NO = '"+PranNew1+"'" );
			
			strBuld3.append(" update NSDL_SD_DTLS set SD_PRAN_NO = '"+PranNew1 +"' where SD_PRAN_NO = '"+ PranNew2+"'" );

			strBuld4.append(" update NSDL_SD_DTLS set SD_PRAN_NO = '"+PranNew2 +"' where SD_PRAN_NO = '"+PranNew1 +"'" );
			
			logger.info("Inside updatePranNo Query************" +strBuld.toString());
			updateQuery = ghibSession.createSQLQuery(strBuld.toString());

			logger.info("Inside updatePranNo Query************" +strBuld2.toString());
			updateQuery2 = ghibSession.createSQLQuery(strBuld2.toString());
			
			logger.info("Inside updatePranNo Query************" +strBuld3.toString());
			updateQuery3 = ghibSession.createSQLQuery(strBuld3.toString());
			
			logger.info("Inside updatePranNo Query************" +strBuld4.toString());
			updateQuery4 = ghibSession.createSQLQuery(strBuld4.toString());
			
			updateQuery.setParameter("strRemark", remark);

			int result=updateQuery.executeUpdate();
			int result2=updateQuery2.executeUpdate();
			int result3=updateQuery3.executeUpdate();
			int result4=updateQuery4.executeUpdate();

			logger.info("result is "+result+"  result2"+result2);
			logger.info("result is "+result+"  result2"+result2);

			if ((result!= 0 && result > 0)&&(result2!= 0 && result2 > 0)) 
			{
				flag="UpdatedPran";

			}
			else {

				flag= "NotUpdated";
			}}
		catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}


}






