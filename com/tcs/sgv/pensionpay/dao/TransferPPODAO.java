/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 27, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.HstPnsnPmntPpoNo;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 May 27, 2011
 */
public interface TransferPPODAO extends GenericDao {

	List getNameAndTreasuryFromPPONo(String lStrPPONo, Long gLngLangId, String lStrLocationCode) throws Exception;

	List getAllTreasuryName(long langId) throws Exception;

	void updateMstPensionerDtls(String lStrPensionerCode) throws Exception;

	void updateTrnPensionRqstHdr(String lStrPensionerCode, String lStrFlagValue, String lStrLocationCode, Long lLngUserId, Long lLngPostId, Date lDtUpdated);

	void updateMstPensionerDtls(String lStrPensionerCode, String lStrBankCode, String lStrBranchCode, String lStrAccNo, String lStrFlagVal) throws Exception;

	List getTransferCases(Long gLngLangId, String gStrLocationCode, String lStrCmbSearchBy, String lStrSearchVal, String lStrTransferCase, Map displaytag) throws Exception;

	Integer getTransferCasesCount(Long gLngLangId, String gStrLocationCode, String lStrCmbSearchBy, String lStrSearchVal, String lStrTransferCase, Map displaytag) throws Exception;

	String getRegNoFromMstPensionerDtls(String lStrPensionerCode) throws Exception;

	Long getAuditorPostIdFromBnkBrnchCode(String lStrBranchCode) throws Exception;

	List getAllBrnchCodes(String lStrLocationCode) throws Exception;

	List getAllPPONo() throws Exception;

	String getPensionerCodeFromPpoNo(String lStrArrOldPPONo) throws Exception;

	HstPnsnPmntPpoNo setDtlsInHstPnsnPmntPpoNo(HstPnsnPmntPpoNo lObjHstPnsnPmntPpoNo, Map inputMap, String lStrLocationCode, String lStrNewPPONo, String lStrOldPPONo, String lStrPensionerCode)
			throws Exception;

	List<String> getAllPnsnrCodes(String lStrLocationCode) throws Exception;

	List getTransferLetterDetails(String lStrPnsnrCode, String lStrLocationCode, String lStrOtherStateName) throws Exception;

	String getDistrictName(String lStrTreasuryName) throws Exception;

	List getBillAmntDtls(String lStrPnsnrCode,List<Short> lLstBillStatus) throws Exception;

	String checkTransferType(String lStrPnsnrCode, String lStrLocationCode, String lStrRequestId) throws Exception;

	List getDatesFromTrnPensionRqstHdr(String lStrPensionerCode) throws Exception;

	Long getMaxRequestId(String locationCode) throws Exception;

	List getTransferDtlsFromReqId(String lStrRequestId, String lStrLocationCode, String lStrOtherStateName) throws Exception;

}
