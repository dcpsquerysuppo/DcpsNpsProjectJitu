package com.tcs.sgv.pensionproc.report;

import java.util.List;

import com.tcs.sgv.common.valuebeans.reports.ReportVO;

public interface PensionprocQueryDAO {
	
	List getPensionCaseTrackingReport(ReportVO lObjReport, String lStrFromDate, String lStrToDate, String lStrPensionType, String lStrRetirementDate,			
			String lStrPensionerName, String lStrSevaarthId, String lStrPpoNo,String lStrLocCode,Long lLngDdoCode) throws Exception;
}
