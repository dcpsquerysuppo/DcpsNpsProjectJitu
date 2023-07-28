package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttdocMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttdocMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAO;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.report.CommonReportDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.wf.appwfinterface.OrgUtilityImpl;
import com.tcs.sgv.wf.valueobject.WfDocMvmntMstVO;
import com.tcs.sgv.wf.valueobject.WorkFlowVO;


/**
 * Common Pension Service Implementation
 * 
 * @author Soumya Adhikari
 * @version 1.0
 */
public class CommonPensionServiceImpl extends ServiceImpl implements CommonPensionService {

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for DB Id */
	Long gLngDBId = null;

	/* Global Variable for Logger Class */
	Log gLogger = LogFactory.getLog(getClass());

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Glonal Variable for Location Code */
	String gStrLocCode = null;

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private static ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");

	/**
	 * Shows Auditor Page....in which he/she can select specific details &
	 * generate the certificate
	 * 
	 * @param Map
	 *            :inputMap
	 * @return ResultObject
	 */
	public ResultObject getAuditorBankCodeList(Map<String, Object> inputMap) {

		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);
		// BigDecimal lBDUserId = (new BigDecimal(gLngUserId));
		// Long lLngPostId = gLngPostId));
		List lLstAuditorBankCodeList = new ArrayList();
		try {
			setSessionInfo(inputMap);
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(servLoc.getSessionFactory());
			/*
			 * lLstAuditorBankCodeList =
			 * lObjCommonPensionDAO.getAuditorBankCodeList
			 * (gLngPostId,gStrLocCode); ArrayList<ComboValuesVO> arrBankCode =
			 * new ArrayList<ComboValuesVO>(); if (lLstAuditorBankCodeList !=
			 * null && !lLstAuditorBankCodeList.isEmpty()) { Iterator it =
			 * lLstAuditorBankCodeList.iterator(); while (it.hasNext()) {
			 * ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
			 * lObjComboValuesVO = (ComboValuesVO) it.next();
			 * arrBankCode.add(lObjComboValuesVO); } }
			 * inputMap.put("ListAuditorBankCode", arrBankCode);
			 */
			List lLstPnsnHeadCode = lObjCommonPensionDAO.getAllHeadCode();
			inputMap.put("listHeadCode", lLstPnsnHeadCode);
			resObj.setResultValue(inputMap);
			resObj.setViewName("lifecertificate");
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	/**
	 * Returns Branch Codes for one bank....(On AJAX)
	 * 
	 * @param Map
	 *            :inputMap
	 * @return ResultObject
	 */
	public ResultObject getAuditorBranchInfo(Map<String, Object> inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrAuditorBankCode = null;
		setSessionInfo(inputMap);

		List lLstAuditorBranchCodeList = new ArrayList();

		// BigDecimal lBDUserId = (new BigDecimal(gLngUserId));
		// BigDecimal lBDPostId = (new BigDecimal(gLngPostId));

		try {
			lStrAuditorBankCode = StringUtility.getParameter("AuditorBankCode", request);
			String lStrAuditor = StringUtility.getParameter("cmbAuditor", request).trim();
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());

			lLstAuditorBranchCodeList = lObjCommonPensionDAO.getAuditorBranchCodeList(gLngPostId, lStrAuditorBankCode, gStrLocCode);

			ArrayList<ComboValuesVO> arrBranchCode = new ArrayList<ComboValuesVO>();
			ComboValuesVO vo = new ComboValuesVO();

			if (lLstAuditorBranchCodeList != null && !lLstAuditorBranchCodeList.isEmpty()) {
				Iterator it = lLstAuditorBranchCodeList.iterator();
				while (it.hasNext()) {
					vo = new ComboValuesVO();
					vo = (ComboValuesVO) it.next();
					arrBranchCode.add(vo);
				}
			}
			String lStrAjaxResult = new AjaxXmlBuilder().addItems(arrBranchCode, "desc", "id").toString();
			inputMap.put("ajaxKey", lStrAjaxResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	/**
	 * Returns All Distinct Head Code descriptions ...(On AJAX)
	 * 
	 * @param Map
	 *            :inputMap
	 * @return ResultObject09/02/1999
	 */
	public ResultObject getHeadCodeDesc(Map<String, Object> inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		StringBuilder lStrData = new StringBuilder();
		String lStrHeadCode = null;
		try {
			lStrHeadCode = StringUtility.getParameter("HeadCode", request);
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			if (!"Y".equalsIgnoreCase(StringUtility.getParameter("isForAdp", request))) {
				lStrData.append("<XMLDOC>");
				// ArrayList PnsnHeadCodeDesc = (ArrayList)
				// lObjCommonPensionDAO.getAllHeadCodeDesc(lStrHeadCode,
				// SessionHelper.getLangId(inputMap));

				String strPnsnHeadCodeDesc = lObjCommonPensionDAO.getAllHeadCodeDesc(lStrHeadCode, SessionHelper.getLangId(inputMap));
				if (strPnsnHeadCodeDesc != null) {
					strPnsnHeadCodeDesc = strPnsnHeadCodeDesc.replace('&', '~');
				}
				List lLstRes = lObjCommonPensionDAO.getBillTypeFromHeadCode(new BigDecimal(lStrHeadCode));
				lStrData.append("<HEADDESC>");
				lStrData.append(strPnsnHeadCodeDesc);
				lStrData.append("</HEADDESC>");
				lStrData.append("<DCRG>");
				if (lLstRes.contains("DCRG")) {
					lStrData.append("Y");
				} else {
					lStrData.append("N");
				}
				lStrData.append("</DCRG>");
				lStrData.append("<Pension>");
				if (lLstRes.contains("PENSION")) {
					lStrData.append("Y");
				} else {
					lStrData.append("N");
				}
				lStrData.append("</Pension>");
				lStrData.append("<CVP>");
				if (lLstRes.contains("CVP")) {
					lStrData.append("Y");
				} else {
					lStrData.append("N");
				}
				lStrData.append("</CVP>");
				if (StringUtility.getParameter("isFromCase", request) != null && "Y".equalsIgnoreCase(StringUtility.getParameter("isFromCase", request))) {
					String lStrIRFlag = null;
					String lStrSpclCase = null;
					String lStrRop = null;
					String lStrBasic = null;
					String lstrRetDate = null;
					String lStrTIFlag = null;
					String lStrDPFlag = null;
					String lStrMAFlag = null;
					BigDecimal lLngBasicAmt = new BigDecimal(0);
					BigDecimal TiAmnt = new BigDecimal(0);
					BigDecimal IRAmnt = new BigDecimal(0);
					if (StringUtility.getParameter("isRop", request) != null && StringUtility.getParameter("isRop", request).length() > 0) {
						if (!request.getParameter("isRop").equals("-1")) {
							lStrRop = StringUtility.getParameter("isRop", request).toString();
						}
					}
					if (StringUtility.getParameter("SpecialCase", request) != null && StringUtility.getParameter("SpecialCase", request).length() > 0) {
						lStrSpclCase = StringUtility.getParameter("SpecialCase", request).toString();
					}

					if (StringUtility.getParameter("BasicPension", request) != null && StringUtility.getParameter("BasicPension", request).length() > 0) {
						lStrBasic = StringUtility.getParameter("BasicPension", request);
						lLngBasicAmt = new BigDecimal(lStrBasic);

						if (lLngBasicAmt.doubleValue() > 0 && lLngBasicAmt.doubleValue() <= 1750) {
							lLngBasicAmt = new BigDecimal(1750);
						} else if (lLngBasicAmt.doubleValue() > 1750 && lLngBasicAmt.doubleValue() <= 3000) {
							lLngBasicAmt = new BigDecimal(3000);
						} else {
							lLngBasicAmt = new BigDecimal(999999);
						}
					}
					lstrRetDate = new SimpleDateFormat("dd-MMM-yyyy").format(new Date());
					lStrMAFlag = "MA";
					lStrIRFlag = "IR";
					if (lStrSpclCase != null && lStrRop != null) {
						if ("Y".equals(lStrRop) || "P".equals(lStrRop)) {
							lStrRop = "Y";
						}
						if (lStrRop.equalsIgnoreCase("Y") && lStrSpclCase.equalsIgnoreCase("N")) {
							lStrTIFlag = "STI";
							lStrDPFlag = "DP";
						}
						if (lStrRop.equalsIgnoreCase("Y") && lStrSpclCase.equalsIgnoreCase("Y")) {
							lStrTIFlag = "RTI";
						}
						if (lStrRop.equalsIgnoreCase("N") && lStrBasic != null) {
							lStrTIFlag = "TI";
						}

						lStrRop = StringUtility.getParameter("ROP1986", request);
						String lStrRop96 = StringUtility.getParameter("ROP1996", request);
						if ("Y".equalsIgnoreCase(lStrRop) && !"Y".equals(lStrRop96)) {
							lStrTIFlag = "TI";
							lStrDPFlag = null;
						}
						if ("Y".equals(lStrRop96) && "Y".equals(lStrRop)) {
							if (lStrSpclCase.equalsIgnoreCase("N")) {
								lStrTIFlag = "STI";
								lStrDPFlag = "DP";
							}
							if (lStrSpclCase.equalsIgnoreCase("Y")) {
								lStrTIFlag = "RTI";
							}
						}
						lStrRop = StringUtility.getParameter("ROP2006", request);
						if ("Y".equalsIgnoreCase(lStrRop)) {
							lStrTIFlag = "TI_06";
							lStrDPFlag = null;
						}

					}
					// List lLstResValue =
					// lObjCommonPensionDAO.getTiAndMdeAllowFromByHeadCode(lStrHeadCode);
					BigDecimal TiRate = new BigDecimal(0);
					if (Integer.parseInt(lStrHeadCode) == 16 || Integer.parseInt(lStrHeadCode) == 17 || Integer.parseInt(lStrHeadCode) == 19 || Integer.parseInt(lStrHeadCode) == 18) {
						lStrBasic = StringUtility.getParameter("BasicPension", request);
						List lLstSp = lObjCommonPensionDAO.getTiRateForSpecialHeadCode(lStrHeadCode, lstrRetDate, lStrBasic);
						if (lLstSp != null && !lLstSp.isEmpty()) {
							if (lLstSp.get(0) != null) {
								TiAmnt = new BigDecimal(lLstSp.get(0).toString());
							}
						}
						lStrData.append("<TIRate>");
						lStrData.append(0);
						lStrData.append("</TIRate>");
						lStrData.append("<TIAmt>");
						lStrData.append(TiAmnt);
						lStrData.append("</TIAmt>");

						BigDecimal MAAmnt = new BigDecimal(0);
						if (lStrMAFlag != null) {
							List llstRateElem = lObjCommonPensionDAO.getTiAndMdeAllowFromByHeadCode(lStrHeadCode, lstrRetDate, lStrMAFlag, null);
							if (llstRateElem != null && !llstRateElem.isEmpty()) {
								if (llstRateElem.get(1) != null) {
									MAAmnt = new BigDecimal(llstRateElem.get(1).toString());
								}
							}
						}
						lStrData.append("<MAAmt>");
						lStrData.append(MAAmnt);
						lStrData.append("</MAAmt>");

					} else {
						if (lStrTIFlag != null) {

							List llstRateElem = new ArrayList();
							if (lStrTIFlag.equals("TI")) {
								llstRateElem = lObjCommonPensionDAO.getTiAndMdeAllowFromByHeadCode(lStrHeadCode, lstrRetDate, lStrTIFlag, lLngBasicAmt.toString());
							} else {
								llstRateElem = lObjCommonPensionDAO.getTiAndMdeAllowFromByHeadCode(lStrHeadCode, lstrRetDate, lStrTIFlag, null);
							}

							if (llstRateElem != null && !llstRateElem.isEmpty()) {
								if (llstRateElem.get(0) != null) {
									TiRate = new BigDecimal(llstRateElem.get(0).toString());
								}
								if (llstRateElem.get(1) != null) {
									TiAmnt = new BigDecimal(llstRateElem.get(1).toString());
								}

							}
						}
						lStrData.append("<TIRate>");
						lStrData.append(TiRate);
						lStrData.append("</TIRate>");
						lStrData.append("<TIAmt>");
						lStrData.append(TiAmnt);
						lStrData.append("</TIAmt>");
						BigDecimal MAAmnt = new BigDecimal(0);
						if (lStrMAFlag != null) {
							List llstRateElem = lObjCommonPensionDAO.getTiAndMdeAllowFromByHeadCode(lStrHeadCode, lstrRetDate, lStrMAFlag, null);
							if (llstRateElem != null && !llstRateElem.isEmpty()) {
								if (llstRateElem.get(1) != null) {
									MAAmnt = new BigDecimal(llstRateElem.get(1).toString());
								}
							}
						}
						lStrData.append("<MAAmt>");
						lStrData.append(MAAmnt);
						lStrData.append("</MAAmt>");
						BigDecimal DpRate = new BigDecimal(0);
						if (lStrDPFlag != null) {
							List llstRateElem = lObjCommonPensionDAO.getTiAndMdeAllowFromByHeadCode(lStrHeadCode, lstrRetDate, lStrDPFlag, null);

							if (llstRateElem != null && !llstRateElem.isEmpty()) {
								if (llstRateElem.get(0) != null) {
									DpRate = new BigDecimal(llstRateElem.get(0).toString());
								}
							}
						}
						lStrData.append("<DPRate>");
						lStrData.append(DpRate);
						lStrData.append("</DPRate>");
						BigDecimal IRRate = new BigDecimal(0);
						if (lStrIRFlag != null) {
							List llstRateElem = lObjCommonPensionDAO.getTiAndMdeAllowFromByHeadCode(lStrHeadCode, lstrRetDate, lStrIRFlag, null);
							if (llstRateElem != null && !llstRateElem.isEmpty()) {
								if (llstRateElem.get(1) != null) {
									IRAmnt = new BigDecimal(llstRateElem.get(1).toString());
								}
								if (llstRateElem.get(0) != null) {
									IRRate = new BigDecimal(llstRateElem.get(0).toString());
								}
							}
						}

						lStrData.append("<IRRate>");
						lStrData.append(IRRate);
						lStrData.append("</IRRate>");
						lStrData.append("<IRAmt>");
						lStrData.append(IRAmnt);
						lStrData.append("</IRAmt>");
					}
				}
				lStrData.append("</XMLDOC>");
			}
			if (StringUtility.getParameter("txtBasicPensionAmnt", request).length() > 0 && ("Y".equalsIgnoreCase(StringUtility.getParameter("ROP2006", request)))) {
				lStrData.append("<ADP>");
				lStrData.append("<ADP2>");
				lStrData.append(getAdpForPensionerBasedOnAge(inputMap));
				lStrData.append("</ADP2>");
				lStrData.append("</ADP>");
			}

			String lStrAjaxResult = new AjaxXmlBuilder().addItem("ajax_key", lStrData.toString()).toString();
			inputMap.put("ajaxKey", lStrAjaxResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject getDistricts(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			setSessionInfo(inputMap);
			ArrayList arrylst = new ArrayList();
			String strStateId = StringUtility.getParameter("state", request);
			BigDecimal stateId = (BigDecimal) inputMap.get("state");
			String strAjaxResult = null;
			CommonPensionDAO commonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());

			if (strStateId != null && strStateId.length() > 0) {
				arrylst = (ArrayList) commonPensionDAO.getDistrictsOfState(new BigDecimal(strStateId), gLngLangId);
				strAjaxResult = new AjaxXmlBuilder().addItems(arrylst, "desc", "id").toString();
				inputMap.put("ajaxKey", strAjaxResult);
				resObj.setViewName("ajaxData");
			} else if (stateId != null) {
				arrylst = (ArrayList) commonPensionDAO.getDistrictsOfState(stateId, gLngLangId);
				inputMap.put("listDistricts", arrylst);
			}
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject getBranch(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			setSessionInfo(inputMap);
			CommonPensionDAO commonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			ArrayList arrylstBranch = new ArrayList();
			String bankCodeFrmRequest = StringUtility.getParameter("bank", request);
			String bankCodeFrmMap = (String) inputMap.get("bank");

			if (bankCodeFrmRequest != null && bankCodeFrmRequest.length() > 0) {
				arrylstBranch = (ArrayList) commonPensionDAO.getBranchsOfBank(bankCodeFrmRequest, gLngLangId, SessionHelper.getLocationCode(inputMap));
				String strAjaxResult = new AjaxXmlBuilder().addItems(arrylstBranch, "desc", "id").toString();
				inputMap.put("ajaxKey", strAjaxResult);
				resObj.setViewName("ajaxData");
			} else if (bankCodeFrmMap != null && bankCodeFrmMap.length() > 0) {
				arrylstBranch = (ArrayList) commonPensionDAO.getBranchsOfBank(bankCodeFrmMap, gLngLangId, SessionHelper.getLocationCode(inputMap));
				inputMap.put("listBranch", arrylstBranch);
			}
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject getBranchAdd(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			CommonPensionDAO commonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			List arrylstBranch = new ArrayList();
			StringBuilder lStrRes = new StringBuilder();
			String bankCodeFrmRequest = StringUtility.getParameter("txtbranchCode", request);
			String locCode = SessionHelper.getLocationCode(inputMap);
			String lStrLocId = StringUtility.getParameter("cmbTreasury", request);
			if (lStrLocId != null && lStrLocId.length() > 0) {
				locCode = lStrLocId;
			} // Deep -- Need to check why chandru has added for sub-treasury
				// functionality

			if (bankCodeFrmRequest.trim().length() > 0) {
				arrylstBranch = commonPensionDAO.getBranchByBranchId(bankCodeFrmRequest, SessionHelper.getLangId(inputMap), locCode);
			}
			lStrRes.append("<XMLDOC>");
			if (arrylstBranch != null && !arrylstBranch.isEmpty()) {
				if (arrylstBranch.get(0) != null) {
					lStrRes.append("<BANKCODE>" + arrylstBranch.get(0).toString());
					lStrRes.append("</BANKCODE>");
				}
				if (arrylstBranch.get(1) != null) {
					lStrRes.append("<BRANCHCODE>" + arrylstBranch.get(1).toString());
					lStrRes.append("</BRANCHCODE>");
				}
				if (arrylstBranch.get(2) != null) {
					lStrRes.append("<BRANCHNAME>" + arrylstBranch.get(2).toString());
					lStrRes.append("</BRANCHNAME>");
				}
				if (arrylstBranch.get(3) != null) {
					lStrRes.append("<BANKNAME>" + arrylstBranch.get(3).toString());
					lStrRes.append("</BANKNAME>");
				}
			}
			lStrRes.append("</XMLDOC>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrRes.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject getBranchForRqstSearch(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		Object[] tuple;
		Iterator itr;
		try {
			CommonPensionDAO commonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			List arrylstBranch = new ArrayList();
			StringBuilder lStrRes = new StringBuilder();
			String bankCodeFrmRequest = StringUtility.getParameter("txtbranchCode", request);
			if ("".equals(bankCodeFrmRequest)) {
				bankCodeFrmRequest = "-1";
			}
			if (bankCodeFrmRequest.trim().length() > 0) {
				arrylstBranch = commonPensionDAO.getAllBank(bankCodeFrmRequest, SessionHelper.getLangId(inputMap), SessionHelper.getLocationCode(inputMap));
			}
			lStrRes.append("<XMLDOCRQST>");
			if (!arrylstBranch.isEmpty()) {
				itr = arrylstBranch.iterator();
				while (itr.hasNext()) {
					tuple = (Object[]) itr.next();
					if (tuple[0] != null) {
						lStrRes.append("<BANKCODE>" + tuple[0].toString());
						lStrRes.append("</BANKCODE>");
					}
					if (tuple[1] != null) {
						lStrRes.append("<BRANCHCODE>" + tuple[2].toString());
						lStrRes.append("</BRANCHCODE>");
					}
					if (tuple[2] != null) {
						lStrRes.append("<BRANCHNAME>" + tuple[4].toString());
						lStrRes.append("</BRANCHNAME>");
					}
				}
			}
			lStrRes.append("</XMLDOCRQST>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrRes.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject getSubTreasury(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		try {
			setSessionInfo(inputMap);
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
			CommonPensionDAO CommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			String strlocID = StringUtility.getParameter("treasury", request);
			String lLngLocCode = CommonPensionDAO.getLocCodeFromRltPPOTrsryMapping(strlocID);
			if (lLngLocCode != null && lLngLocCode != "") {
				strlocID = lLngLocCode.toString();
			}
			ArrayList ArrLstSubTreausry = (ArrayList) CommonPensionDAO.getSubTreasurysOfTreasury(strlocID, gLngLangId);
			String strAjaxResult = new AjaxXmlBuilder().addItems(ArrLstSubTreausry, "desc", "id").toString();
			inputMap.put("ajaxKey", strAjaxResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject getHOD(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			setSessionInfo(inputMap);
			CommonPensionDAO commonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			String strDepCodeFrmRequest = StringUtility.getParameter("department", request);
			BigDecimal deptCodeFrmMap = (BigDecimal) inputMap.get("department");
			ArrayList arrHod = null;
			String strAjaxResult = null;
			if (strDepCodeFrmRequest != null && strDepCodeFrmRequest.length() > 0) {
				BigDecimal lBdDeptCode = new BigDecimal(strDepCodeFrmRequest);
				arrHod = (ArrayList) commonPensionDAO.getHodFromDepartmet(lBdDeptCode, gLngLangId, null);
				strAjaxResult = new AjaxXmlBuilder().addItems(arrHod, "desc", "id").toString();
				inputMap.put("ajaxKey", strAjaxResult);
				resObj.setViewName("ajaxData");
			} else if (deptCodeFrmMap != null) {
				arrHod = (ArrayList) commonPensionDAO.getHodFromDepartmet(deptCodeFrmMap, gLngLangId, null);
				inputMap.put("listHod", arrHod);
			}
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	/*
	 * public String getMyCases(Map<String,Object> inputMap) throws Exception {
	 * setSessionInfo(inputMap); WorkFlowVO workFlowVO = new WorkFlowVO();
	 * ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
	 * Connection conn =
	 * serv.getSessionFactory().getCurrentSession().connection(); String
	 * gStrLocId = SessionHelper.getLocationCode(inputMap); Long gLngDBId =
	 * SessionHelper.getDbId(inputMap); List list = new ArrayList(); List
	 * resultLst = null; List caseList = new ArrayList(); String lStrFinalString
	 * = ""; try { workFlowVO.setAppMap(inputMap);
	 * workFlowVO.setCrtEmpId(SessionHelper.getUserId(inputMap).toString());
	 * workFlowVO.setCrtPost(SessionHelper.getPostId(inputMap).toString());
	 * workFlowVO.setCrtUsr(SessionHelper.getUserId(inputMap).toString());
	 * workFlowVO.setConnection(conn);
	 * workFlowVO.setActId(DBConstants.WF_ActionId_Create);
	 * workFlowVO.setDocId(Long
	 * .parseLong(bundleConst.getString("PENSION.CASEDOCUMENTID")));
	 * workFlowVO.setLocID(gStrLocId); workFlowVO.setDbId(gLngDBId);
	 * OrgUtilityImpl orgUtil = new OrgUtilityImpl(workFlowVO); // adding the
	 * posts list.add(gLngPostId.toString()); resultLst =
	 * orgUtil.getDocList(list); if (resultLst != null && !resultLst.isEmpty())
	 * { for (int lCount = 0; lCount < resultLst.size(); lCount++) {
	 * WfDocMvmntMstVO lObjDocMvmntMst = (WfDocMvmntMstVO)
	 * resultLst.get(lCount); if (lObjDocMvmntMst.getDocId() ==
	 * Long.parseLong(bundleConst.getString("PENSION.CASEDOCUMENTID"))) {
	 * caseList.add(lObjDocMvmntMst.getJobRefId()); } } } int ChangedListSize =
	 * caseList.size(); int j = 999; String lStrnew = ""; int LsitSize =
	 * caseList.size(); StringBuffer lSb = new StringBuffer(); for(int k = 0; k
	 * < LsitSize;k++) { if(k >= j && ChangedListSize >= 999) {
	 * lSb.append("("+lStrnew+")"); lSb.append("~"); lStrnew = ""; if(j+999 <=
	 * LsitSize) { j = j+999; } ChangedListSize = ChangedListSize - 999; }
	 * lStrnew = lStrnew+caseList.get(k)+","; } if(lSb != null && lStrnew!=
	 * null) { lSb.append("("+lStrnew+")"); } lStrFinalString =
	 * lSb.toString().replace(",)", ")"); } catch (Exception e) {
	 * gLogger.error("Error is : " + e, e); throw (e); } return lStrFinalString;
	 * }
	 */

	private void setSessionInfo(Map inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gDtCurrDt = DBUtility.getCurrentDateFromDB();
		gStrLocCode = SessionHelper.getLocationCode(inputMap);
		serv = (ServiceLocator) inputMap.get("serviceLocator");
	}

	public ResultObject getSancAuthPrefix(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			setSessionInfo(inputMap);
			CommonPensionDAO commonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			String lStrSancAuthCode = StringUtility.getParameter("sancAuthCmb", request);
			String lStrPrifix = "";
			StringBuilder lStrRes = new StringBuilder();
			lStrRes.append("<XMLDOC>");
			lStrRes.append("<PreFix>");
			if (lStrSancAuthCode != null && lStrSancAuthCode.length() > 0) {
				lStrPrifix = commonPensionDAO.getPrifixSancAuth(Long.valueOf(lStrSancAuthCode), SessionHelper.getLangId(inputMap));
				lStrRes.append(lStrPrifix);
			}
			lStrRes.append("</PreFix>");
			lStrRes.append("</XMLDOC>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrRes.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}
		return resObj;
	}

	public String getMyMRCases(Map<String, Object> inputMap) throws Exception {

		setSessionInfo(inputMap);
		WorkFlowVO workFlowVO = new WorkFlowVO();
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		Connection conn = serv.getSessionFactory().getCurrentSession().connection();
		StringBuffer lStrMyCases = new StringBuffer();
		String gStrLocId = SessionHelper.getLocationCode(inputMap);
		Long gLngDBId = SessionHelper.getDbId(inputMap);
		List list = new ArrayList();
		List resultLst = null;
		List caseList = new ArrayList();
		try {
			workFlowVO.setAppMap(inputMap);
			workFlowVO.setCrtEmpId(SessionHelper.getUserId(inputMap).toString());
			workFlowVO.setCrtPost(SessionHelper.getPostId(inputMap).toString());
			workFlowVO.setCrtUsr(SessionHelper.getUserId(inputMap).toString());
			workFlowVO.setConnection(conn);
			workFlowVO.setActId(DBConstants.WF_ActionId_Create);
			workFlowVO.setDocId(Long.parseLong(bundleConst.getString("WF.DocId.MRCase")));
			workFlowVO.setLocID(gStrLocId);
			workFlowVO.setDbId(gLngDBId);
			OrgUtilityImpl orgUtil = new OrgUtilityImpl(workFlowVO);
			// adding the posts
			list.add(gLngPostId.toString());
			resultLst = orgUtil.getDocList(list);
			if (resultLst != null && !resultLst.isEmpty()) {
				for (int lCount = 0; lCount < resultLst.size(); lCount++) {
					WfDocMvmntMstVO lObjDocMvmntMst = (WfDocMvmntMstVO) resultLst.get(lCount);
					if (lObjDocMvmntMst.getDocId() == Long.parseLong(bundleConst.getString("WF.DocId.MRCase"))) {
						caseList.add(lObjDocMvmntMst.getJobRefId());
					}
				}
			}
			Iterator lItr = caseList.iterator();
			lStrMyCases.append("(");
			String lStrMyCases1;
			while (lItr.hasNext()) {
				lStrMyCases1 = (String) lItr.next();

				lStrMyCases.append("'" + lStrMyCases1 + "'");

				if (lItr.hasNext()) {
					lStrMyCases.append(",");
				}
			}
			lStrMyCases.append(")");
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lStrMyCases.toString();
	}

	public ResultObject loadCrdxMasterScreen(Map<String, Object> argsMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		List listDesignation = null;
		List lLstCmbSanAuth = null;
		try {
			setSessionInfo(argsMap);
			ServiceLocator serv = (ServiceLocator) argsMap.get("serviceLocator");
			HttpServletRequest request = (HttpServletRequest) argsMap.get("requestObj");
			CommonPensionDAO CommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			if (request.getParameter("srNo") != null && request.getParameter("srNo").length() > 0) {
				argsMap.put("srNo", request.getParameter("srNo"));
			}
			if (StringUtility.getParameter("stauts", request) != null && "save".equalsIgnoreCase(StringUtility.getParameter("stauts", request))) {
				savePensionCardexSign(argsMap);
				resObj.setResultValue(argsMap);
			} else if (StringUtility.getParameter("stauts", request) != null && "View".equalsIgnoreCase(StringUtility.getParameter("stauts", request))) {
				// MstPensionCardexSigndtlsDao lObjDao = new
				// MstPensionCardexSigndtlsDao(MstPensionCardexSigndtls.class,
				// serv.getSessionFactory());
				String lStrIsDeactivate = StringUtility.getParameter("hidisActive", request);
				if (lStrIsDeactivate != null && lStrIsDeactivate.length() > 0) {
					savePensionCardexSign(argsMap);
				}
				// List<SavedCardexSignVo> lLstSigns =
				// lObjDao.getSavedCrdxSigns(argsMap);
				argsMap.put("SignLsit", new ArrayList());
				resObj.setViewName("SavedCadxSigns");
				resObj.setResultValue(argsMap);
			} else if (StringUtility.getParameter("stauts", request) != null && "upDateMstScreen".equalsIgnoreCase(StringUtility.getParameter("stauts", request))) {
				listDesignation = CommonPensionDAO.getAllDesignation(null, gLngLangId);
				lLstCmbSanAuth = CommonPensionDAO.getSanctionAuthPrefix(gLngLangId);
				argsMap.put("SancAuthList", lLstCmbSanAuth);
				argsMap.put("listDesignation", listDesignation);
				String lStrId = StringUtility.getParameter("hidcrdxDtlsId", request);
				if (lStrId != null && lStrId.length() > 0) {
					/*
					 * MstPensionCardexSigndtlsDao lObjDao = new
					 * MstPensionCardexSigndtlsDao
					 * (MstPensionCardexSigndtls.class,
					 * serv.getSessionFactory()); MstPensionCardexSigndtls
					 * lObjCrdxNewVo = new MstPensionCardexSigndtls();
					 * lObjCrdxNewVo = lObjDao.read(Long.parseLong(lStrId));
					 * CmnAttachmentMstDAO mnAttachmentMstDAO = new
					 * CmnAttachmentMstDAOImpl(CmnAttachmentMst.class,
					 * serv.getSessionFactory()); CmnAttachmentMst
					 * cmnAttachmentMst =
					 * mnAttachmentMstDAO.findByAttachmentId(lObjCrdxNewVo
					 * .getCardexSignId());
					 * 
					 * argsMap.put("scan", cmnAttachmentMst);
					 * argsMap.put("MstPensionCardexSign", lObjCrdxNewVo);
					 */
				}
				resObj.setViewName("CrdxMStScreenPopup");
				resObj.setResultValue(argsMap);
			} else if (StringUtility.getParameter("stauts", request) != null && "getNamesCmb".equalsIgnoreCase(StringUtility.getParameter("stauts", request))) {
				// MstPensionCardexSigndtlsDao lObjDao = new
				// MstPensionCardexSigndtlsDao(MstPensionCardexSigndtls.class,
				// serv.getSessionFactory());
				String lStrRes = "";
				ComboValuesVO lOjCmbVo = null;
				String lStrAuthCode = StringUtility.getParameter("authCode", request);
				if (lStrAuthCode != null && lStrAuthCode.length() > 0) {
					// List<ComboValuesVO> lLstRes =
					// lObjDao.getCrdxNames(lStrAuthCode);

					/*
					 * if (lLstRes != null && !lLstRes.isEmpty()) { Iterator itr
					 * = lLstRes.iterator(); while (itr.hasNext()) { lOjCmbVo =
					 * (ComboValuesVO) itr.next(); lStrRes = lStrRes +
					 * "<option value = " + lOjCmbVo.getId() + " >" +
					 * lOjCmbVo.getDesc() + "</option>"; } }
					 */
				}
				argsMap.put("ajaxKey", lStrRes);
				resObj.setViewName("ajaxData");
				resObj.setResultValue(argsMap);
			}

			else {
				listDesignation = CommonPensionDAO.getAllDesignation(null, gLngLangId);
				lLstCmbSanAuth = CommonPensionDAO.getSanctionAuthPrefix(gLngLangId);
				argsMap.put("SancAuthList", lLstCmbSanAuth);
				argsMap.put("listDesignation", listDesignation);
				resObj.setViewName("CrdxMStScreenPopup");
				resObj.setResultValue(argsMap);
			}

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}
		return resObj;
	}

	private void savePensionCardexSign(Map<String, Object> argsMap) {

		try {
			/*
			 * ServiceLocator serv = (ServiceLocator)
			 * argsMap.get("serviceLocator"); MstPensionCardexSigndtls
			 * lObjCrdxNewVo = new MstPensionCardexSigndtls();
			 * MstPensionCardexSigndtlsDao lObjDao = new
			 * MstPensionCardexSigndtlsDao(MstPensionCardexSigndtls.class,
			 * serv.getSessionFactory()); lObjCrdxNewVo =
			 * getSignDtlsVo(argsMap); if (lObjCrdxNewVo != null &&
			 * lObjCrdxNewVo.getCardexSigndtlsId() != null) {
			 * lObjDao.update(lObjCrdxNewVo); } else { Long lLngPk =
			 * IDGenerateDelegate.getNextId("mst_pension_cardex_signdtls",
			 * argsMap); lObjCrdxNewVo.setCardexSigndtlsId(lLngPk);
			 * lObjDao.create(lObjCrdxNewVo); }
			 */
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}
	}

	public BigDecimal getAdpForPensionerBasedOnAge(Map argsMap) {

		HttpServletRequest request = (HttpServletRequest) argsMap.get("requestObj");
		BigDecimal lBDADPAmount = BigDecimal.ZERO;
		Long lLngTempDiffYear = 0L;
		BigDecimal lBDSlabPrcnt = BigDecimal.ZERO;
		try {
			String lStrDOB = StringUtility.getParameter("txtDateOfBirth", request);
			String lStrDOD = StringUtility.getParameter("txtDateOfDeath", request);
			String lStrBasic = StringUtility.getParameter("txtBasicPensionAmnt", request);
			String lStrFMDOB = StringUtility.getParameter("FMDob", request);
			BigDecimal lBDgBaisc = BigDecimal.ZERO;
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String lStrCurrDate = lObjDateFormat.format(DBUtility.getCurrentDateFromDB());
			if (lStrDOB.length() > 0 || lStrDOD.length() > 0) {
				if (lStrDOD.length() > 0 && StringUtility.getParameter("FPAmount", request).length() > 0 && lStrFMDOB.length() > 0) {
					lLngTempDiffYear = calculateAgeSlab(Integer.parseInt(lStrCurrDate.split("/")[0]), Integer.parseInt(lStrCurrDate.split("/")[1]), Integer.parseInt(lStrCurrDate.split("/")[2]),
							Integer.parseInt(lStrFMDOB.split("/")[0]), Integer.parseInt(lStrFMDOB.split("/")[1]), Integer.parseInt(lStrFMDOB.split("/")[2]));
					if (lLngTempDiffYear < 80) {
						lBDADPAmount = BigDecimal.ZERO;
					} else {
						lBDSlabPrcnt = getSlabBasedonAgeYear(lLngTempDiffYear.intValue());
						lBDgBaisc = new BigDecimal(StringUtility.getParameter("FPAmount", request));
						lBDADPAmount = lBDgBaisc.multiply(lBDSlabPrcnt).divide(new BigDecimal(100));
					}

				} else {
					lLngTempDiffYear = calculateAgeSlab(Integer.parseInt(lStrCurrDate.split("/")[0]), Integer.parseInt(lStrCurrDate.split("/")[1]), Integer.parseInt(lStrCurrDate.split("/")[2]),
							Integer.parseInt(lStrDOB.split("/")[0]), Integer.parseInt(lStrDOB.split("/")[1]), Integer.parseInt(lStrDOB.split("/")[2]));
					if (lLngTempDiffYear < 80) {
						lBDADPAmount = BigDecimal.ZERO;
					} else {
						lBDSlabPrcnt = getSlabBasedonAgeYear(lLngTempDiffYear.intValue());
						lBDgBaisc = new BigDecimal(lStrBasic);
						lBDADPAmount = lBDgBaisc.multiply(lBDSlabPrcnt).divide(new BigDecimal(100));
					}
				}

			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}
		return lBDADPAmount;
	}

	private Long calculateAgeSlab(int FRmDD, int FRmMM, int FRmYY, int ToDD, int ToMM, int ToYY) throws Exception {

		long diffDays = 0L;
		try {
			/*
			 * Calendar calendar1 = Calendar.getInstance(); Calendar calendar2 =
			 * Calendar.getInstance(); calendar2.set(FRmYY, FRmMM-1,
			 * FRmDD);//currentDate calendar1.set(ToYY, ToMM-1, ToDD); //Date of
			 * birth or fp1 date long milliseconds1 =
			 * calendar1.getTimeInMillis(); long milliseconds2 =
			 * calendar2.getTimeInMillis(); long diff = milliseconds2 -
			 * milliseconds1; diffDays = diff / (24 60 60 1000);
			 */
			int tempDifYear = FRmYY - ToYY;
			if (FRmMM < ToMM) {
				tempDifYear = tempDifYear - 1;
				diffDays = Long.valueOf(tempDifYear);
			} else {
				if (ToMM == FRmMM) {
					if (FRmDD < ToDD) {
						tempDifYear = tempDifYear - 1;
						diffDays = Long.valueOf(tempDifYear);
					} else {
						diffDays = Long.valueOf(tempDifYear);
					}
				} else {
					diffDays = Long.valueOf(tempDifYear);
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
		}
		return diffDays;
	}

	private BigDecimal getSlabBasedonAgeYear(int lIntNoOfYear) {

		BigDecimal lBDReturnVal = new BigDecimal(0);
		if (lIntNoOfYear >= 80 && lIntNoOfYear < 85) {
			lBDReturnVal = new BigDecimal(20);
		}
		if (lIntNoOfYear >= 85 && lIntNoOfYear < 90) {
			lBDReturnVal = new BigDecimal(30);
		}
		if (lIntNoOfYear >= 90 && lIntNoOfYear < 95) {
			lBDReturnVal = new BigDecimal(40);
		}
		if (lIntNoOfYear >= 90 && lIntNoOfYear < 95) {
			lBDReturnVal = new BigDecimal(40);
		}
		if (lIntNoOfYear >= 95 && lIntNoOfYear < 100) {
			lBDReturnVal = new BigDecimal(50);
		}
		if (lIntNoOfYear >= 100) {
			lBDReturnVal = new BigDecimal(100);
		}
		return lBDReturnVal;
	}

	/**
	 * Method for Autosuggest PPO No.
	 * 
	 * @author 232312
	 * @param inputMap
	 * @return Result Object containing ajax data list of suggested PPO No.s
	 */
	public ResultObject getSearchPPONo(Map<String, Object> inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			setSessionInfo(inputMap);
			List<ComboValuesVO> arrylst = new ArrayList<ComboValuesVO>();

			String lStrPPONo = null;

			String lStrPPOId = StringUtility.getParameter("dppfPPOId", request);
			if (!lStrPPOId.equals("")) {
				lStrPPONo = StringUtility.getParameter("txtPPONo" + lStrPPOId, request);
			} else {
				lStrPPONo = StringUtility.getParameter("txtPPONo", request);
			}
			String strAjaxResult = null;
			CommonPensionDAO commonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());

			if (lStrPPONo != null && lStrPPONo.length() > 0) {
				arrylst = commonPensionDAO.getPPONo(lStrPPONo, gStrLocCode);
				strAjaxResult = new AjaxXmlBuilder().addItems(arrylst, "desc", "desc").toString();
				inputMap.put("ajaxKey", strAjaxResult);
				resObj.setViewName("ajaxData");
			}

			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject getBanksOfAuditorAjax(Map<String, Object> inputMap) {

		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		StringBuilder lStrRes = new StringBuilder();

		List lLstAuditorBankCodeList = new ArrayList();
		try {
			setSessionInfo(inputMap);
			String lStrAuditorPostId = StringUtility.getParameter("lAuditor", request).trim();
			String lStrTreasuryCode = StringUtility.getParameter("treasury", request);
			if (lStrTreasuryCode == null || lStrTreasuryCode.equals("") || lStrTreasuryCode.equals("-1")) {
				lStrTreasuryCode = gStrLocCode;
			}
			Long lLngAudiPost = Long.parseLong(lStrAuditorPostId);
			// String lStrFlag = StringUtility.getParameter("lFlag", request);

			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(servLoc.getSessionFactory());

			lLstAuditorBankCodeList = lObjCommonPensionDAO.getAuditorBankCodeList(lLngAudiPost, lStrTreasuryCode);
			ArrayList<ComboValuesVO> arrBankCode = new ArrayList<ComboValuesVO>();
			ComboValuesVO lObjComboValuesVO = new ComboValuesVO();

			if (lLstAuditorBankCodeList != null && !lLstAuditorBankCodeList.isEmpty()) {
				Iterator it = lLstAuditorBankCodeList.iterator();
				while (it.hasNext()) {
					lObjComboValuesVO = (ComboValuesVO) it.next();
					arrBankCode.add(lObjComboValuesVO);
				}
			}
			String lStrResult = new AjaxXmlBuilder().addItems(arrBankCode, "desc", "id").toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject getRectifyRevisionBillData(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());

		List lBillDtlsLst = null;

		try {
			setSessionInfo(inputMap);

			String lStrPPONO = StringUtility.getParameter("txtPPONo", request);

			if (lStrPPONO != null) {
				lBillDtlsLst = lObjCommonPensionDAO.getRevisionBillDtlsForRectify(gStrLocCode, lStrPPONO);
			}

			inputMap.put("BillPPONo", lStrPPONO != null ? lStrPPONO : "");
			inputMap.put("BillDtlsLst", lBillDtlsLst);
			resObj.setResultValue(inputMap);
			resObj.setViewName("rectifyRevisionBills");

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}
		return resObj;
	}

	public ResultObject updateRectifyBillData(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());

		List lBillDtlsLst = null;

		String lStrPPONO = null;

		try {
			setSessionInfo(inputMap);

			lStrPPONO = StringUtility.getParameter("txtPPONo", request);

			String[] lStrCheckedList = StringUtility.getParameterValues("chkbox", request);

			for (int i = 0; i < lStrCheckedList.length; i++) {
				String lStrBillIDLst = StringUtility.getParameter("hdiBillID_" + lStrCheckedList[i], request);
				String lStrBillTypeLst = StringUtility.getParameter("hdiBillType_" + lStrCheckedList[i], request);

				String lStrDelEditChkLst = StringUtility.getParameter("chkDeleteEdit_" + lStrCheckedList[i], request);
				String lStrPayModeLst = StringUtility.getParameter("cmbPayMode_" + lStrCheckedList[i], request);
				String lStrNewPayAmt = StringUtility.getParameter("txtNewPayAmount_" + lStrCheckedList[i], request);

				/*
				 * if(lStrDelEditChkLst != null && lStrDelEditChkLst.length > 0)
				 * {
				 */// EDIT Entry
				if (lStrDelEditChkLst != null && lStrDelEditChkLst.equals("EDIT")) {
					lObjCommonPensionDAO.updateRectifyBillDtls(lStrBillIDLst, lStrPPONO, lStrPayModeLst, lStrNewPayAmt, gStrLocCode, lStrBillTypeLst, gLngPostId, gLngUserId, gDtCurrDt);
				}
				// DELETE Exist Entry.
				else if (lStrDelEditChkLst != null && lStrDelEditChkLst.equals("DELETE")) {
					lObjCommonPensionDAO.deleteRectifyBillDtls(lStrBillIDLst, lStrPPONO, gStrLocCode, lStrBillTypeLst);
				}
				/* } */
			}

			resObj.setResultValue(inputMap);
			resObj.setViewName("rectifyRevisionBills");

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}
		return resObj;
	}

	public ResultObject updatePensionRemarks(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		Exception lTempEx = null;
		try {
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			String lStrRemarksFromReq = StringUtility.getParameter("lTxtRemarks", request);
			if (lStrRemarksFromReq.length() > 0) {
				for (int i = 0; i < lStrRemarksFromReq.split(";").length; i++) {
					lObjCommonPensionDAO.updatePensionRemarks(lStrRemarksFromReq.split(";")[i]);
				}
				inputMap.put("Message", "Success");
				resObj.setResultValue(inputMap);
			}
			resObj.setViewName("PensionRemarks");
		} catch (Exception e) {
			lTempEx = e;
			inputMap.put("Message", lTempEx.toString());
			resObj.setResultValue(inputMap);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("PensionRemarks");
		}

		return resObj;
	}

	public ResultObject showAttachmentTxtFile(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		StringBuffer lSBPrintString = new StringBuffer();
		StringBuffer lSBDisplayString = new StringBuffer();

		try {
			setSessionInfo(inputMap);
			String lAttchID = request.getParameter("attachmentId");

			CmnAttdocMst lObjAttdocMst = new CmnAttdocMst();
			CmnAttdocMstDAOImpl lObjAttdocMstDAOImpl = new CmnAttdocMstDAOImpl(CmnAttdocMst.class, serv.getSessionFactory());

			String lStrPrint = null;

			lObjAttdocMst = lObjAttdocMstDAOImpl.read(lAttchID != null ? Long.valueOf(lAttchID) : 0L);
			lStrPrint = new String(lObjAttdocMst.getFinalAttachment());
			if (lStrPrint != null) {
				lSBPrintString.append(lStrPrint);
				lSBDisplayString.append(lStrPrint.replace("</pre></div>", "").replace("<div><pre>", "")); // NOPMD
																											// by
																											// 602477
																											// on
																											// 7/1/09
																											// 2:51
																											// PM
			}

			inputMap.put("PrintString", lSBPrintString);
			inputMap.put("DisplayString", lSBDisplayString);
			resObj.setResultValue(inputMap);
			resObj.setViewName("PaidPayableReportPopUp");
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject getBranchByAuditorBankForAjax(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			setSessionInfo(inputMap);
			CommonPensionDAO commonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			ArrayList arrylstBranch = new ArrayList();
			String bankCodeFrmRequest = StringUtility.getParameter("bank", request);
			String auditorPostIdFrmRequest = StringUtility.getParameter("auditor", request);
			String lStrTreasuryCode = StringUtility.getParameter("treasury", request);
			if (lStrTreasuryCode == null || lStrTreasuryCode.equals("") || lStrTreasuryCode.equals("-1")) {
				lStrTreasuryCode = gStrLocCode;
			}
			arrylstBranch = (ArrayList) commonPensionDAO.getBranchsOfAuditorBank(bankCodeFrmRequest, auditorPostIdFrmRequest, gLngLangId, lStrTreasuryCode);
			String strAjaxResult = new AjaxXmlBuilder().addItems(arrylstBranch, "desc", "id").toString();
			inputMap.put("ajaxKey", strAjaxResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject getAuditorByTreasuryForAjax(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrLangId = SessionHelper.getLangId(inputMap).toString();
		try {
			setSessionInfo(inputMap);
			CommonReportDAOImpl lObjPensionPaymentDAO = new CommonReportDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactorySlave());

			String treasuryCode = StringUtility.getParameter("treasury", request);
			if (treasuryCode == null || treasuryCode.equals("") || treasuryCode.equals("-1")) {
				treasuryCode = gStrLocCode;
			}
			List lLstAuditor = lObjPensionPaymentDAO.getAuditor(lStrLangId, treasuryCode);
			String strAjaxResult = new AjaxXmlBuilder().addItems(lLstAuditor, "desc", "id").toString();
			inputMap.put("ajaxKey", strAjaxResult);
			resObj.setViewName("ajaxData");

			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.CommonPensionService#generateLibraryNo
	 * (java.util.Map)
	 */
	public String generateLibraryNo(Map<String, Object> inputMap) throws Exception {

		// TODO Auto-generated method stub
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrPensionerName = "";
		String lStrNameFirstChar = "";
		String lStrLibraryNo = "";
		String lStrPensionerCode = "";
		try {
			MstPensionerHdr lObjMstPensionerHdrVO = new MstPensionerHdr();
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			lStrPensionerCode = (String) inputMap.get("pensionerCode");
			// Generate Library Number start
			lObjMstPensionerHdrVO = lObjPhysicalCaseInwardDAO.getMstPensionerHdrVO(lStrPensionerCode);
			lStrPensionerName = lObjMstPensionerHdrVO.getFirstName();
			lStrNameFirstChar = lStrPensionerName.substring(0, 1);
			inputMap.put("LibraryNo", "LibraryNo_" + lStrNameFirstChar);

			String lStrCnt = getNextLibraryNoId(inputMap);

			lStrLibraryNo = gStrLocCode + lStrNameFirstChar + lStrCnt;

			lObjMstPensionerHdrVO.setLibraryNo(lStrLibraryNo);
			lObjPhysicalCaseInwardDAO.update(lObjMstPensionerHdrVO);
			// Generate Library Number end
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			// e.printStackTrace();
		}
		return lStrLibraryNo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.CommonPensionService#getNextLibraryNoId
	 * (java.util.Map)
	 */
	public String getNextLibraryNoId(Map<String, Object> inputMap) throws Exception {

		// TODO Auto-generated method stub
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrLibraryNo = "";
		String lStrGeneratedId = "";
		Integer lIntGeneratedId = 0;
		String lStrLibraryNoId = "";
		try {
			lStrLibraryNo = inputMap.get("LibraryNo").toString();
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			lStrGeneratedId = lObjCommonPensionDAO.getGeneratedIdForLibraryNo(lStrLibraryNo, gStrLocCode);
			if (lStrGeneratedId != null && lStrGeneratedId != "") {
				lIntGeneratedId = Integer.parseInt(lStrGeneratedId);
			}

			lStrLibraryNoId = String.format("%" + 5 + "s", lIntGeneratedId).replace(' ', '0');
			lIntGeneratedId = ++lIntGeneratedId;
			lObjCommonPensionDAO.updateGeneratedIdForLibraryNo(lStrLibraryNo, gStrLocCode, lIntGeneratedId);

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return lStrLibraryNoId;
	}

	public ResultObject getAGCircleByLocationCode(Map<String, Object> inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrLocCode = null;
		CommonPensionDAO lObjCommonPensionDAO = null;
		List<Object[]> lLstAGCircleDtl = null;
		String lStrAGCirclName = "";
		String lStrAGCircleCode = "";
		Object[] lArrObj = null;
		StringBuilder lSBResp = null;
		String lStrResult = null;
		try {
			setSessionInfo(inputMap);
			lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			lStrLocCode = StringUtility.getParameter("locCode", request);
			lLstAGCircleDtl = lObjCommonPensionDAO.getAGCircleDtlFromLocationCode(lStrLocCode);
			lArrObj = lLstAGCircleDtl.get(0);
			lStrAGCircleCode = (lArrObj[0] != null) ? lArrObj[0].toString() : "";
			lStrAGCirclName = (lArrObj[1] != null) ? lArrObj[1].toString() : "";
			lSBResp = getResponseXMLDocForAGCircle(lStrAGCirclName, lStrAGCircleCode);
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBResp.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public static StringBuilder getResponseXMLDocForAGCircle(String lStrAGCirclName, String lStrAGCircleCode) throws Exception {

		StringBuilder lSBResp = new StringBuilder();
		try {
			lSBResp.append("<XMLDOC>");
			lSBResp.append("<AGCIRCLECODE>");
			lSBResp.append(lStrAGCircleCode);
			lSBResp.append("</AGCIRCLECODE>");
			lSBResp.append("<AGCIRCLENAME>");
			lSBResp.append(lStrAGCirclName);
			lSBResp.append("</AGCIRCLENAME>");
			lSBResp.append("</XMLDOC>");
		} catch (Exception e) {
			throw e;
		}
		return lSBResp;
	}

	public ResultObject getReportingBranchListFromReportingBank(Map<String, Object> inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrReportinbBankCode = null;
		setSessionInfo(inputMap);

		List lLstReportingBranchCodeList = new ArrayList();

		// BigDecimal lBDUserId = (new BigDecimal(gLngUserId));
		// BigDecimal lBDPostId = (new BigDecimal(gLngPostId));

		try {
			setSessionInfo(inputMap);
			lStrReportinbBankCode = StringUtility.getParameter("ReportingBankCode", request);
			CommonDAO lObjCommonPensionDAO = new CommonDAOImpl(serv.getSessionFactory());
			lLstReportingBranchCodeList = lObjCommonPensionDAO.getReportingBranchListFromReportingBank(gStrLocCode, gLngLangId, lStrReportinbBankCode);
			String lStrAjaxResult = new AjaxXmlBuilder().addItems(lLstReportingBranchCodeList, "desc", "id").toString();
			inputMap.put("ajaxKey", lStrAjaxResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject getPensionDaRate(Map<String, Object> inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrDAType = null;
		String lStrStateDeptCode = null;
		CommonPensionDAO lObjCommonPensionDAO = null;
		List<ComboValuesVO> lLstDARates = null;
		try {
			setSessionInfo(inputMap);
			lStrDAType = StringUtility.getParameter("cmbDaRateType", request).trim();
			lStrStateDeptCode = StringUtility.getParameter("cmbStateCode", request).trim();
			lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			if (lStrStateDeptCode.length() > 0) {
				lLstDARates = lObjCommonPensionDAO.getDaRateFromPayCommissionStateDept(lStrDAType, new BigDecimal(lStrStateDeptCode));
			}
			String lStrAjaxResult = new AjaxXmlBuilder().addItems(lLstDARates, "desc", "id").toString();
			inputMap.put("ajaxKey", lStrAjaxResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

}
