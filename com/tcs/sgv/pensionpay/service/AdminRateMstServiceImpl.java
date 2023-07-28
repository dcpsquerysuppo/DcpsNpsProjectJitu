package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
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

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.SgvaMonthMst;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.AdminRateMstDAO;
import com.tcs.sgv.pensionpay.dao.AdminRateMstDAOImpl;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAO;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.dao.RltPensionHeadcodeRateDAO;
import com.tcs.sgv.pensionpay.dao.RltPensionHeadcodeRateDAOImpl;
import com.tcs.sgv.pensionpay.dao.RltPensionRevisedPaymentDAO;
import com.tcs.sgv.pensionpay.dao.RltPensionRevisedPaymentDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionArrearDtlsDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionArrearDtlsDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO;
import com.tcs.sgv.pensionpay.valueobject.MstPensionStateRate;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerFamilyDtls;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeRate;
import com.tcs.sgv.pensionpay.valueobject.RltPensionRevisedPayment;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.ValidPcodeView;


public class AdminRateMstServiceImpl extends ServiceImpl {

	/* Global Variable for UserId */
	private Long gLngUserId = null;

	/* Global Variable for LangId */
	private Long gLngLangId = null;

	/* Global Variable for Logger Class */
	private Log gLogger = LogFactory.getLog(getClass());

	/* Global Variable for Current Date */
	private Date gDate = null;

	/* Global Variable for Location Code */
	private String gStrLocCode = null;

