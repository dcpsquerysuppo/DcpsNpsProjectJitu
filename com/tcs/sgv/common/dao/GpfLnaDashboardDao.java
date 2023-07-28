package com.tcs.sgv.common.dao;

import com.tcs.sgv.core.dao.GenericDao;

public abstract interface GpfLnaDashboardDao
  extends GenericDao
{
  public abstract String getCount(String paramString);
  
  public abstract String getUserType(Long paramLong);
  
  public abstract String getLNANoOfApprovedRequests(String paramString1, String paramString2, String paramString3);
  
  public abstract String getNoOfOrdersGenerated(String paramString1, String paramString2, String paramString3);
  
  public abstract String getLNADraftRequestCount(String paramString1, String paramString2);
  
  public abstract String getRejectedReqCount(String paramString1, String paramString2);
}
