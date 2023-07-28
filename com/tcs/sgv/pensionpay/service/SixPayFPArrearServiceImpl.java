/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 10, 2011		Vrajesh Raval								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.icu.util.Calendar;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.AdminRateMstDAO;
import com.tcs.sgv.pensionpay.dao.AdminRateMstDAOImpl;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAO;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.dao.SixPayFPArrearDAO;
import com.tcs.sgv.pensionpay.dao.SixPayFPArrearDAOImpl;
import com.tcs.sgv.pensionpay.dao.TransferPPODAO;
import com.tcs.sgv.pensionpay.dao.TransferPPODAOImpl;
import com.tcs.sgv.pensionpay.valueobject.HstTrnPensionSixpayfpArrear;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionSixpayfpArrear;


/**
 * Class Description - This class has the implementation of all the methods of
 * Six Pay arrear and FP change from 7years to 10 years arrear functionality.
 * 
 * 
 * @author Vrajesh Raval
 * @version 0.1
 * @since JDK 5.0 Jun 10, 2011
 */
public class SixPayFPArrearServiceImpl extends ServiceImpl implements SixPayFPArrearService {

	/* Global Variable for LangId */
	Long gLngLangId = null;
	Long gLngUserId = null;
	Long gLngPostId = null;
	Date gDtCurr = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	Log gLogger = LogFactory.getLog(getClass());

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");

	private final ResourceBundle gObjCaseRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	/* Configuration for number of installments for arrear process */
	// final Integer NO_OF_INSTALLMENTS =
	// Integer.valueOf(gObjRsrcBndle.getString("SIXPAY.NOOFINSTALLMENTS"));

	private void setSessionInfo(Map inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gDtCurr = DBUtility.getCurrentDateFromDB();
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		serv = (ServiceLocator) inputMap.get("serviceLocator");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.SixPayFPArrearService#getArrearScreen(
	 * java.util.Map)
	 */

