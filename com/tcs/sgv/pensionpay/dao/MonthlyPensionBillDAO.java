package com.tcs.sgv.pensionpay.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO;
import com.tcs.sgv.pensionpay.valueobject.MstPensionHeadcode;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerFamilyDtls;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeChargable;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeRate;
import com.tcs.sgv.pensionpay.valueobject.TrnCvpRestorationDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillReceipt;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionChangeDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionChangeHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCutDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.ValidPcodeView;


public interface MonthlyPensionBillDAO {

	List getAllScheme(long langId) throws Exception;

	List<ValidPcodeView> getValidPensionerCode(String bankCode, List<String> lLstBranchCode, Map<String, Object> inputMap, String lStrCaseSatus) throws Exception;

	List<String> getPnsrCodesOfGeneratedChangeStatement(Integer lIntForMonth) throws Exception;

	ArrayList<TrnPensionBillDtls> getTrnPensionBillDtls(String lStrBillId, String lStrForMonth, String flag) throws Exception;

	List<TrnPensionBillHdr> getTrnPensionBillHdr(List<Long> lLstBillNo) throws Exception;

	List getConsolidatedBillHdrDtls(List<Long> lLstBillNo) throws Exception;

	MstPensionerFamilyDtls getMstPensionerFamilyDtls(String lStrPnsnrCode) throws Exception;

	// String isValidBill(String forMonth, String bankCode, String branchCode,
	// String scheme,String lStrPPoNo,Long lLngPostId) throws Exception;
	String getBankName(String lStrBank) throws Exception;

	String getBranchName(String lStrBank, String lStrBranch, String lStrLocCode) throws Exception;

	List getMonthDtls(String lStrMonth, Long langId) throws Exception;

	ArrayList getArrearDtls(String lStrPensionerCode, String lStrForMonth) throws Exception;

	List getArrearDtlsByPnsrCode(String lStrPensionerCode, Integer lIntForMonth) throws Exception;

	List<TrnCvpRestorationDtls> getMonthlyCommutationAmount(String lStrPensionerCode, Integer lIntForMonth) throws Exception;

	List<RltPensionHeadcodeRate> getIRRateFromHeadcodeRateRlt(Long lHeadcode, List<String> lLstFieldType) throws Exception;

	ArrayList getItCutDtls(String lStrPensionerCode, String lStrForMonth) throws Exception;

	Double getRecoveryDtls(String lStrPensionerCode, String lStrStatus, String lForMonth) throws Exception;

	List getBankCode(String lStrBranch, String lStrLocCode) throws Exception;

	List<Long> getBillNo(String lStrDate, String lStrBranch, String lStrScheme, String lStrPPoPara, String lStrLocCode, String lStrAuditor) throws Exception;

	RltPensionHeadcodeChargable getRltPensionHeadcodeChargable(String headcode, String lStrBillType) throws Exception;

	Map<String, Object> getRltPensionHeadcodeChargable(Set<Long> lHeadCodeSet) throws Exception;

	boolean isRejectedBill(String forMonth, String bankCode, String branchCode, String scheme) throws Exception;

	List getRcvrArrCutDtlsForMonth(String lStrPensionerCode, String lStrFlage, String lForMonth, String lArgsLocCode) throws Exception;

	void getRcvrArrCutDtlsMapForMonth(Map inputMap) throws Exception;

	Double getAppliedDP2004DiffAmt(String lStrPPONo, int lPhase1, int lPhase2, int lPhase3, String locationCode) throws Exception;

	List getOldBasicSixPayNewBasic(String lStrPnsnrCode) throws Exception;

	String getChqNoFrmBil(List<Long> lLstBillNo) throws Exception;

	Map getHeaderDtl(String lStrBranchCode, String lStrLocId, long lLangId, List lLstNonBilledBrnchs) throws Exception;

	List getAuditorNonBilledBranches(String lStrDate, Long lBDAuditor, String lStrBank, String gStrLocCode, String lStrScheme) throws Exception;

	List getAuditorBilledBranches(String lStrDate, Long lBDAuditor, String lStrBank, String gStrLocCode, String lStrScheme) throws Exception;

	String getBilStatusFrmBillNo(String lstrBank, String lstrBranch, String lStrScheme, String lStrPPoNo, String lLocCode, Long lPostId, String currDate) throws Exception;

	List<TrnPensionBillReceipt> getHeadCodeWiseBillRcptEdpDtl(String lForMonth, List lPnsnrCodeLst, String lStrPPOList, String lStrLocCode, List<String> lLstOmrPenCode) throws Exception;

	void getAllCVPCutMap(Map inputMap) throws Exception;

	List<TrnPensionChangeHdr> getTrnPensionChangeHdrVOList(Long lLngChangeRqstId) throws Exception;

	List<TrnPensionChangeDtls> getTrnPensionChangeDtlsVOList(Long lLngChangeHdrId) throws Exception;

