/**
 * 
 */
package com.tcs.sgv.common.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.BillMovementDAOImpl;
import com.tcs.sgv.common.dao.BptmCommonServicesDAO;
import com.tcs.sgv.common.dao.BptmCommonServicesDAOImpl;
import com.tcs.sgv.common.dao.CmnDistrictMstDAO;
import com.tcs.sgv.common.dao.CmnDistrictMstDAOImpl;
import com.tcs.sgv.common.dao.CmnLocationMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLookupMstDAOImpl;
import com.tcs.sgv.common.dao.LocationDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.helper.WorkFlowHelper;
import com.tcs.sgv.common.valueobject.CmnDistrictMst;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.common.valueobject.TrnBillMvmnt;
import com.tcs.sgv.common.valueobject.TrnBillRegister;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.wf.valueobject.WorkFlowVO;


/**
 * BptmCommonServiceImpl This class is used for common methods which are
 * frequently used. It contains method for inserting remarks, billDetails,
 * budgetHead, ReceiptDetails, movement, bill-challan mapping. It also contains
 * method for getting intimation, department List, Lookup Values, nextSeqNum,
 * Bill Control No. Date of Creation : 7th July 2007 Author : Hiral Shah
 * Revision History ===================== Hiral 23-Oct-2007 For making changes
 * for code formating
 */
public class BptmCommonServiceImpl extends ServiceImpl {

	static Log logger = LogFactory.getLog(BptmCommonServiceImpl.class);

	static HashMap hMapBillControl = new HashMap();

	static ResourceBundle bundleConst = ResourceBundle.getBundle("resources/billproc/BillprocConstants");

	/**
	 * Method to get all User wise Intimation
	 * 
	 * @param Map
	 *            : objectArgs
	 * @return ResultObject
	 */
	public static List getMyIntimation(Map objectArgs) {

		WorkFlowVO workFlowVO = new WorkFlowVO();

		ServiceLocator serv = (ServiceLocator) objectArgs.get("serviceLocator");
		Connection conn = serv.getSessionFactory().getCurrentSession().connection();
		workFlowVO.setAppMap(objectArgs);
		workFlowVO.setCrtEmpId(SessionHelper.getUserId(objectArgs).toString());
		workFlowVO.setCrtPost(SessionHelper.getPostId(objectArgs).toString());
		workFlowVO.setCrtUsr(SessionHelper.getUserId(objectArgs).toString());
		workFlowVO.setConnection(conn);

		List lObjListIntimation = new ArrayList();
		try {
			lObjListIntimation = WorkFlowHelper.getAllIntimationByPostId(SessionHelper.getPostId(objectArgs).toString(), objectArgs);
		} catch (Exception e) {
			logger.error("Error while executing getMyIntimation of BptmCommonServiceImpl", e);
		}
		return lObjListIntimation;
	}

	/**
	 * Method to get all Departments of Sachivalay.
	 * 
	 * @param Map
	 *            : inputMap
	 * @return List
	 */
	public static List getDeptList(Map inputMap) throws Exception {

		ResourceBundle lObjRsrcBndle = ResourceBundle.getBundle("resources/billproc/BillprocConstants");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		LocationDAOImpl lObjLocDAOImpl = new LocationDAOImpl(CmnLocationMst.class, serv.getSessionFactory());

		List deptList = lObjLocDAOImpl.getDeptLocation(lObjRsrcBndle.getObject("CMN.SachivalayDept").toString(), SessionHelper.getLocale(request));
		return deptList;
	}

	/**
	 * This method will generate a sequence number in an auto-incremental mode.
	 * 
	 * @param String
	 *            : tableName
	 * @param Map
	 *            : inputMap
	 * @return Long
	 */

	/*
	 * public static List getLookupValues(String lStrLookupName, Long
	 * lLngLangId, Map inputMap) { ServiceLocator serv = (ServiceLocator)
	 * inputMap.get("serviceLocator"); CmnLookupMstDAOImpl lObjCmnLookupDAOImpl
	 * = new CmnLookupMstDAOImpl( CmnLookupMst.class, serv.getSessionFactory());
	 * List lLstLookupValues = lObjCmnLookupDAOImpl
	 * .getAllChildrenByLookUpNameAndLang(lStrLookupName, lLngLangId); return
	 * lLstLookupValues; }
	 */

