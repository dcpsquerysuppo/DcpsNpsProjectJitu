package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface NsdlReportsDAO extends GenericDao{

	List getFinyears();

	List getDetails(String lstrFinYear, String lstrMonth, String strLocationCode, String pranWise);

	String getStatusOfDist(String strLocationCode);

}
