/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 2, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.dao;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.dcps.valueobject.DdoOffice;

/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 Feb 2, 2011
 */
public interface TrnPnsnProcPnsnrDtlsDAO extends GenericDao {
		String getDesignation(Long lLngEmpId);
		
		Long getFieldDept(String lStrDdoCode);
		
		DdoOffice getEmpOfficeDtls(Long lLngOfficeId);
		
		String getBankAccNo(Long lLngEmpId);
}
