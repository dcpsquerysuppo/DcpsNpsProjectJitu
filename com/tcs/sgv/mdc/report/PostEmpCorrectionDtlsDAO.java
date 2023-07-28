package com.tcs.sgv.mdc.report;

import java.util.List;

import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.core.dao.GenericDao;

public interface PostEmpCorrectionDtlsDAO extends GenericDao
{
	public	List getAllCorrectemployee() throws Exception;

	public	List getAdminName() throws Exception;
	public	List getFieldName() throws Exception;
	public	List getFieldbyAdmin(Long adminID) throws Exception;
	  public List getTotalDDOCount() throws Exception;
	  public List getTotalCountWhoHaveEnter() throws Exception;

}
