/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 20, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.valueobject.MstScheme;
import com.tcs.sgv.common.valueobject.RltBankBranch;
import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pension.valueobject.TrnPrvosionalPensionDtls;
import com.tcs.sgv.pensionpay.valueobject.HstCommutationDtls;
import com.tcs.sgv.pensionpay.valueobject.HstPnsnPmntDcrgDtls;
import com.tcs.sgv.pensionpay.valueobject.HstReEmploymentDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerFamilyDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerNomineeDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnCvpRestorationDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCorrectionDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCutDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionerRivisionDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnProvisionalPensionDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnProvisionalVoucherDtls;


/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 Apr 20, 2011
 */
public interface PhysicalCaseInwardDAO extends GenericDao {

	/* Method to get all headcode */
	List<ComboValuesVO> getAllHeadCode() throws Exception;

	/* Method to get description of headcode */
	String getHeadCodeDesc(String lStrHeadCode, Long lLngLangId) throws Exception;

	TrnPensionRqstHdr getTrnPensionRqstHdrVO(String lStrPensionerCode) throws Exception;

	MstPensionerHdr getMstPensionerHdrVO(String lStrPensionerCode) throws Exception;

	List<TrnProvisionalPensionDtls> getTrnProvisionalPensionDtlsVO(String lStrPensionerCode) throws Exception;

	MstPensionerDtls getMstPensionerDtlsVO(String lStrPensionerCode) throws Exception;

	List<MstPensionerFamilyDtls> getMstPensionerFamilyDtlsVO(String lStrPensionerCode) throws Exception;

	List<MstPensionerNomineeDtls> getMstPensionerNomineeDtlsVO(String lStrPensionerCode) throws Exception;

	List<MstPensionerNomineeDtls> getMstPensionerNomDtlsVO(String lStrPensionerCode) throws Exception;

	List<TrnProvisionalVoucherDtls> getTrnProvisionalVoucherDtlsVO(String lStrPensionerCode) throws Exception;

	List getTrnPensionRecoveryDtlsVO(String lStrPensionerCode, List<String> lLstRecoveryFrom) throws Exception;
	
	List<TrnPensionRecoveryDtls> getTrnPensionRecoveryDtlsForDelete(String lStrPensionerCode, List<String> lLstRecoveryFrom) throws Exception;

	List<TrnCvpRestorationDtls> getTrnCvpRestorationDtlsVO(String lStrPensionerCode) throws Exception;

	List<HstReEmploymentDtls> getHstReEmploymentDtlsVO(String lStrPensionerCode) throws Exception;

	List getHstCommutationDtlsVO(String lStrPensionerCode) throws Exception;

	List<HstPnsnPmntDcrgDtls> getDcrgHistory(String lStrPensionerCode) throws Exception;

	TrnPensionRecoveryDtls getRecoveryDtlsFromRecoveryType(String lStrPensionerCode, String lStrRecoveryType) throws Exception;

	TrnPensionerRivisionDtls getTrnPensionerRivisionDtlsVO(String lStrPensionerCode) throws Exception;

	void updatePensionCaseStatus(String lStrPensionerCode) throws Exception;

	BigDecimal getRltPensionHeadcodeRate(String lStrHeadCode, String lStrTypeFlag) throws Exception;

	String getIfscCodeFromBranchCode(Long lStrBranchCode, String lStrLocationCode) throws Exception;

	List<String> isValidPPONo(String lStrPPONo,List<String> lLstLocCode) throws Exception;

	String getTreasuryName(Long lLngLangId, Long lLngLocationCode) throws Exception;

	List<TrnPensionCorrectionDtls> getCorrectedValuesByPensionerCode(String lStrPensionerCode, String lStrLocCode) throws Exception;

