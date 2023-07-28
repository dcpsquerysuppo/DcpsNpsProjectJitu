/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 5, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcPnsnrDtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocFamilydtls;

/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Mar 5, 2011
 */
public interface TrnPnsnprocFamilydtlsDAO extends GenericDao{
	
	List<TrnPnsnprocFamilydtls> getListOfPnsnrFamilyDtls(Long lLngInwardPensionId) throws Exception ; 
}
