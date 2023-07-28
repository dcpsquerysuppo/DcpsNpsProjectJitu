package com.tcs.sgv.dcps.service;

//com.tcs.sgv.dcps.service.DcpsDdoSchemeVOGenerator

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DdoSchemeDAO;
import com.tcs.sgv.dcps.dao.DdoSchemeDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsBillGroup;


public class DdoBillGroupVOGenerator extends ServiceImpl implements VOGeneratorService

{

	public ResultObject generateMap(Map inputMap) {

		// TODO Auto-generated method stub

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		// inputMap.get("requestObj");
		MstDcpsBillGroup dcpsddobillgroup = null;

		if (dcpsddobillgroup == null) {
			dcpsddobillgroup = new MstDcpsBillGroup();
		}
		try {

			dcpsddobillgroup = generateBillGroupMap(inputMap);
			inputMap.put("dcpsddobillgroup", dcpsddobillgroup);
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			e.printStackTrace();

		}
		return objRes;
	}

	private MstDcpsBillGroup generateBillGroupMap(Map inputMap) {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");

		MstDcpsBillGroup dcpsBillGroupVO = new MstDcpsBillGroup();

		Integer createOrUpdateFlag = 1;

		try {

			DcpsCommonDAO DcpsCommonDAOObj = new DcpsCommonDAOImpl(null, servLoc.getSessionFactory());
			DdoSchemeDAO lObjDdoSchemeDAO = new DdoSchemeDAOImpl(null, servLoc.getSessionFactory());

			Long LangId = SessionHelper.getLangId(inputMap);
			Long LocId = SessionHelper.getLocationId(inputMap);
			Long DbId = SessionHelper.getDbId(inputMap);
			Long CreatedPostId = SessionHelper.getPostId(inputMap);
			Long CreatedUserId = SessionHelper.getUserId(inputMap);
			Long UpdatedPostId = SessionHelper.getPostId(inputMap);
			Long UpdatedUserId = SessionHelper.getUserId(inputMap);
			Date UpdatedDate = DBUtility.getCurrentDateFromDB();
			Date CreatedDate = DBUtility.getCurrentDateFromDB();

			String lStrDDOCode = DcpsCommonDAOObj.getDdoCode(SessionHelper.getPostId(inputMap));
			dcpsBillGroupVO.setDcpsDdoCode(lStrDDOCode);

			if (!StringUtility.getParameter("createOrUpdateFlag", request).equalsIgnoreCase("") && StringUtility.getParameter("createOrUpdateFlag", request) != null) {
				createOrUpdateFlag = Integer.parseInt(StringUtility.getParameter("createOrUpdateFlag", request).trim());
			}

			String lStrSchemeName = "";
			String lStrDescription = "";

			String lStrSchemeCode = StringUtility.getParameter("txtSchemeCode", request).trim();
			dcpsBillGroupVO.setDcpsDdoSchemeCode(lStrSchemeCode);

			
				// StringUtility.getParameter("cmbSchemeName",request);
			if (!StringUtility.getParameter("txtDescription", request).equalsIgnoreCase("") && StringUtility.getParameter("txtDescription", request) != null) 
				{
					lStrSchemeName = lObjDdoSchemeDAO.getSchemeNameFromCode(lStrSchemeCode);
					lStrDescription = StringUtility.getParameter("txtDescription", request).trim();
				}
			else
				{
					lStrSchemeName = lObjDdoSchemeDAO.getSchemeNameFromCode(lStrSchemeCode);
					lStrDescription = "Bill Group For Scheme " + lStrSchemeName;
				}
			
			dcpsBillGroupVO.setDcpsDdoBillSchemeName(lStrSchemeName);
			dcpsBillGroupVO.setDcpsDdoBillDescription(lStrDescription);

			dcpsBillGroupVO.setLangId(LangId);
			dcpsBillGroupVO.setLocId(LocId);
			dcpsBillGroupVO.setDbId(DbId);
			dcpsBillGroupVO.setPostId(CreatedPostId);
			dcpsBillGroupVO.setUserId(CreatedUserId);
			dcpsBillGroupVO.setCreatedDate(CreatedDate);
			dcpsBillGroupVO.setUpdatedPostId(UpdatedPostId);
			dcpsBillGroupVO.setUpdatedUserId(UpdatedUserId);
			dcpsBillGroupVO.setUpdatedDate(UpdatedDate);

			inputMap.put("createOrUpdateFlag", createOrUpdateFlag);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dcpsBillGroupVO;
	}
}
