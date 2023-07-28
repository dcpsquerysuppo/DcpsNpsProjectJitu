package com.tcs.sgv.common.service;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.tcs.sgv.common.dao.ReSetPwdForTOAndAsstDAOImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.ess.valueobject.OrgEmpMst;

public class ReSetPwdForTOAndAsstServiceImpl extends ServiceImpl
{	
	private final Log gLogger = LogFactory.getLog(getClass());
	
	public ResultObject loadResetPwdToAndAsst(Map<String, Object> inputMap)
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");		
		
		try{
			ReSetPwdForTOAndAsstDAOImpl lObjReSetPwdDaoImpl = new ReSetPwdForTOAndAsstDAOImpl(OrgEmpMst.class,serv.getSessionFactory());			
			
			List lLstToAndAsst = lObjReSetPwdDaoImpl.loadResetPassword();
			
			inputMap.put("ToAnsAsstList", lLstToAndAsst);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ReSetPasswordForTO");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadResetPwdToAndAsst");
		}
		
		return resObj;
	}	
}
