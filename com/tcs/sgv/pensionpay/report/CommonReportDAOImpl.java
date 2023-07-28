package com.tcs.sgv.pensionpay.report;

/**
 * For Common Dyanamic Reports.
 * 
 * @author Sagar
 */

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;

public class CommonReportDAOImpl extends
		GenericDaoHibernateImpl<TrnPensionRqstHdr, BigDecimal> implements
		CommonReportDAO {

	/* Global Variable for Logger Class */
	private Log logger = LogFactory.getLog(getClass());

	static ResourceBundle bundleApplicationDB = ResourceBundle
			.getBundle("ApplicationDB");

	/* Global Variable for Session Class */

	ResourceBundle bundleConst = ResourceBundle
			.getBundle("resources/pensionpay/PensionConstants");

	public CommonReportDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}

	/**
	 * @param class1
	 * @param sessionFactory
	 */

	/**
	 * Create For Getting a Header Detail of Pension Ledger Report
	 * 
	 * @author Sagar
	 */
	public List getLedgerRptHeaderDtl(String lStrPPONO, String lStrLocId,
			long lLangId, String lCaseStatus) throws Exception {

		List resList = null;

		try {
			Session ghibSession = getReadOnlySession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery
					.append(" select l.loc_name, l.loc_addr_1, l.loc_addr_2, h.first_name, h.middle_name, "
							+ " h.last_name , r.ppo_no  from mst_pensioner_hdr h, trn_pension_rqst_hdr r, cmn_location_mst l "
							+ " where r.pensioner_code = h.pensioner_code AND r.case_status = :lStatus AND "
							+ " h.case_status = :lStatus AND l.lang_id = :lLangId AND "
							+ " r.ppo_no = :lppoNo and r.location_code=l.location_code and l.location_code = :lStrLocId ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setParameter("lppoNo", lStrPPONO);
			lQuery.setParameter("lStrLocId", lStrLocId);
			lQuery.setParameter("lLangId", lLangId);

			if (lCaseStatus != null && lCaseStatus.length() > 0) {
				lQuery.setParameter("lStatus", lCaseStatus);
			} else {
				lQuery.setParameter("lStatus", "APPROVED");
			}

			List lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty()) {
				resList = lLstVO;
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return resList;
	}

	/**
	 * Create For Getting a Personal Detail of Pensioner for Ledger entitlement
	 * Report
	 * 
	 * @author Sagar
	 */
	public List getEntitlePnsnerDtls(String lStrPPONO, String lStrLocId,
			long lLangId, String lCaseStatus) throws Exception {

		List resList = null;

		/*
		 * SELECT h.FIRST_NAME,-- 0 h.MIDDLE_NAME,-- 1 h.LAST_NAME ,-- 2
		 * r.PPO_NO,-- 3 r.SCHEME_TYPE,-- 4 (SELECT MSTBANK4_.BANK_NAME FROM
		 * MST_BANK MSTBANK4_ WHERE MSTBANK4_.BANK_CODE = d.BANK_CODE AND
		 * MSTBANK4_.LANG_ID = 1),-- 5 (SELECT RLTBANKBRA5_.BRANCH_NAME FROM
		 * RLT_BANK_BRANCH RLTBANKBRA5_ WHERE RLTBANKBRA5_.BRANCH_CODE =
		 * d.BRANCH_CODE AND RLTBANKBRA5_.LOCATION_CODE = 10055),-- 6
		 * d.ACOUNT_NO,-- 7 r.prov_ppo_no,-- 8 h.date_of_birth,-- 9
		 * h.date_of_join,-- 10 (SELECT tr.authority FROM trn_pension_rqst_hdr
		 * tr WHERE r.prov_ppo_no IS NOT NULL AND tr.ppo_no = r.prov_ppo_no AND
		 * tr.case_status = 'CONVERTED' ), -- 11 h.date_of_retirement,-- 12
		 * r.authority,-- 13 r.commension_date,-- 14 h.pensner_addr,-- 15
		 * r.fp1_date,-- 16 r.fp2_date,-- 17 h.pay_scale,-- 18 h.height,-- 19
		 * r.pensionable_amount,-- 20 h.identity_mark,-- 21 h.date_of_death,--
		 * 22 r.form22_issued_date,-- 23 r.Form22_Issued_Auth,-- 24
		 * r.lpc_issued_date,-- 25 r.lpc_issued_auth,-- 26 r.head_code, -- 27
		 * r.remark -- 28 FROM MST_PENSIONER_HDR h, MST_PENSIONER_DTLS d,
		 * TRN_PENSION_RQST_HDR r, CMN_LOCATION_MST l WHERE h.PENSIONER_CODE =
		 * d.PENSIONER_CODE AND r.PENSIONER_CODE = h.PENSIONER_CODE AND
		 * r.CASE_STATUS = 'APPROVED' AND h.CASE_STATUS = 'APPROVED' AND
		 * d.CASE_STATUS = 'APPROVED' AND l.LANG_ID = 1 AND r.PPO_NO =
		 * 'DPPF/TEST/FP/2' AND l.LOCATION_CODE = 10055
		 */

		try {
			Session ghibSession = getReadOnlySession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery
					.append(" select h.first_name firstName, h.middle_name midName, h.last_name lstName , r.ppo_no ppoNo, r.scheme_type schemeTyp, "
							+ " (select mb.bank_name from mst_bank mb where mb.bank_code = d.bank_code and mb.lang_id = :lLangId) bnkName, "
							+ " (select rb.branch_name from rlt_bank_branch rb where rb.branch_code = d.branch_code and "
							+ " rb.location_code = :lStrLocId) brnchNme, "
							+ " d.acount_no accntNme, r.prov_ppo_no provPpo, h.date_of_birth DoB, h.date_of_join DoJ, "
							+ " (select tr.authority from trn_pension_rqst_hdr tr where r.prov_ppo_no is not null "
							+ " and tr.ppo_no = r.prov_ppo_no "
							+ " and tr.case_status = :lCnvrt and tr.location_code=:lStrLocId ) auth,"
							+ " h.date_of_retirement DoR, r.authority ath, r.commension_date commDate, h.pensner_addr pnsnAdr, r.fp1_date fp1Dt, r.fp2_date fp2Dt, "
							+ " h.pay_scale payScl, h.height hght, r.pensionable_amount penAmnt, h.identity_mark idMark, h.date_of_death DoD, r.form22_issued_date form22, "
							+ " r.form22_issued_auth form22Aut, r.lpc_issued_date lpc, r.lpc_issued_auth lpcAuth, r.head_code hdCd, r.remark rmrk, "
							+ " r.cvp_date cvppaid, r.dcrg_date dcrgpaid, r.cvp_restoration_date cvprsto, r.applied_date cvpapp "
							+ " from mst_pensioner_hdr h, mst_pensioner_dtls d, trn_pension_rqst_hdr r, cmn_location_mst l "
							+ " where h.pensioner_code = d.pensioner_code AND r.pensioner_code = h.pensioner_code AND "
							+ " r.case_status = :lStatus AND h.case_status = :lStatus AND d.case_status = :lStatus AND "
							+ " l.lang_id = :lLangId AND "
							+ " r.ppo_no = :lppoNo AND l.location_code = :lStrLocId and r.location_code=:lStrLocId ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setParameter("lppoNo", lStrPPONO);
			lQuery.setParameter("lStrLocId", lStrLocId);
			lQuery.setParameter("lLangId", lLangId);
			lQuery.setParameter("lCnvrt", "CONVERTED");

			lQuery.addScalar("firstName", Hibernate.STRING);
			lQuery.addScalar("midName", Hibernate.STRING);
			lQuery.addScalar("lstName", Hibernate.STRING);
			lQuery.addScalar("ppoNo", Hibernate.STRING);
			lQuery.addScalar("schemeTyp", Hibernate.STRING);
			lQuery.addScalar("bnkName", Hibernate.STRING);
			lQuery.addScalar("brnchNme", Hibernate.STRING);
			lQuery.addScalar("accntNme", Hibernate.LONG);
			lQuery.addScalar("provPpo", Hibernate.STRING);
			lQuery.addScalar("DoB", Hibernate.DATE);
			lQuery.addScalar("DoJ", Hibernate.DATE);
			lQuery.addScalar("auth", Hibernate.STRING);
			lQuery.addScalar("DoR", Hibernate.DATE);
			lQuery.addScalar("ath", Hibernate.STRING);
			lQuery.addScalar("commDate", Hibernate.DATE);
			lQuery.addScalar("pnsnAdr", Hibernate.STRING);
			lQuery.addScalar("fp1Dt", Hibernate.DATE);
			lQuery.addScalar("fp2Dt", Hibernate.DATE);
			lQuery.addScalar("payScl", Hibernate.STRING);
			lQuery.addScalar("hght", Hibernate.STRING);
			lQuery.addScalar("penAmnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("idMark", Hibernate.STRING);
			lQuery.addScalar("DoD", Hibernate.DATE);
			lQuery.addScalar("form22", Hibernate.DATE);
			lQuery.addScalar("form22Aut", Hibernate.STRING);
			lQuery.addScalar("lpc", Hibernate.DATE);
			lQuery.addScalar("lpcAuth", Hibernate.STRING);
			lQuery.addScalar("hdCd", Hibernate.LONG);
			lQuery.addScalar("rmrk", Hibernate.STRING);
			lQuery.addScalar("cvppaid", Hibernate.DATE);
			lQuery.addScalar("dcrgpaid", Hibernate.DATE);
			lQuery.addScalar("cvprsto", Hibernate.DATE);
			lQuery.addScalar("cvpapp", Hibernate.DATE);

			if (lCaseStatus != null && lCaseStatus.length() > 0) {
				lQuery.setParameter("lStatus", lCaseStatus);
			} else {
				lQuery.setParameter("lStatus", "APPROVED");
			}

			List lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty()) {
				resList = lLstVO;
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return resList;
	}

	/**
	 * Create For Getting a Details of Pension Ledger Report for Monthly
	 * 
	 * @author Sagar
	 */
	public List getLedgerRptDtl(String lStrPPONO, Date lFromDate, Date lToDate,
			String lStrLocId, long lLangId, String lCaseStatus)
			throws Exception {

		List resList = new ArrayList();

		try {
			Session ghibSession = getReadOnlySession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery
					.append(" SELECT H.BILL_TYPE BILLTYPE,H.FOR_MONTH FORMONTH,D.PENSION_AMOUNT PENSIONAMOUNT,D.PENSN_CUT_AMOUNT PENSNCUT,"
							+ " D.DP_AMOUNT DPAMOUNT,D.TI_AMOUNT TIAMOUNT,D.MEDICAL_ALLOWENCE_AMOUNT MAAMOUNT,"
							+ " D.CVP_MONTH_AMOUNT CVPAMOUNT,D.PERSONAL_PENSION_AMOUNT PPAMOUNT,D.IR_AMOUNT IRAMOUNT,D.ARREAR_AMOUNT ARRAMOUNT,"
							+ " D.TI_ARREAR_AMOUNT TIARRAMOUNT,D.SPECIAL_CUT_AMOUNT SPCUTAMOUNT,"
							+ " D.REDUCED_PENSION REDPENSION,D.INCOME_TAX_CUT_AMOUNT ITCUTAMOUNT,D.RECOVERY_AMOUNT RECAMOUNT,H.BILL_DATE BILLDATE,"
							+ " (SELECT MB.BANK_NAME FROM MST_BANK MB WHERE MB.BANK_CODE = H.BANK_CODE AND MB.LANG_ID = :lLangId) BANKNAME,"
							+ " (SELECT RB.BRANCH_NAME FROM RLT_BANK_BRANCH RB WHERE RB.BRANCH_CODE = H.BRANCH_CODE AND RB.LOCATION_CODE = :lStrLocId) BRANCHNAME ,"
							+ " D.TRN_PENSION_BILL_DTLS_ID DTLSID,D.PPO_NO PPONO,"
							+ " (SELECT CL.LOC_NAME FROM CMN_LOCATION_MST CL WHERE CL.LOCATION_CODE = H.LOCATION_CODE AND CL.LANG_ID = :lLangId) LOCNAME ,"
							+ " D.OTHER_BENEFITS OTHBENEFITS,D.FROM_DATE FRMDATE,D.TO_DATE TODATE,D.PENSIONER_NAME PNSNNAME,D.ADP_AMOUNT ADPAMOUNT,"
							+ " CASE WHEN br.curr_bill_status = -1 THEN 'Bill splitted and sent to book'	WHEN br.curr_bill_status = 5 THEN 'Bill Created'"
							+ " WHEN br.curr_bill_status = 10 THEN 'Bill with Counter' ELSE ms.status_desc END BILLSTATUS"
							+ " FROM TRN_PENSION_BILL_HDR  H"
							+ " LEFT JOIN trn_bill_register br ON br.bill_no = H.bill_no AND br.phy_bill = 2 AND br.location_code = H.LOCATION_CODE"
							+ " LEFT JOIN mst_status ms ON ms.status_code = br.curr_bill_status,"
							+ " TRN_PENSION_BILL_DTLS D, TRN_PENSION_RQST_HDR  R"
							+ " WHERE H.TRN_PENSION_BILL_HDR_ID = D.TRN_PENSION_BILL_HDR_ID AND H.REJECTED = 0 AND R.PPO_NO = :lppoNo AND"
							+ " D.PENSIONER_CODE = R.PENSIONER_CODE AND R.CASE_STATUS = :lcaseStatus "
							+ " AND (H.FOR_MONTH = D.PAY_FOR_MONTH OR D.PAY_FOR_MONTH IS NULL) AND R.LOCATION_CODE = :lStrLocId "
							+ " AND (1 NOT IN (SELECT 1 FROM TRN_BILL_MVMNT TRNBILLMVM6_ WHERE TRNBILLMVM6_.BILL_NO = H.BILL_NO AND TRNBILLMVM6_.MVMNT_STATUS = 80)) ");

			if (lFromDate != null) {
				lSBQuery.append(" AND H.BILL_DATE >=:fromDate");
			}
			if (lToDate != null) {
				lSBQuery.append(" AND H.BILL_DATE <:toDate");
			}

			lSBQuery.append(" ORDER BY H.FOR_MONTH, H.BILL_TYPE DESC");
			// lSBQuery.append(" ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setParameter("lppoNo", lStrPPONO);
			lQuery.setParameter("lStrLocId", lStrLocId);
			lQuery.setParameter("lLangId", lLangId);

			lQuery.addScalar("BILLTYPE", Hibernate.STRING);
			lQuery.addScalar("FORMONTH", Hibernate.INTEGER);
			lQuery.addScalar("PENSIONAMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("PENSNCUT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("DPAMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("TIAMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("MAAMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("CVPAMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("PPAMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("IRAMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("ARRAMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("TIARRAMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("SPCUTAMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("REDPENSION", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("ITCUTAMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("RECAMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("BILLDATE", Hibernate.TIMESTAMP);
			lQuery.addScalar("BANKNAME", Hibernate.STRING);
			lQuery.addScalar("BRANCHNAME", Hibernate.STRING);
			lQuery.addScalar("DTLSID", Hibernate.LONG);
			lQuery.addScalar("PPONO", Hibernate.STRING);
			lQuery.addScalar("LOCNAME", Hibernate.STRING);
			lQuery.addScalar("OTHBENEFITS", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("FRMDATE", Hibernate.TIMESTAMP);
			lQuery.addScalar("TODATE", Hibernate.TIMESTAMP);
			lQuery.addScalar("PNSNNAME", Hibernate.STRING);
			lQuery.addScalar("ADPAMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("BILLSTATUS", Hibernate.STRING);

			if (lCaseStatus != null && lCaseStatus.length() > 0) {
				lQuery.setParameter("lcaseStatus", lCaseStatus);
			} else {
				lQuery.setParameter("lcaseStatus", "APPROVED");
			}

			if (lFromDate != null) {
				lQuery.setDate("fromDate", lFromDate);
			}
			if (lToDate != null) {
				Date ltoDate = lToDate;
				long dateMillis = ltoDate.getTime();
				long dayInMillis = 1000 * 60 * 60 * 24;// subtract a day
				dateMillis = dateMillis + dayInMillis;
				ltoDate.setTime(dateMillis);
				lQuery.setDate("toDate", ltoDate);
			}

			List lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty()) {
				resList = lLstVO;
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return resList;
	}

	/*
	*//**
	 * Methods for Pension Payment Report
	 */
	/*
	 * public List getPensionPaymentData1(String ppoNo,String lStrLocCode)
	 * throws Exception {
	 * 
	 * Log gLogger = LogFactory.getLog(getClass());
	 * 
	 * ArrayList arrInner = null;
	 * 
	 * try { Session hiSession = getReadOnlySession(); StringBuilder lSBQuery =
	 * new StringBuilder();
	 * 
	 * lSBQuery.append(
	 * " select rq.PPO_NO ppoNo ,mh.FIRST_NAME fName,mh.MIDDLE_NAME mName,mh.LAST_NAME lName,mh.PENSNER_ADDR pnsnAddr,"
	 * + " (rq.BASIC_PENSION_AMOUNT " +
	 * " + rq.BASIC_PENSION_AMOUNT * rq.DP_PERCENT / :fpPercent) lDpAmnt ,(rq.BASIC_PENSION_AMOUNT + rq.BASIC_PENSION_AMOUNT "
	 * +
	 * " * rq.DP_PERCENT / :fpPercent - rq.CVP_MONTHLY_AMOUNT ) lNetAmnt ,mh.date_of_death DOD ,fd.name fpName,fd.date_of_death fpDOD"
	 * +
	 * " from TRN_PENSION_RQST_HDR rq, MST_PENSIONER_HDR mh  left join mst_pensioner_family_dtls fd "
	 * +
	 * " on fd.pensioner_code = mh.pensioner_code and fd.percentage =:fpPercent where "
	 * +
	 * " rq.PENSIONER_CODE = mh.PENSIONER_CODE and rq.CASE_STATUS = mh.CASE_STATUS and "
	 * +
	 * " mh.CASE_STATUS = 'APPROVED' and rq.PPO_NO = :ppoNo and rq.location_code = mh.location_code and rq.location_code = :location_code "
	 * );
	 * 
	 * 
	 * lSBQuery.append(
	 * " SELECT tr.ppoNo,mh.firstName,mh.middleName,mh.lastName,mh.pensnerAddr, "
	 * );lSBQuery.append(
	 * " (tr.basicPensionAmount + ( tr.basicPensionAmount * tr.dpPercent / 100 )) AS basic, "
	 * ); lSBQuery.append(" (tr.basicPensionAmount + ( tr.basicPensionAmount
	 * tr.dpPercent / 100 ) - tr.cvpMonthlyAmount) AS reducedPnsnh
	 * "); lSBQuery.append(" FROM TrnPensionRqstHdr tr, MstPensionerHdr mh
	 * "); lSBQuery.append(" WHERE tr.pensionerCode = mh.pensionerCode
	 * "); lSBQuery.append(" AND tr.caseStatus =
	 * mh.caseStatus"); lSBQuery.append(" AND mh.caseStatus = 'APPROVED'");
	 * lSBQuery.append(" AND tr.ppoNo = :ppoNo");
	 * 
	 * 
	 * SQLQuery hbQuery = hiSession.createSQLQuery(lSBQuery.toString());
	 * 
	 * hbQuery.setParameter("ppoNo", ppoNo); hbQuery.setParameter("fpPercent",
	 * 100); hbQuery.setParameter("location_code", lStrLocCode);
	 * 
	 * hbQuery.addScalar("ppoNo",Hibernate.STRING);
	 * hbQuery.addScalar("fName",Hibernate.STRING);
	 * hbQuery.addScalar("mName",Hibernate.STRING);
	 * hbQuery.addScalar("lName",Hibernate.STRING);
	 * hbQuery.addScalar("pnsnAddr",Hibernate.STRING);
	 * hbQuery.addScalar("lDpAmnt",Hibernate.BIG_DECIMAL);
	 * hbQuery.addScalar("lNetAmnt",Hibernate.BIG_DECIMAL);
	 * hbQuery.addScalar("DOD",Hibernate.DATE);
	 * hbQuery.addScalar("fpName",Hibernate.STRING);
	 * hbQuery.addScalar("fpDOD",Hibernate.DATE);
	 * 
	 * List resList = hbQuery.list();
	 * 
	 * if (resList != null && !resList.isEmpty()) {
	 * 
	 * Iterator it = resList.iterator();
	 * 
	 * while (it.hasNext()) { Object[] tuple = (Object[]) it.next();
	 * 
	 * arrInner = new ArrayList();
	 * 
	 * arrInner.add(tuple[0]); //PPO NO. arrInner.add(tuple[1]); //First Name
	 * arrInner.add(tuple[2]); //Middle Name arrInner.add(tuple[3]); //Last Name
	 * arrInner.add(tuple[4]); //Address arrInner.add(tuple[5]); //Basic
	 * arrInner.add(tuple[6]); //Reduced Pension arrInner.add(tuple[7]); // pnsn
	 * DOD arrInner.add(tuple[8]); // fp NAme arrInner.add(tuple[9]); // fpDOD }
	 * }
	 * 
	 * } catch(Exception e) { gLogger.error("Error is " + e, e); throw e; }
	 * return arrInner; }
	 * 
	 * public List getPensionPaymentData2(String ppoNo, Integer year,String
	 * locCode) throws Exception {
	 * 
	 * Log gLogger = LogFactory.getLog(getClass());
	 * 
	 * ArrayList arrList = null; ArrayList arrInner = null; ;
	 * 
	 * try { Session hiSession = getReadOnlySession(); StringBuilder lSBQuery =
	 * new StringBuilder();
	 * 
	 * lSBQuery.append(
	 * " SELECT dh.for_Month month,(dt.reduced_Pension- dt.arrear_Amount + dt.income_Tax_Cut_Amount+dt.recovery_Amount) paidAmount, "
	 * );
	 * lSBQuery.append(" dt.income_Tax_Cut_Amount ITCut,dt.arrear_Amount arrAmnt "
	 * );
	 * lSBQuery.append(" FROM Trn_Pension_Bill_Dtls dt, Trn_Pension_Bill_Hdr dh "
	 * );lSBQuery.append(
	 * " WHERE dt.trn_Pension_Bill_Hdr_Id = dh.trn_Pension_Bill_Hdr_Id");
	 * lSBQuery.append(" AND dh.bill_Type IN ('Monthly','PENSION') ");
	 * lSBQuery.append
	 * (" AND dt.pay_For_Month = dh.for_Month AND dt.ppo_No = :ppoNo");
	 * lSBQuery.append(
	 * " AND dh.for_Month >= :frmYr AND dh.for_Month <= :toYr AND dh.rejected = 0 and dh.location_code=:locCode "
	 * ); lSBQuery.append(" ORDER BY dh.for_Month");
	 * 
	 * SQLQuery lQuery = hiSession.createSQLQuery(lSBQuery.toString());
	 * 
	 * lQuery.setCacheable(true).setCacheRegion("pensionCache");
	 * 
	 * lQuery.addScalar("month", Hibernate.LONG); lQuery.addScalar("paidAmount",
	 * Hibernate.BIG_DECIMAL); lQuery.addScalar("ITCut", Hibernate.BIG_DECIMAL);
	 * lQuery.addScalar("arrAmnt", Hibernate.BIG_DECIMAL);
	 * 
	 * lQuery.setParameter("ppoNo", ppoNo); lQuery.setParameter("frmYr",
	 * Integer.valueOf((year) + "03")); lQuery.setParameter("toYr",
	 * Integer.valueOf((year + 1) + "02"));
	 * lQuery.setParameter("locCode",locCode);
	 * 
	 * List resList = lQuery.list(); if (resList != null && !resList.isEmpty())
	 * { arrList = new ArrayList(); Iterator it = resList.iterator(); while
	 * (it.hasNext()) { Object[] tuple = (Object[]) it.next(); arrInner = new
	 * ArrayList(); arrInner.add(tuple[0]); //For Month arrInner.add(tuple[1]);
	 * //Paid Amount arrInner.add(tuple[2]); //IT Cut Amount
	 * arrInner.add(tuple[3]); //Arrear Amount
	 * 
	 * arrList.add(arrInner); } } } catch(Exception e) {
	 * gLogger.error("Error is " + e, e); throw e; } return arrList; }
	 */
	/**
	 * Methods for Pension Payment Report
	 * 
	 * @param strLocCode
	 */
	public List getPensionPaymentData(String lStrPpoNo, Integer lIntYear,
			Long lLngAuditor, String lStrBankCode, String lStrBranchCode,
			String lStrLocCode) throws Exception {
		Log gLogger = LogFactory.getLog(getClass());

		ArrayList arrList = null;
		ArrayList arrInner = null;

		try {
			Session hiSession = getReadOnlySession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery
					.append("select rq.PPO_NO ppoNo ,mh.FIRST_NAME fName,mh.MIDDLE_NAME mName,mh.LAST_NAME lName, ");
			lSBQuery
					.append("mh.PENSNER_ADDR pnsnAddr, (rq.BASIC_PENSION_AMOUNT+ rq.BASIC_PENSION_AMOUNT * rq.DP_PERCENT / 100) lDpAmnt , ");
			lSBQuery
					.append("(rq.BASIC_PENSION_AMOUNT + rq.BASIC_PENSION_AMOUNT * rq.DP_PERCENT / 100 - rq.CVP_MONTHLY_AMOUNT ) lNetAmnt , ");
			lSBQuery
					.append("mh.date_of_death DOD , fd.name fpName, fd.date_of_death fpDOD, mh.case_status, dh.for_month month, "); // DATE_FORMAT(br.voucher_date,'%Y%m')
			lSBQuery
					.append("(dt.reduced_Pension- dt.arrear_Amount + dt.income_Tax_Cut_Amount+dt.recovery_Amount) paidAmount, ");
			lSBQuery
					.append("dt.income_Tax_Cut_Amount ITCut,dt.arrear_Amount arrAmnt,dh.bill_Type billType,br.subject_id,md.branch_code, rq.pensioner_code pCode ");

			lSBQuery.append("FROM Trn_Pension_Bill_Dtls dt,");
			lSBQuery
					.append("MST_PENSIONER_HDR mh LEFT JOIN mst_pensioner_family_dtls fd ON ");
			lSBQuery
					.append(" fd.pensioner_code = mh.pensioner_code  AND fd.percentage =:fpPercent, ");
			lSBQuery
					.append("Trn_Pension_Bill_Hdr dh LEFT JOIN trn_bill_register br ON br.bill_no = dh.bill_no, ");
			lSBQuery
					.append("TRN_PENSION_RQST_HDR rq JOIN mst_pensioner_dtls md ON ");
			lSBQuery
					.append("md.pensioner_code = rq.pensioner_code AND rq.CASE_STATUS = md.CASE_STATUS AND md.active_flag = 'Y' "
							+ " JOIN wf_job_mst wj ON wj.doc_id = 210003 AND wj.job_status = 'Active' AND wj.job_ref_id_num = rq.pension_request_id AND wj.lst_act_post_id_num = :Auditor ");

			lSBQuery
					.append("WHERE rq.PENSIONER_CODE = mh.PENSIONER_CODE AND rq.CASE_STATUS = mh.CASE_STATUS ");
			lSBQuery
					.append("AND mh.CASE_STATUS = 'APPROVED' AND rq.location_code = mh.location_code ");
			lSBQuery
					.append("and dt.trn_Pension_Bill_Hdr_Id = dh.trn_Pension_Bill_Hdr_Id AND dh.bill_Type IN ('Monthly','PENSION','CVP','DCRG','OMR') ");
			lSBQuery
					.append("AND dt.pay_For_Month = dh.for_Month AND dt.pensioner_code = rq.pensioner_code AND dh.rejected = 0 ");
			lSBQuery
					.append("AND rq.location_code =:locCode AND dh.for_month between :frmMonth and :toMonth ");

			if (lStrPpoNo != null && lStrPpoNo.length() != 0) {
				lSBQuery.append("AND rq.ppo_no=:ppoNo ");
			}
			if (lStrBankCode != null && lStrBankCode.length() != 0
					&& !lStrBankCode.equals("-1")) {
				lSBQuery.append("AND md.bank_code=:bankCode ");
			}
			if (lStrBranchCode != null && lStrBranchCode.length() != 0
					&& !lStrBranchCode.equals("-1")) {
				lSBQuery.append("AND md.branch_code=:branchCode ");
			}
			lSBQuery
					.append("order by md.branch_code,rq.head_code,rq.PPO_NO,dh.bill_type,dh.for_Month ");
			SQLQuery lQuery = hiSession.createSQLQuery(lSBQuery.toString());
			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.addScalar("ppoNo", Hibernate.STRING);
			lQuery.addScalar("fName", Hibernate.STRING);
			lQuery.addScalar("mName", Hibernate.STRING);
			lQuery.addScalar("lName", Hibernate.STRING);
			lQuery.addScalar("pnsnAddr", Hibernate.STRING);
			lQuery.addScalar("lDpAmnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lNetAmnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("DOD", Hibernate.DATE);
			lQuery.addScalar("fpName", Hibernate.STRING);
			lQuery.addScalar("fpDOD", Hibernate.DATE);

			lQuery.addScalar("month", Hibernate.LONG);
			lQuery.addScalar("paidAmount", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("ITCut", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("arrAmnt", Hibernate.BIG_DECIMAL);

			lQuery.addScalar("billType", Hibernate.STRING);
			lQuery.addScalar("subject_id", Hibernate.INTEGER);
			lQuery.addScalar("branch_code", Hibernate.STRING);
			lQuery.addScalar("pCode", Hibernate.STRING);

			if (lStrPpoNo != null && lStrPpoNo.length() != 0) {
				lQuery.setParameter("ppoNo", lStrPpoNo);
			}
			if (lStrBankCode != null && lStrBankCode.length() != 0
					&& !lStrBankCode.equals("-1")) {
				lQuery.setParameter("bankCode", lStrBankCode);
			}
			if (lStrBranchCode != null && lStrBranchCode.length() != 0
					&& !lStrBranchCode.equals("-1")) {
				lQuery.setParameter("branchCode", lStrBranchCode);
			}

			// Calendar cal = Calendar.getInstance();

			lQuery.setLong("Auditor", lLngAuditor);
			// cal.set(lIntYear, 3, 1);
			lQuery.setInteger("frmMonth", new Integer(lIntYear.toString()
					+ "03"));
			// cal.set(lIntYear+1, 2, 31);
			lIntYear += 1;
			lQuery.setInteger("toMonth",
					new Integer(lIntYear.toString() + "02"));
			lQuery.setParameter("locCode", lStrLocCode);
			lQuery.setParameter("fpPercent", 100);

			List resList = lQuery.list();

			if (resList != null && !resList.isEmpty()) {
				arrList = new ArrayList();
				Iterator it = resList.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					arrInner = new ArrayList();

					arrInner.add(tuple[0]); // PPO NO.
					arrInner.add(tuple[1]); // First Name
					arrInner.add(tuple[2]); // Middle Name
					arrInner.add(tuple[3]); // Last Name
					arrInner.add(tuple[4]); // Address
					arrInner.add(tuple[5]); // Basic
					arrInner.add(tuple[6]); // Reduced Pension
					arrInner.add(tuple[7]); // pnsn DOD
					arrInner.add(tuple[8]); // fp NAme
					arrInner.add(tuple[9]); // fpDOD

					arrInner.add(tuple[10]); // For Month
					arrInner.add(tuple[11]); // Paid Amount
					arrInner.add(tuple[12]); // IT Cut Amount
					arrInner.add(tuple[13]); // Arrear Amount
					arrInner.add(tuple[14]); // Bill Type
					arrInner.add(tuple[15]); // Subject Id
					arrInner.add(tuple[16]); // Branch Code
					arrInner.add(tuple[17]); // Pensioner Code
					arrList.add(arrInner);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is " + e, e);
			throw e;
		}
		return arrList;
	}

	public Map getPensionPaymentChallanData(String lStrPpoNo, Integer lIntYear,
			Long lLngAuditor, String lStrBankCode, String lStrBranchCode,
			String lStrLocCode) throws Exception {
		Log gLogger = LogFactory.getLog(getClass());
		java.util.Map<String, Double> lPCodeMap = new HashMap<String, Double>();

		try {
			Session hiSession = getReadOnlySession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery
					.append("SELECT rd.pensioner_code pCode, amount lamount FROM trn_pension_recovery_dtls rd WHERE recovery_from_flag = 'Challan' "
							+ " AND from_month BETWEEN :frmMonth and :toMonth And Exists ");

			lSBQuery.append("( Select 1 FROM Trn_Pension_Bill_Dtls dt,");
			lSBQuery.append("Trn_Pension_Bill_Hdr dh, ");
			lSBQuery
					.append("TRN_PENSION_RQST_HDR rq JOIN mst_pensioner_dtls md ON ");
			lSBQuery
					.append("md.pensioner_code = rq.pensioner_code AND rq.CASE_STATUS = md.CASE_STATUS "
							+ " JOIN wf_job_mst wj ON wj.doc_id = 210003 AND wj.job_status = 'Active' AND wj.job_ref_id_num = rq.pension_request_id AND wj.lst_act_post_id_num = :Auditor ");

			lSBQuery.append("WHERE rq.CASE_STATUS = 'APPROVED' ");
			lSBQuery
					.append("and dt.trn_Pension_Bill_Hdr_Id = dh.trn_Pension_Bill_Hdr_Id AND dh.bill_Type IN ('Monthly','PENSION','CVP','DCRG','OMR') ");
			lSBQuery
					.append("AND dt.pay_For_Month = dh.for_Month AND dt.pensioner_code=rq.pensioner_code AND dh.rejected = 0 ");
			lSBQuery
					.append("AND rq.location_code =:locCode AND dh.for_month between :frmMonth and :toMonth AND rd.pensioner_code = rq.pensioner_code ");

			/*
			 * if(lStrPpoNo!=null && lStrPpoNo.length()!=0){
			 * lSBQuery.append("AND rq.ppo_no=:ppoNo "); } if(lStrBankCode
			 * !=null && lStrBankCode.length()!=0 &&!lStrBankCode.equals("-1")
			 * ){ lSBQuery.append("AND md.bank_code=:bankCode "); }
			 * if(lStrBranchCode!=null && lStrBranchCode.length()!=0 &&
			 * !lStrBranchCode.equals("-1")){
			 * lSBQuery.append("AND md.branch_code=:branchCode "); }
			 */
			lSBQuery.append(" ) "); // GROUP BY rd.pensioner_code
			SQLQuery lQuery = hiSession.createSQLQuery(lSBQuery.toString());
			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.addScalar("pCode", Hibernate.STRING);
			lQuery.addScalar("lamount", Hibernate.BIG_DECIMAL);

			/*
			 * if(lStrPpoNo!=null && lStrPpoNo.length()!=0){
			 * lQuery.setParameter("ppoNo", lStrPpoNo); } if(lStrBankCode !=null
			 * && lStrBankCode.length()!=0 &&!lStrBankCode.equals("-1") ){
			 * lQuery.setParameter("bankCode",lStrBankCode); }
			 * if(lStrBranchCode!=null && lStrBranchCode.length()!=0 &&
			 * !lStrBranchCode.equals("-1")){ lQuery.setParameter("branchCode",
			 * lStrBranchCode); }
			 */

			// Calendar cal = Calendar.getInstance();
			lQuery.setLong("Auditor", lLngAuditor);
			// cal.set(lIntYear, 3, 1);
			lQuery.setInteger("frmMonth", new Integer(lIntYear.toString()
					+ "03"));
			// cal.set(lIntYear+1, 2, 31);
			lIntYear += 1;
			lQuery.setInteger("toMonth",
					new Integer(lIntYear.toString() + "04"));
			lQuery.setParameter("locCode", lStrLocCode);

			List resList = lQuery.list();

			if (resList != null && !resList.isEmpty()) {
				Iterator it = resList.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					lPCodeMap.put(tuple[0].toString(), new Double(tuple[1]
							.toString()));
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is " + e, e);
			throw e;
		}

		return lPCodeMap;
	}

	public List<TrnPensionRqstHdr> getPaidRqstList(String frmDate,
			String toDate, String ppoNo, String locCode) throws Exception {

		List<TrnPensionRqstHdr> lLstRqstHdr = new ArrayList<TrnPensionRqstHdr>();

		/*
		 * select from trn_pension_rqst_hdr r where r.case_status = 'APPROVED'
		 * and r.pensioner_code in ( select distinct d.pensioner_code from
		 * trn_pension_bill_dtls d, trn_pension_bill_hdr h where
		 * h.trn_pension_bill_hdr_id = d.trn_pension_bill_hdr_id and h.bill_type
		 * in ('CVP','DCRG','PENSION') and h.created_date >= '10/Sep/2008' and
		 * h.created_date <= '12/Sep/2008')
		 */
		try {
			Session ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			lSBQuery.append(" FROM TrnPensionRqstHdr A WHERE ");
			// lSBQuery.append("A.caseStatus = 'APPROVED' AND");
			lSBQuery
					.append(" A.pensionerCode in (select distinct d.pensionerCode ");
			lSBQuery.append(" from TrnPensionBillDtls d, TrnPensionBillHdr h ");
			lSBQuery
					.append(" where h.trnPensionBillHdrId = d.trnPensionBillHdrId ");
			lSBQuery.append(" and h.billType in ('CVP','DCRG','PENSION') ");
			lSBQuery
					.append(" and h.createdDate <=:toDate and h.locationCode=:locCode ");
			if (frmDate != null) {
				lSBQuery.append(" and h.createdDate >=:frmDate");
			}
			lSBQuery.append(") ");
			if (ppoNo != null && ppoNo.length() > 0) {
				lSBQuery.append(" and A.ppoNo = '" + ppoNo + "' ");
			}

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setDate("toDate", lObjDateFormat.parse(toDate));
			lQuery.setDate("frmDate", lObjDateFormat.parse(frmDate));
			lQuery.setString("locCode", locCode);

			List lLstVO = lQuery.list();
			if (lLstVO != null && !lLstVO.isEmpty()) {
				lLstRqstHdr = lLstVO;
			}
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
		return lLstRqstHdr;
	}

	/*
	 * public ArrayList getBilldtls(String penCode) throws Exception { ArrayList
	 * lArrBillDtls = new ArrayList(); try{ Session ghibSession = getSession();
	 * StringBuilder lSBQuery = new StringBuilder(); lSBQuery.append(" SELECT
	 * B.billType, A.reducedPension, B.forMonth FROM TrnPensionBillDtls
	 * A,TrnPensionBillHdr B "); lSBQuery.append(" WHERE A.trnPensionBillHdrId =
	 * B.trnPensionBillHdrId "); lSBQuery.append(" AND A.pensionerCode =
	 * :penCode AND (B.billType = 'PENSION' OR B.billType = 'DCRG' OR B.billType
	 * = 'CVP')) "); Query lQuery =
	 * ghibSession.createQuery(lSBQuery.toString());
	 * lQuery.setParameter("penCode", penCode); List lLstVO = lQuery.list();
	 * if(lLstVO != null && !lLstVO.isEmpty()) { Iterator it =
	 * lLstVO.iterator(); while(it.hasNext()) { Object[] tuple =
	 * (Object[])it.next(); lArrBillDtls.add(tuple[0]);
	 * lArrBillDtls.add(tuple[1]); lArrBillDtls.add(tuple[2]); } } }
	 * catch(Exception e) { logger.error("Error is."+e,e); throw(e); } return
	 * lArrBillDtls; }
	 */

	/*
	 * public Double getPensionCut(String penCode, Integer forMonth) throws
	 * Exception { Double lPensionCut = 0D; select sum(i.amount) from
	 * trn_pension_it_cut_dtls i where i.pensioner_code = 10001000421 and
	 * ((i.type_flag = 'PT' and i.from_month <= '200804' and i.to_month >=
	 * '200804') or i.type_flag = 'PP') try{ Session ghibSession = getSession();
	 * StringBuilder lSBQuery = new StringBuilder();
	 * lSBQuery.append(" select sum(i.amount) from TrnPensionItCutDtls i ");
	 * lSBQuery.append(" where i.pensionerCode = :penCode "); lSBQuery.append("
	 * and ((i.typeFlag = 'PT' and i.fromMonth <= :forMonth and i.toMonth >=
	 * :forMonth) or i.typeFlag = 'PP')
	 * "); Query lQuery = ghibSession.createQuery(lSBQuery.toString()); lQuery.setParameter("
	 * penCode", penCode); lQuery.setParameter("forMonth", forMonth); List
	 * lLstVO = lQuery.list(); if(lLstVO != null && !lLstVO.isEmpty()) {
	 * if(lLstVO.get(0) != null) { lPensionCut =
	 * Double.parseDouble(lLstVO.get(0).toString()); } } } catch(Exception e) {
	 * logger.error("Error is."+e,e); throw(e); } return lPensionCut; }
	 */

	public List getFamilyDtls(String penCode) throws Exception {

		List lLstFamDet = new ArrayList();

		/*
		 * select f.name from mst_pensioner_family_dtls f where f.pensioner_code
		 * = 10001000421 and f.percentage > 0
		 */

		try {
			Session ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select f.name from MstPensionerFamilyDtls f ");
			lSBQuery.append(" where f.pensionerCode = :penCode ");
			lSBQuery.append(" and f.percentage > 0 ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("penCode", penCode);
			List lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty()) {
				for (int i = 0; i < lLstVO.size(); i++) {
					lLstFamDet.add(lLstVO.get(i));
				}
			}
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw (e);
		}
		return lLstFamDet;
	}

	/**
	 * Method to find details for OverPaymentReport
	 */
	public ArrayList getOverPayBasicDtls(String ppoNo, String locCode)
			throws Exception {

		StringBuilder strQuery = new StringBuilder();
		ArrayList arrDtls = new ArrayList();
		/*
		 * select h.first_Name, h.middle_Name, h.last_Name, h.date_Of_Death,
		 * d.acount_No, b.branch_Name, m.bank_Name, b.branch_Address,
		 * r.pension_Request_Id, r.pensioner_Code, h.pensner_addr, fd.name,
		 * fd.date_of_death from Mst_Pensioner_Hdr h, Mst_Pensioner_Dtls d left
		 * join Rlt_Bank_Branch b on b.branch_Code = d.branch_Code left join
		 * Mst_Bank m on m.bank_Code = b.bank_Code and m.lang_id = 1 ,
		 * Trn_Pension_Rqst_Hdr r left join mst_pensioner_family_dtls fd on
		 * fd.pensioner_code=r.pensioner_code where r.pensioner_Code =
		 * h.pensioner_Code and r.pensioner_Code = d.pensioner_Code and r.ppo_No
		 * ='G/FPS/100506' and b.location_Code =10055 and r.case_Status =
		 * h.case_Status and h.case_Status = d.case_Status and r.case_Status =
		 * 'APPROVED'
		 */
		try {
			Session ghibSession = getReadOnlySession();
			strQuery
					.append(" select h.first_Name, h.middle_Name, h.last_Name, h.date_Of_Death, d.acount_No, ");
			strQuery
					.append(" b.branch_Name, m.bank_Name, b.branch_Address,r.pension_Request_Id,r.pensioner_Code, ");
			strQuery
					.append(" h.pensner_addr,fd.name,fd.date_of_death,r.last_paid_date ");
			strQuery.append(" from Mst_Pensioner_Hdr h, Mst_Pensioner_Dtls d ");
			strQuery
					.append(" left join Rlt_Bank_Branch b on b.branch_Code = d.branch_Code ");
			strQuery
					.append(" left join Mst_Bank m on m.bank_Code = b.bank_Code and m.lang_id = 1, ");
			strQuery
					.append(" Trn_Pension_Rqst_Hdr r left join mst_pensioner_family_dtls fd on fd.pensioner_code=r.pensioner_code ");
			strQuery
					.append(" where r.pensioner_Code = h.pensioner_Code and r.pensioner_Code = d.pensioner_Code ");
			strQuery
					.append(" and r.ppo_No =:ppoNo and b.location_Code =:locCode  and r.case_Status = h.case_Status and h.case_Status = d.case_Status and r.case_Status = 'APPROVED' ");

			Query hqlQuery = ghibSession.createSQLQuery(strQuery.toString());
			hqlQuery.setParameter("ppoNo", ppoNo.trim());
			hqlQuery.setParameter("locCode", locCode);

			List resultList = hqlQuery.list();
			if (resultList != null && !resultList.isEmpty()) {
				Object[] tuple = (Object[]) resultList.get(0);

				if (tuple[1] != null) {
					arrDtls.add(tuple[0] + " " + tuple[1] + " " + tuple[2]); // name
				} else {
					arrDtls.add(tuple[0] + " " + tuple[2]); // name
				}
				arrDtls.add(tuple[3]); // DoD1
				arrDtls.add(tuple[4]); // accNo2
				arrDtls.add(tuple[5]); // branch name3
				arrDtls.add(tuple[6]); // bank name4
				arrDtls.add(tuple[7]); // branch addr5
				arrDtls.add(tuple[8]); // PensionRqstId6
				arrDtls.add(tuple[9]); // PensionerCode7
				arrDtls.add(tuple[10]); // PensionerAddr8
				arrDtls.add(tuple[11]); // FamilyPensionerName9
				arrDtls.add(tuple[12]); // FamilyPensionerDOD10
				arrDtls.add(tuple[13]); // lastPaidDate11
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return arrDtls;
	}

	/**
	 * Method to get over payment details for a pensioner from bill dtls
	 * 
	 * @param ppoNo
	 * @param deathMonth
	 * @return
	 * @throws Exception
	 */
	public ArrayList getOverPayBillDtls(String ppoNo, String deathMonth)
			throws Exception {

		StringBuilder strQuery = new StringBuilder();
		ArrayList arrDtls = new ArrayList();
		/*
		 * select sum(bd.reduced_pension), max(bd.pay_for_month) from
		 * trn_pension_bill_dtls bd where bd.pay_for_month >= 200801 and
		 * bd.ppo_no = 'DPP/P/NG/003855'
		 */
		try {
			Session ghibSession = getReadOnlySession();
			strQuery
					.append(" select sum(bd.reduced_Pension) redPension, max(bd.pay_For_Month) payForMonth ");
			strQuery.append(" from Trn_Pension_Bill_Dtls bd ");
			strQuery.append(" where bd.pay_For_Month >= :deathMonth ");
			strQuery.append(" and bd.ppo_No = :ppoNo ");

			SQLQuery hqlQuery = ghibSession.createSQLQuery(strQuery.toString());

			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");

			hqlQuery.addScalar("redPension", Hibernate.BIG_DECIMAL);

			hqlQuery.addScalar("payForMonth", Hibernate.BIG_DECIMAL);

			hqlQuery.setParameter("ppoNo", ppoNo);
			hqlQuery.setParameter("deathMonth", Integer.parseInt(deathMonth));

			List resultList = hqlQuery.list();
			if (resultList != null && !resultList.isEmpty()) {
				Object[] tuple = (Object[]) resultList.get(0);

				arrDtls.add(tuple[0]); // amount paid
				arrDtls.add(tuple[1]); // last paid month
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return arrDtls;
	}

	public Double getOverPayRecoveryDtls(String ppoNo, String locCode)
			throws Exception {

		StringBuilder strQuery = new StringBuilder();
		Double recAmt = 0D;
		/*
		 * select sum(re.amount) from trn_pension_recovery_dtls re,
		 * trn_pension_rqst_hdr r where r.pensioner_code = re.pensioner_code and
		 * r.ppo_no = 'DPP/P/NG/003855' and re.recovery_type = 'OPR'
		 */
		try {
			Session ghibSession = getReadOnlySession();
			strQuery.append(" select sum(re.amount) recAmnt ");
			strQuery
					.append(" from Trn_Pension_Recovery_Dtls re, Trn_Pension_Rqst_Hdr r ");
			strQuery.append(" where r.pensioner_Code = re.pensioner_Code ");
			strQuery.append(" and r.ppo_No = :ppoNo ");
			strQuery
					.append(" and re.recovery_Type = 'OPR' and r.location_code=:locCode ");

			SQLQuery hqlQuery = ghibSession.createSQLQuery(strQuery.toString());
			hqlQuery.setParameter("ppoNo", ppoNo);
			hqlQuery.setString("locCode", locCode);

			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");

			hqlQuery.addScalar("recAmnt", Hibernate.BIG_DECIMAL);

			List resultList = hqlQuery.list();
			if (resultList != null && !resultList.isEmpty()) {
				Object tuple = resultList.get(0);

				if (tuple != null) {
					recAmt = Double.parseDouble(tuple.toString()); // recovered
					// amt
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return recAmt;
	}

	// Query for General.jsp
	public List getGeneralDeviationData(Integer lFromMonthYear,
			Integer lToMonthYear, String lStrScheme, String lStrHeadCode,
			List lPayType, String lStrLocCode) throws Exception {

		BigDecimal lBDReducedAmnt = new BigDecimal(0);
		List lLstTemp = new ArrayList();
		try {
			Session ghibSession = getReadOnlySession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery
					.append(" select sum(a.reduced_Pension) redPension,count(1) count ");
			lSBQuery
					.append(" FROM Trn_Pension_Bill_Dtls a, Trn_Pension_Bill_Hdr b");
			lSBQuery
					.append(" WHERE a.trn_Pension_Bill_Hdr_Id = b.trn_Pension_Bill_Hdr_Id AND b.for_Month BETWEEN "
							+ lFromMonthYear + " AND " + lToMonthYear);
			lSBQuery
					.append(" AND (a.pay_For_Month = b.for_Month) and (b.rejected != 1 or b.rejected is null) AND b.bill_Type != 'MR' and b.LOCATION_CODE  ='"
							+ lStrLocCode + "'");
			if (lStrScheme != null && lStrScheme.length() > 0) {
				lSBQuery.append(" AND b.scheme = '" + lStrScheme + "'");
			}
			lSBQuery
					.append(" AND b.bill_Type in ('Monthly','CVP','DCRG') and b.branch_code is not null ");

			if (lStrHeadCode != null && lStrHeadCode.length() > 0) {
				lSBQuery.append(" AND b.head_Code = " + lStrHeadCode);
			}
			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.addScalar("redPension", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("count", Hibernate.INTEGER);
			List lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty()) {

				Object[] tuple = (Object[]) lLstVO.get(0);
				if (tuple[0] != null) {
					lBDReducedAmnt = (BigDecimal) tuple[0];
				} else {
					lBDReducedAmnt = BigDecimal.ZERO;
				}
				lLstTemp.add(lBDReducedAmnt);
				if (tuple[1] != null) {
					lLstTemp.add(tuple[1].toString());
				} else {
					lLstTemp.add(0);
				}
			} else {
				lLstTemp.add(BigDecimal.ZERO);
				lLstTemp.add(0);
			}
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw (e);
		}
		return lLstTemp;
	}

	// to get bank name from bank code
	public String getBankNameFromCode(String lStrBankCode) throws Exception {

		String lStrBankName = null;

		try {
			Session ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT mb.bankName FROM MstBank mb ");
			lSBQuery.append(" WHERE mb.bankCode = :BankCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("BankCode", lStrBankCode);

			List lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty()) {
				if (lLstVO.get(0) != null) {
					lStrBankName = lLstVO.get(0).toString();
				}
			}
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw (e);
		}
		return lStrBankName;

	}

	public List getDeviationForBank(Integer lIntFromMonth, Integer lIntToMonth,
			String lStrBank, String lStrScheme, List lPayType,
			String lStrHeadCode, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List lLstRes = new ArrayList();
		try {
			lSBQuery
					.append(" SELECT SUM(A.REDUCED_PENSION) redPension,MB.BANK_NAME,MB.BANK_CODE,COUNT(DISTINCT a.ppo_no) Count"
							+ " FROM TRN_PENSION_BILL_DTLS A"
							+ " JOIN TRN_PENSION_BILL_HDR  B ON B.TRN_PENSION_BILL_HDR_ID = A.TRN_PENSION_BILL_HDR_ID "
							+ " AND (B.rejected != 1 OR B.rejected is null )"
							+ " LEFT OUTER JOIN MST_BANK MB ON MB.BANK_CODE = B.BANK_CODE AND MB.LANG_ID = 1 "
							+ " WHERE B.FOR_MONTH BETWEEN :lIntFromMonth AND :lIntToMonth AND b.bill_Type != 'MR' ");

			if (lStrBank != null) {
				lSBQuery.append(" AND  B.BANK_CODE = :lStrBank ");
			}
			if (lStrScheme != null && !"-1".equals(lStrScheme)
					&& lStrScheme.length() > 0) {
				lSBQuery.append(" AND B.Scheme = :lStrScheme ");
			}

			lSBQuery
					.append(" AND b.bill_Type in ('Monthly','CVP','DCRG') and b.branch_code is not null ");

			if (lStrHeadCode != null && !"-1".equals(lStrHeadCode)
					&& lStrHeadCode.length() > 0) {
				lSBQuery.append(" AND B.Head_Code = :lStrHeadCode ");
			}
			lSBQuery
					.append(" AND (B.FOR_MONTH = A.PAY_FOR_MONTH) and b.location_code = '"
							+ lStrLocCode
							+ "' "
							+ " GROUP BY B.BANK_CODE,MB.BANK_NAME,MB.BANK_CODE ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setLong("lIntFromMonth", lIntFromMonth);
			lQuery.setLong("lIntToMonth", lIntToMonth);
			if (lStrBank != null) {
				lQuery.setString("lStrBank", lStrBank);
			}
			if (lStrScheme != null && !"-1".equals(lStrScheme)
					&& lStrScheme.length() > 0) {
				lQuery.setString("lStrScheme", lStrScheme);
			}
			if (lStrHeadCode != null && !"-1".equals(lStrHeadCode)
					&& lStrHeadCode.length() > 0) {
				lQuery.setLong("lStrHeadCode", Long.valueOf(lStrHeadCode));
			}

			// lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.addScalar("redPension", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("BANK_NAME", Hibernate.STRING);
			lQuery.addScalar("BANK_CODE", Hibernate.STRING);
			lQuery.addScalar("Count", Hibernate.INTEGER);
			lLstRes = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstRes;
	}

	public List getDeviationForBranch(Integer lIntFromMonth,
			Integer lIntToMonth, String lStrBank, String lStrBranch,
			String locationCode) throws Exception {
		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List lLstRes = new ArrayList();
		try {
			lSBQuery
					.append(" SELECT  SUM(A.REDUCED_PENSION) redPension,MB.BANK_NAME,RBB.BRANCH_NAME,RBB.BRANCH_CODE,RBB.BANK_CODE,COUNT(DISTINCT a.ppo_no) Count "
							+ " FROM TRN_PENSION_BILL_DTLS A "
							+ " JOIN TRN_PENSION_BILL_HDR  B ON B.TRN_PENSION_BILL_HDR_ID = A.TRN_PENSION_BILL_HDR_ID "
							+ " AND (B.rejected != 1  OR B.rejected is null ) "
							+ " LEFT OUTER JOIN MST_BANK MB ON MB.BANK_CODE = B.BANK_CODE AND MB.LANG_ID = 1 "
							+ " JOIN RLT_BANK_BRANCH RBB ON (RBB.BRANCH_CODE) = B.Branch_Code "
							+ " WHERE B.FOR_MONTH BETWEEN :lIntFromMonth AND :lIntToMonth "
							+ " AND RBB.LOCATION_CODE = :locationCode ");

			if (lStrBank != null && lStrBank.length() > 0) {
				lSBQuery.append(" AND B.BANK_CODE = :lStrBank ");
			}
			if (lStrBranch != null && lStrBranch.length() > 0) {
				lSBQuery.append(" AND B.BRANCH_CODE = :lStrBranch ");
			}
			lSBQuery
					.append(" AND b.bill_Type in ('Monthly','CVP','DCRG') AND B.FOR_MONTH = A.PAY_FOR_MONTH AND B.LOCATION_CODE=:locationCode"
							+ " GROUP BY B.BANK_CODE, B.BRANCH_CODE, MB.BANK_NAME,RBB.BRANCH_NAME, "
							+ " RBB.BRANCH_CODE,RBB.BANK_CODE ORDER BY RBB.BANK_CODE");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setLong("lIntFromMonth", lIntFromMonth);
			lQuery.setLong("lIntToMonth", lIntToMonth);
			lQuery.setString("locationCode", locationCode);

			if (lStrBank != null && lStrBank.length() > 0) {
				lQuery.setString("lStrBank", lStrBank);
			}
			if (lStrBranch != null && lStrBranch.length() > 0) {
				lQuery.setLong("lStrBranch", Long.valueOf(lStrBranch));
			}

			// lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.addScalar("redPension", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("BANK_NAME", Hibernate.STRING);
			lQuery.addScalar("BRANCH_NAME", Hibernate.STRING);
			lQuery.addScalar("BRANCH_CODE", Hibernate.STRING);
			lQuery.addScalar("BANK_CODE", Hibernate.STRING);
			lQuery.addScalar("Count", Hibernate.INTEGER);
			lLstRes = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstRes;
	}

	public List getFPPaymentDetails(Integer lIntFromMonth, Integer lIntToMonth,
			List lPayType, String lStrHeadCode, String lStrScheme,
			String lStrLocCOde) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List lLstRes = new ArrayList();
		try {
			lSBQuery
					.append(" SELECT SUM(A.REDUCED_PENSION)redPension,b.bill_type ");
			lSBQuery
					.append(" FROM TRN_PENSION_BILL_DTLS A JOIN TRN_PENSION_BILL_HDR B ON "
							+ "B.TRN_PENSION_BILL_HDR_ID = A.TRN_PENSION_BILL_HDR_ID AND (B.rejected != 1 OR B.rejected is null)");
			lSBQuery.append(" WHERE B.FOR_MONTH BETWEEN " + lIntFromMonth
					+ " AND " + lIntToMonth);
			if (lPayType != null && !lPayType.isEmpty()) {
				lSBQuery.append(" AND B.BILL_TYPE in (:lStrPayType)");

			}
			if (lStrHeadCode != null) {
				lSBQuery.append(" AND B.HEAD_CODE = " + lStrHeadCode);
			}
			if (lStrScheme != null) {
				lSBQuery.append(" AND B.SCHEME = " + lStrScheme);
			}
			lSBQuery
					.append(" AND(A.PAY_FOR_MONTH  = B.FOR_MONTH ) AND "
							+ "(B.BRANCH_CODE IS NULL OR B.BRANCH_CODE = '-1' ) and B.LOCATION_CODE =:locCode ");
			lSBQuery.append(" GROUP BY B.BILL_TYPE ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			if (lPayType != null && !lPayType.isEmpty()) {
				lQuery.setParameterList("lStrPayType", lPayType);
			}
			lQuery.setString("locCode", lStrLocCOde);
			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.addScalar("redPension", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("bill_type", Hibernate.STRING);

			lLstRes = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstRes;
	}

	public List getPPONosByBranchCode(String lStrJoinString,
			Integer lArgsFromMonth1, Integer lArgsFromMonth2,
			Integer lArgsToMonth1, Integer lArgsToMonth2, String lArgsBank,
			String lArgsBranch, String lArgslocationCode) throws Exception {
		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List lLstRes = new ArrayList();
		List<MonthlyPensionBillVO> lLstobjTrnPensionBillDtls = new ArrayList<MonthlyPensionBillVO>();
		try {

			lSBQuery
					.append("SELECT OUTERQUERY.PPO_NO PPO_NO,OUTERQUERY.PENSIONER_NAME PENSIONER_NAME,OUTERQUERY.ACCOUNT_NO ACCOUNT_NO,OUTERQUERY.PENSION_AMOUNT PENSION_AMOUNT"
							+ ",OUTERQUERY.DP_AMOUNT DP_AMOUNT, OUTERQUERY.TI_AMOUNT TI_AMOUNT, OUTERQUERY.MEDICAL_ALLOWENCE_AMOUNT MEDICAL_ALLOWENCE_AMOUNT, OUTERQUERY.CVP_MONTH_AMOUNT CVP_MONTH_AMOUNT, "
							+ " OUTERQUERY.TI_ARREAR_AMOUNT TI_ARREAR_AMOUNT, OUTERQUERY.ARREAR_AMOUNT ARREAR_AMOUNT, OUTERQUERY.INCOME_TAX_CUT_AMOUNT INCOME_TAX_CUT_AMOUNT, "
							+ " OUTERQUERY.SPECIAL_CUT_AMOUNT SPECIAL_CUT_AMOUNT, OUTERQUERY.OTHER_BENEFITS OTHER_BENEFITS, OUTERQUERY.OMR OMR, OUTERQUERY.PENSN_CUT_AMOUNT PENSN_CUT_AMOUNT, "
							+ " OUTERQUERY.RECOVERY_AMOUNT RECOVERY_AMOUNT, OUTERQUERY.REDUCED_PENSION REDUCED_PENSION, OUTERQUERY.PERSONAL_PENSION_AMOUNT PERSONAL_PENSION_AMOUNT, OUTERQUERY.IR_AMOUNT IR_AMOUNT, "
							+ " OUTERQUERY.MO_COMMISSION MO_COMMISSION, OUTERQUERY.PAY_FOR_MONTH PAY_FOR_MONTH "
							+ " FROM (SELECT temp1.PPO_NO PPO_NO , temp1.PENSIONER_NAME PENSIONER_NAME, temp1.ACCOUNT_NO ACCOUNT_NO, temp1.PENSION_AMOUNT PENSION_AMOUNT"
							+ ", temp1.DP_AMOUNT DP_AMOUNT, temp1.TI_AMOUNT TI_AMOUNT, temp1.MEDICAL_ALLOWENCE_AMOUNT MEDICAL_ALLOWENCE_AMOUNT, temp1.CVP_MONTH_AMOUNT CVP_MONTH_AMOUNT, "
							+ "temp1.TI_ARREAR_AMOUNT TI_ARREAR_AMOUNT, temp1.ARREAR_AMOUNT ARREAR_AMOUNT, temp1.INCOME_TAX_CUT_AMOUNT INCOME_TAX_CUT_AMOUNT, "
							+ "temp1.SPECIAL_CUT_AMOUNT SPECIAL_CUT_AMOUNT, temp1.OTHER_BENEFITS OTHER_BENEFITS, temp1.OMR OMR, temp1.PENSN_CUT_AMOUNT PENSN_CUT_AMOUNT, "
							+ "temp1.RECOVERY_AMOUNT RECOVERY_AMOUNT, temp1.REDUCED_PENSION REDUCED_PENSION, temp1.PERSONAL_PENSION_AMOUNT PERSONAL_PENSION_AMOUNT, temp1.IR_AMOUNT IR_AMOUNT, "
							+ "temp1.MO_COMMISSION MO_COMMISSION, temp1.PAY_FOR_MONTH PAY_FOR_MONTH"
							+ " FROM ( (SELECT A.* ,B.BILL_TYPE "
							+ " FROM TRN_PENSION_BILL_DTLS A JOIN TRN_PENSION_BILL_HDR B ON B.TRN_PENSION_BILL_HDR_ID = A.TRN_PENSION_BILL_HDR_ID "
							+ " AND (B.rejected != 1 OR B.rejected is null)"
							+ " LEFT OUTER JOIN MST_BANK MB ON MB.BANK_CODE = B.BANK_CODE AND MB.LANG_ID = 1 "
							+ " JOIN RLT_BANK_BRANCH RBB ON RBB.BRANCH_CODE = B.BRANCH_CODE"
							+ " WHERE B.FOR_MONTH =:lArgsFromMonth1 AND RBB.LOCATION_CODE = :lArgslocationCode ");

			if (lArgsBank != null && !"-1".equals(lArgsBank)
					&& lArgsBank.length() > 0) {
				lSBQuery.append(" AND B.BANK_CODE = " + lArgsBank);
			}
			if (lArgsBranch != null && !"-1".equals(lArgsBranch)
					&& lArgsBranch.length() > 0) {
				lSBQuery.append(" AND B.BRANCH_CODE = " + lArgsBranch);
			}

			lSBQuery
					.append(" AND B.FOR_MONTH =  A.PAY_FOR_MONTH AND b.bill_Type in ('Monthly','CVP','DCRG')) temp1) LEFT JOIN "
							+ " ( (SELECT A.* ,B.BILL_TYPE "
							+ " FROM TRN_PENSION_BILL_DTLS A JOIN TRN_PENSION_BILL_HDR B ON B.TRN_PENSION_BILL_HDR_ID = A.TRN_PENSION_BILL_HDR_ID "
							+ " AND (B.rejected != 1 OR B.rejected is null) "
							+ " LEFT OUTER JOIN MST_BANK MB ON MB.BANK_CODE = B.BANK_CODE AND MB.LANG_ID = 1 "
							+ " JOIN RLT_BANK_BRANCH RBB ON RBB.BRANCH_CODE = B.BRANCH_CODE "
							+ " WHERE B.FOR_MONTH =:lArgsToMonth1 AND RBB.LOCATION_CODE = :lArgslocationCode ");

			if (lArgsBank != null && !"-1".equals(lArgsBank)
					&& lArgsBank.length() > 0) {
				lSBQuery.append(" AND B.BANK_CODE = :lArgsBank ");
			}
			if (lArgsBranch != null && !"-1".equals(lArgsBranch)
					&& lArgsBranch.length() > 0) {
				lSBQuery.append(" AND B.BRANCH_CODE = :lArgsBranch ");
			}
			lSBQuery
					.append(" AND B.FOR_MONTH = A.PAY_FOR_MONTH  AND b.bill_Type in ('Monthly','CVP','DCRG')) tmp2) "
							+ " ON temp1.PPO_NO = tmp2.PPO_NO AND temp1.bill_Type = tmp2.bill_Type ");

			lSBQuery.append(" UNION ");

			lSBQuery
					.append(" SELECT tmp2.PPO_NO PPO_NO , tmp2.PENSIONER_NAME PENSIONER_NAME, tmp2.ACCOUNT_NO ACCOUNT_NO, tmp2.PENSION_AMOUNT PENSION_AMOUNT"
							+ ", tmp2.DP_AMOUNT DP_AMOUNT, tmp2.TI_AMOUNT TI_AMOUNT, tmp2.MEDICAL_ALLOWENCE_AMOUNT MEDICAL_ALLOWENCE_AMOUNT, tmp2.CVP_MONTH_AMOUNT CVP_MONTH_AMOUNT, "
							+ "tmp2.TI_ARREAR_AMOUNT TI_ARREAR_AMOUNT, tmp2.ARREAR_AMOUNT ARREAR_AMOUNT, tmp2.INCOME_TAX_CUT_AMOUNT INCOME_TAX_CUT_AMOUNT, "
							+ "tmp2.SPECIAL_CUT_AMOUNT SPECIAL_CUT_AMOUNT, tmp2.OTHER_BENEFITS OTHER_BENEFITS, tmp2.OMR OMR, tmp2.PENSN_CUT_AMOUNT PENSN_CUT_AMOUNT, "
							+ "tmp2.RECOVERY_AMOUNT RECOVERY_AMOUNT, tmp2.REDUCED_PENSION REDUCED_PENSION, tmp2.PERSONAL_PENSION_AMOUNT PERSONAL_PENSION_AMOUNT, tmp2.IR_AMOUNT IR_AMOUNT, "
							+ "tmp2.MO_COMMISSION MO_COMMISSION, tmp2.PAY_FOR_MONTH PAY_FOR_MONTH"
							+ " FROM ( (SELECT A.* ,B.BILL_TYPE "
							+ " FROM TRN_PENSION_BILL_DTLS A JOIN TRN_PENSION_BILL_HDR B ON B.TRN_PENSION_BILL_HDR_ID = A.TRN_PENSION_BILL_HDR_ID "
							+ " AND (B.rejected != 1 OR B.rejected is null)"
							+ " LEFT OUTER JOIN MST_BANK MB ON MB.BANK_CODE = B.BANK_CODE AND MB.LANG_ID = 1 "
							+ " JOIN RLT_BANK_BRANCH RBB ON RBB.BRANCH_CODE = B.BRANCH_CODE"
							+ " WHERE B.FOR_MONTH =:lArgsFromMonth1 AND RBB.LOCATION_CODE = :lArgslocationCode ");

			if (lArgsBank != null && !"-1".equals(lArgsBank)
					&& lArgsBank.length() > 0) {
				lSBQuery.append(" AND B.BANK_CODE = " + lArgsBank);
			}
			if (lArgsBranch != null && !"-1".equals(lArgsBranch)
					&& lArgsBranch.length() > 0) {
				lSBQuery.append(" AND B.BRANCH_CODE = " + lArgsBranch);
			}

			lSBQuery
					.append(" AND B.FOR_MONTH =  A.PAY_FOR_MONTH AND b.bill_Type in ('Monthly','CVP','DCRG')) temp1) RIGHT JOIN "
							+ " ( (SELECT A.* ,B.BILL_TYPE "
							+ " FROM TRN_PENSION_BILL_DTLS A JOIN TRN_PENSION_BILL_HDR B ON B.TRN_PENSION_BILL_HDR_ID = A.TRN_PENSION_BILL_HDR_ID "
							+ " AND (B.rejected != 1 OR B.rejected is null) "
							+ " LEFT OUTER JOIN MST_BANK MB ON MB.BANK_CODE = B.BANK_CODE AND MB.LANG_ID = 1 "
							+ " JOIN RLT_BANK_BRANCH RBB ON RBB.BRANCH_CODE = B.BRANCH_CODE "
							+ " WHERE B.FOR_MONTH =:lArgsToMonth1 AND RBB.LOCATION_CODE = :lArgslocationCode ");

			if (lArgsBank != null && !"-1".equals(lArgsBank)
					&& lArgsBank.length() > 0) {
				lSBQuery.append(" AND B.BANK_CODE = :lArgsBank ");
			}
			if (lArgsBranch != null && !"-1".equals(lArgsBranch)
					&& lArgsBranch.length() > 0) {
				lSBQuery.append(" AND B.BRANCH_CODE = :lArgsBranch ");
			}
			lSBQuery
					.append(" AND B.FOR_MONTH = A.PAY_FOR_MONTH  AND b.bill_Type in ('Monthly','CVP','DCRG')) tmp2) "
							+ " ON temp1.PPO_NO = tmp2.PPO_NO AND temp1.bill_Type = tmp2.bill_Type) OUTERQUERY ORDER BY OUTERQUERY.PPO_NO,OUTERQUERY.PAY_FOR_MONTH ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setLong("lArgsFromMonth1", lArgsFromMonth1);
			lQuery.setLong("lArgsToMonth1", lArgsToMonth1);
			lQuery.setString("lArgslocationCode", lArgslocationCode);
			if (lArgsBank != null && !"-1".equals(lArgsBank)
					&& lArgsBank.length() > 0) {
				lQuery.setString("lArgsBank", lArgsBank);
			}
			if (lArgsBranch != null && !"-1".equals(lArgsBranch)
					&& lArgsBranch.length() > 0) {
				lQuery.setLong("lArgsBranch", Long.valueOf(lArgsBranch));
			}
			lQuery.addScalar("PPO_NO", Hibernate.STRING);
			lQuery.addScalar("PENSIONER_NAME", Hibernate.STRING);
			lQuery.addScalar("ACCOUNT_NO", Hibernate.STRING);
			lQuery.addScalar("PENSION_AMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("DP_AMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("TI_AMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("MEDICAL_ALLOWENCE_AMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("CVP_MONTH_AMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("TI_ARREAR_AMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("ARREAR_AMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("INCOME_TAX_CUT_AMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("SPECIAL_CUT_AMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("OTHER_BENEFITS", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("OMR", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("PENSN_CUT_AMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("RECOVERY_AMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("REDUCED_PENSION", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("PERSONAL_PENSION_AMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("IR_AMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("MO_COMMISSION", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("PAY_FOR_MONTH", Hibernate.STRING);
			lLstRes = lQuery.list();
			MonthlyPensionBillVO lObjMonthlyVo = null;
			BigDecimal lBDTemp = BigDecimal.ZERO;

			if (lLstRes != null && !lLstRes.isEmpty()) {
				Iterator it = lLstRes.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					lObjMonthlyVo = new MonthlyPensionBillVO();
					lBDTemp = BigDecimal.ZERO;
					if (tuple[0] != null) {
						lObjMonthlyVo.setPpoNo((String) tuple[0]);
					}
					if (tuple[1] != null) {
						lObjMonthlyVo.setPensionerName((String) tuple[1]);
					}
					if (tuple[2] != null) {
						lObjMonthlyVo.setAccountNo((String) tuple[2]);
					}
					if (tuple[3] != null) {
						lBDTemp = (BigDecimal) tuple[3];

					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setBasicPensionAmount(lBDTemp);

					if (tuple[4] != null) {
						lBDTemp = (BigDecimal) tuple[4];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setDpPercentAmount(lBDTemp);

					if (tuple[5] != null) {
						lBDTemp = (BigDecimal) tuple[5];

					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setTiPercentAmount(lBDTemp);

					if (tuple[6] != null) {
						lBDTemp = (BigDecimal) tuple[6];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setMedicalAllowenceAmount(lBDTemp);

					if (tuple[7] != null) {
						lBDTemp = (BigDecimal) tuple[7];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setCvpMonthlyAmount(lBDTemp);

					if (tuple[8] != null) {
						lBDTemp = (BigDecimal) tuple[8];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setTIArrearsAmount(lBDTemp);

					if (tuple[9] != null) {
						lBDTemp = (BigDecimal) tuple[9];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setOtherArrearsAmount(lBDTemp);

					if (tuple[10] != null) {
						lBDTemp = (BigDecimal) tuple[10];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setItCutAmount(lBDTemp);

					if (tuple[11] != null) {
						lBDTemp = (BigDecimal) tuple[11];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setSpecialCutAmount(lBDTemp);

					if (tuple[12] != null) {
						lBDTemp = (BigDecimal) tuple[12];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setOtherBenefit(lBDTemp);

					if (tuple[13] != null) {
						lBDTemp = (BigDecimal) tuple[13];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setOMR(lBDTemp);

					if (tuple[14] != null) {
						lBDTemp = (BigDecimal) tuple[14];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setPensionCutAmount(lBDTemp);

					if (tuple[15] != null) {
						lBDTemp = (BigDecimal) tuple[15];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setRecoveryAmount(lBDTemp);

					if (tuple[16] != null) {
						lBDTemp = (BigDecimal) tuple[16];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setNetPensionAmount(lBDTemp);

					if (tuple[17] != null) {
						lBDTemp = (BigDecimal) tuple[17];

					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setPersonalPension(lBDTemp);

					if (tuple[18] != null) {
						lBDTemp = (BigDecimal) tuple[18];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setIr(lBDTemp);
					if (tuple[19] != null) {
						lBDTemp = (BigDecimal) tuple[19];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setMOComm(lBDTemp);
					if (tuple[20] != null) {
						lBDTemp = new BigDecimal(tuple[20].toString());
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setForMonth(lBDTemp.intValue());
					lLstobjTrnPensionBillDtls.add(lObjMonthlyVo);
				}
			}
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstobjTrnPensionBillDtls;
	}

	public ArrayList<MonthlyPensionBillVO> getTrnPensionBillDtls(
			List lLstPpoNo, Integer lIntFromMonth, Integer lIntToMonth,
			String lArgslocationCode, String lArgsBank, String lArgsBranch)
			throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		ArrayList<MonthlyPensionBillVO> lLstobjTrnPensionBillDtls = new ArrayList<MonthlyPensionBillVO>();
		MonthlyPensionBillVO lObjMonthlyVo = null;
		try {
			lSBQuery.append(" SELECT A.PPO_NO,");
			lSBQuery.append(" A.PENSIONER_NAME,");
			lSBQuery.append(" A.ACCOUNT_NO,");
			lSBQuery.append(" A.PENSION_AMOUNT,");
			lSBQuery.append(" A.DP_AMOUNT,");
			lSBQuery.append(" A.TI_AMOUNT,");
			lSBQuery.append(" A.MEDICAL_ALLOWENCE_AMOUNT,");
			lSBQuery.append(" A.CVP_MONTH_AMOUNT,");
			lSBQuery.append(" A.TI_ARREAR_AMOUNT,");
			lSBQuery.append(" A.ARREAR_AMOUNT,");
			lSBQuery.append(" A.INCOME_TAX_CUT_AMOUNT,");
			lSBQuery.append(" A.SPECIAL_CUT_AMOUNT,");
			lSBQuery.append(" A.OTHER_BENEFITS,");
			lSBQuery.append(" A.OMR,");
			lSBQuery.append(" A.PENSN_CUT_AMOUNT,");
			lSBQuery.append(" A.RECOVERY_AMOUNT,");
			lSBQuery.append(" A.REDUCED_PENSION,");
			lSBQuery.append(" A.PERSONAL_PENSION_AMOUNT,");
			lSBQuery.append(" A.IR_AMOUNT,");
			lSBQuery.append(" A.MO_COMMISSION,");
			lSBQuery.append(" A.PAY_FOR_MONTH");
			lSBQuery.append(" FROM TRN_PENSION_BILL_DTLS A  ");
			lSBQuery
					.append(" JOIN TRN_PENSION_BILL_HDR B ON B.TRN_PENSION_BILL_HDR_ID = A.TRN_PENSION_BILL_HDR_ID "
							+ " AND (B.rejected != 1 OR B.rejected is null )");
			lSBQuery
					.append(" LEFT OUTER JOIN MST_BANK MB ON MB.BANK_CODE = B.BANK_CODE AND MB.LANG_ID = 1 ");
			/*
			 * lSBQuery.append(" JOIN RLT_BILL_CHEQUE C ON C.BILL_NO = B.BILL_NO "
			 * );lSBQuery.append(
			 * " JOIN TRN_CHEQUE_DTLS D ON D.CHEQUE_ID = C.CHEQUE_ID ");
			 */
			lSBQuery
					.append(" JOIN RLT_BANK_BRANCH RBB ON RBB.BRANCH_CODE = B.BRANCH_CODE ");
			lSBQuery.append(" WHERE B.FOR_MONTH BETWEEN " + lIntFromMonth
					+ " AND " + lIntToMonth);
			lSBQuery.append(" AND RBB.LOCATION_CODE = " + lArgslocationCode
					+ " AND ");
			lSBQuery.append(" B.FOR_MONTH = A.PAY_FOR_MONTH AND B.BANK_CODE = "
					+ lArgsBank + " AND B.BRANCH_CODE = " + lArgsBranch
					+ " AND ");
			lSBQuery
					.append(" (B.BILL_TYPE = 'Monthly' OR B.BILL_TYPE = 'MR') ");
			// lSBQuery.append(" AND A.PPO_NO = '" + lStrPpoNo + "' ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			List lLstVO = lQuery.list();
			BigDecimal lBDTemp;
			if (lLstVO != null && !lLstVO.isEmpty()) {
				Iterator it = lLstVO.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					lObjMonthlyVo = new MonthlyPensionBillVO();
					if (tuple[0] != null) {
						lObjMonthlyVo.setPpoNo((String) tuple[0]);
					}
					if (tuple[1] != null) {
						lObjMonthlyVo.setPensionerName((String) tuple[1]);
					}
					if (tuple[2] != null) {
						lObjMonthlyVo.setAccountNo((String) tuple[2]);
					}
					if (tuple[3] != null) {
						lBDTemp = (BigDecimal) tuple[3];

					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setBasicPensionAmount(lBDTemp);

					if (tuple[4] != null) {
						lBDTemp = (BigDecimal) tuple[4];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setDpPercentAmount(lBDTemp);

					if (tuple[5] != null) {
						lBDTemp = (BigDecimal) tuple[5];

					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setTiPercentAmount(lBDTemp);

					if (tuple[6] != null) {
						lBDTemp = (BigDecimal) tuple[6];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setMedicalAllowenceAmount(lBDTemp);

					if (tuple[7] != null) {
						lBDTemp = (BigDecimal) tuple[7];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setCvpMonthlyAmount(lBDTemp);

					if (tuple[8] != null) {
						lBDTemp = (BigDecimal) tuple[8];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setTIArrearsAmount(lBDTemp);

					if (tuple[9] != null) {
						lBDTemp = (BigDecimal) tuple[9];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setOtherArrearsAmount(lBDTemp);

					if (tuple[10] != null) {
						lBDTemp = (BigDecimal) tuple[10];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setItCutAmount(lBDTemp);

					if (tuple[11] != null) {
						lBDTemp = (BigDecimal) tuple[11];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setSpecialCutAmount(lBDTemp);

					if (tuple[12] != null) {
						lBDTemp = (BigDecimal) tuple[12];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setOtherBenefit(lBDTemp);

					if (tuple[13] != null) {
						lBDTemp = (BigDecimal) tuple[13];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setOMR(lBDTemp);

					if (tuple[14] != null) {
						lBDTemp = (BigDecimal) tuple[14];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setPensionCutAmount(lBDTemp);

					if (tuple[15] != null) {
						lBDTemp = (BigDecimal) tuple[15];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setRecoveryAmount(lBDTemp);

					if (tuple[16] != null) {
						lBDTemp = (BigDecimal) tuple[16];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setNetPensionAmount(lBDTemp);

					if (tuple[17] != null) {
						lBDTemp = (BigDecimal) tuple[17];

					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setPersonalPension(lBDTemp);

					if (tuple[18] != null) {
						lBDTemp = (BigDecimal) tuple[18];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setIr(lBDTemp);
					if (tuple[19] != null) {
						lBDTemp = (BigDecimal) tuple[19];
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setMOComm(lBDTemp);
					if (tuple[20] != null) {
						lBDTemp = new BigDecimal(tuple[20].toString());
					} else {
						lBDTemp = new BigDecimal(0);
					}
					lObjMonthlyVo.setForMonth(lBDTemp.intValue());
					lLstobjTrnPensionBillDtls.add(lObjMonthlyVo);
				}
			}
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstobjTrnPensionBillDtls;
	}

	/*
	 * public ArrayList getMstPenHdrDtls(String penCode) throws Exception {
	 * ArrayList lArrDtls = new ArrayList(); select
	 * m.pnsnr_prefix||' '||m.first_name||' '||m.middle_name||' '||m.last_name,
	 * d.dsgn_name,m.date_of_death from mst_pensioner_hdr m,org_designation_mst
	 * d where m.pensioner_code = 1000100055108 and m.designation = d.dsgn_id
	 * and m.case_status = 'APPROVED' try{ Session ghibSession = getSession();
	 * StringBuilder lSBQuery = new StringBuilder();lSBQuery.append(
	 * " select m.pnsnrPrefix||' '||m.firstName||' '||m.middleName||' '||m.lastName, "
	 * ); lSBQuery.append(" d.dsgnName,m.dateOfDeath "); lSBQuery.append(" from
	 * MstPensionerHdr m,OrgDesignationMst d "); lSBQuery.append(" where
	 * m.pensionerCode = :penCode "); lSBQuery.append(" and m.designation =
	 * d.dsgnId "); lSBQuery.append(" and m.caseStatus = 'APPROVED' "); Query
	 * lQuery = ghibSession.createQuery(lSBQuery.toString());
	 * lQuery.setParameter("penCode", penCode); List lLstVO = lQuery.list();
	 * if(lLstVO != null && !lLstVO.isEmpty()) { Object[] tuple =
	 * (Object[])lLstVO.get(0); lArrDtls.add(tuple[0]); lArrDtls.add(tuple[1]);
	 * lArrDtls.add(tuple[2]); } } catch(Exception e) {
	 * logger.error("Error is."+e,e); throw(e); } return lArrDtls; }
	 */

	public ArrayList getChqCoverStickerDtls(String frmDate, String toDate,
			String billType, String lStrLocCode) throws Exception {

		ArrayList lArrDtls = new ArrayList();
		/*
		 * select bb.branch_name, bb.branch_address, c.advice_no from
		 * trn_cheque_dtls c, rlt_bill_cheque rb, trn_pension_bill_hdr bh,
		 * rlt_bank_branch bb where c.chq_dsptch_date >= '01-Sep-2008' and
		 * c.chq_dsptch_date <= '05-Oct-2008' and rb.cheque_id = c.cheque_id and
		 * bh.bill_no = rb.bill_no and bh.branch_code = bb.branch_code and
		 * bh.scheme = 'IRLA' and bh.bill_type = 'Monthly' and bh.location_code
		 * = bb.location_code order by c.advice_no
		 */
		try {
			Session ghibSession = getReadOnlySession();
			StringBuilder lSBQuery = new StringBuilder();
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");

			lSBQuery
					.append(" select distinct bb.branch_Name, bb.branch_Address, c.advice_No "
							+ "from Trn_Cheque_Dtls c, Rlt_Bill_Cheque rb, Trn_Pension_Bill_Hdr bh, Rlt_Bank_Branch bb "
							+ "where c.chq_Dsptch_Date >= :frmDate and c.chq_Dsptch_Date <= :toDate and rb.cheque_Id = c.cheque_Id "
							+ "and bh.bill_No = rb.bill_No and bh.branch_Code = bb.branch_Code "
							+ "and bh.location_code=:lStrLocCode   and bh.scheme = :lScheme and bh.bill_Type = :billType and bh.location_Code = bb.location_Code order by c.advice_No ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setDate("frmDate", lObjDateFormat.parse(frmDate));
			lQuery.setDate("toDate", lObjDateFormat.parse(toDate));
			lQuery.setString("billType", billType);
			lQuery.setString("lScheme", "IRLA");
			lQuery.setString("lStrLocCode", lStrLocCode);

			List lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty()) {
				Iterator it = lLstVO.iterator();

				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					ArrayList arrInner = new ArrayList();
					arrInner.add(tuple[0]); // branch name
					arrInner.add(tuple[1]); // branch addr
					arrInner.add(tuple[2]); // advice no

					lArrDtls.add(arrInner);
				}

			}
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw (e);
		}
		return lArrDtls;
	}

	public ArrayList getStickerDtls(String frmDate, String toDate,
			String ppoNo, String locCode) throws Exception {

		ArrayList lArrDtls = new ArrayList();
		/*
		 * select trh.ppo_no, tpbh.bill_type, tpbd.reduced_pension,
		 * tpbh.for_month, tpbd.pensioner_name, trh.commension_date,
		 * trh.basic_pension_amount, trh.cvp_monthly_amount, trh.dp_percent,
		 * trh.ti_percent, trh.medical_allowence_amount,
		 * trh.cvp_restoration_date, trh.paid_date, mph.date_of_death, fd.name,
		 * fd.date_of_death from trn_pension_rqst_hdr trh, trn_pension_bill_hdr
		 * tpbh, trn_pension_bill_dtls tpbd, mst_pensioner_hdr mph left join
		 * mst_pensioner_family_dtls fd on fd.pensioner_code =
		 * mph.pensioner_code and fd.percentage=100, trn_pensioner_seen_dtls s
		 * where trh.ppo_no = tpbd.ppo_no and tpbd.trn_pension_bill_hdr_id =
		 * tpbh.trn_pension_bill_hdr_id and trh.pensioner_code =
		 * s.pensioner_code and s.active_flag ='Y' and (tpbh.for_month =
		 * tpbd.pay_for_month or tpbd.pay_for_month is null) and
		 * trh.curr_case_status in (5,70) and trh.case_status != 'REJECTCHNG'
		 * and s.seen_date between '01/jan/2009' and '17/jan/2009' and
		 * mph.pensioner_code = tpbd.pensioner_code and trh.pensioner_code =
		 * mph.pensioner_code and mph.case_status = trh.case_status and
		 * tpbh.bill_type in ('CVP', 'DCRG', 'PENSION') order by trh.ppo_no
		 */
		try {
			Session ghibSession = getReadOnlySession();
			StringBuilder lSBQuery = new StringBuilder();
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");

			lSBQuery
					.append("select trh.ppo_no ppoNo,tpbh.bill_type billType,tpbd.reduced_pension redPen,tpbh.for_month forMonth,tpbd.pensioner_name penName, "
							+ "trh.commension_date commDate,trh.basic_pension_amount basicPen,trh.cvp_monthly_amount cvpMonthly,trh.dp_percent dpPercent, "
							+ "ROUND(tpbd.ti_amount/(tpbd.pension_amount + tpbd.dp_amount) * 100) tiPercent,trh.medical_allowence_amount MAAmnt,trh.cvp_restoration_date cvpRestDt,trh.paid_date paidDt,trh.type_Flag typeFlg,trh.cvp_Date cvpDt, "
							+ "mph.date_of_death DOD,fd.name fpName,fd.date_of_death fpDate,tpbd.adp_amount ADPAmnt,tpbd.income_tax_cut_amount ITCut,tpbd.recovery_amount recAmnt, "
							+ "trh.last_paid_date lstPaidDt from trn_pension_rqst_hdr trh,trn_pension_bill_hdr tpbh,trn_pension_bill_dtls tpbd, "
							+ "trn_pensioner_seen_dtls s,mst_pensioner_hdr mph left join mst_pensioner_family_dtls fd on "
							+ "fd.pensioner_code = mph.pensioner_code and fd.percentage= :fpPercent "
							+ "where trh.ppo_no = tpbd.ppo_no and "
							+ "tpbd.trn_pension_bill_hdr_id = tpbh.trn_pension_bill_hdr_id and trh.pensioner_code = s.pensioner_code and "
							+ "s.active_flag =:lActvFlg and (tpbh.for_month = tpbd.pay_for_month or tpbd.pay_for_month is null) and "
							+ "trh.curr_case_status in (:currCaseSt1,:currCaseSt2) and trh.case_status != :caseStatus and "
							+ "mph.pensioner_code = tpbd.pensioner_code and trh.pensioner_code = mph.pensioner_code "
							+ "and mph.case_status = trh.case_status "
							+ "and s.seen_date between :frmDate and :toDate and tpbh.bill_type in (:lCVP,:lDCRG,:lPENSION )"
							+ " and trh.location_code =:locCode ");

			if (ppoNo != null && ppoNo.length() > 0) {
				lSBQuery.append(" and trh.ppo_no like '%" + ppoNo + "%' ");
			}
			lSBQuery.append(" order by trh.ppo_no ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			if (frmDate != null) {
				lQuery.setDate("frmDate", lObjDateFormat.parse(frmDate));
			}
			lQuery.setDate("toDate", lObjDateFormat.parse(toDate));

			lQuery.setString("locCode", locCode);
			lQuery.setParameter("currCaseSt1", 5);
			lQuery.setParameter("currCaseSt2", 70);
			lQuery.setString("caseStatus", "REJECTCHNG");
			lQuery.setString("lCVP", "CVP");
			lQuery.setString("lDCRG", "DCRG");
			lQuery.setString("lPENSION", "PENSION");
			lQuery.setString("lActvFlg", "Y");
			lQuery.setInteger("fpPercent", 100);

			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.addScalar("ppoNo", Hibernate.STRING);
			lQuery.addScalar("billType", Hibernate.STRING);
			lQuery.addScalar("redPen", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("forMonth", Hibernate.STRING);
			lQuery.addScalar("penName", Hibernate.STRING);
			lQuery.addScalar("commDate", Hibernate.DATE);
			lQuery.addScalar("basicPen", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("cvpMonthly", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("dpPercent", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("tiPercent", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("MAAmnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("cvpRestDt", Hibernate.DATE);
			lQuery.addScalar("paidDt", Hibernate.DATE);
			lQuery.addScalar("typeFlg", Hibernate.STRING);
			lQuery.addScalar("cvpDt", Hibernate.DATE);
			lQuery.addScalar("DOD", Hibernate.DATE);
			lQuery.addScalar("fpName", Hibernate.STRING);
			lQuery.addScalar("fpDate", Hibernate.DATE);
			lQuery.addScalar("ADPAmnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("ITCut", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("recAmnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lstPaidDt", Hibernate.DATE);

			List lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty()) {
				Iterator it = lLstVO.iterator();

				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					lArrDtls.add(tuple[0]); // ppo no
					lArrDtls.add(tuple[1]); // bill type
					lArrDtls.add(tuple[2]); // reduced pension
					lArrDtls.add(tuple[3]); // for month
					// lArrDtls.add(tuple[4]); //pension cut
					lArrDtls.add(tuple[4]); // name
					lArrDtls.add(tuple[5]); // commension date
					lArrDtls.add(tuple[6]); // basic amont
					lArrDtls.add(tuple[7]); // cvp monthly
					lArrDtls.add(tuple[8]); // dp percent
					lArrDtls.add(tuple[9]); // ti percent
					lArrDtls.add(tuple[10]); // ma
					lArrDtls.add(tuple[11]); // cvp rest date
					lArrDtls.add(tuple[12]); // paid date
					/*
					 * lArrDtls.add(tuple[14]); // middle name
					 * lArrDtls.add(tuple[15]); // last anme
					 * lArrDtls.add(tuple[16]); // pensioner prefix
					 */lArrDtls.add(tuple[13]); // type flag
					lArrDtls.add(tuple[14]); // cvp date
					lArrDtls.add(tuple[15]); // pensioner's DOD
					lArrDtls.add(tuple[16]); // fp name
					lArrDtls.add(tuple[17]); // fp DOD
					lArrDtls.add(tuple[18]); // adp amount
					lArrDtls.add(tuple[19]); // income tax cut amount
					lArrDtls.add(tuple[20]); // rec amount
					lArrDtls.add(tuple[21]); // last paid date

				}

			}
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw (e);
		}
		return lArrDtls;
	}

	public ArrayList getPPORegisterDtls(String frmDate, String toDate,
			String ppoNo, String locCode) throws Exception {

		ArrayList lArrDtls = new ArrayList();

		try {
			Session ghibSession = getReadOnlySession();
			StringBuilder lSBQuery = new StringBuilder();
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");

			lSBQuery
					.append("select rq.ppo_no ppoNo,rq.case_register_no caseRegNo,rq.sanction_letter_no sancLttrNo,rq.paid_date paidDt,rq.ppo_date ppoDate, "
							+ "rq.basic_pension_amount basicAmnt,rq.commension_date commDate,rq.cvp_monthly_amount cvpMonthly,rq.pension_type penType,rq.dp_percent dpPrcnt, "
							+ "rq.cvp_date cvpDate,rq.medical_allowence_amount medAllAmnt,rq.fp1_date fp1Dt,rq.fp1_amount fp1Amnt,rq.fp2_date fp2Dt,rq.fp2_amount fp2Amnt, "
							+ "rq.form22_issued_date form22Dt,rq.lpc_issued_date lpcDt,tpbh.bill_type billType,tpbd.reduced_pension redPen,tpbh.for_month forMnth, "
							+ "mph.pnsnr_prefix PREF,"
							+ "mph.date_of_death DOD,odm.dsgn_name desName, "
							+ "mph.other_designation otherDesg,fd.name fpName,cm.loc_name locName,rq.case_regdate regDate,rq.form22_Issued_Auth form22Auth,rq.lpc_Issued_Auth lpcAuth "
							+ ",tpbd.adp_Amount adpAmnt,tpbd.income_tax_cut_amount ITcutAmnt,tpbd.recovery_amount recAmnt,rq.cvp_restoration_date cvpRestDt,rq.last_paid_date lstPaidDt, "
							+ " isu1.lpc_issue_auth lpcIss,isu2.form22_issue_auth form22Iss,rq.lpc_issued_otherauth lpcOther,rq.form22_issued_otherauth fRM22Other, "
							+ "mph.first_name FRNAME,mph.middle_name MDNAME,mph.last_name LSNAME "
							+ " from trn_pension_bill_hdr tpbh,trn_pension_bill_dtls tpbd, "
							+ "mst_pensioner_hdr mph left join org_designation_mst odm on  mph.designation = odm.dsgn_code and "
							+ "odm.lang_id=1,trn_pensioner_seen_dtls s,trn_pension_rqst_hdr rq left join mst_pensioner_family_dtls fd "
							+ "on rq.pensioner_code =fd.pensioner_code and fd.percentage=:lPercent left join trn_pension_transfer_dtls trd  "
							+ "on trd.pensioner_code = rq.pensioner_code  and trd.to_location =:lLocCode left join cmn_location_mst cm on "
							+ "trd.from_location = cm.location_code left join mst_pension_form_issue_auth isu1 ON isu1.auith_id  = rq.lpc_Issued_Auth "
							+ "left join mst_pension_form_issue_auth isu2 ON isu2.auith_id  = rq.form22_Issued_Auth"
							+ " where s.seen_date between :frmDate and :toDate and "
							+ "tpbh.trn_pension_bill_hdr_id = tpbd.trn_pension_bill_hdr_id and tpbd.pensioner_code = mph.pensioner_code and "
							+ "mph.pensioner_code = s.pensioner_code and s.active_flag = :lActvFlg and  rq.ppo_no = tpbd.ppo_no "
							+ "and rq.curr_case_status in (:lStatus1,:lStatus2) and (tpbh.for_month = tpbd.pay_for_month or tpbd.pay_for_month is null) and "
							+ "tpbh.bill_type in (:lCVP,:lDCRG,:lPENSION) and  rq.case_status != :lStatus and rq.location_code=:lLocCode ");

			if (!"".equals(ppoNo)) {
				lSBQuery.append(" and rq.ppo_no like '%" + ppoNo + "%'");
			}
			lSBQuery.append(" order by rq.ppo_no");

			/*
			 * lSBQuery.append(" select tpbd.ppo_no, tpbh.bill_type, tpbd.reduced_pension, tpbh.for_month, "
			 * + "sum(it.amount), mph.first_name," +
			 * "mph.date_of_death,odm.dsgn_name ,mph.middle_name,mph.last_name,mph.PNSNR_PREFIX "
			 * + "from trn_pension_bill_hdr tpbh " + "join trn_pension_bill_dtls
			 * tpbd on tpbh.trn_pension_bill_hdr_id =
			 * tpbd.trn_pension_bill_hdr_id and tpbh.bill_type in
			 * ('CVP','DCRG','PENSION') " + "join mst_pensioner_hdr mph on
			 * tpbd.pensioner_code = mph.pensioner_code " + "left join
			 * org_designation_mst odm on mph.designation = odm.dsgn_id
			 * " + "left join trn_pension_it_cut_dtls it on mph.pensioner_code =
			 * it.pensioner_code " + "and (it.type_flag = 'PP' or (it.type_flag
			 * = 'PT' and it.from_month >= tpbh.for_month and it.to_month <=
			 * tpbh.for_month)) " + "where mph.pensioner_code =:pnsnrCode
			 * "); //lSBQuery.append(" and mph.case_status = 'APPROVED'
			 * "); if(frmDate != null) { lSBQuery.append(" and tpbh.created_date
			 * >=:frmDate"); } lSBQuery.append(" and tpbh.created_date <=:toDate
			 * " + "and (tpbh.for_month = tpbd.pay_for_month or
			 * tpbd.pay_for_month is null) " + "group by tpbd.ppo_no,
			 * tpbh.bill_type, tpbd.reduced_pension, tpbh.for_month,
			 * mph.first_name, " + "mph.middle_name,
			 * mph.last_name,mph.date_of_death,odm.dsgn_name,mph.PNSNR_PREFIX
			 * " + "order by tpbd.ppo_no, tpbh.bill_type ");
			 */

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			// lQuery.setParameter("pnsnrCode", pnsnrCode);

			if (frmDate != null) {
				lQuery.setDate("frmDate", lObjDateFormat.parse(frmDate));
			}

			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.addScalar("ppoNo", Hibernate.STRING);
			lQuery.addScalar("caseRegNo", Hibernate.STRING);
			lQuery.addScalar("sancLttrNo", Hibernate.STRING);
			lQuery.addScalar("paidDt", Hibernate.DATE);
			lQuery.addScalar("ppoDate", Hibernate.DATE);
			lQuery.addScalar("basicAmnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("commDate", Hibernate.DATE);
			lQuery.addScalar("cvpMonthly", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("penType", Hibernate.STRING);
			lQuery.addScalar("dpPrcnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("cvpDate", Hibernate.DATE);
			lQuery.addScalar("medAllAmnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("fp1Dt", Hibernate.DATE);
			lQuery.addScalar("fp1Amnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("fp2Dt", Hibernate.DATE);
			lQuery.addScalar("fp2Amnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("form22Dt", Hibernate.DATE);
			lQuery.addScalar("lpcDt", Hibernate.DATE);
			lQuery.addScalar("billType", Hibernate.STRING);
			lQuery.addScalar("redPen", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("forMnth", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("PREF", Hibernate.STRING);
			lQuery.addScalar("DOD", Hibernate.DATE);
			lQuery.addScalar("desName", Hibernate.STRING);
			lQuery.addScalar("otherDesg", Hibernate.STRING);
			lQuery.addScalar("fpName", Hibernate.STRING);
			lQuery.addScalar("locName", Hibernate.STRING);
			lQuery.addScalar("regDate", Hibernate.DATE);
			lQuery.addScalar("form22Auth", Hibernate.STRING);
			lQuery.addScalar("lpcAuth", Hibernate.STRING);
			lQuery.addScalar("adpAmnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("ITcutAmnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("recAmnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("cvpRestDt", Hibernate.DATE);
			lQuery.addScalar("lstPaidDt", Hibernate.DATE);
			lQuery.addScalar("lpcIss", Hibernate.STRING);
			lQuery.addScalar("form22Iss", Hibernate.STRING);
			lQuery.addScalar("lpcOther", Hibernate.STRING);
			lQuery.addScalar("fRM22Other", Hibernate.STRING);
			lQuery.addScalar("FRNAME", Hibernate.STRING);
			lQuery.addScalar("MDNAME", Hibernate.STRING);
			lQuery.addScalar("LSNAME", Hibernate.STRING);

			lQuery.setDate("toDate", lObjDateFormat.parse(toDate));
			lQuery.setParameter("lPercent", 100);
			lQuery.setParameter("lLocCode", locCode);
			lQuery.setString("lActvFlg", "Y");
			lQuery.setParameter("lStatus1", 5);
			lQuery.setParameter("lStatus2", 70);
			lQuery.setString("lCVP", "CVP");
			lQuery.setString("lDCRG", "DCRG");
			lQuery.setString("lPENSION", "PENSION");
			lQuery.setString("lStatus", "REJECTCHNG");
			// lQuery.setParameter("pnsnrCode", pnsnrCode);

			List lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty()) {
				Iterator it = lLstVO.iterator();

				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					lArrDtls.add(tuple[0]); // ppo no
					lArrDtls.add(tuple[1]); // case_register_no
					lArrDtls.add(tuple[2]); // sanction_letter_no
					lArrDtls.add(tuple[3]); // paid_date
					lArrDtls.add(tuple[4]); // ppo_date
					lArrDtls.add(tuple[5]); // basic_pension_amount
					lArrDtls.add(tuple[6]); // commension_date
					lArrDtls.add(tuple[7]); // cvp_monthly_amount
					lArrDtls.add(tuple[8]); // pension_type
					lArrDtls.add(tuple[9]); // dp_percent
					lArrDtls.add(tuple[10]); // cvp_date

					lArrDtls.add(tuple[11]); // medical_allowence_amount
					lArrDtls.add(tuple[12]); // fp1_date
					lArrDtls.add(tuple[13]); // fp1_amount
					lArrDtls.add(tuple[14]); // fp2_date
					lArrDtls.add(tuple[15]); // fp2_amount
					lArrDtls.add(tuple[16]); // form22_issued_date

					lArrDtls.add(tuple[17]); // lpc_issued_date
					lArrDtls.add(tuple[18]); // bill_type
					lArrDtls.add(tuple[19]); // reduced_pension
					lArrDtls.add(tuple[20]); // for_month

					StringBuffer lStrPenName = new StringBuffer();
					lStrPenName
							.append(tuple[21] != null ? tuple[21] + " " : "");
					lStrPenName
							.append(tuple[39] != null ? tuple[39] + " " : "");
					lStrPenName
							.append(tuple[40] != null ? tuple[40] + " " : "");
					lStrPenName.append(tuple[41] != null ? tuple[41] : "");

					lArrDtls.add(lStrPenName.toString()); // name

					lArrDtls.add(tuple[22]); // date of death
					lArrDtls.add(tuple[23]); // dsgn_name
					lArrDtls.add(tuple[24]); // other_designation
					lArrDtls.add(tuple[25]); // FP name
					lArrDtls.add(tuple[26]); // loc_name
					lArrDtls.add(tuple[27]); // reg date
					lArrDtls.add(tuple[28]); // form22 issue auth
					lArrDtls.add(tuple[29]); // lpc issue auth
					lArrDtls.add(tuple[30]); // adp amount
					lArrDtls.add(tuple[31]); // IT Cut amount
					lArrDtls.add(tuple[32]); // Rec amount
					lArrDtls.add(tuple[33]); // cvp rest date
					lArrDtls.add(tuple[34]); // last paid date
					lArrDtls.add(tuple[35]); // lpcIss
					lArrDtls.add(tuple[36]); // form22Iss
					lArrDtls.add(tuple[37]); // lpcOther
					lArrDtls.add(tuple[38]); // fRM22Other

				}
			}
		} catch (Exception e) {
			logger.error("Error is." + e, e);

			throw (e);
		}
		return lArrDtls;
	}

	public String getTrnsfrTrsyName(String pensionerCode, String locCode)
			throws Exception {

		String lStrTrsyName = null;
		/*
		 * select lm.loc_name from trn_pension_transfer_dtls td,
		 * cmn_location_mst lm where td.pensioner_code = 100010005574 and
		 * td.to_location = 100209 and td.from_location = lm.location_code
		 */
		try {
			Session ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery
					.append(" select lm.locName from TrnPensionTransferDtls td, CmnLocationMst lm ");
			lSBQuery.append(" where td.pensionerCode = :penCode ");
			lSBQuery.append(" and td.toLocation = :locCode ");
			lSBQuery.append(" and td.fromLocation = lm.locationCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("penCode", pensionerCode);
			lQuery.setParameter("locCode", locCode);

			List lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty()) {
				if (lLstVO.get(0) != null) {
					lStrTrsyName = lLstVO.get(0).toString();
				}
			}
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw (e);
		}
		return lStrTrsyName;

	}

	public List getMRBill(Date lStrFromDate, Date lStrToDate,
			String lStrScheme, String lStrReportType, String lStrLocCode)
			throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List lLstRes = new ArrayList();
		List lLstType = new ArrayList();
		try {
			lSBQuery
					.append(" select mr.inwd_no,mr.inwd_date,mr.ppo_no,mr.pensioner_name,mr.updated_date,mr.grant_amnt,mr.created_post_id,mr.branch_code "
							+ " from trn_pension_med_rembrsmnt mr "
							+ " where mr.status = :lStatus "
							+ " AND mr.inwd_date between :lStrFromDate "
							+ " AND :lStrToDate "
							+ " AND mr.scheme IN (:lStrScheme) and mr.location_code = :location_code ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setDate("lStrFromDate", lStrFromDate);
			lQuery.setDate("lStrToDate", lStrToDate);
			lQuery.setLong("lStatus", new Long(60));

			if (lStrScheme.equalsIgnoreCase("Bank Payments")) {
				lLstType.add("IRLA");
				lLstType.add("PSB");
			}
			if (lStrScheme.equalsIgnoreCase("RUBARU")) {
				lLstType.add("RUBARU");
			}
			if (lStrScheme.equalsIgnoreCase("MONEY ORDER")) {
				lLstType.add("MONEY ORDER");
			}

			lQuery.setParameter("location_code", lStrLocCode);
			lQuery.setParameterList("lStrScheme", lLstType);
			lLstRes = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstRes;
	}

	public List getAuditorNameForMRBill(String postId, Long langId)
			throws Exception {

		/*
		 * select distinct concat(concat(oem.emp_fname, ''),
		 * concat(concat(oem.emp_mname, ''),concat(oem.emp_lname, ''))),
		 * r.post_id from org_userpost_rlt r, org_emp_mst oem where r.post_id in
		 * (104542, 104542, 104542, 104542, 104542, 104542, 104542, 104542,
		 * 104542, 104542, 104542, 104542, 104542, 104542, 104542, 104542,
		 * 104542, 104542, 104542, 104542, 104542, 104542, 104542, 104542,
		 * 104542, 104542, 104542, 104542, 104542, 104542, 104542, 104542,
		 * 104542) and r.user_id = oem.user_id and oem.lang_Id = 1
		 */
		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List lLstRes = new ArrayList();
		try {
			lSBQuery
					.append(" select distinct concat(concat(oem.emp_fname, ''),concat(concat(oem.emp_mname, ''),concat(oem.emp_lname, ''))) NAME, "
							+ " r.post_id POST from org_userpost_rlt r, org_emp_mst oem "
							+ " where r.post_id in ("
							+ postId
							+ ") and r.user_id = oem.user_id and r.activate_flag=1 and oem.activate_flag=1 and oem.lang_Id= :langId");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.addScalar("NAME", Hibernate.STRING);
			lQuery.addScalar("POST", Hibernate.STRING);

			lQuery.setLong("langId", langId);
			lLstRes = lQuery.list();
			/*
			 * if(lLstRes != null && ! lLstRes.isEmpty()) { }
			 */
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstRes;
	}

	public ArrayList getAuditor(String lStrLangId, String lstrLocId)
			throws Exception {

		ArrayList<ComboValuesVO> arrAuditor = new ArrayList<ComboValuesVO>();
		ResourceBundle bundleConst = ResourceBundle
				.getBundle("resources/pensionpay/PensionConstants");
		String lStrAuditor = bundleConst.getString("PENSION.CASESUBJECT");
		try {
			Session hibSession = ServiceLocator.getServiceLocator()
					.getSessionFactorySlave().getCurrentSession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery
					.append("select up.post_id POST,emp.emp_fname FNAME,emp.emp_mname MNAME,emp.emp_lname LNAME from org_emp_mst emp,org_userpost_rlt up,wf_hierarchy_reference_mst wf, "
							+ " wf_hierachy_post_mpg wfp "
							+ " where emp.user_id = up.user_id and wfp.HIERACHY_REF_ID = wf.HIERACHY_REF_ID and up.post_id = wfp.POST_ID and "
							+ " wf.LOC_CODE = :locID and wf.DESCRIPTION = :desc and up.activate_flag = 1 and wfp.LEVEL_ID = 50 "
							+ " order by emp.emp_fname, emp.emp_mname, emp.emp_lname");

			SQLQuery Query = hibSession.createSQLQuery(lSBQuery.toString());

			Query.addScalar("POST", Hibernate.STRING);
			Query.addScalar("FNAME", Hibernate.STRING);
			Query.addScalar("MNAME", Hibernate.STRING);
			Query.addScalar("LNAME", Hibernate.STRING);

			Query.setParameter("desc", lStrAuditor);
			Query.setParameter("locID", lstrLocId);

			List listValues = Query.list();
			if (listValues != null && !listValues.isEmpty()) {
				Iterator it = listValues.iterator();
				while (it.hasNext()) {
					ComboValuesVO vo = new ComboValuesVO();
					Object[] tuple = (Object[]) it.next();
					vo.setId(tuple[0].toString());

					StringBuffer lStrAuditorName = new StringBuffer();
					lStrAuditorName.append(tuple[1] != null ? tuple[1] + " "
							: "");
					lStrAuditorName.append(tuple[2] != null ? tuple[2] + " "
							: "");
					lStrAuditorName.append(tuple[3] != null ? tuple[3] : "");

					vo.setDesc(lStrAuditorName.toString());
					arrAuditor.add(vo);
				}
			}
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw (e);
		}
		return arrAuditor;
	}

	public List getMRTrackingReportSummary(Date lStrFromDate, Date lStrToDate,
			String lStrAuditorName, Long langId, String lStrLocCode)
			throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List lLstRes = new ArrayList();
		List finalList = new ArrayList();
		Object[] innerList = null;
		try {
			lSBQuery
					.append(" SELECT T1.emp_fname fname,t1.emp_mname mname,t1.emp_lname lname,T1.adPstId auditorPostId,SUM(APPRV) apprv,SUM(RJCT) rjct,SUM(PAND) pand,"
							+ " SUM(TOTAL) total FROM  "
							+ " (select oem.emp_fname emp_fname,oem.emp_mname emp_mname,oem.emp_lname emp_lname"
							+ " ,tmr.auditor_post_id adPstId, "
							+ " CASE WHEN tmr.status = :lStatus1 THEN COUNT(tmr.status) ELSE :lZero "
							+ " END APPRV, "
							+ " CASE WHEN tmr.status = :lStatus2 THEN COUNT(tmr.status) ELSE :lZero END RJCT, "
							+ " "
							+ " CASE WHEN tmr.status = :lStatus3 THEN COUNT(tmr.status) ELSE :lZero END PAND, "
							+ " CASE WHEN tmr.status IN "
							+ " (:lStatus1,:lStatus2,:lStatus3) THEN COUNT(tmr.status) END TOTAL "
							+ " from trn_pension_med_rembrsmnt tmr, org_userpost_rlt rup, org_emp_mst oem "
							+ " where tmr.auditor_post_id = rup.post_id and tmr.location_code = :locationCode and rup.user_id = oem.user_id and rup.activate_flag=1 and oem.activate_flag=1 AND "
							+ " tmr.status IN (:lStatus1,:lStatus2,:lStatus3) "
							+ " AND tmr.inwd_date between :lStrFromDate AND :lStrToDate "
							+ " AND oem.lang_id = :langId "
							+ " GROUP BY tmr.auditor_post_id,oem.emp_fname,oem.emp_mname,oem.emp_lname, "
							+ " tmr.status " + " ) T1 ");

			Long lngAuditiorPostId = null;
			if (!lStrAuditorName.equals("-1")) {
				lngAuditiorPostId = Long.parseLong(lStrAuditorName);
				lSBQuery.append("WHERE T1.adPstId = " + lngAuditiorPostId + "");
			}

			lSBQuery
					.append("GROUP BY T1.emp_fname,t1.emp_mname,t1.emp_lname, T1.adPstId ORDER BY T1.emp_fname,t1.emp_mname,t1.emp_lname ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("locationCode", lStrLocCode);
			lQuery.setDate("lStrFromDate", lStrFromDate);
			lQuery.setDate("lStrToDate", lStrToDate);
			lQuery.setLong("lStatus1", new Long(60));
			lQuery.setLong("lStatus2", new Long(70));
			lQuery.setLong("lStatus3", new Long(20));
			lQuery.setLong("lZero", new Long(0));
			lQuery.setLong("langId", langId);

			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.addScalar("fname", Hibernate.STRING);
			lQuery.addScalar("mname", Hibernate.STRING);
			lQuery.addScalar("lname", Hibernate.STRING);
			lQuery.addScalar("auditorPostId", Hibernate.LONG);

			lQuery.addScalar("apprv", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("rjct", Hibernate.BIG_DECIMAL);

			lQuery.addScalar("pand", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("total", Hibernate.BIG_DECIMAL);

			lLstRes = lQuery.list();
			if (lLstRes != null) {
				StringBuffer sbName = new StringBuffer();
				for (int i = 0; i < lLstRes.size(); i++) {
					Object[] tuple = (Object[]) lLstRes.get(i);
					innerList = new Object[6];
					if (tuple[0] != null || tuple[1] != null
							|| tuple[2] != null) {
						sbName = new StringBuffer();
						if (tuple[0] != null) {
							sbName.append(tuple[0].toString());
						}
						if (tuple[1] != null) {
							sbName.append(" " + tuple[1].toString());
						}
						if (tuple[2] != null) {
							sbName.append(" " + tuple[2].toString());
						}
						innerList[0] = sbName;
					} else {
						innerList[0] = " ";
					}
					innerList[1] = tuple[3];
					innerList[2] = tuple[4];
					innerList[3] = tuple[5];
					innerList[4] = tuple[6];
					innerList[5] = tuple[7];
					finalList.add(innerList);
				}
			} else {
				finalList = lLstRes;
			}
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return finalList;
	}

	public List getMRTrackingReportDetail(Date lStrFromDate, Date lStrToDate,
			String lStrAuditorName, String lStrStatusType, String lStrLocCode)
			throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List lLstRes = new ArrayList();
		try {
			lSBQuery
					.append(" select mr.inwd_no inwdNo,mr.inwd_date inwdDate,mr.ppo_no ppoNo,mr.pensioner_name pName,mr.rem_amnt remAmnt, "
							+ " (case when mr.status = :lStatus1 then :status1 "
							+ "      when mr.status = :lStatus2 then :status2 "
							+ "      when mr.status = :lStatus3 then :status3 "
							+ "      else 'Not Known' end) lStatus,mr.created_post_id crPstId,mr.auditor_post_id audPostId"
							+ " from trn_pension_med_rembrsmnt mr "
							+ " where mr.inwd_date between :lStrFromDate "
							+ " AND :lStrToDate ");

			if (!lStrAuditorName.equals("-1")) {
				lSBQuery.append("AND mr.auditor_post_id = :lStrAuditorName");
			}
			if (!lStrStatusType.equalsIgnoreCase("-1")) {
				lSBQuery.append(" AND mr.status  = :lStrStatusType ");
			} else {
				lSBQuery
						.append(" AND mr.status  in (:lStatus1,:lStatus2,:lStatus3) ");
			}

			lSBQuery.append(" AND mr.location_Code = :locationCode ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setDate("lStrFromDate", lStrFromDate);
			lQuery.setDate("lStrToDate", lStrToDate);
			lQuery.setLong("lStatus1", new Long(60));
			lQuery.setLong("lStatus2", new Long(70));
			lQuery.setLong("lStatus3", new Long(20));
			lQuery.setString("status1", "Approved");
			lQuery.setString("status2", "Rejected");
			lQuery.setString("status3", "Pending");
			lQuery.setString("locationCode", lStrLocCode);

			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.addScalar("inwdNo", Hibernate.STRING);
			lQuery.addScalar("inwdDate", Hibernate.DATE);
			lQuery.addScalar("ppoNo", Hibernate.STRING);
			lQuery.addScalar("pName", Hibernate.STRING);
			lQuery.addScalar("remAmnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lStatus", Hibernate.STRING);
			lQuery.addScalar("crPstId", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("audPostId", Hibernate.BIG_DECIMAL);

			if (!lStrAuditorName.equals("-1")) {
				lQuery.setString("lStrAuditorName", lStrAuditorName);
			}
			if (!lStrStatusType.equalsIgnoreCase("-1")) {
				lQuery.setString("lStrStatusType", lStrStatusType);
			}

			lLstRes = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstRes;
	}

	public List getDISDate(Date lStrDate, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List lLstRes = new ArrayList();
		try {
			lSBQuery
					.append(" select red.mjr_hd mjrHd,red.sub_mjr_hd sbMjrHd,red.min_hd minHd,red.sub_hd subHd, "
							+ " red.dtl_hd dtlHd,sum(red.gross_amnt) grossHd"
							+ " from rpt_expenditure_dtls red , trn_bill_register tbr  "
							+ " where red.exp_status_code = 160 "
							+ " AND tbr.bill_no = red.exp_no "
							+ " AND red.exp_dt = :lStrDate "
							+ " and tbr.Location_code = :lLocCode and tbr.Location_code = red.tsry_code"
							+ " group by red.mjr_hd,red.sub_mjr_hd,red.min_hd,red.sub_hd,red.dtl_hd ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.addScalar("mjrHd", Hibernate.STRING);
			lQuery.addScalar("sbMjrHd", Hibernate.STRING);

			lQuery.addScalar("minHd", Hibernate.STRING);
			lQuery.addScalar("subHd", Hibernate.STRING);
			lQuery.addScalar("dtlHd", Hibernate.STRING);
			lQuery.addScalar("grossHd", Hibernate.BIG_DECIMAL);

			lQuery.setDate("lStrDate", lStrDate);
			lQuery.setString("lLocCode", lStrLocCode);
			lLstRes = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstRes;
	}

	public List getDISSubDetails(Date lStrDate, List<Long> strBillLst,
			String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List lLstRes = null;
		try {
			lSBQuery
					.append(" select rrd.mjr_hd mjrHd,rrd.sub_mjr_hd sbMjrHd,rrd.min_hd minHd,rrd.sub_hd subHd, "
							+ " rrd.dtl_hd dtlHd ,sum(rrd.amount)amnt from  rpt_receipt_dtls rrd,trn_bill_register tbr "
							+ " where tbr.curr_bill_status = 160  AND tbr.bill_no = rrd.rcpt_no  AND tbr.bill_no in ( :BillLst )"
							+ " AND rrd.revenue_dt = :lStrDate "
							+ " and tbr.Location_code = :lLocCode and tbr.Location_code = rrd.tsry_code"
							+ " group by rrd.mjr_hd,rrd.sub_mjr_hd,rrd.min_hd,rrd.sub_hd,rrd.dtl_hd ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.addScalar("mjrHd", Hibernate.STRING);
			lQuery.addScalar("sbMjrHd", Hibernate.STRING);

			lQuery.addScalar("minHd", Hibernate.STRING);
			lQuery.addScalar("subHd", Hibernate.STRING);
			lQuery.addScalar("dtlHd", Hibernate.STRING);
			lQuery.addScalar("amnt", Hibernate.BIG_DECIMAL);

			lQuery.setDate("lStrDate", lStrDate);
			lQuery.setString("lLocCode", lStrLocCode);
			lQuery.setParameterList("BillLst", strBillLst);
			lLstRes = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstRes;
	}

	public List getBillNumber(String mjh, String smjh, String Mnh, String smnh,
			String dlts, Date lStrDate, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List<Long> lLstRes = new ArrayList<Long>();
		try {
			lSBQuery
					.append(" select br.bill_no  from trn_bill_register br  join rpt_expenditure_dtls exd on br.bill_no = exd.exp_no "
							+ " left join rpt_receipt_dtls rcpt on br.bill_no = rcpt.rcpt_no  where br.location_code = :LocCode and br.curr_bill_status = 160 "
							+ " AND exd.mjr_hd = :mjh and exd.sub_mjr_hd = :smjh  and exd.min_hd = :Mnh  AND exd.sub_hd = :smnh AND exd.dtl_hd  = :dlts ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("mjh", mjh);
			lQuery.setString("smjh", smjh);
			lQuery.setString("Mnh", Mnh);
			lQuery.setString("smnh", smnh);
			lQuery.setString("dlts", dlts);
			lQuery.setString("LocCode", lStrLocCode);
			// lQuery.setDate("lStrDate", lStrDate);

			lLstRes = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstRes;
	}

	public List getMinMaxDateForAllAuditor(Date lStrFromDate, Date lStrToDate,
			String lStrAuditorName) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List lLstRes = new ArrayList();
		try {
			lSBQuery
					.append(" select tmr.auditor_post_id audPostId,min(tmr.inwd_date) inwdDate1,max(tmr.inwd_date) inwdDate2 "
							+ " from trn_pension_med_rembrsmnt tmr "
							+ " WHERE tmr.inwd_date between :lStrFromDate AND :lStrToDate "
							+ " AND tmr.status = '20'  ");

			Long lngAuditiorPostId = null;
			if (!lStrAuditorName.equals("-1")) {
				lngAuditiorPostId = Long.parseLong(lStrAuditorName);
				lSBQuery.append("AND tmr.auditor_post_id = "
						+ lngAuditiorPostId + "");
			}
			lSBQuery
					.append("GROUP BY tmr.auditor_post_id ORDER BY tmr.auditor_post_id ");
			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.addScalar("audPostId", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("inwdDate1", Hibernate.DATE);

			lQuery.addScalar("inwdDate2", Hibernate.DATE);

			lQuery.setDate("lStrFromDate", lStrFromDate);
			lQuery.setDate("lStrToDate", lStrToDate);
			lLstRes = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstRes;
	}

	public List getAllocationReportDate(String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List lLstRes = new ArrayList();
		try {
			lSBQuery
					.append("SELECT HDCODE hdCd,DESCR DES,SUM(BB56) BB56,SUM(AB56) AB56,SUM(AG60) AG60,SUM(BB56+AB56+AG60) lSUM "
							+ " FROM "
							+ " ( "
							+ " SELECT MPH.HEAD_CODE HDCODE,MPH.DESCRIPTION DESCR, SUM(TRH.RED_BF_1_11_56) BB56, "
							+ " SUM(TRH.RED_AF_1_11_56) AB56,SUM(TRH.RED_AF_1_05_60) AG60 "
							+ " FROM TRN_PENSION_RQST_HDR TRH,MST_PENSION_HEADCODE MPH "
							+ " WHERE MPH.HEAD_CODE = TRH.HEAD_CODE "
							+ " AND TRH.HEAD_CODE IN (1,2,60) "
							+ " AND TRH.LOCATION_CODE =:locCode AND TRH.STATUS = 'Continue' AND TRH.PPO_INWARD_DATE <=:inWardDate  "
							+ " GROUP BY MPH.HEAD_CODE,MPH.DESCRIPTION "
							+ " ) temp GROUP BY  HDCODE, DESCR ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			Calendar cal = Calendar.getInstance();
			cal.setTime(SessionHelper.getCurDate());
			cal.add(Calendar.DATE, 1);
			lQuery.setDate("inWardDate", cal.getTime());
			lQuery.setString("locCode", lStrLocCode);

			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.addScalar("hdCd", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("DES", Hibernate.STRING);
			lQuery.addScalar("BB56", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("AB56", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("AG60", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lSUM", Hibernate.BIG_DECIMAL);

			lLstRes = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstRes;
	}

	public List get1996StateRevisedPensionerDtls(Date lDateFrom, Date lDateTo,
			String lStrROPType, String HeadCodeType, String lStrSeenType,
			String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List lLstRes = new ArrayList();
		try {
			lSBQuery
					.append(" select typ TYP, sum(cnt1) CNT1, sum(cnt2) CNT2, sum(cnt3) CNT3,sum(cnt4) CNT4, sum(cnt5) CNT5, "
							+ " sum(cnt6) CNT6,sum(cnt7) CNT7, sum(cnt8) CNT8, sum(cnt9) CNT9 "
							+ " from (select rq.pension_type typ,"
							+ " case when rq.basic_pension_amount <= 1275 then  count(1) else 0 end cnt1,"
							+ " case when rq.basic_pension_amount > 1275 and rq.basic_pension_amount <= 1540 then count(1) else 0 end cnt2,"
							+ " case when rq.basic_pension_amount > 1540 and rq.basic_pension_amount <= 3030 then count(1) else 0 end cnt3,"
							+ " case when rq.basic_pension_amount > 3030 and rq.basic_pension_amount <= 4520 then count(1) else 0 end cnt4,"
							+ " case when rq.basic_pension_amount > 4520 and rq.basic_pension_amount <= 5640 then count(1) else 0 end cnt5,"
							+ " case when rq.basic_pension_amount > 5640 and rq.basic_pension_amount <= 6575 then count(1) else 0 end cnt6,"
							+ " case when rq.basic_pension_amount > 6575 and rq.basic_pension_amount <= 7880 then count(1) else 0 end cnt7,"
							+ " case when rq.basic_pension_amount > 7880 and rq.basic_pension_amount <= 8660 then count(1) else 0 end cnt8,"
							+ " case when rq.basic_pension_amount > 8660 then count(1) else 0 end cnt9"
							+ " from trn_pension_rqst_hdr rq, trn_pnsncase_rop_rlt po where rq.ppo_no = po.ppo_no AND po.rop_paid = 'Y' ");

			if (lStrROPType.equalsIgnoreCase("R")) {
				lSBQuery.append(" AND po.rop = 1996 ");
			} else {
				lSBQuery.append(" AND po.rop != 1996 ");
			}
			if (lStrSeenType != null) {
				lSBQuery.append(" AND rq.seen_flag =:seenFlag ");
			}
			if (lDateFrom != null && lDateTo != null) {
				lSBQuery
						.append(" AND rq.ppo_inward_date between :lDateFrom AND :lDateTo ");
			}
			if (HeadCodeType.equalsIgnoreCase("SP")) {
				lSBQuery
						.append(" AND rq.head_code IN (1,2,3,4,5,6,7,8,9,10,11,12,15,16,19,20) and rq.location_code =:locCode ");
			}
			lSBQuery
					.append(" and rq.location_code=:locCode group by rq.pension_type, rq.basic_pension_amount) tmp group by typ ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.addScalar("TYP", Hibernate.STRING);
			lQuery.addScalar("CNT1", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("CNT2", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("CNT3", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("CNT4", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("CNT5", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("CNT6", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("CNT7", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("CNT8", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("CNT9", Hibernate.BIG_DECIMAL);

			if (lStrSeenType != null) {
				lQuery.setParameter("seenFlag", lStrSeenType);
			}
			if (lDateFrom != null && lDateTo != null) {
				lQuery.setDate("lDateFrom", lDateFrom);
				lQuery.setDate("lDateTo", lDateTo);
			}
			lQuery.setString("locCode", lStrLocCode);
			lLstRes = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstRes;
	}

	public List get1996PTPERevisedPensionerDtls(Date lDateFrom, Date lDateTo,
			String lStrROPType, String HeadCodeType, String lStrSeenType,
			String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List lLstRes = new ArrayList();
		try {
			lSBQuery
					.append("select typ TYP,"
							+ " sum(case when x in(13,14) then cnt1 else 0 end) APT,"
							+ " sum(case when x in(13,14) then cnt2 else 0 end) BPT,"
							+ " sum(case when x in(13,14) then cnt3 else 0 end) CPT,"
							+ " sum(case when x in(13,14) then cnt4 else 0 end) DPT,"
							+ " sum(case when x in(13,14) then cnt5 else 0 end) EPT,"
							+ " sum(case when x in(13,14) then cnt6 else 0 end) FPT,"
							+ " sum(case when x in(13,14) then cnt7 else 0 end) GPT,"
							+ " sum(case when x in(13,14) then cnt8 else 0 end) HPT,"
							+ " sum(case when x in(13,14) then cnt9 else 0 end) IPT,"
							+ " sum(case when x in(21,22) then cnt1 else 0 end) APE,"
							+ " sum(case when x in(21,22) then cnt2 else 0 end) BPE,"
							+ " sum(case when x in(21,22) then cnt3 else 0 end) CPE,"
							+ " sum(case when x in(21,22) then cnt4 else 0 end) DPE,"
							+ " sum(case when x in(21,22) then cnt5 else 0 end) EPE,"
							+ " sum(case when x in(21,22) then cnt6 else 0 end) FPE,"
							+ " sum(case when x in(21,22) then cnt7 else 0 end) GPE,"
							+ " sum(case when x in(21,22) then cnt8 else 0 end) HPE,"
							+ " sum(case when x in(21,22) then cnt9 else 0 end) IPE "
							+ " from (select rq.pension_type typ, "
							+ " case when rq.basic_pension_amount <= 1275 then"
							+ " count(1) else 0 end cnt1,"
							+ " case when rq.basic_pension_amount > 1275 and"
							+ " rq.basic_pension_amount <= 1540 then"
							+ " count(1) else 0 end cnt2,"
							+ " case when rq.basic_pension_amount > 1540 and"
							+ " rq.basic_pension_amount <= 3030 then"
							+ " count(1) else 0 end cnt3,"
							+ " case when rq.basic_pension_amount > 3030 and"
							+ " rq.basic_pension_amount <= 4520 then"
							+ " count(1) else 0 end cnt4,"
							+ " case when rq.basic_pension_amount > 4520 and"
							+ " rq.basic_pension_amount <= 5640 then"
							+ " count(1) else 0 end cnt5,"
							+ " case when rq.basic_pension_amount > 5640 and"
							+ " rq.basic_pension_amount <= 6575 then"
							+ " count(1) else 0 end cnt6,"
							+ " case when rq.basic_pension_amount > 6575 and"
							+ " rq.basic_pension_amount <= 7880 then"
							+ " count(1) else 0 end cnt7,"
							+ " case when rq.basic_pension_amount > 7880 and"
							+ " rq.basic_pension_amount <= 8660 then"
							+ " count(1) else 0 end cnt8,"
							+ " case when rq.basic_pension_amount > 8660 then"
							+ " count(1) else 0 end cnt9,"
							+ " rq.head_code x  from trn_pension_rqst_hdr rq, trn_pnsncase_rop_rlt po"
							+ " where rq.ppo_no = po.ppo_no AND po.rop_paid = 'Y' ");
			if (lStrROPType.equalsIgnoreCase("R")) {
				lSBQuery.append(" AND po.rop = 1996 ");
			} else {
				lSBQuery.append(" AND po.rop != 1996 ");
			}
			if (lStrSeenType != null) {
				lSBQuery.append(" AND rq.seen_flag =:seenFlag ");
			}
			if (lDateFrom != null && lDateTo != null) {
				lSBQuery
						.append(" AND rq.ppo_inward_date between :lDateFrom AND :lDateTo ");
			}
			lSBQuery
					.append(" AND rq.head_code IN(13, 14, 21, 22) and rq.location_code =:locCode ");
			lSBQuery
					.append(" group by rq.pension_type, rq.basic_pension_amount, rq.head_code) temp "
							+ "group by typ ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.addScalar("TYP", Hibernate.STRING);
			lQuery.addScalar("APT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("BPT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("CPT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("DPT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("EPT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("FPT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("GPT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("HPT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("IPT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("APE", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("BPE", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("CPE", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("DPE", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("EPE", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("FPE", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("GPE", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("HPE", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("IPE", Hibernate.BIG_DECIMAL);

			if (lStrSeenType != null) {
				lQuery.setParameter("seenFlag", lStrSeenType);
			}
			if (lDateFrom != null && lDateTo != null) {
				lQuery.setDate("lDateFrom", lDateFrom);
				lQuery.setDate("lDateTo", lDateTo);
			}
			lQuery.setParameter("locCode", lStrLocCode);
			lLstRes = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstRes;
	}

	public List getPaidDtlsBetweenTwoDates(String lStrPPONo, Date fromDate,
			Date toDate, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List lLstRes = new ArrayList();
		try {
			lSBQuery
					.append(" SELECT TPD.PAY_FOR_MONTH,TPD.REDUCED_PENSION,TPD.ARREAR_AMOUNT,TPD.TI_ARREAR_AMOUNT,TPD.FROM_DATE,TPD.To_DATE "
							+ " FROM TRN_PENSION_BILL_DTLS TPD,TRN_PENSION_BILL_HDR TPH "
							+ " WHERE TPD.TRN_PENSION_BILL_HDR_ID = TPH.TRN_PENSION_BILL_HDR_ID "
							+ " AND TPD.PPO_NO =:ppoNo "
							+ " AND TPH.rejected != 1"
							+ " AND TPH.BILL_TYPE IN ('Monthly','PENSION') "
							+ " AND TPH.FOR_MONTH = TPD.PAY_FOR_MONTH "
							+ " AND TPD.to_date BETWEEN :fromDate AND :toDate AND TPH.location_code = :locCode ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("ppoNo", lStrPPONo);
			lQuery.setParameter("fromDate", fromDate);
			lQuery.setParameter("toDate", toDate);
			lQuery.setParameter("locCode", lStrLocCode);
			lLstRes = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstRes;
	}

	// krupa
	public ArrayList getPensionerListAfter15Days(String locCode)
			throws Exception {

		StringBuilder strQuery = new StringBuilder();
		ArrayList lArrLstIn;
		ArrayList lArrLstOut = new ArrayList();

		/*
		 * select TPR.PPO_NO, MH.FIRST_NAME || ' ' || MH.MIDDLE_NAME || ' ' ||
		 * MH.LAST_NAME, MH.PENSNER_ADDR, mh.date_of_death, fd.name FROM
		 * TRN_PENSION_RQST_HDR TPR, MST_PENSIONER_HDR MH left join
		 * mst_pensioner_family_dtls fd on fd.pensioner_code = mh.pensioner_code
		 * and fd.percentage = 100 and mh.date_of_death is not null WHERE
		 * (SYSDATE - TPR.PPO_INWARD_DATE) > 15 AND TPR.PENSIONER_CODE =
		 * MH.PENSIONER_CODE AND tpr.location_code = 10055 and
		 * tpr.curr_case_status < 5 and tpr.case_status = mh.case_status and
		 * tpr.approve_status != 'CONVERTED'
		 */
		// MH.PENSIONER_CODE=9910055908
		try {
			Session ghibSession = getReadOnlySession();

			if (bundleApplicationDB.getString("DBTYPE").equalsIgnoreCase(
					"mysql")) {
				strQuery
						.append(" select TPR.PPO_NO lPpoNo,mh.first_Name FNAME,"
								+ " MH.PENSNER_ADDR lAddr, "
								+ " mh.date_of_death lDOD,fd.name lFPName,mh.middle_name MNAME,mh.last_Name LSNAME FROM TRN_PENSION_RQST_HDR TPR,MST_PENSIONER_HDR mh left join "
								+ " mst_pensioner_family_dtls fd on fd.pensioner_code=mh.pensioner_code and fd.percentage=:fpPercent "
								+ " and mh.date_of_death is not null "
								+ " WHERE (to_days(:currDt) -to_days(TPR.PPO_INWARD_DATE)) > :lDays "
								+ " AND TPR.PENSIONER_CODE = mh.PENSIONER_CODE AND "
								+ " tpr.location_code=:locCode and .tpr.curr_case_status< :currCaseSt and tpr.case_status = mh.case_status "
								+ " and tpr.approve_status != :caseStatus ");

			} else {
				strQuery
						.append(" select TPR.PPO_NO lPpoNo,mh.first_Name FNAME,"
								+ " MH.PENSNER_ADDR lAddr, "
								+ " mh.date_of_death lDOD,fd.name lName,mh.middle_name MNAME,mh.last_Name LSNAME FROM TRN_PENSION_RQST_HDR TPR,MST_PENSIONER_HDR mh left join "
								+ " mst_pensioner_family_dtls fd on fd.pensioner_code=mh.pensioner_code and fd.percentage=:fpPercent "
								+ " and mh.date_of_death is not null "
								+ " WHERE (TPR.PPO_INWARD_DATE + :lDays) > :currDt  AND TPR.PENSIONER_CODE = mh.PENSIONER_CODE AND "
								+ " tpr.location_code=:locCode and tpr.curr_case_status< :currCaseSt and tpr.case_status = mh.case_status "
								+ " and tpr.approve_status != :caseStatus ");
			}

			SQLQuery sqlQuery = ghibSession.createSQLQuery(strQuery.toString());

			sqlQuery.setInteger("fpPercent", 100);
			sqlQuery.setInteger("lDays", 15);
			sqlQuery.setString("locCode", locCode);
			sqlQuery.setString("caseStatus", "CONVERTED");
			sqlQuery.setInteger("currCaseSt", 5);
			sqlQuery.setDate("currDt", SessionHelper.getCurDate());

			sqlQuery.addScalar("lPpoNo", Hibernate.STRING);
			sqlQuery.addScalar("FNAME", Hibernate.STRING);
			sqlQuery.addScalar("lAddr", Hibernate.STRING);
			sqlQuery.addScalar("lDOD", Hibernate.DATE);
			sqlQuery.addScalar("lFPName", Hibernate.STRING);
			sqlQuery.addScalar("MNAME", Hibernate.STRING);
			sqlQuery.addScalar("LSNAME", Hibernate.STRING);

			List resultList = sqlQuery.list();
			if (resultList != null && !resultList.isEmpty()) {
				for (int recNo = 0; recNo < resultList.size(); recNo++) {
					lArrLstIn = new ArrayList();
					Object[] tuple = (Object[]) resultList.get(recNo);

					lArrLstIn.add(tuple[0]); // ppoNo---lArrLstIn[0]

					if (tuple[2] == null)// Pensioner's Address---lArrLstIn[1]
					{
						lArrLstIn.add("");
					} else {
						lArrLstIn.add(tuple[2]);
					}

					if (tuple[3] == null)// Pensioner's death date
					{
						StringBuffer lStrPenName = new StringBuffer();
						lStrPenName.append(tuple[1] != null ? tuple[1] + " "
								: "");
						lStrPenName.append(tuple[5] != null ? tuple[5] + " "
								: "");
						lStrPenName.append(tuple[6] != null ? tuple[6] : "");

						lArrLstIn.add(lStrPenName.toString());// name
					} else {

						if (tuple[4] == null)// Pensioner's family member's name
						{
							StringBuffer lStrPenName = new StringBuffer();
							lStrPenName.append(tuple[1] != null ? tuple[1]
									+ " " : "");
							lStrPenName.append(tuple[5] != null ? tuple[5]
									+ " " : "");
							lStrPenName
									.append(tuple[6] != null ? tuple[6] : "");

							lArrLstIn.add(lStrPenName.toString());// name
						} else {
							lArrLstIn.add(tuple[4]);
						}
					}

					lArrLstOut.add(lArrLstIn);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lArrLstOut;
	}

	public List getRecoverChallans(String lStrPensionerCode, Integer lForMonth)
			throws Exception {

		List resultList;
		List<TrnPensionRecoveryDtls> lLstTrnPnsnRcryDtls = new ArrayList<TrnPensionRecoveryDtls>();
		try {
			Session ghibSession = getReadOnlySession();
			StringBuilder strQuery = new StringBuilder();

			strQuery
					.append(" SELECT TPR.pensionerCode, TPR.recoveryType, TPR.accountNumber, TPR.edpCode, TPR.mjrhdCode, "
							+ " TPR.submjrhdCode, TPR.minhdCode, TPR.subhdCode, TPR.amount  "
							+ " FROM TrnPensionRecoveryDtls TPR WHERE TPR.pensionerCode = :pensionerCode  ");

			if (lForMonth != null && lForMonth > 0) {
				strQuery
						.append(" AND :ForMonth BETWEEN TPR.fromMonth and toMonth ");
			}

			strQuery
					.append(" ORDER BY TPR.recoveryType, TPR.edpCode, TPR.mjrhdCode, TPR.submjrhdCode,  "
							+ " TPR.minhdCode, TPR.subhdCode ");

			Query hqlQuery = ghibSession.createQuery(strQuery.toString());

			hqlQuery.setString("pensionerCode", lStrPensionerCode);
			if (lForMonth != null && lForMonth > 0) {
				hqlQuery.setInteger("ForMonth", lForMonth);
			}

			resultList = hqlQuery.list();

			if (resultList != null && !resultList.isEmpty()) {
				Iterator it = resultList.iterator();
				while (it.hasNext()) {
					Object tuple[] = (Object[]) it.next();
					if (tuple[0] != null) {
						TrnPensionRecoveryDtls lObjVo = new TrnPensionRecoveryDtls();
						lObjVo.setPensionerCode(tuple[0].toString());
						lObjVo.setRecoveryType(tuple[1].toString());

						if (tuple[2] != null) {
							lObjVo.setAccountNumber(tuple[2].toString());
						}
						if (tuple[3] != null) {
							lObjVo.setEdpCode(tuple[3].toString());
						}
						if (tuple[4] != null) {
							lObjVo.setMjrhdCode(tuple[4].toString());
						}
						if (tuple[5] != null) {
							lObjVo.setSubmjrhdCode(tuple[5].toString());
						}
						if (tuple[6] != null) {
							lObjVo.setMinhdCode(tuple[6].toString());
						}
						if (tuple[7] != null) {
							lObjVo.setSubhdCode(tuple[7].toString());
						}
						if (tuple[8] != null) {
							lObjVo
									.setAmount(new BigDecimal(tuple[8]
											.toString()));
						}
						lLstTrnPnsnRcryDtls.add(lObjVo);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstTrnPnsnRcryDtls;
	}

	public List getDtlsAnnexure2(String lStrPenReqId, String lStrLocCode)
			throws Exception {

		StringBuffer lSBQry = new StringBuffer();
		Session ghibSession = getReadOnlySession();
		List lLstRes = new ArrayList();

		/*
		 * select distinct rq.ppo_no, rq.dp_percent, mhd.first_name,
		 * mhd.middle_name, mhd.last_name, mhd.date_of_birth,
		 * mhd.date_of_retirement, mhd.date_of_death, fd.name as lName,
		 * fd.date_of_death as fpDOD, mdt.acount_no, mdt.bank_code,
		 * mdt.branch_code, ardt.old_amount_percentage,
		 * concat((concat(mb.bank_name, ' ')), rbb.branch_name),
		 * rq.pension_request_id, ardt.created_date, rq.fp1_date, rq.fp1_amount,
		 * rq.fp2_date, rq.fp2_amount, rq.basic_pension_amount,
		 * e.emp_Fname,e.emp_Mname,e.emp_Lname, mhd.pensner_addr from
		 * trn_pension_rqst_hdr rq left join trn_pension_arrear_dtls ardt on
		 * rq.pensioner_code = ardt.pensioner_code and rq.pension_request_id =
		 * ardt.pension_request_id and ardt.arrear_field_type = 'Pension_2006',
		 * mst_pensioner_hdr mhd left join mst_pensioner_family_dtls fd on
		 * fd.pensioner_code = mhd.pensioner_code and fd.percentage = 100,
		 * mst_pensioner_dtls mdt, rlt_bank_branch rbb, mst_bank mb, wf_job_mst
		 * wfj, org_emp_mst e, Org_Userpost_Rlt our where rq.case_status =
		 * 'APPROVED' and rq.pensioner_code = mhd.pensioner_code and
		 * rq.case_status = mhd.case_status and mhd.pensioner_code =
		 * mdt.pensioner_code and rq.location_code = mhd.location_code and
		 * mhd.case_status = mdt.case_status and mdt.active_flag = 'Y' and
		 * mdt.location_code = mhd.location_code and rbb.lang_id = e.lang_id and
		 * rbb.branch_code = mdt.branch_code and rbb.bank_code = mdt.bank_code
		 * and rq.location_code = rbb.location_code and rbb.location_code =
		 * '10055' and mb.bank_code = rbb.bank_code and mb.lang_id = rbb.lang_id
		 * and rq.pension_request_id in (99100553534) and wfj.doc_id='210003'
		 * and wfj.job_Ref_Id_num = rq.pension_Request_Id and our.post_Id =
		 * wfj.lst_Act_Post_Id_num and our.user_Id = e.user_Id and wfj.loc_id =
		 * rq.location_code and e.lang_Id = 1 order by ardt.created_date
		 */

		try {
			lSBQry
					.append(" select distinct rq.ppo_no ppoNo,rq.dp_percent dpPer,mhd.first_name fname,mhd.middle_name mName,mhd.last_name lName,"
							+ " mhd.date_of_birth pDOB, "
							+ "mhd.date_of_retirement pDOR,mhd.date_of_death mdDOD,fd.name fpName,fd.date_of_death fpDOD,mdt.acount_no accntNo, "
							+ " mdt.bank_code bnkCode, "
							+ "mdt.branch_code brnchCode,ardt.old_amount_percentage oldAmnt,"
							+ " concat((concat(mb.bank_name, ' ')), rbb.branch_name) bnkName,rq.pension_request_id reqstId, "
							+ "rq.fp1_date fp1dt,rq.fp1_amount fp1Amnt,rq.fp2_date fp2Dt,rq.fp2_amount fp2Amnt,rq.basic_pension_amount basic,"
							+ "e.emp_Fname efname, "
							+ "e.emp_Mname emName,e.emp_Lname elName,fd.date_of_birth fpDOB ,mhd.pensner_addr lAddr "
							+ "from trn_pension_rqst_hdr rq left join trn_pension_arrear_dtls ardt on rq.pensioner_code = ardt.pensioner_code "
							+ "and rq.pension_request_id = ardt.pension_request_id and ardt.arrear_field_type = :fieldType, mst_pensioner_hdr mhd "
							+ "left join mst_pensioner_family_dtls fd on fd.pensioner_code = mhd.pensioner_code and fd.percentage =:fpPercent, "
							+ "mst_pensioner_dtls mdt,rlt_bank_branch rbb,mst_bank mb,wf_job_mst wfj,org_emp_mst e,Org_Userpost_Rlt our "
							+ "where rq.case_status = :lstatus and rq.pensioner_code = mhd.pensioner_code and rq.case_status = mhd.case_status and "
							+ "mhd.pensioner_code = mdt.pensioner_code and rq.location_code = mhd.location_code and mhd.case_status = mdt.case_status "
							+ "and mdt.active_flag = :lActvFlg and mdt.location_code = mhd.location_code and rbb.lang_id = e.lang_id and "
							+ "rbb.branch_code = mdt.branch_code and rbb.bank_code = mdt.bank_code and  rq.location_code = rbb.location_code "
							+ "and rbb.location_code = :locCode and mb.bank_code = rbb.bank_code and mb.lang_id = rbb.lang_id and rq.pension_request_id "
							+ "in ("
							+ lStrPenReqId
							+ ") and wfj.doc_id=:docId and wfj.job_Ref_Id_num = rq.pension_Request_Id and our.post_Id = "
							+ "wfj.lst_Act_Post_Id_num and our.user_Id = e.user_Id and our.activate_flag=1 and e.activate_flag=1 and  wfj.loc_id = rq.location_code and e.lang_Id = :langId ");

			SQLQuery sqlQuery = ghibSession.createSQLQuery(lSBQry.toString());

			sqlQuery.addScalar("ppoNo", Hibernate.STRING);
			sqlQuery.addScalar("dpPer", Hibernate.BIG_DECIMAL);
			sqlQuery.addScalar("fname", Hibernate.STRING);
			sqlQuery.addScalar("mName", Hibernate.STRING);
			sqlQuery.addScalar("lName", Hibernate.STRING);
			sqlQuery.addScalar("pDOB", Hibernate.DATE);
			sqlQuery.addScalar("pDOR", Hibernate.DATE);
			sqlQuery.addScalar("mdDOD", Hibernate.DATE);
			sqlQuery.addScalar("fpName", Hibernate.STRING);
			sqlQuery.addScalar("fpDOD", Hibernate.DATE);
			sqlQuery.addScalar("accntNo", Hibernate.STRING);
			sqlQuery.addScalar("bnkCode", Hibernate.STRING);
			sqlQuery.addScalar("brnchCode", Hibernate.STRING);
			sqlQuery.addScalar("oldAmnt", Hibernate.BIG_DECIMAL);
			sqlQuery.addScalar("bnkName", Hibernate.STRING);
			sqlQuery.addScalar("reqstId", Hibernate.LONG);
			sqlQuery.addScalar("fp1dt", Hibernate.DATE);
			sqlQuery.addScalar("fp1Amnt", Hibernate.BIG_DECIMAL);
			sqlQuery.addScalar("fp2Dt", Hibernate.DATE);
			sqlQuery.addScalar("fp2Amnt", Hibernate.BIG_DECIMAL);
			sqlQuery.addScalar("basic", Hibernate.BIG_DECIMAL);
			sqlQuery.addScalar("efname", Hibernate.STRING);
			sqlQuery.addScalar("emName", Hibernate.STRING);
			sqlQuery.addScalar("elName", Hibernate.STRING);
			sqlQuery.addScalar("fpDOB", Hibernate.DATE);
			sqlQuery.addScalar("lAddr", Hibernate.STRING);

			sqlQuery.setString("fieldType", "Pension_2006");
			sqlQuery.setLong("fpPercent", 100);
			sqlQuery.setString("lstatus", "APPROVED");
			sqlQuery.setString("lActvFlg", "Y");
			sqlQuery.setLong("langId", 1);
			sqlQuery.setString("locCode", lStrLocCode);
			sqlQuery.setString("docId", "210003");

			lLstRes = sqlQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lLstRes;
	}

	public List getPensionExpChq_ECSDtls(String lFromDate, String lToDate,
			String lStrLocId) throws Exception {

		List lResLst = new ArrayList();
		StringBuffer lSBQry = new StringBuffer();
		Session ghibSession = getReadOnlySession();
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		try {
			lSBQry
					.append("	SELECT CD.CHEQUE_TYPE, SUM(BR.BILL_GROSS_AMOUNT) gross, SUM(RD.AMOUNT) ded, "
							+ " SUM(BR.BILL_GROSS_AMOUNT - NVL(RD.AMOUNT, 0)) net "
							+ " FROM TRN_CHEQUE_DTLS   CD, RLT_BILL_CHEQUE   BC, TRN_BILL_REGISTER BR LEFT "
							+ "JOIN RPT_RECEIPT_DTLS RD ON RD.RCPT_NO = BR.BILL_NO AND RD.MJR_HD != 0071"
							+ " WHERE BC.CHEQUE_ID = CD.CHEQUE_ID AND BR.BILL_NO = BC.BILL_NO AND"
							+ " CD.FROM_DT >= :lFromDate AND CD.FROM_DT <= :lToDate AND"
							+ " CD.LOCATION_CODE = :lLocId GROUP BY CD.CHEQUE_TYPE");

			Query sqlQuery = ghibSession.createSQLQuery(lSBQry.toString());

			sqlQuery.setDate("lFromDate", lObjDateFormat.parse(lFromDate));
			sqlQuery.setDate("lToDate", lObjDateFormat.parse(lToDate));
			sqlQuery.setString("lLocId", lStrLocId);

			lResLst = sqlQuery.list();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lResLst;
	}

	public List getCommonRptHeader(String lStrLocId, long lLangId)
			throws Exception {

		List resList = null;

		try {
			Session ghibSession = getReadOnlySession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery
					.append(" select l.loc_name, l.loc_addr_1, l.loc_addr_2 "
							+ " from cmn_location_mst l "
							+ " where l.lang_id = :lLangId AND l.location_code = :lStrLocId ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setParameter("lStrLocId", lStrLocId);
			lQuery.setParameter("lLangId", lLangId);

			List lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty()) {
				resList = lLstVO;
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return resList;

	}

	public List getPPOsToObtainCountForBankWise(Integer lIntFromMonth,
			Integer lIntToMonth, String lStrBank, String lStrBranch,
			String locationCode) throws Exception {
		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List lLstRes = new ArrayList();
		new ArrayList();
		List<String> lLstTempPPOs = new ArrayList<String>();
		List lLstTempBanks = new ArrayList<String>();
		List<BigDecimal> lLstRedPension = new ArrayList<BigDecimal>();
		try {
			lSBQuery
					.append(" SELECT  distinct a.ppo_no ppoNo,MB.BANK_NAME BankName,sum(a.reduced_pension) redPension ");
			lSBQuery
					.append(" FROM TRN_PENSION_BILL_DTLS A "
							+ " JOIN TRN_PENSION_BILL_HDR  B ON B.TRN_PENSION_BILL_HDR_ID = A.TRN_PENSION_BILL_HDR_ID "
							+ " AND (B.rejected != 1  OR B.rejected is null ) "
							+ " LEFT OUTER JOIN MST_BANK MB ON MB.BANK_CODE = B.BANK_CODE AND MB.LANG_ID = 1 "
							+ " JOIN RLT_BANK_BRANCH RBB ON (RBB.BRANCH_CODE) = B.Branch_Code "
							+ " WHERE B.FOR_MONTH BETWEEN :lIntFromMonth AND :lIntToMonth "
							+ " AND RBB.LOCATION_CODE = :locationCode ");

			if (lStrBank != null && lStrBank.length() > 0) {
				lSBQuery.append(" AND B.BANK_CODE = :lStrBank ");
			}
			if (lStrBranch != null && lStrBranch.length() > 0) {
				lSBQuery.append(" AND B.BRANCH_CODE = :lStrBranch ");
			}
			lSBQuery
					.append(" AND b.bill_Type in ('Monthly','CVP','DCRG') AND B.FOR_MONTH = A.PAY_FOR_MONTH AND B.LOCATION_CODE=:locationCode"
							+ " GROUP BY a.ppo_no,BankName");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setLong("lIntFromMonth", lIntFromMonth);
			lQuery.setLong("lIntToMonth", lIntToMonth);
			lQuery.setString("locationCode", locationCode);

			if (lStrBank != null && lStrBank.length() > 0) {
				lQuery.setString("lStrBank", lStrBank);
			}
			if (lStrBranch != null && lStrBranch.length() > 0) {
				lQuery.setLong("lStrBranch", Long.valueOf(lStrBranch));
			}

			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.addScalar("ppoNo", Hibernate.STRING);
			lQuery.addScalar("BankName", Hibernate.STRING);
			lQuery.addScalar("redPension", Hibernate.BIG_DECIMAL);

			lLstRes = lQuery.list();

			Object tuple[] = null;
			for (int i = 0; i < lLstRes.size(); i++) {
				tuple = (Object[]) lLstRes.get(i);
				lLstTempPPOs.add(tuple[0].toString());
				lLstTempBanks.add(tuple[1].toString());
				lLstRedPension.add(new BigDecimal(tuple[2].toString()));
			}
			lLstRes = new ArrayList();
			lLstRes.add(lLstTempPPOs);
			lLstRes.add(lLstTempBanks);
			lLstRes.add(lLstRedPension);
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstRes;

	}

	public List getPPOsToObtainCountForBranchWise(Integer lIntFromMonth,
			Integer lIntToMonth, String lStrBank, String lStrBranch,
			String locationCode) throws Exception {
		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = getReadOnlySession();
		List lLstRes = new ArrayList();
		new ArrayList();
		List<String> lLstTempPPOs = new ArrayList<String>();
		List lLstTempBanks = new ArrayList<String>();
		List<BigDecimal> lLstRedPension = new ArrayList<BigDecimal>();
		try {
			lSBQuery
					.append(" SELECT  distinct a.ppo_no ppoNo,B.Branch_Code BranchCode,sum(a.reduced_pension) redPension");
			lSBQuery
					.append(" FROM TRN_PENSION_BILL_DTLS A "
							+ " JOIN TRN_PENSION_BILL_HDR  B ON B.TRN_PENSION_BILL_HDR_ID = A.TRN_PENSION_BILL_HDR_ID "
							+ " AND (B.rejected != 1  OR B.rejected is null ) "
							+ " LEFT OUTER JOIN MST_BANK MB ON MB.BANK_CODE = B.BANK_CODE AND MB.LANG_ID = 1 "
							+ " JOIN RLT_BANK_BRANCH RBB ON (RBB.BRANCH_CODE) = B.Branch_Code "
							+ " WHERE B.FOR_MONTH BETWEEN :lIntFromMonth AND :lIntToMonth "
							+ " AND RBB.LOCATION_CODE = :locationCode ");

			if (lStrBank != null && lStrBank.length() > 0) {
				lSBQuery.append(" AND B.BANK_CODE = :lStrBank ");
			}
			if (lStrBranch != null && lStrBranch.length() > 0) {
				lSBQuery.append(" AND B.BRANCH_CODE = :lStrBranch ");
			}
			lSBQuery
					.append(" AND b.bill_Type in ('Monthly','CVP','DCRG') AND B.FOR_MONTH = A.PAY_FOR_MONTH AND B.LOCATION_CODE=:locationCode"
							+ " group by a.ppo_no,B.Branch_Code ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setLong("lIntFromMonth", lIntFromMonth);
			lQuery.setLong("lIntToMonth", lIntToMonth);
			lQuery.setString("locationCode", locationCode);

			if (lStrBank != null && lStrBank.length() > 0) {
				lQuery.setString("lStrBank", lStrBank);
			}
			if (lStrBranch != null && lStrBranch.length() > 0) {
				lQuery.setLong("lStrBranch", Long.valueOf(lStrBranch));
			}

			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.addScalar("ppoNo", Hibernate.STRING);
			lQuery.addScalar("BranchCode", Hibernate.STRING);
			lQuery.addScalar("redPension", Hibernate.BIG_DECIMAL);

			lLstRes = lQuery.list();

			Object tuple[] = null;
			for (int i = 0; i < lLstRes.size(); i++) {
				tuple = (Object[]) lLstRes.get(i);
				lLstTempPPOs.add(tuple[0].toString());
				lLstTempBanks.add(tuple[1].toString());
				lLstRedPension.add(new BigDecimal(tuple[2].toString()));
			}
			lLstRes = new ArrayList();
			lLstRes.add(lLstTempPPOs);
			lLstRes.add(lLstTempBanks);
			lLstRes.add(lLstRedPension);
		} catch (Exception e) {
			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstRes;
	}

	public List getBillAndPartyDtlForPaidPnsnrRprt(Date lDtFromDate,
			Date lDtToDate, List lLngBillType, String LocationCode, Long lLangId)
			throws Exception {

		List lLstResult = new ArrayList();
		try {

			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery
					.append("SELECT DISTINCT bt.subjectDesc, p.partyName, b.tokenNum, b.billNo, b.billGrossAmount, b.billNetAmount, bh.branchCode ");
			lSBQuery
					.append("FROM TrnBillRegister b, MstBillType bt, RltBillParty p, TrnPensionBillHdr bh ");
			lSBQuery
					.append("WHERE b.billDate BETWEEN :fromDate AND :toDate AND b.phyBill = 2 ");
			lSBQuery
					.append("AND b.subjectId IN (:billType) AND b.locationCode = :locCode ");
			lSBQuery
					.append("AND b.billNo = p.billNo AND bh.billNo = b.billNo AND bh.locationCode = b.locationCode "); // AND
			// b.currBillStatus
			// !=
			// -1
			lSBQuery
					.append("AND b.subjectId = bt.subjectId AND p.locationCode = b.locationCode AND bt.langId = :LangId");

			Session lHibSession = getReadOnlySession();

			Query lQuery = lHibSession.createQuery(lSBQuery.toString());

			lQuery.setDate("fromDate", lDtFromDate);
			lQuery.setDate("toDate", lDtToDate);

			lQuery.setString("locCode", LocationCode);
			lQuery.setLong("LangId", lLangId);
			lQuery.setParameterList("billType", lLngBillType);

			lLstResult = lQuery.list();

		} catch (Exception e) {

			logger.error("Error is." + e, e);
			throw e;
		}
		return lLstResult;
	}

	public BigDecimal getMandateSerialNo() throws Exception {
		StringBuffer lSBQuery = new StringBuffer();
		BigDecimal lBgdcSerailNo = BigDecimal.ZERO;

		try {
			Session ghibSession = getSession();
			/*
			 * char locCode1 = locCode.charAt(locCode.length() - 1); char
			 * locCode2 = locCode.charAt(locCode.length() - 2); String
			 * locationCode = String.valueOf(locCode2) + locCode1;
			 */
			// if (lIntCurrentMonth.length() == 1) {
			// lIntCurrentMonth = "0" + lIntCurrentMonth;
			// }
			lSBQuery
					.append(" SELECT MAX(mandate_Serial_no) mansrno FROM TRN_ECS_DTL");
			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.addScalar("mansrno", Hibernate.BIG_INTEGER);

			if (lQuery.uniqueResult() != null) {
				lBgdcSerailNo = BigDecimal.valueOf(Long.valueOf(lQuery
						.uniqueResult().toString()));
			} else {
				lBgdcSerailNo = BigDecimal.valueOf(Long.valueOf(0));
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lBgdcSerailNo.add(BigDecimal.ONE);
	}

	public void setMandateSerialNo(String lStrECSCode,
			BigDecimal lBgdcMandteSerialNo) throws Exception

	{
		StringBuffer lSBQuery = new StringBuffer();
		Session ghibSession = getSession();
		try {
			lSBQuery
					.append("UPDATE TRN_ECS_DTL SET mandate_Serial_no = :lBgdcMandteSerialNo WHERE ecs_code=:lStrECSCode");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("lBgdcMandteSerialNo", lBgdcMandteSerialNo);
			lQuery.setParameter("lStrECSCode", lStrECSCode);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.report.CommonReportDAO#getEmpNameFromRoleId(java.util.Map, java.lang.String, java.lang.String)
	 */
	
	public String getEmpNameFromRoleId(String lStrRoleId,String lStrLocCode) 
	{
		String lStrEmpName = null;
		Query lHibQry  = null;
		StringBuffer lSBQuery = null;
		Session lHibSession = null;
		try
		{
			lHibSession = getSession();
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT concat(concat(concat(concat(oem.empFname,' '),oem.empMname),' '),oem.empLname) \n");
			lSBQuery.append(" FROM AclRoleMst arm,AclPostroleRlt apr,OrgUserpostRlt oup,OrgEmpMst oem, OrgPostMst opm \n");
			lSBQuery.append(" WHERE arm.roleId=apr.aclRoleMst.roleId \n");
			lSBQuery.append(" AND oup.orgPostMstByPostId.postId=apr.orgPostMst.postId \n");
			lSBQuery.append(" AND oup.orgUserMst.userId = oem.orgUserMst.userId \n");
			lSBQuery.append(" AND oup.orgPostMstByPostId.postId=opm.postId\n");
			lSBQuery.append(" AND arm.roleId=:roleId \n");
			lSBQuery.append(" AND opm.locationCode=:locationCode \n");

			lHibQry = lHibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("locationCode", lStrLocCode);
			lHibQry.setParameter("roleId",Long.valueOf(lStrRoleId));
			lStrEmpName = lHibQry.list().get(0).toString();
			
		}
		catch (Exception e) 
		{
			//e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lStrEmpName;
	}

}
