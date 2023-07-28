/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 13, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.valueobject.MstScheme;
import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.MstPensionHeadcode;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerDtls;
import com.tcs.sgv.pensionpay.valueobject.RltAuditorBank;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 May 13, 2011
 */
public interface PensionConfigDAO extends GenericDao {

	List getAuditorName(Map inputMap, String lStrLocCode) throws Exception;

	List getBankNames(Long lLngLangId, String lStrLocCode) throws Exception;

	Boolean validateMapping(String lStrPostId, String lStrBankCode, String lStrBranchCode, String lStrLocationCode) throws Exception;

	void removeMapping(String lStrPostId, String lStrBankCode, String lStrArrRemoveValue, String StrLocationCode) throws Exception;

	List getMappingDtls(Long lLngAuditorPostId, String lStrBankCode, String gStrLocationCode) throws Exception;

	List getRemarksOfBill(Long lLngBillNo, String gStrLocationCode, Long gLngLangId) throws Exception;

	Long getFinYearIdFromCurrDt(String lStrCurYear) throws Exception;

	List getHeadCodeConfigDtls(Long gLngLangId, Map displayTag) throws Exception;

	Integer getHeadCodeConfigDtlsCount(Long gLngLangId, Map displayTag) throws Exception;

	List getRltPensionHeadcodeChargableVO(String lStrHeadCode) throws Exception;

	MstPensionHeadcode getMstPensionHeadcodeVO(String lStrHeadCode) throws Exception;

	void delRltPensionHeadcodeChargableVO(BigDecimal lBDHeadCode) throws Exception;

	List getHeadCodeNos() throws Exception;

	List getCurrMappingDtls(String gStrLocationCode, Long gLngLangId, String lStrAuditorPostId) throws Exception;

	BigDecimal getMaxHeadCode() throws Exception;

	void removeCtgryIfExists(String gStrLocCode);

	List<Integer> getSelectedChngStmntCatgry(Long lLngLocCode) throws Exception;

	List getPPODetails(String lStrPPONo, String gStrLocationCode) throws Exception;

	List getPPONoHistory(String lStrPensionerCode) throws Exception;

	List<Integer> getChangeStmntCtgry(String lStrLocationCode) throws Exception;

	List<ComboValuesVO> getBankGroupOfAuditor(Long lLngAuditPostId, String lStrLocCode) throws Exception;

	void deleteBankBranchGrpDtls(Long lLngGrpId) throws Exception;

	StringBuilder getBankGroupDtlsByGrpId(Long lLngGrpId, Long lLngPostId, String lStrLocCode) throws Exception;

	String getGrpNameFromBranchCode(String lStrBankCode, String lStrBranchCode, String lStrLocationCode, Long lLngPostId) throws Exception;

	Integer getMainCategoryConfigDtlsCount(Long gLngLangId, Map displayTag) throws Exception;

	List getMainCategoryConfigDtls(Long gLngLangId, Map displayTag) throws Exception;

	Long getMaxMainCatId(Long gLngLangId) throws Exception;

	List getAllMainCtgryId(Long gLngLangId) throws Exception;

	Integer getMstSchemeCount(Long lLngLangId) throws Exception;

	List<MstScheme> getMstSchemeList(Map displayTag, Long lLngLangId) throws Exception;

	List getMainCtgryConfigDtls(Long gLngLangId) throws Exception;

	Long getTotalPnsrsOfBranch(String lStrBranchCode, String lStrBankCode, String lStrLocCode) throws Exception;
	
	Long getAuditorPostId(String lStrBranchCode,String lStrBankCode, String gStrLocationCode) throws Exception;
	
	List getBnkBrnchDtls(List<Long> lLstAuditorPostId,List<Long> lLstBranchCode) throws Exception;
	
	List<RltAuditorBank> getRltAuditorBankVO(String lStrBranchCode) throws Exception;
	
	List<MstPensionerDtls> getMstPensionerDtlsVO(String lStrBranchCode) throws Exception;
	
	List<String> getPensionerCode(String lStrBranchCode) throws Exception;
	
	List<String> checkSchemeCode(String lStrSchemeCode) throws Exception;
}
