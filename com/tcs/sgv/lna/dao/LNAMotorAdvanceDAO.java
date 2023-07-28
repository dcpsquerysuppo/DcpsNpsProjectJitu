package com.tcs.sgv.lna.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface LNAMotorAdvanceDAO extends GenericDao {
	public List getMotorAdvance(String lStrSevaarthId, Long lLngRequestType);

	public List getMotorAdvanceToDEOApprover(Long lLngHouseAdvnId);

	public Boolean requestDataAlreadyExists(String lStrSevaarthId, Long lLngRequestType);

	public Boolean requestPendingStatus(String lStrSevaarthId);

	public Boolean requestCheck(String lStrSevaarthId, Long lLngRequestType);

	public Double getMotorSubtype(String lStrSevaarthId);

}
