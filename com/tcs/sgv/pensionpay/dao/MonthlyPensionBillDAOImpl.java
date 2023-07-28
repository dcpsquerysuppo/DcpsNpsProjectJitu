package com.tcs.sgv.pensionpay.dao;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.service.MonthlyCases;
import com.tcs.sgv.pensionpay.service.MonthlyLogger;
import com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO;
import com.tcs.sgv.pensionpay.valueobject.MstPensionHeadcode;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerFamilyDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeChargable;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeRate;
import com.tcs.sgv.pensionpay.valueobject.TrnCvpRestorationDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillReceipt;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionChangeDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionChangeHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCutDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionOtherPartyPay;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPnsncaseRopRlt;
import com.tcs.sgv.pensionpay.valueobject.ValidPcodeView;


public class MonthlyPensionBillDAOImpl extends GenericDaoHibernateImpl<MstPensionerHdr, Long> implements MonthlyPensionBillDAO {

	private Log logger = LogFactory.getLog(getClass());

	private Session ghibSession = null;

	private static ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");

	private static ResourceBundle bundleCaseConst = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	public MonthlyPensionBillDAOImpl(Class<MstPensionerHdr> type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}

	private SessionFactory sessionFactory = null;

	private static String lStrUseView = bundleConst.getString("MNTH.USEVIEW");

	static String lStrMonthlyView = "";
	static String lStrMonthlyViewVO = "";

	public void useView() {

		if ("Y".equals(lStrUseView)) {
			lStrMonthlyView = bundleConst.getString("MNTH.VALIDPCODEVIEW");
			lStrMonthlyViewVO = bundleConst.getString("MNTH.VALIDPCODEVIEWVO");
		} else {
			lStrMonthlyView = bundleConst.getString("MNTH.TEMPTABLE");
			lStrMonthlyView = lStrMonthlyView + String.valueOf(getSession().hashCode());
		}
	}

