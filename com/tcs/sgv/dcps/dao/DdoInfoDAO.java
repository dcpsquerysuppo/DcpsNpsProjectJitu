/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 19, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.dcps.valueobject.DDOInformationDetail;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Mar 19, 2011
 */
public interface DdoInfoDAO extends GenericDao {

	public DDOInformationDetail getDdoInfo(String lStrDdoCode);

	public Boolean checkDdoExistOrNot(String lStrDdoCode);

	public OrgDdoMst getDdoInformation(String lStrDdoCode);
	
	public void updateDdoName(Long lLngDdoAstPostId, String lStrName);
	
	public void updateParentLocInCmnLocMstForDDO(Long lLongLocIdOfDDO,String lStrFieldHodDept);
	
	public void updateDesigInOrgPost(String lStrDDOCode,String lStrDdoDesignation);
	
	public void updateDesigNameInOrgPostDtlRlt(String lStrDDOCode,String lStrDdoDesignation);

}