	public ResultObject getArrearScreen(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List<ComboValuesVO> lLstMonths = null;
		List<ComboValuesVO> lLstYears = null;
		// List lLstGetAllPnsnrCode = null;
		String lStrRevision = null;
		String lStrPnsnrCode = null;
		// List lLstFinalPnsnrCode = new ArrayList();
		SixPayFPArrearDAO lObjSixPayFPArrearDAO = null;
		String lStrHistory = null;
		String lStrScreen = null;
		List lLstPPODtls = null;
		CommonDAO lObjCommonDAO = null;
		List lLstSixPayArrearCashDtls = null;
		try {
			setSessionInfo(inputMap);
			lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			lObjSixPayFPArrearDAO = new SixPayFPArrearDAOImpl(null, serv.getSessionFactory());
			lLstMonths = lObjCommonDAO.getMonthList(SessionHelper.getLocale(request));
			lLstYears = lObjCommonDAO.getYearList(SessionHelper.getLocale(request));
			/*
			 * lLstGetAllPnsnrCode = lObjSixPayFPArrearDAO.getAllPnsnrCode();
			 * Iterator it = lLstGetAllPnsnrCode.iterator();
			 * 
			 * String tuple = null;
			 * 
			 * for (int lIntLoop = 0; lIntLoop < lLstGetAllPnsnrCode.size();
			 * lIntLoop++) { tuple = it.next().toString().trim(); String
			 * lStrPnsnsrCode = tuple.trim();
			 * lLstFinalPnsnrCode.add(lStrPnsnsrCode); }
			 */
			Iterator iterator = lLstMonths.iterator();
			ComboValuesVO comboMonthsVO = new ComboValuesVO();
			int lIntLoopJ = 0;
			List<String> lLstComboMonthDesc = new ArrayList<String>();
			List<String> lLstComboMonthId = new ArrayList<String>();
			String lStrMonthId = null;
			String lStrMonthDesc = null;
			while (iterator.hasNext()) {
				comboMonthsVO = (ComboValuesVO) iterator.next();
				lStrMonthDesc = comboMonthsVO.getDesc().toString().trim();
				lStrMonthId = comboMonthsVO.getId().toString().trim();
				lLstComboMonthDesc.add(lStrMonthDesc.trim());
				lLstComboMonthId.add(lStrMonthId.trim());
				lIntLoopJ++;
			}

			Iterator lObjIterator = lLstYears.iterator();
			ComboValuesVO comboYearsVO = new ComboValuesVO();
			int lIntCount = 0;
			List<String> lLstComboYearDesc = new ArrayList<String>();
			String lStrYearDesc = null;

			while (lObjIterator.hasNext()) {
				comboYearsVO = (ComboValuesVO) lObjIterator.next();
				lStrYearDesc = comboYearsVO.getDesc().toString().trim();
				lLstComboYearDesc.add(lStrYearDesc.trim());
				lIntCount++;
			}

			inputMap.put("ListOfMonthDesc", lLstComboMonthDesc);
			inputMap.put("ListOfMonthId", lLstComboMonthId);
			inputMap.put("TotalNoOfMonths", lLstMonths.size());

			inputMap.put("ListOfYearDesc", lLstComboYearDesc);
			inputMap.put("TotalNoOfYears", lLstYears.size());

			// inputMap.put("lLstPnsnrCode", lLstFinalPnsnrCode);
			// inputMap.put("TotalNoOfPnsnrCode", lLstFinalPnsnrCode.size());

			inputMap.put("lLstMonths", lLstMonths);
			inputMap.put("lLstYears", lLstYears);

			lStrRevision = StringUtility.getParameter("Revision", request);
			lStrHistory = StringUtility.getParameter("History", request);
			lStrPnsnrCode = StringUtility.getParameter("txtPnsnrCode", request);
			lStrScreen = StringUtility.getParameter("type", request);

			if (lStrRevision.length() > 0) {

				inputMap.put("Revision", lStrRevision.trim());
				inputMap.put("PnsnrCode", lStrPnsnrCode.trim());
				resObj.setResultValue(inputMap);
				resObj.setViewName("SixPayFPArrearRevised");
			} else if (lStrHistory.length() > 0) {
				if (lStrPnsnrCode.length() > 0) {
					lLstPPODtls = lObjSixPayFPArrearDAO.getPPODtls(lStrPnsnrCode.trim(), lStrHistory.trim());
				}
				if (lLstPPODtls != null && lLstPPODtls.size() > 0) {
					Iterator lObjiterator = lLstPPODtls.iterator();
					Object[] lArrObj = null;
					int lIntCnt = 0;
					List<String> lLstPPOYearDesc = new ArrayList<String>();
					List<String> lLstPPOMonthId = new ArrayList<String>();
					List<String> lLstPPOMonthDesc = new ArrayList<String>();
					List<BigDecimal> lLstPPOInstallments = new ArrayList<BigDecimal>();
					List<String> lLstRemarks = new ArrayList<String>();
					String lStrPPOYearMonth = null;
					String lStrPPOYearDesc = null;
					String lStrPPOMonthId = null;
					String lStrPPOMonthDesc = null;
					String lStrRemarks = null;
					while (lObjiterator.hasNext()) {
						lArrObj = (Object[]) lObjiterator.next();
						if (lArrObj[0] == null) {
							lLstPPOYearDesc.add(null);
							lLstPPOMonthId.add(null);
							lLstPPOMonthDesc.add(null);
						} else {
							lStrPPOYearMonth = lArrObj[0].toString().trim();
							lStrPPOYearDesc = lStrPPOYearMonth.substring(0, 4);
							lStrPPOMonthId = lStrPPOYearMonth.substring(4);
							lStrPPOMonthDesc = lObjSixPayFPArrearDAO.getMonthDescFromMonthId(lStrPPOMonthId.trim(), SessionHelper.getLocale(request));
							lLstPPOYearDesc.add(lStrPPOYearDesc.trim());
							lLstPPOMonthId.add(lStrPPOMonthId.trim());
							lLstPPOMonthDesc.add(lStrPPOMonthDesc.trim());
						}

						BigDecimal lBDInstallmentAmnt = (BigDecimal) lArrObj[1];
						lLstPPOInstallments.add(lBDInstallmentAmnt);

						if (lArrObj[2] == null) {
							lLstRemarks.add(lStrRemarks);
						} else {
							lStrRemarks = lArrObj[2].toString();
							lLstRemarks.add(lStrRemarks);
						}
						lIntCnt++;
					}

					inputMap.put("lLstPPOYearDesc", lLstPPOYearDesc);
					inputMap.put("lLstPPOMonthId", lLstPPOMonthId);
					inputMap.put("lLstPPOMonthDesc", lLstPPOMonthDesc);
					inputMap.put("lLstPPOInstallments", lLstPPOInstallments);
					inputMap.put("lLstRemarks", lLstRemarks);
					inputMap.put("Counter", lLstPPOInstallments.size() / 5);
				}

				resObj.setResultValue(inputMap);
				resObj.setViewName("SixPayFPArrearHistory");
			} else if (lStrScreen.length() > 0) {
				resObj.setResultValue(inputMap);
				resObj.setViewName("SixPayInstlmntDateUpdate");
			} else {

				resObj.setResultValue(inputMap);
				resObj.setViewName("SixPayFPArrear");
			}

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			// e.printStackTrace();
		}

		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.SixPayFPArrearService#getSixPayPPODtls
	 * (java.util.Map)
	 */

	public ResultObject getSixPayPPODtls(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List lLstPnsnsrNameAndPnsnsrCode = null;
		String lStrPPONo = null;
		TransferPPODAO lObjTransferPPODAO = null;
		String lStrFlagVal = null;
		String lStrPnsnrCode = null;
		String lStrResult = null;
		String lSBStatus = null;
		String lStrCashArrearResp = "";
		List lLstPPODtls = null;
		SixPayFPArrearDAO lObjSixPayFPArrearDAO = null;
		String lStrRopType = null;
		List<Object[]> lArrSixPayArrearDtls = null;

		try {
			setSessionInfo(inputMap);
			lObjTransferPPODAO = new TransferPPODAOImpl(TransferPPOServiceImpl.class, serv.getSessionFactory());
			lObjSixPayFPArrearDAO = new SixPayFPArrearDAOImpl(null, serv.getSessionFactory());

			lStrPPONo = StringUtility.getParameter("txtPpoNo", request);
			lStrPnsnrCode = StringUtility.getParameter("pnsnrCode", request);

			if (lStrPPONo.length() > 0 || lStrPnsnrCode.length() > 0) {
				lStrFlagVal = StringUtility.getParameter("FlagVal", request);
				if (lStrFlagVal.trim().length() > 0) {
					if (lStrFlagVal.equals("Y")) {
						if (lStrPnsnrCode.length() > 0) {
							lLstPPODtls = lObjSixPayFPArrearDAO.getPPODtls(lStrPnsnrCode, null);
						}
						if (lLstPPODtls != null && lLstPPODtls.size() > 0) {
							Iterator it = lLstPPODtls.iterator();
							Object[] tuple = null;
							int lIntLoopJ = 0;
							Long lLngArrearId = null;
							String lStrPaidFlag = "";
							Character lChPaidFlag = null;
							List<String> lLstYearDesc = new ArrayList<String>();
							List<String> lLstMonthId = new ArrayList<String>();
							List<String> lLstMonthDesc = new ArrayList<String>();
							List<BigDecimal> lLstInstallments = new ArrayList<BigDecimal>();
							List<Long> lLstArrearId = new ArrayList<Long>();
							List<String> lLstPaidFlag = new ArrayList<String>();
							String lStrYearMonth = null;
							String lStrYearDesc = null;
							String lStrMonthId = null;
							String lStrMonthDesc = null;
							while (it.hasNext()) {
								tuple = (Object[]) it.next();
								if (tuple[0] == null) {
									lLstYearDesc.add(null);
									lLstMonthId.add(null);
									lLstMonthDesc.add(null);
								} else {
									lStrYearMonth = tuple[0].toString().trim();
									lStrYearDesc = lStrYearMonth.substring(0, 4);
									lStrMonthId = lStrYearMonth.substring(4);
									lStrMonthDesc = lObjSixPayFPArrearDAO.getMonthDescFromMonthId(lStrMonthId.trim(), SessionHelper.getLocale(request));
									lLstYearDesc.add(lStrYearDesc.trim());
									lLstMonthId.add(lStrMonthId.trim());
									lLstMonthDesc.add(lStrMonthDesc.trim());
								}

								BigDecimal lBDInstallmentAmnt = (BigDecimal) tuple[1];
								lLstInstallments.add(lBDInstallmentAmnt);

								lLngArrearId = (Long) tuple[3];
								lLstArrearId.add(lLngArrearId);

								lChPaidFlag = (Character) tuple[4];
								lStrPaidFlag = Character.toString(lChPaidFlag);
								lLstPaidFlag.add(lStrPaidFlag);
								lIntLoopJ++;
							}
							lSBStatus = getResponseXMLDocPPODtls(lLstYearDesc, lLstMonthId, lLstMonthDesc, lLstInstallments, lLstArrearId, lLstPaidFlag).toString();
							lArrSixPayArrearDtls = lObjSixPayFPArrearDAO.getCashSixPayArrearDtls(lStrPnsnrCode);
							if (lArrSixPayArrearDtls != null && lArrSixPayArrearDtls.size() > 0) {
								lStrCashArrearResp = getResponseCashArrearDtls(lArrSixPayArrearDtls);
							}
							lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus + lStrCashArrearResp).toString();
						} else {
							lSBStatus = getResponseXMLDocPPODtls(null, null, null, null, null, null).toString();
							lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
						}
					}
				} else {
					List<Object[]> lLstPPONoDtls = lObjSixPayFPArrearDAO.validatePPONo(lStrPPONo.toUpperCase(), gLngLangId, gStrLocationCode);
					String lStrErrorMessage = null;
					String lStrCaseStatus = "";
					if (!lLstPPONoDtls.isEmpty()) {
						for (Object[] lArrObj : lLstPPONoDtls) {
							lStrRopType = (lArrObj[2] != null) ? lArrObj[2].toString() : null;
							lStrCaseStatus = (lArrObj[1] != null) ? lArrObj[1].toString() : null;
						}
						if (lStrCaseStatus != null) {
							if (lStrCaseStatus.equals(gObjCaseRsrcBndle.getString("STATFLG.APPROVED")) && "2006".equals(lStrRopType)) {
								lLstPnsnsrNameAndPnsnsrCode = lObjTransferPPODAO.getNameAndTreasuryFromPPONo(lStrPPONo, gLngLangId, gStrLocationCode);
								lStrRopType = lObjSixPayFPArrearDAO.getRopType(lStrPPONo.toUpperCase(), gStrLocationCode);
								lSBStatus = getResponseXMLDocPPONo(lLstPnsnsrNameAndPnsnsrCode, lStrRopType).toString();
								lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
							} else if (!lStrCaseStatus.equals(gObjCaseRsrcBndle.getString("STATFLG.APPROVED"))) {
								lStrErrorMessage = "Case is not in approved status.";
							} else if (!"2006".equals(lStrRopType)) {
								lStrErrorMessage = "Sixpay arrear can be calculated only for cases having 6PC roptype.Roptype is " + lStrRopType + " for this ppo.";
							}
						}
					} else {
						lStrErrorMessage = "No such ppo exists.";
					}
					if (lStrErrorMessage != null) {
						StringBuilder lStrBldXML = new StringBuilder();
						lStrBldXML.append("<ERRORMSG>");
						lStrBldXML.append(lStrErrorMessage);
						lStrBldXML.append("</ERRORMSG>");
						lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
					}
				}

			}
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

			// inputMap.put("noOfInstallments", NO_OF_INSTALLMENTS);

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			// e.printStackTrace();
		}

		return resObj;
	}

