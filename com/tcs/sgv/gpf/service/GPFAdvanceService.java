package com.tcs.sgv.gpf.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * @author 397138
 * 
 */
public interface GPFAdvanceService {
	ResultObject saveAdvance(Map inputMap);

	ResultObject forwardAdvanceToHO(Map inputMap);

	ResultObject approveAdvance(Map inputMap);

	ResultObject forwardAdvanceToDEOApprover(Map inputMap);
}