	void updateCorrectionDtlsByPensionerCode(List<Long> lLstPensionerCode, String lStrApproveStatus, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception;

	List<ComboValuesVO> getAllTreasury(List<Long> lLstDepartmentId, Long lLngLangId) throws Exception;

	List getPnsnrDtlsFromPnsnrCode(String lStrPensionerCode, String lStrLocationCode, Long lLngLangId) throws Exception;

	List<String> isValidSchemeCode(String lStrSchemeCode, String lStrSchemeType) throws Exception;

	String getBankName(Long lLngBankCode) throws Exception;

	String getBranchName(Long lLngBranchCode, String lStrLocCode) throws Exception;

	String getDesignationName(Long lLngDesignationId, Long lLngLangId) throws Exception;

	String getDepartmentName(Long lLngDepartmentId, Long lLngLocId, Long lLngLangId) throws Exception;

	String getStateName(Long lLngStateId, Long lLngLangId) throws Exception;

	String getDistrictName(String lStrDistrictCode, Long lLngLangId) throws Exception;

	List<ComboValuesVO> getOfficeListForAutoComplete(String lStrOfficeName) throws Exception;

	List<ComboValuesVO> getDesignationForAutoComplete(String lStrDesignation) throws Exception;

	List<TrnPrvosionalPensionDtls> getProvisionalPensionDtls(String lStrPensionerCode) throws Exception;

	Integer getMstSchemeCount(Long lLngLangId, String lStrSchemeCode, String lStrSchemeType) throws Exception;

	List<MstScheme> getMstSchemeList(Map displayTag, Long lLngLangId, String lStrSchemeCode, String lStrSchemeType) throws Exception;

	List getPensionCaseRemarks(String lStrPensionerCode, String lStrLocCode, Long lLngLangId) throws Exception;

	List<TrnPensionCutDtls> getPensionCutDtls(String lStrPensionerCode) throws Exception;
	
	void deleteCommutationHistory(List<Long> lLstCvpDtlsId) throws Exception;
	 
	HstPnsnPmntDcrgDtls getHstPnsnPmntDcrgDtlsVO(String lStrPensionerCode)  throws Exception;
	
	List getTrnPensionRecoveryDtlsForDCRGHstry(String lStrPensionerCode)  throws Exception;
	
	List getTrnPensionRecoveryDtlsForDCRGHstryPopUp(String lStrPensionerCode)  throws Exception;
	
	List getHstDcrgDtlsId(String lStrPensionerCode) throws Exception;
	
	void removeHstPnsnPmntDcrgDtlsAndRecoveryDtls(List<Long> lLstDcrgDtlsId) throws Exception;
	
	RltBankBranch getBankBranchFrmPaymentScheme(String lStrLocationCode) throws Exception;
	
	HstPnsnPmntDcrgDtls getHstDcrgDtlsFrmBillNo(Long lLngBillNo,String lStrPensionerCode) throws Exception;
	
	HstCommutationDtls getHstCommutationDtlsFrmBillNo(Long lLngBillNo,String lStrPensionerCode) throws Exception;
	
	TrnPensionArrearDtls getTrnPensionArrearDtlsVO(String lStrPensionerCode,String lStrArrearType) throws Exception;
	
	List<TrnPensionArrearDtls> validateArrearDtls(String lStrPensionerCode,String lStrArrearType,String lStrPayInMonthYear) throws Exception;
	
	List validateMajorHead(String lStrMajorHead,String lStrSchemeType) throws Exception;
	
	List<MstScheme> getSchemeCodeListFrmMajorHead(String lStrMajorHead, String lStrSchemeType) throws Exception;
	
	String getSchemeNameFromSchemeCode(String lStrSchemeCode) throws Exception;
	
	String getPensionerCodeFromPpoNo(String lStrPpoNo,String lStrLocationCode, String lStrCaseStatus) throws Exception; 
	
	List getBillDtlsFromPensionerCode(String lStrPensionerCode) throws Exception; 
	
	List getPnsnrDtlsFromForUpdateBankDtls(String lStrPensionerCode,String lStrLocationCode, String lStrCaseStatus) throws Exception; 
	
	void updateBankBranchDtlsForMigratedPpo(String lStrPensionerCode, String lStrLocationCode,String lStrBankCode, String lStrBranchCode, BigDecimal lBgDcmlPostId,BigDecimal lBgDcmlUserId,Date lDtCurrDate) throws Exception; 
	
	void updateCaseOwnerForMigratedPpo(String lStrPensionerCode, String lStrLocationCode,BigDecimal lBgDcmlCaseOwner, BigDecimal lBgDcmlPostId,BigDecimal lBgDcmlUserId,Date lDtCurrDate) throws Exception; 
}
