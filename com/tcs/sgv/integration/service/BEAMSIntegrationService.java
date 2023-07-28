/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Sep 7, 2012		Vrajesh Raval								
 *******************************************************************************
 */
package com.tcs.sgv.integration.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;

import com.tcs.sgv.common.service.BiometricAttendanceServiceImpl;
import com.tcs.sgv.common.service.CommonServiceImpl;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.helper.BaseControllerServiceExecuter;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


/**
 * Class Description -
 * 
 * 
 * @author Shripal Soni
 * @version 0.1
 * @since JDK 5.0 Sep 7, 2012
 */
public class BEAMSIntegrationService {

	private final static Logger gLogger = Logger.getLogger(BEAMSIntegrationService.class);

	public String rejectBill(String lStrXML) {

		String lStrPayBillId = null;
		String lStrDtlsHead = null;
		String lStrAuthNo = null;
		String lStrBeamsBillStatus = null;
		String lStrVoucherNo = null;
		String lStrVoucherDate = null;
		String lStrStatusCodes = null;
		String lStrDDOCode = null;
		String lStrBillType = null;
		Integer lIntVoucherNo = null;
		Long lLngPayBillId = null;
		Date lDtVoucher = null;
		boolean isError = false;
		StringBuilder lSBStatusCode = new StringBuilder();
		SimpleDateFormat lSdf = new SimpleDateFormat("yyyy-MM-dd");
		String lTmpXML = "";
		XStream lObjXStream = new XStream(new DomDriver());
		lObjXStream.alias("collection", Map.class);
		lObjXStream.registerConverter(new MapConverter());
		Map lMapXMLData = (Map) lObjXStream.fromXML(lStrXML);
		System.out.println("XML Obj is :" + lMapXMLData);
		lStrPayBillId = (String) lMapXMLData.get("PaybillId");
		lStrAuthNo = (String) lMapXMLData.get("AuthorizationNo");
		lStrDtlsHead = (String) lMapXMLData.get("DetailHead");
		lStrDDOCode = (String) lMapXMLData.get("DDOCode");
		lStrBillType = (String) lMapXMLData.get("BillType");
		// lStrVoucherNo = (String) lMapXMLData.get("VoucherNo");
		// lStrVoucherDate = (String) lMapXMLData.get("VoucherDate");
		// lStrBeamsBillStatus = (String) lMapXMLData.get("BillStatusCode");
		try {
			gLogger.info("Mubeen:1		BEAMSIntegrationService WebService	");
			//System.out.println("Mubeen:1		BEAMSIntegrationService WebService	");
			
			if (lStrPayBillId != null && lStrPayBillId.length() > 0) {
				lStrPayBillId = lStrPayBillId.trim();
				if (CommonServiceImpl.isValidLongNumber(lStrPayBillId)) {
					lLngPayBillId = Long.parseLong(lStrPayBillId);
				} else {
					lSBStatusCode.append("01|");
					isError = true;
				}
			} else {
				lSBStatusCode.append("01|");
				isError = true;
			}

			if (lStrDtlsHead != null && lStrDtlsHead.trim().length() > 0) {
				lStrDtlsHead = lStrDtlsHead.trim();
				if ((!"01".equals(lStrDtlsHead) && !"04".equals(lStrDtlsHead)) && (!"50".equals(lStrDtlsHead) && !"55".equals(lStrDtlsHead))) {
					lSBStatusCode.append("02|");
					isError = true;
				}
			} else {
				lSBStatusCode.append("02|");
				isError = true;
			}

			if ((lStrAuthNo != null && lStrAuthNo.trim().length() > 0)) {
				lStrAuthNo = lStrAuthNo.trim();
			} else {
				lSBStatusCode.append("03|");
				isError = true;
			}

			if ((lStrDDOCode != null && lStrDDOCode.trim().length() > 0)) {
				lStrDDOCode = lStrDDOCode.trim();
			} else {
				lSBStatusCode.append("04|");
				isError = true;
			}

			if ((lStrBillType != null && lStrBillType.trim().length() > 0)) {
				lStrBillType = lStrBillType.trim();
			} else {
				lSBStatusCode.append("05|");
				isError = true;
			}
			/*
			 * if ((lStrBeamsBillStatus != null &&
			 * lStrBeamsBillStatus.trim().length() > 0)) { lStrBeamsBillStatus =
			 * lStrBeamsBillStatus.trim(); if (!"0".equals(lStrBeamsBillStatus)
			 * && !"1".equals(lStrBeamsBillStatus)) {
			 * lSBStatusCode.append("04|"); isError = true; } } else {
			 * lSBStatusCode.append("04|"); isError = true; }
			 * 
			 * 
			 * if ("1".equals(lStrBeamsBillStatus)) { if (lStrVoucherNo != null
			 * && lStrVoucherNo.length() > 0) { lStrVoucherNo =
			 * lStrVoucherNo.trim(); if (isValidIntNumber(lStrVoucherNo)) {
			 * lIntVoucherNo = Integer.parseInt(lStrVoucherNo); } else {
			 * lSBStatusCode.append("05|"); isError = true; } } else {
			 * lSBStatusCode.append("05|"); isError = true; } if
			 * (lStrVoucherDate != null && lStrVoucherDate.length() > 0) {
			 * lStrVoucherDate = lStrVoucherDate.trim(); if
			 * (isValidDate(lStrVoucherDate)) { lDtVoucher =
			 * lSdf.parse(lStrVoucherDate); } else {
			 * lSBStatusCode.append("06|"); isError = true; } } else {
			 * lSBStatusCode.append("06|"); isError = true; } }
			 */
			gLogger.info("isError is :" + isError);
			if (!isError) {
				gLogger.info("inside to call method to save detials");
				System.out.println("Mubeen:1		BEAMSIntegrationService WebService	");
				
				
				ServiceLocator servLoc = ServiceLocator.getServiceLocator();
				Map ObjectArgs = new HashedMap();
				ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
				BaseControllerServiceExecuter baseController = servLoc.getServiceExecuter();
				ObjectArgs.put("serviceNameForThr", "SAVE_BEAMS_VOUCHER_DTLS");
				ObjectArgs.put("billNo", lLngPayBillId);
				ObjectArgs.put("authNo", lStrAuthNo);
				ObjectArgs.put("ddoCode", lStrDDOCode);
				ObjectArgs.put("billType", lStrBillType);
				ObjectArgs.put("detailHead", lStrDtlsHead);
				// ObjectArgs.put("voucherNo", lIntVoucherNo);
				// ObjectArgs.put("voucherDate", lDtVoucher);
				ObjectArgs.put("beamsBillStatus", lStrBeamsBillStatus);
				ObjectArgs.put("serviceLocator", servLoc);
				resultObject = baseController.offlineServiceExecuter(ObjectArgs);

				System.out.println("Mubeen: Return from Offline Service Executer");
				
				if (ObjectArgs.get("isBillNoInvalid") != null && ((Boolean) ObjectArgs.get("isBillNoInvalid"))) {
					lSBStatusCode.append("01|");
				}
				if (ObjectArgs.get("isAuthNoInvalid") != null && ((Boolean) ObjectArgs.get("isAuthNoInvalid"))) {
					lSBStatusCode.append("03|");
				}
				if (ObjectArgs.get("isBillTypeInvalid") != null && ((Boolean) ObjectArgs.get("isBillTypeInvalid"))) {
					lSBStatusCode.append("05|");
				}
				if (ObjectArgs.get("isRecordFound") != null && !((Boolean) ObjectArgs.get("isRecordFound"))) {
					lSBStatusCode.append("06|");
				}
				if (ObjectArgs.get("BeamsBillStatus") != null) {
					if ("1".equals(ObjectArgs.get("BeamsBillStatus"))) {
						lSBStatusCode.append("08|");
					} else if ("0".equals(ObjectArgs.get("BeamsBillStatus"))) {
						lSBStatusCode.append("09|");
					}
				}
				gLogger.info("result object is :" + resultObject);
				System.out.println("Mubeen: Status Code is: "+lSBStatusCode.toString());
				if (ObjectArgs.get("isError") != null && !((Boolean) ObjectArgs.get("isError"))) {
					lSBStatusCode.append("00|");
				}
			}
		} catch (Exception e) {
			gLogger.error("Exception is e:" + e);
			lSBStatusCode.append("07|");
		}
		return lSBStatusCode.toString();
	}
}
