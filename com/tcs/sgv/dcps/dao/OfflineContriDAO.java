/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 7, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.dcps.valueobject.MstDcpsContriVoucherDtls;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 7, 2011
 */
public interface OfflineContriDAO extends GenericDao {

	public List getEmpListForContribution(String lStrDDOCode,
			Long lLongbillGroupId, Long monthId, Long yearId, String lStrUser,
			String lStrUse, String lStrPostId, Map displayTag,
			String lDtFirstDate, String lStrTypeOfPaymentMaster,Long delayedMonthId,Long delayedYearId,String lDtLastDate);
	
	public List getEmpListForContributionFinal(String lStrDDOCode,
			Long lLongbillGroupId, Long monthId, Long yearId, String lStrUser,
			String lStrUse, String lStrPostId, Map displayTag,
			String lDtFirstDate, String lStrTypeOfPaymentMaster,Long delayedMonthId,Long delayedYearId,String lDtLastDate,String lStrFirstDateMainMonth,String schemeCode);

	public List getCurrentTreasury(String gStrLocationCode);

	public List getAllTreasuries();

	public List getAllDDO(String lStrTreasuryLocCode);
	
	public List getAllDDOForContriForwardedToTO(String lStrTreasuryLocCode) ;

	public List getBillGroupsForDdo(String lStrDdoCode) throws Exception;

	public List getTreasuryForDDO(String lStrDdoCode);

	public List getDdoNameFromDdoCode(String lStrDdoCode);

	public String getInitUnitPostIdForContriIdOnline(String dcpsContriId);

	public String getInitUnitPostIdForContriIdManual(String dcpsContriId);

	public Integer getRegStatusForContriId(String dcpsContriId);

	public MstDcpsContriVoucherDtls getContriVoucherVOForInputDtls(Long yearId,
			Long monthId, String ddoCode, Long billGroupId);

	public MstDcpsContriVoucherDtls getContriApprovedVoucherVOForInputDtls(
			Long yearId, Long monthId, String ddoCode, Long billGroupId);

	public MstDcpsContriVoucherDtls getContriVoucherVOForInputDtlsForPopup(
			Long yearId, Long monthId, String ddoCode, Long billGroupId,
			Long treasuryCode);

	public void revertRequestAndUpdtVoucherVO(Long year, Long month,
			Long billGroupId, Long treasuryCode, Long voucherNo,
			String reasonForRevert);

	public List getAllRevertRequestsForSRKA();

	public List getAllRevertRequestsForTreasury(String lStrTreasuryLocCode);

	public List getApprovedBillGroupsForDdo(String lStrDdoCode,
			Long lLongMonthId, Long lLongYearId) throws Exception;

	public Double getTotalVoucherAmountForGivenVoucher(Long monthId,
			Long finYearId, Long billGroupId, String ddoCode);

	public MstDcpsContriVoucherDtls checkContributionsForPrvsMonth(Long yearId,
			Long monthId, String ddoCode, Long billGroupId);

	public List getApprovedBillGroupsForDdoInDDOLogin(String lStrDdoCode)
			throws Exception;

	public List getBillGroupsForDdoInDDOLogin(String lStrDdoCode)
			throws Exception;

	public List getBillGroupsForDdoInTOLogin(String lStrDdoCode)
			throws Exception;

	public Boolean checkContriOfEmpForSelectedPeriod_new(Long dcpsEmpId,
			Date contriStartDate, Date contriEndDate, String typeOfPayment,String lStrDDOCode,String payCommission);
	
	public Boolean checkContriOfEmpForSelectedPeriod(Long dcpsEmpId,
			Date contriStartDate, Date contriEndDate, String typeOfPayment,String lStrDDOCode);

	public Float getDARateForPayCommission(String payCommission);

	public Double getDelayedContribution(Long lLngDcpsEmpId, Long lLngYearId,
			Long lLngMonthId);
	
