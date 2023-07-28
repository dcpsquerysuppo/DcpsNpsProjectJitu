package com.tcs.sgv.dcps.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Jul 23, 2012
 */

public class UpdateSixPCArrearMasterAmountDAOImpl extends GenericDaoHibernateImpl implements UpdateSixPCArrearMasterAmountDAO
{
	Session ghibSession = null;
	public UpdateSixPCArrearMasterAmountDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public List getDataFromEmpCode(String lStrEmpCode)throws Exception
	{
		List lLstArrearData = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ME.dcpsEmpId, ME.payCommission, ME.name, MS.totalAmount, MS.amountPaid FROM MstEmp ME, ");
			lSBQuery.append("MstSixPCArrears MS WHERE MS.dcpsEmpId = ME.dcpsEmpId AND MS.statusFlag = 'A' AND ME.sevarthId =:sevarthId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevarthId", lStrEmpCode);
			lLstArrearData = lQuery.list();
		}catch(Exception e){
			logger.error("Error is: " + e, e);
			throw e;
		}
		return lLstArrearData;
	}
	
	public void updateMstDcpsSixPC(Long lLngDcpsEmpId, Long lLngNewAmount, Long lLngPostId, Long lLngUserId)throws Exception
	{
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE MstSixPCArrears SET totalAmount =:totalAmount, updatedUserId=:updtUserId, ");
			lSBQuery.append("updatedPostId =:updtPostId, updatedDate =:updtDate ");
			lSBQuery.append("WHERE dcpsEmpId = :dcpsEmpId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("totalAmount", lLngNewAmount);
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			lQuery.setLong("updtUserId", lLngUserId);
			lQuery.setLong("updtPostId", lLngPostId);
			lQuery.setParameter("updtDate", DBUtility.getCurrentDateFromDB());
			lQuery.executeUpdate();
		}catch(Exception e){
			logger.error("Error is: " + e, e);
			throw e;
		}
	}
	
	public void updateRltDcpsSixPCYearly(Long lLngDcpsEmpId, Long lLngNewAmount, 
			String lStrPayComm, Long lLngPostId, Long lLngUserId)throws Exception
	{
		Long lLngYearlyAmt = null;
		try{
			if(lStrPayComm.equals("700339")){
				lLngYearlyAmt = lLngNewAmount/2;
			}else{
				lLngYearlyAmt = lLngNewAmount/5;
			}
			
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE RltDcpsSixPCYearly SET yearlyAmount =:yearlyAmount, updatedUserId =:updtUserId, ");
			lSBQuery.append("updatedPostId =:updtPostId, updatedDate= :updtDate ");
			lSBQuery.append("WHERE dcpsEmpId = :dcpsEmpId AND statusFlag IN ('D','R','F','G')");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("yearlyAmount", lLngYearlyAmt);
			lQuery.setLong("dcpsEmpId", lLngDcpsEmpId);
			lQuery.setLong("updtUserId", lLngUserId);
			lQuery.setLong("updtPostId", lLngPostId);
			lQuery.setParameter("updtDate", DBUtility.getCurrentDateFromDB());
			lQuery.executeUpdate();
		}catch(Exception e){
			logger.error("Error is: " + e, e);
			throw e;
		}
	}
}
