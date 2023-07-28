package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

// TODO: Auto-generated Javadoc
/**
 * The Interface DCPSArrearsService.
 */
public interface ArrearsService {

	/**
	 * Load dcps tier i.
	 * 
	 * @param inputMap
	 *            the input map
	 * 
	 * @return the result object
	 */
	public ResultObject loadDCPSTierI(Map<String, Object> inputMap);

	public ResultObject loadsixthPCYearlyInstallment(
			Map<String, Object> inputMap);

	public ResultObject loadsixthPCArrearsEntry(Map<String, Object> inputMap);

	public ResultObject loadOfflineDCPSForm(Map inputMap) throws Exception;

	public ResultObject deleteContributions(Map inputMap);

	public ResultObject saveContributions(Map inputMap);

	public ResultObject getEmpDtlsFromDDODesig(Map inputMap);

	public ResultObject getEmpNameFromId(Map inputMap);

	public ResultObject loadSavedFormForTier(Map inputMap);

	public ResultObject createandfrwrdWF(Map inputMap);

	public ResultObject forwardDCPSArrearsYearly(Map inputMap);

	public ResultObject loadSixPCArrearAmountSchedule(
			Map<String, Object> inputMap);

	public ResultObject loadOfflineCorrectionForm(Map inputMap)
			throws Exception;

	public ResultObject forwardRequestToTO(Map objectArgs);

	public ResultObject rejectRequestToATO(Map objectArgs);

	public ResultObject loadDCPSTier(Map<String, Object> inputMap);

	public ResultObject forwardTierDDO(Map objectArgs) throws Exception;

}
