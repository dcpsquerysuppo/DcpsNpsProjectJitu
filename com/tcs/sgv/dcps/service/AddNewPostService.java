package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface AddNewPostService {
	
	public ResultObject showAdminOrgPostDtl(Map<String, Object> inputMap);
	
	public ResultObject showNewPostForm(Map<String, Object> inputMap);
	
	public ResultObject showAdminPostForm(Map<String, Object> inputMap);
	
	public ResultObject getDdoListFromFieldDept(Map<String, Object> inputMap);
	
	public ResultObject submitAdminOrgPostDtl(Map<String, Object> inputMap);
	
	public ResultObject getOfficeCityClassAndHRA(Map<String, Object> inputMap);
}
