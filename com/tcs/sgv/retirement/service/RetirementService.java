package com.tcs.sgv.retirement.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Interface Description -
 * 
 * 
 * @author Niteesh Kumar Bhargava and shekhar Kadam DAT, Mumbai
 * @version 0.1
 * @since JDK 1.7 Aug 12, 2014
 */
public interface RetirementService {

	public ResultObject loadRetirementList(Map<String, Object> inputMap);
}
