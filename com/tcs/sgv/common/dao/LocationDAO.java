package com.tcs.sgv.common.dao;

import java.util.ArrayList;
import java.util.List;

import com.tcs.sgv.common.valueobject.CmnDistrictMst;


public interface LocationDAO {

	public List getDeptLocation(String lookupName, String langName) throws Exception;

	public String getDeptCodeByLocaId(long billNo, String langName) throws Exception;

	public String getDeptCodeByLocId(String locId, String langName) throws Exception;

	public List getLocByDept(String lStrDeptCode, Long lLngLangId, Long lLngDbId) throws Exception;

	public CmnDistrictMst getDistCodeFrmLocId(String lStrLocId, Long lLngLangId) throws Exception;

	public List getLocByDept(ArrayList lDeptCode, Long lLngLangId) throws Exception;

	public String getLocationName(String lLocationCode, String langId) throws Exception;

	public List getAllDistrict(Long lLngLangId) throws Exception;

	public List getAllSecretariateDept(Long lLngLangId) throws Exception;

	public List getAllDistrictOffices(Long lLngLangId) throws Exception;

	public List getAllHod(Long lLngLangId) throws Exception;

	public List getHodByDept(String strLocationCode, Long lLngLangId) throws Exception;
	
	public List getAllDistrictCode(Long lLngLangId) throws Exception;
}
