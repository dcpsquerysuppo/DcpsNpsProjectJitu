package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;


public class RevisedArrearCalcVOGenerator extends ServiceImpl implements VOGeneratorService {

	/* Global Variable for PostId */
	private Long gLngPostId = null;

	/* Global Variable for UserId */
	private Long gLngUserId = null;

	/* Global Variable for Current Date */
	private Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	/**
	 * Generates VO for insertion into trn_pension_arrear_dtls
	 * 
	 * @param Map
	 *            :objArgs
	 * @return ResultObject
	 */
	public ResultObject generateMap(Map objArgs) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			setSessionInfo(objArgs);
			TrnPensionArrearDtls lObjTrnPensionArrearDtls = new TrnPensionArrearDtls();

			lObjTrnPensionArrearDtls = generateTrnPensionArrearDtls(objArgs);
			objArgs.put("lObjTrnPensionArrearDtls", lObjTrnPensionArrearDtls);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

	private void setSessionInfo(Map inputMap) {

		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gDtCurrDt = DBUtility.getCurrentDateFromDB();
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
	}

	private TrnPensionArrearDtls generateTrnPensionArrearDtls(Map lMapInput) throws Exception {

		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");

		String lStrpayfrom = null;
		String lStrPayMon = null;

		Integer nextbillpayyr = 0;
		Integer nextbillpaymon = 0;
		String final_next_monyr = null;

		TrnPensionArrearDtls lObjTrnPensionArrearDtls = null;
		try {
			lObjTrnPensionArrearDtls = new TrnPensionArrearDtls();

			String lStrpenreqid = StringUtility.getParameter("hidPensnReqId", request);
			String lStrpenrcode = StringUtility.getParameter("hidPnsnrCode", request);
			String lStrefffrom = StringUtility.getParameter("fromdate", request);
			String lStreffto = StringUtility.getParameter("todate", request);
			lStrpayfrom = StringUtility.getParameter("hidlastBillMonth", request);
			Long lLngNetArrear = Long.valueOf(StringUtility.getParameter("hidArrearAmnt", request));

			// String Efffrom = lStrefffrom.substring(6) +
			// lStrefffrom.substring(3, 5);
			// String Effto = lStreffto.substring(6) + lStreffto.substring(3,
			// 5);

			if (lStrpayfrom != null && !lStrpayfrom.equals("")) {
				if (lStrpayfrom.substring(3, 5).equals("12")) {
					nextbillpayyr = Integer.parseInt(lStrpayfrom.substring(6)) + 1;
					lStrPayMon = "01";
				} else {
					nextbillpayyr = Integer.parseInt(lStrpayfrom.substring(6));
					nextbillpaymon = Integer.parseInt(lStrpayfrom.substring(3, 5)) + 1;
					if (nextbillpaymon <= 9) {
						lStrPayMon = "0" + nextbillpaymon;
					} else {
						lStrPayMon = nextbillpaymon.toString();
					}
				}
				final_next_monyr = nextbillpayyr.toString() + lStrPayMon;
			} else {
				Calendar lObjCalendar = Calendar.getInstance();
				int month = lObjCalendar.get(Calendar.MONTH) + 1;
				int year = lObjCalendar.get(Calendar.YEAR);

				final_next_monyr = "" + year + (month < 10 ? "0" + month : month);
			}

			lObjTrnPensionArrearDtls.setPensionRequestId(Long.parseLong(lStrpenreqid));
			lObjTrnPensionArrearDtls.setPensionerCode(lStrpenrcode);
			lObjTrnPensionArrearDtls.setArrearFieldType("Pension");
			// lObjTrnPensionArrearDtls.setEffectedFromYyyymm(Integer.parseInt(Efffrom));
			// lObjTrnPensionArrearDtls.setEffectedToYyyymm(Integer.parseInt(Effto));
			lObjTrnPensionArrearDtls.setDifferenceAmount(new BigDecimal(lLngNetArrear.toString()));
			lObjTrnPensionArrearDtls.setTotalDifferenceAmt(new BigDecimal(lLngNetArrear.toString()));
			lObjTrnPensionArrearDtls.setPaymentFromYyyymm(Integer.parseInt(final_next_monyr));
			lObjTrnPensionArrearDtls.setPaymentToYyyymm(Integer.parseInt(final_next_monyr));
			lObjTrnPensionArrearDtls.setRemarks("Arrear Due to Revision");
			lObjTrnPensionArrearDtls.setCreatedDate(gDtCurrDt);
			lObjTrnPensionArrearDtls.setCreatedPostId(new BigDecimal(gLngPostId));
			lObjTrnPensionArrearDtls.setCreatedUserId(new BigDecimal(gLngUserId));

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lObjTrnPensionArrearDtls;
	}
}