package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.RltPensioncaseBill;


public class RltPensioncaseBillDAOImpl extends GenericDaoHibernateImpl<RltPensioncaseBill, Long> implements RltPensioncaseBillDAO {

	Log gLogger = LogFactory.getLog(getClass());

	public RltPensioncaseBillDAOImpl(Class<RltPensioncaseBill> type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}

	public List getPKForRltPensioncaseBill(String lStrPPONo, String lStrBillType, String lStrStatus, String lStrLocCode) throws Exception {

		List lLstPK = null;
		StringBuffer lSBQuery = new StringBuffer();
		try {
			Session ghibSession = getSession();
			lSBQuery.append(" select r.rlt_pensioncase_bill_id from Rlt_Pensioncase_Bill r where r.ppo_no = :PPONo AND r.bill_type = :billType"
					+ " AND r.status = :status AND r.bill_no is null AND r.location_code = :locationCode ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("PPONo", lStrPPONo);
			lQuery.setString("billType", lStrBillType);
			lQuery.setString("status", lStrStatus);
			lQuery.setString("locationCode", lStrLocCode);

			lLstPK = lQuery.list();
			lLstPK.toString();

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstPK;
	}

	public void updateBillStatusByPPONoAndStatusAndBillType(String lStrPPONo, String lStrStatus, List lLstBillType, String lStrLocCd, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		try {
			Session ghibSession = getSession();
			lSBQuery.append(" UPDATE RltPensioncaseBill SET status =:status,updatedUserId = :updated_user_id,updatedPostId = :updated_post_id,updatedDate = :updated_date ");
			if (lStrStatus != null && lStrStatus.length() > 0) {
				lSBQuery.append(" WHERE status !=:status ");
			}
			if (lStrPPONo != null && lStrPPONo.length() > 0) {
				lSBQuery.append(" AND ppoNo =:PPONo ");
			}
			if (lLstBillType != null && !lLstBillType.isEmpty()) {
				lSBQuery.append(" AND billType in (:billType)");
			}
			lSBQuery.append(" and billNo is not null AND locationCode = :locationCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			if (lStrStatus != null && lStrStatus.length() > 0) {
				lQuery.setString("status", lStrStatus);
			}
			if (lStrPPONo != null && lStrPPONo.length() > 0) {
				lQuery.setString("PPONo", lStrPPONo);
			}
			if (lLstBillType != null && !lLstBillType.isEmpty()) {
				lQuery.setParameterList("billType", lLstBillType);
			}
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			lQuery.setString("locationCode", lStrLocCd);
			lQuery.executeUpdate();

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
	}

	public void updateBillStatusByPPONoAndBillType(Map<String, List> statusMap, String lStrLocCd, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		try {
			Session hibSession = getSession();

			lSBQuery.append(" UPDATE RltPensioncaseBill SET status = :status,updatedUserId = :updated_user_id,updatedPostId = :updated_post_id,updatedDate = :updated_date "
					+ " WHERE status != :status AND ppoNo in (:ppoNoList) " + " AND billType =:billType AND billNo is not null AND locationCode = :locationCode ");

			Query lQuery = hibSession.createQuery(lSBQuery.toString());

			for (String keyStat : statusMap.keySet()) {
				List ppoNoLst = statusMap.get(keyStat);
				if (ppoNoLst != null && !ppoNoLst.isEmpty()) {
					lQuery.setParameterList("ppoNoList", ppoNoLst);
					lQuery.setString("billType", keyStat);
					lQuery.setString("status", "N");
					lQuery.setString("locationCode", lStrLocCd);
					lQuery.setLong("updated_user_id", gLngUserId);
					lQuery.setLong("updated_post_id", gLngPostId);
					lQuery.setDate("updated_date", gDate);
					lQuery.executeUpdate();
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
	}

	public ArrayList getPKPnsnCseBlbypaymode(String lstrPPONo, String lStrLocCd) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		ArrayList resultList = null;
		try {
			Session ghibSession = getSession();
			lSBQuery.append(" SELECT RPB.rltPensioncaseBillId,RPB.billType FROM RltPensioncaseBill RPB WHERE RPB.ppoNo =:ppoNo  AND RPB.billType in(:CVP,:DCRG) "
					+ " AND RPB.billNo is null AND RPB.status = 'N' AND RPB.payMode in(:MFlag,:CFlag) AND RPB.locationCode = :locationCode ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("ppoNo", lstrPPONo);
			lQuery.setString("CFlag", "C");
			lQuery.setString("MFlag", "M");
			lQuery.setString("CVP", "CVP");
			lQuery.setString("DCRG", "DCRG");
			lQuery.setString("locationCode", lStrLocCd);
			resultList = (ArrayList) lQuery.list();
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return resultList;
	}

	public void updateRltPensioncaseBillForMonthlyByPPONo(String ppoNo, String lStrStatus, List<Long> lLstPkList, String lStrBillType, String lStrLocCd, Long gLngPostId, Long gLngUserId, Date gDate)
			throws Exception {

		try {
			StringBuffer lSBQuery = new StringBuffer();
			Session ghibSession = getSession();
			lSBQuery
					.append(" UPDATE RLT_PENSIONCASE_BILL SET STATUS =:Status,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date   WHERE PPO_NO =:ppoNo  AND BILL_NO IS NULL  AND STATUS != :Status AND LOCATION_CODE = :LOCATION_CODE ");
			if (lLstPkList != null) {
				lSBQuery.append(" and RLT_PENSIONCASE_BILL_ID IN (:lLstPkList) ");
			}
			if (lStrBillType != null) {
				lSBQuery.append(" and BILL_TYPE =:billType ");
			}
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("ppoNo", ppoNo);
			lQuery.setString("Status", lStrStatus);
			lQuery.setString("LOCATION_CODE", lStrLocCd);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			if (lLstPkList != null) {
				lQuery.setParameterList("lLstPkList", lLstPkList);
			}
			if (lStrBillType != null) {
				lQuery.setString("billType", lStrBillType);
			}
			lQuery.executeUpdate();
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
	}

	public void deleteCVPDCRGRowsforUpdation(String ppoNo, String billType, String status, String lStrPayMode, String lStrLocCd) throws Exception {

		try {
			StringBuffer lSBQuery = new StringBuffer();
			Session ghibSession = getSession();
			lSBQuery.append(" DELETE FROM RLT_PENSIONCASE_BILL WHERE PPO_NO =:ppoNo AND status =:status AND BILL_NO IS NULL AND bill_type = :billType AND LOCATION_CODE = :LOCATION_CODE ");
			if (lStrPayMode != null && "-1".equals(lStrPayMode)) {
				lSBQuery.append(" And PAY_MODE is null ");
			}
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("ppoNo", ppoNo);
			lQuery.setString("billType", billType);
			lQuery.setString("status", status);
			lQuery.setString("LOCATION_CODE", lStrLocCd);
			lQuery.executeUpdate();
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
	}

	public void updateBillStatusByPKValuesList(List<Long> lLstPkVals, String lStrStatus, String lStrLocCd, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		try {
			StringBuffer lSBQuery = new StringBuffer();
			Session ghibSession = getSession();
			lSBQuery
					.append(" UPDATE RLT_PENSIONCASE_BILL SET STATUS =:Status ,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date WHERE RLT_PENSIONCASE_BILL_ID IN(:PkList) AND LOCATION_CODE = :LOCATION_CODE ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameterList("PkList", lLstPkVals);
			lQuery.setString("Status", lStrStatus);
			lQuery.setString("LOCATION_CODE", lStrLocCd);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			lQuery.executeUpdate();
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw e;
		}
	}

	public Double getAmountFromRltCaseBillByBillTypeAndMode(String ppoNo, String lStrStatus, String lStrBillType, String lStrPaymode, String lLocCode) throws Exception {

		Double lDBLBillAmount = 0D;
		try {
			StringBuffer lSBQuery = new StringBuffer();
			Session ghibSession = getSession();
			lSBQuery.append(" SELECT SUM(RPB.PAY_AMOUNT) SPAY_AMOUNT FROM RLT_PENSIONCASE_BILL RPB " + " WHERE RPB.PPO_NO =:ppoNo  " + " AND RPB.BILL_TYPE =:BillType "
					+ " AND RPB.PAY_MODE =:PayMode " + " AND RPB.STATUS =:Status " + " AND RPB.BILL_NO IS NULL " + " AND RPB.LOCATION_CODE = :locCode");
			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.addScalar("SPAY_AMOUNT", Hibernate.BIG_DECIMAL);

			lQuery.setString("ppoNo", ppoNo);
			lQuery.setString("Status", lStrStatus);
			lQuery.setString("PayMode", lStrPaymode);
			lQuery.setString("BillType", lStrBillType);
			lQuery.setString("locCode", lLocCode);
			List resultList = lQuery.list();
			if (!resultList.isEmpty()) {
				if (resultList.get(0) != null) {
					lDBLBillAmount = Double.valueOf(resultList.get(0).toString());
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw e;
		}
		return lDBLBillAmount;
	}

	public void updateRltPensioncaseBillStatusAndBillNoForByPPONo(String ppoNo, String lStrStatus, List<Long> lLstPkList, String lStrBillType, Long lLngBillNo, String lStrLocCd, Long gLngPostId,
			Long gLngUserId, Date gDate) throws Exception {

		try {
			StringBuffer lSBQuery = new StringBuffer();
			Session ghibSession = getSession();
			lSBQuery
					.append(" UPDATE RLT_PENSIONCASE_BILL SET STATUS =:Status,BILL_NO =:billNo,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date  WHERE PPO_NO =:ppoNo AND BILL_NO IS NULL AND STATUS !=:Status AND LOCATION_CODE = :LOCATION_CODE ");

			if (lStrBillType != null) {
				lSBQuery.append(" and BILL_TYPE =:billType ");
			}
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("ppoNo", ppoNo);
			lQuery.setString("Status", lStrStatus);
			lQuery.setString("LOCATION_CODE", lStrLocCd);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			lQuery.setLong("billNo", lLngBillNo);

			if (lStrBillType != null) {
				lQuery.setString("billType", lStrBillType);
			}
			lQuery.executeUpdate();
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
	}

	public void updateRltPensioncaseBillTokenForByBillNo(Long lLngBillNo, Long lLngTokenno, String lStrLocCd, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		try {
			StringBuffer lSBQuery = new StringBuffer();
			Session ghibSession = getSession();
			lSBQuery
					.append(" UPDATE RLT_PENSIONCASE_BILL SET TOKEN_NO = :tokenNo,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date WHERE BILL_NO =:lLngBillNo AND LOCATION_CODE = :LOCATION_CODE ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("lLngBillNo", lLngBillNo);
			lQuery.setLong("tokenNo", lLngTokenno);
			lQuery.setString("LOCATION_CODE", lStrLocCd);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			lQuery.executeUpdate();
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
	}

	public Long getBillNo(String lStrPPONo, String lStrLocCd) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List lLstResult = new ArrayList();
		Session ghibSession = getSession();
		Long lLngBillNo = 0L;
		try {
			lSBQuery.append(" SELECT RPB.billNo  " + "FROM RltPensioncaseBill RPB " + " WHERE RPB.ppoNo =:ppoNo  " + " AND RPB.billType =:BillType " + " AND RPB.status =:Status "
					+ " AND RPB.locationCode = :locCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("ppoNo", lStrPPONo);
			lQuery.setString("BillType", "PENSION");
			lQuery.setString("Status", "Y");
			lQuery.setString("locCode", lStrLocCd);

			lLstResult = lQuery.list();
			if (!lLstResult.isEmpty()) {
				lLngBillNo = (Long) lLstResult.get(0);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lLngBillNo;
	}
}
