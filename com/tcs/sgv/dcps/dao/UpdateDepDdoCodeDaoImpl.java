package com.tcs.sgv.dcps.dao;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class UpdateDepDdoCodeDaoImpl extends GenericDaoHibernateImpl implements UpdateDepDdoCodeDao{
	private Session ghibSession = null;
	private final Logger gLogger = Logger.getLogger(getClass());
	public UpdateDepDdoCodeDaoImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
		// TODO Auto-generated constructor stub
	}

	public List getAllEmp(String lStrSevaarthId){

		List lLstEmpDeselect = null;

		StringBuilder  Strbld = new StringBuilder();
		try {
			Strbld.append(" SELECT emp.EMP_NAME,emp.DCPS_ID,emp.SEVARTH_ID, to_char(emp.DOB,'dd-MM-yyyy'), to_char(emp.DOJ,'dd-MM-yyyy'),emp.PPAN,emp.PRAN_NO,emp.DDO_CODE ");
			Strbld.append(" FROM mst_dcps_emp emp inner join FRM_FORM_S1_DTLS f on f.SEVARTH_ID = emp.SEVARTH_ID  where  emp.DDO_CODE is null and f.DEP_UPDATED_DDO_CD is null ");

			if (lStrSevaarthId != null && !"".equals(lStrSevaarthId)) {
				Strbld.append(" AND UPPER(emp.SEVARTH_ID) = :sevarthId");
			}

			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

			if (lStrSevaarthId != null && !"".equals(lStrSevaarthId)) {
				lQuery.setString("sevarthId", lStrSevaarthId);
			}

			logger.info("query getAllEmp ---------"+ Strbld.toString());
			lLstEmpDeselect = lQuery.list();

		}
		catch(Exception e)
		{
			logger.info("Error occured in getAllEmp ---------"+ e);
			e.printStackTrace();
		}
		return lLstEmpDeselect;
	}

	public List checkSevaarthIdExist(String lStrSevaarthId) {

		StringBuilder sb = new StringBuilder();
		Query selectQuery = null;

		sb.append(" SELECT SEVARTH_ID, nvl(DDO_CODE,'0'), nvl(dcps_or_gpf,'A') FROM mst_dcps_emp where REG_STATUS = 1 ");

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
	
	public List checkDepDdoCode(String depDdoCode) {
		StringBuilder sb = new StringBuilder();
		Query selectQuery = null;

		sb.append(" SELECT * FROM ORG_DDO_MST where DDO_CODE = '"+depDdoCode+"'");

		logger.info("Inside checkDepDdoCode query is *********** "+sb.toString());
		selectQuery = ghibSession.createSQLQuery(sb.toString());
		List exist = selectQuery.list();
		return exist;

	}

	
	public List checkFormUpdate(String lStrSevaarthId) {

		StringBuilder sb = new StringBuilder();
		Query selectQuery = null;

		sb.append(" SELECT nvl(frm.DEP_UPDATED_DDO_CD,'0') , e.SEVARTH_ID FROM mst_dcps_emp e inner join FRM_FORM_S1_DTLS frm on frm.SEVARTH_ID = e.SEVARTH_ID where 1=1 ");

		if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
			sb.append(" and UPPER(e.SEVARTH_ID) = :lStrSevaarthId");
		}

		logger.info("Inside checkSevaarthIdExist query is *********** "+sb.toString());

		selectQuery = ghibSession.createSQLQuery(sb.toString());

		if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
			selectQuery.setParameter("lStrSevaarthId", lStrSevaarthId.trim().toUpperCase());	
		}
		List exist = selectQuery.list();

		return exist;

	}

	public String updateDdoCode(String sevarthId, String depDdoCode)
	{
		logger.info("Inside updateDdoCode Query************");
		StringBuilder strBuld = new StringBuilder();  
		Query updateQuery = null;
		String flag = "NA";
		try
		{
			strBuld.append(" update FRM_FORM_S1_DTLS set DEP_UPDATED_DDO_CD = '"+depDdoCode+"' where SEVARTH_ID = '"+sevarthId+"'" );

			logger.info("Inside updatePranNo Query************" +strBuld.toString());
			updateQuery = ghibSession.createSQLQuery(strBuld.toString());
			int result=updateQuery.executeUpdate();
			logger.info("result is "+result);

			if ((result!= 0 && result > 0)) 
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


}






