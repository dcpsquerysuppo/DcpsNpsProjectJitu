/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 2, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;
//com.tcs.sgv.pensionpay.service.SupplementaryBillServiceImpl
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.BptmCommonServicesDAO;
import com.tcs.sgv.common.dao.BptmCommonServicesDAOImpl;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.dao.RltBillPartyDAO;
import com.tcs.sgv.common.dao.RltBillPartyDAOImpl;
import com.tcs.sgv.common.helper.ColumnVo;
import com.tcs.sgv.common.helper.ReportExportHelper;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.idgenerator.delegate.IDGenerateDelegate;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.RltBillParty;
import com.tcs.sgv.common.valueobject.TrnBillRegister;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.CVPRestorationLetterDAO;
import com.tcs.sgv.pensionpay.dao.CVPRestorationLetterDAOImpl;
import com.tcs.sgv.pensionpay.dao.NewPensionBillDAO;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAO;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.dao.MstPensionerHdrDAO;
import com.tcs.sgv.pensionpay.dao.MstPensionerHdrDAOImpl;
import com.tcs.sgv.pensionpay.dao.NewPensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.OnlinePensionCaseDAO;
import com.tcs.sgv.pensionpay.dao.OnlinePensionCaseDAOImpl;
import com.tcs.sgv.pensionpay.dao.PensionBillDAO;
import com.tcs.sgv.pensionpay.dao.PensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.dao.RltPensioncaseBillDAO;
import com.tcs.sgv.pensionpay.dao.RltPensioncaseBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.SixPayFPArrearDAO;
import com.tcs.sgv.pensionpay.dao.SixPayFPArrearDAOImpl;
import com.tcs.sgv.pensionpay.dao.SupplementaryBillDAO;
import com.tcs.sgv.pensionpay.dao.SupplementaryBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillDtlsDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillDtlsDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillHdrDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillHdrDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionRecoveryDtlsDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionRecoveryDtlsDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.HstCommutationDtls;
import com.tcs.sgv.pensionpay.valueobject.HstPnsnPmntDcrgDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.RltPensioncaseBill;
import com.tcs.sgv.pensionpay.valueobject.SupplementaryPartyDtlsVO;
import com.tcs.sgv.pensionpay.valueobject.TrnEcsDtl;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionSixpayfpArrear;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionSupplyBillDtls;
import com.tcs.sgv.web.jsp.tags.DateUtilities;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Jun 2, 2011
 */
public class SupplementaryBillServiceImpl extends ServiceImpl implements SupplementaryBillService {

	/* Global Variable for PostId */
	private Long gLngPostId = null;

	/* Global Variable for UserId */
	private Long gLngUserId = null;

	/* Glonal Variable for Location Code */
	private String gStrLocCode = null;
	
	private String gStrLocShortName = null;

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	/* Global Variable for Current Date */
	private Date gDtCurrDt = null;

	/* Global Variable for Lang ID */
	private Long gLangID = null;
	
	/* Global Variable for DB Id */
	Long gLngDBId = null;
	
	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");
	