	private StringBuilder getResponseXMLDocPPODtls(List<String> lLstYearDesc, List<String> lLstMonthId, List<String> lLstMonthDesc, List<BigDecimal> lLstInstallments, List<Long> lLstArrearId,
			List<String> lLstPaidFlag) {

		StringBuilder lStrBldXML = new StringBuilder();
		lStrBldXML.append("<XMLDOCPPODTLS>");
		if (lLstYearDesc != null && lLstMonthId != null && lLstMonthDesc != null && lLstInstallments != null) {
			lStrBldXML.append("<ListYearDesc>");
			lStrBldXML.append(lLstYearDesc);
			lStrBldXML.append("</ListYearDesc>");
			lStrBldXML.append("<ListMonthId>");
			lStrBldXML.append(lLstMonthId);
			lStrBldXML.append("</ListMonthId>");
			lStrBldXML.append("<ListMonthDesc>");
			lStrBldXML.append(lLstMonthDesc);
			lStrBldXML.append("</ListMonthDesc>");
			lStrBldXML.append("<ListInstallments>");
			lStrBldXML.append(lLstInstallments);
			lStrBldXML.append("</ListInstallments>");
			lStrBldXML.append("<Count>");
			lStrBldXML.append(lLstInstallments.size());
			lStrBldXML.append("</Count>");
			lStrBldXML.append("<ISDTLSEXSIST>");
			lStrBldXML.append("Y");
			lStrBldXML.append("</ISDTLSEXSIST>");
			lStrBldXML.append("<ARREARID>");
			lStrBldXML.append(lLstArrearId);
			lStrBldXML.append("</ARREARID>");
			lStrBldXML.append("<PAIDFLAG>");
			lStrBldXML.append(lLstPaidFlag);
			lStrBldXML.append("</PAIDFLAG>");
		} else {
			lStrBldXML.append("<ISDTLSEXSIST>");
			lStrBldXML.append("N");
			lStrBldXML.append("</ISDTLSEXSIST>");
		}
		lStrBldXML.append("</XMLDOCPPODTLS>");

		return lStrBldXML;
	}

	private String getResponseCashArrearDtls(List<Object[]> lArrSixPayArrearDtls) {

		StringBuilder lSBResp = new StringBuilder();
		for (Object[] lObj : lArrSixPayArrearDtls) {
			lSBResp.append("<CASHARREARDTLS>");
			lSBResp.append("<YEARMONTH>");
			lSBResp.append(lObj[0]);
			lSBResp.append("</YEARMONTH>");
			lSBResp.append("<AMOUNT>");
			lSBResp.append(lObj[1]);
			lSBResp.append("</AMOUNT>");
			lSBResp.append("<PAIDFLAG>");
			lSBResp.append(lObj[2]);
			lSBResp.append("</PAIDFLAG>");
			lSBResp.append("<TABLEPK>");
			lSBResp.append(lObj[3]);
			lSBResp.append("</TABLEPK>");
			lSBResp.append("</CASHARREARDTLS>");
		}
		return lSBResp.toString();
	}

