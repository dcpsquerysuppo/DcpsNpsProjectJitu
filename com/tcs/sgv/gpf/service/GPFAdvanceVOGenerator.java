package com.tcs.sgv.gpf.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.FinancialYearDAO;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAO;
import com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstGpfAdvance;

/**
 * @author 397138
 * 
 */
public class GPFAdvanceVOGenerator extends ServiceImpl implements VOGeneratorService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.core.service.VOGeneratorService#generateMap(java.util.Map)
	 */
	private final Log gLogger = LogFactory.getLog(getClass());

	public ResultObject generateMap(Map inputMap) {

		// TODO Auto-generated method stub

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		MstGpfAdvance lObjMstGpfAdvance = null;
		try {
			lObjMstGpfAdvance = generateGpfAdvanceData(inputMap);
			inputMap.put("GPFAdvanceData", lObjMstGpfAdvance);

			objRes.setResultValue(inputMap);
		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, objRes, ex, "Error is: ");
		}

		return objRes;
	}

	/**
	 * @param inputMap
	 * @return
	 * @throws Exception
	 */
	private MstGpfAdvance generateGpfAdvanceData(Map inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		GPFAdvanceProcessDAO lObjAdvanceProcessDAO = new GPFAdvanceProcessDAOImpl(MstGpfAdvance.class, servLoc
				.getSessionFactory());
		MstGpfAdvance lObjMstGpfAdvance = null;

		Integer iSaveOrUpdateFlag = 0;

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);

		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		try {

			String lStrRequestName = StringUtility.getParameter("requestName", request);
			String lStrGpfAccNo = StringUtility.getParameter("txtGpfAccNo", request);
			String lStrClubFlag = StringUtility.getParameter("clubFlag", request);
			String lStrClubReqTrnId = StringUtility.getParameter("txtClubbedTrnId", request);
			String lStrMstGpfAdvanceId = null;
			String lStrSanctionedAmount = null;
			String lStrSancNoOfInstallments = null;
			String lStrPayableAmount = null;
			String lStrInstallmentAmount = null;
			String lStrOddInstallmentNo = null;
			String lStrOddInstallment = null;
			String lStrBalanceOutstanding = null;
			// String lStrTrnId = null;
			String lStrAdvanceAmt = null;
			String lStrAppDate = null;
			String lStrPurposeCat = null;
			String lStrOtherPurpose = null;
			String lStrSubType = null;
			String lStrSancInst = "";
			Integer lIntSancInst = null;
			FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(null, servLoc.getSessionFactory());
			Long lLngYearId = new Integer(lObjFinancialYearDAO.getFinYearIdByCurDate()).longValue();
			String lStrRemarks = StringUtility.getParameter("textareaRemarks", request);
			String lStrSpecialCase = StringUtility.getParameter("cbSpecialCase", request);
			String lStrSpecialCase90Percnt = StringUtility.getParameter("cbSpecialCaseAmt", request);

			String lStrInstallment1 = StringUtility.getParameter("txtInstallment1", request);
			String lStrInstallment2 = StringUtility.getParameter("txtInstallment2", request);
			String lStrInstallment3 = StringUtility.getParameter("txtInstallment3", request);
			String lStrInstallment4 = StringUtility.getParameter("txtInstallment4", request);
			String lStrReleaseDate1 = StringUtility.getParameter("txtReleaseDate1", request);
			String lStrReleaseDate2 = StringUtility.getParameter("txtReleaseDate2", request);
			String lStrReleaseDate3 = StringUtility.getParameter("txtReleaseDate3", request);
			String lStrReleaseDate4 = StringUtility.getParameter("txtReleaseDate4", request);

			if (lStrRequestName.equals("RA")) {
				lStrMstGpfAdvanceId = StringUtility.getParameter("hidPKValueRA", request);
				// lStrTrnId = StringUtility.getParameter("hidTIDRA", request);
				lStrAdvanceAmt = StringUtility.getParameter("txtAdvanceAmount", request);
				lStrAppDate = StringUtility.getParameter("txtAppDateRA", request);
				lStrPurposeCat = StringUtility.getParameter("cmblstPurposeCategory", request);
				lStrOtherPurpose = StringUtility.getParameter("textareaOther", request);
				lStrSanctionedAmount = StringUtility.getParameter("txtSancAmount", request);
				lStrSancNoOfInstallments = StringUtility.getParameter("txtSancInstallments", request);
				lStrPayableAmount = StringUtility.getParameter("txtPayableAmount", request);
				lStrInstallmentAmount = StringUtility.getParameter("txtInstallmentAmount", request);
				lStrOddInstallmentNo = StringUtility.getParameter("cmbOddInstallNo", request);
				lStrOddInstallment = StringUtility.getParameter("txtOddInstallmentAmt", request);
				lStrBalanceOutstanding = StringUtility.getParameter("txtOutstandingBalance", request);
			} else if (lStrRequestName.equals("NRA")) {
				lStrMstGpfAdvanceId = StringUtility.getParameter("hidPKValueNRA", request);
				// lStrTrnId = StringUtility.getParameter("hidTIDNRA", request);
				lStrAdvanceAmt = StringUtility.getParameter("txtAdvanceAmount2", request);
				lStrAppDate = StringUtility.getParameter("txtAppDate2", request);
				lStrPurposeCat = StringUtility.getParameter("cmblstPurposeCategory2", request);
				lStrOtherPurpose = StringUtility.getParameter("textareaOther2", request);
				lStrSanctionedAmount = StringUtility.getParameter("txtSancAmountNRA", request);
				lStrBalanceOutstanding = StringUtility.getParameter("txtOutstandingBalanceNRA", request);
				lStrSubType = StringUtility.getParameter("cmblstSubType", request);
				lStrSancInst = StringUtility.getParameter("txtSancPayInstNo", request);
				if (!lStrSancInst.equalsIgnoreCase("") && lStrSancInst != null) {
					lIntSancInst = Integer.parseInt(lStrSancInst);
				}
			}

			if (lStrMstGpfAdvanceId != null && !lStrMstGpfAdvanceId.equals("")) {
				lObjMstGpfAdvance = (MstGpfAdvance) lObjAdvanceProcessDAO.read(Long.parseLong(lStrMstGpfAdvanceId));
				iSaveOrUpdateFlag = 2;

			} else {
				lObjMstGpfAdvance = new MstGpfAdvance();
				iSaveOrUpdateFlag = 1;
			}

			inputMap.put("iSaveOrUpdateFlag", iSaveOrUpdateFlag);
			// lObjMstGpfAdvance.setTransactionId(lStrTrnId);
			lObjMstGpfAdvance.setFinYearId(lLngYearId);
			
			if(lStrRequestName.equals("RA")){
				lObjMstGpfAdvance.setSpecialCase(lStrSpecialCase.charAt(0));
				lObjMstGpfAdvance.setSpecialCaseFor90Perc(lStrSpecialCase90Percnt.charAt(0));
			}
			
			if(lStrRequestName.equals("NRA")){
				lObjMstGpfAdvance.setSpecialCase(lStrSpecialCase.charAt(0));
				lObjMstGpfAdvance.setSpecialCaseFor90Perc(lStrSpecialCase90Percnt.charAt(0));
			}
			// StringUtility.getParameter("textareaOther", request);
			if (lStrClubFlag != null && !"".equals(lStrClubFlag.trim()) && lStrClubFlag.trim().equals("1")) {
				lObjMstGpfAdvance.setClubbedReqTrnId(lStrClubReqTrnId);
			}
			if (lStrRequestName != null && !"".equals(lStrRequestName.trim())) {
				lObjMstGpfAdvance.setAdvanceType(lStrRequestName);
			}
			if (lStrGpfAccNo != null && !"".equals(lStrGpfAccNo.trim())) {
				lObjMstGpfAdvance.setGpfAccNo(lStrGpfAccNo);
			}
			if (lStrAppDate != null && !"".equals(lStrAppDate.trim())) {
				lObjMstGpfAdvance.setApplicationDate(simpleDateFormat.parse(lStrAppDate));
			}
			if (lStrAdvanceAmt != null && !(lStrAdvanceAmt.equals(""))) {
				lObjMstGpfAdvance.setAdvanceAmt(Double.parseDouble(lStrAdvanceAmt));
			}

			if (lStrPurposeCat != null && !(lStrPurposeCat.equals(""))) {
				lObjMstGpfAdvance.setPurposeCategory(Long.parseLong(lStrPurposeCat));
				if (lStrPurposeCat.equals("800009") || lStrPurposeCat.equals("800026")) {
					if (lStrOtherPurpose != null && !(lStrOtherPurpose.equals(""))) {
						lObjMstGpfAdvance.setOtherPurpose(lStrOtherPurpose);
					}
				}
				if (lStrPurposeCat.equals("800024") || lStrPurposeCat.equals("800025")) {
					if (lStrSubType != null && !(lStrSubType.equals(""))) {
						lObjMstGpfAdvance.setSubType(Long.parseLong(lStrSubType));
					}
				}
			}
			if (lStrSanctionedAmount != null && !(lStrSanctionedAmount.equals(""))) {
				lObjMstGpfAdvance.setAmountSanctioned(Double.parseDouble(lStrSanctionedAmount));
			}
			if (lStrSancNoOfInstallments != null && !(lStrSancNoOfInstallments.equals(""))) {
				lObjMstGpfAdvance.setNoOfInstallments(Integer.parseInt(lStrSancNoOfInstallments));
			}
			if (lStrPayableAmount != null && !(lStrPayableAmount.equals(""))) {
				lObjMstGpfAdvance.setPayableAmount(Double.parseDouble(lStrPayableAmount));
			}
			if (lStrInstallmentAmount != null && !(lStrInstallmentAmount.equals(""))) {
				lObjMstGpfAdvance.setInstallmentAmount(Double.parseDouble(lStrInstallmentAmount));
			}
			if (lStrOddInstallmentNo != null && !(lStrOddInstallmentNo.equals(""))) {
				lObjMstGpfAdvance.setOddInstallmentNo(Long.parseLong(lStrOddInstallmentNo));
			}
			if (lStrOddInstallment != null && !(lStrOddInstallment.equals(""))) {
				lObjMstGpfAdvance.setOddInstallment(Double.parseDouble(lStrOddInstallment));
			}
			if (lStrBalanceOutstanding != null && !(lStrBalanceOutstanding.equals(""))) {
				lObjMstGpfAdvance.setBalance(Double.parseDouble(lStrBalanceOutstanding));
			}

			if (lStrRemarks != null && !(lStrRemarks.equals(""))) {

				lObjMstGpfAdvance.setUserRemarks(lStrRemarks);
			} else {
				lStrRemarks = StringUtility.getParameter("textareaRemarks2", request);
				lObjMstGpfAdvance.setUserRemarks(lStrRemarks);
			}
			if (lIntSancInst != null && !(lIntSancInst.equals(""))) {
				lObjMstGpfAdvance.setNoOfInstallments(lIntSancInst);
			}

			if (!lStrInstallment1.equalsIgnoreCase("") && lStrInstallment1 != null) {
				lObjMstGpfAdvance.setInstallment1(Double.parseDouble(lStrInstallment1));
			}
			if (!lStrInstallment2.equalsIgnoreCase("") && lStrInstallment2 != null) {
				lObjMstGpfAdvance.setInstallment2(Double.parseDouble(lStrInstallment2));
			}
			if (!lStrInstallment3.equalsIgnoreCase("") && lStrInstallment3 != null) {
				lObjMstGpfAdvance.setInstallment3(Double.parseDouble(lStrInstallment3));
			}
			if (!lStrInstallment4.equalsIgnoreCase("") && lStrInstallment4 != null) {
				lObjMstGpfAdvance.setInstallment4(Double.parseDouble(lStrInstallment4));
			}

			if (!lStrReleaseDate1.equalsIgnoreCase("") && lStrReleaseDate1 != null) {
				lObjMstGpfAdvance.setReleaseDate1(simpleDateFormat.parse(lStrReleaseDate1));
			}
			if (!lStrReleaseDate2.equalsIgnoreCase("") && lStrReleaseDate2 != null) {
				lObjMstGpfAdvance.setReleaseDate2(simpleDateFormat.parse(lStrReleaseDate2));
			}
			if (!lStrReleaseDate3.equalsIgnoreCase("") && lStrReleaseDate3 != null) {
				lObjMstGpfAdvance.setReleaseDate3(simpleDateFormat.parse(lStrReleaseDate3));
			}
			if (!lStrReleaseDate4.equalsIgnoreCase("") && lStrReleaseDate4 != null) {
				lObjMstGpfAdvance.setReleaseDate4(simpleDateFormat.parse(lStrReleaseDate4));
			}

			if (iSaveOrUpdateFlag == 1) {
				lObjMstGpfAdvance.setCreatedPostId(gLngPostId);
				lObjMstGpfAdvance.setCreatedUserId(gLngUserId);
				lObjMstGpfAdvance.setCreatedDate(gDtCurrDt);
			}
			if (iSaveOrUpdateFlag == 2) {

				lObjMstGpfAdvance.setUpdatedPostId(gLngPostId);
				lObjMstGpfAdvance.setUpdatedUserId(gLngUserId);
				lObjMstGpfAdvance.setUpdatedDate(gDtCurrDt);
			}

		} catch (Exception ex) {
			gLogger.error("Error is :" + ex, ex);
		}
		return lObjMstGpfAdvance;
	}
}
