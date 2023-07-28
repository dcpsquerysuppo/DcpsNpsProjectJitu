/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Sep 06, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 Sep 06, 2011
 */
public class ChallanEntryDAOImpl extends GenericDaoHibernateImpl implements ChallanEntryDAO {
	Session ghibSession = null;

	public ChallanEntryDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	/**
	 * DAO method to get the pending re-fundable advances for GPF account number
	 * 
	 * @param String
	 *            lStrGpfAccNo
	 * @return List
	 */
	public List getPendingAdvance(String lStrGpfAccNo) {
		List lLstGPFAdvance = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		Date lDtCurDate = new Date();
		Date lDtStartDate = new Date(lDtCurDate.getYear(), lDtCurDate.getMonth(), 01);

		try {
			lSBQuery.append("SELECT mstGpfAdvanceId,transactionId,outstandingAmount,installmentAmount");
			lSBQuery.append(" FROM MstGpfAdvance");
			lSBQuery.append(" WHERE gpfAccNo = :gpfAccNo AND installmentsLeft != 0 AND statusFlag='A' AND advanceType ='RA'");
			lSBQuery.append(" AND sanctionedDate < :prevDate");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);			
			lQuery.setParameter("prevDate", lDtStartDate);

			lLstGPFAdvance = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getPendingAdvance of ChallanEntryDAOImpl  : ", e);
		}
		return lLstGPFAdvance;
	}

	/**
	 * DAO method to get Employee details using GPF acc no and ddo code
	 * 
	 * @param String
	 *            lStrGpfAccNo, String lStrDdoCode
	 * @return List
	 */

	public List getEmployeeData(String lStrGpfAccNo, String lStrDdoCode) {
		List lLstEmpList = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();

		try {
			lSBQuery.append("SELECT MGE.name,MGE.group,MGE.doj,OEM.empSrvcExp");
			lSBQuery.append(" FROM MstEmp MGE,OrgEmpMst OEM,MstEmpGpfAcc MGA");
			lSBQuery
					.append(" WHERE MGE.orgEmpMstId = OEM.empId AND MGE.dcpsEmpId = MGA.mstGpfEmpId AND MGE.dcpsOrGpf='N' AND MGA.ddoCode =:ddoCode AND MGA.gpfAccNo = :gpfAccNo AND MGE.group ='D'");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);

			lLstEmpList = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getEmployeeData of ChallanEntryDAOImpl  : ", e);
		}
		return lLstEmpList;
	}

}
