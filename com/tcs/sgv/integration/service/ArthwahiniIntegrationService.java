package com.tcs.sgv.integration.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;

import com.tcs.sgv.common.service.CommonServiceImpl;
import com.tcs.sgv.common.service.TreasuryIntegrationService;
import com.tcs.sgv.common.valueobject.TrnIfmsArthwahiniIntegration;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.helper.BaseControllerServiceExecuter;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ArthwahiniIntegrationService {
	private final static Logger gLogger = Logger
			.getLogger(BEAMSIntegrationService.class);

	public String getVoucherDetails(String lStrXML) {
		StringBuilder lSBResultXML = new StringBuilder();
		XStream lObjXStream = new XStream(new DomDriver());
		lObjXStream.alias("collection", Map.class);
		lObjXStream.registerConverter(new ArthwahiniMapCoverter());
		TreasuryIntegrationService lObjTreasuryIntegrationService = null;
		try {
			List<Map> lLstVoucerDtls = (List<Map>) lObjXStream.fromXML(lStrXML);
			ServiceLocator servLoc = ServiceLocator.getServiceLocator();
			Map ObjectArgs = new HashedMap();
			ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
			BaseControllerServiceExecuter baseController = servLoc.getServiceExecuter();
			ObjectArgs.put("serviceNameForThr", "SAVE_ARTHWAHINI_VOUCHER_DTLS");
			ObjectArgs.put("lLstVoucerDtls", lLstVoucerDtls);
			ObjectArgs.put("serviceLocator", servLoc);
			resultObject = baseController.offlineServiceExecuter(ObjectArgs);
			lSBResultXML = (StringBuilder)ObjectArgs.get("IntegStatusCodes");
		} catch (Exception e) {
			System.out.println("Exception is :" + e);
		}
		return lSBResultXML.toString();
	}

//	public static void main(String[] args) {
//		String lStrTestXMLData = null;
//		StringBuilder lSBXml = new StringBuilder();
//		lSBXml.append("<?xml version='1.0' encoding='UTF-8' ?>");
//		lSBXml.append("<collection>");
//		lSBXml.append("<vouchDtls>");
//		lSBXml.append("<AuthorizationNo>327067157112590146</AuthorizationNo>");
//		lSBXml.append("<VoucherNo>12</VoucherNo>");
//		lSBXml.append("<VoucherDate>10-10-2012</VoucherDate>");
//		lSBXml.append("<BillType>04</BillType>");
//		lSBXml.append("<DetailHead>04</DetailHead>");
//		lSBXml.append("</vouchDtls>");
//		lSBXml.append("<vouchDtls>");
//		lSBXml.append("<AuthorizationNo>11111111</AuthorizationNo>");
//		lSBXml.append("<VoucherNo>1</VoucherNo>");
//		lSBXml.append("<VoucherDate>10-10-2012</VoucherDate>");
//		lSBXml.append("<BillType>01</BillType>");
//		lSBXml.append("<DetailHead>01</DetailHead>");
//		lSBXml.append("</vouchDtls>");
//		lSBXml.append("</collection>");
//		getVoucherDetails(lSBXml.toString());
//	}
}
