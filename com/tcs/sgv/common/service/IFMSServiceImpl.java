package com.tcs.sgv.common.service;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;

public class IFMSServiceImpl extends ServiceImpl {

	
	public ServiceLocator serviceLocator ;
	public ResultObject resultObj  ;        
	public HttpServletRequest requestObj;
	
	
	public Long gLongUserId = null;
	
	public Long gLongPostId = null;
	
	    /* Global Variable for LangId */
    public Long gLongLangId = null;

    /* Global Variable for Location Code */
    public String gStringLocationCode = null;

    /* Global Variable for DB Id */
    public Long gLongDBId = null;

    /* Global Variable for Current Date */
    public Date gDateCurrDt = null;
	
	     
}