	List<MstPensionHeadcode> getAllHeadCodeAndDesc() throws Exception;

	List<MonthlyPensionBillVO> getMonthlyPensionBillVOList(Long lLngBillNo, String lStrLocCode, String lStrSortBy) throws Exception;

	List<Object[]> getPnsrAddedToBranchDtls(List<String> lLstPnsrCode, Integer lPrevMonth) throws Exception;

	List<Object[]> getPnsrRemovedFromBranchDtls(List<String> lLstPnsrCode) throws Exception;

	Integer getChangeStatementDtlsCount(String lStrLocationCode, Long lLngAuditorPostId, String lStrSearchCrt, String lStrSearchVal, String lStrSearchBankCode, String lStrSearchBranchCode,
			String lStrShowWLFor) throws Exception;

	List getChangeStatementDtls(String lStrLocationCode, Long lLngAuditorPostId, String lStrSearchCrt, String lStrSearchVal, String lStrSearchBankCode, String lStrSearchBranchCode, Map displayTag,
			String lStrShowWLFor) throws Exception;

	List<MonthlyPensionBillVO> getMonthlyPensionBillVOListForGetData(Long lLngChangeRqstId, String lStrLocCode) throws Exception;

	List<MonthlyPensionBillVO> getMonthlyPensionBillSummary(Long lLngBillNo, String lStrLocCode) throws Exception;

	List getMonthlyDataForAuditRegister(String lStrPensionerCode, String lStrLocationCode) throws Exception;

	List<Object[]> getStatusOfChangeStatement(String lStrLocationCode, List<String> lLstBranchCode, Integer lIntForMonth, Long lLngAuditorPostId) throws Exception;

	List<Object[]> getMonthlyBillDtlsByBillNo(List<Long> lLstLngBillNo) throws Exception;

	void discardChngStmntOnMonthlyBillDiscard(String lStrBankCode, String lStrBranchCode, Integer lIntForMonth) throws Exception;

	List<Object[]> getPrevMonthBillStatus(List<String> lLstBranchCode, Integer lIntForMonth, String lStrLocCode) throws Exception;

	void getMstPensionerFamilyDtlsMap(Map inputMap) throws Exception;

	void getAllArrearMap(Map inputMap) throws Exception;

	void getAllRcvrMap(Map inputMap) throws Exception;

	void getAllSixPayArrearMap(Map inputMap) throws Exception;

	List<Object[]> getPnsrWithPreMonthHeldStatus(List<String> lLstPnsrCode, Integer lPrevMonth) throws Exception;

	Map<String, List<TrnPensionRecoveryDtls>> getRecoveryDtlsForChngStmnt(List<String> lLstPnsrCode, String lStrForMonth) throws Exception;

	public List<BigInteger> getBillNosFromBnkBrnchPayMonth(String lStrbankCode, String lStrBranchCode, Integer lIntForMonth, String lStrLocationCode) throws Exception;

	public List getBankBranchFromAuditor(Integer lIntPostId, String lStrLocationCode) throws Exception;

	List<String> getPnsrCodesOfGeneratedFirstPension(Integer lIntForMonth, String lLocCode) throws Exception;

	List<TrnPensionCutDtls> getMonthlyPensionCutAmount(String lStrPensionerCode, Integer lIntForMonth) throws Exception;

	void getAllPensionCutMap(Map inputMap) throws Exception;

	Map<Long, Object[]> getPartyDtlsByChngRqstIds(List<Long> lLstChngRqstId, String lStrLocCode, Long lLngLangId) throws Exception;

	List<String> getAllSchemeCode() throws Exception;

	String getSchemeNameBySchemeCode(String lStrSchemeCode, String lStrLangId) throws Exception;

	List<TrnPensionChangeHdr> getChngHdrDtlsBySchemeCode(String lStrSchemeCode, String lStrPayMode, String lStrForMonth, String lLocCode) throws Exception;

	List<String> getBranchCodeOfGeneratedBills(String lStrSchemeCode, String lStrPayMode, String lStrForMonth, String lLocCode) throws Exception;

	List<Object[]> getViewMonthlyPensionBillListDtls(String lLocCode, Map displayTag, String lStrSearchCrt1, String lStrSearchCrt2, String lStrSearchCrt1Value, String lStrSearchCrt2Value,
			Integer lIntForMonth) throws Exception;

	Integer getViewMonthlyPensionBillListCount(String lLocCode, String lStrSearchCrt1, String lStrSearchCrt2, String lStrSearchCrt1Value, String lStrSearchCrt2Value, Integer lIntForMonth)
			throws Exception;

	void rejectAllChangeStmntOnRejectOfBill(String lStrForMonth, String lStrLocCode, Long lLngUserId, Long lLngPostId) throws Exception;

	void rejectAllMonthlyBilL(String lStrForMonth, String lStrLocCode, Long lLngUserId, Long lLngPostId) throws Exception;

