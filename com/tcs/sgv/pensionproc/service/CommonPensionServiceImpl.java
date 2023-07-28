package com.tcs.sgv.pensionproc.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.log4j.Logger;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionproc.dao.CommonPensionDAO;
import com.tcs.sgv.pensionproc.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAOImpl;

public class CommonPensionServiceImpl extends ServiceImpl implements CommonPensionService {
	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for DB Id */
	Long gLngDBId = null;

	/* Global Variable for Logger Class */
	private final static Logger gLogger = Logger.getLogger(CommonPensionServiceImpl.class);

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	private void setSessionInfo(Map inputMap) {
		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gDtCurrDt = DBUtility.getCurrentDateFromDB();

	}

	public ResultObject loadDistricts(Map inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrStateId = null;
		String lStrAjaxResult = null;
		List<ComboValuesVO> lLstDistrict = new ArrayList<ComboValuesVO>();
		Long lLngStateId = null;
		try {
			setSessionInfo(inputMap);
			lStrStateId = StringUtility.getParameter("state", request).toString();
			lLngStateId = Long.valueOf(lStrStateId);

			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());

			if (lStrStateId != null && lStrStateId.length() > 0) {
				lLstDistrict = lObjCommonPensionDAO.getDistrictsOfState(lLngStateId, gLngLangId);
				lStrAjaxResult = new AjaxXmlBuilder().addItems(lLstDistrict, "desc", "id").toString();
				inputMap.put("ajaxKey", lStrAjaxResult);
				resObj.setViewName("ajaxData");
			} else if (lLngStateId != null) {
				lLstDistrict = lObjCommonPensionDAO.getDistrictsOfState(lLngStateId, gLngLangId);
				inputMap.put("lLstDistrict", lLstDistrict);
			}
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject getHOOFromDepartment(Map inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		List<ComboValuesVO> lLstHOOFrmDept = new ArrayList<ComboValuesVO>();
		String strAjaxResult = null;
		Long lLngDeptCodeFrmMap = null;
		try {
			setSessionInfo(inputMap);
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			String lStrDepCodeFrmRequest = StringUtility.getParameter("department", request).toString();

			if (inputMap.get("department") != null) {
				lLngDeptCodeFrmMap = Long.valueOf(inputMap.get("department").toString());
			}

			if (lStrDepCodeFrmRequest != null && lStrDepCodeFrmRequest.length() > 0) {
				Long lLngDeptCode = Long.valueOf(lStrDepCodeFrmRequest);
				if (lLngDeptCode.toString().equals("-1")) {
					lLngDeptCode = Long.valueOf(-2);
				}
				lLstHOOFrmDept = lObjCommonPensionDAO.getFieldDeptFromAdmDept(lLngDeptCode);
				strAjaxResult = new AjaxXmlBuilder().addItems(lLstHOOFrmDept, "desc", "id").toString();
				inputMap.put("ajaxKey", strAjaxResult);
				resObj.setViewName("ajaxData");

			} else if (lLngDeptCodeFrmMap != null) {
				if (lLngDeptCodeFrmMap.toString().equals("-1")) {
					lLngDeptCodeFrmMap = Long.valueOf(-2);
				}
				
				lLstHOOFrmDept = lObjCommonPensionDAO.getFieldDeptFromAdmDept(lLngDeptCodeFrmMap);
				inputMap.put("lLstDepartment", lLstHOOFrmDept);

			}

			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject getBranch(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			setSessionInfo(inputMap);
			CommonPensionDAO commonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(null, serv.getSessionFactory());
			ArrayList arrylstBranch = new ArrayList();
			String bankCodeFrmRequest = StringUtility.getParameter("bank", request);
			String bankCodeFrmMap = (String) inputMap.get("bank");
			String lStrLocationCode = lObjTrnPnsnProcInwardPensionDAO.getLocCodeFromDDO(SessionHelper.getLocationCode(inputMap));
			if (bankCodeFrmRequest != null && bankCodeFrmRequest.length() > 0) {
				arrylstBranch = (ArrayList) commonPensionDAO.getBranchesOfBank(bankCodeFrmRequest, gLngLangId, lStrLocationCode);
				String strAjaxResult = new AjaxXmlBuilder().addItems(arrylstBranch, "desc", "id").toString();
				inputMap.put("ajaxKey", strAjaxResult);
				resObj.setViewName("ajaxData");
			} else if (bankCodeFrmMap != null && bankCodeFrmMap.length() > 0) {
				arrylstBranch = (ArrayList) commonPensionDAO.getBranchesOfBank(bankCodeFrmMap, gLngLangId, SessionHelper.getLocationCode(inputMap));
				inputMap.put("listBranch", arrylstBranch);
			}
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}
}
