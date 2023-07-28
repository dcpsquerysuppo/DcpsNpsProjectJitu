/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 24, 2011		Kapil Devani								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

/**
 * Class Description - 
 *
 *
 * @author Kapil Devani
 * @version 0.1
 * @since JDK 5.0
 * Feb 24, 2011
 */
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLanguageMst;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DdoOfficeDAO;
import com.tcs.sgv.dcps.dao.DdoOfficeDAOImpl;
import com.tcs.sgv.dcps.valueobject.DdoOffice;

public class DDOOfficeVOGenerator extends ServiceImpl implements
		VOGeneratorService {

	public ResultObject generateMap(Map inputMap) {

		// TODO Auto-generated method stub

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		inputMap.get("requestObj");
		DdoOffice dcpsddoofficevo = null;
		CmnLocationMst lObjCmnLocationMst = new CmnLocationMst();

		try {

			dcpsddoofficevo = generateVOMap(inputMap);
			inputMap.put("DCPSOfficeData", dcpsddoofficevo);

			lObjCmnLocationMst = generateLocVOMap(inputMap);
			inputMap.put("CmnLocationMst", lObjCmnLocationMst);

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

	public DdoOffice generateVOMap(Map inputMap) {

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");

		DdoOffice dcpsddoofficevo = new DdoOffice();

		try {
			Long lLngTelNo1 = null;
//			Long lLngTelNo2 = null;
			Long lLngFax = null;
			Long LangId = SessionHelper.getLangId(inputMap);
			SessionHelper.getLocationId(inputMap);
			Long DbId = SessionHelper.getDbId(inputMap);
			Long CreatedPostId = SessionHelper.getPostId(inputMap);
			Long CreatedUserId = SessionHelper.getUserId(inputMap);
			Date CreatedDate = DBUtility.getCurrentDateFromDB();
			Long lLongDdoOfficeId = null;
			ServiceLocator servLoc = (ServiceLocator) inputMap
					.get("serviceLocator");
			DdoOfficeDAO ddoOfficeDAO = new DdoOfficeDAOImpl(DdoOffice.class,
					servLoc.getSessionFactory());

			Integer lIntSaveorUpdateFlag = Integer.parseInt(StringUtility.getParameter("saveOrUpdateFlag", request).trim());
			if (lIntSaveorUpdateFlag == 1) {
				if (dcpsddoofficevo == null) {
					dcpsddoofficevo = new DdoOffice();
				}
			}

			if (lIntSaveorUpdateFlag == 2) {

				lLongDdoOfficeId = Long.parseLong(StringUtility.getParameter(
						"ddoOfficeId", request).trim());
				dcpsddoofficevo = (DdoOffice) ddoOfficeDAO
						.read(lLongDdoOfficeId);

			}

			// =============================
			String dcpsDdoCode = StringUtility.getParameter("txtDdoCode",
					request).trim();
			String lStrNameOffice = StringUtility.getParameter(
					"txtNameOfOffice", request).trim();
			String lStrDdoFlag = StringUtility.getParameter("radioButtonDDO",
					request).trim();
			String lStrState = StringUtility.getParameter("cmbState", request).trim();
			String lStrDistrict = StringUtility
					.getParameter("cmbDist", request).trim();

			String lStrTaluka = StringUtility
					.getParameter("cmbTaluka", request).trim();
			String lStrTown = StringUtility.getParameter("txtTown", request).trim();
			String lStrVillage = StringUtility.getParameter("txtVillage",
					request).trim();
			String lStrAddress1 = StringUtility.getParameter("txtAddress1",
					request).trim();
			String lStrAddress2 = StringUtility.getParameter("txtAddress2",
					request).trim();
			String lStrAddress3 = StringUtility.getParameter("txtAddress3",
					request).trim();
			String lLngPinS = StringUtility.getParameter("txtPin",request).trim();
			Long lLngPin = null;
			if(lLngPinS!=null && !(lLngPinS.equals("")))
				lLngPin = Long.parseLong(lLngPinS);

			String lStrOfficeCityClass = StringUtility.getParameter(
					"cmbOfficeCityClass", request).trim();

			String lStrTelNo1 = StringUtility
					.getParameter("txtTelNo1", request).trim();
			/*if (!("".equals(lStrTelNo1))) {
				lLngTelNo1 = Long.parseLong(lStrTelNo1);
			}*/

			String lStrTelNo2 = StringUtility
					.getParameter("txtTelNo2", request).trim().toString();
			

			String lStrFax = StringUtility.getParameter("txtMobileNo", request).trim();
			if (!("".equals(lStrFax))) {
				lLngFax = Long.parseLong(lStrFax);
			}

			String lStrEmail = StringUtility.getParameter("txtEmail", request).trim();
			String lStrTriableArea = StringUtility.getParameter(
					"RadioButtonTriableArea", request).trim();
			String lStrHillyArea = StringUtility.getParameter(
					"RadioButtonHillyArea", request).trim();
			String lStrNaxaliteArea = StringUtility.getParameter(
					"RadioButtonNaxaliteArea", request).trim();
			

			if (lStrNameOffice != null && !(lStrNameOffice.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeName(lStrNameOffice);
			}
			if (lStrDdoFlag != null && !(lStrDdoFlag.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeDdoFlag(lStrDdoFlag);
			}

			if (lStrState != null && !(lStrState.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeState(lStrState);
			}
			if (lStrDistrict != null && !(lStrDistrict.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeDistrict(lStrDistrict);
			}
			if (lStrTaluka != null && !(lStrTaluka.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeTaluka(lStrTaluka);
			}
			if (lIntSaveorUpdateFlag==1) {
				dcpsddoofficevo.setDcpsDdoOfficeTown(null);
			}
			if (lIntSaveorUpdateFlag==1) {
				dcpsddoofficevo.setDcpsDdoOfficeVillage(null);
			}
			if (lIntSaveorUpdateFlag==1) {
				dcpsddoofficevo.setDcpsDdoOfficeAddress1(null);
			}

			if (lLngPin != null && !(lLngPin.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficePin(lLngPin);
			}

			if (lStrOfficeCityClass != null && !(lStrOfficeCityClass.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeCityClass(lStrOfficeCityClass);
			}
			if (lLngTelNo1 != null && !(lLngTelNo1.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeTelNo1(null);
			}
			if (lStrTelNo2 != null && !(lStrTelNo2.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeTelNo2(null);
			}
			if (lLngFax != null && !(lLngFax.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeFax(lLngFax);
			}
			if (lStrEmail != null && !(lStrEmail.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeEmail(lStrEmail);
			}
			if (lStrTriableArea != null && !(lStrTriableArea.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeTribalFlag(lStrTriableArea);
			}
			if (lStrHillyArea != null && !(lStrHillyArea.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeHillyFlag(lStrHillyArea);
			}
			if (lStrNaxaliteArea != null && !(lStrNaxaliteArea.equals(""))) {
				dcpsddoofficevo
						.setDcpsDdoOfficeNaxaliteAreaFlag(lStrNaxaliteArea);
			}
			//added by arpan
			if (lStrTown != null && !(lStrTown.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeTownNew(lStrTown);
			}
			if (lStrVillage != null && !(lStrVillage.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeVillageNew(lStrVillage);
			}
			if (lStrAddress1 != null && !(lStrAddress1.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeAddress1New(lStrAddress1);
			}
			if (lStrAddress2 != null && !(lStrAddress2.equals(""))) {
				dcpsddoofficevo
						.setDcpsDdoOfficeAddress2New(lStrAddress2);
			}
			if (lStrAddress3 != null && !(lStrAddress3.equals(""))) {
				dcpsddoofficevo
						.setDcpsDdoOfficeAddress3New(lStrAddress3);
			}
			if (lStrTelNo1 != null && !(lStrTelNo1.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeTelNo1New(lStrTelNo1);
			}
			if (lStrTelNo2 != null && !(lStrTelNo2.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeTelNo2New(lStrTelNo2);
			}
			if (lStrEmail != null && !(lStrEmail.equals(""))) {
				dcpsddoofficevo.setDcpsDdoOfficeEmailNew(lStrEmail);
			}
			//end
			dcpsddoofficevo.setDcpsDdoCode(dcpsDdoCode);
			dcpsddoofficevo.setLangId(LangId);

			dcpsddoofficevo.setDbId(DbId);
			dcpsddoofficevo.setPostId(CreatedPostId);
			dcpsddoofficevo.setUserId(CreatedUserId);
			dcpsddoofficevo.setCreatedDate(CreatedDate);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dcpsddoofficevo;
	}

	public CmnLocationMst generateLocVOMap(Map inputMap) {

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ResourceBundle lObjRsrcBndle = ResourceBundle
				.getBundle("resources/dcps/DCPSConstants");
		
		ServiceLocator servLoc = (ServiceLocator) inputMap
		.get("serviceLocator");
		
		DdoOfficeDAO ddoOfficeDAO = new DdoOfficeDAOImpl(CmnLocationMst.class,
				servLoc.getSessionFactory());

		CmnLocationMst lObjCmnLocationMstvo = new CmnLocationMst();
		Long lLongLocId = null;

		try {
			
			Long LangId = SessionHelper.getLangId(inputMap);
			Long LocId = SessionHelper.getLocationId(inputMap);
			SessionHelper.getDbId(inputMap);
			Long CreatedPostId = SessionHelper.getPostId(inputMap);
			Long CreatedUserId = SessionHelper.getUserId(inputMap);
			Date CreatedDate = DBUtility.getCurrentDateFromDB();

			StringUtility.getParameter("txtDdoCode", request);
			String lStrNameOffice = StringUtility.getParameter(
					"txtNameOfOffice", request).trim();
			StringUtility.getParameter("radioButtonDDO", request);
			String lStrState = StringUtility.getParameter("cmbState", request).trim();
			String lStrDistrict = StringUtility
					.getParameter("cmbDist", request).trim();

			String lStrTaluka = StringUtility
					.getParameter("cmbTaluka", request).trim();
			StringUtility.getParameter("txtTown", request).trim();
			StringUtility.getParameter("txtVillage", request).trim();
			String lStrAddress1 = StringUtility.getParameter("txtAddress1",
					request).trim();
			String lLngPinS = StringUtility.getParameter("txtPin",request).trim();
			Long lLngPin = null;
			if(lLngPinS!=null && !(lLngPinS.equals("")))
				lLngPin = Long.parseLong(lLngPinS);

			StringUtility.getParameter("cmbOfficeCityClass", request);

			String lStrTelNo2 = StringUtility
					.getParameter("txtTelNo2", request).toString();
			String lStrFax = StringUtility.getParameter("txtMobileNo", request).trim();
			
			
			if (!(lStrFax.equals(""))) {
				Long.parseLong(lStrFax);
			}
			StringUtility.getParameter("txtEmail", request);
			StringUtility.getParameter("RadioButtonTriableArea", request);
			StringUtility.getParameter("RadioButtonHillyArea", request);
			StringUtility.getParameter("RadioButtonNaxaliteArea", request);

			if (lStrNameOffice != null && !(lStrNameOffice.equals(""))) {

				lObjCmnLocationMstvo.setLocName(lStrNameOffice);
				lObjCmnLocationMstvo.setLocShortName(lStrNameOffice.substring(
						0, 3).toUpperCase());
			}

			if (lStrState != null && !(lStrState.equals(""))) {

				lObjCmnLocationMstvo.setLocStateId(Long.parseLong(lStrState));
			}
			if (lStrDistrict != null && !(lStrDistrict.equals(""))) {

				lObjCmnLocationMstvo.setLocDistrictId(Long
						.parseLong(lStrDistrict));
			}
			if (lStrTaluka != null && !(lStrTaluka.equals(""))) {

				lObjCmnLocationMstvo.setLocTalukaId(Long.parseLong(lStrTaluka));
			}

			if (lStrAddress1 != null && !(lStrAddress1.equals(""))) {
				lObjCmnLocationMstvo.setLocAddr1(lStrAddress1);
			}

			if (lLngPin != null && !(lLngPin.equals(""))) {

				lObjCmnLocationMstvo.setLocPin(lLngPin.toString());
			}

			CmnLanguageMst lObjCmnLanguageMst = new CmnLanguageMst();
			lObjCmnLanguageMst.setLangId(LangId);
			lObjCmnLocationMstvo.setCmnLanguageMst(lObjCmnLanguageMst);

			lObjCmnLocationMstvo.setParentLocId(LocId);

			Long lLngDepartmentId = Long.valueOf(lObjRsrcBndle
					.getString("DCPS.DDODEPARTMENTID"));
			Long lLngLookupId = Long.valueOf(lObjRsrcBndle
					.getString("DCPS.DDOLOOKUPID"));

			CmnLookupMst lObjCmnLookupMst = new CmnLookupMst();
			lObjCmnLookupMst.setLookupId(lLngLookupId);

			lObjCmnLocationMstvo.setCmnLookupMst(lObjCmnLookupMst);
			lObjCmnLocationMstvo.setDepartmentId(lLngDepartmentId);
			lObjCmnLocationMstvo.setStartDate(CreatedDate);
			lObjCmnLocationMstvo.setActivateFlag(1L);
			lObjCmnLocationMstvo.setCreatedByPost(CreatedPostId);
			lObjCmnLocationMstvo.setCreatedBy(CreatedUserId);
			lObjCmnLocationMstvo.setCreatedDate(CreatedDate);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return lObjCmnLocationMstvo;
	}

}
