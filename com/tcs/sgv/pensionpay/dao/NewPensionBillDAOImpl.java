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
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import com.tcs.sgv.pensionpay.constants.PensionConstants;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeRate;
import com.tcs.sgv.pensionpay.valueobject.RltPensionRevisedPayment;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionItCutDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPnsncaseRopRlt;


public class NewPensionBillDAOImpl implements NewPensionBillDAO {

	/* Global Variable for Logger Class */
	private Log logger = LogFactory.getLog(getClass());

	private SessionFactory sessionFactory = null;

	public NewPensionBillDAOImpl(SessionFactory sessionFactory) {

		this.sessionFactory = sessionFactory;
	}

	public Session getSession() {

		boolean allowCreate = true;
		return SessionFactoryUtils.getSession(sessionFactory, allowCreate);
	}

	/**
	 * get {@link TrnPensionRqstHdr} VO
	 * 
	 * Written By Sagar
	 */

	public TrnPensionRqstHdr getPensionRqstHdrDtls(String lStrPnsnerCode, String lStrStatus, String lStrLocCode) throws Exception {

		TrnPensionRqstHdr lObjTrnPensionRqstHdr = null;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM TrnPensionRqstHdr A WHERE A.pensionerCode = :lPnsnerCode AND A.status = :lStatus AND A.locationCode = :locationCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPnsnerCode", lStrPnsnerCode);
			lQuery.setString("lStatus", lStrStatus);
			lQuery.setString("locationCode", lStrLocCode);

			List resultList = lQuery.list();
			if (!resultList.isEmpty()) {
				lObjTrnPensionRqstHdr = (TrnPensionRqstHdr) resultList.get(0);
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lObjTrnPensionRqstHdr;
	}

	/**
	 * get {@link TrnPensionItCutDtls} VO
	 * 
	 * Written By Sagar
	 */

	public ArrayList<TrnPensionItCutDtls> getTrnPensionItCutDtls(String lStrPensionerCode) throws Exception {

		List<TrnPensionItCutDtls> lLstobjTrnPensionItCutDtls = new ArrayList<TrnPensionItCutDtls>();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM  TrnPensionItCutDtls H WHERE H.pensionerCode = :lPnsrCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPnsrCode", lStrPensionerCode);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				for (int i = 0; i < lLstVO.size(); i++) {
					lLstobjTrnPensionItCutDtls.add((TrnPensionItCutDtls) lLstVO.get(i));
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return (ArrayList<TrnPensionItCutDtls>) lLstobjTrnPensionItCutDtls;
	}

	/**
	 * get {@link TrnPensionRecoveryDtls} VO
	 * 
	 * Written By Sagar
	 */

	public Double getTrnPensionRecoveryDtls(String lStrPensionerCode, String lStrStatus) throws Exception {

		Double lPensionRecovery = 0D;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT SUM(A.amount) FROM TrnPensionRecoveryDtls A WHERE A.pensionerCode = :lPnsrCode AND A.recoveryFromFlag = :lStatus ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPnsrCode", lStrPensionerCode);
			lQuery.setString("lStatus", lStrStatus);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				if (lLstVO.get(0) != null) {
					lPensionRecovery = Double.valueOf(lLstVO.get(0).toString());
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lPensionRecovery;
	}

	/**
	 * get Totat Recovery For the Given Month.
	 * 
	 * Written By Sagar
	 */
	public Double getTotalRecoveryForMonth(String lStrPensionerCode, int ForMonth) throws Exception {

		Double lTotalRecoveryAmt = 0D;
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT sum(rc.amount) FROM TrnPensionRecoveryDtls rc WHERE rc.pensionerCode = :lPensionerCode And rc.fromMonth <= :lForMonth AND rc.toMonth >= :lForMonth");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lPensionerCode", lStrPensionerCode);
			lQuery.setInteger("lForMonth", ForMonth);
			List lLstVO = lQuery.list();
			if (!lLstVO.isEmpty()) {
				if (lLstVO.get(0) != null) {
					lTotalRecoveryAmt = Double.valueOf(lLstVO.get(0).toString());
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lTotalRecoveryAmt;
	}

	/**
	 * get Totat Pension Cut (PT And PP) For the Given Month.
	 * 
	 * Written By Sagar
	 */
	public Double getTotalPensionCutForMonth(String lStrPensionerCode, int ForMonth) throws Exception {

		Double lTotalPensionCutAmt = 0D;
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT SUM(it.amount) FROM TrnPensionItCutDtls it WHERE it.pensionerCode = :lPensionerCode"
					+ " AND (it.typeFlag = :lPTCut AND it.fromMonth <= :lForMonth AND it.toMonth >= :lForMonth) OR (it.typeFlag = :lPPCut AND it.pensionerCode = :lPensionerCode)");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPTCut", PensionConstants.CUTPT);
			lQuery.setString("lPPCut", PensionConstants.CUTPP);
			lQuery.setString("lPensionerCode", lStrPensionerCode);
			lQuery.setInteger("lForMonth", ForMonth);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				if (lLstVO.get(0) != null) {
					lTotalPensionCutAmt = Double.valueOf(lLstVO.get(0).toString());
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lTotalPensionCutAmt;
	}

	/**
	 * get Totat Other Benefit (PermanentBenefit & TemporaryBenefit) For the
	 * Given Month.
	 * 
	 * Written By Sagar
	 */
	public Double getTotalOtherBenefitsForMonth(String lStrPensionerCode, int ForMonth) throws Exception {

		Double lTotalBenefitsAmt = 0D;
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT SUM(it.amount) FROM TrnPensionItCutDtls it WHERE it.pensionerCode = :lPensionerCode"
					+ " AND (it.typeFlag = :lTBenefit AND it.fromMonth <= :lForMonth AND it.toMonth >= :lForMonth) OR (it.typeFlag = :lPBenefit AND it.pensionerCode = :lPensionerCode)");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPBenefit", PensionConstants.CMNPBENEFIT);
			lQuery.setString("lTBenefit", PensionConstants.CMNTBENEFIT);
			lQuery.setString("lPensionerCode", lStrPensionerCode);
			lQuery.setInteger("lForMonth", ForMonth);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				if (lLstVO.get(0) != null) {
					lTotalBenefitsAmt = Double.valueOf(lLstVO.get(0).toString());
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lTotalBenefitsAmt;
	}

	/**
	 * get Totat Special Cut (SP) For the Given Month.
	 * 
	 * Written By Sagar
	 */
	public Double getTotalSpecialCutForMonth(String lStrPensionerCode, int ForMonth) throws Exception {

		Double lTotalSpecialCutAmt = 0D;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT SUM(it.amount) FROM TrnPensionItCutDtls it WHERE it.pensionerCode = :lPensionerCode"
					+ " AND it.typeFlag = :lSTCut AND it.fromMonth <= :lForMonth AND it.toMonth >= :lForMonth");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lSTCut", PensionConstants.CUTST);
			lQuery.setString("lPensionerCode", lStrPensionerCode);
			lQuery.setInteger("lForMonth", ForMonth);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				if (lLstVO.get(0) != null) {
					lTotalSpecialCutAmt = Double.valueOf(lLstVO.get(0).toString());
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lTotalSpecialCutAmt;
	}

	/**
	 * get Totat Pensioner IT Cut For the Given Month.
	 * 
	 * Written By Sagar
	 */
	public Double getTotalITCutForMonth(String lStrPensionerCode, int ForMonth) throws Exception {

		Double lTotalPnsnerITCutAmt = 0D;
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT SUM(it.amount) FROM TrnPensionItCutDtls it WHERE it.pensionerCode = :lPensionerCode"
					+ " AND it.typeFlag = :lITCut AND it.fromMonth <= :lForMonth AND it.toMonth >= :lForMonth");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lITCut", PensionConstants.CUTIT);
			lQuery.setString("lPensionerCode", lStrPensionerCode);
			lQuery.setInteger("lForMonth", ForMonth);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				if (lLstVO.get(0) != null) {
					lTotalPnsnerITCutAmt = Double.valueOf(lLstVO.get(0).toString());
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lTotalPnsnerITCutAmt;
	}

	/**
	 * get {@link TrnPensionRqstHdr} VO
	 * 
	 * Written By Sagar
	 */

	public List getRltPensioncaseBillPK(String lStrPPONO, String lStrBillType, String lStrStatus, String lStrLocCode) throws Exception {

		Iterator it = null;
		Object[] tuple = null;
		List lLstDtlsList = new ArrayList();
		Long lLngPk = null;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" select r.rlt_pensioncase_bill_id from Rlt_Pensioncase_Bill r where r.ppo_no = :lppoNo AND r.bill_type = :lBillType"
					+ " AND r.status = :lStatus AND r.bill_no is null AND r.location_code = :locationCode");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("lppoNo", lStrPPONO);
			lQuery.setString("lBillType", lStrBillType);
			lQuery.setString("lStatus", lStrStatus);
			lQuery.setString("locationCode", lStrLocCode);

			List resultList = lQuery.list();

			if (resultList != null && !resultList.isEmpty()) {
				lLngPk = Long.valueOf(resultList.get(0).toString());
				lLstDtlsList.add(lLngPk);
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lLstDtlsList;
	}

	/**
	 * get {@link TrnPensionBillDtls} VO
	 * 
	 * Written By Sagar
	 */

	public TrnPensionBillDtls getTrnPensionBillDtls(Long TrnPensionBillHdrPK) throws Exception {

		TrnPensionBillDtls lobjTrnPensionBillDtls = new TrnPensionBillDtls();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM TrnPensionBillDtls A WHERE A.trnPensionBillHdrId = :ltrnPensionBillHdrId ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("ltrnPensionBillHdrId", TrnPensionBillHdrPK);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				lobjTrnPensionBillDtls = (TrnPensionBillDtls) lLstVO.get(0);
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lobjTrnPensionBillDtls;
	}

	/**
	 * get {@link TrnPensionArrearDtls} VO
	 * 
	 * Written By Sagar
	 */
	public List<TrnPensionArrearDtls> getTrnPensionArrearDtls(String lStrPensionerCode, Long lLngBillNo) throws Exception {

		List<TrnPensionArrearDtls> lLstobjTrnPensionArrearDtls = new ArrayList<TrnPensionArrearDtls>();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM  TrnPensionArrearDtls A WHERE A.pensionerCode = :lPnsrCode AND A.billNo = :lBillNo ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPnsrCode", lStrPensionerCode);
			lQuery.setLong("lBillNo", lLngBillNo);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				for (int i = 0; i < lLstVO.size(); i++) {
					lLstobjTrnPensionArrearDtls.add((TrnPensionArrearDtls) lLstVO.get(i));
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lLstobjTrnPensionArrearDtls;
	}

	/**
	 * Feching Applied ROP(s) of Pensioner.
	 * 
	 * Written By Sagar
	 */
	public List<TrnPnsncaseRopRlt> getROPAppliedToPensner(String lStrPPONO) throws Exception {

		List<TrnPnsncaseRopRlt> lPnsnerCaseRopLst = new ArrayList<TrnPnsncaseRopRlt>();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM TrnPnsncaseRopRlt r WHERE r.ppoNo = :lPPONO ORDER BY r.rop ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPPONO", lStrPPONO);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				for (int i = 0; i < lLstVO.size(); i++) {
					lPnsnerCaseRopLst.add((TrnPnsncaseRopRlt) lLstVO.get(i));
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lPnsnerCaseRopLst;
	}

	/**
	 * Getting New ROP Amount accordign to ROP and column.
	 * 
	 * Written By Sagar
	 */

	public Double getNewBasicFromROPMst(String lStrROP, String lStrColumnNo, Double lStrOldBasic) throws Exception {

		Double lDNewBasicAmt = 0D;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			if (lStrROP != null && lStrROP.equalsIgnoreCase("1986")) {
				lSBQuery.append(" SELECT column" + lStrColumnNo + "Amount FROM MstPensionRop1986 ");
			} else if (lStrROP != null && lStrROP.equalsIgnoreCase("1996")) {
				lSBQuery.append(" SELECT newAmount FROM MstPensionRop1996 ");
			} else if (lStrROP != null && lStrROP.equalsIgnoreCase("2006")) {
				lSBQuery.append(" SELECT newAmount FROM MstPensionRop2006 ");
			}

			if (lStrROP != null && lStrROP.equalsIgnoreCase("2006") && lStrColumnNo != null && lStrColumnNo.equals("0")) {
				lSBQuery.append(" WHERE oldAmntiWthDp = :lOldBasic");
			} else {
				lSBQuery.append(" WHERE oldAmount = :lOldBasic");
			}

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.setBigDecimal("lOldBasic", new BigDecimal(lStrOldBasic));

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				if (lLstVO.get(0) != null) {
					lDNewBasicAmt = Double.valueOf(lLstVO.get(0).toString());
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lDNewBasicAmt;
	}

	/**
	 * Getting selected fileds from RltPensionHeadcodeRate for Revised Arrear
	 * Claculation and put them Map Map key : Field Type + Eff From Date + Eff
	 * To Date + PK
	 * 
	 * @param Long
	 *            lLngHeadcode
	 * @return Map
	 * @author 218580
	 */
	public Map getRateFromHeadcodeRateRlt(Long lLngHeadcode) throws Exception {

		Map<String, Object> lMapHeadCodeRate = new HashMap<String, Object>();
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT r.headCode, r.fieldType, r.effectiveFromDate, r.effectiveToDate,  ");
			lSBQuery.append(" r.rate, r.minAmount, r.uptoBasic, r.pensionHeadcodeRateId ");
			lSBQuery.append(" FROM RltPensionHeadcodeRate r WHERE r.headCode = :lHeadcode ORDER BY r.effectiveFromDate ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setBigDecimal("lHeadcode", new BigDecimal(lLngHeadcode));

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				for (int intCtr = 0; intCtr < lLstVO.size(); intCtr++) {
					Object[] lObjArr = (Object[]) lLstVO.get(intCtr);
					lMapHeadCodeRate.put(lObjArr[1] + "~" + lObjArr[2] + "~" + lObjArr[3] + "~" + lObjArr[7], lObjArr);
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lMapHeadCodeRate;
	}

	/**
	 * Getting Rate List from Rlt_Pension_Headcode_Rate for any FieldType
	 * accordign to HeadCode, FieldType, and ForDate. Written By Sagar
	 */

	public List<RltPensionHeadcodeRate> getRateLstFromHeadcode(Long lHeadcode, List<String> lLstFieldType) throws Exception {

		List<RltPensionHeadcodeRate> lObjPensionHeadcodeRate = new ArrayList<RltPensionHeadcodeRate>();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();
			List lLstVO = new ArrayList();
			// Format a Date for compare between from and to date.
			lSBQuery.append(" FROM RltPensionHeadcodeRate  WHERE fieldType IN (:lFieldType) AND headCode = :lHeadcode ORDER BY effectiveFromDate");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameterList("lFieldType", lLstFieldType);
			lQuery.setBigDecimal("lHeadcode", new BigDecimal(lHeadcode));

			lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				lObjPensionHeadcodeRate = new ArrayList<RltPensionHeadcodeRate>();
				for (int i = 0; i < lLstVO.size(); i++) {
					lObjPensionHeadcodeRate.add((RltPensionHeadcodeRate) lLstVO.get(i));
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lObjPensionHeadcodeRate;
	}

	/**
	 * Getting Rate or Amount from Rlt_Pension_Headcode_Special accordign to
	 * HeadCode, FieldType, and ForDate. Written By Sagar
	 */
	public Double getBasicFromHeadcodeSpecialRlt(Long lHeadcode, Double lStrOldPnsnBasic, Date lForDate) throws Exception {

		Double lBDNetAmount = null;
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();
			// Format a Date for compare between from and to date.
			// String lStrForDate = new
			// SimpleDateFormat("dd/MMM/yyyy").format(lForDate);

			lSBQuery.append(" Select newAmount FROM RltPensionHeadcodeSpecial  WHERE headCode = :lHeadcode AND oldAmount =:lOldPnsnBasic AND ((fromDate <=:lStrForDate AND toDate >=:lStrForDate) OR "
					+ " (fromDate <=:lStrForDate AND toDate IS NULL)) ");
			// lSBQuery.append(" ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setBigDecimal("lHeadcode", new BigDecimal(lHeadcode));
			lQuery.setBigDecimal("lOldPnsnBasic", new BigDecimal(lStrOldPnsnBasic));
			// lQuery.setDate("lStrForDate", lObjSmplDtFmt.parse(lStrForDate));
			lQuery.setDate("lStrForDate", lForDate);

			List resultList = lQuery.list();
			if (!resultList.isEmpty() && resultList.get(0) != null) {
				lBDNetAmount = Double.valueOf(resultList.get(0).toString());
			}

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lBDNetAmount;
	}

	/**
	 * Feching PK of ROP(s) of Pensioner.
	 * 
	 * Written By Sagar
	 */
	public Long getPKOfPnsncaseROPRlt(String lStrPPONO, String lStrROP) throws Exception {

		Long lPnsnRopRltPK = null;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("SELECT r.pnsncaseRopRltId FROM TrnPnsncaseRopRlt r WHERE r.ppoNo = :lPPONO and r.rop = :lROP ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPPONO", lStrPPONO);
			lQuery.setString("lROP", lStrROP);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				lPnsnRopRltPK = Long.parseLong(lLstVO.get(0).toString());
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lPnsnRopRltPK;
	}

	/**
	 * Feching Payment Month for the given forMonth .
	 * 
	 * Written By Sagar
	 */
	public RltPensionRevisedPayment getPaymentMnthDtls(long lPensionRatePk, String lStrFieldType, String lStrForDate) throws Exception {

		RltPensionRevisedPayment lRevisedPaymentVo = null;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM RltPensionRevisedPayment where headcodeRatePk = :PensionRatePk and fieldType = :FieldType ");
			if (lStrForDate != null && lStrForDate.length() > 0) {
				lSBQuery.append(" And forPayMonth <=:lStrForDate AND toPayMonth >=:lStrForDate ");
			}

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("FieldType", lStrFieldType);
			lQuery.setLong("PensionRatePk", lPensionRatePk);
			if (lStrForDate != null && lStrForDate.length() > 0) {
				lQuery.setInteger("lStrForDate", Integer.parseInt(lStrForDate));
			}
			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				if (lLstVO.get(0) != null) {
					lRevisedPaymentVo = (RltPensionRevisedPayment) lLstVO.get(0);
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lRevisedPaymentVo;
	}

	/**
	 * getting Pension Bill Previous Month's Arrears Details
	 * 
	 * @author Sagar
	 * @return List
	 */
	public List<TrnPensionBillDtls> getPrvBillArrearsDtls(String lStrPensionerCode, Long lBillHdrPK) throws Exception {

		List<TrnPensionBillDtls> lPnsnBillArrDtlsLst = null;
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("FROM TrnPensionBillDtls d1 WHERE d1.trnPensionBillHdrId = :BillHdrPK ORDER BY d1.payForMonth DESC");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setFirstResult(1);
			lQuery.setLong("BillHdrPK", lBillHdrPK);

			List lLstVO = lQuery.list();
			if (!lLstVO.isEmpty()) {
				lPnsnBillArrDtlsLst = new ArrayList<TrnPensionBillDtls>();
				for (int i = lLstVO.size(); i > 0;) {
					lPnsnBillArrDtlsLst.add((TrnPensionBillDtls) lLstVO.get(--i));
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lPnsnBillArrDtlsLst;
	}

	// ///////////////////////////////////////////Added By
	// Vipul//////////////////////////////////
	public Date getPensionerSeenDate(String lArgsStrPensionerCode, String lStrActiveFlag) throws Exception {

		StringBuffer strQuery = new StringBuffer();
		List resultList = null;
		Date lseenDate = null;
		try {
			Session hiSession = getSession();
			strQuery.append(" SELECT seenDate FROM TrnPensionerSeenDtls  WHERE pensionerCode =:lPensionerCode AND activeFlag=:lActiveFlag ");
			Query hqlQuery = hiSession.createQuery(strQuery.toString());
			hqlQuery.setString("lPensionerCode", lArgsStrPensionerCode);
			hqlQuery.setString("lActiveFlag", lStrActiveFlag);
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				lseenDate = (Date) resultList.get(0);
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lseenDate;
	}

	/**
	 * Getting Rate or Amount from Rlt_Pension_Headcode_Rate accordign to
	 * HeadCode, FieldType, and ForDate. Written By Sagar
	 */

	public RltPensionHeadcodeRate getRateFromHeadcodeRateRlt(Long lHeadcode, String lStrFieldType, Double lStrPnsnBasic, Date lForDate) throws Exception {

		RltPensionHeadcodeRate lObjPensionHeadcodeRate = null;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			// Format a Date for compare between from and to date.
			lSBQuery.append(" FROM RltPensionHeadcodeRate  WHERE fieldType = :lFieldType AND headCode = :lHeadcode" +

			" AND ((effectiveFromDate <=:lStrForDate AND effectiveToDate >=:lStrForDate) OR  (effectiveFromDate <=:lStrForDate AND effectiveToDate IS NULL)) ");
			// lSBQuery.append(" ");

//			if (lStrFieldType.equalsIgnoreCase("DA_1986") && lStrPnsnBasic != 0) {
//				if (lStrPnsnBasic <= 1750) {
//					lSBQuery.append(" AND uptoBasic = 1750 ");
//				} else if (lStrPnsnBasic <= 3000) {
//					lSBQuery.append(" AND uptoBasic = 3000 ");
//				} else {
//					lSBQuery.append(" AND uptoBasic = 999999");
//				}
//			}

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lFieldType", lStrFieldType);
			lQuery.setBigDecimal("lHeadcode", new BigDecimal(lHeadcode));
			lQuery.setDate("lStrForDate", lForDate);
			lQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				if (lLstVO.get(0) != null) {
					lObjPensionHeadcodeRate = (RltPensionHeadcodeRate) lLstVO.get(0);
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lObjPensionHeadcodeRate;
	}

	/**
	 * Calculate Greater TI Amount by Per. and Minimum amount.
	 * 
	 * @param lPensionHeadcodeRateVO
	 * @param lOldBasicAmt
	 * @throws Exception
	 * @author Sagar
	 */
	public Double getGreaterTIAmt(RltPensionHeadcodeRate lPensionHeadcodeRateVO, Double lBasicAmt) throws Exception {

		Double lGreaterTIAmt = 0D;

		Double lMinAmt = 0D;
		Double lTIPer = 0D;

		try {
			if (lPensionHeadcodeRateVO != null) {
				if (lPensionHeadcodeRateVO.getRate() != null) {
					lTIPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
					lGreaterTIAmt = ((lBasicAmt) * ((lTIPer) / 100));
				}
				if (lPensionHeadcodeRateVO.getMinAmount() != null) // Checking
				// for
				// greater
				// TI Amount
				// from
				// (Per||MinAmt)
				{
					lMinAmt = Double.valueOf(lPensionHeadcodeRateVO.getMinAmount().toString());

					if (lMinAmt > lGreaterTIAmt) {
						lGreaterTIAmt = lMinAmt;
					}
				}
			}
		} catch (Exception e) {
			throw (e);
		}
		return lGreaterTIAmt;
	}
	// /////////////////////////////////////////Added By
	// Vipul//////////////////////////////////
}
