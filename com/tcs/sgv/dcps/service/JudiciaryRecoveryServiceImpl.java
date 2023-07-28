package com.tcs.sgv.dcps.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.JudiciaryRecoveryDAOImpl;

public class JudiciaryRecoveryServiceImpl extends ServiceImpl {
	Log gLogger = LogFactory.getLog(getClass());
	public ResultObject getEmpDtlsFromSevaarthId(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		JudiciaryRecoveryDAOImpl dao = new JudiciaryRecoveryDAOImpl(null, null);
		try{
			String lStrSevaarthId = StringUtility.getParameter("SevaarthId", request);
			List lLstEmployeeInfo = dao.getEmployeeInfo(lStrSevaarthId);
			
		}
		catch (Exception e) {
			// TODO: handle exception
		}

		
		return null;
		
	}

}
