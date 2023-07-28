package com.tcs.sgv.gpf.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;


/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Jul 18, 2012
 */

public class GPFMissingCreditDAOImpl extends GenericDaoHibernateImpl implements GPFMissingCreditDAO
{
	Session ghibSession = null;
	public GPFMissingCreditDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public String getGpfAccNo(String lStrSevaarthId) throws Exception 
	{
		String lStrGpfAccNo = "";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT gpfAccNo");
			lSBQuery.append(" FROM MstEmpGpfAcc");
			lSBQuery.append(" WHERE sevaarthId = :sevaarthId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrSevaarthId);
			lStrGpfAccNo = lQuery.list().get(0).toString();
		}catch (Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lStrGpfAccNo;
	}
	
	public String getBillGroupId(String lStrSevaarthId) throws Exception
	{
		String lStrBillGroupId="";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT billGroupId ");
			lSBQuery.append("FROM MstEmp ");
			lSBQuery.append("WHERE sevarthId = :sevarthId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevarthId", lStrSevaarthId);
			lStrBillGroupId = lQuery.list().get(0).toString();
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		
		return lStrBillGroupId;
	}
	
	public Long getMissingCreditId(String lStrGpfAccNo, String lStrFinYear, String lStrMonth, Long lLngBillGroup, String lStrStatus)throws Exception
	{
		Long lLngGpfMonthlyId = null;
		List lLstResData = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT mstGpfMissingCreditId FROM MstGpfMissingCredit ");
			lSBQuery.append("WHERE gpfAccNo =:gpfAccNo AND finYearId =:finYear AND monthId =:monthId ");
			lSBQuery.append("AND billgroupId =:billGroup AND status = :status");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("status", lStrStatus);
			lQuery.setLong("finYear", Long.parseLong(lStrFinYear));
			lQuery.setLong("monthId", Long.parseLong(lStrMonth));
			lQuery.setLong("billGroup", lLngBillGroup);
			lLstResData = lQuery.list();
			
			if(lLstResData != null && lLstResData.size() > 0){
				lLngGpfMonthlyId = (Long) lLstResData.get(0);
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lLngGpfMonthlyId;
	}
	
	public String chkForApprovedReq(String lStrGpfAccNo, String []lArrFinYear, String []lArrMonth, Long lLngBillGroup)throws Exception
	{
		Long lLngGpfMonthlyId = null;
		List lLstResData = null;
		StringBuilder lSBQuery = null;
		String lStrFinalData = "Y";
		
		try{
			for(Integer lIntCnt = 0; lIntCnt < lArrFinYear.length; lIntCnt++)
			{
				lSBQuery = new StringBuilder();
				lSBQuery.append("SELECT mstGpfMissingCreditId FROM MstGpfMissingCredit ");
				lSBQuery.append("WHERE gpfAccNo =:gpfAccNo AND finYearId =:finYear AND monthId =:monthId ");
				lSBQuery.append("AND billgroupId =:billGroup AND status IN ('S','A')");
				Query lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("gpfAccNo", lStrGpfAccNo);			
				lQuery.setLong("finYear", Long.parseLong(lArrFinYear[lIntCnt]));
				lQuery.setLong("monthId", Long.parseLong(lArrMonth[lIntCnt]));
				lQuery.setLong("billGroup", lLngBillGroup);
				lLstResData = lQuery.list();
				
				if(lLstResData != null && lLstResData.size() > 0){
					lStrFinalData = lArrMonth[lIntCnt] +","+ lArrFinYear[lIntCnt];
					break;
				}
			}
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lStrFinalData;
	}
	
	public String getMonthNameFromID(String lStrMonth)throws Exception
	{
		String lStrMonthName = "";
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT monthName FROM SgvaMonthMst ");
			lSBQuery.append("WHERE monthId =:monthId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setBigDecimal("monthId", new BigDecimal(lStrMonth));
			lStrMonthName = lQuery.list().get(0).toString();
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		
		return lStrMonthName;
	}
	
	public List getInstAmountTrnIdForCurrentRA(String lStrGpfAccNo)throws Exception
	{		
		List lLstResData = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT transactionId, installmentAmount FROM MstGpfAdvance WHERE advanceType = 'RA' ");
			lSBQuery.append("AND statusFlag = 'A' AND installmentsLeft > 0 AND outstandingAmount > 0 ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lLstResData = lQuery.list();			
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lLstResData;
	}
	
	public List getDraftData(String lStrGpfAccNo, Long lLngBillGroup)throws Exception
	{		
		List lLstResData = null;
		StringBuilder lSBQuery = null;		
		
		try{
			lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT regularSubscription, advanceRecovery, finYearId, monthId, advanceTrnId,voucherNo, voucherDate, mstGpfMissingCreditId FROM MstGpfMissingCredit ");
			lSBQuery.append("WHERE gpfAccNo =:gpfAccNo AND billgroupId =:billGroup AND status = 'D'");			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);			
			lQuery.setLong("billGroup", lLngBillGroup);
			lLstResData = lQuery.list();			
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lLstResData;
	}
	
	public List loadDraftDataWorkList(Long lLngPostId)throws Exception
	{
		List lLstMissingDraft = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT DISTINCT MEGA.sevaarthId, OEM.empFname || ' ' || OEM.empMname || ' ' || OEM.empLname ");
			lSBQuery.append("FROM MstEmpGpfAcc MEGA, MstGpfMissingCredit MGM, MstEmp ME, OrgEmpMst OEM, OrgDdoMst ODM ");
			lSBQuery.append("WHERE MGM.status = 'D' AND ODM.postId =:postId AND MGM.gpfAccNo = MEGA.gpfAccNo ");
			lSBQuery.append("AND ODM.ddoCode = MEGA.ddoCode AND ME.sevarthId = MEGA.sevaarthId ");
			lSBQuery.append("AND ME.orgEmpMstId = OEM.empId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("postId", lLngPostId);
			lLstMissingDraft = lQuery.list();
		}catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lLstMissingDraft;
	}	
}
