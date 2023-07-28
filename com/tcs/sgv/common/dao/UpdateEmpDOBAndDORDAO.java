package com.tcs.sgv.common.dao;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface UpdateEmpDOBAndDORDAO extends GenericDao {
	public List getEmployeeInfo(String lStrSevaarthId);

	public void updateDOB(String lStrSevaarthId, Date lDtDOB, Date lDtDOR);

	public void updateDOR(Long lLngOrgEmpId, Date lDtDOB, Date lDtDOR, String txtReason);
}
