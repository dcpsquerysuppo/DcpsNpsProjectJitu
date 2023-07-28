package com.tcs.sgv.mdc.report;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDao;

public interface DemoReportDAO  extends GenericDao {

	//public Map getFieldlist(String name);
	
	public List getTotalDDOCountEnter(Long Loc_code, String field);
	public Long getFieldCount(Long lStrAdminName) ;
	public List getTotalDDOCount(Long Loc_code);
	public Long getEnteredFieldCount(Long lStrAdminName);
	public List getTotalDDOCountnotenter(Long Loc_code);
	public List getTotalEmployeeCount(Long Loc_code);
	
	public List getTotalEmployeeEnterCount(Long Loc_code);
	public List getDDOInformation(String lStrAdminid,String lStrfieldid);
	public Long getTotalNoOFDDOCount(String Loc_code,String Loc_id);
	public Long getCountTotalNoOFDDOEnter(String Loc_code,String Loc_id) ;
	
	public List getField_ID(Long Loc_code);
	
	
}
