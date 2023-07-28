package com.tcs.sgv.dcps.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.RltBillgroupClassgroup;

public class DdoBillGroupClassGroupVOGenerator extends ServiceImpl implements
		VOGeneratorService {

	public ResultObject generateMap(Map inputMap) {

		//System.out.println("In VOGEN  ");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		inputMap.get("requestObj");

		try {
			//System.out.println("inside VOgenerator try");

			// =========== Code for RLT_DCPS_BILLGROUP_CLASSGROUP
			// =================
			List<RltBillgroupClassgroup> dcpsBillGroupClassGroupVOList = generateBillGroupClassGroupVOMap(inputMap);
			inputMap.put("dcpsBillGroupClassGroupVOList",
					dcpsBillGroupClassGroupVOList);

			// =========== Code for RLT_DCPS_BILLGROUP_DDOOFFICE
			// =================
			// List<RltDcpsBillgroupDdoOffice> dcpsBillGroupOfficeIdList =
			// generateBillGroupDdoOfficeMap(inputMap);
			// inputMap.put("dcpsBillGroupOfficeIdList",
			// dcpsBillGroupOfficeIdList);

			// =========== Code for MST_DCPS_BILLGROUP_DDOOFFICE
			// =================
			// inputMap.put("dcpsBillGroupVO", dcpsBillGroupVO);
			// dcpsBillGroupVO = generateBillGroupMap(inputMap);
			// inputMap.put("dcpsBillGroupVO", dcpsBillGroupVO);

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

	private List<RltBillgroupClassgroup> generateBillGroupClassGroupVOMap(
			Map inputMap) {
		// TODO Auto-generated method stub

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		LogFactory.getLog(getClass());

		RltBillgroupClassgroup dcpsBillGroupClassGroupVO = null;
		java.util.List<RltBillgroupClassgroup> dcpsBillGroupClassGroupVOList = new ArrayList<RltBillgroupClassgroup>();

		try {

			Long LangId = SessionHelper.getLangId(inputMap);
			Long LocId = SessionHelper.getLocationId(inputMap);
			Long DbId = SessionHelper.getDbId(inputMap);
			Long CreatedPostId = SessionHelper.getPostId(inputMap);
			Long CreatedUserId = SessionHelper.getUserId(inputMap);
			Long UpdatedPostId = SessionHelper.getPostId(inputMap);
			Long UpdatedUserId = SessionHelper.getUserId(inputMap);
			Date UpdatedDate = DBUtility.getCurrentDateFromDB();
			Date CreatedDate = DBUtility.getCurrentDateFromDB();

			String lStrClassGroup = StringUtility.getParameter("groups",
					request);
			String[] lArrClassGroup = lStrClassGroup.split(",");

			Integer createOrUpdateFlag = Integer.parseInt(StringUtility
					.getParameter("createOrUpdateFlag", request));
			inputMap.put("createOrUpdateFlag", createOrUpdateFlag);

			for (Integer i = 0; i < lArrClassGroup.length; i++) {
				dcpsBillGroupClassGroupVO = new RltBillgroupClassgroup();

				if (createOrUpdateFlag != 2) {

					Long lLngBillGroupId = Long.parseLong(StringUtility
							.getParameter("txtBillGroupNo", request));

					if (lLngBillGroupId != null) {
						dcpsBillGroupClassGroupVO
								.setDcpsBillGroupId(lLngBillGroupId);
					}

				}
				dcpsBillGroupClassGroupVO.setDcpsClassGroup(lArrClassGroup[i]);

				dcpsBillGroupClassGroupVO.setLangId(LangId);
				dcpsBillGroupClassGroupVO.setLocId(LocId);
				dcpsBillGroupClassGroupVO.setDbId(DbId);
				dcpsBillGroupClassGroupVO.setPostId(CreatedPostId);
				dcpsBillGroupClassGroupVO.setUserId(CreatedUserId);
				dcpsBillGroupClassGroupVO.setCreatedDate(CreatedDate);
				dcpsBillGroupClassGroupVO.setUpdatedPostId(UpdatedPostId);
				dcpsBillGroupClassGroupVO.setUpdatedUserId(UpdatedUserId);
				dcpsBillGroupClassGroupVO.setUpdatedDate(UpdatedDate);
				dcpsBillGroupClassGroupVOList.add(dcpsBillGroupClassGroupVO);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dcpsBillGroupClassGroupVOList;
	}

	/*
	 * private MstDcpsBillGroup generateBillGroupMap(Map inputMap) {
	 * 
	 * System.out.println("IN generateBillGroupMap"); HttpServletRequest request
	 * = (HttpServletRequest) inputMap .get("requestObj"); ServiceLocator
	 * servLoc = (ServiceLocator) inputMap .get("serviceLocator");
	 * LogFactory.getLog(getClass());
	 * 
	 * MstDcpsBillGroup dcpsBillGroupVO = new MstDcpsBillGroup();
	 * 
	 * try {
	 * 
	 * Long LangId = SessionHelper.getLangId(inputMap); Long LocId =
	 * SessionHelper.getLocationId(inputMap); Long DbId =
	 * SessionHelper.getDbId(inputMap); Long CreatedPostId =
	 * SessionHelper.getPostId(inputMap); Long CreatedUserId =
	 * SessionHelper.getUserId(inputMap); Long UpdatedPostId =
	 * SessionHelper.getPostId(inputMap); Long UpdatedUserId =
	 * SessionHelper.getUserId(inputMap); Date UpdatedDate =
	 * DBUtility.getCurrentDateFromDB(); Date CreatedDate =
	 * DBUtility.getCurrentDateFromDB();
	 * 
	 * Long lLngBillGroupId = Long.parseLong(StringUtility.getParameter(
	 * "txtBillGroupNo", request)); // lLngBillGroupId = //
	 * IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_bill_group", // inputMap);
	 * dcpsBillGroupVO.setDcpsDdoBillGroupId(lLngBillGroupId);
	 * 
	 * // ========================================
	 * 
	 * DdoProfileDAO DdoProfileDAOImplObj = new DdoProfileDAOImpl(
	 * MstDcpsBillGroup.class, servLoc.getSessionFactory()); String lStrDDOCode
	 * = DdoProfileDAOImplObj.getDdoCode(SessionHelper .getPostId(inputMap));
	 * dcpsBillGroupVO.setDcpsDdoCode(lStrDDOCode);
	 * 
	 * // ============================================ String lStrDescription =
	 * StringUtility.getParameter( "txtDescription", request);
	 * dcpsBillGroupVO.setDcpsDdoBillDescription(lStrDescription);
	 * 
	 * String lStrSchemeName = StringUtility.getParameter("txtSchemename",
	 * request); dcpsBillGroupVO.setDcpsDdoBillSchemeName(lStrSchemeName);
	 * 
	 * String lStrSchemeCode = StringUtility.getParameter("txtCode", request);
	 * System.out.println("lLngSchemeCode is " + lStrSchemeCode);
	 * dcpsBillGroupVO.setDcpsDdoSchemeCode(lStrSchemeCode);
	 * 
	 * String lStrPostType = StringUtility.getParameter(
	 * "RadioPermenantTempBoth", request);
	 * dcpsBillGroupVO.setDcpsDdoBillTypeOfPost(lStrPostType);
	 * 
	 * dcpsBillGroupVO.setLangId(LangId); dcpsBillGroupVO.setLocId(LocId);
	 * dcpsBillGroupVO.setDbId(DbId); dcpsBillGroupVO.setPostId(CreatedPostId);
	 * dcpsBillGroupVO.setUserId(CreatedUserId);
	 * dcpsBillGroupVO.setCreatedDate(CreatedDate);
	 * dcpsBillGroupVO.setUpdatedPostId(UpdatedPostId);
	 * dcpsBillGroupVO.setUpdatedUserId(UpdatedUserId);
	 * dcpsBillGroupVO.setUpdatedDate(UpdatedDate);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * return dcpsBillGroupVO; }
	 * 
	 * private java.util.List<RltDcpsBillgroupDdoOffice>
	 * generateBillGroupDdoOfficeMap( Map inputMap) { // TODO Auto-generated
	 * method stub System.out.println("IN generateVOMap"); HttpServletRequest
	 * request = (HttpServletRequest) inputMap .get("requestObj");
	 * LogFactory.getLog(getClass());
	 * 
	 * RltDcpsBillgroupDdoOffice dcpsBillGroupDdoOfficeVO = null;
	 * java.util.List<RltDcpsBillgroupDdoOffice> dcpsBillGroupDdoOfficeList =
	 * new ArrayList<RltDcpsBillgroupDdoOffice>();
	 * 
	 * try { System.out.println("In generateCheckListMap try ");
	 * 
	 * Long LangId = SessionHelper.getLangId(inputMap); Long LocId =
	 * SessionHelper.getLocationId(inputMap); Long DbId =
	 * SessionHelper.getDbId(inputMap); Long CreatedPostId =
	 * SessionHelper.getPostId(inputMap); Long CreatedUserId =
	 * SessionHelper.getUserId(inputMap); Long UpdatedPostId =
	 * SessionHelper.getPostId(inputMap); Long UpdatedUserId =
	 * SessionHelper.getUserId(inputMap); Date UpdatedDate =
	 * DBUtility.getCurrentDateFromDB(); Date CreatedDate =
	 * DBUtility.getCurrentDateFromDB();
	 * 
	 * String lStrCheckGroup = StringUtility.getParameter("lCheck", request);
	 * 
	 * // ========== For getting MST_DCPS_DDO_OFFICE PRIMARY KEY VALUE AS //
	 * STRING ======= System.out.println("LCheck String is=======>" +
	 * lStrCheckGroup); String[] lAryCheckGroup = lStrCheckGroup.split(",");
	 * 
	 * for (Integer i = 0; i < lAryCheckGroup.length; i++) {
	 * System.out.println("Total String Array Value is ==>" +
	 * lAryCheckGroup[i]); // Long[] lArrGroup = null; }
	 * 
	 * 
	 * for(Integer i=0;i<lAryCheckGroup.length;i++) { lArrGroup[i] =
	 * Long.parseLong(lAryCheckGroup[i]);
	 * System.out.println("Long Array Value is====>"+lArrGroup[i]); }
	 * 
	 * 
	 * // System.out.println("Total Array Value is====>"+lArrGroup.length); for
	 * (Integer i = 0; i < lAryCheckGroup.length; i++) {
	 * dcpsBillGroupDdoOfficeVO = new RltDcpsBillgroupDdoOffice();
	 * 
	 * long lLngBillClassGroupId = 01; lLngBillClassGroupId =
	 * IFMSCommonServiceImpl.getNextSeqNum( "RLT_DCPS_BILLGROUP_DDOOFFICE",
	 * inputMap); dcpsBillGroupDdoOfficeVO
	 * .setDcpsBillGroupDdoOfficeId(lLngBillClassGroupId);
	 * 
	 * Long lLngBillGroupNo = Long.parseLong(StringUtility
	 * .getParameter("txtBillGroupNo", request)); dcpsBillGroupDdoOfficeVO
	 * .setDcpsBillGroupDdoOffId(lLngBillGroupNo);
	 * System.out.println("lLngBillGroupId is " + lLngBillGroupNo);
	 * 
	 * if (lLngBillGroupNo != null) { dcpsBillGroupDdoOfficeVO
	 * .setDcpsBillGroupDdoOffId(lLngBillGroupNo); System.out
	 * .println("===========>>>>>Bill Group ID=========>>>>>>" +
	 * lLngBillGroupNo); }
	 * 
	 * dcpsBillGroupDdoOfficeVO.setDcpsDdoOfficeId(Long
	 * .parseLong(lAryCheckGroup[i]));
	 * 
	 * dcpsBillGroupDdoOfficeVO.setLangId(LangId);
	 * dcpsBillGroupDdoOfficeVO.setLocId(LocId);
	 * dcpsBillGroupDdoOfficeVO.setDbId(DbId);
	 * dcpsBillGroupDdoOfficeVO.setPostId(CreatedPostId);
	 * dcpsBillGroupDdoOfficeVO.setUserId(CreatedUserId);
	 * dcpsBillGroupDdoOfficeVO.setCreatedDate(CreatedDate);
	 * dcpsBillGroupDdoOfficeVO.setUpdatedPostId(UpdatedPostId);
	 * dcpsBillGroupDdoOfficeVO.setUpdatedUserId(UpdatedUserId);
	 * dcpsBillGroupDdoOfficeVO.setUpdatedDate(UpdatedDate);
	 * dcpsBillGroupDdoOfficeList.add(dcpsBillGroupDdoOfficeVO);
	 * 
	 * System.out.println("dcpsBillGroupDdoOfficeList is ==>" +
	 * dcpsBillGroupDdoOfficeList); }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * return dcpsBillGroupDdoOfficeList; }
	 */

}
