/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 28, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.common.dao;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;

import com.tcs.sgv.common.valueobject.MstScheme;


/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 Apr 28, 2011
 */
public interface CommonDAO {

	List<ComboValuesVO> getAllDesignation(Long lLngLangId) throws Exception;

	List<ComboValuesVO> getAllDepartment(Long lLngDepartmentId, Long lLngLangId) throws Exception;

	List<ComboValuesVO> getAllState(Long lLngLangId) throws Exception;

	List<ComboValuesVO> getDistrictsOfState(Long lLngStateId, Long lLngLangId) throws Exception;

	List<ComboValuesVO> getCityList(Long lLngLangId) throws Exception;

	List<ComboValuesVO> getBankList(Map inputMap, Long lLngLangId) throws Exception;

	List<ComboValuesVO> getBranchListFromBankCode(Long lLngBankCode, String lStrLocCode, Long lLnglangId) throws Exception;

	List<ComboValuesVO> getMonthList(String lStrLangId) throws Exception;

	List<ComboValuesVO> getYearList(String lStrLangId) throws Exception;

	List isIFSCCodeExsist(String lStrBranchCode) throws Exception;

	String getRoleByPost(Long lLngPostId) throws Exception;

	String getAuditorNameByPostId(Long lLngPostId) throws Exception;

	String getTreasuryName(Long lLngLangId, Long lLngLocationCode) throws Exception;

	String getBankNameFromBankCode(String lStrBankCode, Long lLngLandId) throws Exception;

	String getBranchNameFromBranchCode(String lStrBankCode, String lStrBranchCode, Long lLngLandId) throws Exception;

	List<ComboValuesVO> getAllTreasury(Long lLngLangId, List<Long> lLstDepartmentId) throws Exception;

	List getBranchsOfBank(String bankCode, Long langId, String lStrLocCode) throws Exception;

	List getReportingBankList(String lStrLocCode, Long langId) throws Exception;

	List getReportingBranchListFromReportingBank(String lStrLocCode, Long langId, String lStrReportingBankCode) throws Exception;

	List getBankBranchNameFromBranchCode(String lStrLocCode, Long langId, List<Long> lLstBranchCodes) throws Exception;

	String getEmpNameByPostId(Long lLngPostId) throws Exception;

	BigInteger getCurrentSeqId(String lStrLocCode, String lStrTableName) throws Exception;

	void updateTableSeqByCount(String lStrLocCode, String lStrTableName, Long lLngCount) throws Exception;
	
	List<ComboValuesVO> getFinYearDesc(String lStrLangId, String lStrLocCode) throws Exception;
	
	SgvcFinYearMst getFinYearDtlsFromFinYearId(String lStrFinYearId) throws Exception;
	
	MstScheme getHeadDtlsBySchemeCode(String lStrSchemeCode) throws Exception;
}
