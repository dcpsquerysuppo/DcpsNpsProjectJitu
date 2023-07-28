package com.tcs.sgv.dcps.dao;

import com.tcs.sgv.core.dao.GenericDao;
import java.util.List;

public abstract interface UpdatePranForEmpDao extends GenericDao
{
  public abstract List getEmpInfoFromSevaarthId(String paramString1, String paramString2);

  public abstract List CheckPranActiveOrNot(String paramString1, String paramString2);

  public abstract List CheckPostActiveOrNot(String paramString1, String paramString2);

  public abstract void UpdatePranDeActiveStatus(long paramLong1, String paramString1, String paramString2, String paramString3, long paramLong2, long paramLong3);
}