package com.tcs.sgv.common.dao;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface UpdateEmpDOJDAO extends GenericDao {
	public List getEmployeeInfo(String lStrSevaarthId);

	public void updateDOJ(String lStrSevaarthId, Date lDtDOJ);

	public void updateJoiningDate(Long lLngOrgEmpId, Date lDtDOJ);
	
	public void updateJoiningDateTable(String lStrSevaarthId, String lStrEmpName,Long postId ,String lStrDdoCode, Date lDtOldDOJ,Date lDtNewDOJ,String txtReason);

}
