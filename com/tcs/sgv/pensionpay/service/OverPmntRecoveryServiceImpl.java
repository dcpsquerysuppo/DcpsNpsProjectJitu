/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jul 1, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.log4j.Logger;
import org.jfree.util.StringUtils;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.idgenerator.delegate.IDGenerateDelegate;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DateUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.CVPRestorationLetterDAO;
import com.tcs.sgv.pensionpay.dao.CVPRestorationLetterDAOImpl;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAO;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.dao.MstPensionerHdrDAOImpl;
import com.tcs.sgv.pensionpay.dao.OverPmntRecoveryDAO;
import com.tcs.sgv.pensionpay.dao.OverPmntRecoveryDAOImpl;
import com.tcs.sgv.pensionpay.dao.SixPayFPArrearDAO;
import com.tcs.sgv.pensionpay.dao.SixPayFPArrearDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionRqstHdrDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionChallanDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.web.jsp.tags.DateUtilities;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Jul 1, 2011
 */
public class OverPmntRecoveryServiceImpl extends ServiceImpl implements OverPmntRecoveryService {

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	private HttpServletRequest request = null; /* REQUEST OBJECT */
	private ServiceLocator serv = null; /* SERVICE LOCATOR */
	private Date gDtCurDate = null; /* CURRENT DATE */

