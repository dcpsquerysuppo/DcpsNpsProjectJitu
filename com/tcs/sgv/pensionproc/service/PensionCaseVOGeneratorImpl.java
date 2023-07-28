/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 1, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.service;

import java.math.BigDecimal;
import java.math.BigInteger;
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
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAOImpl;
import com.tcs.sgv.pensionproc.dao.TrnPnsnprocFamilydtlsDAO;
import com.tcs.sgv.pensionproc.dao.TrnPnsnprocFamilydtlsDAOImpl;
import com.tcs.sgv.pensionproc.dao.TrnPnsnprocNomineedtlsDAO;
import com.tcs.sgv.pensionproc.dao.TrnPnsnprocNomineedtlsDAOImpl;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcAdvnceBal;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcAssesdDues;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcCheckList;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcPnsnCalc;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcPnsnrDtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcRecovery;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcRevision;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocAgDtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocAuthorityDtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocAvgPayCalc;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocEventdtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocFamilydtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocForeignServ;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocNomineedtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocPnsnrpay;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocPnsnrservcbreak;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocProvisionalPaid;

/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 Feb 1, 2011
 */
public class PensionCaseVOGeneratorImpl extends ServiceImpl implements PensionCaseVOGenerator {

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

	String gStrAuditorFlag = null;

	String gStrStatus = null;

	Long gLngAuditPostId = null;

	Long gLngAuditUserId = null;

	Log gLogger = LogFactory.getLog(getClass());

	Date gDateDBDate = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionproc/PensionCaseConstants");

