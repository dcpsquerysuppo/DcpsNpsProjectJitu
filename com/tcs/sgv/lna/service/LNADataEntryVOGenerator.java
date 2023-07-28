package com.tcs.sgv.lna.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.FinancialYearDAO;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.lna.dao.LNAComputerAdvanceDAO;
import com.tcs.sgv.lna.dao.LNAComputerAdvanceDAOImpl;
import com.tcs.sgv.lna.dao.LNADataEntryFormDAO;
import com.tcs.sgv.lna.dao.LNADataEntryFormDAOImpl;
import com.tcs.sgv.lna.dao.LNAHouseAdvanceDAO;
import com.tcs.sgv.lna.dao.LNAHouseAdvanceDAOImpl;
import com.tcs.sgv.lna.dao.LNAMotorAdvanceDAO;
import com.tcs.sgv.lna.dao.LNAMotorAdvanceDAOImpl;
import com.tcs.sgv.lna.valueobject.MstLnaChallanDtls;
import com.tcs.sgv.lna.valueobject.MstLnaCompAdvance;
import com.tcs.sgv.lna.valueobject.MstLnaHouseAdvance;
import com.tcs.sgv.lna.valueobject.MstLnaMonthly;
import com.tcs.sgv.lna.valueobject.MstLnaMotorAdvance;
import com.tcs.sgv.lna.valueobject.TrnEmpLoanDtls;

public class LNADataEntryVOGenerator extends ServiceImpl implements VOGeneratorService {

	Log glogger = LogFactory.getLog(getClass());
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/LNAConstants");

	public ResultObject generateMap(Map inputMap) {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		MstLnaCompAdvance lObjCompAdvance = null;
		MstLnaHouseAdvance lObjHouseAdvance = null;
		MstLnaMotorAdvance lObjMotorAdvance = null;
		TrnEmpLoanDtls lObjLoanDtls = null;
		Map<String, List> lScheduleVOMapCA = new HashMap<String, List>();
		Map<String, List> lScheduleVOMapHBA = new HashMap<String, List>();
		Map<String, List> lScheduleVOMapMCA = new HashMap<String, List>();
		try {
			lObjCompAdvance = generateCompAdvance(inputMap);
			lObjHouseAdvance = generateHouseAdvance(inputMap);
			lObjMotorAdvance = generateMotorAdvance(inputMap);
			lScheduleVOMapCA = generateComputerMonthly(inputMap);
			lScheduleVOMapHBA = generateHouseMonthly(inputMap);
			lScheduleVOMapMCA = generateMotorMonthly(inputMap);
			lObjLoanDtls = generateEmpLoanDtls(inputMap);
			inputMap.put("CompAdvance", lObjCompAdvance);
			inputMap.put("HouseAdvance", lObjHouseAdvance);
			inputMap.put("MotorAdvance", lObjMotorAdvance);
			inputMap.put("LoanDtls", lObjLoanDtls);
			inputMap.put("ScheduleDtls", lScheduleVOMapCA);
			inputMap.put("ScheduleDtlsHBA", lScheduleVOMapHBA);
			inputMap.put("ScheduleDtlsMCA", lScheduleVOMapMCA);
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			objRes.setResultValue(null);
			glogger.error(" Error is Data Entry VoGen: " + e, e);
		}
		return objRes;
	}

	private MstLnaCompAdvance generateCompAdvance(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		MstLnaCompAdvance lObjCompAdvance = null;

		LNAComputerAdvanceDAO lObjComputerAdvanceDAO = new LNAComputerAdvanceDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
		FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		Integer iSaveOrUpdateFlag = 0;
		Integer lIntInsLeft = null;

		String lStrComAdvanceId = StringUtility.getParameter("hidCompId", request);
		String lStrSevaarthId = StringUtility.getParameter("txtEmployeeCode", request);
		String lStrCompSubType = StringUtility.getParameter("cmbComputerSubType", request);
		String lStrSancAmount = StringUtility.getParameter("txtSancAmountCA", request);
		String lStrSanctionedDate = StringUtility.getParameter("txtSanctionedDateCA", request);
		String lStrSancInstallments = StringUtility.getParameter("txtTotalInsCA", request);
		String lStrInterestRate = StringUtility.getParameter("txtInterestRateCA", request);
		String lStrOrderNo = StringUtility.getParameter("txtOrderNoCA", request);
		String lStrOrderDate = StringUtility.getParameter("txtOrderDateCA", request);
		String lStrUserRemarks = StringUtility.getParameter("txtUserRemarksCA", request);
		String lStrRecoveredIns = StringUtility.getParameter("hidTotalRecoveredCA", request);
		try {
			Integer lIntFinYearId = lObjFinancialYearDAO.getFinYearId(lStrSanctionedDate);
			if (!"".equals(lStrComAdvanceId)) {
				lObjCompAdvance = (MstLnaCompAdvance) lObjComputerAdvanceDAO.read(Long.parseLong(lStrComAdvanceId));
				iSaveOrUpdateFlag = 2;

			} else {
				lObjCompAdvance = new MstLnaCompAdvance();
				iSaveOrUpdateFlag = 1;
			}

			inputMap.put("iSaveOrUpdateFlag", iSaveOrUpdateFlag);

			if (!"".equals(lStrSevaarthId.trim())) {
				lObjCompAdvance.setSevaarthId(lStrSevaarthId);
			}
			lObjCompAdvance.setAdvanceType(Long.parseLong(gObjRsrcBndle.getString("LNA.COMPUTERADVANCE")));

			if (!(lStrCompSubType.equals("-1"))) {
				lObjCompAdvance.setAdvanceSubType(Long.parseLong(lStrCompSubType));
			}
			if (!"".equals(lStrSanctionedDate.trim())) {
				lObjCompAdvance.setApplicationDate(lObjSimpleDateFormat.parse(lStrSanctionedDate));
			}
			if (!"".equals(lStrSancAmount.trim())) {
				lObjCompAdvance.setAmountRequested(Long.parseLong(lStrSancAmount));
			}
			if (!"".equals(lStrSancAmount.trim())) {
				lObjCompAdvance.setAmountSanctioned(Long.parseLong(lStrSancAmount));
			}
			if (!"".equals(lStrSancInstallments.trim())) {
				lObjCompAdvance.setSancInstallments(Integer.parseInt(lStrSancInstallments));
			}
			if (!"".equals(lStrRecoveredIns.trim()) && !"".equals(lStrSancInstallments.trim())) {
				lIntInsLeft = Integer.parseInt(lStrSancInstallments) - Integer.parseInt(lStrRecoveredIns);
				lObjCompAdvance.setInstallmentLeft(lIntInsLeft);
				if (lIntInsLeft == 0) {
					lObjCompAdvance.setRecoveryStatus(1);
				} else {
					lObjCompAdvance.setRecoveryStatus(0);
				}
			}
			if (!"".equals(lStrSancAmount.trim()) && !"".equals(lStrSancInstallments.trim())) {
				Double lDblInst = Math.floor(Double.parseDouble(lStrSancAmount) / Double.parseDouble(lStrSancInstallments));
				lObjCompAdvance.setInstallmentAmount(lDblInst.longValue());
				Double lDblOddValue = Double.parseDouble(lStrSancAmount) - (lDblInst * (Double.parseDouble(lStrSancInstallments) - 1));
				if (!lDblInst.equals(lDblOddValue)) {
					lObjCompAdvance.setOddInstallment(lDblOddValue.longValue());
					lObjCompAdvance.setOddInstallmentNumber(Long.parseLong(gObjRsrcBndle.getString("LNA.FIRSTODDINS")));
					if (!"".equals(lStrRecoveredIns.trim())) {
						lObjCompAdvance.setRecoveredAmount((lDblInst.longValue() * (Long.parseLong(lStrRecoveredIns) - 1)) + lDblOddValue.longValue());
						lObjCompAdvance.setOutstandingAmount(Long.parseLong(lStrSancAmount)
								- ((lDblInst.longValue() * (Long.parseLong(lStrRecoveredIns) - 1)) + lDblOddValue.longValue()));
					}
				} else {
					if (!"".equals(lStrRecoveredIns.trim())) {
						lObjCompAdvance.setRecoveredAmount(lDblInst.longValue() * Long.parseLong(lStrRecoveredIns));
						lObjCompAdvance.setOutstandingAmount(Long.parseLong(lStrSancAmount) - (lDblInst.longValue() * Long.parseLong(lStrRecoveredIns)));
					}
					lObjCompAdvance.setOddInstallment(null);
				}
			}
			if (!"".equals(lStrSanctionedDate.trim())) {
				lObjCompAdvance.setSanctionedDate(lObjSimpleDateFormat.parse(lStrSanctionedDate));
			}
			if (!"".equals(lStrUserRemarks.trim())) {
				lObjCompAdvance.setUserRemarks(lStrUserRemarks);
			}
			if (!"".equals(lStrOrderNo.trim())) {
				lObjCompAdvance.setOrderNo(lStrOrderNo);
			}
			if (!"".equals(lStrOrderDate.trim())) {
				lObjCompAdvance.setOrderDate(lObjSimpleDateFormat.parse(lStrOrderDate));
			}
			if (!"".equals(lStrInterestRate.trim())) {
				lObjCompAdvance.setInterestRate(Float.parseFloat(lStrInterestRate));
			}
			lObjCompAdvance.setFinYearId(Long.parseLong(lIntFinYearId.toString()));
			lObjCompAdvance.setHodasstActionDate(gDtCurrDt);
			if (iSaveOrUpdateFlag == 1) {
				lObjCompAdvance.setCreatedPostId(gLngPostId);
				lObjCompAdvance.setCreatedUserId(gLngUserId);
				lObjCompAdvance.setCreatedDate(gDtCurrDt);
			}
			if (iSaveOrUpdateFlag == 2) {
				lObjCompAdvance.setUpdatedUserId(gLngUserId);
				lObjCompAdvance.setUpdatedPostId(gLngPostId);
				lObjCompAdvance.setUpdatedDate(gDtCurrDt);
			}
		} catch (Exception e) {
			glogger.error(" Error is Data Entry Generate Computer: " + e, e);
		}
		return lObjCompAdvance;
	}

