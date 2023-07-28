/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 16, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.report;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Aug 16, 2011
 */
public interface PensionpayQueryDAO {

	public List<TrnPensionBillDtls> getPensionAllocationDetails(Integer lIntForMonth, String lStrBranchCode, String lStrHeadCode) throws Exception;

	public List getPensionCaseTrackingReport(ReportVO lObjReport, String lStrLocationCode, String lStrFromDate, String lStrToDate, String lStrTreasuryName, String lStrBankName, String lStrBranchName,
			String lStrPensionerName, String lStrAccountNumber, String lStrPpoNo) throws Exception;

	public List getBillTrackingReport(ReportVO lObjReport, String lStrLocationCode, String lStrFromDate, String lStrToDate, String lStrBillNo, String lStrBillType) throws Exception;

	public String getEmpNameFromRoleId(String lStrRoleId, String lStrLocCode) throws Exception;

	public BigDecimal getMandateSerialNo() throws Exception;

	public void setMandateSerialNo(String lStrECSCode, BigDecimal lBgdcMandteSerialNo) throws Exception;

	void setReportHeaderAndFooter(ReportVO lObjReport, LoginDetails lObjLoginVO) throws Exception;

	List getBankBranchWisePensionerCount(String lStrBankCode, String lStrBranchCode, String lStrLocationCode) throws Exception;

	List getPensionerCountForMonth(String lStrBankCode, String lStrBranchCode, String lStrForMonth, String lStrLocationCode) throws Exception;

	List getArrearDtlsBankBranchWise(String lStrBankCode, String lStrBranchCode, String lStrForMonth, String lStrLocationCode) throws Exception;

	List getRecoveryReport(String lStrBankCode, String lStrBranchCode, String lStrForMonth, String lStrSchemeCode, String lStrLocationCode) throws Exception;

	List getSixPayArrearDtls(String lStrBankCode, String lStrBranchCode, String lStrPpoNo, String lStrLocationCode) throws Exception;

	List getFirstPmntCases(String lStrTreasuryCode, String lStrPPONo) throws Exception;

	List getArchivedCases(String lStrTreasuryCode, String lStrPPONo) throws Exception;

	List<Object[]> getBankPaymentDtlsForMonthly(String lStrBankCode, String lStrBranchCode, String lStrForMonthYear, String lStrAudPostId, String lStrLocationCode, String lStrAudBankFlag,
			List<String> lLstPaymentMode, String lStrExportTo, Date lDtFromDate, Date lDtToDate, String lStrBillType, String lStrSchemeCode, String lStrOrderBy, String lStrReportingBankCode,
			String lStrReportingBranchCode) throws Exception;

	List<Object[]> getBankPaymentDtls(String lStrBankCode, String lStrBranchCode, String lStrForMonthYear, String lStrAudPostId, String lStrLocationCode, String lStrAudBankFlag,
			List<String> lLstPaymentMode, String lStrExportTo, Date lDtFromDate, Date lDtToDate, String lStrBillType, String lStrOrderBy) throws Exception;

	List getSchemeWisePaymentDtls(String lStrForMonthYear, String lStrSchemeCode, String lStrLocationCode) throws Exception;

	List getBankWisePaymentDtls(String lStrForMonthYear, String lStrSchemeCode, String lStrBankCode, String lStrLocationCode) throws Exception;

	List getBranchWisePaymentDtls(String lStrForMonthYear, String lStrSchemeCode, String lStrBankCode, String lStrBranchCode, String lStrLocationCode) throws Exception;

	StringBuilder getReportHeader(String lStrLocCode) throws Exception;

	List getAGFirstPayStatement(Date lDtFromVoucherDate, Date lDtToVoucherDate, Long lLngTreasuryCode, String lStrPPONo) throws Exception;

	List getCVPPaymentDtls(Date lDtFromVoucherDate, Date lDtToVoucherDate, Long lLngTreasuryCode, String lStrPPONo) throws Exception;

	List getDCRGPaymentDtls(Date lDtFromVoucherDate, Date lDtToVoucherDate, Long lLngTreasuryCode, String lStrPPONo) throws Exception;

	List<ComboValuesVO> getVoucherNo(Integer lIntForMonth, String lStrTreasuryCode) throws Exception;

	List<ComboValuesVO> getYearListForMonthlyPenReport(String lStrLangId) throws Exception;

	List<Object> getMonthlyPenDtlsReport(Integer lIntForMonth, String lStrLocCode, String lStrVoucherNo, String lStrLangId) throws Exception;

	List<Object> getMonthlyPenRecoveryReport(Integer lIntForMonth, String lStrLocCode, String lStrVoucherNo, String lStrLangId) throws Exception;

	List<Object> getMonthlyPenAllocationReport(Integer lIntForMonth, String lStrLocCode, String lStrVoucherNo, String lStrLangId) throws Exception;

	List getAuditorwisePensionerCountForMonth(String lStrAudPostId, String lStrForMonth, String lStrLocationCode) throws Exception;

	List getChangeStatementData(Long lLngAudPostId, Integer lIntForMonth, String lStrLocationCode, String lStrLangId) throws Exception;

	List getPensionerACNumber(String lStrLocationCode, String lStrBankCode, String lStrBranchCode, Long lLngLangId) throws Exception;
	
	List getRevisedAgCases(Date lDtAcceptFromDate, Date lDtAcceptToDate, String lStrLocCode) throws Exception;
	
