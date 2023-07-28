package com.tcs.sgv.dcps.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.MstDcpsTierICntrnbtn;

public class ArrearsVOGeneratorImpl extends ServiceImpl {

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for EmpId */
	Long gLngEmpId = null;

	/* Global Variable for Location Id */
	String gStrLocId = null;

	/* Global Variable for DB Id */
	Long gLngDBId = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	String gStrAuditorFlag = null;

	String gStrStatus = null;

	Long gLngAuditPostId = null;

	Long gLngAuditUserId = null;

	Log gLogger = LogFactory.getLog(getClass());

	Date gDateDBDate = null;

	// Sets session information in the global variables
	private void setSessionInfo(Map inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngEmpId = SessionHelper.getEmpId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocId = SessionHelper.getLocationCode(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gDateDBDate = DBUtility.getCurrentDateFromDB();
	}

	public ResultObject generateMap(Map inputMap) {
		gLogger.info("In generateMap of DCPSArrearsVOGeneratorImpl........");
		//System.out.println("DCPSArrearsVOGeneratorImpl");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {
			List<MstDcpsTierICntrnbtn> lLstMstDcpsTierICntrnbtnVO = new ArrayList<MstDcpsTierICntrnbtn>();
			lLstMstDcpsTierICntrnbtnVO = generateMstDcpsTierICntrnbtnVO(inputMap);
			inputMap.put("lLstMstDcpsTierICntrnbtnVO",
					lLstMstDcpsTierICntrnbtnVO);
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);

		}
		return objRes;

	}

	public List<MstDcpsTierICntrnbtn> generateMstDcpsTierICntrnbtnVO(
			Map inputMap) {

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		MstDcpsTierICntrnbtn lObjMstDcpsTierICntrnbtnVO = null;
		List<MstDcpsTierICntrnbtn> lLstMstDcpsTierICntrnbtnVO = new ArrayList<MstDcpsTierICntrnbtn>();

		try {

			setSessionInfo(inputMap);
			String[] lArrStrEmployeeId = StringUtility.getParameterValues(
					"hidEmpId", request);

			String[] lArrStrEmployeePensionId = StringUtility
					.getParameterValues("txtEmpPnsnId", request);

			String[] lArrStrEmployeeName = StringUtility.getParameterValues(
					"txtEmpName", request);

			String[] lArrStrArrearstypeLookupid = StringUtility
					.getParameterValues("hidTypeOfArrearId", request);

			String[] lArrStrParentDepartmentId = StringUtility
					.getParameterValues("hidFieldDeptId", request);

			String[] lArrStrDesignationId = StringUtility.getParameterValues(
					"hidDesignationId", request);

			String[] lArrStrTierICntrnbtnFromDate = StringUtility
					.getParameterValues("TierICntrnbtnFromDate", request);

			String[] lArrStrTierICntrnbtnToDate = StringUtility
					.getParameterValues("TierICntrnbtnToDate", request);

			String[] lArrStrTotalAmount = StringUtility.getParameterValues(
					"txtAmount", request);

			String[] lArrStrNoOfInstlmnt = StringUtility.getParameterValues(
					"hidNoOfInstlmnt", request);

			String[] lArrStrWholeMonthInstlmntAmount = StringUtility
					.getParameterValues("hidMonthInstlmntAmt", request);

			String[] lArrStrFirstOddInstlmntAmount = StringUtility
					.getParameterValues("hidFirstOddInstlmntAmt", request);

			String[] lArrStrLastOddInstlmntAmount = StringUtility
					.getParameterValues("hidLastOddInstlmntAmt", request);

			for (int lIntCnt = 0; lIntCnt < lArrStrEmployeeId.length; lIntCnt++) {

				lObjMstDcpsTierICntrnbtnVO = new MstDcpsTierICntrnbtn();
				lObjMstDcpsTierICntrnbtnVO.setDbId(gLngDBId);
				lObjMstDcpsTierICntrnbtnVO.setLocationCode(Long
						.parseLong(gStrLocationCode));
				lObjMstDcpsTierICntrnbtnVO.setCreatedUserId(gLngUserId);
				lObjMstDcpsTierICntrnbtnVO.setCreatedPostId(gLngPostId);
				lObjMstDcpsTierICntrnbtnVO.setCreatedDate(gDateDBDate);

				if (lArrStrEmployeeId[lIntCnt] != null
						&& lArrStrEmployeeId[lIntCnt].trim().length() > 0) 
				{
					lObjMstDcpsTierICntrnbtnVO.setEmployeeId(Long
							.parseLong(lArrStrEmployeeId[lIntCnt].trim()));
				}
				else
				{
					lObjMstDcpsTierICntrnbtnVO.setEmployeeId(Long
							.parseLong(lArrStrEmployeePensionId[lIntCnt].trim()));
				}
				if (lArrStrEmployeePensionId[lIntCnt] != null
						&& lArrStrEmployeePensionId[lIntCnt].trim().length() > 0) {
					lObjMstDcpsTierICntrnbtnVO
							.setEmployeePensionId(Long
									.parseLong(lArrStrEmployeePensionId[lIntCnt]
											.trim()));
				}
				if (lArrStrEmployeeName[lIntCnt] != null
						&& lArrStrEmployeeName[lIntCnt].trim().length() != 0) {
					lObjMstDcpsTierICntrnbtnVO
							.setEmployeeName(lArrStrEmployeeName[lIntCnt]
									.trim());
				}

				if (lArrStrArrearstypeLookupid[lIntCnt] != null
						&& lArrStrArrearstypeLookupid[lIntCnt].trim().length() > 0) {
					lObjMstDcpsTierICntrnbtnVO.setArrearstypeLookupid(Long
							.parseLong(lArrStrArrearstypeLookupid[lIntCnt]
									.trim()));
				}

				if (lArrStrParentDepartmentId[lIntCnt] != null
						&& lArrStrParentDepartmentId[lIntCnt].trim().length() > 0) {
					lObjMstDcpsTierICntrnbtnVO.setParentDepartmentId(Long
							.parseLong(lArrStrParentDepartmentId[lIntCnt]
									.trim()));
				}

				if (lArrStrDesignationId[lIntCnt] != null
						&& lArrStrDesignationId[lIntCnt].trim().length() > 0) {
					lObjMstDcpsTierICntrnbtnVO.setDesignationId(Long
							.parseLong(lArrStrDesignationId[lIntCnt].trim()));
				}
				if (lArrStrTierICntrnbtnFromDate[lIntCnt] != null
						&& lArrStrTierICntrnbtnFromDate[lIntCnt].trim()
								.length() > 0) {
					lObjMstDcpsTierICntrnbtnVO
							.setTierICntrnbtnFromDate(simpleDateFormat
									.parse(lArrStrTierICntrnbtnFromDate[lIntCnt]
											.trim()));
				}
				if (lArrStrTierICntrnbtnToDate[lIntCnt] != null
						&& lArrStrTierICntrnbtnToDate[lIntCnt].trim().length() > 0) {
					lObjMstDcpsTierICntrnbtnVO
							.setTierICntrnbtnToDate(simpleDateFormat
									.parse(lArrStrTierICntrnbtnToDate[lIntCnt]
											.trim()));
				}

				if (lArrStrTotalAmount[lIntCnt] != null
						&& lArrStrTotalAmount[lIntCnt].trim().length() > 0) {
					lObjMstDcpsTierICntrnbtnVO.setTotalAmount(new BigDecimal(
							lArrStrTotalAmount[lIntCnt].trim()));
				}
				if (lArrStrNoOfInstlmnt[lIntCnt] != null
						&& lArrStrNoOfInstlmnt[lIntCnt].trim().length() > 0) {
					lObjMstDcpsTierICntrnbtnVO.setNoOfInstlmnt(Long
							.parseLong(lArrStrNoOfInstlmnt[lIntCnt].trim()));
				}
				if (lArrStrWholeMonthInstlmntAmount[lIntCnt] != null
						&& lArrStrWholeMonthInstlmntAmount[lIntCnt].trim()
								.length() > 0) {
					lObjMstDcpsTierICntrnbtnVO
							.setWholeMonthInstlmntAmount(new BigDecimal(
									lArrStrWholeMonthInstlmntAmount[lIntCnt]
											.trim()));
				}
				if (lArrStrFirstOddInstlmntAmount[lIntCnt] != null
						&& lArrStrFirstOddInstlmntAmount[lIntCnt].trim()
								.length() > 0) {
					lObjMstDcpsTierICntrnbtnVO
							.setFirstOddInstlmntAmount(new BigDecimal(
									lArrStrFirstOddInstlmntAmount[lIntCnt]
											.trim()));
				}
				if (lArrStrLastOddInstlmntAmount[lIntCnt] != null
						&& lArrStrLastOddInstlmntAmount[lIntCnt].trim()
								.length() > 0) {
					lObjMstDcpsTierICntrnbtnVO
							.setLastOddInstlmntAmount(new BigDecimal(
									lArrStrLastOddInstlmntAmount[lIntCnt]
											.trim()));
				}

				lLstMstDcpsTierICntrnbtnVO.add(lObjMstDcpsTierICntrnbtnVO);
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);

		}

		return lLstMstDcpsTierICntrnbtnVO;
	}

}
