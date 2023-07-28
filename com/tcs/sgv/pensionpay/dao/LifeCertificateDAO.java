/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 30, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionerSeenDtls;


/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * May 30, 2011
 */
public interface LifeCertificateDAO extends GenericDao{

	 Integer getLifeCertificateCount(List<String> lLstCaseStatus,Date lDtDate,String lStrLocCode, String lStrSearchCrt,String lStrSearchType, String lStrSearchVal, String lStrPageNo, String lStrBankCode, String lStrBranchCode,String lStrLifeCertFlag,Long lLngPostId) throws Exception;
	
	 
	 /* Before 30 Nov 2012
	 List getLifeCertificateList(Map displayTag,List<String> lLstCaseStatus,Date lDtDate,String lStrLocCode, String lStrSearchCrt,String lStrSearchType, String lStrSearchVal, String lStrPageNo, String lStrBankCode, String lStrBranchCode,String lStrLifeCertFlag,Long lLngPostId) throws Exception;
	 */
	 List getLifeCertificateList(Map displayTag,List<String> lLstCaseStatus,Date lDtDate,String lStrLocCode, String lStrSearchCrt, String lStrSortCrt, String lStrSearchType, String lStrSearchVal, String lStrPageNo, String lStrBankCode, String lStrBranchCode,String lStrLifeCertFlag,Long lLngPostId) throws Exception;
	 
	 List generateLifeCertificate(List<String> lLstCaseStatus,Date lDtDate,String lStrLocCode, String lStrSearchCrt,String lStrSearchType, String lStrSearchVal, String lStrPageNo, List<String> lLstPensionerCode, String lStrBankCode, String lStrBranchCode,String lStrLifeCertFlag,Long lLngPostId) throws Exception;
	
	 String getAtoNameFromLocationCode(String lStrLocCode) throws Exception;
	
	 List getPnsnrDtlsFromPpoNo(String lStrPpoNo, String lStrLocCode) throws Exception;
	
	 Integer getReceivedLifeCertificateCount(String lStrLocCode) throws Exception;
	
	 List validatePpoWithReceivedCert(String lStrLocCode,String lStrPpoNo, Long lLngFinYearId) throws Exception;
	
	 List getReceivedLifeCertificateList(Map displayTag,String lStrLocCode) throws Exception;
	
	 void updatePensionerSeenDtls(List<String> lLstPensionerCode,Long gLngPostId,Long gLngUserId,Date gDate, Long lLngFinYearId) throws Exception;
	 
	 TrnPensionerSeenDtls getTrnPensionerSeenDtls(String lStrPensionerCode) throws Exception;
	 
	 TrnPensionArrearDtls getTrnPensionArrearDtls(String lStrPensionerCode) throws Exception;
}
