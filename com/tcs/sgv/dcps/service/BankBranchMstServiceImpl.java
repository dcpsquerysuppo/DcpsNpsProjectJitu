package com.tcs.sgv.dcps.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.BankBranchMstDaoImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import java.util.List;

public class BankBranchMstServiceImpl extends ServiceImpl {
	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrUserId = null; /* STRING USER ID */

	private String gStrLocale = null; /* STRING LOCALE */

	private Locale gLclLocale = null; /* LOCALE */

	private Long gLngLangId = null; /* LANG ID */

	private Long gLngDBId = null; /* DB ID */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	/* Global Variable for User Loc Map */
	static HashMap sMapUserLoc = new HashMap();

	/* Global Variable for User Location */
	String gStrUserLocation = null;

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/dcps/DCPSConstants");

	private void setSessionInfo(Map inputMap) {
		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			session = request.getSession();
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLclLocale = new Locale(SessionHelper.getLocale(request));
			gStrLocale = SessionHelper.getLocale(request);
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gDtCurrDt = SessionHelper.getCurDate();
		} catch (Exception e) {

		}

	}

	public ResultObject viewBankBranchMst(Map<String, Object> inputMap) throws Exception{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try{
			setSessionInfo(inputMap);
			List bnkDtls = null;
			List rbiBankNames = null;
			long size = 0l;
			BankBranchMstDaoImpl bnkBrnchDaoImpl = new BankBranchMstDaoImpl(null, serv.getSessionFactory());
			bnkDtls = bnkBrnchDaoImpl.getExistingBanks();
			rbiBankNames = bnkBrnchDaoImpl.getRBIBankNames();
			inputMap.put("bnkNames",bnkDtls);
			inputMap.put("rbiBankNames",rbiBankNames);
			
			gLogger.info("size outside.. "+size);
			String extBankcode = "";
			String rbiBankcode = "";
			List extBankBrnch = null;
			List rbiBankBrnch = null;
			List lLstCity = null;
			if(request != null){
				if(StringUtility.getParameter("extBankCode", request) != null && StringUtility.getParameter("rbiBnkCode", request) != null){
					extBankcode = StringUtility.getParameter("extBankCode", request).trim(); 					
					extBankBrnch = bnkBrnchDaoImpl.getExtBankBranch(Long.parseLong(extBankcode));
					inputMap.put("extBankBrnch",extBankBrnch);
					size = extBankBrnch.size();
					gLogger.info("size inside.. "+size);
					
					rbiBankcode = StringUtility.getParameter("rbiBnkCode", request).trim();
					rbiBankBrnch = bnkBrnchDaoImpl.getRBIBankBranch(Long.parseLong(rbiBankcode));
					inputMap.put("rbiBankBrnch",rbiBankBrnch);
					
					lLstCity = bnkBrnchDaoImpl.getCityList();
					inputMap.put("extBankcode",extBankcode);
					inputMap.put("rbiBankcode",rbiBankcode);
					inputMap.put("lLstCity",lLstCity);
					
				}
				
				
			}
			inputMap.put("size", size);
		}
		catch(Exception e) {

		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("viewBankBranchMst");
		return resObj;
	}
	
	public ResultObject getBranchesFrmCity(Map inputMap) throws Exception{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try{
			String rbiBankCode = null;
			String cityid = null;
			setSessionInfo(inputMap);
			List lLstBranch = null;
			BankBranchMstDaoImpl bnkBrnchDaoImpl = new BankBranchMstDaoImpl(null, serv.getSessionFactory());
			if(request != null){
				if(StringUtility.getParameter("rbiBnkCode", request) != null && StringUtility.getParameter("cityName", request) != null){
					rbiBankCode = StringUtility.getParameter("rbiBnkCode", request);
					cityid = StringUtility.getParameter("cityName", request);
					gLogger.info("cityid "+cityid);
					gLogger.info("rbiBankCode "+rbiBankCode);
					lLstBranch = bnkBrnchDaoImpl.getBrachesFrmCity(Long.parseLong(rbiBankCode),Long.parseLong(cityid));
					
					String lStrTempResult = "";
					if (lLstBranch != null) {
						lStrTempResult = (new AjaxXmlBuilder().addItems(lLstBranch,"desc", "id")).toString();
					}
					gLogger.info("lStrTempResult "+lStrTempResult);
					inputMap.put("ajaxKey", lStrTempResult);
					resObj.setResultValue(inputMap);
					resObj.setResultCode(ErrorConstants.SUCCESS);
					resObj.setViewName("ajaxData");
				}
			}
		}
		catch(Exception e){
			
		}
		return resObj;
	}
	
	public ResultObject getIFSCCodeFrmBrnchCode(Map inputMap) throws Exception{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try{
			String brchCode = null;
			String ifscCode = null;
			List brnchDtls = null;
			setSessionInfo(inputMap);
			
			BankBranchMstDaoImpl bnkBrnchDaoImpl = new BankBranchMstDaoImpl(null, serv.getSessionFactory());
			if(request != null){
				if(StringUtility.getParameter("brchCode", request) != null ){
					brchCode = StringUtility.getParameter("brchCode", request);
										
					gLogger.info("rbiBankCode "+brchCode);
					brnchDtls = bnkBrnchDaoImpl.getIFSCFrmBranchCode(Long.parseLong(brchCode));					
					String lStrTempResult = "";
					
					if (brnchDtls != null) {
						Object obj[] = (Object[]) brnchDtls.get(0);
						StringBuilder lStrBldXML = new StringBuilder();
						lStrBldXML.append("<XMLDOC>");
						lStrBldXML.append("<ifscCode>");
						//lStrBldXML.append("<postName>");
						lStrBldXML.append("<![CDATA[");
						lStrBldXML.append(obj[0]);
						lStrBldXML.append("]]>");						
						lStrBldXML.append("</ifscCode>");
						lStrBldXML.append("<brncCity>");
						//lStrBldXML.append("<postName>");
						lStrBldXML.append("<![CDATA[");
						lStrBldXML.append(obj[1]);
						lStrBldXML.append("]]>");						
						lStrBldXML.append("</brncCity>");
						lStrBldXML.append("<micrCode>");
						//lStrBldXML.append("<postName>");
						lStrBldXML.append("<![CDATA[");
						lStrBldXML.append(obj[2]);
						lStrBldXML.append("]]>");						
						lStrBldXML.append("</micrCode>");
						lStrBldXML.append("</XMLDOC>");
						//String lStrTempResult = null;				
						gLogger.info("ifscCode "+ifscCode);
						lStrTempResult = new AjaxXmlBuilder().addItem("ajax_Key",lStrBldXML.toString()).toString();
						inputMap.put("ajaxKey", lStrTempResult);
						gLogger.info("ifscCode .."+lStrTempResult);
					}
					else{
						StringBuilder lStrBldXML = new StringBuilder();
						lStrBldXML.append("<XMLDOC>");
						lStrBldXML.append("<ifscCode>");
						//lStrBldXML.append("<postName>");
						lStrBldXML.append("<![CDATA[");
						lStrBldXML.append(" ");
						lStrBldXML.append("]]>");						
						lStrBldXML.append("</ifscCode>");
						lStrBldXML.append("</XMLDOC>");
						lStrBldXML.append("<brncCity>");
						//lStrBldXML.append("<postName>");
						lStrBldXML.append("<![CDATA[");
						lStrBldXML.append(" ");
						lStrBldXML.append("]]>");						
						lStrBldXML.append("</brncCity>");
						lStrBldXML.append("</XMLDOC>");
						lStrBldXML.append("<micrCode>");
						//lStrBldXML.append("<postName>");
						lStrBldXML.append("<![CDATA[");
						lStrBldXML.append(" ");
						lStrBldXML.append("]]>");						
						lStrBldXML.append("</micrCode>");
						lStrBldXML.append("</XMLDOC>");
						//String lStrTempResult = null;				
						gLogger.info("ifscCode "+ifscCode);
						lStrTempResult = new AjaxXmlBuilder().addItem("ajax_Key",lStrBldXML.toString()).toString();
						inputMap.put("ajaxKey", lStrTempResult);
						gLogger.info("ifscCode.."+lStrTempResult);
					}					
				}
			}			
			
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		}
		catch(Exception e){
			
		}
		return resObj;
	}
	
	
	public ResultObject bankBranchMapping(Map inputMap) throws Exception{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try{
			gLogger.info("in mappingg ");
			String extngBranch = null;
			String rbiBranch = null;
			String city = null;
			String ifscCode = null;
			String micrCode = null;
			String exctngBranches[] = null;
			String rbiBranches[] = null;
			String cities[] = null;
			String ifscCodelst[] = null;
			String lLstMicrCode[] = null; 
			setSessionInfo(inputMap);
			
			BankBranchMstDaoImpl bnkBrnchDaoImpl = new BankBranchMstDaoImpl(null, serv.getSessionFactory());
			if(request != null){
				if((StringUtility.getParameter("extngBranch", request) != null) && 
						(StringUtility.getParameter("rbiBranch", request) != null) && 
						(StringUtility.getParameter("city", request) != null) &&
						(StringUtility.getParameter("ifscCode", request) != null) &&  
						(StringUtility.getParameter("micrCode", request) != null))
				{
					extngBranch = StringUtility.getParameter("extngBranch", request);
					rbiBranch = StringUtility.getParameter("rbiBranch", request);
					city = StringUtility.getParameter("city", request);
					ifscCode = StringUtility.getParameter("ifscCode", request);
					micrCode = StringUtility.getParameter("micrCode", request);
					
					exctngBranches = extngBranch.split("~");
					rbiBranches = rbiBranch.split("~");
					cities = city.split("~");
					ifscCodelst = ifscCode.split("~");
					lLstMicrCode = micrCode.split("~");
					
					gLogger.info("in for loopp "+exctngBranches[0]);
					 int rowUpdated = 0; 
					 gLogger.info("exctngBranches.length "+exctngBranches.length);
					for(int i =0; i<exctngBranches.length;i++){
						gLogger.info("in for loopp ");
						gLogger.info("Long.parseLong(exctngBranches[i]) "+Long.parseLong(exctngBranches[i]));
						gLogger.info("Long.parseLong(rbiBranches[i] "+Long.parseLong(rbiBranches[i]));
						gLogger.info("ifscCodelst[i] "+ifscCodelst[i]);
						rowUpdated = rowUpdated +	bnkBrnchDaoImpl.mapBankBranches(Long.parseLong(exctngBranches[i].trim()),Long.parseLong(rbiBranches[i].trim()),
								ifscCodelst[i].trim(),cities[i].trim().toUpperCase(),lLstMicrCode[i].trim());
					}
					
					gLogger.info("rbiBankCode "+rowUpdated);
									
					String lStrTempResult = "";					
						StringBuilder lStrBldXML = new StringBuilder();
						lStrBldXML.append("<XMLDOC>");
						lStrBldXML.append("<ifscCode>");
						//lStrBldXML.append("<postName>");
						lStrBldXML.append("<![CDATA[");
						lStrBldXML.append(rowUpdated);
						lStrBldXML.append("]]>");						
						lStrBldXML.append("</ifscCode>");
						lStrBldXML.append("</XMLDOC>");
						//String lStrTempResult = null;				
						gLogger.info("ifscCode "+rowUpdated);
						lStrTempResult = new AjaxXmlBuilder().addItem("ajax_Key",lStrBldXML.toString()).toString();
						inputMap.put("ajaxKey", lStrTempResult);
						gLogger.info("ifscCode .."+lStrTempResult);					
									
				}
			}			
			gLogger.info("in out if loooop ");
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		}
		catch(Exception e){
			
		}
		return resObj;
	}
	
}

