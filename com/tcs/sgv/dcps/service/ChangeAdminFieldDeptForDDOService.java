package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Sep 10, 2012
 */

public interface ChangeAdminFieldDeptForDDOService 
{
	ResultObject loadChangeAdminFieldDeptForm(Map<String, Object> inputMap);
	
	ResultObject getAdminAndFieldDeptForDDO(Map<String, Object> inputMap);
}
