package com.tcs.sgv.lna.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.lna.dao.LNARequestProcessDAO;
import com.tcs.sgv.lna.dao.LNARequestProcessDAOImpl;
import com.tcs.sgv.lna.valueobject.MstLnaHodDtls;

public class LNAHodSignatureConfigServiceImpl extends ServiceImpl implements LNAHodSignatureConfigService {
	Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private Date gDtCurDate = null; /* CURRENT DATE */
	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;
	
	/* Global Variable for LangId */
	Long gLngLangId = null;
	
	Long gLngLocationCode = null;
	
	Integer lIntSubtypeSelection = 0;
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/LNAConstants");

	private void setSessionInfo(Map inputMap) {

		request = (HttpServletRequest) inputMap.get("requestObj");
		serv = (ServiceLocator) inputMap.get("serviceLocator");
		gLngPostId = SessionHelper.getPostId(inputMap);
		gStrPostId = gLngPostId.toString();
		gLngUserId = SessionHelper.getUserId(inputMap);
		gDtCurDate = SessionHelper.getCurDate();
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gLngLocationCode = Long.parseLong(gStrLocationCode);
		gLngLangId = SessionHelper.getLangId(inputMap);

	}
		
	public ResultObject saveHODSignature(Map<String, Object> inputMap) throws Exception {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		Boolean lBlFlag = false;
		
		try {
			setSessionInfo(inputMap);
			
			String lStrHodName = StringUtility.getParameter("hodName", request).trim();
			String lStrHodDsgn = StringUtility.getParameter("hodDsgn", request).trim();
			
			LNARequestProcessDAO lObjProcessDAO = new LNARequestProcessDAOImpl(MstLnaHodDtls.class, serv.getSessionFactory());
			
			MstLnaHodDtls lObjHodDtls = new MstLnaHodDtls();
			List<MstLnaHodDtls> lLstHodDtls = lObjProcessDAO.getListByColumnAndValue("locationCode", gStrLocationCode);

			if(lLstHodDtls != null && !lLstHodDtls.isEmpty() ){
				lObjHodDtls = lLstHodDtls.get(0);				
				lObjHodDtls.setHodName(lStrHodName);
				lObjHodDtls.setHodDsgn(lStrHodDsgn);
				lObjHodDtls.setUpdatedUserId(gLngUserId);
				lObjHodDtls.setUpdatedPostId(gLngPostId);
				lObjHodDtls.setUpdatedDate(gDtCurrDt);
				lObjProcessDAO.update(lObjHodDtls);
			}else{
				Long lLngHodDtlsId = IFMSCommonServiceImpl.getNextSeqNum("MST_LNA_HOD_DTLS", inputMap);
				lObjHodDtls.setLnaHodDtlsId(lLngHodDtlsId);
				lObjHodDtls.setLocationCode(gStrLocationCode);
				lObjHodDtls.setHodName(lStrHodName);
				lObjHodDtls.setHodDsgn(lStrHodDsgn);
				lObjHodDtls.setCreatedUserId(gLngUserId);
				lObjHodDtls.setCreatedPostId(gLngPostId);
				lObjHodDtls.setCreatedDate(gDtCurrDt);
				lObjProcessDAO.create(lObjHodDtls);
			}
			
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getSaveResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	private StringBuilder getSaveResponseXMLDoc(Boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");		
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
}
