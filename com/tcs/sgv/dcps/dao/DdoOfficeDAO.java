/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 26, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.dcps.valueobject.DDOInformationDetail;
import com.tcs.sgv.dcps.valueobject.DdoOffice;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 26, 2011
 */
public interface DdoOfficeDAO extends GenericDao {

	public List getAllOffices(String lStrDdoCode);

	public DDOInformationDetail getDdoInfo(String lStrDdoCode);

	public DdoOffice getDdoOfficeDtls(Long ddoOfficeId);

	public void updateDdoOffice(String lStrDdoOffice, String lStrDdoCode);

	public String getDefaultDdoOffice(String lStrDdoCode);
	
	public void updateOtherOfficeToNO(Long lLongDdoOfficeId,String lStrDdoCode);
	
	//added by arpan
	public List isNewAddressAdded (String lStrDdoCode);

	
}
