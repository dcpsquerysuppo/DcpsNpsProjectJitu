package com.tcs.sgv.gpf.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.gpf.valueobject.MstGpfArrearsYearly;

/**
 * @author 397138
 * 
 */
public interface GPFInterestCalculationDAO extends GenericDao {

	public List getEmpDtls(String lStrSevaarthID, String lStrDdoCode);

	public List getOfficeDtls(String lStrDdoCode);

	public List getEmpDtlsUsingOfficeId(String lStrOfficeId, Long lLngGroupId, Long lLngYearId, Float lFltInterestRate);

	public Float getInterestRateForGroup(Long lLngGroupId, String lStrFinYear);

	public Float getInterestRateForSingleEmp(Long lLngGroupId, String lStrCurrDate);

	public Long getPkValue(String lStrGpfAccNo, Long lLngYearId);

	Float getCurrentInterestRateForEmp(String lStrSevaarthId, String lStrCurrDate);

	MstGpfArrearsYearly getArrearYearlyIdForFinYear(String lStrGpfAccNo, Long lLngYearId);

	Double getArrearBalance(String lStrGpfAccNo, Long lLngYearId);

	Double getMonthlyTotalForInterestCalc(String lStrGpfAccNo, Long lLngYearId);

	public Long getYearlyIdForFinYear(String lStrGpfAccNo, Long lLngYearId);

	public Long getFinYearIdByFinCode(String lStrFinCode);

	Double getMissingCreditsPreviousYears(String lStrGpfAccNo, Long lLngYearId);
	
	String isRateExistForPeriod(Long lLngGroupId, String lStrEffectFromDate);
	
	List getMissingCreditsToApprove(String lStrGpfAccNo, Long lLngYearId);

}
