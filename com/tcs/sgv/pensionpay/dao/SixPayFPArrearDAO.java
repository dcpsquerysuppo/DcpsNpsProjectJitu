/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 13, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionSixpayfpArrear;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Jun 13, 2011
 */
public interface SixPayFPArrearDAO extends GenericDao {

	List getAllPnsnrCode();

	List getPPODtls(String lStrPnsnrCode, String lStrHistory);

	String getMonthDescFromMonthId(String lStrMonthId, String lStrLangId);

	Character getMaxRvsnCntr(String lStrPnsnrCode);

	void updateOldPPOEntries(Character lCharRvsnCntr, String lStrPnsnrCode);

	List<TrnPensionSixpayfpArrear> getTrnPensionSixpayfpArrearVOs(String lStrPnsnrCode, Character lCharRvsnCntr);

	List getSixPayFPArrearList(String lStrNoOfInstallment, List<BigDecimal> lLstDaRateFor, Map displaytag, String lStrArrearConfigBy, String lStrPPONo);

	Integer getSixPayFPArrearListCount(String lStrNoOfInstallment, List<BigDecimal> lLstDaRateFor, Map displaytag, String lStrArrearConfigBy, String lStrPPONo);

	List<ComboValuesVO> getAllHeadCodeAndDesc(String lStrLangId);

	String getRopType(String lStrPPONo, String gStrLocationCode);

	List<Integer> getPaidPPOEntries(Character lCharRvsnCntr, String lStrPnsnrCode);

	BigDecimal getPrevInstAmnt(String lStrPnsnrCode, Character lCharRvsnCntr, Integer lIntInstallmentNo);

	void deleteUnPaidSixPayArrearDtls(String lStrPnsrCode) throws Exception;

	List<Object[]> getCashSixPayArrearDtls(String LStrPnsrCode) throws Exception;

	List<Object[]> validatePPONo(String lStrPPONo, Long gLngLangId, String gStrLocationCode) throws Exception;

	void updatePayInMonthByArrearIds(List<Long> lLstArrearIds, String lStrPayInMonth, BigDecimal lBDUserId, BigDecimal lBDPostId, Date lDtUpdate) throws Exception;

	Long getMaxRevision(String lStrPensionerCode) throws Exception;

	void updateSixPayArrearInstlmentDate(String lStrPayInMonth, String lStrLocCode, List<BigDecimal> lLstBgDaRateForState, BigDecimal lBDUserId, BigDecimal lBDPostId, Date lDtUpdate) throws Exception;
	
	String getSixpayfpArrearTotalPaidCashAmt(String lStrPnsnrCode) throws Exception;
	
	String getSupplRequestNoFromPnsnrCode(String lStrPensionerCode, List<Short> lLstStatus, String lStrLocCode) throws Exception;
}
