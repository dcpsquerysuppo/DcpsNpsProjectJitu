package com.tcs.sgv.gpf.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface GPFEmpPayrollProcessingDAO extends GenericDao {
	List getMonthlySubscriptionForBillgroup(Long lLngBillGroupId, Long lLngYearId, Long lLngMonthId);

	Object getGPFAccountObjForEmpId(Long lLngEmpId);

	List getAdvanceDetailsForGPFAccNo(String lStrGpfAccNo);
}
