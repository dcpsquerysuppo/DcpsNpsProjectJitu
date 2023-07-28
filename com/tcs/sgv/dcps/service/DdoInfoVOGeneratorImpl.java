/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 19, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.DDOInformationDetail;

/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Mar 19, 2011
 */
public class DdoInfoVOGeneratorImpl extends ServiceImpl {

	Log gLogger = LogFactory.getLog(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.DCPSDdoInfoVOGenerator#generateMap(java.util
	 * .Map)
	 */
	public ResultObject generateMap(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {

			DDOInformationDetail lObjDcpsDdoInfoVO = new DDOInformationDetail();
			lObjDcpsDdoInfoVO = generateDcpsDdoInfoVO(inputMap);
			inputMap.put("lObjDcpsDdoInfoVO", lObjDcpsDdoInfoVO);
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			gLogger.info("Error is  " + e);
		}

		return objRes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.DCPSDdoInfoVOGenerator#generateDcpsDdoInfoVO
	 * (java.util.Map)
	 */
	public DDOInformationDetail generateDcpsDdoInfoVO(Map inputMap) {

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");

		DDOInformationDetail lObjDDOInfo = new DDOInformationDetail();

		try {
			String lStrAdministrativeDept = StringUtility.getParameter(
					"txtAdminDept", request);
			String lStrFieldHodDept = StringUtility.getParameter(
					"txtFieldDept", request);
			String lStrDdoName = StringUtility.getParameter("txtDDOName",
					request);
			String lStrDdoDesignation = StringUtility.getParameter(
					"cmbDesignation", request);
			String lStrWefDate = StringUtility.getParameter("txtWEFDate",
					request);
			String lStrTanNo = StringUtility.getParameter("txtTANNo", request);
			String lStrItawardcircle = StringUtility.getParameter(
					"txtITWardCircle", request);
			String lStrBankName = StringUtility.getParameter("cmbBankName",
					request);
			String lStrBranchName = StringUtility.getParameter("cmbBranchName",
					request);
			String lStrAccountNo = StringUtility.getParameter("txtAccountNo",
					request);
			String lStrRemarks = StringUtility.getParameter("txtRemarks",
					request);

			String lStrDdoCode = StringUtility.getParameter("txtDdoCode",
					request);

			String lStrIfscCode = StringUtility.getParameter("txtIFSCCode",
					request);

			Date dtWEFDate = null;

			if (lStrWefDate != null && !"".equals(lStrWefDate.trim())) {
				dtWEFDate = IFMSCommonServiceImpl
						.getDateFromString(lStrWefDate);
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
			lObjDDOInfo.setIfscCode(lStrIfscCode);

		} catch (Exception ex) {

		}

		return lObjDDOInfo;
	}

}
