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
import com.tcs.sgv.dcps.dao.InterchangePranDao;
import com.tcs.sgv.dcps.dao.InterchangePranDaoImpl;

public class InterchangePranServiceImpl extends ServiceImpl {
	/* Global Variable for Logger Class */
	private final Log logger = LogFactory.getLog(getClass());

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	String lStrSevaarthId1 = null;
	String lStrSevaarthId2 = null;
	
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


		String lStrPranNo = null;
		List lLstEmpDeselect = null;
		
		try {
			setSessionInfo(inputMap);
			InterchangePranDao objUpdateDao = new InterchangePranDaoImpl(null, serv.getSessionFactory());
			
			if (StringUtility.getParameter("fromSearch", request).trim().equals("Yes")) {
				lStrSevaarthId1 = StringUtility.getParameter("txtSevaarthId1", request).trim().toUpperCase();
				lStrSevaarthId2 = StringUtility.getParameter("txtSevaarthId2", request).trim().toUpperCase();
				lLstEmpDeselect = objUpdateDao.getAllEmp(lStrSevaarthId1,lStrSevaarthId2 );
			}
		if(lLstEmpDeselect != null && lLstEmpDeselect.size()>0){
				inputMap.put("DESELECTEMPLIST", lLstEmpDeselect);
		}
				objRes.setResultValue(inputMap);
				objRes.setViewName("InterchangePran");				
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

		String lStrSevId1 = null;
		String lStrSevId2 = null;
		String lStrPranNo = null;

		List lLstEmpDeselect = null;
		List lLstFileSelect = null;
		try {
			setSessionInfo(inputMap);

			InterchangePranDao objUpdateDao = new InterchangePranDaoImpl(null, serv.getSessionFactory());		
			if (StringUtility.getParameter("fromSearch", request).trim().equals("Yes")) {
				lStrSevId1 = StringUtility.getParameter("txtSevaarthId1", request).trim().toUpperCase();
				lStrSevId2 = StringUtility.getParameter("txtSevaarthId2", request).trim().toUpperCase();
				lStrPranNo = StringUtility.getParameter("txtPranNo", request).trim().toUpperCase();
				logger.info("lStrSevId1"+lStrSevId1);
				logger.info("lStrSevId2"+lStrSevId2);
				logger.info("lStrPranNo"+lStrPranNo);

				lLstFileSelect = objUpdateDao.getAllFile(lStrPranNo);
				lLstEmpDeselect = objUpdateDao.getAllEmp(lStrSevId1,lStrSevId2 );
				}

				logger.info("####### in lLstEmpDeselect1");
				if(lLstEmpDeselect != null){
				inputMap.put("DESELECTEMPLIST", lLstEmpDeselect);
				}
				if(lLstFileSelect != null){
				inputMap.put("SELECTFILES", lLstFileSelect);
				}
				objRes.setResultValue(inputMap);
				objRes.setViewName("InterchangePran");				
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
		String pranActive= null;
		String PranNo = null;
		String NotExist="";
		
		try {
		setSessionInfo(inputMap);
		InterchangePranDao lObjcheck = new InterchangePranDaoImpl(null,serv.getSessionFactory());
		lStrSevaarthId = StringUtility.getParameter("txtSevaarthId", request).trim().toUpperCase();
		logger.info("in checkSevaarthId"+" lStrSevaarthId"+lStrSevaarthId);

		List existList = lObjcheck.checkSevaarthIdExist(lStrSevaarthId);

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
		pranActive = tuple[1].toString();
		PranNo = tuple[2].toString();
		  if (pranActive.equals("0"))
		  {
			  NotExist="NA2"; 
			  break label;
		  }	
		  if (PranNo.equals("1"))
		  {
			  NotExist="NA3"; 
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
	
	public ResultObject swapPranNo(Map inputMap)
	{
	  ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
	  String PranNew1 = null;
	  String PranNew2 = null;
	  String fileName = null;
	  String remarks = null;

	  String flag= null;
		logger.info("inside service swapPranNo");
	  try
	  {
		 setSessionInfo(inputMap);
		 PranNew1 = StringUtility.getParameter("txtPranNew1", request).trim();
		 PranNew2 = StringUtility.getParameter("txtPranNew2", request).trim();
		 fileName = StringUtility.getParameter("txtFileName",request).trim();
		 remarks = StringUtility.getParameter("txtRemark",request).trim();
		 
		 InterchangePranDao lObjcheck = new InterchangePranDaoImpl(null,serv.getSessionFactory());
			logger.info("FLAG:"+flag+" fileName"+fileName+" PranNew1"+PranNew1+" PranNew2"+PranNew2+" remarks"+remarks);

		 if(fileName!= null && fileName!= ""){
		 flag= lObjcheck.swapPranNo(fileName,PranNew1,PranNew2, remarks);
		 }
		 else
		 {
			 logger.info("in if o f#################");
			 flag= lObjcheck.swapPran(PranNew1,PranNew2, remarks);
		 }
 
			logger.info("FLAG:"+flag+" fileName"+fileName+" PranNew1"+PranNew1+" remarks"+remarks);
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