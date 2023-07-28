package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface DayBookReportService 
{
	ResultObject loadDayBookReport(Map<String, Object> inputMap);
}
