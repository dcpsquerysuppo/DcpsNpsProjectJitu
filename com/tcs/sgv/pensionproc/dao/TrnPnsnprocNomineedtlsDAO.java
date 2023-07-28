/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 7, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocNomineedtls;

/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Mar 7, 2011
 */
public interface TrnPnsnprocNomineedtlsDAO extends GenericDao{
	
	List<TrnPnsnprocNomineedtls> getListOfPnsnrNomineeDtls(Long lLngInwardPensionId) throws Exception ;
	
	List<TrnPnsnprocNomineedtls> getPnsnrNomineeDtls(Long lLngInwardPensionId) throws Exception ;

}
