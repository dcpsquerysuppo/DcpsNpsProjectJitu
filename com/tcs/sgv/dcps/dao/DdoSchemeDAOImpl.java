/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 26, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAOImpl;


/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 26, 2011
 */
public class DdoSchemeDAOImpl extends GenericDaoHibernateImpl implements DdoSchemeDAO {

	private Session ghibSession = null;
	private static final Logger gLogger = Logger.getLogger(TrnPnsnProcInwardPensionDAOImpl.class);

	public DdoSchemeDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}

	public List getSchemeListForDDO(String lStrDDOCode) {

		List lDcpsDdoSchemList = null;
		try {
			StringBuilder SBQuery = new StringBuilder();

			SBQuery
					.append("select rlt.dcpsSchemeCode,mst.schemeName FROM RltDcpsDdoScheme rlt,MstScheme mst where mst.schemeCode=rlt.dcpsSchemeCode and rlt.dcpsDdoCode = :ddoCode order by rlt.dcpsSchemeCode,mst.schemeName ");
			Query lQuery = ghibSession.createQuery(SBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDDOCode);
			lDcpsDdoSchemList = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is :" + e, e);

		}
		return lDcpsDdoSchemList;
	}

	public List getSchemeNamesFromCode(String schemeCode, String lStrDdoCode) {

		List resultList = null;

		try {
			StringBuilder SBQuery = new StringBuilder();

			SBQuery.append("SELECT MS.scheme_code, MS.scheme_name FROM Mst_Scheme MS WHERE scheme_Code LIKE '" + schemeCode + "%'");
			Query lQuery = ghibSession.createSQLQuery(SBQuery.toString());

			resultList = lQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);

		}
		return resultList;
	}

	public String getSchemeNameFromCode(String schemeCode) {

		String schemeName = null;

		try {
			StringBuilder SBQuery = new StringBuilder();

			SBQuery.append("select schemeName from MstScheme where schemeCode = :schemeCode");
			Query lQuery = ghibSession.createQuery(SBQuery.toString());
			lQuery.setParameter("schemeCode", schemeCode);

			schemeName = lQuery.list().get(0).toString();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);

		}
		return schemeName;
	}

}
