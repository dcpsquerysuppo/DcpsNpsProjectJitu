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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
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
public class RevisedPensionCaseVOGeneratorImpl  extends ServiceImpl implements RevisedPensionCaseVOGenerator{

	
	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for EmpId */
	Long gLngEmpId = null;

	/* Global Variable for Location Id */
	String gStrLocId = null;

	/* Global Variable for DB Id */
	Long gLngDBId = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	Log gLogger = LogFactory.getLog(getClass());

	Date gCurrDate = null;

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	// Sets session information in the global variables
	private void setSessionInfo(Map inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngEmpId = SessionHelper.getEmpId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocId = SessionHelper.getLocationCode(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gCurrDate = DBUtility.getCurrentDateFromDB();
		serv = (ServiceLocator) inputMap.get("serviceLocator");
	}

	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.service.RevisedPensionCaseVOGenerator#generateMap(java.util.Map)
	 */
	public ResultObject generateMap(Map inputMap) {
		
		gLogger.info("In generateMap of RevisedPensionCaseVOGeneratorImpl........");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List<TrnPensionerRivisionDtls> lLstTrnPensionerRivisionDtlsVO=new ArrayList<TrnPensionerRivisionDtls>();
		TrnPensionerRivisionDtls lObjTrnPensionerRivisionDtlsVO=new TrnPensionerRivisionDtls();
		//TrnPensionRqstHdr lObjTrnPensionRqstHdrVO=new TrnPensionRqstHdr();
		String lStrMode=null;
		try {
			setSessionInfo(inputMap);
			String lStrPensionerCode=StringUtility.getParameter("hdnPnsnrCode", request).trim();
			String lStrRevisionFlag=StringUtility.getParameter("RevisionFlag", request).trim();
			
			RevisedPensionCaseDAO lObjRevisedPensionCaseDAO = new RevisedPensionCaseDAOImpl(TrnPensionerRivisionDtls.class, serv.getSessionFactory());
			lLstTrnPensionerRivisionDtlsVO=lObjRevisedPensionCaseDAO.getTrnPensionerRevisionDtlsVO(lStrPensionerCode, gStrLocationCode);
			if (lLstTrnPensionerRivisionDtlsVO != null && !lLstTrnPensionerRivisionDtlsVO.isEmpty())
			{
				lStrMode="Update";
				for(int lIntCnt=0;lIntCnt<lLstTrnPensionerRivisionDtlsVO.size();lIntCnt++)
				{
					lObjTrnPensionerRivisionDtlsVO=lLstTrnPensionerRivisionDtlsVO.get(lIntCnt);
					lObjRevisedPensionCaseDAO.delete(lObjTrnPensionerRivisionDtlsVO);
				}
			}
			else
			{
				lStrMode="Add";
			}
			lLstTrnPensionerRivisionDtlsVO=new ArrayList<TrnPensionerRivisionDtls>();
			
			lLstTrnPensionerRivisionDtlsVO = generateTrnPensionerRivisionDtlsVO(inputMap);
			
//			if("A".equals(lStrRevisionFlag) && !lLstTrnPensionerRivisionDtlsVO.isEmpty())
//			{
//				int lIntCnt=lLstTrnPensionerRivisionDtlsVO.size();
//				lObjTrnPensionerRivisionDtlsVO=lLstTrnPensionerRivisionDtlsVO.get(lIntCnt-1);
//				PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
//				lObjTrnPensionRqstHdrVO=lObjPhysicalCaseInwardDAO.getTrnPensionRqstHdrVO(lStrPensionerCode);
//				lObjTrnPensionRqstHdrVO.setRopType(lObjTrnPensionerRivisionDtlsVO.getRopType());
//				lObjTrnPensionRqstHdrVO.setBasicPensionAmount(lObjTrnPensionerRivisionDtlsVO.getBasicPension());
//				lObjTrnPensionRqstHdrVO.setCvpAmount(lObjTrnPensionerRivisionDtlsVO.getCvpAmount());
//				lObjTrnPensionRqstHdrVO.setCvpMonthlyAmount(lObjTrnPensionerRivisionDtlsVO.getCvpMonthlyAmount());
//				lObjTrnPensionRqstHdrVO.setFp1Amount(lObjTrnPensionerRivisionDtlsVO.getFp1Amount());
//				lObjTrnPensionRqstHdrVO.setFp2Amount(lObjTrnPensionerRivisionDtlsVO.getFp2Amount());
//				lObjTrnPensionRqstHdrVO.setDcrgAmount(lObjTrnPensionerRivisionDtlsVO.getDcrgAmount());
//			}
			inputMap.put("lLstTrnPensionerRivisionDtlsVO", lLstTrnPensionerRivisionDtlsVO);
			//inputMap.put("TrnPensionRqstHdrVO", lObjTrnPensionRqstHdrVO);
			inputMap.put("RevisionFlag", lStrRevisionFlag);
			inputMap.put("Mode", lStrMode);
	
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);

		}
		return objRes;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.service.RevisedPensionCaseVOGenerator#generateTrnPensionerRivisionDtlsVO(java.util.Map)
	 */
	public List<TrnPensionerRivisionDtls> generateTrnPensionerRivisionDtlsVO(Map<String, Object> inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		TrnPensionerRivisionDtls lObjTrnPensionerRivisionDtlsVO=new TrnPensionerRivisionDtls();
		List<TrnPensionerRivisionDtls> lLstTrnPensionerRivisionDtlsVO =new ArrayList<TrnPensionerRivisionDtls>();
		setSessionInfo(inputMap);
		String lStrCurrentDate=lObjSimpleDateFormat.format(gCurrDate);
		
		try {
			String[] lArrStrRopType = StringUtility.getParameterValues("cmbRopType", request);
			String[] lArrStrRevisionDate = StringUtility.getParameterValues("txtRevisionDate", request);
			String[] lArrStrPnsnSanctionAmt = StringUtility.getParameterValues("txtPensionSanctionAmt", request);
			String[] lArrStrCvpAmount = StringUtility.getParameterValues("txtCvpAmount", request);
			String[] lArrStrCvpMonthlyAmt = StringUtility.getParameterValues("txtCvpMonthlyAmount", request);
			String[] lArrStrFp1Amount = StringUtility.getParameterValues("txtFp1Amount", request);
			String[] lArrStrFp2Amount=StringUtility.getParameterValues("txtFp2Amount", request);
			String[] lArrStrDcrgAmount=StringUtility.getParameterValues("txtDcrgAmount", request);
			String[] lArrStrRevisionFlag=StringUtility.getParameterValues("hdnRevisionFlag", request);
			String lStrPensionerCode=StringUtility.getParameter("hdnPnsnrCode", request).trim();
			String lStrRevisionFlag=StringUtility.getParameter("RevisionFlag", request).trim();
				
			if(lArrStrRopType.length>0)
			{
			 for (int lIntCnt = 0; lIntCnt < lArrStrRopType.length; lIntCnt++) {
				lObjTrnPensionerRivisionDtlsVO=new TrnPensionerRivisionDtls();
				lObjTrnPensionerRivisionDtlsVO.setDbId(new BigDecimal(gLngDBId));
				lObjTrnPensionerRivisionDtlsVO.setLocationCode(gStrLocationCode);
				lObjTrnPensionerRivisionDtlsVO.setPensionerCode(lStrPensionerCode);
				lObjTrnPensionerRivisionDtlsVO.setCreatedUserId(new BigDecimal(gLngUserId));
				lObjTrnPensionerRivisionDtlsVO.setCreatedPostId(new BigDecimal(gLngPostId));
				lObjTrnPensionerRivisionDtlsVO.setCreatedDate(gCurrDate);
				
				if (lArrStrRopType[lIntCnt] != "-1"	&& lArrStrRopType[lIntCnt].trim().length() > 0) {
					lObjTrnPensionerRivisionDtlsVO.setRopType(lArrStrRopType[lIntCnt].trim());
				}

				if (lArrStrRevisionDate[lIntCnt] != "" && lArrStrRevisionDate[lIntCnt].trim().length() > 0) {
					lObjTrnPensionerRivisionDtlsVO.setRivisionDate(lObjSimpleDateFormat.parse(lArrStrRevisionDate[lIntCnt].trim()));
				}

				if (lArrStrPnsnSanctionAmt[lIntCnt] != ""	&& lArrStrPnsnSanctionAmt[lIntCnt].trim().length() > 0) {
					lObjTrnPensionerRivisionDtlsVO.setBasicPension(new BigDecimal(lArrStrPnsnSanctionAmt[lIntCnt].trim()));
				}

				if (lArrStrCvpAmount[lIntCnt] != "" && lArrStrCvpAmount[lIntCnt].trim().length() > 0) {
					lObjTrnPensionerRivisionDtlsVO.setCvpAmount(new BigDecimal(lArrStrCvpAmount[lIntCnt].trim()));
				}

				if (lArrStrCvpMonthlyAmt[lIntCnt] != ""	&& lArrStrCvpMonthlyAmt[lIntCnt].trim().length() != 0) {
					lObjTrnPensionerRivisionDtlsVO.setCvpMonthlyAmount(new BigDecimal(lArrStrCvpMonthlyAmt[lIntCnt].trim()));
				}
				
				if (lArrStrFp1Amount[lIntCnt] != ""	&& lArrStrFp1Amount[lIntCnt].trim().length() != 0) {
					lObjTrnPensionerRivisionDtlsVO.setFp1Amount(new BigDecimal(lArrStrFp1Amount[lIntCnt].trim()));
				}
				
				if (lArrStrFp2Amount[lIntCnt] != ""	&& lArrStrFp2Amount[lIntCnt].trim().length() != 0) {
					lObjTrnPensionerRivisionDtlsVO.setFp2Amount(new BigDecimal(lArrStrFp2Amount[lIntCnt].trim()));
				}
				if (lArrStrDcrgAmount[lIntCnt] != ""	&& lArrStrDcrgAmount[lIntCnt].trim().length() != 0) {
					lObjTrnPensionerRivisionDtlsVO.setDcrgAmount(new BigDecimal(lArrStrDcrgAmount[lIntCnt].trim()));
				}
				if("S".equals(lStrRevisionFlag))
				{
					if (lArrStrRevisionFlag[lIntCnt].trim().length()==0) {
						lObjTrnPensionerRivisionDtlsVO.setRevisionFlag("S");
					}
					else
					{
						lObjTrnPensionerRivisionDtlsVO.setRevisionFlag(lArrStrRevisionFlag[lIntCnt]);
					}
				}
				if("A".equals(lStrRevisionFlag))
				{
					lObjTrnPensionerRivisionDtlsVO.setRevisionFlag("A");
				}
				lLstTrnPensionerRivisionDtlsVO.add(lObjTrnPensionerRivisionDtlsVO);

			  }
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw e;

		}
		return lLstTrnPensionerRivisionDtlsVO;
	}

}
