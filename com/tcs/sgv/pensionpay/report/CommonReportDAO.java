package com.tcs.sgv.pensionpay.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO;

public interface CommonReportDAO {
	List getLedgerRptHeaderDtl(String lStrPPONO, String lStrLocId,
			long lLangId, String lCaseStatus) throws Exception;

	List getEntitlePnsnerDtls(String lStrPPONO, String lStrLocId, long lLangId,
			String lCaseStatus) throws Exception;

	List getLedgerRptDtl(String lStrPPONO, Date lFromDate, Date lToDate,
			String lStrLocId, long lLangId, String lCaseStatus)
			throws Exception;

	ArrayList getOverPayBasicDtls(String ppoNo, String locCode)
			throws Exception;

	ArrayList getOverPayBillDtls(String ppoNo, String deathMonth)
			throws Exception;

	Double getOverPayRecoveryDtls(String ppoNo, String locCode)
			throws Exception;

	List getGeneralDeviationData(Integer lFromMonthYear, Integer lToMonthYear,
			String lStrScheme, String lStrHeadCode, List lLstPayType,
			String lStrLocCode) throws Exception;

	String getBankNameFromCode(String lStrBankCode) throws Exception;

	List getDeviationForBank(Integer lIntFromMonth, Integer lIntToMonth,
			String lStrBank, String lStrScheme, List lLstPayType,
			String lStrHeadCode, String lStrLocCode) throws Exception;

	List getDeviationForBranch(Integer lIntFromMonth, Integer lIntToMonth,
			String lStrBank, String lStrBranch, String locationCode)
			throws Exception;

	List getFPPaymentDetails(Integer lIntFromMonth, Integer lIntToMonth,
			List lPayType, String lStrHeadCode, String lStrScheme,
			String lStrLocCode) throws Exception;

	List getPPONosByBranchCode(String lStrJoinString, Integer lArgsFromMonth1,
			Integer lArgsFromMonth2, Integer lArgsToMonth1,
			Integer lArgsToMonth2, String lArgsBank, String lArgsBranch,
			String lArgslocationCode) throws Exception;

	ArrayList<MonthlyPensionBillVO> getTrnPensionBillDtls(List lLsrPpoNo,
			Integer lIntFromMonth, Integer lIntToMonth,
			String lArgslocationCode, String lArgsBank, String lArgsBranch)
			throws Exception;

	List getMRBill(Date lStrFromDate, Date lStrToDate, String lStrScheme,
			String lStrReportType, String lStrLocCode) throws Exception;

	List getAuditorNameForMRBill(String postId, Long langId) throws Exception;

	ArrayList getAuditor(String lStrLangId, String lstrLocId) throws Exception;

	List getMRTrackingReportSummary(Date lStrFromDate, Date lStrToDate,
			String lStrAuditorName, Long langId, String lStrLocCode)
			throws Exception;

	List getMRTrackingReportDetail(Date lStrFromDate, Date lStrToDate,
			String lStrAuditorName, String lStrStatusType, String lStrLocCode)
			throws Exception;

	List getMinMaxDateForAllAuditor(Date lStrFromDate, Date lStrToDate,
			String lStrAuditorName) throws Exception;

	List getDISDate(Date lStrDate, String lStrLocCode) throws Exception;

	List getDISSubDetails(Date lStrDate, List<Long> strBillLst,
			String lStrLocCode) throws Exception;

	List getBillNumber(String mjh, String smjh, String Mnh, String smnh,
			String dlts, Date lStrDate, String lStrLocCode) throws Exception;

	List getPaidDtlsBetweenTwoDates(String lStrPPONo, Date fromDate,
			Date toDate, String lStrLocCode) throws Exception;

	List getRecoverChallans(String lStrPensionerCode, Integer lForMonth)
			throws Exception;

	List getPensionExpChq_ECSDtls(String lFromDate, String lToDate,
			String lStrLocId) throws Exception;

	List getCommonRptHeader(String lStrLocId, long lLangId) throws Exception;

	List getPPOsToObtainCountForBankWise(Integer lIntFromMonth,
			Integer lIntToMonth, String lStrBank, String lStrBranch,
			String locationCode) throws Exception;

	List getPPOsToObtainCountForBranchWise(Integer lIntFromMonth,
			Integer lIntToMonth, String lStrBank, String lStrBranch,
			String locationCode) throws Exception;

	BigDecimal getMandateSerialNo() throws Exception;

	void setMandateSerialNo(String lStrECSCode, BigDecimal lBgdcMandteSerialNo)
			throws Exception;

	// void resetMandateSerialNo() throws Exception;
	
	public String getEmpNameFromRoleId(String lStrRoleId,String lStrLocCode);
}
