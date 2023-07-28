/**
 * 
 */
package com.tcs.sgv.common.service;

import java.util.Map;

import com.tcs.sgv.core.service.Service;
import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * @author 157045
 *
 */
public interface IFMSCommonService  extends Service{
	
	Character DATE = 'D';
	Character MONTH = 'M';
	Character YEAR = 'Y';
	
	public ResultObject seeUserManual(Map inputMap);	
	
}
