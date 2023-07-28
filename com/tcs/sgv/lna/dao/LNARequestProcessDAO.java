package com.tcs.sgv.lna.dao;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.eis.valueobject.HrEisScaleMst;

public interface LNARequestProcessDAO extends GenericDao {
	public List getGPFEmployeeDetail(Long lLngEmpId);

	public List getDCPSEmployeeDetail(Long lLngEmpId);

	public List getEmployeeDcpsOrGpf(String lStrSevaarthId, String empName, String criteria, String lStrDdoCode, String lStrHodLocCode,String lStrUser);

	public List getEmployeeDetailForApprover(String lStrPostId);

	public String getNewTransactionId(String lStrSevaarthId, Long lLngAdvanceType);

	public Boolean checkEligibilityForLNA(String lStrSevaarthId);

	public HrEisScaleMst getPayScaleData(Long empId);

	public List getCheckList(String lStrSevaarthId, Long lLngReqType, Long lLngReqSubType);

	public List getChecklistPk(String lStrSevaarthId, Long lLngRequestType, Long lLngReqSubType);

	public List getEmpNameForAutoComplete(String searchKey, String lStrDdoCode, String lStrHodLocCode,String lStrUser);

	public List getDraftRequestList(String lStrCriteria, String lStrName, Date lDtSaveDate, String lStrHodLocCode, String gStrPostId);

	public List getEmpLoanDetails(Long lLngUserId);

	public List getEmpBankDetails(String lStrSevaarthId);
	
	public String getEmpNameFromSevaarthId(String lStrSevaarthId);
	
	public List<ComboValuesVO> getAllDepartment(Long lLngLocationCode, Long langId) throws Exception;
	
	public List getEmployeeDetailForApprover(Long lLngOfficeId,Date lDtFromDate,Date lDtToDate,String lStrPostId);
	
	public String generateOrderNo();
	
	public Boolean isPriLoanMappedWithDDO(String lStrSevaarthId,Long lLngLoanType);
	
	public Boolean isIntLoanMappedWithDDO(String lStrSevaarthId,Long lLngLoanType);
	
	public String getAdvNameFromId(Long lLngLoanId);

	public String getDdoCodeFromLocCode(String lStrLocationCode);

	public String getSevarthIDfromEmpName(String strName);

	public Boolean isPriLoanAlreadyTaken(String lStrSevaarthId, Long lLngRequestType);
	
	public Boolean isIntLoanAlreadyTaken(String lStrSevaarthId, Long lLngRequestType);
	
	public List getHROList(Long locationCode);
	
	public Long getHODPostId(Long locationCode);
}