	/**
	 * Method to get all Bill Codes.
	 * 
	 * @param Map
	 *            : inputMap
	 * @return List
	 */
	public static List getBillCodeList(Map inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResourceBundle lObjRsrcBndle = ResourceBundle.getBundle("resources/billproc/BillprocConstants");
		CmnLookupMstDAOImpl lObjCmnLookupDAOImpl = new CmnLookupMstDAOImpl(CmnLookupMst.class, serv.getSessionFactory());

		List lLstBillCode = lObjCmnLookupDAOImpl.getAllChildren(lObjRsrcBndle.getObject("CMN.BillCode").toString());
		return lLstBillCode;
	}

	/**
	 * Method to get List of available Bill Categories(Regular, TC and Nil).
	 * 
	 * @param Map
	 *            : inputMap
	 * @return List
	 */
	public static List getBillCategoryList(Map inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResourceBundle lObjRsrcBndle = ResourceBundle.getBundle("resources/billproc/BillprocConstants");
		CmnLookupMstDAOImpl lObjCmnLookupDAOImpl = new CmnLookupMstDAOImpl(CmnLookupMst.class, serv.getSessionFactory());

		List lLstTCBill = lObjCmnLookupDAOImpl.getAllChildren(lObjRsrcBndle.getObject("CMN.TcBillType").toString());
		return lLstTCBill;
	}

	/**
	 * Method to get List of available Bill Categories(Regular, TC and Nil).
	 * 
	 * @param Map
	 *            : inputMap
	 * @return List
	 */
	public static String getExpType(Map inputMap, String lStrExpType) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResourceBundle lObjRsrcBndle = ResourceBundle.getBundle("resources/billproc/BillprocConstants");
		CmnLookupMstDAOImpl lObjCmnLookupDAOImpl = new CmnLookupMstDAOImpl(CmnLookupMst.class, serv.getSessionFactory());
		String lStrExpTypeId = null;
		List lLstTCBill = lObjCmnLookupDAOImpl.getAllChildrenByLookUpNameAndLang(lObjRsrcBndle.getObject("CMN.Exp_type").toString(), SessionHelper.getLangId(inputMap));
		// List lLstTCBill =
		// lObjCmnLookupDAOImpl.getAllChildren(lObjRsrcBndle.getObject("CMN.Exp_type").toString());