	private Long gLngPostId = null;
	private Long gLngDBId = null;

	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");
	private ResourceBundle bundleCaseConst = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	public ResultObject loadAdminRateMst(Map<String, Object> inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		String lStrLangId = SessionHelper.getLangId(inputMap).toString();

		setSessionInfo(inputMap);
		try {
			// To populate month combo....
			List<SgvaMonthMst> lObjSgvaMonthMst = new ArrayList<SgvaMonthMst>();

			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());

			Integer lIntCurrentMonth = DBUtility.getCurrentDateFromDB().getMonth() + 1;
			inputMap.put("CurrentMonth", lIntCurrentMonth);
			inputMap.put("CurrentYear", new SimpleDateFormat("yyyy").format(gDate));

			// Returning VO array...
			lObjSgvaMonthMst = lObjCommonPensionDAO.getSgvaMonthMstVO(lStrLangId);

			if (lObjSgvaMonthMst != null) {
				inputMap.put("SgvaMonthMstVOArray", lObjSgvaMonthMst);
			}

			// To populate year combo....
			List<SgvcFinYearMst> lObjSgvcFinYearMst = new ArrayList<SgvcFinYearMst>();

			// Returning VO array...
			lObjSgvcFinYearMst = lObjCommonPensionDAO.getSgvcFinYearMstVO(lStrLangId);

			if (lObjSgvcFinYearMst != null) {
				inputMap.put("SgvcFinYearMstVOArray", lObjSgvcFinYearMst);
			}

			// To populate HeadCode combo...
			AdminRateMstDAOImpl lObjAdminRateMst = new AdminRateMstDAOImpl(serv.getSessionFactory());

			List listPnsnHeadCode = lObjCommonPensionDAO.getAllHeadCode();

			if (listPnsnHeadCode != null) {
				inputMap.put("listHeadCode", listPnsnHeadCode);
			}

			// To populate TI rate type combo...
			List listTIRate = lObjAdminRateMst.getTIRateType(gLngLangId);

			List lLstStateDept = lObjAdminRateMst.getAllStateDept(gLngLangId);
			if (lLstStateDept != null) {
				inputMap.put("lLstStateDept", lLstStateDept);
			}

			if (listTIRate != null) {
				inputMap.put("listTIRate", listTIRate);
			}

			// To populate For Pension combo...
			List listForPension = lObjAdminRateMst.getForPension(gLngLangId);

			if (listForPension != null) {
				inputMap.put("listForPension", listForPension);
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("AdminRateMst");
		return resObj;
	}

	public ResultObject getDataFromHeadcode(Map<String, Object> inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrHeadCode = null;
		String lStrConfigType = null;
		String lStrTIRateType = null;
		String lStrForPension = null;
		Double lRate = 0D;
		// Double lAmt = 0D;
		String EffFrmDate = null;
		String tablePK = null;
		Calendar cal = Calendar.getInstance();
		List lLstHdRate = new ArrayList();

		try {
			setSessionInfo(inputMap);

			lStrHeadCode = StringUtility.getParameter("HeadCode", request);
			lStrConfigType = StringUtility.getParameter("ConfigType", request);
			lStrTIRateType = StringUtility.getParameter("TIRateType", request);
			lStrForPension = StringUtility.getParameter("ForPension", request);
			Long lngNewPK = IFMSCommonServiceImpl.getNextSeqNum("rlt_pension_headcode_rate", inputMap);

			String lStrCode = StringUtility.getParameter("code", request);
			inputMap.put("lngNewPK", lngNewPK);

			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			String strPnsnHeadCodeDesc = lObjCommonPensionDAO.getAllHeadCodeDesc(lStrHeadCode, SessionHelper.getLangId(inputMap));
			if (strPnsnHeadCodeDesc != null) {
				strPnsnHeadCodeDesc = strPnsnHeadCodeDesc.replace('&', '~');
				inputMap.put("HeadCodeDesc", strPnsnHeadCodeDesc);
			}

			/*
			 * StringBuilder lStrData = new StringBuilder();
			 * lStrData.append("<XMLDOC>"); lStrData.append("<HEADDESC>");
			 * lStrData.append(strPnsnHeadCodeDesc);
			 * lStrData.append("</HEADDESC>");
			 */

			AdminRateMstDAOImpl lObjAdminRateDAO = new AdminRateMstDAOImpl(serv.getSessionFactory());
			if ("TI".equals(lStrConfigType)) {

				// normal case
				// get vo of rlt_pension_headcode_rate
				lLstHdRate = new ArrayList();
				lLstHdRate = lObjAdminRateDAO.getRltPensionHeadcodeRate(lStrHeadCode, lStrConfigType, lStrTIRateType, lStrForPension);

				if (lLstHdRate != null && !lLstHdRate.isEmpty()) {
					if (lStrForPension.equals("UPTO 1750") && lLstHdRate.get(1) != null) {
						lRate = Double.parseDouble(lLstHdRate.get(1).toString());
					} else {
						if (lLstHdRate.get(1) != null) {
							lRate = Double.parseDouble(lLstHdRate.get(1).toString());
						}
						// if (lLstHdRate.get(2) != null) {
						// lAmt =
						// Double.parseDouble(lLstHdRate.get(2).toString());
						// }
					}
					tablePK = lLstHdRate.get(0).toString();
					cal.setTime((Date) lLstHdRate.get(2));

				}

			} else {
				// get vo of rlt_pension_headcode_rate
				lLstHdRate = new ArrayList();
				lLstHdRate = lObjAdminRateDAO.getRltPensionHeadcodeRate(lStrHeadCode, lStrConfigType, lStrTIRateType, lStrForPension);

				if (lLstHdRate != null && !lLstHdRate.isEmpty()) {
					if ("DP".equals(lStrConfigType) && lLstHdRate.get(1) != null) {
						lRate = Double.parseDouble(lLstHdRate.get(1).toString());
					} else if ("IR".equals(lStrConfigType)) {
						if (lLstHdRate.get(1) != null) {
							lRate = Double.parseDouble(lLstHdRate.get(1).toString());
						}
						// if (lLstHdRate.get(2) != null) {
						// lAmt =
						// Double.parseDouble(lLstHdRate.get(2).toString());
						// }
					} else if ("MA".equals(lStrConfigType) && lLstHdRate.get(2) != null) {
						lRate = Double.parseDouble(lLstHdRate.get(2).toString());
					}
					tablePK = lLstHdRate.get(0).toString();
					cal.setTime((Date) lLstHdRate.get(2));
				}
			}

			inputMap.put("rate", lRate);
			inputMap.put("tablePK", tablePK);

			List lLstDARateDetails = lObjAdminRateDAO.getDARateDetails(lStrTIRateType, lStrHeadCode, lStrForPension);
			inputMap.put("DARateDetails", lLstDARateDetails);

			if (tablePK == null) {
				/*
				 * lStrData.append("<Flag>"); lStrData.append("N");
				 * lStrData.append("</Flag>");
				 */
				inputMap.put("flag", "N");
			} else {
				/*
				 * lStrData.append("<Flag>"); lStrData.append("Y");
				 * lStrData.append("</Flag>");
				 */
				inputMap.put("flag", "Y");
				EffFrmDate = new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
				inputMap.put("EffFrmDate", EffFrmDate);
			}

			/*
			 * lStrData.append("<Rate>"); lStrData.append(lRate);
			 * lStrData.append("</Rate>"); lStrData.append("<tablePK>");
			 * lStrData.append(tablePK); lStrData.append("</tablePK>");
			 * lStrData.append("<tableNewPK>"); lStrData.append(lngNewPK);
			 * lStrData.append("</tableNewPK>");
			 * lStrData.append("<EffectDate>"); lStrData.append(EffFrmDate);
			 * lStrData.append("</EffectDate>");
			 * 
			 * lStrData.append("</XMLDOC>");
			 * 
			 * String lStrAjaxResult = new AjaxXmlBuilder().addItem("ajax_key",
			 * lStrData.toString()).toString();
			 */

			List listTIRate = lObjAdminRateDAO.getTIRateType(gLngLangId);
			if (listTIRate != null) {
				inputMap.put("listTIRate", listTIRate);
			}

			List lLstStateDept = lObjAdminRateDAO.getAllStateDept(gLngLangId);
			if (lLstStateDept != null) {
				inputMap.put("lLstStateDept", lLstStateDept);
			}

			inputMap.put("HeadCode", lStrHeadCode.trim());
			inputMap.put("TIRateType", lStrTIRateType.trim());

			List<SgvaMonthMst> lObjSgvaMonthMst = new ArrayList<SgvaMonthMst>();
			lObjSgvaMonthMst = lObjCommonPensionDAO.getSgvaMonthMstVO(gLngLangId.toString());

			if (lObjSgvaMonthMst != null) {
				inputMap.put("SgvaMonthMstVOArray", lObjSgvaMonthMst);
			}

			List<SgvcFinYearMst> lObjSgvcFinYearMst = new ArrayList<SgvcFinYearMst>();
			lObjSgvcFinYearMst = lObjCommonPensionDAO.getSgvcFinYearMstVO(gLngLangId.toString());

			if (lObjSgvcFinYearMst != null) {
				inputMap.put("SgvcFinYearMstVOArray", lObjSgvcFinYearMst);
			}

			resObj.setResultValue(inputMap);
			if (lStrCode.equals("HIS")) {
				resObj.setViewName("AdminRateHistory");
			} else if (lStrCode.equals("MST")) {
				resObj.setViewName("AdminRateMst");
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject saveAdmin(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		RltPensionRevisedPayment lObjPensionRevisedPayment = null;
		RltPensionRevisedPaymentDAO lObjRevisedPaymentDAO = new RltPensionRevisedPaymentDAOImpl(RltPensionRevisedPayment.class, serv.getSessionFactory());
		String lStrXMLMsg = "Records saved successfully";

		try {
			setSessionInfo(inputMap);

			// now we need to compute what amount has to be paid for all those
			// months
			// for all pensioners and make entries of those in arrears table
			List<TrnPensionArrearDtls> lLstArrear = getArrearForPayment(inputMap);

			/*
			 * int i = 1; HibernateTemplate hmt = new
			 * HibernateTemplate(serv.getSessionFactory()); if (lLstArrear !=
			 * null && !lLstArrear.isEmpty()) { Long ledgerID =
			 * IFMSCommonServiceImpl
			 * .getCurrentSeqNumAndUpdateCount("trn_pension_arrear_dtls",
			 * inputMap, lLstArrear.size()); for (TrnPensionArrearDtls
			 * lObjArrearDtl1 : lLstArrear) { if (i % 500 == 0) { i = 1;
			 * serv.getSessionFactory().getCurrentSession().flush(); }
			 * lObjArrearDtl1
			 * .setPensionArrearDtlsId(IFMSCommonServiceImpl.getFormattedPrimaryKey
			 * (++ledgerID, inputMap)); hmt.save(lObjArrearDtl1); i++; } }
			 */

			// Code Commented by vrajesh: Start
			// if ("16".equals(lHeadCode) || "17".equals(lHeadCode) ||
			// "18".equals(lHeadCode)
			// || "19".equals(lHeadCode)) {
			// // special headcode
			// RltPensionHeadcodeSpecialDAO lObjHeadcodeSpecialDAO = new
			// RltPensionHeadcodeSpecialDAOImpl(
			// RltPensionHeadcodeSpecial.class, serv.getSessionFactory());
			// RltPensionHeadcodeSpecial lObjHeadcodeSpecial = new
			// RltPensionHeadcodeSpecial();
			// lObjHeadcodeSpecial = (RltPensionHeadcodeSpecial) inputMap
			// .get("RltPensionHeadcodeSpecialOld");
			// lObjHeadcodeSpecialDAO.update(lObjHeadcodeSpecial);
			//
			// lObjHeadcodeSpecial = new RltPensionHeadcodeSpecial();
			// lObjHeadcodeSpecial = (RltPensionHeadcodeSpecial) inputMap
			// .get("RltPensionHeadcodeSpecialNew");
			// //
			// lObjHeadcodeSpecial.setPensionHeadcodeSpecialId(IFMSCommonServiceImpl.getNextSeqNum("rlt_pension_headcode_special",
			// // inputMap));
			// lObjHeadcodeSpecialDAO.create(lObjHeadcodeSpecial);
			// } else {
			// normal headcode
			RltPensionHeadcodeRateDAO lObjHeadcodeRateDAO = new RltPensionHeadcodeRateDAOImpl(RltPensionHeadcodeRate.class, serv.getSessionFactory());
			RltPensionHeadcodeRate lObjHeadcodeRate = new RltPensionHeadcodeRate();
			lObjHeadcodeRate = (RltPensionHeadcodeRate) inputMap.get("RltPensionHeadcodeRateOld");
			lObjHeadcodeRateDAO.update(lObjHeadcodeRate);

			lObjHeadcodeRate = new RltPensionHeadcodeRate();
			lObjHeadcodeRate = (RltPensionHeadcodeRate) inputMap.get("RltPensionHeadcodeRateNew");
			// lObjHeadcodeRate.setPensionHeadcodeRateId(IFMSCommonServiceImpl.getNextSeqNum("rlt_pension_headcode_rate",
			// inputMap));
			lObjHeadcodeRateDAO.create(lObjHeadcodeRate);
			// }
			// Code Commented by vrajesh: End

			// now we have to do entries in rlt_pension_revised_payment
			/*
			 * List<RltPensionRevisedPayment> lLstRevisedPayment =
			 * (List<RltPensionRevisedPayment>)
			 * inputMap.get("LstRevisedPayment"); if (lLstRevisedPayment != null
			 * && !lLstRevisedPayment.isEmpty()) { Long ledgerID =
			 * IFMSCommonServiceImpl
			 * .getCurrentSeqNumAndUpdateCount("rlt_pension_revised_payment",
			 * inputMap, lLstRevisedPayment.size()); for (int z = 0; z <
			 * lLstRevisedPayment.size(); z++) { lObjPensionRevisedPayment = new
			 * RltPensionRevisedPayment(); lObjPensionRevisedPayment =
			 * lLstRevisedPayment.get(z);
			 * lObjPensionRevisedPayment.setPensionRevisedPaymentId
			 * (IFMSCommonServiceImpl.getFormattedPrimaryKey(++ledgerID,
			 * inputMap));
			 * lObjRevisedPaymentDAO.create(lObjPensionRevisedPayment); } }
			 */

			StringBuffer lStrBldXML = new StringBuffer();

			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>");
			lStrBldXML.append(lStrXMLMsg);
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("</XMLDOC>");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();

			inputMap.put("ajaxKey", lStrResult);

			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

	List<TrnPensionArrearDtls> getArrearForPayment(Map<String, Object> inputMap) throws Exception {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		List<TrnPensionArrearDtls> lLstArrearList = new ArrayList<TrnPensionArrearDtls>();
		TrnPensionArrearDtls lObjArrearVO = null;
		AdminRateMstDAOImpl lObjAdminDAO = new AdminRateMstDAOImpl(serv.getSessionFactory());
		RltPensionRevisedPayment lObjRevPay = null;
		List lLstReqdData = new ArrayList();
		Date lCalcToDt = null;
		Date lDateOfDeath = null;
		Date lEndDate = null;
		// Date lCommDate = null;
		// Integer lDaysOfComm = 0;
		Integer lcuryyyyMM = 0;
		// Integer lCommyyyyMM = 0;
		// Integer lDaysOfMonth = 0;
		Integer lEndYYYYMM = 0;
		Integer lDODYYYYMM = 0;

		ValidPcodeView lObjValidPcode = null;
		Map lMapFamily = new HashMap<String, MstPensionerFamilyDtls>();
		// Map lMapPenCut = new HashMap();
		MstPensionerFamilyDtls lLstFPmembers = null;
		List fpMemberList = new ArrayList();
		Date lFPDODdate = null;
		PensionBillProcessServiceImpl lObjPensionBill = new PensionBillProcessServiceImpl();
		MonthlyPensionBillVO lPensionBillVO = null;

		String lStrPensionFlag = bundleConst.getString("RECOVERY.PENSION");

		try {
			setSessionInfo(inputMap);

			String lStateCode = inputMap.get("HeadCode").toString();
			String lFieldType = inputMap.get("Fieldtype").toString();
			Double lOldRate = Double.parseDouble(inputMap.get("OldRate").toString());
			Double lNewRate = Double.parseDouble(inputMap.get("NewRate").toString());
			List<RltPensionRevisedPayment> lLstRevisedPayment = (List<RltPensionRevisedPayment>) inputMap.get("LstRevisedPayment");
			String lStrTIStyle = null;
			// fetching family details and putting conditions for end date.
			Map<String, Object> lNewMap = new HashMap<String, Object>();
			Double lArrears = 0D;
			Double lPrevTIAmnt = 0D;
			Double lNewTIAmnt = 0D;
			String lStrLstPdDt = null;
			String lStrForPension = null;
			inputMap.put("BillType", lStrPensionFlag);
			String lStrCalcArrear = (String) inputMap.get("CalcArrear");
			if (inputMap.containsKey("TIStyle")) {
				lStrTIStyle = inputMap.get("TIStyle").toString();
			}

			if ("Y".equals(lStrCalcArrear)) {
				if (lStrTIStyle.equals("DA_2006")) {
					lLstReqdData = lObjAdminDAO.getRecordsFor6ThPay(gStrLocCode, lStateCode);
					lMapFamily = lObjAdminDAO.getMstFamilyDtlsMap(lStateCode);
				} else if (lStrTIStyle.equals("DA_1996_DP")) {
					lLstReqdData = lObjAdminDAO.getRecordsFor5ThPay(gStrLocCode, lStateCode);
					lMapFamily = lObjAdminDAO.getMstFamilyDtlsMapFor5thPay(lStateCode);
				} else if (lStrTIStyle.equals("DA_1986")) {
					lStrForPension = inputMap.get("ForPension").toString();
					lLstReqdData = lObjAdminDAO.getRecordsFor4thPay(lStrForPension, lStateCode);
					lMapFamily = lObjAdminDAO.getMstFamilyDtlsMapFor4thPay(lStateCode, lStrForPension);
					// System.out.println("Length = "+ lLstReqdData.size());
				}
			}
			for (int x = 0; x < lLstRevisedPayment.size(); x++) {
				lObjRevPay = lLstRevisedPayment.get(x);
				Integer lFromMonth = lObjRevPay.getForPayMonth();
				Integer lToMonth = lObjRevPay.getToPayMonth();
				Integer lInMonth = lObjRevPay.getPayInMonth();
				// inputMap.put("lMapPenCut", lMapPenCut);

				if (lStrTIStyle.equals("DA_2006") || lStrTIStyle.equals("DA_1996_DP") || lStrTIStyle.equals("DA_1986")) {

					if (lLstReqdData != null && !lLstReqdData.isEmpty()) {

						// if (lStrTIStyle.equals("DA_1996_DP")) {
						// lMapPenCut =
						// lObjAdminDAO.getPenCutMapFor5thPay(lHeadCode,
						// lFromMonth, lToMonth);
						// } else if (lStrTIStyle.equals("DA_2006")) {
						// lMapPenCut = lObjAdminDAO.getPenCutMap(lHeadCode,
						// lFromMonth, lToMonth);
						// } else if (lStrTIStyle.equals("DA_1986")) {
						// lMapPenCut =
						// lObjAdminDAO.getPenCutMapFor4thPay(lHeadCode,
						// lFromMonth, lToMonth, lStrForPension);
						// }

						for (int y = 0; y < lLstReqdData.size(); y++) {

							lObjValidPcode = new ValidPcodeView();
							lObjValidPcode = (ValidPcodeView) lLstReqdData.get(y);

							lCalcToDt = lObjValidPcode.getLastPaidDate();

							if (lObjValidPcode.getCommensionDate() != null) {
								if ((Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lObjValidPcode.getCommensionDate()))) > lFromMonth) {
									lFromMonth = Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lObjValidPcode.getCommensionDate()));
								}
							}

							if (lObjValidPcode.getLastPaidDate() != null) {
								lStrLstPdDt = new SimpleDateFormat("yyyyMM").format(lCalcToDt);

								if (lToMonth >= Integer.valueOf(lStrLstPdDt)) {
									lToMonth = Integer.valueOf(lStrLstPdDt);
								}
							}
							// System.out.println(lObjValidPcode.getPpoNo()+
							// " "+lFromMonth+ " "+ lToMonth);
							inputMap.put("Date", new SimpleDateFormat("yyyyMM").format(SessionHelper.getCurDate()));

							lToMonth = (lToMonth % 100 == 12) ? (lToMonth + 89) : (lToMonth + 1);

							for (Integer i = lFromMonth; i < lToMonth;) {
								// System.out.println("i + pCode -- >"+i+" "+lObjValidPcode.getPensionerCode());
								inputMap.put("Date", i);

								if (lObjValidPcode.getEndDate() != null && lObjValidPcode.getLastPaidDate() != null && lObjValidPcode.getEndDate().before(lObjValidPcode.getLastPaidDate())) {
									lObjValidPcode.setEndDate(null);
								}

								if (lObjValidPcode.getEndDate() != null) {
									lEndDate = lObjValidPcode.getEndDate();
									lEndYYYYMM = Integer.parseInt(new SimpleDateFormat("yyyyMM").format(lEndDate));
									lcuryyyyMM = Integer.valueOf(new SimpleDateFormat("yyyyMM").format(gDate));
									if (lEndYYYYMM != 0 && lcuryyyyMM >= lEndYYYYMM) // checking
									// EndDate
									// before
									// Current
									// month
									// or
									// not.
									{
										lcuryyyyMM = lEndYYYYMM;
									}
								}

								lDateOfDeath = lObjValidPcode.getDateOfDeath();

								if (lDateOfDeath != null) {
									lDODYYYYMM = Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lDateOfDeath));

									if (lMapFamily != null && !lMapFamily.isEmpty() && lDateOfDeath != null && lMapFamily.containsKey("Family" + lObjValidPcode.getPensionerCode())) {
										lLstFPmembers = (MstPensionerFamilyDtls) (lMapFamily.get("Family" + lObjValidPcode.getPensionerCode()));
									}
									if (lLstFPmembers != null) {
										fpMemberList = lObjPensionBill.getFpMemberList(lLstFPmembers);
										lFPDODdate = lLstFPmembers.getDateOfDeath();
									}
									if (!fpMemberList.isEmpty() && lFPDODdate != null) {
										if (lFPDODdate.after(lDateOfDeath)) {
											if (lEndDate == null) {
												lEndDate = lFPDODdate;
											} else if (lEndDate != null && (lFPDODdate.before(lEndDate) || lFPDODdate.equals(lEndDate))) {
												lEndDate = lFPDODdate;
											}
										} else {
											if (lEndDate == null) {
												lEndDate = lDateOfDeath;
											} else if (lEndDate != null && (lDateOfDeath.before(lEndDate) || lDateOfDeath.equals(lEndDate))) {
												lEndDate = lDateOfDeath;
											}
										}
										lEndYYYYMM = Integer.parseInt(new SimpleDateFormat("yyyyMM").format(lEndDate));
									} else if (fpMemberList.isEmpty()) {
										if (lEndDate == null) {
											lEndDate = lDateOfDeath;
										} else if (lEndDate != null && (lDateOfDeath.before(lEndDate) || lDateOfDeath.equals(lEndDate))) {
											lEndDate = lDateOfDeath;
										}
										lEndYYYYMM = Integer.parseInt(new SimpleDateFormat("yyyyMM").format(lEndDate));
									}
									inputMap.put("EndDate", lEndDate);
									inputMap.put("FPmembersVo", lLstFPmembers);
									inputMap.put("fpMemberList", fpMemberList);

								}

								if (lEndYYYYMM != 0 && lcuryyyyMM >= lEndYYYYMM) // checking
								// EndDate
								// before
								// Current
								// month
								// or
								// not.
								{
									lcuryyyyMM = lEndYYYYMM;
								}
								inputMap.put("BillCrtMonth", lcuryyyyMM);
								inputMap.put("DateOfDeath", lObjValidPcode.getDateOfDeath());
								inputMap.put("DateOfRetirement", lObjValidPcode.getDateOfRetirement());
								inputMap.put("ArrComputeFlag", "N");
								inputMap.put("lPendingArrear", 0);
								inputMap.put("lMapFamilyDtls", lMapFamily);
								inputMap.put("EndDate", lEndDate);
								inputMap.put("lObjValidPcode", lObjValidPcode);

								inputMap.put("NewPensionBasic", Double.valueOf(lObjValidPcode.getBasicPensionAmount().toString()));
								inputMap.put("NewFP1Basic", Double.valueOf(lObjValidPcode.getFp1Amount().toString()));
								inputMap.put("NewFP2Basic", Double.valueOf(lObjValidPcode.getFp2Amount().toString()));

								inputMap.put("ROP_1986", "P");
								if (lStrTIStyle.equals("DA_1986")) {
									inputMap.put("ROP_1996", "N");
								} else {
									inputMap.put("ROP_1996", "P");
								}

								if (lStrTIStyle.equals("DA_1996_DP") || lStrTIStyle.equals("DA_1986")) {
									inputMap.put("ROP_2006", "N");
								} else {
									inputMap.put("ROP_2006", "P");
								}

								inputMap.put("PayStartDate", lObjValidPcode.getCommensionDate());

								if (lDateOfDeath != null && i >= lDODYYYYMM) {
									inputMap.put("FPFlag", "Y");
								}
								inputMap.put("fromAdmin", "Y");

								if (lEndYYYYMM == 0 || lEndYYYYMM >= i) {
									// Previous TI Amount
									inputMap.put("lDiffRate", lOldRate);
									lNewMap = lObjPensionBill.getCurrMonthData(inputMap);
									if ((List) lNewMap.get("lLstMonthlyPensionBillVO") != null && !((List) lNewMap.get("lLstMonthlyPensionBillVO")).isEmpty()) {
										lPensionBillVO = (MonthlyPensionBillVO) ((List) lNewMap.get("lLstMonthlyPensionBillVO")).get(0);
									}
									if (lPensionBillVO != null) {
										lPrevTIAmnt = Double.parseDouble(lPensionBillVO.getTiPercentAmount().toString());
										if (lStrTIStyle.equals("DA_1996_DP") || lStrTIStyle.equals("DA_1986")) {
											lPrevTIAmnt = (double) Math.round(lPrevTIAmnt);
										} else {
											lPrevTIAmnt = Math.ceil(lPrevTIAmnt);
										}
									}

									// New TI Amount
									inputMap.put("lDiffRate", lNewRate);
									lNewMap = lObjPensionBill.getCurrMonthData(inputMap);
									if ((List) lNewMap.get("lLstMonthlyPensionBillVO") != null && !((List) lNewMap.get("lLstMonthlyPensionBillVO")).isEmpty()) {
										lPensionBillVO = (MonthlyPensionBillVO) ((List) lNewMap.get("lLstMonthlyPensionBillVO")).get(0);
									}
									if (lPensionBillVO != null) {
										lNewTIAmnt = Double.parseDouble(lPensionBillVO.getTiPercentAmount().toString());
										if (lStrTIStyle.equals("DA_1996_DP") || lStrTIStyle.equals("DA_1986")) {
											lNewTIAmnt = (double) Math.round(lNewTIAmnt);
										} else {
											lNewTIAmnt = Math.ceil(lNewTIAmnt);
										}
									}

									lArrears += lNewTIAmnt - lPrevTIAmnt;

								}
								i += ((Integer.parseInt((i.toString().substring(4, 6)))) == 12) ? 89 : 1;

							} // month wise loop end
								// System.out.print(" arrear -- >"+lArrears);

							if (lArrears != 0D) {
								lObjArrearVO = new TrnPensionArrearDtls();

								lObjArrearVO.setPensionerCode(lObjValidPcode.getPensionerCode());
								lObjArrearVO.setPensionRequestId(new Long(lObjValidPcode.getPensionRequestId()));
								if ("DP".equals(lFieldType) || "IR".equals(lFieldType)) {
									lObjArrearVO.setArrearFieldType("Pension");
								} else {
									lObjArrearVO.setArrearFieldType(lFieldType);
								}
								lObjArrearVO.setEffectedFromYyyymm(lFromMonth);
								lObjArrearVO.setEffectedToYyyymm(lObjRevPay.getToPayMonth());
								lObjArrearVO.setOldAmountPercentage(new BigDecimal(lOldRate));
								lObjArrearVO.setNewAmountPercentage(new BigDecimal(lNewRate));

								lObjArrearVO.setPaymentFromYyyymm(lInMonth);
								lObjArrearVO.setPaymentToYyyymm(lInMonth);
								lObjArrearVO.setCreatedDate(gDate);
								if (lStrTIStyle.equals("DA_1996_DP") || lStrTIStyle.equals("DA_1986")) {
									lObjArrearVO.setDifferenceAmount(new BigDecimal(Math.round(lArrears)));
									lObjArrearVO.setTotalDifferenceAmt(new BigDecimal(Math.round(lArrears)));
									lObjArrearVO.setCreatedPostId(BigDecimal.ONE);
								} else {
									lObjArrearVO.setDifferenceAmount(new BigDecimal(Math.ceil(lArrears)));
									lObjArrearVO.setTotalDifferenceAmt(new BigDecimal(Math.ceil(lArrears)));
									lObjArrearVO.setCreatedPostId(BigDecimal.ZERO);
								}
								lObjArrearVO.setCreatedUserId(new BigDecimal(gLngUserId));

								lLstArrearList.add(lObjArrearVO);
							}
							lArrears = 0D;
							lObjValidPcode = null;
							lDateOfDeath = null;
							lEndYYYYMM = 0;
							lEndDate = null;
							lToMonth = lObjRevPay.getToPayMonth();
							lFromMonth = lObjRevPay.getForPayMonth();
						}
					}
				}

			}

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstArrearList;
	}

	public ResultObject checkQueue(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		StringBuilder lStrBldXML = new StringBuilder();
		try {
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

			String lStrActv = "N";
			String lHeadCode = StringUtility.getParameter("HeadCode", request).trim();

			AdminRateMstDAOImpl lObjAdminDAO = new AdminRateMstDAOImpl(serv.getSessionFactory());
			lStrActv = lObjAdminDAO.chkStatusForHeadCode(lHeadCode);

			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>");
			lStrBldXML.append(lStrActv);
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("</XMLDOC>");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}
		return objRes;
	}

	public ResultObject loadDARateHistoryConfig(Map<String, Object> inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		// String lStrLangId = SessionHelper.getLangId(inputMap).toString();
		String lStrHistory = null;
		String lStrDARateType = null;
		String lStrHeadCodeType = null;
		String lStrHeadCodeTypeText = null;
		String lStrForPension = null;
		List lLstDARateDetails = null;
		setSessionInfo(inputMap);

		try {
			lStrHistory = StringUtility.getParameter("History", request);
			lStrDARateType = StringUtility.getParameter("TIRateTypeText", request);
			lStrHeadCodeType = StringUtility.getParameter("headCodeType", request);
			lStrHeadCodeTypeText = StringUtility.getParameter("headCodeTypeText", request);
			lStrForPension = StringUtility.getParameter("forPension", request);
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			// To populate HeadCode combo...
			AdminRateMstDAOImpl lObjAdminRateMst = new AdminRateMstDAOImpl(serv.getSessionFactory());

			// To populate TI rate type combo...
			List listTIRate = lObjAdminRateMst.getTIRateType(gLngLangId);

			List listPnsnHeadCode = lObjCommonPensionDAO.getAllHeadCode();

			// To populate For Pension combo...
			List listForPension = lObjAdminRateMst.getForPension(gLngLangId);

			if (lStrDARateType.length() > 0) {
				inputMap.put("TIRateTypeText", lStrDARateType.trim());
			}
			if (lStrHeadCodeTypeText.length() > 0) {
				inputMap.put("HeadCodeTypeText", lStrHeadCodeTypeText.trim());
			}
			if (lStrForPension.length() > 0) {
				inputMap.put("ForPension", lStrForPension.trim());
			}

			if (listForPension != null) {
				inputMap.put("listForPension", listForPension);
			}

			if (listPnsnHeadCode != null) {
				inputMap.put("listHeadCode", listPnsnHeadCode);
			}

			if (listTIRate != null) {
				inputMap.put("listTIRate", listTIRate);
			}

			List lLstStateDept = lObjAdminRateMst.getAllStateDept(gLngLangId);
			if (lLstStateDept != null) {
				inputMap.put("lLstStateDept", lLstStateDept);
			}

			if (lStrHistory.length() > 0) {
				if (!lStrDARateType.equals("DA_1986") && !lStrDARateType.equals("")) {
					lStrForPension = "";
				}
				if (lStrDARateType.length() > 0) {
					lLstDARateDetails = lObjAdminRateMst.getDARateDetails(lStrDARateType, lStrHeadCodeType, lStrForPension);
				}

				Iterator lObjiterator = lLstDARateDetails.iterator();
				Object[] lArrObj = null;
				int lIntCnt = 0;
				List<Date> lLstFromDate = new ArrayList<Date>();
				List<Date> lLstToDate = new ArrayList<Date>();
				List<BigDecimal> lLstRate = new ArrayList<BigDecimal>();
				List<BigDecimal> lLstMinAmnt = new ArrayList<BigDecimal>();
				Date lDtFromDate = null;
				Date lDtToDate = null;
				BigDecimal lBDRate = BigDecimal.ZERO;
				BigDecimal lBDMinAmnt = BigDecimal.ZERO;
				while (lObjiterator.hasNext()) {
					lArrObj = (Object[]) lObjiterator.next();

					if (lArrObj[0] == null) {
						lDtFromDate = null;
					} else {
						lDtFromDate = (Date) lArrObj[0];
					}
					if (lArrObj[1] == null) {
						lDtToDate = null;
					} else {
						lDtToDate = (Date) lArrObj[1];
					}

					if (lArrObj[2] != null) {
						lBDRate = (BigDecimal) lArrObj[2];
					}
					if (lArrObj[3] != null) {
						lBDMinAmnt = (BigDecimal) lArrObj[3];
					}

					lLstFromDate.add(lDtFromDate);
					lLstToDate.add(lDtToDate);
					lLstRate.add(lBDRate);
					lLstMinAmnt.add(lBDMinAmnt);

					lIntCnt++;
				}

				inputMap.put("lLstFromDate", lLstFromDate);
				inputMap.put("lLstToDate", lLstToDate);
				inputMap.put("lLstRate", lLstRate);
				inputMap.put("lLstMinAmnt", lLstMinAmnt);
				inputMap.put("LoopIndex", lLstDARateDetails.size());

				objRes.setResultCode(ErrorConstants.SUCCESS);
				objRes.setResultValue(inputMap);
				objRes.setViewName("AdminRateHistoryPopUp");
			} else {
				objRes.setResultCode(ErrorConstants.SUCCESS);
				objRes.setResultValue(inputMap);
				objRes.setViewName("AdminRateHistory");
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}

		return objRes;

	}

	public ResultObject chkDateIsOverLapOrNot(Map<String, Object> inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		AdminRateMstDAO lObjAdminRateMstDAO = null;
		String lStrDARateType = null;
		String lStrHeadCodeType = null;
		String lStrForPension = null;
		String lStrResVal = null;
		String lStrFromDate = null;
		String lStrToDate = null;
		List lLstDARateDetails = null;
		Boolean lBlResFlag = false;
		StringBuilder lStrBldXML = null;
		String lStrResult = null;
		try {
			setSessionInfo(inputMap);
			lStrDARateType = StringUtility.getParameter("TiRateTypeText", request);
			lStrForPension = StringUtility.getParameter("ForPensionText", request);
			lStrHeadCodeType = StringUtility.getParameter("HeadCode", request);
			lStrFromDate = StringUtility.getParameter("fromDate", request);
			lStrToDate = StringUtility.getParameter("toDate", request);
			lObjAdminRateMstDAO = new AdminRateMstDAOImpl(serv.getSessionFactory());

			lLstDARateDetails = lObjAdminRateMstDAO.getDARateDetails(lStrDARateType, lStrHeadCodeType, lStrForPension);

			Iterator lObjiterator = lLstDARateDetails.iterator();
			Object[] lArrObj = null;
			int lIntCnt = 0;
			Date lDtEffctvFromDate = null;
			Date lDtEffctvToDate = null;
			List<String> lLstResVal = new ArrayList<String>();
			while (lObjiterator.hasNext()) {
				lArrObj = (Object[]) lObjiterator.next();

				if (lArrObj[0] == null) {
					lDtEffctvFromDate = null;
				} else {
					lDtEffctvFromDate = (Date) lArrObj[0];
				}
				if (lArrObj[1] == null) {
					lDtEffctvToDate = DBUtility.getCurrentDateFromDB();
				} else {
					lDtEffctvToDate = (Date) lArrObj[1];
				}
				lStrResVal = lObjAdminRateMstDAO.chkDateIsOverLapOrNot(lStrDARateType, lStrForPension, lStrHeadCodeType, StringUtility.convertStringToDate(lStrFromDate),
						StringUtility.convertStringToDate(lStrToDate), lDtEffctvFromDate, lDtEffctvToDate);
				lLstResVal.add(lStrResVal);
				lIntCnt++;
			}
			if (lLstResVal.contains("Y")) {
				lBlResFlag = true;
			}

			lStrBldXML = getResponseXMLDoc(lBlResFlag);
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);

			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

	private StringBuilder getResponseXMLDoc(Boolean lBlResFlag) {

		StringBuilder lStrHidPKs = new StringBuilder();

		lStrHidPKs.append("<XMLDOCCHECKDATE>");
		lStrHidPKs.append("<RESFLAG>");
		lStrHidPKs.append(lBlResFlag);
		lStrHidPKs.append("</RESFLAG>");
		lStrHidPKs.append("</XMLDOCCHECKDATE>");

		gLogger.info("lStrHidPKs : " + lStrHidPKs);
		return lStrHidPKs;

	}

	public ResultObject saveAdminHistory(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		Long lLngPensionHeadcodeRateId = 0l;
		RltPensionHeadcodeRate lObjRltPensionHeadcodeRate = new RltPensionHeadcodeRate();
		List<RltPensionHeadcodeRate> lLstRltPensionHeadcodeRate = new ArrayList<RltPensionHeadcodeRate>();
		String lStrFinalData = "";

		try {
			RltPensionHeadcodeRateDAO lObjRltPensionHeadcodeRateDAO = new RltPensionHeadcodeRateDAOImpl(RltPensionHeadcodeRate.class, serv.getSessionFactory());
			String lStrDateChk = (String) inputMap.get("maxDate");
			if (!lStrDateChk.equals("Y")) {
				lLstRltPensionHeadcodeRate = (List<RltPensionHeadcodeRate>) inputMap.get("lLstRltPensionHeadcodeRate");
				if (lLstRltPensionHeadcodeRate != null) {
					for (int lIntCount = 0; lIntCount < lLstRltPensionHeadcodeRate.size(); lIntCount++) {
						lObjRltPensionHeadcodeRate = lLstRltPensionHeadcodeRate.get(lIntCount);
						lLngPensionHeadcodeRateId = IFMSCommonServiceImpl.getNextSeqNum("rlt_pension_headcode_rate", inputMap);
						lObjRltPensionHeadcodeRate.setPensionHeadcodeRateId(lLngPensionHeadcodeRateId);
						lObjRltPensionHeadcodeRateDAO.create(lObjRltPensionHeadcodeRate);
						gLogger.info("Record Inserted in table rlt_pension_headcode_rate successfully.");
					}
				}
				lStrFinalData = "Add";
			} else {
				lStrFinalData = "MAX";
			}
			StringBuffer lStrBldXML = new StringBuffer();

			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>");
			lStrBldXML.append(lStrFinalData);
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("</XMLDOC>");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();

			inputMap.put("ajaxKey", lStrResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

	public ResultObject loadDARateConfigForState(Map<String, Object> inputMap) {

		gLogger.info("In loadDARateConfigForState method.......");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		AdminRateMstDAO lObjAdminRateMstDAO = null;
		List lLstDARateConfigForStateDtls = null;
		Integer lIntTotalRecords = null;
		// List lLstDARateForStateId = null;
		try {
			setSessionInfo(inputMap);
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			lObjAdminRateMstDAO = new AdminRateMstDAOImpl(serv.getSessionFactory());
			lIntTotalRecords = lObjAdminRateMstDAO.getDARateConfigForStateCount(gLngLangId, gStrLocCode, displayTag);
			lLstDARateConfigForStateDtls = lObjAdminRateMstDAO.getDARateConfigForStateDtls(gLngLangId, gStrLocCode, displayTag);
			// lLstDARateForStateId =
			// lObjAdminRateMstDAO.getAllDARateStateId(gLngLangId);

			inputMap.put("totalRecords", lIntTotalRecords);
			inputMap.put("lLstDARateConfigForStateDtls", lLstDARateConfigForStateDtls);
			// inputMap.put("lLstMainCategoryId", lLstMainCategory);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("adminRateMstForState");
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			// e.printStackTrace();

		}
		return resObj;

	}

	public ResultObject saveDARateConfigForState(Map<String, Object> inputMap) {

		gLogger.info("In saveDARateConfigForState method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = null;
		StringBuilder lStrBldXML = null;
		String lStrResult = null;
		String lStrTransMode = null;
		MstPensionStateRate lObjMstPensionStateRate = null;
		Long lLngMstPensionStateRateId = null;
		try {
			setSessionInfo(inputMap);
			lObjMstPensionStateRate = new MstPensionStateRate();
			lStrTransMode = (String) inputMap.get("Mode");
			lObjMstPensionStateRate = (MstPensionStateRate) inputMap.get("lObjMstPensionStateRate");
			lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionStateRate.class, serv.getSessionFactory());
			if (lStrTransMode.equalsIgnoreCase("Add")) {
				if (lObjMstPensionStateRate != null) {
					lLngMstPensionStateRateId = IFMSCommonServiceImpl.getNextSeqNum("MST_PENSION_STATE_RATE", inputMap);
					lObjMstPensionStateRate.setPensionStateRateId(lLngMstPensionStateRateId);
					lObjPhysicalCaseInwardDAO.create(lObjMstPensionStateRate);
					gLogger.info("Record Inserted in table mst_pension_state_rate successfully.");
				}
			} else if (lStrTransMode.equalsIgnoreCase("Update")) {
				String lStrStateDesc = StringUtility.getParameter("txtStateDesc", request);
				if (!"".equals(lStrStateDesc) && lStrStateDesc.length() > 0) {
					lObjMstPensionStateRate.setStateDesc(lStrStateDesc.trim());
				}

				lObjPhysicalCaseInwardDAO.update(lObjMstPensionStateRate);

			} else // delete
			{
				String lStrMstPensionStateRateId = StringUtility.getParameter("MstPensionStateRateId", request);
				String[] lStrMstPensionStateRateIdArr = lStrMstPensionStateRateId.split("~");
				for (int lIntCnt = 0; lIntCnt < lStrMstPensionStateRateIdArr.length; lIntCnt++) {
					lObjMstPensionStateRate = (MstPensionStateRate) lObjPhysicalCaseInwardDAO.read(Long.valueOf(lStrMstPensionStateRateIdArr[lIntCnt].trim()));
					lObjPhysicalCaseInwardDAO.delete(lObjMstPensionStateRate);
				}
			}
			lStrBldXML = getResponseXMLDoc(inputMap, lStrTransMode);
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);

			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			// e.printStackTrace();

		}
		return objRes;

	}

	private StringBuilder getResponseXMLDoc(Map inputMap, String strMode) {

		StringBuilder lStrHidPKs = new StringBuilder();
		if (strMode.equalsIgnoreCase("Add")) {
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append("Add");
			lStrHidPKs.append("</MESSAGECODE>");
			lStrHidPKs.append("</XMLDOC>");

		}
		if (strMode.equals("Update")) {
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append("Update");
			lStrHidPKs.append("</MESSAGECODE>");
			lStrHidPKs.append("</XMLDOC>");
		}
		if (strMode.equals("Delete")) {
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append("Delete");
			lStrHidPKs.append("</MESSAGECODE>");
			lStrHidPKs.append("</XMLDOC>");
		}
		gLogger.info("lStrHidPKs : " + lStrHidPKs);
		return lStrHidPKs;

	}

	public ResultObject loadDARateArrearConfig(Map<String, Object> inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrLangId = SessionHelper.getLangId(inputMap).toString();
		CommonDAO lObjCommonDAO = null;
		List<ComboValuesVO> lLstAllTreasury = null;
		List<Long> lLstTreasuryId = new ArrayList<Long>();
		String lStrCalcDAArrearBy = null;
		try {
			setSessionInfo(inputMap);
			lStrCalcDAArrearBy = StringUtility.getParameter("calcArrearBy", request).trim();// ---If
																							// lStrCalcDAArrearBy
																							// is
																							// 'P'
																							// then
																							// calculate
																							// da
																							// arrear
																							// by
																							// Payment
																							// history
																							// else
																							// by
																							// current
																							// value.
			List<SgvaMonthMst> lObjSgvaMonthMst = new ArrayList<SgvaMonthMst>();

			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());

			Integer lIntCurrentMonth = DBUtility.getCurrentDateFromDB().getMonth() + 1;
			inputMap.put("CurrentMonth", lIntCurrentMonth);
			inputMap.put("CurrentYear", new SimpleDateFormat("yyyy").format(gDate));

			// Returning VO array...
			lObjSgvaMonthMst = lObjCommonPensionDAO.getSgvaMonthMstVO(lStrLangId);

			if (lObjSgvaMonthMst != null) {
				inputMap.put("SgvaMonthMstVOArray", lObjSgvaMonthMst);
			}

			// To populate year combo....
			List<SgvcFinYearMst> lObjSgvcFinYearMst = new ArrayList<SgvcFinYearMst>();

			// Returning VO array...
			lObjSgvcFinYearMst = lObjCommonPensionDAO.getSgvcFinYearMstVO(lStrLangId);

			if (lObjSgvcFinYearMst != null) {
				inputMap.put("SgvcFinYearMstVOArray", lObjSgvcFinYearMst);
			}

			// ---To Populate DA Rate for state/dept combo
			AdminRateMstDAOImpl lObjAdminRateMst = new AdminRateMstDAOImpl(serv.getSessionFactory());
			List lLstStateDept = lObjAdminRateMst.getAllStateDept(gLngLangId);
			if (lLstStateDept != null) {
				inputMap.put("lLstStateDept", lLstStateDept);
			}

			// ---To Populate Treasury Combo.
			lLstTreasuryId.add(Long.valueOf("100003")); // /100003 is pension
														// department id.
			lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			lLstAllTreasury = lObjCommonDAO.getAllTreasury(gLngLangId, lLstTreasuryId);
			inputMap.put("lLstTreasury", lLstAllTreasury);
			inputMap.put("lStrCalcDAArrearBy", lStrCalcDAArrearBy);
			resObj.setResultValue(inputMap);
			resObj.setViewName("DAArrearConfig");
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject saveDAArrearConfig(Map<String, Object> inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrLocCode = null;
		String lStrFromMonth = null;
		String lStrFromYear = null;
		String lStrToMonth = null;
		String lStrToYear = null;
		String lStrPayInMonthYear = null;
		String lStrOldDARate = null;
		String lStrNewDARate = null;
		String lStrDARateForState = null;
		String lStrRopType = null;
		AdminRateMstDAO lObjAdminRateMstDAO = null;
		List<Object[]> lLstPnsrDtls = null;
		ValidPcodeView lObjValidPcodeView = null;
		Map lMapFamilyDtls = null;
		Double lDblCalculatedBasicAmt = 0.0;
		Double lDblDPAmt = 0.0;
		Double lDblDAAmt = 0.0;
		StringBuilder lSBResp = new StringBuilder();
		String lStrRemarks = null;
		int daArrearConfigCount = 0;
		Map<String, Object[]> lMapPnsrDAArrearDtls = new HashMap<String, Object[]>();
		Double lDblOldDARate = 0.0;
		Double lDblNewDARate = 0.0;
		Set<String> lSetPnsrCode = null;
		TrnPensionArrearDtls lObjTrnPensionArrearDtls = null;
		Long lLngPkCntPensionArrearDtls = null;
		Long lLngArrearId = null;
		Object[] lArrArrearDtls = null;
		String lStrArrearType = bundleCaseConst.getString("ARREARTYPE.DA");
		BigDecimal lBDOldDARate = null;
		BigDecimal lBDNewDARate = null;
		BigDecimal lBDCrtdUserId = null;
		BigDecimal lBDCrtdPostId = null;
		List<TrnPensionArrearDtls> lLstTrnPnsnArrearDtls = null;
		TrnPensionArrearDtlsDAO lObjTrnPensionArrearDtlsDAO = null;
		CommonDAO lObjCommonDAO = null;
		Integer lIntInsertedArrearCnt = 0;
		BigInteger lBigIntGeneratedId = null;
		try {
			setSessionInfo(inputMap);
			gLogger.error("DA Arrear configuration By Current values called on date time :" + gDate);
			lBDCrtdUserId = new BigDecimal(gLngUserId);
			lBDCrtdPostId = new BigDecimal(gLngPostId);
			lObjAdminRateMstDAO = new AdminRateMstDAOImpl(serv.getSessionFactory());
			lObjTrnPensionArrearDtlsDAO = new TrnPensionArrearDtlsDAOImpl(TrnPensionArrearDtls.class, serv.getSessionFactory());
			lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			lStrLocCode = StringUtility.getParameter("locCode", request).trim();
			lStrFromMonth = StringUtility.getParameter("fromMonth", request).trim();
			lStrFromYear = StringUtility.getParameter("fromYear", request).trim();
			lStrToMonth = StringUtility.getParameter("toMonth", request).trim();
			lStrToYear = StringUtility.getParameter("toYear", request).trim();
			lStrPayInMonthYear = StringUtility.getParameter("payInMonthYear", request).trim();
			lStrOldDARate = StringUtility.getParameter("oldDARate", request).trim();
			lStrNewDARate = StringUtility.getParameter("newDARate", request).trim();
			lStrDARateForState = StringUtility.getParameter("daForState", request).trim();
			lStrRopType = StringUtility.getParameter("ropType", request).trim();
			Integer lIntPayInMonth = Integer.valueOf(lStrPayInMonthYear);
			Integer lIntFromYear = Integer.valueOf(lStrFromYear);
			Integer lIntFromMonth = Integer.valueOf(lStrFromMonth);
			Integer lIntToMonth = Integer.valueOf(lStrToMonth);
			Integer lIntToYear = Integer.valueOf(lStrToYear);
			List<Integer> lLstDAForMonth = new ArrayList<Integer>();
			if (lIntFromMonth < 10) {
				lStrFromMonth = "0" + lIntFromMonth;
			} else {
				lStrFromMonth = lIntFromMonth.toString();
			}
			if (lIntToMonth < 10) {
				lStrToMonth = "0" + lIntToMonth;
			} else {
				lStrToMonth = lIntToMonth.toString();
			}
			String lStrFromMonthYear = lStrFromYear + lStrFromMonth;
			String lStrToMonthYear = lStrToYear + lStrToMonth;
			Integer lIntFromMonthYear = Integer.valueOf(lStrFromMonthYear);
			Integer lIntToMonthYear = Integer.valueOf(lStrToMonthYear);
			Integer lIntEffectFromMonthYear = Integer.valueOf(lStrFromMonthYear);
			Integer lIntEffectToMonthYear = Integer.valueOf(lStrToMonthYear);
			lDblOldDARate = Double.parseDouble(lStrOldDARate);
			lDblNewDARate = Double.parseDouble(lStrNewDARate);
			lBDOldDARate = new BigDecimal(lDblOldDARate);
			lBDNewDARate = new BigDecimal(lDblNewDARate);
			if (bundleCaseConst.getString("PPMT.DA1986").equals(lStrRopType)) {
				lStrRopType = "1986";
				lStrRemarks = "DA_1986_ARREAR";
			}
			if (bundleCaseConst.getString("PPMT.DA1996DPMERGED").equals(lStrRopType)) {
				lStrRopType = "1996";
				lStrRemarks = "DA_1996_ARREAR";
			}
			if (bundleCaseConst.getString("PPMT.DA2006").equals(lStrRopType)) {
				lStrRopType = "2006";
				lStrRemarks = "DA_2006_ARREAR";
			}
			inputMap.put("lStrROPType", lStrRopType);
			inputMap.put("lStrLocCode", lStrLocCode);
			inputMap.put("lStrDAForState", lStrDARateForState);
			inputMap.put("lIntPayInMonth", lIntPayInMonth);
			inputMap.put("lIntEffectFromMonth", lIntEffectFromMonthYear);
			inputMap.put("lIntEffectToMonth", lIntEffectToMonthYear);
			inputMap.put("lDblOldRate", lDblOldDARate);
			inputMap.put("lDblNewRate", lDblNewDARate);
			inputMap.put("lLngCreatedPost", gLngPostId);
			inputMap.put("lLngCreatedUser", gLngUserId);
			inputMap.put("lDateCurr", gDate);
			inputMap.put("lStrRemarks", lStrRemarks);
			// int daArrearConfigCount =
			// lObjAdminRateMstDAO.saveDAArrearDtls(inputMap);
			gLogger.error("DA Arrear configuration count for location ':" + lStrLocCode + "' :" + daArrearConfigCount + " .");

			while (lIntFromMonthYear <= lIntToMonthYear) {
				lLstDAForMonth.add(lIntFromMonthYear);
				if (lIntFromMonth.equals(12)) {
					lIntFromMonth = 1;
					lIntFromYear = lIntFromYear + 1;
				} else {
					lIntFromMonth = lIntFromMonth + 1;
				}
				if (lIntFromMonth < 10) {
					lStrFromMonth = "0" + lIntFromMonth;
				} else {
					lStrFromMonth = lIntFromMonth.toString();
				}
				lStrFromMonthYear = lIntFromYear.toString() + lStrFromMonth;
				lIntFromMonthYear = Integer.valueOf(lStrFromMonthYear);
			}
			lLstPnsrDtls = lObjAdminRateMstDAO.getPensionerDtlsForDAArrearCalc(inputMap);
			if (lLstPnsrDtls != null && lLstPnsrDtls.size() > 0) {
				lMapFamilyDtls = lObjAdminRateMstDAO.getMstPensionerFamilyDtlsMap(lStrRopType, lStrLocCode, lStrDARateForState);
			}
			inputMap.put("lMapFamilyDtls", lMapFamilyDtls);
			if (lLstPnsrDtls != null) {
				for (Object[] lArrObj : lLstPnsrDtls) {
					lObjValidPcodeView = new ValidPcodeView();
					lObjValidPcodeView.setEndDate((Timestamp) lArrObj[0]);
					lObjValidPcodeView.setCommensionDate((Timestamp) lArrObj[1]);
					lObjValidPcodeView.setFp1Date((Timestamp) lArrObj[2]);
					lObjValidPcodeView.setFp2Date((Timestamp) lArrObj[3]);
					lObjValidPcodeView.setDateOfDeath((Timestamp) lArrObj[4]);
					lObjValidPcodeView.setBasicPensionAmount((lArrObj[5] != null) ? (BigDecimal) lArrObj[5] : BigDecimal.ZERO);
					lObjValidPcodeView.setFp1Amount((lArrObj[6] != null) ? (BigDecimal) lArrObj[6] : BigDecimal.ZERO);
					lObjValidPcodeView.setFp2Amount((lArrObj[7] != null) ? (BigDecimal) lArrObj[7] : BigDecimal.ZERO);
					lObjValidPcodeView.setPensionerCode((String) lArrObj[8]);
					lObjValidPcodeView.setPensionRequestId(((BigInteger) lArrObj[9]).longValue());
					lObjValidPcodeView.setDpFlag((lArrObj[10] != null) ? lArrObj[10].toString() : null);
					inputMap.put("lObjValidPcodeVO", lObjValidPcodeView);
					lDblDAAmt = 0.0;
					// --If rop type is 1996 and dp flag = 'Y' then considering
					// dp amout in da
					// arrear calculations.
					if ("1996".equals(lStrRopType)) {
						if (lObjValidPcodeView.getDpFlag() != null && "Y".equals(lObjValidPcodeView.getDpFlag())) {
							for (Integer lIntForMonth : lLstDAForMonth) {
								lDblCalculatedBasicAmt = 0.0;
								lDblDPAmt = 0.0;
								inputMap.put("lCurrentyyyyMM", lIntForMonth);
								lDblCalculatedBasicAmt = getCalculatedPensionAmount(inputMap);
								lDblDPAmt = lDblCalculatedBasicAmt / 2;
								lDblDAAmt = lDblDAAmt + ((lDblCalculatedBasicAmt + lDblDPAmt) * (lDblNewDARate / 100) - (lDblCalculatedBasicAmt + lDblDPAmt) * (lDblOldDARate / 100));
							}
						} else {
							for (Integer lIntForMonth : lLstDAForMonth) {
								lDblCalculatedBasicAmt = 0.0;
								inputMap.put("lCurrentyyyyMM", lIntForMonth);
								lDblCalculatedBasicAmt = getCalculatedPensionAmount(inputMap);
								lDblDAAmt = lDblDAAmt + (lDblCalculatedBasicAmt * (lDblNewDARate / 100) - lDblCalculatedBasicAmt * (lDblOldDARate / 100));
							}
						}
					} else {
						for (Integer lIntForMonth : lLstDAForMonth) {
							lDblCalculatedBasicAmt = 0.0;
							inputMap.put("lCurrentyyyyMM", lIntForMonth);
							lDblCalculatedBasicAmt = getCalculatedPensionAmount(inputMap);
							lDblDAAmt = lDblDAAmt + (lDblCalculatedBasicAmt * (lDblNewDARate / 100) - lDblCalculatedBasicAmt * (lDblOldDARate / 100));
						}
					}
					lArrArrearDtls = new Object[2];
					lArrArrearDtls[0] = lDblDAAmt;
					lArrArrearDtls[1] = lObjValidPcodeView.getPensionRequestId();
					lMapPnsrDAArrearDtls.put((String) lArrObj[8], lArrArrearDtls);
					lArrArrearDtls = null;
					lObjValidPcodeView = null;
				}
			}
			lSetPnsrCode = lMapPnsrDAArrearDtls.keySet();
			// ----Insertion in Arrear Dtls Table Starts <<<<<<
			lLstTrnPnsnArrearDtls = new ArrayList<TrnPensionArrearDtls>();
			lBigIntGeneratedId = lObjCommonDAO.getCurrentSeqId(lStrLocCode, "trn_pension_arrear_dtls");
			lLngPkCntPensionArrearDtls = lBigIntGeneratedId.longValue();
			lObjCommonDAO.updateTableSeqByCount(lStrLocCode, "trn_pension_arrear_dtls", lBigIntGeneratedId.longValue() + lSetPnsrCode.size());
			for (String lStrPnsrCode : lSetPnsrCode) {
				lArrArrearDtls = lMapPnsrDAArrearDtls.get(lStrPnsrCode);
				if (Math.round(((Double) lArrArrearDtls[0])) > 0) {
					lLngArrearId = ++lLngPkCntPensionArrearDtls;
					lLngArrearId = Long.valueOf(gLngDBId + lStrLocCode + lLngArrearId);
					// lLngArrearId =
					// IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngArrearId,
					// inputMap);
					lObjTrnPensionArrearDtls = new TrnPensionArrearDtls();
					lObjTrnPensionArrearDtls.setPensionArrearDtlsId(lLngArrearId);
					lObjTrnPensionArrearDtls.setPensionRequestId((Long) lArrArrearDtls[1]);
					lObjTrnPensionArrearDtls.setPensionerCode(lStrPnsrCode);
					lObjTrnPensionArrearDtls.setArrearFieldType(lStrArrearType);
					lObjTrnPensionArrearDtls.setOldAmountPercentage(lBDOldDARate);
					lObjTrnPensionArrearDtls.setNewAmountPercentage(lBDNewDARate);
					lObjTrnPensionArrearDtls.setEffectedFromYyyymm(lIntEffectFromMonthYear);
					lObjTrnPensionArrearDtls.setEffectedToYyyymm(lIntEffectToMonthYear);
					lObjTrnPensionArrearDtls.setTotalDifferenceAmt(new BigDecimal(Math.round(((Double) lArrArrearDtls[0]))));
					lObjTrnPensionArrearDtls.setPaymentFromYyyymm(lIntPayInMonth);
					lObjTrnPensionArrearDtls.setPaymentToYyyymm(lIntPayInMonth);
					lObjTrnPensionArrearDtls.setCreatedUserId(lBDCrtdUserId);
					lObjTrnPensionArrearDtls.setCreatedPostId(lBDCrtdPostId);
					lObjTrnPensionArrearDtls.setCreatedDate(gDate);
					lObjTrnPensionArrearDtls.setRemarks(lStrRemarks);
					lObjTrnPensionArrearDtls.setPaidFlag('N');
					lLstTrnPnsnArrearDtls.add(lObjTrnPensionArrearDtls);
					if (lLstTrnPnsnArrearDtls.size() == 5000) {
						lObjTrnPensionArrearDtlsDAO.insertArrearDtls(lLstTrnPnsnArrearDtls);
						lIntInsertedArrearCnt = lIntInsertedArrearCnt + 5000;
						lLstTrnPnsnArrearDtls.clear();
					}
				}
			}
			// --Inserting all remaining arrears.
			if (lLstTrnPnsnArrearDtls.size() > 0) {
				lObjTrnPensionArrearDtlsDAO.insertArrearDtls(lLstTrnPnsnArrearDtls);
				lIntInsertedArrearCnt = lIntInsertedArrearCnt + lLstTrnPnsnArrearDtls.size();
				lLstTrnPnsnArrearDtls.clear();
			}
			// ----Insertion in Arrear Dtls Table Ends >>>>>>>

			lSBResp.append("<XMLDOC>");
			lSBResp.append("<STATUS>");
			if (lIntInsertedArrearCnt > 0) {
				lSBResp.append("SUCCESS");
			} else {
				lSBResp.append("FAIL");
			}
			lSBResp.append("</STATUS>");
			lSBResp.append("</XMLDOC>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBResp.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject saveDAArrearConfigByProc(Map<String, Object> inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrLocCode = null;
		String lStrFromMonth = null;
		String lStrFromYear = null;
		String lStrToMonth = null;
		String lStrToYear = null;
		String lStrPayInMonthYear = null;
		String lStrOldDARate = null;
		String lStrNewDARate = null;
		String lStrDARateForState = null;
		String lStrRopType = null;
		AdminRateMstDAO lObjAdminRateMstDAO = null;
		StringBuilder lSBResp = new StringBuilder();
		String lStrRemarks = null;
		try {
			setSessionInfo(inputMap);
			lObjAdminRateMstDAO = new AdminRateMstDAOImpl(serv.getSessionFactory());
			lStrLocCode = StringUtility.getParameter("locCode", request).trim();
			lStrFromMonth = StringUtility.getParameter("fromMonth", request).trim();
			lStrFromYear = StringUtility.getParameter("fromYear", request).trim();
			lStrToMonth = StringUtility.getParameter("toMonth", request).trim();
			lStrToYear = StringUtility.getParameter("toYear", request).trim();
			lStrPayInMonthYear = StringUtility.getParameter("payInMonthYear", request).trim();
			lStrOldDARate = StringUtility.getParameter("oldDARate", request).trim();
			lStrNewDARate = StringUtility.getParameter("newDARate", request).trim();
			lStrDARateForState = StringUtility.getParameter("daForState", request).trim();
			lStrRopType = StringUtility.getParameter("ropType", request).trim();
			Integer lIntPayInMonth = Integer.valueOf(lStrPayInMonthYear);
			Integer lIntFromMonth = Integer.valueOf(lStrFromMonth);
			Integer lIntToMonth = Integer.valueOf(lStrToMonth);
			if (lIntFromMonth < 10) {
				lStrFromMonth = "0" + lIntFromMonth;
			} else {
				lStrFromMonth = lIntFromMonth.toString();
			}
			if (lIntToMonth < 10) {
				lStrToMonth = "0" + lIntToMonth;
			} else {
				lStrToMonth = lIntToMonth.toString();
			}
			String lStrFromMonthYear = lStrFromYear + lStrFromMonth;
			String lStrToMonthYear = lStrToYear + lStrToMonth;
			Integer lIntFromMonthYear = Integer.valueOf(lStrFromMonthYear);
			Integer lIntToMonthYear = Integer.valueOf(lStrToMonthYear);
			if (bundleCaseConst.getString("PPMT.DA1986").equals(lStrRopType)) {
				lStrRopType = "1986";
				lStrRemarks = "DA_1986_ARREAR";
			}
			if (bundleCaseConst.getString("PPMT.DA1996DPMERGED").equals(lStrRopType)) {
				lStrRopType = "1996";
				lStrRemarks = "DA_1996_ARREAR";
			}
			if (bundleCaseConst.getString("PPMT.DA2006").equals(lStrRopType)) {
				lStrRopType = "2006";
				lStrRemarks = "DA_2006_ARREAR";
			}
			inputMap.put("lStrROPType", lStrRopType);
			inputMap.put("lStrLocCode", lStrLocCode);
			inputMap.put("lStrDAForState", lStrDARateForState);
			inputMap.put("lIntPayInMonth", lIntPayInMonth);
			inputMap.put("lIntEffectFromMonth", lIntFromMonthYear);
			inputMap.put("lIntEffectToMonth", lIntToMonthYear);
			inputMap.put("lDblOldRate", Double.parseDouble(lStrOldDARate));
			inputMap.put("lDblNewRate", Double.parseDouble(lStrNewDARate));
			inputMap.put("lLngCreatedPost", gLngPostId);
			inputMap.put("lLngCreatedUser", gLngUserId);
			inputMap.put("lDateCurr", gDate);
			inputMap.put("lStrRemarks", lStrRemarks);
			gLogger.error("DA Arrear configuration By Proc called on date time :" + gDate);
			int daArrearConfigCount = lObjAdminRateMstDAO.saveDAArrearDtlsByProc(inputMap);
			gLogger.error("DA Arrear configuration count for location ':" + lStrLocCode + "' :" + daArrearConfigCount + " .");
			lSBResp.append("<XMLDOC>");
			lSBResp.append("<STATUS>");
			if (daArrearConfigCount > 0) {
				lSBResp.append("SUCCESS");
			} else {
				lSBResp.append("FAIL");
			}
			lSBResp.append("</STATUS>");
			lSBResp.append("</XMLDOC>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBResp.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public Double getCalculatedPensionAmount(Map<String, Object> inputMap) throws Exception {

		SimpleDateFormat lObjSmplDateFrm = new SimpleDateFormat("dd/MM/yyyy");
		Date lCurrentDate = null;
		Double lCalculatedBasicPensionAmt = 0.0;
		ValidPcodeView lObjValidPcodeVO = (ValidPcodeView) inputMap.get("lObjValidPcodeVO");
		Integer lCurrentyyyyMM = (Integer) inputMap.get("lCurrentyyyyMM");
		Date lPPOEndDate = (lObjValidPcodeVO.getEndDate() != null) ? addDaysInDate(lObjValidPcodeVO.getEndDate(), 0) : null;
		Date lCommDate = (lObjValidPcodeVO.getCommensionDate() != null) ? addDaysInDate(lObjValidPcodeVO.getCommensionDate(), 0) : null;
		Date lFP1Date = (lObjValidPcodeVO.getFp1Date() != null) ? addDaysInDate(lObjValidPcodeVO.getFp1Date(), 0) : null;
		Date lFP2Date = (lObjValidPcodeVO.getFp2Date() != null) ? addDaysInDate(lObjValidPcodeVO.getFp2Date(), 0) : null;
		Date lDeathDate = (lObjValidPcodeVO.getDateOfDeath() != null) ? addDaysInDate(lObjValidPcodeVO.getDateOfDeath(), 0) : null;
		String lStrPnsrCode = lObjValidPcodeVO.getPensionerCode();
		int lCommyyyyMM = lCommDate != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lCommDate)) : 0;
		int lPPOEndYYYYMM = lPPOEndDate != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lPPOEndDate)) : 0;
		int lDeathYYYYMM = lDeathDate != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lDeathDate)) : 0;
		double lTotalDaysofMonth = getDaysOfMonth(lCurrentyyyyMM);

		double lBasicPayDays = 0;
		double lFP1PayDays = 0;
		double lFP2PayDays = 0;
		double lTotalPayableDays = 0;
		MstPensionerFamilyDtls lMstPensionerFamilyDtlsVO = null;
		String lStrPpoEndFlag = "N";
		try {

			if (lCommDate != null) {
				if (lCommyyyyMM > lCurrentyyyyMM) {
					lStrPpoEndFlag = "Y";
				}
			}
			if (lPPOEndDate != null) {
				if (lPPOEndYYYYMM < lCurrentyyyyMM) {
					lStrPpoEndFlag = "Y";
				}
			}
			if ("N".equals(lStrPpoEndFlag)) {

				lCurrentDate = lObjSmplDateFrm.parse("01/" + (lCurrentyyyyMM.toString()).substring(4, 6) + "/" + (lCurrentyyyyMM.toString()).substring(0, 4));
				/** Basic Amount Calculation Start ****************************************************************************/
				SimpleDateFormat lFormatDDYYYYMM = new SimpleDateFormat("ddyyyyMM");
				Date lMonthStartDt = lFormatDDYYYYMM.parse("01" + lCurrentyyyyMM);
				Date lMonthEndDt = lFormatDDYYYYMM.parse(((int) lTotalDaysofMonth + "" + lCurrentyyyyMM));
				Date lPayStartDate = lMonthStartDt; // First Date of month
				Date lPayEndDate = lMonthEndDt; // Last Date of month

				if (lCommyyyyMM == lCurrentyyyyMM) {
					lPayStartDate = lCommDate.after(lMonthStartDt) ? lCommDate : lMonthStartDt; // which
					// ever
					// is
					// later
				} else {
					lPayStartDate = lMonthStartDt;
				}

				if (lPPOEndDate != null) {
					if (lPPOEndYYYYMM == lCurrentyyyyMM) {
						lPayEndDate = lPPOEndDate.after(lMonthEndDt) ? lMonthEndDt : lPPOEndDate; // which
						// ever
						// is
						// earlier
					} else {
						lPayEndDate = lMonthEndDt;
					}
				} else {
					lPayEndDate = lMonthEndDt;
				}

				if (lDeathDate == null) {
					lBasicPayDays = getDaysDifference(lPayStartDate, lPayEndDate);
				} else if (lDeathDate != null) {
					// Integer.valueOf(new
					// SimpleDateFormat("yyyyMM").format(lFP1Date));
					Map lMapFamilyDtls = (Map) inputMap.get("lMapFamilyDtls");
					if (lMapFamilyDtls != null) {
						lMstPensionerFamilyDtlsVO = (MstPensionerFamilyDtls) lMapFamilyDtls.get(lStrPnsrCode);
					}
					List lLstFPMember = new ArrayList(); // lLstFPMember.add("1");
					if (lMstPensionerFamilyDtlsVO != null) {
						PensionBillProcessServiceImpl lObjProcessServiceImpl = new PensionBillProcessServiceImpl();
						lLstFPMember = lObjProcessServiceImpl.getFpMemberList(lMstPensionerFamilyDtlsVO);

						Date lFPDeathDate = lMstPensionerFamilyDtlsVO.getDateOfDeath();
						inputMap.put("lLstFPMember", lLstFPMember);
						if (lLstFPMember.isEmpty()) // No valid family member
													// found
						{
							// Exit;
							if (lDeathYYYYMM == lCurrentyyyyMM) {
								lBasicPayDays = getDaysDifference(lPayStartDate, (lDeathDate.before(lPayEndDate)) ? lDeathDate : lPayEndDate); // change
							}
						} else if (!lLstFPMember.isEmpty()) {
							if (lFP1Date != null && lFP2Date != null) {
								int lFP2yyyyMM = Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lFP2Date));
								// Pensioner
								// and
								// family
								// member
								// both
								// are
								// dead
								if (lFPDeathDate != null) {
									int lFPDeathYYYYMM = lFPDeathDate != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lFPDeathDate)) : 0;

									if (lFPDeathYYYYMM == lCurrentyyyyMM) {
										if (lFPDeathDate.before(lDeathDate) || lFPDeathDate.equals(lDeathDate)) {
											// Exit
											lBasicPayDays = getDaysDifference(lPayStartDate, (lDeathDate.before(lPayEndDate)) ? lDeathDate : lPayEndDate); // change
										} else {
											lBasicPayDays = getDaysDifference(lPayStartDate, (lDeathDate.before(lPayEndDate)) ? lDeathDate : lPayEndDate); // change

											if (lFP2yyyyMM == lCurrentyyyyMM) {
												if (lFP1Date.before(lDeathDate)) {
													lFP2PayDays = getDaysDifference(addDaysInDate(lDeathDate, 1), (lFPDeathDate.before(lPayEndDate)) ? lFPDeathDate : lPayEndDate); // change
												} else {
													lFP1PayDays = getDaysDifference(addDaysInDate(lDeathDate, 1), (lFP1Date.before(lPayEndDate)) ? lFP1Date : lPayEndDate); // change
												}

												if (lFP2Date.before(lFPDeathDate)) {
													// lFP2PayDays =
													// getDaysDifference(lFP2Date,
													// lFPDeathDate);
													lFP2PayDays = getDaysDifference(lFP2Date, (lFPDeathDate.before(lPayEndDate)) ? lFPDeathDate : lPayEndDate); // changed
												}
											} else {

												if (lFP1Date.before(lDeathDate)) {
													lFP2PayDays = getDaysDifference(addDaysInDate(lDeathDate, 1), (lFPDeathDate.before(lPayEndDate)) ? lFPDeathDate : lPayEndDate); // changed
												} else {
													lFP1PayDays = getDaysDifference(addDaysInDate(lDeathDate, 1), (lFPDeathDate.before(lPayEndDate)) ? lFPDeathDate : lPayEndDate); // changed
												}

												/*
												 * if(lFP2Date.before(lPayStartDate
												 * )) { lFP1PayDays = 0;
												 * lFP2PayDays =
												 * getDaysDifference
												 * (lPayStartDate,lFPDeathDate);
												 * } else {
												 * if(lFP1Date.equals(lPayEndDate
												 * )) { lFP1PayDays =
												 * getDaysDifference
												 * (addDaysInDate
												 * (lDeathDate,1),lFPDeathDate);
												 * }
												 * 
												 * lFP2PayDays = 0; }
												 */
											}

										}

									}
								} else // Pensioner is expired and Family member
										// is
										// alive
								{
									if (lDeathYYYYMM == lCurrentyyyyMM) {
										// ----For family pension case
										// commencement
										// date
										// is
										// next to death date
										if (lCommDate.equals(addDaysInDate(lDeathDate, 1))) {
											lFP1PayDays = getDaysDifference(lPayStartDate, lPayEndDate);
										} else {
											lBasicPayDays = getDaysDifference(lPayStartDate, (lDeathDate.before(lPayEndDate)) ? lDeathDate : lPayEndDate);

											if (lFP2yyyyMM == lCurrentyyyyMM) {
												lFP1PayDays = getDaysDifference(addDaysInDate(lDeathDate, 1), lFP1Date);
												lFP2PayDays = getDaysDifference((lDeathDate.after(lFP2Date)) ? lDeathDate : lFP2Date, lPayEndDate);// whichever
												// is
												// later.
											} else {
												if (lFP2Date.before(lPayStartDate)) {
													lFP1PayDays = 0;
													// lFP2PayDays =
													// getDaysDifference(lPayStartDate,
													// lPayEndDate);
													lFP2PayDays = getDaysDifference(addDaysInDate(lDeathDate, 1), lPayEndDate); // changed
												} else {
													/*
													 * If fp2 date is after
													 * current month then check
													 * if fp1 date is last day
													 * of current month or not.
													 */
													if (lFP1Date.equals(lPayEndDate)) {
														lFP1PayDays = getDaysDifference(addDaysInDate(lDeathDate, 1), lPayEndDate);
													} /*
													 * else { lFP1PayDays =
													 * getDaysDifference
													 * (addDaysInDate
													 * (lDeathDate, 1),
													 * lPayEndDate); // changed
													 * }
													 */
													lFP2PayDays = 0;
												}
											}
										}
									} else // Pensioner's Death already done
											// earlier
											// not
											// in
									// current month (before current month)
									{
										if (lFP2yyyyMM == lCurrentyyyyMM) {
											if (lFP2Date.equals(lPayStartDate)) {
												lFP2PayDays = getDaysDifference(lFP2Date, lPayEndDate);
												lFP1PayDays = 0;
											} else {
												lFP1PayDays = getDaysDifference(lPayStartDate, lFP1Date);
												lFP2PayDays = getDaysDifference(lFP2Date, lPayEndDate);
											}
										} else {
											// change in this block
											if (lFP2Date.before(lPayStartDate)) {
												lFP1PayDays = 0;
												// lFP2PayDays =
												// getDaysDifference(lPayStartDate,
												// lPayEndDate);
												lFP2PayDays = getDaysDifference(lPayStartDate, lPayEndDate); // changed
												lFP1PayDays = 0;
											} else {
												lFP1PayDays = getDaysDifference(lPayStartDate, lPayEndDate);
												lFP2PayDays = 0;
											}
										}
									}
								}
							}
						}
					} else {
						if (lDeathYYYYMM == lCurrentyyyyMM) {
							lBasicPayDays = getDaysDifference(lPayStartDate, (lDeathDate.before(lPayEndDate)) ? lDeathDate : lPayEndDate); // change
						}
					}
				}

				/*
				 * Double lBasicPensionAmt = 10000D; Double lFP1Amount = 1000D;
				 * Double lFP2Amount = 100D;
				 */

				Double lBasicPensionAmt = lObjValidPcodeVO.getBasicPensionAmount().doubleValue();
				Double lFP1Amount = lObjValidPcodeVO.getFp1Amount().doubleValue();
				Double lFP2Amount = lObjValidPcodeVO.getFp2Amount().doubleValue();

				lTotalPayableDays = lBasicPayDays + lFP1PayDays + lFP2PayDays;
				// System.out.println("lBasicPayDays =" + lBasicPayDays);
				// System.out.println("lFP1PayDays =" + lFP1PayDays);
				// System.out.println("lFP2PayDays =" + lFP2PayDays);

				// System.out.println("total Days =" + (lBasicPayDays +
				// lFP1PayDays
				// + lFP2PayDays));

				Double lDblCalculatedBasicAmtForPnsr = lBasicPensionAmt * (lBasicPayDays / lTotalDaysofMonth);
				Double lDblCalculatedBasicAmtForFp = (lFP1Amount * (lFP1PayDays / lTotalDaysofMonth)) + (lFP2Amount * (lFP2PayDays / lTotalDaysofMonth));

				lCalculatedBasicPensionAmt = (lBasicPensionAmt * (lBasicPayDays / lTotalDaysofMonth)) + (lFP1Amount * (lFP1PayDays / lTotalDaysofMonth))
						+ (lFP2Amount * (lFP2PayDays / lTotalDaysofMonth));
			} else {
				lCalculatedBasicPensionAmt = 0.0;
			}
			// System.out.println("Basic Amount =" +
			// (lCalculatedBasicPensionAmt));
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			throw e;
		}
		return lCalculatedBasicPensionAmt;
	}

