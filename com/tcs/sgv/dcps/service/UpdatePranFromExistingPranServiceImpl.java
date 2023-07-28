package com.tcs.sgv.dcps.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.UpdatePranFromExistingPranDao;
import com.tcs.sgv.dcps.dao.UpdatePranFromExistingPranDaoImpl;
import com.tcs.sgv.dcps.dao.updateOldPranNoDAOImpl;

public class UpdatePranFromExistingPranServiceImpl extends ServiceImpl {
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
			UpdatePranFromExistingPranDao objUpdateDao = new UpdatePranFromExistingPranDaoImpl(null, serv.getSessionFactory());
			
			if (StringUtility.getParameter("fromSearch", request).trim().equals("Yes")) {
				lStrSevaarthId = StringUtility.getParameter("txtSevaarthId", request).trim().toUpperCase();
				lStrPranNo = StringUtility.getParameter("txtPranNo", request).trim().toUpperCase();
				lLstEmpDeselect = objUpdateDao.getAllEmp(lStrSevaarthId,lStrPranNo );
			}
		if(lLstEmpDeselect != null && lLstEmpDeselect.size()>0){
				inputMap.put("DESELECTEMPLIST", lLstEmpDeselect);
		}
				objRes.setResultValue(inputMap);
				objRes.setViewName("updateExistingPranNo");				
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

			UpdatePranFromExistingPranDao objUpdateDao = new UpdatePranFromExistingPranDaoImpl(null, serv.getSessionFactory());		
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
				objRes.setViewName("updateExistingPranNo");				
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
		String dcpsEmployee= null;

		try {
		setSessionInfo(inputMap);
		UpdatePranFromExistingPranDao lObjcheck = new UpdatePranFromExistingPranDaoImpl(null,serv.getSessionFactory());
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
		dcpsEmployee = tuple[3].toString();
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
		  if (!dcpsEmployee.equals("Y"))
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
	
	public ResultObject updatingPranNo(Map inputMap)
	{
	  ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
	  String OldpranNo = null;
	  String fileName = null;
	  String remarks = null;
	  String empName = null;
	  String ddoCode = null;
	  String treasuryName = null;
	  String dcpsId= null;
	  String newPranNo = null;
	  String sevarthId= null;
	  String flag= null;
	  Long lLngPkIdForPran = null;

		logger.info("inside service deactivatePranNo");
	  try
	  {
		 setSessionInfo(inputMap);
		 OldpranNo = StringUtility.getParameter("txtOldPranNo", request).trim();
		 fileName = StringUtility.getParameter("txtFileName",request).trim();
		 remarks = StringUtility.getParameter("txtRemark",request).trim();	 
		 empName = StringUtility.getParameter("txtEmpName", request).trim();
		 ddoCode = StringUtility.getParameter("txtDdoCode",request).trim();
		 treasuryName = StringUtility.getParameter("txtTreasuryName",request).trim();	 
		 dcpsId = StringUtility.getParameter("txtDcpsId", request).trim();
		 newPranNo = StringUtility.getParameter("txtNewPranNo",request).trim();
		 sevarthId = StringUtility.getParameter("txtSevaarthId",request).trim();

		UpdatePranFromExistingPranDao lObjcheck = new UpdatePranFromExistingPranDaoImpl(null,serv.getSessionFactory());
		logger.info("FLAG:"+flag+" fileName"+fileName+" OldpranNo"+OldpranNo+" remarks"+remarks+" treasuryName"+treasuryName+" empName"+empName+" ddoCode"+ddoCode+" dcpsId"+dcpsId+" newPranNo"+newPranNo+" sevarthId"+sevarthId);
		lLngPkIdForPran = IFMSCommonServiceImpl.getNextSeqNum("MST_PRAN", inputMap);
		logger.info("lLngPkIdForPran++++++++++++++"+lLngPkIdForPran);
		
		 if(fileName!= null && fileName!= ""){
		 flag= lObjcheck.deactivatePranNo(fileName,OldpranNo, remarks,newPranNo);
		 lObjcheck.insertPranDetails(lLngPkIdForPran,empName,dcpsId,sevarthId,OldpranNo,newPranNo,remarks,ddoCode,treasuryName);
		 }
		 else
		 {
			 logger.info("in if o f#################");
			 flag= lObjcheck.deactivatePran(OldpranNo, remarks,newPranNo);
			 lObjcheck.insertPranDetails(lLngPkIdForPran,empName,dcpsId,sevarthId,OldpranNo,newPranNo,remarks,ddoCode,treasuryName);
		 }
			logger.info("FLAG:"+flag+" fileName"+fileName+" OldpranNo"+OldpranNo+" remarks"+remarks);
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
	public ResultObject testPranNO(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
        ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
        HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		Boolean lBlFlag = false;
		List pranExist=null;

		try {

			setSessionInfo(inputMap);
			UpdatePranFromExistingPranDao lObjupdatePr = new UpdatePranFromExistingPranDaoImpl(null,serv.getSessionFactory());    	

			String Pran_no = StringUtility.getParameter("pranno",request).trim();

			if (!"".equals(Pran_no)) 
			{
				
				pranExist = lObjupdatePr.testPranNO(Pran_no);
				if(pranExist.size()==0||pranExist==null){
					pranExist = lObjupdatePr.testPranNoForOld(Pran_no);
				}
			}
			String empName="NA";
			String dcpsId="NA";
			if(pranExist.size()!=0){
				Object[] tuple = (Object[]) pranExist.get(0);	
				lBlFlag=true;
				empName=tuple[0].toString();
				dcpsId=tuple[1].toString();
			}
			StringBuilder strbuflag = new StringBuilder();
			 String lSBStatus="";
		//	StringBuffer strbuflag = new StringBuffer();
			strbuflag.append("<XMLDOC>");
			strbuflag.append("<Flag>");
			strbuflag.append(lBlFlag);
			strbuflag.append("</Flag>");
			strbuflag.append("<EmpName>");
			strbuflag.append(empName);
			strbuflag.append("</EmpName>");
			strbuflag.append("<DcpsId>");
			strbuflag.append(dcpsId);
			strbuflag.append("</DcpsId>");
			strbuflag.append("</XMLDOC>");

			lSBStatus = strbuflag.toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus).toString();	
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setViewName("ajaxData");

			
//			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",strbuflag.toString()).toString();
//
//			inputMap.put("ajaxKey", lStrResult);
//			resObj.setViewName("ajaxData");
//			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			logger.error(" Error in testpranno " + e, e);
		}

		return resObj;

	}
	
}