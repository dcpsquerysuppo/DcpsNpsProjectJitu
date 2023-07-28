/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 29, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.dao;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.gpf.valueobject.MstGpfMonthly;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 Aug 29, 2011
 */
public interface ScheduleGenerationDAO extends GenericDao {
	List getEmpMonthlySubsData(Long lLngBillGroupId, Long lLngMonthId, Long lLngYearId) throws Exception;

	List getEmpAdvanceRecovery(String lStrGpfAccNo) throws Exception;

	Double getOpeningBalForCurrMonth(String lStrGpfAccNo, Long lLngMonthId, Long lLngYearId) throws Exception;

	Boolean isScheduleAlreadyGenerated(Long lLngBillGroupId, Long lLngMonthId, Long lLngYearId) throws Exception;

	List getOfficeNameForOfficeId(Long lLngOfficeId) throws Exception;

	String getTreasuryCodeForDDO(String lStrDdoCode);

	String getTreasuryNameForDDO(String lStrDdoCode);

	// List getCurrentOffices(String lStrDdoCode);

	List getYears();

	List getMonths();

	String getDdoCodeForDDO(Long lLngPostId);

	List getDraftScheduleForBillGroups(String lStrDdoCode, Long lLngYearId);

	List getBillGroups(String lStrDdoCode) throws Exception;

	List getDdoInfoForPost(Long lLngPostId);

	Double getAdvanceSanctionedForMonth(String lStrGpfAccNo, Integer lLngMonthId, String lStrFinYear);

	String getFinYearCodeForFinYearId(Long lLngFinYearId);

	Double getChallanPaidForMonth(String lStrGpfAccNo, Integer lLngMonthId, String lStrFinYear, Long lLngChallanType);

	Double getArrearsDetails(String lStrGpfAccNo, Long lLngMonthId, Long lLongYearId);

	List getEmpDataForLedger(String lStrGpfAccNo, Long lLngYearId);

	List getDiscardedScheduleForBillGroups(String lStrDdoCode, Long lLngYearId);

	Boolean isScheduleAlreadyApproved(Long lLngBillGroupId, Long lLngMonthId, Long lLngYearId) throws Exception;

	List getLastScheduleData(String lStrGpfAccNo) throws Exception;

	List getPendingSchedule(Long lLngBillgroupId, Long lLngMonthId, Long lLongYearId);

	Date getStartDateOfFinancialYear(Long lLngFinYearId);

	List getMonthlyEmpListForPayroll(Long lLngBillgroupId, Long lLngMonthId, Long lLongYearId);

	MstGpfMonthly getMstGpfMonthlyObject(String lStrGpfAccNo, Long lLngMonthId, Long lLongYearId);
	
	Long getFinyearFromYearCode(String lStrFinYearCode);
}
