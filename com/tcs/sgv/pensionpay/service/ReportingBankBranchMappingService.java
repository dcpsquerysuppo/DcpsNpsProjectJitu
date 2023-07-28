package com.tcs.sgv.pensionpay.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface ReportingBankBranchMappingService {
	public ResultObject loadReportingBankBranchForm(Map<String, Object> inputMap);

	public ResultObject updateReportingBankBranch(Map<String, Object> inputMap);

}