	List<Object[]> getMonthlyPensionBillOuterDtls(String lStrBillNo, String lStrLocCode, Map displayTag) throws Exception;

	List<Object[]> getMonthlyOuterBillTotalAmts(String lStrBillNo, String lStrLocCode) throws Exception;

	List<Object[]> getSchemeCodeWiseRecovery(String lStrBillNo, String lStrLocCode) throws Exception;

	List<Object[]> getSupplBillSchemeCodeWiseRecovery(String lStrBillNo) throws Exception;

	List<Object[]> getChangeRqstIdByBillNo(BigInteger lBillNo, String lStrLocationCode, String lStrForMonth) throws Exception;

	List<Object[]> getPrevMonthBillDtlVOList(String lStrBranchCode, String lStrLocationCode, Integer lIntPrevForMonth, String lStrSchemeCode) throws Exception;

	List<Object[]> getCurrMnthChngStmntDtlVOList(String lStrChangeRqstId, String lStrSchemeCode) throws Exception;

	List<Object[]> getMonthlyPensionBillInnerDtls(String lStrBillNo, String lStrLocCode, String lStrBankCode, String lStrBranchCode) throws Exception;

	List<Object[]> getBankBranchCategoryWiseSummary(String lStrBillNo) throws Exception;

	List<Object[]> getCategoryWiseSummary(String lStrBillNo) throws Exception;

	List<MonthlyPensionBillVO> getMonthlyPensionBillVOListByChngStmntId(Long lLngChngRqstId, String lStrLocCode, String lStrSortBy) throws Exception;

	List<MonthlyPensionBillVO> getMonthlyPensionBillSummaryByChngStmntId(Long lLngChngRqstId, String lStrLocCode) throws Exception;

	List<String> getChangeStmntRqstIdsForOuterByBillNo(Long lLngBillNo) throws Exception;

	List<Long> getAllBillNoToReject(String lStrForMonth, String lStrLocCode) throws Exception;

	List<Object[]> getAllPensionerCodeOfBill(List<Long> lLstLngBillNo) throws Exception;

	void updateMonthlyPnsnRcryOnChngStmntApproval(List<Long> lLstChngRqstId, String lStrStatus, String lStrLocCode, Long lLngUserId, Long lLngPostId, Date lUpdatedDate) throws Exception;

	List<Long> getChangeRqstIdFromBillNo(List<Long> lLngBillNo) throws Exception;

	List<Object[]> getECSCPBKFileDetails(String lStrLocCode, String lStrForMonth) throws Exception;

	Integer getMonthlyPensionBillOuterDtlsCount(String lStrBillNo, String lStrLocCode) throws Exception;

	List<Object[]> getChangeStmntIdsBYMonthYear(Integer lIntForMonth, String lStrLocCode) throws Exception;

	List<Long> getChangeRqstIdsFromBnkBranch(String lStrbankCode, String lStrBranchCode, Integer lIntForMonth, String lStrLocationCode) throws Exception;

	List<Object[]> getMonthlyPensionBillBankwiseOuterDtls(String lStrBillNo, String lStrLocCode) throws Exception;

	void updateMonthlyBillNoInArrearDtls(Long lLngBillNo, Integer lIntForMonth, Long lLngUpdtUserId, Long lLngUpdtPostId, Date lDtUpdtDate, String lStrLocCode, String lStrSchemeCode,
			Short lShortSubId, String lStrECSBranchFlag) throws Exception;

	void updateMonthlyBillNoForRecovery(List<String> lLstStrPnsrCode, Integer lIntForMonth, String lStrLocCode, Long lLngBillNo, Long gLngUserId, Long gLngPostId, Date gDate, String lStrSchemeCode,
			Short lShortSubId, String lStrECSBranchFlag) throws Exception;

	void saveAllCollection(List lLstValues) throws Exception;

	List<Object[]> getChngStmntBranchSchemeCodeDtls(Integer lIntForMonth, String lStrLocationCode, List<String> lLstBranchCode) throws Exception;

	List<Object[]> getBillPartyDtlsForSchemewiseBill(Integer lIntForMonth, String lStrLocationCode, String lStrSchemeCode, String lStrECSBranchFlag) throws Exception;

	List<Object[]> getPrevMonnthNotGeneratedBillsBranch(Integer lIntForMonth, String lStrLocationCode, List<String> lLstBranchCode) throws Exception;

	List<Object[]> getPrevMonthChangeStmntStatus(Integer lIntForMonth, String lStrLocationCode, List<String> lLstBranchCode) throws Exception;
	
	List<MonthlyPensionBillVO> getMonthlyPensionBillVOListFrmBankBranch(String lStrBankCode, String lStrBranchCode,String lStrForMonthYear, String lStrLocCode, String lStrSortBy)  throws Exception;
	
	List<MonthlyPensionBillVO> getMonthlyPensionBillBankBranchSummary(String lStrBankCode, String lStrBranchCode,String lStrForMonthYear, String lStrLocCode) throws Exception;

}
