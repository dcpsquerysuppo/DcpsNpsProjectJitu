/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 18, 2011		Kapil Devani								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.dcps.valueobject.HstDcpsChanges;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.RltDcpsPayrollEmp;

public interface ChangesFormDAO extends GenericDao {

	//public List getAllDcpsEmployees(String lStrDesignationId, String lStrDdoCode);

	public MstEmp getEmpDetails(Long dcpsEmpId);

	public List getCurrentOffices();

	public List getNominees(String empId);

	public List getChangesDraftsForDesig(String lStrDesignationId,
			String lStrUserType, String lStrDDOCode);

	public HstDcpsChanges getChangesDetails(Long dcpsChangesId);

	public Long getPersonalChangesIdforChangesId(Long dcpsChangesId);

	public RltDcpsPayrollEmp getEmpPayrollDetailsForEmpId(Long dcpsEmpId);

	public Long getOfficeChangesIdforChangesId(Long dcpsChangesId);

	public Long getOtherChangesIdforChangesId(Long dcpsChangesId);

	public void deleteNomineesFromHstForGivenEmployee(Long lLongEmpId);

	public Long getLatestRefIdForNomineeChanges(Long dcpsEmpId,
			Long dcpsChangesId);

	public List getNomineesFromHst(Long changesNomineeRefId, Long dcpsEmpId);

	public List getChangesFromTrnForChangesId(Long dcpsChangesId);

	public Boolean checkPkInTrnExistsForTheChange(String fieldName,
			String oldValue, Long dcpsChangesId);

	public Long getPksFromTrnForTheChange(String fieldName, String oldValue,
			Long dcpsChangesId);

	public Long getPkFromTrnForTheChangeInPhotoSign(Long dcpsChangesId,
			String fieldName);

	public void deleteTrnVOForPk(Long TrnIdPk);

	public void deleteTrnVOForDcpsChangesId(Long dcpsChangesId);

	public Date getDobForTheEmployee(Long dcpsEmpId);

	public Long getNextRefIdForHstNomineeChanges(Long dcpsEmpId);

	public List getPhotoSignNewValue(Long lLngChangesId);
	
	public String getGroupIdForCadreId(Long cadreId);

	public List getAllDcpsEmployees(String lStrDesignationId,
			String lStrDdoCode, String sevarthId, String employeeName);

	public String getGradeForGivenStateLevel(String level);

	public List getStateBasicAsPerGrade(String grade);

	public String getGradeForGivenLevel(String level);

	public List getBasicAsPerGrade(String grade);
}
