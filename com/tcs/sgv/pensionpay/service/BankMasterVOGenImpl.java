package com.tcs.sgv.pensionpay.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.PropertyValueException;

import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.MstBank;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.valueobject.ResultObject;


public class BankMasterVOGenImpl extends ServiceImpl {

	Log logger = LogFactory.getLog(getClass());
	ResultObject retObj = new ResultObject(ErrorConstants.SUCCESS);

	public ResultObject generateMapForInsertBankMaster(Map objectArgs) {

		try {
			logger.info("BankMasterVOGenImpl generateMapForInsertBankMaster Called");

			HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
			MstBank bankMstVO;

			String editMode = StringUtility.getParameter("edit", request);
			long bankId = 0;

			if (editMode.equalsIgnoreCase("N")) {

				bankMstVO = new MstBank();
				objectArgs.put("edit", "N");
			} else {
				String BankId = StringUtility.getParameter("bankId", request);
				if (BankId != null && !BankId.equals("")) {
					bankId = Long.parseLong(BankId);
				}
				bankMstVO = new MstBank();
				bankMstVO.setBankId(bankId);
				bankMstVO.setBankCode(Long.toString(bankId));
				objectArgs.put("edit", "Y");
			}

			String bankName = StringUtility.getParameter("bankName", request);
			bankMstVO.setBankName(bankName);

			// String bankCode = StringUtility.getParameter("bankCode",
			// request);

			// logger.info("bankCode***********" + bankCode);

			// String micrCode = StringUtility.getParameter("txtMicrCode",
			// request);
			// bankMstVO.setMicrCode(micrCode);

			// logger.info("micrCode***********" + micrCode);

			String bankAddress = StringUtility.getParameter("txtBankhAdd", request);
			bankMstVO.setBankAddress(bankAddress);

			logger.info("bankAddress***********" + bankAddress);

			long activateFlag = 1;
			bankMstVO.setActivateFlag(activateFlag);

			logger.info("bankAddress***********" + bankAddress);

			logger.info(" ****************************BankMasterVO " + bankMstVO);
			objectArgs.put("bankMst", bankMstVO);
			retObj.setResultValue(objectArgs);
			retObj.setResultCode(ErrorConstants.SUCCESS);
		} catch (PropertyValueException pe) {
			logger.info("Exception in generateMapForInsertBankMaster-----" + pe);
			logger.info("setViewName-->errorPage");
			Map result = new HashMap();
			result.put("MESSAGECODE", 300004);
			retObj.setResultValue(result);
			retObj.setThrowable(pe);
			retObj.setResultCode(-1);
			retObj.setViewName("errorPage");
			//pe.printStackTrace();

		} catch (Exception e) {
			ResultObject retObj = new ResultObject(ErrorConstants.ERROR);
			logger.info("Exception in generateMapForInsertBankMaster-----" + e);
			logger.info("setViewName-->errorPage");
			Map result = new HashMap();
			result.put("MESSAGECODE", 300004);
			retObj.setResultValue(result);
			retObj.setThrowable(e);
			retObj.setResultCode(-1);
			retObj.setViewName("errorPage");
			// e.printStackTrace();

		}
		return retObj;
	}
}
