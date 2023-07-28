/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 8, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Apr 8, 2011
 */
public interface SixPCArrearsDAO extends GenericDao
{
	public List getEmpListForSixPCArrears(String lStrDDOCode,String lStrDesignation, Map displayTag) throws Exception; 
	public Integer getEmpListForSixPCArrearsCount(String lStrDDOCode,String lStrDesignation) throws Exception ;
	String getDdoCodeForDDO(Long lLngPostId) throws Exception;
	public Integer getEmpListForSixPCArrearsDDOCount(String lStrDDOCode,String lStrDesignation) throws Exception;
	public List getEmpListForSixPCArrearsDDO(String lStrDDOCode,String lStrDesignation, Map displayTag) throws Exception ;
	String getDdoCode(Long lLngAsstPostId) throws Exception;
	
}
