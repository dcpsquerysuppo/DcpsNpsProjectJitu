package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface GroupWiseEmpReportService 
{
	ResultObject loadGroupWiseReport(Map<String, Object> inputMap);
}
