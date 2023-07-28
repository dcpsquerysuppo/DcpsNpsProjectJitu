package com.tcs.sgv.dcps.service;

//com.tcs.sgv.dcps.service.DcpsDdoSchemeVOGenerator

import java.util.Date;
import java.util.List;
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
import com.tcs.sgv.dcps.dao.DdoProfileDAO;
import com.tcs.sgv.dcps.dao.DdoProfileDAOImpl;
import com.tcs.sgv.dcps.valueobject.RltDcpsDdoScheme;

public class DcpsDdoSchemeVOGenerator extends ServiceImpl implements
		VOGeneratorService

{

	public ResultObject generateMap(Map inputMap) {
		// TODO Auto-generated method stub
		//System.out.println("In VOGEN  ");

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		// inputMap.get("requestObj");
		RltDcpsDdoScheme dcpsddoscheme = null;

		if (dcpsddoscheme == null) {
			dcpsddoscheme = new RltDcpsDdoScheme();
		}
		try {
		//	System.out.println("inside VOgenerator try");
			inputMap.put("DcpsDdoScheme", dcpsddoscheme);
			dcpsddoscheme = generateVOMap(inputMap);
			inputMap.put("DcpsDdoScheme", dcpsddoscheme);
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

	private RltDcpsDdoScheme generateVOMap(Map inputMap) throws Exception {

		//System.out.println("IN generateVOMap");

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		DdoProfileDAO DdoProfileDAOImplObj = new DdoProfileDAOImpl(
				RltDcpsDdoScheme.class, serv.getSessionFactory());
		String lStrSchemeCode = null;

		if ((StringUtility.getParameter("txtSchemeCode", request) != null)
				&& (!StringUtility.getParameter("txtSchemeCode", request)
						.equals(""))) {
			lStrSchemeCode = (StringUtility.getParameter("txtSchemeCode",
					request));
		}

		RltDcpsDdoScheme dcpsddoscheme = new RltDcpsDdoScheme();

		try {

			Long LangId = SessionHelper.getLangId(inputMap);
			Long LocId = SessionHelper.getLocationId(inputMap);
			Long DbId = SessionHelper.getDbId(inputMap);
			Long PostId = SessionHelper.getPostId(inputMap);
			Long UserId = SessionHelper.getUserId(inputMap);
			Date CreatedDate = DBUtility.getCurrentDateFromDB();

			Long UpdatedPostId = SessionHelper.getPostId(inputMap);
			Long UpdatedUserId = SessionHelper.getUserId(inputMap);
			Date UpdatedDate = DBUtility.getCurrentDateFromDB();
			String lStrDDOCode = DdoProfileDAOImplObj.getDdoCode(PostId);

			if ((StringUtility.getParameter("txtSchemeCode", request) != null)
					&& (!StringUtility.getParameter("txtSchemeCode", request)
							.equals(""))) {
				dcpsddoscheme.setDcpsSchemeCode(lStrSchemeCode);
			}

			Boolean schemeAddedOrNot = true;

			List lListSchemeCodeAndNames = DdoProfileDAOImplObj
					.getSchemeListForDDO(lStrDDOCode);

			for (int lInt = 0; lInt < lListSchemeCodeAndNames.size(); lInt++) {
				Object[] lObjTemp = (Object[]) lListSchemeCodeAndNames
						.get(lInt);
				//System.out.println("lObjTemp-->" + lObjTemp);
				String lStrSchemeCodeFromDB = (String) lObjTemp[0];
				//System.out.println("lStrSchemeCodeFromDB-->"
				//		+ lStrSchemeCodeFromDB);
				if (lStrSchemeCode.equals(lStrSchemeCodeFromDB)) {
					schemeAddedOrNot = false;

				}
			}

			inputMap.put("schemeAddedOrNot", schemeAddedOrNot);

			dcpsddoscheme.setDcpsDdoCode(lStrDDOCode);
			dcpsddoscheme.setLangId(LangId);
			dcpsddoscheme.setLocId(LocId);
			dcpsddoscheme.setDbId(DbId);
			dcpsddoscheme.setCreatedPostId(PostId);
			dcpsddoscheme.setCreatedUserId(UserId);
			dcpsddoscheme.setCreatedDate(CreatedDate);

			dcpsddoscheme.setUpdatedPostId(UpdatedPostId);
			dcpsddoscheme.setUpdatedUserId(UpdatedUserId);
			dcpsddoscheme.setUpdatedDate(UpdatedDate);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dcpsddoscheme;
	}

}
