package com.tcs.sgv.pensionpay.service;

import java.util.Map;
import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Common Pension Service Interface.
 * 
 * @author Soumya Adhikari
 * @version 1.0
 */

public interface CommonPensionService 
{
	ResultObject getAuditorBankCodeList(Map<String,Object> inputMap);
	ResultObject getHeadCodeDesc(Map<String,Object> inputMap);
	//String getMyCases(Map<String,Object> inputMap) throws Exception;
    ResultObject getSancAuthPrefix(Map<String,Object> inputMap);
    String getMyMRCases(Map<String,Object> inputMap) throws Exception;
    public ResultObject getSearchPPONo(Map<String, Object> inputMap) throws Exception;
    String generateLibraryNo(Map<String, Object> inputMap) throws Exception;
    String getNextLibraryNoId(Map<String, Object> inputMap) throws Exception;
}
