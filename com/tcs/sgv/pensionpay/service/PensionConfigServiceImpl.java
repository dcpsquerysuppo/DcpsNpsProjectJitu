/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 13, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
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
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.MstScheme;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAO;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.dao.PensionConfigDAO;
import com.tcs.sgv.pensionpay.dao.PensionConfigDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.HstPnsnPmntPpoNo;
import com.tcs.sgv.pensionpay.valueobject.MstBankGrp;
import com.tcs.sgv.pensionpay.valueobject.MstChangeStmntCtgry;
import com.tcs.sgv.pensionpay.valueobject.MstPensionHeadcode;
import com.tcs.sgv.pensionpay.valueobject.MstPensionMainCategory;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerDtls;
import com.tcs.sgv.pensionpay.valueobject.RltAuditorBank;
import com.tcs.sgv.pensionpay.valueobject.RltBankBranchGrp;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeChargable;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionproc.service.PensionCaseServiceImpl;
import com.tcs.sgv.web.jsp.tags.DateUtilities;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 May 13, 2011
 */
public class PensionConfigServiceImpl extends ServiceImpl implements PensionConfigService {

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private final static Logger gLogger = Logger.getLogger(PensionCaseServiceImpl.class);

	private static final Log logger = LogFactory.getLog(PensionCaseServiceImpl.class); /* LOGGER */

	private Date gDate = null;

	private Long gLngUserId = null;

