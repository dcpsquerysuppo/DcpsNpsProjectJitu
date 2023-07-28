package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.pensionpay.constants.PensionConstants;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerFamilyDtls;
import com.tcs.sgv.pensionpay.valueobject.ValidPcodeView;


public class AdminRateMstDAOImpl implements AdminRateMstDAO {

	/* Global Variable for Logger Class */
	private static final Log logger = LogFactory.getLog(AdminRateMstDAOImpl.class);

	private SessionFactory sessionFactory = null;
	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	public AdminRateMstDAOImpl(SessionFactory sessionFactory) {

		this.sessionFactory = sessionFactory;
	}

	public Session getSession() {

		boolean allowCreate = true;
		return SessionFactoryUtils.getSession(sessionFactory, allowCreate);
	}

	public List getTIRateType(long langId) {

		ArrayList arrScheme = new ArrayList();
		try {
			Session hibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer(300);
			String lStrName = "TIRateType";
			lSBQuery.append(" select c1.lookupName from CmnLookupMst c1, CmnLookupMst c2 where c1.cmnLanguageMst.langId = c2.cmnLanguageMst.langId "
					+ " and c1.cmnLanguageMst.langId = :lLangId and c1.parentLookupId = c2.lookupId and c2.lookupName = :lLookupName order by c1.orderNo ");
			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("lLangId", langId);
			lQuery.setString("lLookupName", lStrName);
			lQuery.setCacheable(true).setCacheRegion("pensionCache");
			List listScheme = lQuery.list();

			if (!listScheme.isEmpty()) {
				Iterator it = listScheme.iterator();
				ComboValuesVO vo;
				Object tuple;
				while (it.hasNext()) {
					vo = new ComboValuesVO();
					tuple = it.next();
					vo.setId(tuple.toString());
					vo.setDesc(tuple.toString());
					arrScheme.add(vo);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);

		}
		return arrScheme;
	}

	public List getForPension(long langId) {

		ArrayList arrScheme = new ArrayList();
		try {
			Session hibSession = getSession();

			StringBuffer lSBQuery = new StringBuffer(300);

			String lStrName = "For Pension";

			lSBQuery.append(" select c1.lookupName from CmnLookupMst c1,CmnLookupMst c2 where c1.cmnLanguageMst.langId = c2.cmnLanguageMst.langId and c1.cmnLanguageMst.langId = :lLangId "
					+ " and c1.parentLookupId = c2.lookupId and c2.lookupName = :lLookupName  order by c1.orderNo ");
			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("lLangId", langId);
			lQuery.setString("lLookupName", lStrName);
			lQuery.setCacheable(true).setCacheRegion("pensionCache");
			List listScheme = lQuery.list();
			if (!listScheme.isEmpty()) {
				Iterator it = listScheme.iterator();
				ComboValuesVO vo;
				Object tuple;
				while (it.hasNext()) {
					vo = new ComboValuesVO();
					tuple = it.next();
					vo.setId(tuple.toString());
					vo.setDesc(tuple.toString());
					arrScheme.add(vo);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);

		}
		return arrScheme;
	}

	public List getRltPensionHeadcodeRate(String lStrHeadCode, String lStrFieldType, String lStrTIRateType, String lStrUptoBasic) {

		String lStrTableField = lStrFieldType;
		Iterator it = null;
		Object[] tuple = null;
		List lLstDtlsList = new ArrayList();

		try {
			BigDecimal lBgdBasic = BigDecimal.ZERO;
			if ("UPTO 1750".equals(lStrUptoBasic)) {
				lBgdBasic = new BigDecimal("1750");
			} else if ("UPTO 3000".equals(lStrUptoBasic)) {
				lBgdBasic = new BigDecimal("3000");
			} else if ("ABOVE 3001".equals(lStrUptoBasic)) {
				lBgdBasic = new BigDecimal("999999");
			}

			if ("TI".equals(lStrFieldType) && "DA_1986".equals(lStrTIRateType)) {
				lStrTableField = "DA_1986";
			} else if ("TI".equals(lStrFieldType) && "DA_1996_DP".equals(lStrTIRateType)) {
				lStrTableField = "DA_1996_DP";
			} else if ("TI".equals(lStrFieldType) && "DA_1996".equals(lStrTIRateType)) {
				lStrTableField = "DA_1996";
			} else if ("TI".equals(lStrFieldType) && "DA_2006".equals(lStrTIRateType)) {
				lStrTableField = "DA_2006";
			}
			StringBuffer lSBQuery = new StringBuffer(200);
			Session hibSession = getSession();
			// headcode is actually state code
			lSBQuery.append(" Select A.pensionHeadcodeRateId,A.rate,A.effectiveFromDate FROM RltPensionHeadcodeRate A WHERE A.headCode = :headCode  AND A.fieldType = :fieldType AND A.effectiveToDate is null ");
			if ("TI".equals(lStrFieldType) && "DA_1986".equals(lStrTIRateType)) {
				lSBQuery.append(" AND A.uptoBasic = :uptoBasic ");
			}
			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setBigDecimal("headCode", new BigDecimal(lStrHeadCode));
			lQuery.setString("fieldType", lStrTableField);
			if ("TI".equals(lStrFieldType) && "DA_1986".equals(lStrTIRateType)) {
				lQuery.setBigDecimal("uptoBasic", lBgdBasic);
			}

			List resultList = lQuery.list();

			if (!resultList.isEmpty()) {
				it = resultList.iterator();
				while (it.hasNext()) {
					tuple = (Object[]) it.next();
					lLstDtlsList.add(tuple[0]);
					lLstDtlsList.add(tuple[1]);
					lLstDtlsList.add(tuple[2]);
					// lLstDtlsList.add(tuple[3]);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);

		}
		return lLstDtlsList;
	}

	public List getRltPensionHeadcodeSpecial(String lStrHeadCode) {

		Iterator it = null;
		Object[] tuple = null;
		List lLstDtlsList = new ArrayList();

		try {
			StringBuffer lSBQuery = new StringBuffer(100);
			Session hibSession = getSession();
			lSBQuery.append(" Select A.pensionHeadcodeSpecialId,A.newAmount,A.fromDate FROM RltPensionHeadcodeSpecial A WHERE A.headCode = :headCode  AND A.toDate is null ");
			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setBigDecimal("headCode", new BigDecimal(lStrHeadCode));
			List resultList = lQuery.list();

			if (!resultList.isEmpty()) {
				it = resultList.iterator();
				while (it.hasNext()) {
					tuple = (Object[]) it.next();
					lLstDtlsList.add(tuple[0]);
					lLstDtlsList.add(tuple[1]);
					lLstDtlsList.add(tuple[2]);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lLstDtlsList;
	}

	/*
	 * public List getValidPensioners(String locCode, String headCode, String
	 * lFieldType, String lStrTIStyle) throws Exception {
	 * 
	 * ArrayList lArrPnsnrCode = new ArrayList(); String lStrBillType1 =
	 * PensionConstants.MNTHPENSION1; String lStrBillType2 =
	 * PensionConstants.MNTHMONTHLY; try { Session hibSession = getSession();
	 * StringBuffer lSBQuery = new StringBuffer();lSBQuery.append(
	 * " SELECT d.pensionerCode,r.pensionRequestId , min(d.payForMonth),max(d.payForMonth)  from TrnPensionBillHdr h, TrnPensionBillDtls d, TrnPensionRqstHdr r "
	 * +
	 * " WHERE h.trnPensionBillHdrId = d.trnPensionBillHdrId  AND (h.billType = :lbillType1 or h.billType = :lbillType2)  AND h.locationCode = :lLocCode "
	 * + " AND h.headCode = :lHeadCode  AND d.ppoNo = r.ppoNo "); if (lFieldType
	 * != null && lFieldType.equals("TI")) { if (lStrTIStyle != null &&
	 * lStrTIStyle.equalsIgnoreCase("STI")) {
	 * lSBQuery.append(" and r.isRop = 'Y' and r.specialCase = 'N'"); } else if
	 * (lStrTIStyle != null && lStrTIStyle.equalsIgnoreCase("RTI")) {
	 * lSBQuery.append(" and r.isRop = 'Y' and r.specialCase = 'Y'"); } else if
	 * (lStrTIStyle != null && lStrTIStyle.equalsIgnoreCase("TI")) {
	 * lSBQuery.append(" and r.isRop = 'N'"); } } else if (lFieldType != null &&
	 * lFieldType.equals("DP")) {
	 * lSBQuery.append(" and r.isRop = 'Y' and r.specialCase = 'N'"); } else if
	 * (lFieldType != null && lFieldType.equals("IR")) {
	 * lSBQuery.append(" and r.isRop = 'N'"); }
	 * lSBQuery.append(" group by d.pensionerCode, r.pensionRequestId "); Query
	 * lQuery = hibSession.createQuery(lSBQuery.toString());
	 * lQuery.setString("lLocCode", locCode); lQuery.setBigDecimal("lHeadCode",
	 * new BigDecimal(headCode)); lQuery.setString("lbillType1", lStrBillType1);
	 * lQuery.setString("lbillType2", lStrBillType2); List listPnsnerCode =
	 * lQuery.list(); if (!listPnsnerCode.isEmpty()) { Iterator it =
	 * listPnsnerCode.iterator(); Object[] tuple = null; while (it.hasNext()) {
	 * tuple = (Object[]) it.next(); lArrPnsnrCode.add(tuple[0]);
	 * lArrPnsnrCode.add(tuple[1]); lArrPnsnrCode.add(tuple[2]);
	 * lArrPnsnrCode.add(tuple[3]); } } } catch (Exception e) {
	 * logger.error(" Error is : " + e, e); throw (e); } return lArrPnsnrCode; }
	 */

	/*
	 * public TrnPensionBillDtls getDPTIAmtFromBillDtls(String pnsnrCode,
	 * Integer forMonth) throws Exception {
	 * 
	 * TrnPensionBillDtls lObjBillDtls = null; try { Session hibSession =
	 * getSession(); StringBuffer lSBQuery = new StringBuffer();
	 * lSBQuery.append(
	 * " FROM TrnPensionBillDtls d  WHERE d.pensionerCode = :pnsnrCode  AND d.payForMonth = :forMonth "
	 * ); Query lQuery = hibSession.createQuery(lSBQuery.toString());
	 * lQuery.setString("pnsnrCode", pnsnrCode); lQuery.setInteger("forMonth",
	 * forMonth); List lLstVO = lQuery.list(); if (!lLstVO.isEmpty()) {
	 * lObjBillDtls = (TrnPensionBillDtls) lLstVO.get(0); } } catch (Exception
	 * e) { logger.error(" Error is : " + e, e); throw (e); } return
	 * lObjBillDtls; }
	 */

	/*
	 * public List<TrnPensionBillDtls> getBillDtls(String pnsnrCode, Integer
	 * fromMonth, Integer toMonth) throws Exception {
	 * 
	 * List<TrnPensionBillDtls> lLstBillDtls = new
	 * ArrayList<TrnPensionBillDtls>(); TrnPensionBillDtls lObjBillDtls = null;
	 * try { Session hibSession = getSession(); StringBuffer lSBQuery = new
	 * StringBuffer();lSBQuery.append(
	 * " FROM TrnPensionBillDtls d  WHERE d.pensionerCode = :pnsnrCode  AND d.payForMonth >= :fromMonth  AND d.payForMonth <= :toMonth "
	 * ); Query lQuery = hibSession.createQuery(lSBQuery.toString());
	 * lQuery.setString("pnsnrCode", pnsnrCode); lQuery.setInteger("fromMonth",
	 * fromMonth); lQuery.setInteger("toMonth", toMonth); List lLstVO =
	 * lQuery.list(); if (!lLstVO.isEmpty()) { for (int i = 0; i <
	 * lLstVO.size(); i++) { lObjBillDtls = (TrnPensionBillDtls) lLstVO.get(i);
	 * lLstBillDtls.add(lObjBillDtls); } } } catch (Exception e) {
	 * logger.error(" Error is : " + e, e); throw (e); } return lLstBillDtls; }
	 */

	public List getDataForRecord(String locCode, String headCode, String lFieldType, String lStrTIStyle, Integer fromMonth, Integer toMonth, Double oldRate, Double newRate) throws Exception {

		ArrayList lArrPnsnrCode = new ArrayList();

		String lStrBillType1 = PensionConstants.MNTHPENSION1;
		String lStrBillType2 = PensionConstants.MNTHMONTHLY;
		try {
			Session hibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer(650);
			lSBQuery.append(" select bd.ppoNo, bd.pensionerCode,rq.pensionRequestId,bd.payForMonth ");
			if (lFieldType != null && lFieldType.equals("TI")) {
				lSBQuery.append(" ,bd.tiAmount,round(:newRate / :oldRate * bd.tiAmount) ");
			} else if (lFieldType != null && lFieldType.equals("DP")) {
				lSBQuery.append(" ,bd.dpAmount, round(:newRate / :oldRate * bd.dpAmount) ");
			} else if (lFieldType != null && lFieldType.equals("IR")) {
				lSBQuery.append(" ,bd.irAmount, round(:newRate / :oldRate * bd.irAmount) ");
			} else if (lFieldType != null && lFieldType.equals("MA")) {
				lSBQuery.append(" ,bd.medicalAllowenceAmount, round(:newRate / :oldRate * bd.medicalAllowenceAmount) ");
			}

			lSBQuery.append(" from TrnPensionBillDtls bd,TrnPensionBillHdr hd, TrnPensionRqstHdr rq  where hd.trnPensionBillHdrId = bd.trnPensionBillHdrId "
					+ " and hd.locationCode = rq.locationCode and rq.ppoNo = bd.ppoNo and rq.caseStatus = :lCaseStatus  and hd.billType in (:lbillType1,:lbillType2) "
					+ " and hd.headCode = :lHeadCode and hd.locationCode = :lLocCode  and bd.payForMonth between :fromMonth and :toMonth ");

			if (lFieldType != null && lFieldType.equals("TI")) {
				if (lStrTIStyle != null && lStrTIStyle.equalsIgnoreCase("STI")) {
					lSBQuery.append(" and rq.isRop in (:FlagY,:isRopP) and rq.dpFlag = :FlagN ");
				} else if (lStrTIStyle != null && lStrTIStyle.equalsIgnoreCase("RTI")) {
					lSBQuery.append(" and rq.isRop in (:FlagY,:isRopP) and rq.dpFlag = :FlagY");
				} else if (lStrTIStyle != null && lStrTIStyle.equalsIgnoreCase("TI")) {
					lSBQuery.append(" and rq.isRop = :FlagN ");
				}
			} else if (lFieldType != null && lFieldType.equals("DP")) {
				lSBQuery.append(" and rq.isRop in (:FlagY,:isRopP) and rq.dpFlag = :FlagN ");
			} else if (lFieldType != null && lFieldType.equals("IR")) {
				lSBQuery.append(" and rq.isRop = :FlagN ");
			}
			lSBQuery.append(" and rq.calcType != :calcType ");
			lSBQuery.append(" order by bd.pensionerCode,bd.payForMonth ");

			Query lQuery = hibSession.createQuery(lSBQuery.toString());

			// lQuery.setString("lLocCode", locCode);
			lQuery.setBigDecimal("lHeadCode", new BigDecimal(headCode));
			lQuery.setString("lbillType1", lStrBillType1);
			lQuery.setString("lbillType2", lStrBillType2);
			lQuery.setString("lCaseStatus", bundleConst.getString("STATFLG.APPROVED"));
			lQuery.setInteger("fromMonth", fromMonth);
			lQuery.setInteger("toMonth", toMonth);
			lQuery.setDouble("oldRate", oldRate);
			lQuery.setDouble("newRate", newRate);

			if (lFieldType != null && lFieldType.equals("TI")) {
				if (lStrTIStyle != null && (lStrTIStyle.equalsIgnoreCase("STI") || lStrTIStyle.equalsIgnoreCase("RTI"))) {
					lQuery.setString("FlagY", "Y");
					lQuery.setString("FlagN", "N");
					lQuery.setString("isRopP", "P");
				}

				if (lStrTIStyle != null && lStrTIStyle.equalsIgnoreCase("TI")) {
					lQuery.setString("FlagN", "N");
					lQuery.setString("isRopP", "P");
				}
			}
			if (lFieldType != null && lFieldType.equals("DP")) {
				lQuery.setString("FlagY", "Y");
				lQuery.setString("FlagN", "N");
				lQuery.setString("isRopP", "P");
			}
			if (lFieldType != null && lFieldType.equals("IR")) {
				lQuery.setString("FlagN", "N");
				lQuery.setString("isRopP", "P");
			}

			lQuery.setCharacter("calcType", 'M');

			List listPnsnerCode = lQuery.list();
			if (!listPnsnerCode.isEmpty()) {
				Iterator it = listPnsnerCode.iterator();
				Object[] tuple = null;
				while (it.hasNext()) {
					tuple = (Object[]) it.next();
					lArrPnsnrCode.add(tuple[0]); // ppo no
					lArrPnsnrCode.add(tuple[1]); // pensioner code
					lArrPnsnrCode.add(tuple[2]); // pension request id
					lArrPnsnrCode.add(tuple[3]); // pay for month
					lArrPnsnrCode.add(tuple[4]); // old amount (TI/DP/IR/MA)
					lArrPnsnrCode.add(tuple[5]); // new amount (TI/DP/IR/MA)
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lArrPnsnrCode;
	}

	public List<ValidPcodeView> getRecordsFor6ThPay(String locCode, String stateCode) throws Exception {

		/*
		 * SELECT rq.ppo_No, rq.pensioner_Code, rq.pension_Request_Id,
		 * rq.basic_Pension_Amount, rq.commencement_date, rq.last_Paid_Date,
		 * rq.end_date, rq.commencement_date, rq.seen_flag, rq.status,
		 * rq.is_rop, rq.fp1_Amount, rq.fp2_amount, rq.fp1_date, rq.fp2_date,
		 * rq.calc_type, rq.dp_percent, rq.scheme_type, rq.ti_percent,
		 * rq.medical_allowence_amount, rq.org_bf_1_11_56, rq.org_af_1_11_56,
		 * rq.org_af_1_05_60, rq.personal_pension, rq.ir, rq.cvp_monthly_amount,
		 * rq.paid_date, rq.red_bf_1_11_56, rq.red_af_1_11_56, rq.cvp_date,
		 * rq.ppo_date, rq.applied_date, rq.commencement_date, rq.pension_type,
		 * rq.omr_type, rq.last_paid_date, rq.special_case,
		 * rq.cvp_effective_month, rq.is_Fp1datechange, ms.date_of_birth,
		 * ms.date_of_death, ms.date_of_retirement FROM Trn_Pension_Rqst_Hdr rq,
		 * Trn_Pnsncase_Rop_Rlt pr, mst_pensioner_hdr ms WHERE rq.ppo_No =
		 * pr.ppo_No AND pr.rop = 2006 AND rq.case_Status = 'APPROVED' AND
		 * rq.head_Code = 1 AND rq.calc_Type = 'A' AND ms.pensioner_code =
		 * rq.pensioner_code AND ms.case_status = rq.case_status AND
		 * ms.location_code = rq.location_code ORDER BY rq.pensioner_Code
		 */

		ArrayList<ValidPcodeView> lArrPnsnrCode = new ArrayList<ValidPcodeView>();

		String lStrBillType1 = PensionConstants.MNTHPENSION1;
		String lStrBillType2 = PensionConstants.MNTHMONTHLY;
		try {
			// Session hibSession =
			// ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			Session hibSession = sessionFactory.getCurrentSession();
			StringBuffer lSBQuery = new StringBuffer(650);
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

			ValidPcodeView lObjValidPcode = null;

			lSBQuery.append(" SELECT rq.ppo_No, "
					+ // 0
					" rq.pensioner_Code, "
					+ // 1
					" rq.pension_Request_Id, "
					+ // 2
					" rq.basic_Pension_Amount, "
					+ // 3
					" rq.commencement_date, "
					+ // 4
					" rq.last_Paid_Date, "
					+ // 5
					" rq.end_date, "
					+ // 6
					" rq.seen_flag, "
					+ // 7
					" rq.status, "
					+ // 8
					" rq.is_rop, "
					+ // 9
					" rq.fp1_Amount, "
					+ // 10
					" rq.fp2_amount, "
					+ // 11
					" rq.fp1_date, "
					+ // 12
					" rq.fp2_date, "
					+ // 13
					" rq.calc_type, "
					+ // 14
					" rq.dp_percent, "
					+ // 15
					" rq.scheme_type, "
					+ // 16
					" rq.ti_percent, "
					+ // 17
					" rq.medical_allowence_amount, "
					+ // 18
					" rq.org_bf_1_11_56, "
					+ // 19
					" rq.org_af_1_11_56, "
					+ // 20
					" rq.org_af_1_05_60, "
					+ // 21
					" rq.personal_pension, "
					+ // 22
					" rq.ir, "
					+ // 23
					" rq.cvp_monthly_amount, "
					+ // 24
					" rq.paid_date, "
					+ // 25
					" rq.red_bf_1_11_56, "
					+ // 26
					" rq.red_af_1_11_56, "
					+ // 27
					" rq.cvp_date, "
					+ // 28
					" rq.ppo_date, "
					+ // 29
					" rq.applied_date, "
					+ // 30
					" rq.pension_type, "
					+ // 31
					" rq.omr_type, "
					+ // 32
					" rq.dp_flag, "
					+ // 33
					" rq.cvp_effective_month, "
					+ // 34
					" rq.is_Fp1datechange, "
					+ // 35
					" ms.date_of_birth, "
					+ // 36
					" ms.date_of_death, "
					+ // 37
					" ms.date_of_retirement "
					+ // 38
					" FROM Trn_Pension_Rqst_Hdr rq, " + " mst_pensioner_hdr    ms " + " WHERE rq.rop_type = :lROP " + " AND rq.case_Status = :caseStat "
					+ " AND rq.DA_RATE_FOR_STATE = :lStateCodeForDA " + " AND rq.calc_Type = :calcType " + "AND ms.pensioner_code = rq.pensioner_code ");

			Query lQuery = hibSession.createSQLQuery(lSBQuery.toString());

			// lQuery.setString("lLocCode", locCode);
			lQuery.setBigDecimal("lStateCodeForDA", new BigDecimal(stateCode));
			lQuery.setString("caseStat", bundleConst.getString("STATFLG.APPROVED"));
			lQuery.setString("calcType", "A");
			lQuery.setString("lROP", "2006");

			List listPnsnerCode = lQuery.list();
			if (!listPnsnerCode.isEmpty()) {
				Iterator it = listPnsnerCode.iterator();
				Object[] tuple = null;
				while (it.hasNext()) {
					tuple = (Object[]) it.next();
					lObjValidPcode = new ValidPcodeView();
					lObjValidPcode.setPpoNo(tuple[0].toString());
					lObjValidPcode.setPensionerCode(tuple[1].toString());
					lObjValidPcode.setPensionRequestId(Long.parseLong(tuple[2].toString()));
					lObjValidPcode.setBasicPensionAmount(new BigDecimal(tuple[3].toString()));

					lObjValidPcode.setCommensionDate(tuple[4] == null ? null : df.parse(df.format(tuple[4])));
					lObjValidPcode.setLastPaidDate(tuple[5] == null ? null : df.parse(df.format(tuple[5])));
					lObjValidPcode.setEndDate(tuple[6] == null ? null : df.parse(df.format(tuple[6])));
					lObjValidPcode.setSeenFlag(tuple[7] == null ? "" : tuple[7].toString());
					lObjValidPcode.setStatus(tuple[8] == null ? "" : (String) tuple[8]);
					lObjValidPcode.setIsRop(tuple[9] == null ? "" : (String) tuple[9]);
					lObjValidPcode.setFp1Amount(new BigDecimal(tuple[10].toString()));
					lObjValidPcode.setFp2Amount(new BigDecimal(tuple[11].toString()));
					lObjValidPcode.setFp1Date(tuple[12] == null ? null : df.parse(df.format(tuple[12])));
					lObjValidPcode.setFp2Date(tuple[13] == null ? null : df.parse(df.format(tuple[13])));
					lObjValidPcode.setCalcType(tuple[14] == null ? "" : tuple[14].toString());
					lObjValidPcode.setDpPercent(tuple[15] == null ? BigDecimal.ZERO : new BigDecimal(tuple[15].toString()));
					lObjValidPcode.setSchemeType(tuple[16] == null ? "" : (String) tuple[16]);
					lObjValidPcode.setTiPercent(tuple[17] == null ? BigDecimal.ZERO : new BigDecimal(tuple[17].toString()));
					lObjValidPcode.setMedicalAllowenceAmount(BigDecimal.ZERO);
					lObjValidPcode.setOrgAf10560(BigDecimal.ZERO);
					lObjValidPcode.setOrgAf11156(BigDecimal.ZERO);
					lObjValidPcode.setOrgBf11156(BigDecimal.ZERO);
					lObjValidPcode.setPersonalPension(new BigDecimal(tuple[22].toString()));
					lObjValidPcode.setIr(new BigDecimal(tuple[23].toString()));
					lObjValidPcode.setCvpMonthlyAmount(BigDecimal.ZERO);
					lObjValidPcode.setPaidDate(tuple[25] == null ? null : df.parse(df.format(tuple[25])));
					lObjValidPcode.setRedAf11156(new BigDecimal(tuple[26].toString()));
					lObjValidPcode.setRedBf11156(new BigDecimal(tuple[27].toString()));
					lObjValidPcode.setCvpDate(tuple[28] == null ? null : df.parse(df.format(tuple[28])));
					lObjValidPcode.setPpoDate(tuple[29] == null ? null : df.parse(df.format(tuple[29])));
					lObjValidPcode.setAppliedDate(tuple[30] == null ? null : df.parse(df.format(tuple[30])));
					lObjValidPcode.setPensionType(tuple[31] == null ? "" : (String) tuple[31]);
					lObjValidPcode.setOmrType("");
					lObjValidPcode.setDpFlag(tuple[33] == null ? "" : tuple[33].toString());
					lObjValidPcode.setCvpEffectiveMonth(tuple[34] == null ? null : Integer.valueOf(tuple[34].toString()));
					lObjValidPcode.setIsFp1datechange(tuple[35] == null ? "" : tuple[35].toString());
					lObjValidPcode.setDateOfBirth(tuple[36] == null ? null : df.parse(df.format(tuple[36])));
					lObjValidPcode.setDateOfDeath(tuple[37] == null ? null : df.parse(df.format(tuple[37])));
					lObjValidPcode.setDateOfRetirement(tuple[38] == null ? null : df.parse(df.format(tuple[38])));
					// lObjValidPcode.setHeadCode(Long.parseLong(headCode));

					lArrPnsnrCode.add(lObjValidPcode);

				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lArrPnsnrCode;
	}

	public List<ValidPcodeView> getRecordsFor5ThPay(String locCode, String stateCode) throws Exception {

		ArrayList<ValidPcodeView> lArrPnsnrCode = new ArrayList<ValidPcodeView>();

		String lStrBillType1 = PensionConstants.MNTHPENSION1;
		String lStrBillType2 = PensionConstants.MNTHMONTHLY;
		try {
			Session hibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			StringBuffer lSBQuery = new StringBuffer(650);
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

			ValidPcodeView lObjValidPcode = null;

			/*
			 * lSBQuery.append( "SELECT rq.location_code," +
			 * "rp.pensioner_code," + "rq.ppo_no,rq.head_code," +
			 * "rq.pension_request_id," + "rq.basic_pension_amount," +
			 * "rq.commencement_date," + "rq.last_paid_date " +
			 * "FROM trn_pnsncase_rop_rlt rp " +
			 * "JOIN trn_pension_rqst_hdr rq ON rq.location_code = rp.location_code"
			 * +
			 * "AND rq.pensioner_code = rp.pensioner_code AND rq.ppo_no = rp.ppo_no WHERE rq.status = 'Continue' "
			 * +
			 * "AND rq.case_status = 'APPROVED' AND rq.SPECIAL_CASE = 'N' AND rq.CALC_TYPE = 'A' "
			 * + "AND rp.rop = 1996 " +
			 * "AND NOT EXISTS ( SELECT 1 FROM trn_pnsncase_rop_rlt rp1 WHERE "
			 * +
			 * "rp1.location_code = rp.location_code AND rp1.pensioner_code = rp.pensioner_code "
			 * + "AND rp1.rop = 2006)");
			 */

			lSBQuery.append(" SELECT rq.ppo_No,rq.pensioner_Code,rq.pension_Request_Id,rq.basic_Pension_Amount,rq.commencement_date,rq.last_Paid_Date,rq.end_date,	");
			lSBQuery.append(" rq.seen_flag,rq.status,rq.is_rop,rq.fp1_Amount,rq.fp2_amount,rq.fp1_date,rq.fp2_date,rq.calc_type,rq.dp_percent,rq.scheme_type,	");
			lSBQuery.append(" rq.ti_percent,rq.medical_allowence_amount,rq.org_bf_1_11_56,rq.org_af_1_11_56,rq.org_af_1_05_60,rq.personal_pension,rq.ir,rq.cvp_monthly_amount,	");
			lSBQuery.append(" rq.paid_date,rq.red_bf_1_11_56,rq.red_af_1_11_56,rq.cvp_date,rq.ppo_date,rq.applied_date,rq.pension_type,rq.omr_type,rq.dp_flag,	");
			lSBQuery.append(" rq.cvp_effective_month,rq.is_Fp1datechange,ms.date_of_birth,ms.date_of_death,ms.date_of_retirement	");
			lSBQuery.append(" FROM Trn_Pension_Rqst_Hdr rq,mst_pensioner_hdr ms	");
			lSBQuery.append(" WHERE ms.pensioner_code = rq.pensioner_code	");
			lSBQuery.append(" AND rq.case_Status = :caseStat AND rq.calc_Type = :calcType	");
			lSBQuery.append(" AND rq.status = :lStatus AND rq.DA_RATE_FOR_STATE = :lStateCodeForDA AND rq.rop_type = :lROP ");
			lSBQuery.append(" AND rq.rop_type = :lSpecialCase ");

			Query lQuery = hibSession.createSQLQuery(lSBQuery.toString());

			// lQuery.setString("lLocCode", locCode);
			lQuery.setBigDecimal("lStateCodeForDA", new BigDecimal(stateCode));
			lQuery.setString("caseStat", bundleConst.getString("STATFLG.APPROVED"));
			lQuery.setString("calcType", "A");
			lQuery.setString("lROP", "1996");
			lQuery.setString("lStatus", "CONTINUE");
			lQuery.setString("lSpecialCase", "Y");

			List listPnsnerCode = lQuery.list();

			if (!listPnsnerCode.isEmpty()) {
				Iterator it = listPnsnerCode.iterator();
				Object[] tuple = null;
				while (it.hasNext()) {
					tuple = (Object[]) it.next();
					lObjValidPcode = new ValidPcodeView();
					lObjValidPcode.setPpoNo(tuple[0].toString());
					lObjValidPcode.setPensionerCode(tuple[1].toString());
					lObjValidPcode.setPensionRequestId(Long.parseLong(tuple[2].toString()));
					lObjValidPcode.setBasicPensionAmount(new BigDecimal(tuple[3].toString()));

					lObjValidPcode.setCommensionDate(tuple[4] == null ? null : df.parse(df.format(tuple[4])));
					lObjValidPcode.setLastPaidDate(tuple[5] == null ? null : df.parse(df.format(tuple[5])));
					lObjValidPcode.setEndDate(tuple[6] == null ? null : df.parse(df.format(tuple[6])));
					lObjValidPcode.setSeenFlag(tuple[7] == null ? "" : tuple[7].toString());
					lObjValidPcode.setStatus(tuple[8] == null ? "" : (String) tuple[8]);
					lObjValidPcode.setIsRop(tuple[9] == null ? "" : (String) tuple[9]);
					lObjValidPcode.setFp1Amount(new BigDecimal(tuple[10].toString()));
					lObjValidPcode.setFp2Amount(new BigDecimal(tuple[11].toString()));
					lObjValidPcode.setFp1Date(tuple[12] == null ? null : df.parse(df.format(tuple[12])));
					lObjValidPcode.setFp2Date(tuple[13] == null ? null : df.parse(df.format(tuple[13])));
					lObjValidPcode.setCalcType(tuple[14] == null ? "" : tuple[14].toString());
					lObjValidPcode.setDpPercent(tuple[15] == null ? BigDecimal.ZERO : new BigDecimal(tuple[15].toString()));
					lObjValidPcode.setSchemeType(tuple[16] == null ? "" : (String) tuple[16]);
					lObjValidPcode.setTiPercent(tuple[17] == null ? BigDecimal.ZERO : new BigDecimal(tuple[17].toString()));
					lObjValidPcode.setMedicalAllowenceAmount(BigDecimal.ZERO);
					lObjValidPcode.setOrgAf10560(BigDecimal.ZERO);
					lObjValidPcode.setOrgAf11156(BigDecimal.ZERO);
					lObjValidPcode.setOrgBf11156(BigDecimal.ZERO);
					lObjValidPcode.setPersonalPension(new BigDecimal(tuple[22].toString()));
					lObjValidPcode.setIr(new BigDecimal(tuple[23].toString()));
					lObjValidPcode.setCvpMonthlyAmount(BigDecimal.ZERO);
					lObjValidPcode.setPaidDate(tuple[25] == null ? null : df.parse(df.format(tuple[25])));
					lObjValidPcode.setRedAf11156(new BigDecimal(tuple[26].toString()));
					lObjValidPcode.setRedBf11156(new BigDecimal(tuple[27].toString()));
					lObjValidPcode.setCvpDate(tuple[28] == null ? null : df.parse(df.format(tuple[28])));
					lObjValidPcode.setPpoDate(tuple[29] == null ? null : df.parse(df.format(tuple[29])));
					lObjValidPcode.setAppliedDate(tuple[30] == null ? null : df.parse(df.format(tuple[30])));
					lObjValidPcode.setPensionType(tuple[31] == null ? "" : (String) tuple[31]);
					lObjValidPcode.setOmrType("");
					lObjValidPcode.setDpFlag(tuple[33] == null ? "" : tuple[33].toString());
					lObjValidPcode.setCvpEffectiveMonth(tuple[34] == null ? null : Integer.valueOf(tuple[34].toString()));
					lObjValidPcode.setIsFp1datechange(tuple[35] == null ? "" : tuple[35].toString());
					lObjValidPcode.setDateOfBirth(tuple[36] == null ? null : df.parse(df.format(tuple[36])));
					lObjValidPcode.setDateOfDeath(tuple[37] == null ? null : df.parse(df.format(tuple[37])));
					lObjValidPcode.setDateOfRetirement(tuple[38] == null ? null : df.parse(df.format(tuple[38])));
					// lObjValidPcode.setHeadCode(Long.parseLong(headCode));

					lArrPnsnrCode.add(lObjValidPcode);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lArrPnsnrCode;
	}

	/* Start -- Added by Vidhi */
	public List<ValidPcodeView> getRecordsFor4thPay(String lStrForPension, String stateCode) throws Exception {

		ArrayList<ValidPcodeView> lArrPnsnrCode = new ArrayList<ValidPcodeView>();

		String lStrBillType1 = PensionConstants.MNTHPENSION1;
		String lStrBillType2 = PensionConstants.MNTHMONTHLY;
		try {
			Session hibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			StringBuffer lSBQuery = new StringBuffer(650);
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

			ValidPcodeView lObjValidPcode = null;

			/*
			 * lSBQuery.append( "SELECT rq.location_code," +
			 * "rp.pensioner_code," + "rq.ppo_no,rq.head_code," +
			 * "rq.pension_request_id," + "rq.basic_pension_amount," +
			 * "rq.commencement_date," + "rq.last_paid_date " +
			 * "FROM trn_pnsncase_rop_rlt rp " +
			 * "JOIN trn_pension_rqst_hdr rq ON rq.location_code = rp.location_code"
			 * +
			 * "AND rq.pensioner_code = rp.pensioner_code AND rq.ppo_no = rp.ppo_no WHERE rq.status = 'Continue' "
			 * +
			 * "AND rq.case_status = 'APPROVED' AND rq.SPECIAL_CASE = 'N' AND rq.CALC_TYPE = 'A' "
			 * + "AND rp.rop = 1996 " +
			 * "AND NOT EXISTS ( SELECT 1 FROM trn_pnsncase_rop_rlt rp1 WHERE "
			 * +
			 * "rp1.location_code = rp.location_code AND rp1.pensioner_code = rp.pensioner_code "
			 * + "AND rp1.rop = 2006)");
			 */

			lSBQuery.append("SELECT rq.ppo_No,"
					+ // 0
					"rq.pensioner_Code,"
					+ // 1
					"rq.pension_Request_Id,"
					+ // 2
					"rq.basic_Pension_Amount,"
					+ // 3
					"rq.commencement_date,"
					+ // 4
					"rq.last_Paid_Date,"
					+ // 5
					"rq.end_date,"
					+ // 6
					"rq.seen_flag,"
					+ // 7
					"rq.status,"
					+ // 8
					"rq.is_rop,"
					+ // 9
					"rq.fp1_Amount,"
					+ // 10
					"rq.fp2_amount,"
					+ // 11
					"rq.fp1_date,"
					+ // 12
					"rq.fp2_date,"
					+ // 13
					"rq.calc_type,"
					+ // 14
					"rq.dp_percent,"
					+ // 15
					"rq.scheme_type,"
					+ // 16
					"rq.ti_percent,"
					+ // 17
					"rq.medical_allowence_amount,"
					+ // 18
					"rq.org_bf_1_11_56,"
					+ // 19
					"rq.org_af_1_11_56,"
					+ // 20
					"rq.org_af_1_05_60,"
					+ // 21
					"rq.personal_pension,"
					+ // 22
					"rq.ir,"
					+ // 23
					"rq.cvp_monthly_amount,"
					+ // 24
					"rq.paid_date,"
					+ // 25
					"rq.red_bf_1_11_56,"
					+ // 26
					"rq.red_af_1_11_56,"
					+ // 27
					"rq.cvp_date,"
					+ // 28
					"rq.ppo_date,"
					+ // 29
					"rq.applied_date,"
					+ // 30
					"rq.pension_type,"
					+ // 31
					"rq.omr_type,"
					+ // 32
					"rq.dp_flag,"
					+ // 33
					"rq.cvp_effective_month,"
					+ // 34
					"rq.is_Fp1datechange,"
					+ // 35
					"ms.date_of_birth,"
					+ // 36
					"ms.date_of_death,"
					+ // 37
					"ms.date_of_retirement "
					+ // 38
					"FROM Trn_Pension_Rqst_Hdr rq," + "Trn_Pnsncase_Rop_Rlt pr," + "mst_pensioner_hdr ms " + "WHERE rq.ppo_No = pr.ppo_No " + "AND rq.location_code = pr.location_code "
					+ "AND pr.rop = :lROP " + "AND rq.case_Status = :caseStat " + "AND rq.calc_Type = :calcType " + "AND rq.status = :lStatus " + "AND rq.dp_flag = :lSpecialCase "
					+ "AND ms.pensioner_code = rq.pensioner_code " + "AND ms.case_status = rq.case_status " + "AND rq.DA_RATE_FOR_STATE = :lStateCodeForDA "
					+
					// "AND rq.pensioner_code = 574890 " +
					"AND NOT EXISTS(SELECT 1 FROM trn_pnsncase_rop_rlt rp1 WHERE " + "rp1.location_code = pr.location_code AND rp1.PPO_NO = pr.PPO_NO " + "AND rp1.rop = :lROP1 ) "
					+ "AND NOT EXISTS(SELECT 1 FROM trn_pnsncase_rop_rlt rp1 WHERE " + "rp1.location_code = pr.location_code AND rp1.PPO_NO = pr.PPO_NO " + "AND rp1.rop = :lROP2 )");

			if (lStrForPension.equalsIgnoreCase("UPTO 1750")) {
				lSBQuery.append(" AND rq.basic_pension_amount <= 1750 ");
			} else if (lStrForPension.equalsIgnoreCase("UPTO 3000")) {
				lSBQuery.append(" AND rq.basic_pension_amount > 1750 AND rq.basic_pension_amount <= 3000");
			} else if (lStrForPension.equalsIgnoreCase("UPTO 3000")) {
				lSBQuery.append(" AND rq.basic_pension_amount > 3000");
			}

			Query lQuery = hibSession.createSQLQuery(lSBQuery.toString());

			// lQuery.setString("lLocCode", locCode);
			lQuery.setBigDecimal("lStateCodeForDA", new BigDecimal(stateCode));
			lQuery.setString("caseStat", bundleConst.getString("STATFLG.APPROVED"));
			lQuery.setString("calcType", "A");
			lQuery.setString("lROP", "1986");
			lQuery.setString("lStatus", "CONTINUE");
			lQuery.setString("lSpecialCase", "N");
			lQuery.setString("lROP1", "2006");
			lQuery.setString("lROP2", "1996");

			List listPnsnerCode = lQuery.list();

			if (!listPnsnerCode.isEmpty()) {
				Iterator it = listPnsnerCode.iterator();
				Object[] tuple = null;
				while (it.hasNext()) {
					tuple = (Object[]) it.next();
					lObjValidPcode = new ValidPcodeView();
					lObjValidPcode.setPpoNo(tuple[0].toString());
					lObjValidPcode.setPensionerCode(tuple[1].toString());
					lObjValidPcode.setPensionRequestId(Long.parseLong(tuple[2].toString()));
					lObjValidPcode.setBasicPensionAmount(new BigDecimal(tuple[3].toString()));

					lObjValidPcode.setCommensionDate(tuple[4] == null ? null : df.parse(df.format(tuple[4])));
					lObjValidPcode.setLastPaidDate(tuple[5] == null ? null : df.parse(df.format(tuple[5])));
					lObjValidPcode.setEndDate(tuple[6] == null ? null : df.parse(df.format(tuple[6])));
					lObjValidPcode.setSeenFlag(tuple[7] == null ? "" : tuple[7].toString());
					lObjValidPcode.setStatus(tuple[8] == null ? "" : (String) tuple[8]);
					lObjValidPcode.setIsRop(tuple[9] == null ? "" : (String) tuple[9]);
					lObjValidPcode.setFp1Amount(new BigDecimal(tuple[10].toString()));
					lObjValidPcode.setFp2Amount(new BigDecimal(tuple[11].toString()));
					lObjValidPcode.setFp1Date(tuple[12] == null ? null : df.parse(df.format(tuple[12])));
					lObjValidPcode.setFp2Date(tuple[13] == null ? null : df.parse(df.format(tuple[13])));
					lObjValidPcode.setCalcType(tuple[14] == null ? "" : tuple[14].toString());
					lObjValidPcode.setDpPercent(tuple[15] == null ? BigDecimal.ZERO : new BigDecimal(tuple[15].toString()));
					lObjValidPcode.setSchemeType(tuple[16] == null ? "" : (String) tuple[16]);
					lObjValidPcode.setTiPercent(tuple[17] == null ? BigDecimal.ZERO : new BigDecimal(tuple[17].toString()));
					lObjValidPcode.setMedicalAllowenceAmount(BigDecimal.ZERO);
					lObjValidPcode.setOrgAf10560(BigDecimal.ZERO);
					lObjValidPcode.setOrgAf11156(BigDecimal.ZERO);
					lObjValidPcode.setOrgBf11156(BigDecimal.ZERO);
					lObjValidPcode.setPersonalPension(new BigDecimal(tuple[22].toString()));
					lObjValidPcode.setIr(new BigDecimal(tuple[23].toString()));
					lObjValidPcode.setCvpMonthlyAmount(BigDecimal.ZERO);
					lObjValidPcode.setPaidDate(tuple[25] == null ? null : df.parse(df.format(tuple[25])));
					lObjValidPcode.setRedAf11156(new BigDecimal(tuple[26].toString()));
					lObjValidPcode.setRedBf11156(new BigDecimal(tuple[27].toString()));
					lObjValidPcode.setCvpDate(tuple[28] == null ? null : df.parse(df.format(tuple[28])));
					lObjValidPcode.setPpoDate(tuple[29] == null ? null : df.parse(df.format(tuple[29])));
					lObjValidPcode.setAppliedDate(tuple[30] == null ? null : df.parse(df.format(tuple[30])));
					lObjValidPcode.setPensionType(tuple[31] == null ? "" : (String) tuple[31]);
					lObjValidPcode.setOmrType("");
					lObjValidPcode.setDpFlag(tuple[33] == null ? "" : tuple[33].toString());
					lObjValidPcode.setCvpEffectiveMonth(tuple[34] == null ? null : Integer.valueOf(tuple[34].toString()));
					lObjValidPcode.setIsFp1datechange(tuple[35] == null ? "" : tuple[35].toString());
					lObjValidPcode.setDateOfBirth(tuple[36] == null ? null : df.parse(df.format(tuple[36])));
					lObjValidPcode.setDateOfDeath(tuple[37] == null ? null : df.parse(df.format(tuple[37])));
					lObjValidPcode.setDateOfRetirement(tuple[38] == null ? null : df.parse(df.format(tuple[38])));
					// lObjValidPcode.setHeadCode(Long.parseLong(headCode));

					lArrPnsnrCode.add(lObjValidPcode);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lArrPnsnrCode;
	}

	/* End -- Added by Vidhi */

	public Map getMstFamilyDtlsMap(String stateCode) throws Exception {

		/*
		 * SELECT fd.name, fd.percentage, fd.relationship, fd.accnt_No,
		 * fd.date_Of_Birth, fd.date_Of_Death, fd.major_Flag, fd.married_Flag,
		 * fd.salary, fd.handicape_Flag, fd.guardian_Name, fd.bank_Code,
		 * fd.branch_Code, fd.pensioner_Code FROM Mst_Pensioner_Family_Dtls
		 * fd,Trn_Pension_Rqst_Hdr rq,Trn_Pnsncase_Rop_Rlt pr WHERE
		 * fd.pensioner_code = rq.pensioner_code AND rq.case_status = 'APPROVED'
		 * AND rq.head_code = 1 AND rq.calc_type = 'A' AND pr.ppo_no = rq.ppo_no
		 * AND pr.rop = '2006' AND fd.percentage = 100
		 */

		MstPensionerFamilyDtls lObjMstPensionerFamilyDtls = null;
		List<MstPensionerFamilyDtls> lLstFamilyDtls = null;
		String tempPCode = null;
		StringBuilder lSBQuery = new StringBuilder();
		Map lMapFamilyDtls = new HashMap<String, Object>();
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();

		try {

			lSBQuery.append(" SELECT fd.name, fd.percentage, fd.relationship,  fd.accnt_No,  fd.date_Of_Birth,  fd.date_Of_Death,  fd.major_Flag,  fd.married_Flag, ");
			lSBQuery.append(" fd.salary, fd.handicape_Flag, fd.guardian_Name, fd.bank_Code, fd.branch_Code, fd.pensioner_Code ");
			lSBQuery.append(" FROM Mst_Pensioner_Family_Dtls fd,Trn_Pension_Rqst_Hdr rq,mst_pensioner_hdr ms  ");
			lSBQuery.append(" WHERE fd.pensioner_code = rq.pensioner_code AND rq.pensioner_code=ms.pensioner_code ");
			lSBQuery.append(" AND rq.case_status = 'Approved'  ");
			lSBQuery.append(" AND rq.calc_type = 'A' ");
			lSBQuery.append(" AND rq.rop_type = '2006' ");
			lSBQuery.append(" AND rq.DA_RATE_FOR_STATE = :lStateCodeForDA ");
			lSBQuery.append(" AND ms.date_of_death IS NOT NULL ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setBigDecimal("lStateCodeForDA", new BigDecimal(stateCode));

			List lLstVO = lQuery.list();
			if (!lLstVO.isEmpty()) {
				// lLstFamilyDtls = new ArrayList<MstPensionerFamilyDtls>();

				Iterator itr = lLstVO.iterator();
				while (itr.hasNext()) {
					lObjMstPensionerFamilyDtls = new MstPensionerFamilyDtls();
					Object[] tuple = (Object[]) itr.next();

					lObjMstPensionerFamilyDtls.setName(tuple[0] != null ? tuple[0].toString() : "");
					lObjMstPensionerFamilyDtls.setPercentage(100L);
					lObjMstPensionerFamilyDtls.setRelationship(tuple[2] != null ? tuple[2].toString() : "");
					lObjMstPensionerFamilyDtls.setAccntNo(tuple[3] != null ? tuple[3].toString() : "");
					lObjMstPensionerFamilyDtls.setDateOfBirth((Date) (tuple[4] != null ? (tuple[4]) : null));
					lObjMstPensionerFamilyDtls.setDateOfDeath((Date) (tuple[5] != null ? (tuple[5]) : null));
					lObjMstPensionerFamilyDtls.setMajorFlag(tuple[6] != null ? tuple[6].toString() : "");
					lObjMstPensionerFamilyDtls.setMarriedFlag(tuple[7] != null ? tuple[7].toString() : "");
					lObjMstPensionerFamilyDtls.setSalary(tuple[8] != null ? new BigDecimal(tuple[8].toString()) : BigDecimal.ZERO);
					lObjMstPensionerFamilyDtls.setHandicapeFlag(tuple[9] != null ? tuple[9].toString() : "");
					lObjMstPensionerFamilyDtls.setGuardianName(tuple[10] != null ? tuple[10].toString() : "");
					lObjMstPensionerFamilyDtls.setBankCode(tuple[11] != null ? tuple[11].toString() : "");
					lObjMstPensionerFamilyDtls.setBranchCode(tuple[12] != null ? tuple[12].toString() : "");
					lObjMstPensionerFamilyDtls.setPensionerCode(tuple[13] != null ? tuple[13].toString() : "");

					tempPCode = lObjMstPensionerFamilyDtls.getPensionerCode();

					lMapFamilyDtls.put("Family" + tempPCode, lObjMstPensionerFamilyDtls);

					// lLstFamilyDtls.add(lObjMstPensionerFamilyDtls);
					lObjMstPensionerFamilyDtls = null;
				}
			}

			return lMapFamilyDtls;
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	/* Added by Vidhi */
	public Map getMstFamilyDtlsMapFor5thPay(String stateCode) throws Exception {

		/*
		 * SELECT fd.name, fd.percentage, fd.relationship, fd.accnt_No,
		 * fd.date_Of_Birth, fd.date_Of_Death, fd.major_Flag, fd.married_Flag,
		 * fd.salary, fd.handicape_Flag, fd.guardian_Name, fd.bank_Code,
		 * fd.branch_Code, fd.pensioner_Code FROM Mst_Pensioner_Family_Dtls
		 * fd,Trn_Pension_Rqst_Hdr rq,Trn_Pnsncase_Rop_Rlt pr WHERE
		 * fd.pensioner_code = rq.pensioner_code AND rq.case_status = 'APPROVED'
		 * AND rq.head_code = 1 AND rq.calc_type = 'A' AND pr.ppo_no = rq.ppo_no
		 * AND pr.rop = '2006' AND fd.percentage = 100
		 */

		MstPensionerFamilyDtls lObjMstPensionerFamilyDtls = null;
		List<MstPensionerFamilyDtls> lLstFamilyDtls = null;
		String tempPCode = null;
		StringBuilder lSBQuery = new StringBuilder();
		Map lMapFamilyDtls = new HashMap<String, Object>();
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();

		try {

			lSBQuery.append("SELECT fd.name,fd.percentage, fd.relationship, fd.accnt_No, fd.date_Of_Birth,fd.date_Of_Death,fd.major_Flag,  fd.married_Flag, ");
			lSBQuery.append(" fd.salary, fd.handicape_Flag, fd.guardian_Name, fd.bank_Code, fd.branch_Code, fd.pensioner_Code ");
			lSBQuery.append(" FROM Mst_Pensioner_Family_Dtls fd,Trn_Pension_Rqst_Hdr rq,mst_pensioner_hdr ms ");
			lSBQuery.append(" WHERE fd.pensioner_code  = rq.pensioner_code  ");
			lSBQuery.append(" and rq.pensioner_code = ms.pensioner_code ");
			lSBQuery.append(" AND rq.case_status = :CaseSt ");
			lSBQuery.append(" AND rq.DA_RATE_FOR_STATE = :lStateCodeForDA  ");
			lSBQuery.append(" AND rq.calc_type = :calcType ");
			lSBQuery.append(" and rq.rop_type = :lROP ");
			lSBQuery.append(" AND rq.status = :lStatus ");
			lSBQuery.append(" AND rq.dp_flag = :lSpecialCase ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("CaseSt", bundleConst.getString("STATFLG.APPROVED"));
			lQuery.setBigDecimal("lStateCodeForDA", new BigDecimal(stateCode));
			lQuery.setString("calcType", "A");
			lQuery.setString("lROP", "1996");
			lQuery.setString("lStatus", "CONTINUE");
			lQuery.setString("lSpecialCase", "Y");

			List lLstVO = lQuery.list();
			if (!lLstVO.isEmpty()) {
				// lLstFamilyDtls = new ArrayList<MstPensionerFamilyDtls>();

				Iterator itr = lLstVO.iterator();
				while (itr.hasNext()) {
					lObjMstPensionerFamilyDtls = new MstPensionerFamilyDtls();
					Object[] tuple = (Object[]) itr.next();

					lObjMstPensionerFamilyDtls.setName(tuple[0] != null ? tuple[0].toString() : "");
					lObjMstPensionerFamilyDtls.setPercentage(100L);
					lObjMstPensionerFamilyDtls.setRelationship(tuple[2] != null ? tuple[2].toString() : "");
					lObjMstPensionerFamilyDtls.setAccntNo(tuple[3] != null ? tuple[3].toString() : "");
					lObjMstPensionerFamilyDtls.setDateOfBirth((Date) (tuple[4] != null ? (tuple[4]) : null));
					lObjMstPensionerFamilyDtls.setDateOfDeath((Date) (tuple[5] != null ? (tuple[5]) : null));
					lObjMstPensionerFamilyDtls.setMajorFlag(tuple[6] != null ? tuple[6].toString() : "");
					lObjMstPensionerFamilyDtls.setMarriedFlag(tuple[7] != null ? tuple[7].toString() : "");
					lObjMstPensionerFamilyDtls.setSalary(tuple[8] != null ? new BigDecimal(tuple[8].toString()) : BigDecimal.ZERO);
					lObjMstPensionerFamilyDtls.setHandicapeFlag(tuple[9] != null ? tuple[9].toString() : "");
					lObjMstPensionerFamilyDtls.setGuardianName(tuple[10] != null ? tuple[10].toString() : "");
					lObjMstPensionerFamilyDtls.setBankCode(tuple[11] != null ? tuple[11].toString() : "");
					lObjMstPensionerFamilyDtls.setBranchCode(tuple[12] != null ? tuple[12].toString() : "");
					lObjMstPensionerFamilyDtls.setPensionerCode(tuple[13] != null ? tuple[13].toString() : "");

					tempPCode = lObjMstPensionerFamilyDtls.getPensionerCode();

					lMapFamilyDtls.put("Family" + tempPCode, lObjMstPensionerFamilyDtls);

					// lLstFamilyDtls.add(lObjMstPensionerFamilyDtls);
					lObjMstPensionerFamilyDtls = null;
				}
			}

			return lMapFamilyDtls;
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	/* Added by Vidhi -- End */

	/* Added by Vidhi */
	public Map getMstFamilyDtlsMapFor4thPay(String stateCode, String lStrForPension) throws Exception {

		MstPensionerFamilyDtls lObjMstPensionerFamilyDtls = null;
		List<MstPensionerFamilyDtls> lLstFamilyDtls = null;
		String tempPCode = null;
		StringBuilder lSBQuery = new StringBuilder();
		Map lMapFamilyDtls = new HashMap<String, Object>();
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();

		try {

			lSBQuery.append("SELECT fd.name," + " fd.percentage, " + " fd.relationship, " + " fd.accnt_No, " + " fd.date_Of_Birth, " + " fd.date_Of_Death, " + " fd.major_Flag, " + " fd.married_Flag,"
					+ " fd.salary," + " fd.handicape_Flag," + " fd.guardian_Name," + " fd.bank_Code," + " fd.branch_Code,"
					+ " fd.pensioner_Code"
					+ " FROM Mst_Pensioner_Family_Dtls fd,Trn_Pension_Rqst_Hdr rq,Trn_Pnsncase_Rop_Rlt pr,mst_pensioner_hdr ms "
					+ " WHERE fd.pensioner_code  = rq.pensioner_code AND rq.case_status = :CaseSt "
					+ " AND rq.DA_RATE_FOR_STATE = :lStateCodeForDA AND rq.calc_type = :calcType AND pr.ppo_no = rq.ppo_no AND pr.rop = :lROP"
					+ " AND fd.percentage = :lPer AND ms.pensioner_code = rq.pensioner_code AND ms.case_status = rq.case_status "
					+ " AND rq.status = :lStatus AND rq.dp_flag = :lSpecialCase "
					+
					// " AND rq.pensioner_code = 532738 "+
					" AND ms.date_of_death IS NOT NULL "
					+
					// " AND rq.pensioner_code = pr.pensioner_code " +
					" AND pr.location_code = rq.location_code " + "AND NOT EXISTS(" + "SELECT 1 FROM trn_pnsncase_rop_rlt rp1 WHERE rp1.location_code = pr.location_code"
					+ " AND rp1.ppo_no = pr.ppo_no AND rp1.rop = :lROP1)" + " AND NOT EXISTS(" + "SELECT 1 FROM trn_pnsncase_rop_rlt rp1 where rp1.location_code = pr.location_code"
					+ " AND rp1.ppo_no = pr.ppo_no AND rp1.rop = :lROP2)");

			if (lStrForPension.equalsIgnoreCase("UPTO 1750")) {
				lSBQuery.append(" AND rq.basic_pension_amount <= 1750 ");
			} else if (lStrForPension.equalsIgnoreCase("UPTO 3000")) {
				lSBQuery.append(" AND rq.basic_pension_amount > 1750 AND rq.basic_pension_amount <= 3000");
			} else if (lStrForPension.equalsIgnoreCase("UPTO 3000")) {
				lSBQuery.append(" AND rq.basic_pension_amount > 3000");
			}

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setLong("lPer", 100);
			lQuery.setString("CaseSt", bundleConst.getString("STATFLG.APPROVED"));
			lQuery.setBigDecimal("lStateCodeForDA", new BigDecimal(stateCode));
			lQuery.setString("calcType", "A");
			lQuery.setString("lROP", "1986");
			lQuery.setString("lStatus", "CONTINUE");
			lQuery.setString("lSpecialCase", "N");
			lQuery.setString("lROP1", "1996");
			lQuery.setString("lROP2", "2006");

			List lLstVO = lQuery.list();
			if (!lLstVO.isEmpty()) {
				// lLstFamilyDtls = new ArrayList<MstPensionerFamilyDtls>();

				Iterator itr = lLstVO.iterator();
				while (itr.hasNext()) {
					lObjMstPensionerFamilyDtls = new MstPensionerFamilyDtls();
					Object[] tuple = (Object[]) itr.next();

					lObjMstPensionerFamilyDtls.setName(tuple[0] != null ? tuple[0].toString() : "");
					lObjMstPensionerFamilyDtls.setPercentage(100L);
					lObjMstPensionerFamilyDtls.setRelationship(tuple[2] != null ? tuple[2].toString() : "");
					lObjMstPensionerFamilyDtls.setAccntNo(tuple[3] != null ? tuple[3].toString() : "");
					lObjMstPensionerFamilyDtls.setDateOfBirth((Date) (tuple[4] != null ? (tuple[4]) : null));
					lObjMstPensionerFamilyDtls.setDateOfDeath((Date) (tuple[5] != null ? (tuple[5]) : null));
					lObjMstPensionerFamilyDtls.setMajorFlag(tuple[6] != null ? tuple[6].toString() : "");
					lObjMstPensionerFamilyDtls.setMarriedFlag(tuple[7] != null ? tuple[7].toString() : "");
					lObjMstPensionerFamilyDtls.setSalary(tuple[8] != null ? new BigDecimal(tuple[8].toString()) : BigDecimal.ZERO);
					lObjMstPensionerFamilyDtls.setHandicapeFlag(tuple[9] != null ? tuple[9].toString() : "");
					lObjMstPensionerFamilyDtls.setGuardianName(tuple[10] != null ? tuple[10].toString() : "");
					lObjMstPensionerFamilyDtls.setBankCode(tuple[11] != null ? tuple[11].toString() : "");
					lObjMstPensionerFamilyDtls.setBranchCode(tuple[12] != null ? tuple[12].toString() : "");
					lObjMstPensionerFamilyDtls.setPensionerCode(tuple[13] != null ? tuple[13].toString() : "");

					tempPCode = lObjMstPensionerFamilyDtls.getPensionerCode();

					lMapFamilyDtls.put("Family" + tempPCode, lObjMstPensionerFamilyDtls);

					// lLstFamilyDtls.add(lObjMstPensionerFamilyDtls);
					lObjMstPensionerFamilyDtls = null;
				}
			}

			return lMapFamilyDtls;
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	/* Added by Vidhi -- End */

	public Map getPenCutMap(String headCode, Integer lFromMonth, Integer lToMonth) throws Exception {

		/*
		 * SELECT SUM(it.AMOUNT) ,
		 * it.TYPE_FLAG,it.FROM_MONTH,it.TO_MONTH,rq.pensioner_code FROM
		 * TRN_PENSION_IT_CUT_DTLS it, trn_pension_rqst_hdr rq,
		 * Trn_Pnsncase_Rop_Rlt pr WHERE it.PENSIONER_CODE = rq.pensioner_code
		 * AND it.pension_request_id = rq.pension_request_id AND rq.ppo_no =
		 * pr.ppo_no AND rq.case_status = 'APPROVED' AND rq.head_code = 1 AND
		 * rq.calc_type = 'A' AND pr.rop = 2006 AND it.TYPE_FLAG IN ('PT' ,'PP')
		 * AND (it.FROM_MONTH <= 200906 OR it.from_month = 0) AND it.to_month
		 * >=200910 GROUP BY
		 * it.TYPE_FLAG,it.FROM_MONTH,it.TO_MONTH,rq.pensioner_code ORDER BY
		 * rq.pensioner_code,it.type_flag
		 */

		Map<String, Object> lCutDtlMap = null;
		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		int lFMonth = 0;
		int lTMonth = 0;
		String lStrTypeFlg = null;
		List lTempLst = null;
		Double lPcut = 0d;
		lCutDtlMap = new HashMap<String, Object>();
		try {
			lSBQuery.append(" SELECT SUM(it.AMOUNT) lAmnt, it.TYPE_FLAG typeFlag,it.FROM_MONTH frmMonth, " + " it.TO_MONTH toMonth,rq.pensioner_code  pCode " + "	  FROM TRN_PENSION_IT_CUT_DTLS it, "
					+ "	       trn_pension_rqst_hdr rq, " + "	       Trn_Pnsncase_Rop_Rlt pr " + "	 WHERE it.PENSIONER_CODE = rq.pensioner_code AND  " + "	       rq.ppo_no = pr.ppo_no AND "
					+ "	       rq.case_status = :CaseSt AND " + "	       rq.head_code = :lHeadCode AND  " + "	       rq.calc_type = :calcType AND " + "	       pr.rop = :lROP AND  "
					+ "	       it.TYPE_FLAG IN (:lType1 ,:lType2) AND " + "	       (it.FROM_MONTH <= :frmMnth OR it.from_month = 0) " +
					// "   and rq.pensioner_code = 525073 "+
					// " AND rq.pensioner_code = pr.pensioner_code " +
					" AND pr.location_code = rq.location_code " + "	 GROUP BY it.TYPE_FLAG,it.FROM_MONTH,it.TO_MONTH,rq.pensioner_code " + "	 ORDER BY rq.pensioner_code,it.type_flag ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("CaseSt", bundleConst.getString("STATFLG.APPROVED"));
			lQuery.setBigDecimal("lHeadCode", new BigDecimal(headCode));
			lQuery.setString("calcType", "A");
			lQuery.setString("lROP", "2006");
			lQuery.setString("lType1", "PT");
			lQuery.setString("lType2", "PP");
			lQuery.setInteger("frmMnth", lFromMonth);
			// lQuery.setInteger("toMnth",lToMonth );

			lQuery.addScalar("pCode", Hibernate.STRING);
			lQuery.addScalar("typeFlag", Hibernate.STRING);
			lQuery.addScalar("frmMonth", Hibernate.INTEGER);
			lQuery.addScalar("toMonth", Hibernate.INTEGER);
			lQuery.addScalar("lAmnt", Hibernate.BIG_DECIMAL);

			List resultList = lQuery.list();
			if (resultList != null && !resultList.isEmpty()) {
				for (Object lObj : resultList) {
					Object[] lArrObj = (Object[]) lObj;
					lTempLst = new ArrayList();

					lStrTypeFlg = lArrObj[1].toString();
					lFMonth = lArrObj[2] == null ? 0 : Integer.valueOf(lArrObj[2].toString());
					lTMonth = lArrObj[3] == null ? 0 : Integer.valueOf(lArrObj[3].toString());

					if ("PP".equals(lStrTypeFlg)) {
						lCutDtlMap.put(lArrObj[4] + "PP", Double.valueOf(lArrObj[0].toString()));
					}
					if (lFMonth != 0 && lTMonth != 0 && lFromMonth != 0 && lToMonth != 0) {
						for (Integer i = lFromMonth; i <= lToMonth;) {
							if ("PT".equals(lStrTypeFlg)) {
								lPcut = Double.valueOf(lArrObj[0].toString());
							}
							if (lCutDtlMap.containsKey(lArrObj[0] + "PT" + "~" + i) && ("PT".equals(lStrTypeFlg))) {
								lPcut += Double.valueOf(lCutDtlMap.get(lArrObj[4] + "PT" + "~" + i).toString());
							}
							if (lPcut > 0 && ("PT".equals(lStrTypeFlg))) {
								lCutDtlMap.put(lArrObj[4] + "PT" + "~" + i, lPcut);
							}

							lPcut = 0d;
							i += ((Integer.parseInt((i.toString().substring(4, 6)))) == 12) ? 89 : 1;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lCutDtlMap;

	}

	/* Added by Vidhi */

	public Map getPenCutMapFor5thPay(String headCode, Integer lFromMonth, Integer lToMonth) throws Exception {

		/*
		 * SELECT SUM(it.AMOUNT) ,
		 * it.TYPE_FLAG,it.FROM_MONTH,it.TO_MONTH,rq.pensioner_code FROM
		 * TRN_PENSION_IT_CUT_DTLS it, trn_pension_rqst_hdr rq,
		 * Trn_Pnsncase_Rop_Rlt pr WHERE it.PENSIONER_CODE = rq.pensioner_code
		 * AND it.pension_request_id = rq.pension_request_id AND rq.ppo_no =
		 * pr.ppo_no AND rq.case_status = 'APPROVED' AND rq.head_code = 1 AND
		 * rq.calc_type = 'A' AND pr.rop = 2006 AND it.TYPE_FLAG IN ('PT' ,'PP')
		 * AND (it.FROM_MONTH <= 200906 OR it.from_month = 0) AND it.to_month
		 * >=200910 GROUP BY
		 * it.TYPE_FLAG,it.FROM_MONTH,it.TO_MONTH,rq.pensioner_code ORDER BY
		 * rq.pensioner_code,it.type_flag
		 */

		Map<String, Object> lCutDtlMap = null;
		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		int lFMonth = 0;
		int lTMonth = 0;
		String lStrTypeFlg = null;
		List lTempLst = null;
		Double lPcut = 0d;
		lCutDtlMap = new HashMap<String, Object>();
		try {
			lSBQuery.append(" SELECT SUM(it.AMOUNT) lAmnt, it.TYPE_FLAG typeFlag,it.FROM_MONTH frmMonth, " + " it.TO_MONTH toMonth,rq.pensioner_code  pCode " + "	  FROM TRN_PENSION_IT_CUT_DTLS it, "
					+ "	       trn_pension_rqst_hdr rq, " + "	       Trn_Pnsncase_Rop_Rlt pr " + "	 WHERE it.PENSIONER_CODE = rq.pensioner_code AND  " + "	       rq.ppo_no = pr.ppo_no AND "
					+ "	       rq.case_status = :CaseSt AND " + "	       rq.head_code = :lHeadCode AND  " + "	       rq.calc_type = :calcType AND " + "	       pr.rop = :lROP AND  "
					+ "	       it.TYPE_FLAG IN (:lType1 ,:lType2) AND "
					+ "	       (it.FROM_MONTH <= :frmMnth OR it.from_month = 0) "
					+
					// "   and rq.pensioner_code = 532909 "+
					// " AND rq.pensioner_code = pr.pensioner_code " +
					" AND pr.location_code = rq.location_code " + " AND NOT EXISTS(" + "SELECT 1 FROM trn_pnsncase_rop_rlt rp1 WHERE rp1.location_code = pr.location_code"
					+ " AND rp1.ppo_no = pr.ppo_no AND rp1.rop = :lROP1) " + "	 GROUP BY it.TYPE_FLAG,it.FROM_MONTH,it.TO_MONTH,rq.pensioner_code " + "	 ORDER BY rq.pensioner_code,it.type_flag ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("CaseSt", bundleConst.getString("STATFLG.APPROVED"));
			lQuery.setBigDecimal("lHeadCode", new BigDecimal(headCode));
			lQuery.setString("calcType", "A");
			lQuery.setString("lROP", "1996");
			lQuery.setString("lType1", "PT");
			lQuery.setString("lType2", "PP");
			lQuery.setInteger("frmMnth", lFromMonth);
			lQuery.setString("lROP1", "2006");
			// lQuery.setInteger("toMnth",lToMonth );

			lQuery.addScalar("pCode", Hibernate.STRING);
			lQuery.addScalar("typeFlag", Hibernate.STRING);
			lQuery.addScalar("frmMonth", Hibernate.INTEGER);
			lQuery.addScalar("toMonth", Hibernate.INTEGER);
			lQuery.addScalar("lAmnt", Hibernate.BIG_DECIMAL);

			List resultList = lQuery.list();
			if (resultList != null && !resultList.isEmpty()) {
				for (Object lObj : resultList) {
					Object[] lArrObj = (Object[]) lObj;
					lTempLst = new ArrayList();

					lStrTypeFlg = lArrObj[1].toString();
					lFMonth = lArrObj[2] == null ? 0 : Integer.valueOf(lArrObj[2].toString());
					lTMonth = lArrObj[3] == null ? 0 : Integer.valueOf(lArrObj[3].toString());

					if ("PP".equals(lStrTypeFlg)) {
						lCutDtlMap.put(lArrObj[4] + "PP", Double.valueOf(lArrObj[0].toString()));
					}
					if (lFMonth != 0 && lTMonth != 0 && lFromMonth != 0 && lToMonth != 0) {
						for (Integer i = lFromMonth; i <= lToMonth;) {
							if ("PT".equals(lStrTypeFlg)) {
								lPcut = Double.valueOf(lArrObj[0].toString());
							}
							if (lCutDtlMap.containsKey(lArrObj[0] + "PT" + "~" + i) && ("PT".equals(lStrTypeFlg))) {
								lPcut += Double.valueOf(lCutDtlMap.get(lArrObj[4] + "PT" + "~" + i).toString());
							}
							if (lPcut > 0 && ("PT".equals(lStrTypeFlg))) {
								lCutDtlMap.put(lArrObj[4] + "PT" + "~" + i, lPcut);
							}

							lPcut = 0d;
							i += ((Integer.parseInt((i.toString().substring(4, 6)))) == 12) ? 89 : 1;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lCutDtlMap;

	}

	/* Start -- Added by Vidhi */
	public Map getPenCutMapFor4thPay(String headCode, Integer lFromMonth, Integer lToMonth, String lStrForPension) throws Exception {

		/*
		 * SELECT SUM(it.AMOUNT) ,
		 * it.TYPE_FLAG,it.FROM_MONTH,it.TO_MONTH,rq.pensioner_code FROM
		 * TRN_PENSION_IT_CUT_DTLS it, trn_pension_rqst_hdr rq,
		 * Trn_Pnsncase_Rop_Rlt pr WHERE it.PENSIONER_CODE = rq.pensioner_code
		 * AND it.pension_request_id = rq.pension_request_id AND rq.ppo_no =
		 * pr.ppo_no AND rq.case_status = 'APPROVED' AND rq.head_code = 1 AND
		 * rq.calc_type = 'A' AND pr.rop = 2006 AND it.TYPE_FLAG IN ('PT' ,'PP')
		 * AND (it.FROM_MONTH <= 200906 OR it.from_month = 0) AND it.to_month
		 * >=200910 GROUP BY
		 * it.TYPE_FLAG,it.FROM_MONTH,it.TO_MONTH,rq.pensioner_code ORDER BY
		 * rq.pensioner_code,it.type_flag
		 */

		Map<String, Object> lCutDtlMap = null;
		StringBuilder lSBQuery = new StringBuilder();
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		int lFMonth = 0;
		int lTMonth = 0;
		String lStrTypeFlg = null;
		List lTempLst = null;
		Double lPcut = 0d;
		lCutDtlMap = new HashMap<String, Object>();
		try {
			lSBQuery.append(" SELECT SUM(it.AMOUNT) lAmnt, it.TYPE_FLAG typeFlag,it.FROM_MONTH frmMonth, " + " it.TO_MONTH toMonth,rq.pensioner_code  pCode " + "	  FROM TRN_PENSION_IT_CUT_DTLS it, "
					+ "	       trn_pension_rqst_hdr rq, " + "	       Trn_Pnsncase_Rop_Rlt pr " + "	 WHERE it.PENSIONER_CODE = rq.pensioner_code AND  " + "	       rq.ppo_no = pr.ppo_no AND "
					+ "	       rq.case_status = :CaseSt AND " + "	       rq.head_code = :lHeadCode AND  "
					+ "	       rq.calc_type = :calcType AND "
					+ "	       pr.rop = :lROP AND  "
					+ "	       it.TYPE_FLAG IN (:lType1 ,:lType2) AND "
					+ "	       (it.FROM_MONTH <= :frmMnth OR it.from_month = 0) "
					+
					// "   and rq.pensioner_code = 532909 "+
					// " AND rq.pensioner_code = pr.pensioner_code " +
					" AND pr.location_code = rq.location_code " + " AND NOT EXISTS(" + "SELECT 1 FROM trn_pnsncase_rop_rlt rp1 WHERE rp1.location_code = pr.location_code"
					+ " AND rp1.ppo_no = pr.ppo_no AND rp1.rop = :lROP1) " + " AND NOT EXISTS(" + "SELECT 1 FROM trn_pnsncase_rop_rlt rp1 WHERE rp1.location_code = pr.location_code"
					+ " AND rp1.ppo_no = pr.ppo_no AND rp1.rop = :lROP2) ");

			if (lStrForPension.equalsIgnoreCase("UPTO 1750")) {
				lSBQuery.append(" AND rq.basic_pension_amount <= 1750 ");
			} else if (lStrForPension.equalsIgnoreCase("UPTO 3000")) {
				lSBQuery.append(" AND rq.basic_pension_amount > 1750 AND rq.basic_pension_amount <= 3000");
			} else if (lStrForPension.equalsIgnoreCase("UPTO 3000")) {
				lSBQuery.append(" AND rq.basic_pension_amount > 3000");
			}

			lSBQuery.append(" GROUP BY it.TYPE_FLAG,it.FROM_MONTH,it.TO_MONTH,rq.pensioner_code " + " ORDER BY rq.pensioner_code,it.type_flag ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("CaseSt", bundleConst.getString("STATFLG.APPROVED"));
			lQuery.setBigDecimal("lHeadCode", new BigDecimal(headCode));
			lQuery.setString("calcType", "A");
			lQuery.setString("lROP", "1986");
			lQuery.setString("lType1", "PT");
			lQuery.setString("lType2", "PP");
			lQuery.setInteger("frmMnth", lFromMonth);
			lQuery.setString("lROP1", "1996");
			lQuery.setString("lROP2", "2006");
			// lQuery.setInteger("toMnth",lToMonth );

			lQuery.addScalar("pCode", Hibernate.STRING);
			lQuery.addScalar("typeFlag", Hibernate.STRING);
			lQuery.addScalar("frmMonth", Hibernate.INTEGER);
			lQuery.addScalar("toMonth", Hibernate.INTEGER);
			lQuery.addScalar("lAmnt", Hibernate.BIG_DECIMAL);

			List resultList = lQuery.list();
			if (resultList != null && !resultList.isEmpty()) {
				for (Object lObj : resultList) {
					Object[] lArrObj = (Object[]) lObj;
					lTempLst = new ArrayList();

					lStrTypeFlg = lArrObj[1].toString();
					lFMonth = lArrObj[2] == null ? 0 : Integer.valueOf(lArrObj[2].toString());
					lTMonth = lArrObj[3] == null ? 0 : Integer.valueOf(lArrObj[3].toString());

					if ("PP".equals(lStrTypeFlg)) {
						lCutDtlMap.put(lArrObj[4] + "PP", Double.valueOf(lArrObj[0].toString()));
					}
					if (lFMonth != 0 && lTMonth != 0 && lFromMonth != 0 && lToMonth != 0) {
						for (Integer i = lFromMonth; i <= lToMonth;) {
							if ("PT".equals(lStrTypeFlg)) {
								lPcut = Double.valueOf(lArrObj[0].toString());
							}
							if (lCutDtlMap.containsKey(lArrObj[0] + "PT" + "~" + i) && ("PT".equals(lStrTypeFlg))) {
								lPcut += Double.valueOf(lCutDtlMap.get(lArrObj[4] + "PT" + "~" + i).toString());
							}
							if (lPcut > 0 && ("PT".equals(lStrTypeFlg))) {
								lCutDtlMap.put(lArrObj[4] + "PT" + "~" + i, lPcut);
							}

							lPcut = 0d;
							i += ((Integer.parseInt((i.toString().substring(4, 6)))) == 12) ? 89 : 1;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lCutDtlMap;

	}

	/* End -- Added by Vidhi */

	public void calProcForSTI(String lHeadCode, Double lOldRate, Double lNewRate, String lStrMnthString, Long lPostId, String lLocCode) {

		logger.info("inside calProcForSTI calling proc_pension_rop_96_sti now");
		logger.info("-----Parameters For Procedure--------");
		logger.info("headcode=" + lHeadCode);
		logger.info("OldRate=" + lOldRate);
		logger.info("NewRate=" + lNewRate);
		logger.info("MonthString=" + lStrMnthString);
		Connection conn = null;
		CallableStatement statement = null;
		Session hibSession = null;
		try {
			hibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();

			conn = hibSession.connection();

			ResourceBundle bundleApplicationDB = ResourceBundle.getBundle("ApplicationDB");
			boolean isOracleDB = true;
			if (bundleApplicationDB.getString("DBTYPE").equalsIgnoreCase("mysql")) {
				isOracleDB = false;
			}
			if (isOracleDB) {
				statement = conn.prepareCall("{call pkg_pension.proc_pension_rop_96_sti(?, ?, ?, ?, ?, ?)}");
			} else {
				statement = conn.prepareCall("{call proc_pension_rop_96_sti(?, ?, ?, ?, ?, ?)}");
			}

			statement.setLong(1, Long.valueOf(lHeadCode));
			statement.setDouble(2, lOldRate);
			statement.setDouble(3, lNewRate);
			statement.setString(4, lStrMnthString);
			statement.setLong(5, lPostId);
			statement.setString(6, lLocCode);
			java.util.Calendar calBefore = java.util.Calendar.getInstance();
			logger.info("Before call proc_pension_rop_96_sti time=" + calBefore.getTime());
			statement.execute();
			java.util.Calendar calAfter = java.util.Calendar.getInstance();
			logger.info("After call proc_pension_rop_96_sti Executed Successfully time=" + calAfter.getTime());
			logger.info("proc_pension_rop_96_sti Exectution time in minutes=" + ((calAfter.getTimeInMillis() - calBefore.getTimeInMillis()) / (1000 * 60)));
		} catch (Exception e) {
			logger.error("Exception in calProcForSTI So making active=N ");
			List resultList;
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("UPDATE trn_pension_rop_queue" + " SET active = 'N'" + " WHERE head_code = " + Long.valueOf(lHeadCode) + " AND old_rate = " + lOldRate + " AND new_rate = " + lNewRate
					+ " AND arr_type = 'STI' ");

			Query hqlQuery = hibSession.createSQLQuery(strQuery.toString());
			hqlQuery.executeUpdate();
			logger.error("Exception in calProcForSTI Query -->" + strQuery.toString());
			logger.error("Exception in calProcForSTI");
		} finally {
			try {
				statement.close();
				conn.close();
			} catch (Exception e1) {
			}
		}
		logger.info("Going out of generateVariationReportData");
	}

	public String chkStatusForHeadCode(String lHeadCode) throws Exception {

		Session hibSession = null;
		String lChActv = "0";
		try {
			hibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select '1' flag from trn_pension_rop_queue" + " WHERE head_code = :HeadCode and active = 'Y' ");

			SQLQuery hqlQuery = hibSession.createSQLQuery(strQuery.toString());
			hqlQuery.setParameter("HeadCode", lHeadCode);
			hqlQuery.addScalar("flag", Hibernate.STRING);

			lChActv = (String) hqlQuery.uniqueResult();

		} catch (Exception e) {
			logger.info("Error in fething the Actv Status");
			throw e;
		}
		return lChActv;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.AdminRateMstDAO#getDARateDetails(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */

	public List getDARateDetails(String strDARateType, String strHeadCodeType, String strForPension) {

		List lLstDARateDetails = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		String lStrAmount = null;
		Session hibSession = null;
		try {
			hibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT DArate.effectiveFromDate,DArate.effectiveToDate,DArate.rate,DArate.minAmount,DArate.pensionHeadcodeRateId FROM RltPensionHeadcodeRate DArate WHERE DArate.fieldType =:fieldType AND DArate.headCode =:headCode ");
			if (strDARateType != null && !strDARateType.equals("-1")) {
				if (strForPension != null && !strForPension.equals("") && !strForPension.equals("-1")) {
					lStrAmount = strForPension.substring(strForPension.length() - 4, strForPension.length());
					if (!strForPension.equals("ABOVE 3001")) {
						lSBQuery.append(" AND DArate.uptoBasic <= :uptoBasic  ");
					} else {
						lSBQuery.append(" AND DArate.uptoBasic > :uptoBasic  ");
					}
				}

			}
			lSBQuery.append(" ORDER BY DArate.effectiveToDate DESC ");

			lHibQry = hibSession.createQuery(lSBQuery.toString());
			if (strDARateType != null && !strDARateType.equals("-1")) {
				lHibQry.setParameter("fieldType", strDARateType);
				lHibQry.setParameter("headCode", BigDecimal.valueOf(Long.valueOf(strHeadCodeType.trim())));
				if (strForPension != null && !strForPension.equals("-1") && !strForPension.equals("")) {
					lHibQry.setParameter("uptoBasic", BigDecimal.valueOf(Long.valueOf(lStrAmount)));
				}
			}

			lLstDARateDetails = lHibQry.list();

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is :" + e, e);
		}

		return lLstDARateDetails;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.AdminRateMstDAO#chkDateIsOverLapOrNot(java
	 * .lang.String, java.lang.String, java.lang.String, java.util.Date,
	 * java.util.Date, java.util.Date, java.util.Date)
	 */

	public String chkDateIsOverLapOrNot(String strDARateType, String strForPension, String strHeadCodeType, Date dtFromDate, Date dtToDate, Date dtEffctvFromDate, Date dtEffctvToDate) {

		Session lHibSession = null;
		String lStrResVal = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		String lStrAmount = null;
		Integer lIntCnt = null;
		try {
			lHibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT COUNT(*) FROM RltPensionHeadcodeRate WHERE fieldType =:fieldType AND headCode =:headCode ");
			lSBQuery.append(" AND :Date BETWEEN :dtEffctvFromDate AND :dtEffctvToDate ");
			if (strDARateType != null && strDARateType.length() > 0) {
				if (strForPension != null && strForPension.length() > 0) {
					lStrAmount = strForPension.substring(strForPension.length() - 4, strForPension.length());
					if (!strForPension.equals("ABOVE 3001")) {
						lSBQuery.append(" AND uptoBasic <= :uptoBasic  ");
					} else {
						lSBQuery.append(" AND uptoBasic > :uptoBasic  ");
					}
				}

			}
			lHibQry = lHibSession.createQuery(lSBQuery.toString());

			if (strDARateType != null && strDARateType.length() > 0) {
				lHibQry.setParameter("fieldType", strDARateType);
				lHibQry.setParameter("headCode", BigDecimal.valueOf(Long.valueOf(strHeadCodeType.trim())));
				if (dtFromDate != null && dtFromDate.toString().length() > 0) {
					lHibQry.setParameter("Date", dtFromDate);
				}
				if (dtToDate != null && dtToDate.toString().length() > 0) {
					lHibQry.setParameter("Date", dtToDate);
				}
				lHibQry.setParameter("dtEffctvFromDate", dtEffctvFromDate);
				lHibQry.setParameter("dtEffctvToDate", dtEffctvToDate);

				if (strForPension != null && strForPension.length() > 0) {
					lHibQry.setParameter("uptoBasic", BigDecimal.valueOf(Long.valueOf(lStrAmount)));
				}
			}
			if (!lHibQry.list().isEmpty()) {
				lIntCnt = Integer.parseInt(lHibQry.list().get(0).toString());
			}
			if (lIntCnt > 0) {
				lStrResVal = "Y";
			} else {
				lStrResVal = "N";
			}

		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			// e.printStackTrace();
		}
		return lStrResVal;

	}

	public Integer getDARateConfigForStateCount(Long gLngLangId, String lStrLocCode, Map displayTag) throws Exception {

		Integer lIntCount = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		Session lHibSession = null;
		try {
			lHibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT COUNT(*) ");
			lSBQuery.append(" FROM MstPensionStateRate \n");
			lSBQuery.append(" WHERE langId =:langId  \n");
			// lSBQuery.append(" AND locationCode = :locationCode ");
			lHibQry = lHibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("langId", gLngLangId);
			// lHibQry.setParameter("locationCode",Long.valueOf(lStrLocCode));
			if (lHibQry.list() != null && lHibQry.list().size() > 0) {
				lIntCount = Integer.parseInt(lHibQry.list().get(0).toString());
			} else {
				lIntCount = 0;
			}
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lIntCount;
	}

	public List getDARateConfigForStateDtls(Long gLngLangId, String gStrLocCode, Map displayTag) throws Exception {

		List lLstDARateConfigForStateDtls = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		Session lHibSession = null;
		try {

			lHibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			lSBQuery = new StringBuffer();

			String[] columnValues = new String[]{"", "pensionStateRateId", "stateCode", "stateDesc"};
			lSBQuery.append(" SELECT pensionStateRateId,stateCode,stateDesc \n");
			lSBQuery.append(" FROM MstPensionStateRate  \n");
			lSBQuery.append(" WHERE langId =:langId  \n");
			// lSBQuery.append(" AND locationCode = :locationCode ");
			String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag.get(Constants.KEY_SORT_ORDER) : "desc");

			Integer orderbypara = null;

			if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				lSBQuery.append(columnValues[orderbypara.intValue()] + " " + orderString);
			} else {
				lSBQuery.append(" ORDER BY stateCode");
			}
			lHibQry = lHibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("langId", gLngLangId);
			// lHibQry.setParameter("locationCode",Long.valueOf(gStrLocCode));

			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lHibQry.setFirstResult((pageNo.intValue() - 1) * Constants.PDWL_PAGE_SIZE);
			lHibQry.setMaxResults(Constants.PDWL_PAGE_SIZE);
			lLstDARateConfigForStateDtls = lHibQry.list();
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is :" + e, e);
		}

		return lLstDARateConfigForStateDtls;

	}

	public Long getMaxStateCode(Long lLngLangId, Long lLngLocationCode) {

		Long llngStateCode = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		Session lHibSession = null;
		try {
			lHibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT MAX(stateCode) ");
			lSBQuery.append(" FROM  MstPensionStateRate ");
			lSBQuery.append(" WHERE langId =:langId ");
			// lSBQuery.append(" AND locationCode = :locationCode ");
			hqlQuery = lHibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("langId", lLngLangId);
			// hqlQuery.setParameter("locationCode",lLngLocationCode);
			if (hqlQuery.list().get(0) == null) {
				llngStateCode = 1L;
			} else {
				llngStateCode = Long.valueOf(hqlQuery.list().get(0).toString()) + 1L;
			}
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is :" + e, e);
		}

		return llngStateCode;

	}

	public List getAllStateDept(Long gLngLangId) throws Exception {

		List lLstStateDept = null;
		List lLstResult = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		Session lHibSession = null;
		try {
			lHibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			lSBQuery = new StringBuffer();
			lSBQuery.append("SELECT stateCode,stateDesc FROM MstPensionStateRate WHERE langId = :langId ");
			// lSBQuery.append(" AND locationCode = :locationCode ");
			lHibQry = lHibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("langId", gLngLangId);
			// lHibQry.setParameter("locationCode",lLngLocCode);

			lLstResult = lHibQry.list();
			ComboValuesVO lObjComboValuesVO = null;

			if (lLstResult != null && lLstResult.size() != 0) {
				lLstStateDept = new ArrayList<Object>();
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstStateDept.add(lObjComboValuesVO);
				}
			} else {
				lLstStateDept = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstStateDept.add(lObjComboValuesVO);
			}

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is :" + e, e);
			throw e;
		}

		return lLstStateDept;

	}

	public BigDecimal getMaxRevision(Long lLngHeadCode, String lStrFieldType) throws Exception {

		BigDecimal lLngRevision = null;
		List lLstResData = null;
		try {
			Session ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT MAX(revision) FROM HstRltPensionHeadcodeRate ");
			lSBQuery.append("WHERE headCode =:headCode AND fieldType =:fieldType ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setBigDecimal("headCode", BigDecimal.valueOf(lLngHeadCode));
			lQuery.setParameter("fieldType", lStrFieldType);
			lLstResData = lQuery.list();

			if (lLstResData.get(0) != null) {
				lLngRevision = (BigDecimal) lLstResData.get(0);
			} else {
				lLngRevision = BigDecimal.valueOf(1l);
				return lLngRevision;
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLngRevision.add(BigDecimal.valueOf(1l));
	}

	public Date getMaxDate(Long lLngHeadCode, String lStrFieldType) throws Exception {

		Date EffctFromdate = null;
		List lLstResData = null;
		StringBuilder lSBQuery = null;
		Long lLngCnt = null;
		try {
			Session ghibSession = getSession();

			lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT COUNT(*) FROM RltPensionHeadcodeRate WHERE effectiveToDate is null ");
			lSBQuery.append("AND headCode =:headCode AND fieldType =:fieldType ");
			Query lQueryCnt = ghibSession.createQuery(lSBQuery.toString());
			lQueryCnt.setBigDecimal("headCode", BigDecimal.valueOf(lLngHeadCode));
			lQueryCnt.setParameter("fieldType", lStrFieldType);
			lLstResData = lQueryCnt.list();
			if (lLstResData.size() > 0) {
				lLngCnt = (Long) lLstResData.get(0);
			}

			if (lLngCnt <= 1) {
				lSBQuery = new StringBuilder();
				lSBQuery.append("SELECT effectiveFromDate FROM RltPensionHeadcodeRate ");
				lSBQuery.append("WHERE headCode =:headCode AND fieldType =:fieldType ");
				lSBQuery.append("AND effectiveToDate is null ");
				Query lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setBigDecimal("headCode", BigDecimal.valueOf(lLngHeadCode));
				lQuery.setParameter("fieldType", lStrFieldType);
				lLstResData = lQuery.list();

				if (lLstResData.size() > 0) {
					EffctFromdate = (Date) lLstResData.get(0);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return EffctFromdate;
	}

	public List<Object[]> getPensionerDtlsForDAArrearCalc(Map<String, Object> inputMap) throws Exception {

		List<Object[]> lLstResData = null;
		StringBuilder lSBQuery = null;
		String lStrROPType = (String) inputMap.get("lStrROPType");
		String lStrLocCode = (String) inputMap.get("lStrLocCode");
		String lStrDAForState = (String) inputMap.get("lStrDAForState");
		Integer lIntPayInMonth = (Integer) inputMap.get("lIntPayInMonth");
		Integer lIntEffectFromMonth = (Integer) inputMap.get("lIntEffectFromMonth");
		Integer lIntEffectToMonth = (Integer) inputMap.get("lIntEffectToMonth");
		String lStrRemarks = (String) inputMap.get("lStrRemarks");
		try {
			Session ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append("Select prh.END_DATE," + "prh.COMMENCEMENT_DATE," + "prh.FP1_DATE," + "prh.FP2_DATE," + "mph.DATE_OF_DEATH," + "prh.BASIC_PENSION_AMOUNT," + "prh.FP1_AMOUNT,"
					+ "prh.FP2_AMOUNT," + "prh.PENSIONER_CODE,prh.PENSION_REQUEST_ID,prh.DP_FLAG \n");
			lSBQuery.append("From \n");
			lSBQuery.append("trn_pension_rqst_hdr prh \n");
			lSBQuery.append("join MST_PENSIONER_HDR mph on prh.pensioner_code = mph.pensioner_code and prh.location_code =:lLocCode and prh.ROP_TYPE = :lRopType and prh.DA_RATE_FOR_STATE = :lDAForState and PRH.CASE_STATUS IN ('Approved','Rejected','ModifiedByAuditor','Modified') and (prh.DA_IN_PENSION_SALARY = 'Pension' OR (PRH.RE_EMPLOYMENT_FLAG = 'N' and PRH.DA_IN_PENSION_SALARY IS NULL)) \n");
			lSBQuery.append("where  not exists \n");
			lSBQuery.append("( 	select 1 from TRN_PENSION_ARREAR_DTLS pad \n");
			lSBQuery.append("	where  \n");
			lSBQuery.append("	pad.pensioner_code = prh.pensioner_code  \n");
			lSBQuery.append("	and pad.ARREAR_FIELD_TYPE = 'DA'  \n");
			lSBQuery.append("	and (pad.REMARKS = :lStrRemarks or pad.REMARKS is null)   \n");
			lSBQuery.append("	and ((pad.EFFECTED_FROM_YYYYMM <= :lFromMonthYear and :lFromMonthYear <= pad.EFFECTED_TO_YYYYMM) or \n");
			lSBQuery.append("		(pad.EFFECTED_FROM_YYYYMM <= :lToMonthYear and :lToMonthYear <= pad.EFFECTED_TO_YYYYMM) or \n");
			lSBQuery.append("		(pad.PAYMENT_FROM_YYYYMM = :lPayInMonth and pad.PAYMENT_TO_YYYYMM = :lPayInMonth )) \n");
			lSBQuery.append(") \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("lLocCode", lStrLocCode);
			lQuery.setString("lRopType", lStrROPType);
			lQuery.setBigDecimal("lDAForState", new BigDecimal(lStrDAForState));
			lQuery.setInteger("lPayInMonth", lIntPayInMonth);
			lQuery.setInteger("lFromMonthYear", lIntEffectFromMonth);
			lQuery.setInteger("lToMonthYear", lIntEffectToMonth);
			lQuery.setString("lStrRemarks", lStrRemarks);
			lLstResData = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstResData;
	}

	public Map<String, MstPensionerFamilyDtls> getMstPensionerFamilyDtlsMap(String lStrROPType, String lStrLocCode, String lStrDAForState) throws Exception {

		Map lMapFamilyDtls = new HashMap<String, Object>();
		String tempPCode = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			Session ghibSession = getSession();

			lSBQuery.append("select fd   \n");
			lSBQuery.append(" from  \n ");
			lSBQuery.append(" MstPensionerFamilyDtls fd, TrnPensionRqstHdr prh,MstPensionerHdr mph \n ");
			lSBQuery.append(" where prh.pensionerCode = fd.pensionerCode \n ");
			lSBQuery.append(" and prh.pensionerCode = mph.pensionerCode \n ");
			lSBQuery.append(" and prh.locationCode = :locCode \n ");
			lSBQuery.append(" and prh.ropType = :lRopType \n ");
			lSBQuery.append(" and prh.daRateForState = :lDAForState \n");
			lSBQuery.append(" and fd.percentage=:lPer \n ");
			lSBQuery.append(" and mph.dateOfDeath is not null \n ");
			lSBQuery.append(" and (prh.daInPensionSalary = 'Pension' or prh.daInPensionSalary is null) \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("lPer", 100);
			lQuery.setString("locCode", lStrLocCode);
			lQuery.setString("lRopType", lStrROPType);
			lQuery.setBigDecimal("lDAForState", new BigDecimal(lStrDAForState));

			List lLstVO = lQuery.list();
			if (!lLstVO.isEmpty()) {
				Iterator itr = lLstVO.iterator();
				while (itr.hasNext()) {
					MstPensionerFamilyDtls lObjMstPensionerFamilyDtlsVO = (MstPensionerFamilyDtls) itr.next();
					tempPCode = lObjMstPensionerFamilyDtlsVO.getPensionerCode();
					lMapFamilyDtls.put(tempPCode, lObjMstPensionerFamilyDtlsVO);
				}
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lMapFamilyDtls;
	}

	public int saveDAArrearDtls(Map<String, Object> inputMap) throws Exception {

		String lStrROPType = (String) inputMap.get("lStrROPType");
		String lStrLocCode = (String) inputMap.get("lStrLocCode");
		String lStrDAForState = (String) inputMap.get("lStrDAForState");
		Integer lIntPayInMonth = (Integer) inputMap.get("lIntPayInMonth");
		Integer lIntEffectFromMonth = (Integer) inputMap.get("lIntEffectFromMonth");
		Integer lIntEffectToMonth = (Integer) inputMap.get("lIntEffectToMonth");
		Double lDblOldRate = (Double) inputMap.get("lDblOldRate");
		Double lDblNewRate = (Double) inputMap.get("lDblNewRate");
		Long lLngCreatedPost = (Long) inputMap.get("lLngCreatedPost");
		Long lLngCreatedUser = (Long) inputMap.get("lLngCreatedUser");
		Date lDateCurr = (Date) inputMap.get("lDateCurr");
		String lStrRemarks = (String) inputMap.get("lStrRemarks");
		int updateCount = 0;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			Session ghibSession = getSession();
			// --If effect to month is before april -2012 than getting data from
			// history tables.
			if (lIntEffectToMonth <= 201203) {
				lSBQuery.append("insert into trn_pension_arrear_dtls \n");
				lSBQuery.append("SELECT '22" + lStrLocCode + "' || SEQ_ARREAR_ID_" + lStrLocCode + ".nextval, \n");
				lSBQuery.append("prh.PENSION_REQUEST_ID,prh.PENSIONER_CODE,'DA',null,null,:lFromMonthYear,:lToMonthYear ,0, \n");
				lSBQuery.append("cast(sum(pbda.PENSION_AMOUNT*(:lDblNewRate / 100)  -  pbda.PENSION_AMOUNT*(:lDblOldRate / 100))  as bigint), \n");
				lSBQuery.append(":lPayInMonth,:lPayInMonth,null,1," + lLngCreatedUser + "," + lLngCreatedPost + ",sysdate,null,null,null,'" + lStrRemarks + "',null,null,'N' \n");
				lSBQuery.append("From trn_bill_register tbr \n");
				lSBQuery.append("join TRN_PENSION_BILL_HDR_ARC pbha  on tbr.bill_no = pbha.bill_no and tbr.BILL_CNTRL_NO = '-1' and tbr.LOCATION_CODE =:lLocCode \n");
				lSBQuery.append("join TRN_PENSION_BILL_DTLS_ARC pbda on pbha.TRN_PENSION_BILL_HDR_ARC_ID = pbda.TRN_PENSION_BILL_HDR_ARC_ID and pbda.PAY_FOR_MONTH >= :lFromMonthYear and pbda.PAY_FOR_MONTH <= :lToMonthYear \n");
				lSBQuery.append("join TRN_PENSION_RQST_HDR prh  on prh.pensioner_code = pbda.pensioner_code and prh.location_code = :lLocCode and prh.rop_type = :lRopType and prh.DA_RATE_FOR_STATE = :lDAForState and (prh.DA_IN_PENSION_SALARY = 'Pension' or prh.DA_IN_PENSION_SALARY is null) \n");
				lSBQuery.append("where not exists \n");
				lSBQuery.append("( \n");
				lSBQuery.append("select 1 from TRN_PENSION_ARREAR_DTLS pad \n");
				lSBQuery.append("where pad.pensioner_code = prh.pensioner_code and pad.ARREAR_FIELD_TYPE = 'DA' and  (pad.REMARKS = :lStrRemarks or pad.REMARKS is null) and  \n");
				lSBQuery.append("((pad.EFFECTED_FROM_YYYYMM <= :lFromMonthYear and :lFromMonthYear <= pad.EFFECTED_TO_YYYYMM) or \n");
				lSBQuery.append("(pad.EFFECTED_FROM_YYYYMM <= :lToMonthYear and :lToMonthYear <= pad.EFFECTED_TO_YYYYMM) or \n");
				lSBQuery.append("(pad.PAYMENT_FROM_YYYYMM = :lPayInMonth and pad.PAYMENT_TO_YYYYMM = :lPayInMonth )) \n");
				lSBQuery.append(") \n");
				lSBQuery.append("group by (prh.PENSION_REQUEST_ID,prh.PENSIONER_CODE) \n");
			} else {
				lSBQuery.append("insert into trn_pension_arrear_dtls \n");
				lSBQuery.append("SELECT '22" + lStrLocCode + "' || SEQ_ARREAR_ID_" + lStrLocCode + ".nextval, \n");
				lSBQuery.append("prh.PENSION_REQUEST_ID,prh.PENSIONER_CODE,'DA',null,null,:lFromMonthYear,:lToMonthYear ,0, \n");
				if ("1996".equals(lStrROPType)) {
					lSBQuery.append("cast(sum((pbd.PENSION_AMOUNT+pbd.dp_amount)*(:lDblNewRate / 100)  -  (pbd.PENSION_AMOUNT+pbd.dp_amount)*(:lDblOldRate / 100))  as bigint), \n");
				} else {
					lSBQuery.append("cast(sum(pbd.PENSION_AMOUNT*(:lDblNewRate / 100)  -  pbd.PENSION_AMOUNT*(:lDblOldRate / 100))  as bigint), \n");
				}
				lSBQuery.append(":lPayInMonth,:lPayInMonth,null,1," + lLngCreatedUser + "," + lLngCreatedPost + ",sysdate,null,null,null,'" + lStrRemarks + "',null,null,'N' \n");
				lSBQuery.append("From trn_bill_register tbr \n");
				lSBQuery.append("join TRN_PENSION_BILL_HDR pbh  on tbr.bill_no = pbh.bill_no and tbr.LOCATION_CODE =:lLocCode and tbr.SUBJECT_ID in (9,44) and tbr.CURR_BILL_STATUS not in (-1,-15,60,5,10)  \n");
				lSBQuery.append("join TRN_PENSION_BILL_DTLS pbd on pbh.TRN_PENSION_BILL_HDR_ID = pbd.TRN_PENSION_BILL_HDR_ID and pbd.ROP_TYPE = :lRopType and pbd.PAY_FOR_MONTH >= :lFromMonthYear and pbd.PAY_FOR_MONTH <= :lToMonthYear \n");
				lSBQuery.append("join TRN_PENSION_RQST_HDR prh  on prh.pensioner_code = pbd.pensioner_code and prh.location_code = :lLocCode and prh.DA_RATE_FOR_STATE = :lDAForState and (prh.DA_IN_PENSION_SALARY = 'Pension' or prh.DA_IN_PENSION_SALARY is null) \n");
				lSBQuery.append("where not exists \n");
				lSBQuery.append("( \n");
				lSBQuery.append("select 1 from TRN_PENSION_ARREAR_DTLS pad \n");
				lSBQuery.append("where pad.pensioner_code = prh.pensioner_code and pad.ARREAR_FIELD_TYPE = 'DA' and  (pad.REMARKS = :lStrRemarks or pad.REMARKS is null) and \n");
				lSBQuery.append("((pad.EFFECTED_FROM_YYYYMM <= :lFromMonthYear and :lFromMonthYear <= pad.EFFECTED_TO_YYYYMM) or \n");
				lSBQuery.append("(pad.EFFECTED_FROM_YYYYMM <= :lToMonthYear and :lToMonthYear <= pad.EFFECTED_TO_YYYYMM) or \n");
				lSBQuery.append("(pad.PAYMENT_FROM_YYYYMM = :lPayInMonth and pad.PAYMENT_TO_YYYYMM = :lPayInMonth )) \n");
				lSBQuery.append(") \n");
				lSBQuery.append("group by (prh.PENSION_REQUEST_ID,prh.PENSIONER_CODE) \n");
			}
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("lLocCode", lStrLocCode);
			lQuery.setString("lRopType", lStrROPType);
			lQuery.setString("lStrRemarks", lStrRemarks);
			lQuery.setInteger("lFromMonthYear", lIntEffectFromMonth);
			lQuery.setInteger("lToMonthYear", lIntEffectToMonth);
			lQuery.setInteger("lPayInMonth", lIntPayInMonth);
			lQuery.setDouble("lDblOldRate", lDblOldRate);
			lQuery.setDouble("lDblNewRate", lDblNewRate);
			lQuery.setBigDecimal("lDAForState", new BigDecimal(lStrDAForState));
			updateCount = lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return updateCount;
	}

	public int saveDAArrearDtlsByProc(Map<String, Object> inputMap) throws Exception {

		String lStrROPType = (String) inputMap.get("lStrROPType");
		String lStrLocCode = (String) inputMap.get("lStrLocCode");
		String lStrDAForState = (String) inputMap.get("lStrDAForState");
		Integer lIntPayInMonth = (Integer) inputMap.get("lIntPayInMonth");
		Integer lIntEffectFromMonth = (Integer) inputMap.get("lIntEffectFromMonth");
		Integer lIntEffectToMonth = (Integer) inputMap.get("lIntEffectToMonth");
		Double lDblOldRate = (Double) inputMap.get("lDblOldRate");
		Double lDblNewRate = (Double) inputMap.get("lDblNewRate");
		Long lLngCreatedPost = (Long) inputMap.get("lLngCreatedPost");
		Long lLngCreatedUser = (Long) inputMap.get("lLngCreatedUser");
		Date lDateCurr = (Date) inputMap.get("lDateCurr");
		String lStrRemarks = (String) inputMap.get("lStrRemarks");
		int insertCount = 0;
		CallableStatement lClbStmnt = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			Session ghibSession = getSession();
			lSBQuery.append("{call SAVE_DA_ARREAR (?,?,?,?,?,?,?,?,?,?,?,?)}");
			lClbStmnt = ghibSession.connection().prepareCall(lSBQuery.toString());
			lClbStmnt.setString(1, lStrLocCode);
			lClbStmnt.setInt(2, lIntEffectFromMonth);
			lClbStmnt.setInt(3, lIntEffectToMonth);
			lClbStmnt.setDouble(4, lDblNewRate);
			lClbStmnt.setDouble(5, lDblOldRate);
			lClbStmnt.setInt(6, lIntPayInMonth);
			lClbStmnt.setLong(7, lLngCreatedUser);
			lClbStmnt.setLong(8, lLngCreatedPost);
			lClbStmnt.setString(9, lStrRemarks);
			lClbStmnt.setString(10, lStrROPType);
			lClbStmnt.setBigDecimal(11, new BigDecimal(lStrDAForState));
			lClbStmnt.registerOutParameter(12, Types.INTEGER);
			lClbStmnt.execute();
			insertCount = lClbStmnt.getInt(12);
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return insertCount;
	}

}
