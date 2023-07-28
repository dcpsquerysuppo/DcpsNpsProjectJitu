/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 12, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;


import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

import com.tcs.sgv.dcps.valueobject.MstDummyOffice;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * May 12, 2011
 */
public interface MstDummyOfficeVOGenerator 
{
	ResultObject generateMap(Map inputMap);
	MstDummyOffice generateMstDummyOfficeVO(Map inputMap);
}
