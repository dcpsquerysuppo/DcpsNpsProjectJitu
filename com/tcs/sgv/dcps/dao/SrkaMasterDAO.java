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

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.dcps.valueobject.DcpsCadreMst;
import com.tcs.sgv.dcps.valueobject.MstDcpsOrganization;

/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Apr 8, 2011
 */
public interface SrkaMasterDAO extends GenericDao {

	List<MstDcpsOrganization> getOrgDsplyDtls() throws Exception;

	public List<DcpsCadreMst> getCadreList(Long CurrFieldDeptId) throws Exception ;

	List<ComboValuesVO> getCadres() throws Exception;

	public List getDesigDsplyDtls(Long CurrFieldDeptId) throws Exception ;

	public List<ComboValuesVO> getDdoCodes(String lStrTreasuryCode);

	public List<ComboValuesVO> getFieldDept(Long lLngDepartmentId);
	
	public Long getMaxCadreInFieldDept(Long lLongFieldDeptId) ;
	
	public Long getMaxDesigInFieldDept(Long lLongFieldDeptId) ;
	
	public Boolean checkDesigExistsInOrgDesigMstOrNot(String lStrDesigName) throws Exception ;
	
	public Long getDesigIdForDesigName(String lStrDesigName)  throws Exception;
	
	public Boolean checkDesigExistInCadreAndFieldDept(String lStrDesigName,Long lLongFieldDept) throws Exception ;
	
}
