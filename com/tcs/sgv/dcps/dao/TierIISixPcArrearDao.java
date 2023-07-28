package com.tcs.sgv.dcps.dao;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public interface TierIISixPcArrearDao {
	
	List getEmpListForFiveInstApprove(String strDDOCode,String gStrLocationCode,String strRoleId, String interestApproved, String searchPara, String searchEmp);  //getDdoSearchList
	
	String getTierIIPendingOrderCount(String ddoCode,String month,String year,String orderId,String type);
	String getTierIIApprovedOrderCount(String ddoCode,String month,String year,String orderId,String type);
	String getTierIIRemaingOrderCount(String ddoCode,String month,String year,String orderId,String type);

	List getDdoSearchList(String gStrLocationCode,String searchDDO);  //getDdoSearchList

	void empListOfFiveInstUpdateTO(String dcpsEmpId,String Interest,String BillNo, String isDeputation)throws Exception;
	
	void updateInstAmtTO(String dcpsEmpId,String dueDrawnAmt,String orderID, String deputation)throws Exception;
	
	public String getRoleId(Long lLngPostId);
	
	void emprejectByTo(String dcpsEmpId,String Reason,String BillID, String deputation)throws Exception;

	List getTierIIBillList(String locId,String searchPara, String searchEmp,String cmbDDOCode,String IsDeputation,String blank);

	List getTierIIBillList(String strDDOCode,String Month,String Year, String searchPara, String searchEmp);////$t9Feb22getEmpViewBill
	
	List getTierIIOrderF(String strDDOCode,String Month,String Year, String login,String searchSeva,String searchEmp,String gStrLocationCode);////$t showP

	void deleteTierIIBill(String dDOCODE, String billNo,String Flag,String deputation);
	
	void ForwardToSrkaTierIIBill(String dDOCODE, String billNo, String IsDeputation);
	
	void ApproveGrantSrkaTierIIBill(String dDOCODE, String billNo, String IsDeputation);	

	List gteBillDetailsForBEAMS(String fileNumber, String dDOCODE, String IsDeputation);
	List getOrderDetails(String fileNumber,String Month,String Year);
	List getOrderDetailsF(String fileNumber,String Month,String Year);
	List getOrderDetailsG(String fileNumber,String Month,String Year);
	
	public String getLocationName(String lLngPostId);
	public String getDDOName(String lLngPostId);

	List getLoopingFOrIntereseCalculations();

	List getInterestCalculation(String financialYear);

	String generateTierTwoBill(String strDDOCode, String interest,
			String totalAMount, String grandTotalAmount, int length,
			String dcpsEmpId, String arrayDcpsID, String arrayTotalAmount,
			String arrayInterest, String isDeputation);
	
	String generateOrderF(String strDDOCode, String interest,
			String totalAMount, String grandTotalAmount, int length,
			String dcpsEmpId, String arrayDcpsID, String arrayTotalAmount,
			String arrayInterest);
	

	void empListNewTierIIInterestCalculation(String string, String interest_split, String totalAMount, String isDeputation);

	void empListNewTierIIInterestCalculationPart(String string);

	String getDDODetails(String ddoCode);
	
	List getEmpListForFiveInstApproveUpdateInst(String gStrLocationCode,String searchDDO, String searchEmp);

	void updateFiveInstAmtTO(String sevarthID, String reason, String total,
			String instI, String instINo, String txtStartDateI, String instII,
			String instIINo, String txtStartDateII, String instIII,
			String instIIINo, String txtStartDateIII, String instIV,
			String instIVNo, String txtStartDateIV, String instV,
			String instVNo, String txtStartDateV, String deputation, String cmbInstDtlsI, String cmbInstDtlsII, String cmbInstDtlsIII, String cmbInstDtlsIV, String cmbInstDtlsV, String cmbVCDtlsI, String cmbVCDtlsII, String cmbVCDtlsIII, String cmbVCDtlsIV, String cmbVCDtlsV, String cmbTreasuryI, String cmbTreasuryII, String cmbTreasuryIII, String cmbTreasuryIV, String cmbTreasuryV, String chanInstI, String chanInstII, String chanInstIII, String chanInstIV, String chanInstV) throws ParseException;

	void insertFiveInstAmtTO(String sevarthID, String reason, String total,
			String instI, String instINo, String txtStartDateI, String instII,
			String instIINo, String txtStartDateII, String instIII,
			String instIIINo, String txtStartDateIII, String instIV,
			String instIVNo, String txtStartDateIV, String instV,
			String instVNo, String txtStartDateV, String deputation,String gStrLocationCode,String gStrPostId,String gStrUserId,String Treasury, String cmbInstDtlsI, String cmbInstDtlsII, String cmbInstDtlsIII, String cmbInstDtlsIV, String cmbInstDtlsV, String cmbVCDtlsI, String cmbVCDtlsII, String cmbVCDtlsIII, String cmbVCDtlsIV, String cmbVCDtlsV, String cmbTreasuryI, String cmbTreasuryII, String cmbTreasuryIII, String cmbTreasuryIV, String cmbTreasuryV) throws ParseException;

	String getDdoCodeForDDO(String string);

	String generateOrderFDept(String strDDOCode, String interest,
			String totalAMount, String grandTotalAmount, int length,
			String dcpsEmpId, String arrayDcpsID, String arrayTotalAmount,
			String arrayInterest);

	String getHODDetails(Long gLngPostId);
	
}
