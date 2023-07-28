/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 23, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.DcpsCadreMst;

/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Mar 23, 2011
 */
public class DCPSCadreMasterDAOImpl extends GenericDaoHibernateImpl implements
		DCPSCadreMasterDAO {

	Session ghibSession = null;
	private static final Logger gLogger = Logger
			.getLogger(DCPSCadreMasterDAOImpl.class);

	public DCPSCadreMasterDAOImpl(Class type, SessionFactory sessionFactory) {
		// TODO Auto-generated constructor stub
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.dcps.dao.DCPSCadreMasterDAO#getCadreList()
	 */
	public List<DcpsCadreMst> getCadreList() throws Exception {
		List<DcpsCadreMst> lLstCadreDtls = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			ghibSession = getSession();
			String query = "select cadreCode,cadreName,fieldDeptId,CLM.lookupName,ministerialFlag,superAntunAge FROM DcpsCadreMst CD,CmnLookupMst CLM WHERE CD.groupId=CLM.lookupId ";
			lSBQuery = new StringBuilder();
			lSBQuery.append(query);
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			lLstCadreDtls = hqlQuery.list();
		} catch (Exception e) {
			e.printStackTrace();
			gLogger
					.info("Error  while executing getCadreList of  DCPSCadreMasterDAOImpl is "
							+ e);

		}
		return lLstCadreDtls;
	}
}
