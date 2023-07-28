package com.tcs.sgv.gpf.service;

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

public interface GPFWorkFlowConfigService 
{
	ResultObject loadGPFWorkFlowConfig(Map<String, Object> inputMap);
	
	ResultObject getAllRolesForUsers(Map<String, Object> inputMap);
}
