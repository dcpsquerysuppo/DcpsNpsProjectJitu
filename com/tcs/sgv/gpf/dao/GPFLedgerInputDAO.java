/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Dec 12, 2011		Chudasama Jayraj								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.dao;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description - 
 *
 *
 * @author Chudasama Jayraj
 * @version 0.1
 * @since JDK 5.0
 * Dec 12, 2011
 */
public interface GPFLedgerInputDAO extends GenericDao
{
	String getEmpNameFromUserId(Long lLngUserId) throws Exception;
}
