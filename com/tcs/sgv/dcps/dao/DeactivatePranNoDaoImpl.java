package com.tcs.sgv.dcps.dao;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class DeactivatePranNoDaoImpl extends GenericDaoHibernateImpl implements DeactivatePranNoDao{
	private Session ghibSession = null;
	private final Logger gLogger = Logger.getLogger(getClass());
	public DeactivatePranNoDaoImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
		// TODO Auto-generated constructor stub
	}

public List getAllEmp(String lStrSevaarthId, String lStrPranNo){
	
	List lLstEmpDeselect = null;
	
	StringBuilder  Strbld = new StringBuilder();
	try {
		Strbld.append(" SELECT emp.EMP_NAME,emp.DCPS_ID,emp.SEVARTH_ID, to_char(emp.DOB,'dd-MM-yyyy'), to_char(emp.DOJ,'dd-MM-yyyy'),emp.PPAN,emp.PRAN_NO,emp.DDO_CODE,loc.LOC_NAME ,cast(dto.DTO_REG_NO as varchar) as DTO_NO, substr(loc.LOC_ID,1,4) as treasury_code  ");
		Strbld.append(" FROM mst_dcps_emp emp left outer join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)= substr(emp.DDO_CODE,1,2) and loc.DEPARTMENT_ID=100003  ");
		Strbld.append(" left outer join MST_DTO_REG dto on substr(dto.DTO_CODE,1,2) = substr(loc.LOC_ID,1,2) where 1=1 ");
		

		if (lStrSevaarthId != null && !"".equals(lStrSevaarthId)) {
			Strbld.append(" AND UPPER(emp.SEVARTH_ID) = :sevarthId");
		}

		if (lStrPranNo != null && !"".equals(lStrPranNo)) {
			Strbld.append(" AND emp.PRAN_NO = :pranNo");
		}
		
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

		if (lStrSevaarthId != null && !"".equals(lStrSevaarthId)) {
		lQuery.setString("sevarthId", lStrSevaarthId);
		}

		if (lStrPranNo != null && !"".equals(lStrPranNo)) {
			lQuery.setString("pranNo", lStrPranNo);
		}
		
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
		Strbld.append(" SELECT bh.FILE_NAME,loc.LOC_NAME, bh.YEAR, mon.MONTH_NAME,bh.BH_TOTAL_AMT FROM NSDL_SD_DTLS sd  ");
		Strbld.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME = sd.FILE_NAME  ");
		Strbld.append(" inner join MST_DCPS_EMP emp on sd.SD_PRAN_NO = emp.PRAN_NO ");
		Strbld.append(" inner join SGVA_MONTH_MST mon on mon.MONTH_ID = bh.MONTH ");
		Strbld.append(" left outer join CMN_LOCATION_MST loc on substr(loc.loc_id,1,4) = substr(sd.FILE_NAME,1,4)  and loc.DEPARTMENT_ID in (100003,100006) ");
		Strbld.append(" where bh.TRANSACTION_ID is null and bh.FILE_STATUS = 1  and bh.STATUS <> -1  ");

		if (lStrPranNo != null && !"".equals(lStrPranNo)) {
			Strbld.append(" AND sd.SD_PRAN_NO = '" + lStrPranNo.trim() +"' ");
		}
		
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

public List checkSevaarthIdExist(String lStrSevaarthId, String strPranNo) {

	StringBuilder sb = new StringBuilder();
	Query selectQuery = null;

	sb.append(" SELECT nvl(PRAN_NO,'#') , SEVARTH_ID , PRAN_ACTIVE FROM mst_dcps_emp where  1=1 ");

	if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
		sb.append(" and UPPER(SEVARTH_ID) = :lStrSevaarthId");
	}
	if (!"".equals(strPranNo) && strPranNo != null) {
		sb.append(" and PRAN_NO = :strPranNo");
	}
	logger.info("Inside checkSevaarthIdExist query is *********** "+sb.toString());
	
	selectQuery = ghibSession.createSQLQuery(sb.toString());
	
	if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
		selectQuery.setParameter("lStrSevaarthId", lStrSevaarthId.trim().toUpperCase());	
	}
	
	if (!"".equals(strPranNo) && strPranNo != null) {
		selectQuery.setParameter("strPranNo", strPranNo.trim().toUpperCase());
	}

	List exist = selectQuery.list();

	return exist;

}

public String deactivatePranNo(String fileName, String pranNo, String remark)
{
	logger.info("Inside updatePranNo Query************");

	StringBuilder strBuld = new StringBuilder();  
	StringBuilder strBuld1 = new StringBuilder();  

	Query updateQuery = null;

	Query updateQuery1 = null;
	String flag = "NA";
	  try
	  {
	
	strBuld.append(" update mst_dcps_emp set PRAN_ACTIVE = 0 , UPDATED_DATE =  sysdate , PRAN_REMARK = :strRemark where PRAN_NO = :strPranNo " );

	strBuld1.append(" update NSDL_BH_DTLS set status = '-1' , FILE_STATUS = '-1' where FILE_NAME in ("+fileName+") " );

	logger.info("Inside updatePranNo Query************" +strBuld.toString());
	updateQuery = ghibSession.createSQLQuery(strBuld.toString());
	
	logger.info("Inside updatePranNo Query************" +strBuld1.toString());
	updateQuery1 = ghibSession.createSQLQuery(strBuld1.toString());
	
//	if (!"".equals(fileName) && fileName != null) {
//		updateQuery1.setParameter("lStrFileName", fileName.trim());	
//		logger.info(fileName);
//
//	}
	
	if (!"".equals(pranNo) && pranNo != null) {
		updateQuery.setParameter("strPranNo", pranNo.trim());
	}
	
	updateQuery.setParameter("strRemark", remark);
	
	int result=updateQuery.executeUpdate();
	int result1=updateQuery1.executeUpdate();
	logger.info("result is "+result+"  result1"+result1);
	
	if ((result!= 0 && result > 0)&&(result1!= 0 && result1 > 0)) 
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


public String deactivatePran(String pranNo, String remark)
{
	logger.info("Inside updatePranNo Query************");

	StringBuilder strBuld = new StringBuilder();  


	Query updateQuery = null;

	String flag = "NA";
	  try
	  {
	
	strBuld.append(" update mst_dcps_emp set PRAN_ACTIVE = 0 , UPDATED_DATE =  sysdate , PRAN_REMARK = :strRemark where PRAN_NO = :strPranNo " );

	logger.info("Inside updatePranNo Query************" +strBuld.toString());
	updateQuery = ghibSession.createSQLQuery(strBuld.toString());
	
	if (!"".equals(pranNo) && pranNo != null) {
		updateQuery.setParameter("strPranNo", pranNo.trim());
	}
	
	updateQuery.setParameter("strRemark", remark);
	
	int result=updateQuery.executeUpdate();

	logger.info("result is "+result);
	
	if ((result!= 0 && result > 0)) 
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
	
	
	
	