	private ResourceBundle bundleCaseConst = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	/**
	 * View CVP Bill data after saving the bill
	 * 
	 * @param inputMap
	 * @return objRes ResultObject
	 */
	public ResultObject getSupplementaryBillData(Map<String, Object> inputMap) {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		HttpServletRequest request = null;
		String lStrStatus = bundleConst.getString("STATUS.CONTINUE");
		List<SupplementaryPartyDtlsVO> lLstPensionerDtls = null;
		SupplementaryPartyDtlsVO lObjSupplementaryPartyDtlsVO = new SupplementaryPartyDtlsVO();
		List<ComboValuesVO> lLstBanks = null;
		MstPensionerHdrDAO lObjMstPensionerHdrDAO = new MstPensionerHdrDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
		
		PensionBillDAO lObjPensionBillDAO = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
		CommonDAO lObjCommonDAO = new CommonDAOImpl(srvcLoc.getSessionFactory());
		List<CmnLookupMst> lLstRecoveryType = new ArrayList();
		String lStrCurrRoleId = "";
		String lStrElementCode = null;
		String lStrSchemeCode = "";
		BigDecimal lBgDcmlHeadCode = BigDecimal.ZERO;
		String lStrLedgerNo = "";
		String lStrPageNo = "";
		CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(srvcLoc.getSessionFactory());
		try {
			setSessionInfo(inputMap);
			lLstPensionerDtls = new ArrayList<SupplementaryPartyDtlsVO>();
			request = (HttpServletRequest) inputMap.get("requestObj");
			lStrElementCode = StringUtility.getParameter("elementId", request).trim();
			String lStrPPONo = StringUtility.getParameter("PPONo", request);
			lStrCurrRoleId = StringUtility.getParameter("CurrRole", request);
			String lStrSuplBillType = StringUtility.getParameter("SuplBillType", request);
			String lStrPnsnCode = "";
			if(!"".equals(lStrPPONo))
				lStrPnsnCode = lObjPensionBillDAO.getPnsnCodeFromPPONo(lStrPPONo.trim(), lStrStatus, gStrLocCode);
			
			lLstPensionerDtls = lObjPensionBillDAO.getSuppBillData(lStrPnsnCode, gStrLocCode,lStrSuplBillType);
			String lStrPensionerName = lObjMstPensionerHdrDAO.getPensionerName(lStrPnsnCode);
			if(!lLstPensionerDtls.isEmpty())
			{
				lObjSupplementaryPartyDtlsVO = lLstPensionerDtls.get(0);
				if(lObjSupplementaryPartyDtlsVO.getDateOfDeath() != null)
				{
					if("PENSION".equals(lStrSuplBillType) || "CVP".equals(lStrSuplBillType))
					{
						lLstPensionerDtls = new ArrayList<SupplementaryPartyDtlsVO>();
						lLstPensionerDtls = lObjPensionBillDAO.getFamilyDtlsSuppBill(lStrPnsnCode, gStrLocCode,lStrSuplBillType);
					}
					else if("DCRG".equals(lStrSuplBillType))
					{
						lLstPensionerDtls = new ArrayList<SupplementaryPartyDtlsVO>();
						List lLstResult = new ArrayList();
						lLstResult = lObjPensionBillDAO.getNomineeDtlsSuppBill(lStrPnsnCode, gStrLocCode);
						if(!lLstResult.isEmpty())
						{
							for(Integer lIntCnt=0;lIntCnt<lLstResult.size();lIntCnt++)
							{   
								lObjSupplementaryPartyDtlsVO = new SupplementaryPartyDtlsVO();
								Object[] obj=(Object[])lLstResult.get(lIntCnt);
								if(obj[0]!=null)
								{
									lObjSupplementaryPartyDtlsVO.setBeneifiicaryName(obj[0].toString());
								}
								if(obj[1]!=null)
								{
									lObjSupplementaryPartyDtlsVO.setBankCode(obj[1].toString());
								}
								if(obj[2]!=null)
								{
									lObjSupplementaryPartyDtlsVO.setBranchName(obj[2].toString());
								}
								if(obj[3]!=null)
								{
									lObjSupplementaryPartyDtlsVO.setAccountNo(obj[3].toString());
								}
								if(obj[4]!=null)
								{
									lObjSupplementaryPartyDtlsVO.setBranchCode(obj[4].toString());
								}
								if(obj[5]!=null)
								{
									lObjSupplementaryPartyDtlsVO.setMicrCode(Long.parseLong(obj[5].toString()));
								}
								lLstPensionerDtls.add(lObjSupplementaryPartyDtlsVO);
							}
						}
					}
				}
			}
			lLstRecoveryType = IFMSCommonServiceImpl.getLookupValues("RECVTYPE", gLangID, inputMap);

			OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(TrnEcsDtl.class, srvcLoc.getSessionFactory());
			// lStrCurrRoleId =
			// lObjOnlinePensionCaseDAO.getRoleByPost(gLngPostId);
			//lStrCurrRoleId = lObjCommonPensionDAO.getRoleByElement(lStrElementCode);
			inputMap.put("currRole", lStrCurrRoleId);
			// lObjIterator = lLstPensionerDtls.iterator();
			//
			// if (lLstPensionerDtls != null) {
			//
			// while (lObjIterator.hasNext()) {
			// Object[] lObj = (Object[]) lObjIterator.next();
			// if (lObj[0] != null) {
			// lStrPensionerName = lObj[0].toString();
			// }
			// if (lObj[1] != null) {
			// lStrBankCode = lObj[1].toString();
			// }
			// if (lObj[2] != null) {
			// lStrBranchName = lObj[2].toString();
			// }
			// if (lObj[3] != null) {
			// lStrAccountNo = lObj[3].toString();
			// }
			// if (lObj[4] != null) {
			// lStrBranchCode = lObj[4].toString();
			// }
			//
			// }
			// }
			if(!lLstPensionerDtls.isEmpty())
			{
				lObjSupplementaryPartyDtlsVO = new SupplementaryPartyDtlsVO();
				lObjSupplementaryPartyDtlsVO = lLstPensionerDtls.get(0);
				lStrSchemeCode = lObjSupplementaryPartyDtlsVO.getSchemeCode();
				lBgDcmlHeadCode = lObjSupplementaryPartyDtlsVO.getHeadCode();
				lStrLedgerNo = lObjSupplementaryPartyDtlsVO.getLedgerNo();
				lStrPageNo = lObjSupplementaryPartyDtlsVO.getPageNo();
			}
			lLstBanks = lObjCommonDAO.getBankList(inputMap, gLangID);
			
			inputMap.put("BankList", lLstBanks);
			inputMap.put("PPONo", lStrPPONo);
			inputMap.put("PensionerCode", lStrPnsnCode);
			inputMap.put("PnsnDtls", lLstPensionerDtls);
			inputMap.put("PensionerName", lStrPensionerName);
			inputMap.put("BillTypeId", "45");
			inputMap.put("RecoveryType", lLstRecoveryType);
			inputMap.put("SuppBillType", lStrSuplBillType);
			inputMap.put("SchemeCode", lStrSchemeCode);
			inputMap.put("HeadCode", lBgDcmlHeadCode);
			inputMap.put("LedgerNo", lStrLedgerNo);
			inputMap.put("PageNo", lStrPageNo);
			inputMap.put("CurrentDate", lObjSimpleDateFormat.format(gDtCurrDt));
			// inputMap.put("BranchName", lStrBranchName);
			// inputMap.put("BankCode", lStrBankCode);
			// inputMap.put("BranchCode", lStrBranchCode);
			// inputMap.put("AccountNo", lStrAccountNo);

			// StringBuilder lStrBldXML = new StringBuilder();
			// lStrBldXML.append("Y");
			// String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
			// lStrBldXML.toString()).toString();
			// inputMap.put("ajaxKey", lStrResult);
			// resObj.setViewName("ajaxData");
			
			StringBuilder lStrBldXML = new StringBuilder();
			lStrBldXML = getResponseXMLDoc(inputMap);
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
			
			//resObj.setViewName("SupplementaryBills");
			//resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		resObj.setResultValue(inputMap);
		return resObj;
	}

	private StringBuilder getResponseXMLDoc(Map<String, Object> inputMap) {

		StringBuilder lStrHidPKs = new StringBuilder();
		SupplementaryPartyDtlsVO lObjSupplementaryPartyDtlsVO = new SupplementaryPartyDtlsVO();
		List<SupplementaryPartyDtlsVO> lLstSupplementaryPartyDtlsVO = new ArrayList<SupplementaryPartyDtlsVO>();
		
		if(inputMap.containsKey("PPONo"))
		{
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<PPONO>" + inputMap.get("PPONo"));
			lStrHidPKs.append("</PPONO>");
			lStrHidPKs.append("<PNSNRCODE>" + inputMap.get("PensionerCode"));
			lStrHidPKs.append("</PNSNRCODE>");
			lStrHidPKs.append("<PNSNRNAME>" + inputMap.get("PensionerName"));
			lStrHidPKs.append("</PNSNRNAME>");
			lStrHidPKs.append("<BILLTYPEID>" + inputMap.get("BillTypeId"));
			lStrHidPKs.append("</BILLTYPEID>");
			lStrHidPKs.append("<SUPPBILLTYPE>" + inputMap.get("SuppBillType"));
			lStrHidPKs.append("</SUPPBILLTYPE>");
			lStrHidPKs.append("<SCHEMECODE>" + inputMap.get("SchemeCode"));
			lStrHidPKs.append("</SCHEMECODE>");
			lStrHidPKs.append("<CURRENTDATE>" + inputMap.get("CurrentDate"));
			lStrHidPKs.append("</CURRENTDATE>");
			lStrHidPKs.append("<HEADCODE>" + inputMap.get("HeadCode"));
			lStrHidPKs.append("</HEADCODE>");
			lStrHidPKs.append("<LEDGERNO>" + inputMap.get("LedgerNo"));
			lStrHidPKs.append("</LEDGERNO>");
			lStrHidPKs.append("<PAGENO>" + inputMap.get("PageNo"));
			lStrHidPKs.append("</PAGENO>");
		    //Pensioner Details					
			lStrHidPKs.append("<PNSNRDTLS>");

			lLstSupplementaryPartyDtlsVO=(List<SupplementaryPartyDtlsVO>) inputMap.get("PnsnDtls");
							
			for(int lIntCnt=0;lIntCnt<lLstSupplementaryPartyDtlsVO.size();lIntCnt++)
			{
				lObjSupplementaryPartyDtlsVO = lLstSupplementaryPartyDtlsVO.get(lIntCnt);
				lStrHidPKs.append("<PARTYNAME>" + lObjSupplementaryPartyDtlsVO.getBeneifiicaryName());
				lStrHidPKs.append("</PARTYNAME>");
				lStrHidPKs.append("<BANKCODE>" + lObjSupplementaryPartyDtlsVO.getBankCode());
				lStrHidPKs.append("</BANKCODE>");
				lStrHidPKs.append("<BRANCHCODE>" + lObjSupplementaryPartyDtlsVO.getBranchCode());
				lStrHidPKs.append("</BRANCHCODE>");
				lStrHidPKs.append("<BRANCHNAME>" + lObjSupplementaryPartyDtlsVO.getBranchName());
				lStrHidPKs.append("</BRANCHNAME>");
				lStrHidPKs.append("<ACCOUNTNO>" + lObjSupplementaryPartyDtlsVO.getAccountNo());
				lStrHidPKs.append("</ACCOUNTNO>");
				lStrHidPKs.append("<MICRCODE>" + lObjSupplementaryPartyDtlsVO.getMicrCode());
				lStrHidPKs.append("</MICRCODE>");
				lStrHidPKs.append("<SCHEMECODE>" + lObjSupplementaryPartyDtlsVO.getSchemeCode());
				lStrHidPKs.append("</SCHEMECODE>");
			}
			
			lStrHidPKs.append("</PNSNRDTLS>");
			lStrHidPKs.append("</XMLDOC>");
		}
		else
		{
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<ISEMPTY>");
			lStrHidPKs.append("Y");
			lStrHidPKs.append("</ISEMPTY>");
			lStrHidPKs.append("</XMLDOC>");
		}
		gLogger.info("lStrHidPKs : " + lStrHidPKs);
		return lStrHidPKs;
}
	
	public ResultObject validatePPONo(Map<String, Object> inputMap) {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		PensionBillDAO lObjPensionBillDAO = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
		List lLstResult = new ArrayList();
		String lStrStatus = bundleConst.getString("STATUS.CONTINUE");
		String lStrFlag = "N";
		try {
			setSessionInfo(inputMap);
			String lStrPPONo = StringUtility.getParameter("PPONo", request);
			lLstResult = lObjPensionBillDAO.isValidPPONo(lStrPPONo.toUpperCase(), gStrLocCode, lStrStatus, gLngPostId);

			if (!lLstResult.isEmpty()) {
				lStrFlag = "Y";
			}

			StringBuilder lStrBldXML = new StringBuilder();
			lStrBldXML.append("<FLAG>");
			lStrBldXML.append(lStrFlag);
			lStrBldXML.append("</FLAG>");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		resObj.setResultValue(inputMap);
		return resObj;
	}
	/**
	 * Save Supplementary pension bill
	 * @param inputMap
	 * @return
	 */
	public ResultObject saveSupplPensionBill(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List<TrnPensionSupplyBillDtls> lLstTrnPensionSupplyBillDtlsVO = new ArrayList<TrnPensionSupplyBillDtls>();
		TrnPensionSupplyBillDtls lObjTrnPensionSupplyBillDtls = new TrnPensionSupplyBillDtls();
		TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtls = new TrnPensionRecoveryDtls();
		
		String lStrBillType = "";
		String lStrPensionerCode = "";
		String lStrPpoNo = "";
		String lStrSchemeNo = "";
		String lStrPensionAmt = "";
		String lStrPeonAllwAmt = "";
		String lStrADPAmt = "";
		String lStrMedicalAllwAmt = "";
		String lStrDPAmt = "";
		String lStrGallantryAmt = "";
		String lStrIR1Amt = "";
		String lStrOtherBenefit = "";
		String lStrIR2Amt = "";
		String lStrArrearPayOfPnsn = "";
		String lStrIR3Amt = "";
		String lStrArrearOfDA = "";
		String lStrDAAmt = "";
		String lStrArrearOf6PC = "";
		String lStrInstallmentAmt = "";
		String lStrInstallment5Amt = "";
		String lStrCashAmt = "";
		String lStrNoOfInstallment = "";
		String lStrSixPayCalcFlag = "";
		String lStrCvpAmt = "";
		String lStrArrearDiffOfComtPnsn = "";
		String lStrGrossAmt = "";
		String lStrArrearAnyOtherDiff = "";
		String lStrRecAmt = "";
		String lStrNetAmt = "";
		String lStrBankCode = "";
		String lStrBankBranchCode = "";
		String lStrAccountNo = "";
		String lStrChkAmt = "";
		String lStrMicrCode = "";
		String lStrHeadCode = "";
		String lStrPayMode = "";
		String lStrCalcArrearAmt = "";
		String lStrPurpose = "";
		String lStrOtherPurpose = "";
		String lStrLedgerNo = "";
		String lStrPageNo = "";
		String lStrConfigFrmSuppl = "";
		String lStrRevisionFlag = "";
		String lStrRevisionCntr = "";
		
		Double lDbTotalGrossAmt = 0D;
		Double lDbTotalRecoveryAmt = 0D;
		Double lDbTotalNetAmt = 0D;
		Double lDbNoOfPensioner = 0D;
		
		
		String lStrDiffAmt = null;
		//String lStrPartyName = null;
		String lStrBillDtls = null;
		String lStrRecoveryDtls = null;
		String lStrPensionerName = null;
		String lStrPartyName = null;
		String lStrArrearDtls = null;
		String[] lArrStrBillAmts = null;
		
		Calendar lObjCalendar = null;
		StringBuilder lStrBldXML = new StringBuilder();
		String lStrRequestNo = "";
		List<SupplementaryPartyDtlsVO> lLstPensionerDtls = new ArrayList<SupplementaryPartyDtlsVO>();
		SupplementaryPartyDtlsVO lObjSupplementaryPartyDtlsVO = new SupplementaryPartyDtlsVO();
		try {
			setSessionInfo(inputMap);
			String lStrRecoveryFlag = bundleConst.getString("RECOVERY.SUPPPNSN");
			lStrBillDtls = StringUtility.getParameter("billDtls", request);
			lStrRecoveryDtls = StringUtility.getParameter("recoveryDtls", request);
			lStrPensionerName = StringUtility.getParameter("pensionerName", request);
			lStrPartyName = StringUtility.getParameter("partyName", request);
			lStrArrearDtls  = StringUtility.getParameter("arrearDtlsStr", request);
			lStrRequestNo  = StringUtility.getParameter("requestNo", request);
			SupplementaryBillDAO lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnPensionSupplyBillDtls.class, serv.getSessionFactory());
			PensionBillDAO lObjPensionBillDAO = new PensionBillDAOImpl(serv.getSessionFactory());
			//lDbNoOfPensioner = (double) lArrStrBillDtls.length;
			if("".equals(lStrRequestNo))
			{
				lStrRequestNo = IDGenerateDelegate.getNextIdWODbidLocationId("SUPPLY_REQUEST_NO",inputMap);
			}
//			for(int lIntCnt=0;lIntCnt<lArrStrBillDtls.length;lIntCnt++)
//			{
				lObjTrnPensionSupplyBillDtls = new TrnPensionSupplyBillDtls();
				lArrStrBillAmts = lStrBillDtls.split("~");
				lStrPensionerCode = lArrStrBillAmts[0];
				lStrPpoNo = lArrStrBillAmts[1];
				lStrSchemeNo = lArrStrBillAmts[2];
				lStrPensionAmt = lArrStrBillAmts[3];
				lStrPeonAllwAmt = lArrStrBillAmts[4];
				lStrADPAmt = lArrStrBillAmts[5];
				lStrMedicalAllwAmt = lArrStrBillAmts[6];
				lStrDPAmt = lArrStrBillAmts[7];
				lStrGallantryAmt = lArrStrBillAmts[8];
				lStrIR1Amt = lArrStrBillAmts[9];
				lStrOtherBenefit = lArrStrBillAmts[10];
				lStrIR2Amt = lArrStrBillAmts[11];
				lStrArrearPayOfPnsn = lArrStrBillAmts[12];
				lStrIR3Amt = lArrStrBillAmts[13];
				lStrArrearOfDA = lArrStrBillAmts[14];
				lStrDAAmt = lArrStrBillAmts[15];
				lStrArrearOf6PC = lArrStrBillAmts[16];
				lStrInstallmentAmt = lArrStrBillAmts[17];
				lStrInstallment5Amt = lArrStrBillAmts[18];
				lStrCashAmt = lArrStrBillAmts[19];
				lStrNoOfInstallment = lArrStrBillAmts[20];
				lStrSixPayCalcFlag = lArrStrBillAmts[21];
				lStrCvpAmt = lArrStrBillAmts[22];
				lStrArrearDiffOfComtPnsn = lArrStrBillAmts[23];
				lStrGrossAmt = lArrStrBillAmts[24];
				lStrArrearAnyOtherDiff = lArrStrBillAmts[25];
				lStrRecAmt = lArrStrBillAmts[26];
				lStrNetAmt = lArrStrBillAmts[27];
				lStrBankCode = lArrStrBillAmts[28];
				lStrBankBranchCode = lArrStrBillAmts[29];
				lStrAccountNo = lArrStrBillAmts[30];
				lStrChkAmt = lArrStrBillAmts[31];
				lStrMicrCode = lArrStrBillAmts[32];
				lStrHeadCode = lArrStrBillAmts[33];
				lStrPayMode = lArrStrBillAmts[34];
				lStrCalcArrearAmt = lArrStrBillAmts[35];
				lStrLedgerNo = lArrStrBillAmts[36];
				lStrPageNo = lArrStrBillAmts[37];
				lStrConfigFrmSuppl = lArrStrBillAmts[38];
				lStrRevisionFlag = lArrStrBillAmts[39];
				lStrRevisionCntr = lArrStrBillAmts[40];
				lStrPurpose = lArrStrBillAmts[41];
				
				lLstPensionerDtls = lObjPensionBillDAO.getSuppBillData(lStrPensionerCode, gStrLocCode,"PENSION");
				if(!lLstPensionerDtls.isEmpty())
				{
					lObjSupplementaryPartyDtlsVO = lLstPensionerDtls.get(0);
					lStrHeadCode = lObjSupplementaryPartyDtlsVO.getHeadCode().toString();
					lStrSchemeNo = lObjSupplementaryPartyDtlsVO.getSchemeCode();
				}
				if(bundleCaseConst.getString("SUPLPURPOSE.OTHER").equals(lStrPurpose))
				{
					lStrOtherPurpose = lArrStrBillAmts[42];
				}
				lDbTotalGrossAmt = lDbTotalGrossAmt + Double.parseDouble(lStrGrossAmt);
				if(!"".equals(lStrRecAmt))
					lDbTotalRecoveryAmt = lDbTotalRecoveryAmt + Double.parseDouble(lStrRecAmt);
				lDbTotalNetAmt = lDbTotalNetAmt + Double.parseDouble(lStrNetAmt);
				
				Long lLngPensionSupplyBillId = IFMSCommonServiceImpl.getNextSeqNum("trn_pension_supply_bill_dtls", inputMap);
				
				lObjTrnPensionSupplyBillDtls.setPensionSupplyBillId(lLngPensionSupplyBillId);
				lObjTrnPensionSupplyBillDtls.setBillType("PENSION");
				lObjTrnPensionSupplyBillDtls.setPensionerCode(lStrPensionerCode);
				lObjTrnPensionSupplyBillDtls.setPpoNo(lStrPpoNo.trim());
				lObjTrnPensionSupplyBillDtls.setPartyName(lStrPartyName);
				lObjTrnPensionSupplyBillDtls.setPensionerName(lStrPensionerName);
				lObjTrnPensionSupplyBillDtls.setSchemeCode(lStrSchemeNo);
				lObjTrnPensionSupplyBillDtls.setPensionAmount(new BigDecimal(lStrPensionAmt.length() > 0 ? lStrPensionAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setPeonAllowance(new BigDecimal(lStrPeonAllwAmt.length() > 0 ? lStrPeonAllwAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setAdpAmount(new BigDecimal(lStrADPAmt.length() > 0 ? lStrADPAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setMedicalAllowance(new BigDecimal(lStrMedicalAllwAmt.length() > 0 ? lStrMedicalAllwAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setDpAmount(new BigDecimal(lStrDPAmt.length() > 0 ? lStrDPAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setGallantryAmount(new BigDecimal(lStrGallantryAmt.length() > 0 ? lStrGallantryAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setIr1Amount(new BigDecimal(lStrIR1Amt.length() > 0 ? lStrIR1Amt : "0"));
				lObjTrnPensionSupplyBillDtls.setOtherBenefit(new BigDecimal(lStrOtherBenefit.length() > 0 ? lStrOtherBenefit : "0"));
				lObjTrnPensionSupplyBillDtls.setIr2Amount(new BigDecimal(lStrIR2Amt.length() > 0 ? lStrIR2Amt : "0"));
				lObjTrnPensionSupplyBillDtls.setArrearPension(new BigDecimal(lStrArrearPayOfPnsn.length() > 0 ? lStrArrearPayOfPnsn : "0"));
				lObjTrnPensionSupplyBillDtls.setIr3Amount(new BigDecimal(lStrIR3Amt.length() > 0 ? lStrIR3Amt : "0"));
				lObjTrnPensionSupplyBillDtls.setArrearDA(new BigDecimal(lStrArrearOfDA.length() > 0 ? lStrArrearOfDA : "0"));
				lObjTrnPensionSupplyBillDtls.setDaAmount(new BigDecimal(lStrDAAmt.length() > 0 ? lStrDAAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setArrear6PC(new BigDecimal(lStrArrearOf6PC.length() > 0 ? lStrArrearOf6PC : "0"));
				lObjTrnPensionSupplyBillDtls.setTotalCvpAmount(new BigDecimal(lStrCvpAmt.length() > 0 ? lStrCvpAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setArrearDiffComtPnsn(new BigDecimal(lStrArrearDiffOfComtPnsn.length() > 0 ? lStrArrearDiffOfComtPnsn : "0"));
				lObjTrnPensionSupplyBillDtls.setGrossAmount(new BigDecimal(lStrGrossAmt.length() > 0 ? lStrGrossAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setArrearAnyOtherDiff(new BigDecimal(lStrArrearAnyOtherDiff.length() > 0 ? lStrArrearAnyOtherDiff : "0"));
				lObjTrnPensionSupplyBillDtls.setDifferenceAmount(new BigDecimal(lStrRecAmt.length() > 0 ? lStrRecAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setNetAmount(new BigDecimal(lStrNetAmt.length() > 0 ? lStrNetAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setBankCode(!"".equals(lStrBankCode) ? lStrBankCode : "");
				lObjTrnPensionSupplyBillDtls.setBranchCode(!"".equals(lStrBankBranchCode) ? lStrBankBranchCode : "");
				lObjTrnPensionSupplyBillDtls.setAccountNo(!"".equals(lStrAccountNo) ? lStrAccountNo : "");
				lObjTrnPensionSupplyBillDtls.setPaidAmount(new BigDecimal(lStrChkAmt.length() > 0 ? lStrChkAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setMicrCode(!"".equals(lStrMicrCode.trim()) ? Long.parseLong(lStrMicrCode.trim()) : null);
				lObjTrnPensionSupplyBillDtls.setHeadCode(new BigDecimal(lStrHeadCode.length() > 0 ? lStrHeadCode : "0"));
				lObjTrnPensionSupplyBillDtls.setPayMode(lStrPayMode);
				lObjTrnPensionSupplyBillDtls.setLocationCode(gStrLocCode);
				lObjTrnPensionSupplyBillDtls.setCreatedUserId(new BigDecimal(gLngUserId));
				lObjTrnPensionSupplyBillDtls.setCreatedPostId(new BigDecimal(gLngPostId));
				lObjTrnPensionSupplyBillDtls.setCreatedDate(gDtCurrDt);
				lObjTrnPensionSupplyBillDtls.setRequestNo(lStrRequestNo);
				lObjTrnPensionSupplyBillDtls.setStatus(DBConstants.SUPPL_REQ_GENERATED);
				lObjTrnPensionSupplyBillDtls.setArrearDtls(lStrArrearDtls);
				lObjTrnPensionSupplyBillDtls.setCalcArrearAmt(new BigDecimal(lStrCalcArrearAmt.length() > 0 ? lStrCalcArrearAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setLedgerNo(lStrLedgerNo);
				lObjTrnPensionSupplyBillDtls.setPageNo(lStrPageNo);
				lObjTrnPensionSupplyBillDtls.setPurpose(lStrPurpose);
				lObjTrnPensionSupplyBillDtls.setOtherPurpose(lStrOtherPurpose);
				lObjSupplementaryBillDAO.create(lObjTrnPensionSupplyBillDtls);
								
				//Insert Recovery details
				if(!"".equals(lStrRecoveryDtls))
				{
					String[] lArrStrRecovery = lStrRecoveryDtls.split("-");
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(gDtCurrDt);
					Integer lIntMonth = lObjCalendar.get(lObjCalendar.MONTH) + 1;
					Integer lIntYear = lObjCalendar.get(lObjCalendar.YEAR);
					String lStrMonthYear = "";
					if(lIntMonth < 10)
					{
						lStrMonthYear = lIntYear + "0" + lIntMonth.toString();
					}
					else
					{
						lStrMonthYear = lIntYear + lIntMonth.toString();	
					}
					
					lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnPensionRecoveryDtls.class, serv.getSessionFactory());
					for (int i = 0; i < lArrStrRecovery.length; i++) {
						String[] lArrStrRecDtls = lArrStrRecovery[i].split("~");
						lObjTrnPensionRecoveryDtls = new TrnPensionRecoveryDtls();
						Long lLngRecoveryDtlsId = IFMSCommonServiceImpl.getNextSeqNum("trn_pension_recovery_dtls", inputMap);
						lObjTrnPensionRecoveryDtls.setTrnPensionRecoveryDtlsId(lLngRecoveryDtlsId);
						lObjTrnPensionRecoveryDtls.setPensionerCode(lStrPensionerCode);
						lObjTrnPensionRecoveryDtls.setFromMonth(Integer.parseInt(lStrMonthYear));
						lObjTrnPensionRecoveryDtls.setToMonth(Integer.parseInt(lStrMonthYear));
						lObjTrnPensionRecoveryDtls.setRecoveryFromFlag(lStrRecoveryFlag);
						lObjTrnPensionRecoveryDtls.setRecoveryType(lArrStrRecDtls[0].toString());
						lObjTrnPensionRecoveryDtls.setAmount(new BigDecimal(lArrStrRecDtls[1]));
						lObjTrnPensionRecoveryDtls.setSchemeCode(lArrStrRecDtls[2]);
						lObjTrnPensionRecoveryDtls.setPensionSupplyBillId(lLngPensionSupplyBillId);
						lObjTrnPensionRecoveryDtls.setCreatedUserId(new BigDecimal(gLngUserId));
						lObjTrnPensionRecoveryDtls.setCreatedPostId(new BigDecimal(gLngPostId));
						lObjTrnPensionRecoveryDtls.setCreatedDate(gDtCurrDt);
						lObjSupplementaryBillDAO.create(lObjTrnPensionRecoveryDtls);
					}
				}
				//Insert six pay arrear config
				if(!"".equals(lStrSixPayCalcFlag))
				{
					if("Y".equals(lStrSixPayCalcFlag))
					{
						inputMap.put("SupplBillId", lLngPensionSupplyBillId);
						inputMap.put("PensionerCode",lStrPensionerCode);
						inputMap.put("SixPayCalcFlag", lStrSixPayCalcFlag);
						inputMap.put("InstallmentAmt", lStrInstallmentAmt);
						inputMap.put("Installment5Amt", lStrInstallment5Amt);
						inputMap.put("CashAmt", lStrCashAmt);
						inputMap.put("NoOfInstallment", lStrNoOfInstallment);
						if(lStrRevisionFlag != null && !"".equals(lStrRevisionFlag) && "Y".equals(lStrRevisionFlag))
						{
							inputMap.put("Revision", "Y");
						}
						
						objRes = serv.executeService("SIXTH_PAY_ARREAR_VOGEN", inputMap);

						objRes = serv.executeService("SAVE_SIXTH_PAY_ARREAR", inputMap);
					}
				}
//			}
				
				
//			if(lArrStrBillDtls.length > 0)
//			{
//				lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnPensionSupplyRequestDtls.class, serv.getSessionFactory());
//				String lStrRequestNo = IDGenerateDelegate.getNextIdWODbidLocationId("SUPPLY_REQUEST_NO",inputMap);
//				lObjTrnPensionSupplyRequestDtls.setPensionSupplyReqId(lLngPensionSupplyReqId);
//				lObjTrnPensionSupplyRequestDtls.setDbId(gLngDBId);
//				lObjTrnPensionSupplyRequestDtls.setLocationCode(Long.parseLong(gStrLocCode));
//				lObjTrnPensionSupplyRequestDtls.setRequestNo(lStrRequestNo);
//				lObjTrnPensionSupplyRequestDtls.setGrossAmount(new BigDecimal(lDbTotalGrossAmt));
//				lObjTrnPensionSupplyRequestDtls.setRecoveryAmount(new BigDecimal(lDbTotalRecoveryAmt));
//				lObjTrnPensionSupplyRequestDtls.setNetAmount(new BigDecimal(lDbTotalNetAmt));
//				lObjTrnPensionSupplyRequestDtls.setNoOfPensioner(lDbNoOfPensioner.longValue());
//				lObjTrnPensionSupplyRequestDtls.setAudPostId(gLngPostId);
//				lObjTrnPensionSupplyRequestDtls.setCreatedPostId(gLngPostId);
//				lObjTrnPensionSupplyRequestDtls.setCreatedUserId(gLngUserId);
//				lObjTrnPensionSupplyRequestDtls.setCreatedDate(gDtCurrDt);
//				lObjSupplementaryBillDAO.create(lObjTrnPensionSupplyRequestDtls);				
//			}
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>Record added to request Successfully.");
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("<SUPPLBILLID>"+lLngPensionSupplyBillId);
			lStrBldXML.append("</SUPPLBILLID>");
			lStrBldXML.append("<REQUESTNO>"+lStrRequestNo);
			lStrBldXML.append("</REQUESTNO>");
			lStrBldXML.append("</XMLDOC>");
			
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
			
		}catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is : " + e, e);
			objRes.setThrowable(e);
			objRes.setResultValue(null);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
	}
	return objRes;
	}
	
	
	/**
	 * Save Supplementary pension bill
	 * @param inputMap
	 * @return
	 */
	public ResultObject updateSupplPensionBill(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPensionSupplyBillDtls lObjTrnPensionSupplyBillDtls = null;
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtls = new ArrayList<TrnPensionRecoveryDtls>();
		TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtls = new TrnPensionRecoveryDtls();
		
		String lStrBillType = "";
		String lStrPensionerCode = "";
		String lStrPpoNo = "";
		String lStrPensionerName = "";
		String lStrSchemeNo = "";
		String lStrPensionAmt = "";
		String lStrPeonAllwAmt = "";
		String lStrADPAmt = "";
		String lStrMedicalAllwAmt = "";
		String lStrDPAmt = "";
		String lStrGallantryAmt = "";
		String lStrIR1Amt = "";
		String lStrOtherBenefit = "";
		String lStrIR2Amt = "";
		String lStrArrearPayOfPnsn = "";
		String lStrIR3Amt = "";
		String lStrArrearOfDA = "";
		String lStrDAAmt = "";
		String lStrArrearOf6PC = "";
		String lStrInstallmentAmt = "";
		String lStrInstallment5Amt = "";
		String lStrCashAmt = "";
		String lStrNoOfInstallment = "";
		String lStrSixPayCalcFlag = "";
		String lStrCvpAmt = "";
		String lStrArrearDiffOfComtPnsn = "";
		String lStrGrossAmt = "";
		String lStrArrearAnyOtherDiff = "";
		String lStrRecAmt = "";
		String lStrNetAmt = "";
		String lStrPartyName = "";
		String lStrBankCode = "";
		String lStrBankBranchCode = "";
		String lStrAccountNo = "";
		String lStrChkAmt = "";
		String lStrMicrCode = "";
		String lStrPayMode = "";
		String lStrArrearDtls = "";
		String lStrCalcArrearAmt = "";
		String lStrPurpose = "";
		String lStrOtherPurpose = "";
		String lStrConfigFrmSuppl = "";
		String lStrRevisionFlag = "";
		String lStrRevisionCntr = "";
			
		Calendar lObjCalendar = null;
		StringBuilder lStrBldXML = new StringBuilder();
		
		try {
			setSessionInfo(inputMap);
			String lStrRecoveryFlag = bundleConst.getString("RECOVERY.SUPPPNSN");
			
			String[] lArrStrRecoveryType = StringUtility.getParameterValues("cmbRecoveryType", request);
			String[] lArrStrAmount = StringUtility.getParameterValues("txtAmount", request);
			String[] lArrStrSchemeCode = StringUtility.getParameterValues("txtSchemeCode", request);						
			
			SupplementaryBillDAO lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnPensionSupplyBillDtls.class, serv.getSessionFactory());
			String lStrPensionSupplyBillId = StringUtility.getParameter("hidPensionBillSupplyId", request);
			
			if(!"".equals(lStrPensionSupplyBillId))
			{
				lObjTrnPensionSupplyBillDtls = (TrnPensionSupplyBillDtls) lObjSupplementaryBillDAO.read(Long.parseLong(lStrPensionSupplyBillId));
				
				lStrPensionerName = StringUtility.getParameter("txtName", request);
				//lStrSchemeNo = StringUtility.getParameter("txtSchemeNo", request);
				lStrPensionAmt = StringUtility.getParameter("txtPensionAmt", request);
				lStrPeonAllwAmt = StringUtility.getParameter("txtPeonAllowAmt", request);
				lStrADPAmt = StringUtility.getParameter("txtADPAmt", request);
				lStrMedicalAllwAmt = StringUtility.getParameter("txtMedicalAllowAmt", request);
				lStrDPAmt = StringUtility.getParameter("txtDPAmt", request);
				lStrGallantryAmt = StringUtility.getParameter("txtGallantryAmt", request);
				lStrIR1Amt = StringUtility.getParameter("txtIR1Amt", request);
				lStrOtherBenefit = StringUtility.getParameter("txtOtherBenefit", request);
				lStrIR2Amt = StringUtility.getParameter("txtIR2Amt", request);
				lStrArrearPayOfPnsn = StringUtility.getParameter("txtArrearPayOfPnsn", request);
				lStrIR3Amt = StringUtility.getParameter("txtIR3Amt", request);
				lStrArrearOfDA = StringUtility.getParameter("txtArrearOfDA", request);
				lStrDAAmt = StringUtility.getParameter("txtDAAmt", request);
				lStrArrearOf6PC = StringUtility.getParameter("txtArrearOf6PC", request);
				lStrInstallmentAmt = StringUtility.getParameter("hdnInstallmentAmt", request);
				lStrInstallment5Amt = StringUtility.getParameter("hdnInstallment5Amt", request);
				lStrCashAmt = StringUtility.getParameter("hdnCashAmt", request);
				lStrNoOfInstallment = StringUtility.getParameter("hdnNoOfInstallment", request);
				lStrSixPayCalcFlag = StringUtility.getParameter("hdnSixPayCalcFlag", request);
				lStrCvpAmt = StringUtility.getParameter("txtCvpAmt", request);
				lStrArrearDiffOfComtPnsn = StringUtility.getParameter("txtArrearDiffOfComtPnsn", request);
				lStrGrossAmt = StringUtility.getParameter("txtGrossAmt", request);
				lStrArrearAnyOtherDiff = StringUtility.getParameter("txtArrearAnyOtherDiff", request);
				lStrRecAmt = StringUtility.getParameter("txtRecAmt", request);
				lStrNetAmt = StringUtility.getParameter("txtNetAmt", request);
				lStrPartyName = StringUtility.getParameter("txtPartyName", request);
				lStrBankCode = StringUtility.getParameter("cmbBankCode", request);
				lStrBankBranchCode = StringUtility.getParameter("cmbBankBranchName", request);
				lStrAccountNo = StringUtility.getParameter("txtAccountNo", request);
				lStrChkAmt = StringUtility.getParameter("txtChkAmt", request);
				lStrMicrCode = StringUtility.getParameter("txtMicrCode", request);
				lStrPayMode =  StringUtility.getParameter("paymentMode", request);
				lStrArrearDtls =  StringUtility.getParameter("arrearDtlsStr", request);
				lStrCalcArrearAmt =  StringUtility.getParameter("hdnArrearAmt", request);
				lStrPurpose =  StringUtility.getParameter("cmbPurpose", request);
				lStrOtherPurpose =  StringUtility.getParameter("txtOtherPurpose", request);
				lStrConfigFrmSuppl = StringUtility.getParameter("hdnConfigFrmSuppl", request);
				lStrRevisionFlag = StringUtility.getParameter("hdnRevisionFlag", request);
				lStrRevisionCntr = StringUtility.getParameter("hdnRevisionCntr", request);
				
				lObjTrnPensionSupplyBillDtls.setPartyName(lStrPartyName);
				lObjTrnPensionSupplyBillDtls.setPensionerName(lStrPensionerName);
				//lObjTrnPensionSupplyBillDtls.setSchemeCode(lStrSchemeNo);
				lObjTrnPensionSupplyBillDtls.setPensionAmount(new BigDecimal(lStrPensionAmt.length() > 0 ? lStrPensionAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setPeonAllowance(new BigDecimal(lStrPeonAllwAmt.length() > 0 ? lStrPeonAllwAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setAdpAmount(new BigDecimal(lStrADPAmt.length() > 0 ? lStrADPAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setMedicalAllowance(new BigDecimal(lStrMedicalAllwAmt.length() > 0 ? lStrMedicalAllwAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setDpAmount(new BigDecimal(lStrDPAmt.length() > 0 ? lStrDPAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setGallantryAmount(new BigDecimal(lStrGallantryAmt.length() > 0 ? lStrGallantryAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setIr1Amount(new BigDecimal(lStrIR1Amt.length() > 0 ? lStrIR1Amt : "0"));
				lObjTrnPensionSupplyBillDtls.setOtherBenefit(new BigDecimal(lStrOtherBenefit.length() > 0 ? lStrOtherBenefit : "0"));
				lObjTrnPensionSupplyBillDtls.setIr2Amount(new BigDecimal(lStrIR2Amt.length() > 0 ? lStrIR2Amt : "0"));
				lObjTrnPensionSupplyBillDtls.setArrearPension(new BigDecimal(lStrArrearPayOfPnsn.length() > 0 ? lStrArrearPayOfPnsn : "0"));
				lObjTrnPensionSupplyBillDtls.setIr3Amount(new BigDecimal(lStrIR3Amt.length() > 0 ? lStrIR3Amt : "0"));
				lObjTrnPensionSupplyBillDtls.setArrearDA(new BigDecimal(lStrArrearOfDA.length() > 0 ? lStrArrearOfDA : "0"));
				lObjTrnPensionSupplyBillDtls.setDaAmount(new BigDecimal(lStrDAAmt.length() > 0 ? lStrDAAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setArrear6PC(new BigDecimal(lStrArrearOf6PC.length() > 0 ? lStrArrearOf6PC : "0"));
				lObjTrnPensionSupplyBillDtls.setTotalCvpAmount(new BigDecimal(lStrCvpAmt.length() > 0 ? lStrCvpAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setArrearDiffComtPnsn(new BigDecimal(lStrArrearDiffOfComtPnsn.length() > 0 ? lStrArrearDiffOfComtPnsn : "0"));
				lObjTrnPensionSupplyBillDtls.setGrossAmount(new BigDecimal(lStrGrossAmt.length() > 0 ? lStrGrossAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setArrearAnyOtherDiff(new BigDecimal(lStrArrearAnyOtherDiff.length() > 0 ? lStrArrearAnyOtherDiff : "0"));
				lObjTrnPensionSupplyBillDtls.setDifferenceAmount(new BigDecimal(lStrRecAmt.length() > 0 ? lStrRecAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setNetAmount(new BigDecimal(lStrNetAmt.length() > 0 ? lStrNetAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setBankCode(!"".equals(lStrBankCode) ? lStrBankCode : "");
				lObjTrnPensionSupplyBillDtls.setBranchCode(!"".equals(lStrBankBranchCode) ? lStrBankBranchCode : "");
				lObjTrnPensionSupplyBillDtls.setAccountNo(!"".equals(lStrAccountNo) ? lStrAccountNo : "");
				lObjTrnPensionSupplyBillDtls.setPaidAmount(new BigDecimal(lStrChkAmt.length() > 0 ? lStrChkAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setMicrCode(!"".equals(lStrMicrCode) ? Long.parseLong(lStrMicrCode) : null);
				lObjTrnPensionSupplyBillDtls.setCalcArrearAmt(new BigDecimal(lStrCalcArrearAmt.length() > 0 ? lStrCalcArrearAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setPayMode(lStrPayMode);
				lObjTrnPensionSupplyBillDtls.setArrearDtls(lStrArrearDtls);
				lObjTrnPensionSupplyBillDtls.setPurpose(lStrPurpose);
				lObjTrnPensionSupplyBillDtls.setOtherPurpose(lStrOtherPurpose);
				lObjTrnPensionSupplyBillDtls.setLocationCode(gStrLocCode);
				lObjTrnPensionSupplyBillDtls.setUpdatedUserId(new BigDecimal(gLngUserId));
				lObjTrnPensionSupplyBillDtls.setUpdatedPostId(new BigDecimal(gLngPostId));
				lObjTrnPensionSupplyBillDtls.setUpdatedDate(gDtCurrDt);
				
				lObjTrnPensionSupplyBillDtls.setStatus(DBConstants.SUPPL_BILL_UPDATED);
				lObjSupplementaryBillDAO.update(lObjTrnPensionSupplyBillDtls);
								
				lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnPensionRecoveryDtls.class, serv.getSessionFactory());
				lLstTrnPensionRecoveryDtls = lObjSupplementaryBillDAO.getListByColumnAndValue("pensionSupplyBillId", lObjTrnPensionSupplyBillDtls.getPensionSupplyBillId());
				
				if(!lLstTrnPensionRecoveryDtls.isEmpty())
				{
					for(int lIntCnt = 0;lIntCnt < lLstTrnPensionRecoveryDtls.size(); lIntCnt++)
					{
						lObjTrnPensionRecoveryDtls = new TrnPensionRecoveryDtls();
						lObjTrnPensionRecoveryDtls = lLstTrnPensionRecoveryDtls.get(lIntCnt);
						lObjSupplementaryBillDAO.delete(lObjTrnPensionRecoveryDtls);
					}
				}
				
				//Insert Recovery details
				if(lArrStrRecoveryType.length > 0)
				{			
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(gDtCurrDt);
					Integer lIntMonth = lObjCalendar.get(lObjCalendar.MONTH) + 1;
					Integer lIntYear = lObjCalendar.get(lObjCalendar.YEAR);
					String lStrMonthYear = "";
					if(lIntMonth < 10)
					{
						lStrMonthYear = lIntYear + "0" + lIntMonth.toString();
					}
					else
					{
						lStrMonthYear = lIntYear + lIntMonth.toString();	
					}
					
					for (int lIntCnt = 0; lIntCnt < lArrStrRecoveryType.length; lIntCnt++) {
						
						lObjTrnPensionRecoveryDtls = new TrnPensionRecoveryDtls();
						Long lLngRecoveryDtlsId = IFMSCommonServiceImpl.getNextSeqNum("trn_pension_recovery_dtls", inputMap);
						lObjTrnPensionRecoveryDtls.setTrnPensionRecoveryDtlsId(lLngRecoveryDtlsId);
						lObjTrnPensionRecoveryDtls.setPensionerCode(lObjTrnPensionSupplyBillDtls.getPensionerCode());
						lObjTrnPensionRecoveryDtls.setRecoveryFromFlag(lStrRecoveryFlag);
						if(!"-1".equals(lArrStrRecoveryType[lIntCnt]))
							lObjTrnPensionRecoveryDtls.setRecoveryType(lArrStrRecoveryType[lIntCnt]);
						if(!"".equals(lArrStrAmount[lIntCnt]))
							lObjTrnPensionRecoveryDtls.setAmount(new BigDecimal(lArrStrAmount[lIntCnt].trim()));
						if(!"".equals(lArrStrSchemeCode[lIntCnt]))
							lObjTrnPensionRecoveryDtls.setSchemeCode(lArrStrSchemeCode[lIntCnt].trim());
						lObjTrnPensionRecoveryDtls.setPensionSupplyBillId(lObjTrnPensionSupplyBillDtls.getPensionSupplyBillId());
						lObjTrnPensionRecoveryDtls.setFromMonth(Integer.parseInt(lStrMonthYear));
						lObjTrnPensionRecoveryDtls.setToMonth(Integer.parseInt(lStrMonthYear));
						lObjTrnPensionRecoveryDtls.setCreatedUserId(new BigDecimal(gLngUserId));
						lObjTrnPensionRecoveryDtls.setCreatedPostId(new BigDecimal(gLngPostId));
						lObjTrnPensionRecoveryDtls.setCreatedDate(gDtCurrDt);
						lObjTrnPensionRecoveryDtls.setUpdatedUserId(new BigDecimal(gLngUserId));
						lObjTrnPensionRecoveryDtls.setUpdatedPostId(new BigDecimal(gLngPostId));
						lObjTrnPensionRecoveryDtls.setUpdatedDate(gDtCurrDt);
						lObjSupplementaryBillDAO.create(lObjTrnPensionRecoveryDtls);
					}
				}
				//Delete sixth pay arrear details, if already configured in the request
				if("Y".equals(lStrConfigFrmSuppl) && "Y".equals(lStrSixPayCalcFlag))
				{
					lObjSupplementaryBillDAO.deleteSixPayArrearDtlsFromSupplBillId(Long.parseLong(lStrPensionSupplyBillId));
					if(lStrRevisionCntr != null && !"".equals(lStrRevisionCntr) && Integer.parseInt(lStrRevisionCntr) > 0)
					{
						lObjSupplementaryBillDAO.updateActiveFlagInSixPayArrear(lObjTrnPensionSupplyBillDtls.getPensionerCode(),Character.valueOf(StringUtility.convertToChar(lStrRevisionCntr)));
					}
				}
				
				if(!"".equals(lStrSixPayCalcFlag))
				{
					if("Y".equals(lStrSixPayCalcFlag))
					{
						inputMap.put("SupplBillId", Long.parseLong(lStrPensionSupplyBillId));
						inputMap.put("PensionerCode",lObjTrnPensionSupplyBillDtls.getPensionerCode());
						inputMap.put("SixPayCalcFlag", lStrSixPayCalcFlag);
						inputMap.put("InstallmentAmt", lStrInstallmentAmt);
						inputMap.put("Installment5Amt", lStrInstallment5Amt);
						inputMap.put("CashAmt", lStrCashAmt);
						inputMap.put("NoOfInstallment", lStrNoOfInstallment);
						if(lStrRevisionFlag != null && !"".equals(lStrRevisionFlag) && "Y".equals(lStrRevisionFlag))
						{
							inputMap.put("Revision", "Y");
						}
												
						objRes = serv.executeService("SIXTH_PAY_ARREAR_VOGEN", inputMap);

						objRes = serv.executeService("SAVE_SIXTH_PAY_ARREAR", inputMap);
					}
				}

			}

			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>Bill Updated Successfully.");
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("</XMLDOC>");
			
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
			
		}catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			objRes.setThrowable(e);
			objRes.setResultValue(null);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
	}
	return objRes;
	}
	
	/**
	 * Delete pensioner from Supplementary request
	 * @param inputMap
	 * @return
	 */
	public ResultObject deletePensionerFromSupplRequest(Map<String, Object> inputMap) {
		
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPensionSupplyBillDtls lObjTrnPensionSupplyBillDtls = new TrnPensionSupplyBillDtls();	
		StringBuilder lStrBldXML = new StringBuilder();
		try {
			setSessionInfo(inputMap);
			String lStrPensionSupplBillId = StringUtility.getParameter("supplBillId", request);
			SupplementaryBillDAO lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnPensionSupplyBillDtls.class, serv.getSessionFactory());
			
			if(!"".equals(lStrPensionSupplBillId))
			{
				lObjSupplementaryBillDAO.deleteRecoveryDtlsFromSupplBillId(Long.parseLong(lStrPensionSupplBillId));
				
				lObjSupplementaryBillDAO.deleteSixPayArrearDtlsFromSupplBillId(Long.parseLong(lStrPensionSupplBillId));
				
				lObjTrnPensionSupplyBillDtls = (TrnPensionSupplyBillDtls) lObjSupplementaryBillDAO.read(Long.parseLong(lStrPensionSupplBillId));
				lObjSupplementaryBillDAO.delete(lObjTrnPensionSupplyBillDtls);
			}
								
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>Record deleted Successfully.");
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("</XMLDOC>");
			
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
			
		}catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			objRes.setThrowable(e);
			objRes.setResultValue(null);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
	}
	return objRes;
	}
	
	/**
	 * Generate Supplementary Pension bills
	 * @param inputMap
	 * @return
	 */
	public ResultObject generateSupplPensionBills(Map<String, Object> inputMap) {
		
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<Short> lLstBillStatus = new ArrayList<Short>();
		List lLstPnsnSupplDtlsSchemeCodePayModeWise = new ArrayList();
		List lLstPnsnSupplDtlBranchHeadWise = new ArrayList();
		List<TrnPensionSupplyBillDtls> lLstTrnPensionSupplyBillDtls = new ArrayList<TrnPensionSupplyBillDtls>();
		TrnPensionSupplyBillDtls lObjTrnPensionSupplyBillDtls = new TrnPensionSupplyBillDtls();
		
		TrnBillRegister lObjTrnBillRegisterVO = new TrnBillRegister();
		List<TrnBillRegister> lLstTrnBillRegister = new ArrayList<TrnBillRegister>();
		TrnPensionBillHdr lObjTrnPensionBillHdrVO = new TrnPensionBillHdr();
		List<TrnPensionBillHdr> lLstTrnPensionBillHdr = new ArrayList<TrnPensionBillHdr>();
		List<TrnPensionBillDtls> lLstTrnPensionBillDtls = new ArrayList<TrnPensionBillDtls>();
		List<TrnPensionBillDtls> lLstTrnPensionBillDtlsVO = new ArrayList<TrnPensionBillDtls>();
		TrnPensionBillDtls lObjTrnPensionBillDtlsVO = new TrnPensionBillDtls();
		List<RltBillParty> lLstRltBillParty = new ArrayList<RltBillParty>();
		List<RltBillParty> lLstRltBillPartyVO = new ArrayList<RltBillParty>();
		RltBillParty lObjRltBillParty = new RltBillParty();
		
		Map<String, TrnBillRegister> lMapSchemCodePayModeWise = new HashMap<String, TrnBillRegister>();
		Map<String, TrnPensionBillHdr> lMapBranchHeadWise = new HashMap<String, TrnPensionBillHdr>();
		Map<String, List<TrnPensionBillDtls>> lMapSupplBillDtls = new HashMap<String, List<TrnPensionBillDtls>>();
		Map<String, RltBillParty> lMapRltBillParty = new HashMap<String, RltBillParty>();
		Map<String, Map<String, List<BigDecimal>>> lMapSchemeBranchHeadMpg = new HashMap<String, Map<String, List<BigDecimal>>>();
		Map<String, List<BigDecimal>> lMapBranchHeadMpg = new HashMap<String, List<BigDecimal>>();
		List<BigDecimal> lLstHeadCode = new ArrayList<BigDecimal>();
		Map<String, List<Long>> lMapSchemeSupplBillId = new HashMap<String, List<Long>>();
		List<Long> lLstSupplBillId = new ArrayList<Long>();
		
		String lStrSchemePayModeKey = "";
		String lStrBranchHeadKey = "";
		String lStrKey = "";
		StringBuilder lStrBldXML = new StringBuilder();
		Calendar lObjCalendar = null;
		try {
			setSessionInfo(inputMap);
			HibernateTemplate hitStg = new HibernateTemplate(serv.getSessionFactory());
			lLstBillStatus.add(DBConstants.SUPPL_REQ_GENERATED);
			lLstBillStatus.add(DBConstants.SUPPL_BILL_UPDATED);
			String lStrRequestNo = StringUtility.getParameter("requestNo", request);
			SupplementaryBillDAO lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnPensionSupplyBillDtls.class, serv.getSessionFactory());
			BptmCommonServicesDAO bptmDAO = new BptmCommonServicesDAOImpl(TrnBillRegister.class, serv.getSessionFactory());
			
			lLstPnsnSupplDtlsSchemeCodePayModeWise = lObjSupplementaryBillDAO.getSupplPnsnDtlSchemeCodePayModeWise(lStrRequestNo, lLstBillStatus, gStrLocCode);
			
			Long lLngBillControlNum = bptmDAO.getBillControlNo(inputMap);
			String lStrBillCntrlNoPrefix = getBillCntrlNoPrefix();
			
			lObjCalendar = Calendar.getInstance();
			lObjCalendar.setTime(gDtCurrDt);
			Integer lIntMonth = lObjCalendar.get(lObjCalendar.MONTH) + 1;
			Integer lIntYear = lObjCalendar.get(lObjCalendar.YEAR);
			String lStrMonthYear = "";
			if(lIntMonth < 10)
			{
				lStrMonthYear = lIntYear + "0" + lIntMonth.toString();
			}
			else
			{
				lStrMonthYear = lIntYear + lIntMonth.toString();	
			}
			
			//Prepare Map for schemecode and payment mode wise TrnBillRegister VO List
			if(!lLstPnsnSupplDtlsSchemeCodePayModeWise.isEmpty())
			{
				for(int lIntCnt = 0;lIntCnt < lLstPnsnSupplDtlsSchemeCodePayModeWise.size();lIntCnt++)
				{
					Object[] obj=(Object[])lLstPnsnSupplDtlsSchemeCodePayModeWise.get(lIntCnt);
					lStrSchemePayModeKey = obj[0] + "~" +  obj[1];
					lObjTrnBillRegisterVO = new TrnBillRegister();
					lObjTrnBillRegisterVO.setBillDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(gDtCurrDt)));
					lObjTrnBillRegisterVO.setSubjectId(Short.parseShort(bundleConst.getString("BILLTYPE.SUPP")));
					lObjTrnBillRegisterVO.setPhyBill(Short.parseShort("1"));
					lObjTrnBillRegisterVO.setSchemeNo(obj[0].toString());
					if(obj[2] != null)
						lObjTrnBillRegisterVO.setBillGrossAmount(new BigDecimal(obj[2].toString()));
					if(obj[4] != null)
						lObjTrnBillRegisterVO.setBillNetAmount(new BigDecimal(obj[4].toString()));
					if(obj[3] != null)
						lObjTrnBillRegisterVO.setDeductionA(new BigDecimal(obj[3].toString()));
					lObjTrnBillRegisterVO.setLocationCode(gStrLocCode);
					lObjTrnBillRegisterVO.setDbId(gLngDBId);
					lObjTrnBillRegisterVO.setCurrBillStatus(DBConstants.ST_BILL_CREATED);
					lObjTrnBillRegisterVO.setAudPostId(gLngPostId);
					lObjTrnBillRegisterVO.setAudUserId(gLngUserId);
					lObjTrnBillRegisterVO.setFinYearId(SessionHelper.getFinYrId(inputMap).toString());
					lObjTrnBillRegisterVO.setTcBill(bundleConst.getString("PPMT.SUPPLY"));
					lObjTrnBillRegisterVO.setBillCntrlNo(lStrBillCntrlNoPrefix + (lLngBillControlNum++));
					lObjTrnBillRegisterVO.setForMonth(Integer.valueOf(lStrMonthYear));
					lObjTrnBillRegisterVO.setPayMode(obj[1].toString());
					lObjTrnBillRegisterVO.setCreatedDate(gDtCurrDt);
					lObjTrnBillRegisterVO.setCreatedPostId(gLngPostId);
					lObjTrnBillRegisterVO.setCreatedUserId(gLngUserId);
					lMapSchemCodePayModeWise.put(lStrSchemePayModeKey,lObjTrnBillRegisterVO);
					
					
					lObjRltBillParty = new RltBillParty();
					lObjRltBillParty.setPartyCode(obj[0].toString());
					lObjRltBillParty.setPartyAmt(new BigDecimal(obj[4].toString()));
					lObjRltBillParty.setCreatedDate(gDtCurrDt);
					lObjRltBillParty.setCreatedPostId(gLngPostId);
					lObjRltBillParty.setCreatedUserId(gLngUserId);
					lObjRltBillParty.setDbId(gLngDBId);
					lObjRltBillParty.setPartyName(obj[0].toString());
					lObjRltBillParty.setLocationCode(gStrLocCode);
					lObjRltBillParty.setPaymentMode(obj[1].toString());
//					lObjRltBillParty.setBankCode(lObjTrnPensionSupplyBillDtls.getBankCode());
//					lObjRltBillParty.setBranchCode(lObjTrnPensionSupplyBillDtls.getBranchCode());
					
					lMapRltBillParty.put(lStrSchemePayModeKey, lObjRltBillParty);
					
				}
			}
			
			lLstPnsnSupplDtlBranchHeadWise = lObjSupplementaryBillDAO.getSupplPnsnDtlBranchHeadWise(lStrRequestNo, lLstBillStatus, gStrLocCode);
			
			//Prepare Map of branch and head wise TrnPensionBillHdr VO List
			if(!lLstPnsnSupplDtlBranchHeadWise.isEmpty())
			{
				for(int lIntCnt = 0;lIntCnt < lLstPnsnSupplDtlBranchHeadWise.size();lIntCnt++)
				{
					Object[] obj=(Object[])lLstPnsnSupplDtlBranchHeadWise.get(lIntCnt);
					lStrBranchHeadKey = obj[1] + "~" +  obj[2] + "~" + obj[4] + "~" + obj[5];
					lObjTrnPensionBillHdrVO = new TrnPensionBillHdr();
					lObjTrnPensionBillHdrVO.setBillType(bundleConst.getString("PPMT.SUPPLY"));
					lObjTrnPensionBillHdrVO.setBillDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(gDtCurrDt)));
					if(obj[5] != null)
						lObjTrnPensionBillHdrVO.setHeadCode(new BigDecimal(obj[5].toString()));
					lObjTrnPensionBillHdrVO.setForMonth(Integer.parseInt(new SimpleDateFormat("yyyyMM").format(gDtCurrDt)));
					lObjTrnPensionBillHdrVO.setBankCode(obj[3].toString());
					lObjTrnPensionBillHdrVO.setBranchCode(obj[4].toString());
					//lObjTrnPensionBillHdrVO.setScheme(lObjTrnPensionChangeHdr.getScheme());
					lObjTrnPensionBillHdrVO.setSchemeCode(obj[1].toString());
					lObjTrnPensionBillHdrVO.setPayMode(obj[2].toString());
					lObjTrnPensionBillHdrVO.setTrnCounter(1);
					lObjTrnPensionBillHdrVO.setLocationCode(gStrLocCode);
					if(obj[6] != null)
						lObjTrnPensionBillHdrVO.setGrossAmount(new BigDecimal(Long.parseLong(obj[6].toString())));
					if(obj[8] != null)
						lObjTrnPensionBillHdrVO.setNetAmount(new BigDecimal(Long.parseLong(obj[8].toString())));
					if(obj[7] != null)
						lObjTrnPensionBillHdrVO.setDeductionA(new BigDecimal(Long.parseLong(obj[7].toString())));
					
					//lObjTrnPensionBillHdrVO.setDeductionB(lObjTrnPensionChangeHdr.getDeductionB());
					if(obj[0] != null){
						lObjTrnPensionBillHdrVO.setNoOfPnsr(Integer.valueOf(obj[0].toString()).longValue());
					}
					lObjTrnPensionBillHdrVO.setCreatedDate(gDtCurrDt);
					lObjTrnPensionBillHdrVO.setCreatedPostId(new BigDecimal(gLngPostId));
					lObjTrnPensionBillHdrVO.setCreatedUserId(new BigDecimal(gLngUserId));
					lMapBranchHeadWise.put(lStrBranchHeadKey, lObjTrnPensionBillHdrVO);
				}
			}
			
			lLstTrnPensionSupplyBillDtls = lObjSupplementaryBillDAO.getSupplPnsnDtlsFromRequestNo(lStrRequestNo, lLstBillStatus, gStrLocCode);
			
			if(!lLstTrnPensionSupplyBillDtls.isEmpty())
			{
				List<String> lLstPensionerCode = new ArrayList<String>();
				TrnPensionRqstHdr lObjTrnPensionRqstHdr = new TrnPensionRqstHdr();
				List<TrnPensionRqstHdr> lLstTrnPensionRqstHdr = new ArrayList<TrnPensionRqstHdr>();
				Map<String,BigDecimal[]> lMapPnsnrCodeWiseAllocation = new HashMap<String,BigDecimal[]>();
				BigDecimal[] lBgDcmlAllocationPercent = null;
				for(int lIntCnt = 0;lIntCnt < lLstTrnPensionSupplyBillDtls.size();lIntCnt++)
				{
					lObjTrnPensionSupplyBillDtls = lLstTrnPensionSupplyBillDtls.get(lIntCnt);
					lLstPensionerCode.add(lObjTrnPensionSupplyBillDtls.getPensionerCode());
				}
				lLstTrnPensionRqstHdr = lObjSupplementaryBillDAO.getAllocationPercentFromPensionerCode(lLstPensionerCode, gStrLocCode);
				for(int lIntCnt = 0;lIntCnt < lLstTrnPensionRqstHdr.size();lIntCnt++)
				{
					lObjTrnPensionRqstHdr = lLstTrnPensionRqstHdr.get(lIntCnt);
					lBgDcmlAllocationPercent = new BigDecimal[5];
					lBgDcmlAllocationPercent[0] = lObjTrnPensionRqstHdr.getOrgBf10436Percent();
					lBgDcmlAllocationPercent[1] = lObjTrnPensionRqstHdr.getOrgAf10436Percent();
					lBgDcmlAllocationPercent[2] = lObjTrnPensionRqstHdr.getOrgAf11156Percent();
					lBgDcmlAllocationPercent[3] = lObjTrnPensionRqstHdr.getOrgAf10560Percent();
					lBgDcmlAllocationPercent[4] = lObjTrnPensionRqstHdr.getOrgZpAfPercent();
					
					lMapPnsnrCodeWiseAllocation.put(lObjTrnPensionRqstHdr.getPensionerCode(), lBgDcmlAllocationPercent);
				}
				
				for(int lIntCnt = 0;lIntCnt < lLstTrnPensionSupplyBillDtls.size();lIntCnt++)
				{
					Double lDblTotalAllocationAmt = 0.0;
					Double lDblAllcBf36Amt = 0.0;
					Double lDblAllcAf36Amt = 0.0;
					Double lDblAllcAf56Amt = 0.0;
					Double lDblAllcAf60Amt = 0.0;
					Double lDblAllcAfZPAmt = 0.0;
					lBgDcmlAllocationPercent = new BigDecimal[5];
					
					lObjTrnPensionSupplyBillDtls = lLstTrnPensionSupplyBillDtls.get(lIntCnt);
					lStrSchemePayModeKey = lObjTrnPensionSupplyBillDtls.getSchemeCode() + "~" +  lObjTrnPensionSupplyBillDtls.getPayMode();
					
					lStrKey = lObjTrnPensionSupplyBillDtls.getSchemeCode() + "~" +  lObjTrnPensionSupplyBillDtls.getPayMode() + "~" 
										+ lObjTrnPensionSupplyBillDtls.getBranchCode() + "~" + lObjTrnPensionSupplyBillDtls.getHeadCode();
					
					lBgDcmlAllocationPercent = lMapPnsnrCodeWiseAllocation.get(lObjTrnPensionSupplyBillDtls.getPensionerCode());
					
					lObjTrnPensionBillDtlsVO = new TrnPensionBillDtls();
					//lObjTrnPensionBillDtlsVO.setTrnPensionBillDtlsId(lLngBillDtlsId);
					//lObjTrnPensionBillDtlsVO.setTrnPensionBillHdrId(lLngBillHdrId);
					lObjTrnPensionBillDtlsVO.setPpoNo(lObjTrnPensionSupplyBillDtls.getPpoNo());
					lObjTrnPensionBillDtlsVO.setPensionerCode(lObjTrnPensionSupplyBillDtls.getPensionerCode());
					lObjTrnPensionBillDtlsVO.setCvpMonthAmount(lObjTrnPensionSupplyBillDtls.getTotalCvpAmount());
					lObjTrnPensionBillDtlsVO.setPensionAmount(lObjTrnPensionSupplyBillDtls.getPensionAmount());
					lObjTrnPensionBillDtlsVO.setDpAmount(lObjTrnPensionSupplyBillDtls.getDpAmount());
					lObjTrnPensionBillDtlsVO.setTiAmount(lObjTrnPensionSupplyBillDtls.getDaAmount());
					lObjTrnPensionBillDtlsVO.setRecoveryAmount(lObjTrnPensionSupplyBillDtls.getDifferenceAmount());
					lObjTrnPensionBillDtlsVO.setReducedPension(lObjTrnPensionSupplyBillDtls.getNetAmount());
					lObjTrnPensionBillDtlsVO.setAccountNo(lObjTrnPensionSupplyBillDtls.getAccountNo());
					lObjTrnPensionBillDtlsVO.setPensionerName(lObjTrnPensionSupplyBillDtls.getPartyName());
					
					lDblTotalAllocationAmt =  lObjTrnPensionSupplyBillDtls.getPensionAmount().doubleValue() - lObjTrnPensionSupplyBillDtls.getTotalCvpAmount().doubleValue();
					lDblAllcBf36Amt = lDblTotalAllocationAmt * (lBgDcmlAllocationPercent[0].doubleValue() / 100);
					lDblAllcAf36Amt = lDblTotalAllocationAmt * (lBgDcmlAllocationPercent[1].doubleValue() / 100);
					lDblAllcAf56Amt = lDblTotalAllocationAmt * (lBgDcmlAllocationPercent[2].doubleValue() / 100);
					lDblAllcAf60Amt = lDblTotalAllocationAmt * (lBgDcmlAllocationPercent[3].doubleValue() / 100);
					lDblAllcAfZPAmt = lDblTotalAllocationAmt * (lBgDcmlAllocationPercent[4].doubleValue() / 100);
					
					lObjTrnPensionBillDtlsVO.setAllcationBf11156(new BigDecimal(lDblAllcAf36Amt).setScale(0, BigDecimal.ROUND_HALF_UP));
					lObjTrnPensionBillDtlsVO.setAllcationAf11156(new BigDecimal(lDblAllcAf56Amt).setScale(0, BigDecimal.ROUND_HALF_UP));
					lObjTrnPensionBillDtlsVO.setAllcationAf10560(new BigDecimal(lDblAllcAf60Amt).setScale(0, BigDecimal.ROUND_HALF_UP));
					lObjTrnPensionBillDtlsVO.setAllcationBf1436(new BigDecimal(lDblAllcBf36Amt).setScale(0, BigDecimal.ROUND_HALF_UP));
					lObjTrnPensionBillDtlsVO.setAllcationAfZp(new BigDecimal(lDblAllcAfZPAmt).setScale(0, BigDecimal.ROUND_HALF_UP));
					
//					lObjTrnPensionBillDtlsVO.setPersonalPensionAmount(lObjTrnPensionChangeDtls.getPersonalPensionAmount());
					lObjTrnPensionBillDtlsVO.setIr1Amount(lObjTrnPensionSupplyBillDtls.getIr1Amount());
					lObjTrnPensionBillDtlsVO.setIr2Amount(lObjTrnPensionSupplyBillDtls.getIr2Amount());
					lObjTrnPensionBillDtlsVO.setIr3Amount(lObjTrnPensionSupplyBillDtls.getIr3Amount());
					lObjTrnPensionBillDtlsVO.setPayForMonth(Integer.parseInt(new SimpleDateFormat("yyyyMM").format(gDtCurrDt)));
					
					lObjTrnPensionBillDtlsVO.setNetAmount(lObjTrnPensionSupplyBillDtls.getNetAmount());
//					lObjTrnPensionBillDtlsVO.setDaRate(lObjTrnPensionChangeDtls.getDaRate());
					lObjTrnPensionBillDtlsVO.setGrossAmount(lObjTrnPensionSupplyBillDtls.getGrossAmount());
					lObjTrnPensionBillDtlsVO.setHeadCode(lObjTrnPensionSupplyBillDtls.getHeadCode());
					lObjTrnPensionBillDtlsVO.setAdpAmount(lObjTrnPensionSupplyBillDtls.getAdpAmount());
					lObjTrnPensionBillDtlsVO.setLedgerNo(lObjTrnPensionSupplyBillDtls.getLedgerNo());
					lObjTrnPensionBillDtlsVO.setPageNo(lObjTrnPensionSupplyBillDtls.getPageNo());
					lObjTrnPensionBillDtlsVO.setPeonAllowance(lObjTrnPensionSupplyBillDtls.getPeonAllowance());
					lObjTrnPensionBillDtlsVO.setMedicalAllowenceAmount(lObjTrnPensionSupplyBillDtls.getMedicalAllowance());
					lObjTrnPensionBillDtlsVO.setOtherBenefits(lObjTrnPensionSupplyBillDtls.getOtherBenefit());
//					lObjTrnPensionBillDtlsVO.setPensnCutAmount(lObjTrnPensionSupplyBillDtls.getPensionCutAmount());
//					lObjTrnPensionBillDtlsVO.setOther1(lObjTrnPensionChangeDtls.getOther1());
//					
//					lObjTrnPensionBillDtlsVO.setRopType(lObjTrnPensionChangeDtls.getRopType());
					lObjTrnPensionBillDtlsVO.setArrearAmount(lObjTrnPensionSupplyBillDtls.getArrearPension()); 
					lObjTrnPensionBillDtlsVO.setTiArrearAmount(lObjTrnPensionSupplyBillDtls.getArrearDA());
					lObjTrnPensionBillDtlsVO.setArrear6PC(lObjTrnPensionSupplyBillDtls.getArrear6PC());
//					lObjTrnPensionBillDtlsVO.setArrearLC(lObjTrnPensionChangeDtls.getArrearLC());
					lObjTrnPensionBillDtlsVO.setArrearCommutation(lObjTrnPensionSupplyBillDtls.getArrearDiffComtPnsn());
					lObjTrnPensionBillDtlsVO.setArrearOthrDiff(lObjTrnPensionSupplyBillDtls.getArrearAnyOtherDiff());
					Long lLngTotalArrearAmt = lObjTrnPensionSupplyBillDtls.getArrearPension().longValue() + lObjTrnPensionSupplyBillDtls.getArrearDA().longValue()
											+ lObjTrnPensionSupplyBillDtls.getArrear6PC().longValue() + lObjTrnPensionSupplyBillDtls.getArrearDiffComtPnsn().longValue()
											+ lObjTrnPensionSupplyBillDtls.getArrearAnyOtherDiff().longValue();
					lObjTrnPensionBillDtlsVO.setTotalArrearAmt(new BigDecimal(lLngTotalArrearAmt));
					
					if(lMapSupplBillDtls.get(lStrKey) != null)
					{
						lLstTrnPensionBillDtls = lMapSupplBillDtls.get(lStrKey);
						lLstTrnPensionBillDtls.add(lObjTrnPensionBillDtlsVO);
					}
					else
					{
						lLstTrnPensionBillDtls = new ArrayList<TrnPensionBillDtls>();
						lLstTrnPensionBillDtls.add(lObjTrnPensionBillDtlsVO);
						lMapSupplBillDtls.put(lStrKey, lLstTrnPensionBillDtls);
					}
					//lObjTrnPensionBillDtlsVO.setPunishmentCutAmt(lObjTrnPensionChangeDtls.getPunishmentCutAmt());  	
	
										
					//Prepare Map of schemecode and payment mode wise branch headcode mapping list
					if(lMapSchemeBranchHeadMpg.get(lStrSchemePayModeKey) != null)
					{
						lMapBranchHeadMpg = lMapSchemeBranchHeadMpg.get(lStrSchemePayModeKey);
						if(lMapBranchHeadMpg.get(lObjTrnPensionSupplyBillDtls.getBranchCode()) != null)
						{
							lLstHeadCode = lMapBranchHeadMpg.get(lObjTrnPensionSupplyBillDtls.getBranchCode());
							lLstHeadCode.add(lObjTrnPensionSupplyBillDtls.getHeadCode());
						}
						else
						{
							lLstHeadCode = new ArrayList<BigDecimal>();
							lLstHeadCode.add(lObjTrnPensionSupplyBillDtls.getHeadCode());
							lMapBranchHeadMpg.put(lObjTrnPensionSupplyBillDtls.getBranchCode(), lLstHeadCode);
						}
					}
					else
					{
						lMapBranchHeadMpg = new HashMap<String, List<BigDecimal>>();
						lLstHeadCode = new ArrayList<BigDecimal>();
						lLstHeadCode.add(lObjTrnPensionSupplyBillDtls.getHeadCode());
						lMapBranchHeadMpg.put(lObjTrnPensionSupplyBillDtls.getBranchCode(), lLstHeadCode);
						lMapSchemeBranchHeadMpg.put(lStrSchemePayModeKey, lMapBranchHeadMpg);
					}
					
					//Prepare map for scheme code, payment mode wise supplementary bill id list
					if(lMapSchemeSupplBillId.get(lStrSchemePayModeKey) != null)
					{
						lLstSupplBillId = lMapSchemeSupplBillId.get(lStrSchemePayModeKey);
						lLstSupplBillId.add(lObjTrnPensionSupplyBillDtls.getPensionSupplyBillId());
					}
					else
					{
						lLstSupplBillId = new ArrayList<Long>();
						lLstSupplBillId.add(lObjTrnPensionSupplyBillDtls.getPensionSupplyBillId());
						lMapSchemeSupplBillId.put(lStrSchemePayModeKey, lLstSupplBillId);
					}
				}
			}
			
			for(String lStrMainKey : lMapSchemeBranchHeadMpg.keySet())
			{
				//Prepare List of TrnBillRegister VO
				lObjTrnBillRegisterVO = lMapSchemCodePayModeWise.get(lStrMainKey);
				Long lLngPkCntBillRegister = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_bill_register", inputMap, 1);
				Long lLngBillNo = ++lLngPkCntBillRegister;
				lLngBillNo = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngBillNo, inputMap);
				lObjTrnBillRegisterVO.setBillNo(lLngBillNo);
				lLstTrnBillRegister.add(lObjTrnBillRegisterVO);
				
				//prepare list of RltBillParty VO
				lObjRltBillParty = lMapRltBillParty.get(lStrMainKey);
				Long lLngPkCntnBillParty = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("rlt_bill_party", inputMap, lLstRltBillParty.size());
				Long lLngBillPartyId = ++lLngPkCntnBillParty;
				lLngBillPartyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngBillPartyId, inputMap);
				
				lObjRltBillParty.setBillPartyId(lLngBillPartyId);
				lObjRltBillParty.setBillNo(lLngBillNo);
				lLstRltBillPartyVO.add(lObjRltBillParty);
				
				
				lMapBranchHeadMpg = lMapSchemeBranchHeadMpg.get(lStrMainKey);
				for(String lStrBranchKey : lMapBranchHeadMpg.keySet())
				{
					lLstHeadCode = lMapBranchHeadMpg.get(lStrBranchKey);
					for(int lIntCnt = 0;lIntCnt < lLstHeadCode.size();lIntCnt++)
					{
						//Prepare List of TrnPensionBillHdr VO
						String lStrHeadKey = lStrMainKey + "~" + lStrBranchKey + "~" + lLstHeadCode.get(lIntCnt);
						lObjTrnPensionBillHdrVO = lMapBranchHeadWise.get(lStrHeadKey);
						Long lLngBillHdrId = IFMSCommonServiceImpl.getNextSeqNum("trn_pension_bill_hdr", inputMap);
						lObjTrnPensionBillHdrVO.setTrnPensionBillHdrId(lLngBillHdrId);
						lObjTrnPensionBillHdrVO.setBillNo(lLngBillNo);
						lLstTrnPensionBillHdr.add(lObjTrnPensionBillHdrVO);
						
						//prepare List of TrnPensionBillDtls VO
						lLstTrnPensionBillDtls = lMapSupplBillDtls.get(lStrHeadKey);
						Long lLngPkCntPensionBillDtls = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_pension_bill_dtls", inputMap, lLstTrnPensionBillDtls.size());
						for(int lIntCount = 0;lIntCount < lLstTrnPensionBillDtls.size();lIntCount++)
						{
							Long lLngBillDtlsId = ++lLngPkCntPensionBillDtls;
							lLngBillDtlsId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngBillDtlsId, inputMap);
							lObjTrnPensionBillDtlsVO = lLstTrnPensionBillDtls.get(lIntCount);
							lObjTrnPensionBillDtlsVO.setTrnPensionBillDtlsId(lLngBillDtlsId);
							lObjTrnPensionBillDtlsVO.setTrnPensionBillHdrId(lLngBillHdrId);
							lLstTrnPensionBillDtlsVO.add(lObjTrnPensionBillDtlsVO);
						}
						
					
					}
				}
				//update billno in Supplementary,recovery and six pay arrear table
				lLstSupplBillId = lMapSchemeSupplBillId.get(lStrMainKey);
				lObjSupplementaryBillDAO.updateBillNoInSupplDtls(lLstSupplBillId, lLngBillNo, DBConstants.ST_BILL_CREATED);
				lObjSupplementaryBillDAO.updateBillNoInRecoveryDtls(lLstSupplBillId, lLngBillNo);
				lObjSupplementaryBillDAO.updateBillNoInSixPayArrearDtls(lLstSupplBillId, lLngBillNo);
			}
			
			
			for (TrnBillRegister lObjTrnBillRegister : lLstTrnBillRegister) {
				hitStg.save(lObjTrnBillRegister);
			}
			lLstTrnBillRegister = null;
			for (TrnPensionBillHdr lObjTrnPensionBillHdr : lLstTrnPensionBillHdr) {
				hitStg.save(lObjTrnPensionBillHdr);
			}
			lLstTrnPensionBillHdr = null;

			for (TrnPensionBillDtls lObjTrnPensionBillDtls : lLstTrnPensionBillDtlsVO) {
				hitStg.save(lObjTrnPensionBillDtls);
			}
			lLstTrnPensionBillDtls = null;
			
			for (RltBillParty lObjRltBillPartyVO : lLstRltBillPartyVO) {
				hitStg.save(lObjRltBillPartyVO);
			}
			lLstRltBillPartyVO = null;
			
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>Bill generated Successfully.");
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("</XMLDOC>");
			
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
			
		}catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is : " + e, e);
			objRes.setThrowable(e);
			objRes.setResultValue(null);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
	}
	return objRes;
	}
	
	/**
	 * Approve or reject supplementary Pension bill
	 * @param inputMap
	 * @return
	 */
	public ResultObject approveRejectSupplPensionBill(Map<String, Object> inputMap) {
		
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnBillRegister lObjTrnBillRegisterVO = new TrnBillRegister();
		Long lLngBillNo = null;
		StringBuilder lStrBldXML = new StringBuilder();
		try {
			setSessionInfo(inputMap);
			String lStrBillNo = StringUtility.getParameter("billNo", request);
			String lStrApproveRejectFlag = StringUtility.getParameter("ApproveRejectFlag", request);
			SupplementaryBillDAO lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnBillRegister.class, serv.getSessionFactory());
			
			if(!"".equals(lStrBillNo))
			{
				lLngBillNo = Long.parseLong(lStrBillNo);
				lObjTrnBillRegisterVO = (TrnBillRegister) lObjSupplementaryBillDAO.read(lLngBillNo);
				if("A".equals(lStrApproveRejectFlag))
				{
					lObjTrnBillRegisterVO.setCurrBillStatus(DBConstants.ST_BILL_APPROVED);
					lObjTrnBillRegisterVO.setUpdatedUserId(gLngUserId);
					lObjTrnBillRegisterVO.setUpdatedPostId(gLngPostId);
					lObjTrnBillRegisterVO.setUpdatedDate(gDtCurrDt);
					lObjSupplementaryBillDAO.update(lObjTrnBillRegisterVO);
					
					lObjSupplementaryBillDAO.updateStatusInSupplBillDtls(lLngBillNo, DBConstants.ST_BILL_APPROVED, new BigDecimal(gLngUserId), new BigDecimal(gLngPostId), gDtCurrDt);
					lObjSupplementaryBillDAO.updatePaidFlagInSixPayFromBillNo(lLngBillNo, new BigDecimal(gLngUserId), new BigDecimal(gLngPostId), gDtCurrDt);					
				}
				if("R".equals(lStrApproveRejectFlag))
				{
					lObjTrnBillRegisterVO.setCurrBillStatus(DBConstants.ST_BILL_REJECTED);
					lObjTrnBillRegisterVO.setUpdatedUserId(gLngUserId);
					lObjTrnBillRegisterVO.setUpdatedPostId(gLngPostId);
					lObjTrnBillRegisterVO.setUpdatedDate(gDtCurrDt);
					lObjSupplementaryBillDAO.update(lObjTrnBillRegisterVO);
					
					lObjSupplementaryBillDAO.updateStatusInSupplBillDtls(lLngBillNo, DBConstants.ST_BILL_REJECTED, new BigDecimal(gLngUserId), new BigDecimal(gLngPostId), gDtCurrDt);
											
				}
				
			}
								
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>");
			if("A".equals(lStrApproveRejectFlag))
			{
				lStrBldXML.append("Bill Approved Successfully and forwarded to Auditor.");
			}
			if("R".equals(lStrApproveRejectFlag))
			{
				lStrBldXML.append("Bill Rejected Successfully and forwarded to Auditor.");
			}
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("</XMLDOC>");
			
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
			
		}catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			objRes.setThrowable(e);
			objRes.setResultValue(null);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
	}
	return objRes;
	}
	
	private String getBillCntrlNoPrefix() {

		String lStrLocShortName = gStrLocShortName;
		String lStrDate = "";
		String lStrBillCntrlNoPrefix = "";
		NumberFormat f = new DecimalFormat("#00");
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(new Date(System.currentTimeMillis()));
		lStrDate = f.format(gc.get(gc.MONTH) + 1) + f.format(gc.get(gc.YEAR));
		lStrBillCntrlNoPrefix = lStrLocShortName + "/" + lStrDate + "/";
		return lStrBillCntrlNoPrefix;
	}
	
	/**
	 * View Supplementary request list
	 * @param inputMap
	 * @return
	 */
	public ResultObject viewSupplRequestList(Map<String, Object> inputMap) {
		
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		Integer lIntTotalRecords=0;
		List lLstTrnPensionSupplyBillDtls = new ArrayList();
		try {
			setSessionInfo(inputMap);
			String lStrElementId = StringUtility.getParameter("elementId", request).trim();
			
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			SupplementaryBillDAO lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnPensionSupplyBillDtls.class, serv.getSessionFactory());
			String lStrCurrRoleId = lObjCommonPensionDAO.getRoleByElement(lStrElementId);
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			lIntTotalRecords = lObjSupplementaryBillDAO.getSupplRequestCount(gLngPostId, gStrLocCode);
			if(lIntTotalRecords > 0)
			{
				lLstTrnPensionSupplyBillDtls = lObjSupplementaryBillDAO.getSupplRequestList(displayTag,gLngPostId, gStrLocCode);
			}
			inputMap.put("totalRecords", lIntTotalRecords);
			inputMap.put("PensionSupplyRequestList", lLstTrnPensionSupplyBillDtls);
			inputMap.put("CurrRole", lStrCurrRoleId);
			
			objRes.setResultValue(inputMap);
			objRes.setViewName("SupplPensionBillRequestList");
			
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			objRes.setThrowable(e);
			objRes.setResultValue(null);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}
	
	/**
	 * View Supplementary request details from request number
	 * @param inputMap
	 * @return
	 */
	public ResultObject viewSupplRequestDtlsFromRequestNo(Map<String, Object> inputMap) {
		
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		Integer lIntTotalRecords=0;
		List<TrnPensionSupplyBillDtls> lLstTrnPensionSupplyBillDtls = new ArrayList<TrnPensionSupplyBillDtls>();
		TrnPensionSupplyBillDtls lObjTrnPensionSupplyBillDtls = new TrnPensionSupplyBillDtls();
		String lStrDisplayFlag = "Y";
		try {
			setSessionInfo(inputMap);
			String lStrRequestNo = StringUtility.getParameter("requestNo", request);
			String lStrCurrRole = StringUtility.getParameter("CurrRole", request);
			SupplementaryBillDAO lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnPensionSupplyBillDtls.class, serv.getSessionFactory());
			
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			lIntTotalRecords = lObjSupplementaryBillDAO.getSupplRequestDtlsFromRequestNoCount(lStrRequestNo, gLngPostId, gStrLocCode);
			if(lIntTotalRecords > 0)
			{
				lLstTrnPensionSupplyBillDtls = lObjSupplementaryBillDAO.getSupplRequestDtlsFromRequestNo(displayTag, lStrRequestNo, gLngPostId, gStrLocCode);
				lObjTrnPensionSupplyBillDtls = lLstTrnPensionSupplyBillDtls.get(0);
				if(lObjTrnPensionSupplyBillDtls.getBillNo() != null)
				{
					lStrDisplayFlag = "N";
				}
			}
					
			inputMap.put("totalRecords", lIntTotalRecords);
			inputMap.put("DisplayFlag",lStrDisplayFlag);
			inputMap.put("PensionSupplyRequestDtls", lLstTrnPensionSupplyBillDtls);
			inputMap.put("RequestNo", lStrRequestNo);
			inputMap.put("CurrRole", lStrCurrRole);
					
			objRes.setResultValue(inputMap);
			objRes.setViewName("SupplPensionBillRequestDtls");
			
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			objRes.setThrowable(e);
			objRes.setResultValue(null);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}
	
	/**
	 * View Supplementary Pension bill data
	 * @param inputMap
	 * @return
	 */
	public ResultObject viewSupplPensionBillData(Map<String, Object> inputMap) {
		
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPensionSupplyBillDtls lObjTrnPensionSupplyBillDtls = new TrnPensionSupplyBillDtls();
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtls = new ArrayList<TrnPensionRecoveryDtls>();
		List<CmnLookupMst> lLstRecoveryType = new ArrayList<CmnLookupMst>();
		List<ComboValuesVO> lLstBanks = null;
		String lStrBranchName = "";
		String lStrShowReadOnly = "";
		try {
			setSessionInfo(inputMap);
			String lStrCurrRole = StringUtility.getParameter("CurrRole", request);
			String lStrPensionSupplBillId = StringUtility.getParameter("supplBillId", request);
			lStrShowReadOnly  = StringUtility.getParameter("showReadOnly", request);
			// Getting Recovery Dtls for the pensioner
			lLstRecoveryType = IFMSCommonServiceImpl.getLookupValues("RECVTYPE", gLangID, inputMap);
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			lLstBanks = lObjCommonDAO.getBankList(inputMap, gLangID);
			SupplementaryBillDAO lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnPensionSupplyBillDtls.class, serv.getSessionFactory());
			
			lObjTrnPensionSupplyBillDtls = (TrnPensionSupplyBillDtls) lObjSupplementaryBillDAO.read(Long.parseLong(lStrPensionSupplBillId));
			
			lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnPensionRecoveryDtls.class, serv.getSessionFactory());
			lLstTrnPensionRecoveryDtls = lObjSupplementaryBillDAO.getListByColumnAndValue("pensionSupplyBillId", Long.parseLong(lStrPensionSupplBillId));
			
			lStrBranchName = lObjCommonDAO.getBranchNameFromBranchCode(lObjTrnPensionSupplyBillDtls.getBankCode(), lObjTrnPensionSupplyBillDtls.getBranchCode(), gLangID);
			
			inputMap.put("TrnPensionSupplyBillDtls", lObjTrnPensionSupplyBillDtls);
			inputMap.put("RecoveryDtls", lLstTrnPensionRecoveryDtls);
			inputMap.put("SuppBillType", "PENSION");
			inputMap.put("BankList", lLstBanks);
			inputMap.put("BranchName", lStrBranchName);
			inputMap.put("RecoveryType", lLstRecoveryType);
			inputMap.put("showReadOnly", lStrShowReadOnly);
			inputMap.put("currRole", lStrCurrRole);
			
			objRes.setResultValue(inputMap);
			objRes.setViewName("SupplementaryBill");
			
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			objRes.setThrowable(e);
			objRes.setResultValue(null);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}
	
	
	
	/**
	 * Saves Supplementary Bill Data in
	 * 
	 * 
	 * @param Map
	 *            :lMapInput
	 * @return ResultObject
	 */
	public ResultObject saveSuppBill(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPensionBillHdrDAO lObjTrnPensionBillHdrDAO = new TrnPensionBillHdrDAOImpl(TrnPensionBillHdr.class, serv.getSessionFactory());
		TrnPensionBillDtlsDAO lObjTrnPensionBillDtlsDAO = new TrnPensionBillDtlsDAOImpl(TrnPensionBillDtls.class, serv.getSessionFactory());
		TrnPensionRecoveryDtlsDAO lObjTrnRecoveryDtls = new TrnPensionRecoveryDtlsDAOImpl(TrnPensionRecoveryDtls.class, serv.getSessionFactory());
		SupplementaryBillDAO lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnPensionSupplyBillDtls.class, serv.getSessionFactory());
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(HstPnsnPmntDcrgDtls.class, serv.getSessionFactory());
		TrnPensionBillHdr lObjTrnPensionBillHdr = null;
		TrnPensionBillDtls lObjTrnPensionBillDtls = null;

		Long lPnsnTokenNo = (Long) inputMap.get("PnsnTokenNo");
		String lStrBillType = bundleConst.getString("RECOVERY.SUPP");
		List<RltBillParty> lLstBillParty = new ArrayList();
		TrnPensionSupplyBillDtls lObjTrnPensionSupplyBillDtls = null;
		RltPensioncaseBillDAO lObjBillCasDao = null;
		RltPensioncaseBill lObjRltPensioncaseBill = null;
		HstPnsnPmntDcrgDtls lObjHstPnsnPmntDcrgDtls= null;
		HstCommutationDtls lObjHstCommutationDtls = null;
		Long lRltPensioncaseBillId = null;
		RltBillParty lObjBillParty = null;
		Long lLngBillPartyId = null;
		String lStrBillNo = null;
		Long lLngBillNo = null;
		String lStrShowReadOnly = null;
		String lStrSuppBillType = "";
		Long lLngDcrgDtlsId = null;
		String lStrSixPayCalcFlag = "";
		try {
			setSessionInfo(inputMap);
			lStrSixPayCalcFlag = StringUtility.getParameter("hdnSixPayCalcFlag", request);
			// Getting VO Object from inputMap with the key
			// "DCRGTrnPensionBillDtlsVO"
			if (inputMap.containsKey("showReadOnly")) {
				lStrShowReadOnly = inputMap.get("showReadOnly").toString();
			}
			if (inputMap.containsKey("billNo")) {
				lStrBillNo = inputMap.get("billNo").toString();
			}
			if (lStrBillNo != null && lStrBillNo.length() > 0) {
				lLngBillNo = Long.parseLong(lStrBillNo);
			}
			lObjTrnPensionBillHdr = (TrnPensionBillHdr) inputMap.get("SuppTrnPensionBillHdrVO");
			lObjTrnPensionBillDtls = (TrnPensionBillDtls) inputMap.get("SuppTrnPensionBillDtlsVO");
			lObjTrnPensionSupplyBillDtls = (TrnPensionSupplyBillDtls) inputMap.get("SuppTrnPensionSupplyBillDtlsVO");
			lStrSuppBillType = inputMap.get("SuppBillType").toString();
			if("DCRG".equals(lStrSuppBillType))
				lObjHstPnsnPmntDcrgDtls = (HstPnsnPmntDcrgDtls)inputMap.get("HstPnsnPmntDcrgDtls");
			if("CVP".equals(lStrSuppBillType))
				lObjHstCommutationDtls = (HstCommutationDtls)inputMap.get("HstCommutationDtls");
			Long lLngTrnPensionRecoveryDtlsId = null;
			RltBillPartyDAO lObjBillPartyDAO = new RltBillPartyDAOImpl(RltBillParty.class, serv.getSessionFactory());

			if (lLngBillNo != null && lLngBillNo > 0) {
				// insert Data into TrnPensionBillHdr.

				if (lPnsnTokenNo != null && lPnsnTokenNo.toString().length() > 0)

				{
					lObjTrnPensionBillHdr.setTokenNo(lPnsnTokenNo.toString());
				}
				if ("N".equals(lStrShowReadOnly)) {

					lObjTrnPensionBillHdrDAO.update(lObjTrnPensionBillHdr);
					lObjTrnPensionBillDtlsDAO.update(lObjTrnPensionBillDtls);
					lObjSupplementaryBillDAO.update(lObjTrnPensionSupplyBillDtls);
					if("DCRG".equals(lStrSuppBillType))
						lObjPhysicalCaseInwardDAO.update(lObjHstPnsnPmntDcrgDtls);
//					if("DCRG".equals(lStrSuppBillType))
//					{
//						if(lObjHstPnsnPmntDcrgDtls != null)
//						{
//							lLngDcrgDtlsId = lObjHstPnsnPmntDcrgDtls.getDcrgDtlsId();
//						}
//						if(lLngDcrgDtlsId == null || "".equals(lLngDcrgDtlsId))
//						{
//							lLngDcrgDtlsId = IFMSCommonServiceImpl.getNextSeqNum("hst_pnsnpmnt_dcrg_dtls", inputMap);
//							lObjHstPnsnPmntDcrgDtls.setDcrgDtlsId(lLngDcrgDtlsId);
//							lObjHstPnsnPmntDcrgDtls.setBillNo(lLngBillNo);
//							lObjPhysicalCaseInwardDAO.create(lObjHstPnsnPmntDcrgDtls);
//						}
//						else
//						{
//							lObjPhysicalCaseInwardDAO.update(lObjHstPnsnPmntDcrgDtls);
//						}
//					}
					if("CVP".equals(lStrSuppBillType))
					{
						lObjPhysicalCaseInwardDAO =new PhysicalCaseInwardDAOImpl(HstCommutationDtls.class, serv.getSessionFactory());
						lObjPhysicalCaseInwardDAO.update(lObjHstCommutationDtls);
						
					}

					// for (int i = 0; i < lLstObjRecovery.size(); i++) {
					// TrnPensionRecoveryDtls lObjRecovery = new
					// TrnPensionRecoveryDtls();
					// lObjRecovery = lLstObjRecovery.get(i);
					// lObjTrnRecoveryDtls.delete(lObjRecovery);
					// }

					// Deleting previous party Info
					lLstBillParty = lObjBillPartyDAO.getPartyByBill(lLngBillNo);
					Iterator<RltBillParty> lItrBillParty = lLstBillParty.iterator();

					while (lItrBillParty.hasNext()) {
						lObjBillPartyDAO.delete(lItrBillParty.next());
					}
					if (inputMap.get("BillPartyDtls") != null) {
						lLstBillParty = (List<RltBillParty>) inputMap.get("BillPartyDtls");
						lItrBillParty = lLstBillParty.iterator();

						while (lItrBillParty.hasNext()) {
							lObjBillParty = lItrBillParty.next();
							lObjBillParty.setBillNo(lLngBillNo);
							lLngBillPartyId = IFMSCommonServiceImpl.getNextSeqNum("rlt_bill_party", inputMap);
							lObjBillParty.setBillPartyId(lLngBillPartyId);
							lObjBillPartyDAO.create(lObjBillParty);
						}
					}

				} else {
					// TrnPensionBillHdr
					lObjTrnPensionBillHdr.setBillType(lStrBillType);
					lObjTrnPensionBillHdr.setBillNo(lLngBillNo);
					lObjTrnPensionBillHdr.setTrnPensionBillHdrId(IFMSCommonServiceImpl.getNextSeqNum("trn_pension_bill_hdr", inputMap));
					lObjTrnPensionBillHdrDAO.create(lObjTrnPensionBillHdr);

					// TrnPensionBillDtls
					lObjTrnPensionBillDtls.setTrnPensionBillDtlsId(IFMSCommonServiceImpl.getNextSeqNum("trn_pension_bill_dtls", inputMap));
					lObjTrnPensionBillDtls.setTrnPensionBillHdrId(lObjTrnPensionBillHdr.getTrnPensionBillHdrId());
					lObjTrnPensionBillDtlsDAO.create(lObjTrnPensionBillDtls);

					// TrnPensionSupplyBillDtls
					Long lLngPensionSupplyBillId = IFMSCommonServiceImpl.getNextSeqNum("trn_pension_supply_bill_dtls", inputMap);
					lObjTrnPensionSupplyBillDtls.setPensionSupplyBillId(lLngPensionSupplyBillId);
					lObjTrnPensionSupplyBillDtls.setBillNo(lLngBillNo);
					lObjSupplementaryBillDAO.create(lObjTrnPensionSupplyBillDtls);

					// RltPensioncaseBill

					lObjBillCasDao = new RltPensioncaseBillDAOImpl(RltPensioncaseBill.class, serv.getSessionFactory());
					lObjRltPensioncaseBill = (RltPensioncaseBill) inputMap.get("SuppRltPensioncaseBillVO");
					lRltPensioncaseBillId = IFMSCommonServiceImpl.getNextSeqNum("rlt_pensioncase_bill", inputMap);
					lObjRltPensioncaseBill.setRltPensioncaseBillId(lRltPensioncaseBillId);
					lObjRltPensioncaseBill.setBillNo(lLngBillNo);
					lObjBillCasDao.create(lObjRltPensioncaseBill);
					
					// HstPnsnPmntDcrgDtls
					if("DCRG".equals(lStrSuppBillType))
					{
						lLngDcrgDtlsId = IFMSCommonServiceImpl.getNextSeqNum("hst_pnsnpmnt_dcrg_dtls", inputMap);
						lObjHstPnsnPmntDcrgDtls.setDcrgDtlsId(lLngDcrgDtlsId);
						lObjHstPnsnPmntDcrgDtls.setBillNo(lLngBillNo);
						lObjPhysicalCaseInwardDAO.create(lObjHstPnsnPmntDcrgDtls);
					}
					
					// HstCommutationDtls
					if("CVP".equals(lStrSuppBillType))
					{
						lObjPhysicalCaseInwardDAO =new PhysicalCaseInwardDAOImpl(HstCommutationDtls.class, serv.getSessionFactory());
						Long lLngCvpDtlsId = IFMSCommonServiceImpl.getNextSeqNum("hst_commutation_dtls", inputMap);
						lObjHstCommutationDtls.setCvpDtlsId(lLngCvpDtlsId);
						lObjHstCommutationDtls.setBillNo(lLngBillNo);
						lObjPhysicalCaseInwardDAO.create(lObjHstCommutationDtls);
					}

				}
				//Insert six pay arrear config
				if("Y".equalsIgnoreCase(lStrSixPayCalcFlag))
				{
					inputMap.put("BillNo", lLngBillNo);
					objRes = serv.executeService("SIXTH_PAY_ARREAR_VOGEN", inputMap);

					objRes = serv.executeService("SAVE_SIXTH_PAY_ARREAR", inputMap);
				}
			}

			inputMap.put("SuppTrnPensionBillDtlsVO", lObjTrnPensionBillDtls);

			inputMap.put("TrnPensionSupplyBillDtls", lObjTrnPensionSupplyBillDtls);

			// TrnPensionRecoveryDtls

			List<TrnPensionRecoveryDtls> lLstObjRecovery = (List<TrnPensionRecoveryDtls>) inputMap.get("SuppTrnPensionRecoveryDtls");
			if (!lLstObjRecovery.isEmpty()) {
				for (int i = 0; i < lLstObjRecovery.size(); i++) {

					TrnPensionRecoveryDtls lObjRecovery = new TrnPensionRecoveryDtls();
					lLngTrnPensionRecoveryDtlsId = IFMSCommonServiceImpl.getNextSeqNum("trn_pension_recovery_dtls", inputMap);
					lObjRecovery = lLstObjRecovery.get(i);
					lObjRecovery.setTrnPensionRecoveryDtlsId(lLngTrnPensionRecoveryDtlsId);
					lObjRecovery.setBillNo(lLngBillNo);

					lObjTrnRecoveryDtls.create(lObjRecovery);
				}
			}
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			objRes.setThrowable(e);
			objRes.setResultValue(null);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

	public ResultObject viewSuppBillData(Map inputMap) {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		TrnPensionBillHdr lObjTrnPensionBillHdr = null;
		List<CmnLookupMst> lLstRecoveryType = new ArrayList();
		String lStrRcvryFlag = "";
		Map lMapDCRGData = new HashMap();
		String lStrPPONo = null;
		String lStrPnsrCode = null;
		Double lSuppAmt = 0D;
		Double lReducedAmt = 0D;
		Double lRecAmt = 0D;
		Date lStrBillDate = null;
		Long lTrnPensionBillHdrPK = null;
		String lStrPnsnrName = null;
		List<RltBillParty> lLstRltBillPartyVO = null;
		RltBillParty lObjRltBillParty = null;
		String lStrBillType = "";
		List<SupplementaryPartyDtlsVO> lLstSupplementaryPartyDtlsVO = null;
		SupplementaryPartyDtlsVO lObjSupplementaryPartyDtlsVO = null;
		List lLstBillDtlsElem = new ArrayList();
		Iterator lObjIterator = null;
		PensionBillDAO lObjPensionBillDAO = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
		TrnPensionBillHdrDAO lObjTrnPensionBillHdrDAO = new TrnPensionBillHdrDAOImpl(TrnPensionBillHdr.class, srvcLoc.getSessionFactory());
		NewPensionBillDAO lObjNewPensionBillDAO = new NewPensionBillDAOImpl(srvcLoc.getSessionFactory());
		MstPensionerHdrDAO lObjMstPensionerHdrDAO = new MstPensionerHdrDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
		CVPRestorationLetterDAO lObjCVPRestorationLetterDAO = new CVPRestorationLetterDAOImpl(null, srvcLoc.getSessionFactory());
		List<ComboValuesVO> lLstBanks = null;
		CommonDAO lObjCommonDAO = new CommonDAOImpl(srvcLoc.getSessionFactory());
		String lStrCurrRoleId = null;
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtls = null;
		List<Object> lLstSchemeWiseRecoveryDtls = new ArrayList<Object>();
		String lStrPaymentMode = null;
		TrnBillRegister lObjTrnBillRegister = new TrnBillRegister();
		TrnPensionSupplyBillDtls lObjTrnPensionSupplyBillDtls = null;
		TrnPensionBillDtls lObjTrnPensionBillDtls = new TrnPensionBillDtls();
		StringBuilder lSbCvpBill =new StringBuilder();
		String lStrPensionerName = "";
		Double lArrearAmt = 0.0;
		Double lOther2 = 0.0;
		Double lOther3 = 0.0;
		CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(srvcLoc.getSessionFactory());
		String lStrArrearDtls = null;
		String lStrElementCode = "";
		String lStrSchemeCode = "";
		String lStrBankName = "";
		String lStrBranchName = "";
		String lStrAccountNo = "";
		String lStrRequestNo = "";
		try {
			setSessionInfo(inputMap);
			Long lStrBillNo = Long.parseLong(inputMap.get("billNo").toString());
			lLstBanks = lObjCommonDAO.getBankList(inputMap, gLangID);

			lStrElementCode = StringUtility.getParameter("elementId", request).trim();
			OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(TrnEcsDtl.class, srvcLoc.getSessionFactory());
			lStrCurrRoleId = lObjCommonPensionDAO.getRoleByElement(lStrElementCode);
			// lStrCurrRoleId =
			// lObjOnlinePensionCaseDAO.getRoleByPost(gLngPostId);
			lStrRcvryFlag= bundleConst.getString("RECOVERY.SUPP");
			if (lStrBillNo != null) {
				// Getting the ObjectVo of TrnPensionBillDtlsVO
				lObjTrnPensionBillHdr = new TrnPensionBillHdr();
				lObjTrnPensionBillHdr = lObjTrnPensionBillHdrDAO.getTrnPensionBillHdr(lStrBillNo, lStrRcvryFlag);
				lStrBillDate = lObjTrnPensionBillHdr.getBillDate();
				lTrnPensionBillHdrPK = lObjTrnPensionBillHdr.getTrnPensionBillHdrId();
				lObjTrnPensionBillDtls = (TrnPensionBillDtls) lObjNewPensionBillDAO.getTrnPensionBillDtls(lTrnPensionBillHdrPK);
				lStrPnsrCode = lObjTrnPensionBillDtls.getPensionerCode();
				lSuppAmt = lObjTrnPensionBillDtls.getReducedPension().doubleValue() + lObjTrnPensionBillDtls.getRecoveryAmount().doubleValue();
				inputMap.put("TrnPensionBillHdr", lObjTrnPensionBillHdr);
				
			}
			inputMap.put("TrnPensionBillDtls", lObjTrnPensionBillDtls);
			

			if (lStrPnsrCode != null && lStrPnsrCode.length() > 0) {
				lObjMstPensionerHdrDAO.getMstPensionerHdrDtls(lStrPnsrCode);

				// Getting the ObjectVo of MstPensionerHdr... End...
			}
			if (inputMap.containsKey("RltBillParty")) {
				lLstRltBillPartyVO = (List<RltBillParty>) inputMap.get("RltBillParty");
			}
			lObjIterator = lLstRltBillPartyVO.iterator();
			lLstSupplementaryPartyDtlsVO = new ArrayList();

			lObjTrnPensionSupplyBillDtls = lObjPensionBillDAO.getSupplyDtlsVO(lStrPnsrCode, lStrBillNo, gStrLocCode);
			lStrBillType=lObjTrnPensionSupplyBillDtls.getBillType();
			
			if(!"PENSION".equals(lStrBillType))
			{
				while (lObjIterator.hasNext()) {
					lObjRltBillParty = (RltBillParty) lObjIterator.next();
					String lStrPartyName = lObjRltBillParty.getPartyName();
					String lStrBankCode = lObjRltBillParty.getBankCode();
					String lStrBranchCode = lObjRltBillParty.getBranchCode();
					Long lLngMicrCode = lObjRltBillParty.getMicrCode();
					BigDecimal lBdDcmlAmount = lObjRltBillParty.getPartyAmt();
					lStrPaymentMode = lObjRltBillParty.getPaymentMode();
					String lBranchName = lObjPensionBillDAO.getBranchName(lStrBankCode, lStrBranchCode, gStrLocCode);
					lStrBankName = lObjCommonDAO.getBankNameFromBankCode(lObjRltBillParty.getBankCode(), gLangID);
					lStrBranchName = lObjCommonDAO.getBranchNameFromBranchCode(lObjRltBillParty.getBankCode(), lObjRltBillParty.getBranchCode(), gLangID);
					lStrAccountNo = (lObjRltBillParty.getAccntNo() != null) ? lObjRltBillParty.getAccntNo() : "";
					lStrPensionerName = lStrPensionerName +" "+ lStrPartyName +" / "+lObjRltBillParty.getPartyAmt().longValue() +  " / " + lStrBankName + " / " + lStrBranchName + " / " + lStrAccountNo+"$"; 
					lObjSupplementaryPartyDtlsVO = new SupplementaryPartyDtlsVO(lStrPartyName, lStrBankCode, lBranchName, lStrAccountNo, lStrBranchCode, lLngMicrCode, lBdDcmlAmount);
					lLstSupplementaryPartyDtlsVO.add(lObjSupplementaryPartyDtlsVO);
				}
			}
			
			if(!"".equals(lStrBillType) && lStrBillType != null)
			{
				if("PENSION".equals(lStrBillType))
				{
					lStrRcvryFlag= bundleConst.getString("RECOVERY.SUPPPNSN");
					SupplementaryBillDAO lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnBillRegister.class, srvcLoc.getSessionFactory());
					lObjTrnBillRegister = (TrnBillRegister) lObjSupplementaryBillDAO.read(lStrBillNo);
					lLstSchemeWiseRecoveryDtls = lObjSupplementaryBillDAO.getSchemewiseRecoveryDtlsFromBillNo(lStrBillNo);
					lStrRequestNo = lObjTrnPensionSupplyBillDtls.getRequestNo();
				}
				if("DCRG".equals(lStrBillType))
				{
					lStrRcvryFlag= bundleConst.getString("RECOVERY.SUPPDCRG");
					/************************Print DCRG bill start*************/
					ReportExportHelper objExport = new ReportExportHelper();
					StringBuilder lSbDcrgBill =new StringBuilder();
					StringBuilder lSbHeader =new StringBuilder();
					StringBuilder lSbFooter = new StringBuilder();
					List<List> arrOuter = new ArrayList<List>();
					ArrayList lineList = new ArrayList();
					String lStrTreasuryName = "";
					String lStrNameOfOffice = "";
					String lStrHeader="";
					lStrSchemeCode = (lObjTrnPensionBillHdr.getSchemeCode() != null) ? lObjTrnPensionBillHdr.getSchemeCode() : "";
					
					lSbHeader.append("Form M.T.R.45 A");
					lSbHeader.append("\r\n");
					lSbHeader.append("( See Rule 406 A )");
					lSbHeader.append("\r\n");
					lSbHeader.append("Simple Receipt");
					lSbHeader.append("\r\n");
					
					lStrTreasuryName = "Name of the Treasury:-"+space(25)+"$"+SessionHelper.getLocationName(inputMap)+space(48 - SessionHelper.getLocationName(inputMap).length())+"$";	
					
					lStrTreasuryName= lStrTreasuryName + "Token No."+space(39)+"$"
										+"Date :"+space(43)+"$"
										+"Voucher No. "+space(38);
					String lStrOffAddr = lObjCVPRestorationLetterDAO.getOffiCeAddr(gStrLocCode);
					lStrNameOfOffice = "Name of the Office:- "+space(1)+lStrOffAddr;
					
					ColumnVo[] columnHeading = new ColumnVo[2];
					columnHeading[0] =  new ColumnVo(lStrTreasuryName,1,50,0,true,false,false);
					columnHeading[1] =  new ColumnVo(lStrNameOfOffice,1,27,0,true,false,false);
					
					lSbFooter.append("Demand No         : G6");
					lSbFooter.append("\r\n");
					lSbFooter.append("Major Head        : 2071 Pension & Other Retirement Benefits.");
					lSbFooter.append("\r\n");
					lSbFooter.append("Minor Head        : 101 Superannuation and Retirement Allowances.");
					lSbFooter.append("\r\n");
					lSbFooter.append("Sub   Head        : 104 DCRG");
					lSbFooter.append("\r\n");
					lSbFooter.append("Detailed Head     : 04");
					lSbFooter.append("\r\n");
					lSbFooter.append("Bill Type         :");
					lSbFooter.append("\r\n");
					lSbFooter.append("Scheme Code       : "+lStrSchemeCode);
					lSbFooter.append("\r\n");
					lSbFooter.append("( Object Expenditure )");
					lSbFooter.append("\r\n");
					lSbFooter.append(getChar(80, "-"));
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append(getChar(80, "-"));
					lSbFooter.append("\r\n");
					String lStrReducedAmt= "Received Sum of(Rs. "+lObjTrnPensionBillDtls.getNetAmount().longValue()+" /-) Rs. "+EnglishDecimalFormat.convertWithSpace(new BigDecimal(lObjTrnPensionBillDtls.getNetAmount().doubleValue())) +" Only";
					lSbFooter.append(lStrReducedAmt);
					lSbFooter.append("\r\n");
					lSbFooter.append(getChar(80, "-"));
					lSbFooter.append("\r\n");
					
					lSbDcrgBill.append("<div><pre>");
					lStrHeader = objExport.getReportFileForCvpBill(arrOuter, columnHeading,lSbHeader.toString(),
							lSbFooter.toString(),null,79 ,true,1);
					lSbDcrgBill.append(lStrHeader);
					
					
					arrOuter = new ArrayList<List>();
					lineList = new ArrayList();
					columnHeading = new ColumnVo[2];
					columnHeading[0] =  new ColumnVo("",1,50,0,true,false,false);
					columnHeading[1] =  new ColumnVo("",1,27,0,true,false,false);
					
					lStrHeader="";
					
					lineList = new ArrayList();
					lineList.add(getChar(51,"-"));
					lineList.add(getChar(27,"-"));
					arrOuter.add(lineList);
					
					lineList = new ArrayList();
					lineList.add("Name/Amount/Bank/Branch/AC.No : "+lStrPensionerName);
					lineList.add("Non Plan");
					arrOuter.add(lineList);
					
					lineList = new ArrayList();
					lineList.add("Authorised by A.G.Mah.Mumbai.	");
					lineList.add("B DCRG");
					arrOuter.add(lineList);
					
					lineList = new ArrayList();
					lineList.add("Authority No. " + lObjTrnPensionSupplyBillDtls.getGpoNo() + "  " +lObjSimpleDateFormat.format(lObjTrnPensionSupplyBillDtls.getGpoDate()));
					lineList.add("B 1-11-1956");
					arrOuter.add(lineList);
					
					lineList = new ArrayList();
					lineList.add(" ");
					lineList.add("A 1-11-1956");
					arrOuter.add(lineList);
					
					lineList = new ArrayList();
					lineList.add("Date : "+DateUtilities.stringFromDate(lStrBillDate));
					lineList.add("A 1-11-1956");
					arrOuter.add(lineList);
					
					lineList = new ArrayList();
					lineList.add("P.P.O. No.  "+lObjTrnPensionBillDtls.getPpoNo());
					lineList.add("");
					arrOuter.add(lineList);
					
					lineList = new ArrayList();
					lineList.add(getChar(51,"-"));
					lineList.add(getChar(27,"-"));
					arrOuter.add(lineList);
					
					lSbFooter = new StringBuilder();
					
					lSbFooter.append("Copy - enclosed                      Received Payment");
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append(String.format("%80s", "Signature & Designation"));
					lSbFooter.append("\r\n");
					lSbFooter.append(String.format("%80s", "A.P.A.O/ATO"));
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append(String.format("%50s", "For use in Treasury"));
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append(getChar(80, "-"));
					lSbFooter.append("\r\n");
					lSbFooter.append("Pay Rs.(  "+lObjTrnPensionBillDtls.getNetAmount().longValue()+" /-) Rupees : "+EnglishDecimalFormat.convertWithSpace(new BigDecimal(lObjTrnPensionBillDtls.getNetAmount().doubleValue())) + " Only");
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append(String.format("%70s","Asstt.Pay and Accounts Officer/Asstt. Treasury Officer."));
					lStrHeader = objExport.getReportFileForCvpBill(arrOuter, columnHeading,"",
							lSbFooter.toString(),null,79 ,true,26);
					lSbDcrgBill.append(lStrHeader);
					
					lSbDcrgBill.append("</pre></div>");
					inputMap.put("PrintBillString", lSbDcrgBill);

					/**********Print DCRG Bill End***********/
				}
				if("CVP".equals(lStrBillType))
				{
					//Print CVP bill data start
					
					ReportExportHelper objExport = new ReportExportHelper();
					
					StringBuilder lSbHeader =new StringBuilder();
					StringBuilder lSbFooter = new StringBuilder();
					List<List> arrOuter = new ArrayList<List>();
					ArrayList lineList = new ArrayList();
					String lStrTreasuryName = "";
					String lStrNameOfOffice = "";
					String lStrHeader="";
					
					lStrSchemeCode = (lObjTrnPensionBillHdr.getSchemeCode() != null) ? lObjTrnPensionBillHdr.getSchemeCode() : "";
					
					lSbHeader.append("Form M.T.R.45 A");
					lSbHeader.append("\r\n");
					lSbHeader.append("( See Rule 406 A )");
					lSbHeader.append("\r\n");
					lSbHeader.append("Simple Receipt");
					lSbHeader.append("\r\n");
					
					lStrTreasuryName = "Name of the Treasury:-"+space(25)+"$"+SessionHelper.getLocationName(inputMap)+space(48 - SessionHelper.getLocationName(inputMap).length())+"$";	
					
					lStrTreasuryName= lStrTreasuryName + "Token No."+space(39)+"$"
										+"Date :"+space(43)+"$"
										+"Voucher No. "+space(38);
					String lStrOffAddr = lObjCVPRestorationLetterDAO.getOffiCeAddr(gStrLocCode);
					
					lStrNameOfOffice = "Name of the Office:- "+space(1)+lStrOffAddr;
					
					ColumnVo[] columnHeading = new ColumnVo[2];
					columnHeading[0] =  new ColumnVo(lStrTreasuryName,1,50,0,true,false,false);
					columnHeading[1] =  new ColumnVo(lStrNameOfOffice,1,27,0,true,false,false);
					
					lSbFooter.append("Demand No         : G6");
					lSbFooter.append("\r\n");
					lSbFooter.append("Major Head        : 2071 Pension & Other Retirement Benefits.");
					lSbFooter.append("\r\n");
					lSbFooter.append("Minor Head        : 101 Superannuation and Retirement Allowances.");
					lSbFooter.append("\r\n");
					lSbFooter.append("Sub   Head        : 102 Commuted Value of Pension.");
					lSbFooter.append("\r\n");
					lSbFooter.append("Detailed Head     : 04");
					lSbFooter.append("\r\n");
					lSbFooter.append("Bill Type         :");
					lSbFooter.append("\r\n");
					lSbFooter.append("Scheme Code       : "+lStrSchemeCode);
					lSbFooter.append("\r\n");
					lSbFooter.append("( Object Expenditure )");
					lSbFooter.append("\r\n");
					lSbFooter.append(getChar(80, "-"));
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append(getChar(80, "-"));
					lSbFooter.append("\r\n");
					String lStrReducedAmt = "Received Sum of(Rs. "+lObjTrnPensionBillDtls.getNetAmount().longValue()+" /-) Rs. "+EnglishDecimalFormat.convertWithSpace(new BigDecimal(lObjTrnPensionBillDtls.getNetAmount().doubleValue())) +" Only";
					lSbFooter.append(lStrReducedAmt);
					lSbFooter.append("\r\n");
					lSbFooter.append(getChar(80, "-"));
					lSbFooter.append("\r\n");
					
					lSbCvpBill.append("<div><pre>");
					lStrHeader = objExport.getReportFileForCvpBill(arrOuter, columnHeading,lSbHeader.toString(),
							lSbFooter.toString(),null,79 ,true,1);
					lSbCvpBill.append(lStrHeader);
					
					
					arrOuter = new ArrayList<List>();
					lineList = new ArrayList();
					columnHeading = new ColumnVo[2];
					columnHeading[0] =  new ColumnVo("",1,50,0,true,false,false);
					columnHeading[1] =  new ColumnVo("",1,27,0,true,false,false);
					
					lStrHeader="";
					
					lineList = new ArrayList();
					lineList.add(getChar(51,"-"));
					lineList.add(getChar(27,"-"));
					arrOuter.add(lineList);
					
					lineList = new ArrayList();
					lineList.add("Name/Amount/Bank/Branch/AC.No : "+lStrPensionerName);
					lineList.add("Non Plan");
					arrOuter.add(lineList);
					
					lineList = new ArrayList();
					lineList.add("Authorised by A.G.Mah.Mumbai.	");
					lineList.add("B Commuted Value of Pension");
					arrOuter.add(lineList);
					
					lineList = new ArrayList();
					lineList.add("Authority No. " + lObjTrnPensionSupplyBillDtls.getCvpOrderNo() + "  " + lObjSimpleDateFormat.format(lObjTrnPensionSupplyBillDtls.getCvpOrderDate()));
					lineList.add("B 1-11-1956");
					arrOuter.add(lineList);
					
					lineList = new ArrayList();
					lineList.add(" ");
					lineList.add("A 1-11-1956");
					arrOuter.add(lineList);
					
					lineList = new ArrayList();
					lineList.add("Date : "+DateUtilities.stringFromDate(lStrBillDate));
					lineList.add("A 1-11-1956");
					arrOuter.add(lineList);
					
					lineList = new ArrayList();
					lineList.add("P.P.O. No.  "+lObjTrnPensionBillDtls.getPpoNo());
					lineList.add("");
					arrOuter.add(lineList);
					
					lineList = new ArrayList();
					lineList.add(getChar(51,"-"));
					lineList.add(getChar(27,"-"));
					arrOuter.add(lineList);
					
					lSbFooter = new StringBuilder();
					
					lSbFooter.append("Copy - enclosed                      Received Payment");
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append(String.format("%80s", "Signature & Designation"));
					lSbFooter.append("\r\n");
					lSbFooter.append(String.format("%80s", "A.P.A.O/ATO"));
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append(String.format("%50s", "For use in Treasury"));
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append(getChar(80, "-"));
					lSbFooter.append("\r\n");
					lSbFooter.append("Pay Rs.(  "+lObjTrnPensionBillDtls.getNetAmount().longValue()+" /-) Rupees : "+EnglishDecimalFormat.convertWithSpace(new BigDecimal(lObjTrnPensionBillDtls.getNetAmount().doubleValue())) + " Only");
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append(String.format("%70s","Asstt.Pay and Accounts Officer/Asstt. Treasury Officer."));
					lStrHeader = objExport.getReportFileForCvpBill(arrOuter, columnHeading,"",
							lSbFooter.toString(),null,79 ,true,26);
					lSbCvpBill.append(lStrHeader);
					
					lSbCvpBill.append("</pre></div>");
					inputMap.put("PrintBillString", lSbCvpBill);
					//Print CVP bill data end
				}
			}
			// Getting Recovery Dtls for the pensioner
			lLstRecoveryType = IFMSCommonServiceImpl.getLookupValues("RECVTYPE", gLangID, inputMap);
			
			lLstTrnPensionRecoveryDtls = lObjPensionBillDAO.getTrnPensionRecoveryDtlsForSupp(lStrPnsrCode, lStrRcvryFlag, lStrBillNo);
			// lStrSuppBillType =
			// lObjPensionBillDAO.getBillTypeForSupp(lStrPnsrCode, lStrBillNo,
			// gStrLocCode);
			
			inputMap.put("TrnPensionSupplyBillDtls", lObjTrnPensionSupplyBillDtls);
			inputMap.put("PnsnDtls", lLstSupplementaryPartyDtlsVO);
			inputMap.put("PPONo", lObjTrnPensionBillDtls.getPpoNo());
			inputMap.put("PensionerName", lObjTrnPensionBillDtls.getPensionerName());
			inputMap.put("SuppAmnt", lSuppAmt);
			//inputMap.put("RecoveryAmount", lRecAmt);
			//inputMap.put("NetAmount", lReducedAmt);
			inputMap.put("BillDate", lStrBillDate);
			inputMap.put("MapDCRGData", lMapDCRGData);
			inputMap.put("PensionerCode", lStrPnsrCode);
			inputMap.put("BillTypeId", "45");
			inputMap.put("currRole", lStrCurrRoleId);
			inputMap.put("BankList", lLstBanks);
			inputMap.put("RecoveryDtls", lLstTrnPensionRecoveryDtls);
			inputMap.put("RecoveryType", lLstRecoveryType);
			inputMap.put("SuppBillType", lObjTrnPensionSupplyBillDtls.getBillType());
			inputMap.put("PayMode", lStrPaymentMode);
			inputMap.put("arrearDtls", lStrArrearDtls);
			
			if("PENSION".equals(lStrBillType))
			{
				inputMap.put("TrnBillRegister", lObjTrnBillRegister);
				inputMap.put("RequestNo", lStrRequestNo);
				inputMap.put("billDate", lObjSimpleDateFormat.format(lObjTrnBillRegister.getBillDate()));
				inputMap.put("SchemeWiseRecoveryDtls", lLstSchemeWiseRecoveryDtls);
			}
			inputMap.put("BillType", lStrBillType);
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

	private void setSessionInfo(Map inputMap) {

		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocCode = SessionHelper.getLocationCode(inputMap);
		gDtCurrDt = DBUtility.getCurrentDateFromDB();
		gLangID = SessionHelper.getLangId(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gStrLocShortName = SessionHelper.getLocationVO(inputMap).getLocShortName();
	}
	
	public String space(int noOfSpace) {

		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}
	
	private String getChar(int count, String ele) {

		StringBuffer lSBSpace = new StringBuffer();
		for (int i = 0; i < count-1; i++) {
			lSBSpace.append(ele);
		}
		return lSBSpace.toString();
	}
	
	public ResultObject validatePnsnrBeforeSixPayArrerCalc(Map inputMap) {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrPensionerCode = "";
		String lStrSupplyBillId = "";
		StringBuilder lStrBldXML = new StringBuilder();
		Character lCharRvsnCntr = null;
		BigDecimal lBgDcmlPaidCashAmt = BigDecimal.ZERO;
		String lStrCashAmt = "0";
		Map<Integer, BigDecimal> lMapPaidInstallmentAmt = new HashMap<Integer, BigDecimal>();
		Map<Integer, TrnPensionSixpayfpArrear> lMapInstNoWiseSixPayDtls = new HashMap<Integer, TrnPensionSixpayfpArrear>();
		List<TrnPensionSixpayfpArrear> lLstPrevSixPayFpArreasDtls = new ArrayList<TrnPensionSixpayfpArrear>();
		try {
			setSessionInfo(inputMap);
			lStrPensionerCode = StringUtility.getParameter("pensionerCode", request);
			lStrSupplyBillId = StringUtility.getParameter("supplyBillId", request);
			Integer lIntRevisionCnt = 0;
			
			SupplementaryBillDAO lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnPensionSixpayfpArrear.class, serv.getSessionFactory());
			SixPayFPArrearDAO lObjSixPayFPArrearDAO = new SixPayFPArrearDAOImpl(TrnPensionSixpayfpArrear.class, serv.getSessionFactory());
			//Sixth pay arrear is cofigure in the request
			if(!"".equals(lStrPensionerCode) && !"".equals(lStrSupplyBillId))
			{
				lIntRevisionCnt = lObjSupplementaryBillDAO.getPnsnrSixPayArrearRevisionCnt(lStrPensionerCode,Long.parseLong(lStrSupplyBillId));
				if(lIntRevisionCnt != 0)
				{
//					1 set flag and send it in reponse
//					2 delete sixpay detail confing in this req
//						set previous revision activeflag- y , on save of arrear statement
				}
			}
			Integer lIntNoOfInstltPaid = 0;
			Integer lIntMaxPaidInstCnt = 0;
			BigDecimal[] lBgDcmlInstlAmts = new BigDecimal[5];
			Integer lIntRvsnCntr = 0;
			if(!"".equals(lStrPensionerCode))
			{
				lCharRvsnCntr = lObjSixPayFPArrearDAO.getMaxRvsnCntr(lStrPensionerCode);
				if(lCharRvsnCntr != null && lIntRevisionCnt != 0){
					lIntRvsnCntr = Integer.valueOf(String.valueOf(lCharRvsnCntr));
					lIntRvsnCntr = lIntRvsnCntr - 1;
					lCharRvsnCntr = Character.valueOf(StringUtility.convertToChar(String.valueOf(lIntRvsnCntr)));
				}
				if(lCharRvsnCntr != null && lCharRvsnCntr != '0') {
						lLstPrevSixPayFpArreasDtls = lObjSixPayFPArrearDAO.getTrnPensionSixpayfpArrearVOs(lStrPensionerCode, lCharRvsnCntr);
						lStrCashAmt = lObjSixPayFPArrearDAO.getSixpayfpArrearTotalPaidCashAmt(lStrPensionerCode);
						if (lLstPrevSixPayFpArreasDtls != null) {
							for (TrnPensionSixpayfpArrear lObjTrnPensionSixpayfpArrear : lLstPrevSixPayFpArreasDtls) {
								lMapInstNoWiseSixPayDtls.put(lObjTrnPensionSixpayfpArrear.getInstallmentNo(), lObjTrnPensionSixpayfpArrear);
								if ((lObjTrnPensionSixpayfpArrear.getPayInMonth() != null) && ('Y' == lObjTrnPensionSixpayfpArrear.getPaidFlag())) {
									lIntNoOfInstltPaid = lIntNoOfInstltPaid +1;
									lMapPaidInstallmentAmt.put(lObjTrnPensionSixpayfpArrear.getInstallmentNo(), lObjTrnPensionSixpayfpArrear.getInstallmentAmnt());
									if(lIntMaxPaidInstCnt.compareTo(lObjTrnPensionSixpayfpArrear.getInstallmentNo()) == -1) {
										lIntMaxPaidInstCnt = lObjTrnPensionSixpayfpArrear.getInstallmentNo();
									}
								}
							}
						}
				}
				
			}
			Integer lIntInstltNo = 1;
			for(int lIntCnt=0;lIntCnt<lIntMaxPaidInstCnt;lIntCnt++)
			{
				lBgDcmlInstlAmts[lIntCnt] = lMapInstNoWiseSixPayDtls.get(lIntInstltNo).getInstallmentAmnt();
				++lIntInstltNo;
			}
			for(int lIntCnt=lIntMaxPaidInstCnt;lIntCnt<5;lIntCnt++)
			{
				lBgDcmlInstlAmts[lIntCnt] = BigDecimal.ZERO;
				
			}
			
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<ISEXISTS>");
			if (lIntRevisionCnt == 0) {
				lStrBldXML.append("N");
			} else {
				lStrBldXML.append("Y");
			}
			lStrBldXML.append("</ISEXISTS>");
			lStrBldXML.append("<ISREVISION>");
			if (!lLstPrevSixPayFpArreasDtls.isEmpty()) {
				lStrBldXML.append("Y");
			} else {
				lStrBldXML.append("N");
			}
			lStrBldXML.append("</ISREVISION>");
			if (!lLstPrevSixPayFpArreasDtls.isEmpty()) {
				lStrBldXML.append("<NOOFINSTLPAID>"+lIntMaxPaidInstCnt);
				lStrBldXML.append("</NOOFINSTLPAID>");
				lStrBldXML.append("<INSTLT1AMT>"+lBgDcmlInstlAmts[0]);
				lStrBldXML.append("</INSTLT1AMT>");
				lStrBldXML.append("<INSTLT2AMT>"+lBgDcmlInstlAmts[1]);
				lStrBldXML.append("</INSTLT2AMT>");
				lStrBldXML.append("<INSTLT3AMT>"+lBgDcmlInstlAmts[2]);
				lStrBldXML.append("</INSTLT3AMT>");
				lStrBldXML.append("<INSTLT4AMT>"+lBgDcmlInstlAmts[3]);
				lStrBldXML.append("</INSTLT4AMT>");
				lStrBldXML.append("<INSTLT5AMT>"+lBgDcmlInstlAmts[4]);
				lStrBldXML.append("</INSTLT5AMT>");
				lStrBldXML.append("<PAIDCASHAMT>");
				if(lStrCashAmt != null && "".equals(lStrCashAmt))
					lStrBldXML.append("0");
				else
					lStrBldXML.append(lStrCashAmt);
				lStrBldXML.append("</PAIDCASHAMT>");
				lStrBldXML.append("<REVISIONCNTR>");
				if(lCharRvsnCntr == null)
				{
					lStrBldXML.append("0");	
				}else {
					lStrBldXML.append(lCharRvsnCntr);
				}
				lStrBldXML.append("</REVISIONCNTR>");
			}
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
	
	public ResultObject validatePpoWithExistRequest(Map inputMap) {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrRequestNo = "";
		StringBuilder lStrBldXML = new StringBuilder();
		List<Short> lLstStatus = new ArrayList<Short>();
		try {
			setSessionInfo(inputMap);
			String lStrPpoNo = StringUtility.getParameter("ppoNo", request);
					
			SupplementaryBillDAO lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnPensionSupplyBillDtls.class, serv.getSessionFactory());
			lLstStatus.add(DBConstants.SUPPL_REQ_GENERATED);
			lLstStatus.add(DBConstants.SUPPL_BILL_UPDATED);
			lLstStatus.add(DBConstants.ST_BILL_CREATED);
			lLstStatus.add(DBConstants.ST_BILL_FORW_TO_ATO);
			lLstStatus.add(DBConstants.ST_BILL_REJECTED);
			lLstStatus.add(DBConstants.ST_BILL_DISCARD);
			lLstStatus.add(DBConstants.ST_BILL_ARCHEIVED);
					
			lStrRequestNo = lObjSupplementaryBillDAO.getRequestNoFromPpoNo(lStrPpoNo, lLstStatus, gStrLocCode);
			
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
