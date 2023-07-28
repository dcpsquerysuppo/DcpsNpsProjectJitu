package com.tcs.sgv.lna.dao;

import java.util.List;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDao;

public interface LNADataEntryFormDAO extends GenericDao {

	public List<ComboValuesVO> getFinYear();

	public List getEmpDtls(String lStrEmpCode, String lStrHodLocCode);

	public Long getBillGroupId(String lStrSevaarthId);

	public List getDraftReq(String lStrUserType, String lStrHodLocCode);

	public List getCompAdvance(String lStrSevaarthId, String lStrUserType);

	public List getHouseAdvance(String lStrSevaarthId, String lStrUserType);

	public List getMotorAdvance(String lStrSevaarthId, String lStrUserType);

	public List getVoucherDtls(String lStrSevaarthId, Long lLngAdvance, String lStrUserType);

	public List getChallanDtls(String lStrSevaarthId, String lStrTransactionId, String lStrUserType);

	public List getPrevVoucherDtls(String lStrSevaarthId, Long lLngAdvance);

	public List getPrevChallanDtls(String lStrSevaarthId, String lStrTransactionId);

	public void updateVoucherDtls(String lStrSevaarthId);

	public void updateChallanDtls(String lStrSevaarthId);

	public void updateEmpLoanDtls(String lStrEmpLoanDtlsId);

	public void updateCompAdvance(String lStrCompAdvanceId, String lStrRemark);

	public void updateHouseAdvance(String lStrHouseAdvanceId, String lStrRemark);

	public void updateMotorAdvance(String lStrMotorAdvanceId, String lStrRemark);

	public String requestPendingStatus(String lStrSevaarthId);

	public List<ComboValuesVO> getAllTreasury(Long lLngDepartmentId, Long langId) throws Exception;
		
	public List getEmpLoanDtlsFromPayroll(String lStrSevaarthId, Long lLngLoanType);
	
	public List getEmpLoanRecoveryDtlsFromPayroll(String lStrSevaarthId, Long lLngLoanType);

	public List getNewEmpDtls(String lStrEmpCode);
}
