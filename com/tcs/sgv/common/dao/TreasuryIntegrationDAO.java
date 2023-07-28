/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 21, 2012		Vrajesh Raval								
 *******************************************************************************
 */
package com.tcs.sgv.common.dao;

import java.util.List;
import java.util.Map;
import java.math.BigInteger;

import com.tcs.sgv.common.valueobject.MstIntegrationBillTypes;
import com.tcs.sgv.core.dao.GenericDao;


/**
 * Class Description -
 * 
 * 
 * @author Vrajesh Raval
 * @version 0.1
 * @since JDK 5.0 Aug 21, 2012
 */
public interface TreasuryIntegrationDAO extends GenericDao {

	Map<String, Long> getPensionBillSchemewiseRecoveryDtls(Long lLngBillNo, long lSubjectId) throws Exception;

	Map<String, Long> getDCRGBillSchemewiseRecoveryDtls(Long lLngBillNo) throws Exception;

	Integer getBeneficiaryCountOfBill(Long lLngBillNo) throws Exception;

	Map<Long, Object[]> getBeamsAuthDtls(List<Long> lLstBillNo) throws Exception;

	// Object[] validateBEAMSBillAuthNo(Long lLngBillNo) throws Exception;
	Object[] validateBEAMSBillAuthNo(Long lLngBillNo,String authNo) throws Exception;
	

	List<Object[]> getPensionBillSummaryForCMP(Long lLngBillNo) throws Exception;

	List<Object[]> getMonthlySuppBillBeneficiaryDtls(Long lLngBillNo) throws Exception;

	List<Object[]> getFirstPayBillBeneficiaryDtls(Long lLngBillNo) throws Exception;
	
	MstIntegrationBillTypes getBEAMSBillTypeDtls(String lStrBillType,String lStrDtlsHead) throws Exception;
	
	boolean isValidBillType(String lStrDtlsHead,String lStrBillType) throws Exception;
	
	List<String> getListOfAuthNumberOfBEAMS(List<String> lLstAuthNumber) throws Exception;
	
	List<String> getListOfAuthNoWithVouchDtlsAvailable(List<String> lLstAuthNumber) throws Exception;
	
	// Mubeen 22 Nov 2012 
	List<Object[]> getBillForSchemePaymode() throws Exception;
	// Mubeen 04 Dec 2012
	List<BigInteger> getListOfBillsToReject(Integer lForMonth, String lLocationCode) throws Exception;
	
	// Mubeen 06 Dec 2012
	List<BigInteger> getListOfBillsForBEAMSReverseReject(Integer lForMonth,String lLocationCode) throws Exception;
	
	// Added by Shailesh on 31st October 2013
	public int rejectSevaarthPaybill(Long lLngBillNo,String authNo) throws Exception;

	// Added by Aditya on 17th December 2013
	Map<String, Long> getMonthlyPensionBillBankwiseDtls(String lStrBillNo,
			String gStrLocCode) throws Exception;

	Map<String, Long> getFirstPensionBillBankwiseDtls(String lStrBillNo,
			long lSubjectId) throws Exception;

	Map<String, Long> getSupplementaryPensionBillBankwiseDtls(
			String lStrBillNo, long lSubjectId)throws Exception;
	
	public String  getPayMode(String lStrBillNo, String gStrLocCode) throws Exception;

	public int getNoOfPensioner(String lStrBillNo)  throws Exception;

	Map<String, Long> getPartyNameForCMP(String lStrBillNo, String gStrLocCode) throws Exception;

	//added by aditya for Check in Reverse Rejection for Voucher
	public String getVoucherFlagForReject(long lLngBillNo)throws Exception;

	public String getCMPBeamsFlag(String gStrLocCode) throws Exception;
	
}