	// Sets session information in the global variables
	private void setSessionInfo(Map inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngEmpId = SessionHelper.getEmpId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocId = SessionHelper.getLocationCode(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gDateDBDate = DBUtility.getCurrentDateFromDB();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionproc.service.PensionCaseVOGenerator#generateMap(java
	 * .util.Map)
	 */

	public ResultObject generateMap(Map inputMap) {
		gLogger.info("In generateMap of PensionRequestVOGenerator........");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		try {
			List<TrnPnsnprocEventdtls> lLstTrnPnsnprocEventdtlsVO = new ArrayList<TrnPnsnprocEventdtls>();
			List<TrnPnsnprocPnsnrservcbreak> lLstTrnPnsnprocPnsnrServcBreakVO = new ArrayList<TrnPnsnprocPnsnrservcbreak>();
			List<TrnPnsnprocAvgPayCalc> lLstTrnPnsnprocAvgPayCalcVO = new ArrayList<TrnPnsnprocAvgPayCalc>();
			List<TrnPnsnProcAssesdDues> lLstTrnPnsnProcAssesdDuesVO = new ArrayList<TrnPnsnProcAssesdDues>();
			List<TrnPnsnProcAdvnceBal> lLstTrnPnsnProcAdvnceBalVO = new ArrayList<TrnPnsnProcAdvnceBal>();
			List<TrnPnsnprocFamilydtls> lLstTrnPnsnprocFamilydtlsVO = new ArrayList<TrnPnsnprocFamilydtls>();
			List<TrnPnsnprocNomineedtls> lLstTrnPnsnprocNomineedtlsVO = new ArrayList<TrnPnsnprocNomineedtls>();
			List<TrnPnsnProcCheckList> lLstTrnPnsnProcCheckListVO = new ArrayList<TrnPnsnProcCheckList>();
			List<TrnPnsnprocProvisionalPaid> lLstTrnPnsnprocProvisionalPaidVO = new ArrayList<TrnPnsnprocProvisionalPaid>();
			List<TrnPnsnprocForeignServ> lLstTrnPnsnprocForeignServ = new ArrayList<TrnPnsnprocForeignServ>();
			List<TrnPnsnprocAuthorityDtls> lLstTrnPnsnprocAuthorityDtls = new ArrayList<TrnPnsnprocAuthorityDtls>();
			TrnPnsnProcInwardPension lObjTrnPnsnProcInwardPensionVO = new TrnPnsnProcInwardPension();
			TrnPnsnProcPnsnrDtls lObjTrnPnsnProcPnsnrDtlsVO = new TrnPnsnProcPnsnrDtls();
			TrnPnsnprocPnsnrpay lObjTrnPnsnprocPnsnrPayVO = new TrnPnsnprocPnsnrpay();
			TrnPnsnprocEventdtls lObjTrnPnsnprocEventdtlsVO = new TrnPnsnprocEventdtls();
			TrnPnsnprocPnsnrservcbreak lObjTrnPnsnprocPnsnrServcBreakVO = new TrnPnsnprocPnsnrservcbreak();
			TrnPnsnprocAvgPayCalc lObjTrnPnsnprocAvgPayCalcVO = null;
			TrnPnsnProcPnsnCalc lObjTrnPnsnProcPnsnCalcVO = new TrnPnsnProcPnsnCalc();
			TrnPnsnprocFamilydtls lObjTrnPnsnprocFamilydtlsVO = new TrnPnsnprocFamilydtls();
			TrnPnsnprocNomineedtls lObjTrnPnsnprocNomineedtlsVO = new TrnPnsnprocNomineedtls();
			TrnPnsnProcRecovery lObjTrnPnsnProcRecoveryVO = new TrnPnsnProcRecovery();
			TrnPnsnProcAdvnceBal lObjTrnPnsnProcAdvnceBalVO = new TrnPnsnProcAdvnceBal();
			TrnPnsnProcAssesdDues lObjTrnPnsnProcAssesdDuesVO = new TrnPnsnProcAssesdDues();
			TrnPnsnProcCheckList lObjTrnPnsnProcCheckListVO = new TrnPnsnProcCheckList();
			TrnPnsnprocProvisionalPaid lObjPnsnprocProvisionalPaidVO = new TrnPnsnprocProvisionalPaid();
			TrnPnsnprocForeignServ lObjPnsnprocForeignServVO = new TrnPnsnprocForeignServ();
			TrnPnsnProcRevision lObjTrnPnsnProcRevisionVO = new TrnPnsnProcRevision();
			TrnPnsnprocAuthorityDtls lObjAuthorityDtlsVO = new TrnPnsnprocAuthorityDtls();
			TrnPnsnprocAgDtls lObjPnsnprocAgDtlsVO = new TrnPnsnprocAgDtls();
			
			TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class, serv.getSessionFactory());
			Long lLngPensionInwardId = null;

			if (!StringUtility.getParameter("InwardId", request).equalsIgnoreCase("")) {
				lLngPensionInwardId = Long.parseLong(StringUtility.getParameter("InwardId", request));
				inputMap.put("Mode", "Update");
				lObjTrnPnsnProcInwardPensionVO = (TrnPnsnProcInwardPension) lObjTrnPnsnProcInwardPensionDAO.read(lLngPensionInwardId);
				inputMap.put("lObjTrnPnsnProcInwardPensionVO", lObjTrnPnsnProcInwardPensionVO);

				//read pensioner details vo
				lObjTrnPnsnProcPnsnrDtlsVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnrDtlsVO(lLngPensionInwardId);
				inputMap.put("lObjTrnPnsnProcPnsnrDtlsVO", lObjTrnPnsnProcPnsnrDtlsVO);

				//read pensioner pay vo
				lObjTrnPnsnprocPnsnrPayVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnrPayVO(lLngPensionInwardId);
				inputMap.put("lObjTrnPnsnprocPnsnrpayVO", lObjTrnPnsnprocPnsnrPayVO);

				//read Provisional Paid vo 
				lLstTrnPnsnprocProvisionalPaidVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnprocProvisionalPaidDtls(lLngPensionInwardId);
				if (lLstTrnPnsnprocProvisionalPaidVO != null && !lLstTrnPnsnprocProvisionalPaidVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnprocProvisionalPaidVO.size(); lIntCnt++) {

						lObjPnsnprocProvisionalPaidVO = lLstTrnPnsnprocProvisionalPaidVO.get(lIntCnt);
						lObjTrnPnsnProcInwardPensionDAO.delete(lObjPnsnprocProvisionalPaidVO);
					}
				}
				
				//read Foreign Serv vo 
				lLstTrnPnsnprocForeignServ = lObjTrnPnsnProcInwardPensionDAO.getForeignServDtls(lLngPensionInwardId);
				if (lLstTrnPnsnprocForeignServ != null && !lLstTrnPnsnprocForeignServ.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnprocForeignServ.size(); lIntCnt++) {

						lObjPnsnprocForeignServVO= lLstTrnPnsnprocForeignServ.get(lIntCnt);
						lObjTrnPnsnProcInwardPensionDAO.delete(lObjPnsnprocForeignServVO);
					}
				}
				lLstTrnPnsnprocAuthorityDtls = lObjTrnPnsnProcInwardPensionDAO.getAuthorityDtls(lLngPensionInwardId);
				if (lLstTrnPnsnprocAuthorityDtls != null && !lLstTrnPnsnprocAuthorityDtls.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnprocAuthorityDtls.size(); lIntCnt++) {

						lObjAuthorityDtlsVO= lLstTrnPnsnprocAuthorityDtls.get(lIntCnt);
						lObjTrnPnsnProcInwardPensionDAO.delete(lObjAuthorityDtlsVO);
					}
				}
	
				
				//read Event details vo 
				lLstTrnPnsnprocEventdtlsVO = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseEventDtls(lLngPensionInwardId);
				if (lLstTrnPnsnprocEventdtlsVO != null && !lLstTrnPnsnprocEventdtlsVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnprocEventdtlsVO.size(); lIntCnt++) {

						lObjTrnPnsnprocEventdtlsVO = lLstTrnPnsnprocEventdtlsVO.get(lIntCnt);
						lObjTrnPnsnProcInwardPensionDAO.delete(lObjTrnPnsnprocEventdtlsVO);
					}
				}

				//read service break vo 
				lLstTrnPnsnprocPnsnrServcBreakVO = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseSrvcBrkDtls(lLngPensionInwardId);
				if (lLstTrnPnsnprocPnsnrServcBreakVO != null && !lLstTrnPnsnprocPnsnrServcBreakVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnprocPnsnrServcBreakVO.size(); lIntCnt++) {

						lObjTrnPnsnprocPnsnrServcBreakVO = lLstTrnPnsnprocPnsnrServcBreakVO.get(lIntCnt);
						lObjTrnPnsnProcInwardPensionDAO.delete(lObjTrnPnsnprocPnsnrServcBreakVO);
					}
				}

				//read average pay calculator vo
				lLstTrnPnsnprocAvgPayCalcVO = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseAvgPayCalcDtls(lLngPensionInwardId);
				if (lLstTrnPnsnprocAvgPayCalcVO != null && !lLstTrnPnsnprocAvgPayCalcVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnprocAvgPayCalcVO.size(); lIntCnt++) {

						lObjTrnPnsnprocAvgPayCalcVO = lLstTrnPnsnprocAvgPayCalcVO.get(lIntCnt);
						lObjTrnPnsnProcInwardPensionDAO.delete(lObjTrnPnsnprocAvgPayCalcVO);
					}
				}

				//read pension calculation vo
				lObjTrnPnsnProcPnsnCalcVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnCalcVO(lLngPensionInwardId);
				inputMap.put("lObjTrnPnsnProcPnsnCalcVO", lObjTrnPnsnProcPnsnCalcVO);
				
				lObjPnsnprocAgDtlsVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnAgDtls(lLngPensionInwardId);
				inputMap.put("lObjPnsnprocAgDtlsVO", lObjPnsnprocAgDtlsVO);
				
				//read pension Revision vo
				Long lLngRevisionId = lObjTrnPnsnProcInwardPensionDAO.getPnsnRevisionId(lObjTrnPnsnProcInwardPensionVO.getSevaarthId());
				inputMap.put("lLngRevisionId",lLngRevisionId);
				lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcRevision.class, serv.getSessionFactory());
				lObjTrnPnsnProcRevisionVO = (TrnPnsnProcRevision) lObjTrnPnsnProcInwardPensionDAO.read(lLngRevisionId);
				if(lObjTrnPnsnProcRevisionVO != null)
					inputMap.put("lObjTrnPnsnProcRevisionVO", lObjTrnPnsnProcRevisionVO);

				//read pensioner family detail vo
				TrnPnsnprocFamilydtlsDAO lObjTrnPnsnprocFamilydtlsDAO = new TrnPnsnprocFamilydtlsDAOImpl(TrnPnsnprocFamilydtls.class, serv.getSessionFactory());
				lLstTrnPnsnprocFamilydtlsVO = lObjTrnPnsnprocFamilydtlsDAO.getListOfPnsnrFamilyDtls(lLngPensionInwardId);
				if (lLstTrnPnsnprocFamilydtlsVO != null && !lLstTrnPnsnprocFamilydtlsVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnprocFamilydtlsVO.size(); lIntCnt++) {

						lObjTrnPnsnprocFamilydtlsVO = lLstTrnPnsnprocFamilydtlsVO.get(lIntCnt);
						lObjTrnPnsnprocFamilydtlsDAO.delete(lObjTrnPnsnprocFamilydtlsVO);
					}
				}

				//read pensioner nominee detail vo
				TrnPnsnprocNomineedtlsDAO lObjTrnPnsnprocNomineedtlsDAO = new TrnPnsnprocNomineedtlsDAOImpl(TrnPnsnprocNomineedtls.class, serv.getSessionFactory());
				lLstTrnPnsnprocNomineedtlsVO = lObjTrnPnsnprocNomineedtlsDAO.getPnsnrNomineeDtls(lLngPensionInwardId);
				if (lLstTrnPnsnprocNomineedtlsVO != null && !lLstTrnPnsnprocNomineedtlsVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnprocNomineedtlsVO.size(); lIntCnt++) {

						lObjTrnPnsnprocNomineedtlsVO = lLstTrnPnsnprocNomineedtlsVO.get(lIntCnt);
						lObjTrnPnsnprocNomineedtlsDAO.delete(lObjTrnPnsnprocNomineedtlsVO);
					}
				}

				//read pension recovery vo
				lObjTrnPnsnProcRecoveryVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnrRecoveryVO(lLngPensionInwardId);
				inputMap.put("lObjTrnPnsnProcRecoveryVO", lObjTrnPnsnProcRecoveryVO);

				//read advance balance vo 
				lLstTrnPnsnProcAdvnceBalVO = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseAdvnceBalDtls(lLngPensionInwardId);
				if (lLstTrnPnsnProcAdvnceBalVO != null && !lLstTrnPnsnProcAdvnceBalVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnProcAdvnceBalVO.size(); lIntCnt++) {

						lObjTrnPnsnProcAdvnceBalVO = lLstTrnPnsnProcAdvnceBalVO.get(lIntCnt);
						lObjTrnPnsnProcInwardPensionDAO.delete(lObjTrnPnsnProcAdvnceBalVO);
					}
				}

				//read assessed dues vo 
				lLstTrnPnsnProcAssesdDuesVO = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseAssesdDueDtls(lLngPensionInwardId);
				if (lLstTrnPnsnProcAssesdDuesVO != null && !lLstTrnPnsnProcAssesdDuesVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnProcAssesdDuesVO.size(); lIntCnt++) {

						lObjTrnPnsnProcAssesdDuesVO = lLstTrnPnsnProcAssesdDuesVO.get(lIntCnt);
						lObjTrnPnsnProcInwardPensionDAO.delete(lObjTrnPnsnProcAssesdDuesVO);
					}
				}

				//read checklist vo
				lLstTrnPnsnProcCheckListVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnrCheklistVO(lLngPensionInwardId);
				if (lLstTrnPnsnProcCheckListVO != null && !lLstTrnPnsnProcCheckListVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnProcCheckListVO.size(); lIntCnt++) {

						lObjTrnPnsnProcCheckListVO = lLstTrnPnsnProcCheckListVO.get(lIntCnt);
						lObjTrnPnsnProcInwardPensionDAO.delete(lObjTrnPnsnProcCheckListVO);
					}
				}

			} else {
				inputMap.put("Mode", "Add");
			}

			lObjTrnPnsnProcInwardPensionVO = generateTrnPnsnProcInwardPensionVO(inputMap);
			lObjTrnPnsnProcPnsnrDtlsVO = generateTrnPnsnProcPnsnrDtlsVO(inputMap);
			lObjTrnPnsnprocPnsnrPayVO = generateTrnPnsnprocPnsnrpayVO(inputMap);
			lLstTrnPnsnprocEventdtlsVO = generateTrnPnsnprocEventdtlsVO(inputMap);
			lLstTrnPnsnprocPnsnrServcBreakVO = generateTrnPnsnprocPnsnrservcbreakVO(inputMap);
			lLstTrnPnsnprocAvgPayCalcVO = generateTrnPnsnprocAvgPayCalcVO(inputMap);
			lObjTrnPnsnProcPnsnCalcVO = generateTrnPnsnProcPnsnCalcVO(inputMap);
			lObjTrnPnsnProcRecoveryVO = generateTrnPnsnProcRecoveryVO(inputMap);
			lLstTrnPnsnProcAdvnceBalVO = generateTrnPnsnProcAdvnceBalVO(inputMap);
			lLstTrnPnsnProcAssesdDuesVO = generateTrnPnsnProcAssesdDuesVO(inputMap);
			lLstTrnPnsnprocFamilydtlsVO = generateTrnPnsnProcPnsnFamilydtlsVO(inputMap);
			lLstTrnPnsnprocNomineedtlsVO = generateTrnPnsnProcPnsnNomineedtlsVO(inputMap);
			lLstTrnPnsnProcCheckListVO = generateTrnPnsnProcCheckListVO(inputMap);
			lLstTrnPnsnprocProvisionalPaidVO = generateTrnPnsnprocProvisionalPaid(inputMap);
			lLstTrnPnsnprocForeignServ = generateTrnPnsnprocForeignServ(inputMap);
			lObjTrnPnsnProcRevisionVO = generateTrnPnsnprocRevision(inputMap);
			lLstTrnPnsnprocAuthorityDtls = generateTrnPnsnprocAuthoDtls(inputMap);
			lObjPnsnprocAgDtlsVO  = generateTrnPnsnprocAgDtls(inputMap);
			
			inputMap.put("lObjTrnPnsnProcInwardPensionVO", lObjTrnPnsnProcInwardPensionVO);
			inputMap.put("lObjTrnPnsnProcPnsnrDtlsVO", lObjTrnPnsnProcPnsnrDtlsVO);
			inputMap.put("lObjTrnPnsnprocPnsnrpayVO", lObjTrnPnsnprocPnsnrPayVO);
			inputMap.put("lLstTrnPnsnprocEventdtlsVO", lLstTrnPnsnprocEventdtlsVO);
			inputMap.put("lLstTrnPnsnprocPnsnrServcBreakVO", lLstTrnPnsnprocPnsnrServcBreakVO);
			inputMap.put("lObjTrnPnsnProcPnsnCalcVO", lObjTrnPnsnProcPnsnCalcVO);
			inputMap.put("lObjTrnPnsnProcRecoveryVO", lObjTrnPnsnProcRecoveryVO);
			inputMap.put("lLstTrnPnsnProcAdvnceBalVO", lLstTrnPnsnProcAdvnceBalVO);
			inputMap.put("lLstTrnPnsnProcAssesdDuesVO", lLstTrnPnsnProcAssesdDuesVO);
			inputMap.put("lLstTrnPnsnprocFamilydtlsVO", lLstTrnPnsnprocFamilydtlsVO);
			inputMap.put("lLstTrnPnsnprocNomineedtlsVO", lLstTrnPnsnprocNomineedtlsVO);
			inputMap.put("lLstTrnPnsnProcCheckListVO", lLstTrnPnsnProcCheckListVO);
			inputMap.put("lLstTrnPnsnprocAvgPayCalcVO", lLstTrnPnsnprocAvgPayCalcVO);
			inputMap.put("lLstTrnPnsnprocProvisionalPaidVO", lLstTrnPnsnprocProvisionalPaidVO);
			inputMap.put("lLstTrnPnsnprocForeignServ", lLstTrnPnsnprocForeignServ);
			inputMap.put("lObjTrnPnsnProcRevisionVO", lObjTrnPnsnProcRevisionVO);
			inputMap.put("lLstTrnPnsnprocAuthorityDtls", lLstTrnPnsnprocAuthorityDtls);
			inputMap.put("lObjPnsnprocAgDtlsVO", lObjPnsnprocAgDtlsVO);
			
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);
		} catch (Exception e) {		
			gLogger.error("Error is :" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);

		}
		return objRes;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionproc.service.PensionCaseVOGenerator#
	 * generateMstPnsnProcInwardPensionVO(java.util.Map)
	 */

	public TrnPnsnProcInwardPension generateTrnPnsnProcInwardPensionVO(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPnsnProcInwardPension lObjTrnPnsnProcInwardPensionVO = null;
		if (inputMap.containsKey("lObjTrnPnsnProcInwardPensionVO")) {
			lObjTrnPnsnProcInwardPensionVO = (TrnPnsnProcInwardPension) inputMap.get("lObjTrnPnsnProcInwardPensionVO");
		} else {
			lObjTrnPnsnProcInwardPensionVO = new TrnPnsnProcInwardPension();
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		gLogger.info("In generateMstPnsnProcInwardPensionVO of PensionCaseVOGeneratorImpl........");

		try {
			Date lDtInwardDate = null;
			String lStrCaseType = "";
			String lStrSevaarthId = "";
			Long lLngDdoCode = 0L;
			Date lDtPpoDate = null;
			String lStrPpoNo = "";
			String lStrPpoDate = "";			
			Long lLngDocumentCount = 0L;
			Long lLngRevisionNo = 0L;
			String lStrPayCommission = "";
			String lStrCommuateFlag = "";
			String lStrPensionType = "";
			String lStrPensionerType = "";
			String lStrCaseStatus = "";
			Date lDtcurDate = SessionHelper.getCurDate();
			String lStrDraftFlag = "";
			String lStrMode = "";
			String lStrComments = "";
			Date lDtCommencementDate = null;
			String lStrCommencementDt = "";
			String lStrOtherPnsnrType = "";
			String lStrAGOfficePension = "";
			String lStrAGOfficeAftrFirstPay = "";
			Long lLngTrsryAftrFirstPay = 0L;
			Long lLngTrsryIdPension = 0L;
			String lStrOutwardNo = "";
			Date lDtOutwardDate = null;
			setSessionInfo(inputMap);

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			
			if (!StringUtility.getParameter("cmbCaseType", request).equals("-1") && StringUtility.getParameter("cmbCaseType", request).length() > 0) {
				lStrCaseType = StringUtility.getParameter("cmbCaseType", request).trim();
			}

			lStrSevaarthId = ((StringUtility.getParameter("txtSevaarthId", request).trim()).length() > 0) ? StringUtility.getParameter("txtSevaarthId", request).toUpperCase().trim() : null;

			gLogger.info("sevaarthId : " + lStrSevaarthId);

			lStrAGOfficeAftrFirstPay = StringUtility.getParameter("cmbAgOffice", request);
			lStrAGOfficePension = StringUtility.getParameter("cmbAgOfficeForPension", request);

			lStrCommencementDt = StringUtility.getParameter("txtDateOfCommencement", request).trim();
			lStrPpoNo = StringUtility.getParameter("txtPpoNo", request).trim();
			lStrPpoDate = StringUtility.getParameter("txtPpoDate", request).trim();

			if (!StringUtility.getParameter("cmbTreasury", request).equals("-1") && StringUtility.getParameter("cmbTreasury", request).length() > 0) {
				lLngTrsryAftrFirstPay = Long.parseLong(StringUtility.getParameter("cmbTreasury", request).trim());
			}

			if (!StringUtility.getParameter("cmbTreasuryForPension", request).equals("-1") && StringUtility.getParameter("cmbTreasuryForPension", request).length() > 0) {
				lLngTrsryIdPension = Long.parseLong(StringUtility.getParameter("cmbTreasuryForPension", request).trim());
			}

			if (lStrCommencementDt.length() > 0) {
				lDtCommencementDate = simpleDateFormat.parse(lStrCommencementDt);
			}
			if (!"".equals(lStrPpoDate)) {
				lDtPpoDate = simpleDateFormat.parse(lStrPpoDate);
			}

			lStrComments = StringUtility.getParameter("txtComments", request);
			if (lStrComments.length() > 0) {
				lObjTrnPnsnProcInwardPensionVO.setComments(lStrComments);
			}

			if (StringUtility.getParameter("DraftFlag", request).trim() != null) {
				lStrDraftFlag = StringUtility.getParameter("DraftFlag", request).trim();
			}

			lLngDdoCode = ((StringUtility.getParameter("txtDDOCode", request).trim()).length() > 0) ? Long.parseLong(StringUtility.getParameter("txtDDOCode", request).trim())
					: null;
			gLogger.info("ddoCode : " + lLngDdoCode);

			if (!StringUtility.getParameter("cmbPayComsn", request).equals("-1") && StringUtility.getParameter("cmbPayComsn", request).length() > 0) {
				lStrPayCommission = StringUtility.getParameter("cmbPayComsn", request).trim();
			}

			lStrCommuateFlag = ((StringUtility.getParameter("radioDoWantCommute", request).trim()).length() > 0) ? StringUtility.getParameter("radioDoWantCommute", request).trim()
					.toUpperCase() : null;

			lStrPensionType = StringUtility.getParameter("cmbClassOfPnsn", request).trim();

			if (!StringUtility.getParameter("cmbPnsnCatg", request).equals("-1") && StringUtility.getParameter("cmbPnsnCatg", request).length() > 0) {
				lStrPensionerType = StringUtility.getParameter("cmbPnsnCatg", request).trim();
			}
			lStrOtherPnsnrType = StringUtility.getParameter("txtOtherPnsnrType", request);
			
			lStrOutwardNo = StringUtility.getParameter("txtOutwardNo", request);
			lDtOutwardDate = ((StringUtility.getParameter("txtOutwardDate", request).trim()).length() > 0) ? simpleDateFormat.parse(StringUtility.getParameter("txtOutwardDate", request)): null;

			if ("Add".equalsIgnoreCase(lStrMode)) {
				if ("D".equalsIgnoreCase(lStrDraftFlag)) {
					lStrCaseStatus = gObjRsrcBndle.getString("PPROC.DRAFTSTATUSID");
				}
				else if ("F".equalsIgnoreCase(lStrDraftFlag)) {
					lStrCaseStatus = gObjRsrcBndle.getString("PPROC.FWDBYDEOSTATUSID");
				}
				else if ("L".equalsIgnoreCase(lStrDraftFlag)) {
					lStrCaseStatus = gObjRsrcBndle.getString("PPROC.APPROVEDBYAGSTATUSID");
				}
				lDtInwardDate = simpleDateFormat.parse(simpleDateFormat.format(lDtcurDate));
				lObjTrnPnsnProcInwardPensionVO.setInwardDate(lDtInwardDate);
			}
			if ("Update".equalsIgnoreCase(lStrMode)) {
				lStrCaseStatus = StringUtility.getParameter("status", request).trim();
			}

			lObjTrnPnsnProcInwardPensionVO.setLocationCode(Long.parseLong(gStrLocationCode));			
			lObjTrnPnsnProcInwardPensionVO.setCaseType(lStrCaseType);
			lObjTrnPnsnProcInwardPensionVO.setSevaarthId(lStrSevaarthId);
			lObjTrnPnsnProcInwardPensionVO.setDdoCode(lLngDdoCode);
			lObjTrnPnsnProcInwardPensionVO.setPpoNo(lStrPpoNo);
			lObjTrnPnsnProcInwardPensionVO.setPpoDate(lDtPpoDate);
			lObjTrnPnsnProcInwardPensionVO.setDocumentCount(lLngDocumentCount);
			lObjTrnPnsnProcInwardPensionVO.setRevisionNo(lLngRevisionNo);
			lObjTrnPnsnProcInwardPensionVO.setPayCommission(lStrPayCommission);
			//	if (!"-1".equals(lStrAgOffice) && lStrAgOffice.length() > 0) {
			lObjTrnPnsnProcInwardPensionVO.setAgOfficePension(lStrAGOfficePension);
			lObjTrnPnsnProcInwardPensionVO.setAgOfficeAftrFirstPay(lStrAGOfficeAftrFirstPay);
			lObjTrnPnsnProcInwardPensionVO.setTrsryIdPension(lLngTrsryIdPension);
			lObjTrnPnsnProcInwardPensionVO.setTrsryIdAftrFirstPay(lLngTrsryAftrFirstPay);
			//}
			lObjTrnPnsnProcInwardPensionVO.setCommensionDate(lDtCommencementDate);

			lObjTrnPnsnProcInwardPensionVO.setOutwardNo(lStrOutwardNo);
			lObjTrnPnsnProcInwardPensionVO.setOutwardDate(lDtOutwardDate);
			
			if (lStrCommuateFlag == null) {
				lObjTrnPnsnProcInwardPensionVO.setCommuateFlag('N');
			} else {
				lObjTrnPnsnProcInwardPensionVO.setCommuateFlag(lStrCommuateFlag.charAt(0));
				if (lStrCommuateFlag.equalsIgnoreCase("Y")) {
					lObjTrnPnsnProcInwardPensionVO.setCommuteVal(Double.parseDouble((StringUtility.getParameter("txtDoWantCommute", request).trim())));
				}
			}
			if (!lStrPensionType.equals("") && !lStrPensionType.equals("-1")) {
				lObjTrnPnsnProcInwardPensionVO.setPensionType(lStrPensionType);
			}

			lObjTrnPnsnProcInwardPensionVO.setPensionerType(lStrPensionerType);
			lObjTrnPnsnProcInwardPensionVO.setManualComputationFlag('N');
			lObjTrnPnsnProcInwardPensionVO.setCaseStatus(lStrCaseStatus);
			Character lChApprovedFlag = 'A';
			if("L".equals(lStrDraftFlag))
				lObjTrnPnsnProcInwardPensionVO.setDraftFlag(lChApprovedFlag);
			else
				lObjTrnPnsnProcInwardPensionVO.setDraftFlag(lStrDraftFlag.charAt(0));
			
			if (lStrOtherPnsnrType.length() > 0) {
				lObjTrnPnsnProcInwardPensionVO.setOtherPnsnrType(lStrOtherPnsnrType);
			}

			if ("Add".equalsIgnoreCase(lStrMode)) {
				lObjTrnPnsnProcInwardPensionVO.setCreatedUserId(gLngUserId);
				lObjTrnPnsnProcInwardPensionVO.setCreatedPostId(gLngPostId);
				lObjTrnPnsnProcInwardPensionVO.setCreatedDate(lDtcurDate);
			}
			if ("Update".equalsIgnoreCase(lStrMode)) {
				lObjTrnPnsnProcInwardPensionVO.setUpdatedDate(lDtcurDate);
				lObjTrnPnsnProcInwardPensionVO.setUpdatedPostId(gLngPostId);
				lObjTrnPnsnProcInwardPensionVO.setUpdatedUserId(gLngUserId);
			}
			lObjTrnPnsnProcInwardPensionVO.setDbId(gLngDBId);
		} catch (Exception e) {

			gLogger.error("Error in generateTrnPnsnProcInwardPensionVO method is :" + e, e);

		}

		return lObjTrnPnsnProcInwardPensionVO;
	}

	/*
	 *  (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionproc.service.PensionCaseVOGenerator#
	 * generateMstPnsnProcPnsnrDtlsVO(java.util.Map)
	 */

	public TrnPnsnProcPnsnrDtls generateTrnPnsnProcPnsnrDtlsVO(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPnsnProcPnsnrDtls lObjTrnPnsnProcPnsnrDtlsVO = null;
		if (inputMap.containsKey("lObjTrnPnsnProcPnsnrDtlsVO")) {
			lObjTrnPnsnProcPnsnrDtlsVO = (TrnPnsnProcPnsnrDtls) inputMap.get("lObjTrnPnsnProcPnsnrDtlsVO");
		} else {
			lObjTrnPnsnProcPnsnrDtlsVO = new TrnPnsnProcPnsnrDtls();
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		try {

			String lStrPnsnrName = "";
			String lStrPnsnrNameInMarathi = "";
			Date lDtBirthDate = null;
			Date lDtJoiningDate = null;
			Date lDtRetirementDate = null;
			Date lDtDeathDate = null;
			Date lDtLostDate = null;
			Date lDtDemandedRetirementDate = null;
			Date lDtFirDate = null;
			String lStrGenderFlag = "";
			Long lLngGroupLookupId = 0L;
			String lStrDesignation = "";
			Integer lIntHeightFeet = 0;
			Integer lIntHeightInches = 0;
			String lStrRemarks = "";
			String lStrPnsnrAddrFlat = "";
			String lStrPnsnrAddrRoad = "";
			String lStrPnsnrAddrArea = "";
			String lStrPnsnrAddrDistCode = "";
			Long lLngPnsnrAddrStateCode = 0L;
			Long lLngPnsnrAddrPincode = 0L;
			String lStrPnsnrAddrMobileNo = "";
			String lStrPnsnrAddrLandlineNo = "";
			String lStrPnsnrAddrEmailAddr = "";			
			String lStrPnsnrAddrRetFlag = "";
			String lStrPnsnrAddrRetFlat = "";
			String lStrPnsnrAddrRetRoad = "";
			String lStrPnsnrAddrRetArea = "";
			String lStrPnsnrAddrRetDistCode = "";
			Long lLngPnsnrAddrRetStateCode = 0L;
			Long lLngPnsnrAddrRetPincode = 0L;
			String lStrPnsnrAddrRetMobileNo = "";
			String lStrPnsnrAddrRetLandlineNo = "";
			String lStrPnsnrAddrRetEmailAddr = "";
			Long lLngDepartmentId = 0L;
			Long lLngHooId = 0L;
			String lStrChangeTrsryFlag = "";
			Long lLngRevisedReason = 0L;
			String lStrOfficeFlat = "";
			String lStrOfficeRoad = "";
			String lStrOfficeArea = "";
			String lStrOfficeDistCode = "";
			Long lLngOfficeStateCode = 0L;
			Long lLngOfficePincode = 0L;
			String lStrOfficeMobileNo = "";
			String lStrOfficeLandLineNo = "";
			String lStrOfficeEmailAddr = "";
			String lStrBankName = "";
			String lStrBankBranchName = "";
			String lStrBankAddress = "";
			String lStrBankAccountNo = "";
			String lStrBankIfscCode = "";
			String lStrUId = "";
			Date lDtcurDate = SessionHelper.getCurDate();
			String lStrDeathDate = "";
			String lStrEId = "";
			String lStrMode = "";
			String lStrEfpYear = "";
			String lStrBnOrAn = "";
			String lStrPANNo = "";
			String lStrDdoOffName = "";

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			setSessionInfo(inputMap);

			if (StringUtility.getParameter("txtPnsnrName", request) != null && StringUtility.getParameter("txtPnsnrName", request).length() > 0) {
				lStrPnsnrName = StringUtility.getParameter("txtPnsnrName", request).toUpperCase();
				gLogger.info("Pensioner Name:" + lStrPnsnrName);
			}

			lStrPnsnrNameInMarathi = StringUtility.getParameter("txtPnsnrNameInMarathi", request);
			lStrDdoOffName = StringUtility.getParameter("txtDdoOfficeName", request);			

			lDtBirthDate = ((StringUtility.getParameter("txtDateOfBirth", request).trim()).length() > 0) ? simpleDateFormat.parse(StringUtility.getParameter("txtDateOfBirth",
					request).trim()) : null;
			gLogger.info("Date of Birth:" + lDtBirthDate);

			lDtJoiningDate = ((StringUtility.getParameter("txtDateOfStartingService", request).trim()).length() > 0) ? simpleDateFormat.parse(StringUtility.getParameter(
					"txtDateOfStartingService", request).trim()) : null;
			gLogger.info("Date of Starting Service:" + lDtJoiningDate);

			lDtRetirementDate = ((StringUtility.getParameter("txtDateOfRetiremt", request).trim()).length() > 0) ? simpleDateFormat.parse(StringUtility.getParameter(
					"txtDateOfRetiremt", request).trim()) : null;
			gLogger.info("Date of Retirement:" + lDtRetirementDate);

			lStrDeathDate = StringUtility.getParameter("txtDateOfExpiry", request);

			if (lStrDeathDate.trim().length() > 0) {
				lDtDeathDate = simpleDateFormat.parse(lStrDeathDate);
			}

			gLogger.info("Date of Retirement:" + lDtRetirementDate);
			lStrGenderFlag = ((StringUtility.getParameter("radioMaleFemale", request).trim()).length() > 0) ? StringUtility.getParameter("radioMaleFemale", request).trim()
					.toUpperCase() : null;
			
			lStrBnOrAn = StringUtility.getParameter("radioBnOrAn", request);
			lStrEfpYear = StringUtility.getParameter("radioEfp", request);

			if (!StringUtility.getParameter("cmbGroupOrClass", request).equals("-1") && StringUtility.getParameter("cmbGroupOrClass", request).length() > 0) {
				lLngGroupLookupId = Long.parseLong(StringUtility.getParameter("cmbGroupOrClass", request).trim());
			}
			lStrDesignation = StringUtility.getParameter("txtDesignation", request).trim();

			lIntHeightFeet = ((StringUtility.getParameter("txtHeight", request).trim()).length() > 0) ? Integer.parseInt(StringUtility.getParameter("txtHeight", request)) : null;
			gLogger.info("Height in feet:" + lIntHeightFeet);

			lIntHeightInches = ((StringUtility.getParameter("txtInches", request).trim()).length() > 0) ? Integer.parseInt(StringUtility.getParameter("txtInches", request)) : null;
			gLogger.info("Height in Inches:" + lIntHeightInches);

			lStrRemarks = ((StringUtility.getParameter("txtPersRemarks", request).trim()).length() > 0) ? StringUtility.getParameter("txtPersRemarks", request) : null;
			gLogger.info("Personal Remarks:" + lStrRemarks);

			lStrPnsnrAddrFlat = ((StringUtility.getParameter("txtPrFlatDoorBlk", request).trim()).length() > 0) ? StringUtility.getParameter("txtPrFlatDoorBlk", request) : null;
			gLogger.info("Present Flat/Door/Block no:" + lStrPnsnrAddrFlat);

			lStrPnsnrAddrRoad = ((StringUtility.getParameter("txtPrRoadPostOff", request).trim()).length() > 0) ? StringUtility.getParameter("txtPrRoadPostOff", request) : null;
			gLogger.info("Present Road/Post Office:" + lStrPnsnrAddrRoad);

			lStrPnsnrAddrRoad = ((StringUtility.getParameter("txtPrRoadPostOff", request).trim()).length() > 0) ? StringUtility.getParameter("txtPrRoadPostOff", request) : null;
			gLogger.info("Present Road/Post Office:" + lStrPnsnrAddrRoad);

			lStrPnsnrAddrArea = ((StringUtility.getParameter("txtPrAreaLocality", request).trim()).length() > 0) ? StringUtility.getParameter("txtPrAreaLocality", request) : null;
			gLogger.info("pnsnrAddrArea:" + lStrPnsnrAddrArea);

			if (!StringUtility.getParameter("cmbPrTownCityDist", request).equals("-1") && StringUtility.getParameter("cmbPrTownCityDist", request).length() > 0) {
				lStrPnsnrAddrDistCode = StringUtility.getParameter("cmbPrTownCityDist", request).trim();

			}

			if (!StringUtility.getParameter("cmbPrState", request).equals("-1") && StringUtility.getParameter("cmbPrState", request).length() > 0) {
				lLngPnsnrAddrStateCode = Long.parseLong(StringUtility.getParameter("cmbPrState", request).trim());
				gLogger.info("pnsnrAddrStateCode:" + lLngPnsnrAddrStateCode);

			}

			lLngPnsnrAddrPincode = ((StringUtility.getParameter("txtPrPincode", request).trim()).length() > 0) ? Long
					.parseLong(StringUtility.getParameter("txtPrPincode", request)) : null;

			gLogger.info("Present Addr Pin code:" + lLngPnsnrAddrPincode);

			lStrPnsnrAddrMobileNo = ((StringUtility.getParameter("txtMobileNo", request).trim()).length() > 0) ? StringUtility.getParameter("txtMobileNo", request) : null;
			gLogger.info("Mobile No:" + lStrPnsnrAddrMobileNo);

			lStrPnsnrAddrRetMobileNo = StringUtility.getParameter("txtARMobileNo", request).trim();

			lStrPnsnrAddrRetLandlineNo = StringUtility.getParameter("txtARLandlineNo", request).trim();

			lStrPnsnrAddrRetEmailAddr = StringUtility.getParameter("txtAREmailId", request).trim();
			
			lStrPANNo = StringUtility.getParameter("txtPANNo", request).trim();

			lStrPnsnrAddrLandlineNo = ((StringUtility.getParameter("txtLandlineNo", request).trim()).length() > 0) ? StringUtility.getParameter("txtLandlineNo", request) : null;
			gLogger.info("Landline No:" + lStrPnsnrAddrLandlineNo);

			lStrPnsnrAddrEmailAddr = ((StringUtility.getParameter("txtEmailId", request).trim()).length() > 0) ? StringUtility.getParameter("txtEmailId", request) : null;
			gLogger.info("Email Id:" + lStrPnsnrAddrEmailAddr);

			lStrPnsnrAddrRetFlag = ((StringUtility.getParameter("radioIsAddrAfterRet", request).trim()).length() > 0) ? StringUtility.getParameter("radioIsAddrAfterRet", request)
					.trim().toUpperCase() : null;

			lStrPnsnrAddrRetFlat = ((StringUtility.getParameter("txtARFlatDoorBlk", request).trim()).length() > 0) ? StringUtility.getParameter("txtARFlatDoorBlk", request) : null;
			gLogger.info("pnsnrAddrRetFlat:" + lStrPnsnrAddrRetFlat);

			lStrPnsnrAddrRetRoad = ((StringUtility.getParameter("txtARRoadPost", request).trim()).length() > 0) ? StringUtility.getParameter("txtARRoadPost", request) : null;
			gLogger.info("pnsnrAddrRetRoad:" + lStrPnsnrAddrRetRoad);

			lStrPnsnrAddrRetArea = ((StringUtility.getParameter("txtARAreaLocality", request).trim()).length() > 0) ? StringUtility.getParameter("txtARAreaLocality", request)
					: null;
			gLogger.info("pnsnrAddrRetArea:" + lStrPnsnrAddrRetArea);

			if (!StringUtility.getParameter("cmbARTownCityDist", request).equals("-1") && StringUtility.getParameter("cmbARTownCityDist", request).length() > 0) {
				lStrPnsnrAddrRetDistCode = StringUtility.getParameter("cmbARTownCityDist", request).trim();

			}
			if (!StringUtility.getParameter("cmbARState", request).equals("-1") && StringUtility.getParameter("cmbARState", request).length() > 0) {
				lLngPnsnrAddrRetStateCode = Long.parseLong(StringUtility.getParameter("cmbARState", request).trim());
				gLogger.info("pnsnrAddrRetStateCode:" + lLngPnsnrAddrRetStateCode);

			}
			lLngPnsnrAddrRetPincode = ((StringUtility.getParameter("txtARPincode", request).trim()).length() > 0) ? Long.parseLong(StringUtility.getParameter("txtARPincode",
					request)) : null;
			gLogger.info("pnsnrAddrRetPincode:" + lLngPnsnrAddrRetPincode);

			if (!StringUtility.getParameter("cmbFieldDept", request).equals("-1") && StringUtility.getParameter("cmbFieldDept", request).length() > 0) {
				lLngDepartmentId = Long.parseLong(StringUtility.getParameter("cmbFieldDept", request).trim());
				gLogger.info("departmentId:" + lLngDepartmentId);
			}
			if (!StringUtility.getParameter("cmbHeadOfOff", request).equals("-1") && StringUtility.getParameter("cmbHeadOfOff", request).length() > 0) {
				lLngHooId = Long.parseLong(StringUtility.getParameter("cmbHeadOfOff", request).trim());

				gLogger.info("hodId:" + lLngHooId);

			}
			lStrChangeTrsryFlag = ((StringUtility.getParameter("radioIsTreasuryChange", request).trim()).length() > 0) ? StringUtility.getParameter("radioIsTreasuryChange",
					request).trim().toUpperCase() : null;

			lStrOfficeFlat = ((StringUtility.getParameter("txtOffFlatDoorBlk", request).trim()).length() > 0) ? StringUtility.getParameter("txtOffFlatDoorBlk", request) : null;
			gLogger.info("officeFlat:" + lStrOfficeFlat);

			lStrOfficeRoad = ((StringUtility.getParameter("txtOffRoadPostOff", request).trim()).length() > 0) ? StringUtility.getParameter("txtOffRoadPostOff", request) : null;
			gLogger.info("officeRoad:" + lStrOfficeRoad);

			lStrOfficeArea = ((StringUtility.getParameter("txtOffAreaLocality", request).trim()).length() > 0) ? StringUtility.getParameter("txtOffAreaLocality", request) : null;
			gLogger.info("officeArea:" + lStrOfficeArea);

			if (!StringUtility.getParameter("cmbOffTownCityDist", request).equals("-1") && StringUtility.getParameter("cmbOffTownCityDist", request).length() > 0) {
				lStrOfficeDistCode = StringUtility.getParameter("cmbOffTownCityDist", request).trim();

			}
			if (!StringUtility.getParameter("cmbOffState", request).equals("-1") && StringUtility.getParameter("cmbOffState", request).length() > 0) {
				lLngOfficeStateCode = Long.parseLong(StringUtility.getParameter("cmbOffState", request).trim());
				gLogger.info("officeStateCode:" + lLngOfficeStateCode);

			}

			lLngOfficePincode = ((StringUtility.getParameter("txtOffPincode", request).trim()).length() > 0) ? Long.parseLong(StringUtility.getParameter("txtOffPincode", request))
					: null;
			gLogger.info("officePincode:" + lLngOfficePincode);

			lStrOfficeMobileNo = ((StringUtility.getParameter("txtOffMobileNo", request).trim()).length() > 0) ? StringUtility.getParameter("txtOffMobileNo", request) : null;
			gLogger.info("officeMobileNo:" + lStrOfficeMobileNo);

			lStrOfficeLandLineNo = ((StringUtility.getParameter("txtOffLandlineNo", request).trim()).length() > 0) ? StringUtility.getParameter("txtOffLandlineNo", request) : null;
			gLogger.info("officeLandLineNo:" + lStrOfficeLandLineNo);

			lStrOfficeEmailAddr = ((StringUtility.getParameter("txtOffEmailId", request).trim()).length() > 0) ? StringUtility.getParameter("txtOffEmailId", request) : null;
			gLogger.info("officeEmailAddr:" + lStrOfficeEmailAddr);
			lStrBankName = (StringUtility.getParameter("cmbBankName", request) != "-1") ? StringUtility.getParameter("cmbBankName", request) : null;
			gLogger.info("Bank Name:" + lStrBankName);

			lStrUId = ((!("").equals(StringUtility.getParameter("txtUidNo1", request))) ? StringUtility.getParameter("txtUidNo1", request) : "")
					+ ((!("").equals(StringUtility.getParameter("txtUidNo2", request))) ? StringUtility.getParameter("txtUidNo2", request) : "")
					+ ((!("").equals(StringUtility.getParameter("txtUidNo3", request))) ? StringUtility.getParameter("txtUidNo3", request) : "");

			gLogger.info("UID :" + lStrUId);

			lStrEId = StringUtility.getParameter("txtEID", request);

			lStrBankBranchName = (StringUtility.getParameter("cmbTargetBranchName", request) != "-1") ? StringUtility.getParameter("cmbTargetBranchName", request) : null;
			gLogger.info("Bank Branch Name:" + lStrBankBranchName);

			lStrBankAddress = ((StringUtility.getParameter("txtBankAddress", request).trim()).length() > 0) ? StringUtility.getParameter("txtBankAddress", request) : null;
			gLogger.info("Bank Address:" + lStrBankAddress);

			lStrBankAccountNo = ((StringUtility.getParameter("txtActNo", request).trim()).length() > 0) ? StringUtility.getParameter("txtActNo", request) : null;
			gLogger.info("Bank Account No:" + lStrBankAccountNo);

			lStrBankIfscCode = ((StringUtility.getParameter("txtIFSCCode", request).trim()).length() > 0) ? StringUtility.getParameter("txtIFSCCode", request) : null;
			gLogger.info("Bank IFSC Code:" + lStrBankIfscCode);

			lObjTrnPnsnProcPnsnrDtlsVO.setDdoOfficeName(lStrDdoOffName);
			lObjTrnPnsnProcPnsnrDtlsVO.setLocationCode(Long.parseLong(gStrLocationCode));
			lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrName(lStrPnsnrName);
			lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrNameInMarathi(lStrPnsnrNameInMarathi);
			lObjTrnPnsnProcPnsnrDtlsVO.setBirthDate(lDtBirthDate);
			lObjTrnPnsnProcPnsnrDtlsVO.setJoiningDate(lDtJoiningDate);
			lObjTrnPnsnProcPnsnrDtlsVO.setRetirementDate(lDtRetirementDate);
			lObjTrnPnsnProcPnsnrDtlsVO.setDeathDate(lDtDeathDate);
			lObjTrnPnsnProcPnsnrDtlsVO.setLostDate(lDtLostDate);
			lObjTrnPnsnProcPnsnrDtlsVO.setDemandedRetirementDate(lDtDemandedRetirementDate);
			lObjTrnPnsnProcPnsnrDtlsVO.setFirDate(lDtFirDate);
			if (lStrGenderFlag == null) {
				lObjTrnPnsnProcPnsnrDtlsVO.setGenderFlag('M');
			} else {
				lObjTrnPnsnProcPnsnrDtlsVO.setGenderFlag(lStrGenderFlag.charAt(0));

			}

			lObjTrnPnsnProcPnsnrDtlsVO.setPanNo(lStrPANNo);
			lObjTrnPnsnProcPnsnrDtlsVO.setGroupLookupId(lLngGroupLookupId);
			lObjTrnPnsnProcPnsnrDtlsVO.setDesignation(lStrDesignation);
			lObjTrnPnsnProcPnsnrDtlsVO.setHeightFeet(lIntHeightFeet);
			lObjTrnPnsnProcPnsnrDtlsVO.setHeightInches(lIntHeightInches);
			lObjTrnPnsnProcPnsnrDtlsVO.setRemarks(lStrRemarks);
			lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrFlat(lStrPnsnrAddrFlat);
			lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrRoad(lStrPnsnrAddrRoad);
			lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrArea(lStrPnsnrAddrArea);
			lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrDistCode(lStrPnsnrAddrDistCode);
			lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrStateCode(lLngPnsnrAddrStateCode);
			lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrPincode(lLngPnsnrAddrPincode);
			lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrMobileNo(lStrPnsnrAddrMobileNo);
			lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrLandlineNo(lStrPnsnrAddrLandlineNo);
			lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrEmailAddr(lStrPnsnrAddrEmailAddr);
			if (lStrPnsnrAddrRetFlag == null) {
				lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrRetFlag('Y');
			} else {
				lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrRetFlag(lStrPnsnrAddrRetFlag.charAt(0));

			}

			lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrRetFlat(lStrPnsnrAddrRetFlat);
			lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrRetRoad(lStrPnsnrAddrRetRoad);
			lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrRetArea(lStrPnsnrAddrRetArea);
			lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrRetDistCode(lStrPnsnrAddrRetDistCode);
			lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrRetStateCode(lLngPnsnrAddrRetStateCode);
			lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrRetPincode(lLngPnsnrAddrRetPincode);
			if (lStrPnsnrAddrRetMobileNo.length() > 0) {
				lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrRetMobileNo(lStrPnsnrAddrRetMobileNo);
			}
			if (lStrPnsnrAddrRetLandlineNo.length() > 0) {
				lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrRetLandlineNo(lStrPnsnrAddrRetLandlineNo);
			}
			if (lStrPnsnrAddrRetEmailAddr.length() > 0) {
				lObjTrnPnsnProcPnsnrDtlsVO.setPnsnrAddrRetEmailAddr(lStrPnsnrAddrRetEmailAddr);
			}

			lObjTrnPnsnProcPnsnrDtlsVO.setDepartmentId(lLngDepartmentId);
			lObjTrnPnsnProcPnsnrDtlsVO.setHooId(lLngHooId);
			if (lStrChangeTrsryFlag == null) {
				lObjTrnPnsnProcPnsnrDtlsVO.setChangeTrsryFlag('N');
			} else {
				lObjTrnPnsnProcPnsnrDtlsVO.setChangeTrsryFlag(lStrChangeTrsryFlag.charAt(0));

			}
			
			lObjTrnPnsnProcPnsnrDtlsVO.setBnOrAn(lStrBnOrAn);
			if("".equals(lStrEfpYear)){
				lObjTrnPnsnProcPnsnrDtlsVO.setEfpYear(null);
			}else{
				lObjTrnPnsnProcPnsnrDtlsVO.setEfpYear(lStrEfpYear.charAt(0));
			}
			lObjTrnPnsnProcPnsnrDtlsVO.setRevisedReason(lLngRevisedReason);
			lObjTrnPnsnProcPnsnrDtlsVO.setOfficeFlat(lStrOfficeFlat);
			lObjTrnPnsnProcPnsnrDtlsVO.setOfficeRoad(lStrOfficeRoad);
			lObjTrnPnsnProcPnsnrDtlsVO.setOfficeArea(lStrOfficeArea);
			lObjTrnPnsnProcPnsnrDtlsVO.setOfficeDistCode(lStrOfficeDistCode);
			lObjTrnPnsnProcPnsnrDtlsVO.setOfficeStateCode(lLngOfficeStateCode);
			lObjTrnPnsnProcPnsnrDtlsVO.setOfficePincode(lLngOfficePincode);
			lObjTrnPnsnProcPnsnrDtlsVO.setOfficeMobileNo(lStrOfficeMobileNo);
			lObjTrnPnsnProcPnsnrDtlsVO.setOfficeLandLineNo(lStrOfficeLandLineNo);
			lObjTrnPnsnProcPnsnrDtlsVO.setOfficeEmailAddr(lStrOfficeEmailAddr);
			lObjTrnPnsnProcPnsnrDtlsVO.setOfficeEmailAddr(lStrOfficeEmailAddr);
			lObjTrnPnsnProcPnsnrDtlsVO.setBankName(lStrBankName);
			lObjTrnPnsnProcPnsnrDtlsVO.setBankBranchName(lStrBankBranchName);
			lObjTrnPnsnProcPnsnrDtlsVO.setBankAddress(lStrBankAddress);
			lObjTrnPnsnProcPnsnrDtlsVO.setBankAccountNo(lStrBankAccountNo);
			lObjTrnPnsnProcPnsnrDtlsVO.setBankIfscCode(lStrBankIfscCode);
			if (lStrUId.length() > 0) {
				lObjTrnPnsnProcPnsnrDtlsVO.setuId(lStrUId);
			}

			if (lStrEId.length() > 0) {
				lObjTrnPnsnProcPnsnrDtlsVO.seteId(lStrEId);
			}

			if ("Add".equalsIgnoreCase(lStrMode)) {
				lObjTrnPnsnProcPnsnrDtlsVO.setCreatedUserId(gLngUserId);
				lObjTrnPnsnProcPnsnrDtlsVO.setCreatedPostId(gLngPostId);
				lObjTrnPnsnProcPnsnrDtlsVO.setCreatedDate(lDtcurDate);
			}
			if ("Update".equalsIgnoreCase(lStrMode)) {
				lObjTrnPnsnProcPnsnrDtlsVO.setUpdatedDate(lDtcurDate);
				lObjTrnPnsnProcPnsnrDtlsVO.setUpdatedPostId(gLngPostId);
				lObjTrnPnsnProcPnsnrDtlsVO.setUpdatedUserId(gLngUserId);
			}
			lObjTrnPnsnProcPnsnrDtlsVO.setDbId(gLngDBId);

		} catch (Exception e) {

			gLogger.error("Error in generateTrnPnsnProcPnsnrDtlsVO method is :" + e, e);

		}

		return lObjTrnPnsnProcPnsnrDtlsVO;
	}

	// * generateVO method for pay details added by sneha

	public TrnPnsnprocPnsnrpay generateTrnPnsnprocPnsnrpayVO(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		TrnPnsnprocPnsnrpay lObjTrnPnsnprocPnsnrpayVO = null;
		if (inputMap.containsKey("lObjTrnPnsnprocPnsnrpayVO")) {
			lObjTrnPnsnprocPnsnrpayVO = (TrnPnsnprocPnsnrpay) inputMap.get("lObjTrnPnsnprocPnsnrpayVO");
		} else {
			lObjTrnPnsnprocPnsnrpayVO = new TrnPnsnprocPnsnrpay();
		}

		try {
			Long lLngLastPayScale = null;
			BigDecimal lBgDcmlGradePay = null;
			BigDecimal lBgDcmlBasicPay = null;
			BigDecimal lBgDcmlDpRate = null;
			String lStrSrvcRenderdFlag = null;
			String lStrPnsnCntrbtnFlag = null;
			Date lDtPnsnCntrbtnFromDate = null;
			Date lDtPnsnCntrbtnToDate = null;
			BigDecimal lBgDcmlPnsnCntrbtnAmount = null;
			String lStrPrvsnlPnsnpaidFlag = null;
			Date lDtPrvsnlPnsnaidFormDate = null;
			Date lDtPrvsnlPnsnaidToDate = null;
			//Long lLngPrvsnlPnsnpaidVoucherno = null;
			//BigDecimal lBgDcmlPrvsnlPnsnPaidAmount = null;
			String lStrPrvsnlGratuityFlag = null;
			Date lDtPrvsnlGratuityDate = null;
			Long lLngPrvsnlGratuityVoucherno = null;
			BigDecimal lBgDcmlPrvsnlGratuityAmount = null;
			Long lLngTotalServiceBreak = null;
			Long lLngQualifyingService = null;
			Long lLngActualService = null;
			Long lLngNonQualifyingService = null;
			BigDecimal lBgDcmlGrandTotal = null;
			BigDecimal lBgDcmlAveragePay = null;
			Date lDtcurDate = SessionHelper.getCurDate();
			// setting session info
			setSessionInfo(inputMap);
			String lStrMode = null;
			String lStrSanctionAuthNo = null;
			String lStrSanctionAuthName = null;
			Integer lIntBrkYear = 0;
			Integer lIntBrkMonth = 0;
			Integer lIntBrkDay = 0;
			Integer lIntActualYear = 0;
			Integer lIntActualMonth = 0;
			Integer lIntActualDay = 0;
			Integer lIntQulifyYear = 0;
			Integer lIntQulifyMonth = 0;
			Integer lIntQulifyDay = 0;
			String lStrForeignPayInfo = null;

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			if (StringUtility.getParameter("txtTotSerBreakYear", request) != "" && StringUtility.getParameter("txtTotSerBreakYear", request).length() > 0) {
				lIntBrkYear = new Integer(StringUtility.getParameter("txtTotSerBreakYear", request));			
			}
			if (StringUtility.getParameter("txtTotSerBreakMonth", request) != "" && StringUtility.getParameter("txtTotSerBreakMonth", request).length() > 0) {
				lIntBrkMonth = new Integer(StringUtility.getParameter("txtTotSerBreakMonth", request));			
			}
			if (StringUtility.getParameter("txtTotSerBreakDay", request) != "" && StringUtility.getParameter("txtTotSerBreakDay", request).length() > 0) {
				lIntBrkDay = new Integer(StringUtility.getParameter("txtTotSerBreakDay", request));			
			}
			if (StringUtility.getParameter("txtActualSerYear", request) != "" && StringUtility.getParameter("txtActualSerYear", request).length() > 0) {
				lIntActualYear = new Integer(StringUtility.getParameter("txtActualSerYear", request));			
			}
			if (StringUtility.getParameter("txtActualSerMonth", request) != "" && StringUtility.getParameter("txtActualSerMonth", request).length() > 0) {
				lIntActualMonth = new Integer(StringUtility.getParameter("txtActualSerMonth", request));			
			}
			if (StringUtility.getParameter("txtActualSerDay", request) != "" && StringUtility.getParameter("txtActualSerDay", request).length() > 0) {
				lIntActualDay = new Integer(StringUtility.getParameter("txtActualSerDay", request));			
			}
			if (StringUtility.getParameter("txtQualifyingServYear", request) != "" && StringUtility.getParameter("txtQualifyingServYear", request).length() > 0) {
				lIntQulifyYear = new Integer(StringUtility.getParameter("txtQualifyingServYear", request));			
			}
			if (StringUtility.getParameter("txtQualifyingServMonth", request) != "" && StringUtility.getParameter("txtQualifyingServMonth", request).length() > 0) {
				lIntQulifyMonth = new Integer(StringUtility.getParameter("txtQualifyingServMonth", request));			
			}
			if (StringUtility.getParameter("txtQualifyingServDay", request) != "" && StringUtility.getParameter("txtQualifyingServDay", request).length() > 0) {
				lIntQulifyDay = new Integer(StringUtility.getParameter("txtQualifyingServDay", request));			
			}
			
			if (StringUtility.getParameter("cmbPayScale", request) != null && StringUtility.getParameter("cmbPayScale", request).length() > 0) {
				lLngLastPayScale = new Long(StringUtility.getParameter("cmbPayScale", request));
				gLogger.info("Pensioner last pay scale:" + lLngLastPayScale);
			}

			if (StringUtility.getParameter("txtGradePay", request) != null && StringUtility.getParameter("txtGradePay", request).length() > 0) {
				lBgDcmlGradePay = new BigDecimal(StringUtility.getParameter("txtGradePay", request));
				gLogger.info("Pensioner grade pay :" + lBgDcmlGradePay);
			}

			if (StringUtility.getParameter("txtBasicPay", request) != null && StringUtility.getParameter("txtBasicPay", request).length() > 0) {
				lBgDcmlBasicPay = new BigDecimal(StringUtility.getParameter("txtBasicPay", request));
				gLogger.info("Pensioner basic pay :" + lBgDcmlBasicPay);
			}

			if (StringUtility.getParameter("txtDP", request) != null && StringUtility.getParameter("txtDP", request).length() > 0) {
				lBgDcmlDpRate = new BigDecimal(StringUtility.getParameter("txtDP", request));
				gLogger.info("Pensioner DP rate:" + lBgDcmlDpRate);
			}

			lStrSrvcRenderdFlag = ((StringUtility.getParameter("radioServRenderForeign", request).trim()).length() > 0) ? StringUtility.getParameter("radioServRenderForeign",
					request).trim().toUpperCase() : null;

			lStrPnsnCntrbtnFlag = ((StringUtility.getParameter("radioPnsnGrtyPaid", request).trim()).length() > 0) ? StringUtility.getParameter("radioPnsnGrtyPaid", request)
					.trim().toUpperCase() : null;

			lDtPnsnCntrbtnFromDate = ((StringUtility.getParameter("txtFromDatePnsnGrty", request).trim()).length() > 0) ? simpleDateFormat.parse(StringUtility.getParameter(
					"txtFromDatePnsnGrty", request).trim()) : null;
			gLogger.info("Date from pension contribution paid:" + lDtPnsnCntrbtnFromDate);

			lDtPnsnCntrbtnToDate = ((StringUtility.getParameter("txtToDatePnsnGrty", request).trim()).length() > 0) ? simpleDateFormat.parse(StringUtility.getParameter(
					"txtToDatePnsnGrty", request).trim()) : null;
			gLogger.info("Date to pension contribution paid:" + lDtPnsnCntrbtnToDate);

			if (StringUtility.getParameter("txtPnsnGrtyAmount", request) != null && StringUtility.getParameter("txtPnsnGrtyAmount", request).length() > 0) {
				lBgDcmlPnsnCntrbtnAmount = new BigDecimal(StringUtility.getParameter("txtPnsnGrtyAmount", request));
				gLogger.info("Pensioner contribution paid amount :" + lBgDcmlPnsnCntrbtnAmount);
			}

			lStrPrvsnlPnsnpaidFlag = ((StringUtility.getParameter("radioPrvsnlPnsnPaid", request).trim()).length() > 0) ? StringUtility
				.getParameter("radioPrvsnlPnsnPaid", request).trim().toUpperCase() : null;

			lDtPrvsnlPnsnaidFormDate = ((StringUtility.getParameter("txtFromDatePrvsnlPnsn", request).trim()).length() > 0) ? simpleDateFormat.parse(StringUtility.getParameter(
						"txtFromDatePrvsnlPnsn", request).trim()) : null;				
	
			lDtPrvsnlPnsnaidToDate = ((StringUtility.getParameter("txtToDatePrvsnlPnsn", request).trim()).length() > 0) ? simpleDateFormat.parse(StringUtility.getParameter(
						"txtToDatePrvsnlPnsn", request).trim()) : null;
			
				
			/*lDtPrvsnlPnsnaidDate = ((StringUtility.getParameter("txtDateOfPrvsnlPnsn", request).trim()).length() > 0) ? simpleDateFormat.parse(StringUtility.getParameter(
					"txtDateOfPrvsnlPnsn", request).trim()) : null;
			gLogger.info("Date to provisional pension paid:" + lDtPrvsnlPnsnaidDate);*/

			/*lLngPrvsnlPnsnpaidVoucherno = ((StringUtility.getParameter("txtPrvsnlPnsnVoucherNo", request).trim()).length() > 0) ? Long.parseLong(StringUtility.getParameter(
					"txtPrvsnlPnsnVoucherNo", request)) : null;*/

			/*if (StringUtility.getParameter("txtPrvsnlPnsnAmount", request) != null && StringUtility.getParameter("txtPrvsnlPnsnAmount", request).length() > 0) {
				lBgDcmlPrvsnlPnsnPaidAmount = new BigDecimal(StringUtility.getParameter("txtPrvsnlPnsnAmount", request));
				gLogger.info("provisional pension paid amount :" + lBgDcmlPrvsnlPnsnPaidAmount);
			}*/

			lStrPrvsnlGratuityFlag = ((StringUtility.getParameter("radioPrvsnlGrtyPaid", request).trim()).length() > 0) ? StringUtility
					.getParameter("radioPrvsnlGrtyPaid", request).trim().toUpperCase() : null;

			lDtPrvsnlGratuityDate = ((StringUtility.getParameter("txtDateOfPnsnGrtyPaid", request).trim()).length() > 0) ? simpleDateFormat.parse(StringUtility.getParameter(
					"txtDateOfPnsnGrtyPaid", request).trim()) : null;
			gLogger.info("Date to provisional gratuity paid:" + lDtPrvsnlGratuityDate);

			lLngPrvsnlGratuityVoucherno = ((StringUtility.getParameter("txtPrvsnlGrtyVoucherNo", request).trim()).length() > 0) ? Long.parseLong(StringUtility.getParameter(
					"txtPrvsnlGrtyVoucherNo", request)) : null;

			if (StringUtility.getParameter("txtPrvsnlGrtyAmount", request) != null && StringUtility.getParameter("txtPrvsnlGrtyAmount", request).length() > 0) {
				lBgDcmlPrvsnlGratuityAmount = new BigDecimal(StringUtility.getParameter("txtPrvsnlGrtyAmount", request));
				gLogger.info("provisional gratuity paid amount :" + lBgDcmlPrvsnlGratuityAmount);
			}
			lLngTotalServiceBreak = ((StringUtility.getParameter("txtTotSerBreak", request).trim()).length() > 0) ? Long.parseLong(StringUtility.getParameter("txtTotSerBreak",
					request)) : null;
			gLogger.info("Total service break:" + lLngTotalServiceBreak);

			lLngQualifyingService = ((StringUtility.getParameter("txtQualifyingServ", request).trim()).length() > 0) ? Long.parseLong(StringUtility.getParameter(
					"txtQualifyingServ", request)) : null;
			gLogger.info("qualifying service:" + lLngQualifyingService);

			lLngActualService = ((StringUtility.getParameter("txtActualSer", request).trim()).length() > 0) ? Long.parseLong(StringUtility.getParameter("txtActualSer", request))
					: null;
			gLogger.info("Actual service:" + lLngActualService);

			lLngNonQualifyingService = ((StringUtility.getParameter("txtNonQualifyingServ", request).trim()).length() > 0) ? Long.parseLong(StringUtility.getParameter(
					"txtNonQualifyingServ", request)) : null;
			lBgDcmlGrandTotal = ((StringUtility.getParameter("txtGrandTotal", request).trim()).length() > 0) ? new BigDecimal(StringUtility.getParameter("txtGrandTotal", request))
					: null;

			lBgDcmlAveragePay = ((StringUtility.getParameter("txtAvgPay", request).trim()).length() > 0) ? new BigDecimal(StringUtility.getParameter("txtAvgPay", request)) : null;
			lStrSanctionAuthNo = StringUtility.getParameter("txtSancAuthNo", request);
			lStrSanctionAuthName = StringUtility.getParameter("txtSancAuthName", request).toUpperCase();
			lStrForeignPayInfo = StringUtility.getParameter("cmbRecoveryType", request);
			
			lObjTrnPnsnprocPnsnrpayVO.setLocationCode(Long.parseLong(gStrLocationCode));
			lObjTrnPnsnprocPnsnrpayVO.setLastPayScale(lLngLastPayScale);
			lObjTrnPnsnprocPnsnrpayVO.setGradePay(lBgDcmlGradePay);
			lObjTrnPnsnprocPnsnrpayVO.setBasicPay(lBgDcmlBasicPay);
			lObjTrnPnsnprocPnsnrpayVO.setDpRate(lBgDcmlDpRate);
			lObjTrnPnsnprocPnsnrpayVO.setForeignPayInfo(lStrForeignPayInfo);
			
			if (lStrSrvcRenderdFlag == null) {
				lObjTrnPnsnprocPnsnrpayVO.setSrvcRenderdFlag('N');
			} else {
				lObjTrnPnsnprocPnsnrpayVO.setSrvcRenderdFlag(lStrSrvcRenderdFlag.charAt(0));

			}
			if (lStrPnsnCntrbtnFlag == null) {
				lObjTrnPnsnprocPnsnrpayVO.setPnsnCntrbtnFlag('N');
			} else {
				lObjTrnPnsnprocPnsnrpayVO.setPnsnCntrbtnFlag(lStrPnsnCntrbtnFlag.charAt(0));

			}
			lObjTrnPnsnprocPnsnrpayVO.setPnsnCntrbtnFromDate(lDtPnsnCntrbtnFromDate);
			lObjTrnPnsnprocPnsnrpayVO.setPnsnCntrbtnToDate(lDtPnsnCntrbtnToDate);
			lObjTrnPnsnprocPnsnrpayVO.setPnsnCntrbtnAmount(lBgDcmlPnsnCntrbtnAmount);
			if (lStrPrvsnlPnsnpaidFlag == null) {
				lObjTrnPnsnprocPnsnrpayVO.setPrvsnlPnsnpaidFlag('N');
			} else {
				lObjTrnPnsnprocPnsnrpayVO.setPrvsnlPnsnpaidFlag(lStrPrvsnlPnsnpaidFlag.charAt(0));

			}

			/*lObjTrnPnsnprocPnsnrpayVO.setPrvsnlPnsnaidDate(lDtPrvsnlPnsnaidDate);
			lObjTrnPnsnprocPnsnrpayVO.setPrvsnlPnsnpaidVoucherno(lLngPrvsnlPnsnpaidVoucherno);
			lObjTrnPnsnprocPnsnrpayVO.setPrvsnlPnsnpaidAmount(lBgDcmlPrvsnlPnsnPaidAmount);*/
			if (lStrPrvsnlGratuityFlag == null) {
				lObjTrnPnsnprocPnsnrpayVO.setPrvsnlGratuityFlag('N');
			} else {
				lObjTrnPnsnprocPnsnrpayVO.setPrvsnlGratuityFlag(lStrPrvsnlGratuityFlag.charAt(0));

			}
			lObjTrnPnsnprocPnsnrpayVO.setBrkYear(lIntBrkYear);
			lObjTrnPnsnprocPnsnrpayVO.setBrkMonth(lIntBrkMonth);
			lObjTrnPnsnprocPnsnrpayVO.setBrkDay(lIntBrkDay);
			lObjTrnPnsnprocPnsnrpayVO.setQulifyYear(lIntQulifyYear);
			lObjTrnPnsnprocPnsnrpayVO.setQulifyMonth(lIntQulifyMonth);
			lObjTrnPnsnprocPnsnrpayVO.setQulifyDay(lIntQulifyDay);
			lObjTrnPnsnprocPnsnrpayVO.setActualYear(lIntActualYear);
			lObjTrnPnsnprocPnsnrpayVO.setActualMonth(lIntActualMonth);
			lObjTrnPnsnprocPnsnrpayVO.setActualDay(lIntActualDay);
			lObjTrnPnsnprocPnsnrpayVO.setPrvsnlPnsnpaidFromDate(lDtPrvsnlPnsnaidFormDate);
			lObjTrnPnsnprocPnsnrpayVO.setPrvsnlPnsnpaidToDate(lDtPrvsnlPnsnaidToDate);
			lObjTrnPnsnprocPnsnrpayVO.setPrvsnlPnsnpaidAuthNo(lStrSanctionAuthNo);
			lObjTrnPnsnprocPnsnrpayVO.setPrvsnlPnsnpaidAuthName(lStrSanctionAuthName);
			lObjTrnPnsnprocPnsnrpayVO.setPrvsnlGratuityDate(lDtPrvsnlGratuityDate);
			lObjTrnPnsnprocPnsnrpayVO.setPrvsnlGratuityVoucherno(lLngPrvsnlGratuityVoucherno);
			lObjTrnPnsnprocPnsnrpayVO.setPrvsnlGratuityAmount(lBgDcmlPrvsnlGratuityAmount);
			lObjTrnPnsnprocPnsnrpayVO.setTotalServiceBreak(lLngTotalServiceBreak);
			lObjTrnPnsnprocPnsnrpayVO.setQualifyingService(lLngQualifyingService);
			lObjTrnPnsnprocPnsnrpayVO.setActualService(lLngActualService);
			lObjTrnPnsnprocPnsnrpayVO.setNonQualifyingService(lLngNonQualifyingService);
			lObjTrnPnsnprocPnsnrpayVO.setGrandTotal(lBgDcmlGrandTotal);
			lObjTrnPnsnprocPnsnrpayVO.setAveragePay(lBgDcmlAveragePay);
			lObjTrnPnsnprocPnsnrpayVO.setDbId(gLngDBId);
			lObjTrnPnsnprocPnsnrpayVO.setUpdatedDate(lDtcurDate);
			if ("Add".equalsIgnoreCase(lStrMode)) {
				lObjTrnPnsnprocPnsnrpayVO.setCreatedUserId(gLngUserId);
				lObjTrnPnsnprocPnsnrpayVO.setCreatedPostId(gLngPostId);
				lObjTrnPnsnprocPnsnrpayVO.setCreatedDate(lDtcurDate);
			}
			if ("Update".equalsIgnoreCase(lStrMode)) {
				lObjTrnPnsnprocPnsnrpayVO.setUpdatedDate(lDtcurDate);
				lObjTrnPnsnprocPnsnrpayVO.setUpdatedPostId(gLngPostId);
				lObjTrnPnsnprocPnsnrpayVO.setUpdatedUserId(gLngUserId);
			}

		} catch (Exception e) {

			gLogger.error("Error is :" + e, e);
		}
		return lObjTrnPnsnprocPnsnrpayVO;
	}

	// generateVO method for event details added by sneha

	public List<TrnPnsnprocEventdtls> generateTrnPnsnprocEventdtlsVO(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		TrnPnsnprocEventdtls lObjTrnPnsnprocEventdtlsVO = null;
		List<TrnPnsnprocEventdtls> lLstTrnPnsnprocEventdtlsVO = new ArrayList<TrnPnsnprocEventdtls>();

		try {

			setSessionInfo(inputMap);
			Date lDtcurDate = SessionHelper.getCurDate();
			String lStrMode = null;
			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			String[] lArrStrEventId = StringUtility.getParameterValues("cmbEvent", request);

			String[] lArrStrPayScaleId = StringUtility.getParameterValues("cmbPayScaleEvent", request);

			String[] lArrStrdcDp = StringUtility.getParameterValues("txtEventDP", request);

			String[] lArrStrdcBasic = StringUtility.getParameterValues("txtBasic", request);

			String[] lArrStrFromDate = StringUtility.getParameterValues("txtDateOfEventFrom", request);

			String[] lArrStrToDate = StringUtility.getParameterValues("txtDateOfEventTo", request);

			String[] lArrStrReason = StringUtility.getParameterValues("txtReason", request);

			if (lArrStrEventId.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrEventId.length; lIntCnt++) {
					lObjTrnPnsnprocEventdtlsVO = new TrnPnsnprocEventdtls();

					lObjTrnPnsnprocEventdtlsVO.setLocationCode(Long.parseLong(gStrLocationCode));

					lObjTrnPnsnprocEventdtlsVO.setCreatedUserId(gLngUserId);
					lObjTrnPnsnprocEventdtlsVO.setCreatedPostId(gLngPostId);
					lObjTrnPnsnprocEventdtlsVO.setCreatedDate(lDtcurDate);
					lObjTrnPnsnprocEventdtlsVO.setUpdatedDate(lDtcurDate);
					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjTrnPnsnprocEventdtlsVO.setUpdatedDate(lDtcurDate);
						lObjTrnPnsnprocEventdtlsVO.setUpdatedPostId(gLngPostId);
						lObjTrnPnsnprocEventdtlsVO.setUpdatedUserId(gLngUserId);
					}

					if (lArrStrEventId[lIntCnt] != null && lArrStrEventId[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocEventdtlsVO.setEventId(Long.parseLong(lArrStrEventId[lIntCnt].trim()));
					}

					if (lArrStrPayScaleId[lIntCnt] != null && lArrStrPayScaleId[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocEventdtlsVO.setPayScaleId((Long.parseLong(lArrStrPayScaleId[lIntCnt].trim())));
					}

					if (lArrStrdcDp[lIntCnt] != null && lArrStrdcDp[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocEventdtlsVO.setDp(new BigDecimal(lArrStrdcDp[lIntCnt].trim()));
					}

					if (lArrStrdcBasic[lIntCnt] != null && lArrStrdcBasic[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocEventdtlsVO.setBasic(new BigDecimal(lArrStrdcBasic[lIntCnt].trim()));
					}

					if (lArrStrFromDate[lIntCnt] != null && lArrStrFromDate[lIntCnt].trim().length() != 0) {
						lObjTrnPnsnprocEventdtlsVO.setFromDate(simpleDateFormat.parse(lArrStrFromDate[lIntCnt].trim()));
					}
					if (lArrStrToDate[lIntCnt] != null && lArrStrToDate[lIntCnt].trim().length() != 0) {
						lObjTrnPnsnprocEventdtlsVO.setToDate(simpleDateFormat.parse(lArrStrToDate[lIntCnt].trim()));
						lObjTrnPnsnprocEventdtlsVO.setReason(null);
					} else {
						lObjTrnPnsnprocEventdtlsVO.setToDate(null);
						lObjTrnPnsnprocEventdtlsVO.setReason(lArrStrReason[lIntCnt].trim());
					}
					lObjTrnPnsnprocEventdtlsVO.setDbId(gLngDBId);
					lLstTrnPnsnprocEventdtlsVO.add(lObjTrnPnsnprocEventdtlsVO);

				}
			}

		} catch (Exception e) {		
			gLogger.error("Error in generateTrnPnsnprocEventdtlsVO method is :" + e, e);
		}

		return lLstTrnPnsnprocEventdtlsVO;
	}

	// generateVO method for service break details added by sneha

	public List<TrnPnsnprocPnsnrservcbreak> generateTrnPnsnprocPnsnrservcbreakVO(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		TrnPnsnprocPnsnrservcbreak lObjTrnPnsnprocPnsnrservcbreakVO = null;
		List<TrnPnsnprocPnsnrservcbreak> lLstTrnPnsnprocPnsnrservcbreakVO = new ArrayList<TrnPnsnprocPnsnrservcbreak>();
		try {
			setSessionInfo(inputMap);
			Date lDtcurDate = SessionHelper.getCurDate();
			String lStrMode = null;

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			String[] lArrStrServiceBreaktypeLookupId = StringUtility.getParameterValues("cmbTypeofBrk", request);
			String[] lArrStrBreakFromDate = StringUtility.getParameterValues("txtDateOfBrkFrom", request);
			String[] lArrStrBreakToDate = StringUtility.getParameterValues("txtDateOfBrkTo", request);
			String[] lArrStrSrvBreakOtherReason = StringUtility.getParameterValues("txtSrvBreakOtherReason", request);
			Integer lIntBreakPeriod = 0;

			if (lArrStrServiceBreaktypeLookupId.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrServiceBreaktypeLookupId.length; lIntCnt++) {
					lObjTrnPnsnprocPnsnrservcbreakVO = new TrnPnsnprocPnsnrservcbreak();

					lObjTrnPnsnprocPnsnrservcbreakVO.setLocationCode(Long.parseLong(gStrLocationCode));
					lObjTrnPnsnprocPnsnrservcbreakVO.setBreakPeriod(lIntBreakPeriod);					
					lObjTrnPnsnprocPnsnrservcbreakVO.setCreatedUserId(gLngUserId);
					lObjTrnPnsnprocPnsnrservcbreakVO.setCreatedPostId(gLngPostId);
					lObjTrnPnsnprocPnsnrservcbreakVO.setCreatedDate(lDtcurDate);
					lObjTrnPnsnprocPnsnrservcbreakVO.setUpdatedDate(lDtcurDate);
					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjTrnPnsnprocPnsnrservcbreakVO.setUpdatedDate(lDtcurDate);
						lObjTrnPnsnprocPnsnrservcbreakVO.setUpdatedPostId(gLngPostId);
						lObjTrnPnsnprocPnsnrservcbreakVO.setUpdatedUserId(gLngUserId);
					}

					if (lArrStrServiceBreaktypeLookupId[lIntCnt] != null && lArrStrServiceBreaktypeLookupId[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocPnsnrservcbreakVO.setServiceBreaktypeLookupId(Long.parseLong(lArrStrServiceBreaktypeLookupId[lIntCnt].trim()));
					}

					if (lArrStrBreakFromDate[lIntCnt] != null && lArrStrBreakFromDate[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocPnsnrservcbreakVO.setBreakFromDate(simpleDateFormat.parse(lArrStrBreakFromDate[lIntCnt].trim()));
					}

					if (lArrStrBreakToDate[lIntCnt] != null && lArrStrBreakToDate[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocPnsnrservcbreakVO.setBreakToDate(simpleDateFormat.parse(lArrStrBreakToDate[lIntCnt].trim()));
					}
					if (lArrStrSrvBreakOtherReason[lIntCnt] != null && lArrStrSrvBreakOtherReason[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocPnsnrservcbreakVO.setSrvcBrkOtherReason(lArrStrSrvBreakOtherReason[lIntCnt]);
					}
					lObjTrnPnsnprocPnsnrservcbreakVO.setDbId(gLngDBId);
					lLstTrnPnsnprocPnsnrservcbreakVO.add(lObjTrnPnsnprocPnsnrservcbreakVO);

				}
			}
		} catch (Exception e) {

			gLogger.info(

			" Exception while generateTrnPnsnprocPnsnrservcbreakVO Grid Insertion........................." + e, e);
		}

		return lLstTrnPnsnprocPnsnrservcbreakVO;
	}

	public List<TrnPnsnprocAvgPayCalc> generateTrnPnsnprocAvgPayCalcVO(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		TrnPnsnprocAvgPayCalc lObjTrnPnsnprocAvgPayCalcVO = null;
		List<TrnPnsnprocAvgPayCalc> lLstTrnPnsnprocAvgPayCalcVO = new ArrayList<TrnPnsnprocAvgPayCalc>();
		try {
			setSessionInfo(inputMap);
			Date lDtcurDate = SessionHelper.getCurDate();
			String lStrMode = null;

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			String lStrEmolumentFromDt = StringUtility.getParameter("txtEmolumentFromDate", request);
			String lStrEmolumentToDt = StringUtility.getParameter("txtEmolumentToDate", request);

			String[] lArrStrPeriodFromDate = StringUtility.getParameterValues("txtPeriodFromDate", request);
			/*String[] lArrStrPeriodToDate = StringUtility
					.getParameterValues("txtPeriodToDate", request);*/
			String[] lArrStrAvgPayBasic = StringUtility.getParameterValues("txtAvgPayBasic", request);
			String[] lArrStrAvgPayDP = StringUtility.getParameterValues("txtAvgPayDP", request);
			String[] lArrStrNPA = StringUtility.getParameterValues("txtNPA", request);
			String[] lArrStrTotal = StringUtility.getParameterValues("txtTotal", request);
			if (lArrStrPeriodFromDate.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrPeriodFromDate.length; lIntCnt++) {
					lObjTrnPnsnprocAvgPayCalcVO = new TrnPnsnprocAvgPayCalc();

					lObjTrnPnsnprocAvgPayCalcVO.setLocationCode(Long.parseLong(gStrLocationCode));

					lObjTrnPnsnprocAvgPayCalcVO.setCreatedUserId(gLngUserId);
					lObjTrnPnsnprocAvgPayCalcVO.setCreatedPostId(gLngPostId);
					lObjTrnPnsnprocAvgPayCalcVO.setCreatedDate(lDtcurDate);
					lObjTrnPnsnprocAvgPayCalcVO.setUpdatedDate(lDtcurDate);
					lObjTrnPnsnprocAvgPayCalcVO.setEmolumentFromDate(StringUtility.convertStringToDate(lStrEmolumentFromDt.trim()));
					lObjTrnPnsnprocAvgPayCalcVO.setEmolumentToDate(StringUtility.convertStringToDate(lStrEmolumentToDt.trim()));
					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjTrnPnsnprocAvgPayCalcVO.setUpdatedDate(lDtcurDate);
						lObjTrnPnsnprocAvgPayCalcVO.setUpdatedPostId(gLngPostId);
						lObjTrnPnsnprocAvgPayCalcVO.setUpdatedUserId(gLngUserId);
					}
					if (lArrStrPeriodFromDate[lIntCnt] != null && lArrStrPeriodFromDate[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocAvgPayCalcVO.setFromDate(simpleDateFormat.parse(lArrStrPeriodFromDate[lIntCnt].trim()));
					}
					/*if (lArrStrPeriodToDate[lIntCnt] != null
							&& lArrStrPeriodToDate[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocAvgPayCalcVO.setToDate(simpleDateFormat
										.parse(lArrStrPeriodToDate[lIntCnt].trim()));
					}*/
					if (lArrStrAvgPayBasic[lIntCnt] != null && lArrStrAvgPayBasic[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocAvgPayCalcVO.setBasic(new BigDecimal(lArrStrAvgPayBasic[lIntCnt].trim()));
					}
					if (lArrStrAvgPayDP[lIntCnt] != null && lArrStrAvgPayDP[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocAvgPayCalcVO.setDp(new BigDecimal(lArrStrAvgPayDP[lIntCnt].trim()));
					}
					if (lArrStrNPA[lIntCnt] != null && lArrStrNPA[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocAvgPayCalcVO.setNpa(new BigDecimal(lArrStrNPA[lIntCnt].trim()));
					}
					if (lArrStrTotal[lIntCnt] != null && lArrStrTotal[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocAvgPayCalcVO.setTotal(new BigDecimal(lArrStrTotal[lIntCnt].trim()));
					}
					lObjTrnPnsnprocAvgPayCalcVO.setDbId(gLngDBId);
					lLstTrnPnsnprocAvgPayCalcVO.add(lObjTrnPnsnprocAvgPayCalcVO);

				}
			}
		} catch (Exception e) {

			gLogger.info(

			" Exception while generateTrnPnsnprocPnsnrservcbreakVO Grid Insertion........................." + e, e);
		}

		return lLstTrnPnsnprocAvgPayCalcVO;

	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionproc.service.PensionCaseVOGenerator#generateTrnPnsnProcPnsnCalcVO(java.util.Map)
	 **/

	public TrnPnsnProcPnsnCalc generateTrnPnsnProcPnsnCalcVO(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPnsnProcPnsnCalc lObjTrnPnsnProcPnsnCalcVO = null;
		if (inputMap.containsKey("lObjTrnPnsnProcPnsnCalcVO")) {
			lObjTrnPnsnProcPnsnCalcVO = (TrnPnsnProcPnsnCalc) inputMap.get("lObjTrnPnsnProcPnsnCalcVO");
		} else {
			lObjTrnPnsnProcPnsnCalcVO = new TrnPnsnProcPnsnCalc();
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		gLogger.info("In generateTrnPnsnProcPnsnCalcVO of PensionCaseVOGeneratorImpl........");

		try {
			setSessionInfo(inputMap);
			BigDecimal lBigDcmlCvpMonthAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlCvpAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlCvpRate = BigDecimal.ZERO;
			BigDecimal lBigDcmlPensionerReducedAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlPensionerTotalAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlSrvcGratuityAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlDcrgAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlFp1Amnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlFp2Amnt = BigDecimal.ZERO;
			Date lDtFp1Date = null;			
			Date lDtFp2Date = null;
			Date lDtCvpAppDate = null;
			Date lDtDcrgAppDate = null;
			Date lDtcurDate = SessionHelper.getCurDate();
			String lStrMode = "";
			String lStrCPONo = "";
			String lStrGPONo = "";
			String lStrCPODate = "";
			String lStrGPODate = "";
			String lStrManualEditFlag = "N";
			Long lLngWitheldGratuity = null;

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			lBigDcmlCvpMonthAmnt = (StringUtility.getParameter("txtMonthAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtMonthAmt", request)
					.trim()) : null;
			gLogger.info("CVP Month Amount is  : " + lBigDcmlCvpMonthAmnt);

			lBigDcmlCvpAmnt = (StringUtility.getParameter("txtCVPAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtCVPAmt", request).trim())
					: null;
			gLogger.info("Commuted Pension Amount is  : " + lBigDcmlCvpAmnt);

			lBigDcmlPensionerTotalAmnt = (StringUtility.getParameter("txtTotPnsnAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtTotPnsnAmt",
					request).trim()) : null;
			gLogger.info("Total Pension Amount is  : " + lBigDcmlPensionerTotalAmnt);

			lBigDcmlPensionerReducedAmnt = (StringUtility.getParameter("txtRedsPnsnAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter(
					"txtRedsPnsnAmt", request).trim()) : null;
			gLogger.info("Reduced Pension Amount is  : " + lBigDcmlPensionerReducedAmnt);

			lBigDcmlSrvcGratuityAmnt = (StringUtility.getParameter("txtServGratyAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtServGratyAmt",
					request).trim()) : null;
			gLogger.info("Service Gratuity Amount is  : " + lBigDcmlSrvcGratuityAmnt);

			lBigDcmlDcrgAmnt = (StringUtility.getParameter("txtTotDCRGAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtTotDCRGAmt", request)
					.trim()) : null;
			gLogger.info("DCRG Amount is  : " + lBigDcmlDcrgAmnt);

			lBigDcmlFp1Amnt = (StringUtility.getParameter("txtFP1TotlAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtFP1TotlAmt", request)
					.trim()) : null;
			gLogger.info("FP1 Amount is  : " + lBigDcmlFp1Amnt);

			lDtFp1Date = ((StringUtility.getParameter("txtFP1Date", request).trim()).length() > 0) ? simpleDateFormat.parse(StringUtility.getParameter("txtFP1Date", request)
					.trim()) : null;
			gLogger.info("FP1 Date is :" + lDtFp1Date);

			lBigDcmlFp2Amnt = (StringUtility.getParameter("txtFP2TotlAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtFP2TotlAmt", request)
					.trim()) : null;
			gLogger.info("FP1 Amount is  : " + lBigDcmlFp2Amnt);

			lDtFp2Date = ((StringUtility.getParameter("txtFP2Date", request).trim()).length() > 0) ? simpleDateFormat.parse(StringUtility.getParameter("txtFP2Date", request)
					.trim()) : null;
			gLogger.info("FP1 Date is :" + lDtFp2Date);

			lBigDcmlCvpRate = (StringUtility.getParameter("txtCvpRate", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtCvpRate", request)
					.trim()) : null;
			
			lDtCvpAppDate = ((StringUtility.getParameter("txtCvpAppDate", request).trim()).length() > 0) ? simpleDateFormat.parse(StringUtility.getParameter("txtCvpAppDate", request)
					.trim()) : null;
			
			lDtDcrgAppDate = ((StringUtility.getParameter("txtDCRGAppDate", request).trim()).length() > 0) ? simpleDateFormat.parse(StringUtility.getParameter("txtDCRGAppDate", request)
					.trim()) : null;
			
			lStrCPONo = StringUtility.getParameter("txtCpoNo", request);
			lStrGPONo = StringUtility.getParameter("txtGpoNo", request);
			
			lStrCPODate = StringUtility.getParameter("txtCpoDate", request);
			lStrGPODate = StringUtility.getParameter("txtGpoDate", request);
			lStrManualEditFlag = StringUtility.getParameter("chkManualEditFlag", request);
			
			lLngWitheldGratuity = (StringUtility.getParameter("txtWitheldGratuity", request).trim().length() > 0) ? new Long(StringUtility.getParameter("txtWitheldGratuity", request)
					.trim()) : null;
			
			if (!"".equals(lStrCPODate) && lStrCPODate.length() > 0) {
				lObjTrnPnsnProcPnsnCalcVO.setCpoDate(simpleDateFormat.parse(lStrCPODate));
			}
			if (!"".equals(lStrGPODate) && lStrGPODate.length() > 0) {
				lObjTrnPnsnProcPnsnCalcVO.setGpoDate(simpleDateFormat.parse(lStrGPODate));
			}
			lObjTrnPnsnProcPnsnCalcVO.setLocationCode(Long.parseLong(gStrLocationCode));
			lObjTrnPnsnProcPnsnCalcVO.setCvpMonthAmnt(lBigDcmlCvpMonthAmnt);
			lObjTrnPnsnProcPnsnCalcVO.setCvpAmnt(lBigDcmlCvpAmnt);
			lObjTrnPnsnProcPnsnCalcVO.setPensionerTotalAmnt(lBigDcmlPensionerTotalAmnt);
			lObjTrnPnsnProcPnsnCalcVO.setPensionerReducedAmnt(lBigDcmlPensionerReducedAmnt);
			lObjTrnPnsnProcPnsnCalcVO.setSrvcGratuityAmnt(lBigDcmlSrvcGratuityAmnt);
			lObjTrnPnsnProcPnsnCalcVO.setDcrgAmnt(lBigDcmlDcrgAmnt);
			lObjTrnPnsnProcPnsnCalcVO.setFp1Amnt(lBigDcmlFp1Amnt);
			lObjTrnPnsnProcPnsnCalcVO.setFp1Date(lDtFp1Date);
			lObjTrnPnsnProcPnsnCalcVO.setFp2Amnt(lBigDcmlFp2Amnt);
			lObjTrnPnsnProcPnsnCalcVO.setFp2Date(lDtFp2Date);
			lObjTrnPnsnProcPnsnCalcVO.setUpdatedDate(lDtcurDate);
			lObjTrnPnsnProcPnsnCalcVO.setManualEditFlag(lStrManualEditFlag);
			lObjTrnPnsnProcPnsnCalcVO.setCvpRate(lBigDcmlCvpRate);
			lObjTrnPnsnProcPnsnCalcVO.setCvpAppDate(lDtCvpAppDate);
			lObjTrnPnsnProcPnsnCalcVO.setWitheldGratuity(lLngWitheldGratuity);
			lObjTrnPnsnProcPnsnCalcVO.setDcrgAppDate(lDtDcrgAppDate);
			
			if (!"".equals(lStrCPONo) && lStrCPONo.length() > 0) {
				lObjTrnPnsnProcPnsnCalcVO.setCpoNo(lStrCPONo.trim());
			}
			if (!"".equals(lStrGPONo) && lStrGPONo.length() > 0) {
				lObjTrnPnsnProcPnsnCalcVO.setGpoNo(lStrGPONo.trim());
			}

			if ("Add".equalsIgnoreCase(lStrMode)) {
				lObjTrnPnsnProcPnsnCalcVO.setCreatedUserId(gLngUserId);
				lObjTrnPnsnProcPnsnCalcVO.setCreatedPostId(gLngPostId);
				lObjTrnPnsnProcPnsnCalcVO.setCreatedDate(lDtcurDate);
			}
			if ("Update".equalsIgnoreCase(lStrMode)) {
				lObjTrnPnsnProcPnsnCalcVO.setUpdatedDate(lDtcurDate);
				lObjTrnPnsnProcPnsnCalcVO.setUpdatedPostId(gLngPostId);
				lObjTrnPnsnProcPnsnCalcVO.setUpdatedUserId(gLngUserId);
			}
			lObjTrnPnsnProcPnsnCalcVO.setDbId(gLngDBId);

		}

		catch (Exception e) {

			gLogger.error("Error in generateTrnPnsnProcPnsnCalcVO method is :" + e, e);

		}
		return lObjTrnPnsnProcPnsnCalcVO;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionproc.service.PensionCaseVOGenerator#generateTrnPnsnProcRecoveryVO(java.util.Map)
	 */

	public TrnPnsnProcRecovery generateTrnPnsnProcRecoveryVO(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPnsnProcRecovery lObjTrnPnsnProcRecoveryVO = null;
		if (inputMap.containsKey("lObjTrnPnsnProcRecoveryVO")) {
			lObjTrnPnsnProcRecoveryVO = (TrnPnsnProcRecovery) inputMap.get("lObjTrnPnsnProcRecoveryVO");
		} else {
			lObjTrnPnsnProcRecoveryVO = new TrnPnsnProcRecovery();
		}

		gLogger.info("In generateTrnPnsnProcRecoveryVO of PensionCaseVOGeneratorImpl........");

		try {

			String lStrAdvnceBalFlag = "";
			BigDecimal lBigDcmlTotalAdvanceAmount = BigDecimal.ZERO;
			BigDecimal lBigDcmlOverPaymentAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlArrearsAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlGovtAcmdtnRetAmnt = BigDecimal.ZERO;
			String lStrAssessedDuesdtlFlag = "";
			BigDecimal lBigDcmlTotalAssessedDues = BigDecimal.ZERO;
			BigDecimal lBigDcmlGratuityAmnt = BigDecimal.ZERO;
			String lStrEnquiryProposedFlag = "";
			String lStrEnquiryPendingFlag = "";
			/*Date lDtInvalidatingDate;
			Date lDtLodgingDate;
			Integer lIntPoliceCertificateNo;
			String lStrPoliceStationName;
			Date lDtPoliceCertificateDate;*/
			Date lDtCurDate = SessionHelper.getCurDate();
			setSessionInfo(inputMap);
			String lStrMode = null;
			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}

			lStrAdvnceBalFlag = ((StringUtility.getParameter("radioBalOfAdv", request).trim()).length() > 0) ? StringUtility.getParameter("radioBalOfAdv", request).trim()
					.toUpperCase() : null;

			lBigDcmlTotalAdvanceAmount = (StringUtility.getParameter("txtTotalAdvanceBalAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter(
					"txtTotalAdvanceBalAmt", request).trim()) : null;

			lBigDcmlOverPaymentAmnt = (StringUtility.getParameter("txtOverPayAllow", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtOverPayAllow",
					request).trim()) : null;
			gLogger.info("lBigDcmlOverPaymentAmnt : " + lBigDcmlOverPaymentAmnt);

			lBigDcmlArrearsAmnt = (StringUtility.getParameter("txtArrearAmnt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtArrearAmnt", request)
					.trim()) : null;
			gLogger.info("lBigDcmlArrearsAmnt  : " + lBigDcmlArrearsAmnt);

			lBigDcmlGovtAcmdtnRetAmnt = (StringUtility.getParameter("txtGovtAcmdtnRetAmnt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter(
					"txtGovtAcmdtnRetAmnt", request).trim()) : null;
			gLogger.info("lBigDcmlGovtAcmdtnRetAmnt  : " + lBigDcmlGovtAcmdtnRetAmnt);

			lStrAssessedDuesdtlFlag = ((StringUtility.getParameter("radioOtherAssessDue", request).trim()).length() > 0) ? StringUtility.getParameter("radioOtherAssessDue",
					request).trim().toUpperCase() : null;

			lBigDcmlTotalAssessedDues = (StringUtility.getParameter("txtTotalAssessDues", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter(
					"txtTotalAssessDues", request).trim()) : null;
			gLogger.info("lBigDcmlTotalAssessedDues  : " + lBigDcmlTotalAssessedDues);

			lBigDcmlGratuityAmnt = (StringUtility.getParameter("txtGratuityAmnt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtGratuityAmnt",
					request).trim()) : null;
			gLogger.info("lBigDcmlGratuityAmnt : " + lBigDcmlGratuityAmnt);

			lStrEnquiryProposedFlag = ((StringUtility.getParameter("radioProposed", request).trim()).length() > 0) ? StringUtility.getParameter("radioProposed", request).trim()
					.toUpperCase() : null;

			lStrEnquiryPendingFlag = (StringUtility.getParameter("radioPending", request).trim().length() > 0) ? (StringUtility.getParameter("radioPending", request).trim())
					: null;
			gLogger.info("lStrEnquiryPendingFlag : " + lStrEnquiryPendingFlag);

			/*lDtInvalidatingDate = ((StringUtility.getParameter(
					"txtDateOfMedCert", request).trim()).length() > 0) ? simpleDateFormat
					.parse(StringUtility.getParameter("txtDateOfMedCert",
							request).trim()): null;
			gLogger.info("lDtInvalidatingDate :" + lDtInvalidatingDate);

			lDtLodgingDate = ((StringUtility.getParameter("txtDateOfLodgFir",
					request).trim()).length() > 0) ? simpleDateFormat
					.parse(StringUtility.getParameter("txtDateOfLodgFir",
							request).trim()) : null;
			gLogger.info("lDtlodgingDate:" + lDtLodgingDate);
			
			lIntPoliceCertificateNo = (StringUtility.getParameter(
					"txtNoOfCertificate", request).trim().length() > 0) ? (Integer
					.parseInt(StringUtility.getParameter("txtNoOfCertificate", request)
							.trim())) : 0;

			gLogger.info("lIntPoliceCertificateNo  : "+ lIntPoliceCertificateNo);

			lStrPoliceStationName = (StringUtility.getParameter(
					"txtPoliceStnName", request).trim().length() > 0) ? (StringUtility
					.getParameter("txtPoliceStnName", request).trim())
					: null;
			gLogger.info("lStrpoliceStationName  : " + lStrPoliceStationName);

			

			lDtPoliceCertificateDate = ((StringUtility.getParameter(
					"txtDateOfCert", request).trim()).length() > 0) ? simpleDateFormat
					.parse(StringUtility.getParameter("txtDateOfCert", request)
							.trim())
					: null;
			gLogger
					.info("lDtpoliceCertificateDate:"+ lDtPoliceCertificateDate);
			*/

			lObjTrnPnsnProcRecoveryVO.setLocationCode(Long.parseLong(gStrLocationCode));
			lObjTrnPnsnProcRecoveryVO.setAdvanceFlag(lStrAdvnceBalFlag);
			lObjTrnPnsnProcRecoveryVO.setTotalAdvanceAmount(lBigDcmlTotalAdvanceAmount);
			lObjTrnPnsnProcRecoveryVO.setOverPaymentAmnt(lBigDcmlOverPaymentAmnt);
			lObjTrnPnsnProcRecoveryVO.setArrearsAmnt(lBigDcmlArrearsAmnt);
			lObjTrnPnsnProcRecoveryVO.setGovtAcmdtnRetAmnt(lBigDcmlGovtAcmdtnRetAmnt);
			lObjTrnPnsnProcRecoveryVO.setAssessedDuesdtlFlag(lStrAssessedDuesdtlFlag);
			lObjTrnPnsnProcRecoveryVO.setTotalAssessedDues(lBigDcmlTotalAssessedDues);
			lObjTrnPnsnProcRecoveryVO.setGratuityAmnt(lBigDcmlGratuityAmnt);
			lObjTrnPnsnProcRecoveryVO.setEnquiryProposedFlag(lStrEnquiryProposedFlag);
			lObjTrnPnsnProcRecoveryVO.setEnquiryPendingFlag(lStrEnquiryPendingFlag);
			/*	lObjTrnPnsnProcRecoveryVO.setPoliceCertificateNo(null);
				lObjTrnPnsnProcRecoveryVO.setPoliceStationName(null);
				lObjTrnPnsnProcRecoveryVO.setInvalidatingDate(null);
				lObjTrnPnsnProcRecoveryVO.setLodgingDate(null);
				lObjTrnPnsnProcRecoveryVO.setPoliceCertificateDate(null);*/
			lObjTrnPnsnProcRecoveryVO.setUpdatedDate(lDtCurDate);
			if ("Add".equalsIgnoreCase(lStrMode)) {
				lObjTrnPnsnProcRecoveryVO.setCreatedUserId(gLngUserId);
				lObjTrnPnsnProcRecoveryVO.setCreatedPostId(gLngPostId);
				lObjTrnPnsnProcRecoveryVO.setCreatedDate(lDtCurDate);
			}
			if ("Update".equalsIgnoreCase(lStrMode)) {
				lObjTrnPnsnProcRecoveryVO.setUpdatedDate(lDtCurDate);
				lObjTrnPnsnProcRecoveryVO.setUpdatedPostId(gLngPostId);
				lObjTrnPnsnProcRecoveryVO.setUpdatedUserId(gLngUserId);
			}
			lObjTrnPnsnProcRecoveryVO.setDbId(gLngDBId);
		}

		catch (Exception e) {

			gLogger.error("Error in generateTrnPnsnProcRecoveryVO method is :" + e, e);

		}
		return lObjTrnPnsnProcRecoveryVO;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionproc.service.PensionCaseVOGenerator#generateTrnPnsnProcAdvnceBalVO(java.util.Map)
	 */

	public List<TrnPnsnProcAdvnceBal> generateTrnPnsnProcAdvnceBalVO(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPnsnProcAdvnceBal lObjTrnPnsnProcAdvnceBalVO = null;
		List<TrnPnsnProcAdvnceBal> lLstTrnPnsnProcAdvnceBalVO = new ArrayList<TrnPnsnProcAdvnceBal>();
		try {
			setSessionInfo(inputMap);
			Date lDtcurDate = SessionHelper.getCurDate();
			String lStrMode = null;

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			String[] lArrStrAdcnceBalTypeId = StringUtility.getParameterValues("cmbTypeOfAdv", request);
			String[] lArrStrAdvanceAmnt = StringUtility.getParameterValues("txtAdvAmount", request);
			String[] lArrStrAdvSchemeCode = StringUtility.getParameterValues("txtAdvSchemeCode", request);

			if (lArrStrAdcnceBalTypeId.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrAdcnceBalTypeId.length; lIntCnt++) {
					lObjTrnPnsnProcAdvnceBalVO = new TrnPnsnProcAdvnceBal();

					lObjTrnPnsnProcAdvnceBalVO.setLocationCode(Long.parseLong(gStrLocationCode));
					lObjTrnPnsnProcAdvnceBalVO.setCreatedUserId(gLngUserId);
					lObjTrnPnsnProcAdvnceBalVO.setCreatedPostId(gLngPostId);
					lObjTrnPnsnProcAdvnceBalVO.setCreatedDate(lDtcurDate);
					lObjTrnPnsnProcAdvnceBalVO.setUpdatedDate(lDtcurDate);
					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjTrnPnsnProcAdvnceBalVO.setUpdatedDate(lDtcurDate);
						lObjTrnPnsnProcAdvnceBalVO.setUpdatedPostId(gLngPostId);
						lObjTrnPnsnProcAdvnceBalVO.setUpdatedUserId(gLngUserId);
					}

					if (lArrStrAdcnceBalTypeId[lIntCnt] != null && lArrStrAdcnceBalTypeId[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnProcAdvnceBalVO.setAdvanceBalTypeId(lArrStrAdcnceBalTypeId[lIntCnt].trim());
					}
					if (lArrStrAdvanceAmnt[lIntCnt] != null && lArrStrAdvanceAmnt[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnProcAdvnceBalVO.setAdvanceAmnt(new BigDecimal(lArrStrAdvanceAmnt[lIntCnt].trim()));
					}
					if (lArrStrAdvSchemeCode[lIntCnt] != null && lArrStrAdvSchemeCode[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnProcAdvnceBalVO.setAdvanceSchemeCode(lArrStrAdvSchemeCode[lIntCnt]);
					}

					lObjTrnPnsnProcAdvnceBalVO.setDbId(gLngDBId);
					lLstTrnPnsnProcAdvnceBalVO.add(lObjTrnPnsnProcAdvnceBalVO);
				}
			}
		} catch (Exception e) {

			gLogger.error("Error in generateTrnPnsnProcAdvnceBalVO method is :" + e, e);
		}
		return lLstTrnPnsnProcAdvnceBalVO;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionproc.service.PensionCaseVOGenerator#generateTrnPnsnProcAssesdDuesVO(java.util.Map)
	 */

	public List<TrnPnsnProcAssesdDues> generateTrnPnsnProcAssesdDuesVO(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		new SimpleDateFormat("dd/MM/yyyy");
		TrnPnsnProcAssesdDues lObjTrnPnsnProcAssesdDuesVO = null;
		List<TrnPnsnProcAssesdDues> lLstTrnPnsnProcAssesdDuesVO = new ArrayList<TrnPnsnProcAssesdDues>();

		try {
			setSessionInfo(inputMap);
			Date lDtcurDate = SessionHelper.getCurDate();
			String lStrMode = null;

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			String[] lArrStrAssesdDuesDtlTypeId = StringUtility.getParameterValues("cmbNature", request);
			String[] lArrStrAssesdDuesDtlAmnt = StringUtility.getParameterValues("txtNatrAmount", request);
			if (lArrStrAssesdDuesDtlTypeId.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrAssesdDuesDtlTypeId.length; lIntCnt++) {
					lObjTrnPnsnProcAssesdDuesVO = new TrnPnsnProcAssesdDues();

					lObjTrnPnsnProcAssesdDuesVO.setLocationCode(Long.parseLong(gStrLocationCode));

					lObjTrnPnsnProcAssesdDuesVO.setCreatedUserId(gLngUserId);
					lObjTrnPnsnProcAssesdDuesVO.setCreatedPostId(gLngPostId);
					lObjTrnPnsnProcAssesdDuesVO.setCreatedDate(lDtcurDate);
					lObjTrnPnsnProcAssesdDuesVO.setUpdatedDate(lDtcurDate);
					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjTrnPnsnProcAssesdDuesVO.setUpdatedDate(lDtcurDate);
						lObjTrnPnsnProcAssesdDuesVO.setUpdatedPostId(gLngPostId);
						lObjTrnPnsnProcAssesdDuesVO.setUpdatedUserId(gLngUserId);
					}

					if (lArrStrAssesdDuesDtlTypeId[lIntCnt] != null && lArrStrAssesdDuesDtlTypeId[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnProcAssesdDuesVO.setAssesdDuesDtlTypeId((lArrStrAssesdDuesDtlTypeId[lIntCnt].trim()));
					}
					if (lArrStrAssesdDuesDtlAmnt[lIntCnt] != null && lArrStrAssesdDuesDtlAmnt[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnProcAssesdDuesVO.setAssesdDuesDtlAmnt(new BigDecimal(lArrStrAssesdDuesDtlAmnt[lIntCnt].trim()));
					}
					lObjTrnPnsnProcAssesdDuesVO.setDbId(gLngDBId);
					lLstTrnPnsnProcAssesdDuesVO.add(lObjTrnPnsnProcAssesdDuesVO);
				}
			}
		} catch (Exception e) {

			gLogger.error("Error in generateTrnPnsnProcAssesdDuesVO method is :" + e, e);
		}
		return lLstTrnPnsnProcAssesdDuesVO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionproc.service.PensionCaseVOGenerator#
	 * generateTrnPnsnProcCheckListVO(java.util.Map)
	 */

	public List<TrnPnsnProcCheckList> generateTrnPnsnProcCheckListVO(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		TrnPnsnProcCheckList lObjTrnPnsnProcCheckListVO = null;
		List<TrnPnsnProcCheckList> lLstTrnPnsnProcCheckListVO = new ArrayList<TrnPnsnProcCheckList>();

		try {
			String lStrGvnmntAcmdtnFlag;
			String lStrNocFlag;
			Date lDtcurDate = SessionHelper.getCurDate();
			String lStrMode = null;
			setSessionInfo(inputMap);

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			String[] lArrStrOfficeTypeId = StringUtility.getParameterValues("cmbOfficeId", request);
			String[] lArrStrCertificateTypeId = StringUtility.getParameterValues("cmbCertId", request);
			//String[] lArrStrFromDate = StringUtility.getParameterValues("txtDateOfFrom", request);
			//String[] lArrStrToDate = StringUtility.getParameterValues("txtDateOfTo", request);

			lStrGvnmntAcmdtnFlag = ((StringUtility.getParameter("radioGovtAcmdn", request).trim()).length() > 0) ? StringUtility.getParameter("radioGovtAcmdn", request).trim()
					.toUpperCase() : null;
			lStrNocFlag = ((StringUtility.getParameter("radioNocObtained", request).trim()).length() > 0) ? StringUtility.getParameter("radioNocObtained", request).trim()
					.toUpperCase() : null;
			if (lArrStrOfficeTypeId.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrOfficeTypeId.length; lIntCnt++) {
					try {
						lObjTrnPnsnProcCheckListVO = new TrnPnsnProcCheckList();

						if (lArrStrOfficeTypeId[lIntCnt] != null && lArrStrOfficeTypeId[lIntCnt].trim().length() > 0) {
							lObjTrnPnsnProcCheckListVO.setOfficeName(lArrStrOfficeTypeId[lIntCnt].trim());
						}

						if (lArrStrCertificateTypeId[lIntCnt] != null && lArrStrCertificateTypeId[lIntCnt].trim().length() > 0) {
							lObjTrnPnsnProcCheckListVO.setCertificateTypeId(Long.parseLong(lArrStrCertificateTypeId[lIntCnt].trim()));
						}
						/*if (lArrStrFromDate[lIntCnt] != null && lArrStrFromDate[lIntCnt].trim().length() != 0) {
							lObjTrnPnsnProcCheckListVO.setFromDate(simpleDateFormat.parse(lArrStrFromDate[lIntCnt].trim()));
						}
						if (lArrStrToDate[lIntCnt] != null && lArrStrToDate[lIntCnt].trim().length() != 0) {
							lObjTrnPnsnProcCheckListVO.setToDate(simpleDateFormat.parse(lArrStrToDate[lIntCnt].trim()));
						}*/
						if (lStrGvnmntAcmdtnFlag == null) {
							lObjTrnPnsnProcCheckListVO.setGvnmntAcmdtnFlag('N');
						} else {
							lObjTrnPnsnProcCheckListVO.setGvnmntAcmdtnFlag(lStrGvnmntAcmdtnFlag.charAt(0));
						}
						if (lStrNocFlag == null) {
							lObjTrnPnsnProcCheckListVO.setNocFlag('N');
						} else {
							lObjTrnPnsnProcCheckListVO.setNocFlag(lStrNocFlag.charAt(0));
						}

						lObjTrnPnsnProcCheckListVO.setLocationCode(Long.parseLong(gStrLocationCode));
						lObjTrnPnsnProcCheckListVO.setGvnmntAcmdtnFlag(StringUtility.convertToChar(lStrGvnmntAcmdtnFlag));
						lObjTrnPnsnProcCheckListVO.setNocFlag(StringUtility.convertToChar(lStrNocFlag));
						lObjTrnPnsnProcCheckListVO.setCreatedUserId(gLngUserId);
						lObjTrnPnsnProcCheckListVO.setCreatedPostId(gLngPostId);
						lObjTrnPnsnProcCheckListVO.setCreatedDate(lDtcurDate);
						lObjTrnPnsnProcCheckListVO.setUpdatedDate(lDtcurDate);
						if ("Update".equalsIgnoreCase(lStrMode)) {
							lObjTrnPnsnProcCheckListVO.setUpdatedDate(lDtcurDate);
							lObjTrnPnsnProcCheckListVO.setUpdatedPostId(gLngPostId);
							lObjTrnPnsnProcCheckListVO.setUpdatedUserId(gLngUserId);
						}
						lObjTrnPnsnProcCheckListVO.setDbId(gLngDBId);
						lLstTrnPnsnProcCheckListVO.add(lObjTrnPnsnProcCheckListVO);
					} catch (Exception e) {
						gLogger.info(" Exception while Grid Insertion......." + e, e);

					}
				}
			}

		} catch (Exception e) {

			gLogger.error("Error in generateTrnPnsnProcCheckListVO method is :" + e, e);
		}

		return lLstTrnPnsnProcCheckListVO;
	}

	public List<TrnPnsnprocFamilydtls> generateTrnPnsnProcPnsnFamilydtlsVO(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPnsnprocFamilydtls lObjTrnPnsnprocFamilydtlsVO = null;
		List<TrnPnsnprocFamilydtls> lLstTrnPnsnprocFamilydtlsVO = new ArrayList<TrnPnsnprocFamilydtls>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		gLogger.info("In generateTrnPnsnProcPnsnFamilydtlsVO of PensionCaseVOGeneratorImpl........");

		try {
			Date lDtcurDate = SessionHelper.getCurDate();
			String lStrMode = null;

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			setSessionInfo(inputMap);
			String[] lArrStrFmlyMemName = StringUtility.getParameterValues("txtNameOfFamilyMember", request);
			String[] lArrStrFmlyMemRelationshipLookupName = StringUtility.getParameterValues("cmbFmlyMemRelation", request);
			String[] lArrStrFmlyMemBirthDate = StringUtility.getParameterValues("txtFmlyMemDateOfBirth", request);
			//String[] lArrStrDateOfDeath = StringUtility.getParameterValues("txtFMDateOfDeath", request);
			String[] lArrStrMinorFlag = StringUtility.getParameterValues("chkMinorVar", request);
			//String[] lArrStrSalary = StringUtility.getParameterValues("txtFMSalary", request);
			String[] lArrStrHandicapeFlag = StringUtility.getParameterValues("cmbPhyHandicap", request);
			String[] lArrStrGuardianName = StringUtility.getParameterValues("txtFMGuardianName", request);
			//String[] lArrStrPercentage = StringUtility.getParameterValues("txtPercentage", request);
			String[] lArrStrMarriedFlag = StringUtility.getParameterValues("chkMarriedVar", request);
			StringUtility.getParameterValues("hdnFamilyMemberId", request);

			if (lArrStrFmlyMemName.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrFmlyMemName.length; lIntCnt++) {
					lObjTrnPnsnprocFamilydtlsVO = new TrnPnsnprocFamilydtls();

					lObjTrnPnsnprocFamilydtlsVO.setLocationCode(Long.parseLong(gStrLocationCode));
					lObjTrnPnsnprocFamilydtlsVO.setCreatedUserId(gLngUserId);
					lObjTrnPnsnprocFamilydtlsVO.setCreatedPostId(gLngPostId);
					lObjTrnPnsnprocFamilydtlsVO.setCreatedDate(lDtcurDate);

					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjTrnPnsnprocFamilydtlsVO.setUpdatedDate(lDtcurDate);
						lObjTrnPnsnprocFamilydtlsVO.setUpdatedPostId(gLngPostId);
						lObjTrnPnsnprocFamilydtlsVO.setUpdatedUserId(gLngUserId);
					}

					if (lArrStrFmlyMemName[lIntCnt] != null && lArrStrFmlyMemName[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocFamilydtlsVO.setName(lArrStrFmlyMemName[lIntCnt].trim().toUpperCase());
					}

					if (lArrStrFmlyMemRelationshipLookupName[lIntCnt] != null && lArrStrFmlyMemRelationshipLookupName[lIntCnt] != "-1") {
						lObjTrnPnsnprocFamilydtlsVO.setRelation(lArrStrFmlyMemRelationshipLookupName[lIntCnt].trim());
					}

					if (lArrStrFmlyMemBirthDate[lIntCnt] != null && lArrStrFmlyMemBirthDate[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocFamilydtlsVO.setBirthDate(simpleDateFormat.parse(lArrStrFmlyMemBirthDate[lIntCnt].trim()));
					}

					/*if (lArrStrDateOfDeath[lIntCnt] != null && lArrStrDateOfDeath[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocFamilydtlsVO.setDeathDate(simpleDateFormat.parse(lArrStrDateOfDeath[lIntCnt].trim()));
					}*/

					if (lArrStrMinorFlag[lIntCnt] != null && lArrStrMinorFlag[lIntCnt].length() > 0) {
						lObjTrnPnsnprocFamilydtlsVO.setMinorFlag("Y");
					} else {
						lObjTrnPnsnprocFamilydtlsVO.setMinorFlag("N");
					}

					/*if (lArrStrSalary[lIntCnt] != null && lArrStrSalary[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocFamilydtlsVO.setSalary(new BigDecimal(lArrStrSalary[lIntCnt].trim()));

					}*/
					if (!lArrStrHandicapeFlag[lIntCnt].equalsIgnoreCase("-1")) {
						lObjTrnPnsnprocFamilydtlsVO.setHandicapeFlag(lArrStrHandicapeFlag[lIntCnt].trim());
					} else {
						lObjTrnPnsnprocFamilydtlsVO.setHandicapeFlag("N");
					}

					if (lArrStrGuardianName[lIntCnt] != null && lArrStrGuardianName[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocFamilydtlsVO.setGuardianName(lArrStrGuardianName[lIntCnt].trim());
					}
					/*if (lArrStrPercentage[lIntCnt] != null && lArrStrPercentage[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocFamilydtlsVO.setPercentage(Long.parseLong(lArrStrPercentage[lIntCnt].trim()));

					}*/
					if (lArrStrMarriedFlag[lIntCnt] != null && lArrStrMarriedFlag[lIntCnt].length() > 0) {
						lObjTrnPnsnprocFamilydtlsVO.setMarriedFlag("Y");
					} else {
						lObjTrnPnsnprocFamilydtlsVO.setMarriedFlag("N");
					}

					lObjTrnPnsnprocFamilydtlsVO.setDbId(gLngDBId);
					lLstTrnPnsnprocFamilydtlsVO.add(lObjTrnPnsnprocFamilydtlsVO);
				}

			}

		} catch (Exception e) {

			gLogger.info(" Exception while family Grid Insertion........................." + e, e);
		}

		return lLstTrnPnsnprocFamilydtlsVO;
	}

	public List<TrnPnsnprocNomineedtls> generateTrnPnsnProcPnsnNomineedtlsVO(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPnsnprocNomineedtls lObjTrnPnsnprocNomineedtlsVO = null;
		List<TrnPnsnprocNomineedtls> lLstTrnPnsnprocNomineedtlsVO = new ArrayList<TrnPnsnprocNomineedtls>();
		gLogger.info("In generateTrnPnsnProcPnsnFamilydtlsVO of PensionCaseVOGeneratorImpl........");

		try {
			setSessionInfo(inputMap);
			Date lDtcurDate = SessionHelper.getCurDate();
			String lStrMode = null;

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			String[] lArrStrNomName = StringUtility.getParameterValues("txtNameOfNominee", request);
			String[] lArrStrPercent = StringUtility.getParameterValues("txtNomPercentage", request);
			String[] lArrStrBankCode = StringUtility.getParameterValues("cmbNomBank", request);
			String[] lArrStrBranchCode = StringUtility.getParameterValues("cmbNomBankBranch", request);
			String[] lArrStrAccountNo = StringUtility.getParameterValues("txtNomAccountNo", request);
			String[] lArrStrNomineeMemRelationshipLookupName = StringUtility.getParameterValues("cmbNomineeMemRelation", request);
			

			if (lArrStrNomName.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrNomName.length; lIntCnt++) {
					lObjTrnPnsnprocNomineedtlsVO = new TrnPnsnprocNomineedtls();

					lObjTrnPnsnprocNomineedtlsVO.setLocationCode(Long.parseLong(gStrLocationCode));
					lObjTrnPnsnprocNomineedtlsVO.setCreatedUserId(gLngUserId);
					lObjTrnPnsnprocNomineedtlsVO.setCreatedPostId(gLngPostId);
					lObjTrnPnsnprocNomineedtlsVO.setCreatedDate(lDtcurDate);					
					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjTrnPnsnprocNomineedtlsVO.setUpdatedDate(lDtcurDate);
						lObjTrnPnsnprocNomineedtlsVO.setUpdatedPostId(gLngPostId);
						lObjTrnPnsnprocNomineedtlsVO.setUpdatedUserId(gLngUserId);
					}

					if (lArrStrNomName[lIntCnt] != null && lArrStrNomName[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocNomineedtlsVO.setName(lArrStrNomName[lIntCnt].trim().toUpperCase());
					}
					
					if (lArrStrNomineeMemRelationshipLookupName[lIntCnt] != null && lArrStrNomineeMemRelationshipLookupName[lIntCnt] != "-1") {
						lObjTrnPnsnprocNomineedtlsVO.setRelation(lArrStrNomineeMemRelationshipLookupName[lIntCnt].trim());
					}
					
					if (lArrStrPercent[lIntCnt] != null && lArrStrPercent[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocNomineedtlsVO.setPercentage(new BigDecimal(lArrStrPercent[lIntCnt].trim()));
					}
					if (!lArrStrBankCode[lIntCnt].equalsIgnoreCase("-1") && !"".equals(lArrStrBankCode[lIntCnt])) {
						lObjTrnPnsnprocNomineedtlsVO.setBankCode(lArrStrBankCode[lIntCnt].trim());
					}
					if (!lArrStrBranchCode[lIntCnt].equalsIgnoreCase("-1") && !"".equals(lArrStrBranchCode[lIntCnt])) {
						lObjTrnPnsnprocNomineedtlsVO.setBranchCode(lArrStrBranchCode[lIntCnt].trim());
					}

					if (lArrStrAccountNo[lIntCnt] != null && lArrStrAccountNo[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocNomineedtlsVO.setAccountNo(lArrStrAccountNo[lIntCnt].trim());
					}

					lObjTrnPnsnprocNomineedtlsVO.setDbId(gLngDBId);
					lLstTrnPnsnprocNomineedtlsVO.add(lObjTrnPnsnprocNomineedtlsVO);

				}
			}

		} catch (Exception e) {

			gLogger.info(" Exception while Nominee Grid Insertion........................." + e, e);
		}

		return lLstTrnPnsnprocNomineedtlsVO;

	}

	public List<TrnPnsnprocProvisionalPaid> generateTrnPnsnprocProvisionalPaid(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		TrnPnsnprocProvisionalPaid lObjTrnPnsnprocProvisionalPaidVO = null;
		List<TrnPnsnprocProvisionalPaid> lLstTrnPnsnprocProvisionalPaid = new ArrayList<TrnPnsnprocProvisionalPaid>();

		try {

			setSessionInfo(inputMap);
			String lStrTempMonth;
			String lStrForMonthYear;
			String lStrToMonthYear;
			
			Date lDtcurDate = SessionHelper.getCurDate();
			String lStrMode = null;
			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			String[] lArrStrSancAuthName = StringUtility.getParameterValues("cmbSancAuthName", request);
			String[] lArrStrSancAuthNo = StringUtility.getParameterValues("txtSancAuthNoPro", request);
			String[] lArrStrSancDate = StringUtility.getParameterValues("txtSanctionedDate", request);
			
			String[] lArrStrPrvsnlPnsnFormDate = StringUtility.getParameterValues("txtApplnFromDate", request);
			String[] lArrStrPrvsnlPnsnToDate = StringUtility.getParameterValues("txtApplnToDate", request);
			
			String[] lArrStrPrvsnlPnsnBillType = StringUtility.getParameterValues("cmbBillType", request);
			String[] lArrStrPrvsnlPnsnAmount = StringUtility.getParameterValues("txtPaidAmount", request);

			String[] lArrStrVoucherNo = StringUtility.getParameterValues("txtProVoucherNo", request);
			String[] lArrStrVoucherDate = StringUtility.getParameterValues("txtProVoucherDate", request);
			
			if (lArrStrSancAuthName.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrSancAuthName.length; lIntCnt++) {
					lObjTrnPnsnprocProvisionalPaidVO = new TrnPnsnprocProvisionalPaid();
					if (lArrStrSancAuthName[lIntCnt] != null && lArrStrSancAuthName[lIntCnt] != "-1") {
						lObjTrnPnsnprocProvisionalPaidVO.setProvPensionSanctionAuthority(lArrStrSancAuthName[lIntCnt]);
					}
					if (lArrStrSancAuthNo[lIntCnt] != null && lArrStrSancAuthNo[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocProvisionalPaidVO.setProvPensionAuthorityNo(lArrStrSancAuthNo[lIntCnt]);				
					}
					if (lArrStrSancDate[lIntCnt] != null && lArrStrSancDate[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocProvisionalPaidVO.setProvPensionAuthorityDate(simpleDateFormat.parse(lArrStrSancDate[lIntCnt]));			
					}
					if (lArrStrPrvsnlPnsnFormDate[lIntCnt] != null && lArrStrPrvsnlPnsnFormDate[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocProvisionalPaidVO.setAppFromDate(simpleDateFormat.parse(lArrStrPrvsnlPnsnFormDate[lIntCnt]));			
					}
					if (lArrStrPrvsnlPnsnToDate[lIntCnt] != null && lArrStrPrvsnlPnsnToDate[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocProvisionalPaidVO.setAppToDate(simpleDateFormat.parse(lArrStrPrvsnlPnsnToDate[lIntCnt]));			
					}
					if (lArrStrPrvsnlPnsnBillType[lIntCnt] != "-1" && lArrStrPrvsnlPnsnBillType[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocProvisionalPaidVO.setBillType(lArrStrPrvsnlPnsnBillType[lIntCnt]);				
					}
					if (lArrStrPrvsnlPnsnAmount[lIntCnt] != null && lArrStrPrvsnlPnsnAmount[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocProvisionalPaidVO.setBasicPensionAmount(Long.parseLong(lArrStrPrvsnlPnsnAmount[lIntCnt]));				
					}
					if (lArrStrVoucherNo[lIntCnt] != null && lArrStrVoucherNo[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocProvisionalPaidVO.setVoucherNo(lArrStrVoucherNo[lIntCnt]);				
					}
					if (lArrStrVoucherDate[lIntCnt] != null && lArrStrVoucherDate[lIntCnt].trim().length() > 0) {
						lObjTrnPnsnprocProvisionalPaidVO.setVoucherDate(simpleDateFormat.parse(lArrStrVoucherDate[lIntCnt]));			
					}
					
					lObjTrnPnsnprocProvisionalPaidVO.setLocationCode(gStrLocationCode);
					lObjTrnPnsnprocProvisionalPaidVO.setCreatedUserId(gLngUserId);
					lObjTrnPnsnprocProvisionalPaidVO.setCreatedPostId(gLngPostId);
					lObjTrnPnsnprocProvisionalPaidVO.setCreatedDate(lDtcurDate);					
					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjTrnPnsnprocProvisionalPaidVO.setUpdatedDate(lDtcurDate);
						lObjTrnPnsnprocProvisionalPaidVO.setUpdatedPostId(gLngPostId);
						lObjTrnPnsnprocProvisionalPaidVO.setUpdatedUserId(gLngUserId);
					}
					
					lLstTrnPnsnprocProvisionalPaid.add(lObjTrnPnsnprocProvisionalPaidVO);

				}
			}

		} catch (Exception e) {		
			gLogger.error("Error in generateTrnPnsnprocProvisionalPaid method is :" + e, e);
		}

		return lLstTrnPnsnprocProvisionalPaid;
	}
	public List<TrnPnsnprocForeignServ> generateTrnPnsnprocForeignServ(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		TrnPnsnprocForeignServ lObTrnPnsnprocForeignServVO = null;
		List<TrnPnsnprocForeignServ> lLstTrnPnsnprocForeignServ = new ArrayList<TrnPnsnprocForeignServ>();

		try {

			setSessionInfo(inputMap);			
			Date lDtcurDate = SessionHelper.getCurDate();
			
			String lStrMode = null;
			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}		
			String[] lArrStrToDate = StringUtility.getParameterValues("txtToDateForeignServ", request);
			
			String[] lArrStrFromDate = StringUtility.getParameterValues("txtFromDateForeignServ", request);
			
			String[] lArrStrTotalAmt = StringUtility.getParameterValues("txtTotalAmt", request);
			
			String[] lArrStrPaidAmt = StringUtility.getParameterValues("txtPaidAmt", request);

			String[] lArrStrChallanNo = StringUtility.getParameterValues("txtChallanNo", request);
			
			String[] lArrStrChallanDate = StringUtility.getParameterValues("txtChallanDate", request);
			
			String[] lArrStrDeptOffName = StringUtility.getParameterValues("txtDeptOffName", request);
			
			if (lArrStrToDate.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrToDate.length; lIntCnt++) {
					lObTrnPnsnprocForeignServVO = new TrnPnsnprocForeignServ();
					
					if (lArrStrToDate[lIntCnt] != null && lArrStrToDate[lIntCnt].trim().length() > 0) {
						lObTrnPnsnprocForeignServVO.setToDate(simpleDateFormat.parse(lArrStrToDate[lIntCnt]));			
					}
					if (lArrStrFromDate[lIntCnt] != null && lArrStrFromDate[lIntCnt].trim().length() > 0) {
						lObTrnPnsnprocForeignServVO.setFromDate(simpleDateFormat.parse(lArrStrFromDate[lIntCnt]));			
					}
					if (lArrStrTotalAmt[lIntCnt] != null && lArrStrTotalAmt[lIntCnt].trim().length() > 0) {
						lObTrnPnsnprocForeignServVO.setTotalAmt(Long.parseLong(lArrStrTotalAmt[lIntCnt].trim()));				
					}
					if (lArrStrPaidAmt[lIntCnt] != null && lArrStrPaidAmt[lIntCnt].trim().length() > 0) {
						lObTrnPnsnprocForeignServVO.setPaidAmt(Long.parseLong(lArrStrPaidAmt[lIntCnt]));			
					}
					if (lArrStrChallanNo[lIntCnt] != null && lArrStrChallanNo[lIntCnt].trim().length() > 0) {
						lObTrnPnsnprocForeignServVO.setChallanNo(lArrStrChallanNo[lIntCnt]);			
					}
					if (lArrStrChallanDate[lIntCnt] != null && lArrStrChallanDate[lIntCnt].trim().length() > 0) {
						lObTrnPnsnprocForeignServVO.setChallanDate(simpleDateFormat.parse(lArrStrChallanDate[lIntCnt].trim()));				
					}
					if (lArrStrDeptOffName[lIntCnt] != null && lArrStrDeptOffName[lIntCnt].trim().length() > 0) {
						lObTrnPnsnprocForeignServVO.setDeptOffName(lArrStrDeptOffName[lIntCnt].trim());				
					}
					
					
					lObTrnPnsnprocForeignServVO.setLocationCode(gStrLocationCode);
					lObTrnPnsnprocForeignServVO.setCreatedUserId(gLngUserId);
					lObTrnPnsnprocForeignServVO.setCreatedPostId(gLngPostId);
					lObTrnPnsnprocForeignServVO.setCreatedDate(lDtcurDate);					
					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObTrnPnsnprocForeignServVO.setUpdatedDate(lDtcurDate);
						lObTrnPnsnprocForeignServVO.setUpdatedPostId(gLngPostId);
						lObTrnPnsnprocForeignServVO.setUpdatedUserId(gLngUserId);
					}
					
					lLstTrnPnsnprocForeignServ.add(lObTrnPnsnprocForeignServVO);

				}
			}

		} catch (Exception e) {		
			gLogger.error("Error in generateTrnPnsnprocForeignServ method is :" + e, e);
		}

		return lLstTrnPnsnprocForeignServ;
	}
	
	public TrnPnsnProcRevision generateTrnPnsnprocRevision(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPnsnProcRevision lObjTrnPnsnProcRevisionVO = null;
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		if (inputMap.containsKey("lObjTrnPnsnProcRevisionVO")) {
			lObjTrnPnsnProcRevisionVO = (TrnPnsnProcRevision) inputMap.get("lObjTrnPnsnProcRevisionVO");
		} else {
			lObjTrnPnsnProcRevisionVO = new TrnPnsnProcRevision();
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		gLogger.info("In generateTrnPnsnProcPnsnCalcVO of PensionCaseVOGeneratorImpl........");

		TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class, serv.getSessionFactory());
		
		try {
			setSessionInfo(inputMap);
			BigDecimal lBigDcmlCvpMonthAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlCvpAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlPensionerReducedAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlPensionerTotalAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlSrvcGratuityAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlDcrgAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlFp1Amnt = BigDecimal.ZERO;			
			BigDecimal lBigDcmlFp2Amnt = BigDecimal.ZERO;
			
			BigDecimal lBigDcmlDiffCvpMonthAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlDiffCvpAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlDiffPensionerReducedAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlDiffPensionerTotalAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlDiffSrvcGratuityAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlDiffDcrgAmnt = BigDecimal.ZERO;
			BigDecimal lBigDcmlDiffFp1Amnt = BigDecimal.ZERO;			
			BigDecimal lBigDcmlDiffFp2Amnt = BigDecimal.ZERO;
			
			Date lDtcurDate = SessionHelper.getCurDate();
			String lStrMode = "";			
			String lStrSevaarthId = "";
			String lStrPpoNo = "";
			Long lLngRevisionCount = null;

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			lBigDcmlCvpMonthAmnt = (StringUtility.getParameter("txtNewMonthAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtNewMonthAmt", request)
					.trim()) : BigDecimal.ZERO;
			
			lBigDcmlDiffCvpMonthAmnt = (StringUtility.getParameter("txtDiffMonthAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtDiffMonthAmt", request)
					.trim()) : BigDecimal.ZERO;

			lBigDcmlCvpAmnt = (StringUtility.getParameter("txtNewCVPAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtNewCVPAmt", request).trim())
					: BigDecimal.ZERO;
			
			lBigDcmlDiffCvpAmnt = (StringUtility.getParameter("txtDiffCVPAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtDiffCVPAmt", request).trim())
			: BigDecimal.ZERO;

			lBigDcmlPensionerTotalAmnt = (StringUtility.getParameter("txtNewTotPnsnAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtNewTotPnsnAmt",
					request).trim()) : BigDecimal.ZERO;
			
			lBigDcmlDiffPensionerTotalAmnt = (StringUtility.getParameter("txtDiffTotPnsnAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtDiffTotPnsnAmt",
					request).trim()) : BigDecimal.ZERO;
		
			lBigDcmlPensionerReducedAmnt = (StringUtility.getParameter("txtNewRedsPnsnAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter(
					"txtNewRedsPnsnAmt", request).trim()) : BigDecimal.ZERO;
			
			lBigDcmlDiffPensionerReducedAmnt = (StringUtility.getParameter("txtDiffRedsPnsnAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter(
					"txtDiffRedsPnsnAmt", request).trim()) : BigDecimal.ZERO;

			lBigDcmlSrvcGratuityAmnt = (StringUtility.getParameter("txtNewServGratyAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtNewServGratyAmt",
					request).trim()) : BigDecimal.ZERO;
			
			lBigDcmlDiffSrvcGratuityAmnt = (StringUtility.getParameter("txtDiffServGratyAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtDiffServGratyAmt",
					request).trim()) : BigDecimal.ZERO;
		
			lBigDcmlDcrgAmnt = (StringUtility.getParameter("txtNewTotDCRGAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtNewTotDCRGAmt", request)
					.trim()) : BigDecimal.ZERO;
			
			lBigDcmlDiffDcrgAmnt = (StringUtility.getParameter("txtDiffTotDCRGAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtDiffTotDCRGAmt", request)
					.trim()) : BigDecimal.ZERO;
			

			lBigDcmlFp1Amnt = (StringUtility.getParameter("txtNewFP1TotlAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtNewFP1TotlAmt", request)
					.trim()) : BigDecimal.ZERO;
		
			lBigDcmlDiffFp1Amnt = (StringUtility.getParameter("txtDiffFP1TotlAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtDiffFP1TotlAmt", request)
					.trim()) : BigDecimal.ZERO;

			
			lBigDcmlFp2Amnt = (StringUtility.getParameter("txtNewFP2TotlAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtNewFP2TotlAmt", request)
					.trim()) : BigDecimal.ZERO;
			
			lBigDcmlDiffFp2Amnt = (StringUtility.getParameter("txtDiffFP2TotlAmt", request).trim().length() > 0) ? new BigDecimal(StringUtility.getParameter("txtDiffFP2TotlAmt", request)
					.trim()) : BigDecimal.ZERO;
			
			lStrSevaarthId = ((StringUtility.getParameter("txtSevaarthId", request).trim()).length() > 0) ? StringUtility.getParameter("txtSevaarthId", request).toUpperCase().trim() : null;
			
			lLngRevisionCount = lObjTrnPnsnProcInwardPensionDAO.getRevisionCount(lStrSevaarthId);
			
			lStrPpoNo = StringUtility.getParameter("txtPpoNo", request).trim();
		
			lObjTrnPnsnProcRevisionVO.setLocationCode(Long.parseLong(gStrLocationCode));
			lObjTrnPnsnProcRevisionVO.setCvpMonthAmnt(lBigDcmlCvpMonthAmnt);
			lObjTrnPnsnProcRevisionVO.setCvpAmnt(lBigDcmlCvpAmnt);
			lObjTrnPnsnProcRevisionVO.setPensionerTotalAmnt(lBigDcmlPensionerTotalAmnt);
			lObjTrnPnsnProcRevisionVO.setPensionerReducedAmnt(lBigDcmlPensionerReducedAmnt);
			lObjTrnPnsnProcRevisionVO.setSrvcGratuityAmnt(lBigDcmlSrvcGratuityAmnt);
			lObjTrnPnsnProcRevisionVO.setDcrgAmnt(lBigDcmlDcrgAmnt);
			lObjTrnPnsnProcRevisionVO.setFp1Amnt(lBigDcmlFp1Amnt);			
			lObjTrnPnsnProcRevisionVO.setFp2Amnt(lBigDcmlFp2Amnt);
			
			lObjTrnPnsnProcRevisionVO.setDiffCvpMonthAmnt((lBigDcmlDiffCvpMonthAmnt.compareTo(BigDecimal.ZERO)<0)?BigDecimal.ZERO:lBigDcmlDiffCvpMonthAmnt);
			lObjTrnPnsnProcRevisionVO.setDiffCvpAmnt((lBigDcmlDiffCvpAmnt.compareTo(BigDecimal.ZERO)<0)?BigDecimal.ZERO:lBigDcmlDiffCvpAmnt);
			lObjTrnPnsnProcRevisionVO.setDiffPensionerTotalAmnt(((lBigDcmlDiffPensionerTotalAmnt.compareTo(BigDecimal.ZERO)<0)?BigDecimal.ZERO:lBigDcmlDiffPensionerTotalAmnt));			
			lObjTrnPnsnProcRevisionVO.setDiffPensionerReducedAmnt((lBigDcmlDiffPensionerReducedAmnt.compareTo(BigDecimal.ZERO)<0)?BigDecimal.ZERO:lBigDcmlDiffPensionerReducedAmnt);
			lObjTrnPnsnProcRevisionVO.setDiffSrvcGratuityAmnt((lBigDcmlDiffSrvcGratuityAmnt.compareTo(BigDecimal.ZERO)<0)?BigDecimal.ZERO:lBigDcmlDiffSrvcGratuityAmnt);			
			lObjTrnPnsnProcRevisionVO.setDiffDcrgAmnt(((lBigDcmlDiffDcrgAmnt.compareTo(BigDecimal.ZERO)<0)?BigDecimal.ZERO:lBigDcmlDiffDcrgAmnt));			
			lObjTrnPnsnProcRevisionVO.setDiffFp1Amnt((lBigDcmlDiffFp1Amnt.compareTo(BigDecimal.ZERO)<0)?BigDecimal.ZERO:lBigDcmlDiffFp1Amnt);			
			lObjTrnPnsnProcRevisionVO.setDiffFp2Amnt((lBigDcmlDiffFp2Amnt.compareTo(BigDecimal.ZERO)<0)?BigDecimal.ZERO:lBigDcmlDiffFp2Amnt);
			
			
			lObjTrnPnsnProcRevisionVO.setActiveFlag("Y");
			lObjTrnPnsnProcRevisionVO.setSevaarthId(lStrSevaarthId);
			lObjTrnPnsnProcRevisionVO.setPpoNo(lStrPpoNo);
			
			Long lLngRevisionId = (Long) inputMap.get("lLngRevisionId");
			
			if(lLngRevisionId == null || lLngRevisionId == 0l){
				lObjTrnPnsnProcRevisionVO.setRevisionNo(lLngRevisionCount);
				lObjTrnPnsnProcRevisionVO.setCreatedUserId(gLngUserId);
				lObjTrnPnsnProcRevisionVO.setCreatedPostId(gLngPostId);
				lObjTrnPnsnProcRevisionVO.setCreatedDate(lDtcurDate);
			}else{
				lObjTrnPnsnProcRevisionVO.setUpdatedDate(lDtcurDate);
				lObjTrnPnsnProcRevisionVO.setUpdatedPostId(gLngPostId);
				lObjTrnPnsnProcRevisionVO.setUpdatedUserId(gLngUserId);
			}


		}

		catch (Exception e) {

			gLogger.error("Error in generateTrnPnsnprocRevision method is :" + e, e);

		}
		return lObjTrnPnsnProcRevisionVO;
	}
	public List<TrnPnsnprocAuthorityDtls> generateTrnPnsnprocAuthoDtls(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		TrnPnsnprocAuthorityDtls lObTrnPnsnprocAuthorityDtls = null;
		List<TrnPnsnprocAuthorityDtls> lLstTrnPnsnprocAuthorityDtls = new ArrayList<TrnPnsnprocAuthorityDtls>();

		try {

			setSessionInfo(inputMap);			
			Date lDtcurDate = SessionHelper.getCurDate();
			
			String lStrMode = null;
			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}		
			String[] lArrStrTypeOfOrder = StringUtility.getParameterValues("cmbTypeOfOrder", request);
			
			String[] lArrStrOrderNo = StringUtility.getParameterValues("txtAuthoOrderNo", request);
			
			String[] lArrStrOrderDate = StringUtility.getParameterValues("txtAuthoOrderDate", request);
			
			String[] lArrStrPnsnrName = StringUtility.getParameterValues("txtAuthoPnsnrName", request);

			String[] lArrStrBasicAmt = StringUtility.getParameterValues("txtAuthoBasicAmt", request);
			
			String[] lArrStrEFP = StringUtility.getParameterValues("txtAuthoEFP", request);
			
			String[] lArrStrFP = StringUtility.getParameterValues("txtAuthoFP", request);
			
			if (lArrStrTypeOfOrder.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrTypeOfOrder.length; lIntCnt++) {
					lObTrnPnsnprocAuthorityDtls = new TrnPnsnprocAuthorityDtls();
					
					if (lArrStrTypeOfOrder[lIntCnt] != null && lArrStrTypeOfOrder[lIntCnt].trim().length() > 0) {
						lObTrnPnsnprocAuthorityDtls.setFlag(lArrStrTypeOfOrder[lIntCnt]);			
					}
					if (lArrStrOrderNo[lIntCnt] != null && lArrStrOrderNo[lIntCnt].trim().length() > 0) {
						lObTrnPnsnprocAuthorityDtls.setOrderNo(lArrStrOrderNo[lIntCnt]);			
					}
					if (lArrStrOrderDate[lIntCnt] != null && lArrStrOrderDate[lIntCnt].trim().length() > 0) {
						lObTrnPnsnprocAuthorityDtls.setOrderDate(simpleDateFormat.parse(lArrStrOrderDate[lIntCnt].trim()));				
					}
					if (lArrStrPnsnrName[lIntCnt] != null && lArrStrPnsnrName[lIntCnt].trim().length() > 0) {
						lObTrnPnsnprocAuthorityDtls.setPnsnrName(lArrStrPnsnrName[lIntCnt]);			
					}
					if (lArrStrBasicAmt[lIntCnt] != null && lArrStrBasicAmt[lIntCnt].trim().length() > 0) {
						lObTrnPnsnprocAuthorityDtls.setBasicAmt(Long.parseLong(lArrStrBasicAmt[lIntCnt]));			
					}
					if (lArrStrEFP[lIntCnt] != null && lArrStrEFP[lIntCnt].trim().length() > 0) {
						lObTrnPnsnprocAuthorityDtls.setEfp(Long.parseLong(lArrStrEFP[lIntCnt].trim()));				
					}
					if (lArrStrFP[lIntCnt] != null && lArrStrFP[lIntCnt].trim().length() > 0) {
						lObTrnPnsnprocAuthorityDtls.setFp(Long.parseLong(lArrStrFP[lIntCnt].trim()));				
					}
					
					lObTrnPnsnprocAuthorityDtls.setLocationCode(gStrLocationCode);
					lObTrnPnsnprocAuthorityDtls.setCreatedUserId(gLngUserId);
					lObTrnPnsnprocAuthorityDtls.setCreatedPostId(gLngPostId);
					lObTrnPnsnprocAuthorityDtls.setCreatedDate(lDtcurDate);					
					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObTrnPnsnprocAuthorityDtls.setUpdatedDate(lDtcurDate);
						lObTrnPnsnprocAuthorityDtls.setUpdatedPostId(gLngPostId);
						lObTrnPnsnprocAuthorityDtls.setUpdatedUserId(gLngUserId);
					}
					
					lLstTrnPnsnprocAuthorityDtls.add(lObTrnPnsnprocAuthorityDtls);

				}
			}

		} catch (Exception e) {		
			gLogger.error("Error in generateTrnPnsnprocForeignServ method is :" + e, e);
		}

		return lLstTrnPnsnprocAuthorityDtls;
	}
	
	public TrnPnsnprocAgDtls generateTrnPnsnprocAgDtls(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPnsnprocAgDtls lObjPnsnprocAgDtlsVO = null;
		if (inputMap.containsKey("lObjPnsnprocAgDtlsVO")) {
			lObjPnsnprocAgDtlsVO = (TrnPnsnprocAgDtls) inputMap.get("lObjPnsnprocAgDtlsVO");
		} else {
			lObjPnsnprocAgDtlsVO = new TrnPnsnprocAgDtls();
		}

		gLogger.info("In generateTrnPnsnprocAgDtls of PensionCaseVOGeneratorImpl........");

		try {
			setSessionInfo(inputMap);
			Date lDtCurDate = SessionHelper.getCurDate();
			
			String lStrMode = null;
			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}

			String lStrDepartmentName  = StringUtility.getParameter("txtAgDept", request);
			String lStrDesignationName  = StringUtility.getParameter("txtAgDesignationName", request);
			String lStrPayscale  = StringUtility.getParameter("txtAgPayScale", request);
			String lStrPayband  = StringUtility.getParameter("txtAgPayBand", request);
			String lStrPaycommission  = StringUtility.getParameter("txtAgPayComm", request);
			String lStrCategorySeries  = StringUtility.getParameter("txtAgCategory", request);
			String lStrGroupClass  = StringUtility.getParameter("txtAgGroup", request);
			String lStrSalutation = StringUtility.getParameter("txtAgSalutation", request);
			String lStrReligion = StringUtility.getParameter("txtAgReligion", request);
			String lStrState = StringUtility.getParameter("txtAgState", request);
			String lStrCity = StringUtility.getParameter("txtAgCity", request);
			String lStrRelation = StringUtility.getParameter("txtAgRelation", request);
			String lStrTreasury  = StringUtility.getParameter("txtAgTreasury", request);
			String lStrTransactionType   = StringUtility.getParameter("txtAgTrnsType", request);
			
			lObjPnsnprocAgDtlsVO.setDepartment(lStrDepartmentName);
			lObjPnsnprocAgDtlsVO.setDesignation(lStrDesignationName);
			lObjPnsnprocAgDtlsVO.setPayscale(lStrPayscale);
			lObjPnsnprocAgDtlsVO.setPayband(lStrPayband);
			lObjPnsnprocAgDtlsVO.setPayCommission(lStrPaycommission);
			lObjPnsnprocAgDtlsVO.setCategory(lStrCategorySeries);
			lObjPnsnprocAgDtlsVO.setGroupClass(lStrGroupClass);
			lObjPnsnprocAgDtlsVO.setSalutation(lStrSalutation);
			lObjPnsnprocAgDtlsVO.setReligion(lStrReligion);
			lObjPnsnprocAgDtlsVO.setState(lStrState);
			lObjPnsnprocAgDtlsVO.setCity(lStrCity);
			lObjPnsnprocAgDtlsVO.setRelation(lStrRelation);
			lObjPnsnprocAgDtlsVO.setReligion(lStrReligion);
			lObjPnsnprocAgDtlsVO.setTreasury(lStrTreasury);
			lObjPnsnprocAgDtlsVO.setTranType(lStrTransactionType);
			
			if ("Add".equalsIgnoreCase(lStrMode)) {
				lObjPnsnprocAgDtlsVO.setCreatedUserId(gLngUserId);
				lObjPnsnprocAgDtlsVO.setCreatedPostId(gLngPostId);
				lObjPnsnprocAgDtlsVO.setCreatedDate(lDtCurDate);
			}
			if ("Update".equalsIgnoreCase(lStrMode)) {
				lObjPnsnprocAgDtlsVO.setUpdatedDate(lDtCurDate);
				lObjPnsnprocAgDtlsVO.setUpdatedPostId(gLngPostId);
				lObjPnsnprocAgDtlsVO.setUpdatedUserId(gLngUserId);
			}
			
		}

		catch (Exception e) {

			gLogger.error("Error in generateTrnPnsnProcRecoveryVO method is :" + e, e);

		}
		return lObjPnsnprocAgDtlsVO;
	}
}
