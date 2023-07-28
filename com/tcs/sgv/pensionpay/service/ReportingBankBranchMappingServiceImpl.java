package com.tcs.sgv.pensionpay.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.RltBankBranch;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.ReportingBankBranchDAO;
import com.tcs.sgv.pensionpay.dao.ReportingBankBranchDAOImpl;


public class ReportingBankBranchMappingServiceImpl extends ServiceImpl implements ReportingBankBranchMappingService {

	Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurDate = null;

	Long gLngLangId = null;

	String gStrLocationCode = null;

	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	private void setSessionInfo(Map inputMap) {

		request = (HttpServletRequest) inputMap.get("requestObj");
		serv = (ServiceLocator) inputMap.get("serviceLocator");
		gLngPostId = SessionHelper.getPostId(inputMap);
		gStrPostId = gLngPostId.toString();
		gLngUserId = SessionHelper.getUserId(inputMap);
		gLngUserId.toString();
		gDtCurDate = SessionHelper.getCurDate();
		gLngLangId = SessionHelper.getLangId(inputMap);
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
	}

	public ResultObject loadReportingBankBranchForm(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
		ReportingBankBranchDAO lObjReportingBankBranchDAO = new ReportingBankBranchDAOImpl(RltBankBranch.class, serv.getSessionFactory());
		List<ComboValuesVO> lLstBank = null;
		List<Long> lLstTreasuryId = new ArrayList<Long>();
		List<ComboValuesVO> lLstTreasury = null;
		List<RltBankBranch> lLstRltBankBranchDtls = null;
		List lLstReportingBranchName = new ArrayList();
		String lStrLocCode = null;
		String lStrBankCode = null;
		String lStrUpdateType = null;
		int totalRecords = 0;
		try {
			lLstBank = lObjCommonDAO.getBankList(inputMap, gLngLangId);
			lLstTreasuryId.add(Long.valueOf(gObjRsrcBndle.getString("PPMT.TREASURYID1")));
			lLstTreasury = lObjCommonDAO.getAllTreasury(gLngLangId, lLstTreasuryId);
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);

			lStrLocCode = gStrLocationCode;
			lStrBankCode = StringUtility.getParameter("cmbBankList", request);
			lStrUpdateType = StringUtility.getParameter("rdoUpdateType", request);

			if (!"".equals(lStrLocCode.trim())) {
				if (!"".equals(lStrBankCode.trim())) {
					totalRecords = lObjReportingBankBranchDAO.getRltBankBranchCount(lStrBankCode, lStrLocCode);
					if (totalRecords > 0) {
						lLstRltBankBranchDtls = lObjReportingBankBranchDAO.getRltBankBranchDtls(lStrBankCode, lStrLocCode, displayTag);
						lLstReportingBranchName = lObjReportingBankBranchDAO.getReportingBranchName();
					}
				}
			}
			inputMap.put("lLstBank", lLstBank);
			inputMap.put("lLstReportingBranchName", lLstReportingBranchName);
			inputMap.put("lLstTreasury", lLstTreasury);
			inputMap.put("lStrLocCode", lStrLocCode);
			inputMap.put("lStrBankCode", lStrBankCode);
			inputMap.put("lStrUpdateType", lStrUpdateType);
			inputMap.put("lLstRltBankBranch", lLstRltBankBranchDtls);
			inputMap.put("totalRecords", totalRecords);

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadReportingBankBranchForm : ");
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("ReportingBranchConfig");
		return resObj;
	}

	public ResultObject updateReportingBankBranch(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ReportingBankBranchDAO lObjReportingBankBranchDAO = new ReportingBankBranchDAOImpl(RltBankBranch.class, serv.getSessionFactory());

		String lStrLocCode = null;
		String lStrBankCode = null;
		String lStrBranchCode = null;
		String lStrUpdateType = null;
		String lStrEcsBranch = null;
		String lStrMicrCode = null;
		String lStrIfscCode = null;
		Long lLngBranchCode = 0L;
		String lStrAllReportingBankCode = null;
		String lStrAllReportingBranchCode = null;
		String lStrReportingBankCode = null;
		String lStrReportingBranchCode = null;
		String[] lArrStrBranchCode = null;
		String[] lArrStrReportingBankCode = null;
		String[] lArrStrReportingBranchCode = null;
		String[] lArrStrEcsBranch = null;
		String[] lArrStrMicrCode = null;
		String[] lArrStrIfscCode = null;
		Boolean lBlFlag = false;

		try {

			lStrLocCode = gStrLocationCode;
			lStrBankCode = StringUtility.getParameter("cmbBankList", request);
			lStrAllReportingBranchCode = StringUtility.getParameter("cmbAllReportBranchList", request);
			lStrAllReportingBankCode = StringUtility.getParameter("cmbAllReportBankList", request);

			lStrBranchCode = StringUtility.getParameter("BranchCode", request);
			lStrEcsBranch = StringUtility.getParameter("strEcsBranch", request);
			lStrMicrCode = StringUtility.getParameter("strMicrCode", request);
			lStrIfscCode = StringUtility.getParameter("strIfscCode", request);
			lStrReportingBankCode = StringUtility.getParameter("rptBankCode", request);
			lStrReportingBranchCode = StringUtility.getParameter("rptBranchCode", request);

			lArrStrReportingBankCode = lStrReportingBankCode.split("~");
			lArrStrReportingBranchCode = lStrReportingBranchCode.split("~");
			lArrStrBranchCode = lStrBranchCode.split("~");
			lArrStrEcsBranch = lStrEcsBranch.split("~");
			lArrStrMicrCode = lStrMicrCode.split("~");
			lArrStrIfscCode = lStrIfscCode.split("~");

			lStrUpdateType = StringUtility.getParameter("rdoUpdateType", request);
			if (!"".equals(lStrLocCode.trim())) {
				if (!"".equals(lStrBankCode.trim())) {
					if ("A".equals(lStrUpdateType)) {
						if (!"".equals(lStrAllReportingBankCode.trim()) && !"".equals(lStrAllReportingBranchCode.trim())) {
							lObjReportingBankBranchDAO.updateAllReportingBankBranch(lStrBankCode, lStrLocCode, lStrAllReportingBankCode, lStrAllReportingBranchCode);
						}
						lBlFlag = true;
					} else if ("I".equals(lStrUpdateType)) {
						for (Integer lInt = 0; lInt < lArrStrBranchCode.length; lInt++) {

							lStrBranchCode = lArrStrBranchCode[lInt].trim();
							if (lStrBranchCode != null && !"".equals(lStrBranchCode)) {
								lLngBranchCode = Long.parseLong(lStrBranchCode);
							}
							lStrReportingBankCode = lArrStrReportingBankCode[lInt].trim();
							lStrReportingBranchCode = lArrStrReportingBranchCode[lInt].trim();
							lStrEcsBranch = lArrStrEcsBranch[lInt].trim();
							lStrMicrCode = lArrStrMicrCode[lInt].trim();
							lStrIfscCode = lArrStrIfscCode[lInt].trim().toUpperCase();

							if (lLngBranchCode != 0L) {
								lObjReportingBankBranchDAO.updateIndividualReportingBankBranch(lLngBranchCode, lStrLocCode, lStrReportingBankCode, lStrReportingBranchCode, lStrEcsBranch,
										lStrMicrCode, lStrIfscCode, gLngUserId, gLngPostId, gDtCurDate);
							}

						}
						lBlFlag = true;
					}
				}
			}

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadReportingBankBranchForm : ");
		}
		String lSBStatus = getUpdateResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	private StringBuilder getUpdateResponseXMLDoc(Boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
}
