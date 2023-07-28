package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.SgvaMonthMst;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeChargable;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCorrectionDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstTracking;


public interface CommonPensionDAO {

	List getAuditorBankCodeList(Long lPostId, String lStrLocCode) throws Exception;

	List getAuditorBranchCodeList(Long lPostId, String lStrAuditorBankCode, String lStrLocCode) throws Exception;

	List getAllHeadCode() throws Exception;

	TrnPensionRqstHdr getTrnPensionRqstHdrDtls(String lStrPPONO, String lStrStatus, String lStrArgsLocCode) throws Exception;

	String getAllHeadCodeDesc(String strHeadCode, long langId) throws Exception;

	List getPensionerDtlsfromPpoNo(String ppoNo, String lStrLocCode, Integer currCaseStatus) throws Exception;

	List getAllState(long langId) throws Exception;

	List getDistrictsOfState(BigDecimal stateId, long langId) throws Exception;

	List getAllBank(String bankCodeFrmRequest, Long langId, String locCode) throws Exception;

	List getBranchsOfBank(String bankCode, Long langId, String locCode) throws Exception;

	List getAllDepartment(long langId, Long lLngDeptId) throws Exception;

	List getAllDesignation(String lStrDesigCode, long langId) throws Exception;

	List getSubTreasurysOfTreasury(String locCode, long langId) throws Exception;

	List getAllTreasury(long langId, String locCode) throws Exception;

	List getHodFromDepartmet(BigDecimal strDepCode, long langId, Long lLngHodId) throws Exception;

	List<SgvaMonthMst> getSgvaMonthMstVO(String lStrLangId) throws Exception;

	String getMonthNo(String lStrLangId, String lStrMonthName) throws Exception;

	List<SgvcFinYearMst> getSgvcFinYearMstVO(String lStrLangId) throws Exception;

	List getBranchByBranchId(String branchCode, Long langId, String locCode) throws Exception;

	List getBillTypeFromHeadCode(BigDecimal lBDHeadCode) throws Exception;

	List getAllBranchsForLocation(String langId, String locationCode) throws Exception;

	String getPensionerCodefromPpoNo(String ppoNo, String lStrLocCd) throws Exception;

	String getBankCodeForPensioner(String lStrPensionerCode, String lStrBranchCode) throws Exception;

	List<TrnPensionRqstHdr> getPensionerDtlsDiffFromPpoNo(String ppoNo, String lStrLocCode) throws Exception;

	List getCurrentUserFromPPO(String lstrPPONo, Long lLngLangId, String lStrLocCode) throws Exception;

	int getLastBillCreatedMonth(String lStrPensionerCode) throws Exception;

	List getTiAndMdeAllowFromByHeadCode(String lStrHeadCode, String lStrRetDate, String lStrTypeFlag, String lStrBasicPension) throws Exception;

	List getSanctionAuthPrefix(Long lLngId) throws Exception;

	String getPrifixSancAuth(Long authorityCode, Long lLngLangId);

	List getPayScaleList(Map inputMap, Long lLngId) throws Exception;

	String getLocCodeFromRltPPOTrsryMapping(String lLngLocCode) throws Exception;

	List getTiRateForSpecialHeadCode(String lStrHeadCode, String lStrRetDate, String lStrBasic) throws Exception;

	Integer getLastMonth(String lStrPnsnrCode) throws Exception;

	List getAllAuditor(String lStrLocationCode, Long lLngLangId) throws Exception;

	List getFormIssueAuthCombo(Long lLngLangId) throws Exception;

	String getParentLocFromLocation(String lStrLocId, Long lLngID) throws Exception;

	int checkForFirstPayment(String lStrPensionerCode) throws Exception;

	String getLocationNameAndAddress(String lStrLocId, Long lLngID) throws Exception;

	Map<String, String> getHeadCodeDesc(Object[] lLstHeadCode, long langId) throws Exception;

	List getSeriesData() throws Exception;

	List getAuditorPostIdByBranchCode(String lStrBranchCode, String lStrLocCode) throws Exception;

	List getAllDiseases() throws Exception;

	List getAllHospitals(String lStrLocationCode) throws Exception;

	String getBranchCodeFromPPONo(String lStrPPONo, String lStrLocCode) throws Exception;

	List<ComboValuesVO> getPPONo(String lStrPPO, String lStrLocation) throws Exception;

	List<TrnPensionRqstTracking> getSavedPensionRequests(BigDecimal lintStatus) throws Exception;

	List getPPONoandName(String lStrPensionerCode, String lStrLocCode) throws Exception;

	String getAuthorityNameFrmAuthorityCode(String lStrAuthorityCode) throws Exception;

	String getAuditorName(String lStrChangeItem, String lStrItemFlag) throws Exception;

	List<TrnPensionCorrectionDtls> getCorrectedValuesByppoNo(String lStrPpoNo, String lArgsLocCode) throws Exception;

	String getAllBankParty(String bankCodeFrmRequest, Long langId, String locCode) throws Exception;

	public List getRevisionBillDtlsForRectify(String lStrLocCode, String lStrPPONO) throws Exception;

	public void updateRectifyBillDtls(String lStrBillId, String lStrPPONo, String lStrPayMode, String lStrNewPayAmt, String lStrLocCode, String lStrBillType, Long gLngPostId, Long gLngUserId,
			Date gDate) throws Exception;

	public void deleteRectifyBillDtls(String lStrBillId, String lStrPPONo, String lStrLocCode, String lStrBillType) throws Exception;

	void updatePensionRemarks(String lStrRemarks) throws Exception;

	Map<String, List<CmnLookupMst>> getPartialLookupVo(List lLstLookUpNames, Long LLangId) throws Exception;

	Map getfulllLookupVo(List lLstLookUpNames, Long LLangId) throws Exception;

	List getBranchsOfAuditorBank(String bankCodeFrmRequest, String auditorPostIdFrmRequest, Long lngLangId, String locationCode) throws Exception;

	public String getAtoNameFromLocationCode(String lStrLocCode) throws Exception;

	Map<BigDecimal, String> getAllHeadCodeSeriesMap() throws Exception;

	String getRoleByElement(String lStrElementCode) throws Exception;

	Map<String, String> getPaymentSchemeCodeMap(List<String> lLstSchemeCode) throws Exception;

	Map<Long, RltPensionHeadcodeChargable> getRltPensionHeadcodeChargableDtls(String lStrBillType) throws Exception;

	String getGeneratedIdForLibraryNo(String lStrLibraryNo, String lStrLocationCode) throws Exception;

	void updateGeneratedIdForLibraryNo(String lStrLibraryNo, String lStrLocationCode, Integer lIntGeneratedId) throws Exception;

	Long getMaxLetterNo(Integer lIntLocId, Integer lIntFinYearId, String lLetterType) throws Exception;

	List<Object[]> getAGCircleDtlFromLocationCode(String lStrLocCode) throws Exception;

	List<ComboValuesVO> getDaRateFromPayCommissionStateDept(String lStrPayCommissionType, BigDecimal lBDDaForState) throws Exception;
	
	String getRoleByPost(Long lLngPostId) throws Exception;
	
	List<String> getLocationListOfAgCircle(String lStrLocCode) throws Exception;
}
