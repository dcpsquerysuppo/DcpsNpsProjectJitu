package com.tcs.sgv.lna.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.lna.dao.LNAApprovedRequestDAO;
import com.tcs.sgv.lna.dao.LNAApprovedRequestDAOImpl;

public class LNAApprovedRequestServiceImpl extends ServiceImpl implements LNAApprovedRequestService {
	Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrUserId = null; /* STRING USER ID */
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

	String gStrLocationCode = null;

	private void setSessionInfo(Map inputMap) {

		request = (HttpServletRequest) inputMap.get("requestObj");
		session = request.getSession();
		serv = (ServiceLocator) inputMap.get("serviceLocator");
		gLngPostId = SessionHelper.getPostId(inputMap);
		gStrPostId = gLngPostId.toString();
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrUserId = gLngUserId.toString();
		gDtCurDate = SessionHelper.getCurDate();
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);

	}

	public ResultObject lnaApprovedList(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		try {
			setSessionInfo(inputMap);
			List lLnaApprovedList = new ArrayList();
			LNAApprovedRequestDAO lObjLNAApprovedRequestDAO = new LNAApprovedRequestDAOImpl(null, serv.getSessionFactory());
			
			String lStrUser = StringUtility.getParameter("userType", request);
			
			lLnaApprovedList = lObjLNAApprovedRequestDAO.getLNAApprovedRequestList(gStrLocationCode,gStrPostId,lStrUser);
			inputMap.put("lnaApprovedList", lLnaApprovedList);
			if (lLnaApprovedList != null && !lLnaApprovedList.isEmpty()) {
				inputMap.put("totalRecords", lLnaApprovedList.size());
			}
			resObj.setResultValue(inputMap);
			resObj.setViewName("LNAApprovedRequest");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		return resObj;
	}
}
