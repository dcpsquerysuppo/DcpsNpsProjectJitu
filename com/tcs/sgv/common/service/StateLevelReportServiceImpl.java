package com.tcs.sgv.common.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.StateLevelReportDAOImpl;
import com.tcs.sgv.common.dao.UserConfigDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnDistrictMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.fms.valueobject.WfHierachyPostMpg;

public class StateLevelReportServiceImpl extends ServiceImpl 
{
	private final Log gLogger = LogFactory.getLog(getClass());


	public ResultObject LoadStateLevelReport(Map inputMap)
	{
		  HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		  ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		  ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		  List lLstDepartment = null;
		  List lLstDistrict = null;		  
		  List lLstDdoOffice = null;
		  List lLstFinYear = null;
		  List lLstMonth = null;
		 
		  try{
			  
			  StateLevelReportDAOImpl lObjStateLevelReportDAOImpl = new StateLevelReportDAOImpl(CmnDistrictMst.class,serv.getSessionFactory());
			  String lStrUser = StringUtility.getParameter("user", request);
			  
			  lLstDepartment = lObjStateLevelReportDAOImpl.getAllDepartment();
			  lLstDistrict = lObjStateLevelReportDAOImpl.getAllDistrict();			  
			  lLstFinYear = lObjStateLevelReportDAOImpl.getAllFinyear();
			  lLstMonth = lObjStateLevelReportDAOImpl.getAllMonth();
			  
			  inputMap.put("deptList", lLstDepartment);
			  inputMap.put("districtList", lLstDistrict);
			  inputMap.put("yearList", lLstFinYear);
			  inputMap.put("monthList", lLstMonth);
			  
			  
			  if (lStrUser.equals("ADMIN")){
				  resObj.setResultValue(inputMap);
				  resObj.setViewName("StateLevelReport");
			  }else{
				  Long tryCode =  SessionHelper.getLocationId(inputMap);
				  DcpsCommonDAO lObjDcpsCommonDAOImpl = new DcpsCommonDAOImpl(null,serv.getSessionFactory());
				  String tryName = lObjDcpsCommonDAOImpl.getLocNameforLocId(tryCode);
				  inputMap.put("tryCode",tryCode);
				  inputMap.put("tryName",tryName);
				  resObj.setResultValue(inputMap);
				  resObj.setViewName("PayBillReport_TO");
				  
			  }
		  }catch(Exception e){
				IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in LoadStateLevelReport");
		  }
		  return resObj;
	}
	
	public ResultObject getDDOOffices(Map objectArgs)
	{
		 ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, DBConstants.CONST_FAIL);
		 ServiceLocator serv = (ServiceLocator)objectArgs.get(DBConstants.SERV_LOCATOR);
		 HttpServletRequest request = (HttpServletRequest) objectArgs.get(DBConstants.REQUEST_OBJ);		 		 
		 List lLstAllLocs = null;
		 String lStrTempResult = "";
		  
		  try
		  {		
			  StateLevelReportDAOImpl lObjStateLevelReportDAOImpl = new StateLevelReportDAOImpl(CmnDistrictMst.class,serv.getSessionFactory());
			  String lStrDistrictId = StringUtility.getParameter("district", request);
			  
			  if(!lStrDistrictId.equals("")){
				  lLstAllLocs = lObjStateLevelReportDAOImpl.getAllOffices(lStrDistrictId);
			  }
			  
			  if (lLstAllLocs != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(lLstAllLocs, "desc", "id", true).toString();
			  }
			
			  objectArgs.put("ajaxKey", lStrTempResult);
			  objRes.setResultValue(objectArgs);
			  objRes.setViewName("ajaxData");
		  }
		  catch(Exception e)
		  {
			  IFMSCommonServiceImpl.setErrorProperties(gLogger, objRes, e, "Error in getDDOOffices");
		  }
		  return objRes;
	}
}
