package com.tcs.sgv.dcps.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DeleteCsrfFormDaoImpl;
import com.tcs.sgv.dcps.dao.DeleteCsrfFormDaoImpl;
import com.tcs.sgv.dcps.dao.updatePranNoDAOImpl;
import com.tcs.sgv.dcps.valueobject.UploadPranNo;

public class DeleteCsrfFormServImpl extends ServiceImpl {
	/* Global Variable for Logger Class */
	private final Log logger = LogFactory.getLog(getClass());

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private void setSessionInfo(Map inputMap) {
		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
		} catch (Exception e) {
		}
	}
		
	public ResultObject loadEmpList(Map inputMap) throws Exception {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		logger.info("Inside loadEmpList ");

		String lStrSevaarthId = null;
		List lLstEmpDeselect = null;
		
		try {
			setSessionInfo(inputMap);
			DeleteCsrfFormDaoImpl objUpdateDao = new DeleteCsrfFormDaoImpl(null, serv.getSessionFactory());
			
			if (StringUtility.getParameter("fromSearch", request).trim().equals("Yes")) {
				lStrSevaarthId = StringUtility.getParameter("txtSevaarthId", request).trim().toUpperCase();
				lLstEmpDeselect = objUpdateDao.getAllEmp(lStrSevaarthId );
			}
		if(lLstEmpDeselect != null && lLstEmpDeselect.size()>0){
				inputMap.put("DESELECTEMPLIST", lLstEmpDeselect);
		}
				objRes.setResultValue(inputMap);
				objRes.setViewName("deleteCsrfForm");				
			}
			catch (Exception e) {
				logger.info("Exception is " + e);
				objRes.setResultValue(null);
				objRes.setThrowable(e);
				objRes.setResultCode(ErrorConstants.ERROR);
				objRes.setViewName("errorPage");
			}
			return objRes;
	}
	
	public ResultObject deleteForm(Map inputMap)
	{
	  ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
	  String SevaarthId = null;

	  String flag= null;
		logger.info("inside service deleteForm");
	  try
	  {
		 setSessionInfo(inputMap);
		 SevaarthId = StringUtility.getParameter("txtSevaarthId", request).trim();

		 DeleteCsrfFormDaoImpl lObjcheck = new DeleteCsrfFormDaoImpl(null,serv.getSessionFactory());
			logger.info("SevaarthId:"+SevaarthId);

		 if(SevaarthId!= null && SevaarthId!= ""){
		 flag= lObjcheck.deleteForm(SevaarthId);
		 } 
			logger.info("FLAG :"+flag);
			StringBuffer strbuflag = new StringBuffer();

			if(flag !=null){
			strbuflag.append("<XMLDOC>");
			strbuflag.append("<Flag>");
			strbuflag.append(flag);
			strbuflag.append("</Flag>");
			strbuflag.append("</XMLDOC>");
			}
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",strbuflag.toString()).toString();
			if(lStrResult !=null){
			inputMap.put("ajaxKey", lStrResult);
			}
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
	  }
	  
	 catch (Exception e) {
		 objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
	}
	 return objRes;
	}
	
	
}