package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface ViewReportDAO extends GenericDao{
	public List getAllDDOForContriForwardedToTO(String lStrTreasuryLocCode,int flag) ;
	public List getTreasuryForDDO(String lStrDdoCode);
	public List getSubTreasury(long l);
}
