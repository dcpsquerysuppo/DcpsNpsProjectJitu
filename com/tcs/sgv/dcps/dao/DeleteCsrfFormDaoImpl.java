package com.tcs.sgv.dcps.dao;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class DeleteCsrfFormDaoImpl extends GenericDaoHibernateImpl{
	private Session ghibSession = null;
	private final Logger gLogger = Logger.getLogger(getClass());
	public DeleteCsrfFormDaoImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
		// TODO Auto-generated constructor stub
	}

	public List getAllEmp(String lStrSevaarthId){

		List lLstEmpDeselect = null;

		StringBuilder  Strbld = new StringBuilder();
		try {
			Strbld.append(" SELECT emp.EMP_NAME,emp.DCPS_ID,emp.SEVARTH_ID, to_char(f.CREATED_DATE,'dd-MM-yyyy'), to_char(emp.DOJ,'dd-MM-yyyy'),nvl(f.MOTHER_NAME,' '),nvl(emp.PRAN_NO,' '),nvl(emp.DDO_CODE,' ') ");
			Strbld.append(" FROM mst_dcps_emp emp inner join FRM_FORM_S1_DTLS f on f.SEVARTH_ID = emp.SEVARTH_ID ");		

			if (lStrSevaarthId != null && !"".equals(lStrSevaarthId)) {
				Strbld.append(" AND UPPER(emp.SEVARTH_ID) = :sevarthId");
			}
			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

			if (lStrSevaarthId != null && !"".equals(lStrSevaarthId)) {
				lQuery.setString("sevarthId", lStrSevaarthId);
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

	public String deleteForm(String SevarthId)
	{
		logger.info("Inside dao deleteForm Query************");

		StringBuilder strBuld = new StringBuilder();  
		Query updateQuery = null;
		String flag = "NA";
		try
		{
			strBuld.append(" delete from FRM_FORM_S1_DTLS where SEVARTH_ID = '"+SevarthId+"'" );

			logger.info("Inside deleteForm Query************" +strBuld.toString());
			updateQuery = ghibSession.createSQLQuery(strBuld.toString());

//			if (!"".equals(SevarthId) && SevarthId != null) {
//				updateQuery.setParameter("strSevarthId", SevarthId.trim());
//			}
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






