package com.tcs.sgv.gpf.service;

/**
 * @author Niteesh Kumar Bhargava
 * @version 0.1
 * @since JDK 1.7 Sep 9, 2014
 * 
 * This is Service Layer interface that defines the methods 
 * for AG and GPF series regarding functions
 * Place new functions in this,
 * if they perform some direct functions on GPF Series
 */

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;
public interface AGGPFService {
	ResultObject loadAGList(Map<String, Object> inputMap);
	ResultObject addAG(Map<String, Object> inputMap);
	ResultObject addPFSeriesforAG(Map<String, Object> inputMap);
	ResultObject loadPFList(Map<String, Object> inputMap) throws Exception;
	ResultObject updateAGName(Map<String, Object> inputMap);
	ResultObject updatePFName(Map<String, Object> inputMap);
	ResultObject obsolateAG(Map<String, Object> inputMap);
	ResultObject activateAG(Map<String, Object> inputMap);
	ResultObject loadPFEditForm(Map<String, Object> inputMap) throws Exception;
	ResultObject activePFService(Map<String, Object> inputMap) throws Exception;
	ResultObject obsoletePFService(Map<String, Object> inputMap) throws Exception;
}
