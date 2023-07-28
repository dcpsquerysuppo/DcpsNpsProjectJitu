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
import com.tcs.sgv.dcps.dao.DeactivatePranNoDao;
import com.tcs.sgv.dcps.dao.DeactivatePranNoDaoImpl;

public class DeactivatePranNoServImpl extends ServiceImpl {
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
		String lStrPranNo = null;
		List lLstEmpDeselect = null;
		
		try {
			setSessionInfo(inputMap);
			DeactivatePranNoDao objUpdateDao = new DeactivatePranNoDaoImpl(null, serv.getSessionFactory());
			
			if (StringUtility.getParameter("fromSearch", request).trim().equals("Yes")) {
				lStrSevaarthId = StringUtility.getParameter("txtSevaarthId", request).trim().toUpperCase();
				lStrPranNo = StringUtility.getParameter("txtPranNo", request).trim().toUpperCase();
				lLstEmpDeselect = objUpdateDao.getAllEmp(lStrSevaarthId,lStrPranNo );
			}
		if(lLstEmpDeselect != null && lLstEmpDeselect.size()>0){
				inputMap.put("DESELECTEMPLIST", lLstEmpDeselect);
		}
				objRes.setResultValue(inputMap);
				objRes.setViewName("deactivatePranNo");				
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
	
	public ResultObject loadAllFiles(Map inputMap) throws Exception {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		logger.info("Inside loadAllFiles ");

		String lStrPranNo = null;
		String lStrSevId = null;

		List lLstEmpDeselect = null;
		List lLstFileSelect = null;
		try {
			setSessionInfo(inputMap);

			DeactivatePranNoDao objUpdateDao = new DeactivatePranNoDaoImpl(null, serv.getSessionFactory());		
			if (StringUtility.getParameter("fromSearch", request).trim().equals("Yes")) {
				lStrPranNo = StringUtility.getParameter("txtPranNo", request).trim().toUpperCase();
				lStrSevId = StringUtility.getParameter("txtSevId", request).trim().toUpperCase();
				lLstFileSelect = objUpdateDao.getAllFile(lStrPranNo);
				lLstEmpDeselect = objUpdateDao.getAllEmp(lStrSevId,lStrPranNo );
				}

				logger.info("####### in lLstEmpDeselect1");
				if(lLstEmpDeselect != null){
				inputMap.put("DESELECTEMPLIST", lLstEmpDeselect);
				}
				if(lLstFileSelect != null){
				inputMap.put("SELECTFILES", lLstFileSelect);
				}
				objRes.setResultValue(inputMap);
				objRes.setViewName("deactivatePranNo");				
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
	
public ResultObject checkSevaarthId(Map inputMap)throws Exception {
	logger.info("Inside checkSevaarthId ");

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrSevaarthId = null;
		String pranNo= null;
		String pranActive= null;
		String lStrPranNo = null;
		String NotExist="";
		
		try {
		setSessionInfo(inputMap);
		DeactivatePranNoDao lObjcheck = new DeactivatePranNoDaoImpl(null,serv.getSessionFactory());
		lStrSevaarthId = StringUtility.getParameter("txtSevaarthId", request).trim().toUpperCase();
		lStrPranNo = StringUtility.getParameter("txtPranNo", request).trim().toUpperCase();
		logger.info("in checkSevaarthId"+" lStrSevaarthId"+lStrSevaarthId+" lStrPranNo"+lStrPranNo);

		List existList = lObjcheck.checkSevaarthIdExist(lStrSevaarthId,lStrPranNo);

		label:
		if(existList== null || existList.size()==0)
		{
			NotExist="NA";
			  break label;
		}	
		label:
		if(existList!= null && existList.size()>0)
		{			
		Object[] tuple = (Object[]) existList.get(0);
		pranNo= tuple[0].toString();
		pranActive = tuple[2].toString();
		  if (pranNo.equals("#"))
		  {
			  NotExist="NA1"; 
			  break label;
		  }
		  if (pranActive.equals("0"))
		  {
			  NotExist="NA2"; 
			  break label;
		  }	
		}
		
		StringBuffer strbuflag = new StringBuffer();
		strbuflag.append("<XMLDOC>");
		strbuflag.append("<Flag>");
		strbuflag.append(NotExist);
		strbuflag.append("</Flag>");
		strbuflag.append("</XMLDOC>");

		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",strbuflag.toString()).toString();
		if(lStrResult != null){
		inputMap.put("ajaxKey", lStrResult);
		}
		objRes.setViewName("ajaxData");
		objRes.setResultValue(inputMap);
		
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception is " + e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
		}
	
	public ResultObject deactivatePranNo(Map inputMap)
	{
	  ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
	  String pranNo = null;
	  String fileName = null;
	  String remarks = null;

	  String flag= null;
		logger.info("inside service deactivatePranNo");
	  try
	  {
		 setSessionInfo(inputMap);
		 pranNo = StringUtility.getParameter("txtPranNo", request).trim();
		 fileName = StringUtility.getParameter("txtFileName",request).trim();
		 remarks = StringUtility.getParameter("txtRemark",request).trim();
		 
		 DeactivatePranNoDao lObjcheck = new DeactivatePranNoDaoImpl(null,serv.getSessionFactory());
			logger.info("FLAG:"+flag+" fileName"+fileName+" pranNo"+pranNo+" remarks"+remarks);

		 if(fileName!= null && fileName!= ""){
		 flag= lObjcheck.deactivatePranNo(fileName,pranNo, remarks);
		 }
		 else
		 {
			 logger.info("in if o f#################");
			 flag= lObjcheck.deactivatePran(pranNo, remarks);
		 }
 
			logger.info("FLAG:"+flag+" fileName"+fileName+" pranNo"+pranNo+" remarks"+remarks);
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