	/**
	 * Method to set Session variables
	 * 
	 * @param inputMap
	 */
	private void setSessionInfo(Map inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gStrLocCode = SessionHelper.getLocationCode(inputMap);
		gDate = DBUtility.getCurrentDateFromDB();
		gLngDBId = SessionHelper.getDbId(inputMap);
	}

	private Integer getDaysOfMonth(Integer lYYYYMM) {

		Integer YYYY = Integer.parseInt(lYYYYMM.toString().substring(0, 4));
		Integer MM = Integer.parseInt(lYYYYMM.toString().substring(4, 6));
		Calendar cal = new GregorianCalendar(YYYY, (MM - 1), 1);
		Integer days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		return days;
	}

	private Integer getDaysDifference(Date lDtFrom, Date lDtTo) {

		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(lDtFrom);

		Calendar calTo = Calendar.getInstance();
		calTo.setTime(lDtTo);
		Integer lIntDaysDiff = (calTo.get(Calendar.DATE) - calFrom.get(Calendar.DATE) + 1);
		return (lIntDaysDiff > 0) ? lIntDaysDiff : 0;
	}

	public Date addDaysInDate(Date lDate, int Days) throws Exception {

		Date lNewDate = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(lDate);
			cal.add(Calendar.DATE, Days);
			lNewDate = cal.getTime();
		} catch (Exception e) {
			throw (e);
		}
		return lNewDate;
	}
}
