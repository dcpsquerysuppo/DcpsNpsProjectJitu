package com.tcs.sgv.dcps.TestWebService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps._TDSWebService.TDSWebService;

public class EmpWebServiceImpl extends ServiceImpl{
	private final static Logger gLogger = Logger.getLogger(TDSWebService.class);
	public ResultObject empDetailsWSTest(Map inputMap)throws Exception 
	{
		gLogger.info("inside empDetailsWSTest");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");

		String sevarthId = inputMap.get("sevaarthId").toString();
gLogger.info("1"+sevarthId);
		EmpDetailsWsDAOImpl dao = new EmpDetailsWsDAOImpl(null, serv.getSessionFactory());
		List empData = dao.getEmpDetails(sevarthId);
		gLogger.info("2 empData: "+Arrays.toString(empData.toArray()));
		Object obj[] =null;
		String empName ="";
		String dob="";
		String doj="";
		if(empData!=null){
			obj = (Object[]) empData.get(0); 
			empName= obj[0].toString();
			dob=obj[1].toString();
			doj=obj[2].toString();
		}
		gLogger.info("3");
		EmpData emp = new EmpData();
		emp.setEmpName(empName);
		emp.setDob(dob);
		emp.setDoj(doj);
		gLogger.info("4");
		inputMap.put("emp", emp);
		return resObj;
	}

}