	private Long gDbId = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	private void setSessionInfo(Map inputMap) throws Exception {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gDate = DBUtility.getCurrentDateFromDB();
			gDbId = SessionHelper.getDbId(inputMap);

		} catch (Exception e) {
			logger.error("Error in setSessionInfo of PensionConfigServiceImpl ", e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PensionPaymentAdminService#
	 * loadAuditorBankBrnchMapping(java.util.Map)
	 */
	public ResultObject loadAuditorBankBrnchMapping(Map<String, Object> inputMap) {

		gLogger.info("In loadAuditorBankBrnchMapping method.......");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		List lLstAuditorNames = null;
		List lLstBankNames = null;
		String lStrAuditorPostId = null;
		List<Object[]> lLstCurrMappingDtls = null;
		String lStrResp = null;
		StringBuilder lStrBldXML = null;
		try {
			setSessionInfo(inputMap);
			lStrAuditorPostId = StringUtility.getParameter("AuditorPostId", request);
			lObjPensionConfigDAO = new PensionConfigDAOImpl(null, serv.getSessionFactory());
			if (lStrAuditorPostId.length() > 0) {
				lLstCurrMappingDtls = lObjPensionConfigDAO.getCurrMappingDtls(gStrLocationCode, gLngLangId, lStrAuditorPostId.trim());
				inputMap.put("lLstCurrMappingDtls", lLstCurrMappingDtls);
				inputMap.put("lLstCurrMappingDtlsCount", lLstCurrMappingDtls.size());
				lStrBldXML = generateXMLDocForViewMapping(lLstCurrMappingDtls);
				lStrResp = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
				inputMap.put("ajaxKey", lStrResp);
				resObj.setViewName("ajaxData");
				resObj.setResultValue(inputMap);
				resObj.setResultCode(ErrorConstants.SUCCESS);
			} else {
				lLstAuditorNames = lObjPensionConfigDAO.getAuditorName(inputMap, gStrLocationCode);
				lLstBankNames = lObjPensionConfigDAO.getBankNames(gLngLangId, gStrLocationCode);
				inputMap.put("lLstBankNames", lLstBankNames);
				inputMap.put("lLstAuditorNames", lLstAuditorNames);
				resObj.setResultCode(ErrorConstants.SUCCESS);
				resObj.setResultValue(inputMap);
				resObj.setViewName("auditorBankBranchMapping");
			}

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;
	}

	private StringBuilder generateXMLDocForViewMapping(List<Object[]> lLstCurrMappingDtls) {

		StringBuilder lSBXMLDoc = new StringBuilder();
		if (lLstCurrMappingDtls != null) {
			lSBXMLDoc.append("<XMLDOC>");
			for (Object[] lArrObj : lLstCurrMappingDtls) {
				lSBXMLDoc.append("<BANKBRANCHMAPPING>");
				lSBXMLDoc.append("<BANKNAME>");
				lSBXMLDoc.append("<![CDATA[" + lArrObj[1] + "]]>");
				lSBXMLDoc.append("</BANKNAME>");
				lSBXMLDoc.append("<BRANCHNAME>");
				lSBXMLDoc.append("<![CDATA[" + lArrObj[2] + "]]>");
				lSBXMLDoc.append("</BRANCHNAME>");
				lSBXMLDoc.append("</BANKBRANCHMAPPING>");
			}
			lSBXMLDoc.append("</XMLDOC>");
		}
		return lSBXMLDoc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PensionPaymentAdminService#
	 * displayBranchesForBank(java.util.Map)
	 */
	public ResultObject displayBranchesForBank(Map<String, Object> inputMap) {

		gLogger.info("In loadAuditorBankBrnchMapping method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		List lLstAuditorNames = null;
		List lLstBankNames = null;

		try {
			setSessionInfo(inputMap);
			lObjPensionConfigDAO = new PensionConfigDAOImpl(null, serv.getSessionFactory());
			lLstAuditorNames = lObjPensionConfigDAO.getAuditorName(inputMap, gStrLocationCode);
			lLstBankNames = lObjPensionConfigDAO.getBankNames(gLngLangId, gStrLocationCode);

			inputMap.put("lLstBankNames", lLstBankNames);
			inputMap.put("lLstAuditorNames", lLstAuditorNames);
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);
			objRes.setViewName("auditorBankBranchMapping");
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
	 * @seecom.tcs.sgv.pensionpay.service.PensionPaymentAdminService#
	 * saveAuditorBankBrnchMapping(java.util.Map)
	 */
	public ResultObject saveAuditorBankBrnchMapping(Map<String, Object> inputMap) {

		gLogger.info("In saveAuditorBankBrnchMapping method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		Long lLngAuditorBankId = 0l;
		List<RltAuditorBank> lLstRltAuditorBankVO = null;
		RltAuditorBank lObjRltAuditorBankVO = null;
		TrnPensionRqstHdr lObjTrnPensionRqstHdrVO = null;
		StringBuilder lStrBldXML = null;
		String lStrResult = null;
		String lStrTransMode = null;
		String lStrBranchCode = null;
		String lStrBankCode = null;
		String lStrPostId = null;
		String[] lStrArrBranchCode = null;
		List<String> lLstBranchCode = new ArrayList<String>();
		List<String> lLstNewBranchCode = new ArrayList<String>();
		List<RltAuditorBank> lLstRltAuditorBank = null;
		List<MstPensionerDtls> lLstMstPensionerDtls = null;
		List<String> lLstPensionerCode = new ArrayList<String>();
		List<TrnPensionRqstHdr> lLstTrnPensionRqstHdr = null;
		String lStrAction = null;
		try {

			setSessionInfo(inputMap);
			lLstRltAuditorBankVO = new ArrayList<RltAuditorBank>();
			lObjRltAuditorBankVO = new RltAuditorBank();
			lStrTransMode = (String) inputMap.get("Mode");
			lStrAction = StringUtility.getParameter("action", request);
			lLstRltAuditorBankVO = (List<RltAuditorBank>) inputMap.get("lLstRltAuditorBankVO");

			lStrPostId = StringUtility.getParameter("cmbAuditorName", request);
			lStrBankCode = StringUtility.getParameter("cmbBankName", request);
			lStrBranchCode = StringUtility.getParameter("CmbDisplayBranchName", request);
			lStrArrBranchCode = lStrBranchCode.split("~");

			if (lStrAction.length() > 0 && lStrAction.equals("Remove")) {
				lObjPensionConfigDAO = new PensionConfigDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
				if (lStrArrBranchCode.length > 0) {
					for (int lIntCnt = 0; lIntCnt < lStrArrBranchCode.length; lIntCnt++) {
						lObjPensionConfigDAO.removeMapping(lStrPostId, lStrBankCode, lStrArrBranchCode[lIntCnt], gStrLocationCode);
						lLstPensionerCode = lObjPensionConfigDAO.getPensionerCode(lStrArrBranchCode[lIntCnt].trim());
						if (!lLstPensionerCode.isEmpty() && lLstPensionerCode.size() > 0) {
							for (String lStrPensionerCode : lLstPensionerCode) {
								lLstTrnPensionRqstHdr = lObjPensionConfigDAO.getListByColumnAndValue("pensionerCode", lStrPensionerCode.trim());
								if (lLstTrnPensionRqstHdr != null && lLstTrnPensionRqstHdr.size() > 0) {
									lObjTrnPensionRqstHdrVO = lLstTrnPensionRqstHdr.get(0);
									lObjTrnPensionRqstHdrVO.setCaseOwner(null);
									lObjPensionConfigDAO.update(lObjTrnPensionRqstHdrVO);
								}
							}
						}
					}
				}
				lStrBldXML = getResponseXMLDoc(inputMap, lStrAction);
			} else {
				lObjPensionConfigDAO = new PensionConfigDAOImpl(RltAuditorBank.class, serv.getSessionFactory());
				// newly added changes for updating already mapped branches with
				// new
				// auditor and in trn_pension_rqst_hdr ::STARTS::

				if (lStrArrBranchCode.length > 0) {
					for (int lIntCnt = 0; lIntCnt < lStrArrBranchCode.length; lIntCnt++) {
						if (lObjPensionConfigDAO.validateMapping(lStrPostId, lStrBankCode, lStrArrBranchCode[lIntCnt].trim(), gStrLocationCode) == false) {
							lLstBranchCode.add(lStrArrBranchCode[lIntCnt].trim());
						}
					}
				}
				if (!lLstBranchCode.isEmpty() && lLstBranchCode.size() > 0) {
					for (String lStrBrnchCode : lLstBranchCode) {
						lLstRltAuditorBank = lObjPensionConfigDAO.getRltAuditorBankVO(lStrBrnchCode.trim());
						if (lLstRltAuditorBank != null && lLstRltAuditorBank.size() > 0) {
							// lObjRltAuditorBankVO = new RltAuditorBank();
							lObjRltAuditorBankVO = lLstRltAuditorBank.get(0);
							lObjRltAuditorBankVO.setPostId(Long.valueOf(lStrPostId.trim()));
						}
					}

					lObjPensionConfigDAO = new PensionConfigDAOImpl(MstPensionerDtls.class, serv.getSessionFactory());
					for (String lStrBrnchCode : lLstBranchCode) {
						lLstMstPensionerDtls = lObjPensionConfigDAO.getMstPensionerDtlsVO(lStrBrnchCode);
						if (lLstMstPensionerDtls != null && lLstMstPensionerDtls.size() > 0) {
							for (MstPensionerDtls lObjMstPensionerDtlsVO : lLstMstPensionerDtls) {
								lLstPensionerCode.add(lObjMstPensionerDtlsVO.getPensionerCode().trim());
							}
						}
					}

					lObjPensionConfigDAO = new PensionConfigDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
					if (!lLstPensionerCode.isEmpty() && lLstPensionerCode.size() > 0) {
						for (String lStrPensionerCode : lLstPensionerCode) {
							lLstTrnPensionRqstHdr = lObjPensionConfigDAO.getListByColumnAndValue("pensionerCode", lStrPensionerCode.trim());
							if (lLstTrnPensionRqstHdr != null && lLstTrnPensionRqstHdr.size() > 0) {
								lObjTrnPensionRqstHdrVO = lLstTrnPensionRqstHdr.get(0);
								lObjTrnPensionRqstHdrVO.setCaseOwner(BigDecimal.valueOf((Long.valueOf(lStrPostId.trim()))));
								lObjPensionConfigDAO.update(lObjTrnPensionRqstHdrVO);
							}
						}

					}
				}

				// newly added changes for updating already mapped branches with
				// new
				// auditor and in trn_pension_rqst_hdr ::ENDS::

				if (lStrTransMode.equalsIgnoreCase("Add")) {

					if (lLstRltAuditorBankVO != null) {
						for (int lIntCount = 0; lIntCount < lLstRltAuditorBankVO.size(); lIntCount++) {
							lObjRltAuditorBankVO = lLstRltAuditorBankVO.get(lIntCount);
							lLngAuditorBankId = IFMSCommonServiceImpl.getNextSeqNum("rlt_auditor_bank", inputMap);
							lObjRltAuditorBankVO.setAuditorBankId(lLngAuditorBankId);
							lLstNewBranchCode.add(lObjRltAuditorBankVO.getBranchCode());
							lObjPensionConfigDAO.create(lObjRltAuditorBankVO);
							gLogger.info("Record Inserted in table rlt_auditor_bank successfully");

						}
						lObjPensionConfigDAO = new PensionConfigDAOImpl(MstPensionerDtls.class, serv.getSessionFactory());
						for (String lStrBrnchCode : lLstNewBranchCode) {
							lLstMstPensionerDtls = lObjPensionConfigDAO.getMstPensionerDtlsVO(lStrBrnchCode);
							if (lLstMstPensionerDtls != null && lLstMstPensionerDtls.size() > 0) {
								for (MstPensionerDtls lObjMstPensionerDtlsVO : lLstMstPensionerDtls) {
									lLstPensionerCode.add(lObjMstPensionerDtlsVO.getPensionerCode().trim());
								}
							}
						}
						lObjPensionConfigDAO = new PensionConfigDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
						if (!lLstPensionerCode.isEmpty() && lLstPensionerCode.size() > 0) {
							for (String lStrPensionerCode : lLstPensionerCode) {
								lLstTrnPensionRqstHdr = lObjPensionConfigDAO.getListByColumnAndValue("pensionerCode", lStrPensionerCode.trim());
								if (lLstTrnPensionRqstHdr != null && lLstTrnPensionRqstHdr.size() > 0) {
									lObjTrnPensionRqstHdrVO = lLstTrnPensionRqstHdr.get(0);
									lObjTrnPensionRqstHdrVO.setCaseOwner(BigDecimal.valueOf((Long.valueOf(lStrPostId.trim()))));
									lObjPensionConfigDAO.update(lObjTrnPensionRqstHdrVO);
								}
							}

						}
					}
					lStrBldXML = getResponseXMLDoc(inputMap, lStrAction);
				}
			}

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

		}
		return objRes;

	}

	private StringBuilder getResponseXMLDoc(Map inputMap, String lStrMode) {

		StringBuilder lStrHidPKs = new StringBuilder();
		if (lStrMode.equalsIgnoreCase("Add")) {
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append("Add");
			lStrHidPKs.append("</MESSAGECODE>");
			lStrHidPKs.append("</XMLDOC>");

		}
		if (lStrMode.equals("Update")) {
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append("Update");
			lStrHidPKs.append("</MESSAGECODE>");
			lStrHidPKs.append("</XMLDOC>");
		}
		if (lStrMode.equals("Remove")) {
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append("Remove");
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
	 * com.tcs.sgv.pensionpay.service.PensionPaymentAdminService#getMappingDtls
	 * (java.util.Map)
	 */
	public ResultObject getMappingDtls(Map<String, Object> inputMap) {

		gLogger.info("In getMappingDtls method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		Long lLngAuditorPostId = null;
		String lStrBankCode = null;
		StringBuilder lStrBldXML = null;
		String lStrResult = null;
		List lLstMappingsDtls = null;
		Integer lIntListSize = null;
		List lLstBankCode = new ArrayList();
		List lLstBankName = new ArrayList();
		String lStrAuditorPostId = null;
		try {

			setSessionInfo(inputMap);
			lStrAuditorPostId = StringUtility.getParameter("AuditorPostId", request);
			lStrBankCode = StringUtility.getParameter("BankCode", request);
			if (lStrAuditorPostId.length() > 0) {
				lLngAuditorPostId = Long.valueOf(lStrAuditorPostId.trim());
			}
			lObjPensionConfigDAO = new PensionConfigDAOImpl(null, serv.getSessionFactory());
			lLstMappingsDtls = lObjPensionConfigDAO.getMappingDtls(lLngAuditorPostId, lStrBankCode.trim(), gStrLocationCode);
			lIntListSize = lLstMappingsDtls.size();

			Iterator it = lLstMappingsDtls.iterator();
			Object[] tuple = null;
			int lIntLoopJ = 0;
			while (it.hasNext()) {
				tuple = (Object[]) it.next();
				String bankCode = tuple[0].toString();
				String bankName = tuple[1].toString();
				lLstBankCode.add(bankCode);
				lLstBankName.add(bankName);
				lIntLoopJ++;
			}

			lStrBldXML = getResponseXMLDocForMappingDtls(lIntListSize, lLstBankCode, lLstBankName);
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
			//e.printStackTrace();

		}
		return objRes;
	}

	private StringBuilder getResponseXMLDocForMappingDtls(Integer lIntListSize, List lLstBankCode, List lLstBankName) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<lNoOfRecords>");
		lStrBldXML.append(lIntListSize);
		lStrBldXML.append("</lNoOfRecords>");
		for (int lInt = 0; lInt < lIntListSize; lInt++) {
			lStrBldXML.append("<BankCode" + lInt + ">");
			lStrBldXML.append(lLstBankCode.get(lInt));
			lStrBldXML.append("</BankCode" + lInt + ">");
			lStrBldXML.append("<BankName" + lInt + ">");
			lStrBldXML.append(lLstBankName.get(lInt));
			lStrBldXML.append("</BankName" + lInt + ">");
		}
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	/*
	 * private StringBuilder getResponseXMLDocForMappingDtls(Integer
	 * lIntListSize, List<RltAuditorBank> lLstMappingsDtls) {
	 * 
	 * StringBuilder lStrBldXML = new StringBuilder();
	 * 
	 * lStrBldXML.append("<XMLDOC>");
	 * 
	 * for (int lInt = 0; lInt < lLstMappingsDtls.size(); lInt++) {
	 * lStrBldXML.append(" <SchemeName" + lInt + ">");
	 * lStrBldXML.append(lLstMappingsDtls.get(lInt));
	 * lStrBldXML.append(" </SchemeName" + lInt + ">");
	 * lStrBldXML.append(" <SchemeCode" + lInt + ">");
	 * lStrBldXML.append(lLstMappingsDtls.get(lInt));
	 * lStrBldXML.append(" </SchemeCode" + lInt + ">"); }
	 * 
	 * lStrBldXML.append("</XMLDOC>");
	 * 
	 * return lStrBldXML; }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.PensionPaymentAdminService#getRemarksOfBill
	 * (java.util.Map)
	 */
	public ResultObject getRemarksOfBill(Map<String, Object> inputMap) {

		gLogger.info("In getRemarksOfBill method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		List lLstTotalUsers = null;
		Long lLngBillNo = null;
		String lStrBillNo = null;
		try {
			setSessionInfo(inputMap);
			lStrBillNo = StringUtility.getParameter("BillNo", request);
			if (lStrBillNo.length() > 0) {
				lLngBillNo = Long.valueOf(lStrBillNo.trim());
			}
			lObjPensionConfigDAO = new PensionConfigDAOImpl(null, serv.getSessionFactory());
			if (lLngBillNo != null) {
				lLstTotalUsers = lObjPensionConfigDAO.getRemarksOfBill(lLngBillNo, gStrLocationCode, gLngLangId);
				inputMap.put("lLngBillNo", lLngBillNo);
				inputMap.put("lLstTotalUsers", lLstTotalUsers);
				objRes.setViewName("ShowRemarksPopUp");
			} else {
				objRes.setViewName("ShowRemarks");
			}
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);

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
	 * com.tcs.sgv.pensionpay.service.PensionConfigService#loadHeadCodeConfig
	 * (java.util.Map)
	 */
	public ResultObject loadHeadCodeConfig(Map<String, Object> inputMap) {

		gLogger.info("In loadHeadCodeConfig method.......");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		List lLstHeadCodeConfigDtls = null;
		Integer lIntTotalRecords = null;
		List<BigDecimal> lLstHeadCode = null;
		BigDecimal lBDHeadCode = BigDecimal.ZERO;
		List<BigDecimal> lLstFinalHeadCode = new ArrayList<BigDecimal>();
		List lLstMainCtgryConfigDtls = null;

		try {
			setSessionInfo(inputMap);
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			lObjPensionConfigDAO = new PensionConfigDAOImpl(null, serv.getSessionFactory());
			lIntTotalRecords = lObjPensionConfigDAO.getHeadCodeConfigDtlsCount(gLngLangId, displayTag);
			lLstHeadCodeConfigDtls = lObjPensionConfigDAO.getHeadCodeConfigDtls(gLngLangId, displayTag);
			lLstMainCtgryConfigDtls = lObjPensionConfigDAO.getMainCtgryConfigDtls(gLngLangId);

			lLstHeadCode = lObjPensionConfigDAO.getHeadCodeNos();
			Iterator<BigDecimal> it = lLstHeadCode.iterator();

			BigDecimal tuple = BigDecimal.ZERO;
			/*
			 * for() { if(it.next()!= null) { tuple = (BigDecimal)(it.next());
			 * lBDHeadCode = tuple; lLstFinalHeadCode.add(lBDHeadCode); } }
			 */

			while (it.hasNext()) {
				tuple = it.next();
				lBDHeadCode = tuple;
				if (lBDHeadCode != null) {
					lLstFinalHeadCode.add(lBDHeadCode);
				}
			}
			inputMap.put("lLstHeadCode", lLstFinalHeadCode);
			inputMap.put("TotalHeadCodeNo", lLstHeadCode.size());
			inputMap.put("totalRecords", lIntTotalRecords);
			inputMap.put("lLstHeadCodeConfigDtls", lLstHeadCodeConfigDtls);
			inputMap.put("lLstMainCtgryConfigDtls", lLstMainCtgryConfigDtls);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("headCodeConfig");
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//e.printStackTrace();

		}
		return resObj;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.PensionConfigService#saveHeadCodeConfig
	 * (java.util.Map)
	 */
	public ResultObject saveHeadCodeConfig(Map<String, Object> inputMap) {

		gLogger.info("In saveHeadCodeConfig method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		List<RltPensionHeadcodeChargable> lLstRltPensionHeadcodeChargableVO = null;
		RltPensionHeadcodeChargable lObjRltPensionHeadcodeChargableVO = null;
		StringBuilder lStrBldXML = null;
		String lStrResult = null;
		String lStrTransMode = null;
		MstPensionHeadcode lObjMstPensionHeadcodeVO = null;
		Long lLngpensionHeadcodeId = null;
		Long lLngpensionHeadcodeChargableId = null;
		String[] lStrArrPnsnHeadCodeId = null;
		String lStrPnsnHeadCodeId = null;
		BigDecimal lBDHeadCode = null;

		try {
			setSessionInfo(inputMap);
			lLstRltPensionHeadcodeChargableVO = new ArrayList<RltPensionHeadcodeChargable>();
			lObjRltPensionHeadcodeChargableVO = new RltPensionHeadcodeChargable();
			lObjMstPensionHeadcodeVO = new MstPensionHeadcode();
			lStrTransMode = (String) inputMap.get("Mode");
			lLstRltPensionHeadcodeChargableVO = (List<RltPensionHeadcodeChargable>) inputMap.get("lLstRltPensionHeadcodeChargableVO");
			lObjMstPensionHeadcodeVO = (MstPensionHeadcode) inputMap.get("lObjMstPensionHeadcodeVO");
			if (lStrTransMode.equalsIgnoreCase("Add")) {

				if (lObjMstPensionHeadcodeVO != null) {
					lObjPensionConfigDAO = new PensionConfigDAOImpl(MstPensionHeadcode.class, serv.getSessionFactory());
					lLngpensionHeadcodeId = IFMSCommonServiceImpl.getNextSeqNum("mst_pension_headcode", inputMap);
					lObjMstPensionHeadcodeVO.setPensionHeadcodeId(lLngpensionHeadcodeId);
					lObjPensionConfigDAO.create(lObjMstPensionHeadcodeVO);
					gLogger.info("Record Inserted in table mst_pension_headcode successfully.");
				}
				if (lLstRltPensionHeadcodeChargableVO != null) {
					lObjPensionConfigDAO = new PensionConfigDAOImpl(RltPensionHeadcodeChargable.class, serv.getSessionFactory());
					for (int lIntCount = 0; lIntCount < lLstRltPensionHeadcodeChargableVO.size(); lIntCount++) {
						lObjRltPensionHeadcodeChargableVO = lLstRltPensionHeadcodeChargableVO.get(lIntCount);
						lLngpensionHeadcodeChargableId = IFMSCommonServiceImpl.getNextSeqNum("rlt_pension_headcode_chargable", inputMap);
						lObjRltPensionHeadcodeChargableVO.setPensionHeadcodeChargableId(lLngpensionHeadcodeChargableId);
						lObjPensionConfigDAO.create(lObjRltPensionHeadcodeChargableVO);
						gLogger.info("Record Inserted in table rlt_pension_headcode_chargable successfully.");

					}
				}

			}
			if (lStrTransMode.equalsIgnoreCase("Update")) {

				lObjPensionConfigDAO = new PensionConfigDAOImpl(MstPensionHeadcode.class, serv.getSessionFactory());
				lObjPensionConfigDAO.update(lObjMstPensionHeadcodeVO);

				lObjPensionConfigDAO = new PensionConfigDAOImpl(RltPensionHeadcodeChargable.class, serv.getSessionFactory());
				for (int lIntCount = 0; lIntCount < lLstRltPensionHeadcodeChargableVO.size(); lIntCount++) {
					lObjRltPensionHeadcodeChargableVO = lLstRltPensionHeadcodeChargableVO.get(lIntCount);
					lObjPensionConfigDAO.update(lObjRltPensionHeadcodeChargableVO);

				}

			}
			if (lStrTransMode.equalsIgnoreCase("Delete")) {
				lStrPnsnHeadCodeId = StringUtility.getParameter("PnsnHeadCodeId", request);
				lStrArrPnsnHeadCodeId = lStrPnsnHeadCodeId.split("~");
				lObjPensionConfigDAO = new PensionConfigDAOImpl(MstPensionHeadcode.class, serv.getSessionFactory());
				for (int lIntCnt = 0; lIntCnt < lStrArrPnsnHeadCodeId.length; lIntCnt++) {
					lObjMstPensionHeadcodeVO = (MstPensionHeadcode) lObjPensionConfigDAO.read(Long.valueOf(lStrArrPnsnHeadCodeId[lIntCnt].trim()));
					lBDHeadCode = lObjMstPensionHeadcodeVO.getHeadCode();
					lObjPensionConfigDAO.delRltPensionHeadcodeChargableVO(lBDHeadCode);
					lObjPensionConfigDAO.delete(lObjMstPensionHeadcodeVO);
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

		}
		return objRes;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.MonthlyPensionBillService#
	 * saveChangeStmntCtgryConfig(java.util.Map)
	 */
	public ResultObject saveChangeStmntCtgryConfig(Map<String, Object> inputMap) {

		gLogger.info("In saveChangeStmntCtgryConfig method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		Long lLngChangeStmntCtgryId = 0l;
		List<MstChangeStmntCtgry> lLstMstChangeStmntCtgry = null;
		MstChangeStmntCtgry lObjMstChangeStmntCtgry = null;
		StringBuilder lStrBldXML = null;
		String lStrResult = null;
		String lStrTransMode = null;
		try {

			setSessionInfo(inputMap);
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
			lLstMstChangeStmntCtgry = new ArrayList<MstChangeStmntCtgry>();
			lObjMstChangeStmntCtgry = new MstChangeStmntCtgry();
			lStrTransMode = (String) inputMap.get("Mode");
			lLstMstChangeStmntCtgry = (List<MstChangeStmntCtgry>) inputMap.get("lLstMstChangeStmntCtgry");
			if (lStrTransMode.equalsIgnoreCase("Add")) {
				if (lLstMstChangeStmntCtgry != null) {
					lObjPensionConfigDAO = new PensionConfigDAOImpl(MstChangeStmntCtgry.class, serv.getSessionFactory());
					for (int lIntCount = 0; lIntCount < lLstMstChangeStmntCtgry.size(); lIntCount++) {
						lObjMstChangeStmntCtgry = lLstMstChangeStmntCtgry.get(lIntCount);
						lLngChangeStmntCtgryId = IFMSCommonServiceImpl.getNextSeqNum("mst_change_statement_category", inputMap);
						lObjMstChangeStmntCtgry.setChangeStmntCtgryId(lLngChangeStmntCtgryId);
						lObjPensionConfigDAO.create(lObjMstChangeStmntCtgry);
						gLogger.info("Record Inserted in table mst_change_statement_category successfully");

					}
				}
			}
			lStrBldXML = getResponseXMLDocForChngStmnt("Add");
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

		}
		return objRes;
	}

	private StringBuilder getResponseXMLDocForChngStmnt(String strMode) {

		StringBuilder lStrHidPKs = new StringBuilder();
		if (strMode.equalsIgnoreCase("Add")) {
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append("Add");
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
	 * com.tcs.sgv.pensionpay.service.PensionConfigService#getPPODetails(java
	 * .util.Map)
	 */

	public ResultObject getPPODetails(Map<String, Object> inputMap) {

		gLogger.info("In getPPODetails method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		String lStrPPONo = null;
		List lLstPPODetails = null;

		try {
			setSessionInfo(inputMap);
			lStrPPONo = StringUtility.getParameter("ppoNo", request);
			lObjPensionConfigDAO = new PensionConfigDAOImpl(null, serv.getSessionFactory());
			if (lStrPPONo.length() > 0) {
				lLstPPODetails = lObjPensionConfigDAO.getPPODetails(lStrPPONo, gStrLocationCode);
			}

			String lSBStatus = getResponseXMLDocPPODetails(lLstPPODetails).toString();
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

	private StringBuilder getResponseXMLDocPPODetails(List lLstPPODetails) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOCPPODETAILS>");
		if (lLstPPODetails != null && lLstPPODetails.size() > 0) {
			Iterator itr = lLstPPODetails.iterator();
			while (itr.hasNext()) {
				Object[] obj = (Object[]) itr.next();
				lStrBldXML.append("<PnsnrName>");
				lStrBldXML.append(obj[0].toString());
				lStrBldXML.append("</PnsnrName>");
				lStrBldXML.append("<PnsnrCode>");
				lStrBldXML.append(obj[1].toString());
				lStrBldXML.append("</PnsnrCode>");
				lStrBldXML.append("<BankName>");
				lStrBldXML.append(obj[2].toString());
				lStrBldXML.append("</BankName>");
				lStrBldXML.append("<BranchName>");
				lStrBldXML.append(obj[3].toString());
				lStrBldXML.append("</BranchName>");
				lStrBldXML.append("<AccNo>");
				lStrBldXML.append(obj[4].toString());
				lStrBldXML.append("</AccNo>");
			}
		} else {
			lStrBldXML.append("<EmptyList>");
			lStrBldXML.append("EmptyList");
			lStrBldXML.append("</EmptyList>");
		}

		lStrBldXML.append("</XMLDOCPPODETAILS>");
		return lStrBldXML;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.PensionConfigService#savePPONo(java.util
	 * .Map)
	 */

	public ResultObject savePPONo(Map<String, Object> inputMap) {

		gLogger.info("In savePPONo method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		PensionConfigDAO lObjPensionConfigDAO = null;
		Long lLngHstPnsnPmntId = 0l;
		HstPnsnPmntPpoNo lObjHstPnsnPmntPpoNo = null;
		StringBuilder lStrBldXML = null;
		String lStrResult = null;
		String lStrTransMode = null;
		String lStrPensionerCode = null;
		try {

			setSessionInfo(inputMap);
			lStrPensionerCode = StringUtility.getParameter("pnsnrCode", request);
			lObjPensionConfigDAO = new PensionConfigDAOImpl(HstPnsnPmntPpoNo.class, serv.getSessionFactory());
			lObjHstPnsnPmntPpoNo = new HstPnsnPmntPpoNo();
			lStrTransMode = (String) inputMap.get("Mode");
			lObjHstPnsnPmntPpoNo = (HstPnsnPmntPpoNo) inputMap.get("lObjHstPnsnPmntPpoNo");
			if (lStrTransMode.equalsIgnoreCase("Add")) {
				if (lObjHstPnsnPmntPpoNo != null) {
					lLngHstPnsnPmntId = IFMSCommonServiceImpl.getNextSeqNum("hst_pnsnpmnt_ppono", inputMap);
					lObjHstPnsnPmntPpoNo.setHstpnsnpmntId(lLngHstPnsnPmntId);
					lObjPensionConfigDAO.create(lObjHstPnsnPmntPpoNo);
					gLogger.info("Record Inserted in table hst_pnsnpmnt_ppono successfully");
				}
			}

			lStrBldXML = getResponseXMLDocForChngStmnt("Add");
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);

			lObjPensionConfigDAO = new PensionConfigDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
			List<TrnPensionRqstHdr> lLstTrnPensionRqstHdr = lObjPensionConfigDAO.getListByColumnAndValue("pensionerCode", lStrPensionerCode);
			if (!lLstTrnPensionRqstHdr.isEmpty()) {
				TrnPensionRqstHdr lObjTrnPensionRqstHdr = lLstTrnPensionRqstHdr.get(0);
				lObjTrnPensionRqstHdr.setPpoNo(lObjHstPnsnPmntPpoNo.getNewppoNo());
			}

			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");

		}
		return objRes;

	}

	public ResultObject loadChangeStmntCtgryConfig(Map<String, Object> inputMap) {

		gLogger.info("In loadChangeStmntCtgryConfig method.......");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		List<Integer> lLstChangeStmntCtgryConfig = null;

		try {
			setSessionInfo(inputMap);

			lObjPensionConfigDAO = new PensionConfigDAOImpl(null, serv.getSessionFactory());
			lLstChangeStmntCtgryConfig = lObjPensionConfigDAO.getChangeStmntCtgry(gStrLocationCode);

			inputMap.put("lLstChangeStmntCtgryConfig", lLstChangeStmntCtgryConfig);

			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("changeStmntCtgryConfig");

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;

	}

	public ResultObject showPPONoHistory(Map<String, Object> inputMap) {

		gLogger.info("In showPPONoHistory method.......");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		String lStrPensionerCode = null;
		List lLstPPONoHistory = null;
		Iterator lObjIterator = null;
		Object[] lObjArr = null;
		List<String> lLstOldPpoNo = new ArrayList<String>();
		List<String> lLstNewPpoNo = new ArrayList<String>();
		List<String> lLstUpdatedDate = new ArrayList<String>();
		try {
			setSessionInfo(inputMap);
			lStrPensionerCode = StringUtility.getParameter("PnsnrCode", request);
			lObjPensionConfigDAO = new PensionConfigDAOImpl(null, serv.getSessionFactory());
			lLstPPONoHistory = lObjPensionConfigDAO.getPPONoHistory(lStrPensionerCode);
			if (!lLstPPONoHistory.isEmpty() && lLstPPONoHistory.size() > 0) {
				lObjIterator = lLstPPONoHistory.iterator();
				while (lObjIterator.hasNext()) {
					lObjArr = (Object[]) lObjIterator.next();
					if (lObjArr[0] != null) {
						lLstOldPpoNo.add((String) lObjArr[0]);
					} else {
						lLstOldPpoNo.add("");
					}
					if (lObjArr[1] != null) {
						lLstNewPpoNo.add((String) lObjArr[1]);
					} else {
						lLstNewPpoNo.add("");
					}
					if (lObjArr[2] != null) {
						lLstUpdatedDate.add(DateUtilities.stringFromDate((Date) lObjArr[2]));
					} else {
						lLstUpdatedDate.add("");
					}
				}

			}
			inputMap.put("LoopIndex", lLstOldPpoNo.size());
			inputMap.put("lLstOldPpoNo", lLstOldPpoNo);
			inputMap.put("lLstNewPpoNo", lLstNewPpoNo);
			inputMap.put("lLstUpdatedDate", lLstUpdatedDate);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("showPPONoHistory");

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject loadBankBranchGroup(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		CommonPensionDAO lObjCommonPensionDAO = null;
		List<ComboValuesVO> lLstBank = null;
		List<ComboValuesVO> lLstBankGroup = null;
		try {
			setSessionInfo(inputMap);
			lObjPensionConfigDAO = new PensionConfigDAOImpl(MstBankGrp.class, serv.getSessionFactory());
			lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			lLstBank = lObjCommonPensionDAO.getAuditorBankCodeList(gLngPostId, gStrLocationCode);
			lLstBankGroup = lObjPensionConfigDAO.getBankGroupOfAuditor(gLngPostId, gStrLocationCode);
			inputMap.put("lLstBank", lLstBank);
			inputMap.put("lLstBankGroup", lLstBankGroup);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("bankGroupMpg");
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject saveBankGroupDtls(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		String lStrAction = null;
		String lStrGroupId = null;
		String lStrGroupName = null;
		String[] lArrBankBranchGrpDtls = null;
		String[] lArrBankBranch = null;
		Long lLngGrpId = null;
		String lStrBankCode = null;
		String lStrBranchCode = null;
		Long lLngBankBrnchGrpId = null;
		MstBankGrp lObjMstBankGrp = null;
		RltBankBranchGrp lObjRltBankBranchGrp = null;
		HibernateTemplate lHit = null;
		List<RltBankBranchGrp> lLstRltBankBranchGrp = new ArrayList<RltBankBranchGrp>();
		String lStrResult = null;
		StringBuilder lSBStatus = new StringBuilder();
		String lStrSaveStatus = "";
		try {
			setSessionInfo(inputMap);
			lStrAction = StringUtility.getParameter("userAction", request);
			lStrGroupId = StringUtility.getParameter("groupId", request);
			lStrGroupName = StringUtility.getParameter("txtGrpName", request);
			lArrBankBranchGrpDtls = StringUtility.getParameterValues("bankBranchGrpDtls", request);
			lObjPensionConfigDAO = new PensionConfigDAOImpl(MstBankGrp.class, serv.getSessionFactory());
			if ("Add".equals(lStrAction)) {
				lStrSaveStatus = "added";
				lLngGrpId = IFMSCommonServiceImpl.getNextSeqNum("mst_bank_grp", inputMap);
				lObjMstBankGrp = new MstBankGrp();
				lObjMstBankGrp.setBankGrpId(lLngGrpId);
				lObjMstBankGrp.setGroupName(lStrGroupName);
				lObjMstBankGrp.setAuditorPostId(gLngPostId);
				lObjMstBankGrp.setLocationCode(gStrLocationCode);
				lObjMstBankGrp.setCreatedDate(gDate);
				lObjMstBankGrp.setCreatedPostId(gLngPostId);
				lObjMstBankGrp.setCreatedUserId(gLngUserId);
				lObjMstBankGrp.setDbId(gDbId);
				lObjPensionConfigDAO.create(lObjMstBankGrp);

			} else if ("Delete".equals(lStrAction)) {
				lStrSaveStatus = "deleted";
				lLngGrpId = Long.valueOf(lStrGroupId);
			} else {
				lStrSaveStatus = "updated";
				lLngGrpId = Long.valueOf(lStrGroupId);
			}

			lHit = new HibernateTemplate(serv.getSessionFactory());
			lObjPensionConfigDAO = new PensionConfigDAOImpl(RltBankBranchGrp.class, serv.getSessionFactory());
			lObjPensionConfigDAO.deleteBankBranchGrpDtls(lLngGrpId);

			/**
			 * Updateing/Inserting bank branch rlt details for Add or Update
			 * user action.
			 */
			if (!"Delete".equals(lStrAction)) {
				if (lArrBankBranchGrpDtls != null && lArrBankBranchGrpDtls.length > 0) {
					for (int i = 0; i < lArrBankBranchGrpDtls.length; i++) {
						lLngBankBrnchGrpId = IFMSCommonServiceImpl.getNextSeqNum("rlt_bank_branch_grp", inputMap);
						lArrBankBranch = lArrBankBranchGrpDtls[i].split("~");
						lStrBankCode = lArrBankBranch[0];
						lStrBranchCode = lArrBankBranch[1];
						lObjRltBankBranchGrp = new RltBankBranchGrp();
						lObjRltBankBranchGrp.setBranchGrpId(lLngBankBrnchGrpId);
						lObjRltBankBranchGrp.setBankGrpId(lLngGrpId);
						lObjRltBankBranchGrp.setBankCode(lStrBankCode);
						lObjRltBankBranchGrp.setBranchCode(lStrBranchCode);
						lObjRltBankBranchGrp.setAuditorPostId(gLngPostId);
						lObjRltBankBranchGrp.setLocationCode(gStrLocationCode);
						lObjRltBankBranchGrp.setCreatedDate(gDate);
						lObjRltBankBranchGrp.setCreatedPostId(gLngPostId);
						lObjRltBankBranchGrp.setCreatedUserId(gLngUserId);
						lObjRltBankBranchGrp.setDbId(gDbId);
						lLstRltBankBranchGrp.add(lObjRltBankBranchGrp);
					}
					lHit.saveOrUpdateAll(lLstRltBankBranchGrp);
				}
			}

			/**
			 * Deleting bank group entry from mstbankgrp table
			 */
			if ("Delete".equals(lStrAction)) {
				lObjPensionConfigDAO = new PensionConfigDAOImpl(MstBankGrp.class, serv.getSessionFactory());
				lObjMstBankGrp = (MstBankGrp) lObjPensionConfigDAO.read(lLngGrpId);
				if (lObjMstBankGrp != null) {
					lObjPensionConfigDAO.delete(lObjMstBankGrp);
				}
			}
			lSBStatus.append("<XMLDOC>");
			lSBStatus.append("<STATUS>");
			lSBStatus.append(lStrSaveStatus);
			lSBStatus.append("</STATUS>");
			lSBStatus.append("</XMLDOC>");
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
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

	public ResultObject getBankGroupDtlsByGrpId(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		String lStrGroupId = null;
		Long lLngGrpId = null;
		StringBuilder lSbRes = null;
		String lStrResult = "";
		try {
			setSessionInfo(inputMap);
			lStrGroupId = StringUtility.getParameter("grpId", request).trim();
			if (!"".equals(lStrGroupId)) {
				lObjPensionConfigDAO = new PensionConfigDAOImpl(RltBankBranchGrp.class, serv.getSessionFactory());
				lSbRes = lObjPensionConfigDAO.getBankGroupDtlsByGrpId(Long.valueOf(lStrGroupId), gLngPostId, gStrLocationCode);
				lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSbRes.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");
				resObj.setResultValue(inputMap);
			}

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject validateBranchForGrp(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		String lStrGroupName = null;
		String lStrBankCode = null;
		String lStrBranchCode = null;
		StringBuilder lSBStatus = new StringBuilder();
		String lStrResult = null;
		try {
			setSessionInfo(inputMap);
			lStrBankCode = StringUtility.getParameter("bankCode", request);
			lStrBranchCode = StringUtility.getParameter("branchCode", request);
			lObjPensionConfigDAO = new PensionConfigDAOImpl(RltBankBranchGrp.class, serv.getSessionFactory());
			lStrGroupName = lObjPensionConfigDAO.getGrpNameFromBranchCode(lStrBankCode, lStrBranchCode, gStrLocationCode, gLngPostId);
			lSBStatus.append("<XMLDOC>");
			if (lStrGroupName.trim().length() > 0) {
				lSBStatus.append("<GRPNAME>");
				lSBStatus.append(lStrGroupName);
				lSBStatus.append("</GRPNAME>");
			}
			lSBStatus.append("</XMLDOC>");
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
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

	public ResultObject loadMainCategoryConfig(Map<String, Object> inputMap) {

		gLogger.info("In loadMainCategoryConfig method.......");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		List lLstMainCategoryConfigDtls = null;
		Integer lIntTotalRecords = null;
		List<Integer> lLstMainCategory = null;
		try {
			setSessionInfo(inputMap);
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			lObjPensionConfigDAO = new PensionConfigDAOImpl(null, serv.getSessionFactory());
			lIntTotalRecords = lObjPensionConfigDAO.getMainCategoryConfigDtlsCount(gLngLangId, displayTag);
			lLstMainCategoryConfigDtls = lObjPensionConfigDAO.getMainCategoryConfigDtls(gLngLangId, displayTag);
			lLstMainCategory = lObjPensionConfigDAO.getAllMainCtgryId(gLngLangId);

			inputMap.put("totalRecords", lIntTotalRecords);
			inputMap.put("lLstMainCategoryConfigDtls", lLstMainCategoryConfigDtls);
			inputMap.put("lLstMainCategoryId", lLstMainCategory);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("mainCategoryConfig");
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//e.printStackTrace();

		}
		return resObj;

	}

	public ResultObject saveMainCategoryConfig(Map<String, Object> inputMap) {

		gLogger.info("In saveMainCategoryConfig method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		StringBuilder lStrBldXML = null;
		String lStrResult = null;
		String lStrTransMode = null;
		MstPensionMainCategory lObjMstPensionMainCategory = null;
		Long lLngMainCatId = null;
		String lStrBillType = null;
		try {
			setSessionInfo(inputMap);
			lObjMstPensionMainCategory = new MstPensionMainCategory();
			lStrTransMode = (String) inputMap.get("Mode");
			lObjMstPensionMainCategory = (MstPensionMainCategory) inputMap.get("lObjMstPensionMainCategory");
			lObjPensionConfigDAO = new PensionConfigDAOImpl(MstPensionMainCategory.class, serv.getSessionFactory());
			if (lStrTransMode.equalsIgnoreCase("Add")) {
				if (lObjMstPensionMainCategory != null) {
					lLngMainCatId = lObjPensionConfigDAO.getMaxMainCatId(gLngLangId);
					if (lLngMainCatId == 1L) {
						lObjMstPensionMainCategory.setMainCatId(lLngMainCatId);
					} else {
						lObjMstPensionMainCategory.setMainCatId(lLngMainCatId + 1L);
					}
					lObjPensionConfigDAO.create(lObjMstPensionMainCategory);
					gLogger.info("Record Inserted in table mst_pension_main_category successfully.");
				}
			} else if (lStrTransMode.equalsIgnoreCase("Update")) {
				// Update Main Category

				String lStrMainCategoryDesc = StringUtility.getParameter("txtMainCtgryDesc", request);
				String lStrPnsnSchemeCode = StringUtility.getParameter("txtPensionSchemeCode", request).trim();
				String lStrCvpSchemeCode = StringUtility.getParameter("txtCvpSchemeCode", request).trim();
				String lStrDcrgSchemeCode = StringUtility.getParameter("txtDcrgSchemeCode", request).trim();

				if (!"".equals(lStrMainCategoryDesc) && lStrMainCategoryDesc.length() > 0) {
					lObjMstPensionMainCategory.setMainCatDesc(lStrMainCategoryDesc);
				}
				if (!"".equals(lStrPnsnSchemeCode) && lStrPnsnSchemeCode.length() > 0) {
					lObjMstPensionMainCategory.setSchemeCodePension(lStrPnsnSchemeCode);
				}
				if (!"".equals(lStrCvpSchemeCode) && lStrCvpSchemeCode.length() > 0) {
					lObjMstPensionMainCategory.setSchemeCodeCVP(lStrCvpSchemeCode);
				}
				if (!"".equals(lStrDcrgSchemeCode) && lStrDcrgSchemeCode.length() > 0) {
					lObjMstPensionMainCategory.setSchemeCodeDCRG(lStrDcrgSchemeCode);
				}

				lObjPensionConfigDAO.update(lObjMstPensionMainCategory);

				// update Subcategory according to main category(description and
				// main category description in mst_pension_headcode)
				lObjPensionConfigDAO = new PensionConfigDAOImpl(MstPensionHeadcode.class, serv.getSessionFactory());
				List<MstPensionHeadcode> lLstMstPensionHeadcode = lObjPensionConfigDAO.getListByColumnAndValue("mainCatCode", lObjMstPensionMainCategory.getMainCatId().intValue());
				ArrayList<Long> lArrayLstHeadCode = new ArrayList<Long>();
				if (lLstMstPensionHeadcode != null && !lLstMstPensionHeadcode.isEmpty()) {
					for (MstPensionHeadcode lObjMstPensionHeadcode : lLstMstPensionHeadcode) {
						lObjMstPensionHeadcode.setMainCatDesc(lStrMainCategoryDesc.trim());
						lObjMstPensionHeadcode.setDescription(lStrMainCategoryDesc.trim().concat("-").concat(lObjMstPensionHeadcode.getSeries()));
						lObjMstPensionHeadcode.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
						lObjMstPensionHeadcode.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
						lObjMstPensionHeadcode.setUpdatedDate(gDate);
						lArrayLstHeadCode.add(lObjMstPensionHeadcode.getHeadCode().longValue());
						lObjPensionConfigDAO.update(lObjMstPensionHeadcode);
					}
				}
				// update head code Chargable table according to main
				// category(scheme code of pension,cvp,dcrg in
				// rlt_pension_headcode_chargable)
				lObjPensionConfigDAO = new PensionConfigDAOImpl(RltPensionHeadcodeChargable.class, serv.getSessionFactory());
				for (Long lLngHeadcode : lArrayLstHeadCode) {
					List<RltPensionHeadcodeChargable> lLstRltPensionHeadcodeChargable = lObjPensionConfigDAO.getListByColumnAndValue("headCode", lLngHeadcode);
					if (lLstRltPensionHeadcodeChargable != null && !lLstRltPensionHeadcodeChargable.isEmpty()) {
						for (RltPensionHeadcodeChargable lObjRltPensionHeadcodeChargable : lLstRltPensionHeadcodeChargable) {
							lStrBillType = lObjRltPensionHeadcodeChargable.getBillType().trim();

							if (lStrBillType.equals("PENSION")) {
								lObjRltPensionHeadcodeChargable.setSchemeCode(lStrPnsnSchemeCode.trim());
							}
							if (lStrBillType.equals("CVP")) {
								lObjRltPensionHeadcodeChargable.setSchemeCode(lStrCvpSchemeCode.trim());
							}
							if (lStrBillType.equals("DCRG")) {
								lObjRltPensionHeadcodeChargable.setSchemeCode(lStrDcrgSchemeCode.trim());
							}
							lObjRltPensionHeadcodeChargable.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
							lObjRltPensionHeadcodeChargable.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
							lObjRltPensionHeadcodeChargable.setUpdatedDate(gDate);
							lObjPensionConfigDAO.update(lObjRltPensionHeadcodeChargable);
						}
					}
				}
			} else // delete
			{
				String lStrMainCatId = StringUtility.getParameter("MainCatId", request);
				String[] lStrMainCatIdArr = lStrMainCatId.split("~");
				ArrayList<Long> lArrLstMainCatId = new ArrayList<Long>();
				ArrayList<Long> lArrLstHeadCode = new ArrayList<Long>();
				for (int lIntCnt = 0; lIntCnt < lStrMainCatIdArr.length; lIntCnt++) {
					lObjMstPensionMainCategory = (MstPensionMainCategory) lObjPensionConfigDAO.read(Long.valueOf(lStrMainCatIdArr[lIntCnt].trim()));
					lArrLstMainCatId.add(lObjMstPensionMainCategory.getMainCatId());
					lObjPensionConfigDAO.delete(lObjMstPensionMainCategory);
				}
				for (Long lLngMainCtgryId : lArrLstMainCatId) {
					List<MstPensionHeadcode> lLstMstPensionHeadcode = lObjPensionConfigDAO.getListByColumnAndValue("mainCatCode", lLngMainCtgryId);
					if (lLstMstPensionHeadcode != null && !lLstMstPensionHeadcode.isEmpty()) {
						for (MstPensionHeadcode lObjMstPensionHeadcode : lLstMstPensionHeadcode) {
							lArrLstHeadCode.add(lObjMstPensionHeadcode.getHeadCode().longValue());
							lObjPensionConfigDAO.delete(lObjMstPensionHeadcode);
						}
					}
				}
				for (Long lLngHeadCode : lArrLstHeadCode) {
					List<RltPensionHeadcodeChargable> lLstRltPensionHeadcodeChargable = lObjPensionConfigDAO.getListByColumnAndValue("mainCatCode", lLngHeadCode);
					if (lLstRltPensionHeadcodeChargable != null && !lLstRltPensionHeadcodeChargable.isEmpty()) {
						for (RltPensionHeadcodeChargable lObjRltPensionHeadcodeChargable : lLstRltPensionHeadcodeChargable) {
							lObjPensionConfigDAO.delete(lObjRltPensionHeadcodeChargable);
						}
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
			gLogger.error("Error is:" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			//e.printStackTrace();

		}
		return objRes;

	}

	public ResultObject loadSchemeCode(Map<String, Object> inputMap) {

		gLogger.info("In loadSchemeCode method.......");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		List<MstScheme> lLstMstSchemeVO = new ArrayList<MstScheme>();
		Integer lIntTotalRecords = 0;
		try {
			setSessionInfo(inputMap);
			lObjPensionConfigDAO = new PensionConfigDAOImpl(null, serv.getSessionFactory());
			String lStrElementId = StringUtility.getParameter("elementId", request);
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			lIntTotalRecords = lObjPensionConfigDAO.getMstSchemeCount(gLngLangId);
			if (lIntTotalRecords > 0) {
				lLstMstSchemeVO = lObjPensionConfigDAO.getMstSchemeList(displayTag, gLngLangId);
			}

			inputMap.put("lLstMstSchemeVO", lLstMstSchemeVO);
			inputMap.put("ElementId", lStrElementId);
			inputMap.put("totalRecords", lIntTotalRecords);
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		resObj.setViewName("SchemeCodePopUp");
		return resObj;

	}

	public ResultObject getTotalPnsrsOfBranch(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		String lStrBankCode = null;
		String lStrBranchCode = null;
		StringBuilder lSBStatus = new StringBuilder();
		String lStrResult = null;
		Long lLngTotalPnsrsOfBranch = 0l;
		try {
			setSessionInfo(inputMap);
			lStrBankCode = StringUtility.getParameter("bankCode", request);
			lStrBranchCode = StringUtility.getParameter("branchCode", request);
			lObjPensionConfigDAO = new PensionConfigDAOImpl(MstPensionerDtls.class, serv.getSessionFactory());
			lLngTotalPnsrsOfBranch = lObjPensionConfigDAO.getTotalPnsrsOfBranch(lStrBranchCode, lStrBankCode, gStrLocationCode);
			lSBStatus.append("<XMLDOC>");
			lSBStatus.append("<TOTALPNSRCOUNT>");
			lSBStatus.append(lLngTotalPnsrsOfBranch);
			lSBStatus.append("</TOTALPNSRCOUNT>");
			lSBStatus.append("</XMLDOC>");
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
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

	public ResultObject vaildateBnkBrnchWithAuditor(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		String lStrBankCode = null;
		String[] lStrBranchCodeArr = null;
		String lStrBranchCode = null;
		String lStrSelectedBranches = null;
		Long lLngAuditorPostId = 0l;
		List lLstBnkBrnchDtls = null;
		List<Long> lLstBranchCode = new ArrayList<Long>();
		List<Long> lLstAuditorPostId = new ArrayList<Long>();
		List<String> lLstAuditorName = new ArrayList<String>();
		List<String> lLstBranchName = new ArrayList<String>();
		List<String> lLstBankName = new ArrayList<String>();
		String lStrAuditorName = null;
		String lStrBranchName = null;
		String lStrBankName = null;
		// String lStrAuditorPostId = null;
		StringBuilder lSBStatus = new StringBuilder();
		String lStrResult = null;
		try {
			setSessionInfo(inputMap);
			lStrBankCode = StringUtility.getParameter("BankCode", request).trim();
			lStrSelectedBranches = StringUtility.getParameter("selectedBranches", request);
			lStrBranchCodeArr = lStrSelectedBranches.split("~");
			// lStrAuditorPostId = StringUtility.getParameter("AuditorPostId",
			// request);
			lObjPensionConfigDAO = new PensionConfigDAOImpl(null, serv.getSessionFactory());

			for (int lIntCnt = 0; lIntCnt < lStrBranchCodeArr.length; lIntCnt++) {
				lLngAuditorPostId = lObjPensionConfigDAO.getAuditorPostId(lStrBranchCodeArr[lIntCnt].trim(), lStrBankCode, gStrLocationCode);
				if (lLngAuditorPostId != 0l) {
					lLstAuditorPostId.add(lLngAuditorPostId);
					lStrBranchCode = lStrBranchCodeArr[lIntCnt].trim();
					lLstBranchCode.add(Long.valueOf(lStrBranchCode));

				}

			}
			if (!lLstAuditorPostId.isEmpty() && lLstAuditorPostId.size() > 0) {
				lLstBnkBrnchDtls = lObjPensionConfigDAO.getBnkBrnchDtls(lLstAuditorPostId, lLstBranchCode);
				Iterator lObjIterator = lLstBnkBrnchDtls.iterator();
				Object[] lArrObj = null;
				while (lObjIterator.hasNext()) {
					lArrObj = (Object[]) lObjIterator.next();
					lStrAuditorName = (String) lArrObj[0];
					lStrBranchName = (String) lArrObj[1];
					lStrBankName = (String) lArrObj[2];
					lLstAuditorName.add(lStrAuditorName.concat("~"));
					lLstBranchName.add(lStrBranchName.concat("~"));
					lLstBankName.add(lStrBankName.concat("~"));
				}
			}

			lSBStatus.append("<XMLDOC>");
			lSBStatus.append("<AuditorNameList>");
			if (!lLstAuditorName.isEmpty() && lLstAuditorName.size() > 0) {
				lSBStatus.append(lLstAuditorName);
			} else {
				lSBStatus.append("null");
			}
			lSBStatus.append("</AuditorNameList>");
			lSBStatus.append("<BranchNameList>");
			if (!lLstBranchName.isEmpty() && lLstBranchName.size() > 0) {
				lSBStatus.append(lLstBranchName);
			} else {
				lSBStatus.append("null");
			}
			lSBStatus.append("</BranchNameList>");
			lSBStatus.append("<TempFlagVal>");
			if (!lLstAuditorName.isEmpty() && lLstAuditorName.size() > 0) {
				lSBStatus.append(false);
			} else {
				lSBStatus.append(true);
			}
			lSBStatus.append("</TempFlagVal>");
			lSBStatus.append("<BankNameList>");
			if (!lLstBankName.isEmpty() && lLstBankName.size() > 0) {
				lSBStatus.append(lLstBankName);
			} else {
				lSBStatus.append("null");
			}
			lSBStatus.append("</BankNameList>");

			lSBStatus.append("</XMLDOC>");
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
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

	public ResultObject checkSchemeCode(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionConfigDAO lObjPensionConfigDAO = null;
		String lStrSchemeCode = null;
		List lLstResult = null;
		StringBuilder lStrBldXML = new StringBuilder();
		try {
			setSessionInfo(inputMap);

			lStrSchemeCode = StringUtility.getParameter("SchemeCode", request);
			lObjPensionConfigDAO = new PensionConfigDAOImpl(null, serv.getSessionFactory());
			if (!"".equals(lStrSchemeCode)) {

				lLstResult = lObjPensionConfigDAO.checkSchemeCode(lStrSchemeCode);
			}

			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<ISEXISTS>");
			if (lLstResult.isEmpty()) {
				lStrBldXML.append("N");
			} else {
				lStrBldXML.append("Y");
			}
			lStrBldXML.append("</ISEXISTS>");
			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
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

}
