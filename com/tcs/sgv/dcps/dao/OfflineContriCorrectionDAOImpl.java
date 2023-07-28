/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 2, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

/**
 * 
 * Class Description -
 * 
 * 
 * @author Vihan
 * @version 0.1
 * @since JDK 5.0 Mar 4, 2011
 */
public class OfflineContriCorrectionDAOImpl extends GenericDaoHibernateImpl
		implements OfflineContriCorrectionDAO {

	/**
	 * @param type
	 */

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/pensionproc/PensionCaseConstants");

	public OfflineContriCorrectionDAOImpl(Class type,
			SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}

	public List getEmpListForContributionCorrection(String lStrDDOCode,
			Long lLongbillGroupId, Long monthId, Long yearId) {

		List finalList = new ArrayList();

		try {
			StringBuilder SBQuery = new StringBuilder();

			SBQuery
					.append("SELECT EM.DCPS_EMP_ID,EM.DCPS_ID,EM.EMP_NAME,CO.DCPS_CONTRIBUTION_ID "
							+ " FROM mst_dcps_emp EM INNER JOIN TRN_DCPS_CONTRIBUTION CO ON EM.DCPS_EMP_ID=CO.DCPS_EMP_ID "
							+ " AND CO.MONTH_ID="
							+ monthId
							+ " AND CO.FIN_YEAR_ID="
							+ yearId
							+ " WHERE EM.DDO_CODE='"
							+ lStrDDOCode
							+ "'"
							+ " AND BILLGROUP_ID="
							+ lLongbillGroupId
							+ " AND EM.REG_STATUS=1 ");

			Query stQuery = ghibSession.createSQLQuery(SBQuery.toString());

			finalList = stQuery.list();

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			e.printStackTrace();
		}
		return finalList;
	}

	public Long getEmpIdforContributionId(Long contributionId) {
		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Long empId = 0L;

		lSBQuery
				.append(" select dcpsEmpId FROM TrnDcpsContribution WHERE dcpsContributionId = :contributionId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("contributionId", contributionId);

		tempList = lQuery.list();
		empId = tempList.get(0);
		return empId;

	}

}
