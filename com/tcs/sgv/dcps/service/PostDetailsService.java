package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description -
 * 
 * 
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0 Oct 17, 2012
 */

public interface PostDetailsService 
{
	ResultObject loadPostDetailsData(Map<String, Object> inputMap);
}
