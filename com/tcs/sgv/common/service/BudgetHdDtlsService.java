package com.tcs.sgv.common.service;

import com.tcs.sgv.core.valueobject.ResultObject;

import java.util.Map;

/**
 * 
 * @author 602399
 * 
 */

public interface BudgetHdDtlsService {
	public ResultObject getGrantHeadsForDDO(Map inputMap);

	public ResultObject getGPFHeads(Map inputMap);

	public ResultObject validateGPFHead(Map inputMap);

	public ResultObject getDDOHeadBySchemeNo(Map inputMap);

	public ResultObject getSchemeNoByDDOGrantHead(Map inputMap);
}
