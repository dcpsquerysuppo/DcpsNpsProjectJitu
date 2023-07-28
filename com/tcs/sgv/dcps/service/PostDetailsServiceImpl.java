package com.tcs.sgv.dcps.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.PostDetailsDAO;
import com.tcs.sgv.dcps.dao.PostDetailsDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstPostDiffDtls;

/**
 * Class Description -
 * 
 * 
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0 Oct 17, 2012
 */

public class PostDetailsServiceImpl extends ServiceImpl implements PostDetailsService
{
	private final Log gLogger = LogFactory.getLog(getClass());

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

	private void setSessionInfo(Map inputMap) {
		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is: " + e, e);
		}
	}
	
	
	public ResultObject loadPostDetailsData(Map<String, Object> inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "Fail");
		List lLstPostDetails = null;
		String lStrDdoCode = "";
		Long lLngPostDiffDtlsId = null;
		List lLstEnteredData = null;
		String lStrPrint = "";
		
		try{
			PostDetailsDAO lObjPostDetails = new PostDetailsDAOImpl(CmnLocationMst.class, serv.getSessionFactory());
			DcpsCommonDAO lObjCommonDao = new DcpsCommonDAOImpl(OrgDdoMst.class, serv.getSessionFactory());
			
			lStrDdoCode = lObjCommonDao.getDdoCode(gLngPostId);
			lLstPostDetails = lObjPostDetails.getPostDetails(lStrDdoCode);
			
			lLngPostDiffDtlsId = lObjPostDetails.getPkForPostDiffDtls(lStrDdoCode);
			
			if(lLngPostDiffDtlsId != null){
				lLstEnteredData = lObjPostDetails.getEnteredData(lStrDdoCode);
				
				Object []lObj = (Object[]) lLstEnteredData.get(0);
				inputMap.put("totalPost", lObj[0]);
				inputMap.put("permntPost", lObj[1]);
				inputMap.put("tempPost", lObj[2]);
				inputMap.put("tempPostExp", lObj[3]);
				inputMap.put("permntPostVacant", lObj[4]);
				inputMap.put("tempPostVacant", lObj[5]);
				
				inputMap.put("totalPostRsn", lObj[6]);
				inputMap.put("permntPostRsn", lObj[7]);
				inputMap.put("tempPostRsn", lObj[8]);
				inputMap.put("tempPostExpRsn", lObj[9]);
				inputMap.put("permntPostVacantRsn", lObj[10]);
				inputMap.put("tempPostVacantRsn", lObj[11]);
				inputMap.put("actvEmployee", lObj[12]);
				inputMap.put("empActiveRsn", lObj[13]);
				
				inputMap.put("save", "Y");
			}else{
				inputMap.put("save", "N");
			}
			
			lStrPrint = lObjPostDetails.chkForPrint(lStrDdoCode);
			
			inputMap.put("print", lStrPrint);
			inputMap.put("PostDetails", lLstPostDetails);
			resObj.setResultValue(inputMap);
			resObj.setViewName("PostDetails");
		}catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadPostDetailsData");
		}
		return resObj;
	}
	
	
	public ResultObject savePostDetailsData(Map<String, Object> inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "Fail");
		List lLstPostDetails = null;
		MstPostDiffDtls lObjMstPostDiffDtls = null;
		String lStrMode = "";
		Long lLngPostDiffDtlsId = null;
		
		try{
			PostDetailsDAO lObjPostDetails = new PostDetailsDAOImpl(MstPostDiffDtls.class, serv.getSessionFactory());
			String lStrDdoCode = StringUtility.getParameter("txtDdoCode", request);
			String lStrTotalPost = StringUtility.getParameter("txtTotalPostAST", request);
			String lStrPermntPost = StringUtility.getParameter("txtPermenantPostAST", request);
			String lStrTempPost = StringUtility.getParameter("txtTempPostAST", request);
			String lStrTempPostExp = StringUtility.getParameter("txtTempPostExpAST", request);
			String lStrPermenantPostVaccant = StringUtility.getParameter("txtPermenantPostVaccantAST", request);
			String lStrTempPostVaccant = StringUtility.getParameter("txtTempPostVaccantAST", request);
			String lStrEmpActvINSevaarth = StringUtility.getParameter("txtEmpActvInSevarthAST", request);
			
			//String lStrTotalPostRsn = StringUtility.getParameter("txtTotalPostRsn", request);
			String lStrPermntPostRsn = StringUtility.getParameter("txtPermenantPostRsn", request);
			String lStrTempPostRsn = StringUtility.getParameter("txtTempPostRsn", request);
			String lStrTempPostExpRsn = StringUtility.getParameter("txtTempPostExpRsn", request);
			String lStrPermntPostVacntRsn = StringUtility.getParameter("txtPermenantPostVaccantRsn", request);
			String lStrTempPostVacntRsn = StringUtility.getParameter("txtTempPostVaccantRsn", request);
			String lStrActvEmployeeRsn = StringUtility.getParameter("txtEmpActvInSevarthRsn", request);
			
			
			
			
			lLngPostDiffDtlsId = lObjPostDetails.getPkForPostDiffDtls(lStrDdoCode);
			
			if(lLngPostDiffDtlsId == null){
				lObjMstPostDiffDtls = new MstPostDiffDtls();
				lStrMode = "Insert";
				
				lLngPostDiffDtlsId = IFMSCommonServiceImpl.getNextSeqNum("MST_POST_DIFF_DTLS", inputMap);
				
				lObjMstPostDiffDtls.setMstPostDiffDtlsId(lLngPostDiffDtlsId);
					
				if (lStrDdoCode!= null && !lStrDdoCode.equals("")) {
					lObjMstPostDiffDtls.setDdoCode(lStrDdoCode);
				}
			}else{
				lObjMstPostDiffDtls = (MstPostDiffDtls) lObjPostDetails.read(lLngPostDiffDtlsId);
				lStrMode = "Update";
			}
			
			
			if (lStrTotalPost!= null && !lStrTotalPost.equals("")) {
				lObjMstPostDiffDtls.setTotalPost(Long.parseLong(lStrTotalPost));
			}			
			if (lStrPermntPost!= null && !lStrPermntPost.equals("")) {
				lObjMstPostDiffDtls.setPermntPostEmpAtch(Long.parseLong(lStrPermntPost));
			}
			if (lStrTempPost!= null && !lStrTempPost.equals("")) {
				lObjMstPostDiffDtls.setTempPostEmpAtch(Long.parseLong(lStrTempPost));
			}
			if (lStrTempPostExp!= null && !lStrTempPostExp.equals("")) {
				lObjMstPostDiffDtls.setTempPostExpired(Long.parseLong(lStrTempPostExp));
			}
			if (lStrPermenantPostVaccant!= null && !lStrPermenantPostVaccant.equals("")) {
				lObjMstPostDiffDtls.setPermntVacant(Long.parseLong(lStrPermenantPostVaccant));
			}
			if (lStrTempPostVaccant!= null && !lStrTempPostVaccant.equals("")) {
				lObjMstPostDiffDtls.setTempVacant(Long.parseLong(lStrTempPostVaccant));
			}
			
			if (lStrEmpActvINSevaarth!= null && !lStrEmpActvINSevaarth.equals("")) {
				lObjMstPostDiffDtls.setActiveEmp(Long.parseLong(lStrEmpActvINSevaarth));
			}
			
			/*if (lStrTotalPostRsn!= null && !lStrTotalPostRsn.equals("")) {
				lObjMstPostDiffDtls.setRsnTotalPost(lStrTotalPostRsn);
			}else{
				lObjMstPostDiffDtls.setRsnTotalPost("");
			}*/
			if (lStrPermntPostRsn!= null && !lStrPermntPostRsn.equals("")) {
				lObjMstPostDiffDtls.setRsnPermntPostEmpAtch(lStrPermntPostRsn);
			}else{
				lObjMstPostDiffDtls.setRsnPermntPostEmpAtch("");
			}
			if (lStrTempPostRsn!= null && !lStrTempPostRsn.equals("")) {
				lObjMstPostDiffDtls.setRsnTempPostEmpAtch(lStrTempPostRsn);
			}else{
				lObjMstPostDiffDtls.setRsnTempPostEmpAtch("");
			}
			if (lStrTempPostExpRsn!= null && !lStrTempPostExpRsn.equals("")) {
				lObjMstPostDiffDtls.setRsnTempPostExpired(lStrTempPostExpRsn);
			}else{
				lObjMstPostDiffDtls.setRsnTempPostExpired("");
			}
			if (lStrPermntPostVacntRsn!= null && !lStrPermntPostVacntRsn.equals("")) {
				lObjMstPostDiffDtls.setRsnPermntVacant(lStrPermntPostVacntRsn);
			}else{
				lObjMstPostDiffDtls.setRsnPermntVacant("");
			}
			if (lStrTempPostVacntRsn!= null && !lStrTempPostVacntRsn.equals("")) {
				lObjMstPostDiffDtls.setRsnTempVacant(lStrTempPostVacntRsn);
			}else{
				lObjMstPostDiffDtls.setRsnTempVacant("");
			}
			if (lStrActvEmployeeRsn!= null && !lStrActvEmployeeRsn.equals("")) {
				lObjMstPostDiffDtls.setRsnActEmployee(lStrActvEmployeeRsn);
			}else{
				lObjMstPostDiffDtls.setRsnActEmployee("");
			}
			
			
			lObjMstPostDiffDtls.setPrint("0");
			
			if(lStrMode.equals("Insert")){
				lObjMstPostDiffDtls.setCreatedPostId(gLngPostId);
				lObjMstPostDiffDtls.setCreatedUserId(gLngUserId);
				lObjMstPostDiffDtls.setCreatedDate(DBUtility.getCurrentDateFromDB());
				lObjPostDetails.create(lObjMstPostDiffDtls);
			}else if(lStrMode.equals("Update")){
				lObjMstPostDiffDtls.setUpdatedPostId(gLngPostId);
				lObjMstPostDiffDtls.setUpdatedUserId(gLngUserId);
				lObjMstPostDiffDtls.setUpdatedDate(DBUtility.getCurrentDateFromDB());
				lObjPostDetails.update(lObjMstPostDiffDtls);
			}
			
			String lSBStatus = getResponseXMLDoc("true").toString();

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");			
			
		}catch (Exception e) {
				IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadPostDetailsData");
		}
		
		return resObj;
	}
	
	public ResultObject freezeDataPostDetails(Map<String, Object> inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "Fail");
		
		try{
			PostDetailsDAO lObjPostDetails = new PostDetailsDAOImpl(MstPostDiffDtls.class, serv.getSessionFactory());
			String lStrDdoCode = StringUtility.getParameter("txtDdoCode", request);
			
			lObjPostDetails.updatePrintFlag(lStrDdoCode);
			
			String lSBStatus = getResponseXMLDoc("true").toString();

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			
		}catch (Exception e) {
				IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadPostDetailsData");
		}
		
		return resObj;
	}
	
	private StringBuilder getResponseXMLDoc(String lStrResData) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");		
		lStrBldXML.append("<STATUS>");
		lStrBldXML.append(lStrResData);
		lStrBldXML.append("</STATUS>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
}
