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
package com.tcs.sgv.pensionproc.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmpNmn;
import com.tcs.sgv.pensionproc.valueobject.SavedPensionCasesVO;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcAdvnceBal;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcAssesdDues;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcCheckList;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcPnsnCalc;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcPnsnrDtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcRecovery;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcRevision;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocAgDtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocAuthorityDtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocAvgPayCalc;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocEventdtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocForeignServ;
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
public interface TrnPnsnProcInwardPensionDAO extends GenericDao {
	List<SavedPensionCasesVO> getAllFrwdCases(String lStrCaseFrom, String gStrPostId, String lStrDraftFlag, Map displayTag,Long lLngDdoCode) throws Exception;

	Integer getAllFrwdCasesCount(String lStrCaseFrom, String gStrPostId, String lStrDraftFlag, Map displayTag,Long lLngDdoCode);

	List<SavedPensionCasesVO> getPensionCaseDtls(String lStrCaseFrom, String lStrDraftFlag, String gStrPostId, Long gLngLangId, String lStrSearchby, String lStrSearchValue,
			Date lDtFrmDate, Date lDtToDate, String lStrSevaarthId, String lStrPPONo, String lStrInwardNo, Date lDtRetiredDate, String lStrName, Long lLngDeparmentTypeId,
			String lStrPensionTypeId, Map displayTag, String lStrBranchName) throws Exception;

	Integer getPensionCaseDtlsCount(String lStrCaseFrom, String lStrDraftFlag, String gStrPostId, Long gLngLangId, String lStrSearchby, String lStrSearchValue, Date lDtFrmDate,
			Date lDtToDate, String lStrSevaarthId, String lStrPPONo, String lStrInwardNo, Date lDtRetiredDate, String lStrName, Long lLngDeparmentTypeId, String lStrPensionTypeId,
			Map displayTag, String lStrBranchName);

	TrnPnsnProcPnsnrDtls getPnsnrDtlsVO(Long lLngInwardPensionId) throws Exception;

	TrnPnsnprocPnsnrpay getPnsnrPayVO(Long lLngInwardPensionId) throws Exception;

	List<TrnPnsnprocPnsnrservcbreak> getPensionCaseSrvcBrkDtls(Long lLngInwardPensionId) throws Exception;

	List<TrnPnsnprocAvgPayCalc> getPensionCaseAvgPayCalcDtls(Long lLngInwardPensionId) throws Exception;

	List<TrnPnsnprocEventdtls> getPensionCaseEventDtls(Long lLngInwardPensionId) throws Exception;

	TrnPnsnProcPnsnCalc getPnsnCalcVO(Long lLngInwardPensionId) throws Exception;

	TrnPnsnProcRecovery getPnsnrRecoveryVO(Long lLngInwardPensionId) throws Exception;

	List<TrnPnsnProcAdvnceBal> getPensionCaseAdvnceBalDtls(Long lLngInwardPensionId) throws Exception;

	List<TrnPnsnProcAssesdDues> getPensionCaseAssesdDueDtls(Long lLngInwardPensionId) throws Exception;

	List<TrnPnsnProcCheckList> getPnsnrCheklistVO(Long lLngInwardPensionId) throws Exception;

	String getLowerLevelForReturn(Long PostId, Long lLngHierarchyRefId) throws Exception;

	List getLowerLevelUserList(String lStrToLevel, Long lLngHierRefId, Long lLngLangId) throws Exception;

	List displayPensionCaseStatus(List<String> lLstStatus, Map displayTag,Long lLngDdoCode) throws Exception;

	Integer displayPensionCaseStatusCount(List<String> lLstStatus, Map displayTag,Long lLngDdoCode) throws Exception;

	String getRoleByPost(Long postId) throws Exception;

	Boolean isWfExists(Long lLngInwardPensionId) throws Exception;

	String getLocCodeFromDDO(String lStrLocationCode) throws Exception;

	BigDecimal getCvpRate(BigDecimal lBDAge, String lStrPayCmsn) throws Exception;

	List<ComboValuesVO> getDesignationForPensionCase(String lStrDesignation,Long lLngLangId) throws Exception;
	
	List<TrnPnsnprocProvisionalPaid> getPnsnprocProvisionalPaidDtls(Long lLngInwardPensionId) throws Exception;
	
	List<TrnPnsnprocForeignServ> getForeignServDtls(Long lLngInwardPensionId) throws Exception;
	
	List<MstEmp> getEmpBasicDtls(String lStrSevaarthId,String lStrDdoCode) throws Exception;
	
	List<MstEmpNmn> getEmpNomineeDtls(Long lLngDcpsEmpId) throws Exception;
	
	String getDdoCodeForDDOAsst(Long lLngAsstPostId);
	
	List getEmpGpfOrDcpsAccNo(String lStrSevaarthId) throws Exception;
	
	String getDeptName(Long lLngLocId) throws Exception;
	
	List getPayScaleDescFromScaleId(Long lLngScaleId) throws Exception;
	
	BigInteger getProvisionalPensionSum(Long lLngInwardId) throws Exception;
	
	String getTresuryNameFormDDOCode(String lStrDdoCode)  throws Exception;
	
	String getLookupNameFromLookupId(Long lLngLookupId)  throws Exception;
	
	String getBranchNameFromBrachCode(String lStrBranchId)  throws Exception;
	
	String getBankNameFromBankCode(String lStrBankId)  throws Exception;
	
	Long getApprovedCaseForRevision(String lStrSevaarthId,Long lLngDdoCode)  throws Exception;
	
	Long isDdoOrDdoAsst(Long lLngPostId);
	
	String getDdoCodeForDDO(Long lLngPostId);
	
	Long getReqPenddingStatus(String lStrSevaarthId,Long lLngDdoCode)  throws Exception;
	
	Long getPnsnRevisionId(String lStrSevaarthId) throws Exception;
	
	Long getRevisionCount(String lStrSevaarthId) throws Exception;
	
	List<TrnPnsnprocAuthorityDtls> getAuthorityDtls(Long lLngInwardPensionId) throws Exception;
	
	TrnPnsnprocAgDtls getPnsnAgDtls(Long lLngInwardPensionId) throws Exception;
}
