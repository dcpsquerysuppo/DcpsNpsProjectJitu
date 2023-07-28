package com.tcs.sgv.pensionpay.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.PensionCaseDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.RltPensioncaseBill;


public class PensionCaseServiceImpl extends ServiceImpl implements PensionCaseService {

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

	/* Global Variable for Current Date */
	Date gCurDate = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	CmnLocationMst gStrLocationVo = null;

	Log gLogger = LogFactory.getLog(getClass());

	String[] lStrMonth = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

	public ResultObject generateBillCase(Map<String, Object> argsMap) {

		Map<String, Object> inputMap = argsMap;
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		setSessionInfo(inputMap);
		try {
			PensionCaseDAOImpl pensionCaseDAOImpl = new PensionCaseDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			String lStrPPONo = StringUtility.getParameter("ppoNo", request);
			String lStrBillType = StringUtility.getParameter("BillType", request);

			StringBuilder lStrData = new StringBuilder();
			lStrData.append("<XMLDOC>");
			lStrData.append("<BillType>");
			lStrData.append(lStrBillType);
			lStrData.append("</BillType>");

			lStrData.append("<PPONo>");
			lStrData.append(lStrPPONo);
			lStrData.append("</PPONo>");

			List lLstFlagDtls = new ArrayList();

			// if (lStrBillType.equals("DCRG")) {
			// lLstFlagDtls = pensionCaseDAOImpl.getDCRGPaidFlag(lStrPPONo,
			// lStrBillType, gStrLocationCode);
			// } else if (lStrBillType.equals("CVP")) {
			// lLstFlagDtls = pensionCaseDAOImpl.getCVPPaidFlag(lStrPPONo,
			// lStrBillType, gStrLocationCode);
			// }

			List lLstRes = pensionCaseDAOImpl.getBillCaseDtls(lStrPPONo, lStrBillType, gStrLocationCode);
			if (!lLstRes.isEmpty()) {

				for (int i = 0; i < lLstRes.size(); i++) {

					lStrData.append("<CreatedBill>");
					lStrData.append(lStrBillType + " bill is already created for PPO no " + lStrPPONo);
					lStrData.append("</CreatedBill>");
				}

			}
			// if (!lLstFlagDtls.isEmpty()) {
			// if (lLstFlagDtls.get(0) != null) {
			// if (("N").equals(lLstFlagDtls.get(0).toString())) {
			// lStrData.append("<Paid>");
			// lStrData.append(lStrBillType + " cannot be generted for PPO no "
			// + lStrPPONo);
			// lStrData.append("</Paid>");
			// }
			// }

			// }
			lStrData.append("</XMLDOC>");

			// System.out.println("========================================================================");
			// System.out.println(lStrData);
			// System.out.println("========================================================================");
			String lStrAjaxResult = new AjaxXmlBuilder().addItem("ajax_key", lStrData.toString()).toString();
			inputMap.put("ajaxKey", lStrAjaxResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		// System.out.println("Over ---------------------------check bill is already created for this PPO No or not");
		return resObj;
	}
	
	public ResultObject validatePpoBeforeMoveToLibrary(Map<String, Object> argsMap) {

		Map<String, Object> inputMap = argsMap;
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		setSessionInfo(inputMap);
		try {
			PensionCaseDAOImpl pensionCaseDAOImpl = new PensionCaseDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			String lStrPpoNo = StringUtility.getParameter("ppoNo", request);
			
			List<Long> lLstBillNo = new ArrayList<Long>();
			List<Short> lLstBillStatus = new ArrayList<Short>();
			List lLstBillDtls = new ArrayList();
			RltPensioncaseBill lObjRltPensioncaseBill = new RltPensioncaseBill();
			
			lLstBillStatus.add(DBConstants.ST_BILL_FORW_TO_TAUD);
			lLstBillStatus.add(DBConstants.ST_BILL_FORW_TO_USER);
			lLstBillStatus.add(DBConstants.ST_BILL_ECS_AUTH);
			
			StringBuilder lStrData = new StringBuilder();
					
			List lLstResult = pensionCaseDAOImpl.getBillCaseDtls(lStrPpoNo, "PENSION", gStrLocationCode);
			if (!lLstResult.isEmpty()) {

				for (int i = 0; i < lLstResult.size(); i++) {
					lObjRltPensioncaseBill = (RltPensioncaseBill) lLstResult.get(i);
					lLstBillNo.add(lObjRltPensioncaseBill.getBillNo());
				}
			}
			if(!lLstBillNo.isEmpty())
			{
				lLstBillDtls = pensionCaseDAOImpl.getPensionBillDtls(lLstBillNo, lLstBillStatus);
			}
			lStrData.append("<XMLDOC>");
			lStrData.append("<VALIDPPO>");
			if(lLstBillDtls.isEmpty())
			{
				lStrData.append("N");			
			}
			else
			{
				lStrData.append("Y");
			}
			lStrData.append("</VALIDPPO>");
			lStrData.append("</XMLDOC>");

			String lStrAjaxResult = new AjaxXmlBuilder().addItem("ajax_key", lStrData.toString()).toString();
			inputMap.put("ajaxKey", lStrAjaxResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		
		return resObj;
	}

	private void setSessionInfo(Map inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngEmpId = SessionHelper.getEmpId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocId = SessionHelper.getLocationCode(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gStrLocationVo = SessionHelper.getLocationVO(inputMap);
		gCurDate = DBUtility.getCurrentDateFromDB();
	}

}