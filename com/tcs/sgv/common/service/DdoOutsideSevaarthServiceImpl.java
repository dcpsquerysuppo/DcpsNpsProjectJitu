package com.tcs.sgv.common.service;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.tcs.sgv.common.dao.ChangeUserNameDAOImpl;
import com.tcs.sgv.common.dao.DdoOutsideSevaarthDAOImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.ess.valueobject.OrgUserMst;

public class DdoOutsideSevaarthServiceImpl extends ServiceImpl
{
private final Log gLogger = LogFactory.getLog(getClass());
	
	public ResultObject getDataFromDdoCode(Map<String, Object> inputMap) 
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		List lLstDdoLst = null;
		String lStrUserName = "";
		String lStrDdoName = "";
		String lStrDdoPrsnlName = "";
		Long lLngPostId = null;
		
		try{
			ChangeUserNameDAOImpl lObjChngUserName = new ChangeUserNameDAOImpl(OrgUserMst.class,serv.getSessionFactory());
			String lStrDdoCode = StringUtility.getParameter("ddoCode", request);
			
			if(lStrDdoCode.length() > 0){
				lLstDdoLst = lObjChngUserName.getDataFromDdoCode(lStrDdoCode);
			}
			if(!lLstDdoLst.isEmpty() && lLstDdoLst.size() > 0){
				lStrUserName = lObjChngUserName.getUnameFromDdoCode(lStrDdoCode);
				Object []lObj = (Object[]) lLstDdoLst.get(0);
				lStrDdoName = lObj[0].toString();
				lStrDdoPrsnlName = lObj[1].toString();
				lLngPostId = Long.parseLong(lObj[2].toString());
			}else{
				lStrDdoName = "Invalid";
			}
			
			inputMap.put("ddoName", lStrDdoName);
			inputMap.put("ddoPName", lStrDdoPrsnlName);
			inputMap.put("uName", lStrUserName);
			inputMap.put("ddoCode", lStrDdoCode);
			inputMap.put("postId", lLngPostId);
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("ddoOutsideSevaarth");
		}
		catch(Exception e){
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in getDataFromDdoCode");
		}
		return resObj;
	}

	public ResultObject updateForDdoOutsideSevaarth(Map<String, Object> inputMap)
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");				
		
		try{
			DdoOutsideSevaarthDAOImpl lObjDdoOutSideSevaarth = new DdoOutsideSevaarthDAOImpl(CmnLocationMst.class,serv.getSessionFactory());
			ChangeUserNameDAOImpl lObjChangeUsrName = new ChangeUserNameDAOImpl(OrgUserMst.class,serv.getSessionFactory());
			String lStrDdoCode = StringUtility.getParameter("ddoCode", request);
			String lStrEmpName = StringUtility.getParameter("empName", request);
			String lStrNewUname = StringUtility.getParameter("newUname", request);
			String lStrOldUname = StringUtility.getParameter("oldUname", request);
			String lStrDob = StringUtility.getParameter("DOB", request);
			String lStrDoj = StringUtility.getParameter("DOJ", request);
			String lStrServcExpr = StringUtility.getParameter("serviceExpr", request);
			
			String lStrDdoData = lObjChangeUsrName.getUserIdPostIDFromDdoCode(lStrDdoCode);
			String []lStrData = lStrDdoData.split("#");			
			Long lLongUserId = Long.parseLong(lStrData[1].toString());
			
			lObjDdoOutSideSevaarth.updateDdoDetails(lLongUserId, lStrEmpName, lStrDob, lStrDoj, lStrServcExpr);
			lObjDdoOutSideSevaarth.updateUserName(lStrNewUname, lStrOldUname);
			
			String lSBStatus = getResponseXMLDoc("Success").toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		}catch(Exception e){
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in validateDdoCode");
		}
		return resObj;
	}
	
	private StringBuilder getResponseXMLDoc(String lStrData) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<DDOCODE>");
		lStrBldXML.append(lStrData);
		lStrBldXML.append("</DDOCODE>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
}
