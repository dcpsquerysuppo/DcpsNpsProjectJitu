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
import com.tcs.sgv.dcps.dao.UpdateDepDdoCodeDaoImpl;
import com.tcs.sgv.dcps.dao.UpdateDepDdoCodeDao;

public class UpdateDepDdoCodeServiceImpl extends ServiceImpl {
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
		//	String lStrPranNo = null;
		List lLstEmpDeselect = null;

		try {
			setSessionInfo(inputMap);
			UpdateDepDdoCodeDao objUpdateDao = new UpdateDepDdoCodeDaoImpl(null, serv.getSessionFactory());

			if (StringUtility.getParameter("fromSearch", request).trim().equals("Yes")) {
				lStrSevaarthId = StringUtility.getParameter("txtSevaarthId", request).trim().toUpperCase();
				lLstEmpDeselect = objUpdateDao.getAllEmp(lStrSevaarthId);
			}
			if(lLstEmpDeselect != null && lLstEmpDeselect.size()>0){
				inputMap.put("DESELECTEMPLIST", lLstEmpDeselect);
			}
			objRes.setResultValue(inputMap);
			objRes.setViewName("UpdateDepDdoCode");				
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
		String depDdoCode= null;
		String pranActive= null;
		String lDdoCode = null;
		String lDcpsOrGpf = null;
		String NotExist="";

		try {
			setSessionInfo(inputMap);
			UpdateDepDdoCodeDao lObjcheck = new UpdateDepDdoCodeDaoImpl(null,serv.getSessionFactory());
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
					lDdoCode = tuple[1].toString();
					lDcpsOrGpf = tuple[2].toString();
					if (!lDdoCode.equals("0"))
					{
						NotExist="NA4"; 
						break label;
					}
					if (!lDcpsOrGpf.equals("Y"))
					{
						NotExist="NA5"; 
						break label;
					}
					else 
					{
						List existList1 = lObjcheck.checkFormUpdate(lStrSevaarthId);

						if(existList1== null || existList1.size()==0)
						{
							NotExist="NA1";
							break label;
						}
						if(existList1!= null && existList1.size()>0)
						{			
							Object[] tuple1 = (Object[]) existList1.get(0);
							depDdoCode= tuple1[0].toString();
							if (!depDdoCode.equals("0"))
							{
								NotExist=depDdoCode; 
								break label;
							}

						}
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

	public ResultObject checkDepDdoCode(Map inputMap)throws Exception {
		logger.info("Inside checkDepDdoCode ");

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String depDdoCode= null;
		String NotExist="";
		try {
			setSessionInfo(inputMap);
			UpdateDepDdoCodeDao lObjcheck = new UpdateDepDdoCodeDaoImpl(null,serv.getSessionFactory());
			depDdoCode = StringUtility.getParameter("txtDepDdoCode", request).trim().toUpperCase();
			logger.info(" depDdoCode"+depDdoCode);
			List existList = lObjcheck.checkDepDdoCode(depDdoCode);

			label:
				if(existList== null || existList.size()==0)
				{
					NotExist="NA";
					break label;
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

	public ResultObject updateDdoCode(Map inputMap)
	{
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String sevarthId = null;
		String depDdoCode = null;

		String flag= null;
		logger.info("inside service updateDdoCode");
		try
		{
			setSessionInfo(inputMap);
			sevarthId = StringUtility.getParameter("txtSevId", request).trim();
			depDdoCode = StringUtility.getParameter("txtDdoCode",request).trim();

			UpdateDepDdoCodeDao lObjcheck = new UpdateDepDdoCodeDaoImpl(null,serv.getSessionFactory());
			logger.info("sevarthId"+sevarthId+" depDdoCode"+depDdoCode);

			if(sevarthId!= null && depDdoCode!= ""){
				flag= lObjcheck.updateDdoCode(sevarthId,depDdoCode);
			}

			logger.info("flag*************"+flag);
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