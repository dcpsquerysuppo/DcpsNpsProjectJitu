package com.tcs.sgv.common.dao;

import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.dao.GenericDao;
import java.util.List;
import java.util.Map;

public abstract interface IFMSCommonDAO
  extends GenericDao
{
  public abstract List findByNamedQuery(String paramString)
    throws Exception;
  
  public abstract List findByNamedQuery(String paramString, Map<String, Object> paramMap)
    throws Exception;
  
  public abstract int deleteOrUpdateByNamedQuery(String paramString)
    throws Exception;
  
  public abstract int deleteOrUpdateByNamedQuery(String paramString, Map<String, Object> paramMap)
    throws Exception;
  
  public abstract Map<String, List<CmnLookupMst>> getPartialCommonLookupMstVo(List paramList, Long paramLong)
    throws Exception;
}
