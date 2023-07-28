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
import com.tcs.sgv.dcps.valueobject.DDOInformationDetail;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAOImpl;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 26, 2011
 */
public class DdoOfficeDAOImpl extends GenericDaoHibernateImpl implements
		DdoOfficeDAO {

	private Session ghibSession = null;
	private static final Logger gLogger = Logger
			.getLogger(TrnPnsnProcInwardPensionDAOImpl.class);

	public DdoOfficeDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}

	public List getAllOffices(String lStrDdoCode) {

		List listSavedOffices = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery
				.append(" SELECT dcpsDdoOfficeIdPk,dcpsDdoOfficeName,dcpsDdoOfficeDdoFlag,dcpsDdoOfficeAddress1,dcpsDdoOfficeAddress2 ");
		lSBQuery.append(" FROM DdoOffice");
		lSBQuery.append(" WHERE dcpsDdoCode = :ddoCode order by dcpsDdoOfficeDdoFlag DESC");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ddoCode", lStrDdoCode);

		listSavedOffices = lQuery.list();

		return listSavedOffices;
	}

	public DDOInformationDetail getDdoInfo(String lStrDdoCode) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("FROM DDOInformationDetail");
		lSBQuery.append(" WHERE ddoCode = :ddoCode ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ddoCode", lStrDdoCode);

		DDOInformationDetail lObjDdoInformation = (DDOInformationDetail) lQuery
				.uniqueResult();

		return lObjDdoInformation;
	}

	public DdoOffice getDdoOfficeDtls(Long ddoOfficeId) {

		List<DdoOffice> resultList = null;

		try {
			StringBuilder SBQuery = new StringBuilder();
			SBQuery
					.append("from DdoOffice where dcpsDdoOfficeIdPk = :ddoOfficeId");
			Query lQuery = ghibSession.createQuery(SBQuery.toString());
			lQuery.setParameter("ddoOfficeId", ddoOfficeId);

			resultList = lQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);

		}
		return resultList.get(0);
	}

	public void updateDdoOffice(String lStrDdoOffice, String lStrDdoCode) {

		try {
			StringBuilder SBQuery = new StringBuilder();
			SBQuery
					.append("UPDATE OrgDdoMst SET ddoOffice = :ddoOffice, ddoName = CONCAT(designName,', ',ddoOffice) WHERE ddoCode = :ddoCode");

			Query lQuery = ghibSession.createQuery(SBQuery.toString());
			lQuery.setParameter("ddoOffice", lStrDdoOffice);
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);

		}

	}
	
	public void updateOtherOfficeToNO(Long lLongDdoOfficeId,String lStrDdoCode) {

		try {
			StringBuilder SBQuery = new StringBuilder();
			SBQuery
					.append("UPDATE DdoOffice SET dcpsDdoOfficeDdoFlag = :dcpsDdoOfficeDdoFlag where dcpsDdoCode = :dcpsDdoCode and dcpsDdoOfficeIdPk <> :lLongDdoOfficeId");

			Query lQuery = ghibSession.createQuery(SBQuery.toString());
			lQuery.setParameter("dcpsDdoOfficeDdoFlag", "No");
			lQuery.setParameter("lLongDdoOfficeId", lLongDdoOfficeId);
			lQuery.setParameter("dcpsDdoCode", lStrDdoCode.trim());
			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);

		}

	}

	public String getDefaultDdoOffice(String lStrDdoCode) {
		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("Select ddoOffice FROM OrgDdoMst ");
		lSBQuery.append(" WHERE ddoCode = :ddoCode ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ddoCode", lStrDdoCode);

		String lStrDdoOffice = lQuery.list().get(0).toString();

		return lStrDdoOffice;
	}
	
	public List isNewAddressAdded (String lStrDdoCode)
	{
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;
		List lAddessMiss=null;
		gLogger.info("DDO code is ***********in DAO*********"+lStrDdoCode);
		lSBQuery.append(" Select distinct upper(OFF_NAME) FROM MST_DCPS_DDO_OFFICE ");
		lSBQuery.append("  WHERE DDO_CODE = :lStrDdoCode  and ((VILLAGE_NEW is null or VILLAGE_NEW=' ') or  (EMAIL_NEW  is null or EMAIL_NEW=' ') or (TOWN_NEW is null or TOWN_NEW = ' ') ");
		lSBQuery.append(" or (TEL_NO_NEW is null) or (ADDRESS1_NEW is null or ADDRESS1_NEW = ' ')  or (DISTRICT is null or DISTRICT = ' ') or (TALUKA is null or TALUKA = ' ') or (OFFICE_CITY_CLASS is null or OFFICE_CITY_CLASS = ' ') or (OFFICE_PIN is null) or (Length(trim(MOBILE_NO_NEW)) < 12)) ");
		lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("lStrDdoCode", lStrDdoCode);
		gLogger.info("isNewAddressAdded**********"+lQuery);
	    lAddessMiss = lQuery.list();
		
		return lAddessMiss;
	}

	
}
