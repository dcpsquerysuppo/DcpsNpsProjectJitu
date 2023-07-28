package com.tcs.sgv.lna.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface LNAHouseAdvanceDAO extends GenericDao {
	public List getHouseAdvance(String lStrSevaarthId, Long lLngRequestType);

	public List getHouseAdvanceToDEOApprover(Long lLngMotorAdvnId);

	public Integer requestDataAlreadyExists(String lStrSevaarthId, Long lLngRequestType);

	public List getGuarantorDtls(String lStrEmpCode);

	public Boolean requestPendingStatus(String lStrSevaarthId);

	public List getSubtypeDtlsforDisbursement(String lStrSevaarthId);

	public List getEligibleStatusForExtnOfRoom(String lStrSevaarthId);
}
