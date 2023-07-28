package com.tcs.sgv.dcps.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.dcps.valueobject.MstDcpsContributionYearly;

public interface TerminalRequestDAO extends GenericDao {

	public List getEmpDtlsForName(String lStrName, String lStrDDOCode) throws Exception;

	public List getAllMissingCreditsForEmp(Long lLongEmpId, Date lDtDOJ, Date lDtTerminationDate) throws Exception;

	public List getAllMissingCreditsSavedForTerminalId(Long lLongTerminalId) throws Exception;

	public List getAllTerminalRequests(String lStrDDOCode, String gStrLocationCode, String lStrUser, String lStrUse)
			throws Exception;

	public Boolean checkTerminalRequestRaisedOrNot(Long dcpsEmpId) throws Exception;

	public void deleteMissingCreditsSavedForTerminalId(Long lLongTerminalId) throws Exception;

	Double getOpeningBalanceForDcpsId(String lStrDcpsId, Long lLngFinYearId) throws Exception;

	Double getTotalMissingCreditsForEmp(String lStrDcpsId) throws Exception;

	Double getTier2ContributionForYear(String lStrDcpsId, Long lLngFinYearId) throws Exception;

	List getContributionTillDate(String lStrDcpsId, Long lLngYearId) throws Exception;

	List getMissingCreditsForDcpsId(String lStrDcpsId) throws Exception;

	Double getPendingEmployerContributionForYear(String lStrDcpsId, Long lLngYearId) throws Exception;

	Double getPaidEmployerContributionForYear(String lStrDcpsId, Long lLngYearId) throws Exception;

	Date getStartDateForFinYear(Long lLngFinYearId) throws Exception;

	Date getEndDateForFinYear(Long lLngFinYearId) throws Exception;

	Long getDcpsEmpIdForDcpsId(String lStrDcpsId) throws Exception;

	public List getAllTreasuries();

	public List getAllEmpsUnderDDO(String selectedDDocode);

	public List getTerminationDetailsOfEmp(String ddoCode, String empId);

	public List getAllDDO(String selectedTreasuryCode);

	public String getSuperAnnDateexist(String sevaarthId);

	public long getfinYearIdFromYr(int year) throws Exception;

	public boolean checkPrvInterestCal(String dcpsId,Long yearId) throws Exception;

	public Double getOpeningBalanceTier1(String dcpsId, long yearId) throws Exception;

	public Double getEmployeeContributionNonMissingTier1(String dcpsId, long yearId) throws Exception;

	public List getArrearsDtlsForAllEmps(String totalDcpsEmpIds,Long yearId) ;
	
    public void deleteYearlyIntrstsForGivenEmpList(String lListDcpsIds,Long lLongYearId);
	
	public void deleteMonthlyIntrstsForGivenEmpList(String lListDcpsIds,Long lLongYearId);

	public List getFromToDate(Long yearId);

	public Double getEmployeeContributionWithMissingTier1(String string, String fromDate, String toDate) throws Exception;

	public Double getInterestTier1(String dcpsId, long yearId) throws Exception;

	public List getContributionAndIntTier2(String dcpsId, long yearId) throws Exception;

	public Long getNextSeqNum(String string);

	public String getReasonOfTermination(String cmbReasonForTermination);

	public List getTerminationDetailsForPartialTerminate(String empId);

	public List checkEmployeeInPartialTermination(String dcpsId);

	public List getFromToDt(Long yearId);

	public String getTerminationDt(String strEmpIds);

	public String getAllDCPSEmployeesForIntrstCalc(Long treasuryCode,String ddoCode,
			String lStrFromDate, String lStrToDate) throws Exception ;
	
	public String getAllDCPSEmployeesForIntrstCalcForMissingCredits(Long treasuryCode,String ddoCode,
			String lStrFromDate, String lStrToDate) throws Exception;
	
	public List getEmpListForRegularConti(String dcpsEmpIds,String strFromDatePassed, String strToDatePassed,String yearId);
	
	public String checkEmployeeEligibleForIntrstCalc(String dcpsEmpId,String lStrFromDate, String lStrToDate,Long yearId);
	
	public List getEmpListForMissingCredit(String listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc,String strFromDatePassed, String strToDatePassed,String yearId);
	
	public List getInterestRatesForVoucherNo(Long finYearId);
	
	public void updateTrnDcpsContriIntCalculated(List lListDcpsContriIdsPk)  throws Exception;
	
	public List getInterestRatesForMissingCredit(String strToDatePassed,String voucherDate,Long yearId);
	
	public MstDcpsContributionYearly getContriYearlyVOForYear(String dcpsId,
			Long previousFinYearId);
	
	public void updateGeneratedId(Long dcpsContributionMonthlyIdForUpdate,String tableName);

	public List getR3DtlsBeforeIntCal(Long dcpsEmpId);

	public int finalterminationFlagUpdation(long dcpsEmpId);

	public String checkEmployeePrvR3gen(long dcpsEmpId);
	
	

}
