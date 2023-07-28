package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionSixpayfpArrear;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionSupplyBillDtls;



public interface SupplementaryBillDAO extends GenericDao {

	Integer getPnsnrSixPayArrearRevisionCnt(String lStrPensionerCode,Long lLngPensionSupplyBillId) throws Exception; 
	
	Map<String, List<TrnPensionRecoveryDtls>> getRecoveryDtlsForSupplPensionBill(String lStrPensionerCode, String lStrForMonth) throws Exception;
	
	List<TrnPensionRecoveryDtls> getRecoveryDtlsForSupplPnsnBillForDelete(String lStrPensionerCode, String lStrRecoveryFrom, Long lLngSupplBillId) throws Exception;
	
	Integer getSupplRequestCount(Long lLngPostId,String lStrLocationCode) throws Exception;
	
	List getSupplRequestList(Map displayTag, Long lLngPostId,String lStrLocationCode) throws Exception;
	
	Integer getSupplRequestDtlsFromRequestNoCount(String lStrRequestNo,Long lLngPostId,String lStrLocationCode) throws Exception;
	
	List<TrnPensionSupplyBillDtls> getSupplRequestDtlsFromRequestNo(Map displayTag, String lStrRequestNo,Long lLngPostId,String lStrLocationCode) throws Exception;
	
	List getSupplPnsnDtlSchemeCodePayModeWise(String lStrRequestNo, List<Short> lLstBillStatus, String lStrLocationCode) throws Exception;
	
	List getSupplPnsnDtlBranchHeadWise(String lStrRequestNo, List<Short> lLstBillStatus, String lStrLocationCode) throws Exception;
	
	List<TrnPensionSupplyBillDtls> getSupplPnsnDtlsFromRequestNo(String lStrRequestNo, List<Short> lLstBillStatus, String lStrLocationCode) throws Exception;
	
	void deleteRecoveryDtlsFromSupplBillId(Long lLngPensionSupplBillId) throws Exception;
	
	void deleteSixPayArrearDtlsFromSupplBillId(Long lLngPensionSupplBillId) throws Exception;
	
	void updateBillNoInSupplDtls(List<Long> lLstSupplBillId,Long lLngBillNo,Short lShStatus) throws Exception;
	
	void updateBillNoInRecoveryDtls(List<Long> lLstSupplBillId,Long lLngBillNo) throws Exception;
	
	void updateBillNoInSixPayArrearDtls(List<Long> lLstSupplBillId,Long lLngBillNo) throws Exception;
	
	List<Object> getSchemewiseRecoveryDtlsFromBillNo(Long lLngBillNo) throws Exception;
	
	void updateStatusInSupplBillDtls(Long lLngBillNo,Short lStatus,BigDecimal lBgDcmlUserId,BigDecimal lBgDcmlPostId,Date lDtCurrDate) throws Exception;
	
	void updatePaidFlagInSixPayFromBillNo(Long lLngBillNo,BigDecimal lBgDcmlUserId,BigDecimal lBgDcmlPostId,Date lDtCurrDate) throws Exception;
	
	List<TrnPensionRqstHdr> getAllocationPercentFromPensionerCode(List<String> lLstPensionerCode, String lStrLocCode) throws Exception;
	
	void updateActiveFlagInSixPayArrear(String lStrPensionerCode,Character lCharRvsnCntr) throws Exception;
	
	String getRequestNoFromPpoNo(String lStrPpoNo,List<Short> lLstStatus, String lStrLocCode) throws Exception;
	// public List getSavedSupplementaryPartyRequests(String lStrPnsnrCode,
	// String lStrLocCode) throws Exception;
	// public void updateSupplyBillNo(Long lLngbillNo,String
	// lStrSupplyBillID,Long gLngPostId,Long gLngUserId,Date gDate) throws
	// Exception;
	// public int checkForExistedBills(String lStrPnsnrCode) throws Exception;
	//	
	// //Soumya
	//
	//
	// void updateSuppReqStatusApproved(String lStrPensionerCode,String
	// lStrLocCode,Long gLngPostId,Long gLngUserId,Date gDate) throws Exception;
	// List getGroupIdForSavedSuppReq(String lStrPnsnrCode,String lStrLocCode)
	// throws Exception;
	// void insertGroupIdInRecoveryDtls(String lStrPensionerCode,String
	// lStrBillType,Long lLngGroupId,Long gLngPostId,Long gLngUserId,Date gDate)
	// throws Exception;
	// List getSuppBillDtlsData(String lStrPKList,String lStrLocationCode)
	// throws Exception;
	// List getSuppBillEDPReceiptDtls(String lStrPKList,String lStrLocationCode)
	// throws Exception;
	// List getSuppPnsnBillPReceiptDtls(String lStrPKList,String
	// lStrLocationCode) throws Exception;
	// List getHeadCodeWiseNetListForView(Long lLngBillNo,String lStrLocCode)
	// throws Exception;
	// String getBrnchScheme(Long lBillNo,String lStrLocCode) throws Exception;
	// List getBnkBrnchHdcodeList(Long lBillNo) throws Exception;
	//	
	//	
	// List getBillTypeListForBillNo(Long lBillNo,String lStrLocationCode)
	// throws Exception;
	// List getHeadCodeListForBillType(Long lBillNo,String lStrBillType,String
	// lStrLocationCode) throws Exception;
	// List getSuppBillDataforViewNew(Long lLngBillNo,String
	// lStrBillType,BigDecimal BDHeadCode,String lStrLocCode) throws Exception;
	// BigDecimal getHeadCodeWiseNetListForBillTypeView(Long lLngBillNo,String
	// lStrBillType,BigDecimal lBDHd,String lStrLocCode) throws Exception;
	// void updateSuppBillDtlsForRejection(String lStrBillNo,Long
	// gLngPostId,Long gLngUserId,Date gDate) throws Exception ;
	// void updateSuppBillDtlsForRejectionCase(String lStrPensionerCode,String
	// lStrLocCode,Long gLngPostId,Long gLngUserId,Date gDate) throws Exception
	// ;
	// String getBillGenStatus(String lStrSuppIdList) throws Exception;
	//	
	// public List getPnsnrBankDtls(String lStrPensionerCode, String
	// lStrCaseStatus, String lStrLocCode) throws Exception;
	// public List getListOfNominee(String pensionerCode, String lStrLocCode)
	// throws Exception;
	// public List getRcptEdpDtlByBillType(String lStrPnsnerCode, String
	// lStrBillType, long lStrGrpId) throws Exception;
	// void archiveApprovedReq(List lLstSuppIdList, Map inputMap) throws
	// Exception;
}