	private StringBuilder getResponseXMLDocPPONo(List lLstPnsnsrNameAndPnsnsrCode, String lStrRopType) {

		StringBuilder lStrBldXML = new StringBuilder();
		lStrBldXML.append("<XMLDOCPPO>");
		if (lLstPnsnsrNameAndPnsnsrCode != null && lLstPnsnsrNameAndPnsnsrCode.size() > 0) {
			Iterator itr = lLstPnsnsrNameAndPnsnsrCode.iterator();
			while (itr.hasNext()) {
				Object[] obj = (Object[]) itr.next();
				lStrBldXML.append("<PnsnrName>");
				lStrBldXML.append(obj[0].toString());
				lStrBldXML.append("</PnsnrName>");
				/*
				 * lStrBldXML.append("<TrsryName>");
				 * lStrBldXML.append("<![CDATA[");
				 * lStrBldXML.append(obj[1].toString());
				 * lStrBldXML.append("]]>"); lStrBldXML.append("</TrsryName>");
				 * lStrBldXML.append("<LocationCode>");
				 * lStrBldXML.append(obj[2].toString());
				 * lStrBldXML.append("</LocationCode>");
				 */
				lStrBldXML.append("<PnsnrCode>");
				lStrBldXML.append(obj[3].toString());
				lStrBldXML.append("</PnsnrCode>");
				lStrBldXML.append("<CaseStatus>");
				lStrBldXML.append(obj[4].toString());
				lStrBldXML.append("</CaseStatus>");
				lStrBldXML.append("<RopType>");
				lStrBldXML.append(lStrRopType);
				lStrBldXML.append("</RopType>");
				lStrBldXML.append("<FamilyPnsrName>");
				lStrBldXML.append((obj[7] != null) ? obj[7].toString() : "");
				lStrBldXML.append("</FamilyPnsrName>");
			}
		} else {
			lStrBldXML.append("<EmptyList>");
			lStrBldXML.append("EmptyList");
			lStrBldXML.append("</EmptyList>");
		}

		lStrBldXML.append("</XMLDOCPPO>");
		return lStrBldXML;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.SixPayFPArrearService#saveSixPayArrears
	 * (java.util.Map)
	 */
	public ResultObject saveSixPayArrears(Map inputMap) {

		gLogger.info("In saveSixPayArrears method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SixPayFPArrearDAO lObjSixPayFPArrearDAO = null;
		Long lLngArrearId = null;
		List<TrnPensionSixpayfpArrear> lLstTrnPensionSixpayfpArrearVO = null;
		TrnPensionSixpayfpArrear lObjTrnPensionSixpayfpArrearVO = null;
		StringBuilder lStrBldXML = null;
		String lStrResult = null;
		String lStrTransMode = null;
		List<TrnPensionSixpayfpArrear> lLstCashSixPayArrear = null;
		Long lLngPkCntTrnPensionSixPayFpArrear = null;
		Long lLngSixPayArrearId = null;
		String lStrSixPayCalcFlag = null;
		try {
			setSessionInfo(inputMap);
			lLstTrnPensionSixpayfpArrearVO = new ArrayList<TrnPensionSixpayfpArrear>();
			lObjTrnPensionSixpayfpArrearVO = new TrnPensionSixpayfpArrear();
			lStrTransMode = (String) inputMap.get("Mode");
			String lStrPnsnrCode = StringUtility.getParameter("txtPnsnrCode", request);
			lStrSixPayCalcFlag = (String) inputMap.get("SixPayCalcFlag");
			lLstTrnPensionSixpayfpArrearVO = (List<TrnPensionSixpayfpArrear>) inputMap.get("lLstTrnPensionSixpayfpArrearVO");
			lLstCashSixPayArrear = (List<TrnPensionSixpayfpArrear>) inputMap.get("lLstCashSixPayArrear");
			lObjSixPayFPArrearDAO = new SixPayFPArrearDAOImpl(TrnPensionSixpayfpArrear.class, serv.getSessionFactory());
			Boolean lBlPaidFlag;
			// Revise means in case of revision of any PPO case ::: create new
			// entries with same PPO with new revision counter and new
			// installment amount
			if (lStrTransMode.equalsIgnoreCase("Add") || lStrTransMode.equalsIgnoreCase("Revise")) {

				if (lLstTrnPensionSixpayfpArrearVO != null) {
					for (int lIntCount = 0; lIntCount < lLstTrnPensionSixpayfpArrearVO.size(); lIntCount++) {
						lObjTrnPensionSixpayfpArrearVO = lLstTrnPensionSixpayfpArrearVO.get(lIntCount);
						lLngArrearId = IFMSCommonServiceImpl.getNextSeqNum("TRN_PENSION_SIXPAYFP_ARREAR", inputMap);
						lObjTrnPensionSixpayfpArrearVO.setArrearId(lLngArrearId);
						if (lObjTrnPensionSixpayfpArrearVO.getDiffAmnt().longValue() != 0l) {
							lBlPaidFlag = true;
						} else {
							lBlPaidFlag = false;
						}
						lObjSixPayFPArrearDAO.create(lObjTrnPensionSixpayfpArrearVO);
						lObjSixPayFPArrearDAO = new SixPayFPArrearDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());

						// ---Setting ROP Type to 2006 on
						if (lStrSixPayCalcFlag != null && "Y".equals(lStrSixPayCalcFlag)) {
							lStrPnsnrCode = (String) inputMap.get("PensionerCode");
							if (lStrPnsnrCode.length() > 0) {
								List<TrnPensionRqstHdr> lLstTrnPensionRqstHdr = lObjSixPayFPArrearDAO.getListByColumnAndValue("pensionerCode", lStrPnsnrCode.trim());
								if (lLstTrnPensionRqstHdr != null && !lLstTrnPensionRqstHdr.isEmpty()) {
									TrnPensionRqstHdr lObjTrnTrnPensionRqstHdr = lLstTrnPensionRqstHdr.get(0);
									lObjTrnTrnPensionRqstHdr.setRopType("2006");
									lObjTrnTrnPensionRqstHdr.setDpFlag("N");
									lObjTrnTrnPensionRqstHdr.setUpdatedDate(gDtCurr);
									lObjTrnTrnPensionRqstHdr.setUpdatedUserId(new BigDecimal(gLngUserId));
									lObjTrnTrnPensionRqstHdr.setUpdatedPostId(new BigDecimal(gLngPostId));
								}
							}
						}
						// if (lBlPaidFlag == true) {
						// if (lStrTransMode.equalsIgnoreCase("Revise")) {
						// if (lStrPnsnrCode.length() > 0) {
						// lObjSixPayFPArrearDAO = new
						// SixPayFPArrearDAOImpl(TrnPensionRqstHdr.class,
						// serv.getSessionFactory());
						// List<TrnPensionRqstHdr> lLstTrnPensionRqstHdr =
						// lObjSixPayFPArrearDAO.getListByColumnAndValue("pensionerCode",
						// lStrPnsnrCode.trim());
						// if (lLstTrnPensionRqstHdr != null &&
						// !lLstTrnPensionRqstHdr.isEmpty()) {
						// TrnPensionRqstHdr lObjTrnTrnPensionRqstHdr =
						// lLstTrnPensionRqstHdr.get(0);
						// if (lObjTrnTrnPensionRqstHdr.getArrearAmount() !=
						// null) {
						// lObjTrnTrnPensionRqstHdr.setArrearAmount(BigDecimal.valueOf(lObjTrnTrnPensionRqstHdr.getArrearAmount().longValue()
						// +
						// lObjTrnPensionSixpayfpArrearVO.getDiffAmnt().longValue()));
						// } else {
						// lObjTrnTrnPensionRqstHdr.setArrearAmount(BigDecimal.valueOf(lObjTrnPensionSixpayfpArrearVO.getDiffAmnt().longValue()));
						// }
						// }
						// }
						// }
						// }
						gLogger.info("Record Inserted in table TRN_PENSION_SIXPAYFP_ARREAR successfully");

					}

				}
			}
			// Config means in case of admin set the payment month and payment
			// year for particular PPO case :::
			if (lStrTransMode.equalsIgnoreCase("Config")) {
				if (lLstTrnPensionSixpayfpArrearVO != null) {
					for (int lIntCount = 0; lIntCount < lLstTrnPensionSixpayfpArrearVO.size(); lIntCount++) {
						lObjTrnPensionSixpayfpArrearVO = lLstTrnPensionSixpayfpArrearVO.get(lIntCount);
						lObjSixPayFPArrearDAO.update(lObjTrnPensionSixpayfpArrearVO);
						gLogger.info("Record upadated  in table TRN_PENSION_SIXPAYFP_ARREAR successfully");

					}
				}
			}
			if (lStrTransMode.equalsIgnoreCase("Update")) {
				if (lLstTrnPensionSixpayfpArrearVO != null) {
					for (int lIntCount = 0; lIntCount < lLstTrnPensionSixpayfpArrearVO.size(); lIntCount++) {
						lObjTrnPensionSixpayfpArrearVO = lLstTrnPensionSixpayfpArrearVO.get(lIntCount);
						lObjSixPayFPArrearDAO.update(lObjTrnPensionSixpayfpArrearVO);
						gLogger.info("Record upadated  in table TRN_PENSION_SIXPAYFP_ARREAR successfully");

					}
				}
			}
			if (lStrTransMode.equalsIgnoreCase("Add") || lStrTransMode.equalsIgnoreCase("Update") || (lStrSixPayCalcFlag != null && "Y".equals(lStrSixPayCalcFlag))) {
				if(!"Y".equals(lStrSixPayCalcFlag))
				{
					lObjSixPayFPArrearDAO.deleteUnPaidSixPayArrearDtls(lStrPnsnrCode);
				}
				if (lLstCashSixPayArrear != null) {
					lLngPkCntTrnPensionSixPayFpArrear = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("TRN_PENSION_SIXPAYFP_ARREAR", inputMap, 1);
					for (TrnPensionSixpayfpArrear lObjTrnPensionSixpayfpArrear : lLstCashSixPayArrear) {
						lLngSixPayArrearId = ++lLngPkCntTrnPensionSixPayFpArrear;
						lLngSixPayArrearId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngSixPayArrearId, inputMap);
						lObjTrnPensionSixpayfpArrear.setArrearId(lLngSixPayArrearId);
						lObjSixPayFPArrearDAO.create(lObjTrnPensionSixpayfpArrear);
					}
				}
			}
			lStrBldXML = getResponseXMLDoc(inputMap, lStrTransMode);
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);

			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is:" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");

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
		if (strMode.equalsIgnoreCase("Revise")) {
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append("Revise");
			lStrHidPKs.append("</MESSAGECODE>");
			lStrHidPKs.append("</XMLDOC>");
		}
		if (strMode.equalsIgnoreCase("Update")) {
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append("Update");
			lStrHidPKs.append("</MESSAGECODE>");
			lStrHidPKs.append("</XMLDOC>");
		}
		if (strMode.equalsIgnoreCase("Config")) {
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append("Config");
			lStrHidPKs.append("</MESSAGECODE>");
			lStrHidPKs.append("</XMLDOC>");
		}

