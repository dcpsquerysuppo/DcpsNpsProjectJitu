package com.tcs.sgv.gpf.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

/**
 * @author 397138
 * 
 */
public class GPFApprovedRequestDAOImpl extends GenericDaoHibernateImpl implements GPFApprovedRequestDAO {
	Session ghibSession = null;

	public GPFApprovedRequestDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFApprovedRequestDAO#getGPFApprovedRequestList(java
	 * .lang.String)
	 */
	public List getGPFApprovedRequestList(String lStrLocationCode) {

		List lGpfApprovedListAdvance = new ArrayList();
		List lGpfApprovedListFinal = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		StringBuilder lSBQueryWd = new StringBuilder();
		try {
			lSBQuery
					.append("select MGA.transactionId,MGA.applicationDate,MG.sevaarthId,MGE.name,MGA.gpfAccNo,MGA.advanceType,MGA.mstGpfAdvanceId");
			lSBQuery.append(" FROM MstGpfAdvance MGA,MstEmpGpfAcc MG,MstEmp MGE,OrgDdoMst ODM");
			lSBQuery
					.append(" WHERE MGA.gpfAccNo = MG.gpfAccNo AND MGA.statusFlag IN ('A','AC') AND MG.mstGpfEmpId = MGE.dcpsEmpId AND MGE.dcpsOrGpf='N' AND MG.ddoCode=ODM.ddoCode AND ODM.locationCode=:locationCode AND MGE.group ='D' and MGA.dataEntry is null");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("locationCode", lStrLocationCode);
			lGpfApprovedListAdvance = lQuery.list();

			lSBQueryWd
					.append("select TGFW.transactionId,TGFW.applicationDate,MG.sevaarthId,MGE.name,TGFW.gpfAccNo,'FW',TGFW.trnGpfFinalWithdrawalId");
			lSBQueryWd.append(" FROM TrnGpfFinalWithdrawal TGFW,MstEmpGpfAcc MG,MstEmp MGE,OrgDdoMst ODM");
			lSBQueryWd
					.append(" WHERE TGFW.gpfAccNo = MG.gpfAccNo AND TGFW.reqStatus = 'A' AND MG.mstGpfEmpId = MGE.dcpsEmpId AND MGE.dcpsOrGpf='N' AND MG.ddoCode=ODM.ddoCode AND ODM.locationCode=:locationCode AND MGE.group ='D'");
			Query lQueryF = ghibSession.createQuery(lSBQueryWd.toString());
			lQueryF.setParameter("locationCode", lStrLocationCode);
			lGpfApprovedListFinal = lQueryF.list();

			// add the Final Requests List to Advance List
			lGpfApprovedListAdvance.addAll(lGpfApprovedListFinal);

		} catch (Exception e) {
			logger.error("Exception in getGPFApprovedRequestList of GPFApprovedRequestDAOImpl  : ", e);
		}
		return lGpfApprovedListAdvance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.gpf.dao.GPFApprovedRequestDAO#getNewOrderRefId()
	 */
	public String getNewOrderRefId(String lStrLocCode) {

		StringBuilder lSBQuery = null;
		new StringBuilder();
		new StringBuilder();
		List tempList = new ArrayList<Long>();
		new ArrayList<Long>();
		new ArrayList<Long>();
		Long lLngNewSeqId = 0L;
		String lStrNewOrdRefId = null;
		String lStrOrderId = "";
		String lStrMonth = "";
		String lStrDate = "";
		String lStrDdoCode = "";
		List lLstChkNewYear = null;
		String lStrChkNewYear = "";

		Calendar cal = Calendar.getInstance();

		try {
			Integer lIntMonth = cal.get(Calendar.MONTH) + 1;
			Integer lIntYear = cal.get(Calendar.YEAR);
			Integer lIntDate = cal.get(Calendar.DATE);

			if (lIntMonth.toString().length() == 1) {
				lStrMonth = "0" + lIntMonth;
			} else {
				lStrMonth = lIntMonth.toString();
			}
			if (lIntDate.toString().length() == 1) {
				lStrDate = "0" + lIntDate;
			} else {
				lStrDate = lIntDate.toString();
			}

			// code to get the First letter of Order Id (i.e. organization id)
			// from sevaarth Id
			// lStrOrderId = lStrSevaarthId.charAt(0) + lStrMonth +
			// lIntYear.toString().substring(2, 4);

			lStrDdoCode = getDdoCode(lStrLocCode);

			lStrChkNewYear = "GPF/" + lStrDdoCode + "/";
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT orderNo FROM MstGpfReq WHERE orderNo LIKE :lStrOrderId");
			Query lQueryChk = ghibSession.createQuery(lSBQuery.toString());
			lQueryChk.setParameter("lStrOrderId", lStrChkNewYear + '%' + lIntYear + '%');
			lLstChkNewYear = lQueryChk.list();

			lStrOrderId = "GPF/" + lStrDdoCode + "/" + lStrDate + lStrMonth + lIntYear.toString() + "/";

			if (lLstChkNewYear != null && lLstChkNewYear.size() > 0) {
				lSBQuery = new StringBuilder();
				lSBQuery.append(" SELECT MAX(orderNo) FROM MstGpfReq WHERE orderNo LIKE :lStrOrderId");

				Query lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("lStrOrderId", lStrOrderId + '%');
				tempList = lQuery.list();

				// in case its the first request of the month, the counter
				// starts
				// from 1, or next sequence number
				if (tempList != null && tempList.size() > 0 && tempList.get(0) != null) {
					lLngNewSeqId = (Long.parseLong(tempList.get(0).toString().substring(24))) + 1L;
					lStrNewOrdRefId = String.format(lStrOrderId + "%06d", lLngNewSeqId);
				} else {
					lStrNewOrdRefId = String.format(lStrOrderId + "%06d", 1);
				}
			} else {
				lStrNewOrdRefId = String.format(lStrOrderId + "%06d", 1);
			}
		} catch (Exception e) {
			logger.error("Exception in getNewOrderRefId of GPFApprovedRequestDAOImpl  : ", e);
		}

		return lStrNewOrdRefId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFApprovedRequestDAO#getGpfReqID(java.lang.String)
	 */
	public String getGpfReqID(String transactionId) {
		StringBuilder lSBQuery = new StringBuilder();
		List lLstGpfReqId = null;
		String lStrGpfReqId = "";

		try {
			lSBQuery.append("SELECT mstGpfReqId FROM MstGpfReq ");
			lSBQuery.append("WHERE ");
			lSBQuery.append("transactionId =:transactionId");
			Query lHqlQuery = ghibSession.createQuery(lSBQuery.toString());
			lHqlQuery.setParameter("transactionId", transactionId);
			lLstGpfReqId = lHqlQuery.list();

			if (lLstGpfReqId != null && lLstGpfReqId.size() > 0) {
				lStrGpfReqId = lLstGpfReqId.get(0).toString();
			}
		} catch (Exception e) {
			logger.error("Exception in getGpfReqID of GPFApprovedRequestDAOImpl  : ", e);
		}

		return lStrGpfReqId;
	}

	public String getDdoCode(String lStrLocationCode) throws Exception {
		String lStrDdoCode = "";

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ddoCode FROM OrgDdoMst WHERE locationCode =:locCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("locCode", lStrLocationCode);
			lStrDdoCode = lQuery.list().get(0).toString();
		} catch (Exception e) {
			logger.error("Exception in getDdoCode of GPFApprovedRequestDAOImpl  : ", e);
			throw e;
		}

		return lStrDdoCode;
	}
	
	public List LoadBillWorkList(String lStrLocationCode) throws Exception
	{
		List lLstBillList = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT transactionId, gpfAccNo, billGeneratedDate, orderNo, orderDate, voucherNo, voucherDate, statusFlag, billDtlsId, advacnceType,billNo ");
			lSBQuery.append("FROM MstGpfBillDtls ");
			lSBQuery.append("WHERE locationCode = :locCode AND statusFlag IN (0,1)");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("locCode", lStrLocationCode);
			
			lLstBillList = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in LoadBillWorkList of GPFApprovedRequestDAOImpl  : ", e);
			throw e;
		}

		return lLstBillList;
	}
	
	public List getAdvanceDetails(String lStrAdvanceTranId) throws Exception
	{
		List lLstAdvanceDtls = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT amountSanctioned, noOfInstallments, installmentAmount, oddInstallment, clubbedReqTrnId ");
			lSBQuery.append("FROM MstGpfAdvance ");
			lSBQuery.append("WHERE transactionId = :transactionId");
			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("transactionId", lStrAdvanceTranId);
			
			lLstAdvanceDtls = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getAdvanceDetails of GPFApprovedRequestDAOImpl  : ", e);
			throw e;
		}

		return lLstAdvanceDtls;
	}
	public int getMaxOfBillNo() 
	{
		int billNO = 0;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("select max(billNo) from MstGpfBillDtls bill ");
			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());			
			
			if( lQuery != null && lQuery.list() != null){
			List lstBillNo =  lQuery.list();
				if(!lstBillNo.isEmpty())
					billNO = lstBillNo.get(0) != null ? Integer.parseInt(lstBillNo.get(0).toString()) :  0;
			}
			
		} catch (Exception e) {
			logger.error("Exception in getAdvanceDetails of GPFApprovedRequestDAOImpl  : ", e);
			
		}

		return billNO;
	}

	
}
