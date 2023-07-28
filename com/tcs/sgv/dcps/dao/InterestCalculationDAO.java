/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 16, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.HashMap;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.dcps.valueobject.MstDcpsContributionYearly;
import com.tcs.sgv.dcps.valueobject.MstDcpsSixPCInterestYearly;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Jun 16, 2011
 */
public interface InterestCalculationDAO extends GenericDao {

	//public List getAllDCPSEmployeesForIntrstCalc(Long treasuryCode,String ddoCode,String lStrFromDate, String lStrToDate) throws Exception ;
	
	//public List getAllDCPSEmployeesForIntrstCalcForMissingCredits(Long treasuryCode,String ddoCode,String lStrFromDate, String lStrToDate) throws Exception;
	
	//public Boolean checkEmployeeEligibleForIntrstCalc(Long dcpsEmpId,String lStrFromDate, String lStrToDate,Long yearId);

	//public List getContriDtlsForGivenEmployee(Long treasuryCode,String lStrFromDate, String lStrToDate, Long dcpsEmpId);

	public Double getInterestRateForGivenYear(String lStrYear);

	public MstDcpsContributionYearly getContriYearlyVOForYear(
			Long dcpsEmpId, Long previousYearId);

	public String getDcpsIdForEmpId(Long dcpsEmpId);

	public Long getYearIdForYearCode(String yearCode);
	
	/*public List getAllDCPSEmployeesForIntrstCalcSixPC(Long treasuryCode,String ddoCode);
	
	public MstDcpsSixPCInterestYearly getSixPCYearlyInterestVOForYear(Long dcpsEmpId, Long previousYearId);
	*/
	//public List getArrearsDtlsForGivenEmployee(Long dcpsEmpId,Long yearId);
	
	public List getAllEmpsUnderDDO(String treasuryCode,String ddoCode,
			String lStrFromDate, String lStrToDate) throws Exception ;
	
	//public void updateTrnDcpsContriIntCalculated(Long lLongDcpsContriId);
	
	public void deleteMonthlyInterestForDCPSIdAndYear(String dcpsId,Long finYearId) throws Exception;
	
	public void updateTrnDcpsContriIntCalculated(List lListDcpsContriIdsPk)  throws Exception;
	
	public List getAllIntRateDtlsForGivenYear(Long lLongYear);
	
	public List getDCPSIdsForDcpsEmpIds(List lListDcpsEmpIds) ;
	
	public void deleteYearlyIntrstsForGivenEmpList(List lListDcpsIds,Long lLongYearId);
	
	public void deleteMonthlyIntrstsForGivenEmpList(List lListDcpsIds,Long lLongYearId);
	
	public List getContriDtlsForGivenEmployeeListFinal(String lStrFromDate, String lStrToDate,Long previousFinYearId,List listAllEmpsDCPSEmpIdsForIntrstCalc)  throws Exception;
	
	public List getArrearsDtlsForAllEmps(String totalDcpsEmpIds,Long yearId) ;
	
	public List getContriDtlsForGivenEmployeeListFinalForMissingCredits(String lStrFromDate, String lStrToDate,Long finYearId,List listAllEmpsEmpIdsForIntrstCalcForMissingCredits) throws Exception;

	//Added by Ashish for New Interest Calculation : start
	public String getAllDCPSEmployeesForIntrstCalc(Long treasuryCode,String ddoCode,
			String lStrFromDate, String lStrToDate) throws Exception ;
	
	public String getAllDCPSEmployeesForIntrstCalcForMissingCredits(Long treasuryCode,String ddoCode,
			String lStrFromDate, String lStrToDate,String excludeEmps) throws Exception;
	
	public List getEmpListForRegularConti(String dcpsEmpIds,String strFromDatePassed, String strToDatePassed,String yearId);
	
	public String checkEmployeeEligibleForIntrstCalc(String dcpsEmpId,String lStrFromDate, String lStrToDate,Long yearId);
	
	public List getEmpListForMissingCredit(String listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc,String strFromDatePassed, String strToDatePassed,String yearId);
	
	public List getInterestRatesForVoucherNo(Long finYearId);
	
	public List getInterestRatesForMissingCredit(String strToDatePassed,String voucherDate,Long yearId);
	
	public MstDcpsContributionYearly getContriYearlyVOForYear(String dcpsId,
			Long previousFinYearId);
	
	public void updateGeneratedId(Long dcpsContributionMonthlyIdForUpdate,String tableName);
	
	public Long getNextSeqNum(String generatePkForMonthly);
	
	  public String getEndDate(String dcpsId);
	
//	public HashMap terminationDetails(String dcpsEmpId) throws Exception;
	
//	public String getDcpsID(Long dcpsEmpId) throws Exception ;
	
	// public Object[] getMonthyAmountForTermination(String dcpsId,Long yearId) throws Exception;
	
	//public void updateYearlyForTermination(String dcpsEmpId,Long lLongYearId) throws Exception ;
	
	// public void deleteTerminationMonthlyEntry(String dcpsEmpId,Long lLongYearId,Long monthId) throws Exception ;
	 
	// public void deleteTerminationYearlyEntry(String dcpsEmpId,Long lLongYearId) throws Exception;

	//Added by Ashish for New Interest Calculation : end



}