	private final static Logger gLogger = Logger.getLogger(OverPmntRecoveryServiceImpl.class);

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	private void setSessionInfo(Map<String, Object> inputMap) throws Exception {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
		} catch (Exception e) {
			gLogger.error("Error in setSessionInfo of OverPmntRecoveryServiceImpl ", e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.OverPmntRecoveryService#loadOverPmntRecovery
	 * (java.util.Map)
	 */
	public ResultObject loadOverPmntRecovery(Map<String, Object> inputMap) {

		gLogger.info("In loadOverPmntRecovery method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SixPayFPArrearDAO lObjSixPayFPArrearDAO = null;
		OverPmntRecoveryDAO lObjOverPmntRecoveryDAO = null;
		CommonPensionDAO lObjCommonPensionDAO = null;
		List<ComboValuesVO> lLstHeadCodes = null;
		List<String> lLstPnsnrCodes = new ArrayList<String>();
		List<String> lLstStatus = new ArrayList<String>();
		List lLstTempList = null;
		String lStrRoleName = null;
		Iterator lObjIterator = null;
		Object[] lObjArr = null;
		try {
			setSessionInfo(inputMap);
			lStrRoleName = StringUtility.getParameter("RoleName", request);
			lObjSixPayFPArrearDAO = new SixPayFPArrearDAOImpl(null, serv.getSessionFactory());
			lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			lLstHeadCodes = lObjCommonPensionDAO.getAllHeadCode();
			lObjOverPmntRecoveryDAO = new OverPmntRecoveryDAOImpl(null, serv.getSessionFactory());
			// lLstTempList = lObjOverPmntRecoveryDAO.getAllPnsnrCodes();
			// if (!lLstTempList.isEmpty() && lLstTempList.size() > 0) {
			// lObjIterator = lLstTempList.iterator();
			// while (lObjIterator.hasNext()) {
			// lObjArr = (Object[]) lObjIterator.next();
			// if (lObjArr[0] != null) {
			// lLstPnsnrCodes.add((String) lObjArr[0]);
			// }
			// if (lObjArr[1] != null) {
			// lLstStatus.add((String) lObjArr[1]);
			// }
			// }
			// }

			// inputMap.put("lLstPnsnrCodes", lLstPnsnrCodes);
			// inputMap.put("lLstStatus", lLstStatus);
			inputMap.put("lStrRoleName", lStrRoleName);
			inputMap.put("lLstHeadCodes", lLstHeadCodes);
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);
			objRes.setViewName("OverPaymentRecovery");
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");

		}
		return objRes;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.OverPmntRecoveryService#loadOverPmntRecovery
	 * (java.util.Map)
	 */
	public ResultObject getPPODtls(Map<String, Object> inputMap) {

		gLogger.info("In getPPODtls method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		OverPmntRecoveryDAO lObjOverPmntRecoveryDAO = null;
		String lStrPPONo = null;
		List lLstPPODtls = null;

		try {
			setSessionInfo(inputMap);
			lStrPPONo = StringUtility.getParameter("txtPpoNo", request);

			lObjOverPmntRecoveryDAO = new OverPmntRecoveryDAOImpl(null, serv.getSessionFactory());
			if (lStrPPONo.length() > 0) {
				lLstPPODtls = lObjOverPmntRecoveryDAO.getPPODtls(lStrPPONo, gLngLangId, gStrLocationCode);
			}

			String lSBStatus = getResponseXMLDocPPODtls(lLstPPODtls).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");

		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			//e.printStackTrace();
		}

		return objRes;

	}

	private StringBuilder getResponseXMLDocPPODtls(List lLstPPODtls) {

		StringBuilder lStrBldXML = new StringBuilder();
		SimpleDateFormat lSdf = new SimpleDateFormat("dd/MM/yyyy");
		lStrBldXML.append("<XMLDOCPPODtls>");
		if (lLstPPODtls != null && lLstPPODtls.size() > 0) {
			Iterator itr = lLstPPODtls.iterator();
			while (itr.hasNext()) {
				Object[] obj = (Object[]) itr.next();
				lStrBldXML.append("<PnsnrName>");
				lStrBldXML.append(obj[0].toString());
				lStrBldXML.append("</PnsnrName>");
				lStrBldXML.append("<PnsnrCode>");
				lStrBldXML.append(obj[1].toString());
				lStrBldXML.append("</PnsnrCode>");
				lStrBldXML.append("<CommensionDate>");
				lStrBldXML.append(obj[2].toString());
				lStrBldXML.append("</CommensionDate>");
				lStrBldXML.append("<CaseStatus>");
				lStrBldXML.append(obj[3].toString());
				lStrBldXML.append("</CaseStatus>");
				lStrBldXML.append("<HeadCode>");
				lStrBldXML.append(obj[4].toString());
				lStrBldXML.append("</HeadCode>");
				lStrBldXML.append("<RequestId>");
				lStrBldXML.append(obj[5].toString());
				lStrBldXML.append("</RequestId>");
				lStrBldXML.append("<DeathDate>");
				lStrBldXML.append((obj[6] != null) ? lSdf.format((Date) obj[6]) : "");
				lStrBldXML.append("</DeathDate>");
				lStrBldXML.append("<TotalRecoveryAmount>");
				lStrBldXML.append((obj[7] != null) ? obj[7].toString() : 0);
				lStrBldXML.append("</TotalRecoveryAmount>");
			}
		} else {
			lStrBldXML.append("<EmptyList>");
			lStrBldXML.append("EmptyList");
			lStrBldXML.append("</EmptyList>");
		}

		lStrBldXML.append("</XMLDOCPPODtls>");
		return lStrBldXML;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.OverPmntRecoveryService#saveOverPmntRecovery
	 * (java.util.Map)
	 */
	public ResultObject saveOverPmntRecovery(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Long lLngTrnPensionChallanId = null;
		OverPmntRecoveryDAO lObjOverPmntRecoveryDAO = null;
		StringBuilder lStrBldXML = null;
		String lStrResult = null;
		String lStrTransMode = null;
		TrnPensionChallanDtls lObjTrnPensionChallanDtlsVO = new TrnPensionChallanDtls();
		TrnPensionRqstHdrDAOImpl lObjTrnPensionRqstHdrDAO = null;
		MstPensionerHdrDAOImpl lObjMstPensionerHdrDAOImpl = null;
		try {
			setSessionInfo(inputMap);
			lObjTrnPensionRqstHdrDAO = new TrnPensionRqstHdrDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
			lObjMstPensionerHdrDAOImpl = new MstPensionerHdrDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			lStrTransMode = (String) inputMap.get("Mode");
			lObjOverPmntRecoveryDAO = new OverPmntRecoveryDAOImpl(TrnPensionChallanDtls.class, serv.getSessionFactory());
			if (lStrTransMode.equalsIgnoreCase("Add")) {
				if (lObjTrnPensionChallanDtlsVO != null) {
					lObjTrnPensionChallanDtlsVO = (TrnPensionChallanDtls) inputMap.get("lObjTrnPensionChallanDtlsVO");
					lLngTrnPensionChallanId = IFMSCommonServiceImpl.getNextSeqNum("trn_pension_challan_dtls", inputMap);
					lObjTrnPensionChallanDtlsVO.setTrnPensionChallanId((lLngTrnPensionChallanId));
					lObjOverPmntRecoveryDAO.create(lObjTrnPensionChallanDtlsVO);
					gLogger.info("Record Inserted in table trn_pension_challan_dtls successfully");
				}
				String lStrHeadCode = StringUtility.getParameter("cmbNewHeadCode", request);
				lObjOverPmntRecoveryDAO = new OverPmntRecoveryDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
				List<TrnPensionRqstHdr> lLstTrnPensionRqstHdr = lObjOverPmntRecoveryDAO.getListByColumnAndValue("pensionerCode", lObjTrnPensionChallanDtlsVO.getPensionerCode());
				if (!lLstTrnPensionRqstHdr.isEmpty()) {
					TrnPensionRqstHdr lObjTrnPensionRqstHdr = lLstTrnPensionRqstHdr.get(0);
					lObjTrnPensionRqstHdr.setStatus(gObjRsrcBndle.getString("STATUS.WITHHELD"));
					lObjTrnPensionRqstHdrDAO.update(lObjTrnPensionRqstHdr);
					// if (lStrHeadCode.length() > 0) {
					// lObjTrnPensionRqstHdr.setHeadCode(BigDecimal.valueOf(Long.valueOf(lStrHeadCode.trim())));
					// }
				}

				lObjOverPmntRecoveryDAO = new OverPmntRecoveryDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
				List<MstPensionerHdr> lLstMstPensionerHdr = lObjOverPmntRecoveryDAO.getListByColumnAndValue("pensionerCode", lObjTrnPensionChallanDtlsVO.getPensionerCode());
				if (!lLstMstPensionerHdr.isEmpty()) {
					MstPensionerHdr lObjMstPensionerHdr = lLstMstPensionerHdr.get(0);
					lObjMstPensionerHdr.setAliveFlag("N");
					lObjMstPensionerHdr.setDateOfDeath(lObjTrnPensionChallanDtlsVO.getDeathDate());
					lObjMstPensionerHdrDAOImpl.update(lObjMstPensionerHdr);
				}

				lStrBldXML = getResponseXMLDoc(lStrTransMode, lObjTrnPensionChallanDtlsVO.getRequestId());
				gLogger.info(" lStrBldXML :: " + lStrBldXML);
				lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);

				objRes.setViewName("ajaxData");
				objRes.setResultValue(inputMap);

			}

		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;

	}

	private StringBuilder getResponseXMLDoc(String strMode, String RequestId) {

		StringBuilder lStrHidPKs = new StringBuilder();

		lStrHidPKs.append("<XMLDOC>");
		lStrHidPKs.append("<MESSAGECODE>");
		lStrHidPKs.append(strMode);
		lStrHidPKs.append("</MESSAGECODE>");
		lStrHidPKs.append("<REQUESTID>");
		lStrHidPKs.append(RequestId);
		lStrHidPKs.append("</REQUESTID>");
		lStrHidPKs.append("</XMLDOC>");

		gLogger.info("lStrHidPKs : " + lStrHidPKs);
		return lStrHidPKs;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.OverPmntRecoveryService#getTotalRecoveryAmnt
	 * (java.util.Map)
	 */
	public ResultObject getTotalRecoveryAmnt(Map<String, Object> inputMap) {

		gLogger.info("In getTotalRecoveryAmnt method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		OverPmntRecoveryDAO lObjOverPmntRecoveryDAO = null;
		String lStrExpiryDt = null;
		String[] lStrArrExpiryDt = null;
		String lStrPnsnrCode = null;
		String lStrBillType = gObjRsrcBndle.getString("PPMT.MONTHLY");
		List lLstEligibleBills = null;
		Iterator lObjIterator = null;
		String lStrExpiryDay = null;
		String lStrExpiryMonth = null;
		String lStrExpiryYear = null;
		try {
			setSessionInfo(inputMap);
			lStrExpiryDt = StringUtility.getParameter("dateOfExpry", request);
			lStrPnsnrCode = StringUtility.getParameter("pnsnrCode", request);
			lObjOverPmntRecoveryDAO = new OverPmntRecoveryDAOImpl(null, serv.getSessionFactory());

			if (lStrExpiryDt.length() > 0) {
				lStrArrExpiryDt = lStrExpiryDt.split("/");
				lStrExpiryDay = lStrArrExpiryDt[0].trim();
				lStrExpiryMonth = lStrArrExpiryDt[1].trim();
				lStrExpiryYear = lStrArrExpiryDt[2].trim();

			}
			lLstEligibleBills = lObjOverPmntRecoveryDAO.getEligibleBills(lObjSimpleDateFormat.parse(lStrExpiryDt), lStrPnsnrCode, lStrBillType, gStrLocationCode);
			lObjIterator = lLstEligibleBills.iterator();
			Object[] lArrObject = null;
			int lIntCnt = 0;
			String lStrYear = null;
			String lStrMonth = null;
			String lStrDay = null;
			Double lDblTempAmnt = 0d;
			BigDecimal lBDTotRecoveryAmnt = BigDecimal.ZERO;

			GregorianCalendar lObjGregorianCalendar = new GregorianCalendar();
			Calendar lObjCalendar = Calendar.getInstance();
			Integer lIntTempDay = 0;
			Integer lIntMonth = 0;
			while (lObjIterator.hasNext()) {
				lArrObject = (Object[]) lObjIterator.next();
				if (lArrObject[1] != null) {
					lStrYear = lArrObject[1].toString().substring(0, 4);
					lStrMonth = lArrObject[1].toString().substring(4, 6);
					lStrDay = "01";
					if (lStrMonth.length() > 0) {
						lIntMonth = Integer.valueOf(lStrMonth);
						if (lIntMonth > 0) {
							lIntMonth = lIntMonth - 1;
						}
					}
				}

				// Net amount addition for eligible bills
				if (Integer.valueOf(lStrYear) >= Integer.valueOf(lStrExpiryYear)) {
					if (Integer.valueOf(lStrYear) > Integer.valueOf(lStrExpiryYear)) {
						if (lArrObject[0] != null) {
							lDblTempAmnt = lDblTempAmnt + ((BigDecimal) lArrObject[0]).doubleValue();
						}
					} else {
						if (Integer.valueOf(lStrMonth) > Integer.valueOf(lStrExpiryMonth)) {
							if (lArrObject[0] != null) {
								lDblTempAmnt = lDblTempAmnt + ((BigDecimal) lArrObject[0]).doubleValue();
							}
						}
						if (Integer.valueOf(lStrMonth).equals(Integer.valueOf(lStrExpiryMonth))) {
							lObjCalendar.set(Integer.valueOf(lStrYear), lIntMonth, Integer.valueOf(lStrDay));
							if (Integer.valueOf(lStrMonth) == 2) {
								if (lObjGregorianCalendar.isLeapYear(Integer.valueOf(lStrExpiryYear)) == true) {
									lIntTempDay = 28;
								} else {
									lIntTempDay = 29;
								}
							} else {
								lIntTempDay = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
							}
							if (lArrObject[0] != null) {
								lDblTempAmnt = lDblTempAmnt + ((BigDecimal) lArrObject[0]).doubleValue()
										- ((((BigDecimal) lArrObject[0]).doubleValue() / lIntTempDay) * (Integer.valueOf(lStrExpiryDay)));
							}
						}
					}
				}

				lIntCnt++;
			}
			lBDTotRecoveryAmnt = BigDecimal.valueOf(lDblTempAmnt).setScale(0, BigDecimal.ROUND_HALF_UP);

			String lSBStatus = getResponseXMLDocTotRecoveryAmnt(lBDTotRecoveryAmnt).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");

		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			//e.printStackTrace();
		}

		return objRes;

	}

	private StringBuilder getResponseXMLDocTotRecoveryAmnt(BigDecimal lBDTotRecoveryAmnt) {

		StringBuilder lStrHidPKs = new StringBuilder();

		lStrHidPKs.append("<XMLDOCRECOVERYAMNT>");
		lStrHidPKs.append("<TOTRECOVERYAMNT>");
		lStrHidPKs.append(lBDTotRecoveryAmnt);
		lStrHidPKs.append("</TOTRECOVERYAMNT>");
		lStrHidPKs.append("</XMLDOCRECOVERYAMNT>");
		return lStrHidPKs;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.OverPmntRecoveryService#getRecoveryDtls
	 * (java.util.Map)
	 */
	public ResultObject getRecoveryDetails(Map<String, Object> inputMap) {

		gLogger.info("In getRecoveryDtls method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		OverPmntRecoveryDAO lObjOverPmntRecoveryDAO = null;
		String lStrRequestId = null;
		List lLstRecoveryDtls = null;

		try {
			setSessionInfo(inputMap);
			lStrRequestId = StringUtility.getParameter("requestId", request);

			lObjOverPmntRecoveryDAO = new OverPmntRecoveryDAOImpl(null, serv.getSessionFactory());
			if (lStrRequestId.length() > 0) {
				lLstRecoveryDtls = lObjOverPmntRecoveryDAO.getRecoveryDtls(lStrRequestId, gStrLocationCode);
			}

			String lSBStatus = getResponseXMLDocRecoveryDtls(lLstRecoveryDtls).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");

		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			//e.printStackTrace();
		}

		return objRes;

	}

	private StringBuilder getResponseXMLDocRecoveryDtls(List lLstRecoveryDtls) {

		StringBuilder lStrBldXML = new StringBuilder();
		lStrBldXML.append("<XMLDOCRECOVERYDTLS>");
		if (lLstRecoveryDtls.size() > 0) {
			Iterator itr = lLstRecoveryDtls.iterator();

			while (itr.hasNext()) {
				Object[] obj = (Object[]) itr.next();
				lStrBldXML.append("<PnsnrCode>");
				lStrBldXML.append(obj[0].toString());
				lStrBldXML.append("</PnsnrCode>");
				lStrBldXML.append("<PPONo>");
				lStrBldXML.append("<![CDATA[");
				lStrBldXML.append(obj[1].toString());
				lStrBldXML.append("]]>");
				lStrBldXML.append("</PPONo>");
				lStrBldXML.append("<PnsnrName>");
				lStrBldXML.append(obj[2].toString());
				lStrBldXML.append("</PnsnrName>");
				lStrBldXML.append("<DeathDate>");
				lStrBldXML.append(obj[3].toString());
				lStrBldXML.append("</DeathDate>");
				lStrBldXML.append("<AmountToRecover>");
				lStrBldXML.append(obj[4].toString());
				lStrBldXML.append("</AmountToRecover>");
				lStrBldXML.append("<SchemeCode>");
				lStrBldXML.append(obj[5].toString());
				lStrBldXML.append("</SchemeCode>");
				lStrBldXML.append("<HeadCode>");
				lStrBldXML.append(obj[6].toString());
				lStrBldXML.append("</HeadCode>");
				lStrBldXML.append("<Status>");
				lStrBldXML.append(obj[7].toString());
				lStrBldXML.append("</Status>");
				if (obj[7].toString().trim().equals(gObjRsrcBndle.getString("OPR.APPROVE"))) {
					lStrBldXML.append("<PayOrderNo>");
					lStrBldXML.append((obj[8] != null) ? obj[8].toString() : "");
					lStrBldXML.append("</PayOrderNo>");
					lStrBldXML.append("<PayOrderDate>");
					lStrBldXML.append((obj[9] != null) ? DateUtilities.stringFromDate((Date) obj[9]) : "");
					lStrBldXML.append("</PayOrderDate>");
					lStrBldXML.append("<ChallanNo>");
					lStrBldXML.append((obj[10] != null) ? obj[10].toString() : "");
					lStrBldXML.append("</ChallanNo>");
					lStrBldXML.append("<ChallanDate>");
					lStrBldXML.append((obj[11] != null) ? DateUtilities.stringFromDate((Date) obj[11]) : "");
					lStrBldXML.append("</ChallanDate>");
					lStrBldXML.append("<PayOrderAmnt>");
					lStrBldXML.append((obj[12] != null) ? obj[12] : "");
					lStrBldXML.append("</PayOrderAmnt>");

				}

			}

		} else {
			lStrBldXML.append("<EmptyList>");
			lStrBldXML.append("EmptyList");
			lStrBldXML.append("</EmptyList>");
		}
		lStrBldXML.append("</XMLDOCRECOVERYDTLS>");
		return lStrBldXML;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.service.OverPmntRecoveryService#
	 * approveOverPmntRecovery(java.util.Map)
	 */
	public ResultObject approveOverPmntRecovery(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		OverPmntRecoveryDAO lObjOverPmntRecoveryDAO = null;
		String lStrAmntRecovered = null;
		String lStrRequestId = null;
		String lStrPayOrderNo = null;
		String lStrPayOrderDate = null;
		String lStrChallanNo = null;
		String lStrChallanDate = null;
		String lStrPayOrderAmnt = null;
		TrnPensionChallanDtls lObjTrnPensionChallanDtls = null;
		MstPensionerHdrDAOImpl lObjMstPensionerHdrDAOImpl = null;
		TrnPensionRqstHdrDAOImpl lObjTrnPensionRqstHdrDAOImpl = null;
		String lStrActionFlag = null;
		try {
			setSessionInfo(inputMap);
			lObjOverPmntRecoveryDAO = new OverPmntRecoveryDAOImpl(TrnPensionChallanDtls.class, serv.getSessionFactory());
			lObjMstPensionerHdrDAOImpl = new MstPensionerHdrDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			lObjTrnPensionRqstHdrDAOImpl = new TrnPensionRqstHdrDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
			lStrRequestId = StringUtility.getParameter("requestId", request);
			lStrAmntRecovered = StringUtility.getParameter("amntRecovered", request);
			lStrPayOrderNo = StringUtility.getParameter("payOrderNo", request);
			lStrPayOrderDate = StringUtility.getParameter("payOrderDate", request);
			lStrPayOrderAmnt = StringUtility.getParameter("payOrderAmnt", request);
			lStrChallanNo = StringUtility.getParameter("challanNo", request);
			lStrChallanDate = StringUtility.getParameter("challanDate", request);
			lStrActionFlag = StringUtility.getParameter("lStrActionFlag", request);
			String[] lArrParam = new String[2];
			lArrParam[0] = "requestId";
			lArrParam[1] = "locationCode";
			Object[] lArrValue = new Object[2];
			lArrValue[0] = lStrRequestId.trim();
			lArrValue[1] = gStrLocationCode;
			if (lStrRequestId.length() > 0) {
				List<TrnPensionChallanDtls> lLstTrnPensionChallanDtls = lObjOverPmntRecoveryDAO.getListByColumnAndValue(lArrParam, lArrValue);
				if (!lLstTrnPensionChallanDtls.isEmpty()) {
					lObjTrnPensionChallanDtls = lLstTrnPensionChallanDtls.get(0);
				}
				if ("A".equals(lStrActionFlag)) {
					lObjTrnPensionChallanDtls.setStatus(gObjRsrcBndle.getString("OPR.APPROVE"));
					if (lStrAmntRecovered.length() > 0) {
						lObjTrnPensionChallanDtls.setAmountRecovered(BigDecimal.valueOf(Long.parseLong((lStrAmntRecovered))));
					}

					lObjTrnPensionChallanDtls.setPayOrderNo(lStrPayOrderNo);
					lObjTrnPensionChallanDtls.setPayOrderDate(StringUtility.convertStringToDate(lStrPayOrderDate));
					if (!"".equals(lStrPayOrderAmnt) && lStrPayOrderAmnt.length() > 0) {
						lObjTrnPensionChallanDtls.setPayOrderAmnt(BigDecimal.valueOf(Long.valueOf(lStrPayOrderAmnt)));
					}
					lObjTrnPensionChallanDtls.setChallanNo(lStrChallanNo);
					lObjTrnPensionChallanDtls.setChallanDate(StringUtility.convertStringToDate(lStrChallanDate));
					lObjTrnPensionChallanDtls.setUpdatedDate(gDtCurDate);
					lObjTrnPensionChallanDtls.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjTrnPensionChallanDtls.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
				}
			}
			if ("R".equals(lStrActionFlag)) {
				lObjOverPmntRecoveryDAO = new OverPmntRecoveryDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
				List<MstPensionerHdr> lLstMstPensionerHdr = lObjOverPmntRecoveryDAO.getListByColumnAndValue("pensionerCode", lObjTrnPensionChallanDtls.getPensionerCode());
				if (!lLstMstPensionerHdr.isEmpty()) {
					MstPensionerHdr lObjMstPensionerHdr = lLstMstPensionerHdr.get(0);
					lObjMstPensionerHdr.setDateOfDeath(null);
					lObjMstPensionerHdr.setAliveFlag("Y");
					lObjMstPensionerHdrDAOImpl.update(lObjMstPensionerHdr);
					lObjTrnPensionChallanDtls.setStatus(gObjRsrcBndle.getString("OPR.REJECTED"));
					lObjTrnPensionChallanDtls.setUpdatedDate(gDtCurDate);
					lObjTrnPensionChallanDtls.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjTrnPensionChallanDtls.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
				}

			}
			lObjOverPmntRecoveryDAO = new OverPmntRecoveryDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
			/*
			 * List<TrnPensionRqstHdr> lLstTrnPensionRqstHdr =
			 * lObjOverPmntRecoveryDAO.getListByColumnAndValue("pensionerCode",
			 * lObjTrnPensionChallanDtls.getPensionerCode()); if
			 * (!lLstTrnPensionRqstHdr.isEmpty()) { TrnPensionRqstHdr
			 * lObjTrnPensionRqstHdr = lLstTrnPensionRqstHdr.get(0);
			 * lObjTrnPensionRqstHdr
			 * .setStatus(gObjRsrcBndle.getString("STATUS.CONTINUE"));
			 * lObjTrnPensionRqstHdrDAOImpl.update(lObjTrnPensionRqstHdr); }
			 */
			inputMap.put("ajaxKey", "Success");
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			//e.printStackTrace();
		}
		return objRes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.OverPmntRecoveryService#generateRecoveryLtr
	 * (java.util.Map)
	 */

	public ResultObject generateRecoveryLetter(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrUniqueLetterNo = null;
		String lStrSrNo = null;
		String lStrNewLine = StringUtils.getLineSeparator();
		String lStrOffAddr = null;
		String lStrPnsnrCode = null;
		StringBuffer lSbDisplayString = new StringBuffer();
		StringBuffer lSbPrintString = new StringBuffer();
		List lLstRecoveryLtrDtls = null;
		List lLstFamilyDtls = null;
		List lLstPeriodDates = new ArrayList();
		CVPRestorationLetterDAO lObjCVPRestorationLetterDAO = null;
		OverPmntRecoveryDAO lObjOverPmntRecoveryDAO = null;
		String lStrBankName = null;
		String lStrBankAddr = null;
		String lStrBranchName = null;
		String lStrBranchAddr = null;
		String lStrPnsnrName = null;
		Date lDtDeathDate = null;
		String lStrAccNo = "";
		String lStrLocationName = null;
		String lStrRequestId = "";
		BigDecimal lBDTotRecvryAmnt = BigDecimal.ZERO;
		String lStrFamilyMemberName = "";
		String lStrFamilyMemberAddr = "";
		String lStrBillForMonth = null;
		Date lDtLastPaidBillDate = new Date();
		String lStrNextDateAftrDeath = null;
		String lStrBillType = gObjRsrcBndle.getString("PPMT.MONTHLY");
		String lStrParaRequestId = null;
		try {
			setSessionInfo(inputMap);
			lStrPnsnrCode = StringUtility.getParameter("PnsnrCode", request).trim();
			lStrParaRequestId = StringUtility.getParameter("requsetId", request).trim();
			lObjCVPRestorationLetterDAO = new CVPRestorationLetterDAOImpl(null, serv.getSessionFactory());
			lStrOffAddr = lObjCVPRestorationLetterDAO.getOffiCeAddr(gStrLocationCode);
			lStrSrNo = IDGenerateDelegate.getNextIdWODbidLocationId("recoveryletter_sr_no", inputMap);
			lStrUniqueLetterNo = SessionHelper.getLocationVO(inputMap).getLocShortName().concat("/Pension Recovery").concat("/" + DateUtility.getCurrentDate()).concat("/" + lStrSrNo);
			lObjOverPmntRecoveryDAO = new OverPmntRecoveryDAOImpl(null, serv.getSessionFactory());
			lLstRecoveryLtrDtls = lObjOverPmntRecoveryDAO.getRecoveryLtrDtls(lStrParaRequestId, gStrLocationCode);
			lLstFamilyDtls = lObjOverPmntRecoveryDAO.getfamilyDtls(lStrPnsnrCode, gStrLocationCode);

			if (!lLstFamilyDtls.isEmpty() && lLstFamilyDtls.size() > 0) {
				Iterator it = lLstFamilyDtls.iterator();
				Object[] lObjArr = null;
				while (it.hasNext()) {
					lObjArr = (Object[]) it.next();
					if (lObjArr[0] != null) {
						lStrFamilyMemberName = lObjArr[0].toString().trim();
					}
					if (lObjArr[1] != null) {
						lStrFamilyMemberAddr = lObjArr[1].toString().trim();
					}
				}
			}
			Iterator lObjIterator = lLstRecoveryLtrDtls.iterator();
			Object[] tuple = null;
			while (lObjIterator.hasNext()) {
				tuple = (Object[]) lObjIterator.next();
				if (tuple[0] != null) {
					lStrBankName = tuple[0].toString().trim();
				}
				if (tuple[1] != null) {
					lStrBankAddr = tuple[1].toString().trim();
				}
				if (tuple[2] != null) {
					lStrBranchName = tuple[2].toString().trim();
				}
				if (tuple[3] != null) {
					lStrBranchAddr = tuple[3].toString().trim();
				}
				if (tuple[4] != null) {
					lStrPnsnrName = tuple[4].toString().trim();
				}
				if (tuple[5] != null) {
					lDtDeathDate = (Date) (tuple[5]);
				}
				if (tuple[6] != null) {
					lStrAccNo = tuple[6].toString().trim();
				}
				if (tuple[7] != null) {
					lBDTotRecvryAmnt = (BigDecimal) tuple[7];
				}
				if (tuple[8] != null) {
					lStrLocationName = tuple[8].toString().trim();
				}
				if (tuple[9] != null) {
					lStrRequestId = tuple[9].toString().trim();
				}

			}
			int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
			SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yy");
			if (lDtDeathDate != null) {
				lStrNextDateAftrDeath = lObjSimpleDateFormat.format(lDtDeathDate.getTime() + MILLIS_IN_DAY);
				lLstPeriodDates = lObjOverPmntRecoveryDAO.getPeriodDates(lStrBillType, lDtDeathDate, lStrPnsnrCode, gStrLocationCode);
			}

			if (!lLstPeriodDates.isEmpty() && lLstPeriodDates.size() > 0) {

				int lIntListIndex = lLstPeriodDates.size();
				Iterator it = lLstPeriodDates.iterator();
				Object[] lObjArr = null;
				for (int lIntCnt = 0; lIntCnt < lLstPeriodDates.size(); lIntCnt++) {
					lObjArr = (Object[]) lLstPeriodDates.get(lIntListIndex - 1);
					if (lObjArr[1] != null) {
						lStrBillForMonth = lObjArr[1].toString().trim();
					}
					String lStrYear = lStrBillForMonth.substring(0, lStrBillForMonth.length() - 2);
					String lStrMonth = lStrBillForMonth.substring(4, lStrBillForMonth.length());
					Calendar lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(StringUtility.convertStringToDate("01/".concat(lStrMonth).concat("/").concat(lStrYear)));
					lObjCalendar.set(Calendar.DATE, lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
					lDtLastPaidBillDate = lObjCalendar.getTime();
					break;
				}

			}

			if (lLstRecoveryLtrDtls != null && lLstRecoveryLtrDtls.size() > 0) {
				// //////////Display String Starts/////////////////
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(37) + gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR"));
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(39) + SessionHelper.getLocationName(inputMap));
				lSbDisplayString.append("\n");
				if (lStrOffAddr != null && lStrOffAddr.length() > 0) {
					lSbDisplayString.append(space(33) + lStrOffAddr);
				}
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(lStrUniqueLetterNo);
				lSbDisplayString.append("\n");
				lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.REQUESTID") + lStrRequestId);
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR1"));
				lSbDisplayString.append("\n");
				lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR2"));
				lSbDisplayString.append("\n");
				lSbDisplayString.append(lStrBankName);
				lSbDisplayString.append("\n");
				if (lStrBankAddr != null && lStrBankAddr.length() > 0) {
					lSbDisplayString.append(space(40) + lStrBankAddr + "\n");
				}
				lSbDisplayString.append(lStrBranchName);
				lSbDisplayString.append("\n");
				if (lStrBranchAddr != null) {
					lSbDisplayString.append(lStrBranchAddr);
				}
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR3"));
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(20) + gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR4") + space(2) + lStrPnsnrName + space(2) + gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR5"));
				if (lDtDeathDate != null) {
					lSbDisplayString.append(DateUtilities.stringFromDate(lDtDeathDate));
				}
				lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR6"));
				if (lStrAccNo.length() > 0) {
					lSbDisplayString.append(lStrAccNo);
				}
				lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR7"));
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(20) + gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR8") + space(2) + String.valueOf(lBDTotRecvryAmnt.doubleValue())
						+ gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR9") + space(2) + lStrNextDateAftrDeath + space(2) + " to " + DateUtilities.stringFromDate(lDtLastPaidBillDate) + ".");
				lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR10") + space(2) + lStrLocationName + ".");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(20) + gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR11"));
				lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR12"));
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(69) + gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR13"));
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(71) + gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR14"));
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(lStrUniqueLetterNo);
				lSbDisplayString.append("\n");

				if (lStrFamilyMemberName.length() > 0 && lStrFamilyMemberAddr.length() > 0) {
					lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR15"));
					lSbDisplayString.append(space(2) + lStrFamilyMemberName + space(2) + lStrFamilyMemberAddr + space(2));
					lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR16"));

				}
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(71) + gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR14"));
				lSbDisplayString.append((char) 12);
				// ////////////Display String Ends/////////////////

				// ////////////Print String Starts/////////////////
				lSbPrintString.append("<div><pre><font size='3px'>");
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(43) + " <strong> " + gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR") + " </strong> ");
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(47) + SessionHelper.getLocationName(inputMap));
				lSbPrintString.append(lStrNewLine);
				if (lStrOffAddr != null && lStrOffAddr.length() > 0) {
					lSbPrintString.append(space(40) + lStrOffAddr);
				}
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrUniqueLetterNo);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(gObjRsrcBndle.getString("PPMT.REQUESTID") + lStrRequestId);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR1"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR2"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrBankName);
				lSbPrintString.append(lStrNewLine);
				if (lStrBankAddr != null && lStrBankAddr.length() > 0) {
					lSbDisplayString.append(space(40) + lStrBankAddr + lStrNewLine);
				}
				lSbPrintString.append(lStrBranchName);
				lSbPrintString.append(lStrNewLine);
				if (lStrBranchAddr != null) {
					lSbPrintString.append(lStrBranchAddr);
				}
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR3"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(20) + gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR4") + space(2) + lStrPnsnrName + space(2) + gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR5"));
				lSbPrintString.append(lStrNewLine);
				if (lDtDeathDate != null) {
					lSbPrintString.append(DateUtilities.stringFromDate(lDtDeathDate) + space(2));
				}
				lSbPrintString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR6"));
				if (lStrAccNo.length() > 0) {
					lSbPrintString.append(space(2) + lStrAccNo + space(2));
				}
				lSbPrintString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR7"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(20) + gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR8") + space(2) + String.valueOf(lBDTotRecvryAmnt.doubleValue()) + space(2)
						+ gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR9"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNextDateAftrDeath + " to " + DateUtilities.stringFromDate(lDtLastPaidBillDate) + ".");
				lSbPrintString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR10") + space(1) + lStrLocationName + ".");
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(20) + gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR11"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR12"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(90) + gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR13"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(94) + gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR14"));
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrUniqueLetterNo);
				lSbPrintString.append(lStrNewLine);
				if (lStrFamilyMemberName.length() > 0 && lStrFamilyMemberAddr.length() > 0) {
					lSbPrintString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR15"));
					lSbPrintString.append(space(2) + lStrFamilyMemberName + space(2) + lStrFamilyMemberAddr + space(2));
					lSbPrintString.append(gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR16"));

				}
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(94) + gObjRsrcBndle.getString("PPMT.PNSNRECVRYLTR14"));
				lSbPrintString.append((char) 12);

				// ////////////Print String Ends/////////////////
			}

			inputMap.put("DisplayRecoveryLetterString", lSbDisplayString);
			inputMap.put("PrintRecoveryLetterString", lSbPrintString);
			objRes.setResultValue(inputMap);
			objRes.setViewName("printRecoveryLetterPopUp");
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is:" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

	public String space(int noOfSpace) {

		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}
}
