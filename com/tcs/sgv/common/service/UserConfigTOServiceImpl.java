package com.tcs.sgv.common.service;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.UserConfigTODAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;

/** 
 * 
 * @author Jayraj Chudasama
 */

public class UserConfigTOServiceImpl extends ServiceImpl{
	
	private final Log gLogger = LogFactory.getLog(getClass());
	
	public ResultObject loadUserConfigTO(Map inputMap)
	{
		ServiceLocator serv = (ServiceLocator)inputMap.get(DBConstants.SERV_LOCATOR);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrLocName = "";  
		
		  try{
			  UserConfigTODAOImpl lObjConfigTODAOImpl = new UserConfigTODAOImpl(CmnLocationMst.class,serv.getSessionFactory());
			  String gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			  lStrLocName = lObjConfigTODAOImpl.getLocationName(gStrLocationCode);
				  
			  inputMap.put("locCode", gStrLocationCode);
			  inputMap.put("locName", lStrLocName);
			  			  
			  resObj.setResultValue(inputMap);
			  resObj.setViewName("gpfUserMappingTO");
		  }catch(Exception e){
				IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadUserConfigTO");
			}
		return resObj;
	}

}
