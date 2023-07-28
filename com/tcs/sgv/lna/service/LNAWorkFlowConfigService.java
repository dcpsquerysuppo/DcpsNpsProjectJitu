package com.tcs.sgv.lna.service;

import java.util.Map;
import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Jul 12, 2012
 */

public interface LNAWorkFlowConfigService 
{
	ResultObject loadLNAWorkFlowConfig(Map<String, Object> inputMap);
	
	ResultObject getAllRolesForUsers(Map<String, Object> inputMap);
}
