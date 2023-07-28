/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Dec 2, 2011		Jayraj Chudasama								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description -
 * 
 * 
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0 Dec 2, 2011
 */
public interface GPFDataEntryFormDAO extends GenericDao {

	List getEmployeeNameAndPay(String lStrEmpCode, String lStrLocCode) throws Exception;

	String getGpfAccNo(String lStrSevaarthId) throws Exception;

	String getEmpGpfAccID(String lStrSevaarthId) throws Exception;

	List getNRADetails(String lStrGpfAccNo, String lStrReqType) throws Exception;

	List getRADetailsHis(String lStrGpfAccNo, String lStrReqType) throws Exception;

	List getRADetailsCur(String lStrGpfAccNo, String lStrReqType) throws Exception;

	List getSubscriptionDetails(String lStrGpfAccNo, String lStrReqType) throws Exception;

	List getEmpListForVerification(String lStrLocationCode) throws Exception;

	String getBillGroupId(String lStrSevaarthId) throws Exception;

	String getTranIdForRAAdvance(String lStrGpfAccNo, String lStrReqType) throws Exception;

	List getScheduleDetails(String lStrGpfAccNo, String lStrReqType) throws Exception;

	String getDdoCodeForDDO(Long lLngPostId) throws Exception;

	List getEmpListForDraftReq(String lStrLocationCode) throws Exception;

	String getTrnEmpGpfAccID(String lStrSevaarthId, String lStrReqType) throws Exception;

	List getPrevDetailsVoucher(String lStrGpfAccNo) throws Exception;

	List getPrevDetailsChallan(String lStrGpfAccNo) throws Exception;

	List chkIfEmpExist(String lStrGpfAccNo) throws Exception;

	List getAdvanceDetails(String lStrGpfAccNo) throws Exception;

	void updateTrnEmpGpfAcc(String lStrPk, String lStrDeoRemark, String lStrDdoRemark, String lStrReqType)
			throws Exception;

	void updateMstEmpGpfAcc(String lStrSevaarthID, String lStrCurBalance, String lStrMonthlySubscription)
			throws Exception;

	void updateEmpSubscription(String lStrSubscriptionID, String lStrReqType) throws Exception;

	void updateGpfMonthly(Long lLngMonthlyID, Long lLngMonth, Long lLngFinYear, String lStrGpfAccNo)
			throws Exception;

	void updateChallanDetails(String lStrChallanID, String lStrReqType) throws Exception;

	void updateAdvanceDetails(String lStrAdvanceID) throws Exception;

	public Long getMonthlyIDForMonth(String lStrGpfAccNo, Integer lIntMonthId, Long lLngYearId) throws Exception;

	public Double getMonthlySubscription(Long lLngMonth, Long lLngFinYear, String lStrGpfAccNo) throws Exception;

	public Double getChallanPaidForMonth(String lStrGpfAccNo, Integer lLngMonthId, String lStrFinYear,
			Long lLngChallanType) throws Exception;

	String chkEntryForSevaarthId(String lStrSevaarthId) throws Exception;

	void discardGPFMonthly(String lStrMonthlyID) throws Exception;

	List getSubscriptionDataFromPayroll(String lStrFinYearCode, Integer lIntCurMonth, String lStrEmpCode);
	
	List getPreviousSubscriptionDataFromPayroll(String lStrFinYearCode, Integer lIntCurMonth, String lStrEmpCode);

	Object[] getAdvanceDataFromPayroll(String lStrEmpCode);
	
	String chkEntryForGpfYearly(String lStrGpfAccNo) throws Exception;
	
	
	
	List getAdvanceDetailsNew(String lStrGpfAccNo) throws Exception;
}
