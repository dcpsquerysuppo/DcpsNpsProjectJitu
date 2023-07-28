package com.tcs.sgv.gpf.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * @author 397138
 * 
 */
public interface GPFChangeSubscriptionDAO extends GenericDao {

	public List getChangeSubscription(String lStrGpfAccNo);

	public List getChangeSubscriptionToDEOApprover(String lStrGpfAccNo, Long lLngChangeSubId, Long lLngPostId);

	public Long getFinancialYearCount(String lStrGpfAccNo, Integer lIntFinYearId);

	public Long getFinancialYearDtlIncrement(String lStrGpfAccNo, Integer lIntFinYearId);

	public Long getFinancialYearDtlDecrement(String lStrGpfAccNo, Integer lIntFinYearId);

	public List getFutureMonthDtlForNullify(String lStrGpfAccNo, Long lLngFinYearId, Integer lIntMonthId);

	public List getMonths(Long lLngFinYearId);

	Boolean requestDataAlreadyExists(String strGpfAccNo);

	List getLastScheduleData(String lStrGpfAccNo, Long lLngCurrFinYearId) throws Exception;
}