		gLogger.info("lStrHidPKs : " + lStrHidPKs);
		return lStrHidPKs;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.SixPayFPArrearService#loadSixPayFPArrearConfig
	 * (java.util.Map)
	 */
	public ResultObject loadSixPayFPArrearConfig(Map inputMap) {

		gLogger.info("In loadSixPayFPArrearConfig method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SixPayFPArrearDAO lObjSixPayFPArrearDAO = null;
		List<ComboValuesVO> lLstHeadCodes = null;
		List lLstInstallments = null;
		List<ComboValuesVO> lLstMonths = null;
		List<ComboValuesVO> lLstYears = null;
		String lStrNoOfInstallment = null;
		String lStrHeadCode = null;
		String[] lStrArrHeadCode = null;
		List lLstSixPayFPArrears = null;
		Integer lIntTotalRecords = null;
		List<BigDecimal> lLstBDHeadCode = new ArrayList<BigDecimal>();
		String lStrCurrMonth = null;
		String lStrCurrYear = null;
		CommonPensionDAO lObjCommonPensionDAO = null;
		AdminRateMstDAO lObjAdminRateMst = null;
		List lLstStateDept = null;
		String lStrPPONo = null;
		String lStrArrearConfigBy = null;
		String lStrNoValidPpoMsg = "";
		CommonDAO lObjCommonDAO = null;
		List<ComboValuesVO> lLstAllTreasury = null;
		List<Long> lLstTreasuryId = new ArrayList<Long>();
		try {
			setSessionInfo(inputMap);
			GregorianCalendar cal = new GregorianCalendar();
			int month = cal.get(Calendar.MONTH);
			int year = cal.get(Calendar.YEAR);
			lStrCurrYear = String.valueOf(year);
			lStrCurrMonth = String.valueOf(month + 1);

			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			lLstMonths = lObjCommonDAO.getMonthList(SessionHelper.getLocale(request));
			lLstYears = lObjCommonDAO.getYearList(SessionHelper.getLocale(request));
			lLstTreasuryId.add(Long.valueOf(gObjCaseRsrcBndle.getString("PPMT.TREASURYID1")));
			lLstAllTreasury = lObjCommonDAO.getAllTreasury(gLngLangId, lLstTreasuryId);

			lLstInstallments = IFMSCommonServiceImpl.getLookupValues("No Of Installment", gLngLangId, inputMap);
			lObjSixPayFPArrearDAO = new SixPayFPArrearDAOImpl(null, serv.getSessionFactory());
			lLstHeadCodes = lObjCommonPensionDAO.getAllHeadCode();
			lObjAdminRateMst = new AdminRateMstDAOImpl(serv.getSessionFactory());
			lLstStateDept = lObjAdminRateMst.getAllStateDept(gLngLangId);
			lStrNoOfInstallment = StringUtility.getParameter("cmbInstallments", request).trim();
			lStrPPONo = StringUtility.getParameter("ppoNo", request).trim();
			lStrArrearConfigBy = StringUtility.getParameter("arrearConfigBy", request).trim();
			if (lStrNoOfInstallment.length() > 0) {
				lStrHeadCode = StringUtility.getParameter("HeadCode", request);
				if (lStrHeadCode.length() > 0) {
					lStrArrHeadCode = lStrHeadCode.split("~");
					for (int lIntCnt = 0; lIntCnt < lStrArrHeadCode.length; lIntCnt++) {
						lLstBDHeadCode.add(BigDecimal.valueOf(Long.valueOf(lStrArrHeadCode[lIntCnt].trim())));
					}
				}
				lIntTotalRecords = lObjSixPayFPArrearDAO.getSixPayFPArrearListCount(lStrNoOfInstallment, null, displayTag, lStrArrearConfigBy, lStrPPONo);
				if (lIntTotalRecords > 0) {
					lLstSixPayFPArrears = lObjSixPayFPArrearDAO.getSixPayFPArrearList(lStrNoOfInstallment, null, displayTag, lStrArrearConfigBy, lStrPPONo);
				} else {
					lStrNoValidPpoMsg = lStrPPONo + " is not a valid pension case for six pay arrear or selected installment no is already configured for this ppo no .";
				}
			}
			inputMap.put("totalRecords", lIntTotalRecords);
			inputMap.put("lLstSixPayFPArrears", lLstSixPayFPArrears);
			inputMap.put("lLstMonths", lLstMonths);
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("lStrCurrMonth", lStrCurrMonth);
			inputMap.put("lStrCurrYear", lStrCurrYear);
			inputMap.put("lLstInstallments", lLstInstallments);
			inputMap.put("lLstHeadCodes", lLstHeadCodes);
			inputMap.put("lLstStateDept", lLstStateDept);
			inputMap.put("NoOfHeadCodes", lLstHeadCodes.size());
			inputMap.put("arrearConfigBy", lStrArrearConfigBy);
			inputMap.put("ppoNo", lStrPPONo);
			inputMap.put("installmentNo", lStrNoOfInstallment);
			inputMap.put("noValidPpoMsg", lStrNoValidPpoMsg);
			inputMap.put("lLstTreasury", lLstAllTreasury);
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);
			objRes.setViewName("SixPayFPArrearConfig");
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

	public ResultObject saveSixPayArrearConfig(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrStateDeptCode = null;
		String lStrPayInMonth = null;
		String lStrPayInYear = null;
		String lStrPPONo = null;
		String lStrArrearConfigBy = null;
		String lStrInstlNo = null;
		SixPayFPArrearDAO lObjSixPayFPArrearDAO = null;
		String[] lArrStrStateDeptCode = null;
		List<BigDecimal> lLstStateDeptCode = new ArrayList<BigDecimal>();
		List lLstAllArrearIds = null;
		List<Long> lLstBatchArrearIds = new ArrayList<Long>();
		String lStrPayInYearMonth = null;
		int maxBatchSize = 900;
		StringBuilder lSBResp = new StringBuilder();
		String lStrArrearId = null;
		String lStrLocationCode = null;
		try {
			setSessionInfo(inputMap);
			lObjSixPayFPArrearDAO = new SixPayFPArrearDAOImpl(TrnPensionSixpayfpArrear.class, serv.getSessionFactory());
			lStrStateDeptCode = StringUtility.getParameter("selctedStateDeptCode", request).trim();
			lStrPayInMonth = StringUtility.getParameter("cmbPayinMonth", request).trim();
			lStrPayInYear = StringUtility.getParameter("cmbPayinYear", request).trim();
			lStrPPONo = StringUtility.getParameter("ppoNo", request).trim();
			lStrInstlNo = StringUtility.getParameter("cmbInstallments", request).trim();
			lStrArrearConfigBy = StringUtility.getParameter("arrearConfigBy", request).trim();
			lStrArrearId = StringUtility.getParameter("arrearId", request).trim();
			lStrLocationCode = StringUtility.getParameter("locCode", request).trim();
			lStrPayInYearMonth = lStrPayInYear + lStrPayInMonth;

			// --Updating payin month-year state/deptwise
			if ("S".equals(lStrArrearConfigBy)) {
				lArrStrStateDeptCode = lStrStateDeptCode.split("~");
				for (int i = 0; i < lArrStrStateDeptCode.length; i++) {
					lLstStateDeptCode.add(new BigDecimal(Long.valueOf(lArrStrStateDeptCode[i])));
				}
				// lLstAllArrearIds =
				// lObjSixPayFPArrearDAO.getSixPayFPArrearList(lStrInstlNo,
				// lLstStateDeptCode, null, lStrArrearConfigBy, lStrPPONo);
				lObjSixPayFPArrearDAO.updateSixPayArrearInstlmentDate(lStrPayInYearMonth, lStrLocationCode, lLstStateDeptCode, new BigDecimal(gLngUserId), new BigDecimal(gLngPostId), gDtCurr);
				// ---If total arrearids are more than 900 then Creating batch
				// of 900 records for updates
				/*
				 * if (lLstAllArrearIds.size() > maxBatchSize) { int totalBatch
				 * = lLstAllArrearIds.size() / maxBatchSize; for (int cnt = 1;
				 * cnt <= totalBatch; cnt++) { lLstBatchArrearIds = new
				 * ArrayList<Long>();
				 * lLstBatchArrearIds.addAll(lLstAllArrearIds.
				 * subList(maxBatchSize * (cnt - 1), (maxBatchSize * cnt)));
				 * lObjSixPayFPArrearDAO
				 * .updatePayInMonthByArrearIds(lLstBatchArrearIds,
				 * lStrPayInYearMonth, new BigDecimal(gLngUserId), new
				 * BigDecimal(gLngPostId), gDtCurr); } int remainingArrearIds =
				 * lLstAllArrearIds.size() % maxBatchSize; if
				 * (remainingArrearIds > 0) { lLstBatchArrearIds = new
				 * ArrayList<Long>();
				 * lLstBatchArrearIds.addAll(lLstAllArrearIds.
				 * subList(maxBatchSize * totalBatch, (maxBatchSize *
				 * totalBatch) + remainingArrearIds));
				 * lObjSixPayFPArrearDAO.updatePayInMonthByArrearIds
				 * (lLstBatchArrearIds, lStrPayInYearMonth, new
				 * BigDecimal(gLngUserId), new BigDecimal(gLngPostId), gDtCurr);
				 * } } else { lLstBatchArrearIds.addAll(lLstAllArrearIds); if
				 * (lLstBatchArrearIds.size() > 0) {
				 * lObjSixPayFPArrearDAO.updatePayInMonthByArrearIds
				 * (lLstBatchArrearIds, lStrPayInYearMonth, new
				 * BigDecimal(gLngUserId), new BigDecimal(gLngPostId), gDtCurr);
				 * } }
				 */
			}
			// --Updating payin month-year pponowise.
			else {
				if (!"".equals(lStrArrearId)) {
					lLstBatchArrearIds = new ArrayList<Long>();
					lLstBatchArrearIds.add(Long.valueOf(lStrArrearId));
					lObjSixPayFPArrearDAO.updatePayInMonthByArrearIds(lLstBatchArrearIds, lStrPayInYearMonth, new BigDecimal(gLngUserId), new BigDecimal(gLngPostId), gDtCurr);
				}
			}

			if ("S".equals(lStrArrearConfigBy) || (lLstBatchArrearIds != null && lLstBatchArrearIds.size() > 0)) {
				lSBResp.append("<XMLDOC>");
				lSBResp.append("<MESSAGECODE>");
				lSBResp.append("Config");
				lSBResp.append("</MESSAGECODE>");
				lSBResp.append("</XMLDOC>");
			} else {
				lSBResp.append("<XMLDOC>");
				lSBResp.append("<MESSAGECODE>");
				lSBResp.append("NoUpdate");
				lSBResp.append("</MESSAGECODE>");
				lSBResp.append("</XMLDOC>");
			}
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBResp.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

	public ResultObject updateSixpayArrearDtls(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		try {
			setSessionInfo(inputMap);
			SixPayFPArrearDAO lObjSixPayFPArrearDAO = new SixPayFPArrearDAOImpl(TrnPensionSixpayfpArrear.class, serv.getSessionFactory());
			SixPayFPArrearDAO lObjSixPayFPArrearDAOHst = new SixPayFPArrearDAOImpl(HstTrnPensionSixpayfpArrear.class, serv.getSessionFactory());
			TrnPensionSixpayfpArrear lObjTrnPensionSixpayfpArrear = null;
			HstTrnPensionSixpayfpArrear lObjHstTrnPensionSixpayfpArrear = null;
			String lStrPayInMonth = "";
			Long lLngHstArrearId = null;
			Long lLngRevision = null;
			int lIntCnt = 0;

			String[] lArrStrMonth = StringUtility.getParameterValues("cmbPayinMonth", request);
			String[] lArrStrYear = StringUtility.getParameterValues("cmbPayinYear", request);
			String[] lArrStrTablePk = StringUtility.getParameterValues("tablePk", request);

			String[] lArrStrMonthCash = StringUtility.getParameterValues("cmbCashPayInMonth", request);
			String[] lArrStrYearCash = StringUtility.getParameterValues("cmbCashPayInYear", request);
			String[] lArrStrAmount = StringUtility.getParameterValues("txtCashAmount", request);
			String[] lArrStrTablePkCash = StringUtility.getParameterValues("hdnArrearIdPk", request);
			String lStrPensionerCode = StringUtility.getParameter("txtPnsnrCode", request);

			lLngRevision = lObjSixPayFPArrearDAO.getMaxRevision(lStrPensionerCode);

			while (!lArrStrMonth[lIntCnt].equals("-1")) {
				lStrPayInMonth = (lArrStrMonth[lIntCnt]).trim();
				if (Integer.parseInt(lStrPayInMonth) < 10) {
					lStrPayInMonth = "0" + lStrPayInMonth;
				}

				lObjTrnPensionSixpayfpArrear = new TrnPensionSixpayfpArrear();
				lObjTrnPensionSixpayfpArrear = (TrnPensionSixpayfpArrear) lObjSixPayFPArrearDAO.read(Long.parseLong(lArrStrTablePk[lIntCnt]));

				lObjHstTrnPensionSixpayfpArrear = new HstTrnPensionSixpayfpArrear();
				lLngHstArrearId = IFMSCommonServiceImpl.getNextSeqNum("HST_TRN_PENSION_SIXPAYFP_ARREAR", inputMap);
				lObjHstTrnPensionSixpayfpArrear.setArrearId(lLngHstArrearId);

				if (lObjTrnPensionSixpayfpArrear.getPensionerCode() != null) {
					lObjHstTrnPensionSixpayfpArrear.setPensionerCode(lObjTrnPensionSixpayfpArrear.getPensionerCode());
				}

				if (lObjTrnPensionSixpayfpArrear.getPayInMonth() != null) {
					lObjHstTrnPensionSixpayfpArrear.setPayInMonth(lObjTrnPensionSixpayfpArrear.getPayInMonth());
				}

				if (lObjTrnPensionSixpayfpArrear.getRemarks() != null) {
					lObjHstTrnPensionSixpayfpArrear.setRemarks(lObjTrnPensionSixpayfpArrear.getRemarks());
				}

				if (lObjTrnPensionSixpayfpArrear.getDiffAmnt() != null) {
					lObjHstTrnPensionSixpayfpArrear.setDiffAmnt(lObjTrnPensionSixpayfpArrear.getDiffAmnt());
				}

				lObjHstTrnPensionSixpayfpArrear.setArrearType(lObjTrnPensionSixpayfpArrear.getArrearType());
				lObjHstTrnPensionSixpayfpArrear.setInstallmentNo(lObjTrnPensionSixpayfpArrear.getInstallmentNo());
				lObjHstTrnPensionSixpayfpArrear.setInstallmentAmnt(lObjTrnPensionSixpayfpArrear.getInstallmentAmnt());
				lObjHstTrnPensionSixpayfpArrear.setPaidFlag(lObjTrnPensionSixpayfpArrear.getPaidFlag());
				lObjHstTrnPensionSixpayfpArrear.setActiveFlag(lObjTrnPensionSixpayfpArrear.getActiveFlag());
				lObjHstTrnPensionSixpayfpArrear.setRevisionCounter(lObjTrnPensionSixpayfpArrear.getRevisionCounter());
				lObjHstTrnPensionSixpayfpArrear.setCreatedDate(DBUtility.getCurrentDateFromDB());
				lObjHstTrnPensionSixpayfpArrear.setCreatedPostId(BigDecimal.valueOf(gLngPostId));
				lObjHstTrnPensionSixpayfpArrear.setCreatedUserId(BigDecimal.valueOf(gLngUserId));
				lObjHstTrnPensionSixpayfpArrear.setRevision(lLngRevision);

				lObjSixPayFPArrearDAOHst.create(lObjHstTrnPensionSixpayfpArrear);

				if (lArrStrYear[lIntCnt].trim().length() > 3) {
					lObjTrnPensionSixpayfpArrear.setPayInMonth(((lArrStrYear[lIntCnt].trim()).concat(lStrPayInMonth)));
				} else {
					lObjTrnPensionSixpayfpArrear.setPayInMonth(null);
				}
				lObjTrnPensionSixpayfpArrear.setUpdatedDate(DBUtility.getCurrentDateFromDB());
				lObjTrnPensionSixpayfpArrear.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
				lObjTrnPensionSixpayfpArrear.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
				lObjSixPayFPArrearDAO.update(lObjTrnPensionSixpayfpArrear);

				if(lIntCnt < 4){
					lIntCnt++;
				}else{
					break;
				}
			}

			for (int lIntCntPay = 0; lIntCntPay < lArrStrMonthCash.length; lIntCntPay++) {
				lStrPayInMonth = (lArrStrMonthCash[lIntCntPay]).trim();
				if (Integer.parseInt(lStrPayInMonth) < 10) {
					lStrPayInMonth = "0" + lStrPayInMonth;
				}

				lObjTrnPensionSixpayfpArrear = new TrnPensionSixpayfpArrear();
				lObjTrnPensionSixpayfpArrear = (TrnPensionSixpayfpArrear) lObjSixPayFPArrearDAO.read(Long.parseLong(lArrStrTablePkCash[lIntCntPay]));

				if (lArrStrYearCash[lIntCntPay].trim().length() > 3) {
					lObjTrnPensionSixpayfpArrear.setPayInMonth(((lArrStrYearCash[lIntCntPay].trim()).concat(lStrPayInMonth)));
				} else {
					lObjTrnPensionSixpayfpArrear.setPayInMonth(null);
				}
				lObjTrnPensionSixpayfpArrear.setInstallmentAmnt(BigDecimal.valueOf(Math.round(Double.parseDouble(lArrStrAmount[lIntCntPay]))));
				lObjSixPayFPArrearDAO.update(lObjTrnPensionSixpayfpArrear);
			}

			StringBuilder lStrBldXML = getResponseXMLDocUpdate("Y");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, objRes, e, "Error in updateSixpayArrearDtls");
		}
		return objRes;
	}

	private StringBuilder getResponseXMLDocUpdate(String lStrRes) {

		StringBuilder lStrHidPKs = new StringBuilder();

		lStrHidPKs.append("<XMLDOC>");
		lStrHidPKs.append("<MESSAGECODE>");
		lStrHidPKs.append(lStrRes);
		lStrHidPKs.append("</MESSAGECODE>");
		lStrHidPKs.append("</XMLDOC>");

		return lStrHidPKs;
	}
	
	public ResultObject validatePpoWithSupplReqBeforeRevision(Map inputMap) {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrRequestNo = "";
		StringBuilder lStrBldXML = new StringBuilder();
		List<Short> lLstStatus = new ArrayList<Short>();
		try {
			setSessionInfo(inputMap);
			String lStrPensionerCode = StringUtility.getParameter("pensionerCode", request);
					
			SixPayFPArrearDAO lObjSixPayFPArrearDAO = new SixPayFPArrearDAOImpl(TrnPensionSixpayfpArrear.class, serv.getSessionFactory());
			lLstStatus.add(DBConstants.SUPPL_REQ_GENERATED);
			lLstStatus.add(DBConstants.SUPPL_BILL_UPDATED);
			lLstStatus.add(DBConstants.ST_BILL_CREATED);
			lLstStatus.add(DBConstants.ST_BILL_FORW_TO_ATO);
			lLstStatus.add(DBConstants.ST_BILL_REJECTED);
			lLstStatus.add(DBConstants.ST_BILL_DISCARD);
			lLstStatus.add(DBConstants.ST_BILL_ARCHEIVED);
					
			lStrRequestNo = lObjSixPayFPArrearDAO.getSupplRequestNoFromPnsnrCode(lStrPensionerCode, lLstStatus, gStrLocationCode);
			
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<ISEXISTS>");
			if (!"".equals(lStrRequestNo)) {
				lStrBldXML.append("Y");
			} else {
				lStrBldXML.append("N");
			}
			lStrBldXML.append("</ISEXISTS>");
			lStrBldXML.append("<REQUESTNO>" + lStrRequestNo);
			lStrBldXML.append("</REQUESTNO>");
			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
			
			
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			resObj.setThrowable(e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}
}