		for (int i = 0; i < lLstTCBill.size(); i++) {
			CmnLookupMst lObjLookup = (CmnLookupMst) lLstTCBill.get(i);
			if (lObjLookup.getLookupName().equalsIgnoreCase(lStrExpType)) {
				lStrExpTypeId = Long.toString(lObjLookup.getLookupId());
			}
		}
		return lStrExpTypeId;
	}

	/**
	 * Method to get List of GO/NGOs.
	 * 
	 * @param Map
	 *            : inputMap
	 * @return List
	 */
	public static List getGoNgoList(Map inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResourceBundle lObjRsrcBndle = ResourceBundle.getBundle("resources/billproc/BillprocConstants");
		CmnLookupMstDAOImpl lObjCmnLookupDAOImpl = new CmnLookupMstDAOImpl(CmnLookupMst.class, serv.getSessionFactory());

		List lLstGoNgo = lObjCmnLookupDAOImpl.getAllChildren(lObjRsrcBndle.getObject("CMN.GoNgo").toString());
		return lLstGoNgo;
	}

	/**
	 * Method to get List of Budget Type(Plan/Non-Plan)
	 * 
	 * @param Map
	 *            : inputMap
	 * @return List
	 */
	public static List getBudgetTypeList(Map inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResourceBundle lObjRsrcBndle = ResourceBundle.getBundle("resources/billproc/BillprocConstants");
		CmnLookupMstDAOImpl lObjCmnLookupDAOImpl = new CmnLookupMstDAOImpl(CmnLookupMst.class, serv.getSessionFactory());

		List lLstBudType = lObjCmnLookupDAOImpl.getAllChildren(lObjRsrcBndle.getObject("CMN.BudgetType").toString());
		return lLstBudType;
	}

	public static void insertMovement(TrnBillMvmnt lObjBillMvmntVO, Map inputMap) throws Exception {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		BillMovementDAOImpl mvmntDAO = new BillMovementDAOImpl(TrnBillMvmnt.class, serv.getSessionFactory());
		Long lLngBillMvmntId = IFMSCommonServiceImpl.getNextSeqNum("trn_bill_mvmnt", inputMap);
		lObjBillMvmntVO.setBillMvmtId(lLngBillMvmntId);
		mvmntDAO.create(lObjBillMvmntVO);
	}

	/**
	 * Method to insert Remarks details in "trn_bill_remarks" (Also generates
	 * and sets Primary Key in sequence)
	 * 
	 * @param TrnBillRemarks
	 *            : lObjRemarksVO
	 * @param Map
	 *            : inputMap
	 * @return void
	 */
	/*
	 * public static void insertRemarks(TrnBillRemarks lObjRemarksVO, Map
	 * inputMap) throws Exception{ ServiceLocator serv = (ServiceLocator)
	 * inputMap.get("serviceLocator"); String lStrRemarks =
	 * lObjRemarksVO.getRemarks(); // if(lStrRemarks!=null &&
	 * lStrRemarks.length()>0) { RemarksDAOImpl lObjRemarksDAOImpl = new
	 * RemarksDAOImpl( TrnBillRemarks.class, serv.getSessionFactory()); Long
	 * lLngRemarksId = IFMSCommonServiceImpl.getNextSeqNum("trn_bill_remarks",
	 * inputMap); lObjRemarksVO.setRmrksId(lLngRemarksId);
	 * lObjRemarksDAOImpl.create(lObjRemarksVO); } }
	 */

	/**
	 * Method to insert Budget Head related Details in "trn_bill_budhead_dtls"
	 * (Also generates and sets Primary Key in sequence)
	 * 
	 * @param TrnBillRegister
	 *            : lObjTrnBillRegVO
	 * @param Map
	 *            : inputMap
	 * @return void
	 */
	/*
	 * public static void insertBudHeadDtls(TrnBillBudheadDtls lObjBudHeadVO,
	 * Map inputMap) throws Exception{ ServiceLocator serv = (ServiceLocator)
	 * inputMap.get("serviceLocator"); BdgtHeadDtlsDAOImpl
	 * lObjBdgtHeadDtlsDAOImpl = new BdgtHeadDtlsDAOImpl(
	 * TrnBillBudheadDtls.class, serv.getSessionFactory()); Long lLngBudHeadId =
	 * IFMSCommonServiceImpl.getNextSeqNum("trn_bill_budhead_dtls", inputMap);
	 * lObjBudHeadVO.setBillBudId(lLngBudHeadId);
	 * lObjBdgtHeadDtlsDAOImpl.create(lObjBudHeadVO); }
	 */

	/**
	 * This method will generate a sequence number in an auto-incremental mode.
	 * 
	 * @param String
	 *            : tableName
	 * @param Map
	 *            : inputMap
	 * @return Long
	 */
	/*
	 * public synchronized static Long getNextSeqNum(String lStrTableName, Map
	 * inputMap) { ResultObject lResObj = new
	 * ResultObject(ErrorConstants.SUCCESS, "FAIL"); try { if (lResObj != null)
	 * { Long lId = IDGenerateDelegate .getNextId(lStrTableName, inputMap);
	 * return (long) lId; } } catch (Exception e) { e.printStackTrace(); }
	 * return (long) -1; }
	 */

	/*
	 * Revised method to get bill control number
	 */
	public synchronized static String getBillControlNumber(Map inputMap) {

		String lStrBillControlNumber = "";
		String lStrDDONum = "";
		String lStrDistCode = "";
		String lStrDate = "";

		String lStrDDOCode = (String) inputMap.get("DDOCode");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		Long langId = SessionHelper.getLangId(inputMap);
		Long locId = SessionHelper.getLocationId(inputMap);
		Long dbId = SessionHelper.getDbId(inputMap);
		String lStrFinYrId = SessionHelper.getFinYrId(inputMap).toString();

		DDOInfoServiceImpl lObjDDOInfoSrvcImpl = new DDOInfoServiceImpl();

		List orgDDOMstList = lObjDDOInfoSrvcImpl.getDDOInfo(lStrDDOCode, langId, dbId, serv);

		if (orgDDOMstList != null && orgDDOMstList.size() > 0) {
			OrgDdoMst orgDDOMst = (OrgDdoMst) orgDDOMstList.get(0);
			lStrDDONum = orgDDOMst.getDdoNo().toString(); // DDO NUMBER
		}

		CmnLocationMstDaoImpl locDAO = new CmnLocationMstDaoImpl(CmnLocationMst.class, serv.getSessionFactory());
		CmnDistrictMstDAOImpl distDAO = new CmnDistrictMstDAOImpl(CmnDistrictMst.class, serv.getSessionFactory());
		CmnDistrictMst districtVO = null;

		CmnLocationMst locVO = locDAO.getLocationVO(locId);

		if (locVO.getLocDistrictId() != null && !locVO.getLocDistrictId().equals("")) {
			districtVO = distDAO.read(locVO.getLocDistrictId());
			lStrDistCode = districtVO.getDistrictCode(); // DISTRICT CODE
		} else {
			lStrDistCode = locVO.getLocShortName(); // DISTRICT CODE
		}

		NumberFormat f = new DecimalFormat("#00");
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(new Date(System.currentTimeMillis()));

		lStrDate = f.format(gc.get(gc.YEAR)).substring(2) + f.format(gc.get(gc.MONTH) + 1); // DATE

		Long ctrlNo = 1L; // CONTROL NUMBER

		if (!hMapBillControl.containsKey(lStrDDOCode + "_" + lStrFinYrId)) {
			BptmCommonServicesDAO bptmDAO = new BptmCommonServicesDAOImpl(TrnBillRegister.class, serv.getSessionFactory());
			Long lLongTmpBillControlNum = bptmDAO.getBillCtrlNo(inputMap);
			if (lLongTmpBillControlNum != null && !lLongTmpBillControlNum.equals("")) {
				// ctrlNo =
				// Integer.parseInt(lLongTmpBillControlNum.substring(lLongTmpBillControlNum.lastIndexOf("/")
				// + 1)) + 1; // CONTROL NUMBER
				ctrlNo = lLongTmpBillControlNum;
			}
		} else {
			ctrlNo = Long.parseLong(hMapBillControl.get(lStrDDOCode + "_" + lStrFinYrId).toString()) + 1; // CONTROL
			// NUMBER
		}

		hMapBillControl.put(lStrDDOCode + "_" + lStrFinYrId, ctrlNo);

		lStrBillControlNumber = lStrDDONum + "/" + lStrDistCode + "/" + lStrDate + "/" + ctrlNo.toString();

		logger.info(">>>>>>>>>>>>>>>>> Generated Bill Control Number : " + lStrBillControlNumber + " <<<<<<<<<<<<<<<<<<<<<");

		return lStrBillControlNumber;

	}

	public synchronized static String getBillControlNo(Map inputMap) throws Exception {

		String lStrBillControlNumber = "";
		// String lStrDDONum = "";
		// String lStrDistCode = "";
		String lStrDate = "";
		String lStrLocName = "";
		// String lStrDDOCode = (String) inputMap.get("DDOCode");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SessionHelper.getLangId(inputMap);
		Long locId = SessionHelper.getLocationId(inputMap);
		SessionHelper.getDbId(inputMap);
		SessionHelper.getFinYrId(inputMap).toString();

		new DDOInfoServiceImpl();

		// List orgDDOMstList = lObjDDOInfoSrvcImpl.getDDOInfo(lStrDDOCode,
		// langId, dbId, serv);

		// if (orgDDOMstList != null && orgDDOMstList.size() > 0) {
		// OrgDdoMst orgDDOMst = (OrgDdoMst) orgDDOMstList.get(0);
		// lStrDDONum = orgDDOMst.getDdoNo().toString(); // DDO NUMBER
		// }

		CmnLocationMstDaoImpl locDAO = new CmnLocationMstDaoImpl(CmnLocationMst.class, serv.getSessionFactory());
		new CmnDistrictMstDAOImpl(CmnDistrictMst.class, serv.getSessionFactory());
		CmnLocationMst locVO = locDAO.getLocationVO(locId);

		// if (locVO.getLocDistrictId() != null &&
		// !locVO.getLocDistrictId().equals("")) {
		// districtVO = distDAO.read(locVO.getLocDistrictId());
		// lStrDistCode = districtVO.getDistrictCode(); // DISTRICT CODE
		// } else {
		// lStrDistCode = locVO.getLocShortName(); // DISTRICT CODE
		// }
		lStrLocName = locVO.getLocShortName();
		NumberFormat f = new DecimalFormat("#00");
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(new Date(System.currentTimeMillis()));

		// lStrDate = f.format(gc.get(gc.YEAR)).substring(2) +
		// f.format(gc.get(gc.MONTH) + 1); // DATE

		lStrDate = f.format(gc.get(gc.MONTH) + 1) + f.format(gc.get(gc.YEAR));

		// if (!hMapBillControl.containsKey(lStrLocName + "_" + lStrFinYrId))
		// {
		BptmCommonServicesDAO bptmDAO = new BptmCommonServicesDAOImpl(TrnBillRegister.class, serv.getSessionFactory());
		Long lLongTmpBillControlNum = bptmDAO.getBillControlNo(inputMap);
		// if (lLongTmpBillControlNum != null &&
		// !lLongTmpBillControlNum.equals("")) {
		// ctrlNo =
		// Integer.parseInt(lLongTmpBillControlNum.substring(lLongTmpBillControlNum.lastIndexOf("/")
		// + 1)) + 1; // CONTROL NUMBER
		// ctrlNo = lLongTmpBillControlNum;
		// }
		// } else {
		// ctrlNo = Long.parseLong(hMapBillControl.get(lStrDDOCode + "_" +
		// lStrFinYrId).toString()) + 1; // CONTROL
		// NUMBER
		// }

		// hMapBillControl.put(lStrDDOCode + "_" + lStrFinYrId, ctrlNo);

		lStrBillControlNumber = lStrLocName + "/" + lStrDate + "/" + lLongTmpBillControlNum.toString();

		logger.info(">>>>>>>>>>>>>>>>> Generated Bill Control Number : " + lStrBillControlNumber + " <<<<<<<<<<<<<<<<<<<<<");

		return lStrBillControlNumber;

	}

	/*
	 * public synchronized static String getBillControlNumber(Map inputMap) {
	 * ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
	 * HttpServletRequest request = (HttpServletRequest) inputMap
	 * .get("requestObj"); DDOInfoServiceImpl lObjDDOInfoSrvcImpl = new
	 * DDOInfoServiceImpl(); String lStrDDOCode = (String)
	 * inputMap.get("DDOCode"); String hMapData = null; String billCntrlNum =
	 * ""; String lStrDDONum = ""; Long langId =
	 * SessionHelper.getLangId(inputMap); Long locId =
	 * SessionHelper.getLocationId(inputMap); Long dbId =
	 * SessionHelper.getDbId(inputMap); List orgDDOMstList =
	 * lObjDDOInfoSrvcImpl.getDDOInfo(lStrDDOCode, langId, dbId, serv); if
	 * (orgDDOMstList != null && orgDDOMstList.size() > 0) { OrgDdoMst orgDDOMst
	 * = (OrgDdoMst) orgDDOMstList.get(0); lStrDDONum = orgDDOMst.getDdoNo(); }
	 * BptmCommonServicesDAO bptmDAO = new BptmCommonServicesDAOImpl(
	 * TrnBillRegister.class, serv.getSessionFactory()); CmnLocationMstDaoImpl
	 * locDAO = new CmnLocationMstDaoImpl( CmnLocationMst.class,
	 * serv.getSessionFactory()); CmnLocationMst locVO =
	 * locDAO.getLocationVO(locId); CmnDistrictMstDAOImpl distDAO = new
	 * CmnDistrictMstDAOImpl(CmnDistrictMst.class,serv.getSessionFactory());
	 * CmnDistrictMst districtVO = null; String distCode = "";
	 * if(locVO.getLocDistrictId() != null &&
	 * !locVO.getLocDistrictId().equals("")) { districtVO =
	 * distDAO.read(locVO.getLocDistrictId()); distCode =
	 * districtVO.getDistrictCode(); } else { distCode =
	 * locVO.getLocShortName(); } GregorianCalendar gc = new
	 * GregorianCalendar(); gc.setTime(new Date(System.currentTimeMillis()));
	 * int returnData = 0; if (hMapBillControl.containsKey(lStrDDOCode)) {
	 * returnData = Integer.parseInt(hMapBillControl.get(lStrDDOCode)
	 * .toString()); returnData++; NumberFormat f = new DecimalFormat("#00");
	 * billCntrlNum = lStrDDONum + "/" + distCode + "/" +
	 * f.format(gc.get(gc.YEAR)).substring(2) + f.format(gc.get(gc.MONTH) + 1) +
	 * "/" + returnData; hMapBillControl.put(lStrDDOCode, returnData); } else {
	 * String BillCtrlNo = bptmDAO.getBillCtrlNo(inputMap); if (BillCtrlNo ==
	 * null || BillCtrlNo.equals("")) { NumberFormat f = new
	 * DecimalFormat("#00"); billCntrlNum = lStrDDONum + "/" + distCode + "/" +
	 * f.format(gc.get(gc.YEAR)).substring(2) + f.format(gc.get(gc.MONTH) + 1) +
	 * "/" + 1; logger.info(" Bill Control Nm " + billCntrlNum); } else {
	 * billCntrlNum = BillCtrlNo; } String arrBillControlNo[] =
	 * billCntrlNum.split("/"); int billNumber = Integer
	 * .parseInt(arrBillControlNo[arrBillControlNo.length - 1]);
	 * hMapBillControl.put(lStrDDOCode, billNumber); } logger.info("return
	 * billCntrlNum is " + billCntrlNum); return billCntrlNum; }
	 */

	public static synchronized String getBillCntrlNumSeq(Map inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrDDOCode = (String) inputMap.get("DDOCode");
		String lStrFinYrId = SessionHelper.getFinYrId(inputMap).toString();

		BptmCommonServicesDAO bptmDAO = new BptmCommonServicesDAOImpl(TrnBillRegister.class, serv.getSessionFactory());

		Long ctrlNo = Long.valueOf(1); // CONTROL NUMBER

		if (!hMapBillControl.containsKey(lStrDDOCode + "_" + lStrFinYrId)) {
			String lStrBillCtrlNo = bptmDAO.getBillCtrlNoForOBPM(inputMap);
			if (lStrBillCtrlNo != null && !lStrBillCtrlNo.equals("")) {
				ctrlNo = Long.valueOf(lStrBillCtrlNo);
			}
		} else {
			ctrlNo = Long.parseLong(hMapBillControl.get(lStrDDOCode + "_" + lStrFinYrId).toString()) + 1; // CONTROL
			// NUMBER
		}

		hMapBillControl.put(lStrDDOCode + "_" + lStrFinYrId, ctrlNo);
		return ctrlNo.toString();
	}

	/**
	 * Generates Bill Control Number for OBPM when Bill is forwarded from DDO to
	 * Treasury Office
	 * 
	 * @param Map
	 *            inputMap
	 * @return billCntrlNum String
	 */
	public static String generateBillCntrlNoForOBPM(Map inputMap) {

		String billCntrlNum = "";

		try {
			String lStrDDONum = "";

			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
			String lStrDDOCode = (String) inputMap.get("DDOCode");
			String lStrBillCntrlNo = (String) inputMap.get("BillCntrlNumber");
			Long lLngDistrictID = (Long) inputMap.get("DistrictID");
			Long langId = SessionHelper.getLangId(inputMap);
			Long dbId = SessionHelper.getDbId(inputMap);

			String distCode = "";

			if (lStrDDOCode != null && lStrBillCntrlNo != null) {
				List orgDDOMstList = new DDOInfoServiceImpl().getDDOInfo(lStrDDOCode, langId, dbId, serv);

				if (orgDDOMstList != null && orgDDOMstList.size() > 0) {
					OrgDdoMst orgDDOMst = (OrgDdoMst) orgDDOMstList.get(0);
					lStrDDONum = orgDDOMst.getDdoNo().toString();
				}

				// String lStrSeltdTrsry =
				// StringUtility.getParameter("seltdTrsry", request);

				if (lLngDistrictID != null) {
					CmnDistrictMstDAO lObjCmnDistrictMstDAO = new CmnDistrictMstDAOImpl(CmnDistrictMst.class, serv.getSessionFactory());
					CmnDistrictMst lObjCmnDistrictMst = lObjCmnDistrictMstDAO.read(lLngDistrictID);
					if (lObjCmnDistrictMst != null) {
						distCode = lObjCmnDistrictMst.getDistrictCode();
					}
				}

				GregorianCalendar gc = new GregorianCalendar();
				gc.setTime(SessionHelper.getCurDate());

				NumberFormat f = new DecimalFormat("#00");

				billCntrlNum = lStrDDONum + "/" + distCode + "/" + f.format(gc.get(gc.YEAR)).substring(2) + f.format(gc.get(gc.MONTH) + 1) + "/" + lStrBillCntrlNo;
			}
		} catch (Exception e) {
			logger.error("Error is " + e);
		}

		return billCntrlNum;
	}

	/**
	 * Returns Gross Amount for a particular Bill
	 * 
	 * @param Map
	 *            inputMap
	 * @return objRes ResultObject
	 */
	public ResultObject getTotalGrossAmount(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
			BptmCommonServicesDAO lBptmObj = new BptmCommonServicesDAOImpl(BptmCommonServiceImpl.class, serv.getSessionFactory());

			BigDecimal lBDDDOExp = lBptmObj.getTotalGrossAmount(inputMap);

			inputMap.put("TotalDDOExp", lBDDDOExp);

			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			e.printStackTrace();
			logger.error("Error in getTotalGrossAmount and Error is : " + e, e);
		}

		return objRes;
	}

	/*
	 * public static String getLookupIdFromName(String lookupName, Map inputMap)
	 * { ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
	 * String lStrLookupId = ""; try { ServiceLocator serv = (ServiceLocator)
	 * inputMap .get("serviceLocator"); HttpServletRequest request =
	 * (HttpServletRequest) inputMap .get("requestObj"); Long langId =
	 * SessionHelper.getLangId(inputMap); BptmCommonServicesDAO lBptmObj = new
	 * BptmCommonServicesDAOImpl( TrnBillRegister.class,
	 * serv.getSessionFactory()); lStrLookupId =
	 * lBptmObj.getLookupIdFromName(lookupName, langId); } catch (Exception e) {
	 * objRes.setResultValue(null); objRes.setThrowable(e);
	 * objRes.setResultCode(ErrorConstants.ERROR);
	 * objRes.setViewName("errorPage"); logger.error("Look up Id error" + e);
	 * e.printStackTrace(); } return lStrLookupId; }
	 */

	public static boolean isPhyBill(Long billNo, Map inputMap) {

		boolean lPhyFlag = false;
		try {
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
			BptmCommonServicesDAO lBptmObj = new BptmCommonServicesDAOImpl(TrnBillRegister.class, serv.getSessionFactory());
			lPhyFlag = lBptmObj.isPhyBill(billNo);
		} catch (Exception e) {
			logger.error("physical bill or not " + e);
			e.printStackTrace();
		}
		return lPhyFlag;
	}

	public static String getShortBudType(String lStrPLNPL) {

		String lStrGrantBudType = null;
		// if (lStrPLNPL.equals("State_Nonplan") ||
		// lStrPLNPL.equals("Other_Agencies") || lStrPLNPL.equals("PlanBud_80"))
		if (lStrPLNPL.equals("1") || lStrPLNPL.equals("5")) {
			lStrGrantBudType = "NP";
		} else if (lStrPLNPL.equals("6") || lStrPLNPL.equals("7") || lStrPLNPL.equals("8") || lStrPLNPL.equals("9")) {
			lStrGrantBudType = "PL";
		} else if (lStrPLNPL.equals("2") || lStrPLNPL.equals("3") || lStrPLNPL.equals("4")) {
			lStrGrantBudType = "CSS";
		}
		return lStrGrantBudType;
	}

	public static String getReplacedSearchParameter(String str) {

		if (str.equals("p.billCntrlNo")) {
			return "p.bill_Cntrl_No";
		} else if (str.equals("p.tokenNum")) {
			return "p.token_Num";
		} else if (str.equals("mb.subjectDesc")) {
			return "mb.subject_Desc";
		} else if (str.equals("p.inwardDt")) {
			return "p.bill_Date";
		} else if (str.equals("odm.ddoNo")) {
			return "odm.ddo_No";
		} else if (str.equals("odm.ddoName")) {
			return "odm.ddo_Name";
		} else if (str.equals("auditorsName")) {
			return "auditorsName";
		} else if (str.equals("p.budmjrHd")) {
			return "p.budmjr_Hd";
		} else if (str.equals("clm.lookupName")) {
			return "p.tc_bill";
		}

		return "";

	}

	/**
	 * Gets the list of Users at all the levels through which document has
	 * passed
	 * 
	 * @param lMapInput
	 *            : Input Framework Map
	 * @return resultObject : Result Object
	 */
	public ResultObject sendToOther(Map lMapInput) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			ServiceLocator serv = (ServiceLocator) lMapInput.get("serviceLocator");
			BptmCommonServicesDAO lBptmObj = new BptmCommonServicesDAOImpl(BptmCommonServiceImpl.class, serv.getSessionFactory());
			HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");

			String lStrBillNo = request.getParameter("billNo");
			String lStrHierRefId = request.getParameter("hierRefId");
			String lStrStatusFlag = request.getParameter("statusFlag");

			Short lShtLevelId = null;
			Long lLngHierRefId = null;
			Long lLngBillNo = null;
			List lLstOtherUsers = null;

			lLngHierRefId = lStrHierRefId != null && lStrHierRefId.trim().length() > 0 ? Long.parseLong(lStrHierRefId) : null;
			lLngBillNo = lStrBillNo != null && lStrBillNo.trim().length() > 0 ? Long.parseLong(lStrBillNo) : null;

			if (lStrStatusFlag != null && lStrStatusFlag.trim().length() > 0) {
				lShtLevelId = getCurrLevelFrmStatus(serv, lStrStatusFlag, "BILL");
			}

			if (lLngHierRefId != null && lLngBillNo != null && lShtLevelId != null) {
				String auditFlg = null;
				if (request.getParameter("auditFlg") != null && request.getParameter("auditFlg").equals("yes")) {
					auditFlg = "yes";
				}
				lLstOtherUsers = lBptmObj.getOtherUserList(lLngHierRefId, lLngBillNo, Long.parseLong(lShtLevelId.toString()), SessionHelper.getLangId(lMapInput), auditFlg);
			}

			lMapInput.put("OtherUserList", lLstOtherUsers);

			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(lMapInput);
			objRes.setViewName("sendToOther");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(logger, objRes, e, "Error is ");
		}

		return objRes;
	}

	public static Short getCurrLevelFrmStatus(ServiceLocator serv, String lStrCurrStatus, String category) throws Exception {

		Short lShtLevelId = null;
		List lObjLst = null;

		try {
			BptmCommonServicesDAO lBptmObj = new BptmCommonServicesDAOImpl(BptmCommonServiceImpl.class, serv.getSessionFactory());

			lObjLst = lBptmObj.getCurrLevelFrmStatus(lStrCurrStatus, category);

			if (lObjLst != null && !lObjLst.isEmpty()) {
				lShtLevelId = (Short) lObjLst.get(0);
			}
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw e;
		}

		return lShtLevelId;
	}
}