	/**
	 * To display values in combo box Scheme
	 * 
	 * @param langId
	 *            long
	 * @return List
	 */
	public List getAllScheme(long langId) throws Exception {

		ArrayList<ComboValuesVO> arrScheme = new ArrayList<ComboValuesVO>();
		try {
			ghibSession = getSession();

			StringBuilder lSBQuery = new StringBuilder();

			String lStrName = bundleConst.getString("MNTH.SCHEMETYPE");

			lSBQuery.append(" select c1.lookupShortName from CmnLookupMst c1,CmnLookupMst c2 ");
			lSBQuery.append(" where c1.cmnLanguageMst.langId = c2.cmnLanguageMst.langId ");
			lSBQuery.append(" and c1.cmnLanguageMst.langId = :lLangId ");
			lSBQuery.append(" and c1.parentLookupId = c2.lookupId ");
			lSBQuery.append(" and c2.lookupName = :lLookupName ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			lQuery.setParameter("lLangId", langId);
			lQuery.setParameter("lLookupName", lStrName);

			List listScheme = lQuery.list();

			if (listScheme != null && !listScheme.isEmpty()) {
				Iterator it = listScheme.iterator();
				ComboValuesVO vo;
				while (it.hasNext()) {
					vo = new ComboValuesVO();
					Object tuple = it.next();

					vo.setId(tuple.toString());
					vo.setDesc(tuple.toString());
					arrScheme.add(vo);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}

		return arrScheme;
	}

	public List<ValidPcodeView> getValidPensionerCode(String bankCode, List<String> lLstBranchCode, Map<String, Object> inputMap, String lStrCaseSatus) throws Exception {

		useView();
		List<ValidPcodeView> listPnsnerCode = new ArrayList<ValidPcodeView>();
		Query lQuery = null;
		StringBuffer sblog = new StringBuffer();

		try {
			ghibSession = getSession();

			StringBuilder lSBQuery = new StringBuilder();

			if ("Y".equals(lStrUseView)) {
				lSBQuery.append("from ValidPcodeView cv where cv.caseStatus = :lApproved ");
				lSBQuery.append("AND cv.branchCode in (:lBranchCode) ");
				lSBQuery.append("AND cv.locationCode= :locationCode ");
				lSBQuery.append("AND cv.status= :lStatus ");
				lSBQuery.append(" ORDER BY cv.branchCode, cv.headCode ");

				lQuery = ghibSession.createQuery(lSBQuery.toString());

				lQuery.setParameterList("lBranchCode", lLstBranchCode);
				lQuery.setParameter("locationCode", SessionHelper.getLocationCode(inputMap).toString());
				lQuery.setParameter("lApproved", lStrCaseSatus);
				lQuery.setString("lStatus", bundleConst.getString("STATUS.CONTINUE"));

				listPnsnerCode = lQuery.list();
			}

			/*
			 * List lLstVO = lQuery.list();
			 * 
			 * if(lLstVO != null && !lLstVO.isEmpty()) { Iterator itr =
			 * lLstVO.iterator(); while (itr.hasNext()) { lObjValidView = new
			 * ValidPcodeView(); Object[] tuple = (Object[])itr.next();
			 * 
			 * lObjValidView.setPensionerCode(tuple[0] != null ?
			 * tuple[0].toString():""); lObjValidView.setBranchCode(tuple[1] !=
			 * null ? tuple[1].toString():""); lObjValidView.setPpoNo(tuple[2]
			 * != null ? tuple[2].toString():"");
			 * lObjValidView.setSeenFlag(tuple[3] != null ?
			 * tuple[3].toString():""); lObjValidView.setHeadCode(tuple[4] !=
			 * null ? (Long)tuple[4]:null); lObjValidView.setStatus(tuple[5] !=
			 * null ? tuple[5].toString():"");
			 * lObjValidView.setSchemeType(tuple[6] != null ?
			 * tuple[6].toString():""); lObjValidView.setIsRop(tuple[7] != null
			 * ? tuple[7].toString():"");
			 * lObjValidView.setBasicPensionAmount(tuple[8] != null ? new
			 * BigDecimal(tuple[8].toString()):BigDecimal.ZERO);
			 * lObjValidView.setFp1Date(tuple[9] != null ? (Date)tuple[9]:null);
			 * lObjValidView.setFp1Amount(tuple[10] != null ? new
			 * BigDecimal(tuple[10].toString()):BigDecimal.ZERO);
			 * lObjValidView.setFp2Date(tuple[11] != null ?
			 * (Date)tuple[11]:null); lObjValidView.setFp2Amount(tuple[12] !=
			 * null ? new BigDecimal(tuple[12].toString()):BigDecimal.ZERO);
			 * lObjValidView.setEndDate(tuple[13] != null ?
			 * (Date)tuple[13]:null);
			 * lObjValidView.setPensionRequestId(tuple[14] != null ?
			 * (Long)tuple[14]:null); lObjValidView.setCalcType(tuple[15] !=
			 * null ? tuple[15].toString():"");
			 * lObjValidView.setDpPercent(tuple[16] != null ? new
			 * BigDecimal(tuple[16].toString()):null);
			 * lObjValidView.setTiPercent(tuple[17] != null ? new
			 * BigDecimal(tuple[17].toString()):null);
			 * lObjValidView.setMedicalAllowenceAmount(tuple[18] != null ? new
			 * BigDecimal(tuple[18].toString()):BigDecimal.ZERO);
			 * lObjValidView.setOrgBf11156(tuple[19] != null ? new
			 * BigDecimal(tuple[19].toString()):BigDecimal.ZERO);
			 * lObjValidView.setOrgAf11156(tuple[20] != null ? new
			 * BigDecimal(tuple[20].toString()):BigDecimal.ZERO);
			 * lObjValidView.setOrgAf10560(tuple[21] != null ? new
			 * BigDecimal(tuple[21].toString()):BigDecimal.ZERO);
			 * lObjValidView.setPersonalPension(tuple[22] != null ? new
			 * BigDecimal(tuple[22].toString()):BigDecimal.ZERO);
			 * lObjValidView.setIr(tuple[23] != null ? new
			 * BigDecimal(tuple[23].toString()):BigDecimal.ZERO);
			 * lObjValidView.setCvpMonthlyAmount(tuple[24] != null ? new
			 * BigDecimal(tuple[24].toString()):BigDecimal.ZERO);
			 * lObjValidView.setPaidDate(tuple[25] != null ?
			 * (Date)tuple[25]:null); lObjValidView.setRedBf11156(tuple[26] !=
			 * null ? new BigDecimal(tuple[26].toString()):BigDecimal.ZERO);
			 * lObjValidView.setRedAf11156(tuple[27] != null ? new
			 * BigDecimal(tuple[27].toString()):BigDecimal.ZERO);
			 * lObjValidView.setAcountNo(tuple[28] != null ?
			 * tuple[28].toString():""); lObjValidView.setPnsnrPrefix(tuple[29]
			 * != null ? tuple[29].toString():"");
			 * lObjValidView.setDateOfDeath(tuple[30] != null ?
			 * (Date)tuple[30]:null);
			 * lObjValidView.setDateOfRetirement(tuple[31] != null ?
			 * (Date)tuple[31]:null); lObjValidView.setLstActPostId(tuple[32] !=
			 * null ? tuple[32].toString() : "");
			 * lObjValidView.setCvpDate(tuple[33] != null ?
			 * (Date)tuple[33]:null); lObjValidView.setAppliedDate(tuple[34] !=
			 * null ? (Date)tuple[34]:null);
			 * lObjValidView.setCommensionDate(tuple[35] != null ?
			 * (Date)tuple[35]:null); lObjValidView.setPpoDate(tuple[36] != null
			 * ? (Date)tuple[36]:null); lObjValidView.setPensionType(tuple[37]
			 * != null ? tuple[37].toString():"");
			 * lObjValidView.setOmrType(tuple[38] != null ?
			 * tuple[38].toString():""); lObjValidView.setSpecialCase(tuple[39]
			 * != null ? tuple[39].toString():"");
			 * lObjValidView.setPostId(tuple[40] != null ?
			 * tuple[40].toString():"");
			 * lObjValidView.setCvpEffectiveMonth(tuple[41] != null ?
			 * (Integer)tuple[41]:0); lObjValidView.setCaseStatus(tuple[42] !=
			 * null ? tuple[42].toString():"");
			 * lObjValidView.setDateOfBirth(tuple[43] != null ?
			 * (Date)tuple[43]:null); lObjValidView.setLastPaidDate(tuple[44] !=
			 * null ? (Date)tuple[44]:null);
			 * 
			 * listPnsnerCode.add(lObjValidView);
			 * 
			 * lObjValidView=null;
			 * 
			 * } }
			 */

		} catch (Exception e) {
			sblog.append("Valid Pcode Query threw error at locatiion" + SessionHelper.getLocationCode(inputMap));
			sblog.append(getStackTrace(e));
			throw (e);
		}
		return listPnsnerCode;
	}

	public List<String> getPnsrCodesOfGeneratedChangeStatement(Integer lIntForMonth) throws Exception {

		List<String> lLstPnsrCode = new ArrayList<String>();
		List<String> lLstChngStmntStatus = new ArrayList<String>();
		lLstChngStmntStatus.add(bundleConst.getString("CHANGSTMNTSTATUS.REJECTED"));
		lLstChngStmntStatus.add(bundleConst.getString("CHANGSTMNTSTATUS.DISCARDED"));
		Query lQuery = null;
		try {
			ghibSession = getSession();

			StringBuilder lSBQuery = new StringBuilder();

			// lSBQuery.append("Select pcd.pensionerCode \n");
			// lSBQuery.append("from TrnPensionChangeDtls pcd,TrnPensionChangeHdr pch,TrnMonthlyChangeRqst mcr \n");
			// lSBQuery.append("where \n");
			// lSBQuery.append("pcd.trnPensionChangeHdrId = pch.trnPensionChangeHdrId \n");
			// lSBQuery.append("and pch.changeRqstId = mcr.changeRqstId \n");
			// lSBQuery.append("and mcr.status not in (:lChngStatus) \n");
			// lSBQuery.append("and pcd.payForMonth= :lForMonth ");

			lSBQuery.append("Select pcd.pensionerCode \n");
			lSBQuery.append("from TrnMonthlyChangeRqst mcr,TrnPensionChangeHdr pch,TrnPensionChangeDtls pcd \n");
			lSBQuery.append("where \n");
			lSBQuery.append("mcr.changeRqstId =pch.changeRqstId \n");
			lSBQuery.append("and pcd.trnPensionChangeHdrId = pch.trnPensionChangeHdrId \n");
			lSBQuery.append("and mcr.status not in (:lChngStatus) \n");
			lSBQuery.append("and pcd.payForMonth= :lForMonth ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameterList("lChngStatus", lLstChngStmntStatus);
			lQuery.setInteger("lForMonth", lIntForMonth);
			lLstPnsrCode = lQuery.list();
		} catch (Exception e) {
			logger.error("Error while writing Monthly log..." + e, e);
			throw (e);
		}
		return lLstPnsrCode;
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAO#
	 * getPnsrCodesOfGeneratedFirstPension(java.lang.Integer, java.lang.String)
	 * Method to get all pensioner code whose first pension bill is generated in
	 * monthly bill generation month and status is neither discarded nor
	 * archived.
	 */
	public List<String> getPnsrCodesOfGeneratedFirstPension(Integer lIntForMonth, String lLocCode) throws Exception {

		List<String> lLstPnsrCode = new ArrayList<String>();
		Query lQuery = null;
		List<Short> lLstBillStatus = new ArrayList<Short>();
		lLstBillStatus.add(DBConstants.ST_BILL_DISCARD);
		lLstBillStatus.add(DBConstants.ST_BILL_ARCHEIVED);
		lLstBillStatus.add(DBConstants.ST_BILL_REJECTED);
		try {
			ghibSession = getSession();

			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append("Select pbd.pensionerCode \n");
			lSBQuery.append("from TrnPensionBillHdr pbh,TrnPensionBillDtls pbd,TrnBillRegister tbr \n");
			lSBQuery.append("where \n");
			lSBQuery.append("pbh.trnPensionBillHdrId = pbd.trnPensionBillHdrId \n");
			lSBQuery.append("and pbh.billNo = tbr.billNo \n");
			lSBQuery.append("and pbh.billType= :lBillType ");
			lSBQuery.append("and pbh.forMonth= :lForMonth ");
			lSBQuery.append("and pbh.locationCode = :lLocCode \n");
			lSBQuery.append("and tbr.currBillStatus not in (:lLstBillStatus) ");

			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lBillType", bundleConst.getString("RECOVERY.PENSION"));
			lQuery.setInteger("lForMonth", lIntForMonth);
			lQuery.setString("lLocCode", lLocCode);
			lQuery.setParameterList("lLstBillStatus", lLstBillStatus);
			lLstPnsrCode = lQuery.list();
		} catch (Exception e) {
			logger.error("Error while writing Monthly log..." + e, e);
			throw (e);
		}
		return lLstPnsrCode;
	}

	public void dropTmpTable(int lIntSessCode) {

		ghibSession = getSession();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append(" Drop temporary table " + lStrMonthlyView);
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.executeUpdate();
		} catch (Exception e) {

			logger.error(" Error is : " + e, e);
		}

	}

	public List<TrnPensionBillHdr> getTrnPensionBillHdr(List<Long> lLstBillNo) throws Exception {

		List<TrnPensionBillHdr> lObjBillHdrLst = null;
		new ArrayList<String>();

		try {
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append(" Select H FROM  TrnPensionBillHdr H,MstPensionHeadcode M WHERE H.headCode=M.headCode AND H.billNo IN ( :lbillNo ) ORDER BY M.series,H.billType ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameterList("lbillNo", lLstBillNo);

			List lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty()) {
				lObjBillHdrLst = new ArrayList<TrnPensionBillHdr>();
				for (Object ltemp : lLstVO) {
					lObjBillHdrLst.add((TrnPensionBillHdr) ltemp);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lObjBillHdrLst;
	}

	public List getConsolidatedBillHdrDtls(List<Long> lLstBillNo) throws Exception {

		List resList = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append(" FROM TrnPensionBillHdr H WHERE H.billNo IN ( :lbillNo )  ORDER BY H.billNo,H.headCode,H.billType ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameterList("lbillNo", lLstBillNo);

			resList = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return resList;
	}

	/**
	 * get {@link TrnPensionBillDtls} VO
	 * 
	 * @param String
	 *            : Bill No
	 * @return List of VO: TrnPensionBillDtls
	 */
	public ArrayList<TrnPensionBillDtls> getTrnPensionBillDtls(String lStrBillId, String lStrForMonth, String flag) throws Exception {

		List<TrnPensionBillDtls> lLstobjTrnPensionBillDtls = new ArrayList<TrnPensionBillDtls>();

		try {
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append(" FROM TrnPensionBillDtls A WHERE A.trnPensionBillHdrId IN (" + lStrBillId + ") ");

			if (flag.equals("Y")) {
				lSBQuery.append(" AND A.payForMonth = :lForMonth ");
			} else if (flag.equals("N")) {
				lSBQuery.append(" AND A.payForMonth <> :lForMonth ");
			}

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("lForMonth", Integer.parseInt(lStrForMonth));

			List lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty()) {
				for (int i = 0; i < lLstVO.size(); i++) {
					lLstobjTrnPensionBillDtls.add((TrnPensionBillDtls) lLstVO.get(i));
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return (ArrayList<TrnPensionBillDtls>) lLstobjTrnPensionBillDtls;

	}

	public Double getRecoveryDtls(String lStrPensionerCode, String lStrStatus, String lForMonth) throws Exception {

		Double lRecoveryAmt = 0D;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			// query change 365450
			lSBQuery.append(" SELECT SUM(A.amount) FROM TrnPensionRecoveryDtls A WHERE A.pensionerCode = :lPnsrCode ");
			lSBQuery.append(" AND ((A.fromMonth <= :lForMonth AND A.toMonth >= :lForMonth) OR (A.fromMonth = :lForMonth AND A.toMonth IS NULL))");
			if (lStrStatus != null && !lStrStatus.equals("-1")) {
				lSBQuery.append(" AND A.recoveryFromFlag = :lStatus ");
			}

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			if (lStrStatus != null && !lStrStatus.equals("-1")) {
				lQuery.setParameter("lStatus", lStrStatus);
			}
			lQuery.setParameter("lPnsrCode", lStrPensionerCode);

			lQuery.setParameter("lForMonth", Integer.valueOf(lForMonth));

			List lLst = lQuery.list();

			if (lLst != null && !lLst.isEmpty() && lLst.get(0) != null) {
				lRecoveryAmt = Double.parseDouble(lLst.get(0).toString());
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lRecoveryAmt;
	}

	public List getArrearDtlsByPnsrCode(String lStrPensionerCode, Integer lIntForMonth) throws Exception {

		List resultList = null;
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT H.arrearFieldType,SUM(H.totalDifferenceAmt) FROM TrnPensionArrearDtls H WHERE H.pensionerCode = :lPnsrCode \n");
			lSBQuery.append(" AND ((H.paymentFromYyyymm <= :lForMonth AND H.paymentToYyyymm >= :lForMonth)) and H.paidFlag = :lPaidFlag \n");
			lSBQuery.append(" Group By  H.arrearFieldType ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPnsrCode", lStrPensionerCode);
			lQuery.setInteger("lForMonth", lIntForMonth);
			lQuery.setCharacter("lPaidFlag", 'N');

			resultList = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return resultList;
	}

	public List<TrnCvpRestorationDtls> getMonthlyCommutationAmount(String lStrPensionerCode, Integer lIntForMonth) throws Exception {

		Date lDtMonthEnd = null;
		Date lDtMonthStart = null;
		Calendar lObjCal = null;
		Date lDtForMonth = null;
		SimpleDateFormat lSdf = null;
		List<TrnCvpRestorationDtls> lLstTrnCvpRestorationDtlsObj = null;

		try {
			lSdf = new SimpleDateFormat("yyyyMM");
			lDtForMonth = lSdf.parse(lIntForMonth.toString());
			lObjCal = new GregorianCalendar();
			lObjCal.setTime(lDtForMonth);
			int lastDateOfMonth = lObjCal.getActualMaximum(Calendar.DAY_OF_MONTH);
			lObjCal.set(Calendar.DATE, lastDateOfMonth);
			lDtMonthEnd = lObjCal.getTime();
			lObjCal.set(Calendar.DATE, 1);
			lDtMonthStart = lObjCal.getTime();
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("SELECT crd FROM TrnCvpRestorationDtls crd WHERE crd.pensionerCode = :lPnsrCode \n");
			lSBQuery.append("and (((crd.fromDate between :lMonthStrtDt and :lMonthEndDt) or  (:lMonthStrtDt > crd.fromDate)) \n");
			lSBQuery.append("		and ((crd.toDate between :lMonthStrtDt and :lMonthEndDt) or (crd.toDate > :lMonthEndDt ))) \n");
			//lSBQuery.append("and  crd.restnAplnReceivedFlag = :lRestnAplFlag \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPnsrCode", lStrPensionerCode);
			lQuery.setDate("lMonthStrtDt", lDtMonthStart);
			lQuery.setDate("lMonthEndDt", lDtMonthEnd);
			//lQuery.setString("lRestnAplFlag", "N");

			lLstTrnCvpRestorationDtlsObj = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstTrnCvpRestorationDtlsObj;
	}

	public List<TrnPensionCutDtls> getMonthlyPensionCutAmount(String lStrPensionerCode, Integer lIntForMonth) throws Exception {

		Date lDtMonthEnd = null;
		Date lDtMonthStart = null;
		Calendar lObjCal = null;
		Date lDtForMonth = null;
		SimpleDateFormat lSdf = null;
		List<TrnPensionCutDtls> lLstTrnPensionCutDtlsObj = null;
		try {
			lSdf = new SimpleDateFormat("yyyyMM");
			lDtForMonth = lSdf.parse(lIntForMonth.toString());
			lObjCal = new GregorianCalendar();
			lObjCal.setTime(lDtForMonth);
			int lastDateOfMonth = lObjCal.getActualMaximum(Calendar.DAY_OF_MONTH);
			lObjCal.set(Calendar.DATE, lastDateOfMonth);
			lDtMonthEnd = lObjCal.getTime();
			lObjCal.set(Calendar.DATE, 1);
			lDtMonthStart = lObjCal.getTime();
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("SELECT pcd FROM TrnPensionCutDtls pcd WHERE pcd.pensionerCode = :lPnsrCode \n");
			lSBQuery.append("and (((pcd.fromDate between :lMonthStrtDt and :lMonthEndDt) or  (:lMonthStrtDt > pcd.fromDate)) \n");
			lSBQuery.append("		and ((pcd.toDate between :lMonthStrtDt and :lMonthEndDt) or (pcd.toDate > :lMonthEndDt ))) \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPnsrCode", lStrPensionerCode);
			lQuery.setDate("lMonthStrtDt", lDtMonthStart);
			lQuery.setDate("lMonthEndDt", lDtMonthEnd);

			lLstTrnPensionCutDtlsObj = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstTrnPensionCutDtlsObj;
	}

	public ArrayList getItCutDtls(String lStrPensionerCode, String lStrForMonth) throws Exception {

		ArrayList arrItCutDtls = new ArrayList();

		try {
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append(" SELECT H.typeFlag,sum(H.amount) FROM TrnPensionItCutDtls H WHERE H.pensionerCode = :lPnsrCode ");
			lSBQuery.append(" AND ");
			lSBQuery.append("((H.fromMonth <= :lForMonth AND H.toMonth >= :lForMonth and (H.typeFlag <> 'PP' or H.typeFlag <> 'PermanentBenefit'))  or ");
			lSBQuery.append(" (H.typeFlag = 'PP' or H.typeFlag = 'PermanentBenefit' or H.typeFlag = 'PM')");
			lSBQuery.append(" ) ");
			lSBQuery.append(" GROUP BY H.typeFlag ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("lPnsrCode", lStrPensionerCode);
			lQuery.setParameter("lForMonth", Integer.valueOf(lStrForMonth));

			List lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty()) {
				Iterator itr = lLstVO.iterator();
				while (itr.hasNext()) {
					Object[] tuple = (Object[]) itr.next();

					arrItCutDtls.add(tuple[0]);
					arrItCutDtls.add(tuple[1]);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return arrItCutDtls;
	}

	public List getotherPartyName(List<BigDecimal> lstOtherParty, String lLocCode) throws Exception {

		List arrPartyDtls = new ArrayList();

		try {
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append(" SELECT party_name,other_party_id FROM MST_PENSION_OTHER_PARTY WHERE other_party_id IN (:lstOtherParty) " + " and location_code = :lLocCode ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setParameterList("lstOtherParty", lstOtherParty);
			lQuery.setString("lLocCode", lLocCode);

			List lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty()) {
				Iterator itr = lLstVO.iterator();
				while (itr.hasNext()) {
					Object[] tuple = (Object[]) itr.next();

					arrPartyDtls.add(tuple[1] + "~" + tuple[0]);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return arrPartyDtls;
	}

	public ArrayList getArrearDtls(String lStrPensionerCode, String lStrForMonth) throws Exception {

		ArrayList larrArrearDtls = new ArrayList();

		try {
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append(" SELECT H.arrearFieldType,SUM(H.differenceAmount) FROM TrnPensionArrearDtls H WHERE H.pensionerCode = :lPnsrCode ");
			lSBQuery.append(" AND H.paymentFromYyyymm <= :lForMonth AND H.paymentToYyyymm >= :lForMonth GROUP BY H.arrearFieldType ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("lPnsrCode", lStrPensionerCode);
			lQuery.setParameter("lForMonth", Integer.valueOf(lStrForMonth));

			List lLstVO = lQuery.list();
			if (lLstVO != null && !lLstVO.isEmpty()) {
				Iterator itr = lLstVO.iterator();
				while (itr.hasNext()) {
					Object[] tuple = (Object[]) itr.next();

					larrArrearDtls.add(tuple[0]);
					larrArrearDtls.add(tuple[1]);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return larrArrearDtls;
	}

	/**
	 * Method to get family pensioner details
	 * 
	 * @param String
	 *            PensionerCode
	 * @return List
	 */
	public MstPensionerFamilyDtls getMstPensionerFamilyDtls(String lStrPnsnrCode) throws Exception {

		MstPensionerFamilyDtls lObjMstPensionerFamilyDtls = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append(" FROM MstPensionerFamilyDtls WHERE pensionerCode = :lPnsrCode and percentage = :lPer ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("lPnsrCode", lStrPnsnrCode);
			lQuery.setLong("lPer", 100);

			List lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty() && lLstVO.get(0) != null) {
				lObjMstPensionerFamilyDtls = (MstPensionerFamilyDtls) lLstVO.get(0);
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lObjMstPensionerFamilyDtls;
	}

	/**
	 * Method to get family pensioner details in mAp
	 * 
	 * @author Astha Notani
	 * @param Map
	 * @return void
	 */
	public void getMstPensionerFamilyDtlsMap(Map inputMap) throws Exception {

		String tempPCode = null;
		String lStrCaseStatus = (String) inputMap.get("lStrCaseStatus");
		List<String> lLstBranchCode = (List<String>) inputMap.get("BranchCode");

		try {
			StringBuilder lSBQuery = new StringBuilder();
			Map lMapFamilyDtls = new HashMap<String, Object>();
			ghibSession = getSession();

			lSBQuery.append("select fd from MstPensionerFamilyDtls fd, " + lStrMonthlyViewVO + " vp where vp.pensionerCode =fd.pensionerCode and  vp.caseStatus = :caseStatus and "
					+ "fd.percentage=:lPer and vp.locationCode = :locCode AND vp.dateOfDeath is not null AND vp.branchCode in (:lBranchCode) ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("lPer", 100);
			lQuery.setString("caseStatus", lStrCaseStatus);
			lQuery.setString("locCode", SessionHelper.getLocationCode(inputMap).toString());
			lQuery.setParameterList("lBranchCode", lLstBranchCode);

			List lLstVO = lQuery.list();
			if (!lLstVO.isEmpty()) {
				Iterator itr = lLstVO.iterator();
				while (itr.hasNext()) {
					MstPensionerFamilyDtls lObjMstPensionerFamilyDtlsVO = (MstPensionerFamilyDtls) itr.next();
					tempPCode = lObjMstPensionerFamilyDtlsVO.getPensionerCode();
					lMapFamilyDtls.put(tempPCode, lObjMstPensionerFamilyDtlsVO);
				}
				inputMap.put("lMapFamilyDtls", lMapFamilyDtls);
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	/**
	 * Method to check valid bill
	 * 
	 * @param String
	 *            forMonth, String bankCode, String branchCode, String headCode
	 * @return boolean
	 */
	/*
	 * public String isValidBill(String forMonth, String bankCode, String
	 * branchCode, String scheme,String lStrPPoNo,Long lLngPostId) throws
	 * Exception { StringBuffer lSBQuery = new StringBuffer(); Session session =
	 * getSession(); Integer lOne = 1;
	 * 
	 * select max(bhd.FOR_MONTH) from TRN_PENSION_BILL_HDR bhd, rlt_auditor_bank
	 * ra where bhd.BILL_TYPE = 'Monthly' and bhd.SCHEME = 'IRLA' and
	 * bhd.BRANCH_CODE = 3002 and ra.branch_code = bhd.branch_code and
	 * ra.bill_type is null and bhd.created_post_id = 104510 and bhd.rejected
	 * !=1
	 * 
	 * try { if(scheme.equals("RUBARU") || scheme.equals("MONEY ORDER")) {
	 * 
	 * lSBQuery.append(
	 * " SELECT max(tpr.forMonth) maxForMonth FROM TrnPensionBillHdr tpr,TrnPensionBillDtls dt WHERE"
	 * ); lSBQuery.append(" tpr.billType = :lbillType ");
	 * lSBQuery.append(" AND tpr.scheme = :scheme ." +
	 * " and bhd.rejected != :one ");
	 * 
	 * if(!"".equals(lStrPPoNo)) { lSBQuery.append(" AND dt.ppoNo = :ppoNo "); }
	 * lSBQuery
	 * .append(" AND tpr.trnPensionBillHdrId = dt.trnPensionBillHdrId ");
	 * 
	 * } else {lSBQuery.append(
	 * "select max(bhd.FOR_MONTH) maxForMonth  from TRN_PENSION_BILL_HDR bhd,rlt_auditor_bank ra where "
	 * +
	 * " bhd.BILL_TYPE = :lbillType and bhd.SCHEME = :scheme and bhd.BRANCH_CODE = :branchCode and "
	 * + " ra.branch_code = bhd.branch_code and ra.bill_type is null and " +
	 * " bhd.created_post_id = :postId and bhd.rejected != :one "); }
	 * 
	 * SQLQuery lQuery = session.createSQLQuery(lSBQuery.toString());
	 * 
	 * if(!scheme.equals("RUBARU") && !scheme.equals("MONEY ORDER")) {
	 * lQuery.setString("branchCode", branchCode); lQuery.setLong("postId",
	 * lLngPostId);
	 * 
	 * }
	 * 
	 * lQuery.setString("lbillType", "Monthly"); lQuery.setString("scheme",
	 * scheme); lQuery.setInteger("one", lOne);
	 * 
	 * if(!"".equals(lStrPPoNo)) { lQuery.setParameter("ppoNo", lStrPPoNo); }
	 * 
	 * lQuery.addScalar("maxForMonth",Hibernate.BIG_DECIMAL);
	 * 
	 * List lList = lQuery.list(); if (lList != null && !lList.isEmpty()) {
	 * BigDecimal maxMonth = (BigDecimal)lList.get(0); BigDecimal currMonth =
	 * new BigDecimal(forMonth); if(maxMonth != null) {
	 * if(currMonth.compareTo(maxMonth) == -1 || currMonth.compareTo(maxMonth)
	 * == 0) { return "bilCreated"; } } } } catch (Exception e) {
	 * logger.error(" Error is : " + e, e); throw e; } return "bilNtCreated"; }
	 */

	public String getBankName(String lStrBank) throws Exception {

		String lStrBankName = null;
		StringBuffer lSBQuery = new StringBuffer();
		try {
			ghibSession = getSession();
			lSBQuery.append(" SELECT B.bankName FROM MstBank B WHERE B.bankCode = :bankCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("bankCode", lStrBank);

			List lLst = lQuery.list();

			if (lLst != null && !lLst.isEmpty() && lLst.get(0) != null) {
				lStrBankName = lLst.get(0).toString();
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lStrBankName;
	}

	public String getBranchName(String lStrBank, String lStrBranch, String lStrLocCode) throws Exception {

		String lStrBranchName = null;
		StringBuffer lSBQuery = new StringBuffer();
		try {
			ghibSession = getSession();
			lSBQuery.append(" SELECT B.branchName FROM RltBankBranch B WHERE B.branchCode = :branchCode ");
			lSBQuery.append(" AND B.bankCode = :bankCode AND B.locationCode :lLocCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("bankCode", Long.valueOf(lStrBank));
			lQuery.setParameter("branchCode", Long.valueOf(lStrBranch));
			lQuery.setParameter("lLocCode", lStrLocCode);

			List lLst = lQuery.list();

			if (lLst != null && !lLst.isEmpty() && lLst.get(0) != null) {
				lStrBranchName = lLst.get(0).toString();
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lStrBranchName;
	}

	public List getMonthDtls(String lStrMonth, Long langId) throws Exception {

		List lArrlist = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append(" select m.monthCode, m.monthName" + " from SgvaMonthMst m, CmnLanguageMst l" + " where m.langId = l.langShortName" + " and m.monthNo = :lMonth"
					+ " and l.langId = :lLangId ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("lMonth", Byte.parseByte(lStrMonth));
			lQuery.setParameter("lLangId", langId);

			List listDtls = lQuery.list();

			if (listDtls != null && !listDtls.isEmpty()) {
				Iterator it = listDtls.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					lArrlist.add(tuple[0]);
					lArrlist.add(tuple[1]);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lArrlist;
	}

	public List getAllBranchPrint(String lLocation) throws Exception {

		ArrayList<ComboValuesVO> arrBranch = new ArrayList<ComboValuesVO>();
		try {
			Session hibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();
			// select r.branch_code,r.branch_name from rlt_bank_branch r where
			// r.location_code = 100109
			lSBQuery.append(" SELECT H.branchCode,H.branchName FROM RltBankBranch H " + " WHERE H.locationCode = :Location ORDER BY H.branchCode ");

			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("Location", lLocation);

			List listBranch = lQuery.list();
			if (listBranch != null && !listBranch.isEmpty()) {
				Iterator it = listBranch.iterator();
				ComboValuesVO vo;
				while (it.hasNext()) {
					vo = new ComboValuesVO();
					Object[] tuple = (Object[]) it.next();
					vo.setId(tuple[0].toString());
					vo.setDesc(tuple[1].toString());
					arrBranch.add(vo);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return arrBranch;
	}

	public List getBankCode(String lStrBranch, String lStrLocCode) throws Exception {

		/*
		 * select br.bank_code ,mb.bank_name from rlt_bank_branch br, mst_bank
		 * mb,br.branch_name where br.branch_code = 12001 and br.location_code =
		 * 10055 and br.lang_id = 1 and br.bank_code = mb.bank_code and
		 * br.lang_id = mb.lang_id and br.location_code = br.location_code
		 */
		List listBranch = new ArrayList();
		try {
			Session hibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();

			/*
			 * lSBQuery.append(" SELECT H.bankCode, FROM RltBankBranch H" +
			 * " WHERE H.branchCode = :BranchCode" +
			 * " AND H.locationCode = :locCode ");
			 */

			lSBQuery.append("select br.bankCode ,mb.bankName,br.branchName from  RltBankBranch br, MstBank mb where br.branchCode = :BranchCode and "
					+ "br.locationCode = :locCode and br.langId = :langId and br.bankCode = mb.bankCode and br.langId = mb.langId ");

			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("BranchCode", Long.valueOf(lStrBranch));
			lQuery.setParameter("locCode", lStrLocCode);
			lQuery.setParameter("langId", new Long(1));

			listBranch = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return listBranch;
	}

	public List<Long> getBillNo(String lStrDate, String lStrBranch, String lStrScheme, String lStrPPoPara, String lStrLocCode, String lStrAuditor) throws Exception {

		List<Long> listBill = new ArrayList<Long>();

		try {
			Session hibSession = getSession();

			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select distinct hd.billNo from TrnPensionBillHdr hd, ");
			lSBQuery.append(" TrnPensionBillDtls dt ");
			lSBQuery.append(" where hd.forMonth = :ForMonth ");

			if (!lStrScheme.equals("RUBARU") && !lStrScheme.equalsIgnoreCase("MONEY ORDER")) {
				lSBQuery.append(" and hd.branchCode = :BranchCode ");
			}
			if (lStrAuditor != null && !"".equals(lStrAuditor)) {
				lSBQuery.append(" and hd.createdPostId = :Auditor ");
			}
			lSBQuery.append(" and hd.scheme = :Scheme ");
			lSBQuery.append(" and hd.billType in('Monthly','CVP','DCRG') " + " and hd.locationCode = :locCode and hd.rejected=0");
			lSBQuery.append(" and dt.trnPensionBillHdrId = hd.trnPensionBillHdrId ");
			lSBQuery.append(" and hd.billNo > 0 ");

			if (!("").equals(lStrPPoPara) && (lStrScheme.equals("RUBARU") || lStrScheme.equalsIgnoreCase("MONEY ORDER"))) {
				lSBQuery.append(" and dt.ppoNo = :ppoNo ");
			}

			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ForMonth", Integer.valueOf(lStrDate));

			if (!lStrScheme.equals("RUBARU") && !lStrScheme.equalsIgnoreCase("MONEY ORDER")) {
				lQuery.setParameter("BranchCode", lStrBranch);
			}
			if (lStrAuditor != null && !"".equals(lStrAuditor)) {
				lQuery.setString("Auditor", lStrAuditor);
			}

			lQuery.setParameter("Scheme", lStrScheme);
			lQuery.setParameter("locCode", lStrLocCode);

			if (!("").equals(lStrPPoPara) && (!lStrScheme.equals("RUBARU") || !lStrScheme.equalsIgnoreCase("MONEY ORDER"))) {
				lQuery.setParameter("ppoNo", lStrPPoPara);
			}

			listBill = lQuery.list();
			/*
			 * if (listBill!=null && !listBill.isEmpty()) { for(String lStr :
			 * (List<String>) listBill) { if(lFalg == false) { billNo = lStr;
			 * lFalg = true; } else { billNo += "," + lStr; } } }
			 */
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return listBill;
	}

	public Map<String, Object> getRltPensionHeadcodeChargable(Set<Long> lHeadCodeSet) throws Exception {

		Map<String, Object> lMapHdChagble = new HashMap<String, Object>();

		try {
			Session hibSession = getSession();

			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" from RltPensionHeadcodeChargable where headCode in (:Headcode) ");

			Query lQuery = hibSession.createQuery(lSBQuery.toString());

			lQuery.setParameterList("Headcode", lHeadCodeSet);

			List<RltPensionHeadcodeChargable> listBill = lQuery.list();

			if (listBill != null && !listBill.isEmpty()) {
				// lObjHeadChargeable = (RltPensionHeadcodeChargable)
				// listBill.get(0);

				for (RltPensionHeadcodeChargable lObjHeadChargeable : listBill) {
					String lStrBillType = ("PENSION".equalsIgnoreCase(lObjHeadChargeable.getBillType())) ? "Monthly" : lObjHeadChargeable.getBillType();
					lMapHdChagble.put(lObjHeadChargeable.getheadCode() + "~" + lStrBillType, lObjHeadChargeable);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lMapHdChagble;
	}

	public List<RltPensionHeadcodeRate> getIRRateFromHeadcodeRateRlt(Long lHeadcode, List<String> lLstFieldType) throws Exception {

		List<RltPensionHeadcodeRate> lLstRltPensionHeadcodeRateVO = new ArrayList<RltPensionHeadcodeRate>();
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM RltPensionHeadcodeRate  WHERE  headCode = :lHeadcode AND fieldType IN (:lFieldType) ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameterList("lFieldType", lLstFieldType);
			lQuery.setBigDecimal("lHeadcode", new BigDecimal(lHeadcode));

			List lLstVO = lQuery.list();

			if (lLstVO != null && lLstVO.size() > 0) {
				for (Object lObjVO : lLstVO) {
					RltPensionHeadcodeRate lObjPensionHeadcodeRate = (RltPensionHeadcodeRate) lObjVO;
					lLstRltPensionHeadcodeRateVO.add(lObjPensionHeadcodeRate);
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lLstRltPensionHeadcodeRateVO;
	}

	public RltPensionHeadcodeChargable getRltPensionHeadcodeChargable(String headcode, String lStrBillType) throws Exception {

		RltPensionHeadcodeChargable lObjHeadChargeable = new RltPensionHeadcodeChargable();

		try {
			Session hibSession = getSession();

			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" from RltPensionHeadcodeChargable hd ");
			lSBQuery.append(" where hd.headCode = :Headcode ");
			lSBQuery.append(" and billType = :lBillType ");

			Query lQuery = hibSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("Headcode", Long.valueOf(headcode));

			lStrBillType = ("Monthly".equalsIgnoreCase(lStrBillType)) ? "PENSION" : lStrBillType;

			lQuery.setString("lBillType", lStrBillType);

			List listBill = lQuery.list();

			if (listBill != null && !listBill.isEmpty()) {
				lObjHeadChargeable = (RltPensionHeadcodeChargable) listBill.get(0);
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lObjHeadChargeable;
	}

	public Map getHeaderDtl(String lStrBranchCode, String lStrLocId, long lLangId, List lLstNonBilledBrnchs) throws Exception {

		List resList = null;
		Map lLocMap = null;

		try {
			Session ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();
			if (lStrBranchCode != null) {
				lSBQuery.append(" select l.locName,b.bankName,rb.branchName,rb.branchCode from CmnLocationMst l,MstBank b,RltBankBranch rb ");
				lSBQuery.append(" where b.bankCode = rb.bankCode and rb.locationCode = l.locationCode and l.cmnLanguageMst.langId = b.langId ");
				lSBQuery.append(" and l.cmnLanguageMst.langId = 1 and l.locationCode = :lStrLocId ");

				if (!"-1".equals(lStrBranchCode) && !lStrBranchCode.equals("")) {
					lSBQuery.append(" and  rb.branchCode = :lBranchCode ");
				}
			} else {
				lSBQuery.append(" select l.locName from CmnLocationMst l");
				lSBQuery.append(" where l.cmnLanguageMst.langId = '1' and l.locationCode = :lStrLocId ");
			}

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			if (!"-1".equals(lStrBranchCode) && !lStrBranchCode.equals("")) {
				lQuery.setParameter("lBranchCode", Long.valueOf(lStrBranchCode));
			}

			lQuery.setParameter("lStrLocId", lStrLocId);
			// lQuery.setParameter("lLangId", lLangId);

			List lLstVO = lQuery.list();

			/*
			 * if("".equals(lStrBranchCode)) { resList = lLstVO; }
			 */

			if (lLstVO != null && !lLstVO.isEmpty()) {
				lLocMap = new HashMap<String, Object>();

				if (lStrBranchCode == null) {
					lLocMap.put("LocName~", resList);
				} else {
					for (Object lObj : lLstVO) {
						Object[] lArrObj = (Object[]) lObj;
						lLocMap.put(lArrObj[3] + "~", lObj);

					}
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLocMap;
	}

	public List getHeadCodeListFromBranchCode(String lStrBranchCode, String lStrLocCode) throws Exception {

		ArrayList<ComboValuesVO> arrHeadcodeVO = new ArrayList<ComboValuesVO>();
		List lLstReturn = null;
		StringBuffer lSBQuery = new StringBuffer();

		try {
			Session ghibSession = getSession();

			lSBQuery.append(" SELECT distinct(R.headCode) ");
			lSBQuery.append(" FROM TrnPensionRqstHdr R, MstPensionerDtls PD ");
			lSBQuery.append(" WHERE R.pensionerCode = PD.pensionerCode ");
			lSBQuery.append(" AND R.caseStatus = PD.caseStatus and PD.active_flag = 'Y' ");
			lSBQuery.append(" AND R.caseStatus = 'APPROVED' ");
			lSBQuery.append(" AND R.locationCode = :locCode ");
			lSBQuery.append(" AND PD.branchCode = :branchCode ");
			lSBQuery.append(" ORDER BY R.headCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("branchCode", lStrBranchCode);
			lQuery.setParameter("locCode", lStrLocCode);

			lLstReturn = lQuery.list();

			if (lLstReturn != null && !lLstReturn.isEmpty()) {
				Iterator itr = lLstReturn.iterator();
				ComboValuesVO cmbVO;
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					Object obj = itr.next();
					cmbVO.setId(obj.toString());
					cmbVO.setDesc(obj.toString());
					arrHeadcodeVO.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return arrHeadcodeVO;
	}

	public List getBranchByBranchId(String branchCode, String locCode, Long postId) throws Exception {

		StringBuilder strQuery = new StringBuilder();
		List resultList = new ArrayList();
		List<String> lLstDtlsList = new ArrayList<String>();
		Iterator it;

		if (branchCode.contains("~")) {
			branchCode = branchCode.split("~")[0];

		}
		try {
			Session ghibSession = getSession();
			strQuery.append(" select mb.bankCode,mb.bankName,rb.branchCode,rb.branchName ");
			strQuery.append(" from RltBankBranch rb,MstBank mb");

			strQuery.append(",RltAuditorBank ra ");

			strQuery.append(" where rb.bankCode = mb.bankCode and ");

			{
				strQuery.append(" ra.postId = :postId and ");
				strQuery.append(" ra.bankCode = rb.bankCode and ra.branchCode = rb.branchCode and rb.locationCode = ra.locationCode and ");
			}
			strQuery.append(" rb.branchCode = :branchCode and rb.locationCode = :locationCode ");

			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setLong("branchCode", Long.parseLong(branchCode));
			hqlQuery.setString("locationCode", locCode);

			{
				hqlQuery.setLong("postId", postId);
			}

			resultList = hqlQuery.list();
			if (resultList != null && !resultList.isEmpty()) {
				it = resultList.iterator();
				while (it.hasNext()) {
					Object tuple[] = (Object[]) it.next();
					lLstDtlsList.add(tuple[0].toString());
					lLstDtlsList.add(tuple[1].toString());
					lLstDtlsList.add(tuple[2].toString());
					lLstDtlsList.add(tuple[3].toString());
				}
			}
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw (e);
		}
		return lLstDtlsList;
	}

	public boolean isRejectedBill(String forMonth, String bankCode, String branchCode, String scheme) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Session session = getSession();
		Integer lOne = 1;

		/*
		 * select distinct hd.rejected from trn_pension_bill_hdr hd where
		 * hd.for_month=200901 and hd.scheme='IRLA' and hd.branch_code=12001
		 */

		try {
			lSBQuery.append(" select distinct tpbh.rejected ");
			lSBQuery.append(" from TrnPensionBillHdr tpbh  ");

			lSBQuery.append(" where tpbh.scheme = :scheme ");

			if (!scheme.equals("RUBARU") && !scheme.equals("MONEY ORDER")) {
				lSBQuery.append(" and tpbh.branchCode = :branchCode ");
			}

			lSBQuery.append(" and tpbh.forMonth = :forMonth and tpbh.rejected = :one ");

			Query lQuery = session.createQuery(lSBQuery.toString());

			if (!scheme.equals("RUBARU") && !scheme.equals("MONEY ORDER")) {
				lQuery.setParameter("branchCode", branchCode);
			}

			lQuery.setParameter("scheme", scheme);
			lQuery.setParameter("forMonth", Integer.parseInt(forMonth));
			lQuery.setParameter("one", lOne);

			List lList = lQuery.list();
			if (lList != null && !lList.isEmpty()) {
				return false;
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return true;
	}

	public List<TrnPensionBillReceipt> getHeadCodeWiseBillRcptEdpDtl(String lForMonth, List lPnsnrCodeLst, String lStrPPOList, String locationCode, List<String> lLstOmrPenCode) throws Exception {

		List<TrnPensionBillReceipt> dataList = null;
		try {
			Session ghibSession = getSession();
			StringBuffer hqlQuery = new StringBuffer();

			hqlQuery.append(" SELECT TR.HEAD_CODE HEADCODE,PR.EDP_CODE EDPCODE,PR.DEDUCTION_TYPE DEDUCTIONTYPE, PR.MJRHD_CODE MJRHDCODE, "
					+ " PR.SUBMJRHD_CODE SUBMJRHDCODE,PR.MINHD_CODE MINHDCODE,PR.SUBHD_CODE SUBHDCODE, SUM(PR.AMOUNT) AMOUNT, " + " PR.RECOVERY_FROM_FLAG RECOVERYFROMFLAG "
					+ " FROM TRN_PENSION_RECOVERY_DTLS PR ,TRN_PENSION_RQST_HDR TR " + " WHERE PR.PENSIONER_CODE = TR.PENSIONER_CODE AND TR.location_code = :LocCode AND TR.CASE_STATUS = :status "
					+ " AND PR.BILL_NO IS NULL AND " + " ( ( (PR.RECOVERY_FROM_FLAG = :lStrTypeFlag AND PR.FROM_MONTH <= :lForMonth AND PR.TO_MONTH >= :lForMonth ) ");

			if (lPnsnrCodeLst != null && !lPnsnrCodeLst.isEmpty()) {
				hqlQuery.append(" and ( ");
				int flg = 0;
				for (int i = 0; i < lPnsnrCodeLst.size(); i++) {
					if (flg == 1) {
						hqlQuery.append(" or ");
					}
					hqlQuery.append(" PR.PENSIONER_CODE IN (" + lPnsnrCodeLst.get(i) + ")");
					flg = 1;
				}
				hqlQuery.append(") ");
			} else {
				// hqlQuery.append(" and 1 = 2 ");
				return dataList;
			}

			hqlQuery.append(" )");

			if (lStrPPOList != null && lStrPPOList.length() > 0) {
				hqlQuery.append(" or " + " ( PR.RECOVERY_FROM_FLAG in (:CVP,:DCRG)" + " AND TR.PPO_NO IN ( " + lStrPPOList + " )" + " )");
			}

			if (lLstOmrPenCode != null && !lLstOmrPenCode.isEmpty()) {
				hqlQuery.append(" or (PR.RECOVERY_FROM_FLAG = :lOMRFlag and PR.PENSIONER_CODE IN (:lstOmr) )");

			}

			hqlQuery.append(" ) ");

			hqlQuery.append(" GROUP BY PR.EDP_CODE, PR.DEDUCTION_TYPE, PR.MJRHD_CODE, PR.SUBMJRHD_CODE, PR.MINHD_CODE, PR.SUBHD_CODE, TR.HEAD_CODE, PR.RECOVERY_FROM_FLAG "
					+ " ORDER BY TR.HEAD_CODE,PR.RECOVERY_FROM_FLAG ");

			SQLQuery lQuery = ghibSession.createSQLQuery(hqlQuery.toString());

			lQuery.setParameter("lForMonth", Integer.parseInt(lForMonth));
			lQuery.setParameter("lStrTypeFlag", "Monthly");
			lQuery.setParameter("status", "APPROVED");
			lQuery.setString("LocCode", locationCode);

			if (lStrPPOList != null && lStrPPOList.length() > 0) {
				lQuery.setString("CVP", "CVP");
				lQuery.setString("DCRG", "DCRG");
			}
			if (lLstOmrPenCode != null && !lLstOmrPenCode.isEmpty()) {
				lQuery.setParameter("lOMRFlag", "OMR");
				lQuery.setParameterList("lstOmr", lLstOmrPenCode);
			}

			lQuery.addScalar("HEADCODE", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("EDPCODE", Hibernate.STRING);
			lQuery.addScalar("DEDUCTIONTYPE", Hibernate.STRING);
			lQuery.addScalar("MJRHDCODE", Hibernate.STRING);
			lQuery.addScalar("SUBMJRHDCODE", Hibernate.STRING);
			lQuery.addScalar("MINHDCODE", Hibernate.STRING);
			lQuery.addScalar("SUBHDCODE", Hibernate.STRING);
			lQuery.addScalar("AMOUNT", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("RECOVERYFROMFLAG", Hibernate.STRING);

			List resList = lQuery.list();

			if (resList != null) {
				Iterator it = resList.iterator();
				dataList = new ArrayList<TrnPensionBillReceipt>();

				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					TrnPensionBillReceipt billEdpVO = new TrnPensionBillReceipt();

					if (tuple[8] != null && ("Monthly".equals(tuple[8].toString()) || "OMR".equals(tuple[8].toString()))) {
						billEdpVO.setTrnPensionBillHdrId(Long.valueOf("90" + tuple[0].toString()));
					} else if (tuple[8] != null && "CVP".equals(tuple[8].toString())) {
						billEdpVO.setTrnPensionBillHdrId(Long.valueOf("110" + tuple[0].toString()));
					} else if (tuple[8] != null && "DCRG".equals(tuple[8].toString())) {
						billEdpVO.setTrnPensionBillHdrId(Long.valueOf("100" + tuple[0].toString()));
					}

					billEdpVO.setEdpCode((String) tuple[1]);
					billEdpVO.setDedType((String) tuple[2]);
					billEdpVO.setMajorHead((String) tuple[3]);
					billEdpVO.setSubmajorHead((String) tuple[4]);
					billEdpVO.setMinorHead((String) tuple[5]);
					billEdpVO.setSubhead((String) tuple[6]);

					billEdpVO.setAmount(new BigDecimal(tuple[7].toString()));

					dataList.add(billEdpVO);
				}
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return dataList;
	}

	public List getHeadCodeWiseBillDedTypeDtl(String lForMonth, List lPnsnrCodeLst, String lStrPpoNoLst, String lLocCode) throws Exception {

		/*
		 * SELECT T.HD_CODE HeadCode, T.REC_TYPE RecType, SUM(T.DEDUCTION_A)
		 * DedA, SUM(T.DEDUCTION_B) DedB FROM (SELECT TR.HEAD_CODE AS HD_CODE,
		 * PR.RECOVERY_FROM_FLAG AS rec_type, CASE WHEN PR.DEDUCTION_TYPE = 'A'
		 * THEN SUM(PR.AMOUNT) ELSE 0 END AS DEDUCTION_A, CASE WHEN
		 * PR.DEDUCTION_TYPE = 'B' THEN SUM(PR.AMOUNT) ELSE 0 END AS DEDUCTION_B
		 * FROM TRN_PENSION_RECOVERY_DTLS PR, TRN_PENSION_RQST_HDR TR WHERE
		 * PR.PENSIONER_CODE = TR.PENSIONER_CODE AND TR.location_code = '10055'
		 * AND TR.CASE_STATUS = 'APPROVED' AND ( ( ( ( PR.RECOVERY_FROM_FLAG =
		 * 'Monthly' AND PR.FROM_MONTH <= 200910 AND PR.TO_MONTH >= 200910) OR
		 * PR.RECOVERY_FROM_FLAG = 'OMR' ) AND PR.PENSIONER_CODE IN ('542326',
		 * '555022', '562087', '991005513740', '991005513769', '554615',
		 * '525954', '542411', '525745', '564842', '565476', '542093', '536611',
		 * '555686', '564790', '567795', '99100553716') ) OR
		 * (PR.RECOVERY_FROM_FLAG IN ('CVP','DCRG') AND TR.PPO_NO IN
		 * ('DPP/P/024581', 'DPP/P/024581') )
		 * 
		 * ) GROUP BY TR.HEAD_CODE, PR.RECOVERY_FROM_FLAG, PR.DEDUCTION_TYPE) T
		 * GROUP BY T.HD_CODE, T.REC_TYPE
		 */

		List dataList = null;
		try {
			Session ghibSession = getSession();
			StringBuffer hqlQuery = new StringBuffer();

			hqlQuery.append(" SELECT T.HD_CODE HeadCode, T.REC_TYPE RecType, SUM(T.DEDUCTION_A) DedA,SUM(T.DEDUCTION_B) DedB FROM ( "
					+ " SELECT TR.HEAD_CODE AS HD_CODE, PR.RECOVERY_FROM_FLAG AS rec_type, " + " CASE WHEN PR.DEDUCTION_TYPE = 'A' THEN SUM(PR.AMOUNT) ELSE 0 END AS DEDUCTION_A, "
					+ " CASE WHEN PR.DEDUCTION_TYPE = 'B' THEN SUM(PR.AMOUNT) ELSE 0 END AS DEDUCTION_B " + " FROM TRN_PENSION_RECOVERY_DTLS PR ,TRN_PENSION_RQST_HDR TR "
					+ " WHERE PR.PENSIONER_CODE = TR.PENSIONER_CODE AND TR.location_code = :LocCode AND TR.CASE_STATUS = :status " + " AND PR.BILL_NO IS NULL AND "
					+ " ( ( (PR.RECOVERY_FROM_FLAG = :lStrTypeFlag AND PR.FROM_MONTH <= :lForMonth AND PR.TO_MONTH >= :lForMonth)");

			if (lPnsnrCodeLst != null && !lPnsnrCodeLst.isEmpty()) {
				hqlQuery.append(" and ( ");
				int flg = 0;
				for (int i = 0; i < lPnsnrCodeLst.size(); i++) {
					if (flg == 1) {
						hqlQuery.append(" or ");
					}
					hqlQuery.append(" PR.PENSIONER_CODE IN (" + lPnsnrCodeLst.get(i) + ")");
					flg = 1;
				}
				hqlQuery.append(") ");
			} else {
				// hqlQuery.append(" and 1 = 2 ");
				return dataList;
			}

			hqlQuery.append(" )");

			if (lStrPpoNoLst != null && lStrPpoNoLst.length() > 0) {
				hqlQuery.append(" or " + " ( PR.RECOVERY_FROM_FLAG in (:CVP,:DCRG)" + " AND TR.PPO_NO IN ( " + lStrPpoNoLst + " )" + " )");
			}

			/*
			 * if(lLstOmrPenCode != null && lLstOmrPenCode.size() > 0) {
			 * hqlQuery.append(" or " + " ( PR.RECOVERY_FROM_FLAG = :OMR" +
			 * " AND PR.PENSIONER_CODE IN ( :lStOMR )" + " )"); }
			 */

			hqlQuery.append(" ) ");

			hqlQuery.append(" GROUP BY TR.HEAD_CODE,PR.RECOVERY_FROM_FLAG,PR.DEDUCTION_TYPE " + " ) T GROUP BY T.HD_CODE,T.REC_TYPE ");

			SQLQuery lQuery = ghibSession.createSQLQuery(hqlQuery.toString());

			lQuery.setParameter("lForMonth", Integer.parseInt(lForMonth));
			lQuery.setParameter("lStrTypeFlag", "Monthly");
			lQuery.setParameter("status", "APPROVED");
			lQuery.setString("LocCode", lLocCode);

			if (lStrPpoNoLst != null && lStrPpoNoLst.length() > 0) {
				lQuery.setString("CVP", "CVP");
				lQuery.setString("DCRG", "DCRG");
			}
			/*
			 * if(lLstOmrPenCode != null && lLstOmrPenCode.size() > 0) {
			 * lQuery.setString("OMR","OMR"); lQuery.setParameterList("lStOMR",
			 * lLstOmrPenCode); }
			 */
			lQuery.addScalar("HeadCode", Hibernate.STRING);
			lQuery.addScalar("RecType", Hibernate.STRING);
			lQuery.addScalar("DedA", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("DedB", Hibernate.BIG_DECIMAL);

			dataList = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return dataList;
	}

	/*
	 * public List<TrnPensionBillReceipt> getHeadCodeWiseBillRcptEdpDtl(String
	 * lForMonth,String lStrLocCode,String lPostId,String lStrScheme,String
	 * lStrBranchCode)throws Exception {
	 * 
	 * select pv.head_Code, pr.edp_Code, pr.deduction_Type, pr.mjrhd_Code,
	 * pr.submjrhd_Code, pr.minhd_Code, pr.subhd_Code, sum(pr.amount) from
	 * Trn_Pension_Recovery_Dtls pr,valid_pcode_view pv where
	 * pr.recovery_From_Flag = 'Monthly' and pr.pensioner_Code =
	 * pv.pensioner_Code and pv.case_Status = 'APPROVED' and pr.from_Month <=
	 * 200908 and pr.to_Month >= 200908 and pr.pensioner_Code =
	 * pv.pensioner_code and pv.LOCATION_CODE = '10055' and pv.SCHEME_TYPE =
	 * 'IRLA' and pv.SEEN_FLAG = 'Y' and pv.post_id = 104505 and pv.branch_code
	 * = 12005 group by pr.edp_Code, pr.deduction_Type, pr.mjrhd_Code,
	 * pr.submjrhd_Code, pr.minhd_Code, pr.subhd_Code, pv.head_Code ORDER BY
	 * pv.head_Code;
	 * 
	 * 
	 * List<TrnPensionBillReceipt> dataList = null; try {
	 * 
	 * Session ghibSession = getSession(); StringBuffer hqlQuery = new
	 * StringBuffer();
	 * 
	 * hqlQuery.append(
	 * " select pv.headCode,pr.edpCode,pr.deductionType,pr.mjrhdCode,pr.submjrhdCode,pr.minhdCode,pr.subhdCode,"
	 * +
	 * " sum(pr.amount) from TrnPensionRecoveryDtls pr,ValidPcodeView pv where pr.recoveryFromFlag = :lStrTypeFlag and "
	 * +
	 * " pr.pensionerCode = pv.pensionerCode and pv.caseStatus = :status and pr.fromMonth <= :lForMonth and "
	 * +
	 * " pr.toMonth >= :lForMonth and pr.pensionerCode  = pv.pensionerCode and pv.locationCode = :lLocCode and "
	 * +
	 * " pv.schemeType = :lScheme and pv.seenFlag  = :lSeen and pv.postId = :lPostId and pv.branchCode = :lBranchCode"
	 * +
	 * " group by pr.edpCode,pr.deductionType,pr.mjrhdCode,pr.submjrhdCode,pr.minhdCode,pr.subhdCode,pv.headCode "
	 * + " ORDER BY pv.headCode ");
	 * 
	 * Query lQuery = ghibSession.createQuery(hqlQuery.toString());
	 * 
	 * lQuery.setParameter("lForMonth", Integer.parseInt(lForMonth));
	 * lQuery.setParameter("lStrTypeFlag", "Monthly");
	 * lQuery.setParameter("status", "APPROVED");
	 * lQuery.setString("lLocCode",lStrLocCode);
	 * lQuery.setString("lScheme",lStrScheme); lQuery.setString("lSeen","Y");
	 * lQuery.setString("lPostId",lPostId);
	 * lQuery.setString("lBranchCode",lStrBranchCode);
	 * 
	 * List resList = lQuery.list();
	 * 
	 * if (resList != null) { Iterator it = resList.iterator(); dataList = new
	 * ArrayList<TrnPensionBillReceipt>();
	 * 
	 * while (it.hasNext()) { Object[] tuple = (Object[]) it.next();
	 * TrnPensionBillReceipt billEdpVO = new TrnPensionBillReceipt();
	 * 
	 * billEdpVO.setTrnPensionBillHdrId(Long.valueOf("90"+tuple[0].toString()));
	 * billEdpVO.setEdpCode((String) tuple[1]); billEdpVO.setDedType((String)
	 * tuple[2]); billEdpVO.setMajorHead((String) tuple[3]);
	 * billEdpVO.setSubmajorHead((String) tuple[4]);
	 * billEdpVO.setMinorHead((String) tuple[5]); billEdpVO.setSubhead((String)
	 * tuple[6]);
	 * 
	 * billEdpVO.setAmount(new BigDecimal(tuple[7].toString()));
	 * 
	 * dataList.add(billEdpVO); } }
	 * 
	 * } catch (Exception e) { logger.error(" Error is : " + e, e); throw (e); }
	 * return dataList; }
	 */

	public List<TrnPensionBillDtls> getBillDtlsForMonth(String pnsnrCode, Long forMonth) throws Exception {

		List<TrnPensionBillDtls> lLstBillDtls = new ArrayList<TrnPensionBillDtls>();
		try {
			Session ghibSession = getSession();
			StringBuffer hqlQuery = new StringBuffer();

			hqlQuery.append(" FROM TrnPensionBillDtls D WHERE D.pensionerCode =:pnsnrCode AND D.payForMonth =:forMonth ");

			Query lQuery = ghibSession.createQuery(hqlQuery.toString());

			lQuery.setParameter("forMonth", Integer.parseInt(forMonth.toString()));
			lQuery.setParameter("pnsnrCode", pnsnrCode);

			List resList = lQuery.list();

			if (resList != null && !resList.isEmpty()) {

				for (int i = 0; i < resList.size(); i++) {
					lLstBillDtls.add((TrnPensionBillDtls) resList.get(i));
				}
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstBillDtls;
	}

	/**
	 * return List of Pension Cuts, itcut,CVP, Recovery, Arrear, other benifits
	 * etc of Pensioner.
	 * 
	 * @param lStrPensionerCode
	 * @param lStrStatus
	 * @param lForMonth
	 * @return
	 * @throws Exception
	 */
	public List getRcvrArrCutDtlsForMonth(String lStrPensionerCode, String lStrStatus, String lForMonth, String lArgsLocCode) throws Exception {

		List lRcvArrCutDtlLst = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append("SELECT T1.PPO_NO lppoNo,T1.p_Code pCode,SUM(PCUT) lPcut,SUM(ICUT) lIcut,SUM(SCUT) lScut,SUM(CCUT) lCcut,SUM(BENEFIT) lBenefit,"
					+ "sum(Pen_Rec.amount) lPenRecAmnt,sum(Pen_Arr.arr) lPenArr,sum(Pen_Arr.TIarr)lTIArr FROM "
					+ "("
					+ "select rq.ppo_no PPO_NO,rq.pensioner_code p_Code,"
					+ "sum(case when (pc.type_flag = 'PT' and pc.from_month <= :lForMonth and pc.to_month >= :lForMonth) OR (PC.TYPE_FLAG='PP' ) then pc.amount  else 0 end) PCUT,"
					+ "sum(case when pc.type_flag = 'IT' and pc.from_month <= :lForMonth and pc.to_month >= :lForMonth then pc.amount else 0 end) ICUT,"
					+ "sum(case when pc.type_flag = 'ST' and pc.from_month <= :lForMonth and pc.to_month >= :lForMonth  then pc.amount else 0 end) SCUT,"
					+ "sum(case when pc.type_flag = 'CVP' and pc.from_month <= :lForMonth and pc.to_month >= :lForMonth then pc.amount else 0 end) CCUT,"
					+ "sum(case when (pc.type_flag = 'TemporaryBenefit' and pc.from_month <= :lForMonth and pc.to_month >= :lForMonth) or pc.type_flag = 'PermanentBenefit' then pc.amount else 0 end) BENEFIT"
					+ " from trn_pension_rqst_hdr rq " + " left join trn_pension_it_cut_dtls pc on pc.pensioner_code=rq.pensioner_code"
					+ " where rq.pensioner_code = :lPnsrCode and rq.location_code=:LocCode AND ( rq.curr_case_status = 5 or rq.case_status = :lCaseStatus) " + " group by rq.ppo_no, rq.pensioner_code"
					+ " ) T1 " + " left join(" + " select rd.pensioner_code, rd.from_month, rd.to_month, sum(rd.amount) Amount" + " from trn_pension_recovery_dtls rd "
					+ " where ((rd.from_month <= :lForMonth and rd.to_month >= :lForMonth ) or (rd.from_month >= :lForMonth and rd.to_month is null )) " + " AND rd.recovery_from_flag = :lRcvStatus "
					+ " group by rd.pensioner_code, rd.from_month, rd.to_month" + " ) Pen_Rec on Pen_Rec.pensioner_code=T1.p_Code" + " left join( "
					+ " select ar.pensioner_code, ar.payment_from_yyyymm,ar.payment_to_yyyymm," + " sum(CASE WHEN ar.arrear_field_type != 'TI' THEN ar.difference_amount ELSE 0 END) arr,"
					+ " sum(CASE WHEN ar.arrear_field_type = 'TI' THEN ar.difference_amount ELSE 0 END) TIarr" + " from trn_pension_arrear_dtls ar"
					+ " where ar.payment_from_yyyymm <= :lForMonth and ar.payment_to_yyyymm >= :lForMonth" + " group by ar.pensioner_code, ar.payment_from_yyyymm,ar.payment_to_yyyymm"
					+ " ) Pen_Arr on Pen_Arr.pensioner_code=T1.p_Code" + " GROUP BY T1.PPO_NO,T1.p_Code");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("lCaseStatus", "APPROVED");
			lQuery.setString("lRcvStatus", lStrStatus);
			lQuery.setParameter("lPnsrCode", lStrPensionerCode);
			lQuery.setInteger("lForMonth", Integer.valueOf(lForMonth));
			lQuery.setString("LocCode", lArgsLocCode);

			lQuery.addScalar("lppoNo", Hibernate.STRING);
			lQuery.addScalar("pCode", Hibernate.STRING);
			lQuery.addScalar("lPcut", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lIcut", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lScut", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lCcut", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lBenefit", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lPenRecAmnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lPenArr", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lTIArr", Hibernate.BIG_DECIMAL);

			List lLst = lQuery.list();

			if (lLst != null && !lLst.isEmpty()) {
				Object[] lArrObj = (Object[]) lLst.get(0);
				lRcvArrCutDtlLst = new ArrayList();
				lRcvArrCutDtlLst.add(lArrObj[0]);
				lRcvArrCutDtlLst.add(lArrObj[1]);
				lRcvArrCutDtlLst.add(lArrObj[2]);
				lRcvArrCutDtlLst.add(lArrObj[3]);
				lRcvArrCutDtlLst.add(lArrObj[4]);
				lRcvArrCutDtlLst.add(lArrObj[5]);
				lRcvArrCutDtlLst.add(lArrObj[6]);
				lRcvArrCutDtlLst.add(lArrObj[7]);
				lRcvArrCutDtlLst.add(lArrObj[8]);
				lRcvArrCutDtlLst.add(lArrObj[9]);

			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lRcvArrCutDtlLst;
	}

	/**
	 * Put Pension Cuts, itcut,CVP, Recovery, Arrear, other benifits etc of
	 * individual Pensioner in inputMap
	 * 
	 * @param lPnsnrCodeLst
	 * @param inputMap
	 * @param lForMonth
	 * @throws Exception
	 */
	public void getRcvrArrCutDtlsMapForMonth(Map inputMap) throws Exception {

		Map<String, Object> lRcvArrCutDtlMap = null;

		try {
			String lStrStatus = bundleConst.getString("RECOVERY.MONTHLY");
			String lForMonth = (String) inputMap.get("Date");
			String lStrBrnchCode = (String) inputMap.get("BranchCode");
			String lStrScheme = (String) inputMap.get("Scheme");
			String lStrAuditor = null;
			char lFlag = (Character) inputMap.get("lFlag");
			if (inputMap.containsKey("lAuditor")) {
				lStrAuditor = inputMap.get("lAuditor").toString();
			}

			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append("SELECT T1.PPO_NO lppoNo,T1.p_Code pCode,SUM(PCUT) lPcut,SUM(ICUT) lIcut,SUM(SCUT) lScut,SUM(CCUT) lCcut,SUM(BENEFIT) lBenefit,"
					+ "sum(Pen_Rec.amount) lPenRecAmnt,sum(Pen_Arr.arr) lPenArr,sum(Pen_Arr.TIarr)lTIArr FROM "
					+ "("
					+ " select rq.ppo_no PPO_NO,rq.pensioner_code p_Code,"
					+ " sum(case when (pc.type_flag = 'PT' and pc.from_month <= :lForMonth and pc.to_month >= :lForMonth) OR (PC.TYPE_FLAG='PP' ) then pc.amount  else 0 end) PCUT,"
					+ " sum(case when pc.type_flag = 'IT' and pc.from_month <= :lForMonth and pc.to_month >= :lForMonth then pc.amount else 0 end) ICUT,"
					+ " sum(case when pc.type_flag = 'ST' and pc.from_month <= :lForMonth and pc.to_month >= :lForMonth  then pc.amount else 0 end) SCUT,"
					+ " sum(case when pc.type_flag = 'CVP' and pc.from_month <= :lForMonth and pc.to_month >= :lForMonth then pc.amount else 0 end) CCUT,"
					+ " sum(case when (pc.type_flag = 'TemporaryBenefit' and pc.from_month <= :lForMonth and pc.to_month >= :lForMonth) or pc.type_flag = 'PermanentBenefit' then pc.amount else 0 end) BENEFIT"
					+ " from trn_pension_rqst_hdr rq join " + lStrMonthlyView + " vp on vp.scheme_type = :lScheme ");

			// if("B".equals(inputMap.get("lFlag").toString()))
			lSBQuery.append("AND vp.lst_Act_Post_Id = :lPostId ");
			// else if("R".equals(inputMap.get("lFlag").toString()))
			// lSBQuery.append("AND vp.lst_Act_Post_Id = vp.post_Id ");

			lSBQuery.append("AND vp.location_Code= :locationCode and vp.location_Code=rq.location_code  ");

			if ("IRLA".equals(lStrScheme)) {
				if (lFlag == 'B') {
					lSBQuery.append("AND vp.branch_Code= :brnchCode ");
				}
				if (lFlag == 'R' && !"-1".equals(lStrBrnchCode)) {
					lSBQuery.append("AND vp.branch_Code= :brnchCode ");
				}

			}

			if (inputMap.containsKey("NonBilledBanks") && ((List) inputMap.get("NonBilledBanks")).size() != 0) {
				lSBQuery.append("and vp.branch_Code  in (:NonBilledBanks) ");
			}
			if (!inputMap.get("PPONoPara").equals("") && (lStrScheme.equals("RUBARU") || lStrScheme.equals("MONEY ORDER"))) {
				lSBQuery.append("AND vp.ppo_No = :ppoNo ");
			}
			/*
			 * if(inputMap.containsKey("BilledBranches") &&
			 * ((List)inputMap.get("BilledBranches")).size()!=0) {
			 * lSBQuery.append("and vp.branch_Code not in (:billedBranches) ");
			 * }
			 */

			lSBQuery.append("and vp.PENSIONER_CODE = rq.pensioner_code and vp.CASE_STATUS = rq.case_status" + " left join trn_pension_it_cut_dtls pc on pc.pensioner_code=rq.pensioner_code ");

			lSBQuery.append("AND ( rq.curr_case_status = 5 or rq.case_status = :lCaseStatus) " + " group by rq.ppo_no, rq.pensioner_code" + " ) T1 " + " left join("
					+ " select rd.pensioner_code, rd.from_month, rd.to_month, sum(rd.amount) Amount" + " from trn_pension_recovery_dtls rd "
					+ " where ((rd.from_month <= :lForMonth and rd.to_month >= :lForMonth ) or (rd.from_month >= :lForMonth and rd.to_month is null )) " + " AND rd.recovery_from_flag = :lRcvStatus "
					+ " group by rd.pensioner_code, rd.from_month, rd.to_month" + " ) Pen_Rec on Pen_Rec.pensioner_code=T1.p_Code" + " left join( "
					+ " select ar.pensioner_code, ar.payment_from_yyyymm,ar.payment_to_yyyymm," + " sum(CASE WHEN ar.arrear_field_type != 'TI' THEN ar.difference_amount ELSE 0 END) arr,"
					+ " sum(CASE WHEN ar.arrear_field_type = 'TI' THEN ar.difference_amount ELSE 0 END) TIarr" + " from trn_pension_arrear_dtls ar"
					+ " where ar.payment_from_yyyymm <= :lForMonth and ar.payment_to_yyyymm >= :lForMonth" + " group by ar.pensioner_code, ar.payment_from_yyyymm,ar.payment_to_yyyymm"
					+ " ) Pen_Arr on Pen_Arr.pensioner_code=T1.p_Code" + " GROUP BY T1.PPO_NO,T1.p_Code");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("lCaseStatus", "APPROVED");
			lQuery.setString("lRcvStatus", lStrStatus);
			lQuery.setInteger("lForMonth", Integer.valueOf(lForMonth));
			lQuery.setString("lScheme", lStrScheme);
			lQuery.setParameter("locationCode", SessionHelper.getLocationCode(inputMap).toString());

			if ("B".equals(inputMap.get("lFlag").toString())) {
				lQuery.setString("lPostId", SessionHelper.getPostId(inputMap).toString());
			} else {
				lQuery.setParameter("lPostId", lStrAuditor);
			}

			if ("IRLA".equals(lStrScheme)) {
				if (lFlag == 'B') {
					lQuery.setParameter("brnchCode", lStrBrnchCode);
				}
				if (lFlag == 'R' && !"-1".equals(lStrBrnchCode)) {
					lQuery.setParameter("brnchCode", lStrBrnchCode);
				}
			}
			if (!inputMap.get("PPONoPara").equals("") && (lStrScheme.equals("RUBARU") || lStrScheme.equals("MONEY ORDER"))) {
				lQuery.setParameter("ppoNo", inputMap.get("PPONoPara").toString());
			}

			/*
			 * if(inputMap.containsKey("BilledBranches") &&
			 * ((List)inputMap.get("BilledBranches")).size()!=0) {
			 * lQuery.setParameterList("billedBranches",
			 * (List)inputMap.get("BilledBranches")); }
			 */
			if (inputMap.containsKey("NonBilledBanks") && ((List) inputMap.get("NonBilledBanks")).size() != 0) {
				lQuery.setParameterList("NonBilledBanks", (List) inputMap.get("NonBilledBanks"));
			}

			lQuery.addScalar("lppoNo", Hibernate.STRING);
			lQuery.addScalar("pCode", Hibernate.STRING);
			lQuery.addScalar("lPcut", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lIcut", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lScut", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lCcut", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lBenefit", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lPenRecAmnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lPenArr", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("lTIArr", Hibernate.BIG_DECIMAL);

			List resultList = lQuery.list();
			if (!resultList.isEmpty()) {
				lRcvArrCutDtlMap = new HashMap<String, Object>();
				for (Object lObj : resultList) {
					Object[] lArrObj = (Object[]) lObj;
					List lTempLst = new ArrayList();
					lTempLst.add(lArrObj[0]);
					lTempLst.add(lArrObj[1]);
					lTempLst.add(lArrObj[2]);
					lTempLst.add(lArrObj[3]);
					lTempLst.add(lArrObj[4]);
					lTempLst.add(lArrObj[5]);
					lTempLst.add(lArrObj[6]);
					lTempLst.add(lArrObj[7]);
					lTempLst.add(lArrObj[8]);
					lTempLst.add(lArrObj[9]);
					lRcvArrCutDtlMap.put(lArrObj[1] + "~" + lForMonth, lTempLst);
				}
				inputMap.put("lRcvArrCutDtlMap", lRcvArrCutDtlMap);
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	// method to get recovery dtls of all the pensioners of all the months in
	// one shot

	public void getAllRcvrMap(Map inputMap) throws Exception {

		Map<String, Double> lRcvDtlMap = new HashMap<String, Double>();
		Map<String, List<TrnPensionRecoveryDtls>> lRcvrDtlVOMap = new HashMap<String, List<TrnPensionRecoveryDtls>>();
		Integer lIntTotalRcvryRecords = 0;
		List<String> lLstBrnchCode = new ArrayList<String>();
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtls = null;
		try {
			String lStrStatus = bundleConst.getString("RECOVERY.MONTHLY");
			String lForMonth = (String) inputMap.get("Date");
			lLstBrnchCode = (List<String>) inputMap.get("BranchCode");
			String lStrCaseStatus = (String) inputMap.get("lStrCaseStatus");

			Double lAmnt = 0d;
			Double lRcvryMapAmt = 0d;
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			// lSBQuery.append("SELECT rd.pensionerCode,SUM(rd.amount) FROM TrnPensionRecoveryDtls rd,"
			// + lStrMonthlyViewVO
			// +
			// " pv WHERE pv.caseStatus = :lCaseStatus AND rd.pensionerCode = pv.pensionerCode AND  rd.recoveryFromFlag = :lRcvStatus  "
			// + "AND pv.locationCode = :locationCode "
			// +
			// "and ((rd.fromMonth <= :lForMonth AND rd.toMonth >= :lForMonth) OR (rd.fromMonth = :lForMonth AND rd.toMonth IS NULL)) ");
			//
			// lSBQuery.append(" AND pv.branchCode= :lBranchCode ");
			//
			// lSBQuery.append("GROUP BY rd.pensionerCode");

			lSBQuery.append("SELECT rd FROM TrnPensionRecoveryDtls rd," + lStrMonthlyViewVO
					+ " pv WHERE pv.caseStatus = :lCaseStatus AND rd.pensionerCode = pv.pensionerCode AND  rd.recoveryFromFlag = :lRcvStatus  " + "AND pv.locationCode = :locationCode "
					+ "and ((rd.fromMonth <= :lForMonth AND rd.toMonth >= :lForMonth) OR (rd.fromMonth = :lForMonth AND rd.toMonth IS NULL)) ");

			lSBQuery.append(" AND pv.branchCode in (:lBranchCode) ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lCaseStatus", lStrCaseStatus);
			lQuery.setString("lRcvStatus", lStrStatus);
			lQuery.setInteger("lForMonth", Integer.valueOf(lForMonth));
			lQuery.setString("locationCode", SessionHelper.getLocationCode(inputMap).toString());
			lQuery.setParameterList("lBranchCode", lLstBrnchCode);

			List<TrnPensionRecoveryDtls> resultList = lQuery.list();

			if (resultList != null && !resultList.isEmpty()) {
				for (TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtls : resultList) {
					lAmnt = lObjTrnPensionRecoveryDtls.getAmount().doubleValue();
					if (lAmnt > 0) {
						lRcvryMapAmt = lRcvDtlMap.get(lObjTrnPensionRecoveryDtls.getPensionerCode());
						if (lRcvryMapAmt != null) {
							lRcvryMapAmt = lRcvryMapAmt + lObjTrnPensionRecoveryDtls.getAmount().doubleValue();
							lLstTrnPensionRecoveryDtls = lRcvrDtlVOMap.get(lObjTrnPensionRecoveryDtls.getPensionerCode());
							lLstTrnPensionRecoveryDtls.add(lObjTrnPensionRecoveryDtls);
							lRcvDtlMap.put(lObjTrnPensionRecoveryDtls.getPensionerCode(), lRcvryMapAmt);
						} else {
							lLstTrnPensionRecoveryDtls = new ArrayList<TrnPensionRecoveryDtls>();
							lLstTrnPensionRecoveryDtls.add(lObjTrnPensionRecoveryDtls);
							lRcvDtlMap.put(lObjTrnPensionRecoveryDtls.getPensionerCode(), lAmnt);
						}
						lRcvrDtlVOMap.put(lObjTrnPensionRecoveryDtls.getPensionerCode(), lLstTrnPensionRecoveryDtls);
						lIntTotalRcvryRecords++;
					}
				}
			}
			inputMap.put("lRcvDtlMap", lRcvDtlMap);
			inputMap.put("lRcvrDtlVOMap", lRcvrDtlVOMap);
			inputMap.put("lIntTotalRcvryRecords", lIntTotalRcvryRecords);

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	// method to get cvp cut dtls of all the pensioners of current months in
	// one
	// shot
	public void getAllCVPCutMap(Map inputMap) throws Exception {

		Map<String, List<TrnCvpRestorationDtls>> lCVPDtlMap = new HashMap<String, List<TrnCvpRestorationDtls>>();
		List<String> lLstBrnchCode = new ArrayList<String>();
		Date lDtMonthEnd = null;
		Date lDtMonthStart = null;
		Calendar lObjCal = null;
		Date lDtForMonth = null;
		SimpleDateFormat lSdf = null;
		List<TrnCvpRestorationDtls> lLstTrnCvpRestorationDtlsObj = null;
		try {
			String lForMonth = (String) inputMap.get("Date");
			lLstBrnchCode = (List<String>) inputMap.get("BranchCode");
			String lStrCaseStatus = (String) inputMap.get("lStrCaseStatus");
			lSdf = new SimpleDateFormat("yyyyMM");
			lDtForMonth = lSdf.parse(lForMonth);
			lObjCal = new GregorianCalendar();
			lObjCal.setTime(lDtForMonth);
			int lastDateOfMonth = lObjCal.getActualMaximum(Calendar.DAY_OF_MONTH);
			lObjCal.set(Calendar.DATE, lastDateOfMonth);
			lDtMonthEnd = lObjCal.getTime();
			lObjCal.set(Calendar.DATE, 1);
			lDtMonthStart = lObjCal.getTime();

			Double lAmnt = 0d;

			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();
			lSBQuery.append("SELECT crd \n");
			lSBQuery.append("from \n");
			lSBQuery.append("TrnCvpRestorationDtls crd, \n");
			lSBQuery.append(lStrMonthlyViewVO + " pv \n");
			lSBQuery.append("where \n");
			lSBQuery.append("pv.caseStatus = :lCaseStatus \n");
			lSBQuery.append("and crd.pensionerCode = pv.pensionerCode \n");
			lSBQuery.append("and pv.locationCode = :locationCode \n");
			// lSBQuery.append("and ((((crd.fromDate between :lMonthStrtDt and :lMonthEndDt) or  (:lMonthStrtDt > crd.fromDate)) \n");
			// lSBQuery.append("and ((crd.toDate between :lMonthStrtDt and :lMonthEndDt) or (crd.toDate > :lMonthEndDt))) \n");
			// lSBQuery.append("or (crd.fromDate is null and crd.toDate is null and crd.amount != 0)) \n");
			//lSBQuery.append("and  crd.restnAplnReceivedFlag = :lRestnAplFlag \n");
			lSBQuery.append("and  pv.branchCode in (:lBranchCode) \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lCaseStatus", lStrCaseStatus);
			// lQuery.setDate("lMonthStrtDt", lDtMonthStart);
			// lQuery.setDate("lMonthEndDt", lDtMonthEnd);
			lQuery.setString("locationCode", SessionHelper.getLocationCode(inputMap).toString());
			lQuery.setParameterList("lBranchCode", lLstBrnchCode);
			//lQuery.setString("lRestnAplFlag", "N");

			List<TrnCvpRestorationDtls> resultList = lQuery.list();

			if (resultList != null && !resultList.isEmpty()) {

				for (TrnCvpRestorationDtls lObjTrnCvpRestorationDtls : resultList) {
					lLstTrnCvpRestorationDtlsObj = lCVPDtlMap.get(lObjTrnCvpRestorationDtls.getPensionerCode());
					lAmnt = lObjTrnCvpRestorationDtls.getAmount().doubleValue();
					if (lLstTrnCvpRestorationDtlsObj != null) {
						if (lAmnt > 0) {
							lLstTrnCvpRestorationDtlsObj.add(lObjTrnCvpRestorationDtls);
						}
					} else {
						lLstTrnCvpRestorationDtlsObj = new ArrayList<TrnCvpRestorationDtls>();
						if (lAmnt > 0) {
							lLstTrnCvpRestorationDtlsObj.add(lObjTrnCvpRestorationDtls);
						}
					}
					lCVPDtlMap.put(lObjTrnCvpRestorationDtls.getPensionerCode(), lLstTrnCvpRestorationDtlsObj);
				}
			}
			inputMap.put("lCVPDtlMap", lCVPDtlMap);

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	// method to get all six pay arrear dtls of all the pensioners of current
	// months in
	// one
	// shot
	public void getAllSixPayArrearMap(Map inputMap) throws Exception {

		Map<String, Object> lSixPayArrearDtlMap = new HashMap<String, Object>();
		List<String> lLstBrnchCode = new ArrayList<String>();

		try {
			String lForMonth = (String) inputMap.get("Date");
			lLstBrnchCode = (List<String>) inputMap.get("BranchCode");
			String lStrCaseStatus = (String) inputMap.get("lStrCaseStatus");

			Double lAmnt = 0d;

			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append("select psa.pensionerCode,SUM(psa.installmentAmnt) \n");
			lSBQuery.append("from TrnPensionSixpayfpArrear psa, \n");
			lSBQuery.append(lStrMonthlyViewVO + " pv \n");
			lSBQuery.append("where  \n");
			lSBQuery.append("psa.pensionerCode = pv.pensionerCode  \n");
			lSBQuery.append("and pv.locationCode = :locationCode  \n");
			lSBQuery.append("and pv.caseStatus = :lCaseStatus   \n");
			lSBQuery.append("and pv.branchCode in (:lBranchCode)  \n");
			lSBQuery.append("and psa.payInMonth = :lForMonth   \n");
			lSBQuery.append("and psa.paidFlag = :lPaidFlag   \n");
			// lSBQuery.append("and psa.arrearType = :lStrArrearType   \n");
			lSBQuery.append("and psa.activeFlag = :lStrActiveFlag   \n");
			lSBQuery.append("and psa.configFromSuppl != :lSuppFlag \n");
			lSBQuery.append("group by psa.pensionerCode   \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lCaseStatus", lStrCaseStatus);
			lQuery.setInteger("lForMonth", Integer.valueOf(lForMonth));
			lQuery.setString("locationCode", SessionHelper.getLocationCode(inputMap).toString());
			lQuery.setParameterList("lBranchCode", lLstBrnchCode);
			lQuery.setCharacter("lSuppFlag", 'Y');
			// lQuery.setString("lStrArrearType", "S");
			lQuery.setString("lStrActiveFlag", "Y");
			lQuery.setCharacter("lPaidFlag", 'N');

			List resultList = lQuery.list();

			if (resultList != null && !resultList.isEmpty()) {
				for (Object lObj : resultList) {
					Object[] lArrObj = (Object[]) lObj;
					lAmnt = Double.valueOf(lArrObj[1].toString());
					if (lAmnt > 0) {
						lSixPayArrearDtlMap.put(lArrObj[0].toString(), lAmnt);
					}
				}
			}
			inputMap.put("lSixPayArrearDtlMap", lSixPayArrearDtlMap);

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	// method to get pension cut dtls of all the pensioners of current months in
	// one
	// shot
	public void getAllPensionCutMap(Map inputMap) throws Exception {

		Map<String, List<TrnPensionCutDtls>> lPensionCutDtlMap = new HashMap<String, List<TrnPensionCutDtls>>();
		List<String> lLstBrnchCode = new ArrayList<String>();
		Date lDtMonthEnd = null;
		Date lDtMonthStart = null;
		Calendar lObjCal = null;
		Date lDtForMonth = null;
		SimpleDateFormat lSdf = null;
		List<TrnPensionCutDtls> lLstTrnPensionCutDtlsObj = null;
		try {
			String lForMonth = (String) inputMap.get("Date");
			lLstBrnchCode = (List<String>) inputMap.get("BranchCode");
			String lStrCaseStatus = (String) inputMap.get("lStrCaseStatus");
			lSdf = new SimpleDateFormat("yyyyMM");
			lDtForMonth = lSdf.parse(lForMonth);
			lObjCal = new GregorianCalendar();
			lObjCal.setTime(lDtForMonth);
			int lastDateOfMonth = lObjCal.getActualMaximum(Calendar.DAY_OF_MONTH);
			lObjCal.set(Calendar.DATE, lastDateOfMonth);
			lDtMonthEnd = lObjCal.getTime();
			lObjCal.set(Calendar.DATE, 1);
			lDtMonthStart = lObjCal.getTime();

			Double lAmnt = 0d;

			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();
			lSBQuery.append("SELECT pcd \n");
			lSBQuery.append("from \n");
			lSBQuery.append("TrnPensionCutDtls pcd, \n");
			lSBQuery.append(lStrMonthlyViewVO + " pv \n");
			lSBQuery.append("where \n");
			lSBQuery.append("pv.caseStatus = :lCaseStatus \n");
			lSBQuery.append("and pcd.pensionerCode = pv.pensionerCode \n");
			lSBQuery.append("and pv.locationCode = :locationCode \n");
			lSBQuery.append("and  pv.branchCode in (:lBranchCode) \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lCaseStatus", lStrCaseStatus);
			lQuery.setString("locationCode", SessionHelper.getLocationCode(inputMap).toString());
			lQuery.setParameterList("lBranchCode", lLstBrnchCode);

			List<TrnPensionCutDtls> resultList = lQuery.list();

			if (resultList != null && !resultList.isEmpty()) {

				for (TrnPensionCutDtls lObjTrnPensionCutDtls : resultList) {
					lLstTrnPensionCutDtlsObj = lPensionCutDtlMap.get(lObjTrnPensionCutDtls.getPensionerCode());
					lAmnt = lObjTrnPensionCutDtls.getAmount().doubleValue();
					if (lLstTrnPensionCutDtlsObj != null) {
						if (lAmnt > 0) {
							lLstTrnPensionCutDtlsObj.add(lObjTrnPensionCutDtls);
						}
					} else {
						lLstTrnPensionCutDtlsObj = new ArrayList<TrnPensionCutDtls>();
						if (lAmnt > 0) {
							lLstTrnPensionCutDtlsObj.add(lObjTrnPensionCutDtls);
						}
					}
					lPensionCutDtlMap.put(lObjTrnPensionCutDtls.getPensionerCode(), lLstTrnPensionCutDtlsObj);
				}
			}
			inputMap.put("lPensionCutDtlMap", lPensionCutDtlMap);

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	// method to get cut dtls of all the pensioners of all the months in one
	// shot

	public void getAllCutsMap(Map inputMap) throws Exception {

		Map<String, Object> lCutDtlMap = null;
		List lOtherParty = new ArrayList();
		List lSubOtherParty = new ArrayList();
		String lStrBrnchCode = "";
		StringBuffer sbLog = new StringBuffer();
		try {
			String lForMonth = (String) inputMap.get("Date");
			lStrBrnchCode = (String) inputMap.get("BranchCode");
			String lStrScheme = (String) inputMap.get("Scheme");
			String lStrAuditor = null;
			String lLstDate = "";
			int lFrmMonth = 0;
			int lToMonth = 0;
			String lStrTypeFlg = null;
			Double lPcut = 0d;
			Double lIcut = 0d;
			Double lScut = 0d;
			Double lPMcut = 0d;
			Double lTempBen = 0d;
			Long lLngOthParty = 0l;
			String lStrCaseStatus = (String) inputMap.get("lStrCaseStatus");
			new SimpleDateFormat("dd-mm-yyyy");
			new MonthlyCases();
			MstPensionerFamilyDtls lLstFPmembers = null;
			char lFlag = (Character) inputMap.get("lFlag");
			if (inputMap.containsKey("lAuditor")) {
				lStrAuditor = inputMap.get("lAuditor").toString();
			}
			String lName = "";
			Date lFPDODdate = null;
			Calendar myCal = Calendar.getInstance();
			myCal.set(Calendar.MONTH, (Integer.parseInt(lForMonth.substring(4, 6)) - 1));
			myCal.set(Calendar.YEAR, Integer.parseInt(lForMonth.substring(0, 4)));
			myCal.set(Calendar.DAY_OF_MONTH, myCal.getActualMaximum(Calendar.DAY_OF_MONTH));

			Date lDtLstpaidDt = myCal.getTime();

			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append(" SELECT pv.pensioner_code pCode,pc.type_flag typeFlag,pc.from_month frmMonth,pc.to_month toMonth,SUM(pc.amount) lAmnt, "
					+ " pc.other_party_id lOth,pv.ppo_no lppo,pv.first_name lFirst,pv.middle_name lMidle,pv.last_name lLast,pv.date_of_death lDOD " + " FROM trn_pension_it_cut_dtls pc,"
					+ lStrMonthlyView + " pv WHERE pv.CASE_STATUS = :lCaseStatus AND " + " pc.pensioner_code = pv.PENSIONER_CODE AND pv.LAST_PAID_DATE < :lstPaidDate AND "
					+ " pv.LOCATION_CODE = :locationCode AND pv.lst_Act_Post_Id = :postId AND pv.SCHEME_TYPE = :lScheme " + " AND (pc.from_month <= :lForMonth  OR pc.from_month = 0 ) ");

			if ("IRLA".equalsIgnoreCase(lStrScheme)) {
				if (lFlag == 'B') {
					lSBQuery.append("AND pv.branch_Code= :BrnchCode ");
				}
				if (lFlag == 'R' && !"".equals(lStrBrnchCode) && !"-1".equals(lStrBrnchCode)) {
					lSBQuery.append("AND pv.branch_Code= :BrnchCode ");
				}

			}
			// lSBQuery.append("and pv.pensioner_Code in (557605) ");
			if (inputMap.containsKey("NonBilledBanks") && ((List) inputMap.get("NonBilledBanks")).size() != 0) {
				lSBQuery.append(" and pv.branch_Code  in (:NonBilledBanks) ");
			}
			if (!inputMap.get("PPONoPara").equals("") && (lStrScheme.equals("RUBARU") || lStrScheme.equals("MONEY ORDER"))) {
				lSBQuery.append("AND pv.ppo_No = :ppoNo ");
			}

			lSBQuery.append("GROUP BY pv.pensioner_code,pc.type_flag,pc.from_month,pc.to_month, " + "  pc.other_party_id,pv.ppo_no,pv.first_name,pv.middle_name,pv.last_name,pv.date_of_death "
					+ "  ORDER BY pc.type_flag,pc.OTHER_PARTY_ID,pv.pensioner_code");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("lCaseStatus", lStrCaseStatus);
			lQuery.setInteger("lForMonth", Integer.valueOf(lForMonth));
			lQuery.setString("lScheme", lStrScheme);
			lQuery.setString("locationCode", SessionHelper.getLocationCode(inputMap).toString());
			lQuery.setDate("lstPaidDate", lDtLstpaidDt);
			if (lFlag == 'B') {
				lQuery.setString("postId", SessionHelper.getPostId(inputMap).toString());
			}
			if (lFlag == 'R' && !"-1".equals(lStrAuditor)) {
				lQuery.setString("postId", lStrAuditor);
			}

			if ("IRLA".equalsIgnoreCase(lStrScheme)) {
				if (lFlag == 'B') {
					lQuery.setString("BrnchCode", lStrBrnchCode);
				}
				if (lFlag == 'R' && !"".equals(lStrBrnchCode) && !"-1".equals(lStrBrnchCode)) {
					lQuery.setString("BrnchCode", lStrBrnchCode);
				}
			}
			if (inputMap.containsKey("NonBilledBanks") && ((List) inputMap.get("NonBilledBanks")).size() != 0) {
				lQuery.setParameterList("NonBilledBanks", (List) inputMap.get("NonBilledBanks"));
			}
			if (!inputMap.get("PPONoPara").equals("") && (lStrScheme.equals("RUBARU") || lStrScheme.equals("MONEY ORDER"))) {
				lQuery.setParameter("ppoNo", inputMap.get("PPONoPara").toString());
			}

			lQuery.addScalar("pCode", Hibernate.STRING); // 0
			lQuery.addScalar("typeFlag", Hibernate.STRING);// 1
			lQuery.addScalar("frmMonth", Hibernate.INTEGER);// 2
			lQuery.addScalar("toMonth", Hibernate.INTEGER);// 3
			lQuery.addScalar("lAmnt", Hibernate.BIG_DECIMAL);// 4
			lQuery.addScalar("lOth", Hibernate.BIG_DECIMAL);// 5
			lQuery.addScalar("lppo", Hibernate.STRING);// 6
			lQuery.addScalar("lFirst", Hibernate.STRING);// 7
			lQuery.addScalar("lMidle", Hibernate.STRING);// 8
			lQuery.addScalar("lLast", Hibernate.STRING);// 9
			lQuery.addScalar("lDOD", Hibernate.DATE);// 10

			List resultList = lQuery.list();

			if (resultList != null && !resultList.isEmpty()) {
				lCutDtlMap = new HashMap<String, Object>();

				for (Object lObj : resultList) {
					Object[] lArrObj = (Object[]) lObj;
					new ArrayList();

					lStrTypeFlg = lArrObj[1].toString();
					lFrmMonth = lArrObj[2] == null ? 0 : Integer.valueOf(lArrObj[2].toString());
					lToMonth = lArrObj[3] == null ? 0 : Integer.valueOf(lArrObj[3].toString());

					if (lArrObj[5] != null) {
						lLngOthParty = Long.parseLong(lArrObj[5].toString());
					}

					if ("PermanentBenefit".equals(lStrTypeFlg)) {
						lCutDtlMap.put(lArrObj[0] + "PermanentBenefit", Double.valueOf(lArrObj[4].toString()));
					}

					if ("PM".equals(lStrTypeFlg)) {
						lPMcut = Double.valueOf(lArrObj[4].toString());
						if (lCutDtlMap.containsKey(lArrObj[0] + "PM")) {
							lPMcut += Double.valueOf(lCutDtlMap.get(lArrObj[0] + "PM").toString());
						}
						if (lPMcut > 0) {
							lCutDtlMap.put(lArrObj[0] + "PM", lPMcut);
						}

						lSubOtherParty = new ArrayList();
						lSubOtherParty.add(lLngOthParty); // party 0
						lSubOtherParty.add(lArrObj[0]); // Pcode 1
						lSubOtherParty.add(lArrObj[4]); // Amnt 2
						lSubOtherParty.add(lArrObj[6]); // PPO No 3

						if (lArrObj[10] == null) {
							lName = lArrObj[7] != null ? (String) lArrObj[7] : "" + lArrObj[8] != null ? (String) lArrObj[8] : "" + lArrObj[9] != null ? (String) lArrObj[9] : "";
						} else {
							if (inputMap.containsKey("lMapFamilyDtls")) {
								lLstFPmembers = new MstPensionerFamilyDtls();
								lLstFPmembers = (MstPensionerFamilyDtls) ((Map) inputMap.get("lMapFamilyDtls")).get("Family" + lArrObj[0].toString());
								if (lLstFPmembers != null) {
									lFPDODdate = lLstFPmembers.getDateOfDeath();
									if (lFPDODdate == null) {
										lName = lLstFPmembers.getName();
									}
								}
							}
						}
						lSubOtherParty.add(lName); // Name 4
						lOtherParty.add(lSubOtherParty);
						lSubOtherParty = null;
					}

					if ("PP".equals(lStrTypeFlg)) {
						lCutDtlMap.put(lArrObj[0] + "PP", Double.valueOf(lArrObj[4].toString()));
					}
					if (lToMonth <= Integer.parseInt(lForMonth) && lToMonth != 0) {
						lLstDate = lArrObj[3].toString();
					} else {
						lLstDate = lForMonth;
					}

					if (lArrObj[2] != null && lArrObj[3] != null && lFrmMonth != 0 && lToMonth != 0) {
						for (Integer i = lFrmMonth; i <= Integer.valueOf(lLstDate);) {

							if ("PT".equals(lStrTypeFlg)) {
								lPcut = Double.valueOf(lArrObj[4].toString());
							}
							if ("IT".equals(lStrTypeFlg)) {
								lIcut = Double.valueOf(lArrObj[4].toString());
							}
							if ("ST".equals(lStrTypeFlg)) {
								lScut = Double.valueOf(lArrObj[4].toString());
							}
							if ("TemporaryBenefit".equals(lStrTypeFlg)) {
								lTempBen = Double.valueOf(lArrObj[4].toString());
							}

							if (lCutDtlMap.containsKey(lArrObj[0] + "PT" + "~" + i) && ("PT".equals(lStrTypeFlg))) {
								lPcut += Double.valueOf(lCutDtlMap.get(lArrObj[0] + "PT" + "~" + i).toString());
							}
							if (lPcut > 0 && ("PT".equals(lStrTypeFlg))) {
								lCutDtlMap.put(lArrObj[0] + "PT" + "~" + i, lPcut);
							}

							if (lCutDtlMap.containsKey(lArrObj[0] + "IT" + "~" + i) && "IT".equals(lStrTypeFlg)) {
								lIcut += Double.valueOf(lCutDtlMap.get(lArrObj[0] + "IT" + "~" + i).toString());
							}
							if (lIcut > 0 && "IT".equals(lStrTypeFlg)) {
								lCutDtlMap.put(lArrObj[0] + "IT" + "~" + i, lIcut);
							}

							if (lCutDtlMap.containsKey(lArrObj[0] + "ST" + "~" + i) && "ST".equals(lStrTypeFlg)) {
								lScut += Double.valueOf(lCutDtlMap.get(lArrObj[0] + "ST" + "~" + i).toString());
							}
							if (lScut > 0 && "ST".equals(lStrTypeFlg)) {
								lCutDtlMap.put(lArrObj[0] + "ST" + "~" + i, lScut);
							}

							if (lCutDtlMap.containsKey(lArrObj[0] + "TempBenefit" + "~" + i) && ("TemporaryBenefit".equals(lStrTypeFlg))) {
								lTempBen += Double.valueOf(lCutDtlMap.get(lArrObj[0] + "TempBenefit" + "~" + i).toString());
							}
							if (lTempBen > 0 && ("TemporaryBenefit".equals(lStrTypeFlg))) {
								lCutDtlMap.put(lArrObj[0] + "TempBenefit" + "~" + i, lTempBen);
							}

							if (i.toString().length() < 6) {
								sbLog.append("Problem in trn_pension_it_cut_dtls pensioner code is " + lArrObj[0].toString() + "for month is " + i + "location "
										+ SessionHelper.getLocationCode(inputMap));
								try {
									new MonthlyLogger(inputMap).writeMonthlyLog(sbLog.toString());
								} catch (Exception e) {
									logger.error("Error while writing Monthly log..." + e, e);
								}
								throw new Exception();
							}

							i += ((Integer.parseInt((i.toString().substring(4, 6)))) == 12) ? 89 : 1;

							lPcut = 0d;
							lIcut = 0d;
							lScut = 0d;
							lTempBen = 0d;

						}

					}
				}
				inputMap.put("lCutDtlMap", lCutDtlMap);
				if (lOtherParty != null) {
					inputMap.put("lOthPartyLst", lOtherParty);
				}
			}
		} catch (Exception e) {
			sbLog.append("getAllCutsMap Query threw error for branch " + lStrBrnchCode + "locatiion" + SessionHelper.getLocationCode(inputMap));
			sbLog.append(getStackTrace(e));
			try {
				new MonthlyLogger(inputMap).writeMonthlyLog(sbLog.toString());
			} catch (Exception ex) {
				logger.error("Error while writing Monthly log..." + ex, ex);
				throw (ex);
			}
			throw (e);
		}
	}

	// method to get arrear dtls of all the pensioners of all the months in one
	// shot

	public void getAllArrearMap(Map inputMap) throws Exception {

		Map<String, Object> lArrDtlMap = new HashMap<String, Object>();
		List<String> lLstBrnchCode = new ArrayList<String>();

		try {
			String lForMonth = (String) inputMap.get("Date");
			lLstBrnchCode = (List<String>) inputMap.get("BranchCode");
			String lStrCaseStatus = (String) inputMap.get("lStrCaseStatus");
			Double lPnsnArrAmnt = 0d;
			Double lTIArrAmnt = 0d;
			Double lLCArrAmt = 0d;
			Double l6PCArrAmt = 0d;
			Double lOthrDiffArrAmt = 0d;
			Double lCommutationArrAmt = 0d;
			Double lDAArrAmt = 0d;
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append("SELECT ar.pensionerCode,ar.arrearFieldType,SUM(ar.totalDifferenceAmt) FROM TrnPensionArrearDtls ar, " + lStrMonthlyViewVO
					+ " pv WHERE pv.caseStatus = :lCaseStatus AND ar.pensionerCode = pv.pensionerCode  AND pv.locationCode = :locationCode  AND "
					+ " (ar.paymentFromYyyymm <= :lForMonth AND ar.paymentToYyyymm >= :lForMonth) and ar.paidFlag = :lPaidFlag ");

			lSBQuery.append("AND pv.branchCode in (:BrnchCode) ");

			lSBQuery.append("GROUP BY ar.pensionerCode,ar.arrearFieldType");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lCaseStatus", lStrCaseStatus);
			lQuery.setInteger("lForMonth", Integer.valueOf(lForMonth));
			lQuery.setCharacter("lPaidFlag", 'N');
			lQuery.setString("locationCode", SessionHelper.getLocationCode(inputMap).toString());
			lQuery.setParameterList("BrnchCode", lLstBrnchCode);

			List resultList = lQuery.list();

			if (resultList != null && !resultList.isEmpty()) {

				for (Object lObj : resultList) {
					Object[] lArrObj = (Object[]) lObj;
					new ArrayList();
					if (lArrObj[0] != null && lArrObj[1] != null && lArrObj[2] != null) {
						if ("TI".equals(lArrObj[1])) {
							lTIArrAmnt = Double.valueOf(lArrObj[2].toString());
							if (lTIArrAmnt > 0) {
								lArrDtlMap.put(lArrObj[0] + "~TI", lTIArrAmnt);
							}
						} else if (bundleCaseConst.getString("ARREARTYPE.LC").equals(lArrObj[1])) {
							lLCArrAmt = Double.valueOf(lArrObj[2].toString());
							if (lLCArrAmt > 0) {
								lArrDtlMap.put(lArrObj[0] + "~LC", lLCArrAmt);
							}
						} else if (bundleCaseConst.getString("ARREARTYPE.DA").equals(lArrObj[1])) {
							lDAArrAmt = Double.valueOf(lArrObj[2].toString());
							if (lDAArrAmt > 0) {
								lArrDtlMap.put(lArrObj[0] + "~DA", lDAArrAmt);
							}
						} else if (bundleCaseConst.getString("ARREARTYPE.6PC").equals(lArrObj[1])) {
							l6PCArrAmt = Double.valueOf(lArrObj[2].toString());
							if (l6PCArrAmt > 0) {
								lArrDtlMap.put(lArrObj[0] + "~6PC", l6PCArrAmt);
							}
						} else if (bundleCaseConst.getString("ARREARTYPE.COMPNSN").equals(lArrObj[1])) {
							lCommutationArrAmt = Double.valueOf(lArrObj[2].toString());
							if (lCommutationArrAmt > 0) {
								lArrDtlMap.put(lArrObj[0] + "~COMM", lCommutationArrAmt);
							}
						} else if (bundleCaseConst.getString("ARREARTYPE.OTHERDIFF").equals(lArrObj[1])) {
							lOthrDiffArrAmt = Double.valueOf(lArrObj[2].toString());
							if (lOthrDiffArrAmt > 0) {
								lArrDtlMap.put(lArrObj[0] + "~OTHRDIFF", lOthrDiffArrAmt);
							}
						} else if (bundleCaseConst.getString("ARREARTYPE.PENSION").equals(lArrObj[1])) {
							lPnsnArrAmnt = Double.valueOf(lArrObj[2].toString());
							if (lPnsnArrAmnt > 0) {
								lArrDtlMap.put(lArrObj[0] + "~PENSION", lPnsnArrAmnt);
							}
						}
					}
				}
			}
			inputMap.put("lArrDtlMap", lArrDtlMap);
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	public List getCVPDCRGConsolidatedData(String lStrScheme, String lStrBrnchCode, Map<String, Object> inputMap, char lFlag) throws Exception {

		ArrayList lArrDtls = new ArrayList();
		ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");
		bundleConst.getString("RECOVERY.DCRG");
		bundleConst.getString("RECOVERY.CVP");
		String lStrAuditor = null;
		StringBuffer sbLog = new StringBuffer();
		if (inputMap.containsKey("lAuditor")) {
			lStrAuditor = inputMap.get("lAuditor").toString();
		}

		try {
			Session hibSession = getSession();

			StringBuilder lSBQuery = new StringBuilder();
			String lStrCaseStatus = (String) inputMap.get("lStrCaseStatus");

			/*
			 * select trh.pension_request_id pnsnRqstId, rcb.bill_type bilType,
			 * trh.head_code hdCode, rcb.ppo_no ppoNo, trh.pensioner_code
			 * pnsnCode, sum(rcb.pay_amount) payAmnt, mh.pnsnr_prefix
			 * pnsnPrefix, mh.first_name firstName, mh.middle_name midName,
			 * mh.last_name lstName, md.acount_no accNo, sum(trc.amount) lAmnt,
			 * md.branch_code, trc.trn_pension_recovery_dtls_id from
			 * mst_pensioner_hdr mh, mst_pensioner_dtls md, rlt_pensioncase_bill
			 * rcb join trn_pension_rqst_hdr trh on trh.ppo_no = rcb.ppo_no join
			 * Valid_Pcode_View vp on vp.scheme_type = 'IRLA' and vp.case_status
			 * = trh.case_status AND vp.lst_Act_Post_Id = 104506 and
			 * vp.branch_Code in (12029,12028) AND vp.location_Code = 10055 and
			 * trh.pensioner_code = vp.pensioner_code left join
			 * trn_pension_recovery_dtls trc on trh.pension_request_id =
			 * trc.pension_request_id and trc.recovery_from_flag = rcb.bill_type
			 * where trh.case_status = 'APPROVED' and rcb.status = 'Y' and
			 * rcb.bill_no is null and rcb.bill_type in ('CVP', 'DCRG') and
			 * rcb.pay_mode = 'M' and trh.pensioner_code = mh.pensioner_code and
			 * trh.case_status = mh.case_status and trh.pensioner_code =
			 * md.pensioner_code and trh.case_status = md.case_status group by
			 * trh.pension_request_id, rcb.ppo_no, rcb.bill_type, trh.head_code,
			 * trh.pensioner_code, mh.pnsnr_prefix, mh.first_name,
			 * mh.middle_name, mh.last_name, md.acount_no, md.branch_code order
			 * by md.branch_code,rcb.bill_type, trh.head_code, rcb.ppo_no;
			 */

			lSBQuery.append(" select trh.pension_request_id pnsnRqstId, rcb.bill_type bilType, trh.head_code hdCode,  "
					+ " rcb.ppo_no ppoNo, trh.pensioner_code pnsnCode,sum(rcb.pay_amount) payAmnt, "
					+ " mh.pnsnr_prefix pnsnPrefix,mh.first_name firstName,mh.middle_name midName,mh.last_name lstName, "
					+ " md.acount_no accNo, sum(trc.amount) lAmnt,md.branch_code brncCode,trc.trn_pension_recovery_dtls_id recId"
					+ " from mst_pensioner_hdr mh, mst_pensioner_dtls md, rlt_pensioncase_bill rcb " + " join trn_pension_rqst_hdr trh on trh.ppo_no = rcb.ppo_no " + " join " + lStrMonthlyView
					+ " vp on vp.scheme_type= :lScheme and vp.case_status = trh.case_status " + " and vp.SEEN_FLAG = 'Y' ");

			// if("B".equals(inputMap.get("lFlag").toString()))
			lSBQuery.append("AND vp.lst_Act_Post_Id = :lPostId ");
			// else if("R".equals(inputMap.get("lFlag").toString()))
			// lSBQuery.append("AND vp.lst_Act_Post_Id = vp.post_Id ");

			if ("IRLA".equals(lStrScheme)) {
				if (lFlag == 'B') {
					lSBQuery.append(" and vp.branch_Code = :brnchCode ");
				}
			}

			if (inputMap.containsKey("NonBilledBanks")) {
				if (((List) inputMap.get("NonBilledBanks")).size() != 0) {
					lSBQuery.append("and vp.branch_Code  in (:NonBilledBanks) ");
				} else if (!"".equals(lStrBrnchCode) && !"-1".equals(lStrBrnchCode)) {
					lSBQuery.append("and vp.branch_Code = :brnchCode ");
				}

				// lSBQuery.append("and vp.branch_Code  in (:NonBilledBanks) ");
			}

			if (!inputMap.get("PPONoPara").equals("") && (lStrScheme.equals("RUBARU") || lStrScheme.equals("MONEY ORDER"))) {
				lSBQuery.append("AND vp.ppo_No = :ppoNo ");
			}

			/*
			 * if(inputMap.containsKey("BilledBranches") &&
			 * ((List)inputMap.get("BilledBranches")).size()!=0) {
			 * lSBQuery.append("and vp.branch_Code not in (:billedBranches) ");
			 * }
			 */

			lSBQuery.append("AND vp.location_Code= :locationCode and vp.location_Code=trh.location_Code ");
			lSBQuery.append(" and trh.pensioner_code = vp.pensioner_code left join trn_pension_recovery_dtls trc "
					+ "on trh.pensioner_code = trc.pensioner_code and trc.recovery_from_flag = rcb.bill_type and trc.bill_no is null "
					+ "where trh.case_status = :caseStatus and rcb.status = :status and rcb.bill_no is null and rcb.bill_type " + "in (:billType1,:billType2) and rcb.pay_mode = :payMode ");
			lSBQuery.append("and trh.pensioner_code = mh.pensioner_code and trh.case_status = mh.case_status and "
					+ "trh.pensioner_code = md.pensioner_code and trh.case_status = md.case_status AND md.active_flag = 'Y' "
					+ "group by trh.pension_request_id,rcb.ppo_no,rcb.bill_type, trh.head_code,trh.pensioner_code, "
					+ "mh.pnsnr_prefix, mh.first_name, mh.middle_name, mh.last_name, md.acount_no, md.branch_code, " + "trc.trn_pension_recovery_dtls_id "
					+ "order by md.branch_code,rcb.bill_type, trh.head_code, rcb.ppo_no ");

			SQLQuery lQuery = hibSession.createSQLQuery(lSBQuery.toString());

			lQuery.addScalar("pnsnRqstId", Hibernate.LONG);
			lQuery.addScalar("bilType", Hibernate.STRING);
			lQuery.addScalar("hdCode", Hibernate.LONG);
			lQuery.addScalar("ppoNo", Hibernate.STRING);
			lQuery.addScalar("pnsnCode", Hibernate.STRING);
			lQuery.addScalar("payAmnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("pnsnPrefix", Hibernate.STRING);
			lQuery.addScalar("firstName", Hibernate.STRING);
			lQuery.addScalar("midName", Hibernate.STRING);
			lQuery.addScalar("lstName", Hibernate.STRING);
			lQuery.addScalar("accNo", Hibernate.STRING);
			lQuery.addScalar("lAmnt", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("brncCode", Hibernate.STRING);
			lQuery.addScalar("recId", Hibernate.LONG);

			lQuery.setString("caseStatus", lStrCaseStatus);
			lQuery.setString("status", "Y");
			lQuery.setString("billType1", "CVP");
			lQuery.setString("billType2", "DCRG");
			lQuery.setString("payMode", "M");
			if ("IRLA".equals(lStrScheme) && lFlag == 'B') {
				lQuery.setString("brnchCode", lStrBrnchCode);
			}

			if (inputMap.containsKey("NonBilledBanks")) {
				if (((List) inputMap.get("NonBilledBanks")).size() != 0) {
					lQuery.setParameterList("NonBilledBanks", (List) inputMap.get("NonBilledBanks"));
				} else if (!"".equals(lStrBrnchCode) && !"-1".equals(lStrBrnchCode)) {
					lQuery.setParameter("brnchCode", lStrBrnchCode);
				}

			}

			lQuery.setParameter("lScheme", lStrScheme);
			if (lFlag == 'B') {
				lQuery.setParameter("lPostId", SessionHelper.getPostId(inputMap).toString());
			} else {
				lQuery.setParameter("lPostId", lStrAuditor);
			}
			if (!inputMap.get("PPONoPara").equals("") && (lStrScheme.equals("RUBARU") || lStrScheme.equals("MONEY ORDER"))) {
				lQuery.setParameter("ppoNo", inputMap.get("PPONoPara").toString());
			}

			lQuery.setParameter("locationCode", SessionHelper.getLocationCode(inputMap).toString());

			List listPnsnerCode = lQuery.list();

			String lStrName = null;
			if (listPnsnerCode != null && !listPnsnerCode.isEmpty()) {
				Iterator it = listPnsnerCode.iterator();
				while (it.hasNext()) {
					lStrName = "";
					Object[] tuple = (Object[]) it.next();
					lArrDtls.add(tuple[0]); // pension request id
					lArrDtls.add(tuple[1]); // billtype
					lArrDtls.add(tuple[2]); // headcode
					lArrDtls.add(tuple[3]); // ppo no
					lArrDtls.add(tuple[4]); // pensioner code
					lArrDtls.add(tuple[5]); // pay amount
					if (tuple[6] != null) {
						lStrName = lStrName + " " + tuple[6].toString();
					}
					if (tuple[7] != null) {
						lStrName = lStrName + " " + tuple[7].toString();
					}
					if (tuple[8] != null) {
						lStrName = lStrName + tuple[8].toString();
					}
					if (tuple[9] != null) {
						lStrName = lStrName + " " + tuple[9].toString();
					}
					lArrDtls.add(lStrName); // name
					lArrDtls.add(tuple[10]); // account no
					lArrDtls.add(tuple[11]); // recovery amt
					lArrDtls.add(tuple[12]); // branchCode
					lArrDtls.add(tuple[13]); // recoveryId
				}
			}
		} catch (Exception e) {
			sbLog.append("CVPDCRG consolidation Query threw error for branch " + lStrBrnchCode + "locatiion" + SessionHelper.getLocationCode(inputMap));
			sbLog.append(getStackTrace(e));
			try {
				new MonthlyLogger(inputMap).writeMonthlyLog(sbLog.toString());
			} catch (Exception ex) {
				logger.error("Error while writing Monthly log..." + ex, ex);
				throw (ex);
			}
			throw (e);
		}
		return lArrDtls;
	}

	/**
	 * Put pensionRequestId<Key>,TrnPensionRqstHdrVO<Value> in inputMap
	 * 
	 * @param inputMap
	 * @throws Exception
	 */
	public void getTrnPensionRqstHdrList(Map<String, Object> inputMap) throws Exception {

		List resultList = null;
		HashMap<Long, TrnPensionRqstHdr> lMapTrnPensionRqstHdr = new HashMap<Long, TrnPensionRqstHdr>();
		try {
			Session ghibSession = getSession();
			StringBuilder strQuery = new StringBuilder();
			List lPnsnrRqstIdLst = (List) inputMap.get("StrLstPnsnRqstId");

			if (lPnsnrRqstIdLst != null && !lPnsnrRqstIdLst.isEmpty()) {
				strQuery.append(" FROM TrnPensionRqstHdr where ");

				int flg = 0;
				for (int i = 0; i < lPnsnrRqstIdLst.size(); i++) {
					if (flg == 1) {
						strQuery.append(" or ");
					}
					strQuery.append("pensionRequestId in (" + lPnsnrRqstIdLst.get(i) + ")");
					flg = 1;
				}
				strQuery.append(") ");
			} else {
				return;
			}

			Query hqlQuery = ghibSession.createQuery(strQuery.toString());

			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				for (Object lObj : resultList) {
					lMapTrnPensionRqstHdr.put(((TrnPensionRqstHdr) lObj).getPensionRequestId(), (TrnPensionRqstHdr) lObj);
				}
			}
			inputMap.put("PensionRqstHdrList", lMapTrnPensionRqstHdr);
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
	}

	public Double getAppliedDP2004DiffAmt(String lStrPPONo, int lPhase1, int lPhase2, int lPhase3, String locationCode) throws Exception {

		Double lDiffValue = 0d;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append(" select (((pr.basic_pension_amount-pr.cvp_monthly_amount)+(round(pr.basic_pension_amount*.50))+(round(pr.basic_pension_amount*1.5*0.11)))-((pr.basic_pension_amount-pr.cvp_monthly_amount)+(round(pr.basic_pension_amount*.61)))) * "
					+ lPhase1);

			if (lPhase2 > 0) {
				lSBQuery.append(" +(((pr.basic_pension_amount-pr.cvp_monthly_amount)+(round(pr.basic_pension_amount*.50))+(round(pr.basic_pension_amount*1.5*0.14)))-((pr.basic_pension_amount-pr.cvp_monthly_amount)+(round(pr.basic_pension_amount*.64)))) * "
						+ lPhase2);
			}

			if (lPhase3 > 0) {
				lSBQuery.append(" +(((pr.basic_pension_amount-pr.cvp_monthly_amount)+(round(pr.basic_pension_amount*.50))+(round(pr.basic_pension_amount*1.5*0.17)))-((pr.basic_pension_amount-pr.cvp_monthly_amount)+(round(pr.basic_pension_amount*.67)))) * "
						+ lPhase3);
			}

			lSBQuery.append(" lAmnt from trn_pension_rqst_hdr pr where pr.head_code in (1,2,3,4,5,6,7,11,12,13,14,21,22) " + " and pr.scheme_type = :lScheme and pr.commension_date < :lCommDate "
					+ " AND pr.case_status = :lStatus " + " and pr.ppo_no = :lPPONo and  pr.location_code=:locationCode");
			if (lPhase1 == 0 && lPhase2 == 0 && lPhase3 == 0) {
				lSBQuery.append(" and 1 = 2 ");
			}

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("lScheme", "IRLA");
			lQuery.setString("lPPONo", lStrPPONo);
			lQuery.setString("lStatus", "APPROVED");
			lQuery.setDate("lCommDate", new SimpleDateFormat("dd-mm-yyyy").parse("01-04-2005"));

			lQuery.setString("locationCode", locationCode);
			lQuery.addScalar("lAmnt", Hibernate.BIG_DECIMAL);

			List lLst = lQuery.list();

			if (lLst != null && !lLst.isEmpty() && lLst.get(0) != null) {
				lDiffValue = Double.valueOf(lLst.get(0).toString());
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lDiffValue;
	}

	public void updateRecoveryDtls(Long billNo, List<Long> lLstRecList, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		try {
			Session hibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" update trn_pension_recovery_dtls " + " set bill_no = :lBillNo,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date "
					+ " where recovery_from_flag in ('CVP','DCRG') " + " and trn_pension_recovery_dtls_id in (:lLstRecList )" + " and bill_no is null ");

			Query lQuery = hibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("lBillNo", billNo);
			lQuery.setParameterList("lLstRecList", lLstRecList);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is: " + e, e);
			throw (e);
		}
	}

	public void updateRqstHdrOmrFlg(Long billNo, List<String> lLstOmrPenCode, String gStrLocCode, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		try {
			Session hibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" UPDATE trn_pension_rqst_hdr SET omr_type = 'P',updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date "
					+ " WHERE pensioner_code IN (:lLstOmrPenCode) AND location_code = :locCode ");

			Query lQuery = hibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameterList("lLstOmrPenCode", lLstOmrPenCode);
			lQuery.setString("locCode", gStrLocCode);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);

			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is: " + e, e);
			throw (e);
		}
	}

	public List getBranchDetailsByBranchId(String branchCode, String locCode, Long postId, Long userId, Long lLngLangId) throws Exception {

		StringBuilder strQuery = new StringBuilder();
		List resultList = new ArrayList();
		List<String> lLstDtlsList = new ArrayList<String>();
		Iterator it;

		try {
			Session ghibSession = getSession();
			strQuery.append(" SELECT oem.emp_prefix PREF," + "  oem.emp_fname FNAME,oem.emp_mname MNAME,oem.emp_lname LNAME,rab.post_Id postId" + " FROM Rlt_Bank_Branch  rb, "
					+ " Rlt_Auditor_Bank rab, Org_Emp_Mst      oem,org_userpost_rlt upr WHERE upr.post_id = "
					+ " rab.post_id and upr.user_id = oem.user_id and rab.bill_Type IS NULL AND rab.location_Code = :locationCode AND "
					+ " rb.location_Code = rab.location_Code AND rb.branch_Code = :branchCode AND " + " rb.location_Code = :locationCode AND rab.branch_Code = :branchCode AND "
					+ " rab.bank_Code = rb.bank_Code AND oem.lang_Id = :LangId AND " + " oem.activate_Flag = 1 AND oem.activate_Flag = upr.activate_flag ");

			SQLQuery hqlQuery = ghibSession.createSQLQuery(strQuery.toString());

			hqlQuery.addScalar("PREF", Hibernate.STRING);
			hqlQuery.addScalar("FNAME", Hibernate.STRING);
			hqlQuery.addScalar("MNAME", Hibernate.STRING);
			hqlQuery.addScalar("LNAME", Hibernate.STRING);
			hqlQuery.addScalar("postId", Hibernate.LONG);

			hqlQuery.setLong("branchCode", Long.parseLong(branchCode));
			hqlQuery.setParameter("locationCode", locCode);
			hqlQuery.setLong("LangId", lLngLangId);
			resultList = hqlQuery.list();
			if (resultList != null && !resultList.isEmpty()) {
				it = resultList.iterator();
				while (it.hasNext()) {
					Object tuple[] = (Object[]) it.next();

					StringBuffer lStrAuditorName = new StringBuffer();
					lStrAuditorName.append(tuple[0] != null ? tuple[0] + " " : "");
					lStrAuditorName.append(tuple[1] != null ? tuple[1] + " " : "");
					lStrAuditorName.append(tuple[2] != null ? tuple[2] + " " : "");
					lStrAuditorName.append(tuple[3] != null ? tuple[3] : "");

					lLstDtlsList.add(lStrAuditorName.toString() + "~" + tuple[4].toString());
				}
			}
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw (e);
		}
		return lLstDtlsList;
	}

	public void getROPAppliedToPensner(Map inputMap) throws Exception {

		/*
		 * select distinct rop.PNSNCASE_ROP_RLT_ID, rop.PPO_NO, rop.ROP,
		 * rop.ADAPTED_TYPE, rop.CREATED_USER_ID, rop.CREATED_POST_ID,
		 * rop.CREATED_DATE, rop.UPDATED_USER_ID, rop.UPDATED_POST_ID,
		 * rop.UPDATED_DATE, rop.ROP_PAID from TRN_PNSNCASE_ROP_RLT rop
		 * ,VALID_PCODE_VIEW vp where rop.PPO_NO = vp.PPO_NO and vp.SCHEME_TYPE
		 * = 'IRLA' and vp.LST_ACT_POST_ID = 104505 and vp.BRANCH_CODE = 12001
		 * and vp.LOCATION_CODE = '10055' order by rop.PPO_NO, rop.ROP
		 */

		String lStrBrnchCode = (String) inputMap.get("BranchCode");
		String lAuditor = "";
		String lStrScheme = (String) inputMap.get("Scheme");
		List<TrnPnsncaseRopRlt> lPnsnerCaseRopLst = null;
		Map lMapTrnPnsnRopRlt = new HashMap<String, List>();
		String tempPPO = null;
		TrnPnsncaseRopRlt lObjRopRlt = null;
		if ("R".equals(inputMap.get("lFlag").toString())) {
			lAuditor = (String) inputMap.get("lAuditor");
		}

		try {
			Session ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select distinct rp.pnsncase_Rop_Rlt_Id,rp.ppo_No,rp.rop,rp.adapted_Type,rp.rop_Paid " + " from Trn_Pnsncase_Rop_Rlt rp , " + lStrMonthlyView
					+ " vp where rp.ppo_No = vp.ppo_No and vp.scheme_Type =:lScheme " + " and vp.lst_Act_Post_Id = :lPostId and vp.case_Status = :lCaseStatus ");

			if ("IRLA".equals(lStrScheme)) {
				if ("B".equals(inputMap.get("lFlag").toString())) {
					lSBQuery.append(" and vp.branch_Code = :brnchCode ");
				}

				if ("R".equals(inputMap.get("lFlag").toString()) && !"".equals(lStrBrnchCode) && !"-1".equals(lStrBrnchCode)) {
					lSBQuery.append("AND vp.branch_Code= :brnchCode ");
				}
			}
			if (inputMap.containsKey("NonBilledBanks") && ((List) inputMap.get("NonBilledBanks")).size() != 0) {
				lSBQuery.append("and vp.branch_Code  in (:NonBilledBanks) ");
			}

			if (!inputMap.get("PPONoPara").equals("") && (lStrScheme.equals("RUBARU") || lStrScheme.equals("MONEY ORDER"))) {
				lSBQuery.append("AND vp.ppo_No = :ppoNo ");
			}

			lSBQuery.append("AND vp.location_Code= :locationCode ");

			lSBQuery.append(" ORDER BY rp.ppo_No ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("lScheme", lStrScheme);
			if ("B".equals(inputMap.get("lFlag").toString())) {
				lQuery.setString("lPostId", SessionHelper.getPostId(inputMap).toString());
			} else if ("R".equals(inputMap.get("lFlag").toString())) {
				lQuery.setString("lPostId", lAuditor);
			}

			if ("IRLA".equals(lStrScheme)) {
				if ("B".equals(inputMap.get("lFlag").toString())) {
					lQuery.setString("brnchCode", lStrBrnchCode);
				}
				if ("R".equals(inputMap.get("lFlag").toString()) && !"".equals(lStrBrnchCode) && !"-1".equals(lStrBrnchCode)) {
					lQuery.setString("brnchCode", lStrBrnchCode);
				}
			}
			if (!inputMap.get("PPONoPara").equals("") && (lStrScheme.equals("RUBARU") || lStrScheme.equals("MONEY ORDER"))) {
				lQuery.setParameter("ppoNo", inputMap.get("PPONoPara").toString());
			}

			lQuery.setString("locationCode", SessionHelper.getLocationCode(inputMap).toString());
			lQuery.setString("lCaseStatus", "APPROVED");
			if (inputMap.containsKey("NonBilledBanks") && ((List) inputMap.get("NonBilledBanks")).size() != 0) {
				lQuery.setParameterList("NonBilledBanks", (List) inputMap.get("NonBilledBanks"));
			}

			List lLstVO = lQuery.list();
			if (!lLstVO.isEmpty()) {
				lPnsnerCaseRopLst = new ArrayList<TrnPnsncaseRopRlt>();

				Iterator itr = lLstVO.iterator();
				while (itr.hasNext()) {
					lObjRopRlt = new TrnPnsncaseRopRlt();
					Object[] tuple = (Object[]) itr.next();
					if (tempPPO != null && !tuple[1].toString().equals(tempPPO)) {
						lMapTrnPnsnRopRlt.put("ROP_" + tempPPO, lPnsnerCaseRopLst);
						lPnsnerCaseRopLst = null;
						lPnsnerCaseRopLst = new ArrayList<TrnPnsncaseRopRlt>();
					}
					tempPPO = tuple[1].toString();

					lObjRopRlt.setPnsncaseRopRltId(new Long((tuple[0]).toString()));
					lObjRopRlt.setPpoNo(tuple[1] != null ? tuple[1].toString() : "");
					lObjRopRlt.setRop(tuple[2] != null ? tuple[2].toString() : "");
					lObjRopRlt.setAdaptedType(tuple[3] != null ? tuple[3].toString() : "");
					lObjRopRlt.setRopPaid(tuple[4] != null ? tuple[4].toString() : "");
					lPnsnerCaseRopLst.add(lObjRopRlt);
				}
				lMapTrnPnsnRopRlt.put("ROP_" + tempPPO, lPnsnerCaseRopLst);
				inputMap.put("MapTrnPnsnRopRlt", lMapTrnPnsnRopRlt);
			}
			/*
			 * if(!lLstVO.isEmpty()) { lPnsnerCaseRopLst = new
			 * ArrayList<TrnPnsncaseRopRlt>(); for(Object lObj : lLstVO) {
			 * if(tempPPO != null &&
			 * !((TrnPnsncaseRopRlt)lObj).getPpoNo().equals(tempPPO.toString()))
			 * { lMapTrnPnsnRopRlt.put("ROP_"+tempPPO, lPnsnerCaseRopLst);
			 * lPnsnerCaseRopLst = null; lPnsnerCaseRopLst = new
			 * ArrayList<TrnPnsncaseRopRlt>(); } tempPPO = new
			 * StringBuffer(((TrnPnsncaseRopRlt)lObj).getPpoNo());
			 * lPnsnerCaseRopLst.add((TrnPnsncaseRopRlt)lObj); }
			 * lMapTrnPnsnRopRlt.put("ROP_"+tempPPO, lPnsnerCaseRopLst);
			 * inputMap.put("MapTrnPnsnRopRlt", lMapTrnPnsnRopRlt); }
			 */
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
	}

	/**
	 * Method to insert/save a record in RltPensioncaseBill
	 * 
	 * @author Astha Notani
	 * @param RltPensioncaseBill
	 *            , Map
	 * @return void
	 */
	public void updateRltPensioncaseBill(Long billNo, String billCntrlNo, Long tokenNo, String lLstPpoNos, String lStrLocCd, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		/*
		 * update rlt_pensioncase_bill pcb set pcb.bill_cntrl_no = '' and
		 * pcb.bill_no = '' and pcb.token_no = '' where pcb.pay_mode = 'M' and
		 * pcb.status = 'Y' and pcb.bill_no is null and pcb.ppo_no =
		 * 'DPP/P/033042'
		 */
		try {
			Session ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" update rlt_pensioncase_bill set bill_cntrl_no = :billCntrlNo, "
					+ " bill_no = :billNo,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date ");

			if (tokenNo != null && tokenNo.toString().length() > 0) {
				lSBQuery.append(" ,token_no = :tokenNo ");
			}

			lSBQuery.append(" where pay_mode = :payMode and " + " status = :status and bill_no is null and ppo_no in (" + lLstPpoNos + ") and location_code = :location_code ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("billCntrlNo", billCntrlNo);
			lQuery.setLong("billNo", billNo);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			if (tokenNo != null && tokenNo.toString().length() > 0) {
				lQuery.setLong("tokenNo", tokenNo);
			}
			lQuery.setString("payMode", "M");
			lQuery.setString("status", "Y");
			lQuery.setString("location_code", lStrLocCd);

			// lQuery.setString("lLstPpoNos","(" +lLstPpoNos+ ")");

			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw (e);
		}
		/*
		 * ServiceLocator serv = (ServiceLocator)
		 * inputMap.get("serviceLocator"); RltPensioncaseBill lObjPenCaseBillVO
		 * = null; try { RltPensioncaseBillDAO lObjPensionCaseBillDAO = new
		 * RltPensioncaseBillDAOImpl(RltPensioncaseBill.class,
		 * serv.getSessionFactory()); for(Long
		 * lPensioncaseBillId:lLstRltPenCaseBillIds) { lObjPenCaseBillVO =
		 * lObjPensionCaseBillDAO.read(lPensioncaseBillId);
		 * lObjPenCaseBillVO.setBillCntrlNo(billCntrlNo);
		 * lObjPenCaseBillVO.setBillNo(new BigDecimal(billNo)); if(tokenNo !=
		 * null && tokenNo.toString().length() > 0) {
		 * lObjPenCaseBillVO.setTokenNo(tokenNo); }
		 * lObjPensionCaseBillDAO.update(lObjPenCaseBillVO); } } catch
		 * (Exception e) { gLogger.error(" Error is : " + e, e); throw(e); }
		 */
	}

	/**
	 * Method to fetch records for whom pension has been closed
	 * 
	 * @author Astha Notani
	 * @return list
	 */

	public List getMonthlyClosed(Long lstPaidMnth, String lbrnchCode, Date lstPdMnth) throws Exception {

		List lLst = null;
		// String lStrCaseStatus = (String) inputMap.get("lStrCaseStatus");
		try {
			ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();
			/*
			 * select dt.pensioner_code from trn_pension_bill_dtls dt,
			 * trn_pension_bill_hdr hd where
			 * dt.trn_pension_bill_hdr_id=hd.trn_pension_bill_hdr_id and
			 * hd.bill_type='Monthly' and hd.branch_code = 12002 and
			 * hd.for_month = 200812 and dt.pensioner_code not in (select
			 * validpcode0_.PENSIONER_CODE from valid_pcode_view validpcode0_
			 * where validpcode0_.SCHEME_TYPE = 'IRLA' and
			 * validpcode0_.CASE_STATUS = 'APPROVED' and
			 * validpcode0_.BRANCH_CODE = 12002 and validpcode0_.PAY_FOR_MONTH =
			 * 200812 and validpcode0_.LST_ACT_POST_ID = validpcode0_.POST_ID)
			 */

			lSBQuery.append("select dt.pensioner_code from trn_pension_bill_dtls dt,trn_pension_bill_hdr hd "
					+ "where dt.trn_pension_bill_hdr_id=hd.trn_pension_bill_hdr_id and hd.bill_type=:billType and " + "hd.branch_code = :brnchCode and hd.for_month = :forMonth and "
					+ "dt.pensioner_code not in (select vp.PENSIONER_CODE from " + lStrMonthlyView
					+
					// " vp " +
					" vp where vp.SCHEME_TYPE = :lScheme and vp.CASE_STATUS = :caseStatus and " + "vp.BRANCH_CODE = :brnchCode and vp.last_Paid_Date between :lstMonth and :lstMonthEnd and "
					+ "vp.LST_ACT_POST_ID = vp.POST_ID) ");

			Calendar cal = Calendar.getInstance();
			cal.setTime(lstPdMnth);
			Integer lstDay = cal.getActualMaximum(cal.DAY_OF_MONTH);
			Date lstMnthEnd = new SimpleDateFormat("dd/MM/yyyy").parse((lstDay.toString() + "/").concat((new SimpleDateFormat("MM/yyyy").format(lstPdMnth))));

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("billType", "Monthly");
			lQuery.setString("brnchCode", lbrnchCode);
			lQuery.setLong("forMonth", lstPaidMnth);
			lQuery.setString("lScheme", "IRLA");
			lQuery.setString("caseStatus", "APPROVED");
			lQuery.setDate("lstMonth", lstPdMnth);
			lQuery.setDate("lstMonthEnd", lstMnthEnd);

			lLst = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw (e);
		}
		return lLst;
	}

	public List getOldBasicSixPayNewBasic(String lStrPnsnrCode) throws Exception {

		List lResLst = null;

		try {
			Session ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT c.field_type,c.prvs_field_value,c.crnt_field_value FROM trn_pension_correction_dtls c "
					+ " WHERE (c.field_type = 'FP1 Amount 2006' OR c.field_type = 'FP2 Amount 2006' OR c.field_type = 'Basic Pension 2006') " + " AND c.pensioner_code = :lPnsnrCode ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("lPnsnrCode", lStrPnsnrCode);

			lResLst = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw (e);
		}

		return lResLst;
	}

	public List getBrnchCodeForPPO(String lStrPPONo) throws Exception {

		List lResLst = null;
		try {
			Session ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select v.BRANCH_CODE from valid_pcode_view v where v.PPO_NO= :lPpoNo ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("lPpoNo", lStrPPONo);
			lResLst = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw (e);
		}

		return lResLst;

	}

	public String getChqNoFrmBil(List<Long> lLstBillNo) throws Exception {

		List lResLst = null;
		String lStrChqNo = "";
		try {
			Session ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" select cd.cheque_no from rlt_bill_cheque rc,trn_cheque_dtls cd where rc.bill_no in ( :lBillNo ) " + " and cd.cheque_id = rc.cheque_id");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setParameterList("lBillNo", lLstBillNo);
			lResLst = lQuery.list();

			if (lResLst != null && !lResLst.isEmpty() && lResLst.get(0) != null) {
				for (Object lObj : lResLst) {
					if (lStrChqNo.equals("")) {
						lStrChqNo += lObj.toString();
					} else {
						lStrChqNo += "," + lObj.toString();
					}
				}

			}
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw (e);
		}

		return lStrChqNo;
	}

	/*
	 * public List getAuditorBranches(String lStrDate,BigDecimal
	 * lBDAuditor,String lStrBank,String gStrLocCode,String lStrScheme) throws
	 * Exception { List lResLst = null;
	 * 
	 * select distinct ra.branch_code from Rlt_Auditor_Bank
	 * ra,trn_pension_bill_hdr hd where ra.post_id=104505 and hd.bill_type =
	 * 'Monthly' and hd.bill_no is not null and hd.for_month = 200905 and
	 * hd.branch_code = ra.branch_code and ra.location_code = hd.location_code
	 * and hd.scheme = 'IRLA' and hd.location_code=10055 and hd.created_post_id
	 * = ra.post_id and hd.bank_code = ra.bank_code and ra.bill_type is null and
	 * ra.pension_scheme = hd.scheme and ra.bank_code= 12000
	 * 
	 * try { Session ghibSession = getSession(); StringBuilder lSBQuery = new
	 * StringBuilder();
	 * 
	 * lSBQuery.append(
	 * "select distinct ra.branchCode from RltAuditorBank ra,TrnPensionBillHdr hd where ra.postId= :postId "
	 * + "and hd.billType = :billType and hd.branchCode = " +
	 * "ra.branchCode and ra.locationCode = hd.locationCode and hd.scheme = :lScheme and hd.locationCode=:locCode and "
	 * +
	 * "hd.createdPostId = ra.postId and hd.bankCode = ra.bankCode and ra.billType is null and "
	 * + "ra.pensionScheme = hd.scheme " ); if(!"-1".equals(lStrBank)) {
	 * lSBQuery.append("and ra.bankCode= :BankCode" ); }
	 * 
	 * Query lQuery = ghibSession.createQuery(lSBQuery.toString());
	 * 
	 * lQuery.setLong("postId",lBDAuditor);
	 * lQuery.setString("billType","Monthly");
	 * lQuery.setString("lScheme","IRLA");
	 * lQuery.setString("locCode",gStrLocCode); if(!"-1".equals(lStrBank)) {
	 * lQuery.setString("BankCode",lStrBank); }
	 * 
	 * lResLst = lQuery.list(); } catch(Exception e) { logger.error("Error is:"
	 * + e, e); throw(e); }
	 * 
	 * return lResLst; }
	 */

	public void getLastPaidMap(Map inputMap) throws Exception {

		/*
		 * SELECT SQL_NO_CACHE vp.PENSIONER_CODE, MAX(hd.for_month) FROM
		 * VALID_PCODE_VIEW vp, Trn_Pension_Bill_Dtls dt, Trn_Pension_Bill_Hdr
		 * hd WHERE vp.LOCATION_CODE = '10055' AND vp.POST_ID = '104505' AND
		 * (hd.bill_type = 'PENSION' OR hd.bill_type = 'Monthly') AND
		 * vp.SCHEME_TYPE = 'IRLA' AND vp.CASE_STATUS = 'APPROVED' AND
		 * hd.rejected = 0 AND vp.LAST_PAID_DATE IS NULL AND vp.location_code =
		 * hd.LOCATION_CODE AND vp.SCHEME_TYPE = hd.SCHEME AND vp.branch_code =
		 * hd.BRANCH_CODE AND vp.ppo_no = dt.PPO_NO AND
		 * dt.trn_Pension_Bill_Hdr_Id = hd.trn_Pension_Bill_Hdr_Id AND
		 * hd.for_month = dt.pay_for_month AND dt.PENSIONER_CODE =
		 * vp.PENSIONER_CODE GROUP BY vp.PENSIONER_CODE
		 */
		try {
			Session ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();
			List resultList = null;
			String lStrMonthly = bundleConst.getString("RECOVERY.MONTHLY");
			String lStrBrnchCode = (String) inputMap.get("BranchCode");
			String lStrScheme = (String) inputMap.get("Scheme");
			String lStrAuditor = null;
			Map lstPaidMap = null;
			String lStrCaseStatus = (String) inputMap.get("lStrCaseStatus");
			char lFlag = (Character) inputMap.get("lFlag");
			if (inputMap.containsKey("lAuditor")) {
				lStrAuditor = inputMap.get("lAuditor").toString();
			}

			lSBQuery.append(" SELECT vp.PENSIONER_CODE pCode, MAX(hd.for_month) forMonth " + "     FROM " + lStrMonthlyView + "          vp, Trn_Pension_Bill_Dtls dt, "
					+ "          Trn_Pension_Bill_Hdr  hd " + " WHERE vp.LOCATION_CODE = :locationCode AND vp.lst_Act_Post_Id = :postId AND " + "       hd.bill_type in ( :lPension, :lMonthly) AND "
					+ "       vp.SCHEME_TYPE = :lScheme AND vp.CASE_STATUS = :lCaseStatus AND " + "       hd.rejected = 0 AND " + "       vp.location_code = hd.LOCATION_CODE AND "
					+ "       vp.SCHEME_TYPE = hd.SCHEME AND vp.branch_code = hd.BRANCH_CODE AND " + "       vp.ppo_no = dt.PPO_NO AND "
					+ "       dt.trn_Pension_Bill_Hdr_Id = hd.trn_Pension_Bill_Hdr_Id AND " + "       hd.for_month = dt.pay_for_month AND " + "       dt.PENSIONER_CODE = vp.PENSIONER_CODE ");

			if ("IRLA".equalsIgnoreCase(lStrScheme)) {
				if (lFlag == 'B') {
					lSBQuery.append("AND vp.branch_Code= :BrnchCode ");
				}
				if (lFlag == 'R' && !"".equals(lStrBrnchCode) && !"-1".equals(lStrBrnchCode)) {
					lSBQuery.append("AND vp.branch_Code= :BrnchCode ");
				}

			}
			if (inputMap.containsKey("NonBilledBanks") && ((List) inputMap.get("NonBilledBanks")).size() != 0) {
				lSBQuery.append("and vp.branch_Code  in (:NonBilledBanks) ");
			}
			if (!inputMap.get("PPONoPara").equals("") && (lStrScheme.equals("RUBARU") || lStrScheme.equals("MONEY ORDER"))) {
				lSBQuery.append("AND vp.ppo_No = :ppoNo ");
			}

			if (inputMap.containsKey("PnsnerCode") && inputMap.get("PnsnerCode") != null) {
				lSBQuery.append("and vp.pensioner_Code = :lPcode");
			}
			/*
			 * else { lSBQuery.append("and vp.LAST_PAID_DATE IS NULL "); }
			 */

			lSBQuery.append(" GROUP BY vp.PENSIONER_CODE ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.addScalar("pCode", Hibernate.BIG_DECIMAL);
			lQuery.addScalar("forMonth", Hibernate.INTEGER);

			lQuery.setString("lCaseStatus", lStrCaseStatus);
			lQuery.setString("lPension", "PENSION");
			lQuery.setString("lMonthly", lStrMonthly);
			lQuery.setString("lScheme", lStrScheme);
			lQuery.setString("locationCode", SessionHelper.getLocationCode(inputMap).toString());
			if (lFlag == 'B') {
				lQuery.setString("postId", SessionHelper.getPostId(inputMap).toString());
			}
			if (lFlag == 'R' && !"-1".equals(lStrAuditor)) {
				lQuery.setString("postId", lStrAuditor);
			}

			if ("IRLA".equalsIgnoreCase(lStrScheme)) {
				if (lFlag == 'B') {
					lQuery.setString("BrnchCode", lStrBrnchCode);
				}
				if (lFlag == 'R' && !"".equals(lStrBrnchCode) && !"-1".equals(lStrBrnchCode)) {
					lQuery.setString("BrnchCode", lStrBrnchCode);
				}
			}
			if (inputMap.containsKey("NonBilledBanks") && ((List) inputMap.get("NonBilledBanks")).size() != 0) {
				lQuery.setParameterList("NonBilledBanks", (List) inputMap.get("NonBilledBanks"));
			}
			if (!inputMap.get("PPONoPara").equals("") && (lStrScheme.equals("RUBARU") || lStrScheme.equals("MONEY ORDER"))) {
				lQuery.setString("ppoNo", inputMap.get("PPONoPara").toString());
			}

			if (inputMap.containsKey("PnsnerCode") && inputMap.get("PnsnerCode") != null) {
				lQuery.setString("lPcode", inputMap.get("PnsnerCode").toString());
			}

			resultList = lQuery.list();
			if (resultList != null && !resultList.isEmpty()) {
				lstPaidMap = new HashMap<String, Object>();

				for (Object lObj : resultList) {
					Object[] lArrObj = (Object[]) lObj;
					lstPaidMap.put(lArrObj[0].toString() + "~", lArrObj[1]);
				}
				inputMap.put("lstPaidMap", lstPaidMap);
			}
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw (e);
		}

	}

	public List getAuditorNonBilledBranches(String lStrDate, Long lLngAuditor, String lStrBank, String lStrLocCode, String lStrScheme) throws Exception {

		/*
		 * select rab.BRANCH_CODE, rbb.BRANCH_NAME from RLT_AUDITOR_BANK rab,
		 * rlt_bank_branch rbb where rab.BRANCH_CODE = rbb.BRANCH_CODE and
		 * rab.BANK_CODE = rbb.BANK_CODE and rab.POST_ID = 104505 and
		 * rab.BANK_CODE = 12000 and rbb.LOCATION_CODE = '10055' and
		 * rab.bill_type is null and not exists (select 1 from
		 * TRN_PENSION_BILL_HDR bh, TRN_PENSION_BILL_DTLS bd where bh.FOR_MONTH
		 * = 200908 and bh.SCHEME = 'IRLA' and bh.BILL_TYPE = 'Monthly' and
		 * bd.TRN_PENSION_BILL_HDR_ID = bh.TRN_PENSION_BILL_HDR_ID and
		 * bh.for_month = bd.pay_for_month and bh.location_code = '10055' and
		 * rab.BRANCH_CODE = bh.branch_code ) order by rab.BRANCH_CODE
		 */
		try {
			Session ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();
			StringBuilder lSBQuery1 = new StringBuilder();
			List<String> resultList = null;
			List<String> resultList1 = null;
			List<String> resultList2 = new ArrayList<String>();

			String lStrMonthly = bundleConst.getString("RECOVERY.MONTHLY");

			lSBQuery.append(" select rab.BRANCH_CODE from RLT_AUDITOR_BANK rab, rlt_bank_branch rbb "
					+ " where rab.BRANCH_CODE = rbb.BRANCH_CODE and rab.BANK_CODE = rbb.BANK_CODE and rab.POST_ID = :postId " + " and rab.LOCATION_CODE = rbb.LOCATION_CODE "
					+ " and rbb.LOCATION_CODE = :locationCode  and rab.bill_type is null  ");

			if (!"-1".equals(lStrBank)) {
				lSBQuery.append("and rab.bank_Code= :BankCode ");
			}
			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("locationCode", lStrLocCode);
			lQuery.setLong("postId", lLngAuditor);
			if (!"-1".equals(lStrBank)) {
				lQuery.setString("BankCode", lStrBank);
			}

			lSBQuery1.append(" select  distinct(bh.BRANCH_CODE) from " + " TRN_PENSION_BILL_HDR  bh, TRN_PENSION_BILL_DTLS bd "
					+ " where bh.FOR_MONTH = :forMonth  and bh.SCHEME = :lScheme and bh.BILL_TYPE = :lMonthly and bd.TRN_PENSION_BILL_HDR_ID = "
					+ " bh.TRN_PENSION_BILL_HDR_ID and bh.for_month = bd.pay_for_month and bh.location_code = :locationCode and " + " rejected = 0 and created_post_id = :postId ");

			if (!"-1".equals(lStrBank)) {
				lSBQuery1.append(" and bh.branch_code = :BankCode ");
			}
			SQLQuery lQuery1 = ghibSession.createSQLQuery(lSBQuery1.toString());

			lQuery1.setString("lMonthly", lStrMonthly);
			lQuery1.setString("locationCode", lStrLocCode);
			lQuery1.setString("lScheme", lStrScheme);
			lQuery1.setLong("postId", lLngAuditor);
			lQuery1.setBigDecimal("forMonth", new BigDecimal(lStrDate));
			if (!"-1".equals(lStrBank)) {
				lQuery1.setString("BankCode", lStrBank);
			}

			resultList = lQuery.list();
			resultList1 = lQuery1.list();
			for (String ltemp : resultList) {
				for (String ltemp1 : resultList1) {
					if (!(resultList1.contains(ltemp))) {
						resultList2.add(ltemp);
						break;

					}

				}
			}

			/*
			 * if(!ltemp.equals(ltemp1)) { flag = true; continue; } else { flag
			 * = false; }
			 * 
			 * } if(flag == false) { resultList2.add(ltemp.toString()); } flag =
			 * false;
			 * 
			 * }
			 */

			return resultList2;
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw (e);
		}
	}

	public List getAuditorBilledBranches(String lStrDate, Long lLngAuditor, String lStrBank, String gStrLocCode, String lStrScheme) throws Exception {

		/*
		 * select distinct bh.bill_no,bh.branch_code from trn_pension_bill_hdr
		 * bh where bh.FOR_MONTH = 200906 and bh.SCHEME = 'IRLA' and
		 * bh.BILL_TYPE = 'Monthly' and bh.created_post_id = '104505' and
		 * bh.location_code = '10055';
		 */
		try {
			Session ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();
			List resultList = null;
			String lStrMonthly = bundleConst.getString("RECOVERY.MONTHLY");

			lSBQuery.append(" select distinct bh.bill_no,bh.branch_code from trn_pension_bill_hdr  bh where bh.FOR_MONTH = :forMonth and "
					+ " bh.SCHEME = :lScheme and bh.BILL_TYPE = :lMonthly  and bh.created_post_id = :postId " + " and bh.location_code = :locationCode and bh.rejected = 0");

			if (!"".equals(lStrBank) && !"-1".equals(lStrBank)) {
				lSBQuery.append(" and bh.bank_Code= :BankCode");
			}
			lSBQuery.append(" order by bh.branch_code ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("lMonthly", lStrMonthly);
			lQuery.setString("locationCode", gStrLocCode);
			lQuery.setString("lScheme", lStrScheme);
			lQuery.setLong("postId", lLngAuditor);
			lQuery.setBigDecimal("forMonth", new BigDecimal(lStrDate));
			if (!"".equals(lStrBank) && !"-1".equals(lStrBank)) {
				lQuery.setString("BankCode", lStrBank);
			}

			resultList = lQuery.list();
			return resultList;
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw (e);
		}

	}

	public String getBilStatusFrmBillNo(String lstrBank, String lstrBranch, String lStrScheme, String lStrPPoNo, String lLocCode, Long lPostId, String calcDate) throws Exception {

		/*
		 * SELECT DISTINCT bl.curr_bill_status, tbh.FOR_MONTH FROM
		 * TRN_PENSION_BILL_HDR tbh,trn_bill_register bl, trn_pension_bill_dtls
		 * dt WHERE bl.bill_no = tbh.bill_no AND bl.location_code =
		 * tbh.location_code AND bl.location_code = '10055' AND
		 * bl.created_post_id = tbh.created_post_id AND bl.created_post_id =
		 * '104518' AND tbh.location_code = '10055' AND tbh.BRANCH_CODE =
		 * '18005' AND tbh.SCHEME = 'IRLA' AND (tbh.BILL_TYPE IN ('Monthly',
		 * 'CVP', 'DCRG')) AND dt.trn_pension_bill_hdr_id =
		 * tbh.trn_pension_bill_hdr_id AND dt.pay_for_month = tbh.for_month AND
		 * tbh.rejected != 1 AND bl.curr_bill_status > 0
		 */
		try {
			Session ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();
			List lLstResult = new ArrayList();
			String lStrBillApp = "";
			new ArrayList<String>();
			lSBQuery.append("    SELECT DISTINCT bl.curr_bill_status, tbh.FOR_MONTH FROM TRN_PENSION_BILL_HDR  tbh,trn_bill_register bl ");

			if (lStrPPoNo != null && !lStrPPoNo.equals("")) {
				lSBQuery.append(" ,trn_pension_bill_dtls dt ");
			}

			lSBQuery.append(" WHERE  bl.bill_no = tbh.bill_no AND bl.location_code = tbh.location_code AND bl.location_code = :locationCode  "
					+ " AND bl.created_post_id = tbh.created_post_id AND bl.created_post_id = :postId AND " + " tbh.SCHEME = :lScheme AND (tbh.BILL_TYPE IN (:lMonthly,:lCVP,:lDCRG)) AND "
					+ " tbh.rejected = 0 ");

			if (lStrPPoNo != null && !("").equals(lStrPPoNo) && !("-1").equals(lStrPPoNo)) {
				lSBQuery.append(" and dt.trn_pension_bill_hdr_id = tbh.trn_pension_bill_hdr_id AND dt.pay_for_month = tbh.for_month ");
			}

			if (lStrPPoNo != null && !"".equals(lStrPPoNo) && !"-1".equals(lStrPPoNo)) {
				lSBQuery.append(" and  dt.ppo_no = :ppoNo ");
			}

			if (lstrBranch != null && !"".equals(lstrBranch) && !"-1".equals(lstrBranch)) {
				lSBQuery.append(" AND tbh.BRANCH_CODE = :lBranchCode  ");
			}
			if (lstrBank != null && !"".equals(lstrBank) && !"-1".equals(lstrBank)) {
				lSBQuery.append(" AND tbh.BANK_CODE = :lBankCode  ");
			}
			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("lScheme", lStrScheme);
			lQuery.setString("lMonthly", "Monthly");
			lQuery.setString("lCVP", "CVP");
			lQuery.setString("lDCRG", "DCRG");
			lQuery.setString("locationCode", lLocCode);
			lQuery.setInteger("postId", Integer.valueOf(lPostId.toString()));
			if (lStrPPoNo != null && !("").equals(lStrPPoNo) && !("-1").equals(lStrPPoNo)) {
				lQuery.setString("ppoNo", lStrPPoNo);
			}

			if (lstrBranch != null && !"".equals(lstrBranch) && !"-1".equals(lstrBranch)) {
				lQuery.setString("lBranchCode", lstrBranch);
			}
			if (lstrBank != null && !"".equals(lstrBank) && !"-1".equals(lstrBank)) {
				lQuery.setString("lBankCode", lstrBank);
			}

			lLstResult = lQuery.list();

			if (lLstResult.size() > 0) {
				Iterator it = lLstResult.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();

					if (tuple[1].toString().equals(calcDate)) {
						if (new Integer(tuple[0].toString()) < 70 && new Integer(tuple[0].toString()) > 0) {
							return "bilNotApproved";
						} else if ("-1".equals(tuple[0].toString()) || new Integer(tuple[0].toString()) >= 70) {
							lStrBillApp = "bilApproved";
						} else {
							lStrBillApp = "bilNtCreated";
						}
					} else if (new Integer(tuple[0].toString()) < 70 && new Integer(tuple[0].toString()) > 0) {
						return "bilNotApproved";
					}
					/*
					 * else if("-1".equals(tuple[0].toString()) || new
					 * Integer(tuple[0].toString()) >= 70) { lStrBillApp =
					 * "bilApproved"; }
					 */
					/*
					 * else { lStrBillApp = "bilNtCreated"; }
					 */

				}
			} else {
				lStrBillApp = "bilNtCreated";
			}
			return lStrBillApp;
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw (e);
		}
	}

	public Map getLedgerAndPageNo(String lStrBillId, String lStrForMonth, String flag, String locationCode) throws Exception {

		Map lMapPensionerLedgerAndPage = new HashMap();

		try {
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append("SELECT A.pensionerCode,B.ledgerNo,B.pageNo FROM TrnPensionBillDtls A,TrnPensionRqstHdr B WHERE A.trnPensionBillHdrId IN (" + lStrBillId
					+ ") AND A.pensionerCode=B.pensionerCode AND B.caseStatus='APPROVED' and B.locationCode=:locationCode ");

			if (flag.equals("Y")) {
				lSBQuery.append(" AND A.payForMonth = :lForMonth ");
			} else if (flag.equals("N")) {
				lSBQuery.append(" AND A.payForMonth <> :lForMonth ");
			}

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setInteger("lForMonth", Integer.parseInt(lStrForMonth));
			lQuery.setString("locationCode", locationCode);

			List<Object[]> lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty()) {
				for (Object[] obj : lLstVO) {
					lMapPensionerLedgerAndPage.put(obj[0], (obj[1] != null && obj[1].toString().trim() != "" ? obj[1] : "0") + "~" + (obj[2] != null && obj[2].toString().trim() != "" ? obj[2] : "0"));
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lMapPensionerLedgerAndPage;

	}

	public String getStackTrace(Exception x) {

		OutputStream buf = new ByteArrayOutputStream();
		PrintStream p = new PrintStream(buf);
		x.printStackTrace(p);
		return buf.toString();
	}

	public List<TrnPensionOtherPartyPay> getOtherPartyPayLst(String lStrForMonth, List<Long> lLstBillNo, String lStrLocCode) throws Exception {

		List<TrnPensionOtherPartyPay> lLstOthPartyVo = new ArrayList<TrnPensionOtherPartyPay>();
		TrnPensionOtherPartyPay lObjOthPartyVo = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append("SELECT DISTINCT oth.other_party_type,oth.party_amnt,oth.ppo_no,oth.pensioner_name, " + " oth.party_name,oth.pensioner_code "
					+ " FROM trn_pension_other_party_pay oth,trn_pension_bill_hdr hd WHERE " + " hd.location_code = oth.location_code AND " + " hd.location_code = :locationCode AND "
					+ " hd.bill_no in ( :lstBillNo )  AND " + " hd.bill_no = oth.bill_no AND  " + " hd.rejected= oth.rejected AND  " + " oth.rejected = 0 AND " + " hd.for_month = :lForMonth  ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setInteger("lForMonth", Integer.parseInt(lStrForMonth));
			lQuery.setString("locationCode", lStrLocCode);
			lQuery.setParameterList("lstBillNo", lLstBillNo);

			List<Object[]> lLstVO = lQuery.list();

			if (lLstVO != null && !lLstVO.isEmpty()) {
				for (Object[] obj : lLstVO) {
					lObjOthPartyVo = new TrnPensionOtherPartyPay();
					lObjOthPartyVo.setOtherPartyType(new Long(obj[0].toString()));
					lObjOthPartyVo.setPartyAmnt(new BigDecimal(obj[1].toString()));
					lObjOthPartyVo.setPpoNo((String) obj[2]);
					lObjOthPartyVo.setPensionerName((String) obj[3]);
					lObjOthPartyVo.setPartyName((String) obj[4]);
					lObjOthPartyVo.setPensionerCode((String) obj[5]);
					lLstOthPartyVo.add(lObjOthPartyVo);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstOthPartyVo;

	}

	public List<MonthlyPensionBillVO> getPrvMonthArrDtls(String lStrPensionerCode, Long lBillHdrId, Long lBillDtlId) throws Exception {

		try {
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append(" FROM TrnPensionBillDtls WHERE trnPensionBillHdrId = :BillHdrId " + " AND trnPensionBillDtlsId != :BillDtlId AND pensionerCode = :pensionerCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("pensionerCode", lStrPensionerCode);
			lQuery.setLong("BillHdrId", lBillHdrId);
			lQuery.setLong("BillDtlId", lBillDtlId);

			List<TrnPensionBillDtls> lResultList = lQuery.list();
			List<MonthlyPensionBillVO> lMonthlyPensionBillList = new ArrayList<MonthlyPensionBillVO>();

			for (TrnPensionBillDtls lObj : lResultList) {
				MonthlyPensionBillVO lObMonthlyPensionBillVO = new MonthlyPensionBillVO();
				lObMonthlyPensionBillVO.setPpoNo(lObj.getPpoNo());
				lObMonthlyPensionBillVO.setPensionerName(lObj.getPensionerName());
				lObMonthlyPensionBillVO.setAccountNo(lObj.getAccountNo());
				lObMonthlyPensionBillVO.setForMonth(lObj.getPayForMonth());
				lObMonthlyPensionBillVO.setAllnBf11156(lObj.getAllcationBf11156());
				lObMonthlyPensionBillVO.setAllnAf11156(lObj.getAllcationAf11156());
				lObMonthlyPensionBillVO.setAllnAf10560(lObj.getAllcationAf10560());
				lObMonthlyPensionBillVO.setBasicPensionAmount(lObj.getPensionAmount());
				lObMonthlyPensionBillVO.setDpPercentAmount(lObj.getDpAmount());
				lObMonthlyPensionBillVO.setAdpAmount(lObj.getAdpAmount());
				lObMonthlyPensionBillVO.setTiPercentAmount(lObj.getTiAmount());
				lObMonthlyPensionBillVO.setMedicalAllowenceAmount(lObj.getMedicalAllowenceAmount());
				lObMonthlyPensionBillVO.setCvpMonthlyAmount(lObj.getCvpMonthAmount());
				lObMonthlyPensionBillVO.setTIArrearsAmount(lObj.getTiArrearAmount());
				lObMonthlyPensionBillVO.setOtherArrearsAmount(lObj.getArrearAmount());
				lObMonthlyPensionBillVO.setItCutAmount(lObj.getIncomeTaxCutAmount());
				lObMonthlyPensionBillVO.setSpecialCutAmount(lObj.getSpecialCutAmount());
				lObMonthlyPensionBillVO.setOtherBenefit(lObj.getOtherBenefits());
				lObMonthlyPensionBillVO.setOMR(lObj.getOmr());
				lObMonthlyPensionBillVO.setRecoveryAmount(lObj.getRecoveryAmount());
				lObMonthlyPensionBillVO.setPensionCutAmount(lObj.getPensnCutAmount());
				lObMonthlyPensionBillVO.setPersonalPension(lObj.getPersonalPensionAmount());
				lObMonthlyPensionBillVO.setIr(lObj.getIrAmount());
				lObMonthlyPensionBillVO.setMOComm(lObj.getMoCommission());
				lObMonthlyPensionBillVO.setNetPensionAmount(lObj.getReducedPension());

				lMonthlyPensionBillList.add(lObMonthlyPensionBillVO);
			}
			return lMonthlyPensionBillList;
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}

	}

	/**
	 * get Modified PPO List of given Bank branch Vidhi
	 * 
	 * @param String
	 *            : Bank and Branch Code
	 * @return : List of Modified PPO
	 */
	public List getModifiedPPOForMonthly(List<String> lLstBranchCode, String lLocCode, Long lLngPostId) throws Exception {

		List<String> lLstCaseStatus = new ArrayList<String>();
		try {
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();
			lLstCaseStatus.add(bundleCaseConst.getString("STATFLG.MODIFIED"));
			lLstCaseStatus.add(bundleCaseConst.getString("STATFLG.MODIFIEDBYAUDITOR"));
			lSBQuery.append("SELECT rq.ppoNo,md.branchCode FROM TrnPensionRqstHdr rq,MstPensionerDtls md \n");
			lSBQuery.append("WHERE \n");
			lSBQuery.append("rq.pensionerCode = md.pensionerCode \n");
			lSBQuery.append("AND rq.locationCode = :lLocCode	\n");
			lSBQuery.append("AND rq.caseOwner = :lPostId \n");
			lSBQuery.append("AND md.branchCode in (:lbranchCode) \n");
			lSBQuery.append("AND rq.caseStatus in (:lModifiedStatus) \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameterList("lbranchCode", lLstBranchCode);
			lQuery.setParameter("lLocCode", lLocCode);
			lQuery.setParameter("lPostId", new BigDecimal(lLngPostId));
			lQuery.setParameterList("lModifiedStatus", lLstCaseStatus);

			return lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	public List<MonthlyPensionBillVO> getPrvMonthlyBillDtls(String lStrLocationCode, Long lAuditorId, String lStrBranchCode, Integer lIntForMonth) throws Exception {

		try {
			StringBuilder lSBQuery = new StringBuilder();
			ghibSession = getSession();

			lSBQuery.append(" SELECT BD.*,BH.Head_Code FROM trn_pension_bill_hdr bh "
					+ " JOIN trn_pension_bill_dtls bd ON bh.trn_pension_bill_hdr_id = bd.trn_pension_bill_hdr_id AND bh.for_month = bd.pay_for_month AND bh.rejected = 0 "
					+ " WHERE bh.location_code = :LocationCode AND bh.created_post_id = :Auditor AND bh.bill_type = 'Monthly' " + " AND bh.branch_code = :BranchCode AND bh.for_month = :ForMonth  ");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("LocationCode", lStrLocationCode);
			lQuery.setLong("Auditor", lAuditorId);
			lQuery.setString("BranchCode", lStrBranchCode);
			lQuery.setInteger("ForMonth", lIntForMonth);

			List<Object[]> lLstVO = lQuery.list();
			List<MonthlyPensionBillVO> lMonthlyPensionBillList = new ArrayList<MonthlyPensionBillVO>();

			if (lLstVO != null && !lLstVO.isEmpty()) {
				for (Object[] obj : lLstVO) {
					MonthlyPensionBillVO lObMonthlyPensionBillVO = new MonthlyPensionBillVO();
					lObMonthlyPensionBillVO.setPpoNo((String) obj[1]);
					lObMonthlyPensionBillVO.setHeadCode(new BigDecimal((Short) obj[32]));
					lObMonthlyPensionBillVO.setPensionerCode((String) obj[2]);
					lObMonthlyPensionBillVO.setPensionerName((String) obj[16]);
					lObMonthlyPensionBillVO.setAccountNo((String) obj[14]);
					lObMonthlyPensionBillVO.setForMonth((Integer) obj[28]);
					lObMonthlyPensionBillVO.setAllnBf11156((BigDecimal) obj[17]);
					lObMonthlyPensionBillVO.setAllnAf11156((BigDecimal) obj[18]);
					lObMonthlyPensionBillVO.setAllnAf10560((BigDecimal) obj[19]);
					lObMonthlyPensionBillVO.setBasicPensionAmount((BigDecimal) obj[7]);
					lObMonthlyPensionBillVO.setDpPercentAmount((BigDecimal) obj[8]);
					lObMonthlyPensionBillVO.setAdpAmount((BigDecimal) obj[29]);
					lObMonthlyPensionBillVO.setTiPercentAmount((BigDecimal) obj[9]);
					lObMonthlyPensionBillVO.setMedicalAllowenceAmount((BigDecimal) obj[10]);
					lObMonthlyPensionBillVO.setCvpMonthlyAmount((BigDecimal) obj[6]);
					lObMonthlyPensionBillVO.setTIArrearsAmount((BigDecimal) obj[23]);
					lObMonthlyPensionBillVO.setOtherArrearsAmount((BigDecimal) obj[11]);
					lObMonthlyPensionBillVO.setItCutAmount((BigDecimal) obj[3]);
					lObMonthlyPensionBillVO.setSpecialCutAmount((BigDecimal) obj[5]);
					lObMonthlyPensionBillVO.setOtherBenefit((BigDecimal) obj[25]);
					lObMonthlyPensionBillVO.setOMR((BigDecimal) obj[24]);
					lObMonthlyPensionBillVO.setRecoveryAmount((BigDecimal) obj[12]);
					lObMonthlyPensionBillVO.setPensionCutAmount((BigDecimal) obj[4]);
					lObMonthlyPensionBillVO.setPersonalPension((BigDecimal) obj[20]);
					lObMonthlyPensionBillVO.setIr((BigDecimal) obj[21]);
					lObMonthlyPensionBillVO.setMOComm((BigDecimal) obj[22]);
					lObMonthlyPensionBillVO.setNetPensionAmount((BigDecimal) obj[13]);

					lMonthlyPensionBillList.add(lObMonthlyPensionBillVO);
				}
			}

			return lMonthlyPensionBillList;
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}

	}

	/*
	 * Do not delete this method. This method is used to validate change
	 * statement when muliple change statements are allowed for one branch in
	 * one month This method will check change statement of upto which date for
	 * selected month-year is generated and not approved.
	 */
	/*
	 * public Date getStatusOfChangeStatement(String lStrLocationCode, String
	 * lStrBranchCode, Integer lIntForMonth, Long lLngAuditorPostId) throws
	 * Exception {
	 * 
	 * StringBuilder lSBQuery = new StringBuilder(); ghibSession = getSession();
	 * Date lDtUpto = null; try {
	 * 
	 * lSBQuery.append(" SELECT mcr.upToDate \n");
	 * lSBQuery.append(" FROM TrnMonthlyChangeRqst mcr \n");
	 * lSBQuery.append(" Where mcr.locationCode = :LocationCode \n");
	 * lSBQuery.append(" AND mcr.createdPostId = :Auditor \n");
	 * lSBQuery.append(" AND mcr.branchCode = :BranchCode \n");
	 * lSBQuery.append(" AND mcr.forMonth = :ForMonth ");
	 * lSBQuery.append(" AND mcr.status = :lStatus ");
	 * 
	 * Query lQuery = ghibSession.createQuery(lSBQuery.toString());
	 * 
	 * lQuery.setString("LocationCode", lStrLocationCode);
	 * lQuery.setLong("Auditor", lLngAuditorPostId);
	 * lQuery.setString("BranchCode", lStrBranchCode);
	 * lQuery.setInteger("ForMonth", lIntForMonth); lQuery.setString("lStatus",
	 * bundleConst.getString("CHANGSTMNTSTATUS.BEFOREAPPROVAL"));
	 * 
	 * List lLstObj = lQuery.list(); if (lLstObj != null && lLstObj.size() > 0)
	 * { lDtUpto = (Date) lLstObj.get(0); } return lDtUpto; } catch (Exception
	 * e) { logger.error(" Error is : " + e, e); throw (e); }
	 * 
	 * }
	 */

	/*
	 * This method is used to check if the change statement for current month is
	 * generated or not.
	 */
	public List<Object[]> getStatusOfChangeStatement(String lStrLocationCode, List<String> lLstBranchCode, Integer lIntForMonth, Long lLngAuditorPostId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		String lStrChngStmntStatus = null;
		List<Object[]> lLstObj = null;
		try {
			// --getting lastest change statement record for current month
			/*
			 * lSBQuery.append(" SELECT mcr.status \n");
			 * lSBQuery.append(" FROM TrnMonthlyChangeRqst mcr \n");
			 * lSBQuery.append(" Where mcr.locationCode = :LocationCode \n");
			 * lSBQuery.append(" AND mcr.createdPostId = :Auditor \n");
			 * lSBQuery.append(" AND mcr.branchCode = :BranchCode \n");
			 * lSBQuery.append(" AND mcr.forMonth = :ForMonth ");
			 * lSBQuery.append(" Order by mcr.changeRqstDate desc");
			 */

			lSBQuery.append(" Select mcr.status,mcr.branchCode,mcr.createdPostId \n");
			lSBQuery.append(" From TrnMonthlyChangeRqst mcr \n");
			lSBQuery.append(" Where \n");
			lSBQuery.append(" (mcr.changeRqstDate,mcr.branchCode,mcr.forMonth) in \n");
			lSBQuery.append(" (Select max(changeRqstDate),branchCode,forMonth \n");
			lSBQuery.append(" From \n");
			lSBQuery.append(" TrnMonthlyChangeRqst \n");
			lSBQuery.append(" Where \n");
			lSBQuery.append(" locationCode = :LocationCode \n");
			// lSBQuery.append(" And createdPostId = :Auditor \n");
			lSBQuery.append(" And branchCode in (:lLstBranchCode) \n");
			lSBQuery.append(" And forMonth = :ForMonth \n");
			lSBQuery.append(" Group by \n");
			lSBQuery.append(" forMonth,branchCode) ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("LocationCode", lStrLocationCode);
			// lQuery.setLong("Auditor", lLngAuditorPostId);
			lQuery.setParameterList("lLstBranchCode", lLstBranchCode);
			lQuery.setInteger("ForMonth", lIntForMonth);
			// lQuery.setMaxResults(1);

			lLstObj = lQuery.list();
			// if (lLstObj != null && lLstObj.size() > 0) {
			// lStrChngStmntStatus = lLstObj.get(0).toString();
			// }
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstObj;
	}

	public List getChangeStatementDtls(String lStrLocationCode, Long lLngAuditorPostId, String lStrSearchCrt, String lStrSearchVal, String lStrSearchBankCode, String lStrSearchBranchCode,
			Map displayTag, String lStrShowWLFor) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		SimpleDateFormat lSdf1 = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat lSdf2 = new SimpleDateFormat("MMMMM-yyyy");
		SimpleDateFormat lSdf3 = new SimpleDateFormat("dd/MM/yyyy");
		String lStrForMonth = null;
		List<Object[]> lLstChngStmntDtl = null;
		List<String> lLstChngStmentStatus = new ArrayList<String>();
		lLstChngStmentStatus.add(bundleConst.getString("CHANGSTMNTSTATUS.BEFOREAPPROVAL"));
		lLstChngStmentStatus.add(bundleConst.getString("CHANGSTMNTSTATUS.APPROVED"));
		lLstChngStmentStatus.add(bundleConst.getString("CHANGSTMNTSTATUS.REJECTED"));
		String[] columnValues = null;
		if ((bundleConst.getString("CHNGSTMNT.SHOWWLFORATO")).equals(lStrShowWLFor)) {
			columnValues = new String[]{"", " mb.bankName", "rbb.branchName", "mcr.upToDate", "mcr.forMonth"};
		} else {
			columnValues = new String[]{"", " mb.bankName", "rbb.branchName", "mcr.upToDate", "mcr.status", "mcr.forMonth"};
		}

		try {
			lSBQuery.append(" SELECT mcr.changeRqstId, \n");
			lSBQuery.append("  mb.bankName, \n");
			lSBQuery.append(" rbb.branchName, \n");
			lSBQuery.append(" concat(concat(concat(concat(oem.empFname,' '),oem.empMname),' '),oem.empLname), \n");
			lSBQuery.append(" mcr.upToDate, \n");
			lSBQuery.append(" mcr.status, \n");
			lSBQuery.append(" mcr.forMonth, \n");
			lSBQuery.append(" mcr.bankCode, \n");
			lSBQuery.append(" mcr.branchCode, \n");
			lSBQuery.append(" mcr.forMonth \n");
			lSBQuery.append(" From \n");
			lSBQuery.append(" TrnMonthlyChangeRqst mcr, \n");
			lSBQuery.append(" RltBankBranch rbb, \n");
			lSBQuery.append(" MstBank mb, \n");
			lSBQuery.append(" OrgUserpostRlt oup, \n");
			lSBQuery.append(" OrgEmpMst oem \n");
			lSBQuery.append(" Where \n");
			lSBQuery.append(" mcr.branchCode = rbb.branchCode \n");
			lSBQuery.append(" And mcr.bankCode = mb.bankCode \n");
			lSBQuery.append(" And mcr.createdPostId=oup.orgPostMstByPostId.postId \n");
			lSBQuery.append(" And oup.orgUserMst.userId = oem.orgUserMst.userId \n");
			if ((bundleConst.getString("CHNGSTMNT.SHOWWLFORATO")).equals(lStrShowWLFor)) {
				lSBQuery.append(" And mcr.status = :lStatus \n");
			} else {
				lSBQuery.append(" And mcr.createdPostId = :lAudiPostId \n");
				lSBQuery.append(" And mcr.status IN (:lLstStatus)  \n");
			}
			lSBQuery.append(" And mcr.billNo is null \n");
			lSBQuery.append(" And mcr.locationCode = :lLocCode \n");
			// lSBQuery.append(" And mcr.forMonth = :lForMonth, \n");
			// lSBQuery.append(" And mcr.branchCode = :lBranchCode");

			/**
			 * 
			 * Code for search starts<<<<
			 */
			if ((lStrSearchCrt != null && !lStrSearchCrt.equals(""))) {
				if ("mcr.bank_code".equalsIgnoreCase(lStrSearchCrt)) {
					if (lStrSearchBankCode != null && !"".equals(lStrSearchBankCode) && !"-1".equals(lStrSearchBankCode)) {
						lSBQuery.append(" and mcr.bankCode = :lStrBankCode \n");
					}
					if (lStrSearchBranchCode != null && !"".equals(lStrSearchBranchCode) && !"-1".equals(lStrSearchBranchCode)) {
						lSBQuery.append(" and mcr.branchCode = :lStrBranchCode \n");
					}
				}
				if (lStrSearchVal != null && !"".equals(lStrSearchVal) && !"-1".equals(lStrSearchVal)) {
					if ("mcr.for_month".equalsIgnoreCase(lStrSearchCrt)) {
						lSBQuery.append(" and mcr.forMonth = :lStrForMonth \n");
					} else if ("mcr.branch_code".equalsIgnoreCase(lStrSearchCrt)) {
						lSBQuery.append(" and mcr.branchCode = :lStrBranchCode \n");
					} else if ("mcr.upto_date".equalsIgnoreCase(lStrSearchCrt)) {
						lSBQuery.append(" and mcr.upToDate = :lDtUptoDate \n");
					} else if ("mcr.status".equalsIgnoreCase(lStrSearchCrt)) {
						lSBQuery.append(" and mcr.status = :lStrStatus \n");
					} else if ("mcr.created_post_id".equalsIgnoreCase(lStrSearchCrt)) {
						lSBQuery.append(" and mcr.createdPostId = :lIntAudPostId \n");
					}
				}
			}
			/**
			 * Code for search ends >>>>
			 */
			lSBQuery.append("Order By \n");
			String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag.get(Constants.KEY_SORT_ORDER) : "desc");
			Integer orderbypara = null;

			if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				lSBQuery.append(columnValues[orderbypara.intValue()] + " " + orderString);
			} else {
				lSBQuery.append("mcr.changeRqstId ");

			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lLocCode", lStrLocationCode);

			if (!(bundleConst.getString("CHNGSTMNT.SHOWWLFORATO")).equals(lStrShowWLFor)) {
				lQuery.setLong("lAudiPostId", lLngAuditorPostId);
				lQuery.setParameterList("lLstStatus", lLstChngStmentStatus);
			} else {
				lQuery.setString("lStatus", bundleConst.getString("CHANGSTMNTSTATUS.WITHATOFORAPPROVAL"));
			}

			/**
			 * 
			 * Setting values for search parameter starts <<<
			 */
			if ("mcr.bank_code".equalsIgnoreCase(lStrSearchCrt)) {
				if (lStrSearchBankCode != null && !"".equals(lStrSearchBankCode) && !"-1".equals(lStrSearchBankCode)) {
					lQuery.setString("lStrBankCode", lStrSearchBankCode);
				}
				if (lStrSearchBranchCode != null && !"".equals(lStrSearchBranchCode) && !"-1".equals(lStrSearchBranchCode)) {
					lQuery.setString("lStrBranchCode", lStrSearchBranchCode);
				}
			}
			if (lStrSearchVal != null && !"".equals(lStrSearchVal) && !"-1".equals(lStrSearchVal)) {
				if ("mcr.for_month".equalsIgnoreCase(lStrSearchCrt)) {
					lQuery.setInteger("lStrForMonth", Integer.parseInt(lStrSearchVal));
				} else if ("mcr.branch_code".equalsIgnoreCase(lStrSearchCrt)) {
					lQuery.setString("lStrBranchCode", lStrSearchVal);
				} else if ("mcr.upto_date".equalsIgnoreCase(lStrSearchCrt)) {
					lQuery.setDate("lDtUptoDate", lSdf3.parse(lStrSearchVal));
				} else if ("mcr.status".equalsIgnoreCase(lStrSearchCrt)) {
					lQuery.setString("lStrStatus", lStrSearchVal);
				} else if ("mcr.created_post_id".equalsIgnoreCase(lStrSearchCrt)) {
					lQuery.setInteger("lIntAudPostId", Integer.valueOf(lStrSearchVal));
				}
			}
			/**
			 * 
			 * Setting values for search parameter ends >>>
			 */
			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lQuery.setFirstResult((pageNo.intValue() - 1) * Constants.PAGE_SIZE);
			lQuery.setMaxResults(Constants.PAGE_SIZE);

			List<Object[]> lLstObj = lQuery.list();
			lLstChngStmntDtl = new ArrayList<Object[]>();
			for (Object[] lArrObj : lLstObj) {
				lStrForMonth = lSdf2.format(lSdf1.parse(lArrObj[6].toString()));
				lArrObj[6] = lStrForMonth;
				lLstChngStmntDtl.add(lArrObj);
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstChngStmntDtl;
	}

	public Integer getChangeStatementDtlsCount(String lStrLocationCode, Long lLngAuditorPostId, String lStrSearchCrt, String lStrSearchVal, String lStrSearchBankCode, String lStrSearchBranchCode,
			String lStrShowWLFor) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		SimpleDateFormat lSdf3 = new SimpleDateFormat("dd/MM/yyyy");
		Integer lIntCount = null;
		List<String> lLstChngStmentStatus = new ArrayList<String>();
		lLstChngStmentStatus.add(bundleConst.getString("CHANGSTMNTSTATUS.BEFOREAPPROVAL"));
		lLstChngStmentStatus.add(bundleConst.getString("CHANGSTMNTSTATUS.APPROVED"));
		lLstChngStmentStatus.add(bundleConst.getString("CHANGSTMNTSTATUS.REJECTED"));
		try {
			lSBQuery.append(" SELECT count(mcr.changeRqstId) \n");
			lSBQuery.append(" From \n");
			lSBQuery.append(" TrnMonthlyChangeRqst mcr, \n");
			lSBQuery.append(" RltBankBranch rbb, \n");
			lSBQuery.append(" MstBank mb, \n");
			lSBQuery.append(" OrgUserpostRlt oup, \n");
			lSBQuery.append(" OrgEmpMst oem \n");
			lSBQuery.append(" Where \n");
			lSBQuery.append(" mcr.branchCode = rbb.branchCode \n");
			lSBQuery.append(" And mcr.bankCode = mb.bankCode \n");
			lSBQuery.append(" And mcr.createdPostId=oup.orgPostMstByPostId.postId \n");
			lSBQuery.append(" And oup.orgUserMst.userId = oem.orgUserMst.userId \n");
			if ((bundleConst.getString("CHNGSTMNT.SHOWWLFORATO")).equals(lStrShowWLFor)) {
				lSBQuery.append(" And mcr.status = :lStatus \n");
			} else {
				lSBQuery.append(" And mcr.createdPostId = :lAudiPostId \n");
				lSBQuery.append(" And mcr.status  IN (:lLstStatus) \n");
			}
			lSBQuery.append(" And mcr.billNo is null \n");
			lSBQuery.append(" And mcr.locationCode = :lLocCode \n");
			// lSBQuery.append(" And mcr.forMonth = :lForMonth, \n");
			// lSBQuery.append(" And mcr.branchCode = :lBranchCode");

			/**
			 * 
			 * Code for search starts<<<<
			 */
			if ((lStrSearchCrt != null && !lStrSearchCrt.equals(""))) {
				if ("mcr.bank_code".equalsIgnoreCase(lStrSearchCrt)) {
					if (lStrSearchBankCode != null && !"".equals(lStrSearchBankCode) && !"-1".equals(lStrSearchBankCode)) {
						lSBQuery.append(" and mcr.bankCode = :lStrBankCode \n");
					}
					if (lStrSearchBranchCode != null && !"".equals(lStrSearchBranchCode) && !"-1".equals(lStrSearchBranchCode)) {
						lSBQuery.append(" and mcr.branchCode = :lStrBranchCode \n");
					}
				}
				if (lStrSearchVal != null && !"".equals(lStrSearchVal) && !"-1".equals(lStrSearchVal)) {
					if ("mcr.for_month".equalsIgnoreCase(lStrSearchCrt)) {
						lSBQuery.append(" and mcr.forMonth = :lStrForMonth \n");
					} else if ("mcr.branch_code".equalsIgnoreCase(lStrSearchCrt)) {
						lSBQuery.append(" and mcr.branchCode = :lStrBranchCode \n");
					} else if ("mcr.upto_date".equalsIgnoreCase(lStrSearchCrt)) {
						lSBQuery.append(" and mcr.upToDate = :lDtUptoDate \n");
					} else if ("mcr.status".equalsIgnoreCase(lStrSearchCrt)) {
						lSBQuery.append(" and mcr.status = :lStrStatus \n");
					} else if ("mcr.created_post_id".equalsIgnoreCase(lStrSearchCrt)) {
						lSBQuery.append(" and mcr.createdPostId = :lIntAudPostId \n");
					}
				}
			}
			/**
			 * Code for search ends >>>>
			 */
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lLocCode", lStrLocationCode);
			if (!(bundleConst.getString("CHNGSTMNT.SHOWWLFORATO")).equals(lStrShowWLFor)) {
				lQuery.setLong("lAudiPostId", lLngAuditorPostId);
				lQuery.setParameterList("lLstStatus", lLstChngStmentStatus);
			} else {
				lQuery.setString("lStatus", bundleConst.getString("CHANGSTMNTSTATUS.WITHATOFORAPPROVAL"));
			}
			// lQuery.setString("lBranchCode", lStrBranchCode);
			// lQuery.setInteger("lForMonth", lIntForMonth);
			/**
			 * 
			 * Setting values for search parameter starts <<<
			 */
			if ("mcr.bank_code".equalsIgnoreCase(lStrSearchCrt)) {
				if (lStrSearchBankCode != null && !"".equals(lStrSearchBankCode) && !"-1".equals(lStrSearchBankCode)) {
					lQuery.setString("lStrBankCode", lStrSearchBankCode);
				}
				if (lStrSearchBranchCode != null && !"".equals(lStrSearchBranchCode) && !"-1".equals(lStrSearchBranchCode)) {
					lQuery.setString("lStrBranchCode", lStrSearchBranchCode);
				}
			}
			if (lStrSearchVal != null && !"".equals(lStrSearchVal) && !"-1".equals(lStrSearchVal)) {
				if ("mcr.for_month".equalsIgnoreCase(lStrSearchCrt)) {
					lQuery.setInteger("lStrForMonth", Integer.parseInt(lStrSearchVal));
				} else if ("mcr.branch_code".equalsIgnoreCase(lStrSearchCrt)) {
					lQuery.setString("lStrBranchCode", lStrSearchVal);
				} else if ("mcr.upto_date".equalsIgnoreCase(lStrSearchCrt)) {
					lQuery.setDate("lDtUptoDate", lSdf3.parse(lStrSearchVal));
				} else if ("mcr.status".equalsIgnoreCase(lStrSearchCrt)) {
					lQuery.setString("lStrStatus", lStrSearchVal);
				} else if ("mcr.created_post_id".equalsIgnoreCase(lStrSearchCrt)) {
					lQuery.setInteger("lIntAudPostId", Integer.valueOf(lStrSearchVal));
				}
			}
			/**
			 * 
			 * Setting values for search parameter ends >>>
			 */
			List lLstObj = lQuery.list();
			lIntCount = ((Long) lLstObj.get(0)).intValue();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lIntCount;
	}

	public Object[] getPrevMonthSummary(String lStrBranchCode, String lStrLocationCode, Integer lIntPrevForMonth, String lStrSchemeCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		Object[] lArrPrevMonthSummary = null;
		List<Short> lLstRjctedBillStatus = new ArrayList<Short>();
		Short lShRjctedStatus = -1;
		Short lShArchivedStatus = -15;
		Short lShDiscardedStatus = 60;
		try {
			lLstRjctedBillStatus.add(lShRjctedStatus);
			lLstRjctedBillStatus.add(lShArchivedStatus);
			lLstRjctedBillStatus.add(lShDiscardedStatus);
			lSBQuery.append(" SELECT count(pbd.pensionerCode),SUM(pbd.netAmount) \n");
			lSBQuery.append(" From \n");
			lSBQuery.append(" TrnBillRegister tbr, \n");
			lSBQuery.append(" TrnPensionBillDtls pbd,TrnPensionBillHdr pbh \n");
			lSBQuery.append(" Where \n");
			lSBQuery.append(" tbr.billNo = pbh.billNo \n");
			lSBQuery.append("And pbd.trnPensionBillHdrId = pbh.trnPensionBillHdrId \n");
			lSBQuery.append("And pbh.locationCode = :lLocCode \n");
			lSBQuery.append("And pbh.branchCode = :lBranchCode \n");
			lSBQuery.append("And pbd.payForMonth = :lPrevForMonth \n");
			lSBQuery.append("And pbh.billType = :lBillType \n");
			lSBQuery.append("And tbr.currBillStatus  not in (:lLstRjctedStatus)\n");
			if (lStrSchemeCode != null && lStrSchemeCode.length() > 0) {
				lSBQuery.append("And pbh.schemeCode = :lSchemeCode \n");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lLocCode", lStrLocationCode);
			lQuery.setString("lBranchCode", lStrBranchCode);
			lQuery.setInteger("lPrevForMonth", lIntPrevForMonth);
			lQuery.setString("lBillType", bundleConst.getString("PPMT.MONTHLY"));
			if (lStrSchemeCode != null && lStrSchemeCode.length() > 0) {
				lQuery.setString("lSchemeCode", lStrSchemeCode);
			}
			lQuery.setParameterList("lLstRjctedStatus", lLstRjctedBillStatus);

			List lLstObj = lQuery.list();
			if (lLstObj != null && lLstObj.size() > 0) {
				lArrPrevMonthSummary = (Object[]) lLstObj.get(0);
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lArrPrevMonthSummary;
	}

	public Object[] getCurrMnthChngStmntSummary(String lStrChngRqstId, String lStrSchemeCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		Object[] lArrChngStmntSummary = null;
		try {
			lSBQuery.append(" SELECT count(pcd.pensioner_Code),cast(SUM(pcd.net_Amount) as double), \n");
			lSBQuery.append("mb.bank_Name,rbb.branch_Name,mcr.branch_code \n");
			lSBQuery.append(" From \n");
			lSBQuery.append(" Trn_Monthly_Change_Rqst mcr \n");
			lSBQuery.append(" Left outer join Trn_Pension_Change_Hdr pch on mcr.change_Rqst_Id = pch.change_Rqst_Id  \n");
			lSBQuery.append(" Left outer join Trn_Pension_Change_Dtls pcd on pcd.trn_Pension_Change_Hdr_Id = pch.trn_Pension_Change_Hdr_Id");
			lSBQuery.append(" Join Mst_Bank mb on mb.bank_Code = mcr.bank_Code  \n");
			lSBQuery.append(" Join Rlt_Bank_Branch rbb on rbb.branch_Code = mcr.branch_Code \n");
			lSBQuery.append(" Where \n");
			lSBQuery.append(" mcr.change_Rqst_Id = :lLngChngRqstId \n");
			if (lStrSchemeCode != null && lStrSchemeCode.length() > 0) {
				lSBQuery.append("And pch.scheme_Code = :lSchemeCode \n");
			}
			lSBQuery.append("group by \n");
			lSBQuery.append("mb.bank_Name,rbb.branch_Name,mcr.branch_code \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setLong("lLngChngRqstId", Long.valueOf(lStrChngRqstId));
			if (lStrSchemeCode != null && lStrSchemeCode.length() > 0) {
				lQuery.setString("lSchemeCode", lStrSchemeCode);
			}
			List lLstObj = lQuery.list();
			if (lLstObj != null && lLstObj.size() > 0) {
				lArrChngStmntSummary = (Object[]) lLstObj.get(0);
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lArrChngStmntSummary;
	}

	public List<Object[]> getPrevMonthBillDtlVOList(String lStrBranchCode, String lStrLocationCode, Integer lIntPrevForMonth, String lStrSchemeCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		List<Object[]> lLstObj = null;
		List<Short> lLstRjctedBillStatus = new ArrayList<Short>();
		Short lShRjctedStatus = -1;
		Short lShArchivedStatus = -15;
		Short lShDiscardedStatus = 60;
		try {
			lLstRjctedBillStatus.add(lShRjctedStatus);
			lLstRjctedBillStatus.add(lShArchivedStatus);
			lLstRjctedBillStatus.add(lShDiscardedStatus);
			lSBQuery.append(" SELECT pbd.pensionerCode,pbd \n");
			lSBQuery.append(" From \n");
			lSBQuery.append(" TrnBillRegister tbr, \n");
			lSBQuery.append(" TrnPensionBillDtls pbd,TrnPensionBillHdr pbh \n");
			lSBQuery.append(" Where \n");
			lSBQuery.append(" tbr.billNo = pbh.billNo \n");
			lSBQuery.append("And pbd.trnPensionBillHdrId = pbh.trnPensionBillHdrId  \n");
			lSBQuery.append("And pbh.locationCode = :lLocCode \n");
			lSBQuery.append("And pbh.branchCode = :lBranchCode \n");
			lSBQuery.append("And pbd.payForMonth = :lPrevForMonth \n");
			lSBQuery.append("And pbh.billType = :lBillType \n");
			lSBQuery.append("And tbr.currBillStatus  not in (:lLstRjctedStatus)\n");
			if (lStrSchemeCode != null && lStrSchemeCode.length() > 0) {
				lSBQuery.append("And pbh.schemeCode = :lSchemeCode \n");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lLocCode", lStrLocationCode);
			lQuery.setString("lBranchCode", lStrBranchCode);
			lQuery.setInteger("lPrevForMonth", lIntPrevForMonth);
			lQuery.setString("lBillType", bundleConst.getString("PPMT.MONTHLY"));
			lQuery.setParameterList("lLstRjctedStatus", lLstRjctedBillStatus);
			if (lStrSchemeCode != null && lStrSchemeCode.length() > 0) {
				lQuery.setString("lSchemeCode", lStrSchemeCode);
			}

			lLstObj = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstObj;
	}

	public List<Object[]> getCurrMnthChngStmntDtlVOList(String lStrChangeRqstId, String lStrSchemeCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		List<Object[]> lLstObj = null;

		try {
			lSBQuery.append(" SELECT pcd.pensionerCode,pcd,mcr.branchCode \n");
			lSBQuery.append(" From \n");
			lSBQuery.append(" TrnPensionChangeDtls pcd,TrnPensionChangeHdr pch,TrnMonthlyChangeRqst mcr \n");
			lSBQuery.append(" Where \n");
			lSBQuery.append(" mcr.changeRqstId = pch.changeRqstId \n");
			lSBQuery.append("And pcd.trnPensionChangeHdrId = pch.trnPensionChangeHdrId \n");
			lSBQuery.append("And mcr.changeRqstId = :lLngChngRqstId \n");
			if (lStrSchemeCode != null && lStrSchemeCode.length() > 0) {
				lSBQuery.append("And pch.schemeCode = :lSchemeCode \n");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("lLngChngRqstId", Long.valueOf(lStrChangeRqstId));
			if (lStrSchemeCode != null && lStrSchemeCode.length() > 0) {
				lQuery.setString("lSchemeCode", lStrSchemeCode);
			}
			lLstObj = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstObj;
	}

	public List<TrnPensionChangeHdr> getTrnPensionChangeHdrVOList(Long lLngChangeRqstId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		List<TrnPensionChangeHdr> lLstTrnPensionChangeHdr = null;
		try {
			lSBQuery.append(" FROM TrnPensionChangeHdr WHERE changeRqstId=:changeRqstId");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("changeRqstId", lLngChangeRqstId);

			lLstTrnPensionChangeHdr = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lLstTrnPensionChangeHdr;

	}

	public List<TrnPensionChangeDtls> getTrnPensionChangeDtlsVOList(Long lLngChangeHdrId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		List<TrnPensionChangeDtls> lLstTrnPensionChangeDtls = null;
		try {
			lSBQuery.append(" FROM TrnPensionChangeDtls WHERE trnPensionChangeHdrId=:lLngChangeHdrId");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("lLngChangeHdrId", lLngChangeHdrId);

			lLstTrnPensionChangeDtls = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lLstTrnPensionChangeDtls;

	}

	public List<MstPensionHeadcode> getAllHeadCodeAndDesc() throws Exception {

		List<MstPensionHeadcode> lLstMstPensionHeadcode = null;
		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		try {
			lSBQuery.append("FROM MstPensionHeadcode ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lLstMstPensionHeadcode = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstMstPensionHeadcode;
	}

	/*
	 * WHERE hdr.bill_no=991005527469 AND reg.bill_no=hdr.bill_no AND
	 * hdr.trn_pension_bill_hdr_id=dtl.trn_pension_bill_hdr_id AND
	 * dtl.pensioner_code=rqst.pensioner_code
	 */

	public List<MonthlyPensionBillVO> getMonthlyPensionBillVOList(Long lLngBillNo, String lStrLocCode, String lStrSortBy) throws Exception {

		List<MonthlyPensionBillVO> lLstMonthlyPensionBillVO = null;
		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		try {
			lLstMonthlyPensionBillVO = new ArrayList<MonthlyPensionBillVO>();
			lSBQuery.append("SELECT new com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO(dtl.headCode,rqst.ledgerNo,rqst.pageNo,dtl.ppoNo,dtl.pensionerName,dtl.accountNo,"
					+ "dtl.allcationBf1436,dtl.allcationBf11156,dtl.allcationAf11156,dtl.allcationAf10560,dtl.allcationAfZp,dtl.ir1Amount,dtl.dpAmount,dtl.ir2Amount,dtl.tiAmount,dtl.ir3Amount,"
					+ "dtl.grossAmount,dtl.netAmount,dtl.tiArrearAmount,dtl.arrearAmount,dtl.recoveryAmount,dtl.peonAllowance,dtl.medicalAllowenceAmount,dtl.other1,dtl.otherBenefits,dtl.totalArrearAmt) \n");
			lSBQuery.append("FROM TrnBillRegister reg,TrnPensionBillHdr hdr,TrnPensionBillDtls dtl,TrnPensionRqstHdr rqst \n");
			lSBQuery.append("WHERE hdr.billNo=:billNo \n");
			lSBQuery.append("AND reg.billNo=hdr.billNo \n");
			lSBQuery.append("AND hdr.trnPensionBillHdrId=dtl.trnPensionBillHdrId \n");
			lSBQuery.append("AND dtl.pensionerCode=rqst.pensionerCode \n");
			lSBQuery.append("AND hdr.locationCode=:locationCode \n");
			if (lStrSortBy != null && !"-1".equals(lStrSortBy)) {
				if (bundleCaseConst.getString("ORDERBY.ACCOUNTNO").equals(lStrSortBy)) {
					lSBQuery.append("ORDER BY cast(dtl.accountNo as double) ");
				}
				if (bundleCaseConst.getString("ORDERBY.VOLUMEPAGENO").equals(lStrSortBy)) {
					lSBQuery.append("ORDER BY cast(rqst.ledgerNo,int),cast(rqst.pageNo,int) ");
				}
			} else {
				lSBQuery.append("ORDER BY rqst.ropType,cast(rqst.ledgerNo,int),cast(rqst.pageNo,int),dtl.headCode ");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("billNo", lLngBillNo);
			lQuery.setString("locationCode", lStrLocCode);
			lLstMonthlyPensionBillVO = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstMonthlyPensionBillVO;
	}

	public List<MonthlyPensionBillVO> getMonthlyPensionBillSummary(Long lLngBillNo, String lStrLocCode) throws Exception {

		List<MonthlyPensionBillVO> lLstMonthlyPensionBillVO = null;
		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		try {
			lLstMonthlyPensionBillVO = new ArrayList<MonthlyPensionBillVO>();
			lSBQuery.append("SELECT new com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO(count(dtl.pensionerName),sum(dtl.allcationBf1436),sum(dtl.allcationBf11156),sum(dtl.allcationAf11156),sum(dtl.allcationAf10560),sum(dtl.allcationAfZp),sum(dtl.ir1Amount),sum(dtl.dpAmount),sum(dtl.ir2Amount),sum(dtl.tiAmount),sum(dtl.ir3Amount),"
					+ "sum(dtl.grossAmount),sum(dtl.netAmount),sum(dtl.tiArrearAmount),sum(dtl.arrearAmount),mph.mainCatCode,mph.mainCatDesc,sum(dtl.recoveryAmount),sum(dtl.totalArrearAmt)) \n");
			lSBQuery.append("FROM TrnBillRegister reg,TrnPensionBillHdr hdr,TrnPensionBillDtls dtl,TrnPensionRqstHdr rqst,MstPensionHeadcode mph \n");
			lSBQuery.append("WHERE hdr.billNo=:billNo \n");
			lSBQuery.append("AND reg.billNo=hdr.billNo \n");
			lSBQuery.append("AND hdr.trnPensionBillHdrId=dtl.trnPensionBillHdrId \n");
			lSBQuery.append("AND dtl.pensionerCode=rqst.pensionerCode \n");
			lSBQuery.append("AND dtl.headCode=mph.headCode \n");
			lSBQuery.append("AND hdr.locationCode=:locationCode \n");
			// lSBQuery.append("GROUP BY dtl.headCode,mph.series \n");
			lSBQuery.append("GROUP BY mph.mainCatCode,mph.mainCatDesc \n");
			lSBQuery.append("ORDER BY mph.mainCatCode,mph.mainCatDesc ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("billNo", lLngBillNo);
			lQuery.setString("locationCode", lStrLocCode);
			lLstMonthlyPensionBillVO = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstMonthlyPensionBillVO;
	}

	public List<MonthlyPensionBillVO> getMonthlyPensionBillVOListByChngStmntId(Long lLngChngRqstId, String lStrLocCode, String lStrSortBy) throws Exception {

		List<MonthlyPensionBillVO> lLstMonthlyPensionBillVO = null;
		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		try {
			lLstMonthlyPensionBillVO = new ArrayList<MonthlyPensionBillVO>();
			lSBQuery.append("SELECT new com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO(pcd.headCode,rqst.ledgerNo,rqst.pageNo,pcd.ppoNo,pcd.pensionerName,pcd.accountNo,"
					+ "pcd.allcationBf1436,pcd.allcationBf11156,pcd.allcationAf11156,pcd.allcationAf10560,pcd.allcationAfZp,pcd.ir1Amount,pcd.dpAmount,pcd.ir2Amount,pcd.tiAmount,pcd.ir3Amount,"
					+ "pcd.grossAmount,pcd.netAmount,pcd.tiArrearAmount,pcd.arrearAmount,pcd.recoveryAmount,pcd.peonAllowance,pcd.medicalAllowenceAmount,pcd.other1,pcd.otherBenefits,pcd.totalArrearAmt) \n");
			lSBQuery.append("FROM TrnMonthlyChangeRqst mcr,TrnPensionChangeHdr pch,TrnPensionChangeDtls pcd,TrnPensionRqstHdr rqst \n");
			lSBQuery.append("WHERE mcr.changeRqstId=:changeRqstId \n");
			lSBQuery.append("AND mcr.changeRqstId=pch.changeRqstId \n");
			lSBQuery.append("AND pch.trnPensionChangeHdrId=pcd.trnPensionChangeHdrId \n");
			lSBQuery.append("AND pcd.pensionerCode=rqst.pensionerCode \n");
			lSBQuery.append("AND pch.locationCode=:locationCode \n");
			if (lStrSortBy != null && !"-1".equals(lStrSortBy)) {
				if (bundleCaseConst.getString("ORDERBY.ACCOUNTNO").equals(lStrSortBy)) {
					lSBQuery.append("ORDER BY cast(pcd.accountNo as double) ");
				}
				if (bundleCaseConst.getString("ORDERBY.VOLUMEPAGENO").equals(lStrSortBy)) {
					lSBQuery.append("ORDER BY cast(rqst.ledgerNo,int),cast(rqst.pageNo,int) ");
				}
			} else {
				lSBQuery.append("ORDER BY rqst.ropType,cast(rqst.ledgerNo,int),cast(rqst.pageNo,int),pcd.headCode ");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("changeRqstId", lLngChngRqstId);
			lQuery.setString("locationCode", lStrLocCode);
			lLstMonthlyPensionBillVO = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstMonthlyPensionBillVO;
	}

	public List<MonthlyPensionBillVO> getMonthlyPensionBillSummaryByChngStmntId(Long lLngChngRqstId, String lStrLocCode) throws Exception {

		List<MonthlyPensionBillVO> lLstMonthlyPensionBillVO = null;
		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		try {
			lLstMonthlyPensionBillVO = new ArrayList<MonthlyPensionBillVO>();
			lSBQuery.append("SELECT new com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO(count(pcd.pensionerName),sum(pcd.allcationBf1436),sum(pcd.allcationBf11156),sum(pcd.allcationAf11156),sum(pcd.allcationAf10560),sum(pcd.allcationAfZp),sum(pcd.ir1Amount),sum(pcd.dpAmount),sum(pcd.ir2Amount),sum(pcd.tiAmount),sum(pcd.ir3Amount),"
					+ "sum(pcd.grossAmount),sum(pcd.netAmount),sum(pcd.tiArrearAmount),sum(pcd.arrearAmount),mph.mainCatCode,mph.mainCatDesc,sum(pcd.recoveryAmount),sum(pcd.totalArrearAmt)) \n");
			lSBQuery.append("FROM  TrnMonthlyChangeRqst mcr,TrnPensionChangeHdr pch,TrnPensionChangeDtls pcd,TrnPensionRqstHdr rqst,MstPensionHeadcode mph \n");
			lSBQuery.append("WHERE mcr.changeRqstId=:changeRqstId \n");
			lSBQuery.append("AND mcr.changeRqstId=pch.changeRqstId \n");
			lSBQuery.append("AND pch.trnPensionChangeHdrId=pcd.trnPensionChangeHdrId \n");
			lSBQuery.append("AND pcd.pensionerCode=rqst.pensionerCode \n");
			lSBQuery.append("AND pcd.headCode=mph.headCode \n");
			lSBQuery.append("AND pch.locationCode=:locationCode \n");
			// lSBQuery.append("GROUP BY dtl.headCode,mph.series \n");
			lSBQuery.append("GROUP BY mph.mainCatCode,mph.mainCatDesc \n");
			lSBQuery.append("ORDER BY mph.mainCatCode,mph.mainCatDesc ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("changeRqstId", lLngChngRqstId);
			lQuery.setString("locationCode", lStrLocCode);
			lLstMonthlyPensionBillVO = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstMonthlyPensionBillVO;
	}

	public List<Object[]> getPnsrWithPreMonthHeldStatus(List<String> lLstPnsrCode, Integer lPrevMonth) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstPnsrDtls = null;
		List<Long> lLngLstPnsrCode = new ArrayList<Long>();
		for (String lStrPnsrCode : lLstPnsrCode) {
			lLngLstPnsrCode.add(Long.valueOf(lStrPnsrCode));
		}
		ghibSession = getSession();
		SimpleDateFormat lSdf1 = new SimpleDateFormat("yyyyMM");
		Calendar lObjCalendar = new GregorianCalendar();
		lObjCalendar.setTime(lSdf1.parse(lPrevMonth.toString()));
		int maxDays = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		lObjCalendar.set(Calendar.DATE, maxDays); // setting date to last day of
		// previous month
		try {
			lSBQuery.append("select opcd.pensionerCode,opcd.crntFieldValue \n");
			lSBQuery.append("from TrnPensionCorrectionDtls opcd \n");
			lSBQuery.append("where  \n");
			lSBQuery.append("opcd.fieldType = :lPnsnCaseStatus  \n");
			lSBQuery.append("and opcd.approvedDate <= :lPrevMonthEndDate  \n");
			lSBQuery.append("and (opcd.pensionerCode,opcd.createdDate) in   \n");
			lSBQuery.append("(select ipcd.pensionerCode,max(ipcd.createdDate) \n");
			lSBQuery.append("from TrnPensionCorrectionDtls ipcd \n");
			lSBQuery.append("where  \n");
			lSBQuery.append("ipcd.fieldType = :lPnsnCaseStatus \n");
			lSBQuery.append("and ipcd.approvedDate <= :lPrevMonthEndDate  \n");
			lSBQuery.append("and ipcd.pensionerCode in (:lLngLstPnsrCode) \n");
			lSBQuery.append("group by ipcd.pensionerCode ) \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lPnsnCaseStatus", "Pension Case Status");
			lQuery.setDate("lPrevMonthEndDate", lObjCalendar.getTime());
			lQuery.setParameterList("lLngLstPnsrCode", lLngLstPnsrCode);
			lLstPnsrDtls = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstPnsrDtls;
	}

	public List<Object[]> getPnsrAddedToBranchDtls(List<String> lLstPnsrCode, Integer lPrevMonth) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstPnsrDtls = null;
		ghibSession = getSession();
		try {
			lSBQuery.append("select pbd.pensionerCode,pbh.locationCode \n");
			lSBQuery.append("from TrnPensionBillDtls pbd,TrnPensionBillHdr pbh \n");
			lSBQuery.append("where  \n");
			lSBQuery.append(" pbd.trnPensionBillHdrId = pbh.trnPensionBillHdrId  \n");
			lSBQuery.append("and pbd.payForMonth = :lPrevMonth \n");
			lSBQuery.append("and pbd.pensionerCode in (:lLstPnsrCode) ");
			lSBQuery.append("and pbh.billType = :lBillType \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("lLstPnsrCode", lLstPnsrCode);
			lQuery.setInteger("lPrevMonth", lPrevMonth);
			lQuery.setString("lBillType", bundleConst.getString("PPMT.MONTHLY"));
			lLstPnsrDtls = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstPnsrDtls;
	}

	public List<Object[]> getPnsrRemovedFromBranchDtls(List<String> lLstPnsrCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstPnsrDtls = null;
		ghibSession = getSession();
		try {
			lSBQuery.append("select prh.pensionerCode,prh.locationCode,prh.status,prh.seenFlag,prh.endDate \n");
			lSBQuery.append("from TrnPensionRqstHdr prh \n");
			lSBQuery.append("where  \n");
			lSBQuery.append("prh.pensionerCode in (:lLstPnsrCode) \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("lLstPnsrCode", lLstPnsrCode);
			lLstPnsrDtls = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstPnsrDtls;
	}

	public List<MonthlyPensionBillVO> getMonthlyPensionBillVOListForGetData(Long lLngChangeRqstId, String lStrLocCode) throws Exception {

		List<MonthlyPensionBillVO> lLstMonthlyPensionBillVO = null;
		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		try {
			lLstMonthlyPensionBillVO = new ArrayList<MonthlyPensionBillVO>();
			lSBQuery.append("SELECT new com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO(dtl.headCode,dtl.ledgerNo,dtl.pageNo,dtl.ppoNo,dtl.pensionerName,dtl.accountNo,"
					+ "dtl.allcationBf1436,dtl.allcationBf11156,dtl.allcationAf11156,dtl.allcationAf10560,dtl.allcationAfZp,dtl.ir1Amount,dtl.dpAmount,dtl.ir2Amount,dtl.tiAmount,dtl.ir3Amount,"
					+ "dtl.grossAmount,dtl.netAmount,dtl.tiArrearAmount,dtl.arrearAmount) \n");
			lSBQuery.append("FROM TrnMonthlyChangeRqst rqst,TrnPensionChangeHdr hdr,TrnPensionChangeDtls dtl \n");
			lSBQuery.append("WHERE rqst.changeRqstId=hdr.changeRqstId \n");
			lSBQuery.append("AND hdr.trnPensionChangeHdrId=dtl.trnPensionChangeHdrId \n");
			lSBQuery.append("AND rqst.changeRqstId=:changeRqstId \n");
			lSBQuery.append("AND hdr.locationCode=:locationCode");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("changeRqstId", lLngChangeRqstId);
			lQuery.setString("locationCode", lStrLocCode);
			lLstMonthlyPensionBillVO = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstMonthlyPensionBillVO;
	}

	public List getMonthlyDataForAuditRegister(String lStrPensionerCode, String lStrLocationCode) throws Exception {

		List lLstResult = null;
		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		try {
			lLstResult = new ArrayList();
			lSBQuery.append("SELECT dtls.payForMonth,SUM(dtls.netAmount),SUM(dtls.arrearAmount),SUM(dtls.recoveryAmount) ");
			lSBQuery.append("FROM TrnBillRegister reg,TrnPensionBillHdr hdr,TrnPensionBillDtls dtls ");
			lSBQuery.append("WHERE dtls.pensionerCode=:pensionerCode ");
			lSBQuery.append("AND reg.billNo=hdr.billNo ");
			lSBQuery.append("AND hdr.trnPensionBillHdrId=dtls.trnPensionBillHdrId ");
			lSBQuery.append("AND hdr.locationCode=:locationCode ");
			lSBQuery.append("GROUP BY dtls.payForMonth ");
			// lSBQuery.append("ORDER BY dtls.payForMonth DESC");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("locationCode", lStrLocationCode);
			lQuery.setParameter("pensionerCode", lStrPensionerCode);
			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public List<Object[]> getMonthlyBillDtlsByBillNo(List<Long> lLstLngBillNo) throws Exception {

		List<Object[]> lLstResult = null;
		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		try {
			lLstResult = new ArrayList();
			lSBQuery.append("Select bankCode,branchCode,forMonth \n");
			lSBQuery.append("from TrnPensionBillHdr  \n");
			lSBQuery.append("where \n");
			lSBQuery.append("billNo in (:lLstLngBillNo) \n");
			lSBQuery.append("and billType = :lStrBillType \n");
			lSBQuery.append("Group by bankCode,branchCode,forMonth ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("lLstLngBillNo", lLstLngBillNo);
			lQuery.setString("lStrBillType", bundleConst.getString("RECOVERY.MONTHLY"));
			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public void discardChngStmntOnMonthlyBillDiscard(String lStrBankCode, String lStrBranchCode, Integer lIntForMonth) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		try {
			lSBQuery.append("Update TrnMonthlyChangeRqst mcr \n");
			lSBQuery.append("Set mcr.status = :lStatusTo \n");
			lSBQuery.append("WHERE \n");
			lSBQuery.append("mcr.bankCode=:lStrBankCode \n");
			lSBQuery.append("and mcr.branchCode=:lStrBranchCode \n");
			lSBQuery.append("and mcr.forMonth=:lIntForMonth \n");
			lSBQuery.append("and mcr.status=:lStatusFrom \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lStrBankCode", lStrBankCode);
			lQuery.setString("lStrBranchCode", lStrBranchCode);
			lQuery.setInteger("lIntForMonth", lIntForMonth);
			lQuery.setString("lStatusTo", bundleConst.getString("CHANGSTMNTSTATUS.DISCARDED"));
			lQuery.setString("lStatusFrom", bundleConst.getString("CHANGSTMNTSTATUS.APPROVED"));
			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
	}

	/*
	 * This method is used to get previous month bill's status. (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAO#getPrevMonthBillStatus
	 * (java.lang.String, java.lang.String, java.lang.Integer, java.lang.String)
	 */
	public List<Object[]> getPrevMonthBillStatus(List<String> lLstBranchCode, Integer lIntForMonth, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		ghibSession = getSession();
		try {
			lSBQuery.append("Select mcr.status,tbr.curr_bill_status,mcr.branch_code \n");
			lSBQuery.append("From \n");
			lSBQuery.append("trn_monthly_change_rqst mcr  \n");
			lSBQuery.append("left outer join trn_bill_register tbr on  mcr.bill_No = tbr.bill_No \n");
			lSBQuery.append("where \n");
			lSBQuery.append("mcr.branch_Code in (:lLstBranchCode) \n");
			lSBQuery.append("and mcr.for_Month = :lIntForMonth \n");
			lSBQuery.append("and mcr.location_Code = :lStrLocCode \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameterList("lLstBranchCode", lLstBranchCode);
			lQuery.setInteger("lIntForMonth", lIntForMonth);
			lQuery.setString("lStrLocCode", lStrLocCode);
			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public Map<String, List<TrnPensionRecoveryDtls>> getRecoveryDtlsForChngStmnt(List<String> lLstPnsrCode, String lStrForMonth) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<TrnPensionRecoveryDtls> lLstResult = null;
		ghibSession = getSession();
		String lStrRcvryFrom = bundleConst.getString("RECOVERY.MONTHLY");
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtlsObj = null;
		Map<String, List<TrnPensionRecoveryDtls>> lMapRcvryDtl = new HashMap<String, List<TrnPensionRecoveryDtls>>();
		double lAmnt = 0;
		try {
			lSBQuery.append("Select prd \n");
			lSBQuery.append("From \n");
			lSBQuery.append("TrnPensionRecoveryDtls prd  \n");
			lSBQuery.append("where \n");
			lSBQuery.append("prd.pensionerCode in (:lLstPnsrCode) \n");
			lSBQuery.append("and prd.recoveryFromFlag = :lRcvryFrom \n");
			lSBQuery.append("and ((prd.fromMonth <= :lForMonth and prd.toMonth >= :lForMonth) OR (prd.fromMonth = :lForMonth AND prd.toMonth IS NULL)) \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("lLstPnsrCode", lLstPnsrCode);
			lQuery.setInteger("lForMonth", Integer.valueOf(lStrForMonth));
			lQuery.setString("lRcvryFrom", lStrRcvryFrom);
			lLstResult = lQuery.list();

			if (lLstResult != null && !lLstResult.isEmpty()) {
				for (TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtls : lLstResult) {
					lLstTrnPensionRecoveryDtlsObj = lMapRcvryDtl.get(lObjTrnPensionRecoveryDtls.getPensionerCode());
					lAmnt = lObjTrnPensionRecoveryDtls.getAmount().doubleValue();
					if (lLstTrnPensionRecoveryDtlsObj != null) {
						if (lAmnt > 0) {
							lLstTrnPensionRecoveryDtlsObj.add(lObjTrnPensionRecoveryDtls);
						}
					} else {
						lLstTrnPensionRecoveryDtlsObj = new ArrayList<TrnPensionRecoveryDtls>();
						if (lAmnt > 0) {
							lLstTrnPensionRecoveryDtlsObj.add(lObjTrnPensionRecoveryDtls);
						}
					}
					lMapRcvryDtl.put(lObjTrnPensionRecoveryDtls.getPensionerCode(), lLstTrnPensionRecoveryDtlsObj);
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lMapRcvryDtl;
	}

	public List<BigInteger> getBillNosFromBnkBrnchPayMonth(String lStrbankCode, String lStrBranchCode, Integer lIntForMonth, String lStrLocationCode) throws Exception {

		Session ghibSession = getSession();
		StringBuffer lSBQuery = new StringBuffer();
		List<BigInteger> lLstBillNos = null;
		List<Short> lLstRjctedBillStatus = new ArrayList<Short>();
		Short lShRjctedStatus = -1;
		Short lShArchivedStatus = -15;
		Short lShDiscardedStatus = 60;
		try {
			lLstRjctedBillStatus.add(lShRjctedStatus);
			lLstRjctedBillStatus.add(lShArchivedStatus);
			lLstRjctedBillStatus.add(lShDiscardedStatus);

			lSBQuery.append("select hdr.bill_no \n");
			lSBQuery.append("from trn_pension_bill_hdr hdr, trn_bill_register reg \n");
			lSBQuery.append("where hdr.bill_no=reg.bill_no \n");
			lSBQuery.append("and hdr.bank_code=:bankCode \n");
			lSBQuery.append("and hdr.branch_code=:branchCode \n");
			lSBQuery.append("and hdr.for_month=:forMonth \n");
			lSBQuery.append("and hdr.location_code=:locationCode \n");
			lSBQuery.append("and reg.SUBJECT_ID=44 \n");
			lSBQuery.append("and reg.curr_bill_status not in (:statusList) \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("locationCode", lStrLocationCode);
			lQuery.setString("bankCode", lStrbankCode);
			lQuery.setString("branchCode", lStrBranchCode);
			lQuery.setInteger("forMonth", lIntForMonth);
			lQuery.setParameterList("statusList", lLstRjctedBillStatus);

			lLstBillNos = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstBillNos;
	}

	public List getBankBranchFromAuditor(Integer lIntPostId, String lStrLocationCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		ghibSession = getSession();

		try {
			lSBQuery.append("select rab.bank_code,rab.branch_code \n");
			lSBQuery.append("from rlt_auditor_bank rab \n");
			lSBQuery.append("where rab.post_id=:postId \n");
			lSBQuery.append("and rab.location_code=:locationCode \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("locationCode", lStrLocationCode);
			lQuery.setInteger("postId", lIntPostId);

			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public Map<Long, Object[]> getPartyDtlsByChngRqstIds(List<Long> lLstChngRqstId, String lStrLocCode, Long lLngLangId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		ghibSession = getSession();
		Object[] lArrPartyDtls = null;
		String lStrPartyName = "";
		String lStrBankName = "";
		String lStrBranchName = "";
		Map<Long, Object[]> lMapChngRqstIdParyDtls = new HashMap<Long, Object[]>();
		try {
			lSBQuery.append("select \n");
			lSBQuery.append("mcr.changeRqstId,mb.bankName,rbb.branchName,rbb.micrCode \n");
			lSBQuery.append("from \n");
			lSBQuery.append("TrnMonthlyChangeRqst mcr, \n");
			lSBQuery.append("MstBank mb,  \n");
			lSBQuery.append("RltBankBranch rbb  \n");
			lSBQuery.append("where  \n");
			lSBQuery.append("mcr.bankCode = mb.bankCode  \n");
			lSBQuery.append("and mcr.branchCode = rbb.branchCode  \n");
			lSBQuery.append("and mb.bankCode = rbb.bankCode  \n");
			lSBQuery.append("and mb.langId = :langId  \n");
			lSBQuery.append("and rbb.langId = :langId  \n");
			lSBQuery.append("and rbb.locationCode = :locationCode  \n");
			lSBQuery.append("and mcr.changeRqstId in (:lLstChngRqstId)  \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameterList("lLstChngRqstId", lLstChngRqstId);
			lQuery.setString("locationCode", lStrLocCode);
			lQuery.setLong("langId", lLngLangId);

			lLstResult = lQuery.list();
			if (lLstResult != null && !lLstResult.isEmpty()) {
				for (Object[] lArrObj : lLstResult) {
					lStrBankName = (lArrObj[1] != null) ? lArrObj[1].toString() : "";
					lStrBranchName = (lArrObj[2] != null) ? lArrObj[2].toString() : "";
					lStrPartyName = lStrBankName + "," + lStrBranchName;
					lArrPartyDtls = new Object[2];
					lArrPartyDtls[0] = lStrPartyName;
					lArrPartyDtls[1] = lArrObj[3];
					lMapChngRqstIdParyDtls.put((Long) lArrObj[0], lArrPartyDtls);
				}
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lMapChngRqstIdParyDtls;
	}

	public List<String> getAllSchemeCode() throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> lLstResult = null;
		ghibSession = getSession();
		String lStrPartyName = "";
		String lStrBankName = "";
		String lStrBranchName = "";
		Map<Long, Object[]> lMapChngRqstIdParyDtls = new HashMap<Long, Object[]>();
		try {
			lSBQuery.append("select \n");
			lSBQuery.append("distinct schemeCode \n");
			lSBQuery.append("from \n");
			lSBQuery.append("RltPensionHeadcodeChargable  \n");
			lSBQuery.append("Where \n");
			lSBQuery.append("billType = :lBillType \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lBillType", bundleConst.getString("RECOVERY.PENSION"));
			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public String getSchemeNameBySchemeCode(String lStrSchemeCode, String lStrLangId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> lLstResult = null;
		ghibSession = getSession();
		Map<Long, Object[]> lMapChngRqstIdParyDtls = new HashMap<Long, Object[]>();
		String lStrSchemeName = "";
		try {
			lSBQuery.append("select \n");
			lSBQuery.append("schemeName \n");
			lSBQuery.append("from \n");
			lSBQuery.append("MstScheme  \n");
			lSBQuery.append("Where \n");
			lSBQuery.append("schemeCode = :lSchemeCode \n");
			lSBQuery.append("and langId = :lLngId \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lSchemeCode", lStrSchemeCode);
			lQuery.setString("lLngId", lStrLangId);
			lLstResult = lQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				lStrSchemeName = lLstResult.get(0);
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lStrSchemeName;
	}

	public List<TrnPensionChangeHdr> getChngHdrDtlsBySchemeCode(String lStrSchemeCode, String lStrPayMode, String lStrForMonth, String lLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<TrnPensionChangeHdr> lLstResult = null;
		ghibSession = getSession();
		String lStrSchemeName = "";
		try {
			lSBQuery.append("select pch \n");
			lSBQuery.append("from \n");
			lSBQuery.append("TrnMonthlyChangeRqst mcr, \n");
			lSBQuery.append("TrnPensionChangeHdr pch, \n");
			lSBQuery.append("RltBankBranch rbb \n");
			lSBQuery.append("Where \n");
			lSBQuery.append("mcr.changeRqstId = pch.changeRqstId \n");
			lSBQuery.append("and rbb.branchCode = pch.branchCode \n");
			lSBQuery.append("and rbb.locationCode = :locCode \n");
			lSBQuery.append("and pch.locationCode = :locCode \n");
			lSBQuery.append("and pch.forMonth = :lForMonth \n");
			lSBQuery.append("and mcr.status = :lStatus \n");
			lSBQuery.append("and pch.schemeCode = :lSchemeCode \n");
			if (bundleCaseConst.getString("PPMT.PAYMODEECS").equals(lStrPayMode)) {
				// lSBQuery.append("and rbb.micrCode is not null \n");
				lSBQuery.append("and rbb.ecsBranch = 'Y' \n");
			} else if (bundleCaseConst.getString("PPMT.PAYMODECHQ").equals(lStrPayMode)) {
				// lSBQuery.append("and rbb.micrCode is null \n");
				lSBQuery.append("and rbb.ecsBranch = 'N' \n");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lSchemeCode", lStrSchemeCode);
			lQuery.setString("locCode", lLocCode);
			lQuery.setString("lForMonth", lStrForMonth);
			lQuery.setString("lStatus", bundleConst.getString("CHANGSTMNTSTATUS.APPROVED"));

			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public List<String> getBranchCodeOfGeneratedBills(String lStrSchemeCode, String lStrPayMode, String lStrForMonth, String lLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> lLstResult = null;
		ghibSession = getSession();
		String lStrSchemeName = "";
		Short lShRjctedStatus = -1;
		Short lShArchivedStatus = -15;
		Short lShDiscardedStatus = 60;
		List<Short> lLstRjctedBillStatus = new ArrayList<Short>();
		try {
			lLstRjctedBillStatus.add(lShRjctedStatus);
			lLstRjctedBillStatus.add(lShArchivedStatus);
			lLstRjctedBillStatus.add(lShDiscardedStatus);
			lSBQuery.append("select bcm.chngStmntIdList \n");
			lSBQuery.append("from \n");
			lSBQuery.append("TrnBillRegister tbr, \n");
			lSBQuery.append("TrnBillChangeStmntMpg bcm \n");
			// lSBQuery.append("RltBankBranch rbb \n");
			lSBQuery.append("Where \n");
			lSBQuery.append("bcm.billNo = tbr.billNo  \n");
			// lSBQuery.append("and rbb.branchCode = pbh.branchCode \n");
			// lSBQuery.append("and rbb.locationCode = :locCode \n");
			lSBQuery.append("and tbr.schemeNo = :lSchemeCode \n");
			// lSBQuery.append("and tbr.payMode = :lPayMode \n");
			lSBQuery.append("and tbr.forMonth = :lForMonth \n");
			lSBQuery.append("and tbr.subjectId = :lMonthlySubjectId \n");
			lSBQuery.append("and tbr.locationCode = :locCode \n");
			lSBQuery.append("and bcm.locationCode = :locCode \n");
			lSBQuery.append("and tbr.currBillStatus not in (:lLstBillStatus) \n");
			// if
			// (bundleCaseConst.getString("PPMT.PAYMODEECS").equals(lStrPayMode))
			// {
			// lSBQuery.append("and rbb.micrCode is not null \n");
			// } else if
			// (bundleCaseConst.getString("PPMT.PAYMODECHQ").equals(lStrPayMode))
			// {
			// lSBQuery.append("and rbb.micrCode is null \n");
			// }
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lSchemeCode", lStrSchemeCode);
			// lQuery.setString("lPayMode", lStrPayMode);
			lQuery.setString("locCode", lLocCode);
			lQuery.setString("lForMonth", lStrForMonth);
			lQuery.setLong("lMonthlySubjectId", 44L);
			lQuery.setParameterList("lLstBillStatus", lLstRjctedBillStatus);

			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public Integer getViewMonthlyPensionBillListCount(String lLocCode, String lStrSearchCrt1, String lStrSearchCrt2, String lStrSearchCrt1Value, String lStrSearchCrt2Value, Integer lIntForMonth)
			throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List lLstResult = null;
		Object[] lObjArr = null;
		ghibSession = getSession();
		String lStrForMonth = "";
		SimpleDateFormat lSdf1 = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat lSdf2 = new SimpleDateFormat("MMM-yyyy");
		SimpleDateFormat lSdf3 = new SimpleDateFormat("dd/MM/yyyy");
		Integer totalRecords = 0;
		try {
			lSBQuery.append("select count(*) \n");
			lSBQuery.append("from \n");
			lSBQuery.append("TrnBillRegister tbr \n");
			lSBQuery.append("Where \n");
			lSBQuery.append("tbr.schemeNo is not null \n");
			lSBQuery.append("and tbr.subjectId = :lMonthlySubId \n");
			lSBQuery.append("and tbr.locationCode = :locCode \n");

			// ---If search criteria available then showing reocrds are per
			// search criteria else showing records of current monthe year only.
			if ((lStrSearchCrt1.length() > 0 && !"-1".equals(lStrSearchCrt1)) && (lStrSearchCrt1Value.length() > 0 && !"-1".equals(lStrSearchCrt1Value))
					|| (lStrSearchCrt2.length() > 0 && !"-1".equals(lStrSearchCrt2)) && (lStrSearchCrt2Value.length() > 0 && !"-1".equals(lStrSearchCrt2Value))) {
				if ((lStrSearchCrt1.length() > 0 && !"-1".equals(lStrSearchCrt1)) && (lStrSearchCrt1Value.length() > 0 && !"-1".equals(lStrSearchCrt1Value))) {
					lSBQuery.append("and " + lStrSearchCrt1 + " = :lStrSearchCrt1Val \n");
				}
				if ((lStrSearchCrt2.length() > 0 && !"-1".equals(lStrSearchCrt2)) && (lStrSearchCrt2Value.length() > 0 && !"-1".equals(lStrSearchCrt2Value))) {
					lSBQuery.append("and " + lStrSearchCrt2 + " = :lStrSearchCrt2Val \n");
				}
			} else {
				lSBQuery.append("and tbr.forMonth = :lForMonth \n");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("locCode", lLocCode);
			lQuery.setShort("lMonthlySubId", (short) 44);
			if ((lStrSearchCrt1.length() > 0 && !"-1".equals(lStrSearchCrt1)) && (lStrSearchCrt1Value.length() > 0 && !"-1".equals(lStrSearchCrt1Value))
					|| (lStrSearchCrt2.length() > 0 && !"-1".equals(lStrSearchCrt2)) && (lStrSearchCrt2Value.length() > 0 && !"-1".equals(lStrSearchCrt2Value))) {
				if ((lStrSearchCrt1.length() > 0 && !"-1".equals(lStrSearchCrt1)) && (lStrSearchCrt1Value.length() > 0 && !"-1".equals(lStrSearchCrt1Value))) {
					if ("tbr.billCntrlNo".equals(lStrSearchCrt1) || "tbr.schemeNo".equals(lStrSearchCrt1) || "tbr.payMode".equals(lStrSearchCrt1)) {
						lQuery.setString("lStrSearchCrt1Val", lStrSearchCrt1Value);
					} else if ("tbr.forMonth".endsWith(lStrSearchCrt1)) {
						lQuery.setInteger("lStrSearchCrt1Val", Integer.valueOf(lStrSearchCrt1Value));
					} else if ("tbr.billDate".endsWith(lStrSearchCrt1)) {
						lQuery.setDate("lStrSearchCrt1Val", lSdf3.parse(lStrSearchCrt1Value));
					} else if ("tbr.currBillStatus".endsWith(lStrSearchCrt1)) {
						lQuery.setInteger("lStrSearchCrt1Val", Integer.parseInt(lStrSearchCrt1Value));
					}
				}
				if ((lStrSearchCrt2.length() > 0 && !"-1".equals(lStrSearchCrt2)) && (lStrSearchCrt2Value.length() > 0 && !"-1".equals(lStrSearchCrt2Value))) {
					if ("tbr.billCntrlNo".equals(lStrSearchCrt2) || "tbr.schemeNo".equals(lStrSearchCrt2) || "tbr.payMode".equals(lStrSearchCrt2)) {
						lQuery.setString("lStrSearchCrt2Val", lStrSearchCrt2Value);
					} else if ("tbr.forMonth".endsWith(lStrSearchCrt2)) {
						lQuery.setInteger("lStrSearchCrt2Val", Integer.valueOf(lStrSearchCrt2Value));
					} else if ("tbr.billDate".endsWith(lStrSearchCrt2)) {
						lQuery.setDate("lStrSearchCrt2Val", lSdf3.parse(lStrSearchCrt2Value));
					} else if ("tbr.currBillStatus".endsWith(lStrSearchCrt2)) {
						lQuery.setInteger("lStrSearchCrt2Val", Integer.parseInt(lStrSearchCrt2Value));
					}
				}
			} else {
				lQuery.setInteger("lForMonth", lIntForMonth);
			}
			lLstResult = lQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				totalRecords = Integer.parseInt(lLstResult.get(0).toString());
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return totalRecords;
	}

	public List<Object[]> getViewMonthlyPensionBillListDtls(String lLocCode, Map displayTag, String lStrSearchCrt1, String lStrSearchCrt2, String lStrSearchCrt1Value, String lStrSearchCrt2Value,
			Integer lIntForMonth) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		Object[] lObjArr = null;
		ghibSession = getSession();
		String lStrForMonth = "";
		SimpleDateFormat lSdf1 = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat lSdf2 = new SimpleDateFormat("MMM-yyyy");
		SimpleDateFormat lSdf3 = new SimpleDateFormat("dd/MM/yyyy");
		Integer orderbypara = null;
		String[] columnValues = null;
		try {
			columnValues = new String[]{"", "tbr.billCntrlNo", "tbr.schemeNo", "tbr.payMode", "tbr.forMonth", "tbr.billDate", "tbr.billGrossAmount", "tbr.deductionA", "tbr.billNetAmount",
					"tbr.currBillStatus"};
			lSBQuery.append("select tbr.schemeNo,tbr.payMode,tbr.forMonth,tbr.billDate,tbr.billGrossAmount,tbr.deductionA,tbr.billNetAmount,tbr.currBillStatus,tbr.billNo,tbr.forMonth,tbr.billCntrlNo \n");
			lSBQuery.append("from \n");
			lSBQuery.append("TrnBillRegister tbr \n");
			lSBQuery.append("Where \n");
			lSBQuery.append("tbr.schemeNo is not null \n");
			lSBQuery.append("and tbr.subjectId = :lMonthlySubId \n");
			lSBQuery.append("and tbr.locationCode = :locCode \n");
			// ---If search criteria available then showing reocrds are per
			// search criteria else showing records of current monthe year only.
			if ((lStrSearchCrt1.length() > 0 && !"-1".equals(lStrSearchCrt1)) && (lStrSearchCrt1Value.length() > 0 && !"-1".equals(lStrSearchCrt1Value))
					|| (lStrSearchCrt2.length() > 0 && !"-1".equals(lStrSearchCrt2)) && (lStrSearchCrt2Value.length() > 0 && !"-1".equals(lStrSearchCrt2Value))) {
				if ((lStrSearchCrt1.length() > 0 && !"-1".equals(lStrSearchCrt1)) && (lStrSearchCrt1Value.length() > 0 && !"-1".equals(lStrSearchCrt1Value))) {
					lSBQuery.append("and " + lStrSearchCrt1 + " = :lStrSearchCrt1Val \n");
				}
				if ((lStrSearchCrt2.length() > 0 && !"-1".equals(lStrSearchCrt2)) && (lStrSearchCrt2Value.length() > 0 && !"-1".equals(lStrSearchCrt2Value))) {
					lSBQuery.append("and " + lStrSearchCrt2 + " = :lStrSearchCrt2Val \n");
				}
			} else {
				lSBQuery.append("and tbr.forMonth = :lForMonth \n");
			}
			lSBQuery.append("Order By \n");
			String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag.get(Constants.KEY_SORT_ORDER) : "desc");

			if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				lSBQuery.append(columnValues[orderbypara.intValue()] + " " + orderString);
			} else {
				lSBQuery.append("tbr.billDate ");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("locCode", lLocCode);
			lQuery.setShort("lMonthlySubId", (short) 44);
			if ((lStrSearchCrt1.length() > 0 && !"-1".equals(lStrSearchCrt1)) && (lStrSearchCrt1Value.length() > 0 && !"-1".equals(lStrSearchCrt1Value))
					|| (lStrSearchCrt2.length() > 0 && !"-1".equals(lStrSearchCrt2)) && (lStrSearchCrt2Value.length() > 0 && !"-1".equals(lStrSearchCrt2Value))) {
				if ((lStrSearchCrt1.length() > 0 && !"-1".equals(lStrSearchCrt1)) && (lStrSearchCrt1Value.length() > 0 && !"-1".equals(lStrSearchCrt1Value))) {
					if ("tbr.billCntrlNo".equals(lStrSearchCrt1) || "tbr.schemeNo".equals(lStrSearchCrt1) || "tbr.payMode".equals(lStrSearchCrt1)) {
						lQuery.setString("lStrSearchCrt1Val", lStrSearchCrt1Value);
					} else if ("tbr.forMonth".endsWith(lStrSearchCrt1)) {
						lQuery.setInteger("lStrSearchCrt1Val", Integer.valueOf(lStrSearchCrt1Value));
					} else if ("tbr.billDate".endsWith(lStrSearchCrt1)) {
						lQuery.setDate("lStrSearchCrt1Val", lSdf3.parse(lStrSearchCrt1Value));
					} else if ("tbr.currBillStatus".endsWith(lStrSearchCrt1)) {
						lQuery.setInteger("lStrSearchCrt1Val", Integer.parseInt(lStrSearchCrt1Value));
					}
				}
				if ((lStrSearchCrt2.length() > 0 && !"-1".equals(lStrSearchCrt2)) && (lStrSearchCrt2Value.length() > 0 && !"-1".equals(lStrSearchCrt2Value))) {
					if ("tbr.billCntrlNo".equals(lStrSearchCrt2) || "tbr.schemeNo".equals(lStrSearchCrt2) || "tbr.payMode".equals(lStrSearchCrt2)) {
						lQuery.setString("lStrSearchCrt2Val", lStrSearchCrt2Value);
					} else if ("tbr.forMonth".endsWith(lStrSearchCrt2)) {
						lQuery.setInteger("lStrSearchCrt2Val", Integer.valueOf(lStrSearchCrt2Value));
					} else if ("tbr.billDate".endsWith(lStrSearchCrt2)) {
						lQuery.setDate("lStrSearchCrt2Val", lSdf3.parse(lStrSearchCrt2Value));
					} else if ("tbr.currBillStatus".endsWith(lStrSearchCrt2)) {
						lQuery.setInteger("lStrSearchCrt2Val", Integer.parseInt(lStrSearchCrt2Value));
					}
				}
			} else {
				lQuery.setInteger("lForMonth", lIntForMonth);
			}
			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lQuery.setFirstResult((pageNo.intValue() - 1) * Constants.PAGE_SIZE);
			lQuery.setMaxResults(Constants.PAGE_SIZE);
			lLstResult = lQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				for (Object[] lArrObj : lLstResult) {
					if (lArrObj[2] != null) {
						lStrForMonth = lArrObj[2].toString();
						lArrObj[2] = lSdf2.format(lSdf1.parse(lStrForMonth));
					}
				}
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	/**
	 * 
	 * <H3>Description -</H3> This method is used to reject all change
	 * statements of the branch codes passed in argument on rejection of monthy
	 * pension bill.
	 * 
	 * @author Shripal Soni
	 * @param lLstBranchCode
	 * @param lStrForMonth
	 * @param lStrLocCode
	 * @throws Exception
	 */
	public void rejectChangeStmntByBranchCodeOnRejectOfBill(List lLstBranchCode, String lStrForMonth, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List lLstResult = null;
		ghibSession = getSession();
		List<List<String>> lLstAllBranchCodeBatches = new ArrayList<List<String>>();
		int maxBatchSize = 900;
		List<String> lLstBranchCodeBatch = null;
		try {
			// --Preparing batches for bulk update starts <<<
			if (lLstBranchCode.size() > maxBatchSize) {
				int totalBatches = lLstBranchCode.size() / maxBatchSize;
				for (int cnt = 1; cnt <= totalBatches; cnt++) {
					lLstBranchCodeBatch = new ArrayList<String>();
					lLstBranchCodeBatch.addAll((lLstBranchCode.subList(maxBatchSize * (cnt - 1), (maxBatchSize * cnt))));
					lLstAllBranchCodeBatches.add(lLstBranchCodeBatch);
				}
				int remainingBranchCodes = lLstBranchCode.size() % maxBatchSize;
				if (remainingBranchCodes > 0) {
					lLstBranchCodeBatch = new ArrayList<String>();
					lLstBranchCodeBatch.addAll(lLstBranchCode.subList(maxBatchSize * totalBatches, (maxBatchSize * totalBatches) + remainingBranchCodes));
					lLstAllBranchCodeBatches.add(lLstBranchCodeBatch);
				}
			}
			// --Preparing batches for bulk update ends >>>>
			lSBQuery.append("update TrnMonthlyChangeRqst  \n");
			lSBQuery.append("set status = :lChngStmntStatus \n");
			lSBQuery.append("Where \n");
			lSBQuery.append("(branchCode in (:lLstBrnchCode1) \n");
			for (int i = 2; i <= lLstAllBranchCodeBatches.size(); i++) {
				lSBQuery.append(" or branchCode in (:lLstBrnchCode" + i + ") ");
			}
			lSBQuery.append(" ) \n");
			lSBQuery.append("and forMonth = :forMonth \n");
			lSBQuery.append("and locationCode = :locCode \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lChngStmntStatus", bundleConst.getString("CHANGSTMNTSTATUS.REJECTED"));
			lQuery.setString("locCode", lStrLocCode);
			lQuery.setInteger("forMonth", Integer.valueOf(lStrForMonth));
			for (int i = 1; i <= lLstAllBranchCodeBatches.size(); i++) {
				lQuery.setParameterList("lLstBrnchCode" + i, lLstAllBranchCodeBatches.get(i - 1));
			}
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	public void rejectAllChangeStmntOnRejectOfBill(String lStrForMonth, String lStrLocCode, Long lLngUserId, Long lLngPostId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List lLstResult = null;
		ghibSession = getSession();
		try {
			lSBQuery.append("update TrnMonthlyChangeRqst  \n");
			lSBQuery.append("set status = :lChngStmntStatus,updatedDate = :lUpdtDate,updatedUserId = :lUpdtUserId,updatedPostId = :lUpdtPostId \n");
			lSBQuery.append("Where \n");
			lSBQuery.append("forMonth = :forMonth \n");
			lSBQuery.append("and locationCode = :locCode \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lChngStmntStatus", bundleConst.getString("CHANGSTMNTSTATUS.REJECTED"));
			lQuery.setString("locCode", lStrLocCode);
			lQuery.setInteger("forMonth", Integer.valueOf(lStrForMonth));
			lQuery.setDate("lUpdtDate", DBUtility.getCurrentDateFromDB());
			lQuery.setInteger("lUpdtUserId", lLngUserId.intValue());
			lQuery.setInteger("lUpdtPostId", lLngPostId.intValue());
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	public void rejectAllMonthlyBilL(String lStrForMonth, String lStrLocCode, Long lLngUserId, Long lLngPostId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List lLstResult = null;
		ghibSession = getSession();
		try {
			lSBQuery.append("update TrnBillRegister  \n");
			lSBQuery.append("set currBillStatus = :lRejectBillStatus,updatedDate = :lUpdtDate,updatedUserId = :lUpdtUserId,updatedPostId = :lUpdtPostId \n");
			lSBQuery.append("Where \n");
			lSBQuery.append("forMonth = :forMonth \n");
			lSBQuery.append("and tcBill = :lTcBill \n");
			lSBQuery.append("and locationCode = :locCode \n");
			lSBQuery.append("and subjectId = 44 \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setShort("lRejectBillStatus", DBConstants.ST_BILL_REJECTED);
			lQuery.setString("lTcBill", bundleConst.getString("MNTH.MONTHLY"));
			lQuery.setString("locCode", lStrLocCode);
			lQuery.setInteger("forMonth", Integer.valueOf(lStrForMonth));
			lQuery.setDate("lUpdtDate", DBUtility.getCurrentDateFromDB());
			lQuery.setInteger("lUpdtUserId", lLngUserId.intValue());
			lQuery.setInteger("lUpdtPostId", lLngPostId.intValue());
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	public Integer getMonthlyPensionBillOuterDtlsCount(String lStrBillNo, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List lLstResult = null;
		ghibSession = getSession();
		Integer totalRecords = 0;
		Long lLngTotalRecords = null;
		try {
			lSBQuery.append("select count(distinct tpb.branchCode) \n");
			lSBQuery.append("from TrnBillRegister tbr,TrnPensionBillHdr tpb,MstBank mb,RltBankBranch rbb,MstScheme ms where \n");
			lSBQuery.append("tbr.billNo = tpb.billNo \n");
			lSBQuery.append("and tpb.bankCode = mb.bankCode \n");
			lSBQuery.append("and tpb.branchCode = rbb.branchCode \n");
			lSBQuery.append("and tbr.schemeNo = ms.schemeCode \n");
			lSBQuery.append("and tbr.billNo = :billNo\n");
			lSBQuery.append("and tbr.locationCode = :locationCode \n");
			// lSBQuery.append("group by tpb.bankCode,tpb.branchCode,mb.bankName,rbb.branchName,tbr.billNo,tbr.billCntrlNo,tbr.schemeNo,ms.schemeName,ms.majorHead,ms.subMajorHead,ms.minorHead, \n");
			// lSBQuery.append("ms.subMinorHead,ms.subHead,ms.demandCode,ms.charged,ms.plan \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("locationCode", lStrLocCode);
			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			lLstResult = lQuery.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				totalRecords = Integer.parseInt(lLstResult.get(0).toString());
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return totalRecords;
	}

	public List<Object[]> getMonthlyPensionBillOuterDtls(String lStrBillNo, String lStrLocCode, Map displayTag) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		ghibSession = getSession();
		Integer orderbypara = null;
		String[] columnValues = null;
		try {
			columnValues = new String[]{"", "mb.bankName", "rbb.branchName", "sum(tpb.noOfPnsr)", "sum(tpb.grossAmount)", "sum(tpb.deductionA + tpb.deductionB)", "sum(tpb.netAmount)"};
			lSBQuery.append("select tbr.billNo,tbr.billCntrlNo,tbr.schemeNo,ms.schemeName,mb.bankName,rbb.branchName,sum(tpb.noOfPnsr),cast(sum(tpb.grossAmount) as long), \n");
			lSBQuery.append("cast(sum(tpb.deductionA + tpb.deductionB) as long),cast(sum(tpb.netAmount) as long),ms.majorHead,ms.subMajorHead,ms.minorHead, \n");
			lSBQuery.append("ms.subMinorHead,ms.subHead,ms.demandCode,ms.charged,ms.plan,tpb.forMonth,tpb.bankCode,tpb.branchCode \n");
			lSBQuery.append("from TrnBillRegister tbr,TrnPensionBillHdr tpb,MstBank mb,RltBankBranch rbb,MstScheme ms where \n");
			lSBQuery.append("tbr.billNo = tpb.billNo \n");
			lSBQuery.append("and tpb.bankCode = mb.bankCode \n");
			lSBQuery.append("and tpb.branchCode = rbb.branchCode \n");
			lSBQuery.append("and tbr.schemeNo = ms.schemeCode \n");
			lSBQuery.append("and tbr.billNo = :billNo\n");
			lSBQuery.append("and tbr.locationCode = :locationCode \n");
			lSBQuery.append("group by tpb.bankCode,tpb.branchCode,mb.bankName,rbb.branchName,tbr.billNo,tbr.billCntrlNo,tbr.schemeNo,ms.schemeName,ms.majorHead,ms.subMajorHead,ms.minorHead, \n");
			lSBQuery.append("ms.subMinorHead,ms.subHead,ms.demandCode,ms.charged,ms.plan,tpb.forMonth \n");
			lSBQuery.append("Order By \n");
			if (displayTag != null && displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag.get(Constants.KEY_SORT_ORDER) : "desc");
				orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				lSBQuery.append(columnValues[orderbypara.intValue()] + " " + orderString);
			} else {
				lSBQuery.append("mb.bankName, rbb.branchName,tbr.billNo,tbr.billCntrlNo,tbr.schemeNo,ms.schemeName ");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("locationCode", lStrLocCode);
			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			if (displayTag != null) {
				Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
				lQuery.setFirstResult((pageNo.intValue() - 1) * Constants.PAGE_SIZE);
				lQuery.setMaxResults(Constants.PAGE_SIZE);
			}

			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public List<Object[]> getMonthlyOuterBillTotalAmts(String lStrBillNo, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		ghibSession = getSession();

		try {

			lSBQuery.append("select cast(sum(tpd.ir1Amount) as double),cast(sum(tpd.ir2Amount) as double),cast(sum(tpd.ir3Amount) as double), \n");
			lSBQuery.append("cast(sum(tpd.allcationBf11156) as double), cast(sum(tpd.allcationAf11156) as double), cast(sum(tpd.allcationAf10560) as double), \n");
			lSBQuery.append("cast(sum(tpd.allcationBf1436) as double), cast(sum(tpd.allcationAfZp) as double) \n");
			lSBQuery.append("from TrnPensionBillHdr tph, TrnPensionBillDtls tpd where tph.trnPensionBillHdrId = tpd.trnPensionBillHdrId \n");
			lSBQuery.append("and tph.billNo = :billNo ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));

			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public List<Object[]> getSchemeCodeWiseRecovery(String lStrBillNo, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		ghibSession = getSession();

		try {
			lSBQuery.append("select schemeCode, cast(sum(amount) as double) from \n");
			lSBQuery.append("TrnMonthlyPensionRecoveryDtls where billNo = :billNo and locationCode = :locationCode group by schemeCode \n");
			// lSBQuery.append("TrnPensionRecoveryDtls where pensionerCode in \n");
			// lSBQuery.append("(select pensionerCode from TrnPensionBillDtls where trnPensionBillHdrId in \n");
			// lSBQuery.append("(select trnPensionBillHdrId from TrnPensionBillHdr where billNo = :billNo and deductionA != 0 and locationCode = :locationCode)) \n");
			// lSBQuery.append("group by schemeCode \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("locationCode", lStrLocCode);
			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));

			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public List<Object[]> getChangeRqstIdByBillNo(BigInteger lBillNo, String lStrLocationCode, String lStrForMonth) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		ghibSession = getSession();
		try {
			lSBQuery.append("select mcr.changeRqstId,mcr.branchCode \n");
			lSBQuery.append("from \n");
			lSBQuery.append("TrnMonthlyChangeRqst mcr \n");
			lSBQuery.append("Where \n");
			lSBQuery.append("(mcr.branchCode,mcr.createdDate) in \n");
			lSBQuery.append("(select mcr.branchCode,max(mcr.createdDate) \n");
			lSBQuery.append("from \n");
			lSBQuery.append("TrnBillRegister tbr, \n");
			lSBQuery.append("TrnPensionBillHdr pbh, \n");
			lSBQuery.append("TrnPensionChangeHdr pch, \n");
			lSBQuery.append("TrnMonthlyChangeRqst mcr \n");
			lSBQuery.append("Where \n");
			lSBQuery.append("tbr.billNo = pbh.billNo \n");
			lSBQuery.append("and pbh.branchCode = pch.branchCode \n");
			lSBQuery.append("and pch.changeRqstId = mcr.changeRqstId \n");
			lSBQuery.append("and tbr.billNo = :lBillNo \n");
			lSBQuery.append("and tbr.locationCode = :locCode \n");
			lSBQuery.append("and mcr.forMonth = :lForMonth \n");
			lSBQuery.append("group by mcr.branchCode) \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setBigInteger("lBillNo", lBillNo);
			lQuery.setString("locCode", lStrLocationCode);
			lQuery.setString("lForMonth", lStrForMonth);
			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public List<Object[]> getMonthlyPensionBillInnerDtls(String lStrBillNo, String lStrLocCode, String lStrBankCode, String lStrBranchCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		ghibSession = getSession();

		try {
			lSBQuery.append("select tph.trnPensionBillHdrId,tbr.billCntrlNo,tph.bankCode,tph.branchCode,mb.bankName,rbb.branchName, \n");
			lSBQuery.append("tbr.schemeNo,ms.schemeName,tph.payMode,tpb.pensionerCode,tpb.ppoNo,tpb.pensionerName,tpb.accountNo,tpb.ropType,mph.series,tpb.ledgerNo,tpb.pageNo,tpb.pensionAmount, \n");
			lSBQuery.append("tpb.pensnCutAmount,tpb.other1,tpb.allcationBf11156,tpb.allcationAf11156,tpb.allcationAf10560,tpb.allcationBf1436,tpb.allcationAfZp, \n");
			lSBQuery.append("tpb.ir1Amount,tpb.ir2Amount,tpb.ir3Amount,tpb.dpAmount,tpb.tiAmount,tpb.peonAllowance,tpb.adpAmount,tpb.medicalAllowenceAmount, \n");
			lSBQuery.append("tpb.totalArrearAmt,tpb.grossAmount,tpb.recoveryAmount,tpb.netAmount,ms.majorHead,ms.subMajorHead,ms.minorHead, \n");
			lSBQuery.append("ms.subMinorHead,ms.subHead,ms.demandCode,ms.charged,ms.plan,tph.forMonth \n");
			lSBQuery.append("from TrnBillRegister tbr,TrnPensionBillHdr tph,MstBank mb,RltBankBranch rbb,MstScheme ms,TrnPensionBillDtls tpb,MstPensionHeadcode mph \n");
			lSBQuery.append("where tbr.billNo = tph.billNo \n");
			lSBQuery.append("and tph.trnPensionBillHdrId = tpb.trnPensionBillHdrId \n");
			lSBQuery.append("and tph.bankCode = mb.bankCode \n");
			lSBQuery.append("and tph.branchCode = rbb.branchCode \n");
			lSBQuery.append("and tbr.schemeNo = ms.schemeCode \n");
			lSBQuery.append("and tpb.headCode = mph.headCode \n");
			lSBQuery.append("and tbr.billNo = :billNo \n");
			lSBQuery.append("and tbr.locationCode = :locationCode \n");
			if ((lStrBankCode != null && lStrBranchCode != null) && (lStrBankCode.length() > 0 && lStrBranchCode.length() > 0)) {
				lSBQuery.append("and tph.bankCode = :lStrBankCode \n");
				lSBQuery.append("and tph.branchCode = :lStrBranchCode \n");
			}
			lSBQuery.append("order by mb.bankName,rbb.branchName,tpb.ropType,cast(tpb.ledgerNo,int),cast(tpb.pageNo,int),tpb.pensionerName");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("locationCode", lStrLocCode);
			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			if ((lStrBankCode != null && lStrBranchCode != null) && (lStrBankCode.length() > 0 && lStrBranchCode.length() > 0)) {
				lQuery.setString("lStrBankCode", lStrBankCode);
				lQuery.setString("lStrBranchCode", lStrBranchCode);
			}
			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAO#
	 * getBankBranchCategoryWiseSummary (java.lang.String)
	 */
	public List<Object[]> getBankBranchCategoryWiseSummary(String lStrBillNo) throws Exception {

		// TODO Auto-generated method stub
		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		ghibSession = getSession();

		try {

			lSBQuery.append("select tph.bankCode,tph.branchCode,sum(tph.noOfPnsr),mph.mainCatCode,mph.mainCatDesc,cast(sum(tph.netAmount) as double) \n");
			lSBQuery.append("from TrnPensionBillHdr tph,MstPensionHeadcode mph \n");
			lSBQuery.append("where tph.headCode = mph.headCode and tph.billNo = :billNo \n");
			lSBQuery.append("group by tph.bankCode,tph.branchCode,mph.mainCatCode,mph.mainCatDesc ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));

			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAO#getCategoryWiseSummary
	 * (java.lang.String)
	 */
	public List<Object[]> getCategoryWiseSummary(String lStrBillNo) throws Exception {

		// TODO Auto-generated method stub
		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		ghibSession = getSession();

		try {

			lSBQuery.append("select sum(tph.noOfPnsr),mph.mainCatCode,mph.mainCatDesc,cast(sum(tph.netAmount) as double) \n");
			lSBQuery.append("from TrnPensionBillHdr tph,MstPensionHeadcode mph \n");
			lSBQuery.append("where tph.headCode = mph.headCode and tph.billNo = :billNo \n");
			lSBQuery.append("group by mph.mainCatCode,mph.mainCatDesc ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));

			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public List<String> getChangeStmntRqstIdsForOuterByBillNo(Long lLngBillNo) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> lLstResult = null;
		ghibSession = getSession();
		try {
			lSBQuery.append("Select \n");
			lSBQuery.append("chngStmntIdList  \n");
			lSBQuery.append("From  \n");
			lSBQuery.append("TrnBillChangeStmntMpg  \n");
			lSBQuery.append("where \n");
			lSBQuery.append("billNo = :lLngBillNo \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("lLngBillNo", lLngBillNo);
			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public List<Long> getAllBillNoToReject(String lStrForMonth, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> lLstResult = null;
		ghibSession = getSession();
		Short lMonthlySubId = 44;
		Short lShRjctedStatus = -1;
		Short lShArchivedStatus = -15;
		Short lShDiscardedStatus = 60;
		List<Short> lLstRjctedBillStatus = new ArrayList<Short>();
		try {
			lLstRjctedBillStatus.add(lShRjctedStatus);
			lLstRjctedBillStatus.add(lShArchivedStatus);
			lLstRjctedBillStatus.add(lShDiscardedStatus);
			lSBQuery.append("Select \n");
			lSBQuery.append("billNo  \n");
			lSBQuery.append("From  \n");
			lSBQuery.append("TrnBillRegister  \n");
			lSBQuery.append("Where  \n");
			lSBQuery.append("forMonth = :forMonth \n");
			lSBQuery.append("and tcBill = :lTcBill \n");
			lSBQuery.append("and subjectId = :lMonthlySubId \n");
			lSBQuery.append("and locationCode = :locCode \n");
			lSBQuery.append("and currBillStatus not in (:lLstBillStatus) \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lTcBill", bundleConst.getString("MNTH.MONTHLY"));
			lQuery.setString("locCode", lStrLocCode);
			lQuery.setInteger("forMonth", Integer.valueOf(lStrForMonth));
			lQuery.setShort("lMonthlySubId", lMonthlySubId);
			lQuery.setParameterList("lLstBillStatus", lLstRjctedBillStatus);
			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public List<Object[]> getAllPensionerCodeOfBill(List<Long> lLstLngBillNo) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		ghibSession = getSession();
		try {
			lSBQuery.append("Select \n");
			lSBQuery.append("pbd.pensionerCode,pbd.ppoNo,tbr.billNo,pbh.forMonth  \n");
			lSBQuery.append("From  \n");
			lSBQuery.append("TrnBillRegister tbr, \n");
			lSBQuery.append("TrnPensionBillHdr pbh, \n");
			lSBQuery.append("TrnPensionBillDtls pbd \n");
			lSBQuery.append("Where  \n");
			lSBQuery.append("tbr.billNo = pbh.billNo  \n");
			lSBQuery.append("and pbh.trnPensionBillHdrId = pbd.trnPensionBillHdrId  \n");
			lSBQuery.append("and tbr.billNo in (:lLstBillNo)  \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("lLstBillNo", lLstLngBillNo);
			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public void updateMonthlyPnsnRcryOnChngStmntApproval(List<Long> lLstChngRqstId, String lStrStatus, String lStrLocCode, Long lLngUserId, Long lLngPostId, Date lUpdatedDate) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List lLstResult = null;
		ghibSession = getSession();
		List<List<Long>> lLstAllChngRqstIdBatches = new ArrayList<List<Long>>();
		int maxBatchSize = 900;
		List<Long> lLstChangeRqstIdBatch = null;
		try {
			// --Preparing batches for bulk update starts <<<
			if (lLstChngRqstId.size() > maxBatchSize) {
				int totalBatches = lLstChngRqstId.size() / maxBatchSize;
				for (int cnt = 1; cnt <= totalBatches; cnt++) {
					lLstChangeRqstIdBatch = new ArrayList<Long>();
					lLstChangeRqstIdBatch.addAll((lLstChngRqstId.subList(maxBatchSize * (cnt - 1), (maxBatchSize * cnt))));
					lLstAllChngRqstIdBatches.add(lLstChangeRqstIdBatch);
				}
				int remainingBranchCodes = lLstChngRqstId.size() % maxBatchSize;
				if (remainingBranchCodes > 0) {
					lLstChangeRqstIdBatch = new ArrayList<Long>();
					lLstChangeRqstIdBatch.addAll(lLstChngRqstId.subList(maxBatchSize * totalBatches, (maxBatchSize * totalBatches) + remainingBranchCodes));
					lLstAllChngRqstIdBatches.add(lLstChangeRqstIdBatch);
				}
			} else {
				lLstAllChngRqstIdBatches.add(lLstChngRqstId);
			}
			// --Preparing batches for bulk update ends >>>>
			lSBQuery.append("update TrnMonthlyPensionRecoveryDtls  \n");
			lSBQuery.append("set changeStmntStatus = :lChngStmntStatus,updatedUserId = :lUpdtUserId,updatedPostId = :lUpdtPostId,updatedDate = :lUpDt \n");
			lSBQuery.append("Where \n");
			lSBQuery.append("(changeRqstId in (:lLstChngRqstIds1) \n");
			for (int i = 2; i <= lLstAllChngRqstIdBatches.size(); i++) {
				lSBQuery.append(" or changeRqstId in (:lLstChngRqstIds" + i + ") ");
			}
			lSBQuery.append(" ) \n");
			lSBQuery.append("and locationCode = :locCode \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lChngStmntStatus", lStrStatus);
			lQuery.setString("locCode", lStrLocCode);
			lQuery.setLong("lUpdtUserId", lLngUserId);
			lQuery.setLong("lUpdtPostId", lLngPostId);
			lQuery.setDate("lUpDt", lUpdatedDate);
			for (int i = 1; i <= lLstAllChngRqstIdBatches.size(); i++) {
				lQuery.setParameterList("lLstChngRqstIds" + i, lLstAllChngRqstIdBatches.get(i - 1));
			}
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	public List<Long> getChangeRqstIdFromBillNo(List<Long> lLstBillNo) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> lLstResult = null;
		ghibSession = getSession();
		try {
			lSBQuery.append("Select \n");
			lSBQuery.append("mcr.changeRqstId  \n");
			lSBQuery.append("From  \n");
			lSBQuery.append("TrnMonthlyChangeRqst mcr \n");
			lSBQuery.append("Where  \n");
			lSBQuery.append("mcr.billNo in (:lLstBillNo)  \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("lLstBillNo", lLstBillNo);
			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public List<Object[]> getECSCPBKFileDetails(String lStrLocCode, String lStrForMonth) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		ghibSession = getSession();
		List<Short> lLstBillStatus = new ArrayList<Short>();
		Short lShRjctedStatus = -1;
		Short lShArchivedStatus = -15;
		Short lShDiscardedStatus = 60;
		Short lShCreatedStatus = 5;
		Short lShForApprovalStatus = 10;
		try {
			lLstBillStatus.add(lShRjctedStatus);
			lLstBillStatus.add(lShArchivedStatus);
			lLstBillStatus.add(lShDiscardedStatus);
			lLstBillStatus.add(lShCreatedStatus);
			lLstBillStatus.add(lShForApprovalStatus);
			lSBQuery.append("Select \n");
			lSBQuery.append("lpad(substr(pbh.branch_code,5,3),3,'0') as bnk_code, \n");
			lSBQuery.append("lpad(substr(pbh.branch_code,9,3),3,'0') as brnch_code, \n");
			lSBQuery.append("rpad(pbd.ACCOUNT_NO,15,' '), \n");
			lSBQuery.append("nvl(mph.GENDER,' '), \n");
			lSBQuery.append("rpad(pbd.PENSIONER_NAME,50,' '), \n");
			lSBQuery.append("rpad(pbd.PPO_NO,20,' '), \n");
			lSBQuery.append("lpad((pbd.ALLCATION_BF_1_4_36+ pbd.ALLCATION_BF_1_11_56+ pbd.ALLCATION_AF_1_11_56+ pbd.ALLCATION_AF_1_05_60+ pbd.ALLCATION_AF_ZP),5,'0'), \n");
			lSBQuery.append("lpad(pbd.TI_AMOUNT,5,'0'), \n");
			lSBQuery.append("lpad(pbd.TOTAL_ARREAR_AMT,7,'0'), \n");
			lSBQuery.append("lpad(pbd.RECOVERY_AMOUNT,7,'0'), \n");
			lSBQuery.append("lpad(pbd.NET_AMOUNT,7,'0') \n");
			lSBQuery.append("From \n");
			lSBQuery.append("TRN_BILL_REGISTER tbr \n");
			lSBQuery.append("join TRN_PENSION_BILL_HDR pbh on tbr.bill_no = pbh.bill_no \n");
			lSBQuery.append("join TRN_PENSION_BILL_DTLS pbd on pbd.TRN_PENSION_BILL_HDR_ID = pbh.TRN_PENSION_BILL_HDR_ID \n");
			lSBQuery.append("join mst_pensioner_hdr mph on mph.pensioner_code = pbd.pensioner_code \n");
			lSBQuery.append("Where \n");
			lSBQuery.append("tbr.curr_bill_status not in (:lLstBillStatus) \n");
			lSBQuery.append("and tbr.subject_id = :lSubId \n");
			lSBQuery.append("and tbr.location_code = :lStrLocCode \n");
			lSBQuery.append("and pbh.for_month = :lStrForMonth \n");
			lSBQuery.append("and pbd.NET_AMOUNT > 0 \n");
			lSBQuery.append("order by bnk_code,brnch_code \n");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("lSubId", 44);
			lQuery.setString("lStrLocCode", lStrLocCode);
			lQuery.setString("lStrForMonth", lStrForMonth);
			lQuery.setParameterList("lLstBillStatus", lLstBillStatus);
			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public List<Object[]> getChangeStmntIdsBYMonthYear(Integer lIntForMonth, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		ghibSession = getSession();
		Short lShRjctedStatus = -1;
		Short lShArchivedStatus = -15;
		Short lShDiscardedStatus = 60;
		List<Short> lLstBillStatus = new ArrayList<Short>();
		try {
			lLstBillStatus.add(lShRjctedStatus);
			lLstBillStatus.add(lShArchivedStatus);
			lLstBillStatus.add(lShDiscardedStatus);
			lSBQuery.append("Select \n");
			lSBQuery.append("bcm.billNo,bcm.schemeCode,bcm.chngStmntIdList \n");
			lSBQuery.append("from \n");
			lSBQuery.append("TrnBillChangeStmntMpg bcm,  \n");
			lSBQuery.append("TrnBillRegister tbr  \n");
			lSBQuery.append("where  \n");
			lSBQuery.append("bcm.billNo = tbr.billNo  \n");
			lSBQuery.append("and bcm.forMonth = :lForMonth  \n");
			lSBQuery.append("and bcm.locationCode = :lLocCode  \n");
			lSBQuery.append("and tbr.currBillStatus not in (:lLstBillStatus)  \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setInteger("lForMonth", lIntForMonth);
			lQuery.setString("lLocCode", lStrLocCode);
			lQuery.setParameterList("lLstBillStatus", lLstBillStatus);
			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public List<Long> getChangeRqstIdsFromBnkBranch(String lStrbankCode, String lStrBranchCode, Integer lIntForMonth, String lStrLocationCode) throws Exception {

		Session ghibSession = getSession();
		StringBuffer lSBQuery = new StringBuffer();
		List<Long> lLstBillNos = null;
		List<String> lLstChngStmntStatus = new ArrayList<String>();
		lLstChngStmntStatus.add(bundleConst.getString("CHANGSTMNTSTATUS.APPROVED"));
		lLstChngStmntStatus.add(bundleConst.getString("CHANGSTMNTSTATUS.ARCHIVED"));
		try {
			lSBQuery.append("select mcr.changeRqstId \n");
			lSBQuery.append("from TrnMonthlyChangeRqst mcr \n");
			lSBQuery.append("where \n");
			lSBQuery.append("mcr.bankCode=:bankCode \n");
			lSBQuery.append("and mcr.branchCode=:branchCode \n");
			lSBQuery.append("and mcr.forMonth=:forMonth \n");
			lSBQuery.append("and mcr.locationCode=:locationCode \n");
			lSBQuery.append("and mcr.status in (:lLstChngStmntStatus) \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("locationCode", lStrLocationCode);
			lQuery.setString("bankCode", lStrbankCode);
			lQuery.setString("branchCode", lStrBranchCode);
			lQuery.setInteger("forMonth", lIntForMonth);
			lQuery.setParameterList("lLstChngStmntStatus", lLstChngStmntStatus);

			lLstBillNos = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstBillNos;
	}

	public List<Object[]> getMonthlyPensionBillBankwiseOuterDtls(String lStrBillNo, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		ghibSession = getSession();
		Integer orderbypara = null;
		try {
			lSBQuery.append("select tbr.bill_No,tbr.bill_Cntrl_No,tbr.scheme_No,ms.scheme_Name,mb.bank_Name,sum(tpb.no_Of_Pnsr),cast(sum(tpb.gross_Amount) as BIGINT), \n");
			lSBQuery.append("cast(sum(tpb.deduction_A + tpb.deduction_B) as BIGINT),cast(sum(tpb.net_Amount) as BIGINT),ms.major_Head,ms.sub_Major_Head,ms.minor_Head, \n");
			lSBQuery.append("ms.sub_Minor_Head,ms.sub_Head,ms.demand_Code,ms.charged,ms.plan,tpb.for_Month \n");
			lSBQuery.append("from Trn_Bill_Register tbr,Trn_Pension_Bill_Hdr tpb,Mst_Bank mb,Mst_Scheme ms,Rlt_Bank_Branch rbb where \n");
			lSBQuery.append("tbr.bill_No = tpb.bill_No \n");
			lSBQuery.append("and tpb.bank_Code = mb.bank_Code \n");
			lSBQuery.append("and tpb.branch_Code = rbb.branch_Code \n");
			lSBQuery.append("and rbb.location_Code = tbr.location_Code \n");
			lSBQuery.append("and tbr.scheme_No = ms.scheme_Code \n");
			lSBQuery.append("and rbb.reporting_Branch_Code is null \n");
			lSBQuery.append("and rbb.reporting_Bank_Code is null \n");
			lSBQuery.append("and tbr.bill_No = :billNo\n");
			lSBQuery.append("and tbr.location_Code = :locationCode \n");
			lSBQuery.append("group by tpb.bank_Code,mb.bank_Name,tbr.bill_No,tbr.bill_Cntrl_No,tbr.scheme_No,ms.scheme_Name,ms.major_Head,ms.sub_Major_Head,ms.minor_Head, \n");
			lSBQuery.append("ms.sub_Minor_Head,ms.sub_Head,ms.demand_Code,ms.charged,ms.plan,tpb.for_Month \n");
			lSBQuery.append(" Union All \n");
			lSBQuery.append("select tbr.bill_No,tbr.bill_Cntrl_No,tbr.scheme_No,ms.scheme_Name,(mb.bank_Name || ',' || rbb2.branch_Name),sum(tpb.no_Of_Pnsr),cast(sum(tpb.gross_Amount) as BIGINT), \n");
			lSBQuery.append("cast(sum(tpb.deduction_A + tpb.deduction_B) as BIGINT),cast(sum(tpb.net_Amount) as BIGINT),ms.major_Head,ms.sub_Major_Head,ms.minor_Head, \n");
			lSBQuery.append("ms.sub_Minor_Head,ms.sub_Head,ms.demand_Code,ms.charged,ms.plan,tpb.for_Month \n");
			lSBQuery.append("from Trn_Bill_Register tbr,Trn_Pension_Bill_Hdr tpb,Mst_Bank mb,Mst_Scheme ms,Rlt_Bank_Branch rbb1,Rlt_Bank_Branch rbb2 where \n");
			lSBQuery.append("tbr.bill_No = tpb.bill_No \n");
			lSBQuery.append("and rbb1.branch_Code = tpb.branch_Code \n");
			lSBQuery.append("and rbb2.branch_Code = rbb1.reporting_Branch_Code \n");
			lSBQuery.append("and rbb1.reporting_Bank_Code = mb.bank_Code \n");
			lSBQuery.append("and rbb1.location_Code = rbb2.location_Code \n");
			lSBQuery.append("and rbb1.location_Code = tbr.location_Code \n");
			lSBQuery.append("and tbr.scheme_No = ms.scheme_Code \n");
			lSBQuery.append("and rbb1.reporting_Branch_Code is not null \n");
			lSBQuery.append("and rbb1.reporting_Bank_Code is not null \n");
			lSBQuery.append("and tbr.bill_No = :billNo\n");
			lSBQuery.append("and tbr.location_Code = :locationCode \n");
			lSBQuery.append("group by mb.bank_Code,mb.bank_Name,rbb2.branch_Name,tbr.bill_No,tbr.bill_Cntrl_No,tbr.scheme_No,ms.scheme_Name,ms.major_Head,ms.sub_Major_Head,ms.minor_Head, \n");
			lSBQuery.append("ms.sub_Minor_Head,ms.sub_Head,ms.demand_Code,ms.charged,ms.plan,tpb.for_Month \n");
			lSBQuery.append("Order By 5 \n");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("locationCode", lStrLocCode);
			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));
			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	/**
	 * 
	 * <H3>Description -</H3> This method is used to set bill no in all arrear
	 * details of the pensioner on generation of bill.
	 * 
	 * 
	 * @author Shripal Soni
	 * @param lLstStrPnsrCode
	 * @param lLngBillNo
	 * @param lLngUpdtUserId
	 * @param lLngUpdtPostId
	 * @param lDtUpdtDate
	 * @throws Exception
	 */
	public void updateMonthlyBillNoInArrearDtls(Long lLngBillNo, Integer lIntForMonth, Long lLngUpdtUserId, Long lLngUpdtPostId, Date lDtUpdtDate, String lStrLocCode, String lStrSchemeCode,
			Short lShortSubId, String lStrECSBranchFlag) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> lLstResult = null;
		Session ghibSession = getSession();
		Short lShRjctedStatus = -1;
		Short lShArchivedStatus = -15;
		Short lShDiscardedStatus = 60;
		List<Short> lLstRjctedBillStatus = new ArrayList<Short>();
		try {
			lLstRjctedBillStatus.add(lShRjctedStatus);
			lLstRjctedBillStatus.add(lShArchivedStatus);
			lLstRjctedBillStatus.add(lShDiscardedStatus);
			lSBQuery.append("update trn_pension_arrear_dtls pad  \n");
			lSBQuery.append("set pad.bill_no = :lLngBillNo,  \n");
			lSBQuery.append("pad.updated_user_id = :lLngUpdtUserId,  \n");
			lSBQuery.append("pad.updated_post_id = :lLngUpdtPostId,  \n");
			lSBQuery.append("pad.updated_date = :lDtUpdtDate  \n");
			lSBQuery.append("where exists  \n");
			lSBQuery.append("	(select 1 from  \n");
			lSBQuery.append("		(select pcd.pensioner_code from  \n");
			lSBQuery.append("		trn_monthly_change_rqst mcr  \n");
			lSBQuery.append("		join trn_pension_change_hdr pch on mcr.change_rqst_id = pch.change_rqst_id and mcr.location_code = :lLocCode and  mcr.for_month = :lIntForMonth and mcr.status = :lStatus and pch.scheme_code = :lSchemeCode \n");
			lSBQuery.append("		join trn_pension_change_dtls pcd on pcd.trn_pension_change_hdr_id = pch.trn_pension_change_hdr_id \n");
			lSBQuery.append("		join rlt_bank_branch rbb on rbb.branch_code = pch.branch_code and rbb.location_code = :lLocCode and rbb.ECS_BRANCH = :lFlagECS \n");
			lSBQuery.append("		minus \n");
			lSBQuery.append("		select pbd.pensioner_code From  \n");
			lSBQuery.append("		trn_bill_register tbr  \n");
			lSBQuery.append("		join trn_pension_bill_hdr pbh on pbh.bill_no = tbr.bill_no and tbr.location_code = :lLocCode and tbr.subject_id = :lSubId and tbr.for_month = :lIntForMonth and tbr.scheme_no = :lSchemeCode and tbr.curr_bill_status not in (:lRejectedStatus)  \n");
			lSBQuery.append("		join trn_pension_bill_dtls pbd on pbh.trn_pension_bill_hdr_id = pbd.trn_pension_bill_hdr_id ) inn  \n");
			lSBQuery.append("	where  \n");
			lSBQuery.append("	inn.pensioner_code = pad.pensioner_code \n");
			lSBQuery.append("	and pad.payment_from_yyyymm <= :lIntForMonth and pad.payment_to_yyyymm >= :lIntForMonth  \n");
			lSBQuery.append("	and pad.paid_flag = 'N'  \n");
			lSBQuery.append("	) \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("lLngBillNo", lLngBillNo);
			lQuery.setLong("lLngUpdtUserId", lLngUpdtUserId);
			lQuery.setLong("lLngUpdtPostId", lLngUpdtPostId);
			lQuery.setDate("lDtUpdtDate", lDtUpdtDate);
			lQuery.setInteger("lIntForMonth", lIntForMonth);
			lQuery.setString("lLocCode", lStrLocCode);
			lQuery.setString("lStatus", "Approved");
			lQuery.setString("lSchemeCode", lStrSchemeCode);
			lQuery.setShort("lSubId", lShortSubId);
			lQuery.setString("lFlagECS", lStrECSBranchFlag);
			lQuery.setParameterList("lRejectedStatus", lLstRjctedBillStatus);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	public void updateMonthlyBillNoForRecovery(List<String> lLstStrPnsrCode, Integer lIntForMonth, String lStrLocCode, Long lLngBillNo, Long gLngUserId, Long gLngPostId, Date gDate,
			String lStrSchemeCode, Short lShortSubId, String lStrECSBranchFlag) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Short lShRjctedStatus = -1;
		Short lShArchivedStatus = -15;
		Short lShDiscardedStatus = 60;
		List<Short> lLstRjctedBillStatus = new ArrayList<Short>();
		try {
			lLstRjctedBillStatus.add(lShRjctedStatus);
			lLstRjctedBillStatus.add(lShArchivedStatus);
			lLstRjctedBillStatus.add(lShDiscardedStatus);
			lSBQuery.append("update trn_monthly_pension_recovery_dtls prd  \n");
			lSBQuery.append("set prd.bill_no = :lLngBillNo,  \n");
			lSBQuery.append("prd.updated_user_id = :lLngUpdtUserId,  \n");
			lSBQuery.append("prd.updated_post_id = :lLngUpdtPostId,  \n");
			lSBQuery.append("prd.updated_date = :lDtUpdtDate  \n");
			lSBQuery.append("where exists  \n");
			lSBQuery.append("	(select 1 from  \n");
			lSBQuery.append("		(select pcd.pensioner_code from  \n");
			lSBQuery.append("		trn_monthly_change_rqst mcr  \n");
			lSBQuery.append("		join trn_pension_change_hdr pch on mcr.change_rqst_id = pch.change_rqst_id and mcr.location_code = :lLocCode and  mcr.for_month = :lIntForMonth and mcr.status = :lStatus and pch.scheme_code = :lSchemeCode \n");
			lSBQuery.append("		join trn_pension_change_dtls pcd on pcd.trn_pension_change_hdr_id = pch.trn_pension_change_hdr_id \n");
			lSBQuery.append("		join rlt_bank_branch rbb on rbb.branch_code = pch.branch_code and rbb.location_code = :lLocCode and rbb.ECS_BRANCH = :lFlagECS \n");
			lSBQuery.append("		minus \n");
			lSBQuery.append("		select pbd.pensioner_code From  \n");
			lSBQuery.append("		trn_bill_register tbr  \n");
			lSBQuery.append("		join trn_pension_bill_hdr pbh on pbh.bill_no = tbr.bill_no and tbr.location_code = :lLocCode and tbr.subject_id = :lSubId and tbr.for_month = :lIntForMonth and tbr.scheme_no = :lSchemeCode and tbr.curr_bill_status not in (:lRejectedStatus)  \n");
			lSBQuery.append("		join trn_pension_bill_dtls pbd on pbh.trn_pension_bill_hdr_id = pbd.trn_pension_bill_hdr_id ) inn  \n");
			lSBQuery.append("	where  \n");
			lSBQuery.append("	inn.pensioner_code = prd.pensioner_code and prd.location_code = :lLocCode and prd.change_stmnt_status = :lStatus and prd.for_month = :lIntForMonth \n");
			lSBQuery.append("	) \n");

			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setInteger("lIntForMonth", lIntForMonth);
			lQuery.setLong("lLngUpdtUserId", gLngUserId);
			lQuery.setLong("lLngUpdtPostId", gLngPostId);
			lQuery.setDate("lDtUpdtDate", gDate);
			lQuery.setLong("lLngBillNo", lLngBillNo);
			lQuery.setString("lLocCode", lStrLocCode);
			lQuery.setString("lStatus", "Approved");
			lQuery.setString("lSchemeCode", lStrSchemeCode);
			lQuery.setShort("lSubId", lShortSubId);
			lQuery.setString("lFlagECS", lStrECSBranchFlag);
			lQuery.setParameterList("lRejectedStatus", lLstRjctedBillStatus);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public void saveAllCollection(List lLstValues) throws Exception {

		try {
			ghibSession = getSession();
			if (lLstValues != null && lLstValues.size() > 0) {
				for (Object lObj : lLstValues) {
					ghibSession.save(lObj);
				}
			}
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public List<Object[]> getChngStmntBranchSchemeCodeDtls(Integer lIntForMonth, String lStrLocationCode, List<String> lLstBranchCode) throws Exception {

		List<Object[]> lLstResult = null;
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("select pch.branchCode,pch.schemeCode  \n");
			lSBQuery.append("From  \n");
			lSBQuery.append("TrnMonthlyChangeRqst mcr,  \n");
			lSBQuery.append("TrnPensionChangeHdr pch  \n");
			lSBQuery.append("Where  \n");
			lSBQuery.append("mcr.changeRqstId = pch.changeRqstId  \n");
			lSBQuery.append("and mcr.locationCode = :lLocCode  \n");
			lSBQuery.append("and mcr.forMonth = :lIntForMonth  \n");
			lSBQuery.append("and mcr.status = 'Approved'  \n");
			lSBQuery.append("and pch.branchCode in (:lLstBranchCode)  \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setInteger("lIntForMonth", lIntForMonth);
			lQuery.setString("lLocCode", lStrLocationCode);
			lQuery.setParameterList("lLstBranchCode", lLstBranchCode);
			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public List<Object[]> getPrevMonthChangeStmntStatus(Integer lIntForMonth, String lStrLocationCode, List<String> lLstBranchCode) throws Exception {

		List<Object[]> lLstResult = null;
		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		try {
			lSBQuery.append("select mcr.branchCode,mcr.status  \n");
			lSBQuery.append("From  \n");
			lSBQuery.append("TrnMonthlyChangeRqst mcr  \n");
			lSBQuery.append("Where  \n");
			lSBQuery.append("(mcr.branchCode,mcr.forMonth,mcr.createdDate) in  \n");
			lSBQuery.append("(select smcr.branchCode,smcr.forMonth,max(smcr.createdDate) from   \n");
			lSBQuery.append("TrnMonthlyChangeRqst smcr  \n");
			lSBQuery.append("Where  \n");
			lSBQuery.append("smcr.forMonth = :lIntForMonth  \n");
			lSBQuery.append("and smcr.locationCode = :lLocCode  \n");
			lSBQuery.append("and smcr.branchCode in (:lLstBranchCode)  \n");
			lSBQuery.append("group by smcr.branchCode,smcr.forMonth ) \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setInteger("lIntForMonth", lIntForMonth);
			lQuery.setString("lLocCode", lStrLocationCode);
			lQuery.setParameterList("lLstBranchCode", lLstBranchCode);
			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public List<Object[]> getPrevMonnthNotGeneratedBillsBranch(Integer lIntForMonth, String lStrLocationCode, List<String> lLstBranchCode) throws Exception {

		List<Object[]> lLstResult = null;
		StringBuilder lSBQuery = new StringBuilder();
		Short lShRjctedStatus = DBConstants.ST_BILL_REJECTED;
		Short lShArchivedStatus = DBConstants.ST_BILL_ARCHEIVED;
		Short lShDiscardedStatus = DBConstants.ST_BILL_DISCARD;
		Short lShBillGenerateStatus = DBConstants.ST_BILL_CREATED;
		Short lShBillPendingApprovalStatus = DBConstants.ST_BILL_FORW_TO_ATO;
		List<Short> lLstRjctedBillStatus = new ArrayList<Short>();
		try {
			lLstRjctedBillStatus.add(lShRjctedStatus);
			lLstRjctedBillStatus.add(lShArchivedStatus);
			lLstRjctedBillStatus.add(lShDiscardedStatus);
			lLstRjctedBillStatus.add(lShBillGenerateStatus);
			lLstRjctedBillStatus.add(lShBillPendingApprovalStatus);
			lSBQuery.append("select pch.branch_code,pch.scheme_code \n");
			lSBQuery.append("From \n");
			lSBQuery.append("TRN_MONTHLY_CHANGE_RQST mcr \n");
			lSBQuery.append("join trn_pension_change_hdr  pch on mcr.CHANGE_RQST_ID = pch.CHANGE_RQST_ID \n");
			lSBQuery.append("where pch.FOR_MONTH = :lIntForMonth \n");
			lSBQuery.append("and mcr.status = 'Approved' \n");
			lSBQuery.append("and mcr.location_Code = :lLocCode \n");
			lSBQuery.append("and pch.branch_code in (:lLstBranchCode) \n");
			lSBQuery.append("group by pch.branch_code,pch.scheme_code  \n");
			lSBQuery.append("minus  \n");
			lSBQuery.append("select pbh.branch_code,pbh.scheme_code  \n");
			lSBQuery.append("from  \n");
			lSBQuery.append("trn_bill_register tbr  \n");
			lSBQuery.append("join trn_pension_bill_hdr  pbh on tbr.bill_no = pbh.bill_no  \n");
			lSBQuery.append("where pbh.FOR_MONTH = :lIntForMonth  \n");
			lSBQuery.append("and tbr.location_Code = :lLocCode  \n");
			lSBQuery.append("and tbr.curr_bill_status not in (:lCurrBillStatus)  \n");
			lSBQuery.append("and pbh.branch_code in  (:lLstBranchCode) \n");
			lSBQuery.append("group by pbh.branch_code,pbh.scheme_code \n");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setInteger("lIntForMonth", lIntForMonth);
			lQuery.setString("lLocCode", lStrLocationCode);
			lQuery.setParameterList("lLstBranchCode", lLstBranchCode);
			lQuery.setParameterList("lCurrBillStatus", lLstRjctedBillStatus);
			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public List<Object[]> getBillPartyDtlsForSchemewiseBill(Integer lIntForMonth, String lStrLocationCode, String lStrSchemeCode, String lStrECSBranchFlag) throws Exception {

		List<Object[]> lLstResult = null;
		StringBuilder lSBQuery = new StringBuilder();
		Short lShRjctedStatus = -1;
		Short lShArchivedStatus = -15;
		Short lShDiscardedStatus = 60;
		List<Short> lLstRjctedBillStatus = new ArrayList<Short>();
		try {
			lLstRjctedBillStatus.add(lShRjctedStatus);
			lLstRjctedBillStatus.add(lShArchivedStatus);
			lLstRjctedBillStatus.add(lShDiscardedStatus);
			lSBQuery.append("select pch.bank_code,mb.bank_name,null,cast(sum(pch.NET_AMOUNT) as bigint)  \n");
			lSBQuery.append("from \n");
			lSBQuery.append("trn_monthly_change_rqst mcr \n");
			lSBQuery.append("join trn_pension_change_hdr pch on mcr.CHANGE_RQST_ID = pch.CHANGE_RQST_ID and mcr.status = 'Approved' and mcr.FOR_MONTH = :lForMonth and pch.scheme_code = :lSchemeCode and mcr.location_code = :lLocCode \n");
			lSBQuery.append("join RLT_BANK_BRANCH rbb on rbb.BRANCH_CODE = pch.BRANCH_CODE and rbb.REPORTING_BANK_CODE is null and rbb.REPORTING_BRANCH_CODE is null and rbb.location_code = :lLocCode and rbb.ECS_BRANCH = :lFlagECS \n");
			lSBQuery.append("join mst_bank mb on mb.bank_code = rbb.bank_code   \n");
			lSBQuery.append("where not exists   \n");
			lSBQuery.append("(   \n");
			lSBQuery.append("	select 1 from trn_bill_register tbr   \n");
			lSBQuery.append("	join trn_pension_bill_hdr pbh on tbr.bill_no = pbh.bill_no and tbr.FOR_MONTH = :lForMonth and tbr.scheme_no = :lSchemeCode and tbr.SUBJECT_ID = 44 and tbr.CURR_BILL_STATUS not in (:lLstRejectedStatus) and tbr.location_code = :lLocCode  \n");
			lSBQuery.append("	where  \n");
			lSBQuery.append("	pbh.branch_code = pch.branch_code  \n");
			lSBQuery.append(")  \n");
			lSBQuery.append("group by pch.bank_code,mb.bank_name  \n");
			lSBQuery.append("union all  \n");
			lSBQuery.append("select mb.bank_code,mb.bank_name || ',' || rbb2.branch_name,rbb2.branch_code,cast(sum(pch.NET_AMOUNT) as bigint)  \n");
			lSBQuery.append("from trn_monthly_change_rqst mcr \n");
			lSBQuery.append("join trn_pension_change_hdr pch on mcr.CHANGE_RQST_ID = pch.CHANGE_RQST_ID and mcr.status = 'Approved' and mcr.FOR_MONTH = :lForMonth and pch.scheme_code = :lSchemeCode and mcr.location_code = :lLocCode \n");
			lSBQuery.append("join RLT_BANK_BRANCH rbb1 on rbb1.BRANCH_CODE = pch.BRANCH_CODE and rbb1.REPORTING_BANK_CODE is not null and rbb1.REPORTING_BRANCH_CODE is not null and rbb1.location_code = :lLocCode and rbb1.ECS_BRANCH = :lFlagECS \n");
			lSBQuery.append("join RLT_BANK_BRANCH rbb2 on rbb2.branch_code = rbb1.REPORTING_BRANCH_CODE  and rbb2.location_code = :lLocCode \n");
			lSBQuery.append("join mst_bank mb on mb.bank_code = rbb1.REPORTING_BANK_CODE and rbb2.location_code = :lLocCode \n");
			lSBQuery.append("where not exists \n");
			lSBQuery.append("( \n");
			lSBQuery.append("	select 1 from trn_bill_register tbr \n");
			lSBQuery.append("	join trn_pension_bill_hdr pbh on tbr.bill_no = pbh.bill_no and tbr.FOR_MONTH =:lForMonth and tbr.scheme_no = :lSchemeCode and tbr.SUBJECT_ID = 44 and tbr.CURR_BILL_STATUS not in (:lLstRejectedStatus) and tbr.location_code = :lLocCode \n");
			lSBQuery.append("	where \n");
			lSBQuery.append("	pbh.branch_code = pch.branch_code \n");
			lSBQuery.append(") \n");
			lSBQuery.append("group by mb.bank_code,mb.bank_name,rbb2.branch_name,rbb2.branch_code \n");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setInteger("lForMonth", lIntForMonth);
			lQuery.setString("lLocCode", lStrLocationCode);
			lQuery.setParameterList("lLstRejectedStatus", lLstRjctedBillStatus);
			lQuery.setString("lSchemeCode", lStrSchemeCode);
			lQuery.setString("lFlagECS", lStrECSBranchFlag);

			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	
	public List<Object[]> getSupplBillSchemeCodeWiseRecovery(String lStrBillNo) throws Exception {

		// TODO Auto-generated method stub
		StringBuilder lSBQuery = new StringBuilder();
		List<Object[]> lLstResult = null;
		ghibSession = getSession();

		try {
			lSBQuery.append("select schemeCode, cast(sum(amount) as double) from \n");
			lSBQuery.append("TrnPensionRecoveryDtls where billNo = :billNo group by schemeCode \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("billNo", Long.parseLong(lStrBillNo));

			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	
	public List<MonthlyPensionBillVO> getMonthlyPensionBillVOListFrmBankBranch(
			String lStrBankCode, String lStrBranchCode,
			String lStrForMonthYear, String lStrLocCode, String lStrSortBy)  throws Exception{
		// TODO Auto-generated method stub
		List<MonthlyPensionBillVO> lLstMonthlyPensionBillVO = null;
		StringBuilder lSBQuery = new StringBuilder();
		ghibSession = getSession();
		List<Short> lLstBillStatus = new ArrayList<Short>();
		lLstBillStatus.add(DBConstants.ST_BILL_REJECTED);
		lLstBillStatus.add(DBConstants.ST_BILL_DISCARD);
		lLstBillStatus.add(DBConstants.ST_BILL_ARCHEIVED);
		try {
			lLstMonthlyPensionBillVO = new ArrayList<MonthlyPensionBillVO>();
			lSBQuery.append("SELECT new com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO(dtl.headCode,rqst.ledgerNo,rqst.pageNo,dtl.ppoNo,dtl.pensionerName,dtl.accountNo,"
					+ "dtl.allcationBf1436,dtl.allcationBf11156,dtl.allcationAf11156,dtl.allcationAf10560,dtl.allcationAfZp,dtl.ir1Amount,dtl.dpAmount,dtl.ir2Amount,dtl.tiAmount,dtl.ir3Amount,"
					+ "dtl.grossAmount,dtl.netAmount,dtl.tiArrearAmount,dtl.arrearAmount,dtl.recoveryAmount,dtl.peonAllowance,dtl.medicalAllowenceAmount,dtl.other1,dtl.otherBenefits,dtl.totalArrearAmt) \n");
			lSBQuery.append("FROM TrnBillRegister reg,TrnPensionBillHdr hdr,TrnPensionBillDtls dtl,TrnPensionRqstHdr rqst \n");
			lSBQuery.append("WHERE \n");
			lSBQuery.append("reg.billNo=hdr.billNo and reg.forMonth = :ForMonthYear and reg.currBillStatus not in (:billStatusList) \n");
			lSBQuery.append("AND hdr.trnPensionBillHdrId=dtl.trnPensionBillHdrId \n");
			lSBQuery.append("AND hdr.bankCode = :BankCode \n");
			lSBQuery.append("AND hdr.branchCode = :BranchCode \n");
			lSBQuery.append("AND dtl.pensionerCode=rqst.pensionerCode \n");
			lSBQuery.append("AND hdr.locationCode=:locationCode \n");
			if (lStrSortBy != null && !"-1".equals(lStrSortBy)) {
				if (bundleCaseConst.getString("ORDERBY.ACCOUNTNO").equals(lStrSortBy)) {
					lSBQuery.append("ORDER BY cast(dtl.accountNo as double) ");
				}
				if (bundleCaseConst.getString("ORDERBY.VOLUMEPAGENO").equals(lStrSortBy)) {
					lSBQuery.append("ORDER BY cast(rqst.ledgerNo,int),cast(rqst.pageNo,int) ");
				}
			} else {
				lSBQuery.append("ORDER BY rqst.ropType,cast(rqst.ledgerNo,int),cast(rqst.pageNo,int),dtl.headCode ");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			//lQuery.setLong("billNo", lLngBillNo);
			lQuery.setParameterList("billStatusList", lLstBillStatus);
			lQuery.setString("BankCode", lStrBankCode);
			lQuery.setString("BranchCode", lStrBranchCode);
			lQuery.setInteger("ForMonthYear", Integer.parseInt(lStrForMonthYear));
			lQuery.setString("locationCode", lStrLocCode);
			lLstMonthlyPensionBillVO = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstMonthlyPensionBillVO;
	}

	
	public List<MonthlyPensionBillVO> getMonthlyPensionBillBankBranchSummary(
			String lStrBankCode, String lStrBranchCode,
			String lStrForMonthYear, String lStrLocCode) throws Exception {
		// TODO Auto-generated method stub
		List<MonthlyPensionBillVO> lLstMonthlyPensionBillVO = null;
		StringBuilder lSBQuery = new StringBuilder();
		List<Short> lLstBillStatus = new ArrayList<Short>();
		lLstBillStatus.add(DBConstants.ST_BILL_REJECTED);
		lLstBillStatus.add(DBConstants.ST_BILL_DISCARD);
		lLstBillStatus.add(DBConstants.ST_BILL_ARCHEIVED);
		ghibSession = getSession();
		try {
			lLstMonthlyPensionBillVO = new ArrayList<MonthlyPensionBillVO>();
			lSBQuery.append("SELECT new com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO(count(dtl.pensionerName),sum(dtl.allcationBf1436),sum(dtl.allcationBf11156),sum(dtl.allcationAf11156),sum(dtl.allcationAf10560),sum(dtl.allcationAfZp),sum(dtl.ir1Amount),sum(dtl.dpAmount),sum(dtl.ir2Amount),sum(dtl.tiAmount),sum(dtl.ir3Amount),"
					+ "sum(dtl.grossAmount),sum(dtl.netAmount),sum(dtl.tiArrearAmount),sum(dtl.arrearAmount),mph.mainCatCode,mph.mainCatDesc,sum(dtl.recoveryAmount),sum(dtl.totalArrearAmt)) \n");
			lSBQuery.append("FROM TrnBillRegister reg,TrnPensionBillHdr hdr,TrnPensionBillDtls dtl,TrnPensionRqstHdr rqst,MstPensionHeadcode mph \n");
			
			lSBQuery.append("where reg.billNo=hdr.billNo and reg.forMonth = :ForMonthYear and reg.currBillStatus not in (:billStatusList) \n");
			lSBQuery.append("AND hdr.trnPensionBillHdrId=dtl.trnPensionBillHdrId \n");
			lSBQuery.append("AND hdr.bankCode = :BankCode \n");
			lSBQuery.append("AND hdr.branchCode = :BranchCode \n");
			lSBQuery.append("AND dtl.pensionerCode=rqst.pensionerCode \n");
			lSBQuery.append("AND dtl.headCode=mph.headCode \n");
			lSBQuery.append("AND hdr.locationCode=:locationCode \n");
			
//			lSBQuery.append("WHERE hdr.billNo=:billNo \n");
//			lSBQuery.append("AND reg.billNo=hdr.billNo \n");
//			lSBQuery.append("AND hdr.trnPensionBillHdrId=dtl.trnPensionBillHdrId \n");
//			lSBQuery.append("AND dtl.pensionerCode=rqst.pensionerCode \n");
//			lSBQuery.append("AND dtl.headCode=mph.headCode \n");
//			lSBQuery.append("AND hdr.locationCode=:locationCode \n");
			
			lSBQuery.append("GROUP BY mph.mainCatCode,mph.mainCatDesc \n");
			lSBQuery.append("ORDER BY mph.mainCatCode,mph.mainCatDesc ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("billStatusList", lLstBillStatus);
			lQuery.setString("BankCode", lStrBankCode);
			lQuery.setString("BranchCode", lStrBranchCode);
			lQuery.setInteger("ForMonthYear", Integer.parseInt(lStrForMonthYear));
			lQuery.setString("locationCode", lStrLocCode);
			//lQuery.setLong("billNo", lLngBillNo);
			
			lLstMonthlyPensionBillVO = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstMonthlyPensionBillVO;
	}
}