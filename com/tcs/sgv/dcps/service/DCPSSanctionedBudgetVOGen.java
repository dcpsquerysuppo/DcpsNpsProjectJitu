package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import com.tcs.sgv.common.helper.SessionHelper;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.SanctionedBudget;

public class DCPSSanctionedBudgetVOGen extends ServiceImpl implements
		VOGeneratorService {

	public ResultObject generateMap(Map inputMap) {
		// TODO Auto-generated method stub

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);

		SanctionedBudget lArrDcpsSancBudget = null;

		try {

			inputMap.put("DCPSSanctionedBudget", lArrDcpsSancBudget);
			lArrDcpsSancBudget = generateVOMap(inputMap);
			inputMap.put("DCPSSanctionedBudget", lArrDcpsSancBudget);
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

	public SanctionedBudget generateVOMap(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SanctionedBudget lArrDcpsSancBudget = new SanctionedBudget();

		try {

			new SanctionedBudget();
			Long CreatedPostId = SessionHelper.getPostId(inputMap);
			Long CreatedUserId = SessionHelper.getUserId(inputMap);
			//Date CreatedDate = DBUtility.getCurrentDateFromDB();
			
			Date CreatedDate = SessionHelper.getCurDate();

			String lStrOrgType = StringUtility.getParameter("cmbOrgType",request).trim();
			Long lLongOrgType = 0L;
			Long lLngFinYear = 0L;
			Long lLngSanctionedAmount = 0L;
			if (!(lStrOrgType.equals(""))) {
				lLongOrgType = Long.parseLong(lStrOrgType);
			}

			String lStrSchemeCode = StringUtility.getParameter("txtSchemeCode",request).trim();

			String lStrFinYear = StringUtility.getParameter("cmbFinYear",request).trim();
			if (!(lStrFinYear.equals(""))) {
				lLngFinYear = Long.parseLong(lStrFinYear);
			}

			String lStrSancDate = StringUtility.getParameter("txtSanctionedDate", request).trim();
			Date lDtSancDate = simpleDateFormat.parse(lStrSancDate.trim());

			String lStrSanctionedAmount = StringUtility.getParameter("txtSanctionedBudget", request).trim();
			
			if (!(lStrSanctionedAmount.equals(""))) {
				lLngSanctionedAmount = Long.parseLong(lStrSanctionedAmount);
			}

			String lStrBudgetType = StringUtility.getParameter("radioButtonType", request).trim();

			lArrDcpsSancBudget.setDcpsSancBudgetOrgId(lLongOrgType);
			lArrDcpsSancBudget.setDcpsSancBudgetSchemeCode(lStrSchemeCode);
			lArrDcpsSancBudget.setDcpsSancBudgetFinYear(lLngFinYear);
			lArrDcpsSancBudget.setDcpsSancBudgetDate(lDtSancDate);
			lArrDcpsSancBudget.setDcpsSancBudgetAmount(lLngSanctionedAmount);
			lArrDcpsSancBudget.setExpenditureTillDate(0L);

			lArrDcpsSancBudget.setDcpsSancBudgetType(lStrBudgetType.trim());

			lArrDcpsSancBudget.setPostId(CreatedPostId);
			lArrDcpsSancBudget.setUserId(CreatedUserId);
			lArrDcpsSancBudget.setCreatedDate(CreatedDate);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return lArrDcpsSancBudget;
	}

}
