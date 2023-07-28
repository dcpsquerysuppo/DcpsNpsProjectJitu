package com.tcs.sgv.pensionpay.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.TrnBillRegister;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;


public class PensionSpecificBillVOGenerator extends ServiceImpl implements VOGeneratorService {

	public ResultObject generateMap(Map lMapInput) {

		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Log Logger = LogFactory.getLog(getClass());
		TrnBillRegister lObjBillRegister = null;
		try {
			OnlineBillVOGenerator objOnlineBillVOGenerator = new OnlineBillVOGenerator();

			// For donot make entry into DSS Data.
			String lStrPnsnBillType = StringUtility.getParameter("hidBillTypeId", request).trim();
			if ("44".equals(lStrPnsnBillType) || "43".equals(lStrPnsnBillType)) {
				lMapInput.put("PnsnDummyBill", "Y");
			}
			objOnlineBillVOGenerator.generateMap(lMapInput);
			if (!"44".equals(lStrPnsnBillType)) {
				lObjBillRegister = (TrnBillRegister) lMapInput.get("BillRegVO");
				String lPnsnTokenNo = StringUtility.getParameter("txtPnsnTokenNo", request).trim();
				String lStrCardexNo = StringUtility.getParameter("hidCardexNo", request);
				if (lPnsnTokenNo.length() > 0) {
					lObjBillRegister.setTokenNum(Long.parseLong(lPnsnTokenNo));
					lObjBillRegister.setInwardedPost(SessionHelper.getPostId(lMapInput));
					lObjBillRegister.setInwardUserId(SessionHelper.getUserId(lMapInput));
					lObjBillRegister.setInwardDt(SessionHelper.getCurDate());
				}
				String lStrPPONO = StringUtility.getParameter("hidDPPONO", request).trim();
				if (lStrPPONO.length() > 0) {
					lObjBillRegister.setPpoNo(lStrPPONO.toUpperCase());
				}
				lObjBillRegister.setPhyBill(DBConstants.CMN_PensionBillType);
				if ((lObjBillRegister != null) && (0 == lObjBillRegister.getBillNo())) {
					lObjBillRegister.setCurrBillStatus(DBConstants.ST_BAPRVD_DDO);
				}
				lObjBillRegister.setExempted("Y");
				lObjBillRegister.setTsryOfficeCode(SessionHelper.getLocationCode(lMapInput));

				if (lStrCardexNo != null && lStrCardexNo.length() > 0) {
					lObjBillRegister.setCardexNo(Long.valueOf(lStrCardexNo));
				}
			}
		} catch (Exception e) {
			Logger.error("Error is : ", e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}
}