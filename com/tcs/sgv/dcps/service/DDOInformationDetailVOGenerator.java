package com.tcs.sgv.dcps.service;

//com.tcs.sgv.dcps.service.DDOInformationDetailVOGenerator

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.DDOInformationDetail;

public class DDOInformationDetailVOGenerator extends ServiceImpl implements
		VOGeneratorService {

	@Override
	public ResultObject generateMap(Map inputMap) {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		inputMap.get("requestObj");

		DDOInformationDetail lObjDDOInfo = null;

		try {
			inputMap.put("DDOInfo", lObjDDOInfo);
			lObjDDOInfo = generateDDOInfo(inputMap);
			inputMap.put("DDOInfo", lObjDDOInfo);
			objRes.setResultValue(inputMap);
		} catch (Exception ex) {
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			ex.printStackTrace();
		}
		return objRes;
	}

	private DDOInformationDetail generateDDOInfo(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		DDOInformationDetail lObjDDOInfo = (DDOInformationDetail) inputMap
				.get("DDOInfo");

		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		String lStrAdministrativeDept = StringUtility.getParameter(
				"txtAdminDept", request);
		String lStrFieldHodDept = StringUtility.getParameter("txtFieldDept",
				request);
		String lStrDdoName = StringUtility.getParameter("txtDDOName", request);
		String lStrDdoDesignation = StringUtility.getParameter(
				"cmbDesignation", request);
		String lStrWefDate = StringUtility.getParameter("txtWEFDate", request);
		String lStrTanNo = StringUtility.getParameter("txtTANNo", request);
		String lStrItawardcircle = StringUtility.getParameter(
				"txtITWardCircle", request);
		String lStrBankName = StringUtility
				.getParameter("cmbBankName", request);
		String lStrBranchName = StringUtility.getParameter("cmbBranchName",
				request);
		String lStrAccountNo = StringUtility.getParameter("txtAccountNo",
				request);
		String lStrRemarks = StringUtility.getParameter("txtRemarks", request);

		String lStrDdoCode = StringUtility.getParameter("txtDdoCode", request);

		Date dtWEFDate = null;

		if (lStrWefDate != null && !"".equals(lStrWefDate.trim())) {
			dtWEFDate = IFMSCommonServiceImpl.getDateFromString(lStrWefDate);
		}

		try {
			if (lObjDDOInfo == null) {
				lObjDDOInfo = new DDOInformationDetail();
			}

			lObjDDOInfo.setDdoCode(lStrDdoCode);
			lObjDDOInfo.setAdministrativeDept(lStrAdministrativeDept);
			lObjDDOInfo.setFieldHodDept(lStrFieldHodDept);
			lObjDDOInfo.setDdoName(lStrDdoName);
			lObjDDOInfo.setDdoDesignation(lStrDdoDesignation);
			lObjDDOInfo.setWefDate(dtWEFDate);
			lObjDDOInfo.setTanNo(lStrTanNo);
			lObjDDOInfo.setItawardcircle(lStrItawardcircle);
			lObjDDOInfo.setBankName(lStrBankName);
			lObjDDOInfo.setBranchName(lStrBranchName);
			lObjDDOInfo.setAccountNo(lStrAccountNo);
			lObjDDOInfo.setRemarks(lStrRemarks);
			lObjDDOInfo.setCreatedDate(gDtCurrDt);

		} catch (Exception ex) {

		}

		return lObjDDOInfo;

	}

}
