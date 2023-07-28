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
package com.tcs.sgv.dcps.dao;

import java.util.List;
import java.util.Map;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Mar 22, 2011
 */
public interface DCPSDesignationDAO extends GenericDao 
{

	public List<ComboValuesVO> getCadres(Map inputmap) throws Exception;
	public List getDesigDsplyDtls(Map inputmap) throws Exception;
	
}
