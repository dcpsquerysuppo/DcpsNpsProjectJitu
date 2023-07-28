package com.tcs.sgv.dcps.service;


import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 25, 2011
 */

public interface TerminalRequestService {
	
	public ResultObject loadTerminalRequest(Map inputMap) throws Exception; 
}
