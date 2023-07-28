/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 22, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.DcpsDdoInfo;
import com.tcs.sgv.dcps.valueobject.MstDcpsDesignation;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Mar 22, 2011
 */
public interface DCPSDesignationVOGenerator 
{
	ResultObject generateMap(Map inputMap);
	MstDcpsDesignation generateMstDcpsDesignationVO(Map inputMap);

}
