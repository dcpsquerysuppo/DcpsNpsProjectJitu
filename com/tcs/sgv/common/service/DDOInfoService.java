package com.tcs.sgv.common.service;

import com.tcs.sgv.core.service.ServiceLocator;

import java.util.List;

/**
 * 
 * @author 602399
 * 
 */

public interface DDOInfoService 
{	
	public List getDDOInfo(String lStrDDOCode, Long lLngLangId, Long lLngDBId, ServiceLocator serv);
	public List getDDOInfoByPost(Long lLngPostId, Long lLngLangId, Long lLngDBId, ServiceLocator serv);
    public List getCompleteDDOInfoByCardex(String lStrCardexNo, String lStrOfficeCode, Long lLngLangId, Long lLngDBId, ServiceLocator serv);
	public List getDDOInfoByNo(String lStrDDONo, String lDDOType, Long lLngLangId, Long lLngDBId, ServiceLocator serv);
	public List getBillOfficeForDDO(ServiceLocator serv, String lStrDDOCode, Long lLngLangId, Long lLngDBId);
}
