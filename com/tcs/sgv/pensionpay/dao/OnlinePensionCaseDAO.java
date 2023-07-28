/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 25, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Mar 25, 2011
 */
public interface OnlinePensionCaseDAO {

	public List getPensionerDetails(String lStrShowCaseFor, Map inputMap, Long lStrPostId, String lStrToRole, List<String> caseStatusList, String strLocCode, Map displayTag, String lStrSearchCrt,
			String lStrSearchVal) throws Exception;

	public List<ComboValuesVO> getBankNames(Map inputMap, Long langId) throws Exception;

	/**
	 * <H3>Description -</H3>
	 * 
	 * 
	 * 
	 * @author Shivani Rana
	 * @param strBankCode
	 * @param lngLangId
	 * @return
	 * @throws Exception
	 */
	public List getBranchsOfBank(String strBankCode, Long lngLangId, String lStrLocCode) throws Exception;

	/**
	 * <H3>Description -</H3>
	 * 
	 * 
	 * 
	 * @author Shivani Rana
	 * @return
	 */
	public List getAuditorName(Map inputMap, String locationCode, String branchCode) throws Exception;

	/**
	 * <H3>Description -</H3>
	 * 
	 * 
	 * 
	 * @author Shivani Rana
	 * @param strLocCode
	 * @return
	 */
	public String getLocationShortNameFromCode(String strLocCode);

	/**
	 * <H3>Description -</H3>
	 * 
	 * 
	 * 
	 * @author Shivani Rana
	 * @param inputMap
	 * @param lngBranchCode
	 * @param lngLangId
	 * @return
	 * @throws Exception
	 */
	public List getBnkBrnchAudiNmeFrmBnkCode(Map<String, Object> inputMap, String locationCode, Long lngBranchCode) throws Exception;

	/**
	 * 
	 * <H3>Description -</H3>
	 * 
	 * 
	 * 
	 * @author Anjana Suvariya
	 * @param lLngPostId
	 * @return
	 * @throws Exception
	 */
	public String getRoleByPost(Long lLngPostId) throws Exception;

	public int getPensionerDetailsCount(String lStrShowCaseFor, Map inputMap, Long lStrPostId, String lStrToRole, List<String> caseStatusList, String locationCode, String lStrSearchCrt,
			String lStrSearchVal) throws Exception;

	public List getAuditorsListFromRoleID(String locationCode, Long langId) throws Exception;
	
	public List validatePPOBeforeArchive(String lStrPpoNo, String lStrLocCode) throws Exception;
	
	public List validatePPOBeforeArchiveWithBill(String lStrPensionerCode) throws Exception;
	
	public void updateCaseStatusAndSendBackToClerk(String lStrPensionerCode,String lStrLocCode,BigDecimal lBgDcmlPostId,BigDecimal lBgDcmlUserId,Date lDtCurrDate) throws Exception;

}
