package com.tcs.sgv.common.dao;

import java.util.List;
import java.util.Map;

import com.tcs.sgv.common.valueobject.EmpPostVO;

public interface PostConfigurationDAO {
	
	public EmpPostVO getSelectedEmp(String office_code, String branch_code, Map mpArg,Long lLngLangId);
	public List getEmpsList(String office_code, String branch_code,Long lLngLangId);
	public long getChallanPostId(String lStrMajorHead,String lStrLocationCode) throws Exception;
    public Map getChqPostId(String lStrAccNo, String lStrLocationCode,Integer lIntFinYrId) throws Exception;
    public List getForwardUser(String lStrLocCode, long lLangId, long lPostId, long lUserId) throws Exception;
}