	private Map<String, List> generateComputerMonthly(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		MstLnaMonthly lObjLnaMonthly = null;
		MstLnaChallanDtls lObjChallanDtls = null;
		Map<String, List> lScheduleVOMap = new HashMap<String, List>();
		List<MstLnaMonthly> lLstVoucherDtls = new ArrayList<MstLnaMonthly>();
		List<MstLnaChallanDtls> lLstChallanDtls = new ArrayList<MstLnaChallanDtls>();
		String lStrSaveOrUpdateChalln = "";
		String lStrSaveOrUpdateVchr = "";
		Integer lIntVchrCnt = 0;
		Integer lIntChallanCnt = 0;
		List lLstPrvDataVchr = null;
		List lLstPrvDataChallan = null;

		LNADataEntryFormDAO lObjDataEntryFormDAO = new LNADataEntryFormDAOImpl(MstLnaMonthly.class, serv.getSessionFactory());
		LNADataEntryFormDAO lObjDataEntryFormDAOForChallan = new LNADataEntryFormDAOImpl(MstLnaChallanDtls.class, serv.getSessionFactory());

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		String lStrTotalRecord = StringUtility.getParameter("TotalRecord", request);
		String lStrSancAmount = StringUtility.getParameter("txtSancAmountCA", request);
		String lStrCompSubType = StringUtility.getParameter("cmbComputerSubType", request);
		String[] lArrStrPk = StringUtility.getParameterValues("hidSchdlDtlsPk", request);
		String lStrSevaarthId = StringUtility.getParameter("txtEmployeeCode", request);
		String lStrTransactionId = StringUtility.getParameter("txtTransactionIdCA", request);

		String[] lArrStrInstlmntVchr = StringUtility.getParameterValues("txtInstlmntVchr", request);
		String[] lArrStrMonth = StringUtility.getParameterValues("cmbMonthCA", request);
		String[] lArrStrYear = StringUtility.getParameterValues("cmbYearCA", request);
		String[] lArrStrInstAmount = StringUtility.getParameterValues("txtInstAmountCA", request);
		String[] lArrStrIsPayrollData = StringUtility.getParameterValues("isPayrollData", request);
		//	String[] lArrStrInterestRate = StringUtility.getParameterValues("txtInterestRateCATable", request);
		//String[] lArrStrAmtToBeRecovered = StringUtility.getParameterValues("txtAmtToBeRecoveredCA", request);
		String[] lArrStrTreasuryName = StringUtility.getParameterValues("txtTreasuryNameCA", request);
		String[] lArrStrVoucherNo = StringUtility.getParameterValues("txtVoucherNoCA", request);
		String[] lArrStrVoucherChallnDate = StringUtility.getParameterValues("txtVoucherDateCA", request);
		System.out.println("challan date:" +lArrStrVoucherChallnDate);
		String lStrSancInstallments = StringUtility.getParameter("txtTotalInsCA", request);
		String lStrRowID = StringUtility.getParameter("rowId", request);
		String lStrVoucherChallan = "";
		String lStrInstlmntChallanFrom = "";
		String lStrInstlmntChallanTo = "";
		Integer lIntTotalRecord = null;
		Integer lIntRowID = null;

		try {

			lLstPrvDataVchr = lObjDataEntryFormDAO.getPrevVoucherDtls(lStrSevaarthId, Long.parseLong(gObjRsrcBndle.getString("LNA.COMPUTERADVANCE")));
			if (lLstPrvDataVchr != null && !lLstPrvDataVchr.isEmpty()) {
				for (int lIntCnt = 0; lIntCnt < lLstPrvDataVchr.size(); lIntCnt++) {
					lObjLnaMonthly = new MstLnaMonthly();
					lObjLnaMonthly = (MstLnaMonthly) lLstPrvDataVchr.get(lIntCnt);
					lObjLnaMonthly.setStatus("X");
					lObjDataEntryFormDAO.update(lObjLnaMonthly);
				}
			}
			if (!"".equals(lStrTransactionId)) {
				lLstPrvDataChallan = lObjDataEntryFormDAOForChallan.getPrevChallanDtls(lStrSevaarthId, lStrTransactionId);
				if (lLstPrvDataChallan != null && !lLstPrvDataChallan.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstPrvDataChallan.size(); lIntCnt++) {
						lObjChallanDtls = new MstLnaChallanDtls();
						lObjChallanDtls = (MstLnaChallanDtls) lLstPrvDataChallan.get(lIntCnt);
						lObjChallanDtls.setStatusFlag("X");
						lObjDataEntryFormDAOForChallan.update(lObjChallanDtls);
					}
				}
			}

			if (!"".equals(lStrTotalRecord)) {
				lIntTotalRecord = Integer.parseInt(lStrTotalRecord);
			}
			if (!"".equals(lStrRowID)) {
				lIntRowID = Integer.parseInt(lStrRowID);
			}
			for (int lIntCnt = 0; lIntCnt <= lIntRowID; lIntCnt++) {
				if (!StringUtility.getParameter("radioVoucherChallan" + lIntCnt, request).equals("")) {
					lStrVoucherChallan += StringUtility.getParameter("radioVoucherChallan" + lIntCnt, request) + ",";
				}
				if (!StringUtility.getParameter("txtInstlmntChallanFrom" + lIntCnt, request).equals("")) {
					lStrInstlmntChallanFrom += StringUtility.getParameter("txtInstlmntChallanFrom" + lIntCnt, request) + ",";
				}
				if (!StringUtility.getParameter("txtInstlmntChallanTo" + lIntCnt, request).equals("")) {
					lStrInstlmntChallanTo += StringUtility.getParameter("txtInstlmntChallanTo" + lIntCnt, request) + ",";
				}
			}
			String[] lArrStrVoucherChallan = lStrVoucherChallan.split(",");
			String[] lArrStrInstlmntChallanFrom = lStrInstlmntChallanFrom.split(",");
			String[] lArrStrInstlmntChallanTo = lStrInstlmntChallanTo.split(",");
			Integer lPkLength = lArrStrPk.length;
			for (Integer lInt = 0; lInt < lIntTotalRecord - 2; lInt++) {

				if (lArrStrVoucherChallan[lInt].equals("V")) {
					if (lPkLength > 0 && lPkLength > lInt) {
						if (lArrStrPk[lInt] != null && !"".equals(lArrStrPk[lInt].trim())) {
							lStrSaveOrUpdateVchr = lStrSaveOrUpdateVchr + "2,";
							lObjLnaMonthly = (MstLnaMonthly) lObjDataEntryFormDAO.read(Long.parseLong(lArrStrPk[lInt]));
						}else {
							lStrSaveOrUpdateVchr = lStrSaveOrUpdateVchr + "1,";
							lObjLnaMonthly = new MstLnaMonthly();
						}
					} 
					if (!"".equals(lStrSevaarthId.trim())) {
						lObjLnaMonthly.setSevaarthId(lStrSevaarthId);
					}
					if (!"".equals(lStrSancAmount.trim())) {
						lObjLnaMonthly.setAdvanceType(Long.parseLong(gObjRsrcBndle.getString("LNA.COMPUTERADVANCE")));
					}
					if (!(lStrCompSubType.equals("-1"))) {
						lObjLnaMonthly.setAdvanceSubType(Long.parseLong(lStrCompSubType));
					}
					if (!"".equals(lArrStrMonth[lInt].trim())) {
						lObjLnaMonthly.setMonthId(Long.parseLong(lArrStrMonth[lInt]));
					}
					if (!"".equals(lArrStrYear[lInt].trim())) {
						lObjLnaMonthly.setFinYearId(Long.parseLong(lArrStrYear[lInt]));
					}
					/*if (!"".equals(lArrStrAmtToBeRecovered[lInt].trim())) {
						lObjLnaMonthly.setOpeningBalance(Long.parseLong(lArrStrAmtToBeRecovered[lInt]));
					}*/
					if (!"".equals(lArrStrInstAmount[lInt].trim())) {
						lObjLnaMonthly.setInstallmentAmount(Long.parseLong(lArrStrInstAmount[lInt]));
					}
					/*if (!"".equals(lArrStrInstAmount[lInt].trim()) && !"".equals(lArrStrAmtToBeRecovered[lInt].trim())) {
						lObjLnaMonthly.setClosingBalance(Long.parseLong(lArrStrAmtToBeRecovered[lInt]) - Long.parseLong(lArrStrInstAmount[lInt]));
					}*/
					if (!"".equals(lArrStrInstlmntVchr[lIntVchrCnt].trim())) {
						lObjLnaMonthly.setInstNo(Integer.parseInt(lArrStrInstlmntVchr[lIntVchrCnt]));
					}
					if (!"".equals(lStrSancInstallments.trim())) {
						lObjLnaMonthly.setTotalInst(Integer.parseInt(lStrSancInstallments));
					}
					if (!"".equals(lArrStrInstAmount[lInt].trim())) {
						lObjLnaMonthly.setInstallmentAmount(Long.parseLong(lArrStrInstAmount[lInt]));
					}
					if (!"".equals(lArrStrInstAmount[lInt].trim())) {
						lObjLnaMonthly.setInstallmentAmount(Long.parseLong(lArrStrInstAmount[lInt]));
					}
					if (!"".equals(lArrStrVoucherNo[lInt].trim())) {
						lObjLnaMonthly.setVoucherNo(lArrStrVoucherNo[lIntVchrCnt]);
					}
					if (!"".equals(lArrStrVoucherChallnDate[lInt])) {
						lObjLnaMonthly.setVoucherDate(lObjSimpleDateFormat.parse(lArrStrVoucherChallnDate[lInt]));
					}
					lObjLnaMonthly.setIsPayrollData(lArrStrIsPayrollData[lInt]);
					lObjLnaMonthly.setTreasuryId(lArrStrTreasuryName[lInt]);
					lObjLnaMonthly.setPrinOrInterestAmount("P");
					//if (iSaveOrUpdateFlag == 1) {
					lObjLnaMonthly.setCreatedPostId(gLngPostId);
					lObjLnaMonthly.setCreatedUserId(gLngUserId);
					lObjLnaMonthly.setCreatedDate(gDtCurrDt);
					/*}
					if (iSaveOrUpdateFlag == 2) {
						lObjLnaMonthly.setUpdatedUserId(gLngUserId);
						lObjLnaMonthly.setUpdatedPostId(gLngPostId);
						lObjLnaMonthly.setUpdatedDate(gDtCurrDt);
					}*/
					lLstVoucherDtls.add(lObjLnaMonthly);
					lIntVchrCnt++;
				} else if (lArrStrVoucherChallan[lInt].equals("C")) {
					if (lPkLength > 0 && lPkLength > lInt) {
						if (lArrStrPk[lInt] != null && !"".equals(lArrStrPk[lInt].trim())) {
							lStrSaveOrUpdateChalln = lStrSaveOrUpdateChalln + "2,";
							lObjChallanDtls = (MstLnaChallanDtls) lObjDataEntryFormDAOForChallan.read(Long.parseLong(lArrStrPk[lInt]));
						}else {
							lStrSaveOrUpdateChalln = lStrSaveOrUpdateChalln + "1,";
							lObjChallanDtls = new MstLnaChallanDtls();
						}
					} 
					if (!"".equals(lStrSevaarthId.trim())) {
						lObjChallanDtls.setSevaarthId(lStrSevaarthId);
					}
					if (!"".equals(lArrStrVoucherNo[lInt].trim())) {
						lObjChallanDtls.setChallanNo(lArrStrVoucherNo[lInt]);
					}
					if (!"".equals(lArrStrVoucherChallnDate[lInt])) {
						System.out.println("in VO date:" +lObjSimpleDateFormat.parse(lArrStrVoucherChallnDate[lInt]));
						lObjChallanDtls.setChallanDate(lObjSimpleDateFormat.parse(lArrStrVoucherChallnDate[lInt]));
					}
					if (!"".equals(lArrStrMonth[lInt].trim())) {
						lObjChallanDtls.setMonthId(Long.parseLong(lArrStrMonth[lInt]));
					}
					if (!"".equals(lArrStrYear[lInt].trim())) {
						lObjChallanDtls.setFinYearId(Long.parseLong(lArrStrYear[lInt]));
					}
					if (!"".equals(lArrStrInstAmount[lInt].trim())) {
						lObjChallanDtls.setInstallmentAmount(Long.parseLong(lArrStrInstAmount[lInt]));
					}
					/*if (!"".equals(lArrStrAmtToBeRecovered[lInt].trim())) {
						lObjChallanDtls.setOpeningBalance(Long.parseLong(lArrStrAmtToBeRecovered[lInt]));
					}
					if (!"".equals(lArrStrInstAmount[lInt].trim()) && !"".equals(lArrStrAmtToBeRecovered[lInt].trim())) {
						lObjChallanDtls.setClosingBalance(Long.parseLong(lArrStrAmtToBeRecovered[lInt]) - Long.parseLong(lArrStrInstAmount[lInt]));
					}*/
					if (!"".equals(lArrStrInstlmntChallanFrom[lIntChallanCnt].trim())) {
						lObjChallanDtls.setInstFrom(Integer.parseInt(lArrStrInstlmntChallanFrom[lIntChallanCnt]));
					}
					if (!"".equals(lArrStrInstlmntChallanTo[lIntChallanCnt].trim())) {
						lObjChallanDtls.setInstTo(Integer.parseInt(lArrStrInstlmntChallanTo[lIntChallanCnt]));
					}
					lObjChallanDtls.setIsPayrollData(lArrStrIsPayrollData[lInt]);
					lObjChallanDtls.setTreasuryId(lArrStrTreasuryName[lInt]);
					lObjChallanDtls.setPrinOrInterestAmount("P");
					lObjChallanDtls.setCreatedPostId(gLngPostId);
					lObjChallanDtls.setCreatedUserId(gLngUserId);
					lObjChallanDtls.setCreatedDate(gDtCurrDt);

					lLstChallanDtls.add(lObjChallanDtls);
					lIntChallanCnt++;
				}

			}
			lScheduleVOMap.put("lLstChallanDtls", lLstChallanDtls);
			lScheduleVOMap.put("lLstVoucherDtls", lLstVoucherDtls);
			inputMap.put("isSaveOrUpdateChln", lStrSaveOrUpdateChalln);
			inputMap.put("isSaveOrUpdateVchr", lStrSaveOrUpdateVchr);
		} catch (Exception e) {
			glogger.error(" Error is Data Entry Generate Computer Monthly: " + e, e);
		}
		return lScheduleVOMap;
	}

	private MstLnaHouseAdvance generateHouseAdvance(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		MstLnaHouseAdvance lObjHouseAdvance = null;

		LNAHouseAdvanceDAO lObjHouseAdvanceDAO = new LNAHouseAdvanceDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
		FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		Integer iSaveOrUpdateFlagHBA = 0;
		Integer lIntPrinInsLeft = null;
		Integer lIntInterestInsLeft = null;
		Float lFtDisbursement1 = null;
		Float lFtDisbursement2 = null;
		Float lFtDisbursement3 = null;
		Float lFtDisbursement4 = null;
		Double lDblInst = null;
		Double lDblOddValue = null;
		Long lLngRecoveredAmt = 0L;

		String lStrHouseAdvanceId = StringUtility.getParameter("hidHouseId", request);
		String lStrSevaarthId = StringUtility.getParameter("txtEmployeeCode", request);
		String lStrHouseSubType = StringUtility.getParameter("cmbHouseSubType", request);
		String lStrPrinAmount = StringUtility.getParameter("txtTotalPrincAmtHBA", request);
		String lStrInterestAmount = StringUtility.getParameter("txtTotalInterestAmtHBA", request);
		String lStrSanctionedDate = StringUtility.getParameter("txtSanctionedDateHBA", request);
		String lStrPrinInstallments = StringUtility.getParameter("txtTotalPrincInsHBA", request);
		String lStrInterestInstallments = StringUtility.getParameter("txtTotalInterestInsHBA", request);
		String lStrPrinRecoveredIns = StringUtility.getParameter("hidTotalPrinRecoveredHBA", request);
		String lStrInterestRecoveredIns = StringUtility.getParameter("hidTotalIntRecoveredHBA", request);
		String lStrInterestRate = StringUtility.getParameter("txtInterestRateHBA", request);
		String lStrNoOfDisbursement = StringUtility.getParameter("txtNoOfDisburseInst", request);
		String lStrOrderNo = StringUtility.getParameter("txtOrderNoHBA", request);
		String lStrOrderDate = StringUtility.getParameter("txtOrderDateHBA", request);
		String lStrUserRemarks = StringUtility.getParameter("txtUserRemarksHBA", request);
		String lStrReleaseDate1 = StringUtility.getParameter("txtReleaseDate1", request);
		String lStrReleaseDate2 = StringUtility.getParameter("txtReleaseDate2", request);
		String lStrReleaseDate3 = StringUtility.getParameter("txtReleaseDate3", request);
		String lStrReleaseDate4 = StringUtility.getParameter("txtReleaseDate4", request);
		try {
			Integer lIntFinYearId = lObjFinancialYearDAO.getFinYearId(lStrSanctionedDate);
			if (!"".equals(lStrHouseAdvanceId)) {
				lObjHouseAdvance = (MstLnaHouseAdvance) lObjHouseAdvanceDAO.read(Long.parseLong(lStrHouseAdvanceId));
				iSaveOrUpdateFlagHBA = 2;

			} else {
				lObjHouseAdvance = new MstLnaHouseAdvance();
				iSaveOrUpdateFlagHBA = 1;
			}

			inputMap.put("iSaveOrUpdateFlagHBA", iSaveOrUpdateFlagHBA);

			if (!"".equals(lStrSevaarthId.trim())) {
				lObjHouseAdvance.setSevaarthId(lStrSevaarthId);
			}
			lObjHouseAdvance.setAdvanceType(Long.parseLong(gObjRsrcBndle.getString("LNA.HOUSEADVANCE")));

			if (!(lStrHouseSubType.equals("-1"))) {
				lObjHouseAdvance.setAdvanceSubType(Long.parseLong(lStrHouseSubType));
			}
			if (!"".equals(lStrSanctionedDate.trim())) {
				lObjHouseAdvance.setApplicationDate(lObjSimpleDateFormat.parse(lStrSanctionedDate));
			}
			if (!"".equals(lStrPrinAmount.trim())) {
				lObjHouseAdvance.setAmountRequested(Long.parseLong(lStrPrinAmount));
			}
			if (!"".equals(lStrPrinAmount.trim())) {
				lObjHouseAdvance.setAmountSanctioned(Long.parseLong(lStrPrinAmount));
			}
			if (!"".equals(lStrInterestAmount.trim())) {
				lObjHouseAdvance.setInterestAmount(Double.parseDouble(lStrInterestAmount));
			}
			if (!"".equals(lStrPrinInstallments.trim())) {
				lObjHouseAdvance.setSancPrinInst(Integer.parseInt(lStrPrinInstallments));
			}
			if (!"".equals(lStrInterestInstallments.trim())) {
				lObjHouseAdvance.setSancInterestInst(Integer.parseInt(lStrInterestInstallments));
			}
			if (!"".equals(lStrPrinRecoveredIns.trim()) && !"".equals(lStrPrinInstallments.trim())) {
				lIntPrinInsLeft = Integer.parseInt(lStrPrinInstallments) - Integer.parseInt(lStrPrinRecoveredIns);
				lObjHouseAdvance.setPrincipalInstLeft(lIntPrinInsLeft);
			}
			if (!"".equals(lStrInterestRecoveredIns.trim()) && !"".equals(lStrInterestInstallments.trim())) {
				lIntInterestInsLeft = Integer.parseInt(lStrInterestInstallments) - Integer.parseInt(lStrInterestRecoveredIns);
				lObjHouseAdvance.setInterestInstLeft(lIntInterestInsLeft);
				if (lIntPrinInsLeft == 0 && lIntInterestInsLeft == 0) {
					lObjHouseAdvance.setRecoveryStatus(1);
				} else {
					lObjHouseAdvance.setRecoveryStatus(0);
				}
			}

			if (!"".equals(lStrPrinInstallments.trim()) && !"".equals(lStrPrinAmount.trim()) && !"0".equals(lStrPrinInstallments.trim()) && !"0".equals(lStrPrinAmount.trim())) {
				lDblInst = Math.floor(Double.parseDouble(lStrPrinAmount) / Double.parseDouble(lStrPrinInstallments));
				lObjHouseAdvance.setPrincipalInstAmtMonth(lDblInst.longValue());
				lDblOddValue = Double.parseDouble(lStrPrinAmount) - (lDblInst * (Double.parseDouble(lStrPrinInstallments) - 1));
				if (!lDblInst.equals(lDblOddValue)) {
					lObjHouseAdvance.setOddInstallment(lDblOddValue.longValue());
					lObjHouseAdvance.setOddInstallmentNumber(Long.parseLong(gObjRsrcBndle.getString("LNA.FIRSTODDINS")));
					if (!"".equals(lStrPrinRecoveredIns.trim())) {
						lLngRecoveredAmt = (lDblInst.longValue() * (Long.parseLong(lStrPrinRecoveredIns) - 1)) + lDblOddValue.longValue();
					}
				} else {
					lObjHouseAdvance.setOddInstallment(null);
					if (!"".equals(lStrPrinRecoveredIns.trim())) {
						lLngRecoveredAmt = lDblInst.longValue() * Long.parseLong(lStrPrinRecoveredIns);
					}
				}

			}
			if (!"".equals(lStrInterestAmount.trim()) && !"".equals(lStrInterestInstallments.trim()) && !"0".equals(lStrInterestAmount.trim()) && !"0".equals(lStrInterestInstallments.trim())) {
				lDblInst = Math.floor(Double.parseDouble(lStrInterestAmount) / Double.parseDouble(lStrInterestInstallments));
				lObjHouseAdvance.setInterestInstAmtMonth(lDblInst);
				
				lDblOddValue = Double.parseDouble(lStrInterestAmount) - (lDblInst * (Double.parseDouble(lStrInterestInstallments) - 1));
				if (!lDblInst.equals(lDblOddValue)) {
					lObjHouseAdvance.setOddInterestInstallment(lDblOddValue.longValue());
					lObjHouseAdvance.setOddInterestInstallmentNo(Long.parseLong(gObjRsrcBndle.getString("LNA.FIRSTODDINS")));
					if (!"".equals(lStrInterestRecoveredIns.trim())) {
						lLngRecoveredAmt = (lDblInst.longValue() * (Long.parseLong(lStrInterestRecoveredIns) - 1)) + lDblOddValue.longValue();
					}
				} else {
					lObjHouseAdvance.setOddInterestInstallment(null);
					if (!"".equals(lStrInterestRecoveredIns.trim())) {
						lLngRecoveredAmt = lLngRecoveredAmt + lDblInst.longValue() * Long.parseLong(lStrInterestRecoveredIns);
					}
				}

			}
			lObjHouseAdvance.setRecoveredAmount(lLngRecoveredAmt);
			if (!"".equals(lStrPrinAmount.trim()) && !"".equals(lStrInterestAmount.trim()) && !"".equals(lStrInterestInstallments.trim())) {
				Double lDblInterestAmt = Double.parseDouble(lStrInterestAmount);
				lObjHouseAdvance.setOutstandingAmount(Long.parseLong(lStrPrinAmount) + lDblInterestAmt.longValue() - lLngRecoveredAmt);
			}

			if (!"".equals(lStrNoOfDisbursement.trim()) && !"".equals(lStrPrinAmount.trim())) {
				if (lStrHouseSubType.equals(gObjRsrcBndle.getString("LNA.CONSTRUCTIONOFFLAT"))) {
					if (lStrNoOfDisbursement.equals("1")) {
						lFtDisbursement1 = Float.parseFloat(lDblInst.toString()) / 0.3F;
						lObjHouseAdvance.setDisbursementOne(lFtDisbursement1);
					} else if (lStrNoOfDisbursement.equals("2")) {
						lFtDisbursement1 = Float.parseFloat(lDblInst.toString()) / 0.3F;
						lObjHouseAdvance.setDisbursementOne(lFtDisbursement1);
						lFtDisbursement2 = Float.parseFloat(lDblInst.toString()) / 0.4F;
						lObjHouseAdvance.setDisbursementTwo(lFtDisbursement2);
					} else if (lStrNoOfDisbursement.equals("3")) {
						lFtDisbursement1 = Float.parseFloat(lDblInst.toString()) / 0.3F;
						lObjHouseAdvance.setDisbursementOne(lFtDisbursement1);
						lFtDisbursement2 = Float.parseFloat(lDblInst.toString()) / 0.4F;
						lObjHouseAdvance.setDisbursementTwo(lFtDisbursement2);
						lFtDisbursement3 = Float.parseFloat(lDblInst.toString()) / 0.3F;
						lObjHouseAdvance.setDisbursementThree(lFtDisbursement3);
					}

				} else if (lStrHouseSubType.equals(gObjRsrcBndle.getString("LNA.PLOATPURCHASE"))) {
					if (lStrNoOfDisbursement.equals("1")) {
						lFtDisbursement1 = Float.parseFloat(lDblInst.toString()) / 0.25F;
						lObjHouseAdvance.setDisbursementOne(lFtDisbursement1);
					} else if (lStrNoOfDisbursement.equals("2")) {
						lFtDisbursement1 = Float.parseFloat(lDblInst.toString()) / 0.25F;
						lObjHouseAdvance.setDisbursementOne(lFtDisbursement1);
						lFtDisbursement2 = Float.parseFloat(lDblInst.toString()) / 0.25F;
						lObjHouseAdvance.setDisbursementTwo(lFtDisbursement2);
					} else if (lStrNoOfDisbursement.equals("3")) {
						lFtDisbursement1 = Float.parseFloat(lDblInst.toString()) / 0.25F;
						lObjHouseAdvance.setDisbursementOne(lFtDisbursement1);
						lFtDisbursement2 = Float.parseFloat(lDblInst.toString()) / 0.25F;
						lObjHouseAdvance.setDisbursementTwo(lFtDisbursement2);
						lFtDisbursement3 = Float.parseFloat(lDblInst.toString()) / 0.3F;
						lObjHouseAdvance.setDisbursementThree(lFtDisbursement3);
					} else if (lStrNoOfDisbursement.equals("4")) {
						lFtDisbursement1 = Float.parseFloat(lDblInst.toString()) / 0.25F;
						lObjHouseAdvance.setDisbursementOne(lFtDisbursement1);
						lFtDisbursement2 = Float.parseFloat(lDblInst.toString()) / 0.25F;
						lObjHouseAdvance.setDisbursementTwo(lFtDisbursement2);
						lFtDisbursement3 = Float.parseFloat(lDblInst.toString()) / 0.3F;
						lObjHouseAdvance.setDisbursementThree(lFtDisbursement3);
						lFtDisbursement4 = Float.parseFloat(lDblInst.toString()) / 0.1F;
						lObjHouseAdvance.setDisbursementFour(lFtDisbursement4);
					}
				}

			}
			if (!"".equals(lStrReleaseDate1.trim())) {
				lObjHouseAdvance.setReleaseDateOne(lObjSimpleDateFormat.parse(lStrReleaseDate1));
			}
			if (!"".equals(lStrReleaseDate2.trim())) {
				lObjHouseAdvance.setReleaseDateTwo(lObjSimpleDateFormat.parse(lStrReleaseDate2));
			}
			if (!"".equals(lStrReleaseDate3.trim())) {
				lObjHouseAdvance.setReleaseDateThree(lObjSimpleDateFormat.parse(lStrReleaseDate3));
			}
			if (!"".equals(lStrReleaseDate4.trim())) {
				lObjHouseAdvance.setReleaseDateFour(lObjSimpleDateFormat.parse(lStrReleaseDate4));
			}
			if (!"".equals(lStrSanctionedDate.trim())) {
				lObjHouseAdvance.setSanctionedDate(lObjSimpleDateFormat.parse(lStrSanctionedDate));
			}
			if (!"".equals(lStrUserRemarks.trim())) {
				lObjHouseAdvance.setUserRemarks(lStrUserRemarks);
			}
			if (!"".equals(lStrOrderNo.trim())) {
				lObjHouseAdvance.setOrderNo(lStrOrderNo);
			}
			if (!"".equals(lStrOrderDate.trim())) {
				lObjHouseAdvance.setOrderDate(lObjSimpleDateFormat.parse(lStrOrderDate));
			}
			if (!"".equals(lStrInterestRate.trim())) {
				lObjHouseAdvance.setInterestRate(Float.parseFloat(lStrInterestRate));
			}
			lObjHouseAdvance.setFinYearId(Long.parseLong(lIntFinYearId.toString()));
			lObjHouseAdvance.setHodasstActionDate(gDtCurrDt);
			if (iSaveOrUpdateFlagHBA == 1) {
				lObjHouseAdvance.setCreatedPostId(gLngPostId);
				lObjHouseAdvance.setCreatedUserId(gLngUserId);
				lObjHouseAdvance.setCreatedDate(gDtCurrDt);
			}
			if (iSaveOrUpdateFlagHBA == 2) {
				lObjHouseAdvance.setUpdatedUserId(gLngUserId);
				lObjHouseAdvance.setUpdatedPostId(gLngPostId);
				lObjHouseAdvance.setUpdatedDate(gDtCurrDt);
			}
		} catch (Exception e) {
			glogger.error(" Error is Data Entry Generate House: " + e, e);
		}
		return lObjHouseAdvance;
	}

	private Map<String, List> generateHouseMonthly(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		MstLnaMonthly lObjLnaMonthly = null;
		MstLnaChallanDtls lObjChallanDtls = null;
		Map<String, List> lScheduleVOMap = new HashMap<String, List>();
		List<MstLnaMonthly> lLstVoucherDtls = new ArrayList<MstLnaMonthly>();
		List<MstLnaChallanDtls> lLstChallanDtls = new ArrayList<MstLnaChallanDtls>();
		String lStrSaveOrUpdateChalln = "";
		String lStrSaveOrUpdateVchr = "";
		Integer lIntVchrCnt = 0;
		Integer lIntChallanCnt = 0;
		Integer lIntPrinIntCnt = 0;
		Integer lIntInterestInsCnt = 0;

		LNADataEntryFormDAO lObjDataEntryFormDAO = new LNADataEntryFormDAOImpl(MstLnaMonthly.class, serv.getSessionFactory());
		LNADataEntryFormDAO lObjDataEntryFormDAOForChallan = new LNADataEntryFormDAOImpl(MstLnaChallanDtls.class, serv.getSessionFactory());

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		String lStrTotalRecord = StringUtility.getParameter("TotalRecordHBA", request);
		String lStrSancAmount = StringUtility.getParameter("txtSancAmountHBA", request);
		String lStrCompSubType = StringUtility.getParameter("cmbHouseSubType", request);
		String[] lArrStrPk = StringUtility.getParameterValues("hidSchdlDtlsPkHBA", request);
		String lStrSevaarthId = StringUtility.getParameter("txtEmployeeCode", request);
		String lStrTransactionId = StringUtility.getParameter("txtTransactionIdHBA", request);
		String[] lArrStrInstlmntVchr = StringUtility.getParameterValues("txtInstlmntVchrHBA", request);
		String[] lArrStrMonth = StringUtility.getParameterValues("cmbMonthHBA", request);
		String[] lArrStrYear = StringUtility.getParameterValues("cmbYearHBA", request);
		//	String[] lArrStrInterestRate = StringUtility.getParameterValues("txtInterestRateCATable", request);
		//String[] lArrStrAmtToBeRecovered = StringUtility.getParameterValues("txtAmtToBeRecoveredHBA", request);
		String[] lArrStrIsPayrollData = StringUtility.getParameterValues("isPayrollDataHBA", request);
		String[] lArrStrTreasuryName = StringUtility.getParameterValues("txtTreasuryNameHBA", request);
		String[] lArrStrVoucherNo = StringUtility.getParameterValues("txtVoucherNoHBA", request);
		String[] lArrStrVoucherChallnDate = StringUtility.getParameterValues("txtVoucherDateHBA", request);
		String lStrSancPrinInstallments = StringUtility.getParameter("txtTotalPrincInsHBA", request);
		String lStrSancInterestInstallments = StringUtility.getParameter("txtTotalInterestInsHBA", request);
		String[] lStrPrinInstAmount = StringUtility.getParameterValues("txtPrinInstAmountHBA", request);
		String[] lStrInterestInstAmount = StringUtility.getParameterValues("txtInterestInstAmountHBA", request);

		String lStrRowID = StringUtility.getParameter("rowIdHBA", request);
		String lStrVoucherChallan = "";
		String lStrInstlmntChallanFrom = "";
		String lStrInstlmntChallanTo = "";
		Integer lIntTotalRecord = null;
		Integer lIntRowID = null;
		List lLstPrvDataVchr = null;
		List lLstPrvDataChallan = null;
		try {
			lLstPrvDataVchr = lObjDataEntryFormDAO.getPrevVoucherDtls(lStrSevaarthId, Long.parseLong(gObjRsrcBndle.getString("LNA.HOUSEADVANCE")));
			if (lLstPrvDataVchr != null && !lLstPrvDataVchr.isEmpty()) {
				for (int lIntCnt = 0; lIntCnt < lLstPrvDataVchr.size(); lIntCnt++) {
					lObjLnaMonthly = new MstLnaMonthly();
					lObjLnaMonthly = (MstLnaMonthly) lLstPrvDataVchr.get(lIntCnt);
					lObjLnaMonthly.setStatus("X");
					lObjDataEntryFormDAO.update(lObjLnaMonthly);
				}
			}
			if (!"".equals(lStrTransactionId)) {
				lLstPrvDataChallan = lObjDataEntryFormDAOForChallan.getPrevChallanDtls(lStrSevaarthId, lStrTransactionId);
				if (lLstPrvDataChallan != null && !lLstPrvDataChallan.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstPrvDataChallan.size(); lIntCnt++) {
						lObjChallanDtls = new MstLnaChallanDtls();
						lObjChallanDtls = (MstLnaChallanDtls) lLstPrvDataChallan.get(lIntCnt);
						lObjChallanDtls.setStatusFlag("X");
						lObjDataEntryFormDAOForChallan.update(lObjChallanDtls);
					}
				}
			}
			if (!"".equals(lStrTotalRecord)) {
				lIntTotalRecord = Integer.parseInt(lStrTotalRecord);
			}
			if (!"".equals(lStrRowID)) {
				lIntRowID = Integer.parseInt(lStrRowID);
			}
			for (int lIntCnt = 0; lIntCnt <= lIntRowID; lIntCnt++) {
				if (!StringUtility.getParameter("radioVoucherChallanHBA" + lIntCnt, request).equals("")) {
					lStrVoucherChallan += StringUtility.getParameter("radioVoucherChallanHBA" + lIntCnt, request) + ",";
				}
				if (!StringUtility.getParameter("txtInstlmntChallanFromHBA" + lIntCnt, request).equals("")) {
					lStrInstlmntChallanFrom += StringUtility.getParameter("txtInstlmntChallanFromHBA" + lIntCnt, request) + ",";
				}
				if (!StringUtility.getParameter("txtInstlmntChallanToHBA" + lIntCnt, request).equals("")) {
					lStrInstlmntChallanTo += StringUtility.getParameter("txtInstlmntChallanToHBA" + lIntCnt, request) + ",";
				}
			}
			String[] lArrStrVoucherChallan = lStrVoucherChallan.split(",");
			String[] lArrStrInstlmntChallanFrom = lStrInstlmntChallanFrom.split(",");
			String[] lArrStrInstlmntChallanTo = lStrInstlmntChallanTo.split(",");
			Integer lPkLength = lArrStrPk.length;
			for (Integer lInt = 0; lInt < lIntTotalRecord - 2; lInt++) {

				if (lArrStrVoucherChallan[lInt].equals("V")) {
					if (lPkLength > 0 && lPkLength > lInt) {
						if (lArrStrPk[lInt] != null && !"".equals(lArrStrPk[lInt].trim())) {
							lStrSaveOrUpdateVchr = lStrSaveOrUpdateVchr + "2,";
							lObjLnaMonthly = (MstLnaMonthly) lObjDataEntryFormDAO.read(Long.parseLong(lArrStrPk[lInt]));
						} else {
							lStrSaveOrUpdateVchr = lStrSaveOrUpdateVchr + "1,";
							lObjLnaMonthly = new MstLnaMonthly();
						}
					}
					if (!"".equals(lStrSevaarthId.trim())) {
						lObjLnaMonthly.setSevaarthId(lStrSevaarthId);
					}
					if (!"".equals(lStrSancAmount.trim())) {
						lObjLnaMonthly.setAdvanceType(Long.parseLong(gObjRsrcBndle.getString("LNA.HOUSEADVANCE")));
					}
					if (!(lStrCompSubType.equals("-1"))) {
						lObjLnaMonthly.setAdvanceSubType(Long.parseLong(lStrCompSubType));
					}
					if (!"".equals(lArrStrMonth[lInt].trim())) {
						lObjLnaMonthly.setMonthId(Long.parseLong(lArrStrMonth[lInt]));
					}
					if (!"".equals(lArrStrYear[lInt].trim())) {
						lObjLnaMonthly.setFinYearId(Long.parseLong(lArrStrYear[lInt]));
					}
					/*if (!"".equals(lArrStrAmtToBeRecovered[lInt].trim())) {
						lObjLnaMonthly.setOpeningBalance(Long.parseLong(lArrStrAmtToBeRecovered[lInt]));
					}*/
					if (!"".equals(lArrStrInstlmntVchr[lIntVchrCnt].trim())) {
						lObjLnaMonthly.setInstNo(Integer.parseInt(lArrStrInstlmntVchr[lIntVchrCnt]));
					}
					if (!"".equals(lStrSancPrinInstallments.trim()) && !"".equals(lStrSancInterestInstallments.trim())) {
						lObjLnaMonthly.setTotalInst(Integer.parseInt(lStrSancPrinInstallments) + Integer.parseInt(lStrSancInterestInstallments));
					}
					if (!"".equals(lArrStrVoucherNo[lInt].trim())) {
						lObjLnaMonthly.setVoucherNo(lArrStrVoucherNo[lIntVchrCnt]);
					}
					if (!"".equals(lArrStrVoucherChallnDate[lInt])) {
						lObjLnaMonthly.setVoucherDate(lObjSimpleDateFormat.parse(lArrStrVoucherChallnDate[lInt]));
					}
					if (!"".equals(lStrPrinInstAmount[lInt].trim())
							//&& !"".equals(lArrStrAmtToBeRecovered[lInt].trim())
							) {
						lObjLnaMonthly.setInstallmentAmount(Long.parseLong(lStrPrinInstAmount[lInt]));
						//lObjLnaMonthly.setClosingBalance(Long.parseLong(lArrStrAmtToBeRecovered[lInt]) - Long.parseLong(lStrPrinInstAmount[lInt]));
						lObjLnaMonthly.setPrinOrInterestAmount("P");
						lIntPrinIntCnt++;
					} else if (!"".equals(lStrInterestInstAmount[lInt].trim()) 
							//&& !"".equals(lArrStrAmtToBeRecovered[lInt].trim())
							) {
						lObjLnaMonthly.setInstallmentAmount(Long.parseLong(lStrInterestInstAmount[lInt]));
						//lObjLnaMonthly.setClosingBalance(Long.parseLong(lArrStrAmtToBeRecovered[lInt]) - Long.parseLong(lStrInterestInstAmount[lInt]));
						lObjLnaMonthly.setPrinOrInterestAmount("I");
						lIntInterestInsCnt++;
					}
					lObjLnaMonthly.setIsPayrollData(lArrStrIsPayrollData[lInt]);
					lObjLnaMonthly.setTreasuryId(lArrStrTreasuryName[lInt]);
					lObjLnaMonthly.setCreatedPostId(gLngPostId);
					lObjLnaMonthly.setCreatedUserId(gLngUserId);
					lObjLnaMonthly.setCreatedDate(gDtCurrDt);

					lLstVoucherDtls.add(lObjLnaMonthly);
					lIntVchrCnt++;
				} else if (lArrStrVoucherChallan[lInt].equals("C")) {
					if (lPkLength > 0 && lPkLength > lInt) {
						if (lArrStrPk[lInt] != null && !"".equals(lArrStrPk[lInt].trim())) {
							lStrSaveOrUpdateChalln = lStrSaveOrUpdateChalln + "2,";
							lObjChallanDtls = (MstLnaChallanDtls) lObjDataEntryFormDAOForChallan.read(Long.parseLong(lArrStrPk[lInt]));
						} else {
							lStrSaveOrUpdateChalln = lStrSaveOrUpdateChalln + "1,";
							lObjChallanDtls = new MstLnaChallanDtls();
						}
					}
					if (!"".equals(lStrSevaarthId.trim())) {
						lObjChallanDtls.setSevaarthId(lStrSevaarthId);
					}
					if (!"".equals(lArrStrVoucherNo[lInt].trim())) {
						lObjChallanDtls.setChallanNo(lArrStrVoucherNo[lInt]);
					}
					if (!"".equals(lArrStrVoucherChallnDate[lInt])) {
						lObjChallanDtls.setChallanDate(lObjSimpleDateFormat.parse(lArrStrVoucherChallnDate[lInt]));
					}
					if (!"".equals(lArrStrMonth[lInt].trim())) {
						lObjChallanDtls.setMonthId(Long.parseLong(lArrStrMonth[lInt]));
					}
					if (!"".equals(lArrStrYear[lInt].trim())) {
						lObjChallanDtls.setFinYearId(Long.parseLong(lArrStrYear[lInt]));
					}
					/*if (!"".equals(lArrStrAmtToBeRecovered[lInt].trim())) {
						lObjChallanDtls.setOpeningBalance(Long.parseLong(lArrStrAmtToBeRecovered[lInt]));
					}*/
					if (!"".equals(lStrPrinInstAmount[lInt].trim()) 
							//&& !"".equals(lArrStrAmtToBeRecovered[lInt].trim())
							) {
						lObjChallanDtls.setInstallmentAmount(Long.parseLong(lStrPrinInstAmount[lInt]));
						//lObjChallanDtls.setClosingBalance(Long.parseLong(lArrStrAmtToBeRecovered[lInt]) - Long.parseLong(lStrPrinInstAmount[lInt]));
						lObjChallanDtls.setPrinOrInterestAmount("P");
					} else if (!"".equals(lStrInterestInstAmount[lInt].trim())
							//&& !"".equals(lArrStrAmtToBeRecovered[lInt].trim())
							) {
						lObjChallanDtls.setInstallmentAmount(Long.parseLong(lStrInterestInstAmount[lInt]));
						//lObjChallanDtls.setClosingBalance(Long.parseLong(lArrStrAmtToBeRecovered[lInt]) - Long.parseLong(lStrPrinInstAmount[lInt]));
						lObjChallanDtls.setPrinOrInterestAmount("I");
					}
					if (!"".equals(lArrStrInstlmntChallanFrom[lIntChallanCnt].trim())) {
						lObjChallanDtls.setInstFrom(Integer.parseInt(lArrStrInstlmntChallanFrom[lIntChallanCnt]));
					}
					if (!"".equals(lArrStrInstlmntChallanTo[lIntChallanCnt].trim())) {
						lObjChallanDtls.setInstTo(Integer.parseInt(lArrStrInstlmntChallanTo[lIntChallanCnt]));
					}
					lObjChallanDtls.setIsPayrollData(lArrStrIsPayrollData[lInt]);
					lObjChallanDtls.setTreasuryId(lArrStrTreasuryName[lInt]);
					lObjChallanDtls.setCreatedPostId(gLngPostId);
					lObjChallanDtls.setCreatedUserId(gLngUserId);
					lObjChallanDtls.setCreatedDate(gDtCurrDt);

					lLstChallanDtls.add(lObjChallanDtls);
					lIntChallanCnt++;
				}

			}
			lScheduleVOMap.put("lLstChallanDtlsHBA", lLstChallanDtls);
			lScheduleVOMap.put("lLstVoucherDtlsHBA", lLstVoucherDtls);
			inputMap.put("isSaveOrUpdateChlnHBA", lStrSaveOrUpdateChalln);
			inputMap.put("isSaveOrUpdateVchrHBA", lStrSaveOrUpdateVchr);
		} catch (Exception e) {
			glogger.error(" Error is Data Entry Generate House Monthly: " + e, e);
		}
		return lScheduleVOMap;
	}

	private MstLnaMotorAdvance generateMotorAdvance(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		MstLnaMotorAdvance lObjMotorAdvance = null;

		LNAMotorAdvanceDAO lObjmMotorAdvanceDAO = new LNAMotorAdvanceDAOImpl(MstLnaMotorAdvance.class, serv.getSessionFactory());
		FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		Integer iSaveOrUpdateFlagMCA = 0;
		Integer lIntPrinInsLeft = null;
		Integer lIntInterestInsLeft = null;
		Double lDblInst = null;
		Double lDblOddValue = null;
		Long lLngRecoveredAmt = 0L;

		String lStrMotorAdvanceId = StringUtility.getParameter("hidMotorId", request);
		String lStrSevaarthId = StringUtility.getParameter("txtEmployeeCode", request);
		String lStrMotorSubType = StringUtility.getParameter("cmbMotorSubType", request);
		String lStrPrinAmount = StringUtility.getParameter("txtTotalPrincAmtMCA", request);
		String lStrInterestAmount = StringUtility.getParameter("txtTotalInterestAmtMCA", request);
		String lStrSanctionedDate = StringUtility.getParameter("txtSanctionedDateMCA", request);
		String lStrPrinInstallments = StringUtility.getParameter("txtTotalPrincInsMCA", request);
		String lStrInterestInstallments = StringUtility.getParameter("txtTotalInterestInsMCA", request);
		String lStrPrinRecoveredIns = StringUtility.getParameter("hidTotalPrinRecoveredMCA", request);
		String lStrInterestRecoveredIns = StringUtility.getParameter("hidTotalIntRecoveredMCA", request);
		String lStrInterestRate = StringUtility.getParameter("txtInterestRateMCA", request);
		String lStrOrderNo = StringUtility.getParameter("txtOrderNoMCA", request);
		String lStrOrderDate = StringUtility.getParameter("txtOrderDateMCA", request);
		String lStrUserRemarks = StringUtility.getParameter("txtUserRemarksMCA", request);
		try {
			Integer lIntFinYearId = lObjFinancialYearDAO.getFinYearId(lStrSanctionedDate);
			if (!"".equals(lStrMotorAdvanceId)) {
				lObjMotorAdvance = (MstLnaMotorAdvance) lObjmMotorAdvanceDAO.read(Long.parseLong(lStrMotorAdvanceId));
				iSaveOrUpdateFlagMCA = 2;

			} else {
				lObjMotorAdvance = new MstLnaMotorAdvance();
				iSaveOrUpdateFlagMCA = 1;
			}

			inputMap.put("iSaveOrUpdateFlagMCA", iSaveOrUpdateFlagMCA);

			if (!"".equals(lStrSevaarthId.trim())) {
				lObjMotorAdvance.setSevaarthId(lStrSevaarthId);
			}
			lObjMotorAdvance.setAdvanceType(Long.parseLong(gObjRsrcBndle.getString("LNA.MOTORADVANCE")));

			if (!(lStrMotorSubType.equals("-1"))) {
				lObjMotorAdvance.setAdvanceSubType(Long.parseLong(lStrMotorSubType));
			}
			if (!"".equals(lStrSanctionedDate.trim())) {
				lObjMotorAdvance.setApplicationDate(lObjSimpleDateFormat.parse(lStrSanctionedDate));
			}
			if (!"".equals(lStrPrinAmount.trim())) {
				lObjMotorAdvance.setAmountRequested(Long.parseLong(lStrPrinAmount));
			}
			if (!"".equals(lStrPrinAmount.trim())) {
				lObjMotorAdvance.setAmountSanctioned(Long.parseLong(lStrPrinAmount));
			}
			if (!"".equals(lStrInterestAmount.trim())) {
				lObjMotorAdvance.setInterestAmount(Double.parseDouble(lStrInterestAmount));
			}
			if (!"".equals(lStrPrinInstallments.trim())) {
				lObjMotorAdvance.setSancCapitalInst(Integer.parseInt(lStrPrinInstallments));
			}
			if (!"".equals(lStrInterestInstallments.trim())) {
				lObjMotorAdvance.setSancInterestInst(Integer.parseInt(lStrInterestInstallments));
			}
			if (!"".equals(lStrPrinRecoveredIns.trim()) && !"".equals(lStrPrinInstallments.trim())) {
				lIntPrinInsLeft = Integer.parseInt(lStrPrinInstallments) - Integer.parseInt(lStrPrinRecoveredIns);
				lObjMotorAdvance.setCapitalInstLeft(lIntPrinInsLeft);
			}
			if (!"".equals(lStrInterestRecoveredIns.trim()) && !"".equals(lStrInterestInstallments.trim())) {
				lIntInterestInsLeft = Integer.parseInt(lStrInterestInstallments) - Integer.parseInt(lStrInterestRecoveredIns);
				lObjMotorAdvance.setInterestInstLeft(lIntInterestInsLeft);
				if (lIntPrinInsLeft == 0 && lIntInterestInsLeft == 0) {
					lObjMotorAdvance.setRecoveryStatus(1);
				} else {
					lObjMotorAdvance.setRecoveryStatus(0);
				}
			}

			if (!"".equals(lStrPrinInstallments.trim()) && !"".equals(lStrPrinAmount.trim()) && !"0".equals(lStrPrinInstallments.trim()) && !"0".equals(lStrPrinAmount.trim())) {
				lDblInst = Math.floor(Double.parseDouble(lStrPrinAmount) / Double.parseDouble(lStrPrinInstallments));
				lObjMotorAdvance.setCappitalInstAmtMonth(lDblInst.longValue());
				lDblOddValue = Double.parseDouble(lStrPrinAmount) - (lDblInst * (Double.parseDouble(lStrPrinInstallments) - 1));
				if (!lDblInst.equals(lDblOddValue)) {
					lObjMotorAdvance.setOddInstallment(lDblOddValue.longValue());
					lObjMotorAdvance.setOddInstallmentNumber(Long.parseLong(gObjRsrcBndle.getString("LNA.FIRSTODDINS")));
				} else {
					lObjMotorAdvance.setOddInstallment(null);
				}
				if (!"".equals(lStrPrinRecoveredIns.trim())) {
					lLngRecoveredAmt = lDblInst.longValue() * Long.parseLong(lStrPrinRecoveredIns);
				}
			}
			if (!"".equals(lStrInterestAmount.trim()) && !"".equals(lStrInterestInstallments.trim()) && !"0".equals(lStrInterestAmount.trim()) && !"0".equals(lStrInterestInstallments.trim())) {
				lDblInst = Math.floor(Double.parseDouble(lStrInterestAmount) / Double.parseDouble(lStrInterestInstallments));
				lObjMotorAdvance.setInterestInstAmtMonth(lDblInst);
				lDblOddValue = Double.parseDouble(lStrInterestAmount) - (lDblInst * (Double.parseDouble(lStrInterestInstallments) - 1));
				if (!lDblInst.equals(lDblOddValue)) {
					lObjMotorAdvance.setOddInterestInstallment(lDblOddValue.longValue());
					lObjMotorAdvance.setOddInterestInstallmentNo(Long.parseLong(gObjRsrcBndle.getString("LNA.FIRSTODDINS")));
				} else {
					lObjMotorAdvance.setOddInterestInstallment(null);
				}
				if (!"".equals(lStrInterestRecoveredIns.trim())) {
					lLngRecoveredAmt = lLngRecoveredAmt + lDblInst.longValue() * Long.parseLong(lStrInterestRecoveredIns);
				}
			}
			lObjMotorAdvance.setRecoveredAmount(lLngRecoveredAmt);
			if (!"".equals(lStrPrinAmount.trim()) && !"".equals(lStrInterestAmount.trim()) && !"".equals(lStrInterestInstallments.trim())) {
				Double lDblInterestAmt = Double.parseDouble(lStrInterestAmount);
				lObjMotorAdvance.setOutstandingAmount(Long.parseLong(lStrPrinAmount) + lDblInterestAmt.longValue() - lLngRecoveredAmt);
			}
			if (!"".equals(lStrSanctionedDate.trim())) {
				lObjMotorAdvance.setSanctionedDate(lObjSimpleDateFormat.parse(lStrSanctionedDate));
			}
			if (!"".equals(lStrUserRemarks.trim())) {
				lObjMotorAdvance.setUserRemarks(lStrUserRemarks);
			}
			if (!"".equals(lStrOrderNo.trim())) {
				lObjMotorAdvance.setOrderNo(lStrOrderNo);
			}
			if (!"".equals(lStrOrderDate.trim())) {
				lObjMotorAdvance.setOrderDate(lObjSimpleDateFormat.parse(lStrOrderDate));
			}
			if (!"".equals(lStrInterestRate.trim())) {
				lObjMotorAdvance.setInterestRate(Float.parseFloat(lStrInterestRate));
			}
			lObjMotorAdvance.setFinYearId(Long.parseLong(lIntFinYearId.toString()));
			lObjMotorAdvance.setHodasstActionDate(gDtCurrDt);
			if (iSaveOrUpdateFlagMCA == 1) {
				lObjMotorAdvance.setCreatedPostId(gLngPostId);
				lObjMotorAdvance.setCreatedUserId(gLngUserId);
				lObjMotorAdvance.setCreatedDate(gDtCurrDt);
			}
			if (iSaveOrUpdateFlagMCA == 2) {
				lObjMotorAdvance.setUpdatedUserId(gLngUserId);
				lObjMotorAdvance.setUpdatedPostId(gLngPostId);
				lObjMotorAdvance.setUpdatedDate(gDtCurrDt);
			}
		} catch (Exception e) {
			glogger.error(" Error is Data Entry Generate Motor: " + e, e);
		}
		return lObjMotorAdvance;
	}

	private Map<String, List> generateMotorMonthly(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		MstLnaMonthly lObjLnaMonthly = null;
		MstLnaChallanDtls lObjChallanDtls = null;
		Map<String, List> lScheduleVOMap = new HashMap<String, List>();
		List<MstLnaMonthly> lLstVoucherDtls = new ArrayList<MstLnaMonthly>();
		List<MstLnaChallanDtls> lLstChallanDtls = new ArrayList<MstLnaChallanDtls>();
		String lStrSaveOrUpdateChalln = "";
		String lStrSaveOrUpdateVchr = "";
		Integer lIntVchrCnt = 0;
		Integer lIntChallanCnt = 0;
		Integer lIntPrinIntCnt = 0;
		Integer lIntInterestInsCnt = 0;

		LNADataEntryFormDAO lObjDataEntryFormDAO = new LNADataEntryFormDAOImpl(MstLnaMonthly.class, serv.getSessionFactory());
		LNADataEntryFormDAO lObjDataEntryFormDAOForChallan = new LNADataEntryFormDAOImpl(MstLnaChallanDtls.class, serv.getSessionFactory());

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		String lStrTotalRecord = StringUtility.getParameter("TotalRecordMCA", request);
		String lStrSancAmount = StringUtility.getParameter("txtSancAmountMCA", request);
		String lStrMotorSubType = StringUtility.getParameter("cmbMotorSubType", request);
		String[] lArrStrPk = StringUtility.getParameterValues("hidSchdlDtlsPkMCA", request);
		String lStrSevaarthId = StringUtility.getParameter("txtEmployeeCode", request);
		String lStrTransactionId = StringUtility.getParameter("txtTransactionIdMCA", request);
		String[] lArrStrInstlmntVchr = StringUtility.getParameterValues("txtInstlmntVchrMCA", request);
		String[] lArrStrMonth = StringUtility.getParameterValues("cmbMonthMCA", request);
		String[] lArrStrYear = StringUtility.getParameterValues("cmbYearMCA", request);
		//	String[] lArrStrInterestRate = StringUtility.getParameterValues("txtInterestRateCATable", request);
		//String[] lArrStrAmtToBeRecovered = StringUtility.getParameterValues("txtAmtToBeRecoveredMCA", request);
		String[] lArrStrIsPayrollData = StringUtility.getParameterValues("isPayrollDataMCA", request);
		String[] lArrStrTreasuryName = StringUtility.getParameterValues("txtTreasuryNameMCA", request);
		String[] lArrStrVoucherNo = StringUtility.getParameterValues("txtVoucherNoMCA", request);
		String[] lArrStrVoucherChallnDate = StringUtility.getParameterValues("txtVoucherDateMCA", request);
		String lStrSancPrinInstallments = StringUtility.getParameter("txtTotalPrincInsMCA", request);
		String lStrSancInterestInstallments = StringUtility.getParameter("txtTotalInterestInsMCA", request);
		String[] lStrPrinInstAmount = StringUtility.getParameterValues("txtPrinInstAmountMCA", request);
		String[] lStrInterestInstAmount = StringUtility.getParameterValues("txtInterestInstAmountMCA", request);

		String lStrRowID = StringUtility.getParameter("rowIdMCA", request);
		String lStrVoucherChallan = "";
		String lStrInstlmntChallanFrom = "";
		String lStrInstlmntChallanTo = "";
		Integer lIntTotalRecord = null;
		Integer lIntRowID = null;
		List lLstPrvDataVchr = null;
		List lLstPrvDataChallan = null;
		try {
			lLstPrvDataVchr = lObjDataEntryFormDAO.getPrevVoucherDtls(lStrSevaarthId, Long.parseLong(gObjRsrcBndle.getString("LNA.MOTORADVANCE")));
			if (lLstPrvDataVchr != null && !lLstPrvDataVchr.isEmpty()) {
				for (int lIntCnt = 0; lIntCnt < lLstPrvDataVchr.size(); lIntCnt++) {
					lObjLnaMonthly = new MstLnaMonthly();
					lObjLnaMonthly = (MstLnaMonthly) lLstPrvDataVchr.get(lIntCnt);
					lObjLnaMonthly.setStatus("X");
					lObjDataEntryFormDAO.update(lObjLnaMonthly);
				}
			}
			if (!"".equals(lStrTransactionId)) {
				lLstPrvDataChallan = lObjDataEntryFormDAOForChallan.getPrevChallanDtls(lStrSevaarthId, lStrTransactionId);
				if (lLstPrvDataChallan != null && !lLstPrvDataChallan.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstPrvDataChallan.size(); lIntCnt++) {
						lObjChallanDtls = new MstLnaChallanDtls();
						lObjChallanDtls = (MstLnaChallanDtls) lLstPrvDataChallan.get(lIntCnt);
						lObjChallanDtls.setStatusFlag("X");
						lObjDataEntryFormDAOForChallan.update(lObjChallanDtls);
					}
				}
			}

			if (!"".equals(lStrTotalRecord)) {
				lIntTotalRecord = Integer.parseInt(lStrTotalRecord);
			}
			if (!"".equals(lStrRowID)) {
				lIntRowID = Integer.parseInt(lStrRowID);
			}
			for (int lIntCnt = 0; lIntCnt <= lIntRowID; lIntCnt++) {
				if (!StringUtility.getParameter("radioVoucherChallanMCA" + lIntCnt, request).equals("")) {
					lStrVoucherChallan += StringUtility.getParameter("radioVoucherChallanMCA" + lIntCnt, request) + ",";
				}
				if (!StringUtility.getParameter("txtInstlmntChallanFromMCA" + lIntCnt, request).equals("")) {
					lStrInstlmntChallanFrom += StringUtility.getParameter("txtInstlmntChallanFromMCA" + lIntCnt, request) + ",";
				}
				if (!StringUtility.getParameter("txtInstlmntChallanToMCA" + lIntCnt, request).equals("")) {
					lStrInstlmntChallanTo += StringUtility.getParameter("txtInstlmntChallanToMCA" + lIntCnt, request) + ",";
				}
			}
			String[] lArrStrVoucherChallan = lStrVoucherChallan.split(",");
			String[] lArrStrInstlmntChallanFrom = lStrInstlmntChallanFrom.split(",");
			String[] lArrStrInstlmntChallanTo = lStrInstlmntChallanTo.split(",");
			Integer lPkLength = lArrStrPk.length;
			for (Integer lInt = 0; lInt < lIntTotalRecord - 2; lInt++) {

				if (lArrStrVoucherChallan[lInt].equals("V")) {
					if (lPkLength > 0 && lPkLength > lInt) {
						if (lArrStrPk[lInt] != null && !"".equals(lArrStrPk[lInt].trim())) {
							lStrSaveOrUpdateVchr = lStrSaveOrUpdateVchr + "2,";
							lObjLnaMonthly = (MstLnaMonthly) lObjDataEntryFormDAO.read(Long.parseLong(lArrStrPk[lInt]));
						}else {
							lStrSaveOrUpdateVchr = lStrSaveOrUpdateVchr + "1,";
							lObjLnaMonthly = new MstLnaMonthly();
						}
					} 
					if (!"".equals(lStrSevaarthId.trim())) {
						lObjLnaMonthly.setSevaarthId(lStrSevaarthId);
					}
					if (!"".equals(lStrSancAmount.trim())) {
						lObjLnaMonthly.setAdvanceType(Long.parseLong(gObjRsrcBndle.getString("LNA.MOTORADVANCE")));
					}
					if (!(lStrMotorSubType.equals("-1"))) {
						lObjLnaMonthly.setAdvanceSubType(Long.parseLong(lStrMotorSubType));
					}
					if (!"".equals(lArrStrMonth[lInt].trim())) {
						lObjLnaMonthly.setMonthId(Long.parseLong(lArrStrMonth[lInt]));
					}
					if (!"".equals(lArrStrYear[lInt].trim())) {
						lObjLnaMonthly.setFinYearId(Long.parseLong(lArrStrYear[lInt]));
					}
					/*if (!"".equals(lArrStrAmtToBeRecovered[lInt].trim())) {
						lObjLnaMonthly.setOpeningBalance(Long.parseLong(lArrStrAmtToBeRecovered[lInt]));
					}*/
					if (!"".equals(lArrStrInstlmntVchr[lIntVchrCnt].trim())) {
						lObjLnaMonthly.setInstNo(Integer.parseInt(lArrStrInstlmntVchr[lIntVchrCnt]));
					}
					if (!"".equals(lStrSancPrinInstallments.trim()) && !"".equals(lStrSancInterestInstallments.trim())) {
						lObjLnaMonthly.setTotalInst(Integer.parseInt(lStrSancPrinInstallments) + Integer.parseInt(lStrSancInterestInstallments));
					}
					if (!"".equals(lArrStrVoucherNo[lInt].trim())) {
						lObjLnaMonthly.setVoucherNo(lArrStrVoucherNo[lIntVchrCnt]);
					}
					if (!"".equals(lArrStrVoucherChallnDate[lInt])) {
						lObjLnaMonthly.setVoucherDate(lObjSimpleDateFormat.parse(lArrStrVoucherChallnDate[lInt]));
					}
					if (!"".equals(lStrPrinInstAmount[lInt].trim()) 
							//&& !"".equals(lArrStrAmtToBeRecovered[lInt].trim())
							) {
						lObjLnaMonthly.setInstallmentAmount(Long.parseLong(lStrPrinInstAmount[lInt]));
						//lObjLnaMonthly.setClosingBalance(Long.parseLong(lArrStrAmtToBeRecovered[lInt]) - Long.parseLong(lStrPrinInstAmount[lInt]));
						lObjLnaMonthly.setPrinOrInterestAmount("P");
						lIntPrinIntCnt++;
					} else if (!"".equals(lStrInterestInstAmount[lInt].trim())
							//&& !"".equals(lArrStrAmtToBeRecovered[lInt].trim())
							) {
						lObjLnaMonthly.setInstallmentAmount(Long.parseLong(lStrInterestInstAmount[lInt]));
						//lObjLnaMonthly.setClosingBalance(Long.parseLong(lArrStrAmtToBeRecovered[lInt]) - Long.parseLong(lStrInterestInstAmount[lInt]));
						lObjLnaMonthly.setPrinOrInterestAmount("I");
						lIntInterestInsCnt++;
					}
					lObjLnaMonthly.setIsPayrollData(lArrStrIsPayrollData[lInt]);
					lObjLnaMonthly.setTreasuryId(lArrStrTreasuryName[lInt]);
					lObjLnaMonthly.setCreatedPostId(gLngPostId);
					lObjLnaMonthly.setCreatedUserId(gLngUserId);
					lObjLnaMonthly.setCreatedDate(gDtCurrDt);

					lLstVoucherDtls.add(lObjLnaMonthly);
					lIntVchrCnt++;
				} else if (lArrStrVoucherChallan[lInt].equals("C")) {
					if (lPkLength > 0 && lPkLength > lInt) {
						if (lArrStrPk[lInt] != null && !"".equals(lArrStrPk[lInt].trim())) {
							lStrSaveOrUpdateChalln = lStrSaveOrUpdateChalln + "2,";
							lObjChallanDtls = (MstLnaChallanDtls) lObjDataEntryFormDAOForChallan.read(Long.parseLong(lArrStrPk[lInt]));
						}else {
							lStrSaveOrUpdateChalln = lStrSaveOrUpdateChalln + "1,";
							lObjChallanDtls = new MstLnaChallanDtls();
						}
					} 
					if (!"".equals(lStrSevaarthId.trim())) {
						lObjChallanDtls.setSevaarthId(lStrSevaarthId);
					}
					if (!"".equals(lArrStrVoucherNo[lInt].trim())) {
						lObjChallanDtls.setChallanNo(lArrStrVoucherNo[lInt]);
					}
					if (!"".equals(lArrStrVoucherChallnDate[lInt])) {
						lObjChallanDtls.setChallanDate(lObjSimpleDateFormat.parse(lArrStrVoucherChallnDate[lInt]));
					}
					if (!"".equals(lArrStrMonth[lInt].trim())) {
						lObjChallanDtls.setMonthId(Long.parseLong(lArrStrMonth[lInt]));
					}
					if (!"".equals(lArrStrYear[lInt].trim())) {
						lObjChallanDtls.setFinYearId(Long.parseLong(lArrStrYear[lInt]));
					}
					/*if (!"".equals(lArrStrAmtToBeRecovered[lInt].trim())) {
						lObjChallanDtls.setOpeningBalance(Long.parseLong(lArrStrAmtToBeRecovered[lInt]));
					}*/
					if (!"".equals(lStrPrinInstAmount[lInt].trim()) 
							//&& !"".equals(lArrStrAmtToBeRecovered[lInt].trim())
							) {
						lObjChallanDtls.setInstallmentAmount(Long.parseLong(lStrPrinInstAmount[lInt]));
						//lObjChallanDtls.setClosingBalance(Long.parseLong(lArrStrAmtToBeRecovered[lInt]) - Long.parseLong(lStrPrinInstAmount[lInt]));
						lObjChallanDtls.setPrinOrInterestAmount("P");
					} else if (!"".equals(lStrInterestInstAmount[lInt].trim()) 
							//&& !"".equals(lArrStrAmtToBeRecovered[lInt].trim())
							) {
						lObjChallanDtls.setInstallmentAmount(Long.parseLong(lStrInterestInstAmount[lInt]));
						//lObjChallanDtls.setClosingBalance(Long.parseLong(lArrStrAmtToBeRecovered[lInt]) - Long.parseLong(lStrPrinInstAmount[lInt]));
						lObjChallanDtls.setPrinOrInterestAmount("I");
					}
					if (!"".equals(lArrStrInstlmntChallanFrom[lIntChallanCnt].trim())) {
						lObjChallanDtls.setInstFrom(Integer.parseInt(lArrStrInstlmntChallanFrom[lIntChallanCnt]));
					}
					if (!"".equals(lArrStrInstlmntChallanTo[lIntChallanCnt].trim())) {
						lObjChallanDtls.setInstTo(Integer.parseInt(lArrStrInstlmntChallanTo[lIntChallanCnt]));
					}
					lObjChallanDtls.setIsPayrollData(lArrStrIsPayrollData[lInt]);
					lObjChallanDtls.setTreasuryId(lArrStrTreasuryName[lInt]);
					lObjChallanDtls.setCreatedPostId(gLngPostId);
					lObjChallanDtls.setCreatedUserId(gLngUserId);
					lObjChallanDtls.setCreatedDate(gDtCurrDt);

					lLstChallanDtls.add(lObjChallanDtls);
					lIntChallanCnt++;
				}

			}
			lScheduleVOMap.put("lLstChallanDtlsMCA", lLstChallanDtls);
			lScheduleVOMap.put("lLstVoucherDtlsMCA", lLstVoucherDtls);
			inputMap.put("isSaveOrUpdateChlnMCA", lStrSaveOrUpdateChalln);
			inputMap.put("isSaveOrUpdateVchrMCA", lStrSaveOrUpdateVchr);
		} catch (Exception e) {
			glogger.error(" Error is Data Entry Generate Motor Monthly: " + e, e);
		}
		return lScheduleVOMap;
	}

	private TrnEmpLoanDtls generateEmpLoanDtls(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		TrnEmpLoanDtls lObjEmpLoanDtls = null;

		LNADataEntryFormDAO lObjEntryFormDAO = new LNADataEntryFormDAOImpl(TrnEmpLoanDtls.class, serv.getSessionFactory());

		String lStrSevaarthId = StringUtility.getParameter("txtEmployeeCode", request);
		String lStrEmpLoanDtls = StringUtility.getParameter("hidTrnEmpLoanPk", request);
		String lStrEmpName = StringUtility.getParameter("txtEmployeeName", request);
		String lStrSancAmountCA = StringUtility.getParameter("txtSancAmountCA", request);
		String lStrSancAmountHBA = StringUtility.getParameter("txtSancAmountHBA", request);
		String lStrSancAmountMCA = StringUtility.getParameter("txtSancAmountMCA", request);
		int cmbComputerSubType=Integer.valueOf(StringUtility.getParameter("cmbComputerSubType", request));
		int cmbHouseSubType=Integer.valueOf(StringUtility.getParameter("cmbHouseSubType", request));
		int cmbMotorSubType=Integer.valueOf(StringUtility.getParameter("cmbMotorSubType", request));
		
		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		Integer iSaveOrUpdateFlagEmpLoan = 0;
		try {
			if (!"".equals(lStrEmpLoanDtls)) {
				lObjEmpLoanDtls = (TrnEmpLoanDtls) lObjEntryFormDAO.read(Long.parseLong(lStrEmpLoanDtls));
				iSaveOrUpdateFlagEmpLoan = 2;

			} else {
				lObjEmpLoanDtls = new TrnEmpLoanDtls();
				iSaveOrUpdateFlagEmpLoan = 1;
			}
			if (!"".equals(lStrSevaarthId)) {
				lObjEmpLoanDtls.setSevaarthId(lStrSevaarthId);
			}
			if (!"".equals(lStrEmpName)) {
				lObjEmpLoanDtls.setEmpName(lStrEmpName);
			}
		//added by aditya on 8/4/13
			if (cmbComputerSubType!=-1) {
				lObjEmpLoanDtls.setCompAdvance("Y");
			}
			if (cmbHouseSubType!=-1) {
				lObjEmpLoanDtls.setHouseAdvance("Y");
			}
			if (cmbMotorSubType!=-1) {
				lObjEmpLoanDtls.setMotorAdvance("Y");
			}
			/*if (!"".equals(lStrSancAmountCA)) {
				lObjEmpLoanDtls.setCompAdvance("Y");
			}
			if (!"".equals(lStrSancAmountHBA)) {
				lObjEmpLoanDtls.setHouseAdvance("Y");
			}
			if (!"".equals(lStrSancAmountMCA)) {
				lObjEmpLoanDtls.setMotorAdvance("Y");
			}*/
			if (iSaveOrUpdateFlagEmpLoan == 1) {
				lObjEmpLoanDtls.setCreatedPostId(gLngPostId);
				lObjEmpLoanDtls.setCreatedUserId(gLngUserId);
				lObjEmpLoanDtls.setCreatedDate(gDtCurrDt);
			}
			if (iSaveOrUpdateFlagEmpLoan == 2) {
				lObjEmpLoanDtls.setUpdatedUserId(gLngUserId);
				lObjEmpLoanDtls.setUpdatedPostId(gLngPostId);
				lObjEmpLoanDtls.setUpdatedDate(gDtCurrDt);
			}
			inputMap.put("iSaveOrUpdateFlagEmpLoan", iSaveOrUpdateFlagEmpLoan);
		} catch (Exception e) {
			glogger.error(" Error is Data Entry generateEmpLoanDtls: " + e, e);
		}
		return lObjEmpLoanDtls;
	}
}
