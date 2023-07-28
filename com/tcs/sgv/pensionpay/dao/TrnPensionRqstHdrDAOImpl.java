package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.valueobject.MstEdp;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;


public class TrnPensionRqstHdrDAOImpl extends GenericDaoHibernateImpl<TrnPensionRqstHdr, Long> implements TrnPensionRqstHdrDAO {

	Log gLogger = LogFactory.getLog(getClass());

	public TrnPensionRqstHdrDAOImpl(Class<TrnPensionRqstHdr> type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}

	public int getLastPaidMonthForPnsnrCode(String pensionerCode) throws Exception {

		int lIntLastPaidMon = 0;
		StringBuffer query = new StringBuffer();
		try {
			Session ghibSession = getSession();
			query.append(" SELECT max(h.forMonth) from TrnPensionBillDtls d,TrnPensionBillHdr h " + " WHERE d.trnPensionBillHdrId = h.trnPensionBillHdrId and d.pensionerCode = :pensionerCode ");

			Query hqlQuery = ghibSession.createQuery(query.toString());

			hqlQuery.setString("pensionerCode", pensionerCode);

			List list = hqlQuery.list();
			if (!list.isEmpty()) {
				Iterator itr = list.iterator();
				while (itr.hasNext()) {
					lIntLastPaidMon = Integer.parseInt(itr.next().toString());
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is" + e, e);
			throw e;
		}
		return lIntLastPaidMon;
	}

	public Long getPensionRqstHdrIdByPnsnrCode(String pensionerCode, String locCode) throws Exception {

		Long pnsnRqstHdrId = null;
		StringBuffer query = new StringBuffer();
		try {
			Session ghibSession = getSession();
			query.append(" SELECT d.pensionRequestId from TrnPensionRqstHdr d " + // Updated
					// By
					// Jatin
					" WHERE d.pensionerCode = :pensionerCode and d.locationCode=:locCode ");

			Query hqlQuery = ghibSession.createQuery(query.toString());

			hqlQuery.setString("pensionerCode", pensionerCode);
			hqlQuery.setString("locCode", locCode);
			List list = hqlQuery.list();
			if (!list.isEmpty()) {
				Iterator itr = list.iterator();
				while (itr.hasNext()) {
					pnsnRqstHdrId = (Long) (itr.next());
				}

			}
		} catch (Exception e) {
			gLogger.error("Error is" + e, e);
			throw e;
		}
		return pnsnRqstHdrId;
	}

	public List getPksForTrnPensionRqstHdr(String lStrStatus, String lStrPpoNo, String lStrLocCode) throws Exception {

		StringBuffer query = new StringBuffer();
		List lLstPk = new ArrayList();
		try {
			Session ghibSession = getSession();
			query.append(" SELECT d.pensionRequestId,d.pensionerCode FROM TrnPensionRqstHdr d " + " WHERE d.ppoNo =:ppono AND caseStatus =:Status and locationCode=:lStrLocCode ");
			Query hqlQuery = ghibSession.createQuery(query.toString());
			hqlQuery.setString("ppono", lStrPpoNo);
			hqlQuery.setString("Status", lStrStatus);
			hqlQuery.setString("lStrLocCode", lStrLocCode);

			lLstPk = hqlQuery.list();
		} catch (Exception e) {
			gLogger.error("Error is" + e, e);
			throw e;
		}
		return lLstPk;
	}

	public String getPPONo(String lStrPensionerCode, String LocCode) throws Exception {

		String lStrPPONo = null;

		List<String> lLstResult = null;

		StringBuffer lSBQuery = new StringBuffer(100);

		Query lQuery = null;

		try {
			Session ghibSession = getSession();
			lSBQuery.append(" SELECT ppoNo FROM TrnPensionRqstHdr WHERE pensionerCode = :pensionerCode and locationCode=:LocCode ");

			lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("pensionerCode", lStrPensionerCode);
			lQuery.setString("LocCode", LocCode);

			lLstResult = lQuery.list();

			if (!lLstResult.isEmpty()) {
				lStrPPONo = lLstResult.get(0);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lStrPPONo;
	}

	public TrnPensionRqstHdr getTrnPensionRqstHdrVO(long lBDPenReqId, String lStrPenCode, String locCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer(400);

		TrnPensionRqstHdr lObjTrnPensionRqstHdr = new TrnPensionRqstHdr();

		List lLstReturn = null;

		Query lQuery = null;

		try {
			Session ghibSession = getSession();
			lSBQuery.append("FROM TrnPensionRqstHdr WHERE pensionRequestId = :pensionRequestId AND pensionerCode = :pensionerCode and locationCode=:locCode ");

			lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("pensionRequestId", lBDPenReqId);
			lQuery.setString("pensionerCode", lStrPenCode);
			lQuery.setString("locCode", locCode);

			lLstReturn = lQuery.list();

			if (!lLstReturn.isEmpty() && lLstReturn.get(0) != null) {
				lObjTrnPensionRqstHdr = (TrnPensionRqstHdr) (lLstReturn.get(0));
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lObjTrnPensionRqstHdr;
	}

	public String validatePPONumberForScheme(String PPONO, String locCode) throws Exception {

		String lStrScheme = "";
		String errorMsg = "";
		try {
			Session hibSession = getSession();
			String mySQL = "select prh.schemeType from TrnPensionRqstHdr prh where prh.ppoNo=:lPpoNo and prh.locationCode=:locCode ";
			Query lQuery = hibSession.createQuery(mySQL);
			lQuery.setString("lPpoNo", PPONO);
			lQuery.setString("locCode", locCode);

			List lLstVO = lQuery.list();
			if (!lLstVO.isEmpty()) {
				lStrScheme = lLstVO.get(0).toString();
			}
			if (!lStrScheme.equalsIgnoreCase("PSB")) {
				errorMsg = "This PPO Number is not associated with PSB Scheme";
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return errorMsg;
	}

	public String validatePPONumberForBank(String PPONO, String branchCode, String locCode) throws Exception {

		String lStrBranchCode = "";
		String errorMsg = "";
		try {
			Session hibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append("select mph.branchCode from TrnPensionRqstHdr prh, MstPensionerDtls mph where prh.ppoNo=:lPpoNo and prh.locationCode=:locCode "
					+ " and prh.pensionerCode = mph.pensionerCode and mph.active_flag = 'Y' ");
			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lPpoNo", PPONO);
			lQuery.setString("locCode", locCode);

			List lLstVO = lQuery.list();
			if (!lLstVO.isEmpty()) {
				lStrBranchCode = lLstVO.get(0).toString();
			}
			if (!lStrBranchCode.equalsIgnoreCase(branchCode)) {
				errorMsg = "This PPO Number is not associated with the selected bank";
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return errorMsg;
	}

	/*
	 * public Map getPaymentDetails(String PPONO,String locCode) throws
	 * Exception { Map resultValueMap = new HashMap(); List lLstVO; Query
	 * lQuery; ArrayList arrPaymentDetails = new ArrayList(); ArrayList
	 * arrMonthYear = new ArrayList(); try { Session hibSession = getSession();
	 * String mySql =
	 * "select prh.headCode, mph.firstName || ' ' || mph.middleName || ' ' || mph.lastName, prh.ppoNo,mh.description "
	 * +
	 * " from TrnPensionRqstHdr prh, MstPensionerHdr mph, MstPensionHeadcode mh"
	 * +
	 * " where prh.ppoNo=:lPPONO and prh.pensionerCode = mph.pensionerCode and mh.headCode = prh.headCode and prh.locationCode=:locCode"
	 * ; lQuery = hibSession.createQuery(mySql); lQuery.setString("lPPONO",
	 * PPONO); lQuery.setString("locCode", locCode); lLstVO = lQuery.list(); if
	 * (! lLstVO.isEmpty()) { for(Object row : lLstVO) { Object[] cols =
	 * (Object[])row; resultValueMap.put("headCode", cols[0]);
	 * resultValueMap.put("pensionerName", cols[1]);
	 * resultValueMap.put("headCodeDesc",cols[3]); } }
	 * 
	 * mySql =
	 * "select monthYear,actualPayment,bankPayment,lessPayment,excessPayment,differenceAmount,incomeTaxCutAmount,pensnCutAmount,specialCutAmount,dpAmount,tiAmount,medicalAllowenceAmount,arrearAmount,recoveryAmount,cvpMonthlyAmount,personalPensionAmount,irAmount,basicPension from TrnPensionPsbDtls where ppoNo = :lPPONO ORDER BY monthYear DESC"
	 * ; lQuery = hibSession.createQuery(mySql); lQuery.setString("lPPONO",
	 * PPONO); lLstVO = lQuery.list();
	 * 
	 * if(lLstVO != null && ! lLstVO.isEmpty()) { for(Object row : lLstVO) {
	 * Object cols[] = (Object[]) row; TrnPensionPsbDtls lObjTrnPensionPsbDtls =
	 * new TrnPensionPsbDtls();
	 * lObjTrnPensionPsbDtls.setMonthYear((Integer)cols[0]);
	 * arrMonthYear.add((Integer)cols[0]);
	 * lObjTrnPensionPsbDtls.setActualPayment((BigDecimal) cols[1]);
	 * lObjTrnPensionPsbDtls.setBankPayment((BigDecimal) cols[2]);
	 * lObjTrnPensionPsbDtls.setLessPayment((BigDecimal) cols[3]);
	 * lObjTrnPensionPsbDtls.setExcessPayment((BigDecimal) cols[4]);
	 * lObjTrnPensionPsbDtls.setDifferenceAmount((BigDecimal) cols[5]);
	 * lObjTrnPensionPsbDtls.setIncomeTaxCutAmount((BigDecimal) cols[6]);
	 * lObjTrnPensionPsbDtls.setPensnCutAmount((BigDecimal) cols[7]);
	 * lObjTrnPensionPsbDtls.setSpecialCutAmount((BigDecimal) cols[8]);
	 * lObjTrnPensionPsbDtls.setDpAmount((BigDecimal) cols[9]);
	 * lObjTrnPensionPsbDtls.setTiAmount((BigDecimal) cols[10]);
	 * lObjTrnPensionPsbDtls.setMedicalAllowenceAmount((BigDecimal) cols[11]);
	 * lObjTrnPensionPsbDtls.setArrearAmount((BigDecimal) cols[12]);
	 * lObjTrnPensionPsbDtls.setRecoveryAmount((BigDecimal) cols[13]);
	 * lObjTrnPensionPsbDtls.setCvpMonthlyAmount((BigDecimal) cols[14]);
	 * lObjTrnPensionPsbDtls.setPersonalPensionAmount((BigDecimal) cols[15]);
	 * lObjTrnPensionPsbDtls.setIrAmount((BigDecimal) cols[16]);
	 * lObjTrnPensionPsbDtls.setBasicPension((BigDecimal) cols[17]);
	 * arrPaymentDetails.add(lObjTrnPensionPsbDtls); } }
	 * 
	 * resultValueMap.put("paymentDetailsList", arrPaymentDetails);
	 * resultValueMap.put("MonthYearList", arrMonthYear); }catch(Exception e){
	 * gLogger.error(" Error is : " + e, e); throw(e); } return resultValueMap;
	 * }
	 */
	public Map getActualPaymentDetails(long lLngPensionReqId, String lStrPensionerCode) throws Exception {

		Map resultValueMap = new HashMap();
		List lLstVO;
		Query lQuery;
		ArrayList arrPaymentDetails = new ArrayList();

		try {
			Session hibSession = getSession();
			String mySql = "SELECT reducedPension,incomeTaxCutAmount,pensnCutAmount,dpAmount,tiAmount,medicalAllowenceAmount,arrearAmount,recoveryAmount,specialCutAmount,cvpMonthAmount,personalPensionAmount,irAmount,pensionAmount FROM TrnPensionBillDtls WHERE pensionerCode = :pensionerCode";
			lQuery = hibSession.createQuery(mySql);
			lQuery.setString("pensionerCode", lStrPensionerCode);
			lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				for (Object row : lLstVO) {
					Object cols[] = (Object[]) row;
					TrnPensionBillDtls lObjTrnPensionBillDtls = new TrnPensionBillDtls();

					lObjTrnPensionBillDtls.setReducedPension((BigDecimal) cols[0]);
					lObjTrnPensionBillDtls.setIncomeTaxCutAmount((BigDecimal) cols[1]);
					lObjTrnPensionBillDtls.setPensnCutAmount((BigDecimal) cols[2]);
					lObjTrnPensionBillDtls.setDpAmount((BigDecimal) cols[3]);
					lObjTrnPensionBillDtls.setTiAmount((BigDecimal) cols[4]);
					lObjTrnPensionBillDtls.setMedicalAllowenceAmount((BigDecimal) cols[5]);
					lObjTrnPensionBillDtls.setArrearAmount((BigDecimal) cols[6]);
					lObjTrnPensionBillDtls.setRecoveryAmount((BigDecimal) cols[7]);
					lObjTrnPensionBillDtls.setSpecialCutAmount((BigDecimal) cols[8]);
					lObjTrnPensionBillDtls.setCvpMonthAmount((BigDecimal) cols[9]);
					lObjTrnPensionBillDtls.setPersonalPensionAmount((BigDecimal) cols[10]);
					lObjTrnPensionBillDtls.setIrAmount((BigDecimal) cols[11]);
					lObjTrnPensionBillDtls.setPensionAmount((BigDecimal) cols[12]);

					arrPaymentDetails.add(lObjTrnPensionBillDtls);
				}
			}

			resultValueMap.put("ActualPaymentDetailsList", arrPaymentDetails);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return resultValueMap;
	}

	public List getPensionRqstHdrCodeId(String lStrPPONo, String lStrStatus, String locCode) throws Exception {

		List lLstResult = null;
		StringBuffer lSBQuery = new StringBuffer();

		try {
			Session ghibSession = getSession();
			lSBQuery.append(" select pensionRequestId,pensionerCode " + " from TrnPensionRqstHdr " + " where ppoNo = :PPONo and status = :CaseStatus and locationCode=:locCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("PPONo", lStrPPONo);
			lQuery.setString("CaseStatus", lStrStatus);
			lQuery.setString("locCode", locCode);

			lLstResult = lQuery.list();
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public TrnPensionRqstHdr getTrnPensionRqstHdrDtlsFromPPONo(String lStrPPONO, String lStrStatus, String lStrCaseStatus, String locCode) throws Exception {

		TrnPensionRqstHdr lobjPensionRqstHdr = null;
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append(" FROM TrnPensionRqstHdr A WHERE A.ppoNo = :lppoNo and A.locationCode=:locCode ");
			if (lStrStatus != null) {
				lSBQuery.append(" AND A.status = :lStatus ");
			}
			if (lStrCaseStatus != null) {
				lSBQuery.append(" AND A.caseStatus = :lCaseStatus ");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lppoNo", lStrPPONO);
			lQuery.setString("locCode", locCode);

			if (lStrStatus != null) {
				lQuery.setString("lStatus", lStrStatus);
			}
			if (lStrCaseStatus != null) {
				lQuery.setString("lCaseStatus", lStrCaseStatus);
			}
			List lLstVO = lQuery.list();
			if (!lLstVO.isEmpty()) {
				lobjPensionRqstHdr = (TrnPensionRqstHdr) lLstVO.get(0);
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return lobjPensionRqstHdr;
	}

	public TrnPensionRqstHdr getTrnPensionRqstHdrDtlsFromPnsnerCode(String lStrPnsrCode, String lStrStatus, String lStrCaseStatus, String locCode) throws Exception {

		TrnPensionRqstHdr lobjPensionRqstHdr = null;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM TrnPensionRqstHdr  WHERE pensionerCode = :lpnsrCode and locationCode=:locCode ");
			if (lStrStatus != null) {
				lSBQuery.append("AND status = :lStatus ");
			}
			lSBQuery.append(" AND caseStatus = :lCaseStatus ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lpnsrCode", lStrPnsrCode);
			if (lStrStatus != null) {
				lQuery.setString("lStatus", lStrStatus);
			}
			lQuery.setString("lCaseStatus", lStrCaseStatus);
			lQuery.setString("locCode", locCode);
			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				lobjPensionRqstHdr = (TrnPensionRqstHdr) lLstVO.get(0);
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}

		return lobjPensionRqstHdr;
	}

	public TrnPensionRqstHdr getTrnPensionRqstHdrVO(String lStrPPONo, String locCode) throws Exception {

		TrnPensionRqstHdr lObjTrnPensionRqstHdr = new TrnPensionRqstHdr();
		StringBuffer lSBQuery = new StringBuffer();

		Query lQuery = null;

		try {
			Session ghibSession = getSession();
			if (lStrPPONo != null) {
				lSBQuery.append(" FROM TrnPensionRqstHdr " + " WHERE ppoNo = :ppoNo and locationCode=:locCode ");

				lQuery = ghibSession.createQuery(lSBQuery.toString());

				lQuery.setString("ppoNo", lStrPPONo);
				lQuery.setString("locCode", lStrPPONo);

				List lLstVO = lQuery.list();

				if (!lLstVO.isEmpty()) {
					lObjTrnPensionRqstHdr = (TrnPensionRqstHdr) lLstVO.get(0);
				}
			}
		} catch (Exception e) {
			gLogger.error("Exception occurred in getTrnPensionRqstHdrVO() method of PensionRecoveryInfoDAOImpl Class " + e, e);
			throw (e);
		}
		return lObjTrnPensionRqstHdr;
	}

	public MstEdp getMstEdpVO(String lStrEdpCode, Long gLngLangId, Integer lFinYr) throws Exception {

		MstEdp lObjMstEdp = new MstEdp();
		StringBuffer lSBQuery = new StringBuffer();

		Query lQuery = null;

		try {
			Session ghibSession = getSession();
			if (lStrEdpCode != null) {
				lSBQuery.append(" FROM MstEdp " + " WHERE edpCode = :edpCode " + " AND langId = :langId AND receiptEdp = 'Y' " + " and activateFlag = '1' and finYearId = :FinYr ");
				lQuery = ghibSession.createQuery(lSBQuery.toString());

				lQuery.setString("edpCode", lStrEdpCode);
				lQuery.setLong("langId", gLngLangId);
				lQuery.setInteger("FinYr", lFinYr);

				List lLstVO = lQuery.list();

				if (!lLstVO.isEmpty()) {
					lObjMstEdp = (MstEdp) lLstVO.get(0);
				}
			}
		} catch (Exception e) {
			gLogger.error("Exception occurred in getMstEdpVO() method of PensionRecoveryInfoDAOImpl Class " + e, e);
			throw (e);
		}

		return lObjMstEdp;
	}

	public BigDecimal getHeadCode(String PPONO, String lStrstatus, String locCode) throws Exception {

		BigDecimal BDhdcode = null;
		try {
			Session hibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append("select prh.headCode from TrnPensionRqstHdr prh where prh.ppoNo=:lPpoNo AND prh.caseStatus = :lStrstatus " + " and prh.locationCode=:locCode ");

			Query lQuery = hibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPpoNo", PPONO);
			lQuery.setString("lStrstatus", lStrstatus);
			lQuery.setString("locCode", locCode);
			List lLstVO = lQuery.list();
			if (!lLstVO.isEmpty()) {
				BDhdcode = new BigDecimal(lLstVO.get(0).toString());
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return BDhdcode;
	}

	public List getDtlsForCVPDCRGBill(String status, String caseStatus, String RecvryFlag, String ppoNo, Long lLangID, String lStrLocCode) throws Exception {


		List lArrDtls = new ArrayList();

		try {
			Session hibSession = getSession();

			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" select hd.pnsnr_prefix, " + " hd.office_addr, od.dsgn_name, rq.pensioner_code, rq.scheme_type, rq.head_code, "
					+ " rq.type_flag, rq.dcrg_amount, rq.cvp_amount, sum(rd.amount) samount,hd.first_name,hd.middle_name,hd.last_name,rq.pension_request_id"
					+ ",rq.dcrg_withheld_amnt,rq.dcrg_amount_after_withheld ");
			if (!"".equals(RecvryFlag)) {
				if (RecvryFlag.equals("CVP")){
					lSBQuery.append(",rq.cvp_order_no,rq.cvp_date,rq.cvp_voucher_no,rq.cvp_voucher_date,rq.basic_pension_amount,rq.cvp_monthly_amount, ");
					lSBQuery.append("rq.reduced_pension,rq.cvp_authority,rq.cvp_start_date,rq.cvp_end_date ");
				}
				if (RecvryFlag.equals("DCRG"))
					lSBQuery.append(",rq.dcrg_order_no,rq.paid_date,rq.dcrg_voucher_no,rq.dcrg_voucher_date,rq.dcrg_paying_auth ");
				// order amount means rq.dcrg_amount
			}
			lSBQuery.append(" from trn_pension_rqst_hdr rq " + " join mst_pensioner_hdr hd on rq.pensioner_code = hd.pensioner_code  "
					+ " left join trn_pension_recovery_dtls rd on rq.pensioner_code = rd.pensioner_code and rd.bill_no is null and rd.recovery_from_flag = :lRecvryFlag "
					+ " left join org_designation_mst od on od.dsgn_code = hd.designation and od.Lang_Id = :lLangID" + " where rq.status = :lStatus and rq.case_status = :lCaseStatus "
					+ " and rq.ppo_no = :lPpoNo and rq.location_code=:lStrLocCode " + " group by hd.pnsnr_prefix, hd.first_name, hd.middle_name, hd.last_name, "
					+ " hd.office_addr, od.dsgn_name, rq.pensioner_code, rq.scheme_type, "
					+ " rq.head_code, rq.dcrg_amount, rq.cvp_amount, rq.type_flag,rq.pension_request_id,rq.dcrg_withheld_amnt,rq.dcrg_amount_after_withheld ");

			if (!"".equals(RecvryFlag)) {
				if (RecvryFlag.equals("CVP")){
					lSBQuery.append(",rq.cvp_order_no,rq.cvp_date,rq.cvp_voucher_no,rq.cvp_voucher_date,rq.basic_pension_amount,rq.cvp_monthly_amount, ");
					lSBQuery.append("rq.reduced_pension,rq.cvp_authority,rq.cvp_start_date,rq.cvp_end_date ");
				}
				if (RecvryFlag.equals("DCRG"))
					lSBQuery.append(",rq.dcrg_order_no,rq.paid_date,rq.dcrg_voucher_no,rq.dcrg_voucher_date,rq.dcrg_paying_auth ");
			}

			SQLQuery lQuery = hibSession.createSQLQuery(lSBQuery.toString());

			lQuery.addScalar("pnsnr_prefix", Hibernate.STRING);
			lQuery.addScalar("office_addr", Hibernate.STRING);

			lQuery.addScalar("dsgn_name", Hibernate.STRING);
			lQuery.addScalar("pensioner_code", Hibernate.STRING);

			lQuery.addScalar("scheme_type", Hibernate.STRING);
			lQuery.addScalar("head_code", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("type_flag", Hibernate.STRING);
			lQuery.addScalar("dcrg_amount", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("cvp_amount", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("samount", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("first_name", Hibernate.STRING);
			lQuery.addScalar("middle_name", Hibernate.STRING);
			lQuery.addScalar("last_name", Hibernate.STRING);
			lQuery.addScalar("pension_request_id", Hibernate.LONG);
			lQuery.addScalar("dcrg_withheld_amnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("dcrg_amount_after_withheld", Hibernate.BIG_DECIMAL);

			if (!"".equals(RecvryFlag)) {
				if (RecvryFlag.equals("CVP")) {
					lQuery.addScalar("cvp_order_no", Hibernate.STRING);
					lQuery.addScalar("cvp_date", Hibernate.DATE);
					lQuery.addScalar("cvp_voucher_no", Hibernate.STRING);
					lQuery.addScalar("cvp_voucher_date", Hibernate.DATE);
					lQuery.addScalar("basic_pension_amount", Hibernate.BIG_DECIMAL);
					lQuery.addScalar("cvp_monthly_amount", Hibernate.BIG_DECIMAL);
					lQuery.addScalar("reduced_pension", Hibernate.BIG_DECIMAL);
					lQuery.addScalar("cvp_authority", Hibernate.STRING);
					lQuery.addScalar("cvp_start_date", Hibernate.DATE);
					lQuery.addScalar("cvp_end_date", Hibernate.DATE);
				}
				if (RecvryFlag.equals("DCRG")) {
					lQuery.addScalar("dcrg_order_no", Hibernate.STRING);
					lQuery.addScalar("paid_date", Hibernate.DATE);
					lQuery.addScalar("dcrg_voucher_no", Hibernate.STRING);
					lQuery.addScalar("dcrg_voucher_date", Hibernate.DATE);
					lQuery.addScalar("dcrg_paying_auth", Hibernate.STRING);
				}
			}

			lQuery.setString("lStatus", status);
			lQuery.setString("lCaseStatus", caseStatus);
			lQuery.setString("lRecvryFlag", RecvryFlag);
			lQuery.setString("lPpoNo", ppoNo);
			lQuery.setLong("lLangID", lLangID);
			lQuery.setString("lStrLocCode", lStrLocCode);

			List listPnsnerCode = lQuery.list();

			if (listPnsnerCode != null && !listPnsnerCode.isEmpty()) {
				Iterator it = listPnsnerCode.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					lArrDtls.add(tuple[0]);
					lArrDtls.add(tuple[1]);
					lArrDtls.add(tuple[2]);
					lArrDtls.add(tuple[3]);
					lArrDtls.add(tuple[4]);
					lArrDtls.add(tuple[5]);
					lArrDtls.add(tuple[6]);
					lArrDtls.add(tuple[7]);
					lArrDtls.add(tuple[8]);
					lArrDtls.add(tuple[9]);

					lArrDtls.add(tuple[10]);
					lArrDtls.add(tuple[11]);
					lArrDtls.add(tuple[12]);
					lArrDtls.add(tuple[13]);
					lArrDtls.add(tuple[14]);
					lArrDtls.add(tuple[15]);

					if (!"".equals(RecvryFlag)) {
						if (RecvryFlag.equals("CVP")) {
							lArrDtls.add(tuple[16]);
							lArrDtls.add(tuple[17]);
							lArrDtls.add(tuple[18]);
							lArrDtls.add(tuple[19]);
							lArrDtls.add(tuple[20]);
							lArrDtls.add(tuple[21]);
							lArrDtls.add(tuple[22]);
							lArrDtls.add(tuple[23]);
							lArrDtls.add(tuple[24]);
							lArrDtls.add(tuple[25]);
						}
						if (RecvryFlag.equals("DCRG")) {
							lArrDtls.add(tuple[16]);
							lArrDtls.add(tuple[17]);
							lArrDtls.add(tuple[18]);
							lArrDtls.add(tuple[19]);
							lArrDtls.add(tuple[20]);
						}
					}

				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lArrDtls;
	
		
	}

	public Date getLastPaidDateByPensionerCodeAndCaseStatus(String pensionerCode, String lStrStatus) throws Exception {

		new ArrayList();
		Date lDateLstPaid = null;
		try {
			Session hibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append("select lastPaidDate from TrnPensionRqstHdr where pensionerCode=:pensionerCode and caseStatus =:caseStatus  ");

			Query lQuery = hibSession.createQuery(lSBQuery.toString());

			lQuery.setString("pensionerCode", pensionerCode);
			lQuery.setString("caseStatus", lStrStatus);
			List lLstRes = lQuery.list();
			if (lLstRes != null && !lLstRes.isEmpty()) {
				lDateLstPaid = (Date) lLstRes.get(0);
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lDateLstPaid;
	}

	public void updateTrnPensionRqstHdrSeenFlag(String lStrPensionerCode, String seenFlag, Long gLngPostId, Long gLngUserId, Date gDate, String lLocCode) throws Exception {

		try {
			Session hibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append(" update TrnPensionRqstHdr set seenFlag=:seenFlag,updatedUserId = :updated_user_id,updatedPostId = :updated_post_id, "
					+ " updatedDate = :updated_date where pensionerCode=:pensionerCode and caseStatus in ('NEW','REJECT','APPROVED') " + " and locationCode = :locCode ");
			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setString("pensionerCode", lStrPensionerCode);
			lQuery.setString("seenFlag", seenFlag);
			lQuery.setString("locCode", lLocCode);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	public void updateTrnPensionRqstHdrLastPaidDate(String strPensionerCode, Date lastPaidDate, Long gLngPostId, Long gLngUserId, Date gDate, String lStrLocCode) throws Exception {

		try {
			Session hibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append(" update TrnPensionRqstHdr set lastPaidDate=:lastPaidDate,updatedUserId = :updated_user_id,updatedPostId = :updated_post_id,updatedDate = :updated_date where pensionerCode=:pensionerCode "
					+ " and caseStatus in ('NEW','APPROVED','REJECT') and locationCode = :locCode ");
			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setString("pensionerCode", strPensionerCode);
			lQuery.setString("locCode", lStrLocCode);
			lQuery.setDate("lastPaidDate", lastPaidDate);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}

	}

	public int getFamilyMemberCountByPensinoerCode(String lStrPensionerCode) throws Exception {

		int Count = 0;
		try {
			Session hibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append("select count(*) from MstPensionerFamilyDtls where pensionerCode=:pensionerCode and percentage=100 and dateOfDeath is null ");

			Query lQuery = hibSession.createQuery(lSBQuery.toString());

			lQuery.setString("pensionerCode", lStrPensionerCode);
			List lLstRes = lQuery.list();
			if (lLstRes != null && !lLstRes.isEmpty()) {
				Count = Integer.parseInt(lLstRes.get(0).toString());
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return Count;
	}

	public List getInwdRegisterVitoDtls(String lStrPenCdArr, String lStrLocCode, Long lLngLangId) throws Exception {

		StringBuffer query = new StringBuffer();
		List lLstDtls = new ArrayList();
		try {
			Session ghibSession = getSession();
			query.append(" SELECT rq.ppo_no PPO, " + "        mh.first_name FANME, " + "        mh.middle_name MNAME, " + "		mh.last_name LNAME, " + "		rq.sanction_letter_no SNO, "
					+ "		rq.COMMENSION_DATE CMDT, " + "		CASE WHEN MH.DESIGNATION != 100984 " + "		        THEN ODM.dsgn_name " + "			 WHEN MH.DESIGNATION = 100984 " + "				THEN other_designation "
					+ "		END DEG, " + "		rq.PPO_DATE PPODT, " + "        rq.pensioner_code PENCODE " + " FROM trn_pension_rqst_hdr rq,mst_pensioner_hdr mh "
					+ " LEFT OUTER JOIN org_designation_mst odm ON odm.dsgn_code = mh.DESIGNATION AND ODM.LANG_ID = :LANG_ID " + " WHERE rq.pensioner_code = mh.pensioner_code "
					+ "	   AND rq.case_status = mh.case_status " + "	   AND rq.case_status = 'NEW' " + "	   AND rq.location_code = mh.location_code " + "	   AND rq.location_code = :lStrLocCode "
					+ "	   AND rq.pensioner_code IN (" + lStrPenCdArr + ") ");

			SQLQuery hqlQuery = ghibSession.createSQLQuery(query.toString());

			hqlQuery.addScalar("PPO", Hibernate.STRING);
			hqlQuery.addScalar("FANME", Hibernate.STRING);
			hqlQuery.addScalar("MNAME", Hibernate.STRING);
			hqlQuery.addScalar("LNAME", Hibernate.STRING);
			hqlQuery.addScalar("SNO", Hibernate.STRING);
			hqlQuery.addScalar("CMDT", Hibernate.DATE);
			hqlQuery.addScalar("DEG", Hibernate.STRING);
			hqlQuery.addScalar("PPODT", Hibernate.DATE);
			hqlQuery.addScalar("PENCODE", Hibernate.STRING);

			hqlQuery.setLong("LANG_ID", lLngLangId);
			hqlQuery.setString("lStrLocCode", lStrLocCode);

			lLstDtls = hqlQuery.list();
		} catch (Exception e) {
			gLogger.error("Error is" + e, e);
			throw e;
		}
		return lLstDtls;
	}

	// public String getPensionerName
	public String getPensionerName(String lStrPPONo) throws Exception {

		String lStrPenName = "";

		List<String> lLstResult = null;

		StringBuffer lSBQuery = new StringBuffer(100);

		Query lQuery = null;

		try {
			Session hiSession = getSession();
			lSBQuery.append(" SELECT MPH.firstName FROM MstPensionerHdr MPH,TrnPensionRqstHdr TPH WHERE TPH.pensionerCode = MPH.pensionerCode AND TPH.ppoNo=:ppoNo ");

			lQuery = hiSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("ppoNo", lStrPPONo);

			lLstResult = lQuery.list();

			if (!lLstResult.isEmpty()) {
				lStrPenName = lLstResult.get(0);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lStrPenName;
	}
}
