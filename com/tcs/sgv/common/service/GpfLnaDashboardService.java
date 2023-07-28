package com.tcs.sgv.common.service;

import com.tcs.sgv.core.valueobject.ResultObject;
import java.util.Map;

public abstract interface GpfLnaDashboardService
{
  public abstract ResultObject getUserType(Map<String, Object> paramMap);
}
