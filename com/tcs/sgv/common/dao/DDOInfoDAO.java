package com.tcs.sgv.common.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.common.valueobject.OrgDdoMst;


/**
 * 
 * @author Nirav Bumia
 * 
 */

public interface DDOInfoDAO 
{
	public List getDDOInfo(String lStrDDOCode, Long lIntLangId, Long lIntDBId)  throws Exception;
	public List getDDOInfoByPost(Long lIntPostId, Long lIntLangId, Long lIntDBId)  throws Exception;
	public List getCompleteDDOInfoByCardex(Integer lIntCardexNo, String lStrOfficeCode, Long lLngLangId)  throws Exception;
	public List getDDOInfoByCardexNo(Integer lIntCardexNo, String lStrOfficeCode, Long lLngLangId, Long lLngDBId)  throws Exception;
	public List getDDOInfoByName(String lStrDDOName,String lStrOfficeCode,Long lLngLangId, Long lLngDBId)  throws Exception;
	public List getDDOInfoByNo(Long lLngDDONo, String lDDOType, Long lLngLangId, Long lLngDBId)  throws Exception;
	public List getBillOfficeForDDO(String lStrDDOCode, Long lLngLangId, Long lLngDBId)  throws Exception;
    public List getTrsryOfficeForDDO(String lStrDDOCode, Long lLngLangId)  throws Exception;
    public List getOrgPostMstbyPost(Long lLngPostId) throws Exception;
    public List getOrgDesgMstbyDesgCode(String lStrDesgnCode) throws Exception;
    public String getLocationCodeFromDdoCode(String lStrDdoCode, Long lLngLangId) throws Exception;
    public String getAllCardexByLocation(String lStrLocCode, Long lLngLangId) throws Exception;
    public Date getLastBillDate(Integer lIntCardexNo,String lStrOfficeCode, Long lLngLangId, Date lDtCurDate) throws Exception;
    public String getCardexMapping(List subLoCode, Long lLngLangId) throws Exception;
    public List<OrgDdoMst> getAllAdminDDO(Long lLngLangId, Long lLngDBId) throws Exception;
    public boolean ddoBilltypeExists(Long subjectId, String ddoCode, Long langId) throws Exception;
    public List getDDOInfoByOfficeCodeAndMjrHd(String majorHead, String officeCode, Long lLngLangId, Long lLngDBId) throws Exception;
}