	public Double getDelayedContributionMatched(Long lLngDcpsEmpId, Long lLngYearId,
			Long lLngMonthId) ;
	
	public List getBillGroupsRejectedForDdo(String lStrDdoCode) throws Exception;
	
	public List getBillGroupsRejectedForDdoInATOLogin(String lStrDdoCode) throws Exception ;
	
	public Double getDARateForGivenPrd(Date contriStartDate, Date contriEndDate,String lStrPayCommission) ;
	
	public List getBillGroupsRegularForDdoInDDOAsstLogin(String lStrDdoCode) throws Exception ;
	
	public List getEmpListForContributionSchdlr(String lStrDDOCode,
			Long lLongbillGroupId, Long monthId, Long yearId, String lStrUser,
			String lStrUse, String lStrPostId,
			String lDtFirstDate, String lStrTypeOfPaymentMaster,Long delayedMonthId,Long delayedYearId);

	public MstDcpsContriVoucherDtls fetchMstContriVoucherDtls(Long lLngVoucherContriId,Map objectArgs);
	
	public TrnDcpsContribution fetchTrnDcpsContribution(Long lLngTrnDcpsContributionId,Map objectArgs);
	
	public MstDcpsContriVoucherDtls getContriVoucherVOForInputDtlsSchdlr(Long yearId,Long monthId, String ddoCode, Long billGroupId,Map objectArgs);
	
	public Boolean checkEntryInTrnForRltContriVoucherId(Long lLongRltContriVoucherId) throws Exception;
	
	public void deleteVoucherForNoContris(Long lLongMstContriVoucherId) throws Exception;
	
	public List getRegularContributionsFromPayroll(Long lLongYearIdFromPayroll, Long lLongMonthId, Long lLongBillGroupId) throws Exception;
	
	public void insertRegularContributions(List lListRegularContributionsFromPayroll,Long lLongMonthId,Long lLongYearId,Long lLongBillGroupId,String lStrDDOCode,Long lLongTreasuryCode,String lStrSchemeCode,Date lDtStartDate,Date lDtEndDate,Date gDtCurDate,Long gLngPostId,Long glongLocationCode,Long lLngContriVoucherIdToBePassed,Map inputMap) throws Exception ;
	
	public String getSchemeCodeForBillGroup(Long lLongbillGroup);
	
	public void deleteRegularContributionsIfExist(Long monthId,Long finYearId,Long dcpsDdoBillGroupId,String ddoCode) throws Exception;
	
	public List getEmpListForDelayedTypesInMonthAndBG(Long lLongYearIdFromPayroll, Long lLongMonthId, Long lLongBillGroupId) throws Exception ;
	
	public List getEmpListForPayArrearTypesInMonthAndBG(Long lLongYearIdFromPayroll, Long lLongMonthId, Long lLongBillGroupId) throws Exception ;
	
	public List getEmpListForDAArrearTypesInMonthAndBG(Long lLongYearIdFromPayroll, Long lLongMonthId, Long lLongBillGroupId) throws Exception ;
	
	public void updateDelayedContriStatusInTrn(List lListDelayedTypeEmpList,Long monthId,Long finYearId,Long dcpsDdoBillGroupId,String ddoCode) throws Exception;
	
	public void updatePayArrearContriStatusInTrn(List lListPayArrearTypeEmpList,Long monthId,Long finYearId,Long dcpsDdoBillGroupId,String ddoCode) throws Exception;
	
	public void updateDAArrearContriStatusInTrn(List lListDAArrearTypeEmpList,Long monthId,Long finYearId,Long dcpsDdoBillGroupId,String ddoCode) throws Exception;
	
	public List getEmpListOfDCPSBrokenPeriodForMonth(Long lLongYearId, Long lLongMonthId) throws Exception ;
	
	public List getBrokenIdPKForEmployee(Long hrEisEmpId, Long lLongYearId, Long lLongMonthId,String billType) throws Exception ;
	