	List getRegisterOfPpoReceived(Date lDtInwardFromDate, Date lDtInwardToDate,String lStrPpoNo, String lStrLocCode,List<String> lLstCaseStatus,String lStrCaseStatus) throws Exception;
	
	List getRegisterOfGratuityOrderReceived(Date lDtOrderFromDate, Date lDtOrderToDate,List<Short> lLstBillStatus, String lStrLocCode) throws Exception;
	
	List getRegisterOfCvpOrderReceived(Date lDtOrderFromDate, Date lDtOrderToDate,List<Short> lLstBillStatus, String lStrLocCode) throws Exception;
	
	List getPensionDtlsReport(Date lDtFromVoucherDate,Date lDtToVoucherDate,Long lLngTreasuryCode, String lStrPPONo, String lStrReportType, String lStrLangId);
	
	List getPensionRecoveryReport(Date lDtFromVoucherDate,Date lDtToVoucherDate,Long lLngTreasuryCode, String lStrPPONo, String lStrReportType, String lStrLangId);
	
	List getPensionAllocationReport(Date lDtFromVoucherDate,Date lDtToVoucherDate,Long lLngTreasuryCode, String lStrPPONo, String lStrReportType, String lStrLangId);

	List getChangeInTrsryData(Date lDtRequestGenFromDate,Date lDtRequestGenToDate,String lStrLocCode, Long lLngLangId) throws Exception;
	
	Integer getAuditRegisterRptPpoCount(String lStrFinYearId,Date lDtFinYearFromDate,Date lDtFinYearToDate,String lStrLocCode, String lStrPostId) throws Exception;
	
	List getAuditRegisterRptPpoList(Map displayTag,String lStrFinYearId,Date lDtFinYearFromDate,Date lDtFinYearToDate,String lStrLocCode, String lStrPostId) throws Exception;
	
	List getAuditRegisterRptPnsnrDtls(String lStrPensionerCode, String lStrLocationCode, Long lLngLangId) throws Exception;
	
	List getAuditRegisterRptCvpDtls(String lStrPensionerCode, String lStrLocationCode,List<Short> lLstBillStatus) throws Exception;
	
	List getAuditRegisterRptGratuityDtls(String lStrPensionerCode, String lStrLocationCode,List<Short> lLstBillStatus) throws Exception;
	
	List getAuditRegisterRptPaymentHistory(String lStrPensionerCode,String lStrFromMonthYear,String lStrToMonthYear,List<Short> lLstBillStatus) throws Exception;
	
	List getBankBranchDtlsFromPnsnrCode(String lStrPensionerCode) throws Exception;
	
	List getOverPaymentRecoveryDtls(Integer lIntFromMonthYear, Integer lIntToMonthYear, List<Short> lLstBillStatus, List<String> lLstRecoveryType,String lStrLocCode,Long gLngLangId);
	
	List getStatePensionStatisticsDtls(Integer lIntForMonthYear,List<Short> lLstBillStatus,List<Long> lLstSubjectId) throws Exception;
	
	List getTrsyWiseStatePensionStatisticsDtls(Integer lIntForMonthYear,List<Short> lLstBillStatus,List<Long> lLstSubjectId) throws Exception;
	
	List getPnsnTypeWisePensionStatisticsDtls(Integer lIntForMonthYear,List<Short> lLstBillStatus,List<Long> lLstSubjectId,String lStrLocCode) throws Exception;
	
	List getDeptWiseAgingAnalysisDtls(String lStrRetirementYear,Long lLngDepartmentId, Long lLngLangId) throws Exception;
	
	List getTrsryDeptWiseAgingAnalysisDtls(String lStrRetirementYear,String lStrDepartmentId,String lStrPensionType) throws Exception;
	
	List getPnsnrTrsryDeptWiseAgingAnalysisDtls(String lStrRetirementYear,String lStrDepartmentId,String lStrPensionType,String lStrLocCode) throws Exception;
	
	List getTrsryWiseAgingAnalysisDtls(String lStrRetirementYear) throws Exception;
	
	List getTrsryPnsnrWiseAgingAnalysisDtls(String lStrRetirementYear,String lStrLocationCode,String lStrPensionType) throws Exception;
	
	List getCasesUploadedByAGDtls() throws Exception;
	
	List getTrsrywiseCasesUploadedByAGDtls(String lStrLocationCode) throws Exception;
	
	Integer getTotalNonIdentifiedPnsnr(String lStrStatus,List<String> lLstCaseStatus) throws Exception;
	
	List getTrsryWiseNonIdentifiedPnsnr(String lStrStatus,List<String> lLstCaseStatus) throws Exception;
	
	List getPnsnrWiseNonIdentifiedPnsnr(String lStrStatus,List<String> lLstCaseStatus,String lStrLocationCode) throws Exception;
	
	List getTrsryWiseRevisedPpoByAGDtls() throws Exception;
	
	List getTrsryWiseNonRevisedPpoByAGDtls() throws Exception;
	
	List getPnsnrWiseRevisedPpoByAGDtls(String lStrLocationCode,String lStrRevisionFlag) throws Exception;
	
	List getPayComWisePensionerDtls(String lStrLocationCode,String lStrRopType) throws Exception;
	
	List getMonthlyRecoveryDtls(Integer lIntFromMonthYear, Integer lIntToMonthYear, List<String> lLstRecoveryType,List<Short> lLstBillStatus,String lStrLocCode);
			
}
