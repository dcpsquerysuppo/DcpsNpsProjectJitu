/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 29, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.dao.RevisedPensionCaseDAO;
import com.tcs.sgv.pensionpay.dao.RevisedPensionCaseDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionerRivisionDtls;


/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Jun 29, 2011
 */
public class RevisedPensionCaseServiceImpl extends ServiceImpl implements RevisedPensionCaseService{

	private Long gLngPostId = null;

	private Long gLngUserId = null;

	private Long gLngLangId = null;

	private Log gLogger = LogFactory.getLog(getClass());

	private Date gCurrDate = null;

	private String gStrLocCode = null;

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private void setSessionInfo(Map<String, Object> inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocCode = SessionHelper.getLocationCode(inputMap);
		gLngLangId = SessionHelper.getLangId(inputMap);
		gCurrDate = DBUtility.getCurrentDateFromDB();
		serv = (ServiceLocator) inputMap.get("serviceLocator");
	}
	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.service.RevisedPensionCaseService#loadPensionerRevisionDtls(java.util.Map)
	 */
	public ResultObject loadPensionerRevisionDtls(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<TrnPensionerRivisionDtls> lLstTrnPensionerRevisionDtlsVO = new ArrayList<TrnPensionerRivisionDtls>();
		String lStrPpoNo=null;
		String lStrPensionerCode=null;
		String lStrPpoInwardDate=null;
		String lStrPensionerName=null;
		String lStrRopType=null;
		String lStrPnsnSanctionAmt=null;
		String lStrCommutationAmt=null;
		String lStrCvpMonthlyAmt=null;
		String lStrFp1Amount=null;
		String lStrFp2Amount=null;
		String lStrDcrgAmount=null;
		try {
			RevisedPensionCaseDAO lObjRevisedPensionCaseDAO = new RevisedPensionCaseDAOImpl(TrnPensionerRivisionDtls.class, serv.getSessionFactory());
			
			lStrPpoNo=StringUtility.getParameter("ppoNo", request).trim();
			lStrPensionerCode=StringUtility.getParameter("pensionerCode", request).trim();
			lStrPpoInwardDate=StringUtility.getParameter("ppoInwardDate", request).trim();
			lStrPensionerName=StringUtility.getParameter("pnsnrName", request).trim();
			lStrRopType=StringUtility.getParameter("ropType", request).trim();
			lStrPnsnSanctionAmt=StringUtility.getParameter("basicPnsnAmt", request).trim();
			lStrCommutationAmt=StringUtility.getParameter("commutationAmt", request).trim();
			lStrCvpMonthlyAmt=StringUtility.getParameter("cvpMonthlyAmt", request).trim();
			lStrFp1Amount=StringUtility.getParameter("fp1Amount", request).trim();
			lStrFp2Amount=StringUtility.getParameter("fp2Amount", request).trim();
			lStrDcrgAmount=StringUtility.getParameter("dcrgAmount", request).trim();
			lObjSimpleDateFormat.format(lObjSimpleDateFormat.parse(lStrPpoInwardDate));
			
			lLstTrnPensionerRevisionDtlsVO=lObjRevisedPensionCaseDAO.getTrnPensionerRevisionDtlsVO(lStrPensionerCode, gStrLocCode);
			
			inputMap.put("PpoNo", lStrPpoNo);
			inputMap.put("PensionerCode", lStrPensionerCode);
			inputMap.put("PpoInwardDate", lStrPpoInwardDate);
			inputMap.put("PensionerName", lStrPensionerName);
			inputMap.put("RopType", lStrRopType);
			inputMap.put("PnsnSanctionAmt", lStrPnsnSanctionAmt);
			inputMap.put("CommutationAmt", lStrCommutationAmt);
			inputMap.put("CvpMonthlyAmt", lStrCvpMonthlyAmt);
			inputMap.put("Fp1Amount", lStrFp1Amount);
			inputMap.put("Fp2Amount", lStrFp2Amount);
			inputMap.put("DcrgAmount", lStrDcrgAmount);
			inputMap.put("TotalCount", lLstTrnPensionerRevisionDtlsVO.size());
			inputMap.put("TrnPensionerRevisionDtls", lLstTrnPensionerRevisionDtlsVO);
			
			resObj.setViewName("RevisedCaseProcess");
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
	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.service.RevisedPensionCaseService#insertPensionerRevisionDtls(java.util.Map)
	 */
	public ResultObject insertPensionerRevisionDtls(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPensionerRivisionDtls lObjTrnPensionerRivisionDtlsVO=new TrnPensionerRivisionDtls();
		List<TrnPensionerRivisionDtls> lLstTrnPensionerRivisionDtlsVO=new ArrayList<TrnPensionerRivisionDtls>();
		//TrnPensionRqstHdr lObjTrnPensionRqstHdrVO=new TrnPensionRqstHdr();
		Long lLngRevisionDtlsId=null;
		StringBuilder lStrBldXML = null;
		String lStrPensionerCode=null;
		String lStrMode=null;
		String lStrRevisionFlag=null;
		try {
			RevisedPensionCaseDAO lObjRevisedPensionCaseDAO = new RevisedPensionCaseDAOImpl(TrnPensionerRivisionDtls.class, serv.getSessionFactory());
			lStrMode=inputMap.get("Mode").toString();
			lStrRevisionFlag=inputMap.get("RevisionFlag").toString();
			lLstTrnPensionerRivisionDtlsVO = (List<TrnPensionerRivisionDtls>) inputMap.get("lLstTrnPensionerRivisionDtlsVO");
			if (lLstTrnPensionerRivisionDtlsVO != null) {
				for (int lIntCount = 0; lIntCount < lLstTrnPensionerRivisionDtlsVO.size(); lIntCount++) {
					lObjTrnPensionerRivisionDtlsVO = lLstTrnPensionerRivisionDtlsVO.get(lIntCount);
					lLngRevisionDtlsId = IFMSCommonServiceImpl.getNextSeqNum("trn_pensioner_rivision_dtls", inputMap);
					lObjTrnPensionerRivisionDtlsVO.setRivisionDtlsId(lLngRevisionDtlsId);
					lStrPensionerCode=lObjTrnPensionerRivisionDtlsVO.getPensionerCode();
					lObjRevisedPensionCaseDAO.create(lObjTrnPensionerRivisionDtlsVO);
				}
			}
//			if("A".equals(lStrRevisionFlag))
//			{
//				lObjRevisedPensionCaseDAO = new RevisedPensionCaseDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
//				lObjTrnPensionRqstHdrVO=(TrnPensionRqstHdr)inputMap.get("TrnPensionRqstHdrVO");
//				lObjRevisedPensionCaseDAO.update(lObjTrnPensionRqstHdrVO);
//			}
			
			lStrBldXML = getResponseXMLDoc(lStrPensionerCode,lStrRevisionFlag,lStrMode);
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}

		return resObj;
	}

	private StringBuilder getResponseXMLDoc(String lStrPensionerCode,String lStrRevisionFlag,String lStrMode) {

		StringBuilder lStrHidPKs = new StringBuilder();
		lStrHidPKs.append("<XMLDOC>");
		lStrHidPKs.append("<MESSAGECODE>");
		if("Add".equalsIgnoreCase(lStrMode))
		{
		lStrHidPKs.append("insert");
		}
		if("Update".equalsIgnoreCase(lStrMode))
		{
		lStrHidPKs.append("update");
		}
		lStrHidPKs.append("</MESSAGECODE>");
		lStrHidPKs.append("<PNSNRCODE>" +lStrPensionerCode);
		lStrHidPKs.append("</PNSNRCODE>");
		lStrHidPKs.append("<REVISION>" +lStrRevisionFlag);
		lStrHidPKs.append("</REVISION>");
		lStrHidPKs.append("</XMLDOC>");
		
		gLogger.info("lStrHidPKs : " + lStrHidPKs);
		return lStrHidPKs;
	}

	
}