	public List getBasicStartAndEndDateForBrokenIdPK(Long lLongBrokenIdPK) throws Exception ;
	
	public List getDcpsEmpIdAndPCForEisId(Long lLongHrEISEmpId) throws Exception ;
	
	public List getDCPSValueForBrokenPeriodIDPk(Long lLongBrokenPeriodIdPk) throws Exception;
	
	public List getDAValueForBrokenPeriodIDPk(Long lLongBrokenPeriodIdPk) throws Exception;
	
	public List getDPValueForBrokenPeriodIDPk(Long lLongBrokenPeriodIdPk) throws Exception;
	
	public Boolean checkIfBillAlreadyGenerated(Long lLongBillGroupId,Long lLongMonth,Long lLongYear);
	
	public void updateVoucherDetailsInPayBillHeadMpg(Long lLongBillGroupId,Long lLongMonth,Long lLongYear,Long lLongVoucherNo,Date lDateVoucherDate);
	
	public void updateNonRegularTypeContriStatusInTrn(Long lLongYearId, Long lLongMonthId, Long lLongBillGroupId) throws Exception ;
	
	public List getCentralDAValueForBrokenPeriodIDPk(Long lLongBrokenPeriodIdPk) throws Exception;
	
	public void updateVoucherDetailsInTrnDcpsContri(Long lLongBillGroupId,Long lLongMonthId,Long lLongYearId,String lStrDDOCode,Long lLongVoucherNo,Date lDateVoucherDate) throws Exception ;
	
	public List getAllDDOWOBrackets(String lStrTreasuryLocCode);
	
	public List getSchemeNamesFromCodeInMissingCredit(String schemeCode, String lStrDdoCode);
	
	public MstEmp getEmpVOForDCPSId(String dcpsId);
	
	public MstDcpsContriVoucherDtls getContriVoucherVOForInputDtlsMissingCredit(Long lLongYearId,Long lLongMonthId,
			String lStrDDOCode,Long lLongBillGroupId,String lStrSchemeCode,Long lLongVoucherNo,Date lDateVoucherDate);
	
/*
	public Double getTotalVoucherAmountForGivenVoucherMissingCredit(Long monthId,
			Long finYearId, Long billGroupId, String ddoCode,String lStrSchemeCode,Long lLongVoucherNo,Date lDateVoucherDate);
*/
	public Double getTotalVoucherAmountForGivenVoucherMissingCredit(Long lLngContriVoucherIdPassed);
	
	public void updateVoucherAmountInMstContriForMissingCreditVoucher(Long lLngContriVoucherIdPassed, Double voucherAmount) ;
	
	public List getBillGroupsForDdoInTOLoginInclMisngCrdt(String lStrDdoCode)
	throws Exception;
	
	public List getAllDDOForApprovedContriInTO(String lStrTreasuryLocCode);
	
	public List getAllDDOCodeASC(String lStrTreasuryLocCode) ;
	
	public List getVoucherNoAndDateForInputDtls(Long yearId,
			Long monthId, String ddoCode, Long billGroupId);
	
	public void updateVoucherDetailsInMstContriOnApproval(Long lLngContriVoucherIdToBePassed,Double lDoubleVoucherAmount) throws Exception ;

	public List getContriOfEmpForSelectedPeriod(Long lngDcpsEmpId, Date dateContriStartDate, Date dateContriEndDate, String strTypeOfPayment, String strDdoCode);
	
	public List getMissingCreditForApprovalSRKA() throws Exception;
	
	 public List getBsicPayDetails(Long dcpsEmpId) throws Exception;

	public Boolean checkContriOfEmpForSelectedPeriod_newARREAR(
			Long lLngDcpsEmpId, Date lDateContriStartDate,
			Date lDateContriEndDate, String lStrTypeOfPayment,
			String lStrDdoCode, String payComm);
}
